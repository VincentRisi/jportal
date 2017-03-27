/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;
using System.Data;
using System.IO;
using System.Reflection;
using System.Text;
using bbd.utility;

namespace bbd.utility.rpc
{
  /// <summary>
  /// $Revision: 401.4 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public class Reader
  {
    protected RpcSocket socket;
    protected Header header;
    protected Popper popper;
    protected MemoryStream mS;
    protected BinaryReader bR;
    protected double readDuration;
    public double ReadDuration {get {return readDuration;}}
    public Reader(RpcSocket socket, Header header, Popper popper)
    {
      this.socket = socket;
      this.header = header;
      this.popper = popper;
      mS = new MemoryStream();
      bR = new BinaryReader(mS);
    }
    public long Position
    {
      get {return mS.Position;}
      set {mS.Seek(value, SeekOrigin.Begin);}
    }
    public void AlignTo(long no)
    {
      long mod = (mS.Position) % no;
      if (mod != 0)
        mS.Seek(no-mod, SeekOrigin.Current);
    }
    private byte ReadByte()
    {
      return bR.ReadByte();
    }
    private Int16 ReadInt16()
    {
      return (Int16)
        ( bR.ReadByte() << 8         
        | bR.ReadByte());
    }
    private Int32 ReadInt32()
    {
      return 
        ( bR.ReadByte() << 24 
        | bR.ReadByte() << 16 
        | bR.ReadByte() << 8 
        | bR.ReadByte());
    }
    private Int64 ReadInt64()
    {
      ulong data 
        = (ulong)(bR.ReadByte()) << 56
        | (ulong)(bR.ReadByte()) << 48
        | (ulong)(bR.ReadByte()) << 40
        | (ulong)(bR.ReadByte()) << 32
        | (ulong)(bR.ReadByte()) << 24
        | (ulong)(bR.ReadByte()) << 16
        | (ulong)(bR.ReadByte()) << 8
        | (ulong)(bR.ReadByte())
        ;
      return (Int64)data;
    }
    private double ReadDouble()
    {
      byte[] db = new byte[8];
      for (int i=7; i>=0; i--)
        db[i] = bR.ReadByte();
      MemoryStream m = new MemoryStream(db);
      BinaryReader b = new BinaryReader(m);
      return b.ReadDouble();
    }
    private string ReadString(int length)
    {
      string result;
      if (length > 0)
      {
        byte[] chars = new byte[length];
        chars = TrimTrailingZero(bR.ReadBytes(length));
        result = Encoding.Default.GetString(chars);
      }    
      else
        result = bR.ReadString();
      return result.TrimEnd(null);
    }
    private DateTime ReadDateTime(int length)
    {
      string datestring = ReadString(length);
      if (string.IsNullOrEmpty(datestring))
        return DateTime.MinValue;
      if (datestring.Length == 6 && datestring != "000000")
        return DateTime.ParseExact(datestring, "HHmmss", null);
      if (datestring.Length == 8 && datestring != "00000000")
        return DateTime.ParseExact(datestring, "yyyyMMdd", null);
      if (datestring.Length == 14 && datestring != "00000000000000")
        return DateTime.ParseExact(datestring, "yyyyMMddHHmmss", null);
      return DateTime.MinValue;
    }
    private decimal ReadDecimal(int length)
    {
      string s = ReadString(length);
      if (!string.IsNullOrEmpty(s))
        return Decimal.Parse(s);
      else
        return 0.0M;
    }
    private JPBlob ReadJPBlob()
    {
      int length = ReadInt32();
      JPBlob result = new JPBlob();
      result.Buffer = ReadBytes(length);
      return result;
    }
    private byte[] ReadBytes(int length)
    {
      return bR.ReadBytes(length);
    }
    private int GetLength(FieldInfo info)
    {
      int length = 0;
      if (info != null)
      {
        object[] attributes = info.GetCustomAttributes(false);
        for (int i = 0; i < attributes.Length; i++)
        {
          FieldAttribute fa = attributes[i] as FieldAttribute;
          if (fa != null)
            length = fa.Size;
        }
      }
      return length;
    }
    private object GetData(FieldInfo info, Type type)
    {
      if (type == typeof(String))
        return (string) ReadString(GetLength(info));
      if (type == typeof(DateTime))
        return (DateTime)ReadDateTime(GetLength(info));
      if (type == typeof(Byte[]))
        return (byte[]) ReadBytes(GetLength(info));
      if (type == typeof(Boolean))
        return (bool)(ReadInt16() == -1);
      if (type == typeof(SByte))
        return (sbyte) ReadByte();
      if (type == typeof(Byte))
        return (byte) ReadByte();
      if (type == typeof(Int16))
        return (Int16) ReadInt16();
      if (type == typeof(UInt16))
        return (UInt16) ReadInt16();
      if (type == typeof(Int32))
        return (Int32) ReadInt32();
      if (type == typeof(UInt32))
        return (UInt32) ReadInt32();
      if (type == typeof(Int64))
        return (Int64)ReadInt64();
      if (type == typeof(UInt64))
        return (UInt64)ReadInt64();
      if (type == typeof(Double))
        return (double) ReadDouble();
      if (type == typeof(Decimal))
        return (decimal)ReadDecimal(GetLength(info));
      if (type == typeof(JPBlob))
        return (JPBlob)ReadJPBlob();
      return null;
    }
    public string GetString(int size, bool align8)
    {
      if (align8 == true)
        AlignTo(8);
      int length = new int();
      Get(length);
      if (align8 == true)
        AlignTo(8);
      return ReadString(size);
    }
    public object Get(object of, bool align8)
    {
      if (align8 == true)
        AlignTo(8);
      Type type = of.GetType();
      if (type.IsArray == true)
      {
        Array arr = (Array)of;
        if (align8 == true)
          AlignTo(8);
        //int length = new int();
        //Get(length);
        foreach (object x in arr)
          Get(x, align8);
      }
      else if (type.IsPrimitive)
      {
        of = GetData(null, type);
      }
      else if (type.IsClass)
      {
        if (mS.Length == 0 || mS.Length - mS.Position == 0)
          return null;
        FieldInfo[] fieldInfo = popper.GetFieldList(type);
        for(int i = 0; i < fieldInfo.Length; i++)
        {
          Type oType = fieldInfo[i].FieldType;
          string ofType = oType.ToString();
          if (oType == typeof(JPBlob))
          {
            object value = GetData(fieldInfo[i], oType);
            fieldInfo[i].SetValue(of, value);
          }
          else if (ofType != "System.String"
            &&  ofType != "System.Byte[]"
            && oType.IsClass)
          {
            object o = fieldInfo[i].GetValue(of);
            if (o == null)
            {
              Assembly a = Assembly.GetAssembly(oType);
              o = a.CreateInstance(oType.FullName);
            }
            Get(o, align8);
          }
          else
          {
            object value = GetData(fieldInfo[i], oType);
            fieldInfo[i].SetValue(of, value);
          }
        }
      }
      return of;
    }
    public object Get(object of)
    {
      return Get(of, false);
    }
    public virtual byte[] Receive(out int length, out int actualLength)
    {
      byte[] result = null;
      try
      {
        int headerLength=0;
        int dataLength=0;
        byte[] read1 = socket.Read(out headerLength);
        readDuration = socket.ReadDuration;
        byte[] read2 = new byte[0];
        popper.OnAfterUncompress(read1, headerLength);
        BinaryWriter bW = new BinaryWriter(mS);
        bW.Write(read1, 0, headerLength);
        Position = 0;
        header = (Header) Get(header);
        length = actualLength = headerLength+4;
        if (header.MSize() > 0)
        {
          read2 = new byte[0];
          read2 = socket.Read(out dataLength);
          readDuration += socket.ReadDuration;
          popper.OnBeforeUncompress(read2, dataLength);
          actualLength += header.MSize()+4;
          length += dataLength+4;
          Position = 0;
          if (header.MSize() > dataLength)
          {
            byte[] zBuff = new byte[header.MSize()];
            int zLen = Rdc.Decompress(read2, dataLength, zBuff);
            popper.OnAfterUncompress(zBuff, zLen);
            bW.Write(zBuff, 0, zLen);
            result = zBuff;
          }
          else
          {
            bW.Write(read2, 0, dataLength);
            result = read2;
          }
          Position = 0;
        }
        if (header.ESize() > 0)
        {
          read2 = new byte[0];
          read2 = socket.Read(out dataLength);
          readDuration += socket.ReadDuration;
          popper.OnBeforeUncompress(read2, dataLength);
          actualLength += header.ESize()+4;
          length += dataLength+4;
          if (header.ESize() > dataLength)
          {
            byte[] zBuff = new byte[header.ESize()];
            int zLen = Rdc.Decompress(read2, dataLength, zBuff);
            popper.OnAfterUncompress(zBuff, zLen);
            throw new RpcException(header.ReturnCode(), Encoding.Default.GetString(zBuff, 0, zLen));
          }
          else
            throw new RpcException(header.ReturnCode(), Encoding.Default.GetString(read2, 0, dataLength));
        }
        if (header.ReturnCode() != 0)
          throw new RpcException(header.ReturnCode());
      }
      catch (RpcException)
      {
        throw; // bounce this catch up
      }
      catch (Exception ex)
      {
        throw new RpcException("Error Reading from Server", ex);
      }
      return result;
    }
    private static byte[] TrimTrailingZero(byte[] source)
    {
      int fullLength = source.Length;
      while ((fullLength != 0) && (source[fullLength-1] == 0))
        fullLength--;
      if (fullLength != source.Length)
      {
        byte []stringChars= new byte[fullLength];
        System.Array.Copy(source, 0, stringChars, 0, stringChars.Length);
        return stringChars;
      }
      else
        return source;
    }
  }
}

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

namespace bbd.idl2.rpc
{
  /// <summary>
  /// $Revision: 401.3 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public class Writer
  {
    protected RpcSocket socket;
    protected Header header;
    protected Popper popper;
    protected MemoryStream mS;
    protected BinaryWriter bW;
    protected long dataLengthPosition;
    protected long dataPosition;
    protected long headerLengthPosition;
    protected long headerPosition;
    protected int headerLength;
    protected int dataLength;
    protected double writeDuration;
    public double WriteDuration {get {return writeDuration;}}
    public Writer(RpcSocket socket, Header header, Popper popper)
    {
      this.socket = socket;
      this.header = header;
      this.popper = popper;
      mS = new MemoryStream();
      bW = new BinaryWriter(mS);
    }
    public long Position
    {
      get {return mS.Position;}
      set {mS.Seek(value, SeekOrigin.Begin);}
    }
    public void PadTo(long no)
    {
      while (((mS.Position-dataPosition) % no) != 0) 
        bW.Write((byte)0x00);
    }
    private void Write(byte b)
    {
      bW.Write(b);
    }
    private void Write(UInt16 u16)
    {
      bW.Write((byte)((u16 & 0xFF00)>>8));
      bW.Write((byte)(u16 & 0x00FF));
    }
    private void Write(uint u32)
    {
      bW.Write((byte)((u32 & 0xFF000000)>>24));
      uint test = (u32 & 0xFF000000)>>24;
      bW.Write((byte)((u32 & 0x00FF0000)>>16));
      test = (u32 & 0x00FF0000)>>16;
      bW.Write((byte)((u32 & 0x0000FF00)>>8));
      test=(u32 & 0x0000FF00)>>8;
      bW.Write((byte)(u32 & 0x000000FF));
      test=u32 & 0x000000FF;
    }
    private void Write(ulong u64)
    {
      bW.Write((byte)((u64 & 0xFF00000000000000) >> 56));
      ulong t8 = (u64 & 0xFF00000000000000) >> 56;
      bW.Write((byte)((u64 & 0x00FF000000000000) >> 48));
      ulong t7 = (u64 & 0x00FF000000000000) >> 48;
      bW.Write((byte)((u64 & 0x0000FF0000000000) >> 40));
      ulong t6 = (u64 & 0x0000FF0000000000) >> 40;
      bW.Write((byte)((u64 & 0x000000FF00000000) >> 32));
      ulong t5 = (u64 & 0x000000FF00000000) >> 32;
      bW.Write((byte)((u64 & 0x00000000FF000000) >> 24));
      ulong t4 = (u64 & 0x00000000FF000000) >> 24;
      bW.Write((byte)((u64 & 0x0000000000FF0000) >> 16));
      ulong t3 = (u64 & 0x0000000000FF0000) >> 16;
      bW.Write((byte)((u64 & 0x000000000000FF00) >> 8));
      ulong t2 = (u64 & 0x000000000000FF00) >> 8;
      bW.Write((byte)((u64 & 0x00000000000000FF)));
      ulong t1 = (u64 & 0x00000000000000FF);
    }
    private void Write(double d)
    {
      MemoryStream m = new MemoryStream(8);
      BinaryWriter b = new BinaryWriter(m);
      b.Write(d);
      byte[] db = m.GetBuffer();
      for (int i=7; i>=0; i--)
        bW.Write(db[i]);
    }
    private void Write(string s, FieldInfo info)
    {
      if (s == null) 
        s = "";
      int buffSize = s.Length;
      if (info != null)
      {
        object[] attributes = info.GetCustomAttributes(false);
        for (int i = 0; i < attributes.Length; i++)
        {
          FieldAttribute fa = attributes[i] as FieldAttribute;
          if (fa != null)
            buffSize = fa.Size;
        }
      }
      byte[] chars = new byte[buffSize];
      byte[] ss = Encoding.Default.GetBytes(s);
      for (int i=0; i<ss.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i=0; i<chars.Length; i++)
        bW.Write(chars[i]);
    }
    private void Write(DateTime s, FieldInfo info)
    {
      int buffSize = 9; // Default to yyyymmdd
      string datestring = "";
      if (info != null)
      {
        object[] attributes = info.GetCustomAttributes(false);
        for (int i = 0; i < attributes.Length; i++)
        {
          FieldAttribute fa = attributes[i] as FieldAttribute;
          if (fa != null)
            buffSize = fa.Size;
        }
      }
      if (s == null || s == DateTime.MinValue)
        datestring = "";
      else if (buffSize == 7)
        datestring = s.ToString("HHmmss");
      else if (buffSize == 9)
        datestring = s.ToString("yyyyMMdd");
      else
        datestring = s.ToString("yyyyMMddHHmmss");
      byte[] chars = new byte[buffSize];
      byte[] ss = Encoding.Default.GetBytes(datestring);
      for (int i = 0; i < datestring.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i = 0; i < chars.Length; i++)
        bW.Write(chars[i]);
    }
    private void Write(decimal d, FieldInfo info)
    {
      string number = d.ToString();
      int buffSize = number.Length;
      if (info != null)
      {
        object[] attributes = info.GetCustomAttributes(false);
        for (int i = 0; i < attributes.Length; i++)
        {
          FieldAttribute fa = attributes[i] as FieldAttribute;
          if (fa != null)
            buffSize = fa.Size;
        }
      }
      byte[] chars = new byte[buffSize];
      byte[] ss = Encoding.Default.GetBytes(number);
      for (int i = 0; i < number.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i = 0; i < chars.Length; i++)
        bW.Write(chars[i]);
    }
    private void Write(byte[] b)
    {
      for (int i=0; i<b.Length; i++)
        bW.Write(b[i]);
    }
    private void Write(JPBlob blob)
    {
      Write((uint)blob.Buffer.Length);
      Write(blob.Buffer);
    }
    public void AddData(object of)
    {
      Type type = of.GetType();
      if (type == typeof(SByte))
        Write((byte)(of));
      else if (type == typeof(Byte))
        Write((byte)(of));
      else if (type == typeof(Boolean))
      {
        Int16 x = (Int16)((bool)(of) ? -1 : 0);
        Write((UInt16)x);
      }
      else if (type == typeof(Int16))
        Write((UInt16)(Int16)(of));
      else if (type == typeof(UInt16))
        Write((UInt16)(of));
      else if (type == typeof(Int32))
        Write((uint)(int)(of));
      else if (type == typeof(UInt32))
        Write((uint)(of));
      else if (type == typeof(Int64))
        Write((ulong)(long)(of));
      else if (type == typeof(UInt64))
        Write((ulong)(of));
      else if (type == typeof(Double))
        Write((double)(of));
      else if (type == typeof(String))
        Write((string)(of), null);
      else if (type == typeof(Byte[]))
        Write((byte[])(of));
      else if (type == typeof(DateTime))
        Write((DateTime)(of), null);
      else if (type == typeof(Decimal))
        Write((Decimal)(of), null);
      else if (type == typeof(JPBlob))
        Write((JPBlob)(of));
    }
    public void AddData(FieldInfo fieldInfo, object of, bool align8)
    {
      Type type = fieldInfo.FieldType;
      if (type == typeof(SByte))
        Write((byte)(sbyte)(fieldInfo.GetValue(of)));
      else if (type == typeof(Byte))
        Write((byte)(fieldInfo.GetValue(of)));
      else if (type == typeof(Boolean))
      {
        Int16 x = (Int16)((bool)(fieldInfo.GetValue(of)) ? -1 : 0);
        Write((UInt16)x);
      }
      else if (type == typeof(Int16))
        Write((UInt16)(Int16)(fieldInfo.GetValue(of)));
      else if (type == typeof(UInt16))
        Write((UInt16)(fieldInfo.GetValue(of)));
      else if (type == typeof(Int32))
        Write((uint)(int)(fieldInfo.GetValue(of)));
      else if (type == typeof(UInt32))
        Write((uint)(fieldInfo.GetValue(of)));
      else if (type == typeof(Int64))
        Write((ulong)(long)(fieldInfo.GetValue(of)));
      else if (type == typeof(UInt64))
        Write((ulong)(fieldInfo.GetValue(of)));
      else if (type == typeof(Double))
        Write((double)(fieldInfo.GetValue(of)));
      else if (type == typeof(String))
        Write((string)fieldInfo.GetValue((of)), fieldInfo);
      else if (type == typeof(DateTime))
        Write((DateTime)fieldInfo.GetValue((of)), fieldInfo);
      else if (type == typeof(Decimal))
        Write((Decimal)fieldInfo.GetValue((of)), fieldInfo);
      else if (type == typeof(JPBlob))
        Write((JPBlob)fieldInfo.GetValue((of)));
      else if (type == typeof(Byte[]))
        Write((byte[])fieldInfo.GetValue((of)));
      else if (type.IsClass)
        Add(fieldInfo.GetValue(of), align8);
    }
    public void Add(object of, bool align8)
    {
      if (align8 == true)
        PadTo(8);
      Type type = of.GetType();
      if (type.IsArray == true)
      {
        Array arr = (Array)of;
        long lengthPoint = Position;
        int length = 0;
        Add(length);
        if (align8 == true)
          PadTo(8);
        long arrayStart = Position;
        foreach (object x in arr)
          Add(x);
        long currPosition = Position;
        length = (int) (currPosition - arrayStart);
        Position = lengthPoint;
        Add(length);
        Position = currPosition;
      }
      else if (type.IsPrimitive)
      {
        AddData(of);
      }
      else if (type == typeof(System.String))
      {
        string s = (of as string)+"\x0";
        int length = s.Length;
        Add(length);
        if (align8 == true)
          PadTo(8);
        AddData(s);
      }
      else if (type.IsClass)
      {
        FieldInfo[] fieldInfo = popper.GetFieldList(type);
        for(int i = 0; i < fieldInfo.Length; i++)
          AddData(fieldInfo[i], of, align8);
      }
    }
    public void Add(object of)
    {
      Add(of, false);
    }
    public void SetupHeader()
    {
      headerLengthPosition = 0;
      headerPosition = 4;
      Position = headerPosition;
      Add(header, false);
      headerLength = (int)(Position - headerPosition); 
      dataLengthPosition = Position;
      Position = Position + 4;
      dataPosition = Position;
      Position = headerLengthPosition;
      Add(headerLength, false);
      Position = dataPosition;
    }
    public virtual byte[] Send(out int length, out int actualLength)
    {
      try
      {
        PadTo(4);
        dataLength = (int)(Position - dataPosition); 
        header.MSize(dataLength);
        mS.Flush();
        Position = headerPosition;
        Add(header, false);
        byte[] mBuffer = mS.GetBuffer();
        byte[] iBuffer = new byte[dataLength];
        for (int i=0; i<dataLength; i++)
          iBuffer[i] = mBuffer[i+(int)dataPosition];
        header.SignMessage(mBuffer, headerLength, iBuffer, socket.HostIP);
        popper.OnBeforeCompress(mBuffer, headerLength);
        byte[] zBuffer = new byte[dataLength];
        actualLength = headerLength+dataLength+8;
        popper.OnBeforeCompress(iBuffer, dataLength);
        int compLength = Rdc.Compress(iBuffer, dataLength, zBuffer);
        Position = dataLengthPosition;
        if (compLength > 0)
        {
          dataLength = compLength;
          Add(dataLength, false);
          bW.Write(zBuffer, 0, dataLength);
          popper.OnAfterCompress(zBuffer, dataLength);
          mS.Flush();
        }
        else
          Add(dataLength, false);
        length = headerLength+dataLength+8;
        socket.Write(mBuffer, length);
        writeDuration = socket.WriteDuration;
        return mBuffer;
      }
      catch (Exception ex)
      {
        throw new RpcException("Error Sending buffer to Server", ex);
      }
    }
  }
}

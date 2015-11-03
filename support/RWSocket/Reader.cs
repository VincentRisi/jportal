using System;
using System.Data;
using System.IO;
using System.Text;

namespace bbd.idl2.rw
{
  public class Reader
  {
    protected MemoryStream mS;
    protected BinaryReader bR;
    protected RpcSocket socket;
    protected Header header;
    protected Handler handler;
    public Reader(RpcSocket socket = null, Header header = null, Handler handler = null)
    {
      this.socket = socket;
      this.header = header;
      this.handler = handler;
      mS = new MemoryStream();
      bR = new BinaryReader(mS);
    }
    public long Position
    {
      get {return mS.Position;}
    }
    public void skip(int no)
    {
      mS.Seek((long)no, SeekOrigin.Current);
    }
    public byte getByte()
    {
      return bR.ReadByte();
    }
    public short getShort()
    {
      return (short)
        ( bR.ReadByte() << 8         
        | bR.ReadByte());
    }
    public int getInt()
    {
      return 
        ( bR.ReadByte() << 24 
        | bR.ReadByte() << 16 
        | bR.ReadByte() << 8 
        | bR.ReadByte());
    }
    public long getlong()
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
      return (long)data;
    }
    public double getDouble()
    {
      byte[] db = new byte[8];
      for (int i = 7; i >= 0; i--)
        db[i] = bR.ReadByte();
      MemoryStream m = new MemoryStream(db);
      BinaryReader b = new BinaryReader(m);
      return b.ReadDouble();
    }
    public string getString(int length)
    {
      string result = "";
      if (length > 0)
      {
        byte[] chars = new byte[length];
        chars = bR.ReadBytes(length);
        result = Encoding.Default.GetString(chars);
      }    
      return result.TrimEnd(null);
    }
    public DateTime getDateTime(int length)
    {
      string datestring = getString(length);
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
    public decimal getDecimal(int length)
    {
      string s = getString(length);
      if (!string.IsNullOrEmpty(s))
        return Decimal.Parse(s);
      else
        return 0.0M;
    }
    public JPBlob getJPBlob()
    {
      int length = getInt();
      JPBlob result = new JPBlob();
      result.Buffer = getBytes(length);
      return result;
    }
    public byte[] getBytes(int length)
    {
      return bR.ReadBytes(length);
    }
    public byte[] Receive(out int receivedLength, out int actualReceivedLength)
    {
      actualReceivedLength = 0;
      receivedLength = 0;
      return mS.GetBuffer();
    }
  }
}

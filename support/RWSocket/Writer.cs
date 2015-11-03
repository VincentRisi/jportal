using System;
using System.Data;
using System.IO;
using System.Reflection;
using System.Text;

namespace bbd.idl2.rw
{
  public class Writer
  {
    protected MemoryStream mS;
    protected BinaryWriter bW;
    protected RpcSocket socket;
    protected Header header;
    protected Handler handler;
    public int Position { get { return (int)mS.Position; } }
    public byte[] Buffer { get { return mS.GetBuffer(); } }
    public void storeInt(int at, int value)
    {
      long keep = mS.Position;
      mS.Position = (long)at;
      putInt(value);
    }
    public Writer(RpcSocket socket=null, Header header=null, Handler handler=null)
    {
      this.socket = socket;
      this.header = header;
      this.handler = handler;
      mS = new MemoryStream();
      bW = new BinaryWriter(mS);
    }
    public void filler(int no)
    {
      for (int i = 0; i < no; i++)
        bW.Write((byte)0x00);
    }
    public void putByte(byte i8)
    {
      bW.Write(i8);
    }
    public void putShort(short i16)
    {
      UInt16 u16 = (UInt16) i16;
      bW.Write((byte)((u16 & 0xFF00) >> 8));
      bW.Write((byte)((u16 & 0x00FF)));
    }
    public void putInt(int i32)
    {
      UInt32 u32 = (UInt32) i32;
      putInt(u32);
    }
    public void putInt(UInt32 u32)
    {
      bW.Write((byte)((u32 & 0xFF000000) >> 24));
      bW.Write((byte)((u32 & 0x00FF0000) >> 16));
      bW.Write((byte)((u32 & 0x0000FF00) >> 8));
      bW.Write((byte)((u32 & 0x000000FF)));
    }
    public void putLong(long i64)
    {
      UInt64 u64 = (UInt64) i64;
      bW.Write((byte)((u64 & 0xFF00000000000000) >> 56));
      bW.Write((byte)((u64 & 0x00FF000000000000) >> 48));
      bW.Write((byte)((u64 & 0x0000FF0000000000) >> 40));
      bW.Write((byte)((u64 & 0x000000FF00000000) >> 32));
      bW.Write((byte)((u64 & 0x00000000FF000000) >> 24));
      bW.Write((byte)((u64 & 0x0000000000FF0000) >> 16));
      bW.Write((byte)((u64 & 0x000000000000FF00) >> 8));
      bW.Write((byte)((u64 & 0x00000000000000FF)));
    }
    public void putDouble(double d)
    {
      MemoryStream m = new MemoryStream(8);
      BinaryWriter b = new BinaryWriter(m);
      b.Write(d);
      byte[] db = m.GetBuffer();
      for (int i=7; i>=0; i--)
        bW.Write(db[i]);
    }
    public void putString(string s, int length)
    {
      if (s == null) 
        s = "";
      byte[] chars = new byte[length];
      byte[] ss = Encoding.Default.GetBytes(s);
      for (int i=0; i<ss.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i=0; i<chars.Length; i++)
        bW.Write(chars[i]);
    }
    public void putDateTime(DateTime s, int length)
    {
      string datestring = "";
      if (s == null || s == DateTime.MinValue)
        datestring = "";
      else if (length == 7)
        datestring = s.ToString("HHmmss");
      else if (length == 9)
        datestring = s.ToString("yyyyMMdd");
      else
        datestring = s.ToString("yyyyMMddHHmmss");
      byte[] chars = new byte[length];
      byte[] ss = Encoding.Default.GetBytes(datestring);
      for (int i = 0; i < datestring.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i = 0; i < chars.Length; i++)
        bW.Write(chars[i]);
    }
    public void putDecimal(decimal d, int length)
    {
      string number = d.ToString();
      int buffSize = number.Length;
      byte[] chars = new byte[buffSize];
      byte[] ss = Encoding.Default.GetBytes(number);
      for (int i = 0; i < number.Length && i < chars.Length; i++)
        chars[i] = ss[i];
      for (int i = 0; i < chars.Length; i++)
        bW.Write(chars[i]);
    }
    public void putBytes(byte[] b)
    {
      for (int i=0; i<b.Length; i++)
        bW.Write(b[i]);
    }
    public void putJPBlob(JPBlob blob)
    {
      putInt(blob.Buffer.Length);
      putBytes(blob.Buffer);
    }
    public byte[] Send(out int sentLength, out int actualSentLength)
    {
      sentLength = 0;
      actualSentLength = 0;
      return mS.GetBuffer();
    }
  }
}

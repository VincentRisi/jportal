package vlab.crackle.rdc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Reader 
{
  public static interface ByteReader
  {
    public void read(Reader reader) throws Throwable;
  }
  static final short JP_NULL = -1;
  static final short JP_NOT_NULL = 0;
  public static class Bais extends ByteArrayInputStream
  {
    public Bais(byte[] data)
    {
      super(data);
    }
    public void alignTo(int align)
    {
      int n = pos % align;
      if (n != 0) pos += (8 - n);
    }
  }
  private Bais bais;
  private DataInputStream dis; 
  public Reader(byte[] data)
  {
    bais = new Bais(data); 
    dis = new DataInputStream(bais);
  }
  public void skip(int no)
  {
    bais.skip(no);
  }
  public char getChar() throws Throwable
  {
    return (char)dis.readByte();
  }
  public byte getByte() throws Throwable
  {
    return dis.readByte();
  }
  public short getShort() throws Throwable
  {
    return dis.readShort();
  }
  public boolean getBoolean() throws Throwable
  {
    return dis.readShort() == JP_NOT_NULL ? false : true;
  }
  public int getInt() throws Throwable
  {
    return dis.readInt();
  }
  public long getLong() throws Throwable
  {
    return dis.readLong();
  }
  public double getDouble() throws Throwable
  {
    return dis.readDouble();
  }
  public byte[] getBytes(int length) throws Throwable
  {
    byte[] b = new byte[length];
    dis.read(b);
    return b;
  }
  public String getString(int length) throws Throwable
  {
    byte[] b = new byte[length];
    dis.read(b);
    return new String(b,0,b.length).trim();
  }
}

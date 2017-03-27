/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// 
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
package bbd.crackle.rdc;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Reader 
{
  public static interface ByteReader
  {
    public void read(Reader reader) throws Exception;
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
  public char getChar() throws Exception
  {
    return (char)dis.readByte();
  }
  public byte getByte() throws Exception
  {
    return dis.readByte();
  }
  public short getShort() throws Exception
  {
    return dis.readShort();
  }
  public boolean getBoolean() throws Exception
  {
    return dis.readShort() == JP_NOT_NULL ? false : true;
  }
  public int getInt() throws Exception
  {
    return dis.readInt();
  }
  public long getLong() throws Exception
  {
    return dis.readLong();
  }
  public double getDouble() throws Exception
  {
    return dis.readDouble();
  }
  public byte[] getBytes(int length) throws Exception
  {
    byte[] b = new byte[length];
    dis.read(b);
    return b;
  }
  public String getString(int length) throws Exception
  {
    byte[] b = new byte[length];
    dis.read(b);
    return new String(b,0,b.length).trim();
  }
}

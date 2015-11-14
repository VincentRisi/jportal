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
package vlab.crackle.rw;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Reader 
{
  public static class Bais extends ByteArrayInputStream
  {
    public Bais(byte[] data)
    {
      super(data);
    }
    public void skip(int length)
    {
      pos += length;
    }
  }
  private final Bais bais;
  private final DataInputStream dis; 
  public Reader(byte[] data)
  {
    bais = new Bais(data); 
    dis = new DataInputStream(bais);
  }
  public void skip(int length)
  {
    bais.skip(length);
  }
  public byte getByte() throws Throwable
  {
    return dis.readByte();
  }
  public char getChar() throws Throwable
  {
    return (char)dis.readByte();
  }
  public short getShort() throws Throwable
  {
    return dis.readShort();
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
  public String getString(int length) throws Throwable
  {
    byte[] b = new byte[length];
    dis.read(b);
    return new String(b,0,b.length-1);
  }
}

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
package bbd.crackle.rw;

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
  public byte getByte() throws Exception
  {
    return dis.readByte();
  }
  public char getChar() throws Exception
  {
    return (char)dis.readByte();
  }
  public short getShort() throws Exception
  {
    return dis.readShort();
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
  public String getString(int length) throws Exception
  {
    byte[] b = new byte[length];
    dis.read(b);
    return new String(b,0,b.length-1);
  }
}

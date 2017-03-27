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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Writer
{
  public static class Baos extends ByteArrayOutputStream
  {
    public void storeLength(int pos, int length)
    {
      buf[pos] =     (byte) ((length & 0xFF000000) >>> 24);
      buf[pos + 1] = (byte) ((length & 0xFF0000) >>> 16);
      buf[pos + 2] = (byte) ((length & 0xFF00) >>> 8);
      buf[pos + 3] = (byte) ((length & 0xFF));
    }
  }
  private final Baos baos;
  private final DataOutputStream dos;
  public Writer()
  {
    baos = new Baos();
    dos = new DataOutputStream(baos);
  }
  public byte[] close() throws Exception
  {
    baos.flush();
    baos.close();
    byte[] result = baos.toByteArray();
    dos.close();
    return result;
  }
  public void filler(int no)  throws IOException
  {
    for (int i = 0; i < no; i++)
      dos.writeByte(0);
  }
  public void putByte(byte i8) throws IOException
  {
    dos.writeByte(i8);
  }
  public void putChar(char ch) throws IOException
  {
    String x = "" + ch;
    byte[] utf8 = x.getBytes("UTF8");
    putBytes(utf8);
  }
  public void putBytes(byte[] bd) throws IOException
  {
    dos.write(bd);
  }
  public void putDouble(double d8) throws IOException
  {
    dos.writeDouble(d8);
  }
  public void putInt(int i32) throws IOException
  {
    dos.writeInt(i32);
  }
  public void putLong(long i64) throws IOException
  {
    dos.writeLong(i64);
  }
  public void putShort(short i16) throws IOException
  {
    dos.writeShort(i16);
  }
  public void putString(String string, int length) throws IOException
  {
    dos.writeBytes(string);
  }
}

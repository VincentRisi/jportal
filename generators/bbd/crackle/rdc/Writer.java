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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Writer
{
  public static interface ByteWriter
  {
    public void write(Writer writer) throws Exception;
  }
  static final short JP_NULL = -1;
  static final short JP_NOT_NULL = 0;
  public static class Baos extends ByteArrayOutputStream
  {
    public void storeLength(int pos, int length)
    {
      buf[pos] = (byte) ((length & 0xFF000000) >>> 24);
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
  public void init()
  {
    baos.reset();
  }
  public byte[] close() throws Exception
  {
    baos.flush();
    baos.close();
    byte[] result = baos.toByteArray();
    dos.close();
    return result;
  }
  private int mSizeOffset;
  private int dataSizeOffset;
  public int size()
  {
    return baos.size();
  }
  public void storeLength(int pos, int length)
  {
    baos.storeLength(pos, length);
  }
  public void filler(int no)
  {
    for (int i = 0; i < no; i++)
      baos.write(0);
  }
  public void putChar(char ch) throws Exception
  {
    dos.write(ch);
  }
  public void putByte(byte i8) throws Exception
  {
    dos.writeShort((short)i8);
  }
  public void putBytes(byte[] bd) throws Exception
  {
    dos.write(bd);
  }
  public void putDouble(double d8) throws Exception
  {
    dos.writeDouble(d8);
  }
  public void putInt(int i32) throws Exception
  {
    dos.writeInt(i32);
  }
  public void putLong(long i64) throws Exception
  {
    dos.writeLong(i64);
  }
  public void putShort(short i16) throws Exception
  {
    dos.writeShort(i16);
  }
  public void putBoolean(boolean b) throws Exception
  {
    dos.writeShort(b == true ? JP_NULL : JP_NOT_NULL);
  }
  public void putString(String string, int length) throws Exception
  {
    byte[] b = string.getBytes();
    putBytes(b, length);
  }
  public void putBytes(byte[] b, int length) throws Exception
  {
    if (b.length >= length)
      dos.write(b, 0, length);
    else 
    {
      int n = length - b.length;
      dos.write(b);
      for (int i = 0; i < n; i++)
        baos.write(0);
    }
  }
  public byte[] data()
  {
    byte[] block = baos.toByteArray(); 
    return block;
  }
  public void setMSizeOffset(int mSizeOffset)
  {
    this.mSizeOffset = mSizeOffset;
  }
  public int getMSizeOffset()
  {
    return mSizeOffset;
  }
  public void setDataSizeOffset(int dataSizeOffset)
  {
    this.dataSizeOffset = dataSizeOffset;
  }
  public int getDataSizeOffset()
  {
    return dataSizeOffset;
  }
}

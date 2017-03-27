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
import java.util.Random;

public class Rdc
{
  private static final int INTERNAL_BUFFSIZE = 8192;
  private static final int CHUNK_SIZE = INTERNAL_BUFFSIZE / 2;
  public Rdc()
  {}
  private static void print(String data)
  {
    System.out.println(data);
  }
  private static void print(String form, Object... objs)
  {
    print(String.format(form, objs));
  }
  public static void main(String[] args) throws Exception
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Random r = new Random();
    int size = r.nextInt(124)*124 + 7;
    for (int i=0; i<size; i++)
    {
      byte[] data = {' ',' ',' ',' ',' ',' ','A','A','A','B','C','D','E','E','E','E',-1,-2,-2,-3,-3,-3};
      baos.write(data[r.nextInt(data.length)]);
    }
    byte[] buffer1 = baos.toByteArray();
    print(Hexdump.dump(buffer1));
    byte[] buffer2;
    byte[] buffer3;
    Rdc rdc = new Rdc();
    print("start");
    buffer2 = rdc.compress(buffer1);
    print("tween");
    buffer3 = rdc.decompress(buffer2, buffer1.length);
    print("%d %d %d", buffer1.length, buffer2.length, buffer3.length);
    for (int i=0; i<buffer1.length; i++)
      if (buffer1[i] != buffer3[i])
        print("at %3d %d != %d", i, buffer1[i], buffer3[i]);
    print("done");
  }
  private static final int hashLen = 4096;
  public byte[] compress(byte[] inBuff)
  {
    int inLen = inBuff.length;
    ByteArrayOutputStream  baos = new ByteArrayOutputStream();
    if (inLen < 9)
    {
      baos.write(inBuff, 0, inLen);
      return baos.toByteArray();
    }
    int[] hashTable = new int[hashLen];
    for (int i=0; i<hashTable.length; i++)
      hashTable[i] = -1;
    int inPos = 0;
    int anchor;
    int patIndex;
    int count;
    int gap;
    byte c;
    int hash;
    int ctrlBits  = 0;
    int ctrlCount = 0;
    int ctrlPos   = 0;
    int outPos     = 1;
    byte[] outBuff = new byte[INTERNAL_BUFFSIZE+CHUNK_SIZE];
    while (inPos < inLen)                //while (InBuff < InBuffEnd)      // scan thru buff
    {
      if (ctrlCount++ == 8)             // make room for control bits
      {                                 // and check for aOutBuff overflow
        outBuff[ctrlPos] = (byte)(ctrlBits & 0xFF);
        if (outPos > INTERNAL_BUFFSIZE)
        {
          baos.write(outBuff, 0, outPos);
          outPos = 0;
        }
        ctrlPos = outPos++;
        ctrlCount = 1;
        ctrlBits = 0;
      }
      anchor = inPos;
      // The value in c may be positive or negative but this does not matter here
      c = inBuff[inPos++];
      while (inPos < inLen && inBuff[inPos] == c && (inPos - anchor) < 4110) // Was 4114
        inPos++;
      count = inPos - anchor;
      if (count > 2)
      {
        if (count <= 18)
        {
          outBuff[outPos++] = (byte)(count - 3);
          outBuff[outPos++] = c;
        }
        else
        {
          count -= 19;
          outBuff[outPos++] = (byte)(16 + (count & 0x0F));
          outBuff[outPos++] = (byte)(count >>> 4);
          outBuff[outPos++] = c;
        }
        ctrlBits = ((ctrlBits << 1) | 1);
        continue;
      }
      inPos = anchor;                // look for pattern if 2 or more
      if ((inLen - inPos) > 2)       // chars remain in the input buffer
      {                              // locate offset of possible pattern
                                     // in sliding dictionary
        int ib1 = (inBuff[inPos] & 0xFF);
        int ib2 = (inBuff[inPos+1] & 0xFF);
        int ib3 = (inBuff[inPos+2] & 0xFF);
        hash = ib1*17+ib2*31+ib3*23;
        hash %= hashLen;
        patIndex = hashTable[hash];
        hashTable[hash] = inPos;
        gap = inPos - patIndex;
        if (patIndex != -1 && gap <= 4090)
        {                            // compare chars within 4098 bytes (4090
          while (inPos < inLen
          &&     patIndex < anchor
          &&     inBuff[patIndex] == inBuff[inPos]
          &&    (inPos - anchor) < 271)
          {
            inPos++;
            patIndex++;
          }
          count = inPos - anchor;
          if (count > 2)
          {                          // store pattern if it is more
            gap -= 3;                // than two chars
            if (count <= 15)         // short pattern
            {
              outBuff[outPos++] = (byte)((count << 4) + (gap & 0x0F));
              outBuff[outPos++] = (byte)(gap >>> 4);
            }
            else
            {
              outBuff[outPos++] = (byte)(32 + (gap & 0x0F));    
              outBuff[outPos++] = (byte)(gap >>> 4);
              outBuff[outPos++] = (byte)(count - 16);
            }
            ctrlBits = ((ctrlBits << 1) | 1);
            continue;
          }
        }
      }
      outBuff[outPos++] = c;            // cant compress this char so
      inPos = ++anchor;                 // copy it to out buf (ignoring its sign here anyway)
      ctrlBits <<= 1;
    }
    ctrlBits <<= (8 - ctrlCount);       // save last load of bits
    outBuff[ctrlPos] = (byte)(ctrlBits & 0xFF);
    baos.write(outBuff, 0, outPos);
    return baos.toByteArray();
  }
  private static class Buffer
  {
    int pos;
    byte[] buffer;
    Buffer(int length)
    {
      pos = 0;
      buffer = new byte[length];
    }
    void put(byte data) throws Exception
    {
      if (pos >= buffer.length)
        throw new Exception(String.format("put(data=%d) -- exceeds buffer [pos=%d length=%d]", data, pos, buffer.length));
      buffer[pos++] = data;
    }
    void copy(int offset, int count) throws Exception
    {
      int in = pos - offset;
      //print("copy((%d-%d)=%d, %d) to %d", pos, offset, in, count, pos);
      if (in < 0 || pos + count > buffer.length + 1)      
        throw new Exception(String.format("copy(offset=%d, count=%d) -- out of ranges [pos=%d length=%d in(pos-offset)=%d ]", offset, count, pos, buffer.length, in));
      for (int i = 0; i < count; i++)
        buffer[pos++] = buffer[in++];
    }
    void rle(byte data, int count) throws Exception
    {
      //print("rle(%d, %d) to %d", data, count, pos);
      if (pos + count >= buffer.length + 1)      
        throw new Exception(String.format("rle(data=%d, count=%d) -- out of ranges [pos=%d length=%d]", data, count, pos, buffer.length));
      for (int i = 0; i < count; i++)
        buffer[pos++] = data;
    }
  }
  public byte[] decompress(byte[] inBuff, int length) throws Exception
  {
    int inLen = inBuff.length;
    int ctrlBits = 0;
    int ctrlMask = 0;
    int inPos = 0;
    int offset;
    Buffer outBuff = new Buffer(length);
    while (inPos < inLen)            // process each item in InBuff
    {
      if ((ctrlMask >>>= 1) == 0)          // get new load of control bits
      {                                   // if needed
        ctrlBits = inBuff[inPos] & 0xFF;
        inPos++;
        ctrlMask = 0x80;
      }
      if ((ctrlBits & ctrlMask) == 0)     // just copy this char if control
      {                                   // bit is zero
        outBuff.put(inBuff[inPos]);
        inPos++;
        continue;
      }
      int command = ((inBuff[inPos] >>> 4) & 0x0F);        // Undo the compression code
      int count = (inBuff[inPos] & 0x0F);
      inPos++;
      switch(command)
      {
      case 0:                             // short rule
        count += 3;
        outBuff.rle(inBuff[inPos], count);
        inPos++;
        break;
      case 1:                             // long rule
        count += ((inBuff[inPos] << 4) & 0xFF0);
        count += 19;
        inPos++;
        outBuff.rle(inBuff[inPos], count);
        inPos++;
        break;
      case 2:                             // long pattern
        offset = (count + 3);
        offset += ((inBuff[inPos] << 4) & 0x0FF0);
        inPos++;
        count = (inBuff[inPos] & 0xFF);
        inPos++;
        count += 16;
        outBuff.copy(offset, count);
        break;
      default:                            // short pattern
        offset = (count + 3);
        offset += ((inBuff[inPos] << 4) & 0xFF0);
        inPos++;
        outBuff.copy(offset, command);
        break;
      }
    }
    return outBuff.buffer;
  }
}

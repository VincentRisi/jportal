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

package bbd.idl2.rdc;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class RDC
{
  private static final int INTERNAL_BUFFSIZE = 8192;
  private static final int CHUNK_SIZE = INTERNAL_BUFFSIZE / 2;
  public static Logger logger = Logger.getLogger("bbd.idl2.rdc");
  public static void main(String[] args) throws Throwable
  {
    PropertyConfigurator.configure(args[0]);
    byte[] buffer1 = {12,12,12,13,1,15,12,1,20,20,20,13,1,15,12,1,20,44,44,33,21,22,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      12,12,12,13,1,15,12,1,20,20,20,13,1,15,12,1,20,44,44,33,21,22,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      12,12,12,13,1,15,12,1,20,20,20,13,1,15,12,1,20,44,44,33,21,22,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      12,12,12,13,1,15,12,1,20,20,20,13,1,15,12,1,20,44,44,33,21,22,
                      86,88,33,21,22,86,20,20,20,20,20,20,12,12,12,12,1,13,55,72,12,
                      47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,
                      47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47,47
                      };
    logger.info("BufferLength = "+buffer1.length);
    byte[] buffer2;
    byte[] buffer3;
    byte[] buffer4;
    buffer2 = compress(buffer1);
    logger.info("CompressLength = "+buffer2.length);
    buffer3 = decompress(buffer2);
    logger.info("DeCompressLength = "+buffer3.length);
    FileInputStream fis = new FileInputStream("C:\\react\\utility\\Special\\write1533298.file");
    buffer2 = new byte[fis.available()];
    fis.read(buffer2);
    fis.close();
    buffer3 = compress(buffer2);
    logger.info("Uncompressed: "+buffer2.length+" Compressed: "+buffer3.length);
    buffer4 = decompress(buffer3);
    logger.info("Uncompressed: "+buffer4.length+" Compressed: "+buffer3.length);
    FileOutputStream fos = new FileOutputStream("C:\\react\\utility\\Special\\write1533298.out");
    fos.write(buffer4);
    fos.close();
  }
  private static final int hashLen = 4096;
  public static byte[] compress(byte[] inBuff)
  {
    int inLen = inBuff.length;
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
    ByteArrayOutputStream  baos = new ByteArrayOutputStream();
    while (inPos < inLen)       //while (InBuff < InBuffEnd)            // scan thru buff
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
        hash = inBuff[inPos]*17+inBuff[inPos+1]*31+inBuff[inPos+2]*23;
        if (hash < 0) hash *= -1;
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
      inPos = ++anchor;                 // copy it to out buf
      ctrlBits <<= 1;
    }
    ctrlBits <<= (8 - ctrlCount);       // save last load of bits
    outBuff[ctrlPos] = (byte)(ctrlBits & 0xFF);
    baos.write(outBuff, 0, outPos);
    return baos.toByteArray();
  }
  public static byte[] decompress(byte[] inBuff)
  {
    return decompress(inBuff, 0);
  }
  public static byte[] decompress(byte[] inBuff, int length)
  {
    int inLen = inBuff.length;
    int ctrlBits = 0;
    int ctrlMask = 0;
    int inPos = 0;
    int outPos = 0;
    int command;
    int count;
    int offset;
    byte[] outBuff = new byte[INTERNAL_BUFFSIZE+CHUNK_SIZE*2];
    ByteArrayOutputStream  baos;
    if (length != 0)
      baos = new ByteArrayOutputStream(length);
    else
      baos = new ByteArrayOutputStream();
    while (inPos < inLen)            // process each item in InBuff
    {
      if ((ctrlMask >>>= 1) == 0)          // get new load of control bits
      {                                   // if needed
        ctrlBits = inBuff[inPos++] & 0xFF;
        ctrlMask = 0x80;
        if (outPos > INTERNAL_BUFFSIZE+CHUNK_SIZE)
        {
          baos.write(outBuff, 0, CHUNK_SIZE);
          for (int i=0; i<outPos-CHUNK_SIZE; i++)
            outBuff[i] = outBuff[i+CHUNK_SIZE];
          outPos -= CHUNK_SIZE;
        }
      }
      if ((ctrlBits & ctrlMask) == 0)     // just copy this char if control
      {                                   // bit is zero
        outBuff[outPos++] = inBuff[inPos++];
        continue;
      }
      command = ((inBuff[inPos] >>> 4) & 0x0F);        // Undo the compression code
      count = (inBuff[inPos++] & 0x0F);
      switch(command)
      {
      case 0:                             // short rule
        count += 3;
        while (count-- > 0) outBuff[outPos++] = inBuff[inPos];
        inPos++;
        break;
      case 1:                             // long rule
        count += ((inBuff[inPos++] << 4) & 0xFF0);
        count += 19;
        while (count-- > 0) outBuff[outPos++] = inBuff[inPos];
        inPos++;
        break;
      case 2:                             // long pattern
        offset = (count + 3);
        offset += ((inBuff[inPos++] << 4) & 0xFF0);
        count = inBuff[inPos++];
        count += 16;
        for (int i=0; i<count; i++, outPos++)
          outBuff[outPos] = outBuff[outPos-offset];
        break;
      default:                            // short pattern
        offset = (count + 3);
        offset += ((inBuff[inPos++] << 4) & 0xFF0);
        for (int i=0; i<command; i++, outPos++)
          outBuff[outPos] = outBuff[outPos-offset];
        break;
      }
    }
    if (outPos > 0)
       baos.write(outBuff, 0, outPos);
    return baos.toByteArray();
  }
}

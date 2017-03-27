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

using System;

namespace bbd.utility
{
  public class Rdc
  {
    private static int hashLen = 4096;
    private static void copy(byte[] outp, byte[] inp)
    {
      for (int i=0; i<outp.Length; i++)
        outp[i] = inp[i];
    }
    public static int Compress(byte[] inBuff, int inLen, byte[] outBuff)
    {
      int[] hashTable = new int[hashLen];
      for (int i=0; i<hashTable.Length; i++)
        hashTable[i] = -1;
      int inPos = 0;
      int anchor;
      int patIndex;
      int count;
      int gap;
      byte c;
      int hash;
      int ctrlIndex  = 0;
      int ctrlBits  = 0;
      int ctrlCount = 0;
      int outPos     = 1;
      if (inLen <= 9)             // skip compression for smaller
      {                           // buffer
        copy(outBuff, inBuff);
        return 0;
      }
      while (inPos < inLen)       //while (InBuff < InBuffEnd)            // scan thru buff
      {
        if (ctrlCount++ == 8)     // make room for control bits
        {                         // and check for aOutBuff overflow
          outBuff[ctrlIndex] = (byte)(ctrlBits & 0xFF);
          ctrlCount = 1;
          ctrlIndex = outPos++;
          if (outPos+1 > outBuff.Length)
          {
            copy(outBuff, inBuff);
            return 0;
          }
        }
        anchor = inPos;
        c = inBuff[inPos++];
        while (inPos < inLen && inBuff[inPos] == c && (inPos - anchor) < 4110)     // Was 4114
          inPos++;
        count = inPos - anchor;
        if (count > 2)
        {
          if (count <= 18)
          {
            if (outPos+2 > outBuff.Length)
            {
              copy(outBuff, inBuff);
              return 0;
            }
            outBuff[outPos++] = (byte)(count - 3);
            outBuff[outPos++] = c;
          }
          else
          {
            count -= 19;
            if (outPos+3 > outBuff.Length)
            {
              copy(outBuff, inBuff);
              return 0;
            }
            outBuff[outPos++] = (byte)(16 + (count & 0x0F));
            outBuff[outPos++] = (byte)((uint)count >> 4);
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
                if (outPos+2 > outBuff.Length)
                {
                  copy(outBuff, inBuff);
                  return 0;
                }
                outBuff[outPos++] = (byte)((count << 4) + (gap & 0x0F));
                outBuff[outPos++] = (byte)((uint)gap >> 4);
              }
              else
              {
                if (outPos+3 > outBuff.Length)
                {
                  copy(outBuff, inBuff);
                  return 0;
                }
                outBuff[outPos++] = (byte)(32 + (gap & 0x0F));
                outBuff[outPos++] = (byte)((uint)gap >> 4);
                outBuff[outPos++] = (byte)(count - 16);
              }
              ctrlBits = ((ctrlBits << 1) | 1);
              continue;
            }
          }
        }
        if (outPos+1 > outBuff.Length)
        {
          copy(outBuff, inBuff);
          return 0;
        }
        outBuff[outPos++] = c;          // cant compress this char so
        inPos = ++anchor;               // copy it to out buf
        ctrlBits <<= 1;
      }
      ctrlBits <<= (8 - ctrlCount);     // save last load of bits
      outBuff[ctrlIndex] = (byte)(ctrlBits & 0xFF);
      if (outPos >= outBuff.Length)
      {
        copy(outBuff, inBuff);
        return 0;
      }
      return outPos;
    }
    public static int Decompress(byte[] inBuff, int inLen, byte[] outBuff)
    {
      int ctrlBits = 0;
      uint ctrlMask = 0;
      int inPos = 0;
      int outPos = 0;
      int command;
      int count;
      int offset;
      while (inPos < inLen)            // process each item in InBuff
      {
        if ((ctrlMask >>= 1) == 0)     // get new load of control bits
        {                              // if needed
          ctrlBits = inBuff[inPos++] & 0xFF;
          ctrlMask = 0x80;
        }
        if ((ctrlBits & ctrlMask) == 0) // just copy this char if control
        {                               // bit is zero
          outBuff[outPos++] = inBuff[inPos++];
          continue;
        }
        command = (int)(((uint)inBuff[inPos] >> 4) & 0x0F); // Undo the compression code
        count = (inBuff[inPos++] & 0x0F);
        switch(command)
        {
        case 0:                         // short rule
          count += 3;
          while (count-- > 0) outBuff[outPos++] = inBuff[inPos];
          inPos++;
          break;
        case 1:                         // long rule
          count += ((inBuff[inPos++] << 4) & 0xFF0);
          count += 19;
          while (count-- > 0) outBuff[outPos++] = inBuff[inPos];
          inPos++;
          break;
        case 2:                         // long pattern
          offset = (count + 3);
          offset += ((inBuff[inPos++] << 4) & 0xFF0);
          count = inBuff[inPos++];
          count += 16;
          for (int i=0; i<count; i++, outPos++)
            outBuff[outPos] = outBuff[outPos-offset];
          break;
        default:                        // short pattern
          offset = (count + 3);
          offset += ((inBuff[inPos++] << 4) & 0xFF0);
          for (int i=0; i<command; i++, outPos++)
            outBuff[outPos] = outBuff[outPos-offset];
          break;
        }
      }
      return outPos;
    }
  }
}

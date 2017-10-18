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
	public class UUCode
	{
		public UUCode()
		{}
    public int Encode(byte[] input, byte[] output)
    {
      uint[] inp = new uint[3];
      int o=0;
      for (int i=0; i<input.Length; i += 3)
      {
        for (int j=0; j<3; j++)
        {
          if (j < input.Length - i)
            inp[j] = input[i];
          else
            inp[j] = 0;
        }
        output[o++] = (byte)((inp[0] >> 2) + 32);
        if (o >= output.Length) 
          break;
        output[o++] = (byte)((((inp[0] & 0x03) << 4) | (inp[1] >> 4)) + 32);
        if (o >= output.Length) 
          break;
        output[o++] = (byte)((((inp[1] & 0x0F) << 2) | (inp[2] >> 6)) + 32);
        if (o >= output.Length) 
          break;
        output[o++] = (byte)((inp[2] & 0x3F) + 32);
        if (o >= output.Length) 
          break;
      }
      return o;
    }
    public int Decode(byte[] input, byte[] output)
    {
      uint[] inp = new uint[4];
      int o=0;
      for (int i=0; i<input.Length; i += 4)
      {
        for (int j=0; j<4; j++)
        {
          if (j < input.Length - i)
            inp[j] = input[i+j] - 32u;
          else
            inp[j] = 0;
        }
        uint p1 = (inp[0] & 0x3F) << 2; // --aaaaaa <-> aaaaaaa--
        uint p2 = (inp[1] & 0x30) >> 4; // --bb---- <-> -------bb
        output[o] = (byte)(p1 | p2);
        if (++o >= output.Length) 
          break;
        p1 = (inp[1] & 0x0F) << 4;      // ----bbbb <-> bbbb----
        p2 = (inp[2] & 0x3c) >> 2;      // --cccc-- <-> ----cccc
        output[o] = (byte)(p1 | p2);
        if (++o >= output.Length) 
          break;
        p1 = (inp[2] & 0x03) << 6;      // ------cc <-> cc------
        p2 = (inp[3] & 0x3F);           // --dddddd <-> --dddddd
        output[o] = (byte)(p1 | p2);
        if (++o >= output.Length) 
          break;
      }
      return o;
    }
  }
}

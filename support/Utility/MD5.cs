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
  public class MD5
  {
    private uint[] ctxbuf  = new uint[4];
    private uint[] ctxi    = new uint[2];
    private byte[] ctxin  = new byte[64];
    private byte[] digest = new byte[16];
    public byte[] Digest {get {return digest;}}
    public MD5()
    {
      ctxi[0] = ctxi[1] = 0;
      ctxbuf[0] = 0x67452301;
      ctxbuf[1] = 0xefcdab89;
      ctxbuf[2] = 0x98badcfe;
      ctxbuf[3] = 0x10325476;
    }
    public MD5(byte[] inBuf) : this()
    {
      Update(inBuf, (uint)inBuf.Length);
      Finish();
    }
    public void Update(byte[] inBuf)
    {
      Update(inBuf, (uint)inBuf.Length);
    }
    public void Update(byte[] inBuf, uint length)
    {
      uint i, ii;
      uint mdi;
      uint[] inp = new uint[16];
      // compute number of bytes mod 64
      mdi = (uint)(ctxi[0] >> 3) & 0x3F;
      // update number of bits
      if ((ctxi[0] + (length << 3)) < ctxi[0])
        ctxi[1]++;
      ctxi[0] += (length << 3);
      ctxi[1] += (length >> 29);
      for (uint n=0; n<length; n++)
      {
        // add new character to buffer, increment mdi 
        ctxin[mdi++] = inBuf[n];
        // transform if necessary 
        if (mdi == 0x40)
        {
          for (i = 0, ii = 0; i < 16; i++, ii += 4)
            inp[i] = ((uint)(ctxin[ii+3] << 24) & 0xFF000000)
              | ((uint)(ctxin[ii+2] << 16) & 0x00FF0000)
              | ((uint)(ctxin[ii+1] <<  8) & 0x0000FF00)
              | ((uint) ctxin[ii]          & 0x000000FF);
          Transform(ctxbuf, inp);
          mdi = 0;
        }
      }
    }
    public void Finish()
    {
      uint[] inp = new uint[16];
      uint mdi;
      uint i, ii;
      uint padLen;
      // save number of bits 
      inp[14] = ctxi[0];
      inp[15] = ctxi[1];
      // compute number of bytes mod 64 
      mdi = (ctxi[0] >> 3) & 0x3F;
      // pad out to 56 mod 64 
      padLen = (mdi < 56) ? (56 - mdi) : (120 - mdi);
      Update(padding, padLen);
      // append length in bits and transform 
      for (i = 0, ii = 0; i < 14; i++, ii += 4)
        inp[i] = ((uint)(ctxin[ii+3] << 24) & 0xFF000000)
          | ((uint)(ctxin[ii+2] << 16) & 0x00FF0000)
          | ((uint)(ctxin[ii+1] << 8)  & 0x0000FF00)
          | ((uint) ctxin[ii]          & 0x000000FF);
      Transform(ctxbuf, inp);
      // store buffer in digest 
      for (i = 0, ii = 0; i < 4; i++, ii += 4)
      {
        digest[ii]   = (byte)( ctxbuf[i]        & 0xFF);
        digest[ii+1] = (byte)((ctxbuf[i] >> 8)  & 0xFF);
        digest[ii+2] = (byte)((ctxbuf[i] >> 16) & 0xFF);
        digest[ii+3] = (byte)((ctxbuf[i] >> 24) & 0xFF);
      }
    }
    private static byte[] padding =
    {
      0x80, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0
    };
    private static uint RotateLeft(uint x, uint n)
    {
      return (x << (int)n) | (x >> (int)(32-n));
    }
    private static uint Fold(uint x, uint y, uint z)
    {
      return (x & y) | (~x & z);
    }
    private static uint FoldRotate(uint a, uint b, uint c, uint d, uint x, uint s, uint ac)
    {
      a = a + Fold(b, c, d) + x + ac;
      a = RotateLeft(a, s);
      return a + b;
    }
    private static uint Flip(uint x, uint y, uint z)
    {
      return (x & z) | (y & ~z);
    }
    private static uint FlipRotate(uint a, uint b, uint c, uint d, uint x, uint s, uint ac)
    {
      a = a + Flip(b, c, d) + x + ac;
      a = RotateLeft(a, s);
      return a + b;
    }
    private static uint Flap(uint x, uint y, uint z)
    {
      return x ^ y ^ z;
    }
    private static uint FlapRotate(uint a, uint b, uint c, uint d, uint x, uint s, uint ac)
    {
      a = a + Flap(b, c, d) + x + ac;
      a  = RotateLeft(a, s);
      return a + b;
    }
    private static uint Fling(uint x, uint y, uint z)
    {
      return y ^ (x | ~z);
    }
    private static uint FlingRotate(uint a, uint b, uint c, uint d, uint x, uint s, uint ac)
    {
      a = a + Fling(b, c, d) + x + ac;
      a  = RotateLeft(a, s);
      return a + b;
    }
    private static void Transform(uint[] buf, uint[] inp)
    {
      uint a = buf[0], b = buf[1], c = buf[2], d = buf[3];
      // Round 1 
      uint S11 = 7;
      uint S12 = 12;
      uint S13 = 17;
      uint S14 = 22;
      a = FoldRotate(a, b, c, d, inp[ 0], S11, 0xD76AA478); // 1 
      d = FoldRotate(d, a, b, c, inp[ 1], S12, 0xE8C7B756); // 2 
      c = FoldRotate(c, d, a, b, inp[ 2], S13, 0x242070DB); // 3 
      b = FoldRotate(b, c, d, a, inp[ 3], S14, 0xC1BDCEEE); // 4 
      a = FoldRotate(a, b, c, d, inp[ 4], S11, 0xF57C0FAF); // 5 
      d = FoldRotate(d, a, b, c, inp[ 5], S12, 0x4787C62A); // 6 
      c = FoldRotate(c, d, a, b, inp[ 6], S13, 0xA8304613); // 7 
      b = FoldRotate(b, c, d, a, inp[ 7], S14, 0xFD469501); // 8 
      a = FoldRotate(a, b, c, d, inp[ 8], S11, 0x698098D8); // 9 
      d = FoldRotate(d, a, b, c, inp[ 9], S12, 0x8B44F7AF); // 10 
      c = FoldRotate(c, d, a, b, inp[10], S13, 0xFFFF5BB1); // 11 
      b = FoldRotate(b, c, d, a, inp[11], S14, 0x895CD7BE); // 12 
      a = FoldRotate(a, b, c, d, inp[12], S11, 0x6B901122); // 13 
      d = FoldRotate(d, a, b, c, inp[13], S12, 0xFD987193); // 14 
      c = FoldRotate(c, d, a, b, inp[14], S13, 0xA679438E); // 15 
      b = FoldRotate(b, c, d, a, inp[15], S14, 0x49B40821); // 16 
      // Round 2 
      uint S21 = 5;
      uint S22 = 9;
      uint S23 = 14;
      uint S24 = 20;
      a = FlipRotate(a, b, c, d, inp[ 1], S21, 0xF61E2562); // 17 
      d = FlipRotate(d, a, b, c, inp[ 6], S22, 0xC040B340); // 18 
      c = FlipRotate(c, d, a, b, inp[11], S23, 0x265E5A51); // 19 
      b = FlipRotate(b, c, d, a, inp[ 0], S24, 0xE9B6C7AA); // 20 
      a = FlipRotate(a, b, c, d, inp[ 5], S21, 0xD62F105D); // 21 
      d = FlipRotate(d, a, b, c, inp[10], S22, 0x02441453); // 22 
      c = FlipRotate(c, d, a, b, inp[15], S23, 0xD8A1E681); // 23 
      b = FlipRotate(b, c, d, a, inp[ 4], S24, 0xE7D3FBC8); // 24 
      a = FlipRotate(a, b, c, d, inp[ 9], S21, 0x21E1CDE6); // 25 
      d = FlipRotate(d, a, b, c, inp[14], S22, 0xC33707D6); // 26 
      c = FlipRotate(c, d, a, b, inp[ 3], S23, 0xF4D50D87); // 27 
      b = FlipRotate(b, c, d, a, inp[ 8], S24, 0x455A14ED); // 28 
      a = FlipRotate(a, b, c, d, inp[13], S21, 0xA9E3E905); // 29 
      d = FlipRotate(d, a, b, c, inp[ 2], S22, 0xFCEFA3F8); // 30 
      c = FlipRotate(c, d, a, b, inp[ 7], S23, 0x676F02D9); // 31 
      b = FlipRotate(b, c, d, a, inp[12], S24, 0x8D2A4C8A); // 32 
      // Round 3 
      uint S31 = 4;
      uint S32 = 11;
      uint S33 = 16;
      uint S34 = 23;
      a = FlapRotate(a, b, c, d, inp[ 5], S31, 0xFFFA3942); // 33 
      d = FlapRotate(d, a, b, c, inp[ 8], S32, 0x8771F681); // 34 
      c = FlapRotate(c, d, a, b, inp[11], S33, 0x6D9D6122); // 35 
      b = FlapRotate(b, c, d, a, inp[14], S34, 0xFDE5380C); // 36 
      a = FlapRotate(a, b, c, d, inp[ 1], S31, 0xA4BEEA44); // 37 
      d = FlapRotate(d, a, b, c, inp[ 4], S32, 0x4BDECFA9); // 38 
      c = FlapRotate(c, d, a, b, inp[ 7], S33, 0xF6BB4B60); // 39 
      b = FlapRotate(b, c, d, a, inp[10], S34, 0xBEBFBC70); // 40 
      a = FlapRotate(a, b, c, d, inp[13], S31, 0x289B7EC6); // 41 
      d = FlapRotate(d, a, b, c, inp[ 0], S32, 0xEAA127FA); // 42 
      c = FlapRotate(c, d, a, b, inp[ 3], S33, 0xD4EF3085); // 43 
      b = FlapRotate(b, c, d, a, inp[ 6], S34, 0x04881D05); // 44 
      a = FlapRotate(a, b, c, d, inp[ 9], S31, 0xD9D4D039); // 45 
      d = FlapRotate(d, a, b, c, inp[12], S32, 0xE6DB99E5); // 46 
      c = FlapRotate(c, d, a, b, inp[15], S33, 0x1FA27CF8); // 47 
      b = FlapRotate(b, c, d, a, inp[ 2], S34, 0xC4AC5665); // 48 
      // Round 4 
      uint S41 = 6;
      uint S42 = 10;
      uint S43 = 15;
      uint S44 = 21;
      a = FlingRotate(a, b, c, d, inp[ 0], S41, 0xF4292244); // 49 
      d = FlingRotate(d, a, b, c, inp[ 7], S42, 0x432AFF97); // 50 
      c = FlingRotate(c, d, a, b, inp[14], S43, 0xAB9423A7); // 51 
      b = FlingRotate(b, c, d, a, inp[ 5], S44, 0xFC93A039); // 52 
      a = FlingRotate(a, b, c, d, inp[12], S41, 0x655B59C3); // 53 
      d = FlingRotate(d, a, b, c, inp[ 3], S42, 0x8F0CCC92); // 54 
      c = FlingRotate(c, d, a, b, inp[10], S43, 0xFFEFF47D); // 55 
      b = FlingRotate(b, c, d, a, inp[ 1], S44, 0x85845DD1); // 56 
      a = FlingRotate(a, b, c, d, inp[ 8], S41, 0x6FA87E4F); // 57 
      d = FlingRotate(d, a, b, c, inp[15], S42, 0xFE2CE6E0); // 58 
      c = FlingRotate(c, d, a, b, inp[ 6], S43, 0xA3014314); // 59 
      b = FlingRotate(b, c, d, a, inp[13], S44, 0x4E0811A1); // 60 
      a = FlingRotate(a, b, c, d, inp[ 4], S41, 0xF7537E82); // 61 
      d = FlingRotate(d, a, b, c, inp[11], S42, 0xBD3AF235); // 62 
      c = FlingRotate(c, d, a, b, inp[ 2], S43, 0x2AD7D2BB); // 63 
      b = FlingRotate(b, c, d, a, inp[ 9], S44, 0xEB86D391); // 64 
      buf[0] += a;
      buf[1] += b;
      buf[2] += c;
      buf[3] += d;
    }
  }
}

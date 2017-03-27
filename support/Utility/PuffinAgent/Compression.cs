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
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.IO.Compression;

namespace bbd.idl2.PuffinAgent
{
  public class Compression
  {
    public static string HexDump(byte[] b)
    {
      string result = "";
      string hexString = "";
      string charString = "";
      int offset = 0;
      int len = b.Length;
      for (int i = 0; i <= len; i++)
      {
        if (i > 0 && (i % 16) == 0 || i == len)
        {
          for (int j = charString.Length; j < 16; j++)
          {
            hexString += "  ";
            charString += " ";
          }
          result += string.Format("{2,5:X} | {0,-36} | {1,-16} | {2,5}\r\n", hexString, charString, offset);
          hexString = "";
          charString = "";
          offset = i;
        }
        if (i == len)
          break;
        string ch;
        if (b[i] > 0x20 && b[i] < 0x7F)
          ch = string.Format("{0}", (char)b[i]);
        else
          ch = ".";
        charString += ch;
        ch = string.Format("{0:X}", b[i]);
        string hex;

        if (ch.Length == 1)
          hex = "0" + ch;
        else
          hex = ch;

        hexString += hex;

        if ((i % 4) == 3)
          hexString += " ";
      }
      return result;
    }
    private static void SetLength(int size, byte[] output, int index)
    {
      output[index] = (byte)((size & 0xFF000000) >> 24);
      output[index + 1] = (byte)((size & 0xFF0000) >> 16);
      output[index + 2] = (byte)((size & 0xFF00) >> 8);
      output[index + 3] = (byte)(size & 0xFF);
    }
    private static int GetLength(byte[] data, int index)
    {
      return (data[index] << 24
      | data[index + 1] << 16
      | data[index + 2] << 8
      | data[index + 3]);
    }
    public static byte[] Compress(string data)
    {
      byte[] input = Encoding.ASCII.GetBytes(data);
      MemoryStream ms = new MemoryStream();
      DeflateStream zipper = new DeflateStream(ms, CompressionMode.Compress, true);
      zipper.Write(input, 0, input.Length);
      zipper.Close();
      byte[] output;
      if (ms.Length < input.Length)
      {
        output = new byte[ms.Length + 8];
        SetLength(input.Length, output, 0);
        SetLength((int)ms.Length, output, 4);
        ms.Position = 0;
        ms.Read(output, 8, output.Length - 8);
      }
      else
      {
        output = new byte[input.Length + 8];
        SetLength(input.Length, output, 0);
        SetLength(input.Length, output, 4);
        input.CopyTo(output, 8);
      }
      return output;
    }
    public static string Decompress(byte[] input)
    {
      int uncomp = GetLength(input, 0);
      int comp = GetLength(input, 4);
      byte[] output = new byte[uncomp];
      if (uncomp > comp)
      {
        MemoryStream ms = new MemoryStream();
        ms.Write(input, 8, comp);
        ms.Flush();
        ms.Position = 0;
        DeflateStream zipper = new DeflateStream(ms, CompressionMode.Decompress);
        int pos = 0;
        pos += zipper.Read(output, pos, 1024);
        while (true)
        {
          int length = zipper.Read(output, pos, output.Length - pos);
          pos += length;
          if (length == 0)
            break;
        }
      }
      else
      {
        for (int i = 0; i < uncomp; i++)
          output[i] = input[i + 8];
      }
      return Encoding.ASCII.GetString(output);
    }
    public static string Encode(byte[] input)
    {
      int len = input.Length * 4;
      int rem = len % 3;
      len = len / 3 + rem;
      byte[] output = new byte[len];
      uint[] inp = new uint[3];
      int o = 0;
      for (int i = 0; i < input.Length; i += 3)
      {
        for (int j = 0; j < 3; j++)
        {
          if (j < input.Length - i)
            inp[j] = input[i+j];
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
      return Encoding.ASCII.GetString(output, 0, o);
    }
    public static byte[] Decode(string data)
    {
      byte[] input = Encoding.ASCII.GetBytes(data);
      int len = input.Length * 3;
      int rem = len % 4;
      len = len / 4 + rem;
      byte[] output = new byte[len];
      uint[] inp = new uint[4];
      int o = 0;
      for (int i = 0; i < input.Length; i += 4)
      {
        for (int j = 0; j < 4; j++)
        {
          if (j < input.Length - i)
            inp[j] = input[i + j] - 32u;
          else
            inp[j] = 0;
        }
        uint p1 = (inp[0] & 0x3F) << 2; // --aaaaaa <-> aaaaaa--
        uint p2 = (inp[1] & 0x30) >> 4; // --bb---- <-> ------bb bbbb----
        output[o] = (byte)(p1 | p2);    //                       ----cccc cc------
        if (++o >= output.Length)       //                                --dddddd
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
      return output;
    }
  }
}

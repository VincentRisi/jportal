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

public class Hexdump
{
  public static String dump(byte[] b)
  {
    StringBuilder result = new StringBuilder(b.length*4);
    char[] hexA = new char[36];
    char[] chA = new char[16];
    int hno = clear(hexA);
    int cno = clear(chA);
    int offset = 0;
    int noBytes = b.length;
    for (int i = 0; i < noBytes; i++)
    {
      if (i > 0 && (i % 16) == 0)
      {
        hexLine(result, hexA, chA, offset);
        hno = clear(hexA);
        cno = clear(chA);
        offset = i;
      }
      String ch;
      if (b[i] > 0x20 && b[i] < 0x7F)
        ch = String.format("%c", (char) b[i]);
      else
        ch = ".";
      chA[cno++] = ch.charAt(0);
      ch = String.format("%X", b[i]);
      String hex;
      if (ch.length() == 1)
        hex = "0" + ch;
      else
        hex = ch;
      hexA[hno++] = hex.charAt(0);
      hexA[hno++] = hex.charAt(1);
      if ((i % 4) == 3)
        hexA[hno++] = ' ';
    }
    if (hno != 0)
      hexLine(result, hexA, chA, offset);
    return result.toString();
  }
  private static int clear(char[] arr)
  {
    for (int i=0; i<arr.length; i++)
      arr[i] = ' ';
    return 0;
  }
  private static void hexLine(StringBuilder result, char[] hexString, char[] charString, int offset)
  {
    result.append(String.format("%06X | ", offset));
    result.append(hexString);
    result.append(" | ");
    result.append(charString);
    result.append(String.format(" | %06d\n", offset));
  }
}

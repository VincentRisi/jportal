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
/*
 * Created on Jan 13, 2004
 */
package bbd.crackle.rpc;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
/**
 * @author vince
 */
public class Services
{
  public static void main(String[] args) throws Exception
  {
    int n = getService("d:\\winnt\\system32\\drivers\\etc\\services", "upm2");
    System.out.println(n);
  }
  public static int getService(String serviceFile, String service)
    throws Exception
  {
    int result = 0;
    try
    {
      FileReader reader = new FileReader(serviceFile);
      try
      {
        char[] data = new char[4096];
        char[] word = new char[256];
        char[] number = new char[256];
        String name = "";
        int state = 0;
        int no;
        int index = 0;
        do
        {
          no = reader.read(data);
          for (int i = 0; i < no; i++)
          {
            char ch = data[i];
            if (ch == 0x0d)
              continue;
            if (ch == 0x0a)
            {
              state = 0;
              index = 0;
              continue;
            }
            if (ch == '#')
            {
              state = 9;
              continue;
            }
            switch (state)
            {
              case 0 : // front of line
                if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
                {
                  word[index++] = ch;
                  state = 1;
                }
                else
                  state = 9;
                break;
              case 1 : // name at start of line
                if (ch == ' ' || ch == '\t')
                {
                  name = new String(word, 0, index);
                  state = 2;
                }
                else
                  word[index++] = ch;
                break;
              case 2 : // skip white space til number
                if (ch >= '0' && ch <= '9')
                {
                  index = 0;
                  number[index++] = ch;
                  state = 3;
                }
                break;
              case 3 : // get number data
                if (ch == '/')
                  state = 4;
                else if (ch < '0' || ch > '9')
                  state = 9;
                else
                  number[index++] = ch;
                break;
              case 4 : // check for t
                if (ch != 't' || !service.equalsIgnoreCase(name))
                  state = 9;
                else
                {
                  String nval = new String(number, 0, index);
                  Integer value = new Integer(nval);
                  return value.intValue();
                }
                break;
              case 9 : // ignore rest of line
                break;
            }
          }
        }
        while (no > 0);
      }
      finally
      {
        reader.close();
      }
    }
    catch (FileNotFoundException fnf)
    {
      System.out.println("file not found");
      throw fnf;
    }
    catch (IOException io)
    {
      System.out.println("io error");
      throw io;
    }
    return result;
  }
}

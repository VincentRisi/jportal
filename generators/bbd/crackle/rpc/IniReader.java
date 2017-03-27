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
import java.util.Hashtable;
/**
 * @author vince
 */
public class IniReader
{
  private Hashtable<String, Hashtable<String, String>> sections = new Hashtable<String, Hashtable<String, String>>();
  private Hashtable<String, String> items;
  public IniReader(String fileName) throws Exception
  {
    try
    {
      FileReader fileReader = new FileReader(fileName);
      try
      {
        int no;
        int state = 0;
        int sectIndex = 0;
        int itemIndex = 0;
        int valueIndex = 0;
        char[] data = new char[4096];
        char[] work = new char[1024];
        String section = "";
        String item = "";
        String value = "";
        do
        {
          no = fileReader.read(data);
          for (int i = 0; i < no; i++)
          {
            char ch = data[i];
            if (ch == 0x0d)
              continue;
            switch (state)
            {
              case 0 : // null state
                if (ch == '[')
                {
                  sectIndex = 0;
                  state = 1;
                  continue;
                }
                if (ch == '{')
                {
                  itemIndex = 0;
                  state = 2;
                  continue;
                }
                break;
              case 1 : // section state
                if (ch == ']')
                {
                  section = new String(work, 0, sectIndex);
                  items = new Hashtable<String, String>();
                  sections.put(section.toUpperCase(), items);
                  state = 0;
                  continue;
                }
                work[sectIndex++] = ch;
                break;
              case 2 : // item state
                if (ch == '}')
                {
                  item = new String(work, 0, itemIndex);
                  valueIndex = 0;
                  state = 3;
                  continue;
                }
                work[itemIndex++] = ch;
                break;
              case 3 : // value state
                if (ch == 0x0a)
                {
                  value = new String(work, 0, valueIndex);
                  items.put(item.toUpperCase(), value);
                  state = 0;
                  continue;
                }
                work[valueIndex++] = ch;
                break;
            }
          }
        }
        while (no > 0);
      }
      finally
      {
        fileReader.close();
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
    }
  }
  public String getString(String section, String item, String defaultValue)
  {
    items = sections.get(section.toUpperCase());
    if (items == null)
      return defaultValue;
    String result = items.get(item.toUpperCase());
    if (result == null)
      return defaultValue;
    return result;
  }
  public int getInteger(String section, String item, int defaultValue)
  {
    String result = getString(section, item, "");
    if (result.length() == 0)
      return defaultValue;
    return Integer.parseInt(result);
  }
  public double getDouble(String section, String item, double defaultValue)
  {
    String result = getString(section, item, "");
    if (result.length() == 0)
      return defaultValue;
    return Double.parseDouble(result);
  }
}

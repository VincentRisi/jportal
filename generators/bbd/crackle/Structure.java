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
package bbd.crackle;

import java.io.Serializable;
import java.util.Vector;

/**
*
*/
public class Structure implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final byte
    NORMAL     = 0
  , PUBLIC     = 1
  , PRIVATE    = 2
  , PROTECTED  = 3
  ;
  public String name;
  public String header;
  public Vector<String> categories;
  public Vector<Field> fields;
  public byte codeType;
  public Vector<String> code;
  public int codeLine;
  public int start;
  public Structure()
  {
    name = "";
    header = "";
    categories = new Vector<String>();
    fields = new Vector<Field>();
    codeType = NORMAL;
    code = new Vector<String>();
    codeLine = 0;
    start = 0;
  }
  public boolean hasSwap()
  {
    for (int i = 0; i < fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      if (field.needsSwap())
        return true;
    }
    return false;
  }
  public int dotNetSize()
  {
    int result = 0;
    for (int i = 0; i < fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      result += field.dotNetSize();
    }
    return result;
  }
  public String toString()
  {
    return name;
  }
}

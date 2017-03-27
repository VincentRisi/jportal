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

/**
*
*/
public class Field implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String name;
  public Type type;
  public Action input;
  public Action output;
  public boolean isInput;
  public boolean isOutput;
  public boolean hasSize;
  public Field()
  {
    name = "";
    type = new Type();
    input = null;
    output = null;
    isInput = false;
    isOutput = false;
    hasSize = false;
  }
  public boolean needsSwap()
  {
    return (type.typeof == Type.SHORT
         || type.typeof == Type.INT
         || type.typeof == Type.BOOLEAN
         || type.typeof == Type.LONG
         || type.typeof == Type.FLOAT
         || type.typeof == Type.DOUBLE);
  }
  public boolean needsTrim()
  {
    return (type.typeof == Type.CHAR
        && (type.arraySizes.size() > 0
        ||  type.reference != Type.BYVAL));
  }
  public boolean isStruct(Module module)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure struct = (Structure)module.structures.elementAt(i);
      if (struct.name.compareTo(type.name) == 0)
        return true;
    }
    return false;
  }
  public Structure asStruct(Module module)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure struct = (Structure)module.structures.elementAt(i);
      if (struct.name.compareTo(type.name) == 0)
        return struct;
    }
    return null;
  }
  public int dotNetSize()
  {
    return type.dotNetSize();
  }
  public String nameLowerFirst()
  {
    String result = name.substring(0, 1).toLowerCase() + name.substring(1);
    if (result.compareTo("ref") == 0 || 
        result.compareTo("out") == 0 || 
        result.compareTo("value") == 0 || 
        result.compareTo("get") == 0 || 
        result.compareTo("set") == 0
      )
      result = "_" + result;
    return result;
  }
  public String nameUpperFirst()
  {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }
}

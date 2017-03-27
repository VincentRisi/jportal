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
public class Type implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final byte
      USERTYPE   =  0
    , BOOLEAN    =  1
    , CHAR       =  2
    , SHORT      =  3
    , LONG       =  4
    , FLOAT      =  5
    , DOUBLE     =  6
    , VOID       =  7
    , BYTE       =  8
    , INT        =  9
    , STRING     = 10
    , WCHAR      = 11
    , BYVAL      =  1
    , BYPTR      =  2
    , BYREF      =  3
    , BYPTRPTR   =  4
    , BYREFPTR   =  5
    , ARRAYED    =  6
    ;
  public String  name;
  public byte    typeof;
  public byte    reference;
  public boolean isUnsigned;
  public Vector<Integer>  arraySizes;
  public Type()
  {
    name = "";
    typeof = USERTYPE;
    reference = BYVAL;
    isUnsigned = false;
    arraySizes = new Vector<Integer>();
  }
  public int relativeSize(boolean longAsInt)
  {
    int n = 1;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      n *= integer.intValue();
    }
    switch(typeof)
    {
    case BYTE:
    case CHAR:
    case STRING:
      return n%8;
    case SHORT:
      return (2*n)%8;
    case INT:
    case BOOLEAN:
      return (4*n)%8;
    case LONG:
      if (longAsInt)
        return (4*n)%8;
      else
        return (8*n)%8;
    default:
      return (8*n)%8;
    }
  }
  public int paddingSize(boolean longAsInt)
  {
    switch(typeof)
    {
    case BYTE:
    case CHAR:
    case STRING:
      return 1;
    case SHORT:
      return 2;
    case INT:
    case BOOLEAN:
      return 4;
    case LONG:
      if (longAsInt)
        return 4;
      else
        return 8;
    default:
      return 8;
    }
  }
  public String cDef(String aName)
  {
    return cDef(aName, true);
  }
  public String cDef(String aName, boolean withBool)
  {
    String result = cName(withBool);
    if (reference == Type.BYPTR)
      result = result + "*";
    else if (reference == Type.BYREF)
      result = result + "&";
    else if (reference == Type.BYREFPTR)
      result = result + "*&";
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String cDefUbi(String aName)
  {
    String result = cNameUbi();
    if (reference == Type.BYPTR)
      result = result + "*";
    else if (reference == Type.BYREF)
      result = result + "&";
    else if (reference == Type.BYREFPTR)
      result = result + "*&";
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer)arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String pyDefUbi(String aName)
  {
    String result = cNameUbi();
    result = result + " " + aName;
    return result;
  }
  public String netDef(String aName)
  {
    String result = cName(false);
    if (reference == Type.BYPTR)
      result = "ref " + result;
    else if (reference == Type.BYREF)
      result = "ref " + result;
    else if (reference == Type.BYREFPTR)
      result = "ref " + result + "[]";
    result = result + " " + aName;
    return result;
  }
  public String csAttribute(boolean isStruct)
  {
    String result = "";
    if (isStruct)
      return result;
    int size = 0;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      size = size + integer.intValue();
    }
    if (size > 0)
      result = "[Field(Size="+size+")] ";
    return result;
  }
  public String csStructDef(String fieldName, boolean isStruct)
  {
    String result;
    int arrStart = 0;
    switch(typeof)
    {
    case BOOLEAN:
      result = "int";
      break;
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
      {
        result = "string";
        arrStart = 1;
      }
      else
        result = "byte";
      break;
    case BYTE:
      result = "byte";
      break;
    case STRING:
      result = "string";
      arrStart = 1;
      break;
    case INT:
      result = "int";
      break;
    case SHORT:
      result = "short";
      break;
    case LONG:
      result = "long";
      break;
    case FLOAT:
    case DOUBLE:
      result = "double";
      break;
    default:
      result = name;
    }
    for (int i = arrStart; i < arraySizes.size(); i++)
      result = result + "[]";
    if (reference == Type.ARRAYED)
      result = result + "[]";
    result = result + " " + fieldName;
    if (isStruct)
    {
      result = result+" = new "+name;
      if (arrStart < arraySizes.size())
      {
        int size = 0;
        for (int i = arrStart; i < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(i);
          size += integer.intValue();
          result = result + "["+size+"]";
        }
      }
      else if (reference == Type.ARRAYED)
        result = result + "[0]";
      else
        result = result + "()";
    }
    return result;
  }
  public String csStructInit(String aName)
  {
    String result = aName;
    String work;
    switch(typeof)
    {
    case BOOLEAN:
      work = "int";
      break;
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
        work = "string";
      else
        work = "byte";
      break;
    case STRING:
      work = "string";
      break;
    case BYTE:
      work = "byte";
      break;
    case SHORT:
      work = "short";
      break;
    case LONG:
      work = "int";
      break;
    case INT:
      work = "int";
      break;
    case FLOAT:
    case DOUBLE:
      work = "double";
      break;
    default:
      work = name;
    }
    if (typeof == CHAR && arraySizes.size() > 0)
    {
      Integer int0 = (Integer) arraySizes.elementAt(0);
      if (arraySizes.size() == 1)
        result = result + " = \"\".PadRight("+int0.intValue()+")";
      else
      {
        work = "new " + work;
        for (int i = 1; i < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(i);
          work = work + "[" + integer.intValue() + "]";
        }
        result = result + " = " + work;
        String w2 = aName;
        for (int i = 1; i < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(i);
          String w = "i"+i;
          result = result + ";for (int "+w+"=0; "+w+"<"+integer+"; "+w+"++)";
          w2 = w2 + "[" + w + "]";
        }
        result = result + w2 + " = \"\".PadRight("+int0.intValue()+")";
      }
    }
    else if (arraySizes.size() > 0)
    {
      work = "new " + work;
      for (int i = 0; i < arraySizes.size(); i++)
      {
        Integer integer = (Integer) arraySizes.elementAt(i);
        work = work + "[" + integer.intValue() + "]";
      }
      result = result + " = " + work;
    }
    else
      result = "";
    return result;
  }
  public String javaStructDef(String aName)
  {
    String result;
    int arrStart = 0;
    switch(typeof)
    {
    case BOOLEAN:
      result = "boolean";
      break;
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
      {
        result = "String";
        arrStart = 1;
      }
      else
        result = "byte";
      break;
    case BYTE:
      result = "byte";
      break;
    case STRING:
      result = "String";
      break;
    case SHORT:
      result = "short";
      break;
    case INT:
      result = "int";
      break;
    case LONG:
      result = "long";
      break;
    case FLOAT:
    case DOUBLE:
      result = "double";
      break;
    default:
      result = name;
    }
    for (int i = arrStart; i < arraySizes.size(); i++)
      result = result + "[]";
    if (reference == Type.ARRAYED)
      result = result + "[]";
    result = result + " " + aName;
    return result;
  }
  public String binuName()
  {
    switch(typeof)
    {
    case BOOLEAN:
      return "boolean";
    case CHAR:
      if (reference == Type.BYPTR)
        return "String";
      else if (reference == Type.BYREFPTR || reference == Type.ARRAYED)
        return "byte";
      else
        return "byte";
    case BYTE:
      return "byte";
    case STRING:
      return "String";
    case SHORT:
      return "short";
    case INT:
      return "int";
    case LONG:
      return "long";
    case FLOAT:
    case DOUBLE:
      return "double";
    default:
      return name;
    }
  }
  public String binuStructDef(String aName)
  {
    String result;
    int arrStart = 0;
    switch(typeof)
    {
    case BOOLEAN:
      result = "boolean";
      break;
    case CHAR:
      if (reference == Type.BYPTR)
        result = "String";
      else if (arraySizes.size() > 0)
      {
        result = "byte";
        arrStart = 1;
      }
      else
        result = "byte";
      break;
    case BYTE:
      result = "byte";
      break;
    case STRING:
      result = "String";
      break;
    case SHORT:
      result = "short";
      break;
    case INT:
      result = "int";
      break;
    case LONG:
      result = "long";
      break;
    case FLOAT:
    case DOUBLE:
      result = "double";
      break;
    default:
      result = name;
    }
    for (int i = arrStart; i < arraySizes.size(); i++)
      result = result + "[]";
    if (reference == Type.ARRAYED)
      result = result + "[]";
    result = result + " " + aName;
    return result;
  }
  public String javaStructInit(String aName)
  {
    String result = aName;
    String work;
    switch(typeof)
    {
    case BOOLEAN:
      work = "boolean";
      break;
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
        work = "String";
      else
        work = "byte";
      break;
    case BYTE:
      work = "byte";
      break;
    case STRING:
      work = "String";
      break;
    case SHORT:
      work = "short";
      break;
    case INT:
      work = "int";
      break;
    case LONG:
      work = "long";
      break;
    case FLOAT:
    case DOUBLE:
      work = "double";
      break;
    default:
      work = name;
    }
    if (typeof == CHAR && arraySizes.size() > 0)
    {
      Integer int0 = (Integer) arraySizes.elementAt(0);
      if (arraySizes.size() == 1)
        result = result + " = \"\".PadRight("+int0.intValue()+")";
      else
      {
        work = "new " + work;
        for (int i = 1; i < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(i);
          work = work + "[" + integer.intValue() + "]";
        }
        result = result + " = " + work;
        String w2 = aName;
        for (int i = 1; i < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(i);
          String w = "i"+i;
          result = result + ";for (int "+w+"=0; "+w+"<"+integer+"; "+w+"++)";
          w2 = w2 + "[" + w + "]";
        }
        result = result + w2 + " = \"\".PadRight("+int0.intValue()+")";
      }
    }
    else if (arraySizes.size() > 0)
    {
      work = "new " + work;
      for (int i = 0; i < arraySizes.size(); i++)
      {
        Integer integer = (Integer) arraySizes.elementAt(i);
        work = work + "[" + integer.intValue() + "]";
      }
      result = result + " = " + work;
    }
    else if (reference == Type.ARRAYED)
      result = result + " = " + work + "[0]";
    else
      result = "";
    return result;
  }
  public String cDefRefPAsP(String aName, boolean withBool)
  {
    String result = cName(withBool);
    if (reference == Type.BYPTR || reference == Type.BYREFPTR)
      result = result + "*";
    else if (reference == Type.BYREF)
      result = result + "&";
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String cDefUbiRefPAsP(String aName)
  {
    String result = cNameUbi();
    if (reference == Type.BYPTR || reference == Type.BYREFPTR)
      result = result + "*";
    else if (reference == Type.BYREF)
      result = result + "&";
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer)arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String cDefNoRef(String aName)
  {
    return cDefNoRef(aName, true);
  }
  public String cDefNoRef(String aName, boolean withBool)
  {
    String result = cName(withBool);
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String cDefUbiNoRef(String aName)
  {
    String result = cNameUbi();
    result = result + " " + aName;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer)arraySizes.elementAt(i);
      result = result + "[" + integer.intValue() + "]";
    }
    return result;
  }
  public String cName()
  {
    return cName(true);
  }
  public String cName(boolean withBool)
  {
    String sign = "";
    if (isUnsigned)
      sign = "unsigned ";

    switch(typeof)
    {
    case BOOLEAN:
      if (withBool)
        return "bool";
      else
        return "int";
    case BYTE:
      return sign+"char";
    case CHAR:
      return sign+"char";
    case SHORT:
      return sign+"short";
    case INT:
      return sign+"int";
    case LONG:
      return sign+"long";
    case FLOAT:
    case DOUBLE:
      return "double";
    default:
      return name;
    }
  }
  public String objcName()
  {
    String sign = "";
    if (isUnsigned)
      sign = "unsigned ";
    switch (typeof)
    {
      case BOOLEAN:
        return "bool";
      case BYTE:
        return sign + "char";
      case CHAR:
        return sign + "char";
      case SHORT:
        return sign + "short";
      case INT:
        return sign + "int";
      case LONG:
        return sign + "long";
      case FLOAT:
      case DOUBLE:
        return "double";
      default:
        return name;
    }
  }
  public String cNameUbi()
  {
    String sign = "";
    if (isUnsigned)
      sign = "u";
    switch (typeof)
    {
      case BOOLEAN:
        return sign + "int32";
      case BYTE:
        return sign + "int8";
      case CHAR:
        return sign + "char";
      case SHORT:
        return sign + "int16";
      case INT:
        return sign + "int32";
      case LONG:
        return sign + "int64";
      case FLOAT:
      case DOUBLE:
        return "double";
      default:
        return name;
    }
  }
  public String netName()
  {
    String sign = "";
    if (isUnsigned)
      sign = "u";
    switch(typeof)
    {
    case BOOLEAN:
      return "int";
    case BYTE:
      return "byte";
    case CHAR:
      return sign+"char";
    case STRING:
      return "string";
    case SHORT:
      return sign+"short";
    case INT:
      return sign + "int";
    case LONG:
      return sign + "long";
    case FLOAT:
    case DOUBLE:
      return "double";
    default:
      return name;
    }
  }
  public String javaName()
  {
    switch(typeof)
    {
    case BOOLEAN:
      return "boolean";
    case CHAR:
      if (arraySizes.size() > 0 || reference == Type.BYPTR)
        return "String";
      else
        return "byte";
    case BYTE:
      return "byte";
    case STRING:
      return "String";
    case SHORT:
      return "short";
    case INT:
      return "int";
    case LONG:
      return "long";
    case FLOAT:
    case DOUBLE:
      return "double";
    default:
      return name;
    }
  }
  public String basDef(String aName)
  {
    return basDef(aName, true);
  }
  public String basDef(String aName, boolean withBool)
  {
    String result;
    if (typeof == CHAR)
    {
      String w = " (";
      result = aName;
      if (arraySizes.size() > 0)
      {
        int i;
        for (i = 0; (i+1) < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(0);
          result = result + w + "0 to " + (integer.intValue()-1);
          w = ", ";
          if (i+2 == arraySizes.size())
            result = result + ")";
        }
        result = result + basType(withBool);
        Integer integer = (Integer) arraySizes.elementAt(i);
        result = result + " * " + integer.intValue();
      }
      else
        result = result + basType(withBool);
    }
    else
    {
      String w = " (";
      result = aName;
      for (int i = 0; i < arraySizes.size(); i++)
      {
        Integer integer = (Integer) arraySizes.elementAt(i);
        result = result + w + "0 to " + (integer.intValue()-1);
        w = ", ";
        if (i+1 == arraySizes.size())
          result = result + ")";
      }
      result = result + basType(withBool);
    }
    return result;
  }
  public String basType()
  {
    return basType(true);
  }
  public String basType(boolean withBool)
  {
    switch(typeof)
    {
    case BOOLEAN:
      if (withBool)
        return " As Boolean";
      else
        return " As Long";
    case BYTE:
      return " As Byte";
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
        return " As String";
      else
        return " As Byte";
    case SHORT:
      return " As Integer";
    case INT:
    case LONG:
      return " As Long";
    case FLOAT:
    case DOUBLE:
      return " As Double";
    default:
      return " As " + name;
    }
  }
  public String basNetDef(String aName)
  {
    return basNetDef(aName, true);
  }
  public String basNetDef(String aName, boolean withBool)
  {
    String result;
    if (typeof == CHAR)
    {
      String w = " (";
      result = aName;
      if (arraySizes.size() > 0)
      {
        int i;
        for (i = 0; (i+1) < arraySizes.size(); i++)
        {
          Integer integer = (Integer) arraySizes.elementAt(0);
          result = result + w + "0 to " + (integer.intValue()-1);
          w = ", ";
          if (i+2 == arraySizes.size())
            result = result + ")";
        }
        result = result + basNetType(withBool);
      }
      else
        result = result + basNetType(withBool);
    }
    else
    {
      String w = " (";
      result = aName;
      for (int i = 0; i < arraySizes.size(); i++)
      {
        Integer integer = (Integer) arraySizes.elementAt(i);
        result = result + w + "0 to " + (integer.intValue()-1);
        w = ", ";
        if (i+1 == arraySizes.size())
          result = result + ")";
      }
      result = result + basNetType(withBool);
    }
    return result;
  }
  public String basNetType()
  {
    return basNetType(true);
  }
  public String basNetType(boolean withBool)
  {
    switch(typeof)
    {
    case BOOLEAN:
      if (withBool)
        return " As Boolean";
      else
        return " As Integer";
    case BYTE:
      return " As Byte";
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
        return " As String";
      else
        return " As Byte";
    case SHORT:
      return " As Short";
    case INT:
    case LONG:
      return " As Integer";
    case FLOAT:
    case DOUBLE:
      return " As Double";
    default:
      return " As " + name;
    }
  }
  public String csNetType()
  {
    switch(typeof)
    {
    case BOOLEAN:
      return "bool";
    case BYTE:
      return "byte";
    case CHAR:
      if (arraySizes.size() > 0
        || reference == Type.BYPTR)
        return " As String";
      else
        return " As Byte";
    case STRING:
      return "string";
    case SHORT:
      return "short";
    case INT:
      return "int";
    case LONG:
      return "long";
    case FLOAT:
    case DOUBLE:
      return "double";
    default:
      return name;
    }
  }
  public int dotNetSize()
  {
    int result;
    int n = 1;
    for (int i = 0; i < arraySizes.size(); i++)
    {
      Integer integer = (Integer) arraySizes.elementAt(i);
      n *= integer.intValue();
    }
    switch(typeof)
    {
    case USERTYPE:
      result = n*4;
      break;
    case BOOLEAN:
      result = n*4;
      break;
    case BYTE:
    case CHAR:
    case STRING:
      result = 4;
      break;
    case SHORT:
      result = n*2;
      break;
    case INT:
      result = n*4;
      break;
    case LONG:
      result = n*8;
      break;
    case FLOAT:
    case DOUBLE:
      result = n*8;
      break;
    case VOID:
      result = n*4;
      break;
    default:
      result = n*4;
      break;
    }
    return result;
  }
}

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

package vlab.jportal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

/**
* This holds the field definition. It also supplies methods for the
* Java format and various SQL formats.
*/
public class Field implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /** Name to use in the database */
  public String name;
  /** Name to use in the class if not present (normal case) then name is used */
  public String alias;
  /** Default value to apply for the field on insert if not specified */
  public String defaultValue;
  /** Check Constraint to applied to field by the database */
  public String checkValue;
  /** Type of field */
  public byte type;
  /** Length of field */
  public int length;
  /** No of digits in a numeric field */
  public int precision;
  /** No of digits after the decimal point|comma */
  public int scale;
  public int bindPos;
  public int definePos;
  /** Array of comments associated with the field */
  public Vector<String> comments;
  public Vector<Enum> enums;
  public Vector<String> valueList;
  public String enumLink;
  /** Indicates field is used in the primary key */
  public boolean isPrimaryKey;
  /** Indicates the field is a Sequence */
  public boolean isSequence;
  /** Indicates the field can be NULL on the database */
  public boolean isNull;
  /** Indicates the field is INPUT */
  public boolean isIn;
  /** Indicates the field is OUTPUT */
  public boolean isOut;
  public static final byte
    BLOB       = 1
  , BOOLEAN    = 2
  , BYTE       = 3
  , CHAR       = 4
  , DATE       = 5
  , DATETIME   = 6
  , DOUBLE     = 7
  , DYNAMIC    = 8
  , FLOAT      = 9
  , IDENTITY   = 10
  , INT        = 11
  , LONG       = 12
  , MONEY      = 13
  , SEQUENCE   = 14
  , SHORT      = 15
  , STATUS     = 16
  , TIME       = 17
  , TIMESTAMP  = 18
  , TLOB       = 19
  , USERSTAMP  = 20
  , ANSICHAR   = 21
  , UID        = 22
  , XML        = 23
  , BIGSEQUENCE = 24
  , BIGIDENTITY = 25
  , AUTOTIMESTAMP = 26
  , WCHAR      = 27
  , WANSICHAR  = 28
  , UTF8       = 29   
  , BIGXML     = 30
  ;
  public static final int DEFAULT_XML = 4096;
  public static final int DEFAULT_BIG_XML = 4194304;
  /** constructor ensures fields have correct default values */
  public Field()
  {
    name = "";
    alias = "";
    checkValue = "";
    defaultValue = "";
    enumLink = "";
    type = 0;
    length = 0;
    precision = 0;
    scale = 0;
    bindPos = 0;
    definePos = 0;
    comments = new Vector<String>();
    enums = new Vector<Enum>();
    valueList = new Vector<String>();
    isPrimaryKey = false;
    isSequence = false;
    isNull = false;
    isIn = false;
    isOut = false;
  }
  public void reader(DataInputStream ids) throws IOException
  {
    name = ids.readUTF();
    alias = ids.readUTF();
    checkValue = ids.readUTF();
    defaultValue = ids.readUTF();
    enumLink = ids.readUTF();
    type = ids.readByte();
    length = ids.readInt();
    precision = ids.readInt();
    scale = ids.readInt();
    bindPos = ids.readInt();
    definePos = ids.readInt();
    int noOf = ids.readInt();
    for (int i=0; i<noOf;i++)
    {
      String value = ids.readUTF();
      comments.addElement(value);
    }
    noOf = ids.readInt();
    for (int i=0; i<noOf;i++)
    {
      Enum value = new Enum();
      value.reader(ids);
      enums.addElement(value);
    }
    noOf = ids.readInt();
    for (int i=0; i<noOf;i++)
    {
      String value = ids.readUTF();
      valueList.addElement(value);
    }
    isPrimaryKey = ids.readBoolean();
    isSequence = ids.readBoolean();
    isNull = ids.readBoolean();
    isIn = ids.readBoolean();
    isOut = ids.readBoolean();
  }
  public void writer(DataOutputStream ods) throws IOException
  {
    ods.writeUTF(name);
    ods.writeUTF(alias);
    ods.writeUTF(checkValue);
    ods.writeUTF(defaultValue);
    ods.writeUTF(enumLink);
    ods.writeByte(type);
    ods.writeInt(length);
    ods.writeInt(precision);
    ods.writeInt(scale);
    ods.writeInt(bindPos);
    ods.writeInt(definePos);
    ods.writeInt(comments.size());
    for (int i=0; i<comments.size(); i++)
    {
      String value = (String) comments.elementAt(i);
      ods.writeUTF(value);
    }
    ods.writeInt(enums.size());
    for (int i=0; i<enums.size(); i++)
    {
      Enum value = (Enum) enums.elementAt(i);
      value.writer(ods);
    }
    ods.writeInt(valueList.size());
    for (int i=0; i<valueList.size(); i++)
    {
      String value = (String) valueList.elementAt(i);
      ods.writeUTF(value);
    }
    ods.writeBoolean(isPrimaryKey);
    ods.writeBoolean(isSequence);
    ods.writeBoolean(isNull);
    ods.writeBoolean(isIn);
    ods.writeBoolean(isOut);
  }
  /** If there is an alias uses that else returns name */
  public String useName()
  {
    if (alias.length() > 0)
      return alias;
    return name;
  }
  /** If there is an alias uses that else returns name */
  public String useLowerName()
  {
    String n = useName();
    String f = n.substring(0, 1);
    return f.toLowerCase()+n.substring(1);
  }
  /** If there is an alias uses that else returns name */
  public String useUpperName()
  {
    String n = useName();
    String f = n.substring(0, 1);
    return f.toUpperCase()+n.substring(1);
  }
  /**
   * Check for empty string as null type fields.
   */
  public boolean isEmptyAsNull()
  {
    if (isNull == false) return false;
    switch(type)
    {
      case ANSICHAR: if (length == 1) break;
      case CHAR:
      case DATE:
      case DATETIME:
      case TIME:
        return true;
    }
    return false;
  }
  /**
   * Check for empty string as null type fields.
   */
  public boolean isCharEmptyAsNull()
  {
    if (isNull == false) return false;
    switch (type)
    {
      case ANSICHAR: if (length == 1) break;
      case CHAR:
      case TLOB:
        return true;
    }
    return false;
  }
  /**
   * Check for empty string as null type fields.
   */
  public boolean ansiIsNull()
  {
    if (isNull == false) return false;
    return type == ANSICHAR && length == 1;
  }
  public boolean isEmptyOrAnsiAsNull()
  {
    return isEmptyAsNull() || ansiIsNull();
  }
  public boolean isCharEmptyOrAnsiAsNull()
  {
    return isCharEmptyAsNull() || ansiIsNull();
  }
}


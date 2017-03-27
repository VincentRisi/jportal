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
package bbd.pickle;

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
	private static final long serialVersionUID = -4803546822591706167L;
  public String  name;
  public String  alias;
  public String  check;
  public byte    type;
  public int     length;
  public int     precision;
  public int     scale;
  public boolean isNull;
  public boolean isUppercase;
  public Vector<String>  comments;
  public Vector<Enum>  enums;
  public static final byte
    BIGSEQUENCE = 2
  , BOOLEAN     = 4
  , BYTE        = 8
  , CHAR        = 12
  , DATE        = 16
  , DATETIME    = 20
  , DOUBLE      = 24
  , INT         = 28
  , LONG        = 32
  , SEQUENCE    = 36
  , SHORT       = 40
  , TIME        = 44
  , TIMESTAMP   = 48
  , USERSTAMP   = 52
  ;
  /** constructor ensures fields have correct default values */
  public Field()
  {
    name = "";
    alias = "";
    check = "";
    type = 0;
    length = 0;
    precision = 0;
    scale = 0;
    isNull = false;
    isUppercase = false;
    comments = new Vector<String>();
    enums = new Vector<Enum>();
  }
  /** If there is an alias uses that else returns name */
  public String useName()
  {
    if (alias.length() > 0)
      return alias;
    return name;
  }
}



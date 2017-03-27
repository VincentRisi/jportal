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
* Table identified by name holds fields, keys, links, grants, views and procedures
* associated with the table.
*/
public class Table implements Serializable
{
  private static final long serialVersionUID = 2812317577367653227L;
  public Application application;
  public String      name;
  public String      descr;
  public String      alias;
  public String      check;
  public Vector<Field>      fields;
  public Vector<String>      comments;
  public Vector<String>      options;
  public Vector<Field>      order;
  public Vector<Field>      show;
  public Vector<Field>      breaks;
  public Vector<Value>      values;
  public Vector<Link>      links;
  public Vector<Key>      keys;
  public Validation  validation;
  public boolean     useSequence;
  public boolean     useBigSequence;
  public boolean     useChar;
  public boolean     noDomain;
  public boolean     viewOnly;
  public boolean     isNullable;
  public Table()
  {
    name              = "";
    descr             = "";
    alias             = "";
    check             = "";
    fields            = new Vector<Field>();
    comments          = new Vector<String>();
    options           = new Vector<String>();
    order             = new Vector<Field>();
    show              = new Vector<Field>();
	  breaks            = new Vector<Field>();
    values            = new Vector<Value>();
    links             = new Vector<Link>();
    keys              = new Vector<Key>();
    validation        = new Validation();
    useSequence       = false;
    useBigSequence    = false;
    useChar           = false;
    noDomain          = false;
    viewOnly          = false;
    isNullable        = false;
  }
  /**
  * If there is an alias uses that else returns name
  */
  public String useName()
  {
    if (alias.length() > 0)
      return alias;
    return name;
  }
  /**
  * Checks for the existence of a field
  */
  public boolean hasField(String s)
  {
    int i;
    for (i=0; i<fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      if (field.name.equalsIgnoreCase(s))
        return true;
    }
    return false;
  }
  /**
  * Checks for the existence of a field
  */
  public Field getField(String s)
  {
    int i;
    for (i=0; i<fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      if (field.name.equalsIgnoreCase(s))
        return field;
    }
    return null;
  }
  /**
  * Checks for the existence of a field
  */
  public Field getPKField(int no)
  {
    int i;
    for (i=0; i<keys.size(); i++)
    {
      Key key = (Key) keys.elementAt(i);
      if (key.primary != true)
        continue;
      if (no < key.list.size())
        return (Field) key.list.elementAt(no);
      break;
    }
    return null;
  }
  /**
  * Checks for the existence of a field
  */
  public int getFieldNo(String s)
  {
    int i;
    for (i=0; i<fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      if (field.name.equalsIgnoreCase(s))
        return i;
    }
    return -1;
  }
  /**
  * Checks for the existence of a field
  */
  public int getOrderNo(int n)
  {
    if (order.size() <= n)
      return -1;
    Field field = (Field) order.elementAt(n);
    return getFieldNo(field.name);
  }
}



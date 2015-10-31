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
* Foreign keys used in database
*/
public class Link implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  /** Name of foreign table */
  public String name;
  public String linkName;
  public Vector<String> fields;
  public Vector<String> linkFields;
  public boolean isDeleteCascade;
  public Link()
  {
    name      = "";
    linkName = "";
    fields    = new Vector<String>();
    linkFields = new Vector<String>();
    isDeleteCascade = false;
  }
  public void reader(DataInputStream ids) throws IOException
  {
    name = ids.readUTF();
    linkName = ids.readUTF();
    int noOf = ids.readInt();
    for (int i=0; i<noOf; i++)
    {
      String value = ids.readUTF();
      fields.addElement(value);
    }
    noOf = ids.readInt();
    for (int i=0; i<noOf; i++)
    {
      String value = ids.readUTF();
      linkFields.addElement(value);
    }
    isDeleteCascade = ids.readBoolean();
  }
  public void writer(DataOutputStream ods) throws IOException
  {
    ods.writeUTF(name);
    ods.writeUTF(linkName);
    ods.writeInt(fields.size());
    for (int i=0; i<fields.size(); i++)
    {
      String value = (String) fields.elementAt(i);
      ods.writeUTF(value);
    }
    for (int i=0; i<linkFields.size(); i++)
    {
      String value = (String) linkFields.elementAt(i);
      ods.writeUTF(value);
    }
    ods.writeBoolean(isDeleteCascade);
  }
  public boolean hasField(String s)
  {
    int i;
    for (i=0; i<fields.size(); i++)
    {
      String name = (String) fields.elementAt(i);
      if (name.equalsIgnoreCase(s))
        return true;
    }
    return false;
  }
}




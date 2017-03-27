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

public class Key implements Serializable
{
  private static final long serialVersionUID = -1746201965095963973L;
  public String name;
  public Vector<Field> list;
  public boolean primary;
  public boolean unique;
  public Key()
  {
    name = "";
    primary = false;
    unique = false;
    list  = new Vector<Field>();
  }
}


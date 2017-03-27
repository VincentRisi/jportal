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
public class Enumerator implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public String             name;
  public Vector<String>    elements;
  public Enumerator()
  {
    name = "";
    elements = new Vector<String>();
  }
}

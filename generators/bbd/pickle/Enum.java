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

/**
* This holds the field definition. It also supplies methods for the
* Java format and various SQL formats.
*/
public class Enum implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
  public String  name;
  public int     value;
  public Enum()
  {
    name = "";
    value = 0;
  }
}

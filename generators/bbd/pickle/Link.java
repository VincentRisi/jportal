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

public class Link implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -3908671489770159128L;
public Table table;
  public Vector<String> list;
  public boolean hasCascade;
  public Link()
  {
    table = null;
    list  = new Vector<String>();
    hasCascade = false;
  }
}


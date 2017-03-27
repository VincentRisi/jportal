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
public class Operation implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final byte
      SIZE = 1
  ,   DYNAMIC = 2
  ;
  public String name;
  public byte code;
  public boolean isConstant;
  public Field field;
  public Operation()
  {
    code = 0;
    name = "";
    isConstant = false;
    field = null;
  }
}

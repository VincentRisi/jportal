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
 * The only operation that is currently implemented is size
 */
public class Action implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public String name;
  public Vector<Operation> operations;
  public Action()
  {
    name = "";
    operations = new Vector<Operation>();
  }
  public void addOperation(Operation operation)
  {
    operations.addElement(operation);
  }
  public boolean hasParameter(Prototype prototype)
  {
    Field parameter;
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      parameter = (Field) prototype.parameters.elementAt(i);
      if (parameter.name.compareTo(name) == 0) return true;
    }
    return false;
  }
  public Field getParameter(Prototype prototype)
  {
    Field parameter;
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      parameter = (Field) prototype.parameters.elementAt(i);
      if (parameter.name.compareTo(name) == 0) return parameter;
    }
    return null;
  }
  public boolean hasSize()
  {
    for (int i = 0; i < operations.size(); i++)
    {
      Operation op = (Operation) operations.elementAt(i);
      if (op.code == Operation.SIZE) return true;
    }
    return false;
  }
  public String getSizeName()
  {
    for (int i = 0; i < operations.size(); i++)
    {
      Operation op = (Operation) operations.elementAt(i);
      if (op.code == Operation.SIZE) return op.name;
    }
    return "";
  }
  public Operation sizeOperation()
  {
    for (int i = 0; i < operations.size(); i++)
    {
      Operation op = (Operation) operations.elementAt(i);
      if (op.code == Operation.SIZE) return op;
    }
    return null;
  }
}
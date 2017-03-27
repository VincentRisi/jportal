/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;

namespace bbd.idl2
{
  /// <summary>
  /// $Revision: 401.3 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public class IDL2Exception : ApplicationException
  {
    public IDL2Exception() : base() { }
    public IDL2Exception(string message) : base(message) { }
    public IDL2Exception(string message, Exception inner) : base(message, inner) { }
  }
  [AttributeUsage(AttributeTargets.Field)]
  public class FieldAttribute : System.Attribute
  {
    private int size, pos;
    public int Size { get { return size; } set { size = value; } }
    public int Pos { get { return pos; } set { pos = value+1; } }
    public FieldAttribute()
    { }
  }
}

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
using System.Collections.Generic;
using System.Text;

namespace bbd.idl2
{
  public class IDL2Exception : ApplicationException
  {
    public IDL2Exception() : base() { }
    public IDL2Exception(string message) : base(message) { }
    public IDL2Exception(string message, Exception inner) : base(message, inner) { }
  }
  public class FieldAttribute : System.Attribute
  {
    private int size, type, pad;
    public int Size { get { return size; } set { size = value; } }
    public int Type { get { return type; } set { type = value; } }
    public int Pad { get { return pad; } set { pad = value; } }
    public FieldAttribute()
    { }
  }
}
namespace Bbd.ScreenBuilderFramework
{
  class FieldMaskAttribute : System.Attribute
  {
    private bool readOnly;
    public bool IsReadOnly { get { return readOnly; } set { readOnly = value; } }
  }
}

using System;
using System.Collections.Generic;
using System.Text;

namespace Bbd.Idl2
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

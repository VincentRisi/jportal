using System;

namespace Bbd.Idl2
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

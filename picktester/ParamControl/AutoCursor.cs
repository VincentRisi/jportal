using System;
using System.Windows.Forms;

namespace bbd.ParamControl
{
  public class AutoCursor : IDisposable
  {
    private Cursor save;
    public static AutoCursor Hourglass    { get { return new AutoCursor(Cursors.WaitCursor); } }
    public static AutoCursor AppStart     { get { return new AutoCursor(Cursors.AppStarting); } }
    public static AutoCursor DatabaseCall { get { return new AutoCursor(Cursors.Hand); } }
    private AutoCursor(Cursor value)
    {
      save = Cursor.Current;
      Cursor.Current = value;
    }
    public void Dispose()
    {
      Cursor.Current = save;
    }
  }
}

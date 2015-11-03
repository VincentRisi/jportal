using System;
using System.Windows.Forms;

namespace Bbd.Utility
{
  public class UCursor : IDisposable
  {
    Cursor save;
    public UCursor(Cursor newOne)
    {
      save = Cursor.Current;
      Cursor.Current = newOne;
    }
    public UCursor() : this(Cursors.WaitCursor) {}
    ~UCursor()
    {
      Dispose();
    }
    public void Dispose()
    {
      if (save != null)
        Cursor.Current = save;
      save = null;
    }
  }
}

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
using System.Windows.Forms;

namespace bbd.utility
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

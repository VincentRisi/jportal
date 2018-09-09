/// ------------------------------------------------------------------
/// Copyright (c) 1996, 2004 Vincent Risi in Association 
///                          with Barone Budge and Dominick 
/// All rights reserved. 
/// This program and the accompanying materials are made available 
/// under the terms of the Common Public License v1.0 
/// which accompanies this distribution and is available at 
/// http://www.eclipse.org/legal/cpl-v10.html 
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// $Date: 2004/11/17 08:50:33 $
/// $Revision: 1.1 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.Drawing;
using System.Collections;
using System.ComponentModel;
using System.Windows.Forms;
using System.Data;
using System.Text;
using System.IO;
using bbd.jportal;
using System.Reflection;
using System.Diagnostics;
using ICSharpCode.TextEditor;
using ICSharpCode.TextEditor.Document;

namespace Bbd.AnyDB
{
	public class ViewerSet : EditSetBase
	{
    internal Target target;
    public ViewerSet(IDEForm form, Target target, ContextMenu popupMenu, TabControl viewTab, ref int no)
      : base(form, viewTab, target.ToString("S"), popupMenu, ref no)
    {
      this.target = target;
      editor.Document.ReadOnly = true;
      tabPage.Controls.Add(editor);
      tabPage.Tag = this;
      if (new FileInfo(target.FileName).Exists)
        LoadFromFile(target.FileName);
    }
    protected override void WatcherChanged(object sender, FileSystemEventArgs e)
    {
      watcher.EnableRaisingEvents = false;
      form.LogInfo = e.FullPath+" changed in viewer";
    }
    protected override void Enter(object sender, EventArgs e)
    {
      form.LastViewer = editor;
    }
  }
}

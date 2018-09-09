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
using System.IO;
using System.Windows.Forms;
using System.Drawing;
using ICSharpCode.TextEditor;
using ICSharpCode.TextEditor.Document;

namespace Bbd.AnyDB
{
	/// <summary>
	/// Summary description for EditSetBase.
	/// </summary>
  public abstract class EditSetBase
  {
    internal TextEditorControl editor;
    internal IDEForm form;
    internal TabControl tabControl;
    internal TabPage tabPage;
    internal ContextMenu popupMenu;
    internal FileSystemWatcher watcher;
    public EditSetBase(IDEForm form, TabControl tabControl, string name, ContextMenu popupMenu, ref int no)
    {
      this.form = form;
      this.tabControl = tabControl;
      this.popupMenu = popupMenu;
      tabPage = new TabPage(name);
      tabControl.Controls.Add(tabPage);
      no = tabControl.TabCount-1;
      editor = new TextEditorControl();
      editor.AllowDrop = false;
      editor.Dock = DockStyle.Fill;
      editor.ShowEOLMarkers = false;
      editor.ShowInvalidLines = false;
      editor.ShowSpaces = false;
      editor.ShowTabs = true;
      editor.ConvertTabsToSpaces = true;
      editor.IndentStyle = IndentStyle.Smart;
      editor.TabIndent = 2;
      editor.ContextMenu = popupMenu;
      editor.Tag = this;
      editor.ActiveTextAreaControl.Caret.PositionChanged += new EventHandler(CaretPositionChanged);
      editor.Enter += new EventHandler(Enter);
    }
    protected abstract void WatcherChanged(object sender, FileSystemEventArgs e);
    public void LoadFromFile(string fileName)
    {
      TextArea area = editor.ActiveTextAreaControl.TextArea;
      editor.Document.HighlightingStrategy = HighlightingStrategyFactory.CreateHighlightingStrategyForFile(fileName);
      try
      {
        using (StreamReader sr = new StreamReader(fileName))
        {
          editor.Document.TextContent = sr.ReadToEnd();
          sr.Close();
        }
        area.Caret.Position = new Point(0,0);
        if (watcher == null)
        {
          watcher = new FileSystemWatcher();
          watcher.NotifyFilter = System.IO.NotifyFilters.LastWrite;
          FileInfo info = new FileInfo(fileName);
          watcher.Path = info.DirectoryName;
          watcher.Filter = info.Name;
          watcher.Changed +=new FileSystemEventHandler(WatcherChanged);
        }
        watcher.EnableRaisingEvents = true;
      }
      catch (Exception ex)
      {
        form.LogError = ex.Message;
      }
    }
    public void SaveToFile(string fileName)
    {
      if (watcher != null)
        watcher.EnableRaisingEvents = false;
      using (StreamWriter sw = new StreamWriter(fileName))
      {
        sw.Write(editor.Document.TextContent);
        sw.Close();
      }
      if (watcher != null)
        watcher.EnableRaisingEvents = true;
    }
    private void CaretPositionChanged(object sender, EventArgs e)
    {
      Caret caret = sender as Caret;
      form.PositionStatus = string.Format("{0}:{1}", caret.Line+1, caret.Column+1);
    }
    public event EventHandler Closing;
    protected abstract void Enter(object sender, EventArgs e);
    internal virtual void Close()
    {
      editor.ActiveTextAreaControl.Caret.PositionChanged -= new EventHandler(CaretPositionChanged);
      editor.Enter -= new EventHandler(Enter);
      if (Closing != null)
        Closing(this, new EventArgs());
    }
    public int RationalStart(int no, int gap)
    {
      no -= gap;
      return no > 0 ? no : 0;
    }
    public void SetEditorActive()
    {
      TabPage page = tabControl.Parent as TabPage;
      if (page != null)
      {
        TabControl main = page.Parent as TabControl;
        if (main != null)
          main.SelectedTab = page;
      }
      tabControl.SelectedTab = tabPage;
      editor.Focus();
      editor.Refresh();
    }
  }
}

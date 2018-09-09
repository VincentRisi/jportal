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
  /// <summary>
  /// Summary description for EditorSet.
  /// </summary>
  public class EditorSet : EditSetBase
  {
    internal Panel topPanel;
    internal CodeCompletion code;
    internal Panel bottomPanel;
    internal Splitter splitter;
    internal ComboBox tableCombo;
    internal ComboBox procsCombo;
    internal Source source;
    internal EditorSet(IDEForm form, Source source, ContextMenu popupMenu, TabControl editTab, ref int no)
      : base(form, editTab, source.ToString("S"), popupMenu, ref no)
    {
      this.source = source;
      topPanel = new Panel();
      topPanel.Dock = DockStyle.Top;
      topPanel.Height = 30;
      bottomPanel = new Panel();
      bottomPanel.Dock = DockStyle.Bottom;
      bottomPanel.Height = 120;
      bottomPanel.Visible = false;
      splitter = new Splitter();
      splitter.Dock = DockStyle.Bottom;
      splitter.Visible = false;
      tableCombo = new ComboBox();
      tableCombo.DropDownStyle = ComboBoxStyle.DropDownList;
      tableCombo.MaxDropDownItems = 20;
      tableCombo.Top = 4;
      // The anchor right enforces a size change as a side effect
      tableCombo.Anchor = AnchorStyles.Left | AnchorStyles.Right;
      tableCombo.SizeChanged += new EventHandler(TableComboSizeChanged);
      tableCombo.Click += new EventHandler(TableComboClick);
      topPanel.Controls.Add(tableCombo);
      procsCombo = new ComboBox();
      procsCombo.DropDownStyle = ComboBoxStyle.DropDownList;
      procsCombo.MaxDropDownItems = 20;
      procsCombo.Top = 4;
      // The anchor right enforces a size change as a side effect
      procsCombo.Anchor = AnchorStyles.Left | AnchorStyles.Right;
      procsCombo.SizeChanged += new EventHandler(ProcsComboSizeChanged); 
      procsCombo.Click += new EventHandler(ProcsComboClick);
      topPanel.Controls.Add(procsCombo);
      //editor.Document.HighlightingStrategy = HighlightingStrategyFactory.CreateHighlightingStrategyForFile(source.FileName);
      code = new CodeCompletion(form, editor);
      editor.Document.DocumentChanged += new DocumentEventHandler(EditorDocumentChanged);
      tabPage.Controls.Add(editor);
      tabPage.Controls.Add(splitter);
      tabPage.Controls.Add(bottomPanel);
      tabPage.Controls.Add(topPanel);
      tabPage.Tag = this;
      bool setupNew = true;
      if (new FileInfo(source.FileName).Exists)
      {
        setupNew = false;
        if (source.Exists == false)
        {
          DialogResult rc = MessageBox.Show(string.Format("The file {0} already exists, would you like to keep the existing file?"
            , source.FileName)
            , "Add Source"
            , MessageBoxButtons.YesNo);
          if (rc == DialogResult.No)
            setupNew = true;
        }
        if (setupNew == false)
          LoadFromFile(source.FileName);
      }
      if (setupNew == true)
        FillInNew(source.ToString("S"));
      source.Modified = false;     
      if (code.Run())
      {
        TableLoadCombo(code.GetTables());
        Table table = tableCombo.SelectedItem as Table;
        if (table != null)
          ProcsLoadCombo(code.GetProcs(table));
      }
      tableCombo.SelectedIndexChanged += new EventHandler(TableComboSelectedIndexChanged);
      procsCombo.SelectedIndexChanged += new EventHandler(ProcsComboSelectedIndexChanged);
    }
    internal void TableLoadCombo(SortedList list)
    {
      tableCombo.SelectedIndex = -1;
      tableCombo.DataSource = list.GetValueList();
    }
    internal void TableComboSizeChanged(object sender, EventArgs e)
    {
      tableCombo.Width = tabPage.Width / 2 - 12;
      tableCombo.Left = 4;
    }
    internal void TableComboClick(object sender, EventArgs e)
    {
      if (code.Run())
        TableLoadCombo(code.GetTables());
    }
    internal void TableComboSelectedIndexChanged(object sender, EventArgs e)
    {
      int start = 0;
      Table table = tableCombo.SelectedItem as Table;
      bbd.jportal.View view = tableCombo.SelectedItem as bbd.jportal.View;
      if (table != null)
      {
        start = table.start;
        ProcsLoadCombo(code.GetProcs(table));
      }
      else if (view != null)
        start = view.start;
      TextArea area = editor.ActiveTextAreaControl.TextArea;
      Caret caret = editor.ActiveTextAreaControl.Caret;
      caret.Position = new Point(0, start-1);
      area.TextView.FirstVisibleLine = RationalStart(start, 8);
      SetEditorActive();
    }
    internal void ProcsLoadCombo(SortedList list)
    {
      procsCombo.SelectedIndex = -1;
      procsCombo.DataSource = list.GetValueList();
    }
    internal void ProcsComboSizeChanged(object sender, EventArgs e)
    {
      procsCombo.Width = tabPage.Width / 2 - 12;
      procsCombo.Left = 12 + procsCombo.Width;
    }
    internal void ProcsComboClick(object sender, EventArgs e)
    {
      if (code.Run() || procsCombo.Items.Count == 0)
      {
        Table table = tableCombo.SelectedItem as Table;
        if (table != null)
          ProcsLoadCombo(code.GetProcs(table));
      }
    }
    internal void ProcsComboSelectedIndexChanged(object sender, EventArgs e)
    {
      Proc proc = procsCombo.SelectedItem as Proc;
      if (proc != null)
      {
        TextArea area = editor.ActiveTextAreaControl.TextArea;
        Caret caret = editor.ActiveTextAreaControl.Caret;
        caret.Position = new Point(0, proc.start-1);
        area.TextView.FirstVisibleLine = RationalStart(proc.start, 8);
        SetEditorActive();
      }
    }
    internal void FillInNew(string name)
    {
      editor.Document.HighlightingStrategy = HighlightingStrategyFactory.CreateHighlightingStrategyForFile(source.FileName);
      TextArea area = editor.ActiveTextAreaControl.TextArea;
      area.InsertString("///\r\n");
      area.InsertString("/// Name:"+name+"\r\n");
      area.InsertString("///\r\n");
    }
    internal void CheckSave()
    {
      if (editor != null && source.Modified == true)
      {
        SaveToFile(source.FileName);        
        source.Modified = false;
        source.Exists = true;
      }
    }
    internal override void Close()
    {
      if (editor != null && source.Modified == true)
      {
        DialogResult rc = MessageBox.Show(source.FileName+" has changed. Do you wish to save?", "File has changed", MessageBoxButtons.YesNo);
        if (rc == DialogResult.Yes)
        {
          SaveToFile(source.FileName);
          source.Modified = false;
          source.Exists = true;
        }
      }
      editor.Document.DocumentChanged -= new DocumentEventHandler(EditorDocumentChanged);
      base.Close();
    }
    internal void Save()
    {
      if (source.Modified)
      {
        SaveToFile(source.FileName);        
        source.Modified = false;
        source.Exists = true;
      }
    }
    internal bool SaveAs(string fileName)
    {
      if (new FileInfo(fileName).Exists == true)
      {
        if (fileName.ToUpper().Equals(source.FileName.ToUpper()) == true)
        {
          Save();
          return false;
        }
        DialogResult rc = MessageBox.Show("The "+fileName+" already exists, do you wish to overwrite?", "Save File As", MessageBoxButtons.YesNo);
        if (rc == DialogResult.No)
          return false;
      }
      source.FileName = fileName;
      SaveToFile(source.FileName);        
      source.Modified = false;
      source.Exists = true;
      return true;
    }
    protected override void WatcherChanged(object sender, FileSystemEventArgs e)
    {
      form.LogInfo = e.FullPath+" changed in editor";
      watcher.EnableRaisingEvents = false;
      DialogResult rc = MessageBox.Show(source.FileName+" has been modified externally. Do you wish to reload?", "File Modified", MessageBoxButtons.YesNo);
      if (rc == DialogResult.Yes)
      {
        LoadFromFile(source.FileName);
        source.Modified = false;
      }
    }
    protected override void Enter(object sender, EventArgs e)
    {
      form.LastEditor = editor;
    }
    private void EditorDocumentChanged(object sender, DocumentEventArgs e)
    {
      source.Modified = true;
    }
  }
}


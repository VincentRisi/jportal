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
/// $Date: 2004/11/17 08:50:32 $
/// $Revision: 1.1 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.Collections;
using bbd.jportal;
using ICSharpCode.TextEditor;

namespace Bbd.AnyDB
{
	public class CodeCompletion
	{
    private string name;
    private Database database;
    private IDEForm form;
    private int hashCode;
    private TextEditorControl editor;
    public TextEditorControl Editor
    {
      get
      {
        return editor;
      }
    }
    public CodeCompletion(IDEForm form, TextEditorControl editor)
    {
      this.form = form;
      this.editor = editor;
      name = editor.Name;
      database = null;
      hashCode = 0;
		}
    public SortedList GetTables()
    {
      SortedList result = new SortedList();
      if (database != null)
      {
        for (int i=0; i<database.tables.size(); i++)
        {
          Table table = (Table) database.tables.elementAt(i);
          if (result.IndexOfKey(table.name) == -1)
            result.Add(table.name, table);
          else
            result.Add(table.name+" "+i.ToString(), table);
        }
        for (int i=0; i<database.views.size(); i++)
        {
          View view = (View) database.views.elementAt(i);
          if (result.IndexOfKey(view.name) == -1)
            result.Add(view.name, view);
          else
            result.Add(view.name+" "+i.ToString(), view);
        }
      }
      return result;
    }
    public SortedList GetProcs(Table table)
    {
      SortedList result = new SortedList();
      if (table != null)
      {
        for (int i=0; i<table.procs.size(); i++)
        {
          Proc proc = (Proc) table.procs.elementAt(i);
          if (proc.isData)
            continue;
          if (result.IndexOfKey(proc.name) == -1)
            result.Add(proc.name, proc);
          else
            result.Add(proc.name+" "+i.ToString(), proc);
        }
      }
      return result;
    }
    public bool Run()
    {
      string code = editor.Document.TextContent;
      // check if we already have compiled this code before
      int hashCode = code.GetHashCode();
      if (database != null && this.hashCode == hashCode)
        return false;
      this.hashCode = hashCode;
      java.io.StringReader reader = new java.io.StringReader(code);
      try
      {
        java.io.StringWriter writer = new java.io.StringWriter();
        try
        {
          java.io.PrintWriter log = new java.io.PrintWriter(writer);
          try
          {
            Database db;
            db = JPortal.run(name, reader, log);
            if (db != null)
              database = db;
          }
          finally
          {
            log.flush();
            log.close();
          }
          string result = writer.ToString();
          char[] sep = {'\n'};
          string[] lines = result.Split(sep);
          for (int i=0; i<lines.Length; i++)
          {
            string line = lines[i].Trim();
            if (line.Length == 0)
              continue;
            form.LogInfo = line;
          }
        }
        finally
        {
          writer.close();
        }
      }
      finally
      {
        reader.close();
      }
      return database != null;
    }
	}
}

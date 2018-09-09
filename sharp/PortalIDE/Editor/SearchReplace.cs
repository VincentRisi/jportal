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
using ICSharpCode.TextEditor;
using ICSharpCode.TextEditor.Document;

namespace Bbd.AnyDB
{
  public class SearchReplace
  {
    private EditSetBase editSet;
    private TextEditorControl editor;
    private TextArea area;
    private Caret caret;
    private ITextBufferStrategy buffer;
    private TextView view;
    IDocument document;
    public SearchReplace(EditSetBase editSet)
    {
      this.editSet = editSet;
      editor = editSet.editor;
      area = editor.ActiveTextAreaControl.TextArea;
      caret = editor.ActiveTextAreaControl.Caret;
      buffer = editor.Document.TextBufferStrategy;
      view = area.TextView;
      document = editor.Document;
    }
    private bool FindUp(string toFind, string replaceWith, bool matchCase, bool matchWord)
    {
      bool result = false;
      int l = buffer.Length;
      int o = caret.Offset;
      string b = buffer.GetText(0, o);
      int n = matchCase 
        ? b.LastIndexOf(toFind)
        : b.ToUpper().LastIndexOf(toFind.ToUpper());
      if (n >= 0)
      {
        if (replaceWith != null)
          document.Replace(n, toFind.Length, replaceWith);
        caret.Position = document.OffsetToPosition(n);
        view.FirstVisibleLine = editSet.RationalStart(caret.Line, 8);
        result = true;
      }
      editSet.SetEditorActive();
      return result;
    }
    private bool FindDown(string toFind, string replaceWith, bool matchCase, bool matchWord)
    {
      bool result = false;
      int l = buffer.Length;
      int o = caret.Offset;
      string f = buffer.GetText(o, l-o);
      int n = matchCase
        ? f.IndexOf(toFind)
        : f.ToUpper().IndexOf(toFind.ToUpper());
      if (n >= 0)
      {
        if (replaceWith != null)
        {
          document.Replace(o+n, toFind.Length, replaceWith);
          caret.Position = document.OffsetToPosition(o+n+replaceWith.Length);
        }
        else
        {
          caret.Position = document.OffsetToPosition(o+n+toFind.Length);
        }
        view.FirstVisibleLine = editSet.RationalStart(caret.Line, 8);
        result = true;
      }
      editSet.SetEditorActive();
      return result;
    }
    private bool FindUpRegEx(string toFind, string replaceWith)
    {
      return false;
    }
    private bool FindDownRegEx(string toFind, string replaceWith)
    {
      return false;
    }
    public bool Find(string toFind, string replaceWith, bool matchCase, bool matchWord, bool searchUp, bool useRegEx)
    {
      if (searchUp)
      {
        if (useRegEx)
          return FindUpRegEx(toFind, replaceWith);
        else
          return FindUp(toFind, replaceWith, matchCase, matchWord);
      }
      else
      {
        if (useRegEx)
          return FindDownRegEx(toFind, replaceWith);
        else
          return FindDown(toFind, replaceWith, matchCase, matchWord);
      }
    }
  }
}

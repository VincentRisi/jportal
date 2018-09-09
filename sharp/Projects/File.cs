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
/// $Date: 2004/10/18 13:48:17 $
/// $Revision: 411.1 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.IO;

namespace Bbd.AnyDB
{
	public class File : IFormattable
	{
    protected string fileName;
    protected DateTime lastWritten = DateTime.MinValue;
    protected FileInfo fileInfo;
    protected bool modified;
    public bool Modified
    {
      get {return modified;}
      set {modified = value;}
    }
    public string FileName
    {
      get {return fileName;}
      set {fileName = value;}
    }
    public DateTime LastWritten
    {
      get
      {
        if (lastWritten == DateTime.MinValue)
        {
          fileInfo = new FileInfo(fileName);
          if (fileInfo.Exists)
            lastWritten = fileInfo.LastWriteTime;
        }
        return lastWritten;
      }
      set {lastWritten = value;}
    }
		public File(string fileName)
		{
      this.fileName = fileName;
      Modified = false;
		}
    public File() : this("")
    {}
    public const string AddinsTag = "AddinList";
    public const string AddinTag = "Addin";
    public const string HighlightersTag = "HighlighterList";
    public const string HighlighterTag = "Highlighter";
    public const string MainTag = "AnyDBIde";
    public const string ProjectsTag = "ProjectList";
    public const string ProjectTag = "Project";
    public const string SwitchesTag = "SwitchList";
    public const string SwitchTag = "Switch";
    public const string SourcesTag = "SourceList";
    public const string SourceTag = "Source";
    public const string TargetsTag = "TargetList";
    public const string TargetTag = "Target";
    public const string ScreenTag = "Screen";
    public const string ListsPanelTag = "ListsPanel";
    public const string SourceListTag = "SourceList";
    #region Implementation of IFormattable
    public string ToString(string format, System.IFormatProvider formatProvider)
    {
      string result;
      switch (format.ToUpper())
      {
      case "L":
        result = fileName;
        break;
      case "S":
        result = new FileInfo(fileName).Name;
        break;
      default:						
        result = fileName.ToString(formatProvider);
        break;
      }
      return result;
    }
    #endregion
    public override string ToString()
    {
      return this.ToString("S");
    }
    public string ToString(string format)
    {
      return this.ToString(format,null);
    }
  }
}

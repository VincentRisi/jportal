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
using System.Collections;
using System.Xml;

namespace Bbd.AnyDB
{	
  public class Source : File, IComparer, IComparable
  {
    private ArrayList targets = new ArrayList();
    private bool exists;
    public bool Exists
    {
      get { return exists; }
      set { exists = value; }
    }
    public int Count
    {
      get { return targets.Count; }
    }
    public Target this[int no]
    {
      get { return (Target)targets[no]; }
      set { targets[no] = value; }
    }
    public bool OutOfDate
    {
      get
      {
        if (Exists == false)
          return true;
        foreach (Target target in targets)
        {
          if (LastWritten > target.LastWritten)
            return true;
        }
        return false;
      }
    }
    public Source(string fileName, bool exists) : base(fileName)
    {
      this.exists = exists;
    }
    public Source() {}
    public int Add(string fileName)
    {
      Target target;
      for (int i=0; i<Count; i++)
      {
        target = this[i];
        if (fileName.ToUpper().Equals(target.FileName.ToUpper()))
          return i;
      }
      target = new Target(fileName);
      return targets.Add(target);
    }
    public void Clear()
    {
      targets.Clear();
    }
    public void EraseTargets()
    {
      Target target;
      for (int i=0; i<Count; i++)
      {
        target = this[i];
        FileInfo x = new FileInfo(target.FileName);
        if (x.Exists == true)
          x.Delete();
      }
      targets.Clear();
    }
    public void Load(XmlNode node)
    {
      FileName = node.Attributes.GetNamedItem("Name").Value;
      int noTargets = int.Parse(node.Attributes.GetNamedItem("Targets").Value);
      exists = bool.Parse(node.Attributes.GetNamedItem("Exists").Value);
      targets.Clear();
      foreach(XmlNode targetNode in node.ChildNodes)
      {
        Target target = new Target();
        target.Load(targetNode);
        targets.Add(target);
      }
    }
    public void Save(TextWriter textWriter)
    {
      textWriter.WriteLine("  <{0} Name=\"{1:L}\" Targets=\"{2}\" Exists=\"{3}\" >"
        , SourceTag, FileName, targets.Count, Exists);
      targets.Sort();
      for (int i=0; i<targets.Count; i++)
        this[i].Save(textWriter);
      textWriter.WriteLine("  </{0}>", SourceTag);
    }
    public int Compare(object x, object y)
    {
      Source a = x as Source;
      Source b = y as Source;
      if (a != null && b != null)
        return a.fileName.CompareTo(b.fileName);
      return 0;
    }
    public int CompareTo(object obj)
    {
      Source a = obj as Source;
      if (a != null)
        return fileName.CompareTo(a.fileName);
      return 0;
    }
  }
}

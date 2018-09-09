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
  public class Project : File
  {
    private SortedList switches = new SortedList();
    private ArrayList sources = new ArrayList();
    public SortedList Switch
    {
      get { return switches; }
      set { switches = value; }
    }
    public int Count
    {
      get { return sources.Count; }
    }
    public Source this[int no]
    {
      get { return (Source)sources[no]; }
      set { sources[no] = value; }
    }
    public Project(string fileName) : base(fileName)
    { }
    public Project()
    { }
    public int Add(string fileName, bool exists)
    {
      Source source;
      for (int i=0; i<Count; i++)
      {
        source = this[i];
        if (fileName.ToUpper().Equals(source.FileName.ToUpper()))
          return i;
        if (new FileInfo(source.FileName).Name.ToUpper().Equals(new FileInfo(fileName).Name.ToUpper()))
          throw new Exception("File "+fileName+" already exists under a different directory.");
      }
      Modified = true;
      source = new Source(fileName, exists);
      return sources.Add(source);
    }
    public void Remove(Source source)
    {
      Modified = true;
      sources.Remove(source);
    }
    public void Load()
    {
      XmlDocument doc = new XmlDocument();
      doc.Load(FileName);
      XmlNode main = doc.ChildNodes[0];
      foreach(XmlNode node in main.ChildNodes)
      {
        switch (node.Name)
        {
        case ProjectTag:
          XmlNode project = node;
          string name = project.Attributes.GetNamedItem("Name").Value;
          int noSwitches = int.Parse(project.Attributes.GetNamedItem("Switches").Value);
          int noSources = int.Parse(project.Attributes.GetNamedItem("Sources").Value);
          break;
        case SwitchesTag:
          switches.Clear();
          foreach(XmlNode switchNode in node)
          {
            string switchName = switchNode.Attributes.GetNamedItem("Name").Value;
            string switchValue = switchNode.Attributes.GetNamedItem("Value").Value;
            switches.Add(switchName, switchValue);
          }
          break;
        case SourcesTag:
          sources.Clear();
          foreach(XmlNode sourceNode in node)
          {
            Source source = new Source();
            source.Load(sourceNode);
            sources.Add(source);
          }
          break;
        }
      }
    }
    public void Save()
    {
      TextWriter textWriter = new StreamWriter(FileName);
      try
      {
        textWriter.WriteLine("<{0}>", MainTag);
        textWriter.WriteLine(" <{0} Name=\"{1}\" Switches=\"{2}\" Sources=\"{3}\" />"
          , ProjectTag, FileName, switches.Count, sources.Count);
        textWriter.WriteLine(" <{0}>", SwitchesTag);
        for (int i=0; i<switches.Count; i++)
          textWriter.WriteLine("  <{0} Name=\"{1}\" Value=\"{2}\" />"
            , SwitchTag, switches.GetKey(i), switches.GetByIndex(i));
        textWriter.WriteLine(" </{0}>", SwitchesTag);
        textWriter.WriteLine(" <{0}>", SourcesTag);
        sources.Sort();
        for (int i=0; i<sources.Count; i++)
          this[i].Save(textWriter);
        textWriter.WriteLine(" </{0}>", SourcesTag);
        textWriter.WriteLine("</{0}>", MainTag);
        Modified = false;
      }
      finally
      {
        textWriter.Close();
      }
    }
  }
}

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
  public class Target : File, IComparer, IComparable
  {
    public Target(string fileName) : base(fileName) {}
    public Target(){}
    public void Load(XmlNode targetNode)
    {
      FileName = targetNode.Attributes.GetNamedItem("Name").Value;
    }
    public void Save(TextWriter textWriter)
    {
      textWriter.WriteLine("   <{0} Name=\"{1}\" />", TargetTag, FileName);
    }
    public int Compare(object x, object y)
    {
      Target a = x as Target;
      Target b = y as Target;
      if (a != null && b != null)
        return a.fileName.CompareTo(b.fileName);
      return 0;
    }
    public int CompareTo(object obj)
    {
      Target a = obj as Target;
      if (a != null)
        return fileName.CompareTo(a.fileName);
      return 0;
    }
  }
}

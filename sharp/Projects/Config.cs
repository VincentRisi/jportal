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
/// $Date: 2004/11/17 09:13:38 $
/// $Revision: 411.2 $ // YMM.Revision
/// ------------------------------------------------------------------

using System;
using System.IO;
using System.Collections;
using System.Xml;

namespace Bbd.AnyDB
{
  public class Config : File
  {
    public const string Assembly        = "Assembly";
    public const string Comment         = "Comment";
    public const string Exists          = "Exists";
    public const string Height          = "Height";
    public const string Last            = "Last";
    public const string LastProject     = "LastProject";
    public const string ListsPanelWidth = "ListsPanelWidth";
    public const string Name            = "Name";
    public const string NoAddinComments = "NoAddinComments";
    public const string NoAddins        = "NoAddins";
    public const string NoRecentProjects = "NoRecentProjects";
    public const string ScreenHeight    = "ScreenHeight";
    public const string ScreenWidth     = "ScreenWidth";
    public const string ScreenX         = "ScreenX";
    public const string ScreenY         = "ScreenY";
    public const string SourceListHeight = "SourceListHeight";
    public const string Sources         = "Sources";
    public const string Switches        = "Switches";
    public const string Targets         = "Targets";
    public const string Value           = "Value";
    public const string Width           = "Width";
    public const string X               = "X";
    public const string Y               = "Y";
    private static Config instance;
    private SortedList list = new SortedList();
    public static Config Instance
    {
      get
      {
        if (instance == null)
          instance = new Config();
        return instance;
      }
    }
    public string this[string key]
    {
      get
      {
        string result = list[key] as string;
        if (result == null)
          result = "";
        return result;
      }
      set
      {
        list[key] = value;
      }
    }
    Microsoft.Win32.RegistryKey regKey;
    private Config()
    {
       regKey = Microsoft.Win32.Registry.CurrentUser.CreateSubKey("Software\\Barone, Budge and Dominick\\AnyDB");
    }
    private int IntParse(string value)
    {
      try
      {
        return int.Parse(value);
      }
      catch
      {
        return 0;
      }
    }
    private bool sawScreen = false;
    public void Load()
    {
      int i = 0;
      XmlDocument doc = new XmlDocument();
      doc.Load(FileName);
      XmlNode main = doc.ChildNodes[0];
      foreach (XmlNode node in main.ChildNodes)
      {
        if (node == null
          || node.GetType() == typeof(XmlComment))
          continue;
        switch (node.Name)
        {
          case ScreenTag:
            sawScreen = true;
            list[ScreenX] = node.Attributes.GetNamedItem(X).Value;
            list[ScreenY] = node.Attributes.GetNamedItem(Y).Value;
            list[ScreenWidth] = node.Attributes.GetNamedItem(Width).Value;
            list[ScreenHeight] = node.Attributes.GetNamedItem(Height).Value;
            break;
          case ListsPanelTag:
            list[ListsPanelWidth] = node.Attributes.GetNamedItem(Width).Value;
            break;
          case SourceListTag:
            list[SourceListHeight] = node.Attributes.GetNamedItem(Height).Value;
            break;
          case ProjectsTag:
            list[LastProject] = node.Attributes.GetNamedItem(Last).Value;
            i = 0;
            foreach (XmlNode project in node.ChildNodes)
            {
              if (node == null)
                continue;
              string keyName = ProjectTag+i.ToString();
              string projName = project.Attributes.GetNamedItem(Name).Value;
              list[keyName] = projName;
              list[projName.ToUpper()] = keyName;
              i++;
            }
            list[NoRecentProjects] = i.ToString();
            break;
          case AddinsTag:
            i = 0;
            int c = 0;
            foreach (XmlNode addin in node.ChildNodes)
            {
              if (addin.GetType() == typeof(XmlComment))
              {
                list[AddinTag+Comment+c] = ((XmlComment)addin).Data;
                c++;
                continue;
              }
              list[AddinTag+i.ToString()] = addin.Attributes.GetNamedItem(Assembly).Value;
              i++;
            }
            if (node == null)
              continue;
            list[NoAddinComments] = c.ToString();
            list[NoAddins] = i.ToString();
            break;
        }
      }
      if (sawScreen == true)
        SaveRegistry();
      else
        LoadRegistry();
    }
    private void SaveRegistry()
    {
      try
      {
        regKey.SetValue(ScreenX, list[ScreenX]);      
        regKey.SetValue(ScreenY, list[ScreenY]);      
        regKey.SetValue(ScreenWidth, list[ScreenWidth]);
        regKey.SetValue(ScreenHeight, list[ScreenHeight]);
        regKey.SetValue(ListsPanelWidth, list[ListsPanelWidth]);
        regKey.SetValue(SourceListHeight, list[SourceListHeight]);
        regKey.SetValue(LastProject, list[LastProject]);
      }
      catch (Exception)
      {
      }
    }
    private string GetValue(string key, string value)
    {
      object v = regKey.GetValue(key);
      if (v as string != null)
        return (string) v;
      return value;
    }
    private void LoadRegistry()
    {
      list[ScreenX] = GetValue(ScreenX, "0");      
      list[ScreenY] = GetValue(ScreenY, "0");      
      list[ScreenWidth] = GetValue(ScreenWidth, "800");
      list[ScreenHeight] = GetValue(ScreenHeight, "600");
      list[ListsPanelWidth] = GetValue(ListsPanelWidth, "150");
      list[SourceListHeight] = GetValue(SourceListHeight, "200");
      list[LastProject] = GetValue(LastProject, "");
    }
    public void Save()
    {
      SaveRegistry();
      if (sawScreen == false)
        return;
      string backup = FileName+".backup";
      if (System.IO.File.Exists(FileName))
      {
        if (System.IO.File.Exists(backup))
           System.IO.File.Delete(backup);
        System.IO.File.Move(FileName, backup);
      }
      TextWriter textWriter = new StreamWriter(FileName);
      try
      {
        textWriter.WriteLine("<{0}>", MainTag);
        textWriter.WriteLine(" <{0}>", AddinsTag);
        int noAddins = IntParse(list[NoAddins] as string);
        int noAddinComments = IntParse(list[NoAddinComments] as string);
        for (int i=0; i<noAddinComments; i++)
          textWriter.WriteLine("  <!--{0}-->", list[AddinTag+Comment+i.ToString()]);
        for (int i=0; i<noAddins; i++)
          textWriter.WriteLine("  <{0} Assembly=\"{1}\" />", AddinTag, list[AddinTag+i.ToString()]);
        textWriter.WriteLine(" </{0}>", AddinsTag);
        textWriter.WriteLine("</{0}>", MainTag);
      }
      catch
      {
      }
      finally
      {
        textWriter.Close();
      }
    }
  }
}

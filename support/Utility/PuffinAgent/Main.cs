using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml;
using System.Collections;
using bbd.idl2.bdcserver;
using bbd.idl2.anydb;
using bbd.idlclient.servers;
using bbd.idl2.rpc;
using bbd.idl2.AnyDB;
using IronPython.Hosting;
using IronPython.Modules;

namespace Bbd.Idl2.PuffinAgent
{
  class XmlRecord
  {
    class ValueSet
    {
      internal string value;
      internal XmlAttributeCollection attributes;
      public ValueSet(string value, XmlAttributeCollection attributes)
      {
        this.value = value;
        this.attributes = attributes;
      }
    }
    private SortedList<string, List<ValueSet>> dictList;
    private XmlDocument doc;
    private void Add(string key, string value, XmlAttributeCollection attributes)
    {
      if (value != null)
        value = value.Trim();
      List<ValueSet> values;
      if (dictList.ContainsKey(key) == true)
      {
        values = dictList[key];
        ValueSet valueSet = values[values.Count - 1];
        if (valueSet.value == null && attributes == null)
          valueSet.value = value;
        else
          values.Add(new ValueSet(value, attributes));
        return;
      }
      values = new List<ValueSet>();
      values.Add(new ValueSet(value, attributes));
      dictList.Add(key, values);
    }
    private void Recurse(XmlNodeList nodes, string key)
    {
      string next = key;
      foreach (XmlNode node in nodes)
      {
        if (node.Name == "#comment")
          continue;
        if (key.Length > 0)
        {
          if (node.Name != "#text")
            next = key + "." + node.Name;
        }
        else
          next = node.Name;
        if (node.ChildNodes != null && node.ChildNodes.Count > 0)
        {
          if (node.Attributes != null && node.Attributes.Count > 0)
            Add(next, node.Value, node.Attributes);
          Recurse(node.ChildNodes, next);
        }
        else
          Add(next, Check(node.Value), node.Attributes);
      }
    }
    private string Check(string p)
    {
      if (p != null)
        return p;
      return "";
    }
    public XmlRecord(string xml)
    {
      dictList = new SortedList<string, List<ValueSet>>();
      doc = new XmlDocument();
      doc.LoadXml(xml);
    }
    public string GetList()
    {
      try
      {
        Recurse(doc.ChildNodes, "");
      }
      catch (Exception ex)
      {
        return "ERROR=" + ex.Message;
      }
      StringBuilder b = new StringBuilder();
      foreach (string key in dictList.Keys)
      {
        List<ValueSet> values = dictList[key];
        if (values.Count == 1)
        {
          if (values[0].value != null)
            b.AppendLine(string.Format("{0}={1}", key, values[0].value));
          if (values[0].attributes != null)
            foreach (XmlAttribute attribute in values[0].attributes)
              b.AppendLine(string.Format("{0}/{1}={2}", key, attribute.Name, attribute.Value));
        }
        else
        {
          for (int i = 0; i < values.Count; i++)
          {
            if (values[i].value != null)
              b.AppendLine(string.Format("{0}%{1}={2}", key, i, values[i].value));
            if (values[i].attributes != null)
              foreach (XmlAttribute attribute in values[i].attributes)
                b.AppendLine(string.Format("{0}%{1}/{2}={3}", key, i, attribute.Name, attribute.Value));
          }
        }
      }
      return b.ToString();
    }
  }
  public partial class PuffinAgent
  {
    BDCIFace Server;
    public PuffinAgent()
    {
      Server = BDCServer.Server();
    }
    public string GetList(string xml)
    {
      XmlRecord rec = new XmlRecord(xml);
      return rec.GetList();
    }
    private string message;
    public string Message { get { return message;  } set { message = value; } }
    private string reply;
    public string Reply { get { return reply; } set { reply = value; } }
  }

  public partial class PuffinPython
  {
    private PuffinAgent puffinAgent;
    private static StringBuilder loaded;
    private static string runDir;
    public static string RunDir { get { return runDir; } set { runDir = value; } }
    private static string srcDir;
    public static string SrcDir { get { return srcDir; } set { srcDir = value; } }
    private MemoryStream sbout;
    private MemoryStream sberr;
    public string stdout { get { return Encoding.ASCII.GetString(sbout.ToArray()); } }
    public string stderr { get { return Encoding.ASCII.GetString(sberr.ToArray()); } }
    private PythonEngine engine;
    private static bool reload = false;
    public static bool Reload { set { reload = value; } }
    public static PuffinPython instance;
    public static PuffinPython Instance
    { get { if (instance == null || reload == true) instance = new PuffinPython(); reload = false;  return instance; } }
    public void Execute(string script)
    {
      sbout.Position = 0;
      sbout.SetLength(0);
      sberr.Position = 0;
      sberr.SetLength(0);
      LoadScript(script);
      string runText = string.Format("import {0}\r\n{0}.main()\r\n", script);
      engine.Execute(runText);
    }
    public void Execute(string script, string message)
    {
      puffinAgent.Message = message;
      Execute(script);
    }
    public string Message { get { return puffinAgent.Message; } set { puffinAgent.Message = value; } }
    public string Reply { get { return puffinAgent.Reply; } set { puffinAgent.Reply = value; } }
    private static void WritePy(string name, string code)
    {
      string lookup = string.Format(":{0};", name.ToUpper());
      if (loaded.ToString().IndexOf(lookup) == -1)
        loaded.Append(lookup);
      string fileName = string.Format("{0}\\{1}.py", runDir, name);
      using (StreamWriter sw = new StreamWriter(fileName, false, Encoding.ASCII))
      {
        sw.Write(code);
        sw.Flush();
        sw.Close();
      }
    }
    private static StringBuilder seen;
    private static string Word(string value)
    {
      string[] work = value.Trim().Split(" \t".ToCharArray());
      if (work.Length >= 1)
        return work[0];
      return value;
    }
    private static void LoadPty(string name)
    { 
      string lookup = string.Format(":{0};", name.ToUpper());
      if (seen.ToString().IndexOf(lookup) != -1)
        return;
      seen.Append(lookup);
      string fileName = string.Format("{0}\\{1}.pty", srcDir, name);
      System.IO.FileInfo info = new FileInfo(fileName);
      if (info.Exists == false)
      {
        if (loaded.ToString().IndexOf(lookup) == -1)
          throw new Exception(string.Format("File {0} either does not exist or is not already loaded.", fileName));
        return;
      }
      StringBuilder code = new StringBuilder();
      using (StreamReader sr = new StreamReader(fileName))
      {
        while (sr.EndOfStream == false)
        {
          string line = sr.ReadLine();
          if (line.IndexOf("#use") == 0)
          {
            string next = line.Substring(4).Trim();
            int n = next.IndexOf('=');
            if (n != -1)
            {
              string useas = Word(next.Substring(n + 1));
              next = next.Substring(0, n);
              code.AppendLine(string.Format("import {0} as {1}", Word(next), useas));
            }
            else
              code.AppendLine(string.Format("from {0} import *", Word(next)));
            LoadPty(Word(next));
            continue;
          }
          code.AppendLine(line);
        }
      }
      WritePy(string.Format("{0}", name),code.ToString());
    }
    public void LoadScript(string name)
    {
      StringBuilder save = loaded;
      try
      {
        loaded = new StringBuilder(save.ToString());
        seen = new StringBuilder();
        LoadPty(name);
      }
      finally
      {
        loaded = save;
      }
    }
    private static string ExtraCode()
    {
      StringBuilder b = new StringBuilder();
      b.AppendLine(@"def xmlparse(xml):");
      b.AppendLine(@"  tags = agent.GetList(xml)");
      b.AppendLine(@"  lines = tags.split('\n')");
      b.AppendLine(@"  dict = {}");
      b.AppendLine(@"  for line in lines:");
      b.AppendLine(@"    arr = line.strip().split('=')");
      b.AppendLine(@"    if len(arr) == 2:");
      b.AppendLine(@"      dict[arr[0]] = arr[1]");
      b.AppendLine(@"  return dict");
      b.AppendLine(@"def readmessage():");
      b.AppendLine(@"  msg = agent.Message");
      b.AppendLine(@"  if len(msg) > 0 and msg[0] == '<':");
      b.AppendLine(@"    return xmlparse(msg)");
      b.AppendLine(@"  return msg");
      b.AppendLine(@"def reply(data):");
      b.AppendLine(@"  agent.Reply = data");
      return b.ToString();
    }
    private PuffinPython()
    {
      if (srcDir == null || srcDir.Length == 0)
        srcDir = @"c:\bbd\python\source";
      if (runDir == null || runDir.Length == 0)
        runDir = @"c:\bbd\python\runtime";
      engine = new PythonEngine();
      sbout = new MemoryStream();
      sberr = new MemoryStream();
      loaded = new StringBuilder();
      EngineModule agent = engine.CreateModule("agentModule", true);
      puffinAgent = new PuffinAgent();
      agent.Globals.Add("agent", puffinAgent);
      engine.SetStandardOutput(sbout);
      engine.SetStandardError(sberr);
      engine.AddToPath(runDir);
      WritePy("INTRINSICS", Code());
      WriteDB();
    }
  }
}

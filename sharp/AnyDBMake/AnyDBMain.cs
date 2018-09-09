using System;
using System.IO;
using System.Collections;
using System.Configuration;
using System.Diagnostics;
using System.Reflection;
using bbd.jportal;

namespace Bbd.AnyDB
{
	public class AnyDBMain
	{
    static TraceLevel traceLevel; 
    static void WriteLineIf(bool doIt, string value)
    {
      if (doIt)
      {
        Console.WriteLine(value);
        Trace.WriteLine(value);
      }
    }
    static String LogVerbose
    {
      set
      {
        WriteLineIf(traceLevel>=TraceLevel.Verbose, value);
      }
    }
    static String LogInfo
    {
      set
      {
        WriteLineIf(traceLevel>=TraceLevel.Info, value);
      }
    }
    static String LogWarn
    {
      set
      {
        WriteLineIf(traceLevel>=TraceLevel.Warning, value);
      }
    }
    static String LogError
    {
      set
      {
        WriteLineIf(traceLevel>=TraceLevel.Error, value);
      }
    }
    private Config config;
    private string StartupPath;
    private bool LoadConfig()
    {
      config = Config.Instance;
      config.FileName = StartupPath+@"\Config\AnyDBMake.xml";
      if (System.IO.File.Exists(config.FileName) == false)
        config.FileName = StartupPath+@"\Config\AnyDBIde.xml";
      if (System.IO.File.Exists(config.FileName) == true)
        config.Load();
      else
        return false;
      return true;
    }
    static SortedList generatorList = new SortedList();
    private void LoadGenerators()
    {
      LogVerbose ="Loading Generators";
      int noAddins = int.Parse(config["NoAddins"]);
      SortedList list = generatorList;
      int u = 0;
      for (int addin=0; addin<noAddins; addin++)
      {
        string assemblyName = config[File.AddinTag+addin.ToString()];
        Assembly assembly = Assembly.LoadFile(assemblyName);
        System.Reflection.Module[] modules = assembly.GetLoadedModules(true);
        for (int m=0; m<modules.Length; m++)
        {
          System.Type[] types = modules[m].GetTypes();
          for (int t=0; t<types.Length; t++)
          {
            if (list.Contains(types[t].Name) == false)
            {
              list.Add(types[t].Name, types[t]);
              LogVerbose = string.Format("Generator {0} loaded.", types[t].Name);
            }
          }
        }
      }
    }
    private void RunGenerators(bbd.jportal.Database database, java.io.PrintWriter log)
    {
      object[] args = {database, typeof(string), log};
      string name = source.ToString("S");
      for (int i=0; i<generatorList.Count; i++)
      {
        string generatorKey = string.Format("{0}.{1}Generator", name, generatorList.GetKey(i));
        if (project.Switch.IndexOfKey(generatorKey) == -1)
          continue;
        bool required = bool.Parse((string)project.Switch[generatorKey]);
        if (required == false)
          continue;
        string directoryKey = string.Format("{0}.{1}Directory", source.ToString("S"), generatorList.GetKey(i));
        if (project.Switch.IndexOfKey(generatorKey) != -1)
          args[1] = project.Switch[directoryKey];
        MethodInfo method = generatorList.GetByIndex(i) as MethodInfo;
        if (method == null)
        {
          LogWarn = "No proper generator for "+name;
          continue;
        }
        try
        {
          method.Invoke(null, args);
        }
        catch
        {
          LogVerbose ="Generation failed for "+name;
        }
      }
    }
    private void RunCompile(Source source)
    {
      source.Clear();
      java.io.StringWriter writer = new java.io.StringWriter();
      try
      {
        java.io.PrintWriter log = new java.io.PrintWriter(writer);
        try
        {
          string currDir = Directory.GetCurrentDirectory();
          try
          {
            FileInfo info = new FileInfo(source.FileName);
            LogVerbose ="Changing to " + info.DirectoryName;
            Directory.SetCurrentDirectory(info.DirectoryName);
            LogVerbose ="Compiling " + info.Name;
            bbd.jportal.Database database = JPortal.run(info.FullName, log);
            RunGenerators(database, log);
          }
          finally
          {
            Directory.SetCurrentDirectory(currDir);
          }
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
          if (line.IndexOf("Code: ") == 0)
            source.Add(line.Substring(6).Trim());
          else if (line.IndexOf("DDL: ") == 0)
            source.Add(line.Substring(5).Trim());
          LogInfo = line;
        }
      }
      finally
      {
        writer.close();
      }
    }
    static Project project;
    static void Compile(Source source, ref bool modified)
    {
      LogInfo = string.Format("{0}", source.FileName);
      if (project.LastWriteTime < source.LastWriteTime)
      {
        RunCompile(source);
        modified = true;
      }
      else
        LogInfo = "Source is up to date";
    }
    [STAThread]
    static int Main(string[] args)
    {
      try
      {
        AppSettingsReader appSettings = new AppSettingsReader();
        traceLevel = (TraceLevel)((int)(appSettings.GetValue("TraceLevel.Value", typeof(int))));
        Assembly assembly = Assembly.GetExecutingAssembly();
        LogInfo = assembly.GetName().ToString();
        if (args.Length < 2)
        {
          LogError = "Usage";
          LogError = "  AnyDBMake config project siFile ...";
          LogError = "  where";
          LogError = "    project is an anyDB xml project file as used for Bbd.AnyDBIde";
          LogError = "    siFile ... is the list of siFiles to compile";
          LogError = "or";
          LogError = "  AnyDBMake config project";
          LogError = "  where";
          LogError = "    project is an anyDB project file as used for Bbd.Idl2Ide";
          LogError = "    and all siFiles contained therein must compile";
          return -1;
        }
        if (LoadConfig(args[0]) == false)
        {
          LogError = string.Format("Config File {0} does not exists", config.FileName);
          return -2;
        }
        project = new Project(args[1]);
        project.Load();
        LoadGenerators();
        bool modified = false;
        if (args.Length > 1)
        {
          for (int i=1; i<args.Length; i++)
          {
            bool made = false;
            LogInfo = string.Format("Making {0}", args[i]);
            for (int j=0; j<project.Sources.Count; j++)
            {
              Source source = project[j];
              if (args[i].ToLower() == source.ToString("S").ToLower())
              {
                Compile(source, ref modified);
                made = true;
                break;
              }
            }
            if (made == false)
            {
              LogError = "Not made as not found in project, project contains:";
              for (int j=0; j<project.Sources.Count; j++)
              {
                Source source = project[j];
                LogError = string.Format("{0}", source.FileName);
              }
              return -3;
            }
          }
        }
        else 
        {
          LogInfo = "Making all siFiles in " + project.FileName;
          for (int j=0; j<project.Sources.Count; j++)
          {
            Source source = project[j];
            Compile(source, ref modified);
          }
        }
        if (modified == true)
          project.Save();
        return 0;
      }
      catch (Exception ex)
      {
        LogError = ex.Message;
        LogVerbose = ex.StackTrace;
        return 1;
      }
    }
	}
}

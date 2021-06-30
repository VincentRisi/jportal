/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi 
///                           
/// All rights reserved. 
/// This program and the accompanying materials are made available 
/// under the terms of the Common Public License v1.0 
/// which accompanies this distribution and is available at 
/// http://www.eclipse.org/legal/cpl-v10.html 
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------

package vlab.crackle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class PopHTTPPython extends Generator
{
  public static String description()
  {
    return "Generates HTTP Python Test Code (AIX|LINUX|WINDOWS)";
  }

  public static String documentation()
  {
    return "Generates HTTP Python Test Code (AIX|LINUX|WINDOWS)";
  }

  private static PrintWriter outLog;

  public static void main(String args[])
  {
    try
    {
      outLog = new PrintWriter(System.out);
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module) in.readObject();
        in.close();
        generate(module, "", outLog);
      }
      outLog.flush();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private static Properties properties;
  private static String urlPrefix = "..";
  private static String defSqlSub = "/yaml2";
  private static String compSqlSub = "/yaml3";
  private static String description;

  public static void generate(Module module, String output, PrintWriter inOutLog)
  {
    outLog = inOutLog;
    outLog.println(module.name + " version " + module.version);
    try
    {
      String propertiesName = format("%s%s.properties", output, module.name);
      InputStream input = new FileInputStream(propertiesName);
      properties = new Properties();
      properties.load(input);
    } 
    catch (Exception ex)
    {
      properties = null;
    }
    getPragmas(module);
    generatePython(module, output, outLog);
  }

  private static String _getProperty(String propName, String propDefault)
  {
    if (properties == null)
      return propDefault;
    String propValue = properties.getProperty(propName);
    if (propValue == null)
      return propDefault;
    outLog.println(propName + "=" + propValue);
    return propValue;
  }

  private static void getPragmas(Module module)
  {
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      pragma = pragma.trim();
      int n = pragma.indexOf("openapi:");
      if (n != 0)
        continue;
      pragma = pragma.substring(8).trim();
      n = pragma.indexOf("urlPrefix:");
      if (n == 0)
      {
        urlPrefix = pragma.substring(10).trim();
        if (urlPrefix.charAt(0) == '$')
          urlPrefix = _getProperty(urlPrefix.substring(1), "..");
        continue;
      }
      n = pragma.indexOf("defSqlSub:");
      if (n == 0)
      {
        defSqlSub = pragma.substring(10).trim();
        if (defSqlSub.charAt(0) == '$')
          defSqlSub = _getProperty(defSqlSub.substring(1), "/yaml2");
        continue;
      }
      n = pragma.indexOf("compSqlSub:");
      if (n == 0)
      {
        compSqlSub = pragma.substring(11).trim();
        if (compSqlSub.charAt(0) == '$')
          compSqlSub = _getProperty(compSqlSub.substring(1), "/yaml3");
        continue;
      }
      n = pragma.indexOf("description:");
      if (n == 0)
      {
        description = pragma.substring(12).trim();
        continue;
      }
    }
  }

  private static void generatePython(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + ".py");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + ".py");
      writer = new PrintWriter(outFile);
      indent_size = 4;
      try
      {
        writeln("# This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln(0, format("module_name = '%s'", module.name));
        if (description != null)
          writeln(0, format("description = '%s'", description));
        writeln(0, format("version = %s", module.version));
        writeln();
        writeln("from http_makes import *");
        writeln();
        File file = new File(format("%s%s", urlPrefix, defSqlSub));
        if (file.exists() == true)
          generateDefinitions(module);
        else
        {
          file = new File(format("%s%s", urlPrefix, compSqlSub));
          if (file.exists() == true)
            generateComponents(module);
        }
        writeln("set_error(tError)");
        writeln();
        generatePaths(module);
      } finally
      {
        writer.flush();
        outFile.close();
      }
    } catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
      System.out.println(e1.toString());
      System.out.flush();
      e1.printStackTrace();
    } catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }

  private static void generateIdl2Classes(Module module)
  {
    try
    {
      for (int i = 0; i < module.structures.size(); i++)
      {
        Structure structure = (Structure) module.structures.elementAt(i);
        if (structure.codeType != Structure.NORMAL)
          continue;
        if (structure.fields.size() > 0)
        {
          writeln(0, format("class %s:", structure.name));
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            switch (field.type.typeof)
            {
            case Type.CHAR:
            case Type.WCHAR:
            case Type.STRING:
              writeln(1, format("%s: str", field.name));
              break;
            case Type.BYTE:
            case Type.BOOLEAN:
            case Type.INT:
            case Type.SHORT:
            case Type.LONG:
              writeln(1, format("%s: int", field.name));
              break;
            case Type.FLOAT:
            case Type.DOUBLE:
              writeln(1, format("%s: float", field.name));
              break;
            case Type.USERTYPE:
              writeln(1, format("# %s: %s' # USERTYPE", field.name, field.type.name));
              break;
            case Type.VOID:
              writeln(1, format("# %s: void' # VOID", field.name));
              break;
            default:
              break;
            }
          }
          writeln(1, "def __init__(self):");
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            switch (field.type.typeof)
            {
            case Type.CHAR:
            case Type.WCHAR:
            case Type.STRING:
              writeln(2, format("self.%s = ''", field.name));
              break;
            case Type.BYTE:
            case Type.BOOLEAN:
            case Type.INT:
            case Type.SHORT:
            case Type.LONG:
              writeln(2, format("self.%s = 0", field.name));
              break;
            case Type.FLOAT:
            case Type.DOUBLE:
              writeln(2, format("self.%s = 0.0", field.name));
              break;
            case Type.USERTYPE:
              writeln(2, format("# %s: %s' # USERTYPE", field.name, field.type.name));
              break;
            case Type.VOID:
              writeln(2, format("# %s: void' # VOID", field.name));
              break;
            default:
              break;
            }
          }
        }
      }
    } catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  
  private static void generateDefinitions(Module module)
  {
    writeln(0, "class tError:");
    writeln(1, "error: str");
    writeln(1, "def __init__(self):");
    writeln(2, "self.error = ''");
    writeln("");
    generateIdl2Classes(module);
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.codeType != Structure.NORMAL)
        continue;
      if (structure.fields.size() > 0)
        continue;
      String table = structure.header.substring(1);
      if (table.endsWith(".h\""))
        continue;
      String structName = format(" %s:", structure.name);
      if (done.containsKey(structName) == true)
        continue;
      int end = table.indexOf('.');
      table = table.substring(0, end);
      String yaml2File;
      yaml2File = format("%s%s/%s.yaml", urlPrefix, defSqlSub, table);
      done.put(structName, table);
      Vector<String> sets = new Vector<String> ();
      try
      {
        Reader input = new FileReader(yaml2File);
        try
        {
          BufferedReader reader = new BufferedReader(input);
          String line = null;
          String fieldName = "";
          int state = 0;
          while ((line = reader.readLine()) != null)
          {
            switch (state)
            {
            case 0:
              if (line.contains(structName) == true)
              {
                writeln(0, format("class%s", structName));
                state = 1;
              }
              break;
            case 1:
              if (line.contains("properties:") == true)
                state = 2;
              break;
            case 2:
              if (line.compareTo("...") == 0 || line.charAt(2) != ' ')
              {
                state = 9;
                break;
              }
              if (line.contains("required:") == true)
              {
                state = 9;
                break;
              }
              if (line.charAt(4) == ' ' && line.charAt(6) != ' ')
              {
                fieldName = line.substring(6, line.length()-1);
                state = 3;
                break;
              }
              break;
            case 3:
              if (line.contains("type:") == true)
              {
                if (line.contains("string") == true)
                {
                  writeln(1, format("%s: str", fieldName));
                  sets.addElement(format("self.%s = ''", fieldName));
                }
                else if (line.contains("integer") == true)
                {
                  writeln(1, format("%s: int", fieldName));
                  sets.addElement(format("self.%s = 0", fieldName));
                }
                else if (line.contains("float") == true)
                {
                  writeln(1, format("%s: float", fieldName));
                  sets.addElement(format("self.%s = 0.0", fieldName));
                }
                else if (line.contains("number") == true)
                {
                  writeln(1, format("%s: float", fieldName));
                  sets.addElement(format("self.%s = 0.0", fieldName));
                }
                state = 2;
                break;
              }
              break;
            }
            if (state == 9)
              break;
          }
          reader.close();
          input.close();
          if (sets.size() > 0)
          {
            writeln(1, "def __init__(self):");
            for (int j = 0; j < sets.size(); j++)
            {
              String set = sets.elementAt(j);
              writeln(2, set);
            }
            writeln();
          }
        } 
        catch (IOException e)
        {
          outLog.println(format("%s - IOException", yaml2File));
        }
      } 
      catch (FileNotFoundException e)
      {
        outLog.println(format("%s %s - FileNotFound", yaml2File, structure.name));
      }
    }
  }
  
  private static void generateComponents(Module module)
  {
    writeln(0, "class tError:");
    writeln(1, "error: str");
    writeln(1, "def __init__(self):");
    writeln(2, "self.error = ''");
    writeln("");
    generateIdl2Classes(module);
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.codeType != Structure.NORMAL)
        continue;
      if (structure.fields.size() > 0)
        continue;
      String table = structure.header.substring(1);
      if (table.endsWith(".h\""))
        continue;
      String structName = format(" %s:", structure.name);
      if (done.containsKey(structName) == true)
        continue;
      int end = table.indexOf('.');
      table = table.substring(0, end);
      String yaml3File;
      yaml3File = format("%s%s/%s.yaml", urlPrefix, compSqlSub, table);
      done.put(structName, table);
      Vector<String> sets = new Vector<String> ();
      try
      {
        Reader input = new FileReader(yaml3File);
        try
        {
          BufferedReader reader = new BufferedReader(input);
          String line = null;
          String fieldName = "";
          int state = 0;
          while ((line = reader.readLine()) != null)
          {
            switch (state)
            {
            case 0:
              if (line.contains(structName) == true)
              {
                writeln(0, format("class%s", structName));
                state = 1;
              }
              break;
            case 1:
              if (line.contains("properties:") == true)
                state = 2;
              break;
            case 2:
              if (line.compareTo("...") == 0 || line.charAt(4) != ' ')
              {
                state = 9;
                break;
              }
              if (line.contains("required:") == true)
              {
                state = 9;
                break;
              }
              if (line.charAt(6) == ' ' && line.charAt(8) != ' ')
              {
                fieldName = line.substring(6, line.length()-1);
                state = 3;
                break;
              }
              break;
            case 3:
              if (line.contains("type:") == true)
              {
                if (line.contains("string") == true)
                {
                  writeln(1, format("%s: str", fieldName));
                  sets.addElement(format("self.%s = ''", fieldName));
                }
                else if (line.contains("integer") == true)
                {
                  writeln(1, format("%s: int", fieldName));
                  sets.addElement(format("self.%s = 0", fieldName));
                }
                else if (line.contains("float") == true)
                {
                  writeln(1, format("%s: float", fieldName));
                  sets.addElement(format("self.%s = 0.0", fieldName));
                }
                else if (line.contains("number") == true)
                {
                  writeln(1, format("%s: float", fieldName));
                  sets.addElement(format("self.%s = 0.0", fieldName));
                }
                state = 2;
                break;
              }
              break;
            }
            if (state == 9)
              break;
          }
          reader.close();
          input.close();
          if (sets.size() > 0)
          {
            writeln(1, "def __init__(self):");
            for (int j = 0; j < sets.size(); j++)
            {
              String set = sets.elementAt(j);
              writeln(2, set);
            }
            writeln();
          }
        } 
        catch (IOException e)
        {
          outLog.println(format("%s - IOException", yaml3File));
        }
      } 
      catch (FileNotFoundException e)
      {
        outLog.println(format("%s %s - FileNotFound", yaml3File, structure.name));
      }
    }
  }

  private static void generatePaths(Module module)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateRequest(module, prototype);
      generateResponses(module, prototype);
    }
  }

  private static Map<String, String> done = new HashMap<String, String>();

  private static void generateRequest(Module module, Prototype prototype)
  {
    boolean doInBody = false;
    OpenApi openApi = PopHTTP.getOpenApi(module, prototype);
    String path = openApi.path;
    if (path.indexOf('/') == -1)
      doInBody = true;
    if (doInBody == true)
      generateInBody(module, prototype);
    else
      generateInOther(module, prototype, path);
  }
  
  private static void swaggerParameter(Operation op, Field field, Vector<String> setters)
  {
    String name = field.name;
    switch (field.type.typeof)
    {
    case Type.USERTYPE:
      if (field.type.reference == Type.BYREFPTR)
      {
        if (op != null)
        {
          writeln(1, format("%s: [%s]", name, field.type.name));
          setters.addElement(format("self.%s = []", name));
        }
        else
        {
          writeln(1, format("%s: %s", name, field.type.name));
          setters.addElement(format("self.%s = %s()", name, field.type.name));
        }
      } 
      else if (field.type.reference == Type.BYPTR)
      {
        if (op != null)
        {
          writeln(1, format("%s: [%s]", name, field.type.name));
          setters.addElement(format("self.%s = []", name));
        }
        else
        {
          writeln(1, format("%s: %s", name, field.type.name));
          setters.addElement(format("self.%s = %s()", name, field.type.name));
        }
      } 
      else
      {
        writeln(1, format("%s: %s", name, field.type.name));
        setters.addElement(format("self.%s = %s()", name, field.type.name));
      }
      break;
    case Type.CHAR:
    case Type.WCHAR:
    case Type.STRING:
      writeln(1, format("%s: str", name));
      setters.addElement(format("self.%s = ''", name));
      break;
    case Type.BYTE:
    case Type.BOOLEAN:
    case Type.INT:
    case Type.SHORT:
    case Type.LONG:
      writeln(1, format("%s: int", name));
      setters.addElement(format("self.%s = 0", name));
      break;
    case Type.FLOAT:
    case Type.DOUBLE:
      writeln(1, format("%s: float", name));
      setters.addElement(format("self.%s = 0.0", name));
      break;
    case Type.VOID:
    default:
      break;
    }
  }

  private static void generateInBody(Module module, Prototype prototype)
  {
    OpenApi openApi = PopHTTP.getOpenApi(module, prototype);
    String operation = openApi.getType();
    if (prototype.inputs.size() == 0)
      return;
    Vector<String> setters = new Vector<String>();
    writeln(0, format("class %s_request:", prototype.name));
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      Action input = (Action) prototype.inputs.elementAt(i);
      Operation op = input.sizeOperation();
      Field field = input.getParameter(prototype);
      if (field == null)
        continue;
      swaggerParameter(op, field, setters);
    }
    if (setters.size() > 0)
    {
      writeln(1, "def __init__(self):");
      for (int i = 0; i < setters.size(); i++)
      {
        String set = setters.elementAt(i);
        writeln(2, format("%s", set));
      }
    }
    writeln(1, format("def %s(self, host, port):", operation));
    writeln(2, format("path = '%s'", openApi.path ));
    writeln(2, format("data = make_data(self)"));
    writeln(2, format("url = make_url(host, port)"));
    writeln(2, format("response = requests.%s(f'{url}/{path}', data=data, headers=headers)", operation));
    writeln(2, "return response");
    writeln();
  }

  private static void generateInOther(Module module, Prototype prototype, String path)
  {
    OpenApi openApi = PopHTTP.getOpenApi(module, prototype);
    String operation = openApi.getType();
    if (prototype.inputs.size() == 0)
      return;
    boolean usePath = path.indexOf('{') > 0;
    Vector<String> setters = new Vector<String>();
    writeln(0, format("class %s_request:", prototype.name));
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      Action input = (Action) prototype.inputs.elementAt(i);
      Operation op = input.sizeOperation();
      Field field = input.getParameter(prototype);
      if (field == null)
        continue;
      swaggerParameter(op, field, setters);
    }
    if (setters.size() > 0)
    {
      writeln(1, "def __init__(self):");
      for (int i = 0; i < setters.size(); i++)
      {
        String set = setters.elementAt(i);
        writeln(2, format("%s", set));
      }
    }
    writeln(1, format("def %s(self, host, port):", operation));
    if (usePath == true)
    {
      writeln(2, "params = {}");
      writeln(2, format("path = make_path(self, '%s')", path));
      writeln(2, format("url = make_url(host, port)"));
      writeln(2, format("response = requests.%s(f'{url}/{path}', params=params, headers=headers)", operation));
      writeln(2, "return response");
    }
    else
    {
      writeln(2, "params = {}");
      writeln(2, format("path = '%s'", path));
      writeln(2, format("url = make_url(host, port)"));
      writeln(2, format("response = requests.%s(f'{url}/{path}', params=params, headers=headers)", operation));
      writeln(2, "return response");
    }
    writeln();
  }
  
  private static void generateResponses(Module module, Prototype prototype)
  {
    boolean returnCode = false;
    boolean hasOutput = false;
    if (prototype.type.typeof != Type.VOID)
    {
      if (prototype.type.reference != Type.BYPTR)
        returnCode = true;
      else
        hasOutput = true;
    }
    if (prototype.outputs.size() > 0)
      hasOutput = true;
    if (returnCode == true || hasOutput == true)
    {
      Vector<String> setters = new Vector<String>();
      writeln(0, format("class %s_response:", prototype.name));
      if (returnCode == true)
      {
        writeln(1, "returnCode: int");
        setters.addElement("self.returnCode = 0");
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Operation op = output.sizeOperation();
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        swaggerParameter(op, field, setters);
      }
      writeln(1, "def __init__(self):");
      for (int i=0; i<setters.size(); i++)
        writeln(2, setters.elementAt(i));
      writeln(1, "def load(self, response):");
      writeln(2, "load_response(self, response)");
      writeln();
    }
  }
}

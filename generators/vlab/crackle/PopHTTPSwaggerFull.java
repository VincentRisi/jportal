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

public class PopHTTPSwaggerFull extends Generator
{
  public static String description()
  {
    return "Generates HTTP Swagger Restful 2.0 YAML (AIX|LINUX|WINDOWS)";
  }

  public static String documentation()
  {
    return "Generates HTTP Swagger Restful 2.0 YAML (AIX|LINUX|WINDOWS)";
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
    } catch (Exception ex)
    {
      properties = null;
    }
    getSwaggerPragmas(module);
    // generateSwaggerDefinitions(module, output, outLog);
    generateSwagger(module, output, outLog);
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

  private static void getSwaggerPragmas(Module module)
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
      n = pragma.indexOf("description:");
      if (n == 0)
      {
        description = pragma.substring(12).trim();
        continue;
      }
    }
  }

  private static void generateSwagger(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + "_swagger.yaml");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + "_swagger.yaml");
      writer = new PrintWriter(outFile);
      indent_size = 2;
      try
      {
        writeln("#%YAML 1.2");
        writeln("#---");
        writeln("# This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("swagger: \"2.0\"");
        writeln("info:");
        writeln(1, "title: " + module.name);
        if (description != null)
          writeln(1, "description: " + description);
        writeln(1, "version: " + module.version);
        generateDefinitions(module);
        generatePaths(module);
        writeln("#...");
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

  private static void generatePaths(Module module)
  {
    writeln(0, "consumes:");
    writeln(1, "- application/json");
    writeln(0, "produces:");
    writeln(1, "- application/json");
    writeln("paths:");
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
  
  private static boolean fileExists(String fileName)
  {
    try
    {
      File f = new File(fileName);
      return f.exists();
    }
    catch (Exception e)
    {
      outLog.println(format("%s - Exception", fileName));
      return false;
    }
  }

  private static void generateDefinitions(Module module)
  {
    writeln("definitions:");
    writeln(1, "tError:");
    writeln(2, "type: object");
    writeln(2, "properties: # fields");
    writeln(3, "error:");
    writeln(4, "type: string");
    writeln(4, "maxLength: 4096");
    generateSwaggerDefinitions(module);
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
      String name = format("%s%s/%s", urlPrefix, defSqlSub, table);
      String yaml2File = format("%s.yaml2", name);
      try
      {
        if (fileExists(yaml2File) == false)
          yaml2File = format("%s.yaml", name);
        done.put(structName, table);
        Reader input = new FileReader(yaml2File);
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        int state = 0;
        while ((line = reader.readLine()) != null)
        {
          switch (state)
          {
          case 0:
            if (line.contains(structName) == true)
            {
              state = 1;
              writeln(line);
            }
            break;
          case 1:
            if (line.compareTo("...") == 0 || line.charAt(2) != ' ')
              state = 2;
            else
              writeln(line);
            break;
          }
          if (state == 2)
            break;
        }
        reader.close();
        input.close();
      } catch (IOException e)
      {
        outLog.println(format("%s - IOException", yaml2File));
      }
    }
  }

  private static String lastPath = "";

  private static void generateRequest(Module module, Prototype prototype)
  {
    boolean doInBody = false;
    OpenApi openApi = PopHTTP.getOpenApi(module, prototype);
    String path = openApi.path;
    String operation = openApi.getType();
    String tags = openApi.tags == null ? path : openApi.tags;
    if (path.indexOf('/') == -1)
      doInBody = true;
    if (lastPath.compareTo(path) != 0)
    {  
      lastPath = path;
      writeln(1, format("/%s:", path));
    }
    writeln(2, format("%s:", operation));
    writeln(3, "tags:");
    writeln(4, format("- %s", tags));
    writeln(3, format("operationId: %s", prototype.name));
    writeln(3, format("summary: %s", prototype.name));
    if (doInBody == true)
      generateInBody(module, prototype);
    else
      generateInOther(module, prototype, path);
  }

  private static void generateInOther(Module module, Prototype prototype, String path)
  {
    boolean usePath = path.indexOf('{') > 0;
    if (prototype.inputs.size() > 0)
    {
      writeln(3, "parameters:");
      for (int i = 0; i < prototype.inputs.size(); i++)
      {
        Action input = (Action) prototype.inputs.elementAt(i);
        Operation op = input.sizeOperation();
        Field field = input.getParameter(prototype);
        if (field == null)
          continue;
        writeln(4, format("- name: %s", field.name));
        if (usePath == true)
          writeln(5, "in: path");
        else
          writeln(5, "in: query");
        writeln(5, format("description: %s", field.name));
        writeln(5, "required: true");
        swaggerParameter(op, field, 5);
      }
    }
  }

  private static void generateInBody(Module module, Prototype prototype)
  {
    if (prototype.inputs.size() > 0)
    {
      writeln(3, "parameters:");
      writeln(4, "- in: body");
      writeln(5, format("name: %s_request", prototype.name));
      writeln(5, format("description: %s Request", prototype.name));
      writeln(5, "schema:");
      writeln(6, format("title: %s_request", prototype.name));
      writeln(6, "type: object");
      boolean required_header = false;
      for (int i = 0; i < prototype.inputs.size(); i++)
      {
        Action input = (Action) prototype.inputs.elementAt(i);
        Field field = input.getParameter(prototype);
        if (field == null)
          continue;
        if (required_header == false)
        {
          writeln(6, "required:");
          required_header = true;
        }
        writeln(7, format("- %s", field.name));
      }
      writeln(6, "properties: # inputs");
      for (int i = 0; i < prototype.inputs.size(); i++)
      {
        Action input = (Action) prototype.inputs.elementAt(i);
        Operation op = input.sizeOperation();
        Field field = input.getParameter(prototype);
        if (field == null)
          continue;
        writeln(7, format("%s:", field.name));
        swaggerParameter(op, field, 8);
      }
    }
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
    writeln(3, "responses:");
    if (returnCode == true || hasOutput == true)
    {
      writeln(4, "'200':");
      writeln(5, "description: Success");
      writeln(5, "schema:");
      writeln(6, format("title: %s_response", prototype.name));
      writeln(6, "type: object");
      writeln(6, "properties:");
      if (returnCode == true)
      {
        writeln(7, "returnCode:");
        writeln(8, "type: integer");
        writeln(8, "format: int32");
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Operation op = output.sizeOperation();
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        writeln(7, format("%s:", field.name));
        swaggerParameter(op, field, 8);
      }
      boolean required_header = false;
      if (returnCode == true)
      {
        writeln(6, "required:");
        required_header = true;
        writeln(7, "- returnCode");
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        if (required_header == false)
        {
          writeln(6, "required:");
          required_header = true;
        }
        writeln(7, format("- %s", field.name));
      }
    } else
    {
      writeln(4, "'204':");
      writeln(5, "description: No response here");
    }
    writeln(4, "'400':");
    writeln(5, "description: Bad Request");
    writeln(5, "schema:");
    writeln(6, "$ref: '#/definitions/tError'");
  }

  private static void swaggerParameter(Operation op, Field field, int ind)
  {
    switch (field.type.typeof)
    {
    case Type.USERTYPE:
      if (field.type.reference == Type.BYREFPTR)
      {
        if (op != null)
        {
          writeln(ind, "type: array");
          writeln(ind, "items:");
          writeln(ind + 1, format("$ref: '#/definitions/%s'", field.type.name));
        } else
          writeln(ind, format("$ref: '#/definitions/%s'", field.type.name));
      } else if (field.type.reference == Type.BYPTR)
      {
        if (op != null)
        {
          writeln(ind, "type: array");
          writeln(ind, "items:");
          writeln(ind + 1, format("$ref: '#/definitions/%s'", field.type.name));
        } else
          writeln(ind, format("$ref: '#/definitions/%s'", field.type.name));
      } else
        writeln(ind, format("$ref: '#/definitions/%s'", field.type.name));
      break;
    case Type.CHAR:
    case Type.WCHAR:
    case Type.STRING:
      writeln(ind, "type: string");
      if (field.type.arraySizes.size() > 0)
      {
        Integer integer = (Integer) field.type.arraySizes.elementAt(0);
        writeln(ind, format("maxLength: %d", integer.intValue() - 1));
      }
      break;
    case Type.BYTE:
      writeln(ind, "type: integer");
      writeln(ind, "format: int8");
      break;
    case Type.BOOLEAN:
    case Type.INT:
      writeln(ind, "type: integer");
      writeln(ind, "format: int32");
      break;
    case Type.SHORT:
      writeln(ind, "type: integer");
      writeln(ind, "format: int16");
      break;
    case Type.LONG:
      writeln(ind, "type: integer");
      writeln(ind, "format: int64");
      break;
    case Type.FLOAT:
    case Type.DOUBLE:
      writeln(ind, "type: number");
      writeln(ind, "format: double");
      break;
    case Type.VOID:
    default:
      break;
    }
  }

  private static void generateSwaggerDefinitions(Module module)
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
          writeln(1, format("%s:", structure.name));
          writeln(2, "type: object");
          writeln(2, "properties: # fields");
          int ind = 3;
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            writeln(ind, format("%s:", field.name));
            switch (field.type.typeof)
            {
            case Type.CHAR:
            case Type.WCHAR:
            case Type.STRING:
              writeln(ind + 1, "type: string");
              if (field.type.arraySizes.size() > 0)
              {
                Integer integer = (Integer) field.type.arraySizes.elementAt(0);
                writeln(ind + 1, format("maxLength: %d", integer.intValue() - 1));
              }
              break;
            case Type.BYTE:
              writeln(ind + 1, "type: integer");
              writeln(ind + 1, "format: int8");
              break;
            case Type.BOOLEAN:
            case Type.INT:
              writeln(ind + 1, "type: integer");
              writeln(ind + 1, "format: int32");
              break;
            case Type.SHORT:
              writeln(ind + 1, "type: integer");
              writeln(ind + 1, "format: int16");
              break;
            case Type.LONG:
              writeln(ind + 1, "type: integer");
              writeln(ind + 1, "format: int64");
              break;
            case Type.FLOAT:
            case Type.DOUBLE:
              writeln(ind + 1, "type: number");
              writeln(ind + 1, "format: double");
              break;
            case Type.USERTYPE:
              writeln(ind + 1, format("$ref: '#/definitions/%s'", field.type.name));
              break;
            case Type.VOID:
              writeln(ind + 1, "$ type: void");
              break;
            default:
              break;
            }
          }
          boolean required_header = false;
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            switch (field.type.typeof)
            {
            case Type.BYTE:
            case Type.BOOLEAN:
            case Type.INT:
            case Type.SHORT:
            case Type.LONG:
            case Type.FLOAT:
            case Type.DOUBLE:
              if (required_header == false)
              {
                writeln(2, "required:");
                required_header = true;
              }
              writeln(3, format("- %s", field.name));
              break;
            default:
              break;
            }
          }
          continue;
        }
      }
    } catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }

  public static void generateCStructs(Module module, boolean doSwap)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.codeType != Structure.NORMAL)
        continue;
      if (structure.fields.size() > 0)
      {
        writeln("struct " + structure.name + "_http : public " + structure.name);
        writeln("{");
        writeln(1, "void BuildData(DataBuilder &dBuild, char *name=\"+structure.name+\")");
        writeln(1, "{");
        writeln(2, "dBuild.name(name);");
        writeln(2, "dBuild.count(" + structure.fields.size() + "); // requires _DATABUILD2_H_");
        for (int j = 0; j < structure.fields.size(); j++)
        {
          Field field = (Field) structure.fields.elementAt(j);
          String name = field.name;
          switch (field.type.typeof)
          {
          case Type.USERTYPE:
            break;
          case Type.CHAR:
            writeln(2, "dBuild.add(\"" + name + "\", " + name + ", sizeof(" + name + "), \"\");");
            break;
          case Type.BYTE:
          case Type.BOOLEAN:
          case Type.INT:
          case Type.SHORT:
          case Type.LONG:
          case Type.FLOAT:
          case Type.DOUBLE:
            writeln(2, "dBuild.add(\"" + name + "\", " + name + ", \"\");");
            break;
          case Type.VOID:
            break;
          case Type.STRING:
            break;
          case Type.WCHAR:
            break;
          default:
            break;
          }
        }
        writeln(1, "}");
        writeln(1, "void SetData(DataBuilder &dBuild, char *name=\"+structure.name+\")");
        writeln(1, "{");
        writeln(2, "dBuild.name(name);");
        writeln(2, "dBuild.count(" + structure.fields.size() + "); // requires _DATABUILD2_H_");
        for (int j = 0; j < structure.fields.size(); j++)
        {
          Field field = (Field) structure.fields.elementAt(j);
          String name = field.name;
          switch (field.type.typeof)
          {
          case Type.USERTYPE:
            break;
          case Type.CHAR:
            writeln(2, "dBuild.set(\"" + name + "\", " + name + ", sizeof(" + name + "), \"\");");
            break;
          case Type.BYTE:
          case Type.BOOLEAN:
            writeln(2, "dBuild.set(\"" + name + "\", " + name + ", sizeof(" + name + "), \"\");");
            break;
          case Type.INT:
          case Type.SHORT:
          case Type.LONG:
          case Type.FLOAT:
          case Type.DOUBLE:
            writeln(2, "dBuild.set(\"" + name + "\", " + name + ", sizeof(" + name + "), \"\");");
            break;
          case Type.VOID:
            break;
          case Type.STRING:
            break;
          case Type.WCHAR:
            break;
          default:
            break;
          }
        }
        writeln(1, "}");
      }
    }
    writeln("};");
    writeln();
  }
}

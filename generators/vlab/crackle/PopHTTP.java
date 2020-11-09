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

import java.io.PrintWriter;

public class PopHTTP extends Generator
{
  public static PrintWriter errLog = new PrintWriter(System.out);

  public static void generateCExterns(Module module, PrintWriter outData)
  {
    boolean done = false;
    for (int i = 0; i < module.structures.size(); i++)
    {
      boolean found = false;
      Structure struct1 = (Structure) module.structures.elementAt(i);
      if (struct1.codeType != Structure.NORMAL)
        continue;
      if (struct1.header.length() > 0)
      {
        for (int j = 0; j < i; j++)
        {
          Structure struct2 = (Structure) module.structures.elementAt(j);
          if (struct2.header.compareTo(struct1.header) == 0)
          {
            found = true;
            break;
          }
        }
        if (found == false)
        {
          outData.println("#include " + struct1.header);
          done = true;
        }
      }
    }
    if (done)
      outData.println();
  }

  public static void generateCStructs(Module module, PrintWriter outData, boolean doSwap)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.codeType != Structure.NORMAL)
        continue;
      if (structure.fields.size() > 0)
      {
        outData.println("struct " + structure.name + "_http : public " + structure.name);
        outData.println("{");
        outData.println("  void BuildData(DataBuilder &dBuild, char *name=\"+structure.name+\")");
        outData.println("  {");
        outData.println("    dBuild.name(name);");
        outData.println("    dBuild.count("+structure.fields.size()+"); // requires _DATABUILD2_H_");
        for (int j = 0; j < structure.fields.size(); j++)
        {
          Field field = (Field) structure.fields.elementAt(j);
          String name=field.name;
          switch (field.type.typeof)
          {
          case Type.USERTYPE:
            break;
          case Type.CHAR:
            outData.println("    dBuild.add(\""+name+"\", "+name+", sizeof(" + name + "), \"\");");
            break;
          case Type.BYTE:
          case Type.BOOLEAN:
          case Type.INT:
          case Type.SHORT:
          case Type.LONG:
          case Type.FLOAT:
          case Type.DOUBLE:
            outData.println("    dBuild.add(\""+name+"\", "+name+", \"\");");
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
        outData.println("  }");
        outData.println("  void SetData(DataBuilder &dBuild, char *name=\"+structure.name+\")");
        outData.println("  {");
        outData.println("    dBuild.name(name);");
        outData.println("    dBuild.count("+structure.fields.size()+"); // requires _DATABUILD2_H_");
        for (int j = 0; j < structure.fields.size(); j++)
        {
          Field field = (Field) structure.fields.elementAt(j);
          String name=field.name;
          switch (field.type.typeof)
          {
          case Type.USERTYPE:
            break;
          case Type.CHAR:
            outData.println("    dBuild.set(\""+name+"\", "+name+", sizeof(" + name + "), \"\");");
            break;
          case Type.BYTE:
          case Type.BOOLEAN:
            outData.println("    dBuild.set(\""+name+"\", "+name+", sizeof(" + name + "), \"\");");
            break;
          case Type.INT:
          case Type.SHORT:
          case Type.LONG:
          case Type.FLOAT:
          case Type.DOUBLE:
            outData.println("    dBuild.set(\""+name+"\", "+name+", sizeof(" + name + "), \"\");");
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
        outData.println("  }");
      }
    }
    outData.println("};");
    outData.println();
  }

  public static void generateCHeader(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print("  " + prototype.type.cDef(prototype.name, false) + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDef(parameter.name, false));
      w1 = ", ";
    }
    outData.println(");");
  }

  public static void generateCInterface(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print("  virtual " + prototype.type.cDef(prototype.name, false) + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDef(parameter.name, false));
      w1 = ", ";
    }
    outData.println(") = 0;");
  }

  public static void generateVirtualCHeader(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print("  virtual " + prototype.type.cDef(prototype.name, false) + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDef(parameter.name, false));
      w1 = ", ";
    }
    outData.println(") = 0;");
  }

  public static void generateCImplCode(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print(prototype.type.cDef("H" + module.name + "::" + prototype.name, false) + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDef(parameter.name, false));
      w1 = ", ";
    }
    outData.println(")");
  }
  
  static final String operations = "'POST'GET'PUT'DELETE'OPTIONS'HEAD'PATCH'TRACE'";

  public static OpenApi getOpenApi(Module module, Prototype prototype)
  {
    OpenApi result = prototype.openApi;
    if (result == null)
    {
      result = new OpenApi();
      String name = prototype.name;
      String lookup = format(" %s:", name);
      for (int i=0; i<module.pragmas.size();i++)
      {
        String pragma = ((String)module.pragmas.elementAt(i));
        if (pragma.contains(lookup) == true)
        {
          String parm = (pragma.substring(lookup.length())).trim();
          String[] args = parm.split(" ");
          if (args.length == 1)
          {
            result.path = parm;
            break;
          }
          String typeof = args[0].toUpperCase();
          if (operations.contains(format("'%s'", typeof)) == false)
            result.typeof = OpenApi.POST;
          else if (typeof.compareTo("POST") == 0)
            result.typeof = OpenApi.POST;
          else if (typeof.compareTo("GET") == 0)
            result.typeof = OpenApi.GET;
          else if (typeof.compareTo("PUT") == 0)
            result.typeof = OpenApi.PUT;
          else if (typeof.compareTo("DELETE") == 0)
            result.typeof = OpenApi.DELETE;
          else if (typeof.compareTo("OPTIONS") == 0)
            result.typeof = OpenApi.OPTIONS;
          else if (typeof.compareTo("HEAD") == 0)
            result.typeof = OpenApi.HEAD;
          else if (typeof.compareTo("PATCH") == 0)
            result.typeof = OpenApi.PATCH;
          else if (typeof.compareTo("TRACE") == 0)
            result.typeof = OpenApi.TRACE;
          if (args.length > 2)
            result.tags  = args[2];
          result.path = args[1];
          break;
        }
      }
    }
    return result;
  }
}

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
package bbd.crackle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class PopUbiCCIdl extends Generator
{
  public static String description()
  {
    return "Generates Client Call .ic Code";
  }

  public static String documentation()
  {
    return "Generates Client Call .ic Code";
  }

  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream inpStr = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module)inpStr.readObject();
        inpStr.close();
        generate(module, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Generates - Idl Client Server Call Code
   */
  private static StringBuffer lookupBuffer;

  public static void generate(Module module, String output, PrintWriter outLog)
  {
    outLog.println(module.name + " version " + module.version);
    lookupBuffer = new StringBuffer();
    generateICS(module, output, outLog);
    generateCSStructs(module, output, outLog);
    generatePyStructs(module, output, outLog);
  }

  private static void generateICS(Module module, String output,
      PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + ".ic");
      OutputStream outFile = new FileOutputStream(output
          + module.name.toLowerCase() + ".ic");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        String modname = module.name.toLowerCase();
        outData.println("/*");
        outData.println(" * idl module name:" + module.name);
        outData.println(" * -- run __" + modname + "->Logon(<service>, <host>, <timeout>) in startup code");
        outData.println(" *    supplying <host> <service> and <timeout> values retrieved from");
        outData.println(" *    your config file or some other sneaky place.");
        outData.println(" */");
        outData.println();
        outData.println("code");
        outData.println("HEADER:#include \"" + modname + "client.h\"");
        outData.println("HEADER:extern T" + module.name + "* __" + modname + ";");
        outData.println("static T" + module.name + " __" + modname + "_impl;");
        outData.println("T" + module.name + "* __" + modname + " = &__" + modname + "_impl;");
        outData.println("endcode");
        outData.println();
        outData.println("public void " + modname + "_CC_Logon(char* host, char* service, int timeout)");
        outData.println("{");
        outData.println("message:#");
        outData.println("input:host;service;timeout;");
        outData.println("code");
        outData.println("   __" + modname + "->Logon(service, host, timeout);");
        outData.println("endcode");
        outData.println("}");
        outData.println("void " + modname + "_CC_Logoff()");
        outData.println("{");
        outData.println("message:#");
        outData.println("code");
        outData.println("   __" + modname + "->Logoff();");
        outData.println("endcode");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateICS(module, prototype, outData);
        }
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }

  private static void generateUsings(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() > 0) continue;
      if (structure.header.toLowerCase().indexOf(".sh") == structure.header.length() - 4)
      {
        outData.println("using Bbd.Idl2.DBPortal;");
        break;
      }
    }
  }
  private static String dropTee(String in, String modName)
  {
    String result = in;
    if (in.compareTo("char") == 0)
    {
      result = "string";
    }
    return result;
  }
  private static void generateCSStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() <= 0) continue;
      if (structure.name.compareTo(module.name) == 0) continue;
      outData.println("  [Serializable()]");
      outData.println("  public class " + dropTee(structure.name, module.name));
      outData.println("  {");
      int relativeOffset = 0;
      int fillerNo = 0;
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        int n = relativeOffset % 8;
        if (n > 0)
        {
          n = 8 - n;
          outData.println("    [Field(Size=" + n + ")] byte[] filler" + fillerNo + " = new byte[" + n + "];");
          fillerNo++;
          relativeOffset += n;
        }
        outData.println("    " + field.type.csAttribute(field.isStruct(module)) + "public " + field.type.csStructDef(field.nameLowerFirst(), field.isStruct(module)) + ";");
        if (field.isStruct(module) == false)
          outData.println("    public " + field.type.csStructDef(field.nameUpperFirst(), field.isStruct(module))
            + "{ get {return " + field.nameLowerFirst() + ";} set {" + field.nameLowerFirst() + " = value;} }");
        relativeOffset += field.type.relativeSize(true);
      }
      int n = relativeOffset % 8;
      if (n > 0)
      {
        n = 8 - n;
        outData.println("    [Field(Size=" + n + ")] byte[] _fill" + fillerNo
            + " = new byte[" + n + "];");
      }
      outData.println("  }");
    }
  }
  private static void generateCSStructs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name + "Client.cs");
      OutputStream outFile = new FileOutputStream(output + module.name + "Client.cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        generateUsings(module, outData);
        outData.println();
        outData.println("namespace Bbd.Idl2.AnyDB");
        outData.println("{");
        generateCSStructs(module, outData);
        generateCSEnums(module, outData);
        outData.println("}");
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateCSEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table)module.tables.elementAt(i);
      outData.println("  public enum " + table.name);
      String w1 = "  { ";
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message)table.messages.elementAt(j);
        outData.println(w1 + message.name);
        w1 = "  , ";
      }
      outData.println("  }");
      outData.println();
    }
    for (int i = 0; i < module.enumerators.size(); i++)
    {
      Enumerator entry = (Enumerator)module.enumerators.elementAt(i);
      outData.println("  public enum " + entry.name);
      String w1 = "  { ";
      for (int j = 0; j < entry.elements.size(); j++)
      {
        String element = (String)entry.elements.elementAt(j);
        outData.println(w1 + element);
        w1 = "  , ";
      }
      outData.println("  }");
      outData.println();
    }
  }
  private static void generatePyStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() <= 0) continue;
      if (structure.name.compareTo(module.name) == 0) continue;
      outData.println("class " + structure.name + "(object):");
      outData.println("    def _make(self): return " + structure.name + "()");
      outData.println("    def _name(self): return ['" + structure.name + "']");
      String slots =  "    __slots__ = [";
      String comma = "";
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        slots += comma + "'" + field.name + "'";
        comma = ", ";
      }
      slots += "]";
      outData.println(slots);
      outData.println("    def __init__(self):");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        outData.println("        self."+field.name+" = ''");
      }
      outData.println("    def _fromList(self, _result):");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        outData.println("        self."+field.name+" = _result[" + j + "]");
      }
      outData.println("    def _toList(self):");
      outData.print(  "        _result = [");
      String padder = "";
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        if (padder.length() > 0)
          outData.println(",");
        outData.print(padder + "str(self."+field.name+")");
        padder="            ";
      }
      outData.println("]");
      outData.println("        return _result");
    }
  }
  private static void generatePyEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table)module.tables.elementAt(i);
      outData.println("class " + table.name + ":");
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message)table.messages.elementAt(j);
        outData.println("    " + message.name + " = '" + message.name + "'");
      }
      outData.println();
    }
    for (int e = 0; e < module.enumerators.size(); e++)
    {
      Enumerator enumerator = (Enumerator)module.enumerators.elementAt(e);
      outData.println(enumerator.name + " = {}");
      for (int x = 0; x < enumerator.elements.size(); x++)
      {
        String element = (String)enumerator.elements.elementAt(x);
        int n = element.indexOf('=');
        if (n > 0)
        {
          String key = element.substring(0, n - 1);
          String value = element.substring(n + 2);
          outData.println(enumerator.name + "['" + key + "'] = " + value);
          outData.println(enumerator.name + "[" + value + "] = '" + key + "'");
        }
        else
        {
          outData.println(enumerator.name + "['" + element + "'] = " + x);
          outData.println(enumerator.name + "[" + x + "] = '" + element + "'");
        }
      }
      outData.println();
    }
  }
  private static void generatePyStructs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String name = output + "idl_" + module.name.toLowerCase() + "_ic.py";
      outLog.println("Code: " + name);
      OutputStream outFile = new FileOutputStream(name);
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("# -*- coding: iso-8859-1 -*-");
        outData.println("## This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("## Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        generatePyEnums(module, outData);
        generatePyStructs(module, outData);
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateICS(Module module, Prototype prototype,
      PrintWriter outData)
  {
    String modname = module.name.toLowerCase();
    boolean hasInput = false;
    boolean hasOutput = false;
    outData.println("/*");
    outData.println(" * " + prototype.name + " " + prototype.type.name + " (i:"
        + prototype.inputs.size() + ", o:" + prototype.outputs.size() + ")");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field f = (Field)prototype.parameters.elementAt(i);
      outData.println(" *  "
          + f.name
          + " "
          + f.type.name
          + (f.isInput ? " i " + (f.input != null ? f.input.getSizeName() : "")
              : "")
          + (f.isOutput ? " o "
              + (f.output != null ? f.output.getSizeName() : "") : "")
          + (f.hasSize ? " size" : ""));
      if (f.isInput == true)
        hasInput = true;
      if (f.isOutput == true)
        hasOutput = true;
    }
    outData.println(" */");
    outData.println();
    boolean doneIt = false;
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field f = (Field)prototype.parameters.elementAt(i);
      if (f.type.typeof == Type.USERTYPE && doStruct(f.type.name) == true)
      {
        generateStruct(module, f.type.name, outData);
        doneIt = true;
      }
    }
    if (doneIt == true)
      outData.println();
    String comma = "";
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
        && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    outData.print(prototype.type.name + " " + modname + "_" + prototype.name
        + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field f = (Field)prototype.parameters.elementAt(i);
      outData.print(comma + defType(f) + " " + f.name);
      comma = ", ";
    }
    outData.println(")");
    outData.println("{");
    outData.println("message:#");
    if (hasInput == true)
    {
      outData.print("input:");
      for (int i = 0; i < prototype.parameters.size(); i++)
      {
        Field f = (Field)prototype.parameters.elementAt(i);
        if (f.isInput == true)
          outData.print(" " + ofInputAction(f) + ";");
      }
      outData.println();
    }
    if (hasOutput == true)
    {
      outData.print("output:");
      for (int i = 0; i < prototype.parameters.size(); i++)
      {
        Field f = (Field)prototype.parameters.elementAt(i);
        if (f.isOutput == true)
          outData.print(" " + ofOutputAction(f) + ";");
      }
      outData.println();
    }
    outData.println("code");
    outData.println("  try");
    outData.println("  {");
    outData.print("    " + (hasReturn == true ? "return " : "") + "__" + modname
        + "->" + prototype.name + "(");
    comma = "";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field f = (Field)prototype.parameters.elementAt(i);
      outData.print(comma + f.name);
      comma = ", ";
    }
    outData.println(");");
    outData.println("  }");
    outData.println("  catch (xCept ex)");
    outData.println("  {");
    outData.println("    logFile->Log(ex);");
    outData.println("    throw;");
    outData.println("  }");
    outData.println("  catch (...)");
    outData.println("  {");
    outData.println("    THROW(UNHANDLED_ERROR);");
    outData.println("  }");
    outData.println("endcode");
    outData.println("}");
  }

  private static void generateStruct(Module module, String name, PrintWriter outData)
  {
    String modname = module.name.toLowerCase();
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.name.compareTo(name) != 0)
        continue;
      if (structure.fields.size() == 0)
        outData.println("struct " + name + " " + structure.header);
      else
        outData.println("struct " + name + " \"" + modname + "client.h\"");
      break;
    }
  }

  private static String ofInputAction(Field field)
  {
    String result = field.name;
    if (field.input.hasSize())
      return result + " size(" + field.input.getSizeName() + ")";
    return result;
  }

  private static String ofOutputAction(Field field)
  {
    String result = field.name;
    if (field.output.hasSize())
      return result + " size(" + field.output.getSizeName() + ")";
    return result;
  }

  private static String defType(Field field)
  {
    String result = field.type.name;
    if (field.type.reference == Type.BYPTR)
      return result + "*";
    if (field.type.reference == Type.BYREFPTR)
      return result + "*&";
    return result;
  }

  private static boolean doStruct(String name)
  {
    String lookup = "[" + name + "]";
    if (lookupBuffer.toString().indexOf(lookup) == -1)
    {
      lookupBuffer.append(lookup);
      return true;
    }
    return false;
  }
}

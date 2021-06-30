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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbi3Python extends Generator
{
  public static String description()
  {
    return "Generates Client Python IDL_xxx Code";
  }
  public static String documentation()
  {
    return "Generates Client Python IDL_xxx Code";
  }
  /*
   *  Static constructor
   */
  private static boolean useAsImport = true;
  /**
  * Reads input from stored repository
  */
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module)in.readObject();
        generate(module, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  private static PrintWriter _outLog;
  /**
  * Generates
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    indent_size = 4;
    _outLog = outLog;
    outLog.println(module.name + " version " + module.version);
    generateStructs(module, output, outLog);
    generateStubs(module, output, outLog);
  }
  private static Vector useList;
  public PopUbi3Python()
  {}
  private static class Usage
  {
    public String name;
    public String module;
    boolean used;
    Usage(String name, String module)
    {
      this.name = name;
      this.module = module;
      used = false;
    }
  }
  private static boolean isUsed(String name)
  {
    boolean result = false;
    for (int i = 0; i < useList.size(); i++)
    {
      Usage nameUsage = (Usage)useList.elementAt(i);
      if (nameUsage.name.compareTo(name) == 0)
      {
        for (int j = 0; j < useList.size(); j++)
        {
          Usage moduleUsage = (Usage)useList.elementAt(j);
          if (moduleUsage.module.compareTo(nameUsage.module) == 0)
          {
            if (moduleUsage.used == true)
            {
              result = true;
              break;
            }
          }
        }
      }
    }
    return result;
  }
  private static void setUsed(String name)
  {
    for (int i = 0; i < useList.size(); i++)
    {
      Usage nameUsage = (Usage)useList.elementAt(i);
      if (nameUsage.name.compareTo(name) == 0)
      {
        for (int j = 0; j < useList.size(); j++)
        {
          Usage moduleUsage = (Usage)useList.elementAt(j);
          if (moduleUsage.module.compareTo(nameUsage.module) == 0)
            moduleUsage.used = true;
        }
      }
    }
  }
  private static void addNoField(String name, String header)
  {
    if (header.length() > 0)
    {
      String module = "DB_" + header.toUpperCase().substring(1, header.length() - 4);
      useList.addElement(new Usage(name, module));
    }
    else
      _outLog.println("struct " + name + " definition has no fields or which header to use for it.");
  }
  private static String usageName(String name)
  {
    for (int i = 0; i < useList.size(); i++)
    {
      Usage usage = (Usage) useList.elementAt(i);
      if (usage.name.compareTo(name) == 0)
        return usage.module;
    }
    return name+"_CheckYourSpelling_OrAddA_struct";
  }
  private static String usageQualified(String name)
  {
    for (int i = 0; i < useList.size(); i++)
    {
      Usage usage = (Usage)useList.elementAt(i);
      if (usage.name.compareTo(name) == 0)
        return usage.module+"."+name;
    }
    return name + "_CheckYourSpelling_OrAddA_struct";
  }
  /**
  * Sets up the writer and generates the general stuff
  */
  private static void generateStructs(Module module, String output, PrintWriter outLog)
  {
    useList = new Vector();
    try
    {
      String sourceName = "IDL_" + module.name.toUpperCase();
      outLog.println("Code: " + output + sourceName + ".py");
      OutputStream outFile = new FileOutputStream(output + sourceName + ".py");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("## This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("## Mutilation, Spindlization and Bending will result in ...");
        for (int e = 0; e < module.enumerators.size(); e++)
        {
          Enumerator enumerator = (Enumerator)module.enumerators.elementAt(e);
          writeln(enumerator.name + " = {}");
          for (int x = 0; x < enumerator.elements.size(); x++)
          {
            String element = (String)enumerator.elements.elementAt(x);
            int n = element.indexOf('=');
            if (n > 0)
            {
              String key = element.substring(0, n-1);
              String value = element.substring(n+2);
              writeln(enumerator.name + "['" + key + "'] = " + value);
              writeln(enumerator.name + "[" + value + "] = '" + key + "'");
            }
            else
            {
              writeln(enumerator.name + "['" + element + "'] = " + x);
              writeln(enumerator.name + "[" + x + "] = '" + element + "'");
            }
          }
          writeln();
        }
        for (int s = 0; s < module.structures.size(); s++)
        {
          Structure structure = (Structure)module.structures.elementAt(s);
          if (structure.codeType != Structure.NORMAL)
          {
            continue;
          }
          if (structure.fields.size() == 0)
          {
            addNoField(structure.name, structure.header);
            continue;
          }
          useList.addElement(new Usage(structure.name, sourceName));
          writeln("## \\class " + structure.name);
          writeln("class " + structure.name + "(object):");
          writeln("    def _make(self): return " + structure.name + "()");
          writeln("    def _name(self): return ['" + structure.name + "']");
          write("    __slots__ = [");
          for (int f = 0; f < structure.fields.size(); f++)
          {
            Field field = (Field)structure.fields.elementAt(f);
            if (f != 0)
              write(", ");
            write("'" + field.name + "'");
          }
          writeln("]");
          writeln("    def __init__(self):");
          for (int f = 0; f < structure.fields.size(); f++)
          {
            Field field = (Field)structure.fields.elementAt(f);
            writeln("        self." + field.name + " = '' ## \\field " + field.name + ":" + typeName(field));
          }
          writeln("    def _fromList(self, _result):");
          for (int f = 0; f < structure.fields.size(); f++)
          {
            Field field = (Field)structure.fields.elementAt(f);
            writeln("        self." + field.name + " = _result[" + f + "]");
          }
          writeln("        return " + structure.fields.size());
          writeln("    def _toList(self):");
          write("        _result = [");
          for (int f = 0; f < structure.fields.size(); f++)
          {
            Field field = (Field)structure.fields.elementAt(f);
            if (f != 0)
              write(", ");
            write("str(self." + field.name + ")");
          }
          writeln("]");
          writeln("        return _result");
        }
        writeln();
      }
      finally
      {
        writer.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
    catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  /**
  * Sets up the writer and generates the general stuff
  */
  private static void generateStubs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toUpperCase() + "_CODE.py");
      OutputStream outFile = new FileOutputStream(output + module.name.toUpperCase() + "_CODE.py");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("# -*- coding: iso-8859-1 -*-");
        writeln("# This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("# Mutilation, Spindlization and Bending will result in ...");
        if (useAsImport == true)
          writeln("import " + module.name.toUpperCase() + "_IMPL");
        else
        {
          writeln();
          writeln("#use INTRINSICS");
          writeln();
          writeln("#use " + module.name.toUpperCase() + "_IMPL=" + module.name.toUpperCase() + "_IMPL");
        }
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.isRpcCall() && prototype.code.size() == 0)
          {
            generateMethodCall(module, prototype);
          }
        }
      }
      finally
      {
        writer.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
    catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static Vector pythonArgs;
  static class PythonArgs
  {
    String name;
    Field field;
    String sizeName;
    boolean isInput = false;
    boolean isOutput = false;
    boolean listType = false;
    PythonArgs()
    {
      sizeName = "";
    }
  }
  private static void addUse(Type type)
  {
    boolean listType = pyList(type.typeof);
    if (listType == true)
    {
      String name = typeof(type, "");
      if (isUsed(name) == false)
      {
        String module = usageName(name);
        if (useAsImport == true)
          writeln("import " + module);
        else
          writeln("#use " + module + "=" + module);
        setUsed(name);
      }
    }
  }
  private static void generateMethodCall(Module module, Prototype prototype)
  {
    pythonArgs = new Vector();
    String inputList = "";
    String inputComma = "";
    String returnList = "";
    String resultList = "";
    String resultComma = "";
    String callParms = "";
    String callComma = "";
    String innerComma = "";
    boolean hasOutputs = false;
    boolean hasReturn = false;
    callParms = "_signature";
    callComma = ", ";
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
    {
      addUse(prototype.type);
      resultList = "_result";
      resultComma = ", ";
      hasReturn = true;
    }
    String innerCallParms = "";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      PythonArgs arg = new PythonArgs();
      arg.field = field;
      arg.isInput = field.isInput;
      arg.isOutput = field.isOutput;
      arg.name = field.name;
      arg.listType = pyList(field.type.typeof);
      addUse(field.type);
      if (field.isInput == true && field.hasSize == true)
        arg.sizeName = field.input.getSizeName();
      else if (field.hasSize == true)
        arg.sizeName = field.output.getSizeName();
      pythonArgs.addElement(arg);
    }
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isInput)
      {
        callParms += callComma + arg.name;
        callComma = ", ";
        inputList = inputList + inputComma + arg.name + ":" + typeof(arg.field.type, " as list");
        inputComma = ", ";
        if (arg.listType == true && arg.sizeName.length() == 0)
          innerCallParms += innerComma + "_" + arg.name.toUpperCase() + "_ONE" + i;
        else if (arg.listType == true)
          innerCallParms += innerComma + "_" + arg.name.toUpperCase() + "_MANY" + i;
        else
          innerCallParms += innerComma + arg.name;
        innerComma = ", ";
      }
      if (arg.isOutput)
      {
        hasOutputs = true;
        if (arg.listType == true && arg.sizeName.length() == 0)
          resultList += resultComma + "_" + arg.name.toUpperCase() + "_ONE" + i;
        else if (arg.listType == true)
          resultList += resultComma + "_" + arg.name.toUpperCase() + "_MANY" + i;
        else
          resultList += resultComma + arg.name;
        returnList += resultComma + arg.name + ":" + typeof(arg.field.type, " as list");
        resultComma = ", ";
      }
    }
    writeln("def " + prototype.name + "(" + callParms + "):");
    writeln("    '''");
    writeln("    inputs  " + inputList);
    writeln("    returns " + (hasReturn == true ? "rc:int, " : "") + returnList);
    writeln("    '''");
    writeln("    if _signature != " + prototype.signature(true) 
      + ": raise AssertionError('Method %s signature incorrect (%d!=%d)' % ('" 
      + prototype.name + "', _signature, " + prototype.signature(true) + "))");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isInput == true)
      {
        writeln("    " + assertof(arg));
        if (arg.listType == true && arg.sizeName.length() == 0)
        {
          String recName = usageQualified(typeof(arg.field.type, ""));
          writeln("    _" + arg.name.toUpperCase() + "_ONE" + i + " = " + recName + "()");
          writeln("    _" + arg.name.toUpperCase() + "_ONE" + i + "._fromList(" + arg.name + ")");
        }
        else if (arg.listType == true)
        {
          String recName = usageQualified(typeof(arg.field.type, ""));
          writeln("    _" + arg.name.toUpperCase() + "_MANY" + i + " = []");
          writeln("    for _asLIST" + i + " in " + arg.name + ":");
          writeln("        _asSTRUCT" + i + " = " + recName + "()");
          writeln("        _asSTRUCT" + i + "._fromList(_asLIST" + i + ")");
          writeln("        _" + arg.name.toUpperCase() + "_MANY" + i + ".append(_asSTRUCT" + i + ")");
        }
      }
    }
    if (hasOutputs == true || hasReturn == true)
    {
      writeln("    " + resultList + " = " + module.name.toUpperCase() + "_IMPL." + prototype.name + "(" + innerCallParms + ")");
      resultComma = "";
      resultList = "";
      if (hasReturn == true)
      {
        resultList = "_result";
        resultComma = ", ";
      }
      for (int i = 0; i < pythonArgs.size(); i++)
      {
        PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
        if (arg.isOutput == true)
        {
          if (arg.listType == true && arg.sizeName.length() == 0)
            resultList += resultComma + "_" + arg.name.toUpperCase() + "_ONE" + i + "._toList()";
          else if (arg.listType == true)
          {
            writeln("    _" + arg.name.toUpperCase() + "_BUNCH = []");
            writeln("    for _" + arg.name.toUpperCase() + "_ONE in _" + arg.name.toUpperCase() + "_MANY" + i + ":");
            writeln("        _" + arg.name.toUpperCase() + "_BUNCH.append(_" + arg.name.toUpperCase() + "_ONE._toList())");
            resultList += resultComma + "_" + arg.name.toUpperCase() + "_BUNCH";
          }
          else
          {
            writeln("    " + assertof(arg));
            resultList += resultComma + arg.name;
          }
          resultComma = ", ";
        }
      }
    }
    else
      writeln("    " + module.name.toUpperCase() + "_IMPL." + prototype.name + "(" + innerCallParms + ")");
    if (resultList.length() > 0)
      writeln("    return " + resultList);
    writeln();
  }
  private static String assertof(PythonArgs arg)
  {
    String x = "";
    switch (arg.field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        x = "int";
        break;
      case Type.LONG:
        return "if not isinstance(" + arg.name + ", int) and not isinstance(" + arg.name + ", long): raise AssertionError('%s is not instance of int or long' % ('" + arg.name + "'))";
      case Type.FLOAT:
      case Type.DOUBLE:
        x = "float";
        break;
      case Type.CHAR:
      case Type.STRING:
        x = "str";
        break;
      case Type.USERTYPE:
      default:
        x = "list";
    }
    return "if not isinstance(" + arg.name + ", " + x + "): raise AssertionError('%s is not an instance of %s' % ('" + arg.name + "', '" + x + "'))";
  }
  public static boolean pyList(byte typeof)
  {
    switch (typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return false;
      default:
        return true;
    }
  }
  static String typeName(Field field)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
        return "byte";
      case Type.SHORT:
        return "short";
      case Type.INT:
        return "int";
      case Type.LONG:
        return "long";
      case Type.CHAR:
        return "char";
      case Type.DOUBLE:
        return "double";
    }
    return "<unsupported>";
  }
  private static String typeof(Type type, String asType)
  {
    switch (type.typeof)
    {
      case Type.USERTYPE:
        return type.name + asType;
      case Type.BOOLEAN:
      case Type.SHORT:
      case Type.BYTE:
      case Type.INT:
        return "int";
      case Type.LONG:
        return "long";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "float";
      case Type.CHAR:
      case Type.STRING:
        return "str";
      default:
        return type.name;
    }
  }
}



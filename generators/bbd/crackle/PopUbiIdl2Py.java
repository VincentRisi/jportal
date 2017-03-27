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

/**
 * @author vince
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class PopUbiIdl2Py extends Generator
{
  public static String description()
  {
    return "Generates Idl2 Client Python Intermediate Code";
  }
  public static String documentation()
  {
    return "Generates Idl2 Client Python Intermediate Code";
  }
  public static PrintWriter errLog;
  /**
   * Reads input from stored repository
   */
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      errLog = outLog;
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module) in.readObject();
        in.close();
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
   * Generates
   */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name + " version " + module.version);
    generateIDL2Python(module, output, outLog);
  }
  private static void generateIDL2Python(Module module, String output,
      PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name + "Idl2.py");
      OutputStream outFile = new FileOutputStream(output + module.name
          + "Idl2.py");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData
            .println("# This code was generated, do not modify it, modify it at source and regenerate it.");
        outData
            .println("# Mutilation, Spindlization and Bending will result in ...");
        generateIDL2PythonCall(module, outData);
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateIDL2PythonCall(Module module, PrintWriter outData)
  {
    outData.println();
    outData.println("class Structure(object):");
    outData.println("  __slots__  = ['name', 'codeType', 'fields']");
    outData.println("  def __init__(self, name, codeType, fields):");
    outData.println("    self.name = name");
    outData.println("    self.codeType = codeType");
    outData.println("    self.fields = fields");
    outData.println();
    outData.println("class Prototype(object):");
    outData.println("  __slots__  = ['name', 'type', 'parameters']");
    outData.println("  def __init__(self, name, type, parameters):");
    outData.println("    self.name = name");
    outData.println("    self.type = type");
    outData.println("    self.parameters = parameters");
    outData.println();
    outData.println("class Field(object):");
    outData.println("  __slots__ = ['name','type','isInput','isOutput']");
    outData
        .println("  def __init__(self, name, type, isInput=False, isOutput=False):");
    outData.println("    self.name = name");
    outData.println("    self.type = type");
    outData.println("    self.isInput = isInput");
    outData.println("    self.isOutput = isOutput");
    outData.println();
    outData.println("class Type(object):");
    outData.println("  __slots__ = ['name', 'typeof', 'reference']");
    outData.println("  def __init__(self, name, typeof, reference):");
    outData.println("    self.name = name");
    outData.println("    self.typeof = typeof");
    outData.println("    self.reference = reference");
    outData.println();
    outData.println("class Proc(object):");
    outData
        .println("  __slots__ = ['name', 'struct', 'table', 'type', 'extended', 'input', 'output']");
    outData
        .println("  def __init__(self, name, struct, table, type, extended, input, output):");
    outData.println("    self.name = name");
    outData.println("    self.struct = struct");
    outData.println("    self.table = table");
    outData.println("    self.type = type");
    outData.println("    self.extended = extended");
    outData.println("    self.input = input");
    outData.println("    self.output = output");
    outData.println();
    outData.println("USERTYPE =  0");
    outData.println("BOOLEAN  =  1");
    outData.println("CHAR     =  2");
    outData.println("SHORT    =  3");
    outData.println("LONG     =  4");
    outData.println("FLOAT    =  5");
    outData.println("DOUBLE   =  6");
    outData.println("VOID     =  7");
    outData.println("BYTE     =  8");
    outData.println("INT      =  9");
    outData.println("STRING   = 10");
    outData.println();
    outData.println("BYVAL    =  1");
    outData.println("BYPTR    =  2");
    outData.println("BYREF    =  3");
    outData.println("BYPTRPTR =  4");
    outData.println("BYREFPTR =  5");
    outData.println("ARRAYED  =  6");
    outData.println();
    outData.println("SINGLE   =  1");
    outData.println("MULTIPLE =  2");
    outData.println("ACTION   =  3");
    outData.println();
    outData.println("NORMAL   =  0");
    outData.println("PUBLIC   =  1");
    outData.println("PRIVATE  =  2");
    outData.println("PROTECTED=  3");

    outData.println();
    outData.println("structures = []");
    outData.println("prototypes = []");
    outData.println("procs      = []");
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() == 0) continue;
      generateStructure(structure, outData);
    }
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL) continue;
      generatePrototype(prototype, outData);
    }
  }
  private static void generateStructure(Structure structure, PrintWriter outData)
  {
    outData.println();
    outData.print("structures.append(Structure('" + structure.name + "', "
        + structType(structure.codeType) + ",");
    if (structure.fields.size() == 0)
    {
      outData.println(" [] ) )");
      return;
    }
    outData.println();
    String square = "[";
    for (int i = 0; i < structure.fields.size(); i++)
    {
      Field field = (Field) structure.fields.elementAt(i);
      outData.println("  " + square + " Field('" + field.name + "', Type('"
          + field.type.name + "', " + fieldType(field.type.typeof) + ", "
          + refType(field.type.reference) + "))"
          + (i + 1 < structure.fields.size() ? "," : " ] ) )"));
      square = " ";
    }
  }
  private static void generatePrototype(Prototype prototype, PrintWriter outData)
  {
    outData.println();
    outData.print("prototypes.append(Prototype('" + prototype.name + "',");
    outData.print(" Type('" + prototype.type.name + "', "
        + fieldType(prototype.type.typeof) + ", "
        + refType(prototype.type.reference) + "), ");
    if (prototype.parameters.size() == 0)
    {
      outData.println(" [] ) )");
      return;
    }
    outData.println();
    String square = "[";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field) prototype.parameters.elementAt(i);
      outData.println("  " + square + " Field('" + field.name + "', Type('"
          + field.type.name + "', " + fieldType(field.type.typeof) + ", "
          + refType(field.type.reference) + "), "
          + (field.isInput == true ? "True" : "False") + ", "
          + (field.isOutput == true ? "True" : "False") + ")"
          + (i + 1 < prototype.parameters.size() ? "," : " ] ) )"));
      square = " ";
    }
  }
  private static String fieldType(int no)
  {
    switch (no)
    {
    case 0:
      return "USERTYPE";
    case 1:
      return "BOOLEAN";
    case 2:
      return "CHAR";
    case 3:
      return "SHORT";
    case 4:
      return "LONG";
    case 5:
      return "FLOAT";
    case 6:
      return "DOUBLE";
    case 7:
      return "VOID";
    case 8:
      return "BYTE";
    case 9:
      return "INT";
    case 10:
      return "STRING";
    }
    return "???";
  }
  private static String refType(int no)
  {
    switch (no)
    {
    case 1:
      return "BYVAL";
    case 2:
      return "BYPTR";
    case 3:
      return "BYREF";
    case 4:
      return "BYPTRPTR";
    case 5:
      return "BYREFPTR";
    case 6:
      return "ARRAYED";
    }
    return "???";
  }
  private static String structType(int no)
  {
    switch (no)
    {
    case 0:
      return "NORMAL";
    case 1:
      return "PUBLIC";
    case 2:
      return "PRIVATE";
    case 3:
      return "PROTECTED";
    }
    return "???";
  }
}
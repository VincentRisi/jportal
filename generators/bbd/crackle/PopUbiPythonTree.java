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

public class PopUbiPythonTree extends Generator
{
  public static String description()
  {
    return "Generates Python Tree Code";
  }
  public static String documentation()
  {
    return "Generates Python Tree Code";
  }
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
    outLog.println(module.name + " version " + module.version);
    generate_tree(module, output, outLog);
  }
  public PopUbiPythonTree()
  {
  }
  private static void generate_field(Field field, String pId, PrintWriter outData)
  {
    outData.println(pId + ".name = '" + field.name + "'");
    outData.println(pId + ".type = Class()");
    outData.println(pId + ".type.name = '" + field.type.name + "'");
    outData.println(pId + ".type.typeof = " + field.type.typeof);
    outData.println(pId + ".type.reference = " + field.type.reference);
    outData.println(pId + ".type.isUnsigned = " + (field.type.isUnsigned ? "True" : "False"));
    outData.println(pId + ".type.arraySizes = []");
    for (int i = 0; i < field.type.arraySizes.size(); i++)
    {
      Integer no = (Integer)field.type.arraySizes.elementAt(i);
      outData.println(pId + ".type.arraySizes.append(" + no.intValue() + ")");
    }
    if (field.input != null)
    {
      outData.println(pId + ".input = Class()");
      outData.println(pId + ".input.name = '" + field.input.name + "'");
      outData.println(pId + ".input.operations = []");
      for (int i = 0; i < field.input.operations.size(); i++)
      {
        Operation operation = (Operation)field.input.operations.elementAt(i);
        outData.println("_op = Class()");
        outData.println(pId + ".input.operations.append(_op)");
        outData.println("_op.name = '" + operation.name + "'");
        outData.println("_op.code = " + operation.code);
        outData.println("_op.isConstant = " + (operation.isConstant ? "True" : "False"));
        if (operation.field != null)
          generate_field(operation.field, "_op", outData);
        else
          outData.println("_op.field = None");
      }
    }
    else
      outData.println(pId + ".input = None");
    if (field.output != null)
    {
      outData.println(pId + ".output = Class()");
      outData.println(pId + ".output.name = '" + field.output.name + "'");
      outData.println(pId + ".output.operations = []");
      for (int i = 0; i < field.output.operations.size(); i++)
      {
        Operation operation = (Operation)field.output.operations.elementAt(i);
        outData.println("_op = Class()");
        outData.println(pId + ".output.operations.append(_op)");
        outData.println("_op.name = '" + operation.name + "'");
        outData.println("_op.code = " + operation.code);
        outData.println("_op.isConstant = " + (operation.isConstant ? "True" : "False"));
        if (operation.field != null)
          generate_field(operation.field, "_op", outData);
        else
          outData.println("_op.field = None");
      }
    }
    else
      outData.println(pId + ".output = None");
    outData.println(pId + ".isInput = " + (field.isInput ? "True" : "False"));
    outData.println(pId + ".isOutput = " + (field.isOutput ? "True" : "False"));
    outData.println(pId + ".hasSize = " + (field.hasSize ? "True" : "False"));
  }
  private static String fix(String line)
  {
    String result = line.replace('\r',' ').replace('\n', ' ');
    return result;
  }
  private static void generate_tree(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String sourceName = module.name.toLowerCase() + "_tree";
      outLog.println("Code: " + output + sourceName + ".py");
      OutputStream outFile = new FileOutputStream(output + sourceName + ".py");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("STRUCTURE_TYPE = {0:'NORMAL',1:'PUBLIC',2:'PRIVATE',3:'PROTECTED'}");
        outData.println("FIELD_TYPE = {0:'USERTYPE',1:'BOOLEAN',2:'CHAR',3:'SHORT',4:'LONG',5:'FLOAT',6:'DOUBLE',7:'VOID',8:'BYTE',9:'INT',10:'STRING'}");
        outData.println("REFERENCE_TYPE = {0:'BYVAL',1:'BYPTR',2:'BYREF',3:'BYPTRPTR',4:'BYREFPTR',5:'ARRAYED'}");
        outData.println("PROTOTYPE_TYPE = {0:'RPCCALL',1:'PUBLIC',2:'PRIVATE',3:'PROTECTED'}");
        outData.println("OPERATION_TYPE = {1:'SIZE',2:'DYNAMIC'}");
        outData.println();
        outData.println("class Class: pass");
        outData.println("module = Class()");
        outData.println("module.sourceName= r'" + module.sourceName + "'");
        outData.println("module.name = '" + module.name + "'");
        outData.println("module.version = '" + module.version + "'");
        outData.println("module.packageName = '" + module.packageName + "'");
        outData.println("module.signature = " + module.signature);
        outData.println("module.countOfHashes = " + module.countOfHashes);
        outData.println("module.messageBase = " + module.messageBase);
        outData.println("module.codeLine = " + module.codeLine);
        outData.println("module.codeStart = " + module.codeStart);
        outData.println("module.messageStart= " + module.messageStart);
        outData.println("module.messages = []");
        for (int i = 0; i < module.messages.size(); i++)
        {
          Message message = (Message)module.messages.elementAt(i);
          outData.println();
          outData.println("_i = Class()");
          outData.println("module.messages.append(_i)");
          outData.println("_i.name = '" + message.name + "'");
          outData.println("_i.value = '" + message.value + "'");
        }
        outData.println("module.tables = []");
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table)module.tables.elementAt(i);
          outData.println();
          outData.println("_i = Class()");
          outData.println("module.tables.append(_i)");
          outData.println("_i.name = '" + table.name + "'");
          outData.println("_i.messages = []");
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message)table.messages.elementAt(j);
            outData.println("_j = Class()");
            outData.println("_i.messages.append(_j)");
            outData.println("_j.name = '" + message.name + "'");
            outData.println("_j.value = '" + message.value + "'");
          }
        }
        outData.println("module.structures = []");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          outData.println();
          outData.println("_i = Class()");
          outData.println("module.structures.append(_i)");
          outData.println("_i.name = '" + structure.name + "'");
          outData.println("_i.header = '" + structure.header + "'");
          outData.println("_i.categories = []");
          outData.println("_i.fields = []");
          outData.println("_i.codeType = " + structure.codeType);
          outData.println("_i.code = []");
          outData.println("_i.codeLine = " + structure.codeLine);
          outData.println("_i.start = " + structure.start);
          for (int j = 0; j < structure.categories.size(); j++)
          {
            String category = (String)structure.categories.elementAt(j);
            outData.println("_i.categories.append('" + category + "'");
          }
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field)structure.fields.elementAt(j);
            outData.println("_j = Class()");
            outData.println("_i.fields.append(_j)");
            generate_field(field, "_j", outData);
          }
          for (int j = 0; j < structure.code.size(); j++)
          {
            String code = (String)structure.code.elementAt(j);
            outData.println("_i.code.append(r'''" + fix(code) + "''')");
          }
        }
        outData.println("module.enumerators = []");
        for (int i = 0; i < module.enumerators.size(); i++)
        {
          Enumerator enumerator = (Enumerator)module.enumerators.elementAt(i);
          outData.println();
          outData.println("_i = Class()");
          outData.println("module.enumerators.append(_i)");
          outData.println("_i.name = '" + enumerator.name + "'");
          outData.println("_i.elements = []");
          for (int j = 0; j < enumerator.elements.size(); j++)
            outData.println("_i.elements.append(\"\"\"" + (String)enumerator.elements.elementAt(j) + "\"\"\")");
        }
        outData.println("module.prototypes = []");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          outData.println();
          outData.println("_i = Class()");
          outData.println("module.prototypes.append(_i)");
          outData.println("_i.name = '" + prototype.name + "'");
          outData.println("_i.message = '" + prototype.message + "'");
          outData.println("_i.type = Class()");
          outData.println("_i.type.name = '" + prototype.type.name + "'");
          outData.println("_i.type.typeof = " + prototype.type.typeof);
          outData.println("_i.type.reference = " + prototype.type.reference);
          outData.println("_i.type.isUnsigned = " + (prototype.type.isUnsigned ? "True" : "False"));
          outData.println("_i.type.arraySizes = []");
          for (int j = 0; j < prototype.type.arraySizes.size(); j++)
          {
            Integer no = (Integer)prototype.type.arraySizes.elementAt(j);
            outData.println("_i.type.arraySizes.append(" + no.intValue() + ")");
          }
          outData.println("_i.parameters = []");
          for (int j = 0; j < prototype.parameters.size(); j++)
          {
            Field field = (Field)prototype.parameters.elementAt(j);
            outData.println("_j = Class()");
            outData.println("_i.parameters.append(_j)");
            generate_field(field, "_j", outData);
          }
          outData.println("_i.inputs = []");
          for (int j = 0; j < prototype.inputs.size(); j++)
          {
            Action action = (Action)prototype.inputs.elementAt(j);
            outData.println("_j = Class()");
            outData.println("_i.inputs.append(_j)");
            outData.println("_j.name = '" + action.name + "'");
            outData.println("_j.operations = []");
            for (int k = 0; k < action.operations.size(); k++)
            {
              Operation operation = (Operation)action.operations.elementAt(k);
              outData.println("_op = Class()");
              outData.println("_j.operations.append(_op)");
              outData.println("_op.name = '" + operation.name + "'");
              outData.println("_op.code = " + operation.code);
              outData.println("_op.isConstant = " + (operation.isConstant ? "True" : "False"));
              if (operation.field != null)
                generate_field(operation.field, "_op", outData);
              else
                outData.println("_op.field = None");
            }
          }
          outData.println("_i.outputs = []");
          for (int j = 0; j < prototype.outputs.size(); j++)
          {
            Action action = (Action)prototype.outputs.elementAt(j);
            outData.println("_j = Class()");
            outData.println("_i.outputs.append(_j)");
            outData.println("_j.name = '" + action.name + "'");
            outData.println("_j.operations = []");
            for (int k = 0; k < action.operations.size(); k++)
            {
              Operation operation = (Operation)action.operations.elementAt(k);
              outData.println("_op = Class()");
              outData.println("_j.operations.append(_op)");
              outData.println("_op.name = '" + operation.name + "'");
              outData.println("_op.code = " + operation.code);
              outData.println("_op.isConstant = " + (operation.isConstant ? "True" : "False"));
              if (operation.field != null)
                generate_field(operation.field, "_op", outData);
              else
                outData.println("_op.field = None");
            }
          }
          outData.println("_i.categories = []");
          for (int j = 0; j < prototype.categories.size(); j++)
          {
            String category = (String)prototype.categories.elementAt(j);
            outData.println("_i.categories.append('" + category + "'");
          }
          outData.println("_i.codeType = " + prototype.codeType);
          outData.println("_i.code = []");
          for (int j = 0; j < prototype.code.size(); j++)
          {
            String code = (String)prototype.code.elementAt(j);
            outData.println("_i.code.append(r'''" + fix(code) + "''')");
          }
          outData.println("_i.codeLine = " + prototype.codeLine);
          outData.println("_i.start = " + prototype.start);
        }
        outData.println("module.pragmas = []");
        for (int i = 0; i < module.pragmas.size(); i++)
        {
          String pragma = (String)module.pragmas.elementAt(i);
          outData.println("module.pragmas.append(r'''" + fix(pragma) + "''')");
        }
        outData.println("module.code = []");
        for (int i = 0; i < module.code.size(); i++)
        {
          String code = (String)module.code.elementAt(i);
          outData.println("module.code.append(r'''" + fix(code) + "''')");
        }
        outData.println("module.imports = []");
        for (int i = 0; i < module.imports.size(); i++)
        {
          String imp = (String)module.imports.elementAt(i);
          outData.println("module.code.append(r'''" + fix(imp) + "''')");
        }
        outData.println();
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
}



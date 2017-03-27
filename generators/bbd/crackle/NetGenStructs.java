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
/*
 * Created on 2005/06/10
 *
 */
package bbd.crackle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author vince
 */
public class NetGenStructs extends Generator
{
  private static boolean keepTee = false;
  public static String description()
  {
    return "Generates Net C# Structs Code";
  }
  public static String documentation()
  {
    return "Generates Net C# Structs Code"
      + "\r\nHandles following pragmas"
      + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
      + "\r\n  KeepTee - Keep the dreaded small tee in the structures."
      ;
  }
  public static void main(String[] args)
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate ... ");
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
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    outLog.println(module.name+" version "+module.version);
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true) {
	} else if (pragma.trim().equalsIgnoreCase("KeepTee") == true)
        keepTee = true;
    }
    generateStructs(module, output, outLog);
  }
  public static void generateEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.enumerators.size(); i++)
    {
      Enumerator element = (Enumerator) module.enumerators.elementAt(i);
      outData.println("  public enum "+element.name);
      String w1 = "  { ";
      for (int j = 0; j < element.elements.size(); j++)
      {
        String derf = (String) element.elements.elementAt(j);
        outData.println(w1+derf);
        w1 = "  , ";
      }
      outData.println("  }");
    }
  }
  public static void generateMessageEnums(Module module, PrintWriter outData)
  {
    outData.println("  public enum Message");
    outData.println("  { OK");
    String w0 = "";
    if (module.messageBase > 0)
      w0 = " = "+module.messageBase;
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message) module.messages.elementAt(i);
      outData.println("  , "+message.name+w0);
      w0 = "";
    }
    outData.println("  , INV_SIGNATURE");
    outData.println("  , UNCAUGHT_DBERROR");
    outData.println("  , UNKNOWN_FUNCTION");
    outData.println("  , LAST_LAST");
    outData.println("  }");
  }
  public static void generateStructs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name+"Structs.cs");
      OutputStream outFile = new FileOutputStream(output+module.name+"Structs.cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        generateUsings(module, outData);
        outData.println();
        outData.println("namespace Bbd.Idl3."+module.name);
        outData.println("{");
        generateStructs(module, outData);
        generateEnums(module, outData);
        generateMessageEnums(module, outData);
        generateTableEnums(module, outData);
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
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  public static void generateUsings(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      if (structure.header.toLowerCase().indexOf(".sh") == structure.header.length()-4)
      {
        outData.println("using Bbd.Idl3.JPortal;");
        break;
      }
    }
  }
  public static void generateStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      outData.println("  [Serializable()]");
      outData.println("  public class "+dropTee(structure.name, module.name));
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        outData.println("    "+"public "+field.type.csStructDef(field.name, field.isStruct(module))+";");
      }
      outData.println("  }");
    }
  }
  public static void generateTableEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table) module.tables.elementAt(i);
      outData.println("    public enum "+table.name);
      String comma  = "    { ";
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message) table.messages.elementAt(j);
        outData.println(comma+message.name);
        comma = "    , ";
      }
      outData.println(comma+table.name.toLowerCase()+"NoOf");
      outData.println("    }");
    }
  }
  public static String dropTee(String in, String modName)
  {
    String result = in;
    if (in.compareTo("char") == 0)
    {
      result = "string";
    }
    else if (keepTee == false)
    {
      if (result.charAt(0) == 't')
        result = result.substring(1);
    }
    return result;
  }
}

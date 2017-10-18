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
 * Created on Jun 6, 2005
 */
package bbd.crackle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

/**
 * @author Vince
 */
public class NetGenServer extends Generator
{
  private static boolean keepTee = false;
  public static String description()
  {
    return "Generates Net C# Server Code";
  }
  public static String documentation()
  {
    return "Generates Net C# Server Code";
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
    generateClasses(module, output, outLog);
  }
  public static void generateCalls(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateCall(module, prototype, i, outData);
    }
  }
  public static void generateCall(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    generateCommon(module, prototype, no, outData, "protected ");
    outData.println(")");
    outData.println("    {");
    for (int i=0; i<prototype.code.size(); i++)
      outData.print ("    "+(String) prototype.code.elementAt(i));
    outData.println("    }");
  }
  public static void generateClasses(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name+"Server.cs");
      OutputStream outFile = new FileOutputStream(output+module.name+"Server.cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        outData.println("using System.IO;");
        outData.println("using Bbd.Idl3;");
        outData.println("using Bbd.Idl3.Rpc;");
        generateUsings(module, outData);
        outData.println();
        outData.println("namespace Bbd.Idl3."+module.name);
        outData.println("{");
        outData.println("  public class "+module.name+": Popper");
        outData.println("  {");
        outData.println("    public string Version {get {return version;}}  string version = "+module.version+";");
        outData.println("    public int Signature  {get {return signature;}} int signature = "+module.signature+";");
        generateMessageLookups(module, outData);
        generateTableLookups(module, outData);
        outData.println("    public "+module.name+"(Header header, string service, int timeOut)");
        outData.println("    : base(header, service, timeOut)");
        outData.println("    {}");
        generateCalls(module, outData);
        outData.println("  }");
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
  public static Vector<Parameter> parameterList;
  public static boolean generateCommon(Module module, Prototype prototype, int no, PrintWriter outData, String extra1)
  {
    Parameter pd = null;
    parameterList = new Vector<Parameter>();
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
      &&  prototype.type.typeof != Type.VOID)
      hasReturn = true;
    outData.print("    "+extra1+(hasReturn ? prototype.type.netName() : "void")+" "+prototype.name+"(");
    String comma = "";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      pd = new Parameter();
      Field field = (Field) prototype.parameters.elementAt(i);
      pd.field = field;
      for (int j = 0; j < prototype.inputs.size(); j++)
      {
        pd.input = (Action) prototype.inputs.elementAt(j);
        if (pd.input.name.compareTo(field.name) == 0)
        {
          pd.isInput = true;
          pd.inputNo = j;
          pd.hasInputSize = pd.input.hasSize();
          pd.inputSizeValue = pd.input.getSizeName();
          break;
        }
      }
      for (int j = 0; j < prototype.outputs.size(); j++)
      {
        pd.output = (Action) prototype.outputs.elementAt(j);
        if (pd.output.name.compareTo(field.name) == 0)
        {
          pd.isOutput = true;
          pd.outputNo = j;
          pd.hasOutputSize = pd.output.hasSize();
          pd.outputSizeValue = pd.output.getSizeName();
          break;
        }
      }
      outData.println(comma);
      outData.print("        ");
      if (pd.isInput && pd.isOutput)
        outData.print("ref ");
      else if (pd.isOutput)
        outData.print("out ");
      else if (field.type.reference == Type.BYREF
        ||  field.type.reference == Type.BYPTR
        ||  field.type.reference == Type.BYREFPTR)
        outData.print("ref ");
      pd.type = dropTee(field.type.netName(), module.name);
      pd.name = lowerFirst(field.name);
      outData.print(pd.type);
      if (field.type.reference == Type.BYREFPTR
        && pd.type.compareTo("string") != 0)
      {
        pd.isArray = true;
        outData.print("[] ");
      }
      else if ((pd.hasInputSize || pd.hasOutputSize)
        && pd.type.compareTo("string") != 0)
      {
        pd.isArray = true;
        outData.print("[] ");
      }
      else
        outData.print(" ");
      outData.print(pd.name);
      parameterList.addElement(pd);
      comma = ",";
    }
    return hasReturn;
  }
  public static void generateMessageLookups(Module module, PrintWriter outData)
  {
    outData.println("    // Used to be a class method but changed to instance method");
    outData.println("    // for the publish and subscribe delegates. Requires downcast");
    outData.println("    // for the same reason.");
    outData.println("    public override string MessageDesc(Object no)");
    outData.println("    {");
    outData.println("      switch ((Message)no)");
    outData.println("      {");
    outData.println("      case Message.OK: return \"OK\";");
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message) module.messages.elementAt(i);
      outData.println("      case Message."+message.name+": return "+message.value+";");
    }
    outData.println("      case Message.INV_SIGNATURE: return \"Invalid Signature\";");
    outData.println("      case Message.UNCAUGHT_DBERROR: return \"Uncaught DBError\";");
    outData.println("      case Message.UNKNOWN_FUNCTION: return \"Unknown Function\";");
    outData.println("      }");
    outData.println("      return String.Format(\"MessageDesc for {0} Unknown.\", no);");
    outData.println("    }");
  }
  public static void generateTableLookups(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table) module.tables.elementAt(i);
      outData.println("    public static string "+table.name+"Value("+table.name+" no)");
      outData.println("    {");
      outData.println("      switch (no)");
      outData.println("      {");
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message) table.messages.elementAt(j);
        outData.println("      case "+table.name+"."+message.name+": return "+message.value+";");
      }
      outData.println("      }");
      outData.println("      return String.Format(\""+table.name+"Value for {0} Unknown.\", no);");
      outData.println("    }");
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
  public static String lowerFirst(String in)
  {
    String result = in.substring(0,1).toLowerCase()+in.substring(1);
    return result;
  }
}

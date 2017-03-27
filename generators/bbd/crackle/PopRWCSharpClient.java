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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopRWCSharpClient extends Generator
{
  public static PrintWriter errLog;
  private static Vector<String> usings;
  private static Vector<Parameter> parameterList;
  public static String asHex(int value)
  {
    return "0x" + Integer.toHexString(value);
  }
  public static String asHex(long value)
  {
    return "0x" + Integer.toHexString((int) value);
  }
  public static String asHex(String value)
  {
    return "0x" + Integer.toHexString(Integer.parseInt(value));
  }
  public static String description()
  {
    return "Generates C# Client Code for Java Server Code";
  }
  public static String documentation()
  {
    return "Generates C# Client Code for Java Server Code"
      ;
  }
  private static String assignValue(Type type)
  {
    if (type.reference != Type.ARRAYED)
    {
      switch (type.typeof)
      {
      case Type.BOOLEAN:
        return "false";
      case Type.CHAR:
        return "\"\"";
      case Type.SHORT:
        return "0";
      case Type.LONG:
        return "0";
      case Type.FLOAT:
        return "0.0";
      case Type.DOUBLE:
        return "0.0";
      case Type.VOID:
        return "null";
      case Type.BYTE:
        return "0";
      case Type.INT:
        return "0";
      case Type.STRING:
        return "\"\"";
      }
    }
    return "new " + type.javaName();
  }
  public static boolean buildParameterList(Prototype prototype)
  {
    parameterList = new Vector<Parameter>();
    boolean hasReturn = Parameter.build(parameterList, prototype, false);
    return hasReturn;
  }
  /**
   * Generates
   */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version " + module.version);
    generateStructs(module, output, outLog);
    generateClasses(module, output, outLog);
  }
  private static void generateCall(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    Parameter pd = new Parameter();
    boolean hasReturn = generateCommon(module, prototype, no, outData, "public ");
    String sNo = ""+no;
    outData.println(")");
    outData.println("    {");
    outData.println("      Init("+(prototype.message.length()>0 ? asHex(prototype.message) : asHex(sNo))
                               +", \""+module.name+"\", \""
                               +prototype.name+"\");");
    outData.println("      try");
    outData.println("      {");
    outData.println("        writer.putInt("+asHex(prototype.signature(true))+");");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      for (int j = 0; j < parameterList.size(); j++)
      {
        pd = (Parameter) parameterList.elementAt(j);
        if (pd.inputNo == i)
          break;
      }
      writeCall(pd.name, pd.field.type, "  ", outData);
    }
    outData.println("        Call();");
    if (hasReturn)
    {
      outData.println("        " + prototype.name + "Return _ret = new " + prototype.name + "Return();");
      outData.println("        _ret.read(reader);");

      outData.println("        return _ret.result;");
    }
    outData.println("      }");
    outData.println("      catch (RpcException ex)");
    outData.println("      {");
    outData.println("        OnError(ex, (int)Message.INV_SIGNATURE);");
    outData.println("        if (ex.ErrorCode != 0)");
    outData.println("        {");
    outData.println("          throw new RpcException(ex.ErrorCode,");
    outData.println("            MessageDesc((Message)ex.ErrorCode)");
    outData.println("              + System.Environment.NewLine");
    outData.println("              + ex.Message,");
    outData.println("            ex.InnerException);");
    outData.println("        }");
    outData.println("        throw; // else just rethrow");
    outData.println("      }");
    outData.println("      finally");
    outData.println("      {");
    outData.println("        Done();");
    outData.println("      }");
    outData.println("    }");
  }
  private static void generateCalls(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.isRpcCall())// || prototype.isExtendedRpcCall())
        generateCall(module, prototype, i, outData);
    }
  }
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateClasses(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name+".cs");
      OutputStream outFile = new FileOutputStream(output+module.name+".cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        outData.println("using System.IO;");
        outData.println("using bbd.crackle.rw;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
        outData.println("{");
        outData.println("  public class "+module.name+": Handler");
        outData.println("  {");
        outData.println("    private string version = "+module.version+";");
        outData.println("    private int signature = "+module.signature+";");
        outData.println("    public string Version {get {return version;}}");
        outData.println("    public int Signature {get {return signature;}}");
        generateMessageLookups(module, outData);
        generateTableLookups(module, outData);
        outData.println("    public "+module.name+"(Header header, string host, string service, int timeOut)");
        outData.println("    : base(header, host, service, timeOut)");
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
  private static boolean generateCommon(Module module, Prototype prototype, int no, PrintWriter outData, String extra1)
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
      pd.type = field.type.netName();
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
  private static void generateEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.enumerators.size(); i++)
    {
      Enumerator entry = (Enumerator) module.enumerators.elementAt(i);
      outData.println("  public enum "+entry.name);
      String w1 = "  { ";
      for (int j = 0; j < entry.elements.size(); j++)
      {
        String element = (String) entry.elements.elementAt(j);
        outData.println(w1+element);
        w1 = "  , ";
      }
      outData.println("  }");
    }
  }
  /**
   * Generates the module message enum
   */
  private static void generateMessageEnums(Module module, PrintWriter outData)
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
    outData.println("  , INV_COOKIE");
    outData.println("  , INV_INIFILE");
    outData.println("  , UNCAUGHT_DBERROR");
    outData.println("  , UNKNOWN_FUNCTION");
    outData.println("  , SNAP_ERROR");
    outData.println("  , ADDLIST_ERROR");
    outData.println("  , ADDLIST_REALLOC_FAILED");
    outData.println("  , LAST_LAST");
    outData.println("  }");
  }
  private static void generateMessageLookups(Module module, PrintWriter outData)
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
    outData.println("      case Message.INV_COOKIE: return \"Invalid Cookie\";");
    outData.println("      case Message.INV_INIFILE: return \"Invalid Inifile\";");
    outData.println("      case Message.UNCAUGHT_DBERROR: return \"Uncaught DBError\";");
    outData.println("      case Message.UNKNOWN_FUNCTION: return \"Unknown Function\";");
    outData.println("      case Message.SNAP_ERROR: return \"Snap Error\";");
    outData.println("      case Message.ADDLIST_ERROR: return \"AddList Error\";");
    outData.println("      case Message.ADDLIST_REALLOC_FAILED: return \"AddList realloc Failed\";");
    outData.println("      }");
    outData.println("      return String.Format(\"MessageDesc for {0} Unknown.\", no);");
    outData.println("    }");
  }
  public static void generateReturnStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("  public partial class " + prototype.name + "Return");
    outData.println("  {");
    RcString rcs = nullReturn(prototype);
    outData.println("    public " + prototype.type.javaName() + " result = " + rcs.result + ";");
    outData.println("    public void read(Reader reader)");
    outData.println("    {");
    if (rcs.rc == true)
      outData.println("      result.read(reader);");
    else
      readCall("result", prototype.type, "", outData);
    outData.println("    }");
    outData.println("    public void write(Writer writer)");
    outData.println("    {");
    if (rcs.rc == true)
      outData.println("      result.write(writer);");
    else
      writeCall("result", prototype.type, "", outData);
    outData.println("    }");
    outData.println("  }");
  }
  private static void readCall(String name, Type type, String pad, PrintWriter outData)
  {
    boolean isArray = false;
    if (type.reference == Type.ARRAYED)
      isArray = true;
    String var = name;
    String reader = "reader";
    pad = pad + "      ";
    Type useType = type;
    switch (useType.typeof)
    {
      case Type.BOOLEAN:
        outData.println(pad + var + " = " + reader + ".getByte() == 0 ? false : true;");
        break;
      case Type.CHAR:
        if (type.arraySizes.size() > 0 || type.reference == Type.BYPTR)
        {
          outData.println(pad + "int " + var + "_length = " + reader + ".getInt();");
          outData.println(pad + var + " = " + reader + ".getString(" + var + "_length);");
        }
        else
          outData.println(pad + var + " = " + reader + ".getByte();");
        break;
      case Type.BYTE:
        outData.println(pad + var + " = " + reader + ".getByte();");
        break;
      case Type.STRING:
        outData.println(pad + "int " + var + "_length = " + reader + ".getInt();");
        outData.println(pad + var + " = " + reader + ".getString(" + var + "_length);");
        break;
      case Type.SHORT:
        outData.println(pad + var + " = " + reader + ".getShort();");
        break;
      case Type.INT:
        outData.println(pad + var + " = " + reader + ".getInt();");
        break;
      case Type.LONG:
        outData.println(pad + var + " = " + reader + ".getLong();");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println(pad + var + " = " + reader + ".getDouble();");
        break;
      default:
        if (isArray == true)
        {
          outData.println(pad + "int " + var + "_count = " + reader + ".getInt();");
          outData.println(pad + "for (int i = 0; i < " + var + "_count; i++)");
          outData.println(pad + "  " + var + "[i].read(" + reader + ");");
        }
        else
          outData.println(pad + var + ".read(" + reader + ");");
        break;
    }
  }
  private static void writeCall(String name, Type type, String pad, PrintWriter outData)
  {
    boolean isArray = false;
    String var = name;
    if (type.reference == Type.ARRAYED)
      isArray = true;
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        outData.println("      writer.putByte(" + var + "? 1 : 0);");
        break;
      case Type.CHAR:
        if (type.arraySizes.size() > 0 || type.reference == Type.BYPTR)
        {
          outData.println(pad + "      int " + var + "_length = " + var + ".Length;");
          outData.println(pad + "      writer.putInt(" + var + "_length);");
          outData.println(pad + "      writer.putString(" + var + ", " + var + "_length);");
        }
        else
          outData.println(pad + "      writer.putByte(" + var + ");");
        break;
      case Type.BYTE:
        outData.println(pad + "      writer putByte(" + var + ");");
        break;
      case Type.STRING:
        outData.println(pad + "      int " + var + "_length = " + var + ".Length;");
        outData.println(pad + "      writer.putInt(" + var + "_length);");
        outData.println(pad + "      writer.putString(" + var + ", " + var + "_length);");
        break;
      case Type.SHORT:
        outData.println(pad + "      writer.putShort(" + var + ");");
        break;
      case Type.INT:
        outData.println(pad + "      writer.putInt(" + var + ");");
        break;
      case Type.LONG:
        outData.println(pad + "      writer.putLong(" + var + ");");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println(pad + "      writer.putDouble(" + var + ");");
        break;
      default:
        if (isArray == true)
        {
          outData.println(pad + "      int " + var + "_count = " + var + ".Length;");
          outData.println(pad + "      writer.putInt(" + var + "_count);");
          outData.println(pad + "      for (int i = 0; i < " + var + "_count; i++)");
          outData.println(pad + "        " + var + "[i].write(writer);");
        }
        else
          outData.println(pad + "      " + var + ".write(writer);");
        break;
    }
  }
  public static void generateReturnStructs(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateReturnStructs(module, prototype, i, outData);
    }
  }
  public static void generateReturnStructs(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    if (hasReturn == true)
      generateReturnStruct(prototype, outData);
  }
  private static void generateStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      outData.println("  public partial class " + structure.name);
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        outData.println("    " + "public " + field.type.csStructDef(field.nameLowerFirst(), field.isStruct(module))+";");
        if (field.isStruct(module) == false)
          outData.println("    public " + field.type.csStructDef(field.nameUpperFirst(), field.isStruct(module))
            + "{ get {return " + field.nameLowerFirst() + ";} set {" + field.nameLowerFirst() + " = value;} }");
      }
      outData.println("    public void read(Reader reader)");
      outData.println("    {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        readCall(field.name, field.type, "", outData);
      }
      outData.println("    }");
      outData.println("    public void write(Writer writer)");
      outData.println("    {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        writeCall(field.name, field.type, "", outData);
      }
      outData.println("    }");
      outData.println("  }");
    }
  }
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateStructs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name+"Structs.cs");
      OutputStream outFile = new FileOutputStream(output+module.name+"Structs.cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// 1: Mutilation, Spindlization and Bending will result in goto 1");
        outData.println();
        outData.println("using System;");
        outData.println("using bbd.crackle.rw;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
        outData.println("{");
        generateStructs(module, outData);
        generateReturnStructs(module, outData);
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
  /**
   * Generates table messages enum
   */
  private static void generateTableEnums(Module module, PrintWriter outData)
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
  private static void generateTableLookups(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table) module.tables.elementAt(i);
      outData.println("    private static string "+table.name+"Value("+table.name+" no)");
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
  private static void generateUsings(Module module, PrintWriter outData)
  {
    usings = new Vector<String>();
    if (module.packageName.length() != 0)
    {
      String defaultUsing = "using " + module.packageName + ";";
      usings.addElement(defaultUsing);
    }
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      if (structure.header.toLowerCase().indexOf(".sh") == structure.header.length()-4)
      {
        outData.println("using Bbd.Idl2.AnyDB;");
        break;
      }
      int n = structure.header.length();
      if (n > 0)
      {
        String importString = structure.header.substring(1, n - 1);
        if (importString.indexOf("import ") == 0 && importString.lastIndexOf(";") != importString.length()-1)
          printImport(structure.name, "using " + importString.substring(7), outData);
      }
    }
  }
  private static String lowerFirst(String in)
  {
    String result = in.substring(0,1).toLowerCase()+in.substring(1);
    return result;
  }
  /**
   * Reads input from stored repository
   */
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      errLog = outLog;
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
  private static String makeNameSpace(Module module)
  {
    if (module.packageName.length() == 0)
      return "namespace Bbd.Idl2."+module.name;
    return "namespace "+module.packageName;
  }
  public static class RcString { boolean rc; String result; }
  private static RcString nullReturn(Prototype prototype)
  {
    RcString rcs = new RcString();
    rcs.rc = false;
    rcs.result = assignValue(prototype.type);
    if (prototype.type.typeof == Type.USERTYPE)
    {
      rcs.result = rcs.result + "()";
      rcs.rc = true;
    }
    return rcs;
  }
  private static void printImport(String name, String data, PrintWriter outData)
  {
    // 0123456789012345678901234
    // using aaa.bbb.CCC; // xx -- CCC (l=25 m=3 n=15 k=20)
    int m = name.length();
    int n = data.indexOf(name);
    int l = data.length();
    int k = data.indexOf(";");
    if (k > 0)
      l = k + 1;
    if (l - n == m + 1) // 19-15 == 3+1
      data = data.substring(0, n-1) + ";";
    for (int i = 0; i < usings.size(); i++)
      if (data.compareTo((String)usings.elementAt(i)) == 0)
        return;
    outData.println(data);
    usings.addElement(data);
  }
}
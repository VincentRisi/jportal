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

public class PopUbiCSharp extends Generator
{
  public static String description()
  {
    return "Generates C# Client Code for C++ Server Code";
  }
  public static String documentation()
  {
    return "Generates C# Client Code for C++ Server Code"
      + "\r\nHandles following pragmas"
      + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
      + "\r\n  KeepTee - Keep the dreaded small tee in the structures."
      ;
  }
  private static boolean alignForSun = false;
  private static boolean keepTee = false;
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
  /**
   * Generates
   */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
      {
        outLog.println(module.name+": Align For Sun");
        alignForSun = true;
      }
      else if (pragma.trim().equalsIgnoreCase("KeepTee") == true)
      {
        outLog.println(module.name+": Keeping Little tee (hee hee)");
        keepTee = true;
    }
    }
    generateStructs(module, output, outLog);
    generateInterfaces(module, output, outLog);
    generateClasses(module, output, outLog);
    generateProxies(module, output, outLog);
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
        outData.println("using Bbd.Idl2;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
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
  private static String makeNameSpace(Module module)
  {
    if (module.packageName.length() == 0)
      return "namespace Bbd.Idl2."+module.name;
    return "namespace "+module.packageName;
  }
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateInterfaces(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+"I"+module.name+".cs");
      OutputStream outFile = new FileOutputStream(output+"I"+module.name+".cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
        outData.println("{");
        outData.println("  public interface I"+module.name+": IDisposable");
        outData.println("  {");
        generateInterfaces(module, outData);
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
        outData.println("using Bbd.Idl2;");
        outData.println("using Bbd.Idl2.Rpc;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
        outData.println("{");
        outData.println("  public class "+module.name+": Popper, I"+module.name);
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
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateProxies(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name+"Proxy.cs");
      OutputStream outFile = new FileOutputStream(output+module.name+"Proxy.cs");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println();
        outData.println("using System;");
        outData.println("using Bbd.Idl2;");
        outData.println("using Bbd.Idl2.Rpc;");
        generateUsings(module, outData);
        outData.println();
        outData.println(makeNameSpace(module));
        outData.println("{");
        outData.println("  public class "+module.name+"Proxy : MarshalByRefObject, I"+module.name);
        outData.println("  {");
        outData.println("    protected "+module.name+" server;");
        outData.println("    public "+module.name+"Proxy(Header header, string host, string service, int timeOut)");
        outData.println("    {");
        outData.println("      server = new "+module.name+"(header, host, service, timeOut);");
        outData.println("    }");
        generateProxies(module, outData);
        outData.println("    public virtual void Dispose(){} // Not quite sure I like this (VR)");
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
  private static Vector<String> usings;
  private static void generateUsings(Module module, PrintWriter outData)
  {
    usings = new Vector<String>();
    if (module.packageName.length() != 0)
    {
      String defaultUsing = "using " + module.packageName + ";";
      usings.addElement(defaultUsing);
    }
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = ((String)module.pragmas.elementAt(i)).trim();
      if (pragma.indexOf("ii(") == 1)
        buffer.append(pragma.substring(3));
    }
    String lookup = buffer.toString();
    buffer = new StringBuffer();
    if (module.packageName.length() > 0)
    {
      buffer.append('[');
      buffer.append(module.packageName);
      buffer.append(']');
    }
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      //boolean usingsDone = false;
      boolean checkLater = false;
      if (structure.header.toLowerCase().indexOf(".sh") == structure.header.length()-4)
      {
        checkLater = true;
        String key = "(" + structure.header.toLowerCase().substring(1, structure.header.length() - 4) + ":";
        int nb = lookup.indexOf(key);
        if (nb >= 0)
        { 
          nb += key.length();
          int ne = lookup.substring(nb).indexOf(")");
          if (ne > 0)
          {
            String packageName = lookup.substring(nb, ne+nb);
            String storeAs = "[" + packageName + "]";
            if (buffer.toString().indexOf(storeAs) >= 0)
              continue;
            buffer.append(storeAs);
            outData.println("using " + packageName + ";");
            //usingsDone = true;
            continue;
          }
        }
      }
      if (checkLater == true)
      {
        //if (usingsDone == false)
          outData.println("using Bbd.Idl2.AnyDB;");
        continue;
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
  private static void generateStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() <= 0) continue;
      if (structure.name.compareTo(module.name) == 0) continue;
      outData.println("  [Serializable()]");
      outData.println("  public class "+dropTee(structure.name, module.name));
      outData.println("  {");
      int relativeOffset = 0;
      int fillerNo = 0;
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        int n = relativeOffset % 8;
        if (n > 0)
        {
          n = 8-n;
          outData.println("    [Field(Size="+n+")] byte[] filler"+fillerNo+" = new byte["+n+"];");
          fillerNo++;
          relativeOffset += n;
        }
        outData.println("    "+field.type.csAttribute(field.isStruct(module))+"public "+field.type.csStructDef(field.nameLowerFirst(), field.isStruct(module))+";");
        if (field.isStruct(module) == false)
          outData.println("    public " + field.type.csStructDef(field.nameUpperFirst(), field.isStruct(module))
            + "{ get {return " + field.nameLowerFirst() + ";} set {" + field.nameLowerFirst() + " = value;} }");
        relativeOffset += field.type.relativeSize(false);
      }
      int n = relativeOffset % 8;
      if (n > 0)
      {
        n = 8-n;
        outData.println("    [Field(Size="+n+")] byte[] filler"+fillerNo+" = new byte["+n+"];");
      }
      outData.println("  }");
    }
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
  private static Vector<Parameter> parameterList;
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
    String align8 = "false";
    if (alignForSun == true)
      align8 = "true";
    outData.println("        Add("+asHex(prototype.signature(true))+", "+align8+");");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      for (int j = 0; j < parameterList.size(); j++)
      {
        pd = (Parameter) parameterList.elementAt(j);
        if (pd.inputNo == i)
          break;
      }
      outData.println("        Add("+pd.name+", "+align8+");");
    }
    outData.println("        Call();");
    if (hasReturn)
    {
      boolean complexReturn = false;
      if (prototype.type.typeof == Type.USERTYPE)
      {
        String name = prototype.type.netName();
        Structure struct = module.getStruct(name);
        if (struct.fields.size() > 0)
        {
          complexReturn = true;
          outData.println("        " + prototype.type.netName() + " result = new " + prototype.type.netName() + "();");
          for (int i = 0; i < struct.fields.size(); i++)
          {
            Field field = (Field)struct.fields.elementAt(i);
            if (field.type.reference == Type.ARRAYED)
            {
              outData.println("        int _len" + i + " = new int();");
              outData.println("        _len" + i + " = (int) Get(_len" + i + ", " + align8 + ");");
              outData.println("        if (_len" + i + " > 0)");
              outData.println("        {");
              outData.println("          result." + field.name + " = new " + field.type.name + "[_len" + i + "];");
              outData.println("          for (int i = 0; i < _len" + i + "; i++)");
              outData.println("            result." + field.name + "[i] = new " + field.type.name + "();");
              outData.println("          result." + field.name + " = (" + field.type.name + "[])Get(result." + field.name + ", " + align8 + ");");
              outData.println("        }");
            }
            else
              outData.println("        result." + field.name + " = (" + field.type.name + ")Get(result." + field.name + ", " + align8 + ");");
          }
        }
      }
      if (complexReturn == false)
      {
        outData.println("        " + prototype.type.netName() + " result = new " + prototype.type.netName() + "();");
        outData.println("        result = (" + prototype.type.netName() + ") Get(result, " + align8 + ");");
      }
    }
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      for (int j = 0; j < parameterList.size(); j++)
      {
        pd = (Parameter) parameterList.elementAt(j);
        if (pd.outputNo == i)
          break;
      }
      if (pd.isOutput == true && pd.isInput == false)
      {
        if (pd.isArray)
        {
          outData.println("        "+pd.name+" = new "+pd.type+"["+lowerFirst(pd.outputSizeValue)+"];");
          outData.println("        for (int i=0; i<"+lowerFirst(pd.outputSizeValue)+"; i++)");
          outData.println("          "+pd.name+"[i] = new "+pd.type+"();");
        }
        else if (pd.type.compareTo("string") != 0)
          outData.println("        "+pd.name+" = new "+pd.type+"();");
      }
      String pdType = pd.type
        + (pd.isArray == true ?"[]":"");
      if (pd.type.compareTo("string") != 0)
        outData.println("        "
          + pd.name
          + " = ("
          + pdType
          + ") Get("
          + pd.name
          + ", "+align8+");");
      else
        outData.println("        "
          + pd.name
          + "= GetString("
          + pd.outputSizeValue
          + ", "+align8+");");
    }
    if (hasReturn)
      outData.println("        return result;");
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
    //if (prototype.isExtendedRpcCall())
    //  outData.println("    #endif");
  }
  private static void generateInterfaces(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.isRpcCall())// || prototype.isExtendedRpcCall())
        generateInterface(module, prototype, i, outData);
    }
  }
  private static void generateInterface(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    //if (prototype.isExtendedRpcCall())
    //  outData.println("    #if ALLOW_PUBLIC_METHODS");
    generateCommon(module, prototype, no, outData, "");
    outData.println(");");
    //if (prototype.isExtendedRpcCall())
    //  outData.println("    #endif");
  }
  private static void generateProxies(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.isRpcCall())// || prototype.isExtendedRpcCall())
        generateProxy(module, prototype, i, outData);
    }
  }
  private static void generateProxy(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    //if (prototype.isExtendedRpcCall())
    //  outData.println("    #if ALLOW_PUBLIC_METHODS");
    Parameter pd = new Parameter();
    boolean hasReturn = generateCommon(module, prototype, no, outData, "public ");
    outData.println(")");
    outData.println("    {");
    if (hasReturn == true)
      outData.print("      return ");
    else
      outData.print("      ");
    outData.print("server."+prototype.name+"(");
    String comma = "";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      pd = new Parameter();
      Field parameter = (Field) prototype.parameters.elementAt(i);
      pd.field = parameter;
      for (int j = 0; j < prototype.inputs.size(); j++)
      {
        pd.input = (Action) prototype.inputs.elementAt(j);
        if (pd.input.name.compareTo(parameter.name) == 0)
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
        if (pd.output.name.compareTo(parameter.name) == 0)
        {
          pd.isOutput = true;
          pd.outputNo = j;
          pd.hasOutputSize = pd.output.hasSize();
          pd.outputSizeValue = pd.output.getSizeName();
          break;
        }
      }
      outData.print(comma);
      if (pd.isInput && pd.isOutput)
        outData.print("ref ");
      else if (pd.isOutput)
        outData.print("out ");
      else if (parameter.type.reference == Type.BYREF
        ||  parameter.type.reference == Type.BYPTR
        ||  parameter.type.reference == Type.BYREFPTR)
        outData.print("ref ");
      pd.name = lowerFirst(parameter.name);
      outData.print(pd.name);
      parameterList.addElement(pd);
      comma = ", ";
    }
    outData.println(");");
    outData.println("    }");
  }
  private static String lowerFirst(String in)
  {
    String result = in.substring(0,1).toLowerCase()+in.substring(1);
    return result;
  }
  private static String dropTee(String in, String modName)
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
  }
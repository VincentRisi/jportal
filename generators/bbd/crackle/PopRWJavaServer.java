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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopRWJavaServer extends Generator
{
  public static class RcString { boolean rc; String result; }
  public static PrintWriter errLog;
  static OutputStream outFile;
  private static Vector<String> usings;
  public static Vector<Parameter> parameterList;
  static String padString = "                                                         ";
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
  public static String asMessage(String name)
  {
    return name.toUpperCase() + "_MESSAGE";
  }
  public static String asSignature(String name)
  {
    return name.toUpperCase() + "_SIGNATURE";
  }
  private static String assignValue(Field field)
  {
    String result = assignValue(field.type);
    if (field.type.reference == Type.ARRAYED)
      result = result + "[0]";
    else if (field.type.typeof == Type.USERTYPE)
    {
      if (field.type.arraySizes.size() > 0)
        result = result + "[0]";
      else
        result = result + "()";
    }
    return result;
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
  public static String description()
  {
    return "Generates Java Server Code for Ubi Client Code";
  }
  public static String documentation()
  {
    return "Generates Java Server Code for Ubi Client Code";
  }
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    try
    {
      outLog.println(module.name + " version " + module.version);
      generateCallStructs(module, output, outLog);
      generateInterface(module, output, outLog);
      generateImplementation(module, output, outLog);
      generateDispatch(module, output, outLog);
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  public static void generateAssignCall(Module module, Prototype prototype, PrintWriter outData)
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String arrayed = prototype.type.reference == Type.ARRAYED ? "[]" : "";
    String work = "  public " + (hasReturn ? prototype.type.javaName() + arrayed : "void") + " " + prototype.name + "(";
    outData.print(work);
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter)parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma + pd.fullType() + " " + pd.name);
      if (comma.length() == 0)
        comma = padded(work.length() - 2) + ", ";
    }
    outData.println(") throws Exception");
    outData.println("  {");
    for (int i = 0; i < prototype.code.size(); i++)
    {
      String codeLine = (String)prototype.code.elementAt(i);
      outData.print(codeLine);
    }
    outData.println("  }");
  }
  public static void generateAssignCalls(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateAssignCall(module, prototype, outData);
    }
  }
  private static String[] seperate(String value, String delim)
  {
    int n = 0, p = 0;
    for (; ; )
    {
      n++;
      int q = value.indexOf(delim, p);
      if (q < 0)
        break;
      p = q + 1;
    }
    String[] result = new String[n];
    p = 0;
    for (int i = 0; i < n; i++)
    {
      int q = value.indexOf(delim, p);
      if (q < 0)
        result[i] = value.substring(p);
      else
        result[i] = value.substring(p, q);
      p = q + 1;
    }
    return result;
  }
  private static String outputDir(Module module, String output)
  {
    StringBuffer result = new StringBuffer();
    String delim = "/";
    if (output.indexOf("\\") != -1)
      delim = "\\";
    String[] mp = seperate(module.packageName, ".");
    String[] op = seperate(output, delim);
    int n = op.length - 1;
    int b = 1;
    if (op[0].charAt(1) == ':')
      b++;
    for (int oi = op.length - 1; oi >= b; oi--)
    {
      if (mp[0].compareTo(op[oi]) == 0)
      {
        n = oi;
        break;
      }
    }
    for (int oi = 0; oi < n; oi++)
    {
      result.append(op[oi]);
      result.append(delim);
    }
    for (int mi = 0; mi < mp.length; mi++)
    {
      result.append(mp[mi]);
      result.append(delim);
    }
    File f = new File(result.toString());
    f.mkdirs();
    return result.toString();
  }
  public static void generateCallStructs(Module module, String stdOutput, PrintWriter outLog) throws Exception
  {
    String output = outputDir(module, stdOutput);
    PrintWriter outData = openOutData(output + module.name + "Structs.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      outData.println("import java.io.Serializable;");
      outData.println("import bbd.crackle.rw.Reader;");
      outData.println("import bbd.crackle.rw.Writer;");
      primeUsings(module);
      generateUsingsNoTables(module, outData);
      outData.println();
      generateStructs(module, outData);
      outData.println("public class " + module.name + "Structs");
      outData.println("{");
      generateReturnStructs(module, outData);
      generateEnums(module, outData, outLog);
      outData.println("  private String version = " + module.version + ";");
      outData.println("  private int signature = " + asHex(module.signature) + ";");
      outData.println("  public String getVersion() {return version;}");
      outData.println("  public int getSignature() {return signature;}");
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateCaseCall(Module module, Prototype prototype, PrintWriter outData, boolean hasReturn)
  {
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput && pd.isOutput)
        outData.println("    _tx." + pd.name + " = " + pd.name + ";");
    }
    String comma = "";
    String work = "    ";
    if (hasReturn)
      work = work + "_tx.result = ";
    work = work + prototype.name + "(";
    outData.print(work);
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma + pd.name);
      if (comma.length() == 0)
        comma = padded(work.length() - 2) + ", ";
    }
    outData.println(");");
  }
  public static void generateDispatch(Module module, String stdOutput, PrintWriter outLog) throws IOException
  {
    String output = outputDir(module, stdOutput);
    PrintWriter outData = openOutData(output + module.name + "Dispatch.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      outData.println("import bbd.crackle.rw.RpcSocket;");
      outData.println("import bbd.crackle.rw.Header;");
      outData.println("import bbd.crackle.rw.Reader;");
      outData.println("import bbd.crackle.rw.Writer;");
      outData.println("import bbd.crackle.rw.Handler;");
      outData.println("import bbd.jportal.util.Connector;");
      outData.println("import org.apache.log4j.Logger;");
      primeUsings(module);
      generateUsings(module, outData);
      outData.println();
      outData.println("public class " + module.name + "Dispatch extends " + module.name + "Impl");
      outData.println("{");
      outData.println("  private static Logger _logger = Logger.getLogger(\"" + packageName(module) + "\");");
      outData.println("  private Handler _handler;");
      outData.println("  private Header  _header;");
      outData.println("  private Reader  _reader;");
      outData.println("  private Writer  _writer;");
      outData.println("  public " + module.name + "Dispatch(Connector _connector)");
      outData.println("  {");
      outData.println("    super(_connector);");
      outData.println("  }");
      generateDispatchProcess(module, outData);
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateDispatchCase(Module module, Prototype prototype, PrintWriter outData)
  {
    outData.println("        case " + asMessage(prototype.name) + ":");
    outData.println("          _" + prototype.name + "();");
    outData.println("          break;");
  }
  public static void generateDispatchFunction(Module module, Prototype prototype, PrintWriter outData)
  {
    boolean hasReturn = buildParameterList(prototype);
    outData.println("  private void _" + prototype.name + "() throws Exception");
    outData.println("  {");
    outData.println("    int _signature = _reader.getInt();");
    outData.println("    _logger.info(\"" + prototype.name + " start\");");
    outData.println("    if (_signature != " + asSignature(prototype.name) + ")");
    outData.println("    {");
    outData.println("      _logger.error(\"" + prototype.name + " Invalid Signature\");");
    outData.println("      _handler.setReply(Message.INV_SIGNATURE.key);");
    outData.println("      return;");
    outData.println("    }");
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
        readCall("_reader", pd.field.type, pd, outData);
    }
    if (hasReturn == true)
      outData.println("    " + prototype.name + "Return _tx = new " + prototype.name + "Return();");
    generateCaseCall(module, prototype, outData, hasReturn);
    outData.println("    _handler.setReply(Message.OK.key);");
    if (hasReturn == true)
      outData.println("    _tx.write(_writer);");
    outData.println("    _logger.info(\"" + prototype.name + " end\");");
    outData.println("    return;");
    outData.println("  }");
  }
  public static void generateDispatchProcess(Module module, PrintWriter outData)
  {
    outData.println("  public void _dispatch(RpcSocket _rpc)");
    outData.println("  {");
    outData.println("    try");
    outData.println("    {");
    outData.println("      _handler = new Handler(_rpc);");
    outData.println("      _header = _handler.getHeader();");
    outData.println("      _reader = _handler.getInput();");
    outData.println("      _writer = _handler.getOutput();");
    outData.println("      _connector.startTran();");
    outData.println("      try");
    outData.println("      {");
    outData.println("        switch (_header.rpcReqID)");
    outData.println("        {");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateDispatchCase(module, prototype, outData);
    }
    outData.println("        default:");
    outData.println("          _handler.setReply(Message.UNKNOWN_FUNCTION.key);");
    outData.println("          break;");
    outData.println("        }");
    outData.println("      }");
    outData.println("      catch (Exception ex)");
    outData.println("      {");
    outData.println("        _connector.flagRollback();");
    outData.println("        throw ex;");
    outData.println("      }");
    outData.println("      finally");
    outData.println("      {");
    outData.println("        _connector.endTran();");
    outData.println("      }");
    outData.println("      _handler.write();");
    outData.println("    }");
    outData.println("    catch (Exception ex)");
    outData.println("    {");
    outData.println("      _handler.setReply(Message.THROWABLE_EXCEPTION.key);");
    outData.println("      _handler.setError(ex);");
    outData.println("      _handler.write();");
    outData.println("    }");
    outData.println("  }");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateDispatchFunction(module, prototype, outData);
    }
  }
  public static void generateEnums(Module module, PrintWriter outData, PrintWriter outLog)
  {
    generateMessageEnums(module, outData);
  }
  public static void generateImplementation(Module module, String stdOutput, PrintWriter outLog) throws IOException
  {
    String output = outputDir(module, stdOutput);
    PrintWriter outData = openOutData(output + module.name + "Impl.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      outData.println("import bbd.jportal.util.Connector;");
      primeUsings(module);
      generateUsings(module, outData);
      outData.println();
      outData.println("public class " + module.name + "Impl extends " + module.name + "Structs implements " + module.name + "IFace");
      outData.println("{");
      outData.println("  protected Connector _connector;");
      outData.println("  public " + module.name + "Impl(Connector _connector)");
      outData.println("  {");
      outData.println("    this._connector = _connector;");
      outData.println("  }");
      generateAssignCalls(module, outData);
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateInterface(Module module, String stdOutput, PrintWriter outLog) throws Exception
  {
    String output = outputDir(module, stdOutput);
    PrintWriter outData = openOutData(output + module.name + "IFace.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      primeUsings(module);
      generateUsingsNoTables(module, outData);
      outData.println();
      outData.println("public interface " + module.name + "IFace");
      outData.println("{");
      for (int i = 0; i < module.code.size(); i++)
      {
        String codeLine = (String)module.code.elementAt(i);
        outData.println("   " + codeLine);
      }
      generateInterfaceConsts(module, outData);
      generateInterfaceCalls(module, outData);
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateInterfaceCall(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String arrayed = prototype.type.reference == Type.ARRAYED ? "[]" : "";
    String work = "  public " + (hasReturn ? prototype.type.javaName()+arrayed : "void") + " " + prototype.name + "(";
    outData.print(work);
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter)parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma + pd.fullType() + " " + pd.name);
      if (comma.length() == 0)
        comma = padded(work.length() - 2) + ", ";
    }
    outData.println(") throws Exception;");
  }
  public static void generateInterfaceCalls(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateInterfaceCall(module, prototype, i, outData);
    }
  }
  public static void generateInterfaceConsts(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      outData.println("  public static final int " + asMessage(prototype.name) + " = " + asHex(prototype.message) + ";");
      outData.println("  public static final int " + asSignature(prototype.name) + " = " + asHex(prototype.signature(true)) + ";");
    }
  }
  public static void generateMessageEnums(Module module, PrintWriter outData)
  {
    outData.println("  public static enum Message");
    outData.println("  { OK(0, \"OK\")");
    int mb = 0;
    if (module.messageBase > 0)
      mb = module.messageBase - 1;
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message)module.messages.elementAt(i);
      outData.println("  , " + message.name + "(" + (++mb) +", " + message.value + ")");
    }
    outData.println("  , INV_SIGNATURE(" + (++mb) + ", \"Invalid Signature\")");
    outData.println("  , UNKNOWN_FUNCTION(" + (++mb) + ", \"Unknown Function\")");
    outData.println("  , THROWABLE_EXCEPTION(" + (++mb) + ", \"Exception Exception\")");
    outData.println("  , LAST_LAST(" + (++mb) + ", \"??Last??\");");
    outData.println("    public final int key;");
    outData.println("    public final String value;");
    outData.println("    Message(int key, String value)");
    outData.println("    {");
    outData.println("      this.key = key;");
    outData.println("      this.value = value;");
    outData.println("    }");
    outData.println("    public static Message get(int no)");
    outData.println("    {");
    outData.println("      for (Message op : values())");
    outData.println("        if (op.key == no) return op;");
    outData.println("      return LAST_LAST;");
    outData.println("    }");
    outData.println("    public String toString()");
    outData.println("    {");
    outData.println("      return value;");
    outData.println("    }");
    outData.println("  }");
  }
  public static void generateReturnStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("  public static class " + prototype.name + "Return");
    outData.println("  {");
    RcString rcs = nullReturn(prototype);
    outData.println("    public " + prototype.type.javaName() + " result = " + rcs.result + ";");
    outData.println("    public " + prototype.name + "Return()");
    outData.println("    {");
    outData.println("    }");
    outData.println("    public " + prototype.name + "Return(" + prototype.type.javaName() + " result)");
    outData.println("    {");
    outData.println("      this.result = result;");
    outData.println("    }");
    outData.println("    public void read(Reader reader) throws Exception");
    outData.println("    {");
    if (rcs.rc == true)
      outData.println("      result.read(reader);");
    else
      readCall("result", prototype.type, null, outData);
    outData.println("    }");
    outData.println("    public void write(Writer writer) throws Exception");
    outData.println("    {");
    if (rcs.rc == true)
      outData.println("      result.write(writer);");
    else
      writeCall("result", prototype.type, outData);
    outData.println("    }");
    outData.println("  }");
  }
  public static void generateReturnArrayedStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("  public static class " + prototype.name + "Return");
    outData.println("  {");
    //RcString rcs = nullReturn(prototype);
    String javaname = prototype.type.javaName();
    outData.println("    public " + javaname + "[] result = new " + javaname + "[0];");
    outData.println("    public " + prototype.name + "Return()");
    outData.println("    {");
    outData.println("    }");
    outData.println("    public " + prototype.name + "Return(" + javaname + "[] result)");
    outData.println("    {");
    outData.println("      this.result = result;");
    outData.println("    }");
    outData.println("    public void read(Reader reader) throws Exception");
    outData.println("    {");
    outData.println("      int no_of = reader.getInt();");
    outData.println("      for (int i = 0; i < no_of; i++)");
    outData.println("        result[i].read(reader);");
    outData.println("    }");
    outData.println("    public void write(Writer writer) throws Exception");
    outData.println("    {");
    outData.println("      int no_of = (int) result.length;");
    outData.println("      writer.putInt(no_of);");
    outData.println("      for (int i = 0; i < no_of; i++)");
    outData.println("        result[i].write(writer);");
    outData.println("    }");
    outData.println("  }");
  }
  public static void generateReturnStructs(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    if (hasReturn == true)
      if (prototype.type.reference == Type.ARRAYED)
        generateReturnArrayedStruct(prototype, outData);
      else
        generateReturnStruct(prototype, outData);
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
  private static void generateStructs(Module module, PrintWriter outData) throws Exception
  {
    primeUsings(module);
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      generateUsings(module, structure.fields, outData);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      generateStruct(module, structure, outData);
    }
  }
  private static void generateStruct(Module module, Structure structure, PrintWriter outData) throws Exception
  {
    outData.println("class " + structure.name);
    outData.println("{");
    for (int i = 0; i < structure.fields.size(); i++)
    {
      Field field = (Field)structure.fields.elementAt(i);
      outData.println("  public " + field.type.javaStructDef(field.name) + " = " + assignValue(field) + ";");
    }
    outData.println("  public void read(Reader reader) throws Exception");
    outData.println("  {");
    for (int i = 0; i < structure.fields.size(); i++)
    {
      Field field = (Field)structure.fields.elementAt(i);
      readCall(field.name, field.type, null, outData);
    }
    outData.println("  }");
    outData.println("  public void write(Writer writer) throws Exception");
    outData.println("  {");
    for (int i = 0; i < structure.fields.size(); i++)
    {
      Field field = (Field)structure.fields.elementAt(i);
      writeCall(field.name, field.type, outData);
    }
    outData.println("  }");
    outData.println("}");
  }
  public static void generateUsings(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      int n = structure.header.length();
      if (n > 0)
        printImport(structure.name, structure.header.substring(1, n - 1), outData);
    }
  }
  private static void generateUsings(Module module, Vector<Field> fields, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      int n = structure.header.length();
      if (n > 0)
      {
        String importString = structure.header.substring(1, n - 1);
        for (int j = 0; j < fields.size(); j++)
        {
          Field field = (Field) fields.elementAt(j);
          if (structure.name.compareTo(field.type.name) == 0)
          {
            printImport(structure.name, importString, outData);
            break;
          }
        }
      }
    }
  }
  public static void generateUsingsNoTables(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      int n = structure.header.length();
      if (n == 0)
        continue;
      String imp = structure.header.substring(1, n - 1);
      if (imp.indexOf("TABLE CLASS") > 0)
        continue;
      printImport(structure.name, imp, outData);
    }
  }
  public static void generateUsingsNoTablesNoStructs(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      int n = structure.header.length();
      if (n == 0)
        continue;
      String imp = structure.header.substring(1, n - 1);
      if (imp.indexOf("TABLE CLASS") > 0)
        continue;
      if (imp.indexOf("STRUCT CLASS") > 0)
        continue;
      printImport(structure.name, imp, outData);
    }
  }
  public static void main(String[] args)
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      errLog = outLog;
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        in.close();
        Module module = (Module) in.readObject();
        generate(module, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
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
  private static PrintWriter openOutData(String fileName, PrintWriter outLog) throws FileNotFoundException, IOException
  {
    outLog.println("Code: " + fileName);
    outFile = new FileOutputStream(fileName);
    PrintWriter outData = new PrintWriter(outFile);
    outData.println("/*");
    outData.println(" * This code was generated, do not modify it here");
    outData.println(" * modify it at source and regenerate it.");
    outData.println(" * 1: Mutilation, Spindling and Bending will result in goto 1");
    outData.println(" */");
    outData.println();
    return outData;
  }
  private static String packageName(Module module)
  {
    if (module.packageName.length() > 0)
      return module.packageName;
    return "bbd." + module.name.toLowerCase() + ".crackle";
  }
  private static String padded(int size)
  {
    if (size > padString.length())
      size = padString.length();
    return padString.substring(0, size);
  }
  private static void primeUsings(Module module)
  {
    usings = new Vector<String>();
    usings.addElement("import " + packageName(module) + ".*;");
  }
  private static void printImport(String name, String data, PrintWriter outData)
  {
    // 0123456789012345678901234
    // import aaa.bbb.CCC; // xx -- CCC (l=25 m=3 n=15 k=20)
    int m = name.length();
    int n = data.indexOf(name);
    int l = data.length();
    int k = data.indexOf(";");
    if (k > 0)
      l = k + 1;
    if (l - n == m + 1) // 19-15 == 3+1
      data = data.substring(0, n) + "*;";
    for (int i = 0; i < usings.size(); i++)
      if (data.compareTo((String) usings.elementAt(i)) == 0)
        return;
    outData.println(data);
    usings.addElement(data);
  }
  private static void readCall(String name, Type type, Parameter pd, PrintWriter outData)
  {
    boolean isArray = false;
    boolean hasInputSize = false;
    if (type.reference == Type.ARRAYED)
      isArray = true;
    String var = name;
    String reader = "reader";
    String pad = "      ";
    Type useType = type;
    if (pd != null)
    {
      if (pd.isArray == true)
        isArray = true;
      hasInputSize = pd.hasInputSize;
      useType = pd.field.type;
      var = pd.name;
      reader = name;
      pad = "    ";
    }
    switch (useType.typeof)
    {
      case Type.BOOLEAN:
        outData.println(pad + "byte " + var + " = " + reader + ".getByte() == 0 ? false : true;");
        break;
      case Type.CHAR:
        if (type.arraySizes.size() > 0 || type.reference == Type.BYPTR)
        {
          outData.println(pad + "int " + var + "_length = " + reader + ".getInt();");
          outData.println(pad + "String " + var + " = " + reader + ".getString(" + var + "_length);");
        }
        else
          outData.println(pad + "char " + var + " = " + reader + ".getByte();");
        break;
      case Type.BYTE:
        outData.println(pad + "byte " + var + " = " + reader + ".getByte();");
        break;
      case Type.STRING:
        outData.println(pad + "int " + var + "_length = " + reader + ".getInt();");
        outData.println(pad + "String " + var + " = " + reader + ".getString(" + var + "_length);");
        break;
      case Type.SHORT:
        outData.println(pad + "short " + var + " = " + reader + ".getShort();");
        break;
      case Type.INT:
        outData.println(pad + "int " + var + " = " + reader + ".getInt();");
        break;
      case Type.LONG:
        outData.println(pad + "long " + var + " = " + reader + ".getLong();");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println(pad + "double " + var + " = " + reader + ".getDouble();");
        break;
      default:
        if (isArray == true)
        {
          outData.println(pad + "int " + var + "_count = " + reader + ".getInt();");
          outData.println(pad + useType.name + "[] " + var + " = new " + useType.name + "[" + var + "_count];");
          outData.println(pad + "for (int i = 0; i < " + var + "_count; i++)");
          outData.println(pad + "{");
          outData.println(pad + "  " + var + "[i] = new " + useType.name + "();");
          outData.println(pad + "  " + var + "[i].read(" + reader + ");");
          outData.println(pad + "}");
        }
        else if (hasInputSize == true)
        {
          outData.println(pad + useType.name + "[] " + var + " = new " + useType.name + "[" + pd.inputSizeValue + "];");
          outData.println(pad + "for (int i = 0; i < " + pd.inputSizeValue + "; i++)");
          outData.println(pad + "{");
          outData.println(pad + "  " + var + "[i] = new " + useType.name + "();");
          outData.println(pad + "  " + var + "[i].read(" + reader + ");");
          outData.println(pad + "}");
        }
        else
        {
          outData.println(pad + useType.name + " " + var + " = new " + useType.name + "();");
          outData.println(pad + var + ".read(" + reader + ");");
        }
        break;
    }    
  }
  private static void writeCall(String name, Type type, PrintWriter outData)
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
          outData.println("      int " + var + "_length = " + var + ".length();");
          outData.println("      writer.putInt(" + var + "_length);");
          outData.println("      writer.putString(" + var + ", " + var + "_length);");
        }
        else
          outData.println("      writer.putByte(" + var + ");");
        break;
      case Type.BYTE:
        outData.println("      writer putByte(" + var + ");");
        break;
      case Type.STRING:
        outData.println("      int " + var + "_length = " + var + ".length();");
        outData.println("      writer.putInt(" + var + "_length);writer.filler(4);");
        outData.println("      writer.putString(" + var + ", " + var + "_length);");
        break;
      case Type.SHORT:
        outData.println("      writer.putShort(" + var + ");");
        break;
      case Type.INT:
        outData.println("      writer.putInt(" + var + ");");
        break;
      case Type.LONG:
        outData.println("      writer.putLong(" + var + ");");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println("      writer.putDouble(" + var + ");");
        break;
      default:
        if (isArray == true)
        {
          outData.println("      int " + var + "_count = " + var + ".length;");
          outData.println("      writer.putInt(" + var + "_count);");
          outData.println("      for (int i = 0; i < " + var + "_count; i++)");
          outData.println("        " + var + "[i].write(writer);");
        }
        else
          outData.println("      " + var + ".write(writer);");
        break;
    }
  }
}

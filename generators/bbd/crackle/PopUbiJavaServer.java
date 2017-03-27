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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbiJavaServer extends Generator
{
  public static String description()
  {
    return "Generates Java Server Code for Ubi Client Code";
  }
  public static String documentation()
  {
    return "Generates Java Server Code for Ubi Client Code" + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries.";
  }
  private static boolean alignForSun = false;
  public static PrintWriter errLog;
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
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    try
    {
      outLog.println(module.name + " version " + module.version);
      for (int i = 0; i < module.pragmas.size(); i++)
      {
        String pragma = (String) module.pragmas.elementAt(i);
        if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
        {
          outLog.println(module.name + ": Align For Sun");
          alignForSun = true;
        }
      }
      generateStructs(module, output, outLog);
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
  static OutputStream outFile;
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
  private static Vector<String> usings;
  private static void generateStructs(Module module, String output, PrintWriter outLog) throws Exception
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      usings = new Vector<String>();
      generateStruct(module, structure, output, outLog);
    }
  }
  private static String packageName(Module module)
  {
    if (module.packageName.length() > 0)
      return module.packageName;
    return "bbd." + module.name.toLowerCase() + ".crackle";
  }
  private static void primeUsings(Module module)
  {
    usings = new Vector<String>();
    usings.addElement("import " + packageName(module) + ".*;");
  }
  private static void generateStruct(Module module, Structure structure, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output + structure.name + ".java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      primeUsings(module);
      generateUsings(module, structure.fields, outData);
      outData.println();
      int relativeOffset = 0;
      int fillerNo = 0;
      outData.println("public class " + structure.name);
      outData.println("{");
      for (int i = 0; i < structure.fields.size(); i++)
      {
        Field field = (Field) structure.fields.elementAt(i);
        int n = relativeOffset % 8;
        if (n > 0)
        {
          n = 8-n;
          outData.println("  byte[] _fill"+fillerNo+" = new byte["+n+"];");
          fillerNo++;
          relativeOffset += n;
        }
        outData.println("  public " + field.type.javaStructDef(field.name) + " = " + assignValue(field) + ";");
        relativeOffset += field.type.relativeSize(true);
      }
      int n = relativeOffset % 8;
      if (n > 0)
      {
        n = 8-n;
        outData.println("  byte[] _fill"+fillerNo+" = new byte["+n+"];");
      }
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
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
      // outData.println(imp);
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
      // outData.println(imp);
    }
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
  public static void generateDispatch(Module module, String output, PrintWriter outLog) throws IOException
  {
    PrintWriter outData = openOutData(output + module.name + "Dispatch.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      outData.println("import bbd.crackle.rdc.RpcSocket;");
      outData.println("import bbd.crackle.rdc.Header;");
      outData.println("import bbd.crackle.rdc.Reader;");
      outData.println("import bbd.crackle.rdc.Handler;");
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
      outData.println("  private Reader  _input;");
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
  public static void generateDispatchProcess(Module module, PrintWriter outData)
  {
    outData.println("  public void _dispatch(RpcSocket _rpc)");
    outData.println("  {");
    outData.println("    try");
    outData.println("    {");
    outData.println("      _handler = new Handler(_rpc);");
    outData.println("      _header = _handler.getHeader();");
    outData.println("      _input = _handler.getInput();");
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
    outData.println("    int _signature = _input.readInt();");
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
        outData.println("    " + pd.fullType() + " " + pd.field.name + " = " + readType(pd) + ";");
    }
    if (hasReturn == true)
      outData.println("    Z" + prototype.name + "Return _tx = new Z" + prototype.name + "Return();");
    generateCaseCall(module, prototype, outData, hasReturn);
    outData.println("    _handler.setReply(Message.OK.key);");
    if (hasReturn == true)
      outData.println("    _handler.setOutput(_tx);");
    outData.println("    _logger.info(\"" + prototype.name + " end\");");
    outData.println("    return;");
    outData.println("  }");
  }
  private static String readType(Parameter pd)
  {
    int noOccurs = 0;
    int arrStart = pd.field.type.typeof == Type.CHAR ? 1 : 0;
    for (int i = arrStart; i < pd.field.type.arraySizes.size(); i++)
    {
      Integer no = (Integer) pd.field.type.arraySizes.elementAt(i);
      noOccurs += no.intValue();
    }
    String occNo = noOccurs > 0 ? ", " + noOccurs : "";
    String align8 = alignForSun == true ? ", true" : "";
    if (pd.hasInputSize == true)
      occNo = ", " + pd.inputSizeValue;
    switch (pd.field.type.typeof)
    {
    case Type.USERTYPE:
      return "(" + pd.fullType() + ")_input.readObject(" + pd.type + ".class" + occNo + align8 + ")";
    case Type.BOOLEAN:
      return "_input.readBoolean(" + occNo + align8 + ")";
    case Type.CHAR:
      return "_input.readString(" + occNo + align8 + ")";
    case Type.SHORT:
      return "_input.readShort(" + occNo + align8 + ")";
    case Type.LONG:
      return "_input.readLong(" + occNo + align8 + ")";
    case Type.FLOAT:
    case Type.DOUBLE:
      return "_input.readDouble(" + occNo + align8 + ")";
    case Type.BYTE:
      return "_input.readByte(" + occNo + align8 + ")";
    case Type.INT:
      return "_input.readInt(" + occNo + align8 + ")";
    case Type.STRING:
      return "_input.readString(" + occNo + align8 + ")";
    default:
      break;
    }
    return "<unknown>";
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
  public static String asMessage(String name)
  {
    return name.toUpperCase() + "_MESSAGE";
  }
  public static String asSignature(String name)
  {
    return name.toUpperCase() + "_SIGNATURE";
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
  public static Vector<Parameter> parameterList;
  public static boolean buildParameterList(Prototype prototype)
  {
    parameterList = new Vector<Parameter>();
    boolean hasReturn = Parameter.build(parameterList, prototype, false);
    return hasReturn;
  }
  static String padString = "                                                         ";
  private static String padded(int size)
  {
    if (size > padString.length())
      size = padString.length();
    return padString.substring(0, size);
  }
  public static void generateEnums(Module module, PrintWriter outData, PrintWriter outLog)
  {
    generateMessageEnums(module, outData);
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
  public static void generateCallStructs(Module module, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output + module.name + "Structs.java", outLog);
    try
    {
      outData.println("package " + packageName(module) + ";");
      outData.println();
      outData.println("import java.io.Serializable;");
      primeUsings(module);
      generateUsingsNoTables(module, outData);
      outData.println();
      outData.println("public class " + module.name + "Structs");
      outData.println("{");
      generateCallStructs(module, outData);
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
  public static void generateCallStructs(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateCallStructs(module, prototype, i, outData);
    }
  }
  public static void generateCallStructs(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    if (hasReturn == true)
      generateReturnStruct(prototype, outData);
  }
  public static void generateReturnStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("  public static class Z" + prototype.name + "Return");
    outData.println("  {");
    outData.println("    public " + prototype.type.javaName() + " result = " + nullReturn(prototype) + ";");
    outData.println("    public Z" + prototype.name + "Return()");
    outData.println("    {");
    outData.println("    }");
    outData.println("    public Z" + prototype.name + "Return(" + prototype.type.javaName() + " result)");
    outData.println("    {");
    outData.println("      this.result = result;");
    outData.println("    }");
    outData.println("  }");
  }
  private static String nullReturn(Prototype prototype)
  {
    String result = assignValue(prototype.type);
    if (prototype.type.typeof == Type.USERTYPE)
      result = result + "()";
    return result;
  }
  public static void generateInterface(Module module, String output, PrintWriter outLog) throws Exception
  {
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
  public static void generateInterfaceCall(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String work = "  public " + (hasReturn ? prototype.type.javaName() : "void") + " " + prototype.name + "(";
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
  public static void generateImplementation(Module module, String output, PrintWriter outLog) throws IOException
  {
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
  public static void generateAssignCall(Module module, Prototype prototype, PrintWriter outData)
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String work = "  public " + (hasReturn ? prototype.type.javaName() : "void") + " " + prototype.name + "(";
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
}

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopGenJava extends Generator
{
  public static String description()
  {
    return "Generates (Java|J#) Client Proxy Code for (Java|J#) Server Assign Code";
  }
  public static String documentation()
  {
    return "Generates (Java|J#) Client Proxy Code for (Java|J#) Server Assign Code"
      + "\r\nHandles following pragmas"
      + "\r\n  KeepTee - Keep the dreaded small tee in the structures."
      ;
  }
  private static boolean keepTee = false;
  public static PrintWriter errLog;
	public static Vector<Parameter> parameterList;
	public static boolean buildParameterList(Prototype prototype)
	{
		parameterList = new Vector<Parameter>();
		boolean hasReturn = Parameter.build(parameterList, prototype, keepTee);
		return hasReturn;
	}
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
	static OutputStream outFile;
  private static PrintWriter openOutData(String fileName, PrintWriter outLog) throws FileNotFoundException, IOException
  {
		outLog.println("Code: "+fileName);
  	outFile = new FileOutputStream(fileName);
	  PrintWriter outData = new PrintWriter(outFile);
		outData.println("/*");
    outData.println(" * This code was generated, do not modify it here");
    outData.println(" * modify it at source and regenerate it.");
    outData.println(" * Mutilation, Spindlization and Bending will result in ");
    outData.println(" * Mutilation, Spindlization and Bending.");
    outData.println(" */");
    outData.println();
		return outData;
  }
  static Vector<String> usings;
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    try
    {
      usings = new Vector<String>();
      errLog = outLog;
      outLog.println(module.name+" version "+module.version);
      for (int i = 0; i < module.pragmas.size(); i++)
      {
        String pragma = (String) module.pragmas.elementAt(i);
        if (pragma.trim().equalsIgnoreCase("KeepTee") == true)
          keepTee = true;
      }
      generateStructs(module, output, outLog);
      generateCallStructs(module, output, outLog);
      generateInterface(module, output, outLog);
      generateProxy(module, output, outLog);
      generateImplementation(module, output, outLog);
      generateAssign(module, output, outLog);
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  public static void generateStructs(Module module, String output, PrintWriter outLog) throws Exception
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
    return "bbd."+module.name.toLowerCase()+".crackle";
  }
  public static void generateStruct(Module module, Structure structure, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output+dropTee(structure.name)+".java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import java.io.Serializable;");
      usings = new Vector<String>();
      generateUsings(module, structure.fields, outData);
      outData.println();
      outData.println("public class "+dropTee(structure.name)+" implements Serializable");
      outData.println("{");
      outData.println("  private static final long serialVersionUID = 1L;");
      for (int i = 0; i < structure.fields.size(); i++)
      {
        Field field = (Field) structure.fields.elementAt(i);
        outData.println("  public "+field.type.javaStructDef(field.name)+" = "+assignValue(field)+";");
      }
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateUsings(Module module, Vector<Field> fields, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      int n = structure.header.length();
      if (n > 0)
      {
        String importString = structure.header.substring(1,n-1);
        for (int j=0; j<fields.size(); j++)
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
  public static String asMessage(String name)
  {
    return name.toUpperCase()+"_MESSAGE";
  }
  public static String asSignature(String name)
  {
    return name.toUpperCase()+"_SIGNATURE";
  }
  public static String asHex(int value)
  {
    return "0x"+Integer.toHexString(value);
  }
  public static String asHex(long value)
  {
    return "0x"+Integer.toHexString((int)value);
  }
  public static String asHex(String value)
  {
    return "0x"+Integer.toHexString(Integer.parseInt(value));
  }
  public static void generateCallStructs(Module module, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output+module.name+"Structs.java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import java.io.Serializable;");
      usings = new Vector<String>();
      generateUsingsNoTables(module, outData);
      outData.println();
      generateCallStructs(module, outData);
      outData.println("public class "+module.name+"Structs");
      outData.println("{");
      outData.println("  private String version = "+module.version+";");
      outData.println("  private int signature = "+asHex(module.signature)+";");
      outData.println("  public String getVersion() {return version;}");
      outData.println("  public int getSignature() {return signature;}");
      generateEnums(module, outData, outLog);
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateEnums(Module module, PrintWriter outData, PrintWriter outLog)
  {
    generateMessageEnums(module, outData);
    generateMessageLookups(module, outData);
  }
  public static void generateMessageEnums(Module module, PrintWriter outData)
  {
    outData.println("  public static class Message");
    outData.println("  { public static final int OK = 0");
    int mb = 0;
    if (module.messageBase > 0)
      mb = module.messageBase-1;
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message) module.messages.elementAt(i);
      outData.println("  , "+message.name+" = "+(++mb));
    }
    outData.println("  , INV_SIGNATURE = "+(++mb));
    outData.println("  , UNKNOWN_FUNCTION = "+(++mb));
    outData.println("  , THROWABLE_EXCEPTION = "+(++mb));
    outData.println("  , LAST_LAST = "+(++mb)+";");
    outData.println("  }");
  }
  public static void generateCallStructs(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateCallStructs(module, prototype, i, outData);
    }
  }
  public static void generateCallStructs(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    if (prototype.inputs.size() > 0)
      generateInputsStruct(prototype, outData);
    if (prototype.outputs.size() > 0)
      throw new Exception("Output parameters are not supported in Java generation");
    if (hasReturn == true)
      generateReturnStruct(prototype, outData);
  }
  public static void generateInputsStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("class Z"+prototype.name+"Input implements Serializable");
    outData.println("{");
    outData.println("  private static final long serialVersionUID = "+asHex(prototype.signature(false))+";");
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
        outData.println("  public "+pd.fullType()+" "+pd.name+";");
    }
    String comma = "";
    String work = "  public Z"+prototype.name+"Input(";
    outData.print(work);
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
      {
        if (comma.length() > 0)
          outData.println();
        outData.print(comma+pd.fullType()+" "+pd.name);
        if (comma.length() == 0)
          comma = padded(work.length()-2)+", ";
      }
    }
    outData.println(")");
    outData.println("  {");
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
        outData.println("    this."+pd.name+" = "+pd.name+";");
    }
    outData.println("  }");
    outData.println("}");
  }
  public static void generateReturnStruct(Prototype prototype, PrintWriter outData)
  {
    outData.println("class Z"+prototype.name+"Return implements Serializable");
    outData.println("{");
    outData.println("  private static final long serialVersionUID = "+asHex(prototype.signature(false))+";");
    outData.println("  public "+prototype.type.javaName()+" result = "+nullReturn(prototype)+";");
    outData.println("  public Z"+prototype.name+"Return()");
    outData.println("  {");
    outData.println("  }");
    outData.println("  public Z"+prototype.name+"Return("+prototype.type.javaName()+" result)");
    outData.println("  {");
    outData.println("    this.result = result;");
    outData.println("  }");
    outData.println("}");
  }
  public static void generateInterface(Module module, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output+module.name+"IFace.java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import bbd.crackle.rpc.BaseIFace;");
      outData.println("import java.util.Hashtable;");
      usings = new Vector<String>();
      generateUsingsNoTables(module, outData);
      outData.println();
      outData.println("public interface "+module.name+"IFace extends BaseIFace");
      outData.println("{");
      for (int i=0; i<module.code.size(); i++)
      {
        String codeLine = (String) module.code.elementAt(i);
        outData.println("   "+codeLine);
      }
      generateInterfaceConsts(module, outData);
      generateInterfaceCalls(module, outData);
      outData.println("  public Hashtable get_CallersPouch();");
      outData.println("  public void set_CallersPouch(Hashtable pouch);");
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
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      outData.println("  public static final int "+asMessage(prototype.name)+" = "+asHex(prototype.message)+";");
      outData.println("  public static final int "+asSignature(prototype.name)+" = "+asHex(prototype.signature(false))+";");
    }
  }
  public static void generateInterfaceCalls(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateInterfaceCall(module, prototype, i, outData);
    }
  }
  public static void generateInterfaceCall(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String work = "  public "+(hasReturn ? prototype.type.javaName():"void")+" "+prototype.name+"(";
    outData.print(work);
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma+pd.fullType()+" "+pd.name);
      if (comma.length() == 0)
        comma = padded(work.length()-2)+", ";
    }
    outData.println(") throws Exception;");
  }
  public static void generateProxy(Module module, String output, PrintWriter outLog) throws Exception
  {
    PrintWriter outData = openOutData(output+module.name+"Proxy.java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import bbd.crackle.rpc.Rpc;");
      outData.println("import java.util.Hashtable;");
      usings = new Vector<String>();
      generateUsingsNoTables(module, outData);
      outData.println();
      outData.println("public class "+module.name+"Proxy extends "+module.name+"Structs implements "+module.name+"IFace");
      outData.println("{");
      outData.println("  public Rpc _rpc;");
      generateProxyCalls(module, outData);
      outData.println("  public Hashtable get_CallersPouch()");
      outData.println("  {");
      outData.println("    return ((bbd.crackle.rpc.SocketRpc)_rpc).get_CallersPouch();");
      outData.println("  }");
      outData.println("  public void set_CallersPouch(Hashtable pouch)");
      outData.println("  {");
      outData.println("    ((bbd.crackle.rpc.SocketRpc)_rpc).set_CallersPouch(pouch);");
      outData.println("  }");
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateProxyCalls(Module module, PrintWriter outData) throws Exception
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateProxyCall(module, prototype, i, outData);
    }
  }
  public static void generateProxyCall(Module module, Prototype prototype, int no, PrintWriter outData) throws Exception
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String work = "  public "+(hasReturn ? prototype.type.javaName():"void")+" "+prototype.name+"(";
    outData.print(work);
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma+pd.fullType()+" "+pd.name);
      if (comma.length() == 0)
        comma = padded(work.length()-2)+", ";
    }
    outData.println(") throws Exception");
    outData.println("  {");
    if (prototype.inputs.size() > 0)
      generateCallInputs(prototype, outData);
    if (prototype.outputs.size() > 0)
      throw new Exception("Outputs are not supported by java as there are no reference parameters.");
    if (hasReturn == true)
      generateCallReturn(prototype, outData);
    else
      generateCall(prototype, outData);
    outData.println("  }");
  }
  public static void generateCallInputs(Prototype prototype, PrintWriter outData)
  {
    outData.print("    Z"+prototype.name+"Input _tx");
    outData.print(" = new Z"+prototype.name+"Input(");
    String comma = "";
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
      {
        outData.print(comma+pd.name);
        comma = ", ";
      }
    }
    outData.println(");");
  }
  public static void generateCallReturn(Prototype prototype, PrintWriter outData)
  {
    if (prototype.inputs.size() > 0)
      outData.println("    Object _obj = _rpc.call(" + asMessage(prototype.name) + ", " + asSignature(prototype.name) + ", _tx);");
    else
      outData.println("    Object _obj = _rpc.call(" + asMessage(prototype.name) + ", " + asSignature(prototype.name) + ");");
    outData.println("    if (_obj == null) return " + nullReturn(prototype) + ";");
    outData.println("    Z" + prototype.name + "Return _rx = (Z" + prototype.name + "Return) _obj;");
    outData.println("    return _rx.result;");
  }
  private static String assignValue(Type type)
  {
    if (type.reference != Type.ARRAYED)
    {
      switch (type.typeof)
      {
        case Type.BOOLEAN: return "false";
        case Type.CHAR: return "0";
        case Type.SHORT: return "0";
        case Type.LONG: return "0";
        case Type.FLOAT: return "0.0";
        case Type.DOUBLE: return "0.0";
        case Type.VOID: return "null";
        case Type.BYTE: return "0";
        case Type.INT: return "0";
        case Type.STRING: return "\"\"";
      }
    }
    return "new "+type.javaName();
  }
  private static String nullReturn(Prototype prototype)
  {
    String result = assignValue(prototype.type);
    if (prototype.type.typeof == Type.USERTYPE)
      result = result + "()";
    return result;
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
  public static void generateCall(Prototype prototype, PrintWriter outData)
  {
    if (prototype.inputs.size() > 0)
      outData.println("    _rpc.call("+asMessage(prototype.name)+", "+asSignature(prototype.name)+", _tx);");
    else
      outData.println("    _rpc.call("+asMessage(prototype.name)+", "+asSignature(prototype.name)+");");
  }
  public static void generateAssign(Module module, String output, PrintWriter outLog) throws IOException
  {
    PrintWriter outData = openOutData(output+module.name+"Assign.java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import bbd.crackle.rpc.Rpc;");
      outData.println("import bbd.crackle.rpc.Header;");
      outData.println("import bbd.crackle.rpc.Event;");
      outData.println("import bbd.jportal.Connector;");
      outData.println("import org.apache.log4j.Logger;");
      usings = new Vector<String>();
      generateUsingsNoTablesNoStructs(module, outData);
      outData.println();
      outData.println("public class "+module.name+"Assign extends "+module.name+"Impl");
      outData.println("{");
      outData.println("  private static Logger _logger = Logger.getLogger(\""+packageName(module)+"\");");
      outData.println("  public "+module.name+"Assign(Connector connector)");
      outData.println("  {");
      outData.println("    super(connector);");
      outData.println("  }");
      generateAssignProcess(module, outData);
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateImplementation(Module module, String output, PrintWriter outLog) throws IOException
  {
    PrintWriter outData = openOutData(output+module.name+"Impl.java", outLog);
    try
    {
      outData.println("package "+packageName(module)+";");
      outData.println();
      outData.println("import bbd.crackle.rpc.Rpc;");
      outData.println("import bbd.jportal.Connector;");
      outData.println("import java.util.Hashtable;");
      usings = new Vector<String>();
      generateUsings(module, outData);
      outData.println();
      outData.println("public class "+module.name+"Impl extends "+module.name+"Structs implements "+module.name+"IFace");
      outData.println("{");
      outData.println("  protected Connector connector;");
      outData.println("  protected Rpc _rpc;");
      outData.println("  public "+module.name+"Impl(Connector connector)");
      outData.println("  {");
      outData.println("    this.connector = connector;");
      outData.println("  }");
      generateAssignCalls(module, outData);
      outData.println("  protected Hashtable _pouch;");
      outData.println("  public Hashtable get_CallersPouch()");
      outData.println("  {");
      outData.println("    return _pouch;");
      outData.println("  }");
      outData.println("  public void set_CallersPouch(Hashtable pouch)");
      outData.println("  {");
      outData.println("    _pouch = pouch;");
      outData.println("  }");
      outData.println("}");
    }
    finally
    {
      outData.flush();
      outFile.close();
    }
  }
  public static void generateAssignProcess(Module module, PrintWriter outData)
  {
    outData.println("  private Header _header;");
    outData.println("  //private Rpc _rpc; moved to Implementation as protected");
    outData.println("  private void _writeHeader(int returnCode)");
    outData.println("  {");
    outData.println("    _header.returnCode = returnCode;");
    outData.println("    _header.eventNo = Event.getEventNo();");
    outData.println("  }");
    outData.println("  public void _process(Rpc rpc)");
    outData.println("  {");
    outData.println("    try");
    outData.println("    {");
    outData.println("      _rpc = rpc;");
    outData.println("      _header = (Header)rpc.read();");
    outData.println("      set_CallersPouch(_header.pouch);");
    outData.println("      _header.hasOutput = false;");
    outData.println("      _header.hasError = false;");
    outData.println("      connector.startTran();");
    outData.println("      try");
    outData.println("      {");
    outData.println("        switch (_header.message)");
    outData.println("        {");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateAssignCase(module, prototype, outData);
    }
    outData.println("        default:");
    outData.println("          _writeHeader(Message.UNKNOWN_FUNCTION);");
    outData.println("          break;");
    outData.println("        }");
    outData.println("      }");
    outData.println("      catch (Exception ex)");
    outData.println("      {");
    outData.println("        connector.flagRollback();");
    outData.println("        throw ex;");
    outData.println("      }");
    outData.println("      finally");
    outData.println("      {");
    outData.println("        connector.endTran();");
    outData.println("      }");
    outData.println("      _rpc.write(_header);");
    outData.println("    }");
    outData.println("    catch (Exception ex)");
    outData.println("    {");
    outData.println("      if (_header == null)");
    outData.println("        _header = new Header();");
    outData.println("      _header.hasError = true;");
    outData.println("      _header.setError(ex);");
    outData.println("      _logger.error(_header.error);");
    outData.println("      _writeHeader(Message.THROWABLE_EXCEPTION);");
    outData.println("      _rpc.write(_header);");
    outData.println("    }");
    outData.println("  }");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateAssignFunction(module, prototype, outData);
    }
  }
  public static void generateAssignCase(Module module, Prototype prototype, PrintWriter outData)
  {
    outData.println("        case "+asMessage(prototype.name)+":");
    outData.println("          _"+prototype.name+"();");
    outData.println("          break;");
  }
  public static void generateAssignFunction(Module module, Prototype prototype, PrintWriter outData)
  {
    boolean hasReturn = buildParameterList(prototype);
    outData.println("  private void _"+prototype.name+"() throws Exception");
    outData.println("  {");
    outData.println("    _logger.info(\""+prototype.name+" start\");");
    outData.println("    if (_header.signature != "+asSignature(prototype.name)+")");
    outData.println("    {");
    outData.println("      _logger.error(\""+prototype.name+" Invalid Signature\");");
    outData.println("      _writeHeader(Message.INV_SIGNATURE);");
    outData.println("      return;");
    outData.println("    }");
    if (prototype.inputs.size() > 0)
      outData.println("    Z"+prototype.name+"Input rx = (Z"+prototype.name+"Input)_header.data;");
    if (hasReturn == true)
      outData.println("    Z"+prototype.name+"Return tx = new Z"+prototype.name+"Return();");
    generateCaseCall(module, prototype, outData, hasReturn);
    if (hasReturn == true)
    {
      outData.println("    _header.hasOutput = tx != null;");
      outData.println("    _header.data = tx;");
    }
    outData.println("    _writeHeader(Message.OK);");
    outData.println("    _logger.info(\""+prototype.name+" end\");");
    outData.println("    return;");
    outData.println("  }");
  }
  public static void generateCaseCall(Module module, Prototype prototype, PrintWriter outData, boolean hasReturn)
  {
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput && pd.isOutput)
        outData.println("    tx."+pd.name+" = rx."+pd.name+";");
    }
    String comma = "";
    String work = "    ";
    if (hasReturn)
      work = work + "tx.result = ";
    work = work + prototype.name+"(";
    outData.print(work);
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma+(pd.isOutput?"tx.":"rx.")+pd.name);
      if (comma.length() == 0)
        comma = padded(work.length()-2)+", ";
    }
    outData.println(");");
  }
  public static void generateAssignCalls(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (prototype.codeType != Prototype.RPCCALL)
        continue;
      generateAssignCall(module, prototype, outData);
    }
  }
  public static void generateAssignCall(Module module, Prototype prototype, PrintWriter outData)
  {
    boolean hasReturn = buildParameterList(prototype);
    String comma = "";
    String work = "  public "+(hasReturn ? prototype.type.javaName():"void")+" "+prototype.name+"(";
    outData.print(work);
    for (int i=0; i<parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (comma.length() > 0)
        outData.println();
      outData.print(comma+pd.fullType()+" "+pd.name);
      if (comma.length() == 0)
        comma = padded(work.length()-2)+", ";
    }
    outData.println(") throws Exception");
    outData.println("  {");
    for (int i=0; i<prototype.code.size(); i++)
    {
      String codeLine = (String) prototype.code.elementAt(i);
      outData.print(codeLine);
    }
    outData.println("  }");
  }
  public static void generateMessageLookups(Module module, PrintWriter outData)
  {
    outData.println("  public static String messageDesc(int no)");
    outData.println("  {");
    outData.println("    switch (no)");
    outData.println("    {");
    outData.println("    case Message.OK: return \"OK\";");
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message) module.messages.elementAt(i);
      outData.println("    case Message."+message.name+": return "+message.value+";");
    }
    outData.println("    case Message.INV_SIGNATURE: return \"Invalid Signature\";");
    outData.println("    case Message.UNKNOWN_FUNCTION: return \"Unknown Function\";");
    outData.println("    case Message.THROWABLE_EXCEPTION: return \"Exception Exception\";");
    outData.println("    }");
    outData.println("    return \"MessageDesc for \"+no+\" Unknown.\";");
    outData.println("  }");
  }
  static void printImport(String name, String data, PrintWriter outData)
  {
    // 0123456789012345678901234
    // import aaa.bbb.CCC; // xx -- CCC (l=25 m=3 n=15 k=20)
    int m = name.length();
    int n = data.indexOf(name);
    int l = data.length();
    int k = data.indexOf(";");
    if (k > 0) l = k+1;
    if (l - n == m + 1) // 19-15 == 3+1
      data = data.substring(0, n)+"*;";
    for (int i=0; i<usings.size(); i++)
      if (data.compareTo((String)usings.elementAt(i)) == 0)
        return;
    outData.println(data);
    usings.addElement(data);
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
      String imp = structure.header.substring(1,n-1);
      if (imp.indexOf("TABLE CLASS") > 0)
        continue;
      if (imp.indexOf("STRUCT CLASS") > 0)
        continue;
      printImport(structure.name, imp, outData);
      //outData.println(imp);
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
      String imp = structure.header.substring(1,n-1);
      if (imp.indexOf("TABLE CLASS") > 0)
        continue;
      printImport(structure.name, imp, outData);
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
        printImport(structure.name, structure.header.substring(1,n-1), outData);
    }
  }
  public static String lowerFirst(String in)
	{
		String result = in.substring(0,1).toLowerCase()+in.substring(1);
		return result;
	}
	public static String dropTee(String in)
	{
		String result = in;
		if (in.compareTo("char") == 0)
		{
			result = "String";
		}
		else if (keepTee == false)
		{
			if (result.charAt(0) == 't')
				result = result.substring(1);
		}
		return result;
	}
  static String padString = "                                                         "; 
  private static String padded(int size)
  {
    if (size > padString.length())
      size = padString.length();
    return padString.substring(0, size);
  }
}

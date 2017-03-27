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
import java.io.File;
import java.util.Vector;
import java.util.HashMap;

public class PopUbiJavaClient extends Generator
{
  public static String description()
  {
    return "Generates Java Client Code for C++ Server Code";
  }
  public static String documentation()
  {
    return "Generates Java Client Code for C++ Server Code";
  }
  public static PrintWriter errLog;

  public static class Pragmas
  {
    private static Vector<String> selected;
    private static HashMap<String, String> packages;
    private static final String UBI_JAVA_CLIENT = "ubijavaclient:";
    private static final String II_PACKAGE = "ii(";
    private static String strip(String pragma)
    {
      String result = pragma.trim();
      char ch = result.charAt(0);
      if (ch == '\"')
        result = result.substring(1, result.length() - 1);
      result = result.trim();
      return result;
    }
    private static void addMap(String mapping)
    {
      int n = mapping.indexOf(':');
      if (n == -1)
        return;
      String key = mapping.substring(1, n);
      String value = mapping.substring(n + 1, mapping.length() - 1);
      packages.put(key, value);
    }
    public static void putImport(PrintWriter outData, String defaultUsing,
        String name, String lookup)
    {
      if (packages.containsKey(lookup) == true)
        outData.println("import " + (String) packages.get(lookup) + "." + name
            + ".*;");
      else
        outData.println(defaultUsing + "." + name + ".*;");
    }
    public static void load(Vector<String> pragmas)
    {
      Vector<String> stubs = new Vector<String>();
      selected = new Vector<String>();
      packages = new HashMap<String, String>();
      for (int i = 0; i < pragmas.size(); i++)
      {
        String pragma = strip((String) pragmas.elementAt(i));
        if (pragma.length() > UBI_JAVA_CLIENT.length())
        {
          String stripped = pragma.substring(0, UBI_JAVA_CLIENT.length());
          if (stripped.compareToIgnoreCase(UBI_JAVA_CLIENT) == 0)
          {
            stubs.addElement(pragma.substring(UBI_JAVA_CLIENT.length()));
            continue;
          }
        }
        if (pragma.length() > II_PACKAGE.length() - 1)
        {
          String stripped = pragma.substring(0, II_PACKAGE.length());
          if (stripped.compareToIgnoreCase(II_PACKAGE) == 0)
          {
            addMap(pragma.substring(II_PACKAGE.length() - 1));
            continue;
          }
        }
      }
      for (int i = 0; i < pragmas.size(); i++)
      {
        String pragma = strip((String) pragmas.elementAt(i));
        int plen = pragma.length();
        for (int j = 0; j < stubs.size(); j++)
        {
          String stub = ":" + (String) stubs.elementAt(j);
          int p = pragma.indexOf(stub);
          if (p == -1 && plen - p != stub.length())
            continue;
          String select = pragma.substring(0, p);
          if ((select + ":").compareTo(UBI_JAVA_CLIENT) != 0)
            selected.addElement(select);
          break;
        }
      }
    }
    public static boolean isSelected(Prototype prototype)
    {
      if (prototype.isRpcCall() == false)
        return false;
      if (selected.size() == 0)
        return true;
      for (int i = 0; i < selected.size(); i++)
      {
        String select = (String) selected.elementAt(i);
        if (select.compareTo(prototype.name) == 0)
          return true;
      }
      return false;
    }
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
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(
            args[i]));
        Module module = (Module) in.readObject();
        in.close();
        generate(module, "", outLog);
      }
      outLog.flush();
    } catch (Exception e)
    {
      e.printStackTrace(errLog);
      errLog.flush();
    }
  }
  /**
   * Generates
   */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name + " version " + module.version);
    Pragmas.load(module.pragmas);
    generateStructs(module, output, outLog);
    generateClasses(module, output, outLog);
  }
  private static String[] seperate(String value, String delim)
  {
    int n = 0, p = 0;
    for (;;)
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
    if (op.length > 0)
    {
      int n = op.length - 1;
      int b = 1;
      if (op[0].length() > 1 && op[0].charAt(1) == ':')
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
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateStructs(Module module, String stdOutput,
      PrintWriter outLog)
  {
    try
    {
      String output = outputDir(module, stdOutput);
      outLog.println("Code: " + output + module.name + "Structs.java");
      OutputStream outFile = new FileOutputStream(output + module.name
          + "Structs.java");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData
            .println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData
            .println("// 1: Mutilation, Spindlization and Bending will result in goto 1");
        outData.println(makeNameSpace(module));
        outData.println();
        outData.println("import bbd.crackle.rdc.*;");
        outData.println();
        outData.println("public class " + module.name + "Structs");
        outData.println("{");
        generateStructs(module, outData);
        generateEnums(module, outData);
        generateMessageEnums(module, outData);
        generateTableEnums(module, outData);
        outData.println("}");
      } finally
      {
        outData.flush();
        outFile.close();
      }
    } catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
      e1.printStackTrace(outLog);
      outLog.flush();
    } catch (Exception e)
    {
      outLog.println(e.toString());
      e.printStackTrace(outLog);
      outLog.flush();
    }
  }
  private static String getPackageName(Module module)
  {
    if (module.packageName.length() == 0)
      return "bbd.crackle." + module.name.toLowerCase();
    return module.packageName;
  }
  private static String makeNameSpace(Module module)
  {
    return "package " + getPackageName(module) + ";";
  }
  /**
   * Sets up the writer and generates the general stuff
   */
  private static void generateClasses(Module module, String stdOutput,
      PrintWriter outLog)
  {
    try
    {
      String output = outputDir(module, stdOutput);
      outLog.println("Code: " + output + module.name + ".java");
      OutputStream outFile = new FileOutputStream(output + module.name
          + ".java");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData
            .println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData
            .println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println(makeNameSpace(module));
        outData.println();
        outData.println("import bbd.crackle.rdc.*;");
        generateUsings(module, outData);
        outData.println();
        outData.println("public class " + module.name + " extends Client");
        outData.println("{");
        outData.println("  private String version = " + module.version + ";");
        outData.println("  private int signature = " + module.signature + ";");
        outData.println("  public String getVersion() {return version;}");
        outData.println("  public int getSignature() {return signature;}");
        outData.println("  public " + module.name
            + "(String host, String service, int timeOut) throws Exception");
        outData.println("  {");
        outData.println("    super(host, service, timeOut);");
        outData.println("  }");
        generateCalls(module, outData);
        outData.println("}");
      } finally
      {
        outData.flush();
        outFile.close();
      }
    } catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
      e1.printStackTrace(outLog);
      outLog.flush();
    } catch (Exception e)
    {
      outLog.println(e.toString());
      e.printStackTrace(outLog);
      outLog.flush();
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
      Message message = (Message) module.messages.elementAt(i);
      outData.println("  , " + message.name + "(" + (++mb) + ", "
          + message.value + ")");
    }
    outData.println("  , INV_SIGNATURE(" + (++mb) + ", \"Invalid Signature\")");
    outData.println("  , UNKNOWN_FUNCTION(" + (++mb)
        + ", \"Unknown Function\")");
    outData.println("  , THROWABLE_EXCEPTION(" + (++mb)
        + ", \"Exception Exception\")");
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
  public static void generateTableEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table) module.tables.elementAt(i);
      outData.println("  public static enum " + table.name);
      int mb = 0;
      String comma = "  {";
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message) table.messages.elementAt(j);
        outData.println(comma + " " + message.name + "(" + (mb++) + ", "
            + message.value + ")");
        comma = "  ,";
      }
      outData.println("    ;");
      outData.println("    public final int key;");
      outData.println("    public final String value;");
      outData.println("    " + table.name + "(int key, String value)");
      outData.println("    {");
      outData.println("      this.key = key;");
      outData.println("      this.value = value;");
      outData.println("    }");
      outData.println("    public static " + table.name + " get(int no)");
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
  }
  private static void generateEnums(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.enumerators.size(); i++)
    {
      Enumerator entry = (Enumerator) module.enumerators.elementAt(i);
      outData.println("  public static enum " + entry.name);
      int mb = 0;
      String comma = "  { ";
      for (int j = 0; j < entry.elements.size(); j++)
      {
        String element = (String) entry.elements.elementAt(j);
        outData.println(comma + element.toUpperCase() + "(" + (mb++) + "\""
            + element + "\"");
        comma = "  , ";
      }
      outData.println("    public final int key;");
      outData.println("    public final String value;");
      outData.println("    " + entry.name + "(int key, String value)");
      outData.println("    {");
      outData.println("      this.key = key;");
      outData.println("      this.value = value;");
      outData.println("    }");
      outData.println("    public static " + entry.name + " get(int no)");
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
  }
  private static void generateUsings(Module module, PrintWriter outData)
  {
    String usings = "";
    String mainUsing = "import " + getPackageName(module);
    outData.println(mainUsing + "." + module.name + "Structs.*;");
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      if (structure.header.indexOf(".sh\"") > 0)
      {
        String lookup = structure.header.substring(1,
            structure.header.length() - 4);
        if (usings.indexOf(structure.header) == -1)
        {
          String name = structure.name.substring(1, lookup.length() + 1);
          usings += structure.header;
          Pragmas.putImport(outData, mainUsing, name, lookup);
        }
      }
    }
  }
  private static void writeCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    switch (field.type.typeof)
    {
    case Type.BOOLEAN:
      outData.println("      writer.putShort(" + name + ");writer.filler(6);");
      break;
    case Type.BYTE:
      if (field.type.arraySizes.size() > 0)
      {
        Integer int0 = (Integer) field.type.arraySizes.elementAt(0);
        int no = int0.intValue();
        outData.print("      writer.putBytes(" + name + ", " + no + ");");
        int n = 8 - no % 8;
        if (n > 0 && n < 8)
          outData.println("writer.filler(" + n + ");");
        else
          outData.println();
      } else
        outData
            .println("      writer.putShort(" + name + ");writer.filler(6);");
      break;
    case Type.SHORT:
      outData.println("      writer.putShort(" + name + ");writer.filler(6);");
      break;
    case Type.INT:
      outData.println("      writer.putInt(" + name + ");writer.filler(4);");
      break;
    case Type.LONG:
      outData.println("      writer.putLong(" + name + ");");
      break;
    case Type.DOUBLE:
      outData.println("      writer.putDouble(" + name + ");");
      break;
    case Type.STRING:
    case Type.CHAR:
      if (field.type.arraySizes.size() == 0)
        outData.println("      writer.putShort((byte)" + name
            + ");writer.filler(6);");
      else
      {
        Integer int0 = (Integer) field.type.arraySizes.elementAt(0);
        int no = int0.intValue();
        String size = "" + no;
        outData.print("      writer.putString(" + name + ", " + size + ");");
        int n = 8 - no % 8;
        if (n > 0 && n < 8)
          outData.println("writer.filler(" + n + ");");
        else
          outData.println();
      }
      break;
    }
  }
  private static void readCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    switch (field.type.typeof)
    {
    case Type.BOOLEAN:
      outData.println("      " + name
          + " = (byte)reader.getShort();reader.skip(6);");
      break;
    case Type.BYTE:
      if (field.type.arraySizes.size() == 0)
        outData.println("      " + name
            + " = (byte)reader.getShort();reader.skip(6);");
      else
      {
        Integer int0 = (Integer) field.type.arraySizes.elementAt(0);
        int no = int0.intValue();
        outData.print("      " + name + " = reader.getBytes(" + no + ");");
        int n = 8 - no % 8;
        if (n > 0 && n < 8)
          outData.println("reader.skip(" + n + ");");
        else
          outData.println();
      }
      break;
    case Type.SHORT:
      outData.println("      " + name + " = reader.getShort();reader.skip(6);");
      break;
    case Type.INT:
      outData.println("      " + name + " = reader.getInt();reader.skip(4);");
      break;
    case Type.LONG:
      outData.println("      " + name + " = reader.getLong();");
      break;
    case Type.DOUBLE:
      outData.println("      " + name + " = reader.getDouble();");
      break;
    case Type.STRING:
    case Type.CHAR:
      if (field.type.arraySizes.size() == 0)
        outData.println("      " + name
            + " = (char)reader.getByte();reader.skip(7);");
      else
      {
        Integer int0 = (Integer) field.type.arraySizes.elementAt(0);
        int no = int0.intValue();
        String size = "" + no;
        outData.print("      " + name + " = reader.getString(" + size + ");");
        int n = 8 - no % 8;
        if (n > 0 && n < 8)
          outData.println("reader.skip(" + n + ");");
        else
          outData.println();
      }
      break;
    }
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
      outData.println("  public final static class " + structure.name);
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        outData.println("    public "
            + field.type.javaStructDef(field.nameLowerFirst()) + " = "
            + assignValue(field) + ";");
      }
      outData.println("    public void write(Writer writer) throws Exception");
      outData.println("    {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        writeCalls(field, outData);
      }
      outData.println("    }");
      outData.println("    public void read(Reader reader) throws Exception");
      outData.println("    {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field) structure.fields.elementAt(j);
        readCalls(field, outData);
      }
      outData.println("    }");
      outData.println("  }");
    }
  }
  private static void generateCalls(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      if (Pragmas.isSelected(prototype) == true)
        generateCall(module, prototype, i, outData);
    }
  }
  private static Vector<Parameter> parameterList;

  private static final class GenerateCommonTuple
  {
    public boolean hasReturn;
    public boolean hasTuple;
    public String name;
    public String body;
    GenerateCommonTuple()
    {
      hasReturn = false;
      hasTuple = false;
      name = "void";
      body = "";
    }
  }
  private static GenerateCommonTuple generateCommon(Module module,
      Prototype prototype, int no, PrintWriter outData, String extra1)
  {
    parameterList = new Vector<Parameter>();
    Parameter.language = Parameter.JAVA_BASED;
    Parameter.build(parameterList, prototype, false, true);
    GenerateCommonTuple result = new GenerateCommonTuple();
    outData.println(result.body);
    result.body = lowerFirst(prototype.name) + "(";
    if (prototype.outputs.size() > 0)
    {
      result.hasTuple = true;
      result.hasReturn = (prototype.type.reference == Type.BYVAL && prototype.type.typeof != Type.VOID);
      result.name = upperFirst(prototype.name) + "Tuple";
    } else if (prototype.type.reference == Type.BYVAL
        && prototype.type.typeof != Type.VOID)
    {
      result.hasReturn = true;
      result.name = prototype.type.javaName();
    }
    String comma = "";
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter) parameterList.elementAt(i);
      if (pd.isInput)
      {
        result.body += comma + pd.fullType() + " " + pd.name;
        comma = ", ";
      }
    }
    result.body += ")";
    return result;
  }
  private static void generateCall(Module module, Prototype prototype, int no,
      PrintWriter outData)
  {
    Parameter.language = Parameter.JAVA_BASED;
    Parameter pd = new Parameter();
    GenerateCommonTuple gcTuple = generateCommon(module, prototype, no,
        outData, "public ");
    String sNo = "" + no;
    if (gcTuple.hasTuple)
    {
      outData.println("  public static final class " + gcTuple.name);
      outData.println("  {");
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        outData.println("    public " + prototype.type.javaName() + " result;");
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter) parameterList.elementAt(i);
        if (pd.isOutput)
          outData.println("    public " + pd.fullType() + " " + pd.name + ";");
      }
      outData.println("  }");
    }
    outData.println("  public " + gcTuple.name + " " + gcTuple.body
        + " throws Exception");
    outData.println("  {");
    outData.println("    init("
        + (prototype.message.length() > 0 ? asHex(prototype.message)
            : asHex(sNo)) + ", \"" + module.name + "\", \"" + prototype.name
        + "\");");
    outData.println("    try");
    outData.println("    {");
    outData.println("      writer.putInt(" + asHex(prototype.signature(true))
        + ");");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      for (int j = 0; j < parameterList.size(); j++)
      {
        pd = (Parameter) parameterList.elementAt(j);
        if (pd.inputNo == i)
          break;
      }
      putCall(pd, outData);
    }
    outData.println("      call();");
    if (gcTuple.hasTuple)
    {
      outData.println("      " + gcTuple.name + " _result = new "
          + gcTuple.name + "();");
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        getCall("_result.result", prototype.type, outData);
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter) parameterList.elementAt(i);
        if (pd.isOutput)
          getCall("_result.", pd, outData);
      }
    } else if (gcTuple.hasReturn)
    {
      outData.println("      " + gcTuple.name + " _result;");
      getCall("_result", prototype.type, outData);
    }
    if (gcTuple.hasReturn || gcTuple.hasTuple)
      outData.println("      return _result;");
    outData.println("    }");
    outData.println("    finally");
    outData.println("    {");
    outData.println("      done();");
    outData.println("    }");
    outData.println("  }");
  }
  private static void putCall(Parameter pd, PrintWriter outData)
  {
    switch (pd.field.type.typeof)
    {
    case Type.BOOLEAN:
      outData.println("      writer.putBoolean(" + pd.name + ");");
      break;
    case Type.BYTE:
      if (pd.field.type.arraySizes.size() > 0
          || pd.field.type.reference == Type.BYPTR)
      {
        outData.println("      writer.putInt(" + pd.name + ".length+1);");
        outData.println("      writer.putBytes(" + pd.name + ", " + pd.name
            + ".length);writer.filler(1);");
      } else
        outData.println("      writer.putByte(" + pd.name + ");");
      break;
    case Type.CHAR:
      if (pd.field.type.arraySizes.size() > 0
          || pd.field.type.reference == Type.BYPTR)
      {
        outData.println("      writer.putInt(" + pd.name + ".length()+1);");
        outData.println("      writer.putString(" + pd.name + ", " + pd.name
            + ".length());writer.filler(1);");
      } else
        outData.println("      writer.putByte(" + pd.name + ");");
      break;
    case Type.STRING:
      outData.println("      writer.putString(" + pd.name + ", " + pd.name
          + ".length());");
      break;
    case Type.SHORT:
      outData.println("      writer.putShort(" + pd.name + ");");
      break;
    case Type.INT:
      outData.println("      writer.putInt(" + pd.name + ");");
      break;
    case Type.LONG:
      outData.println("      writer.putLong(" + pd.name + ");");
      break;
    case Type.FLOAT:
    case Type.DOUBLE:
      outData.println("      writer.putDouble(" + pd.name + ");");
      break;
    default:
      if (pd.isArray == true)
      {
        outData.println("      int " + pd.name
            + "StartPos = writer.size();writer.filler(4);");
        outData.println("      for (" + pd.type + " rec : " + pd.name + ")");
        outData.println("        rec.write(writer);");
        outData.println("      writer.storeLength(" + pd.name
            + "StartPos, writer.size() - " + pd.name + "StartPos);");
      } else
        outData.println("      " + pd.name + ".write(writer);");
      break;
    }
  }
  private static void getCall(String string, Type type, PrintWriter outData)
  {
    getCall(string, type, null, outData);
  }
  private static void getCall(String string, Parameter pd, PrintWriter outData)
  {
    getCall(string, null, pd, outData);
  }
  private static void getCall(String string, Type type, Parameter pd,
      PrintWriter outData)
  {
    boolean isArray = false;
    String sizeName = "";
    String var = string;
    String recType = "";
    Type useType = type;
    if (pd != null)
    {
      isArray = pd.isArray;
      useType = pd.field.type;
      sizeName = string + pd.outputSizeValue;
      var = string + pd.name;
      recType = pd.type;
    }
    switch (useType.typeof)
    {
    case Type.BOOLEAN:
      outData.println("      " + var + " = reader.getShort();");
      break;
    case Type.BYTE:
      if (pd != null && pd.fullType().compareTo("byte[]") == 0)
        outData.println("      reader.skip(4);" + var + " = reader.getBytes("
            + sizeName + ");");
      else if (pd != null && pd.hasOutputSize == true)
        outData.println("      reader.skip(4);" + var + " = reader.getBytes("
            + sizeName + ");");
      else
        outData.println("      " + var + " = reader.getShort();");
      break;
    case Type.CHAR:
      if (pd != null
          && (pd.field.type.arraySizes.size() > 0 || pd.field.type.reference == Type.BYPTR))
        outData.println("      " + var + " = reader.getString(" + sizeName
            + ");");
      else if (pd != null && pd.hasOutputSize == true)
        outData.println("      " + var + " = reader.getBytes(" + sizeName
            + ");");
      else
        outData.println("      " + var + " = reader.getShort();");
      break;
    case Type.STRING:
      outData.println("      " + var + " = reader.getString(" + sizeName + ")");
      break;
    case Type.SHORT:
      outData.println("      " + var + " = reader.getShort();");
      break;
    case Type.INT:
      outData.println("      " + var + " = reader.getInt();");
      break;
    case Type.LONG:
      outData.println("      " + var + " = reader.getLong();");
      break;
    case Type.FLOAT:
    case Type.DOUBLE:
      outData.println("      " + var + " = reader.getDouble();");
      break;
    default:
      if (isArray == true)
      {
        outData.println("      reader.skip(4); // skip data block length");
        outData.println("      " + var + " = new " + recType + "[" + sizeName
            + "];");
        outData.println("      for (int i=0; i < " + sizeName + "; i++)");
        outData.println("      {");
        outData.println("        " + var + "[i] = new " + recType + "();");
        outData.println("        " + var + "[i].read(reader);");
        outData.println("      }");
      } else
      {
        outData.println("      " + var + " = new " + recType + "();");
        outData.println("      " + var + ".read(reader);");
      }
      break;
    }
  }
  private static String lowerFirst(String in)
  {
    String result = in.substring(0, 1).toLowerCase() + in.substring(1);
    return result;
  }
  private static String upperFirst(String in)
  {
    String result = in.substring(0, 1).toUpperCase() + in.substring(1);
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
        if (type.arraySizes.size() == 0)
          return "0";
        else
        {
          Integer int0 = (Integer) type.arraySizes.elementAt(0);
          int no = int0.intValue();
          return "new byte[" + no + "]";
        }
      case Type.INT:
        return "0";
      case Type.STRING:
        return "\"\"";
      }
    }
    return "new " + type.javaName();
  }
}
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

public class PopUbiObjC extends Generator
{
  public static String description()
  {
    return "Generates ObjectiveC Client Code for C++ Server Code";
  }
  public static String documentation()
  {
    return "Generates ObjectiveC Client Code for C++ Server Code";
  }
  public static PrintWriter errLog;
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
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name + " version " + module.version);
    generateStructs(module, output, outLog);
    generateClasses(module, output, outLog);
  }
  private static void generateStructs(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name + "Structs.h");
      OutputStream outFile = new FileOutputStream(output + module.name + "Structs.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// 1: Mutilation, Spindlization and Bending will result in having to program this language in hell - goto 1");
        outData.println();
        outData.println("#import \"cracklerdc.h\"");
        outData.println();
        generateEnumsInterface(module, outData);
        generateStructsInterface(module, outData);
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
      outLog.println("Code: " + output + module.name + "Structs.m");
      outFile = new FileOutputStream(output + module.name + "Structs.m");
      outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// 1: Mutilation, Spindlization and Bending will result in joining SJ goto 1");
        outData.println("#import \"" + module.name + "Structs.h\"");
        outData.println();
        generateEnumsImplementation(module, outData);
        generateStructsImplementation(module, outData);
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
    } catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateStructsInterface(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      outData.println("@interface " + structure.name + "Rec: NSObject");
      outData.println("{");
      outData.println(" @protected");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        outData.println("  " + objcDef(field.type, field.nameLowerFirst())); 
      }
      outData.println("}");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        outData.println(objcProperty(field.type, field.nameLowerFirst()));
      }
      outData.println("- (id)init;");
      outData.println("- (void)clear;");
      outData.println("- (void)write:(Writer*)writer;");
      outData.println("- (void)read:(Reader*)reader;");
      outData.println("- (void)dealloc;");
      outData.println("@end");
      outData.println();
    }
  }
  private static void generateStructsImplementation(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() <= 0)
        continue;
      if (structure.name.compareTo(module.name) == 0)
        continue;
      outData.println("@implementation " + structure.name + "Rec");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        outData.println(objcSynthesis(field.nameLowerFirst()));
      }
      outData.println("- (id)init");
      outData.println("  {");
      outData.println("    self = [super init];");
      outData.println("    return self;");
      outData.println("  }");
      outData.println("- (void)clear");
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        clearCalls(field, outData);
      }
      outData.println("  }");
      outData.println("- (void)write:(Writer*)writer");
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        writeCalls(field, outData);
      }
      outData.println("  }");
      outData.println("- (void)read:(Reader*)reader");
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        readCalls(field, outData);
      }
      outData.println("  }");
      outData.println("- (void)dealloc");
      outData.println("  {");
      for (int j = 0; j < structure.fields.size(); j++)
      {
        Field field = (Field)structure.fields.elementAt(j);
        freeCalls(field, outData);
      }
      outData.println("    [super dealloc];");
      outData.println("  }");
      outData.println("@end");
    }
  }
  private static void clearCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
        outData.println("    self." + name + " = NO;");
        break;
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
        outData.println("    self." + name + " = 0;");
        break;
      case Type.BYTE:
        if (field.type.arraySizes.size() == 0)
          outData.println("    self." + name + " = 0;");
        else
        {
          outData.println("    self." + name + " = nil; // memset of pointer is stupid");
        }
        break;
      case Type.DOUBLE:
        outData.println("    self." + name + " = 0.0;");
        break;
      case Type.STRING:
      case Type.CHAR:
        if (field.type.arraySizes.size() == 0)
          outData.println("    self." + name + " = 0;");
        else
        {
          outData.println("    self." + name + " = nil; // memset of pointer is stupid");
        }
        break;
    }
  }
  private static void writeCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    if (field.type.arraySizes.size() > 0 && field.type.typeof != Type.STRING && field.type.typeof != Type.CHAR)
    {
      int no = objcSize(field.type);
      for (int i = 0; i < field.type.arraySizes.size(); i++)
      {
        Integer integer = (Integer)field.type.arraySizes.elementAt(i);
        no *= integer.intValue();
      }
      outData.print("    [writer putData:self." + name + " length:" + no + "];");
      int n = 8 - no % 8;
      if (n > 0 && n < 8)
        outData.println("[writer filler:" + n + "];");
      else
        outData.println();
      return;
    }
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
        outData.println("    [writer putBoolean:self." + name + "];[writer filler:6];");
        break;
      case Type.BYTE:
        outData.println("    [writer putShort:self." + name + "];[writer filler:6];");
        break;
      case Type.SHORT:
        outData.println("    [writer putShort:self." + name + "];[writer filler:6];");
        break;
      case Type.INT:
        outData.println("    [writer putInt:self." + name + "];[writer filler:4];");
        break;
      case Type.LONG:
        outData.println("    [writer putLong:self." + name + "];");
        break;
      case Type.DOUBLE:
        outData.println("    [writer putDouble:self." + name + "];");
        break;
      case Type.STRING:
      case Type.CHAR:
        if (field.type.arraySizes.size() == 0)
          outData.println("    [writer putShort:(byte)self." + name + "];[writer filler:6];");
        else
        {
          Integer int0 = (Integer)field.type.arraySizes.elementAt(0);
          int no = int0.intValue();
          String size = "" + no;
          outData.print("    [writer putString:self." + name + " length:" + size + "];");
          int n = 8 - no % 8;
          if (n > 0 && n < 8)
            outData.println("[writer filler:" + n + "];");
          else
            outData.println();
        }
        break;
    }
  }
  private static void readCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    if (field.type.arraySizes.size() > 0 && field.type.typeof != Type.STRING && field.type.typeof != Type.CHAR)
    {
      int no = objcSize(field.type);
      for (int i = 0; i < field.type.arraySizes.size(); i++)
      {
        Integer integer = (Integer)field.type.arraySizes.elementAt(i);
        no *= integer.intValue();
      }
      outData.print("    self." + name + " = [reader getData:" + no + "];");
      int n = 8 - no % 8;
      if (n > 0 && n < 8)
        outData.println("[reader skip:" + n + "];");
      else
        outData.println();
      return;
    }
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
        outData.println("    self." + name + " = [reader getBoolean];[reader skip:6];");
        break;
      case Type.BYTE:
        outData.println("    self." + name + " = (byte)[reader getShort];[reader skip:6];");
        break;
      case Type.SHORT:
        outData.println("    self." + name + " = [reader getShort];[reader skip:6];");
        break;
      case Type.INT:
        outData.println("    self." + name + " = [reader getInt];[reader skip:4];");
        break;
      case Type.LONG:
        outData.println("    self." + name + " = [reader getLong];");
        break;
      case Type.DOUBLE:
        outData.println("    self." + name + " = [reader getDouble];");
        break;
      case Type.STRING:
      case Type.CHAR:
        if (field.type.arraySizes.size() == 0)
          outData.println("    self." + name + " = (char)[reader getByte];[reader skip:7];");
        else
        {
          Integer int0 = (Integer)field.type.arraySizes.elementAt(0);
          int no = int0.intValue();
          outData.print("    self." + name + " = [reader getString:" + no + "];");
          int n = 8 - no % 8;
          if (n > 0 && n < 8)
            outData.println("[reader skip:" + n + "];");
          else
            outData.println();
        }
        break;
    }
  }
  private static void freeCalls(Field field, PrintWriter outData)
  {
    String name = field.nameLowerFirst();
    if (field.type.arraySizes.size() > 0 && field.type.typeof != Type.STRING && field.type.typeof != Type.CHAR)
    {
      outData.println("    [" + name + " release];");
      return;
    }
    switch (field.type.typeof)
    {
      //case Type.BYTE:
      case Type.STRING:
      case Type.CHAR:
        if (field.type.arraySizes.size() == 1)
          outData.println("    [" + name + " release];");
        break;
      case Type.USERTYPE:
        outData.println("    [" + name + " release];");
        break;
    }
  }
  private static void generateEnumsInterface(Module module, PrintWriter outData)
  {
    outData.println("enum e" + module.name);
    outData.println("{ " + module.name.toUpperCase() + "_OK");
    String w0 = "";
    if (module.messageBase > 0)
      w0 = " = " + module.messageBase;
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message)module.messages.elementAt(i);
      outData.println(", " + module.name.toUpperCase() + "_" + message.name + w0 + " // " + message.value);
      w0 = "";
    }
    outData.println(", " + module.name.toUpperCase() + "_INV_SIGNATURE");
    outData.println(", " + module.name.toUpperCase() + "_INV_COOKIE");
    outData.println(", " + module.name.toUpperCase() + "_LAST_LAST");
    outData.println("};");
    outData.println();
    outData.println("extern char *" + module.name + "Errors[];");
    outData.println();
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table)module.tables.elementAt(i);
      outData.println("enum e" + module.name + table.name);
      outData.println("{");
      String comma = "  ";
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message)table.messages.elementAt(j);
        outData.println(comma + table.name.toLowerCase() + message.name + "   // " + message.value);
        comma = ", ";
      }
      outData.println(comma + table.name.toLowerCase() + "NoOf");
      outData.println("};");
      outData.println();
      outData.println("extern char *" + module.name + table.name + "[];");
      outData.println();
    }
    if (module.enumerators.size() > 0)
    {
      for (int i = 0; i < module.enumerators.size(); i++)
      {
        Enumerator entry = (Enumerator)module.enumerators.elementAt(i);
        outData.println("enum " + entry.name);
        String w1 = "{ ";
        for (int j = 0; j < entry.elements.size(); j++)
        {
          String element = (String)entry.elements.elementAt(j);
          outData.println(w1 + element);
          w1 = ", ";
        }
        outData.println("};");
        outData.println();
      }
    }
  }
  private static void generateEnumsImplementation(Module module, PrintWriter outData)
  {
    outData.println("char *" + module.name + "Errors[] = ");
    outData.println("{ \"No Error\"");
    for (int i = 0; i < module.messages.size(); i++)
    {
      Message message = (Message)module.messages.elementAt(i);
      outData.println(", " + message.value + "   // " + message.name);
    }
    outData.println(", \"Invalid Signature\"");
    outData.println(", \"Invalid Logon Cookie\"");
    outData.println(", \"Last error not in message table\"");
    outData.println("};");
    outData.println();
    for (int i = 0; i < module.tables.size(); i++)
    {
      Table table = (Table)module.tables.elementAt(i);
      String comma = "  ";
      outData.println("char *" + module.name + table.name + "[] = ");
      outData.println("{");
      for (int j = 0; j < table.messages.size(); j++)
      {
        Message message = (Message)table.messages.elementAt(j);
        outData.println(comma + message.value + "   // " + message.name);
        comma = ", ";
      }
      outData.println("};");
      outData.println();
    }
  }
  private static void generateClasses(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name + ".h");
      OutputStream outFile = new FileOutputStream(output + module.name + ".h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println("#import \"cracklerdc.h\"");
        generateUsings(module, outData);
        outData.println();
        generateCallsTupleInterface(module, outData);
        outData.println("@interface " + module.name + ":Client");
        outData.println("{");
        outData.println("  NSString* version;");
        outData.println("  int signature;");
        outData.println("}");
        outData.println("@property(retain) NSString* version;");
        outData.println("@property(assign) int signature;");
        outData.println("- (id)initWithHost:(NSString*)aHost");
        outData.println("           service:(NSString*)aService");
        outData.println("           timeOut:(int)aTimeOut;");
        outData.println("- (void)dealloc;");
        generateCallsInterface(module, outData);
        outData.println("@end");
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
      outLog.println("Code: " + output + module.name + ".m");
      outFile = new FileOutputStream(output + module.name + ".m");
      outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println("#import \"" + module.name + ".h\"");
        outData.println();
        generateCallsTupleImplementation(module, outData);
        outData.println("@implementation " + module.name);
        outData.println("@synthesize version;");
        outData.println("@synthesize signature;");
        outData.println("- (id)initWithHost:(NSString*)aHost");
        outData.println("           service:(NSString*)aService");
        outData.println("           timeOut:(int)aTimeOut");
        outData.println("  {");
        outData.println("    self = [super initWithHost:aHost service:aService timeout:aTimeOut];");
        outData.println("    self.version = @" + module.version + ";");
        outData.println("    self.signature = " + asHex(module.signature) + ";");
        outData.println("    return self;");
        outData.println("  }");
        outData.println("- (void)dealloc");
        outData.println("  {");
        outData.println("    self.version = nil;");
        outData.println("    [super dealloc];");
        outData.println("  }");
        generateCallsImplementation(module, outData);
        outData.println("@end");
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
  private static void generateUsings(Module module, PrintWriter outData)
  {
    String usings = "";
    String defaultUsing = "#import \"";
    //if (module.packageName.length() != 0)
    //  defaultUsing = "#import \"" + replaceDots(module.packageName) + "/";
    outData.println(defaultUsing + module.name + "Structs.h\"");
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure)module.structures.elementAt(i);
      if (structure.fields.size() > 0)
        continue;
      if (structure.header.indexOf(".sh\"") > 0)
      {
        String name = structure.name.substring(1);
        String key = structure.header;
        if (usings.indexOf(key) == -1)
        {
          usings += key;
          outData.println(defaultUsing + name + ".h\"");
        }
      }
    }
  }
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
  private static Vector<Parameter> parameterList;
  private static String LF = (String) System.getProperty("line.separator");
  private static GenerateCommonTuple generateCommon(Module module, Prototype prototype, int no, PrintWriter outData, String extra1)
  {
    parameterList = new Vector<Parameter>();
    Parameter.language = Parameter.OBJC_BASED;
    Parameter.build(parameterList, prototype, false);
    GenerateCommonTuple result = new GenerateCommonTuple();
    result.body = lowerFirst(prototype.name);
    if (prototype.outputs.size() > 0)
    {
      result.hasTuple = true;
      result.hasReturn = (prototype.type.reference == Type.BYVAL && prototype.type.typeof != Type.VOID);
      result.name = upperFirst(prototype.name) + "Tuple";
    }
    else if (prototype.type.reference == Type.BYVAL
        && prototype.type.typeof != Type.VOID)
    {
      result.hasReturn = true;
      result.name = prototype.type.objcName();
    }
    boolean first = true;
    int colonPos = 0;
    for (int i = 0; i < parameterList.size(); i++)
    {
      Parameter pd = (Parameter)parameterList.elementAt(i);
      if (pd.isInput)
      {
        if (first == false)
        {
          int n = colonPos - pd.name.length();
          if (n < 0)
            n = 2;
          result.body += LF + padded(n) + lowerFirst(pd.name);
        }
        result.body += ":(" +objcType(pd, true)+ ")" + lowerFirst(pd.name);
        if (first == true)
        {
          colonPos = result.body.indexOf(':') + result.name.length() + 4 + (result.hasTuple ? 1 : 0);
          first = false;
        }
      }
    }
    return result;
  }
  private static void generateCallsTupleInterface(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall())
        generateCallTupleInterface(module, prototype, i, outData);
    }
  }
  //objcProperty(Type type, String aName)
  private static void generateCallTupleInterface(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    Parameter.language = Parameter.OBJC_BASED;
    Parameter pd = new Parameter(); 
    GenerateCommonTuple gcTuple = generateCommon(module, prototype, no, outData, "public ");
    if (gcTuple.hasTuple)
    {
      outData.println("@interface " + gcTuple.name + ":NSObject");
      outData.println("{");
      outData.println(" @protected");
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        outData.println("  " + objcDef(prototype.type, "result"));
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          outData.println("  " + objcDef(pd.field.type, pd.name));
      }
      outData.println("}");
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        outData.println("  " + objcProperty(prototype.type, "result"));
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          outData.println("  " + objcProperty(pd.field.type, pd.name));
      }
      outData.println("- (id)init;");
      outData.println("- (void)dealloc;");
      outData.println("@end");
      outData.println();
    }
  }
  private static void generateCallsTupleImplementation(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall())
        generateCallTupleImplementation(module, prototype, i, outData);
    }
  }
  private static void generateCallTupleImplementation(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    Parameter.language = Parameter.OBJC_BASED;
    Parameter pd = new Parameter();
    GenerateCommonTuple gcTuple = generateCommon(module, prototype, no, outData, "public ");
    if (gcTuple.hasTuple)
    {
      outData.println("@implementation " + gcTuple.name);
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        outData.println(objcSynthesis("result"));
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          outData.println(objcSynthesis(pd.name));
      }
      outData.println("- (id)init");
      outData.println("  {");
      outData.println("    self = [super init];");
      outData.println("    if (self)");
      outData.println("    {");
      if (prototype.type.reference == Type.BYVAL
          && prototype.type.typeof != Type.VOID)
        outData.println("      " + objcInit(prototype.type, "self.result") + ";");
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          outData.println("      " + objcInit(pd.field.type, "self." + pd.name) + ";");
      }
      outData.println("    }");
      outData.println("    return self;");
      outData.println("  }");
      outData.println("- (void)dealloc");
      outData.println("  {");
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          freeCalls(pd.field, outData);
      }
      outData.println("    [super dealloc];");
      outData.println("  }");
      outData.println("@end");
      outData.println();
    }
  }
  private static void generateCallsInterface(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall())
        generateCallInterface(module, prototype, i, outData);
    }
  }
  private static void generateCallInterface(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    Parameter.language = Parameter.OBJC_BASED;
    //Parameter pd = new Parameter();
    //pd.language = Parameter.OBJC_BASED;
    GenerateCommonTuple gcTuple = generateCommon(module, prototype, no, outData, "public ");
    String star = "";
    if (gcTuple.hasTuple) star = "*";
    outData.println("- (" + gcTuple.name + star + ")" + gcTuple.body + ";");
  }
  private static void generateCallsImplementation(Module module, PrintWriter outData)
  {
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall())
        generateCallImplementation(module, prototype, i, outData);
    }
  }
  private static void generateCallImplementation(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    Parameter.language = Parameter.OBJC_BASED;
    Parameter pd = new Parameter();
    GenerateCommonTuple gcTuple = generateCommon(module, prototype, no, outData, "public ");
    String star = "";
    String sNo = "" + no;
    if (gcTuple.hasTuple) star = "*";
    outData.println("- (" + gcTuple.name + star + ")" + gcTuple.body + "");
    outData.println("  {");
    outData.println("    [super startWithRequestId:" 
        + (prototype.message.length() > 0 ? asHex(prototype.message) : asHex(sNo)) 
        + " serverName:@\"" + module.name + "\""
        + " methodName:@\"" + prototype.name + "\"];");
    outData.println("    @try");
    outData.println("    {");
    outData.println("      [writer putInt:" + asHex(prototype.signature(true)) + "];");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      for (int j = 0; j < parameterList.size(); j++)
      {
        pd = (Parameter)parameterList.elementAt(j);
        if (pd.inputNo == i)
          break;
      }
      putCall(pd, outData);
    }
    outData.println("      [super call];");
    if (gcTuple.hasTuple)
    {
      outData.println("      " + gcTuple.name + "* _result = [[[" + gcTuple.name + " alloc] init] autorelease];");
      if (prototype.type.reference == Type.BYVAL
      && prototype.type.typeof != Type.VOID)
        getCall("_result.result", prototype.type, outData);
      for (int i = 0; i < parameterList.size(); i++)
      {
        pd = (Parameter)parameterList.elementAt(i);
        if (pd.isOutput)
          getCall("_result.", pd, outData);
      }
    }
    else if (gcTuple.hasReturn)
    {
      outData.println("      " + gcTuple.name + " _result;");
      getCall("_result", prototype.type, outData);
    }
    if (gcTuple.hasReturn || gcTuple.hasTuple)
      outData.println("      return _result;");
    outData.println("    }");
    outData.println("    @finally");
    outData.println("    {");
    outData.println("      [super done];");
    outData.println("    }");
    outData.println("  }");
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
    return "0x" + Integer.toHexString((int)value);
  }
  public static String asHex(String value)
  {
    return "0x" + Integer.toHexString(Integer.parseInt(value));
  }
  private static void putCall(Parameter pd, PrintWriter outData)
  {
    switch (pd.field.type.typeof)
    {
      case Type.BOOLEAN:
        outData.println("      [writer putBoolean:" + pd.name + "];");
        break;
      case Type.CHAR:
        if (pd.field.type.arraySizes.size() > 0 || pd.field.type.reference == Type.BYPTR)
        {
          outData.println("      int " + pd.name + "_length = [" + pd.name + " length];");
          outData.println("      [writer putInt:" + pd.name + "_length+1];");
          outData.println("      [writer putString:" + pd.name + " length:" + pd.name + "_length];");
          outData.println("      [writer filler:1];");
        }
        else
          outData.println("      [writer putByte:" + pd.name + "];");
        break;
      case Type.BYTE:
        outData.println("      [writer putByte:" + pd.name + "];");
        break;
      case Type.STRING:
        outData.println("      int " + pd.name + "_length = [" + pd.name + " length];");
        outData.println("      [writer putInt:" + pd.name + "_length+1];");
        outData.println("      [writer putString:" + pd.name + " length:" + pd.name + "_length];");
        outData.println("      [writer filler:1];");
        break;
      case Type.SHORT:
        outData.println("      [writer putShort:" + pd.name + "];");
        break;
      case Type.INT:
        outData.println("      [writer putInt:" + pd.name + "];");
        break;
      case Type.LONG:
        outData.println("      [writer putLong:" + pd.name + "];");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println("      [writer putDouble:" + pd.name + "];");
        break;
      default:
        if (pd.isArray == true)
        {
          outData.println("      int " + pd.name + "StartPos = [writer size];");
          outData.println("      [writer filler:4];");
          outData.println("      for (int i = 0; i < [" + pd.name +" count]; i++)");
          outData.println("        [[" + pd.name + " objectAtIndex:i] write:writer];");
          outData.println("      [writer storeLength:[writer size] - " + pd.name + "StartPos atPosition:" + pd.name + "StartPos];");
        }
        else
          outData.println("      [" + pd.name + " write:writer];");
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
  private static void getCall(String string, Type type, Parameter pd, PrintWriter outData)
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
      recType = pd.type + "Rec";
    }
    switch (useType.typeof)
    {
      case Type.BOOLEAN:
        outData.println("      " + var + " = [reader getBoolean];");
        break;
      case Type.CHAR:
        if (pd != null && (pd.field.type.arraySizes.size() > 0 || pd.field.type.reference == Type.BYPTR))
          outData.println("      " + var + " = [reader getString:" + sizeName + "];");
        else if (pd != null && pd.fullType().compareTo("byte[]") == 0)
        {
          outData.println("      [reader skip:4];");
          outData.println("      " + var + " = [reader getData:" + sizeName + "];");
        }
        else
          outData.println("      " + var + " = [reader getShort];");
        break;
      case Type.BYTE:
        outData.println("      " + var + " = [reader getShort];");
        break;
      case Type.STRING:
        outData.println("      " + var + " = [reader getString:" + sizeName + "];");
        break;
      case Type.SHORT:
        outData.println("      " + var + " = [reader getShort];");
        break;
      case Type.INT:
        outData.println("      " + var + " = [reader getInt];");
        break;
      case Type.LONG:
        outData.println("      " + var + " = [reader getLong];");
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        outData.println("      " + var + " = [reader getDouble];");
        break;
      default:
        if (isArray == true)
        {
          outData.println("      [reader skip:4];");
          outData.println("      for (int i=0; i < " + sizeName + "; i++)");
          outData.println("      {");
          outData.println("        " + recType + "* _rec = [[[" + recType + " alloc] init] autorelease];");
          outData.println("        [_rec read:reader];");
          outData.println("        [" + var + " addObject:_rec];");
          outData.println("      }");
        }
        else
        {
          outData.println("      [" + var + " read:reader];");
        }
        break;
    }
  }
  private static String padString = "                                                           ";
  private static String padded(int size)
  {
    if (size == 0)
      return "";
    if (size > padString.length())
      size = padString.length();
    return padString.substring(0, size);
  }
  private static String objcDefType(Type type)
  {
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        return "BOOL";
      case Type.BYTE:
        return "int8";
      case Type.SHORT:
        return "int16";
      case Type.INT:
        return "int32";
      case Type.LONG:
        return "int64";
      case Type.CHAR:
      case Type.STRING:
        return "char";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "double";
    }
    return type.name + "Rec";
  }
  private static String objcProperty(Type type, String aName)
  {
    String access = "readwrite";
    if (type.reference == Type.BYPTR && type.typeof == Type.USERTYPE)
      access = "retain";
    else if (type.reference == Type.BYREFPTR && type.typeof == Type.CHAR)
      access = "retain";
    else if (type.reference == Type.BYREFPTR)
      access = "retain";
    else if (type.typeof == Type.CHAR && type.arraySizes.size() == 1)
      access = "retain";
    else if (type.arraySizes.size() > 0)
      access = "retain";
    return "@property(" + access + ") " + objcDef(type, aName);
  }
  private static String objcSynthesis(String aName)
  {
    return "@synthesize " + aName + ";";
  }
  private static int objcSize(Type type)
  {
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        return 2;
      case Type.BYTE:
        return 1;
      case Type.SHORT:
        return 2;
      case Type.INT:
        return 4;
      case Type.LONG:
        return 8;
      case Type.FLOAT:
      case Type.DOUBLE:
        return 8;
    }
    return 1;
  }
  private static String objcDef(Type type, String aName)
  {
    if (type.reference == Type.BYPTR && type.typeof == Type.USERTYPE)
      return objcDefType(type) + "* " + aName + ";";
    if (type.reference == Type.BYREFPTR && type.typeof == Type.CHAR)
      return "NSMutableData* " + aName + ";";
    if (type.reference == Type.BYREFPTR)
      return "NSMutableArray* " + aName + "; // " + objcDefType(type);
    if (type.typeof == Type.CHAR && type.arraySizes.size() == 1)
      return "NSString* " + aName +";";
    if (type.arraySizes.size() > 0)
    {
      int size = objcSize(type);
      for (int i = 0; i < type.arraySizes.size(); i++)
      {
        Integer integer = (Integer)type.arraySizes.elementAt(i);
        size *= integer.intValue();
      }
      return "NSMutableData* " + aName + "; // " + size;
    }
    return objcDefType(type) + " " + aName + ";";
  }
  public static String objcInit(Type type, String name)
  {
    if (type.reference == Type.BYREFPTR && type.typeof == Type.CHAR)
      return name + " = 0";
    if (type.reference == Type.BYREFPTR)
      return name + " = [[NSMutableArray alloc] init]";
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        return name + " = NO";
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
        return name + " = 0";
      case Type.CHAR:
        if (type.arraySizes.size() > 0 || type.reference == Type.BYPTR)
          return name + " = [[NSMutableArray alloc] init]";
        else
          return name + " = 0";
      case Type.STRING:
        return name + " = 0";
      case Type.FLOAT:
      case Type.DOUBLE:
        return name + " = 0.0";
    }
    return name + " = [[" + objcDefType(type) + " alloc] init]";
  }
  private static String objcType(Parameter parm, boolean addRec)
  {
    String adder = "";
    String asType = parm.type;
    if (asType.compareTo("String") == 0) asType = "NSString*";
    if (parm.field.type.typeof == Type.USERTYPE)
    {
      if (addRec == true)
        adder = "Rec*";
      else
        adder = "*";
      if (parm.isArray) return "NSMutableArray*";
    }
    if (parm.field.type.reference == Type.ARRAYED) return asType + adder + "*";
    return asType + adder + (parm.isArray ? "*" : "");
  }
}
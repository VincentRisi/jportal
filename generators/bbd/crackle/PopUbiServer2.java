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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbiServer2 extends Generator
{
  public static String description()
  {
    return "Generates Ubiquitous Server2 Code (AIX|SUN|NT)";
  }
  public static String documentation()
  {
    return "Generates Ubiquitous Server2 Code (AIX|SUN|NT)"
     + "\r\nHandles following pragmas"
     + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
      ;
  }
  /*
   *  Static constructor
   */
  {
    setupPragmaVector();
  }
  public static void resetPragmaVector()
  {
    pragmaVector = null;
    setupPragmaVector();
  }
  private static void setupPragmaVector()
  {
    if (pragmaVector == null || pragmaVector.size() < 1) 
    {
      pragmaVector = new Vector<Pragma>();
      pragmaVector.addElement(new Pragma("AlignForSun", false, "Ensure that all fields are on 8 byte boundaries."));
    }
  }
  private static boolean alignForSun;
  private static void setPragmas(Module module)
  {
    // Ensure these are in the same order as aobove
    setupPragmaVector();
    int no=0;
    alignForSun = ((Pragma)pragmaVector.elementAt(no++)).value;
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
        alignForSun = true;
    }
  }
  private static boolean hasShutDownCode = false;
  private static PrintWriter errLog;
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
  * - C Header for Server
  * - C Server Marshalling code
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    setPragmas(module);
    generateCHeader(module, output, outLog);
    if (module.tables.size() > 0)
    {
      generateCTablesHeader(module, output, outLog);
      generateCTablesCode(module, output, outLog);
    }
    generateReqIDTable(module, output, outLog);
    generateCMain(module, output, outLog);
    if (generateCServer(module, output, outLog))
    {
      generateCCode(module, output, outLog);
      if (generatePyCCode(module, output, outLog))
        generatePyHCode(module, output, outLog);
      if (generateEmbedCCode(module, output, outLog))
        generateEmbedHCode(module, output, outLog);
    }
  }
  private static void generateCTablesHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"tables.h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"tables.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#ifndef _"+module.name+"Tables_H_");
        outData.println("#define _"+module.name+"Tables_H_");
        outData.println();
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          outData.println("enum e"+module.name+table.name);
          outData.println("{");
          String comma = "  ";
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            outData.println(comma+table.name.toLowerCase()+message.name+"   // "+message.value);
            comma = ", ";
          }
          outData.println(comma+table.name.toLowerCase()+"NoOf");
          outData.println("};");
          outData.println();
          outData.println("extern char *"+module.name+table.name+"[];");
          outData.println();
        }
        outData.println("#endif");
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateCTablesCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"tables.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"tables.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"machine.h\"");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+"tables.h\"");
        outData.println();
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          String comma = "  ";
          outData.println("char *"+module.name+table.name+"[] = ");
          outData.println("{");
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            outData.println(comma+message.value+"   // "+message.name);
            comma = ", ";
          }
          outData.println("};");
          outData.println();
        }
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
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
  private static String nameOf(String fullName)
  {
    int n = fullName.lastIndexOf('/');
    if (n > 0)
      return fullName.substring(n+1);
    n = fullName.lastIndexOf('\\');
    if (n > 0)
      return fullName.substring(n+1);
    return fullName;
  }
  private static void generateCHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+".h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+".h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        boolean hasProtected = false;
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#ifndef _" + module.name + "H_");
        outData.println("#define _" + module.name + "H_");
        outData.println();
        outData.println("#include <assert.h>");
        outData.println("#include \"swapbytes.h\"");
        outData.println("#include \"ubidata.h\"");
        outData.println("#include \"ubiserver.h\"");
        outData.println("#include \"ubirpc.h\"");
        outData.println("#include \"ubibasevars.h\"");
        outData.println("#include \"handles.h\"");
        outData.println();
        outData.println("class T" + module.name + ";");
        outData.println("typedef tHandle<T" + module.name + "*, 100, 100> T" + module.name + "Handle;");
        outData.println();
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String)module.code.elementAt(j);
            if (codeLine.indexOf("#include") == -1 && codeLine.indexOf("HEADER:") == -1 && codeLine.indexOf("BOTH:") == -1)
              continue;
            if (codeLine.indexOf("CODE:") != -1)
              continue;
            if (firstTime)
              firstTime = false;
            outData.print(cleanUp(codeLine));
          }
          if (firstTime == false)
            outData.println();
        }
        PopUbiGen.generateCExterns(module, outData);
        outData.println("extern const char *" + module.name + "Version;");
        outData.println("extern int32 " + module.name + "Signature;");
        outData.println();
        PopUbiGen.generateCStructs(module, outData, true);
        outData.println();
        outData.println("class T" + module.name + "Interface : public UbiBaseVars");
        outData.println("{");
        outData.println("public:");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          if (structure.codeType != Structure.PUBLIC)
            continue;
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field)structure.fields.elementAt(j);
            if (field.name.equals("connect") == true)
            {
              outData.println("  // connect is already defined in the UbiBaseVars class");
              continue;
            }
            if (field.name.equals("logFile") == true)
            {
              outData.println("  // logFile is already defined in the UbiBaseVars class");
              continue;
            }
            outData.println("  " + field.type.cDefUbi(field.name) + ";");
          }
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PRIVATE
          || prototype.codeType == Prototype.PROTECTED)
            continue;
          PopUbiGen.generateVirtualCHeader(module, prototype, outData);
        }
        if (hasProtected == true)
        {
          outData.println("protected:");
          for (int i = 0; i < module.structures.size(); i++)
          {
            Structure structure = (Structure)module.structures.elementAt(i);
            if (structure.codeType != Structure.PROTECTED)
              continue;
            for (int j = 0; j < structure.fields.size(); j++)
            {
              Field field = (Field)structure.fields.elementAt(j);
              outData.println("  " + field.type.cDefUbi(field.name) + ";");
            }
          }
          for (int i = 0; i < module.prototypes.size(); i++)
          {
            Prototype prototype = (Prototype)module.prototypes.elementAt(i);
            if (prototype.codeType != Prototype.PROTECTED)
              continue;
            PopUbiGen.generateVirtualCHeader(module, prototype, outData);
          }
        }
        outData.println("};");
        outData.println();
        outData.println("class T" + module.name + " : public T" + module.name + "Interface");
        outData.println("{");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          if (structure.codeType == Structure.PROTECTED)
            hasProtected = true;
          if (structure.codeType != Structure.PRIVATE)
            continue;
          for (int j = 0; j < structure.fields.size(); j++)
          {
            Field field = (Field)structure.fields.elementAt(j);
            outData.println("  " + field.type.cDefUbi(field.name) + ";");
          }
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PROTECTED)
            hasProtected = true;
          if (prototype.codeType != Prototype.PRIVATE)
            continue;
          PopUbiGen.generateCHeader(module, prototype, outData);
        }
        outData.println("public:");
        outData.println("  static T" + module.name + "Handle " + module.name + "Handle;");
        outData.println("  int instance;");
        outData.println("  T" + module.name + "(){instance = " + module.name + "Handle.Create(this);}");
        outData.println(" ~T" + module.name + "(){" + module.name + "Handle.Release(instance, 0);}");
        outData.println("  static T" + module.name + "* Instance(int instance=100)");
        outData.println("  {");
        outData.println("    T" + module.name + "* result = (T" + module.name + "*) " + module.name + "Handle.Use(instance);");
        //outData.println("    if (result == 0)");
        //outData.println("      throw xIdlException(__FILE__, __LINE__, \"" + module.name + "\", 0, \"Module " + module.name + " not instantiated (most likely not loaded).\");");
        outData.println("    return result;");
        outData.println("  }");
        outData.println("  void StartUpCode()");
        outData.println("  {");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          if (structure.codeType == Structure.NORMAL)
            continue;
          if (structure.code.size() > 0 && structure.codeLine > 0)
            outData.println("/** #line " + structure.codeLine + " \"" + nameOf(module.sourceName.toLowerCase()) + "\"**/");
          for (int j = 0; j < structure.code.size(); j++)
          {
            String codeLine = (String)structure.code.elementAt(j);
            if ((codeLine.trim()).equalsIgnoreCase("onshutdown:"))
            {
              outData.println("  }");
              outData.println("  void ShutDownCode()");
              outData.println("  {");
              hasShutDownCode = true;
            }
            else
              outData.print(codeLine);
          }
        }
        outData.println("  }");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PRIVATE
          || prototype.codeType == Prototype.PROTECTED)
            continue;
          PopUbiGen.generateCHeader(module, prototype, outData);
        }
        if (hasProtected == true)
        {
          outData.println("protected:");
          for (int i = 0; i < module.structures.size(); i++)
          {
            Structure structure = (Structure)module.structures.elementAt(i);
            if (structure.codeType != Structure.PROTECTED)
              continue;
            for (int j = 0; j < structure.fields.size(); j++)
            {
              Field field = (Field)structure.fields.elementAt(j);
              outData.println("  " + field.type.cDefUbi(field.name) + ";");
            }
          }
          for (int i = 0; i < module.prototypes.size(); i++)
          {
            Prototype prototype = (Prototype)module.prototypes.elementAt(i);
            if (prototype.codeType != Prototype.PROTECTED)
              continue;
            PopUbiGen.generateCHeader(module, prototype, outData);
          }
        }
        outData.println("};");
        outData.println();
        outData.println("class T" + module.name + "Server : public UbiServer");
        outData.println("{");
        outData.println("public:");
        outData.println("  T" + module.name + "Server();");
        outData.println("  ~T" + module.name + "Server();");
        outData.println("  void StartUpCode()");
        outData.println("  {");
        outData.println("    " + module.name + "->StartUpCode();");
        outData.println("  }");
        outData.println("#if defined(M_AIX) || defined(M_GNU)");
        outData.println("  void SetConnect(TJConnector *connect)");
        outData.println("  {");
        outData.println("    " + module.name + "->connect = connect;");
        outData.println("  }");
        outData.println("#endif");  
        outData.println("  T" + module.name + " *" + module.name + ";");
        outData.println("  e" + module.name + " result;");
        outData.println("  char* replyBody;");
        outData.println("  int32 replySize;");
        outData.println("  int32 recvSize;");
        outData.println("  void Service(const UbiData *input, UbiData *output);");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          outData.println("  e" + module.name + " " + prototype.name + "(char *ip);");
        }
        outData.println("};");
        outData.println();
        outData.println("#endif");
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static String cleanUp(String data)
  {
    if (data.indexOf("HEADER:") == 0)
      return data.substring(7);
    if (data.indexOf("CODE:") == 0)
      return data.substring(5);
    if (data.indexOf("BOTH:") == 0)
      return data.substring(5);
    return data;
  }
  private static void generateEmbedHCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String modname = module.name.toLowerCase();
      File mainfile = new File(output + modname + "embed.h");
      //outLog.println("Code: " + output + modname + "embed.h");
      if (mainfile.exists())
        return;
      OutputStream outFile = new FileOutputStream(output + modname + "embed.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code is generated if there is not a current copy of it.");
        outData.println("// If you want to modify it copy it out of the generate path and use the.");
        outData.println("// copy for your compilation.");
        outData.println("");
        outData.println("#ifndef " + modname.toLowerCase() + "embedH");
        outData.println("#define " + modname.toLowerCase() + "embedH");
        outData.println();
        outData.println("#include \"callbackinterface.h\"");
        outData.println("#include \"pythonmodule.h\"");
        outData.println();
        outData.println("#endif");
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
      System.out.println(e1.toString());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static boolean generateEmbedCCode(Module module, String output, PrintWriter outLog)
  {
    boolean todo = false;
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.code.size() == 0 && prototype.name.compareTo("SetupPythonInterp") == 0)
      {
        todo = true;
        break;
      }
    }
    if (todo == false)
      return false;
    try
    {
      String modname = module.name.toLowerCase();
      outLog.println("Code: " + output + modname + "embed.cpp");
      outLog.println("Code: " + output + modname + "embed.h");
      File mainfile = new File(output + modname + "embed.cpp");
      if (mainfile.exists())
        return todo;
      OutputStream outFile = new FileOutputStream(output+modname+"embed.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code is generated if there is not a current copy of it.");
        outData.println("// If you want to modify it copy it out of the generate path and use the.");
        outData.println("// copy for your compilation.");
        outData.println("");
        outData.println("#include \"pythontraceback.h\"");
        outData.println("#include \"" + modname + "embed.h\"");
        outData.println("#include \"" + modname + ".h\"");
        outData.println("");
        outData.println("void T" + module.name + "::SetupPythonInterp(Config *config)");
        outData.println("{");
        outData.println("#if defined(PYZIP_MODULE_USAGE)");
        outData.println("  bstring pythonPath;");
        outData.println("  bstring moduleList;");
        outData.println("  config->Get(\"pythonPath\", pythonPath);");
        outData.println("  config->Get(\"moduleList\", moduleList);");
        outData.println("  if (TPythonModule::LoadModules(this, config, moduleList.c_str()) == false)");
        outData.println("    throw XPython(\"Python Modules failed to load or could not be initialised.\");");
        outData.println("  interp = new TPython(pythonPath.c_str());");
        outData.println("  pyModule = interp->Import(\"" + module.name.toUpperCase() + "_CODE\");");
        outData.println("#else");
        outData.println("  bstring scriptPath;");
        outData.println("  bstring moduleList;");
        outData.println("  config->Get(\"scriptPath\", scriptPath);");
        outData.println("  interfaceHandle = InterfaceCBHandle.Create(callbackInterface);");
        outData.println("  config->Get(\"moduleList\", moduleList);");
        outData.println("  if (TPythonModule::LoadModules(this, config, moduleList.c_str()) == false)");
        outData.println("    throw XPython(\"Python Modules failed to load or could not be initialised.\");");
        outData.println("  char *argv[1];");
        outData.println("  argv[0] = \"" + module.name + "Server\";");
        outData.println("  interp = new TPython(interfaceHandle, (char*)scriptPath.c_str(), 1, argv, false);");
        outData.println("  pyModule = interp->Import(\"" + module.name.toUpperCase() + "_CODE\", 2);");
        outData.println("#endif");
        outData.println("  if (pyModule == 0)");
        outData.println("  {");
        outData.println("    TBChar errorBuf;");
        outData.println("    TPythonTraceback::PyErrorHandler(errorBuf);");
        outData.println("    errorBuf.append(\"\\nModule failed to import.\");");
        outData.println("    throw XPython(errorBuf.data);");
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
      System.out.println(e1.toString());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
    return true;
  }
  private static boolean generatePyCCode(Module module, String output, PrintWriter outLog)
  {
    boolean todo = false;
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.code.size() == 0 && prototype.isRpcCall())
      {
        todo = true;
        break;
      }
    }
    if (todo == false)
      return false;
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"pycode.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"pycode.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"" + module.name.toLowerCase() + "pycode.h\"");
        outData.println("#include \"pythontraceback.h\"");
        outData.println("#include \"ubixcept.h\"");
        outData.println("#include \"tbuffer.h\"");
        outData.println();
        outData.println("struct _autoModule");
        outData.println("{");
        outData.println("  PyObject *module;");
        outData.println("  _autoModule(const char* name)");
        outData.println("  {");
        outData.println("    PyObject *pyName;");
        outData.println("    pyName = PyString_FromString(name);");
        outData.println("    module = PyImport_Import(pyName);");
        outData.println("    Py_DECREF(pyName);");
        outData.println("  }");
        outData.println("  ~_autoModule()");
        outData.println("  {");
        outData.println("    Py_XDECREF(module);");
        outData.println("  }");
        outData.println("};");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.code.size() == 0 && prototype.isRpcCall())
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, outData);
            outData.println("{");
            generateMethodCall(module, prototype, outData);
            outData.println("}");
            outData.println();
          }
        }
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
      System.out.println(e1.toString());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
    return true;
  }
  public PopUbiServer2() {}
  private static PopUbiServer2 us = new PopUbiServer2(); 
  private static Vector<PythonArgs> pythonArgs;
  class PythonArgs
  {
    String name;
    Field field;
    String sizeName;
    boolean isInput = false;
    boolean isOutput = false;
    boolean listType = false;
    PythonArgs()
    {
      sizeName = "";
    }
  }
  public static boolean pyList(byte typeof)
  {
    switch (typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return false;
      default:
        return true;
    }
  }
  private static String typeof(Type type)
  {
    switch (type.typeof)
    {
      case Type.USERTYPE:
        return type.name;
      case Type.BOOLEAN:
      case Type.SHORT:
      case Type.BYTE:
      case Type.INT:
        return "int32";
      case Type.LONG:
        return "int64";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "double";
      case Type.CHAR:
        return "char";
      case Type.STRING:
        return "char*";
      default:
        return type.name;
    }
  }
  private static String inIntrinsic(Field field, int no)
  {
    String ptr = "";
    switch (field.type.typeof)
    {
      case Type.BYTE:
      case Type.SHORT:
      case Type.BOOLEAN:
      case Type.INT:
        if (field.type.reference == Type.BYPTR)
          ptr = "*";
        return "PyObject* _item" + no + " = PyInt_FromLong((int64)" + ptr + field.name + ");";
      case Type.LONG:
        if (field.type.reference == Type.BYPTR)
          ptr = "*";
        return "PyObject* _item" + no + " = PyLong_FromLong((int64)" + ptr + field.name + ");";
      case Type.FLOAT:
      case Type.DOUBLE:
        if (field.type.reference == Type.BYPTR)
          ptr = "*";
        return "PyObject* _item" + no + " = PyFloat_FromDouble((double)" + ptr + field.name + ");";
      case Type.CHAR:
      case Type.STRING:
        return "PyObject* _item" + no + " = PyString_FromString(" + field.name + ");";
      default:
        return "#error not catered for choose another form of input";
    }
  }
  private static String outIntrinsic(String name, Type type, int no)
  {
    switch (type.typeof)
    {
      case Type.BYTE:
      case Type.SHORT:
      case Type.BOOLEAN:
      case Type.INT:
        return name + " = PyInt_AsLong(_out" + no + ");";
      case Type.LONG:
        return name + " = PyLong_AsLong(_out" + no + ");";
      case Type.FLOAT:
      case Type.DOUBLE:
        return name + " = PyFloat_AsDouble(_out" + no + ");";
      case Type.CHAR:
      case Type.STRING:
        return name + " = PyString_AsString(_out" + no + ");";
      default:
        return "#error not catered for choose another form of output";
    }
  }
  private static void generateMethodCall(Module module, Prototype prototype, PrintWriter outData)
  {
    pythonArgs = new Vector<PythonArgs>();
    boolean hasResult = false;
    int noInputs = 1;
    int noOutputs = 0;
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      PythonArgs arg = us.new PythonArgs();
      arg.field = field;
      arg.isInput = field.isInput;
      if (field.isInput)
        noInputs++;
      arg.isOutput = field.isOutput;
      if (field.isOutput)
        noOutputs++;
      arg.name = field.name;
      arg.listType = pyList(field.type.typeof);
      if (field.isInput == true && field.hasSize == true)
        arg.sizeName = field.input.getSizeName();
      else if (field.hasSize == true)
        arg.sizeName = field.output.getSizeName();
      pythonArgs.addElement(arg);
    }
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
    {
      hasResult = true;
      outData.println("  " + prototype.type.cDefUbi("_returnValue = 0;"));
      noOutputs++;
    }
    outData.println("  _autoModule _am(\"" + module.name.toUpperCase() + "_CODE\");"); 
    outData.println("  if (_am.module != 0)");
    outData.println("  {");
    outData.println("    PyObject* _function;");
    outData.println("    PyObject* _args;");
    outData.println("    PyObject* _result;");
    outData.println("    _function = PyObject_GetAttrString(_am.module, \"" + prototype.name + "\");");
    outData.println("    if (_function && PyCallable_Check(_function))");
    outData.println("    {");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.listType == true)
        outData.println("      Transfer<" + typeof(arg.field.type) + "> _transfer" + i + ";");
    }
    int inpNo = 1;
    outData.println("      _args = PyTuple_New(" + noInputs + ");");
    outData.println("      PyObject* _signature = PyInt_FromLong(" + prototype.signature(true) + ");");
    outData.println("      PyTuple_SetItem(_args, 0, _signature);");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isInput)
      {
        String item = "_item" + inpNo;
        String type = typeof(arg.field.type);
        if (arg.listType == true && arg.sizeName.length() == 0)
        {
          outData.println("      PyObject* " + item + " = PyList_New(" + type + "::NoBuildFields());");
          outData.println("      _transfer" + i + ".rec = *" + arg.name + ";");
          outData.println("      _transfer" + i + ".toList(" + item + ");");
          outData.println("      PyTuple_SetItem(_args, " + inpNo + ", " + item + ");");
        }
        else if (arg.listType == true)
        {
          outData.println("      PyObject* _list" + inpNo + " = PyList_New(" + arg.sizeName + ");");
          outData.println("      for (int i = 0; i < " + arg.sizeName + "; i++)");
          outData.println("      {");
          outData.println("        _transfer" + i + ".rec = " + arg.name + "[i];");
          outData.println("        PyObject* " + item + " = PyList_New(" + type + "::NoBuildFields());");
          outData.println("        _transfer" + i + ".toList(" + item + ");");
          outData.println("        PyList_SetItem(_list" + inpNo + ", i, " + item + ");");
          outData.println("      }");
          outData.println("      PyTuple_SetItem(_args, " + inpNo + ", _list" + inpNo + ");");
        }
        else
        {
          outData.println("      " + inIntrinsic(arg.field, inpNo));
          outData.println("      PyTuple_SetItem(_args, " + inpNo + ", " + item + ");");
        }
        inpNo++;
      }
    }
    outData.println("      _result = PyObject_CallObject(_function, _args);");
    outData.println("      Py_DECREF(_args);");
    outData.println("      if (_result != 0)");
    outData.println("      {");
    String pad = "";
    if (noOutputs > 1)
    {
      pad = "  ";
      outData.println("        if (PyTuple_Check(_result) == true && PyTuple_Size(_result) == " + noOutputs + ")");
      outData.println("        {");
    }
    int outNo = 0;
    if (hasResult == true)
    {
      if (noOutputs > 1)
        outData.println(pad + "        PyObject* _out" + outNo + " = PyTuple_GetItem(_result, " + outNo + ");");
      else
        outData.println(pad + "        PyObject* _out" + outNo + " = _result;");
      outData.println(pad + "        " + outIntrinsic("_returnValue", prototype.type, outNo));
      outNo++;
    }
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (noOutputs > 1)
        outData.println(pad + "        PyObject* _out" + outNo + " = PyTuple_GetItem(_result, " + outNo + ");");
      else
        outData.println(pad + "        PyObject* _out" + outNo + " = _result;");
      if (arg.listType == true && arg.sizeName.length() == 0)
      {
        outData.println(pad + "        if (PyList_Check(_out" + outNo + ") == true)");
        outData.println(pad + "        {");
        outData.println(pad + "          _transfer" + i + ".fromList(_out" + outNo + ");");
        outData.println(pad + "          *" + arg.field.name + " = _transfer" + i + ".rec;");
        outData.println(pad + "        }");
      }
      else if (arg.listType == true)
      {
        outData.println(pad + "        if (PyList_Check(_out" + outNo + ") == true)");
        outData.println(pad + "        {");
        outData.println(pad + "          " + arg.field.name + " = (" + typeof(arg.field.type) + "*) calloc(*" + arg.sizeName + ", sizeof(" + typeof(arg.field.type) + "));");
        outData.println(pad + "          for (int i = 0; i < *" + arg.sizeName + "; i++)");
        outData.println(pad + "          {");
        outData.println(pad + "            PyObject* _inner" + outNo + " = PyList_GetItem(_out" + outNo + ", i);");
        outData.println(pad + "            _transfer" + i + ".fromList(_inner" + outNo + ");");
        outData.println(pad + "            " + arg.field.name + "[i] = _transfer" + i + ".rec;");
        outData.println(pad + "          }");
        outData.println(pad + "        }");
      }
      else if (arg.field.type.typeof == Type.CHAR && arg.sizeName.length() > 0)
      {
        if (arg.field.type.reference == Type.BYREFPTR)
        {
          outData.println(pad + "        if (*" + arg.sizeName + " > 0)");
          outData.println(pad + "        {");
          outData.println(pad + "          char *" + outIntrinsic("_temp" + outNo, arg.field.type, outNo));
          outData.println(pad + "          " + arg.field.name + " = (char*) malloc(*" + arg.sizeName + ");");
          outData.println(pad + "          memcpy(" + arg.field.name + ", _temp" + outNo + ", *" + arg.sizeName + ");");
          outData.println(pad + "        }");
        }
      }
      else
        outData.println(pad + "        *" + outIntrinsic(arg.field.name, arg.field.type, outNo));
      outNo++;
    }
    if (noOutputs > 1)
      outData.println("        }");
    outData.println("        Py_DECREF(_result);");
    outData.println("      }");
    outData.println("      else");
    outData.println("      {");
    outData.println("        TBChar errorBuf;");
    outData.println("        TPythonTraceback::PyErrorHandler(errorBuf);");
    outData.println("        throw TUbiException(99999, errorBuf.data, __FILE__, __LINE__);");
    outData.println("      }");
    outData.println("      Py_XDECREF(_function);");
    outData.println("    }");
    outData.println("  }");
    if (hasResult == true)
      outData.println("  return _returnValue;");
  }
  private static void generatePyHCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + "pycode.h");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + "pycode.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("#ifndef " + module.name.toLowerCase() + "pycodeH");
        outData.println("#define " + module.name.toLowerCase() + "pycodeH");
        outData.println("");
        outData.println("#include \"pythonbuild.h\"");
        outData.println("#include \"" + module.name.toLowerCase() + ".h\"");
        outData.println("");
        outData.println("#endif");
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
      System.out.println(e1.toString());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static void generateCCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"impl.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"impl.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"machine.h\"");
        outData.println("#include \"ubixcept.h\"");
        outData.println();
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println("#include <assert.h>");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+".h\"");
        outData.println("T" + module.name + "Handle T" + module.name + "::" + module.name + "Handle;");
        outData.println();
        outData.println("#define THROW(err) throw TUbiException(" + module.name.toUpperCase() + "_##err, \"\", __FILE__, __LINE__);");
        outData.println("#define THROW2(err, str) throw TUbiException(" + module.name.toUpperCase() + "_##err, str, __FILE__, __LINE__);");
        outData.println();
        outData.println("template <class TYPE, class INDEX>");
        outData.println("void AddList(TYPE*& List, INDEX& Index, const TYPE& Rec, const INDEX Delta)");
        outData.println("{");
        outData.println("  if ((List == 0) != (Index == 0))");
        outData.println("    THROW(ADDLIST_ERROR);");
        outData.println("  if (Index % Delta == 0)");
        outData.println("  {");
        outData.println("    TYPE* Resized = (TYPE*) realloc(List, sizeof(Rec)*(Index+Delta));");
        outData.println("    if (Resized == 0) // Cater for alloc failure");
        outData.println("      THROW(ADDLIST_REALLOC_FAILED);");
        outData.println("    List = Resized;");
        outData.println("  }");
        outData.println("  List[Index++] = Rec;");
        outData.println("}");
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String) module.code.elementAt(j);
            if (codeLine.indexOf("#include") != -1 || codeLine.indexOf("HEADER:") != -1)
            {
              if (codeLine.indexOf("BOTH:") == -1 && codeLine.indexOf("CODE:") == -1)
              {
                if (firstTime != true)
                  outData.println();
                continue;
              }
            }
            if (firstTime == true && module.codeLine > 0)
            {
              firstTime = false;
              outData.println("/** #line "+(module.codeLine+j)+" \""+nameOf(module.sourceName.toLowerCase())+"\" **/");
            }
            outData.print(cleanUp(codeLine));
          }
          if (firstTime == false)
            outData.println();
        }
        outData.println();
        outData.println("#ifndef AUTO_PROFILE");
        outData.println("#define AUTO_PROFILE(tag)");
        outData.println("#endif");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.code.size() > 0)
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, outData);
            outData.println("{");
            outData.println("  AUTO_PROFILE("+prototype.name+");");
            if (prototype.code.size() > 0 && prototype.codeLine > 0)
              outData.println("/** #line "+prototype.codeLine+" \""+nameOf(module.sourceName.toLowerCase())+"\" **/");
            for (int j = 0; j < prototype.code.size(); j++)
            {
              String codeLine = (String) prototype.code.elementAt(j);
              outData.print(codeLine);
            }
            outData.println("}");
            outData.println();
          }
        }
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  private static String toHex(String value)
  {
    int n = new Integer(value).intValue();
    return toHex(n);
  }
  private static String toHex(int value)
  {
    String result = Integer.toHexString(value);
    while (result.length() < 8)
      result = "0" + result;
    result = "0x" + result.toUpperCase();
    return result;
  }
  private static void generateReqIDTable(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"ReqID.txt");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"ReqID.txt");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.message.length() > 0)
            outData.println("'" + module.name + "', " + toHex(prototype.message) + ", '" + prototype.name + "'");
          else
            outData.println("'" + module.name + "', " + toHex(i) + ", '" + prototype.name + "'");
        }
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
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
  private static void generateCMain(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + ".cpp");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + ".cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code is initial minimal code, copy it to another source point if you need to modify it.");
        outData.println("#include \"machine.h\"");
        outData.println();
        outData.println("#include \"ubimain.h\"");
        outData.println();
        outData.println("#include \"xcept.h\"");
        outData.println("#include \"cmdlinearg.h\"");
        outData.println("#include \"config.h\"");
        outData.println("#include \"logfile.h\"");
        outData.println();
        outData.println("#include \"" + module.name.toLowerCase() + ".h\"");
        outData.println();
        outData.println("int main(int argc, char **argv)");
        outData.println("{");
        outData.println("  UbiMain *server = 0;");
        outData.println("  try");
        outData.println("  {");
        outData.println("    if (!CmdLineArg::Initialize(argc, argv))");
        outData.println("      return 1;");
        outData.println("    server = new UbiMain(new T" + module.name + "Server());");
        outData.println("    server->Initialize();  ");
        outData.println("    server->Run();");
        outData.println("  }");
        outData.println("  catch (xCept &x)");
        outData.println("  {");
        outData.println("    SingletonLogFile::LogFile()->lprintf(eLogError, \"Exception caught in main\");");
        outData.println("    SingletonLogFile::LogFile()->Log(x);");
        outData.println("    if (server) { delete server; server = 0; }");
        outData.println("    return x.Error();");
        outData.println("  }");
        outData.println("  catch (...)");
        outData.println("  {");
        outData.println("    SingletonLogFile::LogFile()->lprintf(eLogError, \"Unknown exception caught in main\");");
        outData.println("    if (server) { delete server; server = 0; }");
        outData.println("    throw;");
        outData.println("  }");
        outData.println("  if (server) { delete server; server = 0; }");
        outData.println("  return 0;");
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
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
  private static boolean generateCServer(Module module, String output, PrintWriter outLog)
  {
    boolean hasCode = false;
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"server.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"server.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"machine.h\"");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println("#include \"" + module.name.toLowerCase() + ".h\"");
        outData.println();
        outData.println("const char *"+module.name+"Version = "+module.version+";");
        outData.println("int32 "+module.name+"Signature = "+module.signature+";");
        outData.println();
        if (alignForSun == true)
        {
          outData.println("inline int32 alignData(int32 size)");
          outData.println("{");
          outData.println("  int n = size % 8;");
          outData.println("  if (n > 0) size += (8-n);");
          outData.println("  return size;");
          outData.println("}");
          outData.println();
        }
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          String comma = "  ";
          outData.println("char *"+module.name+table.name+"[] = ");
          outData.println("{");
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            outData.println(comma+message.value+"   // "+message.name);
            comma = ", ";
          }
          outData.println("};");
          outData.println();
        }
        outData.println("T"+module.name+"Server::T"+module.name+"Server()");
        outData.println("{");
        outData.println("  " + module.name + " = new T" + module.name + "();");
        for (int j = 0; j < module.pragmas.size(); j++)
        {
          String pragma = (String)module.pragmas.elementAt(j);
          int n = pragma.indexOf("CACHE:");
          if (n < 0) 
            continue;
          pragma = pragma.substring(n+6).trim();
          outData.println("  " + pragma + "::Instance()->Setup();");
          break;
        }
        outData.println("}");
        outData.println();
        outData.println("T"+module.name+"Server::~T"+module.name+"Server()");
        outData.println("{");
        if (hasShutDownCode == true)
          outData.println("  " + module.name + "->ShutDownCode();");
        outData.println("}");
        outData.println();
        outData.println("void T" + module.name + "Server::Service(const UbiData *input, UbiData *output)");
        outData.println("{");
        outData.println("#ifdef __LOGFILE_H_");
        outData.println("#define IDL2_UBI_LOG(x) x");
        outData.println("#else");
        outData.println("#define IDL2_UBI_LOG(x)");
        outData.println("#endif");
        outData.println("  char* ip = (char*)input->data;");
        outData.println("  int32 signature;");
        outData.println("  signature = *(int32*)ip;");
        outData.println("  SwapBytes(signature);");
        if (alignForSun == true)
          outData.println("  ip += alignData(sizeof(int32));");
        else
          outData.println("  ip += sizeof(int32);");
        outData.println("  switch (input->reqID)");
        outData.println("  {");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.code.size() > 0)
            hasCode = true;
          if (prototype.message.length() > 0)
            outData.println("  case "+toHex(prototype.message)+":");
          else
            outData.println("  case "+toHex(i)+":");
          outData.println("    IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"reqID=%d " + prototype.name + "[" + prototype.signature(true) + "]\", input->reqID)));");
          outData.println("    if (signature != " + toHex((int)prototype.signature(true)) + ")");
          outData.println("      result = "+module.name.toUpperCase()+"_INV_SIGNATURE;");
          outData.println("    else");
          outData.println("      result = "+prototype.name+"(ip);");
          outData.println("    IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"result=%d " + prototype.name + "[" + prototype.signature(true) + "]\", result)));");
          outData.println("    break;");
        }
        outData.println("  default:");
        outData.println("    IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"reqID=%d *** Unknown Message ***\", result)));");
        outData.println("    result = " + module.name.toUpperCase() + "_UNKNOWN_FUNCTION;");
        outData.println("    return;");
        outData.println("  }");
        outData.println("  output->Use((unsigned char *)replyBody, replySize, result);");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCServer(module, prototype, i, outData);
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
      System.out.println(e1.toString ());
      System.out.flush();
      e1.printStackTrace();
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
    return hasCode;
  }
  /**
  * Generates the prototypes defined
  */
  private static void generateCServer(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    outData.println("e" + module.name + " T" + module.name + "Server::" + prototype.name + "(char *ip)");
    outData.println("{");
    outData.println("  e" + module.name + " resultCode = " + module.name.toUpperCase() + "_OK;");
    outData.println("  try");
    outData.println("  {");
    outData.println("    replySize = 0;replyBody = 0;");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      boolean addUp = true;
      if (i + 1 == prototype.inputs.size())
        addUp = false;
      Action input = (Action)prototype.inputs.elementAt(i);
      Field field = input.getParameter(prototype);
      if (field == null)
        continue;
      byte savedReference = field.type.reference;
      if (field.type.reference == Type.BYPTR
      || field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("    recvSize = *(int32*)ip;");
            outData.println("    SwapBytes(recvSize);");
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof(int32));");
            else
              outData.println("    ip += sizeof(int32);");
            outData.println("    char* " + field.name + " = ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(recvSize);");
              else
                outData.println("    ip += recvSize;");
            }
          }
          else if (field.type.typeof == Type.USERTYPE)
          {
           
          }
          else
          {
            outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(sizeof(*" + field.name + "));");
              else
                outData.println("    ip += sizeof(*" + field.name + ");");
            }
          }
        }
        else
        {
          outData.println("    recvSize = *(int32*)ip;");
          outData.println("    SwapBytes(recvSize);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32));");
          else
            outData.println("    ip += sizeof(int32);");
          outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(recvSize);");
            else
              outData.println("    ip += recvSize;");
          }
        }
      }
      else
      {
        outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(*" + field.name + "));");
          else
            outData.println("    ip += sizeof(*" + field.name + ");");
        }
        field.type.reference = Type.BYPTR;
      }
      Field opField0 = field;
      Operation inOp = input.sizeOperation();
      byte saveRef0 = 0;
      if (inOp != null)
      {
        opField0 = prototype.getParameter(inOp.name);
        if (opField0 != null)
        {
          saveRef0 = opField0.type.reference;
          opField0.type.reference = Type.BYPTR;
        }
      }
      generateCSwaps(module, prototype, field, inOp, outData);
      if (inOp != null && opField0 != null)
        opField0.type.reference = saveRef0;
      field.type.reference = savedReference;
    }
    boolean hasOutput = false;
    boolean hasresult = false;
    if (prototype.type.typeof != Type.VOID
    || prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        if (alignForSun == true)
          outData.println("    replySize += alignData((int32)sizeof(" + prototype.type.cNameUbi() + ")); // result Size");
        else
          outData.println("    replySize += (int32)sizeof(" + prototype.type.cNameUbi() + "); // result Size");
        hasOutput = true;
        hasresult = true;
      }
    }
    int Variants = 0;
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action output = (Action)prototype.outputs.elementAt(i);
      Field field = output.getParameter(prototype);
      if (field == null)
        continue;
      hasOutput = true;
      Operation op = output.sizeOperation();
      if (field.type.reference == Type.BYPTR)
      {
        if (output.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("#error unsized char* on output");
            errLog.println("#error unsized char* on output");
          }
          else
          {
            if (alignForSun == true)
              outData.println("    replySize += alignData(sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
            else
              outData.println("    replySize += sizeof(" + field.type.cNameUbi() + "); // size " + field.name);
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          if (alignForSun == true)
            outData.println("    replySize += alignData(sizeof(int32));  // size of Block");
          else
            outData.println("    replySize += sizeof(int32);  // size of Block");
          if (alignForSun == true)
            outData.println("    replySize += alignData(" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
          else
            outData.println("    replySize += (" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
        }
      }
      else if (field.type.reference == Type.BYREFPTR)
      {
        if (output.hasSize() == false)
        {
          outData.println("#error unsized BYREFPTR on output");
          errLog.println("#error unsized BYREFPTR on output");
        }
        else
        {
          Variants++;
          if (alignForSun == true)
            outData.println("    replySize += alignData(sizeof(" + field.type.cNameUbi() + "*)); // Variant " + field.name);
          else
            outData.println("    replySize += sizeof(" + field.type.cNameUbi() + "*); // Variant " + field.name);
        }
      }
      else
      {
        if (alignForSun == true)
          outData.println("    replySize += alignData(sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
        else
          outData.println("    replySize += sizeof(" + field.type.cNameUbi() + "); // size " + field.name);
      }
    }
    if (hasOutput)
    {
      if (Variants > 0)
      {
        outData.println("    int32 Variants[" + Variants + "];");
        outData.println("    int32 VariantSize[" + Variants + "];");
        Variants = 0;
      }
      outData.println("    replyBody = new char [replySize];");
      outData.println("    memset(replyBody, 0, replySize);");
      outData.println("    ip = replyBody;");
    }
    if (prototype.type.typeof != Type.VOID
    || prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        outData.println("    " + prototype.type.cNameUbi() + "* result = (" + prototype.type.cNameUbi() + "*) ip;");
        if (prototype.outputs.size() > 0)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(" + prototype.type.cNameUbi() + "));");
          else
            outData.println("    ip += sizeof(" + prototype.type.cNameUbi() + ");");
        }
      }
    }
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      boolean addUp = true;
      if (i + 1 == prototype.outputs.size())
        addUp = false;
      Action output = (Action)prototype.outputs.elementAt(i);
      Field field = output.getParameter(prototype);
      if (field == null)
        continue;
      Operation op = output.sizeOperation();
      Action input = (Action)prototype.getInputAction(field.name);
      if (field.type.reference == Type.BYPTR)
      {
        if (output.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
            continue;
          else
          {
            if (input != null)
            {
              outData.println("    memcpy(ip, " + field.name + ", sizeof(" + field.type.cNameUbi() + "));");
              outData.println("    " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
            }
            else
              outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(sizeof(" + field.type.cNameUbi() + "));");
              else
                outData.println("    ip += sizeof(" + field.type.cNameUbi() + ");");
            }
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          outData.println("    *(int32*)ip = (" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + "));");
          outData.println("    SwapBytes(*(int32*)ip);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32));");
          else
            outData.println("    ip += sizeof(int32);");
          if (input != null)
          {
            outData.println("    memcpy(ip, " + field.name + ", (" + w1 + op.name + " * sizeof(" + field.type.cNameUbi() + ")));");
            outData.println("    " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
          }
          else
            outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(" + w1 + op.name + " * sizeof(" + field.type.cNameUbi() + "));");
            else
              outData.println("    ip += (" + w1 + op.name + " * sizeof(" + field.type.cNameUbi() + "));");
          }
        }
      }
      else if (field.type.reference == Type.BYREFPTR)
      {
        if (output.hasSize() == false)
          continue;
        else
        {
          if (op.isConstant == false)
          {
          }
          else
            outData.println("#error Constant Size with BYREFPTR output");
          outData.println("    Variants[" + (Variants++) + "] = (int32)(ip-replyBody);");
          if (input != null)
            outData.println("    (" + field.type.cNameUbi() + "*)*ip = " + field.name + ";");
          else
            outData.println("    " + field.type.cNameUbi() + "** " + field.name + " = (" + field.type.cNameUbi() + "**)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof(" + field.type.cNameUbi() + "*));");
            else
              outData.println("    ip += sizeof(" + field.type.cNameUbi() + "*);");
          }
        }
      }
      else
      {
        if (input != null)
        {
          outData.println("    memcpy(ip, " + field.name + ", sizeof(" + field.type.cNameUbi() + "));");
          outData.println("    " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
        }
        else
          outData.println("    " + field.type.cNameUbi() + "* " + field.name + " = (" + field.type.cNameUbi() + "*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(" + field.type.cNameUbi() + "));");
          else
            outData.println("    ip += sizeof(" + field.type.cNameUbi() + ");");
        }
      }
    }
    outData.println("    try");
    outData.println("    {");
    String w1 = "", w2 = "";
    outData.print("      ");
    if (hasresult)
      outData.print("*result = ");
    outData.print(module.name + "->" + prototype.name + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      if (field.type.reference == Type.BYPTR)
        w2 = "";
      else
        w2 = "*";
      outData.print(w1 + w2 + field.name);
      w1 = ", ";
    }
    outData.println(");");
    outData.println("    }");
    outData.println("    catch (e" + module.name + " rc)");
    outData.println("    {");
    outData.println("      resultCode = rc;");
    outData.println("    }");
    if (hasresult && prototype.needsSwap())
      outData.println("    SwapBytes(*result);");
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action output = (Action)prototype.outputs.elementAt(i);
      Field field = output.getParameter(prototype);
      if (field == null)
        continue;
      Operation op = output.sizeOperation();
      Field opField = field;
      byte saveRef = 0;
      if (op != null)
      {
        opField = prototype.getParameter(op.name);
        if (opField != null)
        {
          saveRef = opField.type.reference;
          opField.type.reference = Type.BYPTR;
        }
      }
      generateCStructSwaps(module, prototype, field, op, outData);
      if (op != null && opField != null)
        opField.type.reference = saveRef;
    }
    if (Variants > 0)
    {
      outData.println("    // We are going to setup a new replyBody because of Variants");
      outData.println("    char *oldreplyBody = replyBody;");
      outData.println("    ip = replyBody;");
      outData.println("    int32 tail = replySize;");
      outData.println("    // Calc new replySize for Contiguous memory from Disjoint memory");
      outData.println("    replySize = 0;");
      if (prototype.type.typeof != Type.VOID
      || prototype.type.reference == Type.BYPTR)
      {
        if (prototype.type.reference != Type.BYPTR)
        {
          if (alignForSun == true)
            outData.println("    replySize += alignData((int32)sizeof(" + prototype.type.cNameUbi() + ")); // result Size");
          else
            outData.println("    replySize += (int32)sizeof(" + prototype.type.cNameUbi() + "); // result Size");
          hasOutput = true;
          hasresult = true;
        }
      }
      int VariantSize = 0;
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action)prototype.outputs.elementAt(i);
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        hasOutput = true;
        Operation op = output.sizeOperation();
        if (field.type.reference == Type.BYPTR
        || field.type.reference == Type.BYREFPTR)
        {
          if (output.hasSize() == false)
          {
            if (field.type.typeof != Type.CHAR)
            {
              if (alignForSun == true)
                outData.println("    replySize += alignData(sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
              else
                outData.println("    replySize += sizeof(" + field.type.cNameUbi() + "); // size " + field.name);
            }
          }
          else
          {
            w1 = "";
            if (op.isConstant == false)
              w1 = "*";
            if (alignForSun == true)
            {
              outData.println("    replySize += alignData(sizeof(int32));  // size of Block");
              outData.println("    replySize += alignData(" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
            }
            else
            {
              outData.println("    replySize += sizeof(int32);  // size of Block");
              outData.println("    replySize += (" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
            }
            if (field.type.reference == Type.BYREFPTR)
              outData.println("    VariantSize[" + (VariantSize++) + "] = (" + w1 + "" + op.name + " * sizeof(" + field.type.cNameUbi() + "));");
          }
        }
        else
        {
          if (alignForSun == true)
            outData.println("    replySize += alignData(sizeof(" + field.type.cNameUbi() + ")); // size " + field.name);
          else
            outData.println("    replySize += sizeof(" + field.type.cNameUbi() + "); // size " + field.name);
        }
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action)prototype.outputs.elementAt(i);
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        Operation op = output.sizeOperation();
        generateCNonStructSwaps(module, prototype, field, op, outData);
      }
      outData.println("    replyBody = new char [replySize];");
      outData.println("    memset(replyBody, 0, replySize);");
      outData.println("    char *op = replyBody;");
      outData.println("    int32 pos = 0;");
      outData.println("    {");
      outData.println("      for (int i = 0; i < " + Variants + "; i++)");
      outData.println("      {");
      outData.println("        int32 size = Variants[i]-pos;");
      outData.println("        if (size > 0)");
      outData.println("        {");
      outData.println("          memcpy(op, ip, size);");
      if (alignForSun == true)
      {
        outData.println("          ip += alignData(size);");
        outData.println("          op += alignData(size);");
        outData.println("          tail -= alignData(size);");
      }
      else
      {
        outData.println("          ip += size;");
        outData.println("          op += size;");
        outData.println("          tail -= size;");
      }
      outData.println("        }");
      outData.println("        memcpy(op, &VariantSize[i], sizeof(VariantSize[i])); ");
      outData.println("        SwapBytes(*(int32*)op);");
      if (alignForSun == true)
        outData.println("        op += alignData(sizeof(VariantSize[i]));");
      else
        outData.println("        op += sizeof(VariantSize[i]);");
      outData.println("        if (VariantSize[i] > 0)");
      outData.println("        {");
      outData.println("          char **block = (char **)ip;");
      outData.println("          memcpy(op, *block, VariantSize[i]);");
      outData.println("          free(*block);");
      outData.println("        }");
      if (alignForSun == true)
      {
        outData.println("        op += alignData(VariantSize[i]);");
        outData.println("        ip += alignData(sizeof(char *));");
        outData.println("        tail -= alignData(sizeof(char *));");
      }
      else
      {
        outData.println("        op += VariantSize[i];");
        outData.println("        ip += sizeof(char *);");
        outData.println("        tail -= sizeof(char *);");
      }
      if (alignForSun == true)
        outData.println("        pos = alignData(Variants[i])+alignData(sizeof(char *));");
      else
        outData.println("        pos = Variants[i]+sizeof(char *);");
      outData.println("      }");
      outData.println("    }");
      outData.println("    if (tail > 0)");
      outData.println("      memcpy(op, ip, tail);");
      outData.println("    delete [] oldreplyBody;");
    }
    else
    {
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action)prototype.outputs.elementAt(i);
        Field field = output.getParameter(prototype);
        if (field == null)
          continue;
        Operation op = output.sizeOperation();
        generateCNonStructSwaps(module, prototype, field, op, outData);
      }
    }
    outData.println("  }");
    outData.println("  catch (e" + module.name + " rc)");
    outData.println("  {");
    outData.println("    return rc;");
    outData.println("  }");
    outData.println("  return resultCode;");
    outData.println("}");
    outData.println();
  }
  /**
  * Generates the prototypes defined
  */
  public static void generateCSwaps(Module module, Prototype prototype, Field field, Operation op, PrintWriter outData)
  {
    String w1 = "", w2 = "", w3 = ".", w4 = "", w5 = "", w6 = "", w7 = "", w8 = "", w9 = "";
    if (field.needsSwap()
    ||  field.isStruct(module))
    {
      if (field.type.reference == Type.BYPTR
      ||  field.type.reference == Type.BYREFPTR)
      {
        w3 = "->";
        w4 = "*";
      }
      if (op != null)
      {
        w8 = "{";
        w9 = "}";
        Field opField = prototype.getParameter(op.name);
        if (opField != null
        && (opField.type.reference == Type.BYPTR
        ||  opField.type.reference == Type.BYREFPTR))
          w5 = "*";
        outData.println("    "+w2+w8+"for (int i = 0; i < "+w5+op.name+"; i++)");
        w1 = w1 + "[i]";
        w2 = "  " + w2;
        if (field.type.reference == Type.BYREFPTR)
        {
          w6 = "(*";
          w7 = ")";
        }
        w3 = ".";
        w4 = "";
      }
      for (int j=0; j < field.type.arraySizes.size(); j++)
      {
        Integer integer = (Integer) field.type.arraySizes.elementAt(j);
        outData.println("    "+w2+"for (int i" + j + " = 0; i" + j
                        + " < " + integer.intValue()
                        + "; i" + j + "++)");
        w1 = w1 + "[i" + j + "]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
      }
    }
    if (field.isStruct(module))
      outData.println("    "+w2+w6+field.name+w7+w1+w3+"Swaps();"+w9);
    else if (field.needsSwap())
      outData.println("    "+w2+"SwapBytes("+w4+field.name+w1+");"+w9);
    else if (field.type.typeof == Type.USERTYPE)
      errLog.println("Warning: "+prototype.name+" "+field.name+" is of UserType and may require swapping.");
  }
  public static void generateCStructSwaps(Module module, Prototype prototype, Field field, Operation op, PrintWriter outData)
  {
    if (field.isStruct(module) == true)
      generateCSwaps(module, prototype, field, op, outData);
  }
  public static void generateCNonStructSwaps(Module module, Prototype prototype, Field field, Operation op, PrintWriter outData)
  {
    if (field.isStruct(module) == false)
      generateCSwaps(module, prototype, field, op, outData);
  }
}

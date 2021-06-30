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

package vlab.crackle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbi3Server extends Generator
{
  public static String description()
  {
    return "Generates Ubiquitous Server Code (python3) (AIX|SUN|NT|GNU)";
  }
  public static String documentation()
  {
    return "Generates Ubiquitous Server Code (python3) (AIX|SUN|NT|GNU)"
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
      pragmaVector = new Vector();
      pragmaVector.addElement(new Pragma("AlignForSun", false, "Ensure that all fields are on 8 byte boundaries."));
      pragmaVector.addElement(new Pragma("QualifyEnums", false, "Enums are generated qualified."));
    }
  }
  private static boolean alignForSun;
  private static boolean qualifyEnums;
  private static void setPragmas(Module module)
  {
    // Ensure these are in the same order as above
    setupPragmaVector();
    int no=0;
    alignForSun = ((Pragma)pragmaVector.elementAt(no++)).value;
    qualifyEnums = ((Pragma)pragmaVector.elementAt(no++)).value;
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
        alignForSun = true;
      if (pragma.trim().equalsIgnoreCase("QualifyEnums") == true)
        qualifyEnums = true;
    }
    PopUbiGen.qualifyEnums = qualifyEnums;
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
    indent_size = 4;
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#ifndef _"+module.name+"Tables_H_");
        writeln("#define _"+module.name+"Tables_H_");
        writeln();
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          writeln("enum e"+module.name+table.name);
          writeln("{");
          String comma = "  ";
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            writeln(comma+table.name.toLowerCase()+message.name+"   // "+message.value);
            comma = ", ";
          }
          writeln(comma+table.name.toLowerCase()+"NoOf");
          writeln("};");
          writeln();
          writeln("extern char *"+module.name+table.name+"[];");
          writeln();
        }
        writeln("#endif");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#include \"machine.h\"");
        writeln();
        writeln("#include \""+module.name.toLowerCase()+"tables.h\"");
        writeln();
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          String comma = "  ";
          writeln("char *"+module.name+table.name+"[] = ");
          writeln("{");
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            writeln(comma+message.value+"   // "+message.name);
            comma = ", ";
          }
          writeln("};");
          writeln();
        }
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        boolean hasProtected = false;
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#ifndef _" + module.name + "H_");
        writeln("#define _" + module.name + "H_");
        writeln();
        writeln("#include <assert.h>");
        writeln("#include \"swapbytes.h\"");
        writeln("#include \"ubidata.h\"");
        writeln("#include \"ubiserver.h\"");
        writeln("#include \"ubirpc.h\"");
        writeln("#include \"ubibasevars.h\"");
        writeln("#include \"handles.h\"");
        writeln();
        writeln("class T" + module.name + ";");
        writeln("typedef tHandle<T" + module.name + "*, 100, 100> T" + module.name + "Handle;");
        writeln();
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String)module.code.elementAt(j);
            boolean outputIt = false;
            // #include go to header - unless flagged as CODE
            if (codeLine.indexOf("#include") >= 0 && codeLine.indexOf("CODE:") < 0)
              outputIt = true;
            // #other code goes to header if flagged as BOTH or HEADER
            else if (codeLine.indexOf("HEADER:") >= 0 || codeLine.indexOf("BOTH:") >= 0)
              outputIt = true;
            if (outputIt == true)
            {
              if (firstTime)
                firstTime = false;
              write(cleanUp(codeLine));
            }
          }
          if (firstTime == false)
            writeln();
        }
        PopUbiGen.generateCExterns(module, writer);
        writeln("extern const char *" + module.name + "Version;");
        writeln("extern int32 " + module.name + "Signature;");
        writeln();
        PopUbiGen.generateCStructs(module, writer, true);
        writeln();
        writeln("class T" + module.name + "Interface : public UbiBaseVars");
        writeln("{");
        writeln("public:");
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
              writeln(1, "// connect is already defined in the UbiBaseVars class");
              continue;
            }
            if (field.name.equals("logFile") == true)
            {
              writeln(1, "// logFile is already defined in the UbiBaseVars class");
              continue;
            }
            writeln(1, "" + field.type.cDefUbi(field.name) + ";");
          }
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PRIVATE
          || prototype.codeType == Prototype.PROTECTED)
            continue;
          PopUbiGen.generateVirtualCHeader(module, prototype, writer);
        }
        if (hasProtected == true)
        {
          writeln("protected:");
          for (int i = 0; i < module.structures.size(); i++)
          {
            Structure structure = (Structure)module.structures.elementAt(i);
            if (structure.codeType != Structure.PROTECTED)
              continue;
            for (int j = 0; j < structure.fields.size(); j++)
            {
              Field field = (Field)structure.fields.elementAt(j);
              writeln(1, "" + field.type.cDefUbi(field.name) + ";");
            }
          }
          for (int i = 0; i < module.prototypes.size(); i++)
          {
            Prototype prototype = (Prototype)module.prototypes.elementAt(i);
            if (prototype.codeType != Prototype.PROTECTED)
              continue;
            PopUbiGen.generateVirtualCHeader(module, prototype, writer);
          }
        }
        writeln("};");
        writeln();
        writeln("class T" + module.name + " : public T" + module.name + "Interface");
        writeln("{");
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
            writeln(1, "" + field.type.cDefUbi(field.name) + ";");
          }
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PROTECTED)
            hasProtected = true;
          if (prototype.codeType != Prototype.PRIVATE)
            continue;
          PopUbiGen.generateCHeader(module, prototype, writer);
        }
        writeln("public:");
        writeln(1, "static T" + module.name + "Handle " + module.name + "Handle;");
        writeln(1, "int instance;");
        writeln(1, "T" + module.name + "(){instance = " + module.name + "Handle.Create(this);}");
        writeln(" ~T" + module.name + "(){" + module.name + "Handle.Release(instance, 0);}");
        writeln(1, "static T" + module.name + "* Instance(int instance=100)");
        writeln(1, "{");
        writeln(2, "T" + module.name + "* result = (T" + module.name + "*) " + module.name + "Handle.Use(instance);");
        writeln(2, "return result;");
        writeln(1, "}");
        writeln(1, "void StartUpCode()");
        writeln(1, "{");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          if (structure.codeType == Structure.NORMAL)
            continue;
          for (int j = 0; j < structure.code.size(); j++)
          {
            String codeLine = (String)structure.code.elementAt(j);
            if ((codeLine.trim()).equalsIgnoreCase("onshutdown:"))
            {
              writeln(1, "}");
              writeln(1, "void ShutDownCode()");
              writeln(1, "{");
              hasShutDownCode = true;
            }
            else
              write(codeLine);
          }
        }
        writeln(1, "}");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PRIVATE
          || prototype.codeType == Prototype.PROTECTED)
            continue;
          PopUbiGen.generateCHeader(module, prototype, writer);
        }
        if (hasProtected == true)
        {
          writeln("protected:");
          for (int i = 0; i < module.structures.size(); i++)
          {
            Structure structure = (Structure)module.structures.elementAt(i);
            if (structure.codeType != Structure.PROTECTED)
              continue;
            for (int j = 0; j < structure.fields.size(); j++)
            {
              Field field = (Field)structure.fields.elementAt(j);
              writeln(1, "" + field.type.cDefUbi(field.name) + ";");
            }
          }
          for (int i = 0; i < module.prototypes.size(); i++)
          {
            Prototype prototype = (Prototype)module.prototypes.elementAt(i);
            if (prototype.codeType != Prototype.PROTECTED)
              continue;
            PopUbiGen.generateCHeader(module, prototype, writer);
          }
        }
        writeln("};");
        writeln();
        writeln("class T" + module.name + "Server : public UbiServer");
        writeln("{");
        writeln("public:");
        writeln(1, "T" + module.name + "Server();");
        writeln(1, "~T" + module.name + "Server();");
        writeln(1, "void StartUpCode()");
        writeln(1, "{");
        writeln(2, "" + module.name + "->StartUpCode();");
        writeln(1, "}");
        writeln("#if defined(M_AIX) || defined(M_GNU)");
        writeln(1, "void SetConnect(TJConnector *connect)");
        writeln(1, "{");
        writeln(2, "" + module.name + "->connect = connect;");
        writeln(1, "}");
        writeln("#endif");  
        writeln(1, "T" + module.name + " *" + module.name + ";");
        writeln(1, "e" + module.name + " result;");
        writeln(1, "char* replyBody;");
        writeln(1, "int32 replySize;");
        writeln(1, "int32 recvSize;");
        writeln(1, "void Service(const UbiData *input, UbiData *output);");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          writeln(1, "e" + module.name + " " + prototype.name + "(char *ip);");
        }
        writeln("};");
        writeln();
        writeln("#endif");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      if (mainfile.exists())
        return;
      OutputStream outFile = new FileOutputStream(output + modname + "embed.h");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code is generated if there is not a current copy of it.");
        writeln("// If you want to modify it copy it out of the generate path and use the.");
        writeln("// copy for your compilation.");
        writeln("");
        writeln("#ifndef " + modname.toLowerCase() + "embedH");
        writeln("#define " + modname.toLowerCase() + "embedH");
        writeln();
        writeln("#include \"callbackinterface.h\"");
        writeln("#include \"pythonmodule.h\"");
        writeln();
        writeln("#endif");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code is generated if there is not a current copy of it.");
        writeln("// If you want to modify it copy it out of the generate path and use the.");
        writeln("// copy for your compilation.");
        writeln("");
        writeln("#include \"pythontraceback.h\"");
        writeln("#include \"" + modname + "embed.h\"");
        writeln("#include \"" + modname + ".h\"");
        writeln("");
        writeln("void T" + module.name + "::SetupPythonInterp(Config *config)");
        writeln("{");
        writeln(1, "bstring pythonPath;");
        writeln(1, "bstring moduleList;");
        writeln(1, "config->Get(\"pythonPath\", pythonPath);");
        writeln(1, "config->Get(\"moduleList\", moduleList);");
        writeln(1, "if (TPythonModule::LoadModules(this, config, moduleList.c_str()) == false)");
        writeln(2, "throw XPython(\"Python Modules failed to load or could not be initialised.\");");
        writeln(1, "interp = new TPython(pythonPath.c_str());");
        writeln(1, "pyModule = interp->Import(\"" + module.name.toUpperCase() + "_CODE\");");
        writeln(1, "if (pyModule == 0)");
        writeln(1, "{");
        writeln(2, "TBChar errorBuf;");
        writeln(2, "TPythonTraceback::PyErrorHandler(errorBuf);");
        writeln(2, "errorBuf.append(\"\\nModule failed to import.\");");
        writeln(2, "throw XPython(errorBuf.data);");
        writeln(1, "}");
        writeln("}");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#include \"" + module.name.toLowerCase() + "pycode.h\"");
        writeln("#include \"pythontraceback.h\"");
        writeln("#include \"ubixcept.h\"");
        writeln("#include \"tbuffer.h\"");
        writeln("#if PY_MAJOR_VERSION == 3");
        writeln("inline char* getAsString(PyObject* item)");
        writeln("{");
        writeln("    PyObject* l1 = PyUnicode_AsLatin1String(item);");
        writeln("    char* value = PyBytes_AsString(l1);");
        writeln("    Py_DECREF(l1);");
        writeln("    return value;");
        writeln("}");
        writeln("inline int32 getIntAsLong(PyObject* item)");
        writeln("{");
        writeln("    return PyLong_AsLong(item);");
        writeln("}");
        writeln("inline int64 getLongAsLong(PyObject* item)");
        writeln("{");
        writeln("    return PyLong_AsLongLong(item);");
        writeln("}");
        writeln("inline double getFloatAsDouble(PyObject* item)");
        writeln("{");
        writeln("    return PyFloat_AsDouble(item);");
        writeln("}");
        writeln("inline PyObject *setIntFromLong(int64 value)");
        writeln("{");
        writeln("    return PyLong_FromLong(value);");
        writeln("}");
        writeln("inline PyObject *setLongFromLong(int64 value)");
        writeln("{");;
        writeln("    return PyLong_FromLongLong(value);");
        writeln("}");
        writeln("inline PyObject *setFloatFromDouble(double value)");
        writeln("{");
        writeln("    return PyFloat_FromDouble(value);");
        writeln("}");
        writeln("inline PyObject *setString(const char* value)");
        writeln("{");
        writeln("    return PyBytes_FromString(value);");
        writeln("}");
        writeln("#else");
        writeln("inline char* getAsString(PyObject* item)");
        writeln("{");
        writeln("    return PyString_AsString(item);");
        writeln("}");
        writeln("inline int32 getIntAsLong(PyObject* item)");
        writeln("{");
        writeln("    return PyInt_AsLong(item);");
        writeln("}");
        writeln("inline int64 getLongAsLong(PyObject* item)");
        writeln("{");
        writeln("    return PyLong_AsLong(item);");
        writeln("}");
        writeln("inline double getFloatAsDouble(PyObject* item)");
        writeln("{");
        writeln("    return PyFloat_AsDouble(item);");
        writeln("}");
        writeln("inline PyObject *setIntFromLong(int64 value)");
        writeln("{");
        writeln("    return PyInt_FromLong(value);");
        writeln("}");
        writeln("inline PyObject *setLongFromLong(int64 value)");
        writeln("{");
        writeln("    return PyLong_FromLong(value);");
        writeln("}");
        writeln("inline PyObject *setFloatFromDouble(double value)");
        writeln("{");
        writeln("    return PyFloat_FromDouble(value);");
        writeln("}");
        writeln("inline PyObject *setString(const char* value)");
        writeln("{");
        writeln("    return PyString_FromString(value);");
        writeln("}");
        writeln("#endif");
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.code.size() == 0 && prototype.isRpcCall())
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, writer);
            writeln("{");
            generateMethodCall(module, prototype);
            writeln("}");
            writeln();
          }
        }
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + "pycode_stubbed.cpp");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + "pycode_stubbed.cpp");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#if defined(USE_" + module.name.toUpperCase() + "_STUBBED)");
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
        writeln();
        writeln("void T" + module.name + "::SetupPythonInterp(Config *config)");
        writeln("{");
        writeln(1, "throw xIdlException(__FILE__, __LINE__, \"" + module.name + "\", 0, \"Module " + module.name + " SetupPythonInterp marked not used.\");");
        writeln("}");
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.code.size() == 0 && prototype.isRpcCall())
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, writer);
            writeln("{");
            writeln(1, "throw xIdlException(__FILE__, __LINE__, \"" + module.name + "\", 0, \"Module " + module.name + " Prototype " + prototype.name+ " marked not used.\");");
            writeln("}");
            writeln();
          }
        }
        writeln("#endif");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
    {
      System.out.println(e.toString());
      System.out.flush();
      e.printStackTrace();
    }
    return true;
  }
  public PopUbi3Server() {}
  private static PopUbi3Server us = new PopUbi3Server(); 
  private static Vector pythonArgs;
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
        return "PyObject* _item" + no + " = setIntFromLong((int64)" + ptr + field.name + ");";
      case Type.LONG:
        if (field.type.reference == Type.BYPTR)
          ptr = "*";
        return "PyObject* _item" + no + " = setLongFromLong((int64)" + ptr + field.name + ");";
      case Type.FLOAT:
      case Type.DOUBLE:
        if (field.type.reference == Type.BYPTR)
          ptr = "*";
        return "PyObject* _item" + no + " = setFloatFromDouble((double)" + ptr + field.name + ");";
      case Type.CHAR:
      case Type.STRING:
        return "PyObject* _item" + no + " = setString(" + field.name + ");";
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
        return name + " = getIntAsLong(_out" + no + ");";
      case Type.LONG:
        return name + " = getLongAsLong(_out" + no + ");";
      case Type.FLOAT:
      case Type.DOUBLE:
        return name + " = getFloatAsDouble(_out" + no + ");";
      case Type.CHAR:
      case Type.STRING:
        return name + " = getAsString(_out" + no + ");";
      default:
        return "#error not catered for choose another form of output";
    }
  }
  private static void generateMethodCall(Module module, Prototype prototype)
  {
    pythonArgs = new Vector();
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
      writeln(1, "" + prototype.type.cDefUbi("_returnValue = 0;"));
      noOutputs++;
    }
    writeln(1, "pythonModuleAuto _am(\"" + module.name.toUpperCase() + "_CODE\");"); 
    writeln(1, "if (_am.module != 0)");
    writeln(1, "{");
    writeln(2, "PyObject* _function;");
    writeln(2, "PyObject* _args;");
    writeln(2, "PyObject* _result;");
    writeln(2, "_function = PyObject_GetAttrString(_am.module, \"" + prototype.name + "\");");
    writeln(2, "if (_function && PyCallable_Check(_function))");
    writeln(2, "{");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.listType == true)
        writeln(3, "Transfer<" + typeof(arg.field.type) + "> _transfer" + i + ";");
    }
    int inpNo = 1;
    writeln(3, "_args = PyTuple_New(" + noInputs + ");");
    writeln(3, "PyObject* _signature = setIntFromLong(" + prototype.signature(true) + ");");
    writeln(3, "PyTuple_SetItem(_args, 0, _signature);");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isInput)
      {
        String item = "_item" + inpNo;
        String type = typeof(arg.field.type);
        if (arg.listType == true && arg.sizeName.length() == 0)
        {
          writeln(3, "PyObject* " + item + " = PyList_New(" + type + "::NoBuildFields());");
          writeln(3, "_transfer" + i + ".rec = *" + arg.name + ";");
          writeln(3, "_transfer" + i + ".toList(" + item + ");");
          writeln(3, "PyTuple_SetItem(_args, " + inpNo + ", " + item + ");");
        }
        else if (arg.listType == true)
        {
          writeln(3, "PyObject* _list" + inpNo + " = PyList_New(" + arg.sizeName + ");");
          writeln(3, "for (int i = 0; i < " + arg.sizeName + "; i++)");
          writeln(3, "{");
          writeln(4, "_transfer" + i + ".rec = " + arg.name + "[i];");
          writeln(4, "PyObject* " + item + " = PyList_New(" + type + "::NoBuildFields());");
          writeln(4, "_transfer" + i + ".toList(" + item + ");");
          writeln(4, "PyList_SetItem(_list" + inpNo + ", i, " + item + ");");
          writeln(3, "}");
          writeln(3, "PyTuple_SetItem(_args, " + inpNo + ", _list" + inpNo + ");");
        }
        else
        {
          writeln(3, "" + inIntrinsic(arg.field, inpNo));
          writeln(3, "PyTuple_SetItem(_args, " + inpNo + ", " + item + ");");
        }
        inpNo++;
      }
    }
    writeln(3, "_result = PyObject_CallObject(_function, _args);");
    writeln(3, "Py_DECREF(_args);");
    writeln(3, "if (_result != 0)");
    writeln(3, "{");
    int pad = 0;
    if (noOutputs > 1)
    {
      pad = 1;
      writeln(4, "if (PyTuple_Check(_result) == true && PyTuple_Size(_result) == " + noOutputs + ")");
      writeln(4, "{");
    }
    int outNo = 0;
    if (hasResult == true)
    {
      if (noOutputs > 1)
        writeln(4+pad, "PyObject* _out" + outNo + " = PyTuple_GetItem(_result, " + outNo + ");");
      else
        writeln(4+pad, "PyObject* _out" + outNo + " = _result;");
      writeln(4+pad, "" + outIntrinsic("_returnValue", prototype.type, outNo));
      outNo++;
    }
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (noOutputs > 1)
        writeln(4+pad, "PyObject* _out" + outNo + " = PyTuple_GetItem(_result, " + outNo + ");");
      else
        writeln(4+pad, "PyObject* _out" + outNo + " = _result;");
      if (arg.listType == true && arg.sizeName.length() == 0)
      {
        writeln(4+pad, "if (PyList_Check(_out" + outNo + ") == true)");
        writeln(4+pad, "{");
        writeln(5+pad, "_transfer" + i + ".fromList(_out" + outNo + ");");
        writeln(5+pad, "*" + arg.field.name + " = _transfer" + i + ".rec;");
        writeln(4+pad, "}");
      }
      else if (arg.listType == true)
      {
        writeln(4+pad, "if (PyList_Check(_out" + outNo + ") == true)");
        writeln(4+pad, "{");
        writeln(5+pad, "" + arg.field.name + " = (" + typeof(arg.field.type) + "*) calloc(*" + arg.sizeName + ", sizeof(" + typeof(arg.field.type) + "));");
        writeln(5+pad, "for (int i = 0; i < *" + arg.sizeName + "; i++)");
        writeln(5+pad, "{");
        writeln(5+pad, "  PyObject* _inner" + outNo + " = PyList_GetItem(_out" + outNo + ", i);");
        writeln(5+pad, "  _transfer" + i + ".fromList(_inner" + outNo + ");");
        writeln(5+pad, "  " + arg.field.name + "[i] = _transfer" + i + ".rec;");
        writeln(5+pad, "}");
        writeln(4+pad, "}");
      }
      else if (arg.field.type.typeof == Type.CHAR && arg.sizeName.length() > 0)
      {
        if (arg.field.type.reference == Type.BYREFPTR)
        {
          writeln(4+pad, "if (*" + arg.sizeName + " > 0)");
          writeln(4+pad, "{");
          writeln(5+pad, "char *" + outIntrinsic("_temp" + outNo, arg.field.type, outNo));
          writeln(5+pad, "" + arg.field.name + " = (char*) malloc(*" + arg.sizeName + ");");
          writeln(5+pad, "memcpy(" + arg.field.name + ", _temp" + outNo + ", *" + arg.sizeName + ");");
          writeln(4+pad, "}");
        }
      }
      else
        writeln(4+pad, "*" + outIntrinsic(arg.field.name, arg.field.type, outNo));
      outNo++;
    }
    if (noOutputs > 1)
      writeln(4, "}");
    writeln(4, "Py_DECREF(_result);");
    writeln(3, "}");
    writeln(3, "else");
    writeln(3, "{");
    writeln(4, "TBChar errorBuf;");
    writeln(4, "TPythonTraceback::PyErrorHandler(errorBuf);");
    writeln(4, "throw TUbiException(99999, errorBuf.data, __FILE__, __LINE__);");
    writeln(3, "}");
    writeln(3, "Py_XDECREF(_function);");
    writeln(2, "}");
    writeln(1, "}");
    if (hasResult == true)
      writeln(1, "return _returnValue;");
  }
  private static void generatePyHCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + "pycode.h");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + "pycode.h");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("#ifndef " + module.name.toLowerCase() + "pycodeH");
        writeln("#define " + module.name.toLowerCase() + "pycodeH");
        writeln("");
        writeln("#include \"pythonbuild.h\"");
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
        writeln("");
        writeln("#endif");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#include \"machine.h\"");
        writeln("#include \"ubixcept.h\"");
        writeln();
        writeln("#include <stdio.h>");
        writeln("#include <stdlib.h>");
        writeln("#include <string.h>");
        writeln("#include <assert.h>");
        writeln();
        writeln("#include \""+module.name.toLowerCase()+".h\"");
        writeln("T" + module.name + "Handle T" + module.name + "::" + module.name + "Handle;");
        writeln();
        writeln("#define THROW(err) throw TUbiException(" + module.name.toUpperCase() + "_##err, \"\", __FILE__, __LINE__);");
        writeln("#define THROW2(err, str) throw TUbiException(" + module.name.toUpperCase() + "_##err, str, __FILE__, __LINE__);");
        writeln();
        writeln("#ifndef " + module.name.toUpperCase() + "_OVERRIDE_ADDLIST");
        writeln("template <class TYPE, class INDEX>");
        writeln("void AddList(TYPE*& List, INDEX& Index, const TYPE& Rec, const INDEX Delta)");
        writeln("{");
        writeln(1, "if ((List == 0) != (Index == 0))");
        writeln(2, format("THROW2(ADDLIST_ERROR, \"%s Addlist Error List out of sync with Index\");", module.name));
        writeln(1, "if (Index % Delta == 0)");
        writeln(1, "{");
        writeln(2, "TYPE* Resized = (TYPE*) realloc(List, sizeof(Rec)*(Index+Delta));");
        writeln(2, "if (Resized == 0) // Cater for alloc failure");
        writeln(3, format("THROW2(ADDLIST_REALLOC_FAILED, \"%s realloc ran out of memory\");", module.name));
        writeln(2, "List = Resized;");
        writeln(1, "}");
        writeln(1, "List[Index++] = Rec;");
        writeln("}");
        writeln("#endif");
        writeln();
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String) module.code.elementAt(j);
            boolean outputIt = true;
            if (codeLine.indexOf("HEADER:") >= 0)
              outputIt = false;
            else if (codeLine.indexOf("BOTH:") >= 0 || codeLine.indexOf("CODE:") >= 0)
              ;//outputIt = true;
            else if (codeLine.indexOf("#include") >= 0)
              outputIt = false;
            if (firstTime == true)
              firstTime = false;
            if (outputIt == true)
              write(cleanUp(codeLine));
          }
          if (firstTime == false)
            writeln();
        }
        writeln();
        writeln("#ifndef AUTO_PROFILE");
        writeln("#define AUTO_PROFILE(tag)");
        writeln("#endif");
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.code.size() > 0)
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, writer);
            writeln("{");
            writeln(1, "AUTO_PROFILE("+prototype.name+");");
            for (int j = 0; j < prototype.code.size(); j++)
            {
              String codeLine = (String) prototype.code.elementAt(j);
              write(codeLine);
            }
            writeln("}");
            writeln();
          }
        }
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.message.length() > 0)
            writeln("'" + module.name + "', " + toHex(prototype.message) + ", '" + prototype.name + "'");
          else
            writeln("'" + module.name + "', " + toHex(i) + ", '" + prototype.name + "'");
        }
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      File mainfile = new File(output + module.name.toLowerCase() + ".cpp");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + ".cpp");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code is initial minimal code, copy it to another source point if you need to modify it.");
        writeln("#include \"machine.h\"");
        writeln();
        writeln("#include \"ubimain.h\"");
        writeln();
        writeln("#include \"xcept.h\"");
        writeln("#include \"cmdlinearg.h\"");
        writeln("#include \"config.h\"");
        writeln("#include \"logfile.h\"");
        writeln();
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
        writeln();
        writeln("int main(int argc, char **argv)");
        writeln("{");
        writeln(1, "UbiMain *server = 0;");
        writeln(1, "try");
        writeln(1, "{");
        writeln(2, "if (!CmdLineArg::Initialize(argc, argv))");
        writeln(3, "return 1;");
        writeln(2, "server = new UbiMain(new T" + module.name + "Server());");
        writeln(2, "server->Initialize();  ");
        writeln(2, "server->Run();");
        writeln(1, "}");
        writeln(1, "catch (xCept &x)");
        writeln(1, "{");
        writeln(2, "SingletonLogFile::LogFile()->lprintf(eLogError, \"Exception caught in main\");");
        writeln(2, "SingletonLogFile::LogFile()->Log(x);");
        writeln(2, "if (server) { delete server; server = 0; }");
        writeln(2, "return x.Error();");
        writeln(1, "}");
        writeln(1, "catch (...)");
        writeln(1, "{");
        writeln(2, "SingletonLogFile::LogFile()->lprintf(eLogError, \"Unknown exception caught in main\");");
        writeln(2, "if (server) { delete server; server = 0; }");
        writeln(2, "throw;");
        writeln(1, "}");
        writeln(1, "if (server) { delete server; server = 0; }");
        writeln(1, "return 0;");
        writeln("}");
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#include \"machine.h\"");
        writeln("#include <stdio.h>");
        writeln("#include <stdlib.h>");
        writeln("#include <string.h>");
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
        writeln();
        writeln("const char *"+module.name+"Version = "+module.version+";");
        writeln("int32 "+module.name+"Signature = "+module.signature+";");
        writeln();
        if (alignForSun == true)
        {
          writeln("inline int32 alignData(int32 size)");
          writeln("{");
          writeln(1, "int n = size % 8;");
          writeln(1, "if (n > 0) size += (8-n);");
          writeln(1, "return size;");
          writeln("}");
          writeln();
        }
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          String comma = "  ";
          writeln("char *"+module.name+table.name+"[] = ");
          writeln("{");
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            writeln(comma+message.value+"   // "+message.name);
            comma = ", ";
          }
          writeln("};");
          writeln();
        }
        writeln("T"+module.name+"Server::T"+module.name+"Server()");
        writeln("{");
        writeln(1, "" + module.name + " = new T" + module.name + "();");
        for (int j = 0; j < module.pragmas.size(); j++)
        {
          String pragma = (String)module.pragmas.elementAt(j);
          int n = pragma.indexOf("CACHE:");
          if (n < 0) 
            continue;
          pragma = pragma.substring(n+6).trim();
          writeln(1, "" + pragma + "::Instance()->Setup();");
          break;
        }
        writeln("}");
        writeln();
        writeln("T"+module.name+"Server::~T"+module.name+"Server()");
        writeln("{");
        if (hasShutDownCode == true)
          writeln(1, "" + module.name + "->ShutDownCode();");
        writeln("}");
        writeln();
        writeln("void T" + module.name + "Server::Service(const UbiData *input, UbiData *output)");
        writeln("{");
        writeln("#ifdef __LOGFILE_H_");
        writeln("#define IDL2_UBI_LOG(x) x");
        writeln("#else");
        writeln("#define IDL2_UBI_LOG(x)");
        writeln("#endif");
        writeln(1, "char* ip = (char*)input->data;");
        writeln(1, "int32 signature;");
        writeln(1, "signature = *(int32*)ip;");
        writeln(1, "SwapBytes(signature);");
        if (alignForSun == true)
          writeln(1, "ip += alignData(sizeof(int32));");
        else
          writeln(1, "ip += sizeof(int32);");
        writeln(1, "switch (input->reqID)");
        writeln(1, "{");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.code.size() > 0)
            hasCode = true;
          if (prototype.message.length() > 0)
            writeln(1, "case "+toHex(prototype.message)+":");
          else
            writeln(1, "case "+toHex(i)+":");
          writeln(2, "IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"reqID=%d " + prototype.name + "[" + prototype.signature(true) + "]\", input->reqID)));");
          writeln(2, "if (signature != " + toHex((int)prototype.signature(true)) + ")");
          writeln(3, "result = "+module.name.toUpperCase()+"_INV_SIGNATURE;");
          writeln(2, "else");
          writeln(3, "result = "+prototype.name+"(ip);");
          writeln(2, "IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"result=%d " + prototype.name + "[" + prototype.signature(true) + "]\", result)));");
          writeln(2, "break;");
        }
        writeln(1, "default:");
        writeln(2, "IDL2_UBI_LOG((" + module.name + "->logFile->lprintf(eLogDebug, \"reqID=%d *** Unknown Message ***\", input->reqID)));");
        writeln(2, "result = " + module.name.toUpperCase() + "_UNKNOWN_FUNCTION;");
        writeln(2, "return;");
        writeln(1, "}");
        writeln(1, "output->Use((unsigned char *)replyBody, replySize, result);");
        writeln("}");
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCServer(module, prototype, i);
        }
        writeln();
      }
      finally
      {
        writer.flush();
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
    catch (Throwable e)
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
  private static void generateCServer(Module module, Prototype prototype, int no)
  {
    writeln("e" + module.name + " T" + module.name + "Server::" + prototype.name + "(char *ip)");
    writeln("{");
    writeln(1, "e"+module.name+" resultCode = "+module.name.toUpperCase()+"_OK;");
    writeln(1, "try");
    writeln(1, "{");
    writeln(2, "replySize = 0;replyBody = 0;");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      boolean addUp = true;
      if (i+1 == prototype.inputs.size())
        addUp = false;
      Action input = (Action) prototype.inputs.elementAt(i);
      Field field  = input.getParameter(prototype);
      if (field == null)
        continue;
      byte savedReference = field.type.reference;
      if (field.type.reference == Type.BYPTR
      ||  field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            writeln(2, "recvSize = *(int32*)ip;");
            writeln(2, "SwapBytes(recvSize);");
            if (alignForSun == true)
              writeln(2, "ip += alignData(sizeof(int32));");
            else
              writeln(2, "ip += sizeof(int32);");
            writeln(2, "char* "+field.name+" = ip;");
            if (addUp)
            {
              if (alignForSun == true)
                writeln(2, "ip += alignData(recvSize);");
              else
                writeln(2, "ip += recvSize;");
            }
          }
          else
          {
            writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                writeln(2, "ip += alignData(sizeof(*"+field.name+"));");
              else
                writeln(2, "ip += sizeof(*"+field.name+");");
            }
          }
        }
        else
        {
          writeln(2, "recvSize = *(int32*)ip;");
          writeln(2, "SwapBytes(recvSize);");
          if (alignForSun == true)
            writeln(2, "ip += alignData(sizeof(int32));");
          else
            writeln(2, "ip += sizeof(int32);");
          writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              writeln(2, "ip += alignData(recvSize);");
            else
              writeln(2, "ip += recvSize;");
          }
        }
      }
      else
      {
        writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            writeln(2, "ip += alignData(sizeof(*"+field.name+"));");
          else
            writeln(2, "ip += sizeof(*"+field.name+");");
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
      generateCSwaps(module, prototype, field, inOp);
      if (inOp != null && opField0 != null)
        opField0.type.reference = saveRef0;
      field.type.reference = savedReference;
    }
    boolean hasOutput = false;
    boolean hasresult = false;
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        if (alignForSun == true)
          writeln(2, "replySize += alignData((int32)sizeof("+prototype.type.cNameUbi()+")); // result Size");
        else
          writeln(2, "replySize += (int32)sizeof("+prototype.type.cNameUbi()+"); // result Size");
        hasOutput = true;
        hasresult = true;
      }
    }
    int Variants = 0;
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action output = (Action) prototype.outputs.elementAt(i);
      Field field  = output.getParameter(prototype);
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
            writeln("#error unsized char* on output");
            errLog.println("#error unsized char* on output");
          }
          else
          {
            if (alignForSun == true)
              writeln(2, "replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            else
              writeln(2, "replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          if (alignForSun == true)
            writeln(2, "replySize += alignData(sizeof(int32));  // size of Block");
          else
            writeln(2, "replySize += sizeof(int32);  // size of Block");
          if (alignForSun == true)
            writeln(2, "replySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
          else
            writeln(2, "replySize += ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
        }
      }
      else if (field.type.reference == Type.BYREFPTR)
      {
        if (output.hasSize() == false)
        {
          writeln("#error unsized BYREFPTR on output");
          errLog.println("#error unsized BYREFPTR on output");
        }
        else
        {
          Variants++;
          if (alignForSun == true)
            writeln(2, "replySize += alignData(sizeof("+field.type.cNameUbi()+"*)); // Variant "+field.name);
          else
            writeln(2, "replySize += sizeof("+field.type.cNameUbi()+"*); // Variant "+field.name);
        }
      }
      else
      {
        if (alignForSun == true)
          writeln(2, "replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
        else
          writeln(2, "replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
      }
    }
    if (hasOutput)
    {
      if (Variants > 0)
      {
        writeln(2, "int32 Variants["+Variants+"];");
        writeln(2, "int32 VariantSize["+Variants+"];");
        Variants = 0;
      }
      writeln(2, "replyBody = new char [replySize];");
      writeln(2, "memset(replyBody, 0, replySize);");
      writeln(2, "ip = replyBody;");
    }
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        writeln(2, ""+prototype.type.cNameUbi()+"* result = ("+prototype.type.cNameUbi()+"*) ip;");
        if (prototype.outputs.size() > 0)
        {
          if (alignForSun == true)
            writeln(2, "ip += alignData(sizeof("+prototype.type.cNameUbi()+"));");
          else
            writeln(2, "ip += sizeof("+prototype.type.cNameUbi()+");");
        }
      }
    }
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      boolean addUp = true;
      if (i+1 == prototype.outputs.size())
        addUp = false;
      Action output = (Action) prototype.outputs.elementAt(i);
      Field field  = output.getParameter(prototype);
      if (field == null)
        continue;
      Operation op = output.sizeOperation();
      Action input = (Action) prototype.getInputAction(field.name);
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
              writeln(2, "memcpy(ip, "+field.name+", sizeof("+field.type.cNameUbi()+"));");
              writeln(2, ""+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            }
            else
              writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                writeln(2, "ip += alignData(sizeof("+field.type.cNameUbi()+"));");
              else
                writeln(2, "ip += sizeof("+field.type.cNameUbi()+");");
            }
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          writeln(2, "*(int32*)ip = ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+"));");
          writeln(2, "SwapBytes(*(int32*)ip);");
          if (alignForSun == true)
            writeln(2, "ip += alignData(sizeof(int32));");
          else
            writeln(2, "ip += sizeof(int32);");
          if (input != null)
          {
            writeln(2, "memcpy(ip, "+field.name+", ("+w1+op.name+" * sizeof("+field.type.cNameUbi()+")));");
            writeln(2, ""+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
          }
          else
            writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              writeln(2, "ip += alignData("+w1+op.name+" * sizeof("+field.type.cNameUbi()+"));");
            else
              writeln(2, "ip += ("+w1+op.name+" * sizeof("+field.type.cNameUbi()+"));");
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
          } else
            writeln("#error Constant Size with BYREFPTR output");
          writeln(2, "Variants["+(Variants++)+"] = (int32)(ip-replyBody);");
          if (input != null)
            writeln(2, "("+field.type.cNameUbi()+"*)*ip = "+field.name+";");
          else
            writeln(2, ""+field.type.cNameUbi()+"** "+field.name+" = ("+field.type.cNameUbi()+"**)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              writeln(2, "ip += alignData(sizeof("+field.type.cNameUbi()+"*));");
            else
              writeln(2, "ip += sizeof("+field.type.cNameUbi()+"*);");
          }
        }
      }
      else
      {
        if (input != null)
        {
          writeln(2, "memcpy(ip, "+field.name+", sizeof("+field.type.cNameUbi()+"));");
          writeln(2, ""+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        }
        else
          writeln(2, ""+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            writeln(2, "ip += alignData(sizeof("+field.type.cNameUbi()+"));");
          else
            writeln(2, "ip += sizeof("+field.type.cNameUbi()+");");
        }
      }
    }
    writeln(2, "try");
    writeln(2, "{");
    String w1 = "", w2 = "";
    write(3, "");
    if (hasresult)
      write("*result = ");
    write(module.name+"->"+prototype.name+"(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field) prototype.parameters.elementAt(i);
      if (field.type.reference == Type.BYPTR)
        w2 = "";
      else
        w2 = "*";
      write(w1 + w2 + field.name);
      w1 = ", ";
    }
    writeln(");");
    writeln(2, "}");
    writeln(2, "catch (e"+module.name+" rc)");
    writeln(2, "{");
    writeln(3, "resultCode = rc;");
    writeln(2, "}");
    if (hasresult && prototype.needsSwap())
      writeln(2, "SwapBytes(*result);");
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action output = (Action) prototype.outputs.elementAt(i);
      Field field  = output.getParameter(prototype);
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
      generateCStructSwaps(module, prototype, field, op);
      if (op != null && opField != null)
        opField.type.reference = saveRef;
    }
    if (Variants > 0)
    {
      writeln(2, "// We are going to setup a new replyBody because of Variants");
      writeln(2, "char *oldreplyBody = replyBody;");
      writeln(2, "ip = replyBody;");
      writeln(2, "int32 tail = replySize;");
      writeln(2, "// Calc new replySize for Contiguous memory from Disjoint memory");
      writeln(2, "replySize = 0;");
      if (prototype.type.typeof != Type.VOID
      ||  prototype.type.reference == Type.BYPTR)
      {
        if (prototype.type.reference != Type.BYPTR)
        {
          if (alignForSun == true)
            writeln(2, "replySize += alignData((int32)sizeof("+prototype.type.cNameUbi()+")); // result Size");
          else
            writeln(2, "replySize += (int32)sizeof("+prototype.type.cNameUbi()+"); // result Size");
          hasOutput = true;
          hasresult = true;
        }
      }
      int VariantSize = 0;
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
        if (field == null)
          continue;
        hasOutput = true;
        Operation op = output.sizeOperation();
        if (field.type.reference == Type.BYPTR
        ||  field.type.reference == Type.BYREFPTR)
        {
          if (output.hasSize() == false)
          {
            if (field.type.typeof != Type.CHAR)
            {
              if (alignForSun == true)
                writeln(2, "replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
              else
                writeln(2, "replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
            }
          }
          else
          {
            w1 = "";
            if (op.isConstant == false)
              w1 = "*";
            if (alignForSun == true)
            {
              writeln(2, "replySize += alignData(sizeof(int32));  // size of Block");
              writeln(2, "replySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            }
            else
            {
              writeln(2, "replySize += sizeof(int32);  // size of Block");
              writeln(2, "replySize += ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            }
            if (field.type.reference == Type.BYREFPTR)
              writeln(2, "VariantSize["+(VariantSize++)+"] = ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+"));");
          }
        }
        else
        {
          if (alignForSun == true)
            writeln(2, "replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
          else
            writeln(2, "replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
        }
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
        if (field == null)
          continue;
        Operation op = output.sizeOperation();
        generateCNonStructSwaps(module, prototype, field, op);
      }
      writeln(2, "replyBody = new char [replySize];");
      writeln(2, "memset(replyBody, 0, replySize);");
      writeln(2, "char *op = replyBody;");
      writeln(2, "int32 pos = 0;");
      writeln(2, "{");
      writeln(3, "for (int i = 0; i < "+Variants+"; i++)");
      writeln(3, "{");
      writeln(4, "int32 size = Variants[i]-pos;");
      writeln(4, "if (size > 0)");
      writeln(4, "{");
      writeln(5, "memcpy(op, ip, size);");
      if (alignForSun == true)
      {
        writeln(5, "ip += alignData(size);");
        writeln(5, "op += alignData(size);");
        writeln(5, "tail -= alignData(size);");
      }
      else
      {
        writeln(5, "ip += size;");
        writeln(5, "op += size;");
        writeln(5, "tail -= size;");
      }
      writeln(4, "}");
      writeln(4, "memcpy(op, &VariantSize[i], sizeof(VariantSize[i])); ");
      writeln(4, "SwapBytes(*(int32*)op);");
      if (alignForSun == true)
        writeln(4, "op += alignData(sizeof(VariantSize[i]));");
      else
        writeln(4, "op += sizeof(VariantSize[i]);");
      writeln(4, "if (VariantSize[i] > 0)");
      writeln(4, "{");
      writeln(5, "char **block = (char **)ip;");
      writeln(5, "memcpy(op, *block, VariantSize[i]);");
      writeln(5, "free(*block);");
      writeln(4, "}");
      if (alignForSun == true)
      {
        writeln(4, "op += alignData(VariantSize[i]);");
        writeln(4, "ip += alignData(sizeof(char *));");
        writeln(4, "tail -= alignData(sizeof(char *));");
      }
      else
      {
        writeln(4, "op += VariantSize[i];");
        writeln(4, "ip += sizeof(char *);");
        writeln(4, "tail -= sizeof(char *);");
      }
      if (alignForSun == true)
        writeln(4, "pos = alignData(Variants[i])+alignData(sizeof(char *));");
      else
        writeln(4, "pos = Variants[i]+sizeof(char *);");
      writeln(3, "}");
      writeln(2, "}");
      writeln(2, "if (tail > 0)");
      writeln(3, "memcpy(op, ip, tail);");
      writeln(2, "delete [] oldreplyBody;");
    }
    else
    {
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
        if (field == null)
          continue;
        Operation op = output.sizeOperation();
        generateCNonStructSwaps(module, prototype, field, op);
      }
    }
    writeln(1, "}");
    writeln(1, "catch (e"+module.name+" rc)");
    writeln(1, "{");
    writeln(2, "return rc;");
    writeln(1, "}");
    writeln(1, "return resultCode;"); 
    writeln("}");
    writeln();
  }
  /**
  * Generates the prototypes defined
  */
  public static void generateCSwaps(Module module, Prototype prototype, Field field, Operation op)
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
        writeln(2, ""+w2+w8+"for (int i = 0; i < "+w5+op.name+"; i++)");
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
        writeln(2, ""+w2+"for (int i" + j + " = 0; i" + j
                        + " < " + integer.intValue()
                        + "; i" + j + "++)");
        w1 = w1 + "[i" + j + "]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
      }
    }
    if (field.isStruct(module))
      writeln(2, ""+w2+w6+field.name+w7+w1+w3+"Swaps();"+w9);
    else if (field.needsSwap())
      writeln(2, ""+w2+"SwapBytes("+w4+field.name+w1+");"+w9);
    else if (field.type.typeof == Type.USERTYPE)
    {
      writeln("#ifndef ALLOW_"+field.type.name.toUpperCase());
      writeln("#error "+prototype.name+" "+field.name+" "+field.type.name+" is of UserType and may require swapping.");
      writeln("#endif");
      errLog.println("Warning: "+prototype.name+" "+field.name+" is of UserType and may require swapping.");
    }
  }
  public static void generateCStructSwaps(Module module, Prototype prototype, Field field, Operation op)
  {
    if (field.isStruct(module) == true)
      generateCSwaps(module, prototype, field, op);
  }
  public static void generateCNonStructSwaps(Module module, Prototype prototype, Field field, Operation op)
  {
    if (field.isStruct(module) == false)
      generateCSwaps(module, prototype, field, op);
  }
}

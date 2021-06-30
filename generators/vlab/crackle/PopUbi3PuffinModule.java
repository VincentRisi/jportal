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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbi3PuffinModule extends Generator
{
  public static String description()
  {
    return "Generates Python3 Module Code";
  }
  public static String documentation()
  {
    return "Generates Python3 Module Code";
  }
  private static boolean qualifyMethod = true;
  /**
  * Reads input from stored repository
  */
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
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
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    indent_size = 4;
    outLog.println(module.name+" version "+module.version);
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      pragma = pragma.trim();
      if (pragma.charAt(0) == '\"' || pragma.charAt(0) == '\'')
        pragma = pragma.substring(1, pragma.length() - 1);
      if (pragma.equalsIgnoreCase("DontQualify") == true)
        qualifyMethod = false;
    }
    generateCClient(module, output, outLog);
    generatePyClient(module, output, outLog);
  }
  public static void generateCClient(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + module.name.toLowerCase() + "python.h");
      OutputStream outFile = new FileOutputStream(output + module.name.toLowerCase() + "python.h");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("// Mutilation, Spindlization and Bending will result in ...");
        writeln("#ifndef _" + module.name + "PYTHON3_H_");
        writeln("#define _" + module.name + "PYTHON3_H_");
        writeln("#include \"machine.h\"");
        writeln("#include \"logfile.h\"");
        writeln();
        writeln("#ifdef _DEBUG");
        writeln(1, "#define _DEBUG_SAVE_ _DEBUG");
        writeln(1, "#undef _DEBUG");
        writeln("#endif");
        writeln();
        writeln("#include \"Python.h\"");
        writeln();
        writeln("#ifdef inline");
        writeln(1, "#undef  inline");
        writeln(1, "#define inline inline");
        writeln("#endif");
        writeln();
        writeln("#ifdef _DEBUG_SAVE_");
        writeln(1, "#define _DEBUG _DEBUG_SAVE_");
        writeln(1, "#undef _DEBUG_SAVE_");
        writeln("#endif");
        writeln();
        writeln("#ifndef OS_DECLSPEC");
        writeln(1, "#define OS_DECLSPEC \"\"");
        writeln("#endif");
        writeln();
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
        writeln();
        writeln("T" + module.name + "* " + module.name + "GetModule();");
        writeln("UbiBaseVars* " + module.name + "Initialise(const char *connectString, TJConnector* share, const char* logfile, eLevel logLevel=eLogDebug, bool display=true, bool rotate=false);");
        writeln("int " + module.name + "CallMethod(const char* name, const char* data, TBChar &value);");
        writeln();
        writeln("#endif");
      }
      finally
      {
        writer.flush();
        outFile.close();
      }
      outLog.println("Code: " + output + module.name.toLowerCase() + "Python.cpp");
      outFile = new FileOutputStream(output+module.name.toLowerCase()+"Python.cpp");
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("#include \"machine.h\"");
        writeln("#include \"xcept.h\"");
        writeln();
        writeln("#include <stdio.h>");
        writeln("#include <stdlib.h>");
        writeln();
        writeln("#include \"handles.h\"");
        writeln("#include \"tbuffer.h\"");
        writeln("#include \"swapbytes.h\"");
        writeln("#include \"xstring.h\"");
        writeln();
        writeln("#include <math.h>");
        writeln();
        writeln("#ifdef _DEBUG");
        writeln(1, "#define _DEBUG_SAVE_ _DEBUG");
        writeln(1, "#undef _DEBUG");
        writeln("#endif");
        writeln();
        writeln("#include \"Python.h\"");
        writeln("#include \"pythonrun.h\"");
        writeln();
        writeln("#ifdef inline");
        writeln(1, "#undef  inline");
        writeln(1, "#define inline inline");
        writeln("#endif");
        writeln();
        writeln("#ifdef _DEBUG_SAVE_");
        writeln(1, "#define _DEBUG _DEBUG_SAVE_");
        writeln(1, "#undef _DEBUG_SAVE_");
        writeln("#endif");
        writeln();
        writeln("#include \"pythonbuild.h\"");
        writeln("#include \"" + module.name.toLowerCase() + "python.h\"");
        writeln();
        writeln("static T" + module.name + "* " + module.name.toLowerCase() + ";");
        writeln();
        writeln("extern \"C\" OS_DECLSPEC T" + module.name + "* " + module.name.toLowerCase() + "_get_module()");
        writeln("{");
        writeln(1, "return " + module.name.toLowerCase() + ";");
        writeln("}");
        writeln();
        writeln("T" + module.name + "* " + module.name + "GetModule()");
        writeln("{");
        writeln(1, "return " + module.name.toLowerCase() + "_get_module();");
        writeln("}");
        writeln();
        writeln("extern \"C\" OS_DECLSPEC UbiBaseVars* " + module.name.toLowerCase() + "_initialise(const char *connectString, TJConnector* share,");
        writeln(1, "const char* logfile, eLevel logLevel=eLogDebug, bool display=true, bool rotate=false)");
        writeln("{");
        writeln(1, "try");
        writeln(1, "{");
        writeln(2, "if (" + module.name.toLowerCase() + " != 0)");
        writeln(2, "{");
        writeln(3, "" + module.name.toLowerCase() + "->connect->Logoff();");
        writeln(3, "delete " + module.name.toLowerCase() + "->connect;");
        writeln(3, "delete " + module.name.toLowerCase() + ";");
        writeln(2, "}");
        writeln(2, "" + module.name.toLowerCase() + " = new T" + module.name + "();");
        writeln(2, "SingletonLogFile::Initialize(logfile, logLevel, display, rotate);");
        writeln(2, "" + module.name.toLowerCase() + "->logFile = SingletonLogFile::LogFile();");
        writeln(2, "if (connectString != 0 && connectString[0] != 0)");
        writeln(2, "{");
        writeln(3, "char schema[256], server[256], user[256];");
        writeln(3, "char *p = (char*) connectString;");
        writeln(3, "char *q = strchr(p, ':');");
        writeln(3, "if (q == 0)");
        writeln(3, "{");
        writeln(4, "" + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 1st ':'\");");
        writeln(4, "return 0;");
        writeln(3, "}");
        writeln(3, "strncpyz(server, p, min((int)(q-p),255));");
        writeln(3, "p = q+1;");
        writeln(3, "q = strchr(p, ':');");
        writeln(3, "if (q == 0)");
        writeln(3, "{");
        writeln(4, "" + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 2nd ':'\");");
        writeln(4, "return 0;");
        writeln(3, "}");
        writeln(3, "strncpyz(schema, p, min((int)(q-p),255));");
        writeln(3, "p = q+1;");
        writeln(3, "q = strchr(p, ':');");
        writeln(3, "if (q == 0)");
        writeln(3, "{");
        writeln(4, "" + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 3rd ':'\");");
        writeln(4, "return 0;");
        writeln(3, "}");
        writeln(3, "strncpyz(user, p, min((int)(q-p),255));");
        writeln(3, "p = q+1;");
        writeln(3, "if (share != 0)");
        writeln(3, "{");
        writeln(4, "" + module.name.toLowerCase() + "->connect = TJConnector::Coordinated();");
        writeln(4, "" + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() created\");");
        writeln(4, "" + module.name.toLowerCase() + "->connect->Logon(user, p, server, schema, &share->cliEnv);");
        writeln(3, "}");
        writeln(3, "else");
        writeln(3, "{");
        writeln(4, "" + module.name.toLowerCase() + "->connect = new TJConnector();");
        writeln(4, "" + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() created\");");
        writeln(4, "" + module.name.toLowerCase() + "->connect->Logon(user, p, server, schema);");
        writeln(3, "}");
        writeln(3, "" + module.name.toLowerCase() + "->logFile->Log(\"" + module.name + " Logon done\");");
        writeln(2, "}");
        writeln(2, "else");
        writeln(2, "{");
        writeln(3, "" + module.name.toLowerCase() + "->connect = TJConnector::Coordinated();");
        writeln(3, "" + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() copy created\");");
        writeln(3, "" + module.name.toLowerCase() + "->connect->CopyOf(*share);");
        writeln(3, "" + module.name.toLowerCase() + "->logFile->Log(\"" + module.name + " Copy done\");");
        writeln(2, "}");
        writeln(1, "}");
        writeln(1, "catch (xCept &ex)");
        writeln(1, "{");
        writeln(2, "" + module.name.toLowerCase() + "->logFile->Log(ex);");
        writeln(2, "return 0;");
        writeln(1, "}");
        for (int i = 0; i < module.pragmas.size(); i++)
        {
          String pragma = (String)module.pragmas.elementAt(i);
          int n = pragma.indexOf("CACHE:");
          if (n < 0)
            continue;
          pragma = pragma.substring(n + 6).trim();
          writeln(1, "" + pragma + "::Instance()->Setup(" + module.name.toLowerCase() + "->connect);");
          break;
        }
        writeln(1, "return " + module.name.toLowerCase() + ";");
        writeln("}");
        writeln();
        writeln("UbiBaseVars* " + module.name + "Initialise(const char *connectString, TJConnector* share,");
        writeln(1, "const char* logfile, eLevel logLevel, bool display, bool rotate)");
        writeln("{");
        writeln(1, "return " + module.name.toLowerCase() + "_initialise(connectString, share, logfile, logLevel, display, rotate);");
        writeln("}");
        writeln();
        writeln("static PyObject *__callback_method__;");
        writeln();
        writeln("#if PY_MAJOR_VERSION == 3");
        writeln("inline char* getString(PyObject* item)");
        writeln("{");
        writeln("    PyObject* l1 = PyUnicode_AsLatin1String(item);");
        writeln("    char* value = PyBytes_AsString(l1);");
        writeln("    Py_DECREF(l1);");
        writeln("    return value;");
        writeln("}");
        writeln("#endif");
        writeln();
        writeln("extern \"C\" OS_DECLSPEC int " + module.name.toLowerCase() + "_call_method(const char* name, const char* data, TBChar &value)");
        writeln("{");
        writeln(1, "if (__callback_method__ == 0)");
        writeln(2, "return 0;");
        writeln(1, "PyObject *args;");
        writeln(1, "PyObject *result;");
        writeln(1, "args = Py_BuildValue(\"ss\", name, data);");
        writeln(1, "result = PyEval_CallObject(__callback_method__, args);");
        writeln(1, "Py_DECREF(args);");
        writeln(1, "if (result == 0)");
        writeln(2, "return 0;");
        writeln("#if PY_MAJOR_VERSION == 3");
        writeln(1, "value.set(getString(result)); ");
        writeln("#else");
        writeln(1, "value.set(PyString_AsString(result)); ");
        writeln("#endif");
        writeln(1, "Py_DECREF(result);");
        writeln(1, "return value.size;");
        writeln("}");
        writeln();
        writeln("int " + module.name + "CallMethod(const char* name, const char* data, TBChar &value)");
        writeln("{");
        writeln(1, "return " + module.name.toLowerCase() + "_call_method(name, data, value);");
        writeln("}");
        writeln();
        writeln("typedef PyObject* (*self_args_ptr)(PyObject *self, PyObject *args);");
        writeln();
        writeln("static PyObject* _error_check(self_args_ptr function, PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "TBChar errBuffer;");
        writeln(1, "try");
        writeln(1, "{");
        writeln(2, "return function(self, args);");
        writeln(1, "}");
        writeln(1, "catch (xDBException &ex1)");
        writeln(1, "{");
        writeln(2, "errBuffer.append(\"DBException: \");");
        writeln(2, "errBuffer.append(ex1.ErrorStr());");
        writeln(2, "return Py_BuildValue(\"si\", errBuffer.data, -1);");
        writeln(1, "}");
        writeln(1, "catch (xCept &ex2)");
        writeln(1, "{");
        writeln(2, "errBuffer.append(\"Exception: \");");
        writeln(2, "errBuffer.append(ex2.ErrorStr());");
        writeln(2, "return Py_BuildValue(\"si\", errBuffer.data, -1);");
        writeln(1, "}");
        writeln(1, "catch (...)");
        writeln(1, "{");
        writeln(2, "errBuffer.append(\"Catch ...\");");
        writeln(2, "return Py_BuildValue(\"si\", errBuffer.data, -1);");
        writeln(1, "}");
        writeln("}");
        writeln();
        writeln("static PyObject* _DO__init__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "char* connectString;");
        writeln(1, "char* logfile;");
        writeln(1, "int result = 0;");
        writeln(1, "if (!PyArg_ParseTuple(args, \"ss\", &connectString, &logfile))");
        writeln(2, "return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        writeln(1, "// a nasty upcast - shudder");
        writeln(1, "T" + module.name + "* module = (T" + module.name + "*)" + module.name.toLowerCase() + "_initialise(connectString, 0, logfile);");
        writeln(1, "if (module != 0)");
        writeln(2, "result = module->instance;");
        writeln(1, "return Py_BuildValue(\"isi\", result, \"OK\", 0);");
        writeln("}");
        writeln();
        writeln("static PyObject* __init__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "return _error_check(_DO__init__, self, args);");
        writeln("}");
        writeln();
        writeln("static PyObject* _DO__set_callback__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "PyObject *object;");
        writeln(1, "if (!PyArg_ParseTuple(args, \"O:__set_callback__\", &object))");
        writeln(2, "return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        writeln(1, "if (!PyCallable_Check(object)) ");
        writeln(2, "return Py_BuildValue(\"si\", \"Parameter must be callable\", -1);");
        writeln(1, "Py_XINCREF(object);");
        writeln(1, "Py_XDECREF(__callback_method__);  // Dispose of previous callback");
        writeln(1, "__callback_method__ = object;     // Remember new callback ");
        writeln(1, "return Py_BuildValue(\"si\", \"OK\", 0);");
        writeln("}");
        writeln();
        writeln("static PyObject* __set_callback__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "return _error_check(_DO__set_callback__, self, args);");
        writeln("}");
        writeln();
        writeln("static PyObject* _DO__putenv__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "char *envstr;");
        writeln(1, "if (!PyArg_ParseTuple(args, \"s\", &envstr))");
        writeln(2, "return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        writeln(1, "int rc = putenv(envstr);");
        writeln(1, "return Py_BuildValue(\"si\", \"Check rc=0 worked else error\", rc);");
        writeln("}");
        writeln();
        writeln("static PyObject* __putenv__(PyObject *self, PyObject *args)");
        writeln("{");
        writeln(1, "return _error_check(_DO__putenv__, self, args);");
        writeln("}");
        writeln();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
          {
            generateDoxygenComment(module, prototype, outLog);
            generateCClientImp(module, prototype);
          }
        }
        generateMethodsDef(module);
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
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  public static void generatePyClient(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String outname = output + module.name.toLowerCase() + ".py";
      outLog.println("Code: " + outname);
      OutputStream outFile = new FileOutputStream(outname);
      writer = new PrintWriter(outFile);
      try
      {
        generateMethodsCall(module, outLog);
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
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  public static String pyArgs(Field field)
  {
    String result = "";
    String initializer = "";
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
        result = "int32";
        initializer = " = 0";
        break;
      case Type.BYTE:
        result = "int8";
        initializer = " = 0";
        break;
      case Type.CHAR:
        if (field.type.reference == Type.BYREFPTR)
          result = "REFPTR<char>";
        else
          result = "char*";
        break;
      case Type.SHORT:
        result = "int16";
        initializer = " = 0";
        break;
      case Type.INT:
        result = "int32";
        initializer = " = 0";
        break;
      case Type.LONG:
        result = "int64";
        initializer = " = 0";
        break;
      case Type.FLOAT:
      case Type.DOUBLE:
        result = "double";
        initializer = " = 0.0";
        break;
      default:
        if (field.type.reference == Type.BYREFPTR)
          result = "TransferList<" + field.type.name + ">";
        else if (field.hasSize == true)
          result = "TransferList<" + field.type.name + ">";
        else
          result = "Transfer<" + field.type.name + ">";
        break;
    }
    result = result + " " + field.name + initializer;
    return result;
  }
  public static String pyInput(Field field)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
        return "b";
      case Type.CHAR:
        if (field.hasSize == true)
          return "s#";
        return "s";
      case Type.SHORT:
        return "h";
      case Type.INT:
        return "i";
      case Type.LONG:
        return "L";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "d";
      default:
        return "O!";
    }
  }
  //static int listNo, listNoOutput;
  static Vector pythonLists;
  public static String pyInputParm(Field field)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return "&" + field.name;
      case Type.CHAR:
        {
          String sizer = "";
          String refer = "&";
          if (field.hasSize == true)
          {
            for (int i=0; i<field.input.operations.size(); i++)
            {
              Operation op = (Operation)field.input.operations.elementAt(i);
              if (op.code == Operation.SIZE && op.isConstant == true)
              {
                refer = "";
                break;
              }
            }
            sizer = ", " + refer + field.input.getSizeName();
          }
          return "&" + field.name + sizer;
        }
      default:
        Python3ListPM list = new Python3ListPM();
        list.name = "list" + pythonLists.size();
        list.field = field;
        list.isInput = true;
        pythonLists.addElement(list);
        writeln(1, "PyObject* " + list.name + ";");
        return "&PyList_Type, &" + list.name;
    }
  }
  public static boolean pyList(Field field)
  {
    switch (field.type.typeof)
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
  public static String pyOutput(Field field)
  {
    Type type = field.type;
    switch (type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
        return "b";
      case Type.CHAR:
        if (field.hasSize && field.output != null)
          return "s#";
        return "s";
      case Type.SHORT:
        return "h";
      case Type.INT:
        return "i";
      case Type.LONG:
        return "L";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "d";
      default:
        return "O";
    }
  }
  public static String pyOutputParm(Field field, String sizeName)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return field.name;
      case Type.CHAR:
        {
          String sizer = "";
          if (field.hasSize && field.output != null)
            sizer = ", " + field.output.getSizeName();
          if (field.type.reference == Type.BYREFPTR)
            return field.name + ".data" + sizer;
          else
            return field.name + sizer;
        }
      default:
        for (int i = 0; i < pythonLists.size(); i++)
        {
          Python3ListPM list = (Python3ListPM)pythonLists.elementAt(i);
          if (field == list.field)
          {
            list.isOutput = true;
            return list.name;
          }
        }
        Python3ListPM list = new Python3ListPM();
        list.name = "list" + pythonLists.size();
        list.sizeName = sizeName;
        list.field = field;
        list.isOutput = true;
        pythonLists.addElement(list);
        if (sizeName.length() > 0)
          writeln(1, "PyObject* " + list.name + " = PyList_New(" + sizeName + ");");
        else
          writeln(1, "PyObject* " + list.name + " = PyList_New("+field.name+".build.count);");
        return list.name;
    }
  }
  public static String pyReturn(Type type)
  {
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        return "int16";
      case Type.BYTE:
        return "int8";
      case Type.CHAR:
        return "char*";
      case Type.SHORT:
        return "int16";
      case Type.INT:
        return "int32";
      case Type.LONG:
        return "int64";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "double";
      default:
        return "";
    }
  }
  public static String pyReturnFlag(Type type)
  {
    switch (type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
        return "b";
      case Type.SHORT:
        return "h";
      case Type.INT:
        return "i";
      case Type.LONG:
        return "L";
      case Type.CHAR:
        return "s";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "d";
      default:
        return "";
    }
  }
  public static void generateCClientImp(Module module, Prototype prototype)
  {
    pythonLists = new Vector();
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    writeln("static PyObject* _DO_" + prototype.name + "(PyObject *self, PyObject *args)");
    writeln("{");
    writeln(1, "int handle;");
    String varList = "i";
    String parmList = "&handle";
    String comma = ", ";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      writeln(1, "" + pyArgs(field) + ";");
      if (field.isInput == true)
      {
        varList += pyInput(field);
        parmList += comma + pyInputParm(field);
      }
    }
    writeln(1, "if (!PyArg_ParseTuple(args, \"" + varList + "\", " + parmList + "))");
    writeln(2, "return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
    writeln(1, "T" + module.name + " *instance = T" + module.name + "::Instance(handle);");
    writeln(1, "if (instance == 0)");
    writeln(2, "return Py_BuildValue(\"si\", \"" + module.name + " invalid instance\", -1);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3ListPM list = (Python3ListPM)pythonLists.elementAt(i);
      Field field = list.field;
      String extra = "";
      if (list.field.hasSize == true)
        extra = ", " + prototype.getInputSizeName(field.name);
      writeln(1, "if (" + field.name + ".fromList(" + list.name + extra + ") == 0)");
      writeln(2, "return Py_BuildValue(\"si\", \"From List Failed\", -1);");
    }
    write(1, "");
    if (hasReturn == true)
      write(pyReturn(prototype.type) + " result = ");
    write("instance->" + prototype.name + "(");
    if (prototype.name.compareTo("runPython") == 0)
      write(" ");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      if (i != 0)
        write(", ");
      Field field = (Field)prototype.parameters.elementAt(i);
      if (field.isInput == true && field.isOutput == true)
      {
        if (pyList(field) == true && field.isInput)
          write("&" + field.name + ".rec");
        else if (pyList(field) == true)
          write("&" + field.name + ".rec");
        else if (field.type.reference == Type.BYREFPTR)
          write(field.name+".data");
        else
          write("&" + field.name);
      }
      else if (field.isOutput == true)
      {
        if (pyList(field) == true)
        {
          if (field.type.reference == Type.BYREFPTR)
            write("*&" + field.name + ".recs");
          else
            write("&" + field.name + ".rec");
        }
        else
        {
          if (field.type.reference == Type.BYREFPTR)
            write(field.name + ".data");
          else
            write("&" + field.name);
        }
      }
      else
      {
        if (pyList(field) == true)
        {
          if (field.hasSize == true)
            write(field.name + ".recs");
          else if (field.type.reference == Type.BYPTR)
            write("&" + field.name + ".rec");
          else
            write(field.name + ".rec");
        }
        else
        {
          if (field.type.reference == Type.BYPTR && field.type.typeof != Type.CHAR)
            write('&' + field.name);
          else
            write(field.name);
        }
      }
    }
    writeln(");");
    varList = "";
    parmList = "";
    if (hasReturn == true)
    {
      varList = pyReturnFlag(prototype.type);
      parmList = ", result";
    }
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action action = (Action)prototype.outputs.elementAt(i);
      Field field = action.getParameter(prototype);
      String sizeName = action.getSizeName();
      varList += pyOutput(field);
      parmList += ", " + pyOutputParm(field, sizeName);
    }
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3ListPM list = (Python3ListPM)pythonLists.elementAt(i);
      if (list.isOutput == true)
      {
        Field field = list.field;
        String extra = "";
        if (list.field.hasSize == true)
          extra = ", " + prototype.getOutputSizeName(field.name);
        writeln(1, "" + field.name + ".toList(" + list.name + extra + ");");
      }
    }
    writeln(1, "PyObject *retval = Py_BuildValue(\"" + varList +"si\""+ parmList + ", \"OK\", 0);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3ListPM list = (Python3ListPM)pythonLists.elementAt(i);
      if (list.isOutput == true && list.isInput == false)
        writeln(1, "Py_XDECREF(" + list.name + ");");
    }
    writeln(1, "return retval;");
    writeln("}");
    writeln();
    writeln("static PyObject* _" + prototype.name + "(PyObject *self, PyObject *args)");
    writeln("{");
    writeln(1, "return _error_check(_DO_" + prototype.name + ", self, args);");
    writeln("}");
    writeln();
  }
  static String padder(String s, int length)
  {
    for (int i = s.length(); i < length - 1; i++)
      s = s + " ";
    return s + " ";
  }
  public static void generateMethodsDef(Module module)
  {
    writeln("static PyMethodDef " + module.name.toLowerCase() + "Methods[] =");
    writeln("{ " + padder("{\"__init__\",", 35) + padder("__init__,", 35) + "METH_VARARGS}");
    writeln(", " + padder("{\"__set_callback__\",", 35) + padder("__set_callback__,", 35) + "METH_VARARGS}");
    writeln(", " + padder("{\"__putenv__\",", 35) + padder("__putenv__,", 35) + "METH_VARARGS}");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
        writeln(", " + padder("{\"_" + prototype.name + "\",", 35) + padder("_" + prototype.name + ",", 35) + "METH_VARARGS}");
    }
    writeln(", {NULL, NULL, 0, NULL} /* Sentinel */");
    writeln("};");
    writeln("#if PY_MAJOR_VERSION == 3");
    writeln("static struct PyModuleDef moduledef =");
    writeln("{");
    writeln("    PyModuleDef_HEAD_INIT,");
    writeln(format("    \"%sMethods\",", module.name.toLowerCase()));
    writeln(format("    \"%s Methods\",", module.name));
    writeln("    -1,");
    writeln(format("    %sMethods", module.name.toLowerCase()));
    writeln("};");
    writeln("");
    writeln(format("PyMODINIT_FUNC init_%sMethods()", module.name.toLowerCase()));
    writeln("{");
    writeln("    return PyModule_Create(&moduledef);");
    writeln("}");
    writeln("#else");
    writeln(format("PyMODINIT_FUNC init%sMethods(void)", module.name.toLowerCase()));
    writeln("{");
    writeln(1, format("(void) Py_InitModule(\"%1$sMethods\", %1$sMethods);", module.name.toLowerCase()));
    writeln("}");
    writeln("#endif");
  }
  public static void generateMethodsCall(Module module, PrintWriter outLog)
  {
    writeln("import " + module.name.toLowerCase() + "Methods");
    writeln();
    writeln("class " + module.name + "Error(Exception):");
    writeln(1, "def __init__(self, value):" );
    writeln(2, "self.value = value" );
    writeln(1, "def __str__(self):" );
    writeln(2, "return repr(self.value)" );
    writeln();
    writeln("__handle__ = 100");
    writeln("def handle():");
    writeln(1, "global __handle__");
    writeln(1, "if not isinstance(__handle__, int): raise AssertionError('The global __handle__ is not an int')");
    writeln(1, "return __handle__");
    writeln();
    writeln("def " + module.name.toLowerCase() + "Handle(handle):");
    writeln(1, "global __handle__");
    writeln(1, "__handle__ = handle");
    writeln();
    writeln("def __init__(connectString, logfile='" + module.name + ".log'):");
    writeln(1, "global __handle__");
    writeln(1, "result = " + module.name.toLowerCase() + "Methods.__init__(connectString, logfile)");
    writeln(1, "if isinstance(result[-1], int) and result[-1] == -1: raise " + module.name + "Error('__init__ failed %s' % (result[-2]))");
    writeln(1, "__handle__ = result[0]");
    writeln(1, "return __handle__");
    writeln();
    writeln("def set_callback(proxy):");
    writeln(1, "" + module.name.toLowerCase() + "Methods.__set_callback__(proxy)");
    writeln();
    writeln("def putenv(name, value):");
    writeln(1, "" + module.name.toLowerCase() + "Methods.__putenv__('%s=%s' % (name, value))");
    writeln();
    writeln("def list_assert(name, maybe, count):");
    writeln(1, "if maybe == None:");
    writeln(2, "if (count != 0): raise AssertionError('The list count is not zero when the list or field is None')");
    writeln(2, "return");
    writeln(1, "if isinstance(maybe, list) == False:");
    writeln(2, "if not name in maybe._name(): raise AssertionError('%s is not of type in %s' % (name, repr(maybe._name())))");
    writeln(2, "return");
    writeln(1, "if count != len(maybe): raise AssertionError('list length %d not the same as count %d supplied' % (len(maybe), count))");
    writeln(1, "for rec in maybe:");
    writeln(2, "if not name in rec._name(): raise AssertionError('%s is not of type in %s' % (name, repr(rec._name())))");
    writeln();
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
      {
        generateMethodCall(module, prototype);
      }
    }
  }
  public static String callArg(Field field)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
      case Type.LONG:
      case Type.FLOAT:
      case Type.DOUBLE:
        return field.name;
      default:
        return field.name + "._toList()";
    }
  }
  public static String callDefault(Field field)
  {
    switch (field.type.typeof)
    {
      case Type.BOOLEAN:
        return "0";
      case Type.BYTE:
        return "0";
      case Type.CHAR:
        return "''";
      case Type.SHORT:
        return "0";
      case Type.INT:
        return "0";
      case Type.LONG:
        return "0";
      case Type.FLOAT:
        return "0.0";
      case Type.DOUBLE:
        return "0.0";
      default:
        return field.type.name + "()";
    }
  }
  private static String qualified(String module, String name)
  {
    if (qualifyMethod == true)
      return module + "_" + name;
    return name;
  }
  private static Vector pythonArgs;
  private static void generateMethodCall(Module module, Prototype prototype)
  {
    pythonArgs = new Vector();
    String inputList = "";
    String inputComma = "";
    String returnList = "";
    String resultList = "";
    String resultComma = "";
    String innerComma = ", ";
    String returnType = "void";
    boolean hasReturn = false;
    boolean hasOutputs = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
    {
      returnType = typeof(prototype.type);
      hasReturn = true;
    }
    String callParms = "";
    String innerCallParms = "handle()";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      Python3ArgsPM arg = new Python3ArgsPM();
      arg.field = field;
      arg.isInput = field.isInput;
      arg.isOutput = field.isOutput;
      arg.name = field.name;
      arg.listType = pyList(field);
      if (field.isInput == true && field.hasSize == true)
        arg.sizeName = field.input.getSizeName();
      else if (field.hasSize == true)
        arg.sizeName = field.output.getSizeName();
      pythonArgs.addElement(arg);
    }
    int inpNo = 0;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      if (i != 0)
        callParms += ", ";
      callParms += arg.name;
      if (arg.isInput)
      {
        inputList = inputList + inputComma + arg.name + ":" + typeof(arg.field.type);
        inputComma = ", ";
        if (arg.listType == true && arg.sizeName.length() > 0)
        {
          innerCallParms += innerComma + "_list" + (inpNo++);
        }
        else
        {
          innerCallParms += innerComma + arg.name;
          if (arg.listType == true)
            innerCallParms += "._toList()";
        }
      }
      if (arg.isOutput)
      {
        hasOutputs = true;
        resultList += resultComma + arg.name;
        returnList += resultComma + arg.name + ":" + typeof(arg.field.type);
        resultComma = ", ";
      }
    }
    writeln("def " + qualified(module.name, prototype.name) + "(" + callParms + "):");
    writeln(1, "'''");
    writeln(1, "inputs  " + inputList);
    writeln(1, "returns " + (hasReturn == true ? "rc:" + returnType +", " : "") + returnList);
    writeln(1, "'''");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      writeln(1, ""+assertof(arg));
    }
    inpNo = 0;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      if (arg.isInput == true && arg.listType == true && arg.sizeName.length() > 0)
      {
        String listName = "_list" + (inpNo++);
        writeln(1, "" + listName + " = []");
        writeln(1, "for n in range(" + arg.sizeName + "):");
        writeln(2, "" + listName + ".append(" + arg.name + "[n]._toList())");
      }
    }
    writeln(1, "_result = " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")");
    writeln(1, "if isinstance(_result[-1], int) and _result[-1] == -1: raise " + module.name + "Error(_result[-2])");
    int no = 0;
    if (hasReturn == true) no = 1;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (arg.listType == false)
      {
        writeln(1, "" + arg.name + " = _result[" + (no) + "]");
        no++;
      }
      else
      {
        if (arg.sizeName.length() == 0)
        {
          writeln(1, "_rec = " + arg.name + "._make()");
          writeln(1, "_rec._fromList(_result[" + (no) + "])");
          writeln(1, "" + arg.name + " = _rec");
          no++;
        }
        else
        {
          writeln(1, "_mainList = _result[" + (no) + "]");
          writeln(1, "_dataList = []");
          writeln(1, "for _data in _mainList:");
          writeln(2, "_rec = " + arg.name + "._make()");
          writeln(2, "_rec._fromList(_data)");
          writeln(2, "_dataList.append(_rec)");
          writeln(1, "" + arg.name + " = _dataList");
          no++;
        }
      }
    }
    if (resultList.length() > 0)
      writeln(1, "return " + (hasReturn == true ? "_result[0], " : "") + resultList);
    else
      writeln(1, "return" + (hasReturn == true ? " _result[0]" : ""));
  }
  private static void generateDoxygenComment(Module module, Prototype prototype, PrintWriter outLog)
  {
    pythonArgs = new Vector();
    String returnList = "";
    String resultComma = "";
    String callParms = "";
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      Action action = (Action)prototype.inputs.elementAt(i);
      if (action == null)
      {
        outLog.println("Module:"+module.name+" Prototype:"+prototype.name+" has null input action for element no:"+i);
        continue;
      }
      Field field = action.getParameter(prototype);
      if (field == null)
      {
        outLog.println("Module:"+module.name+" Prototype:"+prototype.name+" has null input field for Action:"+action.name);
        continue;
      }
      Python3ArgsPM arg = new Python3ArgsPM();
      arg.field = field;
      arg.isInput = true;
      arg.name = field.name;
      arg.listType = pyList(field);
      pythonArgs.addElement(arg);
    }
    for (int i = 0; i < prototype.outputs.size(); i++)
    {
      Action action = (Action)prototype.outputs.elementAt(i);
      if (action == null)
      {
        outLog.println("Module:"+module.name+" Prototype:"+prototype.name+" has null output action for element no:"+i);
        continue;
      }
      Field field = action.getParameter(prototype);
      if (field == null)
      {
        outLog.println("Module:"+module.name+" Prototype:"+prototype.name+" has null output field for Action:"+action.name);
        continue;
      }
      String sizeName = action.getSizeName();
      Python3ArgsPM arg = new Python3ArgsPM();
      arg.name = field.name;
      arg.listType = pyList(field);
      boolean reUsed = false;
      for (int j = 0; j < pythonArgs.size(); j++)
      {
        Python3ArgsPM pa = (Python3ArgsPM)pythonArgs.elementAt(j);
        if (pa.field == field)
        {
          arg = pa;
          reUsed = true;
          break;
        }
      }
      if (reUsed == false)
        pythonArgs.addElement(arg);
      arg.field = field;
      arg.isOutput = true;
      arg.sizeName = sizeName;
    }
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      if (i != 0)
        callParms += ", ";
      callParms += arg.name;
      if (arg.isOutput)
      {
        returnList += resultComma + arg.name + ":" + arg.field.type.name;
        resultComma = ", ";
      }
    }
    writeln("/**");
    writeln(" * \\fn def " + qualified(module.name, prototype.name) + "(" + callParms + "):");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      Python3ArgsPM arg = (Python3ArgsPM)pythonArgs.elementAt(i);
      writeln(" * \\param " + arg.name + ":" + arg.field.type.name);
    }
    writeln(" * \\return " + (hasReturn == true ? "rc:" + prototype.type.name + ", " : "") + returnList + "");
    writeln(" */");
  }
  private static String typeof(Type type)
  {
    switch (type.typeof)
    {
      case Type.USERTYPE:
        return type.name;
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        return "int";
      case Type.LONG:
        return "long";
      case Type.FLOAT:
      case Type.DOUBLE:
        return "float";
      case Type.CHAR:
      case Type.STRING:
        return "str";
      default:
        return type.name;
    }
  }
  private static String assertof(Python3ArgsPM arg)
  {
    String x = "";
    switch (arg.field.type.typeof)
    {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        x = "int";
        break;
      case Type.LONG:
        return "if not isinstance(" + arg.name + ", int) and not isinstance(" + arg.name + ", long): raise AssertionError('%s is not instance of int or long' % ('" + arg.name + "'))";
      case Type.FLOAT:
      case Type.DOUBLE:
        x = "float";
        break;
      case Type.CHAR:
      case Type.STRING:
        x = "str";
        break;
      case Type.USERTYPE:
        if (arg.listType == true && arg.sizeName.length() > 0) 
          return "list_assert('" + arg.field.type.name + "', " + arg.name + ", "+ arg.sizeName+")";
      default:
        return "if not '" + arg.field.type.name + "' in " + arg.name + "._name(): raise AssertionError('%s not in %s' % ('" + arg.field.type.name + "', repr(" + arg.name + "._name())))";
    }
    return "if not isinstance(" + arg.name + ", " + x + "): raise AssertionError('%s is not instance of %s' % ('" + arg.name + "', '" + x + "'))";
  }
}

class Python3ListPM
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  Python3ListPM()
  {
    sizeName = "";
  }
}

class Python3ArgsPM
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  boolean listType = false;
  Python3ArgsPM()
  {
    sizeName = "";
  }
}


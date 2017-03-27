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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopUbiPuffinModule extends Generator
{
  public static String description()
  {
    return "Generates Client C Dll and C++ class Code";
  }
  public static String documentation()
  {
    return "Generates Client Code";
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
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println("#ifndef _" + module.name + "PYTHON_H_");
        outData.println("#define _" + module.name + "PYTHON_H_");
        outData.println("#include \"machine.h\"");
        outData.println("#include \"logfile.h\"");
        outData.println();
        outData.println("#ifdef _DEBUG");
        outData.println("  #define _DEBUG_SAVE_ _DEBUG");
        outData.println("  #undef _DEBUG");
        outData.println("#endif");
        outData.println();
        outData.println("#include \"Python.h\"");
        outData.println();
        outData.println("#ifdef inline");
        outData.println("  #undef  inline");
        outData.println("  #define inline inline");
        outData.println("#endif");
        outData.println();
        outData.println("#ifdef _DEBUG_SAVE_");
        outData.println("  #define _DEBUG _DEBUG_SAVE_");
        outData.println("  #undef _DEBUG_SAVE_");
        outData.println("#endif");
        outData.println();
        outData.println("#ifndef OS_DECLSPEC");
        outData.println("  #define OS_DECLSPEC \"\"");
        outData.println("#endif");
        outData.println();
        outData.println("#include \"" + module.name.toLowerCase() + ".h\"");
        outData.println();
        outData.println("T" + module.name + "* " + module.name + "GetModule();");
        outData.println("UbiBaseVars* " + module.name + "Initialise(const char *connectString, TJConnector* share, const char* logfile, eLevel logLevel=eLogDebug, bool display=true, bool rotate=false);");
        outData.println("int " + module.name + "CallMethod(const char* name, const char* data, TBChar &value);");
        outData.println();
        outData.println("#endif");
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
      outLog.println("Code: " + output + module.name.toLowerCase() + "Python.cpp");
      outFile = new FileOutputStream(output+module.name.toLowerCase()+"Python.cpp");
      outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"machine.h\"");
        outData.println("#include \"xcept.h\"");
        outData.println();
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println();
        outData.println("#include \"handles.h\"");
        outData.println("#include \"tbuffer.h\"");
        outData.println("#include \"swapbytes.h\"");
        outData.println("#include \"xstring.h\"");
        outData.println();
        outData.println("#include <math.h>");
        outData.println();
        outData.println("#ifdef _DEBUG");
        outData.println("  #define _DEBUG_SAVE_ _DEBUG");
        outData.println("  #undef _DEBUG");
        outData.println("#endif");
        outData.println();
        outData.println("#include \"Python.h\"");
        outData.println("#include \"pythonrun.h\"");
        outData.println();
        outData.println("#ifdef inline");
        outData.println("  #undef  inline");
        outData.println("  #define inline inline");
        outData.println("#endif");
        outData.println();
        outData.println("#ifdef _DEBUG_SAVE_");
        outData.println("  #define _DEBUG _DEBUG_SAVE_");
        outData.println("  #undef _DEBUG_SAVE_");
        outData.println("#endif");
        outData.println();
        outData.println("#include \"pythonbuild.h\"");
        outData.println("#include \"" + module.name.toLowerCase() + "python.h\"");
        outData.println();
        outData.println("static T" + module.name + "* " + module.name.toLowerCase() + ";");
        outData.println();
        outData.println("extern \"C\" OS_DECLSPEC T" + module.name + "* " + module.name.toLowerCase() + "_get_module()");
        outData.println("{");
        outData.println("  return " + module.name.toLowerCase() + ";");
        outData.println("}");
        outData.println();
        outData.println("T" + module.name + "* " + module.name + "GetModule()");
        outData.println("{");
        outData.println("  return " + module.name.toLowerCase() + "_get_module();");
        outData.println("}");
        outData.println();
        outData.println("extern \"C\" OS_DECLSPEC UbiBaseVars* " + module.name.toLowerCase() + "_initialise(const char *connectString, TJConnector* share,");
        outData.println("  const char* logfile, eLevel logLevel=eLogDebug, bool display=true, bool rotate=false)");
        outData.println("{");
        outData.println("  try");
        outData.println("  {");
        outData.println("    if (" + module.name.toLowerCase() + " != 0)");
        outData.println("    {");
        outData.println("      " + module.name.toLowerCase() + "->connect->Logoff();");
        outData.println("      delete " + module.name.toLowerCase() + "->connect;");
        outData.println("      delete " + module.name.toLowerCase() + ";");
        outData.println("    }");
        outData.println("    " + module.name.toLowerCase() + " = new T" + module.name + "();");
        outData.println("    SingletonLogFile::Initialize(logfile, logLevel, display, rotate);");
        outData.println("    " + module.name.toLowerCase() + "->logFile = SingletonLogFile::LogFile();");
        outData.println("    if (connectString != 0 && connectString[0] != 0)");
        outData.println("    {");
        outData.println("      char schema[256], server[256], user[256];");
        outData.println("      char *p = (char*) connectString;");
        outData.println("      char *q = strchr(p, ':');");
        outData.println("      if (q == 0)");
        outData.println("      {");
        outData.println("        " + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 1st ':'\");");
        outData.println("        return 0;");
        outData.println("      }");
        outData.println("      strncpyz(server, p, min((int)(q-p),255));");
        outData.println("      p = q+1;");
        outData.println("      q = strchr(p, ':');");
        outData.println("      if (q == 0)");
        outData.println("      {");
        outData.println("        " + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 2nd ':'\");");
        outData.println("        return 0;");
        outData.println("      }");
        outData.println("      strncpyz(schema, p, min((int)(q-p),255));");
        outData.println("      p = q+1;");
        outData.println("      q = strchr(p, ':');");
        outData.println("      if (q == 0)");
        outData.println("      {");
        outData.println("        " + module.name.toLowerCase() + "->logFile->Log(\"Connect String missing 3rd ':'\");");
        outData.println("        return 0;");
        outData.println("      }");
        outData.println("      strncpyz(user, p, min((int)(q-p),255));");
        outData.println("      p = q+1;");
        outData.println("      if (share != 0)");
        outData.println("      {");
        outData.println("        " + module.name.toLowerCase() + "->connect = TJConnector::Coordinated();");
        outData.println("        " + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() created\");");
        outData.println("        " + module.name.toLowerCase() + "->connect->Logon(user, p, server, schema, &share->cliEnv);");
        outData.println("      }");
        outData.println("      else");
        outData.println("      {");
        outData.println("        " + module.name.toLowerCase() + "->connect = new TJConnector();");
        outData.println("        " + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() created\");");
        outData.println("        " + module.name.toLowerCase() + "->connect->Logon(user, p, server, schema);");
        outData.println("      }");
        outData.println("      " + module.name.toLowerCase() + "->logFile->Log(\"" + module.name + " Logon done\");");
        outData.println("    }");
        outData.println("    else");
        outData.println("    {");
        outData.println("      " + module.name.toLowerCase() + "->connect = TJConnector::Coordinated();");
        outData.println("      " + module.name.toLowerCase() + "->logFile->Log(\"new " + module.name + " TJConnector() copy created\");");
        outData.println("      " + module.name.toLowerCase() + "->connect->CopyOf(*share);");
        outData.println("      " + module.name.toLowerCase() + "->logFile->Log(\"" + module.name + " Copy done\");");
        outData.println("    }");
        outData.println("  }");
        outData.println("  catch (xCept &ex)");
        outData.println("  {");
        outData.println("    " + module.name.toLowerCase() + "->logFile->Log(ex);");
        outData.println("    return 0;");
        outData.println("  }");
        for (int i = 0; i < module.pragmas.size(); i++)
        {
          String pragma = (String)module.pragmas.elementAt(i);
          int n = pragma.indexOf("CACHE:");
          if (n < 0)
            continue;
          pragma = pragma.substring(n + 6).trim();
          outData.println("  " + pragma + "::Instance()->Setup(" + module.name.toLowerCase() + "->connect);");
          break;
        }
        outData.println("  return " + module.name.toLowerCase() + ";");
        outData.println("}");
        outData.println();
        outData.println("UbiBaseVars* " + module.name + "Initialise(const char *connectString, TJConnector* share,");
        outData.println("  const char* logfile, eLevel logLevel, bool display, bool rotate)");
        outData.println("{");
        outData.println("  return " + module.name.toLowerCase() + "_initialise(connectString, share, logfile, logLevel, display, rotate);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject *__callback_method__;");
        outData.println();
        outData.println("extern \"C\" OS_DECLSPEC int " + module.name.toLowerCase() + "_call_method(const char* name, const char* data, TBChar &value)");
        outData.println("{");
        outData.println("  if (__callback_method__ == 0)");
        outData.println("    return 0;");
        outData.println("  PyObject *args;");
        outData.println("  PyObject *result;");
        outData.println("  args = Py_BuildValue(\"ss\", name, data);");
        outData.println("  result = PyEval_CallObject(__callback_method__, args);");
        outData.println("  Py_DECREF(args);");
        outData.println("  if (result == 0)");
        outData.println("    return 0;");
        outData.println("  value.set(PyString_AsString(result)); ");
        outData.println("  Py_DECREF(result);");
        outData.println("  return value.size;");
        outData.println("}");
        outData.println();
        outData.println("int " + module.name + "CallMethod(const char* name, const char* data, TBChar &value)");
        outData.println("{");
        outData.println("  return " + module.name.toLowerCase() + "_call_method(name, data, value);");
        outData.println("}");
        outData.println();
        outData.println("typedef PyObject* (*self_args_ptr)(PyObject *self, PyObject *args);");
        outData.println();
        outData.println("static PyObject* _error_check(self_args_ptr function, PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  TBChar errBuffer;");
        outData.println("  try");
        outData.println("  {");
        outData.println("    return function(self, args);");
        outData.println("  }");
        outData.println("  catch (xDBException &ex1)");
        outData.println("  {");
        outData.println("    errBuffer.append(\"DBException: \");");
        outData.println("    errBuffer.append(ex1.ErrorStr());");
        outData.println("    return Py_BuildValue(\"si\", errBuffer.data, -1);");
        outData.println("  }");
        outData.println("  catch (xCept &ex2)");
        outData.println("  {");
        outData.println("    errBuffer.append(\"Exception: \");");
        outData.println("    errBuffer.append(ex2.ErrorStr());");
        outData.println("    return Py_BuildValue(\"si\", errBuffer.data, -1);");
        outData.println("  }");
        outData.println("  catch (...)");
        outData.println("  {");
        outData.println("    errBuffer.append(\"Catch ...\");");
        outData.println("    return Py_BuildValue(\"si\", errBuffer.data, -1);");
        outData.println("  }");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* _DO__init__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  char* connectString;");
        outData.println("  char* logfile;");
        outData.println("  int result = 0;");
        outData.println("  if (!PyArg_ParseTuple(args, \"ss\", &connectString, &logfile))");
        outData.println("    return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        outData.println("  // a nasty upcast - shudder");
        outData.println("  T" + module.name + "* module = (T" + module.name + "*)" + module.name.toLowerCase() + "_initialise(connectString, 0, logfile);");
        outData.println("  if (module != 0)");
        outData.println("    result = module->instance;");
        outData.println("  return Py_BuildValue(\"isi\", result, \"OK\", 0);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* __init__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  return _error_check(_DO__init__, self, args);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* _DO__set_callback__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  PyObject *object;");
        outData.println("  if (!PyArg_ParseTuple(args, \"O:__set_callback__\", &object))");
        outData.println("    return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        outData.println("  if (!PyCallable_Check(object)) ");
        outData.println("    return Py_BuildValue(\"si\", \"Parameter must be callable\", -1);");
        outData.println("  Py_XINCREF(object);");
        outData.println("  Py_XDECREF(__callback_method__);  // Dispose of previous callback");
        outData.println("  __callback_method__ = object;     // Remember new callback ");
        outData.println("  return Py_BuildValue(\"si\", \"OK\", 0);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* __set_callback__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  return _error_check(_DO__set_callback__, self, args);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* _DO__putenv__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  char *envstr;");
        outData.println("  if (!PyArg_ParseTuple(args, \"s\", &envstr))");
        outData.println("    return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
        outData.println("  int rc = putenv(envstr);");
        outData.println("  return Py_BuildValue(\"si\", \"Check rc=0 worked else error\", rc);");
        outData.println("}");
        outData.println();
        outData.println("static PyObject* __putenv__(PyObject *self, PyObject *args)");
        outData.println("{");
        outData.println("  return _error_check(_DO__putenv__, self, args);");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
          {
            generateDoxygenComment(module, prototype, outData, outLog);
            generateCClientImp(module, prototype, outData);
          }
        }
        generateMethodsDef(module, outData);
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
  public static void generatePyClient(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String outname = output + module.name.toLowerCase() + ".py";
      outLog.println("Code: " + outname);
      OutputStream outFile = new FileOutputStream(outname);
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        generateMethodsCall(module, outData, outLog);
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
  static Vector<PythonListPM> pythonLists;
  public static String pyInputParm(Field field, PrintWriter outData)
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
        PythonListPM list = new PythonListPM();
        list.name = "list" + pythonLists.size();
        list.field = field;
        list.isInput = true;
        pythonLists.addElement(list);
        outData.println("    PyObject* " + list.name + ";");
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
  public static String pyOutputParm(Field field, PrintWriter outData, String sizeName)
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
          PythonListPM list = (PythonListPM)pythonLists.elementAt(i);
          if (field == list.field)
          {
            list.isOutput = true;
            return list.name;
          }
        }
        PythonListPM list = new PythonListPM();
        list.name = "list" + pythonLists.size();
        list.sizeName = sizeName;
        list.field = field;
        list.isOutput = true;
        pythonLists.addElement(list);
        if (sizeName.length() > 0)
          outData.println("    PyObject* " + list.name + " = PyList_New(" + sizeName + ");");
        else
          outData.println("    PyObject* " + list.name + " = PyList_New("+field.name+".build.count);");
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
  public static void generateCClientImp(Module module, Prototype prototype, PrintWriter outData)
  {
    pythonLists = new Vector<PythonListPM>();
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    outData.println("static PyObject* _DO_" + prototype.name + "(PyObject *self, PyObject *args)");
    outData.println("{");
    outData.println("  int handle;");
    String varList = "i";
    String parmList = "&handle";
    String comma = ", ";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      outData.println("  " + pyArgs(field) + ";");
      if (field.isInput == true)
      {
        varList += pyInput(field);
        parmList += comma + pyInputParm(field, outData);
      }
    }
    outData.println("  if (!PyArg_ParseTuple(args, \"" + varList + "\", " + parmList + "))");
    outData.println("    return Py_BuildValue(\"si\", \"Tuple Parse Failed\", -1);");
    outData.println("  T" + module.name + " *instance = T" + module.name + "::Instance(handle);");
    outData.println("  if (instance == 0)");
    outData.println("    return Py_BuildValue(\"si\", \"" + module.name + " invalid instance\", -1);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonListPM list = (PythonListPM)pythonLists.elementAt(i);
      Field field = list.field;
      String extra = "";
      if (list.field.hasSize == true)
        extra = ", " + prototype.getInputSizeName(field.name);
      outData.println("  if (" + field.name + ".fromList(" + list.name + extra + ") == 0)");
      outData.println("    return Py_BuildValue(\"si\", \"From List Failed\", -1);");
    }
    outData.print("  ");
    if (hasReturn == true)
      outData.print(pyReturn(prototype.type) + " result = ");
    outData.print("instance->" + prototype.name + "(");
    if (prototype.name.compareTo("runPython") == 0)
      outData.print(" ");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      if (i != 0)
        outData.print(", ");
      Field field = (Field)prototype.parameters.elementAt(i);
      if (field.isInput == true && field.isOutput == true)
      {
        if (pyList(field) == true && field.isInput)
          outData.print("&" + field.name + ".rec");
        else if (pyList(field) == true)
          outData.print("&" + field.name + ".rec");
        else if (field.type.reference == Type.BYREFPTR)
          outData.print(field.name+".data");
        else
          outData.print("&" + field.name);
      }
      else if (field.isOutput == true)
      {
        if (pyList(field) == true)
        {
          if (field.type.reference == Type.BYREFPTR)
            outData.print("*&" + field.name + ".recs");
          else
            outData.print("&" + field.name + ".rec");
        }
        else
        {
          if (field.type.reference == Type.BYREFPTR)
            outData.print(field.name + ".data");
          else
            outData.print("&" + field.name);
        }
      }
      else
      {
        if (pyList(field) == true)
        {
          if (field.hasSize == true)
            outData.print(field.name + ".recs");
          else if (field.type.reference == Type.BYPTR)
            outData.print("&" + field.name + ".rec");
          else
            outData.print(field.name + ".rec");
        }
        else
        {
          if (field.type.reference == Type.BYPTR && field.type.typeof != Type.CHAR)
            outData.print('&' + field.name);
          else
            outData.print(field.name);
        }
      }
    }
    outData.println(");");
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
      parmList += ", " + pyOutputParm(field, outData, sizeName);
    }
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonListPM list = (PythonListPM)pythonLists.elementAt(i);
      if (list.isOutput == true)
      {
        Field field = list.field;
        String extra = "";
        if (list.field.hasSize == true)
          extra = ", " + prototype.getOutputSizeName(field.name);
        outData.println("  " + field.name + ".toList(" + list.name + extra + ");");
      }
    }
    outData.println("  PyObject *retval = Py_BuildValue(\"" + varList +"si\""+ parmList + ", \"OK\", 0);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonListPM list = (PythonListPM)pythonLists.elementAt(i);
      if (list.isOutput == true && list.isInput == false)
        outData.println("  Py_XDECREF(" + list.name + ");");
    }
    outData.println("  return retval;");
    outData.println("}");
    outData.println();
    outData.println("static PyObject* _" + prototype.name + "(PyObject *self, PyObject *args)");
    outData.println("{");
    outData.println("  return _error_check(_DO_" + prototype.name + ", self, args);");
    outData.println("}");
    outData.println();
  }
  static String padder(String s, int length)
  {
    for (int i = s.length(); i < length - 1; i++)
      s = s + " ";
    return s + " ";
  }
  public static void generateMethodsDef(Module module, PrintWriter outData)
  {
    outData.println("static PyMethodDef " + module.name.toLowerCase() + "Methods[] =");
    outData.println("{ " + padder("{\"__init__\",", 35) + padder("__init__,", 35) + "METH_VARARGS}");
    outData.println(", " + padder("{\"__set_callback__\",", 35) + padder("__set_callback__,", 35) + "METH_VARARGS}");
    outData.println(", " + padder("{\"__putenv__\",", 35) + padder("__putenv__,", 35) + "METH_VARARGS}");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
        outData.println(", " + padder("{\"_" + prototype.name + "\",", 35) + padder("_" + prototype.name + ",", 35) + "METH_VARARGS}");
    }
    outData.println(", {NULL, NULL, 0, NULL} /* Sentinel */");
    outData.println("};");
    outData.println("PyMODINIT_FUNC");
    outData.println("init" + module.name.toLowerCase() + "Methods(void)");
    outData.println("{");
    outData.println("  (void) Py_InitModule(\"" + module.name.toLowerCase() + "Methods\", " + module.name.toLowerCase() + "Methods);");
    outData.println("}");
  }
  public static void generateMethodsCall(Module module, PrintWriter outData, PrintWriter outLog)
  {
    outData.println("import " + module.name.toLowerCase() + "Methods");
    outData.println();
    outData.println("class " + module.name + "Error(Exception):");
    outData.println("    def __init__(self, value):" );
    outData.println("        self.value = value" );
    outData.println("    def __str__(self):" );
    outData.println("        return repr(self.value)" );
    outData.println();
    outData.println("__handle__ = 100");
    outData.println("def handle():");
    outData.println("    global __handle__");
    outData.println("    if not isinstance(__handle__, int): raise AssertionError, 'The global __handle__ is not an int'");
    outData.println("    return __handle__");
    outData.println();
    outData.println("def " + module.name.toLowerCase() + "Handle(handle):");
    outData.println("    global __handle__");
    outData.println("    __handle__ = handle");
    outData.println();
    outData.println("def __init__(connectString, logfile='" + module.name + ".log'):");
    outData.println("    global __handle__");
    outData.println("    result = " + module.name.toLowerCase() + "Methods.__init__(connectString, logfile)");
    outData.println("    if isinstance(result[-1], int) and result[-1] == -1: raise " + module.name + "Error, '__init__ failed %s' % (result[-2])");
    outData.println("    __handle__ = result[0]");
    outData.println("    return __handle__");
    outData.println();
    outData.println("def set_callback(proxy):");
    outData.println("    " + module.name.toLowerCase() + "Methods.__set_callback__(proxy)");
    outData.println();
    outData.println("def putenv(name, value):");
    outData.println("    " + module.name.toLowerCase() + "Methods.__putenv__('%s=%s' % (name, value))");
    outData.println();
    outData.println("def list_assert(name, maybe, count):");
    outData.println("    if maybe == None:");
    outData.println("        if (count != 0): raise AssertionError, 'The list count is not zero when the list or field is None'");
    outData.println("        return");
    outData.println("    if isinstance(maybe, list) == False:");
    outData.println("        if not name in maybe._name(): raise AssertionError, '%s is not of type in %s' % (name, repr(maybe._name()))");
    outData.println("        return");
    outData.println("    if count != len(maybe): raise AssertionError, 'list length %d not the same as count %d supplied' % (len(maybe), count)");
    outData.println("    for rec in maybe:");
    outData.println("        if not name in rec._name(): raise AssertionError, '%s is not of type in %s' % (name, repr(rec._name()))");
    outData.println();
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
      {
        generateMethodCall(module, prototype, outData);
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
  private static Vector<PythonArgsPM> pythonArgs;
  private static void generateMethodCall(Module module, Prototype prototype, PrintWriter outData)
  {
    pythonArgs = new Vector<PythonArgsPM>();
    String inputList = "";
    String inputComma = "";
    String returnList = "";
    String resultList = "";
    String resultComma = "";
    String innerComma = ", ";
    String returnType = "void";
    boolean hasReturn = false;
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
      PythonArgsPM arg = new PythonArgsPM();
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
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
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
        resultList += resultComma + arg.name;
        returnList += resultComma + arg.name + ":" + typeof(arg.field.type);
        resultComma = ", ";
      }
    }
    outData.println("def " + qualified(module.name, prototype.name) + "(" + callParms + "):");
    outData.println("    '''");
    outData.println("    inputs  " + inputList);
    outData.println("    returns " + (hasReturn == true ? "rc:" + returnType +", " : "") + returnList);
    outData.println("    '''");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
      outData.println("    "+assertof(arg));
    }
    inpNo = 0;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
      if (arg.isInput == true && arg.listType == true && arg.sizeName.length() > 0)
      {
        String listName = "_list" + (inpNo++);
        outData.println("    " + listName + " = []");
        outData.println("    for n in range(" + arg.sizeName + "):");
        outData.println("        " + listName + ".append(" + arg.name + "[n]._toList())");
      }
    }
    outData.println("    _result = " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")");
    outData.println("    if isinstance(_result[-1], int) and _result[-1] == -1: raise " + module.name + "Error(_result[-2])");
    int no = 0;
    if (hasReturn == true) no = 1;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (arg.listType == false)
      {
        outData.println("    " + arg.name + " = _result[" + (no) + "]");
        no++;
      }
      else
      {
        if (arg.sizeName.length() == 0)
        {
          outData.println("    _rec = " + arg.name + "._make()");
          outData.println("    _rec._fromList(_result[" + (no) + "])");
          outData.println("    " + arg.name + " = _rec");
          no++;
        }
        else
        {
          outData.println("    _mainList = _result[" + (no) + "]");
          outData.println("    _dataList = []");
          outData.println("    for _data in _mainList:");
          outData.println("        _rec = " + arg.name + "._make()");
          outData.println("        _rec._fromList(_data)");
          outData.println("        _dataList.append(_rec)");
          outData.println("    " + arg.name + " = _dataList");
          no++;
        }
      }
    }
    if (resultList.length() > 0)
      outData.println("    return " + (hasReturn == true ? "_result[0], " : "") + resultList);
    else
      outData.println("    return" + (hasReturn == true ? " _result[0]" : ""));
  }
  private static void generateDoxygenComment(Module module, Prototype prototype, PrintWriter outData, PrintWriter outLog)
  {
    pythonArgs = new Vector<PythonArgsPM>();
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
      PythonArgsPM arg = new PythonArgsPM();
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
      PythonArgsPM arg = new PythonArgsPM();
      arg.name = field.name;
      arg.listType = pyList(field);
      boolean reUsed = false;
      for (int j = 0; j < pythonArgs.size(); j++)
      {
        PythonArgsPM pa = (PythonArgsPM)pythonArgs.elementAt(j);
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
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
      if (i != 0)
        callParms += ", ";
      callParms += arg.name;
      if (arg.isOutput)
      {
        returnList += resultComma + arg.name + ":" + arg.field.type.name;
        resultComma = ", ";
      }
    }
    outData.println("/**");
    outData.println(" * \\fn def " + qualified(module.name, prototype.name) + "(" + callParms + "):");
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgsPM arg = (PythonArgsPM)pythonArgs.elementAt(i);
      outData.println(" * \\param " + arg.name + ":" + arg.field.type.name);
    }
    outData.println(" * \\return " + (hasReturn == true ? "rc:" + prototype.type.name + ", " : "") + returnList + "");
    outData.println(" */");
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
  private static String assertof(PythonArgsPM arg)
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
        return "if not isinstance(" + arg.name + ", int) and not isinstance(" + arg.name + ", long): raise AssertionError, '%s is not instance of int or long' % ('" + arg.name + "')";
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
        return "if not '" + arg.field.type.name + "' in " + arg.name + "._name(): raise AssertionError, '%s not in %s' % ('" + arg.field.type.name + "', repr(" + arg.name + "._name()))";
    }
    return "if not isinstance(" + arg.name + ", " + x + "): raise AssertionError, '%s is not instance of %s' % ('" + arg.name + "', '" + x + "')";
  }
}

class PythonListPM
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  PythonListPM()
  {
    sizeName = "";
  }
}

class PythonArgsPM
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  boolean listType = false;
  PythonArgsPM()
  {
    sizeName = "";
  }
}


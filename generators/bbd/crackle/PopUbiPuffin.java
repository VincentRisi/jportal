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

public class PopUbiPuffin extends Generator
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
  }
  /**
  * Sets up the writer and generates the general stuff
  */
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
        outData.println("extern PyMethodDef " + module.name + "Methods[];");
        outData.println("extern char* " + module.name + "Support[];");
        outData.println("extern int " + module.name + "SupportCount;");
        outData.println("int " + module.name + "SupportLength();");
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
        outData.println("#include \"pythonbuild.h\"");// must be before the shell
        outData.println("#include \"" + module.name.toLowerCase() + "python.h\"");
        outData.println();
        outData.println("#include \"" + module.name.toLowerCase() + ".h\"");
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
  static Vector<PythonList> pythonLists;
  public static String pyInputParm(Field field, PrintWriter outData)
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
        return "&" + field.name;
      default:
        PythonList list = new PythonList();
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
  public static String pyOutput(Type type)
  {
    switch (type.typeof)
    {
      case Type.BOOLEAN:
        return "i";
      case Type.BYTE:
        return "i";
      case Type.CHAR:
        return "s";
      case Type.SHORT:
        return "i";
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
        if (field.type.reference == Type.BYREFPTR)
          return field.name + ".data";
        else
          return field.name;
      default:
        for (int i = 0; i < pythonLists.size(); i++)
        {
          PythonList list = (PythonList)pythonLists.elementAt(i);
          if (field == list.field)
          {
            list.isOutput = true;
            return list.name;
          }
        }
        PythonList list = new PythonList();
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
    pythonLists = new Vector<PythonList>();
    boolean hasReturn = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    outData.println("static PyObject* _" + prototype.name + "(PyObject *self, PyObject *args)");
    outData.println("{");
    outData.println("  TBChar errBuffer;");
    outData.println("  try");
    outData.println("  {");
    outData.println("    int handle;");
    String varList = "i";
    String parmList = "&handle";
    String comma = ", ";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      outData.println("    " + pyArgs(field) + ";");
      if (field.isInput == true)
      {
        varList += pyInput(field);
        parmList += comma + pyInputParm(field, outData);
      }
    }
    outData.println("    if (!PyArg_ParseTuple(args, \"" + varList + "\", " + parmList + "))");
    outData.println("      return 0;");
    outData.println("    T" + module.name + " *instance = T" + module.name + "::Instance(handle);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonList list = (PythonList)pythonLists.elementAt(i);
      Field field = list.field;
      String extra = "";
      if (list.field.hasSize == true)
        extra = ", " + prototype.getInputSizeName(field.name);
      outData.println("    if (" + field.name + ".fromList(" + list.name + extra + ") == 0)");
      outData.println("      return 0;");
    }
    outData.print("    ");
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
          else
            outData.print("&" + field.name + ".rec");
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
      varList += pyOutput(field.type);
      parmList += ", " + pyOutputParm(field, outData, sizeName);
    }
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonList list = (PythonList)pythonLists.elementAt(i);
      if (list.isOutput == true)
      {
        Field field = list.field;
        String extra = "";
        if (list.field.hasSize == true)
          extra = ", " + prototype.getOutputSizeName(field.name);
        outData.println("    " + field.name + ".toList(" + list.name + extra + ");");
      }
    }
    outData.println("    PyObject *retval = Py_BuildValue(\"" + varList +"s\""+ parmList + ", errBuffer.data);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      PythonList list = (PythonList)pythonLists.elementAt(i);
      if (list.isOutput == true && list.isInput == false)
        outData.println("    Py_XDECREF(" + list.name + ");");
    }
    outData.println("    return retval;");
    outData.println("  }");
    outData.println("  catch (xDBException &ex1)");
    outData.println("  {");
    outData.println("    errBuffer.append(\"DBException: \");");
    outData.println("    errBuffer.append(ex1.ErrorStr());");
    outData.println("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    outData.println("    return 0;");
    outData.println("  }");
    outData.println("  catch (xCept &ex2)");
    outData.println("  {");
    outData.println("    errBuffer.append(\"Exception: \");");
    outData.println("    errBuffer.append(ex2.ErrorStr());");
    outData.println("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    outData.println("    return 0;");
    outData.println("  }");
    outData.println("  catch (...)");
    outData.println("  {");
    outData.println("    errBuffer.append(\"Catch ...\");");
    outData.println("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    outData.println("    return 0;");
    outData.println("  }");
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
    String w1 = "{ ";
    outData.println("PyMethodDef "+module.name+"Methods[] =");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
      {
        outData.println(w1 + padder("{\"_" + prototype.name + "\",", 35) + padder("_" + prototype.name + ",", 35) + "METH_VARARGS}");
        w1 = ", ";
      }
    }
    outData.println(", {0, 0}");
    outData.println("};");
  }
  static String front = "_NL \"";
  static String end = "\"";
  public static void generateMethodsCall(Module module, PrintWriter outData, PrintWriter outLog)
  {
    if (front.length() > 0)
    {
      outData.println("#define _NL \"\\n\"");
      outData.println("char *" + module.name + "Support[] = {\"import " + module.name.toLowerCase() + "Methods\" _NL");
    }
    else
      outData.println("import " + module.name.toLowerCase() + "Methods");
    outData.println(front + "class " + module.name + "Error:" + end);
    outData.println(front + "  def __init__(self, value):" + end);
    outData.println(front + "    self.value = value" + end);
    outData.println(front + "  def __str__(self):" + end);
    outData.println(front + "    return repr(self.value)" + end);
    outData.println(front + "def list_assert(name, maybe, count):" + end);
    outData.println(front + "  if maybe == None:" + end);
    outData.println(front + "    if (count != 0) : raise AssertionError, '%s: list count != 0 in None case' % (name)" + end);
    outData.println(front + "    return" + end);
    outData.println(front + "  if isinstance(maybe, list) == False:" + end);
    outData.println(front + "    if not name in maybe._name(): raise AssertionError, '%s is not in %s' % (name, repr(maybe._name()))" + end);
    outData.println(front + "    return" + end);
    outData.println(front + "  if count != len(maybe): raise AssertionError, '%s: the count=%d does not match the number of elements=%d' % (name, count, len(maybe))" + end);
    outData.println(front + "  for rec in maybe:" + end);
    outData.println(front + "    if not name in rec._name(): raise AssertionError, '%s is not in %s' % (name, repr(rec._name()))" + end);
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      if (i % 20 == 0)
        outData.println("_NL , // ==== ==== ====");
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
        generateMethodCall(module, prototype, outData, outLog);
    }
    if (front.length() > 0)
    {
      outData.println("_NL };");
      outData.println("int " + module.name + "SupportCount = sizeof(" + module.name + "Support) / sizeof(char*);");
      outData.println("int " + module.name + "SupportLength()");
      outData.println("{");
      outData.println("  int result = 0;");
      outData.println("  for (int i=0; i<" + module.name + "SupportCount; i++)");
      outData.println("    result += strlen(" + module.name + "Support[i]);");
      outData.println("  return result;");
      outData.println("}");
      outData.println("#undef _NL");
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
  private static Vector<PythonArgs> pythonArgs;
  private static void generateMethodCall(Module module, Prototype prototype, PrintWriter outData, PrintWriter outLog)
  {
    pythonArgs = new Vector<PythonArgs>();
    String returnList = "";
    String resultComma = "";
    String innerComma = ", ";
    boolean hasReturn = false;
    boolean hasOutputs = false;
    if (prototype.type.reference == Type.BYVAL
    && prototype.type.typeof != Type.VOID)
      hasReturn = true;
    String callParms = "";
    String innerCallParms = "__handle__";
    //String innerCallParms = "";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      PythonArgs arg = new PythonArgs();
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
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (i != 0)
        callParms += ", ";
      callParms += arg.name;
      if (arg.isInput)
      {
        if (arg.listType == true && arg.sizeName.length() > 0)
        {
          //innerCallParms += ", _list" + (inpNo++);
          innerCallParms += innerComma + "_list" + (inpNo++);
          //innerComma = ", ";
        }
        else
        {
          //innerCallParms += ", " + arg.name;
          innerCallParms += innerComma + arg.name;
          //innerComma = ", ";
          if (arg.listType == true)
            innerCallParms += "._toList()";
        }
      }
      if (arg.isOutput)
      {
        hasOutputs = true;
        returnList += resultComma + arg.name;
        resultComma = ", ";
      }
    }
    outData.println(front+"def " + qualified(module.name, prototype.name) + "(" + callParms + "):"+end);
    outData.println(front+"  'returns: " + (hasReturn == true ? "rc, " : "") + returnList + "'"+end);
    inpNo = 0;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      outData.println(front+"  " + assertof(arg)+end);
    }
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isInput == true && arg.listType == true && arg.sizeName.length() > 0)
      {
        String listName = "_list" + (inpNo++);
        outData.println(front+"  " + listName + " = []"+end);
        outData.println(front+"  for n in range(" + arg.sizeName + "):"+end);
        outData.println(front+"    " + listName + ".append(" + arg.name + "[n]._toList())"+end);
      }
    }
    if (hasOutputs == true || hasReturn == true)
      outData.println(front+"  _result = " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")"+end);
    else
      outData.println(front+"  " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")"+end);
    int no = 0;
    if (hasReturn == true) no = 1;
    for (int i = 0; i < pythonArgs.size(); i++)
    {
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (arg.listType == false)
      {
        outData.println(front+"  " + arg.name + " = _result[" + (no) + "]"+end);
        no++;
      }
      else
      {
        if (arg.sizeName.length() == 0)
        {
          outData.println(front+"  _rec = " + arg.name + "._make()"+end);
          outData.println(front+"  _rec._fromList(_result[" + (no) + "])"+end);
          outData.println(front+"  " + arg.name + " = _rec"+end);
          no++;
        }
        else
        {
          outData.println(front+"  _mainList = _result[" + (no) + "]"+end);
          outData.println(front+"  _dataList = []"+end);
          outData.println(front+"  for _data in _mainList:"+end);
          outData.println(front+"    _rec = " + arg.name + "._make()"+end);
          outData.println(front+"    _rec._fromList(_data)"+end);
          outData.println(front+"    _dataList.append(_rec)"+end);
          outData.println(front+"  " + arg.name + " = _dataList"+end);
          no++;
        }
      }
    }
    if (returnList.length() > 0)
      outData.println(front + "  return " + (hasReturn == true ? "_result[0], " : "") + returnList + end);
    else if (hasReturn == true)
    {
      outData.println(front + "  return _result[0]" + end);
      outLog.println(qualified(module.name, prototype.name) + " is no longer a tuple return - by order Simon Bugslayer");
    }
    else
      outData.println(front + "  return" + end);
  }
  public static void generateDoxygenComment(Module module, Prototype prototype, PrintWriter outData, PrintWriter outLog)
  {
    pythonArgs = new Vector<PythonArgs>();
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
      PythonArgs arg = new PythonArgs();
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
      PythonArgs arg = new PythonArgs();
      arg.name = field.name;
      arg.listType = pyList(field);
      boolean reUsed = false;
      for (int j = 0; j < pythonArgs.size(); j++)
      {
        PythonArgs pa = (PythonArgs)pythonArgs.elementAt(j);
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
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
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
      PythonArgs arg = (PythonArgs)pythonArgs.elementAt(i);
      outData.println(" * \\param " + arg.name + ":" + arg.field.type.name);
    }
    outData.println(" * \\return " + (hasReturn == true ? "rc:" + prototype.type.name + ", " : "") + returnList + "");
    outData.println(" */");
  }
  private static String assertof(PythonArgs arg)
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
        return "if not isinstance(" + arg.name + ", int) and not isinstance(" + arg.name + ", long): raise AssertionError, '%s is not an instance of int or long' % ('" + arg.name + "')";
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
          return "list_assert('" + arg.field.type.name + "', " + arg.name + ", " + arg.sizeName + ")";
      default:
        return "if not '" + arg.field.type.name + "' in " + arg.name + "._name(): raise AssertionError, '%s is not in %s' % ('" + arg.field.type.name + "', repr(" + arg.name + "._name()))";
    }
    return "if not isinstance(" + arg.name + ", " + x + "): raise AssertionError, '%s is not an instance of %s' % ('" + arg.name + "', '" + x + "')";
  }
}

class PythonList
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  PythonList()
  {
    sizeName = "";
  }
}

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


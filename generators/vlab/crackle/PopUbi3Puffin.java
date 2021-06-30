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

public class PopUbi3Puffin extends Generator
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
      writer = new PrintWriter(outFile);
      try
      {
        writeln("// This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("// Mutilation, Spindlization and Bending will result in ...");
        writeln("#ifndef _" + module.name + "PYTHON_H_");
        writeln("#define _" + module.name + "PYTHON_H_");
        writeln("#include \"machine.h\"");
        writeln();
        writeln("#ifdef _DEBUG");
        writeln("  #define _DEBUG_SAVE_ _DEBUG");
        writeln("  #undef _DEBUG");
        writeln("#endif");
        writeln();
        writeln("#include \"Python.h\"");
        writeln();
        writeln("#ifdef inline");
        writeln("  #undef  inline");
        writeln("  #define inline inline");
        writeln("#endif");
        writeln();
        writeln("#ifdef _DEBUG_SAVE_");
        writeln("  #define _DEBUG _DEBUG_SAVE_");
        writeln("  #undef _DEBUG_SAVE_");
        writeln("#endif");
        writeln();
        writeln("extern PyMethodDef " + module.name + "Methods[];");
        writeln("extern char* " + module.name + "Support[];");
        writeln("extern int " + module.name + "SupportCount;");
        writeln("int " + module.name + "SupportLength();");
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
        writeln();
        writeln("#include <math.h>");
        writeln();
        writeln("#ifdef _DEBUG");
        writeln("  #define _DEBUG_SAVE_ _DEBUG");
        writeln("  #undef _DEBUG");
        writeln("#endif");
        writeln();
        writeln("#include \"Python.h\"");
        writeln("#include \"pythonrun.h\"");
        writeln();
        writeln("#ifdef inline");
        writeln("  #undef  inline");
        writeln("  #define inline inline");
        writeln("#endif");
        writeln();
        writeln("#ifdef _DEBUG_SAVE_");
        writeln("  #define _DEBUG _DEBUG_SAVE_");
        writeln("  #undef _DEBUG_SAVE_");
        writeln("#endif");
        writeln();
        writeln("#include \"pythonbuild.h\"");// must be before the shell
        writeln("#include \"" + module.name.toLowerCase() + "python.h\"");
        writeln();
        writeln("#include \"" + module.name.toLowerCase() + ".h\"");
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
    catch (Throwable e)
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
  static Vector pythonLists;
  public static String pyInputParm(Field field)
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
        Python3List list = new Python3List();
        list.name = "list" + pythonLists.size();
        list.field = field;
        list.isInput = true;
        pythonLists.addElement(list);
        writeln("    PyObject* " + list.name + ";");
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
        if (field.type.reference == Type.BYREFPTR)
          return field.name + ".data";
        else
          return field.name;
      default:
        for (int i = 0; i < pythonLists.size(); i++)
        {
          Python3List list = (Python3List)pythonLists.elementAt(i);
          if (field == list.field)
          {
            list.isOutput = true;
            return list.name;
          }
        }
        Python3List list = new Python3List();
        list.name = "list" + pythonLists.size();
        list.sizeName = sizeName;
        list.field = field;
        list.isOutput = true;
        pythonLists.addElement(list);
        if (sizeName.length() > 0)
          writeln("    PyObject* " + list.name + " = PyList_New(" + sizeName + ");");
        else
          writeln("    PyObject* " + list.name + " = PyList_New("+field.name+".build.count);");
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
    writeln("static PyObject* _" + prototype.name + "(PyObject *self, PyObject *args)");
    writeln("{");
    writeln("  TBChar errBuffer;");
    writeln("  try");
    writeln("  {");
    writeln("    int handle;");
    String varList = "i";
    String parmList = "&handle";
    String comma = ", ";
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field)prototype.parameters.elementAt(i);
      writeln("    " + pyArgs(field) + ";");
      if (field.isInput == true)
      {
        varList += pyInput(field);
        parmList += comma + pyInputParm(field);
      }
    }
    writeln("    if (!PyArg_ParseTuple(args, \"" + varList + "\", " + parmList + "))");
    writeln("      return 0;");
    writeln("    T" + module.name + " *instance = T" + module.name + "::Instance(handle);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3List list = (Python3List)pythonLists.elementAt(i);
      Field field = list.field;
      String extra = "";
      if (list.field.hasSize == true)
        extra = ", " + prototype.getInputSizeName(field.name);
      writeln("    if (" + field.name + ".fromList(" + list.name + extra + ") == 0)");
      writeln("      return 0;");
    }
    write("    ");
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
          else
            write("&" + field.name + ".rec");
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
      varList += pyOutput(field.type);
      parmList += ", " + pyOutputParm(field, sizeName);
    }
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3List list = (Python3List)pythonLists.elementAt(i);
      if (list.isOutput == true)
      {
        Field field = list.field;
        String extra = "";
        if (list.field.hasSize == true)
          extra = ", " + prototype.getOutputSizeName(field.name);
        writeln("    " + field.name + ".toList(" + list.name + extra + ");");
      }
    }
    writeln("    PyObject *retval = Py_BuildValue(\"" + varList +"s\""+ parmList + ", errBuffer.data);");
    for (int i = 0; i < pythonLists.size(); i++)
    {
      Python3List list = (Python3List)pythonLists.elementAt(i);
      if (list.isOutput == true && list.isInput == false)
        writeln("    Py_XDECREF(" + list.name + ");");
    }
    writeln("    return retval;");
    writeln("  }");
    writeln("  catch (xDBException &ex1)");
    writeln("  {");
    writeln("    errBuffer.append(\"DBException: \");");
    writeln("    errBuffer.append(ex1.ErrorStr());");
    writeln("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    writeln("    return 0;");
    writeln("  }");
    writeln("  catch (xCept &ex2)");
    writeln("  {");
    writeln("    errBuffer.append(\"Exception: \");");
    writeln("    errBuffer.append(ex2.ErrorStr());");
    writeln("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    writeln("    return 0;");
    writeln("  }");
    writeln("  catch (...)");
    writeln("  {");
    writeln("    errBuffer.append(\"Catch ...\");");
    writeln("    PyErr_SetString(PyExc_Exception, errBuffer.data);");
    writeln("    return 0;");
    writeln("  }");
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
    String w1 = "{ ";
    writeln("PyMethodDef "+module.name+"Methods[] =");
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
      {
        writeln(w1 + padder("{\"_" + prototype.name + "\",", 35) + padder("_" + prototype.name + ",", 35) + "METH_VARARGS}");
        w1 = ", ";
      }
    }
    writeln(", {0, 0}");
    writeln("};");
  }
  static String front = "_NL \"";
  static String end = "\"";
  public static void generateMethodsCall(Module module, PrintWriter outLog)
  {
    if (front.length() > 0)
    {
      writeln("#define _NL \"\\n\"");
      writeln("char *" + module.name + "Support[] = {\"import " + module.name.toLowerCase() + "Methods\" _NL");
    }
    else
      writeln("import " + module.name.toLowerCase() + "Methods");
    writeln(front + "class " + module.name + "Error:" + end);
    writeln(front + "  def __init__(self, value):" + end);
    writeln(front + "    self.value = value" + end);
    writeln(front + "  def __str__(self):" + end);
    writeln(front + "    return repr(self.value)" + end);
    writeln(front + "def list_assert(name, maybe, count):" + end);
    writeln(front + "  if maybe == None:" + end);
    writeln(front + "    if (count != 0) : raise AssertionError('%s: list count != 0 in None case' % (name))" + end);
    writeln(front + "    return" + end);
    writeln(front + "  if isinstance(maybe, list) == False:" + end);
    writeln(front + "    if not name in maybe._name(): raise AssertionError('%s is not in %s' % (name, repr(maybe._name())))" + end);
    writeln(front + "    return" + end);
    writeln(front + "  if count != len(maybe): raise AssertionError('%s: the count=%d does not match the number of elements=%d' % (name, count, len(maybe)))" + end);
    writeln(front + "  for rec in maybe:" + end);
    writeln(front + "    if not name in rec._name(): raise AssertionError('%s is not in %s' % (name, repr(rec._name())))" + end);
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      if (i % 20 == 0)
        writeln("_NL , // ==== ==== ====");
      Prototype prototype = (Prototype)module.prototypes.elementAt(i);
      if (prototype.isRpcCall() || prototype.isExtendedRpcCall())
        generateMethodCall(module, prototype, outLog);
    }
    if (front.length() > 0)
    {
      writeln("_NL };");
      writeln("int " + module.name + "SupportCount = sizeof(" + module.name + "Support) / sizeof(char*);");
      writeln("int " + module.name + "SupportLength()");
      writeln("{");
      writeln("  int result = 0;");
      writeln("  for (int i=0; i<" + module.name + "SupportCount; i++)");
      writeln("    result += strlen(" + module.name + "Support[i]);");
      writeln("  return result;");
      writeln("}");
      writeln("#undef _NL");
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
  private static Vector Python3Args;
  private static void generateMethodCall(Module module, Prototype prototype, PrintWriter outLog)
  {
    Python3Args = new Vector();
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
      Python3Args arg = new Python3Args();
      arg.field = field;
      arg.isInput = field.isInput;
      arg.isOutput = field.isOutput;
      arg.name = field.name;
      arg.listType = pyList(field);
      if (field.isInput == true && field.hasSize == true)
        arg.sizeName = field.input.getSizeName();
      else if (field.hasSize == true)
        arg.sizeName = field.output.getSizeName();
      Python3Args.addElement(arg);
    }
    int inpNo = 0;
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
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
    writeln(front+"def " + qualified(module.name, prototype.name) + "(" + callParms + "):"+end);
    writeln(front+"  'returns: " + (hasReturn == true ? "rc, " : "") + returnList + "'"+end);
    inpNo = 0;
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
      writeln(front+"  " + assertof(arg)+end);
    }
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
      if (arg.isInput == true && arg.listType == true && arg.sizeName.length() > 0)
      {
        String listName = "_list" + (inpNo++);
        writeln(front+"  " + listName + " = []"+end);
        writeln(front+"  for n in range(" + arg.sizeName + "):"+end);
        writeln(front+"    " + listName + ".append(" + arg.name + "[n]._toList())"+end);
      }
    }
    if (hasOutputs == true || hasReturn == true)
      writeln(front+"  _result = " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")"+end);
    else
      writeln(front+"  " + module.name.toLowerCase() + "Methods._" + prototype.name + "(" + innerCallParms + ")"+end);
    int no = 0;
    if (hasReturn == true) no = 1;
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
      if (arg.isOutput == false)
        continue;
      if (arg.listType == false)
      {
        writeln(front+"  " + arg.name + " = _result[" + (no) + "]"+end);
        no++;
      }
      else
      {
        if (arg.sizeName.length() == 0)
        {
          writeln(front+"  _rec = " + arg.name + "._make()"+end);
          writeln(front+"  _rec._fromList(_result[" + (no) + "])"+end);
          writeln(front+"  " + arg.name + " = _rec"+end);
          no++;
        }
        else
        {
          writeln(front+"  _mainList = _result[" + (no) + "]"+end);
          writeln(front+"  _dataList = []"+end);
          writeln(front+"  for _data in _mainList:"+end);
          writeln(front+"    _rec = " + arg.name + "._make()"+end);
          writeln(front+"    _rec._fromList(_data)"+end);
          writeln(front+"    _dataList.append(_rec)"+end);
          writeln(front+"  " + arg.name + " = _dataList"+end);
          no++;
        }
      }
    }
    if (returnList.length() > 0)
      writeln(front + "  return " + (hasReturn == true ? "_result[0], " : "") + returnList + end);
    else if (hasReturn == true)
    {
      writeln(front + "  return _result[0]" + end);
      outLog.println(qualified(module.name, prototype.name) + " is no longer a tuple return - by order Simon Bugslayer");
    }
    else
      writeln(front + "  return" + end);
  }
  public static void generateDoxygenComment(Module module, Prototype prototype, PrintWriter outLog)
  {
    Python3Args = new Vector();
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
      Python3Args arg = new Python3Args();
      arg.field = field;
      arg.isInput = true;
      arg.name = field.name;
      arg.listType = pyList(field);
      Python3Args.addElement(arg);
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
      Python3Args arg = new Python3Args();
      arg.name = field.name;
      arg.listType = pyList(field);
      boolean reUsed = false;
      for (int j = 0; j < Python3Args.size(); j++)
      {
        Python3Args pa = (Python3Args)Python3Args.elementAt(j);
        if (pa.field == field)
        {
          arg = pa;
          reUsed = true;
          break;
        }
      }
      if (reUsed == false)
        Python3Args.addElement(arg);
      arg.field = field;
      arg.isOutput = true;
      arg.sizeName = sizeName;
    }
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
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
    for (int i = 0; i < Python3Args.size(); i++)
    {
      Python3Args arg = (Python3Args)Python3Args.elementAt(i);
      writeln(" * \\param " + arg.name + ":" + arg.field.type.name);
    }
    writeln(" * \\return " + (hasReturn == true ? "rc:" + prototype.type.name + ", " : "") + returnList + "");
    writeln(" */");
  }
  private static String assertof(Python3Args arg)
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
        return "if not isinstance(" + arg.name + ", int) and not isinstance(" + arg.name + ", long): raise AssertionError('%s is not an instance of int or long' % ('" + arg.name + "'))";
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
        return "if not '" + arg.field.type.name + "' in " + arg.name + "._name(): raise AssertionError('%s is not in %s' % ('" + arg.field.type.name + "', repr(" + arg.name + "._name())))";
    }
    return "if not isinstance(" + arg.name + ", " + x + "): raise AssertionError('%s is not an instance of %s' % ('" + arg.name + "', '" + x + "'))";
  }
}

class Python3List
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  Python3List()
  {
    sizeName = "";
  }
}

class Python3Args
{
  String name;
  Field field;
  String sizeName;
  boolean isInput = false;
  boolean isOutput = false;
  boolean listType = false;
  Python3Args()
  {
    sizeName = "";
  }
}


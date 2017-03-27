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

public class PopUbiModule extends Generator
{
  public static String description()
  {
    return "Generates Ubiquitous Module Code (AIX|SUN|NT)";
  }
  public static String documentation()
  {
    return "Generates Ubiquitous Module Code (AIX|SUN|NT)"
     + "\r\nHandles following pragmas"
     + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
      ;
  }
  private static void setupPragmaVector()
  {
    if (pragmaVector == null) 
    {
      pragmaVector = new Vector<Pragma>();
      pragmaVector.addElement(new Pragma("AlignForSun", false, "Ensure that all fields are on 8 byte boundaries."));
    }
  }
  {
    setupPragmaVector();
  }
  private static boolean alignForSun;
  private static void setPragmas(Module module)
  {
    // Ensure these are in the same order as above
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
  * - C Server Marshaling code
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    setPragmas(module);
    generateCHeader(module, output, outLog);
    if (generateCModule(module, output, outLog))
      generateCCode(module, output, outLog);
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
        outData.println("#include \"swapbytes.h\"");
        outData.println("#include \"ubidata.h\"");
        outData.println("#include \"ubimodule.h\"");
        //outData.println("#include \"logfile.h\"");
        //outData.println("#include \"pgapi.h\"");
        outData.println("#include \"pgaspect.h\"");
        outData.println("#include \"handles.h\"");
        outData.println();
        PopUbiGen.generateCExterns(module, outData);
        outData.println("class I" + module.name/* + ": public PgAspect"*/);
        outData.println("{");
        //outData.println("protected: PgAspect* aspect;");
        outData.println("public:");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure)module.structures.elementAt(i);
          if (structure.codeType != Structure.PUBLIC)
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
        outData.println("class T" + module.name + " : public I" + module.name);
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
        outData.println("protected:");
        outData.println("  int instance;");
        outData.println("  tLogFile* logFile;");
        outData.println("  TJConnector* connect;");
        outData.println("public:");
        outData.println("  void Setup(tLogFile* logFile= 0, TJConnector* connect = 0) {this->logFile = logFile; this->connect = connect;}");
        outData.println("  typedef tHandle<T" + module.name + "*, 100, 100> T" + module.name + "Handle;");
        outData.println("  static T" + module.name + "Handle " + module.name + "Handle;");
        outData.println("  T" + module.name + "(){instance = " + module.name + "Handle.Create(this);Setup();}");
        outData.println(" ~T" + module.name + "(){" + module.name + "Handle.Release(instance, 0);}");
        outData.println("  static T" + module.name + "* Instance(int instance=100)");
        outData.println("  {");
        outData.println("    T" + module.name + "* result = (T" + module.name + "*) " + module.name + "Handle.Use(instance);");
        outData.println("    return result;");
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
        outData.println("class T" + module.name + "Module : public UbiModule");
        outData.println("{");
        outData.println("public:");
        outData.println("  T" + module.name + " *" + module.name + ";");
        outData.println("  T" + module.name + "Module();");
        outData.println("  ~T" + module.name + "Module();");
        outData.println("  static UbiMessage messages[];");
        outData.println("  UbiData* Dispatch(const UbiData *input);");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          outData.println("  int32 " + prototype.name + "(char *ip);");
        }
        outData.println("  UbiMessage* Messages(int &noOf);");
        outData.println("  bool HasMessage(int reqID, int signature);");
        outData.println("  void Setup(PgAspect *aspect) {" + module.name + "->Setup(aspect->logFile, aspect->connect);}");
        outData.println("  void Clear();");
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
  private static void generateCCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+".cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+".cpp");
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
        outData.println();
        outData.println("T" + module.name + "::T" + module.name + "Handle T" + module.name + "::" + module.name + "Handle;");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.code.size() > 0)
          {
            PopUbiGen.generateUbiCImplCode(module, prototype, outData);
            outData.println("{");
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
  /**
  * Sets up the writer and generates the general stuff
  */
  private static boolean generateCModule(Module module, String output, PrintWriter outLog)
  {
    boolean hasCode = false;
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"module.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"module.cpp");
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
        String comma = "{ ";
        outData.println("UbiMessage T" + module.name + "Module::messages[] = ");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          String reqID = toHex(i);
          if (prototype.message.length() > 0)
            reqID = toHex(prototype.message);
          outData.println(comma + " UbiMessage(" + reqID + ", " + toHex((int)prototype.signature(true)) + ") // "+ prototype.name);
          comma = ", ";
        }
        outData.println("};");
        outData.println();
        outData.println("T" + module.name + "Module::T" + module.name + "Module()");
        outData.println("{");
        outData.println("  " + module.name + " = new T" + module.name + "();");
        outData.println("  output = new UbiData();");
        outData.println("}");
        outData.println();
        outData.println("T"+module.name+"Module::~T"+module.name+"Module()");
        outData.println("{");
        outData.println("}");
        outData.println();
        outData.println("extern \"C\" OS_DECLSPEC UbiModule* " + module.name + "construct()");
        outData.println("{");
        outData.println("  UbiModule *result = new T" + module.name + "Module();");
        outData.println("  return result;");
        outData.println("}");
        outData.println("");
        outData.println("extern \"C\" OS_DECLSPEC void " + module.name + "destruct(UbiModule *& module)");
        outData.println("{");
        outData.println("  delete module;");
        outData.println("  module = 0;");
        outData.println("}");
        outData.println();
        outData.println("UbiData* T" + module.name + "Module::Dispatch(const UbiData *input)");
        outData.println("{");
        outData.println("  char* ip = (char*)input->data;");
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
            outData.print("    case "+toHex(prototype.message)+":");
          else
            outData.print("    case "+toHex(i)+":");
          outData.print(" result = "+prototype.name+"(ip);");
          outData.println(" break;");
        }
        outData.println("  default:");
        outData.println("    return output;");
        outData.println("  }");
        outData.println("  output->Use((unsigned char *)replyBody, replySize, result);");
        outData.println("  return output;");
        outData.println("}");
        outData.println();
        outData.println("void T" + module.name + "Module::Clear()");
        outData.println("{");
        outData.println("  output->Clear();");
        outData.println("}");
        outData.println();
        outData.println("UbiMessage* T" + module.name + "Module::Messages(int &noOf)");
        outData.println("{");
        outData.println("  noOf = sizeof(messages) / sizeof(UbiMessage);");
        outData.println("  return messages;");
        outData.println("}");
        outData.println();
        outData.println("bool T" + module.name + "Module::HasMessage(int reqID, int signature)");
        outData.println("{");
        outData.println("  switch (reqID)");
        outData.println("  {");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.message.length() > 0)
            outData.print("    case " + toHex(prototype.message) + ":");
          else
            outData.print("    case " + toHex(i) + ":");
          outData.println(" return signature == " + toHex((int)prototype.signature(true)) + "; // "+ prototype.name);
        }
        outData.print("    default: return false;");
        outData.println("  }");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCModule(module, prototype, i, outData);
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
  private static void generateCModule(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    outData.println("int T" + module.name + "Module::" + prototype.name + "(char *ip)");
    outData.println("{");
    outData.println("  int resultCode = 0;");
    outData.println("  try");
    outData.println("  {");
    outData.println("    replySize = 0;replyBody = 0;");
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
            outData.println("    recvSize = *(int32*)ip;");
            outData.println("    SwapBytes(recvSize);");
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof(int32));");
            else
              outData.println("    ip += sizeof(int32);");
            outData.println("    char* "+field.name+" = ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(recvSize);");
              else
                outData.println("    ip += recvSize;");
            }
          }
          else
          {
            outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(sizeof(*"+field.name+"));");
              else
                outData.println("    ip += sizeof(*"+field.name+");");
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
          outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
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
        outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(*"+field.name+"));");
          else
            outData.println("    ip += sizeof(*"+field.name+");");
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
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        if (alignForSun == true)
          outData.println("    replySize += alignData((int32)sizeof("+prototype.type.cNameUbi()+")); // result Size");
        else
          outData.println("    replySize += (int32)sizeof("+prototype.type.cNameUbi()+"); // result Size");
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
            outData.println("#error unsized char* on output");
            errLog.println("#error unsized char* on output");
          }
          else
          {
            if (alignForSun == true)
              outData.println("    replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            else
              outData.println("    replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
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
            outData.println("    replySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
          else
            outData.println("    replySize += ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
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
            outData.println("    replySize += alignData(sizeof("+field.type.cNameUbi()+"*)); // Variant "+field.name);
          else
            outData.println("    replySize += sizeof("+field.type.cNameUbi()+"*); // Variant "+field.name);
        }
      }
      else
      {
        if (alignForSun == true)
          outData.println("    replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
        else
          outData.println("    replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
      }
    }
    if (hasOutput)
    {
      if (Variants > 0)
      {
        outData.println("    int32 Variants["+Variants+"];");
        outData.println("    int32 VariantSize["+Variants+"];");
        Variants = 0;
      }
      outData.println("    replyBody = new char [replySize];");
      outData.println("    memset(replyBody, 0, replySize);");
      outData.println("    ip = replyBody;");
    }
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        outData.println("    "+prototype.type.cNameUbi()+"* result = ("+prototype.type.cNameUbi()+"*) ip;");
        if (prototype.outputs.size() > 0)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof("+prototype.type.cNameUbi()+"));");
          else
            outData.println("    ip += sizeof("+prototype.type.cNameUbi()+");");
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
              outData.println("    memcpy(ip, "+field.name+", sizeof("+field.type.cNameUbi()+"));");
              outData.println("    "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            }
            else
              outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(sizeof("+field.type.cNameUbi()+"));");
              else
                outData.println("    ip += sizeof("+field.type.cNameUbi()+");");
            }
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          outData.println("    *(int32*)ip = ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+"));");
          outData.println("    SwapBytes(*(int32*)ip);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32));");
          else
            outData.println("    ip += sizeof(int32);");
          if (input != null)
          {
            outData.println("    memcpy(ip, "+field.name+", ("+w1+op.name+" * sizeof("+field.type.cNameUbi()+")));");
            outData.println("    "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
          }
          else
            outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData("+w1+op.name+" * sizeof("+field.type.cNameUbi()+"));");
            else
              outData.println("    ip += ("+w1+op.name+" * sizeof("+field.type.cNameUbi()+"));");
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
            outData.println("#error Constant Size with BYREFPTR output");
          outData.println("    Variants["+(Variants++)+"] = (int32)(ip-replyBody);");
          if (input != null)
            outData.println("    ("+field.type.cNameUbi()+"*)*ip = "+field.name+";");
          else
            outData.println("    "+field.type.cNameUbi()+"** "+field.name+" = ("+field.type.cNameUbi()+"**)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof("+field.type.cNameUbi()+"*));");
            else
              outData.println("    ip += sizeof("+field.type.cNameUbi()+"*);");
          }
        }
      }
      else
      {
        if (input != null)
        {
          outData.println("    memcpy(ip, "+field.name+", sizeof("+field.type.cNameUbi()+"));");
          outData.println("    "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        }
        else
          outData.println("    "+field.type.cNameUbi()+"* "+field.name+" = ("+field.type.cNameUbi()+"*)ip;");
        if (addUp)
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof("+field.type.cNameUbi()+"));");
          else
            outData.println("    ip += sizeof("+field.type.cNameUbi()+");");
        }
      }
    }
    outData.println("    try");
    outData.println("    {");
    String w1 = "", w2 = "";
    outData.print("      ");
    if (hasresult)
      outData.print("*result = ");
    outData.print(module.name+"->"+prototype.name+"(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field) prototype.parameters.elementAt(i);
      if (field.type.reference == Type.BYPTR)
        w2 = "";
      else
        w2 = "*";
      outData.print(w1 + w2 + field.name);
      w1 = ", ";
    }
    outData.println(");");
    outData.println("    }");
    outData.println("    catch (int rc)");
    outData.println("    {");
    outData.println("      resultCode = rc;");
    outData.println("    }");
    if (hasresult && prototype.needsSwap())
      outData.println("    SwapBytes(*result);");
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
      ||  prototype.type.reference == Type.BYPTR)
      {
        if (prototype.type.reference != Type.BYPTR)
        {
          if (alignForSun == true)
            outData.println("    replySize += alignData((int32)sizeof("+prototype.type.cNameUbi()+")); // result Size");
          else
            outData.println("    replySize += (int32)sizeof("+prototype.type.cNameUbi()+"); // result Size");
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
                outData.println("    replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
              else
                outData.println("    replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
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
              outData.println("    replySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            }
            else
            {
              outData.println("    replySize += sizeof(int32);  // size of Block");
              outData.println("    replySize += ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+")); // size "+field.name);
            }
            if (field.type.reference == Type.BYREFPTR)
              outData.println("    VariantSize["+(VariantSize++)+"] = ("+w1+""+op.name+" * sizeof("+field.type.cNameUbi()+"));");
          }
        }
        else
        {
          if (alignForSun == true)
            outData.println("    replySize += alignData(sizeof("+field.type.cNameUbi()+")); // size "+field.name);
          else
            outData.println("    replySize += sizeof("+field.type.cNameUbi()+"); // size "+field.name);
        }
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
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
      outData.println("      for (int i = 0; i < "+Variants+"; i++)");
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
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
        if (field == null)
          continue;
        Operation op = output.sizeOperation();
        generateCNonStructSwaps(module, prototype, field, op, outData);
      }
    }
    outData.println("  }");
    outData.println("  catch (int rc)");
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

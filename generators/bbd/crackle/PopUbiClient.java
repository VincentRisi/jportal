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

public class PopUbiClient extends Generator
{
  public static String description()
  {
    return "Generates Client C Dll and C++ class Code";
  }
  public static String documentation()
  {
    return "Generates Client Code"
      + "\r\nHandles following pragmas"
      + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
      ;
  }
  private static boolean alignForSun = false;
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
  * - C Header for Client
  * - C Client
  * - C Server Marshalling code
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
        alignForSun = true;
    }
    generateCClient(module, output, outLog);
    generateCClientHeader(module, output, outLog);
  }
  /**
  * Sets up the writer and generates the general stuff
  */
  private static void generateCClientHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"client.h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"client.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("// Mutilation, Spindlization and Bending will result in ...");
        outData.println("#ifndef _"+module.name+"CLIENT_H_");
        outData.println("#define _"+module.name+"CLIENT_H_");
        outData.println("#include \"machine.h\"");
        outData.println();
        outData.println("#include \"swapbytes.h\"");
        outData.println();
        outData.println("extern char *"+module.name+"Version;");
        outData.println("extern int32 "+module.name+"Signature;");
        PopUbiGen.generateCExterns(module, outData);
        outData.println();
        PopUbiGen.generateCStructs(module, outData, true);
        outData.println();
        outData.println("class T"+module.name+"Interface");
        outData.println("{");
        outData.println("public:");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          PopUbiGen.generateCInterface(module, prototype, outData);
        }
        outData.println("};");
        outData.println();
        outData.println("class T" + module.name + " : public T" + module.name + "Interface");
        outData.println("{");
        outData.println("public:");
        outData.println("  int32 Handle;");
        outData.println("  e"+module.name+" errorCode;");
        outData.println("  bool loggedOn;");
        outData.println("  char fErrBuffer[4096];");
        outData.println("  char fErrorDesc[256];");
        outData.println("private:");
        outData.println("  static T" + module.name + "* instance;");
        outData.println("public:");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype)module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          PopUbiGen.generateCHeader(module, prototype, outData);
        }
        outData.println("  static T" + module.name + "* Instance() {return instance;}");
        outData.println("  T" + module.name + "() {loggedOn = false; instance=this;}");
        outData.println("  ~T"+module.name+"() {if (loggedOn) Logoff();}");
        outData.println("  int32 getHandle();");
        outData.println("  void Logon(char* Service, char* Host, int32 Timeout=150000);");
        outData.println("  void Logoff();");
        outData.println("  char* ErrBuffer() {return ErrBuffer(fErrBuffer, sizeof(fErrBuffer));}");
        outData.println("  char* ErrorDesc() {return ErrorDesc(fErrorDesc, sizeof(fErrorDesc));}");
        outData.println("  char* ErrorDescForNo(e"+module.name+" ErrorNo) { return ErrorDescForNo(ErrorNo, fErrorDesc, sizeof(fErrorDesc));}");
        outData.println("  char* ErrorDescForNo(e"+module.name+" ErrorNo, char *Buffer, int32 BufferLen);");
        outData.println("  char* ErrBuffer(char *Buffer, int32 BufferLen);");
        outData.println("  char* ErrorDesc(char *Buffer, int32 BufferLen);");
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
  private static void generateCClient(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String w1 = "";
      outLog.println("Code: "+output+module.name.toLowerCase()+"client.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"client.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#include \"machine.h\"");
        outData.println("#include \"xcept.h\"");
        outData.println("#include \"xstring.h\"");
        outData.println();
        outData.println("char *"+module.name+"Version = "+module.version+";");
        outData.println("int32 "+module.name+"Signature = "+module.signature+";");
        outData.println();
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println();
        outData.println("#include \"swapbytes.h\"");
        outData.println("#include \"ubiclient.h\"");
        outData.println("#include \"handles.h\"");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+"client.h\"");
        outData.println();
        outData.println("char *"+module.name+"Errors[] = ");
        outData.println("{ \"No Error\"");
        for (int i = 0; i < module.messages.size(); i++)
        {
          Message message = (Message) module.messages.elementAt(i);
          outData.println(", "+message.value+"   // "+message.name);
        }
        outData.println(", \"Invalid Signature\"");
        outData.println(", \"Invalid Logon Cookie\"");
        outData.println(", \"Invalid INI File\"");
        outData.println(", \"Uncaught DB Connect Error\"");
        outData.println(", \"Unknown function call\"");
        outData.println(", \"Snap Creation Error\"");
        outData.println(", \"AddList (Index == 0) != (List == 0) Error\"");
        outData.println(", \"AddList (realloc == 0) Error\"");
        outData.println(", \"Last error not in message table\"");
        outData.println("};");
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
        if (alignForSun == true)
        {
          outData.println("inline int32 alignData(int32 size)");
          outData.println("{");
          outData.println("  int32 n = size % 8;");
          outData.println("  if (n > 0) size += (8-n);");
          outData.println("  return size;");
          outData.println("}");
          outData.println();
        }
        outData.println("struct T"+module.name+"UbiCB");
        outData.println("{");
        outData.println("  UbiClient* RPC;");
        outData.println("  char *sendBuff;");
        outData.println("  int32 sendBuffLen;");
        outData.println("  int32 recvSize;");
        outData.println("  e"+module.name+" result;");
        outData.println("  int32 RC;");
        Vector<String> retrievals = new Vector<String>();
        Vector<String> submittals = new Vector<String>();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          for (int j = 0; j < prototype.parameters.size(); j++)
          {
            Field parameter = (Field) prototype.parameters.elementAt(j);
            if (parameter.type.typeof != Type.CHAR
            && (parameter.type.reference == Type.BYPTR
                || parameter.type.reference == Type.BYREFPTR))
            {
              if (prototype.hasOutputSize(parameter.name)
              ||  prototype.hasInputSize(parameter.name))
              {
                if (parameter.type.reference == Type.BYREFPTR)
                  outData.println("  "+parameter.type.cDefUbiRefPAsP(prototype.name+parameter.name)+";");
                else
                  outData.println("  "+parameter.type.cDefUbi(prototype.name+parameter.name)+";");
              }
              if (prototype.hasOutputSize(parameter.name))
                retrievals.addElement(prototype.name+parameter.name);
              if (prototype.hasInputSize(parameter.name))
                submittals.addElement(prototype.name+parameter.name);
            }
          }
        }
        outData.println("  T"+module.name+"UbiCB(char* Service, char* Host, int32 Timeout)");
        w1 = ":";
        // There generally wont be the same retrieval and submittal,
        // even if there is it should not really matter much. If it
        // does then we will change this code.
        for (int i = 0; i < retrievals.size(); i++)
        {
          String s = (String) retrievals.elementAt(i);
          outData.println("  "+w1+" "+s+"(0)");
          w1 = ",";
        }
        outData.println("  {");
        outData.println("    RPC = new UbiClient(Host, Service, Timeout);");
        outData.println("  }");
        outData.println("  ~T"+module.name+"UbiCB()");
        outData.println("  {");
        outData.println("    delete RPC;");
        outData.println("  }");
        outData.println("};");
        outData.println();
        outData.println("const int CookieStart = 2329;");
        outData.println("const int NoCookies = 32;");
        outData.println("static tHandle<T"+module.name+"UbiCB*, CookieStart, NoCookies> "+module.name+"CBHandle;");
        outData.println();
        outData.println("e"+module.name+" "+module.name+"SnapLogon(int32* Handle, char* Service, char* Host, int32 Timeout)");
        outData.println("{");
        outData.println("  try");
        outData.println("  {");
        outData.println("    *Handle = "+module.name+"CBHandle.Create(new T"+module.name+"UbiCB(Service, Host, Timeout));");
        outData.println("    return "+module.name.toUpperCase()+"_OK;");
        outData.println("  }");
        outData.println("  catch (xCept &x)");
        outData.println("  {");
        outData.println("    *Handle = -1;");
        outData.println("    return "+module.name.toUpperCase()+"_SNAP_ERROR;");
        outData.println("  }");
        outData.println("}");
        outData.println();
        outData.println("e"+module.name+" "+module.name+"SnapLogoff(int32* Handle)");
        outData.println("{");
        outData.println("  if (*Handle < CookieStart || *Handle >= CookieStart+NoCookies)");
        outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
        outData.println("  "+module.name+"CBHandle.Release(*Handle);");
        outData.println("  *Handle = -1;");
        outData.println("  return "+module.name.toUpperCase()+"_OK;");
        outData.println("}");
        outData.println();
        outData.println("void " + module.name + "SnapThrowOnError(int32 Handle, char *file=0, int line=0)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return;");
        outData.println("  T" + module.name + "UbiCB* ubiCB = " + module.name + "CBHandle.Use(Handle);");
        outData.println("  if (ubiCB->RPC->ErrSize())");
        outData.println("  {");
        outData.println("    xUbiException error(file, line, ubiCB->RPC->ErrBuffer());");
        outData.println("    ubiCB->RPC->ErrFree();");
        outData.println("    throw error;");
        outData.println("  }");
        outData.println("  return;");
        outData.println("}");
        outData.println();
        outData.println("e" + module.name + " " + module.name + "SnapErrBuffer(int32 Handle, char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
        outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  if (ubiCB->RPC->ErrSize())");
        outData.println("    strncpy(Buffer, ubiCB->RPC->ErrBuffer(), BufferLen);");
        outData.println("  else");
        outData.println("    strcpy(Buffer, \"\");");
        outData.println("  for(int32 i = strlen(Buffer); i < BufferLen; i++)");
        outData.println("    Buffer[i]  = ' ';");
        outData.println("  return "+module.name.toUpperCase()+"_OK;");
        outData.println("}");
        outData.println();
        outData.println("void "+module.name+"SnapErrorDesc(e"+module.name+" RC, char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  char W1[20]=\"\";");
        outData.println("  if (RC < "+module.name.toUpperCase()+"_OK");
        w1 = "RC";
        if (module.messageBase > 0)
        {
          outData.println("  || (RC > "+module.name.toUpperCase()+"_OK && RC < "+(module.messageBase)+")");
          w1 = "(RC?RC-("+module.messageBase+"-1):0)";
        }
        outData.println("  ||  RC > "+module.name.toUpperCase()+"_LAST_LAST)");
        outData.println("  {");
        outData.println("    sprintf(W1, \" (RC=%d)\", RC);");
        outData.println("    RC = "+module.name.toUpperCase()+"_LAST_LAST;");
        outData.println("  }");
        outData.println("  int32 n = (int32)"+w1+";");
        outData.println("  strncpy(Buffer, "+module.name+"Errors[n], BufferLen-(strlen(W1)+1));");
        outData.println("  if (RC == "+module.name.toUpperCase()+"_LAST_LAST)");
        outData.println("    strcat(Buffer, W1);");
        outData.println("  for(int32 i = strlen(Buffer); i < BufferLen; i++)");
        outData.println("    Buffer[i]  = ' ';");
        outData.println("}");
        outData.println();
        outData.println("void "+module.name+"SnapVersion(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  strncpy(Buffer, "+module.name+"Version, BufferLen);");
        outData.println("  for(int32 i = strlen(Buffer); i < BufferLen; i++)");
        outData.println("    Buffer[i]  = ' ';");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCClient(module, prototype, i, outData);
        }
        outData.println("T" + module.name + "* T" + module.name + "::instance;");
        outData.println("int32 T"+module.name+"::getHandle()");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    throw "+module.name.toUpperCase()+"_INV_COOKIE;");
        outData.println("  return Handle;");
        outData.println("}");
        outData.println();
        outData.println("void T"+module.name+"::Logon(char* Service, char* Host, int32 Timeout)");
        outData.println("{");
        outData.println("  errorCode = "+module.name+"SnapLogon(&Handle, Service, Host, Timeout);");
        outData.println("  if (errorCode != 0)");
        outData.println("  {");
        outData.println("    Handle = -1;");
        outData.println("    throw errorCode;");
        outData.println("  }");
        outData.println("  loggedOn = true;");
        outData.println("}");
        outData.println();
        outData.println("void T"+module.name+"::Logoff()");
        outData.println("{");
        outData.println("  if (loggedOn == false)");
        outData.println("    return;");
        outData.println("  loggedOn = false;");
        outData.println("  errorCode = "+module.name+"SnapLogoff(&Handle);");
        outData.println("  if (errorCode != 0)");
        outData.println("  {");
        outData.println("    Handle = -1;");
        outData.println("    throw errorCode;");
        outData.println("  }");
        outData.println("}");
        outData.println();
        outData.println("char* T"+module.name+"::ErrBuffer(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  "+module.name+"SnapErrBuffer(getHandle(), Buffer, BufferLen-1);");
        outData.println("  Buffer[BufferLen-1] = 0;");
        outData.println("  return strtrim(Buffer);");
        outData.println("}");
        outData.println();
        outData.println("char* T"+module.name+"::ErrorDesc(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  "+module.name+"SnapErrorDesc(errorCode, Buffer, BufferLen-1);");
        outData.println("  Buffer[BufferLen-1] = 0;");
        outData.println("  return strtrim(Buffer);");
        outData.println("}");
        outData.println();
        outData.println("char* T"+module.name+"::ErrorDescForNo(e"+module.name+" ErrorNo, char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  "+module.name+"SnapErrorDesc(ErrorNo, Buffer, BufferLen-1);");
        outData.println("  Buffer[BufferLen-1] = 0;");
        outData.println("  return strtrim(Buffer);");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCClientImp(module, prototype, outData);
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
    }
    catch (Exception e)
    {
      System.out.println(e.toString ());
      System.out.flush();
      e.printStackTrace();
    }
  }
  /**
  * Generates the prototypes defined
  */
  private static int n=0;
  private static void generateCSwaps(Module module, Prototype prototype, Field field, Operation op, PrintWriter outData)
  {
    String w1 = "", w2 = "", w3 = ".", w4 = "", w5 = "", w6 = "";
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
        Field opField = prototype.getParameter(op.name);
        if (opField != null
        && (opField.type.reference == Type.BYPTR
        ||  opField.type.reference == Type.BYREFPTR))
          w5 = "*";
        n++;
        outData.println("  "+w2+"{for (int i_"+n+" = 0; i_"+n+" < "+w5+op.name+"; i_"+n+"++)");
        w1 = w1 + "[i_"+n+"]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
        w6 = w6+"}";
      }
      for (int j=0; j < field.type.arraySizes.size(); j++)
      {
        Integer integer = (Integer) field.type.arraySizes.elementAt(j);
        n++;
        outData.println("    "+w2+"{for (int i_"+n+"_"+j+" = 0; i_"+n+"_"+j
                        + " < " + integer.intValue()
                        + "; i_"+n+"_"+j+"++)");
        w1 = w1 + "[i_"+n+"_"+j+"]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
        w6 = w6+"}";
      }
    }
    if (field.isStruct(module))
      outData.println("    "+w2+field.name+w1+w3+"Swaps();"+w6);
    else if (field.needsSwap())
      outData.println("    "+w2+"SwapBytes("+w4+field.name+w1+");"+w6);
    else if (field.type.typeof == Type.USERTYPE)
    {
      //Toolkit.getDefaultToolkit().beep();
      errLog.println("Warning: "+prototype.name+" "+field.name+" is of UserType and may require swapping.");
    }
  }
  /**
  * Generates the prototypes defined
  */
  private static void generateCClientImp(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print(prototype.type.cDefUbi("T"+module.name+"::"+prototype.name)+"(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDefUbi(parameter.name));
      w1 = ", ";
    }
    outData.println(")");
    outData.println("{");
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
      outData.println("  "+prototype.type.cNameUbi()+" Result;");
    outData.print("  errorCode = "+module.name+"Snap" + prototype.name+"(getHandle()");
    outData.print(", "+prototype.signature(true));
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
      outData.print(", Result");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(", "+parameter.name);
    }
    outData.println(");");
    outData.println("  " + module.name + "SnapThrowOnError(getHandle(), __FILE__, __LINE__);");
    outData.println("  if (errorCode != " + module.name.toUpperCase() + "_OK)");
    outData.println("    throw errorCode;");
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
      outData.println("  return Result;");
    outData.println("}");
    outData.println();
  }
  /**
  * Generates the prototypes defined
  */
  private static void generateCClient(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    boolean hasReturn = false;
    Vector<Field> retrievals = new Vector<Field>();
    Vector<Field> submittals = new Vector<Field>();
    Vector<String> allocated = new Vector<String>();
    if (prototype.type.reference != Type.BYVAL)
    {
      outData.println("#error Only non pointers are allowed as return values");
      errLog.println("#error Only non pointers are allowed as return values");
    }
    if (prototype.type.reference == Type.BYVAL
    &&  prototype.type.typeof != Type.VOID)
      hasReturn = true;
    outData.print("e"+module.name+" "+module.name+"Snap" + prototype.name+"(int32 Handle, int32 Signature");
    if (hasReturn)
        outData.print(", "+prototype.type.cNameUbi()+"& Result");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(", " + parameter.type.cDefUbi(parameter.name));
    }
    outData.println(")");
    outData.println("{");
    outData.println("  if (Signature != "+prototype.signature(true)+")");
    outData.println("    return "+module.name.toUpperCase()+"_INV_SIGNATURE;");
    outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
    outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
    outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
    outData.println("  try");
    outData.println("  {");
    if (alignForSun == true)
      outData.println("    ubiCB->sendBuffLen = alignData(4);");
    else
      outData.println("    ubiCB->sendBuffLen = 4;");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      Action input = (Action) prototype.inputs.elementAt(i);
      Field field  = input.getParameter(prototype);
      String opExtra = "";
      if (field == null)
      {
        outData.println("#error "+input.name+" is an undefined input parameter");
        errLog.println("#error "+input.name+" is an undefined input parameter");
        continue;
      }
      if (field.type.reference == Type.BYPTR
      ||  field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            if (alignForSun == true)
              outData.println("    ubiCB->sendBuffLen += alignData(alignData(sizeof(int32))+strlen(" + field.name + ")+1);");
            else
              outData.println("    ubiCB->sendBuffLen += (sizeof(int32)+strlen(" + field.name + ")+1);");
          }
          else
          {
            if (alignForSun == true)
              outData.println("    ubiCB->sendBuffLen += alignData(sizeof(*" + field.name + "));");
            else
              outData.println("    ubiCB->sendBuffLen += sizeof(*" + field.name + ");");
          }
        }
        else
        {
          Operation op = input.sizeOperation();
          Field opField = prototype.getParameter(op.name);
          if (opField != null && opField.type.reference == Type.BYPTR)
            opExtra = "*";
          if (alignForSun == true)
            outData.println("    ubiCB->sendBuffLen += alignData(alignData(sizeof(int32))+" + opExtra + op.name + "*sizeof(*" + field.name + "));");
          else
            outData.println("    ubiCB->sendBuffLen += (sizeof(int32)+" + opExtra + op.name + "*sizeof(*" + field.name + "));");
        }
      }
      else
      {
        if (input.hasSize() == false)
        {
          if (alignForSun == true)
            outData.println("    ubiCB->sendBuffLen += alignData(sizeof("+field.name+"));");
          else
            outData.println("    ubiCB->sendBuffLen += sizeof("+field.name+");");
        }
        else
        {
          outData.println("#error "+field.name+" is not a pointer parameter but has a size");
          errLog.println("#error "+field.name+" is not a pointer parameter but has a size");
        }
      }
    }
    outData.println("    ubiCB->sendBuff = new char[ubiCB->sendBuffLen];");
    outData.println("    char* ip = ubiCB->sendBuff;");
    String w1 = "";
    if (prototype.inputs.size() == 0)
      w1 = "// ";
    outData.println("    *(int32*)ip = Signature;");
    outData.println("    SwapBytes(*(int32*)ip);");
    if (alignForSun == true)
      outData.println("    "+w1+"ip += alignData(sizeof(int32));");
    else
      outData.println("    "+w1+"ip += sizeof(int32);");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      if (i+1 == prototype.inputs.size())
        w1 = "// ";
      Action input = (Action) prototype.inputs.elementAt(i);
      Field field  = input.getParameter(prototype);
      if (field == null)
        continue;
      Operation op = input.sizeOperation();
      generateCSwaps(module, prototype, field, op, outData);
      if (field.type.reference == Type.BYPTR
      || field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("    *(int32*)ip = (strlen(" + field.name + ")+1);");
            outData.println("    SwapBytes(*(int32*)ip);");
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof(int32));");
            else
              outData.println("    ip += sizeof(int32);");
            outData.println("    memcpy(ip, " + field.name + ", (int32)strlen(" + field.name + ")+1);");
            if (alignForSun == true)
              outData.println("    " + w1 + "ip += alignData(strlen(" + field.name + ")+1);");
            else
              outData.println("    " + w1 + "ip += (strlen(" + field.name + ")+1);");
          }
          else
          {
            outData.println("    memcpy(ip, (void*)" + field.name + ", (int32)sizeof(*" + field.name + "));");
            if (alignForSun == true)
              outData.println("    " + w1 + "ip += alignData(sizeof(*" + field.name + "));");
            else
              outData.println("    " + w1 + "ip += sizeof(*" + field.name + ");");
          }
        }
        else
        {
          String opExtra = "";
          Field opField = prototype.getParameter(op.name);
          if (opField != null && opField.type.reference == Type.BYPTR)
            opExtra = "*";
          if (field.type.typeof != Type.CHAR)
            submittals.addElement(field);
          outData.println("    *(int32*)ip = (" + opExtra + op.name + "*sizeof(*" + field.name + "));");
          outData.println("    SwapBytes(*(int32*)ip);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32));");
          else
            outData.println("    ip += sizeof(int32);");
          outData.println("    memcpy(ip, (void*)" + field.name + ", (int32)(" + opExtra + op.name + "*sizeof(*" + field.name + ")));");
          if (alignForSun == true)
            outData.println("    " + w1 + "ip += alignData(int32(" + opExtra + op.name + "*sizeof(*" + field.name + ")));");
          else
            outData.println("    " + w1 + "ip += (int32)(" + opExtra + op.name + "*sizeof(*" + field.name + "));");
        }
      }
      else
      {
        outData.println("    memcpy(ip, (char*)&" + field.name + ", (int32)sizeof(" + field.name + "));");
        if (alignForSun == true)
          outData.println("    " + w1 + "ip += alignData(sizeof(" + field.name + "));");
        else
          outData.println("    " + w1 + "ip += sizeof(" + field.name + ");");
      }
      generateCSwaps(module, prototype, field, op, outData);
    }
    if (prototype.message.length() > 0)
      w1 = prototype.message;
    else
      w1 = ""+no;
    outData.println("    ubiCB->RPC->Call("+w1+", ubiCB->sendBuff, ubiCB->sendBuffLen);");
    outData.println("    delete [] ubiCB->sendBuff;");
    outData.println("    ubiCB->sendBuff = 0;");
    boolean hasRX     = false;
    w1 = "// ";
    if (prototype.outputs.size() > 0)
      w1 = "";
    if (prototype.outputs.size() > 0 || hasReturn)
    {
      hasRX = true;
      outData.println("    if (ubiCB->RPC->RxSize())");
      outData.println("    {");
      outData.println("      ip = (char*)ubiCB->RPC->RxBuffer();");
      if (hasReturn)
      {
        outData.println("      memcpy(&Result, ip, (int32)sizeof(Result));");
        outData.println("      SwapBytes(Result);");
        if (alignForSun == true)
          outData.println("      "+w1+"ip += alignData(sizeof(Result));");
        else
          outData.println("      "+w1+"ip += sizeof(Result);");
      }
      for (int i = 0; i < prototype.outputs.size(); i++)
      {
        if (i+1 == prototype.outputs.size())
          w1 = "// ";
        Action output = (Action) prototype.outputs.elementAt(i);
        Field field  = output.getParameter(prototype);
        if (field == null)
        {
          outData.println("#error "+output.name+" is an undefined input parameter");
          errLog.println("#error "+output.name+" is an undefined input parameter");
          continue;
        }
        Operation op = output.sizeOperation();
        if (field.type.reference == Type.BYPTR
        ||  field.type.reference == Type.BYREFPTR)
        {
          if (output.hasSize() == false)
          {
            if (field.type.typeof == Type.CHAR)
            {
              outData.println("#error "+output.name+" unsized chars cannot be used as output");
              errLog.println("#error "+output.name+" unsized chars cannot be used as output");
              continue;
            }
            outData.println("      memcpy("+field.name+", ip, sizeof(*"+field.name+"));");
            if (alignForSun == true)
              outData.println("      "+w1+"ip += alignData(sizeof(*"+field.name+"));");
            else
              outData.println("      "+w1+"ip += sizeof(*"+field.name+");");
          }
          else
          {
            if (field.type.typeof != Type.CHAR)
              retrievals.addElement(field);
            outData.println("      ubiCB->recvSize = *(int32*)ip;");
            outData.println("      SwapBytes(ubiCB->recvSize);");
            if (alignForSun == true)
              outData.println("      ip += alignData(sizeof(int32));");
            else
              outData.println("      ip += sizeof(int32);");
            if (field.type.reference == Type.BYREFPTR)
            {
              String s = prototype.getOutputSizeName(field.name);
              Field sf = prototype.getParameter(s);
              String w = "";
              if (sf.type.reference == Type.BYPTR)
                w = "*";
              outData.println("      "+field.name+" = new "+field.type.cNameUbi()+"["+w+s+"];");
            }
            outData.println("      memcpy("+field.name+", ip, ubiCB->recvSize);");
            if (alignForSun == true)
              outData.println("      "+w1+"ip += alignData(ubiCB->recvSize);");
            else
              outData.println("      "+w1+"ip += ubiCB->recvSize;");
          }
        }
        else
        {
          outData.println("      memcpy(&"+field.name+", ip, sizeof("+field.name+"));");
          if (alignForSun == true)
            outData.println("      "+w1+"ip += alignData(sizeof("+field.name+"));");
          else
            outData.println("      "+w1+"ip += sizeof("+field.name+");");
        }
        generateCSwaps(module, prototype, field, op, outData);
      }
    }
    if (hasRX)
    {
      outData.println("      ubiCB->RPC->RxFree();");
      outData.println("    }");
    }
    outData.println("    return (e"+module.name+")ubiCB->RPC->ReturnCode();");
    outData.println("  }");
    outData.println("  catch(xCept &x)");
    outData.println("  {");
    outData.println("    ubiCB->RPC->ErrBuffer(x.ErrorStr());");
    outData.println("    return "+module.name.toUpperCase()+"_SNAP_ERROR;");
    outData.println("  }");
    outData.println("  catch(...)");
    outData.println("  {");
    outData.println("    return "+module.name.toUpperCase()+"_SNAP_ERROR;");
    outData.println("  }");
    outData.println("}");
    outData.println();
    if (submittals.size() > 0)
    {
      for (int i = 0; i < submittals.size(); i++)
      {
        Field parameter = (Field) submittals.elementAt(i);
        outData.println("void "+module.name+"Snap"+prototype.name+parameter.name+"Prepare(int32 Handle, int32 Size)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return;");
        outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  ubiCB->"+prototype.name+parameter.name+" = new "
                            +parameter.type.cNameUbi()+" [Size];");
        allocated.addElement(parameter.name);
        outData.println("}");
        outData.println();
        outData.print("int32 "+module.name+"Snap"+prototype.name+parameter.name+"Fill(int32 Handle");
        outData.print(", " + parameter.type.cDefUbi("Rec"));
        outData.println(", int Index)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return 0;");
        outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  ubiCB->"+prototype.name+parameter.name+"[Index] = *Rec;");
        outData.println("  return 1;");
        outData.println("}");
        outData.println();
      }
    }
    if (retrievals.size() > 0 || submittals.size() > 0)
    {
      outData.print("e"+module.name+" "+module.name+"Snap" + prototype.name + "Start(int32 Handle, int32 Signature");
      if (hasReturn)
        outData.print(", "+prototype.type.cNameUbi()+"& Result");
      for (int i = 0; i < prototype.parameters.size(); i++)
      {
        Field parameter = (Field) prototype.parameters.elementAt(i);
        if (parameter.type.typeof != Type.CHAR
        && (parameter.type.reference == Type.BYPTR
        ||  parameter.type.reference == Type.BYREFPTR))
          if (prototype.hasOutputSize(parameter.name)
          ||  prototype.hasInputSize(parameter.name))
            continue;
        outData.print(", " + parameter.type.cDefUbi(parameter.name));
      }
      outData.println(")");
      outData.println("{");
      outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
      outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
      outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
      for (int i = 0; i < retrievals.size(); i++)
      {
        Field parameter = (Field) retrievals.elementAt(i);
        if (parameter.type.reference == Type.BYREFPTR)
          continue;
        String s = prototype.getOutputSizeName(parameter.name);
        Field sf = prototype.getParameter(s);
        String w = "";
        if (sf == null)
        {
          outData.println("#error "+parameter.name+" size " + s + " not found");
          continue;
        }
        boolean alreadyDone = false;
        for (int j=0; j<allocated.size(); j++)
        {
          String name = (String) allocated.elementAt(j);
          if (name.compareTo(parameter.name) == 0)
          {
            alreadyDone = true;
            break;
          }
        }
        if (alreadyDone)
          continue;
        if (sf.type.reference == Type.BYPTR)
          w = "*";
        outData.println("  ubiCB->"+prototype.name+parameter.name+" = new "
                        +parameter.type.cNameUbi()+"["+w+s+"];");
      }
      outData.print("  e"+module.name+" result = "+module.name+"Snap"+prototype.name+"(Handle, Signature");
      if (hasReturn)
        outData.print(", Result");
      for (int i = 0; i < prototype.parameters.size(); i++)
      {
        Field parameter = (Field) prototype.parameters.elementAt(i);
        if (parameter.type.typeof != Type.CHAR
        && (parameter.type.reference == Type.BYPTR
        ||  parameter.type.reference == Type.BYREFPTR)
        && (prototype.hasOutputSize(parameter.name)
        ||  prototype.hasInputSize(parameter.name)))
          outData.print(", ubiCB->"+prototype.name+parameter.name);
        else
          outData.print(", "+parameter.name);
      }
      outData.println(");");
      for (int i = 0; i < submittals.size(); i++)
      {
        Field parameter = (Field) submittals.elementAt(i);
        outData.println("  delete [] ubiCB->"+prototype.name+parameter.name+";");
        outData.println("  ubiCB->"+prototype.name+parameter.name+" = 0;");
      }
      outData.println("  return result;");
      outData.println("}");
      outData.println();
    }
    if (retrievals.size() > 0)
    {
      for (int i = 0; i < retrievals.size(); i++)
      {
        Field parameter = (Field) retrievals.elementAt(i);
        outData.print("int32 "+module.name+"Snap"+prototype.name+parameter.name+"Next(int32 Handle");
        outData.print(", " + parameter.type.cDefUbiRefPAsP("Rec"));
        outData.println(", int32 Index)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return 0;");
        outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  *Rec = ubiCB->"+prototype.name+parameter.name+"[Index];");
        outData.println("  return 1;");
        outData.println("}");
        outData.println();
        outData.println("void "+module.name+"Snap"+prototype.name+parameter.name+"Done(int32 Handle)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return;");
        outData.println("  T"+module.name+"UbiCB* ubiCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  if (ubiCB->"+prototype.name+parameter.name+")");
        outData.println("    delete [] ubiCB->"+prototype.name+parameter.name+";");
        outData.println("  ubiCB->"+prototype.name+parameter.name+" = 0;");
        outData.println("}");
        outData.println();
      }
    }
  }
}

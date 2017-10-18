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

public class PopGenAIXClient extends Generator
{
  public static String description()
  {
    return "Generate AIX(Unix) Client Code";
  }
  public static String documentation()
  {
    return "Generate AIX(Unix) Client Code";
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
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    generateCClient(module, output, outLog);
    generateCClientHeader(module, output, outLog);
    generateCClientMake(module, output, outLog);
  }
  /**
  * Sets up the writer and generates the general stuff
  */
  public static void generateCClientMake(Module module, String output, PrintWriter outLog)
  {
    try
    {
      File makefile = new File(output+"makefile");
      if (makefile.exists())
        return;
      outLog.println("Code: "+output+"makefile");
      OutputStream outFile = new FileOutputStream(output+"makefile");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println(".AUTODEPEND");
        outData.println();
        outData.println("# "+module.name+" makefile");
        outData.println("CC = bcc32");
        outData.println();
        outData.println("INCS = -I..;s:\\bc5\\include");
        outData.println();
        outData.println("LIBS = \\");
        outData.println("\tf:\\jenga\\lib\\libsnap.lib \\");
        outData.println();
        outData.println("CFLAGS = -H- -d -v -y -WD");
        outData.println();
        outData.println(".cpp.obj:");
        outData.println("\t$(CC) -c $(CFLAGS) $(INCS) $<");
        outData.println();
        outData.println(module.name.toUpperCase()+" =\\");
        outData.println("\t"+module.name.toLowerCase()+"Client.obj");
        outData.println();
        outData.println(module.name.toLowerCase()+".dll: $("+module.name.toUpperCase()+")");
        outData.println("\t$(CC) $(CFLAGS) -e"+module.name.toLowerCase()+".dll -Ls:\\bc5\\lib @&&|");
        outData.println("$("+module.name.toUpperCase()+")");
        outData.println("$(LIBS)");
        outData.println("|");
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
  public static void generateCClientHeader(Module module, String output, PrintWriter outLog)
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
        //outData.println("#pragma option -b");
        //outData.println();
        outData.println("#include \"machine.h\"");
        outData.println("#include \"popgen.h\"");
        outData.println();
        outData.println("extern char *"+module.name+"Version;");
        outData.println("extern int32 "+module.name+"Signature;");
        PopGen.generateCExterns(module, outData);
        outData.println("#pragma pack (push,1)");
        PopGen.generateCStructs(module, outData, true);
        outData.println("#pragma pack (pop)");
        outData.println();
        outData.println("class t"+module.name);
        outData.println("{");
        outData.println("  int32 Handle;");
        outData.println("  e"+module.name+" errorCode;");
        outData.println("  bool loggedOn;");
        outData.println("  char fErrBuffer[4096];");
        outData.println("  char fErrorDesc[256];");
        outData.println("public:");
        outData.println("  t"+module.name+"() {loggedOn = false;}");
        outData.println("  ~t"+module.name+"() {if (loggedOn) Logoff();}");
        outData.println("  int32 getHandle();");
        outData.println("  void Logon(char* UserID, char* Service, char* Host, int32 Timeout=150000);");
        outData.println("  void Logoff();");
        outData.println("  char* ErrBuffer() {return ErrBuffer(fErrBuffer, sizeof(fErrBuffer));}");
        outData.println("  char* ErrorDesc() {return ErrorDesc(fErrorDesc, sizeof(fErrorDesc));}");
        outData.println("  char* ErrBuffer(char *Buffer, int32 BufferLen);");
        outData.println("  char* ErrorDesc(char *Buffer, int32 BufferLen);");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          PopGen.generateCHeader(module, prototype, outData);
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
  public static void generateCClient(Module module, String output, PrintWriter outLog)
  {
    try
    {
      String w1 = "";
      outLog.println("Code: "+output+module.name.toLowerCase()+"Client.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Client.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println();
        outData.println("#include \"ti.h\"");
        outData.println();
        outData.println("char *"+module.name+"Version = "+module.version+";");
        outData.println("int32 "+module.name+"Signature = "+module.signature+";");
        outData.println();
        outData.println("#include <xstring.h>");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println();
        outData.println("#include \"popgen.h\"");
        outData.println("#include \"snaprpcaixclient.h\"");
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
        outData.println("struct t"+module.name+"SnapCB");
        outData.println("{");
        outData.println("  tRPCClient* RPC;");
        outData.println("  char *sendBuff;");
        outData.println("  int32 sendBuffLen;");
        outData.println("  int32 recvSize;");
        outData.println("  e"+module.name+" result;");
        outData.println("  int32 RC;");
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
                  outData.println("  "+parameter.type.cDefRefPAsP(prototype.name+parameter.name, false)+";");
                else
                  outData.println("  "+parameter.type.cDef(prototype.name+parameter.name, false)+";");
              }
            }
          }
        }
        outData.println("  t"+module.name+"SnapCB(tString UserID, tString Service, tString Host, int32 Timeout)");
        w1 = ":";
        // There generally wont be the same retrieval and submittal,
        // even if there is it should not really matter much. If it
        // does then we will change this code.
        outData.println("  {");
        outData.println("    RPC = new tRPCClient(UserID, Host, Service, Timeout);");
        outData.println("  }");
        outData.println("  ~t"+module.name+"SnapCB()");
        outData.println("  {");
        outData.println("    delete RPC;");
        outData.println("  }");
        outData.println("};");
        outData.println();
        outData.println("const CookieStart = 1719;");
        outData.println("const NoCookies = 32;");
        outData.println("static tHandle<t"+module.name+"SnapCB*, CookieStart, NoCookies> "+module.name+"CBHandle;");
        outData.println();
        outData.println("e"+module.name+" "+module.name+"SnapLogon(int32* Handle, char* UserID, char* Service, char* Host, int32 Timeout)");
        outData.println("{");
        outData.println("  try");
        outData.println("  {");
        outData.println("    *Handle = "+module.name+"CBHandle.Create(new t"+module.name+"SnapCB(UserID, Service, Host, Timeout));");
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
        outData.println("e"+module.name+" "+module.name+"SnapErrBuffer(int32 Handle, char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
        outData.println("  t"+module.name+"SnapCB* snapCB = "+module.name+"CBHandle.Use(Handle);");
        outData.println("  if (snapCB->RPC->ErrSize())");
        outData.println("    strncpy(Buffer, snapCB->RPC->ErrBuffer(), BufferLen);");
        outData.println("  else");
        outData.println("    strcpy(Buffer, \"\");");
        outData.println("  for(int i = strlen(Buffer); i < BufferLen; i++)");
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
        outData.println("  int n = (int)"+w1+";");
        outData.println("  strncpy(Buffer, "+module.name+"Errors[n], BufferLen-(strlen(W1)+1));");
        outData.println("  if (RC == "+module.name.toUpperCase()+"_LAST_LAST)");
        outData.println("    strcat(Buffer, W1);");
        outData.println("  for(int i = strlen(Buffer); i < BufferLen; i++)");
        outData.println("    Buffer[i]  = ' ';");
        outData.println("}");
        outData.println();
        outData.println("void "+module.name+"SnapVersion(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  strncpy(Buffer, "+module.name+"Version, BufferLen);");
        outData.println("  for(int i = strlen(Buffer); i < BufferLen; i++)");
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
        outData.println("int32 t"+module.name+"::getHandle()");
        outData.println("{");
        outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
        outData.println("    throw "+module.name.toUpperCase()+"_INV_COOKIE;");
        outData.println("  return Handle;");
        outData.println("}");
        outData.println();
        outData.println("void t"+module.name+"::Logon(char* UserID, char* Service, char* Host, int32 Timeout)");
        outData.println("{");
        outData.println("  errorCode = "+module.name+"SnapLogon(&Handle, UserID, Service, Host, Timeout);");
        outData.println("  if (errorCode != 0)");
        outData.println("  {");
        outData.println("    Handle = -1;");
        outData.println("    throw errorCode;");
        outData.println("  }");
        outData.println("  loggedOn = true;");
        outData.println("}");
        outData.println();
        outData.println("void t"+module.name+"::Logoff()");
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
        outData.println("char* t"+module.name+"::ErrBuffer(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  "+module.name+"SnapErrBuffer(getHandle(), Buffer, BufferLen-1);");
        outData.println("  Buffer[BufferLen-1] = 0;");
        outData.println("  return strtrim(Buffer);");
        outData.println("}");
        outData.println();
        outData.println("char* t"+module.name+"::ErrorDesc(char *Buffer, int32 BufferLen)");
        outData.println("{");
        outData.println("  "+module.name+"SnapErrorDesc(errorCode, Buffer, BufferLen-1);");
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
  static int n=0;
  public static void generateCSwaps(Module module, Prototype prototype, Field field, Operation op, PrintWriter outData)
  {
    String w1 = "", w2 = "", w3 = ".", w4 = "", w5 = "";
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
        outData.println("  "+w2+"for (int i_"+n+" = 0; i_"+n+" < "+w5+op.name+"; i_"+n+"++)");
        w1 = w1 + "[i_"+n+"]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
      }
      for (int j=0; j < field.type.arraySizes.size(); j++)
      {
        Integer integer = (Integer) field.type.arraySizes.elementAt(j);
        n++;
        outData.println("    "+w2+"for (int i_"+n+"_"+j+" = 0; i_"+n+"_"+j
                        + " < " + integer.intValue()
                        + "; i_"+n+"_"+j+"++)");
        w1 = w1 + "[i_"+n+"_"+j+"]";
        w2 = w2 + "  ";
        w3 = ".";
        w4 = "";
      }
    }
    if (field.isStruct(module))
      outData.println("    "+w2+field.name+w1+w3+"Swaps();");
    else if (field.needsSwap())
      outData.println("    "+w2+"SwapBytes("+w4+field.name+w1+");");
    else if (field.type.typeof == Type.USERTYPE)
    {
      //Toolkit.getDefaultToolkit().beep();
      errLog.println("Warning: "+prototype.name+" "+field.name+" is of UserType and may require swapping.");
    }
  }
  /**
  * Generates the prototypes defined
  */
  public static void generateCClientImp(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "";
    outData.print(prototype.type.cDef("t"+module.name+"::"+prototype.name, false)+"(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(w1 + parameter.type.cDef(parameter.name, false));
      w1 = ", ";
    }
    outData.println(")");
    outData.println("{");
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
      outData.println("  "+prototype.type.cName(false)+" Result;");
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
    outData.println("  if (errorCode != "+module.name.toUpperCase()+"_OK)");
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
  public static void generateCClient(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    boolean hasReturn = false;
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
        outData.print(", "+prototype.type.cName(false)+"& Result");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      outData.print(", " + parameter.type.cDef(parameter.name, false));
    }
    outData.println(")");
    outData.println("{");
    outData.println("  if (Signature != "+prototype.signature(true)+")");
    outData.println("    return "+module.name.toUpperCase()+"_INV_SIGNATURE;");
    outData.println("  if (Handle < CookieStart || Handle >= CookieStart+NoCookies)");
    outData.println("    return "+module.name.toUpperCase()+"_INV_COOKIE;");
    outData.println("  t"+module.name+"SnapCB* snapCB = "+module.name+"CBHandle.Use(Handle);");
    outData.println("  try");
    outData.println("  {");
    outData.println("    snapCB->sendBuffLen = 4;");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      Action input = (Action) prototype.inputs.elementAt(i);
      Field field  = input.getParameter(prototype);
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
            outData.println("    snapCB->sendBuffLen += (sizeof(int32)+strlen("+field.name+")+1);");
          else
            outData.println("    snapCB->sendBuffLen += sizeof(*"+field.name+");");
        }
        else
        {
          Operation op = input.sizeOperation();
          outData.println("    snapCB->sendBuffLen += (sizeof(int32)+"+op.name+"*sizeof(*"+field.name+"));");
        }
      }
      else
      {
        if (input.hasSize() == false)
          outData.println("    snapCB->sendBuffLen += sizeof("+field.name+");");
        else
        {
          outData.println("#error "+field.name+" is not a pointer parameter but has a size");
          errLog.println("#error "+field.name+" is not a pointer parameter but has a size");
        }
      }
    }
    outData.println("    snapCB->sendBuff = new char[snapCB->sendBuffLen];");
    outData.println("    char* ip = snapCB->sendBuff;");
    String w1 = "";
    if (prototype.inputs.size() == 0)
      w1 = "// ";
    outData.println("    *(int32*)ip = Signature;");
    outData.println("    SwapBytes(*(int32*)ip);");
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
      ||  field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("    *(int32*)ip = (strlen("+field.name+")+1);");
            outData.println("    SwapBytes(*(int32*)ip);");
            outData.println("    ip += sizeof(int32);");
            outData.println("    memcpy(ip, "+field.name+", (int32)strlen("+field.name+")+1);");
            outData.println("    "+w1+"ip += (strlen("+field.name+")+1);");
          }
          else
          {
            outData.println("    memcpy(ip, (void*)"+field.name+", (int32)sizeof(*"+field.name+"));");
            outData.println("    "+w1+"ip += sizeof(*"+field.name+");");
          }
        }
        else
        {
          outData.println("    *(int32*)ip = ("+op.name+"*sizeof(*"+field.name+"));");
          outData.println("    SwapBytes(*(int32*)ip);");
          outData.println("    ip += sizeof(int32);");
          outData.println("    memcpy(ip, (void*)"+field.name+", (int32)("+op.name+"*sizeof(*"+field.name+")));");
          outData.println("    "+w1+"ip += (int32)("+op.name+"*sizeof(*"+field.name+"));");
        }
      }
      else
      {
        outData.println("    memcpy(ip, (char*)&"+field.name+", (int32)sizeof("+field.name+"));");
        outData.println("    "+w1+"ip += sizeof("+field.name+");");
      }
      generateCSwaps(module, prototype, field, op, outData);
    }
    if (prototype.message.length() > 0)
      w1 = prototype.message;
    else
      w1 = ""+no;
    outData.println("    snapCB->RPC->Call("+w1+", snapCB->sendBuff, snapCB->sendBuffLen);");
    outData.println("    delete [] snapCB->sendBuff;");
    outData.println("    snapCB->sendBuff = 0;");
    boolean hasRX     = false;
    w1 = "// ";
    if (prototype.outputs.size() > 0)
      w1 = "";
    if (prototype.outputs.size() > 0 || hasReturn)
    {
      hasRX = true;
      outData.println("    ip = (char*)snapCB->RPC->RxBuffer();");
      if (hasReturn)
      {
        outData.println("    memcpy(&Result, ip, (int32)sizeof(Result));");
        outData.println("    SwapBytes(Result);");
        outData.println("    "+w1+"ip += sizeof(Result);");
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
            outData.println("    memcpy("+field.name+", ip, sizeof(*"+field.name+"));");
            outData.println("    "+w1+"ip += sizeof(*"+field.name+");");
          }
          else
          {
            outData.println("    snapCB->recvSize = *(int32*)ip;");
            outData.println("    SwapBytes(snapCB->recvSize);");
            outData.println("    ip += sizeof(int32);");
            if (field.type.reference == Type.BYREFPTR)
            {
              String s = prototype.getOutputSizeName(field.name);
              Field sf = prototype.getParameter(s);
              String w = "";
              if (sf.type.reference == Type.BYPTR)
                w = "*";
              outData.println("    "+field.name+" = new "+field.type.cName(false)+"["+w+s+"];");
            }
            outData.println("    memcpy("+field.name+", ip, snapCB->recvSize);");
            outData.println("    "+w1+"ip += snapCB->recvSize;");
          }
        }
        else
        {
          outData.println("    memcpy(&"+field.name+", ip, sizeof("+field.name+"));");
          outData.println("    "+w1+"ip += sizeof("+field.name+");");
        }
        generateCSwaps(module, prototype, field, op, outData);
      }
    }
    if (hasRX)
      outData.println("    snapCB->RPC->RxFree();");
    outData.println("    return (e"+module.name+")snapCB->RPC->ReturnCode();");
    outData.println("  }");
    outData.println("  catch(xCept &x)");
    outData.println("  {");
    outData.println("    snapCB->RPC->ErrBuffer(x.ErrorStr());");
    outData.println("    return "+module.name.toUpperCase()+"_SNAP_ERROR;");
    outData.println("  }");
    outData.println("  catch(...)");
    outData.println("  {");
    outData.println("    return "+module.name.toUpperCase()+"_SNAP_ERROR;");
    outData.println("  }");
    outData.println("}");
    outData.println();
  }
}

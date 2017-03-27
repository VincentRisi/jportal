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

public class PopGenNPServer extends Generator
{
  public static String description()
  {
    return "Generates Name Pipe Server NP Code (only NT Server)";
  }
  public static String documentation()
  {
    return "Generates Name Pipe Server NP Code (only NT Server)";
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
    generateCHeader(module, output, outLog);
    if (module.tables.size() > 0)
    {
      generateCTablesHeader(module, output, outLog);
      generateCTablesCode(module, output, outLog);
    }
    //generateCHeader(module, output, outLog);
    if (generateCServer(module, output, outLog))
      generateCCode(module, output, outLog);
  }
  private static void generateCTablesHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"Tables.h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Tables.h");
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
    }
  }
  private static void generateCTablesCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"Tables.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Tables.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println();
        outData.println("#include \"machine.h\"");
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
    }
  }
  /**
  * Sets up the writer and generates the general stuff
  */
  private static void generateCHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"Server.h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Server.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        boolean hasProtected = false;
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("#ifndef _"+module.name+"H_");
        outData.println("#define _"+module.name+"H_");
        outData.println("#include \"machine.h\"");
        outData.println();
        outData.println("#include <vcl\\Classes.hpp>");
        outData.println("#include \"snaprpcntserver.h\"");
        outData.println();
        PopGen.generateCExterns(module, outData);
        outData.println("extern char *"+module.name+"Version;");
        outData.println("extern int32 "+module.name+"Signature;");
        outData.println();
        PopGen.generateCStructs(module, outData, false);
        outData.println("class t"+module.name);
        outData.println("{");
        outData.println("private:");
        outData.println("  tRPCServerNP *RPC;");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure) module.structures.elementAt(i);
          if (structure.codeType == Structure.PROTECTED)
            hasProtected = true;
          if (structure.codeType != Structure.PRIVATE)
            continue;
          for (int j=0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            outData.println("  "+field.type.cDef(field.name, false)+";");
          }
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PROTECTED)
            hasProtected = true;
          if (prototype.codeType != Prototype.PRIVATE)
            continue;
          PopGen.generateCHeader(module, prototype, outData);
        }
        outData.println("public:");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure) module.structures.elementAt(i);
          if (structure.codeType != Structure.PUBLIC)
            continue;
          for (int j=0; j < structure.fields.size(); j++)
          {
            Field field = (Field) structure.fields.elementAt(j);
            outData.println("  "+field.type.cDef(field.name, false)+";");
          }
        }
        outData.println("  t"+module.name+"()");
        outData.println("  {");
        outData.println("  }");
        outData.println("  void StartUpCode()");
        outData.println("  {");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure) module.structures.elementAt(i);
          if (structure.codeType == Structure.NORMAL)
            continue;
          if (structure.code.size() > 0)
            outData.println("#line "+structure.codeLine+" \""+module.sourceName.toLowerCase()+"\"");
          for (int j=0; j < structure.code.size(); j++)
          {
            String codeLine = (String) structure.code.elementAt(j);
            if ((codeLine.trim()).equalsIgnoreCase("onshutdown:"))
            {
              outData.println("  }");
              outData.println("  void ShutDownCode()");
              outData.println("  {");
              outData.println("#line "+(structure.codeLine+j+1)+" \""+module.sourceName.toLowerCase()+"\"");
              hasShutDownCode = true;
            }
            else
              outData.print(codeLine);
          }
        }
        outData.println("  }");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType == Prototype.PRIVATE
          ||  prototype.codeType == Prototype.PROTECTED)
            continue;
          PopGen.generateCHeader(module, prototype, outData);
        }
        if (hasProtected == true)
        {
          outData.println("protected:");
          for (int i = 0; i < module.structures.size(); i++)
          {
            Structure structure = (Structure) module.structures.elementAt(i);
            if (structure.codeType != Structure.PROTECTED)
              continue;
            for (int j=0; j < structure.fields.size(); j++)
            {
              Field field = (Field) structure.fields.elementAt(j);
              outData.println("  "+field.type.cDef(field.name, false)+";");
            }
          }
          for (int i = 0; i < module.prototypes.size(); i++)
          {
            Prototype prototype = (Prototype) module.prototypes.elementAt(i);
            if (prototype.codeType != Prototype.PROTECTED)
              continue;
            PopGen.generateCHeader(module, prototype, outData);
          }
        }
        outData.println("};");
        outData.println();
        outData.println("class t"+module.name+"Server : public TThread");
        outData.println("{");
        outData.println("public:");
        outData.println("  __fastcall t"+module.name+"Server(AnsiString aPipename);");
        outData.println("  void __fastcall Execute();");
        outData.println("private:");
        outData.println("  t"+module.name+" *"+module.name+";");
        outData.println("  tRPCServerNP *RPC;");
        outData.println("  AnsiString Pipename;");
        outData.println("  char *ReplyBody;");
        outData.println("  e"+module.name+" Result;");
        outData.println("  int32 ReplySize;");
        outData.println("  int32 RecvSize;");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          outData.println("  e"+module.name+" "+prototype.name+"(char *ip);");
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
  }
  private static void generateCCode(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"Impl.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Impl.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println();
        outData.println("#include \"ti.h\"");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println("#include \""+module.name.toLowerCase()+"Server.h\"");
        outData.println();
        outData.println("#ifdef DIM");
        outData.println("#undef DIM");
        outData.println("#endif");
        outData.println();
        outData.println("#define DIM(a)   (sizeof(a) / sizeof(a[0]))");
        outData.println();
        outData.println("#define THROW(err) throw "+module.name.toUpperCase()+"_##err");
        outData.println("#define THROW2(err, str) RPC->ErrBuffer(str);throw "+module.name.toUpperCase()+"_##err");
        outData.println();
        outData.println("template <class TYPE, class INDEX>");
        outData.println("void AddList(TYPE*& List, INDEX& Index, TYPE& Rec, INDEX Delta)");
        outData.println("{");
        outData.println("  if ((List == 0) != (Index == 0))");
        outData.println("    THROW(ADDLIST_ERROR);");
        outData.println("  if (Index % Delta == 0)");
        outData.println("  {");
        outData.println("    TYPE* Resized = (TYPE*) realloc(List, sizeof(Rec)*(Index+Delta));");
        outData.println("    if (Resized == 0)");
        outData.println("      THROW(ADDLIST_REALLOC_FAILED);");
        outData.println("    List = Resized;");
        outData.println("  }");
        outData.println("  List[Index++] = Rec;");
        outData.println("}");
        outData.println();
        if (module.code.size() > 0)
        {
          outData.println("#line "+module.codeLine+" \""+module.sourceName.toLowerCase()+"\"");
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String) module.code.elementAt(j);
            outData.print(codeLine);
          }
          outData.println();
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.code.size() > 0)
          {
            PopGen.generateCImplCode(module, prototype, outData);
            outData.println("{");
            if (prototype.code.size() > 0)
              outData.println("#line "+prototype.codeLine+" \""+module.sourceName.toLowerCase()+"\"");
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
      outLog.println("Code: "+output+module.name.toLowerCase()+"Server.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Server.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println();
        outData.println("char *"+module.name+"Version = "+module.version+";");
        outData.println("int32 "+module.name+"Signature = "+module.signature+";");
        outData.println();
        outData.println("#include \"ti.h\"");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+"Server.h\"");
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
        outData.println("__fastcall t"+module.name+"Server::t"+module.name+"Server(AnsiString aPipename)");
        outData.println(": TThread(false)");
        outData.println("{");
        outData.println("  Pipename = aPipename;");
        outData.println("  "+module.name+" = new t"+module.name+"();");
        outData.println("}");
        outData.println();
        outData.println("void __fastcall t"+module.name+"Server::Execute()");
        outData.println("{");
        outData.println("  "+module.name+"->StartUpCode();");
        outData.println("  RPC = new tRPCServerNP(Pipename.c_str());");
        outData.println("  bool ShutDown = false;");
        outData.println("  while (!ShutDown)");
        outData.println("  {");
        outData.println("    RPC->WaitForCall();");
        outData.println("    ReplyBody=0;");
        outData.println("    ReplySize=0;");
        outData.println("    char* ip = (char*)RPC->RxBuffer();");
        outData.println("    int32 Signature;");
        outData.println("    Signature = *(int32*)ip;");
        outData.println("    ip += sizeof(int32);");
        outData.println("    switch (RPC->ReqID())");
        outData.println("    {");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.code.size() > 0)
            hasCode = true;
          if (prototype.message.length() > 0)
            outData.println("    case "+prototype.message+":");
          else
            outData.println("    case "+i+":");
          outData.println("      if (Signature != "+prototype.signature(true)+")");
          outData.println("        Result = "+module.name.toUpperCase()+"_INV_SIGNATURE;");
          outData.println("      else");
          outData.println("        Result = "+prototype.name+"(ip);");
          outData.println("      break;");
        }
        outData.println("    case 999999999:");
        outData.println("      ShutDown = true;");
        outData.println("      Result = 0;");
        outData.println("      break;");
        outData.println("    default:");
        outData.println("      Result = "+module.name.toUpperCase()+"_UNKNOWN_FUNCTION;");
        outData.println("    }");
        outData.println("    RPC->RespondToCall(Result, ReplyBody, ReplySize);");
        outData.println("    if (ReplyBody)");
        outData.println("      delete[] ReplyBody;");
        outData.println("    }");
        if (hasShutDownCode)
          outData.println("  "+module.name+"->ShutDownCode();");
        outData.println("  delete RPC;");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCServer(module, prototype, i, outData);
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
    return hasCode;
  }
  /**
  * Generates the prototypes defined
  */
  private static void generateCServer(Module module, Prototype prototype, int no, PrintWriter outData)
  {
    outData.println("e"+module.name+" t"+module.name+"Server::"+prototype.name+"(char *ip)");
    outData.println("{");
    outData.println("  try");
    outData.println("  {");
    for (int i = 0; i < prototype.inputs.size(); i++)
    {
      boolean addUp = true;
      if (i+1 == prototype.inputs.size())
        addUp = false;
      Action input = (Action) prototype.inputs.elementAt(i);
      Field field  = input.getParameter(prototype);
      if (field == null)
        continue;
      Action output = (Action) prototype.getOutputAction(field.name);
      if (output != null) {
	}
      if (field.type.reference == Type.BYPTR
      ||  field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("    RecvSize = *(int32*)ip;");
            outData.println("    ip += sizeof(int32*);");
            outData.println("    char* "+field.name+" = ip;");
            if (addUp)
              outData.println("     ip += RecvSize;");
          }
          else
          {
            outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
            if (addUp)
              outData.println("    ip += sizeof(*"+field.name+");");
          }
        }
        else
        {
          outData.println("    RecvSize = *(int32*)ip;");
          outData.println("    ip += sizeof(int32*);");
          outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          if (addUp)
            outData.println("    ip += RecvSize;");
        }
      }
      else
      {
        outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
        if (addUp)
          outData.println("    ip += sizeof(*"+field.name+");");
      }
    }
    boolean hasOutput = false;
    boolean hasResult = false;
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        outData.println("    ReplySize += (int32)sizeof("+prototype.type.cName(false)+");");
        hasOutput = true;
        hasResult = true;
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
      //Action input = (Action) prototype.getInputAction(field.name);
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
            outData.println("    ReplySize += sizeof("+field.type.cName(false)+");");
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          outData.println("    ReplySize += sizeof(int32);");
          outData.println("    ReplySize += ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+"));");
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
          outData.println("    ReplySize += sizeof("+field.type.cName(false)+"*); // Variant "+field.name);
        }
      }
      else
      {
        outData.println("    ReplySize += sizeof("+field.type.cName(false)+");");
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
      outData.println("    ReplyBody = new char [ReplySize];");
      outData.println("    memset(ReplyBody, 0, ReplySize);");
      outData.println("    ip = ReplyBody;");
    }
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        outData.println("    "+prototype.type.cName(false)+"* Result = ("+prototype.type.cName(false)+"*) ip;");
        if (prototype.outputs.size() > 0)
          outData.println("    ip += sizeof("+prototype.type.cName(false)+");");
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
      if (op != null)
      {
      }
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
              outData.println("    memcpy(ip, "+field.name+", sizeof("+field.type.cName(false)+"));");
              outData.println("    "+field.name+" = ("+field.type.cName(false)+"*)ip;");
            }
            else
              outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
            if (addUp)
              outData.println("    ip += sizeof("+field.type.cName(false)+");");
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          outData.println("    *(int32*)ip = ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+"));");
          outData.println("    ip += sizeof(int32);");
          if (input != null)
          {
            outData.println("    memcpy(ip, "+field.name+", ("+w1+op.name+" * sizeof("+field.type.cName(false)+")));");
            outData.println("    "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          }
          else
            outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          if (addUp)
            outData.println("    ip += ("+w1+op.name+" * sizeof("+field.type.cName(false)+"));");
        }
      }
      else if (field.type.reference == Type.BYREFPTR)
      {
        if (output.hasSize() == false)
          continue;
        else
        {
          if (op.isConstant == false) {
		} else
            outData.println("#error Constant Size with BYREFPTR output");
          outData.println("    Variants["+(Variants++)+"] = ip-ReplyBody;");
          if (input != null)
            outData.println("    ("+field.type.cName(false)+"*)*ip = "+field.name+";");
          else
            outData.println("    "+field.type.cName(false)+"** "+field.name+" = ("+field.type.cName(false)+"**)ip;");
          if (addUp)
            outData.println("    ip += sizeof("+field.type.cName(false)+"*);");
        }
      }
      else
      {
        if (input != null)
        {
          outData.println("    memcpy(ip, "+field.name+", sizeof("+field.type.cName(false)+"));");
          outData.println("    "+field.name+" = ("+field.type.cName(false)+"*)ip;");
        }
        else
          outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
        if (addUp)
          outData.println("    ip += sizeof("+field.type.cName(false)+");");
      }
    }
    String w1 = "", w2 = "";
    outData.print("    ");
    if (hasResult)
      outData.print("*Result = ");
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
    if (Variants > 0)
    {
      outData.println("    // We are going to setup a new ReplyBody because of Variants");
      outData.println("    char *oldReplyBody = ReplyBody;");
      outData.println("    ip = ReplyBody;");
      outData.println("    int32 tail = ReplySize;");
      outData.println("    // Calc new ReplySize for Contiguous memory from Disjoint memory");
      outData.println("    ReplySize = 0;");
      if (prototype.type.typeof != Type.VOID
      ||  prototype.type.reference == Type.BYPTR)
      {
        if (prototype.type.reference != Type.BYPTR)
        {
          outData.println("    ReplySize += (int32)sizeof("+prototype.type.cName(false)+"); // Result Size");
          hasOutput = true;
          hasResult = true;
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
        //Action input = (Action) prototype.getInputAction(field.name);
        if (field.type.reference == Type.BYPTR
        ||  field.type.reference == Type.BYREFPTR)
        {
          if (output.hasSize() == false)
          {
            if (field.type.typeof != Type.CHAR)
              outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
          }
          else
          {
            w1 = "";
            if (op.isConstant == false)
              w1 = "*";
            outData.println("    ReplySize += sizeof(int32);  // size of Block");
            outData.println("    ReplySize += ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+")); // size "+field.name);
            if (field.type.reference == Type.BYREFPTR)
              outData.println("    VariantSize["+(VariantSize++)+"] = ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+"));");
          }
        }
        else
        {
          outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
        }
      }
      outData.println("    ReplyBody = new char [ReplySize];");
      outData.println("    memset(ReplyBody, 0, ReplySize);");
      outData.println("    char *op = ReplyBody;");
      outData.println("    int32 pos = 0;");
      outData.println("    for (int i = 0; i < "+Variants+"; i++)");
      outData.println("    {");
      outData.println("      int32 size = Variants[i]-pos;");
      outData.println("      if (size > 0)");
      outData.println("      {");
      outData.println("        memcpy(op, ip, size);");
      outData.println("        ip += size;");
      outData.println("        op += size;");
      outData.println("        tail -= size;");
      outData.println("      }");
      outData.println("      memcpy(op, &VariantSize[i], sizeof(VariantSize[i])); ");
      outData.println("      op += sizeof(VariantSize[i]);");
      outData.println("      if (VariantSize[i] > 0)");
      outData.println("      {");
      outData.println("        char **block = (char **)ip;");
      outData.println("        memcpy(op, *block, VariantSize[i]);");
      outData.println("        free(*block);");
      outData.println("      }");
      outData.println("      op += VariantSize[i];");
      outData.println("      ip += sizeof(char *);");
      outData.println("      tail -= sizeof(char *);");
      outData.println("      pos = Variants[i]+sizeof(char *);");
      outData.println("    }");
      outData.println("    if (tail > 0)");
      outData.println("      memcpy(op, ip, tail);");
      outData.println("    delete [] oldReplyBody;");
    }
    outData.println("  }");
    outData.println("  catch (e"+module.name+" rc)");
    outData.println("  {");
    outData.println("    return rc;");
    outData.println("  }");
    outData.println("  return "+module.name.toUpperCase()+"_OK;");
    outData.println("}");
    outData.println();
  }
}

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

public class PopGenServer extends Generator
{
  public static String description()
  {
    return "Generates Server Code (AIX|SUN|NT)";
  }
  public static String documentation()
  {
    return "Generates Server Code (AIX|SUN|NT)"
     + "\r\nHandles following pragmas"
     + "\r\n  GetConnectFromFile - reads ini file to get database connect."
     + "\r\n  AlignForSUN - ensure that all fields are on 8 byte boundaries."
     + "\r\n  NoDBConnect - do not require database activity."
     + "\r\n  DBFill - DBFill template must be generated."
     + "\r\n  Metrics - metric switch handling code to be generated."
      ;
  }
  /*
   *  Static constructor
   */
  {
    setupPragmaVector();
  }
  private static void setupPragmaVector()
  {
	  if (pragmaVector == null) 
	  {
		  pragmaVector = new Vector<Pragma>();
		  pragmaVector.addElement(new Pragma("AlignForSun", false, "Ensure that all fields are on 8 byte boundaries."));
		  pragmaVector.addElement(new Pragma("DBConnect",   true,  "Requires automatic database activity."));
		  pragmaVector.addElement(new Pragma("DBFill",      true,  "DBFill template must be generated."));
		  pragmaVector.addElement(new Pragma("GetConnectFromFile", false, "Reads ini file to get database connect."));
      pragmaVector.addElement(new Pragma("Metrics",     false, "Metric switch handling code to be generated."));
    }
  }
  private static boolean alignForSun;
  private static boolean doConnect;
  private static boolean doDBFill;
  private static boolean doNedTelShuffle;
  private static boolean doMetrics;
  private static void setPragmas(Module module)
  {
    // Ensure these are in the same order as above
    setupPragmaVector();
    alignForSun = ((Pragma)pragmaVector.elementAt(0)).value;
    doConnect = ((Pragma)pragmaVector.elementAt(1)).value;
    doDBFill = ((Pragma)pragmaVector.elementAt(2)).value;
    doNedTelShuffle = ((Pragma)pragmaVector.elementAt(3)).value;
    doMetrics = ((Pragma)pragmaVector.elementAt(4)).value;
    for (int i = 0; i < module.pragmas.size(); i++)
    {
      String pragma = (String) module.pragmas.elementAt(i);
      if (pragma.trim().equalsIgnoreCase("GetConnectFromFile") == true)
        doNedTelShuffle = true;
      else if (pragma.trim().equalsIgnoreCase("AlignForSUN") == true)
        alignForSun = true;
      else if (pragma.trim().equalsIgnoreCase("NoDBConnect") == true)
        doConnect = false;
      else if (pragma.trim().equalsIgnoreCase("DBFill") == true)
        doDBFill = true;
      else if (pragma.trim().equalsIgnoreCase("Metrics") == true)
        doMetrics = true;
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
    if (generateCServer(module, output, outLog))
      generateCCode(module, output, outLog);
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
        outData.println("#ifndef _"+module.name+"H_");
        outData.println("#define _"+module.name+"H_");
        outData.println("#include \"machine.h\"");
        outData.println("#include \"popgen.h\"");
        outData.println("#include \"logfile.h\"");
        outData.println("#include \"ini.h\"");
        if (doConnect == true)
          outData.println("#include \"dbportal.h\"");
        outData.println("#if defined(M_AIX)");
        outData.println("#include \"snaprpcaixserver.h\"");
        outData.println("#elif defined(M_GNU)");
        outData.println("#include \"snaprpcgnuserver.h\"");
        outData.println("#else");
        outData.println("#include \"snaprpcntserver.h\"");
        outData.println("#endif");
        outData.println("#include \"snapprocess.h\"");
        outData.println();
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String) module.code.elementAt(j);
            if (codeLine.indexOf("#include") == -1)
              continue;
            if (firstTime)
              firstTime = false;
            outData.print(codeLine);
          }
          if (firstTime == false)
            outData.println();
        }
        PopGen.generateCExterns(module, outData);
        outData.println("extern char *"+module.name+"Version;");
        outData.println("extern int32 "+module.name+"Signature;");
        outData.println("extern char *ConfigFile;");
        outData.println();
        PopGen.generateCStructs(module, outData, true);
        outData.println();
        outData.println("class t"+module.name+"Server;");
        outData.println();
        outData.println("class t"+module.name+" : public tSnapIDL");
        outData.println("{");
        outData.println("private:");
        if (doConnect == true)
          outData.println("  tDBConnect &Connect;");
        outData.println("  tLogFile   &LogFile;");
        outData.println("  tRPCServer *RPC;");
        outData.println("  t"+module.name+"Server &"+module.name+"Server;");
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
        if (doConnect == true)
        {
          outData.println("  t"+module.name+"(tDBConnect &aConnect");
          outData.println("                 , tLogFile &aLogFile");
          outData.println("                 , t"+module.name+"Server &a"+module.name+"Server)");
          outData.println("  : Connect(aConnect)");
          outData.println("  , LogFile(aLogFile)");
        }
        else
        {
          outData.println("  t"+module.name+"(tLogFile &aLogFile");
          outData.println("                 , t"+module.name+"Server &a"+module.name+"Server)");
          outData.println("  : LogFile(aLogFile)");
        }
        outData.println("  , "+module.name+"Server(a"+module.name+"Server)");
        outData.println("  {");
        outData.println("  }");
        outData.println("  void StartUpCode()");
        outData.println("  {");
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure) module.structures.elementAt(i);
          if (structure.codeType == Structure.NORMAL)
            continue;
          for (int j=0; j < structure.code.size(); j++)
          {
            String codeLine = (String) structure.code.elementAt(j);
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
        outData.println("  void setRPC(tRPCServer *aRPC) {RPC = aRPC;}");
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
        outData.println("class t"+module.name+"Server : public tSnapProcess");
        outData.println("{");
        outData.println("public:");
        outData.println("  t"+module.name+"Server(tString IniFile);");
        outData.println("  ~t"+module.name+"Server();");
        outData.println("  void ServiceClient();");
        outData.println("  t"+module.name+" *"+module.name+";");
        outData.println("  tRPCServer *RPC;");
        if (doConnect == true)
        {
          outData.println("  tString BinFile;");
          outData.println("  tString Connection;");
          outData.println("  int DBTimeout;");
        }
        outData.println("#ifdef UPM_AWARE");
        outData.println("  tString UserID;");
        outData.println("  tString HostName;");
        outData.println("#endif");
        if (doConnect == true)
          outData.println("  tDBConnect *Connect;");
        outData.println("  char *ReplyBody;");
        outData.println("  int32 Result;");
        outData.println("  int32 ReplySize;");
        outData.println("  int32 RecvSize;");
        outData.println("  void ReadIniFile(tString IniFile);");
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
      outLog.println("Code: "+output+module.name.toLowerCase()+"impl.cpp");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"impl.cpp");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println();
        outData.println("#include \"ti.h\"");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println("#include <assert.h>");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+".h\"");
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
        outData.println();
        if (doConnect == true && doDBFill == true)
        {
          outData.println("template <class REC, class QUERY> ");
          outData.println("struct tDBFill : public QUERY");
          outData.println("{");
          outData.println("  REC& Rec;");
          outData.println("  tDBFill(tDBConnect &Connect, REC& aRec) ");
          outData.println("  : QUERY(Connect, &aRec)");
          outData.println("  , Rec(aRec) {}");
          outData.println("  int32 Limited(REC* List, int32 Limit)");
          outData.println("  {");
          outData.println("    int Count = 0;");
          outData.println("    while (Count < Limit && Fetch())");
          outData.println("      List[Count++] = Rec;");
          outData.println("    return Count;");
          outData.println("  }");
          outData.println("  int32 Dynamic(REC*& List)");
          outData.println("  {");
          outData.println("    int32 Count = 0;");
          outData.println("    while (Fetch())");
          outData.println("      AddList(List, Count, Rec, 16L);");
          outData.println("    return Count;");
          outData.println("  }");
          outData.println("  int32 DynamicLimited(REC*& List, int32 Limit)");
          outData.println("  {");
          outData.println("    int32 Count = 0;");
          outData.println("    while (Count < Limit && Fetch())");
          outData.println("      AddList(List, Count, Rec, 16L);");
          outData.println("    return Count;");
          outData.println("  }");
          outData.println("};");
          outData.println();
        }
        if (module.code.size() > 0)
        {
          boolean firstTime = true;
          for (int j = 0; j < module.code.size(); j++)
          {
            String codeLine = (String) module.code.elementAt(j);
            if (codeLine.indexOf("#include") != -1)
            {
              if (firstTime != true)
                outData.println();
              continue;
            }
            if (firstTime == true && module.codeLine > 0)
              firstTime = false;
            outData.print(codeLine);
          }
          if (firstTime == false)
            outData.println();
        }
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.code.size() > 0)
          {
            PopGen.generateCImplCode(module, prototype, outData);
            outData.println("{");
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
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.message.length() > 0)
            outData.println("'"+module.name+"', "+prototype.message+", '"+prototype.name+"'");
          else
            outData.println("'"+module.name+"', "+i+", '"+prototype.name+"'");
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
        outData.println("#include \"ti.h\"");
        outData.println("#include <stdio.h>");
        outData.println("#include <stdlib.h>");
        outData.println("#include <string.h>");
        outData.println("#if defined(M_AIX) || defined(M_GNU)");
        outData.println("#include <unistd.h>");
        outData.println("#endif");
        outData.println("#include \"popgen.h\"");
        if (doConnect == true)
          outData.println("#include \"dbportal.h\"");
        outData.println();
        outData.println("char *"+module.name+"Version = "+module.version+";");
        outData.println("int32 "+module.name+"Signature = "+module.signature+";");
        outData.println();
        outData.println("#define INI_FILE \""+module.name.toLowerCase()+".ini\"");
        outData.println();
        outData.println("#include \""+module.name.toLowerCase()+".h\"");
        outData.println();
        outData.println("char *ConfigFile = INI_FILE;");
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
        outData.println("t"+module.name+"Server::t"+module.name+"Server(tString IniFile)");
        outData.println(": tSnapProcess(IniFile)");
        outData.println("{");
        if (doConnect == true)
        {
          outData.println("  try");
          outData.println("  {");
          outData.println("    Connect=0;");
          outData.println("    RPC=0;");
          outData.println("    ReadIniFile(IniFile);");
          outData.println("    Connect=new tDBConnect();");
          outData.println("    "+module.name+" = new t"+module.name+"(*Connect, *LogFile, *this);");
          outData.println("  }");
          outData.println("  catch (xDBError &x)");
          outData.println("  {");
          outData.println("    LogFile->Log(x);");
          outData.println("  }");
        }
        else
        {
          outData.println("  RPC=0;");
          outData.println("  ReadIniFile(IniFile);");
          outData.println("  "+module.name+" = new t"+module.name+"(*LogFile, *this);");
        }
        outData.println("}");
        outData.println();
        outData.println("t"+module.name+"Server::~t"+module.name+"Server()");
        outData.println("{");
        if (doConnect == true)
          outData.println("  if (Connect) delete Connect;");
        outData.println("}");
        outData.println();
        outData.println("void t"+module.name+"Server::ReadIniFile(tString IniFile)");
        outData.println("{");
        outData.println("  tINI Ini(IniFile);");
        if (doConnect == true)
        {
          outData.println("  Ini.QueryValue(\"{Connection}\", Connection, \"DataBase\");");
          outData.println("  Ini.QueryValue(\"{BinFile}\", BinFile, \"DataBase\");");
          outData.println("  Ini.QueryValue(\"{Timeout}\", DBTimeout, \"DataBase\");");
          if (doNedTelShuffle == true)
          {
            outData.println("  tINI DBIni(Connection);");
            outData.println("  DBIni.QueryValue(\"{Sql Logon String}\", Connection, \"DBLOGON\");");
          }
        }
        outData.println("#ifdef UPM_AWARE");
        outData.println("  Ini.QueryValue(\"{User ID}\", UserID, \"UPM\");");
        outData.println("  Ini.QueryValue(\"{HostName}\", HostName, \"UPM\");");
        outData.println("#endif");
        outData.println("}");
        outData.println();
        outData.println("void t"+module.name+"Server::ServiceClient()");
        outData.println("{");
        outData.println("#if defined(M_AIX) || defined(M_GNU)");
        outData.println("  sprintf(LogPID, \"[%d] \", getpid());");
        outData.println("  LogFile->SetCustomString(LogPID);");
        outData.println("#else");
        outData.println("  LogFile->SetCustomString(\"\");");
        outData.println("#endif");
        if (doConnect == true)
        {
          outData.println("  if (!Connect) return;");
          outData.println("  try");
          outData.println("  {");
          outData.println("    Connect->Logon(BinFile, Connection);");
          if (doNedTelShuffle == true)
            outData.println("    Connection = \"<removed>\";");
          outData.println("    Connect->CB()->isVB = -1; // Kludge for Server C++ but Client VB");
          outData.println("    Connect->SetTimeout(DBTimeout); // Timeout to kill transaction in seconds (0=no timeout)");
        }
        else
        {
          outData.println("  try");
          outData.println("  {");
        }
        outData.println("    RPC = new tRPCServer(*Sock);");
        outData.println("#ifdef UPM_AWARE");
        outData.println("    RPC->UserID(UserID);");
        outData.println("    RPC->HostName(HostName);");
        outData.println("#endif");
        outData.println("    RPC->mLogReceived = (logreceive == 0) ? false : true;");
        outData.println("    RPC->mLogTransmitted = (logtransmit == 0) ? false : true;");
        if (doMetrics == true)
          outData.println("    RPC->mMetrics = (metrics == 0) ? false : true;");
        outData.println("    "+module.name+"->setRPC(RPC);");
        outData.println("    "+module.name+"->StartUpCode();");
        outData.println("    while (RPC->Shutdown() == false) // Recoverable exceptions Loop");
        outData.println("    {");
        outData.println("      int CountDown = 10;");
        outData.println("      try");
        outData.println("      {");
        outData.println("        ReplyBody=0;");
        outData.println("        ReplySize=0;");
        outData.println("        RPC->WaitForCall();");
        outData.println("        if (RPC->Shutdown() == true)");
        outData.println("          break;");
        outData.println("        char* ip = (char*)RPC->RxBuffer();");
        outData.println("        int32 Signature;");
        outData.println("        Signature = *(int32*)ip;");
        outData.println("        SwapBytes(Signature);");
        if (alignForSun == true)
          outData.println("        ip += alignData(sizeof(int32));");
        else
          outData.println("        ip += sizeof(int32);");
        outData.println("        switch (RPC->ReqID())");
        outData.println("        {");
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          if (prototype.code.size() > 0)
            hasCode = true;
          if (prototype.message.length() > 0)
            outData.println("        case "+prototype.message+":");
          else
            outData.println("        case "+i+":");
          outData.println("          LogFile->lprintf(eLogDebug, \"ReqID=%d "+prototype.name+"["+prototype.signature(true)+"]\", RPC->ReqID());");
          outData.println("          if (Signature != "+prototype.signature(true)+")");
          outData.println("            Result = (int32)"+module.name.toUpperCase()+"_INV_SIGNATURE;");
          outData.println("          else");
          outData.println("            Result = (int32)"+prototype.name+"(ip);");
          outData.println("          LogFile->lprintf(eLogDebug, \"Result=%d "+prototype.name+"["+prototype.signature(true)+"]\", Result);");
          outData.println("          break;");
        }
        outData.println("        default:");
        outData.println("          Result = (int32)"+module.name.toUpperCase()+"_UNKNOWN_FUNCTION;");
        outData.println("        }");
        outData.println("        RPC->RespondToCall(Result, ReplyBody, ReplySize);");
        outData.println("        if (ReplyBody)");
        outData.println("          delete[] ReplyBody;");
        outData.println("      }");
        outData.println("      catch(xSockErr &x)");
        outData.println("      {");
        outData.println("        LogFile->Log(x);");
        outData.println("        if (ReplyBody)");
        outData.println("          delete[] ReplyBody;");
        outData.println("        switch (x.Error())");
        outData.println("        {");
        outData.println("          case xSockErr::errSockTimeout:");
        outData.println("            LogFile->Log(\"Timeout -- continuing\");");
        outData.println("            continue;");
        outData.println("          default:");
        outData.println("            if (--CountDown)");
        outData.println("            {");
        outData.println("              LogFile->Log(\"Counting down --  continuing\");");
        outData.println("#if defined(M_AIX) || defined(M_GNU)");
        outData.println("              sleep(5 / CountDown);");
        outData.println("#endif");
        outData.println("              continue;");
        outData.println("            }");
        outData.println("            LogFile->Log(\"Counted out blowing popstand\");");
        outData.println("            throw;");
        outData.println("        }");
        outData.println("      }");
        if (doConnect == true)
        {
          outData.println("      catch (xDBError &x)");
          outData.println("      {");
          outData.println("        if (ReplyBody)");
          outData.println("          delete[] ReplyBody;");
          outData.println("        LogFile->Log(x);");
          outData.println("        Connect->Rollback();");
          outData.println("        Result=(int32)"+module.name.toUpperCase()+"_UNCAUGHT_DB_ERROR;");
          outData.println("      }");
        }
        outData.println("    }");
        if (hasShutDownCode)
          outData.println("    "+module.name+"->ShutDownCode();");
        if (doConnect == true)
          outData.println("    Connect->Logoff();");
        outData.println("    delete RPC;");
        outData.println("  }");
        outData.println("  // Critical exception handlers");
        outData.println("  catch (xCept &x)");
        outData.println("  {");
        outData.println("    LogFile->Log(x);");
        outData.println("  }");
        outData.println("}");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.codeType != Prototype.RPCCALL)
            continue;
          generateCServer(module, prototype, i, outData);
        }
        outData.println("#if defined(M_AIX) || defined(M_GNU) || defined(__MSVC__) || _MSC_VER >= 1200");
        outData.println();
        outData.println("#include \"getargs.h\"");
        outData.println();
        outData.println("static ARG argtab[] =");
        outData.println("{{'c', STRING, (int*)&ConfigFile,    \"Which config file\"}");
        outData.println("};");
        outData.println("#define TABSIZE (sizeof(argtab) / sizeof(ARG))");
        outData.println();
        outData.println("t"+module.name+"Server *"+module.name+"Server;");
        outData.println("");
        outData.println("#if defined(M_AIX)");
        outData.println("void ShutdownHandler(int arg)");
        outData.println("{");
        outData.println("   "+module.name+"Server->RPC->SetShutdown();");
        outData.println("}");
        outData.println("#elif defined(M_GNU)");
        outData.println("#include <sys/signal.h>");
        outData.println("extern \"C\" void ShutdownHandler(int arg)");
        outData.println("{");
        outData.println("   "+module.name+"Server->RPC->SetShutdown();");
        outData.println("}");
        outData.println("#endif");
        outData.println();
        outData.println("int main(int argc, char *argv[])");
        outData.println("{");
        outData.println("  int rc=0;");
        outData.println("  try");
        outData.println("  {");
        outData.println("#if defined(M_AIX) || defined(M_GNU)");
        outData.println("    Init();");
        outData.println("#endif");
        outData.println("    argc = GetArgs(argc, argv, argtab, TABSIZE);");
        outData.println("    "+module.name+"Server = new t"+module.name+"Server(ConfigFile);");
        outData.println("#if defined(M_AIX)");
        outData.println("    sleep(1);");
        outData.println("    struct sigaction Action, OAction;");
        outData.println("    Action.sa_mask.losigs = 0;");
        outData.println("    Action.sa_mask.hisigs = 0;");
        outData.println("    Action.sa_flags = 0;");
        outData.println("    Action.sa_handler = ShutdownHandler;");
        outData.println("    sigaction(SIGUSR1, &Action, &OAction);");
        outData.println("#elif defined(M_GNU)");
        outData.println("    sleep(1);");
        outData.println("    signal(SIGUSR1, ShutdownHandler);");
        outData.println("#endif");
        outData.println("    "+module.name+"Server->CreateChildren();");
        outData.println("#if defined(_SNAPPROCESS_HAS_EXITCODE)");
        outData.println("    // see snapprocess.h for a discussion on exitCode");
        outData.println("    rc="+module.name+"Server->exitCode;");
        outData.println("#endif");
        outData.println("    delete "+module.name+"Server;");
        outData.println("  }");
        outData.println("  catch (xCept &x)");
        outData.println("  {");
        outData.println("    cerr << \"Uncaught xCept error\" << endl << flush;");
        outData.println("    DisplayVerbose(x, cout);");
        outData.println("    rc=-1;");
        outData.println("  }");
        outData.println("  catch (...)");
        outData.println("  {");
        outData.println("    cout << \"General error trapped here ...\" << endl << flush;");
        outData.println("    rc=-2;");
        outData.println("  }");
        outData.println("  return rc;");
        outData.println("}");
        outData.println("#endif");
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
    outData.println("e"+module.name+" t"+module.name+"Server::"+prototype.name+"(char *ip)");
    outData.println("{");
    outData.println("  e"+module.name+" resultCode = "+module.name.toUpperCase()+"_OK;");
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
      byte savedReference = field.type.reference;
      //Action output = (Action) prototype.getOutputAction(field.name);
      //if (output != null)
      //  op = output.sizeOperation();
      if (field.type.reference == Type.BYPTR
      ||  field.type.reference == Type.BYREFPTR)
      {
        if (input.hasSize() == false)
        {
          if (field.type.typeof == Type.CHAR)
          {
            outData.println("    RecvSize = *(int32*)ip;");
            outData.println("    SwapBytes(RecvSize);");
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof(int32*));");
            else
              outData.println("    ip += sizeof(int32*);");
            outData.println("    char* "+field.name+" = ip;");
            if (addUp)
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(RecvSize);");
              else
                outData.println("    ip += RecvSize;");
            }
          }
          else
          {
            outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
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
          outData.println("    RecvSize = *(int32*)ip;");
          outData.println("    SwapBytes(RecvSize);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32*));");
          else
            outData.println("    ip += sizeof(int32*);");
          outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(RecvSize);");
            else
              outData.println("    ip += RecvSize;");
          }
        }
      }
      else
      {
        outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
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
    boolean hasResult = false;
    if (prototype.type.typeof != Type.VOID
    ||  prototype.type.reference == Type.BYPTR)
    {
      if (prototype.type.reference != Type.BYPTR)
      {
        if (alignForSun == true)
          outData.println("    ReplySize += alignData((int32)sizeof("+prototype.type.cName(false)+")); // Result Size");
        else
          outData.println("    ReplySize += (int32)sizeof("+prototype.type.cName(false)+"); // Result Size");
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
          {
            if (alignForSun == true)
              outData.println("    ReplySize += alignData(sizeof("+field.type.cName(false)+")); // size "+field.name);
            else
              outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          if (alignForSun == true)
            outData.println("    ReplySize += alignData(sizeof(int32));  // size of Block");
          else
            outData.println("    ReplySize += sizeof(int32);  // size of Block");
          if (alignForSun == true)
            outData.println("    ReplySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cName(false)+")); // size "+field.name);
          else
            outData.println("    ReplySize += ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+")); // size "+field.name);
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
            outData.println("    ReplySize += alignData(sizeof("+field.type.cName(false)+"*)); // Variant "+field.name);
          else
            outData.println("    ReplySize += sizeof("+field.type.cName(false)+"*); // Variant "+field.name);
        }
      }
      else
      {
        if (alignForSun == true)
          outData.println("    ReplySize += alignData(sizeof("+field.type.cName(false)+")); // size "+field.name);
        else
          outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
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
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof("+prototype.type.cName(false)+"));");
          else
            outData.println("    ip += sizeof("+prototype.type.cName(false)+");");
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
      //if (op != null)
      //{
      //  opInput = (Action) prototype.getInputAction(op.name);
      //  opOutput = (Action) prototype.getOutputAction(op.name);
      //}
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
            {
              if (alignForSun == true)
                outData.println("    ip += alignData(sizeof("+field.type.cName(false)+"));");
              else
                outData.println("    ip += sizeof("+field.type.cName(false)+");");
            }
          }
        }
        else
        {
          String w1 = "";
          if (op.isConstant == false)
            w1 = "*";
          outData.println("    *(int32*)ip = ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+"));");
          outData.println("    SwapBytes(*(int32*)ip);");
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof(int32));");
          else
            outData.println("    ip += sizeof(int32);");
          if (input != null)
          {
            outData.println("    memcpy(ip, "+field.name+", ("+w1+op.name+" * sizeof("+field.type.cName(false)+")));");
            outData.println("    "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          }
          else
            outData.println("    "+field.type.cName(false)+"* "+field.name+" = ("+field.type.cName(false)+"*)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData("+w1+op.name+" * sizeof("+field.type.cName(false)+"));");
            else
              outData.println("    ip += ("+w1+op.name+" * sizeof("+field.type.cName(false)+"));");
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
          outData.println("    Variants["+(Variants++)+"] = ip-ReplyBody;");
          if (input != null)
            outData.println("    ("+field.type.cName(false)+"*)*ip = "+field.name+";");
          else
            outData.println("    "+field.type.cName(false)+"** "+field.name+" = ("+field.type.cName(false)+"**)ip;");
          if (addUp)
          {
            if (alignForSun == true)
              outData.println("    ip += alignData(sizeof("+field.type.cName(false)+"*));");
            else
              outData.println("    ip += sizeof("+field.type.cName(false)+"*);");
          }
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
        {
          if (alignForSun == true)
            outData.println("    ip += alignData(sizeof("+field.type.cName(false)+"));");
          else
            outData.println("    ip += sizeof("+field.type.cName(false)+");");
        }
      }
    }
    outData.println("    try");
    outData.println("    {");
    String w1 = "", w2 = "";
    outData.print("      ");
    if (hasResult)
      outData.print("*Result = ");
    outData.print(module.name+"->"+prototype.name+"(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field) prototype.parameters.elementAt(i);
      if (field.type.reference == Type.BYPTR)
      //||  field.type.reference == Type.BYREFPTR)
        w2 = "";
      else
        w2 = "*";
      outData.print(w1 + w2 + field.name);
      w1 = ", ";
    }
    outData.println(");");
    outData.println("    }");
    outData.println("    catch (e"+module.name+" rc)");
    outData.println("    {");
    outData.println("      resultCode = rc;");
    outData.println("    }");
    if (hasResult && prototype.needsSwap())
      outData.println("    SwapBytes(*Result);");
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
          if (alignForSun == true)
            outData.println("    ReplySize += alignData((int32)sizeof("+prototype.type.cName(false)+")); // Result Size");
          else
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
            {
              if (alignForSun == true)
                outData.println("    ReplySize += alignData(sizeof("+field.type.cName(false)+")); // size "+field.name);
              else
                outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
            }
          }
          else
          {
            w1 = "";
            if (op.isConstant == false)
              w1 = "*";
            if (alignForSun == true)
            {
              outData.println("    ReplySize += alignData(sizeof(int32));  // size of Block");
              outData.println("    ReplySize += alignData("+w1+""+op.name+" * sizeof("+field.type.cName(false)+")); // size "+field.name);
            }
            else
            {
              outData.println("    ReplySize += sizeof(int32);  // size of Block");
              outData.println("    ReplySize += ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+")); // size "+field.name);
            }
            if (field.type.reference == Type.BYREFPTR)
              outData.println("    VariantSize["+(VariantSize++)+"] = ("+w1+""+op.name+" * sizeof("+field.type.cName(false)+"));");
          }
        }
        else
        {
          if (alignForSun == true)
            outData.println("    ReplySize += alignData(sizeof("+field.type.cName(false)+")); // size "+field.name);
          else
            outData.println("    ReplySize += sizeof("+field.type.cName(false)+"); // size "+field.name);
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
      outData.println("    ReplyBody = new char [ReplySize];");
      outData.println("    memset(ReplyBody, 0, ReplySize);");
      outData.println("    char *op = ReplyBody;");
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
      outData.println("    delete [] oldReplyBody;");
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
    outData.println("  catch (e"+module.name+" rc)");
    outData.println("  {");
    outData.println("    return rc;");
    outData.println("  }");
    outData.println("  return resultCode;"); // +module.name.toUpperCase()+"_OK;");
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

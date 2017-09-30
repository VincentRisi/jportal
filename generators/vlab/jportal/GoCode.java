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

package vlab.jportal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class GoCode extends Generator
{
  private static PlaceHolder placeHolder;
  public static void main(String[] args)
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate GO Code");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Database database = (Database)in.readObject();
        in.close();
        generate(database, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  public static String description()
  {
    return "Generate GO Code";
  }
  public static String documentation()
  {
    return "Generate GO Code";
  }
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    for (int i=0; i<database.tables.size(); i++)
    {
      Table table = (Table) database.tables.elementAt(i);
      generateTable(table, output, outLog);
    }
  }
  static void println(PrintWriter pw)
  {
    pw.println();
  }
  static void println(PrintWriter pw, String line)
  {
    String newline = line.replace("`", "    ");
    pw.println(newline);
  }
  static void print(PrintWriter pw, String line)
  {
    String newline = line.replace("`", "    ");
    pw.print(newline);
  }
  static String packageName(Table table)
  {
    return table.database.packageName.toLowerCase();
  }
  private static void generateTable(Table table, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+table.useName() + ".go");
      OutputStream outFile = new FileOutputStream(output+table.useName() + ".go");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        println(outData, "//This code is generated - it is inadvisable to modify generated code.");
        println(outData);
        println(outData, String.format("package %s", packageName(table)));
        println(outData);
        println(outData, "import (");
        println(outData, "`\"database/sql\"");
        println(outData, ")");
        println(outData);
        if (table.hasStdProcs)
          generateStdRec(table, outData);
        generateUserRecs(table, outData);
        generateImpl(table, outData);
        outData.flush();
      }
      finally
      {
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  private static void generateStdRec(Table table, PrintWriter outData)
  {
    println(outData, String.format("type %sRec struct {", table.useName()));
    for (Field field :table.fields)
      println(outData, fieldDef(field));
    println(outData, "}");
    println(outData);
  }
  private static void generateUserRecs(Table table, PrintWriter outData)
  {
    for (Proc proc : table.procs)
    {
      if (proc.isData || proc.isStd || proc.hasNoData())
        continue;
      if (proc.isStdExtended())
        continue;
      println(outData, String.format("type %s%sRec struct {", table.useName(), proc.upperFirst()));
      for (Field field : proc.inputs)
        println(outData, fieldDef(field));
      for (Field field : proc.outputs)
        if (proc.hasInput(field.name) == false)
          println(outData, fieldDef(field));
      for (int i=0; i<proc.dynamics.size(); i++)
      {
        String name = proc.dynamics.elementAt(i);
        Integer len = proc.dynamicSizes.elementAt(i);
        //Boolean strung = proc.dynamicStrung.elementAt(i);
        println(outData, String.format("`%s string // %d", name, len.intValue()));
      }
      println(outData, "}");
      println(outData);
    }
  }
  private static void generateImpl(Table table, PrintWriter outData)
  {
    for (Proc proc : table.procs)
      if (proc.isData == false)
        generateProc(table, proc, outData);
    
  }
  private static void generateProc(Table table, Proc proc, PrintWriter outData)
  {
    placeHolder = new PlaceHolder(proc, PlaceHolder.AT_NAMED, "");
    String recUsed = generateCommand(proc, outData);
    if (proc.isMultipleInput)
      generateMultipleImplementation(table, proc, recUsed, outData);
    else
      generateImplementation(table, proc, recUsed, outData);
  }
  static String check(String value)
  {
    String trimmed = value.trim();
    if (trimmed.startsWith("_ret.") == true)
      return trimmed;
    return "rec." + trimmed;
  }
  static String generateCommand(Proc proc, PrintWriter outData)
  {
    boolean isReturning = false;
    boolean isBulkSequence = false;
    String parms = "";
    String recUsed = "";
    if (proc.isStd == true || proc.isStdExtended() == true)
      recUsed = String.format("%sRec", proc.table.useName());
    else if (proc.hasNoData() == false)
      recUsed = String.format("%s%sRec", proc.table.useName(), proc.upperFirst());
    Vector<String> lines = placeHolder.getLines();
    if (proc.isInsert == true && proc.hasReturning == true && proc.outputs.size() == 1)
    {
      Field field = (Field)proc.outputs.elementAt(0);
      if (field.isSequence == true)
        isReturning = true;
    }
    if (proc.isMultipleInput == true && proc.isInsert == true)
      isBulkSequence = true;
    if (isReturning == true || isBulkSequence == true)
      parms = "ret GoRet";
    if (proc.hasNoData() == true)
      print(outData, String.format("func %s%sCmd() string {\n`var result = \"\"", proc.table.useName(), proc.name, parms));
    else
      print(outData, String.format("func (rec *%s) %sCmd(%s) string {\n`var result = \"\"", recUsed, proc.name, parms));
    if (lines.size() > 0)
    { 
      String endLine = " + \"\\n\" + \n``";
      for (int i = 0; i < lines.size(); i++)
      {
        String l = (String)lines.elementAt(i);
        if (l.charAt(0) != '"')
          print(outData, endLine + check(l));
        else
          print(outData, endLine + l);
      }
      println(outData, "\n`return result\n}\n");
    }
    return recUsed;
  }
  private static void generateImplementation(Table table, Proc proc, String recUsed, PrintWriter outData)
  {
    println(outData, String.format("func (rec *%s) %sExec() {", recUsed, proc.name));
    println(outData, "}\n");
  }
  private static void generateMultipleImplementation(Table table, Proc proc, String recUsed, PrintWriter outData)
  {
  }
  private static String fieldDef(Field field)
  {
    switch(field.type)
    {
    case Field.BOOLEAN:
      return String.format("`%s bool", field.useName());
    case Field.BYTE:
      return String.format("`%s int8", field.useName());
    case Field.SHORT:
      return String.format("`%s int16", field.useName());
    case Field.INT:
    case Field.SEQUENCE:
    case Field.IDENTITY:
      return String.format("`%s int32", field.useName());
    case Field.LONG:
    case Field.BIGSEQUENCE:
    case Field.BIGIDENTITY:
      return String.format("`%s int64", field.useName());
    case Field.CHAR:
    case Field.ANSICHAR:
      return String.format("`%s string // %d", field.useName(), field.length+1);
    case Field.DATE:
      return String.format("`%s string // 9", field.useName());
    case Field.DATETIME:
      return String.format("`%s string // 15", field.useName());
    case Field.TIME:
      return String.format("`%s string // 7", field.useName());
    case Field.TIMESTAMP:
      return String.format("`%s string // 15", field.useName());
    case Field.FLOAT:
    case Field.DOUBLE:
      return String.format("`%s float64", field.useName());
    case Field.BLOB:
    case Field.TLOB:
      return String.format("`%s string", field.useName());
    case Field.MONEY:
      return String.format("`%s string", field.useName());
    case Field.USERSTAMP:
      return String.format("`%s string", field.useName());
    }
    return field.useName() + " : <unsupported>";
  }
  
}

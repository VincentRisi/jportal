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
import java.util.HashMap;

public class OpenApiCode extends Generator
{
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
        outLog.println(args[i]+": Generate OpenApi Schema");
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
    return "Generate OpenApi Schema";
  }
  public static String documentation()
  {
    return "Generate OpenApi Schema";
  }
  static String padder(String s, int length)
  {
    for (int i = s.length(); i < length-1; i++)
      s = s + " ";
    return s + " ";
  }
  //private static PrintWriter writer;
  //private static String format(String fmt, Object... objects)
  //{
  //  return String.format(fmt,  objects);
  //}
  //private static void println(int no, String value)
  //{
  //  writer.println(indent(no)+value);
  //}
  //private static void println(String value)
  //{
  //  println(0, value);
  //}
  //private static void println()
  //{
  //  writer.println();
  //}
  //private static String indent_string = "                                                                                             ";
  //static int indent_size = 2;
  //static String indent(int no)
  //{
  //   int max = indent_string.length();
  //   int to = no * indent_size;
  //   if (to > max)
  //     to = max;
  //   return indent_string.substring(0,  to);
  //}
  /**
  * Generates the procedure classes for each table present.
  */
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    for (int i=0; i<database.tables.size(); i++)
    {
      Table table = (Table) database.tables.elementAt(i);
      generateTable(table, output, outLog);
    }
  }
  static String structName = "";
  private static void generateTable(Table table, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + table.useName().toLowerCase() + ".yaml");
      OutputStream outFile = new FileOutputStream(output + table.useName().toLowerCase() + ".yaml");
      try
      {
        writer = new PrintWriter(outFile);
        indent_size = 2;
        writeln("%YAML 1.2");
        writeln("---");
        writeln("# This code was generated, do not modify it, modify it at source and regenerate it.");
        writeln("components:");
        writeln(1, "schemas:");
        try
        {
          if (table.hasStdProcs)
            generateStdOutputRec(table);
          generateUserOutputRecs(table);
          writeln("...");
        }
        finally
        {
          writer.flush();
          writer.close();
        }
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
  private static void generateUserOutputRecs(Table table)
  {
    for (int i = 0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc)table.procs.elementAt(i);
      if (proc.isData || proc.isStd || proc.hasNoData())
        continue;
      if (proc.isStdExtended())
        continue;
      String work = "";
      String baseClass = "";
      boolean canExtend = true;
      Vector fields = proc.outputs;
      for (int j = 0; j < fields.size(); j++)
      {
        Field field = (Field)fields.elementAt(j);
        if (field.type == Field.BLOB)
          canExtend = false;
      }
      fields = proc.inputs;
      for (int j = 0; j < fields.size(); j++)
      {
        Field field = (Field)fields.elementAt(j);
        if (field.type == Field.BLOB)
          canExtend = false;
      }
      fields = proc.outputs;
      if (fields.size() > 0)
      {
        String typeChar = "D";
        if (proc.hasDiscreteInput())
          typeChar = "O";
        work = " : public " + typeChar + table.useName() + proc.upperFirst();
        //baseClass = typeChar + table.useName() + proc.upperFirst();
        structName = typeChar + table.useName() + proc.upperFirst();
        writeln(2, structName + ":");
        writeln(3, "type: object");
        writeln(3, "properties:");
        if (fields.size() > 0)
          writeln(4, "# outputs");
        for (int j = 0; j < fields.size(); j++)
        {
          Field field = (Field)fields.elementAt(j);
          writeln(4, field.useName() + ":");
          fieldType(field, 5);
          if (isNull(field))
          {
            writeln(4, field.useName() + "IsNull:");
            writeln(5, "type: "+"integer");
            writeln(5, "format: int16");
          }
        }
        writeln(3, "required:");
        fieldsRequired(fields, 4);
      }
      if (proc.hasDiscreteInput())
      {
        structName = "D" + table.useName() + proc.upperFirst();
        writeln(2, structName + ":");
        writeln(3, "type: object");
        writeln(3, "properties:");
        Vector inputs = proc.inputs;
        if (inputs.size() > 0)
          writeln(4, "# inputs");
        for (int j = 0; j < inputs.size(); j++)
        {
          Field field = (Field)inputs.elementAt(j);
          //if (proc.hasOutput(field.name))
          //  continue;
          writeln(4, field.useName() + ":");
          fieldType(field, 5);
          if (isNull(field))
          {
            writeln(4, field.useName() + "IsNull:");
            writeln(5, "type: "+"integer");
            writeln(5, "format: int16");
          }
        }
        Vector outputs = proc.outputs;
        if (outputs.size() > 0)
          writeln(4, "# outputs");
        for (int j = 0; j < fields.size(); j++)
        {
          Field field = (Field)fields.elementAt(j);
          if (proc.hasInput(field.name))
            continue;
          writeln(4, field.useName() + ":");
          fieldType(field, 5);
          if (isNull(field))
          {
            writeln(4, field.useName() + "IsNull:");
            writeln(5, "type: "+"integer");
            writeln(5, "format: int16");
          }
        }
        if (proc.dynamics.size() > 0)
          writeln(4, "# dynamics");
        for (int j = 0; j < proc.dynamics.size(); j++)
        {
          String s = (String)proc.dynamics.elementAt(j);
          Integer n = (Integer)proc.dynamicSizes.elementAt(j);
          writeln(4, s + ":");
          writeln(5, "type: string");
          writeln(5, "maxLength: "+n.intValue());
        }
        writeln(3, "required:");
        fieldsRequired(inputs, 4);
        for (int j = 0; j < fields.size(); j++)
        {
          Field field = (Field)fields.elementAt(j);
          if (proc.hasInput(field.name))
            continue;
          writeln(4, "- " + field.useName());
          if (isNull(field))
            writeln(4, "- " + field.useName() + "IsNull");
        }
        for (int j = 0; j < proc.dynamics.size(); j++)
        {
          String s = (String)proc.dynamics.elementAt(j);
          writeln(4, "- " + s);
        }
      }
    }
  }
  private static void generateStdOutputRec(Table table)
  {
    structName = "D" + table.useName();
    writeln(2, structName + ":");
    writeln(3, "type: object");
    writeln(3, "properties:");
    writeln(4, "# table fields");
    Vector fields = table.fields;
    for (int i = 0; i < fields.size(); i++)
    {
      Field field = (Field)fields.elementAt(i);
      writeln(4, field.useName() + ":");
      fieldType(field, 5);
      if (isNull(field))
      {
        writeln(4, field.useName() + "IsNull:");
        writeln(5, "type: "+"integer");
        writeln(5, "format: int16");
      }
    }
    writeln(3, "required:");
    fieldsRequired(fields, 4);
  }
  private static void fieldsRequired(Vector fields, int ind)
  {
    for (int i = 0; i < fields.size(); i++)
    {
      Field field = (Field)fields.elementAt(i);
      writeln(ind, "- " + field.useName());
      if (isNull(field))
        writeln(ind, "- " + field.useName() + "IsNull");
    }
  }
  static boolean isNull(Field field)
  {
    if (field.isNull == false)
      return false;
    switch (field.type)
    {
      case Field.BOOLEAN:
      case Field.FLOAT:
      case Field.DOUBLE:
      case Field.MONEY:
      case Field.BYTE:
      case Field.SHORT:
      case Field.INT:
      case Field.LONG:
      case Field.IDENTITY:
      case Field.SEQUENCE:
      case Field.BIGIDENTITY:
      case Field.BIGSEQUENCE:
      case Field.BLOB:
      case Field.DATE:
      case Field.DATETIME:
      case Field.TIMESTAMP:
      case Field.AUTOTIMESTAMP:
      case Field.TIME:
      //case Field.XML:
        return true;
    }
    return false;
  }
  static void fieldType(Field field, int ind)
  {
    switch (field.type)
    {
      case Field.BOOLEAN:
      case Field.BYTE:
      case Field.SHORT:
        writeln(ind, "type: "+"integer");
        writeln(ind, "format: int16");
        break;
      case Field.INT:
      case Field.IDENTITY:
      case Field.SEQUENCE:
        writeln(ind, "type: "+"integer");
        writeln(ind, "format: int32");
        break;
      case Field.LONG:
      case Field.BIGIDENTITY:
      case Field.BIGSEQUENCE:
        writeln(ind, "type: "+"integer");
        writeln(ind, "format: int64");
        break;
      case Field.CHAR:
      case Field.ANSICHAR:
      case Field.TLOB:
      case Field.XML:
      case Field.DATE:
      case Field.TIME:
      case Field.USERSTAMP:
      case Field.DATETIME:
      case Field.TIMESTAMP:
      case Field.AUTOTIMESTAMP:
        writeln(ind, "type: "+"string");
        writeln(ind, format("maxLength: %d", field.length));
        break;
      case Field.BLOB:
      //case Field.IMAGE:  
        writeln(ind, "type: "+"string");
        writeln(ind, format("maxLength: %d", field.length));
        break;
      case Field.FLOAT:
      case Field.DOUBLE:
        if (field.precision > 15)
        {
          writeln(ind, "type: "+"string");
          writeln(ind, format("maxLength: %d", field.precision+2));
          break;
        }
        writeln(ind, "type: "+"number");
        writeln(ind, "format: double");
        break;
      case Field.MONEY:
        writeln(ind, "type: "+"string");
        writeln(ind, format("maxLength: %d", 20));
        break;
      default:
        writeln(ind, "type: "+field.type);
        break;
    }
  }
}

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

public class JPortalSI extends Generator
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
        outLog.println(args[i]+": generate JPortal SI");
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
  /**
  * Generates the procedure classes for each table present.
  */
  public static String description()
  {
    return "generate JPortal SI";
  }
  public static String documentation()
  {
    return "generate JPortal SI";
  }
  static String pad(String s, int length)
  {
    for (int i = s.length(); i < length-1; i++)
      s = s + " ";
    return s + " ";
  }
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println(output+database.name + ".si");
      OutputStream outFile = new FileOutputStream(output+database.name + ".si");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println(pad("DATABASE", 10)+database.name);
        for (int i=0; i<database.flags.size(); i++)
          outData.println(pad("FLAGS", 10)+"\""+(String) database.flags.elementAt(i)+"\"");
        if (database.packageName.length() > 0)
          outData.println(pad("PACKAGE", 10)+database.packageName);
        if (database.output.length() > 0)
          outData.println(pad("OUTPUT", 10)+database.output);
        if (database.server.length() > 0)
          outData.println(pad("SERVER", 10)+database.server);
        if (database.userid.length() > 0)
          outData.println(pad("USERID", 10)+database.userid);
        if (database.password.length() > 0)
          outData.println(pad("PASSWORD", 10)+database.password);
        outData.println();
        for (int i=0; i<database.tables.size(); i++)
          generate((Table) database.tables.elementAt(i), outData, outLog);
        for (int i=0; i<database.views.size(); i++)
          generate((View) database.views.elementAt(i), outData, outLog);
      }
      finally
      {
        outData.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  static void generate(Table table, PrintWriter outData, PrintWriter outLog)
  {
    String line = pad("TABLE", 8)+table.name;
    if (table.alias.length() > 0)
      line = pad(line,20) + "(" + table.alias+ ")";
    if (table.comments.size() > 0)
      line = pad(line, 56) + (String) table.comments.elementAt(0);
    outData.println(line);
    for (int i = 1; i < table.comments.size(); i++)
      outData.println(pad("", 56) + "** " + (String) table.comments.elementAt(i));
    for (int i = 0; i < table.options.size(); i++)
      outData.println(pad("OPTIONS", 8) + "\"" + (String) table.options.elementAt(i) + "\"");
    for (int i = 0; i < table.fields.size(); i++)
      generate((Field) table.fields.elementAt(i), outData, outLog);
    outData.println();
    for (int i = 0; i < table.grants.size(); i++)
      generate((Grant) table.grants.elementAt(i), outData, outLog);
    outData.println();
    for (int i = 0; i < table.keys.size(); i++)
      generate((Key) table.keys.elementAt(i), outData, outLog);
    for (int i = 0; i < table.links.size(); i++)
      generate((Link) table.links.elementAt(i), outData, outLog);
    for (int i = 0; i < table.views.size(); i++)
      generate((View) table.views.elementAt(i), outData, outLog);
    for (int i = 0; i < table.procs.size(); i++)
      generate((Proc) table.procs.elementAt(i), outData, outLog);
    outData.println();
  }
  static void generate(Field field, PrintWriter outData, PrintWriter outLog)
  {
    String line = "  "+field.name;
    String ft = "";
    if (field.alias.length() > 0)
      line = pad(line,20) + "(" + field.alias+ ")";
    switch (field.type)
    {
    case Field.BLOB:
      ft = "blob";
      break;
    case Field.BOOLEAN:
      ft = "boolean";
      break;
    case Field.BYTE:
      ft = "byte";
      break;
    case Field.CHAR:
      ft = "char ("+field.length+")";
      break;
    case Field.ANSICHAR:
      ft = "ansichar ("+field.length+")";
      break;
    case Field.DATE:
      ft = "date";
      break;
    case Field.DATETIME:
      ft = "datetime";
      break;
    case Field.DOUBLE:
      ft = "double";
      if (field.precision > 0 || field.scale > 0)
        ft = ft + "(" + field.precision + "," + field.scale + ")";
      break;
    case Field.DYNAMIC:
      return;
    case Field.FLOAT:
      ft = "float";
      if (field.precision > 0 || field.scale > 0)
        ft = ft + "(" + field.precision + "," + field.scale + ")";
      break;
    case Field.IDENTITY:
      ft = "identity";
      break;
    case Field.INT:
      ft = "int";
      break;
    case Field.LONG:
      ft = "long";
      break;
    case Field.MONEY:
      ft = "double (15,2)";
      break;
    case Field.SEQUENCE:
      ft = "sequence";
      break;
    case Field.SHORT:
      ft = "short";
      break;
    case Field.STATUS:
      return;
    case Field.TIME:
      ft = "time";
      break;
    case Field.TIMESTAMP:
      ft = "timestamp";
      break;
    case Field.TLOB:
      ft = "tlob";
      break;
    case Field.USERSTAMP:
      ft = "userstamp";
      break;
    }
    line = pad(line, 36) + ft;
    if (field.isNull)
      line = pad(line, 48) + "    NULL";
    else
      line = pad(line, 48) + "NOT NULL";
    if (field.comments.size() > 0)
      line = pad(line, 56) + (String) field.comments.elementAt(0);
    outData.println(line);
    for (int i = 1; i < field.comments.size(); i++)
      outData.println(pad("", 56) + "** " + (String) field.comments.elementAt(i));
  }
  static void generate(Grant grant, PrintWriter outData, PrintWriter outLog)
  {
    String line = "GRANT";
    for (int i = 0; i < grant.perms.size(); i++)
      line = line + " " + (String) grant.perms.elementAt(i);
    line = line + " TO";
    for (int i = 0; i < grant.users.size(); i++)
      line = line + " " + (String) grant.users.elementAt(i);
    outData.println(line);
  }
  static void generate(Key key, PrintWriter outData, PrintWriter outLog)
  {
    outData.println(pad("KEY", 8) + key.name);
    for (int i = 0; i < key.options.size(); i++)
      outData.println(pad("OPTIONS", 8) + "\"" + (String) key.options.elementAt(i) + "\"");
    if (key.isPrimary)
      outData.println("PRIMARY");
    else if (key.isUnique)
      outData.println("UNIQUE");
    for (int i = 0; i < key.fields.size(); i++)
      outData.println("  " + (String) key.fields.elementAt(i));
    outData.println();
  }
  static void generate(Link link, PrintWriter outData, PrintWriter outLog)
  {
    outData.println(pad("LINK", 8) + link.name);
    for (int i = 0; i < link.fields.size(); i++)
      outData.println("  " + (String) link.fields.elementAt(i));
    outData.println();
  }
  static void generate(View view, PrintWriter outData, PrintWriter outLog)
  {
    outData.println(pad("VIEW", 8) + view.name);
    if (view.users.size() > 0)
    {
      String line = pad("TO", 7);
      for (int i = 0; i < view.users.size(); i++)
        line = line + (String) view.users.elementAt(i);
    }
    outData.println("OUTPUT");
    for (int i = 0; i < view.aliases.size(); i++)
      outData.println("  " + (String) view.aliases.elementAt(i));
    outData.println("CODE");
    for (int i = 0; i < view.lines.size(); i++)
      outData.println("  '" + (String) view.lines.elementAt(i) + "'");
    outData.println("ENDCODE");
    outData.println();
  }
  static void generate(Proc proc, PrintWriter outData, PrintWriter outLog)
  {
    boolean stdProc = true;
    if (proc.name.compareTo("Insert") == 0
    ||  proc.name.compareTo("Update") == 0
    ||  proc.name.compareTo("DeleteOne") == 0
    ||  proc.name.compareTo("DeleteAll") == 0
    ||  proc.name.compareTo("SelectOne") == 0
    ||  proc.name.compareTo("SelectAll") == 0
    ||  proc.name.compareTo("Count") == 0
    ||  proc.name.compareTo("Exists") == 0)
      outData.println(pad("PROC", 8) + proc.name);
    else if (proc.name.compareTo("SelectOneUpd") == 0)
      outData.println(pad("PROC", 8) + "SelectOne FOR UPDATE");
    else if (proc.name.compareTo("SelectAllUpd") == 0)
      outData.println(pad("PROC", 8) + "SelectAll FOR UPDATE");
    else if (proc.name.compareTo("SelectAllSorted") == 0)
      outData.println(pad("PROC", 8) + "SelectAll IN ORDER");
    else if (proc.name.compareTo("SelectAllSortedUpd") == 0)
      outData.println(pad("PROC", 8) + "SelectAll IN ORDER FOR UPDATE");
    else
    {
      outData.println(pad("PROC", 8) + proc.name);
      stdProc = false;
    }
    for (int i=0; i < proc.comments.size(); i++)
      outData.println(pad("**", 56) + (String) proc.comments.elementAt(i));
    for (int i=0; i < proc.options.size(); i++)
      outData.println(pad("OPTIONS", 8) + "'" + (String) proc.options.elementAt(i) + "'");
    if (stdProc)
      return;
    if (proc.inputs.size() > 0)
    {
      outData.println("INPUT");
      for (int i=0; i < proc.inputs.size(); i++)
        generate((Field) proc.inputs.elementAt(i), outData, outLog);
    }
    if (proc.outputs.size() > 0)
    {
      outData.println("OUTPUT");
      for (int i=0; i < proc.outputs.size(); i++)
        generate((Field) proc.outputs.elementAt(i), outData, outLog);
    }
    proc.isSql = true;
      outData.print("SQL ");
    outData.println("CODE");
    for (int i=0; i < proc.lines.size(); i++)
    {
      Line line = (Line) proc.lines.elementAt(i);
      if (line.isVar)
        outData.println(line.line);
      else
        outData.println("  '"+line.line+"'");
    }
    outData.println("ENDCODE");
    outData.println();
  }
}


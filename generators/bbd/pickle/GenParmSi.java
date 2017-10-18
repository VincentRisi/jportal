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
package bbd.pickle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class GenParmSi
{
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate Domain Access and Update SI file");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Application application = (Application)in.readObject();
        in.close();
        generate(application, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  static String padder(String s, int length)
  {
    for (int i = s.length(); i < length-1; i++)
      s = s + " ";
    return s + " ";
  }
  static boolean genJPortalSI = false;
  static String dynVarMarker = "$";
  static String sqlCode      = "SQL CODE";
  static void setFlags(Application application, PrintWriter outLog)
  {
    for (int i=0; i < application.flags.size(); i++)
    {
      String flag = (String) application.flags.elementAt(i);
      if (flag.equalsIgnoreCase("jportal"))
        genJPortalSI = true;
    }
    if (genJPortalSI)
      outLog.println(" (Generating for JPortal)");
  }
  public static void generate(Application application, String output, PrintWriter outLog)
  {
    setFlags(application, outLog);
    if (genJPortalSI)
    {
      dynVarMarker = "&";
      sqlCode      = "SQLCODE";
    }
    for (int i=0; i<application.tables.size(); i++)
    {
      Table table = (Table) application.tables.elementAt(i);
      generateTable(table, output, outLog);
    }
    for (int i=0; i<application.relations.size(); i++)
    {
      Relation relation = (Relation) application.relations.elementAt(i);
      generateRelation(relation, output, outLog);
    }
  }
  static boolean isNumeric(String s)
  {
    for (int i=0; i<s.length(); i++)
    {
      char ch = s.charAt(i);
      if (ch < '0' || ch > '9')
        if (ch != 'e'  && ch != '.' && ch != 'E' && ch != '-' && ch != '+')
          return false;
    }
    return true;
  }
  static String alias(Table table)
  {
    String result = table.name;
    if (table.alias.length() > 0)
      result += " ("+table.alias+")";
    if (!genJPortalSI && table.isNullable)
      result += " (NULL)";
    if (table.check.length() > 0)
      result += " CHECK \""+table.check+"\"";
    return result;
  }
  static String alias(Field field)
  {
    if (field.alias.length() > 0)
      return field.name+" ("+field.alias+")";
    return field.name;
  }
  static void generateTable(Table table, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+table.name+".si");
      OutputStream outFile = new FileOutputStream(output+table.name+".si");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        try
        {
          Application application = table.application;
          if (genJPortalSI)
          {
            outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
            outData.println();
            outData.println("DATABASE "+application.name);
            outData.println("SERVER   "+application.server);
            outData.println("USERID   "+application.user);
            outData.println("PASSWORD "+application.password);
          }
          else
          {
            outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
            outData.println();
            outData.println("$Parms");
            outData.println("$Descr '"+table.descr+"'");
            if (table.order.size() > 0)
            {
              outData.print("$Lookup ");
              for (int j=0; j<table.order.size(); j++)
              {

                outData.print((j==0) ? "'" : " ");
                Field field = (Field) table.order.elementAt(j);
                outData.print(field.name);
              }
              outData.println("'");
            }
            if (table.show.size() > 0)
            {
              outData.print("$Show ");
              for (int j=0; j<table.show.size(); j++)
              {

                outData.print((j==0) ? "'" : " ");
                Field field = (Field) table.show.elementAt(j);
                outData.print(field.name);
              }
              outData.println("'");
            }
            if (table.noDomain == true)
              outData.println("$NoDomain");
            if (table.viewOnly == true)
              outData.println("$ViewOnly");
            outData.println();
            outData.println("SERVER   @"+application.server);
            outData.println("CONNECT  "+application.user+"/"+application.password);
          }
          outData.println();
          outData.println("TABLE "+alias(table));
          for (int i=0; i<table.fields.size(); i++)
          {
            Field field = (Field) table.fields.elementAt(i);
            outData.println("  "+alias(field)+" "+outputField(field, true));
          }
          if (application.supplieds.size() > 0)
          {
            for (int i=0; i<application.supplieds.size(); i++)
            {
              Field field = (Field) application.supplieds.elementAt(i);
              outData.println("  "+alias(field)+" "+outputField(field, true));
            }
          }
          else
          {
            outData.println("  USId         Char(16)");
            outData.println("  TmStamp      TimeStamp");
          }
          outData.println();
          if (table.keys.size() == 0)
          {
            outData.println("KEY "+table.name+"Key PRIMARY");
            for (int i=0; i<table.order.size(); i++)
            {
              Field field = (Field) table.order.elementAt(i);
              outData.println("  "+field.name);
            }
          }
          else
          {
            for (int i=0; i<table.keys.size(); i++)
            {
              Key key = (Key) table.keys.elementAt(i);
              outData.print  ("KEY "+table.name+key.name);
              if (key.primary)
                outData.println(" PRIMARY");
              else if (key.unique)
                outData.println(" UNIQUE");
              else
                outData.println();
              for (int j=0; j< key.list.size(); j++)
              {
                Field field = (Field) key.list.elementAt(j);
                outData.println("  "+field.name);
              }
            }
          }
          outData.println();
          for (int i=0; i<table.links.size(); i++)
          {
            Link link = (Link) table.links.elementAt(i);
            outData.println("LINK "+link.table.name);
            for (int j=0; j<link.list.size(); j++)
              outData.println("  "+(String)link.list.elementAt(j));
            if (link.hasCascade)
              outData.println("  DELETE CASCADE");
            outData.println();
          }
          outData.println("PROC Insert");
          outData.println("PROC Update");
          outData.println("PROC SelectOne");
          outData.println("PROC SelectAll");
          outData.println("PROC DeleteOne");
          outData.println();
          if (table.values.size() > 0)
          {
            outData.println("DATA");
            for (int i=0; i<table.values.size(); i++)
            {
              Value value = (Value) table.values.elementAt(i);
              outData.print("  INSERT INTO "+table.name+" VALUES(");
              for (int j=0; j<value.list.size(); j++)
              {
                String s = (String) value.list.elementAt(j);
                if (j > 0)
                  outData.print(", ");
                if (isNumeric(s))
                  outData.print(s);
                else
                  outData.print("'"+s+"'");
              }
              outData.println(", 'DEFINED', SYSDATE);");
            }
            outData.println("ENDDATA");
            outData.println();
          }
        }
        finally
        {
          outData.flush();
          outData.close();
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
  static void generateRelation(Relation relation, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+relation.name+".si");
      OutputStream outFile = new FileOutputStream(output+relation.name+".si");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        try
        {
          Application application = relation.application;
          outData.println("// This code was generated, do not modify it, modify it at source and regenerate it.");
          outData.println();
          outData.println("$Parms");
          outData.println("$Descr '"+relation.descr+"'");
          if (genJPortalSI)
          {
            outData.println("DATABASE "+application.name);
            outData.println("SERVER   "+application.server);
            outData.println("USERID   "+application.user);
            outData.println("PASSWORD "+application.password);
          }
          else
          {
            outData.println("SERVER   @"+application.server);
            outData.println("CONNECT  "+application.user+"/"+application.password);
          }
          outData.println();
          outData.println("TABLE "+relation.name);
          for (int i=0; i<relation.fromFields.size(); i++)
          {
            Field field = (Field) relation.fromFields.elementAt(i);
            if (relation.fromShort.length() > 0)
              outData.println("  "+relation.fromShort+field.name+" "+outputField(field));
            else
              outData.println("  "+(String)relation.fromFieldNames.elementAt(i)+" "+outputField(field));
          }
          for (int i=0; i<relation.toFields.size(); i++)
          {
            Field field = (Field) relation.toFields.elementAt(i);
            if (relation.toShort.length() > 0)
              outData.println("  "+relation.toShort+field.name+" "+outputField(field));
            else
              outData.println("  "+(String)relation.toFieldNames.elementAt(i)+" "+outputField(field));
          }
          if (application.supplieds.size() > 0)
          {
            for (int i=0; i<application.supplieds.size(); i++)
            {
              Field field = (Field) application.supplieds.elementAt(i);
              outData.println("  "+alias(field)+" "+outputField(field, true));
            }
          }
          else
          {
            outData.println("  USId         Char(16)");
            outData.println("  TmStamp      TimeStamp");
          }
          outData.println();
          outData.println("KEY "+relation.name+"Key PRIMARY");
          for (int i=0; i<relation.fromFields.size(); i++)
          {
            Field field = (Field) relation.fromFields.elementAt(i);
            if (relation.fromShort.length() > 0)
              outData.println("  "+relation.fromShort+field.name);
            else
              outData.println("  "+(String)relation.fromFieldNames.elementAt(i));
          }
          for (int i=0; i<relation.toFields.size(); i++)
          {
            Field field = (Field) relation.toFields.elementAt(i);
            if (relation.toShort.length() > 0)
              outData.println("  "+relation.toShort+field.name);
            else
              outData.println("  "+(String)relation.toFieldNames.elementAt(i));
          }
          outData.println();
          outData.println("LINK "+relation.fromTable.name);
          for (int i=0; i<relation.fromFields.size(); i++)
          {
            Field field = (Field) relation.fromFields.elementAt(i);
            if (relation.fromShort.length() > 0)
              outData.println("  "+relation.fromShort+field.name);
            else
              outData.println("  "+(String)relation.fromFieldNames.elementAt(i));
          }
          outData.println("  DELETE CASCADE");
          outData.println();
          outData.println("LINK "+relation.toTable.name);
          for (int i=0; i<relation.toFields.size(); i++)
          {
            Field field = (Field) relation.toFields.elementAt(i);
            if (relation.toShort.length() > 0)
              outData.println("  "+relation.toShort+field.name);
            else
              outData.println("  "+(String)relation.toFieldNames.elementAt(i));
          }
          outData.println("  DELETE CASCADE");
          outData.println();
          outData.println("PROC Insert");
          outData.println("PROC DeleteOne");
          outData.println();
          if (relation.values.size() > 0)
          {
            outData.println("DATA");
            for (int i=0; i<relation.values.size(); i++)
            {
              Value value = (Value) relation.values.elementAt(i);
              outData.print("  INSERT INTO "+relation.name+" VALUES(");
              for (int j=0; j<value.list.size(); j++)
              {
                String s = (String) value.list.elementAt(j);
                if (j > 0)
                  outData.print(", ");
                if (isNumeric(s))
                  outData.print(s);
                else
                  outData.print("'"+s+"'");
              }
              outData.println(", 'DEFINED', SYSDATE);");
            }
            outData.println("ENDDATA");
            outData.println();
          }
        }
        finally
        {
          outData.flush();
          outData.close();
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
  static String outputField(Field field)
  {
    return outputField(field, false);
  }
  static String outputField(Field field, boolean asSequence)
  {
    String x = "";
    if (field.isNull)
      x += " NULL";
    if (field.isUppercase)
      x += " UPPER";
    if (field.check.length() > 0)
      x += " CHECK \""+field.check+"\"";
    switch (field.type)
    {
    case Field.SEQUENCE:
      if (asSequence)
        return "Sequence"+x;
      return "Int"+x;
    case Field.BIGSEQUENCE:
      if (asSequence)
        return "BigSequence"+x;
      return "Long"+x;
    case Field.BOOLEAN:
    case Field.BYTE:
    return "TinyInt"+x;
    case Field.SHORT:
    return "SmallInt"+x;
    case Field.INT:
    case Field.LONG:
      return "Int"+x;
    case Field.CHAR:
      return "Char("+field.length+")"+x;
    case Field.DATE:
      return "Date"+x;
    case Field.DATETIME:
      return "DateTime"+x;
    case Field.DOUBLE:
      return "Double"+x;
    case Field.TIME:
      return "Time"+x;
    case Field.TIMESTAMP:
      return "TimeStamp"+x;
    case Field.USERSTAMP:
      return "UserStamp"+x;
    }
    return "CRAP";
  }
  static String selectField(Field field)
  {
    switch (field.type)
    {
    case Field.DATE:
      if (!genJPortalSI)
        return "to_char("+field.name+", 'YYYYMMDD')";
      return field.name;
    case Field.DATETIME:
    case Field.TIMESTAMP:
      if (!genJPortalSI)
        return "to_char("+field.name+", 'YYYYMMDDHH24MISS')";
      return field.name;
    case Field.TIME:
      if (!genJPortalSI)
        return "to_char("+field.name+", 'HH24MISS')";
      return field.name;
    case Field.BOOLEAN:
    case Field.BYTE:
    case Field.INT:
    case Field.LONG:
    case Field.SEQUENCE:
    case Field.BIGSEQUENCE:
    case Field.SHORT:
    case Field.CHAR:
    case Field.USERSTAMP:
    case Field.DOUBLE:
      return field.name;
    }
    return "CRAP";
  }
}


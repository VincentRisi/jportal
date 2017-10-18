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

public class GenDomainSi
{
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate Domain Access and Update SI file");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(
            args[i]));
        Application application = (Application) in.readObject();
        in.close();
        generate(application, "", outLog);
      }
      outLog.flush();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  static String padder(String s, int length)
  {
    for (int i = s.length(); i < length - 1; i++)
      s = s + " ";
    return s + " ";
  }
  static boolean genJPortalSI = false;
  static String dynVarMarker = "$";
  static String sqlCode = "SQL CODE";
  static void setFlags(Application application, PrintWriter outLog)
  {
    for (int i = 0; i < application.flags.size(); i++)
    {
      String flag = (String) application.flags.elementAt(i);
      if (flag.equalsIgnoreCase("jportal"))
        genJPortalSI = true;
    }
    if (genJPortalSI)
      outLog.println(" (Generating for JPortal)");
  }
  public static void generate(Application application, String output,
      PrintWriter outLog)
  {
    setFlags(application, outLog);
    if (genJPortalSI)
    {
      dynVarMarker = "&";
      sqlCode = "SQLCODE";
    }
    try
    {
      outLog.println("Code: " + output + "Domain.si");
      OutputStream outFile = new FileOutputStream(output + "Domain.si");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        try
        {
          outData
              .println("// This code was generated, do not modify it, modify it at source and regenerate it.");
          outData.println();
          if (genJPortalSI)
          {
            outData.println("DATABASE " + application.name);
            outData.println("SERVER   " + application.server);
            outData.println("USERID   " + application.user);
            outData.println("PASSWORD " + application.password);
          } else
          {
            outData.println("SERVER   @" + application.server);
            outData.println("CONNECT  " + application.user + "/"
                + application.password);
          }
          outData.println();
          outData.println("TABLE Domain");
          outData.println();
          for (int i = 0; i < application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            boolean skipDomain = false;
            for (int j = 0; j < table.comments.size(); j++)
            {
              String comment = (String) table.comments.elementAt(j);
              outLog.println(table.name + " (" + comment.substring(0, 9) + ")");
              if (comment.substring(0, 9).toUpperCase().compareTo("$NODOMAIN") == 0)
              {
                skipDomain = true;
                break;
              }
            }
            if (skipDomain == true)
              continue;
            generateDomain(table, outData);
          }
          outData.println();
        } finally
        {
          outData.flush();
        }
      } finally
      {
        outFile.close();
      }
    } catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  static void generateDomain(Table table, PrintWriter outData)
  {
    outData.println("PROC " + table.useName());
    if (genJPortalSI)
      outData.println("OUTPUT");
    else
      outData.println("OUTPUT (Multiple)");
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field) table.fields.elementAt(i);
      outData.println("  " + field.useName() + " " + outputField(field));
    }
    outData.println(sqlCode);
    String comma = "  SELECT ";
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field) table.fields.elementAt(i);
      outData.println(comma + selectField(field));
      comma = "       , ";
    }
    outData.println("  FROM " + table.name);
    comma = "  ORDER BY ";
    for (int i = 0; i < table.order.size(); i++)
    {
      Field field = (Field) table.order.elementAt(i);
      outData.println(comma + selectField(field));
      comma = "         , ";
    }
    outData.println("ENDCODE");
    outData.println();
  }
  static boolean isNumeric(String s)
  {
    for (int i = 0; i < s.length(); i++)
    {
      char ch = s.charAt(i);
      if (ch < '0' || ch > '9')
        if (ch != 'e' && ch != '.' && ch != 'E' && ch != '-' && ch != '+')
          return false;
    }
    return true;
  }
  static String alias(Table table)
  {
    if (table.alias.length() > 0)
      return table.name + " (" + table.alias + ")";
    return table.name;
  }
  static String alias(Field field)
  {
    if (field.alias.length() > 0)
      return field.name + " (" + field.alias + ")";
    return field.name;
  }
  static String outputField(Field field)
  {
    return outputField(field, false);
  }
  static String outputField(Field field, boolean asSequence)
  {
    String x = "";
    if (field.isNull)
      x = " NULL";
    switch (field.type)
    {
    case Field.SEQUENCE:
      if (asSequence)
        return "Sequence" + x;
      return "Int" + x;
    case Field.BOOLEAN:
    case Field.BYTE:
      return "TinyInt" + x;
    case Field.SHORT:
      return "SmallInt" + x;
    case Field.INT:
    case Field.LONG:
      return "Int" + x;
    case Field.CHAR:
      return "Char(" + field.length + ")" + x;
    case Field.DATE:
      return "Date" + x;
    case Field.DATETIME:
      return "DateTime" + x;
    case Field.DOUBLE:
      return "Double" + x;
    case Field.TIME:
      return "Time" + x;
    case Field.TIMESTAMP:
      return "TimeStamp" + x;
    case Field.USERSTAMP:
      return "UserStamp" + x;
    }
    return "CRAP";
  }
  static String selectField(Field field)
  {
    switch (field.type)
    {
    case Field.DATE:
      if (!genJPortalSI)
        return "to_char(" + field.name + ", 'YYYYMMDD')";
      return field.name;
    case Field.DATETIME:
    case Field.TIMESTAMP:
      if (!genJPortalSI)
        return "to_char(" + field.name + ", 'YYYYMMDDHH24MISS')";
      return field.name;
    case Field.TIME:
      if (!genJPortalSI)
        return "to_char(" + field.name + ", 'HH24MISS')";
      return field.name;
    case Field.BOOLEAN:
    case Field.BYTE:
    case Field.INT:
    case Field.LONG:
    case Field.SEQUENCE:
    case Field.SHORT:
    case Field.CHAR:
    case Field.USERSTAMP:
    case Field.DOUBLE:
      return field.name;
    }
    return "CRAP";
  }
}

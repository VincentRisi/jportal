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

public class MSSqlDDL extends Generator
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
        outLog.println(args[i]+": Generate MSSql DDL");
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
    return "Generate MSSql DDL";
  }
  public static String documentation()
  {
    return "Generate MSSql DDL";
  }
  protected static Vector<Flag> flagsVector;
  static boolean addTimestamp;
  static boolean useInsertTrigger;
  static boolean useUpdateTrigger;
  static boolean internalStamps;
  static boolean generate42;
  static boolean auditTrigger;
  private static void flagDefaults()
  {
    addTimestamp = false;
    useInsertTrigger = false;
    useUpdateTrigger = false;
    internalStamps = false;
    auditTrigger = false;
    generate42 = false;
  }
  public static Vector<Flag> flags()
  {
    if (flagsVector == null)
    {
      flagDefaults();
      flagsVector = new Vector<Flag>();
      flagsVector.addElement(new Flag("add timestamp", new Boolean (addTimestamp), "Add Timestamp - legacy flags"));
      flagsVector.addElement(new Flag("use insert trigger", new Boolean (useInsertTrigger), "Use Insert Trigger - legacy flags"));
      flagsVector.addElement(new Flag("use update trigger", new Boolean (useUpdateTrigger), "Use Update Trigger - legacy flags"));
      flagsVector.addElement(new Flag("internal stamps", new Boolean (internalStamps), "Use Internal Stamps - legacy flags"));
      flagsVector.addElement(new Flag("generate 4.2", new Boolean (generate42), "Generate for SqlServer 4.2 - legacy flags"));
      flagsVector.addElement(new Flag("auditTrigger", new Boolean (auditTrigger), "Generate Auditing Table and Triggers"));
    }
    return flagsVector;
  }
  /**
  * Sets generation flags.
  */
  static void setFlags(Database database, PrintWriter outLog)
  {
    if (flagsVector != null)
    {
      addTimestamp = toBoolean(((Flag)flagsVector.elementAt(0)).value);
      useInsertTrigger = toBoolean (((Flag)flagsVector.elementAt(1)).value);
      useUpdateTrigger = toBoolean (((Flag)flagsVector.elementAt(2)).value);
      internalStamps = toBoolean (((Flag)flagsVector.elementAt(3)).value);
      generate42 = toBoolean (((Flag)flagsVector.elementAt(4)).value);
      auditTrigger = toBoolean (((Flag)flagsVector.elementAt(5)).value);
    }
    else
      flagDefaults();
    for (int i=0; i < database.flags.size(); i++)
    {
      String flag = (String) database.flags.elementAt(i);
      if (flag.equalsIgnoreCase("add timestamp"))
        addTimestamp = true;
      else if (flag.equalsIgnoreCase("use triggers"))
      {
        useInsertTrigger = true;
        useUpdateTrigger = true;
      }
      else if (flag.equalsIgnoreCase("use insert trigger"))
        useInsertTrigger = true;
      else if (flag.equalsIgnoreCase("use update trigger"))
        useUpdateTrigger = true;
      else if (flag.equalsIgnoreCase("internal stamps"))
        internalStamps = true;
      else if (flag.equalsIgnoreCase("generate 4.2"))
        generate42 = true;
      else if (flag.equalsIgnoreCase("audit triggers"))
        auditTrigger = true;
    }
    if (addTimestamp)
      outLog.println(" (add timestamp)");
    if (useInsertTrigger)
      outLog.println(" (use insert trigger)");
    if (useUpdateTrigger)
      outLog.println(" (use update trigger)");
    if (internalStamps)
      outLog.println(" (internal stamps)");
    if (generate42)
      outLog.println(" (generate 4.2)");
    if (auditTrigger)
      outLog.println(" (audit triggers)");
  }
  private static String tableOwner;
  /**
  * Generates the SQL for SQLServer Table creation.
  */
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    try
    {
      setFlags(database, outLog);
      String fileName;
      if (database.output.length() > 0)
        fileName = database.output;
      else
        fileName = database.name;
      if (database.schema.length() > 0)
        tableOwner = database.schema + ".";
      else
        tableOwner = "";
      outLog.println("DDL: " + output + fileName + ".sql");
      OutputStream outFile = new FileOutputStream(output + fileName + ".sql");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        if (database.schema.length() > 0)
          outData.println("use " + database.schema);
        else
          outData.println("use " + database.name);
        outData.println();
        for (int i=0; i<database.tables.size(); i++)
          generateTable((Table) database.tables.elementAt(i), outData);
        for (int i=0; i<database.views.size(); i++)
          generateView((View) database.views.elementAt(i), outData, "");
        outData.flush();
      }
      finally
      {
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate SQLServer SQL IO Error");
    }
  }
  static void generateAuditTable(Table table, PrintWriter outData)
  {
    String tableName = tableOwner + table.name;
    outData.println("drop table " + tableName + "Audit");
    outData.println("go");
    outData.println();
    outData.println("create table "+tableName+"Audit");
    outData.println("(");
    outData.println("  auditId integer IDENTITY(1,1) NOT NULL PRIMARY KEY");
    outData.println(", auditAction integer NOT NULL -- 1 = insert, 2 = delete, 3 = update");
    outData.println(", auditWhen datetime NOT NULL");
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field) table.fields.elementAt(i);
      outData.println(", "+varType(field, true, false)+" NULL");
    }
    outData.println(")");
    outData.println("go");
    outData.println();
  }
  static void generateAuditTrigger(Table table, PrintWriter outData)
  {
    String tableName = tableOwner + table.name;
    outData.println("drop trigger " + tableName + "AuditTrigger");
    outData.println("go");
    outData.println();
    outData.println("create trigger " + tableName + "AuditTrigger on " + tableName); 
    outData.println("for insert, delete, update as");
    outData.println("begin");
    outData.println("  declare @insert int, @delete int, @action int;");
    outData.println("  select @insert = count(*) from inserted;");
    outData.println("  select @delete = count(*) from deleted;");
    outData.println("  if @insert > 0 select @action = 1 else select @action = 0;");
    outData.println("  if @delete > 0 select @action = @action + 2;");
    outData.println("  -- 1 = insert, 2 = delete, 3 = update");
    outData.println("  if @action = 2 begin");
    outData.println("    insert into " + tableName + "Audit");
    outData.println("    select @action");
    outData.println("         , current_timestamp");
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field)table.fields.elementAt(i);
      outData.println("          , " + field.name);
    }
    outData.println("    from deleted;");
    outData.println("  end else");
    outData.println("  begin");
    outData.println("    insert into " + tableName + "Audit");
    outData.println("    select @action");
    outData.println("         , current_timestamp");
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field)table.fields.elementAt(i);
      outData.println("         , " + field.name);
    }
    outData.println("    from inserted;");
    outData.println("  end");
    outData.println("end");
    outData.println("go");
    outData.println();
  }
  static void generateTable(Table table, PrintWriter outData)
  {
    String tableName = tableOwner + table.name;
    String comma = "  ";
    outData.println("drop table "+tableName);
    outData.println("go");
    outData.println();
    outData.println("create table "+tableName);
    outData.println("(");
    for (int i = 0; i < table.fields.size(); i++, comma = ", ")
    {
      Field field = (Field) table.fields.elementAt(i);
      outData.print(comma+varType(field, false, table.hasSequenceReturning));
      if (!field.isNull)
        outData.println(" not null");
      else
        outData.println(" null");
    }
    if (internalStamps)
    {
      outData.println(comma+"UpdateWhen  DateTime Default CURRENT_TIMESTAMP NULL");
      outData.println(comma+"UpdateByWho Char(8)  Default USER NULL ");
    }
    if (addTimestamp)
      outData.println(comma+"timestamp");
    if (!generate42)
    {
      for (int i=0; i < table.keys.size(); i++)
      {
        Key key = (Key) table.keys.elementAt(i);
        if (key.isPrimary)
          generatePrimary(key, outData);
        else if (key.isUnique)
          generateUnique(key, outData);
      }
      for (int i=0; i < table.links.size(); i++)
      {
        Link link = (Link) table.links.elementAt(i);
        generateLink(link, outData);
      }
//      if (hasEnums)
//        generateEnumLinks(table, outData);
    }
    outData.println(")");
    outData.println("go");
    outData.println();
    for (int i=0; i < table.keys.size(); i++)
    {
      Key key = (Key) table.keys.elementAt(i);
      if (generate42 || (!key.isPrimary && !key.isUnique))
        generateKey(key, outData, tableName);
    }
    if (generate42)
    {
      for (int i=0; i < table.links.size(); i++)
      {
        Link link = (Link) table.links.elementAt(i);
        generateSpLink(link, outData, tableName);
      }
    }
    for (int i=0; i < table.grants.size(); i++)
    {
      Grant grant = (Grant) table.grants.elementAt(i);
      generateGrant(grant, outData, table.database.userid + "." + tableName);
    }
    if (useInsertTrigger)
    {
      if (table.hasSequence || table.hasUserStamp || table.hasTimeStamp)
      {
        outData.println("drop trigger "+tableName+"InsertTrigger");
        outData.println("go");
        outData.println();
        outData.println("create trigger "+tableName+"InsertTrigger on "+tableName+" for insert as");
        for (int i=0; i < table.fields.size(); i++)
        {
          Field field = (Field) table.fields.elementAt(i);
          if (field.type == Field.SEQUENCE)
          {
            outData.println("update "+tableName+" set "+field.name+"="+field.name+"+0");
            outData.println("where "+field.name+"=(select max("+field.name+") from "+tableName+")");
          }
        }
        outData.println("update "+tableName);
        outData.println("set");
        comma = "  ";
        for (int i=0; i < table.fields.size(); i++)
        {
          Field field = (Field) table.fields.elementAt(i);
          if (field.type == Field.SEQUENCE)
          {
            outData.println(comma+field.name+" = (select max("+field.name+") from "+tableName+")+1");
            comma = ", ";
          }
          else if (field.type == Field.USERSTAMP)
          {
            outData.println(comma+field.name+" = user_name()");
            comma = ", ";
          }
          else if (field.type == Field.TIMESTAMP)
          {
            outData.println(comma+field.name+" = getdate()");
            comma = ", ";
          }
        }
        String cond = "where ";
        for (int i=0; i < table.fields.size(); i++)
        {
          Field field = (Field) table.fields.elementAt(i);
          if (field.isPrimaryKey)
          {
            outData.println(cond+field.name+" = (select "+field.name+" from inserted)");
            cond = "  and ";
          }
        }
        outData.println("go");
        outData.println();
      }
    }
    if (useUpdateTrigger)
    {
      if (table.hasUserStamp || table.hasTimeStamp)
      {
        outData.println("drop trigger "+tableName+"UpdateTrigger");
        outData.println("go");
        outData.println();
        outData.println("create trigger "+tableName+"UpdateTrigger on "+tableName+" for update as");
        outData.println("update "+tableName);
        outData.println("set");
        comma = "  ";
        for (int i=0; i < table.fields.size(); i++)
        {
          Field field = (Field) table.fields.elementAt(i);
          if (field.type == Field.USERSTAMP)
          {
            outData.println(comma+field.name+" = user_name()");
            comma = ", ";
          }
          else if (field.type == Field.TIMESTAMP)
          {
            outData.println(comma+field.name+" = getdate()");
            comma = ", ";
          }
        }
        String cond = "where ";
        for (int i=0; i < table.fields.size(); i++)
        {
          Field field = (Field) table.fields.elementAt(i);
          if (field.isPrimaryKey)
          {
            outData.println(cond+field.name+" = (select "+field.name+" from inserted)");
            cond = "  and ";
          }
        }
        outData.println("go");
        outData.println();
      }
    }
    if (auditTrigger)
    {
      generateAuditTable(table, outData);
      generateAuditTrigger(table, outData);
    }
    for (int i=0; i < table.views.size(); i++)
    {
      View view = (View) table.views.elementAt(i);
      generateView(view, outData, tableName);
    }
    for (int i=0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc) table.procs.elementAt(i);
      if (proc.isData)
        generateProc(proc, outData);
    }
  }
  /**
  * Generates SQL code for SQL Server Index
  */
  static void generateKey(Key key, PrintWriter outData, String table)
  {
    String comma = "  ";
    if (key.isPrimary)
      outData.println("create unique clustered index "+key.name+" on "+table);
    else if (key.isUnique)
      outData.println("create unique index "+key.name+" on "+table);
    else
      outData.println("create index "+key.name+" on "+table);
    outData.println("(");
    for (int i=0; i < key.fields.size(); i++, comma = ", ")
    {
      String name = (String) key.fields.elementAt(i);
      outData.println(comma+name);
    }
    outData.println(")");
    outData.println("go");
    outData.println();
    if (key.isPrimary)
    {
      outData.println("sp_primarykey "+table);
      for (int i=0; i<key.fields.size(); i++)
      {
        String name = (String) key.fields.elementAt(i);
        outData.println(", "+name);
      }
      outData.println("go");
      outData.println();
    }
  }
  /**
  * Generates SQL code for ORACLE Primary Key create
  */
  static void generatePrimary(Key key, PrintWriter outData)
  {
    String comma = "    ";
    outData.println(", primary key (");
    for (int i=0; i < key.fields.size(); i++, comma = "  , ")
    {
      String name = (String) key.fields.elementAt(i);
      outData.println(comma+name);
    }
    outData.println("  )");
  }
  /**
  * Generates SQL code for ORACLE Unique Key create
  */
  static void generateUnique(Key key, PrintWriter outData)
  {
    String comma = "    ";
    outData.println(", unique (");
    for (int i=0; i<key.fields.size(); i++, comma = "  , ")
    {
      String name = (String) key.fields.elementAt(i);
      outData.println(comma+name);
    }
    outData.println("  )");
  }
  /**
  * Generates foreign key SQL Code appended to table
  */
  static void generateLink(Link link, PrintWriter outData)
  {
    String comma = "    ";
    outData.println(", foreign key (");
    for (int i=0; i < link.fields.size(); i++, comma = "   ,")
    {
      String name = (String) link.fields.elementAt(i);
      outData.println(comma+name);
    }
    outData.println("  )");
    outData.print("  references "+link.name);
    if (link.linkFields.size() > 0)
    {
      comma = "";
      outData.print("(");
      for (int i=0; i<link.linkFields.size(); i++)
      {
        String name = (String) link.linkFields.elementAt(i);
        outData.print(comma+name);
        comma = ", ";
      }
      outData.print(")");
    }
    outData.println();
  }
  /**
  * Generates foreign key SQL Code for SQL Server
  */
  static void generateSpLink(Link link, PrintWriter outData, String table)
  {
    outData.println("sp_foreignkey "+table+", "+link.name);
    for (int i=0; i<link.fields.size(); i++)
    {
      String name = (String) link.fields.elementAt(i);
      outData.println(", "+name);
    }
    outData.println("go");
    outData.println();
  }
  /**
  * Generates grant SQL Code for SQL Server
  */
  static void generateGrant(Grant grant, PrintWriter outData, String object)
  {
    for (int i=0; i < grant.perms.size(); i++)
    {
      String perm = (String) grant.perms.elementAt(i);
      for (int j=0; j < grant.users.size(); j++)
      {
        String user = (String) grant.users.elementAt(j);
        outData.println("grant " + perm + " on " + object + " to " + user);
        outData.println("go");
        outData.println();
      }
    }
  }
  /**
  * Generates view SQL Code for SQL Server
  */
  static void generateView(View view, PrintWriter outData, String tableName)
  {
    outData.println("drop view "+tableName+view.name);
    outData.println("go");
    outData.println();
    outData.println("create view "+tableName+view.name);
    outData.println("(");
    String comma = "  ";
    for (int i=0; i<view.aliases.size(); i++)
    {
      String alias = (String) view.aliases.elementAt(i);
      outData.println(comma+alias);
      comma = ", ";
    }
    outData.println(") as");
    outData.println("(");
    for (int i=0; i<view.lines.size(); i++)
    {
      String line = (String) view.lines.elementAt(i);
      outData.println(line);
    }
    outData.println(")");
    outData.println("go");
    outData.println();
    for (int i=0; i<view.users.size(); i++)
    {
      String user = (String) view.users.elementAt(i);
      outData.println("grant select on "+tableName+view.name+" to "+user);
    }
    if (view.users.size() > 0)
    {
      outData.println("go");
      outData.println();
    }
  }
  static void generateProc(Proc proc, PrintWriter outData)
  {
    for (int i=0; i < proc.lines.size(); i++)
    {
      Line l = proc.lines.elementAt(i);
      outData.println(l.line);
    }
    outData.println();
  }
  /**
  * Translates field type to SQLServer SQL column types
  */
  static String varType(Field field, boolean typeOnly, boolean hasSequenceReturning)
  {
    switch(field.type)
    {
    case Field.BOOLEAN:
      return field.name+" bit";
    case Field.BYTE:
      return field.name+" tinyint";
    case Field.SHORT:
      return field.name+" smallint";
    case Field.INT:
      return field.name + " int";
    case Field.LONG:
      return field.name+" bigint";
    case Field.SEQUENCE:
      if (hasSequenceReturning)
        return field.name+" integer IDENTITY(1,1)";
      return field.name+" integer";
    case Field.BIGSEQUENCE:
      if (hasSequenceReturning)
        return field.name+" bigint IDENTITY(1,1)";
      return field.name+" bigint";            
    case Field.IDENTITY:
      if (typeOnly == true)
        return field.name + " integer";
      else
        return field.name+" integer IDENTITY(1,1)";
    case Field.BIGIDENTITY:
      if (typeOnly == true)
        return field.name + " bigint";
      else
        return field.name + " bigint IDENTITY(1,1)";
    case Field.UID:
      return field.name + " uniqueidentifier";
    case Field.CHAR:
      if (field.length > 8000) 
        return field.name + " varchar(MAX)";
      return field.name+" varchar("+String.valueOf(field.length)+")";
    case Field.ANSICHAR:
      return field.name+" char("+String.valueOf(field.length)+")";
    case Field.DATE:
      return field.name+" datetime";
    case Field.DATETIME:
      return field.name+" datetime";
    case Field.TIME:
      return field.name+" datetime";
    case Field.TIMESTAMP:
      return field.name+" datetime";
    case Field.FLOAT:
    case Field.DOUBLE:
      if (field.precision > 15)
        return field.name + " decimal("+field.precision+","+field.scale+")";
      return field.name + " float";
    case Field.BLOB:
      return field.name+" image";
    case Field.TLOB:
      return field.name+" text";
    case Field.XML:
      return field.name + " xml";
    case Field.MONEY:
      return field.name+" money";
    case Field.USERSTAMP:
      return field.name+" char(8)";
    }
    return "unknown";
  }
}
 

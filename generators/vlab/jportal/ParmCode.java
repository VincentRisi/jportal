package vlab.jportal;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class ParmCode extends Generator
{

	public static void main(String[] args) 
	{
	  try
	  {
	    PrintWriter outLog = new PrintWriter(System.out);
	    for (int i = 0; i < args.length; i++)
	    {
	      outLog.println(args[i] + ": generate Parm application code");
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
    return "generate Parm application code";
  }
  public static String documentation()
  {
    return "generate Parm application code";
  }
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    output = database.packageMerge(output);
    for (int i = 0; i < database.tables.size(); i++)
    {
      Table table = database.tables.get(i);
      generateTable(table, output, outLog);
    }
  }
  private static void generateTable(Table table, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: " + output + table.useName() + ".pi");
      OutputStream outFile = new FileOutputStream(output + table.useName() + ".pi");
      try
      {
        PrintWriter outData = new PrintWriter(outFile);
        generateTable(table, outData, outLog);
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
  private static class ParmOptions
  {
    public boolean viewOnly;
    public String descr;
    public boolean domain;
    public boolean nullEnabled;
    public ParmOptions()
    {
      viewOnly = false;
      descr = "";
      domain = true;
      nullEnabled=false;
    }
  }
  private static void generateTable(Table table, PrintWriter outData, PrintWriter outLog)
  {
    ParmOptions opts = loadOptions(table);
    if (table.links.size() > 0)
    {
      if (genUses(table, opts, outData, outLog) == true)
      {
        genRelation(table, opts, outData, outLog);
        opts.viewOnly = true;
      }
    }
    outData.print(String.format("TABLE %s", checkReserved(table.name)));
    if (opts.descr.length() > 0)
      outData.print(String.format(" '%s'", opts.descr));
    if (opts.nullEnabled == true)
      outData.print(" NULL");
    if (opts.domain == false)
      outData.print(" NODOMAIN");
    if (opts.viewOnly == true)
      outData.print(" VIEWONLY");
    outData.println();
    if (table.check.length() > 0)
      outData.println(String.format("  CHECK \"%s\"", table.check));
    for (Field field : table.fields)
    {
      if (field.name.equalsIgnoreCase("USid") == true
      ||  field.name.equalsIgnoreCase("TmStamp") == true)
        continue;
      String checker="";
      if (field.checkValue.length() > 0)
        checker = String.format(" CHECK \"%s\"", field.checkValue);
      outData.println(String.format("%s%-28s %s%s%s"
          , commentOf(field)
          , nameOf(field)
          , typeOf(field)
          , field.isNull ? " NULL" : ""
          , checker
          ));
    }
  }
  private static String typeOf(Field field)
  {
    switch (field.type)
    {
    case Field.ANSICHAR:
      return String.format("ansichar(%s)", field.length);
    case Field.AUTOTIMESTAMP:
      return "timestamp";
    case Field.BIGIDENTITY:
      return "bigidentity";
    case Field.BIGSEQUENCE:
      return "bigsequence";
    case Field.BIGXML:
      return String.format("bigxml(%s)", field.length);
    case Field.BLOB:
      return String.format("blob(%s)", field.length);
    case Field.BOOLEAN:
      return "boolean";
    case Field.BYTE:
      return "byte";
    case Field.CHAR:
      return String.format("char(%s)", field.length);
    case Field.DATE:
      return "date";
    case Field.DATETIME:
      return "datetime";
    case Field.DOUBLE:
      return String.format("double(%d, %d)", field.precision, field.scale);
    case Field.DYNAMIC:
      return "dynamic";
    case Field.FLOAT:
      return "double";
    case Field.IDENTITY:
      return "identity";
    case Field.INT:
      return "int";
    case Field.LONG:
      return "long";
    case Field.MONEY:
      return "money";
    case Field.SEQUENCE:
      return "sequence";
    case Field.SHORT:
      return "short";
    case Field.STATUS:
      return "status";
    case Field.TIME:
      return "time";
    case Field.TIMESTAMP:
      return "timestamp";
    case Field.TLOB:
      return String.format("tlob(%s)", field.length);
    case Field.UID:
      return "uid";
    case Field.USERSTAMP:
      return "userstamp";
    case Field.UTF8:
      return String.format("utf8(%s)", field.length);
    case Field.WANSICHAR:
      return String.format("wansichar(%s)", field.length);
    case Field.WCHAR:
      return String.format("wchar(%s)", field.length);
    case Field.XML:
      return String.format("xml(%s)", field.length);
    }
    return "?typeOf?";
  }
  private static String nameOf(Field field)
  {
    String result = field.name;
    if (field.alias.length() > 0)
      result = String.format("%s (%s)", field.name, field.alias);
    return result;
  }
  private static String commentOf(Field field)
  {
    return "  ";
  }
  static final String reservedWords=":"
      +"all:"
      +"ansichar:"
      +"application:"
      +"boolean:"
      +"byte:"
      +"cascade:"
      +"char:"
      +"check:"
      +"date:"
      +"datetime:"
      +"delete:"
      +"float:"
      +"double:"
      +"money:"
      +"flags:"
      +"int:"
      +"integer:"
      +"identity:"
      +"key:"
      +"link:"
      +"long:"
      +"lookup:"
      +"money:"
      +"nodomain:"
      +"not:"
      +"null:"
      +"options:"
      +"order:"
      +"output:"
      +"password:"
      +"primary:"
      +"registry:"
      +"relation:"
      +"sequence:"
      +"server:"
      +"short:"
      +"show:"
      +"smallint:"
      +"sizes:"
      +"table:"
      +"time:"
      +"timestamp:"
      +"tinyint:"
      +"unique:"
      +"upper:"
      +"uppercase:"
      +"use:"
      +"user:"
      +"userstamp:"
      +"value:"
      +"viewonly:";
  private static String checkReserved(String name)
  {
    String work=String.format(":%s:", name.toLowerCase());
    if (reservedWords.indexOf(work) != -1)
      return String.format("L'%s'", name);
    return name;
  }
  private static void genRelation(Table table, ParmOptions opts, PrintWriter outData, PrintWriter outLog)
  {
    outData.print(String.format("RELATION %s", table.name));
    if (opts.descr.length() > 0)
      outData.print(String.format(" %s", opts.descr));
    outData.println();
    Link link0 = table.links.get(0);
    Link link1 = table.links.get(1);
    outData.print(String.format("  %s", link0.linkName));
    for (int i=0; i < link0.fields.size(); i++)
    {
      String field = link0.fields.get(i);
      outData.print(String.format("%s%s", i==0?"(":" ", field));
    }
    outData.println(")");
    outData.print(String.format("  %s", link1.linkName));
    for (int i=0; i < link1.fields.size(); i++)
    {
      String field = link1.fields.get(i);
      outData.print(String.format("%s%s", i==0?"(":" ", field));
    }
    outData.println(")");
    outData.println();
  }
  private static int countOf(Table table)
  {
    int result = 0;
    for (int i=0; i<table.fields.size(); i++)
    {
      Field field = table.fields.get(i);
      if (field.name.equalsIgnoreCase("USId") == true)
        continue;
      if (field.type == Field.TIMESTAMP)
        continue;
      result++;
    }
    return result;
  }
  private static boolean genUses(Table table, ParmOptions opts, PrintWriter outData, PrintWriter outLog)
  {
    outData.print("//USES:");
    int n = table.links.size();
    for (int i=0; i<n; i++)
    {
      Link link = table.links.get(i);
      outData.print(link.name+":");
    }
    outData.println();
    outData.println();
    if (n != 2)
      return false;
    Link link0 = table.links.get(0);
    Link link1 = table.links.get(1);
    if (link0.name.equalsIgnoreCase(link1.name) == true)
      return false;
    n = link0.linkFields.size()+link1.linkFields.size();
    if (n != countOf(table))
      return false;
    for (int i=0; i<link0.fields.size(); i++)
    {
      String field0 = link0.fields.get(i);
      for (int j=0; j<link1.fields.size(); j++)
      {
        String field1 = link1.fields.get(j);
        if (field0.equalsIgnoreCase(field1) == true)
          return false;
      }
    }
    return true;
  }
  private static ParmOptions loadOptions(Table table)
  {
    ParmOptions opts = new ParmOptions();
    for (int i=0; i < table.options.size(); i++)
    {
      String option = table.options.get(i);
      if (option.toLowerCase().indexOf("descr=") == 0)
        opts.descr = option.substring(6).trim();
      else if (option.equalsIgnoreCase("nodomain") == true)
        opts.domain = false;
      else if (option.equalsIgnoreCase("null") == true)
        opts.nullEnabled = true;
      
    }
    return opts;
  }
}

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class CSNet3Code extends Generator
{
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i < args.length; i++)
      {
        outLog.println(args[i] + ": Generate C# Code for ADO.NET via IDbConnection");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Database database = (Database) in.readObject();
        in.close();
        generate(database, "", outLog);
      }
      outLog.flush();
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  public static String description()
  {
    return "Generate C# Code for ADO.NET via IDbConnection";
  }
  public static String documentation()
  {
    return "Generate C# Code for ADO.NET via IDbConnection" + "\r\nDATABASE name FLAGS flag"
        + "\r\n- \"mssql storedprocs\" generate stored procedures for MSSql"
        + "\r\n- \"use generics\" generate lists as generics" + "\r\n- \"use partials\" generate classes as partials"
        + "\r\n- \"use yields\" generate code for yields"
        + "\r\n- \"no datatables\" do not generate code for datatables"
        + "\r\n- \"use C# 2.0\" generate classes with above uses"
        + "\r\n- \"use C# 1.0\" generate classes compatable with C# 1.0"
        + "\r\n- \"use separate\" generate classes in separate files"
        + "\r\n- \"use notify\" generate classes with INotifyPropertyChanged implemented";
  }
  protected static Vector<Flag> flagsVector;
  static boolean mSSqlStoredProcs;
  static boolean hasStoredProcs;
  static boolean useGenerics;
  static boolean usePartials;
  static boolean useSeparate;
  static boolean noDatatables;
  static boolean useYields;
  static boolean useCSharp2;
  static boolean useCSharp1;
  static boolean useNotify;
  static boolean useFunc;
  static String version = "2.1.";
  static String runTimeVersion = "2.0.50727.5485";
  private static void flagDefaults()
  {
    hasStoredProcs = false;
    mSSqlStoredProcs = false;
    useGenerics = false;
    usePartials = false;
    useYields = false;
    useSeparate = false;
    noDatatables = false;
    useCSharp1 = false;
    useCSharp2 = false;
    useNotify = false;
    useFunc = false;
  }
  public static Vector<Flag> flags()
  {
    if (flagsVector == null)
    {
      flagsVector = new Vector<Flag>();
      flagDefaults();
      flagsVector
          .addElement(new Flag("mssql storedprocs", new Boolean(mSSqlStoredProcs), "Generate MSSql Stored Procedures"));
      flagsVector.addElement(new Flag("use generics", new Boolean(useGenerics), "Generate C# 2.0 Generics"));
      flagsVector.addElement(new Flag("use partials", new Boolean(usePartials), "Generate C# 2.0 Partials"));
      flagsVector.addElement(new Flag("use yields", new Boolean(useYields), "Generate C# 2.0 Yields"));
      flagsVector.addElement(new Flag("use separate", new Boolean(useSeparate), "Generate Separate Files"));
      flagsVector.addElement(new Flag("no datatables", new Boolean(noDatatables), "Do not Generate Datatables"));
      flagsVector.addElement(new Flag("use C#2.0", new Boolean(useCSharp2), "Generate for C#2.0"));
      flagsVector.addElement(new Flag("use C#1.0", new Boolean(useCSharp1), "Generate for C#1.0"));
      flagsVector.addElement(new Flag("use notify", new Boolean(useNotify), "Generate for INotifyPropertyChanged"));
      flagsVector.addElement(new Flag("use func", new Boolean(useFunc), "Generate Functions"));
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
      mSSqlStoredProcs = toBoolean(((Flag) flagsVector.elementAt(0)).value);
      useGenerics = toBoolean(((Flag) flagsVector.elementAt(1)).value);
      usePartials = toBoolean(((Flag) flagsVector.elementAt(2)).value);
      useYields = toBoolean(((Flag) flagsVector.elementAt(3)).value);
      useSeparate = toBoolean(((Flag) flagsVector.elementAt(4)).value);
      noDatatables = toBoolean(((Flag) flagsVector.elementAt(5)).value);
      useCSharp2 = toBoolean(((Flag) flagsVector.elementAt(6)).value);
      useCSharp1 = toBoolean(((Flag) flagsVector.elementAt(7)).value);
      useNotify = toBoolean(((Flag) flagsVector.elementAt(8)).value);
      useFunc = toBoolean(((Flag) flagsVector.elementAt(9)).value);
    } else
      flagDefaults();
    for (int i = 0; i < database.flags.size(); i++)
    {
      String flag = (String) database.flags.elementAt(i);
      if (flag.equalsIgnoreCase("mssql storedprocs"))
        mSSqlStoredProcs = true;
      else if (flag.equalsIgnoreCase("use generics"))
        useGenerics = true;
      else if (flag.equalsIgnoreCase("use partials"))
        usePartials = true;
      else if (flag.equalsIgnoreCase("use yields"))
        useYields = true;
      else if (flag.equalsIgnoreCase("use separate"))
        useSeparate = true;
      else if (flag.equalsIgnoreCase("no datatables"))
        noDatatables = true;
      else if (flag.equalsIgnoreCase("use C#2.0"))
        useGenerics = usePartials = useYields = useCSharp2 = true;
      else if (flag.equalsIgnoreCase("use C#1.0"))
      {
        useGenerics = usePartials = useYields = useCSharp2 = false;
        useCSharp1 = true;
        runTimeVersion = "1.1";
      } else if (flag.equalsIgnoreCase("use notify"))
        useNotify = true;
      else if (flag.equalsIgnoreCase("use func"))
        useFunc = true;
    }
    if (mSSqlStoredProcs)
      outLog.println(" (mssql storedprocs)");
    if (useGenerics)
      outLog.println(" (use generics)");
    if (usePartials)
      outLog.println(" (use partials)");
    if (useYields)
      outLog.println(" (use yields)");
    if (useSeparate)
      outLog.println(" (use separate)");
    if (noDatatables)
      outLog.println(" (no datatables)");
    if (useNotify)
      outLog.println(" (use notify)");
    if (useFunc)
      outLog.println(" (use func)");
  }
  public static void generate(Database database, String output, PrintWriter outLog)
  {
    setFlags(database, outLog);

    for (int i = 0; i < database.tables.size(); i++)
    {
      Table table = (Table) database.tables.elementAt(i);
      generate(table, output, outLog);
    }
  }
  static OutputStream procFile;
  static PrintWriter procData;
  static void generate(Table table, String output, PrintWriter outLog)
  {
    OutputStream outFile;
    try
    {
      String added = "";
      if (useSeparate == true)
        added = "Structs";
      outFile = openOutputStream(table, output, outLog, added);
      hasStoredProcs = false;
      if (mSSqlStoredProcs == false && isStoredProcs(table))
      {
        hasStoredProcs = true;
      }

      if (mSSqlStoredProcs == true || hasStoredProcs == true)
      {
        outLog.println("DDL: " + output + table.useName() + ".sproc.sql");
        procFile = new FileOutputStream(output + table.name + ".sproc.sql");
        procData = new PrintWriter(procFile);
        procData.println("USE " + table.database.name);
        procData.println();
      }
      try
      {
        PrintWriter outData = openWriterPuttingTop(table, outFile);
        generateStructs(table, outData);
        if (!noDatatables)
        {
          if (useSeparate == true)
          {
            outData.println("}");
            outData.flush();
            outFile.close();
            outFile = openOutputStream(table, output, outLog, "Tables");
            outData = openWriterPuttingTop(table, outFile);
          }

          generateDataTables(table, outData);
        }
        if (useSeparate == true)
        {
          outData.println("}");
          outData.flush();
          outFile.close();
          outFile = openOutputStream(table, output, outLog, "");
          outData = openWriterPuttingTop(table, outFile);
        }
        generateCode(table, outData);
        outData.println("}");
        outData.flush();
        if (mSSqlStoredProcs == true || hasStoredProcs == true)
          procData.flush();
      } catch (Exception e)
      {
        outLog.println("Generate Procs IO Error" + e.toString());
      } finally
      {
        outFile.close();
        if (mSSqlStoredProcs == true || hasStoredProcs == true)
          procFile.close();
      }
    } catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  private static PrintWriter openWriterPuttingTop(Table table, OutputStream outFile)
  {
    PrintWriter outData = new PrintWriter(outFile);
    String packageName = table.database.packageName;
    if (packageName.length() == 0)
      packageName = "Bbd.JPortal";
    outData.println("//------------------------------------------------------------------------------");
    outData.println("// <auto-generated>");
    outData.println("//     This code was generated by a tool.");
    outData.println("//     Runtime Version: " + runTimeVersion);
    outData.println("//");
    outData.println("//     Changes to this file may cause incorrect behavior and will be lost if");
    outData.println("//     the code is regenerated.");
    outData.println("// </auto-generated>");
    outData.println("//------------------------------------------------------------------------------");
    outData.println("");
    outData.println("// ");
    outData.println("// This source code was auto-generated by jportal.jar, Version=" + version);
    outData.println("// ");
    outData.println("using System;");
    if (useGenerics)
      outData.println("using System.Collections.Generic;");
    else
    {
      outData.println("using System.Collections;");
      outData.println("using System.Collections.Specialized;");
    }
    if (useNotify)
    {
      outData.println("using System.ComponentModel;");
      outData.println("using System.Runtime.CompilerServices;");
    }
    outData.println("using System.Data;");
    outData.println("using Bbd.Idl2.AnyDb;");
    outData.println("");
    outData.println("namespace " + packageName);
    outData.println("{");
    return outData;
  }
  private static OutputStream openOutputStream(Table table, String output, PrintWriter outLog, String added)
      throws FileNotFoundException, IOException
  {
    OutputStream outFile;
    String outFileName = output + table.name + added + ".cs";
    outLog.println("Code: " + outFileName);
    outFile = new FileOutputStream(outFileName);
    return outFile;
  }
  private static void generateSelector(Field field, PrintWriter outData)
  {
    outData.println("    public class Nfpp" + field.useUpperName() + " // NO _______ PARAMETER PROPERTIES ");
    outData.println("    {");
    outData.println("      DataTable parent;");
    outData.println("      public " + fieldCastNo(field) + " this[int row]");
    outData.println("      {");
    outData.println("        get");
    outData.println("        {");
    if (field.isNull == true)
    {
      outData.println("          if (parent.Rows[row][c" + field.useUpperName() + "] == DBNull.Value)");
      if (useCSharp1)
      {
        outData.println("            return " + validNull(field) + ";");
      } else
      {
        outData.println("            return default(" + fieldCastNo(field) + ");");
      }
      outData.println("          else");
      outData
          .println("            return (" + fieldCastNo(field) + ")parent.Rows[row][c" + field.useUpperName() + "];");
    } else
      outData
          .println("            return (" + fieldCastNo(field) + ")parent.Rows[row][c" + field.useUpperName() + "];");
    outData.println("        }");
    outData.println("        set");
    outData.println("        {");
    outData.println("           parent.Rows[row][c" + field.useUpperName() + "] = value;");
    outData.println("        }");
    outData.println("      }");
    if (field.isNull == true)
    {
      outData.println(
          "      public bool IsNull(int row){ return parent.Rows[row].IsNull(c" + field.useUpperName() + "); }");
      outData.println(
          "      public void SetNull(int row){ parent.Rows[row][c" + field.useUpperName() + "] = DBNull.Value; }");
    }
    outData.println("      public Nfpp" + field.useUpperName() + "(DataTable parent) { this.parent = parent; }");
    outData.println("    }");
    outData.println("    private Nfpp" + field.useUpperName() + " m" + field.useUpperName() + ";");
    outData.println("    public Nfpp" + field.useUpperName() + " " + field.useUpperName() + "{ get { return m"
        + field.useUpperName() + "; }" + " set { m" + field.useUpperName() + " = value; } }");
  }

  public static boolean isStoredProcs(Table table)
  {
    boolean hasStoredProcs = false;
    for (int i = 0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc) table.procs.elementAt(i);
      if (proc.isSProc == true)
        return true;
      if (proc.outputs.size() == 0 || proc.isSingle)
        continue;
    }
    return hasStoredProcs;
  }

  public static void generateDataTables(Table table, PrintWriter outData)
  {
    boolean hasDataTables = false;
    for (int i = 0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc) table.procs.elementAt(i);
      if (proc.outputs.size() == 0 || proc.isSingle)
        continue;
      hasDataTables = true;
      outData.println("  [Serializable()]");
      outData.println("  public " + (usePartials ? "partial " : "") + "class " + table.useName() + proc.upperFirst()
          + "DataTable : DataTable");
      outData.println("  {");
      int noInDataSet = 0;
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        outData.println("    public const int c" + field.useUpperName() + " = " + noInDataSet + ";");
        noInDataSet++;
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        outData.println("    public const int c" + field.useUpperName() + " = " + noInDataSet + ";");
        noInDataSet++;
      }
      outData.println("    public static string ToString(int ordinal)");
      outData.println("    {");
      outData.println("      switch (ordinal)");
      outData.println("      {");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        outData.println("      case c" + field.useUpperName() + ": return \"" + field.useUpperName() + "\";");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        outData.println("      case c" + field.useUpperName() + ": return \"" + field.useUpperName() + "\";");
      }
      outData.println("      }");
      outData.println("      return \"<??\"+ordinal+\"??>\";");
      outData.println("    }");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        generateSelector(field, outData);
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        generateSelector(field, outData);
      }
      String mainName = table.useName();
      if (proc.isStd == false)
        mainName = mainName + proc.upperFirst();
      outData.println("    public class RowBag");
      outData.println("    {");
      outData.println("      public " + mainName + "Rec mRec;");
      outData.println("      public object tag = null;");
      outData.println("      public RowBag(" + mainName + "Rec aRec)");
      outData.println("      {");
      outData.println("        mRec = aRec;");
      outData.println("      }");
      outData.println("    }");
      if (useGenerics)
        outData.println("    public Dictionary<DataRow, RowBag> dictionary;");
      else
        outData.println("    public HybridDictionary dictionary;");
      if (useGenerics)
        outData.println(
            "    public " + table.useName() + proc.upperFirst() + "DataTable(List<" + mainName + "Rec> aList)");
      else
        outData.println("    public " + table.useName() + proc.upperFirst() + "DataTable(ArrayList aList)");
      outData.println("    : base(\"" + table.useName() + proc.upperFirst() + "\")");
      outData.println("    {");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        outData.println("      Columns.Add(new DataColumn(\"" + field.useUpperName() + "\", typeof("
            + dataTableType(field) + ")));");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        outData.println("      Columns.Add(new DataColumn(\"" + field.useUpperName() + "\", typeof("
            + dataTableType(field) + ")));");
      }
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        outData.println("      m" + field.useUpperName() + " = new Nfpp" + field.useUpperName() + "(this);");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        outData.println("      m" + field.useUpperName() + " = new Nfpp" + field.useUpperName() + "(this);");
      }
      if (useGenerics)
        outData.println("      dictionary = new Dictionary<DataRow, RowBag>();");
      else
        outData.println("      dictionary = new HybridDictionary();");
      outData.println("      foreach (" + mainName + "Rec wRec in aList)");
      outData.println("      {");
      outData.println("        DataRow wRow = NewRow();");
      outData.println("        dictionary.Add(wRow, new RowBag(wRec));");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        if (field.isNull == true)
        {
          outData.println("        if (wRec." + field.useUpperName() + "IsNull == true)");
          outData.println("          wRow[c" + field.useUpperName() + "] = DBNull.Value;");
          outData.println("        else");
          outData.println("          wRow[c" + field.useUpperName() + "] = wRec." + field.useUpperName() + ";");
        } else
          outData.println("        wRow[c" + field.useUpperName() + "] = wRec." + field.useUpperName() + ";");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        if (field.isNull == true)
        {
          outData.println("        if (wRec." + field.useUpperName() + "IsNull == true)");
          outData.println("          wRow[c" + field.useUpperName() + "] = DBNull.Value;");
          outData.println("        else");
          outData.println("          wRow[c" + field.useUpperName() + "] = wRec." + field.useUpperName() + ";");
        } else
          outData.println("        wRow[c" + field.useUpperName() + "] = wRec." + field.useUpperName() + ";");
      }
      outData.println("        Rows.Add(wRow);");
      outData.println("      }");
      outData.println("    }");
      outData.println("    public RowBag GetRowBag(int row)");
      outData.println("    {");
      outData.println("      DataRow wRow = Rows[row];");
      if (useGenerics)
        outData.println("      return dictionary[wRow];");
      else
        outData.println("      return (RowBag)dictionary[wRow];");
      outData.println("    }");
      outData.println("    public " + mainName + "Rec this[int row]");
      outData.println("    {");
      outData.println("      get");
      outData.println("      {");
      outData.println("        DataRow wRow = Rows[row];");
      outData.println("        " + mainName + "Rec wRec = new " + mainName + "Rec();");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        if (field.isNull == true)
        {
          outData.println("        if (wRow.IsNull(c" + field.useUpperName() + "))");
          outData.println("          wRec." + field.useUpperName() + "IsNull = true;");
          outData.println("        else");
          outData.println("          wRec." + field.useUpperName() + " = " + fieldCast(field) + "wRow[c"
              + field.useUpperName() + "];");
        } else
          outData.println("        wRec." + field.useUpperName() + " = " + fieldCast(field) + "wRow[c"
              + field.useUpperName() + "];");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        if (field.isNull == true)
        {
          outData.println("        if (wRow.IsNull(c" + field.useUpperName() + "))");
          outData.println("          wRec." + field.useUpperName() + "IsNull = true;");
          outData.println("        else");
          outData.println("          wRec." + field.useUpperName() + " = " + fieldCast(field) + "wRow[c"
              + field.useUpperName() + "];");
        } else
          outData.println("        wRec." + field.useUpperName() + " = " + fieldCast(field) + "wRow[c"
              + field.useUpperName() + "];");
      }
      outData.println("        return wRec;");
      outData.println("      }");
      outData.println("      set");
      outData.println("      {");
      outData.println("        DataRow wRow = Rows[row];");
      for (int j = 0; j < proc.inputs.size(); j++)
      {
        Field field = (Field) proc.inputs.elementAt(j);
        if (field.isNull == true)
        {
          outData.println("        if (value." + field.useUpperName() + "IsNull == true)");
          outData.println("          wRow[c" + field.useUpperName() + "] = DBNull.Value;");
          outData.println("        else");
          outData.println("          wRow[c" + field.useUpperName() + "] = value." + field.useUpperName() + ";");
        } else
          outData.println("        wRow[c" + field.useUpperName() + "] = value." + field.useUpperName() + ";");
      }
      for (int j = 0; j < proc.outputs.size(); j++)
      {
        Field field = (Field) proc.outputs.elementAt(j);
        if (proc.hasInput(field.name))
          continue;
        if (field.isNull == true)
        {
          outData.println("        if (value." + field.useUpperName() + "IsNull == true)");
          outData.println("          wRow[c" + field.useUpperName() + "] = DBNull.Value;");
          outData.println("        else");
          outData.println("          wRow[c" + field.useUpperName() + "] = value." + field.useUpperName() + ";");
        } else
          outData.println("        wRow[c" + field.useUpperName() + "] = value." + field.useUpperName() + ";");
      }
      outData.println("      }");
      outData.println("    }");
      outData.println("  }");
    }
    if (hasDataTables == true && usePartials == true && useSeparate == true)
    {
      String mainName = table.useName();
      // outData.println(" [Serializable()]");
      outData.println("  public partial class " + mainName);
      outData.println("  {");
      for (int i = 0; i < table.procs.size(); i++)
      {
        Proc proc = (Proc) table.procs.elementAt(i);
        if (proc.isData == true || proc.isStd == false)
          continue;
        if (!noDatatables)
        {
          if (proc.outputs.size() > 0 && !proc.isSingle)
            generateFetchProcDataTables(proc, mainName, outData);
        }
      }
      outData.println("  }");
      for (int i = 0; i < table.procs.size(); i++)
      {
        Proc proc = (Proc) table.procs.elementAt(i);
        if (proc.isData == true || proc.isStd == true)
          continue;
        if (proc.outputs.size() > 0 && !proc.isSingle)
        {
          mainName = table.useName() + proc.upperFirst();
          // outData.println(" [Serializable()]");
          outData.println("  public partial class " + mainName);
          outData.println("  {");
          if (!noDatatables)
          {
            generateFetchProcDataTables(proc, mainName, outData);
          }
          outData.println("  }");
        }
      }
    }
  }
  public static void generateStructPairs(Proc proc, Vector<Field> fields, Vector<?> dynamics, String mainName,
      PrintWriter outData, String tableName, boolean hasReturning)
  {
    outData.println("  [Serializable()]");
    String inherit = "";
    if (proc != null && proc.extendsStd == true)
    {
      inherit = " : " + tableName + "Rec" + (useNotify ? ", INotifyPropertyChanged" : "");
    } else
    {
      inherit = useNotify ? " : INotifyPropertyChanged" : "";
    }
    outData.println("  public " + (usePartials ? "partial " : "") + "class " + mainName + "Rec" + inherit);
    outData.println("  {");
    if (useNotify && fields.size() > 0)
    {
      outData.println("      #region INotifyPropertyChanged Members ");
      outData.println("");
      outData.println("      public event PropertyChangedEventHandler PropertyChanged;");
      outData.println("");
      outData.println("      public void NotifyPropertyChanged([CallerMemberName] string propertyName = null)");
      outData.println("      {");
      outData.println("          if (this.PropertyChanged != null)");
      outData.println("              this.PropertyChanged(this, new PropertyChangedEventArgs(propertyName));");
      outData.println("      }");
      outData.println("");
      outData.println("      #endregion");
      outData.println("");
    }
    for (int i = 0; i < fields.size(); i++)
    {
      Field field = (Field) fields.elementAt(i);
      String temp = "[Column(MaxLength=" + field.length;
      if (field.precision > 0)
      {
        temp = "[Column(MaxLength=" + field.precision + field.scale + 1;
      }

      if (field.isNull)
      {
        temp = temp + ", CanBeNull=true";
      }
      if (field.isPrimaryKey)
      {
        temp = temp + ", IsPrimaryKey=true";
      }
      if (field.isSequence)
      {
        temp = temp + ", isSequence=true";
      }
      if (field.precision > 0)
      {
        temp = temp + ", Precision=" + field.precision + ", Scale=" + field.scale;
      }

      outData.println("    " + fieldDef(field, temp));
      if (field.isNull)
      {
        if (getDataType(field, new StringBuffer(), new StringBuffer()) == "string")
        {
          outData.println("      private bool " + field.useLowerName() + "IsNull;");
          outData.println("      public bool " + field.useUpperName() + "IsNull");
          outData.println("      { ");
          outData.println("           get { return this." + field.useLowerName() + " == null ? true : "
              + field.useLowerName() + "IsNull; }");
          outData.println("           set { this." + field.useLowerName() + "IsNull = value; }");
          outData.println("      }");
        } else
        {
          // outData.println(" public bool " + field.useUpperName() +
          // "IsNull;");
          outData.println("      private bool " + field.useLowerName() + "IsNull;");
          outData.println("      public bool " + field.useUpperName() + "IsNull");
          outData.println("      { ");
          outData.println("           get { return this." + field.useLowerName() + "IsNull; }");
          outData.println("           set { this." + field.useLowerName() + "IsNull = value; }");
          outData.println("      }");
        }
      }
    }
    outData.println("  }");
  }
  public static void generateEnumOrdinals(Table table, PrintWriter outData)
  {
    for (int i = 0; i < table.fields.size(); i++)
    {
      Field field = (Field) table.fields.elementAt(i);
      if (field.enums.size() > 0)
      {
        outData.println("  public class " + table.useName() + field.useUpperName() + "Ord");
        outData.println("  {");
        String datatype = "int";
        if (field.type == Field.ANSICHAR && field.length == 1)
          datatype = "string";
        for (int j = 0; j < field.enums.size(); j++)
        {
          Enum en = (Enum) field.enums.elementAt(j);
          String evalue = "" + en.value;
          if (field.type == Field.ANSICHAR && field.length == 1)
            evalue = "\"" + (char) en.value + "\"";
          outData.println("    public const " + datatype + " " + en.name + " = " + evalue + ";");
        }
        outData.println("    public static string ToString(" + datatype + " ordinal)");
        outData.println("    {");
        outData.println("      switch (ordinal)");
        outData.println("      {");
        for (int j = 0; j < field.enums.size(); j++)
        {
          Enum en = (Enum) field.enums.elementAt(j);
          String evalue = "" + en.value;
          if (field.type == Field.ANSICHAR && field.length == 1)
            evalue = "\"" + (char) en.value + "\"";
          outData.println("      case " + evalue + ": return \"" + en.name + "\";");
        }
        outData.println("      }");
        outData.println("      return \"unknown ordinal: \"+ordinal;");
        outData.println("    }");
        outData.println("  }");
      }
    }
  }
  public static void generateStructs(Table table, PrintWriter outData)
  {
    if (table.fields.size() > 0)
    {
      if (table.comments.size() > 0)
      {
        outData.println("  /// <summary>");
        for (int i = 0; i < table.comments.size(); i++)
        {
          String s = (String) table.comments.elementAt(i);
          outData.println("  /// " + s);
        }
        outData.println("  /// </summary>");
      }
      boolean hasReturning = false;
      for (int i = 0; i < table.procs.size(); i++)
      {
        Proc proc = (Proc) table.procs.elementAt(i);
        if (proc.hasReturning && proc.isStd)
        {
          hasReturning = true;
          break;
        }
      }
      generateStructPairs(null, table.fields, null, table.useName(), outData, null, hasReturning);
      generateEnumOrdinals(table, outData);
      for (int i = 0; i < table.procs.size(); i++)
      {
        Proc proc = (Proc) table.procs.elementAt(i);
        if (proc.isData || proc.isStd || proc.hasNoData())
          continue;
        if (proc.comments.size() > 0)
        {
          outData.println("  /// <summary>");
          for (int j = 0; j < proc.comments.size(); j++)
          {
            String s = (String) proc.comments.elementAt(j);
            outData.println("  /// " + s);
          }
          outData.println("  /// </summary>");
        }
        Vector<Field> fields = new Vector<Field>();
        if (!proc.extendsStd)
          for (int j = 0; j < proc.outputs.size(); j++)
            fields.addElement(proc.outputs.elementAt(j));
        for (int j = 0; j < proc.inputs.size(); j++)
        {
          Field field = (Field) proc.inputs.elementAt(j);
          if (proc.hasOutput(field.name) == false || field.isExtStd)
            fields.addElement(field);
        }
        for (int j = 0; j < proc.dynamics.size(); j++)
        {
          String s = (String) proc.dynamics.elementAt(j);
          Integer n = (Integer) proc.dynamicSizes.elementAt(j);
          Field field = new Field();
          field.name = s;
          field.type = Field.DYNAMIC;
          field.length = n.intValue();
          fields.addElement(field);
        }
        generateStructPairs(proc, fields, proc.dynamics, table.useName() + proc.upperFirst(), outData, table.useName(),
            false);
      }
    }
  }
  public static void generateCode(Table table, PrintWriter outData)
  {
    boolean firsttime = true;
    boolean isLoaded = true;
    for (int i = 0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc) table.procs.elementAt(i);
      if (proc.isData == true || proc.isStd == false)
        continue;
      generateStdCode(table, proc, outData, firsttime, isLoaded);
      if (proc.outputs.size() > 0 && !proc.isSingle)
      {
        isLoaded = false;
      }
      firsttime = false;
    }
    if (firsttime == false)
      outData.println("  }");
    for (int i = 0; i < table.procs.size(); i++)
    {
      Proc proc = (Proc) table.procs.elementAt(i);
      if (proc.isData == true || proc.isStd == true)
        continue;
      generateCode(table, proc, outData);
    }
  }
  static PlaceHolder placeHolder;
  static void generateStoredProc(Proc proc, String storedProcName, Vector<?> lines)
  {
    // procData.println("IF EXISTS (SELECT * FROM SYSOBJECTS WHERE ID =
    // OBJECT_ID('dbo." + storedProcName + "') AND SYSSTAT & 0xf = 4)");
    procData.println("IF OBJECT_ID('dbo." + storedProcName + "','P') IS NOT NULL");
    procData.println("    DROP PROCEDURE dbo." + storedProcName);
    procData.println("GO");
    procData.println("");
    procData.println("CREATE PROCEDURE dbo." + storedProcName);
    String comma = "(";
    for (int i = 0; i < placeHolder.pairs.size(); i++)
    {
      PlaceHolderPairs pair = (PlaceHolderPairs) placeHolder.pairs.elementAt(i);
      Field field = pair.field;
      procData.println(comma + " @P" + i + " " + varType(field) + " -- " + field.name);
      comma = ",";
    }
    if (placeHolder.pairs.size() > 0)
      procData.println(")");
    procData.println("AS");
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String) lines.elementAt(i);
      procData.println(line.substring(1, line.length() - 1));
    }
    if (proc.isInsert && proc.hasReturning && proc.table.hasIdentity)
    {
      procData.println("; SELECT CAST(SCOPE_IDENTITY() AS INT)");
    }
    procData.println("GO");
    procData.println("");
  }
  static void generateStoredProcCommand(Proc proc, PrintWriter outData)
  {
    placeHolder = new PlaceHolder(proc, PlaceHolder.AT, "");
    String storedProcName = proc.table.useName() + proc.upperFirst();
    Vector<?> lines = placeHolder.getLines();
    generateStoredProc(proc, storedProcName, lines);
    outData.println("    public string Command" + proc.upperFirst() + "()");
    outData.println("    {");
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String) lines.elementAt(i);
      outData.println("      // " + line.substring(1, line.length() - 1));
    }
    outData.println("      return \"" + storedProcName + "\";");
    outData.println("    }");
  }
  static void generateCommand(Proc proc, PrintWriter outData)
  {
    if (proc.hasReturning)
    {
      placeHolder = new PlaceHolder(proc, PlaceHolder.CURLY, "");
      outData.println(
          "    public string Command" + proc.upperFirst() + "(Connect aConnect, string aTable, string aField)");
    } else
    {
      placeHolder = new PlaceHolder(proc, PlaceHolder.CURLY, "Rec.");
      outData.println("    public string Command" + proc.upperFirst() + "()");
    }
    Vector<?> lines = placeHolder.getLines();
    outData.println("    {");
    if (proc.hasReturning)
      outData.println("      Returning _ret = new Returning(aConnect.TypeOfVendor, aTable, aField);");
    outData.println("      return ");
    String plus = "        ";
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String) lines.elementAt(i);
      // if (proc.hasReturning)
      // {
      // String retName = returningField(proc);
      // int b = line.indexOf(retName);
      // if (b > 0 && (line.charAt(b - 1) == ' ' || line.charAt(b - 1) == '[' ||
      // line.charAt(b - 1) == '\"' || line.charAt(b - 1) == '\''))
      // {
      // int e = line.indexOf(',');
      // if (e == -1) e = line.indexOf('"');
      // if (e - b == retName.length())
      // line = "_ret.checkUse(" + line + ")";
      // }
      // }
      outData.println(plus + line);
      plus = "      + ";
    }
    outData.println("      ;");
    outData.println("    }");
  }
  static void generateNonQueryProc(Proc proc, String mainName, PrintWriter outData)
  {
    Field identity = null;
    for (int i = 0; i < proc.table.fields.size(); i++)
    {
      Field field = (Field) proc.table.fields.elementAt(i);
      if (field.isPrimaryKey)
      {
        identity = field;
        break;
      }
    }
    if (proc.hasReturning)
      outData.println("    public bool " + proc.upperFirst() + "(Connect aConnect)");
    else
      outData.println("    public void " + proc.upperFirst() + "(Connect aConnect)");
    outData.println("    {");
    outData.println("      using (Cursor wCursor = new Cursor(aConnect))");
    outData.println("      {");
    if (doMSSqlStoredProcs(proc))
      outData.println("        wCursor.Procedure(Command" + proc.upperFirst() + "());");
    else
    {
      if (placeHolder.pairs.size() > 0)
        outData.println("        // format command to change {n} to ?, @Pn or :Pn placeholders depending on Vendor");
      outData.println("        wCursor.Format(Command" + proc.upperFirst() + "(" + returning(proc) + "), "
          + placeHolder.pairs.size() + ");");
    }
    for (int i = 0; i < placeHolder.pairs.size(); i++)
    {
      PlaceHolderPairs pair = (PlaceHolderPairs) placeHolder.pairs.elementAt(i);
      Field field = pair.field;
      String member = "";
      if (field.type == Field.BLOB)
        member = ".getBlob()";
      String tail = "";
      if (field.isNull)
        tail = ", mRec." + field.useUpperName() + "IsNull";
      if (proc.isInsert && field.isSequence)
      {
        outData.println("        var " + field.useLowerName() + " = mRec." + field.useUpperName() + ";");
        outData.println("        wCursor.Parameter(" + i + ", wCursor.GetSequence(\"" + proc.table.name + "\",\""
            + field.name + "\", ref " + field.useLowerName() + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + ";");
      } else if (field.type == Field.TIMESTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetTimeStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else if (field.type == Field.USERSTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetUserStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else
        outData.println("        wCursor.Parameter(" + i + ", mRec." + field.useUpperName() + member + tail + ");");
    }
    if (proc.hasReturning)
    {
      outData.println("        wCursor.Run();");
      outData.println("        bool wResult = (wCursor.HasReader() && wCursor.Read());");
      outData.println("        if (wResult == true)");
      if (identity.isNull && getDataType(identity, new StringBuffer(), new StringBuffer()) == "string")
      {
        outData.println("      {");
        outData.println(
            "          var " + identity.useLowerName() + "IsNull = mRec." + identity.useUpperName() + "IsNull;");
        outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity)
            + "wCursor.GetString(0, out " + identity.useLowerName() + "IsNull);");
        outData
            .println("          mRec." + identity.useUpperName() + "IsNull = " + identity.useLowerName() + "IsNull;");
        outData.println("      }");
      } else if (identity.isNull)
      {
        outData.println(
            "          var " + identity.useLowerName() + "IsNull = mRec." + identity.useUpperName() + "IsNull;");
        outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity) + "wCursor."
            + cursorGet(identity, 0) + ";");
        outData
            .println("          mRec." + identity.useUpperName() + "IsNull = " + identity.useLowerName() + "IsNull;");
      } else
      {
        outData.println("      {");
        outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity) + "wCursor."
            + cursorGet(identity, 0) + ";");
        outData.println("      }");
      }

      outData.println("        if (wCursor.HasReader())");
      outData.println("          wCursor.Close();");
      outData.println("        return wResult;");
      outData.println("      }");
      outData.println("    }");
    } else
    {
      outData.println("        wCursor.Exec();");
      outData.println("      }");
      outData.println("    }");
    }
  }

  static void generateFunc(Proc proc, String mainName, PrintWriter outData)
  {
    String line, input = "", postfix = "";
    boolean isLoaded = false;
    if (proc.outputs.size() > 0 && !proc.isSingle && !proc.isInsert)
    {
      isLoaded = true;
    }

    for (int i = 0; i < proc.inputs.size(); i++)
    {

      Field field = (Field) proc.inputs.elementAt(i);
      if (field.isPrimaryKey || proc.updateFields.contains(field.name))
      {
        StringBuffer maker = new StringBuffer();
        StringBuffer temp2 = new StringBuffer();
        String result = returnNullableDataType(field, maker, temp2);
        line = ", ";
        input = input + line + result + " " + field.useLowerName();
      }
    }

    for (int i = 0; i < proc.inputs.size(); i++)
    {
      Field field = (Field) proc.inputs.elementAt(i);
      if (field.isExtStdOut)
        continue;
      if (field.type == Field.TIMESTAMP || field.type == Field.USERSTAMP)
      {
        continue;
      }
      if (!(field.isPrimaryKey || proc.updateFields.contains(field.name)))
      {
        StringBuffer maker = new StringBuffer();
        StringBuffer temp2 = new StringBuffer();
        String result = returnNullableDataType(field, maker, temp2);
        line = ", ";
        input = input + line + result + " " + field.useLowerName();
      }
    }
    for (int j = 0; j < proc.dynamics.size(); j++)
    {
      String s = (String) proc.dynamics.elementAt(j);
      Integer n = (Integer) proc.dynamicSizes.elementAt(j);
      Field field = new Field();
      field.name = s;
      field.type = Field.DYNAMIC;
      field.length = n.intValue();
      line = ", ";
      input = input + line + "string " + field.useLowerName();
    }
    input = input + ")";
    if (input.equals(")"))
    {
      postfix = "Struct";
    }

    if (proc.isSingle)
      outData.println("    public " + mainName + "Rec " + proc.upperFirst() + postfix + "(Connect aConnect" + input);
    else if (!isLoaded)
    {
      outData.println("    public void " + proc.upperFirst() + postfix + "(Connect aConnect" + input);
    } else if (proc.hasReturning)
    {
      outData.println("    public bool " + proc.upperFirst() + postfix + "(Connect aConnect" + input);
    } else
      outData
          .println("    public List<" + mainName + "Rec> " + proc.upperFirst() + postfix + "(Connect aConnect" + input);
    outData.println("    {");
    for (int i = 0; i < proc.inputs.size(); i++)
    {
      Field field = (Field) proc.inputs.elementAt(i);
      if (field.isExtStdOut)
        continue;
      if (field.type == Field.TIMESTAMP || field.type == Field.USERSTAMP)
      {
        continue;
      }
      if (field.isNull && getNullableType(field))
      {
        outData.println("       if (" + field.useLowerName() + ".HasValue)");
        outData.println("         mRec." + field.useUpperName() + " = " + field.useLowerName() + ".Value;");
        outData.println("       mRec." + field.useUpperName() + "IsNull = !" + field.useLowerName() + ".HasValue;");
      } else
      {
        outData.println("       mRec." + field.useUpperName() + " = " + field.useLowerName() + ";");
      }
    }
    for (int j = 0; j < proc.dynamics.size(); j++)
    {
      String s = (String) proc.dynamics.elementAt(j);
      Integer n = (Integer) proc.dynamicSizes.elementAt(j);
      Field field = new Field();
      field.name = s;
      field.type = Field.DYNAMIC;
      field.length = n.intValue();
      line = ", ";
      outData.println("       mRec." + field.useUpperName() + " = " + field.useLowerName() + ";");
    }
    if (proc.isSingle)
    {
      outData.println("       if (" + proc.upperFirst() + "(aConnect))");
      outData.println("         return mRec;");
      outData.println("       else");
      outData.println("         return null;");
      outData.println("    }");
    } else if (!isLoaded)
    {
      outData.println("       " + proc.upperFirst() + "(aConnect);");
      outData.println("    }");
    } else if (proc.hasReturning)
    {
      outData.println("       return " + proc.upperFirst() + "(aConnect);");
      outData.println("    }");
    } else
    {
      outData.println("       " + proc.upperFirst() + "Load(aConnect);");
      outData.println("       return Loaded;");
      outData.println("    }");
    }

  }
  static void generateReturningProc(Proc proc, String mainName, PrintWriter outData)
  {
    Field identity = null;
    for (int i = 0; i < proc.table.fields.size(); i++)
    {
      Field field = (Field) proc.table.fields.elementAt(i);
      if (field.isPrimaryKey)
      {
        identity = field;
        break;
      }
    }
    if (identity == null)
    {
      generateNonQueryProc(proc, mainName, outData);
      return;
    }
    outData.println("    public bool " + proc.upperFirst() + "(Connect aConnect)");
    outData.println("    {");
    outData.println("      using (Cursor wCursor = new Cursor(aConnect))");
    outData.println("      {");
    if (doMSSqlStoredProcs(proc))
      outData.println("        wCursor.Procedure(Command" + proc.upperFirst() + "());");
    else
    {
      if (placeHolder.pairs.size() > 0)
        outData.println("        // format command to change {n} to ?, @Pn or :Pn placeholders depending on Vendor");
      outData.println("        wCursor.Format(Command" + proc.upperFirst() + "(" + returning(proc) + "), "
          + placeHolder.pairs.size() + ");");
    }
    for (int i = 0; i < placeHolder.pairs.size(); i++)
    {
      PlaceHolderPairs pair = (PlaceHolderPairs) placeHolder.pairs.elementAt(i);
      Field field = pair.field;
      String member = "";
      if (field.type == Field.BLOB)
        member = ".getBlob()";
      String tail = "";
      if (field.isNull)
        tail = ", mRec." + field.useUpperName() + "IsNull";
      if (field.type == Field.TIMESTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetTimeStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else if (field.type == Field.USERSTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetUserStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else
        outData.println("        wCursor.Parameter(" + i + ", mRec." + field.useUpperName() + member + tail + ");");
    }
    outData.println("        wCursor.Run();");
    outData.println("        bool wResult = (wCursor.HasReader() && wCursor.Read());");
    outData.println("        if (wResult == true)");
    if (identity.isNull && getDataType(identity, new StringBuffer(), new StringBuffer()) == "string")
    {
      outData.println("      {");
      outData
          .println("          var " + identity.useLowerName() + "IsNull = mRec." + identity.useUpperName() + "IsNull;");
      outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity)
          + "wCursor.GetString(0, out " + identity.useLowerName() + "IsNull);");
      outData.println("          mRec." + identity.useUpperName() + "IsNull = " + identity.useLowerName() + "IsNull;");
      outData.println("      }");
    } else if (identity.isNull)
    {
      outData
          .println("          var " + identity.useLowerName() + "IsNull = mRec." + identity.useUpperName() + "IsNull;");
      outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity) + "wCursor."
          + cursorGet(identity, 0) + ";");
      outData.println("          mRec." + identity.useUpperName() + "IsNull = " + identity.useLowerName() + "IsNull;");
    } else
    {
      outData.println("      {");
      outData.println("          mRec." + identity.useUpperName() + " = " + castOf(identity) + "wCursor."
          + cursorGet(identity, 0) + ";");
      outData.println("      }");
    }

    outData.println("        if (wCursor.HasReader())");
    outData.println("          wCursor.Close();");
    outData.println("        return wResult;");
    outData.println("      }");
    outData.println("    }");
  }
  static void generateReadOneProc(Proc proc, String mainName, PrintWriter outData)
  {
    outData.println("    public bool " + proc.upperFirst() + "(Connect aConnect)");
    outData.println("    {");
    outData.println("      using (Cursor wCursor = new Cursor(aConnect))");
    outData.println("      {");
    if (doMSSqlStoredProcs(proc))
      outData.println("        wCursor.Procedure(Command" + proc.upperFirst() + "());");
    else
    {
      if (placeHolder.pairs.size() > 0)
        outData.println("        // format command to change {n} to ?, @Pn or :Pn placeholders depending on Vendor");
      outData.println("        wCursor.Format(Command" + proc.upperFirst() + "(" + returning(proc) + "), "
          + placeHolder.pairs.size() + ");");
    }
    for (int i = 0; i < placeHolder.pairs.size(); i++)
    {
      PlaceHolderPairs pair = (PlaceHolderPairs) placeHolder.pairs.elementAt(i);
      Field field = pair.field;
      String member = "";
      if (field.type == Field.BLOB)
        member = ".getBlob()";
      String tail = "";
      if (field.isNull)
        tail = ", mRec." + field.useUpperName() + "IsNull";
      if (field.type == Field.TIMESTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetTimeStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else if (field.type == Field.USERSTAMP)
      {
        outData.println("        var " + field.useLowerName() + i + " = mRec." + field.useUpperName() + ";");
        outData.println(
            "        wCursor.Parameter(" + i + ", wCursor.GetUserStamp(ref " + field.useLowerName() + i + "));");
        outData.println("        mRec." + field.useUpperName() + " = " + field.useLowerName() + i + ";");
      } else
        outData.println("        wCursor.Parameter(" + i + ", mRec." + field.useUpperName() + member + tail + ");");
    }
    outData.println("        wCursor.Run();");
    outData.println("        bool wResult = (wCursor.HasReader() && wCursor.Read());");
    outData.println("        if (wResult == true)");
    outData.println("        {");
    for (int i = 0; i < proc.outputs.size(); i++)
    {
      Field field = (Field) proc.outputs.elementAt(i);
      String member = "";
      if (field.type == Field.BLOB)
        member = ".Buffer";
      if (field.isNull && getDataType(field, new StringBuffer(), new StringBuffer()) == "string")
      {
        outData.println("          var " + field.useLowerName() + "IsNull = mRec." + field.useUpperName() + "IsNull;");
        outData.println("          mRec." + field.useUpperName() + member + " = " + castOf(field) + "wCursor.GetString("
            + i + ", out " + field.useLowerName() + "IsNull);");
        outData.println("          mRec." + field.useUpperName() + "IsNull = " + field.useLowerName() + "IsNull;");
      } else if (field.isNull)
      {
        outData.println("          var " + field.useLowerName() + "IsNull = mRec." + field.useUpperName() + "IsNull;");
        outData.println("          mRec." + field.useUpperName() + member + " = " + castOf(field) + "wCursor."
            + cursorGet(field, i) + ";");
        outData.println("          mRec." + field.useUpperName() + "IsNull = " + field.useLowerName() + "IsNull;");
      } else
      {
        outData.println("          mRec." + field.useUpperName() + member + " = " + castOf(field) + "wCursor."
            + cursorGet(field, i) + ";");
      }
    }
    outData.println("        }");
    outData.println("        if (wCursor.HasReader())");
    outData.println("          wCursor.Close();");
    outData.println("        return wResult;");
    outData.println("      }");
    outData.println("    }");
  }
  static String returning(Proc proc)
  {
    if (proc.hasReturning == false)
      return "";
    String tableName = proc.table.useName();
    String fieldName = "";
    for (int i = 0; i < proc.table.fields.size(); i++)
    {
      Field field = (Field) proc.table.fields.elementAt(i);
      if (field.isSequence == true)
      {
        fieldName = field.useName();
        break;
      }
    }
    return "aConnect, \"" + tableName + "\", \"" + fieldName + "\"";
  }
  static void generateFetchProc(Proc proc, String mainName, PrintWriter outData, boolean isLoaded)
  {
    outData.println("    private void " + proc.upperFirst() + "(Connect aConnect)");
    outData.println("    {");
    if (doMSSqlStoredProcs(proc))
      outData.println("      mCursor.Procedure(Command" + proc.upperFirst() + "());");
    else
    {
      if (placeHolder.pairs.size() > 0)
        outData.println("      // format command to change {n} to ?, @Pn or :Pn placeholders depending on Vendor");
      outData.println("      mCursor.Format(Command" + proc.upperFirst() + "(" + returning(proc) + "), "
          + placeHolder.pairs.size() + ");");
    }
    for (int i = 0; i < placeHolder.pairs.size(); i++)
    {
      PlaceHolderPairs pair = (PlaceHolderPairs) placeHolder.pairs.elementAt(i);
      Field field = pair.field;
      String member = "";
      if (field.type == Field.BLOB)
        member = ".getBlob()";
      String tail = "";
      if (field.isNull)
        tail = ", mRec." + field.useUpperName() + "IsNull";
      outData.println("      mCursor.Parameter(" + i + ", mRec." + field.useUpperName() + member + tail + ");");
    }
    outData.println("      mCursor.Run();");
    outData.println("    }");
    outData.println("    private bool " + proc.upperFirst() + "Fetch()");
    outData.println("    {");
    outData.println("      bool wResult = (mCursor.HasReader() && mCursor.Read());");
    outData.println("      if (wResult == true)");
    outData.println("      {");
    for (int i = 0; i < proc.outputs.size(); i++)
    {
      Field field = (Field) proc.outputs.elementAt(i);
      String member = "";
      if (field.type == Field.BLOB)
        member = ".Buffer";
      if (field.isNull && getDataType(field, new StringBuffer(), new StringBuffer()) == "string")
      {
        outData.println("        var " + field.useLowerName() + "IsNull = mRec." + field.useUpperName() + "IsNull;");
        outData.println("        mRec." + field.useUpperName() + member + " = " + castOf(field) + "mCursor.GetString("
            + i + ", out " + field.useLowerName() + "IsNull);");
        outData.println("        mRec." + field.useUpperName() + "IsNull = " + field.useLowerName() + "IsNull;");
      } else if (field.isNull)
      {
        outData.println("        var " + field.useLowerName() + "IsNull = mRec." + field.useUpperName() + "IsNull;");
        outData.println("        mRec." + field.useUpperName() + member + " = " + castOf(field) + "mCursor."
            + cursorGet(field, i) + ";");
        outData.println("        mRec." + field.useUpperName() + "IsNull = " + field.useLowerName() + "IsNull;");
      } else
      {
        outData.println("        mRec." + field.useUpperName() + member + " = " + castOf(field) + "mCursor."
            + cursorGet(field, i) + ";");
      }
    }
    outData.println("      }");
    outData.println("      else if (mCursor.HasReader())");
    outData.println("        mCursor.Close();");
    outData.println("      return wResult;");
    outData.println("    }");
    outData.println("    public void " + proc.upperFirst() + "Load(Connect aConnect)");
    outData.println("    {");
    outData.println("      using (mCursor = new Cursor(aConnect))");
    outData.println("      {");
    outData.println("        " + proc.upperFirst() + "(aConnect);");
    outData.println("        while (" + proc.upperFirst() + "Fetch())");
    outData.println("        {");
    outData.println("          mList.Add(mRec);");
    outData.println("          mRec = new " + mainName + "Rec();");
    outData.println("        }");
    outData.println("      }");
    outData.println("    }");
    outData.println("    public bool " + proc.upperFirst() + "First(Connect aConnect)");
    outData.println("    {");
    outData.println("      using (mCursor = new Cursor(aConnect))");
    outData.println("      {");
    outData.println("        " + proc.upperFirst() + "(aConnect);");
    outData.println("        if (" + proc.upperFirst() + "Fetch())");
    outData.println("        {");
    outData.println("          mCursor.Close();");
    outData.println("          return true;");
    outData.println("        }");
    outData.println("        return false;");
    outData.println("      }");
    outData.println("    }");
    if (isLoaded)
    {
      if (useGenerics)
        outData.println("    public List<" + mainName + "Rec> Loaded { get { return mList; } }");
      else
        outData.println("    public ArrayList Loaded { get { return mList; } }");
    }
    outData.println("    public class " + proc.upperFirst() + "Ord");
    outData.println("    {");
    int noInDataSet = 0;
    for (int i = 0; i < proc.inputs.size(); i++)
    {
      Field field = (Field) proc.inputs.elementAt(i);
      outData.println("      public const int " + field.useUpperName() + " = " + noInDataSet + ";");
      noInDataSet++;
    }
    for (int i = 0; i < proc.outputs.size(); i++)
    {
      Field field = (Field) proc.outputs.elementAt(i);
      if (proc.hasInput(field.name))
        continue;
      outData.println("      public const int " + field.useUpperName() + " = " + noInDataSet + ";");
      noInDataSet++;
    }
    outData.println("      public static string ToString(int ordinal)");
    outData.println("      {");
    outData.println("        switch (ordinal)");
    outData.println("        {");
    noInDataSet = 0;
    for (int i = 0; i < proc.inputs.size(); i++)
    {
      Field field = (Field) proc.inputs.elementAt(i);
      outData.println("        case " + noInDataSet + ": return \"" + field.useUpperName() + "\";");
      noInDataSet++;
    }
    for (int i = 0; i < proc.outputs.size(); i++)
    {
      Field field = (Field) proc.outputs.elementAt(i);
      if (proc.hasInput(field.name))
        continue;
      outData.println("        case " + noInDataSet + ": return \"" + field.useUpperName() + "\";");
      noInDataSet++;
    }
    outData.println("        }");
    outData.println("        return \"<??\"+ordinal+\"??>\";");
    outData.println("      }");
    outData.println("    }");
    if (!noDatatables)
    {
      if (useSeparate == false && usePartials == false)
        generateFetchProcDataTables(proc, mainName, outData);
    }
  }
  static void generateFetchProcDataTables(Proc proc, String mainName, PrintWriter outData)
  {
    outData.println(
        "    public " + proc.table.useName() + proc.upperFirst() + "DataTable " + proc.upperFirst() + "DataTable()");
    outData.println("    {");
    outData.println("      " + proc.table.useName() + proc.upperFirst() + "DataTable wResult = new "
        + proc.table.useName() + proc.upperFirst() + "DataTable(mList);");
    outData.println("      return wResult;");
    outData.println("    }");
    outData.println("    public " + proc.table.useName() + proc.upperFirst() + "DataTable " + proc.upperFirst()
        + "DataTable(Connect aConnect)");
    outData.println("    {");
    outData.println("      " + proc.upperFirst() + "Load(aConnect);");
    outData.println("      return " + proc.upperFirst() + "DataTable();");
    outData.println("    }");
  }
  static void generateProcFunctions(Proc proc, String name, PrintWriter outData, boolean isLoaded)
  {
    if (proc.outputs.size() > 0 && !proc.isSingle)
      generateFetchProc(proc, name, outData, isLoaded);
    else if (proc.outputs.size() > 0)
      generateReadOneProc(proc, name, outData);
    else if (proc.isInsert && proc.hasReturning)
      generateReturningProc(proc, name, outData);
    else
      generateNonQueryProc(proc, name, outData);
    if (useFunc)
      generateFunc(proc, name, outData);
  }
  static void generateCClassTop(Proc proc, String mainName, PrintWriter outData, boolean doCursor)
  {
    outData.println("  [Serializable()]");
    outData.println("  public " + (usePartials ? "partial " : "") + "class " + mainName);
    outData.println("  {");
    if (doCursor == true || proc.hasNoData() == false)
    {
      outData.println("    private " + mainName + "Rec mRec;");
      outData.println("    public " + mainName + "Rec Rec { get { return mRec; } set { mRec = value; } }");

      if (doCursor == true || (proc.outputs.size() > 0 && !proc.isSingle))
      {
        if (useGenerics)
          outData.println("    private List<" + mainName + "Rec> mList;");
        else
          outData.println("    private ArrayList mList;");
        outData.println("    public int Count { get { return mList.Count; } }");
        outData.println("    public Cursor mCursor;");
        outData.println("    public " + mainName + "Rec this[int i]");
        outData.println("    {");
        outData.println("      get");
        outData.println("      {");
        outData.println("        if (i >= 0 && i < mList.Count)");
        if (useGenerics)
          outData.println("          return mList[i];");
        else
          outData.println("          return (" + mainName + "Rec)mList[i];");
        outData.println("        throw new JPortalException(\"" + mainName + " index out of range\");");
        outData.println("      }");
        outData.println("      set");
        outData.println("      {");
        outData.println("        if (i < mList.Count)");
        outData.println("          mList.RemoveAt(i);");
        outData.println("        mList.Insert(i, value);");
        outData.println("      }");
        outData.println("    }");
        if (useYields)
        {
          if (useGenerics)
            outData.println("    public IEnumerable<" + mainName + "Rec> Yielded()");
          else
            outData.println("    public IEnumerable Yielded()");
          outData.println("    {");
          outData.println("      for (int i=0; i<Count; i++)");
          outData.println("        yield return this[i];");
          outData.println("    }");
        }
      }
      outData.println("    public void Clear()");
      outData.println("    {");
      if (doCursor == true || (proc.outputs.size() > 0 && !proc.isSingle))
        if (useGenerics)
          outData.println("      mList = new List<" + mainName + "Rec>();");
        else
          outData.println("      mList = new ArrayList();");
      outData.println("      mRec = new " + mainName + "Rec();");
      outData.println("    }");
      outData.println("    public " + mainName + "()");
      outData.println("    {");
      outData.println("      Clear();");
      outData.println("    }");
    }
  }

  static boolean doMSSqlStoredProcs(Proc proc)
  {
    return (mSSqlStoredProcs == true && proc.dynamics.size() == 0)
        || (proc.isSProc == true && proc.dynamics.size() == 0);
  }
  static void generateCode(Table table, Proc proc, PrintWriter outData)
  {
    if (proc.comments.size() > 0)
    {
      outData.println("  /// <summary>");
      for (int i = 0; i < proc.comments.size(); i++)
      {
        String comment = (String) proc.comments.elementAt(i);
        outData.println("  /// " + comment);
      }
      outData.println("  /// </summary>");
    }

    generateCClassTop(proc, table.useName() + proc.upperFirst(), outData, false);
    if (doMSSqlStoredProcs(proc) == true)
      generateStoredProcCommand(proc, outData);
    else
      generateCommand(proc, outData);
    generateProcFunctions(proc, table.useName() + proc.upperFirst(), outData, true);
    outData.println("  }");
  }
  static void generateStdCode(Table table, Proc proc, PrintWriter outData, boolean firsttime, boolean isLoaded)
  {
    if (firsttime == true)
      generateCClassTop(proc, table.useName(), outData, table.hasCursorStdProc());
    if (proc.comments.size() > 0)
    {
      outData.println("    /// <summary>");
      for (int i = 0; i < proc.comments.size(); i++)
      {
        String comment = (String) proc.comments.elementAt(i);
        outData.println("    /// " + comment);
      }
      outData.println("    /// </summary>");
    }
    if (doMSSqlStoredProcs(proc) == true)
      generateStoredProcCommand(proc, outData);
    else
      generateCommand(proc, outData);
    generateProcFunctions(proc, table.useName(), outData, isLoaded);
  }
  static String castOf(Field field)
  {
    switch (field.type)
    {
    case Field.BYTE:
      return "(byte)";
    case Field.SHORT:
      return "(short)";
    }
    return "";
  }
  static String validNull(Field field)
  {
    switch (field.type)
    {
    case Field.DATE:
    case Field.DATETIME:
    case Field.TIMESTAMP:
    case Field.TIME:
      return "DateTime.MinValue";
    case Field.BOOLEAN:
      return "false";
    case Field.BYTE:
    case Field.DOUBLE:
    case Field.FLOAT:
    case Field.IDENTITY:
    case Field.INT:
    case Field.LONG:
    case Field.SEQUENCE:
    case Field.BIGIDENTITY:
    case Field.BIGSEQUENCE:
    case Field.SHORT:
    case Field.MONEY:
    case Field.STATUS:
      return "0";
    case Field.UID:
      return "Guid.Empty";
    }
    return "null";
  }
  static String cursorGet(Field field, int occurence)
  {
    String tail = ")";
    if (field.isNull)
      tail = ", out " + field.useLowerName() + "IsNull)";
    switch (field.type)
    {
    case Field.ANSICHAR:
      return "GetString(" + occurence + tail;
    case Field.BLOB:
      return "GetBlob(" + occurence + ", " + field.length + tail;
    case Field.BOOLEAN:
      return "GetBoolean(" + occurence + tail;
    case Field.BYTE:
      return "GetByte(" + occurence + tail;
    case Field.CHAR:
      return "GetString(" + occurence + tail;
    case Field.DATE:
      return "GetDateTime(" + occurence + tail;
    case Field.DATETIME:
      return "GetDateTime(" + occurence + tail;
    case Field.DYNAMIC:
      return "GetString(" + occurence + tail;
    case Field.DOUBLE:
    case Field.FLOAT:
      if (field.precision > 15)
        return "GetDecimal(" + occurence + tail;
      return "GetDouble(" + occurence + tail;
    case Field.IDENTITY:
      return "GetInt(" + occurence + tail;
    case Field.INT:
      return "GetInt(" + occurence + tail;
    case Field.LONG:
      return "GetLong(" + occurence + tail;
    case Field.BIGSEQUENCE:
      return "GetLong(" + occurence + tail;
    case Field.BIGIDENTITY:
      return "GetLong(" + occurence + tail;
    case Field.MONEY:
      return "GetDecimal(" + occurence + tail;
    case Field.SEQUENCE:
      return "GetInt(" + occurence + tail;
    case Field.SHORT:
      return "GetShort(" + occurence + tail;
    case Field.TIME:
      return "GetDateTime(" + occurence + tail;
    case Field.TIMESTAMP:
      return "GetDateTime(" + occurence + tail;
    case Field.TLOB:
      return "GetString(" + occurence + tail;
    case Field.XML:
      return "GetString(" + occurence + tail;
    case Field.UID:
      return "GetGuid(" + occurence + tail;
    case Field.USERSTAMP:
      return "GetString(" + occurence + tail;
    default:
      break;
    }
    return "Get(" + occurence + tail;
  }
  static String dataTableType(Field field)
  {
    switch (field.type)
    {
    case Field.ANSICHAR:
      return "String";
    case Field.BLOB:
      return "Byte[]";
    case Field.BOOLEAN:
      return "Boolean";
    case Field.BYTE:
      return "Byte";
    case Field.CHAR:
      return "String";
    case Field.DATE:
      return "DateTime";
    case Field.DATETIME:
      return "DateTime";
    case Field.DYNAMIC:
      return "String";
    case Field.DOUBLE:
    case Field.FLOAT:
      if (field.precision > 15)
        return "String";
      return "Double";
    case Field.IDENTITY:
      return "Int32";
    case Field.BIGIDENTITY:
      return "Int64";
    case Field.INT:
      return "Int32";
    case Field.LONG:
      return "Int64";
    case Field.MONEY:
      return "String";
    case Field.SEQUENCE:
      return "Int32";
    case Field.BIGSEQUENCE:
      return "Int64";
    case Field.SHORT:
      return "Int16";
    case Field.TIME:
      return "DateTime";
    case Field.TIMESTAMP:
      return "DateTime";
    case Field.TLOB:
      return "String";
    case Field.XML:
      return "String";
    case Field.UID:
      return "Guid";
    case Field.USERSTAMP:
      return "String";
    default:
      break;
    }
    return "dataTableType";
  }
  static String fieldDef(Field field, String temp)
  {
    StringBuffer maker = new StringBuffer();
    StringBuffer temp2 = new StringBuffer(temp);
    String result = getDataType(field, maker, temp2);
    String set = "          set { this." + field.useLowerName() + " = value; } \r\n";

    if (useNotify)
    {
      set = "          set {\r\n" + "                   this." + field.useLowerName() + " = value; \r\n"
          + "                   NotifyPropertyChanged();\r\n" + "              }\r\n";

    }

    if (field.isNull && result == "string")
    {
      set = "          set {\r\n" + "                   this." + field.useLowerName() + " = value; \r\n"
          + "                   this." + field.useUpperName() + "IsNull = value == null ? true : false;\r\n"
          + (useNotify ? "                   NotifyPropertyChanged();\r\n" : "") + "              }\r\n";
    }
    String ret = "  private " + result + " " + field.useLowerName() + maker.toString() + ";\r\n" + "      "
        + temp2.toString() + ")]\r\n" + "      public " + result + " " + field.useUpperName() + "\r\n" + "      { \r\n"
        + "          get { return this." + field.useLowerName() + ";}\r\n" + set + "      }";
    return ret;
  }
  static String getDataType(Field field, StringBuffer maker, StringBuffer temp)
  {
    String result;
    switch (field.type)
    {
    case Field.ANSICHAR:
    case Field.CHAR:
    case Field.USERSTAMP:
      result = "string";
      break;
    case Field.MONEY:
      result = "decimal";
      break;
    case Field.BLOB:
      result = "JPBlob";
      maker.append(" = new JPBlob()");
      break;
    case Field.TLOB:
      result = "string";
      break;
    case Field.XML:
      result = "string";
      break;
    case Field.UID:
      result = "Guid";
      break;
    case Field.DATE:
    case Field.DATETIME:
    case Field.TIME:
    case Field.TIMESTAMP:
      result = "DateTime";
      temp.append(", IsVersion=true");
      break;
    case Field.BOOLEAN:
      result = "bool";
      break;
    case Field.BYTE:
      result = "byte";
      break;
    case Field.STATUS:
      result = "short";
      break;
    case Field.DOUBLE:
    case Field.FLOAT:
      if (field.precision > 15)
        result = "decimal";
      else
        result = "double";
      break;
    case Field.INT:
    case Field.SEQUENCE:
    case Field.IDENTITY:
      result = "int";
      break;
    case Field.LONG:
    case Field.BIGSEQUENCE:
    case Field.BIGIDENTITY:
      result = "long";
      break;
    case Field.SHORT:
      result = "short";
      break;
    case Field.DYNAMIC:
      result = "string";
      break;
    default:
      result = "whoknows";
      break;
    }
    return result;
  }

  static String returnNullableDataType(Field field, StringBuffer maker, StringBuffer temp)
  {
    String datatype = getDataType(field, maker, temp);
    if (getNullableType(field) && field.isNull)
      datatype = datatype + "?";
    return datatype;
  }

  static boolean getNullableType(Field field)
  {
    boolean nullableType;
    switch (field.type)
    {

    case Field.MONEY:
    case Field.DATE:
    case Field.DATETIME:
    case Field.TIME:
    case Field.TIMESTAMP:
    case Field.BOOLEAN:
    case Field.BYTE:
    case Field.STATUS:
    case Field.DOUBLE:
    case Field.FLOAT:
    case Field.INT:
    case Field.SEQUENCE:
    case Field.IDENTITY:
    case Field.LONG:
    case Field.BIGSEQUENCE:
    case Field.BIGIDENTITY:
    case Field.SHORT:
      nullableType = true;
      break;
    default:
      nullableType = false;
      break;
    }
    return nullableType;
  }

  static String fieldCastNo(Field field)
  {
    String result;
    switch (field.type)
    {
    case Field.ANSICHAR:
    case Field.CHAR:
    case Field.USERSTAMP:
    case Field.DYNAMIC:
      result = "string";
      break;
    case Field.MONEY:
      result = "decimal";
      break;
    case Field.BLOB:
      result = "JPBlob";
      break;
    case Field.TLOB:
      result = "string";
      break;
    case Field.XML:
      result = "string";
      break;
    case Field.UID:
      result = "Guid";
      break;
    case Field.DATE:
    case Field.DATETIME:
    case Field.TIME:
    case Field.TIMESTAMP:
      result = "DateTime";
      break;
    case Field.BOOLEAN:
      result = "bool";
      break;
    case Field.BYTE:
      result = "byte";
      break;
    case Field.STATUS:
      result = "short";
      break;
    case Field.DOUBLE:
    case Field.FLOAT:
      if (field.precision > 15)
        result = "decimal";
      else
        result = "double";
      break;
    case Field.INT:
    case Field.SEQUENCE:
    case Field.IDENTITY:
      result = "int";
      break;
    case Field.LONG:
    case Field.BIGSEQUENCE:
    case Field.BIGIDENTITY:
      result = "long";
      break;
    case Field.SHORT:
      result = "short";
      break;
    default:
      result = "whoknows";
      break;
    }
    return result;
  }
  static String fieldCast(Field field)
  {
    return "(" + fieldCastNo(field) + ")";
  }
  /**
   * Translates field type to SQLServer SQL column types
   */
  static String varType(Field field)
  {
    switch (field.type)
    {
    case Field.BOOLEAN:
      return "bit";
    case Field.BYTE:
      return "tinyint";
    case Field.SHORT:
      return "smallint";
    case Field.INT:
    case Field.SEQUENCE:
    case Field.IDENTITY:
      return "integer";
    case Field.LONG:
    case Field.BIGSEQUENCE:
    case Field.BIGIDENTITY:
      return "longint";
    case Field.CHAR:
      if (field.length > 8000)
      {
        return field.name + " varchar(MAX)";
      }
      return field.name + " varchar(" + String.valueOf(field.length) + ")";
    case Field.ANSICHAR:
      return "char(" + String.valueOf(field.length) + ")";
    case Field.DATE:
      return "datetime";
    case Field.DATETIME:
      return "datetime";
    case Field.TIME:
      return "datetime";
    case Field.TIMESTAMP:
      return "datetime";
    case Field.FLOAT:
    case Field.DOUBLE:
      if (field.precision > 15)
        return "decimal";
      return "float";
    case Field.BLOB:
      return "image";
    case Field.TLOB:
      return "text";
    case Field.XML:
      return "xml";
    case Field.UID:
      return "uniqueidentifier";
    case Field.MONEY:
      return "money";
    case Field.USERSTAMP:
      return "varchar(50)";
    default:
      break;
    }
    return "unknown";
  }
}

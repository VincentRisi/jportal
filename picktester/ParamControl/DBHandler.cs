using System;
using System.Data;
using System.Text;
using System.Windows.Forms;
using bbd.jportal;

namespace bbd.ParamControl
{
  public struct TPCApplication
  {
    public string name;
    public string descr;
    public string version;
    public byte[] server;
    public byte[] user;
    public byte[] password;
    public string registry;
    public int noTables;
    public int noRelations;
    public int noFields;
    public int noEnums;
    public int noKeyFields;
    public int noOrderFields;
    public int noShowFields;
    public int noBreakFields;
    public int noLinks;
    public int noLinkPairs;
    public string validateAll;
    public string validateOther;
    public string validateInit;
    public string Formatted()
    {
      StringBuilder result = new StringBuilder();
      result.Append("Application");
      result.AppendFormat(" name:{0}",          name);
      result.AppendFormat(" descr:{0}",         descr);
      result.AppendFormat(" version:{0}",       version);
      result.AppendFormat(" registry:{0} ",     registry);
      result.AppendFormat(" noTables:{0}",      noTables);
      result.AppendFormat(" noRelations:{0}",   noRelations);
      result.AppendFormat(" noFields:{0}",      noFields);
      result.AppendFormat(" noEnums:{0}",       noEnums);
      result.AppendFormat(" noKeyFields:{0}",   noKeyFields);
      result.AppendFormat(" noOrderFields:{0}", noOrderFields);
      result.AppendFormat(" noShowFields:{0}",  noShowFields);
      result.AppendFormat(" noBreakFields:{0}", noBreakFields);
      result.AppendFormat(" noLinks:{0}",       noLinks);
      result.AppendFormat(" noLinkPairs:{0}",   noLinkPairs);
      result.AppendFormat(" validateAll:{0}",   validateAll);
      result.AppendFormat(" validateOther:{0}", validateOther);
      return result.ToString();
    }
  }
  public struct TPCTable
  {
    public int index;
    public string name;
    public string descr;
    public int noFields;
    public int offsetFields;
    public int noLinks;
    public int offsetLinks;
    public int noKeyFields;
    public int offsetKeyFields;
    public int noOrderFields;
    public int offsetOrderFields;
    public int noShowFields;
    public int offsetShowFields;
    public int noBreakFields;
    public int offsetBreakFields;
    public int viewOnly;
    public string validate;
    public string Formatted(int i = 0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("Table[{0}]", i);
      result.AppendFormat(" name:{0}", name);
      result.AppendFormat(" descr:{0}", descr);
      result.AppendFormat(" noFields:{0}", noFields);
      result.AppendFormat(" offsetFields:{0}", offsetFields);
      result.AppendFormat(" noLinks:{0}", noLinks);
      result.AppendFormat(" offsetLinks:{0}", offsetLinks);
      result.AppendFormat(" noKeyFields:{0}", noKeyFields);
      result.AppendFormat(" offsetKeyFields:{0}", offsetKeyFields);
      result.AppendFormat(" noOrderFields:{0}", noOrderFields);
      result.AppendFormat(" offsetOrderFields:{0}", offsetOrderFields);
      result.AppendFormat(" noShowFields:{0}", noShowFields);
      result.AppendFormat(" offsetShowFields:{0}", offsetShowFields);
      result.AppendFormat(" noBreakFields:{0}", noBreakFields);
      result.AppendFormat(" offsetBreakFields:{0}", offsetBreakFields);
      result.AppendFormat(" viewOnly:{0}", viewOnly);
      result.AppendFormat(" validate:{0}", validate);
      return result.ToString();
    }
  }
  public struct TPCIndexField
  {
    public int index;
    public string Formatted(string n, int i = 0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("{0}[{1}] index:{2}", n, i, index);
      return result.ToString();
    }
  }
  public struct TPCLink
  {
    public int tableNo;
    public int noLinkPairs;
    public int offsetLinkPairs;
    public string Formatted(int i = 0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("Link[{0}]", i);
      result.AppendFormat(" tableNo:{0}", tableNo);
      result.AppendFormat(" noLinkPairs:{0}", noLinkPairs);
      result.AppendFormat(" offsetLinkPairs:{0}", offsetLinkPairs);
      return result.ToString();
    }
  }
  public struct TPCLinkPair
  {
    public int fromNo;
    public int toNo;
    public string Formatted(int i = 0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("LinkPair[{0}]", i);
      result.AppendFormat(" fromNo:{0}", fromNo);
      result.AppendFormat(" toNo:{0}", toNo);
      return result.ToString();
    }
  }
  public struct TPCRelation
  {
    public int index;
    public string name;
    public string descr;
    public int fromTable;
    public int noFromFields;
    public int toTable;
    public int noToFields;
    public int offsetFields;
    public int offsetFromLink;
    public int offsetToLink;
    public string validate;
    public string Formatted(int i = 0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("Relation[{0}]", i);
      result.AppendFormat(" name:{0}", name);
      result.AppendFormat(" descr:{0}", descr);
      result.AppendFormat(" fromTable:{0}", fromTable);
      result.AppendFormat(" noFromFields:{0}", noFromFields);
      result.AppendFormat(" toTable:{0}", toTable);
      result.AppendFormat(" noToFields:{0}", noToFields);
      result.AppendFormat(" offsetFields:{0}", offsetFields);
      result.AppendFormat(" offsetFromLink:{0}", offsetFromLink);
      result.AppendFormat(" offsetToLink:{0}", offsetToLink);
      result.AppendFormat(" validate:{0}", validate);
      return result.ToString();
    }
  }
  public struct TPCEnum
  {
    public string name;
    public int value;
    public string Formatted(int i=0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("Enum[{0}]", i);
      result.AppendFormat(" name:{0}", name);
      result.AppendFormat(" value:{0}", value);
      return result.ToString();
    }
  }
  public struct TPCField
  {
    public string name;
    public int type;
    public int length;
    public int precision;
    public int scale;
    public int offset;
    public int paddedLength;
    public int uppercase;
    public int isNull;
    public int noEnums;
    public int offsetEnums;
    public string Formatted(int i=0)
    {
      StringBuilder result = new StringBuilder();
      result.AppendFormat("Field[{0}]", i);
      result.AppendFormat(" name:{0}", name);
      result.AppendFormat(" type:{0}", type);
      result.AppendFormat(" length:{0}", length);
      result.AppendFormat(" precision:{0}", precision);
      result.AppendFormat(" scale:{0}", scale);
      result.AppendFormat(" offset:{0}", offset);
      result.AppendFormat(" paddedLength:{0}", paddedLength);
      result.AppendFormat(" uppercase:{0}", uppercase);
      result.AppendFormat(" isNull:{0}", isNull);
      result.AppendFormat(" noEnums:{0}", noEnums);
      result.AppendFormat(" offsetEnums:{0}", offsetEnums);
      return result.ToString();
    }
  }
  public struct TPCFieldInfo
  {
    public string name;
    public Type type;
  }
  public class DBHandler
  {
    public static string LogError { set { Logger.Error = value; } }
    public static string LogWarn { set { Logger.Warn = value; } }
    public static string LogInfo { set { Logger.Info = value; } }
    public static string LogDebug { set { Logger.Debug = value; } }
    public static bool ShowSQL { get { return showSQL; } set { showSQL = value; } } private static bool showSQL;  
    public const int PC_BOOLEAN = 4;
    public const int PC_BYTE = 8;
    public const int PC_CHAR = 12;
    public const int PC_DATE = 16;
    public const int PC_DATETIME = 20;
    public const int PC_DOUBLE = 24;
    public const int PC_INT = 28;
    public const int PC_LONG = 32;
    public const int PC_SEQUENCE = 36;
    public const int PC_SHORT = 40;
    public const int PC_TIME = 44;
    public const int PC_TIMESTAMP = 48;
    public const int PC_USERSTAMP = 52;
    public int rowSize;
    public int noRows;
    public int noFields;
    public int maxRows;
    public string Lookup;
    public string Limit;
    public string UsId;
    public string TmStamp;
    public TPCField[] fields;
    public JConnect connect;
    public JCursor cursor;
    public DataTableGrid dataTableGrid;
    public DBHandler(JConnect connect)
    {
      this.connect = connect;
      cursor = new JCursor(connect);
      fields = null;
      rowSize = 0;
      noRows = 32;
      noFields = 0;
      maxRows = 0;
      Limit = "";
      Lookup = "";
      UsId = "UsId";
      TmStamp = "TmStamp";
    }
    public void GetDistinctList(string tableName, TPCField field)
    {
      string query = "SELECT DISTINCT " + field.name + " FROM " + tableName + " ORDER BY " + field.name;
      cursor.Format(query, 0);
      if (ShowSQL)
        LogDebug = query;
      cursor.Run();
    }
    public int GetCount(string tableName)
    {
      string query = "SELECT COUNT(*) FROM " + tableName;
      cursor.Format(query, 0);
      if (ShowSQL)
        LogDebug = query;
      cursor.Run();
      DataTable table = new DataTable();
      table.Load(cursor.Reader);
#if do_it_with_oracle
      decimal d = (decimal)table.Rows[0][0];
#elif do_it_with_lite3
      long d = (long)table.Rows[0][0];
#elif do_it_with_mssql
      int d = (int)table.Rows[0][0];
#elif do_it_with_mysql
      int d = (int)table.Rows[0][0];
#elif do_it_with_postgres
      long d = (long)table.Rows[0][0];
#endif
      return int.Parse(d.ToString());
    }
    public void GetTable(string tableName, TPCField[] allFields, int offsetFields, int noFields, TPCIndexField[] orderFields, int offsetOrderFields, int noOrderFields)
    {
      string query = "SELECT";
#if do_it_with_mssql
      if (Limit.Length > 0)
        query += " " + Limit;
      else
        query += " TOP 1000";
#endif
      string comma = " ";
      fields = new TPCField[noFields];
      for (int i=0; i<noFields; i++) 
      {
        fields[i] = allFields[offsetFields + i];
        query += comma + fields[i].name;
        comma = ", ";
      }
      dataTableGrid = new DataTableGrid(tableName);
      dataTableGrid.MakeTableColumns(fields);
      query += comma + UsId + ", " + TmStamp + " FROM " + tableName;
      if (Lookup.Length > 0)
        query += " " + Lookup;
#if do_it_with_oracle
      if (Lookup.Length == 0)
        query += " WHERE ROWNUM <= 1000";
#endif
      if (noOrderFields > 0)
      {
        string orderBy = " ORDER BY";
        comma = " ";
        for (int i = 0; i < noOrderFields; i++)
        {
          int no = orderFields[offsetOrderFields + i].index;
          TPCField field = fields[no];
          orderBy += comma + field.name;
          comma = ", ";
        }
        query += orderBy;
      }
#if do_it_with_lite3
      if (Limit.Length > 0)
        query += " " + Limit;
      else
        query += " LIMIT 1000";
#endif
      cursor.Format(query, 0);
      if (ShowSQL)
        LogDebug = query;
      cursor.Run();
    }
    public DataGridView Grid
    {
      set 
      { 
        if (cursor.HasReader())
        {
          dataTableGrid.Grid = value;
          dataTableGrid.Table.Load(cursor.Reader);
        }
      }
    }
    public DataTable GetDataTable()
    {
      if (cursor.HasReader())
      {
        DataTable table = new DataTable();
        table.Load(cursor.Reader);
        return table;
      }
      return null;
    }
    public void PopulateCombo(ComboBox combo, TPCIndexField[] keys, int offsetkeys, int noKeys)
    {
      combo.Items.Clear();
      if (cursor.HasReader())
      {
        dataTableGrid.Table.Load(cursor.Reader);
        System.Data.DataRowCollection rows = dataTableGrid.Table.Rows;
        for (int r = 0; r < rows.Count; r++)
        {
          string text = "";
          string w = "";
          for (int i = 0; i < noKeys; i++)
          {
            int c = keys[i + offsetkeys].index;
            text += w + rows[r][c];
            w = "|";
          }
          combo.Items.Add(text);
        }
      }
    }
    public void PopulateCombo(ComboBox combo)
    {
      combo.Items.Clear();
      if (cursor.HasReader())
      {
        DataTable table = new DataTable();
        table.Load(cursor.Reader);
        System.Data.DataRowCollection rows = table.Rows;
        for (int r = 0; r < rows.Count; r++)
          combo.Items.Add(rows[r][0]);
      }
    }
    public void Execute(string action)
    {
      cursor.Format(action, 0);
      if (ShowSQL)
        LogDebug = action;
      cursor.Exec();
    }
    public string Query(string query)
    {
      string result = "[";
      cursor.Format(query, 0);
      if (ShowSQL)
        LogDebug = query;
      cursor.Run();
      if (cursor.HasReader())
      {
        DataTable table = new DataTable();
        IDataReader reader = cursor.Reader; 
        table.Load(reader);
        System.Data.DataRowCollection rows = table.Rows;
        for (int r = 0; r < rows.Count; r++)
        {
          if (r > 0) result += ", ";
          result += "{";
          for (int c = 0; c < table.Columns.Count; c++)
          {
            if (c > 0) result += ", ";
            result += string.Format("'{0}':'{1}'", table.Columns[c].ColumnName, rows[r][c]);
          }
          result += "}";
        }
      }
      return result + "]";
    }
  }
}
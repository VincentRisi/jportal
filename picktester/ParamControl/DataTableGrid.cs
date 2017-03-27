using System;
using System.Data;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace bbd.ParamControl
{
  public class DataTableGrid
  {
    DataTable table;
    DataGridView grid;
    public DataTable Table
    {
      get 
      {
        return table;
      }
      set
      {
        table = value;
        if (grid != null)
          grid.DataSource = table;
      }
    }
    public DataView View
    {
      get
      {
        if (table != null) 
          return table.DefaultView;
        return null;
      }
    }
    public DataGridView Grid
    {
      get 
      {
        return grid;
      }
      set 
      {
        grid = value;
        if (table != null)
          grid.DataSource = table;
      }
    }
    public DataTableGrid(string name)
    {
      table = new DataTable(name);
    }
    public DataTableGrid(string name, DataGridView grid) : this(name)
    {
      Grid = grid;
    }
    public void MakeTableColumns(TPCField[] fields)
    {
      table.Columns.Clear();
      foreach (TPCField field in fields)
        table.Columns.Add(field.name, getType(field.type));
    }
    private Type getType(int type)
    {
      switch (type)
      {
        case DBHandler.PC_BOOLEAN: 
          return typeof(byte);
        case DBHandler.PC_BYTE: 
          return typeof(byte);
        case DBHandler.PC_CHAR: 
          return typeof(string);
        case DBHandler.PC_DATE: 
          return typeof(string);
        case DBHandler.PC_DATETIME: 
          return typeof(string);
        case DBHandler.PC_DOUBLE: 
          return typeof(double);
        case DBHandler.PC_INT: 
          return typeof(int);
        case DBHandler.PC_LONG: 
          return typeof(long);
        case DBHandler.PC_SEQUENCE: 
          return typeof(int);
        case DBHandler.PC_SHORT: 
          return typeof(short); ;
        case DBHandler.PC_TIME: 
          return typeof(string);
        case DBHandler.PC_TIMESTAMP: 
          return typeof(string);
        case DBHandler.PC_USERSTAMP: 
          return typeof(string);
      }
      return typeof(int);
    }
  }
}

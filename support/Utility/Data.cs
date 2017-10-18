/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;
using System.Data;
using System.IO;
using System.Reflection;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace bbd.utility
{
  /// <summary>
  /// A class is to assist in the conversion of arrays of class records to a standard 
  /// DataTable and to display them via a standard DataGrid. It will also ensure 
  /// that the Grid columns are resized according to data content. It will also
  /// cater for displaying existing DataTable objects as well as the ability
  /// to us another DataGrid.
  /// </summary>
  public class Data
  {
    DataTable table;
    DataGridTableStyle tableStyle;
    DataGrid grid;
    /// <summary>
    /// Property to get or set the DataTable object being used. The set will
    /// reoganize the grid display.
    /// </summary>
    public DataTable Table
    {
      get 
      {
        return table;
      }
      set
      {
        table = value;
        tableStyle.MappingName = table.TableName;
        if (grid != null)
        {
          grid.DataSource = table;
          ResizeGrid();
        }
      }
    }
    public DataGrid Grid
    {
      get 
      {
        return grid;
      }
      set 
      {
        grid = value;
        grid.TableStyles.Clear();
        tableStyle = new DataGridTableStyle();
        tableStyle.AllowSorting = grid.AllowSorting;
        tableStyle.AlternatingBackColor = grid.AlternatingBackColor;
        tableStyle.BackColor = grid.BackColor;
        tableStyle.ColumnHeadersVisible = grid.ColumnHeadersVisible;
        tableStyle.ForeColor = grid.ForeColor;
        tableStyle.GridLineColor = grid.GridLineColor;
        tableStyle.GridLineStyle = grid.GridLineStyle;
        tableStyle.HeaderBackColor = grid.HeaderBackColor;
        tableStyle.HeaderFont = grid.HeaderFont;
        tableStyle.HeaderForeColor = grid.HeaderForeColor;
        tableStyle.LinkColor = grid.LinkColor;
        tableStyle.ReadOnly = grid.ReadOnly;
        tableStyle.RowHeadersVisible = grid.RowHeadersVisible;
        tableStyle.RowHeaderWidth = grid.RowHeaderWidth;
        tableStyle.SelectionBackColor = grid.SelectionBackColor;
        tableStyle.SelectionForeColor = grid.SelectionForeColor;
        if (table != null)
        {
          grid.DataSource = table;
          tableStyle.MappingName = table.TableName;
        }
      }
    }
    public Data(string name)
    {
      table = new DataTable(name);
    }
    public Data(string name, DataGrid grid) : this(name)
    {
      Grid = grid;
    }
    public int Width()
    {
      int result = 0;
      for(int i = 0; i < tableStyle.GridColumnStyles.Count; i++)
      {
        DataGridColumnStyle colStyle = tableStyle.GridColumnStyles[i];
        result += colStyle.Width;
      }
      return result;
    }
    public int Make(Array array)
    {
      foreach (object x in array)
        MakeRec(x);
      grid.TableStyles.Add(tableStyle);
      return Width();
    }
    private void MakeTableColumns(FieldInfo[] fieldList)
    {
      table.Columns.Clear();
      foreach (FieldInfo field in fieldList)
        table.Columns.Add(field.Name, field.FieldType);
    }
    private void MakeGridColumnStyles(FieldInfo[] fieldList)
    {
      DataGridColumnStyle columnStyle;
      tableStyle.GridColumnStyles.Clear();
      foreach (FieldInfo field in fieldList)
      {
        columnStyle = new DataGridTextBoxColumn();
        columnStyle.MappingName = field.Name;
        columnStyle.HeaderText = field.Name;
        tableStyle.GridColumnStyles.Add(columnStyle);
      }
    }
    private void PopulateTable(object of, FieldInfo[] fieldList)
    {
      DataRow row = table.NewRow();
      table.Rows.Add(row);
      int i=0;
      foreach (FieldInfo field in fieldList)
        row[i++] = field.GetValue(of);
    }
    private void ResizeGrid()
    {
      using (Graphics g = Graphics.FromHwnd(grid.Handle))
      {
        float width;
        foreach (DataGridColumnStyle style in tableStyle.GridColumnStyles)
        {
          width = g.MeasureString(style.HeaderText, grid.Font).Width;
          style.Width = (int)(width + grid.Font.Size);
        }
        foreach (DataRow row in table.Rows)
        {
          for(int i = 0; i < row.ItemArray.Length; i++)
          {
            width = g.MeasureString(row[i].ToString(), grid.Font).Width;
            DataGridColumnStyle style = tableStyle.GridColumnStyles[i];
            if (width > style.Width)
              style.Width = (int)(width + grid.Font.Size);
          }
        }
      }
    }
    private void MakeRec(object of)
    {
      Type type = of.GetType();
      if (type.IsClass)
      {
        FieldInfo[] fieldInfo = type.GetFields( 
          BindingFlags.Instance | 
          BindingFlags.Public 
          );
        if (table.Columns.Count == 0)
        {
          MakeTableColumns(fieldInfo);
          MakeGridColumnStyles(fieldInfo);
        }
        PopulateTable(of, fieldInfo);
        ResizeGrid();
      }
    }
  }
}

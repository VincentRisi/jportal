using System;
using System.Collections.Generic;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;
using System.IO;
using System.Configuration;
using bbd.jportal;
#if do_it_with_oracle
using Oracle.ManagedDataAccess.Client;
#elif do_it_with_mssql
using System.Data.SqlClient;
#elif do_it_with_lite3 
using System.Data.SQLite;
#elif do_it_with_mysql 
using MySql.Data.MySqlClient;
#elif do_it_with_postgres 
using Devart.Data.PostgreSql;
#endif
using IronPython.Hosting;
using Microsoft.Scripting;
using Microsoft.Scripting.Hosting;

namespace bbd.ParamControl
{
  public partial class ParamControlForm : Form
  {
    private string[] args;
    private string ParamBinName = ConfigurationManager.AppSettings["binFileName"];
    private string ParamLogName = ConfigurationManager.AppSettings["logFileName"];
    private string binFileName;
    private string registryName;
    private string logFileName;
    private string xuser, ypassword, zserver;
    private Registry registry;
    private static JConnect connect;
    private bool allowUpdates;
    private string userName = Environment.UserName;
    private TPCTable pcTable;
    private TPCRelation pcRelation;
    private TPCApplication pcApplication;
    private TPCTable[] pcTables;
    private TPCRelation[] pcRelations;
    private TPCField[] pcFields;
    private TPCEnum[] pcEnums;
    private TPCIndexField[] pcKeyFields;
    private TPCIndexField[] pcOrderFields;
    private TPCIndexField[] pcBreakFields;
    private TPCIndexField[] pcShowFields;
    private TPCLink[] pcLinks;
    private TPCLinkPair[] pcLinkPairs;
    private static ParamControlForm thisForm;
    private string[] getArgs(string[] args)
    {
      List<string> list = new List<string>();
      char state = ' ';
      foreach (string arg in args)
      {
        switch (state)
        {
          case ' ':
            if (arg == "-b") state = 'b';
            else if (arg == "-l") state = 'l';
            else if (arg == "-x") state = 'x';
            else if (arg == "-y") state = 'y';
            else if (arg == "-z") state = 'z';
            else if (arg[0] == '-') state = '?';
            else list.Add(arg);
            break;
          case 'b':
            binFileName = arg;
            state = ' ';
            break;
          case 'l':
            logFileName = arg;
            state = ' ';
            break;
          case 'x':
            xuser = arg;
            state = ' ';
            break;
          case 'y':
            ypassword = arg;
            state = ' ';
            break;
          case 'z':
            zserver = arg;
            state = ' ';
            break;
          case '?':
            System.Console.WriteLine("Valid command line arguments:");
            System.Console.WriteLine(" -b <binFileName>      : " + binFileName);
            System.Console.WriteLine(" -l <logFileName>      : " + logFileName);
            System.Console.WriteLine(" -x <user>             : " + xuser);
            System.Console.WriteLine(" -y <password>         : ");
            System.Console.WriteLine(" -z <server>           : " + zserver);
            state = ' ';
            break;
        }
      }
      return list.ToArray<string>();
    }
    public ParamControlForm(string[] args)
    {
      InitializeComponent();
      binFileName = ParamBinName;
      logFileName = ParamLogName;
      thisForm = this;
      this.args = getArgs(args);
    }
    public static string LogError { set { Logger.Error = value; } }
    public static string LogWarn  { set { Logger.Warn = value;  } }
    public static string LogInfo  { set { Logger.Info = value;  } }
    public static string LogDebug { set { Logger.Debug = value; } }
    public bool ShowSql { get { return showSQL.Checked; } }

    public object Resource1 { get; private set; }

    private ScriptEngine pyEngine = null;
    private ScriptScope pyScope = null;
    private void applicationLoad(object sender, EventArgs e)
    {
      using (AutoCursor.AppStart)
      {
        try
        {
          Logger.LogMemo = logMemo;
          Logger.LogFile = new StreamWriter(logFileName);
          LogInfo = string.Format("Log File: {0} opened", logFileName);
          valueCombo.Top = lookupCombo.Top;
          valueLabel.Top = lookupLabel.Top;
          lookupCombo.Top = likeEdit.Top;
          lookupLabel.Top = likeLabel.Top;
          doValueButton.Top = doButton.Top;
          BinTables.LoadParameters(binFileName);
          pcApplication = BinTables.PCApplication;
          pcTables = BinTables.PCTables;
          pcRelations = BinTables.PCRelations;
          pcFields = BinTables.PCFields;
          pcEnums = BinTables.PCEnums;
          pcKeyFields = BinTables.PCKeyFields;
          pcOrderFields = BinTables.PCOrderFields;
          pcBreakFields = BinTables.PCBreakFields;
          pcShowFields = BinTables.PCShowFields;
          pcLinks = BinTables.PCLinks;
          pcLinkPairs = BinTables.PCLinkPairs;
          registryName = pcApplication.registry;
          string ironPythonSetup = Properties.Resources.IronPythonSetup;
          string pythonCode = "";
          if (BinTables.HasValidation() == true)
          {
            pyEngine = Python.CreateEngine();
            pyScope = pyEngine.CreateScope();
            pythonCode = string.Format("{0}\r\n{1}\r\nset_userName('{2}')\r\n", ironPythonSetup ,pcApplication.validateInit, userName);
            ScriptSource __init__ = pyEngine.CreateScriptSourceFromString(pythonCode, SourceCodeKind.Statements);
            __init__.Execute(pyScope);
          }
          mainTab.SelectTab(0);
          registry = new Registry(String.Format("Software\\{0}", registryName));
          maxDropdownEdit.Value = Registry.IntOf(registry["maxDropDown", "256"]);
          showBinfile.Checked = registry["showBinfile", "False"] == "True";
          showSQL.Checked = registry["showSQL", "False"] == "True";
          showSetup.Checked = registry["showSetup", "False"] == "True";
          setGeneralOptions();
          if (showBinfile.Checked == true)
            printBinFile();
          LogDebug = pythonCode;
          Text = pcApplication.descr;
          appNameStripLabel.Text = pcApplication.name;
          appVersionStripLabel.Text = pcApplication.version;
          if (pcApplication.noRelations == 0)
            mainTab.TabPages.Remove(relationsPage);
          else
          {
            for (int i = 0; i < pcApplication.noRelations; i++)
            {
              string[] values = new string[1];
              string[] descr = new string[1];
              values[0] = pcRelations[i].name;
              descr[0] = pcRelations[i].descr;
              listOfRelations.Items.Add(new ListItem(values, descr, i));
            }
          }
          for (int i = 0; i < pcApplication.noTables; i++)
          {
            string[] values = new string[1];
            string[] descr = new string[1];
            values[0] = pcTables[i].name;
            descr[0] = pcTables[i].descr;
            listOfTables.Items.Add(new ListItem(values, descr, i));
          }
          string server;
#if do_it_with_mssql
          server = zserver == null ? decrypt(pcApplication.server) : zserver;
          connectionStripLabel.Text = dropPassword(server, "Password=");
          connect = new JConnect(new SqlConnection(server));
          connect.TypeOfVendor = VendorType.SqlServer;
#elif do_it_with_mysql
          server = zserver == null ? decrypt(pcApplication.server) : zserver;
          connectionStripLabel.Text = dropPassword(server, "Pwd=");
          connect = new JConnect(new MySqlConnection(server));
          connect.TypeOfVendor = VendorType.MySql;
#elif do_it_with_lite3
          server = zserver == null ? decrypt(pcApplication.server) : zserver;
          connectionStripLabel.Text = server;
          connect = new JConnect(new SQLiteConnection(server));
          connect.TypeOfVendor = VendorType.Lite3;
#elif do_it_with_postgres
          server = zserver == null ? decrypt(pcApplication.server) : zserver;
          connectionStripLabel.Text = dropPassword(server, "Password=");
          connect = new JConnect(new Devart.Data.PostgreSql.PgSqlConnection(server));
          connect.TypeOfVendor = VendorType.PostgreSQL;
#elif do_it_with_oracle
          server = zserver == null ? decrypt(pcApplication.server) : zserver;
          connectionStripLabel.Text = dropPassword(server, "Password=");
          connect = new JConnect(new OracleConnection(server));
          connect.TypeOfVendor = VendorType.Oracle;
#endif
          connect.Open();
        }
        catch (Exception exception)
        {
          showException(exception);
          Close();
        }
      }
    }
    private string dropPassword(string server, string v)
    {
      int n = server.ToUpper().IndexOf(v.ToUpper());
      if (n >= 0)
      {
        int t = server.Substring(n).IndexOf(";");
        if (t > 0)
        {
          string result = server.Substring(0, n) + server.Substring(n + t + 1);
          return result;
        }
      }
      return server;
    }
    private string decrypt(byte[] p)
    {
      int[] APPLICATION = { (int)'4', (int)'P', (int)'P', (int)'7', (int)'1', (int)'C', (int)'4', (int)'T', (int)'1', (int)'0', (int)'N' };
      string result = "";
      int al = APPLICATION.Length;
      for (int i = 0; i < p.Length; i++)
      {
        byte ch = (byte)((int)p[i] ^ APPLICATION[i % al] & 0xFF);
        result += (char)ch;
      }
      return result.Trim('\0');
    }
    public static bool ShowMessage(string message, string title, int oftype)
    {
      MessageBoxButtons value;
      switch (oftype)
      {
        case 0: value = MessageBoxButtons.OK; break;
        case 1: value = MessageBoxButtons.OKCancel; break;
        case 2: value = MessageBoxButtons.AbortRetryIgnore; break;
        case 3: value = MessageBoxButtons.YesNoCancel; break;
        case 4: value = MessageBoxButtons.YesNo; break;
        case 5: value = MessageBoxButtons.RetryCancel; break;
        default: value = MessageBoxButtons.OK; break;
      }
      DialogResult dr = MessageBox.Show(message, title, value);
      return dr == DialogResult.OK || dr == DialogResult.Yes || dr == DialogResult.Ignore;
    }
    public static void SQLAction(string code)
    {
      if (connect == null) return;
      DBHandler db = new DBHandler(connect);
      db.Execute(code);
    }
    public static string SQLQuery(string code)
    {
      if (connect == null) return "";
      DBHandler db = new DBHandler(connect);
      return db.Query(code);
    }
    private static void showException(Exception exception)
    {
      string message = "", plus = "";
      thisForm.lastErrorStripLabel.Text = exception.Message;
      for (Exception x = exception; x != null; x = x.InnerException)
      {
        LogError = x.Message;
        message += plus + x.Message;
        plus = "\r\n";
      }
      MessageBox.Show(message + "\r\n" + exception.StackTrace, "Exception");
    }
    private void printBinFile()
    {
      LogDebug = pcApplication.Formatted();
      for (int i = 0; i < pcApplication.noTables; i++)
        LogDebug = pcTables[i].Formatted(i);
      for (int i = 0; i < pcApplication.noRelations; i++)
        LogDebug = pcRelations[i].Formatted(i);
      for (int i = 0; i < pcApplication.noFields; i++)
        LogDebug = pcFields[i].Formatted(i);
      for (int i = 0; i < pcApplication.noEnums; i++)
        LogDebug = pcEnums[i].Formatted(i);
      for (int i = 0; i < pcApplication.noKeyFields; i++)
        LogDebug = pcKeyFields[i].Formatted("Key", i);
      for (int i = 0; i < pcApplication.noOrderFields; i++)
        LogDebug = pcOrderFields[i].Formatted("Order", i);
      for (int i = 0; i < pcApplication.noShowFields; i++)
        LogDebug = pcShowFields[i].Formatted("Show", i);
      for (int i = 0; i < pcApplication.noBreakFields; i++)
        LogDebug = pcBreakFields[i].Formatted("Break", i);
      for (int i = 0; i < pcApplication.noLinks; i++)
        LogDebug = pcLinks[i].Formatted(i);
      for (int i = 0; i < pcApplication.noLinkPairs; i++)
        LogDebug = pcLinkPairs[i].Formatted(i);
    }
    private void setUpdateActions()
    {
      addButton.Enabled = allowUpdates;
      changeButton.Enabled = allowUpdates;
      deleteButton.Enabled = allowUpdates;
    }
    private void setDB(DBHandler db, string tableName)
    {
      dbLookupLabel.Text = db.Lookup = registry["Lookup", tableName, ""];
      db.UsId = registry["UsId", tableName, "UsId"];
      db.TmStamp = registry["TmStamp", tableName, "TmStamp"];
    }
    private void listOfTablesDoubleClick(object sender, EventArgs e)
    {
      ListItem item = listOfTables.SelectedItem as ListItem;
      if (item == null) return;
      pcTable = pcTables[item.Index];
      tableNameStripLabel.Text = item.Values[0];
      tableDescrStripLabel.Text = item.Descr[0];
      showTableGrid();
    }
    private void showTableGrid()
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          lastErrorStripLabel.Text = "";
          maxRowsEdit.Value = Registry.IntOf(registry["MaxRows", pcTable.name, "0"]);
          allowUpdates = pcTable.viewOnly == 0;
          setUpdateActions();
          DBHandler db = new DBHandler(connect);
          setDB(db, pcTable.name);
          db.GetTable(pcTable.name,
            pcFields, pcTable.offsetFields, pcTable.noFields,
            pcOrderFields, pcTable.offsetOrderFields, pcTable.noOrderFields);
          tablesDataGrid.Columns.Clear();
          db.Grid = tablesDataGrid;
          clearAdd.Checked = registry["ClearAdd", pcTable.name, "False"] == "True" ? true : false;
          if (pcTable.noOrderFields > 1)
          {
            lookupCombo.Visible = true;
            lookupLabel.Visible = true;
            valueCombo.Visible = true;
            valueLabel.Visible = true;
            doValueButton.Visible = true;
            likeLabel.Visible = false;
            likeEdit.Visible = false;
            doButton.Visible = false;
            lookupCombo.Items.Clear();
            valueCombo.Items.Clear();
            int o = pcTable.offsetFields;
            for (int j = 0; j < pcTable.noOrderFields; j++)
            {
              int k = j + pcTable.offsetOrderFields;
              int f = pcOrderFields[k].index + o;
              lookupCombo.Items.Add(pcFields[f].name);
            }
            lookupCombo.Text = registry["LookupKey", pcTable.name, ""];
            valueCombo.Text = registry["LookupValue", pcTable.name, ""];
          }
          else
          {
            lookupCombo.Visible = false;
            lookupLabel.Visible = false;
            valueCombo.Visible = false;
            valueLabel.Visible = false;
            doValueButton.Visible = false;
            likeLabel.Visible = true;
            likeEdit.Visible = true;
            doButton.Visible = true;
            likeEdit.Text = registry["Like", pcTable.name, ""];
          }
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void lookupChange(object sender, EventArgs e)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          lastErrorStripLabel.Text = "";
          DBHandler db = new DBHandler(connect);
          setDB(db, pcTable.name);
          db.Lookup = "";
          string C = " WHERE ";
          string MR = "5000";
          if (maxRowsEdit.Value > 0)
            MR = maxRowsEdit.Value.ToString();
#if do_it_with_oracle
          db.Lookup += C + "RowNum <= " + MR;
          C = "AND ";
#endif
          string value = likeEdit.Text.Trim();
          if (value.Length > 0)
          {
            int o = pcTable.offsetFields;
            int k = pcTable.offsetKeyFields;
            int f = pcKeyFields[k].index + o;
            value = value.Replace("'", "''");
            int p = value.IndexOf('%');
            if (p == 0) p = value.IndexOf('_');
            db.Lookup += string.Format("{0} {1} {2} '{3}'", C, pcFields[f].name, p == 0 ? "=" : "LIKE", value);
          }
#if do_it_with_lite3
          db.Limit = " LIMIT " + MR;
#elif do_it_with_mssql
          db.Limit = " TOP " + MR;
#endif
          dbLookupLabel.Text = registry["Lookup", pcTable.name] = db.Lookup;
          db.GetTable(pcTable.name,
            pcFields, pcTable.offsetFields, pcTable.noFields,
            pcOrderFields, pcTable.offsetOrderFields, pcTable.noOrderFields);
          db.Grid = tablesDataGrid;
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void valueComboChange(object sender, EventArgs e)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          registry["Value", pcTable.name] = valueCombo.Text;
          DBHandler db = new DBHandler(connect);
          setDB(db, pcTable.name);
          db.Lookup = "";
          string C = " WHERE ";
          string MR = "5000";
          if (maxRowsEdit.Value > 0)
            MR = maxRowsEdit.Value.ToString();
#if do_it_with_oracle
          db.Lookup += C + "RowNum <= " + MR;
          C = "AND ";
#endif
          string value = valueCombo.Text.Trim();
          value = value.Replace("'", "''");
          int p = value.IndexOf('%');
          if (p == 0) p = value.IndexOf('_');
          if (value.Length > 0)
            db.Lookup += string.Format("{0} {1} {2} '{3}'", C, lookupCombo.Text, p == 0 ? "=" : "LIKE", value);
#if do_it_with_lite3
          db.Limit = " LIMIT " + MR;
#endif
          db.GetTable(pcTable.name,
            pcFields, pcTable.offsetFields, pcTable.noFields,
            pcOrderFields, pcTable.offsetOrderFields, pcTable.noOrderFields);
          db.Grid = tablesDataGrid;
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void populateCombo(ComboBox combo, int tableIndex)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          TPCTable pcTable = pcTables[tableIndex];
          lastErrorStripLabel.Text = "";
          DBHandler db = new DBHandler(connect);
          setDB(db, pcTable.name);
          db.Lookup = "";
          db.GetTable(pcTable.name,
            pcFields, pcTable.offsetFields, pcTable.noFields,
            pcOrderFields, pcTable.offsetOrderFields, pcTable.noOrderFields);
          db.PopulateCombo(combo, pcKeyFields, pcTable.offsetKeyFields, pcTable.noKeyFields);
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void listOfRelationsDoubleClick(object sender, EventArgs e)
    {
      if (pcApplication.noRelations == 0) return;
      ListItem item = listOfRelations.SelectedItem as ListItem;
      if (item == null) return;
      pcRelation = pcRelations[item.Index];
      tableNameStripLabel.Text = item.Values[0];
      tableDescrStripLabel.Text = item.Descr[0];
      showRelationsGrid();
    }
    private void showRelationsGrid()
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          lastErrorStripLabel.Text = "";
          DBHandler db = new DBHandler(connect);
          int offsetFields = pcRelation.offsetFields;
          int noFields = pcRelation.noFromFields + pcRelation.noToFields;
          db.GetTable(pcRelation.name,
            pcFields, offsetFields, noFields,
            pcOrderFields, 0, 0);
          db.Grid = relationDataGrid;
          populateCombo(leftRelationCombo, pcRelation.fromTable);
          leftTableLabel.Text = pcTables[pcRelation.fromTable].name;
          populateCombo(rightRelationCombo, pcRelation.toTable);
          rightTableLabel.Text = pcTables[pcRelation.toTable].name;
          availableRelationList.Items.Clear();
          selectedRelationList.Items.Clear();
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private int relationFromCol;
    private int relationNoFromCols;
    private int relationToCol;
    private int relationNoToCols;
    private string relationFromValue;
    private void fillInRelationLists(string fromValue, ComboBox toCombo, int fromCol, int noFromCols, int toCol, int noToCols)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          lastErrorStripLabel.Text = "";
          relationFromCol = fromCol;
          relationNoFromCols = noFromCols;
          relationToCol = toCol;
          relationNoToCols = noToCols;
          relationFromValue = fromValue;
          string[] fromList = fromValue.Split(('|'));
          string[] toList = new string[noToCols];
          availableRelationList.Items.Clear();
          selectedRelationList.Items.Clear();
          for (int i = 0; i < toCombo.Items.Count; i++)
          {
            string toValue = (string)toCombo.Items[i];
            toList = toValue.Split(('|'));
            bool selected = false;
            DataTable table = (DataTable)relationDataGrid.DataSource;
            foreach (DataRow row in table.Rows)
            {
              bool doContinue = false;
              for (int f = 0; f < noFromCols; f++)
              {
                string cell = row[f + fromCol].ToString();
                if (fromList[f].CompareTo(cell) != 0)
                {
                  doContinue = true;
                  break;
                }
              }
              if (doContinue)
                continue;
              bool inSelected = true;
              for (int t = 0; t < noToCols; t++)
              {
                string cell = row[t + toCol].ToString();
                if (toList[t].CompareTo(cell) != 0)
                {
                  inSelected = false;
                  break;
                }
              }
              if (inSelected)
              {
                selectedRelationList.Items.Add(toValue);
                selected = true;
              }
            }
            if (selected == false)
              availableRelationList.Items.Add(toValue);
          }
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void leftRelationComboChange(object sender, EventArgs e)
    {
      int i = leftRelationCombo.SelectedIndex;
      if (i == -1) return;
      rightRelationCombo.SelectedIndex = -1;
      fillInRelationLists(leftRelationCombo.Text, rightRelationCombo
                        , 0, pcRelation.noFromFields
                        , pcRelation.noFromFields, pcRelation.noToFields
                        );
    }
    private void rightRelationComboChange(object sender, EventArgs e)
    {
      int i = rightRelationCombo.SelectedIndex;
      if (i == -1) return;
      leftRelationCombo.SelectedIndex = -1;
      fillInRelationLists(rightRelationCombo.Text, leftRelationCombo
                        , pcRelation.noFromFields, pcRelation.noToFields
                        , 0, pcRelation.noFromFields
                        );
    }
    private void toSelectedClick(object sender, EventArgs e)
    {
      using (AutoCursor.DatabaseCall)
      {
        connect.BeginTransaction();
        try
        {
          lastErrorStripLabel.Text = "";
          int fromCol = relationFromCol == 0 ? relationFromCol : relationToCol;
          int toCol = relationToCol == 0 ? relationFromCol : relationToCol;
          int noFromCols = relationFromCol == 0 ? relationNoFromCols : relationNoToCols;
          int noToCols = relationToCol == 0 ? relationNoFromCols : relationNoToCols;
          int offsetFields = pcRelation.offsetFields;
          string fromValue = relationFromValue;
          string[] toValues = new string[availableRelationList.SelectedIndices.Count];
          int i = 0;
          foreach (int index in availableRelationList.SelectedIndices)
            toValues[i++] = availableRelationList.Items[index].ToString();
          foreach (string toValue in toValues)
          {
            string[] fromList = relationFromCol == 0 ? fromValue.Split(('|')) : toValue.Split(('|'));
            string[] toList = relationToCol == 0 ? fromValue.Split(('|')) : toValue.Split(('|'));
            string fieldNames = "[";
            string values = "[";
            string comma = "";
            string command = "INSERT INTO " + pcRelation.name + " (";
            for (int c = 0; c < noFromCols; c++)
            {
              command += comma + pcFields[c + offsetFields + fromCol].name.ToUpper();
              comma = ", ";
            }
            for (int c = 0; c < noToCols; c++)
            {
              command += comma + pcFields[c + offsetFields + toCol].name.ToUpper();
              comma = ", ";
            }
            command += ", USID, TMSTAMP) VALUES (";
            comma = "";
            for (int c = 0; c < noFromCols; c++)
            {
              fieldNames += comma + "'" + pcFields[c + offsetFields + fromCol].name + "'";
              values += comma + "'" + fromList[c] + "'";
              comma = ", ";
              command += "'" + fromList[c] + "', ";
            }
            for (int c = 0; c < noToCols; c++)
            {
              fieldNames += comma + "'" + pcFields[c + offsetFields + toCol].name + "'";
              values += comma + "'" + toList[c] + "'";
              command += "'" + toList[c] + "', ";
            }
#if do_it_with_oracle
           string CurrentDate = "SysDate";
#elif do_it_with_lite3
            string CurrentDate = "'" + DateTime.Now.ToString("yyyy-MM-dd hh:mm:ss") + "'"; 
#elif do_it_with_mssql
            string CurrentDate = "GetDate()";
#elif do_it_with_mysql
            string CurrentDate = "Now()";
#elif do_it_with_postgres
            string CurrentDate = "CURRENT_TIMESTAMP";
#endif
            command += "'" + userName + "', " + CurrentDate + ")";
            fieldNames += "]";
            values += "]";
            DBHandler db = new DBHandler(connect);
            db.Execute(command);
            if (BinTables.HasValidation() == true)
            {
              if (validatePython(pcRelation, EHow.ADD, fieldNames, values) == false)
              {
                connect.FlagRollback();
                return;
              }
            }
          }
          listOfRelationsDoubleClick(sender, e);
          Refresh();
          if (relationFromCol == 0)
          {
            leftRelationCombo.SelectedIndex = leftRelationCombo.Items.IndexOf(fromValue);
            leftRelationComboChange(sender, e);
          }
          else
          {
            rightRelationCombo.SelectedIndex = rightRelationCombo.Items.IndexOf(fromValue);
            rightRelationComboChange(sender, e);
          }
        }
        catch (Exception ex)
        {
          showException(ex);
          connect.FlagRollback();
        }
        finally
        {
          connect.EndTransaction();
        }
      }
    }
    private void fromSelectedClick(object sender, EventArgs e)
    {
      using (AutoCursor.DatabaseCall)
      {
        connect.BeginTransaction();
        try
        {
          lastErrorStripLabel.Text = "";
          int fromCol = relationFromCol == 0 ? relationFromCol : relationToCol;
          int toCol = relationToCol == 0 ? relationFromCol : relationToCol;
          int noFromCols = relationFromCol == 0 ? relationNoFromCols : relationNoToCols;
          int noToCols = relationToCol == 0 ? relationNoFromCols : relationNoToCols;
          int offsetFields = pcRelation.offsetFields;
          string fromValue = relationFromValue;
          string[] toValues = new string[selectedRelationList.SelectedIndices.Count];
          int i = 0;
          foreach (int index in selectedRelationList.SelectedIndices)
            toValues[i++] = selectedRelationList.Items[index].ToString();
          foreach (string toValue in toValues)
          {
            string[] fromList = relationFromCol == 0 ? fromValue.Split(('|')) : toValue.Split(('|'));
            string[] toList = relationToCol == 0 ? fromValue.Split(('|')) : toValue.Split(('|'));
            string fieldNames = "[";
            string values = "[";
            string comma = "";
            string command = "DELETE FROM " + pcRelation.name;
            string conj = " WHERE ";
            for (int c = 0; c < noFromCols; c++)
            {
              fieldNames += comma + "'" + pcFields[c + offsetFields + fromCol].name + "'";
              values += comma + "'" + fromList[c] + "'";
              comma = ", ";
              command += conj + pcFields[c + offsetFields + fromCol].name + " = '" + fromList[c] + "'";
              conj = " AND ";
            }
            for (int c = 0; c < noToCols; c++)
            {
              fieldNames += comma + "'" + pcFields[c + offsetFields + toCol].name + "'";
              values += comma + "'" + fromList[c] + "'";
              command += conj + pcFields[c + offsetFields + toCol].name + " = '" + toList[c] + "'";
              conj = " AND ";
            }
            fieldNames += "]";
            values += "]";
            DBHandler db = new DBHandler(connect);
            db.Execute(command);
            if (BinTables.HasValidation() == true)
            {
              if (validatePython(pcRelation, EHow.DELETE, fieldNames, values) == false)
              {
                connect.FlagRollback();
                return;
              }
            }
          }
          listOfRelationsDoubleClick(sender, e);
          Refresh();
          if (relationFromCol == 0)
          {
            leftRelationCombo.SelectedIndex = leftRelationCombo.Items.IndexOf(fromValue);
            leftRelationComboChange(sender, e);
          }
          else
          {
            rightRelationCombo.SelectedIndex = rightRelationCombo.Items.IndexOf(fromValue);
            rightRelationComboChange(sender, e);
          }
        }
        catch (Exception ex)
        {
          showException(ex);
          connect.FlagRollback();
        }
        finally
        {
          connect.EndTransaction();
        }
      }
    }
    private void allToSelectedClick(object sender, EventArgs e)
    {
      for (int i = 0; i < availableRelationList.Items.Count; i++)
        availableRelationList.SetSelected(i, true);
      toSelectedClick(sender, e);
    }
    private void allFromSelectedClick(object sender, EventArgs e)
    {
      for (int i = 0; i < selectedRelationList.Items.Count; i++)
        selectedRelationList.SetSelected(i, true);
      fromSelectedClick(sender, e);
    }
    private void relationDataGridDoubleClick(object sender, EventArgs e)
    {
      string w = " where ", c = "";
      DataTable table = (DataTable)relationDataGrid.DataSource;
      int j = relationDataGrid.CurrentRow.Index;
      DataRow row = table.Rows[j];
      int o = pcRelation.offsetFields;
      for (int k = 0; k < pcRelation.noFromFields + pcRelation.noToFields; k++)
      {
        w = w + c + pcFields[o + k].name + "= \"" + row[k] + "\"";
        c = ", ";
        w = "and ";
      }
    }
    private void maxRowsEditChange(object sender, EventArgs e)
    {
      registry["MaxRows", pcTable.name] = maxRowsEdit.Value.ToString();
    }
    private void lookupComboChanged(object sender, EventArgs e)
    {
      registry["LookupKey", pcTable.name] = lookupCombo.Text.Trim();
      valueCombo.Items.Clear();
      valueCombo.Text = "";
    }
    private void valueComboDropDown(object sender, EventArgs e)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          lastErrorStripLabel.Text = "";
          if (valueCombo.Items.Count > 0) return;
          int field;
          int noFields = pcTable.noFields;
          int offsetFields = pcTable.offsetFields;
          for (field = 0; field < noFields; field++)
            if (lookupCombo.Text.CompareTo(pcFields[field + offsetFields].name) == 0)
              break;
          if (field > noFields) return;
          DBHandler db = new DBHandler(connect);
          db.GetDistinctList(pcTable.name, pcFields[field + offsetFields]);
          db.PopulateCombo(valueCombo);
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private bool isKey(TPCTable pcTable, int field)
    {
      int o = pcTable.offsetKeyFields;
      for (int i = 0; i < pcTable.noKeyFields; i++)
        if (pcKeyFields[o + i].index == field)
          return true;
      return false;
    }
    private string escape(string data)
    {
      int n = data.IndexOf("'");
      if (n == -1)
        return data;
      return data.Substring(0, n) + "'" + escape(data.Substring(n));
    }
    string sqlReady(TPCField field, string data, bool isInsert = false, string tableName = "")
    {
      data = data.Trim();
      if (data.Length == 0 && field.isNull == 1)
        return "NULL";
      switch (field.type)
      {
        case DBHandler.PC_BOOLEAN:
        case DBHandler.PC_BYTE:
        case DBHandler.PC_SHORT:
        case DBHandler.PC_DOUBLE:
        case DBHandler.PC_INT:
        case DBHandler.PC_LONG:
          return data;
        case DBHandler.PC_SEQUENCE:
          if (isInsert == true)
            return tableName + "Seq.NextVal";
          return data;
        case DBHandler.PC_CHAR:
        case DBHandler.PC_USERSTAMP:
          return "'" + escape(data) + "'";
#if do_it_with_oracle
        case DBHandler.PC_DATE:
          return "to_date('" + data + "','YYYYMMDD')";
        case DBHandler.PC_DATETIME:
          return "to_date('" + data + "','YYYYMMDDHH24MISS')";
        case DBHandler.PC_TIME:
          return "to_date('" + data + "','HH24MISS')";
        case DBHandler.PC_TIMESTAMP:
          return "to_date('" + data + "','YYYYMMDDHH24MISS')";
#elif do_it_with_lite3
        case DBHandler.PC_DATE:
          return String.Format("'{0}-{1}-{2}'", data.Substring(0,4), data.Substring(4,2), data.Substring(6,2));
        case DBHandler.PC_DATETIME:
        case DBHandler.PC_TIMESTAMP:
          return String.Format("'{0}-{1}-{2} {3}:{4}:{5}'", data.Substring(0, 4), data.Substring(4, 2), data.Substring(6, 2), data.Substring(8, 2), data.Substring(10, 2), data.Substring(12, 2));
        case DBHandler.PC_TIME:
          return String.Format("'{0}:{1}:{2}'", data.Substring(0, 2), data.Substring(2, 2), data.Substring(4, 2));
#elif do_it_with_mssql
        case DBHandler.PC_DATE:
          return "CONVERT(DATETIME, " + String.Format("'{0}-{1}-{2}')", data.Substring(0,4), data.Substring(4,2), data.Substring(6,2));
        case DBHandler.PC_DATETIME:
        case DBHandler.PC_TIMESTAMP:
          return "CONVERT(DATETIME, " + String.Format("'{0}-{1}-{2} {3}:{4}:{5}')", data.Substring(0, 4), data.Substring(4, 2), data.Substring(6, 2), data.Substring(8, 2), data.Substring(10, 2), data.Substring(12, 2));
        case DBHandler.PC_TIME:
          return "CONVERT(DATETIME, " + String.Format("'0001-01-01 {0}:{1}:{2}')", data.Substring(0, 2), data.Substring(2, 2), data.Substring(2, 2));
#elif do_it_with_mysql
        case DBHandler.PC_DATE:
          return "STR_TO_DATE('" + data + "', '%Y%m%d')";
        case DBHandler.PC_DATETIME:
        case DBHandler.PC_TIMESTAMP:
          return "STR_TO_DATE('" + data + "', '%Y%m%d%H%i%s')";
        case DBHandler.PC_TIME:
          return "STR_TO_DATE('" + data + "', '%H%i%s')";
#elif do_it_with_postgres
        case DBHandler.PC_DATE:
          return "TO_DATE('" + data + "','YYYYMMDD')";
        case DBHandler.PC_DATETIME:
        case DBHandler.PC_TIMESTAMP:
          return "TO_DATE('" + data + "','YYYYMMDDHH24MISS')";
        case DBHandler.PC_TIME:
          return "TO_DATE('" + data + "','HH24MISS')";
#endif
      }
      return "";
    }
    int maxLength(TPCField field)
    {
      switch (field.type)
      {
        case DBHandler.PC_CHAR:
        case DBHandler.PC_USERSTAMP:
        case DBHandler.PC_DATE:
        case DBHandler.PC_DATETIME:
        case DBHandler.PC_TIME:
        case DBHandler.PC_TIMESTAMP:
          return field.length;
        case DBHandler.PC_BOOLEAN:
          return 1;
        case DBHandler.PC_BYTE:
          return 3;
        case DBHandler.PC_SHORT:
          return 5;
        case DBHandler.PC_DOUBLE:
          return 20;
        case DBHandler.PC_SEQUENCE:
        case DBHandler.PC_INT:
        case DBHandler.PC_LONG:
          return 10;
      }
      return 0;
    }
    private bool sizeIsOk(int link)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          DBHandler db = new DBHandler(connect);
          int tno = pcLinks[link].tableNo;
          string name = pcTables[tno].name;
          int size = db.GetCount(name);
          return size < int.Parse(maxDropdownEdit.Text);
        }
        catch (Exception ex)
        {
          showException(ex);
          return false;
        }
      }
    }
    public enum EHow { ADD, CHANGE, DELETE, DISPLAY }
    private bool validatePython(TPCRelation relation, EHow how, string fieldNames, string values)
    {
      string oldValues = how == EHow.ADD ? "[]" : values;
      string newValues = how == EHow.DELETE ? "[]" : values;
      string validate = string.Format("result = validate('{0}', {1}, {2}, {3}, {4})", relation.name, (int)how, fieldNames, oldValues, newValues);
      if (pcRelation.validate.Length > 0)
      {
        if (runValidate(pcRelation.validate, validate) == false)
          return false;
      }
      else
      {
        if (runValidate(pcApplication.validateOther, validate) == false)
          return false;
      }
      if (runValidate(pcApplication.validateAll, validate) == false)
        return false;
      return true;
    }
    internal bool validateTablePython(EHow how, List<EntryItem> edits)
    {
      
      string oldValues = "[";
      string newValues = "[";
      string fieldNames = "[";
      string comma = "";
      foreach (EntryItem plus in edits)
      {
        fieldNames += string.Format("{0}'{1}'", comma, plus.label.Text);
        if (how != EHow.ADD)
          oldValues += string.Format("{0}'{1}'", comma, plus.oldValue);
        if (how != EHow.DELETE)
          newValues += string.Format("{0}'{1}'", comma, plus.Value);
        comma = ", ";
      }
      fieldNames += "]";
      oldValues += "]";
      newValues += "]";
      string validate = string.Format("result = validate('{0}', {1}, {2}, {3}, {4})", pcTable.name, (int)how, fieldNames, oldValues, newValues);
      if (pcTable.validate.Length > 0)
      {
        if (runValidate(pcTable.validate, validate) == false)
          return false;
      }
      else
      {
        if (runValidate(pcApplication.validateOther, validate) == false)
          return false;
      }
      return true;
    }
    private bool validateAuditPython(TPCTable table, EHow how, List<EntryItem> edits, string oldUsId, string newUsId, string oldTmStamp, string newTmStamp)
    {
      string oldValues = "[";
      string newValues = "[";
      string fieldNames = "[";
      string comma = "";
      foreach (EntryItem plus in edits)
      {
        fieldNames += string.Format("{0}'{1}'", comma, plus.label.Text);
        if (how != EHow.ADD)
          oldValues += string.Format("{0}'{1}'", comma, plus.oldValue);
        if (how != EHow.DELETE)
          newValues += string.Format("{0}'{1}'", comma, plus.Value);
        comma = ", ";
      }
      fieldNames += ", 'UsId', 'TmStamp']";
      if (how != EHow.ADD)
        oldValues += string.Format(", '{0}', '{1}'", oldUsId, oldTmStamp);
      oldValues += "]";
      if (how != EHow.DELETE)
        newValues += string.Format(", '{0}', '{1}'", newUsId, newTmStamp);
      newValues += "]";
      string validate = string.Format("result = validate('{0}', {1}, {2}, {3}, {4})", table.name, (int)how, fieldNames, oldValues, newValues);
      if (runValidate(pcApplication.validateAll, validate) == false)
        return false;
      return true;
    }
    private bool runValidate(string runCode, string validate)
    {
      if (runCode.Length == 0)
        return true;
      string pythonCode = string.Format("{0}\r\n"
                                + "{1}\r\n", runCode, validate);
      try
      {
        ScriptSource validation = pyEngine.CreateScriptSourceFromString(pythonCode, SourceCodeKind.Statements);
        validation.Execute(pyScope);
        bool result = pyScope.GetVariable("result");
        return result;
      }
      catch (Exception exception)
      {
        LogError = pythonCode;
        LogError = exception.ToString();
        MessageBox.Show(string.Format("{0}\r\n{1}", exception.Message,pythonCode), "runValidate");
        return false;
      }
    }
    private ScaffoldForm scaffoldForm;
    private bool setupLink(TPCTable pcTable, int fieldNo, out int rlink, out int rpair)
    {
      rlink = -1;
      rpair = -1;
      if (pcTable.noLinks == 0)
        return false;
      int offsetLink = pcTable.offsetLinks;
      for (int link = 0; link < pcTable.noLinks; link++)
      {
        int offsetPair = pcLinks[offsetLink + link].offsetLinkPairs;
        for (int pair = 0; pair < pcLinks[offsetLink + link].noLinkPairs; pair++)
        {
          if (pcLinkPairs[offsetPair + pair].fromNo == fieldNo)
          {
            rlink = offsetLink + link;
            rpair = pair;
            return true;
          }
        }
      }
      return false;
    }
    private int getIndex(ComboBox combo, string text)
    {
      for (int i = 0; i < combo.Items.Count; i++)
      {
        ListItem li = combo.Items[i] as ListItem;
        if (li != null && li.Values[0] == text)
          return i;
      }
      return 0;
    }
    private void newDropDown(EntryItem edit, int fromTable, int link, int Top, int Left, int Width, string text)
    {
      using (AutoCursor.DatabaseCall)
      {
        try
        {
          edit.Combo(Top, Left, Width, text);
          DBHandler db = new DBHandler(connect);
          int tno = pcLinks[link].tableNo;
          TPCTable pcTable = pcTables[tno];
          int noFields = pcTable.noFields;
          int offsetFields = pcTable.offsetFields;
          int noShowFields = pcTable.noShowFields;
          int offsetShowFields = pcTable.offsetShowFields;
          int noOrderFields = pcTable.noOrderFields;
          int noLinkPairs = pcLinks[link].noLinkPairs;
          int offsetOrderFields = pcTable.offsetOrderFields;
          if (noShowFields > 0)
            db.GetTable(pcTable.name, pcFields, offsetFields, noFields, pcShowFields, offsetShowFields, noShowFields);
          else
            db.GetTable(pcTable.name, pcFields, offsetFields, noFields, pcOrderFields, offsetOrderFields, noOrderFields);
          DataTable table = db.GetDataTable();
          System.Data.DataRowCollection rows = table.Rows;
          for (int row = 0; row < rows.Count; row++)
          {
            string[] values = new string[noLinkPairs];
            string[] descr = new string[noShowFields == 0 ? noLinkPairs : noShowFields];
            for (int field = 0; field < noFields; field++)
            {
              int offset = pcLinks[link].offsetLinkPairs;
              int offset2 = pcTable.offsetShowFields;
              for (int pair = 0; pair < noLinkPairs; pair++)
              {
                if (field == pcLinkPairs[pair + offset].toNo)
                {
                  values[pair] = rows[row][field].ToString();
                  if (noShowFields == 0)
                    descr[pair] = values[pair];
                }
              }
              for (int show = 0; show < noShowFields; show++)
              {
                if (field == pcShowFields[offset2 + show].index)
                  descr[show] = rows[row][field].ToString();
              }
            }
            edit.combo.Items.Add(new ListItem(values, descr, row));
          }
          int no = getIndex(edit.combo, text);
          edit.combo.SelectedIndex = no;
        }
        catch (Exception ex)
        {
          showException(ex);
        }
      }
    }
    private void enumDropDown(EntryItem edit, int field, int top, int left, int width, string text)
    {
      edit.Combo(top, left, width, text);
      for (int i = 0, offset = pcFields[field].offsetEnums; i < pcFields[field].noEnums; i++)
      {
        string[] values = new string[1];
        string result = "";
        if (edit.ofType == DBHandler.PC_CHAR)
          values[0] = pcEnums[i + offset].name;
        else
        {
          values[0] = pcEnums[i + offset].value.ToString();
          result = values[0] + "=";
        }
        string[] descr = new string[1];
        descr[0] =  result+pcEnums[i + offset].name;
        edit.combo.Items.Add(new ListItem(values, descr, i));
      }
      int no = getIndex(edit.combo, text);
      edit.combo.SelectedIndex = no;
    }
    private void parmExecute(EHow how)
    {
      if (allowUpdates == false && how != EHow.DISPLAY)
        return;
      string where = "", c = "", query = "";
      int row = -1;
      int field;
      DataTable data = null;
      int offset = pcTable.offsetFields;
      if (tablesDataGrid.CurrentRow != null)
      {
        where = "WHERE ";
        row = tablesDataGrid.CurrentRow.Index;
        data = tablesDataGrid.DataSource as DataTable;
        for (int k = 0; k < pcTable.noKeyFields; k++)
        {
          int okfield = k + pcTable.offsetKeyFields;
          field = pcKeyFields[okfield].index;
          string value = data.Rows[row][field].ToString();
          TPCField pcField = pcFields[offset + field];
          string ready = sqlReady(pcField, value);
          where += c + pcFields[offset + field].name + " = " + ready;
          c = " AND ";
        }
      }
      string oldUsId = "";
      string oldTmStamp = "";
      string newUsId = "";
      string newTmStamp = "";
      if (row != -1)
      {
        int no = pcTable.noFields;
        oldUsId = data.Rows[row][no].ToString();
        oldTmStamp = DateTime.Parse(data.Rows[row][no + 1].ToString()).ToString("yyyyMMddhhmmss");
      }
      List<EntryItem> edits = new List<EntryItem>(pcTable.noFields);
      ScaffoldForm form = scaffoldForm = new ScaffoldForm(this, how, edits);
      int comboWidth = form.ClientSize.Width - 132;
      int editWidth = comboWidth - 18;
      // The next 3 lines are indicative of why properties are at best second class.
      Size size = form.MinimumSize;
      size.Height = pcTable.noFields * 24 + 66 + 12;
      form.MinimumSize = size;
      Dictionary<int, ComboBox> combos = new Dictionary<int, ComboBox>();
      for (field = 0; field < pcTable.noFields; field++)
      {
        EntryItem edit = new EntryItem();
        edits.Add(edit);
        TPCField pcField = pcFields[field + offset];
        edit.ofType = pcField.type;
        edit.isNull = pcField.isNull == 1;
        if (how != EHow.ADD && (how == EHow.DELETE || how == EHow.DISPLAY || isKey(pcTable, field))
        || (how == EHow.ADD && pcField.type == DBHandler.PC_SEQUENCE))
          edit.isReadOnly = true;
        if (pcField.uppercase == 1 || isKey(pcTable, field) == true)
          edit.uppercase = true;
        Label label = edit.label = new Label();
        label.Text = pcField.name;
        label.Top = field * 24 + 4;
        label.Left = 4;
        form.CapturePanel.Controls.Add(label);
        if (showSetup.Checked == true)
          LogDebug = label.Text + " " + label.Top + " " + label.Left;
        string setupWork = pcField.name;
        string value = "";
        if (how == EHow.ADD || (how == EHow.CHANGE && isKey(pcTable, field) == false))
        {
          value = row != -1 ? data.Rows[row][field].ToString() : "";
          if (clearAdd.Checked == true && how == EHow.ADD) value = "";
          int link;  int pair;
          if (setupLink(pcTable, field, out link, out pair))
          {
            setupWork += " Is Link Field";
            if (sizeIsOk(link) == true)
            {
              setupWork += " and is not too large";
              if (pair == 0)
              {
                newDropDown(edit, pcTable.index, link, field * 24 + 4, 124, comboWidth, value);
                combos.Add(link, edit.combo);
                edit.pair = pair;
                form.CapturePanel.Controls.Add(edit.combo);
                if (showSetup.Checked == true)
                  LogDebug = setupWork;
              }
              else
              {
                setupWork += " is extra link pair field";
                edit.isReadOnly = true;
                edit.combo = combos[link];
                edit.pair = pair;
                edit.combo.SelectedIndexChanged += new EventHandler(editComboSelectedIndexChanged);
                edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
                List<EntryItem> list = (List<EntryItem>)edit.combo.Tag;
                list.Add(edit);
                form.CapturePanel.Controls.Add(edit.text);
              }
              continue;
            }
          }
          else if (pcField.noEnums > 0)
          {
            setupWork += " Is Enum Field";
            enumDropDown(edit, field + offset, field * 24 + 4, 124, comboWidth, value);
            form.CapturePanel.Controls.Add(edit.combo);
            if (showSetup.Checked == true)
              LogDebug = setupWork;
            continue;
          }
        }
        value = row != -1 ? data.Rows[row][field].ToString() : "";
        if (clearAdd.Checked == true && how == EHow.ADD) value = "";
        switch (pcField.type)
        {
          case DBHandler.PC_CHAR:
          case DBHandler.PC_USERSTAMP:
            {
              setupWork += " Is Text Field";
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
          case DBHandler.PC_DATE:
            {
              setupWork += " Is Date Field";
              edit.Date(field * 24 + 4, 124, comboWidth, value);
              form.CapturePanel.Controls.Add(edit.date);
            }
            break;
          case DBHandler.PC_DATETIME:
          case DBHandler.PC_TIMESTAMP:
            {
              setupWork += " Is Date Time Field";
              edit.Date(field * 24 + 4, 124, comboWidth * 8 / 14, value);
              form.CapturePanel.Controls.Add(edit.date);
              edit.Time(field * 24 + 4, 124 + edit.date.Width + 4, comboWidth * 6 / 14 - 4, value);
              form.CapturePanel.Controls.Add(edit.time);
            }
            break;
          case DBHandler.PC_TIME:
            {
              setupWork += " Is Time Field";
              edit.Time(field * 24 + 4, 124, comboWidth, value);
              form.CapturePanel.Controls.Add(edit.time);
            }
            break;
          case DBHandler.PC_BOOLEAN:
            {
              setupWork += " Is Boolean";
              edit.Combo(field * 24 + 4, 124, comboWidth, value);
              form.CapturePanel.Controls.Add(edit.combo);
            }
            break;
          case DBHandler.PC_BYTE:
            {
              setupWork += " Is Byte";
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
          case DBHandler.PC_SHORT:
            {
              setupWork += " Is Short";
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
          case DBHandler.PC_DOUBLE:
            {
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
          case DBHandler.PC_SEQUENCE:
          case DBHandler.PC_INT:
            {
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
          case DBHandler.PC_LONG:
            {
              edit.Text(field * 24 + 4, 124, editWidth, maxLength(pcField), value);
              form.CapturePanel.Controls.Add(edit.text);
            }
            break;
        }
        if (showSetup.Checked == true)
          LogDebug = setupWork;
      }
      switch (how)
      {
        case EHow.ADD:
          form.Text = "Add Record for " + pcTable.name;
          form.isAdd = true;
          form.isChange = false;
          form.isDelete = false;
          form.isDisplay = false;
          break;
        case EHow.CHANGE:
          form.Text = "Change Record on " + pcTable.name;
          form.isAdd = false;
          form.isChange = true;
          form.isDelete = false;
          form.isDisplay = false;
          break;
        case EHow.DELETE:
          form.Text = "Delete Record in " + pcTable.name;
          form.isAdd = false;
          form.isChange = false;
          form.isDelete = true;
          form.isDisplay = false;
          break;
        case EHow.DISPLAY:
          form.Text = "Display Record of " + pcTable.name;
          form.isAdd = false;
          form.isChange = false;
          form.isDelete = false;
          form.isDisplay = true;
          break;
      }
      DialogResult rc = form.ShowDialog();
      if (rc != DialogResult.OK || how == EHow.DISPLAY)
        return;
      List<string> cells = new List<string>(pcTable.noFields);
      for (field = 0; field < pcTable.noFields; field++)
        cells.Add(edits[field].Value);
      connect.BeginTransaction();
      try
      {
        DBHandler db = new DBHandler(connect);
        string sysdate = DateTime.Now.ToString("yyyyMMddhhmmss");
#if do_it_with_oracle
        string CurrentDate = "SysDate";
#elif do_it_with_lite3
        string CurrentDate = "'" + DateTime.Now.ToString("yyyy-MM-dd hh:mm:ss") + "'"; 
#elif do_it_with_mssql
        string CurrentDate = "GetDate()";
#elif do_it_with_mysql
        string CurrentDate = "Now()";
#elif do_it_with_postgres
        string CurrentDate = "CURRENT_TIMESTAMP";
#endif
        switch (how)
        {
          case EHow.ADD:
            newUsId = userName;
            newTmStamp = sysdate;
            query = "INSERT INTO " + pcTable.name;
            query += " VALUES ";
            c = "( ";
            for (field = 0; field < pcTable.noFields; field++)
            {
              query += c + sqlReady(pcFields[offset + field], cells[field], true, pcTable.name);
              c = ", ";
            }
            query += ", '" + userName + "', " + CurrentDate + ")";
            break;
          case EHow.CHANGE:
            newUsId = userName;
            newTmStamp = sysdate;
            query = "UPDATE " + pcTable.name;
            query += " SET ";
            c = "";
            for (field = 0; field < pcTable.noFields; field++)
            {
              if (isKey(pcTable, field) == true)
                continue;
              query += c + pcFields[offset + field].name + " = "
                         + sqlReady(pcFields[offset + field], cells[field]);
              c = ", ";
            }
            query += ", " + db.UsId + " = '" + userName + "', " + db.TmStamp + " = " + CurrentDate;
            query += " " + where;
            break;
          case EHow.DELETE:
            query = "DELETE FROM " + pcTable.name;
            query += " " + where;
            break;
        }
        using (AutoCursor.DatabaseCall)
        {
          try
          {
            if (showSQL.Checked == true)
              LogDebug = query;
            db.Execute(query);
            if (BinTables.HasValidation() == true)
            {
              if (validateAuditPython(pcTable, how, edits, oldUsId, newUsId, oldTmStamp, newTmStamp) == false)
              {
                connect.FlagRollback();
                return;
              }
              showTableGrid();
            }
          }
          catch (Exception ex)
          {
            connect.FlagRollback();
            showException(ex);
          }
        }
      }
      finally
      {
        connect.EndTransaction();
      }
    }
    void editComboSelectedIndexChanged(object sender, EventArgs e)
    {
      ComboBox combo = sender as ComboBox;
      if (combo == null)
        return;
      ListItem li = combo.SelectedItem as ListItem;
      List<EntryItem> list = combo.Tag as List<EntryItem>;
      for (int i = 1; i < list.Count; i++)
      {
        list[i].text.Text = li.Values[i];
      }
    }
    private void addExecute(object sender, EventArgs e)
    {
      using (AutoCursor.Hourglass)
        parmExecute(EHow.ADD);
    }
    private void changeExecute(object sender, EventArgs e)
    {
      using (AutoCursor.Hourglass)
        parmExecute(EHow.CHANGE);
    }
    private void deleteExecute(object sender, EventArgs e)
    {
      using (AutoCursor.Hourglass)
        parmExecute(EHow.DELETE);
    }
    private void displayExecute(object sender, EventArgs e)
    {
      using (AutoCursor.Hourglass)
        parmExecute(EHow.DISPLAY);
    }
    private void maxDropDownEditChange(object sender, EventArgs e)
    {
      registry["maxDropDown"] = maxDropdownEdit.Value.ToString();
    }
    private void showSQLCheckedChanged(object sender, EventArgs e)
    {
      registry["showSQL"] = showSQL.Checked ? "True" : "False";
    }
    private void showBinfileCheckedChanged(object sender, EventArgs e)
    {
      registry["showBinfile"] = showBinfile.Checked ? "True" : "False";
    }
    private void showSetupCheckedChanged(object sender, EventArgs e)
    {
      registry["showSetup"] = showSetup.Checked ? "True" : "False";
    }
    private void likeEditChange(object sender, EventArgs e)
    {
      registry["Like", pcTable.name] = likeEdit.Text;
    }
    private void clearAddChanged(object sender, EventArgs e)
    {
      registry["ClearAdd", pcTable.name] = clearAdd.Checked ? "True" : "False";
    }
    private void logLevelChanged(object sender, EventArgs e)
    {
      RadioButton button = sender as RadioButton;
      if (button != null)
        registry[button.Name] = button.Checked.ToString();
      switch (button.Name)
      {
        case "logErrorRB": Logger.Level = Logger.LogType.error; break;
        case "logWarnRB": Logger.Level = Logger.LogType.warn; break;
        case "logInfoRB": Logger.Level = Logger.LogType.info; break;
        case "logDebugRB": Logger.Level = Logger.LogType.debug; break;
      }
      setDebugOptions();
    }
    private void tablesVSplitMoved(object sender, SplitterEventArgs e)
    {
      registry["tablesVSplitDist"] = tablesVSplit.SplitterDistance.ToString();
    }
    private void tablesHSplitMoved(object sender, SplitterEventArgs e)
    {
      registry["tablesHSplitDist"] = tablesHSplit.SplitterDistance.ToString();
    }
    private void relationsVSplitMoved(object sender, SplitterEventArgs e)
    {
      registry["relationsVSplitDist"] = relationsVSplit.SplitterDistance.ToString();
    }
    private void relationsHSplitMoved(object sender, SplitterEventArgs e)
    {
      registry["relationsHSplitDist"] = relationsHSplit.SplitterDistance.ToString();
    }
    private void resizeBalance(object sender, EventArgs e)
    {
      registry["listOfRelationsWidth"] = listOfRelations.Width.ToString();
      int median = (listOfRelations.Width - middlePanel.Width) / 2;
      leftSidePanel.Width = righttSidePanel.Width = median;
      Refresh();
    }
    private void formSizeChanged(object sender, EventArgs e)
    {
      registry["formSizeTop"] = this.Top.ToString();
      registry["formSizeLeft"] = this.Left.ToString();
      registry["formSizeHeight"] = this.Height.ToString();
      registry["formSizeWidth"] = this.Width.ToString();
    }
    private void positionForm()
    {
      try
      {
        int Top = Registry.IntOf(registry["formSizeTop", "0"]);
        int Left = Registry.IntOf(registry["formSizeLeft", "0"]);
        int Height = Registry.IntOf(registry["formSizeHeight", "768"]);
        int Width = Registry.IntOf(registry["formSizeWidth", "1024"]);
        int tablesVSplitDist = Registry.IntOf(registry["tablesVSplitDist", "320"]);
        int tablesHSplitDist = Registry.IntOf(registry["tablesHSplitDist", "450"]);
        int relationsHSplitDist = Registry.IntOf(registry["relationsHSplitDist", "265"]);
        if (Top < 0) Top = 0;
        if (Left < 0) Left = 0;
        this.Top = Top;
        this.Left = Left;
        this.Height = Height;
        this.Width = Width;
        Refresh();
        this.tablesVSplit.SplitterDistance = tablesVSplitDist;
        this.tablesHSplit.SplitterDistance = tablesHSplitDist;
        this.relationsHSplit.SplitterDistance = relationsHSplitDist;
        Refresh();
      }
      catch (Exception exception)
      {
        showException(exception);
      }
    }
    private void setDebugOptions()
    {
      showSQL.Visible = showBinfile.Visible = showSetup.Visible = logDebugRB.Checked;
      DBHandler.ShowSQL = ShowSql;
    }
    private void setGeneralOptions()
    {
      if (registry["logErrorRB", "False"] == "True") logErrorRB.Checked = true;
      else if (registry["logWarnRB ", "False"] == "True") logWarnRB.Checked = true;
      else if (registry["logInfoRB ", "False"] == "True") logInfoRB.Checked = true;
      else if (registry["logDebugRB", "False"] == "True") logDebugRB.Checked = true;
    }
    private void applicationActivated(object sender, EventArgs e)
    {
      positionForm();
    }
    private void clearLogButton_Click(object sender, EventArgs e)
    {
      logMemo.Clear();
    }
  }
}

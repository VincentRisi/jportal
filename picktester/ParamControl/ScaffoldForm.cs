using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace bbd.ParamControl
{
  public partial class ScaffoldForm : Form
  {
    public Control CapturePanel { get { return capturePanel; } }
    public bool isAdd;
    public bool isChange;
    public bool isDelete;
    public bool isDisplay;
    public bool firstTime;
    private List<EntryItem> edits;
    private ParamControlForm pcForm;
    private ParamControlForm.EHow how;
    public ScaffoldForm(ParamControlForm pcForm, ParamControlForm.EHow how, List<EntryItem> edits)
    {
      InitializeComponent();
      this.edits = edits;
      this.pcForm = pcForm;
      this.how = how;
    }
    
    private static bool inValidBoolean(string label, TextBox edit)
    {
      string ud = edit.Text.ToUpper();
      if (ud == "T"
      || ud == "TRUE"
      || ud == "Y"
      || ud == "YES"
      || ud == "1")
      {
        edit.Text = "1";
        return false;
      }
      if (ud == "F"
      || ud == "FALSE"
      || ud == "N"
      || ud == "NO"
      || ud == "0")
      {
        edit.Text = "0";
        return false;
      }
      string msg = string.Format("The field {0} contains the value '{1}'. "
                               + "This is invalid for a non null boolean field", label, edit.Text);
      MessageBox.Show(msg, "Invalid Boolean", MessageBoxButtons.OK);
      return true;
    }

    private static int strspn(string s, string spn)
    {
      int no = 0;
      foreach (char ch in s)
      {
        if (spn.IndexOf(ch) == -1)
          break;
        no++;
      }
      return no;
    }

    private bool inValidInteger(string label, TextBox edit, int length)
    {
      if (edit.ReadOnly == true)
        return false;
      string ud = edit.Text.Trim();
      if (ud.Length == 0)
      {
        string msg1 = string.Format("The field {0} contains the value '{1}'. "
                                 + " This is invalid for a non null integer field allowing"
                                 + " for {2} digits of data", label, edit.Text, length);
        MessageBox.Show(msg1, "Invalid Integer", MessageBoxButtons.OK);
        return true;
      }
      if (ud[0] == '-')
        ud = ud.Substring(1);
      int n = ud.Length;
      if (n <= length
      && strspn(ud, "0123456789") == n)
        return false;
      string msg2 = string.Format("The field {0} contains the value '{1}'. "
                               + " This is invalid for a non null integer field allowing"
                               + " for {2} digits of data", label, edit.Text, length);
      MessageBox.Show(msg2, "Invalid Integer", MessageBoxButtons.OK);
      return true;
    }

    private static bool inValidDate(string label, DateTimePicker date)
    {
      string ud = date.Text.Trim();
      int n = ud.Length;
      if (n == 8 && strspn(ud, "0123456789") == 8)
      {
        int year = int.Parse(ud.Substring(0, 4));
        int month = int.Parse(ud.Substring(4, 2));
        int day = int.Parse(ud.Substring(6, 2));
        try
        {
          DateTime d = new DateTime(year, month, day);
          return false;
        }
        catch (Exception)
        { }
      }
      string msg = string.Format("The field {0} contains the value '{1}'. "
                               + "This is invalid for a non null date field", label, date.Text);
      MessageBox.Show(msg, "Invalid Date", MessageBoxButtons.OK);
      return true;
    }

    private static bool inValidChar(string label, TextBox edit)
    {
      if (edit.Text.Length > 0)
        return false;
      string msg = string.Format("The field {0} contains the value '{1}'. "
                               + "This is invalid for a non null char field", label, edit.Text);
      MessageBox.Show(msg, "Invalid Char", MessageBoxButtons.OK);
      return true;
    }

    private static bool inValidDateTime(string label, DateTimePicker date, DateTimePicker time)
    {
      string ud = date.Text.Trim() + time.Text.Trim();
      int n = ud.Length;
      if (n == 14 && strspn(ud, "0123456789") == 14)
      {
        int year = int.Parse(ud.Substring(0, 4));
        int month = int.Parse(ud.Substring(4, 2));
        int day = int.Parse(ud.Substring(6, 2));
        int hour = int.Parse(ud.Substring(8, 2));
        int min = int.Parse(ud.Substring(10, 2));
        int sec = int.Parse(ud.Substring(12, 2));
        try
        {
          DateTime d = new DateTime(year, month, day, hour, min, sec);
          return false;
        }
        catch (Exception)
        { }
      }
      string msg = string.Format("The fields {0} contains the value '{1}{2}'. "
                               + "This is invalid for a non null date field", label, date.Text, time.Text);
      MessageBox.Show(msg, "Invalid DateTime", MessageBoxButtons.OK);
      return true;
    }

    private static bool inValidDouble(string label, TextBox edit)
    {
      string ud = edit.Text.Trim();
      if (ud[0] == '-')
        ud = ud.Substring(1);
      if (strspn(ud, "0123456789.") == ud.Length)
        return false;
      string msg = string.Format("The field {0} contains the value '{1}'. "
                               + "This is invalid for a non null double field", label, edit.Text);
      MessageBox.Show(msg, "Invalid Double", MessageBoxButtons.OK);
      return true;
    }

    private static bool inValidTime(string label, DateTimePicker time)
    {
      string ud = time.Text.Trim();
      int n = ud.Length;
      if (n == 6 && strspn(ud, "0123456789") == 6)
      {
        int hour = int.Parse(ud.Substring(0, 2));
        int min = int.Parse(ud.Substring(2, 2));
        int sec = int.Parse(ud.Substring(4, 2));
        try
        {
          DateTime d = new DateTime(2001, 01, 01, hour, min, sec);
          return false;
        }
        catch (Exception)
        { }
      }
      string msg = string.Format("The field {0} contains the value '{1}'. "
                               + "This is invalid for a non null date field", label, time.Text);
      MessageBox.Show(msg, "Invalid Time", MessageBoxButtons.OK);
      return true;
    }

    public void OkButtonClick(object sender, EventArgs e)
    {
      DialogResult = System.Windows.Forms.DialogResult.None;
      Label label;
      foreach (Control control in capturePanel.Controls)
      {
        EntryItem edit = control.Tag as EntryItem;
        if (edit != null)
        {
          if (edit.Value.Length == 0 && edit.isNull == true)
            continue;
          if (edit.combo == null)
          {
            switch (edit.ofType)
            {
              case DBHandler.PC_BOOLEAN:
                if (inValidBoolean(edit.label.Text, edit.text) == true)
                  return;
                break;
              case DBHandler.PC_BYTE:
                if (inValidInteger(edit.label.Text, edit.text, 3) == true)
                  return;
                break;
              case DBHandler.PC_CHAR:
                if (inValidChar(edit.label.Text, edit.text) == true)
                  return;
                break;
              case DBHandler.PC_DATE:
                if (inValidDate(edit.label.Text, edit.date) == true)
                  return;
                break;
              case DBHandler.PC_DATETIME:
                if (inValidDateTime(edit.label.Text, edit.date, edit.time) == true)
                  return;
                break;
              case DBHandler.PC_DOUBLE:
                if (inValidDouble(edit.label.Text, edit.text) == true)
                  return;
                break;
              case DBHandler.PC_INT:
                if (inValidInteger(edit.label.Text, edit.text, 10) == true)
                  return;
                break;
              case DBHandler.PC_LONG:
                if (inValidInteger(edit.label.Text, edit.text, 18) == true)
                  return;
                break;
              case DBHandler.PC_SEQUENCE:
                if (inValidInteger(edit.label.Text, edit.text, 10) == true)
                  return;
                break;
              case DBHandler.PC_SHORT:
                if (inValidInteger(edit.label.Text, edit.text, 5) == true)
                  return;
                break;
              case DBHandler.PC_TIME:
                if (inValidTime(edit.label.Text, edit.time) == true)
                  return;
                break;
            }
          }
          continue;
        }
        Label l = control as Label;
        if (l != null) label = l;
      }
      if (pcForm.validateTablePython(how, edits) == true)
        DialogResult = System.Windows.Forms.DialogResult.OK;
    }
  }

  public class ListItem
  {
    string[] values; public string[] Values { get { return values; } set { values = value; } }
    string[] descr; public string[] Descr { get { return descr; } set { descr = value; } }
    int index; public int Index { get { return index; } set { index = value; } }
    public ListItem(string[] values, string[] descr, int index)
    {
      Values = values;
      Descr = descr;
      Index = index;
    }
    public override string ToString()
    {
      string result = "";
      string comma = "";
      foreach (string d in descr)
      {
        result = result + comma + d;
        comma = ", ";
      }
      return result;
    }
  }

  public class EntryItem
  {
    public bool isControl;
    public bool isNull;
    public bool isReadOnly;
    public bool uppercase;
    public int ofType;
    public ComboBox combo;
    public int pair;
    public Label label;
    public TextBox text;
    public DateTimePicker date;
    public DateTimePicker time;
    public string oldValue;
    public string Value 
    { 
      get 
      {
        if (text != null)
          return text.Text;
        else if (combo != null)
        {
          string data = combo.SelectedItem as String;
          if (data != null) return data;
          ListItem li = combo.SelectedItem as ListItem;
          if (li == null) return "";
          if (pair > 0)
            return li.Values[pair];
          return li.Values[0];
        }
        else if (date != null && time != null)
          return date.Text + time.Text;
        else if (date != null)
          return date.Text;
        else if (time != null)
          return time.Text;
        return "";
      } 
    }
    public EntryItem()
    {
      isNull = false;
      isReadOnly = false; 
      label = null;
      combo = null;
      date  = null;
      text = null;
      time = null;
      pair = -1;
    }
    public void Text(int top, int left, int width, int maxlength, string value)
    {
      text = new TextBox();
      text.Name = label.Text + "Text";
      text.Top = top;
      text.Left = left;
      text.Width = width;
      text.Text = value;
      text.MaxLength = maxlength;
      text.BorderStyle = BorderStyle.FixedSingle;
      text.Anchor |= AnchorStyles.Right;
      if (isReadOnly == true)
      {
        text.ReadOnly = true;
        text.BackColor = Color.LightGray;
      }
      if (uppercase == true)
        text.CharacterCasing = CharacterCasing.Upper;
      text.Tag = this;
      oldValue = value;
    }
    public void Date(int top, int left, int width, string value)
    {
      date = new DateTimePicker();
      date.Name = label.Text + "Date";
      date.Top = top;
      date.Left = left;
      date.Width = width;
      date.Text = value;
      date.Format = DateTimePickerFormat.Custom;
      date.CustomFormat = "yyyyMMdd";
      date.Anchor |= AnchorStyles.Right;
      date.Tag = this;
      if (isReadOnly == true)
        date.Enabled = false;
      if (value.Length != 0)
        oldValue = DateTime.Parse(value).ToString("yyyyMMdd");
      else
        oldValue = value;
    }
    public void Time(int top, int left, int width, string value)
    {
      time = new DateTimePicker();
      time.Name = label.Text + "Time";
      time.Top = top;
      time.Left = left;
      time.Width = width;
      time.Text = value;
      time.Format = DateTimePickerFormat.Custom;
      time.CustomFormat = "HHmmss";
      time.AllowDrop = false;
      time.ShowUpDown = true;
      time.Anchor = AnchorStyles.Top | AnchorStyles.Right;
      time.Tag = this;
      if (isReadOnly == true)
        time.Enabled = false;
      if (value.Length != 0)
        oldValue += DateTime.Parse(value).ToString("hhmmss");
      else
        oldValue = value;
    }
    public void Combo(int top, int left, int width, string value)
    {
      combo = new ComboBox();
      combo.Name = label.Text + "Combo";
      combo.Top = top;
      combo.Left = left;
      combo.Width = width;
      combo.Text = value;
      combo.DropDownStyle = ComboBoxStyle.DropDownList;
      combo.Anchor |= AnchorStyles.Right;
      List<EntryItem> list = new List<EntryItem>();
      list.Add(this);
      combo.Tag = list;
      oldValue = value;
    }
  }
}

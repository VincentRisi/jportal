#include <vcl.h>
#pragma hdrstop

#include "GFEEntryUnit.h"
#include "DBHandler.h"

#pragma package(smart_init)
#pragma resource "*.dfm"
TEntryForm *EntryForm;

__fastcall TEntryForm::TEntryForm(TComponent* Owner)
  : TForm(Owner)
{
}

void __fastcall TEntryForm::FormCreate(TObject *Sender)
{
  firstTime = true;
}


void __fastcall TEntryForm::FormActivate(TObject *Sender)
{
  if (firstTime)
  {
    firstTime = false;
    if (isDelete)
      FocusControl(CancelButton);
    else if (isAdd)
    {
      TControl *Control = (TWinControl*)FindComponent("Edit0");
      TEditPlus* Edit = dynamic_cast<TEditPlus*>(Control);
      if (Edit != 0 && Edit->ReadOnly != true)
        FocusControl(Edit);
    }
    else if (isDisplay)
      CancelButton->Visible = false;
  }
}

bool __fastcall TEntryForm::InValidBoolean(AnsiString label, TEditPlus *edit)
{
  AnsiString ud = edit->Text.UpperCase();
  if (ud == "T"
  ||  ud == "TRUE"
  ||  ud == "Y"
  ||  ud == "YES"
  ||  ud == "1")
  {
    edit->Text = "1";
    return false;
  }
  if (ud == "F"
  ||  ud == "FALSE"
  ||  ud == "N"
  ||  ud == "NO"
  ||  ud == "0")
  {
    edit->Text = "0";
    return false;
  }
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null boolean field";
  Application->MessageBox(msg.c_str(), "Invalid Boolean", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidInteger(AnsiString label, TEditPlus *edit, int length)
{
  if (edit->ReadOnly == true)
    return false;
  AnsiString ud = edit->Text;
  if (ud.IsEmpty() == true)
  {
    AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                    +" This is invalid for a non null integer field allowing"
                    +" for "+length+" bytes of data";
    Application->MessageBox(msg.c_str(), "No Data Entered", ID_OK);
    return true;
  }
  if (ud[1] == '-')
    ud.Delete(1,1);
  int n = ud.Length();
  if (n <= length
  && strspn(ud.c_str(), "0123456789") == (unsigned)n)
    return false;
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null integer field allowing"
                  +" for "+length+" bytes of data";
  Application->MessageBox(msg.c_str(), "Invalid Number", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidDate(AnsiString label, TEditPlus *edit)
{
  AnsiString ud = edit->Text;
  int n = ud.Length();
  if (n == 8 && strspn(ud.c_str(), "0123456789") == 8)
  {
    int year = ud.SubString(1,4).ToInt();
    int month = ud.SubString(5,2).ToInt();
    int day = ud.SubString(7,2).ToInt();
    try
    {
      TDate d(year, month, day);
      return false;
    }
    catch (...)
    {}
  }
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null date field.";
  Application->MessageBox(msg.c_str(), "Invalid Date", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidChar(AnsiString label, TEditPlus *edit)
{
  if (edit->Text.Length() > 0)
    return false;
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null char field.";
  Application->MessageBox(msg.c_str(), "Invalid Char", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidDateTime(AnsiString label, TEditPlus *edit)
{
  AnsiString ud = edit->Text;
  int n = ud.Length();
  if (n == 14 && strspn(ud.c_str(), "0123456789") == 14)
  {
    try
    {
      int year = ud.SubString(1,4).ToInt();
      int month = ud.SubString(5,2).ToInt();
      int day = ud.SubString(7,2).ToInt();
      int hour = ud.SubString(9,2).ToInt();
      int min = ud.SubString(11,2).ToInt();
      int sec = ud.SubString(13,2).ToInt();
      TDateTime d(year, month, day);
      TDateTime t(hour, min, sec, 0);
      return false;
    }
    catch (...)
    {}
  }
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null datetime field.";
  Application->MessageBox(msg.c_str(), "Invalid DateTime", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidDouble(AnsiString label, TEditPlus *edit)
{
  AnsiString ud = edit->Text;
  if (ud[1] == '-')
    ud.Delete(1,1);
  if (strspn(ud.c_str(), "0123456789.") == (unsigned)ud.Length())
    return false;
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null double field";
  Application->MessageBox(msg.c_str(), "Invalid Double", ID_OK);
  return true;
}

bool __fastcall TEntryForm::InValidTime(AnsiString label, TEditPlus *edit)
{
  AnsiString ud = edit->Text;
  int n = ud.Length();
  if (n == 6 && strspn(edit->Text.c_str(), "0123456789") == 6)
  {
    try
    {
      int hour = ud.SubString(9,2).ToInt();
      int min = ud.SubString(11,2).ToInt();
      int sec = ud.SubString(13,2).ToInt();
      TDateTime t(hour, min, sec, 0);
      return true;
    }
    catch (...)
    {}
  }
  AnsiString msg = "The field "+label+" contains the value '"+edit->Text+"'."
                  +" This is invalid for a non null time field.";
  Application->MessageBox(msg.c_str(), "Invalid Time", ID_OK);
  return true;
}

void __fastcall TEntryForm::OkButtonClick(TObject *Sender)
{
  int i;
  TControl *Control;
  TEditPlus* Edit;
  TLabel* Label;
  ModalResult = mrNone;
  for (i = 0; i < ControlCount; i++)
  {
    Control = Controls[i];
    if ((Edit = dynamic_cast<TEditPlus*>(Control)) != 0)
    {
      ActiveControl = Edit;
      Edit->Text.Trim();
      Label = Edit->Label;
      if (Edit->Text.Length() == 0 && Edit->null == 1)
        continue;
      switch (Edit->type)
      {
      case TPC_BOOLEAN:
        if (InValidBoolean(Label->Caption, Edit))
          return;
        break;
      case TPC_BYTE:
        if (InValidInteger(Label->Caption, Edit, 3))
          return;
        break;
      case TPC_CHAR:
        if (InValidChar(Label->Caption, Edit))
          return;
        break;
      case TPC_DATE:
        if (InValidDate(Label->Caption, Edit))
          return;
        break;
      case TPC_DATETIME:
        if (InValidDateTime(Label->Caption, Edit))
          return;
        break;
      case TPC_DOUBLE:
        if (InValidDouble(Label->Caption, Edit))
          return;
        break;
      case TPC_INT:
        if (InValidInteger(Label->Caption, Edit, 10))
          return;
        break;
      case TPC_LONG:
        if (InValidInteger(Label->Caption, Edit, 10))
          return;
        break;
      case TPC_SEQUENCE:
        if (InValidInteger(Label->Caption, Edit, 10))
          return;
        break;
      case TPC_SHORT:
        if (InValidInteger(Label->Caption, Edit, 5))
          return;
        break;
      case TPC_TIME:
        if (InValidTime(Label->Caption, Edit))
          return;
        break;
      }
    }
    else if (dynamic_cast<TLabel*>(Control) != 0)
      Label = dynamic_cast<TLabel*>(Control);
  }
  ModalResult = mrOk;
}



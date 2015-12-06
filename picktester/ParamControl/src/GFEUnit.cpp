#include <vcl.h>
#include <io.h>
#pragma hdrstop

#include "GFEUnit.h"
#include "tbuffer.h"
#include "verinfo.hpp"

#pragma package(smart_init)
#pragma resource "*.dfm"
TMainForm *MainForm;
#ifdef _UPM_AWARE
TWSSecure *ModSec;
#endif

template <class T>
struct Form
{
  T* form;
  Form(TForm *f) {form = new T(f);}
  ~Form()        {delete form;}
};

struct autoCursor
{
  TCursor saveCursor;
  autoCursor(TCursor newCursor)
  {
    saveCursor = Screen->Cursor;
    Screen->Cursor = newCursor;
  }
  ~autoCursor()
  {
    Screen->Cursor = saveCursor;
  }
};

#ifdef _UPM_AWARE
static void __fastcall Extractor(TWSSecure* ModSec)
{
  ModSec->Extract(MainForm);
}
#endif

__fastcall TMainForm::TMainForm(TComponent* Owner)
  : TForm(Owner)
{
}

void __fastcall TMainForm::FormCreate(TObject *Sender)
{
  FirstTime = true;
}

struct TStringObject : public TObject
{
  AnsiString value;
  TStringObject(AnsiString in) {value = in;}
};

void __fastcall TMainForm::LogParameters()
{
  strstream buffer;
  buffer << PC_Application;
  buffer << "***********\r\n Tables\r\n***********\r\n";
  for (int i=0; i<PC_Application.noTables; i++)
    buffer << "[" << i << "] " << PC_Table[i];
  buffer << "************\r\n Relations\r\n************\r\n";
  for (int i=0; i<PC_Application.noRelations; i++)
    buffer << "[" << i << "] " << PC_Relation[i];
  buffer << "************\r\n Fields\r\n************\r\n";
  for (int i=0; i<PC_Application.noFields; i++)
    buffer << "[" << i << "] " << PC_Field[i];
  buffer << "************\r\n Enums\r\n************\r\n";
  for (int i=0; i<PC_Application.noEnums; i++)
    buffer << "[" << i << "] " << PC_Enum[i];
  buffer << "************\r\n KeyFields\r\n************\r\n";
  for (int i=0; i<PC_Application.noKeyFields; i++)
    buffer << "[" << i << "] " << PC_KeyField[i];
  buffer << "************\r\n OrderFields\r\n************\r\n";
  for (int i=0; i<PC_Application.noOrderFields; i++)
    buffer << "[" << i << "] " << PC_OrderField[i];
  buffer << "************\r\n ShowFields\r\n************\r\n";
  for (int i=0; i<PC_Application.noShowFields; i++)
    buffer << "[" << i << "] " << PC_ShowField[i];
  buffer << "************\r\n BreakFields\r\n************\r\n";
  for (int i=0; i<PC_Application.noBreakFields; i++)
    buffer << "[" << i << "] " << PC_BreakField[i];
  buffer << "************\r\n Links\r\n************\r\n";
  for (int i=0; i<PC_Application.noLinks; i++)
    buffer << "[" << i << "] " << PC_Link[i];
  buffer << "************\r\n LinkPairs\r\n************\r\n";
  for (int i=0; i<PC_Application.noLinkPairs; i++)
    buffer << "[" << i << "] " << PC_LinkPair[i];
  LogMemo->Lines->Add(buffer.str());
}

void __fastcall TMainForm::FormActivate(TObject *Sender)
{
  if (!FirstTime)
    return;
  FirstTime = false;
  autoCursor ac(crAppStart);
  ValueCombo->Top = LookupCombo->Top;
  ValueLabel->Top = LookupLabel->Top;
  LookupCombo->Top = LikeEdit->Top;
  LookupLabel->Top = LikeLabel->Top;
  DOValueButton->Top = DOButton->Top;
#ifdef _UPM_AWARE
  AnsiString ShortName = ChangeFileExt(ExtractFileName(ExtractShortPathName(_argv[0])), "").UpperCase();
  AnsiString Parmfile = ShortName+".BIN";
#else
  AnsiString Parmfile = "ParmGFE.BIN";
  if (_argc > 1)
    Parmfile = _argv[1];
#endif
  AnsiString StartUpConfig = AnsiString(_argv[0])+".config";
  if (access(StartUpConfig.c_str(), 0) == 0)
  {
    Log = "Startup Config File: "+StartUpConfig;
    ParmGFEConfig config(StartUpConfig.c_str());
    Parmfile = config.parameterBinFile.data;
  }
  else
  {
    AnsiString StartUpRegistry = ExtractFilePath(_argv[0]);
    if (StartUpRegistry.Length() > 2)
    {
      if (StartUpRegistry[2] == ':')
        StartUpRegistry.Delete(1,2);
      if (StartUpRegistry[StartUpRegistry.Length()] == '\\')
        StartUpRegistry.Delete(StartUpRegistry.Length(), 1);
      Log = "Startup Registry: \\SOFTWARE"+StartUpRegistry;
      TQRegistry Reg("\\SOFTWARE"+StartUpRegistry);
      if (_argc > 1)
        Reg.WriteString("Parameter", "Binary File", Parmfile);
      Parmfile = Reg.ReadString("Parameter", "Binary File", Parmfile);
    }
  }
  Log = "Parmfile: "+Parmfile;
  TVerInfoRes *Info = new TVerInfoRes(_argv[0]);
  double Version;
  try
  {
    Version = 1.0;
    char x[24];
    sprintf(x, "PGM:%0.3f", Version);
    MainForm->StatusBar->Panels->Items[4]->Text = x;
  }
  __finally
  {
    delete Info;
  }
#ifdef _UPM_AWARE
  AnsiString Puck = "";
  if (_argc > 1)
    Puck = _argv[_argc-1];
  if (Puck == "IOBJECT")
    UserID = "SIMONSHEMESH";
  else
  {
    ModSec = new TWSSecure(this, ShortName.c_str(), Version, "Parameter Maintenance", Extractor);
    if (ModSec->IsExtract)
    {
      PostQuitMessage(1);
      return;
    }
    ModSec->InitDialog(this);
    UserID = ModSec->UserID();
  }
#else
  UserID = "PARMGFE";
#endif
  Pages->ActivePage = Tables;
  Refresh();
  TPC_LoadParameters(Parmfile.c_str());
  if (ShowBin->Checked == true)
    LogParameters();
  MainForm->Caption = PC_Application.descr;
  MainForm->StatusBar->Panels->Items[0]->Text = PC_Application.name;
  MainForm->StatusBar->Panels->Items[1]->Text = "BIN:"+PC_Application.version;
  QRegistry = new TQRegistry(PC_Application.registry);
  try
  {
    MainForm->StatusBar->Panels->Items[5]->Text = QRegistry->UserID+"@"+QRegistry->Server;
    conn.Logon(QRegistry->UserID.c_str(), QRegistry->Password.c_str(), QRegistry->Server.c_str());
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
  MaxDropdownEdit->Text = QRegistry->MaxDropdown;
  ShowPanel->Visible = QRegistry->ShowPanel;
  ShowSQL->Checked = QRegistry->ShowSQL;
  ShowBin->Checked = QRegistry->ShowBin;
  ShowSetup->Checked = QRegistry->ShowSetup;
  for (int i=0; i<PC_Application.noTables; i++)
    ListOfTables->Items->AddObject(PC_Table[i].descr, new TStringObject(PC_Table[i].name));
  for (int i=0; i<PC_Application.noRelations; i++)
    ListOfRelations->Items->AddObject(PC_Relation[i].descr, new TStringObject(PC_Relation[i].name));
}

void __fastcall TMainForm::FormDestroy(TObject *Sender)
{
  try
  {
    TPC_FreeParameters();
    conn.Logoff();
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::ClearDataGrid(TStringGrid *DataGrid)
{
  for (int row=0; row<DataGrid->RowCount; row++)
    for (int col=0; col<DataGrid->ColCount; col++)
      DataGrid->Cells[col][row] = "";
  DataGrid->RowCount = 2;
  DataGrid->ColCount = 1;
}

AnsiString __fastcall TMainForm::GetField(TDBHandler &db, int field)
{
  int type = db.fields[field].type;
  switch (type)
  {
    case TPC_BOOLEAN:
    case TPC_BYTE:
    case TPC_SHORT:
    {
      short data;
      bool null = db.GetTableField(field, data);
      if (null) return "";
      return data;
    }
    case TPC_CHAR:
    case TPC_USERSTAMP:
    case TPC_DATE:
    case TPC_DATETIME:
    case TPC_TIME:
    case TPC_TIMESTAMP:
    {
      TBChar ac(db.fields[field].length+8);
      bool null = db.GetTableField(field, ac.data);
      if (null) return "";
      return ac.data;
    }
    case TPC_DOUBLE:
    {
      double data;
      bool null = db.GetTableField(field, data);
      if (null) return "";
      return data;
    }
    case TPC_SEQUENCE:
    case TPC_INT:
    {
      int data;
      bool null = db.GetTableField(field, data);
      if (null) return "";
      return data;
    }
    case TPC_LONG:
    {
      long data;
      bool null = db.GetTableField(field, data);
      if (null) return "";
      return data;
    }
  }
  return "";
}

void __fastcall TMainForm::FillInGrid(TDBHandler &db, TStringGrid *DataGrid)
{
  int no = db.noFields;
  ClearDataGrid(DataGrid);
  DataGrid->ColCount = no;
  for (int i=0; i<no; i++)
  {
    DataGrid->Cells[i][0] = db.fields[i].name;
    DataGrid->ColWidths[i] = DataGrid->Canvas->TextExtent(DataGrid->Cells[i][0]).cx+8;
  }
  for (int r=1; db.GetTableFetch(); r++)
  {
    DataGrid->RowCount = r+1;
    if (r > 15)
      Refresh();
    for (int i=0; i<no; i++)
    {
      DataGrid->Cells[i][r] = GetField(db, i);
      int len = DataGrid->Canvas->TextExtent(DataGrid->Cells[i][r]).cx+8;
      if (DataGrid->ColWidths[i] < len)
        DataGrid->ColWidths[i] = len;
    }
  }
  DataGrid->Hint = (DataGrid->RowCount-1);
}

void __fastcall TMainForm::MessageBox(TOciApiException &x)
{
  char Buffer[sizeof(x)+256];
  sprintf(Buffer, "ErrorNo:%d\r\n%s\r\nFile:%s(%d)", x.ErrorNo, x.ErrorDesc, x.File, x.Line);
  Application->MessageBox(Buffer, "List Of Tables", MB_OK);
}

void __fastcall TMainForm::SetUpdateActions()
{
  AddParm->Enabled = AllowUpdates;
  ChangeParm->Enabled = AllowUpdates;
  DelParm->Enabled = AllowUpdates;
  DisplayParm->Enabled = AllowUpdates;
}

void __fastcall TMainForm::SetDB(TDBHandler &db)
{
  db.Lookup  = QRegistry->Lookup[AnsiString(PC_Table[TableIndex].name)];
  db.UsId    = QRegistry->UsId[AnsiString(PC_Table[TableIndex].name)];
  db.TmStamp = QRegistry->TmStamp[AnsiString(PC_Table[TableIndex].name)];
}

void __fastcall TMainForm::ListOfTablesDblClick(TObject *Sender)
{
  if (ListOfTables->ItemIndex == -1)
    return;
  int i = 0;
  while(PC_Table[i].name != ((TStringObject*)ListOfTables->Items->Objects[ListOfTables->ItemIndex])->value)
    i++;
  TableIndex = i;
  MainForm->StatusBar->Panels->Items[2]->Text = PC_Table[TableIndex].name;
  MainForm->StatusBar->Panels->Items[3]->Text = PC_Table[TableIndex].descr;
  MaxRowsEdit->Text = QRegistry->MaxRows[AnsiString(PC_Table[i].name)];
  try
  {
    if (PC_Table[TableIndex].viewOnly == 0)
      AllowUpdates = true;
    else
      AllowUpdates = false;
    SetUpdateActions();
    TDBHandler db(conn);SetDB(db);
    autoCursor ac(crSQLWait);
    db.GetTable(PC_Table[i].name,
                PC_Table[i].noFields,
               &PC_Field[PC_Table[i].offsetFields],
                PC_OrderField,
                PC_Table[i].noOrderFields,
                PC_Table[i].offsetOrderFields);
    FillInGrid(db, TableDataGrid);
    if (PC_Table[i].noOrderFields > 1)
    {
      LookupCombo->Visible = true;
      LookupLabel->Visible = true;
      ValueCombo->Visible = true;
      ValueLabel->Visible = true;
      DOValueButton->Visible = true;
      LikeLabel->Visible = false;
      LikeEdit->Visible = false;
      DOButton->Visible = false;
      LookupCombo->Clear();
      ValueCombo->Clear();
      int o = PC_Table[i].offsetFields;
      for (int j=0; j<PC_Table[i].noOrderFields; j++)
      {
        int k = j+PC_Table[i].offsetOrderFields;
        int f = PC_OrderField[k].index+o;
        LookupCombo->Items->Add(PC_Field[f].name);
      }
      LookupCombo->Text = QRegistry->LookupKey[AnsiString(PC_Table[i].name)];
      ValueCombo->Text = QRegistry->LookupValue[AnsiString(PC_Table[i].name)];
    }
    else
    {
      LookupCombo->Visible = false;
      LookupLabel->Visible = false;
      ValueCombo->Visible = false;
      ValueLabel->Visible = false;
      DOValueButton->Visible = false;
      LikeLabel->Visible = true;
      LikeEdit->Visible = true;
      DOButton->Visible = true;
      LikeEdit->Text = QRegistry->Like[AnsiString(PC_Table[i].name)];
    }
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::LookupChange(TObject *Sender)
{
  TDBHandler db(conn);SetDB(db);
  int table = TableIndex;
  db.Lookup = "";
  AnsiString C = "WHERE";
  if (MaxRowsEdit->Text.ToInt() > 0)
  {
    db.Lookup += C + " RowNum <= "+MaxRowsEdit->Text;
    C = "AND";
  }
  if (LikeEdit->Text.Trim().Length() > 0)
  {
    int o = PC_Table[table].offsetFields;
    int k = PC_Table[table].offsetKeyFields;
    int f = PC_KeyField[k].index+o;
    db.Lookup += C + " "+PC_Field[f].name+" LIKE '"+LikeEdit->Text.Trim()+"'";
  }
  QRegistry->Lookup[AnsiString(PC_Table[TableIndex].name)] = db.Lookup;
  try
  {
    autoCursor ac(crSQLWait);
    db.GetTable(PC_Table[table].name,
                PC_Table[table].noFields,
               &PC_Field[PC_Table[table].offsetFields],
                PC_OrderField,
                PC_Table[table].noOrderFields,
                PC_Table[table].offsetOrderFields);
    FillInGrid(db, TableDataGrid);
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::PopulateCombo(TComboBox *Combo, int tableIndex)
{
  try
  {
    TDBHandler db(conn);
    autoCursor ac(crSQLWait);
    db.GetTable(PC_Table[tableIndex].name,
                PC_Table[tableIndex].noFields,
               &PC_Field[PC_Table[tableIndex].offsetFields],
                PC_OrderField,
                PC_Table[tableIndex].noOrderFields,
                PC_Table[tableIndex].offsetOrderFields);
    Combo->Clear();
    while (db.GetTableFetch())
    {
      AnsiString Text = "";
      AnsiString W = "";
      int o = PC_Table[tableIndex].offsetKeyFields;
      for (int i=0; i < PC_Table[tableIndex].noKeyFields; i++)
      {
        int n = PC_KeyField[o+i].index;
        Text += W + GetField(db, n);
        W = "|";
      }
      Combo->Items->Add(Text);
    }
    Combo->Hint = Combo->Items->Count;
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::ListOfRelationsDblClick(TObject *Sender)
{
  if (ListOfRelations->ItemIndex == -1)
    return;
  try
  {
    TDBHandler db(conn);
    int i = RelationIndex = ListOfRelations->ItemIndex;
    MainForm->Caption = "Parameter Control Maintenance: "+PC_Relation[i].name;
    autoCursor ac(crSQLWait);
    db.GetTable(PC_Relation[i].name,
                PC_Relation[i].noFromFields+PC_Relation[i].noToFields,
               &PC_Field[PC_Relation[i].offsetFields], PC_OrderField, 0, 0);
    FillInGrid(db, RelationDataGrid);
    PopulateCombo(LeftRelationCombo, PC_Relation[i].fromTable);
    LeftTableLabel->Caption = PC_Table[PC_Relation[i].fromTable].name;
    PopulateCombo(RightRelationCombo, PC_Relation[i].toTable);
    RightTableLabel->Caption = PC_Table[PC_Relation[i].toTable].name;
    AvailableRelationList->Clear();
    SelectedRelationList->Clear();
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

static void SplitList(AnsiString Value, TBuffer<AnsiString> &List, int no)
{
  if (no == 1)
  {
    List[0] = Value;
  }
  else for (int i=0; i<no; i++)
  {
    int p = Value.Pos("|");
    if (p > 1)
    {
      List[i] = Value.SubString(1, p-1);
      Value.Delete(1, p);
      continue;
    }
    List[i] = Value;
    break;
  }
  return;
}

void __fastcall TMainForm::FillInRelationLists(AnsiString FromValue, TComboBox *ToCombo, int FromCol, int noFromCols, int ToCol, int noToCols)
{
  RelationFromCol = FromCol;
  RelationNoFromCols = noFromCols;
  RelationToCol = ToCol;
  RelationNoToCols = noToCols;
  RelationFromValue = FromValue;
  TBuffer<AnsiString> FromList(noFromCols);
  SplitList(FromValue, FromList, noFromCols);
  TBuffer<AnsiString> ToList(noToCols);
  AvailableRelationList->Clear();
  SelectedRelationList->Clear();
  for (int i=0; i<ToCombo->Items->Count; i++)
  {
    AnsiString ToValue = ToCombo->Items->Strings[i];
    SplitList(ToValue, ToList, noToCols);
    bool Selected = false;
    for (int r=1; r<RelationDataGrid->RowCount; r++)
    {
      bool doContinue = false;
      for (int f=0; f<noFromCols; f++)
      {
        if (FromList[f].AnsiCompare(RelationDataGrid->Cells[f+FromCol][r]) != 0)
        {
          doContinue = true;
          break;
        }
      }
      if (doContinue)
        continue;
      bool inSelected = true;
      for (int t=0; t<noToCols; t++)
      {
        if (ToList[t].AnsiCompare(RelationDataGrid->Cells[t+ToCol][r]) != 0)
        {
          inSelected = false;
          break;
        }
      }
      if (inSelected)
      {
        SelectedRelationList->Items->Add(ToValue);
        Selected = true;
      }
    }
    if (Selected == false)
      AvailableRelationList->Items->Add(ToValue);
  }
}

void __fastcall TMainForm::LeftRelationComboChange(TObject *Sender)
{
  int i = LeftRelationCombo->ItemIndex;
  int r = RelationIndex;
  if (i == -1)
    return;
  RightRelationCombo->Text = "";
  FillInRelationLists(LeftRelationCombo->Text, RightRelationCombo,
                      0, PC_Relation[r].noFromFields,
                      PC_Relation[r].noFromFields, PC_Relation[r].noToFields);
}

void __fastcall TMainForm::RightRelationComboChange(TObject *Sender)
{
  int i = RightRelationCombo->ItemIndex;
  int r = RelationIndex;
  if (i == -1)
    return;
  LeftRelationCombo->Text = "";
  FillInRelationLists(RightRelationCombo->Text, LeftRelationCombo,
                      PC_Relation[r].noFromFields, PC_Relation[r].noToFields,
                      0, PC_Relation[r].noFromFields);
}

void __fastcall TMainForm::ToSelectedClick(TObject *Sender)
{
  int FromCol = RelationFromCol;
  int noFromCols = RelationNoFromCols;
  int noToCols = RelationNoToCols;
  int r = RelationIndex;
  AnsiString FromValue = RelationFromValue;
  TBuffer<AnsiString> FromList(noFromCols);
  SplitList(FromValue, FromList, noFromCols);
  TBuffer<AnsiString> ToList(noToCols);
  try
  {
    for (int i=0; i<AvailableRelationList->Items->Count; i++)
    {
      if (AvailableRelationList->Selected[i] == true)
      {
        AnsiString ToValue = AvailableRelationList->Items->Strings[i];
        AnsiString Command = "INSERT INTO "+PC_Relation[r].name+" VALUES (";
        SplitList(ToValue, ToList, noToCols);
        if (FromCol == 0)
        {
          for (int i=0; i<noFromCols; i++)
            Command += "'" + FromList[i] + "', ";
          for (int i=0; i<noToCols; i++)
            Command += "'" + ToList[i] + "', ";
        }
        else
        {
          for (int i=0; i<noToCols; i++)
            Command += "'" + ToList[i] + "', ";
          for (int i=0; i<noFromCols; i++)
            Command += "'" + FromList[i] + "', ";
        }
        Command += "'"+UserID+"', SysDate)";
        if (ShowSQL->Checked == true)
          LogMemo->Lines->Add(Command);
        TDBHandler db(conn);
        db.Execute(Command);
      }
    }
    conn.Commit();
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
  ListOfRelationsDblClick(Sender);
  Refresh();
  if (FromCol == 0)
  {
    LeftRelationCombo->ItemIndex = LeftRelationCombo->Items->IndexOf(RelationFromValue);
    LeftRelationComboChange(Sender);
  }
  else
  {
    RightRelationCombo->ItemIndex = RightRelationCombo->Items->IndexOf(RelationFromValue);
    RightRelationComboChange(Sender);
  }
}

void __fastcall TMainForm::FromSelectedClick(TObject *Sender)
{
  int FromCol = RelationFromCol;
  int noFromCols = RelationNoFromCols;
  int ToCol = RelationToCol;
  int noToCols = RelationNoToCols;
  int r = RelationIndex;
  AnsiString FromValue = RelationFromValue;
  TBuffer<AnsiString> FromList(noFromCols);
  SplitList(FromValue, FromList, noFromCols);
  TBuffer<AnsiString> ToList(noToCols);
  try
  {
    for (int i=0; i<SelectedRelationList->Items->Count; i++)
    {
      if (SelectedRelationList->Selected[i] == true)
      {
        AnsiString ToValue = SelectedRelationList->Items->Strings[i];
        SplitList(ToValue, ToList, noToCols);
        AnsiString Command = "DELETE FROM "+PC_Relation[r].name+" ";
        SplitList(ToValue, ToList, noToCols);
        for (int i=0; i<noFromCols; i++)
          Command += (i==0?"WHERE ":" AND ")+RelationDataGrid->Cells[i+FromCol][0] + " = '" + FromList[i] +"'";
        for (int i=0; i<noToCols; i++)
          Command += " AND "+RelationDataGrid->Cells[i+ToCol][0] + " = '" + ToList[i]+"'";
        if (ShowSQL->Checked == true)
          LogMemo->Lines->Add(Command);
        TDBHandler db(conn);
        db.Execute(Command);
      }
    }
    conn.Commit();
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
  ListOfRelationsDblClick(Sender);
  Refresh();
  if (FromCol == 0)
  {
    LeftRelationCombo->ItemIndex = LeftRelationCombo->Items->IndexOf(RelationFromValue);
    LeftRelationComboChange(Sender);
  }
  else
  {
    RightRelationCombo->ItemIndex = RightRelationCombo->Items->IndexOf(RelationFromValue);
    RightRelationComboChange(Sender);
  }
}

void __fastcall TMainForm::AllToSelectedClick(TObject *Sender)
{
  for (int i=0; i<AvailableRelationList->Items->Count; i++)
    AvailableRelationList->Selected[i] = true;
  ToSelectedClick(Sender);
}

void __fastcall TMainForm::AllFromSelectedClick(TObject *Sender)
{
  for (int i=0; i<SelectedRelationList->Items->Count; i++)
    SelectedRelationList->Selected[i] = true;
  FromSelectedClick(Sender);
}

void __fastcall TMainForm::RelationDataGridDblClick(TObject *Sender)
{
  AnsiString w = " where ", c = "";
  int i = RelationIndex;
  int j = RelationDataGrid->Row;
  int o = PC_Relation[i].offsetFields;
  for (int k=0; k < PC_Relation[i].noFromFields+PC_Relation[i].noToFields; k++)
  {
    w = w + c + PC_Field[o+k].name + "= \"" + RelationDataGrid->Cells[k][j] + "\"";
    c = ", ";
  }
  StatusBar->Panels->Items[0]->Text = "Relation("+AnsiString(i)+") :"+w;
}

bool setupLink(int tableNo, int fieldNo, int& rlink)
{
  if (PC_Table[tableNo].noLinks == 0)
    return false;
  int offsetLink = PC_Table[tableNo].offsetLinks;
  for (int link = 0; link < PC_Table[tableNo].noLinks; link++)
  {
    int offsetPair = PC_Link[offsetLink+link].offsetLinkPairs;
    for (int pair = 0; pair < PC_Link[offsetLink+link].noLinkPairs; pair++)
    {
      if (PC_LinkPair[offsetPair+pair].fromNo == fieldNo)
      {
        if (pair == 0)
          rlink = offsetLink+link;
        else
          rlink = -1;
        return true;
      }
    }
  }
  return false;
}

void __fastcall TMainForm::datePicker(TForm *form, TEdit* edit, int top, int left, int width, AnsiString text, EDEWhat what)
{
  TDateEdit* DateEdit = new TDateEdit(form, edit, what);
  DateEdit->Top = top;
  DateEdit->Left = left;
  DateEdit->Width = width;
  DateEdit->Anchors << akRight;
  DateEdit->OnChange = DateEditChange;
  form->InsertControl(DateEdit);
  try
  {
    switch (what)
    {
      case dewDate:
      case dewDateOf:
      {
        TDateTime Date(text.SubString(1,4).ToInt(), text.SubString(5,2).ToInt(), text.SubString(7,2).ToInt());
        DateEdit->Date = Date;
        break;
      }
      case dewTime:
      {
        DateEdit->Kind = dtkTime;
        TDateTime Time(text.SubString(1,2).ToInt(), text.SubString(3,2).ToInt(), text.SubString(5,2).ToInt(), 0);
        DateEdit->Time = Time;
        break;
      }
      case dewTimeOf:
      {
        DateEdit->Kind = dtkTime;
        TDateTime Time2(text.SubString(9,2).ToInt(), text.SubString(11,2).ToInt(), text.SubString(13,2).ToInt(), 0);
        DateEdit->Time = Time2;
        break;
      }
    }
  }
  catch (...)
  {}
}

void __fastcall TMainForm::EditPlusEnter(TObject *Sender)
{
  TEditPlus* Edit;
  TForm* Form;
  if ((Edit = dynamic_cast<TEditPlus*>(Sender)) == 0)
    return;
  if ((Form = dynamic_cast<TForm*>(Edit->Owner)) == 0)
    return;
  if (Edit->Combo != 0)
  {
    if (Edit->Combo->DroppedDown == true)
    {
      Form->ActiveControl = Edit->Combo;
      Edit->ReadOnly = true;
    }
    else
    {
      Edit->ReadOnly = false;
    }
  }
}

static int _Compare_(AnsiString One, AnsiString Two)
{
  int result;
  for (result = 1; result <= One.Length(); result++)
  {
    if (One[result] == Two[result])
      continue;
    return result;
  }
  return 0;
}

void __fastcall TMainForm::EditPlusChange(TObject *Sender)
{
  TEditPlus* Edit;
  if ((Edit = dynamic_cast<TEditPlus*>(Sender)) == 0)
    return;
  if (Edit->Combo == 0)
    return;
  int i, n;
  TStringList *Temp = new TStringList();
  try
  {
    Temp->Sorted = true;
    for (i=0; i<Edit->Combo->Items->Count; i++)
      Temp->Add(Edit->Combo->Items->Strings[i]);
    for (i=0; i<Temp->Count; i++)
    {
      n = _Compare_(Edit->Text, Temp->Strings[i]);
      if (n > 0
      && Edit->Text.AnsiCompare(Temp->Strings[i]) < 0)
        break;
      if (n == 0)
      {
        Edit->Combo->ItemIndex = i;
        return;
      }
    }
    if (Edit->Text[n] < Edit->Combo->Items->Strings[i][n]
    && i > 0)
      i--;
    Edit->Combo->ItemIndex = i;
    AnsiString Work = Edit->Text;
    Work.Delete(n, Work.Length());
    Edit->Text = Work;
    Edit->SelStart = n;
    Beep();
  }
  __finally
  {
    delete Temp;
  }
}

void __fastcall TMainForm::enumDropDown(TForm *Form, TEditPlus *Edit, int field, int top, int left, int width, AnsiString text)
{
  TComboEnum* ComboEnum = new TComboEnum(Form, field, Edit);
  Edit->Combo = ComboEnum;
  Edit->OnEnter = EditPlusEnter;
  Edit->OnChange = EditPlusChange;
  ComboEnum->Top = top;
  ComboEnum->Left = left;
  ComboEnum->Width = width;
  ComboEnum->Style = csOwnerDrawFixed;
  ComboEnum->Anchors << akRight;
  ComboEnum->OnChange = ComboEnumChange;
  ComboEnum->OnDrawItem = ComboEnumDrawItem;
  Form->InsertControl(ComboEnum);
  for (int i=0, offset = PC_Field[field].offsetEnums; i<PC_Field[field].noEnums; i++)
    ComboEnum->Items->AddObject(AnsiString(PC_Enum[i+offset].value), new TAnsiString(PC_Enum[i+offset].name));
}

bool __fastcall TMainForm::sizeIsOk(int link)
{
  try
  {
    TDBHandler db(conn);
    int table = PC_Link[link].tableNo;
    autoCursor ac(crSQLWait);
    int size = db.GetCount(PC_Table[table].name);
    return size < MaxDropdownEdit->Text.ToInt();
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
    return false;
  }
}

void __fastcall TMainForm::newDropDown(TForm *Form, TEditPlus *Edit, int fromTable, int link, int top, int left, int width, AnsiString text)
{
  try
  {
    TDBHandler db(conn);
    int table = PC_Link[link].tableNo;
    autoCursor ac(crSQLWait);
    if (PC_Table[table].noShowFields > 0)
      db.GetTable(PC_Table[table].name,
                  PC_Table[table].noFields,
                 &PC_Field[PC_Table[table].offsetFields],
                  PC_ShowField,
                  PC_Table[table].noShowFields,
                  PC_Table[table].offsetShowFields);
    else
      db.GetTable(PC_Table[table].name,
                  PC_Table[table].noFields,
                 &PC_Field[PC_Table[table].offsetFields],
                  PC_OrderField,
                  PC_Table[table].noOrderFields,
                  PC_Table[table].offsetOrderFields);
    TComboLink* ComboLink = new TComboLink(Form, link, fromTable, Edit);
    Edit->Combo = ComboLink;
    Edit->OnEnter = EditPlusEnter;
    Edit->OnChange = EditPlusChange;
    ComboLink->Top = top;
    ComboLink->Left = left;
    ComboLink->Width = width;
    ComboLink->Style = csOwnerDrawFixed;
    ComboLink->Anchors << akRight;
    ComboLink->OnChange = ComboBoxChange;
    ComboLink->OnDrawItem = ComboBoxDrawItem;
    ComboLink->OnDropDown = ComboBoxDropDown;
    Form->InsertControl(ComboLink);
    while (db.GetTableFetch())
    {
      AnsiString w = "";
      AnsiString v = "";
      bool isValue;
      for (int field=0; field<db.noFields-2; field++)
      {
        int offset = PC_Link[link].offsetLinkPairs;
        isValue = false;
        for (int pair=0; pair<PC_Link[link].noLinkPairs; pair++)
        {
          if (field == PC_LinkPair[pair+offset].toNo)
          {
            isValue = true;
            break;
          }
        }
        bool isShow = false;
        if (isValue == false && PC_Table[table].noShowFields > 0)
        {
          for (int show=0, offset = PC_Table[table].offsetShowFields; show<PC_Table[table].noShowFields; show++)
          {
            if (field == PC_ShowField[offset+show].index)
            {
              isShow = true;
              break;
            }
          }
        }
        if (isValue == true || PC_Table[table].noShowFields == 0 || isShow == true)
        {
          int type = db.fields[field].type;
          switch (type)
          {
            case TPC_CHAR:
            {
              TBChar ac(db.fields[field].length+1);
              db.GetTableField(field, ac.data);
              if (isValue)
                v = ac.data;
              else if (w.Length() == 0)
                w = ac.data;
              else if (isShow == true)
                w = w + "_" + ac.data;
              break;
            }
            case TPC_DATE:
            case TPC_DATETIME:
            case TPC_TIME:
            case TPC_TIMESTAMP:
            case TPC_USERSTAMP:
            {
              if (!isValue && !isShow)
                break;
              TBChar ac(db.fields[field].length+4);
              if (db.GetTableField(field, ac.data) == true)
                v = "";
              else if (isShow == true)
              {
                if (w.Length() == 0)
                  w = ac.data;
                else
                  w = w + "_" + ac.data;
              }
              else
                v = AnsiString(ac.data);
              break;
            }
            case TPC_BYTE:
            case TPC_SHORT:
            {
              if (!isValue && !isShow)
                break;
              short s;
              if (db.GetTableField(field, s) == true)
                v = "";
              else if (isShow == true)
              {
                if (w.Length() == 0)
                  w = s;
                else
                  w = w + "_" + s;
              }
              else
                v = AnsiString(s);
              break;
            }
            case TPC_DOUBLE:
            {
              if (!isValue && !isShow)
                break;
              double d;
              if (db.GetTableField(field, d) == true)
                v = "";
              else if (isShow == true)
              {
                if (w.Length() == 0)
                  w = d;
                else
                  w = w + "_" + d;
              }
              else
                v = AnsiString(d);
              break;
            }
            case TPC_BOOLEAN:
            case TPC_INT:
            case TPC_SEQUENCE:
            {
              if (!isValue && !isShow)
                break;
              int i;
              if (db.GetTableField(field, i) == true)
                v = "";
              else if (isShow == true)
              {
                if (w.Length() == 0)
                  w = i;
                else
                  w = w + "_" + i;
              }
              else
                v = AnsiString(i);
              break;
            }
            case TPC_LONG:
            {
              if (!isValue && !isShow)
                break;
              long l;
              if (db.GetTableField(field, l) == true)
                v = "";
              else if (isShow == true)
              {
                if (w.Length() == 0)
                  w = l;
                else
                  w = w + "_" + l;
              }
              else
                v = AnsiString(l);
              break;
            }
          }
        }
      }
      if (w.Length() == 0)
        w = v;
      ComboLink->Items->AddObject(v, new TAnsiString(w));
    }
    ComboLink->Text = text;
    ComboLink->Hint = ComboLink->Items->Count;
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

bool isKey(int table, int field)
{
  int o = PC_Table[table].offsetKeyFields;
  for (int i=0; i<PC_Table[table].noKeyFields; i++)
    if (PC_KeyField[o+i].index == field)
      return true;
  return false;
}

AnsiString __fastcall TMainForm::Escape(AnsiString data)
{
  int n = data.Pos("'");
  if (n == 0)
    return data;
  return data.SubString(1,n)+"'"+Escape(data.SubString(n+1, data.Length()-n));
}

AnsiString __fastcall TMainForm::SQLReady(TPC_Field field, AnsiString data, bool isInsert, AnsiString tableName)
{
  data = data.Trim();
  if (data.Length() == 0 && field.null == 1)
    return "NULL";
  switch (field.type)
  {
    case TPC_BOOLEAN:
    case TPC_BYTE:
    case TPC_SHORT:
    case TPC_DOUBLE:
    case TPC_INT:
    case TPC_LONG:
      return data;
    case TPC_SEQUENCE:
      if (isInsert == true)
        return tableName+"Seq.NextVal";
      return data;
    case TPC_CHAR:
    case TPC_USERSTAMP:
      return "'"+Escape(data)+"'";
    case TPC_DATE:
      return "to_date('"+data+"','YYYYMMDD')";
    case TPC_DATETIME:
      return "to_date('"+data+"','YYYYMMDDHH24MISS')";
    case TPC_TIME:
      return "to_date('"+data+"','HH24MISS')";
    case TPC_TIMESTAMP:
      return "to_date('"+data+"','YYYYMMDDHH24MISS')";
  }
  return "";
}

int __fastcall TMainForm::MaxLength(TPC_Field field)
{
  switch (field.type)
  {
    case TPC_CHAR:
    case TPC_USERSTAMP:
    case TPC_DATE:
    case TPC_DATETIME:
    case TPC_TIME:
    case TPC_TIMESTAMP:
      return field.length;
    case TPC_BOOLEAN:
      return 1;
    case TPC_BYTE:
      return 3;
    case TPC_SHORT:
      return 5;
    case TPC_DOUBLE:
      return 20;
    case TPC_SEQUENCE:
    case TPC_INT:
    case TPC_LONG:
      return 10;
  }
  return 0;
}

void __fastcall TMainForm::ParmExecute(EHow how)
{
  AnsiString SetupWork;
  if (AllowUpdates == false)
    return;
  int ComboWidth;
  int EditWidth;
  AnsiString where = "WHERE ", c = "", query;
  int table = TableIndex;
  int row = TableDataGrid->Row;
  int offset = PC_Table[table].offsetFields;
  TDBHandler db(conn);SetDB(db);
  Form<TEntryForm> af(this);
  TBuffer<TEditPlus*> edits(PC_Table[table].noFields);
  for (int kfield=0; kfield < PC_Table[table].noKeyFields; kfield++)
  {
    int okfield = kfield+PC_Table[table].offsetKeyFields;
    int field = PC_KeyField[okfield].index;
    where += c + PC_Field[offset+field].name+" = "
            + SQLReady(PC_Field[offset+field], TableDataGrid->Cells[field][row]);
    c = " AND ";
  }
  ComboWidth = af.form->ClientWidth-132;
  EditWidth  = ComboWidth-18;
  af.form->Constraints->MinHeight = PC_Table[table].noFields*24+66;
  for (int field=0; field<PC_Table[table].noFields; field++)
  {
    TLabel *Label = new TLabel(af.form);
    Label->Caption = PC_Field[field+offset].name;
    Label->Top = field * 24 + 4;
    Label->Left = 4;
    af.form->InsertControl(Label);
    if (ShowSetup->Checked)
      LogMemo->Lines->Add(Label->Caption + " " + AnsiString(Label->Top) + " " + AnsiString(Label->Left));
    int link;
    bool hasOverlay = false;
    TEditPlus *Edit = edits.data[field] = new TEditPlus(af.form);
    Edit->Name = "Edit"+AnsiString(field);
    Edit->Label = Label;
    if (how != xADD || ClearAdd->Checked == false)
      Edit->Text = TableDataGrid->Cells[field][row];
    else
      Edit->Text = "";
    if (PC_Field[field+offset].uppercase == 1 || isKey(table, field) == true)
      Edit->CharCase = ecUpperCase;
    Edit->Top = field * 24 + 4;
    Edit->Left = 124;
    Edit->Width = EditWidth;
    Edit->Anchors << akRight;
    Edit->type = PC_Field[field+offset].type;
    Edit->null = PC_Field[field+offset].null;
    Edit->MaxLength = MaxLength(PC_Field[field+offset]);
    if (ShowSetup->Checked)
      SetupWork = Edit->Name;
    if (how == xADD || (how == xCHANGE && isKey(table, field) == false))
    {
      if (setupLink(table, field, link))
      {
        if (ShowSetup->Checked && link >= 0)
          SetupWork += " Is Link Field";
        if (link >= 0 && sizeIsOk(link))
        {
          if (ShowSetup->Checked)
            SetupWork += " and is not too large";
          hasOverlay = true;
          newDropDown(af.form, Edit, table, link, field*24+4, 124, ComboWidth, TableDataGrid->Cells[field][row]);
        }
      }
      else if (PC_Field[field+offset].noEnums > 0)
      {
        if (ShowSetup->Checked)
          SetupWork += " Is Enum Field";
        hasOverlay = true;
        enumDropDown(af.form, Edit, field+offset, field*24+4, 124, ComboWidth, TableDataGrid->Cells[field][row]);
      }
      else switch (PC_Field[field+offset].type)
      {
      case TPC_DATE:
        if (ShowSetup->Checked)
          SetupWork += " Is Date Field";
        hasOverlay = true;
        datePicker(af.form, Edit, field*24+4, 124, ComboWidth, TableDataGrid->Cells[field][row], dewDate);
        break;
      case TPC_DATETIME:
      case TPC_TIMESTAMP:
        if (ShowSetup->Checked)
          SetupWork += " Is Date Time Field";
        hasOverlay = true;
        Edit->Width = Edit->Width-18;
        datePicker(af.form, Edit, field*24+4, 124, ComboWidth, TableDataGrid->Cells[field][row], dewTimeOf);
        datePicker(af.form, Edit, field*24+4, 124, ComboWidth-18, TableDataGrid->Cells[field][row], dewDateOf);
        break;
      case TPC_TIME:
        if (ShowSetup->Checked)
          SetupWork += " Is Time Field";
        hasOverlay = true;
        datePicker(af.form, Edit, field*24+4, 124, ComboWidth, TableDataGrid->Cells[field][row], dewTime);
        break;
      }
    }
    if (hasOverlay
    || (how != xADD && (how == xDELETE || how == xDISPLAY || isKey(table, field) == true))
    || (how == xADD && PC_Field[field+offset].type == TPC_SEQUENCE)
    )
    {
      Edit->ReadOnly = true;
      Edit->Color = clLtGray;
      Edit->CharCase = ecUpperCase;
      Edit->TabStop = false;
      if (how == xADD && PC_Field[field+offset].type == TPC_SEQUENCE)
         Edit->Text = "SEQUENCE";
    }
    af.form->InsertControl(Edit);
    if (ShowSetup->Checked)
      LogMemo->Lines->Add(SetupWork);
  }
  switch (how)
  {
    case xADD:
      af.form->Caption = "Add Record for "+AnsiString(PC_Table[table].name);
      af.form->isAdd     = true;
      af.form->isChange  = false;
      af.form->isDelete  = false;
      af.form->isDisplay = false;
      break;
    case xCHANGE:
      af.form->Caption = "Change Record on "+AnsiString(PC_Table[table].name);
      af.form->isAdd    = false;
      af.form->isChange = true;
      af.form->isDelete = false;
      af.form->isDisplay = false;
      break;
    case xDELETE:
      af.form->Caption = "Delete Record in "+AnsiString(PC_Table[table].name);
      af.form->isAdd    = false;
      af.form->isChange = false;
      af.form->isDelete = true;
      af.form->isDisplay = false;
      break;
    case xDISPLAY:
      af.form->Caption = "Display Record of "+AnsiString(PC_Table[table].name);
      af.form->isAdd    = false;
      af.form->isChange = false;
      af.form->isDelete = false;
      af.form->isDisplay = true;
      break;
  }
  int rc = af.form->ShowModal();
  if (rc != mrOk || how == xDISPLAY)
    return;
  TBuffer<AnsiString> cells(PC_Table[table].noFields);
  for (int field=0; field<PC_Table[table].noFields; field++)
    cells.data[field] = edits.data[field]->Text;
  switch (how)
  {
    case xADD:
      query = "INSERT INTO "+AnsiString(PC_Table[table].name);
      query += " VALUES ";
      c = "( ";
      for (int field=0; field<PC_Table[table].noFields; field++)
      {
        query += c + SQLReady(PC_Field[offset+field], cells.data[field], true, AnsiString(PC_Table[table].name));
        c = ", ";
      }
      query += ", '"+UserID+"', Sysdate)";
      break;
    case xCHANGE:
      query = "UPDATE "+AnsiString(PC_Table[table].name);
      query += " SET ";
      c = "";
      for (int field=0; field<PC_Table[table].noFields; field++)
      {
        if (isKey(table, field) == true)
          continue;
        query += c + AnsiString(PC_Field[offset+field].name) + " = "
                   + SQLReady(PC_Field[offset+field], cells.data[field]);
        c = ", ";
      }
      query += ", "+db.UsId+" = '"+UserID+"', "+db.TmStamp+" = Sysdate";
      query += " "+where;
      break;
    case xDELETE:
      query = "DELETE FROM "+AnsiString(PC_Table[table].name);
      query += " "+where;
      break;
  }
  try
  {
    if (ShowSQL->Checked == true)
      LogMemo->Lines->Add(query);
    db.Execute(query);
    conn.Commit();
    ListOfTablesDblClick(0);
    if (row < TableDataGrid->RowCount)
      TableDataGrid->Row = row;
    else
      TableDataGrid->Row = TableDataGrid->RowCount - 1;
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::AddParmExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  ParmExecute(xADD);
}

void __fastcall TMainForm::ChangeParmExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  ParmExecute(xCHANGE);
}

void __fastcall TMainForm::DelParmExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  ParmExecute(xDELETE);
}

void __fastcall TMainForm::DisplayParmExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  ParmExecute(xDISPLAY);
}

void __fastcall TMainForm::ComboBoxDrawItem(TWinControl *Control,
      int Index, const TRect &Rect, const TOwnerDrawState State)
{
  TColor   Color;
  TComboLink* ComboLink;
  if ((ComboLink = dynamic_cast<TComboLink*>(Control)) == 0)
    return;
  TAnsiString *w = dynamic_cast<TAnsiString*>(ComboLink->Items->Objects[Index]);
  AnsiString display = w->value;
  Color = clNavy;
  if (State.Contains(odSelected))
  {
    Color = TColor(0xE0E8FF);
    display = display + "=" + ComboLink->Items->Strings[Index];
  }
  ComboLink->Canvas->FillRect(Rect);
  ComboLink->Canvas->Font->Color = Color;
  ComboLink->Canvas->TextOut(Rect.Left + 2, Rect.Top, display);
}

void __fastcall TMainForm::ComboBoxDropDown(TObject *Sender)
{
  //TComboLink* ComboLink;
  if ((/*ComboLink =*/ dynamic_cast<TComboLink*>(Sender)) == 0)
    return;
}

void __fastcall TMainForm::ComboBoxChange(TObject *Sender)
{
  TComboLink* ComboLink;
  if ((ComboLink = dynamic_cast<TComboLink*>(Sender)) == 0)
    return;
//  if (ComboLink->DroppedDown == false)
//    ComboLink->Style = csDropDown;
  ComboLink->Edit->Text = ComboLink->Items->Strings[ComboLink->ItemIndex];
}

void __fastcall TMainForm::ComboEnumDrawItem(TWinControl *Control,
      int Index, const TRect &Rect, const TOwnerDrawState State)
{
  TColor   Color;
  TComboEnum* ComboEnum;
  if ((ComboEnum = dynamic_cast<TComboEnum*>(Control)) == 0)
    return;
  TAnsiString *w = dynamic_cast<TAnsiString*>(ComboEnum->Items->Objects[Index]);
  AnsiString display = w->value;
  Color = clNavy;
  if (State.Contains(odSelected))
  {
    Color = TColor(0xE0E8FF);
    display = display + "=" + ComboEnum->Items->Strings[Index];
  }
  ComboEnum->Canvas->FillRect(Rect);
  ComboEnum->Canvas->Font->Color = Color;
  ComboEnum->Canvas->TextOut(Rect.Left + 2, Rect.Top, display);
}

void __fastcall TMainForm::ComboEnumChange(TObject *Sender)
{
  TComboEnum* ComboEnum;
  TForm* Form;
  if ((ComboEnum = dynamic_cast<TComboEnum*>(Sender)) == 0)
    return;
  ComboEnum->Edit->Text = ComboEnum->Items->Strings[ComboEnum->ItemIndex];
}

void __fastcall TMainForm::DateEditChange(TObject *Sender)
{
  TDateEdit* DateEdit;
  TForm* Form;
  if ((DateEdit = dynamic_cast<TDateEdit*>(Sender)) == 0)
    return;
  AnsiString Date = DateEdit->Date.FormatString("yyyymmdd");
  AnsiString Time = DateEdit->Time.FormatString("hhnnss");
  switch (DateEdit->what)
  {
    case dewDate:
      DateEdit->Edit->Text = Date;
      break;
    case dewDateOf:
      DateEdit->Edit->Text = Date + DateEdit->Edit->Text.SubString(9,6);
      break;
    case dewTime:
      DateEdit->Edit->Text = Time;
      break;
    case dewTimeOf:
      DateEdit->Edit->Text = DateEdit->Edit->Text.SubString(1,8)+Time;
      break;
  }
}

void __fastcall TMainForm::LookupComboChange(TObject *Sender)
{
  int table = TableIndex;
  QRegistry->LookupKey[AnsiString(PC_Table[table].name)] = LookupCombo->Text.Trim();
  ValueCombo->Clear();
}

void __fastcall TMainForm::ValueComboDropDown(TObject *Sender)
{
  if (ValueCombo->Items->Count > 0)
    return;
  int field;
  int table = TableIndex;
  for (field = 0; field < PC_Table[table].noFields; field++)
  {
    if (LookupCombo->Text.AnsiCompare(PC_Field[field+PC_Table[table].offsetFields].name) == 0)
      break;
  }
  if (field >= PC_Table[table].noFields)
    return;
  field += PC_Table[table].offsetFields;
  try
  {
    TDBHandler db(conn);
    autoCursor ac(crSQLWait);
    db.GetDistinctList(PC_Table[table].name, PC_Field[field]);
    ValueCombo->Clear();
    while (db.GetTableFetch())
      ValueCombo->Items->Add(GetField(db, 0));
    ValueCombo->Hint = ValueCombo->Items->Count;
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::ValueComboChange(TObject *Sender)
{
  int table = TableIndex;
  QRegistry->LookupValue[AnsiString(PC_Table[table].name)] = ValueCombo->Text.Trim();
  try
  {
    TDBHandler db(conn); SetDB(db);
    db.Lookup = "";
    AnsiString C = "WHERE";
    if (MaxRowsEdit->Text.ToInt() > 0)
    {
      db.Lookup += C + " RowNum <= "+MaxRowsEdit->Text;
      C = "AND";
    }
    AnsiString value = ValueCombo->Text.Trim();
    int p = value.Pos("%");
    if (p==0) p = value.Pos("_");
    if (value.Length() > 0)
      db.Lookup += C+" "+LookupCombo->Text+(p==0?"=":" LIKE ")+"'"+ValueCombo->Text.Trim()+"'";
    QRegistry->Lookup[AnsiString(PC_Table[TableIndex].name)] = db.Lookup;
    autoCursor ac(crSQLWait);
    db.GetTable(PC_Table[table].name,
                PC_Table[table].noFields,
               &PC_Field[PC_Table[table].offsetFields],
                PC_OrderField,
                PC_Table[table].noOrderFields,
                PC_Table[table].offsetOrderFields);
    FillInGrid(db, TableDataGrid);
  }
  catch (TOciApiException &x)
  {
    MessageBox(x);
  }
}

void __fastcall TMainForm::MaxRowsEditChange(TObject *Sender)
{
  QRegistry->MaxRows[AnsiString(PC_Table[TableIndex].name)] = MaxRowsEdit->Text;
}

void __fastcall TMainForm::MaxDropdownEditChange(TObject *Sender)
{
  QRegistry->MaxDropdown = MaxDropdownEdit->Text;
}

void __fastcall TMainForm::LikeEditChange(TObject *Sender)
{
  QRegistry->Like[AnsiString(PC_Table[TableIndex].name)] = LikeEdit->Text;
}

void __fastcall TMainForm::CutGridToClipBoard(TStringGrid *DataGrid, bool All)
{
  AnsiString Line;
  LogMemo->Clear();
  TGridRect Rect = DataGrid->Selection;
  if (All)
  {
    Rect.Top = 1;
    Rect.Bottom = DataGrid->RowCount-1;
    Rect.Left = 0;
    Rect.Right = DataGrid->ColCount-1;
  }
  for (int row=Rect.Top; row<=Rect.Bottom; row++)
  {
    AnsiString T="";
    Line = "";
    for (int col=Rect.Left; col<=Rect.Right; col++)
    {
      Line += T+DataGrid->Cells[col][row];
      T = "\t";
    }
    LogMemo->Lines->Add(Line);
  }
  LogMemo->SelectAll();
  LogMemo->CutToClipboard();
}

void __fastcall TMainForm::CopyAllExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  CutGridToClipBoard(TableDataGrid, true);
}

void __fastcall TMainForm::CopySelectionExecute(TObject *Sender)
{
  autoCursor ac(crHourGlass);
  CutGridToClipBoard(TableDataGrid, false);
}

void __fastcall TMainForm::TableDataGridColumnMoved(TObject *Sender,
      int FromIndex, int ToIndex)
{
  AllowUpdates = false;
  SetUpdateActions();
}

void __fastcall TMainForm::ShowSQLClick(TObject *Sender)
{
  QRegistry->ShowSQL = ShowSQL->Checked;
}

void __fastcall TMainForm::ShowBinClick(TObject *Sender)
{
  QRegistry->ShowBin = ShowBin->Checked;
}

void __fastcall TMainForm::ShowSetupClick(TObject *Sender)
{
  QRegistry->ShowSetup = ShowSetup->Checked;
}

static AnsiString padIt(AnsiString data, int length)
{
  AnsiString result = "";
  for (int i=0; i<(length-data.Length()); i++)
    result += " ";
  return result+data;
}

void __fastcall TMainForm::SortGrid(TStringGrid* grid, bool asc)
{
  int col = grid->Col;
  if (col < 0 || col >= grid->ColCount)
    return;
  AnsiString data;
  TStringList *list = new TStringList();
  try
  {
    int length = 0;
    bool doPad = true;
    for (int r=1; r<grid->RowCount; r++)
    {
      AnsiString d = grid->Cells[col][r];
      if (d.Length() > length) length = d.Length();
      for (int i=0; i<d.Length(); i++)
      {
        if (AnsiString("0123456789.").Pos(d[i+1]) == 0)
        {
          doPad = false;
          break;
        }
      }
      if (doPad == false)
        break;
    }
    for (int r=1; r<grid->RowCount; r++)
    {
      AnsiString d = grid->Cells[col][r];
      data = "{]"+(doPad?padIt(d,length):d)+"[}";
      for (int c=0; c<grid->ColCount; c++)
      {
        if (c == col)
          continue;
        data += "{]"+grid->Cells[c][r]+"[}";
      }
      list->Add(data);
    }
    list->Sorted = true;
    for (int l=0; l<list->Count; l++)
    {
      data = list->Strings[l];
      int r= asc?l+1:list->Count-l;
      int p=data.Pos("[}");
      AnsiString d=data.SubString(3, p-3);
      grid->Cells[col][r] = doPad?d.Trim():d;
      data = data.SubString(p+2, data.Length()-p+3);
      for (int c=0; c<grid->ColCount; c++)
      {
        if (c == col)
          continue;
        p=data.Pos("[}");
        grid->Cells[c][r] = data.SubString(3, p-3);
        data = data.SubString(p+2, data.Length()-p+3);
      }
    }
  }
  __finally
  {
    delete list;
  }
}

void __fastcall TMainForm::SortAscendingExecute(TObject *Sender)
{
  SortGrid(TableDataGrid, true);
}

void __fastcall TMainForm::SortDescendingExecute(TObject *Sender)
{
  SortGrid(TableDataGrid, false);
}



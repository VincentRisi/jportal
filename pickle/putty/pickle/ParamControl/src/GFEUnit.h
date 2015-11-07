#ifndef GFEUnitH
#define GFEUnitH

#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <Menus.hpp>
#include <ComCtrls.hpp>
#include <Grids.hpp>
#include <ExtCtrls.hpp>
#include <ActnList.hpp>
#include <ImgList.hpp>
#include <ToolWin.hpp>
#include <Buttons.hpp>

#ifdef _UPM_AWARE
#include "WSSecure.hpp"
#endif

#include "QRegistry.h"
#include "BinTables.h"
#include <Buttons.hpp>
#include "GFEEntryUnit.h"
#include "ParmGFEConfig.h"

class TComboLink : public TComboBox
{
public:
  int link;
  int table;
  TEditPlus *Edit;
  TComboLink(TComponent* AOwner, int aLink, int aTable, TEditPlus *AEdit)
  : TComboBox(AOwner)
  , link(aLink)
  , table(aTable)
  , Edit(AEdit)
  {}
};

class TComboEnum : public TComboBox
{
public:
  int field;
  TEditPlus *Edit;
  TComboEnum(TComponent* AOwner, int aField, TEditPlus *AEdit)
  : TComboBox(AOwner)
  , field(aField)
  , Edit(AEdit)
  {}
};

class TAnsiString : public TObject
{
public:
  AnsiString value;
  TAnsiString(AnsiString AValue)
  : value(AValue)
  {}
};

enum EDEWhat {dewDate, dewTime, dewDateOf, dewTimeOf};
class TDateEdit : public TDateTimePicker
{
public:
  EDEWhat what;
  TEdit *Edit;
  TDateEdit(TComponent* AOwner, TEdit *aEdit, EDEWhat aWhat=dewDate)
  : TDateTimePicker(AOwner)
  , Edit(aEdit)
  , what(aWhat)
  {}
};

class TMainForm : public TForm
{
__published:	// IDE-managed Components
  TPageControl *Pages;
  TTabSheet *Tables;
  TTabSheet *Relations;
  TSplitter *TableSplitter;
  TStringGrid *TableDataGrid;
  TSplitter *RelationSplitter;
  TStringGrid *RelationDataGrid;
  TStatusBar *StatusBar;
  TPopupMenu *TablesPopup;
  TMenuItem *AddParmMenuItem;
  TMenuItem *MenuChangeParm;
  TMenuItem *MenuDeleteParm;
  TActionList *ActionList;
  TAction *AddParm;
  TAction *ChangeParm;
  TAction *DelParm;
  TAction *AddReln;
  TAction *DelReln;
  TImageList *ActionImages;
  TPopupMenu *RelationsPopup;
  TMenuItem *AddRelnMenuItem;
  TMenuItem *DeleteRelnMenuItem;
  TToolBar *ToolBar1;
  TToolButton *ToolButton1;
  TToolButton *ToolButton2;
  TToolButton *ToolButton3;
  TPanel *Panel1;
  TListBox *ListOfTables;
  TPanel *Panel2;
  TListBox *ListOfRelations;
  TSplitter *Splitter1;
  TPanel *Panel3;
  TPanel *Panel4;
  TLabel *MaxRowsLabel;
  TEdit *MaxRowsEdit;
  TLabel *LookupLabel;
  TLabel *ValueLabel;
  TComboBox *LookupCombo;
  TComboBox *ValueCombo;
  TLabel *LikeLabel;
  TEdit *LikeEdit;
  TTabSheet *LogTab;
  TMemo *LogMemo;
  TButton *DOButton;
  TAction *CopyAll;
  TToolButton *ToolButton4;
  TCheckBox *ClearAdd;
  TToolButton *ToolButton5;
  TAction *DisplayParm;
  TPopupMenu *CopyPopup;
  TMenuItem *All1;
  TMenuItem *Selection1;
  TAction *CopySelection;
  TComboBox *LeftRelationCombo;
  TComboBox *RightRelationCombo;
  TListBox *AvailableRelationList;
  TListBox *SelectedRelationList;
  TSpeedButton *ToSelected;
  TSpeedButton *FromSelected;
  TButton *DOValueButton;
  TLabel *LeftTableLabel;
  TLabel *RightTableLabel;
  TLabel *Label3;
  TLabel *Label4;
  TSplitter *Splitter2;
  TSpeedButton *AllToSelected;
  TSpeedButton *AllFromSelected;
  TPanel *ShowPanel;
  TCheckBox *ShowSQL;
  TCheckBox *ShowBin;
  TCheckBox *ShowSetup;
  TLabel *Label1;
  TEdit *MaxDropdownEdit;
  TAction *SortAscending;
  TMenuItem *Sort1;
  TAction *SortDescending;
  TMenuItem *SortDescending1;
  void __fastcall FormCreate(TObject *Sender);
  void __fastcall ListOfTablesDblClick(TObject *Sender);
  void __fastcall ListOfRelationsDblClick(TObject *Sender);
  void __fastcall FormDestroy(TObject *Sender);
  void __fastcall RelationDataGridDblClick(TObject *Sender);
  void __fastcall AddParmExecute(TObject *Sender);
  void __fastcall ChangeParmExecute(TObject *Sender);
  void __fastcall DelParmExecute(TObject *Sender);
  void __fastcall FormActivate(TObject *Sender);
  void __fastcall LookupChange(TObject *Sender);
  void __fastcall LookupComboChange(TObject *Sender);
  void __fastcall ValueComboChange(TObject *Sender);
  void __fastcall MaxRowsEditChange(TObject *Sender);
  void __fastcall LikeEditChange(TObject *Sender);
  void __fastcall CopyAllExecute(TObject *Sender);
  void __fastcall DisplayParmExecute(TObject *Sender);
  void __fastcall CopySelectionExecute(TObject *Sender);
  void __fastcall LeftRelationComboChange(TObject *Sender);
  void __fastcall RightRelationComboChange(TObject *Sender);
  void __fastcall ToSelectedClick(TObject *Sender);
  void __fastcall FromSelectedClick(TObject *Sender);
  void __fastcall AllToSelectedClick(TObject *Sender);
  void __fastcall AllFromSelectedClick(TObject *Sender);
  void __fastcall ValueComboDropDown(TObject *Sender);
  void __fastcall TableDataGridColumnMoved(TObject *Sender, int FromIndex,
          int ToIndex);
  void __fastcall MaxDropdownEditChange(TObject *Sender);
  void __fastcall ShowSQLClick(TObject *Sender);
  void __fastcall ShowBinClick(TObject *Sender);
  void __fastcall ShowSetupClick(TObject *Sender);
  void __fastcall SortAscendingExecute(TObject *Sender);
  void __fastcall SortDescendingExecute(TObject *Sender);
private:	// User declarations
  bool FirstTime;
  bool AllowUpdates;
  int  TableIndex;
  int  RelationIndex;
  int  RelationFromCol;
  int  RelationNoFromCols;
  int  RelationToCol;
  int  RelationNoToCols;
  AnsiString RelationFromValue;
  AnsiString UserID;
  TJConnector conn;
  TQRegistry *QRegistry;
  void __fastcall ClearDataGrid(TStringGrid *DataGrid);
  void __fastcall CutGridToClipBoard(TStringGrid *DataGrid, bool All=false);
  AnsiString __fastcall GetField(TDBHandler &db, int field);
  void __fastcall FillInGrid(TDBHandler &db, TStringGrid *DataGrid);
  bool __fastcall sizeIsOk(int link);
  void __fastcall newDropDown(TForm *form, TEditPlus* edit, int fromTable, int link, int top, int left, int width, AnsiString text);
  void __fastcall enumDropDown(TForm *form, TEditPlus* edit, int field, int top, int left, int width, AnsiString text);
  void __fastcall datePicker(TForm *form, TEdit* edit, int top, int left, int width, AnsiString text, EDEWhat what);
  enum EHow {xADD, xCHANGE, xDELETE, xDISPLAY};
  AnsiString __fastcall Escape(AnsiString data);
  AnsiString __fastcall SQLReady(TPC_Field field, AnsiString data, bool isInsert=false, AnsiString tableName="");
  int __fastcall MaxLength(TPC_Field field);
  void __fastcall ParmExecute(EHow how);
  void __fastcall ComboBoxDrawItem(TWinControl *Control, int Index,
          const TRect &Rect, const TOwnerDrawState State);
  void __fastcall ComboEnumDrawItem(TWinControl *Control, int Index,
          const TRect &Rect, const TOwnerDrawState State);
  void __fastcall ComboBoxDropDown(TObject *Sender);
  void __fastcall ComboBoxChange(TObject *Sender);
  void __fastcall ComboEnumChange(TObject *Sender);
  void __fastcall EditPlusEnter(TObject *Sender);
  void __fastcall EditPlusChange(TObject *Sender);
  void __fastcall DateEditChange(TObject *Sender);
  void __fastcall MessageBox(TOciApiException &x);
  void __fastcall PopulateCombo(TComboBox *Combo, int tableIndex);
  void __fastcall FillInRelationLists(AnsiString FromValue, TComboBox *ToCombo, int FromCol, int noFromCols, int ToCol, int noToCols);
  void __fastcall SetUpdateActions();
  void __fastcall LogParameters();
  void __fastcall SetDB(TDBHandler &db);
  void __fastcall SortGrid(TStringGrid* grid, bool asc);
  void __fastcall SetLog(AnsiString value) {LogMemo->Lines->Add(value);}
public:		// User declarations
  __property AnsiString Log = {write = SetLog};
  __fastcall TMainForm(TComponent* Owner);
};

extern PACKAGE TMainForm *MainForm;

#endif

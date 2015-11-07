#ifndef GFEEntryUnitH
#define GFEEntryUnitH

#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>

class TEditPlus : public TEdit
{
public:
  bool null;
  int type;
  TComboBox* Combo;
  TLabel* Label;
  __fastcall TEditPlus(TComponent* AOwner)
  : TEdit(AOwner)
  , Combo(0)
  , Label(0)
  {}
};

class TEntryForm : public TForm
{
__published:	// IDE-managed Components
  TButton *OkButton;
  TButton *CancelButton;
  void __fastcall FormCreate(TObject *Sender);
  void __fastcall FormActivate(TObject *Sender);
  void __fastcall OkButtonClick(TObject *Sender);
private:	// User declarations
  bool __fastcall InValidBoolean(AnsiString label, TEditPlus *edit);
  bool __fastcall InValidInteger(AnsiString label, TEditPlus *edit, int length);
  bool __fastcall InValidDate(AnsiString label, TEditPlus *edit);
  bool __fastcall InValidChar(AnsiString label, TEditPlus *edit);
  bool __fastcall InValidDateTime(AnsiString label, TEditPlus *edit);
  bool __fastcall InValidDouble(AnsiString label, TEditPlus *edit);
  bool __fastcall InValidTime(AnsiString label, TEditPlus *edit);
public:		// User declarations
  bool isAdd;
  bool isChange;
  bool isDelete;
  bool isDisplay;
  bool firstTime;
  __fastcall TEntryForm(TComponent* Owner);
};

extern PACKAGE TEntryForm *EntryForm;

#endif

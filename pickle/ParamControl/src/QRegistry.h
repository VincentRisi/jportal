#ifndef QRegistryH
#define QRegistryH

#include <vcl\Forms.hpp>
#include <vcl\registry.hpp>

#define GFE_DATABASE  "Database"
#define GFE_GENERAL   "General"
#define GFE_SELECTION "Selection"
#define GFE_SCREEN    "Screen"

class TQRegistry
{
  AnsiString Root;
  TRegistry *Registry;
  AnsiString GetUserID() {return ReadString(GFE_DATABASE, "UserID", "scott");}
  AnsiString GetPassword() {return ReadString(GFE_DATABASE, "Password", "tiger");}
  AnsiString GetServer() {return ReadString(GFE_DATABASE, "Server", "orcl");}
  int GetMaximumRows() {return ReadInteger(GFE_SELECTION, "Max Rows", 20);}
  void SetMaximumRows(int Value) {WriteInteger(GFE_SELECTION, "Max Rows", Value);}
  int GetLeft() {return ReadInteger(GFE_SCREEN, "Left", 80);}
  void SetLeft(int Value) {WriteInteger(GFE_SCREEN, "Left", Value);}
  int GetTop() {return ReadInteger(GFE_SCREEN, "Top", 60);}
  void SetTop(int Value) {WriteInteger(GFE_SCREEN, "Top", Value);}
  int GetHeight() {return ReadInteger(GFE_SCREEN, "Height", 480);}
  void SetHeight(int Value) {WriteInteger(GFE_SCREEN, "Height", Value);}
  int GetWidth() {return ReadInteger(GFE_SCREEN, "Width", 640);}
  void SetWidth(int Value) {WriteInteger(GFE_SCREEN, "Width", Value);}
  AnsiString GetLookup(AnsiString Section) {return ReadString(Section, "Lookup", "");}
  void SetLookup(AnsiString Section, AnsiString Value) {WriteString(Section, "Lookup", Value);}
  AnsiString GetLike(AnsiString Section) {return ReadString(Section, "Like", "");}
  void SetLike(AnsiString Section, AnsiString Value) {WriteString(Section, "Like", Value);}
  AnsiString GetMaxRows(AnsiString Section) {return ReadString(Section, "MaxRows", "200");}
  void SetMaxRows(AnsiString Section, AnsiString Value) {WriteString(Section, "MaxRows", Value);}
  AnsiString GetMaxDropdown() {return ReadString(GFE_SELECTION, "MaxDropdown", "1024");}
  void SetMaxDropdown(AnsiString Value) {WriteString(GFE_SELECTION, "MaxDropdown", Value);}
  AnsiString GetLookupKey(AnsiString Section) {return ReadString(Section, "LookupKey", "");}
  void SetLookupKey(AnsiString Section, AnsiString Value) {WriteString(Section, "LookupKey", Value);}
  AnsiString GetLookupValue(AnsiString Section) {return ReadString(Section, "LookupValue", "");}
  void SetLookupValue(AnsiString Section, AnsiString Value) {WriteString(Section, "LookupValue", Value);}
  AnsiString GetUsId(AnsiString Section) {return ReadString(Section, "UsId", "UsId");}
  void SetUsId(AnsiString Section, AnsiString Value) {WriteString(Section, "UsId", Value);}
  AnsiString GetTmStamp(AnsiString Section) {return ReadString(Section, "TmStamp", "TmStamp");}
  void SetTmStamp(AnsiString Section, AnsiString Value) {WriteString(Section, "TmStamp", Value);}
  bool GetShowPanel() {return ReadBool(GFE_SELECTION, "ShowPanel", false);}
  bool GetShowSQL() {return ReadBool(GFE_SELECTION, "ShowSQL", false);}
  void SetShowSQL(bool value) {WriteBool(GFE_SELECTION, "ShowSQL", value);}
  bool GetShowBin() {return ReadBool(GFE_SELECTION, "ShowBin", false);}
  void SetShowBin(bool value) {WriteBool(GFE_SELECTION, "ShowBin", value);}
  bool GetShowSetup() {return ReadBool(GFE_SELECTION, "ShowSetup", false);}
  void SetShowSetup(bool value) {WriteBool(GFE_SELECTION, "ShowSetup", value);}
public:
  TQRegistry(AnsiString ARoot) {Registry = new TRegistry();LastSection = "";Root = ARoot;}
  ~TQRegistry() {delete Registry;}
  AnsiString LastSection;
  void Section(AnsiString Name);
  int ReadInteger(AnsiString Name, int Default);
  double ReadFloat(AnsiString Name, double Default);
  bool ReadBool(AnsiString Name, bool Default);
  AnsiString ReadString(AnsiString Name, AnsiString Default);
  int ReadInteger(AnsiString ASection, AnsiString Name, int Default);
  double ReadFloat(AnsiString ASection, AnsiString Name, double Default);
  bool ReadBool(AnsiString ASection, AnsiString Name, bool Default);
  AnsiString ReadString(AnsiString ASection, AnsiString Name, AnsiString Default);
  void WriteInteger(AnsiString Name, int Value);
  void WriteFloat(AnsiString Name, double Value);
  void WriteBool(AnsiString Name, bool Value);
  void WriteString(AnsiString Name, AnsiString Value);
  void WriteInteger(AnsiString Section, AnsiString Name, int Value);
  void WriteFloat(AnsiString Section, AnsiString Name, double Value);
  void WriteBool(AnsiString Section, AnsiString Name, bool Value);
  void WriteString(AnsiString ASection, AnsiString Name, AnsiString Value);
  __property AnsiString UserID = {read = GetUserID};
  __property AnsiString Password = {read = GetPassword};
  __property AnsiString Server = {read = GetServer};
  __property int MaximumRows = {read = GetMaximumRows, write = SetMaximumRows};
  __property int Left = {read = GetLeft, write = SetLeft};
  __property int Top = {read = GetTop, write = SetTop};
  __property int Height = {read = GetHeight, write = SetHeight};
  __property int Width = {read = GetWidth, write = SetWidth};
  __property AnsiString Lookup[AnsiString Section] = {read = GetLookup, write = SetLookup};
  __property AnsiString Like[AnsiString Section] = {read = GetLike, write = SetLike};
  __property AnsiString MaxRows[AnsiString Section] = {read = GetMaxRows, write = SetMaxRows};
  __property AnsiString LookupKey[AnsiString Section] = {read = GetLookupKey, write = SetLookupKey};
  __property AnsiString LookupValue[AnsiString Section] = {read = GetLookupValue, write = SetLookupValue};
  __property AnsiString MaxDropdown = {read = GetMaxDropdown, write = SetMaxDropdown};
  __property AnsiString UsId[AnsiString Section] = {read = GetUsId, write = SetUsId};
  __property AnsiString TmStamp[AnsiString Section] = {read = GetTmStamp, write = SetTmStamp};
  __property bool ShowPanel = {read = GetShowPanel};
  __property bool ShowSQL = {read = GetShowSQL, write = SetShowSQL};
  __property bool ShowBin = {read = GetShowBin, write = SetShowBin};
  __property bool ShowSetup = {read = GetShowSetup, write = SetShowSetup};
};

#endif

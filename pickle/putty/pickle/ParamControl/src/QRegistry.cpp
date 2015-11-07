#include <vcl.h>
#pragma hdrstop

#include "QRegistry.h"

#pragma package(smart_init)

void TQRegistry::Section(AnsiString Name)
{
  AnsiString FullName = Root + "\\" + Name;
  if (FullName.AnsiCompare(LastSection) == 0)
    return;
  LastSection = FullName;
  Registry->RootKey = HKEY_CURRENT_USER;
  Registry->OpenKey(FullName, true);
}

int TQRegistry::ReadInteger(AnsiString Name, int Default)
{
  try
  {
    return Registry->ReadInteger(Name);
  }
  catch (...)
  {
    Registry->WriteInteger(Name, Default);
    return Default;
  }
}

double TQRegistry::ReadFloat(AnsiString Name, double Default)
{
  try
  {
    return Registry->ReadFloat(Name);
  }
  catch (...)
  {
    Registry->WriteFloat(Name, Default);
    return Default;
  }
}

bool TQRegistry::ReadBool(AnsiString Name, bool Default)
{
  try
  {
    return Registry->ReadBool(Name);
  }
  catch (...)
  {
    Registry->WriteBool(Name, Default);
    return Default;
  }
}

AnsiString TQRegistry::ReadString(AnsiString Name, AnsiString Default)
{
  try
  {
    AnsiString Read = Registry->ReadString(Name);
    if (Read == "")
    {
      Registry->WriteString(Name, Default);
      return Default;
    }
    return Read;
  }
  catch (...)
  {
    Registry->WriteString(Name, Default);
    return Default;
  }
}

int TQRegistry::ReadInteger(AnsiString ASection, AnsiString Name, int Default)
{
  Section(ASection);
  return ReadInteger(Name, Default);
}

double TQRegistry::ReadFloat(AnsiString ASection, AnsiString Name, double Default)
{
  Section(ASection);
  return ReadFloat(Name, Default);
}

bool TQRegistry::ReadBool(AnsiString ASection, AnsiString Name, bool Default)
{
  Section(ASection);
  return ReadBool(Name, Default);
}

AnsiString TQRegistry::ReadString(AnsiString ASection, AnsiString Name, AnsiString Default)
{
  Section(ASection);
  return ReadString(Name, Default);
}

void TQRegistry::WriteInteger(AnsiString Name, int Value)
{
  Registry->WriteInteger(Name, Value);
}

void TQRegistry::WriteFloat(AnsiString Name, double Value)
{
  Registry->WriteFloat(Name, Value);
}

void TQRegistry::WriteBool(AnsiString Name, bool Value)
{
  Registry->WriteBool(Name, Value);
}

void TQRegistry::WriteString(AnsiString Name, AnsiString Value)
{
  Registry->WriteString(Name, Value);
}

void TQRegistry::WriteInteger(AnsiString ASection, AnsiString Name, int Value)
{
  Section(ASection);
  WriteInteger(Name, Value);
}

void TQRegistry::WriteFloat(AnsiString ASection, AnsiString Name, double Value)
{
  Section(ASection);
  WriteFloat(Name, Value);
}

void TQRegistry::WriteBool(AnsiString ASection, AnsiString Name, bool Value)
{
  Section(ASection);
  WriteBool(Name, Value);
}

void TQRegistry::WriteString(AnsiString ASection, AnsiString Name, AnsiString Value)
{
  Section(ASection);
  WriteString(Name, Value);
}



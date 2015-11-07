#include <vcl.h>
#pragma hdrstop

#include "ti.h"
#include "BinTables.h"

const int ValidSignature = 0xBADFACED;
#define VALID_MARK(SUB) if (Mark.AnsiCompare(#SUB) != 0) throw Exception("Invalid Mark " #SUB)

TPC_Application PC_Application;
TPC_Table      *PC_Table;
TPC_Relation   *PC_Relation;
TPC_Field      *PC_Field;
TPC_Enum       *PC_Enum;
TPC_IndexField *PC_KeyField;
TPC_IndexField *PC_OrderField;
TPC_IndexField *PC_BreakField;
TPC_IndexField *PC_ShowField;
TPC_Link       *PC_Link;
TPC_LinkPair   *PC_LinkPair;

static int ReadInt(FILE *In)
{
  int Data;
  fread(&Data, sizeof(int), 1, In);
  char *p = (char*)&Data;
  p[0]^=p[3]^=p[0]^=p[3];
  p[1]^=p[2]^=p[1]^=p[2];
  return Data;
}

static AnsiString ReadString(FILE *In)
{
  char Buffer[2048];
  int Length = ReadInt(In);
  memset(Buffer, 0, sizeof(Buffer));
  fread(Buffer, Length, 1, In);
  return AnsiString(Buffer);
}

static void LoadApplication(FILE *In)
{
  int Signature = ReadInt(In);
  if (Signature != ValidSignature)
    throw Exception("Invalid Configuration Binary File");
  AnsiString Mark = ReadString(In);
  VALID_MARK(APPLICATION);
  PC_Application.name = ReadString(In);
  int p = PC_Application.name.Pos(" DESCR:");
  if (p > 0)
  {
    PC_Application.descr = PC_Application.name.SubString(p+7, PC_Application.name.Length());
    PC_Application.name.Delete(p, PC_Application.name.Length());
    p = PC_Application.descr.Pos(" VERSION:");
    if (p > 0)
    {
      PC_Application.version = PC_Application.descr.SubString(p+9, PC_Application.descr.Length());
      PC_Application.descr.Delete(p, PC_Application.descr.Length());
    }
  }
  PC_Application.registry = ReadString(In);
  #define READ(SUB) \
    PC_Application.no##SUB##s = ReadInt(In); \
    if (PC_Application.no##SUB##s) \
      PC_##SUB = new TPC_##SUB [PC_Application.no##SUB##s]
  READ(Table);
  READ(Relation);
  READ(Field);
  READ(Enum);
  READ(KeyField);
  READ(OrderField);
  READ(ShowField);
  READ(BreakField);
  READ(Link);
  READ(LinkPair);
  #undef READ
}

static void TPC_LoadTables(FILE *In)
{
  for (int i=0; i<PC_Application.noTables; i++)
  {
    PC_Table[i].name = ReadString(In);
    PC_Table[i].descr = ReadString(In);
    #define READ(SUB) PC_Table[i].##SUB = ReadInt(In);
    READ(noFields);
    READ(offsetFields);
    READ(noLinks);
    READ(offsetLinks);
    READ(noKeyFields);
    READ(offsetKeyFields);
    READ(noOrderFields);
    READ(offsetOrderFields);
    READ(noShowFields);
    READ(offsetShowFields);
    READ(noBreakFields);
    READ(offsetBreakFields);
    READ(viewOnly);
    #undef READ
  }
}

static void TPC_LoadRelations(FILE *In)
{
  for (int i=0; i<PC_Application.noRelations; i++)
  {
    PC_Relation[i].name = ReadString(In);
    PC_Relation[i].descr = ReadString(In);
    #define READ(SUB) PC_Relation[i].##SUB = ReadInt(In);
    READ(fromTable);
    READ(noFromFields);
    READ(toTable);
    READ(noToFields);
    READ(offsetFields);
    READ(offsetFromLink);
    READ(offsetToLink);
    #undef READ
  }
}

static void TPC_LoadFields(FILE *In)
{
  for (int i=0; i<PC_Application.noFields; i++)
  {
    PC_Field[i].name = ReadString(In);
    #define READ(SUB) PC_Field[i].##SUB = ReadInt(In);
    READ(type);
    READ(length);
    READ(precision);
    READ(scale);
    READ(offset);
    READ(paddedLength);
    READ(uppercase);
    READ(null);
    READ(noEnums);
    READ(offsetEnums);
    #undef READ
  }
}

static void TPC_LoadEnums(FILE *In)
{
  for (int i=0; i<PC_Application.noEnums; i++)
  {
    PC_Enum[i].name = ReadString(In);
    #define READ(SUB) PC_Enum[i].##SUB = ReadInt(In);
    READ(value);
    #undef READ
  }
}

static void TPC_LoadKeyFields(FILE *In)
{
  for (int i=0; i<PC_Application.noKeyFields; i++)
  {
    #define READ(SUB) PC_KeyField[i].##SUB = ReadInt(In);
    READ(index);
    #undef READ
  }
}

static void TPC_LoadOrderFields(FILE *In)
{
  for (int i=0; i<PC_Application.noOrderFields; i++)
  {
    #define READ(SUB) PC_OrderField[i].##SUB = ReadInt(In);
    READ(index);
    #undef READ
  }
}

static void TPC_LoadShowFields(FILE *In)
{
  for (int i=0; i<PC_Application.noShowFields; i++)
  {
    #define READ(SUB) PC_ShowField[i].##SUB = ReadInt(In);
    READ(index);
    #undef READ
  }
}

static void TPC_LoadBreakFields(FILE *In)
{
  for (int i=0; i<PC_Application.noBreakFields; i++)
  {
    #define READ(SUB) PC_BreakField[i].##SUB = ReadInt(In);
    READ(index);
    #undef READ
  }
}

static void TPC_LoadLinks(FILE *In)
{
  for (int i=0; i<PC_Application.noLinks; i++)
  {
    #define READ(SUB) PC_Link[i].##SUB = ReadInt(In);
    READ(tableNo);
    READ(noLinkPairs);
    READ(offsetLinkPairs);
    #undef READ
  }
}

static void TPC_LoadLinkPairs(FILE *In)
{
  for (int i=0; i<PC_Application.noLinkPairs; i++)
  {
    #define READ(SUB) PC_LinkPair[i].##SUB = ReadInt(In);
    READ(fromNo);
    READ(toNo);
    #undef READ
  }
}

void TPC_LoadParameters(char* BinFilename)
{
  FILE *In = fopen(BinFilename, "rb");
  if (In == 0) return;
  LoadApplication(In);
  AnsiString Mark = ReadString(In);  VALID_MARK(TABLES);
  if (PC_Table)      TPC_LoadTables(In);
  Mark = ReadString(In);   VALID_MARK(RELATIONS);
  if (PC_Relation)   TPC_LoadRelations(In);
  Mark = ReadString(In);   VALID_MARK(FIELDS);
  if (PC_Field)      TPC_LoadFields(In);
  Mark = ReadString(In);   VALID_MARK(ENUMS);
  if (PC_Enum)       TPC_LoadEnums(In);
  Mark = ReadString(In);   VALID_MARK(KEYFIELDS);
  if (PC_KeyField)   TPC_LoadKeyFields(In);
  Mark = ReadString(In);   VALID_MARK(ORDERFIELDS);
  if (PC_OrderField) TPC_LoadOrderFields(In);
  Mark = ReadString(In);   VALID_MARK(SHOWFIELDS);
  if (PC_ShowField) TPC_LoadShowFields(In);
  Mark = ReadString(In);   VALID_MARK(BREAKFIELDS);
  if (PC_BreakField) TPC_LoadBreakFields(In);
  Mark = ReadString(In);   VALID_MARK(LINKS);
  if (PC_Link)       TPC_LoadLinks(In);
  Mark = ReadString(In);   VALID_MARK(LINKPAIRS);
  if (PC_LinkPair)   TPC_LoadLinkPairs(In);
  Mark = ReadString(In);   VALID_MARK(ENDOFDATA);
}

void TPC_FreeParameters()
{
  if (PC_Table)       delete [] PC_Table;
  if (PC_Relation)    delete [] PC_Relation;
  if (PC_Field)       delete [] PC_Field;
  if (PC_Enum)        delete [] PC_Enum;
  if (PC_KeyField)    delete [] PC_KeyField;
  if (PC_OrderField)  delete [] PC_OrderField;
  if (PC_BreakField)  delete [] PC_BreakField;
  if (PC_Link)        delete [] PC_Link;
  if (PC_LinkPair)    delete [] PC_LinkPair;
}


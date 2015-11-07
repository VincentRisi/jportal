#ifndef DBHandlerH
#define DBHandlerH

#include <stddef.h>
#include <strstream.h>
#include "ociapi.h"

const int TPC_BOOLEAN    = 4;
const int TPC_BYTE       = 8;
const int TPC_CHAR       = 12;
const int TPC_DATE       = 16;
const int TPC_DATETIME   = 20;
const int TPC_DOUBLE     = 24;
const int TPC_INT        = 28;
const int TPC_LONG       = 32;
const int TPC_SEQUENCE   = 36;
const int TPC_SHORT      = 40;
const int TPC_TIME       = 44;
const int TPC_TIMESTAMP  = 48;
const int TPC_USERSTAMP  = 52;

struct TPC_Application
{
  AnsiString name;
  AnsiString descr;
  AnsiString version;
  AnsiString registry;
  int        noTables;
  int        noRelations;
  int        noFields;
  int        noEnums;
  int        noKeyFields;
  int        noOrderFields;
  int        noShowFields;
  int        noBreakFields;
  int        noLinks;
  int        noLinkPairs;
  friend ostream& operator<< (ostream& o, const TPC_Application& p)
  {
    return o << "Application name:" << p.name.c_str()
             << " descr:"           << p.descr.c_str()
             << " version:"         << p.version.c_str()
             << " registry:"        << p.registry.c_str()
             << " noTables:"        << p.noTables
             << " noRelations:"     << p.noRelations
             << " noFields:"        << p.noFields
             << " noEnums:"         << p.noEnums
             << " noKeyFields:"     << p.noKeyFields
             << " noOrderFields:"   << p.noOrderFields
             << " noShowFields:"    << p.noShowFields
             << " noBreakFields:"   << p.noBreakFields
             << " noLinks:"         << p.noLinks
             << " noLinkPairs:"     << p.noLinkPairs
             << "\x0d\x0a" << flush;
  }
};

struct TPC_Table
{
  AnsiString name;
  AnsiString descr;
  int        noFields;
  int        offsetFields;
  int        noLinks;
  int        offsetLinks;
  int        noKeyFields;
  int        offsetKeyFields;
  int        noOrderFields;
  int        offsetOrderFields;
  int        noShowFields;
  int        offsetShowFields;
  int        noBreakFields;
  int        offsetBreakFields;
  int        viewOnly;
  friend ostream& operator<< (ostream& o, const TPC_Table& p)
  {
    return o << "Table name:"         << p.name.c_str()
             << " descr:"             << p.descr.c_str()
             << " noFields:"          << p.noFields
             << " offsetFields:"      << p.offsetFields
             << " noLinks:"           << p.noLinks
             << " offsetLinks:"       << p.offsetLinks
             << " noKeyFields:"       << p.noKeyFields
             << " offsetKeyFields:"   << p.offsetKeyFields
             << " noOrderFields:"     << p.noOrderFields
             << " offsetOrderFields:" << p.offsetOrderFields
             << " noShowFields:"      << p.noShowFields
             << " offsetShowFields:"  << p.offsetShowFields
             << " noBreakFields:"     << p.noBreakFields
             << " offsetBreakFields:" << p.offsetBreakFields
             << " viewOnly:"          << p.viewOnly
             << "\x0d\x0a" << flush;
  }
};

typedef struct TPC_IndexField
{
  int        index;
  friend ostream& operator<< (ostream& o, const TPC_IndexField& p)
  {
    return o << "IndexField index:"    << p.index
             << "\x0d\x0a" << flush;
  }
} TPC_KeyField, TPC_OrderField, TPC_ShowField, TPC_BreakField;

struct TPC_Link
{
  int        tableNo;
  int        noLinkPairs;
  int        offsetLinkPairs;
  friend ostream& operator<< (ostream& o, const TPC_Link& p)
  {
    return o << " tableNo:"         << p.tableNo
             << " noLinkPairs:"     << p.noLinkPairs
             << " offsetLinkPairs:" << p.offsetLinkPairs
             << "\x0d\x0a" << flush;
  }
};

struct TPC_LinkPair
{
  int        fromNo;
  int        toNo;
  friend ostream& operator<< (ostream& o, const TPC_LinkPair& p)
  {
    return o << " fromNo:"         << p.fromNo
             << " toNo:"           << p.toNo
             << "\x0d\x0a" << flush;
  }
};

struct TPC_Relation
{
  AnsiString name;
  AnsiString descr;
  int        fromTable;
  int        noFromFields;
  int        toTable;
  int        noToFields;
  int        offsetFields;
  int        offsetFromLink;
  int        offsetToLink;
  friend ostream& operator<< (ostream& o, const TPC_Relation& p)
  {
    return o << "Relation name:"        << p.name.c_str()
             << " descr:"               << p.descr.c_str()
             << " fromTable:"           << p.fromTable
             << " noFromFields:"        << p.noFromFields
             << " toTable:"             << p.toTable
             << " noToFields:"          << p.noToFields
             << " offsetFields:"        << p.offsetFields
             << " offsetFromLink:"      << p.offsetFromLink
             << " offsetToLink:"        << p.offsetToLink
             << "\x0d\x0a" << flush;
  }
};

struct TPC_Enum
{
  AnsiString name;
  int        value;
  friend ostream& operator<< (ostream& o, const TPC_Enum& p)
  {
    return o << "Enum name:"            << p.name.c_str()
             << " value:"               << p.value
             << "\x0d\x0a" << flush;
  }
};

struct TPC_Field
{
  AnsiString name;
  int        type;
  int        length;
  int        precision;
  int        scale;
  int        offset;
  int        paddedLength;
  int        uppercase;
  int        null;
  int        noEnums;
  int        offsetEnums;
  TPC_Field(AnsiString a_name, int a_type, int a_length,
            int a_precision, int a_scale, int a_offset,
            int a_paddedLength, int a_uppercase=0, int a_null=0,
            int a_noEnums=0, int a_offsetEnums=0)
  : name(a_name)
  , type(a_type)
  , length(a_length)
  , precision(a_precision)
  , scale(a_scale)
  , offset(a_offset)
  , paddedLength(a_paddedLength)
  , uppercase(a_uppercase)
  , null(a_null)
  , noEnums(a_noEnums)
  , offsetEnums(a_offsetEnums)
  {}
  TPC_Field() {memset(this, 0, sizeof(*this));}
  friend ostream& operator<< (ostream& o, const TPC_Field& p)
  {
    return o << "Field name:"           << p.name.c_str()
             << " type:"                << p.type
             << " length:"              << p.length
             << " precision:"           << p.precision
             << " scale:"               << p.scale
             << " offset:"              << p.offset
             << " paddedLength:"        << p.paddedLength
             << " uppercase:"           << p.uppercase
             << " null:"                << p.null
             << " noEnums:"             << p.noEnums
             << " offsetEnums:"         << p.offsetEnums
             << "\x0d\x0a" << flush;
  }
};

struct TDBHandler
{
  int rowSize;
  int noRows;
  int noFields;
  int maxRows;
  AnsiString Lookup;
  AnsiString UsId;
  AnsiString TmStamp;
  TPC_Field* fields;
  TJQuery query;
  TDBHandler(TJConnector &aConn);
  ~TDBHandler();
  int GetCount(AnsiString aTableName);
  void GetTable(AnsiString aTableName, int aNoFields, TPC_Field *aFields, TPC_IndexField *orderFields, int noOrderFields, int offsetOrderFields);
  void GetDistinctList(AnsiString aTableName, TPC_Field &aField);
  void Define(int field=0, int position=0);
  bool GetTableFetch();
  bool GetTableField(int fieldNo, char* field);
  bool GetTableField(int fieldNo, int& field);
  bool GetTableField(int fieldNo, long& field);
  bool GetTableField(int fieldNo, short& field);
  bool GetTableField(int fieldNo, double& field);
  void Execute(AnsiString command);
};

extern TPC_Application PC_Application;
extern TPC_Table      *PC_Table;
extern TPC_Relation   *PC_Relation;
extern TPC_Field      *PC_Field;
extern TPC_Enum       *PC_Enum;
extern TPC_IndexField *PC_KeyField;
extern TPC_IndexField *PC_OrderField;
extern TPC_IndexField *PC_ShowField;
extern TPC_IndexField *PC_BreakField;
extern TPC_Link       *PC_Link;
extern TPC_LinkPair   *PC_LinkPair;

#endif

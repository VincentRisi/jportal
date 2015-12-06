#include <vcl.h>
#pragma hdrstop

#include "DBHandler.h"

#pragma package(smart_init)

TDBHandler::TDBHandler(TJConnector &aConn)
: query(aConn)
{
  fields = 0;
  rowSize = 0;
  noRows  = 32;
  noFields = 0;
  maxRows = 0;
  //AnsiString
  Lookup = "";
  UsId = "UsId";
  TmStamp = "TmStamp";
}

TDBHandler::~TDBHandler()
{
  if (query.data)
    query.Close();
  if (fields)
    delete [] fields;
  fields = 0;
}

void TDBHandler::Define(int field, int position)
{
  int type = fields[field].type;
  switch (type)
  {
  case TPC_BOOLEAN:
  case TPC_BYTE:
  case TPC_SHORT:
    query.FileAndLine(JP_MARK);
    query.Define(field, (short*) (query.data+position));
    break;
  case TPC_CHAR:
  case TPC_USERSTAMP:
    query.FileAndLine(JP_MARK);
    query.Define(field, (char*) (query.data+position), fields[field].length+1);
    break;
  case TPC_DATE:
  case TPC_DATETIME:
  case TPC_TIME:
  case TPC_TIMESTAMP:
    query.FileAndLine(JP_MARK);
    query.Define(field, (TJOCIDate*) (query.data+position));
    break;
  case TPC_DOUBLE:
    query.FileAndLine(JP_MARK);
    query.Define(field, (double*) (query.data+position));
    break;
  case TPC_SEQUENCE:
  case TPC_INT:
    query.FileAndLine(JP_MARK);
    query.Define(field, (int*) (query.data+position));
    break;
  case TPC_LONG:
    query.FileAndLine(JP_MARK);
    query.Define(field, (long*) (query.data+position));
    break;
  }
}

void TDBHandler::GetDistinctList(AnsiString aTableName, TPC_Field &aField)
{
  noFields = 1;
  fields = new TPC_Field[noFields]; // must make a copy because of destructor
  fields[0] = aField;
  AnsiString work = "select distinct "+fields[0].name+" from "+aTableName+" order by "+fields[0].name;
  query.command = new char [work.Length()+1];
  memset(query.command, 0, work.Length()+1);
  strcpy(query.command, work.c_str());
  query.FileAndLine(JP_MARK);
  fields[0].offset = 0;
  rowSize = fields[0].paddedLength;
  query.Open(query.command, 0, noFields, noRows, 0, rowSize);
  Define(0, 0);
  query.FileAndLine(JP_MARK);
  query.Exec();
}

int TDBHandler::GetCount(AnsiString aTableName)
{
  int result;
  AnsiString work = "select count(*) from " + aTableName;
  query.command = new char [work.Length()+1];
  memset(query.command, 0, work.Length()+1);
  strcpy(query.command, work.c_str());
  query.FileAndLine(JP_MARK);
  query.Open(query.command, 0, 1, 1, 0, 4);
  query.Define(0, (int*)(query.data));
  query.Exec();
  query.Fetch();
  query.Get(result, query.data);
  return result;
}

void TDBHandler::GetTable(AnsiString aTableName, int aNoFields, TPC_Field *aFields, TPC_IndexField *orderFields, int noOrderFields, int offsetOrderFields)
{
  noFields = aNoFields + 2;
  fields = new TPC_Field[noFields];
  AnsiString work = "select ";
  int i, f;
  for (i=0; i<aNoFields; i++)
  {
    fields[i] = aFields[i];
    work = work + (i==0 ? "" : ", ") + fields[i].name;
  }
  TPC_Field UsId(this->UsId, TPC_CHAR, 16, 0, 0, fields[i-1].offset+fields[i-1].paddedLength, 24, 0, 0);
  fields[i++] = UsId;
  TPC_Field TmStamp(this->TmStamp, TPC_TIMESTAMP, 14, 0, 0, fields[i-1].offset+fields[i-1].paddedLength, 16, 0, 0);
  fields[i++] = TmStamp;
  rowSize = fields[noFields-1].offset+fields[noFields-1].paddedLength;
  work = work + ", "+this->UsId+", "+this->TmStamp;
  work = work + " from " + aTableName;
  if (Lookup.Length() > 0)
    work = work + " " + Lookup;
  else
    work = work + " where rownum <= 1000";
  AnsiString order = " ORDER BY";
  if (noOrderFields > 0)
  {
    for (int i=0; i<noOrderFields; i++)
      order += (i == 0 ? " " : ", ")+fields[orderFields[offsetOrderFields+i].index].name;
  }
  else
  {
    for (int i=0; i<noFields-2; i++)
      order += (i == 0 ? " " : ", ")+AnsiString(i+1);
  }
  work += order;
  query.command = new char [work.Length()+1];
  memset(query.command, 0, work.Length()+1);
  strcpy(query.command, work.c_str());
  query.FileAndLine(JP_MARK);
  query.Open(query.command, 0, noFields, noRows, 0, rowSize);
  for (int i=0; i<noFields; i++)
  {
    int position = noRows * fields[i].offset;
    Define(i, position);
  }
  query.FileAndLine(JP_MARK);
  query.Exec();
}

bool TDBHandler::GetTableFetch()
{
  query.FileAndLine(JP_MARK);
  return query.Fetch();
}

bool TDBHandler::GetTableField(int fieldNo, char* field)
{
  int position = noRows * fields[fieldNo].offset;
  short into;
  query.GetNull(into, fieldNo);
  if (JP_NULL == into)
    return true;
  int type = fields[fieldNo].type;
  switch (type)
  {
    case TPC_DATE:
      query.FileAndLine(JP_MARK);
      query.Get(TJDate(field), query.data+position);
      break;
    case TPC_TIME:
      query.FileAndLine(JP_MARK);
      query.Get(TJTime(field), query.data+position);
      break;
    case TPC_DATETIME:
    case TPC_TIMESTAMP:
      query.FileAndLine(JP_MARK);
      query.Get(TJDateTime(field), query.data+position);
      break;
    case TPC_CHAR:
    case TPC_USERSTAMP:
      query.FileAndLine(JP_MARK);
      query.Get(field, query.data+position, fields[fieldNo].length+1);
      break;
  }
  return false;
}

bool TDBHandler::GetTableField(int fieldNo, int& field)
{
  query.FileAndLine(JP_MARK);
  short into;
  query.GetNull(into, fieldNo);
  if (JP_NULL == into)
    return true;
  query.Get(field, query.data+noRows*fields[fieldNo].offset);
  return false;
}

bool TDBHandler::GetTableField(int fieldNo, long& field)
{
  query.FileAndLine(JP_MARK);
  short into;
  query.GetNull(into, fieldNo);
  if (JP_NULL == into)
    return true;
  query.Get(field, query.data+noRows*fields[fieldNo].offset);
  return false;
}

bool TDBHandler::GetTableField(int fieldNo, short& field)
{
  query.FileAndLine(JP_MARK);
  short into;
  query.GetNull(into, fieldNo);
  if (JP_NULL == into)
    return true;
  query.Get(field, query.data+noRows*fields[fieldNo].offset);
  return false;
}

bool TDBHandler::GetTableField(int fieldNo, double& field)
{
  query.FileAndLine(JP_MARK);
  short into;
  query.GetNull(into, fieldNo);
  if (JP_NULL == into)
    return true;
  query.Get(field, query.data+noRows*fields[fieldNo].offset);
  return false;
}

void TDBHandler::Execute(AnsiString command)
{
  query.command = new char [command.Length()+1];
  memset(query.command, 0, command.Length()+1);
  strcpy(query.command, command.c_str());
  query.FileAndLine(JP_MARK);
  query.Open(query.command);
  query.Exec();
}




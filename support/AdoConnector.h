/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

#ifndef __AdoConnector__H_
#define __AdoConnector__H_

#include <stdio.h>
#include <math.h>
#import "c:\Program Files\Common Files\System\ADO\msado15.dll" no_namespace rename("EOF", "EndOfFile")

class  TConnector;
class  TQuery;
struct TDBException;

typedef void (*TSequenceFunction)(TConnector &conn,  char *name, int &value);
typedef void (*TTimeStampFunction)(TConnector &conn, double &value);
typedef void (*TUserStampFunction)(TConnector &conn, char* value);

class TConnector
{
  friend TQuery;
  _ConnectionPtr _conn;
  char _User[49];
  TSequenceFunction _Sequence;
  TTimeStampFunction _TimeStamp;
  TUserStampFunction _UserStamp;
  static int D18991230;
  static int JulianDate(int year, int month, int day);
  static void CalendarDate(int date, int& year, int& month, int& day);
  static void CalendarDate(int jul, char* YYYYMMDD);
  static void DateAsYYYYMMDD(double date, char* result);
  static void TimeAsHHMMSS(double time, char* result);
  static double HHMMSSAsTime(char* input);
  static double YYYYMMDDAsDate(char* input);
  bool pIsLoggedOn;
  bool pInTran;

public:
  TConnector();
  void Logon(char *aServer, char *aUser=0, char *aPassword=0, long TimeOut=60);
  void Logoff();

  int GetSequence(char *aName);
  double GetTimeStamp();
  char *GetUserStamp(char *aValue);
  void SetSequence(TSequenceFunction   aSequence)  {_Sequence  = aSequence;}
  void SetTimeStamp(TTimeStampFunction aTimeStamp) {_TimeStamp = aTimeStamp;}
  void SetUserStamp(TUserStampFunction aUserStamp) {_UserStamp = aUserStamp;}
  long BeginTran() {long ret = _conn->BeginTrans(); pInTran=true; return ret;}
  long Commit() {pInTran=false; return _conn->CommitTrans();}
  long Rollback() {pInTran=false; return _conn->RollbackTrans();}

  bool IsLoggedOn() { return pIsLoggedOn;}
  bool InTran() { return pInTran;}

  _ConnectionPtr GetConnectionPtr() { return _conn; }
  static void Trim(char *data, int length)
  {
    data[length-1] = 0;
    for (int i=length-2; i >= 0 && (data[i] == ' ' || data[i] == 0); i--)
      data[i] = 0;
  }
  static void Pad(char *data, int length)
  {
    for (int i=0; i < length; i++)
      if (data[i] == 0)
        for (;i < length; i++)
          data[i] = ' ';
  }

};

class TQuery
{
  _RecordsetPtr _rset;
  _CommandPtr _cmd;
  char   _File[128];
  long   _Line;
public:
  enum TDataType
  { BLOB       = 1
  , BOOLEAN    = 2
  , BYTE       = 3
  , CHAR       = 4
  , DATE       = 5
  , DATETIME   = 6
  , DOUBLE     = 7
  , DYNAMIC    = 8
  , FLOAT      = 9
  , IDENTITY   = 10
  , INT        = 11
  , LONG       = 12
  , MONEY      = 13
  , SEQUENCE   = 14
  , SHORT      = 15
  , STATUS     = 16
  , TIME       = 17
  , TIMESTAMP  = 18
  , TLOB       = 19
  , USERSTAMP  = 20
  , ANSICHAR   = 21
  };
  TConnector *conn;
  TQuery();
  void Open(TConnector &aConn, char *command);
  void Close();
  void Exec();
  void Get(void *field, int size, int index, TDataType type);
  void GetLob(void *field, int *sizeout, int size, int index);
  void GetNull(short &isNull, int index);
  void Put(char *name, void *field, int size, TDataType type, short isNull=0);
  bool EndOfFile();
  bool BeginOfFile();
  void Next();
  void MoveFirst();
  void FileAndLine(char *aFile, long aLine)
  {
    strncpy(_File, aFile, sizeof(_File)/sizeof(char)-1);
    _Line = aLine;
  }
};

struct TDBException
{
  HRESULT ErrorNo;
  char ErrorTitle[256];
  char ErrorDesc[4096];
  char File[128];
  long Line;
  TDBException(HRESULT aErrorNo, char *aErrorTitle, char *aFile=0, long aLine=0)
  {
    memset(this, 0, sizeof(*this));
    ErrorNo = aErrorNo;
    strncpy(ErrorTitle, aErrorTitle, sizeof(ErrorTitle)/sizeof(char)-1);
    SetErrorDesc(aErrorNo);
    if (aFile)
      strncpy(File, aFile, sizeof(File)/sizeof(char)-1);
	  File[sizeof(File)-1] = 0;
    Line = aLine;
  }
  TDBException(_com_error &e, char *aErrorTitle, char *aFile=0, long aLine=0)
  {
    memset(this, 0, sizeof(*this));
    bstr_t bstrVal = e.Description();
	  WideCharToMultiByte(CP_ACP, 0, bstrVal, SysStringLen(bstrVal), ErrorDesc,
                        sizeof(ErrorDesc) - 1, NULL, NULL);
	  ErrorNo = e.Error();
    strncpy(ErrorTitle, aErrorTitle, sizeof(ErrorTitle)/sizeof(char)-1);
    if (aFile)
      strncpy(File, aFile, sizeof(File)/sizeof(char)-1);
	  File[sizeof(File)-1] = 0;
    Line = aLine;
  }
  TDBException(const TDBException& copy)
  {
    *this = copy;
  }
  void SetErrorDesc(HRESULT aErrorNo);
};
#endif


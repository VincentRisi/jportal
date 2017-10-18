#include "ti.h"

#include "dbportal.h"

static pchar DBErrors[] =
{ "OK ???"
, "Logon"
, "Logoff"
, "RunProc"
, "Commit"
, "Rollback"
, "Open"
, "Close"
, "Exec"
, "Fetch"
, "FetchMany"
, "Cancel"
, "SetNull"
, "TestNull"
, "Cipher"
};

xDBError::xDBError(pchar aFname, int aLine, eError aError, int aNo, int aDBNo,
                   pchar aMsg, pchar aMsg1, pchar aMsg2)
: xCept(aFname, aLine, xDBErrorName, aError)
, No(aNo)
, DBNo(aDBNo)
{
  osErr << "Database (" << DBNo << ") " << DBErrors[aError] << " error" << endl;
  osErr << "1) " << aNo << ": " << GetSqlError(aNo) << endl;
  if (aMsg)  osErr << "2) " << aMsg << endl;
  if (aMsg1) osErr << "3) " << aMsg1 << endl;
  if (aMsg2) osErr << "4) " << aMsg2 << endl;
  osErr << ends;
}

tDBConnect *tDBConnect::connect;
tDBConnect *tDBConnect::singleton(const char *aBinFile, const char *aLogonString)
{
  if (connect == 0)
  {
    connect = new tDBConnect();
    connect->Logon((char*)aBinFile, (char*)aLogonString);
  }
  return connect;
}

tDBConnect::tDBConnect()
{
  SqlCB = 0;
}

tDBConnect::~tDBConnect()
{
  if (SqlCB)
    Logoff();
}

void tDBConnect::Logon(char *aBinFile, char *aLogonString)
{
  if (SqlCB)
    Logoff();
  int RC = SqlLogon(&SqlCB, aBinFile, aLogonString);
  if (RC)
  {
    int s = 0;
    for (int i=0; i<strlen(aLogonString); i++)
    {
      if (aLogonString[i] == '@')
        s = 0;
      else if (aLogonString[i] == '/')
        s = 1;
      else if (s == 1)
        aLogonString[i] = '*';
    }
    struct Msg
    {
      char *p;
      Msg(char *m){p = strdup(m);}
      ~Msg() {free(p);}
    } msg(ErrorMsg());
    int ErrCode = SqlCB->ociErrCode;
    free(SqlCB);
    SqlCB = 0;
    throw XDBError2(errDBLogon, RC, ErrCode, msg.p, aBinFile, aLogonString);
  }
}

pchar tDBConnect::Cipher(pchar Input, ushort InpLen,
                         pchar Output, ushort OutLen,
                         ushort Seed)
{
  SqlOneWayCipher(Input, InpLen, Output, OutLen, Seed);
  return Output;
}

void tDBConnect::Logoff()
{
  int RC = 0;
  if (SqlCB)
    RC = SqlLogoff(SqlCB);
  if (RC)
    throw XDBError(errDBLogoff, RC, SqlCB->ociErrCode, ErrorMsg());
  SqlCB = 0;
}

pchar tDBConnect::ErrorMsg()
{
  if (SqlCB->ociErrCode)
    return SqlCBError(SqlCB);
  return 0;
}

void tDBConnect::RunProc(pchar aQuery, void* aData)
{
  int RC = SqlRunProc(SqlCB, aQuery, aData);
  if (RC)
    throw XDBError1(errDBRunProc, RC, SqlCB->ociErrCode, ErrorMsg(), aQuery);
}

bool tDBConnect::ReadOne(pchar aQuery, void* aData)
{
  int RC = SqlRunProc(SqlCB, aQuery, aData);
  if (RC)
  {
    if (SqlCB->ociErrCode==OCI_NO_DATA||SqlCB->ociErrCode==1403)
      return false;
    throw XDBError1(errDBRunProc, RC, SqlCB->ociErrCode, ErrorMsg(), aQuery);
  }
  return true;
}

void tDBConnect::Commit(void)
{
  int RC = SqlCommit(SqlCB);
  if (RC)
    throw XDBError(errDBCommit, RC, SqlCB->ociErrCode, ErrorMsg());
}

void tDBConnect::Rollback(void)
{
  int RC = SqlRollback(SqlCB);
  if (RC)
    throw XDBError(errDBRollback, RC, SqlCB->ociErrCode, ErrorMsg());
}

tDBQuery::tDBQuery(tDBConnect& aDBConnect, pchar aQuery, void* aData)
{
  int RC = SqlOpen(aDBConnect.SqlCB, &SqlCursor, aQuery, aData);
  if (RC)
    throw XDBError1(errDBOpen, RC, SqlCursor->ociErrCode, ErrorMsg(), aQuery);
}

tDBQuery::~tDBQuery()
{
  if (SqlCursor)
    Close();
}

void tDBQuery::Close()
{
  int RC = SqlClose(SqlCursor);
  if (RC)
    throw XDBError1(errDBClose, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
  SqlCursor = 0;
}

void tDBQuery::Exec()
{
  int RC = SqlExec(SqlCursor);
  if (RC)
    throw XDBError1(errDBExec, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
}

bool tDBQuery::Fetch()
{
  int RC = SqlFetch(SqlCursor);
  if (RC)
  {
    if (SqlEOD(SqlCursor))
      return false;
    throw XDBError1(errDBFetch, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
  }
  return true;
}

pchar tDBQuery::ErrorMsg()
{
  if (SqlCursor->ociErrCode)
    return SqlCursorError(SqlCursor);
  return 0;
}

void tDBQuery::Cancel()
{
  int RC = SqlCancel(SqlCursor);
  if (RC)
    throw XDBError1(errDBCancel, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
}

bool tDBQuery::FetchMany(pushort aNo, void* aData)
{
  int RC = SqlFetchMany(SqlCursor, aNo, aData);
  if (RC)
  {
    if (SqlEOD(SqlCursor))
      return false;
    throw XDBError1(errDBFetchMany, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
  }
  return true;
}

void tDBQuery::SetNull(pchar aColumn, bool aHow)
{
  int RC = SqlSetNull(SqlCursor, aColumn, aHow);
  if (RC)
    throw XDBError1(errDBSetNull, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
}

bool tDBQuery::TestNull(pchar aColumn)
{
  bool aHow;
  int RC = SqlTestNull(SqlCursor, aColumn, &aHow);
  if (RC)
    throw XDBError1(errDBTestNull, RC, SqlCursor->ociErrCode, ErrorMsg(), SqlCursor->Command);
  return aHow;
}


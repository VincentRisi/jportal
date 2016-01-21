#include "machine.h"

#if defined(M_W32)
#include "windows.h"
#endif

#define IS_NET 71

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

#include "xstring.h"
#include "xtalloc.h"

#include "sqlapi.h"
#include "sqlcons.h"
#include "sqltok.h"

#include "tbuffer.h"
#include "binio.h"
#include "zcompress.h"

#ifdef nexttime
static FILE *debug;
static void OpenDebug()
{
  if (!debug)
  {
    debug = fopen("sqlapi32.lst", "wt");
    fprintf(debug, "Opened\n");
    fflush(debug);
  }
}
#endif

ushort ImageMax=8192;

static tSqlCB ABNSqlCB;
static tSqlCursor ABNSqlCursor;

static pSqlCB _GetSqlCB(void);
static pSqlCursor _SqlOpen(pSqlCB SqlCB, pSqlQuery SqlQuery, void* Data);
static pchar _SqlError(pSqlCB SqlCB);

#if !defined(APPLAPI)
#define APPLAPI
#endif

char *Errors[] =
{
  "SQLapi: ok"
, "SQLapi: error: allocating memory!"
, "SQLapi: loading binary control block File!"
, "SQLapi: logon connect failure!"
, "SQLapi: query not found!"
, "SQLapi: cursor open error!"
, "SQLapi: cursor command parse error!"
, "SQLapi: cursor bind error!"
, "SQLapi: cursor define error!"
, "SQLapi: cursor execution error!"
, "SQLapi: cursor fetch error!"
, "SQLapi: cursor cancel error!"
, "SQLapi: cursor close error!"
, "SQLapi: transaction commit error!"
, "SQLapi: transaction rollback error!"
, "SQLapi: no more rows in query error"
, "SQLapi: invalid CB!"
, "SQLapi: invalid cursor!"
, "SQLapi: invalid query state!"
, "SqlApi: ODBC alloc environment error"
, "SqlApi: ODBC alloc connect error"
, "SqlApi: Unsupported in multi-row fetch"
, "SqlApi: Field name not found:"
, "SqlApi: Error in Database Connection File:"
, "SqlApi: No more CB handles"
, "SqlApi: No more cursor handles"
, "SqlApi: logoff error"
, "SQLapi: error out of range!"
};

static pchar _SqlApiError(int errNo)
{
   if (errNo == SqlApiOK)
     return Errors[SqlApiOK];
   if (errNo < SqlApiFirstErr || errNo > SqlApiLastErr)
     return Errors[SqlApiLastErr-SqlApiFirstErr];
   return Errors[errNo-SqlApiFirstErr+1];
}

pchar GetSqlError(int errNo)
{
  return _SqlApiError(errNo);
}

static char *_SqlError(pSqlCB SqlCB)
{
  const int bufsiz = 4096;
  if (!SqlCB->ociBuf)
  {
    SqlCB->ociBuf = (text *) calloc(bufsiz, 1);
    if (!SqlCB->ociBuf)
      return "No memory for message buffer -- dire circumstances";
  }
  if (SqlCB->ociError)
  {
    text *p = SqlCB->ociBuf;
    int siz=bufsiz;
    int n, l;
    sb4 EC;
    SqlCB->ociErrCode = 0;
    for (n = 1; n < 5; n++)
    {
      sword RC = OCIErrorGet(SqlCB->ociError,
                             n++, 0, &EC, p, siz,
                             OCI_HTYPE_ERROR);
      if (RC != 0)
        break;
      if (SqlCB->ociErrCode == 0)
        SqlCB->ociErrCode = EC;
      l = strlen((const char *)p);
      siz -= l;
      p += l;
    }
  }
  else
    sprintf((char*)SqlCB->ociBuf, "Oracle error no handle (RC==%d)", SqlCB->RC);
  return (char*)SqlCB->ociBuf;
}

static char *_SqlCursorError(pSqlCursor SqlCursor)
{
  char *result = _SqlError(SqlCursor->SqlCB);
  SqlCursor->RC = SqlCursor->SqlCB->RC;
  SqlCursor->ociErrCode = SqlCursor->SqlCB->ociErrCode;
  return result;
}

#if defined(M_W32)
int APPLAPI SqlVBLogon(pSqlCB *SqlCB, pchar FileName, pchar User)
{
  int RC = SqlLogon(SqlCB, FileName, User);
  (*SqlCB)->isVB = 1;
  return RC;
}

int APPLAPI SqlNetLogon(pSqlCB *SqlCB, pchar FileName, pchar User)
{
  int RC = SqlLogon(SqlCB, FileName, User);
  (*SqlCB)->isVB = IS_NET;
  return RC;
}
#endif

int APPLAPI SqlLogon(pSqlCB *SqlCB, pchar FileName, pchar User)
{
  char wUser[256], wPassword[256]="", wServer[256]="";
  strcpy(wUser, User);
  char *p1 = strchr(wUser, '@');
  char *p2 = strchr(wUser, '/');
  pSqlCB CB;
  CB = *SqlCB = _GetSqlCB();
  if (CB->Error)
    goto Return;
  if (SqlBinOpen(&CB->SqlBin, FileName))
  {
    CB->Error = SqlApiBinFileErr;
    goto Return;
  }
  if (p1)
  {
    p1[0] = 0;
    p1++;
  }
  if (p2)
  {
    p2[0] = 0;
    p2++;
  }
  if (p1) strcpy(wServer, p1);
  if (p2) strcpy(wPassword, p2);
  CB->RC = OCIInitialize(OCI_DEFAULT, 0, 0, 0, 0);
  if (CB->RC == 0)
    CB->RC = OCIEnvInit(&CB->ociEnv, OCI_DEFAULT, 0, 0);
  if (CB->RC == 0)
    CB->RC = OCIHandleAlloc(CB->ociEnv, (void **)&CB->ociError, OCI_HTYPE_ERROR, 0, 0);
  if (CB->RC == 0)
    CB->RC = OCILogon(CB->ociEnv, CB->ociError, &CB->ociSvcCtx,
                     (text*) wUser,     strlen(wUser),
                     (text*) wPassword, strlen(wPassword),
                     (text*) wServer,   strlen(wServer));
  if (CB->RC)
  {
    _SqlError(CB);
    SqlBinClose(CB->SqlBin);
    CB->SqlBin = 0;
    CB->Error = SqlApiConnectErr;
  }
  CB->isVB = 0;
Return:
  CB->ErrorMsg = _SqlApiError(CB->Error);
  return CB->Error;
}

int APPLAPI SqlSetTimeout(pSqlCB SqlCB, int milliSeconds=0)
{
  SqlCB->timeout = milliSeconds;
  return 0;
}

static pSqlCB _GetSqlCB(void)
{
  pSqlCB SqlCB = (pSqlCB)calloc(sizeof(tSqlCB), 1);
  if (SqlCB == 0)
  {
    SqlCB = &ABNSqlCB;
    SqlCB->Error = SqlApiMemErr;
  }
  else
    SqlCB->Signature = CBSignature;
  return SqlCB;
}

static int InvalidCB(pSqlCB &SqlCB)
{
#if defined(M_W32)
  if (SqlCB == 0 || IsBadWritePtr(SqlCB, sizeof(tSqlCB)) || SqlCB->Signature != CBSignature)
#else
  if (SqlCB == 0 || SqlCB->Signature != CBSignature)
#endif
    SqlCB = &ABNSqlCB;
  if (SqlCB == &ABNSqlCB)
  {
    SqlCB->Error = SqlApiCBErr;
    SqlCB->ErrorMsg = _SqlApiError(SqlCB->Error);
    return 1;
  }
  return 0;
}

static int InvalidCursor(pSqlCursor &SqlCursor)
{
#if defined(M_W32)
  if (SqlCursor == 0 || IsBadWritePtr(SqlCursor, sizeof(tSqlCursor)) || SqlCursor->Signature != CursorSignature)
#else
  if (SqlCursor == 0 || SqlCursor->Signature != CursorSignature)
#endif
    SqlCursor = &ABNSqlCursor;
  if (SqlCursor == &ABNSqlCursor)
  {
    SqlCursor->Error = SqlApiCursorErr;
    SqlCursor->ErrorMsg = _SqlApiError(SqlCursor->Error);
    return 1;
  }
  return 0;
}

int APPLAPI SqlOpen(pSqlCB SqlCB, pSqlCursor *SqlCursor, pchar Query, void* Data)
{
  pSqlCursor Cursor;
  pSqlQuery  SqlQuery;
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  if (SqlCB->SqlBin == 0 || SqlBinQuery(SqlCB->SqlBin, &SqlQuery, Query))
  {
    *SqlCursor = Cursor = &ABNSqlCursor;
    Cursor->Error = SqlApiNoQueryErr;
  }
  else
    *SqlCursor = Cursor = _SqlOpen(SqlCB, SqlQuery, Data);
  SqlCB->Error = Cursor->Error;
  Cursor->ErrorMsg = _SqlApiError(SqlCB->Error);
  SqlCB->ErrorMsg = Cursor->ErrorMsg;
  _SqlError(SqlCB);
  return SqlCB->Error;
}

int APPLAPI SqlOpenWQ(pSqlCB SqlCB, pSqlCursor *SqlCursor, pSqlQuery Query, void* Data)
{
  pSqlCursor Cursor;
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  *SqlCursor = Cursor = _SqlOpen(SqlCB, Query, Data);
  SqlCB->Error = Cursor->Error;
  SqlCB->ErrorMsg = Cursor->ErrorMsg = _SqlApiError(SqlCB->Error);
  _SqlError(SqlCB);
  return SqlCB->Error;
}

int APPLAPI SqlOpenExec(pSqlCB SqlCB, pSqlCursor *SqlCursor, pchar Query, void* Data)
{
  int RC;
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
  RC = SqlOpen(SqlCB, SqlCursor, Query, Data);
  if (RC == 0)
    RC = SqlExec(*SqlCursor);
  return RC;
}

#if defined(M_W32)
struct ATFields
{
  int NoFields;
  pSqlField *Fields;
  ~ATFields()
  {
    delete [] Fields;
  }
  ATFields(pSqlQuery SqlQuery)
  {
    NoFields = SqlQuery->NoFields;
    Fields = new pSqlField[NoFields];
    for (int i=0; i<NoFields; i++)
      Fields[i] = &SqlQuery->Fields[i];
    Sort();
  }
  void Sort()
  {
    int i, j;
    pSqlField t;
    for (i=0; i <= NoFields-2; i++)
    {
      for (j=NoFields-1; j >= i+1; j--)
      {
        if (Fields[i]->Offset > Fields[j]->Offset)
        {
          t = Fields[i];
          Fields[i] = Fields[j];
          Fields[j] = t;
        }
      }
    }
  }
};

static void tonet(pSqlCursor SqlCursor, pchar aData)
{
  ATFields F(SqlCursor->SqlQuery);
  pchar Data = (pchar)SqlCursor->Data;
  for (int i=0; i<F.NoFields; i++)
  {
    pSqlField Field = F.Fields[i];
    switch (Field->SqlType)
    {
      case 5:
      case 96:
      {
        pchar Dest = *(pchar*)aData;
        memcpy(Dest, Data+Field->Offset, Field->Size);
        aData += sizeof(pchar);
        break;
      }
      default:
      {
        memcpy(aData, Data+Field->Offset, Field->Size);
        aData += Field->Size;
      }
    }
  }
}

static void fromnet(pSqlCursor SqlCursor, pchar aData)
{
  ATFields F(SqlCursor->SqlQuery);
  pchar Data = (pchar)SqlCursor->Data;
  for (int i=0; i<F.NoFields; i++)
  {
    pSqlField Field = F.Fields[i];
    switch (Field->SqlType)
    {
      case 5:
      case 96:
      {
        pchar Src = *(pchar*)aData;
        memcpy(Data+Field->Offset, Src, Field->Size);
        aData += sizeof(pchar);
        break;
      }
      default:
      {
        memcpy(Data+Field->Offset, aData, Field->Size);
        aData += Field->Size;
      }
    }
  }
}
#endif

int APPLAPI SqlRunProc(pSqlCB SqlCB, pchar Query, void* Data)
{
  pSqlCursor SqlCursor;
  int RC;
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
  RC = SqlOpen(SqlCB, &SqlCursor, Query, Data);
  if (RC)
    goto Return;
  RC = SqlExec(SqlCursor);
  if (RC)
  {
    if (SqlEOD(SqlCursor) == true)
      RC = SqlApiNoMoreRows;
    _SqlError(SqlCB);
    SqlClose(SqlCursor);
    goto Return;
  }
#if defined(M_W32)
  if (SqlCursor->SqlCB->isVB == IS_NET)
    tonet(SqlCursor, (pchar)Data);
  else if (SqlCursor->SqlCB->isVB > 0)
    memcpy(Data, SqlCursor->Data, SqlCursor->DataSize);
#endif
  SqlClose(SqlCursor);
Return:
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  return RC;
}

static char *vbstrrtrim(char *s, short len)
{
  s[len - 1] = ' ';
  while (len && (s[len - 1] == ' ' || s[len - 1] == '\t'))
    s[--len] = 0;
  return s;
}

static void vbstrrpad(char *s, short fldlen, char padchar)
{
  int len = strlen(s);
  while (len < fldlen)
    s[len++] = padchar;
}

// ZCompress(unsigned char * InBuff, long InLen, unsigned char * OutBuff, long * OutLen)
static void compress(char *ZLOB)//, pSqlField Field)
{
  int32         *Lengths = (int32 *)ZLOB;
  unsigned char *Data    = (unsigned char *)ZLOB+8;

  if (Lengths[1] == 0)
    Lengths[0] = Lengths[1];
  else
  {
    TBUChar Buffer(Lengths[1]); // Allocate area at least as big

    ZCompress(Data, Lengths[1], Buffer.data, &Lengths[0]);

    if (Lengths[0] < Lengths[1])
      memcpy(Data, Buffer.data, Lengths[0]);
  }
  Swap(Lengths[1], (int)sizeof(Lengths[1]));
  Lengths[0] += 4;
}

// ZDecompress(unsigned char * InBuff, long InLen, unsigned char * OutBuff, long * OutLen)
static void uncompress(char *ZLOB)//, pSqlField Field)
{
  int32         *Lengths = (int32 *)ZLOB;
  unsigned char *Data    = (unsigned char *)ZLOB+8;

  Swap(Lengths[1], (int)sizeof(Lengths[1]));
  if (Lengths[0] >= 4)
  {
    Lengths[0] += 4;
    if (Lengths[0] < Lengths[1])
    {
      TBUChar Buffer(Lengths[1]); // Allocate area at least as big
      int32 OutLen = Lengths[1];
      ZDecompress(Data, Lengths[0], Buffer.data, &OutLen);
      memcpy(Data, Buffer.data, OutLen);
    }
  }
}

static void VBPad(pSqlCursor SqlCursor)
{
  ushort i;
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  pchar Data = (pchar) SqlCursor->Data;

  for (i=0; i<SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlQuery->Fields[i];
    if ( (Field->SqlType==5) || (Field->SqlType==96) )
      vbstrrpad(Data+Field->Offset, Field->Size, ' ');
  }
}

static void VBTrim(pSqlCursor SqlCursor)
{
  ushort i;
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  pchar Data = (pchar) SqlCursor->Data;
  for (i=0; i<SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlQuery->Fields[i];
    if ( (Field->SqlType==5) || (Field->SqlType==96) )
      vbstrrtrim(Data+Field->Offset, Field->Size);
  }
}

static int DynamicFieldSize(pSqlQuery aQuery, pchar token)
{
  int i;
  for (i=0; i < aQuery->NoFields; i++)
  {
    pSqlField Field = &aQuery->Fields[i];
    if ((Field->isBind == 0 && Field->isDefine == 0)
    && (stricmp(Field->Name+1, token)) == 0)
      return Field->Size;
  }
  return 0;
}

static pchar DynamicFieldValue(pSqlQuery aQuery, pchar token, pchar data)
{
  int i;
  for (i=0; i < aQuery->NoFields; i++)
  {
    pSqlField Field = &aQuery->Fields[i];
    if ((Field->isBind == 0 && Field->isDefine == 0)
    && (stricmp(Field->Name+1, token)) == 0)
    {
      data += Field->Offset;
      return vbstrrtrim(data, Field->Size);
    }
  }
  return "";
}

static pchar FixupCommand(pSqlQuery aQuery, pchar data)
{
  int len = strlen(aQuery->Command), added=0;
  pchar nextPos = aQuery->Command;
  pchar Command;
  char token[128];
  while (nextPos[0])
  {
    nextPos = SqlToken(nextPos, token, sizeof(token));
    if (token[0] != '$')
      continue;
    added += DynamicFieldSize(aQuery, token+1);
  }
  Command = (pchar) calloc(len+added+1, 1);
  if (added == 0)
  {
    strcpy(Command, aQuery->Command);
    return Command;
  }
  nextPos = aQuery->Command;
  while (nextPos[0])
  {
    nextPos = SqlToken(nextPos, token, sizeof(token));
    if (token[0] != '$' || DynamicFieldSize(aQuery, token+1) == 0)
    {
      strcat(Command, token);
      continue;
    }
    strcat(Command, DynamicFieldValue(aQuery, token+1, data));
  }
  return Command;
}

static pSqlCursor _SqlOpen(pSqlCB SqlCB, pSqlQuery SqlQuery, void* Data)
{
  pSqlCursor SqlCursor;
  pchar IndData;
  int i, no, size;
  SqlCursor = (pSqlCursor) calloc(1, sizeof(tSqlCursor));
  if (SqlCursor == 0)
  {
    SqlCursor = &ABNSqlCursor;
    SqlCursor->Error = SqlApiMemErr;
    return SqlCursor;
  }
  SqlCursor->SqlCB = SqlCB;
  SqlCursor->Signature = CursorSignature;
  SqlCursor->SqlQuery = SqlQuery;
  size=0;
  for (i=0; i<SqlCursor->SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlCursor->SqlQuery->Fields[i];
    no = Field->Offset+Field->Size;
    if (no > size)
      size = no;
    if (Field->isBind)
      SqlCursor->NoBinds++;
    if (Field->isDefine)
      SqlCursor->NoDefines++;
    if (Field->SqlType == SQL_OCI_HUGECHAR)
      SqlCursor->NoLobLocators++;
  }
#if defined(M_W32)
  if (SqlCB->isVB > 0)
  {
    SqlCursor->DataSize = size;
    SqlCursor->Data = calloc(size+4, 1);
    if (SqlCB->isVB == IS_NET)
      fromnet(SqlCursor, (pchar)Data);
    else
      memcpy(SqlCursor->Data, Data, SqlCursor->DataSize);
  }
  else
    SqlCursor->Data = Data;
#else
  SqlCursor->Data = Data;
#endif
  SqlCursor->Command = FixupCommand(SqlQuery, (pchar) SqlCursor->Data);
  if (SqlQuery->isMultiFetch)
  {
    for (i=0, size=0; i<SqlQuery->NoFields; i++)
    {
      pSqlField Field = &SqlQuery->Fields[i];
      if (Field->isDefine)
        size += Field->Size;
    }
    if (size)
    {
      const int maxbufsize = 24*1024;
      SqlCursor->RowsCount = no = (maxbufsize / size) + 1;
      SqlCursor->RowIndex = 0;
      SqlCursor->RowsDone = 0;
      SqlCursor->RowsRead = 0;
      SqlCursor->BufferSize = size*no;
      SqlCursor->Buffer = calloc(no, size);
      if (SqlCursor->Buffer == 0)
        SqlQuery->isMultiFetch = 0;
    }
    else
      SqlQuery->isMultiFetch = 0;
  }
  if (!SqlQuery->isMultiFetch)
    SqlCursor->RowsCount = 1;
  if (SqlQuery->NoFields)
  {
    SqlCursor->Indicators = (pshort) calloc(SqlQuery->NoFields*SqlCursor->RowsCount,
                                            sizeof(short));
    if (SqlCursor->Indicators == 0)
    {
      SqlCursor->Error = SqlApiMemErr;
      return SqlCursor;
    }
  }
  if (SqlCursor->NoBinds)
  {
    SqlCursor->ociBinds = (OCIBind**) calloc(SqlCursor->NoBinds, sizeof(OCIBind*));
    if (SqlCursor->ociBinds == 0)
    {
      SqlCursor->Error = SqlApiMemErr;
      return SqlCursor;
    }
  }
  if (SqlCursor->NoDefines)
  {
    SqlCursor->ociDefines = (OCIDefine**) calloc(SqlCursor->NoDefines, sizeof(OCIDefine*));
    if (SqlCursor->ociDefines == 0)
    {
      SqlCursor->Error = SqlApiMemErr;
      return SqlCursor;
    }
  }
  if (SqlCursor->NoLobLocators)
  {
    SqlCursor->ociLobLocators = (OCILobLocator**) calloc(SqlCursor->NoLobLocators, sizeof(OCILobLocator*));
    if (SqlCursor->ociLobLocators == 0)
    {
      SqlCursor->Error = SqlApiMemErr;
      return SqlCursor;
    }
  }
  SqlCB->RC = OCIHandleAlloc(SqlCB->ociEnv,
                            (void **)&SqlCursor->ociStmt,
                             OCI_HTYPE_STMT,
                             0, 0);
  if (SqlCB->RC == 0)
    SqlCB->RC = OCIStmtPrepare(SqlCursor->ociStmt,
                               SqlCB->ociError,
                               (text *) SqlCursor->Command,
                               strlen(SqlCursor->Command),
                               OCI_NTV_SYNTAX,
                               OCI_DEFAULT);
  if (SqlCB->RC)
  {
    _SqlCursorError(SqlCursor);
    SqlCursor->Error = SqlApiOpenErr;
    return SqlCursor;
  }
  int BindNo = 0;
  int DefineNo = 0;
  int LobLocatorNo = 0;
  char *data;
  int length;
  for (i=0, no=0, size=0; i<SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlQuery->Fields[i];
    if (Field->isBind)
    {
      if (Field->SqlType == SQL_OCI_BLIMAGE
      ||  Field->SqlType == SQL_OCI_CLIMAGE)
      {
        pchar LOB = (pchar)SqlCursor->Data+Field->Offset;
        SqlCB->RC = OCIBindByName(SqlCursor->ociStmt,
                                  &SqlCursor->ociBinds[BindNo++],
                                  SqlCB->ociError,
                                  (text *) Field->Name, -1,
                                  LOB, Field->Size,
                                  Field->SqlType,
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, 0, 0, OCI_DEFAULT);
      }
      else if (Field->SqlType == SQL_OCI_HUGECHAR)
      {
        length = *(int*) ((pchar)SqlCursor->Data+Field->Offset);
        data = *(pchar*) ((pchar)SqlCursor->Data+Field->Offset+4);
        SqlCB->RC = OCIBindByName(SqlCursor->ociStmt,
                                  &SqlCursor->ociBinds[BindNo++],
                                  SqlCB->ociError,
                                  (text *) Field->Name, -1,
                                  data, length,
                                  SQL_OCI_VARCHAR2,
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, 0, 0, OCI_DEFAULT);
      }
      else if (Field->Size == 0)
      {
        IndData = *(pchar*)((pchar)SqlCursor->Data+Field->Offset);
        SqlCB->RC = OCIBindByName(SqlCursor->ociStmt,
                                  &SqlCursor->ociBinds[BindNo++],
                                  SqlCB->ociError,
                                  (text *) Field->Name, -1,
                                  IndData, ImageMax,
                                  (ub2) (Field->SqlType==96?5:Field->SqlType),
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, 0, 0, OCI_DEFAULT);
      }
      else
      {
        SqlCB->RC = OCIBindByName(SqlCursor->ociStmt,
                                  &SqlCursor->ociBinds[BindNo++],
                                  SqlCB->ociError,
                                  (text *) Field->Name, -1,
                                  (pchar)SqlCursor->Data+Field->Offset, Field->Size,
                                  (ub2) (Field->SqlType==96?5:Field->SqlType),
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, 0, 0, OCI_DEFAULT);
      }
      if (SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiBindErr;
        return SqlCursor;
      }
    }
    if (Field->isDefine)
    {
      if (Field->SqlType == SQL_OCI_BLIMAGE
      ||  Field->SqlType == SQL_OCI_CLIMAGE)
      {
        pchar LOB = (pchar)SqlCursor->Data+Field->Offset;
        SqlCB->RC = OCIDefineByPos(SqlCursor->ociStmt,
                                  &SqlCursor->ociDefines[DefineNo++],
                                  SqlCB->ociError,
                                  ++no,
                                  LOB, Field->Size,
                                  Field->SqlType,
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, OCI_DEFAULT);
      }
      else if (Field->SqlType == SQL_OCI_HUGECHAR)
      {
        int el = LobLocatorNo;LobLocatorNo++;
        OCILobLocator *& lobLoc = SqlCursor->ociLobLocators[el];
        OCIDescriptorAlloc(SqlCB->ociEnv,
                          (dvoid**)&lobLoc,
                          (ub4)OCI_DTYPE_LOB,
                          (size_t) 0,
                          (dvoid**) 0);
        SqlCB->RC = OCIDefineByPos(SqlCursor->ociStmt,
                                  &SqlCursor->ociDefines[DefineNo++],
                                  SqlCB->ociError,
                                  ++no,
                                  (dvoid*)&lobLoc, 0,
                                  (ub2)SQLT_CLOB,
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, OCI_DEFAULT);
      }
      else if (Field->Size == 0)
      {
        IndData = *(pchar*)((pchar)SqlCursor->Data+Field->Offset);
        SqlCB->RC = OCIDefineByPos(SqlCursor->ociStmt,
                                  &SqlCursor->ociDefines[DefineNo++],
                                  SqlCB->ociError,
                                  ++no,
                                  IndData, ImageMax,
                                  (ub2)(Field->SqlType==96?5:Field->SqlType),
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, OCI_DEFAULT);
      }
      else
      {
        if (SqlQuery->isMultiFetch)
        {
          IndData = (pchar)((pchar)SqlCursor->Buffer+size);
          size += (SqlCursor->RowsCount*Field->Size);
        }
        else
          IndData = (pchar)SqlCursor->Data+Field->Offset;
        SqlCB->RC = OCIDefineByPos(SqlCursor->ociStmt,
                                  &SqlCursor->ociDefines[DefineNo++],
                                  SqlCB->ociError,
                                  ++no,
                                  IndData, Field->Size,
                                  (ub2)(Field->SqlType==96?5:Field->SqlType),
                                  &SqlCursor->Indicators[i*SqlCursor->RowsCount],
                                  0, 0, OCI_DEFAULT);
      }
      if (SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiDefineErr;
        return SqlCursor;
      }
    }
  }
  SqlCursor->Error = SqlApiOK;
  return SqlCursor;
}

static void FudgeData(pSqlCursor SqlCursor)
{
  ushort i;
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  pchar Data = (pchar) SqlCursor->Data;
  for (i=0; i<SqlQuery->NoFields; i++)
  {
    short isNull = 0;
    pSqlField Field = &SqlQuery->Fields[i];
    if (SqlQuery->isNullEnabled
    && ((Field->isBind|Field->isDefine) & fieldIsNullable)
    && ((Field->isBind|Field->isDefine) & fieldIsNull))
    {
      isNull = *(short*)(Data+Field->Offset+Field->Size);
      SqlCursor->Indicators[i*SqlCursor->RowsCount] = (short) isNull ? -1 : 0;
    }
    if (isNull == 0)
    {
      if (Field->SqlType == SQL_OCI_CHARx)
        strrpad(Data+Field->Offset, Field->Size-1, ' ');
      else if (Field->CType == SQL_C_ZLIMAGE)
        compress(Data+Field->Offset);
    }
  }
}

static void UnFudgeData(pSqlCursor SqlCursor)
{
  ushort i;
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  pchar Data = (pchar) SqlCursor->Data;
  for (i=0; i<SqlQuery->NoFields; i++)
  {
    short isNull = 0;
    pSqlField Field = &SqlQuery->Fields[i];
    if ((Field->isBind|Field->isDefine) & fieldIsNull)
    {
      int n = SqlCursor->RowIndex - 1;
      if (n < 0)
        n = 0;
      isNull = SqlCursor->Indicators[i*SqlCursor->RowsCount+n];
    }
    if (SqlQuery->isNullEnabled
    && ((Field->isBind|Field->isDefine) & fieldIsNullable)
    && ((Field->isBind|Field->isDefine) & fieldIsNull))
      *(short*)(Data+Field->Offset+Field->Size) = isNull;
    if (isNull != 0)
    {
        switch(Field->SqlType)
        {
        case SQL_OCI_VARCHAR2:
        case SQL_OCI_CHARx:
        case SQL_OCI_CHARz:
          Data[Field->Offset] = 0;
        }
      }
    else
    {
    if (Field->SqlType == SQL_OCI_CHARx)
      strrtrim(Data+Field->Offset);
      else if (Field->CType == SQL_C_ZLIMAGE)
        uncompress(Data+Field->Offset);
    }
  }
}

int APPLAPI SqlSetNull(pSqlCursor SqlCursor, pchar aName, bool aHow)
{
  int i;
  for (i=0; i < SqlCursor->SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlCursor->SqlQuery->Fields[i];
    if ((stricmp(aName, Field->Name+1))==0)
    {
      SqlCursor->Indicators[i*SqlCursor->RowsCount] = (short) (aHow ? -1 : 0);
      SqlCursor->Error = SqlApiOK;
      goto Return;
    }
  }
  SqlCursor->Error = SqlApiNoFldname;
  Return:
  return SqlCursor->Error;
}

int APPLAPI SqlTestNull(pSqlCursor SqlCursor, pchar aName, bool *aHow)
{
  int i,n;
  for (i=0; i < SqlCursor->SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlCursor->SqlQuery->Fields[i];
    if ((stricmp(aName, Field->Name+1))==0)
    {
      n = SqlCursor->RowIndex - 1;
      if (n < 0)
        n = 0;
      *aHow = (SqlCursor->Indicators[i*SqlCursor->RowsCount+n] < 0) ? true : false;
      SqlCursor->Error = SqlApiOK;
      goto Return;
    }
  }
  SqlCursor->Error = SqlApiNoFldname;
  Return:
  return SqlCursor->Error;
}

static int DoTheFetch(pSqlCursor SqlCursor)
{
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  SqlCursor->Error = SqlApiOK;
  if (SqlQuery->isMultiFetch)
  {
    if (SqlCursor->RowsDone >= SqlCursor->RowsRead)
    {
      if (SqlCursor->RC == OCI_NO_DATA)
      {
        SqlCursor->Error = SqlApiNoMoreRows;
        goto Return;
      }
      memset(SqlCursor->Buffer, 0, SqlCursor->BufferSize);
      SqlCursor->SqlCB->RC = OCIStmtFetch(SqlCursor->ociStmt,
                                          SqlCursor->SqlCB->ociError,
                                          SqlCursor->RowsCount,
                                          OCI_FETCH_NEXT,
                                          OCI_DEFAULT);
      if (SqlCursor->SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        if (SqlCursor->RC != OCI_NO_DATA)
        {
          SqlCursor->Error = SqlApiFetchErr;
          goto Return;
        }
      }
      SqlCursor->RowIndex = 0;
      sword AGRC = OCIAttrGet(SqlCursor->ociStmt,
                              OCI_HTYPE_STMT,
                              &SqlCursor->RowsRead,
                              0,
                              OCI_ATTR_ROW_COUNT,
                              SqlCursor->SqlCB->ociError);
      if (AGRC)
      {
        SqlCursor->SqlCB->RC = AGRC;
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiFetchErr;
        goto Return;
      }
    }
    if (SqlCursor->RowsDone < SqlCursor->RowsRead)
    {
      ushort i;
      ushort size;
      pchar FromData, ToData;
      for (i=0, size=0; i<SqlQuery->NoFields; i++)
      {
        pSqlField Field = &SqlQuery->Fields[i];
        if (Field->isDefine)
        {
          FromData = (pchar)((pchar)SqlCursor->Buffer+size);
          FromData += (SqlCursor->RowIndex*Field->Size);
          size += (ushort)(SqlCursor->RowsCount*Field->Size);
          ToData = (pchar)SqlCursor->Data+Field->Offset;
          memcpy(ToData, FromData, Field->Size);
        }
      }
      SqlCursor->RowsDone++;
      SqlCursor->RowIndex++;
    }
    else
    {
      // ??
      SqlCursor->Error = SqlApiFetchErr;
      goto Return;
    }
  }
  else
  {
    SqlCursor->SqlCB->RC = OCIStmtFetch(SqlCursor->ociStmt,
                                        SqlCursor->SqlCB->ociError,
                                        1,
                                        OCI_FETCH_NEXT,
                                        OCI_DEFAULT);
    if (SqlCursor->SqlCB->RC)
    {
      _SqlCursorError(SqlCursor);
      SqlCursor->Error = SqlApiFetchErr;
      goto Return;
    }
  }
Return:
#ifdef M_AIX
  if (SqlCursor->SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  return SqlCursor->Error;
}

static int FetchLobData(pSqlCursor SqlCursor)
{
  int el, i;
  ub4 length, read=0, offset=1;
  char** data;
  for (i = 0, el = 0; i < SqlCursor->SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlCursor->SqlQuery->Fields[i];
    if (Field->isDefine && Field->SqlType == SQL_OCI_HUGECHAR)
    {
      OCILobLocator *& lobLoc = SqlCursor->ociLobLocators[el];
      length = *(ub4*) ((pchar)SqlCursor->Data+Field->Offset);
      data = (char**) ((pchar)SqlCursor->Data+Field->Offset+4);
      SqlCursor->SqlCB->RC = OCILobOpen(SqlCursor->SqlCB->ociSvcCtx,
                                        SqlCursor->SqlCB->ociError,
                                        lobLoc,
                                        OCI_LOB_READONLY);
      if (SqlCursor->SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiFetchErr;
        return SqlCursor->Error;
      }
      SqlCursor->SqlCB->RC = OCILobGetLength(SqlCursor->SqlCB->ociSvcCtx,
                                        SqlCursor->SqlCB->ociError,
                                        lobLoc,
                                        &length);
      *(ub4*) ((pchar)SqlCursor->Data+Field->Offset) = length;
      if (SqlCursor->SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiFetchErr;
        OCILobClose(SqlCursor->SqlCB->ociSvcCtx,
                    SqlCursor->SqlCB->ociError,
                    lobLoc);
        return SqlCursor->Error;
      }
      if (length > 0)
      {
        if (data[0] != 0)
          delete [] data[0];
        data[0] = new char[length+1];
        data[0][length] = 0;
        SqlCursor->SqlCB->RC = OCILobRead(SqlCursor->SqlCB->ociSvcCtx,
                                          SqlCursor->SqlCB->ociError,
                                          lobLoc,
                                          &read, offset,
                                          data[0], length,
                                          (dvoid*)0,
                                          (sb4 (*)(dvoid *, const dvoid *, ub4, ub1)) 0,
                                          (ub2) 0, (ub1) SQLCS_IMPLICIT);
        if (SqlCursor->SqlCB->RC)
        {
          _SqlCursorError(SqlCursor);
          SqlCursor->Error = SqlApiFetchErr;
          OCILobClose(SqlCursor->SqlCB->ociSvcCtx,
                      SqlCursor->SqlCB->ociError,
                      lobLoc);
          return SqlCursor->Error;
        }
      }
      SqlCursor->SqlCB->RC = OCILobClose(SqlCursor->SqlCB->ociSvcCtx,
                                        SqlCursor->SqlCB->ociError,
                                        lobLoc);
      if (SqlCursor->SqlCB->RC)
      {
        _SqlCursorError(SqlCursor);
        SqlCursor->Error = SqlApiFetchErr;
        return SqlCursor->Error;
      }
      el++;
    }
  }
  return SqlCursor->Error;
}

static int DoFetch(pSqlCursor SqlCursor)
{
  int rc = DoTheFetch(SqlCursor);
  if (rc == 0 && SqlCursor->NoLobLocators > 0)
    rc = FetchLobData(SqlCursor);
  return rc;
}

#ifdef M_AIX
static pSqlCB AlarmSqlCB;
static void AlarmExecBreak(int Signal)
{
  alarm(0);
  OCIBreak(AlarmSqlCB->ociSvcCtx, AlarmSqlCB->ociError);
  return;
}
#endif

int APPLAPI SqlExec(pSqlCursor SqlCursor)
{
  pSqlQuery SqlQuery = SqlCursor->SqlQuery;
  if (InvalidCursor(SqlCursor))
    return SqlCursor->Error;
  if (SqlCursor->SqlCB->isVB != 0) // VB neg Special Case
    VBTrim(SqlCursor);
  FudgeData(SqlCursor);
  int no = 1;
  if (SqlCursor->NoDefines)
    no = 0;
#ifdef M_AIX
  if (SqlCursor->SqlCB->timeout > 0)
  {
    AlarmSqlCB = SqlCursor->SqlCB;
    signal(SIGALRM, AlarmExecBreak);
    alarm((unsigned int)SqlCursor->SqlCB->timeout);
  }
#endif
  SqlCursor->SqlCB->RC = OCIStmtExecute(SqlCursor->SqlCB->ociSvcCtx,
                                        SqlCursor->ociStmt,
                                        SqlCursor->SqlCB->ociError,
                                        no, //sSqlCursor->RowsCount,
                                        0, 0, 0, OCI_DEFAULT);
  if (SqlCursor->SqlCB->RC)
  {
    _SqlCursorError(SqlCursor);
    SqlCursor->Error = SqlApiExecErr;
    goto Return;
  }
  if (SqlQuery->isMultiFetch)
  {
    SqlCursor->RowsDone = 0;
    SqlCursor->RowsRead = 0;
  }
  if (SqlQuery->isFetch)
  {
    if (DoFetch(SqlCursor))
    {
      if (SqlEOD(SqlCursor) == true)
        SqlCursor->Error = SqlApiNoMoreRows;
      else
        SqlCursor->Error = SqlApiFetchErr;
      goto Return;
    }
  }
  UnFudgeData(SqlCursor);
  if (SqlCursor->SqlCB->isVB != 0) //VB neg Special Case
    VBPad(SqlCursor);
  SqlCursor->Error = SqlApiOK;
Return:
  SqlCursor->ErrorMsg = _SqlApiError(SqlCursor->Error);
  return SqlCursor->Error;
}

#if defined(M_W32)
int APPLAPI SqlFetchData(pSqlCursor SqlCursor, void *Data)
{
  int rc = SqlFetch(SqlCursor);
  if (SqlCursor->SqlCB->isVB > 0 && rc == 0)
  {
    if (SqlCursor->SqlCB->isVB == IS_NET)
      tonet(SqlCursor, (pchar)Data);
    else
      memcpy(Data, SqlCursor->Data, SqlCursor->DataSize);
  }
  return rc;
}
#endif

int APPLAPI SqlFetch(pSqlCursor SqlCursor)
{
  if (InvalidCursor(SqlCursor))
    return SqlCursor->Error;
  if (DoFetch(SqlCursor))
  {
    if (SqlEOD(SqlCursor) == true)
      SqlCursor->Error = SqlApiNoMoreRows;
    else
      SqlCursor->Error = SqlApiFetchErr;
  }
  else
  {
    UnFudgeData(SqlCursor);
    if (SqlCursor->SqlCB->isVB != 0) // VB neg Special Case
      VBPad(SqlCursor);
    SqlCursor->Error = SqlApiOK;
  }
  SqlCursor->ErrorMsg = _SqlApiError(SqlCursor->Error);
  return SqlCursor->Error;
}

static ushort GetSize(pSqlQuery SqlQuery)
{
  ushort i, size = 0;
  for (i=0; i<SqlQuery->NoFields; i++)
  {
    pSqlField Field = &SqlQuery->Fields[i];
    ushort next = (ushort) (Field->Offset + Field->Size);
    if (next > size)
      size = next;
  }
  return (ushort)(((size+3)/4)*4);
}

int APPLAPI SqlFetchMany(pSqlCursor SqlCursor, pushort aNo, void *Data)
{
  ushort i, No = *aNo;
  ushort size = GetSize(SqlCursor->SqlQuery);
  int rc;
  pchar Keep = (pchar) calloc(No, size);
  if (Keep == 0)
  {
    SqlCursor->Error = SqlApiMemErr;
    return SqlCursor->Error;
  }
  for (i=0; i<No; i++)
  {
    rc = SqlFetch(SqlCursor);
    if (rc)
      break;
    memcpy(Keep+i*size, SqlCursor->Data, size);
  }
  if (i)
    memcpy(Data, Keep, size*i);
  *aNo = i;
  free(Keep);
  return rc;
}

int APPLAPI SqlCancel(pSqlCursor SqlCursor) // No longer used
{
  if (InvalidCursor(SqlCursor))
    return SqlCursor->Error;
  SqlCursor->Error = SqlApiOK;
  SqlCursor->ErrorMsg = _SqlApiError(SqlCursor->Error);
  return SqlCursor->Error;
}

int APPLAPI SqlClose(pSqlCursor SqlCursor)
{
  int i;
  char *n;
  if (InvalidCursor(SqlCursor))
    return SqlCursor->Error;
#if defined(M_W32)
  if (SqlCursor->SqlCB->isVB > 0)
  {
    if (SqlCursor->Data)
      free(SqlCursor->Data);
  }
#endif
  if (SqlCursor->ociBinds)
    free(SqlCursor->ociBinds);
  if (SqlCursor->ociDefines)
    free(SqlCursor->ociDefines);
  if (SqlCursor->ociLobLocators)
  {
    for (i=0; i<SqlCursor->NoLobLocators;i++)
      if (SqlCursor->ociLobLocators[i])
        OCIDescriptorFree((dvoid*)SqlCursor->ociLobLocators[i], (ub4)OCI_DTYPE_LOB);
    free(SqlCursor->ociLobLocators);
  }
  if (SqlCursor->Indicators)
    free(SqlCursor->Indicators);
  if (SqlCursor->Command)
    free(SqlCursor->Command);
  if (SqlCursor->Buffer)
    free(SqlCursor->Buffer);
  OCIHandleFree(SqlCursor->ociStmt, OCI_HTYPE_STMT);
  SqlCursor->Error = SqlApiOK;
  n = _SqlApiError(SqlCursor->Error);
  ABNSqlCursor.Error = SqlCursor->Error;
  free(SqlCursor);
  SqlCursor = &ABNSqlCursor;
  SqlCursor->ErrorMsg = n;
  return SqlCursor->Error;
}

int APPLAPI SqlCommit(pSqlCB SqlCB)
{
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  SqlCB->RC = OCITransCommit(SqlCB->ociSvcCtx,
                             SqlCB->ociError,
                             OCI_DEFAULT);
  if (SqlCB->RC)
  {
    _SqlError(SqlCB);
    SqlCB->Error = SqlApiCommitErr;
  }
  else
    SqlCB->Error = SqlApiOK;
  SqlCB->ErrorMsg = _SqlApiError(SqlCB->Error);
  _SqlError(SqlCB);
  return SqlCB->Error;
}

int APPLAPI SqlRollback(pSqlCB SqlCB)
{
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  SqlCB->RC = OCITransRollback(SqlCB->ociSvcCtx,
                               SqlCB->ociError,
                               OCI_DEFAULT);
  if (SqlCB->RC)
  {
    _SqlError(SqlCB);
    SqlCB->Error = SqlApiRollbackErr;
  }
  else
    SqlCB->Error = SqlApiOK;
  SqlCB->ErrorMsg = _SqlApiError(SqlCB->Error);
  _SqlError(SqlCB);
  return SqlCB->Error;
}

int APPLAPI SqlLogoff(pSqlCB SqlCB)
{
  int RC;
  if (InvalidCB(SqlCB))
    return SqlCB->Error;
#ifdef M_AIX
  if (SqlCB->timeout > 0)
  {
    signal(SIGALRM, SIG_IGN);
    alarm(0);
  }
#endif
  if (SqlCB->ociBuf)
    free(SqlCB->ociBuf);
  if (SqlCB == &ABNSqlCB)
    return SqlCB->Error;
  SqlCB->RC = OCILogoff(SqlCB->ociSvcCtx,
                        SqlCB->ociError);
  if (SqlCB->RC)
    RC = SqlApiLogoffErr;
  else
  {
    OCIHandleFree(SqlCB->ociError, OCI_HTYPE_ERROR);
    OCIHandleFree(SqlCB->ociSvcCtx, OCI_HTYPE_SVCCTX);
    RC = SqlApiOK;
  }
  SqlBinClose(SqlCB->SqlBin);
  SqlCB->SqlBin = 0;
  free(SqlCB);
  return RC;
}

pchar APPLAPI SqlCBError(pSqlCB SqlCB)
{
  if (InvalidCB(SqlCB))
    return "CB is Invalid";
  return (char *) SqlCB->ociBuf;
}

ushort APPLAPI SqlCBErrorCode(pSqlCB SqlCB)
{
  if (InvalidCB(SqlCB))
    return (ushort) SqlCB->Error;
  return (ushort) SqlCB->ociErrCode;
}

bool APPLAPI SqlCBEOD(pSqlCB SqlCB)
{
  if (InvalidCB(SqlCB))
    return false;
  return SqlCB->RC == OCI_NO_DATA ? true : false;
}

pchar APPLAPI SqlCursorError(pSqlCursor SqlCursor)
{
  if (InvalidCursor(SqlCursor))
    return SqlCursor->ErrorMsg;
  return (char *) SqlCursor->SqlCB->ociBuf;
}

#if defined(M_W32)
int APPLAPI SqlVBCBError(pSqlCB SqlCB, char * Message, short MessageLen)
{
  strncpy(Message, SqlCBError(SqlCB), MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}

int APPLAPI SqlVBCommand(pSqlCursor SqlCursor, char * Message, short MessageLen)
{
  strncpy(Message, SqlCursor->Command, MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}

int APPLAPI SqlVBCursorError(pSqlCursor SqlCursor, char * Message, short MessageLen)
{
  strncpy(Message, SqlCursorError(SqlCursor), MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}

int APPLAPI SqlNetCBError(pSqlCB SqlCB, char * Message, short MessageLen)
{
  strncpy(Message, SqlCBError(SqlCB), MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}

int APPLAPI SqlNetCommand(pSqlCursor SqlCursor, char * Message, short MessageLen)
{
  strncpy(Message, SqlCursor->Command, MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}

int APPLAPI SqlNetCursorError(pSqlCursor SqlCursor, char * Message, short MessageLen)
{
  strncpy(Message, SqlCursorError(SqlCursor), MessageLen-1);
  Message[MessageLen-1] = 0;
  return 0;
}
#endif

ushort APPLAPI SqlCursorErrorCode(pSqlCursor SqlCursor)
{
  if (InvalidCursor(SqlCursor))
    return (ushort) SqlCursor->Error;
  return (ushort) SqlCursor->ociErrCode;
}

bool APPLAPI SqlEOD(pSqlCursor SqlCursor)
{
  if (InvalidCursor(SqlCursor))
    return false;
  return SqlCursor->RC == OCI_NO_DATA ? true : false;
}

bool APPLAPI SqlDuplErr(pSqlCursor SqlCursor)
{
  if (InvalidCursor(SqlCursor))
    return false;
  return SqlCursor->ociErrCode == 1 ? true : false; // nasty possible
}

short APPLAPI SqlOneWayCipher(pchar Input, ushort InpLen,
                              pchar Output, ushort OutLen,
                              ushort Seed)
{
  int i;
  for (i = 0; i<InpLen; i++)
    Seed *= (ushort)((toupper(Input[i]) * (i+1)) * 17);
  for (i = 0; i <OutLen; i++)
  {
    Seed += (ushort) (toupper(Input[i%InpLen]) * 47 * (i+1));
    Output[i] = (char) ('A' + (Seed % 26));
  }
  return (short) i;
}




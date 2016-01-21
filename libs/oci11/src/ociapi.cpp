#include "ociapi.h"

JP_INTERNAL(int) JP_NewConnector(TJConnector *&conn);
JP_INTERNAL(int) JP_NewQuery(TJQuery *&qry, TJConnector &conn);
JP_INTERNAL(int) JP_FreeConnector(TJConnector *&conn);
JP_INTERNAL(int) JP_FreeQuery(TJQuery *&qry);

JP_INTERNAL(int) JP_Logon(TJConnector &conn, const text* User, const text* Password, const text* Server);
JP_INTERNAL(int) JP_Logoff(TJConnector &conn);
JP_INTERNAL(int) JP_Commit(TJConnector &conn);
JP_INTERNAL(int) JP_Rollback(TJConnector &conn);

JP_INTERNAL(int) JP_Open(TJQuery &qry, const text* Query);
JP_INTERNAL(int) JP_Exec(TJQuery &qry);
JP_INTERNAL(int) JP_Fetch(TJQuery &qry);
JP_INTERNAL(int) JP_Deliver(TJQuery &qry, int doit);
JP_INTERNAL(int) JP_Close(TJQuery &qry);

JP_INTERNAL(void) JP_Setup(TJQuery &qry, int noBinds, int noDefines, int noRows, int noLobs, int sizeRow);
JP_INTERNAL(void) JP_SetupArray(TJQuery &qry, int noBinds, int noRows, int noLobs, int sizeRow);

JP_INTERNAL(int) JP_BindString(TJQuery &qry,  text* Name, int bindNo, char  *Data, int Datalen, int ansichar);
JP_INTERNAL(int) JP_BindLong(TJQuery &qry,    text* Name, int bindNo, int64 &Data);
JP_INTERNAL(int) JP_BindInt(TJQuery &qry,     text* Name, int bindNo, int32 &Data);
JP_INTERNAL(int) JP_BindShort(TJQuery &qry,   text* Name, int bindNo, int16 &Data);
JP_INTERNAL(int) JP_BindDouble(TJQuery &qry,  text* Name, int bindNo, double &Data);
JP_INTERNAL(int) JP_BindOCIDate(TJQuery &qry, text* Name, int bindNo, TJOCIDate &Data);
JP_INTERNAL(int) JP_BindLob(TJQuery &qry,     text* Name, int bindNo, OCILobLocator *Data, int lobType);

JP_INTERNAL(int) JP_BindDoubleNull(TJQuery &qry,  text* Name, int bindNo, double  &Data, int16 &Null);
JP_INTERNAL(int) JP_BindLongNull(TJQuery &qry,    text* Name, int bindNo, int64   &Data, int16 &Null);
JP_INTERNAL(int) JP_BindIntNull(TJQuery &qry,     text* Name, int bindNo, int32   &Data, int16 &Null);
JP_INTERNAL(int) JP_BindShortNull(TJQuery &qry,   text* Name, int bindNo, int16   &Data, int16 &Null);
JP_INTERNAL(int) JP_BindStringNull(TJQuery &qry,  text* Name, int bindNo, char    *Data, int Datalen, int16 &Null, int ansichar);
JP_INTERNAL(int) JP_BindOCIDateNull(TJQuery &qry, text* Name, int bindNo, TJOCIDate &Data, int16 &Null);
JP_INTERNAL(int) JP_BindLobNull(TJQuery &qry,     text* Name, int bindNo, OCILobLocator *Data, int lobType, int16 &Null);

JP_INTERNAL(int) JP_BindStringArray(TJQuery &qry,  text* Name, int bindNo, char    *Data, int Datalen, int ansichar);
JP_INTERNAL(int) JP_BindLongArray(TJQuery &qry,    text* Name, int bindNo, int64   *Data);
JP_INTERNAL(int) JP_BindIntArray(TJQuery &qry,     text* Name, int bindNo, int32   *Data);
JP_INTERNAL(int) JP_BindShortArray(TJQuery &qry,   text* Name, int bindNo, int16   *Data);
JP_INTERNAL(int) JP_BindDoubleArray(TJQuery &qry,  text* Name, int bindNo, double  *Data);
JP_INTERNAL(int) JP_BindOCIDateArray(TJQuery &qry, text* Name, int bindNo, TJOCIDate *Data);
JP_INTERNAL(int) JP_BindLobArray(TJQuery &qry,     text* Name, int bindNo, OCILobLocator **Data, int lobType);

JP_INTERNAL(int) JP_DefineString(TJQuery &qry,   int defineNo, char *data, int dataLen, int ansichar);
JP_INTERNAL(int) JP_DefineLong(TJQuery &qry,     int defineNo, int64 *data);
JP_INTERNAL(int) JP_DefineInt(TJQuery &qry,      int defineNo, int32 *data);
JP_INTERNAL(int) JP_DefineShort(TJQuery &qry,    int defineNo, int16 *data);
JP_INTERNAL(int) JP_DefineDouble(TJQuery &qry,   int defineNo, double *data);
JP_INTERNAL(int) JP_DefineDate(TJQuery &qry,     int defineNo, TJOCIDate *data);
JP_INTERNAL(int) JP_DefineLob(TJQuery &qry,      int defineNo, OCILobLocator *data, int lobType);

JP_INTERNAL(void) JP_GetString(TJQuery &qry,     char   *into, char *from, int dataLen);
JP_INTERNAL(void) JP_GetLong(TJQuery &qry,       int64  &into, char *from);
JP_INTERNAL(void) JP_GetInt(TJQuery &qry,        int32  &into, char *from);
JP_INTERNAL(void) JP_GetShort(TJQuery &qry,      int16  &into, char *from);
JP_INTERNAL(void) JP_GetDouble(TJQuery &qry,     double &into, char *from);
JP_INTERNAL(void) JP_GetDate(TJQuery &qry,       char   *into, char *from);
JP_INTERNAL(void) JP_GetTime(TJQuery &qry,       char   *into, char *from);
JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry,   char   *into, char *from);
JP_INTERNAL(void) JP_GetLob(TJQuery &qry,        TJLob  &into, char *from);

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int16 &into, int defineNo);

JP_INTERNAL(void) JP_PutString(TJQuery &qry,     char *into, char   *from, int dataLen);
JP_INTERNAL(void) JP_PutLong(TJQuery &qry,       char *into, int64  &from);
JP_INTERNAL(void) JP_PutInt(TJQuery &qry,        char *into, int32  &from);
JP_INTERNAL(void) JP_PutShort(TJQuery &qry,      char *into, int16  &from);
JP_INTERNAL(void) JP_PutDouble(TJQuery &qry,     char *into, double &from);
JP_INTERNAL(void) JP_PutDate(TJQuery &qry,       char *into, char   *from);
JP_INTERNAL(void) JP_PutTime(TJQuery &qry,       char *into, char   *from);
JP_INTERNAL(void) JP_PutDateTime(TJQuery &qry,   char *into, char   *from);
JP_INTERNAL(void) JP_PutLob(TJQuery &qry,        char *into, TJLob  &from);

JP_INTERNAL(void) JP_PutNull(TJQuery &qry, int16 &from, int bindNo);

JP_INTERNAL(void) JP_Sequence(TJQuery& qry, int &sequence, char *sequencer);
JP_INTERNAL(void) JP_UserStamp(TJQuery& qry, char* userStamp);
JP_INTERNAL(void) JP_Date(TJQuery& qry, TJOCIDate &ocidate, char *date);
JP_INTERNAL(void) JP_Time(TJQuery& qry, TJOCIDate &ocidate, char *time);
JP_INTERNAL(void) JP_DateTime(TJQuery& qry, TJOCIDate &ocidate, char *dateTime);
JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry, TJOCIDate &ocidate, char *timeStamp);

JP_INTERNAL(int) JP_JulianDate(int year, int month, int day);
JP_INTERNAL(int) JP_JulianDate(char* YYYYMMDD);
JP_INTERNAL(int) JP_Seconds(char* HHMISS);
JP_INTERNAL(void) JP_CalendarDate(int juldate, int& year, int& month, int& day);
JP_INTERNAL(void) JP_CalendarDate(int jul, char* YYYYMMDD);
JP_INTERNAL(void) _FromOracleDate(TJOCIDate& ocidate, int& yyyymmdd, int& hhmmss);
JP_INTERNAL(void) _ToOracleDate(TJOCIDate& ocidate, int yyyymmdd, int hhmmss);
JP_INTERNAL(void) _SqlError(TJConnector &conn);
JP_INTERNAL(int) _Result(TJConnector &conn);
JP_INTERNAL(int) _Result(TJQuery &qry);
JP_INTERNAL(void) _BindByName(TJQuery &qry, const text* name, int bindNo, void *data, int dataLen, ub2 dataType, sb2 isNull=0);
JP_INTERNAL(void) _BindByNameArray(TJQuery &qry, const text* name, int bindNo, void *data, int dataLen, ub2 dataType);
JP_INTERNAL(void) _DefineByPos(TJQuery &qry, int defineNo, void *data, int dataLen, ub2 dataType);

JP_INTERNAL(int) JP_JulianDate(int year, int month, int day)
{
  if (year < 1)
    year = 1;
  if (month > 2)
    month = month - 3;
  else
  {
    month = month + 9;
    year  = year - 1;
  }
  int century = year / 100;
  int cy = year - 100 * century;
  return (146097 * century) / 4
       + (1461 * cy) / 4
       + (153 * month + 2) / 5
       + day
       + 1721119;
}

JP_INTERNAL(int) JP_JulianDate(char* YYYYMMDD)
{
  int day, month, year;
  char work[5];
  strncpy(work, YYYYMMDD, 4);   work[4] = 0; year  = atoi(work);
  strncpy(work, YYYYMMDD+4, 2); work[2] = 0; month = atoi(work);
  strncpy(work, YYYYMMDD+6, 2); work[2] = 0; day   = atoi(work);
  return  JP_JulianDate(year, month, day);
}

JP_INTERNAL(int) JP_Seconds(char* HHMISS)
{
  int secs, mins, hours;
  char work[5];
  strncpy(work, HHMISS, 2);     work[2] = 0; hours = atoi(work);
  strncpy(work, HHMISS+2, 2);   work[2] = 0; mins  = atoi(work);
  strncpy(work, HHMISS+4, 2);   work[2] = 0; secs  = atoi(work);
  return  hours*60*60+mins*60+secs;
}

JP_INTERNAL(void) JP_CalendarDate(int juldate, int& year, int& month, int& day)
{
  int temp = juldate - 1721119;
  year = (4 * temp - 1) / 146097;
  temp = 4 * temp - 1 - 146097 * year;
  day = temp / 4;
  temp = (4 * day + 3) / 1461;
  day = 4 * day + 3 - 1461 * temp;
  day = (day + 4) / 4;
  month = (5 * day - 3) / 153;
  day = 5 * day - 3 - 153 * month;
  day = (day + 5) / 5;
  year = 100 * year + temp;
  if (month < 10)
    month += 3;
  else
  {
    month -= 9;
    year++;
  }
}

JP_INTERNAL(void) JP_CalendarDate(int jul, char* YYYYMMDD)
{
  int day, month, year;
  JP_CalendarDate(jul, year, month, day);
  char work[20];
  sprintf(work, "%04.4d%02.2d%02.2d", year, month, day);
  strncpy(YYYYMMDD, work, 8);
}

const TJOCIDate ZERO_DATE = {100,100,1,1,1,1,1};

JP_INTERNAL(void) _FromOracleDate(TJOCIDate& ocidate, int& yyyymmdd, int& hhmmss)
{
  if (memcmp(&ocidate, &ZERO_DATE, 7)==0)
  {
    yyyymmdd = hhmmss = 0;
    return;
  }
  yyyymmdd = (ocidate.cc - 100) * 1000000
           + (ocidate.yy - 100) * 10000;
  if (yyyymmdd >= 0)
    yyyymmdd += (ocidate.mm * 100 + ocidate.dd);
  else
    yyyymmdd -= (ocidate.mm * 100 + ocidate.dd);
  hhmmss = (ocidate.hh-1) * 10000
         + (ocidate.mi-1) * 100
         + (ocidate.ss-1);
}

JP_INTERNAL(void) _ToOracleDate(TJOCIDate& ocidate, int yyyymmdd, int hhmmss)
{
  ocidate = ZERO_DATE;
  if (yyyymmdd == 0 && hhmmss == 0)
    return;
  if (hhmmss)
  {
    ocidate.ss = (unsigned char)(hhmmss % 100+1); hhmmss /= 100;
    ocidate.mi = (unsigned char)(hhmmss % 100+1); hhmmss /= 100;
    ocidate.hh = (unsigned char)(hhmmss % 100+1);
  }
  if (yyyymmdd)
  {
    int BC = yyyymmdd < 0;
    if (BC)
      yyyymmdd = -yyyymmdd;
    ocidate.dd = (unsigned char)(yyyymmdd % 100); yyyymmdd /= 100;
    ocidate.mm = (unsigned char)(yyyymmdd % 100); yyyymmdd /= 100;
    ocidate.yy = (unsigned char)(yyyymmdd % 100); yyyymmdd /= 100;
    ocidate.cc = (unsigned char) yyyymmdd;
    if (BC)
    {
      ocidate.yy = (unsigned char)(100u - ocidate.yy);
      ocidate.cc = (unsigned char)(100u - ocidate.cc);
    }
    else
    {
      ocidate.yy = (unsigned char)(ocidate.yy + 100u);
      ocidate.cc = (unsigned char)(ocidate.cc + 100u);
    }
  }
}

void TJConnector::Logon(const char* User, const char* Password, const char* Server)
{
  if (JP_Logon(*this, (text*)User, (text*)Password, (text*)Server))
    ThrowOnError("Logon");
  LoggedOn = 1;
}

void TJConnector::Logoff()
{
  if (LoggedOn == 0 || JP_Logoff(*this))
    ThrowOnError("Logoff");
  LoggedOn = 0;
}

void TJConnector::Commit()
{
  if (LoggedOn == 0 || JP_Commit(*this))
    ThrowOnError("Commit");
}

void TJConnector::Rollback()
{
  if (LoggedOn == 0 || JP_Rollback(*this))
    ThrowOnError("Rollback");
}

void TJConnector::Error(char *Msg, int MsgLen)
{
  if (JP_Error(*this, Msg, MsgLen))
    ThrowOnError("Error");
  LoggedOn = 0;
}

void TJConnector::ThrowOnError(char *file, int line)
{
  if (result != 0)
    throw TOciApiException(result, (char*)msgbuf, file, line);
}

TJQuery::~TJQuery()
{
  Close();
}

void TJConnector::TimeStamp(char *timeStamp)
{
  if (serverTime == 0)
  {
    time(&systemTime);
    TJQuery q(*this);
    q.command = new char [32];
    strcpy(q.command, "select SYSDATE from dual");
    q.Open(q.command, 0, 1, 1, 8);
    q.Define(0, (TJOCIDate*) (q.data));
    q.Exec();
    serverTime = systemTime;
    if (q.Fetch())
    {
      q.Get(TJDateTime(timeStamp), q.data);
      serverTime = JP_JulianDate(timeStamp)-JP_JulianDate("19700101");
      serverTime = serverTime*(24*60*60) + JP_Seconds(timeStamp+8);
    }
  }
  time_t tt;
  struct tm *lt;
  time(&tt);
  tt += (serverTime-systemTime);
  lt  = gmtime(&tt);
  int dateNow = 19000000+(10000*lt->tm_year)
                        +(100L*(lt->tm_mon+1))
                        +lt->tm_mday;
  int timeNow = 10000L*lt->tm_hour
               +100*(lt->tm_min)
               +lt->tm_sec;
  sprintf(timeStamp, "%08.8d%06.6d", dateNow, timeNow);
}

void TJQuery::Open(const char* Query, int noBinds, int noDefines, int noRows, int noLobs, int sizeRow)
{
  if (JP_Open(*this, (text*)Query))
    conn.ThrowOnError(file, line);
  JP_Setup(*this, noBinds, noDefines, noRows, noLobs, sizeRow);
}

void TJQuery::OpenArray(const char* Query, int noBinds, int noRows, int noLobs, int sizeRow)
{
  if (JP_Open(*this, (text*)Query))
    conn.ThrowOnError(file, line);
  JP_SetupArray(*this, noBinds, noRows, noLobs, sizeRow);
}

void TJQuery::Bind(const char* name, int bindNo, char *data, int dataLen, int16 *null, int ansichar)
{
  int result;
  if (null)
    result = JP_BindStringNull(*this, (text*)name, bindNo, data, dataLen, *null, ansichar);
  else
    result = JP_BindString(*this, (text*)name, bindNo, data, dataLen, ansichar);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, int64 &data, int16 *null)
{
  int result;
  if (null)
    result = JP_BindLongNull(*this, (text*)name, bindNo, data, *null);
  else
    result = JP_BindLong(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, int32 &data, int16 *null)
{
  int result;
  if (null)
    result = JP_BindIntNull(*this, (text*)name, bindNo, data, *null);
  else
    result = JP_BindInt(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, int16 &data, int16 *null)
{
  int result;
  if (null)
    result = JP_BindShortNull(*this, (text*)name, bindNo, data, *null);
  else
    result = JP_BindShort(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, double &data, int16 *null)
{
  int result;
  if (null)
    result = JP_BindDoubleNull(*this, (text*)name, bindNo, data, *null);
  else
    result = JP_BindDouble(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, TJOCIDate &data, int16 *null)
{
  int result;
  if (null)
    result = JP_BindOCIDateNull(*this, (text*)name, bindNo, data, *null);
  else
    result = JP_BindOCIDate(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::Bind(const char* name, int bindNo, OCILobLocator *data, int lobType, int16 *null)
{
  int result;
  if (null)
    result = JP_BindLobNull(*this, (text*)name, bindNo, data, lobType, *null);
  else
    result = JP_BindLob(*this, (text*)name, bindNo, data, lobType);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, char *data, int dataLen, int ansichar)
{
  int result;
  result = JP_BindStringArray(*this, (text*)name, bindNo, data, dataLen, ansichar);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, int64 *data)
{
  int result;
  result = JP_BindLongArray(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, int32 *data)
{
  int result;
  result = JP_BindIntArray(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, int16 *data)
{
  int result;
  result = JP_BindShortArray(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, double *data)
{
  int result;
  result = JP_BindDoubleArray(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* name, int bindNo, TJOCIDate *data)
{
  int result;
  result = JP_BindOCIDateArray(*this, (text*)name, bindNo, data);
  if (result)
    conn.ThrowOnError(file, line);
}

void TJQuery::BindArray(const char* Name, int bindNo, OCILobLocator *Data, int lobType)
{
}

void TJQuery::Define(int defineNo, char *data, int dataLen, int ansichar)
{
  if (JP_DefineString(*this, defineNo, data, dataLen, ansichar))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, int64 *data)
{
  if (JP_DefineLong(*this, defineNo, data))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, int32 *data)
{
  if (JP_DefineInt(*this, defineNo, data))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, int16 *data)
{
  if (JP_DefineShort(*this, defineNo, data))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, double *data)
{
  if (JP_DefineDouble(*this, defineNo, data))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, TJOCIDate *data)
{
  if (JP_DefineDate(*this, defineNo, data))
    conn.ThrowOnError(file, line);
}

void TJQuery::Define(int defineNo, OCILobLocator *data, int lobType)
{
}

void TJQuery::Exec()
{
  if (JP_Exec(*this))
    conn.ThrowOnError(file, line);
}

bool TJQuery::Fetch()
{
  int result = JP_Fetch(*this);
  if (result == OCI_NO_DATA)
    return false;
  if (result)
    conn.ThrowOnError(file, line);
  return true;
}

void TJQuery::Deliver(int doit)
{
  if (JP_Deliver(*this, doit))
    conn.ThrowOnError(file, line);
}

void TJQuery::Get(char *into, char *from, int dataLen)
{
  JP_GetString(*this, into, from, dataLen);
}

void TJQuery::Get(int64 &into, char *from)
{
  JP_GetLong(*this, into, from);
}

void TJQuery::Get(int32 &into, char *from)
{
  JP_GetInt(*this, into, from);
}

void TJQuery::Get(int16 &into, char *from)
{
  JP_GetShort(*this, into, from);
}

void TJQuery::Get(double &into, char *from)
{
  JP_GetDouble(*this, into, from);
}

void TJQuery::Get(TJDate into, char *from)
{
  JP_GetDate(*this, into.date, from);
}

void TJQuery::Get(TJTime into, char *from)
{
  JP_GetTime(*this, into.time, from);
}

void TJQuery::Get(TJDateTime into, char *from)
{
  JP_GetDateTime(*this, into.dateTime, from);
}

void TJQuery::Get(TJLob &into, char *from)
{
  JP_GetLob(*this, into, from);
}

void TJQuery::GetNull(int16 &into, int defineNo)
{
  JP_GetNull(*this, into, defineNo);
}

void TJQuery::Put(char *into, char *from, int dataLen)
{
  JP_PutString(*this, into, from, dataLen);
}

void TJQuery::Put(char *into, int64 &from)
{
  JP_PutLong(*this, into, from);
}

void TJQuery::Put(char *into, int32 &from)
{
  JP_PutInt(*this, into, from);
}

void TJQuery::Put(char *into, int16 &from)
{
  JP_PutShort(*this, into, from);
}

void TJQuery::Put(char *into, double &from)
{
  JP_PutDouble(*this, into, from);
}

void TJQuery::Put(char *into, TJDate from)
{
  JP_PutDate(*this, into, from.date);
}

void TJQuery::Put(char *into, TJTime from)
{
  JP_PutTime(*this, into, from.time);
}

void TJQuery::Put(char *into, TJDateTime from)
{
  JP_PutDateTime(*this, into, from.dateTime);
}

void TJQuery::Put(char *into, TJLob &from)
{
  JP_PutLob(*this, into, from);
}

void TJQuery::PutNull(int16 &from, int bindNo)
{
  JP_PutNull(*this, from, bindNo);
}

void TJQuery::Clear()
{
  if (noRows && sizeRow)
    memset(data, 0, noRows * sizeRow);
  if (noIndicators)
    memset(indicators, 0, noIndicators * sizeof(short));
}

void TJQuery::Close()
{
  int rc=conn.result;
  if (JP_Close(*this) && rc == 0)
    conn.ThrowOnError(file, line);
}

int& TJQuery::Sequence(int &sequence, char *sequencer)
{
  JP_Sequence(*this, sequence, sequencer);
  return sequence;
}

char* TJQuery::UserStamp(char* userStamp)
{
  JP_UserStamp(*this, userStamp);
  return userStamp;
}

TJOCIDate& TJQuery::Date(TJOCIDate &ocidate, char *date)
{
  JP_Date(*this, ocidate, date);
  return ocidate;
}

TJOCIDate& TJQuery::Time(TJOCIDate &ocidate, char *time)
{
  JP_Time(*this, ocidate, time);
  return ocidate;
}

TJOCIDate& TJQuery::DateTime(TJOCIDate &ocidate, char *dateTime)
{
  JP_DateTime(*this, ocidate, dateTime);
  return ocidate;
}

TJOCIDate& TJQuery::TimeStamp(TJOCIDate &ocidate, char *timeStamp)
{
  JP_TimeStamp(*this, ocidate, timeStamp);
  return ocidate;
}

OCILobLocator* TJQuery::LobLocator(OCILobLocator* lobLocator, TJLob &lobData)
{
  return 0;
}

JP_INTERNAL(void) _SqlError(TJConnector &conn)
{
  if (conn.result != 0)
  {
    if (conn.ociError)
    {
      OCIErrorGet(conn.ociError,
                  1,
                  0,
                  &conn.ociErrCode,
                  conn.msgbuf,
                  sizeof(conn.msgbuf),
                  OCI_HTYPE_ERROR);
    }
    else
      sprintf((char*)conn.msgbuf, "Oracle error no handle (RC==%d)", conn.result);
  }
  else
    strcpy((char*)conn.msgbuf, "No oracle error (RC==0)");
}

JP_INTERNAL(int) _Result(TJConnector &conn)
{
  _SqlError(conn);
  return conn.result;// ? conn.ociErrCode : 0;
}

JP_INTERNAL(int) _Result(TJQuery &qry)
{
  _SqlError(qry.conn);
  return qry.conn.result;// ? qry.conn.ociErrCode : 0;
}

JP_INTERNAL(int) JP_Logon(TJConnector &conn, const text* aUser, const text* aPassword, const text* aServer)
{
  conn.user = strdup((char*) aUser);
  conn.server = strdup((char*) aServer);
  conn.result = OCIInitialize(OCI_DEFAULT, 0, 0, 0, 0);
  if (conn.result == 0)
    conn.result = OCIEnvInit(&conn.ociEnv, OCI_DEFAULT, 0, 0);
  if (conn.result == 0)
    conn.result = OCIHandleAlloc(conn.ociEnv, (void **)&conn.ociError, OCI_HTYPE_ERROR, 0, 0);
  if (conn.result == 0)
    conn.result = OCILogon(conn.ociEnv, conn.ociError, &conn.ociSvcCtx,
                      TJ_CAST aUser,     strlen((char*)aUser),
                      TJ_CAST aPassword, strlen((char*)aPassword),
                      TJ_CAST aServer,   strlen((char*)aServer));
  return _Result(conn);
}

JP_INTERNAL(int) JP_Logoff(TJConnector &conn)
{
  if (conn.user) free(conn.user);
  if (conn.server) free(conn.server);
  conn.result = OCILogoff(conn.ociSvcCtx, conn.ociError);
  conn.ociSvcCtx = 0;
  conn.user = 0;
  conn.server = 0;
  return _Result(conn);
}

JP_INTERNAL(int) JP_Commit(TJConnector &conn)
{
  conn.result = OCITransCommit(conn.ociSvcCtx, conn.ociError,
                                  OCI_DEFAULT);
  return _Result(conn);
}

JP_INTERNAL(int) JP_Rollback(TJConnector &conn)
{
  conn.result = OCITransRollback(conn.ociSvcCtx, conn.ociError,
                                    OCI_DEFAULT);
  return _Result(conn);
}

JP_EXTERNAL(int) JP_Error(TJConnector &conn, char *Msg, int MsgLen)
{
  MsgLen--;
  memcpy(Msg, conn.msgbuf, MsgLen > sizeof(conn.msgbuf) ? sizeof(conn.msgbuf) : MsgLen);
  Msg[MsgLen] = 0;
  return conn.result;
}

JP_INTERNAL(int) JP_Open(TJQuery &qry, const text* Query)
{
  if (qry.isOpen == 0)
  {
    qry.conn.result = OCIHandleAlloc(qry.conn.ociEnv,
                              (void**)&qry.ociStmt,
                              OCI_HTYPE_STMT,
                              0, 0);
    if (qry.conn.result == 0)
      qry.conn.result = OCIStmtPrepare(qry.ociStmt,
                                qry.conn.ociError,
                                TJ_CAST Query,
                                strlen((char*)Query),
                                OCI_NTV_SYNTAX,
                                OCI_DEFAULT);
    if (qry.conn.result == 0)
      qry.isOpen = 1;
  }
  return _Result(qry);
}

JP_INTERNAL(int) JP_Exec(TJQuery &qry)
{
  qry.rowsDone = 0;
  qry.rowsRead = 0;
  qry.rowsIndex = 0;
  qry.error = 0;
  qry.conn.result = OCIStmtExecute(qry.conn.ociSvcCtx,
                                   qry.ociStmt,
                                   qry.conn.ociError,
                                   qry.noDefines ? 0 : 1,
                                   0, 0, 0,
                                   qry.noDefines ? OCI_DEFAULT : OCI_EXACT_FETCH);
  return _Result(qry);
}

JP_INTERNAL(int) JP_Fetch(TJQuery &qry)
{
  if (qry.rowsDone >= qry.rowsRead)
  {
    if (qry.error == OCI_NO_DATA)
      return qry.error;
    if (qry.rowsDone > 0)
      qry.Clear();
    qry.conn.result = OCIStmtFetch(qry.ociStmt,
                              qry.conn.ociError,
                              qry.noRows,
                              OCI_FETCH_NEXT,
                              OCI_DEFAULT);
    if (qry.conn.result)
    {
      qry.error = _Result(qry);
      if (qry.error != OCI_NO_DATA)
        return qry.error;
      qry.conn.result = 0;
    }
    qry.rowsIndex = 0;
    sword AGRC = OCIAttrGet(qry.ociStmt,
                 OCI_HTYPE_STMT,
                 &qry.rowsRead,
                 0,
                 OCI_ATTR_ROW_COUNT,
                 qry.conn.ociError);
    if (AGRC)
    {
      qry.conn.result = AGRC;
      return _Result(qry);
    }
    if (qry.rowsRead == 0
    || qry.rowsRead == qry.rowsDone)
      return qry.error;
  }
  else
    qry.rowsIndex++;
  qry.rowsDone++;
  return _Result(qry);
}

JP_INTERNAL(int) JP_Deliver(TJQuery &qry, int doit)
{
  if (qry.rowsIndex+1 >= qry.noRows
  || (qry.rowsIndex > 0 && doit == 1))
  {
    if (doit == 0)
      qry.rowsIndex++;
    qry.conn.result = OCIStmtExecute(qry.conn.ociSvcCtx,
                                     qry.ociStmt,
                                     qry.conn.ociError,
                                     qry.rowsIndex,
                                     0, 0, 0, OCI_DEFAULT);
    if (qry.conn.result == 0 && qry.doCommit != 0)
      JP_Commit(qry.conn);
    qry.rowsIndex=0;
  }
  else
    qry.rowsIndex++;
  return _Result(qry);
}

JP_INTERNAL(int) JP_Close(TJQuery &qry)
{
  int result = 0;
  if (qry.isOpen == 1)
  {
    if (qry.command)
    {
      delete [] qry.command;
    }
    if (qry.data)
    {
      delete [] qry.data;
    }
    if (qry.indicators)
    {
      delete [] qry.indicators;
    }
    if (qry.ociBinds)
    {
      delete [] qry.ociBinds;
    }
    if (qry.ociDefines)
    {
      delete [] qry.ociDefines;
    }
    if (qry.ociLobs)
    {
      int i;
      for (i=0; i<qry.noLobs; i++)
        OCIDescriptorFree(qry.ociLobs[i], OCI_DTYPE_LOB);
      delete [] qry.ociLobs;
    }
    if (qry.ociStmt)
    {
      result = OCIHandleFree(qry.ociStmt, OCI_HTYPE_STMT);
    }
    qry.data = 0;
    qry.indicators = 0;
    qry.ociBinds = 0;
    qry.ociLobs = 0;
    qry.ociDefines = 0;
    qry.ociStmt = 0;
    qry.isOpen = 0;
    if (qry.conn.result == 0)
    {
      qry.conn.result = result;
      return _Result(qry);
    }
  }
  return result;
}

JP_INTERNAL(void) JP_GetString(TJQuery &qry, char *into, char *from, int dataLen)
{
  memcpy(into, from+(qry.rowsIndex*dataLen), dataLen);
}

JP_INTERNAL(void) JP_GetLong(TJQuery &qry, int64 &into, char *from)
{
  into = *(int64 *)(from+(qry.rowsIndex*sizeof(int64)));
}

JP_INTERNAL(void) JP_GetInt(TJQuery &qry, int32 &into, char *from)
{
  into = *(int32 *)(from+(qry.rowsIndex*sizeof(int32)));
}

JP_INTERNAL(void) JP_GetShort(TJQuery &qry, int16 &into, char *from)
{
  into = *(int16 *)(from+(qry.rowsIndex*sizeof(int16)));
}

JP_INTERNAL(void) JP_GetDouble(TJQuery &qry, double &into, char *from)
{
  into = *(double *)(from+(qry.rowsIndex*sizeof(double)));
}

JP_INTERNAL(void) JP_GetDate(TJQuery &qry, char *into, char *from)
{
  int date;
  int time;
  _FromOracleDate(*(TJOCIDate*)(from+qry.rowsIndex*8), date, time);
  sprintf(into, "%08.8d", date);
}

JP_INTERNAL(void) JP_GetTime(TJQuery &qry, char *into, char *from)
{
  int date;
  int time;
  _FromOracleDate(*(TJOCIDate*)(from+qry.rowsIndex*8), date, time);
  sprintf(into, "%06.6d", time);
}

JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry, char *into, char *from)
{
  int date;
  int time;
  _FromOracleDate(*(TJOCIDate*)(from+qry.rowsIndex*8), date, time);
  sprintf(into, "%08.8d%06.6d", date, time);
}

JP_INTERNAL(void) JP_GetLob(TJQuery &qry, TJLob &into, char *from)
{
}

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int16 &into, int defineNo)
{
  into = qry.indicators[qry.noBinds+defineNo*qry.noRows+qry.rowsIndex];
}

JP_INTERNAL(void) JP_PutString(TJQuery &qry, char *into, char *from, int dataLen)
{
  memcpy(into+(qry.rowsIndex*dataLen), from, dataLen);
}

JP_INTERNAL(void) JP_PutLong(TJQuery &qry, char *into, int64 &from)
{
  *(int64 *)(into+(qry.rowsIndex*sizeof(int64))) = from;
}

JP_INTERNAL(void) JP_PutInt(TJQuery &qry, char *into, int32 &from)
{
  *(int32 *)(into+(qry.rowsIndex*sizeof(int32))) = from;
}

JP_INTERNAL(void) JP_PutShort(TJQuery &qry, char *into, int16 &from)
{
  *(int16 *)(into+(qry.rowsIndex*sizeof(int16))) = from;
}

JP_INTERNAL(void) JP_PutDouble(TJQuery &qry, char *into, double &from)
{
  *(double *)(into+(qry.rowsIndex*sizeof(double))) = from;
}

JP_INTERNAL(void) JP_PutDate(TJQuery &qry, char *into, char *from)
{
  char date[9];
  strncpy(date, from, 8); date[8]=0;
  _ToOracleDate(*(TJOCIDate*)(into+qry.rowsIndex*7), atol(date), 0);
}

JP_INTERNAL(void) JP_PutTime(TJQuery &qry, char *into, char *from)
{
  char time[7];
  strncpy(time, from, 6); time[6]=0;
  _ToOracleDate(*(TJOCIDate*)(into+qry.rowsIndex*7), 0, atol(time));
}

JP_INTERNAL(void) JP_PutDateTime(TJQuery &qry, char *into, char *from)
{
  char date[9];
  char time[7];
  strncpy(date, from, 8);   date[8]=0;
  strncpy(time, from+8, 6); time[6]=0;
  _ToOracleDate(*(TJOCIDate*)(into+qry.rowsIndex*7), atol(date), atol(time));
}

JP_INTERNAL(void) JP_PutLob(TJQuery &qry, char *into, TJLob  &from)
{
}

JP_INTERNAL(void) JP_PutNull(TJQuery &qry, int16 &from, int bindNo)
{
  qry.indicators[qry.noRows*bindNo+qry.rowsIndex] = from;
}

JP_INTERNAL(void) JP_Setup(TJQuery &qry, int noBinds, int noDefines, int noRows, int noLobs, int sizeRow)
{
  qry.noBinds   = noBinds;
  qry.noDefines = noDefines;
  qry.noLobs    = noLobs;
  qry.noRows    = noRows;
  qry.sizeRow   = sizeRow;
  int noIndicators = qry.noIndicators = noBinds + (noDefines * noRows);
  if (noDefines && qry.ociDefines == 0)
    qry.ociDefines = new OCIDefine* [noDefines];
  if (noBinds && qry.ociBinds == 0)
    qry.ociBinds = new OCIBind* [noBinds];
  if (noLobs && qry.ociLobs == 0)
  {
  }
  if (noRows && noDefines && sizeRow)
  {
    if (qry.data == 0)
      qry.data = new char [noRows * sizeRow];
    memset(qry.data, 0, noRows * sizeRow);
  }
  if (noIndicators)
  {
    if (qry.indicators == 0)
      qry.indicators = new short [noIndicators];
    memset(qry.indicators, 0, noIndicators * sizeof(short));
  }
}

JP_INTERNAL(void) JP_SetupArray(TJQuery &qry, int noBinds, int noRows, int noLobs, int sizeRow)
{
  qry.noDefines = 0;
  qry.noBinds   = noBinds;
  qry.noRows    = noRows;
  qry.noLobs    = noLobs;
  qry.sizeRow   = sizeRow;
  int noIndicators = qry.noIndicators = noBinds * noRows;
  if (qry.ociBinds == 0)
    qry.ociBinds = new OCIBind* [noBinds];
  if (qry.data == 0)
    qry.data = new char [noRows * sizeRow];
  memset(qry.data, 0, noRows * sizeRow);
  if (qry.indicators == 0)
    qry.indicators = new short [noIndicators];
  memset(qry.indicators, 0, noIndicators * sizeof(short));
}

JP_INTERNAL(void) JP_CheckError(TJConnector &conn, char *file, int line)
{
  if (conn.result != 0)
    throw TOciApiException(conn.result, (char*)conn.msgbuf, file, line);
}

JP_INTERNAL(void) _BindByName(TJQuery &qry, const text* name, int bindNo, void *data, int dataLen, ub2 dataType, sb2 isNull)
{
  qry.indicators[bindNo] = isNull == JP_NOT_NULL ? JP_NOT_NULL : JP_NULL;
  qry.conn.result = OCIBindByName(qry.ociStmt,
                            &qry.ociBinds[bindNo],
                            qry.conn.ociError,
                    TJ_CAST name,
                            -1,
                            data,
                            dataLen,
                            dataType,
                            &qry.indicators[bindNo],
                            0, 0, 0, 0, OCI_DEFAULT);
}

JP_INTERNAL(void) _BindByNameArray(TJQuery &qry, const text* name, int bindNo, void *data, int dataLen, ub2 dataType)
{
  qry.conn.result = OCIBindByName(qry.ociStmt,
                            &qry.ociBinds[bindNo],
                            qry.conn.ociError,
                    TJ_CAST name,
                            -1,
                            data,
                            dataLen,
                            dataType,
                            &qry.indicators[bindNo*qry.noRows],
                            0, 0, 0, 0, OCI_DEFAULT);
}

JP_INTERNAL(void) _DefineByPos(TJQuery &qry, int defineNo, void *data, int dataLen, ub2 dataType)
{
  qry.conn.result = OCIDefineByPos(qry.ociStmt,
                            &qry.ociDefines[defineNo],
                            qry.conn.ociError,
                            defineNo+1,
                            data,
                            dataLen,
                            dataType,
                            &qry.indicators[qry.noBinds+defineNo*qry.noRows],
                            0, 0, OCI_DEFAULT);
}

JP_INTERNAL(int) JP_BindString(TJQuery &qry, text* name, int bindNo, char *data, int dataLen, int ansichar)
{
  _BindByName(qry, name, bindNo, data, dataLen, ansichar ? SQLT_CHR : SQLT_STR);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindStringNull(TJQuery &qry, text* name, int bindNo, char *data, int dataLen, short &null, int ansichar)
{
  _BindByName(qry, name, bindNo, data, dataLen, ansichar ? SQLT_CHR : SQLT_STR, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindStringArray(TJQuery &qry, text* name, int bindNo, char *data, int dataLen, int ansichar)
{
  _BindByName(qry, name, bindNo, data, dataLen, ansichar ? SQLT_CHR : SQLT_STR);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLong(TJQuery &qry, text* name, int bindNo, int64 &data)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLongNull(TJQuery &qry, text* name, int bindNo, int64 &data, int16 &null)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLongArray(TJQuery &qry, text* name, int bindNo, int64 *data)
{
  _BindByName(qry, name, bindNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindInt(TJQuery &qry, text* name, int bindNo, int32 &data)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindIntNull(TJQuery &qry, text* name, int bindNo, int32 &data, int16 &null)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindIntArray(TJQuery &qry, text* name, int bindNo, int32 *data)
{
  _BindByName(qry, name, bindNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindShort(TJQuery &qry, text* name, int bindNo, int16 &data)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindShortNull(TJQuery &qry, text* name, int bindNo, int16 &data, int16 &null)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_INT, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindShortArray(TJQuery &qry, text* name, int bindNo, int16 *data)
{
  _BindByName(qry, name, bindNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindDouble(TJQuery &qry, text* name, int bindNo, double &data)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_FLT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindDoubleNull(TJQuery &qry, text* name, int bindNo, double &data, short &null)
{
  _BindByName(qry, name, bindNo, &data, sizeof(data), SQLT_FLT, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindDoubleArray(TJQuery &qry, text* name, int bindNo, double *data)
{
  _BindByName(qry, name, bindNo, data, sizeof(*data), SQLT_FLT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindOCIDate(TJQuery &qry, text* name, int bindNo, TJOCIDate &data)
{
  _BindByName(qry, name, bindNo, &data, 7, SQLT_DAT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindOCIDateNull(TJQuery &qry, text* name, int bindNo, TJOCIDate &data, short &null)
{
  _BindByName(qry, name, bindNo, &data, 7, SQLT_DAT, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindOCIDateArray(TJQuery &qry, text* name, int bindNo, TJOCIDate *data)
{
  _BindByName(qry, name, bindNo, data, 7, SQLT_DAT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLob(TJQuery &qry, text* name, int bindNo, OCILobLocator *data, int lobType)
{
  _BindByName(qry, name, bindNo, &data, -1, lobType);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLobNull(TJQuery &qry, text* name, int bindNo, OCILobLocator *data, int lobType, short &null)
{
  _BindByName(qry, name, bindNo, &data, -1, lobType, null);
  return _Result(qry);
}

JP_INTERNAL(int) JP_BindLobArray(TJQuery &qry, text* name, int bindNo, OCILobLocator **data, int lobType)
{
  _BindByName(qry, name, bindNo, data, -1, lobType);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineString(TJQuery &qry, int defineNo, char *data, int dataLen, int ansichar)
{
  _DefineByPos(qry, defineNo, data, dataLen, ansichar ? SQLT_CHR : SQLT_STR);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineLong(TJQuery &qry, int defineNo, int64 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineInt(TJQuery &qry, int defineNo, int32 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineShort(TJQuery &qry, int defineNo, int16 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQLT_INT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineDouble(TJQuery &qry, int defineNo, double *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQLT_FLT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineDate(TJQuery &qry, int defineNo, TJOCIDate *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(TJOCIDate), SQLT_DAT);
  return _Result(qry);
}

JP_INTERNAL(int) JP_DefineLob(TJQuery &qry, int defineNo, OCILobLocator *data, int lobType)
{
  _DefineByPos(qry, defineNo, data, -1, lobType);
  return _Result(qry);
}

JP_INTERNAL(void) JP_Sequence(TJQuery& qry, int &sequence, char *sequencer)
{
  TJQuery q(qry.conn);
  q.command = new char [32+strlen(sequencer)];
  strcpy(q.command, "select ");
  strcat(q.command, sequencer);
  strcat(q.command, ".NEXTVAL from dual");
  q.Open(q.command, 0, 1, 1, 4);
  q.Define(0, (int*) (q.data));
  q.Exec();
  if (q.Fetch())
    q.Get(sequence, q.data);
  else
    sequence = 0;
}

JP_INTERNAL(void) JP_UserStamp(TJQuery& qry, char* userStamp)
{
  strcpy(userStamp, qry.conn.user);
}

JP_INTERNAL(void) JP_Date(TJQuery& qry, TJOCIDate &ocidate, char *aDate)
{
  char date[9];
  strncpy(date, aDate, 8); date[8]=0;
  _ToOracleDate(ocidate, atol(date), 0);
}

JP_INTERNAL(void) JP_Time(TJQuery& qry, TJOCIDate &ocidate, char *aTime)
{
  char time[7];
  strncpy(time, aTime, 6); time[6]=0;
  _ToOracleDate(ocidate, 0, atol(time));
}

JP_INTERNAL(void) JP_DateTime(TJQuery& qry, TJOCIDate &ocidate, char *dateTime)
{
  char date[9];
  char time[7];
  strncpy(date, dateTime, 8);   date[8]=0;
  strncpy(time, dateTime+8, 6); time[6]=0;
  _ToOracleDate(ocidate, atol(date), atol(time));
}

JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry, TJOCIDate &ocidate, char *timeStamp)
{
  qry.conn.TimeStamp(timeStamp);
  JP_DateTime(qry, ocidate, timeStamp);
}

JP_INTERNAL(int) JP_NewConnector(TJConnector *&conn)
{
  try
  {
    conn = new TJConnector;
    return 0;
  }
  catch (...)
  {
    return -1;
  }
}

JP_INTERNAL(int) JP_NewQuery(TJQuery *&qry, TJConnector &conn)
{
  try
  {
    qry = new TJQuery(conn);
    return 0;
  }
  catch (...)
  {
    return -1;
  }
}

JP_INTERNAL(int) JP_FreeConnector(TJConnector *&conn)
{
  try
  {
    delete conn;
    conn = 0;
    return 0;
  }
  catch (...)
  {
    return -1;
  }
}

JP_INTERNAL(int) JP_FreeQuery(TJQuery *&qry)
{
  try
  {
    delete qry;
    qry = 0;
    return 0;
  }
  catch (...)
  {
    return -1;
  }
}







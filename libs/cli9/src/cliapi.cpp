#include "cliapi.h"
#include "swapbytes.h"

JP_INTERNAL(SQLRETURN) JP_Logon(TJConnector &conn, const char* User, const char* Password, const char* Server, const char* Schema=0, const SQLHANDLE *cliEnv=0);
JP_INTERNAL(SQLRETURN) JP_Logoff(TJConnector &conn);
JP_INTERNAL(SQLRETURN) JP_Commit(TJConnector &conn);
JP_INTERNAL(SQLRETURN) JP_Rollback(TJConnector &conn);
JP_INTERNAL(void) JP_SetSchema(TJConnector &conn);
JP_INTERNAL(SQLRETURN) JP_Sync(TJConnector &conn);
JP_INTERNAL(SQLRETURN) JP_SetCoordinated(TJConnector &conn);
JP_INTERNAL(SQLRETURN) JP_SetForkable(TJConnector &conn);

JP_INTERNAL(SQLRETURN) JP_Open(TJQuery &qry, const char* Query);
JP_INTERNAL(SQLRETURN) JP_Exec(TJQuery &qry);
JP_INTERNAL(SQLRETURN) JP_Fetch(TJQuery &qry);
JP_INTERNAL(SQLRETURN) JP_Close(TJQuery &qry);

JP_INTERNAL(SQLRETURN) JP_Setup(TJQuery &qry, int32 noBinds, int32 noDefines, int32 noRows, int32 sizeRow);
JP_INTERNAL(SQLRETURN) JP_ArraySetup(TJQuery &qry, int32 noBinds, int32 noNulls, int32 noRows, int32 sizeRow);

JP_INTERNAL(SQLRETURN) JP_BindString(TJQuery &qry,  int32 bindNo, char  *Data, int32 Datalen, int32 direction, int32 typeChar, int32 nullChar);
JP_INTERNAL(SQLRETURN) JP_BindBLOB(TJQuery &qry,    int32 bindNo, char  *Data, int32 Datasize, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindLong(TJQuery &qry,    int32 bindNo, int64  &Data, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindInt(TJQuery &qry,     int32 bindNo, int32   &Data, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindShort(TJQuery &qry,   int32 bindNo, int16 &Data, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindDouble(TJQuery &qry,  int32 bindNo, double &Data, int32 precision, int32 scale, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindDecimal(TJQuery &qry, int32 bindNo, char *Data, int32 precision, int32 scale, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindCLIDate(TJQuery &qry, int32 bindNo, DATE_STRUCT &Data, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindCLITime(TJQuery &qry, int32 bindNo, TIME_STRUCT &Data, int32 direction);
JP_INTERNAL(SQLRETURN) JP_BindCLIDateTime(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT &Data, int32 direction);

JP_INTERNAL(SQLRETURN) JP_BindDoubleNull(TJQuery &qry,  int32 bindNo, double  &Data, int32 precision, int32 scale, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindDecimalNull(TJQuery &qry, int32 bindNo, char    *Data, int32 precision, int32 scale, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindLongNull(TJQuery &qry,    int32 bindNo, int64   &Data, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindIntNull(TJQuery &qry,     int32 bindNo, int32   &Data, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindShortNull(TJQuery &qry,   int32 bindNo, int16   &Data, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindCLIDateNull(TJQuery &qry, int32 bindNo, DATE_STRUCT &Data, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindCLITimeNull(TJQuery &qry, int32 bindNo, TIME_STRUCT &Data, int32 direction, int16 &Null);
JP_INTERNAL(SQLRETURN) JP_BindCLIDateTimeNull(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT &Data, int32 direction, int16 &Null);

JP_INTERNAL(SQLRETURN) JP_BindCharArray(TJQuery &qry, int32 bindNo, char *data, int32 dataLen, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindAnsiCharArray(TJQuery &qry, int32 bindNo, char *data, int32 dataLen, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindInt64Array(TJQuery &qry,  int32 bindNo, int64 *data, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindInt32Array(TJQuery &qry, int32 bindNo, int32 *data, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindInt16Array(TJQuery &qry, int32 bindNo, int16 *data, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindDoubleArray(TJQuery &qry, int32 bindNo, double *data, int32 precision, int32 scale, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindDecimalArray(TJQuery &qry, int32 bindNo, char *data, int32 precision, int32 scale, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindCLIDateArray(TJQuery &qry, int32 bindNo, DATE_STRUCT *data, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindCLITimeArray(TJQuery &qry, int32 bindNo, TIME_STRUCT *data, SQLINTEGER* indicators);
JP_INTERNAL(SQLRETURN) JP_BindCLIDateTimeArray(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT *data, SQLINTEGER* indicators);

JP_INTERNAL(SQLRETURN) JP_DefineString(TJQuery &qry,   int32 defineNo, char *data, int32 dataLen, int32 typeChar);
JP_INTERNAL(SQLRETURN) JP_DefineBLOB(TJQuery &qry,     int32 defineNo, char *data, int32 dataSize);
JP_INTERNAL(SQLRETURN) JP_DefineLong(TJQuery &qry,     int32 defineNo, int64 *data);
JP_INTERNAL(SQLRETURN) JP_DefineInt(TJQuery &qry,      int32 defineNo, int32 *data);
JP_INTERNAL(SQLRETURN) JP_DefineShort(TJQuery &qry,    int32 defineNo, int16 *data);
JP_INTERNAL(SQLRETURN) JP_DefineDouble(TJQuery &qry,   int32 defineNo, double *data);
JP_INTERNAL(SQLRETURN) JP_DefineDecimal(TJQuery &qry,  int32 defineNo, char *data, int32 dataSize);
JP_INTERNAL(SQLRETURN) JP_DefineDate(TJQuery &qry,     int32 defineNo, DATE_STRUCT *data);
JP_INTERNAL(SQLRETURN) JP_DefineTime(TJQuery &qry,     int32 defineNo, TIME_STRUCT *data);
JP_INTERNAL(SQLRETURN) JP_DefineDateTime(TJQuery &qry, int32 defineNo, TIMESTAMP_STRUCT *data);

JP_INTERNAL(void) JP_GetString(TJQuery &qry,     char   *into, char *from, int32 dataLen);
JP_INTERNAL(void) JP_GetBLOB(TJQuery &qry,       int32 &intolen, unsigned char *into, char *from, int32 dataSize);
JP_INTERNAL(void) JP_GetLong(TJQuery &qry,       int64  &into, char *from);
JP_INTERNAL(void) JP_GetInt(TJQuery &qry,        int32  &into, char *from);
JP_INTERNAL(void) JP_GetShort(TJQuery &qry,      int16  &into, char *from);
JP_INTERNAL(void) JP_GetDouble(TJQuery &qry,     double &into, char *from);
JP_INTERNAL(void) JP_GetDecimal(TJQuery &qry,    char   *into, char *from, int32 dataLen);
JP_INTERNAL(void) JP_GetDate(TJQuery &qry,       char   *into, char *from);
JP_INTERNAL(void) JP_GetTime(TJQuery &qry,       char   *into, char *from);
JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry,   char   *into, char *from);

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int16 &into, int32 defineNo);
JP_INTERNAL(void) JP_Sequence(TJQuery& qry, int32 &sequence, char *sequencer);
JP_INTERNAL(void) JP_BigSequence(TJQuery& qry, int64 &sequence, char *sequencer);
JP_INTERNAL(void) JP_UserStamp(TJQuery& qry, char* userStamp);
JP_INTERNAL(void) JP_Date(TJQuery& qry, DATE_STRUCT &clidate, char *date);
JP_INTERNAL(void) JP_Time(TJQuery& qry, TIME_STRUCT &clitime, char *time);
JP_INTERNAL(void) JP_DateTime(TJQuery& qry, TIMESTAMP_STRUCT &cliDateTime, char *dateTime);
JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry, TIMESTAMP_STRUCT &cliTimeStamp, char *timeStamp);

JP_INTERNAL(int32) JP_JulianDate(int32 year, int32 month, int32 day);
JP_INTERNAL(int32) JP_JulianDate(char* YYYYMMDD);
JP_INTERNAL(int32) JP_Seconds(char* HHMISS);
JP_INTERNAL(void) JP_CalendarDate(int32 juldate, int32& year, int32& month, int32& day);
JP_INTERNAL(void) JP_CalendarDate(int32 jul, char* YYYYMMDD);
JP_INTERNAL(void) _FromDate(DATE_STRUCT& cliDate, int32& yyyymmdd);
JP_INTERNAL(void) _ToDate(DATE_STRUCT& cliDate, int32 yyyymmdd);
JP_INTERNAL(void) _FromTime(TIME_STRUCT& cliTime, int32& hhmmss);
JP_INTERNAL(void) _ToTime(TIME_STRUCT& cliTime, int32 hhmmss);
JP_INTERNAL(void) _FromDateTime(TIMESTAMP_STRUCT& cliDateTime, int32& yyyymmdd, int32& hhmmss);
JP_INTERNAL(void) _ToDateTime(TIMESTAMP_STRUCT& cliDateTime, int32 yyyymmdd, int32 hhmmss);
JP_INTERNAL(void) _SqlError(TJConnector &conn);
JP_INTERNAL(SQLRETURN) _Result(TJConnector &conn);
JP_INTERNAL(SQLRETURN) _Result(TJQuery &qry);
JP_INTERNAL(void) _BindByPos(TJQuery &qry
                             , SQLUSMALLINT bindPos
                             , SQLSMALLINT inputOutput
                             , SQLSMALLINT cType
                             , SQLSMALLINT sqlType
                             , SQLULEN precision
                             , SQLSMALLINT scale
                             , void *data
                             , SQLULEN dataLen
                             , int16 isNull);
JP_INTERNAL(void) _BindByPosArray(TJQuery &qry
                                  , SQLUSMALLINT bindPos
                                  , SQLSMALLINT cType
                                  , SQLSMALLINT sqlType
                                  , SQLULEN precision
                                  , SQLSMALLINT scale
                                  , void *data
                                  , SQLULEN dataLen
                                  , SQLINTEGER* indicators);
JP_INTERNAL(void) _DefineByPos(TJQuery &qry
                               , int32 defineNo
                               , void *data
                               , int32 dataLen
                               , SQLSMALLINT dataType);

JP_INTERNAL(int32) JP_JulianDate(int32 year, int32 month, int32 day)
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
  int32 century = year / 100;
  int32 cy = year - 100 * century;
  return (146097 * century) / 4
    + (1461 * cy) / 4
    + (153 * month + 2) / 5
    + day
    + 1721119;
}

JP_INTERNAL(int32) JP_JulianDate(char* YYYYMMDD)
{
  int32 day, month, year;
  char work[5];
  strncpy(work, YYYYMMDD, 4);   work[4] = 0; year  = atoi(work);
  strncpy(work, YYYYMMDD+4, 2); work[2] = 0; month = atoi(work);
  strncpy(work, YYYYMMDD+6, 2); work[2] = 0; day   = atoi(work);
  return  JP_JulianDate(year, month, day);
}

JP_INTERNAL(int32) JP_Seconds(char* HHMISS)
{
  int32 secs, mins, hours;
  char work[5];
  strncpy(work, HHMISS, 2);     work[2] = 0; hours = atoi(work);
  strncpy(work, HHMISS+2, 2);   work[2] = 0; mins  = atoi(work);
  strncpy(work, HHMISS+4, 2);   work[2] = 0; secs  = atoi(work);
  return  hours*60*60+mins*60+secs;
}

JP_INTERNAL(void) JP_CalendarDate(int32 juldate, int32& year, int32& month, int32& day)
{
  int32 temp = juldate - 1721119;
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

JP_INTERNAL(void) JP_CalendarDate(int32 jul, char* YYYYMMDD)
{
  int32 day, month, year;
  JP_CalendarDate(jul, year, month, day);
  char work[20];
  sprintf(work, "%4.4d%2.2d%2.2d", year, month, day);
  strncpy(YYYYMMDD, work, 8);
}

JP_INTERNAL(void) _FromDate(DATE_STRUCT& cliDate, int32& yyyymmdd)
{
  yyyymmdd = cliDate.year * 10000 + cliDate.month * 100 + cliDate.day;
}

JP_INTERNAL(void) _ToDate(DATE_STRUCT& cliDate, int32 yyyymmdd)
{
  cliDate.day   = (SQLUSMALLINT)(yyyymmdd % 100); yyyymmdd /= 100;
  cliDate.month = (SQLUSMALLINT)(yyyymmdd % 100); yyyymmdd /= 100;
  cliDate.year  = (SQLSMALLINT)yyyymmdd;
}

JP_INTERNAL(void) _FromTime(TIME_STRUCT& cliTime, int32& hhmmss)
{
  hhmmss = cliTime.second + cliTime.minute * 100 + cliTime.hour * 10000;
}

JP_INTERNAL(void) _ToTime(TIME_STRUCT& cliTime, int32 hhmmss)
{
  cliTime.second = (SQLUSMALLINT)(hhmmss % 100); hhmmss /= 100;
  cliTime.minute = (SQLUSMALLINT)(hhmmss % 100); hhmmss /= 100;
  cliTime.hour = (SQLUSMALLINT)hhmmss;
}

JP_INTERNAL(void) _FromDateTime(TIMESTAMP_STRUCT& cliDateTime, int32& yyyymmdd, int32& hhmmss)
{
  yyyymmdd = cliDateTime.year * 10000 + cliDateTime.month * 100 + cliDateTime.day;
  hhmmss = cliDateTime.second + cliDateTime.minute * 100 + cliDateTime.hour * 10000;
}

JP_INTERNAL(void) _ToDateTime(TIMESTAMP_STRUCT& cliDateTime, int32 yyyymmdd, int32 hhmmss)
{
  cliDateTime.day   = (SQLUSMALLINT)(yyyymmdd % 100); yyyymmdd /= 100;
  cliDateTime.month = (SQLUSMALLINT)(yyyymmdd % 100); yyyymmdd /= 100;
  cliDateTime.year  = (SQLSMALLINT)yyyymmdd;
  cliDateTime.second = (SQLUSMALLINT)(hhmmss % 100); hhmmss /= 100;
  cliDateTime.minute = (SQLUSMALLINT)(hhmmss % 100); hhmmss /= 100;
  cliDateTime.hour = (SQLUSMALLINT)hhmmss;
  cliDateTime.fraction = (SQLINTEGER)0;
}

bool TJAlloc::Failed = false;

void TJConnector::Logon(const char* User, const char* Password, const char* Server, const char* Schema, const SQLHANDLE *cliEnv)
{
  if (JP_Logon(*this, User, Password, Server, Schema, cliEnv))
    ThrowOnError(JP_MARK);
  loggedOn = 1;
}

void TJConnector::Logoff()
{
  if (copyOf == true)
    return;
  if (loggedOn == 0 || JP_Logoff(*this))
    ThrowOnError(JP_MARK);
  loggedOn = 0;
}

void TJConnector::Commit()
{
  if (loggedOn == 0 || JP_Commit(*this))
    ThrowOnError(JP_MARK);
}

void TJConnector::Rollback()
{
  if (loggedOn == 0 || JP_Rollback(*this))
    ThrowOnError(JP_MARK);
}

void TJConnector::Sync()
{
  if (JP_Sync(*this))  
    ThrowOnError(JP_MARK);
}

void TJConnector::Error(char *Msg, int32 MsgLen)
{
  if (JP_Error(*this, Msg, MsgLen))
    ThrowOnError(JP_MARK);
  loggedOn = 0;
}

void TJConnector::ThrowOnError(char *file, int32 line)
{
  if (result != 0)
    throw TCliApiException(result, state, allow, msgbuf, file, line);
}

bool TJConnector::Allow(SQLRETURN value)
{
  switch (value)
  {
  case SQL_SUCCESS:
    strncpy(allow, "SUCCESS", sizeof(allow)-1);
    return true;
  case SQL_INVALID_HANDLE:
    strncpy(allow, "INVALID_HANDLE", sizeof(allow)-1);
    return false;
  case SQL_ERROR:
    strncpy(allow, "ERROR", sizeof(allow)-1);
    return false;
  case SQL_SUCCESS_WITH_INFO:
    strncpy(allow, "SUCCESS_WITH_INFO", sizeof(allow)-1);
    return true;
  case SQL_STILL_EXECUTING:
    strncpy(allow, "STILL_EXECUTING", sizeof(allow)-1);
    return false;
  case SQL_NEED_DATA:
    strncpy(allow, "NEED_DATA", sizeof(allow)-1);
    return false;
  case SQL_NO_DATA_FOUND:
    strncpy(allow, "NO_DATA_FOUND", sizeof(allow)-1);
    return true;
  default:
    strncpy(allow, "UNDEFINED", sizeof(allow)-1);
    return false;
  }
}

TJQuery::~TJQuery()
{
  Close();
}

static time_t removal_constant;
#if defined(M_AIX) || defined(M_GNU)
//#include <math.h>
#include <sys/time.h>

static void timewithfraction(time_t &seconds, time_t &fraction)
{
  struct timeval now;
  gettimeofday (&now, 0);
  seconds = now.tv_sec;
  fraction = now.tv_usec;
}
#else
static void timewithfraction(time_t &seconds, time_t &fraction)
{
  DWORD time_millis = timeGetTime();
  time(&seconds);
  fraction = (time_t)time_millis % 1000;
  fraction *= 1000;
}
#endif

void TJConnector::TimeStamp(char *timeStamp, int &fraction)
{
  enum { NO_BINDS = 0
    , NO_DEFINES = 1
    , NO_ROWS = 1
    , ROW_SIZE = sizeof(TIMESTAMP_STRUCT)
  };
  const char command[] = "SELECT CURRENT_TIMESTAMP from SYSIBM.SYSDUMMY1";
  time_t system_seconds, system_fraction, server_seconds, server_fraction;
  if ((int)floor(serverTime) == 0)
  {
    removal_constant = 1325376000;
    timewithfraction(system_seconds, system_fraction);
    TJQuery q(*this);
    q.command = TJAlloc::newChar(sizeof(command));
    strcpy(q.command, command);
    q.Open(q.command, NO_BINDS, NO_DEFINES, NO_ROWS, ROW_SIZE);
    q.Define(0, (TIMESTAMP_STRUCT*) (q.data));
    q.Exec();
    TIMESTAMP_STRUCT* server_ts = (TIMESTAMP_STRUCT*) (q.data);
    serverTime = systemTime = system_seconds - removal_constant + (system_fraction / 1e6);
    if (q.Fetch())
    {
      q.Get(TJDateTime(timeStamp), q.data);
      int days = JP_JulianDate(timeStamp)-JP_JulianDate("19700101");
      int seconds = JP_Seconds(timeStamp+8);
      server_seconds = days*(24*60*60) + seconds;
      server_fraction = server_ts->fraction / 1000;
      serverTime = (server_seconds - removal_constant + (server_fraction / 1e6)) - systemTime;
    }
  }
  double work;
  timewithfraction(system_seconds, system_fraction);
  double stampTime = system_seconds - removal_constant + (system_fraction / 1e6) + serverTime;
  time_t tt = (time_t)floor(stampTime + removal_constant);
  fraction = (int)(modf(stampTime, &work) * 1e9);
  struct tm *lt;
  lt  = gmtime(&tt);
  int32 dateNow = 19000000+(10000*lt->tm_year)
    +(100L*(lt->tm_mon+1))
    +lt->tm_mday;
  int32 timeNow = 10000L*lt->tm_hour
    +100*(lt->tm_min)
    +lt->tm_sec;
  sprintf(timeStamp, "%8.8d%6.6d", dateNow, timeNow);
}

void TJQuery::Open(const char* Query, int32 noBinds, int32 noDefines, int32 noRows, int32 sizeRow)
{
  conn.command = (char*) Query;
  conn.ThrowOnError(JP_Open(*this, Query), file, line);
  conn.ThrowOnError(JP_Setup(*this, noBinds, noDefines, noRows, sizeRow), file, line);
}

void TJQuery::OpenArray(const char* Query, int32 noBinds, int32 noNulls, int32 noRows, int32 sizeRow)
{
  conn.command = (char*) Query;
  conn.ThrowOnError(JP_Open(*this, Query), file, line);
  conn.ThrowOnError(JP_ArraySetup(*this, noBinds, noNulls, noRows, sizeRow), file, line);
}

void TJQuery::Bind(int32 bindNo, char *data, int32 dataLen, int16 direction, int32 typeChar, int nullChar)
{
  conn.ThrowOnError(JP_BindString(*this, bindNo, data, dataLen, direction, typeChar, nullChar), file, line);
}

void TJQuery::BindBlob(int32 bindNo, char *data, int32 dataSize, int16 direction)
{
  conn.ThrowOnError(JP_BindBLOB(*this, bindNo, data, dataSize, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, int64 &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindLongNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindLong(*this, bindNo, data, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, int32 &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindIntNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindInt(*this, bindNo, data, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, int16 &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindShortNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindShort(*this, bindNo, data, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, double &data, int16 precision, int16 scale, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindDoubleNull(*this, bindNo, data, precision, scale, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindDouble(*this, bindNo, data, precision, scale, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, char *data, int16 precision, int16 scale, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindDecimalNull(*this, bindNo, data, precision, scale, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindDecimal(*this, bindNo, data, precision, scale, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, DATE_STRUCT &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindCLIDateNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindCLIDate(*this, bindNo, data, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, TIME_STRUCT &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindCLITimeNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindCLITime(*this, bindNo, data, direction), file, line);
}

void TJQuery::Bind(int32 bindNo, TIMESTAMP_STRUCT &data, int16 direction, int16 *null)
{
  if (null)
    conn.ThrowOnError(JP_BindCLIDateTimeNull(*this, bindNo, data, direction, *null), file, line);
  else
    conn.ThrowOnError(JP_BindCLIDateTime(*this, bindNo, data, direction), file, line);
}

void TJQuery::BindCharArray(int32 bindNo, char *data, int dataLen, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindCharArray(*this, bindNo, data, dataLen, null), file, line);
}

void TJQuery::BindAnsiCharArray(int32 bindNo, char *data, int dataLen, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindAnsiCharArray(*this, bindNo, data, dataLen, null), file, line);
}

void TJQuery::BindInt64Array(int32 bindNo, int64 *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindInt64Array(*this, bindNo, data, null), file, line);
}

void TJQuery::BindInt32Array(int32 bindNo, int32 *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindInt32Array(*this, bindNo, data, null), file, line);
}

void TJQuery::BindInt16Array(int32 bindNo, int16 *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindInt16Array(*this, bindNo, data, null), file, line);
}

void TJQuery::BindDoubleArray(int32 bindNo, double *data, int16 precision, int16 scale, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindDoubleArray(*this, bindNo, data, precision, scale, null), file, line);
}

void TJQuery::BindMoneyArray(int32 bindNo, char *data, int16 precision, int16 scale, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindDecimalArray(*this, bindNo, data, precision, scale, null), file, line);
}

void TJQuery::BindDateArray(int32 bindNo, DATE_STRUCT *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindCLIDateArray(*this, bindNo, data, null), file, line);
}

void TJQuery::BindTimeArray(int32 bindNo, TIME_STRUCT *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindCLITimeArray(*this, bindNo, data, null), file, line);
}

void TJQuery::BindDateTimeArray(int32 bindNo, TIMESTAMP_STRUCT *data, SQLINTEGER* null)
{
  conn.ThrowOnError(JP_BindCLIDateTimeArray(*this, bindNo, data, null), file, line);
}

void TJQuery::Define(int32 defineNo, char *data, int32 dataLen, int32 typeChar)
{
  conn.ThrowOnError(JP_DefineString(*this, defineNo, data, dataLen, typeChar), file, line);
}

void TJQuery::DefineBlob(int32 defineNo, char *data, int32 dataSize)
{
  conn.ThrowOnError(JP_DefineBLOB(*this, defineNo, data, dataSize), file, line);
}

void TJQuery::Define(int32 defineNo, int64 *data)
{
  conn.ThrowOnError(JP_DefineLong(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, int32 *data)
{
  conn.ThrowOnError(JP_DefineInt(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, int16 *data)
{
  conn.ThrowOnError(JP_DefineShort(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, double *data)
{
  conn.ThrowOnError(JP_DefineDouble(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, char *data, int16 precision)
{
  conn.ThrowOnError(JP_DefineDecimal(*this, defineNo, data, precision+3), file, line);
}

void TJQuery::Define(int32 defineNo, DATE_STRUCT *data)
{
  conn.ThrowOnError(JP_DefineDate(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, TIME_STRUCT *data)
{
  conn.ThrowOnError(JP_DefineTime(*this, defineNo, data), file, line);
}

void TJQuery::Define(int32 defineNo, TIMESTAMP_STRUCT *data)
{
  conn.ThrowOnError(JP_DefineDateTime(*this, defineNo, data), file, line);
}

void TJQuery::Exec()
{
  conn.ThrowOnError(JP_Exec(*this), file, line);
}

bool TJQuery::Fetch()
{
  SQLRETURN result = JP_Fetch(*this);
  if (result == SQL_NO_DATA_FOUND)
    return false;
  conn.ThrowOnError(result, file, line);
  return true;
}

void TJQuery::Get(char *into, char *from, int32 dataLen)
{
  JP_GetString(*this, into, from, dataLen);
}

void TJQuery::Get(int32 &intolen, unsigned char *into, char *from, int32 dataSize)
{
  intolen = dataSize-4;
  JP_GetBLOB(*this, intolen, into, from, dataSize);
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

void TJQuery::GetNull(int16 &into, int32 defineNo)
{
  JP_GetNull(*this, into, defineNo);
}

void TJQuery::Clear()
{
  if (noRows && sizeRow)
    memset(data, 0, noRows * sizeRow);
  if (noIndicators)
    memset(indicators, 0, noIndicators * sizeof(int16));
}

void TJQuery::Close()
{
  int32 rc=conn.result;
  if (JP_Close(*this) && rc == 0)
    conn.ThrowOnError(file, line);
}

int32& TJQuery::Sequence(int32 &sequence, char *sequencer)
{
  JP_Sequence(*this, sequence, sequencer);
  return sequence;
}

int64& TJQuery::Sequence(int64 &sequence, char *sequencer)
{
  JP_BigSequence(*this, sequence, sequencer);
  return sequence;
}

char* TJQuery::UserStamp(char* userStamp)
{
  JP_UserStamp(*this, userStamp);
  return userStamp;
}

DATE_STRUCT& TJQuery::Date(DATE_STRUCT &clidate, char *date)
{
  JP_Date(*this, clidate, date);
  return clidate;
}

TIME_STRUCT& TJQuery::Time(TIME_STRUCT &clitime, char *time)
{
  JP_Time(*this, clitime, time);
  return clitime;
}

TIMESTAMP_STRUCT& TJQuery::DateTime(TIMESTAMP_STRUCT &clidatetime, char *dateTime)
{
  JP_DateTime(*this, clidatetime, dateTime);
  return clidatetime;
}

TIMESTAMP_STRUCT& TJQuery::TimeStamp(TIMESTAMP_STRUCT &clitimestamp, char *timeStamp)
{
  JP_TimeStamp(*this, clitimestamp, timeStamp);
  return clitimestamp;
}

static char* chardup(char*& data)
{
  int len = strlen(data)+1;
  char* result = new char [len];
  strcpy(result, data);
  data += len;
  return result;
}

static int intof(char*& data)
{
  int len = strlen(data)+1;
  int result = data[1] == ':' ? atoi(data+2) : atoi(data);
  data += len;
  return result;
}

static bool boolof(char*& data)
{
  bool result = *data == 'Y';
  data += 1;
  return result;
}

static bool check(char*& data, const char* tag)
{
  bool result = strcmp(data, tag) == 0;
  int len = strlen(data)+1;
  data += len;
  return result;
}

TJLine::~TJLine()
{
  if (line != 0) delete [] line;
}

char* TJLine::load(char* p)
{
  if (p[0] == '&')
  {
    p++;
    isVar = true;
  }
  else
    isVar = false;
  line = chardup(p);
  return p;
}

TJField::~TJField()
{
  if (name != 0) delete [] name;
  if (alias != 0) delete [] alias;
}

char* TJField::load(char* p)
{
  name = chardup(p);
  alias = chardup(p);
  type = intof(p);
  length = intof(p);
  precision = intof(p);
  scale = intof(p);
  offset = intof(p);
  used = intof(p);
  isPrimaryKey = boolof(p);
  isSequence = boolof(p);
  isNull = boolof(p);
  isIn = boolof(p);
  isOut = boolof(p);
  return p+1;
}

TJDynamic::~TJDynamic()
{
  if (name != 0) delete [] name;
}

char* TJDynamic::load(char *p)
{
  name = chardup(p);
  length = intof(p);
  offset = intof(p);
  return p;
}

TJProc::~TJProc()
{
  if (table != 0) delete [] table;
  if (name != 0) delete [] name;
  if (lines != 0) delete [] lines;
  if (inputs != 0) delete [] inputs;
  if (outputs != 0) delete [] outputs;
  if (dynamics != 0) delete [] dynamics;
}

char* TJProc::load(char *p)
{
  table = chardup(p);
  name = chardup(p);
  noRows = intof(p); 
  isProc = boolof(p);
  isSProc = boolof(p);
  isData = boolof(p);
  isIdlCode = boolof(p);
  isSql = boolof(p);
  isSingle = boolof(p);
  isAction = boolof(p);
  isStd = boolof(p);
  useStd = boolof(p);
  extendsStd = boolof(p);
  useKey = boolof(p);
  hasImage = boolof(p);
  isMultipleInput = boolof(p);
  isInsert = boolof(p);
  hasReturning = boolof(p);
  p++;
  noPairs = intof(p);
  noInputs = intof(p);
  noOutputs = intof(p);
  noDynamics = intof(p);
  noLines = intof(p);
  if (noLines > 0)
  {
    lines = new TJLine[noLines];
    for (int i=0; i<noLines; i++)
      lines[i].load(p);
  }
  if (noPairs > 0)
  {
    pairs = new int [noPairs];
    for (int i=0; i<noPairs; i++)
      pairs[i] = intof(p);
  }
  if (noOutputs > 0)
  {
    outputs = new TJField[noOutputs];
    for (int i=0; i<noOutputs; i++)
    {
      bool valid = check(p, "OUT");
      outputs[i].load(p);
    }
  }
  if (noInputs > 0)
  {
    inputs = new TJField[noInputs];
    for (int i=0; i<noInputs; i++)
    {
      bool valid = check(p, "INP");
      inputs[i].load(p);
    }
  }
  if (noDynamics > 0)
  {
    dynamics = new TJDynamic[noDynamics];
    for (int i=0; i<noDynamics; i++)
    {
      bool valid = check(p, "DYN");
      dynamics[i].load(p);
    }
  }
  return p;
}

TJTable::~TJTable()
{
  if (name != 0) delete [] name;
}

char* TJTable::load(char *p)
{
  name = chardup(p);
  noProcs = intof(p);
  if (noProcs > 0)
  {
    procs = new TJProc[noProcs];
    for (int i=0; i<noProcs; i++)
    {
      bool valid = check(p, "QRY");
      procs[i].load(p);
    }
  }
  return p;
}

JP_INTERNAL(void) _SqlError(TJConnector &conn)
{
  conn.Allow(conn.result);
  conn.state[0] = 0;
  conn.msgbuf[0] = 0;
  if (conn.result != SQL_SUCCESS)
  {
    SQLSMALLINT recNumber = 1;
    SQLSMALLINT msgLength;
    SQLRETURN result = SQL_SUCCESS;
    JP_CHAR state(10);
    JP_CHAR msgbuf(4096);
    SQLINTEGER cliErrCode;
    result = SQLGetDiagRec(conn.diagType
      , conn.diagHandle
      , recNumber
      , state.data
      , &cliErrCode
      , msgbuf.data
      , msgbuf.size
      , &msgLength);
    if (strlen(state)+1 < sizeof(conn.state) - strlen(conn.state))
    {
      strcat(conn.state, state);
      strcat(conn.state, ":");
    }
    if (conn.command != 0 && strlen(conn.command)+1 < sizeof(conn.msgbuf) - strlen(conn.msgbuf))
    {
      strcat(conn.msgbuf, conn.command);
      strcat(conn.msgbuf, "\n");
    }
    if (strlen(msgbuf)+1 < sizeof(conn.msgbuf) - strlen(conn.msgbuf))
    {
      strcat(conn.msgbuf, msgbuf);
      strcat(conn.msgbuf, "\n");
    }
  }
  else
    strcpy(conn.msgbuf, "No database error (RC==0)");
}

JP_INTERNAL(SQLRETURN) _Result(TJConnector &conn)
{
  _SqlError(conn);
  return conn.result;
}

JP_INTERNAL(SQLRETURN) _Result(TJQuery &qry)
{
  _SqlError(qry.conn);
  return qry.conn.result;
}

JP_INTERNAL(void) JP_SetSchema(TJConnector &conn)
{
  enum { NO_BINDS = 0
    , NO_DEFINES = 0
    , NO_ROWS = 0
    , ROW_SIZE = 0
  };
  TJQuery q(conn);
  q.command = TJAlloc::newChar(32+strlen(conn.schema));
  strcpy(q.command, "set schema ");
  strcat(q.command, conn.schema);
  q.Open(q.command, NO_BINDS, NO_DEFINES, NO_ROWS, ROW_SIZE);
  q.Exec();
}

JP_INTERNAL(SQLRETURN) JP_Logon(TJConnector &conn, const char* aUser, const char* aPassword, const char* aServer, const char* aSchema, const SQLHANDLE *aCliEnv)
{
  conn.user = TJAlloc::dupChar(aUser);
  conn.password = TJAlloc::dupChar(aPassword);
  conn.server = TJAlloc::dupChar(aServer);
  if (aSchema != 0)
    conn.schema = TJAlloc::dupChar(aSchema);
  JP_CHAR user(conn.user);
  JP_CHAR password(conn.password);
  JP_CHAR server(conn.server);
  conn.ForDiagnosis(SQL_HANDLE_ENV, SQL_NULL_HANDLE);
  if (aCliEnv != 0)
  {
    conn.cliEnv = *aCliEnv;
    conn.ownerOfEnv = false; 
    conn.result = 0;
  }
  else
  {
    conn.result = SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &conn.cliEnv);
    conn.ownerOfEnv = true; 
  }
  if (conn.Allow(conn.result) == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
    conn.result = SQLSetEnvAttr(conn.cliEnv,
      SQL_ATTR_ODBC_VERSION,
      (void *)SQL_OV_ODBC3,
      0);
  }
  if (conn.forkable == true)  
    JP_SetForkable(conn);
  if (conn.Allow(conn.result) == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
    conn.result = SQLAllocHandle(SQL_HANDLE_DBC, conn.cliEnv, &conn.cliDbc);
  }
  if (conn.coordinated == true && conn.ownerOfEnv == true)  
    JP_SetCoordinated(conn);
  if (conn.Allow(conn.result) == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLConnect(conn.cliDbc
      , server.data, SQL_NTS
      , user.data, SQL_NTS
      , password.data, SQL_NTS
      );
  }
  if (conn.Allow(conn.result) == true)
  { /* set AUTOCOMMIT OFF */
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLSetConnectAttr(conn.cliDbc
      , SQL_ATTR_AUTOCOMMIT
      , (SQLPOINTER)SQL_AUTOCOMMIT_OFF
      , SQL_NTS);
  }
  if (conn.schema != 0 && conn.Allow(conn.result) == true)
    JP_SetSchema(conn);
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_Sync(TJConnector &conn)
{
  JP_CHAR server(conn.server);
  JP_CHAR user(conn.user);
  JP_CHAR password(conn.password);
  SQLDisconnect(conn.cliDbc);
  SQLFreeHandle(SQL_HANDLE_DBC, conn.cliDbc);
  conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
  conn.result = SQLAllocHandle(SQL_HANDLE_DBC, conn.cliEnv, &conn.cliDbc);
  if (conn.Allow(conn.result) == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLConnect(conn.cliDbc
      , server.data, SQL_NTS
      , user.data, SQL_NTS
      , password.data, SQL_NTS
      );
  }
  if (conn.schema != 0 && conn.Allow(conn.result) == true)
    JP_SetSchema(conn);
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_SetCoordinated(TJConnector &conn)
{
  if (conn.Allow(conn.result) == true)
  { /* set ATTR_CONNECTTYPE */
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLSetConnectAttr(conn.cliDbc,
      SQL_ATTR_CONNECTTYPE,
      (SQLPOINTER)SQL_COORDINATED_TRANS,
      SQL_NTS);
  }
  if (conn.Allow(conn.result) == true)
  { /* set ATTR_SYNC_POINT */
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLSetConnectAttr(conn.cliDbc,
      SQL_ATTR_SYNC_POINT,
      (SQLPOINTER)SQL_TWOPHASE,
      SQL_NTS);
  }
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_SetForkable(TJConnector &conn)
{
  if (conn.Allow(conn.result) == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
    conn.result = SQLSetEnvAttr(conn.cliEnv,
      SQL_ATTR_PROCESSCTL,
      (void *)SQL_PROCESSCTL_NOFORK,
      0);
  }
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_Logoff(TJConnector &conn)
{
  conn.result = SQL_SUCCESS;
  TJAlloc::deleteChar(conn.user);
  TJAlloc::deleteChar(conn.password);
  TJAlloc::deleteChar(conn.server);
  TJAlloc::deleteChar(conn.schema);
  conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
  //conn.result =
  SQLDisconnect(conn.cliDbc);
  SQLFreeHandle(SQL_HANDLE_DBC, conn.cliDbc);
  conn.cliDbc = 0;
  conn.user = 0;
  conn.password = 0;
  conn.server = 0;
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_Commit(TJConnector &conn)
{
  if (conn.coordinated == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
    conn.result = SQLEndTran(SQL_HANDLE_ENV, conn.cliEnv, SQL_COMMIT);
  }
  else
  {
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLEndTran(SQL_HANDLE_DBC, conn.cliDbc, SQL_COMMIT);
  }
  return _Result(conn);
}

JP_INTERNAL(SQLRETURN) JP_Rollback(TJConnector &conn)
{
  if (conn.coordinated == true)
  {
    conn.ForDiagnosis(SQL_HANDLE_ENV, conn.cliEnv);
    conn.result = SQLEndTran(SQL_HANDLE_ENV, conn.cliEnv, SQL_ROLLBACK);
  }
  else
  {
    conn.ForDiagnosis(SQL_HANDLE_DBC, conn.cliDbc);
    conn.result = SQLEndTran(SQL_HANDLE_DBC, conn.cliDbc, SQL_ROLLBACK);
  }
  return _Result(conn);
}

JP_EXTERNAL(SQLRETURN) JP_Error(TJConnector &conn, char *Msg, int32 MsgLen)
{
  MsgLen--;
  memcpy(Msg, conn.msgbuf, MsgLen > sizeof(conn.msgbuf) ? sizeof(conn.msgbuf) : MsgLen);
  Msg[MsgLen] = 0;
  return conn.result;
}

JP_INTERNAL(SQLRETURN) JP_Open(TJQuery &qry, const char* Query)
{
  if (qry.isOpen == 0)
  {
    qry.conn.ForDiagnosis(SQL_HANDLE_DBC, qry.conn.cliDbc);
    qry.conn.result = SQLAllocHandle(SQL_HANDLE_STMT, qry.conn.cliDbc, &qry.cliStmt);
    if (qry.conn.Allow(qry.conn.result) == true)
    {
      qry.conn.ForDiagnosis(SQL_HANDLE_STMT, qry.cliStmt);
      qry.conn.result = SQLPrepare(qry.cliStmt
        , JP_CHAR(Query).data
        , SQL_NTS);
    }
    if (qry.conn.Allow(qry.conn.result) == true)
      qry.isOpen = 1;
  }
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_Exec(TJQuery &qry)
{
  qry.rowsDone = 0;
  qry.rowsRead = 0;
  qry.totalRowsRead = 0;
  qry.rowsIndex = 0;
  qry.error = 0;
  qry.conn.ForDiagnosis(SQL_HANDLE_STMT, qry.cliStmt);
  qry.conn.result = SQLExecute(qry.cliStmt);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_Fetch(TJQuery &qry)
{
  if (qry.rowsDone >= qry.totalRowsRead)
  {
    if (qry.error == SQL_NO_DATA_FOUND)
      return qry.error;
    if (qry.rowsDone > 0)
      qry.Clear();
    qry.conn.result = SQLFetch(qry.cliStmt);
    if (qry.conn.result != SQL_SUCCESS && qry.conn.result != SQL_SUCCESS_WITH_INFO)
    {
      qry.error = _Result(qry);
      if (qry.error != SQL_NO_DATA_FOUND)
        return qry.error;
      qry.conn.result = SQL_SUCCESS;
    }
    qry.totalRowsRead += qry.rowsRead;
    qry.rowsIndex = 0;
    if (qry.rowsRead == 0 || qry.totalRowsRead == qry.rowsDone)
      return qry.error;
  }
  else
    qry.rowsIndex++;
  qry.rowsDone++;
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_Close(TJQuery &qry)
{
  int32 result = 0;
  if (qry.isOpen == 1)
  {
    if (qry.command)
    {
      TJAlloc::deleteChar(qry.command);
      qry.conn.command = 0;
    }
    if (qry.data)
      TJAlloc::deleteChar(qry.data);
    if (qry.extra)
      TJAlloc::deleteChar(qry.extra);
    if (qry.offsets)
      TJAlloc::deleteInt(qry.offsets);
    if (qry.indicators)
      TJAlloc::deleteSQLINTEGER(qry.indicators);
    if (qry.cliStmt)
    {
      qry.conn.ForDiagnosis(SQL_HANDLE_DBC, qry.conn.cliDbc);
      result = SQLFreeHandle(SQL_HANDLE_STMT, qry.cliStmt);
      // kludge on Invalid Handle
      if (result == SQL_INVALID_HANDLE)
        result = SQL_SUCCESS;
    }
    qry.data = 0;
    qry.extra = 0;
    qry.offsets = 0;
    qry.indicators = 0;
    qry.cliStmt = SQL_NULL_HANDLE;
    qry.isOpen = 0;
    if (qry.conn.result == SQL_SUCCESS)
    {
      qry.conn.result = result;
      return _Result(qry);
    }
  }
  return result;
}

JP_INTERNAL(void) JP_GetString(TJQuery &qry, char *into, char *from, int32 dataLen)
{
  memcpy(into, from+(qry.rowsIndex*dataLen), dataLen);
}

JP_INTERNAL(void) JP_GetBLOB(TJQuery &qry, int32 &intolen, unsigned char *into, char *from, int32 dataSize)
{
  int maxSize = dataSize-8;
  intolen = *(int32 *)(from+(qry.rowsIndex*dataSize));
  SwapBytes(intolen);
  // -- stupid belts and bracers fix -- temporary - ha ha ha -- (just next two lines dope)
  if (intolen > maxSize)
    SwapBytes(intolen);
  if (intolen <= 0 || intolen > maxSize)
    intolen = maxSize;
  memcpy(into, from+(qry.rowsIndex*dataSize)+4, intolen);
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
  int32 date;
  _FromDate(*(DATE_STRUCT*)(from+qry.rowsIndex*sizeof(DATE_STRUCT)), date);
  sprintf(into, "%8.8d", date);
}

JP_INTERNAL(void) JP_GetTime(TJQuery &qry, char *into, char *from)
{
  int32 time;
  _FromTime(*(TIME_STRUCT*)(from+qry.rowsIndex*sizeof(TIME_STRUCT)), time);
  sprintf(into, "%6.6d", time);
}

JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry, char *into, char *from)
{
  int32 date;
  int32 time;
  _FromDateTime(*(TIMESTAMP_STRUCT*)(from+qry.rowsIndex*sizeof(TIMESTAMP_STRUCT)), date, time);
  sprintf(into, "%8.8d%6.6d", date, time);
}

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int16 &into, int32 defineNo)
{
  into = (int16)qry.indicators[qry.noBinds+defineNo*qry.noRows+qry.rowsIndex];

  if (into >= 0)
    into = 0;
  else
    into = -1;
}

JP_INTERNAL(SQLRETURN) JP_Setup(TJQuery &qry, int32 noBinds, int32 noDefines, int32 noRows, int32 sizeRow)
{
  qry.conn.result = SQL_SUCCESS;
  qry.noBinds   = noBinds;
  qry.noDefines = noDefines;
  qry.noRows    = noRows;
  qry.sizeRow   = sizeRow;
  int32 noIndicators = qry.noIndicators = noBinds + (noDefines * noRows);
  if (noRows && noDefines && sizeRow)
  {
    TJAlloc::deleteChar(qry.data);
    qry.data = TJAlloc::newChar(noRows * sizeRow);
    qry.conn.ForDiagnosis(SQL_HANDLE_STMT, qry.cliStmt);
    qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_ROW_ARRAY_SIZE, (SQLPOINTER)qry.noRows, 0);
    if (qry.conn.Allow(qry.conn.result) == true)
      qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_ROW_BIND_TYPE, SQL_BIND_BY_COLUMN, 0);
    if (qry.conn.Allow(qry.conn.result) == true)
      qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_ROWS_FETCHED_PTR, &qry.rowsRead, 0);
    if (qry.conn.Allow(qry.conn.result) == true)
      qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_CURSOR_TYPE, (SQLPOINTER)SQL_CURSOR_STATIC, 0);
    qry.conn.result = 0;
    if (qry.conn.Allow(qry.conn.result) == true)
      qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_USE_BOOKMARKS, (SQLPOINTER)SQL_UB_VARIABLE, 0);
    qry.conn.result = 0;
  }
  if (qry.conn.Allow(qry.conn.result) == true && noIndicators > 0)
  {
    TJAlloc::deleteSQLINTEGER(qry.indicators);
    qry.indicators = TJAlloc::newSQLINTEGER(noIndicators);
  }
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_ArraySetup(TJQuery &qry, int32 noBinds, int32 noNulls, int32 noRows, int32 sizeRow)
{
  qry.conn.result = SQL_SUCCESS;
  qry.noBinds   = noBinds;
  qry.noDefines = 0;
  qry.noRows    = noRows;
  qry.sizeRow   = sizeRow;
  int32 noIndicators = qry.noIndicators = noNulls * noRows;
  TJAlloc::deleteChar(qry.data);
  qry.data = TJAlloc::newChar(noRows * sizeRow);
  qry.conn.ForDiagnosis(SQL_HANDLE_STMT, qry.cliStmt);
  qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_PARAMSET_SIZE, (SQLPOINTER)qry.noRows, 0);
  if (qry.conn.Allow(qry.conn.result) == true)
    qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_ROW_BIND_TYPE, SQL_BIND_BY_COLUMN, 0);
  if (qry.conn.Allow(qry.conn.result) == true)
    qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_CURSOR_TYPE, (SQLPOINTER)SQL_CURSOR_STATIC, 0);
  qry.conn.result = 0;
  if (qry.conn.Allow(qry.conn.result) == true)
    qry.conn.result = SQLSetStmtAttr(qry.cliStmt, SQL_ATTR_USE_BOOKMARKS, (SQLPOINTER)SQL_UB_VARIABLE, 0);
  qry.conn.result = 0;
  if (qry.conn.Allow(qry.conn.result) == true)
  {
    TJAlloc::deleteSQLINTEGER(qry.indicators);
    if (noIndicators > 0)
      qry.indicators = TJAlloc::newSQLINTEGER(noIndicators);
  }
  return _Result(qry);
}

JP_INTERNAL(void) JP_CheckError(TJConnector &conn, char *file, int32 line)
{
  conn.ThrowOnError(file, line);
  //if (conn.result != 0)
  //  throw TCliApiException(conn.result, conn.state, conn.allow, conn.msgbuf, file, line);
}

JP_INTERNAL(void) _BindByPos(TJQuery &qry
                             , SQLUSMALLINT bindPos
                             , SQLSMALLINT inputOutput
                             , SQLSMALLINT cType
                             , SQLSMALLINT sqlType
                             , SQLULEN precision
                             , SQLSMALLINT scale
                             , void *data
                             , SQLULEN dataLen
                             , int16 isNull)
{
  if (cType == SQL_C_CHAR && isNull != JP_NULL)
    qry.indicators[bindPos] = SQL_NTS;
  else if (cType == SQL_C_BINARY)
    qry.indicators[bindPos] = dataLen;
  else
    qry.indicators[bindPos] = isNull == JP_NOT_NULL ? 0 : SQL_NULL_DATA;
  qry.conn.result = SQLBindParameter(qry.cliStmt
    , bindPos+1
    , inputOutput
    , cType
    , sqlType
    , precision
    , scale
    , data
    , dataLen
    , &qry.indicators[bindPos]);
}

JP_INTERNAL(SQLRETURN) JP_BindString(TJQuery &qry, int32 bindNo, char *data, int32 dataLen, int32 direction, int32 typeChar, int32 nullChar)
{
  int16 null = strlen(data) > 0 ? JP_NOT_NULL : JP_NULL;
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_CHAR
    , typeChar == 1 ? SQL_CHAR : SQL_VARCHAR
    , 0
    , 0
    , data
    , dataLen
    , nullChar == 1 ? null : JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindBLOB(TJQuery &qry, int32 bindNo, char *data, int32 dataSize, int32 direction)
{
  int32 dataLen = *(int32*)data + 4;
  SwapBytes(*(int32*)data);
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_BINARY
    , SQL_BINARY
    , dataSize
    , 0
    , data
    , dataLen
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindLong(TJQuery &qry, int32 bindNo, int64 &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_SBIGINT
    , SQL_BIGINT
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindLongNull(TJQuery &qry, int32 bindNo, int64 &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_SBIGINT
    , SQL_BIGINT
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindInt(TJQuery &qry, int32 bindNo, int32 &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_LONG
    , SQL_INTEGER
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindIntNull(TJQuery &qry, int32 bindNo, int32 &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_LONG
    , SQL_INTEGER
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindShort(TJQuery &qry, int32 bindNo, int16 &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_SHORT
    , SQL_SMALLINT
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindShortNull(TJQuery &qry, int32 bindNo, int16 &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_SHORT
    , SQL_SMALLINT
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDouble(TJQuery &qry, int32 bindNo, double &data, int32 precision, int32 scale, int32 direction)
{
  SQLSMALLINT sqlType = SQL_DOUBLE;
  if (precision != 0 || scale != 0)
    sqlType = SQL_DECIMAL;
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_DOUBLE
    , sqlType
    , precision
    , scale
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDecimal(TJQuery &qry, int32 bindNo, char *data, int32 precision, int32 scale, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_CHAR
    , SQL_DECIMAL
    , precision
    , scale
    , &data
    , precision+3
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDoubleNull(TJQuery &qry, int32 bindNo, double &data, int32 precision, int32 scale, int32 direction, int16 &null)
{
  SQLSMALLINT sqlType = SQL_DOUBLE;
  if (precision != 0 || scale != 0)
    sqlType = SQL_DECIMAL;
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_DOUBLE
    , sqlType
    , precision
    , scale
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDecimalNull(TJQuery &qry, int32 bindNo, char *data, int32 precision, int32 scale, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_CHAR
    , SQL_DECIMAL
    , precision
    , scale
    , &data
    , precision+3
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDate(TJQuery &qry, int32 bindNo, DATE_STRUCT &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_DATE
    , SQL_TYPE_DATE
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDateNull(TJQuery &qry, int32 bindNo, DATE_STRUCT &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_DATE
    , SQL_TYPE_DATE
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLITime(TJQuery &qry, int32 bindNo, TIME_STRUCT &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_TIME
    , SQL_TYPE_TIME
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLITimeNull(TJQuery &qry, int32 bindNo, TIME_STRUCT &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_TIME
    , SQL_TYPE_TIME
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDateTime(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT &data, int32 direction)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_TIMESTAMP
    , SQL_TYPE_TIMESTAMP
    , 0
    , 0
    , &data
    , sizeof(data)
    , JP_NOT_NULL);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDateTimeNull(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT &data, int32 direction, int16 &null)
{
  _BindByPos(qry
    , bindNo
    , direction
    , SQL_C_TYPE_TIMESTAMP
    , SQL_TYPE_TIMESTAMP
    , 0
    , 0
    , &data
    , sizeof(data)
    , null);
  return _Result(qry);
}

JP_INTERNAL(void) _BindByPosArray(TJQuery &qry
                                  , SQLUSMALLINT bindPos
                                  , SQLSMALLINT cType
                                  , SQLSMALLINT sqlType
                                  , SQLULEN precision
                                  , SQLSMALLINT scale
                                  , void *data
                                  , SQLULEN dataLen
                                  , SQLINTEGER* indicators)
{
  qry.conn.result = SQLBindParameter(qry.cliStmt
    , bindPos+1
    , SQL_PARAM_INPUT
    , cType
    , sqlType
    , precision
    , scale
    , data
    , dataLen
    , indicators);
}

JP_INTERNAL(SQLRETURN) JP_BindCharArray(TJQuery &qry, int32 bindNo, char *data, int32 dataLen, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_CHAR
    , SQL_VARCHAR
    , 0
    , 0
    , data
    , dataLen
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindAnsiCharArray(TJQuery &qry, int32 bindNo, char *data, int32 dataLen, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_CHAR
    , SQL_CHAR
    , 0
    , 0
    , data
    , dataLen
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindInt64Array(TJQuery &qry,  int32 bindNo, int64 *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_SBIGINT
    , SQL_BIGINT
    , 0
    , 0
    , data
    , sizeof(int64)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindInt32Array(TJQuery &qry, int32 bindNo, int32 *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_LONG
    , SQL_INTEGER
    , 0
    , 0
    , data
    , sizeof(int32)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindInt16Array(TJQuery &qry, int32 bindNo, int16 *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_SHORT
    , SQL_SMALLINT
    , 0
    , 0
    , data
    , sizeof(int16)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDoubleArray(TJQuery &qry, int32 bindNo, double *data, int32 precision, int32 scale, SQLINTEGER* indicators)
{
  SQLSMALLINT sqlType = SQL_DOUBLE;
  if (precision != 0 || scale != 0)
    sqlType = SQL_DECIMAL;
  _BindByPosArray(qry
    , bindNo
    , SQL_C_DOUBLE
    , sqlType
    , precision
    , scale
    , data
    , sizeof(double)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindDecimalArray(TJQuery &qry, int32 bindNo, char *data, int32 precision, int32 scale, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_CHAR
    , SQL_DECIMAL
    , precision
    , scale
    , data
    , precision+3
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDateArray(TJQuery &qry, int32 bindNo, DATE_STRUCT *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_TYPE_DATE
    , SQL_TYPE_DATE
    , 0
    , 0
    , data
    , sizeof(DATE_STRUCT)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLITimeArray(TJQuery &qry, int32 bindNo, TIME_STRUCT *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_TYPE_TIME
    , SQL_TYPE_TIME
    , 0
    , 0
    , data
    , sizeof(TIME_STRUCT)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_BindCLIDateTimeArray(TJQuery &qry, int32 bindNo, TIMESTAMP_STRUCT *data, SQLINTEGER* indicators)
{
  _BindByPosArray(qry
    , bindNo
    , SQL_C_TYPE_TIMESTAMP
    , SQL_TYPE_TIMESTAMP
    , 0
    , 0
    , data
    , sizeof(TIME_STRUCT)
    , indicators);
  return _Result(qry);
}

JP_INTERNAL(void) _DefineByPos(TJQuery &qry, int32 defineNo, void *data, int32 dataLen, SQLSMALLINT dataType)
{
  qry.conn.result = SQLBindCol( qry.cliStmt
    , defineNo+1
    , dataType
    , data
    , dataLen
    , &qry.indicators[qry.noBinds+defineNo*qry.noRows]
  );
}

JP_INTERNAL(SQLRETURN) JP_DefineString(TJQuery &qry, int32 defineNo, char *data, int32 dataLen, int32 ansichar)
{
  _DefineByPos(qry, defineNo, data, dataLen, SQL_C_CHAR);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineBLOB(TJQuery &qry, int32 defineNo, char *data, int32 dataSize)
{
  _DefineByPos(qry, defineNo, data, dataSize, SQL_C_BINARY);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineLong(TJQuery &qry, int32 defineNo, int64 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQL_C_SBIGINT);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineInt(TJQuery &qry, int32 defineNo, int32 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQL_C_LONG);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineShort(TJQuery &qry, int32 defineNo, int16 *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQL_C_SHORT);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineDouble(TJQuery &qry, int32 defineNo, double *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(*data), SQL_C_DOUBLE);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineDecimal(TJQuery &qry, int32 defineNo, char *data, int32 dataSize)
{
  _DefineByPos(qry, defineNo, data, dataSize, SQL_C_CHAR);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineDate(TJQuery &qry, int32 defineNo, DATE_STRUCT *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(DATE_STRUCT), SQL_C_TYPE_DATE);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineTime(TJQuery &qry, int32 defineNo, TIME_STRUCT *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(TIME_STRUCT), SQL_C_TYPE_TIME);
  return _Result(qry);
}

JP_INTERNAL(SQLRETURN) JP_DefineDateTime(TJQuery &qry, int32 defineNo, TIMESTAMP_STRUCT *data)
{
  _DefineByPos(qry, defineNo, data, sizeof(TIMESTAMP_STRUCT), SQL_C_TYPE_TIMESTAMP);
  return _Result(qry);
}

JP_INTERNAL(void) JP_Sequence(TJQuery& qry, int32 &sequence, char *sequencer)
{
  enum { NO_BINDS = 0
    , NO_DEFINES = 1
    , NO_ROWS = 1
    , ROW_SIZE = sizeof(sequence)
  };
  TJQuery q(qry.conn);
  q.command = TJAlloc::newChar(128);
  strcpy(q.command, "SELECT NEXTVAL FOR ");
  strcat(q.command, sequencer);
  strcat(q.command, " from SYSIBM.SYSDUMMY1 WITH UR" );
  q.Open(q.command, NO_BINDS, NO_DEFINES, NO_ROWS, ROW_SIZE);
  q.Define(0, (int32*) (q.data));
  q.Exec();
  if (q.Fetch())
    q.Get(sequence, q.data);
  else
    sequence = 0;
}

JP_INTERNAL(void) JP_BigSequence(TJQuery& qry, int64 &sequence, char *sequencer)
{
  enum { NO_BINDS = 0
    , NO_DEFINES = 1
    , NO_ROWS = 1
    , ROW_SIZE = sizeof(sequence)
  };
  TJQuery q(qry.conn);
  q.command = TJAlloc::newChar(128);
  strcpy(q.command, "SELECT NEXTVAL FOR ");
  strcat(q.command, sequencer);
  strcat(q.command, " from SYSIBM.SYSDUMMY1 WITH UR" );
  q.Open(q.command, NO_BINDS, NO_DEFINES, NO_ROWS, ROW_SIZE);
  q.Define(0, (int64*) (q.data));
  q.Exec();
  if (q.Fetch())
    q.Get(sequence, q.data);
  else
    sequence = 0;
}

JP_INTERNAL(void) JP_UserStamp(TJQuery& qry, char* userStamp)
{
  strcpy(userStamp, (char*)qry.conn.user);
}

JP_INTERNAL(void) JP_Date(TJQuery& qry, DATE_STRUCT &clidate, char *aDate)
{
  char date[9];
  strncpy(date, aDate, 8); date[8]=0;
  _ToDate(clidate, atol(date));
}

JP_INTERNAL(void) JP_Time(TJQuery& qry, TIME_STRUCT &clitime, char *aTime)
{
  char time[7];
  strncpy(time, aTime, 6); time[6]=0;
  _ToTime(clitime, atol(time));
}

JP_INTERNAL(void) JP_DateTime(TJQuery& qry, TIMESTAMP_STRUCT &clidatetime, char *dateTime)
{
  char date[9];
  char time[7];
  strncpy(date, dateTime, 8);   date[8]=0;
  strncpy(time, dateTime+8, 6); time[6]=0;
  _ToDateTime(clidatetime, atol(date), atol(time));
}

JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry, TIMESTAMP_STRUCT &clitimestamp, char *timeStamp)
{
  int fraction;
  qry.conn.TimeStamp(timeStamp, fraction);
  JP_DateTime(qry, clitimestamp, timeStamp);
  clitimestamp.fraction = fraction;
}


#include "machine.h"
#include "pgapi.h"
#include "swapbytes.h"

JP_INTERNAL(int) JP_Logon(TJConnector &conn
                        , const char* Host
                        , const char* DBName
                        , const char* User
                        , const char* Password
                        , int Port=5432);
JP_INTERNAL(int) JP_Logoff(TJConnector &conn);
JP_INTERNAL(int) JP_Commit(TJConnector &conn);
JP_INTERNAL(int) JP_Rollback(TJConnector &conn);

JP_INTERNAL(void) JP_Open(TJQuery &qry, int noBinds, int noColumns);
JP_INTERNAL(int) JP_Exec(TJQuery &qry);
JP_INTERNAL(int) JP_Fetch(TJQuery &qry);
JP_INTERNAL(void) JP_Close(TJQuery &qry);

JP_INTERNAL(void) JP_BindString(TJQuery &qry, int bindNo, char  *Data,  int Datalen, bool ansichar);
//JP_INTERNAL(void) JP_BindBLOB(TJQuery &qry, int bindNo, char  *Data,  int Datasize);

JP_INTERNAL(void) JP_BindLong(TJQuery &qry, int bindNo, int64 &Data);
JP_INTERNAL(void) JP_BindInt(TJQuery &qry, int bindNo, int32 &Data);
JP_INTERNAL(void) JP_BindShort(TJQuery &qry, int bindNo, int16 &Data);
JP_INTERNAL(void) JP_BindDouble(TJQuery &qry, int bindNo, double &Data, int precision, int scale);
JP_INTERNAL(void) JP_BindDecimal(TJQuery &qry, int bindNo, char *Data, int precision, int scale);
JP_INTERNAL(void) JP_BindPGDate(TJQuery &qry, int bindNo, TPGDate &Data);
JP_INTERNAL(void) JP_BindPGTime(TJQuery &qry, int bindNo, TPGTime &Data);
JP_INTERNAL(void) JP_BindPGDateTime(TJQuery &qry, int bindNo, TPGDateTime &Data);

JP_INTERNAL(void) JP_BindLongNull(TJQuery &qry, int bindNo, int64 &Data, int16 &Null);
JP_INTERNAL(void) JP_BindIntNull(TJQuery &qry, int bindNo, int32 &Data, int16 &Null);
JP_INTERNAL(void) JP_BindShortNull(TJQuery &qry, int bindNo, int16 &Data, int16 &Null);
JP_INTERNAL(void) JP_BindDoubleNull(TJQuery &qry, int bindNo, double  &Data, int precision, int scale, int16 &Null);
JP_INTERNAL(void) JP_BindDecimalNull(TJQuery &qry, int bindNo, char *Data, int precision, int scale, int16 &Null);
JP_INTERNAL(void) JP_BindPGDateNull(TJQuery &qry, int bindNo, TPGDate &Data, int16 &Null);
JP_INTERNAL(void) JP_BindPGTimeNull(TJQuery &qry, int bindNo, TPGTime &Data, int16 &Null);
JP_INTERNAL(void) JP_BindPGDateTimeNull(TJQuery &qry, int bindNo, TPGDateTime &Data, int16 &Null);

JP_INTERNAL(void) JP_GetString(TJQuery &qry, int colNo, char *into, int dataLen);
//JP_INTERNAL(void) JP_GetBLOB(TJQuery &qry, int colNo, int32 &intolen, unsigned char *into, int dataSize);
JP_INTERNAL(void) JP_GetLong(TJQuery &qry, int colNo, int64 &into);
JP_INTERNAL(void) JP_GetInt(TJQuery &qry, int colNo, int32 &into);
JP_INTERNAL(void) JP_GetShort(TJQuery &qry, int colNo, int16 &into);
JP_INTERNAL(void) JP_GetDouble(TJQuery &qry, int colNo, double &into);
JP_INTERNAL(void) JP_GetDecimal(TJQuery &qry, int colNo, char *into, int dataLen);
JP_INTERNAL(void) JP_GetDate(TJQuery &qry, int colNo, char *into);
JP_INTERNAL(void) JP_GetTime(TJQuery &qry, int colNo, char *into);
JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry, int colNo, char *into);

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int colNo, int16 &into);

JP_INTERNAL(void) JP_Sequence(TJQuery& qry,      int32 &sequence, const char *sequencer);
JP_INTERNAL(void) JP_UserStamp(TJQuery& qry,     char* userStamp, int len);
JP_INTERNAL(void) JP_Date(TJQuery& qry,          TPGDate &pgdate, char *date);
JP_INTERNAL(void) JP_Time(TJQuery& qry,          TPGTime &pgtime, char *time);
JP_INTERNAL(void) JP_DateTime(TJQuery& qry,      TPGDateTime &pgDateTime, char *dateTime);
JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry,     TPGDateTime &pgTimeStamp, char *timeStamp);

JP_INTERNAL(int)  JP_JulianDate(int year, int month, int day);
JP_INTERNAL(int)  JP_JulianDate(const char* YYYYMMDD);
JP_INTERNAL(int)  JP_Seconds(char* HHMISS);
JP_INTERNAL(void) JP_CalendarDate(int juldate, int& year, int& month, int& day);
JP_INTERNAL(void) JP_CalendarDate(int jul, char* YYYYMMDD);
JP_INTERNAL(void) _FromDate(TPGDate& pgDate, char*& yyyymmdd);
JP_INTERNAL(void) _ToDate(TPGDate& pgDate, char* yyyymmdd);
JP_INTERNAL(void) _FromTime(TPGTime& pgTime, char*& hhmmss);
JP_INTERNAL(void) _ToTime(TPGTime& pgTime, char* hhmmss);
JP_INTERNAL(void) _FromDateTime(TPGDateTime& pgDateTime, char*& yyyymmddhhmmss);
JP_INTERNAL(void) _ToDateTime(TPGDateTime& pgDateTime, char* yyyymmddhhmmss);
JP_INTERNAL(void) _SqlError(TJConnector &conn);
JP_INTERNAL(int)  _Result(TJConnector &conn);
JP_INTERNAL(int)  _Result(TJQuery &qry);
JP_INTERNAL(int64) _atoint64(const char *s);

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

JP_INTERNAL(int) JP_JulianDate(const char* YYYYMMDD)
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
  sprintf(work, "%04d%02d%02d", year, month, day);
  strncpy(YYYYMMDD, work, 8);
}

JP_INTERNAL(void) _FromDate(TPGDate& pgDate, char*& yyyymmdd)
{
  // 0123456789
  // YYYY-MM-DD to YYYYMMDD
  sprintf(yyyymmdd, "%4.4s%2.2s%2.2s", pgDate.date, pgDate.date+5, pgDate.date+8);
}

JP_INTERNAL(void) _ToDate(TPGDate& pgDate, char* yyyymmdd)
{
  // 01234567
  // YYYYMMDD to YYYY-MM-DD
  sprintf(pgDate.date, "%4.4s-%2.2s-%2.2s", yyyymmdd, yyyymmdd+4, yyyymmdd+6);
}

JP_INTERNAL(void) _FromTime(TPGTime& pgTime, char*& hhmmss)
{
  // 01234567
  // HH:MI:SS to HHMISS
  sprintf(hhmmss, "%2.2s%2.2s%2.2s", pgTime.time, pgTime.time+3, pgTime.time+6);
}

JP_INTERNAL(void) _ToTime(TPGTime& pgTime, char* hhmmss)
{
  // 012345
  // HHMISS to HH:MI:SS
  sprintf(pgTime.time, "%2.2s:%2.2s:%2.2s", hhmmss, hhmmss+2, hhmmss+4);
}

JP_INTERNAL(void) _FromDateTime(TPGDateTime& pgDateTime, char*& yyyymmddhhmmss)
{
  // 0123456789012345678
  // YYYY-MM-DD HH:MM:SS to YYYYMMDDHHMMSS
  sprintf(yyyymmddhhmmss, "%4.4s%2.2s%2.2s%2.2s%2.2s%2.2s"
                        , pgDateTime.datetime, pgDateTime.datetime+5, pgDateTime.datetime+8
                        , pgDateTime.datetime+11, pgDateTime.datetime+14, pgDateTime.datetime+17);
}

JP_INTERNAL(void) _ToDateTime(TPGDateTime& pgDateTime, char* yyyymmddhhmmss)
{
  // 01234567890123
  // YYYYMMDDHHMMSS to YYYY-MM-DD HH:MM:SS
  sprintf(pgDateTime.datetime, "%4.4s-%2.2s-%2.2s %2.2s:%2.2s:%2.2s"
                        , yyyymmddhhmmss, yyyymmddhhmmss+4, yyyymmddhhmmss+6
                        , yyyymmddhhmmss+8, yyyymmddhhmmss+10, yyyymmddhhmmss+12);
}

TJConnector::ConnectHandle TJConnector::connectHandle;

void TJConnector::Logon(const char* host, const char* dbName, const char* user, const char* password, int port)
{
  ThrowOnError(JP_Logon(*this, host, dbName, user, password, port), JP_MARK);
  loggedOn = true;
}

void TJConnector::Logoff()
{
  if (loggedOn == true)
    ThrowOnError(JP_Logoff(*this), JP_MARK);
  loggedOn = false;
}

void TJConnector::Commit()
{
  if (loggedOn == true)
    ThrowOnError(JP_Commit(*this), JP_MARK);
}

void TJConnector::Rollback()
{
  if (loggedOn == true)
    ThrowOnError(JP_Rollback(*this), JP_MARK);
}

void TJConnector::Error(char *Msg, int MsgLen)
{
  int rc;
  if (rc = JP_Error(*this, Msg, MsgLen))
    ThrowOnError(rc, JP_MARK);
  loggedOn = false;
}

void TJConnector::ThrowOnError(int error, const char *file, int line)
{
  if (result != 0)
    throw TPGApiException(result, msgbuf, file, line);
}

TJQuery::~TJQuery()
{
  Close();
}

void TJConnector::TimeStamp(char *timeStamp)
{
  const char command[] = "SELECT CURRENT_TIMESTAMP";
  if (serverTime == 0)
  {
    time(&systemTime);
    TJQuery q(*this);
    q.Open(command, 0, 1);
    q.Exec();
    serverTime = systemTime;
    if (q.Fetch())
    {
      q.Get(0, TJDateTime(timeStamp));
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
  sprintf(timeStamp, "%08d%06d", dateNow, timeNow);
}

void TJQuery::Open(const char* command, int noBinds, int noColumns)
{
  this->command = (char*)command;
  JP_Open(*this, noBinds, noColumns);
}

void TJQuery::Bind(int bindNo, char *data, int dataLen, bool ansichar)
{
  JP_BindString(*this, bindNo, data, dataLen, ansichar);
}

//void TJQuery::BindBlob(int bindNo, char *data, int dataSize)
//{
//  JP_BindBLOB(*this, bindNo, data, dataSize);
//}

void TJQuery::Bind(int bindNo, int64 &data, int64 &work, int16 *null)
{
  work = data;SwapBytes(work);
  if (null)
    JP_BindLongNull(*this, bindNo, work, *null);
  else
    JP_BindLong(*this, bindNo, work);
}

void TJQuery::Bind(int bindNo, int32 &data, int32 &work, int16 *null)
{
  work = data;SwapBytes(work);
  if (null)
    JP_BindIntNull(*this, bindNo, work, *null);
  else
    JP_BindInt(*this, bindNo, work);
}

void TJQuery::Bind(int bindNo, int16 &data, int16 &work, int16 *null)
{
  work = data;SwapBytes(work);
  if (null)
    JP_BindShortNull(*this, bindNo, work, *null);
  else
    JP_BindShort(*this, bindNo, work);
}

void TJQuery::Bind(int bindNo, double &data, double &work, int precision, int scale, int16 *null)
{
  work = data;SwapBytes(work);
  if (null)
    JP_BindDoubleNull(*this, bindNo, work, precision, scale, *null);
  else
    JP_BindDouble(*this, bindNo, work, precision, scale);
}

void TJQuery::Bind(int bindNo, char *data, int precision, int scale, int16 *null)
{
  if (null)
    JP_BindDecimalNull(*this, bindNo, data, precision, scale, *null);
  else
    JP_BindDecimal(*this, bindNo, data, precision, scale);
}

void TJQuery::Bind(int bindNo, TPGDate &data, int16 *null)
{
  if (null)
    JP_BindPGDateNull(*this, bindNo, data, *null);
  else
    JP_BindPGDate(*this, bindNo, data);
}

void TJQuery::Bind(int bindNo, TPGTime &data, int16 *null)
{
  if (null)
    JP_BindPGTimeNull(*this, bindNo, data, *null);
  else
    JP_BindPGTime(*this, bindNo, data);
}

void TJQuery::Bind(int bindNo, TPGDateTime &data, int16 *null)
{
  if (null)
    JP_BindPGDateTimeNull(*this, bindNo, data, *null);
  else
    JP_BindPGDateTime(*this, bindNo, data);
}

void TJQuery::Exec()
{
  conn.ThrowOnError(JP_Exec(*this), file, line);
}

bool TJQuery::Fetch()
{
  int result = JP_Fetch(*this);
  if (result == 0)
    return false;
  conn.ThrowOnError(result, file, line);
  return true;
}

void TJQuery::Get(int colNo, char *into, int dataLen)
{
  JP_GetString(*this, colNo, into, dataLen);
}

//void TJQuery::Get(int colNo, int32 &intolen, unsigned char *into, int dataSize)
//{
//  intolen = dataSize-4;
//  JP_GetBLOB(*this, colNo, intolen, into, dataSize);
//}

void TJQuery::Get(int colNo, int64 &into)
{
  JP_GetLong(*this, colNo, into);
}

void TJQuery::Get(int colNo, int32 &into)
{
  JP_GetInt(*this, colNo, into);
}

void TJQuery::Get(int colNo, int16 &into)
{
  JP_GetShort(*this, colNo, into);
}

void TJQuery::Get(int colNo, double &into)
{
  JP_GetDouble(*this, colNo, into);
}

void TJQuery::Get(int colNo, TJDate into)
{
  JP_GetDate(*this, colNo, into.date);
}

void TJQuery::Get(int colNo, TJTime into)
{
  JP_GetTime(*this, colNo, into.time);
}

void TJQuery::Get(int colNo, TJDateTime into)
{
  JP_GetDateTime(*this, colNo, into.dateTime);
}

void TJQuery::GetNull(int colNo, int16 &into)
{
  JP_GetNull(*this, colNo, into);
}

void TJQuery::Close()
{
  JP_Close(*this);
}

int32& TJQuery::Sequence(int32 &sequence, const char *sequencer)
{
  JP_Sequence(*this, sequence, sequencer);
  return sequence;
}

char* TJQuery::UserStamp(char* userStamp, int len)
{
  JP_UserStamp(*this, userStamp, len);
  return userStamp;
}

TPGDate& TJQuery::Date(TPGDate &pgdate, char *date)
{
  JP_Date(*this, pgdate, date);
  return pgdate;
}

TPGTime& TJQuery::Time(TPGTime &pgtime, char *time)
{
  JP_Time(*this, pgtime, time);
  return pgtime;
}

TPGDateTime& TJQuery::DateTime(TPGDateTime &pgdatetime, char *dateTime)
{
  JP_DateTime(*this, pgdatetime, dateTime);
  return pgdatetime;
}

TPGDateTime& TJQuery::TimeStamp(TPGDateTime &pgtimestamp, char *timeStamp)
{
  JP_TimeStamp(*this, pgtimestamp, timeStamp);
  return pgtimestamp;
}

JP_INTERNAL(void) _SqlError(TJConnector &conn)
{
  conn.result = 0;
  if (conn.connStatus != CONNECTION_OK)
  {
    conn.result = conn.connStatus;
    switch(conn.connStatus)
    {
    #define CASE(x) case x: snprintf(conn.msgbuf, sizeof(conn.msgbuf), #x ": %s", PQerrorMessage(conn.pgConn)); break
    CASE(CONNECTION_BAD); 
    CASE(CONNECTION_STARTED);
    CASE(CONNECTION_MADE);
    CASE(CONNECTION_AWAITING_RESPONSE);
    CASE(CONNECTION_AUTH_OK);
    CASE(CONNECTION_SETENV);
    CASE(CONNECTION_SSL_STARTUP);
    CASE(CONNECTION_NEEDED);
    #undef CASE
    }
    return;
  }
  if (conn.execStatus != PGRES_COMMAND_OK && conn.execStatus != PGRES_TUPLES_OK)
  {
    conn.result = conn.execStatus;
    switch(conn.execStatus)
    {
    #define CASE(x, y) case x: snprintf(conn.msgbuf, sizeof(conn.msgbuf), #x ": %s", PQerrorMessage(conn.pgConn)); if (y) conn.result = 1000+y; break
 	  CASE(PGRES_EMPTY_QUERY, 0);
	  CASE(PGRES_COMMAND_OK, 0);
	  CASE(PGRES_TUPLES_OK, 0);
	  CASE(PGRES_COPY_OUT, 0);
	  CASE(PGRES_COPY_IN, 0);
	  CASE(PGRES_BAD_RESPONSE, conn.execStatus);
	  CASE(PGRES_NONFATAL_ERROR, 0);
	  CASE(PGRES_FATAL_ERROR, conn.execStatus);
    #undef CASE
    }
    return;
  }
}

JP_INTERNAL(int) _Result(TJConnector &conn)
{
  _SqlError(conn);
  return conn.result;
}

JP_INTERNAL(int) _Result(TJQuery &qry)
{
  _SqlError(qry.conn);
  return qry.conn.result;
}

JP_INTERNAL(int) JP_Logon(TJConnector &conn
                        , const char* host
                        , const char* dbName
                        , const char* user
                        , const char* password
                        , int port
                        )
{
  char conninfo[1024];
  conn.host = strdup(host);
  conn.dbName = strdup(dbName);
  conn.user = strdup(user);
  snprintf(conninfo,sizeof(conninfo),"host=%s port=%d dbname=%s user=%s password=%s"
    , host, port, dbName, user, password);
  conn.pgConn = PQconnectdb(conninfo);
  conn.connStatus = PQstatus(conn.pgConn);
  if (_Result(conn) == 0)
  {
    PGresult *pgResult = PQexec(conn.pgConn, "BEGIN");
    conn.execStatus = PQresultStatus(pgResult);
    PQclear(pgResult);
    _Result(conn);
  }
  return conn.result;
}

JP_INTERNAL(int) JP_Logoff(TJConnector &conn)
{
  conn.result = 0;
  if (conn.host) delete conn.host;
  if (conn.user) delete conn.user;
  if (conn.dbName) delete conn.dbName;
  conn.user = 0;
  conn.dbName = 0;
  conn.host = 0;
  PQfinish(conn.pgConn);
  return 0;
}

JP_INTERNAL(int) JP_Commit(TJConnector &conn)
{
  PGresult *pgResult = PQexec(conn.pgConn, "COMMIT;BEGIN");
  conn.execStatus = PQresultStatus(pgResult);
  PQclear(pgResult);
  return _Result(conn);
}

JP_INTERNAL(int) JP_Rollback(TJConnector &conn)
{
  PGresult *pgResult = PQexec(conn.pgConn, "ROLLBACK;BEGIN");
  conn.execStatus = PQresultStatus(pgResult);
  PQclear(pgResult);
  return _Result(conn);
}

JP_EXTERNAL(int) JP_Error(TJConnector &conn, char *Msg, int MsgLen)
{
  MsgLen--;
  memcpy(Msg, conn.msgbuf, MsgLen > sizeof(conn.msgbuf) ? sizeof(conn.msgbuf) : MsgLen);
  Msg[MsgLen] = 0;
  return conn.result;
}

JP_INTERNAL(void) JP_Open(TJQuery &qry, int noBinds, int noColumns)
{
  qry.noRows = 0;
  qry.rowIndex = 0;
  qry.noBinds   = noBinds;
  qry.noColumns = noColumns;
  if (noBinds > 0)
  {
    qry.paramTypes = new Oid[noBinds];
    qry.paramLengths = new int[noBinds];
    qry.paramFormats = new int[noBinds];
    qry.paramValues  = new const char*[noBinds];
  }
  qry.isOpen = 1;
}

JP_INTERNAL(int) JP_Exec(TJQuery &qry)
{
  if (qry.noBinds == 0)
    qry.pgResult = PQexec(qry.conn.pgConn, qry.command);
  else
    qry.pgResult = PQexecParams(qry.conn.pgConn
                              , qry.command
                              , qry.noBinds
                              , qry.paramTypes
                              , qry.paramValues
                              , qry.paramLengths
                              , qry.paramFormats
                              , 0 // char based result set.
                              );
  if (qry.pgResult == 0)
    throw TPGApiException(99990, PQerrorMessage(qry.conn.pgConn), JP_MARK);
  qry.conn.execStatus = PQresultStatus(qry.pgResult);
  if (qry.noColumns > 0)
  {
    if (qry.conn.execStatus == PGRES_TUPLES_OK)
    { 
      int noColumns = PQnfields(qry.pgResult);
      if (noColumns != qry.noColumns)
      {
        char work[1024];
        sprintf(work, "No of columns (%d) returned does not match no of outputs (%d) required", noColumns, qry.noColumns);
        throw TPGApiException(99991, work, JP_MARK);
      }
      qry.noRows = PQntuples(qry.pgResult);
    }
  }
  else
  {
    PQclear(qry.pgResult);
    qry.pgResult = 0;
  }
  return _Result(qry);
}

JP_INTERNAL(int) JP_Fetch(TJQuery &qry)
{
  if (qry.pgResult == 0)
    throw TPGApiException(99992, "There is no active Result Set for Fetch", JP_MARK);
  if (qry.rowIndex >= qry.noRows)
    return 0;
  qry.rowIndex++;
  return 1;
}

JP_INTERNAL(void) JP_Close(TJQuery &qry)
{
  if (qry.isOpen == 1)
  {
    if (qry.paramTypes) 
      delete [] qry.paramTypes;
    if (qry.paramLengths) 
      delete [] qry.paramLengths;
    if (qry.paramFormats) 
      delete [] qry.paramFormats;
    if (qry.paramValues) 
      delete [] qry.paramValues;
    qry.isOpen = 0;
    qry.paramTypes = 0;
    qry.paramLengths = 0;
    qry.paramFormats = 0;
    qry.paramValues  = 0;
  }
  if (qry.pgResult != 0)
  {
    PQclear(qry.pgResult);
    qry.pgResult = 0;
  }
}

JP_INTERNAL(void) JP_GetString(TJQuery &qry, int colNo, char *into, int dataLen)
{
  memset(into, 0, dataLen);
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
    strncpy(into, value, dataLen);
}

//JP_INTERNAL(void) JP_GetBLOB(TJQuery &qry, int32 &intolen, unsigned char *into, char *from, int dataSize)
//{
//  int maxSize = dataSize-8;
//  intolen = *(int32 *)(from+(qry.rowsIndex*dataSize));
//  SwapBytes(intolen);
//  if (intolen > maxSize)
//    SwapBytes(intolen);
//  if (intolen <= 0 || intolen > maxSize)
//      intolen = maxSize;
//  memcpy(into, from+(qry.rowsIndex*dataSize)+4, intolen);
//}

// I really hate int64 and c++ 
// This function assumes the number come in with a starting optional sign
// and just digits thereafter. It terminates when it does not see a digit
// running from the back. it is not exactly un like the atoi and atol functions
// in this respect.

JP_INTERNAL(int64) _atoint64(const char *s)
{
  int64 result = 0;
  int64 mp = 1;
  for (int n = strlen(s)-1; n >= 0; n--)  
  {
    char ch = s[n];
    if (ch == '-')
      mp *= -1;
    if (ch < '0' || ch > '9') 
      break;
    int64 d = ch - '0';
    result += d * mp;
    mp *= 10;
  }
  return result;
}

JP_INTERNAL(void) JP_GetLong(TJQuery &qry, int colNo, int64 &into)
{
  into = 0;
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
    into = _atoint64(value);
}

JP_INTERNAL(void) JP_GetInt(TJQuery &qry, int colNo, int32 &into)
{
  into = 0;
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
    into = (int32) atol(value);
}

JP_INTERNAL(void) JP_GetShort(TJQuery &qry, int colNo, int16 &into)
{
  into = 0;
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
    into = (int16) atoi(value);
}

JP_INTERNAL(void) JP_GetDouble(TJQuery &qry, int colNo, double &into)
{
  into = 0.0;
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
    into = atof(value);
}

JP_INTERNAL(void) JP_GetDate(TJQuery &qry, int colNo, char *into)
{
  memset(into, 0, 9);
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
  {
    TPGDate date(value);
    _FromDate(date, into);
  }
}

JP_INTERNAL(void) JP_GetTime(TJQuery &qry, int colNo, char *into)
{
  memset(into, 0, 7);
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
  {
    TPGTime time(value);
    _FromTime(time, into);
  }
}

JP_INTERNAL(void) JP_GetDateTime(TJQuery &qry, int colNo, char *into)
{
  memset(into, 0, 15);
  char* value = PQgetvalue(qry.pgResult, qry.rowIndex-1, colNo);
  if (value != 0)
  {
    TPGDateTime datetime(value);
    _FromDateTime(datetime, into);
  }
}

JP_INTERNAL(void) JP_GetNull(TJQuery &qry, int colNo, int16 &into)
{
  into = PQgetisnull(qry.pgResult, qry.rowIndex-1, colNo) == 1 ? JP_NULL : JP_NOT_NULL;
}

//JP_INTERNAL(void) JP_CheckError(TJConnector &conn, const char *file, int line)
//{
//  if (conn.result != 0)
//    throw TPGApiException(conn.result, conn.msgbuf, file, line);
//}

JP_INTERNAL(void) JP_BindString(TJQuery &qry, int bindNo, char *data, int dataLen, bool ansichar)
{
  qry.paramValues[bindNo]  = (const char*)data;
  qry.paramTypes[bindNo]   = ansichar == true ? CHAROID : VARCHAROID;
  qry.paramLengths[bindNo] = dataLen;
  qry.paramFormats[bindNo] = 0;
}

//JP_INTERNAL(void) JP_BindBLOB(TJQuery &qry, int bindNo, char *data, int dataSize)
//{
//}

JP_INTERNAL(void) JP_BindLong(TJQuery &qry, int bindNo, int64 &data)
{
  qry.paramValues[bindNo]  = (const char *)&data;
  qry.paramTypes[bindNo]   = INT8OID;
  qry.paramLengths[bindNo] = sizeof(int64);
  qry.paramFormats[bindNo] = 1;
}

JP_INTERNAL(void) JP_BindLongNull(TJQuery &qry, int bindNo, int64 &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = INT8OID;
    qry.paramLengths[bindNo] = sizeof(int64);
    qry.paramFormats[bindNo] = 1;
  }
  else JP_BindLong(qry, bindNo, data);
}

JP_INTERNAL(void) JP_BindInt(TJQuery &qry, int bindNo, int32 &data)
{
  qry.paramValues[bindNo]  = (const char *)&data;
  qry.paramTypes[bindNo]   = INT4OID;
  qry.paramLengths[bindNo] = sizeof(int32);
  qry.paramFormats[bindNo] = 1;
}

JP_INTERNAL(void) JP_BindIntNull(TJQuery &qry, int bindNo, int32 &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = INT4OID;
    qry.paramLengths[bindNo] = sizeof(int32);
    qry.paramFormats[bindNo] = 1;
  }
  else JP_BindInt(qry, bindNo, data);
}

JP_INTERNAL(void) JP_BindShort(TJQuery &qry, int bindNo, int16 &data)
{
  qry.paramValues[bindNo]  = (const char *)&data;
  qry.paramTypes[bindNo]   = INT2OID;
  qry.paramLengths[bindNo] = sizeof(int16);
  qry.paramFormats[bindNo] = 1;
}

JP_INTERNAL(void) JP_BindShortNull(TJQuery &qry, int bindNo, int16 &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = INT2OID;
    qry.paramLengths[bindNo] = sizeof(int16);
    qry.paramFormats[bindNo] = 1;
  }
  else JP_BindShort(qry, bindNo, data);
}

JP_INTERNAL(void) JP_BindDouble(TJQuery &qry, int bindNo, double &data, int precision, int scale)
{
  qry.paramValues[bindNo]  = (const char *)&data;
  qry.paramTypes[bindNo]   = FLOAT8OID;
  qry.paramLengths[bindNo] = sizeof(double);
  qry.paramFormats[bindNo] = 1;
}

JP_INTERNAL(void) JP_BindDoubleNull(TJQuery &qry, int bindNo, double &data, int precision, int scale, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = FLOAT8OID;
    qry.paramLengths[bindNo] = sizeof(double);
    qry.paramFormats[bindNo] = 1;
  }
  else JP_BindDouble(qry, bindNo, data, precision, scale);
}

JP_INTERNAL(void) JP_BindDecimal(TJQuery &qry, int bindNo, char *data, int precision, int scale)
{
  qry.paramValues[bindNo]  = (const char *)data;
  qry.paramTypes[bindNo]   = NUMERICOID;
  qry.paramLengths[bindNo] = precision+3;
  qry.paramFormats[bindNo] = 0;
}

JP_INTERNAL(void) JP_BindDecimalNull(TJQuery &qry, int bindNo, char *data, int precision, int scale, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = NUMERICOID;
    qry.paramLengths[bindNo] = precision+3;
    qry.paramFormats[bindNo] = 0;
  }
  else JP_BindDecimal(qry, bindNo, data, precision, scale);
}

JP_INTERNAL(void) JP_BindPGDate(TJQuery &qry, int bindNo, TPGDate &data)
{
  qry.paramValues[bindNo]  = (const char*)data.date;
  qry.paramTypes[bindNo]   = DATEOID;
  qry.paramLengths[bindNo] = 10;
  qry.paramFormats[bindNo] = 0;
}

JP_INTERNAL(void) JP_BindPGDateNull(TJQuery &qry, int bindNo, TPGDate &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = DATEOID;
    qry.paramLengths[bindNo] = 10;
    qry.paramFormats[bindNo] = 0;
  }
  else JP_BindPGDate(qry, bindNo, data);
}

JP_INTERNAL(void) JP_BindPGTime(TJQuery &qry, int bindNo, TPGTime &data)
{
  qry.paramValues[bindNo]  = (const char*)data.time;
  qry.paramTypes[bindNo]   = TIMEOID;
  qry.paramLengths[bindNo] = 8;
  qry.paramFormats[bindNo] = 0;
}

JP_INTERNAL(void) JP_BindPGTimeNull(TJQuery &qry, int bindNo, TPGTime &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = TIMEOID;
    qry.paramLengths[bindNo] = 0;
    qry.paramFormats[bindNo] = 0;
  }
  else JP_BindPGTime(qry, bindNo, data);
}

JP_INTERNAL(void) JP_BindPGDateTime(TJQuery &qry, int bindNo, TPGDateTime &data)
{
  qry.paramValues[bindNo]  = (const char*)data.datetime;
  qry.paramTypes[bindNo]   = TIMESTAMPOID;
  qry.paramLengths[bindNo] = 19;
  qry.paramFormats[bindNo] = 0;
}

JP_INTERNAL(void) JP_BindPGDateTimeNull(TJQuery &qry, int bindNo, TPGDateTime &data, int16 &null)
{
  if (null == JP_NULL)
  {
    qry.paramValues[bindNo]  = 0;
    qry.paramTypes[bindNo]   = TIMESTAMPOID;
    qry.paramLengths[bindNo] = 19;
    qry.paramFormats[bindNo] = 0;
  }
  else JP_BindPGDateTime(qry, bindNo, data);
}

JP_INTERNAL(void) JP_Sequence(TJQuery& qry, int32 &sequence, const char *sequencer)
{
  TJQuery q(qry.conn);
  char work[1024];
  strcpy(work, "select nextval('");
  strcat(work, sequencer);
  strcat(work, "')" );
  q.Open(work, 0, 1);
  q.Exec();
  if (q.Fetch() == true)
    q.Get(0, sequence);
  else
    sequence = 0;
}

JP_INTERNAL(void) JP_UserStamp(TJQuery& qry, char* userStamp, int len)
{
  strncpy(userStamp, (char*)qry.conn.user, len-1);
  userStamp[len-1] = 0;
}

JP_INTERNAL(void) JP_Date(TJQuery& qry, TPGDate &pgdate, char *aDate)
{
  char date[9];
  strncpy(date, aDate, 8); date[8]=0;
  _ToDate(pgdate, date);
}

JP_INTERNAL(void) JP_Time(TJQuery& qry, TPGTime &pgtime, char *aTime)
{
  char time[7];
  strncpy(time, aTime, 6); time[6]=0;
  _ToTime(pgtime, time);
}

JP_INTERNAL(void) JP_DateTime(TJQuery& qry, TPGDateTime &pgdatetime, char *aDateTime)
{
  char datetime[15];
  strncpy(datetime, aDateTime, 14); datetime[14]=0;
  _ToDateTime(pgdatetime, datetime);
}

JP_INTERNAL(void) JP_TimeStamp(TJQuery& qry, TPGDateTime &pgtimestamp, char *timeStamp)
{
  qry.conn.TimeStamp(timeStamp);
  JP_DateTime(qry, pgtimestamp, timeStamp);
}


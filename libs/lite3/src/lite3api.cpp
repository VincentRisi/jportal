#include "lite3api.h"

enum
{ NOT_IN_ERROR = 0
, CONNECT_NOT_INITIALISED = 1001
, QUERY_NOT_ENOUGH_DYNAMIC_SPACE
, QUERY_NOT_INITIALISED
, NULL_FIELD_ERROR
};

Lite3Connector::Lite3Connector()
{
  conn = 0;
}

Lite3Connector::~Lite3Connector()
{
  if (conn)
    sqlite3_close(conn);
}

void Lite3Connector::open(char* databaseName)
{
  int rc = sqlite3_open(databaseName, &conn);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Database open failed", LITE3_MARK);
}

void Lite3Connector::close()
{
  int rc = sqlite3_close(conn);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Database close failed", LITE3_MARK);
  conn = 0;
}

void Lite3Connector::begin()
{
  if (conn == 0)
    throw Lite3Exception(CONNECT_NOT_INITIALISED, "Connection not initialised", LITE3_MARK);
  Lite3Query commit(this);
  commit.init("begin", 0);
  commit.exec();
  commit.close();
}

void Lite3Connector::commit()
{
  if (conn == 0)
    throw Lite3Exception(CONNECT_NOT_INITIALISED, "Connection not initialised", LITE3_MARK);
  Lite3Query commit(this);
  commit.init("commit", 0);
  commit.exec();
  commit.close();
}

void Lite3Connector::rollback()
{
  if (conn == 0)
    throw Lite3Exception(CONNECT_NOT_INITIALISED, "Connection not initialised", LITE3_MARK);
  Lite3Query rollback(this);
  rollback.init("rollback", 0);
  rollback.exec();
  rollback.close();
}

Lite3Query::Lite3Query(Lite3Connector *connector)
{
  if (connector != 0 && connector->conn == 0)
    throw Lite3Exception(CONNECT_NOT_INITIALISED, "Connection not initialised", LITE3_MARK);
  this->connector = connector;
  commandSize = 0;
  command = 0;
  stmt = 0;
}

Lite3Query::~Lite3Query()
{
  if (command != 0)
    delete [] command;
  if (stmt != 0)
    sqlite3_finalize(stmt);
}

/**
 * This just sets up the command string - exec does the prepare and
 * execute.
 */
void Lite3Query::init(const char *command, int dynSize)
{
  commandSize = strlen(command)+dynSize+1;
  this->command = new char [commandSize];
  strcpy(this->command, command);
}

void Lite3Query::dynamic(char *key, char *value)
{
  if (command == 0)
    throw Lite3Exception(QUERY_NOT_INITIALISED, "Query not initialised", LITE3_MARK);
  int keylen;
  int vlen;
  char* p;
  char* q;
  keylen = strlen(key);
  vlen = strlen(value);
  while ((p = strstr(command, key)) != 0)
  {
    int i, len;
    q = p+keylen;
    len = strlen(q)+1;
    if (&p[len+vlen]-command > commandSize)
      throw Lite3Exception(QUERY_NOT_ENOUGH_DYNAMIC_SPACE, "Not enough space for dynamic data", LITE3_MARK);
    memcpy(p, q, len);
    for (i = len; i >= 0; i--)
      p[i+vlen] = p[i];
    memcpy(p, value, vlen);
  }
}

void Lite3Query::prepare()
{
  const char* tail;
  int rc = sqlite3_prepare(connector->conn, command, strlen(command), &stmt, &tail);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Statement preparation failed", LITE3_MARK);
}

void Lite3Query::bindChar(int no, char *data, int len)
{
  if (stmt == 0) prepare();
  int rc;
  //if (null != 0 && *null != 0)
  //  rc = sqlite3_bind_null(stmt, no);
  //else
  rc = sqlite3_bind_text(stmt, no, data, strlen(data), 0);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of text failed", LITE3_MARK);
}

void Lite3Query::bindTinyInt(int no, char *data, int *null)
{
  signed char* sdata = (signed char *) data;
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
    rc = sqlite3_bind_int(stmt, no, *sdata);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of tiny int failed", LITE3_MARK);
}

void Lite3Query::bindShort(int no, short *data, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
    rc = sqlite3_bind_int(stmt, no, *data);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of short int failed", LITE3_MARK);
}

void Lite3Query::bindInt(int no, int *data, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
    rc = sqlite3_bind_int(stmt, no, *data);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of int failed", LITE3_MARK);
}

void Lite3Query::bindLong(int no, long *data, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
    rc = sqlite3_bind_int64(stmt, no, *data);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of long failed", LITE3_MARK);
}

void Lite3Query::bindDouble(int no, double *data, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
    rc = sqlite3_bind_double(stmt, no, *data);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of double failed", LITE3_MARK);
}

void Lite3Query::bindDate(int no, char *data, int len, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
  {
    char work[11];
    strncpy(work, "YYYY-MM-DD");
    if (len >= 8) memcpy(work, data, 4);
    if (len >= 8) memcpy(work+5, data+4, 2);
    if (len >= 8) memcpy(work+8, data+6, 2);
    rc = sqlite3_bind_text(stmt, no, work, 11, 0);
  }
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of date failed", LITE3_MARK);
}

void Lite3Query::bindTime(int no, char *data, int len, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
  {
    char work[9];
    strncpy(work, "HH:MI:SS");
    if (len >= 6) memcpy(work, data, 2);
    if (len >= 6) memcpy(work+3, data+2, 2);
    if (len >= 6) memcpy(work+6, data+4, 2);
    rc = sqlite3_bind_text(stmt, no, work, 9, 0);
  }
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of time failed", LITE3_MARK);
}

void Lite3Query::bindDateTime(int no, char *data, int len, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
  {
    char work[20];
    strncpy(work, "YYYY-MM-DD HH:MI:SS");
    if (len >= 14) memcpy(work, data, 4);
    if (len >= 14) memcpy(work+5, data+4, 2);
    if (len >= 14) memcpy(work+8, data+6, 2);
    if (len >= 14) memcpy(work+11, data+8, 2);
    if (len >= 14) memcpy(work+14, data+10, 2);
    if (len >= 14) memcpy(work+17, data+12, 2);
    rc = sqlite3_bind_text(stmt, no, work, 20, 0);
  }
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of date time failed", LITE3_MARK);
}

void Lite3Query::bindTimeStamp(int no, char *data, int len, int *null)
{
  if (stmt == 0) prepare();
  int rc;
  if (null != 0 && *null != 0)
    rc = sqlite3_bind_null(stmt, no);
  else
  {
    char work[20];
    strncpy(work, "YYYY-MM-DD HH:MI:SS");
    if (len >= 14) memcpy(work, data, 4);
    if (len >= 14) memcpy(work+5, data+4, 2);
    if (len >= 14) memcpy(work+8, data+6, 2);
    if (len >= 14) memcpy(work+11, data+8, 2);
    if (len >= 14) memcpy(work+14, data+10, 2);
    if (len >= 14) memcpy(work+17, data+12, 2);
    rc = sqlite3_bind_text(stmt, no, work, 20, 0);
  }
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Bind of time stamp failed", LITE3_MARK);
}

bool Lite3Query::isNull(int no, int *null, int &rc)
{
  rc = sqlite3_column_type(stmt, no);
  if (rc == SQLITE_NULL)
  {
    if (null == 0)
      throw Lite3Exception(NULL_FIELD_ERROR, "Data is null but null is not catered for", LITE3_MARK);
    *null = 1;
    return true;
  }
  if (null != 0)
    *null = 0;
  return false;
}

void Lite3Query::getChar(int no, char *data, int len)
{
  int rc;
  int null;
  if (isNull(no, &null, rc) == true)
  {
    data[0] = 0;
    return;
  }
  if (rc != SQLITE_TEXT)
    throw Lite3Exception(rc, "Get of type not text", LITE3_MARK);
  const unsigned char* value = sqlite3_column_text(stmt, no);
  strncpy(data, (const char*)value, len);
  data[len] = 0;
}

void Lite3Query::getTinyInt(int no, char *data, int *null)
{
  signed char* sdata = (signed char*) data;
  int rc;
  if (isNull(no, null, rc) == true)
  {
    *data = 0;
    return;
  }
  if (rc != SQLITE_INTEGER && rc != SQLITE_FLOAT)
    throw Lite3Exception(rc, "Get of type not numeric", LITE3_MARK);
  *sdata = (signed char)sqlite3_column_int(stmt, no);
}

void Lite3Query::getShort(int no, short *data, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    *data = 0;
    return;
  }
  if (rc != SQLITE_INTEGER && rc != SQLITE_FLOAT)
    throw Lite3Exception(rc, "Get of type not numeric", LITE3_MARK);
  *data = (short)sqlite3_column_int(stmt, no);
}

void Lite3Query::getInt(int no, int *data, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    *data = 0;
    return;
  }
  if (rc != SQLITE_INTEGER && rc != SQLITE_FLOAT)
    throw Lite3Exception(rc, "Get of type not numeric", LITE3_MARK);
  *data = sqlite3_column_int(stmt, no);
}

void Lite3Query::getLong(int no, long *data, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    *data = 0;
    return;
  }
  if (rc != SQLITE_INTEGER && rc != SQLITE_FLOAT)
    throw Lite3Exception(rc, "Get of type not numeric", LITE3_MARK);
  *data = (long)sqlite3_column_int64(stmt, no);
}

void Lite3Query::getDouble(int no, double *data, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    *data = 0;
    return;
  }
  if (rc != SQLITE_INTEGER && rc != SQLITE_FLOAT)
    throw Lite3Exception(rc, "Get of type not numeric", LITE3_MARK);
  *data = (double)sqlite3_column_int64(stmt, no);
}

void Lite3Query::getDate(int no, char *data, int len, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    data[0] = 0;
    return;
  }
  if (rc != SQLITE_TEXT)
    throw Lite3Exception(rc, "Get of type not text", LITE3_MARK);
  const unsigned char* value = sqlite3_column_text(stmt, no);
  if (len >= 4) strncpy(data, (const char*)value, 4);
  if (len >= 6) strncpy(data+4, (const char*)value+5, 2);
  if (len >= 8) strncpy(data+6, (const char*)value+8, 2);
  data[len] = 0;
}

void Lite3Query::getTime(int no, char *data, int len, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    data[0] = 0;
    return;
  }
  if (rc != SQLITE_TEXT)
    throw Lite3Exception(rc, "Get of type not text", LITE3_MARK);
  const unsigned char* value = sqlite3_column_text(stmt, no);
  strncpy(data, (const char*)value, len);
  if (len >= 2) strncpy(data+8, (const char*)value+11, 2);
  if (len >= 4) strncpy(data+10, (const char*)value+14, 2);
  if (len >= 6) strncpy(data+12, (const char*)value+17, 2);
  data[len] = 0;
}

// YYYY-MM-DD hh:mm:ss.sss
// 01234567890123456789012
void Lite3Query::getDateTime(int no, char *data, int len, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    data[0] = 0;
    return;
  }
  if (rc != SQLITE_TEXT)
    throw Lite3Exception(rc, "Get of type not text", LITE3_MARK);
  const unsigned char* value = sqlite3_column_text(stmt, no);
  if (len >= 4) strncpy(data, (const char*)value, 4);
  if (len >= 6) strncpy(data+4, (const char*)value+5, 2);
  if (len >= 8) strncpy(data+6, (const char*)value+8, 2);
  if (len >= 10) strncpy(data+8, (const char*)value+11, 2);
  if (len >= 12) strncpy(data+10, (const char*)value+14, 2);
  if (len >= 14) strncpy(data+12, (const char*)value+17, 2);
  data[len] = 0;
}

void Lite3Query::getTimeStamp(int no, char *data, int len, int *null)
{
  int rc;
  if (isNull(no, null, rc) == true)
  {
    data[0] = 0;
    return;
  }
  if (rc != SQLITE_TEXT)
    throw Lite3Exception(rc, "Get of type not text", LITE3_MARK);
  const unsigned char* value = sqlite3_column_text(stmt, no);
  if (len >= 4) strncpy(data, (const char*)value, 4);
  if (len >= 6) strncpy(data+4, (const char*)value+5, 2);
  if (len >= 8) strncpy(data+6, (const char*)value+8, 2);
  if (len >= 10) strncpy(data+8, (const char*)value+11, 2);
  if (len >= 12) strncpy(data+10, (const char*)value+14, 2);
  if (len >= 14) strncpy(data+12, (const char*)value+17, 2);
  data[len] = 0;
}

void Lite3Query::exec()
{
  if (stmt == 0) prepare();
  int rc = sqlite3_step(stmt);
  if (rc != SQLITE_DONE)
    throw Lite3Exception(rc, "Execution of action failed to get done", LITE3_MARK);
}

bool Lite3Query::fetch()
{
  if (stmt == 0) prepare();
  int rc = sqlite3_step(stmt);
  bool result;
  if (rc == SQLITE_ROW)
    result = true;
  else if (rc == SQLITE_DONE)
  {
    result = false;
    close();
  }
  else
    throw Lite3Exception(rc, "Execution of fetch failed", LITE3_MARK);
  return result;
}

void Lite3Query::close()
{
  int rc = sqlite3_finalize(stmt);
  if (rc != SQLITE_OK)
    throw Lite3Exception(rc, "Finalize of query statement error", LITE3_MARK);
  stmt = 0;
}

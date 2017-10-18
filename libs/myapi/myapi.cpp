#include "myapi.h"

static int db_bind(DB_BIND* db_bind, MYSQL_BIND* bind, int buffer_type, void *data, int buffer_length, int *null, DB_TRANSFER_FUNC transfer)
{
  bind->buffer_type = (enum_field_types) buffer_type;
  bind->buffer_length = buffer_length;
  if (buffer_length != 0)
    bind->length = &db_bind->result_length;
  db_bind->transfer = transfer;
  db_bind->buffer = data;
  if (db_bind->transfer != 0)
    bind->buffer = (char *)&db_bind->union_buffer;
  else
    bind->buffer = (char *)data;
  if (null != 0)
    bind->is_null = &db_bind->is_null;
  else
    bind->is_null = 0;
  return 0;
}

static int _char2date(MYSQL_TIME *target, char *source)
{
  char work[5];
  strncpy(work, source, 4);
  work[4] = 0;
  target->year = (unsigned int)atoi(work);
  strncpy(work, source+4, 2);
  work[2] = 0;
  target->month = (unsigned int)atoi(work);
  strncpy(work, source+6, 2);
  work[2] = 0;
  target->day = (unsigned int)atoi(work);
  return 0;
}

static int _char2time(MYSQL_TIME *target, char *source)
{
  char work[3];
  strncpy(work, source, 2);
  work[2] = 0;
  target->hour = (unsigned int)atoi(work);
  strncpy(work, source+2, 2);
  work[2] = 0;
  target->minute = (unsigned int)atoi(work);
  strncpy(work, source+4, 2);
  work[2] = 0;
  target->second = (unsigned int)atoi(work);
  return 0;
}

static int db_date_put(void *target, void *source)
{
  // source char "YYYYMMDD" target MYSQL_TIME
  char *s = (char*) source;
  MYSQL_TIME *t = (MYSQL_TIME *) target;
  memset(t, 0, sizeof(MYSQL_TIME));
  _char2date(t, s);
  return 0;
}

static int db_time_put(void *target, void *source)
{
  // source char "HHMISS" target MYSQL_TIME
  char *s = (char*) source;
  MYSQL_TIME *t = (MYSQL_TIME *) target;
  memset(t, 0, sizeof(MYSQL_TIME));
  _char2date(t, s);
  return 0;
}

static int db_datetime_put(void *target, void *source)
{
  // source char "YYYYMMDDHHMISS" target MYSQL_TIME
  char *s = (char*) source;
  MYSQL_TIME *t = (MYSQL_TIME *) target;
  memset(t, 0, sizeof(MYSQL_TIME));
  db_date_put(t, s);
  db_time_put(t, s+8);
  return 0;
}

static int db_date_get(void *target, void *source)
{
  // target char "YYYYMMDD" source MYSQL_TIME
  char *t = (char*) target;
  MYSQL_TIME *s = (MYSQL_TIME *) source;
  sprintf(t, "%04.4d%02.2d%02.2d", s->year, s->month, s->day);
  return 0;
}

static int db_datetime_get(void *target, void *source)
{
  // target char "YYYYMMDDHHMISS" source MYSQL_TIME
  char *t = (char*) target;
  MYSQL_TIME *s = (MYSQL_TIME *) source;
  sprintf(t, "%02.2d%02.2d%02.2d", s->hour, s->minute, s->second);
  return 0;
}

static int db_time_get(void *target, void *source)
{
  // target char "HHMISS" source MYSQL_TIME
  char *t = (char*) target;
  MYSQL_TIME *s = (MYSQL_TIME *) source;
  sprintf(t, "%04.4d%02.2d%02.2d%02.2d%02.2d%02.2d", s->year, s->month, s->day, s->hour, s->minute, s->second);
  return 0;
}

int db_connector_init(DB_CONNECTOR* connector, const char *host, const char *user, const char *passwd, const char *db)
{
  int rc;
  memset(connector, 0, sizeof(DB_CONNECTOR));
  connector->host = strdup(host);
  connector->user = strdup(user);
  connector->passwd = strdup(passwd);
  connector->db = strdup(db);
  connector->state = 0xCC;
  if (mysql_init(&connector->mysql) == 0)
  {
    connector->error = "mysql_init Failed: most probably lack of memory";
    return 1;
  }
  if (mysql_real_connect(&connector->mysql, host, user, passwd, db, 0, NULL, 0) == 0)
  {
    connector->error = (char*)mysql_error(&connector->mysql);
    return 1;
  }
  return 0;
}

int db_connector_close(DB_CONNECTOR* connector)
{
  if (connector->state == 0xCC)
  {
    free(connector->host);
    free(connector->user);
    free(connector->passwd);
    free(connector->db);
    mysql_close(&connector->mysql);
  }
  memset(connector, 0xFF, sizeof(DB_CONNECTOR));
  return 0;
}

int db_connector_commit(DB_CONNECTOR* connector)
{
  mysql_commit(&connector->mysql);
  return 0;
}

int db_connector_rollback(DB_CONNECTOR* connector)
{
  mysql_rollback(&connector->mysql);
  return 0;
}

int db_query_init(DB_QUERY* query, DB_CONNECTOR* connector, char *command, int dynamicSize, int nobinds, int nodefines)
{
  memset(query, 0, sizeof(DB_QUERY));
  query->connector = connector;
  query->nobinds = nobinds;
  query->nodefines = nodefines;
  int length = strlen(command)+dynamicSize+1;
  if ((query->command = (char*) calloc(length, 1)) == 0)
  {
    connector->error = "Memory Allocation Error for Command";
    return 1;
  }
  strncpy(query->command, command, length);
  if ((query->db_binds = (DB_BIND*)calloc(nobinds, sizeof(DB_BIND))) == 0)
  {
    connector->error = "Memory Allocation Error";
    return 1;
  }
  if ((query->db_defines = (DB_BIND*)calloc(nobinds, sizeof(DB_BIND))) == 0)
  {
    connector->error = "Memory Allocation Error";
    return 1;
  }
  if ((query->binds = (MYSQL_BIND*)calloc(nobinds, sizeof(MYSQL_BIND))) == 0)
  {
    connector->error = "Memory Allocation Error";
    return 1;
  }
  if ((query->defines = (MYSQL_BIND*)calloc(nobinds, sizeof(MYSQL_BIND))) == 0)
  {
    connector->error = "Memory Allocation Error";
    return 1;
  }
  if ((query->stmt = mysql_stmt_init(&connector->mysql)) == 0)
  {
    connector->error = (char*)mysql_error(&connector->mysql);
    return 1;
  }
  query->state = 0xDD;
  return 0;
}

int db_query_close(DB_QUERY* query)
{
  int rc = 0;
  DB_CONNECTOR* connector = query->connector;
  if (query->state == 0xDD)
  {
    if (query->nobinds > 0) 
    {
      free(query->binds);
      free(query->db_binds);
    }
    if (query->nodefines > 0) 
    {
      free(query->defines);
      free(query->db_defines);
    }
    free(query->command);
    if (query->stmt != 0)
    {
      if (mysql_stmt_close(query->stmt) != 0)
      {
        connector->error = (char*)mysql_error(&connector->mysql);
        rc = 1;
      }
    }
    if (query->result != 0)
    {
      if (mysql_stmt_free_result(query->stmt) != 0)
      {
        connector->error = (char*)mysql_error(&connector->mysql);
        rc = 1;
      }
    }
  }
  memset(query, 0xFF, sizeof(DB_QUERY));
  return rc;
}

int db_query_dynamic(DB_QUERY* query, char* key, char* value)
{
  DB_CONNECTOR* connector = query->connector;
  int keylen;
  int vlen;
  char* p;
  char* q;
  if (query->state != 0xDD) 
  {
    connector->error = "Query is in invalid state. Most probably already closed.";
    return 1;
  }
  keylen = strlen(key);
  vlen = strlen(value);
  while ((p = strstr(query->command, key)) != 0)
  {
    q = p+keylen;
    int len = strlen(q)+1;
    memcpy(p, q, len);
    int i;
    for (i = len; i >= 0; i--) 
      p[i+vlen] = p[i];
    memcpy(p, value, vlen);
  }
  return 0;
}

int db_query_bind_tinyint(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_TINY, data, 0, null, 0);
}

int db_query_bind_short(DB_QUERY* query, int no, short* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_SHORT, data, 0, null, 0);
}

int db_query_bind_int(DB_QUERY* query, int no, int* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_LONG, data, 0, null, 0);
}

int db_query_bind_long(DB_QUERY* query, int no, long* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_LONG, data, 0, null, 0);
}

int db_query_bind_char(DB_QUERY* query, int no, char* data, int length, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_STRING, data, length, null, 0);
}

int db_query_bind_blob(DB_QUERY* query, int no, void* data, int length, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_BLOB, data, length, null, 0);
}

int db_query_bind_tlob(DB_QUERY* query, int no, char* data, int length, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_BLOB, data, length, null, 0);
}

int db_query_bind_date(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_DATE, data, 0, null, db_date_put);
}

int db_query_bind_time(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_TIME, data, 0, null, db_time_put);
}

int db_query_bind_datetime(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_DATETIME, data, 0, null, db_datetime_put);
}

int db_query_bind_timestamp(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_TIMESTAMP, data, 0, null, db_datetime_put);
}

int db_query_bind_double(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_binds[no], &query->binds[no], MYSQL_TYPE_DOUBLE, data, 0, null, 0);
}

int db_query_define_tinyint(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_TINY, data, 0, null, 0);
}

int db_query_define_short(DB_QUERY* query, int no, short* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_SHORT, data, 0, null, 0);
}

int db_query_define_int(DB_QUERY* query, int no, int* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_LONG, data, 0, null, 0);
}

int db_query_define_long(DB_QUERY* query, int no, long* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_LONG, data, 0, null, 0);
}

int db_query_define_char(DB_QUERY* query, int no, char* data, int length, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_STRING, data, length, null, 0);
}

int db_query_define_blob(DB_QUERY* query, int no, void* data, int length, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_BLOB, data, length, null, 0);
}

int db_query_define_tlob(DB_QUERY* query, int no, char* data, int length, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_BLOB, data, length, null, 0);
}

int db_query_define_date(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_DATE, data, 0, null, db_date_get);
}

int db_query_define_time(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_TIME, data, 0, null, db_time_get);
}

int db_query_define_datetime(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_DATETIME, data, 0, null, db_datetime_get);
}

int db_query_define_timestamp(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_TIMESTAMP, data, 0, null, db_datetime_get);
}

int db_query_define_double(DB_QUERY* query, int no, char* data, int* null)
{
  return db_bind(&query->db_defines[no], &query->defines[no], MYSQL_TYPE_DOUBLE, data, 0, null, 0);
}

int db_query_execute(DB_QUERY* query)
{
  int i;
  int rc;
  DB_BIND* db_bind;
  MYSQL_BIND* bind;
  DB_CONNECTOR* connector = query->connector;
  if ((mysql_stmt_prepare(query->stmt, query->command, strlen(query->command))) != 0)
  {
    connector->error = (char*)mysql_error(&connector->mysql);
    return 1;
  }
  query->param_count = mysql_stmt_param_count(query->stmt);
  if (query->nobinds > 0)
  {
    for (i=0; i < query->nobinds; i++)
    {
      db_bind = &query->db_binds[i]; 
      bind = &query->binds[i];
      if (db_bind->transfer != 0)
        db_bind->transfer(bind->buffer, (void*)db_bind->buffer);
    }
    if ((mysql_stmt_bind_param(query->stmt, query->binds)) != 0)
    {
      connector->error = (char*)mysql_error(&connector->mysql);
      return 1;
    }
  }
  if (query->nodefines > 0)
  {
    query->result = mysql_stmt_result_metadata(query->stmt);
    if (query->result == 0)
    {
      connector->error = (char*)mysql_error(&connector->mysql);
      return 1;
    }
    query->column_count = mysql_num_fields(query->result);
  }
  if ((mysql_stmt_execute(query->stmt)) != 0)
  {
    connector->error = (char*)mysql_error(&connector->mysql);
    return 1;
  }
  return 0;
}

int db_query_fetch(DB_QUERY* query, int rebind)
{
  int i;
  int rc;
  DB_BIND* db_define;
  DB_CONNECTOR* connector = query->connector;
  MYSQL_BIND* bind;
  MYSQL_ROW row;
  if (rebind == 1)
  {
    rc = mysql_stmt_bind_result(query->stmt, query->defines);
    if (rc != 0)
    {
      connector->error = (char*)mysql_error(&connector->mysql);
      return -1;
    }
  }
  row = mysql_fetch_row(query->result);
  if (row == 0)
    return 0;
  for (i=0; i < query->nodefines; i++)
  {
    db_define = &query->db_defines[i]; 
    bind = &query->defines[i];
    if (db_define->transfer != 0)
      db_define->transfer((void*)db_define->buffer, bind->buffer);
  }
  return 1;
}


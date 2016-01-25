#ifndef myapiH
#define myapiH

#ifndef _WINSOCKAPI_
#include <winsock2.h>
#endif
#include "mysql.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct SDB_CONNECTOR
{
  char state;
  char *host;
  char *user;
  char *passwd;
  char *db;
  char *error;
  MYSQL mysql;
} DB_CONNECTOR;

int db_connector_init(DB_CONNECTOR* connector, const char *host, const char *user, const char *passwd, const char *db);
int db_connector_close(DB_CONNECTOR* connector);
int db_connector_commit(DB_CONNECTOR* connector);
int db_connector_rollback(DB_CONNECTOR* connector);

typedef int (*DB_TRANSFER_FUNC)(void *target, void *source);

typedef struct SDB_BIND
{
  union
  {
    MYSQL_TIME mysql_time;
  } union_buffer;
  void* buffer;
  my_bool is_null;
  unsigned long result_length;
  DB_TRANSFER_FUNC transfer;
} DB_BIND;

typedef struct SDB_QUERY
{
  char state;
  int nobinds;
  int nodefines;
  unsigned int param_count;
  unsigned int column_count;
  char *command;
  DB_CONNECTOR *connector;
  DB_BIND *db_binds;   
  DB_BIND *db_defines;   
  MYSQL_BIND *binds;
  MYSQL_BIND *defines;
  MYSQL_STMT *stmt;
  MYSQL_RES *result;
  void *data;
} DB_QUERY;

int db_query_init(DB_QUERY* query, DB_CONNECTOR* connector, char *command, int dynamic_size, int no_binds, int no_defines);
int db_query_dynamic(DB_QUERY* query, char* name, char* value);

int db_query_bind_tinyint(DB_QUERY* query, int no, char* data, int* null);
int db_query_bind_short(DB_QUERY* query, int no, short* data, int* null);
int db_query_bind_int(DB_QUERY* query, int no, int* data, int* null);
int db_query_bind_long(DB_QUERY* query, int no, long* data, int* null);
int db_query_bind_char(DB_QUERY* query, int no, char* data, int length, int* null);
int db_query_bind_blob(DB_QUERY* query, int no, void* data, int length, int* null);
int db_query_bind_tlob(DB_QUERY* query, int no, char* data, int length, int* null);
int db_query_bind_date(DB_QUERY* query, int no, char* data, int* null);
int db_query_bind_time(DB_QUERY* query, int no, char* data, int* null);
int db_query_bind_datetime(DB_QUERY* query, int no, char* data, int* null);
int db_query_bind_timestamp(DB_QUERY* query, int no, char* data, int* null);
int db_query_bind_double(DB_QUERY* query, int no, char* data, int* null);

int db_query_define_tinyint(DB_QUERY* query, int no, char* data, int length, int* null);
int db_query_define_short(DB_QUERY* query, int no, short* data, int* null);
int db_query_define_int(DB_QUERY* query, int no, int* data, int* null);
int db_query_define_long(DB_QUERY* query, int no, long* data, int* null);
int db_query_define_char(DB_QUERY* query, int no, char* data, int length, int* null);
int db_query_define_blob(DB_QUERY* query, int no, void* data, int length, int* null);
int db_query_define_tlob(DB_QUERY* query, int no, char* data, int length, int* null);
int db_query_define_date(DB_QUERY* query, int no, char* data, int* null);
int db_query_define_time(DB_QUERY* query, int no, char* data, int* null);
int db_query_define_datetime(DB_QUERY* query, int no, char* data, int* null);
int db_query_define_timestamp(DB_QUERY* query, int no, char* data, int* null);
int db_query_define_double(DB_QUERY* query, int no, char* data, int* null);

int db_query_execute(DB_QUERY* query);
int db_query_fetch(DB_QUERY* query, int rebind);
int db_query_close(DB_QUERY* query);

#ifdef __cplusplus
}
#endif

#endif
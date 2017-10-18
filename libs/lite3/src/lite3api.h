#ifndef lite3H
#define lite3H

#include <stdio.h>
#include "xcept.h"
#include "sqlite3.h"

#define LITE3_MARK __FILE__,  __LINE__

struct Lite3Exception : public xDBException
{
  Lite3Exception(int aErrorNo, char *error, char *aFile, int aLine)
  : xDBException(aFile, aLine, "Lite3Exception", aErrorNo, error)
  {}
  Lite3Exception(const Lite3Exception& copy)
  : xDBException(copy)
  {}
};

struct Lite3Connector
{
  sqlite3 *conn;
  Lite3Connector();
  ~Lite3Connector();
  void open(char* databaseName);
  void close();
  void begin();
  void commit();
  void rollback();
};

struct Lite3Query
{
  Lite3Connector *connector;
  sqlite3_stmt *stmt;
  int commandSize;
  char *command;
  Lite3Query(Lite3Connector *connector=0);
  ~Lite3Query();
  void init(const char *command, int dynSize);
  void dynamic(char *key, char *value);
  void bindChar(int no, char *data, int len);
  void bindTinyInt(int no, char *data, int *null=0);
  void bindShort(int no, short *data, int *null=0);
  void bindInt(int no, int *data, int *null=0);
  void bindLong(int no, long *data, int *null=0);
  void bindDouble(int no, double *data, int *null=0);
  void bindDate(int no, char *data, int len, int *null=0);
  void bindTime(int no, char *data, int len, int *null=0);
  void bindDateTime(int no, char *data, int len, int *null=0);
  void bindTimeStamp(int no, char *data, int len, int *null=0);
  void getChar(int no, char *data, int len);
  void getTinyInt(int no, char *data, int *null=0);
  void getShort(int no, short *data, int *null=0);
  void getInt(int no, int *data, int *null=0);
  void getLong(int no, long *data, int *null=0);
  void getDouble(int no, double *data, int *null=0);
  void getDate(int no, char *data, int len, int *null=0);
  void getTime(int no, char *data, int len, int *null=0);
  void getDateTime(int no, char *data, int len, int *null=0);
  void getTimeStamp(int no, char *data, int len, int *null=0);
  void exec();
  bool fetch();
  void close();
private:
  void prepare();
  bool isNull(int no, int *null, int &rc);
};

#endif
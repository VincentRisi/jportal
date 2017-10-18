#ifndef pgapiH
#define pgapiH
#include "machine.h"
#include "padgen.h"
#include "handles.h"

#include "pgmachine.h"

struct TJDate
{
  char *date;
  TJDate(char* aDate) {date = aDate;}
};

struct TJTime
{
  char *time;
  TJTime(char* aTime) {time = aTime;}
};

struct TJDateTime
{
  char *dateTime;
  TJDateTime(char* aDateTime) {dateTime = aDateTime;}
};

struct TPGDate
{
  // format "2008-10-23"
  #define size 10
  char date[size+1];
  TPGDate(char *value=0) {memset(date, 0, sizeof(date));if (value) strncpy(date, value, size);}
  #undef size
};

struct TPGTime
{
  #define size 18
  // format "10:11:17.943402+02"
  char time[size+1];
  TPGTime(char *value=0) {memset(time, 0, sizeof(time));if (value) strncpy(time, value, size);}
  #undef size
};

struct TPGDateTime
{
  #define size 29
  // format "2008-10-23 10:11:17.943402+02"
  char datetime[size+1];
  TPGDateTime(char *value=0) {memset(datetime, 0, sizeof(datetime));if (value) strncpy(datetime, value, size);}
  #undef size
};

/**
 * Helper Class to manage Ubi Impl Addlist allocation and local free.
 */
template <class X>
struct TJAddList
{
  X *recs;
  int noOf;
  TJAddList() {recs = 0; noOf = 0;}
  virtual ~TJAddList() {if (recs) free(recs);}
};

struct TJConnector
{
  int            signature;
  PGconn*        pgConn;
  char           msgbuf[8192];
  int            result;
  ConnStatusType connStatus;
  ExecStatusType execStatus;
  char*          user;
  char*          dbName;
  char*          schema;
  char*          host;
  int            port;
  time_t         serverTime;
  time_t         systemTime;
  bool           loggedOn;
  typedef        tHandle<TJConnector*, 100, 100> ConnectHandle;
  static         ConnectHandle connectHandle;
  int            instance;
  TJConnector()
  {
    memset(this, 0, sizeof(*this));
    signature = 0x0DEC0DED;
    dbName = 0;
    schema = 0;
    user = 0;
    host = 0;
    instance = connectHandle.Create(this);
  }
  ~TJConnector()
  {
    if (dbName) free(dbName);
    if (schema) free(schema);
    if (user)   free(user);
    if (host)   free(host);
    connectHandle.Release(instance, 0);
  }
  static TJConnector* Instance(int instance=100)
  {
    TJConnector* result = (TJConnector*) connectHandle.Use(instance);
    return result;
  }
  void Logon(const char* host, const char* dbName, const char* user, const char* password, int port=5432);
  void Logoff();
  void Commit();
  void Rollback();
  void TimeStamp(char *timeStamp);
  void Error(char *Msg, int MsgLen);
  void ThrowOnError(int error, const char *file=0, int line=0);
};

struct TJQuery
{
  int          signature;
  PGresult*    pgResult;
  int          noBinds;
  int          noColumns;
  int          rowIndex;
  int          noRows;
  int          isOpen;
  char*        command;
  Oid*         paramTypes;
  int*         paramLengths;
  int*         paramFormats;
  const char** paramValues;
  char         file[65];
  int          line;
  void FileAndLine(const char *aFile, int32 aLine)
  {
    strncpy(file, aFile, sizeof(file)-1);
    line = aLine;
  }
  TJConnector &conn;
  TJQuery(TJConnector &aConnector) : conn(aConnector)
  {
    signature = 0x1DEC0DED;
    pgResult = 0;
    noBinds = 0;
    noColumns = 0;
    noRows = 0;
    rowIndex = 0;
    isOpen = 0;
    command = 0;
    paramValues = 0;
    paramTypes = 0;
    paramLengths = 0;
    paramFormats = 0;
    file[sizeof(file)-1] = 0;
  }
  ~TJQuery();
  void  Open(const char* query, int noBinds=0, int noColumns=0);
  void  Bind(int bindNo, char *data, int datalen, bool ansichar=false);
  //void  BindBlob(int bindNo, char *data, int dataSize);
  void  Bind(int bindNo, int64 &data, int64 &work, int16 *null=0);
  void  Bind(int bindNo, int32 &data, int32 &work, int16 *null=0);
  void  Bind(int bindNo, int16 &data, int16 &work, int16 *null=0);
  void  Bind(int bindNo, double &data, double &work, int precision, int scale, int16 *null=0);
  void  Bind(int bindNo, char *data, int precision, int scale, int16 *null=0);
  void  Bind(int bindNo, TPGDate &data, int16 *null=0);
  void  Bind(int bindNo, TPGTime &data, int16 *null=0);
  void  Bind(int bindNo, TPGDateTime &data, int16 *null=0);
  void  Exec();
  bool  Fetch();
  void  Get(int columnNo, char *into, int dataLen);
  //void  Get(int columnNo, int32 &intolen, unsigned char *into, int dataSize);
  void  Get(int columnNo, int64 &into);
  void  Get(int columnNo, int32 &into);
  void  Get(int columnNo, int16 &into);
  void  Get(int columnNo, double &into);
  void  Get(int columnNo, TJDate into);
  void  Get(int columnNo, TJTime into);
  void  Get(int columnNo, TJDateTime into);
  void  GetNull(int columnNo, int16 &into);
  void  Close();
  void  Clear();
  virtual int32& Sequence(int32 &sequence, const char *sequencer);
  virtual char* UserStamp(char* userStamp, int len);
  virtual TPGDate& Date(TPGDate &pgDate, char *date);
  virtual TPGTime& Time(TPGTime &pgTime, char *time);
  virtual TPGDateTime& DateTime(TPGDateTime &pgDateTime, char *dateTime);
  virtual TPGDateTime& TimeStamp(TPGDateTime &pgTimeStamp, char *timeStamp);
};

JP_EXTERNAL(int) JP_Error(TJConnector &conn, char *Msg, int32 MsgLen);

struct JP_XML_FORMAT
{
  char result[64];
  JP_XML_FORMAT(int32 value)
  {
    sprintf(result, "%d", value);
  }
  JP_XML_FORMAT(int64 value)
  {
    sprintf(result, "%lld", value);
  }
  JP_XML_FORMAT(double value)
  {
    sprintf(result, "%0.15g", value);
  }
};

#endif


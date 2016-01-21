#ifndef ociapiH
#define ociapiH 

#include "ocimachine.h"

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

struct TJOCIDate
{
  unsigned char cc, yy, mm, dd, hh, mi, ss;
  unsigned char filler;
};

struct TJLob
{
  unsigned long size;
  unsigned char* data;
  TJLob()
  {
    size = 0;
    data = 0;
  }
  ~TJLob()
  {
    if (data)
      delete [] data;
  }
  void set(unsigned long aSize, char* aData)
  {
    if (size != aSize)
    {
      delete [] data;
      size = aSize;
      if (size)
      {
        data = new unsigned char [size];
        memcpy(data, aData, size);
      }
    }
  }
  #ifdef _NEDGEN_H_
  void Swaps()
  {
    SwapBytes(size);
  }
  #endif
};

struct TJConnector
{
  long       signature;
  int        result;
  text       msgbuf[2048];
  int        error;
  sb4        ociErrCode;
  char*      user;
  char*      server;
  time_t     serverTime;
  time_t     systemTime;
  OCIEnv*    ociEnv;
  OCIError*  ociError;
  OCISvcCtx* ociSvcCtx;
  int        LoggedOn;
  TJConnector()
  {
    memset(this, 0, sizeof(*this));
    signature = 0xFACE0FF0;
  }
  ~TJConnector()
  {
    if (ociSvcCtx)
      Logoff();
  }
  void Logon(const char* aUser, const char* aPassword, const char* aServer);
  void Logoff();
  void Commit();
  void Rollback();
  void TimeStamp(char *timeStamp);
  void Error(char *Msg, int MsgLen);
  void ThrowOnError(char *file=0, int line=0);
};

struct TJQuery
{
  long        signature;
  int         noBinds;
  int         noDefines;
  int         noLobs;
  long        noRows;
  long        sizeRow;
  long        noIndicators;
  long        rowsIndex;
  long        rowsDone;
  long        rowsRead;
  long        rowsWritten;
  int         doCommit;
  int         isOpen;
  int         error;
  char*       command;
  char*       data;
  short*      indicators;
  char        file[65];
  long        line;
  void FileAndLine(char *aFile, long aLine)
  {
    strncpy(file, aFile, sizeof(file)-1);
    line = aLine;
  }
  TJConnector &conn;
  OCIBind**   ociBinds;
  OCIDefine** ociDefines;
  OCILobLocator** ociLobs;
  OCIStmt*    ociStmt;
  TJQuery(TJConnector &aConnector) : conn(aConnector)
  {
    signature = 0xFACE0FF1;
    noBinds = 0;
    noDefines = 0;
    noLobs = 0;
    noRows = 0;
    sizeRow = 0;
    noIndicators = 0;
    rowsIndex = 0;
    rowsDone = 0;
    rowsRead = 0;
    rowsWritten = 0;
    doCommit = 0;
    isOpen = 0;
    error = 0;
    command = 0;
    data = 0;
    indicators = 0;
    ociBinds = 0;
    ociDefines = 0;
    ociLobs = 0;
    ociStmt = 0;
    file[sizeof(file)-1] = 0; // just in case
  }
  ~TJQuery();
  void  SetCommit(int commit) {doCommit = commit;}
  void  Open(const char* query, int noBinds=0, int noDefines=0, int noLobs=0, int noRows=0, int sizeRow=0);
  void  OpenArray(const char* query, int noBinds, int noRows, int noLobs, int sizeRow);
  void  Bind(const char* name, int bindNo, char *data, int datalen, short *null=0, int ansichar=0);
  void  Bind(const char* name, int bindNo, long &data, short *null=0);
  void  Bind(const char* name, int bindNo, int &data, short *null=0);
  void  Bind(const char* name, int bindNo, short &data, short *null=0);
  void  Bind(const char* name, int bindNo, double &data, short *null=0);
  void  Bind(const char* name, int bindNo, TJOCIDate &data, short *null=0);
  void  Bind(const char* name, int bindNo, OCILobLocator *data, int lobType, short *null=0);
  void  BindArray(const char* name, int bindNo, char *data, int datalen, int ansichar=0);
  void  BindArray(const char* name, int bindNo, long *data);
  void  BindArray(const char* name, int bindNo, int *data);
  void  BindArray(const char* name, int bindNo, short *data);
  void  BindArray(const char* name, int bindNo, double *data);
  void  BindArray(const char* name, int bindNo, TJOCIDate *data);
  void  BindArray(const char* name, int bindNo, OCILobLocator *data, int lobType);
  void  Define(int defineNo, char *data, int dataLen, int ansichar=0);
  void  Define(int defineNo, long *data);
  void  Define(int defineNo, int *data);
  void  Define(int defineNo, short *data);
  void  Define(int defineNo, double *data);
  void  Define(int defineNo, TJOCIDate *data);
  void  Define(int defineNo, OCILobLocator *data, int lobType);
  void  Exec();
  bool  Fetch();
  void  Deliver(int doit);
  void  Get(char *into, char *from, int dataLen);
  void  Get(long &into, char *from);
  void  Get(int &into, char *from);
  void  Get(short &into, char *from);
  void  Get(double &into, char *from);
  void  Get(TJDate into, char *from);
  void  Get(TJTime into, char *from);
  void  Get(TJDateTime into, char *from);
  void  Get(TJLob &into, char *from);
  void  GetNull(short &into, int defineNo);
  void  Put(char *into, char *from, int dataLen);
  void  Put(char *into, long &from);
  void  Put(char *into, int &from);
  void  Put(char *into, short &from);
  void  Put(char *into, double &from);
  void  Put(char *into, TJDate from);
  void  Put(char *into, TJTime from);
  void  Put(char *into, TJDateTime from);
  void  Put(char *into, TJLob &from);
  void  PutNull(short &from, int bindNo);
  void  Close();
  void  Clear();
  virtual int&  Sequence(int &sequence, char *sequencer);
  virtual char* UserStamp(char* userStamp);
  virtual TJOCIDate& Date(TJOCIDate &ocidate, char *date);
  virtual TJOCIDate& Time(TJOCIDate &ocidate, char *time);
  virtual TJOCIDate& DateTime(TJOCIDate &ocidate, char *dateTime);
  virtual TJOCIDate& TimeStamp(TJOCIDate &ocidate, char *timeStamp);
  virtual OCILobLocator* LobLocator(OCILobLocator* lobLocator, TJLob &lobData);
};

JP_EXTERNAL(int) JP_Error(TJConnector &conn, char *Msg, int MsgLen);
struct JP_XML_FORMAT
{
  char result[64];
  JP_XML_FORMAT(long value)
  {
    sprintf(result, "%d", value);
  }
  JP_XML_FORMAT(double value)
  {
    sprintf(result, "%0.15g", value);
  }
};

#endif


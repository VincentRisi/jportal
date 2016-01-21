#ifndef cliapiH
#define cliapiH "$Revision: 412.2 $ $Date: 2004/11/19 14:14:23 $"
#include "machine.h"

#include <math.h>
#include "padgen.h"
#include "climachine.h"

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

template <int32 size>
struct TJBlob
{
	int32 len;
	unsigned char data[size-sizeof(int32)];
	void Swaps()
	{
#ifdef swapbytesH
		SwapBytes(len);
#endif
	}
};

/**
* Helper Class to manage Ubi Impl Addlist allocation and local free.
*/
template <class X>
struct TJAddList
{
	X *recs;
	int32 noOf;
	TJAddList() {recs = 0; noOf = 0;}
	virtual ~TJAddList() {if (recs) free(recs);}
};

class TJAlloc
{
  static bool Failed;
public:
  static bool NoMemory()
  {
    return Failed;
  }
  static char* newChar(int size)
  {
    char* result = new char[size];
    Failed = (result == 0);
    if (result != 0)
      memset(result, 0, size);
    else
    {
      char work[256];
      snprintf(work, sizeof(work), "TJAlloc::newChar(%d) Memory allocation Error", size);
      throw TCliApiException(0xDEADBEEF, "allocation", "char*", work, JP_MARK);
    }
    return result;
  }
  static int32* newInt(int size)
  {
    int32* result = new int32[size];
    Failed = (result == 0);
    if (result != 0)
      memset(result, 0, size);
    else
    {
      char work[256];
      snprintf(work, sizeof(work), "TJAlloc::newInt(%d) Memory allocation Error", size);
      throw TCliApiException(0xDEADBEEF, "allocation", "int32*", work, JP_MARK);
    }
    return result;
  }
  static SQLINTEGER* newSQLINTEGER(int size)
  {
    SQLINTEGER* result = new SQLINTEGER[size];
    Failed = (result == 0);
    if (result != 0)
      memset(result, 0, size*sizeof(SQLINTEGER));
    else
    {
      char work[256];
      snprintf(work, sizeof(work), "TJAlloc::newSQLINTEGER(%ld) Memory allocation Error", size*sizeof(SQLINTEGER));
      throw TCliApiException(0xDEADBEEF, "allocation", "SQLINTEGER*", work, JP_MARK);
    }
    return result;
  }
  static char* dupChar(const char* value)
  {
    char* result = new char[strlen(value)+1];
    if (result != 0)
      strcpy(result, value);
    return result;
  }
  static void deleteChar(char*& data)
  {
    if (data != 0)
    {
      delete [] data;
      data = 0;
    }
  }
  static void deleteInt(int32*& data)
  {
    if (data != 0)
    {
      delete [] data;
      data = 0;
    }
  }
  static void deleteSQLINTEGER(SQLINTEGER*& data)
  {    if (data != 0)
    {
      delete [] data;
      data = 0;
    }
  }
};

struct TJBuffer
{
  char *data;
  TJBuffer(int32 size)
  {
    data = TJAlloc::newChar(size);
  }
  virtual ~TJBuffer()
  {
    TJAlloc::deleteChar(data);
  }
};

/** 
 * This is basically used in object based system and is not designed
 * to be subclassed. Therefore no virtual function or a dynamic virtual
 * method table. It does have static methods however.
 */
struct TJConnector
{
	int32       signature;
	SQLRETURN   result;
	SQLSMALLINT diagType;
	SQLHANDLE   diagHandle;
	char        state[256];
	char        msgbuf[8192];
	char        allow[64];
	int32       error;
	char*       user;
	char*       password;
	char*       server;
	char*       schema;
        double      serverTime;
        double      systemTime;
	SQLHANDLE   cliEnv;
	SQLHANDLE   cliDbc;
	int32       loggedOn;
	char*       command;
  bool        copyOf;
  bool        ownerOfEnv;
  bool        forkable;
  bool        coordinated;
  
  TJConnector(bool forkable = false)
  {
    memset(this, 0, sizeof(*this));
    this->forkable = forkable;
  }
  
  static TJConnector* Coordinated(bool forkable = false) {TJConnector* conn=new TJConnector(forkable); conn->coordinated = true; return conn; }
  
	~TJConnector()
	{
    if (copyOf == true)
      return;
		if (cliDbc)
			Logoff();
		TJAlloc::deleteChar(server);
		TJAlloc::deleteChar(schema);
		TJAlloc::deleteChar(user);
		TJAlloc::deleteChar(password);
	}
	void ForDiagnosis(SQLSMALLINT aDiagType, SQLHANDLE aDiagHandle=SQL_NULL_HANDLE)
	{
		diagType = aDiagType;
		diagHandle = aDiagHandle;
	}
	void Logon(const char* aUser, const char* aPassword, const char* aServer, const char* aSchema=0, const SQLHANDLE* aCliEnv=0);
	void Logon();
	void Logoff();
	void Commit();
	void Rollback();
  void Sync();
  void SetCoordinated() { coordinated = true; }
  void SetForkable()    { forkable = true; }
  void TimeStamp(char *timeStamp, int &fraction);
	void Error(char *Msg, int32 MsgLen);
	void ThrowOnError(char *file=0, int32 line=0);
	void ThrowOnError(SQLRETURN value, char *file=0, int32 line=0)
	{
		if (!Allow(value)) ThrowOnError(file, line);
	}
	bool Allow(SQLRETURN value);
  void CopyOf(TJConnector &basis)
  {
    signature = basis.signature;
    cliEnv = basis.cliEnv;
    cliDbc = basis.cliDbc;
    diagType = basis.diagType;
    diagHandle = basis.diagHandle;
    serverTime = basis.serverTime;
    systemTime = basis.systemTime;
    server = basis.server;
    schema = basis.schema;
    user = basis.user;
    password = basis.password;
    loggedOn = basis.loggedOn;
    ownerOfEnv = basis.ownerOfEnv;
    forkable = basis.forkable;
    coordinated = basis.coordinated;
    copyOf = true;
  }
private:
  //Made private, because you should use the CopyOf member instead
  TJConnector(const TJConnector& orig) {}
};

struct TJQuery
{
	int32         signature;
	int32         noBinds;
	int32         noDefines;
	int32         noRows;
	int32         sizeRow;
	int32         noIndicators;
	SQLUINTEGER   rowsIndex;
	SQLUINTEGER   rowsDone;
	SQLUINTEGER   rowsRead;
	SQLUINTEGER   totalRowsRead;
	int32         rowsWritten;
	int32         doCommit;
	int32         isOpen;
	int32         error;
	char*         command;
	char*         data;
  char*         extra;
  int32*        offsets;
	SQLINTEGER*   indicators;
	SQLUSMALLINT* rowStatus;
	char          file[65];
	int32         line;
	void FileAndLine(char *aFile, int32 aLine)
	{
		strncpy(file, aFile, sizeof(file)-1);
		line = aLine;
	}
	TJConnector &conn;
	SQLHANDLE   cliStmt;
	TJQuery(TJConnector &aConnector) : conn(aConnector)
	{
		signature = 0x1DEFACED;
		noBinds = 0;
		noDefines = 0;
		noRows = 0;
		sizeRow = 0;
		noIndicators = 0;
		rowsIndex = 0;
		rowsDone = 0;
		rowsRead = 0;
		totalRowsRead = 0;
		rowsWritten = 0;
		doCommit = 0;
		isOpen = 0;
		error = 0;
		command = 0;
		data = 0;
    extra = 0;
    offsets = 0;
		indicators = 0;
		cliStmt = SQL_NULL_HANDLE;
		file[sizeof(file)-1] = 0;
	}
	~TJQuery();
	void  SetCommit(int32 commit) {doCommit = commit;}
	void  Open(const char* query, int32 noBinds=0, int32 noDefines=0, int32 noRows=0, int32 sizeRow=0);
	void  OpenArray(const char* query, int32 noBinds, int32 noNulls, int32 noRows, int32 sizeRow);

	void  Bind(int32 bindNo, char *data, int32 datalen, int16 direction=SQL_PARAM_INPUT, int32 typeChar=0, int32 nullChar=0);
	void  BindBlob(int32 bindNo, char *data, int32 dataSize, int16 direction=SQL_PARAM_INPUT);
	void  Bind(int32 bindNo, int64 &data, int16  direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, int32 &data, int16  direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, int16 &data, int16  direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, double &data, int16 precision, int16 scale, int16 direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, char *data, int16 precision, int16 scale, int16 direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, DATE_STRUCT &data, int16 direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, TIME_STRUCT &data, int16 direction=SQL_PARAM_INPUT, int16 *null=0);
	void  Bind(int32 bindNo, TIMESTAMP_STRUCT &data, int16 direction=SQL_PARAM_INPUT, int16 *null=0);

	void  BindCharArray(int32 bindNo, char *data, int dataLen, SQLINTEGER* null=0);
	void  BindAnsiCharArray(int32 bindNo, char *data, int dataLen, SQLINTEGER* null=0);
	void  BindInt64Array(int32 bindNo, int64 *data, SQLINTEGER* null=0);
	void  BindInt32Array(int32 bindNo, int32 *data, SQLINTEGER* null=0);
	void  BindInt16Array(int32 bindNo, int16 *data, SQLINTEGER* null=0);
	void  BindDoubleArray(int32 bindNo, double *data, int16 precision, int16 scale, SQLINTEGER* null=0);
	void  BindMoneyArray(int32 bindNo, char *data, int16 precision, int16 scale, SQLINTEGER* null=0);
	void  BindDateArray(int32 bindNo, DATE_STRUCT *data, SQLINTEGER* null=0);
	void  BindTimeArray(int32 bindNo, TIME_STRUCT *data, SQLINTEGER* null=0);
	void  BindDateTimeArray(int32 bindNo, TIMESTAMP_STRUCT *data, SQLINTEGER* null=0);

	void  Define(int32 defineNo, char *data, int32 dataLen, int32 typeChar=0);
	void  DefineBlob(int32 defineNo, char *data, int32 dataSize);
	void  Define(int32 defineNo, int64 *data);
	void  Define(int32 defineNo, int32 *data);
	void  Define(int32 defineNo, int16 *data);
	void  Define(int32 defineNo, double *data);
	void  Define(int32 defineNo, char *data, int16 precision);
	void  Define(int32 defineNo, DATE_STRUCT *data);
	void  Define(int32 defineNo, TIME_STRUCT *data);
	void  Define(int32 defineNo, TIMESTAMP_STRUCT *data);

	void  Exec();
	bool  Fetch();

	void  Get(char *into, char *from, int32 dataLen);
	void  Get(int32 &intolen, unsigned char *into, char *from, int32 dataSize);
	void  Get(int64 &into, char *from);
	void  Get(int32 &into, char *from);
	void  Get(int16 &into, char *from);
	void  Get(double &into, char *from);
	void  Get(TJDate into, char *from);
	void  Get(TJTime into, char *from);
	void  Get(TJDateTime into, char *from);

	void  GetNull(int16 &into, int32 defineNo);

	void  Close();
	void  Clear();
	virtual int32&  Sequence(int32 &sequence, char *sequencer);
	virtual int64&  Sequence(int64 &sequence, char *sequencer);
	virtual char* UserStamp(char* userStamp);
	virtual DATE_STRUCT& Date(DATE_STRUCT &cliDate, char *date);
	virtual TIME_STRUCT& Time(TIME_STRUCT &cliTime, char *time);
	virtual TIMESTAMP_STRUCT& DateTime(TIMESTAMP_STRUCT &cliDateTime, char *dateTime);
	virtual TIMESTAMP_STRUCT& TimeStamp(TIMESTAMP_STRUCT &cliTimeStamp, char *timeStamp);
};

JP_EXTERNAL(SQLRETURN) JP_Error(TJConnector &conn, char *Msg, int32 MsgLen);

struct JP_XML_FORMAT
{
	char result[64];
	JP_XML_FORMAT(int32 value)
	{
		sprintf(result, "%d", value);
	}
	JP_XML_FORMAT(int64 value)
	{
		sprintf(result, "%ld", value);
	}
	JP_XML_FORMAT(double value)
	{
		sprintf(result, "%0.15g", value);
	}
};

struct TJLine
{
  bool isVar;
  char* line;
  TJLine() {memset(this, 0, sizeof(*this));}
  virtual ~TJLine();
  char* load(char* input);
};

struct TJField
{
  static const int
    TJ_BLOB       = 1
  , TJ_BOOLEAN    = 2
  , TJ_BYTE       = 3
  , TJ_CHAR       = 4
  , TJ_DATE       = 5
  , TJ_DATETIME   = 6
  , TJ_DOUBLE     = 7
  , TJ_DYNAMIC_   = 8
  , TJ_FLOAT      = 9
  , TJ_IDENTITY   = 10
  , TJ_INT        = 11
  , TJ_LONG       = 12
  , TJ_MONEY      = 13
  , TJ_SEQUENCE   = 14
  , TJ_SHORT      = 15
  , TJ_STATUS     = 16
  , TJ_TIME       = 17
  , TJ_TIMESTAMP  = 18
  , TJ_TLOB       = 19
  , TJ_USERSTAMP  = 20
  , TJ_ANSICHAR   = 21
  , TJ_UID        = 22
  , TJ_XML        = 23
  ;
  char *name;
  char *alias;
  int type;
  int length;
  int precision;
  int scale;
  int offset;
  int used;
  bool isPrimaryKey;
  bool isSequence;
  bool isNull;
  bool isIn;
  bool isOut;
  TJField() {memset(this, 0, sizeof(*this));}
  virtual ~TJField();
  char* load(char* input);
};

struct TJDynamic
{
  char *name;
  int length;
  int offset;
  TJDynamic() {memset(this, 0, sizeof(*this));}
  virtual ~TJDynamic();
  char* load(char* input);
};

struct TJProc
{
  char *table;
  char *name;
  int noRows; 
  bool isProc;
  bool isSProc;
  bool isData;
  bool isIdlCode;
  bool isSql;
  bool isSingle;
  bool isAction;
  bool isStd;
  bool useStd;
  bool extendsStd;
  bool useKey;
  bool hasImage;
  bool isMultipleInput;
  bool isInsert;
  bool hasReturning;
  int noPairs; // input or bind positions
  int noInputs;
  int noOutputs;
  int noDynamics;
  int noLines;
  TJLine* lines;
  int *pairs;  // allows for duplicates
  TJField* inputs;
  TJField* outputs;
  TJDynamic* dynamics;
  TJProc() {memset(this, 0, sizeof(*this));}
  virtual ~TJProc();
  char* load(char* input);
};

struct TJTable
{
  char* name;
  int noProcs;
  TJProc* procs;
  TJTable() {memset(this, 0, sizeof(*this));}
  virtual ~TJTable();
  char* load(char* input);
};

#endif


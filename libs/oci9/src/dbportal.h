#ifndef _DBPORTAL_H_
#define _DBPORTAL_H_ 
#include "machine.h"

#include "sqlapi.h"
#include "ti.h"
#include "hugechar.h"

#define xDBErrorName "xDBError"
XCLASSDEF(DBError);
class xDBError : public xCept
{
  int No;
  int DBNo;
public:
  enum eError
  { errDBOK
  , errDBLogon
  , errDBLogoff
  , errDBRunProc
  , errDBCommit
  , errDBRollback
  , errDBOpen
  , errDBClose
  , errDBExec
  , errDBFetch
  , errDBFetchMany
  , errDBCancel
  , errDBSetNull
  , errDBTestNull
  , errDBCipher
  };
  xDBError(pchar aFname, int aLine, eError aError, int aNo, int aDBNo,
           pchar aMsg1, pchar aMsg2=0, pchar aMsg3=0);
  xDBError(const xDBError& aX) : xCept(aX), No(No = aX.No), DBNo(DBNo = aX.DBNo) {}
  int DBErrorNo() {return DBNo;}
  bool Duplicate() {return DBNo == 1 ? true : false;}     //
  bool NotFound() {return (DBNo == OCI_NO_DATA || DBNo == 1403) ? true : false;}   //
};

#define XDBError(err, no, dbno, msg1) \
        xDBError(__FILE__, __LINE__, xDBError::err, no, dbno, msg1)
#define XDBError1(err, no, dbno, msg1, msg2) \
        xDBError(__FILE__, __LINE__, xDBError::err, no, dbno, msg1, msg2)
#define XDBError2(err, no, dbno, msg1, msg2, msg3) \
        xDBError(__FILE__, __LINE__, xDBError::err, no, dbno, msg1, msg2, msg3)

CLASSDEF(DBConnect);
class tDBConnect
{
protected:
  friend class tDBQuery;
  pchar ErrorMsg();
public:
  static tDBConnect *connect;
  static tDBConnect *singleton(const char *aBinFile, const char *aLogonString);
  pSqlCB SqlCB;
  tDBConnect();
  ~tDBConnect();
  pSqlCB CB() { return SqlCB; }
  void SetTimeout(int seconds) {SqlCB->timeout = seconds;}
  void Commit();
  void Logoff();
  void Logon(char *aBinFile, char *aLogonString);
  bool ReadOne(pchar aQuery, void* aData);
  void Rollback();
  void RunProc(pchar aQuery, void* aData=0);
  bool Duplicate(void) {return SqlCB->ociErrCode == 1 ? true : false;}
  bool NotFound(void) {return (SqlCB->RC == OCI_NO_DATA||SqlCB->RC == 1403) ? true : false;}
  pchar Cipher(pchar Input, ushort InpLen,
               pchar Output, ushort OutLen,
               ushort Seed);
  // ctor     - Sets up an instance ready for Logon
  // dtor     - Will run Logoff if not already done
  // CB       - Returns the SQLAPI control block
  // Commit   - commits a transaction or unit of work
  // Logoff   - Logs off from the database and frees the Queries dictionary
  // Logon    - Logs onto the database and opens the Queries dictionary.
  // ReadOne  - Runs a single fetch query and returns false in no data
  //            found.
  // RollBack - Rollsback a unit of work
  // RunProc  - Runs a single query or stored procedure
  // Cipher   - Creates a one way cipher code value for password generation
};

CLASSDEF(DBQuery);
class tDBQuery
{
protected:
  pSqlCursor SqlCursor;
  pchar ErrorMsg();
public:
  tDBQuery(tDBConnect& aDBConnect, pchar aQuery, void* aData=0);
  ~tDBQuery();
  void Cancel();
  void Close();
  void Exec();
  bool Fetch();
  bool FetchMany(pushort aNo, void* aData);
  void SetNull(pchar aColumn, bool aHow = true);
  bool TestNull(pchar aColumn);
  bool Duplicate(void) {return SqlCursor->ociErrCode == 1 ? true : false;}
  bool NotFound(void) {return (SqlCursor->RC == OCI_NO_DATA||SqlCursor->RC == 1403) ? true : false;}
  // ctor      - establishes a cursor for a query
  // dtor      - closes the cursor if it is still open
  // Cancel    - kills the query if it is still in progress
  // Close     - closes the cursor explicity
  // Exec      - starts the query (returns data if single or stored proc)
  // Fetch     - fetches the next data record returns false if no more data.
  // FetchMany - fetches a block of data returns false if no more data.
  // SetNull   - sets a column to NULL for inserting (used on numerics usu.)
  // TestNull  - test if a column returned NULL (used on numerics usu.)
};

#endif


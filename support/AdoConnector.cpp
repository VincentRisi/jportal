/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

#include "adoconnector.h"

struct InitOle
{

  InitOle()
  {
    ::CoInitialize(NULL);
  }
  ~InitOle() { ::CoUninitialize();}

} _init_InitOle_;

TConnector::TConnector()
{
  memset(this, 0, sizeof(*this));
  pIsLoggedOn = false;
  pInTran=false;

  HRESULT rc = _conn.CreateInstance(__uuidof(Connection));
  if (rc != S_OK)
    throw TDBException(rc, "TConnector CoCreateInstance Failure");
}

void TConnector::Logon(char *aServer, char *aUser, char *aPassword, long TimeOut)
{
  _conn->ConnectionTimeout = TimeOut;
  strncpy(_User, aUser?aUser:"", sizeof(_User) - 1/*/sizeof(char)*/);
  HRESULT rc = _conn->Open(aServer, aUser?aUser:"", aPassword?aPassword:"", 0);
  if (rc != S_OK)
    throw TDBException(rc, "TConnector Logon Failure");
  pIsLoggedOn = true;
}

void TConnector::Logoff()
{
  if (_conn->State & adStateOpen)
  {
    HRESULT rc = _conn->Close();
    if (rc != S_OK)
      throw TDBException(rc, "TConnector Logoff Failure");
  }
  pIsLoggedOn = false;
}


int TConnector::D18991230 = TConnector::JulianDate(1899, 12, 30); // day 0 of double date;

int TConnector::GetSequence(char * aName)
{
  int result = 0;
  if (_Sequence != 0)
    _Sequence(*this, aName, result);
  return result;
}

double TConnector::GetTimeStamp()
{
  double result = 0.0;
  if (_TimeStamp != 0)
    _TimeStamp(*this, result);
  return result;
}

char *TConnector::GetUserStamp(char *aValue)
{
  strcpy(aValue, _User);
  if (_UserStamp != 0)
    _UserStamp(*this, aValue);
  return aValue;
}

int TConnector::JulianDate(int year, int month, int day)
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
  return((146097 * century) / 4
         + (1461 * cy) / 4
         + (153 * month + 2) / 5
         + day
         + 1721119);
}

void TConnector::CalendarDate(int date, int& year, int& month, int& day)
{
  int temp = date - 1721119;
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

void TConnector::CalendarDate(int jul, char* YYYYMMDD)
{
  int day, month, year;
  CalendarDate(jul, year, month, day);
  sprintf(YYYYMMDD, "%04.4d%02.2d%02.2d", year, month, day);
}

void TConnector::DateAsYYYYMMDD(double date, char* result)
{
  int JDate = (int) date + D18991230; // jul date
  CalendarDate(JDate, result);
}

void TConnector::TimeAsHHMMSS(double time, char* HHMMSS)
{
  double f,i;
  f = modf(time, &i);
  int secs = (int)(f * (24 * 60 * 60));
  int hour = secs / (60*60);
  secs = secs % (60*60);
  int min = secs / 60;
  secs = secs % 60;
  sprintf(HHMMSS, "%02.2d%02.2d%02.2d", hour, min, secs);
}

double TConnector::YYYYMMDDAsDate(char* YYYYMMDD)
{
  int day, month, year;
  char work[5];
  strncpy(work, YYYYMMDD, 4);   work[4] = 0; year  = atoi(work);
  strncpy(work, YYYYMMDD+4, 2); work[2] = 0; month = atoi(work);
  strncpy(work, YYYYMMDD+6, 2); work[2] = 0; day   = atoi(work);
  return(double)(JulianDate(year, month, day) - D18991230);
}

double TConnector::HHMMSSAsTime(char* HHMMSS)
{
  int secs, mins, hours;
  char work[5];
  strncpy(work, HHMMSS, 2);     work[2] = 0; hours = atoi(work);
  strncpy(work, HHMMSS+2, 2);   work[2] = 0; mins  = atoi(work);
  strncpy(work, HHMMSS+4, 2);   work[2] = 0; secs  = atoi(work);
  return(hours*60.0*60.0+mins*60.0+secs) / (24.0 * 60.0 * 60.0);
}

TQuery::TQuery()
{
  memset(this, 0, sizeof(*this));
  HRESULT rc = _cmd.CreateInstance(__uuidof(Command));
  if (rc != S_OK)
    throw TDBException(rc, "TQuery command CoCreateInstance Failure");
}

void TQuery::Open(TConnector &aConn, char * command)
{
  try
  {
    conn = &aConn;
    _cmd->ActiveConnection = conn->_conn;
    _cmd->CommandText = command;
    _cmd->CommandType = adCmdText;
  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Open Failure");
  }
}

void TQuery::Close()
{
  try
  {
    if (_rset->State == adStateOpen)
      _rset->Close();

    _rset->Release();

  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Close Failure");
  }
}

void TQuery::Exec()
{
  try
  {
    _rset = _cmd->Execute(0,0,adCmdText);
  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Exec Failure");
  }
}

void TQuery::GetNull(short &isNull, int index)
{
  _variant_t I;
  I.Clear();
  I = (long)index;
  FieldPtr f = _rset->Fields->GetItem(I);

  _variant_t v;
  v.Clear();
  v = f->Value;

  isNull = (v.vt == VT_NULL) ? 0 : 1;
}



// the sucky indentation is due to the fact that i copied it from sucky msdn
static BOOL GetBinaryFromVariant(VARIANT & ovData, BYTE * ppBuf,
                                 unsigned long * pcBufLen,unsigned long reallen)
{
  BOOL fRetVal = FALSE;

  //Binary data is stored in the variant as an array of unsigned char
  if (ovData.vt == (VT_ARRAY|VT_UI1))  // (OLE SAFEARRAY)
  {
    //Retrieve size of array
    *pcBufLen = ovData.parray->rgsabound[0].cElements;

    if (*pcBufLen > reallen && reallen) *pcBufLen = reallen;

    {
      void * pArrayData;

      //Obtain safe pointer to the array
      SafeArrayAccessData(ovData.parray,&pArrayData);

      //Copy the bitmap into our buffer
      memcpy(ppBuf, pArrayData, *pcBufLen);

      //Unlock the variant data
      SafeArrayUnaccessData(ovData.parray);
      fRetVal = TRUE;
    }
  }
  return fRetVal;
}

void TQuery::GetLob(void *field, int *sizeout, int size, int index)
{
  try
  {
    _variant_t I;
    I.Clear();
    I = (long)index;
    FieldPtr f = _rset->Fields->GetItem(I);
    _variant_t v;
    v.Clear();
    v = f->Value;
    GetBinaryFromVariant(v,(unsigned char *)field,(unsigned long*)sizeout,(unsigned long)size);
  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Get Failure");
  }

}


void TQuery::Get(void *field, int size, int index, TDataType type)
{
  try
  {
    _variant_t I;
    I.Clear();
    I = (long)index;
    FieldPtr f = _rset->Fields->GetItem(I);
    _variant_t v;
    v.Clear();
    v = f->Value;
    switch (type)
    {
    case BOOLEAN:
    case BYTE:
    case SHORT:
      {
        *(short *) field = (short) v.iVal;
        break;
      }
    case INT:
    case IDENTITY:
    case SEQUENCE:
      {
        *(int *) field = v.iVal;
        break;
      }
    case LONG:
      {
        *(long *) field = v.lVal;
        break;
      }
    case ANSICHAR:
    case CHAR:
    case USERSTAMP:
      {
        _bstr_t b(v.bstrVal);
        strncpy((char*) field, b, size);
        break;
      }
    case BLOB:
    case TLOB:  // Rather use GetLob(...) than Get(...)
      {
        unsigned long sz;
        GetBinaryFromVariant(v,(unsigned char *)field,&sz,0);
        break;
      }
    case FLOAT:
    case DOUBLE:
    case MONEY:
      {
        *(double *) field = v.dblVal;
        break;
      }
    case DATE:
      {
        TConnector::DateAsYYYYMMDD(v.date, (char*)field);
        break;
      }
    case TIME:
      {
        TConnector::TimeAsHHMMSS(v.date, (char*)field);
        break;
      }
    case DATETIME:
    case TIMESTAMP:
      {
        TConnector::DateAsYYYYMMDD(v.date, (char*)field);
        TConnector::TimeAsHHMMSS(v.date, ((char*)field)+8);
        break;
      }
    }
  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Get Failure");
  }
}


static BOOL PutBinaryIntoVariant(VARIANT &var, BYTE * pBuf,
                                 unsigned long cBufLen)
{
  BOOL fRetVal = FALSE;

  //VARIANT var;
  VariantInit(&var);  //Initialize our variant

  //Set the type to an array of unsigned chars (OLE SAFEARRAY)
  var.vt = VT_ARRAY | VT_UI1;

  //Set up the bounds structure
  SAFEARRAYBOUND  rgsabound[1];

  rgsabound[0].cElements = cBufLen;
  rgsabound[0].lLbound = 0;

  //Create an OLE SAFEARRAY
  var.parray = SafeArrayCreate(VT_UI1,1,rgsabound);

  if (var.parray != NULL)
  {
    void * pArrayData = NULL;

    //Get a safe pointer to the array
    SafeArrayAccessData(var.parray,&pArrayData);

    //Copy bitmap to it
    memcpy(pArrayData, pBuf, cBufLen);

    //Unlock the variant data
    SafeArrayUnaccessData(var.parray);

    fRetVal = TRUE;
  }

  return fRetVal;
}


void TQuery::Put(char * name, void *field, int size, TDataType type, short isNull)
{
  try
  {
    _ParameterPtr p;
    _variant_t v;
    _bstr_t b(name);
    v.Clear();
    DataTypeEnum dataType;
    switch (type)
    {
    case BOOLEAN:
    case BYTE:
    case SHORT:
      {
        dataType = adSmallInt;
        v = *(short*)field;
        break;
      }
    case IDENTITY:
    case SEQUENCE:
    case INT:
      {
        dataType = adInteger;
        v = *(long*)field;
        break;
      }
    case LONG:
      {
        dataType = adBigInt;
        v = *(long*)field;
        break;
      }
    case ANSICHAR:
    case CHAR:
    case USERSTAMP:
      {

        dataType = adVarChar; //adChar;
        v = (char*)field;
        break;
      }
    case BLOB:
    case TLOB:
      {
        dataType = adBinary; // adVarBinary?
        PutBinaryIntoVariant(v,(unsigned char*)field,size);
        break;
      }
    case FLOAT:
    case DOUBLE:
    case MONEY:
      {
        dataType = adDouble;
        v = *(double*)field;
        break;
      }
    case DATE:
      {
        dataType = adDBTimeStamp;//adDate;
        v = (char*)field;
        break;
      }
    case TIME:
      {
        dataType = adDate;
        v = TConnector::HHMMSSAsTime((char*)field);
        break;
      }
    case DATETIME:
    case TIMESTAMP:
      {
        dataType = adDBTimeStamp; //adDate;
        /*v.date = TConnector::YYYYMMDDAsDate((char*)field)
               + TConnector::HHMMSSAsTime(((char*)field)+8);
         */
        v = (char*)field;
        break;

      }
    }
    p = _cmd->CreateParameter(b, dataType, adParamInput, size, vtMissing);
    _cmd->Parameters->Append(p);
    p->Value = v;
  }
  catch (_com_error &e)
  {
    throw TDBException(e, "TQuery Put Failure");
  }
}

bool TQuery::EndOfFile()
{
  VARIANT_BOOL b;
  b = _rset->EndOfFile;
  return b == VARIANT_TRUE;
}

bool TQuery::BeginOfFile()
{
  VARIANT_BOOL b;
  b = _rset->BOF;
  return b == VARIANT_TRUE;
}

void TQuery::Next()
{
  _rset->MoveNext();
}

void TQuery::MoveFirst()
{
  _rset->MoveFirst();
}

struct TError
{
  HRESULT ErrorNo;
  char *ErrorDesc;
} ErrorList[16] =
{ {0x800A0BB9, "adErrInvalidArgument     3001"
    " The application is using arguments that are of the wrong type,"
    " are out of acceptable range, or are in conflict with one another."
  }
  , {0x800A0BCD, "adErrNoCurrentRecord     3021"
      " Either BOF or EOF is True, or the current record has been deleted;"
      " the operation requested by the application requires a current record."
  }
  , {0x800A0C93, "adErrIllegalOperation    3219"
      " The operation requested by the application is not allowed in this context."
  }
  , {0x800A0CAE, "adErrInTransaction       3246"
      " The application cannot explicitly close a Connection object while in the"
      " middle of a transaction."
  }
  , {0x800A0CB3, "adErrFeatureNotAvailable 3251"
      " The operation requested by the application is not supported by the provider."
  }
  , {0x800A0CC1, "adErrItemNotFound        3265"
      " ADO could not find the object in the collection corresponding to the name"
      " or ordinal reference requested by the application."
  }
  , {0x800A0D27, "adErrObjectInCollection  3367"
      " Can't append. The object is already in the collection."
  }
  , {0x800A0D5C, "adErrObjectNotSet        3420"
      " The object referenced by the application no longer points to a valid object."
  }
  , {0x800A0D5D, "adErrDataConversion      3421"
      " The application is using a value of the wrong type for the current operation."
  }
  , {0x800A0E78, "adErrObjectClosed        3704"
      " The operation requested by the application is not allowed if the"
      " object is closed."
  }
  , {0x800A0E79, "adErrObjectOpen          3705"
      " The operation requested by the application is not allowed if the"
      " object is open."
  }
  , {0x800A0E7A, "adErrProviderNotFound    3706"
      " ADO could not find the specified provider."
  }
  , {0x800A0E7B, "adErrBoundToCommand      3707"
      " The application cannot change the ActiveConnection property of a"
      " Recordset object with a Command object as its source."
  }
  , {0x800A0E7C, "adErrInvalidParamInfo    3708"
      " The application has improperly defined a Parameter object."
  }
  , {0x800A0E7D, "adErrInvalidConnection   3709"
      " The application requested an operation on an object with a reference"
      " to a closed or invalid Connection object."
  }
  , {0x800A0E98, "adErrUnavailable         3736"
      "Operation failed to complete and the status is unavailable."
      " The field may be unavailable or the operation was not attempted."
  }
};

void TDBException::SetErrorDesc(HRESULT aErrorNo)
{
  for (int i=0; i<16; i++)
  {
    if (aErrorNo == ErrorList[i].ErrorNo)
    {
      strncpy(ErrorDesc, ErrorList[i].ErrorDesc, sizeof(ErrorDesc)-1);
      return;
    }
  }
  strcpy(ErrorDesc, "Unknown Error");
}




#include "ocinumber.h"

const char *ODECIMAL_DEFAULT_FORMAT = "99999999999999999999D999999";
OCIEnv* ODecimal::ociEnv = 0;
OCIError* ODecimal::ociError = 0;

void ODecimal::setResult(int &result, int value, char *file, int line)
{
  result = value;
  if (result != 0)
  {
    char msgbuf[2048];
    sb4 ociErrCode;
    if (ociError)
    {
      OCIErrorGet(ociError,
                  1,
                  0,
                  &ociErrCode,
                  (OraText*)msgbuf,
                  sizeof(msgbuf),
                  OCI_HTYPE_ERROR);
    }
    throw TOciApiException(result, (char*)msgbuf, file, line);
  }
}

void ODecimal::setResult(int value, char *file, int line)
{
  setResult(result, value, file, line);
}

ODecimal::~ODecimal()
{
  if (numberFormat != 0) delete [] numberFormat;
  if (text != 0) delete [] text;
}

char* ODecimal::setFormat(const char* format) 
{
  if (numberFormat != 0) delete [] numberFormat;
  numberFormat = new char [strlen(format)+1];
  Textformat = numberFormat;
  strcpy(numberFormat, format);
  return numberFormat;
}

char* ODecimal::setFormat(int digit, int decimal) 
{
  if (numberFormat != 0) delete [] numberFormat;
  numberFormat = new char [digit + decimal + 2];
  Textformat = numberFormat;
  memset(numberFormat, '9', digit);
  numberFormat[digit] = 'D';
  memset(numberFormat+digit+1, '9', decimal);
  numberFormat[digit+decimal+1] = 0;
  return numberFormat;
}

void ODecimal::Setup()
{
  text = 0;
  if (ociEnv == 0 && ociError == 0)
  {
    int result = OCIInitialize(OCI_OBJECT, 0, 0, 0, 0);
    if (result == 0)
      result = OCIEnvInit(&ociEnv, OCI_DEFAULT, 0, 0);
    if (result == 0)
      result = OCIHandleAlloc(ociEnv, (void **)&ociError, OCI_HTYPE_ERROR, 0, 0);
  }
  numberFormat = new char [strlen(ODECIMAL_DEFAULT_FORMAT)+1];
  strcpy(numberFormat, ODECIMAL_DEFAULT_FORMAT);
  Textformat = numberFormat;
}

// sword OCINumberFromText ( OCIError *err,
// CONST text *str,
// ub4 str_length,
// CONST text *format,
// ub4 fmt_length,
// CONST text *nls_params,
// ub4 nls_p_length,
// OCINumber *number );
ODecimal::ODecimal(const char *str)
{
  Setup();
  setResult(OCINumberFromText(ociError, (const oratext*)str, strlen(str), (const oratext*)numberFormat, strlen(numberFormat), 0, 0, &number), JP_MARK);
}

// sword OCINumberFromInt ( OCIError *err,
// CONST dvoid *inum,
// uword inum_length,
// uword inum_s_flag,
// OCINumber *number );
ODecimal::ODecimal()
{
  Setup();
  OCINumberSetZero(ociError, &number);
  result = 0;
}

// sword OCINumberAssign ( OCIError *err,
// CONST OCINumber *from,
// OCINumber *to );
ODecimal::ODecimal(ODecimal &input)
{
  Setup();
  numberFormat = input.numberFormat;
  setResult(OCINumberAssign(ociError, &input.number, &number), JP_MARK);
}

ODecimal::ODecimal(int input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_SIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(unsigned int input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_UNSIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(long input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_SIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(unsigned long input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_UNSIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(short input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_SIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(unsigned short input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_UNSIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(char input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_SIGNED, &number), JP_MARK);
}

ODecimal::ODecimal(unsigned char input)
{
  Setup();
  setResult(OCINumberFromInt(ociError, &input, sizeof(input), OCI_NUMBER_UNSIGNED, &number), JP_MARK);
}

// sword OCINumberFromReal ( OCIError *err,
// CONST dvoid *rnum,
// uword rnum_length,
// OCINumber *number );
ODecimal::ODecimal(float input)
{
  Setup();
  setResult(OCINumberFromReal(ociError, &input, sizeof(input), &number), JP_MARK);
}

ODecimal::ODecimal(double input)
{
  Setup();
  setResult(OCINumberFromReal(ociError, &input, sizeof(input), &number), JP_MARK);
}

ODecimal::ODecimal(long double input)
{
  Setup();
  setResult(OCINumberFromReal(ociError, &input, sizeof(input), &number), JP_MARK);
}

// sword OCINumberAbs ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::abs()
{
  ODecimal target;
  setResult(target.result, OCINumberAbs(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberCos ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::cos()
{
  ODecimal target;
  setResult(target.result, OCINumberCos(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberSin ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::sin()
{
  ODecimal target;
  setResult(target.result, OCINumberSin(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberTan ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::tan()
{
  ODecimal target;
  setResult(target.result, OCINumberTan(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberHypCos ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::hypCos()
{
  ODecimal target;
  setResult(target.result, OCINumberHypCos(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberHypSin ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::hypSin()
{
  ODecimal target;
  setResult(target.result, OCINumberHypSin(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberHypTan ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::hypTan()
{
  ODecimal target;
  setResult(target.result, OCINumberHypTan(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberArcCos ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::arcCos()
{
  ODecimal target;
  setResult(target.result, OCINumberArcCos(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberArcSin ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::arcSin()
{
  ODecimal target;
  setResult(target.result, OCINumberArcSin(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberArcTan ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::arcTan()
{
  ODecimal target;
  setResult(target.result, OCINumberArcTan(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberArcTan2 ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::arcTan2(ODecimal& second)
{
  ODecimal target;
  setResult(target.result, OCINumberArcTan2(ociError, &number, &second.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberCeil( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::ceil()
{
  ODecimal target;
  setResult(target.result, OCINumberCeil(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberFloor( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::floor()
{
  ODecimal target;
  setResult(target.result, OCINumberFloor(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberExp ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::exp()
{
  ODecimal target;
  setResult(target.result, OCINumberExp(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberLn ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::ln()
{
  ODecimal target;
  setResult(target.result, OCINumberLn(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberNeg ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::neg()
{
  ODecimal target;
  setResult(target.result, OCINumberNeg(ociError, &number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberSqrt ( OCIError *err,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::sqrt()
{
  ODecimal target;
  setResult(target.result, OCINumberSqrt(ociError, &number, &target.number), JP_MARK);
  return target;
}

char ODecimal::getChar()
{
  char value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_SIGNED, &value), JP_MARK);
  return value;
}

short ODecimal::getShort()
{
  short value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_SIGNED, &value), JP_MARK);
  return value;
}

int ODecimal::getInt()
{
  int value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_SIGNED, &value), JP_MARK);
  return value;
}

long ODecimal::getLong()
{
  long value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_SIGNED, &value), JP_MARK);
  return value;
}

unsigned char ODecimal::getUChar()
{
  unsigned char value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_UNSIGNED, &value), JP_MARK);
  return value;
}

unsigned int ODecimal::getUInt()
{
  unsigned int value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_UNSIGNED, &value), JP_MARK);
  return value;
}

unsigned long ODecimal::getULong()
{
  unsigned long value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_UNSIGNED, &value), JP_MARK);
  return value;
}

unsigned short ODecimal::getUShort()
{
  unsigned short value;
  setResult(result, OCINumberToInt(ociError, &number, sizeof(value), OCI_NUMBER_UNSIGNED, &value), JP_MARK);
  return value;
}

float ODecimal::getFloat()
{
  float value;
  setResult(result, OCINumberToReal(ociError, &number, sizeof(value), &value), JP_MARK);
  return value;
}

double ODecimal::getDouble()
{
  double value;
  setResult(result, OCINumberToReal(ociError, &number, sizeof(value), &value), JP_MARK);
  return value;
}

long double ODecimal::getLongDouble()
{
  long double value;
  setResult(result, OCINumberToReal(ociError, &number, sizeof(value), &value), JP_MARK);
  return value;
}

//sword OCINumberToText ( OCIError *err,
//CONST OCINumber *number,
//CONST text *format,
//ub4 fmt_length,
//return value;
//CONST text *nls_params,
//ub4 nls_p_length,
//ub4 *buf_size,
//text *buf );

char* ODecimal::getText(char *value, ub4 &length, const char* format)
{
  setResult(result, OCINumberToText(ociError, &number, (const oratext *)format, strlen(format), 0, 0, &length, (oratext*)value), JP_MARK);
  return value;
}

char* ODecimal::getText(char *value, ub4 &length)
{
  return getText(value, length, numberFormat);
}

char* ODecimal::getText(const char *format)
{
  if (format != 0)
    setFormat(format);
  if (text != 0) delete [] text;
  ub4 len = (ub4) strlen(numberFormat)+1;
  text = new char [len];
  return getText(text, len, numberFormat);
}

// sword OCINumberIntPower ( OCIError *err,
// CONST OCINumber *base,
// CONST sword exp,
// OCINumber *result );
ODecimal ODecimal::power(int exp)
{
  ODecimal target;
  setResult(target.result, OCINumberIntPower(ociError, &number, exp, &target.number), JP_MARK);
  return target;
}

// sword OCINumberPrec ( OCIError *err,
// CONST OCINumber *number,
// eword nDigs,
// OCINumber *result );
//ODecimal ODecimal::prec(int exp)
//{ /
//  ODecimal target;
//  target.result = OCINumberPrec(ociError, &number, exp, &target.number);
//  return target;
//}

// sword OCINumberRound ( OCIError *err,
// CONST OCINumber *number,
// sword decplace,
// OCINumber *result );
ODecimal ODecimal::round(int places)
{
  ODecimal target;
  setResult(target.result, OCINumberRound(ociError, &number, places, &target.number), JP_MARK);
  return target;
}

// sword OCINumberTrunc ( OCIError *err,
// CONST OCINumber *number,
// sword decplace,
// OCINumber *result );
ODecimal ODecimal::trunc(int places)
{
  ODecimal target;
  setResult(target.result, OCINumberTrunc(ociError, &number, places, &target.number), JP_MARK);
  return target;
}

// sword OCINumberShift ( OCIError *err,
// CONST OCINumber *number,
// CONST sword nDig,
// OCINumber *result );
// ODecimal ODecimal::shift(int places)
// {
//   ODecimal target;
//   target.result = OCINumberShift(ociError, &number, places, &target.number);
//   return target;
// }


// sword OCINumberPower ( OCIError *err,
// CONST OCINumber *base,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::power(const ODecimal& exp)
{
  ODecimal target;
  setResult(target.result, OCINumberPower(ociError, &number, (OCINumber*)&exp.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberLog ( OCIError *err,
// CONST OCINumber *base,
// CONST OCINumber *number,
// OCINumber *result );
ODecimal ODecimal::log(const ODecimal& exp)
{
  ODecimal target;
  setResult(target.result, OCINumberLog(ociError, &number, (OCINumber*)&exp.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberCmp ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// sword *result );
int ODecimal::compare(const ODecimal& other)
{
  int value;
  setResult(result, OCINumberCmp(ociError, &number, (OCINumber*)&other.number, &value), JP_MARK);
  return value;
}

// sword OCINumberSign ( OCIError *err,
// CONST OCINumber *number,
// sword *result );
int ODecimal::sign()
{
  int value;
  setResult(result, OCINumberSign(ociError, &number, &value), JP_MARK);
  return value;
}


// sword OCINumberIsZero ( OCIError *err,
// CONST OCINumber *number,
// boolean *result );
int ODecimal::isZero()
{
  int value;
  setResult(result, OCINumberIsZero(ociError, &number, &value), JP_MARK);
  return value;
}

// void OCINumberSetZero (OCIError *err,
// CONST OCINumber *number);
void ODecimal::setZero()
{
  OCINumberSetZero(ociError, &number);
}

// sword OCINumberIsInt ( OCIError *err,
// CONST OCINumber *number,
// boolean *result );
//int ODecimal::isInt()
//{
//  int value;
//  result = OCINumberIsInt(ociError, &number, &value);
//  return value;
//}

// OCINumberDec(OCIError *err,
// OCINumber *number );
//void  ODecimal::dec()
//{
  //result = OCINumberDec(ociError, &number);
//}

// OCINumberDec(OCIError *err,
// OCINumber *number );
//void  ODecimal::inc()
//{
  //result = OCINumberInc(ociError, &number);
//}

ODecimal ODecimal::operator =(const ODecimal &rhs)
{
  setResult(result, OCINumberAssign(ociError, (OCINumber*)&rhs.number, &number), JP_MARK);
  return *this;
}

// sword OCINumberAdd ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::operator +(const ODecimal &rhs)
{
  ODecimal target;
  setResult(target.result, OCINumberAdd(ociError, (OCINumber*)&number, (OCINumber*)&rhs.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberSub ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::operator -(const ODecimal &rhs)
{
  ODecimal target;
  setResult(target.result, OCINumberSub(ociError, (OCINumber*)&number, (OCINumber*)&rhs.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberMul ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::operator *(const ODecimal &rhs)
{
  ODecimal target;
  setResult(target.result, OCINumberMul(ociError, (OCINumber*)&number, (OCINumber*)&rhs.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberDiv ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::operator /(const ODecimal &rhs)
{
  ODecimal target;
  setResult(target.result, OCINumberDiv(ociError, (OCINumber*)&number, (OCINumber*)&rhs.number, &target.number), JP_MARK);
  return target;
}

// sword OCINumberMod ( OCIError *err,
// CONST OCINumber *number1,
// CONST OCINumber *number2,
// OCINumber *result );
ODecimal ODecimal::operator %(const ODecimal &rhs)
{
  ODecimal target;
  setResult(target.result, OCINumberMod(ociError, (OCINumber*)&number, (OCINumber*)&rhs.number, &target.number), JP_MARK);
  return target;
}


bool ODecimal::operator >(const ODecimal &rhs)
{
  return compare(rhs) > 0;
}

bool ODecimal::operator <(const ODecimal &rhs)
{
  return compare(rhs) < 0;
}

bool ODecimal::operator ==(const ODecimal &rhs)
{
  return compare(rhs) == 0;
}

bool ODecimal::operator !=(const ODecimal &rhs)
{
  return compare(rhs) != 0;
}

bool ODecimal::operator >=(const ODecimal &rhs)
{
  return compare(rhs) >= 0;
}

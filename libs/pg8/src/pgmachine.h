#ifndef pgmachineH
#define pgmachineH 

#if defined(__OS2__)
  #define TJ_OS2
#elif defined(__NT__) || defined(__WIN32__) || defined(_WIN32)
  #define TJ_W32
#elif defined(_WINDOWS) || defined(_Windows)
  #define TJ_WIN
#elif defined(_MSDOS) || defined(__MSDOS__) || defined(__DOS__)
  #define TJ_DOS
#elif defined(__GNUC__)
  #define TJ_GNU
#elif defined(_AIX)
  #define TJ_AIX
  #if defined(_AIX51) && !defined(M_HASBOOL)
    #define M_HASBOOL
  #endif
#else
  #define TJ_UNKNOWN
#endif

#ifdef TJ_UNKNOWN
#error Unknown OS and Compiler
#endif

#if defined(TJ_AIX) || defined(TJ_OS2)
  #if !defined(M_HASBOOL) && !defined(True) && !defined(true)
    typedef enum bool{false, true};
    #define False false
    #define True  true
  #endif
#endif

#if defined(TJ_AIX)
  #include <va_list.h>
#endif

#include <stdio.h>
#if !defined(TJ_AIX) && !defined(TJ_GNU)
  #if defined(_MSC_VER)
    #include <memory.h>
    #include <malloc.h>
  #else
    #include <mem.h>
    #include <alloc.h>
  #endif
#endif
#include <string.h>
#include <stdlib.h>
#include <time.h>

#ifndef __STDC__
#define __STDC__ 1
#endif

#ifdef UNICODE
#undef UNICODE
#endif

#include "libpq-fe.h"
// This is an extract from catalog/pg_type - but I do not want the rest of the headaches
// associated by including that header.
#define BOOLOID                        16
#define BYTEAOID                       17
#define CHAROID                        18
#define NAMEOID                        19
#define INT8OID                        20
#define INT2OID                        21
#define INT2VECTOROID                  22
#define INT4OID                        23
#define REGPROCOID                     24
#define TEXTOID                        25
#define OIDOID                         26
#define TIDOID                         27
#define XIDOID                         28
#define CIDOID                         29
#define OIDVECTOROID                   30
#define PG_TYPE_RELTYPE_OID            71
#define PG_ATTRIBUTE_RELTYPE_OID       75
#define PG_PROC_RELTYPE_OID            81
#define PG_CLASS_RELTYPE_OID           83
#define XMLOID                        142
#define POINTOID                      600
#define LSEGOID                       601
#define PATHOID                       602
#define BOXOID                        603
#define POLYGONOID                    604
#define LINEOID                       628
#define CIDROID                       650
#define FLOAT4OID                     700
#define FLOAT8OID                     701
#define RELTIMEOID                    703
#define TINTERVALOID                  704
#define UNKNOWNOID                    705
#define CIRCLEOID                     718
#define CASHOID                       790
#define MACADDROID                    829
#define INETOID                       869
#define INT4ARRAYOID                 1007
#define FLOAT4ARRAYOID               1021
#define BPCHAROID                    1042
#define VARCHAROID                   1043
#define DATEOID                      1082
#define TIMEOID                      1083
#define TIMESTAMPOID                 1114
#define TIMESTAMPTZOID               1184
#define INTERVALOID                  1186
#define CSTRINGARRAYOID              1263
#define TIMETZOID                    1266
#define BITOID                       1560
#define VARBITOID                    1562
#define NUMERICOID                   1700
#define REFCURSOROID                 1790
#define REGPROCEDUREOID              2202
#define REGOPEROID                   2203
#define REGOPERATOROID               2204
#define REGCLASSOID                  2205
#define REGTYPEOID                   2206
#define REGTYPEARRAYOID              2211
#define RECORDOID                    2249
#define CSTRINGOID                   2275
#define VOIDOID                      2278
#define TRIGGEROID                   2279
#define LANGUAGE_HANDLEROID          2280
#define INTERNALOID                  2281
#define OPAQUEOID                    2282
#define TSVECTOROID                  3614
#define TSQUERYOID                   3615
#define GTSVECTOROID                 3642
#define REGCONFIGOID                 3734
#define REGDICTIONARYOID             3769
//

#define JP_INTERFACE(_type) extern "C" _type __export __stdcall
#define JP_INTERNAL(_type) static _type
#define JP_EXTERNAL(_type) _type
#define JP_NULL     (short)-1
#define JP_NOT_NULL (short)0
#define JP_MARK __FILE__,  __LINE__

#include <stdio.h>
#include "xcept.h"

struct TPGApiException : public xDBException
{
  TPGApiException(int aErrorNo, const char *error, const char *aFile, int aLine)
  : xDBException(aFile, aLine, "PGApiException", aErrorNo, error)
  {
    osErr << ends;
  }
  TPGApiException(const TPGApiException& copy)
  : xDBException(copy)
  {
  }
};

#endif


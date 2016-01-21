#ifndef ocimachineH
#define ocimachineH 

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
  #if !defined(M_HASBOOL) &&  !defined(True) && !defined(true)
    typedef enum bool{false, true};
    #define False false
    #define True  true
  #endif
#endif

#if defined(TJ_AIX) || defined(_MSC_VER)
  #define TJ_CAST (text*)
#else
  #define TJ_CAST (text*)
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

#include "oci.h"

#define JP_INTERFACE(_type) extern "C" _type __export __stdcall
#define JP_INTERNAL(_type) static _type
#define JP_EXTERNAL(_type) _type
#define JP_NULL     (short)-1
#define JP_NOT_NULL (short)0
#define JP_MARK __FILE__,  __LINE__

#include <stdio.h>

struct TOciApiException
{
  int  ErrorNo;
  char ErrorDesc[4096];
  char File[128];
  long Line;
  TOciApiException(int aErrorNo, char *aErrorDesc, char *aFile, long aLine)
  {
    memset(this, 0, sizeof(*this));
    ErrorNo = aErrorNo;
    strncpy(ErrorDesc, aErrorDesc, sizeof(ErrorDesc)-1);
    if (aFile)
      strncpy(File, aFile, sizeof(File)-1);
    Line = aLine;
  }
  TOciApiException(const TOciApiException& copy)
  {
    *this = copy;
  }
};

#endif


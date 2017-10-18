#ifndef __PG_ASPECT_H_
#define __PG_ASPECT_H_

#include "machine.h"
#include "logfile.h"
#include "pgapi.h"

#if defined(M_W32)
#define OS_DECLSPEC __declspec(dllexport)
#endif

class PgAspect
{
public:
  tLogFile* logFile;
  TJConnector* connect;
  void Setup(const char* logFileName);
};

#endif

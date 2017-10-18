#ifndef __UBI_BASE_VARS_H_
#define __UBI_BASE_VARS_H_

#include "machine.h"
#include "logfile.h"
#include "dbportal.h"

class UbiBaseVars
{
public:
  tLogFile   *logFile, &LogFile;
  tDBConnect *connect, &Connect;
  UbiBaseVars() 
  : LogFile(*logFile)
  , Connect(*connect)
  {
    logFile = tLogFile::logFile;
    connect = tDBConnect::connect;
  }
  virtual ~UbiBaseVars() 
  {
  }
};

#endif

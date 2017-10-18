#include "pgaspect.h" 

void PgAspect::Setup(const char* logFileName)
{
  TJConnector* connect =  new TJConnector();
  tLogFile* logFile = new tLogFile((char*)logFileName);
  this->connect = connect;    
  this->logFile = logFile;
}

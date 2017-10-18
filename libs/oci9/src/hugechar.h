#ifndef hugecharH
#define hugecharH
#include "machine.h"

class tHugeCHAR
{
public:
  int32 length;
  char *data;
  tHugeCHAR();
  ~tHugeCHAR();
  operator char*() {return data;}
  tHugeCHAR& operator=(const tHugeCHAR& hugeCHAR);
  tHugeCHAR& operator=(const char* inData);
  void setData(const char* inData);
};


#endif
#include "hugechar.h"
#include <string.h>

tHugeCHAR::tHugeCHAR()
{
  length = 0;
  data = 0;
}

tHugeCHAR::~tHugeCHAR()
{
  if (data)
  {
    delete [] data;
    data = 0;
  }
  length = 0;
}

tHugeCHAR& tHugeCHAR::operator=(const tHugeCHAR& inClob)
{
  if (data == inClob.data)
    return *this;
  length = inClob.length;
  if (data)
    delete [] data;
  data = 0;
  if (length > 0)
  {
    data = new char[length];
    memcpy(data, inClob.data, length-1);
    data[length-1] = 0;
  }
  return *this;
}

tHugeCHAR& tHugeCHAR::operator=(const char* inData)
{
  if (data)
    delete [] data;
  data = 0;
  if (inData == 0 || inData[0] == 0)
    length = 0;
  else
  {
    length = strlen(inData)+1;
    data = new char[length];
    memcpy(data, inData, length-1);
    data[length-1] = 0;
  }
  return *this;
}

void tHugeCHAR::setData(const char* inData)
{
  if (length > 0)
  {
    data = new char[length];
    memcpy(data, inData, length);
  }
}


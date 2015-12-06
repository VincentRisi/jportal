#ifndef __PARMGFECONFIG_H__
#define __PARMGFECONFIG_H__ "$Revision: 1.2 $ $Date: 2004/07/13 09:10:50 $"
#include "versions.h"
HEADER_VERSION(__PARMGFECONFIG_H__);

#include "xmlrecord.h"
#include "logfile.h"

struct ParmGFEConfig
{
  TBEnvChar configFileName;
  TBEnvChar parameterBinFile;
  void setup(TXMLRecord &msg)
  {
    msg.GetValue("Parameter/BinFile", parameterBinFile);
  }
  ParmGFEConfig(char* aConfigFileName)
  : configFileName(64)
  , parameterBinFile(64)
  {
     configFileName.set(aConfigFileName);
     TXMLRecord msg;
     msg.LoadFile(configFileName);
     setup(msg);
  }
  void show()
  {
    cout << "<?xml version=\"1.0\"?>" << endl;
    cout << "<!--" << endl;
    cout << "  This is well formed XML, strung out for readability" << endl;
    cout << "  -->" << endl;
    cout << "<ParmGFEConfig>" << endl;
    cout << "  <Parameter" << endl;
    cout << "    BinFile  = \"" << parameterBinFile << "\"" << endl;
    cout << "  />" << endl;
    cout << "</ParmGFEConfig>" << endl;
  }
};

#endif

/// ------------------------------------------------------------------
/// Copyright (c) 1996, 2004 Vincent Risi in Association
///                          with Barone Budge and Dominick
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// $Date: 2004/10/18 13:48:10 $
/// $Revision: 411.1 $ // YMM.Revision
/// ------------------------------------------------------------------
#include <vcl\vcl.h>
#pragma hdrstop

#include "CBuilderAdoConnector.h"

int TConnector::ConnNo;

__fastcall TConnector::TConnector(TComponent* Owner)
: Conn(new TADOConnection(Owner))
{
  fSequence = 0;
  fTimeStamp = 0;
  fUserStamp = 0;
  fUser      = "";
  ConnNo     = 0;
}

void TConnector::Logon(char *aProvider)
{
  Conn->Provider = aProvider;
  Conn->Open();
}

void TConnector::Logoff()
{
  Conn->Close();
}

TDateTime TConnector::getTimeStamp()
{
  if (fTimeStamp)
  {
    TDateTime result;
    fTimeStamp(this, result);
    return result;
  }
  return Date()+Time();
}

int TConnector::getSequence(System::AnsiString aTableName)
{
  if (fSequence)
  {
    int result;
    fSequence(this, aTableName, result);
    return result;
  }
  return 0;
}

System::AnsiString TConnector::getUserStamp()
{
  if (fUserStamp)
  {
    System::AnsiString result;
    fUserStamp(this, result);
    return result;
  }
  return User;
}


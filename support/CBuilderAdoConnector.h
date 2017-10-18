/// ------------------------------------------------------------------
/// Copyright (c) from 1996
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------
#ifndef CBuilderAdoConnectorH
#define CBuilderAdoConnectorH

#include <vcl\Classes.hpp>
#include <vcl\Controls.hpp>
#include <vcl\StdCtrls.hpp>
#include <vcl\adodb.hpp>

class TConnector;

typedef void __fastcall (*TSequenceFunction)(TConnector *Conn, System::AnsiString name, int &value);
typedef void __fastcall (*TTimeStampFunction)(TConnector *Conn, TDateTime &value);
typedef void __fastcall (*TUserStampFunction)(TConnector *Conn, System::AnsiString &value);

class TConnector// : public TADOConnection
{
private:
  static int ConnNo;
  TSequenceFunction  fSequence;
  TTimeStampFunction fTimeStamp;
  TUserStampFunction fUserStamp;
  AnsiString         fUser;
public:
  TADOConnection *Conn;
  __fastcall TConnector(TComponent* Owner);
  __fastcall virtual ~TConnector()
  {
    delete Conn;
  }
  void Logon(char *aProvider);
  void Logoff();
  TDateTime getTimeStamp();
  int getSequence(System::AnsiString aTableName);
  System::AnsiString getUserStamp();
  __property TSequenceFunction Sequence = {write = fSequence};
  __property TTimeStampFunction TimeStamp = {write = fTimeStamp};
  __property TUserStampFunction UserStamp = {write = fUserStamp};
  __property AnsiString User = {read = fUser, write = fUser};
};

#endif

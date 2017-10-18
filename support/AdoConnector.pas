/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
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

unit AdoConnector;

interface
uses
  SysUtils,
  AdoDb;

type
  TConnector = class;
  TSequenceFunction = function(const Conn: TConnector; TableName: string): Integer;
  TTimeStampFunction = function(const Conn: TConnector): TDateTime;
  TUserStampFunction = function(const Conn: TConnector): string;
  TConnector = class(TADOConnection)
  private
    fUser: string;
    fSequence: TSequenceFunction;
    fTimestamp: TTimeStampFunction;
    fUserstamp: TUserStampFunction;
  public
    procedure Logon(const AConnectString, AUser, APassword: string);
    procedure Logoff;
    function getSequence(table: string): Integer;
    function getTimestamp: TDateTime;
    function getUserstamp: string;
    property Sequence: TSequenceFunction write fSequence;
    property TimeStamp: TTimeStampFunction write fTimestamp;
    property UserStamp: TUserStampFunction write fUserstamp;
    property User: string read fUser write fUser;
  end;

implementation
var
  ConnNo: Integer = 0;

function TConnector.getSequence(table: string): Integer;
begin
  if assigned(fSequence) then
    Result := fSequence(Self, table)
  else
    Result := 0;
end;

function TConnector.getTimestamp: TDateTime;
begin
  if assigned(fTimestamp) then
    Result := fTimestamp(Self)
  else
    Result := Date;
end;

function TConnector.getUserstamp: string;
begin
  if assigned(fUserstamp) then
    Result := fUserstamp(Self)
  else
    Result := User;
end;

procedure TConnector.Logon(const AConnectString, AUser, APassword: string);
begin
  ConnectionString := AConnectString;
  if (Length(AUser) > 0) and (Length(APassword) > 0) then
    LoginPrompt := false;
  Open(AUser, APassword);
end;

procedure TConnector.Logoff;
begin
  Close;
end;

end.


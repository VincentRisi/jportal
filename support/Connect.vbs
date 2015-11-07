<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- Copyright (c) from 1996 Vincent Risi                              -->
<!-- All rights reserved.                                              -->
<!-- This program and the accompanying materials are made available    -->
<!-- under the terms of the Common Public License v1.0                 -->
<!-- which accompanies this distribution and is available at           -->
<!-- http://www.eclipse.org/legal/cpl-v10.html                         -->
<!-- Contributors:                                                     -->
<!--    Vincent Risi                                                   -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!-- System : JPortal                                                  -->
<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
<!--#includes file="adoenums.vbs"-->
<%
class CConnect
  public  routineName
  public  userMesssage
  public  errorCode
  public  errorDescr
  public  conn
  public  cursorLocation
  private connString
  private loggedOn

  private sub Class_Initialize()
    cursorLocation = adUseClient
    loggedOn = false
  end sub

  public sub Clear
    err.Clear
    errorDescr = ""
  end sub

  ' Name of connection alias in global.asa file
  public function Logon(connAlias)
    Logon = false
    if loggedOn = true then exit function
    Clear
    on error resume next
    connString = Session(connAlias)
    if err = 0 then set conn = CreateObject("ADODB.Connection") else RecordError "Session(connAlias)"
    if err = 0 then conn.CursorLocation = cursorLocation else RecordError "CreateObject(ADODB.Connection)"
    if err = 0 then conn.Open connString else RecordError "conn.CursorLocation"
    if err = 0 then loggedOn = true else RecordError "conn.Open connString"
    Logon = loggedOn
  end function

  public function Logoff()
    Logoff = false
    if loggedOn = true then
      Clear
      on error resume next
      conn.Close
      if err <> 0 then RecordError "conn.Close"
      set conn = nothing
      loggedOn = false
    end if
    Logoff = (err = 0)
  end function

  public function BeginTrans()
    BeginTrans = false
    Clear
    on error resume next
    conn.BeginTrans
    if err = 0 then BeginTrans = true else RecordError "conn.BeginTrans"
  end function

  public function HandleCommit(message)
    if errorCode = 0 then Commit
    if errorCode <> 0 then Rollback
    HandleCommit = (errorCode = 0)
  end function

  public sub Commit()
    Clear
    on error resume next
    conn.CommitTrans
    if err <> 0 then RecordError "Commit Failed"
  end sub

  public sub Rollback()
    Clear
    on error resume next
    conn.RollbackTrans
    if err <> 0 then RecordError "Rollback Failed"
  end sub

  public sub RecordError(message)
    errorCode = err
    errorDescr = err.Description
    userMessage = message
    Clear
  end sub

end class

class CCursor
  public rs
  public cmd
  public connect
  public isOpen

  private sub Class_Initialize()
    isOpen = false
  end sub

  private sub Class_Terminate()
    set rs = nothing
    set cmd = nothing
  end sub

  public sub SetConnect(byref aConnect)
    set connect = aConnect
    on error resume next
    set cmd = CreateObject("ADODB.Command")
    if err = 0 then set cmd.ActiveConnection = connect.conn else connect.RecordError "CreateObject(ADODB.Command)"
    if err <> 0 then connect.RecordError "cmd.ActiveConnection = connect.conn"
  end sub

  public sub CommandText(byref aText)
    on error resume next
    cmd.CommandText = aText
    if err = 0 then cmd.CommandType = adCmdText else connect.RecordError "cmd.CommandText = " & aText
    if err <> 0 then connect.RecordError "cmd.CommandType = adCmdText"
  end sub

  public sub CommandProc(aProc)
    on error resume next
    cmd.CommandText = aProc
    if err = 0 then cmd.CommandType = adCmdStoredProc else connect.RecordError "cmd.CommandProc = " & aProc
    if err <> 0 then connect.RecordError "cmd.CommandType = adCmdStoredProc"
  end sub

  public sub CreateParameter(parmName, parmType, parmDir, parmSize, value)
    on error resume next
    dim p
    set p = cmd.CreateParameter(parmName, parmType, parmDir, parmSize, value)
    if err = 0 then cmd.Parameters.Append p else connect.RecordError "cmd.CreateParameter"
    if err <> 0 then connect.RecordError "cmd.Parameters.Append p"
  end sub

  public sub Execute
    on error resume next
    set rs = cmd.Execute
    if err <> 0 then connect.RecordError "rs = cmd.Execute"
  end sub

end class

%>


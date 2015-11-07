VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "tConnect"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Attribute VB_Ext_KEY = "SavedWithClassBuilder" ,"Yes"
Attribute VB_Ext_KEY = "Top_Level" ,"Yes"
''' ------------------------------------------------------------------
''' Copyright (c) from 1996 Vincent Risi
''' All rights reserved.
''' This program and the accompanying materials are made available
''' under the terms of the Common Public License v1.0
''' which accompanies this distribution and is available at
''' http://www.eclipse.org/legal/cpl-v10.html
''' Contributors:
'''    Vincent Risi
''' ------------------------------------------------------------------
''' System : JPortal
''' ------------------------------------------------------------------
Option Explicit
' General Connect Class used for all JPortal ODBCDirect Access
' This code can be modified, but preferably back it up and make the
' modifications to an application specific version.

Public RoutineName As String
Public UserMessage As String
Public ErrorCode   As Long
Public ErrorDescr  As String
Public WS          As Workspace
Public DB          As Connection

Private LoggedOn   As Boolean

Public Function BeginTrans(Optional ShowError As Boolean) As Boolean
   BeginTrans = False
   On Error GoTo BeginTransError
   WS.BeginTrans
   BeginTrans = True
BeginTransExit:
   Exit Function
BeginTransError:
   DisplayError "BeginTrans Failure", ShowError
   Resume BeginTransExit
End Function

Public Sub Commit(Optional ShowError As Boolean)
   On Error GoTo CommitError
   WS.CommitTrans
CommitExit:
   Exit Sub
CommitError:
   UserMessage = "Commit Failed"
   ErrorCode = Err
   ErrorDescr = Error$
   Resume CommitExit
End Sub

Public Function DAOError(Optional ShowError As Boolean) As Boolean
   On Error GoTo DAOErrorError
   UserMessage = " Error"
   If IsMissing(ShowError) Then ShowError = False
   If ShowError Then DisplayDAOError ("Database Error")
   DAOError = (ErrorCode <> ERC_OK)
DAOErrorExit:
   Exit Function
DAOErrorError:
   DAOError = True
   ErrorCode = Err
   ErrorDescr = Error$
   If IsMissing(ShowError) Then ShowError = False
   If ShowError Then DisplayDAOError ("Invalid object passed to DAOError handler")
   Resume DAOErrorExit
End Function

Public Function HandleDAOCommit(UserMsg As String) As Boolean
   If ErrorCode = ERC_OK Then Commit
   If ErrorCode <> ERC_OK Then
      Rollback
      DisplayDAOError UserMsg
   End If
   HandleDAOCommit = (ErrorCode = ERC_OK)
End Function

Public Function Logoff(Optional ShowError As Boolean) As Boolean
   Logoff = False
   If LoggedOn Then
      '*** Close The Database Instance ***'
      On Error GoTo LogoffError
      DB.Close
      LoggedOn = False
   End If
   Logoff = True
LogoffExit:
   Exit Function
LogoffError:
   If IsMissing(ShowError) Then ShowError = True
   DisplayError "Database logoff failed", ShowError
   Resume LogoffExit
End Function

Public Function Logon(DataBaseName As String, ConnectStr As String) As Boolean
   Logon = False
   If LoggedOn Then
      LogError LOG_ERROR, "This instance has already been used for a connection"
      GoTo LogonExit
   End If
   On Error GoTo LogonWSError
   Set WS = CreateWorkspace("ODBCDirectWS", "admin", "", dbUseODBC)
   Workspaces.Append WS
   On Error GoTo LogonDBError
   Set DB = WS.OpenConnection(DataBaseName, dbDriverNoPrompt, False, ConnectStr)
   LoggedOn = True
   Logon = True
LogonExit:
   Exit Function
LogonWSError:
   DisplayError "WorkSpace create error", True
   Resume LogonExit
LogonDBError:
   DisplayError "Database logon failed", True
   Resume LogonExit
End Function

Public Sub Rollback(Optional ShowError As Boolean)
   On Error GoTo RollBackError
   WS.Rollback
RollBackExit:
   Exit Sub
RollBackError:
   DisplayError "Rollback Failed", ShowError
   LogError LOG_FATAL, "Fatal Error In Rollback"
End Sub

Public Sub DisplayDAOError(UserMsg As String)
   If ErrorCode <> 0 Then
      MsgBox UserMsg & CRCR & "Error Code :" & Str$(ErrorCode) & ", Error Descr : " & ErrorDescr, vbOKOnly + vbCritical, UserMessage
      Screen.ActiveForm.Refresh
   End If
End Sub

VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "tCursor"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = True
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
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
' General Cursor class used for all JPortal Multiple Row Queries
' This code can be modified, but preferably back it up and make the
' modifications to an application specific version.

Public QD   As QueryDef
Public RS   As Recordset
Public Connect As tConnect
Public Function CloseCursor(Optional ShowError As Boolean) As Boolean
   CloseCursor = False
   On Error GoTo CloseCursorError
   RS.Close
   QD.Close
   CloseCursor = True
CloseCursorExit:
   Exit Function
CloseCursorError:
   If IsMissing(ShowError) Then ShowError = True
   DisplayError "CloseCursor Failed", ShowError
   Resume CloseCursorExit
End Function

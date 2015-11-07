### ------------------------------------------------------------------
### Copyright (c) from 1996 Vincent Risi
### All rights reserved.
### This program and the accompanying materials are made available
### under the terms of the Common Public License v1.0
### which accompanies this distribution and is available at
### http://www.eclipse.org/legal/cpl-v10.html
### Contributors:
###    Vincent Risi
### ------------------------------------------------------------------
### System : JPortal
### ------------------------------------------------------------------
class TConnector():
    def __init__(self):
        self.this = apply(_adoconnector.new_TConnector,args)
        self.thisown = 1
    def Logoff(*args): return apply(_adoconnector.TConnector_Logoff,args)
    def GetSequence(*args): return apply(_adoconnector.TConnector_GetSequence,args)
    def GetTimeStamp(*args): return apply(_adoconnector.TConnector_GetTimeStamp,args)
    def GetUserStamp(*args): return apply(_adoconnector.TConnector_GetUserStamp,args)
    def SetSequence(*args): return apply(_adoconnector.TConnector_SetSequence,args)
    def SetTimeStamp(*args): return apply(_adoconnector.TConnector_SetTimeStamp,args)
    def SetUserStamp(*args): return apply(_adoconnector.TConnector_SetUserStamp,args)
    def BeginTran(*args): return apply(_adoconnector.TConnector_BeginTran,args)
    def Commit(*args): return apply(_adoconnector.TConnector_Commit,args)
    def Rollback(*args): return apply(_adoconnector.TConnector_Rollback,args)
    def IsLoggedOn(*args): return apply(_adoconnector.TConnector_IsLoggedOn,args)
    def InTran(*args): return apply(_adoconnector.TConnector_InTran,args)
    __swig_getmethods__["Trim"] = lambda x: _adoconnector.TConnector_Trim
    if _newclass:Trim = staticmethod(_adoconnector.TConnector_Trim)
    __swig_getmethods__["Pad"] = lambda x: _adoconnector.TConnector_Pad
    if _newclass:Pad = staticmethod(_adoconnector.TConnector_Pad)
    def Logon(*args): return apply(_adoconnector.TConnector_Logon,args)
    def __del__(self, destroy= _adoconnector.delete_TConnector):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C TConnector instance at %s>" % (self.this,)

class TQuery():
    BLOB = _adoconnector.TQuery_BLOB
    BOOLEAN = _adoconnector.TQuery_BOOLEAN
    BYTE = _adoconnector.TQuery_BYTE
    CHAR = _adoconnector.TQuery_CHAR
    DATE = _adoconnector.TQuery_DATE
    DATETIME = _adoconnector.TQuery_DATETIME
    DOUBLE = _adoconnector.TQuery_DOUBLE
    DYNAMIC = _adoconnector.TQuery_DYNAMIC
    FLOAT = _adoconnector.TQuery_FLOAT
    IDENTITY = _adoconnector.TQuery_IDENTITY
    INT = _adoconnector.TQuery_INT
    LONG = _adoconnector.TQuery_LONG
    MONEY = _adoconnector.TQuery_MONEY
    SEQUENCE = _adoconnector.TQuery_SEQUENCE
    SHORT = _adoconnector.TQuery_SHORT
    STATUS = _adoconnector.TQuery_STATUS
    TIME = _adoconnector.TQuery_TIME
    TIMESTAMP = _adoconnector.TQuery_TIMESTAMP
    TLOB = _adoconnector.TQuery_TLOB
    USERSTAMP = _adoconnector.TQuery_USERSTAMP
    ANSICHAR = _adoconnector.TQuery_ANSICHAR
    __swig_setmethods__["conn"] = _adoconnector.TQuery_conn_set
    __swig_getmethods__["conn"] = _adoconnector.TQuery_conn_get
    if _newclass:conn = property(_adoconnector.TQuery_conn_get,_adoconnector.TQuery_conn_set)
    def __init__(self,*args):
        self.this = apply(_adoconnector.new_TQuery,args)
        self.thisown = 1
    def Open(*args): return apply(_adoconnector.TQuery_Open,args)
    def Close(*args): return apply(_adoconnector.TQuery_Close,args)
    def Exec(*args): return apply(_adoconnector.TQuery_Exec,args)
    def Get(*args): return apply(_adoconnector.TQuery_Get,args)
    def GetNull(*args): return apply(_adoconnector.TQuery_GetNull,args)
    def Put(*args): return apply(_adoconnector.TQuery_Put,args)
    def EndOfFile(*args): return apply(_adoconnector.TQuery_EndOfFile,args)
    def BeginOfFile(*args): return apply(_adoconnector.TQuery_BeginOfFile,args)
    def Next(*args): return apply(_adoconnector.TQuery_Next,args)
    def MoveFirst(*args): return apply(_adoconnector.TQuery_MoveFirst,args)
    def FileAndLine(*args): return apply(_adoconnector.TQuery_FileAndLine,args)
    def getInt(*args): return apply(_adoconnector.TQuery_getInt,args)
    def getLong(*args): return apply(_adoconnector.TQuery_getLong,args)
    def getDouble(*args): return apply(_adoconnector.TQuery_getDouble,args)
    def getString(*args): return apply(_adoconnector.TQuery_getString,args)
    def setInt(*args): return apply(_adoconnector.TQuery_setInt,args)
    def setLong(*args): return apply(_adoconnector.TQuery_setLong,args)
    def setDouble(*args): return apply(_adoconnector.TQuery_setDouble,args)
    def setString(*args): return apply(_adoconnector.TQuery_setString,args)
    def isNull(*args): return apply(_adoconnector.TQuery_isNull,args)
    def __del__(self, destroy= _adoconnector.delete_TQuery):
        try:
            if self.thisown: destroy(self)
        except: pass
    def __repr__(self):
        return "<C TQuery instance at %s>" % (self.this,)

class TQueryPtr(TQuery):
    def __init__(self,this):
        self.this = this
        if not hasattr(self,"thisown"): self.thisown = 0
        self.__class__ = TQuery
_adoconnector.TQuery_swigregister(TQueryPtr)



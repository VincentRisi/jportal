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
import win32com.client as wc
#---- CursorTypeEnum Values ----
adOpenForwardOnly = 0
adOpenKeyset = 1
adOpenDynamic = 2
adOpenStatic = 3

#---- CursorOptionEnum Values ----
adHoldRecords = 0x00000100
adMovePrevious = 0x00000200
adAddNew = 0x01000400
adDelete = 0x01000800
adUpdate = 0x01008000
adBookmark = 0x00002000
adApproxPosition = 0x00004000
adUpdateBatch = 0x00010000
adResync = 0x00020000
adNotify = 0x00040000
adFind = 0x00080000
adSeek = 0x00400000
adIndex = 0x00800000

#---- LockTypeEnum Values ----
adLockReadOnly = 1
adLockPessimistic = 2
adLockOptimistic = 3
adLockBatchOptimistic = 4

#---- ExecuteOptionEnum Values ----
adAsyncExecute = 0x00000010
adAsyncFetch = 0x00000020
adAsyncFetchNonBlocking = 0x00000040
adExecuteNoRecords = 0x00000080

#---- ConnectOptionEnum Values ----
adAsyncConnect = 0x00000010

#---- ObjectStateEnum Values ----
adStateClosed = 0x00000000
adStateOpen = 0x00000001
adStateConnecting = 0x00000002
adStateExecuting = 0x00000004
adStateFetching = 0x00000008

#---- CursorLocationEnum Values ----
adUseServer = 2
adUseClient = 3

#---- DataTypeEnum Values ----
adEmpty = 0
adTinyInt = 16
adSmallInt = 2
adInteger = 3
adBigInt = 20
adUnsignedTinyInt = 17
adUnsignedSmallInt = 18
adUnsignedInt = 19
adUnsignedBigInt = 21
adSingle = 4
adDouble = 5
adCurrency = 6
adDecimal = 14
adNumeric = 131
adBoolean = 11
adError = 10
adUserDefined = 132
adVariant = 12
adIDispatch = 9
adIUnknown = 13
adGUID = 72
adDate = 7
adDBDate = 133
adDBTime = 134
adDBTimeStamp = 135
adBSTR = 8
adChar = 129
adVarChar = 200
adLongVarChar = 201
adWChar = 130
adVarWChar = 202
adLongVarWChar = 203
adBinary = 128
adVarBinary = 204
adLongVarBinary = 205
adChapter = 136
adFileTime = 64
adPropVariant = 138
adVarNumeric = 139
adArray = 0x2000

#---- FieldAttributeEnum Values ----
adFldMayDefer = 0x00000002
adFldUpdatable = 0x00000004
adFldUnknownUpdatable = 0x00000008
adFldFixed = 0x00000010
adFldIsNullable = 0x00000020
adFldMayBeNull = 0x00000040
adFldLong = 0x00000080
adFldRowID = 0x00000100
adFldRowVersion = 0x00000200
adFldCacheDeferred = 0x00001000
adFldIsChapter = 0x00002000
adFldNegativeScale = 0x00004000
adFldKeyColumn = 0x00008000
adFldIsRowURL = 0x00010000
adFldIsDefaultStream = 0x00020000
adFldIsCollection = 0x00040000

#---- EditModeEnum Values ----
adEditNone = 0x0000
adEditInProgress = 0x0001
adEditAdd = 0x0002
adEditDelete = 0x0004

#---- RecordStatusEnum Values ----
adRecOK = 0x0000000
adRecNew = 0x0000001
adRecModified = 0x0000002
adRecDeleted = 0x0000004
adRecUnmodified = 0x0000008
adRecInvalid = 0x0000010
adRecMultipleChanges = 0x0000040
adRecPendingChanges = 0x0000080
adRecCanceled = 0x0000100
adRecCantRelease = 0x0000400
adRecConcurrencyViolation = 0x0000800
adRecIntegrityViolation = 0x0001000
adRecMaxChangesExceeded = 0x0002000
adRecObjectOpen = 0x0004000
adRecOutOfMemory = 0x0008000
adRecPermissionDenied = 0x0010000
adRecSchemaViolation = 0x0020000
adRecDBDeleted = 0x0040000

#---- GetRowsOptionEnum Values ----
adGetRowsRest = -1

#---- PositionEnum Values ----
adPosUnknown = -1
adPosBOF = -2
adPosEOF = -3

#---- BookmarkEnum Values ----
adBookmarkCurrent = 0
adBookmarkFirst = 1
adBookmarkLast = 2

#---- MarshalOptionsEnum Values ----
adMarshalAll = 0
adMarshalModifiedOnly = 1

#---- AffectEnum Values ----
adAffectCurrent = 1
adAffectGroup = 2
adAffectAllChapters = 4

#---- ResyncEnum Values ----
adResyncUnderlyingValues = 1
adResyncAllValues = 2

#---- CompareEnum Values ----
adCompareLessThan = 0
adCompareEqual = 1
adCompareGreaterThan = 2
adCompareNotEqual = 3
adCompareNotComparable = 4

#---- FilterGroupEnum Values ----
adFilterNone = 0
adFilterPendingRecords = 1
adFilterAffectedRecords = 2
adFilterFetchedRecords = 3
adFilterConflictingRecords = 5

#---- SearchDirectionEnum Values ----
adSearchForward = 1
adSearchBackward = -1

#---- PersistFormatEnum Values ----
adPersistADTG = 0
adPersistXML = 1

#---- StringFormatEnum Values ----
adClipString = 2

#---- ConnectPromptEnum Values ----
adPromptAlways = 1
adPromptComplete = 2
adPromptCompleteRequired = 3
adPromptNever = 4

#---- ConnectModeEnum Values ----
adModeUnknown = 0
adModeRead = 1
adModeWrite = 2
adModeReadWrite = 3
adModeShareDenyRead = 4
adModeShareDenyWrite = 8
adModeShareExclusive = 0xc
adModeShareDenyNone = 0x10
adModeRecursive = 0x400000

#---- RecordCreateOptionsEnum Values ----
adCreateCollection = 0x00002000
adCreateStructDoc = 0x80000000
adCreateNonCollection = 0x00000000
adOpenIfExists = 0x02000000
adCreateOverwrite = 0x04000000
adFailIfNotExists = -1

#---- RecordOpenOptionsEnum Values ----
adOpenRecordUnspecified = -1
adOpenSource = 0x00800000
adOpenAsync = 0x00001000
adDelayFetchStream = 0x00004000
adDelayFetchFields = 0x00008000

#---- IsolationLevelEnum Values ----
adXactUnspecified = 0xffffffff
adXactChaos = 0x00000010
adXactReadUncommitted = 0x00000100
adXactBrowse = 0x00000100
adXactCursorStability = 0x00001000
adXactReadCommitted = 0x00001000
adXactRepeatableRead = 0x00010000
adXactSerializable = 0x00100000
adXactIsolated = 0x00100000

#---- XactAttributeEnum Values ----
adXactCommitRetaining = 0x00020000
adXactAbortRetaining = 0x00040000

#---- PropertyAttributesEnum Values ----
adPropNotSupported = 0x0000
adPropRequired = 0x0001
adPropOptional = 0x0002
adPropRead = 0x0200
adPropWrite = 0x0400

#---- ErrorValueEnum Values ----
adErrProviderFailed = 0xbb8
adErrInvalidArgument = 0xbb9
adErrOpeningFile = 0xbba
adErrReadFile = 0xbbb
adErrWriteFile = 0xbbc
adErrNoCurrentRecord = 0xbcd
adErrIllegalOperation = 0xc93
adErrCantChangeProvider = 0xc94
adErrInTransaction = 0xcae
adErrFeatureNotAvailable = 0xcb3
adErrItemNotFound = 0xcc1
adErrObjectInCollection = 0xd27
adErrObjectNotSet = 0xd5c
adErrDataConversion = 0xd5d
adErrObjectClosed = 0xe78
adErrObjectOpen = 0xe79
adErrProviderNotFound = 0xe7a
adErrBoundToCommand = 0xe7b
adErrInvalidParamInfo = 0xe7c
adErrInvalidConnection = 0xe7d
adErrNotReentrant = 0xe7e
adErrStillExecuting = 0xe7f
adErrOperationCancelled = 0xe80
adErrStillConnecting = 0xe81
adErrInvalidTransaction = 0xe82
adErrUnsafeOperation = 0xe84
adwrnSecurityDialog = 0xe85
adwrnSecurityDialogHeader = 0xe86
adErrIntegrityViolation = 0xe87
adErrPermissionDenied = 0xe88
adErrDataOverflow = 0xe89
adErrSchemaViolation = 0xe8a
adErrSignMismatch = 0xe8b
adErrCantConvertvalue = 0xe8c
adErrCantCreate = 0xe8d
adErrColumnNotOnThisRow = 0xe8e
adErrURLIntegrViolSetColumns = 0xe8f
adErrURLDoesNotExist = 0xe8f
adErrTreePermissionDenied = 0xe90
adErrInvalidURL = 0xe91
adErrResourceLocked = 0xe92
adErrResourceExists = 0xe93
adErrCannotComplete = 0xe94
adErrVolumeNotFound = 0xe95
adErrOutOfSpace = 0xe96
adErrResourceOutOfScope = 0xe97
adErrUnavailable = 0xe98
adErrURLNamedRowDoesNotExist = 0xe99
adErrDelResOutOfScope = 0xe9a
adErrPropInvalidColumn = 0xe9b
adErrPropInvalidOption = 0xe9c
adErrPropInvalidValue = 0xe9d
adErrPropConflicting = 0xe9e
adErrPropNotAllSettable = 0xe9f
adErrPropNotSet = 0xea0
adErrPropNotSettable = 0xea1
adErrPropNotSupported = 0xea2
adErrCatalogNotSet = 0xea3
adErrCantChangeConnection = 0xea4
adErrFieldsUpdateFailed = 0xea5
adErrDenyNotSupported = 0xea6
adErrDenyTypeNotSupported = 0xea7

#---- ParameterAttributesEnum Values ----
adParamSigned = 0x0010
adParamNullable = 0x0040
adParamLong = 0x0080

#---- ParameterDirectionEnum Values ----
adParamUnknown = 0x0000
adParamInput = 0x0001
adParamOutput = 0x0002
adParamInputOutput = 0x0003
adParamReturnValue = 0x0004

#---- CommandTypeEnum Values ----
adCmdUnknown = 0x0008
adCmdText = 0x0001
adCmdTable = 0x0002
adCmdStoredProc = 0x0004
adCmdFile = 0x0100
adCmdTableDirect = 0x0200

#---- EventStatusEnum Values ----
adStatusOK = 0x0000001
adStatusErrorsOccurred = 0x0000002
adStatusCantDeny = 0x0000003
adStatusCancel = 0x0000004
adStatusUnwantedEvent = 0x0000005

#---- EventReasonEnum Values ----
adRsnAddNew = 1
adRsnDelete = 2
adRsnUpdate = 3
adRsnUndoUpdate = 4
adRsnUndoAddNew = 5
adRsnUndoDelete = 6
adRsnRequery = 7
adRsnResynch = 8
adRsnClose = 9
adRsnMove = 10
adRsnFirstChange = 11
adRsnMoveFirst = 12
adRsnMoveNext = 13
adRsnMovePrevious = 14
adRsnMoveLast = 15

#---- SchemaEnum Values ----
adSchemaProviderSpecific = -1
adSchemaAsserts = 0
adSchemaCatalogs = 1
adSchemaCharacterSets = 2
adSchemaCollations = 3
adSchemaColumns = 4
adSchemaCheckConstraints = 5
adSchemaConstraintColumnUsage = 6
adSchemaConstraintTableUsage = 7
adSchemaKeyColumnUsage = 8
adSchemaReferentialConstraints = 9
adSchemaTableConstraints = 10
adSchemaColumnsDomainUsage = 11
adSchemaIndexes = 12
adSchemaColumnPrivileges = 13
adSchemaTablePrivileges = 14
adSchemaUsagePrivileges = 15
adSchemaProcedures = 16
adSchemaSchemata = 17
adSchemaSQLLanguages = 18
adSchemaStatistics = 19
adSchemaTables = 20
adSchemaTranslations = 21
adSchemaProviderTypes = 22
adSchemaViews = 23
adSchemaViewColumnUsage = 24
adSchemaViewTableUsage = 25
adSchemaProcedureParameters = 26
adSchemaForeignKeys = 27
adSchemaPrimaryKeys = 28
adSchemaProcedureColumns = 29
adSchemaDBInfoKeywords = 30
adSchemaDBInfoLiterals = 31
adSchemaCubes = 32
adSchemaDimensions = 33
adSchemaHierarchies = 34
adSchemaLevels = 35
adSchemaMeasures = 36
adSchemaProperties = 37
adSchemaMembers = 38
adSchemaTrustees = 39

#---- FieldStatusEnum Values ----
adFieldOK = 0
adFieldCantConvertValue = 2
adFieldIsNull = 3
adFieldTruncated = 4
adFieldSignMismatch = 5
adFieldDataOverflow = 6
adFieldCantCreate = 7
adFieldUnavailable = 8
adFieldPermissionDenied = 9
adFieldIntegrityViolation = 10
adFieldSchemaViolation = 11
adFieldBadStatus = 12
adFieldDefault = 13
adFieldIgnore = 15
adFieldDoesNotExist = 16
adFieldInvalidURL = 17
adFieldResourceLocked = 18
adFieldResourceExists = 19
adFieldCannotComplete = 20
adFieldVolumeNotFound = 21
adFieldOutOfSpace = 22
adFieldCannotDeleteSource = 23
adFieldReadOnly = 24
adFieldResourceOutOfScope = 25
adFieldAlreadyExists = 26
adFieldPendingInsert = 0x10000
adFieldPendingDelete = 0x20000
adFieldPendingChange = 0x40000
adFieldPendingUnknown = 0x80000
adFieldPendingUnknownDelete = 0x100000

#---- SeekEnum Values ----
adSeekFirstEQ = 0x1
adSeekLastEQ = 0x2
adSeekAfterEQ = 0x4
adSeekAfter = 0x8
adSeekBeforeEQ = 0x10
adSeekBefore = 0x20

#---- ADCPROP_UPDATECRITERIA_ENUM Values ----
adCriteriaKey = 0
adCriteriaAllCols = 1
adCriteriaUpdCols = 2
adCriteriaTimeStamp = 3

#---- ADCPROP_ASYNCTHREADPRIORITY_ENUM Values ----
adPriorityLowest = 1
adPriorityBelowNormal = 2
adPriorityNormal = 3
adPriorityAboveNormal = 4
adPriorityHighest = 5

#---- ADCPROP_AUTORECALC_ENUM Values ----
adRecalcUpFront = 0
adRecalcAlways = 1

#---- ADCPROP_UPDATERESYNC_ENUM Values ----

#---- ADCPROP_UPDATERESYNC_ENUM Values ----

#---- MoveRecordOptionsEnum Values ----
adMoveUnspecified = -1
adMoveOverWrite = 1
adMoveDontUpdateLinks = 2
adMoveAllowEmulation = 4

#---- CopyRecordOptionsEnum Values ----
adCopyUnspecified = -1
adCopyOverWrite = 1
adCopyAllowEmulation = 4
adCopyNonRecursive = 2

#---- StreamTypeEnum Values ----
adTypeBinary = 1
adTypeText = 2

#---- LineSeparatorEnum Values ----
adLF = 10
adCR = 13
adCRLF = -1

#---- StreamOpenOptionsEnum Values ----
adOpenStreamUnspecified = -1
adOpenStreamAsync = 1
adOpenStreamFromRecord = 4

#---- StreamWriteEnum Values ----
adWriteChar = 0
adWriteLine = 1

#---- SaveOptionsEnum Values ----
adSaveCreateNotExist = 1
adSaveCreateOverWrite = 2

#---- FieldEnum Values ----
adDefaultStream = -1
adRecordURL = -2

#---- StreamReadEnum Values ----
adReadAll = -1
adReadLine = -2

#---- RecordTypeEnum Values ----
adSimpleRecord = 0
adCollectionRecord = 1
adStructDoc = 2

class Proforma:
  def __init__(self, list):
    for i in list:
      self.__dict__[i.upper()] = None
  def __setattr__(self, n, v):
    n = n.upper()
    if self.__dict__.has_key(n):
      self.__dict__[n] = v
    else:
      raise n+' is not a member of this class'
  def __getattr__(self, n):
    return self.__dict__[n.upper()]

class TConnector(Proforma):
  def __init__(self):
    Proforma.__init__(self, ['conn', 'cursorLocation', 'loggedOn'])
    self.conn = None
    self.cursorLocation = adUseClient
    self.loggedOn = 0
  def Logon(self, connString):
    if self.loggedOn == 1:
      return 0
    self.conn = wc.Dispatch("ADODB.Connection")
    self.conn.CursorLocation = self.cursorLocation
    self.conn.Open(connString)
    self.loggedOn = 1
    return 1
  def Logoff(self):
    if self.loggedOn == 1:
      self.conn.Close()
      self.conn = None
      self.loggedOn = 0
    return 1
  def BeginTran(self):
    self.conn.BeginTrans()
  def Commit(self):
    self.conn.Commit()
  def Rollback(self):
    self.conn.Rollback()

class TQuery(Proforma):
  def __init__(self):
    Proforma.__init__(self, ['rs', 'cmd', 'connect', 'isOpen'])
    self.rs = None
    self.cmd = None
    self.isOpen = 0
  def setConnect(self, connect):
    self.connect = connect
    self.cmd = wc.Dispatch("ADODB.Command")
    self.cmd.ActiveConnection = self.connect.conn
  def commandText(self, text):
    self.cmd.CommandText = text
    self.cmd.CommandType = adCmdText
  def commandProc(self, text):
    self.cmd.CommandText = text
    self.cmd.CommandType = adCmdStoredProc
  def createParameter(self, parmName, parmType, parmDir, parmSize, value):
    p = self.cmd.CreateParameter(parmName, parmType, parmDir, parmSize, value)
    self.cmd.Parameters.Append p
  def execute(self):
    self.rs = self.cmd.Execute()

  #def GetSequence(*args): return apply(_adoconnector.TConnector_GetSequence,args)
  #def GetTimeStamp(*args): return apply(_adoconnector.TConnector_GetTimeStamp,args)
  #def GetUserStamp(*args): return apply(_adoconnector.TConnector_GetUserStamp,args)
  #def SetSequence(*args): return apply(_adoconnector.TConnector_SetSequence,args)
  #def SetTimeStamp(*args): return apply(_adoconnector.TConnector_SetTimeStamp,args)
  #def SetUserStamp(*args): return apply(_adoconnector.TConnector_SetUserStamp,args)
  #def IsLoggedOn(*args): return apply(_adoconnector.TConnector_IsLoggedOn,args)
  #def InTran(*args): return apply(_adoconnector.TConnector_InTran,args)
  #__swig_getmethods__["Trim"] = lambda x: _adoconnector.TConnector_Trim
  #if _newclass:Trim = staticmethod(_adoconnector.TConnector_Trim)
  #__swig_getmethods__["Pad"] = lambda x: _adoconnector.TConnector_Pad
  #if _newclass:Pad = staticmethod(_adoconnector.TConnector_Pad)
  #def __del__(self, destroy= _adoconnector.delete_TConnector):
  #    try:
  #        if self.thisown: destroy(self)
  #    except: pass
  #def __repr__(self):
  #    return "<C TConnector instance at %s>" % (self.this,)

  #  BLOB = _adoconnector.TQuery_BLOB
  #  BOOLEAN = _adoconnector.TQuery_BOOLEAN
  #  BYTE = _adoconnector.TQuery_BYTE
  #  CHAR = _adoconnector.TQuery_CHAR
  #  DATE = _adoconnector.TQuery_DATE
  #  DATETIME = _adoconnector.TQuery_DATETIME
  #  DOUBLE = _adoconnector.TQuery_DOUBLE
  #  DYNAMIC = _adoconnector.TQuery_DYNAMIC
  #  FLOAT = _adoconnector.TQuery_FLOAT
  #  IDENTITY = _adoconnector.TQuery_IDENTITY
  #  INT = _adoconnector.TQuery_INT
  #  LONG = _adoconnector.TQuery_LONG
  #  MONEY = _adoconnector.TQuery_MONEY
  #  SEQUENCE = _adoconnector.TQuery_SEQUENCE
  #  SHORT = _adoconnector.TQuery_SHORT
  #  STATUS = _adoconnector.TQuery_STATUS
  #  TIME = _adoconnector.TQuery_TIME
  #  TIMESTAMP = _adoconnector.TQuery_TIMESTAMP
  #  TLOB = _adoconnector.TQuery_TLOB
  #  USERSTAMP = _adoconnector.TQuery_USERSTAMP
  #  ANSICHAR = _adoconnector.TQuery_ANSICHAR
  #  __swig_setmethods__["conn"] = _adoconnector.TQuery_conn_set
  #  __swig_getmethods__["conn"] = _adoconnector.TQuery_conn_get
  #  if _newclass:conn = property(_adoconnector.TQuery_conn_get,_adoconnector.TQuery_conn_set)
  #  def __init__(self,*args):
  #      self.this = apply(_adoconnector.new_TQuery,args)
  #      self.thisown = 1
  #  def Open(*args): return apply(_adoconnector.TQuery_Open,args)
  #  def Close(*args): return apply(_adoconnector.TQuery_Close,args)
  #  def Exec(*args): return apply(_adoconnector.TQuery_Exec,args)
  #  def Get(*args): return apply(_adoconnector.TQuery_Get,args)
  #  def GetNull(*args): return apply(_adoconnector.TQuery_GetNull,args)
  #  def Put(*args): return apply(_adoconnector.TQuery_Put,args)
  #  def EndOfFile(*args): return apply(_adoconnector.TQuery_EndOfFile,args)
  #  def BeginOfFile(*args): return apply(_adoconnector.TQuery_BeginOfFile,args)
  #  def Next(*args): return apply(_adoconnector.TQuery_Next,args)
  #  def MoveFirst(*args): return apply(_adoconnector.TQuery_MoveFirst,args)
  #  def FileAndLine(*args): return apply(_adoconnector.TQuery_FileAndLine,args)
  #  def getInt(*args): return apply(_adoconnector.TQuery_getInt,args)
  #  def getLong(*args): return apply(_adoconnector.TQuery_getLong,args)
  #  def getDouble(*args): return apply(_adoconnector.TQuery_getDouble,args)
  #  def getString(*args): return apply(_adoconnector.TQuery_getString,args)
  #  def setInt(*args): return apply(_adoconnector.TQuery_setInt,args)
  #  def setLong(*args): return apply(_adoconnector.TQuery_setLong,args)
  #  def setDouble(*args): return apply(_adoconnector.TQuery_setDouble,args)
  #  def setString(*args): return apply(_adoconnector.TQuery_setString,args)
  #  def isNull(*args): return apply(_adoconnector.TQuery_isNull,args)
  #  def __del__(self, destroy= _adoconnector.delete_TQuery):
  #      try:
  #          if self.thisown: destroy(self)
  #      except: pass
  #  def __repr__(self):
  #      return "<C TQuery instance at %s>" % (self.this,)




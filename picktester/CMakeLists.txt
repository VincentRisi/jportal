project (jportal_picktester)

anydbMake(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb "${PICKTESTER_BINARY_DIR}/parm/picktester.bin")

anydbMake(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb "${PICKTESTER_BINARY_DIR}/sql/pgDDl/FieldSearch.sql")

set (psql_list
  QueueType Drivers SourceSystem StreamFieldsDef StreamMessageFormat Staff
  Currency BankFile AccountType Grps StreamType FieldSearchDef BlobData
  StreamFieldMsgRel StaffQueuePerm FileOutput FileInput StaffQueueConfig Message
  CountryCurrency Audits BankAccount QueueRecovery ScriptGroup StaffConfigName
  ScriptVersion Comments Lookup Routing FinidCorrespondentRouting Reply
  TestMessage StaffGroup BankCorrespondents AlmanacBank AlmanacCorrespondent
  FigCorrespondentBank StaffProfile TestPackMessage TestpackPayment TestPayment
  Scripts TestPack Response Dates StreamFields Country Persistent Summary
  Queue Streams Audittrail Fields FieldSearch
)

message (STATUS "COMMAND ${psqlExe} -h localhost -d postgres -U vince -a -e -f ... -o ...")
foreach (name ${psql_list})
  set (logFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.log)
  set (srcFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.sql)
  add_custom_command(
    OUTPUT  ${logFile}
    DEPENDS ${srcFile}
    COMMAND ${psqlExe} -h localhost -d postgres -U vince -a -e -f ${srcFile} -o ${logFile} >> ${logFile}
  )
  add_custom_target(
    psql_${name}_build ALL  
    DEPENDS ${logFile}
  )
endforeach()

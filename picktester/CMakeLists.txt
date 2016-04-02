project (jportal_picktester)

anydbMake2(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb)

anydbMake2(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb)

set (sql_list
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

set (user_name $ENV{USER})
message (STATUS "COMMAND ${psqlExe} -h localhost -d postgres -U ${user_name} -a -e -f ... -o ...")
foreach (name ${sql_list})
  set (logFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.log)
  set (srcFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.sql)
  add_custom_command(
    OUTPUT  ${logFile}
    DEPENDS ${srcFile}
    COMMAND ${psqlExe} -h localhost -d postgres -U ${user_name} -a -e -f ${srcFile} -o ${logFile} >> ${logFile}
  )
  add_custom_target(
    psql_${name}_build ALL  
    DEPENDS ${logFile}
  )
endforeach()

set (conn_str $ENV{VLAB_ORACONN})
set (sqlplusExe sqlplus)
message (STATUS "COMMAND ${sqlplusExe} ${conn_str} @${srcFile} > ${logFile}")
foreach (name ${sql_list})
  set (logFile ${PICKTESTER_BINARY_DIR}/sql/oraDDL/${name}.log)
  set (srcFile ${PICKTESTER_BINARY_DIR}/sql/oraDDL/${name}.sql)
  add_custom_command(
    OUTPUT  ${logFile}
    DEPENDS ${srcFile}
    COMMAND ${sqlplusExe} ${conn_str} @${srcFile} > ${logFile}
  )
  add_custom_target(
    sqlname_${name}_build ALL  
    DEPENDS ${logFile}
  )
endforeach()

project (jportal_picktester)

option (USE_PICKTESTER "Use picktester" OFF)
if (USE_PICKTESTER)
  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktester.cmake)
  anydbMake2(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb ${sources} ${targets})

  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktesterrest.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktesterrest.cmake)
  anydbMake2(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb ${sources} ${targets})
  
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
  
  option (USE_PICKTESTER_PSQL "Use picktester psql" OFF)
  if (USE_PICKTESTER_PSQL)
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
  endif ()
  
  option (USE_PICKTESTER_ORACLE "Use picktester ORACLE" OFF)
  if (USE_PICKTESTER_ORACLE)
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
        sqlplus_${name}_build ALL  
        DEPENDS ${logFile}
      )
    endforeach()
  endif ()

  option (USE_PICKTESTER_SQLITE "Use picktester SQLITE" OFF)
  if (USE_PICKTESTER_SQLITE)
    set (conn_str ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/vlab.db)
    set (lite3SQL ${CMAKE_SOURCE_DIR}/pyutils/lite3SQL.py)
    message (STATUS "COMMAND ${pythonExe} ${lite3SQL} -d ${conn_str} -v [srcFile] > [logFile]")
    foreach (name ${sql_list})
      set (logFile ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/${name}.log)
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/${name}.sql)
      add_custom_command(
        OUTPUT  ${logFile}
        DEPENDS ${srcFile}
        COMMAND COMMAND ${pythonExe} ${lite3SQL} -d ${conn_str} -v ${srcFile} > ${logFile}
      )
      add_custom_target(
        lite3SQL_${name}_build ALL
        DEPENDS ${logFile}
      )
    endforeach()
  endif ()

  if (WIN32)
    option (USE_PICKTESTER_PARAM_CONTROL "Use picktester param control" OFF)
    if (USE_PICKTESTER_PARAM_CONTROL)
      include_external_msproject(ParamControl,
        ${PICKTESTER_SOURCE_DIR}/ParamControl/ParamControl.csproj 
        TYPE "935D56EF-171A-46DB-987E-23421FB992C5"
        PLATFORM "Any CPU"
        jportal_picktester
      )
    endif ()
  endif ()

endif()

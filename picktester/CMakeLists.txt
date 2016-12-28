project (jportal_picktester)

option (USE_PICKTESTER "Use picktester" OFF)
if (USE_PICKTESTER)
  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktester.cmake)
  anydbMake2(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb ${sources} ${targets})

  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktesterrest.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktesterrest.cmake)
  anydbMake2(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb ${sources} ${targets})
  
  ## This list was manually created using depends.py - see pyutils
  set (depends_list
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
  
  option (USE_PICKTESTER_ORACLE "Use picktester ORACLE" OFF)
  if (USE_PICKTESTER_ORACLE)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_oracle.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_oracle.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_oracle.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_oracle.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_oracle.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    anydbMake2(jportal_picktester_oracle_bin ${PICKTESTER_SOURCE_DIR}/picktester_oracle.anydb ${sources} ${targets})
    set (conn_str $ENV{VLAB_ORACONN})
    set (sqlplusExe sqlplus)
    set (depends ${PICKTESTER_BINARY_DIR}/sql/oraDDL/depends.sql)
    file(WRITE ${depends})
    foreach (name ${depends_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/oraDDL/${name}.sql)
      file(APPEND ${depends} "@@ ${srcFile}\n")
    endforeach()
    set (logFile ${PICKTESTER_BINARY_DIR}/sql/oraDDL/${name}.log)
    message (STATUS "COMMAND ${sqlplusExe} ${conn_str} @${depends} > ${logFile}")
    add_custom_target (jportal_picktester_oracle_sql ALL
      ${sqlplusExe} ${conn_str} @${depends} > ${logFile}
      SOURCES ${depends} ${logfile}
    )
  endif ()

  option (USE_PICKTESTER_PSQL "Use picktester POSTGRES" OFF)
  if (USE_PICKTESTER_PSQL)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_postgres.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_postgres.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_postgres.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_postgres.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_postgres.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    set (depends ${PICKTESTER_BINARY_DIR}/sql/pgDDL/depends.sql)
    file(WRITE ${depends})
    foreach (name ${depends_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.sql)
      file(APPEND ${depends} "\\i ${srcFile}\n")
    endforeach()
    anydbMake2(jportal_picktester_postgres_bin ${PICKTESTER_SOURCE_DIR}/picktester_postgres.anydb ${sources} ${targets})
    set (user_name $ENV{USER})
    set (logFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.log)
    add_custom_target (jportal_picktester_postgres_sql ALL
      ${psqlExe} -h localhost -d postgres -U ${user_name} -a -e -f ${filelist} -o ${logFile} >> ${logFile}	
      SOURCES ${depends} ${logfile}
    )
  endif ()
  
  option (USE_PICKTESTER_SQLITE "Use picktester SQLITE" OFF)
  if (USE_PICKTESTER_SQLITE)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    anydbMake2(jportal_picktester_sqlite3_bin ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb ${sources} ${targets})
    set (conn_str ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/vlab.db)
    set (lite3SQL ${CMAKE_SOURCE_DIR}/pyutils/lite3SQL.py)
    set (filelist ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/file.lst)
    set (filelog ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/file.log)
    message (STATUS "COMMAND ${pythonExe} ${lite3SQL} -d ${conn_str} -f ${filelist} -v > ${filelog}")
    file(WRITE ${filelist})
    set (sources)
    foreach (name ${depends_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/${name}.sql)
      file(APPEND ${filelist} "${srcFile}\n")
      list(APPEND sources ${srcFile})
    endforeach()
    add_custom_target (jportal_picktester_sqlite3_sql ALL
      ${pythonExe} ${lite3SQL} -d ${conn_str} -f ${filelist} -v > ${filelog}
      SOURCES ${sources} ${filelist} ${filelog}
    )
  endif ()

  option (USE_PICKTESTER_MSSQL "Use picktester MSSQL" OFF)
  if (USE_PICKTESTER_MSSQL)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    set (depends ${PICKTESTER_BINARY_DIR}/sql/msDDL/depends.sql)
    file(WRITE ${depends} "USE vlab;\n")
    foreach (name ${depends_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/msDDL/${name}.sql)
      file(APPEND ${depends} ":r ${srcFile}\n")
    endforeach()
    anydbMake2(jportal_picktester_mssql_bin ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb ${sources} ${targets} ${depends})
  endif ()

  option (USE_PICKTESTER_MYSQL "Use picktester MYSQL" OFF)
  if (USE_PICKTESTER_MYSQL)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mysql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mysql.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mysql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mysql.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_mysql.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    anydbMake2(jportal_picktester_mysql_bin ${PICKTESTER_SOURCE_DIR}/picktester_mysql.anydb ${sources} ${targets})
    set (depends ${PICKTESTER_BINARY_DIR}/sql/myDDL/depends.sql)
    file(WRITE ${depends})
    foreach (name ${depends_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/myDDL/${name}.sql)
      file(APPEND ${depends} "\\. ${srcFile};\n")
    endforeach()
    add_custom_target (jportal_picktester_mysql_sql ALL
      echo "TBD mysql db_name < ${depends} > output.tab"
      SOURCES ${depends}
    )
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

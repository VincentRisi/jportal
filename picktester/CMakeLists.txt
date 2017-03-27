project (jportal_picktester)

option (USE_PICKTESTER "Use picktester" OFF)
if (USE_PICKTESTER)
  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktester.cmake)
  anydbMake2(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb ${sources} ${targets})

  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktesterrest.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb)
  include (${PICKTESTER_BINARY_DIR}/picktesterrest.cmake)
  anydbMake2(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb ${sources} ${targets})

  ## These list was manually created using depends.py - see pyutils
  set (buildorder_list
    AccountType Audits BankFile Currency Drivers FieldSearch FileInput Grps
    Persistent QueueType ScriptGroup SourceSystem StreamFields
    StreamMessageFormat StreamType TestMessage TestPackMessage TestPayment
    AlmanacBank BankAccount Country Dates FieldSearchDef FileOutput Lookup Queue
    Routing ScriptVersion StreamFieldsDef TestPack AlmanacCorrespondent
    BankCorrespondents CountryCurrency FigCorrespondentBank Message Reply
    StreamFieldMsgRel Summary AuditTrail Comments
    FinidCorrespondentRouting Staff StaffGroup StaffQueueConfig Streams BlobData
    QueueRecovery StaffConfigName StaffQueuePerm Fields StaffProfile Response
    TestpackPayment
  )

  set (droporder_list
    TestpackPayment Response StaffProfile Fields StaffQueuePerm StaffConfigName
    QueueRecovery BlobData Streams StaffQueueConfig StaffGroup Staff
    FinidCorrespondentRouting Comments AuditTrail Summary StreamFieldMsgRel
    Reply Message FigCorrespondentBank CountryCurrency
    BankCorrespondents AlmanacCorrespondent TestPack StreamFieldsDef ScriptVersion
    Routing Queue Lookup FileOutput FieldSearchDef Dates Country BankAccount
    AlmanacBank TestPayment TestPackMessage TestMessage StreamType
    StreamMessageFormat StreamFields SourceSystem ScriptGroup QueueType
    Persistent Grps FileInput FieldSearch Drivers Currency BankFile Audits
    AccountType
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
    foreach (name ${buildorder_list})
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
    foreach (name ${buildorder_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/${name}.sql)
      file(APPEND ${depends} "\\i ${srcFile}\n")
    endforeach()
    set (drops ${PICKTESTER_BINARY_DIR}/sql/pgDDL/drops.sql)
    file(WRITE ${drops})
    foreach (name ${droporder_list})
      file(APPEND ${drops} "DROP TABLE ${name} CASCADE;\n")
    endforeach()
    anydbMake2(jportal_picktester_postgres_bin ${PICKTESTER_SOURCE_DIR}/picktester_postgres.anydb ${sources} ${targets} ${drops})
    set(ENV{PGPASSWORD} postgres)
    set (user_name "postgres")
    set (logFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/build.log)
    file (WRITE ${logFile})
    set (lstFile ${PICKTESTER_BINARY_DIR}/sql/pgDDL/build.lst)
    file (WRITE ${lstFile})
    message (STATUS "${psqlExe} -h localhost -d postgres -U ${user_name} -a -e -f ${depends} -o ${logFile}")
    add_custom_target (jportal_picktester_postgres_sql ALL
      ${psqlExe} -h localhost -d postgres -U ${user_name} -a -e -f ${depends} -o ${logFile} >> ${lstFile}
      SOURCES ${depends} ${logfile} ${lstFile}
    )
  endif ()

  option (USE_PICKTESTER_SQLITE "Use picktester SQLITE" OFF)
  if (USE_PICKTESTER_SQLITE)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_sqlite.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    anydbMake2(jportal_picktester_sqlite3_bin ${PICKTESTER_SOURCE_DIR}/picktester_sqlite.anydb ${sources} ${targets})
    set (conn_str ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/bbd.db)
    set (lite3SQL ${CMAKE_SOURCE_DIR}/pyutils/lite3SQL.py)
    set (depends ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/file.lst)
    set (lstfile ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/file.log)
    message (STATUS "COMMAND ${pythonExe} ${lite3SQL} -d ${conn_str} -f ${depends} -v > ${lstfile}")
    file(WRITE ${depends})
    set (sources)
    foreach (name ${buildorder_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/lt3DDL/${name}.sql)
      file(APPEND ${depends} "${srcFile}\n")
      list(APPEND sources ${srcFile})
    endforeach()
    add_custom_target (jportal_picktester_sqlite3_sql ALL
      ${pythonExe} ${lite3SQL} -d ${conn_str} -f ${depends} -v > ${lstfile}
      SOURCES ${sources} ${depends} ${lstfile}
    )
  endif ()

  option (USE_PICKTESTER_MSSQL "Use picktester MSSQL" OFF)
  if (USE_PICKTESTER_MSSQL)
  	message (STATUS "COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb")
    execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake -S -T ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb)
    include (${PICKTESTER_BINARY_DIR}/picktester_mssql.cmake)
    set_source_files_properties (${sources} PROPERTIES GENERATED TRUE)
    set (depends ${PICKTESTER_BINARY_DIR}/sql/msDDL/depends.sql)
    file(WRITE ${depends} "USE bbd;\n")
    foreach (name ${buildorder_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/msDDL/${name}.sql)
      file(APPEND ${depends} ":!! echo ${srcFile}\n")
      file(APPEND ${depends} ":r ${srcFile}\n")
    endforeach()
    set (drops ${PICKTESTER_BINARY_DIR}/sql/msDDL/drops.sql)
    file(WRITE ${drops} "USE bbd;\n")
    foreach (name ${droporder_list})
      file(APPEND ${drops} "IF OBJECT_ID('${name}','U') IS NOT NULL\n")
      file(APPEND ${drops} "  DROP TABLE ${name};\n")
    endforeach()
    anydbMake2(jportal_picktester_mssql_bin ${PICKTESTER_SOURCE_DIR}/picktester_mssql.anydb ${sources} ${targets} ${depends} ${drops})
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
    foreach (name ${buildorder_list})
      set (srcFile ${PICKTESTER_BINARY_DIR}/sql/myDDL/${name}.sql)
      file(APPEND ${depends} "\\. ${srcFile}\n")
    endforeach()
    set (drops ${PICKTESTER_BINARY_DIR}/sql/myDDL/drops.sql)
    file(WRITE ${drops})
    foreach (name ${droporder_list})
      file(APPEND ${drops} "DROP TABLE IF EXISTS ${name};\n")
    endforeach()
    set (lstFile ${PICKTESTER_BINARY_DIR}/sql/myDDL/build.lst)
    file (WRITE ${lstFile})
    set (mysqlExe "C:/MariaDB/bin/mysql.exe")
    message (STATUS "${mysqlExe} --user=root --password=mariadb mysql < ${drops} >> ${lstFile}")
    message (STATUS "${mysqlExe} --user=root --password=mariadb mysql < ${depends} >> ${lstFile}")
    add_custom_target (jportal_picktester_mysql_sql ALL
      COMMAND ${mysqlExe} --user=root --password=mariadb mysql < ${drops} >> ${lstFile}
      COMMAND ${mysqlExe} --user=root --password=mariadb mysql < ${depends} >> ${lstFile}
      SOURCES ${depends} ${drops}
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

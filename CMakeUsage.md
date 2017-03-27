# CMake usage - here is the basic way to build the sources.

We use cmake to build more than just *C/C++* source.
The following functions are a big help here.

## functions.cmake

    set (jportalJar ${GENERATORS_SOURCE_DIR}/bin/jportal.jar)
    set (crackleJar ${GENERATORS_SOURCE_DIR}/bin/crackle.jar)
    set (pickleJar  ${GENERATORS_SOURCE_DIR}/bin/pickle.jar)
    set (anydbMake ${TOOLS_DIR}/anydbMake.py)

    if (WIN32) #TBD - use cmake to find them
      message (STATUS "Setup link or junction for PostgreSQL and JavaJDK")
      set (pythonExe c:/python27/python.exe)
      set (psqlExe "c:/PostgeSQL/bin/psql.exe")
      set (javaExe "c:/JavaJDK/bin/java.exe")
      set (jarExe  "c:/JavaJDK/bin/jar.exe")
    else ()
      set (pythonExe /usr/bin/python)
      set (psqlExe /usr/bin/psql)
      set (javaExe /usr/bin/java)
      set (jarExe /usr/bin/jar)
    endif ()

    function (pathed result ext_dir)
      foreach (arg ${ARGN})
        if (${ARGN} STREQUAL REMOVE)
          file (GLOB remFiles ${ext_dir}/*.*)
          list (LENGTH remFiles count)
          if (0 LESS count) 
            message (STATUS "Removing all files in ${ext_dir}")
            file (REMOVE ${remFiles})
          endif ()
        endif ()
      endforeach ()
      file(MAKE_DIRECTORY ${ext_dir})
      set ("${result}" ${ext_dir} PARENT_SCOPE)
    endfunction ()

    function (jportal projectName siFiles)
      set (switches)
      foreach (arg ${ARGN})
        list(APPEND switches "${arg}")
      endforeach ()
      set (sqlFiles)
      foreach (siFile ${siFiles})
        get_filename_component (temp ${siFile} NAME)
        string (TOLOWER ${temp} temp1)
        get_filename_component (filename ${temp1} NAME_WE)
        set (sqlFile ${sqlDir}/${filename}.sql)
        list (APPEND sqlFiles ${sqlFile})
        add_custom_command (
          OUTPUT  ${sqlFile}
          COMMAND ${javaExe} -jar ${jportalJar} ${siFile} ${switches}
          DEPENDS ${siFile}
          VERBATIM
        )
      endforeach ()
      set (sqlFiles ${sqlFiles} PARENT_SCOPE)
      add_custom_target (${projectName} ALL
        DEPENDS ${sqlFiles} 
        SOURCES ${siFiles}
      )
    endfunction()

    function (crackle projectName imFile idlDir iiDir ibDir)
      set (switches)
      foreach (arg ${ARGN})
        list(APPEND switches "${arg}")
      endforeach ()
      get_filename_component(temp ${imFile} NAME)
      get_filename_component(filename ${temp} NAME_WE)
      set (idlFile ${idlDir}/${filename}.idl2)
      file (GLOB ibFiles ${ibDir}/*.ib)
      file (GLOB iiFiles ${iiDir}/*.ii)
      add_custom_command(
        OUTPUT ${idlFile}
        COMMAND ${javaExe} -jar ${crackleJar} -i ${iiDir} -b ${ibDir} -f ${idlFile} ${imFile} ${switches}
        DEPENDS ${imFile} ${ibFiles} ${iiFiles}
        VERBATIM
      )
      add_custom_target (${projectName} ALL
        DEPENDS ${idlFile} 
        SOURCES ${imFile} ${ibFiles} ${iiFiles} ${idlFile}
      )
    endfunction()

    function (anydbMake projectName anydbMakeFile targetFiles)
      add_custom_command(
        OUTPUT ${targetFiles}
        COMMAND ${pythonExe} ${anydbMake} -c ${crackleJar} -j ${jportalJar} -p ${pickleJar} -v ${anydbMakeFile}
        DEPENDS ${anydbMakeFile}
        VERBATIM
      )
      add_custom_target (${projectName} ALL
        DEPENDS ${targetFiles} 
        SOURCES ${anydbMakeFile}
      )
    endfunction ()

Here we define the starting cmake file for the project.
We include the above functions for use with added the subdirectories. 

## jportal

    project (main_jportal)
    
    CMAKE_MINIMUM_REQUIRED (VERSION 2.8)
    
    set (TOOLS_DIR ${CMAKE_SOURCE_DIR}/tools)
    
    set (GENERATORS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/generators)
    set (GENERATORS_BINARY_DIR ${CMAKE_BINARY_DIR}/generators)
    
    set (LOYALTY_SOURCE_DIR ${CMAKE_SOURCE_DIR}/loyalty)
    set (LOYALTY_BINARY_DIR ${CMAKE_BINARY_DIR}/loyalty)
    
    set (IDL2TESTER_SOURCE_DIR ${CMAKE_SOURCE_DIR}/idl2tester)
    set (IDL2TESTER_BINARY_DIR ${CMAKE_BINARY_DIR}/idl2tester)
    
    set (PICKTESTER_SOURCE_DIR ${CMAKE_SOURCE_DIR}/picktester)
    set (PICKTESTER_BINARY_DIR ${CMAKE_BINARY_DIR}/picktester)
    
    set (ANYDBTEST_SOURCE_DIR ${CMAKE_SOURCE_DIR}/anydbtest)
    set (ANYDBTEST_BINARY_DIR ${CMAKE_BINARY_DIR}/anydbtest)
    
    set (BINUC_SOURCE_DIR ${CMAKE_SOURCE_DIR}/binuc)
    set (BINUC_BINARY_DIR ${CMAKE_BINARY_DIR}/binuc)
    
    set (BINUJ_SOURCE_DIR ${CMAKE_SOURCE_DIR}/binuj)
    set (BINUJ_BINARY_DIR ${CMAKE_BINARY_DIR}/binuj)
    
    include (${CMAKE_SOURCE_DIR}/cmake/functions.cmake)
    
    add_subdirectory(generators)
    add_subdirectory(loyalty)
    add_subdirectory(idl2tester)
    add_subdirectory(binuc)
    add_subdirectory(binuj)
    add_subdirectory(picktester)
    add_subdirectory(demo)

This cmake builds the generator jar files. 
The java source for these jars are maintained using Eclipse.
It is also a good example of how add_custom_target works in cmake.
We include a subdirectory for a database decompiler application used from jportal.

## jportal/generators

    project (jportal_generators)
    
    set (class_bin_dir ${GENERATORS_SOURCE_DIR}/bin)
    
    add_subdirectory(bbd/jportal/decompiler)
    
    set (wild_card 
      bbd/jportal/*.class 
      bbd/jportal/decompiler/*.class 
      bbd/jportal/util/*.class
    )
    set (jportal_jar    ${class_bin_dir}/jportal.jar)
    set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/jportal/Manifest.txt)
    message (STATUS "COMMAND ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}")
    add_custom_target (target_jportal_jar ALL
      ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}
      WORKING_DIRECTORY ${class_bin_dir}
    )
    
    set (wild_card 
      bbd/crackle/*.class 
      bbd/crackle/rdc/*.class 
      bbd/crackle/rpc/*.class 
      bbd/crackle/rw/*.class
      bbd/crackle/util/*.class
    )
    set (crackle_jar ${class_bin_dir}/crackle.jar)
    set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/crackle/Manifest.txt)
    message (STATUS "COMMAND ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}")
    add_custom_target (target_crackle_jar ALL
      ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}
      WORKING_DIRECTORY ${class_bin_dir}
    )
    
    set (wild_card bbd/pickle/*.class)
    set (pickle_jar ${class_bin_dir}/pickle.jar)
    set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/pickle/Manifest.txt)
    message (STATUS "COMMAND ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}")
    add_custom_target (target_pickle_jar ALL
      ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}
      WORKING_DIRECTORY ${class_bin_dir}
    )

This is an example of using anydbMake within cmake.
It should be noted that anydbMake is a special make system for the jportal generators.
The is a markdown document describing the anydbMake, viz. anydbMake.md.

## jportal/generators/bbd/jportal/decompiler

    project (jportal_decompiler)
    
    anydbMake(jportal_decompiler ${CMAKE_CURRENT_SOURCE_DIR}/decompiler.anydb "${CMAKE_CURRENT_SOURCE_DIR}/Oracle.java")

This is an example of using the generators not using anydbMake but invoking jportal.jar and crackle.jar using the cmake functions described above.

## jportal/loyalty

    project (jportal_loyalty)
    
    pathed (javaDir   ${LOYALTY_BINARY_DIR}/loyalty/xyz)
    pathed (javaWRDir ${LOYALTY_BINARY_DIR}/java)
    pathed (idljDir   ${LOYALTY_BINARY_DIR}/idlj)
    pathed (idlDir    ${LOYALTY_BINARY_DIR}/idl)
    pathed (jidlDir   ${LOYALTY_BINARY_DIR}/jidl)
    pathed (jtreeDir  ${LOYALTY_BINARY_DIR}/jtree)
    
    set (si_files
      ${LOYALTY_SOURCE_DIR}/loyalty_code_lookup.si
    )
    
    set (switches 
      "-o" "${javaWRDir}" "JavaWRCode"
      "-o" "${jidlDir}"   "JavaIdlCode"  
      "-o" "${jtreeDir}"  "PythonTreeCode"  
    )
    
    jportal (loyalty_jportal "${si_files}" ${switches})
    
    pathed (jserv    ${LOYALTY_BINARY_DIR}/jserv)
    pathed (jclient  ${LOYALTY_BINARY_DIR}/jclient)
    pathed (idlDir   ${LOYALTY_BINARY_DIR}/idl2)
    
    set (im_file ${LOYALTY_SOURCE_DIR}/loyalty.im)
    set (ib_dir  ${LOYALTY_SOURCE_DIR}/ib)
    
    set (switches 
      "-o" "${jserv}"   "PopUbiJavaServer"
      "-o" "${jclient}" "PopUbiJavaClient"
    )
    
    crackle (loyalty_crackle ${im_file} ${idlDir} ${jidlDir} ${ib_dir} ${switches})
    
    add_dependencies(loyalty_jportal target_jportal_jar)
    add_dependencies(loyalty_crackle target_crackle_jar)

Idl2 is a client/server system which has been used very successfully for C# front end clients talking to C++ or Java services.
Idl2tester is a small subsystem to test out the generators. 
It uses arbitrary named tables and functions which could never be confused to be real world applications.

## jportal/idl2tester

    project (jportal_idl2tester)
    
    anydbMake(jportal_idl2tester ${IDL2TESTER_SOURCE_DIR}/idl2tester.anydb "${IDL2TESTER_BINARY_DIR}/idl/idl2tester.idl")

## jportal/binuc

    project (jportal_binuc)
    
    anydbMake(jportal_binuc ${BINUC_SOURCE_DIR}/binuc.anydb "${BINUC_BINARY_DIR}/idl/server/binucserver.cpp")
    
    add_subdirectory(coco)

CocoR is a very nice parser generator. The cmake here builds it using standard cmake c++ executable building.

## jportal/binuc/coco

    project (exe_coco)
    
    set (source
      ${BINUC_SOURCE_DIR}/coco/Action.cpp
      ${BINUC_SOURCE_DIR}/coco/ArrayList.cpp
      ${BINUC_SOURCE_DIR}/coco/BitArray.cpp
      ${BINUC_SOURCE_DIR}/coco/CharClass.cpp
      ${BINUC_SOURCE_DIR}/coco/CharSet.cpp
      ${BINUC_SOURCE_DIR}/coco/Coco.cpp
      ${BINUC_SOURCE_DIR}/coco/Comment.cpp
      ${BINUC_SOURCE_DIR}/coco/DFA.cpp
      ${BINUC_SOURCE_DIR}/coco/Generator.cpp
      ${BINUC_SOURCE_DIR}/coco/HashTable.cpp
      ${BINUC_SOURCE_DIR}/coco/Melted.cpp
      ${BINUC_SOURCE_DIR}/coco/Node.cpp
      ${BINUC_SOURCE_DIR}/coco/Parser.cpp
      ${BINUC_SOURCE_DIR}/coco/ParserGen.cpp
      ${BINUC_SOURCE_DIR}/coco/Position.cpp
      ${BINUC_SOURCE_DIR}/coco/Scanner.cpp
      ${BINUC_SOURCE_DIR}/coco/SortedList.cpp
      ${BINUC_SOURCE_DIR}/coco/State.cpp
      ${BINUC_SOURCE_DIR}/coco/StringBuilder.cpp
      ${BINUC_SOURCE_DIR}/coco/Symbol.cpp
      ${BINUC_SOURCE_DIR}/coco/Tab.cpp
      ${BINUC_SOURCE_DIR}/coco/Target.cpp
    )
    
    add_executable (Coco ${source})

## jportal/binuj

    project (jportal_binuj)
    
    anydbMake(jportal_binuj ${BINUJ_SOURCE_DIR}/binuj.anydb "${BINUJ_BINARY_DIR}/bbd/binu/BinuImpl.java")
    
    set (cocoJar ${CMAKE_SOURCE_DIR}/support/Coco.jar)
    set (parser_dir ${BINUJ_BINARY_DIR}/bbd/binu/parser)
    file (MAKE_DIRECTORY ${parser_dir})
    
    set (generated
      ${parser_dir}/Parser.java
      ${parser_dir}/Scanner.java
    )
    
    set (source
      ${BINUJ_SOURCE_DIR}/clibin.atg
      ${BINUJ_SOURCE_DIR}/Parser.frame
      ${BINUJ_SOURCE_DIR}/Scanner.frame
    )
    
    add_custom_command (
      OUTPUT  ${generated}
      COMMAND ${javaExe} -jar ${cocoJar} ${BINUJ_SOURCE_DIR}/clibin.atg -o ${parser_dir}
      DEPENDS ${source}
    )
    
    add_custom_target (binu_java_coco ALL
      DEPENDS ${generated}
      SOURCES ${source} ${generated}
    )

The sql_list has the table names used for the database generation is dependancy order.

## jportal/picktester

    project (jportal_picktester)
    
    anydbMake(jportal_picktester ${PICKTESTER_SOURCE_DIR}/picktester.anydb "${PICKTESTER_BINARY_DIR}/parm/picktester.bin")
    
    anydbMake(jportal_picktester_rest ${PICKTESTER_SOURCE_DIR}/picktesterrest.anydb "${PICKTESTER_BINARY_DIR}/sql/pgDDl/FieldSearch.sql")
    
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

## jportal/demos

    project (jportal_demos)
    
    anydbMake(jportal_mcdemo ${CMAKE_CURRENT_SOURCE_DIR}/mcdemo.anydb "${CMAKE_CURRENT_BINARY_DIR}/kamcodes.cs")
    
    anydbMake(jportal_ojdemo ${CMAKE_CURRENT_SOURCE_DIR}/ojdemo.anydb "${CMAKE_CURRENT_BINARY_DIR}/Master.java")

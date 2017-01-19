set (jportalJar ${GENERATORS_SOURCE_DIR}/bin/jportal.jar)
set (crackleJar ${GENERATORS_SOURCE_DIR}/bin/crackle.jar)
set (pickleJar  ${GENERATORS_SOURCE_DIR}/bin/pickle.jar)
set (anydbMake ${TOOLS_DIR}/anydbMake.py)
set (lite3SQL  ${CMAKE_SOURCE_DIR}/pyutils/lite3SQL.py)

set (antExe ${ANT_EXECUTABLE})
set (javaExe ${Java_JAVA_EXECUTABLE})
set (jarExe ${Java_JAR_EXECUTABLE})
set (psqlExe ${POSTGRES_EXECUTABLE})
set (pythonExe ${PYTHON_EXECUTABLE})
set (mysqlExe ${MYSQL_EXECUTABLE})

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

function (anydbMake2 projectName anydbMakeFile)
  add_custom_target (${projectName} ALL
    ${pythonExe} ${anydbMake} -c ${crackleJar} -j ${jportalJar} -p ${pickleJar} -v ${anydbMakeFile}
    SOURCES ${anydbMakeFile} ${ARGN}
  )
endfunction ()

function (antMake projectName buildFile)
  add_custom_target (${projectName} ALL
    ${antExe} -f ${buildFile}
    SOURCES ${buildFile}
  )
endfunction ()

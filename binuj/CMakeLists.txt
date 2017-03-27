project (jportal_binuj)

option (USE_BINUJ "Use generic bin java server" OFF)

if (USE_BINUJ)

  set (sources
    ${BINUJ_SOURCE_DIR}/idl/binuj.idl
  )
  
  anydbMake2(jportal_binuj ${BINUJ_SOURCE_DIR}/binuj.anydb ${sources})
  
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

endif ()

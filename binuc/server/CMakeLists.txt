project (binu_server)
  
if (TO_BE_IN_USE)
  
  if (NOT ADD_SILO_PYSCRIPT)
    message (FATAL_ERROR "This project requires that the pyscript SILO be present")
  endif ()
  
  source_group ("Configs"  REGULAR_EXPRESSION ".*[.]xml$")
  
  include_directories (
    ${BINU_BINARY_DIR}/cpp
    ${BINU_BINARY_DIR}/cpp/coco  
    ${BASE_SOURCE_DIR}/c/libti/libs/portal/cli9/src
  )
  
  set (generated
    ${BINU_BINARY_DIR}/cpp/binu.cpp
    ${BINU_BINARY_DIR}/cpp/binuserver.cpp
    ${BINU_BINARY_DIR}/cpp/binuimpl.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Parser.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Scanner.cpp
  )
  
  set_source_files_properties (${generated} PROPERTIES GENERATED TRUE)
  
  set (source
    ${generated}
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/base64.cpp
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/base64.h
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/ulog.cpp
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/ulog.h
    ${BASE_SOURCE_DIR}/c/libti/libs/portal/cli9/src/clibinu.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Parser.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Scanner.cpp
    ${BINU_SOURCE_DIR}/server/config.xml
    ${BINU_SOURCE_DIR}/server/aixconfig.xml
  )
  
  add_executable(binuserver ${source})
  
  set(linkLibs
    libcli9
    libidlubi
    libbbd
  )
  
  if (WIN32)
    list (APPEND linkLibs ws2_32 winmm)
  elseif (LINUX)    
    list (APPEND linkLibs db2 dl)
  else()
    list (APPEND linkLibs db2)
  endif()
  
  target_link_libraries (binuserver ${linkLibs})
  
  add_dependencies (binuserver binu_cpp_coco)
  
  install(TARGETS binuserver RUNTIME DESTINATION bin/servers COMPONENT servers)
  
  set_target_properties(binuserver PROPERTIES PROJECT_LABEL binu_server)

endif ()  
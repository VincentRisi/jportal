project(binu_module)

if (TO_BE_USED)

  include_directories (
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics
    ${BINU_BINARY_DIR}/cpp
    ${BINU_BINARY_DIR}/cpp/coco  
    ${BINU_BINARY_DIR}/python
  )
  
  set (generated
    ${BINU_BINARY_DIR}/cpp/binuimpl.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Parser.cpp
    ${BINU_BINARY_DIR}/cpp/coco/Scanner.cpp
    ${BINU_BINARY_DIR}/python/binuPython.cpp
  )
  
  set_source_files_properties (${generated} PROPERTIES GENERATED TRUE)
  
  set(source
    ${generated}
    ${BASE_SOURCE_DIR}/c/libti/libs/portal/cli9/src/clibinu.cpp
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/pythonbuild.cpp
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/pythonbuild.h
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/pythontraceback.cpp
    ${PYSCRIPT_SOURCE_DIR}/c/intrinsics/pythontraceback.h
  )
  
  set(linkLibs
    libcli9
    libidlubi
    libbbd
  )
  
  add_definitions(-D"OS_DECLSPEC=\"\"")
  list(APPEND linkLibs ${PYTHON_PIC_LIBRARIES})
  
  add_library(binuMethods SHARED ${source})
  
  set_target_properties(binuMethods PROPERTIES PREFIX "" PROJECT_LABEL binu_module)
  
  add_dependencies(binuMethods binu_gen_idl2)
  
  target_link_libraries(binuMethods ${linkLibs})
  
  install(TARGETS binuMethods LIBRARY DESTINATION bin/pyextensions ARCHIVE DESTINATION bin/pyextensions COMPONENT pyextensions)

endif ()

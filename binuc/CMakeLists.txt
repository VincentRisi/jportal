project (jportal_binuc)

option (USE_BINUC "Use generic bin cpp idl2 server" OFF)

if (USE_BINUC)

  set (sources ${BINUC_SOURCE_DIR}/idl/binuc.idl)
  anydbMake2(jportal_binuc ${BINUC_SOURCE_DIR}/binuc.anydb ${sources})
  add_subdirectory(coco)

endif ()

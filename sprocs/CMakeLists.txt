project (jportal_sprocs)

option (USE_SPROCS "Use sprocs" OFF)

if (USE_SPROCS)

  anydbMake2(jportal_sprocs ${SPROCS_SOURCE_DIR}/sprocs.anydb)
  
endif ()  

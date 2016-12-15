project (jportal_sprocs)

option (USE_SPROCS "Use sprocs" OFF)

if (USE_SPROCS)

  set (sources 
  	${SPROCS_SOURCE_DIR}/sql/usp.si
  )
  anydbMake2(jportal_sprocs ${SPROCS_SOURCE_DIR}/sprocs.anydb ${sources})
  #add_custom_target (jportal_sprocs_source ALL SOURCES ${sources})
  #add_dependencies (jportal_sprocs jportal_sprocs_source)
  
endif ()  

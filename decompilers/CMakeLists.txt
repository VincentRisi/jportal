project (jportal_decompiler)

option (USE_DECOMPILERS "Use decompilers" OFF)

if (USE_DECOMPILERS)

  set (sources 
  	${CMAKE_CURRENT_SOURCE_DIR}/sql/si/oracle.si
  	${CMAKE_CURRENT_SOURCE_DIR}/java/bbd/jportal/decompiler/OracleRE.java
  ) 
  anydbMake2(jportal_decompiler ${CMAKE_CURRENT_SOURCE_DIR}/decompiler.anydb ${sources})

  add_subdirectory(java)

endif()

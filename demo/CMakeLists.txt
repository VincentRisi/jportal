project (jportal_demos)

option (USE_DEMOS "Use demos" OFF)

if (USE_DEMOS)

  anydbMake2(jportal_mcdemo ${CMAKE_CURRENT_SOURCE_DIR}/mcdemo.anydb "${CMAKE_CURRENT_SOURCE_DIR}/kamcodes.si")
  
  anydbMake2(jportal_ojdemo ${CMAKE_CURRENT_SOURCE_DIR}/ojdemo.anydb "${CMAKE_CURRENT_SOURCE_DIR}/master.si")

endif ()

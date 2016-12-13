project (jportal_anydbtest)

option (USE_ANYDBTEST "Use anydbtest" OFF)
if (USE_ANYDBTEST)
  anydbMake2(jportal_anydbtest ${ANYDBTEST_SOURCE_DIR}/tester.anydb)
endif ()  

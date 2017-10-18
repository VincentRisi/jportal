project (jportal_anydbtest)

option (USE_ANYDBTEST "Use anydbtest" OFF)
if (USE_ANYDBTEST)

  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${ANYDBTEST_BINARY_DIR}/tester.cmake -S -T ${ANYDBTEST_SOURCE_DIR}/tester.anydb)
  include (${ANYDBTEST_BINARY_DIR}/tester.cmake)
  
  anydbMake2(jportal_anydbtest ${ANYDBTEST_SOURCE_DIR}/tester.anydb ${sources} ${targets})

endif ()  

project (jportal_anydbtest)

option (USE_ANYDBTEST "Use anydbtest" OFF)
if (USE_ANYDBTEST)

  set (source_dir ${IDL2TESTER_SOURCE_DIR})
  set (sources
    ${source_dir}/sql/si/ZedZed.si
    ${source_dir}/sql/si/ZedZedBiz.si
    ${source_dir}/sql/si/ZedZedBled.si
    ${source_dir}/sql/si/ZedZedClobs.si
    ${source_dir}/sql/si/ZhedIsDhed.si
    ${source_dir}/idl/idl2tester.im
    ${source_dir}/idl/wallap.ib
  )
  
  anydbMake2(jportal_anydbtest ${ANYDBTEST_SOURCE_DIR}/tester.anydb)
  add_custom_target (jportal_anydbtest_source ALL SOURCES ${sources})
  add_dependencies (jportal_anydbtest jportal_anydbtest_source)

endif ()  

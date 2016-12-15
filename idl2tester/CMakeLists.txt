project (jportal_idl2tester)

option (USE_IDL2TESTER "Use idl2tester" OFF)

if (USE_IDL2TESTER)
  set (sources
    ${IDL2TESTER_SOURCE_DIR}/sql/si/ZedZed.si
    ${IDL2TESTER_SOURCE_DIR}/sql/si/ZedZedBiz.si
    ${IDL2TESTER_SOURCE_DIR}/sql/si/ZedZedBled.si
    ${IDL2TESTER_SOURCE_DIR}/sql/si/ZedZedClobs.si
    ${IDL2TESTER_SOURCE_DIR}/sql/si/ZhedIsDhed.si
    ${IDL2TESTER_SOURCE_DIR}/idl/idl2tester.im
    ${IDL2TESTER_SOURCE_DIR}/idl/wallap.ib
  )	
  anydbMake2(jportal_idl2tester ${IDL2TESTER_SOURCE_DIR}/idl2tester.anydb ${sources})
endif ()

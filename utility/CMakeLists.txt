project (jportal_utility)

option (USE_UTILITY "Use utility" ON)

if (USE_UTILITY)

  set (source dbxsrc.cpp)

  add_executable (dbxsrc dbxsrc.cpp)
  set_target_properties (dbxsrc PROPERTIES PROJECT_LABEL utility.dbxsrc)

endif ()

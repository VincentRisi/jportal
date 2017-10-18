project (portal_cli9)

option (ADD_LIB_PORTAL_CLI9 "Add lib portal cli9" ON)

if (ADD_LIB_PORTAL_CLI9) 
  include_directories (
    ${INCLUDE_TI}
  )
  add_library(cli9
    ${LIBS_SOURCE_DIR}/portal/cli9/src/cliapi.cpp 
  )
  set_target_properties (cli9 PROPERTIES PROJECT_LABEL libs_portal_cli9)
  target_link_libraries(cli9 ti)
  add_dependencies(cli9 padgen)
endif ()  


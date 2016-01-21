project (portal_pg8)

option (ADD_LIB_PORTAL_PG8 "Add lib portal posgres 8" OFF)

if (ADD_LIB_PORTAL_PG8) 
  include_directories(
    ${INCLUDE_TI}
    ${INCLUDE_PG8}
  )
  
  ADD_LIBRARY(pg8
    ${LIBS_SOURCE_DIR}/portal/pg8/src/pgapi.cpp 
    ${LIBS_SOURCE_DIR}/portal/pg8/src/pgaspect.cpp
  )
  
  set_target_properties (pg8 PROPERTIES PROJECT_LABEL libs_portal_pg8)
  
  target_link_libraries(pg8 ti)
  
  add_dependencies(pg8 padgen)

endif ()
  
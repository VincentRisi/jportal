project (portal_oci11)

option (ADD_LIB_PORTAL_OCI11 "Add lib portal oci11" ON)

if (ADD_LIB_PORTAL_OCI11) 
  include_directories(
    ${INCLUDE_TI}
    ${INCLUDE_OCI11}
  )
  add_library(oci11
    ${LIBS_SOURCE_DIR}/portal/oci11/src/dbportal.cpp
    ${LIBS_SOURCE_DIR}/portal/oci11/src/ociapi.cpp
    ${LIBS_SOURCE_DIR}/portal/oci11/src/ocinumber.cpp
    ${LIBS_SOURCE_DIR}/portal/oci11/src/portalextract.cpp
    ${LIBS_SOURCE_DIR}/portal/oci11/src/sqlapi.cpp
    ${LIBS_SOURCE_DIR}/portal/oci11/src/hugechar.cpp
  )
  target_link_libraries(oci11 ti)
  set_target_properties (oci11 PROPERTIES PROJECT_LABEL libs_portal_oci11)
  add_dependencies(oci11 padgen)
  if (WIN32)
    link_directories(${LIBS_SOURCE_DIR}/portal/oci11/oracle/lib/msvc)
    add_library (sqlapi32 SHARED
      ${LIBS_SOURCE_DIR}/portal/oci11/src/sqlapi.cpp
    )
    target_link_libraries(sqlapi32 ti oci)
    set_target_properties (sqlapi32 PROPERTIES PROJECT_LABEL libs_sqlapi32_oci11)
  endif ()
endif ()  

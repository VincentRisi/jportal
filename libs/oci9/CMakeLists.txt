project (portal_oci9)

option (ADD_LIB_PORTAL_OCI9 "Add lib portal oci9" OFF)

if (ADD_LIB_PORTAL_OCI9) 
  include_directories(
    ${INCLUDE_TI}
    ${INCLUDE_OCI9}
  )
  
  add_library(oci9
    ${LIBS_SOURCE_DIR}/portal/oci9/src/dbportal.cpp
    ${LIBS_SOURCE_DIR}/portal/oci9/src/ociapi.cpp
    ${LIBS_SOURCE_DIR}/portal/oci9/src/ocinumber.cpp
    ${LIBS_SOURCE_DIR}/portal/oci9/src/portalextract.cpp
    ${LIBS_SOURCE_DIR}/portal/oci9/src/sqlapi.cpp
    ${LIBS_SOURCE_DIR}/portal/oci9/src/hugechar.cpp
  )
  
  set_target_properties (oci9 PROPERTIES PROJECT_LABEL libs_portal_oci9)
  
  target_link_libraries(oci9 ti)
  
  add_dependencies(oci9 padgen)
endif ()  

project (portal_lite3)

option (ADD_LIB_PORTAL_LITE3 "Add lib portal lite3" OFF)

if (ADD_LIB_PORTAL_LITE3) 
  include_directories (
    ${INCLUDE_LITE3}
    ${INCLUDE_TI}
  )
  add_library (lite3
    ${LIBS_SOURCE_DIR}/portal/lite3/src/lite3api.cpp
    ${LIBS_SOURCE_DIR}/portal/lite3/src/sqlite3.c
  )
  set_target_properties (lite3 PROPERTIES PROJECT_LABEL libs_portal_lite3)
endif ()

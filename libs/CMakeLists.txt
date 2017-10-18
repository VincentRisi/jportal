project (libs_portal)

option (ADD_LIB_PORTAL "Add lib portal" ON)

if (ADD_LIB_PORTAL) 
  add_subdirectory(cli9)
  add_subdirectory(lite3)
  add_subdirectory(oci9)
  add_subdirectory(oci11)
  add_subdirectory(pg8)
endif()
  
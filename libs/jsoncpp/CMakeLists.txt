project (jsoncpp)

include_directories (
  ${CMAKE_CURRENT_SOURCE_DIR}
  ${CMAKE_CURRENT_SOURCE_DIR}/src
  )

set (jsoncpp_source
  ${CMAKE_CURRENT_SOURCE_DIR}/json/allocator.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/assertions.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/autolink.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/config.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/features.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/forwards.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/json.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/reader.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/value.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/version.h
  ${CMAKE_CURRENT_SOURCE_DIR}/json/writer.h
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_tool.h
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_reader.cpp
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_valueiterator.inl
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_value.cpp
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_writer.cpp
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_databuild.cpp
  ${CMAKE_CURRENT_SOURCE_DIR}/src/json_databuild.h
  ${CMAKE_CURRENT_SOURCE_DIR}/src/databuild2.h
  )

add_library(jsoncpp ${jsoncpp_source})
target_link_libraries (jsoncpp main)

copy ("${CMAKE_CURRENT_SOURCE_DIR}/json/*.*" ${CMAKE_BINARY_DIR}/deploy/jsoncpp/json)
copy ("${CMAKE_CURRENT_SOURCE_DIR}/src/*.*" ${CMAKE_BINARY_DIR}/deploy/jsoncpp/src)
if (WIN32)
  copy (${CMAKE_BINARY_DIR}/lib/Debug/jsoncpp.lib ${CMAKE_BINARY_DIR}/deploy/lib)
else ()  
  copy (${CMAKE_BINARY_DIR}/lib/libjsoncpp.a ${CMAKE_BINARY_DIR}/deploy/lib)
endif ()

set_target_properties(jsoncpp PROPERTIES PROJECT_LABEL bbd.jsoncpp)
set_property(TARGET jsoncpp PROPERTY FOLDER "bbd")


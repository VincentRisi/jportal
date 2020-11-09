project (http)

include_directories (
  ${LIBS_SOURCE_DIR}/jsoncpp
  ${LIBS_SOURCE_DIR}/jsoncpp/src
  )

set (source
    src/httpserver.cpp
    src/httpserver.h
    src/httprequest.cpp
    src/httprequest.h
    src/httpxcept.cpp
    src/httpxcept.h
  )

ADD_LIBRARY(http
   ${source}
)
target_link_libraries (http jsoncpp)

copy ("${CMAKE_CURRENT_SOURCE_DIR}/src/*.*" ${CMAKE_BINARY_DIR}/deploy/http/src)
if (WIN32)
  copy (${CMAKE_BINARY_DIR}/lib/Debug/http.lib    ${CMAKE_BINARY_DIR}/deploy/lib)
else ()  
  copy (${CMAKE_BINARY_DIR}/lib/libhttp.a    ${CMAKE_BINARY_DIR}/deploy/lib)
endif ()

set_target_properties(http PROPERTIES PROJECT_LABEL bbd.http)
set_property(TARGET http PROPERTY FOLDER "bbd")

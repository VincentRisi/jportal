project (main_jportal)

CMAKE_MINIMUM_REQUIRED (VERSION 2.8)

set (GENERATORS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/generators)
set (GENERATORS_BINARY_DIR ${CMAKE_SOURCE_DIR}/generators/build)

set (LOYALTY_SOURCE_DIR ${CMAKE_SOURCE_DIR}/loyalty)
set (LOYALTY_BINARY_DIR ${CMAKE_SOURCE_DIR}/loyalty/build)

include (${CMAKE_SOURCE_DIR}/cmake/functions.cmake)

add_subdirectory(generators)
add_subdirectory(loyalty)
 
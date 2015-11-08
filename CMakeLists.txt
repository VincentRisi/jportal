project (main_jportal)

CMAKE_MINIMUM_REQUIRED (VERSION 2.8)

include (${CMAKE_SOURCE_DIR}/cmake/functions.cmake)

set (PROJECTS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/projects)
set (PROJECTS_BINARY_DIR ${CMAKE_SOURCE_DIR}/projects/build)

set (LOYALTY_SOURCE_DIR ${CMAKE_SOURCE_DIR}/loyalty)
set (LOYALTY_BINARY_DIR ${CMAKE_SOURCE_DIR}/loyalty/build)

add_subdirectory(projects)
add_subdirectory(loyalty)
 
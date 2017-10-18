project (main_jportal)

CMAKE_MINIMUM_REQUIRED (VERSION 2.8)

find_package (PythonInterp)
find_package (Java)
if (WIN32)
  set (ANT_EXECUTABLE C:/apache-ant/bin/ant CACHE PATH "Ant Executable")
  set (POSTGRES_EXECUTABLE C:/PostgreSQL/bin/psql.exe CACHE PATH "postgresql executable")
  set (MYSQL_EXECUTABLE C:/MariaDB/bin/mysql.exe CACHE PATH "mysql mariadb executable")
else ()
  set (ANT_EXECUTABLE /usr/bin/ant       CACHE PATH "Ant Executable")
  set (POSTGRES_EXECUTABLE /usr/bin/psql CACHE PATH "postgresql executable")
  set (MYSQL_EXECUTABLE /usr/bin/mysql   CACHE PATH "mysql mariadb executable")
endif ()

set (TOOLS_DIR ${CMAKE_SOURCE_DIR}/tools)

set (ANYDBTEST_SOURCE_DIR ${CMAKE_SOURCE_DIR}/anydbtest)
set (ANYDBTEST_BINARY_DIR ${CMAKE_BINARY_DIR}/anydbtest)

set (BINUC_SOURCE_DIR ${CMAKE_SOURCE_DIR}/binuc)
set (BINUC_BINARY_DIR ${CMAKE_BINARY_DIR}/binuc)

set (BINUJ_SOURCE_DIR ${CMAKE_SOURCE_DIR}/binuj)
set (BINUJ_BINARY_DIR ${CMAKE_BINARY_DIR}/binuj)

set (DECOMPILERS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/decompilers)
set (DECOMPILERS_BINARY_DIR ${CMAKE_BINARY_DIR}/decompilers)

set (GENERATORS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/generators)
set (GENERATORS_BINARY_DIR ${CMAKE_BINARY_DIR}/generators)

set (IDL2TESTER_SOURCE_DIR ${CMAKE_SOURCE_DIR}/idl2tester)
set (IDL2TESTER_BINARY_DIR ${CMAKE_BINARY_DIR}/idl2tester)

set (LOYALTY_SOURCE_DIR ${CMAKE_SOURCE_DIR}/loyalty)
set (LOYALTY_BINARY_DIR ${CMAKE_BINARY_DIR}/loyalty)

set (PICKTESTER_SOURCE_DIR ${CMAKE_SOURCE_DIR}/picktester)
set (PICKTESTER_BINARY_DIR ${CMAKE_BINARY_DIR}/picktester)

set (SPROCS_SOURCE_DIR ${CMAKE_SOURCE_DIR}/sprocs)
set (SPROCS_BINARY_DIR ${CMAKE_BINARY_DIR}/sprocs)

include (${CMAKE_SOURCE_DIR}/cmake/functions.cmake)

add_subdirectory(generators)
add_subdirectory(anydbtest)
add_subdirectory(decompilers)
add_subdirectory(loyalty)
add_subdirectory(idl2tester)
add_subdirectory(binuc)
add_subdirectory(binuj)
add_subdirectory(picktester)
add_subdirectory(demo)
add_subdirectory(sprocs)

project (pickle_putty)

include(${CMAKE_SOURCE_DIR}/cmake/dbportal.cmake)

## The first five must be defined here (ii,sh, so, sql and bin)
pathed(iiDir     ${CMAKE_CURRENT_BINARY_DIR}/ii) 
pathed(shDir     ${CMAKE_CURRENT_BINARY_DIR}/sh)
pathed(soDir     ${CMAKE_CURRENT_BINARY_DIR}/so)
pathed(sqlDir    ${CMAKE_CURRENT_BINARY_DIR}/sql)
pathed(logDir    ${CMAKE_CURRENT_BINARY_DIR}/log/)
pathed(binDir    ${CMAKE_CURRENT_BINARY_DIR}/bin)

pathed(pyDir     ${CMAKE_CURRENT_BINARY_DIR}/py)
pathed(csDir     ${CMAKE_CURRENT_BINARY_DIR}/cs)
pathed(csadoDir  ${CMAKE_CURRENT_BINARY_DIR}/cs/ado)
pathed(csnet7Dir ${CMAKE_CURRENT_BINARY_DIR}/cs/7)
pathed(csrwDir   ${CMAKE_CURRENT_BINARY_DIR}/cs/rw)
pathed(piDir     ${CMAKE_CURRENT_BINARY_DIR}/pi)

## each element concatenated must be separate for command line arguments
set (switches 
  "OneSQLScript=1"
  "Internal=1"
  "IDLUbi=1"
  "ControlDB=npud00"
  "UConnect=npu"
  "IDL|${iiDir}|.ii|IDL Files|.*[.]ii$"
  "C|${shDir}|.sh|SH Files|.*[.]sh$"
  "SO|${soDir}|.so"
  "SQL|${sqlDir}|.sql|SQL Files|.*[.]sql$"
  "LogDir=${logDir}"
  "PythonDir=${pyDir}"
  "CSIDL2Dir=${csDir}"
  "CSAdoNet|${csadoDir}|.cs|CS Files|.*[.]cs$"
  "CSNet7Dir=${csnet7Dir}"
  "CSRWDir=${csrwDir}"
  "PARMSDir=${piDir}"
)

file (GLOB siFiles ${CMAKE_CURRENT_SOURCE_DIR}/si/*.si)

## The double quotes are required for keeping sifiles and switches as seen as 2 parameters
dbportal(putty_dbportal putty "${siFiles}" ${switches}) 

pathed (pickleBinDir ${CMAKE_SOURCE_DIR}/pickle/ParamControl/bin/Debug)

## each element concatenated must be separate for command line arguments
set (switches
  "-o" "${pickleBinDir}" "GenFrontEndBin"
)

set (pmFile ${CMAKE_CURRENT_SOURCE_DIR}/pickle/putty.pm)
set (piDir  ${CMAKE_CURRENT_BINARY_DIR}/pi)
set (prDir  ${CMAKE_CURRENT_SOURCE_DIR}/pickle)

pickle(putty_pickle ${pmFile} ${piDir} ${prDir} ${switches})

add_dependencies(putty_pickle putty_dbportal)
  
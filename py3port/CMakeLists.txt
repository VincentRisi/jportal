project (jportal_py3port)

option (USE_PY3PORT "Use py3port" OFF)
if (USE_PY3PORT)

  execute_process(COMMAND ${pythonExe} ${anydbMake} -n -o  ${PY3PORT_BINARY_DIR}/py3port.cmake -S -T ${PY3PORT_SOURCE_DIR}/py3port.anydb)
  if (EXISTS ${PY3PORT_BINARY_DIR}/py3port.cmake)
    include (${PY3PORT_BINARY_DIR}/py3port.cmake)
  endif ()  
  
  anydbMake2(jportal_py3port ${PY3PORT_SOURCE_DIR}/py3port.anydb ${sources} ${targets})

endif ()  

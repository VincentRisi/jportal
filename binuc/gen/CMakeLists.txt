project (binu_gen)

source_group ("IDL Source"  REGULAR_EXPRESSION ".*[.](idl2|idlj)$")
source_group ("SI Files"    REGULAR_EXPRESSION ".*[.]si$")
source_group ("CS Files"    REGULAR_EXPRESSION ".*[.]cs$")
source_group ("CPP Files"   REGULAR_EXPRESSION ".*[.](cpp|h)$")
source_group ("PY Files"    REGULAR_EXPRESSION ".*[.]py$")
source_group ("JAVA Files"  REGULAR_EXPRESSION ".*[.]java$")

set (cpp_dir    ${BINU_BINARY_DIR}/cpp)
set (python_dir ${BINU_BINARY_DIR}/python)
set (csharp_dir ${BINU_BINARY_DIR}/csharp)
set (java_dir   ${BINU_BINARY_DIR}/java)

file(MAKE_DIRECTORY ${cpp_dir})
file(MAKE_DIRECTORY ${python_dir})
file(MAKE_DIRECTORY ${csharp_dir})
file(MAKE_DIRECTORY ${java_dir})

function (idl2_simple projectName idlFile targets)
  set (switches)
  foreach (arg ${ARGN})
    list(APPEND switches "${arg}")
  endforeach ()
  add_custom_command(
    OUTPUT ${targets}
    COMMAND java -jar ${CRACKLE_JAR} ${idlFile} ${switches}
    DEPENDS ${idlFile} ${CRACKLE_JAR}
    VERBATIM
  )
  add_custom_target (${projectName} ALL
    DEPENDS ${targets}
    SOURCES ${idlFile} ${targets}
  )
endfunction ()

function (jportal projectName siFiles)
  set (switches)
  foreach (arg ${ARGN})
    list(APPEND switches "${arg}")
  endforeach ()
  set (pyFiles)
  set (csFiles)
  set (outFiles)
  foreach (siFile ${siFiles})
    get_filename_component (temp ${siFile} NAME)
    string (TOLOWER ${temp} lower_temp)
    get_filename_component (filename ${lower_temp} NAME_WE)
    set (pyFile ${python_dir}/binu_${filename}.py)
    list (APPEND pyFiles ${pyFile})
    set (csFile ${csharp_dir}/${filename}.cs)
    list (APPEND csFiles ${csFile})
    list (APPEND outFiles ${pyFile} ${csFile})
    add_custom_command (
      OUTPUT ${pyFile} ${csFile}
      COMMAND java -jar ${JPORTAL_JAR} ${siFile} ${switches}
      DEPENDS ${siFile} ${JPORTAL_JAR}
      VERBATIM
    )
  endforeach ()
  set (pyFiles ${pyFiles} PARENT_SCOPE)
  add_custom_target (${projectName} ALL
    DEPENDS ${outFiles}
    SOURCES ${siFiles} ${pyFiles} ${csFiles}
  )
endfunction()

set (source ${BINU_SOURCE_DIR}/idl/binu.idl2)

set (targets
  ${python_dir}/binu.py
  ${python_dir}/binupython.h
  ${python_dir}/binuPython.cpp
  ${csharp_dir}/Binu.cs
  ${csharp_dir}/BinuProxy.cs
  ${csharp_dir}/BinuStructs.cs
  ${csharp_dir}/IBinu.cs
  ${cpp_dir}/binu.h
  ${cpp_dir}/binu.cpp
  ${cpp_dir}/binuimpl.cpp
  ${cpp_dir}/binuserver.cpp
)  

set (switches 
  "-o" "${cpp_dir}"    "PopUbiServer"
  "-o" "${csharp_dir}" "PopUbiCSharp"
  "-o" "${python_dir}" "PopUbiPuffinModule"
)

idl2_simple(binu_gen_idl2 ${source} "${targets}" ${switches})

if (WIN32)
  set (source ${BINU_SOURCE_DIR}/idl/binu.idlj)
  
  set (targets
    ${java_dir}/bbd/binu/BinuStructs.java
    ${java_dir}/bbd/binu/BinuIFace.java
    ${java_dir}/bbd/binu/BinuImpl.java
    ${java_dir}/bbd/binu/BinuDispatch.java
  )  
  
  set (switches 
    "-o" "${java_dir}/bbd/binu" "PopBinuJavaServer"
  )
  
  idl2_simple(binu_gen_idlj ${source} "${targets}" ${switches})
endif ()

set (source
  ${BINU_SOURCE_DIR}/sql/si/zedzed.si
  ${BINU_SOURCE_DIR}/sql/si/zedzedbiz.si
  ${BINU_SOURCE_DIR}/sql/si/pongo.si
  ${BASE_SOURCE_DIR}/sql/servicemanager/si/singletrans.si
)

set (switches 
  "-o" "${python_dir}" "BinPythonCode"
  "-o" "${csharp_dir}" "BinCSCode"
  )
  
jportal(binu_gen_jportal "${source}" ${switches})

message (STATUS "${pyFiles}")

set (output ${LIBRARY_OUTPUT_PATH}/binu.zip)

set (starter ${BINU_SOURCE_DIR}/python/binu_start.py)

set (extras
  ${BINU_SOURCE_DIR}/python/binu_query.py
  ${BINU_SOURCE_DIR}/python/binu_tester.py
)

list (APPEND pyFiles ${starter} ${extras})

add_custom_command(
  OUTPUT ${output}
  COMMAND ${PYTHON_EXECUTABLE} ${DBZIPS_MAKE} -c -s -o ${output} ${pyFiles} ${python_dir}/binu.py
  COMMAND ${CMAKE_COMMAND} -E copy ${starter} ${LIBRARY_OUTPUT_PATH}
  DEPENDS ${pyFiles} ${python_dir}/binu.py
) 

add_custom_target(binu_gen_zip ALL
  DEPENDS ${output}
  SOURCES ${pyFiles} ${python_dir}/binu.py
)         

install(FILES ${output}  DESTINATION bin/pyextensions COMPONENT pyFiles)
install(FILES ${starter} DESTINATION bin/pyextensions COMPONENT pyFiles)

project (loyalty)

pathed (javaDir  ${LOYALTY_BINARY_DIR}/loyalty/xyz)
pathed (java1Dir ${LOYALTY_BINARY_DIR}/java1)
pathed (java2Dir ${LOYALTY_BINARY_DIR}/java2)
pathed (java3Dir ${LOYALTY_BINARY_DIR}/java3)
pathed (idljDir  ${LOYALTY_BINARY_DIR}/idlj)
pathed (idlDir   ${LOYALTY_BINARY_DIR}/idl)
pathed (jidlDir  ${LOYALTY_BINARY_DIR}/jidl)
pathed (jtreeDir ${LOYALTY_BINARY_DIR}/jtree)

set (si_files
  ${LOYALTY_SOURCE_DIR}/loyalty_code_lookup.si
)

set (switches 
  "-o" "${javaDir}"  "JavaCode"
  "-o" "${java1Dir}" "Java1Code"
  "-o" "${java2Dir}" "Java2Code"
  "-o" "${java3Dir}" "Java3Code"
  "-o" "${jidlDir}"  "JavaIdlCode"  
  "-o" "${jtreeDir}" "PythonTreeCode"  
)

jportal (loyalty_jportal "${si_files}" ${switches})


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

pathed (jserv    ${LOYALTY_BINARY_DIR}/jserv)
pathed (jclient  ${LOYALTY_BINARY_DIR}/jclient)
pathed (idlDir   ${LOYALTY_BINARY_DIR}/idl2)

set (im_file ${LOYALTY_SOURCE_DIR}/loyalty.im)
set (ib_dir  ${LOYALTY_SOURCE_DIR}/ib)

set (switches 
  "-o" "${jserv}"   "PopUbiJavaServer"
  "-o" "${jclient}" "PopUbiJavaClient"
)

crackle (loyalty_crackle ${im_file} ${idlDir} ${jidlDir} ${ib_dir} ${switches})


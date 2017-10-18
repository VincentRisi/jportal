project (jportal_loyalty)

pathed (javaDir   ${LOYALTY_BINARY_DIR}/loyalty/xyz)
pathed (javaWRDir ${LOYALTY_BINARY_DIR}/java)
pathed (idljDir   ${LOYALTY_BINARY_DIR}/idlj)
pathed (idlDir    ${LOYALTY_BINARY_DIR}/idl)
pathed (jidlDir   ${LOYALTY_BINARY_DIR}/jidl)
pathed (jtreeDir  ${LOYALTY_BINARY_DIR}/jtree)

set (si_files
  ${LOYALTY_SOURCE_DIR}/loyalty_code_lookup.si
)

set (switches 
  "-o" "${javaWRDir}" "JavaWRCode"
  "-o" "${jidlDir}"   "JavaIdlCode"  
  "-o" "${jtreeDir}"  "PythonTreeCode"  
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

add_dependencies(loyalty_jportal target_jportal_jar)
add_dependencies(loyalty_crackle target_crackle_jar)

project (projects_loyalty)

set (source_dir ${LOYALTY_SOURCE_DIR}/src)
set (binary_dir ${LOYALTY_SOURCE_DIR}/src/build)

pathed (javaDir ${binary_dir}/java)
pathed (java2Dir ${binary_dir}/java2)
pathed (idljDir ${binary_dir}/idlj)
pathed (idlDir ${binary_dir}/idl)

set (si_files
  ${source_dir}/loyalty_code_lookup.si
)

set (switches 
  "-o" "${javaDir}"  "JavaCode"
  "-o" "${java2Dir}" "Java2Code"
  "-o" "${idljDir}"  "IdlJCode"  
  "-o" "${idlDir}"   "IdlCode"  
)

jportal (loyalty_jportal "${si_files}" ${switches})


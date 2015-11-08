project (projects_loyalty)

set (source_dir ${LOYALTY_SOURCE_DIR}/src)
set (binary_dir ${LOYALTY_SOURCE_DIR}/src/build)

pathed (javaDir  ${binary_dir}/loyalty/xyz)
pathed (java1Dir ${binary_dir}/java1)
pathed (java2Dir ${binary_dir}/java2)
pathed (java3Dir ${binary_dir}/java3)
#pathed (idljDir  ${binary_dir}/idlj)
#pathed (idlDir   ${binary_dir}/idl)
pathed (jidlDir  ${binary_dir}/jidl)
pathed (jtreeDir ${binary_dir}/jtree)

set (si_files
  ${source_dir}/loyalty_code_lookup.si
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


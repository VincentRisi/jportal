project (jportal_generators)

set (class_bin_dir ${GENERATORS_SOURCE_DIR}/bin)

set (wild_card 
  vlab/jportal/*.class 
  vlab/jportal/decompiler/*.class 
  vlab/jportal/util/*.class
)
set (jportal_jar    ${class_bin_dir}/jportal.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/jportal/Manifest.txt)
message (STATUS "COMMAND jar vcfm ${jportal_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${jportal_jar}
  COMMAND jar vcfm ${jportal_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_bin_dir}
)
add_custom_target (target_jportal_jar ALL
  DEPENDS ${jportal_jar} 
)

set (wild_card 
  vlab/crackle/*.class 
  vlab/crackle/rdc/*.class 
  vlab/crackle/rpc/*.class 
  vlab/crackle/rw/*.class vlab/crackle/util/*.class
)
set (crackle_jar ${class_bin_dir}/crackle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/crackle/Manifest.txt)
message (STATUS "COMMAND jar vcfm ${crackle_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${crackle_jar}
  COMMAND jar vcfm ${crackle_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_bin_dir}
)
add_custom_target (target_crackle_jar ALL
  DEPENDS ${crackle_jar}
)

set (wild_card vlab/pickle/*.class)
set (pickle_jar ${class_bin_dir}/pickle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/pickle/Manifest.txt)
message (STATUS "COMMAND jar vcfm ${pickle_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${pickle_jar}
  COMMAND jar vcfm ${pickle_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_bin_dir}
)
add_custom_target (target_pickle_jar ALL
  DEPENDS ${pickle_jar}
)

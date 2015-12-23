project (jportal_generators)

set (class_bin_dir ${GENERATORS_SOURCE_DIR}/bin)

add_subdirectory(vlab/jportal/decompiler)

set (wild_card 
  vlab/jportal/*.class 
  vlab/jportal/decompiler/*.class 
  vlab/jportal/util/*.class
)
set (jportal_jar    ${class_bin_dir}/jportal.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/jportal/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_jportal_jar ALL
  ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
)

set (wild_card 
  vlab/crackle/*.class 
  vlab/crackle/rdc/*.class 
  vlab/crackle/rpc/*.class 
  vlab/crackle/rw/*.class
  vlab/crackle/util/*.class
)
set (crackle_jar ${class_bin_dir}/crackle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/crackle/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_crackle_jar ALL
  ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
)

set (wild_card vlab/pickle/*.class)
set (pickle_jar ${class_bin_dir}/pickle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/vlab/pickle/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_pickle_jar ALL
  ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
)

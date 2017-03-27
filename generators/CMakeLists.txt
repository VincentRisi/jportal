project (jportal_generators)

antMake(target_generators_ant ${GENERATORS_SOURCE_DIR}/build.xml)

set (class_bin_dir ${GENERATORS_SOURCE_DIR}/bin)

set (wild_card 
  bbd/jportal/*.class 
  bbd/jportal/util/*.class
)
set (jportal_jar    ${class_bin_dir}/jportal.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/jportal/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_jportal_jar ALL
  ${jarExe} vcfm ${jportal_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
  DEPENDS target_generators_ant
)

set (wild_card 
  bbd/crackle/*.class 
  bbd/crackle/rdc/*.class 
  bbd/crackle/rpc/*.class 
  bbd/crackle/rw/*.class
  bbd/crackle/util/*.class
)
set (crackle_jar ${class_bin_dir}/crackle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/crackle/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_crackle_jar ALL
  ${jarExe} vcfm ${crackle_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
  DEPENDS target_generators_ant
)

set (wild_card bbd/pickle/*.class)
set (pickle_jar ${class_bin_dir}/pickle.jar)
set (manifest_file  ${GENERATORS_SOURCE_DIR}/bbd/pickle/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_pickle_jar ALL
  ${jarExe} vcfm ${pickle_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
  DEPENDS target_generators_ant
)

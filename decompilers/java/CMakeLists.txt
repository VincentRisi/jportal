project (decompilers_java)

antMake(target_decompiler_ant ${DECOMPILERS_SOURCE_DIR}/java/build.xml)

set (class_bin_dir ${DECOMPILERS_SOURCE_DIR}/java/bin)

set (wild_card
  bbd/jportal/decompiler/*.class
)
set (decompiler_jar ${class_bin_dir}/decompiler.jar)
set (manifest_file  ${DECOMPILERS_SOURCE_DIR}/java/Manifest.txt)
message (STATUS "COMMAND ${jarExe} vcfm ${decompiler_jar} ${manifest_file} ${wild_card}")
add_custom_target (target_decompiler_jar ALL
  ${jarExe} vcfm ${decompiler_jar} ${manifest_file} ${wild_card}
  WORKING_DIRECTORY ${class_bin_dir}
  DEPENDS target_decompiler_ant
)

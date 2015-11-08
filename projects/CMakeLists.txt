project (jportal_projects)

set (class_dir ${CMAKE_SOURCE_DIR}/projects/bin)

set (wild_card vlab/jportal/*.class)
set (jportal_class_files)
file (GLOB files ${class_dir}/${wild_card})
foreach (file ${files})
  list (APPEND jportal_class_files ${file})
endforeach()  
set (jportal_jar    ${CMAKE_SOURCE_DIR}/projects/bin/jportal.jar)
set (manifest_file  ${CMAKE_SOURCE_DIR}/projects/vlab/jportal/Manifest.txt)
set_source_files_properties(${jportal_class_files} PROPERTIES GENERATED TRUE)

message (STATUS "COMMAND jar vcfm ${jportal_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${jportal_jar}
  COMMAND jar vcfm ${jportal_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_dir}
)

add_custom_target (target_jportal_jar ALL
  DEPENDS ${jportal_jar} 
)

set (wild_card vlab/crackle/*.class)
set (crackle_class_files)
file (GLOB files ${class_dir}/${wild_card})
foreach (file ${files})
  list (APPEND crackle_class_files ${file})
endforeach()  
set (crackle_jar ${CMAKE_SOURCE_DIR}/projects/bin/crackle.jar)
set (manifest_file  ${CMAKE_SOURCE_DIR}/projects/vlab/crackle/Manifest.txt)
set_source_files_properties(${crackle_class_files} PROPERTIES GENERATED TRUE)

message (STATUS "COMMAND jar vcfm ${crackle_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${crackle_jar}
  COMMAND jar vcfm ${crackle_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_dir}
)

add_custom_target (target_crackle_jar ALL
  DEPENDS ${crackle_jar}
)

set (wild_card vlab/pickle/*.class)
set (pickle_class_files)
file (GLOB files ${class_dir}/${wild_card})
foreach (file ${files})
  list (APPEND pickle_class_files ${file})
endforeach()  
set (pickle_jar ${CMAKE_SOURCE_DIR}/projects/bin/pickle.jar)
set (manifest_file  ${CMAKE_SOURCE_DIR}/projects/vlab/pickle/Manifest.txt)
set_source_files_properties(${pickle_class_files} PROPERTIES GENERATED TRUE)

message (STATUS "COMMAND jar vcfm ${pickle_jar} ${manifest_file} ${wild_card}")
add_custom_command(
  OUTPUT ${pickle_jar}
  COMMAND jar vcfm ${pickle_jar} ${manifest_file} ${wild_card} 
  WORKING_DIRECTORY ${class_dir}
)

add_custom_target (target_pickle_jar ALL
  DEPENDS ${pickle_jar}
)

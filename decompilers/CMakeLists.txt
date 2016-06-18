project (jportal_decompiler)

anydbMake(jportal_decompiler ${CMAKE_CURRENT_SOURCE_DIR}/decompiler.anydb "${CMAKE_CURRENT_SOURCE_DIR}/Oracle.java")

add_subdirectory(java)

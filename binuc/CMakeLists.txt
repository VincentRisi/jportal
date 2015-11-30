project (jportal_binuc)

anydbMake(jportal_binuc ${BINUC_SOURCE_DIR}/binuc.anydb "${BINUC_BINARY_DIR}/build/idl/server/binucserver.cpp")

add_subdirectory(coco)

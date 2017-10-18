project (exe_coco)

set (source
  ${BINUC_SOURCE_DIR}/coco/Action.cpp
  ${BINUC_SOURCE_DIR}/coco/ArrayList.cpp
  ${BINUC_SOURCE_DIR}/coco/BitArray.cpp
  ${BINUC_SOURCE_DIR}/coco/CharClass.cpp
  ${BINUC_SOURCE_DIR}/coco/CharSet.cpp
  ${BINUC_SOURCE_DIR}/coco/Coco.cpp
  ${BINUC_SOURCE_DIR}/coco/Comment.cpp
  ${BINUC_SOURCE_DIR}/coco/DFA.cpp
  ${BINUC_SOURCE_DIR}/coco/Generator.cpp
  ${BINUC_SOURCE_DIR}/coco/HashTable.cpp
  ${BINUC_SOURCE_DIR}/coco/Melted.cpp
  ${BINUC_SOURCE_DIR}/coco/Node.cpp
  ${BINUC_SOURCE_DIR}/coco/Parser.cpp
  ${BINUC_SOURCE_DIR}/coco/ParserGen.cpp
  ${BINUC_SOURCE_DIR}/coco/Position.cpp
  ${BINUC_SOURCE_DIR}/coco/Scanner.cpp
  ${BINUC_SOURCE_DIR}/coco/SortedList.cpp
  ${BINUC_SOURCE_DIR}/coco/State.cpp
  ${BINUC_SOURCE_DIR}/coco/StringBuilder.cpp
  ${BINUC_SOURCE_DIR}/coco/Symbol.cpp
  ${BINUC_SOURCE_DIR}/coco/Tab.cpp
  ${BINUC_SOURCE_DIR}/coco/Target.cpp
)

add_executable (Coco ${source})


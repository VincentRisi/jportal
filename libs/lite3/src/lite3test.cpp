#include <stdio.h>
#include "lite3test.h"

static Lite3Connector connector;

int run(char *database)
{
  connector.open(database);
  NodesCount nodesCount(&connector);
  nodesCount.ReadOne();
  printf("Count of Nodes = %d\n", nodesCount.noOf);
  NodesScriptNodes scriptNodes(&connector);
  scriptNodes.Exec();
  while (scriptNodes.Fetch())
  {
    if (strcmp(scriptNodes.Id, "NUTTER") > 0)
      continue;
    printf("Id='%s', Name='%s', Descr='%s', ScriptName='%s', Adaptor='%s', Priority=%d\n"
    , scriptNodes.Id
    , scriptNodes.Name
    , scriptNodes.Descr
    , scriptNodes.ScriptName
    , scriptNodes.Adaptor
    , (int)scriptNodes.Priority
    );
  }
  connector.begin();
  try
  {
    NodesDeleteOne deleteOne(&connector);
    strncpy(deleteOne.Id, "NUTTER", sizeof(deleteOne.Id)-1);
    deleteOne.Exec();
    NodesInsert insert(&connector);
    strncpy(insert.Id, "NUTTER", sizeof(insert.Id)-1);
    strncpy(insert.Name, "AGNES NUTTER", sizeof(insert.Name)-1);
    strncpy(insert.Descr, "AGNES NUTTER - Good Omens", sizeof(insert.Descr)-1);
    strncpy(insert.ScriptName, "AGNES_NUTTER", sizeof(insert.ScriptName)-1);
    strncpy(insert.Adaptor, "AGNES", sizeof(insert.Adaptor)-1);
    insert.Priority = 1;
    insert.Status = 0;
    insert.CanChangeMsgs[0] = 'Y';
    insert.IsDeadLetter[0] = 'N';
    insert.Exec();
    connector.commit();
  }
  catch (Lite3Exception &ex)
  {
    connector.rollback();
    throw;
  }
  connector.close();
  return 0;
}

int main(int argc, char *argv[])
{
  try
  {
    if (argc < 2)
      run("c:\\sars\\code\\puffin\\bin\\puffin.db");
    else
      run(argv[1]);
  }
  catch (Lite3Exception &ex)
  {
    printf("Exception: %s\n", ex.ErrorStr());
  }
}

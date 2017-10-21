#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <malloc.h>
#include "addlist.h"

#define SIZE 64*1024
#define START "/main/nedcor/bbd/common"

TAddList<char*, int> sources;

int compare(char **A, char **B)
{
  return strcmp(*A, *B);
}
int main(int argc, char* argv[])
{
  const char* start;
  if (argc < 2) return 1;
  if (argc > 2)
    start = argv[2];
  else
    start = START;
  FILE* input = fopen(argv[1], "rb");
  if (input == 0) return 2;
  char buffer[SIZE];
  char fpath[256];
  size_t pos = 0;
  memset(fpath, 0, sizeof(fpath));
  while (true)
  {
    size_t size;
    size = fread(buffer, 1, SIZE, input);
    if (size == 0) break;
    for (size_t i = 0; i < size; i++)
    {
      if ((buffer[i] <= ' ' || buffer[i] > 127 || strchr(":{}[]()%#*?", buffer[i]))
      ||  (pos < strlen(start) && pos < 255 && buffer[i] != start[pos]))
      {
        if (pos > strlen(start))
        {
          char* path = strdup(fpath);
          sources.add(path);
        }
        memset(fpath, 0, sizeof(fpath));
        pos = 0;
        continue;
      }
      fpath[pos++] = buffer[i];
    }
  }
  sources.compare = compare;
  sources.sort();
  memset(fpath, 0, sizeof(fpath));
  for (int i = 0; i < sources.getCount(); i++)
  {
    if (strcmp(sources[i], fpath) != 0)
    {
      strncpy(fpath, sources[i], sizeof(fpath) - 1);
      printf("%s\n", fpath);
    }
  }

  return 0;
}

#ifndef _SNIPLIST_H_
#define _SNIPLIST_H_

struct xBase
{
  const char* file;
  int line;
  const char* msg;
  long value;
  xBase(const char* file, int line, const char* msg, long value=0)
  {
    this->file=file;
    this->line=line;
    this->msg=msg;
    this->value=value;
  }
};
template <class TYPE, class INDEX>
inline void SnipAddList(TYPE*& List, INDEX& Index, const TYPE& Rec, const INDEX Delta)
{
  if ((List == 0) != (Index == 0))
    throw xBase(__FILE__, __LINE__, "SnipAddList: List and Index out of sync");
  if (Index % Delta == 0)
  {
    TYPE* Resized = (TYPE*) realloc(List, sizeof(Rec)*(Index+Delta));
    if (Resized == 0) // Cater for alloc failure
      throw xBase(__FILE__, __LINE__, "SnipAddList: Not enough memory to allocate memory", (long)sizeof(Rec)*(Index+Delta));
    List = Resized;
  }
  List[Index++] = Rec;
}

#endif

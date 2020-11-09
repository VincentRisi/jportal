#ifndef _lookup_SH_
#define _lookup_SH_ ETYPDWH
/* 2425451994 */

// SPECIAL for testing ONLY lookup.sh renamed to lookup.inl
#include "machine.h"
#pragma pack(1)

#include "xstring.h"

typedef struct tLookup
{
  char   Name[256]; /*    */
  char   Refs[256]; /*    */
  char   Value[256]; /*    */
  char   USId[17]; /*    */
  char   TmStamp[15]; /*    */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Refs, sizeof(Refs));
    TrimTrailingBlanks(Value, sizeof(Value));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="Lookup")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Refs>");XRec.ampappend(Refs);XRec.append("</Refs>\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
    XRec.append("  <TmStamp>");XRec.ampappend(TmStamp);XRec.append("</TmStamp>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Refs", work);memcpy(Refs, work.data, sizeof(Refs)-1);
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  // You can supply your own DataBuilder with a guard of _DATABUILD_H_
  // and the following methods :-
  //  void name(char *);
  //  void add(char *, <T> input);
  //  void set(char *, <T> output, int size); /* remember char[n] are one size bigger */
  void BuildData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Refs", Refs, sizeof(Refs), "");
    #else
    dBuild.add("Refs", Refs, sizeof(Refs));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Refs", Refs, sizeof(Refs), "");
    #else
    dBuild.set("Refs", Refs, sizeof(Refs));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookup()
  {
    memset(this, 0, sizeof(*this));
  }
  void Insert(tDBConnect& C)
  {
    C.RunProc("LookupInsert", this);
  }
  void Insert(tDBConnect& C, const char *aName, const char *aRefs, const char *aValue
        , const char *aUSId, const char *aTmStamp)
  {
    strncpyz(Name, aName, sizeof(Name)-1);
    strncpyz(Refs, aRefs, sizeof(Refs)-1);
    strncpyz(Value, aValue, sizeof(Value)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    strncpyz(TmStamp, aTmStamp, sizeof(TmStamp)-1);
    C.RunProc("LookupInsert", this);
  }
  void Update(tDBConnect& C)
  {
    C.RunProc("LookupUpdate", this);
  }
  void Update(tDBConnect& C, const char *aName, const char *aRefs, const char *aValue
        , const char *aUSId, const char *aTmStamp)
  {
    strncpyz(Name, aName, sizeof(Name)-1);
    strncpyz(Refs, aRefs, sizeof(Refs)-1);
    strncpyz(Value, aValue, sizeof(Value)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    strncpyz(TmStamp, aTmStamp, sizeof(TmStamp)-1);
    C.RunProc("LookupUpdate", this);
  }
  bool SelectOneReadOne(tDBConnect& C)
  {
    return C.ReadOne("LookupSelectOne", this);
  }
  bool SelectOne(tDBConnect& C, const char *aName, const char *aRefs)
  {
    strncpyz(Name, aName, sizeof(Name)-1);
    strncpyz(Refs, aRefs, sizeof(Refs)-1);
    return C.ReadOne("LookupSelectOne", this);
  }
  void SelectOne(tDBConnect& C)
  {
    C.RunProc("LookupSelectOne", this);
  }
  #endif
} tLookup, *pLookup;

#if defined(_DBPORTAL_H_)
inline void LookupDeleteAll(tDBConnect& C)
{
  C.RunProc("LookupDeleteAll");
}

#endif

typedef struct tLookupKey
{
  char   Name[256]; /*    */
  char   Refs[256]; /*    */
#if defined(_DBPORTAL_H_)
  void Populate(tLookup &Rec)
  {
    strncpyz(Name, Rec.Name, sizeof(Name)-1);
    strncpyz(Refs, Rec.Refs, sizeof(Refs)-1);
  }
  #endif
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Refs, sizeof(Refs));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupKey")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Refs>");XRec.ampappend(Refs);XRec.append("</Refs>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Refs", work);memcpy(Refs, work.data, sizeof(Refs)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupKey")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Refs", Refs, sizeof(Refs), "");
    #else
    dBuild.add("Refs", Refs, sizeof(Refs));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Refs", Refs, sizeof(Refs), "");
    #else
    dBuild.set("Refs", Refs, sizeof(Refs));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupKey()
  {
    memset(this, 0, sizeof(*this));
  }
  void DeleteOne(tDBConnect& C)
  {
    C.RunProc("LookupDeleteOne", this);
  }
  #endif
} tLookupKey, *pLookupKey;

typedef struct tLookupSelectList
{
  char   Name[256]; /* i  */
  char   Refs[256]; /*  o */
  char   Value[256]; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Refs, sizeof(Refs));
    TrimTrailingBlanks(Value, sizeof(Value));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupSelectList")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Refs>");XRec.ampappend(Refs);XRec.append("</Refs>\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Refs", work);memcpy(Refs, work.data, sizeof(Refs)-1);
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupSelectList")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "i");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Refs", Refs, sizeof(Refs), "o");
    #else
    dBuild.add("Refs", Refs, sizeof(Refs));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "o");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "i");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Refs", Refs, sizeof(Refs), "o");
    #else
    dBuild.set("Refs", Refs, sizeof(Refs));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "o");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupSelectList()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tLookupSelectList, *pLookupSelectList;

#if defined(_DBPORTAL_H_)
struct tLookupSelectListQuery : public tDBQuery
{
  tLookupSelectListQuery(tDBConnect& C, pLookupSelectList D, bool DoExec=true)
  : tDBQuery(C, "LookupSelectList", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tLookupGet
{
  char   Name[257]; /* i  */
  char   Ref[257]; /* i  */
  char   Value[257]; /*  o */
  char   filler3[1];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Ref, sizeof(Ref));
    TrimTrailingBlanks(Value, sizeof(Value));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupGet")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Ref>");XRec.ampappend(Ref);XRec.append("</Ref>\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Ref", work);memcpy(Ref, work.data, sizeof(Ref)-1);
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupGet")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "i");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.add("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "o");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
    dBuild.fill(1);
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "i");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.set("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "o");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
    dBuild.skip(1);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupGet()
  {
    memset(this, 0, sizeof(*this));
  }
  bool GetReadOne(tDBConnect& C)
  {
    return C.ReadOne("LookupGet", this);
  }
  bool Get(tDBConnect& C, const char *aName, const char *aRef)
  {
    strncpyz(Name, aName, sizeof(Name)-1);
    strncpyz(Ref, aRef, sizeof(Ref)-1);
    return C.ReadOne("LookupGet", this);
  }
  void Get(tDBConnect& C)
  {
    C.RunProc("LookupGet", this);
  }
  #endif
} tLookupGet, *pLookupGet;

typedef struct tLookupNameList
{
  char   Name[257]; /*  o */
  char   filler1[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Name, sizeof(Name));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupNameList")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupNameList")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "o");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "o");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupNameList()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tLookupNameList, *pLookupNameList;

#if defined(_DBPORTAL_H_)
struct tLookupNameListQuery : public tDBQuery
{
  tLookupNameListQuery(tDBConnect& C, pLookupNameList D, bool DoExec=true)
  : tDBQuery(C, "LookupNameList", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tLookupSelectIBAN
{
  char   Ref[256]; /* i  */
  char   Name[256]; /*  o */
  char   Value[256]; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Ref, sizeof(Ref));
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Value, sizeof(Value));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupSelectIBAN")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Ref>");XRec.ampappend(Ref);XRec.append("</Ref>\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Ref", work);memcpy(Ref, work.data, sizeof(Ref)-1);
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupSelectIBAN")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.add("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "o");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "o");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.set("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "o");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "o");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupSelectIBAN()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tLookupSelectIBAN, *pLookupSelectIBAN;

#if defined(_DBPORTAL_H_)
struct tLookupSelectIBANQuery : public tDBQuery
{
  tLookupSelectIBANQuery(tDBConnect& C, pLookupSelectIBAN D, bool DoExec=true)
  : tDBQuery(C, "LookupSelectIBAN", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tLookupSelectVatAccNumber
{
  char   Ref[256]; /* i  */
  char   Value[256]; /*  o */
  char   USid[17]; /*  o */
  char   TmStamp[19]; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Ref, sizeof(Ref));
    TrimTrailingBlanks(Value, sizeof(Value));
    TrimTrailingBlanks(USid, sizeof(USid));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupSelectVatAccNumber")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Ref>");XRec.ampappend(Ref);XRec.append("</Ref>\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("  <USid>");XRec.ampappend(USid);XRec.append("</USid>\n");
    XRec.append("  <TmStamp>");XRec.ampappend(TmStamp);XRec.append("</TmStamp>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Ref", work);memcpy(Ref, work.data, sizeof(Ref)-1);
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
    msg.GetValue("USid", work);memcpy(USid, work.data, sizeof(USid)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupSelectVatAccNumber")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.add("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "o");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USid", USid, sizeof(USid), "o");
    #else
    dBuild.add("USid", USid, sizeof(USid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Ref", Ref, sizeof(Ref), "i");
    #else
    dBuild.set("Ref", Ref, sizeof(Ref));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "o");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USid", USid, sizeof(USid), "o");
    #else
    dBuild.set("USid", USid, sizeof(USid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupSelectVatAccNumber()
  {
    memset(this, 0, sizeof(*this));
  }
  bool SelectVatAccNumberReadOne(tDBConnect& C)
  {
    return C.ReadOne("LookupSelectVatAccNumber", this);
  }
  bool SelectVatAccNumber(tDBConnect& C, const char *aRef)
  {
    strncpyz(Ref, aRef, sizeof(Ref)-1);
    return C.ReadOne("LookupSelectVatAccNumber", this);
  }
  void SelectVatAccNumber(tDBConnect& C)
  {
    C.RunProc("LookupSelectVatAccNumber", this);
  }
  #endif
} tLookupSelectVatAccNumber, *pLookupSelectVatAccNumber;

typedef struct tLookupSelectAllVatAccNumber
{
  char   Value[256]; /*  o */
  char   USid[17]; /*  o */
  char   TmStamp[19]; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(Value, sizeof(Value));
    TrimTrailingBlanks(USid, sizeof(USid));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="LookupSelectAllVatAccNumber")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Value>");XRec.ampappend(Value);XRec.append("</Value>\n");
    XRec.append("  <USid>");XRec.ampappend(USid);XRec.append("</USid>\n");
    XRec.append("  <TmStamp>");XRec.ampappend(TmStamp);XRec.append("</TmStamp>\n");
    XRec.append("</");XRec.append(Outer);XRec.append(">\n");
  }
  #endif
  #if defined(__XMLRECORD_H__)
  void FromXML(TBAmp &XRec)
  {
    TXMLRecord msg;
    TBAmp work;
    msg.Load(XRec);
    memset(this, 0, sizeof(*this));
    msg.GetValue("Value", work);memcpy(Value, work.data, sizeof(Value)-1);
    msg.GetValue("USid", work);memcpy(USid, work.data, sizeof(USid)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="LookupSelectAllVatAccNumber")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Value", Value, sizeof(Value), "o");
    #else
    dBuild.add("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USid", USid, sizeof(USid), "o");
    #else
    dBuild.add("USid", USid, sizeof(USid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Lookup")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Value", Value, sizeof(Value), "o");
    #else
    dBuild.set("Value", Value, sizeof(Value));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USid", USid, sizeof(USid), "o");
    #else
    dBuild.set("USid", USid, sizeof(USid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tLookupSelectAllVatAccNumber()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tLookupSelectAllVatAccNumber, *pLookupSelectAllVatAccNumber;

#if defined(_DBPORTAL_H_)
struct tLookupSelectAllVatAccNumberQuery : public tDBQuery
{
  tLookupSelectAllVatAccNumberQuery(tDBConnect& C, pLookupSelectAllVatAccNumber D, bool DoExec=true)
  : tDBQuery(C, "LookupSelectAllVatAccNumber", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

#ifdef M_AIX
#pragma pack(pop)
#else
#pragma pack()
#endif
#endif


#ifndef _streams_SH_
#define _streams_SH_ NJKUUBM
/* 3728368735 */

/* Please do not modify, spindle or mutilate. */
#include "machine.h"
#pragma pack(1)

#include "xstring.h"

enum eStreamsMessageType
{ StreamsMessageTypeXML = 0
, StreamsMessageTypeText = 1
, StreamsMessageTypeFile = 2
};

inline char *StreamsMessageTypeLookup(int no)
{
  switch(no)
  {
  case StreamsMessageTypeXML: return "XML";
  case StreamsMessageTypeText: return "Text";
  case StreamsMessageTypeFile: return "File";
  default: return "<n/a>";
  }
}

enum eStreamsStatus
{ StreamsStatusNone = 0
, StreamsStatusSent = 1
, StreamsStatusAck = 2
, StreamsStatusNak = 3
};

inline char *StreamsStatusLookup(int no)
{
  switch(no)
  {
  case StreamsStatusNone: return "None";
  case StreamsStatusSent: return "Sent";
  case StreamsStatusAck: return "Ack";
  case StreamsStatusNak: return "Nak";
  default: return "<n/a>";
  }
}

typedef struct tStreams
{
  int32  Id; /*    */
  int32  MessageId; /*    */
  char   QueueId[17]; /*    */
  char   EventQueueId[17]; /*    */
  char   StreamRef[129]; /*    */
  char   StreamType[17]; /*    */
  char   StreamDescr[66]; /*    */
  char   filler7[2];
  int32  MessageLen; /*    */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int8   MessageType; /*    */
  int8   Priority; /*    */
  int8   Status; /*    */
  char   DateCreated[15]; /*    */
  char   USId[17]; /*    */
  char   TmStamp[15]; /*    */
  char   filler15[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="Streams")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
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
  void BuildData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(15);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
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
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(15);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
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
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreams()
  {
    memset(this, 0, sizeof(*this));
  }
  void Insert(tDBConnect& C)
  {
    C.RunProc("StreamsInsert", this);
  }
  void Insert(tDBConnect& C, int32 aId, int32 aMessageId, const char *aQueueId
        , const char *aEventQueueId, const char *aStreamRef, const char *aStreamType
        , const char *aStreamDescr, int32 aMessageLen, void *aMessageData, int8 aMessageType
        , int8 aPriority, int8 aStatus, const char *aDateCreated, const char *aUSId
        , const char *aTmStamp)
  {
    Id = aId;
    MessageId = aMessageId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    strncpyz(EventQueueId, aEventQueueId, sizeof(EventQueueId)-1);
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    strncpyz(StreamType, aStreamType, sizeof(StreamType)-1);
    strncpyz(StreamDescr, aStreamDescr, sizeof(StreamDescr)-1);
    MessageLen = aMessageLen;
    memcpy(&MessageData, aMessageData, sizeof(MessageData));
    MessageType = aMessageType;
    Priority = aPriority;
    Status = aStatus;
    strncpyz(DateCreated, aDateCreated, sizeof(DateCreated)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    strncpyz(TmStamp, aTmStamp, sizeof(TmStamp)-1);
    C.RunProc("StreamsInsert", this);
  }
  void Update(tDBConnect& C)
  {
    C.RunProc("StreamsUpdate", this);
  }
  void Update(tDBConnect& C, int32 aId, int32 aMessageId, const char *aQueueId
        , const char *aEventQueueId, const char *aStreamRef, const char *aStreamType
        , const char *aStreamDescr, int32 aMessageLen, void *aMessageData, int8 aMessageType
        , int8 aPriority, int8 aStatus, const char *aDateCreated, const char *aUSId
        , const char *aTmStamp)
  {
    Id = aId;
    MessageId = aMessageId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    strncpyz(EventQueueId, aEventQueueId, sizeof(EventQueueId)-1);
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    strncpyz(StreamType, aStreamType, sizeof(StreamType)-1);
    strncpyz(StreamDescr, aStreamDescr, sizeof(StreamDescr)-1);
    MessageLen = aMessageLen;
    memcpy(&MessageData, aMessageData, sizeof(MessageData));
    MessageType = aMessageType;
    Priority = aPriority;
    Status = aStatus;
    strncpyz(DateCreated, aDateCreated, sizeof(DateCreated)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    strncpyz(TmStamp, aTmStamp, sizeof(TmStamp)-1);
    C.RunProc("StreamsUpdate", this);
  }
  bool SelectOneReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsSelectOne", this);
  }
  bool SelectOne(tDBConnect& C, int32 aId)
  {
    Id = aId;
    return C.ReadOne("StreamsSelectOne", this);
  }
  void SelectOne(tDBConnect& C)
  {
    C.RunProc("StreamsSelectOne", this);
  }
  bool SelectOneUpdReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsSelectOneUpd", this);
  }
  bool SelectOneUpd(tDBConnect& C, int32 aId)
  {
    Id = aId;
    return C.ReadOne("StreamsSelectOneUpd", this);
  }
  void SelectOneUpd(tDBConnect& C)
  {
    C.RunProc("StreamsSelectOneUpd", this);
  }
  #endif
} tStreams, *pStreams;

#if defined(_DBPORTAL_H_)
struct tStreamsSelectAllQuery : public tDBQuery
{
  tStreamsSelectAllQuery(tDBConnect& C, pStreams D, bool DoExec=true)
  : tDBQuery(C, "StreamsSelectAll", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsKey
{
  int32  Id; /*    */
#if defined(_DBPORTAL_H_)
  void Populate(tStreams &Rec)
  {
    Id = Rec.Id;
  }
  #endif
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsKey")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsKey")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "");
    #else
    dBuild.add("Id", Id);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsKey()
  {
    memset(this, 0, sizeof(*this));
  }
  void DeleteOne(tDBConnect& C)
  {
    C.RunProc("StreamsDeleteOne", this);
  }
  #endif
} tStreamsKey, *pStreamsKey;

typedef struct tStreamsExists
{
  int32  Count; /*  o */
  int32  Id; /* i  */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Count);
    SwapBytes(Id);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsExists")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Count>");sprintf(Work, "%d", Count);XRec.append(Work);XRec.append("</Count>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
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
    msg.GetValue("Count", work);Count = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsExists")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Count", Count, "o");
    #else
    dBuild.add("Count", Count);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Count", Count, sizeof(Count), "o");
    #else
    dBuild.set("Count", Count, sizeof(Count));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsExists()
  {
    memset(this, 0, sizeof(*this));
  }
  bool ExistsReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsExists", this);
  }
  bool Exists(tDBConnect& C, int32 aId)
  {
    Id = aId;
    return C.ReadOne("StreamsExists", this);
  }
  void Exists(tDBConnect& C)
  {
    C.RunProc("StreamsExists", this);
  }
  #endif
} tStreamsExists, *pStreamsExists;

typedef struct tStreamsCount
{
  int32  NoOf; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(NoOf);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsCount")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <NoOf>");sprintf(Work, "%d", NoOf);XRec.append(Work);XRec.append("</NoOf>\n");
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
    msg.GetValue("NoOf", work);NoOf = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsCount")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("NoOf", NoOf, "o");
    #else
    dBuild.add("NoOf", NoOf);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("NoOf", NoOf, sizeof(NoOf), "o");
    #else
    dBuild.set("NoOf", NoOf, sizeof(NoOf));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsCount()
  {
    memset(this, 0, sizeof(*this));
  }
  bool CountReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsCount", this);
  }
  void Count(tDBConnect& C)
  {
    C.RunProc("StreamsCount", this);
  }
  #endif
} tStreamsCount, *pStreamsCount;

typedef struct tStreamsQueued
{
  char   Queue[17]; /* i  */
  char   filler1[3];
  int32  DayInterval; /* i  */
  int8   Status; /* i  */
  char   filler3[3];
  int32  Id; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(DayInterval);
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(Queue, sizeof(Queue));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsQueued")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Queue>");XRec.ampappend(Queue);XRec.append("</Queue>\n");
    char Work[32];
    XRec.append("  <DayInterval>");sprintf(Work, "%d", DayInterval);XRec.append(Work);XRec.append("</DayInterval>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
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
    msg.GetValue("Queue", work);memcpy(Queue, work.data, sizeof(Queue)-1);
    msg.GetValue("DayInterval", work);DayInterval = atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsQueued")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.add("Queue", Queue, sizeof(Queue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("DayInterval", DayInterval, "i");
    #else
    dBuild.add("DayInterval", DayInterval);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.set("Queue", Queue, sizeof(Queue));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("DayInterval", DayInterval, sizeof(DayInterval), "i");
    #else
    dBuild.set("DayInterval", DayInterval, sizeof(DayInterval));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsQueued()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsQueued, *pStreamsQueued;

#if defined(_DBPORTAL_H_)
struct tStreamsQueuedQuery : public tDBQuery
{
  tStreamsQueuedQuery(tDBConnect& C, pStreamsQueued D, bool DoExec=true)
  : tDBQuery(C, "StreamsQueued", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByStatusQueueDate
{
  char   Queue[17]; /* i  */
  int8   Status; /* i  */
  char   DateFrom[15]; /* i  */
  char   filler3[3];
  int32  Id; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler5[1];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(Queue, sizeof(Queue));
    TrimTrailingBlanks(DateFrom, sizeof(DateFrom));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByStatusQueueDate")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Queue>");XRec.ampappend(Queue);XRec.append("</Queue>\n");
    char Work[32];
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateFrom>");XRec.ampappend(DateFrom);XRec.append("</DateFrom>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
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
    msg.GetValue("Queue", work);memcpy(Queue, work.data, sizeof(Queue)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateFrom", work);memcpy(DateFrom, work.data, sizeof(DateFrom)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByStatusQueueDate")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.add("Queue", Queue, sizeof(Queue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(1);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.set("Queue", Queue, sizeof(Queue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(1);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByStatusQueueDate()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByStatusQueueDate, *pStreamsByStatusQueueDate;

#if defined(_DBPORTAL_H_)
struct tStreamsByStatusQueueDateQuery : public tDBQuery
{
  tStreamsByStatusQueueDateQuery(tDBConnect& C, pStreamsByStatusQueueDate D, bool DoExec=true)
  : tDBQuery(C, "StreamsByStatusQueueDate", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsRevertStatusToNone
{
  char   Queue[17]; /* i  */
  char   filler1[3];
  int32  StatusNone; /* i  */
  int32  StatusSent; /* i  */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(StatusNone);
    SwapBytes(StatusSent);
  }
  void Trims()
  {
    TrimTrailingBlanks(Queue, sizeof(Queue));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsRevertStatusToNone")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <Queue>");XRec.ampappend(Queue);XRec.append("</Queue>\n");
    char Work[32];
    XRec.append("  <StatusNone>");sprintf(Work, "%d", StatusNone);XRec.append(Work);XRec.append("</StatusNone>\n");
    XRec.append("  <StatusSent>");sprintf(Work, "%d", StatusSent);XRec.append(Work);XRec.append("</StatusSent>\n");
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
    msg.GetValue("Queue", work);memcpy(Queue, work.data, sizeof(Queue)-1);
    msg.GetValue("StatusNone", work);StatusNone = atoi(work.data);
    msg.GetValue("StatusSent", work);StatusSent = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsRevertStatusToNone")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.add("Queue", Queue, sizeof(Queue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("StatusNone", StatusNone, "i");
    #else
    dBuild.add("StatusNone", StatusNone);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StatusSent", StatusSent, "i");
    #else
    dBuild.add("StatusSent", StatusSent);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Queue", Queue, sizeof(Queue), "i");
    #else
    dBuild.set("Queue", Queue, sizeof(Queue));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("StatusNone", StatusNone, sizeof(StatusNone), "i");
    #else
    dBuild.set("StatusNone", StatusNone, sizeof(StatusNone));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StatusSent", StatusSent, sizeof(StatusSent), "i");
    #else
    dBuild.set("StatusSent", StatusSent, sizeof(StatusSent));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsRevertStatusToNone()
  {
    memset(this, 0, sizeof(*this));
  }
  void RevertStatusToNone(tDBConnect& C)
  {
    C.RunProc("StreamsRevertStatusToNone", this);
  }
  void RevertStatusToNone(tDBConnect& C, const char *aQueue, int32 aStatusNone
        , int32 aStatusSent)
  {
    strncpyz(Queue, aQueue, sizeof(Queue)-1);
    StatusNone = aStatusNone;
    StatusSent = aStatusSent;
    C.RunProc("StreamsRevertStatusToNone", this);
  }
  #endif
} tStreamsRevertStatusToNone, *pStreamsRevertStatusToNone;

typedef struct tStreamsUpdateStatus
{
  int32  Id; /* i  */
  char   StreamRef[256]; /* i  */
  int8   Status; /* i  */
  char   USId[17]; /* i  */
  char   filler4[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(USId, sizeof(USId));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsUpdateStatus")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsUpdateStatus")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "i");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "i");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsUpdateStatus()
  {
    memset(this, 0, sizeof(*this));
  }
  void UpdateStatus(tDBConnect& C)
  {
    C.RunProc("StreamsUpdateStatus", this);
  }
  void UpdateStatus(tDBConnect& C, int32 aId, const char *aStreamRef, int8 aStatus
        , const char *aUSId)
  {
    Id = aId;
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    Status = aStatus;
    strncpyz(USId, aUSId, sizeof(USId)-1);
    C.RunProc("StreamsUpdateStatus", this);
  }
  #endif
} tStreamsUpdateStatus, *pStreamsUpdateStatus;

typedef struct tStreamsForUpd
{
  int32  Id; /* i  */
  char   QueueId[17]; /* i  */
  int8   Status; /* i  */
  char   EventQueueID[17]; /*  o */
  char   StreamRef[256]; /*  o */
  char   filler5[1];
  int32  MessageLen; /*  o */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  char   filler9[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueID, sizeof(EventQueueID));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsForUpd")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <EventQueueID>");XRec.ampappend(EventQueueID);XRec.append("</EventQueueID>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("EventQueueID", work);memcpy(EventQueueID, work.data, sizeof(EventQueueID)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsForUpd")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(9);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(9);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsForUpd()
  {
    memset(this, 0, sizeof(*this));
  }
  bool ForUpdReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsForUpd", this);
  }
  bool ForUpd(tDBConnect& C, int32 aId, const char *aQueueId, int8 aStatus)
  {
    Id = aId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    Status = aStatus;
    return C.ReadOne("StreamsForUpd", this);
  }
  void ForUpd(tDBConnect& C)
  {
    C.RunProc("StreamsForUpd", this);
  }
  #endif
} tStreamsForUpd, *pStreamsForUpd;

typedef struct tStreamsGetByRef
{
  char   StreamRef[256]; /* i  */
  int32  ID; /* i  */
  int32  MessageID; /* i  */
  char   EventQueueID[17]; /* i  */
  char   ReplyQueueID[17]; /* i  */
  char   filler5[2];
  int32  StreamCount; /* i  */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(ID);
    SwapBytes(MessageID);
    SwapBytes(StreamCount);
  }
  void Trims()
  {
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(EventQueueID, sizeof(EventQueueID));
    TrimTrailingBlanks(ReplyQueueID, sizeof(ReplyQueueID));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsGetByRef")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    char Work[32];
    XRec.append("  <ID>");sprintf(Work, "%d", ID);XRec.append(Work);XRec.append("</ID>\n");
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <EventQueueID>");XRec.ampappend(EventQueueID);XRec.append("</EventQueueID>\n");
    XRec.append("  <ReplyQueueID>");XRec.ampappend(ReplyQueueID);XRec.append("</ReplyQueueID>\n");
    XRec.append("  <StreamCount>");sprintf(Work, "%d", StreamCount);XRec.append(Work);XRec.append("</StreamCount>\n");
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
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("ID", work);ID = atoi(work.data);
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("EventQueueID", work);memcpy(EventQueueID, work.data, sizeof(EventQueueID)-1);
    msg.GetValue("ReplyQueueID", work);memcpy(ReplyQueueID, work.data, sizeof(ReplyQueueID)-1);
    msg.GetValue("StreamCount", work);StreamCount = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsGetByRef")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(6);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ID", ID, "i");
    #else
    dBuild.add("ID", ID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "i");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID), "i");
    #else
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "i");
    #else
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("StreamCount", StreamCount, "i");
    #else
    dBuild.add("StreamCount", StreamCount);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(6);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ID", ID, sizeof(ID), "i");
    #else
    dBuild.set("ID", ID, sizeof(ID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "i");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID), "i");
    #else
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "i");
    #else
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount), "i");
    #else
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsGetByRef()
  {
    memset(this, 0, sizeof(*this));
  }
  void GetByRef(tDBConnect& C)
  {
    C.RunProc("StreamsGetByRef", this);
  }
  void GetByRef(tDBConnect& C, const char *aStreamRef, int32 aID, int32 aMessageID
        , const char *aEventQueueID, const char *aReplyQueueID, int32 aStreamCount
        )
  {
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    ID = aID;
    MessageID = aMessageID;
    strncpyz(EventQueueID, aEventQueueID, sizeof(EventQueueID)-1);
    strncpyz(ReplyQueueID, aReplyQueueID, sizeof(ReplyQueueID)-1);
    StreamCount = aStreamCount;
    C.RunProc("StreamsGetByRef", this);
  }
  #endif
} tStreamsGetByRef, *pStreamsGetByRef;

typedef struct tStreamsGetByQRef
{
  char   StreamRef[256]; /* i  */
  char   QueueId[17]; /* i  */
  int8   Status; /* i  */
  char   filler3[2];
  int32  ID; /* i  */
  int32  MessageID; /* i  */
  char   EventQueueID[17]; /* i  */
  char   ReplyQueueID[17]; /* i  */
  char   ReplyQueue[65]; /* i  */
  char   filler8[1];
  int32  StreamCount; /* i  */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(ID);
    SwapBytes(MessageID);
    SwapBytes(StreamCount);
  }
  void Trims()
  {
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueID, sizeof(EventQueueID));
    TrimTrailingBlanks(ReplyQueueID, sizeof(ReplyQueueID));
    TrimTrailingBlanks(ReplyQueue, sizeof(ReplyQueue));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsGetByQRef")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    char Work[32];
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <ID>");sprintf(Work, "%d", ID);XRec.append(Work);XRec.append("</ID>\n");
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <EventQueueID>");XRec.ampappend(EventQueueID);XRec.append("</EventQueueID>\n");
    XRec.append("  <ReplyQueueID>");XRec.ampappend(ReplyQueueID);XRec.append("</ReplyQueueID>\n");
    XRec.append("  <ReplyQueue>");XRec.ampappend(ReplyQueue);XRec.append("</ReplyQueue>\n");
    XRec.append("  <StreamCount>");sprintf(Work, "%d", StreamCount);XRec.append(Work);XRec.append("</StreamCount>\n");
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
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("ID", work);ID = atoi(work.data);
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("EventQueueID", work);memcpy(EventQueueID, work.data, sizeof(EventQueueID)-1);
    msg.GetValue("ReplyQueueID", work);memcpy(ReplyQueueID, work.data, sizeof(ReplyQueueID)-1);
    msg.GetValue("ReplyQueue", work);memcpy(ReplyQueue, work.data, sizeof(ReplyQueue)-1);
    msg.GetValue("StreamCount", work);StreamCount = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsGetByQRef")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(9);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("ID", ID, "i");
    #else
    dBuild.add("ID", ID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "i");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID), "i");
    #else
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "i");
    #else
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueue", ReplyQueue, sizeof(ReplyQueue), "i");
    #else
    dBuild.add("ReplyQueue", ReplyQueue, sizeof(ReplyQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("StreamCount", StreamCount, "i");
    #else
    dBuild.add("StreamCount", StreamCount);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(9);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("ID", ID, sizeof(ID), "i");
    #else
    dBuild.set("ID", ID, sizeof(ID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "i");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID), "i");
    #else
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "i");
    #else
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueue", ReplyQueue, sizeof(ReplyQueue), "i");
    #else
    dBuild.set("ReplyQueue", ReplyQueue, sizeof(ReplyQueue));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount), "i");
    #else
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsGetByQRef()
  {
    memset(this, 0, sizeof(*this));
  }
  void GetByQRef(tDBConnect& C)
  {
    C.RunProc("StreamsGetByQRef", this);
  }
  void GetByQRef(tDBConnect& C, const char *aStreamRef, const char *aQueueId
        , int8 aStatus, int32 aID, int32 aMessageID, const char *aEventQueueID
        , const char *aReplyQueueID, const char *aReplyQueue, int32 aStreamCount
        )
  {
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    Status = aStatus;
    ID = aID;
    MessageID = aMessageID;
    strncpyz(EventQueueID, aEventQueueID, sizeof(EventQueueID)-1);
    strncpyz(ReplyQueueID, aReplyQueueID, sizeof(ReplyQueueID)-1);
    strncpyz(ReplyQueue, aReplyQueue, sizeof(ReplyQueue)-1);
    StreamCount = aStreamCount;
    C.RunProc("StreamsGetByQRef", this);
  }
  #endif
} tStreamsGetByQRef, *pStreamsGetByQRef;

typedef struct tStreamsGetById
{
  int32  ID; /* io */
  int32  MessageID; /*  o */
  char   EventQueueID[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   filler4[2];
  int32  StreamCount; /*  o */
  char   ReplyQueueID[17]; /*  o */
  int8   Status; /*  o */
  char   filler7[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(ID);
    SwapBytes(MessageID);
    SwapBytes(StreamCount);
  }
  void Trims()
  {
    TrimTrailingBlanks(EventQueueID, sizeof(EventQueueID));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(ReplyQueueID, sizeof(ReplyQueueID));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsGetById")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <ID>");sprintf(Work, "%d", ID);XRec.append(Work);XRec.append("</ID>\n");
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <EventQueueID>");XRec.ampappend(EventQueueID);XRec.append("</EventQueueID>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamCount>");sprintf(Work, "%d", StreamCount);XRec.append(Work);XRec.append("</StreamCount>\n");
    XRec.append("  <ReplyQueueID>");XRec.ampappend(ReplyQueueID);XRec.append("</ReplyQueueID>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
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
    msg.GetValue("ID", work);ID = atoi(work.data);
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("EventQueueID", work);memcpy(EventQueueID, work.data, sizeof(EventQueueID)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamCount", work);StreamCount = atoi(work.data);
    msg.GetValue("ReplyQueueID", work);memcpy(ReplyQueueID, work.data, sizeof(ReplyQueueID)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsGetById")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(7);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ID", ID, "io");
    #else
    dBuild.add("ID", ID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "o");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("StreamCount", StreamCount, "o");
    #else
    dBuild.add("StreamCount", StreamCount);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(7);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ID", ID, sizeof(ID), "io");
    #else
    dBuild.set("ID", ID, sizeof(ID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "o");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount), "o");
    #else
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsGetById()
  {
    memset(this, 0, sizeof(*this));
  }
  bool GetByIdReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsGetById", this);
  }
  bool GetById(tDBConnect& C, int32 aID)
  {
    ID = aID;
    return C.ReadOne("StreamsGetById", this);
  }
  void GetById(tDBConnect& C)
  {
    C.RunProc("StreamsGetById", this);
  }
  #endif
} tStreamsGetById, *pStreamsGetById;

typedef struct tStreamsGetMsgStatus
{
  int32  MessageID; /* i  */
  int32  MyCount; /*  o */
  int8   Status; /*  o */
  char   filler3[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MessageID);
    SwapBytes(MyCount);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsGetMsgStatus")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <MyCount>");sprintf(Work, "%d", MyCount);XRec.append(Work);XRec.append("</MyCount>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
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
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("MyCount", work);MyCount = atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsGetMsgStatus")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "i");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MyCount", MyCount, "o");
    #else
    dBuild.add("MyCount", MyCount);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "i");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MyCount", MyCount, sizeof(MyCount), "o");
    #else
    dBuild.set("MyCount", MyCount, sizeof(MyCount));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsGetMsgStatus()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsGetMsgStatus, *pStreamsGetMsgStatus;

#if defined(_DBPORTAL_H_)
struct tStreamsGetMsgStatusQuery : public tDBQuery
{
  tStreamsGetMsgStatusQuery(tDBConnect& C, pStreamsGetMsgStatus D, bool DoExec=true)
  : tDBQuery(C, "StreamsGetMsgStatus", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsCountMsgStatus
{
  int32  MessageID; /* i  */
  int8   Status; /* i  */
  char   filler2[3];
  int32  MyCount; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MessageID);
    SwapBytes(MyCount);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsCountMsgStatus")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <MyCount>");sprintf(Work, "%d", MyCount);XRec.append(Work);XRec.append("</MyCount>\n");
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
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("MyCount", work);MyCount = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsCountMsgStatus")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "i");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("MyCount", MyCount, "o");
    #else
    dBuild.add("MyCount", MyCount);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "i");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MyCount", MyCount, sizeof(MyCount), "o");
    #else
    dBuild.set("MyCount", MyCount, sizeof(MyCount));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsCountMsgStatus()
  {
    memset(this, 0, sizeof(*this));
  }
  bool CountMsgStatusReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsCountMsgStatus", this);
  }
  bool CountMsgStatus(tDBConnect& C, int32 aMessageID, int8 aStatus)
  {
    MessageID = aMessageID;
    Status = aStatus;
    return C.ReadOne("StreamsCountMsgStatus", this);
  }
  void CountMsgStatus(tDBConnect& C)
  {
    C.RunProc("StreamsCountMsgStatus", this);
  }
  #endif
} tStreamsCountMsgStatus, *pStreamsCountMsgStatus;

typedef struct tStreamsCountMsgStatusMultiple
{
  int32  StreamID; /* i  */
  int8   Status1; /* i  */
  int8   Status2; /* i  */
  char   filler3[2];
  int32  MyCount1; /*  o */
  int32  MyCount2; /*  o */
  int32  StreamCount; /*  o */
  int32  MessageID; /*  o */
  char   ReplyQueueID[17]; /*  o */
  char   filler8[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(StreamID);
    SwapBytes(MyCount1);
    SwapBytes(MyCount2);
    SwapBytes(StreamCount);
    SwapBytes(MessageID);
  }
  void Trims()
  {
    TrimTrailingBlanks(ReplyQueueID, sizeof(ReplyQueueID));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsCountMsgStatusMultiple")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <StreamID>");sprintf(Work, "%d", StreamID);XRec.append(Work);XRec.append("</StreamID>\n");
    XRec.append("  <Status1>");sprintf(Work, "%d", Status1);XRec.append(Work);XRec.append("</Status1>\n");
    XRec.append("  <Status2>");sprintf(Work, "%d", Status2);XRec.append(Work);XRec.append("</Status2>\n");
    XRec.append("  <MyCount1>");sprintf(Work, "%d", MyCount1);XRec.append(Work);XRec.append("</MyCount1>\n");
    XRec.append("  <MyCount2>");sprintf(Work, "%d", MyCount2);XRec.append(Work);XRec.append("</MyCount2>\n");
    XRec.append("  <StreamCount>");sprintf(Work, "%d", StreamCount);XRec.append(Work);XRec.append("</StreamCount>\n");
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <ReplyQueueID>");XRec.ampappend(ReplyQueueID);XRec.append("</ReplyQueueID>\n");
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
    msg.GetValue("StreamID", work);StreamID = atoi(work.data);
    msg.GetValue("Status1", work);Status1 = (int8)atoi(work.data);
    msg.GetValue("Status2", work);Status2 = (int8)atoi(work.data);
    msg.GetValue("MyCount1", work);MyCount1 = atoi(work.data);
    msg.GetValue("MyCount2", work);MyCount2 = atoi(work.data);
    msg.GetValue("StreamCount", work);StreamCount = atoi(work.data);
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("ReplyQueueID", work);memcpy(ReplyQueueID, work.data, sizeof(ReplyQueueID)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsCountMsgStatusMultiple")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(8);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamID", StreamID, "i");
    #else
    dBuild.add("StreamID", StreamID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status1", Status1, "i");
    #else
    dBuild.add("Status1", Status1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status2", Status2, "i");
    #else
    dBuild.add("Status2", Status2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MyCount1", MyCount1, "o");
    #else
    dBuild.add("MyCount1", MyCount1);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MyCount2", MyCount2, "o");
    #else
    dBuild.add("MyCount2", MyCount2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamCount", StreamCount, "o");
    #else
    dBuild.add("StreamCount", StreamCount);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageID", MessageID, "o");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(8);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamID", StreamID, sizeof(StreamID), "i");
    #else
    dBuild.set("StreamID", StreamID, sizeof(StreamID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status1", Status1, sizeof(Status1), "i");
    #else
    dBuild.set("Status1", Status1, sizeof(Status1));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status2", Status2, sizeof(Status2), "i");
    #else
    dBuild.set("Status2", Status2, sizeof(Status2));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MyCount1", MyCount1, sizeof(MyCount1), "o");
    #else
    dBuild.set("MyCount1", MyCount1, sizeof(MyCount1));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MyCount2", MyCount2, sizeof(MyCount2), "o");
    #else
    dBuild.set("MyCount2", MyCount2, sizeof(MyCount2));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount), "o");
    #else
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "o");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsCountMsgStatusMultiple()
  {
    memset(this, 0, sizeof(*this));
  }
  bool CountMsgStatusMultipleReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsCountMsgStatusMultiple", this);
  }
  bool CountMsgStatusMultiple(tDBConnect& C, int32 aStreamID, int8 aStatus1
        , int8 aStatus2)
  {
    StreamID = aStreamID;
    Status1 = aStatus1;
    Status2 = aStatus2;
    return C.ReadOne("StreamsCountMsgStatusMultiple", this);
  }
  void CountMsgStatusMultiple(tDBConnect& C)
  {
    C.RunProc("StreamsCountMsgStatusMultiple", this);
  }
  #endif
} tStreamsCountMsgStatusMultiple, *pStreamsCountMsgStatusMultiple;

typedef struct tStreamsByQueueDate
{
  char   QueueId[17]; /* i  */
  char   DateFrom[15]; /* i  */
  char   DateTo[15]; /* i  */
  char   filler3[1];
  int32  MaxRows; /* i  */
  int32  Id; /*  o */
  char   SourceSysid[17]; /*  o */
  char   Reference[65]; /*  o */
  int8   MessageType; /*  o */
  char   DateCreated[15]; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MaxRows);
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(DateFrom, sizeof(DateFrom));
    TrimTrailingBlanks(DateTo, sizeof(DateTo));
    TrimTrailingBlanks(SourceSysid, sizeof(SourceSysid));
    TrimTrailingBlanks(Reference, sizeof(Reference));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByQueueDate")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <DateFrom>");XRec.ampappend(DateFrom);XRec.append("</DateFrom>\n");
    XRec.append("  <DateTo>");XRec.ampappend(DateTo);XRec.append("</DateTo>\n");
    char Work[32];
    XRec.append("  <MaxRows>");sprintf(Work, "%d", MaxRows);XRec.append(Work);XRec.append("</MaxRows>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <SourceSysid>");XRec.ampappend(SourceSysid);XRec.append("</SourceSysid>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
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
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("DateFrom", work);memcpy(DateFrom, work.data, sizeof(DateFrom)-1);
    msg.GetValue("DateTo", work);memcpy(DateTo, work.data, sizeof(DateTo)-1);
    msg.GetValue("MaxRows", work);MaxRows = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("SourceSysid", work);memcpy(SourceSysid, work.data, sizeof(SourceSysid)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByQueueDate")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(11);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.add("DateTo", DateTo, sizeof(DateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("MaxRows", MaxRows, "i");
    #else
    dBuild.add("MaxRows", MaxRows);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("SourceSysid", SourceSysid, sizeof(SourceSysid), "o");
    #else
    dBuild.add("SourceSysid", SourceSysid, sizeof(SourceSysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(11);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.set("DateTo", DateTo, sizeof(DateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MaxRows", MaxRows, sizeof(MaxRows), "i");
    #else
    dBuild.set("MaxRows", MaxRows, sizeof(MaxRows));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("SourceSysid", SourceSysid, sizeof(SourceSysid), "o");
    #else
    dBuild.set("SourceSysid", SourceSysid, sizeof(SourceSysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByQueueDate()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByQueueDate, *pStreamsByQueueDate;

#if defined(_DBPORTAL_H_)
struct tStreamsByQueueDateQuery : public tDBQuery
{
  tStreamsByQueueDateQuery(tDBConnect& C, pStreamsByQueueDate D, bool DoExec=true)
  : tDBQuery(C, "StreamsByQueueDate", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByDate
{
  char   DateFrom[15]; /* i  */
  char   DateTo[15]; /* i  */
  char   filler2[2];
  int32  MaxRows; /* i  */
  int32  Id; /*  o */
  char   QueueId[17]; /*  o */
  char   SourceSysid[17]; /*  o */
  char   Reference[65]; /*  o */
  int8   MessageType; /*  o */
  char   DateCreated[15]; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   filler11[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MaxRows);
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(DateFrom, sizeof(DateFrom));
    TrimTrailingBlanks(DateTo, sizeof(DateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(SourceSysid, sizeof(SourceSysid));
    TrimTrailingBlanks(Reference, sizeof(Reference));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByDate")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <DateFrom>");XRec.ampappend(DateFrom);XRec.append("</DateFrom>\n");
    XRec.append("  <DateTo>");XRec.ampappend(DateTo);XRec.append("</DateTo>\n");
    char Work[32];
    XRec.append("  <MaxRows>");sprintf(Work, "%d", MaxRows);XRec.append(Work);XRec.append("</MaxRows>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <SourceSysid>");XRec.ampappend(SourceSysid);XRec.append("</SourceSysid>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
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
    msg.GetValue("DateFrom", work);memcpy(DateFrom, work.data, sizeof(DateFrom)-1);
    msg.GetValue("DateTo", work);memcpy(DateTo, work.data, sizeof(DateTo)-1);
    msg.GetValue("MaxRows", work);MaxRows = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("SourceSysid", work);memcpy(SourceSysid, work.data, sizeof(SourceSysid)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByDate")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(11);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.add("DateTo", DateTo, sizeof(DateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MaxRows", MaxRows, "i");
    #else
    dBuild.add("MaxRows", MaxRows);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("SourceSysid", SourceSysid, sizeof(SourceSysid), "o");
    #else
    dBuild.add("SourceSysid", SourceSysid, sizeof(SourceSysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(11);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.set("DateTo", DateTo, sizeof(DateTo));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MaxRows", MaxRows, sizeof(MaxRows), "i");
    #else
    dBuild.set("MaxRows", MaxRows, sizeof(MaxRows));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("SourceSysid", SourceSysid, sizeof(SourceSysid), "o");
    #else
    dBuild.set("SourceSysid", SourceSysid, sizeof(SourceSysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByDate()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByDate, *pStreamsByDate;

#if defined(_DBPORTAL_H_)
struct tStreamsByDateQuery : public tDBQuery
{
  tStreamsByDateQuery(tDBConnect& C, pStreamsByDate D, bool DoExec=true)
  : tDBQuery(C, "StreamsByDate", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsQueues
{
  char   DateFrom[15]; /* i  */
  char   DateTo[15]; /* i  */
  char   Id[17]; /*  o */
  char   Name[65]; /*  o */
  char   Descr[257]; /*  o */
  char   filler5[3];
  int32  NoOf; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(NoOf);
  }
  void Trims()
  {
    TrimTrailingBlanks(DateFrom, sizeof(DateFrom));
    TrimTrailingBlanks(DateTo, sizeof(DateTo));
    TrimTrailingBlanks(Id, sizeof(Id));
    TrimTrailingBlanks(Name, sizeof(Name));
    TrimTrailingBlanks(Descr, sizeof(Descr));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsQueues")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <DateFrom>");XRec.ampappend(DateFrom);XRec.append("</DateFrom>\n");
    XRec.append("  <DateTo>");XRec.ampappend(DateTo);XRec.append("</DateTo>\n");
    XRec.append("  <Id>");XRec.ampappend(Id);XRec.append("</Id>\n");
    XRec.append("  <Name>");XRec.ampappend(Name);XRec.append("</Name>\n");
    XRec.append("  <Descr>");XRec.ampappend(Descr);XRec.append("</Descr>\n");
    char Work[32];
    XRec.append("  <NoOf>");sprintf(Work, "%d", NoOf);XRec.append(Work);XRec.append("</NoOf>\n");
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
    msg.GetValue("DateFrom", work);memcpy(DateFrom, work.data, sizeof(DateFrom)-1);
    msg.GetValue("DateTo", work);memcpy(DateTo, work.data, sizeof(DateTo)-1);
    msg.GetValue("Id", work);memcpy(Id, work.data, sizeof(Id)-1);
    msg.GetValue("Name", work);memcpy(Name, work.data, sizeof(Name)-1);
    msg.GetValue("Descr", work);memcpy(Descr, work.data, sizeof(Descr)-1);
    msg.GetValue("NoOf", work);NoOf = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsQueues")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(6);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.add("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.add("DateTo", DateTo, sizeof(DateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, sizeof(Id), "o");
    #else
    dBuild.add("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Name", Name, sizeof(Name), "o");
    #else
    dBuild.add("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Descr", Descr, sizeof(Descr), "o");
    #else
    dBuild.add("Descr", Descr, sizeof(Descr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("NoOf", NoOf, "o");
    #else
    dBuild.add("NoOf", NoOf);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(6);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom), "i");
    #else
    dBuild.set("DateFrom", DateFrom, sizeof(DateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateTo", DateTo, sizeof(DateTo), "i");
    #else
    dBuild.set("DateTo", DateTo, sizeof(DateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Name", Name, sizeof(Name), "o");
    #else
    dBuild.set("Name", Name, sizeof(Name));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Descr", Descr, sizeof(Descr), "o");
    #else
    dBuild.set("Descr", Descr, sizeof(Descr));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("NoOf", NoOf, sizeof(NoOf), "o");
    #else
    dBuild.set("NoOf", NoOf, sizeof(NoOf));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsQueues()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsQueues, *pStreamsQueues;

#if defined(_DBPORTAL_H_)
struct tStreamsQueuesQuery : public tDBQuery
{
  tStreamsQueuesQuery(tDBConnect& C, pStreamsQueues D, bool DoExec=true)
  : tDBQuery(C, "StreamsQueues", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsGetData
{
  int32  Id; /* i  */
  int8   MessageType; /*  o */
  char   filler2[3];
  int32  MessageLen; /*  o */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsGetData")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsGetData")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsGetData()
  {
    memset(this, 0, sizeof(*this));
  }
  bool GetDataReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsGetData", this);
  }
  bool GetData(tDBConnect& C, int32 aId)
  {
    Id = aId;
    return C.ReadOne("StreamsGetData", this);
  }
  void GetData(tDBConnect& C)
  {
    C.RunProc("StreamsGetData", this);
  }
  #endif
} tStreamsGetData, *pStreamsGetData;

typedef struct tStreamsRouterForUpd
{
  int32  Id; /* i  */
  char   QueueId[17]; /* i  */
  int8   Status; /* i  */
  char   filler3[2];
  int32  MessageId; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   filler6[2];
  int32  MessageLen; /*  o */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  char   filler10[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsRouterForUpd")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsRouterForUpd")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(10);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(10);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsRouterForUpd()
  {
    memset(this, 0, sizeof(*this));
  }
  bool RouterForUpdReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsRouterForUpd", this);
  }
  bool RouterForUpd(tDBConnect& C, int32 aId, const char *aQueueId, int8 aStatus
        )
  {
    Id = aId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    Status = aStatus;
    return C.ReadOne("StreamsRouterForUpd", this);
  }
  void RouterForUpd(tDBConnect& C)
  {
    C.RunProc("StreamsRouterForUpd", this);
  }
  #endif
} tStreamsRouterForUpd, *pStreamsRouterForUpd;

typedef struct tStreamsRoute
{
  int32  Id; /* i  */
  char   QueueId[17]; /* i  */
  char   USId[17]; /* i  */
  char   filler3[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(USId, sizeof(USId));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsRoute")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsRoute")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "i");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "i");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsRoute()
  {
    memset(this, 0, sizeof(*this));
  }
  void Route(tDBConnect& C)
  {
    C.RunProc("StreamsRoute", this);
  }
  void Route(tDBConnect& C, int32 aId, const char *aQueueId, const char *aUSId
        )
  {
    Id = aId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    C.RunProc("StreamsRoute", this);
  }
  #endif
} tStreamsRoute, *pStreamsRoute;

typedef struct tStreamsByMessageID
{
  int32  InMessageId; /* i  */
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler8[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler15[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(InMessageId);
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByMessageID")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <InMessageId>");sprintf(Work, "%d", InMessageId);XRec.append(Work);XRec.append("</InMessageId>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InMessageId", work);InMessageId = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByMessageID")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(15);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InMessageId", InMessageId, "i");
    #else
    dBuild.add("InMessageId", InMessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(15);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InMessageId", InMessageId, sizeof(InMessageId), "i");
    #else
    dBuild.set("InMessageId", InMessageId, sizeof(InMessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByMessageID()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByMessageID, *pStreamsByMessageID;

#if defined(_DBPORTAL_H_)
struct tStreamsByMessageIDQuery : public tDBQuery
{
  tStreamsByMessageIDQuery(tDBConnect& C, pStreamsByMessageID D, bool DoExec=true)
  : tDBQuery(C, "StreamsByMessageID", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByMessageIdStreamType
{
  int32  MessageId; /* io */
  char   StreamType[17]; /* io */
  char   filler2[3];
  int32  Id; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler7[3];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler14[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MessageId);
    SwapBytes(Id);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByMessageIdStreamType")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByMessageIdStreamType")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(14);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "io");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "io");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(14);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "io");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "io");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByMessageIdStreamType()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByMessageIdStreamType, *pStreamsByMessageIdStreamType;

#if defined(_DBPORTAL_H_)
struct tStreamsByMessageIdStreamTypeQuery : public tDBQuery
{
  tStreamsByMessageIdStreamTypeQuery(tDBConnect& C, pStreamsByMessageIdStreamType D, bool DoExec=true)
  : tDBQuery(C, "StreamsByMessageIdStreamType", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByMessageIDData
{
  int32  InMessageId; /* i  */
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler8[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler15[2];
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(InMessageId);
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByMessageIDData")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <InMessageId>");sprintf(Work, "%d", InMessageId);XRec.append(Work);XRec.append("</InMessageId>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
    XRec.append("  <TmStamp>");XRec.ampappend(TmStamp);XRec.append("</TmStamp>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
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
    msg.GetValue("InMessageId", work);InMessageId = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
    msg.GetValue("MessageData", work);/*unhandled*/;
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByMessageIDData")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(16);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InMessageId", InMessageId, "i");
    #else
    dBuild.add("InMessageId", InMessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(16);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InMessageId", InMessageId, sizeof(InMessageId), "i");
    #else
    dBuild.set("InMessageId", InMessageId, sizeof(InMessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByMessageIDData()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByMessageIDData, *pStreamsByMessageIDData;

#if defined(_DBPORTAL_H_)
struct tStreamsByMessageIDDataQuery : public tDBQuery
{
  tStreamsByMessageIDDataQuery(tDBConnect& C, pStreamsByMessageIDData D, bool DoExec=true)
  : tDBQuery(C, "StreamsByMessageIDData", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsUpdStatus
{
  int32  Id; /* i  */
  int8   Status; /* i  */
  char   USId[17]; /* i  */
  char   filler3[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(USId, sizeof(USId));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsUpdStatus")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsUpdStatus")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "i");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "i");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsUpdStatus()
  {
    memset(this, 0, sizeof(*this));
  }
  void UpdStatus(tDBConnect& C)
  {
    C.RunProc("StreamsUpdStatus", this);
  }
  void UpdStatus(tDBConnect& C, int32 aId, int8 aStatus, const char *aUSId)
  {
    Id = aId;
    Status = aStatus;
    strncpyz(USId, aUSId, sizeof(USId)-1);
    C.RunProc("StreamsUpdStatus", this);
  }
  #endif
} tStreamsUpdStatus, *pStreamsUpdStatus;

typedef struct tStreamsByQueue
{
  char   InQueue[17]; /* i  */
  int8   InStatus; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler11[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler18[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByQueue")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    char Work[32];
    XRec.append("  <InStatus>");sprintf(Work, "%d", InStatus);XRec.append(Work);XRec.append("</InStatus>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InStatus", work);InStatus = (int8)atoi(work.data);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByQueue")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(18);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InStatus", InStatus, "i");
    #else
    dBuild.add("InStatus", InStatus);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(18);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InStatus", InStatus, sizeof(InStatus), "i");
    #else
    dBuild.set("InStatus", InStatus, sizeof(InStatus));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByQueue()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByQueue, *pStreamsByQueue;

#if defined(_DBPORTAL_H_)
struct tStreamsByQueueQuery : public tDBQuery
{
  tStreamsByQueueQuery(tDBConnect& C, pStreamsByQueue D, bool DoExec=true)
  : tDBQuery(C, "StreamsByQueue", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByQueueAll
{
  char   InQueue[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   filler3[1];
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler10[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler17[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByQueueAll")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByQueueAll")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(17);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(17);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByQueueAll()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByQueueAll, *pStreamsByQueueAll;

#if defined(_DBPORTAL_H_)
struct tStreamsByQueueAllQuery : public tDBQuery
{
  tStreamsByQueueAllQuery(tDBConnect& C, pStreamsByQueueAll D, bool DoExec=true)
  : tDBQuery(C, "StreamsByQueueAll", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByQueueAlli3Batch
{
  char   InQueue[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   filler3[1];
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   Sourcesysid[17]; /*  o */
  char   Reference[65]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler19[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(Sourcesysid, sizeof(Sourcesysid));
    TrimTrailingBlanks(Reference, sizeof(Reference));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByQueueAlli3Batch")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <Sourcesysid>");XRec.ampappend(Sourcesysid);XRec.append("</Sourcesysid>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Sourcesysid", work);memcpy(Sourcesysid, work.data, sizeof(Sourcesysid)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByQueueAlli3Batch")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(19);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Sourcesysid", Sourcesysid, sizeof(Sourcesysid), "o");
    #else
    dBuild.add("Sourcesysid", Sourcesysid, sizeof(Sourcesysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(19);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Sourcesysid", Sourcesysid, sizeof(Sourcesysid), "o");
    #else
    dBuild.set("Sourcesysid", Sourcesysid, sizeof(Sourcesysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByQueueAlli3Batch()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByQueueAlli3Batch, *pStreamsByQueueAlli3Batch;

#if defined(_DBPORTAL_H_)
struct tStreamsByQueueAlli3BatchQuery : public tDBQuery
{
  tStreamsByQueueAlli3BatchQuery(tDBConnect& C, pStreamsByQueueAlli3Batch D, bool DoExec=true)
  : tDBQuery(C, "StreamsByQueueAlli3Batch", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByStreamRef
{
  char   InStreamRef[129]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   filler3[1];
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler10[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler17[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InStreamRef, sizeof(InStreamRef));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByStreamRef")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InStreamRef>");XRec.ampappend(InStreamRef);XRec.append("</InStreamRef>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InStreamRef", work);memcpy(InStreamRef, work.data, sizeof(InStreamRef)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByStreamRef")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(17);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InStreamRef", InStreamRef, sizeof(InStreamRef), "i");
    #else
    dBuild.add("InStreamRef", InStreamRef, sizeof(InStreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(17);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InStreamRef", InStreamRef, sizeof(InStreamRef), "i");
    #else
    dBuild.set("InStreamRef", InStreamRef, sizeof(InStreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByStreamRef()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByStreamRef, *pStreamsByStreamRef;

#if defined(_DBPORTAL_H_)
struct tStreamsByStreamRefQuery : public tDBQuery
{
  tStreamsByStreamRefQuery(tDBConnect& C, pStreamsByStreamRef D, bool DoExec=true)
  : tDBQuery(C, "StreamsByStreamRef", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByQStreamRef
{
  char   InQueue[17]; /* i  */
  char   InStreamRef[129]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler11[2];
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler18[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InStreamRef, sizeof(InStreamRef));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByQStreamRef")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    XRec.append("  <InStreamRef>");XRec.ampappend(InStreamRef);XRec.append("</InStreamRef>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InStreamRef", work);memcpy(InStreamRef, work.data, sizeof(InStreamRef)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByQStreamRef")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(18);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InStreamRef", InStreamRef, sizeof(InStreamRef), "i");
    #else
    dBuild.add("InStreamRef", InStreamRef, sizeof(InStreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(18);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InStreamRef", InStreamRef, sizeof(InStreamRef), "i");
    #else
    dBuild.set("InStreamRef", InStreamRef, sizeof(InStreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByQStreamRef()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByQStreamRef, *pStreamsByQStreamRef;

#if defined(_DBPORTAL_H_)
struct tStreamsByQStreamRefQuery : public tDBQuery
{
  tStreamsByQStreamRefQuery(tDBConnect& C, pStreamsByQStreamRef D, bool DoExec=true)
  : tDBQuery(C, "StreamsByQStreamRef", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsStatusCount
{
  int8   InStatus; /* i  */
  char   InQueueID[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  int32  Cnt; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Cnt);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueueID, sizeof(InQueueID));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsStatusCount")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <InStatus>");sprintf(Work, "%d", InStatus);XRec.append(Work);XRec.append("</InStatus>\n");
    XRec.append("  <InQueueID>");XRec.ampappend(InQueueID);XRec.append("</InQueueID>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    XRec.append("  <Cnt>");sprintf(Work, "%d", Cnt);XRec.append(Work);XRec.append("</Cnt>\n");
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
    msg.GetValue("InStatus", work);InStatus = (int8)atoi(work.data);
    msg.GetValue("InQueueID", work);memcpy(InQueueID, work.data, sizeof(InQueueID)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Cnt", work);Cnt = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsStatusCount")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InStatus", InStatus, "i");
    #else
    dBuild.add("InStatus", InStatus);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Cnt", Cnt, "o");
    #else
    dBuild.add("Cnt", Cnt);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InStatus", InStatus, sizeof(InStatus), "i");
    #else
    dBuild.set("InStatus", InStatus, sizeof(InStatus));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Cnt", Cnt, sizeof(Cnt), "o");
    #else
    dBuild.set("Cnt", Cnt, sizeof(Cnt));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsStatusCount()
  {
    memset(this, 0, sizeof(*this));
  }
  bool StatusCountReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsStatusCount", this);
  }
  bool StatusCount(tDBConnect& C, int8 aInStatus, const char *aInQueueID, const char *aInDateFrom
        , const char *aInDateTo)
  {
    InStatus = aInStatus;
    strncpyz(InQueueID, aInQueueID, sizeof(InQueueID)-1);
    strncpyz(InDateFrom, aInDateFrom, sizeof(InDateFrom)-1);
    strncpyz(InDateTo, aInDateTo, sizeof(InDateTo)-1);
    return C.ReadOne("StreamsStatusCount", this);
  }
  void StatusCount(tDBConnect& C)
  {
    C.RunProc("StreamsStatusCount", this);
  }
  #endif
} tStreamsStatusCount, *pStreamsStatusCount;

typedef struct tStreamsStatusCountAll
{
  char   InQueueID[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   filler3[1];
  int32  Cnt; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Cnt);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueueID, sizeof(InQueueID));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsStatusCountAll")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueueID>");XRec.ampappend(InQueueID);XRec.append("</InQueueID>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Cnt>");sprintf(Work, "%d", Cnt);XRec.append(Work);XRec.append("</Cnt>\n");
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
    msg.GetValue("InQueueID", work);memcpy(InQueueID, work.data, sizeof(InQueueID)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Cnt", work);Cnt = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsStatusCountAll")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("Cnt", Cnt, "o");
    #else
    dBuild.add("Cnt", Cnt);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Cnt", Cnt, sizeof(Cnt), "o");
    #else
    dBuild.set("Cnt", Cnt, sizeof(Cnt));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsStatusCountAll()
  {
    memset(this, 0, sizeof(*this));
  }
  bool StatusCountAllReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsStatusCountAll", this);
  }
  bool StatusCountAll(tDBConnect& C, const char *aInQueueID, const char *aInDateFrom
        , const char *aInDateTo)
  {
    strncpyz(InQueueID, aInQueueID, sizeof(InQueueID)-1);
    strncpyz(InDateFrom, aInDateFrom, sizeof(InDateFrom)-1);
    strncpyz(InDateTo, aInDateTo, sizeof(InDateTo)-1);
    return C.ReadOne("StreamsStatusCountAll", this);
  }
  void StatusCountAll(tDBConnect& C)
  {
    C.RunProc("StreamsStatusCountAll", this);
  }
  #endif
} tStreamsStatusCountAll, *pStreamsStatusCountAll;

typedef struct tStreamsByID
{
  int32  InID; /* i  */
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  char   filler8[2];
  int32  MessageLen; /*  o */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler16[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(InID);
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByID")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <InID>");sprintf(Work, "%d", InID);XRec.append(Work);XRec.append("</InID>\n");
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InID", work);InID = atoi(work.data);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByID")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(16);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InID", InID, "i");
    #else
    dBuild.add("InID", InID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(16);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InID", InID, sizeof(InID), "i");
    #else
    dBuild.set("InID", InID, sizeof(InID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByID()
  {
    memset(this, 0, sizeof(*this));
  }
  bool ByIDReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsByID", this);
  }
  bool ByID(tDBConnect& C, int32 aInID)
  {
    InID = aInID;
    return C.ReadOne("StreamsByID", this);
  }
  void ByID(tDBConnect& C)
  {
    C.RunProc("StreamsByID", this);
  }
  #endif
} tStreamsByID, *pStreamsByID;

typedef struct tStreamsUpdQueue
{
  int32  InMsgNo; /* i  */
  char   InQueueID[17]; /* i  */
  char   filler2[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(InMsgNo);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueueID, sizeof(InQueueID));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsUpdQueue")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <InMsgNo>");sprintf(Work, "%d", InMsgNo);XRec.append(Work);XRec.append("</InMsgNo>\n");
    XRec.append("  <InQueueID>");XRec.ampappend(InQueueID);XRec.append("</InQueueID>\n");
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
    msg.GetValue("InMsgNo", work);InMsgNo = atoi(work.data);
    msg.GetValue("InQueueID", work);memcpy(InQueueID, work.data, sizeof(InQueueID)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsUpdQueue")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InMsgNo", InMsgNo, "i");
    #else
    dBuild.add("InMsgNo", InMsgNo);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.add("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InMsgNo", InMsgNo, sizeof(InMsgNo), "i");
    #else
    dBuild.set("InMsgNo", InMsgNo, sizeof(InMsgNo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID), "i");
    #else
    dBuild.set("InQueueID", InQueueID, sizeof(InQueueID));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsUpdQueue()
  {
    memset(this, 0, sizeof(*this));
  }
  void UpdQueue(tDBConnect& C)
  {
    C.RunProc("StreamsUpdQueue", this);
  }
  void UpdQueue(tDBConnect& C, int32 aInMsgNo, const char *aInQueueID)
  {
    InMsgNo = aInMsgNo;
    strncpyz(InQueueID, aInQueueID, sizeof(InQueueID)-1);
    C.RunProc("StreamsUpdQueue", this);
  }
  #endif
} tStreamsUpdQueue, *pStreamsUpdQueue;

typedef struct tStreamsFilemqForUpd
{
  int32  Id; /* i  */
  char   QueueId[17]; /* i  */
  int8   Status; /* i  */
  char   filler3[2];
  int32  MessageID; /*  o */
  char   EventQueueID[17]; /*  o */
  char   StreamRef[256]; /*  o */
  char   filler6[3];
  int32  MessageLen; /*  o */
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  char   filler10[2];
  int32  StreamCount; /*  o */
  char   ReplyQueueID[17]; /*  o */
  char   filler12[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageID);
    SwapBytes(MessageLen);
    SwapBytes(MessageData.len);
    SwapBytes(StreamCount);
  }
  void Trims()
  {
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(EventQueueID, sizeof(EventQueueID));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(ReplyQueueID, sizeof(ReplyQueueID));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsFilemqForUpd")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <MessageID>");sprintf(Work, "%d", MessageID);XRec.append(Work);XRec.append("</MessageID>\n");
    XRec.append("  <EventQueueID>");XRec.ampappend(EventQueueID);XRec.append("</EventQueueID>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <StreamCount>");sprintf(Work, "%d", StreamCount);XRec.append(Work);XRec.append("</StreamCount>\n");
    XRec.append("  <ReplyQueueID>");XRec.ampappend(ReplyQueueID);XRec.append("</ReplyQueueID>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("MessageID", work);MessageID = atoi(work.data);
    msg.GetValue("EventQueueID", work);memcpy(EventQueueID, work.data, sizeof(EventQueueID)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("StreamCount", work);StreamCount = atoi(work.data);
    msg.GetValue("ReplyQueueID", work);memcpy(ReplyQueueID, work.data, sizeof(ReplyQueueID)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsFilemqForUpd")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(12);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "i");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("MessageID", MessageID, "o");
    #else
    dBuild.add("MessageID", MessageID);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.add("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(3);
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(2);
    dBuild.add("StreamCount", StreamCount, "o");
    #else
    dBuild.add("StreamCount", StreamCount);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.add("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(12);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "i");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "i");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageID", MessageID, sizeof(MessageID), "o");
    #else
    dBuild.set("MessageID", MessageID, sizeof(MessageID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID), "o");
    #else
    dBuild.set("EventQueueID", EventQueueID, sizeof(EventQueueID));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    dBuild.skip(3);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    dBuild.skip(2);
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount), "o");
    #else
    dBuild.set("StreamCount", StreamCount, sizeof(StreamCount));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID), "o");
    #else
    dBuild.set("ReplyQueueID", ReplyQueueID, sizeof(ReplyQueueID));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsFilemqForUpd()
  {
    memset(this, 0, sizeof(*this));
  }
  bool FilemqForUpdReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsFilemqForUpd", this);
  }
  bool FilemqForUpd(tDBConnect& C, int32 aId, const char *aQueueId, int8 aStatus
        )
  {
    Id = aId;
    strncpyz(QueueId, aQueueId, sizeof(QueueId)-1);
    Status = aStatus;
    return C.ReadOne("StreamsFilemqForUpd", this);
  }
  void FilemqForUpd(tDBConnect& C)
  {
    C.RunProc("StreamsFilemqForUpd", this);
  }
  #endif
} tStreamsFilemqForUpd, *pStreamsFilemqForUpd;

typedef struct tStreamsUpdateStreamRef
{
  int32  Id; /* i  */
  char   StreamRef[256]; /* i  */
  char   USId[17]; /* i  */
  char   filler3[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
  }
  void Trims()
  {
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(USId, sizeof(USId));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsUpdateStreamRef")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <USId>");XRec.ampappend(USId);XRec.append("</USId>\n");
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
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsUpdateStreamRef")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Id", Id, "i");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "i");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(3);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "i");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "i");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "i");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsUpdateStreamRef()
  {
    memset(this, 0, sizeof(*this));
  }
  void UpdateStreamRef(tDBConnect& C)
  {
    C.RunProc("StreamsUpdateStreamRef", this);
  }
  void UpdateStreamRef(tDBConnect& C, int32 aId, const char *aStreamRef, const char *aUSId
        )
  {
    Id = aId;
    strncpyz(StreamRef, aStreamRef, sizeof(StreamRef)-1);
    strncpyz(USId, aUSId, sizeof(USId)-1);
    C.RunProc("StreamsUpdateStreamRef", this);
  }
  #endif
} tStreamsUpdateStreamRef, *pStreamsUpdateStreamRef;

typedef struct tStreamsBySourceAlli3BatchRTGS
{
  char   InQueue[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   filler3[1];
  int32  Id; /*  o */
  int32  MessageId; /*  o */
  char   QueueId[17]; /*  o */
  char   Sourcesysid[17]; /*  o */
  char   Reference[17]; /*  o */
  char   EventQueueId[17]; /*  o */
  char   StreamRef[129]; /*  o */
  char   StreamType[17]; /*  o */
  char   StreamDescr[66]; /*  o */
  int32  MessageLen; /*  o */
  int8   MessageType; /*  o */
  int8   Priority; /*  o */
  int8   Status; /*  o */
  char   DateCreated[15]; /*  o */
  char   USId[17]; /*  o */
  char   TmStamp[15]; /*  o */
  char   filler19[2];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(Id);
    SwapBytes(MessageId);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(QueueId, sizeof(QueueId));
    TrimTrailingBlanks(Sourcesysid, sizeof(Sourcesysid));
    TrimTrailingBlanks(Reference, sizeof(Reference));
    TrimTrailingBlanks(EventQueueId, sizeof(EventQueueId));
    TrimTrailingBlanks(StreamRef, sizeof(StreamRef));
    TrimTrailingBlanks(StreamType, sizeof(StreamType));
    TrimTrailingBlanks(StreamDescr, sizeof(StreamDescr));
    TrimTrailingBlanks(DateCreated, sizeof(DateCreated));
    TrimTrailingBlanks(USId, sizeof(USId));
    TrimTrailingBlanks(TmStamp, sizeof(TmStamp));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsBySourceAlli3BatchRTGS")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    char Work[32];
    XRec.append("  <Id>");sprintf(Work, "%d", Id);XRec.append(Work);XRec.append("</Id>\n");
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <QueueId>");XRec.ampappend(QueueId);XRec.append("</QueueId>\n");
    XRec.append("  <Sourcesysid>");XRec.ampappend(Sourcesysid);XRec.append("</Sourcesysid>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
    XRec.append("  <EventQueueId>");XRec.ampappend(EventQueueId);XRec.append("</EventQueueId>\n");
    XRec.append("  <StreamRef>");XRec.ampappend(StreamRef);XRec.append("</StreamRef>\n");
    XRec.append("  <StreamType>");XRec.ampappend(StreamType);XRec.append("</StreamType>\n");
    XRec.append("  <StreamDescr>");XRec.ampappend(StreamDescr);XRec.append("</StreamDescr>\n");
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
    XRec.append("  <MessageType>");sprintf(Work, "%d", MessageType);XRec.append(Work);XRec.append("</MessageType>\n");
    XRec.append("  <Priority>");sprintf(Work, "%d", Priority);XRec.append(Work);XRec.append("</Priority>\n");
    XRec.append("  <Status>");sprintf(Work, "%d", Status);XRec.append(Work);XRec.append("</Status>\n");
    XRec.append("  <DateCreated>");XRec.ampappend(DateCreated);XRec.append("</DateCreated>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Id", work);Id = atoi(work.data);
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("QueueId", work);memcpy(QueueId, work.data, sizeof(QueueId)-1);
    msg.GetValue("Sourcesysid", work);memcpy(Sourcesysid, work.data, sizeof(Sourcesysid)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
    msg.GetValue("EventQueueId", work);memcpy(EventQueueId, work.data, sizeof(EventQueueId)-1);
    msg.GetValue("StreamRef", work);memcpy(StreamRef, work.data, sizeof(StreamRef)-1);
    msg.GetValue("StreamType", work);memcpy(StreamType, work.data, sizeof(StreamType)-1);
    msg.GetValue("StreamDescr", work);memcpy(StreamDescr, work.data, sizeof(StreamDescr)-1);
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
    msg.GetValue("MessageType", work);MessageType = (int8)atoi(work.data);
    msg.GetValue("Priority", work);Priority = (int8)atoi(work.data);
    msg.GetValue("Status", work);Status = (int8)atoi(work.data);
    msg.GetValue("DateCreated", work);memcpy(DateCreated, work.data, sizeof(DateCreated)-1);
    msg.GetValue("USId", work);memcpy(USId, work.data, sizeof(USId)-1);
    msg.GetValue("TmStamp", work);memcpy(TmStamp, work.data, sizeof(TmStamp)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsBySourceAlli3BatchRTGS")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(19);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("Id", Id, "o");
    #else
    dBuild.add("Id", Id);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "o");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.add("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Sourcesysid", Sourcesysid, sizeof(Sourcesysid), "o");
    #else
    dBuild.add("Sourcesysid", Sourcesysid, sizeof(Sourcesysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.add("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.add("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.add("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.add("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageType", MessageType, "o");
    #else
    dBuild.add("MessageType", MessageType);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Priority", Priority, "o");
    #else
    dBuild.add("Priority", Priority);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Status", Status, "o");
    #else
    dBuild.add("Status", Status);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.add("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("USId", USId, sizeof(USId), "o");
    #else
    dBuild.add("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.add("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.fill(2);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(19);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("Id", Id, sizeof(Id), "o");
    #else
    dBuild.set("Id", Id, sizeof(Id));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "o");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("QueueId", QueueId, sizeof(QueueId), "o");
    #else
    dBuild.set("QueueId", QueueId, sizeof(QueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Sourcesysid", Sourcesysid, sizeof(Sourcesysid), "o");
    #else
    dBuild.set("Sourcesysid", Sourcesysid, sizeof(Sourcesysid));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId), "o");
    #else
    dBuild.set("EventQueueId", EventQueueId, sizeof(EventQueueId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef), "o");
    #else
    dBuild.set("StreamRef", StreamRef, sizeof(StreamRef));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamType", StreamType, sizeof(StreamType), "o");
    #else
    dBuild.set("StreamType", StreamType, sizeof(StreamType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr), "o");
    #else
    dBuild.set("StreamDescr", StreamDescr, sizeof(StreamDescr));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageType", MessageType, sizeof(MessageType), "o");
    #else
    dBuild.set("MessageType", MessageType, sizeof(MessageType));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Priority", Priority, sizeof(Priority), "o");
    #else
    dBuild.set("Priority", Priority, sizeof(Priority));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Status", Status, sizeof(Status), "o");
    #else
    dBuild.set("Status", Status, sizeof(Status));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated), "o");
    #else
    dBuild.set("DateCreated", DateCreated, sizeof(DateCreated));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("USId", USId, sizeof(USId), "o");
    #else
    dBuild.set("USId", USId, sizeof(USId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp), "o");
    #else
    dBuild.set("TmStamp", TmStamp, sizeof(TmStamp));
    #endif
    dBuild.skip(2);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsBySourceAlli3BatchRTGS()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsBySourceAlli3BatchRTGS, *pStreamsBySourceAlli3BatchRTGS;

#if defined(_DBPORTAL_H_)
struct tStreamsBySourceAlli3BatchRTGSQuery : public tDBQuery
{
  tStreamsBySourceAlli3BatchRTGSQuery(tDBConnect& C, pStreamsBySourceAlli3BatchRTGS D, bool DoExec=true)
  : tDBQuery(C, "StreamsBySourceAlli3BatchRTGS", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByIDGetReference
{
  int32  MessageId; /* i  */
  char   content[65]; /*  o */
  char   filler2[3];
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MessageId);
  }
  void Trims()
  {
    TrimTrailingBlanks(content, sizeof(content));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByIDGetReference")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    char Work[32];
    XRec.append("  <MessageId>");sprintf(Work, "%d", MessageId);XRec.append(Work);XRec.append("</MessageId>\n");
    XRec.append("  <content>");XRec.ampappend(content);XRec.append("</content>\n");
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
    msg.GetValue("MessageId", work);MessageId = atoi(work.data);
    msg.GetValue("content", work);memcpy(content, work.data, sizeof(content)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByIDGetReference")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageId", MessageId, "i");
    #else
    dBuild.add("MessageId", MessageId);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("content", content, sizeof(content), "o");
    #else
    dBuild.add("content", content, sizeof(content));
    #endif
    dBuild.fill(3);
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(2);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageId", MessageId, sizeof(MessageId), "i");
    #else
    dBuild.set("MessageId", MessageId, sizeof(MessageId));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("content", content, sizeof(content), "o");
    #else
    dBuild.set("content", content, sizeof(content));
    #endif
    dBuild.skip(3);
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByIDGetReference()
  {
    memset(this, 0, sizeof(*this));
  }
  bool ByIDGetReferenceReadOne(tDBConnect& C)
  {
    return C.ReadOne("StreamsByIDGetReference", this);
  }
  bool ByIDGetReference(tDBConnect& C, int32 aMessageId)
  {
    MessageId = aMessageId;
    return C.ReadOne("StreamsByIDGetReference", this);
  }
  void ByIDGetReference(tDBConnect& C)
  {
    C.RunProc("StreamsByIDGetReference", this);
  }
  #endif
} tStreamsByIDGetReference, *pStreamsByIDGetReference;

typedef struct tStreamsByMessageExclusionsi3Batch
{
  char   InQueue[17]; /* i  */
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   Reference[65]; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
  }
  void Trims()
  {
    TrimTrailingBlanks(InQueue, sizeof(InQueue));
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(Reference, sizeof(Reference));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByMessageExclusionsi3Batch")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InQueue>");XRec.ampappend(InQueue);XRec.append("</InQueue>\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
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
    msg.GetValue("InQueue", work);memcpy(InQueue, work.data, sizeof(InQueue)-1);
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByMessageExclusionsi3Batch")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.add("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(4);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InQueue", InQueue, sizeof(InQueue), "i");
    #else
    dBuild.set("InQueue", InQueue, sizeof(InQueue));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByMessageExclusionsi3Batch()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByMessageExclusionsi3Batch, *pStreamsByMessageExclusionsi3Batch;

#if defined(_DBPORTAL_H_)
struct tStreamsByMessageExclusionsi3BatchQuery : public tDBQuery
{
  tStreamsByMessageExclusionsi3BatchQuery(tDBConnect& C, pStreamsByMessageExclusionsi3Batch D, bool DoExec=true)
  : tDBQuery(C, "StreamsByMessageExclusionsi3Batch", D)
    {
    if (DoExec == true)
      Exec();
    }
};
#endif

typedef struct tStreamsByReplyGetCorrBanks
{
  char   InDateFrom[15]; /* i  */
  char   InDateTo[15]; /* i  */
  char   Reference[65]; /*  o */
  char   filler3[1];
  struct tMessageData {int32 len; unsigned char data[64000];} MessageData;
  int32  MessageLen; /*  o */
  #if defined(_NEDGEN_H_)
  void Swaps()
  {
    SwapBytes(MessageData.len);
    SwapBytes(MessageLen);
  }
  void Trims()
  {
    TrimTrailingBlanks(InDateFrom, sizeof(InDateFrom));
    TrimTrailingBlanks(InDateTo, sizeof(InDateTo));
    TrimTrailingBlanks(Reference, sizeof(Reference));
  }
  #endif
  #if defined(_TBUFFER_H_)
  void ToXML(TBAmp &XRec, char *Attr=0, char *Outer="StreamsByReplyGetCorrBanks")
  {
    XRec.clear();
    XRec.append("<");XRec.append(Outer);if (Attr) XRec.append(Attr);XRec.append(">\n");
    XRec.append("  <InDateFrom>");XRec.ampappend(InDateFrom);XRec.append("</InDateFrom>\n");
    XRec.append("  <InDateTo>");XRec.ampappend(InDateTo);XRec.append("</InDateTo>\n");
    XRec.append("  <Reference>");XRec.ampappend(Reference);XRec.append("</Reference>\n");
    XRec.append("  <MessageData>");XRec.append("unhandled");XRec.append("</MessageData>\n");
    char Work[32];
    XRec.append("  <MessageLen>");sprintf(Work, "%d", MessageLen);XRec.append(Work);XRec.append("</MessageLen>\n");
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
    msg.GetValue("InDateFrom", work);memcpy(InDateFrom, work.data, sizeof(InDateFrom)-1);
    msg.GetValue("InDateTo", work);memcpy(InDateTo, work.data, sizeof(InDateTo)-1);
    msg.GetValue("Reference", work);memcpy(Reference, work.data, sizeof(Reference)-1);
    msg.GetValue("MessageData", work);/*unhandled*/;
    msg.GetValue("MessageLen", work);MessageLen = atoi(work.data);
  }
  #endif
  #if defined(_DATABUILD_H_) || defined(_DATABUILD2_H_)
  void BuildData(DataBuilder &dBuild, char *name="StreamsByReplyGetCorrBanks")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.add("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.add("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.add("Reference", Reference, sizeof(Reference));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.fill(1);
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.add("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.add("MessageLen", MessageLen, "o");
    #else
    dBuild.add("MessageLen", MessageLen);
    #endif
  }
  void SetData(DataBuilder &dBuild, char *name="Streams")
  {
    dBuild.name(name);
    #if defined(_DATABUILD2_H_)
    dBuild.count(5);
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom), "i");
    #else
    dBuild.set("InDateFrom", InDateFrom, sizeof(InDateFrom));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo), "i");
    #else
    dBuild.set("InDateTo", InDateTo, sizeof(InDateTo));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("Reference", Reference, sizeof(Reference), "o");
    #else
    dBuild.set("Reference", Reference, sizeof(Reference));
    #endif
    dBuild.skip(1);
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData), "o");
    #else
    dBuild.set("MessageData", (void*)&MessageData, sizeof(MessageData));
    #endif
    #if defined(_DATABUILD2_H_)
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen), "o");
    #else
    dBuild.set("MessageLen", MessageLen, sizeof(MessageLen));
    #endif
  }
  #endif
  #if defined(_DBPORTAL_H_)
  tStreamsByReplyGetCorrBanks()
  {
    memset(this, 0, sizeof(*this));
  }
  #endif
} tStreamsByReplyGetCorrBanks, *pStreamsByReplyGetCorrBanks;

#if defined(_DBPORTAL_H_)
struct tStreamsByReplyGetCorrBanksQuery : public tDBQuery
{
  tStreamsByReplyGetCorrBanksQuery(tDBConnect& C, pStreamsByReplyGetCorrBanks D, bool DoExec=true)
  : tDBQuery(C, "StreamsByReplyGetCorrBanks", D)
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


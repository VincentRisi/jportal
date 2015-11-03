using System;
using bbd.idl2;

namespace bbd.idl2.rw
{
  public class Header
  {
    const int HEADER_SIZE = 96;
    public string rpcVersion;
    public string hostId;
    public string wsId;
    public string usrId;
    public int rpcReqId;
    public int returnCode;
    public int mSize;
    public int eSize;
    public Header()
    {
      rpcVersion = "";
      hostId = "";
      wsId = "";
      usrId = "";
      rpcReqId = 0;
      returnCode= 0;
      mSize = 0;
      eSize = 0;
    }
    public int Size { get { return HEADER_SIZE; } }
    public int ReqId { get { return ReqId; } set { rpcReqId = value; } }
    public int ReturnCode { get { return returnCode; } set { returnCode = value; } }
    public int MSize { get { return mSize; } set { mSize = value; } }
    public int ESize { get { return eSize; } set { eSize = value; } }
    public void read(Reader reader)
    {
      rpcVersion = reader.getString(16);
      hostId = reader.getString(16);
      wsId = reader.getString(16);
      usrId = reader.getString(16);
      rpcReqId = reader.getInt();reader.skip(4);
      returnCode = reader.getInt();reader.skip(4);
      mSize = reader.getInt();reader.skip(4);
      eSize = reader.getInt();reader.skip(4);
    }
    public void write(Writer writer)
    {
      writer.putString(rpcVersion, 16);
      writer.putString(hostId, 16);
      writer.putString(wsId, 16);
      writer.putString(usrId, 16);
      writer.putInt(rpcReqId);writer.filler(4);
      writer.putInt(returnCode);writer.filler(4);
      writer.putInt(mSize);writer.filler(4);
      writer.putInt(eSize);writer.filler(4);
    }
    public void SavePrevStats(double starts, 
      int sentLength, int actualSentLength, int receivedLength, int actualReceivedLength, 
      double duration)
    {
    }
  }
}

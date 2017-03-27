/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// 
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
package bbd.crackle.rw;

public class Header
{
  final int HEADER_SIZE = 96;
  public String rpcVersion;
  public String hostId;
  public String wsId;
  public String usrId;
  public int rpcReqID;
  public int returnCode;
  public int mSize;
  public int eSize;
  public Header()
  {
    rpcVersion = "";
    hostId = "";
    wsId = "";
    usrId = "";
    rpcReqID = 0;
    returnCode= 0;
    mSize = 0;
    eSize = 0;
  }
  public int getSize()
  {
    return HEADER_SIZE;
  }
  public void read(Reader reader) throws Exception
  {
    rpcVersion = reader.getString(16);
    hostId = reader.getString(16);
    wsId = reader.getString(16);
    usrId = reader.getString(16);
    rpcReqID = reader.getInt();reader.skip(4);
    returnCode = reader.getInt();reader.skip(4);
    mSize = reader.getInt();reader.skip(4);
    eSize = reader.getInt();reader.skip(4);
  }
  public void write(Writer writer) throws Exception
  {
    writer.putString(rpcVersion, 16);
    writer.putString(hostId, 16);
    writer.putString(wsId, 16);
    writer.putString(usrId, 16);
    writer.putInt(rpcReqID);writer.filler(4);
    writer.putInt(returnCode);writer.filler(4);
    writer.putInt(mSize);writer.filler(4);
    writer.putInt(eSize);writer.filler(4);
  }
}

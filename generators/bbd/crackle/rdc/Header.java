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
package bbd.crackle.rdc;

public class Header
{
  private final static String RPC_TRUSTED = "BEEBEEDEE";
  public String rpcVersion;
  public String hostId;
  public String wsId;
  public String usrId;
  public int rpcReqID;
  public int returnCode;
  public int mSize;
  public int eSize;
  public int prevRpcReqID;
  public int prevTimeSlot;
  public int prevReturnCode;
  public int prevBytesSent;
  public int prevBytesReceived;
  public int prevActualBytesReceived;
  public int prevActualBytesSent;
  public int prevProcessId;
  public double prevCallDuration;
  public double prevProcessDuration;

  public Header()
  {
    rpcVersion = RPC_TRUSTED;
    usrId = "TRUSTED";
    wsId = "192.168.110.120";
    hostId = "192.168.110.121";
    rpcReqID = 0;
    returnCode = 0;
    mSize = 0;
    eSize = 0;
    prevRpcReqID = 0;
    prevTimeSlot = 0;
    prevReturnCode = 0;
    prevBytesSent = 0;
    prevBytesReceived = 0;
    prevActualBytesReceived = 0;
    prevActualBytesSent = 0;
    prevProcessId = 0;
    prevCallDuration = 0.0;
    prevProcessDuration = 0.0;
  }

  public void write(Writer writer) throws Exception
  {
    writer.putString(rpcVersion, 16);
    writer.putString(usrId, 16);
    writer.putString(wsId, 16);
    writer.putString(hostId, 16);
    writer.putInt(rpcReqID);writer.filler(4);
    writer.putInt(returnCode);writer.filler(4);
    writer.setMSizeOffset(writer.size());
    writer.putInt(mSize);writer.filler(4);
    writer.putInt(eSize);writer.filler(4);
    writer.putInt(prevRpcReqID);writer.filler(4);
    writer.putInt(prevTimeSlot);writer.filler(4);
    writer.putInt(prevReturnCode);writer.filler(4);
    writer.putInt(prevBytesSent);writer.filler(4);
    writer.putInt(prevBytesReceived);writer.filler(4);
    writer.putInt(prevActualBytesReceived);writer.filler(4);
    writer.putInt(prevActualBytesSent);writer.filler(4);
    writer.putInt(prevProcessId);writer.filler(4);
    writer.putDouble(prevCallDuration);
    writer.putDouble(prevProcessDuration);
    writer.filler(80);
  }

  public void read(Reader reader) throws Exception
  {
    rpcVersion = reader.getString(16);
    usrId = reader.getString(16);
    wsId = reader.getString(16);
    hostId = reader.getString(16);
    rpcReqID = reader.getInt();reader.skip(4);
    returnCode = reader.getInt();reader.skip(4);
    mSize = reader.getInt();reader.skip(4);
    eSize = reader.getInt();reader.skip(4);
    prevRpcReqID = reader.getInt();reader.skip(4);
    prevTimeSlot = reader.getInt();reader.skip(4);
    prevReturnCode = reader.getInt();reader.skip(4);
    prevBytesSent = reader.getInt();reader.skip(4);
    prevBytesReceived = reader.getInt();reader.skip(4);
    prevActualBytesReceived = reader.getInt();reader.skip(4);
    prevActualBytesSent = reader.getInt();reader.skip(4);
    prevProcessId = reader.getInt();reader.skip(4);
    prevCallDuration = reader.getDouble();
    prevProcessDuration = reader.getDouble();
    reader.skip(80);
  }

  public int getSize()
  {
    return 256;
  }
}

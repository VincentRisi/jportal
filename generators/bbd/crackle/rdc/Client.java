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

public class Client
{
  protected RpcSocket socket;
  protected Header header;
  protected Rdc rdc;
  protected String serverName;
  protected String methodName;
  protected int reqId;
  protected Reader reader;
  protected Writer writer;

  public Client(String host, String service, int timeOut) throws Exception
  {
    header = new Header();
    rdc = new Rdc();
    socket = new RpcSocket(host, service);
    socket.setTimeout(timeOut);
  }

  public void init(int reqId, String serverName, String methodName) throws Exception
  {
    this.reqId = reqId;
    this.serverName = serverName;
    this.methodName = methodName;
    header.rpcReqID = reqId;
    header.eSize = 0;
    header.returnCode = 0;
    writer = new Writer();
    writer.putInt(256);
    header.write(writer);
    writer.setDataSizeOffset(writer.size());
    writer.putInt(0);
  }

  public void call() throws Exception
  {
    int mSize = writer.size() - 264;
    writer.storeLength(writer.getMSizeOffset(), mSize);
    writer.storeLength(writer.getDataSizeOffset(), mSize);
    socket.open();
    try
    {
      byte[] outData = writer.data();
      socket.write(outData);
      byte[] inData = socket.read(260);
      reader = new Reader(inData);
      int headerLength = reader.getInt();
      if (headerLength != 256)
        throw new RpcException(
            String.format(
                "Length of header data should be 256 but %s was read",
                headerLength), null);
      header.read(reader);
      if (header.eSize > 0)
        raiseServerError();
      if (header.mSize > 0)
      {
        int dataLength = readInt();
        byte[] input = readBytes(dataLength);
        if (header.mSize > dataLength)
          input = rdc.decompress(input, header.mSize);
        reader = new Reader(input);
      }
    } finally
    {
      socket.close();
    }
  }

  private byte[] readBytes(int dataLength) throws Exception
  {
    return socket.read(dataLength);
  }

  private int readInt() throws Exception
  {
    byte[] data = readBytes(4);
    return new Reader(data).getInt();
  }

  private void raiseServerError() throws Exception
  {
    int errorLength = readInt();
    byte[] error = readBytes(errorLength);
    if (header.eSize > errorLength)
      error = rdc.decompress(error, header.eSize);
    String errorStr = new String(error, 0, error.length - 1);
    throw new RpcException(errorStr, null);
  }

  public void done()
  {
  }

}

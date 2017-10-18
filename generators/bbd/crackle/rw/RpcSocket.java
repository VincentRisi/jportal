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

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class RpcSocket
{
  private InetAddress inetAddress = null;
  private String      host;
  private final int   port;
  private int         timeout;
  private Socket      socket        = null;
  private ServerSocket serverSocket = null;
  private OutputStream os = null;

  public RpcSocket(String service)
  {
    try
    {
      inetAddress = InetAddress.getLocalHost();
      if (inetAddress != null)
        host = inetAddress.getHostName();
    } catch (Exception ex)
    {
      host = "local";
    }
    port = Integer.parseInt(service);
  }
  private RpcSocket(RpcSocket rpc)
  {
    inetAddress = rpc.inetAddress;
    host = rpc.host;
    port = rpc.port;
    socket = rpc.socket;
    try
    {
      socket.setSoTimeout(timeout);
    }
    catch (SocketException e)
    {
      e.printStackTrace();
    }
  }
  public RpcSocket open() throws Exception
  {
    if (serverSocket == null)
    {
      serverSocket = new ServerSocket(port);
    }
    socket = serverSocket.accept();
    return new RpcSocket(this);
  }
  public byte[] read(int size) throws Exception
  {
    DataInputStream dis = new DataInputStream(socket.getInputStream());
    int expect = dis.readInt();
    if (expect > size)
      throw new RpcException(String.format("Data to read (%d) is greater that size (%d) allowed", expect, size), null);
    if (expect <= 0)
      throw new RpcException(String.format("Data to read (%d) is less than or equal to zero", expect), null);
    int pos = 0;
    byte[] data = new byte[expect];
    int no = expect;
    for (;;)
    {
      int n = dis.read(data, pos, no);
      if (n == no || n == 0)
        break;
      pos += n;
      no -= n;
    }
    return data;
  }
  public void write(byte[] data) throws Exception
  {
    os = socket.getOutputStream();
    os.write(data);
    os.flush();   
  }
  public void close() throws Exception
  {
    socket.close();
  }
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;   
  }
  public Socket getSocket()
  { 
    return socket;
  }
}

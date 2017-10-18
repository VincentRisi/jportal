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
/*
 * Created on Jan 13, 2004
 */
package bbd.crackle.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author vince
 */
public class SocketRpc implements Rpc
{
  private String             host;
  private int                port;
  private int                timeout;
  private String             userId = "";
  private String             locationId = "";
  private Socket             socket       = null;
  private ServerSocket       serverSocket = null;
  private InetAddress        inetAddress;
  private ObjectInputStream  inStream;
  private ObjectOutputStream outStream;
  private GZIPInputStream    inZipStream;
  private GZIPOutputStream   outZipStream;
  public SocketRpc(String host, String service) throws UnknownHostException
  {
    this.host = host;
    inetAddress = InetAddress.getByName(host);
    port = Integer.parseInt(service);
  }
  public SocketRpc(String service) // throws UnknownHostException
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
  public SocketRpc(SocketRpc rpc)
  {
    inetAddress = rpc.inetAddress;
    host = rpc.host;
    port = rpc.port;
    socket = rpc.socket;
    inStream = rpc.inStream;
    outStream = rpc.outStream;
    inZipStream = rpc.inZipStream;
    outZipStream = rpc.outZipStream;
  }
  public Object call(int message, int signature, Object object)
      throws Exception
  {
    Object result = null;
    // InetAddress inet;
    Header header = new Header();
    header.message = message;
    header.signature = signature;
    header.locationId = locationId;
    header.userId = userId;
    header.hasInput = object != null;
    try
    {
      socket = new Socket(inetAddress, port);
      try
      {
        socket.setSoTimeout(timeout);
        outZipStream = new GZIPOutputStream(socket.getOutputStream());
        outStream = new ObjectOutputStream(outZipStream);
        outStream.writeObject(header);
        if (object != null)
          outStream.writeObject(object);
        outStream.flush();
        outZipStream.finish();
        socket.shutdownOutput();
        inZipStream = new GZIPInputStream(socket.getInputStream());
        inStream = new ObjectInputStream(inZipStream);
        header = (Header) inStream.readObject();
        Event.setEventNo(header.eventNo);
        if (header.hasOutput)
          result = inStream.readObject();
        if (header.hasError)
          throw new Exception(header.error);
        return result;
      } finally
      {
        socket.shutdownInput();
        socket.close();
      }
    } catch (UnknownHostException e)
    {
      e.printStackTrace();
      throw new Exception("UnknownHostException: " + e.getMessage());
    } catch (IOException e)
    {
      e.printStackTrace();
      throw new Exception("IOException: " + e.getMessage());
    } catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      throw new Exception("ClassNotFoundException: " + e.getMessage());
    }
    // return null;
  }
  public Object call(int message, int signature) throws Exception
  {
    return call(message, signature, null);
  }
  public Rpc open()
  {
    try
    {
      if (serverSocket == null)
        serverSocket = new ServerSocket(port);
      socket = serverSocket.accept();
      outZipStream = new GZIPOutputStream(socket.getOutputStream());
      inZipStream = new GZIPInputStream(socket.getInputStream());
      inStream = new ObjectInputStream(inZipStream);
      outStream = new ObjectOutputStream(outZipStream);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    return new SocketRpc(this);
  }
  public Object read()
  {
    Object result = null;
    try
    {
      result = inStream.readObject();
    } catch (IOException e)
    {
      e.printStackTrace();
    } catch (ClassNotFoundException e)
    {
      e.printStackTrace();
    }
    return result;
  }
  public void write(Object object)
  {
    try
    {
      outStream.writeObject(object);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  public void close()
  {
    try
    {
      if (socket != null)
      {
        outStream.flush();
        outZipStream.finish();
        socket.shutdownOutput();
        socket.close();
        socket = null;
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  /**
   * @return Returns the timeout.
   */
  public int getTimeout()
  {
    return timeout;
  }
  /**
   * @param timeout
   *          The timeout to set.
   */
  public void setTimeout(int timeout)
  {
    this.timeout = timeout;
  }
  public void setLocationId(String locationId)
  {
    this.locationId = locationId;
  }
  public void setUserId(String userId)
  {
    this.userId = userId;
  }
  public String getLocationId()
  {
    return locationId;
  }
  public String getUserId()
  {
    return userId;
  }
}

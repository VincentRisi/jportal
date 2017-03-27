/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;
using System.IO;
using System.Text;
using System.Net;
using System.Net.Sockets;
using bbd.idl2;
using bbd.utility;

namespace bbd.idl2.rpc
{
  /// <summary>
  /// $Revision: 401.4 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public class RpcTcpClient : TcpClient
  {
    string host;
    public RpcTcpClient(string host, int port) : base(host, port)
    {
      this.host = host;
    }
    public void Dispose()
    {
      Dispose(true);
    }
  }
  public class RpcSocket
  {
    private string host;
    private int port;
    private int timeout;
    private RpcTcpClient tcp;
    private NetworkStream net;
    public RpcSocket(string host, int port, int timeout)
    {
      this.host = host;
      this.port = port;
      this.timeout = timeout;
    }
    ~RpcSocket()
    {
      if (tcp != null)
        tcp.Close();
    }
    public string Host {get{return host;}}
    public string HostIP
    {
      get
      {
        return TcpIP.HostIP(Host);
      }
    }
    public int Port {get{return port;}}
    public int Timeout {get{return timeout;}}
    private double openDuration;
    private double readDuration;
    private double writeDuration;
    private double closeDuration;
    public double OpenDuration {get {return openDuration;}}
    public double ReadDuration {get {return readDuration;}}
    public double WriteDuration {get {return writeDuration;}}
    public double CloseDuration {get {return closeDuration;}}
    public void Open()
    {
      double begin = Logger.TimeVal();
      if (tcp != null)
        tcp.Close();
      tcp = new RpcTcpClient(host, port);
      net = tcp.GetStream();
      tcp.ReceiveTimeout = timeout;
      openDuration = Logger.TimeVal()-begin;
    }
    private void closeTcp()
    {
      tcp.LingerState = new LingerOption(true, 5);
      tcp.Client.Shutdown(SocketShutdown.Both);
      tcp.Dispose();
      tcp.Close();
      tcp = null;
    }
    public void Close()
    {
      if (tcp != null)
      {
        double begin = Logger.TimeVal();
        closeTcp();
        tcp = null;
        closeDuration = Logger.TimeVal()-begin;
      }
    }
    private int ReadAll(byte[] readBuff, int readLen)
    {
      int offset=0;
      while (readLen-offset > 0)
        offset += net.Read(readBuff, offset, readLen-offset);
      return offset;
    }
    public static void InsertLength(ref byte[] buffer, int offset, int length)
    {
      buffer[offset]   = (byte) ((length & 0xFF000000) >> 24);
      buffer[offset+1] = (byte) ((length & 0x00FF0000) >> 16);
      buffer[offset+2] = (byte) ((length & 0x0000FF00) >>  8);
      buffer[offset+3] = (byte) ((length & 0x000000FF));
    }
    public int ReadLength()
    {
      byte[] b4 = new byte[4];
      ReadAll(b4, 4);
      return (b4[0] << 24 | b4[1] << 16 | b4[2] << 8 | b4[3]);
    }
    public byte[] Read(out int length)
    {
      double begin = Logger.TimeVal();
      length = ReadLength();
      byte[] rs = new byte[length];
      ReadAll(rs, (int)length);
      readDuration = Logger.TimeVal()-begin;
      return rs;
    }
    public void Write(byte[] bs, int length)
    {
      double begin = Logger.TimeVal();
      net.Write(bs, 0, length);
      writeDuration = Logger.TimeVal()-begin;
    }
  }
}

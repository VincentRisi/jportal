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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Handler
{
  private static final String TRUSTED_CHICKEN = "TRUSTED_CHICKEN";
  private final RpcSocket rpc;
  private final Header header;
  private byte[] data = null;
  public Handler(RpcSocket rpc) throws Exception
  {
    this.rpc = rpc;
    header = new Header();
    Reader reader = new Reader(receive(header.getSize()));
    header.read(reader);
    if (header.rpcVersion.compareTo(TRUSTED_CHICKEN) != 0)
      throw new RpcException("Invalid Header received", null);
    if (header.mSize > 0)
      data = receive(header.mSize);
  }
  public byte[] getData()
  {
    return data;
  }
  private byte[] receive(int size) throws Exception
  {
    byte[] data = rpc.read(size);
    if (data.length < size)
    {
      byte[] bigger = decompress(data, size);
      return bigger;
    }
    return data;
  }
  private byte[] compress(byte[] data) throws Exception
  {
    ByteArrayOutputStream baos= new ByteArrayOutputStream(data.length);
    ZipOutputStream zos = new ZipOutputStream(baos);
    DataOutputStream dos = new DataOutputStream(zos);
    zos.flush();
    baos.flush();
    baos.close();
    byte[] result = baos.toByteArray();
    dos.close();
    zos.close();
    if (result.length < data.length)
      return result;
    return data;
  }
  private byte[] decompress(byte[] data, int size) throws Exception
  {
    if (size > data.length)
    {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ZipInputStream zis = new ZipInputStream(bais);
      DataInputStream dis = new DataInputStream(zis);
      dis.read(data);
    }
    return data;
  }
  public Header getHeader()
  {
    return header;
  }
  public Reader getInput()
  {
    return new Reader(data);
  }
  public void setReply(int value)
  {
    header.mSize = 0;
    header.eSize = 0;
    header.returnCode = value;
  }
  private Writer messageWriter;
  public Writer getOutput()
  {
    messageWriter = new Writer();
    return messageWriter;
  }
  public void setMessage(byte[] data) throws Exception
  {
    header.mSize = data.length;
    //
  }
  public void setOutput(Writer.ByteWriter tx)
  {
    
  }
  private byte[] error = null;
  public void setError(Exception ex)
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream stream = new PrintStream(baos);
    ex.printStackTrace(stream);
    error = baos.toByteArray();
    header.eSize = error.length;
  }
  public void write()
  {
    try
    {
      Writer writer = new Writer();
      header.write(writer);
      byte[] headData = writer.close();
      int size = header.getSize();
      byte[] data = messageWriter.close();
      header.mSize = data.length;
      byte[] message = compress(data);
      if (header.mSize > 0)
        size += 4 + message.length;
      if (header.eSize > 0)
        size += 4 + error.length;
      ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeInt(headData.length);
      dos.write(headData);
      if (header.mSize > 0)
      {
        dos.writeInt(message.length);
        dos.write(message);
      }
      if (header.eSize > 0)
      {
        dos.writeInt(error.length);
        dos.write(error);
      }
      rpc.write(baos.toByteArray());
      rpc.close();
    }
    catch (Exception ex)
    {
      try
      {
        rpc.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
}

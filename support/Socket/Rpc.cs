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
using System.Data;
using System.IO;
using System.Reflection;
using System.Runtime.Serialization;
using System.Text;
using bbd.utility;

namespace bbd.idl2.rpc
{
  ///<summary>
  ///$Revision: 401.6 $ $Date: 2004/03/08 08:01:47 $
  ///This is the generic Idl2 exception. It derives from ApplicationException 
  ///like all good excptions should.
  ///</summary>
  ///<remarks>
  ///It makes the class Serializable.
  ///</remarks>
  [Serializable()]
  public class RpcException : ApplicationException
  {
    private int errorCode;   
    public RpcException() : base() {}
    public RpcException(string message) : base(message) {}
    public RpcException(string message, Exception inner) : base(message, inner) {}
    public RpcException(int errorCode) : this() {this.errorCode = errorCode;}
    public RpcException(int errorCode, string message) : this(message) {this.errorCode = errorCode;}
    public RpcException(int errorCode, string message, Exception inner) : this(message, inner) {this.errorCode = errorCode;}
    public int ErrorCode {get {return errorCode;}}
    /// <summary>
    /// This is to allow the Exception to be serailized over remoting according and
    /// was researched and supplied by Cape Town - Roaan Vos.
    /// All I did was remove the superflous underscores. :-)
    /// </summary>
    /// <param name="info"></param>
    /// <param name="ctx"></param>
    protected RpcException(SerializationInfo info, StreamingContext ctx) : base(info, ctx)
    { 
      errorCode = (int)info.GetValue("errorCode", typeof(int)); 
    }
    public override void GetObjectData(SerializationInfo info, StreamingContext ctx)
    {
      info.AddValue("errorCode", errorCode);
      base.GetObjectData(info, ctx);
    }  
  }
  [Serializable()]
  public class JPBlob
  {
    private byte[] buffer;
    public JPBlob() { buffer = new byte[0]; }
    public byte[] Buffer
    {
      get { return buffer; }
      set { buffer = new byte[value.Length];  value.CopyTo(buffer, 0); }
    }
  }
  /// <summary>
  /// $Id: Rpc.cs,v 401.6 2004/03/08 08:01:47 vince Exp $
  /// This is the base class for IDL2. It implements Instrumentation using 
  /// IInstrument for logging.
  /// </summary>
  /// <remarks>
  /// Implements IDisposable so that a using construct can be used.
  /// </remarks>
  public class Popper : IInstrument, IDisposable 
  {
    private RpcSocket socket;
    private Writer writer;
    private Reader reader;
    private Header header;
    private byte[] sent;
    private byte[] received;
    private int reqId;
    private int sentLength;
    private int receivedLength;
    private int actualSentLength;
    private int actualReceivedLength;
    private double initialised;
    private double starts;
    private double ends;
    private double completed;
    private string serverName, methodName;
    /// <summary>
    /// Readonly server name.
    /// </summary>
    public string ServerName {get {return serverName;}}
    /// <summary>
    /// Readonly last method invoked name.
    /// </summary>
    public string MethodName {get {return methodName;}}
    /// <summary>
    /// Readonly last method invoked Request or Message Id. 
    /// </summary>
    public int ReqId {get {return reqId;}}
    /// <summary>
    /// Size of buffers sent for last invoked method. Compressed buffers sizes used.
    /// </summary>
    public int SentLength {get {return sentLength;}}
    /// <summary>
    /// Size of buffers sent for last invoked method. Compressed buffers sizes used.
    /// </summary>
    public int ReceivedLength {get {return receivedLength;}}
    /// <summary>
    /// Size of buffers sent for last invoked method. Uncompressed buffers sizes used.
    /// </summary>
    public int ActualSentLength {get {return actualSentLength;}}
    /// <summary>
    /// Size of buffers received for last invoked method. Uncompressed buffers sizes used.
    /// </summary>
    public int ActualReceivedLength {get {return actualReceivedLength;}}
    /// <summary>
    /// Start time of last method invoked.
    /// </summary>
    public double Starts {get {return starts;}}
    /// <summary>
    /// End time of last method invoked.
    /// </summary>
    public double Ends {get {return ends;}}
    /// <summary>
    /// Time of last method initialization (should generally be the same as Starts).
    /// </summary>
    public double Initialised {get {return initialised;}}
    /// <summary>
    /// Time that the last run method completed.
    /// </summary>
    public double Completed {get {return completed;}}
    /// <summary>
    /// Time taken to open socket for last method call. Best to use 
    /// in DoneCall event; 
    /// </summary>
    public double OpenDuration {get {return socket.OpenDuration;}}
    /// <summary>
    /// Time taken to close socket for last method call. Best to use 
    /// in DoneCall event; 
    /// </summary>
    public double CloseDuration {get {return socket.CloseDuration;}}
    /// <summary>
    /// Time taken to read data for last method call. Best to use 
    /// in DoneCall event; 
    /// </summary>
    public double ReadDuration {get {return reader.ReadDuration;}}
    /// <summary>
    /// Time taken to write data for last method call. Best to use 
    /// in DoneCall event; 
    /// </summary>
    public double WriteDuration {get {return writer.WriteDuration;}}

    /// <summary>
    /// Get a List and rebuild it if you using inheared object, eg.
    /// class x {a,b,c}, class y:x {d,e,f}
    /// </summary>
    /// <param name="obj"></param>
    /// <returns></returns>
    public FieldInfo[] GetFieldList(Type type)
    {
      FieldInfo[] fieldInfo = type.GetFields(
                BindingFlags.Instance |
                BindingFlags.Public |
                BindingFlags.NonPublic
                );
      if (type.BaseType != null && type.BaseType != typeof(System.Object))
      //if (type.BaseType != typeof(System.Object))
        return RebuildFieldInfo(fieldInfo, type);
      return fieldInfo;
    }

    //class Pair : IComparable
    //{
    //  internal readonly int index, pos;
    //  internal Pair(int index, int pos)
    //  {
    //    this.index = index;
    //    this.pos = pos;
    //  }
    //  public int CompareTo(object obj)
    //  {
    //    return pos - ((Pair)obj).pos;
    //  }
    //}

    //private FieldInfo[] RebuildFieldInfo(FieldInfo[] fieldInfoList, Type type)
    //{
    //  FieldInfo[] newFieldInfoList = new FieldInfo[fieldInfoList.Length];
    //  Pair[] pairs = new Pair[fieldInfoList.Length];
    //  for (int i = 0; i < fieldInfoList.Length; i++)
    //  {
    //    FieldInfo fieldInfo = fieldInfoList[i];
    //    object[] attributes = fieldInfo.GetCustomAttributes(false);
    //    int pos = 1 + 1;
    //    for (int j = 0; j < attributes.Length; j++)
    //    {
    //      FieldAttribute fa = attributes[j] as FieldAttribute;
    //      if (fa != null)
    //        pos = fa.Pos;
    //    }
    //    pairs[i] = new Pair(i, pos);
    //  }
    //  Array.Sort(pairs);
    //  for (int i = 0; i < pairs.Length; i++)
    //    newFieldInfoList[i] = fieldInfoList[pairs[i].index];
    //  return newFieldInfoList;
    //}

    private FieldInfo[] RebuildFieldInfo(FieldInfo[] fieldInfo, Type type)
    {
      FieldInfo[] newFieldInfo = new FieldInfo[fieldInfo.Length];
      int currPos = 0;
      Type bsType = type;
      int endpos = fieldInfo.Length;
      for (int i = fieldInfo.Length - 1; i > 0; i--)
      {
        if (fieldInfo[i - 1].DeclaringType != fieldInfo[i].DeclaringType)
        {
          for (int j = i; j < endpos; j++)
          {
            newFieldInfo[currPos++] = fieldInfo[j];
          }
          endpos = i;
        }
      }

      for (int j = 0; j < endpos; j++)
      {
        newFieldInfo[currPos++] = fieldInfo[j];
      }
      return newFieldInfo;
    }

    /// <summary>
    /// Constructor if you already have an RpcSocket instantiated.
    /// </summary>
    /// <param name="header">
    /// Base header class. This is an abstract class so you would use a derived 
    /// class for this
    /// </param>
    /// <param name="socket">
    /// RpcSocket instance.
    /// </param>
    public Popper(Header header, RpcSocket socket)
    {
      this.header = header;
      this.socket = socket;
    }
    /// <summary>
    /// 
    /// </summary>
    /// <param name="header"></param>
    /// <param name="host">Name in host file or DNS.</param>
    /// <param name="service">Service port. Expects a number.</param>
    /// <param name="timeout">
    /// Timeout in millisecs. Passed through to RpcSocket.
    /// </param>
    public Popper(Header header, string host, string service, int timeout)
    {
      try
      {
        this.header = header;
        this.socket = new RpcSocket(host, int.Parse(service), timeout);
      }
      catch(Exception ex)
      {
        throw new RpcException(string.Format("Error connecting to host:{0} service:{1} timeout:{2}"
          , host, service, timeout), ex);
      }
    }
    protected void Init(int reqId, string serverName, string methodName)
    {
      try
      {
        this.reqId = reqId;
        this.serverName = serverName;
        this.methodName = methodName;
        initialised = Logger.TimeVal();
        header.ReqId(reqId);
        header.ESize(0);
        header.ReturnCode(0);
        writer = new Writer(socket, header, this);
        reader = new Reader(socket, header, this);
        writer.SetupHeader();
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Initialising Function Call for "+reqId.ToString(), ex);
      }
      finally
      {
        OnInit();
      }
    }
    protected void Add(object of, bool align8)
    {
      try
      {
        writer.Add(of, align8);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Adding Function Call Data", ex);
      }
    }
    protected void Add(object of)
    {
      try
      {
        Add(of, false);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Adding Function Call Data", ex);
      }
    }
    protected object Get(object of, bool align8)
    {
      try
      {
        return reader.Get(of, align8);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Getting Function Result Data", ex);
      }
    }
    protected object Get(object of)
    {
      try
      {
        return Get(of, false);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Getting Function Result Data", ex);
      }
    }
    protected string GetString(int size, bool align8)
    {
      try
      {
        return reader.GetString(size, align8);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Getting Function Result Data", ex);
      }
    }
    protected string GetString(int size)
    {
      try
      {
        return GetString(size, false);
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Getting Function Result Data", ex);
      }
    }
    protected void Call()
    {
      starts = Logger.TimeVal();
      OnBefore();
      try
      {
        RunCall();
      }
      finally
      {
        OnAfter();
      }
    }
    private void RunCall()
    {
      try
      {
        socket.Open();
        try
        {
          sent = writer.Send(out sentLength, out actualSentLength);
          received = reader.Receive(out receivedLength, out actualReceivedLength);
        }
        finally
        {
          socket.Close();
          ends = Logger.TimeVal();
          header.SavePrevStats(starts, sentLength, actualSentLength, receivedLength, actualReceivedLength, ends-starts);
        }
      }
      catch(RpcException)
      {
        throw; // REBOUND the throw
      }
      catch(Exception ex)
      {
        throw new RpcException("Error Calling to Server " + this.serverName, ex);
      }
    }
    protected void Done()
    {
      completed = Logger.TimeVal();
      OnDone();
    }
    #region IInstrument Members
    public event PopperEventHandler InitCall;
    public event PopperEventHandler BeforeCall;
    public event PopperEventHandler AfterCall;
    public event PopperEventHandler DoneCall;
    public event PopperErrorEventHandler ErrorCall;
    public event PopperDumpEventHandler SendBeforeCompress;
    public event PopperDumpEventHandler SendAfterCompress;
    public event PopperDumpEventHandler ReceiveBeforeUncompress;
    public event PopperDumpEventHandler ReceiveAfterUncompress;
    #endregion
    private void OnInit()
    {
      if (InitCall != null)
        InitCall(this);
    }
    private void OnBefore()
    {
      if (BeforeCall != null)
        BeforeCall(this);
    }
    private void OnAfter()
    {
      if (AfterCall != null)
        AfterCall(this);
    }
    private void OnDone()
    {
      if (DoneCall != null)
        DoneCall(this);
    }
    protected void OnError(RpcException exception, int techStart)
    {
      if (ErrorCall != null)
        ErrorCall(this, exception, techStart);
    }
    public void OnBeforeCompress(byte[] buffer, int bufferLength)
    {
      if (SendBeforeCompress != null)
        SendBeforeCompress(this, Logger.HexDump(buffer, bufferLength));
    }
    public void OnAfterCompress(byte[] buffer, int bufferLength)
    {
      if (SendAfterCompress != null)
        SendAfterCompress(this, Logger.HexDump(buffer, bufferLength));
    }
    public void OnBeforeUncompress(byte[] buffer, int bufferLength)
    {
      if (ReceiveBeforeUncompress != null)
        ReceiveBeforeUncompress(this, Logger.HexDump(buffer, bufferLength));
    }
    public void OnAfterUncompress(byte[] buffer, int bufferLength)
    {
      if (ReceiveAfterUncompress != null)
        ReceiveAfterUncompress(this, Logger.HexDump(buffer, bufferLength));
    }
    public virtual void Dispose()
    {}
    public virtual string MessageDesc(object no) 
    {return "Popper.MessageDesc - this should not have been returned.";}
  }
}

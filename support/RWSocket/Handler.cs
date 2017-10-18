using System;
using System.Data;
using System.IO;
using System.Text;
//using Bbd.Utility;

namespace bbd.idl2.rw
{
  public class Handler : IInstrument, IDisposable 
  {
    private RpcSocket socket;
    protected Writer writer;
    protected Reader reader;
    protected Header header;
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
    public string ServerName {get {return serverName;}}
    public string MethodName {get {return methodName;}}
    public int ReqId {get {return reqId;}}
    public int SentLength {get {return sentLength;}}
    public int ReceivedLength {get {return receivedLength;}}
    public int ActualSentLength {get {return actualSentLength;}}
    public int ActualReceivedLength {get {return actualReceivedLength;}}
    public double Starts {get {return starts;}}
    public double Ends {get {return ends;}}
    public double Initialised {get {return initialised;}}
    public double Completed {get {return completed;}}
    public double OpenDuration {get {return socket.OpenDuration;}}
    public double CloseDuration {get {return socket.CloseDuration;}}
    public double ReadDuration {get {return socket.ReadDuration;}}
    public double WriteDuration {get {return socket.WriteDuration;}}
    public Handler(Header header, RpcSocket socket)
    {
      this.header = header;
      this.socket = socket;
    }
    public Handler(Header header, string host, string service, int timeout)
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
        header.ReqId = reqId;
        header.ESize = 0;
        header.ReturnCode = 0;
        writer = new Writer(socket, header, this);
        reader = new Reader(socket, header, this);
        //header.write(writer);
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
    public event HandlerEventHandler InitCall;
    public event HandlerEventHandler BeforeCall;
    public event HandlerEventHandler AfterCall;
    public event HandlerEventHandler DoneCall;
    public event HandlerErrorEventHandler ErrorCall;
    public event HandlerDumpEventHandler SendBeforeCompress;
    public event HandlerDumpEventHandler SendAfterCompress;
    public event HandlerDumpEventHandler ReceiveBeforeUncompress;
    public event HandlerDumpEventHandler ReceiveAfterUncompress;
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
    {return "Handler.MessageDesc - this should not have been returned.";}
  }
  public delegate void HandlerEventHandler(Handler handler);
  public delegate void HandlerErrorEventHandler(Handler handler, RpcException exception, int techStart);
  public delegate void HandlerDumpEventHandler(Handler handler, string dump);
  public interface IInstrument
  {
    event HandlerEventHandler InitCall;
    event HandlerEventHandler BeforeCall;
    event HandlerEventHandler AfterCall;
    event HandlerEventHandler DoneCall;
    event HandlerErrorEventHandler ErrorCall;
    event HandlerDumpEventHandler SendBeforeCompress;
    event HandlerDumpEventHandler SendAfterCompress;
    event HandlerDumpEventHandler ReceiveBeforeUncompress;
    event HandlerDumpEventHandler ReceiveAfterUncompress;
  }
  public class IDL2Exception : ApplicationException
  {
    public IDL2Exception() : base() { }
    public IDL2Exception(string message) : base(message) { }
    public IDL2Exception(string message, Exception inner) : base(message, inner) { }
  }
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
  }
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
}

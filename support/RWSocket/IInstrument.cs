using System;

namespace bbd.idl2.rw
{
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
}

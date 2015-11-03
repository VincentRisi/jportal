using System;

namespace Bbd.Idl2.Rpc
{
  /// <summary>
  /// $Revision: 401.3 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public delegate void PopperEventHandler(Popper popper);
  public delegate void PopperErrorEventHandler(Popper popper, RpcException exception, int techStart);
  public delegate void PopperDumpEventHandler(Popper popper, string dump);
  public interface IInstrument
  {
    event PopperEventHandler InitCall;
    event PopperEventHandler BeforeCall;
    event PopperEventHandler AfterCall;
    event PopperEventHandler DoneCall;
    event PopperErrorEventHandler ErrorCall;
    event PopperDumpEventHandler SendBeforeCompress;
    event PopperDumpEventHandler SendAfterCompress;
    event PopperDumpEventHandler ReceiveBeforeUncompress;
    event PopperDumpEventHandler ReceiveAfterUncompress;
  }
}

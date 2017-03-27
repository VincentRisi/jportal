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

namespace bbd.idl2.rpc
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

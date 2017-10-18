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
using bbd.idl2;

namespace bbd.idl2.rpc
{
  /// <summary>
  /// $Revision: 401.4 $ $Date: 2004/03/08 08:01:47 $
  /// </summary>
  public abstract class Header
  {
    public abstract void CheckVersion();
    public abstract void ReqId(int value);
    public abstract void ReturnCode(int value);
    public abstract int ReturnCode();
    public abstract void MSize(int value);
    public abstract int MSize();
    public abstract void ESize(int value);
    public abstract int ESize();
    public virtual void SignMessage(byte[] mBuffer, int headerLength, byte[] data, string ipAddr)
    {}
    public virtual void SavePrevStats(double starts, int sentLength, int actualSentLength, int receivedLength, int actualReceivedLength, double duration)
    {}
  }
}

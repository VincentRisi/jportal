using System;
using Bbd.Idl2;

namespace Bbd.Idl2.Rpc
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

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
using System.Net.Sockets;
using System.Text;
using bbd.idl2;
using bbd.idl2.rpc;
using bbd.utility;

namespace bbd.idl2.rpc.ubi
{
  public class Header : Bbd.Idl2.Rpc.Header
  {
    private const string VERSION = "0xBEEBEEDEE";
    private const string TRUSTED = "BEEBEEDEE";
    [Field(Size=16, Pos=1)] public string RPCVersion;                //   0                                                   //   0 -  15
    [Field(Size=16, Pos=2)] public string HostId;                    //  16                                                    //  16 -  31
    [Field(Size=16, Pos=3)] public string WsId;                      //  32                                                   //  32 -  47
    [Field(Size=16, Pos=4)] public string UsrId;                     //  48                                                   //  48 -  63
    [Field(Pos=5)]          public int    RpcReqID;                  //  64
    [Field(Size=4, Pos=6)]  byte[] _f_1 = new byte[4];               //  68
    [Field(Pos=7)]          public int    returnCode;                //  72
    [Field(Size=4, Pos=8)]  byte[] _f_2 = new byte[4];               //  76
    [Field(Pos=9)]          public int    Msize;                     //  80
    [Field(Size=4, Pos=10)] byte[] _f_3 = new byte[4];               //  84
    [Field(Pos=11)]         public int    Esize;                     //  88
    [Field(Size=4, Pos=12)] byte[] _f_4 = new byte[4];               //  92
    [Field(Pos=13)]         public int    PrevRpcReqID;              //  96
    [Field(Size=4, Pos=14)] byte[] _f_5 = new byte[4];               // 100
    [Field(Pos=15)]         public int    PrevTimeSlot;              // 104
    [Field(Size=4, Pos=16)] byte[] _f_6 = new byte[4];               // 108
    [Field(Pos=17)]         public int    PrevReturnCode;            // 112
    [Field(Size=4, Pos=18)] byte[] _f_7 = new byte[4];               // 116
    [Field(Pos=19)]         public int    PrevBytesSent;             // 120
    [Field(Size=4, Pos=20)] byte[] _f_8 = new byte[4];               // 124
    [Field(Pos=21)]         public int    PrevBytesReceived;         // 128
    [Field(Size=4, Pos=22)] byte[] _f_9 = new byte[4];               // 132
    [Field(Pos=23)]         public int    PrevActualBytesReceived;   // 136
    [Field(Size=4, Pos=24)] byte[] _f_10 = new byte[4];              // 140
    [Field(Pos=25)]         public int    PrevActualBytesSent;       // 144
    [Field(Size=4, Pos=26)] byte[] _f_11 = new byte[4];              // 148
    [Field(Pos=27)]         public int    PrevProcessId;             // 152
    [Field(Size=4, Pos=28)] byte[] _f_12 = new byte[4];              // 156
    [Field(Pos=29)]         public double PrevCallDuration;          // 160                                                   // 160 - 167
    [Field(Pos=30)]         public double PrevProcessDuration;       // 168                                                   // 168 - 175
    [Field(Size=80, Pos=31)]byte[] _f_13 = new byte[80];             // 176 - 255
    private void SetDefaults()
    {
      RPCVersion = VERSION;
    }
    private Header()
    {
      SetDefaults();
      RPCVersion = TRUSTED;
      WsId = TcpIP.LocalIP;
      UsrId = "TRUSTED";
      HostId = "XXX.XXX.XXX.XXX";
    }
    public override void CheckVersion()
    {
      if (RPCVersion != VERSION && RPCVersion != TRUSTED)
        throw new RpcException("Version of Header failed");
    }
    public override void ReqId(int value)
    {
      RpcReqID = value;
    }
    public override void ReturnCode(int value)
    {
      returnCode = value;
    }
    public override int ReturnCode()
    {
      return returnCode;
    }
    public override void MSize(int value)
    {
      Msize = value;
    }
    public override int MSize()
    {
      return Msize;
    }
    public override void ESize(int value)
    {
      Esize = value;
    }
    public override int ESize()
    {
      return Esize;
    }
    public static Header Make()
    {
      return new Header();
    }

    public override void SavePrevStats(double starts, int sentLength, int actualSentLength, int receivedLength, int actualReceivedLength, double duration)
    {
      DateTime now = DateTime.Now;
      PrevTimeSlot = (int)(starts - now.DayOfYear * 24.0);
      PrevBytesSent = sentLength;
      PrevActualBytesSent = actualSentLength;
      PrevBytesReceived = receivedLength;
      PrevActualBytesReceived = actualReceivedLength;
      PrevCallDuration = duration;
      PrevRpcReqID = RpcReqID;
      PrevReturnCode = returnCode;
    }
    public override void SignMessage(byte[] mBuffer, int headerLength, byte[] data, string ipAddr)
    {
      byte[] hostIp = Encoding.ASCII.GetBytes(ipAddr);
      byte[] header = new byte[headerLength];
      for (int i=0; i<headerLength; i++)
      {
        if (i >= 16 && i < 32)
        {
          int j = i-16;
          if (j < hostIp.Length)
            header[i] = mBuffer[i+4] = hostIp[j];
          else
            header[i] = mBuffer[i+4] = 0;
        }
        else if (i >= 72 && i < 80)
        {
          header[i] = 0;
        }
        else
          header[i] = mBuffer[i+4];
      }
    }
  }
}

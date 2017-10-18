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
using System.Net;

namespace bbd.utility
{
	/// <summary>
	/// This class is used to supply various TcpIP functions
	/// like Host to IP
	/// </summary>
	public class TcpIP
	{
    /// <summary>
    /// This function will translate host (say "atlas") to
    /// dotted notation (say "10.3.0.156")
    /// </summary>
    /// <param name="host">Standard DNS host name</param>
    /// <returns></returns>
    public static string HostIP(string host)
    {
      IPAddress hostadd = Dns.Resolve(host).AddressList[0];
      return hostadd.ToString();
    }
    public static string LocalName
    {
      get
      {
        return System.Net.Dns.GetHostName();
      }
    }
    public static string LocalIP
    {
      get
      {
        return HostIP(LocalName);
      }
    }
  }
}

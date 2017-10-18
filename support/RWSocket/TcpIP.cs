using System;
using System.Net;

namespace bbd.idl2.rw
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
      IPHostEntry entry = Dns.GetHostEntry(host);
      IPAddress hostadd = entry.AddressList[0];
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

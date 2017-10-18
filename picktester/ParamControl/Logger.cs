using System;
using System.Windows.Forms;
using System.IO;

namespace bbd.ParamControl
{
  public class Logger
  {
    public enum LogType
    {
      error,
      warn,
      info,
      debug
    }
    private static LogType level = LogType.info; public static LogType Level { set { level = value; } }
    private static TextBox logMemo = null; public static TextBox LogMemo { set { logMemo = value; } }
    private static StreamWriter logFile = null; public static StreamWriter LogFile { set { logFile = value; } }
    private static void log(LogType logType, string data)
    {
      if (level < logType) return;
      String loglevel = "(?)";
      switch (logType)
      {
        case LogType.error: loglevel = "(E)"; break;
        case LogType.warn: loglevel = "(W)"; break;
        case LogType.info: loglevel = "(I)"; break;
        case LogType.debug: loglevel = "(D)"; break;
      }
      string when = DateTime.Now.ToString("yyyyMMdd|hhmmss");
      string message = string.Format("{0}{1}: {2}\r\n", when, loglevel, data);
      if (logMemo != null) logMemo.AppendText(message);
      if (logFile != null) logFile.Write(message);
    }
    public static string Error { set { log(LogType.error, value); } }
    public static string Warn  { set { log(LogType.warn, value); } }
    public static string Info  { set { log(LogType.info, value); } }
    public static string Debug { set { log(LogType.debug, value); } }
  }
}
using System;
using System.Windows.Forms;
using System.Collections;
using System.IO; 
using System.Text;
using System.Diagnostics;

namespace bbd.idl2.rw
{
  public class Logger
  {
    private Logger()
    {
      level = TraceLevel.Error;
      Trace.AutoFlush = true;
    }
    static Logger instance;
    static public Logger Instance
    {
      get
      {
        if (instance == null)
          instance = new Logger();
        return instance;
      }
    }
    static RichTextBox textBox;
    public RichTextBox TextBox
    {
      get {return textBox;}
      set {textBox = value;}
    }
    static Stream logStream;
    public Stream LogStream
    {
      get {return logStream; }
      set {logStream = value;}
    }
    private static SortedList list = new SortedList();
    public string this[string key]
    {
      get 
      { 
        string result = list[key] as string; 
        if (result == null)
          result = "";
        return result;
      }
      set 
      { 
        list[key] = value; 
      }
    }
    static TraceLevel level;
    public TraceLevel Level
    {
      get {return level;}
      set {level = value;}
    }
    /// <summary>
    /// Millisecond resolution double as seconds of based on day of the year.
    /// </summary>
    /// <returns>
    /// No of seconds since the beginning of the year with millisecond resolution.
    /// </returns>
    public static double TimeVal()
    {
      DateTime now = DateTime.Now;
      return (double)(((now.DayOfYear*24.0+now.Hour)*60.0+now.Minute*60.0)+now.Second)+(double)(now.Millisecond/1000.0); 
    }
    /// <summary>
    /// Returns an IBM style HexDump of a byte array.
    /// </summary>
    /// <param name="b">byte[] buffer</param>
    /// <param name="len">size to dump</param>
    /// <returns></returns>
    public static string HexDump(byte[] b, long len)
    {
      if (b == null)
        return "<no buffer>";
      string result = "";
      string hexString = "";
      string charString = "";
      int offset = 0;
      for (int i=0; i<=len; i++)
      {
        if (i > 0 && (i % 16) == 0 || i == len)
        {
          for (int j=charString.Length; j<16; j++)
          {
            hexString += "  ";
            charString += " ";
          }
          result += string.Format("{2,5:X} | {0,-36} | {1,-16} | {2,5}\r\n", hexString, charString, offset);
          hexString = "";
          charString = "";
          offset = i;
        }
        if (i == len)
          break;
        string ch;
        if (b[i] > 0x20 && b[i] < 0x7F)
          ch = string.Format("{0}", (char)b[i]);
        else
          ch = ".";
        charString += ch;
        ch = string.Format("{0:X}", b[i]);
        string hex;
        if (ch.Length == 1)
          hex = "0"+ch;
        else
          hex = ch;
        hexString += hex;
        if ((i % 4) == 3)
          hexString += " ";
      }
      return result;
    }
    private void writeLog(TraceLevel level, string value)
    {
      if (level <= this.Level && textBox != null) 
      {
        textBox.AppendText(DateTime.Now+" "+level.ToString()+": "+value+"\r\n");
        textBox.Refresh();
      }
      if(level <= this.Level && logStream != null)
      {
        byte[] ss = Encoding.ASCII.GetBytes(DateTime.Now+" "+level.ToString()+": "+value+"\r\n");
        logStream.Write(ss, 0, ss.Length); 
        logStream.Flush(); 
      }
      Trace.WriteLineIf(level <= this.Level, DateTime.Now+" "+level.ToString()+": "+value);
    }
    public string Log
    {
      set 
      {
        writeLog(TraceLevel.Verbose, value);
      }
    }
    public string LogInfo
    {
      set 
      {
        writeLog(TraceLevel.Info, value);
      }
    }
    public string LogWarning
    {
      set 
      {
        writeLog(TraceLevel.Warning, value);
      }
    }
    public string LogError
    {
      set 
      {
        writeLog(TraceLevel.Error, value);
      }
    }
  }
}

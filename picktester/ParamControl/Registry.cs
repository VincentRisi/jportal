using System;
using System.Collections.Generic;
using System.Text;

namespace bbd.ParamControl
{
  public class Registry
  {
    private Microsoft.Win32.RegistryKey regKey;
    public Registry(string which)
    {
      regKey = Microsoft.Win32.Registry.CurrentUser.CreateSubKey(which);
    }
    public string this[string key, string subkey, string value]
    {
      get
      {
        string newkey = string.Format("{0}/{1}", key, subkey);
        return GetValue(newkey, value);
      }
    }
    public string this[string key, string second]
    {
      get
      {
        return GetValue(key, second);
      }
      set
      {
        string newkey = string.Format("{0}/{1}", key, second);
        regKey.SetValue(newkey, value);
      }
    }
    public string this[string key]
    {
      set
      {
        regKey.SetValue(key, value);
      }
    }
    private string GetValue(string key, string value)
    {
      object v = regKey.GetValue(key, value);
      if (v as string != null)
        return (string)v;
      regKey.SetValue(key, value);
      return value;
    }
    static public int IntOf(string value)
    {
      try
      {
        return int.Parse(value);
      }
      catch (Exception)
      {
        return 0;
      }
    }
  }
}

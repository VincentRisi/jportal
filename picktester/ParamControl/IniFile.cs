using System;
using System.Runtime.InteropServices;
using System.Text;

namespace bbd.ParamControl
{
  public class IniFile
  {
    public string path;
    [DllImport("kernel32")]
    private static extern long WritePrivateProfileString(string section, string key, string val, string filePath);
    [DllImport("kernel32")]
    private static extern int GetPrivateProfileString(string section, string key, string def, StringBuilder retVal, int size, string filePath);
    public IniFile(string path)
    {
      this.path = path;
    }
    public void IniWriteValue(string section, string key, string value)
    {
      WritePrivateProfileString(section, key, value, path);
    }
    public string IniReadValue(string section, string key, string def)
    {
      StringBuilder temp = new StringBuilder(255);
      int i = GetPrivateProfileString(section, key, def, temp, 255, path);
      return temp.ToString();
    }
    public string IniReadValue(string section, string key)
    {
      return IniReadValue(section, key, "");
    }
  }
}

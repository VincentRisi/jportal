// --- not currently used ---  using System;
// --- not currently used ---  using System.Collections.Generic;
// --- not currently used ---  using System.Linq;
// --- not currently used ---  using System.Text;
// --- not currently used ---  using System.IO;
// --- not currently used ---  
// --- not currently used ---  namespace bbd.Utility
// --- not currently used ---  {
// --- not currently used ---    public struct Argument
// --- not currently used ---    {
// --- not currently used ---      public char shortSwitch;
// --- not currently used ---      public string longSwitch;
// --- not currently used ---      public string description;
// --- not currently used ---      public string defaultValue;
// --- not currently used ---      public Argument(char shortSwitch
// --- not currently used ---                    , string longSwitch
// --- not currently used ---                    , string description
// --- not currently used ---                    , string defaultValue
// --- not currently used ---                    )
// --- not currently used ---      {
// --- not currently used ---        this.shortSwitch = shortSwitch;
// --- not currently used ---        this.longSwitch = longSwitch;
// --- not currently used ---        this.description = description;
// --- not currently used ---        this.defaultValue = defaultValue;
// --- not currently used ---      }
// --- not currently used ---    }
// --- not currently used ---    public class GetArgs
// --- not currently used ---    {
// --- not currently used ---      public List<Argument> arguments;
// --- not currently used ---      public void Add(char shortSwitch
// --- not currently used ---                    , string longSwitch
// --- not currently used ---                    , string description
// --- not currently used ---                    , string defaultValue
// --- not currently used ---                    )
// --- not currently used ---      {
// --- not currently used ---        arguments.Add(new Argument(shortSwitch, longSwitch, description, defaultValue));
// --- not currently used ---      }
// --- not currently used ---    }
// --- not currently used ---  }
                               
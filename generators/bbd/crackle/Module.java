/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// 
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
package bbd.crackle;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;
/**
*
*/
public class Module implements Serializable
{
  //public class Import
  //{
  //  Module module;
  //}
  private static final long serialVersionUID = 1L;
  public String sourceName;
  public String name;
  public String version;
  public String packageName;
  public int signature;
  public int countOfHashes;
  public int messageBase;
  public Vector<Message> messages;
  public Vector<Table> tables;
  public Vector<Structure> structures;
  public Vector<Enumerator> enumerators;
  public Vector<Prototype> prototypes;
  public Vector<String> pragmas;
  public Vector<String> code;
  public Vector<String> imports;
  public int codeLine;
  public int codeStart;
  public int messageStart;
  public Module()
  {
    sourceName = "";
    name = "";
    version = "1.0";
    packageName = "";
    signature = 0;
    countOfHashes = 0;
    messageBase = 6000; // default RPC Idl base
    messages = new Vector<Message>();
    tables = new Vector<Table>();
    structures = new Vector<Structure>();
    enumerators = new Vector<Enumerator>();
    prototypes = new Vector<Prototype>();
    pragmas = new Vector<String>();
    code = new Vector<String>();
    imports = new Vector<String>();
    codeLine = 0;
    codeStart = 0;
    messageStart = 0;
  }
  public static int j2Hash = 1;
  public static int hashCode(String data)
  {
    int h = 0;
    int len = data.length();
    char val[] = data.toCharArray();
    if (j2Hash == 1)
    {
      for (int i = 0; i < len; i++)
        h = 31 * h + val[i];
    }
    else
    {
      if (len < 16)
      {
        for (int i = len; i > 0; i--)
          h = (h * 37) + val[i];
      }
      else
      {
        int skip = len / 8;
        int off = 0;
        for (int i = len; i > 0; i -= skip, off += skip)
          h = (h * 39) + val[off];
      }
    }
    return h;
  }
  public void hash(String data)
  {
    countOfHashes++;
    signature += (hashCode(data) * countOfHashes);
    signature = signature % 137731;
  }
  public boolean hasStruct(String name)
  {
    for (int i = 0; i < structures.size(); i++)
    {
      Structure struct = (Structure) structures.elementAt(i);
      if (name.compareTo(struct.name) == 0)
        return true;
    }
    return false;
  }
  public Structure getStruct(String name)
  {
    for (int i = 0; i < structures.size(); i++)
    {
      Structure struct = (Structure) structures.elementAt(i);
      if (name.compareTo(struct.name) == 0)
        return struct;
    }
    return null;
  }
  public boolean doCompress()
  {
    boolean result = true;
    for (int i = 0; i < pragmas.size(); i++)
    {
      String s = (String) pragmas.elementAt(i);
      s = s.toUpperCase().trim();
      if (s.compareTo("NOCOMPRESS") == 0)
        result = false;
      else if (s.compareTo("COMPRESS") == 0)
        result = true;
    }
    return result;
  }
  public boolean waitOnKids()
  {
    boolean result = false;
    for (int i = 0; i < pragmas.size(); i++)
    {
      String s = (String) pragmas.elementAt(i);
      s = s.toUpperCase().trim();
      if (s.compareTo("NOWAITFORKIDS") == 0)
        result = false;
      else if (s.compareTo("WAITFORKIDS") == 0)
        result = true;
    }
    return result;
  }
  public String toString()
  {
    return name;
  }
  public Module readRepository(String name, PrintWriter outLog) throws Exception
  {
    outLog.println("Inputting "+name+".module.repo");
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(name+".module.repo"));
    try
    {
      Module next = (Module)in.readObject();
      return next;
    }
    finally
    {
      in.close();
    }  
  }
  public boolean hasImport(String in, PrintWriter outLog)
  {
    for (int i=0; i<imports.size(); i++)
    {
      String name = (String) imports.elementAt(i);
      if (in.compareTo(name) == 0)
        return true;
      Module module;
      try
      {
        module = readRepository(in, outLog);
        if (module.hasImport(name, outLog) == true)
          return true;
      } catch (Exception e)
      {
        e.printStackTrace(outLog);
      }
    }
    return false;
  }
  public boolean hasMessage(Message in)
  {
    for (int i=0; i<messages.size(); i++)
    {
      Message local = (Message) messages.elementAt(i);
      if (local.name.compareTo(in.name) == 0)
        return true;
    }
    return false;
  }
  public boolean hasTable(Table in)
  {
    for (int i=0; i<tables.size(); i++)
    {
      Table local = (Table) tables.elementAt(i);
      if (local.name.compareTo(in.name) == 0)
        return true;
    }
    return false;
  }
  public boolean hasStructure(Structure in)
  {
    for (int i=0; i<structures.size(); i++)
    {
      Structure local = (Structure) structures.elementAt(i);
      if (local.name.compareTo(in.name) == 0)
        return true;
    }
    return false;
  }
  public boolean hasEnumerator(Enumerator in)
  {
    for (int i=0; i<enumerators.size(); i++)
    {
      Enumerator local = (Enumerator) enumerators.elementAt(i);
      if (local.name.compareTo(in.name) == 0)
        return true;
    }
    return false;
  }
  public boolean hasPrototype(Prototype in)
  {
    for (int i=0; i<prototypes.size(); i++)
    {
      Prototype local = (Prototype) prototypes.elementAt(i);
      if (local.name.compareTo(in.name) == 0)
        return true;
    }
    return false;
  }
  public boolean hasPragma(String in)
  {
    for (int i=0; i<pragmas.size(); i++)
    {
      String local = (String) pragmas.elementAt(i);
      if (local.compareTo(in) == 0)
        return true;
    }
    return false;
  }
  public Module expand(PrintWriter outLog)
  {
    Module module = this;
    if (imports.size() == 0)
      return module;
    for (int i=0; i<imports.size(); i++)
    {
      try
      {
        Module next = readRepository((String)imports.elementAt(i), outLog);
        module = next.expand(outLog);  
      }
      catch (Exception e)
      {
        e.printStackTrace(outLog);
        continue;
      }
      for (int j=0; j<module.messages.size(); j++)
      {
        Message other = (Message) module.messages.elementAt(j);
        if (hasMessage(other) == true)
          continue;
        messages.addElement(other);
      }
      for (int j=0; j<module.tables.size(); j++)
      {
        Table other = (Table) module.tables.elementAt(j);
        if (hasTable(other) == true)
          continue;
        tables.addElement(other);
      }
      for (int j=0; j<module.structures.size(); j++)
      {
        Structure other = (Structure) module.structures.elementAt(j);
        if (hasStructure(other) == true)
          continue;
        structures.addElement(other);
      }
      for (int j=0; j<module.enumerators.size(); j++)
      {
        Enumerator other = (Enumerator) module.enumerators.elementAt(j);
        if (hasEnumerator(other) == true)
          continue;
        enumerators.addElement(other);
      }
      for (int j=0; j<module.prototypes.size(); j++)
      {
        Prototype other = (Prototype) module.prototypes.elementAt(j);
        if (hasPrototype(other) == true)
          continue;
        prototypes.addElement(other);
      }
      for (int j=0; j<module.pragmas.size(); j++)
      {
        String other = (String) module.pragmas.elementAt(j);
        if (hasPragma(other) == true)
          continue;
        pragmas.addElement(other);
      }
      for (int j=0; j<module.code.size(); j++)
      {
        String other = (String) module.code.elementAt(j);
        code.addElement(other);
      }
    }
    return module;
  }
}

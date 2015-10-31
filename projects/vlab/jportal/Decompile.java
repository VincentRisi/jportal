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

package vlab.jportal;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

public class Decompile
{
  /**
  * Reads input from stored repository
  */
  public static void main(String args[])
  {
    PrintWriter outLog = new PrintWriter(System.out);
    try
    {
      String log = "";
      int i = 0;
      if (args[i].equals("-l"))
      {
        if (i+1 < args.length)
        {
          log = args[++i];
          OutputStream outFile = new FileOutputStream(log);
          outLog.flush();
          outLog = new PrintWriter(outFile);
        }
        i++;
      }
      if (args.length < 2+i)
      {
        outLog.println("usage java jportal.Decompile <decompiler> <keyinfo> (generators)+");
        outLog.println("for example to re(verse)engineer Oracle");
        outLog.println();
        outLog.println("java jportal.Decompile Oracle \"UFAD00/control@orcl\" -o sql OracleDDL");
        outLog.println("This would expect <decompiler> of jportal.decopilers.Oracle");
        outLog.println("                  <keyinfo> of UFAD00 with password control at OracleSID of orcl");
        outLog.flush();
        return;
      }
      outLog.println(args[i]+" "+args[i+1]);
      Class c0 = Class.forName("vlab.jportal.decompiler."+args[i]);
      Class d0[] = {args[i+1].getClass(), outLog.getClass()};
      Method m0 = c0.getMethod("devolve", d0);
      Object o0[] = {args[i+1], outLog};
      Database database = (Database) m0.invoke(null, o0);
      if (database == null)
      {
        outLog.println("Decompile has errors");
        outLog.flush();
        return;
      }
      String output = "";
      for (i+=2; i < args.length; i++)
      {
        if (args[i].equals("-o"))
        {
          if (i+1 < args.length)
          {
            output = args[++i];
            char ch = output.charAt(output.length()-1);
            if (ch != '\\')
              output = output + "\\";
          }
          continue;
        }
        else if (args[i].equals("-l"))
        {
          if (i+1 < args.length)
          {
            log = args[++i];
            OutputStream outFile = new FileOutputStream(log);
            outLog.flush();
            outLog = new PrintWriter(outFile);
          }
          continue;
        }
        outLog.println(args[i]);
        Class c = Class.forName("jportal."+args[i]);
        Class d[] = {database.getClass(), output.getClass(), outLog.getClass()};
        Method m = c.getMethod("generate", d);
        Object o[] = {database, output, outLog};
        m.invoke(database, o);
      }
      outLog.flush();
    }
    catch (Throwable e)
    {
      outLog.println("Error: "+e);
      outLog.flush();
    }
  }
}

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
import java.lang.reflect.InvocationTargetException;
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
        outLog.println("usage java jportal.Decompile <decompiler> <connect> (generators)+");
        outLog.println("for example to re(verse)engineer Oracle");
        outLog.println();
        outLog.println("java jportal.Decompile OracleRE vlab/polly@192.168.1.141:1521/orcl -o sql OracleDDL");
        outLog.println("This would expect <decompiler> of vlab.jportal.decompiler.Oracle");
        outLog.println("                  <connect> of vlab with password polly at host:port/SID of 192.168.1.141:1521/orcl");
        outLog.flush();
        return;
      }
      outLog.println(args[i]+" "+args[i+1]);
      String decompiler = args[i];
      String connect = args[i+1];
      Database database = runDecompiler(outLog, decompiler, connect);
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
        	i++;
            output = args[i];
            char ch = output.charAt(output.length()-1);
            if (output.indexOf('/') == -1)
            {
              if (ch != '\\')
                output = output + "\\";
            }
            else if (ch != '/')
              output = output + "/";
          }
          continue;
        }
        else if (args[i].equals("-l"))
        {
          if (i+1 < args.length)
          {
        	i++;  
            log = args[i];
            OutputStream outFile = new FileOutputStream(log);
            outLog.flush();
            outLog = new PrintWriter(outFile);
          }
          continue;
        }
        outLog.println(args[i]);
        String generator = args[i];
        Class<?> classOf = Class.forName("vlab.jportal."+generator);
        Class<?> parmsOf[] = {database.getClass(), output.getClass(), outLog.getClass()};
        Method methodOf = classOf.getMethod("generate", parmsOf);
        Object parms[] = {database, output, outLog};
        methodOf.invoke(database, parms);
      }
      outLog.flush();
    }
    catch (Throwable e)
    {
      outLog.println("Error: "+e);
      outLog.flush();
    }
  }
  private static Database runDecompiler(PrintWriter outLog, String decompiler, String connect)
		throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException 
  {
    Class<?> classOf = Class.forName("vlab.jportal.decompiler."+decompiler);
    Class<?> parmsOf[] = {connect.getClass(), outLog.getClass()};
    Method methodOf = classOf.getMethod("devolve", parmsOf);
    Object parms[] = {connect, outLog};
    Database database = (Database) methodOf.invoke(null, parms);
    return database;
  }
}

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
package bbd.pickle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Vector;

public class Compiler
{
  /**
  * Reads input from stored repository
  */
  static PrintWriter outLog;
  static String pickleFileName = "";
  static String piDirName = "";
  static String prDirName = "";
  static boolean combine = false;
  private static boolean mkPath(String pathname)
  {
    File path = new File(pathname);
    boolean result = path.exists();
    if (result == false)
      result = path.mkdirs();
    return result;
  }
  private static boolean mkFilesPath(String filename)
  {
    File check = new File(filename);
    boolean result = mkPath(check.getParent());
    if (result == false)  
      outLog.println("Directory of " + filename + " did not make.");
    return result;
  }
  private static String[] argsParse(String args[]) throws FileNotFoundException
  {
    boolean reported = false;
    int i = 0, no = args.length;
    while (i < no)
    {
      String arg = args[i];
      if (arg.charAt(0) != '-')
        break;
      if (arg.length() == 1)
      {
        outLog.println("A singular '-' is an invalid command line argument");
        i++;
        break;
      }
      if (arg.charAt(1) == '-')
        break;
      switch (arg.charAt(1))
      {
      case 'p':
        if (arg.length() > 2)
          piDirName = arg.substring(2);
        else if (i < no && args[i+1].charAt(0) != '-')
        {
          i++;
          piDirName = args[i];
        }
        else
          piDirName = "";
        combine = true;
        break;
      case 'r':
        if (arg.length() > 2)
          prDirName = arg.substring(2);
        else if (i < no && args[i+1].charAt(0) != '-')
        {
          i++;
          prDirName = args[i];
        }
        else
          prDirName = "";
        combine = true;
        break;
      case 'l':
        String logName;
        if (arg.length() > 2)
          logName = arg.substring(2);
        else if (i < no)
        {
          i++;
          logName = args[i];
        } 
        else
          break;
        if (mkFilesPath(logName) == true)
        {
          outLog.println("Switch Logging to " + logName);
          OutputStream outFile = new FileOutputStream(logName);
          outLog.flush();
          outLog = new PrintWriter(outFile);
        }
        break;
      case 'f':
        if (arg.length() > 2)
          pickleFileName = arg.substring(2);
        else if (i < no)
        {
          i++;
          pickleFileName = args[i];
        }
        mkFilesPath(pickleFileName);
        break;
      default:
        outLog.println("'" + arg + "' is an invalid command line argument");
        if (reported == false)
        {
          reported = true;
          outLog.println("Valid command line arguments here are");
          outLog.println(" -f <name>   Filename of .idl file to be combined");
          outLog.println(" -l <name>   Change logging name");
          outLog.println(" -p <dir>    Directory containing source with .pi extension from db .si files");
          outLog.println(" -r <dir>    Directory containing source with .pr extension for relation or validation files");
        }
        break;
      }
      i++;
    }
    if (i == 0)
      return args;
    String[] result = new String[no - i];
    System.arraycopy(args, i, result, 0, no - i);
    return result;
  }
  public static void main(String args[])
  {
    outLog = new PrintWriter(System.out);
    outLog.println("Starting pickle compile");
    try
    {
      args = argsParse(args);
      if (args.length < 1)
      {
        outLog.println("usage java pickle.Compiler ((-l log) | (-p piDir) | (-r prDir) | (-f piFile))* infile (((-l log)? (-o outDir)?) generator)+");
        System.exit(1);
      }
      Application application;
      if (combine == true)
      {
        outLog.println("Combining");
        String code = combineFileReader(args[0]);
        if (pickleFileName.length() > 0)
        {
          FileWriter fileWriter = new FileWriter(pickleFileName);
          PrintWriter writer = new PrintWriter(fileWriter);
          writer.println(code);
          writer.flush();
          writer.close();
          fileWriter.close();
        }
        StringReader reader = new StringReader(code);
        application = Pickle.run(reader, outLog);
      } 
      else
      {
        outLog.println("Compiling file " + args[0]);
        application = Pickle.run(args[0], outLog);
        outLog.println("Compile completed of " + args[0]);
      }
      if (application == null)
      {
        outLog.println("Error: Compile aborted due to errors");
        outLog.flush();
        System.exit(1);
      }
      String output = "";
      for (int i = 1; i < args.length; i++)
      {
        if (args[i].equals("-o"))
        {
          if (i + 1 < args.length)
          {
            output = args[++i];
            char term = '\\';
            if (output.indexOf('/') != -1)
              term = '/';
            char ch = output.charAt(output.length() - 1);
            if (ch != term)
              output = output + term;
            mkPath(output);
            outLog.println("Generating to " + output);
          }
          continue;
        } else if (args[i].equals("-l"))
        {
          if (i + 1 < args.length)
          {
            String log = args[++i];
            mkFilesPath(log);
            OutputStream outFile = new FileOutputStream(log);
            outLog.flush();
            outLog = new PrintWriter(outFile);
          }
          continue;
        }
        outLog.println(args[i]);
        Class<?> c = Class.forName("bbd.pickle."+args[i]);
        Class<?> d[] = {application.getClass(), output.getClass(), outLog.getClass()};
        Method m = c.getMethod("generate", d);
        Object o[] = {application, output, outLog};
        m.invoke(application, o);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      outLog.println("Error: "+e);
      e.printStackTrace();
      outLog.flush();
      System.exit(1);
    }
    System.exit(0);
  }
  private static String processFileReader(String fileName)
  {
    StringBuilder builder = new StringBuilder();
    try
    {
      outLog.println(fileName + " to be read");
      outLog.flush();
      FileReader fileReader = new FileReader(fileName);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      try
      {
        while (bufferedReader.ready())
        {
          String line = bufferedReader.readLine();
          builder.append(line);
          builder.append('\n');
        }
        bufferedReader.close();
      } catch (NullPointerException e2)
      {
      }
    } catch (FileNotFoundException e)
    {
      outLog.println("PreProcess FileNotFoundException");
      outLog.flush();
    } catch (IOException e)
    {
      outLog.println("PreProcess IOException");
      outLog.flush();
    }
    return builder.toString();
  }
  private static Vector<Block> blocks; 
  public static class Block
  {
    public String name;
    public String code;
    public Block(String name, String code)
    {
      this.name = name;
      this.code = code;
    }
  }
  private static String combineFileReader(String fileName)
  {
    StringBuilder builder = new StringBuilder();
    builder.append(processFileReader(fileName));
    blocks = new Vector<Block>();
    appendFromDir(piDirName, ".pi");
    build(builder, blocks);
    blocks = new Vector<Block>();
    appendFromDir(prDirName, ".pr");
    for (int i=0; i<blocks.size(); i++)
    {
      Block block = (Block)blocks.elementAt(i);
      builder.append("// -- "+block.name+".pr --\n");
      builder.append(block.code);
    }
    return builder.toString();
  }
  private static int strspn(String code, String span)
  {
    char[] bytes = code.toCharArray();
    for (int i=0; i<bytes.length; i++)
    {
      if (span.indexOf(bytes[i]) == -1)
        return i;
    } 
    return bytes.length;
  }
  private static int strcspn(String code, String span)
  {
    char[] bytes = code.toCharArray();
    for (int i=0; i<bytes.length; i++)
    {
      if (span.indexOf(bytes[i]) != -1)
        return i;
    } 
    return bytes.length;
  }
  private static String getTableName(String code)   // table fred xxxxxxxx
  {                                                 // 01234567890123456789
    String lower = code.toLowerCase();
    int n = lower.indexOf("table ");                // n=0
    if (n == -1) return null;
    lower = lower.substring(n+6);                   // fred xxxxxxxx
    n = strspn(lower, " \t\r\n");                   // n=0
    if (n == lower.length()) return null;
    lower = lower.substring(n);                     // fred xxxxxxxx 
    n = strcspn(lower, " \t\r\n");                  // n=4
    lower = lower.substring(0, n);
    if (lower.charAt(1) == '\'' || lower.charAt(1) == '\"')
      lower = lower.substring(2, lower.length()-1);
    return lower;
  }
  private static void build(StringBuilder builder, Vector<Block> blocks)
  {
    String done = ":";
    Vector<Block> usesBlocks = new Vector<Block>();
    for (int i=0; i<blocks.size(); i++)
    {
      Block block = (Block)blocks.elementAt(i);
      if (block.code.startsWith("//USES") == true)
      {
        usesBlocks.addElement(block);
        continue;
      }
      String tableName = getTableName(block.code);
      if (tableName == null) tableName = block.name.toLowerCase();
      done += tableName + ":";
      builder.append("// -- "+block.name+".pi(" + tableName+ ") --\n");
      builder.append(block.code);
    }
    int loop = 0;
    while (usesBlocks.size() > 0)
    {
      int count = usesBlocks.size();
      for (int i=count-1; i>=0; i--)
      {
        Block block = (Block)usesBlocks.elementAt(i);
        String code = block.code;
        int n = code.indexOf('\n');
        String uses = code.substring(0, n-1).substring(7).toLowerCase();
        String[] use_list = uses.split(":");
        boolean okay = true;
        for (int j=0; j<use_list.length; j++)
        {
          String use = use_list[j];
          if (done.indexOf(":"+use+":") == -1)
          {
            okay = false;
            break;
          }
        }
        if (okay == true)
        {
          usesBlocks.removeElementAt(i);
          String tableName = getTableName(block.code);
          if (tableName == null) tableName = block.name.toLowerCase();
          done += tableName + ":";
          builder.append("//UB" + loop + " -- " + block.name + ".pi(" + tableName+ ") --\n");
          builder.append(block.code);
        }
      }
      if (count == usesBlocks.size())
        break;
      loop++;
    }
    for (int i=0; i<usesBlocks.size(); i++)
    {
      Block block = (Block)usesBlocks.elementAt(i);
      String tableName = getTableName(block.code);
      if (tableName == null) tableName = block.name.toLowerCase();
      done += tableName + ":";
      builder.append("//MB" + i + " -- "+block.name+".pi(" + tableName+ ") --\n");
      builder.append(block.code);
    }
  }
  private static String fullName(String dirName, String fileName)
  {
    String dirsep = "/";
    if (dirName.length() > 2 && dirName.charAt(1) == ':')
      dirsep = "\\";
    String result = dirName + dirsep + fileName;
    return result;
  }
  private static void appendFromDir(String dirName, String ext)
  {
    outLog.println(dirName + " for ext:" + ext + " to be read");
    if (dirName.length() == 0)
      return;
    File dir = new File(dirName);
    String[] list = dir.list();
    if (list == null)
    {
      outLog.println(dirName + " does not appear to be a good file directory");
      outLog.flush();
    }
    else
    {
      for (int i = 0; i < list.length; i++)
      {
        String name = list[i];
        int n = name.length();
        if (n <= 3)
          continue;
        if (name.substring(n-3).compareTo(ext) == 0)
        {
          String combName = fullName(dirName, name);
          blocks.addElement(new Block(name.substring(0, n-3), processFileReader(combName)));
        }
      }
    }
  }
}

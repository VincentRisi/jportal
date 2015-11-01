package vlab.crackle;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class PopGenCppDll extends Generator
{
  public static String description()
  {
    return "Generates Cpp Dll Code";
  }
  public static String documentation()
  {
    return "Generates Cpp Dll Code";
  }
  private static PrintWriter errLog;
  public static void main(String[] args)
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      errLog = outLog;
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module)in.readObject();
        generate(module, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    errLog = outLog;
    outLog.println(module.name+" version "+module.version);
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+"Server.h");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+"Server.h");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
}

package bbd.binu;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import bbd.crackle.rdc.RpcSocket;

public class Server
{
  private static Properties iniFile;
  private Logger logger;
  private void check_log4j(Properties log4j, String[] args)
  {
    for(String arg : args)
    {
      if (arg.startsWith("log4j.") == true)
      {
        String[] kv = arg.split("=");
        if (kv.length == 2)
          log4j.setProperty(kv[0], kv[1]);
      } 
    }
  }
  private String check(String key, String[] args)
  {
    for(String arg : args)
    {
      if (arg.startsWith(key) == true)
      {
        String[] kv = arg.split("=");
        if (kv.length == 2)
          return kv[1];
      } 
    }
    return iniFile.getProperty(key);
  }
  public void listener(Notifier<RpcSocket> notifier, String port, int timeout, int noThreads) throws Exception
  {
    final RpcSocket rpc = new RpcSocket(port, timeout);
    int count = 0;
    logger.info(String.format("Server on port:%s timeout:%d noThreads:%d started.", port, timeout, noThreads));
    while (true)
    {
      try
      {
        RpcSocket request = rpc.accept();
        logger.info("Request received");
        notifier.put(request);
      } 
      catch (Exception e)
      {
        if (count == 0)
          e.printStackTrace();
        logger.error(e.toString());
        count++;
        if (count > 5)
          break;
      }
      RpcSocket killer = null;
      for (int i=0; i<noThreads; i++)
        notifier.put(killer);
    }
  }
  public Server(String[] args) throws Exception
  {
    String iniFileName = args[0];
    iniFile = new Properties();
    iniFile.load(new FileInputStream(iniFileName));
    Enumeration<?> keys = iniFile.keys();
    Properties log4j = new Properties();
    while (keys.hasMoreElements())
    {
      String key = (String)keys.nextElement();
      if (key.startsWith("log4j.") == true)
        log4j.setProperty(key, (String)iniFile.get(key));
    }
    check_log4j(log4j, args);    
    PropertyConfigurator.configure(log4j);
    logger = Logger.getLogger("BinuServer");
    String port = check("server.port", args);
    int timeout = Integer.parseInt(check("server.timeout", args));
    int noThreads = Integer.parseInt(check("server.noThreads", args));
    String dbs[] = check("database.connects", args).split(" ");
    String[] connects = new String[dbs.length];
    for (int i=0; i< dbs.length; i++)
    {
      String propname = String.format("database.%s", dbs[i]);
      connects[i] = check(propname, args);
    }
    Notifier<RpcSocket> notifier = new Notifier<RpcSocket>();
    Idl2Thread[] idl2Thread = new Idl2Thread[noThreads]; 
    for (int i=0; i<noThreads; i++)
      idl2Thread[i] = new Idl2Thread(notifier, dbs, connects, i);
    for (int i=0; i<noThreads; i++)
      idl2Thread[i].run();
    listener(notifier, port, timeout, noThreads);    
  }
  public static void main(String[] args)
  {
    try
    {
      if (args.length > 0)
        new Server(args);
    }
    catch (Exception ex)
    {
      System.out.println("Server failed: " + ex.toString());
      StackTraceElement[] traceElements = ex.getStackTrace();
      for (StackTraceElement te : traceElements)
        System.out.println(te.toString());
    }
  }
}

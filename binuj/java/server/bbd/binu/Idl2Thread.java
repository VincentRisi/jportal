package bbd.binu;

import org.apache.log4j.Logger;
import bbd.crackle.rdc.RpcSocket;
import bbd.jportal.util.Connector;
import bbd.jportal.util.ConnectorDB2;

public class Idl2Thread extends Thread
{
  private Notifier<RpcSocket> notifier;
  private Logger logger;
  private String[] dbs;
  private Connector[] conns;
  BinuDispatch dispatcher;
  public Idl2Thread(Notifier<RpcSocket> notifier, String[] dbs, String[] connects, int no) throws Exception
  {
    this.notifier = notifier;
    this.dbs = dbs;
    logger = Logger.getLogger(String.format("Idl2Thread[%d]", no));
    logger.info(String.format("Thread %d setup", no));
    conns = new Connector[connects.length];
    for (int i = 0; i < connects.length; i++)
    {
      String[] parts = connects[i].split(":", 4);
      conns[i] = new ConnectorDB2(parts[0], parts[1], parts[2], parts[3]);
    }
    dispatcher = new BinuDispatch(conns[0]);
    dispatcher.setParent(this);
  }
  public Connector getConn(String db)
  {
    for (int i=0; i<dbs.length; i++)
      if (dbs[i].compareTo(db) == 0)
        return conns[i];
    return conns[0];
  }
  public void run()
  {
    try
    {
      while (true)
      {
        try
        {
          RpcSocket socket = notifier.get();
          if (socket == null)
            break;
          try
          {
            logger.debug("Read socket");
            dispatcher._dispatch(socket);
            socket.close();
            logger.debug("Done");
          } finally
          {
            socket.close();
          }
        } catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}

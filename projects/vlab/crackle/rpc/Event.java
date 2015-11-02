package vlab.crackle.rpc;

public class Event
{
  static private int eventNo;

  public static synchronized int getEventNo()
  {
    return eventNo;
  }

  public static synchronized void setEventNo(int eventNo)
  {
    Event.eventNo = eventNo;
  }
}

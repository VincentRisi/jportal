package bbd.binu;

public class Notifier<PARCEL>
{
  private PARCEL parcel;
  private boolean message = false;
  public synchronized PARCEL get()
  {
    while (message == false)
    {
      try
      {
        wait();
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    message = false;
    notifyAll();
    return parcel;
  }
  public synchronized void put(PARCEL parcel)
  {
    while (message == true)
    {
      try
      {
        wait();
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    this.parcel = parcel;
    message = true;
    notifyAll();
  }
}

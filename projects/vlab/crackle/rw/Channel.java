/*
 * Created on Jan 23, 2004
 */
package vlab.crackle.rw;


/**
 * @author vince
 */
public class Channel<PARCEL>
{
  private PARCEL parcel;
  private boolean   message = false;
  public synchronized PARCEL get()
  {
    while (message == false)
    {
      try
      {
        wait();
      } catch (Throwable e)
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
      } catch (Throwable e)
      {
        e.printStackTrace();
      }
    }
    this.parcel = parcel;
    message = true;
    notifyAll();
  }
}

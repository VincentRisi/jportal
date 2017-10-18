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
/*
 * Created on Jan 23, 2004
 */
package bbd.crackle.rw;


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

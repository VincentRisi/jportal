/// ------------------------------------------------------------------
/// Copyright (c) 1996, 2004 Vincent Risi in Association
///                          with Barone Budge and Dominick
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// $Date: 2004/10/18 13:48:10 $
/// $Revision: 411.1 $ // YMM.Revision
/// ------------------------------------------------------------------

package bbd.jportal;

import java.util.*;
import java.sql.*;

abstract public class Connector
{
  class Calendar extends GregorianCalendar
  {
    long AsMillis()
    {
      return getTimeInMillis();
    }
  }
  public Connection connection;
  abstract public String getUserstamp()              throws SQLException;
  public Timestamp getTimestamp()                    throws SQLException
  {
    Calendar now = new Calendar();
    return new Timestamp(now.AsMillis());
  }
  abstract public int getSequence(String table)      throws SQLException;
  public void setAutoCommit(boolean cond)            throws SQLException
  {
    connection.setAutoCommit(cond);
  }
  public void commit()                               throws SQLException
  {
    connection.commit();
  }
  public void rollback()                             throws SQLException
  {
    connection.rollback();
  }
}


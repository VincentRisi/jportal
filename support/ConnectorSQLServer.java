/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

package bbd.jportal;

import java.io.*;
import java.sql.*;

public class ConnectorSQLServer extends Connector
{
  String userId;
  /**
  * @param forName    - connect.microsoft.MicrosoftDriver
  *                   - connect.sybase.SybaseDriver
  * @param url        - jdbc:ff-microsoft://waddy:1433
  *                   - jdbc:ff-sybase://gionata:5000
  * @param user       -
  * @param password   -
  */
  public ConnectorSQLServer(String forName, String url,
                  String user, String password) throws ClassNotFoundException, SQLException
  {
    userId = user;
    System.out.println(" Loading Driver "+forName);
    Class.forName(forName);
    System.out.println(" Connecting to  "+url+", "+user+", "+password);
    connection = DriverManager.getConnection(url, user, password);
    System.out.print(" Verifying that the connection is open: ");
    System.out.println(!connection.isClosed ());
    connection.setAutoCommit(false);
  }
  /**
  * Use the user id supplied for the connect
  */
  public String getUserstamp() throws SQLException
  {
    return userId;
  }
  /**
  * This form of sequence uses an insert trigger in order to allocate the next highest
  * number. The DDL generates a create insert trigger. Using a zero for the seq no
  * and the massaging to the next max(value)+1.
  */
  public int getSequence(String table) throws SQLException
  {
    return 0;
  }
}



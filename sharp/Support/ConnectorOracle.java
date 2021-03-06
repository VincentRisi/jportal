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
/// $Date: 2004/10/18 13:48:11 $
/// $Revision: 411.1 $ // YMM.Revision
/// ------------------------------------------------------------------

package bbd.jportal;

import java.io.*;
import java.sql.*;
import java.math.*;

public class ConnectorOracle extends Connector
{
  String userId;
  /**
  ** @param driverName - gets inserted into oracle.jdbc.----.OracleDriver
  ** @param driverType - get inserted into  jdbc:oracle:----:user/password[@server]
  ** @param server     - get inserted into  jdbc:oracle:driverType:user/password[@----]
  ** @param user       - get inserted into  jdbc:oracle:driverType:----/password[@server]
  ** @param password   - get inserted into  jdbc:oracle:driverType:user/----[@server]
  */
  public ConnectorOracle(String driverType, String server,
                  String user, String password) throws ClassNotFoundException, SQLException
  {
    String url = "jdbc:oracle:"+driverType+":";
    if (server.length() > 0)
      url = url+"@"+server;
    connect(url, user, password);
  }
  void connect(String url,
               String user, String password) throws ClassNotFoundException, SQLException
  {
    userId = user;
    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
    connection = DriverManager.getConnection(url, user, password);
    System.out.println(!connection.isClosed ());
    connection.setAutoCommit(false);
  }
  public String getUserstamp() throws SQLException
  {
    return userId;
  }
  public int getSequence(String table) throws SQLException
  {
    int nextNo;
    PreparedStatement prep = connection.prepareStatement("select "+table+"Seq.NextVal from dual");
    ResultSet result = prep.executeQuery();
    result.next();
    nextNo =  result.getInt(1);
    result.close();
    prep.close();
    return nextNo;
  }
}


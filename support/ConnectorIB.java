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

import java.sql.*;

public class ConnectorIB extends Connector
{
  public String getUserstamp() throws SQLException
  {
    return "N/A";
  }
  public int getSequence(String table) throws SQLException
  {
    return 0;
  }
}


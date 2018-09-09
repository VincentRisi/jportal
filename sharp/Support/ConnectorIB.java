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


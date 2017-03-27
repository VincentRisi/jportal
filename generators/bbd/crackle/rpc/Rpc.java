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
 * Created on Jan 7, 2004
 */
package bbd.crackle.rpc;

/**
 * @author vince
 */
public interface Rpc
{
	Object call(int message, int signature, Object object) throws Exception;
	Object call(int message, int signature) throws Exception;
	Rpc open();
	public Object read();
	void write(Object object);
	void close();
  public void setUserId(String userId);
  public String getUserId();
  public void setLocationId(String locationId);
  public String getLocationId();
}

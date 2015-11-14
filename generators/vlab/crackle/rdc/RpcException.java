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
package vlab.crackle.rdc;

public class RpcException extends Throwable
{
  private static final long serialVersionUID = 1L;
  public RpcException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

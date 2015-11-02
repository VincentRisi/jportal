/*
 * Created on Jan 7, 2004
 */
package vlab.crackle.rpc;

import java.io.Serializable;

/**
 * @author vince
 */
public class Header implements Serializable
{
  /**
   * use the default serial version for now.
   */
  private static final long serialVersionUID = 1L;
  /**
   * Unique message no
   */
  public int message;
  /**
   * Unique message signature
   */
  public int signature;
  /**
   * Useful for Event handling Client Server.
   */
  public int eventNo;
  /**
   * The user responsible for the activity
   */
  public String userId;
  /**
   * The location of the user, eg. terminal, pc, ipaddr - etc. 
   */
  public String locationId;
  /**
   * The result of the activity
   */
  public int returnCode;
  /**
   * Indicates an block of input is to be sent
   */
  public boolean hasInput;
  /**
   * Indicates that an error string is present.
   */
  public boolean hasError;
  /**
   * Indicates an block of output is to be received
   */
  public boolean hasOutput;
  /**
   * A string with the error value
   */
  public String error;
  public Header()
  {
    hasError = false;
    hasOutput = false;
    error = null;
    userId = "";
    locationId = "";
  }
}

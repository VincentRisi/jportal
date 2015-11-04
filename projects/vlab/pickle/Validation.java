package vlab.pickle;

import java.io.Serializable;
import java.util.Vector;

public class Validation implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -3793173313807899419L;
  public Vector<String> code;
  public Validation()
  {
    code = new Vector<String>();
  }
}

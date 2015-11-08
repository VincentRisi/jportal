package vlab.crackle;

import java.io.Serializable;
import java.util.Vector;

/**
 * 
 */
public class Enumerator implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public String             name;
  public Vector<String>    elements;
  public Enumerator()
  {
    name = "";
    elements = new Vector<String>();
  }
}

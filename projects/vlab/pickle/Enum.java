package vlab.pickle;

import java.io.Serializable;

/**
* This holds the field definition. It also supplies methods for the
* Java format and various SQL formats.
*/
public class Enum implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
public String  name;
  public int     value;
  public Enum()
  {
    name = "";
    value = 0;
  }
}

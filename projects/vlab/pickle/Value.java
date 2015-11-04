package vlab.pickle;

import java.io.Serializable;
import java.util.Vector;

public class Value implements Serializable
{
  /**
	 * 
	 */
  private static final long serialVersionUID = -1062931244979924970L;
  public Vector<String> list;
  public Value()
  {
    list  = new Vector<String>();
  }
}


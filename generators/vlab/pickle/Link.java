package vlab.pickle;

import java.io.Serializable;
import java.util.Vector;

public class Link implements Serializable
{
  /**
	 * 
	 */
	private static final long serialVersionUID = -3908671489770159128L;
public Table table;
  public Vector<String> list;
  public boolean hasCascade;
  public Link()
  {
    table = null;
    list  = new Vector<String>();
    hasCascade = false;
  }
}


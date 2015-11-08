package vlab.pickle;

import java.io.Serializable;
import java.util.Vector;

public class Key implements Serializable
{
  private static final long serialVersionUID = -1746201965095963973L;
  public String name;
  public Vector<Field> list;
  public boolean primary;
  public boolean unique;
  public Key()
  {
    name = "";
    primary = false;
    unique = false;
    list  = new Vector<Field>();
  }
}


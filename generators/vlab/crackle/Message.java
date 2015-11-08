package vlab.crackle;

import java.io.Serializable;

/**
*
*/
public class Message implements Serializable
{
  private static final long serialVersionUID = 1L;
  public String name;
  public String value;
  public Message()
  {
    name  = "";
    value = "";
  }
}


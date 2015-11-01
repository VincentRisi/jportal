package vlab.crackle;

import java.io.Serializable;

/**
*
*/
public class Operation implements Serializable
{
  private static final long serialVersionUID = 1L;
  public static final byte
      SIZE = 1
  ,   DYNAMIC = 2
  ;
  public String name;
  public byte code;
  public boolean isConstant;
  public Field field;
  public Operation()
  {
    code = 0;
    name = "";
    isConstant = false;
    field = null;
  }
}

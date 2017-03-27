package bbd.binu.parser;

public class TJInt
{
  private Integer data;
  private boolean nullable;
  public TJInt(boolean nullable)
  {
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else;
      data = new Integer(0);
  }
  public TJInt()
  {
    this(false);
  }
  public int get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return 0;
      else
        throw new TJException("Invalid Integer - non nullable int is not set");
    return data.intValue();  
  }
  public void set(int value) throws TJException
  {
    data = new Integer(value);    
  }
}

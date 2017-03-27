package bbd.binu.parser;

public class TJLong
{
  private Long data;
  private boolean nullable;
  public TJLong(boolean nullable)
  {
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else;
      data = new Long(0);
  }
  public TJLong()
  {
    this(false);
  }
  public long get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return 0;
      else
        throw new TJException("Invalid Double - non nullable double is not set");
    return data.longValue();  
  }
  public void set(long value) throws TJException
  {
    data = value;    
  }
}

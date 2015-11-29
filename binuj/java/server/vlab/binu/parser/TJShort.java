package bbd.binu.parser;

public class TJShort
{
  private Short data;
  private boolean nullable;
  public TJShort(boolean nullable)
  {
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else;
      data = new Short((short)0);
  }
  public TJShort()
  {
    this(false);
  }
  public short get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return 0;
      else
        throw new TJException("Invalid Short - non nullable short is not set");
    return data.shortValue();  
  }
  public void set(short value) throws TJException
  {
    data = new Short(value);    
  }
}

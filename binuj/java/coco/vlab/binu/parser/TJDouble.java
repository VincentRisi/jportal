package bbd.binu.parser;

public class TJDouble
{
  private Double data;
  private boolean nullable;
  public TJDouble(boolean nullable)
  {
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else;
      data = new Double(0.0);
  }
  public TJDouble()
  {
    this(false);
  }
  public double get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return 0.0;
      else
        throw new TJException("Invalid Double - non nullable double is not set");
    return data.doubleValue();  
  }
  public void set(double value) throws TJException
  {
    data = new Double(value);    
  }
}

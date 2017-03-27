package bbd.binu.parser;

public class TJMoney
{
  private int precision;
  private int scale;
  private char data[];
  private boolean nullable;
  public TJMoney(int precision, int scale, boolean nullable)
  {
    this.precision = precision;
    this.scale = scale;
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else
      data = "0".toCharArray();
  }
  public TJMoney(int precision, int scale)
  {
    this(precision, scale, false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid money - non nullable money is not set");
    return new String(data);
  }
  public void set(String value) throws TJException
  {
    if (nullable == true && value.length() == 0)
    {
      data = null;
      return;
    }
    int dp = -1;
    int len = 0;
    boolean minus = false;
    for (int i=0; i<value.length(); i++)
    {
      char ch = value.charAt(i);
      if (i==0 && ch == '-')
      {
        len++;
        minus = true;
        continue;
      }
      if (ch == '.')
      {
        len++;
        if (dp >= 0) 
          throw new TJException("Invalid money data - too many decimal point markers");
        dp = i;
        continue;
      }
      if (ch < '0' || ch > '9')
        throw new TJException("Invalid money data - not numeric data supplied");
      len++; 
    }
    int allow = precision;
    if (minus == true) allow++;
    if (dp > -1) allow++;
    if (len > allow)
      throw new TJException("Invalid money data - value is greater than allowed");
    if (dp > -1 && len - dp -1 > scale)
      throw new TJException("Invalid money data - to many digits after decimal point");
    data = value.substring(0, len).toCharArray();
  }
}

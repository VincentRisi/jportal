package bbd.binu.parser;

public class TJString
{
  private int size;
  private char data[];
  private boolean nullable;
  public TJString(int size, boolean nullable)
  {
    this.size = size;
    this.nullable = nullable;
    reset();
  }
  public void reset()
  {
    if (nullable == true)
      data = null;
    else
      data = "".toCharArray();
  }
  public TJString(int size)
  {
    this(size, false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid String - non nullable string is not set");
    return new String(data);
  }
  public void set(String value) throws TJException
  {
    int len = value.length();
    if (len > size) throw new TJException(String.format("Invalid String '%s' - value supplied is too long", value));
    data = value.toCharArray();
  }
}

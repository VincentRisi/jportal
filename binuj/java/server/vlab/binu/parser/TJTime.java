package bbd.binu.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TJTime
{
  private static SimpleDateFormat timeFormat = new SimpleDateFormat("hhmmss");
  private java.sql.Time data;
  private boolean nullable;
  public TJTime(boolean nullable)
  {
    this.nullable = nullable;
    data = null;
  }
  public TJTime()
  {
    this(false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid time - non nullable date is not set");
    return time(data);
  }
  public void set(String value) throws TJException
  {
    data = time(value);
  }
  private static String time(java.sql.Time value)
  {
    return timeFormat.format(value);
  }
  private static java.sql.Time time(String value) throws TJException
  {
    try
    {
      java.util.Date date = timeFormat.parse(value); 
      return new java.sql.Time(date.getTime());
    } catch (ParseException e)
    {
      throw new TJException(String.format("Invalid time '%s' - invalid time format '%s' does not parse", value, timeFormat));
    }
  }
}

package bbd.binu.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TJTimeStamp
{
  private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private java.sql.Timestamp data;
  private boolean nullable;
  public TJTimeStamp(boolean nullable)
  {
    this.nullable = nullable;
    data = null;
  }
  public TJTimeStamp()
  {
    this(false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid timestamp - non nullable timestamp is not set");
    return timeStamp(data);
  }
  public void set(String value) throws TJException
  {
    data = timeStamp(value);
  }
  private static String timeStamp(java.sql.Timestamp value)
  {
    return dateTimeFormat.format(value);
  }
  private static java.sql.Timestamp timeStamp(String value) throws TJException
  {
    try
    {
      java.util.Date date = dateTimeFormat.parse(value); 
      return new java.sql.Timestamp(date.getTime());
    } catch (ParseException e)
    {
      throw new TJException(String.format("Invalid timestamp '%s' - invalid timestamp format '%s' does not parse", value, dateTimeFormat));
    }
  }
}

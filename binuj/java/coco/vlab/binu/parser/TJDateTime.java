package bbd.binu.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TJDateTime
{
  private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
  private java.sql.Timestamp data;
  private boolean nullable;
  public TJDateTime(boolean nullable)
  {
    this.nullable = nullable;
    data = null;
  }
  public TJDateTime()
  {
    this(false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid datetime - non nullable date is not set");
    return dateTime(data);
  }
  public void set(String value) throws TJException
  {
    data = dateTime(value);
  }
  private static String dateTime(java.sql.Timestamp value)
  {
    return dateTimeFormat.format(value);
  }
  private static java.sql.Timestamp dateTime(String value) throws TJException
  {
    try
    {
      java.util.Date date = dateTimeFormat.parse(value); 
      return new java.sql.Timestamp(date.getTime());
    } catch (ParseException e)
    {
      throw new TJException(String.format("Invalid datetime '%s'- invalid datetime format '%s' does not parse", value, dateTimeFormat));
    }
  }
}

package bbd.binu.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TJDate
{
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
  private java.sql.Date data;
  private boolean nullable;
  public TJDate(boolean nullable)
  {
    this.nullable = nullable;
    data = null;
  }
  public TJDate()
  {
    this(false);
  }
  public String get() throws TJException
  {
    if (data == null)
      if (nullable == true)
        return "";
      else
        throw new TJException("Invalid date - non nullable date is not set");
    return date(data);
  }
  public void set(String value) throws TJException
  {
    data = date(value);
  }
  private static String date(java.sql.Date value)
  {
    return dateFormat.format(value);
  }
  private static java.sql.Date date(String value) throws TJException
  {
    try
    {
      java.util.Date date = dateFormat.parse(value); 
      return new java.sql.Date(date.getTime());
    } catch (ParseException e)
    {
      throw new TJException(String.format("Invalid date '%s' - invalid date format '%s' does not parse", value, dateFormat));
    }
  }
}

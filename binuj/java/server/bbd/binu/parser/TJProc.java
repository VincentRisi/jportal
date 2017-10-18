package bbd.binu.parser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.sql.Timestamp;
import bbd.jportal.util.Connector;
import bbd.jportal.util.JPBlob;
import bbd.jportal.util.ByteArrayBlob;
import bbd.jportal.util.DataHandler;

public class TJProc
{
  public String database, server, schema, user, password;
  public String table, name;
  public int noRows, noLines, noBinds, noOuts, noInps, noDyns, recSize;
  public TJLine[] lines;
  public TJField[] outs;
  public TJField[] inps;
  public TJDynamic[] dyns;
  public int[] binds;
  public boolean isProc, isSProc, isData, isIdlCode, isSql, isSingle, isAction, isStd, useStd, extendsStd, useKey, hasImage,
      isMultipleInp, isInsert, hasReturning;
  public void resetOptions()
  {
    isProc = isSProc = isData = isIdlCode = isSql = isSingle = false;
    isAction = isStd = useStd = extendsStd = useKey = hasImage = false;
    isMultipleInp = isInsert = hasReturning = false;
  }
  private StringBuffer command;
  private PreparedStatement prep;
  public PreparedStatement setup(Connector conn) throws Exception
  {
    command = new StringBuffer();
    for (TJLine line : lines)
    {
      if (line.isVar == true)
        throw new Exception("Command has dynamic variables");
      command.append(line.line);
    }
    prep = conn.prepareStatement(command.toString());
    return prep;
  }
  private Connector.Returning returning;
  public PreparedStatement setup(Connector conn, byte[] rec) throws Exception
  {
    command = new StringBuffer();
    if (isInsert == true && hasReturning == true)
    {
      for (TJField field : outs)
        if (field.isSequence && field.isPrimaryKey)
          returning = conn.getReturning(table, field.name);
    } else if (isInsert && isMultipleInp)
      returning = conn.getReturning(table, "not-used");
    for (TJLine line : lines)
      command.append(getLine(line, rec));
    prep = conn.prepareStatement(command.toString(), returning.doesGeneratedKeys);
    bind(conn, rec);
    return prep;
  }
  public PreparedStatement setupBulk(Connector conn, int noRecs, byte[] recs) throws Exception
  {
    byte[] rec = new byte[recSize];
    setup(conn);
    for (int i = 0; i < noRecs; i++)
    {
      System.arraycopy(recs, i * recSize, rec, 0, recSize);
      bind(conn, rec);
      prep.addBatch();
    }
    return prep;
  }
  private int _noRecs;
  private int _rc;
  private int _outBufferSize;
  private byte[] _outBuffer;
  public int getNoRecs()
  {
    return _noRecs;
  }
  public int getOutBufferSize()
  {
    return _outBufferSize;
  }
  public byte[] getOutBuffer()
  {
    return _outBuffer;
  }
  public int getRc()
  {
    return _rc;
  }
  public void runMulti() throws Exception
  {
    _noRecs = 0;
    _outBufferSize = 0;
    _outBuffer = null;
    ResultSet result;
    result = prep.executeQuery();
    if (!result.next())
    {
      result.close();
      return;
    }
    ResultSetMetaData _rsmd_ = result.getMetaData();
    int _columns_ = _rsmd_.getColumnCount();
    if (_columns_ != outs.length)
      throw new Exception("Columns Read=" + _columns_ + " != Expected=" + outs.length);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    while (true)
    {
      _noRecs++;
      define(dos, result);
      _outBufferSize = dos.size();
      if (!result.next())
      {
        result.close();
        break;
      }
    }
    dos.flush();
    dos.close();
    _outBuffer = baos.toByteArray();
    baos.close();
  }
  public void runSingle() throws Exception
  {
    _rc = 0;
    _outBufferSize = 0;
    _outBuffer = null;
    ResultSet result;
    result = prep.executeQuery();
    if (!result.next())
    {
      result.close();
      return;
    }
    ResultSetMetaData _rsmd_ = result.getMetaData();
    int _columns_ = _rsmd_.getColumnCount();
    if (_columns_ != outs.length)
      throw new Exception("Columns Read=" + _columns_ + " != Expected=" + outs.length);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    define(dos, result);
    _outBufferSize = dos.size();
    result.close();
    dos.flush();
    dos.close();
    _outBuffer = baos.toByteArray();
    baos.close();
  }
  private boolean getNull(TJField field, DataInputStream dis) throws Exception
  {
    switch (field.type)
    {
    case TJField.TJ_ANSICHAR:
    case TJField.TJ_CHAR:
    case TJField.TJ_TLOB:
    case TJField.TJ_XML:
      String value = getString(field, dis);
      if (value.length() == 0)
        return true;
      return false;
    }
    short value = dis.readShort();
    return value != 0;
  }
  private short getShort(TJField field, DataInputStream dis) throws Exception
  {
    short value = dis.readShort();
    return value;
  }
  private int getInt(TJField field, DataInputStream dis) throws Exception
  {
    int value = dis.readInt();
    return value;
  }
  private long getLong(TJField field, DataInputStream dis) throws Exception
  {
    long value = dis.readLong();
    return value;
  }
  private double getDouble(TJField field, DataInputStream dis) throws Exception
  {
    double value = dis.readDouble();
    return value;
  }
  private ByteArrayBlob getBlob(TJField field, DataInputStream dis) throws Exception
  {
    JPBlob blob = new JPBlob(field.size);
    byte[] data = new byte[field.size];
    dis.read(data, 0, field.size);
    blob.setBytes(data);
    return blob.getBlob();
  }
  private java.sql.Date getDate(TJField field, DataInputStream dis) throws Exception
  {
    String value = getString(field, dis);
    return DataHandler.date(value);
  }
  private java.sql.Timestamp getTimestamp(TJField field, DataInputStream dis) throws Exception
  {
    String value = getString(field, dis);
    return DataHandler.timeStamp(value);
  }
  private java.sql.Time getTime(TJField field, DataInputStream dis) throws Exception
  {
    String value = getString(field, dis);
    return DataHandler.time(value);
  }
  private String getString(TJField field, DataInputStream dis) throws Exception
  {
    byte[] data = new byte[field.size];
    dis.read(data, 0, field.size);
    String value = new String(data);
    return value;
  }
  private void bind(Connector conn, byte[] rec) throws Exception
  {
    ByteArrayInputStream bais = new ByteArrayInputStream(rec);
    DataInputStream dis = new DataInputStream(bais);
    dis.mark(rec.length);
    short shortValue;
    int intValue;
    long longValue;
    boolean nullValue;
    double doubleValue;
    Blob blobValue;
    java.sql.Date dateValue;
    java.sql.Timestamp timestampValue;
    java.sql.Time timeValue;
    String stringValue;
    for (int no : binds)
    {
      int pos = no + 1;
      TJField field = inps[no];
      if (field.isNull)
      {
        dis.reset();
        dis.skip(field.offset + field.size);
        nullValue = getNull(field, dis);
        if (nullValue == true)
        {
          prep.setNull(pos, javaType(field));
          continue;
        }
      }
      dis.reset();
      dis.skip(field.offset);
      switch (field.type)
      {
      case TJField.TJ_BLOB:
        blobValue = getBlob(field, dis);
        prep.setBlob(pos, blobValue);
        break;
      case TJField.TJ_BOOLEAN:
        shortValue = getShort(field, dis);
        prep.setShort(pos, shortValue);
        break;
      case TJField.TJ_BYTE:
        shortValue = getShort(field, dis);
        prep.setShort(pos, shortValue);
        break;
      case TJField.TJ_SHORT:
        shortValue = getShort(field, dis);
        prep.setShort(pos, shortValue);
        break;
      case TJField.TJ_DATE:
        dateValue = getDate(field, dis);
        prep.setDate(pos, dateValue);
        break;
      case TJField.TJ_DATETIME:
        timestampValue = getTimestamp(field, dis);
        prep.setTimestamp(pos, timestampValue);
        break;
      case TJField.TJ_TIME:
        timeValue = getTime(field, dis);
        prep.setTime(pos, timeValue);
        break;
      case TJField.TJ_TIMESTAMP:
        timestampValue = conn.getTimestamp();
        prep.setTimestamp(pos, timestampValue);
        break;
      case TJField.TJ_FLOAT:
      case TJField.TJ_DOUBLE:
        if (field.precision > 15)
        {
          stringValue = getString(field, dis);
          prep.setString(pos, stringValue);
        } else
        {
          doubleValue = getDouble(field, dis);
          prep.setDouble(pos, doubleValue);
        }
        break;
      case TJField.TJ_INT:
        intValue = getInt(field, dis);
        prep.setInt(pos, intValue);
        break;
      case TJField.TJ_SEQUENCE:
        intValue = conn.getSequence(table, field.name);
        prep.setInt(pos, intValue);
        break;
      case TJField.TJ_IDENTITY:
        intValue = conn.getSequence(table, field.name);
        prep.setInt(pos, intValue);
        break;
      case TJField.TJ_LONG:
        longValue = getLong(field, dis);
        prep.setLong(pos, longValue);
        break;
      case TJField.TJ_MONEY:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_STATUS:
        shortValue = getShort(field, dis);
        prep.setShort(pos, shortValue);
        break;
      case TJField.TJ_CHAR:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_TLOB:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_XML:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_USERSTAMP:
        stringValue = conn.getUserstamp();
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_ANSICHAR:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_UID:
        stringValue = getString(field, dis);
        prep.setString(pos, stringValue);
        break;
      case TJField.TJ_BIGSEQUENCE:
        longValue = conn.getBigSequence(table, field.name);
        prep.setLong(pos, longValue);
        break;
      case TJField.TJ_BIGIDENTITY:
        longValue = conn.getBigSequence(table, field.name);
        prep.setLong(pos, longValue);
        break;
      }
    }
  }
  private void define(DataOutputStream dos, ResultSet result) throws Exception
  {
    short shortValue;
    int intValue;
    long longValue;
    boolean isNull, useNull;
    double doubleValue;
    Blob blobValue;
    java.sql.Date dateValue;
    java.sql.Timestamp timestampValue;
    java.sql.Time timeValue;
    String stringValue;
    int pos = 0;
    for (TJField field : outs)
    {
      pos++;
      useNull = field.isNull;
      if (useNull == true)
        isNull = result.getObject(pos) == null;
      else
        isNull = false;
      switch (field.type)
      {
      case TJField.TJ_BLOB:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          blobValue = result.getBlob(pos);
          putBlob(dos, blobValue, field.size);
        }
        break;
      case TJField.TJ_BOOLEAN:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          shortValue = result.getShort(pos);
          putShort(dos, shortValue);
        }
        break;
      case TJField.TJ_BYTE:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          shortValue = result.getShort(pos);
          putShort(dos, shortValue);
        }
        break;
      case TJField.TJ_SHORT:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          shortValue = result.getShort(pos);
          putShort(dos, shortValue);
        }
        break;
      case TJField.TJ_DATE:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          dateValue = result.getDate(pos);
          putDate(dos, dateValue);
        }
        break;
      case TJField.TJ_DATETIME:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          timestampValue = result.getTimestamp(pos);
          putTimestamp(dos, timestampValue);
        }
        break;
      case TJField.TJ_TIME:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          timeValue = result.getTime(pos);
          putTime(dos, timeValue);
        }
        break;
      case TJField.TJ_TIMESTAMP:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          timestampValue = result.getTimestamp(pos);
          putTimestamp(dos, timestampValue);
        }
        break;
      case TJField.TJ_FLOAT:
      case TJField.TJ_DOUBLE:
        if (isNull == true)
          putNull(dos, field);
        else if (field.precision > 15)
        {
          stringValue = result.getString(pos);
          putString(dos, stringValue, field.size);
        } else
        {
          doubleValue = result.getDouble(pos);
          putDouble(dos, doubleValue);
        }
        break;
      case TJField.TJ_INT:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          intValue = result.getInt(pos);
          putInt(dos, intValue);
        }
        break;
      case TJField.TJ_SEQUENCE:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          intValue = result.getInt(pos);
          putInt(dos, intValue);
        }
        break;
      case TJField.TJ_IDENTITY:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          intValue = result.getInt(pos);
          putInt(dos, intValue);
        }
        break;
      case TJField.TJ_LONG:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          longValue = result.getLong(pos);
          putLong(dos, longValue);
        }
        break;
      case TJField.TJ_MONEY:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          stringValue = result.getString(pos);
          putString(dos, stringValue, field.size);
        }
        break;
      case TJField.TJ_STATUS:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          shortValue = result.getShort(pos);
          putShort(dos, shortValue);
        }
        break;
      case TJField.TJ_CHAR:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);

        break;
      case TJField.TJ_TLOB:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);
        break;
      case TJField.TJ_XML:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);
        break;
      case TJField.TJ_USERSTAMP:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);
        break;
      case TJField.TJ_ANSICHAR:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);
        break;
      case TJField.TJ_UID:
        if (isNull == true)
          stringValue = "";
        else
          stringValue = result.getString(pos);
        putString(dos, stringValue, field.size);
        break;
      case TJField.TJ_BIGSEQUENCE:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          longValue = result.getLong(pos);
          putLong(dos, longValue);
        }
        break;
      case TJField.TJ_BIGIDENTITY:
        if (isNull == true)
          putNull(dos, field);
        else
        {
          longValue = result.getLong(pos);
          putLong(dos, longValue);
        }
        break;
      }
    }
  }
  private void putNull(DataOutputStream dos, TJField field) throws Exception
  {
    {
      fill(dos, field.size);
      putShort(dos, (short) 1);
    }
  }
  private void fill(DataOutputStream dos, int len) throws Exception
  {
    for (int i = 0; i < len; i++)
      dos.writeByte(0);
  }
  private void putLong(DataOutputStream dos, long longValue) throws Exception
  {
    dos.writeLong(longValue);
  }
  private void putInt(DataOutputStream dos, int intValue) throws Exception
  {
    dos.writeInt(intValue);
    fill(dos, 4);
  }
  private void putDouble(DataOutputStream dos, double doubleValue) throws Exception
  {
    dos.writeDouble(doubleValue);
  }
  private void putString(DataOutputStream dos, String stringValue, int size) throws Exception
  {
    int extra = size - stringValue.length();
    byte[] b = stringValue.getBytes();
    if (extra > 0)
    {
      dos.write(b);
      fill(dos, extra);
    } else
      dos.write(b, 0, size);
  }
  private void putTime(DataOutputStream dos, Time timeValue) throws Exception
  {
    String time = DataHandler.time(timeValue);
    putString(dos, time, 8);
  }
  private void putTimestamp(DataOutputStream dos, Timestamp timestampValue) throws Exception
  {
    String timeStamp = DataHandler.timeStamp(timestampValue);
    putString(dos, timeStamp, 16);
  }
  private void putDate(DataOutputStream dos, Date dateValue) throws Exception
  {
    String date = DataHandler.date(dateValue);
    putString(dos, date, 8);
  }
  private void putBlob(DataOutputStream dos, Blob blobValue, int size) throws Exception
  {
    byte[] data = blobValue.getBytes(0, size);
    int extra = size - data.length;
    dos.write(data, 0, data.length);
    if (extra > 0)
      fill(dos, extra);
  }
  private void putShort(DataOutputStream dos, short shortValue) throws Exception
  {
    dos.writeShort(shortValue);
    fill(dos, 6);
  }
  private static int javaType(TJField field)
  {
    switch (field.type)
    {
    case TJField.TJ_BYTE:
      return java.sql.Types.TINYINT;
    case TJField.TJ_CHAR:
      return java.sql.Types.VARCHAR;
    case TJField.TJ_ANSICHAR:
      return java.sql.Types.CHAR;
    case TJField.TJ_DATE:
      return java.sql.Types.DATE;
    case TJField.TJ_DATETIME:
      return java.sql.Types.TIMESTAMP;
    case TJField.TJ_FLOAT:
    case TJField.TJ_DOUBLE:
      return java.sql.Types.DOUBLE;
    case TJField.TJ_BLOB:
      return java.sql.Types.LONGVARBINARY;
    case TJField.TJ_TLOB:
      return java.sql.Types.LONGVARCHAR;
    case TJField.TJ_INT:
    case TJField.TJ_SEQUENCE:
    case TJField.TJ_IDENTITY:
      return java.sql.Types.INTEGER;
    case TJField.TJ_LONG:
    case TJField.TJ_BIGSEQUENCE:
    case TJField.TJ_BIGIDENTITY:
      return java.sql.Types.BIGINT;
    case TJField.TJ_MONEY:
      return java.sql.Types.DOUBLE;
    case TJField.TJ_SHORT:
      return java.sql.Types.SMALLINT;
    case TJField.TJ_TIME:
      return java.sql.Types.TIME;
    case TJField.TJ_TIMESTAMP:
      return java.sql.Types.TIMESTAMP;
    case TJField.TJ_USERSTAMP:
      return java.sql.Types.VARCHAR;
    }
    return java.sql.Types.OTHER;
  }
  private String getLine(TJLine line, byte[] rec)
  {
    if (line.isVar == false)
      return line.line;
    String varName;
    if (line.line.startsWith("_ret."))
    {
      varName = line.line.substring(5);
      if (varName.compareTo("head") == 0)
        return returning.head;
      if (varName.compareTo("sequence") == 0)
        return returning.sequence;
      if (varName.compareTo("output") == 0)
        return returning.output;
      if (varName.compareTo("dropField") == 0)
        return returning.dropField;
    }
    varName = line.line.trim();
    for (TJDynamic dyn : dyns)
    {
      if (varName.compareTo(dyn.name) == 0)
      {
        int n = dyn.length;
        byte[] sub = new byte[n];
        System.arraycopy(rec, dyn.offset, sub, 0, n);
        return new String(sub);
      }
    }
    return varName + "not-present";
  }
}

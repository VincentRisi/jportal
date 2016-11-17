/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;
using System.Data;

namespace Bbd.Idl2.AnyDb
{
  public class JPortalException : ApplicationException
  {
    protected JPortalException() : base() { }
    public JPortalException(string error) : base(error) { }
    public JPortalException(string error, Exception ex) : base(error, ex) { }
  }
  public enum ParameterType
  {
    QuestionMark,
    Colon,
    AtSymbol,
    Dollar
  }
  public enum VendorType
  {
    Oracle,
    SqlServer,
    Odbc,
    OleDb,
    PostgreSQL,
    DB2,
    Lite3
  }
  public class Connect : IDisposable
  {
    internal IDbConnection connection;
    internal IDbTransaction transaction;
    internal ParameterType typeOfParameter;
    internal VendorType typeOfVendor;
    internal int commandTimeout;
    public int CommandTimeout { get { return commandTimeout; } set { commandTimeout = value; } }
    public delegate string GetReturningHandler(string fieldName);
    public GetReturningHandler GetReturningProc;
    public string GetReturning(string fieldName)
    {
      if (GetReturningProc != null)
        return GetReturningProc(fieldName);
      return "";
    }
    public string OracleReturning(string fieldName)
    {
      return string.Format(" RETURNING {0}", fieldName);
    }
    public string PostgreSQLReturning(string fieldName)
    {
      return string.Format(" RETURNING {0}", fieldName);
    }
    public string SqlServerReturning(string fieldName)
    {
      return string.Format("; SELECT CAST(SCOPE_IDENTITY() AS INT)  /* {0} */", fieldName);
    }
    public delegate void GetSequenceHandler(string tableName, string fieldName, ref int value);
    public GetSequenceHandler GetSequenceProc;
    public void GetSequence(string tableName, string fieldName, ref int value)
    {
      if (GetSequenceProc != null)
        GetSequenceProc(tableName, fieldName, ref value);
    }
    public delegate void GetTimeStampHandler(ref DateTime value);
    public GetTimeStampHandler GetTimeStampProc;
    public void GetTimeStamp(ref DateTime value)
    {
      if (GetTimeStampProc != null)
        GetTimeStampProc(ref value);
    }
    void DefaultGetTimeStampProc(ref DateTime value)
    {
      value = DateTime.Now;
    }
    public delegate void GetUserStampHandler(ref string value);
    public GetUserStampHandler GetUserStampProc;
    public void GetUserStamp(ref string value)
    {
      if (GetUserStampProc != null)
        GetUserStampProc(ref value);
    }
    private bool isOpen = false;
    public IDbConnection Connection { get { return connection; } }
    public IDbTransaction Transaction { get { return transaction; } }
    public ParameterType TypeOfParameter { get { return typeOfParameter; } set { typeOfParameter = value; } }
    public VendorType TypeOfVendor
    {
      get { return typeOfVendor; }
      set
      {
        typeOfVendor = value;
        switch (value)
        {
          case VendorType.Oracle:
            typeOfParameter = ParameterType.Colon;
            GetReturningProc = OracleReturning;
            break;
          case VendorType.SqlServer:
            typeOfParameter = ParameterType.AtSymbol;
            GetReturningProc = SqlServerReturning;
            break;
          case VendorType.Odbc:
            typeOfParameter = ParameterType.QuestionMark;
            break;
          case VendorType.OleDb:
            typeOfParameter = ParameterType.QuestionMark;
            break;
          case VendorType.PostgreSQL:
            typeOfParameter = ParameterType.Dollar;
            GetReturningProc = PostgreSQLReturning;
            break;
          case VendorType.DB2:
            typeOfParameter = ParameterType.QuestionMark;
            break;
          case VendorType.Lite3:
            typeOfParameter = ParameterType.Colon;
            break;
        }
      }
    }
    public Connect(IDbConnection connection)
    {
      this.connection = connection;
      commandTimeout = 30;
      Type type = connection.GetType();
      if (type == typeof(System.Data.Odbc.OdbcConnection))
        TypeOfVendor = VendorType.Odbc;
      else if (type == typeof(System.Data.OleDb.OleDbConnection))
        TypeOfVendor = VendorType.OleDb;
      else if (type == typeof(System.Data.SqlClient.SqlConnection))
        TypeOfVendor = VendorType.SqlServer;
      else
        TypeOfVendor = VendorType.Oracle;
      GetTimeStampProc = DefaultGetTimeStampProc;
    }
    public void Open()
    {
      connection.Open();
      isOpen = true;
    }
    public void Close()
    {
      if (isOpen)
      {
        if (transaction != null)
        {
          Rollback();
          transaction.Dispose();
          transaction = null;
        }
        connection.Close();
        isOpen = false;
      }
    }
    private bool inTransaction = false;
    private bool doRollback = false;
    public void BeginTransaction()
    {
      if (inTransaction == true)
      {
        doRollback = true;
        EndTransaction();
      }
      transaction = connection.BeginTransaction();
      inTransaction = true;
      doRollback = false;
    }
    public void EndTransaction()
    {
      if (inTransaction == true)
      {
        if (doRollback == true)
          Rollback();
        else
          Commit();
      }
      inTransaction = false;
      doRollback = false;
    }
    public void FlagRollback()
    {
      if (inTransaction == true)
        doRollback = true;
      else
        Rollback();
    }
    public void Commit()
    {
      if (inTransaction == true)
        doRollback = inTransaction = false;
      if (transaction != null)
      {
        transaction.Commit();
        transaction.Dispose();
        transaction = null;
      }
    }
    public void Rollback()
    {
      if (inTransaction == true)
        doRollback = inTransaction = false;
      if (transaction != null)
      {
        transaction.Rollback();
        transaction.Dispose();
        transaction = null;
      }
    }
    public void Dispose()
    {
      Close();
    }
    public void PostgreSQLSequence(string tableName, string fieldName, ref int value)
    {
      Cursor cursor = new Cursor(this);
      cursor.Format(string.Format("select nextval('{0}_{1}_seq')::int", tableName.ToLower(), fieldName.ToLower()), 0);
      cursor.Run();
      cursor.Read();
      value = cursor.GetInt(0);
    }
    public void UsePostgreSQLSequence()
    {
      GetSequenceProc = PostgreSQLSequence;
    }
    public void OracleSequence(string tableName, string fieldName, ref int value)
    {
      Cursor cursor = new Cursor(this);
      cursor.Format(string.Format("select {0}Seq.NextVal from dual)", tableName), 0);
      cursor.Run();
      cursor.Read();
      value = cursor.GetInt(0);
    }
    public void UseOracleSequence()
    {
      GetSequenceProc = OracleSequence;
    }
  }
  public class Cursor : IDisposable
  {
    public Connect connect;
    protected IDbConnection connection;
    protected IDbCommand command;
    protected IDataReader reader;
    public IDbCommand Command { get { return command; } }
    public IDataReader Reader { get { return reader; } }
    public Cursor(Connect connect)
    {
      this.connect = connect;
      connection = connect.Connection;
    }
    private string ParameterShape(int no)
    {
      switch (connect.TypeOfParameter)
      {
        case ParameterType.QuestionMark:
          return "?";
        case ParameterType.Colon:
          return ":P" + no;
        case ParameterType.AtSymbol:
          return "@P" + no;
        case ParameterType.Dollar:
          return "$" + (no+1);
      }
      return "?";
    }
    public int GetSequence(string tableName, string fieldName, ref int value)
    {
      connect.GetSequence(tableName, fieldName, ref value);
      return value;
    }
    public DateTime GetTimeStamp(ref DateTime value)
    {
      connect.GetTimeStamp(ref value);
      return value;
    }
    public string GetUserStamp(ref string value)
    {
      connect.GetUserStamp(ref value);
      return value;
    }
    public void Procedure(string proc)
    {
      command = connection.CreateCommand();
      command.CommandText = proc;
      command.CommandType = CommandType.StoredProcedure;
      command.Connection = connect.Connection;
      if (connect.transaction != null)
        command.Transaction = connect.Transaction;
    }
    public void Format(string query, int count)
    {
      String[] parms = null;
      if (count > 0)
        parms = new String[count];
      for (int i = 0; i < count; i++)
        parms[i] = ParameterShape(i);
      command = connection.CreateCommand();
      if (count > 0)
        command.CommandText = string.Format(query, parms);
      else
        command.CommandText = query;
      command.Connection = connect.Connection;
      if (connect.transaction != null)
        command.Transaction = connect.Transaction;
    }
    public void Run()
    {
      command.CommandTimeout = connect.CommandTimeout;
      reader = command.ExecuteReader();
    }
    public void Exec()
    {
      command.ExecuteNonQuery();
    }
    public double GetDouble(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0.0;
      else
        return reader.GetDouble(ordinal);
    }
    public double GetDouble(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0.0;
      else
        return reader.GetDouble(ordinal);
    }
    public long GetInt64(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return (int)reader.GetInt64(ordinal);
    }
    public long GetInt64(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0;
      else
      {
        long result = reader.GetInt64(ordinal);
        return result;
      }
    }
    public long GetLong(int ordinal, out bool isNull) // this will be deprecated
    {
       return GetInt64(ordinal, out isNull);
    }
    public long GetLong(int ordinal) // this will be deprecated
    {
       return GetInt64(ordinal);
    }
    public int GetInt32(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
      {
        long result = reader.GetInt64(ordinal);
        return (Int32)result;
      }
    }
    public int GetInt32(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0;
      else
      {
        long result = reader.GetInt32(ordinal);
        return (int)result;
      }
    }
    public int GetInt(int ordinal, out bool isNull)  // this will be deprecated
    {
       return GetInt32(ordinal, out isNull);
    }
    public int GetInt(int ordinal)  // this will be deprecated
    {
      return GetInt32(ordinal);
    }
    public short GetInt16(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return reader.GetInt16(ordinal);
    }
    public short GetInt16(int ordinal)
    {
      string name = reader.GetName(ordinal);
      string typeName = reader.GetDataTypeName(ordinal);
      if (reader.IsDBNull(ordinal))
        return 0;
      else
      {
        short result = reader.GetInt16(ordinal);
        return result;
      }
    }
    public int GetShort(int ordinal, out bool isNull)  // this will be deprecated
    {
      return GetInt16(ordinal, out isNull);
    }
    public int GetShort(int ordinal)  // this will be deprecated
    {
      return GetInt16(ordinal);
    }
    public sbyte GetByte(int ordinal, out bool isNull)
    {
      string name = reader.GetName(ordinal);
      string typeName = reader.GetDataTypeName(ordinal);
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return (sbyte)(short)reader.GetInt16(ordinal);
    }
    public sbyte GetByte(int ordinal)
    {
      string typeName = reader.GetDataTypeName(ordinal);
      if (reader.IsDBNull(ordinal))
        return 0;
      else
        return (sbyte)(short)reader.GetInt16(ordinal);
    }
    public bool GetBoolean(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return false;
      else
        return reader.GetBoolean(ordinal);
    }
    public bool GetBoolean(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return false;
      else
        return reader.GetBoolean(ordinal);
    }
    public string GetString(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return "";
      else
        return reader.GetString(ordinal);
    }
    public string GetString(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return "";
      else
        return reader.GetString(ordinal);
    }
    DateTime NullDefault = new DateTime(1910, 06, 15, 00, 00, 00);
    public DateTime GetDateTime(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return NullDefault;
      else
        return reader.GetDateTime(ordinal);
    }
    public DateTime GetDateTime(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return NullDefault;
      else
        return reader.GetDateTime(ordinal);
    }
    public decimal GetDecimal(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0;
      else
        return reader.GetDecimal(ordinal);
    }
    public decimal GetDecimal(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return reader.GetDecimal(ordinal);
    }
    public byte[] GetBlob(int ordinal, int maxsize)
    {
      if (reader.IsDBNull(ordinal))
        return new byte[0];
      else
      {
        byte[] buffer = new byte[maxsize];
        int size = (int)reader.GetBytes(ordinal, 0, buffer, 0, maxsize);
        JPBlob blob = new JPBlob();
        blob.setBlob(buffer, size);
        return blob.Buffer;
      }
    }
    public byte[] GetBlob(int ordinal, int maxsize, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return new byte[0];
      else
      {
        byte[] buffer = new byte[maxsize];
        int size = (int)reader.GetBytes(ordinal, 0, buffer, 0, maxsize);
        JPBlob blob = new JPBlob();
        blob.setBlob(buffer, size);
        return blob.Buffer;
      }
    }
    public IDbDataParameter Parameter(int no, object value, bool isNull)
    {
      if (value != null && value.GetType() == typeof(byte[]))
        return BlobParameter(no, (byte[])value, isNull);
      IDbDataParameter parameter = command.CreateParameter();
      string p = ParameterShape(no);
      if (p != "?")
        parameter.ParameterName = p;
      if (isNull && value == null)
        parameter.Value = DBNull.Value;
      else
        parameter.Value = value;
      command.Parameters.Add(parameter);
      return parameter;
    }
    // another microfuckup
    public IDbDataParameter BlobParameter(int no, byte[] value, bool isNull)
    {
      string p = ParameterShape(no);
      System.Data.SqlClient.SqlParameter parameter = new System.Data.SqlClient.SqlParameter(p, SqlDbType.Image);
      if (isNull)
        parameter.Value = DBNull.Value;
      else
        parameter.Value = value;
      command.Parameters.Add(parameter);
      return parameter;
    }
    public IDbDataParameter Parameter(int no, object value)
    {
      return Parameter(no, value, false);
    }
    public void Close()
    {
      if (reader != null)
      {
        reader.Close();
        reader.Dispose();
        reader = null;
      }
    }
    public bool Read()
    {
      return reader.Read();
    }
    public bool HasReader()
    {
      return reader != null;
    }
    public void Dispose()
    {
      Close();
    }
  }
  public class Returning
  {
    public string head = "";
    public string output = "";
    public string sequence = "";
    public string tail = "";
    public bool dropField = false;
    public Returning(VendorType type, string table, string field)
    {
      switch (type)
      {
        case VendorType.Odbc:
          break;
        case VendorType.OleDb:
          break;
        case VendorType.Oracle:
          sequence = "select " + table + "Seq.NextVal,";
          tail = "returning " + field + "into ?";
          break;
        case VendorType.PostgreSQL:
          dropField = true;
          tail = "returning " + field + "into ?";
          break;
        case VendorType.SqlServer:
          dropField = true;
          output = "output inserted." + field;
          break;
        case VendorType.DB2:
          head = "select " + field + " from new table (";
          sequence = "select nextval for " + table + "seq,";
          tail = ")";
          break;
      }
    }
    public string checkUse(string line)
    {
      if (dropField == true)
        return "";
      return line;
    }
  }
  public class JPBlob
  {
    private byte[] buffer;
    public JPBlob()
    {
      buffer = new byte[0];
    }
    public byte[] Buffer
    {
      get
      {
        return buffer;
      }
      set
      {
        buffer = value;
      }
    }
    public byte[] getBlob()
    {
      int size = buffer.Length;
      byte[] blob = new byte[size + 4];
      Array.Copy(buffer, 0, blob, 4, size);
      blob[0] = (byte)((size & 0xFF000000) >> 24);
      blob[1] = (byte)((size & 0x00FF0000) >> 16);
      blob[2] = (byte)((size & 0x0000FF00) >> 8);
      blob[3] = (byte)(size & 0x000000FF);
      return blob;
    }
    public void setBlob(byte[] value)
    {
      int size = value.Length;
      if (size > 4)
        setBlob(value, size);
      else
        buffer = new byte[0];
    }
    public void setBlob(byte[] value, int length)
    {
      if (length > 4)
      {
        buffer = new byte[length - 4];
        Array.Copy(value, 4, buffer, 0, length - 4);
      }
      else
        buffer = new byte[0];
    }
  }
  public static class Nullable
  {
    public static long? GetInt64(object value)
    {
      if (value is DBNull) return null;
      return (long)value;
    }
    public static int? GetInt32(object value)
    {
      if (value is DBNull) return null;
      return (int)value;
    }
    public static short? GetInt16(object value)
    {
      if (value is DBNull) return null;
      return (short)value;
    }
    public static byte? GetByte(object value)
    {
      if (value is DBNull) return null;
      return (byte)(short)value;
    }
    public static double? GetDouble(object value)
    {
      if (value is DBNull) return null;
      return (double)value;
    }
  }
}

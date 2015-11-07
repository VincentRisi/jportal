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

namespace vlab.jportal
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
    AtSymbol
  }
  public enum VendorType
  {
    Oracle,
    SqlServer,
    Odbc,
    OleDb,
    PostgreSQL
  }
  public class Connect : IDisposable
  {
    internal IDbConnection connection;
    internal IDbTransaction transaction;
    internal ParameterType typeOfParameter;
    internal VendorType typeOfVendor;
    public delegate void GetSequenceHandler(string tableName, string fieldName, ref int value);
    public event GetSequenceHandler GetSequenceProc;
    public void GetSequence(string tableName, string fieldName, ref int value)
    {
      if (GetSequenceProc != null)
        GetSequenceProc(tableName, fieldName, ref value);
    }
    public delegate void GetTimeStampHandler(ref DateTime value);
    public event GetTimeStampHandler GetTimeStampProc;
    public void GetTimeStamp(ref DateTime value)
    {
      if (GetTimeStampProc != null)
        GetTimeStampProc(ref value);
    }
    public delegate void GetUserStampHandler(ref string value);
    public event GetUserStampHandler GetUserStampProc;
    public void GetUserStamp(ref string value)
    {
      if (GetUserStampProc != null)
        GetUserStampProc(ref value);
    }
    private bool isOpen = false;
    public IDbConnection Connection { get { return connection; } }
    public IDbTransaction Transaction { get { return transaction; } }
    public ParameterType TypeOfParameter { get { return typeOfParameter; } set { typeOfParameter = value; } }
    public VendorType TypeOfVendor { get { return typeOfVendor; } set { typeOfVendor = value; } }
    public Connect(IDbConnection connection)
    {
      this.connection = connection;
      Type type = connection.GetType();
      if (type == typeof(System.Data.Odbc.OdbcConnection))
      {
        typeOfParameter = ParameterType.QuestionMark;
        typeOfVendor = VendorType.Odbc;
      }
      else if (type == typeof(System.Data.OleDb.OleDbConnection))
      {
        typeOfParameter = ParameterType.QuestionMark;
        typeOfVendor = VendorType.OleDb;
      }
      else if (type == typeof(System.Data.SqlClient.SqlConnection))
      {
        typeOfParameter = ParameterType.AtSymbol;
        typeOfVendor = VendorType.SqlServer;
      }
      else
      {
        typeOfParameter = ParameterType.Colon;
        typeOfVendor = VendorType.Oracle;
      }
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
      GetSequenceProc += new GetSequenceHandler(PostgreSQLSequence);
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
      GetSequenceProc += new GetSequenceHandler(OracleSequence);
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
      reader = command.ExecuteReader();
    }
    public void Exec()
    {
      command.ExecuteNonQuery();
    }
    public int GetInt(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0;
      else
      {
        int result = reader.GetInt32(ordinal);
        return result;
      }
    }
    public int GetInt(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return (int) reader.GetInt32(ordinal);
    }
    public sbyte GetByte(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0;
      else
        return (sbyte)reader.GetByte(ordinal);
    }
    public sbyte GetByte(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0;
      else
        return (sbyte)reader.GetByte(ordinal);
    }
    public bool GetBoolean(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return false;
      else
        return reader.GetBoolean(ordinal);
    }
    public bool GetBoolean(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return false;
      else
        return reader.GetBoolean(ordinal);
    }
    public string GetString(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return "";
      else
        return reader.GetString(ordinal);
    }
    public string GetString(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return "";
      else
        return reader.GetString(ordinal);
    }
    public double GetDouble(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return 0.0;
      else
        return reader.GetDouble(ordinal);
    }
    public double GetDouble(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return 0.0;
      else
        return reader.GetDouble(ordinal);
    }
    public DateTime GetDateTime(int ordinal)
    {
      if (reader.IsDBNull(ordinal))
        return System.DateTime.Now;
      else
        return reader.GetDateTime(ordinal);
    }
    public DateTime GetDateTime(int ordinal, out bool isNull)
    {
      isNull = reader.IsDBNull(ordinal);
      if (isNull)
        return System.DateTime.Now;
      else
        return reader.GetDateTime(ordinal);
    }
    public void Parameter(int no, object value, bool isNull)
    {
      IDbDataParameter parameter = command.CreateParameter();
      string p = ParameterShape(no);
      if (p != "?")
        parameter.ParameterName = p;
      if (isNull)
        parameter.Value = DBNull.Value;
      else
        parameter.Value = value;
      command.Parameters.Add(parameter);
    }
    public void Parameter(int no, object value)
    {
      Parameter(no, value, false);
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
}

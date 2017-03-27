/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi

/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi, Hennie Hamman
/// ------------------------------------------------------------------
/// System : JPortal
using System;
using System.Data;

namespace bbd.jportal 
{
    public delegate string CredentialHandler();

    public delegate void LogHandler(string message, params object[] args);

    public delegate void LogHandlerException(Exception ex);

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
        Lite3,
        MySql
    }

    public interface IPortalHandler
    {
        #region Events

        event LogHandler Debug;

        event LogHandler Error;

        event LogHandlerException Exep;

        event LogHandler Info;

        event CredentialHandler UserName;

        event LogHandler Warning;

        #endregion Events
    }

    public static class Nullable
    {
        #region Methods

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

        public static short? GetInt16(object value)
        {
            if (value is DBNull) return null;
            return (short)value;
        }

        public static int? GetInt32(object value)
        {
            if (value is DBNull) return null;
            return (int)value;
        }

        public static long? GetInt64(object value)
        {
            if (value is DBNull) return null;
            return (long)value;
        }

        #endregion Methods
    }

    [AttributeUsage(AttributeTargets.Property)]
    public sealed class ColumnAttribute : Attribute
    {
        #region Constructors

        // Summary:
        //     Initializes a new instance of the System.Data.Linq.Mapping.ColumnAttribute
        //     class.
        public ColumnAttribute()
        {
        }

        #endregion Constructors

        #region Properties

        //
        // Summary:
        //     Gets or sets whether a column can contain null values.
        //
        // Returns:
        //     Default = true.
        public bool CanBeNull { get; set; }

        //
        // Summary:
        //     Gets or sets the type of the database column.
        //
        // Returns:
        //     See Remarks.
        public string DbType { get; set; }

        //
        // Summary:
        //     Gets or sets whether a column is a computed column in a database.
        //
        // Returns:
        //     Default = empty.
        public string Expression { get; set; }

        //
        // Summary:
        //     Gets or sets whether this class member represents a column that is part or
        //     all of the primary key of the table.
        //
        // Returns:
        //     Default = false.
        public bool IsPrimaryKey { get; set; }

        //
        // Summary:
        //     Gets or sets whether this class member represents a column that is a
        //     Sequence.
        //
        // Returns:
        //     Default = false.
        public bool isSequence { get; set; }

        //
        // Summary:
        //     Gets or sets whether the column type of the member is a database timestamp
        //     or version number.
        //
        // Returns:
        //     Default value = false.
        public bool IsVersion { get; set; }

        //
        // Summary:
        //     Gets or sets whether the column type of the member is a database length
        //     or version number.
        //
        // Returns:
        //     Default value = false.
        public int MaxLength { get; set; }

        //
        // Summary:
        //     Gets or sets the column precision
        //
        // Returns:
        //     Default value = false.
        public int Precision { get; set; }

        //
        // Summary:
        //     Gets or sets the column scale
        //
        // Returns:
        //     Default value = false.
        public int Scale { get; set; }

        #endregion Properties
    }

    public class JConnect : PortalHandlerBase, IDisposable
    {
        #region Fields

        public GetReturningHandler GetReturningProc;
        public GetSequenceHandler GetSequenceProc;
        public GetTimeStampHandler GetTimeStampProc;
        public GetUserStampHandler GetUserStampProc;
        public bool IsNew;
        internal int commandTimeout;
        internal IDbConnection connection;
        internal PortalHandlerBase handler;
        internal int queryToLong;
        internal IDbTransaction transaction;
        internal ParameterType typeOfParameter;
        internal VendorType typeOfVendor;
        private bool doRollback = false;
        private bool inTransaction = false;
        private bool isOpen = false;

        #endregion Fields

        #region Constructors

        /// <summary>
        /// Set up Portal connection
        /// </summary>
        /// <param name="connection">Db Connection</param>
        /// <param name="handler">Handel logging and credentials</param>
        /// <param name="queryToLong">Will log long running queries as warning</param>
        public JConnect(IDbConnection connection, PortalHandlerBase handler = null, int queryToLong = 5)
        {
            this.connection = connection;
            this.handler = handler;
            this.queryToLong = queryToLong;

            commandTimeout = 300;
            Type type = connection.GetType();
            if (type == typeof(System.Data.Odbc.OdbcConnection))
                TypeOfVendor = VendorType.Odbc;
            else if (type == typeof(System.Data.OleDb.OleDbConnection))
                TypeOfVendor = VendorType.OleDb;
            else if (type == typeof(System.Data.SqlClient.SqlConnection))
                TypeOfVendor = VendorType.SqlServer;
            else if (type == typeof(System.Data.SQLite.SQLiteConnection))
                TypeOfVendor = VendorType.Lite3;
            else
                TypeOfVendor = VendorType.Oracle;
            GetTimeStampProc = DefaultGetTimeStampProc;
            GetUserStampProc = DefaultGetUserStampProc;
        }

        #endregion Constructors

        #region Delegates

        public delegate string GetReturningHandler(string fieldName);

        public delegate void GetSequenceHandler(string tableName, string fieldName, ref int value);

        public delegate void GetTimeStampHandler(ref DateTime value);

        public delegate void GetUserStampHandler(ref string value);

        #endregion Delegates

        #region Properties

        public int CommandTimeout { get { return commandTimeout; } set { commandTimeout = value; } }
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

        #endregion Properties

        #region Methods

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

        public void Close()
        {
            if (isOpen)
            {
                if (transaction != null)
                {
                    Rollback();
                }
                connection.Close();
                isOpen = false;
            }
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

        public void Dispose()
        {
            Close();
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

        public string GetReturning(string fieldName)
        {
            if (GetReturningProc != null)
                return GetReturningProc(fieldName);
            return "";
        }

        public void GetSequence(string tableName, string fieldName, ref int value)
        {
            if (GetSequenceProc != null)
                GetSequenceProc(tableName, fieldName, ref value);
        }

        public void GetTimeStamp(ref DateTime value)
        {
            if (GetTimeStampProc != null)
                GetTimeStampProc(ref value);
        }

        public void GetUserStamp(ref string value)
        {
            if (GetUserStampProc != null)
                GetUserStampProc(ref value);
        }

        public bool IsClosed()
        {
            return !isOpen;
        }

        public void Open()
        {
            connection.Open();
            isOpen = true;
        }

        public string OracleReturning(string fieldName)
        {
            return string.Format(" RETURNING {0}", fieldName);
        }

        public void OracleSequence(string tableName, string fieldName, ref int value)
        {
            JCursor cursor = new JCursor(this);
            cursor.Format(string.Format("select {0}Seq.NextVal from dual)", tableName), 0);
            cursor.Run();
            cursor.Read();
            value = cursor.GetInt(0);
        }

        public string PostgreSQLReturning(string fieldName)
        {
            return string.Format(" RETURNING {0}", fieldName);
        }

        public void PostgreSQLSequence(string tableName, string fieldName, ref int value)
        {
            JCursor cursor = new JCursor(this);
            cursor.Format(string.Format("select nextval('{0}_{1}_seq')::int", tableName.ToLower(), fieldName.ToLower()), 0);
            cursor.Run();
            cursor.Read();
            value = cursor.GetInt(0);
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

        public void SqliteSequence(string tableName, string fieldName, ref int value)
        {
            JCursor cursor = new JCursor(this);
            cursor.Format(string.Format("select seq from sqlite_sequence where name = '{0}'", tableName), 0);
            cursor.Run();
            cursor.Read();
            value = cursor.GetInt(0) + 1;
        }

        public string SqlServerReturning(string fieldName)
        {
            return string.Format("; SELECT CAST(SCOPE_IDENTITY() AS INT)  /* {0} */", fieldName);
        }

        public void UseOracleSequence()
        {
            GetSequenceProc = OracleSequence;
        }

        public void UsePostgreSQLSequence()
        {
            GetSequenceProc = PostgreSQLSequence;
        }

        public void UseSqliteSequence()
        {
            GetSequenceProc = SqliteSequence;
        }

        private void DefaultGetTimeStampProc(ref DateTime value)
        {
            value = DateTime.Now;
        }

        private void DefaultGetUserStampProc(ref string value)
        {
            value = handler.OnUserName();
        }

        #endregion Methods
    }

    public class JCursor : IDisposable
    {
        #region Fields

        public JConnect connect;
        protected IDbCommand command;
        protected IDbConnection connection;
        protected PortalHandlerBase handler;
        protected IDataReader reader;
        private DateTime NullDefault = DateTime.MinValue;
        private int queryToLong;
        private DateTime startTimeFetch;

        #endregion Fields

        #region Constructors

        public JCursor(JConnect connect)
        {
            this.connect = connect;
            this.handler = connect.handler;
            connection = connect.Connection;
            this.queryToLong = connect.queryToLong;
        }

        #endregion Constructors

        #region Properties

        public IDbCommand Command { get { return command; } }
        public IDataReader Reader { get { return reader; } }

        #endregion Properties

        #region Methods

        // another microfuckup
        public IDbDataParameter BlobParameter(int no, byte[] value, bool isNull)
        {
            if (VendorType.Lite3 == connect.typeOfVendor)
            {
                string p = ParameterShape(no);
                System.Data.SQLite.SQLiteParameter parameter = new System.Data.SQLite.SQLiteParameter(p, DbType.Binary, value.Length);
                if (isNull)
                    parameter.Value = DBNull.Value;
                else
                    parameter.Value = value;
                command.Parameters.Add(parameter);
                return parameter;
                //return null;
            }
            else
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
        }

        public void Close()
        {
            if (reader != null)
            {
                reader.Close();
                reader.Dispose();
                reader = null;
                TestTime(true);
            }
        }

        public void Dispose()
        {
            Close();
        }

        public void Exec()
        {
            startTimeFetch = DateTime.Now;
            command.ExecuteNonQuery();
            TestTime();
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

        public byte[] GetBlob(int ordinal, int maxsize)
        {
            if (reader.IsDBNull(ordinal))
                return new byte[0];
            else
            {
                byte[] buffer = new byte[maxsize];
                int size = (int)reader.GetBytes(ordinal, 0, buffer, 0, maxsize);
                JPBlob blob = new JPBlob();
                blob.setBlob(buffer, size, 0);
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
                blob.setBlob(buffer, size, 0);
                return blob.Buffer;
            }
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

        public sbyte GetByte(int ordinal, out bool isNull)
        {
            string name = reader.GetName(ordinal);
            string typeName = reader.GetDataTypeName(ordinal);
            isNull = reader.IsDBNull(ordinal);
            if (isNull)
                return 0;
            else
                return (sbyte)reader.GetByte(ordinal);
        }

        public sbyte GetByte(int ordinal)
        {
            string typeName = reader.GetDataTypeName(ordinal);
            if (reader.IsDBNull(ordinal))
                return 0;
            else
                return (sbyte)reader.GetByte(ordinal);
        }

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

        public Guid GetGuid(int ordinal, out bool isNull)
        {
            isNull = reader.IsDBNull(ordinal);
            if (reader.IsDBNull(ordinal))
                return Guid.Empty;
            else
                return reader.GetGuid(ordinal);
        }

        public Guid GetGuid(int ordinal)
        {
            if (reader.IsDBNull(ordinal))
                return Guid.Empty;
            else
                return reader.GetGuid(ordinal);
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

        public int GetInt32(int ordinal, out bool isNull)
        {
            isNull = reader.IsDBNull(ordinal);
            if (isNull)
                return 0;
            else
            {
                int result = reader.GetInt32(ordinal);
                return (int)result;
            }
        }

        public int GetInt32(int ordinal)
        {
            if (reader.IsDBNull(ordinal))
                return 0;
            else
            {
                int result = Convert.ToInt32(reader.GetInt32(ordinal));
                return (int)result;
            }
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

        public int GetSequence(string tableName, string fieldName, ref int value)
        {
            connect.GetSequence(tableName, fieldName, ref value);
            return value;
        }

        public int GetShort(int ordinal, out bool isNull)  // this will be deprecated
        {
            return GetInt16(ordinal, out isNull);
        }

        public int GetShort(int ordinal)  // this will be deprecated
        {
            return GetInt16(ordinal);
        }

        public string GetString(int ordinal, out bool isNull)
        {
            isNull = reader.IsDBNull(ordinal);
            if (isNull)
                return null;
            else
                return reader.GetString(ordinal);
        }

        public string GetString(int ordinal)
        {
            if (reader.IsDBNull(ordinal))
                return null;
            else
                return reader.GetString(ordinal);
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

        public object GetValue(int ordinal)
        {
            return reader.GetValue(ordinal);
            
        }

        public object GetValueByName(string fieldName)
        {
            var ordinal = reader.GetOrdinal(fieldName);
            return reader.GetValue(ordinal);

        }

        public bool HasReader()
        {
            return reader != null;
        }

        /// <summary>
        ///
        /// </summary>
        /// <param name="no"></param>
        /// <param name="value"></param>
        /// <param name="isNull"></param>
        /// <returns></returns>
        public IDbDataParameter Parameter(int no, object value, bool isNull)
        {
            if (value != null && value.GetType() == typeof(byte[]))
                return BlobParameter(no, (byte[])value, isNull);
            IDbDataParameter parameter = command.CreateParameter();
            string p = ParameterShape(no);
            if (p != "?")
                parameter.ParameterName = p;
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

        public void Procedure(string proc)
        {
            command = connection.CreateCommand();
            command.CommandText = proc;
            command.CommandType = CommandType.StoredProcedure;
            command.Connection = connect.Connection;
            if (connect.transaction != null)
                command.Transaction = connect.Transaction;
        }

        public bool Read()
        {
            try
            {
                return reader.Read();
            }
            catch (Exception ex)
            {
                Close();

                handler.OnError("Failed to read from SQL statement\r\n{0}", ReplaceParameters(command));
                handler.OnExeption(ex);
                throw;
            }
        }

        public void Run()
        {
            try
            {
                command.CommandTimeout = connect.CommandTimeout;
                startTimeFetch = DateTime.Now;
                reader = command.ExecuteReader();
                //TestTime();
            }
            catch (Exception ex)
            {
                Close();
                handler.OnError("Failed to execute SQL statement\r\n{0}", ReplaceParameters(command));
                handler.OnExeption(ex);
                throw;
            }
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
                    return "$" + (no + 1);
            }
            return "?";
        }

        private string ReplaceParameters(IDbCommand command)
        {
            string txt = command.CommandText;
            for (int i = command.Parameters.Count; i > 0; i--)
            {
                IDbDataParameter p = command.Parameters[i - 1] as IDbDataParameter;
                string val = null;
                if (p.Value != null && p.Value != DBNull.Value)
                {
                    val = p.Value.ToString();
                }

                if (val == null)
                {
                    val = "null";
                }
                else
                {
                    val = "'" + val + "'";
                }

                txt = txt.Replace(p.ParameterName, val);
            }

            return txt;
        }

        private void TestTime(bool fetch = false)
        {
            IDbCommand comm = command;
            string message = "{0}{1}ms to run query [{2}]";
            var temp = command.CommandText.Split(';');
            

            if (fetch)
            {
                if (temp.Length == 2)
                {
                    comm.CommandText = temp[1];
                }

                message = "{0}{1}ms to run fetch [{2}]";
            }
            var elapsedTime = (DateTime.Now - startTimeFetch);

            if (handler != null)
            {
              if (elapsedTime.Seconds > queryToLong)
                handler.OnWarning(message, elapsedTime.Seconds == 0 ? "" : elapsedTime.Seconds + "s", elapsedTime.Milliseconds, ReplaceParameters(comm));
              else
                handler.OnDebug(message, elapsedTime.Seconds == 0 ? "" : elapsedTime.Seconds + "s", elapsedTime.Milliseconds, ReplaceParameters(comm));
            }
        }

        #endregion Methods
    }

    public class JPBlob
    {
        #region Fields

        private byte[] buffer;

        #endregion Fields

        #region Constructors

        public JPBlob()
        {
            buffer = new byte[0];
        }

        #endregion Constructors

        #region Properties

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

        #endregion Properties

        #region Methods

        public byte[] getBlob()
        {
            //int size = buffer.Length;
            //byte[] blob = new byte[size + 4];
            //Array.Copy(buffer, 0, blob, 4, size);
            //blob[0] = (byte)((size & 0xFF000000) >> 24);
            //blob[1] = (byte)((size & 0x00FF0000) >> 16);
            //blob[2] = (byte)((size & 0x0000FF00) >> 8);
            //blob[3] = (byte)(size & 0x000000FF);
            return buffer;
        }

        public void setBlob(byte[] value)
        {
            int size = value.Length;
            if (size > 0)
                setBlob(value, size, 0);
            else
                buffer = new byte[0];
        }

        public void setBlob(byte[] value, int length, int currPos)
        {
            if (length > 0)
            {
                buffer = new byte[length];
                Array.Copy(value, currPos, buffer, 0, length);
            }
            else
                buffer = new byte[0];
        }

        #endregion Methods
    }

    public class JPortalException : ApplicationException
    {
        #region Constructors

        public JPortalException(string error)
            : base(error)
        {
        }

        public JPortalException(string error, Exception ex)
            : base(error, ex)
        {
        }

        protected JPortalException()
            : base()
        {
        }

        #endregion Constructors
    }

    public class PortalHandlerBase : IPortalHandler
    {
        #region Events

        public event LogHandler Debug;

        public event LogHandler Error;

        public event LogHandlerException Exep;

        public event LogHandler Info;

        public event CredentialHandler UserName;

        public event LogHandler Warning;

        #endregion Events

        #region Public Methods

        public string OnUserName()
        {
            if (UserName != null)
            {
                return UserName();
            }
            else
            {
                return "Portal";
            }
        }

        #endregion Public Methods

        #region Methods

        public void OnDebug(string message, params object[] args)
        {
            if (Debug != null)
            {
                Debug(message, args);
            }
        }

        public void OnError(string message, params object[] args)
        {
            if (Error != null)
            {
                Error(message, args);
            }
        }

        public void OnExeption(Exception ex)
        {
            if (Exep != null)
            {
                Exep(ex);
            }
        }

        public void OnInfo(string message, params object[] args)
        {
            if (Info != null)
            {
                Info(message, args);
            }
        }

        public void OnWarning(string message, params object[] args)
        {
            if (Warning != null)
            {
                Warning(message, args);
            }
        }

        #endregion Methods
    }

    public class Returning
    {
        #region Fields

        public bool dropField = false;
        public string head = "";
        public string output = "";
        public string sequence = "";
        public string tail = "";

        #endregion Fields

        #region Constructors

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
                    //output = "output inserted." + field;
                    tail = " SELECT CAST(SCOPE_IDENTITY() AS BIGINT)";
                    break;

                case VendorType.DB2:
                    head = "select " + field + " from new table (";
                    sequence = "select nextval for " + table + "seq,";
                    tail = ")";
                    break;

                case VendorType.Lite3:
                    dropField = true;
                    tail = "; select last_insert_rowid()";
                    break;
            }
        }

        #endregion Constructors

        #region Methods

        public string checkUse(string line)
        {
            if (dropField == true)
                return "";
            return line;
        }

        #endregion Methods
    }
}
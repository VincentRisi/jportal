package jportal.decompilers;
import bbd.jportal.*;
import java.sql.*;
/**
* PLEASE NOTE no fields as we are not defining anything new
* but the name Oracle will be tagged onto the procedures.
* This code was generated, do not modify it, modify it at source and regenerate it.
*/
public class Oracle
{
  Connector connector;
  Connection connection;
  public Oracle(Connector connector)
  {
    this.connector = connector;
    connection = connector.connection;
  }
  public class Standard
  {
    /**
    * @param Connector for specific database
    */
    public Standard()
    {
    }
  }
  public Standard getStandard()
  {
    return new Standard();
  }
  /**
  */
  public class Tables
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (output)
    */
    public String tableName;
    /**
    * (output)
    */
    public String tableSpaceName;
    /**
    * (output)
    */
    public int initialExtent;
    /**
    * (output)
    */
    public int nextExtent;
    public Tables()
    {
      owner = "";
      tableName = "";
      tableSpaceName = "";
      initialExtent = 0;
      nextExtent = 0;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tables() throws SQLException
    {
      String statement = 
      "select TABLE_NAME "
    + ", TABLESPACE_NAME "
    + ", INITIAL_EXTENT "
    + ", NEXT_EXTENT "
    + "from SYS.ALL_TABLES "
    + "where OWNER = ? "
    + "order by TABLE_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tables(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      tableName =  result.getString(1);
      tableSpaceName =  result.getString(2);
      initialExtent =  result.getInt(3);
      nextExtent =  result.getInt(4);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @exception SQLException is passed through
    */
    public Query tables(
      String owner
    ) throws SQLException
    {
      this.owner = owner;
      return tables();
    }
  }
  public Tables getTables()
  {
    return new Tables();
  }
  /**
  */
  public class TablePrimaryKey
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String constraintName;
    public TablePrimaryKey()
    {
      owner = "";
      tableName = "";
      constraintName = "";
    }
    /**
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean tablePrimaryKey() throws SQLException
    {
      String statement = 
      "select CONSTRAINT_NAME "
    + "from SYS.ALL_CONSTRAINTS "
    + "where OWNER = ? "
    + "and   TABLE_NAME = ? "
    + "and   CONSTRAINT_TYPE = 'P' "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, tableName);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      constraintName =  result.getString(1);
      result.close();
      prep.close();
      return true;
    }
    /**
    * Returns at most one record.
    * @return true if a record is returned.
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public boolean tablePrimaryKey(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tablePrimaryKey();
    }
  }
  public TablePrimaryKey getTablePrimaryKey()
  {
    return new TablePrimaryKey();
  }
  /**
  */
  public class TableUniqueKeys
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String constraintName;
    public TableUniqueKeys()
    {
      owner = "";
      tableName = "";
      constraintName = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableUniqueKeys() throws SQLException
    {
      String statement = 
      "select CONSTRAINT_NAME "
    + "from SYS.ALL_CONSTRAINTS "
    + "where OWNER = ? "
    + "and   TABLE_NAME = ? "
    + "and   CONSTRAINT_TYPE = 'U' "
    + "order by CONSTRAINT_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, tableName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableUniqueKeys(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      constraintName =  result.getString(1);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableUniqueKeys(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tableUniqueKeys();
    }
  }
  public TableUniqueKeys getTableUniqueKeys()
  {
    return new TableUniqueKeys();
  }
  /**
  */
  public class ForeignLinks
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (output)
    */
    public String tableName;
    /**
    * (output)
    */
    public String linkName;
    public ForeignLinks()
    {
      owner = "";
      tableName = "";
      linkName = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query foreignLinks() throws SQLException
    {
      String statement = 
      "select distinct a.TABLE_NAME "
    + ", b.TABLE_NAME LINK_NAME "
    + "from SYS.ALL_CONSTRAINTS a, SYS.ALL_CONSTRAINTS b "
    + "where a.OWNER = ? "
    + "and   a.CONSTRAINT_TYPE = 'R' "
    + "and   b.OWNER = a.R_OWNER "
    + "and   b.CONSTRAINT_NAME = a.R_CONSTRAINT_NAME "
    + "and   a.TABLE_NAME <> b.TABLE_NAME "
    + "order by a.TABLE_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean foreignLinks(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      tableName =  result.getString(1);
      linkName =  result.getString(2);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @exception SQLException is passed through
    */
    public Query foreignLinks(
      String owner
    ) throws SQLException
    {
      this.owner = owner;
      return foreignLinks();
    }
  }
  public ForeignLinks getForeignLinks()
  {
    return new ForeignLinks();
  }
  /**
  */
  public class TableForeignKeys
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String constraintName;
    /**
    * (output)
    */
    public String rConstraintName;
    /**
    * (output)
    */
    public String rOwner;
    /**
    * (output)
    */
    public String rTableName;
    public TableForeignKeys()
    {
      owner = "";
      tableName = "";
      constraintName = "";
      rConstraintName = "";
      rOwner = "";
      rTableName = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableForeignKeys() throws SQLException
    {
      String statement = 
      "select a.CONSTRAINT_NAME "
    + ", a.R_CONSTRAINT_NAME "
    + ", a.R_OWNER "
    + ", b.TABLE_NAME R_TABLE_NAME "
    + "from SYS.ALL_CONSTRAINTS a, SYS.ALL_CONSTRAINTS b "
    + "where a.OWNER = ? "
    + "and   a.TABLE_NAME = ? "
    + "and   a.CONSTRAINT_TYPE = 'R' "
    + "and   b.OWNER = a.R_OWNER "
    + "and   b.CONSTRAINT_NAME = a.R_CONSTRAINT_NAME "
    + "order by a.CONSTRAINT_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, tableName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableForeignKeys(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      constraintName =  result.getString(1);
      rConstraintName =  result.getString(2);
      rOwner =  result.getString(3);
      rTableName =  result.getString(4);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableForeignKeys(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tableForeignKeys();
    }
  }
  public TableForeignKeys getTableForeignKeys()
  {
    return new TableForeignKeys();
  }
  /**
  */
  public class TableTypeC
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String constraintName;
    /**
    * (output)
    */
    public String condition;
    public TableTypeC()
    {
      owner = "";
      tableName = "";
      constraintName = "";
      condition = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableTypeC() throws SQLException
    {
      String statement = 
      "select CONSTRAINT_NAME "
    + ", SEARCH_CONDITION "
    + "from SYS.ALL_CONSTRAINTS "
    + "where OWNER = ? "
    + "and   CONSTRAINT_TYPE = 'C' "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableTypeC(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      constraintName =  result.getString(1);
      condition =  result.getString(2);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableTypeC(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tableTypeC();
    }
  }
  public TableTypeC getTableTypeC()
  {
    return new TableTypeC();
  }
  /**
  */
  public class ConstraintColumns
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String constraintName;
    /**
    * (output)
    */
    public String tableName;
    /**
    * (output)
    */
    public String column;
    /**
    * (output)
    */
    public int position;
    public ConstraintColumns()
    {
      owner = "";
      constraintName = "";
      tableName = "";
      column = "";
      position = 0;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query constraintColumns() throws SQLException
    {
      String statement = 
      "select TABLE_NAME "
    + ", COLUMN_NAME "
    + ", POSITION "
    + "from SYS.ALL_CONS_COLUMNS "
    + "where OWNER = ? "
    + "and   CONSTRAINT_NAME = ? "
    + "order by POSITION "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, constraintName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean constraintColumns(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      tableName =  result.getString(1);
      column =  result.getString(2);
      position =  result.getInt(3);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param constraintName input.
    * @exception SQLException is passed through
    */
    public Query constraintColumns(
      String owner
    , String constraintName
    ) throws SQLException
    {
      this.owner = owner;
      this.constraintName = constraintName;
      return constraintColumns();
    }
  }
  public ConstraintColumns getConstraintColumns()
  {
    return new ConstraintColumns();
  }
  /**
  */
  public class GetIndexTableSpace
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String indexName;
    /**
    * (output)
    */
    public String tableSpaceName;
    /**
    * (output)
    */
    public int initialExtent;
    /**
    * (output)
    */
    public int nextExtent;
    public GetIndexTableSpace()
    {
      owner = "";
      indexName = "";
      tableSpaceName = "";
      initialExtent = 0;
      nextExtent = 0;
    }
    /**
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean getIndexTableSpace() throws SQLException
    {
      String statement = 
      "select TABLESPACE_NAME "
    + ", INITIAL_EXTENT "
    + ", NEXT_EXTENT "
    + "from SYS.ALL_INDEXES "
    + "Where OWNER = ? "
    + "and   INDEX_NAME = ? "
    + "order by TABLESPACE_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, indexName);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      tableSpaceName =  result.getString(1);
      initialExtent =  result.getInt(2);
      nextExtent =  result.getInt(3);
      result.close();
      prep.close();
      return true;
    }
    /**
    * Returns at most one record.
    * @return true if a record is returned.
    * @param owner input.
    * @param indexName input.
    * @exception SQLException is passed through
    */
    public boolean getIndexTableSpace(
      String owner
    , String indexName
    ) throws SQLException
    {
      this.owner = owner;
      this.indexName = indexName;
      return getIndexTableSpace();
    }
  }
  public GetIndexTableSpace getGetIndexTableSpace()
  {
    return new GetIndexTableSpace();
  }
  /**
  */
  public class Indexes
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (output)
    */
    public String indexName;
    /**
    * (output)
    */
    public String tableName;
    /**
    * (output)
    */
    public String tableSpaceName;
    /**
    * (output)
    */
    public int initialExtent;
    /**
    * (output)
    */
    public int nextExtent;
    public Indexes()
    {
      owner = "";
      indexName = "";
      tableName = "";
      tableSpaceName = "";
      initialExtent = 0;
      nextExtent = 0;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query indexes() throws SQLException
    {
      String statement = 
      "select INDEX_NAME "
    + ", TABLE_NAME "
    + ", TABLESPACE_NAME "
    + ", INITIAL_EXTENT "
    + ", NEXT_EXTENT "
    + "from SYS.ALL_INDEXES "
    + "where OWNER = ? "
    + "and   INDEX_NAME not in (select CONSTRAINT_NAME from ALL_CONSTRAINTS where OWNER = ?) "
    + "order by INDEX_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean indexes(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      indexName =  result.getString(1);
      tableName =  result.getString(2);
      tableSpaceName =  result.getString(3);
      initialExtent =  result.getInt(4);
      nextExtent =  result.getInt(5);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @exception SQLException is passed through
    */
    public Query indexes(
      String owner
    ) throws SQLException
    {
      this.owner = owner;
      return indexes();
    }
  }
  public Indexes getIndexes()
  {
    return new Indexes();
  }
  /**
  */
  public class TableIndexes
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String indexName;
    /**
    * (output)
    */
    public String tableSpaceName;
    /**
    * (output)
    */
    public int initialExtent;
    /**
    * (output)
    */
    public int nextExtent;
    public TableIndexes()
    {
      owner = "";
      tableName = "";
      indexName = "";
      tableSpaceName = "";
      initialExtent = 0;
      nextExtent = 0;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableIndexes() throws SQLException
    {
      String statement = 
      "select INDEX_NAME "
    + ", TABLESPACE_NAME "
    + ", INITIAL_EXTENT "
    + ", NEXT_EXTENT "
    + "from SYS.ALL_INDEXES "
    + "where OWNER = ? "
    + "and   TABLE_NAME = ? "
    + "and   INDEX_NAME not in (select CONSTRAINT_NAME from ALL_CONSTRAINTS where OWNER = ?) "
    + "order by INDEX_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, tableName);
      prep.setString(3, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableIndexes(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      indexName =  result.getString(1);
      tableSpaceName =  result.getString(2);
      initialExtent =  result.getInt(3);
      nextExtent =  result.getInt(4);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableIndexes(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tableIndexes();
    }
  }
  public TableIndexes getTableIndexes()
  {
    return new TableIndexes();
  }
  /**
  */
  public class IndexColumns
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String indexName;
    /**
    * (output)
    */
    public int position;
    /**
    * (output)
    */
    public String tableOwner;
    /**
    * (output)
    */
    public String tableName;
    /**
    * (output)
    */
    public String column;
    public IndexColumns()
    {
      owner = "";
      indexName = "";
      position = 0;
      tableOwner = "";
      tableName = "";
      column = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query indexColumns() throws SQLException
    {
      String statement = 
      "select COLUMN_POSITION "
    + ", TABLE_OWNER "
    + ", TABLE_NAME "
    + ", COLUMN_NAME "
    + "from SYS.ALL_IND_COLUMNS "
    + "where INDEX_OWNER = ? "
    + "and   INDEX_NAME = ? "
    + "order by COLUMN_POSITION "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, indexName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean indexColumns(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      position =  result.getInt(1);
      tableOwner =  result.getString(2);
      tableName =  result.getString(3);
      column =  result.getString(4);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param indexName input.
    * @exception SQLException is passed through
    */
    public Query indexColumns(
      String owner
    , String indexName
    ) throws SQLException
    {
      this.owner = owner;
      this.indexName = indexName;
      return indexColumns();
    }
  }
  public IndexColumns getIndexColumns()
  {
    return new IndexColumns();
  }
  /**
  */
  public class TableColumns
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public int columnID;
    /**
    * (output)
    */
    public String columnName;
    /**
    * (output)
    */
    public String dataType;
    /**
    * (output)
    */
    public int dataLength;
    /**
    * (output)
    */
    public int dataPrecision;
    /**
    * (output)
    */
    public int dataScale;
    /**
    * (output)
    */
    public String nullable;
    /**
    * (output)
    */
    public int defaultLength;
    /**
    * (output)
    */
    public String defaultValue;
    public TableColumns()
    {
      owner = "";
      tableName = "";
      columnID = 0;
      columnName = "";
      dataType = "";
      dataLength = 0;
      dataPrecision = 0;
      dataScale = 0;
      nullable = "";
      defaultLength = 0;
      defaultValue = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableColumns() throws SQLException
    {
      String statement = 
      "select COLUMN_ID "
    + ", COLUMN_NAME "
    + ", DATA_TYPE "
    + ", DATA_LENGTH "
    + ", nvl(DATA_PRECISION, -1) "
    + ", nvl(DATA_SCALE, -1) "
    + ", NULLABLE "
    + ", nvl(DEFAULT_LENGTH, -1) "
    + ", DATA_DEFAULT "
    + "from SYS.ALL_TAB_COLUMNS "
    + "where OWNER =  ? "
    + "and TABLE_NAME = ? "
    + "order by COLUMN_ID "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, tableName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableColumns(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      columnID =  result.getInt(1);
      columnName =  result.getString(2);
      dataType =  result.getString(3);
      dataLength =  result.getInt(4);
      dataPrecision =  result.getInt(5);
      dataScale =  result.getInt(6);
      nullable =  result.getString(7);
      defaultLength =  result.getInt(8);
      defaultValue =  result.getString(9);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableColumns(
      String owner
    , String tableName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      return tableColumns();
    }
  }
  public TableColumns getTableColumns()
  {
    return new TableColumns();
  }
  /**
  */
  public class TableSequence
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (input)
    */
    public String sequenceName;
    /**
    * (output)
    */
    public String columnName;
    public TableSequence()
    {
      owner = "";
      tableName = "";
      sequenceName = "";
      columnName = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableSequence() throws SQLException
    {
      String statement = 
      "select "
    + "col.COLUMN_NAME "
    + "from SYS.ALL_SEQUENCES seq "
    + ", SYS.ALL_CONSTRAINTS con "
    + ", SYS.ALL_CONS_COLUMNS col "
    + "where seq.SEQUENCE_OWNER  = ? "
    + "and   seq.SEQUENCE_NAME   = ? "
    + "and   con.OWNER           = ? "
    + "and   con.CONSTRAINT_TYPE = 'P' "
    + "and   con.TABLE_NAME      = ? "
    + "and   col.OWNER           = ? "
    + "and   col.TABLE_NAME      = ? "
    + "and   col.CONSTRAINT_NAME = con.CONSTRAINT_NAME "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, sequenceName);
      prep.setString(3, owner);
      prep.setString(4, tableName);
      prep.setString(5, owner);
      prep.setString(6, tableName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableSequence(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      columnName =  result.getString(1);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param tableName input.
    * @param sequenceName input.
    * @exception SQLException is passed through
    */
    public Query tableSequence(
      String owner
    , String tableName
    , String sequenceName
    ) throws SQLException
    {
      this.owner = owner;
      this.tableName = tableName;
      this.sequenceName = sequenceName;
      return tableSequence();
    }
  }
  public TableSequence getTableSequence()
  {
    return new TableSequence();
  }
  /**
  */
  public class TableGrants
  {
    /**
    * (input)
    */
    public String grantor;
    /**
    * (input)
    */
    public String tableName;
    /**
    * (output)
    */
    public String grantee;
    /**
    * (output)
    */
    public String schema;
    /**
    * (output)
    */
    public String privilege;
    public TableGrants()
    {
      grantor = "";
      tableName = "";
      grantee = "";
      schema = "";
      privilege = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query tableGrants() throws SQLException
    {
      String statement = 
      "select GRANTEE "
    + ", TABLE_SCHEMA "
    + ", PRIVILEGE "
    + "from SYS.ALL_TAB_PRIVS "
    + "where GRANTOR = ? "
    + "and TABLE_NAME = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, grantor);
      prep.setString(2, tableName);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean tableGrants(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      grantee =  result.getString(1);
      schema =  result.getString(2);
      privilege =  result.getString(3);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param grantor input.
    * @param tableName input.
    * @exception SQLException is passed through
    */
    public Query tableGrants(
      String grantor
    , String tableName
    ) throws SQLException
    {
      this.grantor = grantor;
      this.tableName = tableName;
      return tableGrants();
    }
  }
  public TableGrants getTableGrants()
  {
    return new TableGrants();
  }
  /**
  */
  public class Sequences
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (output)
    */
    public String name;
    /**
    * (output)
    */
    public double minValue;
    /**
    * (output)
    */
    public double maxValue;
    /**
    * (output)
    */
    public double increment;
    /**
    * (output)
    */
    public String cycleFlag;
    /**
    * (output)
    */
    public String orderFlag;
    /**
    * (output)
    */
    public double cache;
    /**
    * (output)
    */
    public double startWith;
    public Sequences()
    {
      owner = "";
      name = "";
      minValue = 0.0;
      maxValue = 0.0;
      increment = 0.0;
      cycleFlag = "";
      orderFlag = "";
      cache = 0.0;
      startWith = 0.0;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query sequences() throws SQLException
    {
      String statement = 
      "select SEQUENCE_NAME "
    + ", MIN_VALUE "
    + ", MAX_VALUE "
    + ", INCREMENT_BY "
    + ", CYCLE_FLAG "
    + ", ORDER_FLAG "
    + ", CACHE_SIZE "
    + ", LAST_NUMBER "
    + "from SYS.ALL_SEQUENCES "
    + "where SEQUENCE_OWNER = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean sequences(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      name =  result.getString(1);
      minValue =  result.getDouble(2);
      maxValue =  result.getDouble(3);
      increment =  result.getDouble(4);
      cycleFlag =  result.getString(5);
      orderFlag =  result.getString(6);
      cache =  result.getDouble(7);
      startWith =  result.getDouble(8);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @exception SQLException is passed through
    */
    public Query sequences(
      String owner
    ) throws SQLException
    {
      this.owner = owner;
      return sequences();
    }
  }
  public Sequences getSequences()
  {
    return new Sequences();
  }
  /**
  */
  public class Synonyms
  {
    /**
    * (input)
    */
    public String tableOwner;
    /**
    * (output)
    */
    public String owner;
    /**
    * (output)
    */
    public String synonym;
    /**
    * (output)
    */
    public String tableName;
    public Synonyms()
    {
      tableOwner = "";
      owner = "";
      synonym = "";
      tableName = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query synonyms() throws SQLException
    {
      String statement = 
      "select OWNER "
    + ", SYNONYM_NAME "
    + ", TABLE_NAME "
    + "from SYS.ALL_SYNONYMS "
    + "where TABLE_OWNER = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, tableOwner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean synonyms(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      owner =  result.getString(1);
      synonym =  result.getString(2);
      tableName =  result.getString(3);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param tableOwner input.
    * @exception SQLException is passed through
    */
    public Query synonyms(
      String tableOwner
    ) throws SQLException
    {
      this.tableOwner = tableOwner;
      return synonyms();
    }
  }
  public Synonyms getSynonyms()
  {
    return new Synonyms();
  }
  /**
  */
  public class Views
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (output)
    */
    public String name;
    /**
    * (output)
    */
    public int textLength;
    /**
    * (output)
    */
    public String text;
    public Views()
    {
      owner = "";
      name = "";
      textLength = 0;
      text = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query views() throws SQLException
    {
      String statement = 
      "select VIEW_NAME "
    + ", TEXT_LENGTH "
    + ", TEXT "
    + "from SYS.ALL_VIEWS "
    + "where OWNER = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean views(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      name =  result.getString(1);
      textLength =  result.getInt(2);
      text =  result.getString(3);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @exception SQLException is passed through
    */
    public Query views(
      String owner
    ) throws SQLException
    {
      this.owner = owner;
      return views();
    }
  }
  public Views getViews()
  {
    return new Views();
  }
  /**
  */
  public class ViewColumns
  {
    /**
    * (input)
    */
    public String owner;
    /**
    * (input)
    */
    public String name;
    /**
    * (output)
    */
    public int position;
    /**
    * (output)
    */
    public String column;
    public ViewColumns()
    {
      owner = "";
      name = "";
      position = 0;
      column = "";
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query viewColumns() throws SQLException
    {
      String statement = 
      "select COLUMN_ID "
    + ", COLUMN_NAME "
    + "from SYS.ALL_TAB_COLUMNS "
    + "where OWNER = ? "
    + "and   TABLE_NAME = ? "
    + "order by COLUMN_ID "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, owner);
      prep.setString(2, name);
      ResultSet result = prep.executeQuery();
      Query query = new Query(prep, result);
      return query;
    }
    /**
    * Returns the next record in a result set.
    * @param result The result set for the query.
    * @return true while records are found.
    * @exception SQLException is passed through
    */
    public boolean viewColumns(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      position =  result.getInt(1);
      column =  result.getString(2);
      return true;
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @param owner input.
    * @param name input.
    * @exception SQLException is passed through
    */
    public Query viewColumns(
      String owner
    , String name
    ) throws SQLException
    {
      this.owner = owner;
      this.name = name;
      return viewColumns();
    }
  }
  public ViewColumns getViewColumns()
  {
    return new ViewColumns();
  }
}

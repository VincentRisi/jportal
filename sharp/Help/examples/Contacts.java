package jportal.oracledemo;
import bbd.jportal.*;
import java.sql.*;
/**
* This table demonstrates the usage of Sequence,
* Timestamp and Userstamp
* This code was generated, do not modify it, modify it at source and regenerate it.
*/
public class Contacts
{
  Connector connector;
  Connection connection;
  public Contacts(Connector connector)
  {
    this.connector = connector;
    connection = connector.connection;
  }
  public class Standard
  {
    /**
    * Unique sequence number (gaps allowed)
    */
    public int id;
    /**
    * Name of the contacts
    */
    public String name;
    /**
    * Date and Time when inserted or updated
    */
    public Timestamp whenDone;
    /**
    * Logged on user responsible for the insert or update
    */
    public String whoDoneIt;
    /**
    * @param Connector for specific database
    */
    public Standard()
    {
      id = 0;
      name = "";
      whenDone = new Timestamp(0);
      whoDoneIt = "";
    }
    /**
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void insert() throws SQLException
    {
      String statement = 
      "insert into Contacts"
    + "( id"
    + ", name"
    + ", whenDone"
    + ", whoDoneIt"
    + ") "
    + "values"
    + " (?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ")"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      id = connector.getSequence("Contacts");
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setInt(1, id);
      prep.setString(2, name);
      prep.setTimestamp(3, whenDone);
      prep.setString(4, whoDoneIt);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param name input.
    * @exception SQLException is passed through
    */
    public void insert(
      String name
    ) throws SQLException
    {
      this.name = name;
      insert();
    }
    /**
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void update() throws SQLException
    {
      String statement = 
      "update Contacts"
    + " set"
    + "  name = ?"
    + ", whenDone = ?"
    + ", whoDoneIt = ?"
    + " where id = ?"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setString(1, name);
      prep.setTimestamp(2, whenDone);
      prep.setString(3, whoDoneIt);
      prep.setInt(4, id);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param id key input.
    * @param name input.
    * @exception SQLException is passed through
    */
    public void update(
      int id
    , String name
    ) throws SQLException
    {
      this.name = name;
      this.id = id;
      update();
    }
    /**
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean selectOne() throws SQLException
    {
      String statement = 
      "select"
    + "  name"
    + ", whenDone"
    + ", whoDoneIt"
    + " from Contacts"
    + " where id = ?"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setInt(1, id);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      name =  result.getString(1);
      whenDone =  result.getTimestamp(2);
      whoDoneIt =  result.getString(3);
      result.close();
      prep.close();
      return true;
    }
    /**
    * Returns at most one record.
    * @return true if a record is returned.
    * @param id key input.
    * @exception SQLException is passed through
    */
    public boolean selectOne(
      int id
    ) throws SQLException
    {
      this.id = id;
      return selectOne();
    }
    /**
    * Returns any number of records.
    * @return result set of records found
    * @exception SQLException is passed through
    */
    public Query selectAllSorted() throws SQLException
    {
      String statement = 
      "select"
    + "  id"
    + ", name"
    + ", whenDone"
    + ", whoDoneIt"
    + " from Contacts"
    + " order by id"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
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
    public boolean selectAllSorted(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      id =  result.getInt(1);
      name =  result.getString(2);
      whenDone =  result.getTimestamp(3);
      whoDoneIt =  result.getString(4);
      return true;
    }
  }
  public Standard getStandard()
  {
    return new Standard();
  }
  /**
  * class method as it has no input or output.
  * @exception SQLException is passed through
  */
  public static void deleteAll(Connector connector) throws SQLException
  {
    String statement = 
      "delete from Contacts"
    ;
    PreparedStatement prep = connector.connection.prepareStatement(statement);
    prep.executeUpdate();
    prep.close();
  }
  /**
  */
  public class DeleteOne
  {
    /**
    * Unique sequence number (gaps allowed)
    * (input)
    */
    public int id;
    public DeleteOne()
    {
      id = 0;
    }
    /**
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void deleteOne() throws SQLException
    {
      String statement = 
      "delete from Contacts"
    + " where id = ?"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setInt(1, id);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param id key input.
    * @exception SQLException is passed through
    */
    public void deleteOne(
      int id
    ) throws SQLException
    {
      this.id = id;
      deleteOne();
    }
  }
  public DeleteOne getDeleteOne()
  {
    return new DeleteOne();
  }
  /**
  */
  public class Count
  {
    /**
    * (output)
    */
    public int noOf;
    public Count()
    {
      noOf = 0;
    }
    /**
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean count() throws SQLException
    {
      String statement = 
      "select count(*) noOf from Contacts"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      noOf =  result.getInt(1);
      result.close();
      prep.close();
      return true;
    }
  }
  public Count getCount()
  {
    return new Count();
  }
  /**
  */
  public class Exists
  {
    /**
    * Unique sequence number (gaps allowed)
    * (input)
    */
    public int id;
    /**
    * (output)
    */
    public int noOf;
    public Exists()
    {
      id = 0;
      noOf = 0;
    }
    /**
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean exists() throws SQLException
    {
      String statement = 
      "select count(*) noOf from Contacts"
    + " where id = ?"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setInt(1, id);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      noOf =  result.getInt(1);
      result.close();
      prep.close();
      return true;
    }
    /**
    * Returns at most one record.
    * @return true if a record is returned.
    * @param id key input.
    * @exception SQLException is passed through
    */
    public boolean exists(
      int id
    ) throws SQLException
    {
      this.id = id;
      return exists();
    }
  }
  public Exists getExists()
  {
    return new Exists();
  }
  /**
  * This procedure demonstrates a single record output
  */
  public class FindName
  {
    /**
    * name to base the lookup on
    * (input)
    */
    public String name;
    /**
    * id no of record looked up
    * (output)
    */
    public int id;
    public FindName()
    {
      name = "";
      id = 0;
    }
    /**
    * This procedure demonstrates a single record output
    * Returns at most one record.
    * @return true if a record is found
    * @exception SQLException is passed through
    */
    public boolean findName() throws SQLException
    {
      String statement = 
      "SELECT id FROM Contacts WHERE name = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      prep.setString(1, name);
      ResultSet result = prep.executeQuery();
      if (!result.next())
      {
        result.close();
        prep.close();
        return false;
      }
      id =  result.getInt(1);
      result.close();
      prep.close();
      return true;
    }
    /**
    * Returns at most one record.
    * @return true if a record is returned.
    * @param name input.
    * @exception SQLException is passed through
    */
    public boolean findName(
      String name
    ) throws SQLException
    {
      this.name = name;
      return findName();
    }
  }
  public FindName getFindName()
  {
    return new FindName();
  }
  /**
  * This procedure demonstrates an update with no output
  */
  public class ChangeName
  {
    /**
    * New name for record
    * (input)
    */
    public String newName;
    /**
    * Timestamp when done
    * (input)
    */
    public Timestamp whenDone;
    /**
    * Userstamp of who dunnit
    * (input)
    */
    public String whoDoneIt;
    /**
    * Old name to change from
    * (input)
    */
    public String oldName;
    public ChangeName()
    {
      newName = "";
      whenDone = new Timestamp(0);
      whoDoneIt = "";
      oldName = "";
    }
    /**
    * This procedure demonstrates an update with no output
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void changeName() throws SQLException
    {
      String statement = 
      "UPDATE Contacts "
    + "SET name = ? "
    + ", whenDone = ? "
    + ", whoDoneIt = ? "
    + "WHERE name = ? "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setString(1, newName);
      prep.setTimestamp(2, whenDone);
      prep.setString(3, whoDoneIt);
      prep.setString(4, oldName);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param newName input.
    * @param oldName input.
    * @exception SQLException is passed through
    */
    public void changeName(
      String newName
    , String oldName
    ) throws SQLException
    {
      this.newName = newName;
      this.oldName = oldName;
      changeName();
    }
  }
  public ChangeName getChangeName()
  {
    return new ChangeName();
  }
  /**
  * This procedure is similar to changeName but demostrates dynamic parameters
  */
  public class ChangeName2
  {
    /**
    * Timestamp when done
    * (input)
    */
    public Timestamp whenDone;
    /**
    * Userstamp of who dunnit
    * (input)
    */
    public String whoDoneIt;
    /**
    * (dynamic)
    */
    public String newName;
    /**
    * (dynamic)
    */
    public String oldName;
    public ChangeName2()
    {
      whenDone = new Timestamp(0);
      whoDoneIt = "";
      newName = "";
      oldName = "";
    }
    /**
    * This procedure is similar to changeName but demostrates dynamic parameters
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void changeName2() throws SQLException
    {
      String statement = 
      "// Note that they must be passed as strings. eg. 'HARRY' "
    + "UPDATE Contacts "
    + "SET name =  "
    +newName
    + " "
    + ", whenDone = ? "
    + ", whoDoneIt = ? "
    + "WHERE name =  "
    +oldName
    + " "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setTimestamp(1, whenDone);
      prep.setString(2, whoDoneIt);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param changeName2 dynamic input.
    * @param changeName2 dynamic input.
    * @exception SQLException is passed through
    */
    public void changeName2(
      String newName
    , String oldName
    ) throws SQLException
    {
      this.newName = newName;
      this.oldName = oldName;
      changeName2();
    }
  }
  public ChangeName2 getChangeName2()
  {
    return new ChangeName2();
  }
  /**
  * This procedure is similar to changeName but demostrates dynamic parameters
  */
  public class ChangeName3
  {
    /**
    * Timestamp when done
    * (input)
    */
    public Timestamp whenDone;
    /**
    * Userstamp of who dunnit
    * (input)
    */
    public String whoDoneIt;
    /**
    * (dynamic)
    */
    public String newName;
    /**
    * (dynamic)
    */
    public String Where;
    public ChangeName3()
    {
      whenDone = new Timestamp(0);
      whoDoneIt = "";
      newName = "";
      Where = "";
    }
    /**
    * This procedure is similar to changeName but demostrates dynamic parameters
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void changeName3() throws SQLException
    {
      String statement = 
      "UPDATE Contacts "
    + "SET name =  "
    +newName
    + ", "
    + "whenDone = ?, "
    + "whoDoneIt = ? "
    + "WHERE  "
    +Where
    + " "
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setTimestamp(1, whenDone);
      prep.setString(2, whoDoneIt);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param changeName3 dynamic input.
    * @param changeName3 dynamic input.
    * @exception SQLException is passed through
    */
    public void changeName3(
      String newName
    , String Where
    ) throws SQLException
    {
      this.newName = newName;
      this.Where = Where;
      changeName3();
    }
  }
  public ChangeName3 getChangeName3()
  {
    return new ChangeName3();
  }
}

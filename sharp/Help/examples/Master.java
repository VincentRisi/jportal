package jportal.oracledemo;
import bbd.jportal.*;
import java.sql.*;
/**
* This table demonstrates the usage of Sequence, Timestamp and Userstamp
* it also demostrates a link or foreign key to Contacts based on contact
* This code was generated, do not modify it, modify it at source and regenerate it.
*/
public class Master
{
  Connector connector;
  Connection connection;
  public Master(Connector connector)
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
    * Name of the master
    */
    public String name;
    /**
    * address lines (say street)
    */
    public String addr1;
    /**
    * address lines (say suburb)
    */
    public String addr2;
    /**
    * address lines (say town)
    */
    public String addr3;
    /**
    * phone numbers
    */
    public String phone;
    /**
    * foreign link to Contacts
    */
    public int contact;
    /**
    * Timestamp when done
    */
    public Timestamp whenDone;
    /**
    * Userstamp of who dunnit
    */
    public String whoDoneIt;
    /**
    * @param Connector for specific database
    */
    public Standard()
    {
      id = 0;
      name = "";
      addr1 = "";
      addr2 = "";
      addr3 = "";
      phone = "";
      contact = 0;
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
      "insert into Master"
    + "( id"
    + ", name"
    + ", addr1"
    + ", addr2"
    + ", addr3"
    + ", phone"
    + ", contact"
    + ", whenDone"
    + ", whoDoneIt"
    + ") "
    + "values"
    + " (?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ", ?"
    + ")"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      id = connector.getSequence("Master");
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setInt(1, id);
      prep.setString(2, name);
      prep.setString(3, addr1);
      prep.setString(4, addr2);
      prep.setString(5, addr3);
      prep.setString(6, phone);
      prep.setInt(7, contact);
      prep.setTimestamp(8, whenDone);
      prep.setString(9, whoDoneIt);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param name input.
    * @param addr1 input.
    * @param addr2 input.
    * @param addr3 input.
    * @param phone input.
    * @param contact input.
    * @exception SQLException is passed through
    */
    public void insert(
      String name
    , String addr1
    , String addr2
    , String addr3
    , String phone
    , int contact
    ) throws SQLException
    {
      this.name = name;
      this.addr1 = addr1;
      this.addr2 = addr2;
      this.addr3 = addr3;
      this.phone = phone;
      this.contact = contact;
      insert();
    }
    /**
    * Returns no output.
    * @exception SQLException is passed through
    */
    public void update() throws SQLException
    {
      String statement = 
      "update Master"
    + " set"
    + "  name = ?"
    + ", addr1 = ?"
    + ", addr2 = ?"
    + ", addr3 = ?"
    + ", phone = ?"
    + ", contact = ?"
    + ", whenDone = ?"
    + ", whoDoneIt = ?"
    + " where id = ?"
      ;
      PreparedStatement prep = connection.prepareStatement(statement);
      whenDone = connector.getTimestamp();
      whoDoneIt = connector.getUserstamp();
      prep.setString(1, name);
      prep.setString(2, addr1);
      prep.setString(3, addr2);
      prep.setString(4, addr3);
      prep.setString(5, phone);
      prep.setInt(6, contact);
      prep.setTimestamp(7, whenDone);
      prep.setString(8, whoDoneIt);
      prep.setInt(9, id);
      prep.executeUpdate();
      prep.close();
    }
    /**
    * Returns no records.
    * @param id key input.
    * @param name input.
    * @param addr1 input.
    * @param addr2 input.
    * @param addr3 input.
    * @param phone input.
    * @param contact input.
    * @exception SQLException is passed through
    */
    public void update(
      int id
    , String name
    , String addr1
    , String addr2
    , String addr3
    , String phone
    , int contact
    ) throws SQLException
    {
      this.name = name;
      this.addr1 = addr1;
      this.addr2 = addr2;
      this.addr3 = addr3;
      this.phone = phone;
      this.contact = contact;
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
    + ", addr1"
    + ", addr2"
    + ", addr3"
    + ", phone"
    + ", contact"
    + ", whenDone"
    + ", whoDoneIt"
    + " from Master"
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
      addr1 =  result.getString(2);
      addr2 =  result.getString(3);
      addr3 =  result.getString(4);
      phone =  result.getString(5);
      contact =  result.getInt(6);
      whenDone =  result.getTimestamp(7);
      whoDoneIt =  result.getString(8);
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
    public Query selectAll() throws SQLException
    {
      String statement = 
      "select"
    + "  id"
    + ", name"
    + ", addr1"
    + ", addr2"
    + ", addr3"
    + ", phone"
    + ", contact"
    + ", whenDone"
    + ", whoDoneIt"
    + " from Master"
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
    public boolean selectAll(Query query) throws SQLException
    {
      if (!query.result.next())
      {
        query.close();
        return false;
      }
      ResultSet result = query.result;
      id =  result.getInt(1);
      name =  result.getString(2);
      addr1 =  result.getString(3);
      addr2 =  result.getString(4);
      addr3 =  result.getString(5);
      phone =  result.getString(6);
      contact =  result.getInt(7);
      whenDone =  result.getTimestamp(8);
      whoDoneIt =  result.getString(9);
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
      "delete from Master"
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
      "delete from Master"
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
      "select count(*) noOf from Master"
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
      "select count(*) noOf from Master"
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
}

/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// 
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
package bbd.pickle;

import java.io.Serializable;
import java.util.Vector;

public class Application implements Serializable
{
  private static final long serialVersionUID = -6109567341206733481L;

  public static class LinkTable
  {
    Link link;
    String name;

    public LinkTable(Link link, String name)
    {
      this.link = link;
      this.name = name;
    }
  }

  public static class ValidationTable
  {
    Validation validation;
    String name;

    public ValidationTable(Validation validation, String name)
    {
      this.validation = validation;
      this.name = name;
    }
  }

  public static class RelationTable
  {
    Relation relation;
    String name;
    boolean from;

    public RelationTable(Relation relation, String name, boolean from)
    {
      this.relation = relation;
      this.name = name;
      this.from = from;
    }
  }

  public String name;
  public String descr;
  public String version;
  public String output;
  public String server;
  public String user;
  public String password;
  public String registry;
  public Vector<Table> tables;
  public Vector<Relation> relations;
  public Vector<String> flags;
  public Vector<LinkTable> missingLinks;
  public Vector<RelationTable> missingRelations;
  public Vector<ValidationTable> missingValidations;
  public Vector<Field> supplieds;
  public Validation validationInit;
  public Validation validationAll;
  public Validation validationOther;
  public int charSize;
  public int descrSize;

  public Application()
  {
    output = "";
    name = "";
    descr = "";
    version = "";
    server = "";
    user = "";
    password = "";
    registry = "";
    tables = new Vector<Table>();
    relations = new Vector<Relation>();
    flags = new Vector<String>();
    missingLinks = new Vector<LinkTable>();
    missingRelations = new Vector<RelationTable>();
    missingValidations = new Vector<ValidationTable>();
    supplieds = new Vector<Field>();
    validationInit = null;
    validationAll = null;
    validationOther = null;
    charSize = 16;
    descrSize = 50;
  }

  /**
   * Check for the existence of a table
   */
  public boolean hasTable(String s)
  {
    int i;
    for (i = 0; i < tables.size(); i++)
    {
      Table table = (Table) tables.elementAt(i);
      if (table.name.equalsIgnoreCase(s))
        return true;
    }
    return false;
  }

  /**
   * Check for the existence of a table
   */
  public Table getTable(String s)
  {
    int i;
    for (i = 0; i < tables.size(); i++)
    {
      Table table = (Table) tables.elementAt(i);
      if (table.name.equalsIgnoreCase(s))
        return table;
    }
    return null;
  }

  /**
   * Check for the existence of a table
   */
  public boolean hasSupplied(String s)
  {
    int i;
    for (i = 0; i < supplieds.size(); i++)
    {
      Field field = (Field) supplieds.elementAt(i);
      if (field.name.equalsIgnoreCase(s))
        return true;
    }
    return false;
  }

  public Relation getRelation(String s)
  {
    int i;
    for (i = 0; i < relations.size(); i++)
    {
      Relation relation = (Relation) relations.elementAt(i);
      if (relation.name.equalsIgnoreCase(s))
        return relation;
    }
    return null;
  }

  public boolean checkMissing(Relation relation)
  {
    boolean result = false;
    for (int i = 0; i < missingValidations.size(); i++)
    {
      ValidationTable vt = (ValidationTable) missingValidations.elementAt(i);
      if (vt.name.equalsIgnoreCase(relation.name))
      {
        relation.validation = vt.validation;
        System.out.println("Missing relation validation " + relation.name
            + " resolved");
        result = true;
      }
    }
    return result;
  }

  public boolean checkMissing(Table table)
  {
    boolean result = false;
    for (int i = 0; i < missingLinks.size(); i++)
    {
      LinkTable lt = (LinkTable) missingLinks.elementAt(i);
      if (lt.name.equalsIgnoreCase(table.name))
      {
        lt.link.table = table;
        System.out.println("Missing table link " + table.name + " resolved");
        result = true;
      }
    }
    for (int i = 0; i < missingRelations.size(); i++)
    {
      RelationTable rt = (RelationTable) missingRelations.elementAt(i);
      if (rt.name.equalsIgnoreCase(table.name))
      {
        if (rt.from == true)
        {
          rt.relation.fromTable = table;
          for (int j = 0; j < rt.relation.fromFieldNames.size(); j++)
          {
            String s = (String) rt.relation.fromFieldNames.elementAt(j);
            Field field = rt.relation.fromTable.getPKField(j);
            System.out.println("From " + field.name + ":" + j + " = " + s);
            rt.relation.fromFields.addElement(field);
          }
        } else
        {
          rt.relation.toTable = table;
          for (int j = 0; j < rt.relation.toFieldNames.size(); j++)
          {
            String s = (String) rt.relation.toFieldNames.elementAt(j);
            Field field = rt.relation.toTable.getPKField(j);
            System.out.println("To " + field.name + ":" + j + " = " + s);
            rt.relation.fromFields.addElement(field);
          }
        }
        System.out.println("Missing table relation " + table.name + " resolved");
        result = true;
      }
    }
    for (int i = 0; i < missingValidations.size(); i++)
    {
      ValidationTable vt = (ValidationTable) missingValidations.elementAt(i);
      if (vt.name.equalsIgnoreCase(table.name))
      {
        table.validation = vt.validation;
        System.out.println("Missing table validation " + table.name
            + " resolved");
        result = true;
      }
    }
    return result;
  }

  /**
   * Check for the existence of a table
   */
  public int getTableNo(String s)
  {
    int i;
    for (i = 0; i < tables.size(); i++)
    {
      Table table = (Table) tables.elementAt(i);
      if (table.name.equalsIgnoreCase(s))
        return i;
    }
    return -1;
  }
}

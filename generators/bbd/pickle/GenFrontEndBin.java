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

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class GenFrontEndBin
{
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate Domain Access");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Application application = (Application)in.readObject();
        in.close();
        generate(application, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  public static void generate(Application application, String output, PrintWriter outLog)
  {
    int noTables = 0;
    int noRelations = 0;
    int noFields = 0;
    int noEnums = 0;
    int noKeyFields = 0;
    int noOrderFields = 0;
    int noShowFields = 0;
    int noBreakFields = 0;
    int noLinks = 0;
    int noLinkPairs = 0;
    try
    {
      outLog.println("Code: "+output+application.name+".bin");
      OutputStream outFile = new FileOutputStream(output+application.name+".bin");
      try
      {
        DataOutputStream outData = new DataOutputStream(outFile);
        try
        {
          int paddedLength, n;
          noTables = application.tables.size();
          noRelations = application.relations.size();
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            n += table.fields.size();
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            n += relation.fromFields.size();
            n += relation.toFields.size();
          }
          if (application.supplieds.size() > 0)
        	n += application.supplieds.size();  
          noFields = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.fields.size(); j++)
            {
              Field field = (Field) table.fields.elementAt(j);
              n += field.enums.size();
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            for (int j=0; j<relation.fromFields.size(); j++)
            {
              Field field = (Field) relation.fromFields.elementAt(j);
              n += field.enums.size();
            }
            for (int j=0; j<relation.toFields.size(); j++)
            {
              Field field = (Field) relation.toFields.elementAt(j);
              n += field.enums.size();
            }
          }
          noEnums = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            Vector<Field> keyVector = table.order;
            for (int j=0; j<table.keys.size(); j++)
            {
              Key key = (Key) table.keys.elementAt(j);
              if (key.primary)
              {
                keyVector = key.list;
                break;
              }
            }
            n += keyVector.size();
          }
          noKeyFields = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            n += table.order.size();
          }
          noOrderFields = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            n += table.show.size();
          }
          noShowFields = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            n += table.breaks.size();
          }
          noBreakFields = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            n += table.links.size();
          }
          n += application.relations.size()*2;
          noLinks = n;
          n = 0;
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.links.size(); j++)
            {
              Link link = (Link) table.links.elementAt(j);
              n += link.list.size();
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            n += relation.fromFields.size();
            n += relation.toFields.size();
          }
          noLinkPairs = n;
          writeInt(outData, 0xBADFACED);
          writeString(outData, "APPLICATION");
          writeString(outData, application.name);
          writeString(outData, application.descr);
          writeString(outData, application.version);
          writeEncode(outData, application.server);
          writeEncode(outData, application.user);
          writeEncode(outData, application.password);
          writeString(outData, application.registry);
          writeInt(outData, noTables);
          writeInt(outData, noRelations);
          writeInt(outData, noFields);
          writeInt(outData, noEnums);
          writeInt(outData, noKeyFields);
          writeInt(outData, noOrderFields);
          writeInt(outData, noShowFields);
          writeInt(outData, noBreakFields);
          writeInt(outData, noLinks);
          writeInt(outData, noLinkPairs);
          int offsetLinks = 0;
          int offsetLinkPairs = 0;
          int offsetFields = 0;
          int offsetEnums = 0;
          int offsetKeyFields = 0;
          int offsetOrderFields = 0;
          int offsetBreakFields = 0;
          int offsetShowFields = 0;
          StringBuffer buffer = new StringBuffer();  
          for (int j=0; j<application.validationInit.code.size(); j++)
          {
            String line = (String) application.validationInit.code.elementAt(j);
            buffer.append(line);
          }
          writeString(outData, buffer.toString());
          buffer = new StringBuffer();  
          for (int j=0; j<application.validationAll.code.size(); j++)
          {
            String line = (String) application.validationAll.code.elementAt(j);
            buffer.append(line);
          }
          writeString(outData, buffer.toString());
          buffer = new StringBuffer();  
          for (int j=0; j<application.validationOther.code.size(); j++)
          {
            String line = (String) application.validationOther.code.elementAt(j);
            buffer.append(line);
          }
          writeString(outData, buffer.toString());
          writeString(outData, "TABLES");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            Vector<Field> keyVector = table.order;
            for (int j=0; j<table.keys.size(); j++)
            {
              Key key = (Key) table.keys.elementAt(j);
              if (key.primary)
              {
                keyVector = key.list;
                break;
              }
            }
            writeString(outData, table.name);
            writeString(outData, table.descr);
            writeInt(outData, table.fields.size());
            writeInt(outData, offsetFields);
            writeInt(outData, table.links.size());
            writeInt(outData, offsetLinks);
            writeInt(outData, keyVector.size());
            writeInt(outData, offsetKeyFields);
            writeInt(outData, table.order.size());
            writeInt(outData, offsetOrderFields);
            writeInt(outData, table.show.size());
            writeInt(outData, offsetShowFields);
            writeInt(outData, table.breaks.size());
            writeInt(outData, offsetBreakFields);
            writeInt(outData, table.viewOnly?1:0);
            offsetFields += table.fields.size();
            offsetLinks += table.links.size();
            offsetKeyFields += keyVector.size();
            offsetOrderFields += table.order.size();
            offsetShowFields += table.show.size();
            buffer = new StringBuffer();  
            for (int j=0; j<table.validation.code.size(); j++)
            {
              String line = (String) table.validation.code.elementAt(j);
              buffer.append(line);
            }
            writeString(outData, buffer.toString());
          }
          writeString(outData, "RELATIONS");
          if (application.relations.size() > 0)
          {
            for (int i=0; i<application.relations.size(); i++)
            {
              Relation relation = (Relation) application.relations.elementAt(i);
              writeString(outData, relation.name);
              writeString(outData, relation.descr);
              writeInt(outData, application.getTableNo(relation.fromTable.name));
              writeInt(outData, relation.fromFields.size());
              writeInt(outData, application.getTableNo(relation.toTable.name));
              writeInt(outData, relation.toFields.size());
              writeInt(outData, offsetFields);
              writeInt(outData, offsetLinks); offsetLinks++;
              writeInt(outData, offsetLinks); offsetLinks++;
              offsetFields += (relation.fromFields.size()+relation.toFields.size());
              buffer = new StringBuffer();  
              for (int j=0; j<relation.validation.code.size(); j++)
              {
                String line = (String) relation.validation.code.elementAt(j);
                buffer.append(line);
              }
              writeString(outData, buffer.toString());
            }
          }
          writeString(outData, "FIELDS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            int offsetData = 0;
            for (int j=0; j<table.fields.size(); j++)
            {
              Field field = (Field) table.fields.elementAt(j);
              writeString(outData, field.name);
              writeInt(outData, field.type);
              writeInt(outData, field.length);
              writeInt(outData, field.precision);
              writeInt(outData, field.scale);
              writeInt(outData, offsetData);
              offsetData += paddedLength = (((fieldLength(field)-1)/8)+1)*8;
              writeInt(outData, paddedLength);
              writeInt(outData, field.isUppercase?1:0);
              writeInt(outData, field.isNull?1:0);
              writeInt(outData, field.enums.size());
              writeInt(outData, offsetEnums);
              offsetEnums += field.enums.size();
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            int offsetData = 0;
            for (int j=0; j<relation.fromFields.size(); j++)
            {
              Field field = (Field) relation.fromFields.elementAt(j);
              if (relation.fromShort.length() > 0)
                writeString(outData, relation.fromShort+field.name);
              else if (relation.fromFieldNames.size() > j)
                writeString(outData, relation.fromFieldNames.elementAt(j));
              else
                writeString(outData, field.name);
              writeInt(outData, field.type);
              writeInt(outData, field.length);
              writeInt(outData, field.precision);
              writeInt(outData, field.scale);
              writeInt(outData, offsetData);
              offsetData += paddedLength = (((fieldLength(field)-1)/8)+1)*8;
              writeInt(outData, paddedLength);
              writeInt(outData, field.isUppercase?1:0);
              writeInt(outData, field.isNull?1:0);
              writeInt(outData, field.enums.size());
              writeInt(outData, offsetEnums);
              offsetEnums += field.enums.size();
            }
            for (int j=0; j<relation.toFields.size(); j++)
            {
              Field field = (Field) relation.toFields.elementAt(j);
              if (relation.fromShort.length() > 0)
                writeString(outData, relation.toShort+field.name);
              else if (relation.toFieldNames.size() > j)
                writeString(outData, relation.toFieldNames.elementAt(j));
              else
                writeString(outData, field.name);
              writeInt(outData, field.type);
              writeInt(outData, field.length);
              writeInt(outData, field.precision);
              writeInt(outData, field.scale);
              writeInt(outData, offsetData);
              offsetData += paddedLength = (((fieldLength(field)-1)/8)+1)*8;
              writeInt(outData, paddedLength);
              writeInt(outData, field.isUppercase?1:0);
              writeInt(outData, field.isNull?1:0);
              writeInt(outData, field.enums.size());
              writeInt(outData, offsetEnums);
              offsetEnums += field.enums.size();
            }
          }
          for (int j=0; j<application.supplieds.size(); j++)
          {
            Field field = (Field) application.supplieds.elementAt(j);
            writeString(outData, field.name);
            writeInt(outData, field.type);
            writeInt(outData, field.length);
            writeInt(outData, field.precision);
            writeInt(outData, field.scale);
            writeInt(outData, 0);
            paddedLength = (((fieldLength(field)-1)/8)+1)*8;
            writeInt(outData, paddedLength);
            writeInt(outData, field.isUppercase?1:0);
            writeInt(outData, field.isNull?1:0);
            writeInt(outData, 0);
            writeInt(outData, 0);
          }
          writeString(outData, "ENUMS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.fields.size(); j++)
            {
              Field field = (Field) table.fields.elementAt(j);
              for (int k=0; k<field.enums.size(); k++)
              {
                Enum en = (Enum) field.enums.elementAt(k);
                writeString(outData, en.name);
                writeInt(outData, en.value);
              }
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            for (int j=0; j<relation.fromFields.size(); j++)
            {
              Field field = (Field) relation.fromFields.elementAt(j);
              for (int k=0; k<field.enums.size(); k++)
              {
                Enum en = (Enum) field.enums.elementAt(k);
                writeString(outData, en.name);
                writeInt(outData, en.value);
              }
            }
            for (int j=0; j<relation.toFields.size(); j++)
            {
              Field field = (Field) relation.toFields.elementAt(j);
              for (int k=0; k<field.enums.size(); k++)
              {
                Enum en = (Enum) field.enums.elementAt(k);
                writeString(outData, en.name);
                writeInt(outData, en.value);
              }
            }
          }
          writeString(outData, "KEYFIELDS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            Vector<Field> keyVector = table.order;
            for (int j=0; j<table.keys.size(); j++)
            {
              Key key = (Key) table.keys.elementAt(j);
              if (key.primary)
              {
                keyVector = key.list;
                break;
              }
            }
            for (int j=0; j<keyVector.size(); j++)
            {
              Field field = (Field) keyVector.elementAt(j);
              writeInt(outData, table.getFieldNo(field.name));
            }
          }
          writeString(outData, "ORDERFIELDS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.order.size(); j++)
            {
              Field field = (Field) table.order.elementAt(j);
              writeInt(outData, table.getFieldNo(field.name));
            }
          }
          writeString(outData, "SHOWFIELDS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.show.size(); j++)
            {
              Field field = (Field) table.show.elementAt(j);
              writeInt(outData, table.getFieldNo(field.name));
            }
          }
          writeString(outData, "BREAKFIELDS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.breaks.size(); j++)
            {
              Field field = (Field) table.breaks.elementAt(j);
              writeInt(outData, table.getFieldNo(field.name));
            }
          }
          writeString(outData, "LINKS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.links.size(); j++)
            {
              Link link = (Link) table.links.elementAt(j);
              writeInt(outData, application.getTableNo(link.table.name));
              writeInt(outData, link.list.size());
              writeInt(outData, offsetLinkPairs);
              offsetLinkPairs += link.list.size();
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            writeInt(outData, application.getTableNo(relation.fromTable.name));
            writeInt(outData, relation.fromFields.size());
            writeInt(outData, offsetLinkPairs);
            offsetLinkPairs += relation.fromFields.size();
            writeInt(outData, application.getTableNo(relation.toTable.name));
            writeInt(outData, relation.toFields.size());
            writeInt(outData, offsetLinkPairs);
            offsetLinkPairs += relation.toFields.size();
          }
          writeString(outData, "LINKPAIRS");
          for (int i=0; i<application.tables.size(); i++)
          {
            Table table = (Table) application.tables.elementAt(i);
            for (int j=0; j<table.links.size(); j++)
            {
              Link link = (Link) table.links.elementAt(j);
              Table linkTable = application.getTable(link.table.name);
              for (int k=0; k<link.list.size(); k++)
              {
                String s = (String)link.list.elementAt(k);
                writeInt(outData, table.getFieldNo(s));
                int ono = linkTable.getOrderNo(k);
                if (ono == -1)
                  outLog.println("TABLE "+table.name+" LINKTABLE "+link.table.name+" ERROR");
                writeInt(outData, linkTable.getOrderNo(k));
              }
            }
          }
          for (int i=0; i<application.relations.size(); i++)
          {
            Relation relation = (Relation) application.relations.elementAt(i);
            for (int j=0; j<relation.fromFields.size(); j++)
            {
              Table linkTable = application.getTable(relation.fromTable.name);
              writeInt(outData, j);
              writeInt(outData, linkTable.getOrderNo(j));
            }
            for (int j=0; j<relation.toFields.size(); j++)
            {
              Table linkTable = application.getTable(relation.toTable.name);
              writeInt(outData, j+relation.fromFields.size());
              writeInt(outData, linkTable.getOrderNo(j));
            }
          }
        }
        finally
        {
          writeString(outData, "ENDOFDATA");
          outData.flush();
        }
      }
      finally
      {
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  private static void writeEncode(DataOutputStream outData, String s) throws java.io.IOException
  {
    byte[] APPLICATION = {'4','P','P','7','1','C','4','T','1','0','N'};
    int al = APPLICATION.length;
    int n = s.length();
    byte[] sb = s.getBytes();
    int r = n % 4;
    if (r != 0) r = 4 - r;
    outData.writeInt(n+r);
    byte[] b = new byte[n+r];
    for (int i=0; i < n+r; i++)
    {
      if (i<n) b[i] = sb[i]; 
      outData.writeByte(b[i]^APPLICATION[i%al]);
    }
  }
  private static void writeInt(DataOutputStream outData, int i) throws java.io.IOException
  {
    outData.writeInt(i);
  }
  private static void writeString(DataOutputStream outData, String s) throws java.io.IOException
  {
    int n = s.length();
    int r = n % 4;
    if (r != 0) r = 4 - r;
    outData.writeInt(n+r);
    outData.writeBytes(s);
    for (int i=0; i < r; i++)
      outData.writeByte(0);
  }
  private static int fieldLength(Field field)
  {
    switch (field.type)
    {
    case Field.CHAR:
    case Field.DATE:
    case Field.DATETIME:
    case Field.TIME:
    case Field.TIMESTAMP:
    case Field.USERSTAMP:
      return field.length+1;
    }
    return field.length;
  }
}


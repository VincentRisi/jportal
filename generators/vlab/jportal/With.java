package vlab.jportal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

public class With  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public Table   table;
  public String  name;
  public Vector<Line>  lines;
  public Vector<String>  comments;
  public Vector<String>  options;
  public int start;
  public With()
  {
    name = "";
    lines = new Vector<Line>();
    comments = new Vector<String>();
    options = new Vector<String>();
    start = 0;
  }
  public void reader(DataInputStream ids) throws IOException
  {
    name = ids.readUTF();
    int noOf = ids.readInt();
    for (int i=0; i<noOf; i++)
    {
      String data = ids.readUTF();
      boolean isVar = ids.readBoolean();
      Line value = new Line(data, isVar);
      lines.addElement(value);
    }
    noOf = ids.readInt();
    for (int i=0; i<noOf;i++)
    {
      String value = ids.readUTF();
      comments.addElement(value);
    }
    noOf = ids.readInt();
    for (int i=0; i<noOf;i++)
    {
      String value = ids.readUTF();
      options.addElement(value);
    }
    start           = ids.readInt();
  }
  public void writer(DataOutputStream ods) throws IOException
  {
    ods.writeUTF(name);
    ods.writeInt(lines.size());
    for (int i=0; i<lines.size(); i++)
    {
      Line value = (Line) lines.elementAt(i);
      value.writer(ods);
    }
    ods.writeInt(comments.size());
    for (int i=0; i<comments.size(); i++)
    {
      String value = (String) comments.elementAt(i);
      ods.writeUTF(value);
    }
    ods.writeInt(options.size());
    for (int i=0; i<options.size(); i++)
    {
      String value = (String) options.elementAt(i);
      ods.writeUTF(value);
    }
    ods.writeInt(start);
  }
  /** Folds the first character of name to an upper case character */
  public String upperFirst()
  {
    String f = name.substring(0, 1);
    return f.toUpperCase()+name.substring(1);
  }
  /** Folds the first character of name to an upper case character */
  public String upperFirstOnly()
  {
    String f = name.substring(0, 1);
    return f.toUpperCase();
  }
  /** Folds the first character of name to an lower case character */
  public String lowerFirst()
  {
    String f = name.substring(0, 1);
    return f.toLowerCase()+name.substring(1);
  }
}

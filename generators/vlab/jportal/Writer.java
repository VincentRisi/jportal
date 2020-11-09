///
/// System : Crackle
/// $Source: /main/nedcor/cvsroot/eclipse/workspace/crackle/bbd/crackle/Generator.java,v $
/// $Author: vince $
/// $Date: 2004/08/02 10:10:07 $
/// $Revision: 401.2 $
///

package vlab.jportal;
import java.io.PrintWriter;

public abstract class Writer
{
  protected static PrintWriter writer;
  protected static String format(String fmt, Object... objects)
  {
    return String.format(fmt,  objects);
  }
  protected static void write(String value)
  {
    writer.print(value);
  }
  protected static void write(int no, String value)
  {
    writer.print(indent(no)+value);
  }
  public static void writeln(int no, String value)
  {
    writer.println(indent(no)+value);
  }
  protected static void writeln(String value)
  {
    writeln(0, value);
  }
  protected static void writeln()
  {
    writer.println();
  }
  protected static String indent_string = "                                                                                             ";
  protected static int indent_size = 2;
  protected static String indent(int no)
  {
     int max = indent_string.length();
     int to = no * indent_size;
     if (to > max)
       to = max;
     return indent_string.substring(0,  to);
  }
  protected static PrintWriter logger;
  
  protected static void logln(String line)
  {
    logger.println(line);
  }
  
}

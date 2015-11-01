///
/// System : Crackle
/// $Source: /main/nedcor/cvsroot/eclipse/workspace/crackle/bbd/crackle/Generator.java,v $
/// $Author: vince $
/// $Date: 2004/08/02 10:10:07 $
/// $Revision: 401.2 $
///

package vlab.crackle;
import java.io.PrintWriter;
import java.util.Vector;

public abstract class Generator
{
  public static void generate(Module module, String output, PrintWriter outLog)
  {
  }
  public static String description()
  {
    return "This description should be implemented in the generator";
  }
  public static String documentation()
  {
    return "This documentation should be implemented in the generator";
  }
  public static class Pragma
  {
    String name;
    boolean value;
    String description;
    public Pragma(String name, boolean value, String description)
    {
      this.name = name;
      this.value = value;
      this.description = description;
    }
  }
  protected static Vector<Pragma> pragmaVector;
  /*
   *  Static constructor
   */
  {
    pragmaVector = new Vector<Pragma>();
  }
  public static Vector<Pragma> pragma()
  {
    return pragmaVector;
  }
}

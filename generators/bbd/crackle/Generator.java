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

package bbd.crackle;
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

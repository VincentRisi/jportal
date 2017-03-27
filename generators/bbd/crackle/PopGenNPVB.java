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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

public class PopGenNPVB extends Generator
{
  public static String description()
  {
    return "Generates Name Pipe VB5/6 Code for Name Pipe Access";
  }
  public static String documentation()
  {
    return "Generates Name Pipe VB5/6 Code for Name Pipe Access";
  }
  /**
  * Reads input from stored repository
  */
  public static void main(String args[])
  {
    try
    {
      PrintWriter outLog = new PrintWriter(System.out);
      for (int i = 0; i <args.length; i++)
      {
        outLog.println(args[i]+": Generate ... ");
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(args[i]));
        Module module = (Module)in.readObject();
        in.close();
        generate(module, "", outLog);
      }
      outLog.flush();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  static void doCategories(Module module, Structure structure)
  {
    for (int i = 0; i < structure.fields.size(); i++)
    {
      Field field = (Field) structure.fields.elementAt(i);
      Structure subStruct = field.asStruct(module);
      if (subStruct != null)
      {
        doCategories(module, subStruct);
        for (int j = 0; j < subStruct.categories.size(); j++)
        {
          String category = (String) subStruct.categories.elementAt(j);
          for (int k = 0; k < structure.categories.size(); k++)
          {
            String existing = (String) structure.categories.elementAt(k);
            if (category == existing)
              continue;
          }
          structure.categories.addElement(category);
        }
      }
    }
  }
  static void doCategories(Module module, Prototype prototype)
  {
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field field = (Field) prototype.parameters.elementAt(i);
      Structure structure = field.asStruct(module);
      if (structure != null)
      {
        for (int j = 0; j < structure.categories.size(); j++)
        {
          String category = (String) structure.categories.elementAt(j);
          for (int k = 0; k < prototype.categories.size(); k++)
          {
            String existing = (String) prototype.categories.elementAt(k);
            if (category == existing)
              continue;
          }
          prototype.categories.addElement(category);
        }
      }
    }
  }
  /**
  * Generates
  * - VB Front end interface file
  */
  public static void generate(Module module, String output, PrintWriter outLog)
  {
    outLog.println(module.name+" version "+module.version);
    for (int i = 0; i < module.structures.size(); i++)
    {
      Structure structure = (Structure) module.structures.elementAt(i);
      doCategories(module, structure);
    }
    for (int i = 0; i < module.prototypes.size(); i++)
    {
      Prototype prototype = (Prototype) module.prototypes.elementAt(i);
      doCategories(module, prototype);
    }
    generateVBHeader(module, output, outLog);
  }
  /**
  * Generates the general stuff
  */
  static void generateVBHeader(Module module, String output, PrintWriter outLog)
  {
    try
    {
      outLog.println("Code: "+output+module.name.toLowerCase()+".bas");
      OutputStream outFile = new FileOutputStream(output+module.name.toLowerCase()+".bas");
      PrintWriter outData = new PrintWriter(outFile);
      try
      {
        outData.println("Attribute VB_Name = \""+module.name+"Structs\"");
        outData.println("' This code was generated, do not modify it, modify it at source and regenerate it.");
        outData.println("Option Explicit");
        outData.println();
        outData.println("Public Const "+module.name+"Version = "+module.version);
        outData.println("Public Const "+module.name+"Signature = "+module.signature);
        outData.println();
        for (int i = 0; i < module.tables.size(); i++)
        {
          Table table = (Table) module.tables.elementAt(i);
          for (int j = 0; j < table.messages.size(); j++)
          {
            Message message = (Message) table.messages.elementAt(j);
            outData.println("Public Const "+module.name.toUpperCase()+"_"+table.name.toUpperCase()+"_"+message.name.toUpperCase()+" = "+j);
          }
          outData.println("Public Const "+module.name.toUpperCase()+"_"+table.name.toUpperCase()+"_NOOF = "+table.messages.size());
          outData.println();
        }
        if (module.enumerators.size() > 0)
        {
          for (int i = 0; i < module.enumerators.size(); i++)
          {
            Enumerator entry = (Enumerator) module.enumerators.elementAt(i);
            int enumValue = -1;
            for (int j = 0; j < entry.elements.size(); j++)
            {
              String element = (String) entry.elements.elementAt(j);
              int p = element.indexOf("=");
              if (p > 0)
              {
                Integer value = new Integer(element.substring(p+1).trim());
                enumValue = value.intValue();
              }
              else
              {
                enumValue++;
                element = element + " = " + enumValue;
              }
              outData.println("Public Const "+element);
            }
            outData.println();
          }
        }
        for (int i = 0; i < module.structures.size(); i++)
        {
          Structure structure = (Structure) module.structures.elementAt(i);
          if (structure.codeType != Structure.NORMAL)
            continue;
          if (structure.fields.size() > 0)
          {
            if (structure.categories.size() > 0)
            {
              String w1 = "#if ";
              String w2 = "";
              for (int j = 0; j < structure.categories.size(); j++)
              {
                String category = (String) structure.categories.elementAt(j);
                w2 = w2 + w1 + module.name + category + " = 1";
                w1 = " and ";
              }
              outData.println(w2+" then");
              outData.println("' The code is conditionally out");
              outData.println("#else");
            }
            generateVBHeader(module, structure, outData);
            if (structure.categories.size() > 0)
              outData.println("#endif");
            outData.println();
          }
        }
        outData.println();
        outData.println("Private Declare Function "+module.name+"SnapLogon Lib \""+module.name.toLowerCase()+".dll\" (Handle As Long, ByVal Pipename As String, ByVal Timeout As Long) As Long");
        outData.println();
        outData.println("Private Declare Function "+module.name+"SnapLogoff Lib \""+module.name.toLowerCase()+".dll\" (Handle As Long) As Long");
        outData.println();
        outData.println("Private Declare Sub "+module.name+"SnapErrBuffer Lib \""+module.name.toLowerCase()+".dll\" (ByVal Handle As Long, ByVal Buffer As String, ByVal BufferSize As Long)");
        outData.println();
        outData.println("Private Declare Sub "+module.name+"SnapErrorDesc Lib \""+module.name.toLowerCase()+".dll\" (ByVal ErrorCode As Long, ByVal Buffer As String, ByVal BufferSize As Long)");
        outData.println();
        outData.println("Private Declare Sub "+module.name+"SnapVersion Lib \""+module.name.toLowerCase()+".dll\" (ByVal Buffer As String, ByVal BufferSize As Long)");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.categories.size() > 0)
          {
            String w1 = "#if ";
            String w2 = "";
            for (int j = 0; j < prototype.categories.size(); j++)
            {
              String category = (String) prototype.categories.elementAt(j);
              w2 = w2 + w1 + module.name + category + " = 1";
              w1 = " and ";
            }
            outData.println(w2+" then");
            outData.println("' The code is conditionally out");
            outData.println("#else");
          }
          generateVBHeader(module, prototype, outData);
          if (prototype.categories.size() > 0)
            outData.println("#endif");
          outData.println();
        }
        outData.println("Private fHandle as Long");
        outData.println("Private ErrorCode as Long");
        outData.println("Private fBuffer as String * 512");
        outData.println("Private fBuffer2 as String * 2048");
        outData.println();
        outData.println("Public Function "+module.name+"Handle() As Long");
        outData.println("  If fHandle < 0 Then Err.Raise fHandle, \""+module.name+"\", \"Currently not logged on\"");
        outData.println("  "+module.name+"Handle = fHandle");
        outData.println("End Function");
        outData.println();
        outData.println("Public Function "+module.name+"ErrorDesc() As String");
        outData.println("  "+module.name+"SnapErrorDesc ErrorCode, fBuffer, 512");
        outData.println("  fBuffer2 = \"--O-O--\"");
        outData.println("  If fHandle >= 0 Then "+module.name+"SnapErrBuffer fHandle, fBuffer2, 2048");
        outData.println("  "+module.name+"ErrorDesc = RTrim$(fBuffer) & vbCr & RTrim$(fBuffer2)");
        outData.println("End Function");
        outData.println();
        outData.println("Public Function "+module.name+"VersionDesc() As String");
        outData.println("  "+module.name+"SnapVersion fBuffer, 512");
        outData.println("  "+module.name+"VersionDesc = RTrim$(fBuffer)");
        outData.println("End Function");
        outData.println();
        outData.println("Public Sub "+module.name+"Logon(Pipename As String, Optional Timeout As Long = 150000)");
        outData.println("  ErrorCode = "+module.name+"SnapLogon(fHandle, Pipename, Timeout)");
        outData.println("  If ErrorCode <> 0 Then Err.Raise ErrorCode, \""+module.name+"\", \"Logon to \" & Pipename & \" failed\"");
        outData.println("End Sub");
        outData.println();
        outData.println("Public Sub "+module.name+"Logoff()");
        outData.println("  If fHandle > 0 Then");
        outData.println("    ErrorCode = "+module.name+"SnapLogoff(fHandle)");
        outData.println("    If ErrorCode <> 0 Then Err.Raise ErrorCode, \""+module.name+"\", \"Logoff failed\"");
        outData.println("  End If");
        outData.println("End Sub");
        outData.println();
        for (int i = 0; i < module.prototypes.size(); i++)
        {
          Prototype prototype = (Prototype) module.prototypes.elementAt(i);
          if (prototype.categories.size() > 0)
          {
            String w1 = "#if ";
            String w2 = "";
            for (int j = 0; j < prototype.categories.size(); j++)
            {
              String category = (String) prototype.categories.elementAt(j);
              w2 = w2 + w1 + module.name + category + " = 1";
              w1 = " and ";
            }
            outData.println(w2+" then");
            outData.println("' The code is conditionally out");
            outData.println("#else");
          }
          generateVBRoutine(module, prototype, outData);
          if (prototype.categories.size() > 0)
            outData.println("#endif");
          outData.println();
        }
      }
      finally
      {
        outData.flush();
        outFile.close();
      }
    }
    catch (IOException e1)
    {
      outLog.println("Generate Procs IO Error");
    }
  }
  /**
  * Generates the structures
  */
  static void generateVBHeader(Module module, Structure structure, PrintWriter outData)
  {
    outData.println("Type "+structure.name);
    int relativeOffset = 0;
    int fillerNo = 0;
    for (int i=0; i < structure.fields.size(); i++)
    {
      Field field = (Field) structure.fields.elementAt(i);
      int n = relativeOffset % field.type.paddingSize(true);
      if (n > 0)
      {
        n = field.type.paddingSize(true)-n;
        outData.println("  Filler"+fillerNo+" as String * "+n);
        fillerNo++;
        relativeOffset += n;
      }
      outData.println("  "+field.type.basDef(field.name, false));
      relativeOffset += field.type.relativeSize(true);
    }
    int n = relativeOffset % 8;
    if (n > 0)
    {
      n = 8-n;
      outData.println("  Filler"+fillerNo+" as String * "+n);
    }
    outData.println("End Type");
  }
  /**
  * Generates the prototypes
  */
  static void generateVBHeader(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "", w2 = "";
    Vector<Field> retrievals = new Vector<Field>();
    Vector<Field> submittals = new Vector<Field>();
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      if (parameter.type.typeof != Type.CHAR
      && (parameter.type.reference == Type.BYPTR
      ||  parameter.type.reference == Type.BYREFPTR)
      && (prototype.hasOutputSize(parameter.name)
      ||  prototype.hasInputSize(parameter.name)))
      {
        w1 = "Start";
      }
    }
    outData.print("Private Declare Function "+module.name+"Snap" + prototype.name
                  + w1 + " Lib \"" + module.name.toLowerCase()+".dll\""
                  + " (ByVal Handle As Long, ByVal Signature As Long");
    if (prototype.type.typeof != Type.VOID)
      outData.print(", Result"+prototype.type.basType(false));
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      w1 = "";
      if (parameter.type.typeof == Type.CHAR
      &&  parameter.type.reference == Type.BYPTR)
        w2 = "ByVal ";
      else if (parameter.type.reference == Type.BYPTR
           ||  parameter.type.reference == Type.BYREFPTR)
      {
        w2 = "";
        if (prototype.hasOutputSize(parameter.name))
          retrievals.addElement(parameter);
        if (prototype.hasInputSize(parameter.name))
          submittals.addElement(parameter);
        if (prototype.hasOutputSize(parameter.name)
        ||  prototype.hasInputSize(parameter.name))
          continue;
      }
      else if (parameter.type.reference == Type.BYREF)
        w2 = "<unsupported>";
      else
        w2 = "ByVal ";
      outData.print(", "+w2+parameter.type.basDef(parameter.name+w1, false));
    }
    outData.println(") As Long");
    for (int i = 0; i < submittals.size(); i++)
    {
      Field parameter = (Field) submittals.elementAt(i);
      outData.println();
      outData.println("Private Declare Sub "+module.name+"Snap" + prototype.name
                    + parameter.name + "Prepare Lib \"" + module.name.toLowerCase()+".dll\""
                    + " (ByVal Handle As Long, ByVal Size As Long)");
      outData.println();
      outData.print("Private Declare Function "+module.name+"Snap" + prototype.name
                    + parameter.name + "Fill Lib \"" + module.name.toLowerCase()+".dll\""
                    + " (ByVal Handle As Long");
      outData.print(", "+parameter.type.basDef("Rec", false));
      outData.println(", ByVal Index As Long) As Long");
    }
    for (int i = 0; i < retrievals.size(); i++)
    {
      Field parameter = (Field) retrievals.elementAt(i);
      outData.println();
      outData.print("Private Declare Function "+module.name+"Snap" + prototype.name
                    + parameter.name + "Next Lib \"" + module.name.toLowerCase()+".dll\""
                    + " (ByVal Handle As Long");
      outData.print(", "+parameter.type.basDef("Rec", false));
      outData.println(", ByVal Index As Long) As Long");
      outData.println();
      outData.println("Private Declare Sub "+module.name+"Snap" + prototype.name
                    + parameter.name + "Done Lib \"" + module.name.toLowerCase()+".dll\""
                    + " (ByVal Handle As Long)");
    }
  }
  /**
  * Generates the prototypes
  */
  static void generateVBRoutine(Module module, Prototype prototype, PrintWriter outData)
  {
    String w1 = "", w2 = "", w3 ="";
    boolean hasVBLists = false;
    Vector<Field> retrievals = new Vector<Field>();
    Vector<Field> submittals = new Vector<Field>();
    if (prototype.type.typeof != Type.VOID)
      outData.print("Public Function "+module.name+prototype.name + "(");
    else
      outData.print("Public Sub "+module.name+prototype.name + "(");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      w3 = "";
      if (parameter.type.typeof == Type.CHAR
      &&  parameter.type.reference == Type.BYPTR)
        w2 = "";
      else if (parameter.type.reference == Type.BYPTR
           ||  parameter.type.reference == Type.BYREFPTR)
      {
        w2 = "";
        if (prototype.hasOutputSize(parameter.name))
        {
          hasVBLists = true;
          retrievals.addElement(parameter);
          w3 = "()";
        }
        if (prototype.hasInputSize(parameter.name))
        {
          hasVBLists = true;
          submittals.addElement(parameter);
          w3 = "()";
        }
      }
      else if (parameter.type.reference == Type.BYREF)
        w2 = "<unsupported>";
      else
        w2 = "ByVal ";
      outData.print(w1 + w2 + parameter.type.basDef(parameter.name+w3, false));
      w1 = ", ";
    }
    if (prototype.type.typeof != Type.VOID)
      outData.println(")"+prototype.type.basType(false));
    else
      outData.println(")");
    if (prototype.type.typeof != Type.VOID)
      outData.println("  Dim Result"+prototype.type.basType(false));
    w1 = "";
    if (hasVBLists)
    {
      outData.println("  Dim Index As Long");
      w1 = "Start";
      for (int i = 0; i < submittals.size(); i++)
      {
        Field parameter = (Field) submittals.elementAt(i);
        outData.println("  "+module.name+"Snap"+prototype.name+parameter.name+"Prepare "+module.name+"Handle(), "
                       + prototype.getInputSizeName(parameter.name));
        outData.println("  For Index = 1 To "+prototype.getInputSizeName(parameter.name));
        outData.println("    ErrorCode = "+module.name+"Snap"+prototype.name+parameter.name+"Fill("+module.name+"Handle(), "
                        +parameter.name+"(Index), Index-1)");
        outData.println("  Next Index");
      }
    }
    outData.print("  ErrorCode = "+module.name+"Snap"+prototype.name+w1+"("+module.name+"Handle()");
    outData.print(", "+prototype.signature(true));
    if (prototype.type.typeof != Type.VOID)
      outData.print(", Result");
    for (int i = 0; i < prototype.parameters.size(); i++)
    {
      Field parameter = (Field) prototype.parameters.elementAt(i);
      if ((parameter.type.reference == Type.BYPTR
      ||   parameter.type.reference == Type.BYREFPTR)
      &&  parameter.type.typeof != Type.CHAR
      &&  (prototype.hasOutputSize(parameter.name)
      ||   prototype.hasInputSize(parameter.name)))
        continue;
      outData.print(", " + parameter.name);
    }
    outData.println(")");
    outData.println("  If ErrorCode <> 0 Then Err.Raise ErrorCode, \""+module.name+"\", \""+prototype.name+" failed\"");
    if (hasVBLists)
    {
      for (int i = 0; i < retrievals.size(); i++)
      {
        Field parameter = (Field) retrievals.elementAt(i);
        outData.println("  ReDim "+parameter.name+"(0 To "+prototype.getOutputSizeName(parameter.name)+")");
        outData.println("  For Index = 1 To "+prototype.getOutputSizeName(parameter.name));
        outData.println("    if "+module.name+"Snap"+prototype.name+parameter.name+"Next("+module.name+"Handle(), "
                        +parameter.name+"(Index), Index-1) = 0 Then Exit For");
        outData.println("  Next Index");
        outData.println("  "+module.name+"Snap"+prototype.name+parameter.name+"Done "+module.name+"Handle()");
      }
    }
    if (prototype.type.typeof != Type.VOID)
    {
      outData.println("  "+module.name+prototype.name + " = Result");
      outData.println("End Function");
    }
    else
      outData.println("End Sub");
  }
}

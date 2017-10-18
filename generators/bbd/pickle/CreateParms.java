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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;

public class CreateParms
{
  //private Hashtable<Table, String> tables = new Hashtable();
  public static void main(String args[])
  {
    PrintWriter outLog = new PrintWriter(System.out);
    try
    {
      for (int i=1; i < args.length; i++)
      {
        FileReader fr = new FileReader(args[i]);
        BufferedReader br = new BufferedReader(fr, 50000);
        StreamTokenizer st = new StreamTokenizer(br);
        while (st.nextToken() != StreamTokenizer.TT_EOF)
        {
          switch(st.ttype)
          {
          case StreamTokenizer.TT_EOL:  
            break;
          case StreamTokenizer.TT_NUMBER:  
            break;
          case StreamTokenizer.TT_WORD:  
            break;
          }
        }
      }
      outLog.flush();
    }
    catch (IOException e1)
    {
      outLog.println("IOException");
      outLog.println(e1.toString ());
      outLog.flush();
      e1.printStackTrace();
    }
    catch (Exception e2)
    {
      outLog.println("Exception");
      outLog.println(e2.toString ());
      outLog.flush();
      e2.printStackTrace();
    }
  }
}

/// ------------------------------------------------------------------
/// Copyright (c) from 1996 Vincent Risi
/// All rights reserved.
/// This program and the accompanying materials are made available
/// under the terms of the Common Public License v1.0
/// which accompanies this distribution and is available at
/// http://www.eclipse.org/legal/cpl-v10.html
/// Contributors:
///    Vincent Risi
/// ------------------------------------------------------------------
/// System : JPortal
/// ------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;

namespace bbd.idl2
{
  public class Convert
  {
    private static int FieldLength(FieldInfo info)
    {
      object[] attributes = info.GetCustomAttributes(false);
      for (int i = 0; i < attributes.Length; i++)
      {
        FieldAttribute fa = attributes[i] as FieldAttribute;
        if (fa != null)
          return fa.Size;
      }
      return -1;
    }
    private const string DBTIME = "HHmmss";
    private const string DBDATE = "yyyyMMdd";
    private const string DBDATETIME = "yyyyMMddHHmmss";
    private static int MatchByName(FieldInfo cdInfo, FieldInfo[] jdArray, int pos)
    {
      int no = jdArray.Length;
      for (int i = 0; i < no; i++)
      {
        int j = (pos + i) % no;
        FieldInfo jdInfo = jdArray[j];
        if (cdInfo.Name == jdInfo.Name)
          return j;
      }
      return -1;
    }
    /// <summary>
    /// This routine will convert a java data structure record to a csharp data structure.
    /// </summary>
    /// <typeparam name="T">A Generated java record</typeparam>
    /// <param name="jd">java instance record with data</param>
    /// <param name="cd">csharp instance record to be copied to</param>
    /// <returns></returns>
    public static T AsCSharp<T>(object jd, T cd)
    {
      try
      {
        FieldInfo[] cdArray = cd.GetType().GetFields(
          BindingFlags.Instance |
          BindingFlags.Public
          );
        FieldInfo[] jdArray = jd.GetType().GetFields(
          BindingFlags.Instance |
          BindingFlags.Public
          );
        if (cdArray.Length > jdArray.Length)
          throw new IDL2Exception("Only the java structure can have more elements.");
        int i = 0;
        foreach (FieldInfo cdInfo in cdArray)
        {
          i = MatchByName(cdInfo, jdArray, i);
          if (i == -1)
            throw new IDL2Exception("The two structures do not agree with each other by name.");
          FieldInfo jdInfo = jdArray[i++];
          object data = jdInfo.GetValue(jd);
          if (cdInfo.FieldType == typeof(DateTime))
          {
            string date = (string)data;
            int length = FieldLength(cdInfo);
            if (length == 15)
              cdInfo.SetValue(cd, DateTime.ParseExact(date, DBDATETIME, System.Globalization.DateTimeFormatInfo.CurrentInfo));
            else if (length == 9)
              cdInfo.SetValue(cd, DateTime.ParseExact(date, DBDATE, System.Globalization.DateTimeFormatInfo.CurrentInfo));
            else if (length == 7)
              cdInfo.SetValue(cd, DateTime.ParseExact(date, DBTIME, System.Globalization.DateTimeFormatInfo.CurrentInfo));
          }
          else if (cdInfo.FieldType == typeof(Int16))
          {
            if (jdInfo.FieldType == typeof(SByte))
            {
              sbyte sb = (sbyte)data;
              Int16 i16 = (Int16)sb;
              cdInfo.SetValue(cd, i16);
            }
            else
              cdInfo.SetValue(cd, data);
          }
          else
            cdInfo.SetValue(cd, data);
        }
        return cd;
      }
      catch (ApplicationException ex)
      {
        throw new IDL2Exception("A general application exception on conversion. See inner exception.", ex);
      }
    }
    /// <summary>
    /// This routine will convert a csharp data structure record to a java data structure record.
    /// </summary>
    /// <typeparam name="T">A Generated csharp record</typeparam>
    /// <param name="cd">csharp instance record with data</param>
    /// <param name="jd">java instance record to be copied to</param>
    /// <returns></returns>
    public static T AsJava<T>(object cd, T jd)
    {
      try
      {
      FieldInfo[] cdArray = cd.GetType().GetFields(
        BindingFlags.Instance |
        BindingFlags.Public
        );
      FieldInfo[] jdArray = jd.GetType().GetFields(
        BindingFlags.Instance |
        BindingFlags.Public
        );
      if (cdArray.Length > jdArray.Length)
        throw new IDL2Exception("Only the java structure can have more elements.");
      int i = 0;
      foreach (FieldInfo cdInfo in cdArray)
      {
        i = MatchByName(cdInfo, jdArray, i);
        if (i == -1)
          throw new IDL2Exception("The two structures do not agree with each other by name.");
        FieldInfo jdInfo = jdArray[i++];
        object data = cdInfo.GetValue(cd);
        Type dataType = data.GetType();
        if (dataType == typeof(DateTime))
        {
          int length = FieldLength(cdInfo);
          if (length == 15)
            jdInfo.SetValue(jd, ((DateTime)data).ToString(DBDATETIME));
          else if (length == 9)
            jdInfo.SetValue(jd, ((DateTime)data).ToString(DBDATE));
          else if (length == 7)
            jdInfo.SetValue(jd, ((DateTime)data).ToString(DBTIME));
        }
        else if (dataType == typeof(Int16))
        {
          Int16 i16 = (Int16)data;
          if (jdInfo.FieldType == typeof(SByte))
          {
            sbyte sb = (sbyte)i16;
            jdInfo.SetValue(jd, sb);
          }
          else
            jdInfo.SetValue(jd, data);
        }
        else
          jdInfo.SetValue(jd, data);
      }
      return jd;
      }
      catch (ApplicationException ex)
      {
        throw new IDL2Exception("A general application exception on conversion. See inner exception.", ex);
      }
    }
  }
}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;

namespace bbd.ParamControl
{
  class BinTables
  {
    static internal Encoding encoding = Encoding.GetEncoding("ISO-8859-1");
    internal const uint ValidSignature = 0xBADFACED;
    static public TPCApplication PCApplication = new TPCApplication();
    static internal TPCTable[] PCTables;
    static internal TPCRelation[] PCRelations;
    static internal TPCField[] PCFields;
    static internal TPCEnum[] PCEnums;
    static internal TPCIndexField[] PCKeyFields;
    static internal TPCIndexField[] PCOrderFields;
    static internal TPCIndexField[] PCBreakFields;
    static internal TPCIndexField[] PCShowFields;
    static internal TPCLink[] PCLinks;
    static internal TPCLinkPair[] PCLinkPairs;
    static void ValidateMark(BinaryReader In, String Mark)
    {
      string work = ReadString(In);
      if (Mark != work)
        throw new Exception("Invalid Mark " + Mark + " " + work + " returned.");
    }
    internal static void Allocate<T>(BinaryReader In, out int no, ref T[] array)
    {
      no = ReadInt(In);
      if (no > 0)
        array = new T[no];
      else
        array = null;
    }
    static void LoadApplication(BinaryReader In)
    {
      int Signature = ReadInt(In);
      if ((uint)Signature != ValidSignature)
        throw new Exception("Invalid Configuration Binary File");
      ValidateMark(In, "APPLICATION");
      PCApplication.name = ReadString(In);
      PCApplication.descr = ReadString(In);
      PCApplication.version = ReadString(In);
      PCApplication.server = ReadBytes(In);
      PCApplication.user = ReadBytes(In);
      PCApplication.password = ReadBytes(In);
      PCApplication.registry = ReadString(In);
      Allocate(In, out PCApplication.noTables, ref PCTables);
      Allocate(In, out PCApplication.noRelations, ref PCRelations);
      Allocate(In, out PCApplication.noFields, ref PCFields);
      Allocate(In, out PCApplication.noEnums, ref PCEnums);
      Allocate(In, out PCApplication.noKeyFields, ref PCKeyFields);
      Allocate(In, out PCApplication.noOrderFields, ref PCOrderFields);
      Allocate(In, out PCApplication.noShowFields, ref PCShowFields);
      Allocate(In, out PCApplication.noBreakFields, ref PCBreakFields);
      Allocate(In, out PCApplication.noLinks, ref PCLinks);
      Allocate(In, out PCApplication.noLinkPairs, ref PCLinkPairs);
      PCApplication.validateInit = ReadString(In);
      PCApplication.validateAll = ReadString(In);
      PCApplication.validateOther = ReadString(In);
    }
    static internal void LoadTables(BinaryReader In)
    {
      ValidateMark(In, "TABLES");
      for (int i = 0; i < PCApplication.noTables; i++)
      {
        PCTables[i].index = i;
        PCTables[i].name = ReadString(In);
        PCTables[i].descr = ReadString(In);
        PCTables[i].noFields = ReadInt(In);
        PCTables[i].offsetFields = ReadInt(In);
        PCTables[i].noLinks = ReadInt(In);
        PCTables[i].offsetLinks = ReadInt(In);
        PCTables[i].noKeyFields = ReadInt(In);
        PCTables[i].offsetKeyFields = ReadInt(In);
        PCTables[i].noOrderFields = ReadInt(In);
        PCTables[i].offsetOrderFields = ReadInt(In);
        PCTables[i].noShowFields = ReadInt(In);
        PCTables[i].offsetShowFields = ReadInt(In);
        PCTables[i].noBreakFields = ReadInt(In);
        PCTables[i].offsetBreakFields = ReadInt(In);
        PCTables[i].viewOnly = ReadInt(In);
        PCTables[i].validate = ReadString(In);
      }
    }
    static internal void LoadRelations(BinaryReader In)
    {
      ValidateMark(In, "RELATIONS");
      for (int i = 0; i < PCApplication.noRelations; i++)
      {
        PCRelations[i].index = i;
        PCRelations[i].name = ReadString(In);
        PCRelations[i].descr = ReadString(In);
        PCRelations[i].fromTable = ReadInt(In);
        PCRelations[i].noFromFields = ReadInt(In);
        PCRelations[i].toTable = ReadInt(In);
        PCRelations[i].noToFields = ReadInt(In);
        PCRelations[i].offsetFields = ReadInt(In);
        PCRelations[i].offsetFromLink = ReadInt(In);
        PCRelations[i].offsetToLink = ReadInt(In);
        PCRelations[i].validate = ReadString(In);
      }
    }
    static internal void LoadFields(BinaryReader In)
    {
      ValidateMark(In, "FIELDS");
      for (int i = 0; i < PCApplication.noFields; i++)
      {
        PCFields[i].name = ReadString(In);
        PCFields[i].type = ReadInt(In);
        PCFields[i].length = ReadInt(In);
        PCFields[i].precision = ReadInt(In);
        PCFields[i].scale = ReadInt(In);
        PCFields[i].offset = ReadInt(In);
        PCFields[i].paddedLength = ReadInt(In);
        PCFields[i].uppercase = ReadInt(In);
        PCFields[i].isNull = ReadInt(In);
        PCFields[i].noEnums = ReadInt(In);
        PCFields[i].offsetEnums = ReadInt(In);
      }
    }
    static internal void LoadEnums(BinaryReader In)
    {
      ValidateMark(In, "ENUMS");
      for (int i = 0; i < PCApplication.noEnums; i++)
      {
        PCEnums[i].name = ReadString(In);
        PCEnums[i].value = ReadInt(In);
      }
    }
    static internal void LoadKeyFields(BinaryReader In)
    {
      ValidateMark(In, "KEYFIELDS");
      for (int i = 0; i < PCApplication.noKeyFields; i++)
      {
        PCKeyFields[i].index = ReadInt(In);
      }
    }
    static internal void LoadOrderFields(BinaryReader In)
    {
      ValidateMark(In, "ORDERFIELDS");
      for (int i = 0; i < PCApplication.noOrderFields; i++)
      {
        PCOrderFields[i].index = ReadInt(In);
      }
    }
    static internal void LoadShowFields(BinaryReader In)
    {
      ValidateMark(In, "SHOWFIELDS");
      for (int i = 0; i < PCApplication.noShowFields; i++)
      {
        PCShowFields[i].index = ReadInt(In);
      }
    }
    static internal void LoadBreakFields(BinaryReader In)
    {
      ValidateMark(In, "BREAKFIELDS");
      for (int i = 0; i < PCApplication.noBreakFields; i++)
      {
        PCBreakFields[i].index = ReadInt(In);
      }
    }
    static internal void LoadLinks(BinaryReader In)
    {
      ValidateMark(In, "LINKS");
      for (int i = 0; i < PCApplication.noLinks; i++)
      {
        PCLinks[i].tableNo = ReadInt(In);
        PCLinks[i].noLinkPairs = ReadInt(In);
        PCLinks[i].offsetLinkPairs = ReadInt(In);
      }
    }
    static internal void LoadLinkPairs(BinaryReader In)
    {
      ValidateMark(In, "LINKPAIRS");
      for (int i = 0; i < PCApplication.noLinkPairs; i++)
      {
        PCLinkPairs[i].fromNo = ReadInt(In);
        PCLinkPairs[i].toNo = ReadInt(In);
      }
    }
    static internal int ReadInt(BinaryReader In)
    {
      return
        (In.ReadByte() << 24
        | In.ReadByte() << 16
        | In.ReadByte() << 8
        | In.ReadByte()
        );
    }
    static internal String ReadString(BinaryReader In)
    {
      int length = ReadInt(In);
      byte[] chars = new byte[length];
      chars = In.ReadBytes(length);
      string result = encoding.GetString(chars);
      return result.TrimEnd('\0');
    }
    static internal byte[] ReadBytes(BinaryReader In)
    {
      int length = ReadInt(In);
      byte[] chars = new byte[length];
      chars = In.ReadBytes(length);
      return chars;
    }
    static public void LoadParameters(String BinFilename)
    {
      using (BinaryReader In = new BinaryReader(new FileStream(BinFilename, FileMode.Open)))
      {
        LoadApplication(In);
        LoadTables(In);
        LoadRelations(In);
        LoadFields(In);
        LoadEnums(In);
        LoadKeyFields(In);
        LoadOrderFields(In);
        LoadShowFields(In);
        LoadBreakFields(In);
        LoadLinks(In);
        LoadLinkPairs(In);
        ValidateMark(In, "ENDOFDATA");
      }
    }
    static public void FreeParameters()
    {
      PCTables = null;
      PCRelations = null;
      PCFields = null;
      PCEnums = null;
      PCKeyFields = null;
      PCOrderFields = null;
      PCBreakFields = null;
      PCLinks = null;
      PCLinkPairs = null;
    }
    static public bool HasValidation()
    {
      if (PCApplication.validateAll.Length > 0) return true;
      if (PCApplication.validateOther.Length > 0) return true;
      foreach (TPCTable table in PCTables) if (table.validate.Length > 0) return true; 
      return false;
    }
  }
}

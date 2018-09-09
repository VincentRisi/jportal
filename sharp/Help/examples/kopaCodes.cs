using System;
using System.Collections;
using System.Data;
using Kam.DBRoutines;

namespace Kam.DBRoutines
{
  /// <summary>
  ///  List of availble Kopa Series Codes
  /// </summary>
  [Serializable()]
  public class kopaCodesRec
  {
    public string ecoCode;
    public string setId;
    public string kopaCode;
    public string pageId;
    public string period;
    public string description;
    public int selected;
    public int strategy;
    public int disUsed;
    public string updBy;
    public DateTime updWhen;
  }
  public class kopaCodesSelectedOrd
  {
    public const int No = 0;
    public const int Yes = 1;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case 0: return "No";
      case 1: return "Yes";
      }
      return "<??"+ordinal+"??>";
    }
  }
  public class kopaCodesStrategyOrd
  {
    public const int Update = 0;
    public const int Replace = 1;
    public const int TakeOn = 2;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case 0: return "Update";
      case 1: return "Replace";
      case 2: return "TakeOn";
      }
      return "<??"+ordinal+"??>";
    }
  }
  public class kopaCodesDisUsedOrd
  {
    public const int InUse = 0;
    public const int NotInUse = 1;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case 0: return "InUse";
      case 1: return "NotInUse";
      }
      return "<??"+ordinal+"??>";
    }
  }
  [Serializable()]
  public class kopaCodesDeleteOneRec
  {
    public string ecoCode;
  }
  [Serializable()]
  public class kopaCodesListRec
  {
    public string ecoCode;
    public string pageId;
    public string kopaCode;
    public string period;
    public string description;
    public int disUsed;
    public string setId;
  }
  [Serializable()]
  public class kopaCodesListBySetRec
  {
    public string ecoCode;
    public int selected;
    public int strategy;
    public int disUsed;
    public string description;
    public string setId;
    public string kopaCode;
    public string pageId;
    public string period;
    public string updBy;
    public DateTime updWhen;
  }
  [Serializable()]
  public class kopaCodesUpdateFromKRec
  {
    public string setId;
    public string pageId;
    public string kopaCode;
    public string description;
    public string updBy;
    public DateTime updWhen;
  }
  [Serializable()]
  public class kopaCodesMarkNotUsedRec
  {
    public string setId;
    public string kopaCode;
  }
  [Serializable()]
  public class kopaCodesUpdateSelectRec
  {
    public string setId;
    public string kopaCode;
    public string ecoCode;
    public int selected;
    public int strategy;
  }
  [Serializable()]
  public class kopaCodesBuildSetListRec
  {
    public string ecoCode;
    public int selected;
    public int strategy;
    public string dir;
    public string setId;
    public string kopaCode;
    public string period;
    public int lastNo;
  }
  [Serializable()]
  public class kopaCodesChangeAliasRec
  {
    public string setId;
    public string kopaCode;
    public string ecoCode;
  }
  public class kopaCodesSelectAllDataTable : DataTable
  {
    public const int cEcoCode = 0;
    public const int cSetId = 1;
    public const int cKopaCode = 2;
    public const int cPageId = 3;
    public const int cPeriod = 4;
    public const int cDescription = 5;
    public const int cSelected = 6;
    public const int cStrategy = 7;
    public const int cDisUsed = 8;
    public const int cUpdBy = 9;
    public const int cUpdWhen = 10;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case cEcoCode: return "EcoCode";
      case cSetId: return "SetId";
      case cKopaCode: return "KopaCode";
      case cPageId: return "PageId";
      case cPeriod: return "Period";
      case cDescription: return "Description";
      case cSelected: return "Selected";
      case cStrategy: return "Strategy";
      case cDisUsed: return "DisUsed";
      case cUpdBy: return "UpdBy";
      case cUpdWhen: return "UpdWhen";
      }
      return "<??"+ordinal+"??>";
    }
    public kopaCodesSelectAllDataTable(ArrayList aList)
    : base("kopaCodesSelectAll")
    {
      Columns.Add(new DataColumn("EcoCode", typeof(String)));
      Columns.Add(new DataColumn("SetId", typeof(String)));
      Columns.Add(new DataColumn("KopaCode", typeof(String)));
      Columns.Add(new DataColumn("PageId", typeof(String)));
      Columns.Add(new DataColumn("Period", typeof(String)));
      Columns.Add(new DataColumn("Description", typeof(String)));
      Columns.Add(new DataColumn("Selected", typeof(Int32)));
      Columns.Add(new DataColumn("Strategy", typeof(Int32)));
      Columns.Add(new DataColumn("DisUsed", typeof(Int32)));
      Columns.Add(new DataColumn("UpdBy", typeof(String)));
      Columns.Add(new DataColumn("UpdWhen", typeof(String)));
      foreach (kopaCodesRec wRec in aList)
      {
        DataRow wRow = NewRow();
        wRow[cEcoCode] = wRec.ecoCode;
        wRow[cSetId] = wRec.setId;
        wRow[cKopaCode] = wRec.kopaCode;
        wRow[cPageId] = wRec.pageId;
        wRow[cPeriod] = wRec.period;
        wRow[cDescription] = wRec.description;
        wRow[cSelected] = wRec.selected;
        wRow[cStrategy] = wRec.strategy;
        wRow[cDisUsed] = wRec.disUsed;
        wRow[cUpdBy] = wRec.updBy;
        wRow[cUpdWhen] = wRec.updWhen;
        Rows.Add(wRow);
      }
    }
    public kopaCodesRec this[int row]
    {
      get
      {
        DataRow wRow = Rows[row];
        kopaCodesRec wRec = new kopaCodesRec();
        wRec.ecoCode = (string)wRow[cEcoCode];
        wRec.setId = (string)wRow[cSetId];
        wRec.kopaCode = (string)wRow[cKopaCode];
        wRec.pageId = (string)wRow[cPageId];
        wRec.period = (string)wRow[cPeriod];
        wRec.description = (string)wRow[cDescription];
        wRec.selected = (int)wRow[cSelected];
        wRec.strategy = (int)wRow[cStrategy];
        wRec.disUsed = (int)wRow[cDisUsed];
        wRec.updBy = (string)wRow[cUpdBy];
        wRec.updWhen = (DateTime)wRow[cUpdWhen];
        return wRec;
      }
      set
      {
        DataRow wRow = Rows[row];
        wRow[cEcoCode] = value.ecoCode;
        wRow[cSetId] = value.setId;
        wRow[cKopaCode] = value.kopaCode;
        wRow[cPageId] = value.pageId;
        wRow[cPeriod] = value.period;
        wRow[cDescription] = value.description;
        wRow[cSelected] = value.selected;
        wRow[cStrategy] = value.strategy;
        wRow[cDisUsed] = value.disUsed;
        wRow[cUpdBy] = value.updBy;
        wRow[cUpdWhen] = value.updWhen;
      }
    }
  }
  public class kopaCodesListDataTable : DataTable
  {
    public const int cSetId = 0;
    public const int cPeriod = 1;
    public const int cEcoCode = 2;
    public const int cPageId = 3;
    public const int cKopaCode = 4;
    public const int cDescription = 5;
    public const int cDisUsed = 6;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case cSetId: return "SetId";
      case cPeriod: return "Period";
      case cEcoCode: return "EcoCode";
      case cPageId: return "PageId";
      case cKopaCode: return "KopaCode";
      case cDescription: return "Description";
      case cDisUsed: return "DisUsed";
      }
      return "<??"+ordinal+"??>";
    }
    public kopaCodesListDataTable(ArrayList aList)
    : base("kopaCodesList")
    {
      Columns.Add(new DataColumn("SetId", typeof(String)));
      Columns.Add(new DataColumn("Period", typeof(String)));
      Columns.Add(new DataColumn("EcoCode", typeof(String)));
      Columns.Add(new DataColumn("PageId", typeof(String)));
      Columns.Add(new DataColumn("KopaCode", typeof(String)));
      Columns.Add(new DataColumn("Description", typeof(String)));
      Columns.Add(new DataColumn("DisUsed", typeof(Int32)));
      foreach (kopaCodesListRec wRec in aList)
      {
        DataRow wRow = NewRow();
        wRow[cSetId] = wRec.setId;
        wRow[cPeriod] = wRec.period;
        wRow[cEcoCode] = wRec.ecoCode;
        wRow[cPageId] = wRec.pageId;
        wRow[cKopaCode] = wRec.kopaCode;
        wRow[cDescription] = wRec.description;
        wRow[cDisUsed] = wRec.disUsed;
        Rows.Add(wRow);
      }
    }
    public kopaCodesListRec this[int row]
    {
      get
      {
        DataRow wRow = Rows[row];
        kopaCodesListRec wRec = new kopaCodesListRec();
        wRec.setId = (string)wRow[cSetId];
        wRec.period = (string)wRow[cPeriod];
        wRec.ecoCode = (string)wRow[cEcoCode];
        wRec.pageId = (string)wRow[cPageId];
        wRec.kopaCode = (string)wRow[cKopaCode];
        wRec.description = (string)wRow[cDescription];
        wRec.disUsed = (int)wRow[cDisUsed];
        return wRec;
      }
      set
      {
        DataRow wRow = Rows[row];
        wRow[cSetId] = value.setId;
        wRow[cPeriod] = value.period;
        wRow[cEcoCode] = value.ecoCode;
        wRow[cPageId] = value.pageId;
        wRow[cKopaCode] = value.kopaCode;
        wRow[cDescription] = value.description;
        wRow[cDisUsed] = value.disUsed;
      }
    }
  }
  public class kopaCodesListBySetDataTable : DataTable
  {
    public const int cSetId = 0;
    public const int cPageId = 1;
    public const int cPeriod = 2;
    public const int cEcoCode = 3;
    public const int cSelected = 4;
    public const int cStrategy = 5;
    public const int cDisUsed = 6;
    public const int cDescription = 7;
    public const int cKopaCode = 8;
    public const int cUpdBy = 9;
    public const int cUpdWhen = 10;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case cSetId: return "SetId";
      case cPageId: return "PageId";
      case cPeriod: return "Period";
      case cEcoCode: return "EcoCode";
      case cSelected: return "Selected";
      case cStrategy: return "Strategy";
      case cDisUsed: return "DisUsed";
      case cDescription: return "Description";
      case cKopaCode: return "KopaCode";
      case cUpdBy: return "UpdBy";
      case cUpdWhen: return "UpdWhen";
      }
      return "<??"+ordinal+"??>";
    }
    public kopaCodesListBySetDataTable(ArrayList aList)
    : base("kopaCodesListBySet")
    {
      Columns.Add(new DataColumn("SetId", typeof(String)));
      Columns.Add(new DataColumn("PageId", typeof(String)));
      Columns.Add(new DataColumn("Period", typeof(String)));
      Columns.Add(new DataColumn("EcoCode", typeof(String)));
      Columns.Add(new DataColumn("Selected", typeof(Int32)));
      Columns.Add(new DataColumn("Strategy", typeof(Int32)));
      Columns.Add(new DataColumn("DisUsed", typeof(Int32)));
      Columns.Add(new DataColumn("Description", typeof(String)));
      Columns.Add(new DataColumn("KopaCode", typeof(String)));
      Columns.Add(new DataColumn("UpdBy", typeof(String)));
      Columns.Add(new DataColumn("UpdWhen", typeof(String)));
      foreach (kopaCodesListBySetRec wRec in aList)
      {
        DataRow wRow = NewRow();
        wRow[cSetId] = wRec.setId;
        wRow[cPageId] = wRec.pageId;
        wRow[cPeriod] = wRec.period;
        wRow[cEcoCode] = wRec.ecoCode;
        wRow[cSelected] = wRec.selected;
        wRow[cStrategy] = wRec.strategy;
        wRow[cDisUsed] = wRec.disUsed;
        wRow[cDescription] = wRec.description;
        wRow[cKopaCode] = wRec.kopaCode;
        wRow[cUpdBy] = wRec.updBy;
        wRow[cUpdWhen] = wRec.updWhen;
        Rows.Add(wRow);
      }
    }
    public kopaCodesListBySetRec this[int row]
    {
      get
      {
        DataRow wRow = Rows[row];
        kopaCodesListBySetRec wRec = new kopaCodesListBySetRec();
        wRec.setId = (string)wRow[cSetId];
        wRec.pageId = (string)wRow[cPageId];
        wRec.period = (string)wRow[cPeriod];
        wRec.ecoCode = (string)wRow[cEcoCode];
        wRec.selected = (int)wRow[cSelected];
        wRec.strategy = (int)wRow[cStrategy];
        wRec.disUsed = (int)wRow[cDisUsed];
        wRec.description = (string)wRow[cDescription];
        wRec.kopaCode = (string)wRow[cKopaCode];
        wRec.updBy = (string)wRow[cUpdBy];
        wRec.updWhen = (DateTime)wRow[cUpdWhen];
        return wRec;
      }
      set
      {
        DataRow wRow = Rows[row];
        wRow[cSetId] = value.setId;
        wRow[cPageId] = value.pageId;
        wRow[cPeriod] = value.period;
        wRow[cEcoCode] = value.ecoCode;
        wRow[cSelected] = value.selected;
        wRow[cStrategy] = value.strategy;
        wRow[cDisUsed] = value.disUsed;
        wRow[cDescription] = value.description;
        wRow[cKopaCode] = value.kopaCode;
        wRow[cUpdBy] = value.updBy;
        wRow[cUpdWhen] = value.updWhen;
      }
    }
  }
  public class kopaCodesBuildSetListDataTable : DataTable
  {
    public const int cSetId = 0;
    public const int cEcoCode = 1;
    public const int cSelected = 2;
    public const int cStrategy = 3;
    public const int cDir = 4;
    public const int cKopaCode = 5;
    public const int cPeriod = 6;
    public const int cLastNo = 7;
    public static string ToString(int ordinal)
    {
      switch (ordinal)
      {
      case cSetId: return "SetId";
      case cEcoCode: return "EcoCode";
      case cSelected: return "Selected";
      case cStrategy: return "Strategy";
      case cDir: return "Dir";
      case cKopaCode: return "KopaCode";
      case cPeriod: return "Period";
      case cLastNo: return "LastNo";
      }
      return "<??"+ordinal+"??>";
    }
    public kopaCodesBuildSetListDataTable(ArrayList aList)
    : base("kopaCodesBuildSetList")
    {
      Columns.Add(new DataColumn("SetId", typeof(String)));
      Columns.Add(new DataColumn("EcoCode", typeof(String)));
      Columns.Add(new DataColumn("Selected", typeof(Int32)));
      Columns.Add(new DataColumn("Strategy", typeof(Int32)));
      Columns.Add(new DataColumn("Dir", typeof(String)));
      Columns.Add(new DataColumn("KopaCode", typeof(String)));
      Columns.Add(new DataColumn("Period", typeof(String)));
      Columns.Add(new DataColumn("LastNo", typeof(Int32)));
      foreach (kopaCodesBuildSetListRec wRec in aList)
      {
        DataRow wRow = NewRow();
        wRow[cSetId] = wRec.setId;
        wRow[cEcoCode] = wRec.ecoCode;
        wRow[cSelected] = wRec.selected;
        wRow[cStrategy] = wRec.strategy;
        wRow[cDir] = wRec.dir;
        wRow[cKopaCode] = wRec.kopaCode;
        wRow[cPeriod] = wRec.period;
        wRow[cLastNo] = wRec.lastNo;
        Rows.Add(wRow);
      }
    }
    public kopaCodesBuildSetListRec this[int row]
    {
      get
      {
        DataRow wRow = Rows[row];
        kopaCodesBuildSetListRec wRec = new kopaCodesBuildSetListRec();
        wRec.setId = (string)wRow[cSetId];
        wRec.ecoCode = (string)wRow[cEcoCode];
        wRec.selected = (int)wRow[cSelected];
        wRec.strategy = (int)wRow[cStrategy];
        wRec.dir = (string)wRow[cDir];
        wRec.kopaCode = (string)wRow[cKopaCode];
        wRec.period = (string)wRow[cPeriod];
        wRec.lastNo = (int)wRow[cLastNo];
        return wRec;
      }
      set
      {
        DataRow wRow = Rows[row];
        wRow[cSetId] = value.setId;
        wRow[cEcoCode] = value.ecoCode;
        wRow[cSelected] = value.selected;
        wRow[cStrategy] = value.strategy;
        wRow[cDir] = value.dir;
        wRow[cKopaCode] = value.kopaCode;
        wRow[cPeriod] = value.period;
        wRow[cLastNo] = value.lastNo;
      }
    }
  }
  [Serializable()]
  public class kopaCodes
  {
    private kopaCodesRec mRec;
    public kopaCodesRec Rec { get { return mRec; } set { mRec = value; } }
    private ArrayList mList;
    public int Count { get { return mList.Count; } }
    public Cursor mCursor;
    public kopaCodesRec this[int i]
    {
      get
      {
        if (i < mList.Count)
          return (kopaCodesRec)mList[i];
        return null;
      }
      set
      {
        if (i < mList.Count)
          mList.RemoveAt(i);
        mList.Insert(i, value);
      }
    }
    public void Clear()
    {
      mList = new ArrayList();
      mRec = new kopaCodesRec();
    }
    public kopaCodes()
    {
      Clear();
    }
    public string CommandInsert
    {
      // insert into kopaCodes
      // ( ecoCode
      // , setId
      // , kopaCode
      // , pageId
      // , period
      // , description
      // , selected
      // , strategy
      // , disUsed
      // , updBy
      // , updWhen
      // ) 
      // values
      //  (@P0
      // , @P1
      // , @P2
      // , @P3
      // , @P4
      // , @P5
      // , @P6
      // , @P7
      // , @P8
      // , @P9
      // , @P10
      // )
      get {return "kopaCodesInsert";}
    }
    public void Insert(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandInsert);
      wCursor.Parameter(0, mRec.ecoCode);
      wCursor.Parameter(1, mRec.setId);
      wCursor.Parameter(2, mRec.kopaCode);
      wCursor.Parameter(3, mRec.pageId);
      wCursor.Parameter(4, mRec.period);
      wCursor.Parameter(5, mRec.description);
      wCursor.Parameter(6, mRec.selected);
      wCursor.Parameter(7, mRec.strategy);
      wCursor.Parameter(8, mRec.disUsed);
      wCursor.Parameter(9, mRec.updBy);
      wCursor.Parameter(10, mRec.updWhen);
      wCursor.Exec();
    }
    public string CommandSelectOne
    {
      // select
      //   setId
      // , kopaCode
      // , pageId
      // , period
      // , description
      // , selected
      // , strategy
      // , disUsed
      // , updBy
      // , updWhen
      //  from kopaCodes
      //  where ecoCode = @P0
      get {return "kopaCodesSelectOne";}
    }
    public bool SelectOne(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandSelectOne);
      wCursor.Parameter(0, mRec.ecoCode);
      wCursor.Run();
      bool wResult = (wCursor.HasReader() && wCursor.Read());
      if (wResult == true)
      {
        mRec.setId = wCursor.GetString(0);
        mRec.kopaCode = wCursor.GetString(1);
        mRec.pageId = wCursor.GetString(2);
        mRec.period = wCursor.GetString(3);
        mRec.description = wCursor.GetString(4);
        mRec.selected = wCursor.GetInt(5);
        mRec.strategy = wCursor.GetInt(6);
        mRec.disUsed = wCursor.GetInt(7);
        mRec.updBy = wCursor.GetString(8);
        mRec.updWhen = wCursor.GetDateTime(9);
      }
      if (wCursor.HasReader())
        wCursor.Close();
      return wResult;
    }
    public string CommandUpdate
    {
      // update kopaCodes
      //  set
      //   setId = @P0
      // , kopaCode = @P1
      // , pageId = @P2
      // , period = @P3
      // , description = @P4
      // , selected = @P5
      // , strategy = @P6
      // , disUsed = @P7
      // , updBy = @P8
      // , updWhen = @P9
      //  where ecoCode = @P10
      get {return "kopaCodesUpdate";}
    }
    public void Update(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandUpdate);
      wCursor.Parameter(0, mRec.setId);
      wCursor.Parameter(1, mRec.kopaCode);
      wCursor.Parameter(2, mRec.pageId);
      wCursor.Parameter(3, mRec.period);
      wCursor.Parameter(4, mRec.description);
      wCursor.Parameter(5, mRec.selected);
      wCursor.Parameter(6, mRec.strategy);
      wCursor.Parameter(7, mRec.disUsed);
      wCursor.Parameter(8, mRec.updBy);
      wCursor.Parameter(9, mRec.updWhen);
      wCursor.Parameter(10, mRec.ecoCode);
      wCursor.Exec();
    }
    public string CommandSelectAll
    {
      // select
      //   ecoCode
      // , setId
      // , kopaCode
      // , pageId
      // , period
      // , description
      // , selected
      // , strategy
      // , disUsed
      // , updBy
      // , updWhen
      //  from kopaCodes
      get {return "kopaCodesSelectAll";}
    }
    public void SelectAll(Connect aConnect)
    {
      mCursor = new Cursor(aConnect);
      mCursor.Procedure(CommandSelectAll);
      mCursor.Run();
    }
    public bool SelectAllFetch()
    {
      bool wResult = (mCursor.HasReader() && mCursor.Read());
      if (wResult == true)
      {
        mRec.ecoCode = mCursor.GetString(0);
        mRec.setId = mCursor.GetString(1);
        mRec.kopaCode = mCursor.GetString(2);
        mRec.pageId = mCursor.GetString(3);
        mRec.period = mCursor.GetString(4);
        mRec.description = mCursor.GetString(5);
        mRec.selected = mCursor.GetInt(6);
        mRec.strategy = mCursor.GetInt(7);
        mRec.disUsed = mCursor.GetInt(8);
        mRec.updBy = mCursor.GetString(9);
        mRec.updWhen = mCursor.GetDateTime(10);
      }
      else if (mCursor.HasReader())
        mCursor.Close();
      return wResult;
    }
    public void SelectAllLoad(Connect aConnect)
    {
      SelectAll(aConnect);
      while (SelectAllFetch())
      {
        mList.Add(mRec);
        mRec = new kopaCodesRec();
      }
    }
    public ArrayList Loaded { get { return mList; } }
    public class SelectAllOrd
    {
      public const int ecoCode = 0;
      public const int setId = 1;
      public const int kopaCode = 2;
      public const int pageId = 3;
      public const int period = 4;
      public const int description = 5;
      public const int selected = 6;
      public const int strategy = 7;
      public const int disUsed = 8;
      public const int updBy = 9;
      public const int updWhen = 10;
      public static string ToString(int ordinal)
      {
        switch (ordinal)
        {
        case 0: return "ecoCode";
        case 1: return "setId";
        case 2: return "kopaCode";
        case 3: return "pageId";
        case 4: return "period";
        case 5: return "description";
        case 6: return "selected";
        case 7: return "strategy";
        case 8: return "disUsed";
        case 9: return "updBy";
        case 10: return "updWhen";
        }
        return "<??"+ordinal+"??>";
      }
    }
    public kopaCodesSelectAllDataTable SelectAllDataTable()
    {
      kopaCodesSelectAllDataTable wResult = new kopaCodesSelectAllDataTable(mList);
      return wResult;
    }
    public kopaCodesSelectAllDataTable SelectAllDataTable(Connect aConnect)
    {
      SelectAllLoad(aConnect);
      return SelectAllDataTable();
    }
  }
  [Serializable()]
  public class kopaCodesDeleteOne
  {
    private kopaCodesDeleteOneRec mRec;
    public kopaCodesDeleteOneRec Rec { get { return mRec; } set { mRec = value; } }
    public void Clear()
    {
      mRec = new kopaCodesDeleteOneRec();
    }
    public kopaCodesDeleteOne()
    {
      Clear();
    }
    public string CommandDeleteOne
    {
      // delete from kopaCodes
      //  where ecoCode = @P0
      get {return "kopaCodesDeleteOne";}
    }
    public void DeleteOne(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandDeleteOne);
      wCursor.Parameter(0, mRec.ecoCode);
      wCursor.Exec();
    }
  }
  [Serializable()]
  public class kopaCodesList
  {
    private kopaCodesListRec mRec;
    public kopaCodesListRec Rec { get { return mRec; } set { mRec = value; } }
    private ArrayList mList;
    public int Count { get { return mList.Count; } }
    public Cursor mCursor;
    public kopaCodesListRec this[int i]
    {
      get
      {
        if (i < mList.Count)
          return (kopaCodesListRec)mList[i];
        return null;
      }
      set
      {
        if (i < mList.Count)
          mList.RemoveAt(i);
        mList.Insert(i, value);
      }
    }
    public void Clear()
    {
      mList = new ArrayList();
      mRec = new kopaCodesListRec();
    }
    public kopaCodesList()
    {
      Clear();
    }
    public string CommandList
    {
      // SELECT ecoCode, pageId, kopaCode, period, description, disUsed 
      // FROM kopaCodes 
      // WHERE setId = @P0 
      // AND   period = @P1 
      // ORDER BY kopaCode 
      get {return "kopaCodesList";}
    }
    public void List(Connect aConnect)
    {
      mCursor = new Cursor(aConnect);
      mCursor.Procedure(CommandList);
      mCursor.Parameter(0, mRec.setId);
      mCursor.Parameter(1, mRec.period);
      mCursor.Run();
    }
    public bool ListFetch()
    {
      bool wResult = (mCursor.HasReader() && mCursor.Read());
      if (wResult == true)
      {
        mRec.ecoCode = mCursor.GetString(0);
        mRec.pageId = mCursor.GetString(1);
        mRec.kopaCode = mCursor.GetString(2);
        mRec.period = mCursor.GetString(3);
        mRec.description = mCursor.GetString(4);
        mRec.disUsed = mCursor.GetInt(5);
      }
      else if (mCursor.HasReader())
        mCursor.Close();
      return wResult;
    }
    public void ListLoad(Connect aConnect)
    {
      List(aConnect);
      while (ListFetch())
      {
        mList.Add(mRec);
        mRec = new kopaCodesListRec();
      }
    }
    public ArrayList Loaded { get { return mList; } }
    public class ListOrd
    {
      public const int setId = 0;
      public const int period = 1;
      public const int ecoCode = 2;
      public const int pageId = 3;
      public const int kopaCode = 4;
      public const int description = 5;
      public const int disUsed = 6;
      public static string ToString(int ordinal)
      {
        switch (ordinal)
        {
        case 0: return "setId";
        case 1: return "period";
        case 2: return "ecoCode";
        case 3: return "pageId";
        case 4: return "kopaCode";
        case 5: return "description";
        case 6: return "disUsed";
        }
        return "<??"+ordinal+"??>";
      }
    }
    public kopaCodesListDataTable ListDataTable()
    {
      kopaCodesListDataTable wResult = new kopaCodesListDataTable(mList);
      return wResult;
    }
    public kopaCodesListDataTable ListDataTable(Connect aConnect)
    {
      ListLoad(aConnect);
      return ListDataTable();
    }
  }
  [Serializable()]
  public class kopaCodesListBySet
  {
    private kopaCodesListBySetRec mRec;
    public kopaCodesListBySetRec Rec { get { return mRec; } set { mRec = value; } }
    private ArrayList mList;
    public int Count { get { return mList.Count; } }
    public Cursor mCursor;
    public kopaCodesListBySetRec this[int i]
    {
      get
      {
        if (i < mList.Count)
          return (kopaCodesListBySetRec)mList[i];
        return null;
      }
      set
      {
        if (i < mList.Count)
          mList.RemoveAt(i);
        mList.Insert(i, value);
      }
    }
    public void Clear()
    {
      mList = new ArrayList();
      mRec = new kopaCodesListBySetRec();
    }
    public kopaCodesListBySet()
    {
      Clear();
    }
    public string CommandListBySet
    {
      // SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period, updBy, updWhen 
      // FROM kopaCodes 
      // WHERE setId = @P0 
      // AND  pageId = @P1 
      // AND  period = @P2 
      // ORDER BY ecoCode 
      get {return "kopaCodesListBySet";}
    }
    public void ListBySet(Connect aConnect)
    {
      mCursor = new Cursor(aConnect);
      mCursor.Procedure(CommandListBySet);
      mCursor.Parameter(0, mRec.setId);
      mCursor.Parameter(1, mRec.pageId);
      mCursor.Parameter(2, mRec.period);
      mCursor.Run();
    }
    public bool ListBySetFetch()
    {
      bool wResult = (mCursor.HasReader() && mCursor.Read());
      if (wResult == true)
      {
        mRec.ecoCode = mCursor.GetString(0);
        mRec.selected = mCursor.GetInt(1);
        mRec.strategy = mCursor.GetInt(2);
        mRec.disUsed = mCursor.GetInt(3);
        mRec.description = mCursor.GetString(4);
        mRec.setId = mCursor.GetString(5);
        mRec.kopaCode = mCursor.GetString(6);
        mRec.pageId = mCursor.GetString(7);
        mRec.period = mCursor.GetString(8);
        mRec.updBy = mCursor.GetString(9);
        mRec.updWhen = mCursor.GetDateTime(10);
      }
      else if (mCursor.HasReader())
        mCursor.Close();
      return wResult;
    }
    public void ListBySetLoad(Connect aConnect)
    {
      ListBySet(aConnect);
      while (ListBySetFetch())
      {
        mList.Add(mRec);
        mRec = new kopaCodesListBySetRec();
      }
    }
    public ArrayList Loaded { get { return mList; } }
    public class ListBySetOrd
    {
      public const int setId = 0;
      public const int pageId = 1;
      public const int period = 2;
      public const int ecoCode = 3;
      public const int selected = 4;
      public const int strategy = 5;
      public const int disUsed = 6;
      public const int description = 7;
      public const int kopaCode = 8;
      public const int updBy = 9;
      public const int updWhen = 10;
      public static string ToString(int ordinal)
      {
        switch (ordinal)
        {
        case 0: return "setId";
        case 1: return "pageId";
        case 2: return "period";
        case 3: return "ecoCode";
        case 4: return "selected";
        case 5: return "strategy";
        case 6: return "disUsed";
        case 7: return "description";
        case 8: return "kopaCode";
        case 9: return "updBy";
        case 10: return "updWhen";
        }
        return "<??"+ordinal+"??>";
      }
    }
    public kopaCodesListBySetDataTable ListBySetDataTable()
    {
      kopaCodesListBySetDataTable wResult = new kopaCodesListBySetDataTable(mList);
      return wResult;
    }
    public kopaCodesListBySetDataTable ListBySetDataTable(Connect aConnect)
    {
      ListBySetLoad(aConnect);
      return ListBySetDataTable();
    }
  }
  [Serializable()]
  public class kopaCodesUpdateFromK
  {
    private kopaCodesUpdateFromKRec mRec;
    public kopaCodesUpdateFromKRec Rec { get { return mRec; } set { mRec = value; } }
    public void Clear()
    {
      mRec = new kopaCodesUpdateFromKRec();
    }
    public kopaCodesUpdateFromK()
    {
      Clear();
    }
    public string CommandUpdateFromK
    {
      // UPDATE kopaCodes 
      // SET pageId = @P0 
      // , description = @P1 
      // , disUsed = 0 
      // WHERE setId = @P2 
      // AND   kopaCode = @P3 
      get {return "kopaCodesUpdateFromK";}
    }
    public void UpdateFromK(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandUpdateFromK);
      wCursor.Parameter(0, mRec.pageId);
      wCursor.Parameter(1, mRec.description);
      wCursor.Parameter(2, mRec.setId);
      wCursor.Parameter(3, mRec.kopaCode);
      wCursor.Exec();
    }
  }
  [Serializable()]
  public class kopaCodesMarkNotUsed
  {
    private kopaCodesMarkNotUsedRec mRec;
    public kopaCodesMarkNotUsedRec Rec { get { return mRec; } set { mRec = value; } }
    public void Clear()
    {
      mRec = new kopaCodesMarkNotUsedRec();
    }
    public kopaCodesMarkNotUsed()
    {
      Clear();
    }
    public string CommandMarkNotUsed
    {
      // UPDATE kopaCodes 
      // SET disUsed = 1 
      // WHERE setId = @P0 
      // AND   kopaCode = @P1 
      get {return "kopaCodesMarkNotUsed";}
    }
    public void MarkNotUsed(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandMarkNotUsed);
      wCursor.Parameter(0, mRec.setId);
      wCursor.Parameter(1, mRec.kopaCode);
      wCursor.Exec();
    }
  }
  [Serializable()]
  public class kopaCodesUpdateSelect
  {
    private kopaCodesUpdateSelectRec mRec;
    public kopaCodesUpdateSelectRec Rec { get { return mRec; } set { mRec = value; } }
    public void Clear()
    {
      mRec = new kopaCodesUpdateSelectRec();
    }
    public kopaCodesUpdateSelect()
    {
      Clear();
    }
    public string CommandUpdateSelect
    {
      // UPDATE kopaCodes 
      // SET ecoCode  = @P0 
      // , selected = @P1 
      // , strategy = @P2 
      // WHERE setId  = @P3 
      // AND kopaCode = @P4 
      get {return "kopaCodesUpdateSelect";}
    }
    public void UpdateSelect(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandUpdateSelect);
      wCursor.Parameter(0, mRec.ecoCode);
      wCursor.Parameter(1, mRec.selected);
      wCursor.Parameter(2, mRec.strategy);
      wCursor.Parameter(3, mRec.setId);
      wCursor.Parameter(4, mRec.kopaCode);
      wCursor.Exec();
    }
  }
  [Serializable()]
  public class kopaCodesBuildSetList
  {
    private kopaCodesBuildSetListRec mRec;
    public kopaCodesBuildSetListRec Rec { get { return mRec; } set { mRec = value; } }
    private ArrayList mList;
    public int Count { get { return mList.Count; } }
    public Cursor mCursor;
    public kopaCodesBuildSetListRec this[int i]
    {
      get
      {
        if (i < mList.Count)
          return (kopaCodesBuildSetListRec)mList[i];
        return null;
      }
      set
      {
        if (i < mList.Count)
          mList.RemoveAt(i);
        mList.Insert(i, value);
      }
    }
    public void Clear()
    {
      mList = new ArrayList();
      mRec = new kopaCodesBuildSetListRec();
    }
    public kopaCodesBuildSetList()
    {
      Clear();
    }
    public string CommandBuildSetList
    {
      // SELECT c.ecoCode, c.selected, c.strategy, s.dir, c.setId, c.kopaCode, c.period, s.lastNo 
      // FROM kopaCodes c, KopaSets s 
      // WHERE c.selected > 0 
      // AND   (s.setId = @P0 or @P1 = '**') 
      // AND   c.setId = s.SetId 
      get {return "kopaCodesBuildSetList";}
    }
    public void BuildSetList(Connect aConnect)
    {
      mCursor = new Cursor(aConnect);
      mCursor.Procedure(CommandBuildSetList);
      mCursor.Parameter(0, mRec.setId);
      mCursor.Parameter(1, mRec.setId);
      mCursor.Run();
    }
    public bool BuildSetListFetch()
    {
      bool wResult = (mCursor.HasReader() && mCursor.Read());
      if (wResult == true)
      {
        mRec.ecoCode = mCursor.GetString(0);
        mRec.selected = mCursor.GetInt(1);
        mRec.strategy = mCursor.GetInt(2);
        mRec.dir = mCursor.GetString(3);
        mRec.setId = mCursor.GetString(4);
        mRec.kopaCode = mCursor.GetString(5);
        mRec.period = mCursor.GetString(6);
        mRec.lastNo = mCursor.GetInt(7);
      }
      else if (mCursor.HasReader())
        mCursor.Close();
      return wResult;
    }
    public void BuildSetListLoad(Connect aConnect)
    {
      BuildSetList(aConnect);
      while (BuildSetListFetch())
      {
        mList.Add(mRec);
        mRec = new kopaCodesBuildSetListRec();
      }
    }
    public ArrayList Loaded { get { return mList; } }
    public class BuildSetListOrd
    {
      public const int setId = 0;
      public const int ecoCode = 1;
      public const int selected = 2;
      public const int strategy = 3;
      public const int dir = 4;
      public const int kopaCode = 5;
      public const int period = 6;
      public const int lastNo = 7;
      public static string ToString(int ordinal)
      {
        switch (ordinal)
        {
        case 0: return "setId";
        case 1: return "ecoCode";
        case 2: return "selected";
        case 3: return "strategy";
        case 4: return "dir";
        case 5: return "kopaCode";
        case 6: return "period";
        case 7: return "lastNo";
        }
        return "<??"+ordinal+"??>";
      }
    }
    public kopaCodesBuildSetListDataTable BuildSetListDataTable()
    {
      kopaCodesBuildSetListDataTable wResult = new kopaCodesBuildSetListDataTable(mList);
      return wResult;
    }
    public kopaCodesBuildSetListDataTable BuildSetListDataTable(Connect aConnect)
    {
      BuildSetListLoad(aConnect);
      return BuildSetListDataTable();
    }
  }
  [Serializable()]
  public class kopaCodesChangeAlias
  {
    private kopaCodesChangeAliasRec mRec;
    public kopaCodesChangeAliasRec Rec { get { return mRec; } set { mRec = value; } }
    public void Clear()
    {
      mRec = new kopaCodesChangeAliasRec();
    }
    public kopaCodesChangeAlias()
    {
      Clear();
    }
    public string CommandChangeAlias
    {
      // update kopaCodes set ecoCode = @P0 
      // where  setId = @P1 
      // and    kopaCode = @P2 
      get {return "kopaCodesChangeAlias";}
    }
    public void ChangeAlias(Connect aConnect)
    {
      Cursor wCursor = new Cursor(aConnect);
      wCursor.Procedure(CommandChangeAlias);
      wCursor.Parameter(0, mRec.ecoCode);
      wCursor.Parameter(1, mRec.setId);
      wCursor.Parameter(2, mRec.kopaCode);
      wCursor.Exec();
    }
  }
}

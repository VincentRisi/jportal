# MsSql CSharp Example 

## SI File

    DATABASE AnyDB FLAGS "mssql storedprocs"
    PACKAGE  Kam.DBRoutines
    OUTPUT   kopaCodes
    SERVER   Kopa

    TABLE kopaCodes           ** List of available Kopa Series Codes
      ecoCode       char 24   ** The Normalised Economic Time Series Code
      setId         char 2    ** The two character selector code
      kopaCode      char 24   ** The Kopa code used for time series
      pageId        char 24   ** They seem to have page type selection 
      period        char      ** D=Daily W=Weekly M=Monthly Q=Quarterly A=Annually S=SemiAnnually
      description   char 250  ** Qualifying Description of Series
      selected      int       (No=0 Yes=1)
      strategy      int       (L'Update'=0 Replace=1 TakeOn=2)
      disUsed       int       (InUse=0 NotInUse=1)
      updBy         char 16   
      updWhen       timestamp

    KEY kopaCodesPK PRIMARY
      ecoCode
    
    KEY kopaCodesUK UNIQUE
      setId
      kopaCode

    PROC Insert
    PROC SelectOne
    PROC Update
    PROC DeleteOne
    PROC SelectAll

    PROC List
    INPUT
      setId           char 2    ** The two character selector code
      period          char      ** D=Daily W=Weekly M=Monthly Q=Quarterly S=SemiAnnually A=Annually 
    OUTPUT
      ecoCode         char 24   ** The Normalised Economic Time Series Code
      pageId          char 24   ** They seem to have page type selection 
      kopaCode        char 24   ** The code used for time series
      period          char      ** D=Daily W=Weekly M=Monthly Q=Quarterly S=SemiAnnually A=Annually 
      description     char 250  ** Qualifying Description of Series
      disUsed         int       
    SQLCODE
      SELECT ecoCode, pageId, kopaCode, period, description, disUsed
      FROM kopaCodes
      WHERE setId = :setId
      AND   period = :period
      ORDER BY kopaCode
    ENDCODE

    PROC ListBySet
    INPUT
      setId         char 2    ** The two character selector code
      pageId        char 24   ** They seem to have page type selection 
      period        char      ** D=Daily W=Weekly M=Monthly Q=Quarterly A=Annually S=???
    OUTPUT
      ecoCode       char 24   ** The code used for time series
      selected      int       
      strategy      int       
      disUsed       int       
      description   char 250  ** Qualifying Description of Series
      setId         char 2    ** The two character selector code
      kopaCode      char 24   ** The code used for time series
      pageId        char 24   ** They seem to have page type selection 
      period        char      ** D=Daily W=Weekly M=Monthly Q=Quarterly A=Annually S=???
      updBy         char 16   
      updWhen       timestamp
    SQLCODE
      SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period,     updBy, updWhen    
      FROM kopaCodes    
      WHERE setId =     :setId    
      AND  pageId =     :pageId    
      AND  period = :period    
      ORDER BY ecoCode
    ENDCODE

    PROC UpdateFromK
    INPUT
      setId           char 2    ** The two character selector code
      pageId          char 24   ** They seem to have page type selection 
      kopaCode        char 24   ** The code used for time series
      description     char 250  ** Qualifying Description of Series
      updBy           char 16   
      updWhen         timestamp
    SQLCODE
      UPDATE kopaCodes 
      SET pageId = :pageId
        , description = :description
      , disUsed = 0
      WHERE setId = :setId
      AND   kopaCode = :kopaCode
    ENDCODE

    PROC MarkNotUsed
    INPUT
      setId           char 2    ** The two character selector code
      kopaCode        char 24   ** The code used for time series
    SQLCODE
      UPDATE kopaCodes 
      SET disUsed = 1
      WHERE setId = :setId
      AND   kopaCode = :kopaCode
    ENDCODE

    PROC UpdateSelect
    INPUT
      setId         char 2    ** The two character selector code
      kopaCode      char 24   ** The Kopa code used for time series
      ecoCode       char 24   ** The normalised code used for time series
      selected      int       
      strategy      int       
    SQLCODE
      UPDATE kopaCodes 
      SET ecoCode  = :ecoCode
        , selected = :selected
        , strategy = :strategy
      WHERE setId  = :setId
      AND kopaCode = :kopaCode
    ENDCODE

    PROC BuildSetList
    INPUT
      setId         char 2    ** The two character selector code
    OUTPUT
      ecoCode       char 24   ** The normalised code used for time series
      selected      int       
      strategy      int       
      dir           char 16   ** Directory on CD where the Data is found
      setId         char 2    ** The two character selector code
      kopaCode      char 24   ** The Kopa code used for time series
      period        char      ** Period for update
      lastNo        integer   ** Set no to be used for update
    SQLCODE
      SELECT c.ecoCode, c.selected, c.strategy, s.dir, c.setId, c.kopaCode, c.period, s.lastNo
      FROM kopaCodes c, KopaSets s
      WHERE c.selected > 0
      AND   (s.setId = :setId or :setId = '**')
      AND   c.setId = s.SetId
    ENDCODE

    PROC ChangeAlias
    INPUT
      setId         char 2    ** The two character selector code
      kopaCode      char 24   ** The Kopa code used for time series
      ecoCode       char 24   ** This code is unique here and will be unique if present in kopaCodes     on JohnnyDB    
    SQLCODE    
      updat    e kopaCodes set ecoCode = :ecoCode     
      where  setId = :setId     
      and    kopaCode = :kopaCode
    ENDCODE

## Generated CS File

    using System;
    using System.Collections;
    using System.Data;
    using Kam.DBRoutines;

    namespace Kam.DBRoutines
    {
      ///  List of available Kopa Series Codes
  
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
          return "";
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
          return "";
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
          return "";
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
          return "";
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
          return "";
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
          return "";
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
          return "";
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
            return "";
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
            return "";
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
            return "";
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
            return "";
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

## Generated DDL

    use AnyDB

    drop table kopaCodes
    go

    create table kopaCodes
    (
      ecoCode varchar(24) not null
    , setId varchar(2) not null
    , kopaCode varchar(24) not null
    , pageId varchar(24) not null
    , period varchar(1) not null
    , description varchar(250) not null
    , selected integer not null
    , strategy integer not null
    , disUsed integer not null
    , updBy varchar(16) not null
    , updWhen datetime not null
    , primary key (
        ecoCode
      )
    , unique (
        setId
      , kopaCode
      )
    )
    go

## Generated Stored Procs

    use AnyDB

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesInsert') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesInsert
    GO

    CREATE PROCEDURE dbo.kopaCodesInsert
    ( @P0 varchar(24) -- ecoCode
    , @P1 varchar(2) -- setId
    , @P2 varchar(24) -- kopaCode
    , @P3 varchar(24) -- pageId
    , @P4 varchar(1) -- period
    , @P5 varchar(250) -- description
    , @P6 integer -- selected
    , @P7 integer -- strategy
    , @P8 integer -- disUsed
    , @P9 varchar(16) -- updBy
    , @P10 datetime -- updWhen
    )
    AS
    insert into kopaCodes
    ( ecoCode
    , setId
    , kopaCode
    , pageId
    , period
    , description
    , selected
    , strategy
    , disUsed
    , updBy
    , updWhen
    ) 
    values
     (@P0
    , @P1
    , @P2
    , @P3
    , @P4
    , @P5
    , @P6
    , @P7
    , @P8
    , @P9
    , @P10
    )
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesSelectOne') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesSelectOne
    GO

    CREATE PROCEDURE dbo.kopaCodesSelectOne
    ( @P0 varchar(24) -- ecoCode
    )
    AS
    select
      setId
    , kopaCode
    , pageId
    , period
    , description
    , selected
    , strategy
    , disUsed
    , updBy
    , updWhen
     from kopaCodes
     where ecoCode = @P0
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdate') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesUpdate
    GO

    CREATE PROCEDURE dbo.kopaCodesUpdate
    ( @P0 varchar(2) -- setId
    , @P1 varchar(24) -- kopaCode
    , @P2 varchar(24) -- pageId
    , @P3 varchar(1) -- period
    , @P4 varchar(250) -- description
    , @P5 integer -- selected
    , @P6 integer -- strategy
    , @P7 integer -- disUsed
    , @P8 varchar(16) -- updBy
    , @P9 datetime -- updWhen
    , @P10 varchar(24) -- ecoCode
    )
    AS
    update kopaCodes
     set
      setId = @P0
    , kopaCode = @P1
    , pageId = @P2
    , period = @P3
    , description = @P4
    , selected = @P5
    , strategy = @P6
    , disUsed = @P7
    , updBy = @P8
    , updWhen = @P9
     where ecoCode = @P10
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesSelectAll') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesSelectAll
    GO
    
    CREATE PROCEDURE dbo.kopaCodesSelectAll
    AS
    select
      ecoCode
    , setId
    , kopaCode
    , pageId
    , period
    , description
    , selected
    , strategy
    , disUsed
    , updBy
    , updWhen
     from kopaCodes
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesDeleteOne') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesDeleteOne
    GO

    CREATE PROCEDURE dbo.kopaCodesDeleteOne
    ( @P0 varchar(24) -- ecoCode
    )
    AS
    delete from kopaCodes
     where ecoCode = @P0
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesList') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesList
    GO

    CREATE PROCEDURE dbo.kopaCodesList
    ( @P0 varchar(2) -- setId
    , @P1 varchar(1) -- period
    )
    AS
    SELECT ecoCode, pageId, kopaCode, period, description, disUsed 
    FROM kopaCodes 
    WHERE setId = @P0 
    AND   period = @P1 
    ORDER BY kopaCode 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesListBySet') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesListBySet
    GO

    CREATE PROCEDURE dbo.kopaCodesListBySet
    ( @P0 varchar(2) -- setId
    , @P1 varchar(24) -- pageId
    , @P2 varchar(1) -- period
    )
    AS
    SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period, updBy, updWhen 
    FROM kopaCodes 
    WHERE setId = @P0 
    AND  pageId = @P1 
    AND  period = @P2 
    ORDER BY ecoCode 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdateFromK') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesUpdateFromK
    GO

    CREATE PROCEDURE dbo.kopaCodesUpdateFromK
    ( @P0 varchar(24) -- pageId
    , @P1 varchar(250) -- description
    , @P2 varchar(2) -- setId
    , @P3 varchar(24) -- kopaCode
    )
    AS
    UPDATE kopaCodes 
    SET pageId = @P0 
    , description = @P1 
    , disUsed = 0 
    WHERE setId = @P2 
    AND   kopaCode = @P3 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesMarkNotUsed') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesMarkNotUsed
    GO

    CREATE PROCEDURE dbo.kopaCodesMarkNotUsed
    ( @P0 varchar(2) -- setId
    , @P1 varchar(24) -- kopaCode
    )
    AS
    UPDATE kopaCodes 
    SET disUsed = 1 
    WHERE setId = @P0 
    AND   kopaCode = @P1 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdateSelect') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesUpdateSelect
    GO

    CREATE PROCEDURE dbo.kopaCodesUpdateSelect
    ( @P0 varchar(24) -- ecoCode
    , @P1 integer -- selected
    , @P2 integer -- strategy
    , @P3 varchar(2) -- setId
    , @P4 varchar(24) -- kopaCode
    )
    AS
    UPDATE kopaCodes 
    SET ecoCode  = @P0 
    , selected = @P1 
    , strategy = @P2 
    WHERE setId  = @P3 
    AND kopaCode = @P4 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesBuildSetList') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesBuildSetList
    GO

    CREATE PROCEDURE dbo.kopaCodesBuildSetList
    ( @P0 varchar(2) -- setId
    , @P1 varchar(2) -- setId
    )
    AS
    SELECT c.ecoCode, c.selected, c.strategy, s.dir, c.setId, c.kopaCode, c.period, s.lastNo 
    FROM kopaCodes c, KopaSets s 
    WHERE c.selected > 0 
    AND   (s.setId = @P0 or @P1 = '**') 
    AND   c.setId = s.SetId 
    GO

    if exists (select * from sysobjects where id = object_id('dbo.kopaCodesChangeAlias') and sysstat & 0xf = 4)
    drop procedure dbo.kopaCodesChangeAlias
    GO

    CREATE PROCEDURE dbo.kopaCodesChangeAlias
    ( @P0 varchar(24) -- ecoCode
    , @P1 varchar(2) -- setId
    , @P2 varchar(24) -- kopaCode
    )
    AS
    update kopaCodes set ecoCode = @P0 
    where  setId = @P1 
    and    kopaCode = @P2 
    GO

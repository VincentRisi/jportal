# MsSql CSharp Example 

This code demonstrates almost 10 to 1 code generation. It is used in conjuction with Portal.cs from support code.

## SI File

    DATABASE AnyDB FLAGS "mssql storedprocs"
    PACKAGE  Kam.DBRoutines
    OUTPUT   kopaCodes
    SERVER   Kopa

    TABLE kopaCodes           ** List of availble Kopa Series Codes
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
      SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period, updBy, updWhen
      FROM kopaCodes
      WHERE setId = :setId
      AND  pageId = :pageId
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
      ecoCode       char 24   ** This code is unique here and will be unique if present in kopaCodes on JohnnyDB
    SQLCODE
      update kopaCodes set ecoCode = :ecoCode 
      where  setId = :setId 
      and    kopaCode = :kopaCode
    ENDCODE

## CS File

    using System;
    using System.Collections.Generic;
    using System.Data;
    using Bbd.Idl2.AnyDb;
    
    namespace Kam.DBRoutines
    {
      /// <summary>
      ///  List of availble Kopa Series Codes
      /// </summary>
      [Serializable()]
      public partial class kopaCodesRec
      {
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string pageId { get { return _pageId; } set { _pageId = value; } } internal string _pageId;
        public string period { get { return _period; } set { _period = value; } } internal string _period;
        public string description { get { return _description; } set { _description = value; } } internal string _description;
        public int selected { get { return _selected; } set { _selected = value; } } internal int _selected;
        public int strategy { get { return _strategy; } set { _strategy = value; } } internal int _strategy;
        public int disUsed { get { return _disUsed; } set { _disUsed = value; } } internal int _disUsed;
        public string updBy { get { return _updBy; } set { _updBy = value; } } internal string _updBy;
        public DateTime updWhen { get { return _updWhen; } set { _updWhen = value; } } internal DateTime _updWhen;
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
          return "unknown ordinal: "+ordinal;
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
          return "unknown ordinal: "+ordinal;
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
          return "unknown ordinal: "+ordinal;
        }
      }
      [Serializable()]
      public partial class kopaCodesDeleteOneRec
      {
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
      }
      [Serializable()]
      public partial class kopaCodesListRec
      {
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
        public string pageId { get { return _pageId; } set { _pageId = value; } } internal string _pageId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string period { get { return _period; } set { _period = value; } } internal string _period;
        public string description { get { return _description; } set { _description = value; } } internal string _description;
        public int disUsed { get { return _disUsed; } set { _disUsed = value; } } internal int _disUsed;
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
      }
      [Serializable()]
      public partial class kopaCodesListBySetRec
      {
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
        public int selected { get { return _selected; } set { _selected = value; } } internal int _selected;
        public int strategy { get { return _strategy; } set { _strategy = value; } } internal int _strategy;
        public int disUsed { get { return _disUsed; } set { _disUsed = value; } } internal int _disUsed;
        public string description { get { return _description; } set { _description = value; } } internal string _description;
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string pageId { get { return _pageId; } set { _pageId = value; } } internal string _pageId;
        public string period { get { return _period; } set { _period = value; } } internal string _period;
        public string updBy { get { return _updBy; } set { _updBy = value; } } internal string _updBy;
        public DateTime updWhen { get { return _updWhen; } set { _updWhen = value; } } internal DateTime _updWhen;
      }
      [Serializable()]
      public partial class kopaCodesUpdateFromKRec
      {
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string pageId { get { return _pageId; } set { _pageId = value; } } internal string _pageId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string description { get { return _description; } set { _description = value; } } internal string _description;
        public string updBy { get { return _updBy; } set { _updBy = value; } } internal string _updBy;
        public DateTime updWhen { get { return _updWhen; } set { _updWhen = value; } } internal DateTime _updWhen;
      }
      [Serializable()]
      public partial class kopaCodesMarkNotUsedRec
      {
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
      }
      [Serializable()]
      public partial class kopaCodesUpdateSelectRec
      {
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
        public int selected { get { return _selected; } set { _selected = value; } } internal int _selected;
        public int strategy { get { return _strategy; } set { _strategy = value; } } internal int _strategy;
      }
      [Serializable()]
      public partial class kopaCodesBuildSetListRec
      {
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
        public int selected { get { return _selected; } set { _selected = value; } } internal int _selected;
        public int strategy { get { return _strategy; } set { _strategy = value; } } internal int _strategy;
        public string dir { get { return _dir; } set { _dir = value; } } internal string _dir;
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string period { get { return _period; } set { _period = value; } } internal string _period;
        public int lastNo { get { return _lastNo; } set { _lastNo = value; } } internal int _lastNo;
      }
      [Serializable()]
      public partial class kopaCodesChangeAliasRec
      {
        public string setId { get { return _setId; } set { _setId = value; } } internal string _setId;
        public string kopaCode { get { return _kopaCode; } set { _kopaCode = value; } } internal string _kopaCode;
        public string ecoCode { get { return _ecoCode; } set { _ecoCode = value; } } internal string _ecoCode;
      }
      [Serializable()]
      public partial class kopaCodes
      {
        private kopaCodesRec mRec;
        public kopaCodesRec Rec { get { return mRec; } set { mRec = value; } }
        private List<kopaCodesRec> mList;
        public int Count { get { return mList.Count; } }
        public Cursor mCursor;
        public kopaCodesRec this[int i]
        {
          get
          {
            if (i >= 0 && i < mList.Count)
              return mList[i];
            throw new JPortalException("m index out of range");
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
          mList = new List<kopaCodesRec>();
          mRec = new kopaCodesRec();
        }
        public kopaCodes()
        {
          Clear();
        }
        public string CommandInsert()
        {
          // insert into kopaCodes (
          //   ecoCode,
          //   setId,
          //   kopaCode,
          //   pageId,
          //   period,
          //   description,
          //   selected,
          //   strategy,
          //   disUsed,
          //   updBy,
          //   updWhen
          //  ) 
          //  values (
          //   @P0,
          //   @P1,
          //   @P2,
          //   @P3,
          //   @P4,
          //   @P5,
          //   @P6,
          //   @P7,
          //   @P8,
          //   @P9,
          //   @P10
          //  )
          return "kopaCodesInsert";
        }
        public void Insert(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandInsert());
            wCursor.Parameter(0, mRec._ecoCode);
            wCursor.Parameter(1, mRec._setId);
            wCursor.Parameter(2, mRec._kopaCode);
            wCursor.Parameter(3, mRec._pageId);
            wCursor.Parameter(4, mRec._period);
            wCursor.Parameter(5, mRec._description);
            wCursor.Parameter(6, mRec._selected);
            wCursor.Parameter(7, mRec._strategy);
            wCursor.Parameter(8, mRec._disUsed);
            wCursor.Parameter(9, mRec._updBy);
            wCursor.Parameter(10, wCursor.GetTimeStamp(ref mRec._updWhen));
            wCursor.Exec();
          }
        }
        public string CommandSelectOne()
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
          return "kopaCodesSelectOne";
        }
        public bool SelectOne(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandSelectOne);
            wCursor.Parameter(0, mRec._ecoCode);
            wCursor.Run();
            bool wResult = (wCursor.HasReader() && wCursor.Read());
            if (wResult == true)
            {
              mRec._setId = wCursor.GetString(0);
              mRec._kopaCode = wCursor.GetString(1);
              mRec._pageId = wCursor.GetString(2);
              mRec._period = wCursor.GetString(3);
              mRec._description = wCursor.GetString(4);
              mRec._selected = wCursor.GetInt(5);
              mRec._strategy = wCursor.GetInt(6);
              mRec._disUsed = wCursor.GetInt(7);
              mRec._updBy = wCursor.GetString(8);
              mRec._updWhen = wCursor.GetDateTime(9);
            }
            if (wCursor.HasReader())
              wCursor.Close();
            return wResult;
          }
        }
        public bool SelectOne(Connect connect
        , string _ecoCode
        )
        {
          mRec = new kopaCodesRec
          { ecoCode = _ecoCode
          };
          return SelectOne(connect);
        }
        public string CommandUpdate()
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
          return "kopaCodesUpdate";
        }
        public void Update(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandUpdate());
            wCursor.Parameter(0, mRec._setId);
            wCursor.Parameter(1, mRec._kopaCode);
            wCursor.Parameter(2, mRec._pageId);
            wCursor.Parameter(3, mRec._period);
            wCursor.Parameter(4, mRec._description);
            wCursor.Parameter(5, mRec._selected);
            wCursor.Parameter(6, mRec._strategy);
            wCursor.Parameter(7, mRec._disUsed);
            wCursor.Parameter(8, mRec._updBy);
            wCursor.Parameter(9, wCursor.GetTimeStamp(ref mRec._updWhen));
            wCursor.Parameter(10, mRec._ecoCode);
            wCursor.Exec();
          }
        }
        public string CommandSelectAll()
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
          return "kopaCodesSelectAll";
        }
        public void SelectAll(Connect connect)
        {
          mCursor = new Cursor(connect);
          mCursor.Procedure(CommandSelectAll);
          mCursor.Run();
        }
        public DataTable SelectAllDataTable(Connect connect)
        {
          SelectAll(connect);
          if (mCursor.HasReader())
          {
            DataTable table = new DataTable();
            table.Load(mCursor.Reader);
            return table;
          }
          else return null;
        }
        public void SetkopaCodesRec(DataTable table, int row)
        {
          mRec = new kopaCodesRec();
          DataRowCollection rows = table.Rows;
          if (row < rows.Count)
          {
            mRec._ecoCode = (string)rows[row]["ecoCode"];
            mRec._setId = (string)rows[row]["setId"];
            mRec._kopaCode = (string)rows[row]["kopaCode"];
            mRec._pageId = (string)rows[row]["pageId"];
            mRec._period = (string)rows[row]["period"];
            mRec._description = (string)rows[row]["description"];
            mRec._selected = Convert.Int32(rows[row]["selected"]);
            mRec._strategy = Convert.Int32(rows[row]["strategy"]);
            mRec._disUsed = Convert.Int32(rows[row]["disUsed"]);
            mRec._updBy = (string)rows[row]["updBy"];
            mRec._updWhen = (DateTime)rows[row]["updWhen"];
          }
        }
        public bool SelectAllFetch()
        {
          bool wResult = (mCursor.HasReader() && mCursor.Read());
          if (wResult == true)
          {
            mRec._ecoCode = mCursor.GetString(0);
            mRec._setId = mCursor.GetString(1);
            mRec._kopaCode = mCursor.GetString(2);
            mRec._pageId = mCursor.GetString(3);
            mRec._period = mCursor.GetString(4);
            mRec._description = mCursor.GetString(5);
            mRec._selected = mCursor.GetInt(6);
            mRec._strategy = mCursor.GetInt(7);
            mRec._disUsed = mCursor.GetInt(8);
            mRec._updBy = mCursor.GetString(9);
            mRec._updWhen = mCursor.GetDateTime(10);
          }
          else if (mCursor.HasReader())
            mCursor.Close();
          return wResult;
        }
        public void SelectAllLoad(Connect connect)
        {
          SelectAll(connect);
          while (SelectAllFetch())
          {
            mList.Add(mRec);
            mRec = new kopaCodesRec();
          }
        }
        public IEnumerable<kopaCodesRec> SelectAllYield(Connect connect)
        {
          try
          {
            SelectAll(connect);
            while (SelectAllFetch())
              yield return mRec;
          }
          finally
          {
            mCursor.Close();
          }
        }
        public List<kopaCodesRec> Loaded { get { return mList; } }
       }
       [Serializable()]
       public partial class kopaCodesDeleteOne
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
         public string CommandDeleteOne()
         {
           // delete from kopaCodes
           //  where ecoCode = @P0
           return "kopaCodesDeleteOne";
         }
         public void DeleteOne(Connect connect)
         {
           using (Cursor wCursor = new Cursor(connect))
           {
             wCursor.Procedure(CommandDeleteOne());
             wCursor.Parameter(0, mRec._ecoCode);
             wCursor.Exec();
           }
         }
       }
       [Serializable()]
       public partial class kopaCodesList
       {
         private kopaCodesListRec mRec;
         public kopaCodesListRec Rec { get { return mRec; } set { mRec = value; } }
         private List<kopaCodesListRec> mList;
         public int Count { get { return mList.Count; } }
         public Cursor mCursor;
         public kopaCodesListRec this[int i]
         {
           get
           {
             if (i >= 0 && i < mList.Count)
               return mList[i];
             throw new JPortalException("m index out of range");
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
           mList = new List<kopaCodesListRec>();
           mRec = new kopaCodesListRec();
         }
         public kopaCodesList()
         {
           Clear();
         }
         public string CommandList()
         {
           // SELECT ecoCode, pageId, kopaCode, period, description, disUsed 
           // FROM kopaCodes 
           // WHERE setId = @P0 
           // AND   period = @P1 
           // ORDER BY kopaCode 
           return "kopaCodesList";
         }
         public void List(Connect connect)
         {
           mCursor = new Cursor(connect);
           mCursor.Procedure(CommandList);
           mCursor.Parameter(0, mRec._setId);
           mCursor.Parameter(1, mRec._period);
           mCursor.Run();
         }
         public DataTable ListDataTable(Connect connect)
         {
           List(connect);
           if (mCursor.HasReader())
           {
             DataTable table = new DataTable();
             table.Load(mCursor.Reader);
             return table;
           }
           else return null;
         }
         public void SetkopaCodesListRec(DataTable table, int row)
         {
           mRec = new kopaCodesListRec();
           DataRowCollection rows = table.Rows;
           if (row < rows.Count)
           {
             mRec._ecoCode = (string)rows[row]["ecoCode"];
             mRec._pageId = (string)rows[row]["pageId"];
             mRec._kopaCode = (string)rows[row]["kopaCode"];
             mRec._period = (string)rows[row]["period"];
             mRec._description = (string)rows[row]["description"];
             mRec._disUsed = Convert.Int32(rows[row]["disUsed"]);
           }
         }
         public bool ListFetch()
         {
           bool wResult = (mCursor.HasReader() && mCursor.Read());
           if (wResult == true)
           {
             mRec._ecoCode = mCursor.GetString(0);
             mRec._pageId = mCursor.GetString(1);
             mRec._kopaCode = mCursor.GetString(2);
             mRec._period = mCursor.GetString(3);
             mRec._description = mCursor.GetString(4);
             mRec._disUsed = mCursor.GetInt(5);
           }
           else if (mCursor.HasReader())
             mCursor.Close();
           return wResult;
         }
         public void ListLoad(Connect connect)
         {
           List(connect);
           while (ListFetch())
           {
             mList.Add(mRec);
             mRec = new kopaCodesListRec();
           }
         }
         public IEnumerable<kopaCodesListRec> ListYield(Connect connect)
         {
           try
           {
             List(connect);
             while (ListFetch())
               yield return mRec;
           }
           finally
           {
             mCursor.Close();
           }
         }
         public IEnumerable<kopaCodesListRec> ListYield(Connect connect
         , string _setId
         , string _period
         )
         {
           mRec = new kopaCodesListRec
           { setId = _setId
           , period = _period
           };
           return ListYield(connect);
         }
         public List<kopaCodesListRec> Loaded { get { return mList; } }
      }
      [Serializable()]
      public partial class kopaCodesListBySet
      {
        private kopaCodesListBySetRec mRec;
        public kopaCodesListBySetRec Rec { get { return mRec; } set { mRec = value; } }
        private List<kopaCodesListBySetRec> mList;
        public int Count { get { return mList.Count; } }
        public Cursor mCursor;
        public kopaCodesListBySetRec this[int i]
        {
          get
          {
            if (i >= 0 && i < mList.Count)
              return mList[i];
            throw new JPortalException("m index out of range");
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
          mList = new List<kopaCodesListBySetRec>();
          mRec = new kopaCodesListBySetRec();
        }
        public kopaCodesListBySet()
        {
          Clear();
        }
        public string CommandListBySet()
        {
          // SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period, updBy, updWhen 
          // FROM kopaCodes 
          // WHERE setId = @P0 
          // AND  pageId = @P1 
          // AND  period = @P2 
          // ORDER BY ecoCode 
          return "kopaCodesListBySet";
        }
        public void ListBySet(Connect connect)
        {
          mCursor = new Cursor(connect);
          mCursor.Procedure(CommandListBySet);
          mCursor.Parameter(0, mRec._setId);
          mCursor.Parameter(1, mRec._pageId);
          mCursor.Parameter(2, mRec._period);
          mCursor.Run();
        }
        public DataTable ListBySetDataTable(Connect connect)
        {
          ListBySet(connect);
          if (mCursor.HasReader())
          {
            DataTable table = new DataTable();
            table.Load(mCursor.Reader);
            return table;
          }
          else return null;
        }
        public void SetkopaCodesListBySetRec(DataTable table, int row)
        {
          mRec = new kopaCodesListBySetRec();
          DataRowCollection rows = table.Rows;
          if (row < rows.Count)
          {
            mRec._ecoCode = (string)rows[row]["ecoCode"];
            mRec._selected = Convert.Int32(rows[row]["selected"]);
            mRec._strategy = Convert.Int32(rows[row]["strategy"]);
            mRec._disUsed = Convert.Int32(rows[row]["disUsed"]);
            mRec._description = (string)rows[row]["description"];
            mRec._setId = (string)rows[row]["setId"];
            mRec._kopaCode = (string)rows[row]["kopaCode"];
            mRec._pageId = (string)rows[row]["pageId"];
            mRec._period = (string)rows[row]["period"];
            mRec._updBy = (string)rows[row]["updBy"];
            mRec._updWhen = (DateTime)rows[row]["updWhen"];
          }
        }
        public bool ListBySetFetch()
        {
          bool wResult = (mCursor.HasReader() && mCursor.Read());
          if (wResult == true)
          {
            mRec._ecoCode = mCursor.GetString(0);
            mRec._selected = mCursor.GetInt(1);
            mRec._strategy = mCursor.GetInt(2);
            mRec._disUsed = mCursor.GetInt(3);
           mRec._description = mCursor.GetString(4);
           mRec._setId = mCursor.GetString(5);
           mRec._kopaCode = mCursor.GetString(6);
           mRec._pageId = mCursor.GetString(7);
           mRec._period = mCursor.GetString(8);
           mRec._updBy = mCursor.GetString(9);
           mRec._updWhen = mCursor.GetDateTime(10);
         }
         else if (mCursor.HasReader())
           mCursor.Close();
         return wResult;
       }
       public void ListBySetLoad(Connect connect)
       {
         ListBySet(connect);
         while (ListBySetFetch())
         {
           mList.Add(mRec);
           mRec = new kopaCodesListBySetRec();
         }
       }
       public IEnumerable<kopaCodesListBySetRec> ListBySetYield(Connect connect)
       {
         try
         {
           ListBySet(connect);
           while (ListBySetFetch())
             yield return mRec;
         }
         finally
         {
           mCursor.Close();
         }
       }
       public IEnumerable<kopaCodesListBySetRec> ListBySetYield(Connect connect
       , string _setId
       , string _pageId
       , string _period
       )
       {
         mRec = new kopaCodesListBySetRec
         { setId = _setId
         , pageId = _pageId
         , period = _period
         };
         return ListBySetYield(connect);
       }
       public List<kopaCodesListBySetRec> Loaded { get { return mList; } }
     }
      [Serializable()]
      public partial class kopaCodesUpdateFromK
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
        public string CommandUpdateFromK()
        {
          // UPDATE kopaCodes 
          // SET pageId = @P0 
          // , description = @P1 
          // , disUsed = 0 
          // WHERE setId = @P2 
          // AND   kopaCode = @P3 
          return "kopaCodesUpdateFromK";
        }
        public void UpdateFromK(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandUpdateFromK());
            wCursor.Parameter(0, mRec._pageId);
            wCursor.Parameter(1, mRec._description);
            wCursor.Parameter(2, mRec._setId);
            wCursor.Parameter(3, mRec._kopaCode);
            wCursor.Exec();
          }
        }
      }
      [Serializable()]
      public partial class kopaCodesMarkNotUsed
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
        public string CommandMarkNotUsed()
        {
          // UPDATE kopaCodes 
          // SET disUsed = 1 
          // WHERE setId = @P0 
          // AND   kopaCode = @P1 
          return "kopaCodesMarkNotUsed";
        }
        public void MarkNotUsed(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandMarkNotUsed());
            wCursor.Parameter(0, mRec._setId);
            wCursor.Parameter(1, mRec._kopaCode);
            wCursor.Exec();
          }
        }
      }
      [Serializable()]
      public partial class kopaCodesUpdateSelect
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
        public string CommandUpdateSelect()
        {
          // UPDATE kopaCodes 
          // SET ecoCode  = @P0 
          // , selected = @P1 
          // , strategy = @P2 
          // WHERE setId  = @P3 
          // AND kopaCode = @P4 
          return "kopaCodesUpdateSelect";
        }
        public void UpdateSelect(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandUpdateSelect());
            wCursor.Parameter(0, mRec._ecoCode);
            wCursor.Parameter(1, mRec._selected);
            wCursor.Parameter(2, mRec._strategy);
            wCursor.Parameter(3, mRec._setId);
            wCursor.Parameter(4, mRec._kopaCode);
            wCursor.Exec();
          }
        }
      }
      [Serializable()]
      public partial class kopaCodesBuildSetList
      {
        private kopaCodesBuildSetListRec mRec;
        public kopaCodesBuildSetListRec Rec { get { return mRec; } set { mRec = value; } }
        private List<kopaCodesBuildSetListRec> mList;
        public int Count { get { return mList.Count; } }
        public Cursor mCursor;
        public kopaCodesBuildSetListRec this[int i]
        {
          get
          {
            if (i >= 0 && i < mList.Count)
              return mList[i];
            throw new JPortalException("m index out of range");
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
          mList = new List<kopaCodesBuildSetListRec>();
          mRec = new kopaCodesBuildSetListRec();
        }
        public kopaCodesBuildSetList()
        {
          Clear();
        }
        public string CommandBuildSetList()
        {
          // SELECT c.ecoCode, c.selected, c.strategy, s.dir, c.setId, c.kopaCode, c.period, s.lastNo 
          // FROM kopaCodes c, KopaSets s 
          // WHERE c.selected > 0 
          // AND   (s.setId = @P0 or @P1 = '**') 
          // AND   c.setId = s.SetId 
          return "kopaCodesBuildSetList";
        }
        public void BuildSetList(Connect connect)
        {
          mCursor = new Cursor(connect);
          mCursor.Procedure(CommandBuildSetList);
          mCursor.Parameter(0, mRec._setId);
          mCursor.Parameter(1, mRec._setId);
          mCursor.Run();
        }
        public DataTable BuildSetListDataTable(Connect connect)
        {
          BuildSetList(connect);
          if (mCursor.HasReader())
          {
            DataTable table = new DataTable();
            table.Load(mCursor.Reader);
            return table;
          }
          else return null;
        }
        public void SetkopaCodesBuildSetListRec(DataTable table, int row)
        {
          mRec = new kopaCodesBuildSetListRec();
          DataRowCollection rows = table.Rows;
          if (row < rows.Count)
          {
            mRec._ecoCode = (string)rows[row]["ecoCode"];
            mRec._selected = Convert.Int32(rows[row]["selected"]);
            mRec._strategy = Convert.Int32(rows[row]["strategy"]);
            mRec._dir = (string)rows[row]["dir"];
            mRec._setId = (string)rows[row]["setId"];
            mRec._kopaCode = (string)rows[row]["kopaCode"];
            mRec._period = (string)rows[row]["period"];
            mRec._lastNo = Convert.Int32(rows[row]["lastNo"]);
          }
        }
        public bool BuildSetListFetch()
        {
          bool wResult = (mCursor.HasReader() && mCursor.Read());
          if (wResult == true)
          {
            mRec._ecoCode = mCursor.GetString(0);
            mRec._selected = mCursor.GetInt(1);
            mRec._strategy = mCursor.GetInt(2);
            mRec._dir = mCursor.GetString(3);
            mRec._setId = mCursor.GetString(4);
            mRec._kopaCode = mCursor.GetString(5);
            mRec._period = mCursor.GetString(6);
            mRec._lastNo = mCursor.GetInt(7);
          }
          else if (mCursor.HasReader())
            mCursor.Close();
          return wResult;
        }
        public void BuildSetListLoad(Connect connect)
        {
          BuildSetList(connect);
          while (BuildSetListFetch())
          {
            mList.Add(mRec);
            mRec = new kopaCodesBuildSetListRec();
          }
        }
        public IEnumerable<kopaCodesBuildSetListRec> BuildSetListYield(Connect connect)
        {
          try
          {
            BuildSetList(connect);
            while (BuildSetListFetch())
              yield return mRec;
          }
          finally
          {
            mCursor.Close();
          }
        }
        public IEnumerable<kopaCodesBuildSetListRec> BuildSetListYield(Connect connect
        , string _setId
        )
        {
          mRec = new kopaCodesBuildSetListRec
          { setId = _setId
          };
          return BuildSetListYield(connect);
        }
        public List<kopaCodesBuildSetListRec> Loaded { get { return mList; } }
      }
      [Serializable()]
      public partial class kopaCodesChangeAlias
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
        public string CommandChangeAlias()
        {
          // update kopaCodes set ecoCode = @P0 
          // where  setId = @P1 
          // and    kopaCode = @P2 
          return "kopaCodesChangeAlias";
        }
        public void ChangeAlias(Connect connect)
        {
          using (Cursor wCursor = new Cursor(connect))
          {
            wCursor.Procedure(CommandChangeAlias());
            wCursor.Parameter(0, mRec._ecoCode);
            wCursor.Parameter(1, mRec._setId);
            wCursor.Parameter(2, mRec._kopaCode);
            wCursor.Exec();
          }
        }
      }
    }

## Stored Procs

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
    insert into kopaCodes (
      ecoCode,
      setId,
      kopaCode,
      pageId,
      period,
      description,
      selected,
      strategy,
      disUsed,
      updBy,
      updWhen
     ) 
     values (
      @P0,
      @P1,
      @P2,
      @P3,
      @P4,
      @P5,
      @P6,
      @P7,
      @P8,
      @P9,
      @P10
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

## DDL 

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
    , selected int not null
    , strategy int not null
    , disUsed int not null
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

package bbd.binu.parser;

public class TJField
{
  static final int
      TJ_BLOB         = 1
    , TJ_BOOLEAN      = 2
    , TJ_BYTE         = 3
    , TJ_CHAR         = 4
    , TJ_DATE         = 5
    , TJ_DATETIME     = 6
    , TJ_DOUBLE       = 7
    , TJ_DYNAMIC_     = 8
    , TJ_FLOAT        = 9
    , TJ_IDENTITY     = 10
    , TJ_INT          = 11
    , TJ_LONG         = 12
    , TJ_MONEY        = 13
    , TJ_SEQUENCE     = 14
    , TJ_SHORT        = 15
    , TJ_STATUS       = 16
    , TJ_TIME         = 17
    , TJ_TIMESTAMP    = 18
    , TJ_TLOB         = 19
    , TJ_USERSTAMP    = 20
    , TJ_ANSICHAR     = 21
    , TJ_UID          = 22
    , TJ_XML          = 23
    , TJ_BIGSEQUENCE  = 24
    , TJ_BIGIDENTITY  = 25
    ;
  public String name;
  public int type;
  public int length;
  public int precision;
  public int scale;
  public int offset;
  public int size;
  public boolean isPrimaryKey, isSequence, isNull, isIn, isOut;
  public void resetOptions()
  {
    isPrimaryKey = isSequence = isNull = isIn = isOut = false;
  }
}

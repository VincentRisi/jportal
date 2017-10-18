Backus Naur Form
----------------

The following rules apply to the BNF diagram.

*  all lowercase indicates a lexical token (see identifier below)
*  all uppercase indicates the actual character sequence.
*  Capitilised words indicate a parser production.
*  Single characters are enclosed in single or double quotes.
*  A question mark indicates zero or one occurrence.
*  An asterisk indicate zero or more occurrences.
*  A plus indicates one or more occurrences.

The BNF is described in alphabetical sequence the start point is at Database.

All whitespace is ignored, this includes single line comment which start with "//" and multi line comments which start with "/*" and end with "*/" and cannot have embedded multi line comments. These must not be confused with "**" line comments which are regarded a lexical tokens.

Lexical Tokens
--------------

    codeline = (~["\n","\r"])* ("\r\n" | "\n" | "\r")
    comment = "**" (~["\n","\r"])* ("\n"|"\r\n")
    digit = ["0"-"9", "#", "$"]
    dquoted = ("\"" (~["\"", "\\"] | "\\" (["\\", "\"", "\'"])?)* "\"")
    identifier = letter (letter|digit)*>
    letter = ["a"-"z", "_", "A"-"Z"]
    lidentifier = ("l"|"L") "'" letter (letter>|digit)* "'"
    number = (["1"-"9"] (["0"-"9"])*)|"0"
    squoted = ("\'" (~["\'", "\\"] | "\\" (["\\", "\"", "\'"])?)* "\'")
    string = dquoted | squoted

**Alias**

    '(' Ident ')'

**CharSize**

    '(' number ')' | '[' number ']' | number

**Check**

    CHECK string

**Column**

    Ident

**Database**

    DATABASE Ident  
    (FLAGS (string)+ )*  
    (PACKAGE Ident (.Ident)* )?  
    (OUTPUT (Ident | string) )?  
    SERVER Ident (USERID Ident PASSWORD Ident)?  
    (Tabledef)+
    (View)*

**DataType**

    ANSICHAR (CharSize)? |  
    BLOB (CharSize)? |  
    BOOLEAN |  
    BYTE |  
    CHAR (CharSize)? |  
    DATE |  
    DATETIME |  
    DOUBLE (FloatSize)? |  
    FLOAT (FloatSize)? |  
    IDENTITY |  
    INT (EnumValue)? |  
    LONG |  
    MONEY |  
    SEQUENCE |  
    SHORT (EnumValue)? |  
    TIME |  
    TIMESTAMP |  
    TLOB (CharSize)? |  
    UID |  
    USERSTAMP

**Default**

    DEFAULT string

**EnumValue**

    '(' (Ident '=' number (',')? )+ ')'

**Extras**

    Grant | Key | Link | View

**Field**

    Ident  
    (Alias)?  
    DataType  
    (Default|Null|Check)*  
    (comment)*  

**FloatSize**

    '(' number ',' number  ')' | '[' number ',' number ']'

**Grant**

    GRANT (Permission)+ TO (User)+

**Ident**

    (identifier | lidentifier)

**Input**

    (InputType)? (Field)+

**InputType**

    '(' (MULTIPLE | number) ')'

**Key**

    KEYIdent (Options)* (Modifier)* (Column)+

**Link**

    LINK Ident (Column)+

**Modifier**

    UNIQUE | PRIMARY

**NewCode**

    SQLCODE  
    (codeline)+  // with embedded Param  
    ENDCODE  

**NewData**

    SQLDATA  
    (codeline)+  
    ENDDATA  

**NewViewCode**

    SQLCODE  
    (codeline)+  
    ENDCODE  

**Null**

    (NOT)? NULL

**OldCode**

    CODE  
    (string | Ident)+  
    ENDCODE  

**OldData**

    DATA  
    (string | Ident)+  
    ENDDATA  

**OldViewCode**

    CODE  
    (string)+  
    ENDCODE  

**Options**

    OPTIONS (string)+

**Output**

    (OutputType)?(Field)+

**OutputType**

    '(' (SINGLE | number) ')'

**Param**

    '&' Ident ('(' number ')')?

**Permission**

    ALL | DELETE | SELECT | UPDATE | EXECUTE

**Proc**

    (PROC (StdProc | UserProc)) | OldData | NewData

**StdProc**

    BULKINSERT (number)? |  
    BULKUPDATE (number)? |  
    COUNT |  
    DELETEALL |  
    DELETEONE |  
    EXISTS |  
    INSERT (RETURNING)? |  
    SELECTALL ((IN)? ORDER)? ((FOR)? UPDATE)? |  
    SELECTONE ((FOR)? UPDATE)? |  
    UPDATE (FOR (field)+)? (RETURNING)? (UName)? |
    UPDATEBY (field)+ (FOR (field)+)? (RETURNING)? (UName)?
    (Options)*  
    
**UName**

    UName string    

**Table**

    TABLE Ident  
    (Alias)?  
    (Check)?  
    (comment)*  
    (Options)*  
    (Field)*  

**Tabledef**

    Table  
    (Extras)*  
    (Proc)*  

**User**

    Ident

**UserProc**

    Ident (comment)*  
    (Input)?  
    (Output)?  
    (OldCode | NewCode)  

**View**

    Ident (TO (User)+)?  
    OUTPUT (Ident (Alias)?)+  
    (OldViewCode | NewViewCode)  


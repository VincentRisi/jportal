Format of an SI File
--------------------

You can use single line comments (those that start with // and multiline comments /* ... */) almost anywhere in the SI file. The code  between SQLCODE ... ENDCODE, SQLDATA ... ENDDATA  must use Sql style comments and these comments are not recognised by the AnyDB compiler.

The older CODE ... ENDCODE and DATA ... ENDDATA constructs have been deprecated and their usage should be avoided.

Blanks, tabs, carriage returns, line feeds and comments are generally regarded as whitespace in the normal lexical state. After the keywords SQLCODE and SQLDATA the lexical state changes and treats subsequent complete lines as tokens until a corresponding ENDCODE or ENDDATA is seen.

The first portion of the SI file is the Database Portion. We are showing keywords in uppercase and the in order that the keywords must be entered. Sometimes you may want to use a keyword as a name, this can be done by escaping the sequence in L'...', eg. if the database name was database then you would use DATABASE L'database'.

*Database Portion*

    DATABASE name                // The database name  
    (FLAGS (string)+)*           // The outer (...)* indicates 0 or more  
                                 // and the inner (...)+ indicates 1 or more  
    (PACKAGE name(.name)*)?      // The outer (...)? indicates 0 or 1  
    (OUTPUT (name|string))?      // The inner (...|...) indicates choice name or string  
    SERVER (name|string)         // Server name  
    (USERID name PASSWORD name)? // Connection information for DDL Generator  

*(Table Definitions)* *

As you can see from above DATABASE and SERVER are always required, but the others may be required for the generators used. If you are generating java or dotnet code you may wish to have a PACKAGE. The DDL is generated using the DATABASE name for the SQL file and you may wish to use OUTPUT name for this, because you have more than one SI file with the same DATABASE name. Following the Database Portion you can have one or more Table Definitions. Some people like one table definition per file, others like to place the tables in dependancy sequence and have clusters of tables per file.

*Table Definitions*

    TABLE name ('(' alias ')')? // the alias is optional, the generators  
                                // generally use this for Code (not DDL)  
    (CHECK string)?             // Check string for DDL Generators  
    ('**' restline)*            // Comments about the table the generators can use  
    (OPTIONS (string)+)*        // Options about the table the generators can use  
    (Field Definitions)*        // An existing table may be defined without field definitions  
    (Extra Definitions)*        // The extras are the Keys, Grants, Links, Views  
    (Proc Definitions)*         // These are the things that AnyDB is really about  

*Field Definitions*

    name ('(' alias ')')?       // Optional alias for Code (DDL normally uses name)  
    (ANSICHAR (charSize)?       // Blank padded character  
    |BLOB (charSize)?           // Binary large object (generator dependant)  
    |TLOB (charSize)?           // Text large object (generator dependant)  
    |BOOLEAN                    // Not often implemented non standard type  
    |BYTE (enumDef)?            // 8 bit number (0 to 255 or -128 to 127)  
    |CHAR (charSize)?           // Null terminated var char  
    |SHORT (enumDef)?           // 16 bit number (0 to 65535 or -32768 to 32767  
    |INT (enumDef)?             // 32 bit number (work it out for yourself)  
    |LONG                       // 32 or 64 bit integer  
    |UID                        // Not often implemented non standard type  
    |DATE                       // Just the Date  
    |DATETIME                   // The Date together with the time  
    |TIME                       // Just the Time  
    |TIMESTAMP                  // The Date and Time as a TimeStamp  
    |USERSTAMP                  // A UserStamp  
    |SEQUENCE                   // Used for sequence number generators  
    |IDENTITY                   // Used for RDBMS that have identity  
    |DOUBLE (floatDef)?         // 64 bit IEEE double  
    |FLOAT (floatDef)?          // 32 or 64 bit IEEE double  
    |MONEY)                     // Not often implemented non standard type  
    (DEFAULT string|(NOT)? NULL|CHECK string)* 

The charSize is either an integral number or an integral number in parenthesis or brackets, e.g. Fred char(10). FloatDef is two numbers indicating precision and scale, e.g. Value float(15,4) indicating a 15 digit number with 4 decimal places. Generally the generator do not distinguish between float and double, they are both treated as double normally, but again that is up to the generation required. The enumDef is a very powerful construct, e.g. State byte (ok=1, err=2). The DDL could implement a check in the database, although we have found that checks can be restrictive from a developers point of view, especially when a Database is maintained by DBA's. By default fields are generally assumed to be NOT NULL, ie. fields that can be NULL must be stated to be so. This is not the norm in ANSI SQL but from a programmers point of view I do not like the the extra testing and uncertainty that NULL fields introduce. There is certainly a place for null fields but not by default. SEQUENCE is useful for databases that have sequence generators, Oracle, Interbase, PostgreSQL. IDENTITY is useful for those that do not use sequence generators like MsSqlServer, MySql.

*Extra Definitions*

    (GRANT (ALL|DELETE|INSERT|SELECT|UPDATE)+ TO (name)+ // Grant rights to users  
    |KEY name (OPTIONS)+ (UNIQUE|PRIMARY)? (columns)+    // Define Keys and Indexes  
    |LINK table (columns)+                               // Define Foreign Keys  
    |View Definition)  

It is normally good pracise to have a primary key for each table in the database. Unique keys and other indexes to help speed up queries should also be discovered. Links are used to define foreign keys to other tables (or even perhaps to itself on occasion). The links expect that the target table has a primary key and often null fields are useful for foreign keys. In the quest for simplicity most of the time programmers like to have a single logon to the database. In this case grants are not used very often. The problem with grants generally occurs when migrating database data to other machines. But grants should be considered together with different logins. I have chosen to expand (or expound) view definition separately.

*View Definition*

    VIEW name (TO (user)+)?  
    OUTPUT (name ('(' alias ')')?)+  
    SQLCODE  
    lines  
    ENDCODE  

Views are a powerful construct and allow for the simplification of queries in some cases. Normally views are readonly but I am sure that in some RDBMS you may be able to do updates and inserts. I personally would avoid updates to views, mainly because I dislike defining NULL fields.

*Proc Definitions*

(Standard Proc|User Proc|Data Blocks)

`Standard Proc`

    PROC  
    (INSERT  
    |BULKINSERT ('(' number ')')?  
    |UPDATE  
    |BULKUPDATE ('(' number ')')?  
    |DELETEONE  
    |DELETEALL  
    |SELECTONE ((FOR)? UPDATE)?  
    |SELECTALL ((IN)? ORDER)? ((FOR)? UPDATE)?  
    |COUNT  
    |EXISTS)  
    (OPTIONS (string)+)*  

`Added Standard Proc`

    PROC name
    (SELECT ('(' STANDARD ')')? (INPUT (InputType)? (Field)+ )? (OUTPUT (OutputType)? (PackageField)* )? (OldCode | NewCode) 
    |SELECTONEBY (Column)+  ((FOR>)? (UPDATE|READONLY))? 
    |SELECTBY (Column)+ ((IN)? ORDER (Column)+ (DESC)? )? ((FOR)? (UPDATE|READONLY))? (OUTPUT (OutputType)? (Field)*)? 
    |DELETEBY (Column)+
    |UPDATEFOR (Column)+ 
    |UPDATEBY (Column)+ (FOR (Column)+)? 
    )  

`User Proc`

    PROC name  
    ('**' restline)*            // Comments about the proc the generators can use  
    (OPTIONS (string)+)*        // Options about the proc the generators can use  
    (INPUT ('(' MULTIPLE | number ')')? (Field Definitions)+)?  
    (OUTPUT ('(' SINGLE | number ')')? (Field Definitions)+)?  
    SQLCODE  
    (lines)+    // Each line parsed later for dynamic parameters &inp('('number')')?  
    ENDCODE  

`Data Blocks`

    SQLDATA  
    (lines)+  
    ENDDATA
 
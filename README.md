# JPortal (or AnyDB)

Database, Module and Application code generators for varying platforms, vendors and languages.
==============================================================================================

There are three code generator types written using the same mechanism.
This mechanism or paradigm follow the same coding pattern. There is a
parser generator or compiler compiler written using javacc for the tokenising
and grammar parsing. Each defines a domain specific language with some crossover
capabilities. These generators are written in plain old java and they are maintained
in Eclipse, but any text editor and javac would suffice to maintain them. They 
produce a generic class network database. They use public members and are not getter 
setter based classes. They also have a minimum of methods. They produce a tree structure
of the compiled source.

The compiled tree then is available to be used by generators to compile code to be
used by any computer language, current or future. This system has been used for many
projects and applications over the last 20 years, very successfully. It used to be Java 1.1
based as there were ide's written in C# to maintain code. Dropping the ide usage and the
need for J# has freed up this restriction.

See *Building JPortal* below.

JPortal RDBMS code generator
----------------------------

The class tree for *JPortal* starts with a single Database instance. As can be seen from
the structure below a database consists of various properties and Vector lists.

**class Database**

* String name, output, server, schema, userid, password, packageName
* Vector Table tables, String flags, Sequence sequences, View views, String imports

**class Table**

* Database database
* String name, alias, check
* Vector Field fields, Key keys, Link links, Grant grants, View views, Proc procs, String comments, String options, String allUsers, Parameter parameters, Const consts
* boolean hasPrimaryKey, hasSequence, hasTimeStamp, hasAutoTimeStamp, hasUserStamp, hasExecute, hasSelect, hasInsert, hasDelete, hasUpdate, hasStdProcs, hasIdentity, asSequenceReturning, hasBigXML, isStoredProc  

**class Proc**

 * Table   table
 * String  name
 * int     noRows
 * Vector Field inputs, Field outputs, String dynamics, Integer, dynamicSizes, Boolean dynamicStrung, String placeHolders, Line lines, String comments, String options
 * boolean isProc, isSProc, isData, isIdlCode, isSql, isSingle, isAction, isStd, useStd, extendsStd, useKey, hasImage, isMultipleInput, isInsert, hasReturning, hasUpdates
  
**class Sequence**

* String  name
* int minValue, maxValue, increment, startWith
* boolean cycleFlag, orderFlag  

**class View**

* String name
* Vector String aliases, String lines, String users  

**class Field**

* String name, alias, defaultValue, checkValue
* byte type
* int length, precision, scale, bindPos, definePos
* Vector String comments, Enum enums, String valueList
* String enumLink
* boolean isPrimaryKey, isSequence, isNull, isIn, isOut
* static final byte BLOB=1, BOOLEAN=2, BYTE=3, CHAR=4, DATE=5, DATETIME=6, DOUBLE=7, DYNAMIC=8, FLOAT=9, IDENTITY=10, INT=11, LONG=12, MONEY=13 SEQUENCE=14, SHORT=15, STATUS=16, TIME=17, TIMESTAMP=18, TLOB=19, USERSTAMP=20, ANSICHAR=21, UID=22, XML=23, BIGSEQUENCE=24, BIGIDENTITY=25, AUTOTIMESTAMP=26, WCHAR=27, WANSICHAR=28, UTF8=29, BIGXML=30
* static final int DEFAULT_XML=4096
* static final int DEFAULT_BIG_XML=4194304

**class Key**
 
* String name;
* Vector fields, String options
* boolean isPrimary, isUnique

**class Link**

* String name, linkName
* Vector String fields, String linkFields, String options
* boolean isDeleteCascade

**class Flag**

* String name
* Object value
* String description

**class Enum**

* String name
* int value

**class Grant**

* Vector String perms, String users

**class Const**

* String name
* Vector Value values

**class Value**

* String key, value

Crackle Module code generator
-----------------------------

The class tree for *Crackle* starts with a single Module instance. As can be seen from
the structure below a database consists of various properties and Vector lists.

**class Module**

* String sourceName, name, version, packageName
* int signature, countOfHashes, messageBase
* Message messages, Table tables, Structure structures, Enumerator enumerators, Prototype prototypes, String pragmas, String code, String imports

**class Message**

* String name, value

**class Table**

* String name;
* Vector Message messages

**class Structure**

* static final byte NORMAL=0, PUBLIC=1, PRIVATE=2, PROTECTED=3
* String name, header
* Vector String categories, Field fields
* byte codeType
* Vector String code;

**class Enumerator**
 
* String name
* Vector String elements

**class Prototype**

* static final byte RPCCALL=0, PUBLIC=1, PRIVATE=2, PROTECTED=3
* String name, message
* Type type
* Vector Field parameters, Action inputs, Action outputs, String categories
* byte codeType
* Vector String code

**class Action**

* String name
* Vector Operation operations

**class Operation**

* static final byte SIZE=1, DYNAMIC=2
* String name
* byte code
* boolean isConstant
* Field field

**class Field**

* String name
* Type type
* Action input, output
* boolean isInput, isOutput, hasSize

**class Type**

* static final byte USERTYPE=0, BOOLEAN=1, CHAR=2, SHORT=3, LONG=4, FLOAT=5, DOUBLE=6, VOID=7, BYTE=8, INT=9, STRING=10, WCHAR=11, BYVAL=1, BYPTR=2, BYREF=3, BYPTRPTR=4, BYREFPTR=5, ARRAYED=6
* String  name
* byte typeof, reference
* boolean isUnsigned
* Vector Integer arraySizes

Pickle Application code generator
---------------------------------

The class tree for *Pickle* starts with a single Application instance. As can be seen from
the structure below a database consists of various properties and Vector lists.

**class Application**

* **class LinkTable**

  - Link link
  - String name

* **class ValidationTable**

  - Validation validation
  - String name

* **class RelationTable**

  - Relation relation
  - String name
  - boolean from

* String name, descr, version, output, server, user, password, registry
* Vector Table tables, Relation relations, String flags, LinkTable missingLinks, RelationTable missingRelations, ValidationTable missingValidations, Field supplieds
* Validation validationInit, validationAll, validationOther
* int charSize, descrSize

**class Table**

* Application application
* String name, descr, alias, check
* Vector Field fields, String comments, String options, Field order, Field show, Field breaks, Value values, Link links, Key keys
* Validation  validation
* boolean useSequence, useChar, noDomain, viewOnly, isNullable

**class Field**

* String name, alias, check, type
* int length, precision, scale
* boolean isNull, isUppercase
* Vector String comments, Enum enums
* static final byte BOOLEAN=4, BYTE=8, CHAR=12, DATE=16, DATETIME=20, DOUBLE=24, INT=28, LONG=32, SEQUENCE=36, SHORT=40, TIME=44, TIMESTAMP=48, USERSTAMP=52

**class Relation**

* Application application
* String name, descr, alias, fromShort, toShort
* Table fromTable, toTable
* Vector Field fromFields, Field toFields, String fromFieldNames, String> toFieldNames, String comments, Value values
* Validation validation

**class Validation**

* Vector String code

**class Value**

* Vector String list

**class Enum**

* String  name
* int value

**class Link**

* Table table
* Vector String list
* boolean hasCascade

**class Key**

* String name
* Vector Field list
* boolean primary, unique


Building JPortal
================

**The tools used to build this are**

* cmake   - minimum 2.8
* eclipse - Version: Mars.1 Release (4.5.1)
* javacc  - SF JavaCC Eclipse Plug-in feature  1.5.32  sf.eclipse.javacc.feature.feature.group Rémi Koutcherawy
* pydev   - PyDev for Eclipse 4.4.0.201510052309  org.python.pydev.feature.feature.group  Fabio Zadrozny
* pydev   - Pydev Mylyn Integration  0.6.0 org.python.pydev.mylyn.feature.feature.group  Fabio Zadrozny
* CocoR   - Compiler Generator Coco/R, Copyright (c) 1990, 2004 Hanspeter Moessenboeck, University of Linz extended by M. Loeberbauer & A. Woess, Univ. of Linz
  - ported from C# to Java by Wolfgang Ahorner 
  - ported to C++ by Csaba Balazs, University of Szeged
  - with improvements by Pat Terry, Rhodes University
* log4j   - Apache log4j-1.2.17.jar
* Oracle DB Developer VM with VirtualBox - OTN_Developer_Day_VM.ova
* oracle-instantclient12.1-basic-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-basiclite-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-devel-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-jdbc-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-odbc-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-precomp-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-sqlplus-12.1.0.2.0-1.x86_64.rpm 
* oracle-instantclient12.1-tools-12.1.0.2.0-1.x86_64.rpm
* postgresql - 9.4

**Windows 7.0 tools**

* postgresql-9.5.0-beta2-3-windows.exe
* cmake-2.8.5-win32-x86.exe
* codeblocks-13.12-setup.exe
* eclipse-java-mars-1-win32.zip
* jdk-8u65-windows-i586.exe
* mingw-w64-install.exe
* postgresql-9.5.0-beta2-3-windows.exe
* PyScripter-v2.5.3-Setup.exe
* PyScripter-v2.5.3-x64-Setup.exe
* python-2.7.9.msi

Assuming the repository has been checked out to */main/jportal*. The cmake is used from */main/jportal/build*, build is in .gitignore so initially one must do a *'mkdir'*  of it and from it do a *'cmake ..'*. Once the cmake has been made and as long as all the required tools are in place, *'make'* should complete an initial build.

I am running on linux mint 17.3 Rosa, which is debian based. Installing oracle client software is quite trial and error. But I do now have the client connecting to the Developer Days stuff. Of course we use sudo alien -i ... to install the instantclient stuff. The linux mint is a superb desktop environment, IMNSHO it is the route Microsoft should have gone, but when you business model is Total Resale every 3-5 years, more the pity.

I have quite good success on a Windows 7 VM - I am not using Visual Studio for the cmake, but using mingw - using unix build - I am also using git bash for the make terminal.

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

* codeline = (~["\n","\r"])* ("\r\n" | "\n" | "\r")
* comment = "**" (~["\n","\r"])* ("\n"|"\r\n")
* digit = ["0"-"9", "#", "$"]
* dquoted = ("\"" (~["\"", "\\"] | "\\" (["\\", "\"", "\'"])?)* "\"")
* identifier = letter (letter|digit)*>
* letter = ["a"-"z", "_", "A"-"Z"]
* lidentifier = ("l"|"L") "'" letter (letter>|digit)* "'"
* number = (["1"-"9"] (["0"-"9"])*)|"0"
* squoted = ("\'" (~["\'", "\\"] | "\\" (["\\", "\"", "\'"])?)* "\'")
* string = dquoted | squoted


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
USERSTAMP |  

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

(PROC (StdProc | UserProc) | OldData | NewData

**StdProc**

BULKINSERT (number)? |  
BULKUPDATE (number)? |  
COUNT |  
DELETEALL |  
DELETEONE |  
EXISTS |  
INSERT |  
SELECTALL ((IN)? ORDER)? ((FOR)? UPDATE)? |  
SELECTONE ((FOR)? UPDATE)? |  
UPDATE  
(Options)*  

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

Standard Proc

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

User Proc

PROC name  
('**' restline)*            // Comments about the proc the generators can use  
(OPTIONS (string)+)*        // Options about the proc the generators can use  
(INPUT ('(' MULTIPLE | number ')')? (Field Definitions)+)?  
(OUTPUT ('(' SINGLE | number ')')? (Field Definitions)+)?  
SQLCODE  
(lines)+    // Each line parsed later for dynamic parameters &inp('('number')')?  
ENDCODE  

Data Blocks

SQLDATA  
(lines)+  
ENDDATA  
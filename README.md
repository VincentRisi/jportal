# jportal

Database code generators for varying platforms and vendors.
===========================================================

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

JPortal
-------

The class tree for *JPortal* starts with a single Database instance. As can be seen from
the structure below a database consists of various String properties and Vector lists.

class Database

* String name, output, server, schema, userid, password, packageName
* Vector Table tables, String flags, Sequence sequences, View views, String imports


class Table

* Database database
* String name, alias, check
* Vector Field fields, Key keys, Link links, Grant grants, View views, Proc procs, String comments, String options, String allUsers, Parameter parameters, Const consts
* boolean hasPrimaryKey, hasSequence, hasTimeStamp, hasAutoTimeStamp, hasUserStamp, hasExecute, hasSelect, hasInsert, hasDelete, hasUpdate, hasStdProcs, hasIdentity, asSequenceReturning, hasBigXML, isStoredProc  
  
class Sequence

* String  name
* int minValue, maxValue, increment, startWith
* boolean cycleFlag, orderFlag  

class View

* String name
* Vector String aliases, String lines, String users  

class Field

* String name, alias, defaultValue, checkValue
* byte type
* int length, precision, scale, bindPos, definePos
* Vector String comments, Enum enums, String valueList
* String enumLink
* boolean isPrimaryKey, isSequence, isNull, isIn, isOut,
* static final byte
  - BLOB       = 1
  - BOOLEAN    = 2
  - BYTE       = 3
  - CHAR       = 4
  - DATE       = 5
  - DATETIME   = 6
  - DOUBLE     = 7
  - DYNAMIC    = 8
  - FLOAT      = 9
  - IDENTITY   = 10
  - INT        = 11
  - LONG       = 12
  - MONEY      = 13
  - SEQUENCE   = 14
  - SHORT      = 15
  - STATUS     = 16
  - TIME       = 17
  - TIMESTAMP  = 18
  - TLOB       = 19
  - USERSTAMP  = 20
  - ANSICHAR   = 21
  - UID        = 22
  - XML        = 23
  - BIGSEQUENCE = 24
  - BIGIDENTITY = 25
  - AUTOTIMESTAMP = 26
  - WCHAR      = 27
  - WANSICHAR  = 28
  - UTF8       = 29   
  - BIGXML     = 30
* static final int DEFAULT_XML = 4096
* static final int DEFAULT_BIG_XML = 4194304


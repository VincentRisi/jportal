# jportal

Database, Server and Parameter Control code generators for varying platforms, vendors and languages.
====================================================================================================

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

JPortal RDBMS code generator
----------------------------

The class tree for *JPortal* starts with a single Database instance. As can be seen from
the structure below a database consists of various String properties and Vector lists.

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
* static final int DEFAULT_XML = 4096
* static final int DEFAULT_BIG_XML = 4194304

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
the structure below a database consists of various String properties and Vector lists.

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

* static final byte SIZE = 1, DYNAMIC = 2
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


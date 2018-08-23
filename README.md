# JPortal (or AnyDB)

## (NB: This repo is not longer maintained - see https://github.com/SI-Gen for the latest version of the code)

## Database, Module and Application code generators for varying platforms, vendors and languages.

There are three code generator types written using the same mechanism: 
  
[JPortal](generators/JPortal.html)

A generator that generates DALs (Data Access Layers) for various databases (Oracle, MS SQL Server, Postgres, SQLLite, DB2) in various languages (C#, Java, C++, Python, VB).  
  
[Crackle](generators/IDL.html)

A generator that generates IDL (Interface Definition Language) RPCs (Remote Procedure Calls) using Client/Server for various languages.  
  
[Pickle](generators/Pickle.html)

A generator that generates Parameter Control Table maintenace code. A front end written in C# that maintains any ADO.NET Database.  

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

See [Building JPortal](#buildingjportal) below.

# Building JPortal <a id="buildingjportal"></a>

## Vagrant ##

**This project now includes a vagrant file to simplify the building process**
To use vagrant, first install the vagrant tool for your platform from https://www.vagrantup.com/downloads.html
Then, from a command shell (windows or linux) run the following commands:

```bash
user@localmachine:~/jportal> vagrant up 
user@localmachine:~/jportal> vagrant ssh 
```
The vagrant up command will take a while, as  it will download a Linux VM to run the compile in,
and it will then install all the required tools.

vagrant ssh will let you SSH into the newly created and running VM.

Once you are ssh'ed into the vm, issue the following commans to build the generators:
```bash
vagrant@vagrant:~$ cd /main/jportal/
vagrant@vagrant:~$ mkdir build && cd build
vagrant@vagrant:~$ cmake ..
vagrant@vagrant:~$ make
```
That will build the generators, and place the jar files in /main/jportal/generators/bin.

If you just want to build jportal, you can issue
```bash
vagrant@vagrant:~$ make target_jportal_jar
```
or 
```bash
vagrant@vagrant:~$ make help
```
to see all possible build targets.

Lastly you can use
```bash
vagrant@vagrant:~$ make VERBOSE=1 [<target>]
```
for debug info.


To stop the VM once you are done, you can issue:
```bash
user@localmachine:~/jportal> vagrant halt
```


## Linux ##
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

## Windows 7.0 tools ##

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

There is a wrinkle - the current source is /main/jportal based on windows c:\main\jportal - this is not a cmake problem, but in the anydbmake having /main/jportal hardcoded - I will look into fixing this - but using c:\main\jportal should suffice for now. A stategic use of mklink /J should solve most problems.

I am running on linux mint 17.3 Rosa, which is debian based. Installing oracle client software is quite trial and error. But I do now have the client connecting to the Developer Days stuff. Of course we use sudo alien -i ... to install the instantclient stuff. The linux mint is a superb desktop environment, IMNSHO it is the route Microsoft should have gone, but when you business model is Total Resale every 3-5 years, more the pity.

I have quite good success on a Windows 7 VM - I am not using Visual Studio for the cmake, but using mingw - using unix build - I am also using git bash for the make terminal.
 
### JPortal RDBMS code generator <a id="jportalgenerator"></a>

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
* static final byte `BLOB=1, BOOLEAN=2, BYTE=3, CHAR=4, DATE=5, DATETIME=6, DOUBLE=7, DYNAMIC=8, FLOAT=9, IDENTITY=10, INT=11, LONG=12, MONEY=13 SEQUENCE=14, SHORT=15, STATUS=16, TIME=17, TIMESTAMP=18, TLOB=19, USERSTAMP=20, ANSICHAR=21, UID=22, XML=23, BIGSEQUENCE=24, BIGIDENTITY=25, AUTOTIMESTAMP=26, WCHAR=27, WANSICHAR=28, UTF8=29, BIGXML=30`
* static final int `DEFAULT_XML=4096`
* static final int `DEFAULT_BIG_XML=4194304`

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

### Crackle Module code generator <a id="cracklegenerator"></a>

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

* static final byte `NORMAL=0, PUBLIC=1, PRIVATE=2, PROTECTED=3`
* String name, header
* Vector String categories, Field fields
* byte codeType
* Vector String code;

**class Enumerator**

* String name
* Vector String elements

**class Prototype**

* static final byte `RPCCALL=0, PUBLIC=1, PRIVATE=2, PROTECTED=3`
* String name, message
* Type type
* Vector Field parameters, Action inputs, Action outputs, String categories
* byte codeType
* Vector String code

**class Action**

* String name
* Vector Operation operations

**class Operation**

* static final byte `SIZE=1, DYNAMIC=2`
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

* static final byte `USERTYPE=0, BOOLEAN=1, CHAR=2, SHORT=3, LONG=4, FLOAT=5, DOUBLE=6, VOID=7, BYTE=8, INT=9, STRING=10, WCHAR=11, BYVAL=1, BYPTR=2, BYREF=3, BYPTRPTR=4, BYREFPTR=5, ARRAYED=6`
* String  name
* byte typeof, reference
* boolean isUnsigned
* Vector Integer arraySizes

### Pickle Application code generator <a id="picklegenerator"></a>

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


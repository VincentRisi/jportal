# anydbMake.py

A simple make scripter for building generator based code

The python code parses a simple text script using the following syntax rules.
The script is read line by line.
The syntax used by the scripter is to make it easy to have readable scripts.

* If there is a # character anywhere on the line the rest of the line including the # char is ignored.  

* If the line contains a single token ie no white space of the form `xxxx=yyyy` this is assumed to be a set of `xxxx` with the value `yyyy` and anything of the form `${xxxx}` is replaced with `yyyy`.
Replacements are done first before any parsing is done.  

* Empty lines ie. lines containing only white space are ignored.  

* The script must start with a project name line. There can be however comment and blank lines before the project name line.

* Main keywords must be a single case sensitive word on a line. These are

 - *jportal* and *source* for the jportal generators for defining database code,
 - *crackle* and *idl* for the crackle generators for defining client, server and implementation code and
 - *pickle* and *app* for the pickle generator for parameter control code.


* After the main keyword *jportal*, *crackle* or *pickle* each non empty line must contain 2 or more items. The first item is the case sensitive name of the code generator.
The second item is the target directory where to generate the code.
Anymore items that follow are the wildcard based or target filenames in the target directory.
Wildcard markers apply to the basename sans extension of the source. eg. /fred/xyz.si would translate to xyz as the basename.

 - %a : asis-case
 - %i : ignore-case
 - %l : lowercase
 - %u : uppercase


* After the main keyword *source* each non empty lines must contain a single fullpath source file for the *jportal* generators.

* After the main keyword *idl* each non empty line must contain a keyword in ['idlfile', 'imfile', 'iifile', ibfile', 'icfile'] followed by a single fullpath filename.
 - There must be one 'idlfile' and at most one 'imfile'. The can be more than one 'ibfile', 'icfile' or 'iifile'.
 - A *jportal* generator can be used to generate any amount of 'iifile'.
If an 'iifile' is being generated then do not define it as it would be included again.
 - If there is no 'imfile' it is assumed the would only be an 'idlfile'.
 - If there is an 'imfile' it is used to create the 'idlfile' together with all supplied 'iifile' and generated 'iifile' followed by all supplied 'ibfile' and 'icfile'.
 - The single 'idlfile' supplied or built is then used by each *crackle* generator.


* After the main keyword *app* each non empty line must contain a keyword in ['appfile', 'pmfile', 'prfile', 'pifile'] followed by a single fullpath filename.

 - There must be one 'appfile' and at most one 'pmfile'. The can be more than one 'prfile' or 'pifile'.
 - A *jportal* generator can be used to generate any amount of 'pifile'. If a 'pifile' is being generated then do not define it as it would be included again.
 - If there is no 'pmfile' it is assumed the would only be an 'appfile'.
 - If there is a 'pmfile' it is used to create the 'appfile' together with all supplied 'pifile' and generated 'pifile' followed by all supplied 'prfile'.
 - The single 'idlfile' supplied or built is then used by each *pickle* generator.

The use of *idl* and *app* is very similar and they show the synergy between *jportal* and *crackle* or *pickle*.

## decompiler

Uses *jportal* to generate java code to access any Oracle database in order to decompile the code.

    #------------------------------------------
    #   DECOMPILER
    #   wildcards %a -asis-case %i -ignore-case
    #             %l -lowercase %u -uppercase
    #------------------------------------------

    project decompiler

    source_dir=/main/jportal/generators/bbd/jportal/decompiler
    binary_dir=/main/jportal/build/generators/bbd/jportal/decompiler

    jportal
      JavaWRCode ${binary_dir} %i.java

    source
      ${source_dir}/oracle.si

## binuc

Uses *crackle* to define the crackle generator to use together with the *idl* to define the server data to use for a cpp based server.

    #------------------------------------------
    #   BINU CPP SERVER
    #   wildcards %a -asis-case %i -ignore-case
    #             %l -lowercase %u -uppercase
    #------------------------------------------

    project binuc

    source_dir=/main/jportal/binuc
    binary_dir=/main/jportal/build/binuc

    crackle  
      PopUbiServer       ${binary_dir}/idl/server %lserver.cpp
      PopUbiPuffinModule ${binary_dir}/idl/module %lPython.cpp

    idl        
      idlfile  ${source_dir}/idl/binuc.idl

## binuj

Uses *crackle* to define the crackle generator to use together with the *idl* to define the server data to use for the java based server.

    #------------------------------------------
    #   BINU JAVA SERVER
    #   wildcards %a -asis-case %i -ignore-case
    #             %l -lowercase %u -uppercase
    #------------------------------------------

    project binuj

    source_dir=/main/jportal/binuj
    binary_dir=/main/jportal/build/binuj

    crackle  
      PopBinuJavaServer  ${binary_dir}/bbd/binu BinuImpl.java

    idl        
      idlfile  ${source_dir}/idl/binuj.idl

## idl2tester

Uses *jportal* to define the jportal generators to use with the *source* to generate the database handling code as well as the code snippets to use for a cpp based server, built with the *crackle* generators used together with the *idl* which defines the the server sources. Note the synergy between *jportal* and *crackle*.

    #------------------------------------------
    #   IDL2 TESTER - with ZEDZED
    #   wildcards %a -asis-case %i -ignore-case
    #             %l -lowercase %u -uppercase
    #------------------------------------------

    project idl2tester

    source_dir=/main/jportal/idl2tester
    binary_dir=/main/jportal/build/idl2tester

    jportal
      CliCCode           ${binary_dir}/sql/c           %l.cpp %l.sh
      Db2DDL             ${binary_dir}/sql/db2         %a.sql
      IdlCode            ${binary_dir}/sql/ii          %l.ii %a.cs
      PythonCliCode      ${binary_dir}/sql/python      DB_%u.py

    crackle  
      PopUbiCCIdl        ${binary_dir}/idl/ic          %l.ic
      PopUbiCSharp       ${binary_dir}/idl/inclcs      %i.cs
      PopUbiPuffinModule ${binary_dir}/idl/pymod       %lPython.cpp
      PopUbiPython       ${binary_dir}/idl/python      %u_CODE.py
      PopUbiServer       ${binary_dir}/idl/server      %lserver.cpp

    source
      ${source_dir}/sql/si/ZedZed.si
      ${source_dir}/sql/si/ZedZedBiz.si
      ${source_dir}/sql/si/ZedZedBled.si
      ${source_dir}/sql/si/ZedZedClobs.si
      ${source_dir}/sql/si/ZhedIsDhed.si

    idl        
      idlfile  ${binary_dir}/idl/idl2tester.idl
      imfile   ${source_dir}/idl/idl2tester.im
      ibfile   ${source_dir}/idl/wallap.ib

## picktester

Uses *jportal* to generate **ParmCode** `xxx.pi` files for each *source*. Uses *app* to generate *appfile* `picktester.app` using *pmfile* `picktester.pm` , *prfile* `picktester.pr` and the generated `xxx.pi` files. We also use **OracleDDL** to generate the DDL for the database.

    #------------------------------------------
    #   PICK TESTER
    #   wildcards %a -asis-case %i -ignore-case
    #             %l -lowercase %u -uppercase
    #------------------------------------------

    project picktester

    source_dir=/main/jportal/picktester
    binary_dir=/main/jportal/build/picktester

    jportal
      ParmCode   ${binary_dir}/sql/parm   %a.pi
      OracleDDL  ${binary_dir}/sql/oraDDL %a.sql

    pickle
      GenFrontEndBin ${binary_dir}/app/parm %a.bin

    source
      ${source_dir}/sql/si/accounttype.si
      ${source_dir}/sql/si/almanacbank.si
      ${source_dir}/sql/si/almanaccorrespondent.si
      ${source_dir}/sql/si/audits.si
      ${source_dir}/sql/si/bankaccount.si
      ${source_dir}/sql/si/bankcorrespondents.si
      ${source_dir}/sql/si/bankfile.si
      ${source_dir}/sql/si/country.si
      ${source_dir}/sql/si/countrycurrency.si
      ${source_dir}/sql/si/currency.si
      ${source_dir}/sql/si/dates.si
      ${source_dir}/sql/si/drivers.si
      ${source_dir}/sql/si/fieldse1.si
      ${source_dir}/sql/si/fieldsearchdef.si
      ${source_dir}/sql/si/figcorrespondentbank.si
      ${source_dir}/sql/si/finidcorrespondentrouting.si
      ${source_dir}/sql/si/group.si
      ${source_dir}/sql/si/lookup.si
      ${source_dir}/sql/si/queue.si
      ${source_dir}/sql/si/queuetype.si
      ${source_dir}/sql/si/scriptsgroup.si
      ${source_dir}/sql/si/sourcesystem.si
      ${source_dir}/sql/si/staff.si
      ${source_dir}/sql/si/staffgroup.si
      ${source_dir}/sql/si/staffqueueperm.si
      ${source_dir}/sql/si/streamfieldmsgrel.si
      ${source_dir}/sql/si/streamfieldsdef.si
      ${source_dir}/sql/si/streammessageformat.si
      ${source_dir}/sql/si/streamtype.si
      ${source_dir}/sql/si/testpack.si

    app        
      appfile  ${binary_dir}/app/parm/picktester.app
      pmfile   ${source_dir}/app/parm/picktester.pm
      prfile   ${source_dir}/app/parm/picktester.pr

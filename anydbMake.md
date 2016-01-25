# anydbMake.py - A simple make scripter for building generator based code

The python code parses a simple text script using the following syntax rules.
The script is read line by line.
If there is a # character anywhere on the line the rest of the line including the # char is ignored
If the line contains a simple xxxx=yyyy this is assumed to be a set of xxxx with the value yyyy and anything of the form ${xxxx} is replaced with yyyy. Replacements are done first before any parsing is done.
Empty line ie. lines containing only white space are ignored.
The script must start with a project name line.

## binuc  uses *crackle* to define the crackle generator to use together with the *idl* to define the server data to use for a cpp based server.

    #------------------------------------------
    #   BINU CPP SERVER
    #------------------------------------------
    
    project binuc
    
    source_dir=/main/jportal/binuc
    binary_dir=/main/jportal/build/binuc
    
    crackle  
      PopUbiServer       ${binary_dir}/idl/server %lserver.cpp
      PopUbiPuffinModule ${binary_dir}/idl/module %lPython.cpp
    
    idl        
      idlfile  ${source_dir}/idl/binuc.idl

## binuj uses *crackle* to define the crackle generator to use together with the *idl* to define the server data to use for the java based server.

    #------------------------------------------
    #   BINU JAVA SERVER
    #------------------------------------------
    
    project binuj
    
    source_dir=/main/jportal/binuj
    binary_dir=/main/jportal/build/binuj
    
    crackle  
      PopBinuJavaServer  ${binary_dir}/vlab/binu BinuImpl.java
    
    idl        
      idlfile  ${source_dir}/idl/binuj.idl

## idl2tester uses *jportal* to define the jportal generators to use with the *source* to generate the database handling code as well as the code snippets to use for a cpp based server, built with the *crackle* generators used together with the *idl* which defines the the server sources. Note the synergy between *jportal* and *crackle*.

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


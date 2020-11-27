# IDL2 doing OPENAPI using PYTHON

## Parts of **accuity.idl2**

### pragma openapi: for where the si generated yaml has been generated

``` bash
module Accuity;
version "19.07.13.0";

pragma openapi: description: Ingress3 ATP Server
pragma openapi: urlPrefix: $urlPrefix
pragma openapi: defSqlSub: $defSqlSub
pragma openapi: compSqlSub: $compSqlSub
pragma openapi: idl2Sub: $idl2Sub
pragma JSON
```

### Additional Python functions in **IDL2** source.

``` python
code
PYTHON:
from INTRINSICS import *
from DB_AUDITS import DBAudits

actions = ['ADD', 'CHANGE', 'DELETE']
ADD, CHANGE, DELETE = 0, 1, 2

def amp_fix(value, asis=False):
    ''' 
      asis=True - leaves previous escapes 
    '''
    escapes = [('&', '&amp;'), ('<','&lt;'), ('>', '&gt;'), ('"','&quot;'), ("'",'&apos;')]
    if asis == False:
        for pair in escapes:
            value = value.replace(pair[1], pair[0])
    for pair in escapes:
        value = value.replace(pair[0], pair[1])
    return value

def make_xml(name, action, fields, values):
    usid = ''
    xml = '<%s action="%s">\r\n' % (name, actions[action])
    for i, field in enumerate(fields):
        value = str(values[i]) if len(values) == len(fields) else ''
        xml += '  <%s value="%s"/>\r\n' % (field, amp_fix(value))
        if field.lower() == 'usid':
            usid = value
    xml += '</%s>' % (name)
    return usid, xml

def write_audit(name, action, fields, old, new):
    if actions[action] != 'ADD':
        usid, xmlOld = make_xml(name, action, fields, old)
    else:
        xmlOld = ' '
    if actions[action] != 'DELETE':
        usid, xmlNew = make_xml(name, action, fields, new)
    else:
        xmlNew = ' '
    audit = DBAudits(connect)
    tmStamp = '20190101000000'
    audit.runInsert(0, name, actions[action], xmlOld, xmlNew, usid, tmStamp)
endcode
```

### Contact showing post, put, patch, get, delete restful usage

Note the new **openapi: op path tag** syntax which is placed before the **input** **output** after the **message**. If the **path** contains '/' or '{...}' then it must in double quotes. See **path** in **get** **delete** operations. 

``` python
struct tAccuityContact "accuitycontact.sh"

void accuityContactInsert(tAccuityContact *newRec)
{
message: #
openapi: post accuityContact Contact
input
    newRec;
output
    newRec;
code
PYTHON:
    try:
        write_audit('AccuityContact', ADD, newRec._fields(), None, newRec._data())
        newRec.execInsert()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
        connect.rollback()
    return newRec
endcode
}

void accuityContactUpdate(tAccuityContact *newRec)
{
message: #
openapi: put accuityContact Contact
input
    newRec;
output
    newRec;
code
PYTHON:
    try:
        oldRec = DBAccuityContact(connect)
        oldRec.Id = newRec.Id
        oldRec.execSelectOne()
        write_audit('AccuityContact', CHANGE, newRec._fields(), oldRec._data(), newRec._data())
        newRec.execUpdate()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
        connect.rollback()
    return newRec
endcode
}

struct tAccuityContactUpdateAllStatus "accuitycontact.sh"

void accuityContactUpdateAllStatus(int status, char* usId)
{
message: #
openapi: patch accuityContact Contact
input
  status;
  usId;
code
PYTHON:
    try:
        rec = DBAccuityContactUpdateAllStatus(connect)
        rec.runUpdateAllStatus(status, usId)
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
endcode
}

void accuityContactSelectOne(int Id, tAccuityContact *rec)
{
message: #
openapi: get "accuityContact/Id/{Id}" Contact
input
    Id;
output
    rec;
code
PYTHON:
    try:
        rec = DBAccuityContact(connect)
        rec.Id = Id
        rec.execSelectOne()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
    return rec
endcode
}

struct tAccuityContactDeleteOne "accuitycontact.sh"

void accuityContactDeleteOne(int Id)
{
message: #
openapi: delete "accuityContact/Id/{Id}" Contact
input
    Id;
code
PYTHON:
    try:
        rec = DBAccuityContactDeleteOne(connect)
        rec.Id = Id
        rec.execDeleteOne()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
endcode
}

struct tAccuityContactExists "accuitycontact.sh"

int accuityContactExists(int Id)
{
message: #
openapi: get "accuityContactExists/Id/{Id}" Contact
input
    Id;
code
PYTHON:
    try:
        rec = DBAccuityContactExists(connect)
        if rec.readExists(Id) == 1:
            return rec.Count
        else:
            return 0
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
endcode
}
```

## CMakeLists.txt for building the **pocioci**

``` cmake
project (putty3.idl2.accuity.sql_build)

pathed(iiDir       ${ACCUITY_BINARY_DIR}/ii)
pathed(shDir       ${ACCUITY_BINARY_DIR}/sh)
pathed(soDir       ${ACCUITY_BINARY_DIR}/so)
pathed(sqlDir      ${ACCUITY_BINARY_DIR}/sql)
pathed(binDir      ${ACCUITY_BINARY_DIR}/bin)
pathed(logDir      ${ACCUITY_BINARY_DIR}/log)
pathed(pyDir       ${ACCUITY_BINARY_DIR}/py)
pathed(pyDBADir    ${ACCUITY_BINARY_DIR}/pydba)
pathed(piDir       ${ACCUITY_BINARY_DIR}/pi)

set (switches 
  "ConnReqd=0"
  "OneSQLScript=1"
  "Internal=1"
  "I3Gen=0"
  "Python3=1"
  "XmlValue=1"
  "ControlDB=npud00"
  "UConnect=npu"
  "C|${shDir}|.sh|SH Files"
  "SO|${soDir}|.so"
  "SQL|${sqlDir}|.sql|SQL Files"
  "LogDir=${logDir}"
  "Python|${pyDir}|.py|PY_Files"
  "PyDBApi|${pyDBADir}|.py|DB_Api"
  "ParmsDir=${piDir}"
)

if (WIN32)
  pathed(csDir       ${ACCUITY_BINARY_DIR}/cs)
  pathed(swaggerDir  ${ACCUITY_BINARY_DIR}/yaml2)
  pathed(openapiDir  ${ACCUITY_BINARY_DIR}/yaml3)
  list (APPEND switches
    "CSIDL2|${csDir}|.cs|CS_Files"
    "CSNet7DIR=${csDir}"
    "Swagger|${swaggerDir}|.yaml|Swagger"
    "Openapi|${openapiDir}|.yaml|OpenApi"
    )
endif ()

set (accuity_list
  accuityroutingcode.si
  accuitycontact.si
  accuitycorrespondent.si
  accuitycorrespondentsub.si
  accuitylocation.si
  accuityofficer.si
  accuityroutingatt.si
  audits.si
  bankfile.si
)

set (accuity_sifiles)
foreach (name ${accuity_list})
  list (APPEND accuity_sifiles ${MCPE_SOURCE_DIR}/sql/si/${name})
endforeach()

dbportal2(accuity_sql_build accuity "${accuity_sifiles}" ${switches})

set_property(TARGET accuity_sql_build  PROPERTY FOLDER "putty3/idl2/accuity/sql_build")
```

## CMakeLists.txt for building the IDL2

Note the configFile that is being generated for the IDL2 **PopHTTP...** generators to be able to generate the swagger and openapi yaml files.

``` cmake
project (putty3.idl2.accuity.idl2_build)

pathed (genServerDir     ${ACCUITY_BINARY_DIR}/server/idl)
pathed (genPythonDir     ${ACCUITY_BINARY_DIR}/server/py)
pathed (genPythonTreeDir ${ACCUITY_BINARY_DIR}/tree)
pathed (genHttpServerDir ${ACCUITY_BINARY_DIR}/http)

set (switches
  "${genServerDir}|PopGenServer|Server Files"
  "${genHttpServerDir}|PopHTTPServerFull|HTTP Server Files"
  "${genHttpServerDir}|PopHTTPSwaggerFull|HTTP Swagger Files"
  "${genHttpServerDir}|PopHTTPOpenApiFull|HTTP Swagger Files"
  "${genHttpServerDir}|PopHTTPPython|HTTP Swagger Files"
  "${genPythonDir}|PopGenPython|PY_Files"
  "${genPythonTreeDir}|PopGenPythonTree|PY_Files"
)

if (WIN32)
  pathed (genCSharpDir     ${ACCUITY_BINARY_DIR}/client/cs)
  list (APPEND switches
    "${genCSharpDir}|PopGenCSharp|CSharp Files"
    )
endif ()

set (configFile ${genHttpServerDir}/accuity.properties)
file (WRITE ${configFile})
file (APPEND ${configFile} "urlPrefix=${ACCUITY_BINARY_DIR}\n")
file (APPEND ${configFile} "defSqlSub=/yaml2\n")
file (APPEND ${configFile} "compSqlSub=/yaml3\n")

set (idlFile ${CMAKE_CURRENT_SOURCE_DIR}/accuity.idl2)
idl2_simple(accuity_idl2_build ${idlFile} ${switches})

add_dependencies (accuity_idl2_build accuity_sql_build)
set_property(TARGET accuity_idl2_build PROPERTY FOLDER "putty3/idl2/accuity/idl2_build")
```

## Openapi code generated in yaml

I think **yaml** is far more human readable than **json**. (Not showing the full code generated.)

``` yaml
openapi: "3.0.0"
info:
  title: Accuity
  description: Ingress3 ATP Server
  version: "19.07.13.0"
components:
  schemas:
    tError:
      type: object
      properties:
        error:
          type: string
          maxLength: 4096
```
The Contact schemas:

``` yaml
    tAccuityContact:
      type: object
      properties:
        Id:
          type: integer
          format: int32
        LocationId:
          type: integer
          format: int32
        Department:
          type: string
          maxLength: 5
        ContactType:
          type: string
          maxLength: 15
        ContactInfo:
          type: string
          maxLength: 100
        Status:
          type: integer
          format: int8
        USId:
          type: string
          maxLength: 16
        TmStamp:
          type: string
          maxLength: 14
      required:
        - Id
        - LocationId
        - Status
    tAccuityContactUpdateAllStatus:
      type: object
      properties:
        Status:
          type: integer
          format: int8
        USId:
          type: string
          maxLength: 16
      required:
        - Status
    tAccuityContactExists:
      type: object
      properties:
        Count:
          type: integer
          format: int32
        Id:
          type: integer
          format: int32
      required:
        - Count
        - Id
```
The Contact paths:

``` yaml
  /accuityContact:
    post:
      tags:
        - Contact
      operationId: accuityContactInsert
      summary: accuityContactInsert
      parameters:
        - name: newRec
          in: query
          description: newRec
          required: true
          schema:
            $ref: '#/components/schemas/tAccuityContact'
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                title: accuityContactInsert_response
                type: object
                properties:
                  newRec:
                    $ref: '#/components/schemas/tAccuityContact'
                required:
                  - newRec
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
    put:
      tags:
        - Contact
      operationId: accuityContactUpdate
      summary: accuityContactUpdate
      parameters:
        - name: newRec
          in: query
          description: newRec
          required: true
          schema:
            $ref: '#/components/schemas/tAccuityContact'
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                title: accuityContactUpdate_response
                type: object
                properties:
                  newRec:
                    $ref: '#/components/schemas/tAccuityContact'
                required:
                  - newRec
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
    patch:
      tags:
        - Contact
      operationId: accuityContactUpdateAllStatus
      summary: accuityContactUpdateAllStatus
      parameters:
        - name: status
          in: query
          description: status
          required: true
          schema:
            type: integer
            format: int32
        - name: usId
          in: query
          description: usId
          required: true
          schema:
            type: string
      responses:
        '204':
          description: No response here
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
  /accuityContact/Id/{Id}:
    get:
      tags:
        - Contact
      operationId: accuityContactSelectOne
      summary: accuityContactSelectOne
      parameters:
        - name: Id
          in: path
          description: Id
          required: true
          schema:
            type: integer
            format: int32
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                title: accuityContactSelectOne_response
                type: object
                properties:
                  rec:
                    $ref: '#/components/schemas/tAccuityContact'
                required:
                  - rec
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
    delete:
      tags:
        - Contact
      operationId: accuityContactDeleteOne
      summary: accuityContactDeleteOne
      parameters:
        - name: Id
          in: path
          description: Id
          required: true
          schema:
            type: integer
            format: int32
      responses:
        '204':
          description: No response here
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
  /accuityContactExists/Id/{Id}:
    get:
      tags:
        - Contact
      operationId: accuityContactExists
      summary: accuityContactExists
      parameters:
        - name: Id
          in: path
          description: Id
          required: true
          schema:
            type: integer
            format: int32
      responses:
        '200':
          description: Success
          content:
            application/json:
              schema:
                title: accuityContactExists_response
                type: object
                properties:
                  returnCode:
                    type: integer
                    format: int32
                required:
                  - returnCode
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/tError'
```
## Additional support code from IDL2 source in **ACCUITYIML.py**.

``` python
actions = ['ADD', 'CHANGE', 'DELETE']
ADD, CHANGE, DELETE = 0, 1, 2

def amp_fix(value, asis=False):
    ''' 
      asis=True - leaves previous escapes 
    '''
    escapes = [('&', '&amp;'), ('<','&lt;'), ('>', '&gt;'), ('"','&quot;'), ("'",'&apos;')]
    if asis == False:
        for pair in escapes:
            value = value.replace(pair[1], pair[0])
    for pair in escapes:
        value = value.replace(pair[0], pair[1])
    return value

def make_xml(name, action, fields, values):
    usid = ''
    xml = '<%s action="%s">\r\n' % (name, actions[action])
    for i, field in enumerate(fields):
        value = str(values[i]) if len(values) == len(fields) else ''
        xml += '  <%s value="%s"/>\r\n' % (field, amp_fix(value))
        if field.lower() == 'usid':
            usid = value
    xml += '</%s>' % (name)
    return usid, xml

def write_audit(name, action, fields, old, new):
    if actions[action] != 'ADD':
        usid, xmlOld = make_xml(name, action, fields, old)
    else:
        xmlOld = ' '
    if actions[action] != 'DELETE':
        usid, xmlNew = make_xml(name, action, fields, new)
    else:
        xmlNew = ' '
    audit = DBAudits(connect)
    tmStamp = '20190101000000'
    audit.runInsert(0, name, actions[action], xmlOld, xmlNew, usid, tmStamp)
```
## Contact Python code generated in **ACCUITYIMPL.py**

``` python
def accuityContactInsert(newRec: DBAccuityContact) -> DBAccuityContact: # newRec
    try:
        write_audit('AccuityContact', ADD, newRec._fields(), None, newRec._data())
        newRec.execInsert()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
        connect.rollback()
    return newRec

def accuityContactUpdate(newRec: DBAccuityContact) -> DBAccuityContact: # newRec
    try:
        oldRec = DBAccuityContact(connect)
        oldRec.Id = newRec.Id
        oldRec.execSelectOne()
        write_audit('AccuityContact', CHANGE, newRec._fields(), oldRec._data(), newRec._data())
        newRec.execUpdate()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
        connect.rollback()
    return newRec

def accuityContactUpdateAllStatus(status: int, usId: str) : 
    try:
        rec = DBAccuityContactUpdateAllStatus(connect)
        rec.runUpdateAllStatus(status, usId)
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))

def accuityContactSelectOne(Id: int) -> DBAccuityContact: # rec
    try:
        rec = DBAccuityContact(connect)
        rec.Id = Id
        rec.execSelectOne()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
    return rec

def accuityContactDeleteOne(Id: int) : 
    try:
        rec = DBAccuityContactDeleteOne(connect)
        rec.Id = Id
        rec.execDeleteOne()
        connect.commit()
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))

def accuityContactExists(Id: int) -> int: # returnCode
    try:
        rec = DBAccuityContactExists(connect)
        if rec.readExists(Id) == 1:
            return rec.Count
        else:
            return 0
    except DBError as db:
        log_error ('DBError: value:{0} rc:{1} ociErr:{2}'.format(db.value, db.rc, db.ociErr))
```
## Python code **http_makes.py**

``` python
import yaml, json, requests
from io import BytesIO, StringIO

headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}

class HttpError(BaseException):
    def __init__(self, message, error, url):
        self.message = message
        self.error = error
        self.url = url
    def __str__(self):
        return repr(self.message)

def make_url(host, port):
    return f'http://{host}:{port}'

def make_path(obj, mask):
    annotations = obj.__annotations__
    for key in annotations:
        x = getattr(obj, key)
        mask = mask.replace('{%s}' % (key), str(x))
    return mask
    
def make_data(cls, yaml_only = False):
    def to_yaml(cls, data, ind, is_list=False):
        indent = '  ' * ind
        plus = ''
        if is_list == True: 
            plus = '- '
        annotations = getattr(cls, '__annotations__')
        for key in annotations:
            annote = annotations[key]
            value = getattr(cls, key)
            if annote is str:
                data.write(f'{indent}{plus}{key}: "{value}"\n')
            elif annote is int:
                data.write(f'{indent}{plus}{key}: {value}\n')
            elif annote is float:
                data.write(f'{indent}{plus}{key}: {value}\n')
            elif type(value) is list:
                data.write(f'{indent}{plus}{key}:\n')
                for v in value:
                    to_yaml(v, data, ind+1, True)
            else:
                data.write(f'{indent}{plus}{key}:\n')
                to_yaml(value, data, ind+1)
            if is_list == True: plus = '  '
    with StringIO() as dataIO:
        to_yaml(cls, dataIO, 0)
        result = dataIO.getvalue()
    if yaml_only == True:    
        print (result)
        return result    
    else:    
        dict = yaml.load(result, Loader=yaml.FullLoader)
        data = json.dumps(dict)
        #print (data)
        return data

def set_error(cls):
    global tError
    tError = cls

def load_response(cls, response):
    def to_cls(cls, data):
        annotations = getattr(cls, '__annotations__')
        for key in annotations:
            if not key in data:
                print (f'{key} missing in data')
                continue
            annote = annotations[key]
            value = data[key]
            if annote is str or annote is int or annote is float:
                setattr(cls, key, value)
            elif type(annote) is list:
                occurs = []
                if value is not None:
                    for i, occur in enumerate(annote):
                        inst = occur()
                        rec = value[i]
                        to_cls(inst, rec)
                        occurs.append(inst)
                setattr(cls, key, occurs)
            else:
                inst = annote()
                to_cls(inst, data[key])
                setattr(cls, key, inst)
    status_code = response.status_code
    if status_code == 400:
        err = tError()
        err.error = response.text
        raise HttpError(err, response.text, response.url)
    data = json.loads(response.text)
    to_cls(cls, data)

def display(cls):
    make_data(cls, True)
```
## Python code in generated **accuity.py**
### Top description and tError class

``` python
# This code was generated, do not modify it, modify it at source and regenerate it.
module_name = 'Accuity'
description = 'Ingress3 ATP Server'
version = "19.07.13.0"

from http_makes import *

class tError:
    error: str
    def __init__(self):
        self.error = ''
```
### Classes for Contact

``` python
class tAccuityContact:
    Id: int
    LocationId: int
    Department: str
    ContactType: str
    ContactInfo: str
    Status: int
    USId: str
    TmStamp: str
    def __init__(self):
        self.Id = 0
        self.LocationId = 0
        self.Department = ''
        self.ContactType = ''
        self.ContactInfo = ''
        self.Status = 0
        self.USId = ''
        self.TmStamp = ''

class tAccuityContactUpdateAllStatus:
    Status: int
    USId: str
    def __init__(self):
        self.Status = 0
        self.USId = ''

class tAccuityContactExists:
    Count: int
    Id: int
    def __init__(self):
        self.Count = 0
        self.Id = 0
```
### Request and Response code functions

``` python
class accuityContactInsert_request:
    newRec: tAccuityContact
    def __init__(self):
        self.newRec = tAccuityContact()
    def post(self, host, port):
        path = 'accuityContact'
        data = make_data(self)
        url = make_url(host, port)
        response = requests.post(f'{url}/{path}', data=data, headers=headers)
        return response

class accuityContactInsert_response:
    newRec: tAccuityContact
    def __init__(self):
        self.newRec = tAccuityContact()
    def load(self, response):
        load_response(self, response)

class accuityContactUpdate_request:
    newRec: tAccuityContact
    def __init__(self):
        self.newRec = tAccuityContact()
    def put(self, host, port):
        path = 'accuityContact'
        data = make_data(self)
        url = make_url(host, port)
        response = requests.put(f'{url}/{path}', data=data, headers=headers)
        return response

class accuityContactUpdate_response:
    newRec: tAccuityContact
    def __init__(self):
        self.newRec = tAccuityContact()
    def load(self, response):
        load_response(self, response)

class accuityContactUpdateAllStatus_request:
    status: int
    usId: str
    def __init__(self):
        self.status = 0
        self.usId = ''
    def patch(self, host, port):
        path = 'accuityContact'
        data = make_data(self)
        url = make_url(host, port)
        response = requests.patch(f'{url}/{path}', data=data, headers=headers)
        return response

class accuityContactSelectOne_request:
    Id: int
    def __init__(self):
        self.Id = 0
    def get(self, host, port):
        params = {}
        path = make_path(self, 'accuityContact/Id/{Id}')
        url = make_url(host, port)
        response = requests.get(f'{url}/{path}', params=params, headers=headers)
        return response

class accuityContactSelectOne_response:
    rec: tAccuityContact
    def __init__(self):
        self.rec = tAccuityContact()
    def load(self, response):
        load_response(self, response)

class accuityContactDeleteOne_request:
    Id: int
    def __init__(self):
        self.Id = 0
    def delete(self, host, port):
        params = {}
        path = make_path(self, 'accuityContact/Id/{Id}')
        url = make_url(host, port)
        response = requests.delete(f'{url}/{path}', params=params, headers=headers)
        return response

class accuityContactExists_request:
    Id: int
    def __init__(self):
        self.Id = 0
    def get(self, host, port):
        params = {}
        path = make_path(self, 'accuityContactExists/Id/{Id}')
        url = make_url(host, port)
        response = requests.get(f'{url}/{path}', params=params, headers=headers)
        return response

class accuityContactExists_response:
    returnCode: int
    def __init__(self):
        self.returnCode = 0
    def load(self, response):
        load_response(self, response)
```
## Python test requests using **accuity.py** generated by **PopHttpPythonCode.java**

### **test_requests.py**

``` python
import sys
import requests
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-H', '--host',    type=str, default='localhost')
parser.add_argument('-P', '--port',    type=str, default='32135')
parser.add_argument('-b', '--build',   type=str, default=r'C:\nedbank\ownfronts\mcpe-single\source\putty3\idl2\accuity\genned\http')
parser.add_argument('-s', '--support', type=str, default=r'C:\nedbank\ownfronts\mcpe-single\build\tools\pyscripts')
args = parser.parse_args()
host = args.host
port = args.port
build = args.build
support = args.support

sys.path.append(build)
sys.path.append(support)
import accuity
HttpError = accuity.HttpError

def do_ping():
    print ('ping')
    print ('****')
    req = accuity.ping_request()
    req.given = 11
    req.add = 1
    try:
       response = req.get(host, port)
       rsp = accuity.ping_response()
       rsp.load(response)
       accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')        

def do_pong():
    print ('pong')
    print ('****')
    req = accuity.pong_request()
    req.given = 11
    req.add = 1
    try:
       response = req.get(host, port)
       rsp = accuity.pong_response()
       rsp.load(response)
       accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')        

def do_pang():
    print ('pang')
    print ('****')
    req = accuity.pang_request()
    req.given = 11
    req.add = 1
    req.gotten = 'Mein'
    try:
        response = req.get(host, port)
        rsp = accuity.pang_response()
        rsp.load(response)
        accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')  
        
def do_Contact_post():
    print ('Contact_post')
    print ('************')
    req = accuity.accuityContactInsert_request()
    rsp = accuity.accuityContactInsert_response()
    req.newRec = accuity.tAccuityContact()
    req.newRec.Id = 2
    req.newRec.LocationId = 10000010
    req.newRec.Department = 'MAIN'
    req.newRec.ContactType = 'TEL'
    req.newRec.ContactInfo = '1-479-271-1363'
    req.newRec.Status = 0
    req.newRec.USId = 'TESTREQ'
    req.newRec.TmStamp = '20200701'
    try:
        response = req.post(host, port)
        rsp.load(response)
        accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')  

def do_Contact_put():
    print ('Contact_put')
    print ('***********')
    req = accuity.accuityContactUpdate_request()
    rsp = accuity.accuityContactUpdate_response()
    req.newRec = accuity.tAccuityContact()
    req.newRec.Id = 2
    req.newRec.LocationId = 10000010
    req.newRec.Department = 'MAINY'
    req.newRec.ContactType = 'TEL'
    req.newRec.ContactInfo = '1-479-271-1363'
    req.newRec.Status = 0
    req.newRec.USId = 'TESTREQ'
    req.newRec.TmStamp = '20200701'
    try:
        response = req.put(host, port)
        rsp.load(response)
        accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')

def do_Contact_patch():
    print ('Contact_patch')
    print ('*************')
    req = accuity.accuityContactUpdateAllStatus_request()
    req.status = 0
    req.usId = 'TESTREQ'
    try:
        response = req.patch(host, port)
        print (response.status_code)
    except HttpError as e:
        print (f'{e.url}: {e.error}')

def do_Contact_get():
    print ('Contact_get')
    print ('***********')
    req = accuity.accuityContactSelectOne_request()
    rsp = accuity.accuityContactSelectOne_response()
    req.Id = 2
    try:
        response = req.get(host, port)
        rsp.load(response)
        accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')

def do_Contact_delete():
    print ('Contact_delete')
    print ('**************')
    req = accuity.accuityContactDeleteOne_request()
    req.Id = 2
    try:
        response = req.delete(host, port)
        print (response.status_code)
    except HttpError as e:
        print (f'{e.url}: {e.error}')

def do_ContactExists_get():
    print ('ContactExists_get')
    print ('*****************')
    req = accuity.accuityContactExists_request()
    rsp = accuity.accuityContactExists_response()
    req.Id = 2
    try:
        response = req.get(host, port)
        rsp.load(response)
        accuity.display(rsp)
    except HttpError as e:
        print (f'{e.url}: {e.error}')

def main():
    do_ping()
    do_pong()
    do_pang()
    do_Contact_post()
    do_Contact_put()
    do_Contact_patch()
    do_ContactExists_get()
    do_Contact_get()
    do_Contact_delete()

if __name__ == '__main__':
    main()
```

### results

``` bash
ping
****
returnCode: 12

pong
****
returnCode: 12

pang
****
returnCode: 12

Contact_post
************
newRec:
  Id: 2
  LocationId: 10000010
  Department: "MAIN"
  ContactType: "TEL"
  ContactInfo: "1-479-271-1363"
  Status: 0
  USId: "TESTREQ"
  TmStamp: "20201126130435"

Contact_put
***********
newRec:
  Id: 2
  LocationId: 10000010
  Department: "MAINY"
  ContactType: "TEL"
  ContactInfo: "1-479-271-1363"
  Status: 0
  USId: "TESTREQ"
  TmStamp: "20201126130436"

Contact_patch
*************
204
ContactExists_get
*****************
returnCode: 1

Contact_get
***********
rec:
  Id: 2
  LocationId: 10000010
  Department: "MAINY"
  ContactType: "TEL"
  ContactInfo: "1-479-271-1363"
  Status: 0
  USId: "TESTREQ"
  TmStamp: "20201126130437"

Contact_delete
**************
204
```

### Server log

``` bash
20201126130432.471 DBG  HTTPRequest started
20201126130432.481 DBG  HTTP/1.1 in header not dealt with here
20201126130432.481 DBG  User-Agent: python-requests/2.24.0
20201126130432.482 DBG  ping/given/11/add/1
20201126130432.483 DBG  Received:
11|1
20201126130432.484 DBG  Transmit:
{"returnCode": 12}
20201126130432.486 DBG  HTTPRequest ended
20201126130433.505 DBG  HTTPRequest started
20201126130433.516 DBG  HTTP/1.1 in header not dealt with here
20201126130433.517 DBG  User-Agent: python-requests/2.24.0
20201126130433.520 DBG  pong/given/11/add/1
20201126130433.521 DBG  Received:
11|1
20201126130433.524 DBG  Transmit:
{
        "returnCode" : 12
}
20201126130433.532 DBG  HTTPRequest ended
20201126130434.560 DBG  HTTPRequest started
20201126130434.571 DBG  HTTP/1.1 in header not dealt with here
20201126130434.573 DBG  User-Agent: python-requests/2.24.0
20201126130434.575 DBG  pang/given/11/add/1/and/Mein
20201126130434.578 DBG  Received:
11|1|Mein
20201126130434.588 INF  Mein
20201126130434.590 DBG  Transmit:
{"returnCode": 12}
20201126130434.600 DBG  HTTPRequest ended
20201126130435.647 DBG  HTTPRequest started
20201126130435.659 DBG  HTTP/1.1 in header not dealt with here
20201126130435.660 DBG  User-Agent: python-requests/2.24.0
20201126130435.664 DBG  Received:
{"newRec": {"Id": 2, "LocationId": 10000010, "Department": "MAIN", "ContactType": "TEL", "ContactInfo": "1-479-271-1363", "Status": 0, "USId": "TESTREQ", "TmStamp": "20200701"}}
20201126130435.684 DBG  Transmit:
{"newRec": {"Id": 2, "LocationId": 10000010, "Department": "MAIN", "ContactType": "TEL", "ContactInfo": "1-479-271-1363", "Status": 0, "USId": "TESTREQ", "TmStamp": "20201126130435"}}
20201126130435.685 DBG  HTTPRequest ended
20201126130436.721 DBG  HTTPRequest started
20201126130436.731 DBG  HTTP/1.1 in header not dealt with here
20201126130436.732 DBG  User-Agent: python-requests/2.24.0
20201126130436.734 DBG  Received:
{"newRec": {"Id": 2, "LocationId": 10000010, "Department": "MAINY", "ContactType": "TEL", "ContactInfo": "1-479-271-1363", "Status": 0, "USId": "TESTREQ", "TmStamp": "20200701"}}
20201126130436.753 DBG  Transmit:
{"newRec": {"Id": 2, "LocationId": 10000010, "Department": "MAINY", "ContactType": "TEL", "ContactInfo": "1-479-271-1363", "Status": 0, "USId": "TESTREQ", "TmStamp": "20201126130436"}}
20201126130436.755 DBG  HTTPRequest ended
20201126130437.772 DBG  HTTPRequest started
20201126130437.782 DBG  HTTP/1.1 in header not dealt with here
20201126130437.783 DBG  User-Agent: python-requests/2.24.0
20201126130437.784 DBG  Received:
{"status": 0, "usId": "TESTREQ"}
20201126130450.680 DBG  HTTPRequest ended
20201126130451.696 DBG  HTTPRequest started
20201126130451.707 DBG  HTTP/1.1 in header not dealt with here
20201126130451.708 DBG  User-Agent: python-requests/2.24.0
20201126130451.709 DBG  accuityContactExists/Id/2
20201126130451.709 DBG  Received:
2
20201126130451.716 DBG  Transmit:
{"returnCode": 1}
20201126130451.717 DBG  HTTPRequest ended
20201126130452.734 DBG  HTTPRequest started
20201126130452.744 DBG  HTTP/1.1 in header not dealt with here
20201126130452.745 DBG  User-Agent: python-requests/2.24.0
20201126130452.746 DBG  accuityContact/Id/2
20201126130452.748 DBG  Received:
2
20201126130452.756 DBG  Transmit:
{"rec": {"Id": 2, "LocationId": 10000010, "Department": "MAINY", "ContactType": "TEL", "ContactInfo": "1-479-271-1363", "Status": 0, "USId": "TESTREQ", "TmStamp": "20201126130437"}}
20201126130452.757 DBG  HTTPRequest ended
20201126130453.774 DBG  HTTPRequest started
20201126130453.785 DBG  HTTP/1.1 in header not dealt with here
20201126130453.785 DBG  User-Agent: python-requests/2.24.0
20201126130453.786 DBG  accuityContact/Id/2
20201126130453.787 DBG  Received:
2
20201126130453.799 DBG  HTTPRequest ended
```

## Server test using Python with requests and **json** based input

### **http_run.py** showing only Contact and pings

``` python
accuityContact_delete = "accuityContact/Id/2"
accuityContact_get = "accuityContact/Id/2"
accuityContact_patch = '''\
{
  "status": 0,
  "usId": "tests"
}
'''
accuityContact_post = '''\
{
  "newRec": {
    "Id": 2,
    "LocationId": 10000010,
    "Department": "MAIN",
    "ContactType": "TEL",
    "ContactInfo": "1-479-271-1363",
    "Status": 0,
    "USId": "##er",
    "TmStamp": "20200701"
  }
}
'''
accuityContact_put = '''\
{
  "newRec": {
    "Id": 2,
    "LocationId": 10000010,
    "Department": "MAIN",
    "ContactType": "TEL",
    "ContactInfo": "1-479-127-1363 EXT 124",
    "Status": 0,
    "USId": "##er",
    "TmStamp": "20200701"
  }
}
'''
## -----------------------------------------------------
pang_get = "pang/given/14/add/3/and/1.4"
ping_get = "ping/given/13/add/12/add/2"
pong_get = "pong/given/12/add/2"
```

### **accuity_requests.py**

``` python
import requests
import argparse
import http_run

parser = argparse.ArgumentParser()
parser.add_argument('-H', '--host', type=str, default='localhost')
parser.add_argument('-P', '--port', type=str, default='32135')
parser.add_argument('rest', nargs='*', type=str)
args = parser.parse_args()
rest = args.rest
host = args.host
port = args.port
headers = {'Accept': 'application/json', 'Content-Type': 'application/json'}

routines = []
for routine in dir(http_run):
    if routine[0] != '_':
        routines.append(routine)

def split_path(data):
    params = {}
    path = data
    p1 = data.split('?')
    if len(p1) == 2:
        path, parms = p1
        p2 = parms.split('&')
        for parm in p2:
            p3 = parm.split('=')
            if len(p3) == 2:
                params[p3[0]] = p3[1]
    return path, params

def get(data):
    path, params = split_path(data)
    print (path, params)
    r = requests.get(f'http://{host}:{port}/{path}', params=params, headers=headers)
    print (r.text)
    print (r.url)
    #rec = r.json()
    #print (rec)

def delete(data):
    path, params = split_path(data)
    print (path, params)
    r = requests.delete(f'http://{host}:{port}/{path}', params=params, headers=headers)
    print (r.text)

def options(path, data):
    if '{' in data:
        print (path, data)
        r = requests.options(f'http://{host}:{port}/{path}', data=data, headers=headers)
        print (r.text)
    else:
        path, params = split_path(data)
        print (path, params)
        r = requests.options(f'http://{host}:{port}/{path}', params=params, headers=headers)
        print (r.text)

def head(path, data):
    if '{' in data:
        print (path, data)
        r = requests.head(f'http://{host}:{port}/{path}', data=data, headers=headers)
        print (r.text)
    else:
        path, params = split_path(data)
        print (path, params)
        r = requests.head(f'http://{host}:{port}/{path}', params=params, headers=headers)
        print (r.text)

def post(path, data):
    if '{' in data:
        print (path, data)
        r = requests.post(f'http://{host}:{port}/{path}', data=data, headers=headers)
        print (r.text)

def put(path, data):
    if '{' in data:
        print (path, data)
        r = requests.put(f'http://{host}:{port}/{path}', data=data, headers=headers)
        print (r.text)

def patch(path, data):
    if '{' in data:
        print (path, data)
        r = requests.patch(f'http://{host}:{port}/{path}', data=data, headers=headers)
        print (r.text)

def main():
    for routine in routines:
        count = 0  
        for opt in rest:
            if not opt in routine:
                continue
            count += 1
        if count == len(rest):
            parts = routine.split('_')
            type = parts[-1]
            path = '_'.join(parts[0:-1])
            data = eval(f'http_run.{routine}')
            try:
                if type == 'get':
                    get(data)
                elif type == 'delete':
                    delete(data)
                elif type == 'options':
                    options(path, data)
                elif type == 'head':
                    head(path, data)
                elif type == 'post':
                    post(path, data)
                elif type == 'put':
                    put(path, data)
                elif type == 'patch':
                    patch(path, data)
            except Exception as ex:
                print (type, path, data, ex.args, "FAILED")
                pass

if __name__ == '__main__':
    main()

```
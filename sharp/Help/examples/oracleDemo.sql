CONNECT scott/tiger@orcl

DROP TABLE Contacts CASCADE CONSTRAINTS;

CREATE TABLE Contacts
( id NUMBER(10)
, name VARCHAR2(50)
, whenDone DATE
, whoDoneIt VARCHAR2(8)
);

DROP PUBLIC SYNONYM Contacts;

CREATE PUBLIC SYNONYM Contacts FOR scott.Contacts;

DROP SEQUENCE ContactsSeq;

CREATE SEQUENCE ContactsSeq
  MINVALUE 1
  MAXVALUE 999999999
  CYCLE
  ORDER;

DROP PUBLIC SYNONYM ContactsSEQ;

CREATE PUBLIC SYNONYM ContactsSEQ FOR scott.ContactsSEQ;

insert into Contacts values(ContactsSeq.NextVal, 'Contact 1', sysdate, user)
insert into Contacts values(ContactsSeq.NextVal, 'Contact 2', sysdate, user)
insert into Contacts values(ContactsSeq.NextVal, 'Contact 3', sysdate, user)
commit
select * from Contacts

ALTER TABLE Contacts
MODIFY
( id CONSTRAINT Contacts_NN01 NOT NULL
, name CONSTRAINT Contacts_NN02 NOT NULL
, whenDone CONSTRAINT Contacts_NN03 NOT NULL
, whoDoneIt CONSTRAINT Contacts_NN04 NOT NULL
);

ALTER TABLE Contacts
ADD
( CONSTRAINT CONTACTSKEY1 PRIMARY KEY
  ( id
  )
, CONSTRAINT CONTACTSKEY2 UNIQUE
  ( name
  )
);

DROP TABLE Master CASCADE CONSTRAINTS;

CREATE TABLE Master
( id NUMBER(10)
, name VARCHAR2(50)
, addr1 VARCHAR2(50)
, addr2 VARCHAR2(50)
, addr3 VARCHAR2(50)
, phone VARCHAR2(20)
, contact NUMBER(10)
, whenDone DATE
, whoDoneIt VARCHAR2(8)
);

DROP PUBLIC SYNONYM Master;

CREATE PUBLIC SYNONYM Master FOR scott.Master;

DROP SEQUENCE MasterSeq;

CREATE SEQUENCE MasterSeq
  MINVALUE 1
  MAXVALUE 999999999
  CYCLE
  ORDER;

DROP PUBLIC SYNONYM MasterSEQ;

CREATE PUBLIC SYNONYM MasterSEQ FOR scott.MasterSEQ;

CREATE OR REPLACE FORCE VIEW MasterView1
( id
, name
, addr1
, addr2
, addr3
, phone
, contact
) AS
(
select master.id
, master.name
, master.addr1
, master.addr2
, master.addr3
, master.phone
, contacts.name
from master
, contacts
where master.contact = contacts.id
);


DROP PUBLIC SYNONYM MasterView1;

CREATE PUBLIC SYNONYM MasterView1 FOR scott.MasterView1;

insert into Master values(MasterSeq.NextVal, 'Porkie Pygg', 'Grass Hut', '1. Pyggs Lane', 'Easy Pyggings', 'oink Oink', 1, sysdate, user)
insert into Master values(MasterSeq.NextVal, 'Morkie Pygg', 'Styx Hut', '2. Pyggs Lane', 'Easy Pyggings', 'oink Oink Eeeh', 2, sysdate, user)
insert into Master values(MasterSeq.NextVal, 'Torkie Pygg', 'BryxTon Hut', '3. Pyggs Lane', 'Easy Pyggings', 'oink Oink Bing Bang', 3, sysdate, user)
commit
select * from Master

ALTER TABLE Master
MODIFY
( id CONSTRAINT Master_NN01 NOT NULL
, name CONSTRAINT Master_NN02 NOT NULL
, addr1 CONSTRAINT Master_NN03 NOT NULL
, addr2 CONSTRAINT Master_NN04 NOT NULL
, addr3 CONSTRAINT Master_NN05 NOT NULL
, phone CONSTRAINT Master_NN06 NOT NULL
, contact CONSTRAINT Master_NN07 NOT NULL
, whenDone CONSTRAINT Master_NN08 NOT NULL
, whoDoneIt CONSTRAINT Master_NN09 NOT NULL
);

ALTER TABLE Master
ADD
( CONSTRAINT MASTERKEY PRIMARY KEY
  ( id
  )
);

ALTER TABLE Master
ADD
( CONSTRAINT Master_FK01 FOREIGN KEY
  ( contact
  ) REFERENCES Contacts
);


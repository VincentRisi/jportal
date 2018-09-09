use AnyDB

drop table kopaCodes
go

create table kopaCodes
(
  ecoCode varchar(24) not null
, setId varchar(2) not null
, kopaCode varchar(24) not null
, pageId varchar(24) not null
, period varchar(1) not null
, description varchar(250) not null
, selected integer not null
, strategy integer not null
, disUsed integer not null
, updBy varchar(16) not null
, updWhen datetime not null
, primary key (
    ecoCode
  )
, unique (
    setId
  , kopaCode
  )
)
go


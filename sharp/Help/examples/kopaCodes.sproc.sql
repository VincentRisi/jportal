use AnyDB

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesInsert') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesInsert
GO

CREATE PROCEDURE dbo.kopaCodesInsert
( @P0 varchar(24) -- ecoCode
, @P1 varchar(2) -- setId
, @P2 varchar(24) -- kopaCode
, @P3 varchar(24) -- pageId
, @P4 varchar(1) -- period
, @P5 varchar(250) -- description
, @P6 integer -- selected
, @P7 integer -- strategy
, @P8 integer -- disUsed
, @P9 varchar(16) -- updBy
, @P10 datetime -- updWhen
)
AS
insert into kopaCodes
( ecoCode
, setId
, kopaCode
, pageId
, period
, description
, selected
, strategy
, disUsed
, updBy
, updWhen
) 
values
 (@P0
, @P1
, @P2
, @P3
, @P4
, @P5
, @P6
, @P7
, @P8
, @P9
, @P10
)
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesSelectOne') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesSelectOne
GO

CREATE PROCEDURE dbo.kopaCodesSelectOne
( @P0 varchar(24) -- ecoCode
)
AS
select
  setId
, kopaCode
, pageId
, period
, description
, selected
, strategy
, disUsed
, updBy
, updWhen
 from kopaCodes
 where ecoCode = @P0
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdate') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesUpdate
GO

CREATE PROCEDURE dbo.kopaCodesUpdate
( @P0 varchar(2) -- setId
, @P1 varchar(24) -- kopaCode
, @P2 varchar(24) -- pageId
, @P3 varchar(1) -- period
, @P4 varchar(250) -- description
, @P5 integer -- selected
, @P6 integer -- strategy
, @P7 integer -- disUsed
, @P8 varchar(16) -- updBy
, @P9 datetime -- updWhen
, @P10 varchar(24) -- ecoCode
)
AS
update kopaCodes
 set
  setId = @P0
, kopaCode = @P1
, pageId = @P2
, period = @P3
, description = @P4
, selected = @P5
, strategy = @P6
, disUsed = @P7
, updBy = @P8
, updWhen = @P9
 where ecoCode = @P10
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesSelectAll') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesSelectAll
GO

CREATE PROCEDURE dbo.kopaCodesSelectAll
AS
select
  ecoCode
, setId
, kopaCode
, pageId
, period
, description
, selected
, strategy
, disUsed
, updBy
, updWhen
 from kopaCodes
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesDeleteOne') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesDeleteOne
GO

CREATE PROCEDURE dbo.kopaCodesDeleteOne
( @P0 varchar(24) -- ecoCode
)
AS
delete from kopaCodes
 where ecoCode = @P0
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesList') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesList
GO

CREATE PROCEDURE dbo.kopaCodesList
( @P0 varchar(2) -- setId
, @P1 varchar(1) -- period
)
AS
SELECT ecoCode, pageId, kopaCode, period, description, disUsed 
FROM kopaCodes 
WHERE setId = @P0 
AND   period = @P1 
ORDER BY kopaCode 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesListBySet') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesListBySet
GO

CREATE PROCEDURE dbo.kopaCodesListBySet
( @P0 varchar(2) -- setId
, @P1 varchar(24) -- pageId
, @P2 varchar(1) -- period
)
AS
SELECT ecoCode, selected, strategy, disUsed, description, setId, kopaCode, pageId, period, updBy, updWhen 
FROM kopaCodes 
WHERE setId = @P0 
AND  pageId = @P1 
AND  period = @P2 
ORDER BY ecoCode 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdateFromK') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesUpdateFromK
GO

CREATE PROCEDURE dbo.kopaCodesUpdateFromK
( @P0 varchar(24) -- pageId
, @P1 varchar(250) -- description
, @P2 varchar(2) -- setId
, @P3 varchar(24) -- kopaCode
)
AS
UPDATE kopaCodes 
SET pageId = @P0 
, description = @P1 
, disUsed = 0 
WHERE setId = @P2 
AND   kopaCode = @P3 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesMarkNotUsed') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesMarkNotUsed
GO

CREATE PROCEDURE dbo.kopaCodesMarkNotUsed
( @P0 varchar(2) -- setId
, @P1 varchar(24) -- kopaCode
)
AS
UPDATE kopaCodes 
SET disUsed = 1 
WHERE setId = @P0 
AND   kopaCode = @P1 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesUpdateSelect') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesUpdateSelect
GO

CREATE PROCEDURE dbo.kopaCodesUpdateSelect
( @P0 varchar(24) -- ecoCode
, @P1 integer -- selected
, @P2 integer -- strategy
, @P3 varchar(2) -- setId
, @P4 varchar(24) -- kopaCode
)
AS
UPDATE kopaCodes 
SET ecoCode  = @P0 
, selected = @P1 
, strategy = @P2 
WHERE setId  = @P3 
AND kopaCode = @P4 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesBuildSetList') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesBuildSetList
GO

CREATE PROCEDURE dbo.kopaCodesBuildSetList
( @P0 varchar(2) -- setId
, @P1 varchar(2) -- setId
)
AS
SELECT c.ecoCode, c.selected, c.strategy, s.dir, c.setId, c.kopaCode, c.period, s.lastNo 
FROM kopaCodes c, KopaSets s 
WHERE c.selected > 0 
AND   (s.setId = @P0 or @P1 = '**') 
AND   c.setId = s.SetId 
GO

if exists (select * from sysobjects where id = object_id('dbo.kopaCodesChangeAlias') and sysstat & 0xf = 4)
drop procedure dbo.kopaCodesChangeAlias
GO

CREATE PROCEDURE dbo.kopaCodesChangeAlias
( @P0 varchar(24) -- ecoCode
, @P1 varchar(2) -- setId
, @P2 varchar(24) -- kopaCode
)
AS
update kopaCodes set ecoCode = @P0 
where  setId = @P1 
and    kopaCode = @P2 
GO


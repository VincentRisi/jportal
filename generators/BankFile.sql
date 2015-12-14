DROP TABLE vlab.BankFile CASCADE;

CREATE TABLE vlab.BankFile
( SwiftAddress varchar(11)
, BankName varchar(105)
, BankTown varchar(35)
, BankType varchar(52)
, BranchName varchar(70)
, PhysicalAddr1 varchar(35)
, PhysicalAddr2 varchar(35)
, PhysicalAddr3 varchar(35)
, PhysicalAddr4 varchar(35)
, PhysicalAddr5 varchar(35)
, PhysicalCountry varchar(35)
, PostalAddr1 varchar(35)
, PostalAddr2 varchar(35)
, PostalAddr3 varchar(35)
, PostalAddr4 varchar(35)
, PostalCountry varchar(35)
, Telephone varchar(20)
, EMail varchar(50)
, Info varchar(100)
, AuthKeysExchd varchar(1)
, TgTestKeysExchd varchar(1)
, DeleteInd smallint
, Status smallint
, USId varchar(16)
, TmStamp timestamp
);

ALTER TABLE vlab.BankFile ALTER SwiftAddress SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER BankName SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER BankTown SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER BankType SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER AuthKeysExchd SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER TgTestKeysExchd SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER DeleteInd SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER Status SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER USId SET NOT NULL;
ALTER TABLE vlab.BankFile ALTER TmStamp SET NOT NULL;

ALTER TABLE vlab.BankFile
 ADD CONSTRAINT BANKFILEKEY PRIMARY KEY
  ( SwiftAddress
  )
;


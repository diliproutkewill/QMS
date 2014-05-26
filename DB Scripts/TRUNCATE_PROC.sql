--------------------------------------------------------
--  DDL for Procedure TRUNCATE_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "TRUNCATE_PROC" AS
BEGIN
     EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
     EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES_GROUP');--@@Added by Anil.k for CR 231214 on 25Jan2011
END;

/

/

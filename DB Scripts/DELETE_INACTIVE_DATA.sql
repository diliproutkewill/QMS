--------------------------------------------------------
--  DDL for Procedure DELETE_INACTIVE_DATA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "DELETE_INACTIVE_DATA" As
Begin
  Delete From Qms_Buyrates_Freq Qbfr
   Where Exists (Select Null
            From Qms_Buyrates_Dtl
           Where Buyrateid = Qbfr.Buyrateid
             And Lane_No = Qbfr.Lane_No
             And (Activeinactive = 'I'));
  Commit;
End;

/

/

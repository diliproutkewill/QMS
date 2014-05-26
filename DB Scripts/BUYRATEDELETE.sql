--------------------------------------------------------
--  DDL for Procedure BUYRATEDELETE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "BUYRATEDELETE" 
as
begin
DELETE FROM  qms_Rec_con_Sellratesdtl  qrd
 WHERE   qrd.ai_flag='I' and (buyrateid,lane_no,version_no) in (select buyrateid,lane_no,version_no from qms_buyrates_dtl bd where bd.activeinactive='I' AND BD.CREATEDTIME <= '31-DEC-2011'
  and bd.buyrateid=qrd.buyrateid and bd.lane_no=qrd.lane_no and bd.version_no=qrd.version_no
   AND NOT EXISTS (SELECT 'x'
          FROM QMS_QUOTE_RATES QR
         WHERE  QR.BUYRATE_ID = BD.BUYRATEID
           AND QR.VERSION_NO = BD.VERSION_NO AND QR.RATE_LANE_NO=BD.LANE_NO));
commit;
DELETE FROM  QMS_BUYRATES_DTL BD
 WHERE   BD.ACTIVEINACTIVE='I' AND BD.CREATEDTIME <= '31-DEC-2011'
   AND NOT EXISTS (SELECT 'x'
          FROM QMS_QUOTE_RATES QR
         WHERE QR.BUYRATE_ID = BD.BUYRATEID
           AND QR.VERSION_NO = BD.VERSION_NO AND QR.RATE_LANE_NO=BD.LANE_NO);
commit;

end;

/

/

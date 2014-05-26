--------------------------------------------------------
--  DDL for Procedure BRRSRDELETION
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "BRRSRDELETION" is
begin
  declare

  CNT        NUMBER;
  BUY_RATE   NUMBER;
  L_NO    NUMBER;
  V_NO NUMBER;
  cursor C1 is(
    select * FROM BRRSRDELETE BRS );
begin

  for K in C1 loop
  BUY_RATE :=0;
     L_NO :=0;
    V_NO :=0;
     begin
    SELECT DTL.BUYRATEID, DTL.LANE_NO, DTL.VERSION_NO
      INTO BUY_RATE, L_NO, V_NO
      FROM QMS_BUYRATES_DTL DTL
     WHERE DTL.ORIGIN = K.ORIGIN
       AND DTL.DESTINATION = K.DESTINATION
       AND DTL.SERVICE_LEVEL = K.SERVICE_LEVEL
       AND DTL.CARRIER_ID = K.CARRIER
       AND DTL.FREQUENCY=K.FREQUENCY
       AND DTL.TRANSIT_TIME=K.TRANSIT_DAYS
        AND DTL.LINE_NO=0 ;

       Exception
       when NO_DATA_FOUND
       THEN
       DBMS_OUTPUT.put_line(k.origin||':' || k.destination||':' || k.carrier||':' ||K.SERVICE_LEVEL);
       end;

    SELECT COUNT(QR.quote_id) INTO CNT
              FROM QMS_QUOTE_RATES QR
             WHERE QR.BUYRATE_ID = BUY_RATE
               and qr.rate_lane_no = L_NO
               and qr.version_no = V_NO AND QR.SELL_BUY_FLAG IN ('BR','RSR');

    IF CNT > 0 THEN
      UPDATE BRRSRDELETE DL SET DL.STATUS = 'T' WHERE DL.SLNO = K.SLNO;

CNT :=0;
      COMMIT;
    END IF;
  end loop;
end;

end BRRSRDELETION;

/

/

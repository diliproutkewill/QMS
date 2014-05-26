--------------------------------------------------------
--  DDL for Procedure NZLCLDELETE_INUPDATE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "NZLCLDELETE_INUPDATE" is
begin
  declare
  V_QUOTEID    varchar2(20000);
  V_COUNT1     number(10);
  V_BUYRATEID  number(10);
  V_LANENO     number(10);
  V_VERSIONNO  number(10);
  V_BUYRATEID1 number(10);
  V_LANENO1    number(10);
  V_VERSIONNO1 number(10);
BEGIN
  FOR I IN (SELECT * FROM QMS_BUYRATES_DELETE2) LOOP
    V_COUNT1  := 0;
    V_QUOTEID := null;
    BEGIN

      SELECT COUNT(QM.QUOTE_ID)
        INTO V_COUNT1
        FROM QMS_QUOTE_MASTER QM, QMS_QUOTE_RATES QR, QMS_BUYRATES_DTL QBD
       WHERE QM.ID = QR.QUOTE_ID
         AND QM.ACTIVE_FLAG = 'A'
         AND QR.BUYRATE_ID = QBD.BUYRATEID
         AND QR.RATE_LANE_NO = QBD.LANE_NO
         AND QR.VERSION_NO = QBD.VERSION_NO
         AND QBD.ORIGIN = I.ORIGIN
         AND QBD.DESTINATION = I.DESTINATION
         AND QBD.CARRIER_ID = I.CARRIER_ID
         AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
         AND QR.LINE_NO = QBD.LINE_NO
         AND QBD.LINE_NO = '0'
         AND QBD.FREQUENCY = I.FREQUENCY
         AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
	 AND QBD.EFFECTIVE_FROM = I.EFFECTIVE_FROM
         AND EXISTS (SELECT 'X'
                FROM QMS_BUYRATES_MASTER
               WHERE BUYRATEID = QBD.BUYRATEID
                 AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
                 AND TERMINALID = I.TERMINAL_ID);
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        V_COUNT1 := 0;
    END;
    IF V_COUNT1 <> 0 THEN

      FOR J IN (SELECT DISTINCT QM.QUOTE_ID,
                                QM.ID,
                                QBD.ORIGIN,
                                QBD.DESTINATION,
                                QBD.CARRIER_ID,
                                QBD.SERVICE_LEVEL,
                                QBD.FREQUENCY,
                                QBD.TRANSIT_TIME,
                                QBD.BUYRATEID,
                                QBD.LANE_NO,
                                QBD.VERSION_NO
                  FROM QMS_QUOTE_MASTER QM,
                       QMS_QUOTE_RATES  QR,
                       QMS_BUYRATES_DTL QBD
                 WHERE QM.ID = QR.QUOTE_ID
                   AND QM.ACTIVE_FLAG = 'A'
                   AND QR.BUYRATE_ID = QBD.BUYRATEID
                   AND QR.RATE_LANE_NO = QBD.LANE_NO
                   AND QR.VERSION_NO = QBD.VERSION_NO
                   AND QBD.ORIGIN = I.ORIGIN
                   AND QBD.DESTINATION = I.DESTINATION
                   AND QBD.CARRIER_ID = I.CARRIER_ID
                   AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
                   AND QBD.FREQUENCY = I.FREQUENCY
                   AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
                   AND QBD.EFFECTIVE_FROM = I.EFFECTIVE_FROM
                   AND EXISTS
                 (SELECT 'X'
                          FROM QMS_BUYRATES_MASTER
                         WHERE BUYRATEID = QBD.BUYRATEID
                           AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
                           AND TERMINALID = I.TERMINAL_ID)
                   AND EXISTS
                 (SELECT 'X'
                          FROM QMS_QUOTES_UPDATED QU
                         WHERE QU.NEW_BUYCHARGE_ID = QBD.BUYRATEID
                           AND QU.NEW_LANE_NO = QBD.LANE_NO
                           AND QU.NEW_VERSION_NO = QBD.VERSION_NO
                           AND QU.CONFIRM_FLAG IS NULL
                           AND QU.SELL_BUY_FLAG IN ('BR', 'RSR')
                           ))

       LOOP
        IF V_QUOTEID IS NULL THEN
          V_QUOTEID := V_QUOTEID || J.QUOTE_ID;
        ELSE
          V_QUOTEID := V_QUOTEID || ',' || J.QUOTE_ID;
        END IF;

        V_BUYRATEID := J.BUYRATEID;
        V_LANENO    := J.LANE_NO;
        V_VERSIONNO := J.VERSION_NO;
      END LOOP;
      UPDATE QMS_BUYRATES_DELETE2
         SET ERROR_MSG  = V_QUOTEID,
             BUYRATEID  = V_BUYRATEID,
             LANE_NO    = V_LANENO,
             VERSION_NO = V_VERSIONNO
       WHERE ROW_ID = I.ROW_ID;
       V_BUYRATEID :='';
       V_LANENO :='';
       V_VERSIONNO :='';
    END IF;
  END LOOP;

END;
end NZLCLDELETE_INUPDATE;

/

/

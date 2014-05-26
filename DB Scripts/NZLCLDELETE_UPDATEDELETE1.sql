--------------------------------------------------------
--  DDL for Procedure NZLCLDELETE_UPDATEDELETE1
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "NZLCLDELETE_UPDATEDELETE1" is
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
    FOR I IN (SELECT * FROM QMS_BUYRATES_DELETE1 WHERE ERROR_MSG IS NULL) LOOP
      V_COUNT1  := 0;
      V_QUOTEID := null;
      BEGIN

        SELECT COUNT(QM.QUOTE_ID)
          INTO V_COUNT1
          FROM QMS_QUOTE_MASTER QM,
               QMS_QUOTE_RATES  QR,
               QMS_BUYRATES_DTL QBD
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ACTIVE_FLAG = 'I'
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
           AND EXISTS
         (SELECT 'X'
                  FROM QMS_BUYRATES_MASTER
                 WHERE BUYRATEID = QBD.BUYRATEID
                   AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
                   AND TERMINALID = I.TERMINAL_ID);
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_COUNT1 := 0;
      END;
      IF V_COUNT1 <> 0 THEN
        BEGIN

          SELECT DISTINCT BD.BUYRATEID, BD.LANE_NO, BD.VERSION_NO
            INTO V_BUYRATEID1, V_LANENO1, V_VERSIONNO1
            FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
           WHERE BD.BUYRATEID = BM.BUYRATEID
             AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.TERMINALID = I.TERMINAL_ID
             AND BD.ORIGIN = I.ORIGIN
             AND BD.DESTINATION = I.DESTINATION
             AND BD.CARRIER_ID = I.CARRIER_ID
             AND BD.SERVICE_LEVEL = I.SERVICE_LEVEL
             AND BD.EFFECTIVE_FROM = I.EFFECTIVE_FROM
             AND BD.LINE_NO = '0';
        EXCEPTION
          WHEN OTHERS THEN
            UPDATE QMS_BUYRATES_DELETE1 BD1
               SET BD1.ERROR_MSG = 'EXCEPTION'
             WHERE BD1.ROW_ID = I.ROW_ID;
        END;

        /*UPDATE QMS_BUYRATES_DTL BDTL
           SET BDTL.ACTIVEINACTIVE = 'I'
         WHERE BDTL.BUYRATEID = V_BUYRATEID1
           AND BDTL.LANE_NO = V_LANENO1
           AND BDTL.VERSION_NO = V_VERSIONNO1;

        UPDATE QMS_REC_CON_SELLRATESDTL RSD
           SET RSD.AI_FLAG = 'I'
         WHERE RSD.BUYRATEID = V_BUYRATEID1
           AND RSD.LANE_NO = V_LANENO1
           AND RSD.VERSION_NO = V_VERSIONNO1;*/

        UPDATE QMS_BUYRATES_DELETE1 BD1
           SET BD1.ERROR_MSG = 'THESE RATES ARE INACTIVE BUT HAVING QUOTES'
         WHERE BD1.ROW_ID = I.ROW_ID;
      END IF;
      IF V_COUNT1 = 0 THEN
        BEGIN

          SELECT DISTINCT BD.BUYRATEID, BD.LANE_NO, BD.VERSION_NO
            INTO V_BUYRATEID1, V_LANENO1, V_VERSIONNO1
            FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
           WHERE BD.BUYRATEID = BM.BUYRATEID
             AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.TERMINALID = I.TERMINAL_ID
             AND BD.ORIGIN = I.ORIGIN
             AND BD.DESTINATION = I.DESTINATION
             AND BD.CARRIER_ID = I.CARRIER_ID
             AND BD.SERVICE_LEVEL = I.SERVICE_LEVEL
             AND BD.EFFECTIVE_FROM = I.EFFECTIVE_FROM
             AND BD.LINE_NO = '0';
        EXCEPTION
          WHEN OTHERS THEN
            UPDATE QMS_BUYRATES_DELETE1 BD1
               SET BD1.ERROR_MSG = 'EXCEPTION'
             WHERE BD1.ROW_ID = I.ROW_ID;
        END;
        /* DELETE FROM QMS_BUYRATES_DTL BDTL
            WHERE BDTL.BUYRATEID = V_BUYRATEID1
              AND BDTL.LANE_NO = V_LANENO1
              AND BDTL.VERSION_NO = V_VERSIONNO1;

           DELETE FROM QMS_REC_CON_SELLRATESDTL RSD
            WHERE RSD.BUYRATEID = V_BUYRATEID1
              AND RSD.LANE_NO = V_LANENO1
              AND RSD.VERSION_NO = V_VERSIONNO1;
        */
        UPDATE QMS_BUYRATES_DELETE1 BD1
           SET BD1.ERROR_MSG = 'NEED TO DELETE FROM THE QMS'
         WHERE BD1.ROW_ID = I.ROW_ID;
      END IF;

    END LOOP;

  END;

end NZLCLDELETE_UPDATEDELETE;

/

/

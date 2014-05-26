--------------------------------------------------------
--  DDL for Procedure ACTIVITYTEST
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "ACTIVITYTEST" is
begin
  -- Created on 7/1/2010 by SUBRAHMANYAMK
  declare
    -- Local variables here
    ncount   integer;
    V_ID1 varchar2(10000);
    V_ID2 varchar2(10000);
    V_QUOTEID VARCHAR2(20);
    V_WHERE VARCHAR2(100);
  begin
    -- Test statements here
    dbms_output.enable(100000000000);
    EXECUTE IMMEDIATE ('TRUNCATE TABLE activeReportTest');
     FOR J IN( SELECT QM.QUOTE_ID FROM QMS_QUOTE_MASTER QM WHERE QM.CREATED_DATE BETWEEN TO_DATE('01-JAN-11') AND TO_DATE('01-FEB-11')  AND QM.COMPLETE_FLAG = 'C' AND (((QM.ACTIVE_FLAG = 'A' OR QM.ACTIVE_FLAG = 'I') AND
                                                               QM.QUOTE_STATUS in ('ACC', 'NAC')) OR
                                                               (QM.ACTIVE_FLAG = 'A' AND QM.QUOTE_STATUS not in ('ACC', 'NAC')))
                                                               AND QM.QUOTING_STATION IN
       (
SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLAUAD'
UNION
SELECT 'DHLAUAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLIDAD'
UNION
SELECT 'DHLIDAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLINAD'
UNION
SELECT 'DHLINAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLAOAD'
UNION
SELECT 'DHLAOAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLCGAD'
UNION
SELECT 'DHLCGAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLCIAD'
UNION
SELECT 'DHLCIAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLCMAD'
UNION
SELECT 'DHLCMAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLGAAD'
UNION
SELECT 'DHLGAAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLGHAD'
UNION
SELECT 'DHLGHAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLKEAD'
UNION
SELECT 'DHLKEAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLLKAD'
UNION
SELECT 'DHLLKAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLMYAD'
UNION
SELECT 'DHLMYAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLNGAD'
UNION
SELECT 'DHLNGAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLNZAD'
UNION
SELECT 'DHLNZAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLPHAD'
UNION
SELECT 'DHLPHAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLPKAD'
UNION
SELECT 'DHLPKAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLSGAD'
UNION
SELECT 'DHLSGAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLSNAD'
UNION
SELECT 'DHLSNAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLTHAD'
UNION
SELECT 'DHLTHAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLTZAD'
UNION
SELECT 'DHLTZAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLUGAD'
UNION
SELECT 'DHLUGAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLVNAD'
UNION
SELECT 'DHLVNAD'
FROM DUAL)
UNION
(SELECT CHILD_TERMINAL_ID
  FROM FS_FR_TERMINAL_REGN
 WHERE PARENT_TERMINAL_ID = 'DHLZAAD'
UNION
SELECT 'DHLZAAD'
FROM DUAL))
     LOOP
      IF instr(J.QUOTE_ID, '_', 1, 2) = 0 THEN
      SELECT wm_concat(ID) INTO V_ID1 FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID=J.QUOTE_ID AND QM.VERSION_NO=1;
      SELECT wm_concat(ID) INTO V_ID2 FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID=J.QUOTE_ID AND QM.VERSION_NO=( SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER QM1 WHERE QM1.QUOTE_ID=QM.QUOTE_ID );
     ELSE
     SELECT SUBSTR(J.QUOTE_ID,0,instr(J.QUOTE_ID, '_', 1, 2)-1 ) INTO V_QUOTEID FROM DUAL ;
     V_WHERE :=' WHERE QM1.QUOTE_ID LIKE '||''''||V_QUOTEID||'%'||'''';
    -- DBMS_OUTPUT.put_line('SELECT MIN(ID),MAX(ID) INTO V_ID1,V_ID2 FROM QMS_QUOTE_MASTER QM1' ||V_WHERE);
   print_out(J.QUOTE_ID||'---------'||V_ID1||'---------------'||V_ID2);
    EXECUTE IMMEDIATE ('SELECT MIN(ID),MAX(ID)  FROM QMS_QUOTE_MASTER QM1'  || ' WHERE QM1.QUOTE_ID LIKE '||''''||V_QUOTEID||'%'||'''')
    INTO V_ID1,V_ID2;
      END IF;
      ncount :=0;
    for i in (SELECT qm.id,
                     QM.CREATED_BY,
                     QM.SALES_PERSON,
                   --to_char(QM.CREATED_DATE, 'DD-MON-YYYY HH24:MM:SS') createdDate,
                   --to_char(QM.CREATED_tstmp, 'DD-MON-YYYY HH24:MM:SS') CreatedTime,
                   QM.CREATED_DATE createdDate,
                   QM.CREATED_tstmp CreatedTime,
                     QM.VALID_TO,
                     QM.QUOTE_ID,
                     QM.CUSTOMER_ID,
                     C.COMPANYNAME,
                     QM.QUOTE_STATUS,
                     QM.SPOT_RATES_FLAG,
                     QM.IS_MULTI_QUOTE,
                     DECODE((SELECT COUNT(*)
                              FROM FS_RT_LEG LG, FS_RT_PLAN RP
                             WHERE RP.QUOTE_ID = QM.QUOTE_ID
                               AND LG.RT_PLAN_ID = RP.RT_PLAN_ID),
                            1,
                            QM.SHIPMENT_MODE,
                            100) SHIPMENT_MODE,
                     DECODE((SELECT COUNT(*)
                              FROM FS_RT_LEG LG, FS_RT_PLAN RP
                             WHERE RP.QUOTE_ID = QM.QUOTE_ID
                               AND LG.RT_PLAN_ID = RP.RT_PLAN_ID),
                            1,
                            DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG
                                     FROM QMS_QUOTE_RATES QR
                                    WHERE QR.QUOTE_ID = QM.ID
                                      AND QR.SELL_BUY_FLAG IN
                                          ('BR', 'RSR', 'CSR', 'SBR')),
                                   'SBR',
                                   (SELECT DISTINCT SERVICELEVEL
                                      FROM QMS_QUOTE_SPOTRATES
                                     WHERE QUOTE_ID = QM.ID
                                       AND SERVICELEVEL NOT IN ('SCH')),
                                   (SELECT DISTINCT SERVICE_LEVEL
                                      FROM QMS_BUYRATES_DTL BD,
                                           QMS_QUOTE_RATES  QR

                                     WHERE BD.BUYRATEID = QR.BUYRATE_ID
                                       AND BD.LANE_NO = QR.RATE_LANE_NO
                                       AND BD.LINE_NO = '0'
                                       AND QR.QUOTE_ID = QM.ID

                                       AND QR.SELL_BUY_FLAG IN
                                           ('BR', 'RSR', 'CSR'))),
                            'Multi-Modal') SERVICE_LEVEL,
                     (SELECT COUNTRYID
                        FROM FS_FR_LOCATIONMASTER
                       WHERE LOCATIONID = QM.ORIGIN_LOCATION) ORIGIN_COUNTRY,
                     QM.ORIGIN_LOCATION,
                     (SELECT COUNTRYID
                        FROM FS_FR_LOCATIONMASTER
                       WHERE LOCATIONID = QM.DEST_LOCATION) DEST_COUNTRY,
                     QM.DEST_LOCATION,
                     QM.ACTIVE_FLAG,
                     QM.TERMINAL_ID,
                     nvl(qm.overlength_cargonotes, 'No Cargo Notes') cargonotes
                FROM QMS_QUOTE_MASTER QM, FS_FR_CUSTOMERMASTER C
               WHERE C.CUSTOMERID = QM.CUSTOMER_ID
                 AND (QM.CREATED_DATE BETWEEN TO_DATE('01-JAN-11') AND
                     TO_DATE('01-FEB-11'))
                    /*AND (((QM.ACTIVE_FLAG = 'A' OR QM.ACTIVE_FLAG = 'I') AND
                                                               QM.QUOTE_STATUS in ('ACC', 'NAC')) OR
                                                               (QM.ACTIVE_FLAG = 'A' AND QM.QUOTE_STATUS not in ('ACC', 'NAC')))*/
                 AND QM.COMPLETE_FLAG = 'C'
              --   and instr(QM.QUOTE_ID, '_', 1, 2) = 0
               --  AND QM.SHIPMENT_MODE = '2'
               AND to_char(QM.ID) IN(V_ID1,V_ID2)
               ORDER BY QM.QUOTE_ID)

     loop
     ncount := ncount+1;

      insert into activeReportTest
      values
        (i.created_by,
         i.sales_person,
         i.createddate,
         i.createdtime,
         i.valid_to,
         i.quote_id,
         i.customer_id,
         i.companyname,
         i.quote_status,
         i.shipment_mode,
         i.service_level,
         i.origin_country,
         i.origin_location,
         i.dest_country,
         i.dest_location,
         i.active_flag,
         i.terminal_id,
         i.id,
         i.spot_rates_flag,
         i.is_multi_quote
      );

    end loop;
--    dbms_output.put_line('ncount..'||ncount);
    END LOOP;
  end;
end ActivityTest;

/

/

--------------------------------------------------------
--  DDL for Package Body QMS_RATES_EXCEL
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_RATES_EXCEL" is



  PROCEDURE PROC_RATES_HEADER_EXCEL_DTL(p_v_Customer_Id  IN QMS_QUOTE_MASTER.Customer_Id%Type,
                                        p_v_Sql          IN varchar2,
                                        p_v_shipmentmode number,
                                        p_v_temp         number)

   IS

    v_c_CustomerId      QMS_QUOTE_MASTER.Customer_Id%Type := p_v_Customer_Id;
    v_c_sql             varchar2(1000) := p_v_Sql;
    v_c_shipmentmode    number := p_v_shipmentmode;
    v_c_weightbreakslab QMS_BUYRATES_DTL.Weight_Break_Slab%Type;
    v_c_rateDescription QMS_BUYRATES_DTL.Rate_Description%Type;
    v_c_temp            number := p_v_temp;
    header_cursor       ResultSet;
  BEGIN
    OPEN header_cursor FOR 'SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER QBM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  QBM.SHIPMENT_MODE = ' || v_c_shipmentmode || '
                 AND QBM.WEIGHT_BREAK = ''FLAT''
                 AND BD.SERVICE_LEVEL = ''SCH''
                 and qm.customer_id=' || '''' || v_c_CustomerId || '''' || v_c_sql || '
                 AND Qm.ACTIVE_FLAG = ''A''
                 AND QM.COMPLETE_FLAG = ''C''
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = QBM.BUYRATEID
                 AND (BD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = QBM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 ORDER BY BD.RATE_DESCRIPTION';
    LOOP
      FETCH header_cursor
        INTO v_c_weightbreakslab, v_c_rateDescription;
      EXIT WHEN header_cursor%NOTFOUND;
      INSERT INTO RATES_HEADER_EXCEL
      VALUES
        (TO_CHAR(v_c_temp),
         v_c_shipmentmode,
         v_c_weightbreakslab,
         'FLAT',
         'BR',
         v_c_rateDescription);

      v_c_temp := v_c_temp + 1;
    END LOOP;
    CLOSE header_cursor;

  END PROC_RATES_HEADER_EXCEL_DTL;
  -----------------------------------------------------------------------------------------
  -----------------------------------------------------------------------------------------

































  procedure qms_ratesreport_excel(p_customerid    IN VARCHAR2,
                                  p_origincountry IN VARCHAR2,
                                  p_origincity    IN VARCHAR2,
                                  p_destcountry   IN VARCHAR2,
                                  p_destcity      IN VARCHAR2,
                                  p_shipmentMode  IN VARCHAR2,
                                  p_consoleType   IN VARCHAR2,
                                  p_spotrateflag  IN VARCHAR2,
                                  p_weightBreak   IN VARCHAR2, --Added By Kishore Podili For Report with Charges on 03-Feb-11
                                  RS              out Resultset
                                  ,RS1             out Resultset
                                  ) As

    countslab            NUMBER(10) := 1;
    countslab1           NUMBER(10) := 1;
    countslab2           NUMBER(10) := 1;
    countlist1           NUMBER(10) := 0;
    countlist2           NUMBER(10) := 0;
    countlist3           NUMBER(10) := 0;
    V_CHARGERATE         NUMBER(20, 2);
    V_RATE               VARCHAR2(32567);
    v_sql                VARCHAR2(1000) := '';
    v_sql2               VARCHAR2(1000) := '';
    v_sqll               VARCHAR2(1000) := '';
    V_SQL4               VARCHAR2(32567) := '';
    v_sql6               VARCHAR2(1000) := '';
    v_sql3               VARCHAR2(1000) := ')';
    V_SQL14              VARCHAR2(32567);
    V_SQL5               VARCHAR2(32567);
    v_curr               sys_refcursor;
    V_WTBREAKSLAB        VARCHAR2(100);
    MODECOUNT            NUMBER(10);
    V_MARGINDISCOUNTFLAG VARCHAR2(10);
    V_MARGIN             NUMBER(20, 5);
    V_BUYRATE            NUMBER(20, 5);
    V_SELLRATE           NUMBER(20, 5);
    V_DISCOUNT           NUMBER(20, 5);
    V_MARGINTYPE         VARCHAR2(10);
    V_DISCOUNTTYPE       VARCHAR2(10);
    TEMP                 NUMBER(5); --KISHORE
    TEMP_1               NUMBER(5); --KISHORE
    V_DESC_TEMP          VARCHAR2(100); -- KISHORE

  BEGIN
    EXECUTE IMMEDIATE ('TRUNCATE TABLE RATES_HEADER_EXCEL');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE RATES_MODE');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE RATES_EXCEL');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_TABLE_EXCEL');
     EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_TABLE_EXCEL');

    IF p_origincity IS NOT NULL THEN
      v_sql := ' AND QM.ORIGIN_LOCATION = ' || '''' || p_origincity || '''';
    END IF;
    IF p_origincity IS NULL AND p_origincountry IS NOT NULL THEN

      v_sql := ' AND QM.ORIGIN_LOCATION IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
               p_origincountry || '''' || ') ';
    END IF;
    IF p_destcity IS NOT NULL THEN
      v_sql := v_sql || ' AND QM.DEST_LOCATION = ' || '''' || p_destcity || '''';
    END IF;
    IF p_destcity IS NULL AND p_destcountry IS NOT NULL THEN
      v_sql := v_sql ||
               ' AND QM.DEST_LOCATION IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
               p_destcountry || '''' || ') ';
    END IF;

    IF p_shipmentMode IS NOT NULL THEN
      IF UPPER(p_shipmentMode) = 'AIR' THEN
        v_sql := v_sql || ' AND QM.SHIPMENT_MODE = ''1''';
      END IF;
      IF UPPER(p_shipmentMode) = 'SEA' THEN
        v_sql := v_sql || ' AND QM.SHIPMENT_MODE = ''2''';
      END IF;
      IF UPPER(p_shipmentMode) = 'TRUCK' THEN
        v_sql := v_sql || ' AND QM.SHIPMENT_MODE = ''4'' ';
      END IF;
      ELSE
      v_sql := v_sql || ' AND QM.SHIPMENT_MODE IN (''1'',''2'',''4'') ';
    END IF;

    --Added By Kishore Podili For Report with Charges on 03-Feb-11
    IF p_weightBreak IS NOT NULL THEN
      IF UPPER(p_weightBreak) = 'FLAT' THEN
        v_sql := v_sql || ' AND WEIGHT_BREAK = ''FLAT''';
      END IF;
      IF UPPER(p_weightBreak) = 'SLAB' THEN
        v_sql := v_sql || ' AND WEIGHT_BREAK = ''SLAB''';
      END IF;
      IF UPPER(p_weightBreak) = 'LIST' THEN
        v_sql := v_sql || ' AND  WEIGHT_BREAK = ''LIST'' ';
      END IF;
    END IF;
    --End Of Kishore Podili For Report with Charges on 03-Feb-11





    IF p_spotrateflag = 'on' and p_consoleType IS NOT NULL THEN
      v_sqll := v_sql || ' AND QSR.SERVICELEVEL like  ' || '''' ||
                p_consoleType || '%' || '''';
    END IF;

    IF p_spotrateflag = 'on' and p_consoleType IS NULL THEN
      v_sqll := v_sql;
    END IF;

    IF p_consoleType IS NOT NULL THEN
      v_sql := v_sql || ' AND QBM.CONSOLE_TYPE = ' || '''' || p_consoleType || '''';
    END IF;

    v_sql2  := 'INSERT INTO RATES_MODE (SELECT  QBM.WEIGHT_BREAK WEIGHT_BREAK,QBM.SHIPMENT_MODE SHIPMENT_MODE ,''BR'' FROM QMS_QUOTE_RATES QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID =' || '''' || p_customerid || '''' ||
               ' AND QM.COMPLETE_FLAG=''C'' AND QM.ACTIVE_FLAG = ''A''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND QR.LINE_NO=''0''
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO';
    v_sql3  := ' GROUP BY QBM.WEIGHT_BREAK,QBM.SHIPMENT_MODE )';
    v_sql6  := ' GROUP BY QSR.WEIGHT_BREAK,QSR.SHIPMENT_MODE )';
    V_SQL14 := 'INSERT INTO RATES_MODE (SELECT  DISTINCT QSR.WEIGHT_BREAK WEIGHT_BREAK,QSR.SHIPMENT_MODE SHIPMENT_MODE ,''SBR'' FROM
          QMS_QUOTE_MASTER    QM,
          QMS_QUOTE_SPOTRATES  QSR
    WHERE QM.CUSTOMER_ID =' || '''' || p_customerid || '''' ||
               ' AND NOT EXISTS (SELECT ''X'' FROM RATES_MODE WHERE WEIGHT_BREAK = QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE) AND QM.ACTIVE_FLAG = ''A''
      AND QM.ID = QSR.QUOTE_ID';

    FOR N IN (SELECT DISTINCT SHIPMENT_MODE FROM RATES_MODE) LOOP
      MODECOUNT := MODECOUNT + N.SHIPMENT_MODE;

    END LOOP;
    IF MODECOUNT = 3 THEN
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (3);
    ELSIF MODECOUNT = 4 THEN
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (4);
    ELSIF MODECOUNT = 7 THEN
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (3);
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (4);
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (5);
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (7);
    ELSIF MODECOUNT = 5 THEN
      INSERT INTO RATES_MODE (SHIPMENT_MODE) VALUES (5);

    END IF;

    EXECUTE IMMEDIATE (v_sql2 || v_sql || v_sql3);
    IF p_spotrateflag = 'on' THEN

      EXECUTE IMMEDIATE (V_SQL14 || v_sqll || v_sql6);
    END IF;

    FOR J IN (SELECT WEIGHT_BREAK, SHIPMENT_MODE, SELL_BUY_FLAG
                FROM RATES_MODE /*WHERE SELL_BUY_FLAG IN ('BR','RSR')*/
              )

     LOOP
      IF J.WEIGHT_BREAK = 'FLAT' AND J.SHIPMENT_MODE = '1' THEN
        --kishore
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('1', '1', 'BASIC', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('2', '1', 'MIN', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('3', '1', 'FLAT', 'FLAT', 'BR', 'A FREIGHT RATE');

        TEMP := 4;
        PROC_RATES_HEADER_EXCEL_DTL(p_customerid,
                                    v_sql,
                                    j.shipment_mode,
                                    TEMP);

        -- Gowtham
        /*
        t_Stmt := 'SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                   FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER QBM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
                  WHERE  QBM.SHIPMENT_MODE = 1
                    AND QBM.WEIGHT_BREAK = ''FLAT''
                    AND BD.SERVICE_LEVEL = ''SCH''
                    and qm.customer_id='|| p_customerid ||'
                    AND Qm.ACTIVE_FLAG = ''A''
                    AND QM.COMPLETE_FLAG = ''C''' ||v_sql||
                    'AND BD.ACTIVEINACTIVE IS NULL
                    AND BD.BUYRATEID = QBM.BUYRATEID
                    AND (BD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                    AND BD.VERSION_NO = QBM.VERSION_NO
                    and qm.id = qr.quote_id
                    and qr.buyrate_id = bd.buyrateid
                  --and bd.buyrateid = qr.buyrate_id
                  ORDER BY BD.RATE_DESCRIPTION;';*/
        --  C1(p_customerid,v_sql);

        --   FOR K IN C1
        -- End of Gowtham
        --Select Proc_RATES_HEADER_EXCEL_DTL(p_customerid,v_sql,j.shipment_mode) into V_header_Cursor FROM DUAL;
        -- V_header_Cursor := FUN_RATES_HEADER_EXCEL_DTL(p_customerid,v_sql,j.shipment_mode);

        -- FOR K IN  V_header_Cursor

        /*(SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
         FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER QBM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
        WHERE  QBM.SHIPMENT_MODE = 1
          AND QBM.WEIGHT_BREAK = 'FLAT'
          AND BD.SERVICE_LEVEL = 'SCH'
          and qm.customer_id= p_customerid
          AND Qm.ACTIVE_FLAG = 'A'
          AND QM.COMPLETE_FLAG = 'C' ||v_sql
          AND BD.ACTIVEINACTIVE IS NULL
          AND BD.BUYRATEID = QBM.BUYRATEID
          AND (BD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
          AND BD.VERSION_NO = QBM.VERSION_NO
          and qm.id = qr.quote_id
          and qr.buyrate_id = bd.buyrateid
        --and bd.buyrateid = qr.buyrate_id
        ORDER BY BD.RATE_DESCRIPTION) */

        /*(SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
         FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
        WHERE BD.BUYRATEID = BM.BUYRATEID
          AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
          AND BD.VERSION_NO = BM.VERSION_NO
          AND BM.SHIPMENT_MODE = 1
          AND BM.WEIGHT_BREAK = 'FLAT'
          AND BD.ACTIVEINACTIVE IS NULL
          AND BD.SERVICE_LEVEL = 'SCH'
        ORDER BY BD.RATE_DESCRIPTION
        )*/

        /* LOOP

        INSERT INTO RATES_HEADER_EXCEL
                    VALUES(TO_CHAR(TEMP),'1',K.WEIGHT_BREAK_SLAB,'FLAT','BR',K.RATE_DESCRIPTION) ;

             TEMP:=TEMP+1;
        END LOOP;*/
        -- Kishore
        SELECT COUNT(1) INTO TEMP_1 FROM RATES_HEADER_EXCEL;

        /*INSERT INTO RATES_HEADER_EXCEL
        VALUES('3','1','FSBASIC','FLAT','BR') ;
           INSERT INTO RATES_HEADER_EXCEL
        VALUES('4','1','FSMIN','FLAT','BR') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('5','1','FSKG','FLAT','BR') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('6','1','SSBASIC','FLAT','BR') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('7','1','SSMIN','FLAT','BR') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('8','1','SSKG','FLAT','BR') ;*/

        -- Kishore

      END IF;
      IF J.WEIGHT_BREAK = 'FLAT' AND J.SHIPMENT_MODE = '2' THEN

        --kishore
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('1', '2', 'BASIC', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('2', '2', 'MIN', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('3', '2', 'FLAT', 'FLAT', 'BR', 'A FREIGHT RATE');
        TEMP := 4;

        PROC_RATES_HEADER_EXCEL_DTL(p_customerid,
                                    v_sql,
                                    j.shipment_mode,
                                    TEMP);

        -- Start by Gowtham
        /*         FOR K IN
              (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER QBM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  QBM.SHIPMENT_MODE = 2
                 AND QBM.WEIGHT_BREAK = 'FLAT'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C' || v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = QBM.BUYRATEID
                 AND (BD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = QBM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)

         LOOP
        INSERT INTO RATES_HEADER_EXCEL
                    VALUES(TO_CHAR(TEMP),'2',K.WEIGHT_BREAK_SLAB,'FLAT','BR',K.RATE_DESCRIPTION) ;

             TEMP:=TEMP+1;
        END LOOP;*/

        -- END  by Gowtham
        -- Kishore

        /*INSERT INTO RATES_HEADER_EXCEL
        VALUES('3','2','BAFMIN','FLAT','BR','Freight Rate') ;
           INSERT INTO RATES_HEADER_EXCEL
        VALUES('4','2','BAFM3','FLAT','BR','Freight Rate') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('5','2','CAFMIN','FLAT','BR','Freight Rate') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('6','2','CAF%','FLAT','BR','Freight Rate') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('7','2','PSSMIN','FLAT','BR','Freight Rate') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('8','2','PSSM3','FLAT','BR','Freight Rate') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES('9','2','CSF','FLAT','BR','Freight Rate') ;*/

      END IF;
      IF J.WEIGHT_BREAK = 'FLAT' AND J.SHIPMENT_MODE = '4' THEN
        --kishore
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('1', '4', 'BASIC', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('2', '4', 'MIN', 'FLAT', 'BR', 'A FREIGHT RATE');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('3', '4', 'FLAT', 'FLAT', 'BR', 'A FREIGHT RATE');
        TEMP := 4;
        PROC_RATES_HEADER_EXCEL_DTL(p_customerid,
                                    v_sql,
                                    j.shipment_mode,
                                    TEMP);

        -- Start BY Gowtham
        /*  FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER QBM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  QBM.SHIPMENT_MODE = 4
                 AND QBM.WEIGHT_BREAK = 'FLAT'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = QBM.BUYRATEID
                 AND (BD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = QBM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
             INSERT INTO RATES_HEADER_EXCEL
                    VALUES(TO_CHAR(TEMP),'4',K.WEIGHT_BREAK_SLAB,'FLAT','BR',K.RATE_DESCRIPTION) ;

             TEMP:=TEMP+1;
        END LOOP;*/
        -- End Of Gowtham
        -- Kishore
        /* INSERT INTO RATES_HEADER_EXCEL
        VALUES('3','4','SURCHARGE','FLAT','BR','Freight Rate') ;*/

      END IF;
      IF J.WEIGHT_BREAK = 'SLAB' AND J.SHIPMENT_MODE = '1' THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';

        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('1', '1', 'BASIC', 'SLAB', 'BR', 'A FREIGHT RATE');
          INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('2', '1', 'MIN', 'SLAB', 'BR', 'A FREIGHT RATE');
        OPEN v_curr FOR 'select TO_NUMBER(QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB--, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
      --AND QBD.Line_No>0
      AND QBD.Line_No>1
      and qbd.weight_break_slab!=''MIN''
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''1''  GROUP BY QBD.WEIGHT_BREAK_SLAB--, QBD.RATE_DESCRIPTION

      UNION

      SELECT  TO_NUMBER(QSR.WEIGHT_BREAK_SLAB)--, QSR.RATE_DESCRIPTION TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB,  QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
      --   AND QSR.Line_No>0
      AND QSR.Line_No>1
      AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
      and QSR.WEIGHT_BREAK_SLAB !=''MIN''
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''1''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB ORDER BY WEIGHT_BREAK_SLAB';

        countslab := 3; --kishore

        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB;--, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countslab, '1', V_WTBREAKSLAB, 'SLAB', 'BR', 'A FREIGHT RATE');
          countslab := countslab + 1;

        END LOOP;
        Close v_curr;


        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'select QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      AND QBD.SERVICE_LEVEL=''SCH''
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''1''  GROUP BY QBD.WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION

      UNION

      SELECT  QSR.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION --TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB,  QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
      AND QSR.Line_No>0
      AND QSR.SERVICELEVEL=''SCH''
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''1''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION ORDER BY WEIGHT_BREAK_SLAB';


        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
            (countslab, '1', V_WTBREAKSLAB, 'SLAB', 'BR', V_DESC_TEMP);
          countslab := countslab + 1;

        END LOOP;
        Close v_curr;



        /* INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab,'1','FSBASIC','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab+1,'1','FSMIN','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab+2,'1','FSKG','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab+3,'1','SSBASIC','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab+4,'1','SSMIN','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
            VALUES(countslab+5,'1','SSKG','SLAB','BR','') ;*/

        /*--Kishore
         FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  BM.SHIPMENT_MODE = 1
                 AND BM.WEIGHT_BREAK = 'SLAB'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = BM.BUYRATEID
                 AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = BM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
        INSERT INTO RATES_HEADER_EXCEL
                    VALUES(countslab,'1',K.WEIGHT_BREAK_SLAB,'SLAB','BR',K.RATE_DESCRIPTION) ;

             countslab :=countslab+1;
        END LOOP;
        -- Kishore*/

      END IF;
      IF J.WEIGHT_BREAK = 'SLAB' AND J.SHIPMENT_MODE = '2' THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';

        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('1', '2', 'BASIC', 'SLAB', 'BR', 'A FREIGHT RATE');

        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          ('2', '2', 'MIN', 'SLAB', 'BR', 'A FREIGHT RATE');

        OPEN v_curr FOR 'SELECT  TO_NUMBER(QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
       and QBD.WEIGHT_BREAK_SLAB !=''MIN''
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''2''  GROUP BY QBD.WEIGHT_BREAK_SLAB --order by WEIGHT_BREAK_SLAB
      UNION
      SELECT TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
         AND QSR.Line_No>0
      AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
       and QSR.WEIGHT_BREAK_SLAB !=''MIN''
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''2''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB ORDER BY WEIGHT_BREAK_SLAB';

        countslab1 := 3; --kishore

        LOOP

          FETCH v_curr
            INTO V_WTBREAKSLAB;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countslab1, '2', V_WTBREAKSLAB, 'SLAB', 'BR', 'A FREIGHT RATE');
          countslab1 := countslab1 + 1;
        END LOOP;
        CLOSE v_curr;

         V_WTBREAKSLAB := '';
         V_DESC_TEMP   := '';
         OPEN v_curr FOR 'SELECT  (QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB,QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      and QBD.SERVICE_LEVEL = ''SCH''
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''2''  GROUP BY QBD.WEIGHT_BREAK_SLAB,QBD.RATE_DESCRIPTION
      UNION
      SELECT  QSR.WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION --TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
      AND QSR.Line_No>0
      AND  QSR.SERVICELEVEL = ''SCH''
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''2''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION ';

        LOOP

          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
            (countslab1, '2', V_WTBREAKSLAB, 'SLAB', 'BR', V_DESC_TEMP);
          countslab1 := countslab1 + 1;
        END LOOP;
        CLOSE v_curr;


        /*INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1,'2','BAFMIN','SLAB','BR','');
          INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1+1,'2','BAFM3','SLAB','BR','');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1+2,'2','CAFMIN','SLAB','BR','');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1+3,'2','CAF%','SLAB','BR','');
        INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1+4,'2','PSSMIN','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
           VALUES(countslab1+5,'2','PSSM3','SLAB','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab1+6,'2','CSF','SLAB','BR','');*/

        --Kishore
        /*FOR K IN
                  (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                   FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
                  WHERE  BM.SHIPMENT_MODE = 2
                    AND BM.WEIGHT_BREAK = 'SLAB'
                    AND BD.SERVICE_LEVEL = 'SCH'
                    and qm.customer_id= p_customerid
                    AND Qm.ACTIVE_FLAG = 'A'
                    AND QM.COMPLETE_FLAG = 'C'|| v_sql
                    AND BD.ACTIVEINACTIVE IS NULL
                    AND BD.BUYRATEID = BM.BUYRATEID
                    AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                    AND BD.VERSION_NO = BM.VERSION_NO
                    and qm.id = qr.quote_id
                    and qr.buyrate_id = bd.buyrateid
                    --and bd.buyrateid = qr.buyrate_id
                  ORDER BY BD.RATE_DESCRIPTION)
            LOOP
        INSERT INTO RATES_HEADER_EXCEL
                       VALUES(countslab1,'2',K.WEIGHT_BREAK_SLAB,'SLAB','BR',K.RATE_DESCRIPTION) ;

                 countslab1 :=countslab1+1;
           END LOOP;*/
        -- Kishore

      END IF;

      IF J.WEIGHT_BREAK = 'SLAB' AND J.SHIPMENT_MODE = '4' THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'SELECT  TO_NUMBER(QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
       --AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE'' or QBD.SERVICE_LEVEL = ''SCH'')
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''4''  GROUP BY QBD.WEIGHT_BREAK_SLAB ORDER BY WEIGHT_BREAK_SLAB
      UNION
      SELECT  TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
         AND QSR.Line_No>0
      AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
     --AND (QSR.RATE_DESCRIPTION=''A FREIGHT RATE'' or QSR.SERVICELEVEL = ''SCH'')
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''4''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB ORDER BY WEIGHT_BREAK_SLAB';

        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countslab2, '4', V_WTBREAKSLAB, 'SLAB', 'BR', 'A FREIGHT RATE');
          countslab2 := countslab2 + 1;
        END LOOP;
        CLOSE v_curr;


        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'SELECT  QBD.WEIGHT_BREAK_SLAB,QBD.RATE_DESCRIPTION --TO_NUMBER(QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      AND QBD.SERVICE_LEVEL = ''SCH''
       --AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE'' or QBD.SERVICE_LEVEL = ''SCH'')
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''SLAB'' AND QBM.SHIPMENT_MODE=''4''  GROUP BY QBD.WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
      UNION
      SELECT  QSR.WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION -- TO_NUMBER(QSR.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
         AND QSR.Line_No>0
      AND QSR.SERVICELEVEL = ''SCH''
      --AND (QSR.RATE_DESCRIPTION=''A FREIGHT RATE'' or QSR.SERVICELEVEL = ''SCH'')
      AND QSR.WEIGHT_BREAK=''SLAB'' AND QSR.SHIPMENT_MODE=''4''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION ';

        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
            (countslab2, '4', V_WTBREAKSLAB, 'SLAB', 'BR', V_DESC_TEMP);
          countslab2 := countslab2 + 1;
        END LOOP;
        CLOSE v_curr;

        /*INSERT INTO RATES_HEADER_EXCEL
        VALUES(countslab2,'4','SURCHARGE','SLAB','BR','');*/

        --Kishore
        /*FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  BM.SHIPMENT_MODE = 4
                 AND BM.WEIGHT_BREAK = 'SLAB'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = BM.BUYRATEID
                 AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = BM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
           INSERT INTO RATES_HEADER_EXCEL
                    VALUES(countslab2,'4',K.WEIGHT_BREAK_SLAB,'SLAB','BR',K.RATE_DESCRIPTION) ;

              countslab2 :=countslab2+1;
        END LOOP;*/
        -- Kishore

      END IF;

      IF J.WEIGHT_BREAK = 'LIST' AND J.SHIPMENT_MODE = '1' /*AND J.SELL_BUY_FLAG IN ('BR','RSR')*/
       THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'SELECT  QBD.WEIGHT_BREAK_SLAB,QBD.RATE_DESCRIPTION --TO_NUMBER(QBD.WEIGHT_BREAK_SLAB) WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      --AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
        AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE'' or QBD.SERVICE_LEVEL = ''SCH'')
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''LIST'' AND QBM.SHIPMENT_MODE=''1''  GROUP BY QBD.WEIGHT_BREAK_SLAB,QBD.RATE_DESCRIPTION
       UNION
       SELECT  QSR.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
         AND QSR.Line_No>0
      -- AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
        AND (QSR.RATE_DESCRIPTION=''A FREIGHT RATE'' or QSR.SERVICELEVEL = ''SCH'')
      AND QSR.WEIGHT_BREAK=''LIST'' AND QSR.SHIPMENT_MODE=''1''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB,QSR.RATE_DESCRIPTION ORDER BY WEIGHT_BREAK_SLAB';
        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countlist1, '1', V_WTBREAKSLAB, 'LIST', 'BR', V_DESC_TEMP);
          countlist1 := countlist1 + 1;
        END LOOP;
        CLOSE v_curr;
        /*INSERT INTO RATES_HEADER_EXCEL
           VALUES(countlist1,'1','OVER PIVOT','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+1,'1','FSBASIC','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+2,'1','FSMIN','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+3,'1','FSKG','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+4,'1','SSBASIC','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+5,'1','SSMIN','LIST','BR','') ;
        INSERT INTO RATES_HEADER_EXCEL
         VALUES(countlist1+6,'1','SSKG','LIST','BR','') ;*/

        --Kishore
        /*FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  BM.SHIPMENT_MODE = 1
                 AND BM.WEIGHT_BREAK = 'LIST'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = BM.BUYRATEID
                 AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = BM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
        INSERT INTO RATES_HEADER_EXCEL
                    VALUES(countlist1,'1',K.WEIGHT_BREAK_SLAB,'LIST','BR',K.RATE_DESCRIPTION) ;

              countlist1 :=countlist1+1;
        END LOOP;*/
        -- Kishore

      END IF;

      IF J.WEIGHT_BREAK = 'LIST' AND J.SHIPMENT_MODE = '2' THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'SELECT  QBD.WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      --AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
        AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE'' or QBD.SERVICE_LEVEL = ''SCH'')
      AND QBM.WEIGHT_BREAK=''LIST'' AND QBM.SHIPMENT_MODE=''2'' GROUP BY QBD.WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION  ORDER BY QBD.WEIGHT_BREAK_SLAB';
        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countlist2, '2', V_WTBREAKSLAB, 'LIST', 'BR', V_DESC_TEMP);
          countlist2 := countlist2 + 1;
        END LOOP;
        CLOSE v_curr;

        --Kishore
        /*FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  BM.SHIPMENT_MODE = 2
                 AND BM.WEIGHT_BREAK = 'LIST'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = BM.BUYRATEID
                 AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = BM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
             INSERT INTO RATES_HEADER_EXCEL
                    VALUES(countlist2,'2',K.WEIGHT_BREAK_SLAB,'LIST','BR',K.RATE_DESCRIPTION) ;

              countlist2 :=countlist2+1;
        END LOOP;*/
        -- Kishore

      END IF;
      IF J.WEIGHT_BREAK = 'LIST' AND J.SHIPMENT_MODE = '4' THEN
        V_WTBREAKSLAB := '';
        V_DESC_TEMP   := '';
        OPEN v_curr FOR 'SELECT  QBD.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
     FROM QMS_BUYRATES_DTL    QBD,
          QMS_QUOTE_RATES     QR,
          QMS_QUOTE_MASTER    QM,
          QMS_BUYRATES_MASTER QBM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sql || '
      AND QM.ACTIVE_FLAG =''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBM.BUYRATEID
      AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QR.VERSION_NO = QBM.VERSION_NO
      AND QBM.BUYRATEID = QBD.BUYRATEID
      AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBM.VERSION_NO = QBD.VERSION_NO
      --AND QBD.RATE_DESCRIPTION=''A FREIGHT RATE''
      AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE'' or QBD.SERVICE_LEVEL = ''SCH'')
      AND QBD.Line_No>0
      AND QBM.WEIGHT_BREAK=''LIST'' AND QBM.SHIPMENT_MODE=''4''  GROUP BY QBD.WEIGHT_BREAK_SLAB, QBD.RATE_DESCRIPTION
      UNION  SELECT  QSR.WEIGHT_BREAK_SLAB WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A''
      AND QM.ID = QSR.QUOTE_ID
         AND QSR.Line_No>0
      --AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
      AND (QSR.RATE_DESCRIPTION=''A FREIGHT RATE'' or QSR.SERVICELEVEL = ''SCH'')
      AND QSR.WEIGHT_BREAK=''LIST'' AND QSR.SHIPMENT_MODE=''4''  AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB AND WEIGHTBREAK=QSR.WEIGHT_BREAK AND SHIPMENT_MODE=QSR.SHIPMENT_MODE)  GROUP BY QSR.WEIGHT_BREAK_SLAB ,QSR.RATE_DESCRIPTION ORDER BY WEIGHT_BREAK_SLAB';
        LOOP
          FETCH v_curr
            INTO V_WTBREAKSLAB, V_DESC_TEMP;
          EXIT WHEN v_curr%NOTFOUND;
          INSERT INTO RATES_HEADER_EXCEL
          VALUES
            (countlist3, '4', V_WTBREAKSLAB, 'LIST', 'BR', V_DESC_TEMP);
          countlist3 := countlist3 + 1;
        END LOOP;
        CLOSE v_curr;
        /*INSERT INTO RATES_HEADER_EXCEL
        VALUES(countlist3,'4','SURCHARGE','LIST','BR','') ;*/

        --Kishore
        /* FOR K IN
               (SELECT DISTINCT BD.WEIGHT_BREAK_SLAB, BD.RATE_DESCRIPTION
                FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM,QMS_QUOTE_MASTER qm, qms_quote_rates qr
               WHERE  BM.SHIPMENT_MODE = 4
                 AND BM.WEIGHT_BREAK = 'LIST'
                 AND BD.SERVICE_LEVEL = 'SCH'
                 and qm.customer_id= p_customerid
                 AND Qm.ACTIVE_FLAG = 'A'
                 AND QM.COMPLETE_FLAG = 'C'|| v_sql
                 AND BD.ACTIVEINACTIVE IS NULL
                 AND BD.BUYRATEID = BM.BUYRATEID
                 AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL)
                 AND BD.VERSION_NO = BM.VERSION_NO
                 and qm.id = qr.quote_id
                 and qr.buyrate_id = bd.buyrateid
                 --and bd.buyrateid = qr.buyrate_id
               ORDER BY BD.RATE_DESCRIPTION)
         LOOP
        INSERT INTO RATES_HEADER_EXCEL
                    VALUES(countlist3,'4',K.WEIGHT_BREAK_SLAB,'LIST','BR',K.RATE_DESCRIPTION) ;

              countlist3 :=countlist3+1;
        END LOOP;*/
        -- Kishore
      END IF;
    END LOOP;

    FOR J IN (SELECT WEIGHT_BREAK, SHIPMENT_MODE, SELL_BUY_FLAG
                FROM RATES_MODE
               WHERE WEIGHT_BREAK = 'LIST'
                 AND SHIPMENT_MODE = '2')

     LOOP

      V_WTBREAKSLAB := '';
      V_DESC_TEMP   := '';
      OPEN v_curr FOR 'SELECT  QSR.WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION
     FROM QMS_QUOTE_SPOTRATES QSR,
          QMS_QUOTE_MASTER    QM
    WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' || v_sqll || '
      AND QM.ACTIVE_FLAG = ''A'' AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
       AND QSR.RATE_DESCRIPTION=''A FREIGHT RATE''
      AND QSR.WEIGHT_BREAK=''LIST'' AND QSR.SHIPMENT_MODE=''2'' /*AND NOT EXISTS (SELECT ''X'' FROM RATES_HEADER_EXCEL WHERE WEIGHTBREAKSLAB =QSR.WEIGHT_BREAK_SLAB)*/ GROUP BY QSR.WEIGHT_BREAK_SLAB, QSR.RATE_DESCRIPTION ORDER BY QSR.WEIGHT_BREAK_SLAB';
      LOOP
        FETCH v_curr
          INTO V_WTBREAKSLAB, V_DESC_TEMP;
        EXIT WHEN v_curr%NOTFOUND;
        INSERT INTO RATES_HEADER_EXCEL
        VALUES
          (countlist2, '2', V_WTBREAKSLAB, 'LIST', 'SBR', V_DESC_TEMP);
        countlist2 := countlist2 + 1;
      END LOOP;
      CLOSE v_curr;

    END LOOP;
    /*V_SQL4 := ' INSERT INTO TEMP_TABLE_EXCEL (SELECT QM.QUOTE_ID,
          QM.SHIPMENT_MODE,
          QR.BUYRATE_ID,QR.RATE_LANE_NO,QR.VERSION_NO,QR.SELLRATE_ID,QR.SELL_BUY_FLAG,
           (SELECT FR.COUNTRYID
             FROM FS_FR_LOCATIONMASTER FR
            WHERE FR.LOCATIONID = QM.ORIGIN_LOCATION)ORIGIN_COUNTRY ,QBD.ORIGIN,
          QBD.DESTINATION,

          (SELECT CA.CARRIERNAME FROM FS_FR_CAMASTER CA WHERE CARRIERID = QBD.CARRIER_ID)CARRIERNAME,
          QBD.TRANSIT_TIME,
          QBM.CURRENCY,
          QBD.FREQUENCY,
          QBM.WEIGHT_BREAK,

          QBD.VALID_UPTO,
           (SELECT FR.COUNTRYID
             FROM FS_FR_LOCATIONMASTER FR
            WHERE FR.LOCATIONID = QM.DEST_LOCATION)DESTI_COUNTRY,
          QBD.DENSITY_CODE,
          Decode((select count(qdg.kg_per_m3) from qms_density_group_code qdg where qdg.kg_per_m3=QBD.DENSITY_CODE),0,''LB'',''KG''),
          QM.ID,
          QBD.SERVICE_LEVEL
     FROM QMS_QUOTE_MASTER    QM,
          QMS_QUOTE_RATES     QR,
          QMS_BUYRATES_MASTER QBM,
          QMS_BUYRATES_DTL    QBD

    WHERE QM.CUSTOMER_ID = '||''''||p_customerid||''''||v_sql||'
    AND Qm.ACTIVE_FLAG=''A''
    AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QR.QUOTE_ID
      AND QR.BUYRATE_ID = QBD.BUYRATEID
      AND QR.RATE_LANE_NO = QBD.LANE_NO
      AND QR.VERSION_NO = QBD.VERSION_NO
      AND QBD.BUYRATEID = QBM.BUYRATEID
      AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
      AND QBD.VERSION_NO = QBM.VERSION_NO
      AND QR.LINE_NO=QBD.LINE_NO
     AND QBD.LINE_NO=''0'' ) ';*/
    V_SQL4 := ' INSERT INTO TEMP_TABLE_EXCEL (SELECT QM.QUOTE_ID,
        QM.SHIPMENT_MODE,
        QR.BUYRATE_ID,QR.RATE_LANE_NO,QR.VERSION_NO,QR.SELLRATE_ID,QR.SELL_BUY_FLAG,
         (SELECT FR.COUNTRYID
           FROM FS_FR_LOCATIONMASTER FR
          WHERE FR.LOCATIONID = QM.ORIGIN_LOCATION)ORIGIN_COUNTRY ,QBD.ORIGIN,
        QBD.DESTINATION,

        (SELECT CA.CARRIERNAME FROM FS_FR_CAMASTER CA WHERE CARRIERID = QBD.CARRIER_ID)CARRIERNAME,
        QBD.TRANSIT_TIME,
        QBM.CURRENCY,
        QBD.FREQUENCY,
        QBM.WEIGHT_BREAK,

        (SELECT  DISTINCT BD.VALID_UPTO FROM QMS_BUYRATES_DTL BD WHERE
         BD.BUYRATEID=QBD.BUYRATEID AND BD.LANE_NO=QBD.LANE_NO
        AND BD.VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_BUYRATES_DTL BD1 WHERE BD1.BUYRATEID=QBD.BUYRATEID AND BD1.LANE_NO=QBD.LANE_NO)
       ),
         (SELECT FR.COUNTRYID
           FROM FS_FR_LOCATIONMASTER FR
          WHERE FR.LOCATIONID = QM.DEST_LOCATION)DESTI_COUNTRY,
        QBD.DENSITY_CODE,
        Decode((select count(qdg.kg_per_m3) from qms_density_group_code qdg where qdg.kg_per_m3=QBD.DENSITY_CODE),0,''LB'',''KG''),
        QM.ID,
        QBD.SERVICE_LEVEL,
        QM.VALID_TO,QN.INTERNAL_NOTES,QN.EXTERNAL_NOTES
   FROM QMS_QUOTE_MASTER    QM,
	 			QMS_QUOTE_NOTES			QN,
        QMS_QUOTE_RATES     QR,
        QMS_BUYRATES_MASTER QBM,
        QMS_BUYRATES_DTL    QBD

  WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' ||
              v_sql || '
  AND Qm.ACTIVE_FLAG=''A''
  AND QM.COMPLETE_FLAG=''C''
    AND QM.ID = QR.QUOTE_ID
    AND QR.BUYRATE_ID = QBD.BUYRATEID
    AND QR.RATE_LANE_NO = QBD.LANE_NO
    AND QR.VERSION_NO = QBD.VERSION_NO
    AND QBD.BUYRATEID = QBM.BUYRATEID
    AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
    AND QBD.VERSION_NO = QBM.VERSION_NO
    AND QR.LINE_NO=QBD.LINE_NO
		AND QN.QUOTE_ID = QM.ID
   AND QBD.LINE_NO=''0'' ) ';

    EXECUTE IMMEDIATE (V_SQL4);

    FOR M IN (SELECT QUOTE_ID,
                     ID,
                     SHIPMENT_MODE,
                     BUYRATE_ID,
                     RATE_LANE_NO,
                     VERSION_NO,
                     SELLRATE_ID,
                     SELL_BUY_FLAG,
                     ORIGIN_COUNTRY,
                     ORIGIN,
                     DESTINATION,
                     DESTI_COUNTRY,
                     CARRIERNAME,
                     TRANSIT_TIME,
                     CURRENCY,
                     FREQUENCY,
                     WEIGHT_BREAK,
                     VALID_UPTO,
                     DENSITY_RATIO,
                     DENSITY_TYPE,
                     SERVICELEVEL,
                     QUOTE_VALID_UPTO,
                     INTERNAL_NOTES,
                     EXTERNAL_NOTES
                FROM TEMP_TABLE_EXCEL
               WHERE SELL_BUY_FLAG in ('BR', 'RSR')) LOOP
      V_RATE := '';

      IF M.SHIPMENT_MODE = '2' AND M.WEIGHT_BREAK = 'LIST' THEN
        FOR N IN (SELECT RH.WEIGHTBREAKSLAB
                    FROM RATES_HEADER_EXCEL RH
                   WHERE RH.SHIPMENT_MODE = M.SHIPMENT_MODE
                     AND RH.WEIGHTBREAK = M.WEIGHT_BREAK
                     AND SELL_BUY_FLAG in ('BR', 'RSR')
                   ORDER BY ROW_ID) LOOP
          V_RATE := '';

          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN
            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;
          END;
          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN
            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB || 'BAF';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;
          END;
          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN
            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB || 'CAF';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;
          END;
          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN

            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB || 'CSF';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;
          END;
          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN

            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB || 'PSS';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;

          END;
          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

          -- INSERT INTO RATES_EXCEL VALUES(M.QUOTE_ID,M.SHIPMENT_MODE,M.WEIGHT_BREAK,M.ORIGIN_COUNTRY,M.ORIGIN,M.DESTINATION,M.CARRIERNAME,M.TRANSIT_TIME,M.CURRENCY,V_RATE,M.VALID_UPTO,M.SELL_BUY_FLAG,M.FREQUENCY,N.WEIGHTBREAKSLAB,M.DESTI_COUNTRY,M.DENSITY_RATIO,M.DENSITY_TYPE,M.SERVICELEVEL);
          INSERT INTO RATES_EXCEL
          VALUES
            (M.QUOTE_ID,
             M.SHIPMENT_MODE,
             M.WEIGHT_BREAK,
             M.ORIGIN_COUNTRY,
             M.ORIGIN,
             M.DESTINATION,
             M.CARRIERNAME,
             M.TRANSIT_TIME,
             M.CURRENCY,
             V_RATE,
             M.VALID_UPTO,
             M.SELL_BUY_FLAG,
             M.FREQUENCY,
             N.WEIGHTBREAKSLAB,
             M.DESTI_COUNTRY,
             M.DENSITY_RATIO,
             M.DENSITY_TYPE,
             M.SERVICELEVEL,
             M.QUOTE_VALID_UPTO,
             m.internal_notes,
             m.external_notes);

        END LOOP;
      ELSE
        V_RATE := NULL; --kishore
        FOR N IN (SELECT RH.WEIGHTBREAKSLAB, rh.surcharge_desc
                    FROM RATES_HEADER_EXCEL RH
                   WHERE RH.SHIPMENT_MODE = M.SHIPMENT_MODE
                     AND RH.WEIGHTBREAK = M.WEIGHT_BREAK
                   ORDER BY ROW_ID) LOOP
          V_MARGINDISCOUNTFLAG := NULL;
          BEGIN
            SELECT BUY_RATE,
                   R_SELL_RATE,
                   MARGIN_DISCOUNT_FLAG,
                   MARGIN_TYPE,
                   DISCOUNT_TYPE,
                   MARGIN,
                   DISCOUNT
              INTO V_BUYRATE,
                   V_SELLRATE,
                   V_MARGINDISCOUNTFLAG,
                   V_MARGINTYPE,
                   V_DISCOUNTTYPE,
                   V_MARGIN,
                   V_DISCOUNT
              FROM QMS_QUOTE_RATES QBD
             WHERE QBD.QUOTE_ID = M.ID
               AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
               AND QBD.BREAK_POINT = N.WEIGHTBREAKSLAB
               and (qbd.charge_description = n.surcharge_desc or
                   qbd.charge_description = 'Freight Rate'); --kishore
          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              IF V_RATE IS NULL THEN
                V_RATE := '-';
              ELSE
                V_RATE := V_RATE || ',' || '-';
              END IF;
            --kishore on 15-Feb
             WHEN others THEN
              dbms_output.put_line(M.ID|| '    ' ||N.WEIGHTBREAKSLAB ||'  '||n.surcharge_desc);

            --Kishor on 15Feb
          END;

          IF V_MARGINDISCOUNTFLAG IS NOT NULL THEN
            IF V_MARGINDISCOUNTFLAG = 'M' THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;

              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
            ELSE
              IF V_DISCOUNTTYPE = 'A' THEN
                V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
              ELSE
                V_CHARGERATE := V_SELLRATE -
                                (V_SELLRATE * V_DISCOUNT) / 100;
              END IF;
            END IF;
            IF V_RATE IS NULL THEN
              V_RATE := V_RATE || V_CHARGERATE;
            ELSE
              V_RATE := V_RATE || ',' || V_CHARGERATE;
            END IF;
          END IF;

        END LOOP;
        -- INSERT INTO RATES_EXCEL VALUES(M.QUOTE_ID,M.SHIPMENT_MODE,M.WEIGHT_BREAK,M.ORIGIN_COUNTRY,M.ORIGIN,M.DESTINATION,M.CARRIERNAME,M.TRANSIT_TIME,M.CURRENCY,V_RATE,M.VALID_UPTO,M.SELL_BUY_FLAG,M.FREQUENCY,NULL,M.DESTI_COUNTRY,M.DENSITY_RATIO,M.DENSITY_TYPE,M.SERVICELEVEL);
        INSERT INTO RATES_EXCEL
        VALUES
          (M.QUOTE_ID,
           M.SHIPMENT_MODE,
           M.WEIGHT_BREAK,
           M.ORIGIN_COUNTRY,
           M.ORIGIN,
           M.DESTINATION,
           M.CARRIERNAME,
           M.TRANSIT_TIME,
           M.CURRENCY,
           V_RATE,
           M.VALID_UPTO,
           M.SELL_BUY_FLAG,
           M.FREQUENCY,
           NULL,
           M.DESTI_COUNTRY,
           M.DENSITY_RATIO,
           M.DENSITY_TYPE,
           M.SERVICELEVEL,
           M.QUOTE_VALID_UPTO,
           m.internal_notes,
           m.external_notes);
      END IF;
    END LOOP;
    /*V_SQL5 := ' INSERT INTO TEMP_TABLE_EXCEL (SELECT QM.QUOTE_ID,
          QM.SHIPMENT_MODE,'''','''','''','''',''SBR'',
          (SELECT FR.COUNTRYID
             FROM FS_FR_LOCATIONMASTER FR
            WHERE FR.LOCATIONID = QM.ORIGIN_LOCATION)ORIGIN_COUNTRY , DECODE(QM.SHIPMENT_MODE ,''2'',QM.ORIGIN_PORT,QM.ORIGIN_LOCATION)ORIGIN_LOCATION,
          DECODE(QM.SHIPMENT_MODE ,''2'',QM.DESTIONATION_PORT,QM.DEST_LOCATION)DEST_LOCATION,
        '''',
          '''',
          QSR.CURRENCYID,
          '''',
          QSR.WEIGHT_BREAK,
          '''',
          (SELECT FR.COUNTRYID
             FROM FS_FR_LOCATIONMASTER FR
            WHERE FR.LOCATIONID = QM.DEST_LOCATION)DESTI_COUNTRY,
          QSR.DENSITY_CODE,
          Decode((select count(qdg.kg_per_m3) from qms_density_group_code qdg where qdg.kg_per_m3=QSR.DENSITY_CODE),0,''LB'',''KG''),
          QM.ID,
          QSR.SERVICELEVEL
     FROM QMS_QUOTE_MASTER    QM,
          QMS_QUOTE_SPOTRATES QSR
    WHERE QM.CUSTOMER_ID = '||''''||p_customerid||''''||v_sqll||'
    AND Qm.ACTIVE_FLAG=''A''
    AND QM.COMPLETE_FLAG=''C''
      AND QM.ID = QSR.QUOTE_ID
       AND QSR.LINE_NO=''0'' ) ';*/

    V_SQL5 := ' INSERT INTO TEMP_TABLE_EXCEL (SELECT distinct QM.QUOTE_ID,
        QM.SHIPMENT_MODE,'''','''','''','''',''SBR'',
        (SELECT FR.COUNTRYID
           FROM FS_FR_LOCATIONMASTER FR
          WHERE FR.LOCATIONID = QM.ORIGIN_LOCATION)ORIGIN_COUNTRY , DECODE(QM.SHIPMENT_MODE ,''2'',QM.ORIGIN_PORT,QM.ORIGIN_LOCATION)ORIGIN_LOCATION,
        DECODE(QM.SHIPMENT_MODE ,''2'',QM.DESTIONATION_PORT,QM.DEST_LOCATION)DEST_LOCATION,
 qr.carrier CARRIERNAME,
       qr.transit_time TRANSIT_TIME,
       QSR.CURRENCYID,
       qr.frequency FREQUENCY,
        QSR.WEIGHT_BREAK,
        '''',
        (SELECT FR.COUNTRYID
           FROM FS_FR_LOCATIONMASTER FR
          WHERE FR.LOCATIONID = QM.DEST_LOCATION)DESTI_COUNTRY,
        QSR.DENSITY_CODE,
        Decode((select count(qdg.kg_per_m3) from qms_density_group_code qdg where qdg.kg_per_m3=QSR.DENSITY_CODE),0,''LB'',''KG''),
        QM.ID,
        QSR.SERVICELEVEL,
        QM.VALID_TO,QN.INTERNAL_NOTES,QN.EXTERNAL_NOTES
   FROM QMS_QUOTE_MASTER    QM,
	 			QMS_QUOTE_NOTES			QN,
        QMS_QUOTE_SPOTRATES QSR ,qms_quote_rates QR
  WHERE QM.CUSTOMER_ID = ' || '''' || p_customerid || '''' ||
              v_sqll || '
  AND Qm.ACTIVE_FLAG=''A''
	AND QN.QUOTE_ID = QM.ID
  AND QM.COMPLETE_FLAG=''C'' AND QR.QUOTE_ID=QM.ID AND   QSR.QUOTE_ID=QR.QUOTE_ID
    AND QM.ID = QSR.QUOTE_ID
     AND QSR.LINE_NO=''0'' ) ';
    IF p_spotrateflag = 'on' THEN
      EXECUTE IMMEDIATE (V_SQL5);
    END IF;
    IF p_spotrateflag = 'on' THEN
      FOR M IN (SELECT QUOTE_ID,
                       ID,
                       SHIPMENT_MODE,
                       SELL_BUY_FLAG,
                       ORIGIN_COUNTRY,
                       ORIGIN,
                       CARRIERNAME,
                       TRANSIT_TIME,
                       FREQUENCY,
                       DESTINATION,
                       DESTI_COUNTRY,
                       CURRENCY,
                       WEIGHT_BREAK,
                       DENSITY_RATIO,
                       DENSITY_TYPE,
                       SERVICELEVEL,
                       QUOTE_VALID_UPTO,
                       INTERNAL_NOTES,
                       EXTERNAL_NOTES
                  FROM TEMP_TABLE_EXCEL
                 WHERE SELL_BUY_FLAG = 'SBR') LOOP
        V_RATE := NULL;

        IF M.SHIPMENT_MODE = '2' AND M.WEIGHT_BREAK = 'LIST' THEN
          FOR N IN (SELECT RH.WEIGHTBREAKSLAB
                      FROM RATES_HEADER_EXCEL RH
                     WHERE RH.SHIPMENT_MODE = M.SHIPMENT_MODE
                       AND RH.WEIGHTBREAK = M.WEIGHT_BREAK
                       AND SELL_BUY_FLAG = 'SBR'
                     ORDER BY ROW_ID) LOOP
            V_RATE := '';

            V_MARGINTYPE := NULL;
            BEGIN
              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND QSR.BREAK_POINT = N.WEIGHTBREAKSLAB
                 AND SELL_BUY_FLAG = 'SBR';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;

            V_MARGINTYPE := NULL;
            BEGIN
              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND (QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'BAF%' or
                     QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'baf%')
                 AND SELL_BUY_FLAG = 'SBR';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN

                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;

            V_MARGINTYPE := NULL;
            BEGIN
              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND (QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'CAF%' or
                     QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'caf%')
                 AND SELL_BUY_FLAG = 'SBR';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN

                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;

            V_MARGINTYPE := NULL;
            BEGIN
              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND (QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'CSF%' or
                     QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'csf%')
                 AND SELL_BUY_FLAG = 'SBR';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;

            V_MARGINTYPE := NULL;
            BEGIN
              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND (QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'PSS%' or
                     QSR.BREAK_POINT LIKE N.WEIGHTBREAKSLAB || 'pss%')
                 AND SELL_BUY_FLAG = 'SBR';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN

                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;
            -- INSERT INTO RATES_EXCEL VALUES(M.QUOTE_ID,M.SHIPMENT_MODE,M.WEIGHT_BREAK,M.ORIGIN_COUNTRY,M.ORIGIN,M.DESTINATION,NULL,NULL,M.CURRENCY,V_RATE,NULL,M.SELL_BUY_FLAG,NULL,N.WEIGHTBREAKSLAB,M.DESTI_COUNTRY,M.DENSITY_RATIO,M.DENSITY_TYPE,M.SERVICELEVEL);
            INSERT INTO RATES_EXCEL
            VALUES
              (M.QUOTE_ID,
             M.SHIPMENT_MODE,
             M.WEIGHT_BREAK,
             M.ORIGIN_COUNTRY,
             M.ORIGIN,
             M.DESTINATION,
             M.CARRIERNAME,
             M.TRANSIT_TIME,
             M.CURRENCY,
             V_RATE,
               NULL,
             M.SELL_BUY_FLAG,
             M.FREQUENCY,
             N.WEIGHTBREAKSLAB,
             M.DESTI_COUNTRY,
             M.DENSITY_RATIO,
             M.DENSITY_TYPE,
             M.SERVICELEVEL,
             M.QUOTE_VALID_UPTO,
             m.internal_notes,
             m.external_notes);

          END LOOP;
        ELSE

          FOR N IN (SELECT RH.WEIGHTBREAKSLAB
                      FROM RATES_HEADER_EXCEL RH
                     WHERE RH.SHIPMENT_MODE = M.SHIPMENT_MODE
                       AND RH.WEIGHTBREAK = M.WEIGHT_BREAK
                     ORDER By ROW_ID) LOOP

            V_MARGINTYPE := NULL;

            BEGIN

              SELECT QSR.BUY_RATE, MARGIN_TYPE, MARGIN
                INTO V_BUYRATE, V_MARGINTYPE, V_MARGIN
                FROM QMS_QUOTE_RATES QSR
               WHERE QSR.QUOTE_ID = M.ID
                 AND QSR.BREAK_POINT = N.WEIGHTBREAKSLAB
                 AND SELL_BUY_FLAG = 'SBR';

            EXCEPTION
              WHEN NO_DATA_FOUND THEN

                IF V_RATE IS NULL THEN
                  V_RATE := '-';
                ELSE
                  V_RATE := V_RATE || ',' || '-';
                END IF;
            END;
            IF V_MARGINTYPE IS NOT NULL THEN
              IF V_MARGINTYPE = 'A' THEN
                V_CHARGERATE := V_BUYRATE + V_MARGIN;
              ELSE
                V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
              END IF;
              IF V_RATE IS NULL THEN
                V_RATE := V_CHARGERATE;
              ELSE
                V_RATE := V_RATE || ',' || V_CHARGERATE;
              END IF;
            END IF;

          END LOOP;

          --INSERT INTO RATES_EXCEL VALUES(M.QUOTE_ID,M.SHIPMENT_MODE,M.WEIGHT_BREAK,M.ORIGIN_COUNTRY,M.ORIGIN,M.DESTINATION,NULL,NULL,M.CURRENCY,V_RATE,NULL,M.SELL_BUY_FLAG,NULL,NULL,M.DESTI_COUNTRY,M.DENSITY_RATIO,M.DENSITY_TYPE,M.SERVICELEVEL);
          INSERT INTO RATES_EXCEL
          VALUES
            (M.QUOTE_ID,
             M.SHIPMENT_MODE,
             M.WEIGHT_BREAK,
             M.ORIGIN_COUNTRY,
             M.ORIGIN,
             M.DESTINATION,
             M.CARRIERNAME,
             M.TRANSIT_TIME,
             M.CURRENCY,
             V_RATE,
               NULL,
             M.SELL_BUY_FLAG,
             M.FREQUENCY,
             NULL,
             M.DESTI_COUNTRY,
             M.DENSITY_RATIO,
             M.DENSITY_TYPE,
             M.SERVICELEVEL,
             M.QUOTE_VALID_UPTO,
             m.internal_notes,
             m.external_notes);

        END IF;
      END LOOP;
    END IF;

    -- Added By Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11


    EXECUTE IMMEDIATE ('TRUNCATE TABLE Rates_charges_excel');

    /*v_sql := '';
    IF p_origincity IS NOT NULL THEN
      if v_sql is null then
         v_sql := 'where re.origin_location = ' || '''' || p_origincity || '''';
      end if;

    END IF;

    IF p_origincity IS NULL AND p_origincountry IS NOT NULL THEN
       if v_sql is null then
           v_sql := ' where re.origin_location IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
               p_origincountry || '''' || ') ';
       else
          v_sql := ' AND re.origin_location IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
               p_origincountry || '''' || ') ';
       end if;
    END IF;

    IF p_destcity IS NOT NULL THEN
        if v_sql is null then
              v_sql :=  ' where re.dest_location = ' || '''' || p_destcity || '''';
        else
             v_sql :=   v_sql || ' AND re.dest_location = ' || '''' || p_destcity || '''';

        end if;
    END IF;

    IF p_destcity IS NULL AND p_destcountry IS NOT NULL THEN

       if v_sql is null then
                 v_sql :=
                       ' where re.dest_location IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
                   p_destcountry || '''' || ') ';
       else
                 v_sql := v_sql ||
                       ' AND re.dest_location IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
                   p_destcountry || '''' || ') ';
       end if;
    END IF;*/



    FOR QUOTE IN ( SELECT DISTINCT re.QUOTE_ID FROM  RATES_EXCEL re ) LOOP

        FOR LANE IN (SELECT ID
                       FROM QMS_QUOTE_MASTER
                      WHERE QUOTE_ID = QUOTE.QUOTE_ID
                        AND VERSION_NO =
                            (SELECT MAX(VERSION_NO)
                               FROM QMS_QUOTE_MASTER
                              WHERE QUOTE_ID = QUOTE.QUOTE_ID)) LOOP

               FOR I IN (SELECT DISTINCT QUOTE_ID,
                                SELL_BUY_FLAG,
                                BUYRATE_ID,
                                SELLRATE_ID,
                                RATE_LANE_NO,
                                VERSION_NO,
                                CHARGE_ID,
                                CHARGE_DESCRIPTION,
                                NVL(MARGIN_DISCOUNT_FLAG, 'M') MARGIN_DISCOUNT_FLAG,
                                MARGIN_TYPE,
                                NVL(MARGIN, 0) MARGIN,
                                DISCOUNT_TYPE,
                                NVL(DISCOUNT, 0) DISCOUNT,
                                NOTES,
                                QUOTE_REFNO,
                                BREAK_POINT,
                                CHARGE_AT,
                                BUY_RATE,
                                R_SELL_RATE,
                                RT_PLAN_ID,
                                SERIAL_NO,
                                MARGIN_TEST_FLAG,
                                CARRIER,
                                FREQUENCY_CHECKED,
                                CARRIER_CHECKED,
                                TRANSIT_CHECKED,
                                VALIDITY_CHECKED
                  FROM QMS_QUOTE_RATES
                 WHERE QUOTE_ID = LANE.ID) LOOP

                      IF UPPER(I.SELL_BUY_FLAG) = 'B' THEN

                          INSERT INTO Rates_charges_excel
                            (CHARGE_ID,
                             CHARGEDESCID,
                             ID,
                             COST_INCURREDAT,
                             CHARGESLAB,
                             CURRENCY,
                             BUYRATE,
                             SELLRATE,
                             MARGIN_TYPE,
                             MARGINVALUE,
                             CHARGEBASIS,
                             SEL_BUY_FLAG,
                             BUY_CHARGE_ID,
                             TERMINALID,
                             WEIGHT_BREAK,
                             WEIGHT_SCALE,
                             RATE_TYPE,
                             PRIMARY_BASIS,
                             SECONDARY_BASIS,
                             TERTIARY_BASIS,
                             BLOCK,
                             DENSITY_RATIO,
                             LBOUND,
                             UBOUND,
                             RATE_INDICATOR,
                             MARGIN_DISCOUNT_FLAG,
                             LINE_NO,
                             INT_CHARGE_NAME,
                             EXT_CHARGE_NAME,
                             MARGIN_TEST_FLAG,
                             RATE_DESCRIPTION,
                             ORG,
                             DEST,
                             QUOTE_ID,
                             QUOTE_UNIQUE_ID)
                            SELECT I.CHARGE_ID,
                                   I.CHARGE_DESCRIPTION,
                                   (SELECT MAX(ID)
                                      FROM QMS_CHARGE_GROUPSMASTER
                                     WHERE CHARGEGROUP_ID IN
                                           (SELECT CHARGEGROUPID
                                              FROM QMS_QUOTE_CHARGEGROUPDTL
                                             WHERE QUOTE_ID = LANE.ID)
                                       AND CHARGE_ID = I.CHARGE_ID
                                       AND CHARGEDESCID = I.CHARGE_DESCRIPTION) ID,
                                   I.CHARGE_AT,
                                   QCD.CHARGESLAB,
                                   QCM.CURRENCY,
                                   QCD.CHARGERATE,
                                   0,
                                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN_TYPE),
                                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                                   (SELECT BASIS_DESCRIPTION
                                      FROM QMS_CHARGE_BASISMASTER
                                     WHERE CHARGEBASIS = QCM.CHARGEBASIS) UOM,
                                   I.SELL_BUY_FLAG,
                                   QCM.BUYSELLCHARGEID,
                                   QCM.TERMINALID,
                                   QCM.RATE_BREAK,
                                   QCM.WEIGHT_CLASS,
                                   QCM.RATE_TYPE,
                                   (SELECT PRIMARY_BASIS
                                      FROM QMS_CHARGE_BASISMASTER
                                     WHERE CHARGEBASIS = QCM.CHARGEBASIS) PRIMARY_BASIS,
                                   (SELECT SECONDARY_BASIS
                                      FROM QMS_CHARGE_BASISMASTER
                                     WHERE CHARGEBASIS = QCM.CHARGEBASIS) SECONDARY_BASIS,
                                   (SELECT TERTIARY_BASIS
                                      FROM QMS_CHARGE_BASISMASTER
                                     WHERE CHARGEBASIS = QCM.CHARGEBASIS) TERTIARY_BASIS,
                                   (SELECT BLOCK
                                      FROM QMS_CHARGE_BASISMASTER
                                     WHERE CHARGEBASIS = QCM.CHARGEBASIS) BLOCK,
                                   QCM.DENSITY_CODE,
                                   QCD.LOWERBOUND,
                                   QCD.UPPERBOUND,
                                   QCD.CHARGERATE_INDICATOR,
                                   I.MARGIN_DISCOUNT_FLAG,
                                   QCD.LANE_NO,
                                   (SELECT REMARKS
                                      FROM QMS_CHARGEDESCMASTER     QCD,
                                           QMS_BUYSELLCHARGESMASTER BCM
                                     WHERE QCD.CHARGEID = I.CHARGE_ID
                                       AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                                       AND QCD.TERMINALID = BCM.TERMINALID
                                       AND BCM.BUYSELLCHARGEID = I.BUYRATE_ID
                                       AND QCD.INACTIVATE = 'N') INT_NAME,
                                   (SELECT EXT_CHARGE_NAME
                                      FROM QMS_CHARGEDESCMASTER     QCD,
                                           QMS_BUYSELLCHARGESMASTER BCM
                                     WHERE QCD.CHARGEID = I.CHARGE_ID
                                       AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                                       AND QCD.TERMINALID = BCM.TERMINALID
                                       AND BCM.BUYSELLCHARGEID = I.BUYRATE_ID
                                       AND INACTIVATE = 'N') EXT_NAME,
                                   I.MARGIN_TEST_FLAG,
                                   I.CHARGE_AT||'BUY CHARGES',
                                   (SELECT Y.Origin_Location
                                      FROM QMS_QUOTE_MASTER Y
                                     WHERE y.id = lane.id),
                                   (SELECT Y.Dest_Location
                                      FROM QMS_QUOTE_MASTER Y
                                     WHERE y.id = lane.id),
                                     QUOTE.QUOTE_ID,
                                     LANE.ID
                              FROM QMS_BUYSELLCHARGESMASTER QCM, QMS_BUYCHARGESDTL QCD
                             WHERE QCM.BUYSELLCHARGEID = I.BUYRATE_ID
                               AND QCD.CHARGESLAB = I.BREAK_POINT
                               AND QCM.BUYSELLCHARGEID = QCD.BUYSELLCHAEGEID;

                    ELSIF UPPER(I.SELL_BUY_FLAG) = 'S' THEN
                            INSERT INTO Rates_charges_excel
                              (SELLCHARGEID,
                               CHARGE_ID,
                               CHARGEDESCID,
                               ID,
                               COST_INCURREDAT,
                               CHARGESLAB,
                               CURRENCY,
                               BUYRATE,
                               SELLRATE,
                               MARGIN_TYPE,
                               MARGINVALUE,
                               CHARGEBASIS,
                               SEL_BUY_FLAG,
                               BUY_CHARGE_ID,
                               TERMINALID,
                               WEIGHT_BREAK,
                               WEIGHT_SCALE,
                               RATE_TYPE,
                               PRIMARY_BASIS,
                               SECONDARY_BASIS,
                               TERTIARY_BASIS,
                               BLOCK,
                               DENSITY_RATIO,
                               LBOUND,
                               UBOUND,
                               RATE_INDICATOR,
                               MARGIN_DISCOUNT_FLAG,
                               LINE_NO,
                               INT_CHARGE_NAME,
                               EXT_CHARGE_NAME,
                               MARGIN_TEST_FLAG,
                               RATE_DESCRIPTION,
                               ORG,
                               DEST,
                               QUOTE_ID,
                               QUOTE_UNIQUE_ID)
                              SELECT I.SELLRATE_ID,
                                     I.CHARGE_ID,
                                     I.CHARGE_DESCRIPTION,
                                     (SELECT MAX(ID)
                                        FROM QMS_CHARGE_GROUPSMASTER
                                       WHERE CHARGEGROUP_ID IN
                                             (SELECT CHARGEGROUPID
                                                FROM QMS_QUOTE_CHARGEGROUPDTL
                                               WHERE QUOTE_ID = LANE.ID)
                                         AND CHARGE_ID = I.CHARGE_ID
                                         AND CHARGEDESCID = I.CHARGE_DESCRIPTION) ID,
                                     I.CHARGE_AT,
                                     QSD.CHARGESLAB,
                                     QSM.CURRENCY,
                                     QSD.CHARGERATE,
                                     DECODE(QSM.MARGIN_TYPE,
                                            'P',
                                            QSD.CHARGERATE +
                                            (QSD.CHARGERATE * QSD.MARGINVALUE * 0.01),
                                            QSD.CHARGERATE + QSD.MARGINVALUE),
                                     DECODE(I.MARGIN_DISCOUNT_FLAG,
                                            'M',
                                            I.MARGIN_TYPE,
                                            I.DISCOUNT_TYPE),
                                     DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                                     (SELECT BASIS_DESCRIPTION
                                        FROM QMS_CHARGE_BASISMASTER
                                       WHERE CHARGEBASIS = QSM.CHARGEBASIS) BASIS,
                                     I.SELL_BUY_FLAG,
                                     QSM.BUYCHARGEID,
                                     QSM.TERMINALID,
                                     QSM.RATE_BREAK,
                                     QSM.WEIGHT_CLASS,
                                     QSM.RATE_TYPE,
                                     (SELECT PRIMARY_BASIS
                                        FROM QMS_CHARGE_BASISMASTER
                                       WHERE CHARGEBASIS = QSM.CHARGEBASIS) PRIMARY_BASIS,
                                     (SELECT SECONDARY_BASIS
                                        FROM QMS_CHARGE_BASISMASTER
                                       WHERE CHARGEBASIS = QSM.CHARGEBASIS) SECONDARY_BASIS,
                                     (SELECT TERTIARY_BASIS
                                        FROM QMS_CHARGE_BASISMASTER
                                       WHERE CHARGEBASIS = QSM.CHARGEBASIS) TERTIARY_BASIS,
                                     (SELECT BLOCK
                                        FROM QMS_CHARGE_BASISMASTER
                                       WHERE CHARGEBASIS = QSM.CHARGEBASIS) BLOCK,
                                     (SELECT DENSITY_CODE
                                        FROM QMS_BUYSELLCHARGESMASTER
                                       WHERE BUYSELLCHARGEID = QSM.BUYCHARGEID),
                                     QSD.LOWERBOUND,
                                     QSD.UPPERBOUND,
                                     QSD.CHARGERATE_INDICATOR,
                                     I.MARGIN_DISCOUNT_FLAG,
                                     QSD.LANE_NO,
                                     (SELECT REMARKS
                                        FROM QMS_CHARGEDESCMASTER  QCD,
                                             QMS_SELLCHARGESMASTER SCM
                                       WHERE QCD.CHARGEID = I.CHARGE_ID
                                         AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                                         AND QCD.TERMINALID = SCM.TERMINALID
                                         AND SCM.SELLCHARGEID = I.SELLRATE_ID
                                         AND QCD.INACTIVATE = 'N') INT_NAME,
                                     (SELECT EXT_CHARGE_NAME
                                        FROM QMS_CHARGEDESCMASTER  QCD,
                                             QMS_SELLCHARGESMASTER SCM
                                       WHERE QCD.CHARGEID = I.CHARGE_ID
                                         AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                                         AND QCD.TERMINALID = SCM.TERMINALID
                                         AND SCM.SELLCHARGEID = I.SELLRATE_ID
                                         AND INACTIVATE = 'N') EXT_NAME,
                                     I.MARGIN_TEST_FLAG,
                                     I.CHARGE_AT||' SELL CHARGES',
                                     (SELECT Y.Origin_Location
                                        FROM QMS_QUOTE_MASTER Y
                                       WHERE y.id = lane.id),
                                     (SELECT Y.Dest_Location
                                        FROM QMS_QUOTE_MASTER Y
                                       WHERE y.id = lane.id),
                                     QUOTE.QUOTE_ID,
                                     LANE.ID
                                FROM QMS_SELLCHARGESDTL QSD, QMS_SELLCHARGESMASTER QSM
                               WHERE QSM.SELLCHARGEID = I.SELLRATE_ID
                                 AND QSD.CHARGESLAB = I.BREAK_POINT
                                 AND QSM.SELLCHARGEID = QSD.SELLCHARGEID;

                      ELSIF UPPER(I.SELL_BUY_FLAG) = 'BC' THEN

                          IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
                            INSERT INTO Rates_charges_excel
                              (COST_INCURREDAT,
                               CHARGEDESCID,
                               CHARGESLAB,
                               CURRENCY,
                               BUYRATE,
                               SELLRATE,
                               MARGIN_TYPE,
                               MARGINVALUE,
                               CHARGEBASIS,
                               SEL_BUY_FLAG,
                               BUY_CHARGE_ID,
                               TERMINALID,
                               WEIGHT_BREAK,
                               WEIGHT_SCALE,
                               RATE_TYPE,
                               PRIMARY_BASIS,
                               SECONDARY_BASIS,
                               TERTIARY_BASIS,
                               BLOCK,
                               DENSITY_RATIO,
                               ZONE,
                               LBOUND,
                               UBOUND,
                               RATE_INDICATOR,
                               MARGIN_DISCOUNT_FLAG,
                               LINE_NO,
                               MARGIN_TEST_FLAG,
                               RATE_DESCRIPTION,
                               ORG,
                               DEST,
                               QUOTE_ID,
                               QUOTE_UNIQUE_ID)
                              SELECT DISTINCT I.CHARGE_AT,
                                              'Pickup Charge',
                                              QCD.CHARGESLAB,
                                              QCM.CURRENCY,
                                              QCD.CHARGERATE,
                                              0,
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN_TYPE),
                                              DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                                              (SELECT BASIS_DESCRIPTION
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              I.SELL_BUY_FLAG,
                                              QCD.CARTAGE_ID,
                                              QCM.TERMINALID,
                                              QCM.WEIGHT_BREAK,
                                              'G',
                                              QCM.RATE_TYPE,
                                              (SELECT PRIMARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT SECONDARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT TERTIARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT BLOCK
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              QCD.DENSITY_CODE,
                                              QCD.ZONE_CODE,
                                              QCD.LOWERBOUND,
                                              QCD.UPPERBOUND,
                                              QCD.CHARGERATE_INDICATOR,
                                              I.MARGIN_DISCOUNT_FLAG,
                                              QCD.LINE_NO,
                                              I.MARGIN_TEST_FLAG,
                                              'PICK UP CARTAGE BUY CHARGES',
                                              (SELECT Y.Origin_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                              (SELECT Y.Dest_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                             QUOTE.QUOTE_ID,
                                             LANE.ID
                                FROM QMS_CARTAGE_BUYDTL         QCD,
                                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                                     QMS_QUOTE_MASTER           QM
                               WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
                                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                                 AND QCD.CHARGESLAB = I.BREAK_POINT
                                 AND QM.ID = I.QUOTE_ID
                                 AND QCM.LOCATION_ID = QM.ORIGIN_LOCATION
                                 AND QCD.ZONE_CODE = QM.SHIPPERZONES
                                 AND QCD.CHARGE_TYPE = 'Pickup'
                                 AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE;

                          ELSE
                            INSERT INTO Rates_charges_excel
                              (COST_INCURREDAT,
                               CHARGEDESCID,
                               CHARGESLAB,
                               CURRENCY,
                               BUYRATE,
                               SELLRATE,
                               MARGIN_TYPE,
                               MARGINVALUE,
                               CHARGEBASIS,
                               SEL_BUY_FLAG,
                               BUY_CHARGE_ID,
                               TERMINALID,
                               WEIGHT_BREAK,
                               WEIGHT_SCALE,
                               RATE_TYPE,
                               PRIMARY_BASIS,
                               SECONDARY_BASIS,
                               TERTIARY_BASIS,
                               BLOCK,
                               DENSITY_RATIO,
                               ZONE,
                               LBOUND,
                               UBOUND,
                               RATE_INDICATOR,
                               MARGIN_DISCOUNT_FLAG,
                               LINE_NO,
                               MARGIN_TEST_FLAG,
                               RATE_DESCRIPTION,
                               ORG,
                               DEST,
                               QUOTE_ID,
                               QUOTE_UNIQUE_ID)
                              SELECT DISTINCT I.CHARGE_AT,
                                              'Delivery Charge',
                                              QCD.CHARGESLAB,
                                              QCM.CURRENCY,
                                              QCD.CHARGERATE,
                                              0,
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN_TYPE),
                                              DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                                              (SELECT BASIS_DESCRIPTION
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              I.SELL_BUY_FLAG,
                                              QCD.CARTAGE_ID,
                                              QCM.TERMINALID,
                                              QCM.WEIGHT_BREAK,
                                              'G',
                                              QCM.RATE_TYPE,
                                              (SELECT PRIMARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT SECONDARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT TERTIARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              (SELECT BLOCK
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QCD.CHARGE_BASIS),
                                              QCD.DENSITY_CODE,
                                              QCD.ZONE_CODE,
                                              QCD.LOWERBOUND,
                                              QCD.UPPERBOUND,
                                              QCD.CHARGERATE_INDICATOR,
                                              I.MARGIN_DISCOUNT_FLAG,
                                              QCD.LINE_NO,
                                              I.MARGIN_TEST_FLAG,
                                              'DELIVERY CARTAGE BUY CHARGES',
                                              (SELECT Y.Origin_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                              (SELECT Y.Dest_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                            QUOTE.QUOTE_ID,
                                            LANE.ID
                                FROM QMS_CARTAGE_BUYDTL         QCD,
                                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                                     QMS_QUOTE_MASTER           QM
                               WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
                                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                                 AND QCD.CHARGESLAB = I.BREAK_POINT
                                 AND QM.ID = I.QUOTE_ID
                                 AND QCM.LOCATION_ID = QM.DEST_LOCATION
                                 AND QCD.ZONE_CODE = QM.CONSIGNEEZONES
                                 AND QCD.CHARGE_TYPE = 'Delivery'
                                 AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE;
                               --  AND NVL(QCM.CONSOLE_TYPE, '~') = NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~'); -- Commented by Gowtham to show Origin and Destination Charges in PDF View
                          END IF;

                        ELSIF UPPER(I.SELL_BUY_FLAG) = 'SC' THEN
                          IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
                            INSERT INTO Rates_charges_excel
                              (SELLCHARGEID,
                               CHARGEDESCID,
                               COST_INCURREDAT,
                               CHARGESLAB,
                               CURRENCY,
                               BUYRATE,
                               SELLRATE,
                               MARGIN_TYPE,
                               MARGINVALUE,
                               CHARGEBASIS,
                               SEL_BUY_FLAG,
                               BUY_CHARGE_ID,
                               TERMINALID,
                               WEIGHT_BREAK,
                               WEIGHT_SCALE,
                               RATE_TYPE,
                               PRIMARY_BASIS,
                               SECONDARY_BASIS,
                               TERTIARY_BASIS,
                               BLOCK,
                               DENSITY_RATIO,
                               ZONE,
                               LBOUND,
                               UBOUND,
                               RATE_INDICATOR,
                               MARGIN_DISCOUNT_FLAG,
                               LINE_NO,
                               MARGIN_TEST_FLAG,
                               RATE_DESCRIPTION,
                               ORG,
                               DEST,
                               QUOTE_ID,
                               QUOTE_UNIQUE_ID)
                              SELECT DISTINCT I.SELLRATE_ID,
                                              'Pickup Charge',
                                              I.CHARGE_AT,
                                              QSD.CHARGESLAB,
                                              QCM.CURRENCY,
                                              QSD.BUYRATE_AMT,
                                              QSD.CHARGERATE,
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN_TYPE,
                                                     I.DISCOUNT_TYPE),
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN,
                                                     I.DISCOUNT),
                                              (SELECT BASIS_DESCRIPTION
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              I.SELL_BUY_FLAG,
                                              QSD.CARTAGE_ID,
                                              QCM.TERMINALID,
                                              QCM.WEIGHT_BREAK,
                                              'G',
                                              QCM.RATE_TYPE,
                                              (SELECT PRIMARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT SECONDARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT TERTIARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT BLOCK
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              QCD.DENSITY_CODE,
                                              QSD.ZONE_CODE,
                                              QSD.LOWERBOUND,
                                              QSD.UPPERBOUND,
                                              QSD.CHARGERATE_INDICATOR,
                                              I.MARGIN_DISCOUNT_FLAG,
                                              QSD.LINE_NO,
                                              I.MARGIN_TEST_FLAG,
                                              'PICK UP CARTAGE SELL CHARGES',
                                              (SELECT Y.Origin_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                              (SELECT Y.Dest_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                                QUOTE.QUOTE_ID,
                                                LANE.ID
                                FROM QMS_CARTAGE_SELLDTL        QSD,
                                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                                     QMS_CARTAGE_BUYDTL         QCD,
                                     QMS_QUOTE_MASTER           QM
                               WHERE QCM.CARTAGE_ID = QSD.CARTAGE_ID
                                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                                 AND QSD.CHARGESLAB = I.BREAK_POINT
                                 AND QSD.CARTAGE_ID = QCD.CARTAGE_ID
                                 AND QCD.CHARGE_TYPE = QSD.CHARGE_TYPE
                                 AND QCD.ZONE_CODE = QSD.ZONE_CODE
                                 AND QM.ID = I.QUOTE_ID
                                 AND QCM.LOCATION_ID = QM.ORIGIN_LOCATION
                                 AND QSD.ZONE_CODE = QM.SHIPPERZONES
                                 AND QSD.CHARGE_TYPE = 'Pickup'
                                 AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE
                                 /*AND NVL(QCM.CONSOLE_TYPE, '~') =
                                     NVL(QM.SHIPPER_CONSOLE_TYPE, '~')*/-- Commented by Gowtham to show Origin and Destination Charges in PDF View
                                 AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
                          ELSE
                            INSERT INTO Rates_charges_excel
                              (SELLCHARGEID,
                               CHARGEDESCID,
                               COST_INCURREDAT,
                               CHARGESLAB,
                               CURRENCY,
                               BUYRATE,
                               SELLRATE,
                               MARGIN_TYPE,
                               MARGINVALUE,
                               CHARGEBASIS,
                               SEL_BUY_FLAG,
                               BUY_CHARGE_ID,
                               TERMINALID,
                               WEIGHT_BREAK,
                               WEIGHT_SCALE,
                               RATE_TYPE,
                               PRIMARY_BASIS,
                               SECONDARY_BASIS,
                               TERTIARY_BASIS,
                               BLOCK,
                               DENSITY_RATIO,
                               ZONE,
                               LBOUND,
                               UBOUND,
                               RATE_INDICATOR,
                               MARGIN_DISCOUNT_FLAG,
                               LINE_NO,
                               MARGIN_TEST_FLAG,
                               RATE_DESCRIPTION,
                               ORG,
                               DEST,
                               QUOTE_ID,
                               QUOTE_UNIQUE_ID)
                              SELECT DISTINCT I.SELLRATE_ID,
                                              'Delivery Charge',
                                              I.CHARGE_AT,
                                              QSD.CHARGESLAB,
                                              QCM.CURRENCY,
                                              QSD.BUYRATE_AMT,
                                              QSD.CHARGERATE,
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN_TYPE,
                                                     I.DISCOUNT_TYPE),
                                              DECODE(I.MARGIN_DISCOUNT_FLAG,
                                                     'M',
                                                     I.MARGIN,
                                                     I.DISCOUNT),
                                              (SELECT BASIS_DESCRIPTION
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              I.SELL_BUY_FLAG,
                                              QSD.CARTAGE_ID,
                                              QCM.TERMINALID,
                                              QCM.WEIGHT_BREAK,
                                              'G',
                                              QCM.RATE_TYPE,
                                              (SELECT PRIMARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT SECONDARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT TERTIARY_BASIS
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              (SELECT BLOCK
                                                 FROM QMS_CHARGE_BASISMASTER
                                                WHERE CHARGEBASIS = QSD.CHARGE_BASIS),
                                              QCD.DENSITY_CODE,
                                              QSD.ZONE_CODE,
                                              QSD.LOWERBOUND,
                                              QSD.UPPERBOUND,
                                              QSD.CHARGERATE_INDICATOR,
                                              I.MARGIN_DISCOUNT_FLAG,
                                              QSD.LINE_NO,
                                              I.MARGIN_TEST_FLAG,
                                              'DELVERY CARTAGE SELL CHARGES',
                                              (SELECT Y.Origin_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                              (SELECT Y.Dest_Location
                                                 FROM QMS_QUOTE_MASTER Y
                                                WHERE y.id = lane.id),
                                                QUOTE.QUOTE_ID,
                                                LANE.ID
                                FROM QMS_CARTAGE_SELLDTL        QSD,
                                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                                     QMS_CARTAGE_BUYDTL         QCD,
                                     QMS_QUOTE_MASTER           QM
                               WHERE QCM.CARTAGE_ID = QSD.CARTAGE_ID
                                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                                 AND QSD.CHARGESLAB = I.BREAK_POINT
                                 AND QSD.CARTAGE_ID = QCD.CARTAGE_ID
                                 AND QCD.CHARGE_TYPE = QSD.CHARGE_TYPE
                                 AND QCD.ZONE_CODE = QSD.ZONE_CODE
                                 AND QM.ID = I.QUOTE_ID
                                 AND QCM.LOCATION_ID = QM.DEST_LOCATION
                                 AND QSD.ZONE_CODE = QM.CONSIGNEEZONES
                                 AND QSD.CHARGE_TYPE = 'Delivery'
                                 AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE
                                 /*AND NVL(QCM.CONSOLE_TYPE, '~') =
                                     NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~')*/-- Commented by Gowtham to show Origin and Destination Charges in PDF View
                                 AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
                            END IF;



                     END IF;

               END LOOP;

         END LOOP;

   END LOOP;

   UPDATE Rates_charges_excel TC
          SET TC.EXT_CHARGE_NAME = ( SELECT distinct QCD.EXT_CHARGE_NAME
                                     FROM QMS_CHARGEDESCMASTER QCD
                                     WHERE QCD.CHARGEDESCID = TC.CHARGEDESCID
                                     and qcd.inactivate='N'
                                     and rownum=1
                                   );
   UPDATE Rates_charges_excel TC SET TC.ORG  = (SELECT LOCATIONNAME FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = TC.ORG),
                              TC.DEST = (SELECT LOCATIONNAME FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = TC.DEST);


  OPEN RS1 FOR SELECT DISTINCT TC.QUOTE_ID,
       TC.CHARGEDESCID,
       TC.TERMINALID,
       TC.CHARGESLAB,
       TC.MARGIN_DISCOUNT_FLAG,
       TC.CURRENCY,
       TO_CHAR(TC.BUYRATE) BUYRATE,
       TO_CHAR(TC.SELLRATE) RSR,
       TC.DENSITY_RATIO,
       TC.ORG,
       TC.DEST,
       TC.CHARGEBASIS,
       TC.Cost_Incurredat,
       TC.LINE_NO,
       TC.ID,
       TO_CHAR(decode(TC.MARGIN_DISCOUNT_FLAG,
              'M',
              decode(TC.Margin_Type,
                     'A',
                     (TC.Buyrate + tc.marginvalue),
                     'P',
                     (TC.Buyrate + decode(tc.marginvalue,0,/*TC.Buyrate*/0,(TC.Buyrate / tc.marginvalue) * 100))),
              'D',
              decode(TC.Margin_Type,
                     'A',
                     (TC.SELLRATE - tc.marginvalue),
                     'P',
                     (TC.SELLRATE - decode(tc.marginvalue,0,/*TC.Buyrate*/0,(TC.Buyrate / tc.marginvalue) * 100))),
              0)) Sellrate,
              RATE_DESCRIPTION,
              -- Added by Kishore Podili
             -- tc.int_charge_name,
              tc.ext_charge_name
  FROM Rates_charges_excel TC
  ORDER BY TC.QUOTE_ID,TC.Id,TC.LINE_NO,TC.CHARGEDESCID,TC.ORG, TC.DEST,TC.CHARGESLAB;





    --  End Of Kishore Podili For QuoteGroupExcel Report with Charges on 03-Feb-11





     commit;  --@@Added by Kameswari for the WPBN issue - on 13/09/2011
    OPEN RS FOR
      SELECT WEIGHTBREAK, SHIPMENT_MODE
        FROM RATES_HEADER_EXCEL RC
       GROUP BY WEIGHTBREAK, SHIPMENT_MODE
       order by shipment_mode, weightbreak;

    EXCEPTION WHEN OTHERS THEN
    rollback;--@@Added by Kameswari for the WPBN issue - on 13/09/2011
    dbms_output.put_line (SQLERRM);
  end qms_ratesreport_excel;
end qms_rates_excel;

/

/

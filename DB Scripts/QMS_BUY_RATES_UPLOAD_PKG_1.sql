--------------------------------------------------------
--  DDL for Package Body QMS_BUY_RATES_UPLOAD_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_BUY_RATES_UPLOAD_PKG" IS

  FUNCTION BUY_RATES_PROC(P_TERMINAL VARCHAR2) RETURN VARCHAR2 IS
    V_HEADER_ERROR VARCHAR2(32767);
    V_MSG          VARCHAR2(32767);
    RECORD_COUNT   NUMBER;
    V_RATIO        VARCHAR2(10);
    V_MODE         VARCHAR2(10);
    V_SHMODE       VARCHAR2(10);
    V_WTBREAK      VARCHAR2(10);
    V_RATETYPE     VARCHAR2(10);
    V_TERMINALID   VARCHAR2(10);
    STR            VARCHAR2(200);
    V_RETURNVALUE  VARCHAR2(10);
    V_BUYRATEID    VARCHAR2(10);
    V_LANENO       VARCHAR2(10);
    V_VERSIONNO    NUMBER(10) := 0;
    V_SEQID        VARCHAR2(10);
    V_COUNT        VARCHAR2(10);
    V_CONSOLETYPE  VARCHAR2(10);
    V_CURRENCY     VARCHAR2(10);
    V_WTCLASS      VARCHAR2(10);
    V_UOM          VARCHAR2(10);
    V_CHANGEDESC   VARCHAR2(2000);
    V_UPDATECOUNT  NUMBER(10);
    V_COUNTQUOTE   NUMBER(10);
    V_COUNTMSG     VARCHAR2(10);
    V_LINENO       VARCHAR2(10);
    V_TIMESTAMP    TIMESTAMP;
    V_TIMESTAMP1   TIMESTAMP;
    V_TIMESTAMP2   TIMESTAMP;
    V_TIMESTAMP3   TIMESTAMP;
    V_TIMESTAMP4   TIMESTAMP;
    CURSOR C1 IS
      SELECT * FROM QMS_STG_BUYRATES_MAIN_HEADER;
    RETURNSTR NUMBER(10);
    SUCCESS   VARCHAR2(10);
  BEGIN
    SELECT SYSTIMESTAMP INTO V_TIMESTAMP FROM DUAL;

    --MAIN HEADER VALIDATION
    FOR R1 IN C1 LOOP
      --SHIPMENT_MODE
      IF R1.SHIPMENT_MODE IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (SHIPMENT  MODE). \N';
      ELSIF (UPPER(R1.SHIPMENT_MODE) <> 'SEA' AND
            UPPER(R1.SHIPMENT_MODE) <> 'AIR' AND
            UPPER(R1.SHIPMENT_MODE) <> 'TRUCK') THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'SHIPMENT MODE  MUST BE  AIR,SEA,OR TRUCK . \N';
      END IF;
      --WEIGHT_BREAK
      IF R1.WEIGHT_BREAK IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (WEIGHT BREAK). \N';
      ELSIF (UPPER(R1.WEIGHT_BREAK) <> 'FLAT' AND
            UPPER(R1.WEIGHT_BREAK) <> 'SLAB' AND
            UPPER(R1.WEIGHT_BREAK) <> 'LIST') THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'WEIGHT BREAK SHOULD BE EITHER FLAT,SLAB OR LIST \N';
      END IF;
      --WEIGHT_CLASS
      IF R1.WEIGHT_CLASS IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (WEIGHT CLASS). \N';
      ELSIF (UPPER(R1.WEIGHT_CLASS) <> 'GENERAL' AND
            UPPER(R1.WEIGHT_CLASS) <> 'WEIGHT SCALE') THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'WEIGHT CLASS SHOULD BE GENERAL OR WEIGHT SCALE . \N';
      END IF;
      -- RATE_TYPE
      IF R1.RATE_TYPE IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (RATE TYPE). \N';
      ELSE
        IF (UPPER(R1.WEIGHT_BREAK) = 'FLAT') THEN
          IF (UPPER(R1.RATE_TYPE) <> 'FLAT') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'RATETYPE SHOULD BE FLAT. \N';
          END IF;
        END IF;
        IF (UPPER(R1.WEIGHT_BREAK) = 'SLAB') THEN
          IF (UPPER(R1.RATE_TYPE) <> 'FLAT' AND
             UPPER(R1.RATE_TYPE) <> 'SLAB' AND
             UPPER(R1.RATE_TYPE) <> 'BOTH') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'RATETYPE SHOULD BE EITHER FLAT,SLAB OR BOTH. \N';
          END IF;
        END IF;
        IF (UPPER(R1.WEIGHT_BREAK) = 'LIST') THEN
          IF (UPPER(R1.SHIPMENT_MODE) = 'AIR') THEN
            IF (UPPER(R1.RATE_TYPE) <> 'PIVOT') THEN
              V_HEADER_ERROR := V_HEADER_ERROR ||
                                'RATETYPE SHOULD BE PIVOT. \N';
            END IF;
          END IF;
          IF (UPPER(R1.SHIPMENT_MODE) = 'SEA' OR
             UPPER(R1.SHIPMENT_MODE) = 'TRUCK') THEN
            IF (UPPER(R1.RATE_TYPE) <> 'FLAT') THEN
              V_HEADER_ERROR := V_HEADER_ERROR ||
                                'RATETYPE SHOULD BE FLAT. \N';
            END IF;
          END IF;
        END IF;
      END IF;
      --UOM
      IF R1.UOM IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (UOM ). \N';
      ELSE
        IF (UPPER(R1.SHIPMENT_MODE) = 'SEA') THEN
          IF (UPPER(R1.UOM) <> 'CBM' AND UPPER(R1.UOM) <> 'CFT') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'UOM SHOULD BE EITHER CBM OR CFT. \N';
          END IF;
        ELSIF (UPPER(R1.SHIPMENT_MODE) = 'AIR') THEN
          IF (UPPER(R1.UOM) <> 'KG' AND UPPER(R1.UOM) <> 'LB') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'UOM SHOULD BE EITHER KG,LB. \N';
          END IF;
        ELSIF (UPPER(R1.SHIPMENT_MODE) = 'TRUCK') THEN
          IF (UPPER(R1.UOM) <> 'KG' AND UPPER(R1.UOM) <> 'LB' AND
             UPPER(R1.UOM) <> 'CBM' AND UPPER(R1.UOM) <> 'CFT') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'UOM SHOULD BE EITHER KG,LB,CBM,CFT. \N';
          END IF;
        END IF;
      END IF;
      --CONSOLETYPE
      IF R1.CONSOLETYPE IS NULL THEN
        IF (UPPER(R1.SHIPMENT_MODE) = 'SEA' OR
           UPPER(R1.SHIPMENT_MODE) = 'TRUCK') THEN
          V_HEADER_ERROR := V_HEADER_ERROR ||
                            'MANDATORY FIELDS ARE NOT PROVIDED (CONSOLETYPE ). \N';
        END IF;
      ELSE
        IF (UPPER(R1.SHIPMENT_MODE) = 'AIR') THEN
          V_HEADER_ERROR := V_HEADER_ERROR ||
                            'THE CONSOLETYPE SHOULD BE EMPTY. \N';
        ELSIF (UPPER(R1.SHIPMENT_MODE) = 'SEA') THEN
          IF (UPPER(R1.CONSOLETYPE) <> 'LCL' AND
             UPPER(R1.CONSOLETYPE) <> 'FCL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE EITHER LCL OR FCL. \N';
          ELSIF (UPPER(R1.WEIGHT_BREAK) = 'LIST' AND
                UPPER(R1.CONSOLETYPE) <> 'FCL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE FCL.\N';
          ELSIF (UPPER(R1.WEIGHT_BREAK) <> 'LIST' AND
                UPPER(R1.CONSOLETYPE) <> 'LCL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE LCL.\N';
          END IF;
        ELSIF (UPPER(R1.SHIPMENT_MODE) = 'TRUCK') THEN
          IF (UPPER(R1.CONSOLETYPE) <> 'FTL' AND
             UPPER(R1.CONSOLETYPE) <> 'LTL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE EITHER LTL OR FTL. \N';
          ELSIF (UPPER(R1.WEIGHT_BREAK) = 'LIST' AND
                UPPER(R1.CONSOLETYPE) <> 'FTL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE FTL.\N';
          ELSIF (UPPER(R1.WEIGHT_BREAK) <> 'LIST' AND
                UPPER(R1.CONSOLETYPE) <> 'LTL') THEN
            V_HEADER_ERROR := V_HEADER_ERROR ||
                              'THE CONSOLETYPE SHOULD BE LTL.\N';
          END IF;
        END IF;
      END IF;
      --CURRENCY
      IF R1.CURRENCY IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (CURRENCY ID). \N';
      ELSE
        SELECT COUNT(1)
          INTO RECORD_COUNT
          FROM FS_COUNTRYMASTER
         WHERE CURRENCYID = R1.CURRENCY;
        IF (RECORD_COUNT = 0) THEN
          V_HEADER_ERROR := V_HEADER_ERROR || 'INVALID CURRENCY ID. \N';
        END IF;
      END IF;
      --DENSITY_RATIO
      IF R1.DENSITY_RATIO IS NULL THEN
        V_HEADER_ERROR := V_HEADER_ERROR ||
                          'MANDATORY FIELDS ARE NOT PROVIDED (DENSITY RATIO). \N';
      ELSE
        SELECT COUNT(1)
          INTO RECORD_COUNT
          FROM QMS_DENSITY_GROUP_CODE
         WHERE INVALIDATE = 'F'
           AND DGCCODE =
               DECODE(R1.SHIPMENT_MODE, 'SEA', '2', 'AIR', '1', '4')
           AND DECODE(R1.UOM,
                      'KG',
                      KG_PER_M3,
                      'CBM',
                      KG_PER_M3,
                      'LB',
                      LB_PER_F3,
                      'CFT',
                      LB_PER_F3) = R1.DENSITY_RATIO;
        IF (RECORD_COUNT = 0) THEN
          V_HEADER_ERROR := V_HEADER_ERROR || 'INVALID DENSITY RATIO. \N';
        END IF;
      END IF;
      IF V_HEADER_ERROR IS NOT NULL THEN
        UPDATE QMS_STG_BUYRATES_MAIN_HEADER SET ERROR_MSG = V_HEADER_ERROR;
        COMMIT;
      END IF;
      VALIDATE_DETAILS(P_TERMINAL, R1.WEIGHT_BREAK, R1.SHIPMENT_MODE);
    END LOOP;
    --END  MAIN HEADER VALIDATION
    /* IF V_HEADER_ERROR IS NULL
    THEN */
    V_MSG := VALIDATEDETAILS;
    /*END IF;*/
    SELECT COUNT(ERROR_MSG)
      INTO V_COUNTMSG
      FROM QMS_STG_BUYRATES_DETAILS
     WHERE ERROR_MSG IS NOT NULL;

    IF V_HEADER_ERROR IS NULL AND V_COUNTMSG = 0 THEN
      SUCCESS   := INSERTDETAILS;
      RETURNSTR := SUCCESS;

    ELSE
      RETURNSTR := 5;
    END IF;

    RETURN RETURNSTR;
  END BUY_RATES_PROC;

  --TO VALIDATE DETAILS
  PROCEDURE VALIDATE_DETAILS(P_TERMINAL      VARCHAR2,
                             P_WEIGHT_BREAK  VARCHAR2,
                             P_SHIPMENT_MODE VARCHAR2) IS
    V_DETAILS_ERROR VARCHAR2(32767);
    NUM             NUMBER;
    CURSOR C2 IS
      SELECT DISTINCT LINE_NO, CONTAINER_NO
        FROM QMS_STG_BUYRATES_DETAILS_DATA;
    --ORDER  BY LINE_NO ASC; --COMMENTED BY SREENADH FOR 169637
  BEGIN
    FOR R2 IN C2 LOOP
      IF (R2.LINE_NO = 0) THEN
        IF (UPPER(P_WEIGHT_BREAK) = 'FLAT' OR
           UPPER(P_WEIGHT_BREAK) = 'SLAB') THEN
          IF (UPPER(R2.CONTAINER_NO) <> 'MIN') THEN
            V_DETAILS_ERROR := V_DETAILS_ERROR ||
                               'INVALID WEIGHT BREAK HEADER(MIN)\N';
          END IF;
        ELSIF (UPPER(P_WEIGHT_BREAK) = 'LIST' OR
              UPPER(P_SHIPMENT_MODE) = 'AIR') THEN
          IF (UPPER(R2.CONTAINER_NO) <> 'OVER-PIVOT RATE') THEN
            V_DETAILS_ERROR := V_DETAILS_ERROR ||
                               'INVALID WEIGHT BREAK HEADER(OVER-PIVOT RATE)\N';
            /*
            ELSE
            NOTE :- STORE AS  ' OVERPIVOT ' IN MAIN TABLE
            */
          END IF;
        END IF;
      ELSIF (R2.LINE_NO = 1) THEN
        IF (UPPER(P_WEIGHT_BREAK) = 'FLAT') THEN
          IF (UPPER(R2.CONTAINER_NO) <> 'FLAT') THEN
            V_DETAILS_ERROR := V_DETAILS_ERROR ||
                               'INVALID WEIGHT BREAK HEADER(FLAT)\N';
          END IF;
        ELSIF (UPPER(P_WEIGHT_BREAK) = 'SLAB') THEN
          BEGIN
            NUM := TO_NUMBER(R2.CONTAINER_NO);
            IF (NUM > 0) THEN
              V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                 'INVALID WEIGHT BREAK VALUES(MIN,-X,X)\N';
            END IF;
          EXCEPTION
            WHEN OTHERS THEN
              V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                 'WEIGHT BREAK VALUE NOT A NUMBER\N';
              RETURN; --NOTE :- PLS CHECK THIS
          END;
        END IF;
      ELSIF (R2.LINE_NO = 2) THEN
        IF (UPPER(P_WEIGHT_BREAK) = 'SLAB') THEN
          BEGIN
            IF (NUM <> ABS(TO_NUMBER(R2.CONTAINER_NO))) THEN
              V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                 'INVALID WEIGHT BREAK VALUES(MIN,-X,X)\N';
            END IF;
          EXCEPTION
            WHEN OTHERS THEN
              V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                 'WEIGHT BREAK VALUE NOT A NUMBER\N';
              RETURN; --NOTE :- PLS CHECK THIS
          END;
        END IF;
        /* NUM := TO_NUMBER(R2.CONTAINER_NO);*/
      ELSE
        IF (INSTR(R2.CONTAINER_NO, 'FS') = 1 OR
           INSTR(R2.CONTAINER_NO, 'SS') = 1 OR
           INSTR(R2.CONTAINER_NO, 'CAF') = 1 OR
           INSTR(R2.CONTAINER_NO, 'BAF') = 1 OR
           INSTR(R2.CONTAINER_NO, 'PSS') = 1 OR
           INSTR(R2.CONTAINER_NO, 'CSF') = 1) THEN
          IF (UPPER(R2.CONTAINER_NO) <> 'FSKG' OR
             UPPER(R2.CONTAINER_NO) <> 'FSBASIC' OR
             UPPER(R2.CONTAINER_NO) <> 'FSMIN' OR
             UPPER(R2.CONTAINER_NO) <> 'SSKG' OR
             UPPER(R2.CONTAINER_NO) <> 'SSBASIC' OR
             UPPER(R2.CONTAINER_NO) <> 'SSMIN' OR
             UPPER(R2.CONTAINER_NO) <> 'CAF%' OR
             UPPER(R2.CONTAINER_NO) <> 'CAFMIN' OR
             UPPER(R2.CONTAINER_NO) <> 'BAFMIN' OR
             UPPER(R2.CONTAINER_NO) <> 'BAFM3' OR
             UPPER(R2.CONTAINER_NO) <> 'PSSMIN' OR
             UPPER(R2.CONTAINER_NO) <> 'PSSM3' OR
             UPPER(R2.CONTAINER_NO) <> 'CSF') THEN
            V_DETAILS_ERROR := V_DETAILS_ERROR || 'INVALID WEIGHT BREAK :' ||
                               R2.CONTAINER_NO;
          END IF;
        ELSE
          IF (UPPER(P_WEIGHT_BREAK) = 'SLAB') THEN
            BEGIN
              IF (NUM < TO_NUMBER(R2.CONTAINER_NO)) THEN
                V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                   'INVALID WEIGHT BREAKS VALUES(MIN,-X,X,Y,Z(Y<Z))\N';
              END IF;
            EXCEPTION
              WHEN OTHERS THEN
                V_DETAILS_ERROR := V_DETAILS_ERROR ||
                                   'WEIGHT BREAK VALUE NOT A NUMBER\N';
                RETURN; --NOTE :- PLS CHECK THIS
            END;
          END IF;
        END IF;
      END IF;
    END LOOP;
  END VALIDATE_DETAILS;

  FUNCTION VALIDATEDETAILS RETURN VARCHAR2 IS
    V_ERRORMSG     VARCHAR2(32767) := '';
    V_MSG          VARCHAR2(32767) := '';
    V_INTEGER      NUMBER(20);
    V_INTEGER1     NUMBER(20);
    V_NUMBER       NUMBER(5);
    V_TRANSIT      NUMBER(5);
    V_ORIGIN       VARCHAR2(10);
    V_DESTINATION  VARCHAR2(10);
    V_CARRIER      VARCHAR2(10);
    V_SERVICELEVEL VARCHAR2(10);
    V_MODE         VARCHAR2(10);
    K              VARCHAR2(100);
    CURSOR C1 IS
      SELECT ROW_ID,
             ORIGIN,
             DESTINATION,
             CARRIER_ID,
             SERVICELEVEL,
             FREQUENCY,
             TRANSIT,
             EFFECTIVE_FROM,
             VALID_UPTO
        FROM QMS_STG_BUYRATES_DETAILS;
    TYPE ROWIDTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.ROW_ID%TYPE;
    TYPE ORIGINTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.ORIGIN%TYPE;
    TYPE DESTINATIONTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.DESTINATION%TYPE;
    TYPE CARRIER_IDTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.CARRIER_ID%TYPE;
    TYPE SERVICELEVELTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.SERVICELEVEL%TYPE;
    TYPE FREQUENCYTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.FREQUENCY%TYPE;
    TYPE TRANSITTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.TRANSIT%TYPE;
    TYPE EFFECTIVEFROMTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.EFFECTIVE_FROM%TYPE;
    TYPE VALIDUPTOTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.VALID_UPTO%TYPE;
    ROWID          ROWIDTAB;
    ORIGIN         ORIGINTAB;
    DESTINATION    DESTINATIONTAB;
    CARRIER_ID     CARRIER_IDTAB;
    SERVICELEVEL   SERVICELEVELTAB;
    FREQUENCY      FREQUENCYTAB;
    TRANSIT        TRANSITTAB;
    EFFECTIVE_FROM EFFECTIVEFROMTAB;
    VALID_UPTO     VALIDUPTOTAB;
  BEGIN
    OPEN C1;
    FETCH C1 BULK COLLECT
      INTO ROWID, ORIGIN, DESTINATION, CARRIER_ID, SERVICELEVEL, FREQUENCY, TRANSIT, EFFECTIVE_FROM, VALID_UPTO;
    CLOSE C1;
    SELECT SHIPMENT_MODE INTO V_MODE FROM QMS_STG_BUYRATES_MAIN_HEADER; --CAN BE PASSED AS A PARAMETER FROM BUY RATES PROC
    IF V_MODE = 'AIR' OR V_MODE = 'TRUCK' THEN
      IF ROWID.COUNT() > 0 THEN
        FOR I IN ROWID.FIRST .. ROWID.LAST LOOP
          K          := 1;
          V_ERRORMSG := '';
          SELECT COUNT(LOCATIONID)
            INTO V_ORIGIN
            FROM FS_FR_LOCATIONMASTER
           WHERE LOCATIONID = ORIGIN(I);
          SELECT COUNT(LOCATIONID)
            INTO V_DESTINATION
            FROM FS_FR_LOCATIONMASTER
           WHERE LOCATIONID = DESTINATION(I);
          SELECT COUNT(CARRIERID)
            INTO V_CARRIER
            FROM FS_FR_CAMASTER
           WHERE CARRIERID = CARRIER_ID(I);
          SELECT COUNT(SERVICELEVELID)
            INTO V_SERVICELEVEL
            FROM FS_FR_SERVICELEVELMASTER
           WHERE SERVICELEVELID = SERVICELEVEL(I);
          IF V_ORIGIN = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'ORIGIN IS INVALID ,';
          END IF;
          IF V_DESTINATION = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'DESTINATION IS INVALID ,';
          END IF;
          IF V_CARRIER = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'CARRIER IS INVALID ,';
          END IF;
          IF V_SERVICELEVEL = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'SERVICE LEVEL IS INVALID ,';
          END IF;
          IF LENGTH(TRANSIT(I)) = 0 THEN
            V_ERRORMSG := V_ERRORMSG ||
                          ' MANDATORY FIELDS ARE NOT PROVIDED (TRANSITTIME ).';
          ELSIF LENGTH(TRANSIT(I)) > 6 THEN
            V_ERRORMSG := V_ERRORMSG ||
                          ' MAX LENGTH MUST BE  6 (TRANSITTIME )';
          ELSIF LENGTH(TRANSIT(I)) = 4 AND
                INSTR(TRANSIT(I), ':', 1, 1) <= 0 THEN
            V_INTEGER := SUBSTR(TRANSIT(I), 1, 2);
            IF V_INTEGER < 0 AND V_INTEGER > 99 THEN
              V_ERRORMSG := V_ERRORMSG ||
                            'THE TRANSIT TIME FORMAT SHOULD BE HH:MM. ';
            END IF;
            V_INTEGER1 := SUBSTR(TRANSIT(I), 3);
            IF V_INTEGER < 0 AND V_INTEGER > 60 THEN
              V_ERRORMSG := V_ERRORMSG ||
                            'THE TRANSIT TIME FORMAT SHOULD BE HH:MM. ';
            END IF;
          ELSIF LENGTH(TRANSIT(I)) = 5 AND INSTR(TRANSIT(I), ':', 1, 1) > 0 THEN
            V_INTEGER := SUBSTR(TRANSIT(I), 1, 2);
            IF V_INTEGER < 0 AND V_INTEGER > 99 THEN
              V_ERRORMSG := V_ERRORMSG ||
                            'THE TRANSIT TIME FORMAT SHOULD BE HH:MM. ';
            END IF;
            V_INTEGER1 := SUBSTR(TRANSIT(I), 4);
            IF V_INTEGER < 0 AND V_INTEGER > 60 THEN
              V_ERRORMSG := V_ERRORMSG ||
                            'THE TRANSIT TIME FORMAT SHOULD BE HH:MM. ';
            END IF;
          END IF;
          IF LENGTH(FREQUENCY(I)) = 0 THEN
            V_ERRORMSG := V_ERRORMSG ||
                          ' MANDATORY FIELDS ARE NOT PROVIDED (FREQUENCY ).';
          ELSE
            WHILE K < LENGTH(FREQUENCY(I)) LOOP
              V_NUMBER := SUBSTR(FREQUENCY(I), K, 1);
              IF V_NUMBER < 0 OR V_NUMBER > 7 THEN
                V_ERRORMSG := V_ERRORMSG ||
                              'FREQUENCY SHOULD BE EITHER 1 OR 7.';
              END IF;
              K := K + 2;
            END LOOP;
          END IF;
          IF TO_TIMESTAMP(EFFECTIVE_FROM(I)) < SYSDATE THEN
            V_ERRORMSG := V_ERRORMSG ||
                          'EFFECTIVE DATE SHOUD BE GREATER THAN OR EQUAL TO CURRENT DATE';
          END IF;
          IF TO_TIMESTAMP(VALID_UPTO(I)) < SYSDATE OR
             TO_TIMESTAMP(VALID_UPTO(I)) < TO_TIMESTAMP(EFFECTIVE_FROM(I)) THEN
            V_ERRORMSG := V_ERRORMSG ||
                          'VALIDUPTO DATE SHOUD BE GREATER THAN OR EQUAL TO EFFECTIVE DATE';
          END IF;
          UPDATE QMS_STG_BUYRATES_DETAILS
             SET ERROR_MSG = V_ERRORMSG
           WHERE ROW_ID = ROWID(I); --INDEX CAN BE CREATED
        END LOOP;
      END IF;
    ELSE
      IF ROWID.COUNT() > 0 THEN
        FOR I IN ROWID.FIRST .. ROWID.LAST LOOP
          V_ERRORMSG := '';
          SELECT COUNT(PORTID)
            INTO V_ORIGIN
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = ORIGIN(I);
          SELECT COUNT(PORTID)
            INTO V_DESTINATION
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = DESTINATION(I);
          SELECT COUNT(CARRIERID)
            INTO V_CARRIER
            FROM FS_FR_CAMASTER
           WHERE CARRIERID = CARRIER_ID(I);
          SELECT COUNT(SERVICELEVELID)
            INTO V_SERVICELEVEL
            FROM FS_FR_SERVICELEVELMASTER
           WHERE SERVICELEVELID = SERVICELEVEL(I);
          IF V_ORIGIN = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'ORIGIN IS INVALID ,';
          END IF;
          IF V_DESTINATION = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'DESTINATION IS INVALID ,';
          END IF;
          IF V_CARRIER = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'CARRIER IS INVALID ,';
          END IF;
          IF V_SERVICELEVEL = 0 THEN
            V_ERRORMSG := V_ERRORMSG || 'SERVICE LEVEL IS INVALID ,';
          END IF;
          IF LENGTH(TRANSIT(I)) = 0 THEN
            V_ERRORMSG := V_ERRORMSG ||
                          ' MANDATORY FIELDS ARE NOT PROVIDED (TRANSITTIME ).';
          ELSE
            BEGIN
              V_TRANSIT := TO_NUMBER(TRANSIT(I));
            EXCEPTION
              WHEN OTHERS THEN
                V_ERRORMSG := V_ERRORMSG ||
                              ' THE TRANSIT TIME FOR SEA MUST BE IN DAYS';
            END;
          END IF;
          IF LENGTH(FREQUENCY(I)) = 0 THEN
            V_ERRORMSG := V_ERRORMSG ||
                          ' MANDATORY FIELDS ARE NOT PROVIDED (FREQUENCY ).';
          END IF;
          IF FREQUENCY(I) <> 'WEEKLY' AND FREQUENCY(I) <> 'FORTNIGHTLY' AND
             FREQUENCY(I) <> 'MONTHLY' AND FREQUENCY(I) <> 'EVERY 10 DAYS' THEN
            V_ERRORMSG := V_ERRORMSG ||
                          'FREQUENCY SHOULD BE EITHER WEEKLY, FORTNIGHTLY OR MONTHLY.';
          END IF;
          IF TO_DATE(EFFECTIVE_FROM(I)) < TO_DATE(SYSDATE) THEN
            V_ERRORMSG := V_ERRORMSG ||
                          'EFFECTIVE DATE SHOUD BE GREATER THAN OR EQUAL TO CURRENT DATE';
          END IF;
          IF TO_DATE(VALID_UPTO(I)) < TO_DATE(SYSDATE) OR
             TO_DATE(VALID_UPTO(I)) < TO_DATE(EFFECTIVE_FROM(I)) THEN
            V_ERRORMSG := V_ERRORMSG ||
                          'VALIDUPTO DATE SHOUD BE GREATER THAN OR EQUAL TO EFFECTIVE DATE';
          END IF;
          UPDATE QMS_STG_BUYRATES_DETAILS
             SET ERROR_MSG = V_ERRORMSG
           WHERE ROW_ID = ROWID(I);
        END LOOP;
      END IF;
    END IF;
    RETURN V_MSG;
  END VALIDATEDETAILS;

  /*PROCEDURE RATES_DELETION(P_RS OUT RESULTSET) IS
    V_QUOTEID    NUMBER(10);
    V_BUYRATEID  NUMBER(10);
    V_LANENO     NUMBER(10);
    V_VERSIONNO  NUMBER(10);
    V_WTBRKSLAB  VARCHAR2(10);
    V_CHARGERATE NUMBER(10);

  BEGIN
    FOR I IN (SELECT ORIGIN,
                     DESTINATION,
                     CARRIER_ID,
                     SERVICELEVEL,
                     FREQUENCY,
                     TRANSIT

                FROM QMS_STG_BUYRATES_DETAILS) LOOP

      BEGIN
        SELECT  QR.BUYRATE_ID,
               QR.RATE_LANE_NO,
               QR.VERSION_NO,
               QBD.WEIGHT_BREAK_SLAB,
               QBD.CHARGERATE
          INTO V_BUYRATEID,
               V_LANENO,
               V_VERSIONNO,
               V_WTBRKSLAB,
               V_CHARGERATE
          FROM QMS_QUOTE_RATES QR, QMS_BUYRATES_DTL QBD
         WHERE QR.BUYRATE_ID = QBD.BUYRATEID
           AND QR.RATE_LANE_NO = QBD.LANE_NO
           AND QR.VERSION_NO = QBD.VERSION_NO
           AND QR.SELL_BUY_FLAG IN ('BR', 'RSR')
           AND QBD.ORIGIN = I.ORIGIN
           AND QBD.DESTINATION = I.DESTINATION
           AND QBD.CARRIER_ID = I.CARRIER_ID
           AND QBD.SERVICE_LEVEL = I.SERVICELEVEL
           AND QBD.FREQUENCY = I.FREQUENCY
           AND QBD.TRANSIT_TIME = I.TRANSIT
           AND TO_DATE(QBD.EFFECTIVE_FROM, 'DD/MM/YYYY') =
               TO_DATE(I.EFFECTIVE_FROM, 'DD/MM/YYYY')
           AND QR.LINE_NO = '0'
           AND ROWNUM = '1';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_BUYRATEID := 0;
      END;
      IF V_BUYRATEID = 0 THEN
        INSERT INTO TEMP_TABLE_1_FCL
          (BUYRATEID,
           LANE_NO,
           VERSION_NO,
           ORIGIN,
           DESTINATION,
           CARRIER_ID,
           SERVICELEVEL,
           FREQUENCY,
           TRANSIT_TIME,
           WEIGHT_BREAK_SLAB,
           CHARGERATE)
          SELECT  QBD.BUYRATEID,
                 QBD.LANE_NO,
                 QBD.VERSION_NO,
                 QBD.ORIGIN,
                 QBD.DESTINATION,
                 QBD.CARRIER_ID,
                 QBD.SERVICE_LEVEL,
                 QBD.FREQUENCY,
                 QBD.TRANSIT_TIME,
                 QBD.WEIGHT_BREAK_SLAB,
                 QBD.CHARGERATE
            FROM QMS_BUYRATES_DTL QBD
           WHERE QBD.ORIGIN = I.ORIGIN
             AND QBD.DESTINATION = I.DESTINATION
             AND QBD.CARRIER_ID = I.CARRIER_ID
             AND QBD.SERVICE_LEVEL = I.SERVICELEVEL
             AND QBD.FREQUENCY = I.FREQUENCY
             AND QBD.TRANSIT_TIME = I.TRANSIT
             AND TO_DATE(QBD.EFFECTIVE_FROM, 'DD/MM/YYYY') =
                 TO_DATE(I.EFFECTIVE_FROM, 'DD/MM/YYYY')
             AND QBD.LINE_NO = '0';
       AND ROWNUM =1

      ELSE
        INSERT INTO TEMP_TABLE_FCL
          (QUOTE_ID,
           BUYRATE_ID,
           RATE_LANE_NO,
           VERSION_NO,
           ORIGIN,
           DESTINATION,
           CARRIER_ID,
           SERVICELEVEL,
           FREQUENCY,
           TRANSIT_TIME,
           WEIGHT_BREAK_SLAB,
           CHARGERATE)
        VALUES
          (V_QUOTEID,
           V_BUYRATEID,
           V_LANENO,
           V_VERSIONNO,
           I.ORIGIN,
           I.DESTINATION,
           I.CARRIER_ID,
           I.SERVICELEVEL,
           I.FREQUENCY,
           I.TRANSIT,
           V_WTBRKSLAB,
           V_CHARGERATE);
      END IF;
    END LOOP;
    OPEN P_RS FOR
      SELECT *
        FROM TEMP_TABLE
       ORDER BY BUYRATEID, LANE_NO, VERSION_NO;
    OPEN P_RS1 FOR
      SELECT * FROM TEMP_TABLE_1 ORDER BY BUYRATEID, LANE_NO, VERSION_NO;

  END RATES_DELETION;*/
  /*PROCEDURE RSR_INSERTIONS(P_RS OUT RESULTSET, P_RS1 OUT RESULTSET) IS
    V_QUOTEID      NUMBER(10);
    V_BUYRATEID    NUMBER(10);
    V_LANENO       NUMBER(10);
    V_WTBRKSLAB    VARCHAR2(10);
    V_CHARGERATE   NUMBER(10);
    V_ORIGIN       VARCHAR2(10);
    V_DESTINATION  VARCHAR2(10);
    V_CARRIERID    VARCHAR2(10);
    V_SERVICELEVEL VARCHAR2(10);
    V_FREQUENCY    VARCHAR2(20);
    V_TRANSITTIME  VARCHAR2(20);
    V_LBOUND       VARCHAR2(20);
    V_UBOUND       VARCHAR2(20);
    V_LINENO       VARCHAR2(20);
    V_VERSIONNO    VARCHAR2(20);
  BEGIN
    FOR I IN (SELECT ORIGIN,
                     DESTINATION,
                     CARRIER_ID,
                     SERVICELEVEL,
                     FREQUENCY,
                     TRANSIT,
                     EFFECTIVE_FROM
                FROM QMS_STG_BUYRATES_DETAILS) LOOP

      --  SELECT  QBD.BUYRATEID,QBD.LANE_NO,QBD.VERSION_NO,QBD.WEIGHT_BREAK_SLAB,QBD.CHARGERATE  INTO V_BUYRATEID,V_LANENO,V_VERSIONNO,V_WTBRKSLAB,V_CHARGERATE FROM QMS_BUYRATES_DTL QBD WHERE AND QR.RATE_LANE_NO=QBD.LANE_NO AND QR.VERSION_NO=QBD.VERSION_NO AND QR.SELL_BUY_FLAG IN ('BR','RSR') AND QBD.ORIGIN=I.ORIGIN AND QBD.DESTINATION=I.DESTINATION AND QBD.CARRIER_ID=I.CARRIER_ID AND QBD.SERVICE_LEVEL=I.SERVICELEVEL AND QBD.FREQUENCY=I.FREQUENCY AND QBD.TRANSIT_TIME=I.TRANSIT AND TO_DATE(QBD.EFFECTIVE_FROM)=I.EFFECTIVE_FROM  AND QR.LINE_NO='0' AND ROWNUM='1';

      FOR J IN (SELECT BD.BUYRATEID,
                       BD.LANE_NO,
                       BD.VERSION_NO,
                       BD.ORIGIN,
                       BD.DESTINATION,
                       BD.CARRIER_ID,
                       BD.SERVICE_LEVEL,
                       BD.FREQUENCY,
                       BD.TRANSIT_TIME,
                       BD.WEIGHT_BREAK_SLAB,
                       BD.CHARGERATE CHG,
                       BD.CHARGERATE CHG1,
                       BD.LINE_NO,
                       BD.LOWERBOUND,
                       BD.UPPERBOUND,
                       BD.RATE_DESCRIPTION,
                       BD.INVALIDATE,
                       BD.DENSITY_CODE,
                   SUBSTR(BD.ORIGIN,0,2) ORIGINCOUNTRY,
                   SUBSTR(BD.DESTINATION,0,2) DESTCOUNTRY
            --INTO V_BUYRATEID,V_LANENO,V_VERSIONNO,V_ORIGIN,V_DESTINATION,V_CARRIERID,V_SERVICELEVEL,V_FREQUENCY,V_TRANSITTIME,V_CHARGERATE,V_WTBRKSLAB,V_LINENO,V_LBOUND,V_UBOUND
                  FROM QMS_BUYRATES_DTL BD
                 WHERE BD.ORIGIN = I.ORIGIN
                   AND BD.DESTINATION = I.DESTINATION
                   AND BD.CARRIER_ID = I.CARRIER_ID
                   AND BD.SERVICE_LEVEL IN (I.SERVICELEVEL, 'SCH')
                      \*  AND BD.FREQUENCY=I.FREQUENCY
                                     AND BD.TRANSIT_TIME=I.TRANSIT*\
                      \*              AND TO_DATE(BD.EFFECTIVE_FROM, 'DD/MM/YYYY') =
                                       TO_DATE(I.EFFECTIVE_FROM, 'DD/MM/YYYY')*\
                   AND BD.ACTIVEINACTIVE IS NULL
                   AND (BD.INVALIDATE = 'F' OR BD.INVALIDATE IS NULL)) LOOP
        INSERT INTO TEMP_TABLE_2
        VALUES
          (J.BUYRATEID,
           J.LANE_NO,
           J.VERSION_NO,
           J.ORIGIN,
           J.DESTINATION,
           J.CARRIER_ID,
           J.SERVICE_LEVEL,
           J.FREQUENCY,
           J.TRANSIT_TIME,
           J.WEIGHT_BREAK_SLAB,
           J.CHG,
            J.LINE_NO,
           J.LOWERBOUND,
           J.UPPERBOUND,
           J.RATE_DESCRIPTION,
           J.INVALIDATE,
           J.DENSITY_CODE,
           '0.0',
           'A',
           J.ORIGINCOUNTRY,
           J.DESTCOUNTRY,
           'FCL',
           J.CHG1);
      END LOOP;
    END LOOP;
  END RSR_INSERTIONS;
  PROCEDURE RATES_UPDATED IS
  V_COUNT NUMBER(10);
  BEGIN
  FOR I IN (SELECT TFL.BUYRATE_ID,TFL.RATE_LANE_NO,TFL.VERSION_NO  FROM TEMP_TABLE_FCL TFL)
  LOOP
      SELECT COUNT(QUOTE_ID) INTO V_COUNT FROM QMS_QUOTES_UPDATED WHERE NEW_BUYCHARGE_ID=I.BUYRATE_ID AND NEW_LANE_NO=I.RATE_LANE_NO AND NEW_VERSION_NO=I.VERSION_NO AND CONFIRM_FLAG IS NULL;
      IF V_COUNT>0
      THEN
      END IF;
  END LOOP;
  END;*/
  FUNCTION INSERTDETAILS RETURN VARCHAR2 IS
    V_HEADER_ERROR VARCHAR2(32767);
    V_MSG          VARCHAR2(32767);
    RECORD_COUNT   NUMBER;
    V_RATIO        VARCHAR2(10);
    V_MODE         VARCHAR2(10);
    V_SHMODE       VARCHAR2(10);
    V_WTBREAK      VARCHAR2(10);
    V_RATETYPE     VARCHAR2(10);
    V_TERMINALID   VARCHAR2(10);
    STR            VARCHAR2(200);
    V_RETURNVALUE  VARCHAR2(10);
    V_BUYRATEID    VARCHAR2(10);
    V_LANENO       VARCHAR2(10);
    V_VERSIONNO    NUMBER(10) := 0;
    V_SEQID        VARCHAR2(10);
    V_COUNT        VARCHAR2(10);
    V_CONSOLETYPE  VARCHAR2(10);
    V_CURRENCY     VARCHAR2(10);
    V_WTCLASS      VARCHAR2(10);
    V_UOM          VARCHAR2(10);
    V_CHANGEDESC   VARCHAR2(2000);
    V_CHANGEDESC1  VARCHAR2(2000);
    V_UPDATECOUNT  NUMBER(10);
    V_COUNTQUOTE   NUMBER(10);
    V_COUNTMSG     VARCHAR2(10);
    V_LINENO       VARCHAR2(10);
    RETURNSTR      VARCHAR2(10);
    V_TIMESTAMP10  TIMESTAMP;
    V_TIMESTAMP11  TIMESTAMP;
    V_TIMESTAMP9   TIMESTAMP;
    V_TIMESTAMP8   TIMESTAMP;
    V_TIMESTAMP7   TIMESTAMP;
    V_TIMESTAMP6   TIMESTAMP;
    V_TIMESTAMP5   TIMESTAMP;
    V_TIMESTAMP4   TIMESTAMP;
    V_TIMESTAMP3   TIMESTAMP;
    V_TIMESTAMP2   TIMESTAMP;
    V_TIMESTAMP1   TIMESTAMP;
    CURSOR C1 IS
      SELECT QSD.ROW_ID,
             QSD.ORIGIN,
             QSD.DESTINATION,
             QSD.CARRIER_ID,
             QSD.SERVICELEVEL,
             QSD.FREQUENCY,
             QSD.TRANSIT,
             QSD.VALID_UPTO,
             QSD.EFFECTIVE_FROM,
             QSD.NOTES
        FROM QMS_STG_BUYRATES_DETAILS QSD;
    TYPE ROWIDTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.ROW_ID%TYPE;
    TYPE ORIGINTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.ORIGIN%TYPE;
    TYPE DESTINATIONTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.DESTINATION%TYPE;
    TYPE CARRIER_IDTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.CARRIER_ID%TYPE;
    TYPE SERVICELEVELTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.SERVICELEVEL%TYPE;
    TYPE FREQUENCYTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.FREQUENCY%TYPE;
    TYPE TRANSITTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.TRANSIT%TYPE;
    TYPE VALIDUPTOTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.VALID_UPTO%TYPE;
    TYPE EFFECTIVEFROMTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.EFFECTIVE_FROM%TYPE;
    TYPE NOTESTAB IS TABLE OF QMS_STG_BUYRATES_DETAILS.NOTES%TYPE;
    ROWID          ROWIDTAB;
    ORIGIN         ORIGINTAB;
    DESTINATION    DESTINATIONTAB;
    CARRIER_ID     CARRIER_IDTAB;
    SERVICELEVEL   SERVICELEVELTAB;
    FREQUENCY      FREQUENCYTAB;
    TRANSIT        TRANSITTAB;
    EFFECTIVE_FROM EFFECTIVEFROMTAB;
    VALID_UPTO     VALIDUPTOTAB;
    NOTES          NOTESTAB;
  BEGIN
    UPDATE QMS_STG_BUYRATES_DETAILS
       SET TRANSIT = SUBSTR(TRANSIT, 0, LENGTH(TRANSIT) - 2)
     WHERE INSTR(TRANSIT, ':', 1, 2) <> 0;
      UPDATE QMS_STG_BUYRATES_DETAILS
       SET NOTES='' WHERE UPPER(NOTES)='NA';
    OPEN C1;
    FETCH C1 BULK COLLECT
      INTO ROWID, ORIGIN, DESTINATION, CARRIER_ID, SERVICELEVEL, FREQUENCY, TRANSIT, VALID_UPTO, EFFECTIVE_FROM, NOTES;
    CLOSE C1;
    SELECT QSM.TERMINALID,
           QSM.SHIPMENT_MODE,
           QSM.WEIGHT_BREAK,
           QSM.RATE_TYPE,
           QSM.DENSITY_RATIO,
           QSM.CONSOLETYPE,
           QSM.CURRENCY,
           QSM.UOM,
           QSM.WEIGHT_CLASS
      INTO V_TERMINALID,
           V_MODE,
           V_WTBREAK,
           V_RATETYPE,
           V_RATIO,
           V_CONSOLETYPE,
           V_CURRENCY,
           V_UOM,
           V_WTCLASS
      FROM QMS_STG_BUYRATES_MAIN_HEADER QSM; --CAN BE CALLED FROM BUY RATES PROC
    IF V_MODE = 'AIR' THEN
      V_SHMODE := '1';
    ELSIF V_MODE = 'SEA' THEN
      V_SHMODE := '2';
    ELSE
      V_SHMODE := '4';
    END IF;
    IF ROWID.COUNT() > 0 THEN
      FOR K IN ROWID.FIRST .. ROWID.LAST LOOP
        V_BUYRATEID := NULL;
        V_LANENO    := NULL;
        V_VERSIONNO := 0;

        STR := PKG_QMS_BUYRATES.VALIDATE_UPD_INSERT_BUYRATE(V_SHMODE,
                                                            ORIGIN(K),
                                                            DESTINATION(K),
                                                            SERVICELEVEL(K),
                                                            CARRIER_ID(K),
                                                            EFFECTIVE_FROM(K),
                                                            VALID_UPTO(K),
                                                            V_WTBREAK,
                                                            V_RATETYPE,
                                                            V_TERMINALID,
                                                            FREQUENCY(K),
                                                            V_RATIO,
                                                            NULL,
                                                            NULL,
                                                            TRANSIT(K),
                                                            'UPLOAD',
                                                            V_CURRENCY);

        V_RETURNVALUE := SUBSTR(STR, 1, 1);
        V_BUYRATEID   := SUBSTR(STR,
                                INSTR(STR, ',', 1, 1) + 1,
                                INSTR(STR, ',', 1, 2) -
                                INSTR(STR, ',', 1, 1) - 1);
        V_LANENO      := SUBSTR(STR,
                                INSTR(STR, ',', 1, 2) + 1,
                                LENGTH(STR) - INSTR(STR, ',', 1, 2));
        SELECT BUYRATE_SEQ.NEXTVAL INTO V_SEQID FROM DUAL;
        V_UPDATECOUNT := 0;
        SELECT SYSTIMESTAMP INTO V_TIMESTAMP2 FROM DUAL;
        IF LENGTH(V_BUYRATEID) > 0 THEN
          SELECT MAX(VERSION_NO)
            INTO V_VERSIONNO
            FROM QMS_BUYRATES_DTL
           WHERE BUYRATEID = V_BUYRATEID
             AND LANE_NO = V_LANENO;
        END IF;
        V_LINENO := 0;
        INSERT INTO QMS_BUYRATES_MASTER
          (BUYRATEID,
           SHIPMENT_MODE,
           CURRENCY,
           UOM,
           WEIGHT_BREAK,
           WEIGHT_CLASS,
           RATE_TYPE,
           CONSOLE_TYPE,
           CREATED_BY,
           ACCESSLEVEL,
           TERMINALID,
           VERSION_NO,
           LANE_NO)
        VALUES
          (DECODE(V_BUYRATEID, NULL, V_SEQID, '', V_SEQID, V_BUYRATEID),
           V_SHMODE,
           V_CURRENCY,
           V_UOM,
           V_WTBREAK,
           V_WTCLASS,
           V_RATETYPE,
           V_CONSOLETYPE,
           NULL,
           NULL,
           V_TERMINALID,
           V_VERSIONNO + 1,
           DECODE(V_LANENO, NULL, V_COUNT, '', V_COUNT, V_LANENO));
        FOR J IN (SELECT LINE_NO,
                         CONTAINER_NO,
                         CONTAINERS_VALUE,
                         LOWER_BOUND,
                         UPPER_BOUND,
                         RATE_DESCRIPTION
                    FROM QMS_STG_BUYRATES_DETAILS_DATA
                   WHERE (UPPER(CONTAINERS_VALUE) <> 'NA' AND
                         UPPER(CONTAINERS_VALUE) <> 'NA' AND
                         UPPER(CONTAINERS_VALUE) <> 'NA' AND
                         UPPER(CONTAINERS_VALUE) <> 'NA')
                     AND ROW_ID = ROWID(K)) LOOP
          INSERT INTO QMS_BUYRATES_DTL
            (BUYRATEID,
             ORIGIN,
             DESTINATION,
             SERVICE_LEVEL,
             FREQUENCY,
             TRANSIT_TIME,
             CHARGERATE,
             WEIGHT_BREAK_SLAB,
             LOWERBOUND,
             UPPERBOUND,
             LANE_NO,
             NOTES,
             LINE_NO,
             CARRIER_ID,
             CREATEDTIME,
             EFFECTIVE_FROM,
             VALID_UPTO,
             ID,
             DENSITY_CODE,
             RATE_DESCRIPTION,
             VERSION_NO)
          VALUES
            (DECODE(V_BUYRATEID, NULL, V_SEQID, '', V_SEQID, V_BUYRATEID),
             ORIGIN(K),
             DESTINATION(K),
             DECODE(J.RATE_DESCRIPTION,
                    'A FREIGHT RATE',
                    SERVICELEVEL(K),
                    'SCH'),
             FREQUENCY(K),
             TRANSIT(K),
             J.CONTAINERS_VALUE,
             J.CONTAINER_NO,
             J.LOWER_BOUND,
             J.UPPER_BOUND,
             DECODE(V_LANENO, NULL, V_COUNT, '', V_COUNT, V_LANENO),
             NOTES(K),
             V_LINENO,
             CARRIER_ID(K),
             (SELECT SYSTIMESTAMP FROM DUAL),
             EFFECTIVE_FROM(K),
             VALID_UPTO(K),
             SEQ_BUYRATES_DTL.NEXTVAL,
             V_RATIO,
             J.RATE_DESCRIPTION,
             V_VERSIONNO + 1);
          V_LINENO := V_LINENO + 1;
        END LOOP;

        IF LENGTH(V_BUYRATEID) > 0 AND V_MODE = 'SEA' AND
           V_CONSOLETYPE = 'FCL' THEN
        /*  FOR M IN (SELECT QR.QUOTE_ID QUOTEID,
                           QR.BREAK_POINT BREAK_POINT,
                           BUY_RATE,
                           BUYRATE_ID,
                           RATE_LANE_NO
                      FROM QMS_QUOTE_RATES QR, QMS_BUYRATES_DTL QBD
                     WHERE QR.BUYRATE_ID = V_BUYRATEID
                       AND QR.RATE_LANE_NO = V_LANENO
                       AND EXISTS (SELECT 'X'
                              FROM QMS_QUOTE_MASTER
                             WHERE ID = QR.QUOTE_ID
                               AND ACTIVE_FLAG = 'A')
                       AND BUYRATEID = QR.BUYRATE_ID
                       AND LANE_NO = QR.RATE_LANE_NO
                       AND QBD.VERSION_NO = V_VERSIONNO + 1
                       AND WEIGHT_BREAK_SLAB = QR.BREAK_POINT
                       AND CHARGERATE <> QR.BUY_RATE

                    ) LOOP*/
                    FOR M IN (SELECT t2.* FROM (SELECT QR.QUOTE_ID QUOTEID,Qr.Version_No,QR.BREAK_POINT BREAK_POINT,BUY_RATE,BUYRATE_ID,RATE_LANE_NO,(SELECT 'X' FROM QMS_QUOTE_MASTER WHERE ID=QR.QUOTE_ID AND ACTIVE_FLAG='A')t1
					      FROM   QMS_QUOTE_RATES QR,QMS_BUYRATES_DTL QBD
				       	WHERE    QR.BUYRATE_ID = V_BUYRATEID
						    AND    QR.RATE_LANE_NO = V_LANENO

				        AND  BUYRATEID=QR.BUYRATE_ID AND LANE_NO=QR.RATE_LANE_NO AND QBD.VERSION_NO=V_VERSIONNO + 1 AND WEIGHT_BREAK_SLAB=QR.BREAK_POINT AND CHARGERATE<>QR.BUY_RATE)t2 WHERE t1 IS NOT NULL

                )
                LOOP
            BEGIN
              V_UPDATECOUNT := V_UPDATECOUNT + 1;
              SELECT COUNT(QUOTE_ID)
                INTO V_COUNTQUOTE
                FROM QMS_QUOTES_UPDATED QU
               WHERE QUOTE_ID = M.QUOTEID
                 AND QU.SELL_BUY_FLAG = 'BR'

                 AND CONFIRM_FLAG IS NULL;
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_COUNTQUOTE := 0;
            END;

            IF V_COUNTQUOTE = 0 THEN

              INSERT INTO QMS_QUOTES_UPDATED
                (QUOTE_ID,
                 NEW_BUYCHARGE_ID,
                 NEW_LANE_NO,
                 SELL_BUY_FLAG,
                 CHANGEDESC,
                 OLD_BUYCHARGE_ID,
                 OLD_LANE_NO,
                 OLD_VERSION_NO,
                 NEW_VERSION_NO)
              VALUES
                (M.QUOTEID,
                 V_BUYRATEID,
                 V_LANENO,
                 'BR',
                 ORIGIN(K) || '-' || DESTINATION(K) || ',' || M.BREAK_POINT ||
                 'FREIGHT RATES AND SURCHARGES',
                 V_BUYRATEID,
                 V_LANENO,
                 V_VERSIONNO,
                 V_VERSIONNO + 1);

            ELSE
              V_CHANGEDESC1 := M.BREAK_POINT || ',' || ORIGIN(K) || '-' ||
                               DESTINATION(K) ||
                               ', SEA FREIGHT RATES AND SURCHARGES';
              SELECT CHANGEDESC
                INTO V_CHANGEDESC
                FROM QMS_QUOTES_UPDATED
               WHERE QUOTE_ID = M.QUOTEID
                 AND SELL_BUY_FLAG = 'BR'
                 AND CONFIRM_FLAG IS NULL;
              IF V_CHANGEDESC1 = V_CHANGEDESC THEN
                V_CHANGEDESC := V_CHANGEDESC;
              ELSE
                V_CHANGEDESC := M.BREAK_POINT || ',' || V_CHANGEDESC;
              END IF;
              UPDATE QMS_QUOTES_UPDATED
                 SET CHANGEDESC     = V_CHANGEDESC,
                     NEW_VERSION_NO = V_VERSIONNO + 1
               WHERE QUOTE_ID = M.QUOTEID
                 AND SELL_BUY_FLAG = 'BR'
                 AND CONFIRM_FLAG IS NULL;
            END IF;

          END LOOP;
        END IF;


        IF V_CONSOLETYPE IS NULL OR V_CONSOLETYPE = '' OR
           V_CONSOLETYPE != 'FCL' OR V_UPDATECOUNT = 0 THEN


          IF V_RETURNVALUE = 3 AND LENGTH(V_BUYRATEID) > 0 THEN

            PKG_QMS_BUYRATES.QMS_UPDATE_NEW_QUOTE('BR',
                                                  ORIGIN(K),
                                                  DESTINATION(K),
                                                  SERVICELEVEL(K),
                                                  CARRIER_ID(K),
                                                  VALID_UPTO(K),
                                                  FREQUENCY(K),
                                                  TRANSIT(K),
                                                  V_BUYRATEID,
                                                  NULL,
                                                  NULL,
                                                  V_VERSIONNO + 1,
                                                  V_LANENO,
                                                  V_TERMINALID);

          END IF;

          IF V_RETURNVALUE = 2 AND LENGTH(V_BUYRATEID) > 0 THEN

            QMS_BUY_RATES_PKG.SELLRATESMSTR_ACC_PROC(V_BUYRATEID,
                                                     V_VERSIONNO + 1,
                                                     V_LANENO);

          END IF;
        END IF;

        IF V_CONSOLETYPE = 'FCL' AND V_UPDATECOUNT <> 0 AND
           LENGTH(V_BUYRATEID) > 0 AND
           V_RETURNVALUE = '2' THEN

          QMS_BUY_RATES_PKG.SELLRATESMSTR_ACC_PROC(V_BUYRATEID,
                                                   V_VERSIONNO + 1,
                                                   V_LANENO);

        END IF;
        IF V_LANENO IS NULL THEN
          V_COUNT := V_COUNT + 1;
        END IF;
      END LOOP;
      RETURNSTR := 6;
    ELSE
      RETURNSTR := 4;
    END IF;
    RETURN RETURNSTR;
  END INSERTDETAILS;

  FUNCTION BUY_RATES_DELETE_PROC RETURN VARCHAR2 IS
    V_COUNT     NUMBER(10) :=2;
    V_COUNT1    NUMBER(10);
    V_RETURNSTR VARCHAR2(5);
    V_QUOTEID   VARCHAR2(4000);
    V_ERRORMSG  NUMBER(10);
    V_BUYRATEID NUMBER(10);
    V_LANENO    NUMBER(10);
    V_VERSIONNO NUMBER(10);
  BEGIN
      COMMIT;
    FOR I IN (SELECT * FROM QMS_BUYRATES_DELETE_DETAILS) LOOP
      dbms_output.put_line(I.DENSITY_CODE||'---------I.DENSITY_CODE');
      BEGIN
       /* SELECT COUNT(QUOTE_ID)
          INTO V_COUNT
          FROM QMS_QUOTE_RATES QR, QMS_BUYRATES_DTL QBD
         WHERE QR.BUYRATE_ID = QBD.BUYRATEID
           AND QR.RATE_LANE_NO = QBD.LANE_NO
           AND QR.VERSION_NO = QBD.VERSION_NO
           AND QBD.ORIGIN = I.ORIGIN
           AND QBD.DESTINATION = I.DESTINATION
           AND QBD.CARRIER_ID = I.CARRIER_ID
           AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
           AND QBD.FREQUENCY = I.FREQUENCY
           AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
           AND EXISTS
         (SELECT 'X'
                  FROM QMS_BUYRATES_MASTER
                 WHERE BUYRATEID = QBD.BUYRATEID
                   AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
                   AND VERSION_NO = QBD.VERSION_NO
                   AND TERMINALID = I.TERMINAL_ID);*/
     select count(1)  INTO V_COUNT from (SELECT distinct QR.QUOTE_ID,QBM.BUYRATEID/*(SELECT BUYRATEID
        FROM QMS_BUYRATES_MASTER BM
       WHERE BUYRATEID = QBD.BUYRATEID
         AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
         AND VERSION_NO = QBD.VERSION_NO
         AND TERMINALID = I.TERMINAL_ID
                   )t1*/
      FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_RATES QR, QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM
         WHERE QR.BUYRATE_ID = QBD.BUYRATEID
           AND QR.RATE_LANE_NO = QBD.LANE_NO
           AND QM.ACTIVE_FLAG = 'A'
           AND QM.ID = QR.QUOTE_ID
           AND QR.VERSION_NO = QBD.VERSION_NO
           AND QBD.BUYRATEID=QBM.BUYRATEID
           AND (QBM.LANE_NO=QBD.LANE_NO OR QBM.LANE_NO IS NULL)
           AND QBD.VERSION_NO=QBM.VERSION_NO
           AND QBM.TERMINALID=I.TERMINAL_ID
           AND QBD.ORIGIN = I.ORIGIN
           AND QBD.DESTINATION = I.DESTINATION
           AND QBD.CARRIER_ID = I.CARRIER_ID
           AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
           AND QBM.CURRENCY  = I.CURRENCY--GOVIND
           AND QBD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
          -- AND QBD.FREQUENCY = I.FREQUENCY
          -- AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
--GROUP BY (i.origin,i.destination,i.carrier_id,i.service_level,i.frequency,i.terminal_id)
         )t2 /*WHERE t1 IS not NULL */   ;

      EXCEPTION
      when others then

        dbms_output.put_line(sqlerrm);
      END;
      IF V_COUNT = 0 THEN
      BEGIN

      SELECT DISTINCT QM.quote_id INTO V_QUOTEID FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED qu, QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM
         WHERE
         QM.ID=QU.QUOTE_ID AND
         Qu.new_buycharge_ID = QBD.BUYRATEID
           AND Qu.new_LANE_NO = QBD.LANE_NO
           AND Qu.new_VERSION_NO = QBD.VERSION_NO AND qu.confirm_flag IS NULL AND QU.SELL_BUY_FLAG IN ('BR','RSR')
           AND QBD.BUYRATEID=QBM.BUYRATEID
           AND (QBM.LANE_NO=QBD.LANE_NO OR QBM.LANE_NO IS NULL)
           AND QBD.VERSION_NO=QBM.VERSION_NO
           AND QBM.TERMINALID=I.TERMINAL_ID
           AND QBD.ORIGIN=I.ORIGIN AND QBD.DESTINATION=I.DESTINATION AND QBD.CARRIER_ID=I.CARRIER_ID
           AND QBM.CURRENCY  = I.CURRENCY--GOVIND
           AND QBD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
           AND QBD.SERVICE_LEVEL=I.SERVICE_LEVEL AND QBD.FREQUENCY=I.FREQUENCY
           --AND QBD.TRANSIT_TIME =I.TRANSIT_TIME
           AND QBD.LINE_NO=0 AND QBD.ACTIVEINACTIVE IS NULL;
           EXCEPTION WHEN NO_DATA_FOUND THEN
           V_COUNT:=0;
           END;
         IF   V_QUOTEID IS NULL
         THEN
            BEGIN

        SELECT BD.BUYRATEID, BD.LANE_NO, BD.VERSION_NO
          INTO V_BUYRATEID, V_LANENO, V_VERSIONNO
          FROM QMS_BUYRATES_DTL BD,QMS_BUYRATES_MASTER BM
         WHERE BD.BUYRATEID=BM.BUYRATEID
           AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL)
           AND BD.VERSION_NO=BM.VERSION_NO
           AND BM.TERMINALID=I.TERMINAL_ID
           AND BM.CURRENCY  = I.CURRENCY--GOVIND
           AND BD.ORIGIN = I.ORIGIN
           AND BD.DESTINATION = I.DESTINATION
           AND BD.CARRIER_ID = I.CARRIER_ID
           AND BD.SERVICE_LEVEL = I.SERVICE_LEVEL
           AND BD.FREQUENCY = I.FREQUENCY
           AND BD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
         --  AND BD.TRANSIT_TIME = I.TRANSIT_TIME
           AND LINE_NO = 0
           AND ACTIVEINACTIVE IS NULL;
           EXCEPTION WHEN OTHERS THEN
           DBMS_OUTPUT.put_line(I.ORIGIN||': '||I.DESTINATION||': '||I.CARRIER_ID||': '||I.SERVICE_LEVEL||': '||I.FREQUENCY||': '||I.TRANSIT_TIME);
           END;
        DELETE FROM QMS_BUYRATES_DTL QBD
         WHERE /*QBD.ORIGIN = I.ORIGIN
           AND QBD.DESTINATION = I.DESTINATION
           AND QBD.CARRIER_ID = I.CARRIER_ID
           AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
           AND QBD.FREQUENCY = I.FREQUENCY
           AND QBD.TRANSIT_TIME = I.TRANSIT_TIME*/
           QBD.BUYRATEID=V_BUYRATEID
           AND QBD.LANE_NO=V_LANENO
           AND QBD.VERSION_NO=V_VERSIONNO
           AND EXISTS
         (SELECT 'X'
                  FROM QMS_BUYRATES_MASTER
                 WHERE BUYRATEID = QBD.BUYRATEID
                   AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL) /* AND VERSION_NO=QBD.VERSION_NO*/
                   AND TERMINALID = I.TERMINAL_ID);
                  /*      AND NOT EXISTS(SELECT 'X' FROM QMS_QUOTE_RATES QR WHERE QR.BUYRATE_ID=QBD.BUYRATEID AND QR.RATE_LANE_NO=QBD.LANE_NO AND QR.VERSION_NO=QBD.VERSION_NO)
        */
        DELETE FROM QMS_REC_CON_SELLRATESDTL RSD
        WHERE RSD.BUYRATEID=V_BUYRATEID
        AND RSD.LANE_NO=V_LANENO
        AND RSD.VERSION_NO=V_VERSIONNO
        AND EXISTS
         (SELECT 'X' FROM QMS_REC_CON_SELLRATESMASTER RSM WHERE RSM.REC_CON_ID=RSD.REC_CON_ID
         AND RSM.TERMINALID=I.TERMINAL_ID);

        DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC RSDA
        WHERE RSDA.BUYRATEID= V_BUYRATEID
        AND RSDA.LANE_NO=V_VERSIONNO
        AND EXISTS (
        SELECT 'X' FROM QMS_REC_CON_SELLRATESMSTR_ACC RSMA WHERE RSMA.REC_CON_ID=RSDA.REC_CON_ID
        AND RSMA.TERMINALID=I.TERMINAL_ID
        );
        ELSE

          UPDATE QMS_BUYRATES_DELETE_DETAILS
             SET ERROR_MSG = V_QUOTEID
           WHERE ROW_ID = I.ROW_ID;
        END IF;
      ELSE
        BEGIN
         /* SELECT COUNT(QM.QUOTE_ID)
            INTO V_COUNT1
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
             AND QR.LINE_NO = QBD.LINE_NO
             AND QBD.LINE_NO = '0'
             AND QBD.FREQUENCY = I.FREQUENCY
             AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
             AND EXISTS
           (SELECT 'X'
                    FROM QMS_BUYRATES_MASTER
                   WHERE BUYRATEID = QBD.BUYRATEID
                     AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL) \*AND VERSION_NO=QBD.VERSION_NO*\
                     AND TERMINALID = I.TERMINAL_ID);*/
       SELECT t3.quote_id INTO V_COUNT1 FROM (SELECT COUNT(Qr.QUOTE_ID)QUOTE_ID,(SELECT qm.id FROM QMS_QUOTE_MASTER qm WHERE QM.ID = QR.QUOTE_ID
             AND QM.ACTIVE_FLAG = 'A')t1,(SELECT BUYRATEID
                    FROM QMS_BUYRATES_MASTER
                   WHERE BUYRATEID = QBD.BUYRATEID
                     AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL)
                      AND VERSION_NO = QBD.VERSION_NO--ADDED BY SUBBU
                     AND TERMINALID = I.TERMINAL_ID)t2

            FROM    QMS_QUOTE_RATES  QR,
                 QMS_BUYRATES_DTL QBD
           WHERE
              QR.BUYRATE_ID = QBD.BUYRATEID
             AND QR.RATE_LANE_NO = QBD.LANE_NO
             --AND QR.VERSION_NO = QBD.VERSION_NO
             AND QBD.ORIGIN = I.ORIGIN
             AND QBD.DESTINATION = I.DESTINATION
             AND QBD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
             AND QBD.CARRIER_ID = I.CARRIER_ID
             AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
             AND QR.LINE_NO = QBD.LINE_NO
             AND QBD.LINE_NO = '0'
             AND QBD.FREQUENCY = I.FREQUENCY

           --  AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
            GROUP BY (i.origin,i.destination,i.carrier_id,i.service_level,i.frequency,i.terminal_id))t3 WHERE t1 IS NOT NULL AND t2 IS NOT NULL;

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            V_COUNT1 := 0;
        END;
        DBMS_OUTPUT.PUT_LINE(V_COUNT1);
        IF V_COUNT1 = 0 THEN
        BEGIN
        --@@ Commented and Added by subrahmanyam for the pbn id: 218197 on 22/sep/10
          /*SELECT BUYRATEID, LANE_NO, VERSION_NO
            INTO V_BUYRATEID, V_LANENO, V_VERSIONNO
            FROM QMS_BUYRATES_DTL BD
           WHERE BD.ORIGIN = I.ORIGIN
             AND BD.DESTINATION = I.DESTINATION
             AND BD.CARRIER_ID = I.CARRIER_ID
             AND BD.SERVICE_LEVEL = I.SERVICE_LEVEL
             AND BD.FREQUENCY = I.FREQUENCY
            -- AND BD.TRANSIT_TIME = I.TRANSIT_TIME
             AND LINE_NO = 0
             AND ACTIVEINACTIVE IS NULL;*/
             SELECT BD.BUYRATEID, BD.LANE_NO, BD.VERSION_NO
          INTO V_BUYRATEID, V_LANENO, V_VERSIONNO
          FROM QMS_BUYRATES_DTL BD,QMS_BUYRATES_MASTER BM
         WHERE BD.BUYRATEID=BM.BUYRATEID
           AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL)
           AND BD.VERSION_NO=BM.VERSION_NO
           AND BM.TERMINALID=I.TERMINAL_ID
           AND BM.CURRENCY = I.CURRENCY--GOVIND
           AND BD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
           AND BD.ORIGIN = I.ORIGIN
           AND BD.DESTINATION = I.DESTINATION
           AND BD.CARRIER_ID = I.CARRIER_ID
           AND BD.SERVICE_LEVEL = I.SERVICE_LEVEL
           AND BD.FREQUENCY = I.FREQUENCY
           AND BD.ACTIVEINACTIVE IS NULL
           AND BD.LINE_NO=0 AND (BD.INVALIDATE='F' OR BD.INVALIDATE IS NULL);
             EXCEPTION WHEN OTHERS THEN
                        DBMS_OUTPUT.put_line(I.ORIGIN||': '||I.DESTINATION||': '||I.CARRIER_ID||': '||I.SERVICE_LEVEL||': '||I.FREQUENCY||': '||I.TRANSIT_TIME);
                           END;
          UPDATE QMS_BUYRATES_DTL QBD
             SET ACTIVEINACTIVE = 'I'
           WHERE /*QBD.ORIGIN = I.ORIGIN
             AND QBD.DESTINATION = I.DESTINATION
             AND QBD.CARRIER_ID = I.CARRIER_ID
             AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
             AND QBD.FREQUENCY = I.FREQUENCY
             AND QBD.TRANSIT_TIME = I.TRANSIT_TIME*/
             QBD.BUYRATEID=V_BUYRATEID
             AND QBD.LANE_NO=V_LANENO
             AND QBD.VERSION_NO=V_VERSIONNO
             AND EXISTS
           (SELECT 'X'
                    FROM QMS_BUYRATES_MASTER
                   WHERE BUYRATEID = QBD.BUYRATEID
                     AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL) /*AND VERSION_NO=QBD.VERSION_NO*/
                     AND TERMINALID = I.TERMINAL_ID);
            UPDATE QMS_REC_CON_SELLRATESDTL RSD
            SET AI_FLAG='I' WHERE RSD.BUYRATEID=V_BUYRATEID
            AND RSD.LANE_NO=V_LANENO
            AND RSD.AI_FLAG='A'
            AND EXISTS (SELECT 'X' FROM QMS_REC_CON_SELLRATESMASTER RSM
            WHERE RSM.REC_CON_ID=RSD.REC_CON_ID AND RSM.TERMINALID=I.TERMINAL_ID);

        ELSE
          FOR J IN (SELECT QM.QUOTE_ID
                      FROM QMS_QUOTE_MASTER QM,
                           QMS_QUOTE_RATES  QR,
                           QMS_BUYRATES_DTL QBD,
                           QMS_BUYRATES_MASTER QBM
                     WHERE QM.ID = QR.QUOTE_ID
                       AND QM.ACTIVE_FLAG = 'A'
                       AND QBD.BUYRATEID=QBM.BUYRATEID
                       AND (QBD.LANE_NO=QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                       AND QBD.VERSION_NO=QBM.VERSION_NO
                       AND QR.BUYRATE_ID = QBD.BUYRATEID
                       AND QR.RATE_LANE_NO = QBD.LANE_NO
                       AND QR.VERSION_NO = QBD.VERSION_NO
                       AND QBD.ORIGIN = I.ORIGIN
                       AND QBM.CURRENCY = I.CURRENCY--GOVIND
                       AND QBD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
                       AND QBD.DESTINATION = I.DESTINATION
                       AND QBD.CARRIER_ID = I.CARRIER_ID
                       AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
                       AND QBD.FREQUENCY = I.FREQUENCY
                      -- AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
                       AND QR.LINE_NO='0'
                       AND QBD.LINE_NO=QR.LINE_NO
                       AND EXISTS
                     (SELECT 'X'
                              FROM QMS_BUYRATES_MASTER
                             WHERE BUYRATEID = QBD.BUYRATEID
                               AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL) /*AND VERSION_NO=QBD.VERSION_NO*/
                               AND TERMINALID = I.TERMINAL_ID))

           LOOP
                IF V_QUOTEID IS NULL THEN
              V_QUOTEID := V_QUOTEID || J.QUOTE_ID ;--|| ',';
            --dbms_output.put_line('1..'||V_QUOTEID);
        ELSIF (INSTR(V_QUOTEID,J.QUOTE_ID)=0) THEN
         -- ELSE
              dbms_output.put_line('2..'||V_QUOTEID);
              V_QUOTEID := V_QUOTEID || ',' || J.QUOTE_ID;
            END IF;
               END LOOP;
           FOR J IN (SELECT QM.QUOTE_ID
                      FROM QMS_QUOTES_UPDATED QU,QMS_QUOTE_MASTER QM,
                             QMS_BUYRATES_DTL QBD,
                           QMS_BUYRATES_MASTER QBM
                     WHERE QM.ID = QU.QUOTE_ID
                       AND QBD.BUYRATEID=QBM.BUYRATEID
                       AND (QBD.LANE_NO=QBM.LANE_NO OR QBM.LANE_NO IS NULL)
                       AND QBD.VERSION_NO=QBM.VERSION_NO
                       AND QM.ACTIVE_FLAG = 'A'
                       AND QU.NEW_BUYCHARGE_ID = QBD.BUYRATEID
                       AND QU.NEW_LANE_NO = QBD.LANE_NO
                       AND QU.NEW_VERSION_NO = QBD.VERSION_NO
                       AND QU.CONFIRM_FLAG IS NULL
                       AND QBM.CURRENCY = I.CURRENCY--GOVIND
                       AND QBD.DENSITY_CODE= TO_NUMBER(I.DENSITY_CODE)--GOVIND
                       AND QBD.ORIGIN = I.ORIGIN
                       AND QBD.DESTINATION = I.DESTINATION
                       AND QBD.CARRIER_ID = I.CARRIER_ID
                       AND QBD.SERVICE_LEVEL = I.SERVICE_LEVEL
                       AND QBD.FREQUENCY = I.FREQUENCY
                     --  AND QBD.TRANSIT_TIME = I.TRANSIT_TIME
                       AND QBD.LINE_NO='0'
                       AND EXISTS
                     (SELECT 'X'
                              FROM QMS_BUYRATES_MASTER
                             WHERE BUYRATEID = QBD.BUYRATEID
                               AND (LANE_NO = QBD.LANE_NO OR LANE_NO IS NULL) /*AND VERSION_NO=QBD.VERSION_NO*/
                               AND TERMINALID = I.TERMINAL_ID))

           LOOP
   dbms_output.put_line('3..'||V_QUOTEID);
            IF V_QUOTEID IS NULL THEN
              V_QUOTEID := V_QUOTEID || J.QUOTE_ID ;--|| ',';

       ELSIF (INSTR(V_QUOTEID,J.QUOTE_ID)=0) THEN
       --   ELSE
                    --    dbms_output.put_line('4..'||V_QUOTEID);
              V_QUOTEID := V_QUOTEID || ',' || J.QUOTE_ID;
            END IF;
            END LOOP;

          UPDATE QMS_BUYRATES_DELETE_DETAILS
             SET ERROR_MSG = V_QUOTEID
           WHERE ROW_ID = I.ROW_ID;
                 V_QUOTEID :='';
        END IF;
      END IF;
    END LOOP;
    COMMIT;
    SELECT COUNT(ERROR_MSG)
      INTO V_ERRORMSG
      FROM QMS_BUYRATES_DELETE_DETAILS
     WHERE ERROR_MSG IS NOT NULL;
    IF V_ERRORMSG = 0 THEN
      V_RETURNSTR := '6';
    ELSE
      V_RETURNSTR := '5';
    END IF;

    RETURN V_RETURNSTR;
  END;
END QMS_BUY_RATES_UPLOAD_PKG;

/

/

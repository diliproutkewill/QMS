--------------------------------------------------------
--  DDL for Package Body PKG_QMS_CARTAGE_ACC
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "PKG_QMS_CARTAGE_ACC" 
AS
   PROCEDURE Cartage_Activeinactive(p_Cartage_Id_Old VARCHAR2,
                                    p_Cartage_Id_New VARCHAR2,
                                    p_Weight_Break   VARCHAR2,
                                    p_Rate_Type      VARCHAR2,
                                    p_Location_Id    VARCHAR2,
                                    p_Zone_Code      VARCHAR2,
                                    p_Charge_Basis   VARCHAR2,
                                    p_Charge_Type    VARCHAR2,
                                    p_Shipment_Mode  VARCHAR2,
                                    p_Console_Type   VARCHAR2,
                                    p_Count          NUMBER) AS
     Rcount       NUMBER(5);
     Rcount1      NUMBER(5);
     p_Zcode      VARCHAR(50) := '';
     v_Pos        NUMBER;
     v_Count      NUMBER;
     v_Cartage_Id VARCHAR2(160);
   BEGIN
     SELECT COUNT(*)
       INTO Rcount
       FROM QMS_CARTAGE_BUYSELLCHARGES_A Mas, QMS_CARTAGE_BUYDTL_A Dtl
      WHERE Mas.Cartage_Id = Dtl.Cartage_Id
        AND Mas.Location_Id = p_Location_Id
        AND Dtl.Zone_Code = p_Zone_Code
        AND Mas.Shipment_Mode = p_Shipment_Mode
        AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
        AND Dtl.Charge_Type = p_Charge_Type;
     IF Rcount > 0 THEN
       DELETE FROM QMS_CARTAGE_SELLDTL_A
        WHERE (Cartage_Id, Zone_Code, Charge_Type) IN
              (SELECT Dtl.Cartage_Id, Dtl.Zone_Code, Dtl.Charge_Type
                 FROM QMS_CARTAGE_BUYSELLCHARGES_A Mas,
                      QMS_CARTAGE_SELLDTL_A        Dtl
                WHERE Mas.Cartage_Id = Dtl.Cartage_Id
                  AND Mas.Location_Id = p_Location_Id
                  AND Dtl.Zone_Code = p_Zone_Code
                  AND Mas.Shipment_Mode = p_Shipment_Mode
                  AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
                  AND Dtl.Charge_Type = p_Charge_Type);
       DELETE FROM QMS_CARTAGE_BUYDTL_A
        WHERE (Cartage_Id, Zone_Code, Charge_Type) IN
              (SELECT Dtl.Cartage_Id, Dtl.Zone_Code, Dtl.Charge_Type
                 FROM QMS_CARTAGE_BUYSELLCHARGES_A Mas,
                      QMS_CARTAGE_BUYDTL_A         Dtl
                WHERE Mas.Cartage_Id = Dtl.Cartage_Id
                  AND Mas.Location_Id = p_Location_Id
                  AND Dtl.Zone_Code = p_Zone_Code
                  AND Mas.Shipment_Mode = p_Shipment_Mode
                  AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
                  AND Dtl.Charge_Type = p_Charge_Type);
     END IF;
     SELECT COUNT(*)
       INTO Rcount
       FROM QMS_CARTAGE_BUYSELLCHARGES Mas, QMS_CARTAGE_SELLDTL Dtl
      WHERE Mas.Cartage_Id = Dtl.Cartage_Id
        AND Mas.Location_Id = p_Location_Id
        AND Dtl.Charge_Type = p_Charge_Type
        AND Dtl.Zone_Code = p_Zone_Code
        AND Mas.Shipment_Mode = p_Shipment_Mode
        AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
        AND Dtl.Activeinactive = 'A';
     IF Rcount > 0 THEN
       SELECT DISTINCT Mas.Cartage_Id
         INTO v_Cartage_Id
         FROM QMS_CARTAGE_BUYSELLCHARGES Mas, QMS_CARTAGE_SELLDTL Dtl
        WHERE Mas.Cartage_Id = Dtl.Cartage_Id
          AND Mas.Location_Id = p_Location_Id
          AND Dtl.Charge_Type = p_Charge_Type
          AND Zone_Code = p_Zone_Code
          AND Mas.Shipment_Mode = p_Shipment_Mode
          AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
          AND Dtl.Activeinactive = 'A';
       IF p_Count = 0 THEN
         INSERT INTO QMS_CARTAGE_BUYSELLCHARGES_A
           SELECT *
             FROM QMS_CARTAGE_BUYSELLCHARGES
            WHERE Cartage_Id = p_Cartage_Id_New;
       END IF;
       INSERT INTO QMS_CARTAGE_BUYDTL_A
         SELECT *
           FROM QMS_CARTAGE_BUYDTL
          WHERE Cartage_Id = p_Cartage_Id_New
            AND Zone_Code = p_Zone_Code
            AND Charge_Type = p_Charge_Type;
       Cartage_Update_Sellcharges(v_Cartage_Id,
                                  p_Cartage_Id_New,
                                  p_Location_Id,
                                  p_Zone_Code,
                                  p_Charge_Type,
                                  p_Weight_Break,
                                  p_Rate_Type,
                                  p_Shipment_Mode,
                                  p_Console_Type);
     END IF;
     --v_count := v_count+1;
     --EXIT WHEN p_temp_str IS NULL;
     --END LOOP;
     --END IF;                         /* END OF IF( LENGTH(p_zone_code)>0 ) */
   END Cartage_Activeinactive;

PROCEDURE Cartage_Update_Sellcharges(p_Cartage_Id_Old VARCHAR2,
                                     p_Cartage_Id_New VARCHAR2,
                                     p_Location_Id    VARCHAR2,
                                     p_Zone_Code      VARCHAR2,
                                     p_Charge_Type    VARCHAR2,
                                     p_Weight_Break   VARCHAR2,
                                     p_Rate_Type      VARCHAR2,
                                     p_Shipment_Mode  VARCHAR2,
                                     p_Console_Type   VARCHAR2) IS
  Cur_Sellcharges  Sys_Refcursor;
  Charge_Rate      NUMBER;
  Charge_Slab      VARCHAR2(100);
  v_Margin_Type    QMS_CARTAGE_SELLDTL.Margin_Type%TYPE;
  v_Margin         QMS_CARTAGE_SELLDTL.Margin%TYPE;
  v_Zone_Code      QMS_CARTAGE_SELLDTL.Zone_Code%TYPE;
  v_Overall_Margin QMS_CARTAGE_SELLDTL.Overall_Margin%TYPE;
  v_Margin_Basis   QMS_CARTAGE_SELLDTL.Margin_Basis%TYPE;
  v_Chargerate     QMS_CARTAGE_SELLDTL.Chargerate%TYPE;
  v_Chargeslab     QMS_CARTAGE_SELLDTL.Chargeslab%TYPE;
  v_Lowerbound     QMS_CARTAGE_SELLDTL.Lowerbound%TYPE;
  v_Upperbound     QMS_CARTAGE_SELLDTL.Upperbound%TYPE;
  v_Charge_Type    QMS_CARTAGE_SELLDTL.Charge_Type%TYPE;
  --v_uom              QMS_CARTAGE_SELLDTL.uom%TYPE;
  v_Charge_Basis   QMS_CARTAGE_SELLDTL.Charge_Basis%TYPE;
  v_Eff_From       QMS_CARTAGE_SELLDTL.Effective_From%TYPE;
  v_Valid_Upto     QMS_CARTAGE_SELLDTL.Valid_Upto%TYPE;
  v_Currency       QMS_CARTAGE_SELLDTL.Currency%TYPE;
  v_Activeinactive QMS_CARTAGE_SELLDTL.Activeinactive%TYPE;
  v_Id             QMS_CARTAGE_SELLDTL.Id%TYPE;
  v_Line_No        QMS_CARTAGE_SELLDTL.Line_No%TYPE;
  v_Buyrate_Amt    QMS_CARTAGE_SELLDTL.Buyrate_Amt%TYPE;
  v_Rate_Indicator QMS_CARTAGE_SELLDTL.Chargerate_Indicator%TYPE;
BEGIN
  --DBMS_OUTPUT.put_line (p_zone_code);
  OPEN Cur_Sellcharges FOR
    SELECT Chargerate,
           Chargeslab,
           Zone_Code,
           Lowerbound,
           Upperbound,
           Charge_Type,
           Line_No,
           Chargerate_Indicator
      FROM QMS_CARTAGE_BUYSELLCHARGES_A Mas, QMS_CARTAGE_BUYDTL_A Dtl
     WHERE Mas.Cartage_Id = Dtl.Cartage_Id
       AND Mas.Cartage_Id = p_Cartage_Id_New
       AND Mas.Weight_Break = p_Weight_Break
       AND Mas.Rate_Type = p_Rate_Type
       AND Mas.Location_Id = p_Location_Id
       AND Dtl.Zone_Code = p_Zone_Code
       AND Dtl.Charge_Type = p_Charge_Type;
  --|| ')';
  LOOP
    FETCH Cur_Sellcharges
      INTO Charge_Rate, Charge_Slab, v_Zone_Code, v_Lowerbound, v_Upperbound, v_Charge_Type, v_Line_No, v_Rate_Indicator;
    EXIT WHEN Cur_Sellcharges%NOTFOUND;
    --dbms_output.put_line(charge_rate||'     '||charge_slab);
    BEGIN
      SELECT DISTINCT Margin_Type,
                      Overall_Margin,
                      Charge_Basis,
                      Currency,
                      Effective_From,
                      Valid_Upto
        INTO v_Margin_Type,
             v_Overall_Margin,
             v_Charge_Basis,
             v_Currency,
             v_Eff_From,
             v_Valid_Upto
        FROM QMS_CARTAGE_SELLDTL
       WHERE Cartage_Id = p_Cartage_Id_Old
         AND Zone_Code = p_Zone_Code
         AND (Activeinactive IS NULL OR Activeinactive = 'A')
         AND Charge_Type = p_Charge_Type;
      INSERT INTO QMS_CARTAGE_SELLDTL_A
        (Cartage_Id,
         Zone_Code,
         Chargerate,
         Margin,--@@ added by subrahmanyam for 170759
         Chargeslab,
         Lowerbound,
         Upperbound,
         Charge_Type,
         Buyrate_Amt,
         Activeinactive,
         Line_No,
         Chargerate_Indicator,
         Id,
         Old_Sell_Id)
      VALUES
        (p_Cartage_Id_New,
         v_Zone_Code,
        /* 0,*/    --@@ Commented by subrahmanyam for 170759
        Charge_Rate,--@@ Added by subrahmanyam for 170759
                   0,--@@ Added by subrahmanyam for 170759
         Charge_Slab,
         v_Lowerbound,
         v_Upperbound,
         v_Charge_Type,
         Charge_Rate,
         'A',
         v_Line_No,
         v_Rate_Indicator,
         Seq_Cartage_Selldtl.NEXTVAL,
         p_Cartage_Id_Old);
      UPDATE QMS_CARTAGE_SELLDTL_A
         SET Margin_Type    = v_Margin_Type,
             Overall_Margin = v_Overall_Margin,
             Charge_Basis   = v_Charge_Basis,
             Currency       = v_Currency,
             Effective_From = v_Eff_From,
             Valid_Upto     = v_Valid_Upto
       WHERE Cartage_Id = p_Cartage_Id_New
         AND Charge_Type = p_Charge_Type
         AND Zone_Code = p_Zone_Code;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        EXIT;
    END;
  END LOOP;
  CLOSE Cur_Sellcharges;
  OPEN Cur_Sellcharges FOR
    SELECT Dtl.Margin, Dtl.Margin_Type, Acc.Buyrate_Amt, Dtl.Chargeslab
      FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_SELLDTL_A Acc
     WHERE Dtl.Zone_Code = Acc.Zone_Code
       AND Dtl.Charge_Type = Acc.Charge_Type
       AND Dtl.Chargeslab = Acc.Chargeslab
       AND Acc.Cartage_Id = p_Cartage_Id_New
       AND Dtl.Cartage_Id = p_Cartage_Id_Old
       AND Dtl.Zone_Code = p_Zone_Code
       AND (Dtl.Activeinactive IS NULL OR Dtl.Activeinactive = 'A')
       AND Dtl.Charge_Type = p_Charge_Type;
  LOOP
    FETCH Cur_Sellcharges
      INTO v_Margin, v_Margin_Type, Charge_Rate, Charge_Slab;
    EXIT WHEN Cur_Sellcharges%NOTFOUND;
    IF UPPER(v_Margin_Type) = 'A' THEN
      v_Chargerate := Charge_Rate + v_Margin;
    END IF;
    IF UPPER(v_Margin_Type) = 'P' THEN
      v_Chargerate := Charge_Rate * (v_Margin / 100) + Charge_Rate;
    END IF;
    UPDATE QMS_CARTAGE_SELLDTL_A
       SET Margin = v_Margin, Chargerate = v_Chargerate
     WHERE Cartage_Id = p_Cartage_Id_New
       AND Charge_Type = p_Charge_Type
       AND Zone_Code = p_Zone_Code
       AND Chargeslab = Charge_Slab;
  END LOOP;
  CLOSE Cur_Sellcharges;
  UPDATE QMS_CARTAGE_SELLDTL Dtl
     SET Acceptance_Flag = 'Y'
   WHERE (Cartage_Id, Zone_Code, Charge_Type) IN
         (SELECT Dtl.Cartage_Id, Dtl.Zone_Code, Dtl.Charge_Type
            FROM QMS_CARTAGE_BUYSELLCHARGES Mas
           WHERE Mas.Cartage_Id = Dtl.Cartage_Id
             AND Dtl.Zone_Code = p_Zone_Code
             AND Dtl.Charge_Type = p_Charge_Type
             AND Mas.Location_Id = p_Location_Id
             AND Dtl.Activeinactive = 'A'
             AND Mas.Shipment_Mode = p_Shipment_Mode
             AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~'));
END Cartage_Update_Sellcharges;

  PROCEDURE cartage_buycharges (
      p_operation           VARCHAR2,
      p_terminalid          VARCHAR2,
      p_chargetype          VARCHAR2,
      p_rate_type           VARCHAR2,
      p_location_id         VARCHAR2,
      p_shipment_mode       VARCHAR2,
      p_zonecode            VARCHAR2,
      p_weightbreak         VARCHAR2,
      p_dtls_data     OUT   resultset,
      p_wtbrks_data   OUT   resultset
   )
   AS
      v_terminal       VARCHAR2 (300);
      v_charge_type    VARCHAR2 (300);
      v_main_qry       VARCHAR2 (3000);
      v_qry            VARCHAR2 (3000);
      v_dyna_qry       VARCHAR2 (32767);
      v_terminals      VARCHAR2 (32767);
      v_opr_adm_flag   VARCHAR2 (2);

      CURSOR c1
      IS
         (SELECT     parent_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id
          START WITH child_terminal_id = p_terminalid
          UNION
          SELECT terminalid term_id
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminalid term_id
            FROM DUAL);
      CURSOR c3
      IS
         SELECT     child_terminal_id term_id
               FROM FS_FR_TERMINAL_REGN
         CONNECT BY PRIOR child_terminal_id = parent_terminal_id
         START WITH parent_terminal_id = p_terminalid UNION
          SELECT p_terminalid term_id
            FROM DUAL;
      CURSOR c2
      IS
         SELECT DISTINCT terminalid termid
           FROM FS_FR_TERMINALMASTER;

   BEGIN
      IF (    LENGTH (p_chargetype) > 0
          AND (p_chargetype = 'Both' OR p_chargetype = 'BOTH')
         )
      THEN
         v_charge_type := v_charge_type || ' ''Pickup'',''Delivery'' ';
      ELSE
         v_charge_type := v_charge_type || '''' || p_chargetype || '''';
      END IF;

      /*IF (LENGTH (p_operation) > 0 AND (p_operation = 'sellAdd'))
      THEN
         v_dyna_qry :=
               v_dyna_qry
            || ' AND (DTL.CHARGE_TYPE,DTL.ZONE_CODE,MAS.LOCATION_ID ) NOT IN ';
      ELSIF (    LENGTH (p_operation) > 0
             AND (   p_operation = 'sellModify'
                  OR p_operation = 'sellView'
                  OR p_operation = 'buyModify'
                  OR p_operation = 'buyView'
                 )
            )
      THEN
         v_dyna_qry :=
               v_dyna_qry
            || ' AND (DTL.CHARGE_TYPE,DTL.ZONE_CODE,MAS.LOCATION_ID ) IN';
      END IF;*/

      /*v_dyna_qry :=
            v_dyna_qry
         || '(SELECT SUBDTL.CHARGE_TYPE,SUBDTL.ZONE_CODE,SUBMAS.LOCATION_ID '
         || 'FROM QMS_CARTAGE_BUYDTL SUBDTL,QMS_CARTAGE_BUYSELLCHARGES SUBMAS '
         || ' WHERE SUBDTL.CARTAGE_ID=DTL.CARTAGE_ID AND SUBMAS.CARTAGE_ID = SUBDTL.CARTAGE_ID '
         || 'AND ';*/

      IF (    LENGTH (p_operation) > 0
          AND (p_operation = 'buyModify' OR p_operation = 'buyView')
         )
      THEN
         v_dyna_qry :='';
      ELSIF (LENGTH (p_operation) > 0 AND p_operation = 'sellAdd') THEN
         v_dyna_qry := v_dyna_qry || ' AND DTL.SELLCHARGE_FLAG !=''Y''';
      ELSE
         v_dyna_qry := v_dyna_qry || ' AND DTL.SELLCHARGE_FLAG =''Y''';
      END IF;

      /*v_dyna_qry :=
            v_dyna_qry
         || 'AND SUBMAS.LOCATION_ID=MAS.LOCATION_ID '
         || 'AND SUBDTL.CHARGE_TYPE=DTL.CHARGE_TYPE AND SUBDTL.ZONE_CODE=DTL.ZONE_CODE)';*/

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_terminalid;

      IF UPPER (v_opr_adm_flag) <> 'H'
      THEN
         IF p_operation = 'buyModify'
         THEN
            FOR i IN c3
            LOOP
               v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
            END LOOP;
         END IF;

         IF (p_operation = 'sellAdd' OR p_operation = 'sellModify') THEN
         FOR i IN c1
            LOOP
               v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
            END LOOP;
         END IF;

        --DBMS_OUTPUT.put_line ('v_terminals '||v_terminals);

         IF p_operation = 'sellView' OR p_operation = 'buyView'
         THEN
            FOR i IN c2
            LOOP
               v_terminals := v_terminals || '''' || i.termid || '''' || ',';
            END LOOP;
         END IF;
      ELSE
         IF (p_operation = 'sellAdd' OR p_operation = 'sellModify') THEN
           FOR i IN c1
              LOOP
                 v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
              END LOOP;
          ELSE
           FOR i IN c2
           LOOP
              v_terminals := v_terminals || '''' || i.termid || '''' || ',';
           END LOOP;
          END IF;
      END IF;

      IF (LENGTH (v_terminals) > 0)
      THEN
         v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
         v_dyna_qry :=
                v_dyna_qry || ' AND TERMINALID IN ( ' || v_terminals || ' ) ';
      END IF;



      OPEN p_dtls_data
       FOR    ' SELECT  MAS.CARTAGE_ID,MAS.MAX_CHARGEPER_TRUCKLOAD,DTL.ZONE_CODE,DTL.CHARGESLAB,MAS.CURRENCY CURRENCY, '
           || ' DTL.CHARGERATE,DTL.CHARGE_TYPE,MAS.UOM,DTL.EFFECTIVE_FROM,DTL.VALID_UPTO,DTL.CHARGERATE_INDICATOR,DTL.LOWERBOUND,DTL.UPPERBOUND,DENSITY_CODE,LINE_NO,DTL.CHARGE_BASIS  '
           || ' FROM	QMS_CARTAGE_BUYDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS  '
           || ' WHERE	MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.WEIGHT_BREAK = '
           || ''''
           || p_weightbreak
           || ''''
           || ' AND MAS.RATE_TYPE='
           || ''''
           || p_rate_type
           || ''''
           || ' AND MAS.LOCATION_ID='
           || ''''
           || p_location_id
           || ''''
           || ' AND ACTIVEINACTIVE = ''A''  AND DTL.ZONE_CODE IN ( '
           || Qms_Rsr_Rates_Pkg.seperator (p_zonecode)
           || ' ) '
           || 'AND MAS.SHIPMENT_MODE = '
           || ''''
           || p_shipment_mode
           || ''''
           /*|| 'AND MAS.CHARGE_BASIS ='
           || ''''
           || p_chargebasis
           || ''''
           || ''*/
           || ' AND DTL.CHARGE_TYPE IN('
           || v_charge_type
           || ')  '
           || v_dyna_qry
           || ' ORDER BY ZONE_CODE,DTL.CARTAGE_ID,CHARGE_TYPE,LINE_NO';

      IF (p_weightbreak = 'Slab')
      THEN
         v_dyna_qry := v_dyna_qry || ' ORDER BY TO_NUMBER(DTL.CHARGESLAB)';
      ELSIF (p_weightbreak = 'List')
      THEN
        /* v_dyna_qry := v_dyna_qry || ' ORDER BY LINE_NO';*/
         v_dyna_qry := v_dyna_qry || ' ORDER BY DTL.CHARGESLAB';--@@Modified by Kameswari for the WPBN issue-
      END IF;

      OPEN p_wtbrks_data
       FOR    ' SELECT  DISTINCT  DTL.CHARGESLAB ' --,DTL.LINE_NO
           || ' FROM	QMS_CARTAGE_BUYDTL DTL,QMS_CARTAGE_BUYSELLCHARGES MAS  '--@@Modified by Kameswari for the WPBN issue-
           || ' WHERE	MAS.CARTAGE_ID=DTL.CARTAGE_ID AND MAS.WEIGHT_BREAK = '
           || ''''
           || p_weightbreak
           || ''''
           || ' AND MAS.RATE_TYPE='
           || ''''
           || p_rate_type
           || ''''
           /*|| ' AND DTL.CHARGESLAB NOT IN (''MIN'',''MAX'') AND MAS.LOCATION_ID='*/ --@@ Commented by subrahmanyam for the Enhancement 170759
           || ' AND DTL.CHARGESLAB NOT IN (''BASE'',''MIN'',''MAX'') AND MAS.LOCATION_ID=' --@@ Added by subrahmanyam for the Enhancement 170759
           || ''''
           || p_location_id
           || ''''
           || ' AND ACTIVEINACTIVE = ''A'' AND DTL.ZONE_CODE IN ( '
           || Qms_Rsr_Rates_Pkg.seperator (p_zonecode)
           || ' ) '
           /*|| ' AND MAS.CHARGE_BASIS ='
           || ''''
           || p_chargebasis
           || ''''*/
           || 'AND MAS.SHIPMENT_MODE = '
           || ''''
           || p_shipment_mode
           || ''''
           || ' AND DTL.CHARGE_TYPE IN('
           || v_charge_type
           || ')  '
           || v_dyna_qry
           || '   ';

           --print_out(v_dyna_qry);
           --DBMS_OUTPUT.put_line (v_dyna_qry);
   END cartage_buycharges;


   PROCEDURE cartage_check_for_sellcharges (
      p_operation           VARCHAR2,
      p_terminalid          VARCHAR2,
      p_chargetype          VARCHAR2,
      p_location_id         VARCHAR2,
      p_zone                VARCHAR2,
      p_dtls_data     OUT   VARCHAR2
   )
   AS
      v_terminal       VARCHAR2 (300);
      v_charge_type    VARCHAR2 (300);
      v_main_qry       VARCHAR2 (3000);
      v_qry            VARCHAR2 (3000);
      v_dyna_qry       VARCHAR2 (32767);
      v_terminals      VARCHAR2 (32767);
      v_opr_adm_flag   VARCHAR2 (2);
      noofrows         NUMBER          := 0;

      CURSOR c1
      IS
         (SELECT     parent_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id
          START WITH child_terminal_id = p_terminalid
          UNION
          SELECT terminalid term_id
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminalid term_id
            FROM DUAL);

      CURSOR c2
      IS
         SELECT     child_terminal_id term_id
               FROM FS_FR_TERMINAL_REGN
         CONNECT BY PRIOR child_terminal_id = parent_terminal_id
         START WITH parent_terminal_id = p_terminalid  UNION
          SELECT p_terminalid term_id
            FROM DUAL;

      CURSOR c3
      IS
         SELECT DISTINCT terminalid term_id
                    FROM FS_FR_TERMINALMASTER;
   BEGIN
      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_terminalid;

      IF UPPER (v_opr_adm_flag) <> 'H'
      THEN
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c3
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      SELECT COUNT(*)
        INTO Noofrows
        FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
       WHERE Mas.Cartage_Id = Dtl.Cartage_Id
         AND Mas.Location_Id = p_Location_Id
         AND Charge_Type = v_Charge_Type
         AND Dtl.Zone_Code = p_Zone
         AND Terminalid IN (v_Terminals);

      IF (noofrows = 0)
      THEN
         FOR i IN c3
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;

         IF UPPER (v_opr_adm_flag) <> 'H'
         THEN
            SELECT COUNT(*)
              INTO Noofrows
              FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
             WHERE Mas.Cartage_Id = Dtl.Cartage_Id
               AND Mas.Location_Id = p_Location_Id
               AND Charge_Type = v_Charge_Type
               AND Dtl.Zone_Code = p_Zone
               AND Terminalid IN (v_Terminals);

            IF (noofrows > 0)
            THEN
               p_dtls_data := '0';
            ELSE
               p_dtls_data := '1';
            END IF;
         ELSE
            p_dtls_data := '2';
         END IF;
      ELSE
         IF UPPER (v_opr_adm_flag) <> 'H'
         THEN
            p_dtls_data := '3';
         ELSE
            p_dtls_data := '4';
         END IF;
      END IF;
   END cartage_check_for_sellcharges;

   PROCEDURE cartage_check_for_buycharges (
      p_operation           VARCHAR2,
      p_terminalid          VARCHAR2,
      p_chargetype          VARCHAR2,
      p_location_id         VARCHAR2,
      p_zone                VARCHAR2,
      p_dtls_data     OUT   VARCHAR2
   )
   AS
      v_terminal       VARCHAR2 (300);
      v_charge_type    VARCHAR2 (300);
      v_main_qry       VARCHAR2 (3000);
      v_qry            VARCHAR2 (3000);
      v_dyna_qry       VARCHAR2 (32767);
      v_terminals      VARCHAR2 (32767);
      v_opr_adm_flag   VARCHAR2 (2);
      noofrows         NUMBER          := 0;

      CURSOR c1
      IS
         (SELECT     parent_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id
          START WITH child_terminal_id = p_terminalid
          UNION
          SELECT terminalid term_id
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminalid term_id
            FROM DUAL);

      CURSOR c2
      IS
         SELECT Child_Terminal_Id Term_Id
           FROM FS_FR_TERMINAL_REGN
         CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
          START WITH Parent_Terminal_Id = p_Terminalid
         UNION
         SELECT p_Terminalid Term_Id FROM Dual;

      CURSOR c3
      IS
         SELECT DISTINCT Terminalid Term_Id FROM FS_FR_TERMINALMASTER;
   BEGIN
      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_terminalid;

      IF UPPER (v_opr_adm_flag) <> 'H'
      THEN
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c3
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      DBMS_OUTPUT.PUT_LINE(' v_charge_type'||v_charge_type);

      EXECUTE IMMEDIATE('SELECT COUNT (*)
        FROM QMS_CARTAGE_BUYDTL dtl, QMS_CARTAGE_BUYSELLCHARGES mas
       WHERE mas.cartage_id = dtl.cartage_id
         AND mas.location_id ='
         ||''''
         || p_location_id
         ||''' AND charge_type = '
         ||''''
         || p_chargetype
         ||''' AND dtl.zone_code ='
         ||''''
         ||p_zone
         ||''' and activeinactive=''A'' AND terminalid IN ('
         ||v_terminals
         ||
         ')')  INTO noofrows;

        DBMS_OUTPUT.PUT_LINE(' noofrows'||noofrows);

      IF (noofrows = 0)
      THEN
         FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;

         IF UPPER (v_opr_adm_flag) <> 'H'
         THEN
            SELECT COUNT(*)
              INTO Noofrows
              FROM QMS_CARTAGE_BUYDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
             WHERE Mas.Cartage_Id = Dtl.Cartage_Id
               AND Mas.Location_Id = p_Location_Id
               AND Charge_Type = v_Charge_Type
               AND Dtl.Zone_Code = p_Zone
               AND Terminalid IN (v_Terminals);

            IF (noofrows > 0)
            THEN
               p_dtls_data := '0';
            ELSE
               p_dtls_data := '1';
            END IF;
         ELSE
            p_dtls_data := '2';
         END IF;
      ELSE
         IF UPPER (v_opr_adm_flag) <> 'H'
         THEN
            p_dtls_data := '3';
         ELSE
            p_dtls_data := '4';
         END IF;
      END IF;
   END cartage_check_for_buycharges;

   PROCEDURE Buy_Cartage_Update_Quote(p_Terminalid      VARCHAR2,
                                      p_Chargetype      VARCHAR2,
                                      p_Location_Id     VARCHAR2,
                                      p_Zone            VARCHAR2,
                                      p_Shipment_Mode   VARCHAR2,
                                      p_Console_Type    VARCHAR2,
                                      p_Newbuycartageid VARCHAR2,
                                      p_Excep           OUT VARCHAR2) AS
     p_Rs           Sys_Refcursor;
     g_Err          VARCHAR2(1000) := '';
     g_Err_Code     VARCHAR2(100) := '';
     v_Oldcartageid VARCHAR2(20) := '';
     v_Charge_Type  VARCHAR2(20) := '';
   BEGIN
     BEGIN
       IF p_Shipment_Mode = '1' THEN
         v_Charge_Type := 'AFR@' || p_Chargetype;
       ELSIF p_Shipment_Mode = '2' THEN
         v_Charge_Type := UPPER(p_Console_Type) || '@' || p_Chargetype;
       END IF;
       OPEN p_Rs FOR
         SELECT DISTINCT Dtl.Cartage_Id
           FROM QMS_CARTAGE_BUYDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
          WHERE Mas.Cartage_Id = Dtl.Cartage_Id
            AND Zone_Code = p_Zone
            AND Charge_Type = p_Chargetype
            AND Location_Id = p_Location_Id
            AND Shipment_Mode = p_Shipment_Mode
            AND NVL(Console_Type, '~') = NVL(p_Console_Type, '~')
            AND Activeinactive = 'A';
       LOOP
         FETCH p_Rs
           INTO v_Oldcartageid;
         EXIT WHEN p_Rs%NOTFOUND;
         Dbms_Output.Put_Line(' INSIDE PROCEDURE::' || v_Oldcartageid);
         Qms_Quotepack_New.Qms_Quote_Update(NULL,
                                            v_Oldcartageid,
                                            NULL,
                                            NULL,
                                            NULL,--added by subrahmanyam for cartage add
                                            NULL,--added by subrahmanyam for cartage add
                                            p_Newbuycartageid,
                                            NULL,
                                            'BC',
                                            v_Charge_Type,
                                            p_Zone,
                                            p_Location_Id);
       END LOOP;
       CLOSE p_Rs;
       UPDATE QMS_CARTAGE_BUYDTL
          SET Activeinactive = 'I'
        WHERE (Cartage_Id, Zone_Code, Charge_Type) IN
              (SELECT Dtl.Cartage_Id, Dtl.Zone_Code, Dtl.Charge_Type
                 FROM QMS_CARTAGE_BUYDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
                WHERE Mas.Cartage_Id = Dtl.Cartage_Id
                  AND Zone_Code = p_Zone
                  AND Charge_Type = p_Chargetype
                  AND Location_Id = p_Location_Id
                  AND Shipment_Mode = p_Shipment_Mode
                  AND NVL(Console_Type, '~') = NVL(p_Console_Type, '~')
                  AND Activeinactive = 'A');
       p_Excep := 1;
     END;
   END Buy_Cartage_Update_Quote;

PROCEDURE Sell_Cartage_Update_Quote(p_Terminalid       VARCHAR2,
                                    p_Chargetype       VARCHAR2,
                                    p_Location_Id      VARCHAR2,
                                    p_Zone             VARCHAR2,
                                    p_Shipment_Mode    VARCHAR2,
                                    p_Console_Type     VARCHAR2,
                                    p_Newsellcartageid VARCHAR2,
                                    p_Newbuycartageid  VARCHAR2,
                                    p_Excep            OUT VARCHAR2) AS
  p_Rs               Sys_Refcursor;
  v_Oldbuycartageid  VARCHAR2(20) := '';
  v_Oldsellcartageid VARCHAR2(20) := '';
  g_Err              VARCHAR2(1000) := '';
  g_Err_Code         VARCHAR2(100) := '';
  v_Charge_Type      VARCHAR2(20) := '';
BEGIN
  IF p_Shipment_Mode = '1' THEN
    v_Charge_Type := 'AFR@' || p_Chargetype;
  ELSIF p_Shipment_Mode = '2' THEN
    v_Charge_Type := UPPER(p_Console_Type) || '@' || p_Chargetype;
  END IF;
  --BEGIN
  OPEN p_Rs FOR
    SELECT DISTINCT Dtl.Cartage_Id, Dtl.Sell_Cartage_Id
      FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
     WHERE Mas.Cartage_Id = Dtl.Cartage_Id
       AND Zone_Code = p_Zone
       AND Charge_Type = p_Chargetype
       AND Location_Id = p_Location_Id
       AND Mas.Shipment_Mode = p_Shipment_Mode
       AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
       AND Id =
           (SELECT MAX(Id)
              FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
             WHERE Mas.Cartage_Id = Dtl.Cartage_Id
               AND Zone_Code = p_Zone
               AND Charge_Type = p_Chargetype
               AND Location_Id = p_Location_Id
               AND Mas.Shipment_Mode = p_Shipment_Mode
               AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~'));
  LOOP
    FETCH p_Rs
      INTO v_Oldbuycartageid, v_Oldsellcartageid;
    EXIT WHEN p_Rs%NOTFOUND;
    Qms_Quotepack_New.Qms_Quote_Update(v_Oldsellcartageid,
                                       v_Oldbuycartageid,
                                       NULL,
                                       NULL,--Added by subrahmanyam for cartage add
                                       NULL,--Added by subrahmanyam for cartage add
                                       p_Newsellcartageid,
                                       p_Newbuycartageid,
                                       NULL,
                                       'SC',
                                       v_Charge_Type,
                                       p_Zone,
                                       p_Location_Id);
  END LOOP;
  CLOSE p_Rs;
  UPDATE QMS_CARTAGE_SELLDTL
     SET Activeinactive = 'I'
   WHERE (Cartage_Id, Zone_Code, Charge_Type) IN
         (SELECT Dtl.Cartage_Id, Dtl.Zone_Code, Dtl.Charge_Type
            FROM QMS_CARTAGE_SELLDTL Dtl, QMS_CARTAGE_BUYSELLCHARGES Mas
           WHERE Mas.Cartage_Id = Dtl.Cartage_Id
             AND Zone_Code = p_Zone
             AND Charge_Type = p_Chargetype
             AND Location_Id = p_Location_Id
             AND Mas.Shipment_Mode = p_Shipment_Mode
             AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~')
             AND Activeinactive = 'A');
  p_Excep := 1;
END Sell_Cartage_Update_Quote;

END;

/

/

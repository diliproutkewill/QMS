--------------------------------------------------------
--  DDL for Package Body PKG_QMS_BUYRATES
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "PKG_QMS_BUYRATES" AS
  /*  Using this function for Add,modify and upload.Validates the data entered
     *  in buy rates upload module against the data base and have
     *  comb_freq_func();
  Va *  lidate_Location();
  Va *  lidate_Carrier();
  Va *  lidate_Servicelevel();
  Va *  lidate_Buyrate(); those functions.
    */
  PROCEDURE Qms_Validate_Buyrate(p_shipment_mode       VARCHAR2,
                                 p_origin              VARCHAR2,
                                 p_destination         VARCHAR2,
                                 p_servicelevel_id     VARCHAR2,
                                 p_carrier_id          VARCHAR2,
                                 p_effective_from      DATE,
                                 p_validate_upto       DATE,
                                 p_weight_break        VARCHAR2,
                                 p_rate_type           VARCHAR2,
                                 p_current_terminal_id VARCHAR2,
                                 p_frequency           VARCHAR2,
                                 p_density             VARCHAR2,
                                 p_uom                 VARCHAR2)
  --RETURN NUMBER
   AS
    v_accesslevel       VARCHAR2(10);
    str                 VARCHAR2(10);
    p_buyrate_id        VARCHAR2(50);
    p_laneno            VARCHAR2(50);
    p_shipment_mode_str VARCHAR2(100);
    --v_freq                VARCHAR2 (1000);
    v_density VARCHAR2(50);
    v_flag    VARCHAR2(1);
  BEGIN

    --Execute Immediate('Truncate Table Temp_Charges');
    DBMS_APPLICATION_INFO.SET_MODULE('Qms_Validate_Buyrate', 'AT START');

    --v_freq := Pkg_Qms_Buyrates.comb_freq_func (p_frequency);

    IF p_shipment_mode = '1' THEN
      p_shipment_mode_str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_shipment_mode = '2' THEN
      p_shipment_mode_str := '(''2'',''3'',''6'',''7'')';
    ELSIF p_shipment_mode = '4' THEN
      p_shipment_mode_str := '(''4'',''5'',''6'',''7'')';
    ELSE
      p_shipment_mode_str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;

    str := Validate_Location(p_location_id => p_origin,
                             --p_shipment_mode_str      => p_shipment_mode_str,
                             p_shipment_mode => p_shipment_mode);

    DBMS_APPLICATION_INFO.SET_ACTION('Origin Validate_Location');

    IF str = 'FALSE' THEN
      INSERT INTO TEMP_CHARGES (Notes) VALUES ('1');
      v_flag := 'T';
      --RETURN '1';
    END IF;

    IF v_flag IS NULL THEN
      str := Validate_Location(p_location_id => p_destination,
                               --p_shipment_mode_str      => p_shipment_mode_str,
                               p_shipment_mode => p_shipment_mode);
      DBMS_APPLICATION_INFO.SET_ACTION('Dest Validate_Location');

      IF str = 'FALSE' THEN
        INSERT INTO TEMP_CHARGES (Notes) VALUES ('2');
        v_flag := 'T';
        --RETURN '2';
      END IF;
    END IF;

    IF v_flag IS NULL THEN
      str := Validate_Carrier(p_terminal_id   => p_current_terminal_id,
                              p_shipment_mode => p_shipment_mode,
                              p_carrier_id    => p_carrier_id);
      DBMS_APPLICATION_INFO.SET_ACTION('Validate_Carrier');

      IF str = 'FALSE' THEN
        INSERT INTO TEMP_CHARGES (Notes) VALUES ('3');
        v_flag := 'T';
        --RETURN '3';
      END IF;
    END IF;

    IF v_flag IS NULL THEN
      str := Validate_Servicelevel(p_terminal_id => p_current_terminal_id,
                                   --p_shipment_mode_str      => p_shipment_mode_str,
                                   p_shipment_mode   => p_shipment_mode,
                                   p_servicelevel_id => p_servicelevel_id);
      DBMS_APPLICATION_INFO.SET_ACTION('Validate_Servicelevel');

      IF str = 'FALSE' THEN
        INSERT INTO TEMP_CHARGES (Notes) VALUES ('4');
        v_flag := 'T';
        --RETURN '4';
      END IF;
    END IF;
    IF v_flag IS NULL THEN
      str := Pkg_Qms_Buyrates.Validate_Buyrate(p_shipment_mode       => p_shipment_mode,
                                               p_origin              => p_origin,
                                               p_destination         => p_destination,
                                               p_service_level_id    => p_servicelevel_id,
                                               p_carrier_id          => p_carrier_id,
                                               p_effective_from      => p_effective_from,
                                               p_validate_upto       => p_validate_upto,
                                               p_weight_break        => p_weight_break,
                                               p_rate_type           => p_rate_type,
                                               p_current_terminal_id => p_current_terminal_id,
                                               --p_frequency                => v_freq
                                               p_frequency => p_frequency);
      DBMS_APPLICATION_INFO.SET_ACTION('Validate_Buyrate');
      IF str = 'TRUE' THEN
        INSERT INTO TEMP_CHARGES (Notes) VALUES ('5');
        v_flag := 'T';
        --RETURN '5';
      END IF;
    END IF;
    --validated by ramakrishna
    IF v_flag IS NULL THEN
      IF (LENGTH(p_density) > 0) THEN
        v_density := validate_density_ratio(p_density,
                                            p_shipment_mode,
                                            p_uom);
        DBMS_APPLICATION_INFO.SET_ACTION('validate_density_ratio');
        IF v_density = 0 THEN
          INSERT INTO TEMP_CHARGES (Notes) VALUES ('7');
          v_flag := 'T';
          --RETURN '7';
        END IF;
      END IF;
    END IF;

    IF v_flag IS NULL THEN
      INSERT INTO TEMP_CHARGES (Notes) VALUES ('6');
    END IF;
    DBMS_APPLICATION_INFO.SET_MODULE(NULL, NULL);
    --RETURN '6';
    /*EXCEPTION
    WHEN OTHERS
    THEN
    Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
    Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

    INSERT INTO QMS_OBJECTS_ERRORS
      (ex_date, module_name, errorcode, errormessage)
    VALUES
      (SYSDATE,
       'Pkg_Qms_Buyrates->Qms_Validate_Buyrate',
       Pkg_Qms_Buyrates.g_err_code,
       Pkg_Qms_Buyrates.g_err);

    COMMIT;
       DBMS_OUTPUT.put_line (SQLERRM);
       RETURN '0';*/
  END;

  /*Added by Govind for validation purpose*/

   /*  Validates the hierarchy and unique combinations of the buy rates against the data
   *  base while uploading the data.
   *  It is passing the in parameters
   *  p_shipment_mode         VARCHAR2,
   *  p_origin                VARCHAR2,
   *  p_destination           VARCHAR2,
   *  p_service_level_id      VARCHAR2,
   *  p_carrier_id            VARCHAR2,
   *  p_effective_from        DATE,
   *  p_validate_upto         DATE,
   *  p_weight_break          VARCHAR2,
   *  p_rate_type             VARCHAR2,
   *  p_current_terminal_id   VARCHAR2,
   *  p_frequency             VARCHAR2
   *  out parameter is
   *  RETURN                  VARCHAR2.
  */
  FUNCTION Validate_Buyrate_new(p_Shipment_Mode       VARCHAR2,
                            p_Origin              VARCHAR2,
                            p_Destination         VARCHAR2,
                            p_Service_Level_Id    VARCHAR2,
                            p_Carrier_Id          VARCHAR2,
                            p_Effective_From      DATE,
                            p_Validate_Upto       DATE,
                            p_Weight_Break        VARCHAR2,
                            p_Rate_Type           VARCHAR2,
                            p_Current_Terminal_Id VARCHAR2,
                            p_Frequency           VARCHAR2,
                            p_density             VARCHAR2,
                            p_currency            VARCHAR2) RETURN VARCHAR2 IS
    TYPE v_Rc IS REF CURSOR;
    p_Buyrate_Id   VARCHAR2(50);
    p_Laneno       VARCHAR2(50);
    v_Sql          VARCHAR2(32600);
    v_Rc_C1        v_Rc;
    v_Srvclvl      VARCHAR2(8);
    v_Carrier_Id   VARCHAR2(5);
    v_Shpment_Mode VARCHAR2(1);
    v_Weight_Break VARCHAR2(16);
    v_Rate_Type    VARCHAR2(16);
    v_Origin       VARCHAR2(16);
    v_Destination  VARCHAR2(16);
    v_Terminal_Id  VARCHAR2(10);
  BEGIN
    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
    /*OPEN v_rc_c1 FOR(' SELECT DISTINCT a.buyrateid buyrateid, b.lane_no lane_no FROM qms_buyrates_master a, qms_buyrates_dtl b WHERE b.buyrateid = a.buyrateid '
    || ' AND b.service_level =:v_srvclvl'
    || ' AND b.carrier_id =:v_carrier_id'
    || ' AND a.shipment_mode =:v_shpment_mode'
    || ' AND a.weight_break =:v_weight_break'
    || ' AND a.rate_type =:v_rate_type'
    || ' AND b.activeinactive IS NULL AND (b.INVALIDATE = ''F'' OR b.INVALIDATE IS NULL) AND b.origin =:v_origin AND b.destination =:v_destination'
    ||' and '
    || p_frequency
    || ' AND a.terminalid IN (SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id =:v_terminal_id'
    ||' UNION SELECT terminalid FROM fs_fr_terminalmaster WHERE oper_admin_flag = ''H'' AND terminalid <>:v_terminal_id '
    || ')') Using p_service_level_id,p_carrier_id,p_shipment_mode,p_weight_break,p_rate_type,p_origin,p_destination,p_current_terminal_id,p_current_terminal_id;*/
    IF p_Shipment_Mode = '2' THEN
      OPEN v_Rc_C1 FOR
        SELECT DISTINCT a.Buyrateid Buyrateid, b.Lane_No Lane_No
          FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
         WHERE b.Buyrateid = a.Buyrateid
         AND b.version_no=a.version_no --@@Added by Kameswari for the WPBN issue-160743
           AND (a.lane_no=b.lane_no OR a.lane_no IS NULL)
           AND b.Service_Level = p_Service_Level_Id
           AND b.Carrier_Id = p_Carrier_Id
           AND a.Shipment_Mode = p_Shipment_Mode
           AND a.Weight_Break = p_Weight_Break
           AND a.Rate_Type = p_Rate_Type
           AND b.Activeinactive IS NULL
           AND (b.Invalidate = 'F' OR b.Invalidate IS NULL)
           AND b.Origin = p_Origin
           AND b.Destination = p_Destination
           AND b.density_code = p_density
           AND a.currency     = p_currency
           AND b.Frequency = p_Frequency
           AND a.Terminalid IN
               (SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Current_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H'
                   AND Terminalid <> p_Current_Terminal_Id);
    ELSE
      OPEN v_Rc_C1 FOR 'Select Distinct /*+ FIRST_ROWS */ a.Buyrateid Buyrateid,
                         b.Lane_No   Lane_No
           FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
          WHERE b.Buyrateid = a.Buyrateid
              AND b.version_no=a.version_no--@@Added by Kameswari for the WPBN issue-160743
            AND (a.lane_no=b.lane_no OR a.lane_no IS NULL)
            AND b.Service_Level = :p_Service_Level_Id
            AND b.Carrier_Id = :p_Carrier_Id
            AND a.Shipment_Mode = :p_Shipment_Mode
            AND a.Weight_Break = :p_Weight_Break
            AND a.Rate_Type = :p_Rate_Type
            AND b.Activeinactive IS NULL
            AND (b.Invalidate = ''F'' OR b.Invalidate IS NULL)
            AND b.Origin = :p_Origin
            AND b.Destination = :p_Destination
            AND b.density_code = :p_density
            AND a.currency     = :p_currency
             AND EXISTS
          (SELECT NULL
                   FROM QMS_BUYRATES_FREQ
                  WHERE Buyrateid = b.Buyrateid
                    AND Lane_No = b.Lane_No
                    AND Frequency IN (' || Qms_Rsr_Rates_Pkg.Seperator(p_Frequency) || ')) AND a.Terminalid IN
                (SELECT Parent_Terminal_Id Terminalid
                   FROM FS_FR_TERMINAL_REGN
                 CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                  START WITH Child_Terminal_Id = :p_Current_Terminal_Id
                 UNION
                 SELECT Terminalid
                   FROM FS_FR_TERMINALMASTER
                  WHERE Oper_Admin_Flag = ''H''
                    AND Terminalid <> :p_Current_Terminal_Id)'
        USING p_Service_Level_Id, p_Carrier_Id, p_Shipment_Mode, p_Weight_Break, p_Rate_Type, p_Origin, p_Destination, p_density, p_currency, p_Current_Terminal_Id, p_Current_Terminal_Id;

      /*(Select Column_Value
       From Table(Inlist(p_Frequency))
      Where Rownum >= 0)*/
    END IF;
    LOOP
      BEGIN
        FETCH v_Rc_C1
          INTO p_Buyrate_Id, p_Laneno;
        EXIT WHEN v_Rc_C1%NOTFOUND;
        RETURN 'TRUE';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          RETURN 'FALSE';
      END;
    END LOOP;
    CLOSE v_Rc_C1;
    RETURN 'FALSE';
    /*IF SQL%NOTFOUND THEN
     RETURN 'FALSE';
    ELSE
     RETURN 'TRUE';
    END IF;*/
    /*EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
      Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

      INSERT INTO QMS_OBJECTS_ERRORS
        (ex_date, module_name, errorcode, errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Buyrate',
         Pkg_Qms_Buyrates.g_err_code,
         Pkg_Qms_Buyrates.g_err);

      COMMIT;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
       RETURN '0';
    WHEN OTHERS
    THEN
      Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

      INSERT INTO QMS_OBJECTS_ERRORS
        (ex_date, module_name, errorcode, errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Buyrate',
         Pkg_Qms_Buyrates.g_err_code,
         Pkg_Qms_Buyrates.g_err);

      COMMIT;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
       RETURN 'FALSE';*/
  END;



  /*Eds here*/


  /*  Validates the hierarchy and unique combinations of the buy rates against the data
   *  base while uploading the data.
   *  It is passing the in parameters
   *  p_shipment_mode         VARCHAR2,
   *  p_origin                VARCHAR2,
   *  p_destination           VARCHAR2,
   *  p_service_level_id      VARCHAR2,
   *  p_carrier_id            VARCHAR2,
   *  p_effective_from        DATE,
   *  p_validate_upto         DATE,
   *  p_weight_break          VARCHAR2,
   *  p_rate_type             VARCHAR2,
   *  p_current_terminal_id   VARCHAR2,
   *  p_frequency             VARCHAR2
   *  out parameter is
   *  RETURN                  VARCHAR2.
  */
  FUNCTION Validate_Buyrate(p_Shipment_Mode       VARCHAR2,
                            p_Origin              VARCHAR2,
                            p_Destination         VARCHAR2,
                            p_Service_Level_Id    VARCHAR2,
                            p_Carrier_Id          VARCHAR2,
                            p_Effective_From      DATE,
                            p_Validate_Upto       DATE,
                            p_Weight_Break        VARCHAR2,
                            p_Rate_Type           VARCHAR2,
                            p_Current_Terminal_Id VARCHAR2,
                            p_Frequency           VARCHAR2) RETURN VARCHAR2 IS
    TYPE v_Rc IS REF CURSOR;
    p_Buyrate_Id   VARCHAR2(50);
    p_Laneno       VARCHAR2(50);
    v_Sql          VARCHAR2(32600);
    v_Rc_C1        v_Rc;
    v_Srvclvl      VARCHAR2(8);
    v_Carrier_Id   VARCHAR2(5);
    v_Shpment_Mode VARCHAR2(1);
    v_Weight_Break VARCHAR2(16);
    v_Rate_Type    VARCHAR2(16);
    v_Origin       VARCHAR2(16);
    v_Destination  VARCHAR2(16);
    v_Terminal_Id  VARCHAR2(10);
  BEGIN
    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
    /*OPEN v_rc_c1 FOR(' SELECT DISTINCT a.buyrateid buyrateid, b.lane_no lane_no FROM qms_buyrates_master a, qms_buyrates_dtl b WHERE b.buyrateid = a.buyrateid '
    || ' AND b.service_level =:v_srvclvl'
    || ' AND b.carrier_id =:v_carrier_id'
    || ' AND a.shipment_mode =:v_shpment_mode'
    || ' AND a.weight_break =:v_weight_break'
    || ' AND a.rate_type =:v_rate_type'
    || ' AND b.activeinactive IS NULL AND (b.INVALIDATE = ''F'' OR b.INVALIDATE IS NULL) AND b.origin =:v_origin AND b.destination =:v_destination'
    ||' and '
    || p_frequency
    || ' AND a.terminalid IN (SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id =:v_terminal_id'
    ||' UNION SELECT terminalid FROM fs_fr_terminalmaster WHERE oper_admin_flag = ''H'' AND terminalid <>:v_terminal_id '
    || ')') Using p_service_level_id,p_carrier_id,p_shipment_mode,p_weight_break,p_rate_type,p_origin,p_destination,p_current_terminal_id,p_current_terminal_id;*/
    IF p_Shipment_Mode = '2' THEN
      OPEN v_Rc_C1 FOR
        SELECT  a.Buyrateid Buyrateid, b.Lane_No Lane_No
          FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
         WHERE b.Buyrateid = a.Buyrateid
         AND b.version_no=a.version_no --@@Added by Kameswari for the WPBN issue-160743
           AND (a.lane_no=b.lane_no OR a.lane_no IS NULL)
           AND b.Service_Level = p_Service_Level_Id
           AND b.Carrier_Id = p_Carrier_Id
           AND a.Shipment_Mode = p_Shipment_Mode
           AND a.Weight_Break = p_Weight_Break
           AND a.Rate_Type = p_Rate_Type
           AND b.Activeinactive IS NULL
           AND (b.Invalidate = 'F' OR b.Invalidate IS NULL)
           AND b.Origin = p_Origin
           AND b.Destination = p_Destination
           AND b.Frequency = p_Frequency
           and b.line_no='0'
           AND a.Terminalid IN
               (SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Current_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H'
                   AND Terminalid <> p_Current_Terminal_Id);
    ELSE
      OPEN v_Rc_C1 FOR 'Select  /*+ FIRST_ROWS */ a.Buyrateid Buyrateid,
                         b.Lane_No   Lane_No
           FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
          WHERE b.Buyrateid = a.Buyrateid
              AND b.version_no=a.version_no--@@Added by Kameswari for the WPBN issue-160743
            AND (a.lane_no=b.lane_no OR a.lane_no IS NULL)
            AND b.Service_Level = :p_Service_Level_Id
            AND b.Carrier_Id = :p_Carrier_Id
            AND a.Shipment_Mode = :p_Shipment_Mode
            AND a.Weight_Break = :p_Weight_Break
            AND a.Rate_Type = :p_Rate_Type
            AND b.Activeinactive IS NULL
            AND (b.Invalidate = ''F'' OR b.Invalidate IS NULL)
            AND b.Origin = :p_Origin
            AND b.Destination = :p_Destination
            and b.line_no=''0''
             AND EXISTS
          (SELECT NULL
                   FROM QMS_BUYRATES_FREQ
                  WHERE Buyrateid = b.Buyrateid
                    AND Lane_No = b.Lane_No
                    AND Frequency IN (' || Qms_Rsr_Rates_Pkg.Seperator(p_Frequency) || ')) AND a.Terminalid IN
                (SELECT Parent_Terminal_Id Terminalid
                   FROM FS_FR_TERMINAL_REGN
                 CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                  START WITH Child_Terminal_Id = :p_Current_Terminal_Id
                 UNION
                 SELECT Terminalid
                   FROM FS_FR_TERMINALMASTER
                  WHERE Oper_Admin_Flag = ''H''
                    AND Terminalid <> :p_Current_Terminal_Id)'
        USING p_Service_Level_Id, p_Carrier_Id, p_Shipment_Mode, p_Weight_Break, p_Rate_Type, p_Origin, p_Destination, p_Current_Terminal_Id, p_Current_Terminal_Id;

      /*(Select Column_Value
       From Table(Inlist(p_Frequency))
      Where Rownum >= 0)*/
    END IF;
    LOOP
      BEGIN
        FETCH v_Rc_C1
          INTO p_Buyrate_Id, p_Laneno;
        EXIT WHEN v_Rc_C1%NOTFOUND;
        RETURN 'TRUE';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          RETURN 'FALSE';
      END;
    END LOOP;
    CLOSE v_Rc_C1;
    RETURN 'FALSE';
    /*IF SQL%NOTFOUND THEN
     RETURN 'FALSE';
    ELSE
     RETURN 'TRUE';
    END IF;*/
    /*EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
      Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

      INSERT INTO QMS_OBJECTS_ERRORS
        (ex_date, module_name, errorcode, errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Buyrate',
         Pkg_Qms_Buyrates.g_err_code,
         Pkg_Qms_Buyrates.g_err);

      COMMIT;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
       RETURN '0';
    WHEN OTHERS
    THEN
      Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

      INSERT INTO QMS_OBJECTS_ERRORS
        (ex_date, module_name, errorcode, errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Buyrate',
         Pkg_Qms_Buyrates.g_err_code,
         Pkg_Qms_Buyrates.g_err);

      COMMIT;
      DBMS_OUTPUT.PUT_LINE(SQLERRM);
       RETURN 'FALSE';*/
  END;
  /*  Validates the carrier id against the data base in upload modules.
   *   It is passing the in parameters are
   *   p_terminal_id         VARCHAR2,
   *   p_shipment_mode_str   VARCHAR2,
   *   p_carrier_id          VARCHAR2,
   *   and out parameter is
   *   RETURN                VARCHAR2.
  */
  FUNCTION Validate_Carrier(p_Terminal_Id   VARCHAR2,
                            p_Shipment_Mode VARCHAR2,
                            p_Carrier_Id    VARCHAR2) RETURN VARCHAR2 IS
    v_Carrier_Id   VARCHAR2(5);
    v_Terminal_Id  VARCHAR2(10);
    v_Opr_Adm_Flag VARCHAR2(10);
    v_Result       VARCHAR2(10);
    /*str              VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN  '
          || p_shipment_mode_str
          || '  and  CARRIERID=:v_carrier_id'
          || '  AND ( INVALIDATE='
          || ''''
          || 'F'
          || ''''
          || '  or INVALIDATE is null)   AND TERMINALID IN (select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=:v_terminal_id'
          || '  union SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID =:v_terminal_id'
          || '   UNION SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=:v_terminal_id'
          || '   UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='
          || ''''
          || 'H'
          || ''''
          || ');'
          || 'END;';

    str0             VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN  '
          || p_shipment_mode_str
          || '  and  CARRIERID=:v_carrier_id'
          || '  AND ( INVALIDATE='
          || ''''
          || 'F'
          || ''''
          || ' or INVALIDATE is null) AND TERMINALID IN ('
          || ' SELECT Distinct CHILD_TERMINAL_ID terminalid  FROM FS_FR_TERMINAL_REGN '
          || ' union '
          || ' SELECT terminalid from FS_FR_TERMINALMASTER where OPER_ADMIN_FLAG ='
          || ''''
          || 'H'
          || ''''
          || ');'
          || 'END;';*/
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal_Id;
    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      IF p_Shipment_Mode = '1' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('1', '3', '5', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      ELSIF p_Shipment_Mode = '2' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('2', '3', '6', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      ELSIF p_Shipment_Mode = '4' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('4', '5', '6', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      ELSE
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      END IF;
      --EXECUTE IMMEDIATE (str0) Using p_carrier_id;
    ELSE
      IF p_Shipment_Mode = '1' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('1', '3', '5', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      ELSIF p_Shipment_Mode = '2' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('2', '3', '6', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      ELSIF p_Shipment_Mode = '4' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Shipmentmode IN ('4', '5', '6', '7')
           AND Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      ELSE
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_CAMASTER
         WHERE Carrierid = p_Carrier_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      END IF;
      --EXECUTE IMMEDIATE (str) Using p_carrier_id,p_terminal_id,p_terminal_id,p_terminal_id;
    END IF;
    IF SQL%FOUND THEN
      RETURN 'TRUE';
    ELSE
      RETURN 'FALSE';
    END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      /*Pkg_Qms_Buyrates.g_err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_err_code := '<< ' || SQLCODE || ' >>';

      INSERT INTO QMS_OBJECTS_ERRORS
        (ex_date, module_name, errorcode, errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Carrier',
         Pkg_Qms_Buyrates.g_err_code,
         Pkg_Qms_Buyrates.g_err);

      COMMIT;*/
      RETURN 'FALSE';
    WHEN OTHERS THEN
      Pkg_Qms_Buyrates.g_Err      := '<< ' || SQLERRM || ' >>';
      Pkg_Qms_Buyrates.g_Err_Code := '<< ' || SQLCODE || ' >>';
      INSERT INTO QMS_OBJECTS_ERRORS
        (Ex_Date, Module_Name, Errorcode, Errormessage)
      VALUES
        (SYSDATE,
         'Pkg_Qms_Buyrates->Validate_Carrier',
         Pkg_Qms_Buyrates.g_Err_Code,
         Pkg_Qms_Buyrates.g_Err);
      COMMIT;
      Dbms_Output.Put_Line(SQLERRM);
      RETURN 'FALSE';
  END;
  /*  Validates the both the carrier and currency entered in the first screen.
   *  It is passing the in parameters are
   *  p_terminal_id         VARCHAR2,
   *  p_shipment_mode_str   VARCHAR2,
   *  p_carrier_id          VARCHAR2,
   *  p_currency_id         VARCHAR2,
   *  out parameter is
   *  RETURN                VARCHAR2.
  */
  FUNCTION Validate_Currency_Carrier(p_terminal_id       VARCHAR2,
                                     p_shipment_mode_str VARCHAR2,
                                     p_carrier_id        VARCHAR2,
                                     p_currency_id       VARCHAR2)
    RETURN VARCHAR2 IS
    str VARCHAR2(4000) := 'DECLARE RESULT VARCHAR2(10);' || 'BEGIN   ' ||
                          'SELECT ''X''  INTO RESULT  FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN  ' ||
                          p_shipment_mode_str || '  and  CARRIERID=' || '''' ||
                          p_carrier_id || '''' ||
                          '  AND (INVALIDATE IS NULL OR INVALIDATE=' || '''' || 'F' || '''' ||
                          ' )   AND TERMINALID IN (select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=' || '''' ||
                          p_terminal_id || '''' ||
                          '  union SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID =' || '''' ||
                          p_terminal_id || '''' ||
                          '   UNION SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=' || '''' ||
                          p_terminal_id || '''' ||
                          '   UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=' || '''' || 'H' || '''' || ');' ||
                          'END;';

    str0 VARCHAR2(4000) := 'DECLARE RESULT VARCHAR2(10);' || 'BEGIN   ' ||
                           'SELECT ''X''  INTO RESULT  FROM FS_FR_CAMASTER WHERE SHIPMENTMODE IN  ' ||
                           p_shipment_mode_str || '  and  CARRIERID=' || '''' ||
                           p_carrier_id || '''' || '  AND ( INVALIDATE=' || '''' || 'F' || '''' ||
                           ' OR INVALIDATE IS NULL ) AND TERMINALID IN (' ||
                           ' SELECT TERMINALID FROM FS_FR_TERMINALMASTER); ' ||
                           'END;';

    RESULT         VARCHAR2(10);
    str1           VARCHAR2(50);
    str2           VARCHAR2(50);
    str3           VARCHAR2(50);
    v_opr_adm_flag VARCHAR2(10);
  BEGIN
    BEGIN

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminal_id;

      IF UPPER(TRIM(v_opr_adm_flag)) = 'H' THEN
        EXECUTE IMMEDIATE (str0);
      ELSE
        EXECUTE IMMEDIATE (str);
      END IF;

      IF SQL%FOUND THEN
        str1 := ' ';
      ELSE
        str1 := 'Carrier';
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        str1 := 'Carrier';
      WHEN TOO_MANY_ROWS THEN
        str1 := 'Carrier';
    END;

    BEGIN
      SELECT COUNT(*)
        INTO RESULT
        FROM FS_COUNTRYMASTER
       WHERE currencyid = p_currency_id;

      IF RESULT > 0 THEN
        str2 := ' ';
      ELSE
        str2 := 'Currency';
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        str2 := 'Currency';
      WHEN TOO_MANY_ROWS THEN
        str2 := 'CURRENCY';
    END;

    IF (str1 = ' ' AND str2 = ' ') THEN
      str3 := '1';
    ELSE
      str3 := str1 || '   ' || str2 || '  Invalid';
    END IF;

    RETURN(str3);
  END;

  /*  Validates the origin and destination locations in upload.
   *  It is passing the in parameters are
   *  p_location_id         VARCHAR2,
   *  p_shipment_mode_str   VARCHAR2,
   *  p_shipment_mode       VARCHAR2,
   *  out parameter is
   *  RETURN                VARCHAR2.
  */
  FUNCTION Validate_Location(p_Location_Id VARCHAR2,
                             --p_shipment_mode_str   VARCHAR2,
                             p_Shipment_Mode VARCHAR2) RETURN VARCHAR2 IS
    v_Location_Id VARCHAR2(5);
    v_Result      VARCHAR2(10);
    /*str1   VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM (select portid from fs_frs_portmaster where portid=:v_location_id'
          || '   AND (INVALIDATE = ''F'' or INVALIDATE is null) );'
          || '   END;';
    str2   VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM'
          || '   (select locationid from fs_fr_locationmaster  WHERE locationid IN (SELECT locationid'
          || '    FROM fs_fr_terminallocation) '
          || '   AND locationid =:v_location_id '
          || '   AND  SHIPMENTMODE IN   '
          || p_shipment_mode_str
          || '   AND  (INVALIDATE = ''F'' or INVALIDATE is null) );   '
          || '  END;';*/
  BEGIN
    IF p_Shipment_Mode = '2' THEN
      SELECT Portid
        INTO v_Result
        FROM FS_FRS_PORTMASTER
       WHERE Portid = p_Location_Id
         AND (Invalidate = 'F' OR Invalidate IS NULL);
      --EXECUTE IMMEDIATE (str1) Using p_location_id;
    ELSIF p_Shipment_Mode = '1' THEN
      SELECT Locationid
        INTO v_Result
        FROM FS_FR_LOCATIONMASTER
       WHERE Locationid IN (SELECT Locationid FROM FS_FR_TERMINALLOCATION)
         AND Locationid = p_Location_Id
         AND Shipmentmode IN ('1', '3', '5', '7')
         AND (Invalidate = 'F' OR Invalidate IS NULL);
    ELSIF p_Shipment_Mode = '4' THEN
      SELECT Locationid
        INTO v_Result
        FROM FS_FR_LOCATIONMASTER
       WHERE Locationid IN (SELECT Locationid FROM FS_FR_TERMINALLOCATION)
         AND Locationid = p_Location_Id
         AND Shipmentmode IN ('4', '5', '6', '7')
         AND (Invalidate = 'F' OR Invalidate IS NULL);
      --EXECUTE IMMEDIATE (str2) Using p_location_id;
    END IF;
    /*  SELECT 'X'
    INTO RESULT
    FROM ((select portid from fs_frs_portmaster where invalidate='F')
           union
          (select locationid from fs_fr_locationmaster  WHERE locationid IN (SELECT locationid
           FROM fs_fr_terminallocation)
           AND locationid = p_location_id
           AND  INVALIDATE = 'F'));*/
    IF SQL%FOUND THEN
      RETURN 'TRUE';
    ELSE
      RETURN 'FALSE';
    END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 'FALSE';
    WHEN TOO_MANY_ROWS THEN
      RETURN 'TRUE';
    WHEN OTHERS THEN
      RETURN SQLERRM;
  END;
  /*  Validates the origin, destination, service level in add/modify modules.
   *  It is passing the in parameters are
   *  p_origin_id           VARCHAR2,
   *  p_destination_id      VARCHAR2,
   *  p_terminal_id         VARCHAR2,
   *  p_shipment_mode_str   VARCHAR2,
   *  p_shipmemt_mode       VARCHAR2,
   *  p_servicelevel_id     VARCHAR2,
   *  p_density             VARCHAR2,
   *  p_uom                 VARCHAR2,
   *  out parameter is
   *  RETURN                VARCHAR2.
  */
  FUNCTION Validate_Org_Dest_Sl(p_origin_id         VARCHAR2,
                                p_destination_id    VARCHAR2,
                                p_terminal_id       VARCHAR2,
                                p_shipment_mode_str VARCHAR2,
                                p_shipmemt_mode     VARCHAR2,
                                p_servicelevel_id   VARCHAR2,
                                p_density           VARCHAR2,
                                p_uom               VARCHAR2) RETURN VARCHAR2 IS

    str VARCHAR2(4000) := 'SELECT count(*)   FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=' || '''' ||
                          p_servicelevel_id || '''' ||
                          '  and  SHIPMENTMODE IN    ' ||
                          p_shipment_mode_str || '  AND ( INVALIDATE=' || '''' || 'F' || '''' ||
                          ' or INVALIDATE is null )AND TERMINALID IN (select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=' || '''' ||
                          p_terminal_id || '''' ||
                          '   UNION SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=' || '''' ||
                          p_terminal_id || '''' ||
                          '   union SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=' || '''' ||
                          p_terminal_id || '''' ||
                          ' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=' || '''' || 'H' || '''' || ') ';
    --            || '  END;';

    str0 VARCHAR2(4000) := 'SELECT count(*) FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=' || '''' ||
                           p_servicelevel_id || '''' ||
                           '  and  SERVICELEVELID NOT IN (''SCH'')  AND SHIPMENTMODE IN    ' ||
                           p_shipment_mode_str || '  AND ( INVALIDATE=' || '''' || 'F' || '''' ||
                           ' or INVALIDATE is null ) AND SERVICELEVELID not in (''SCH'') AND TERMINALID IN (SELECT DISTINCT TERMINALID FROM FS_FR_TERMINALMASTER) ';

    str3 VARCHAR2(4000) := 'SELECT count(*)   FROM (select portid from fs_frs_portmaster where portid=' || '''' ||
                           p_origin_id || '''' ||
                           '  AND (INVALIDATE = ''F'' or INVALIDATE is null) )';

    str4 VARCHAR2(4000) := 'SELECT count(*)   FROM' ||
                           '   (select locationid from fs_fr_locationmaster  WHERE locationid IN (SELECT locationid' ||
                           '    FROM fs_fr_terminallocation)' ||
                           '    AND locationid = ' || '''' || p_origin_id || '''' ||
                           '  AND  SHIPMENTMODE IN   ' ||
                           p_shipment_mode_str ||
                           '  AND  (INVALIDATE = ''F'' or INVALIDATE is null) )';

    str5 VARCHAR2(4000) := 'SELECT count(*)    FROM (select portid from fs_frs_portmaster where portid=' || '''' ||
                           p_destination_id || '''' ||
                           ' AND (INVALIDATE = ''F'' or INVALIDATE is null) )';

    str6 VARCHAR2(4000) := 'SELECT count(*)  FROM' ||
                           '   (select locationid from fs_fr_locationmaster  WHERE locationid IN (SELECT locationid' ||
                           '    FROM fs_fr_terminallocation)' ||
                           '    AND locationid = ' || '''' ||
                           p_destination_id || '''' ||
                           '  AND  SHIPMENTMODE IN   ' ||
                           p_shipment_mode_str ||
                           '  AND  (INVALIDATE = ''F'' or INVALIDATE is null) )';

    RESULT        VARCHAR2(10);
    str11         VARCHAR2(50) := ' ';
    str22         VARCHAR2(50);
    str33         VARCHAR2(50);
    str44         VARCHAR2(100);
    str55         VARCHAR2(20) := ' ';
    v_accesslevel VARCHAR2(10);
    v_density     NUMBER(5);
  BEGIN
    BEGIN
      SELECT oper_admin_flag
        INTO v_accesslevel
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_terminal_id;

      IF v_accesslevel = 'H' THEN
        EXECUTE IMMEDIATE (str0)
          INTO RESULT;

        IF RESULT > 0 THEN
          str11 := ' ';
        ELSE
          str11 := 'ServiceLevel';
        END IF;
      ELSE
        EXECUTE IMMEDIATE (str)
          INTO RESULT;

        DBMS_OUTPUT.put_line(' In Else....' || RESULT);

        IF RESULT > 0 THEN
          str11 := ' ';
        ELSE
          str11 := 'ServiceLevel';
        END IF;
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        str11 := 'ServiceLevel';
    END;

    DBMS_OUTPUT.put_line('SERVICELEVEL ' || RESULT);

    BEGIN
      IF p_shipmemt_mode = '2' THEN
        EXECUTE IMMEDIATE (str3)
          INTO RESULT;
      ELSE
        EXECUTE IMMEDIATE (str4)
          INTO RESULT;
      END IF;

      IF RESULT > 0 THEN
        str22 := ' ';
      ELSE
        str22 := 'Origin';
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        str22 := 'Origin';
    END;

    DBMS_OUTPUT.put_line('ORGIN ' || RESULT);

    BEGIN
      IF p_shipmemt_mode = '2' THEN
        EXECUTE IMMEDIATE (str5)
          INTO RESULT;
      ELSE
        EXECUTE IMMEDIATE (str6)
          INTO RESULT;
      END IF;

      DBMS_OUTPUT.put_line('dest ' || RESULT);

      IF RESULT > 0 THEN
        str33 := ' ';
      ELSE
        str33 := 'Destination';
      END IF;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        str33 := 'Destination';
    END;

    DBMS_OUTPUT.put_line('p_density ' || LENGTH(p_density));
    --validated by ramakrishna
    IF (LENGTH(p_density) > 0 OR p_density <> '') THEN
      v_density := validate_density_ratio(p_density, p_shipmemt_mode, p_uom);

      IF v_density = 0 THEN
        str55 := ' Densityratio';
      ELSE
        str55 := ' ';
      END IF;
    END IF;

    IF (str11 = ' ' AND str22 = ' ' AND str33 = ' ' AND str55 = ' ') THEN
      str44 := '1';
    ELSE
      str44 := str11 || '  ' || str22 || '  ' || str33 || '  ' || str55 || '  ' ||
               ' Invalid';
    END IF;

    DBMS_OUTPUT.put_line(str44);
    RETURN(str44);
  END;
  /*  Validates the service level.
   *  It is passing the in parameters are
   *  p_terminal_id         VARCHAR2,
   *  p_shipment_mode_str   VARCHAR2,
   *  p_servicelevel_id     VARCHAR2,
   *  out parameter is
   *  RETURN                VARCHAR2.
  */
  FUNCTION Validate_Servicelevel(p_Terminal_Id     VARCHAR2,
                                 p_Shipment_Mode   VARCHAR2,
                                 p_Servicelevel_Id VARCHAR2) RETURN VARCHAR2 IS
    v_Service_Level_Id VARCHAR2(5);
    v_Terminal_Id      VARCHAR2(10);
    v_Result           VARCHAR2(10);
    /*str             VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=:v_service_level_id'
          || '  and  SERVICELEVELID NOT IN (''SCH'') AND SHIPMENTMODE IN    '
          || p_shipment_mode_str
          || '  AND ( INVALIDATE='
          || ''''
          || 'F'
          || ''''
          || ' or INVALIDATE is null )AND TERMINALID IN (select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=:v_terminal_id'
          || '   union SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=:v_terminal_id'
          || '   UNION SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=:v_terminal_id'
          || '   UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='
          || ''''
          || 'H'
          || ''''
          || ');   '
          || '  END;';

    str0            VARCHAR2 (4000)
       :=    'DECLARE RESULT VARCHAR2(10);'
          || 'BEGIN   '
          || 'SELECT ''X''  INTO RESULT  FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=:v_service_level_id'
          || '  and  SERVICELEVELID NOT IN (''SCH'') AND SHIPMENTMODE IN    '
          || p_shipment_mode_str
          || '  AND ( INVALIDATE='
          || ''''
          || 'F'
          || ''''
          || ' or INVALIDATE is null ) AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG=''A'' AND (INVALIDATE=''F'' OR INVALIDATE IS NULL));   '
          || '  END;';*/
    v_Accesslevel VARCHAR2(10);
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Accesslevel
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = p_Terminal_Id;
    /*IF v_accesslevel = 'H'
    THEN
       --EXECUTE IMMEDIATE (str0) Using p_servicelevel_id;

       IF SQL%FOUND
       THEN
          RETURN 'TRUE';
       ELSE
          RETURN 'FALSE';
       END IF;
    ELSE
       EXECUTE IMMEDIATE (str) Using p_servicelevel_id,p_terminal_id,p_terminal_id,p_terminal_id;

       IF SQL%FOUND
       THEN
          RETURN 'TRUE';
       ELSE
          RETURN 'FALSE';
       END IF;
    END IF;*/
    IF v_Accesslevel = 'H' THEN
      IF p_Shipment_Mode = '1' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (1, 3, 5, 7)
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      ELSIF p_Shipment_Mode = '2' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (2, 3, 6, 7)
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      ELSIF p_Shipment_Mode = '4' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (4, 5, 6, 7)
           AND (Invalidate = 'F' OR Invalidate IS NULL);
      END IF;
      --EXECUTE IMMEDIATE (str0) Using p_carrier_id;
    ELSE
      IF p_Shipment_Mode = '1' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (1, 3, 5, 7)
           AND Servicelevelid NOT IN ('SCH')
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      ELSIF p_Shipment_Mode = '2' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (2, 3, 6, 7)
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      ELSIF p_Shipment_Mode = '4' THEN
        SELECT 'X'
          INTO v_Result
          FROM FS_FR_SERVICELEVELMASTER
         WHERE Servicelevelid = p_Servicelevel_Id
           AND Servicelevelid NOT IN ('SCH')
           AND Shipmentmode IN (4, 5, 6, 7)
           AND (Invalidate = 'F' OR Invalidate IS NULL)
           AND Terminalid IN
               (SELECT Child_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                 START WITH Parent_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Terminalid = p_Terminal_Id
                UNION
                SELECT Parent_Terminal_Id Terminalid
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
                 START WITH Child_Terminal_Id = p_Terminal_Id
                UNION
                SELECT Terminalid
                  FROM FS_FR_TERMINALMASTER
                 WHERE Oper_Admin_Flag = 'H');
      END IF;
      --EXECUTE IMMEDIATE (str) Using p_carrier_id,p_terminal_id,p_terminal_id,p_terminal_id;
    END IF;
    IF SQL%FOUND THEN
      RETURN 'TRUE';
    ELSE
      RETURN 'FALSE';
    END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 'FALSE';
    WHEN TOO_MANY_ROWS THEN
      RETURN 'TRUE';
    WHEN OTHERS THEN
      --   DBMS_OUTPUT.put_line ('ERROR3');
      RETURN 'FALSE';
  END;
  --@@ For BuyRates Add
  /*  Validates the unique combination of the buy rates while add and Used for inactivating
   *  the lower level buy rates in case of higher level adding them for a particular combination
   *  and calls the quote updating procedures.
   *  It is passing the in parameters are
   *  p_shipment_mode         VARCHAR2,
   *  p_origin                VARCHAR2,
   *  p_destination           VARCHAR2,
   *  p_servicelevel_id       VARCHAR2,
   *  p_carrier_id            VARCHAR2,
   *  p_effective_from        DATE,
   *  p_validate_upto         DATE,
   *  p_weight_break          VARCHAR2,
   *  p_rate_type             VARCHAR2,
   *  p_current_terminal_id   VARCHAR2,
   *  p_frequency             VARCHAR2,
   *  p_density               VARCHAR2,
   *  p_newbuyrateid          VARCHAR2,
   *  p_newlaneno             VARCHAR2,
   *  out parameter is
   *  RETURN                  NUMBER.
  */
  FUNCTION Validate_Upd_Insert_Buyrate(p_shipment_mode VARCHAR2,

                                       p_origin VARCHAR2,

                                       p_destination VARCHAR2,

                                       p_servicelevel_id VARCHAR2,

                                       p_carrier_id VARCHAR2,

                                       p_effective_from DATE,

                                       p_validate_upto DATE,

                                       p_weight_break VARCHAR2,

                                       p_rate_type VARCHAR2,

                                       p_current_terminal_id VARCHAR2,

                                       p_frequency VARCHAR2,

                                       p_density VARCHAR2,

                                       p_newbuyrateid VARCHAR2,

                                       p_newlaneno VARCHAR2,

                                       p_transittime VARCHAR2,

                                       p_type VARCHAR2,

                                       p_currency VARCHAR2) --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08
   RETURN VARCHAR2 IS
    str             VARCHAR2(10);
    v_accesslevel   VARCHAR2(10);
    v_frequency     VARCHAR2(4000);
    v_density       VARCHAR2(50);
    v_shipmodestr   VARCHAR2(10);
    v_return        VARCHAR2(100);
    v_curr          sys_refcursor;
    v_curr1         sys_refcursor;
    v_buyrateid     VARCHAR2(25);
    v_laneno        VARCHAR2(5);
    v_marginbasis   VARCHAR2(100);
    v_margintype    VARCHAR2(100);
    v_rec_con_id    NUMBER:=0;
    v_weightclass   VARCHAR2(100);
    v_overallmargin VARCHAR2(100);
    v_seq           VARCHAR2(1000);
    v_versionno     VARCHAR2(5);
    v_newversionno  VARCHAR2(5); --@@Added for the WPBN issues-146448,146968 on 19/12/08
    v_id            VARCHAR2(2000);
    v_currency      VARCHAR2(10);
    v_consoletype      VARCHAR2(10);
    k               NUMBER(10) := 0;
    v_margin_perc  NUMBER(20,6);--Added by subrahmanyam for security charge missing issue
  BEGIN
 -- raise_application_error(-22222,'error n application@@@@@@@@');
    v_frequency := Pkg_Qms_Buyrates.comb_freq_func(p_frequency);


    --SUBSTR(v_frequency,2,LENGTH(v_frequency)-2)
    IF p_type = 'NEW' THEN
      str := Pkg_Qms_Buyrates.Validate_Buyrate_new(p_shipment_mode       => p_shipment_mode,
                                               p_origin              => p_origin,
                                               p_destination         => p_destination,
                                               p_service_level_id    => p_servicelevel_id,
                                               p_carrier_id          => p_carrier_id,
                                               p_effective_from      => p_effective_from,
                                               p_validate_upto       => p_validate_upto,
                                               p_weight_break        => p_weight_break,
                                               p_rate_type           => p_rate_type,
                                               p_current_terminal_id => p_current_terminal_id,
                                               p_frequency           => p_frequency,
                                               p_density             => p_density,
                                               p_currency            => p_currency);

    ELSE
      str := 'FALSE';
    END IF;
    v_buyrateid := '';
    v_laneno    := '';

    IF str = 'TRUE' THEN
      --RETURN '1' --@@Modified for the WPBN issues -146448,146968 on 18/12/08
      v_return := '1' || ',' || v_buyrateid || ',' || v_laneno;
      RETURN v_return;
    ELSIF str = '0' THEN
      --RETURN '0' --@@Modified for the WPBN issues -146448,146968 on 18/12/08
      v_return := '0' || ',' || v_buyrateid || ',' || v_laneno;
      RETURN v_return;
    END IF;

    v_return := '2';

    SELECT oper_admin_flag
      INTO v_accesslevel
      FROM FS_FR_TERMINALMASTER
     WHERE terminalid = p_current_terminal_id;

    IF p_shipment_mode = '1' THEN
      v_shipmodestr := 'Air';
    ELSIF p_shipment_mode = '2' THEN
      v_shipmodestr := 'Sea';
    ELSE
      v_shipmodestr := 'Truck';
    END IF;

    IF v_accesslevel = 'H' THEN

      /** OPEN V_CURR FOR 'SELECT  a.buyrateid buyrateid, b.lane_no lane_no' || ' FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b' || ' WHERE b.buyrateid = a.buyrateid' || ' AND b.service_level = ' || '''' || p_servicelevel_id || '''' || ' And b.line_no=''0'' ' || ' AND b.carrier_id = ' || '''' || p_carrier_id || '''' || ' AND a.shipment_mode = ' || p_shipment_mode || ' AND a.weight_break = ' || '''' || p_weight_break || '''' || ' AND ' || v_frequency || ' AND a.rate_type = ' || '''' || p_rate_type || '''' || ' AND b.activeinactive IS NULL ' || ' AND b.origin = ' || '''' || p_origin || '''' || ' AND b.destination = ' || '''' || p_destination || '''';
      -- ||' AND a.terminalid IN (SELECT terminalid FROM FS_FR_TERMINALMASTER  )';*/
      --@@Modified for the WPBN issues-146448,146968 on 19/12/08
      OPEN V_CURR FOR 'SELECT  a.console_type,a.buyrateid buyrateid, b.lane_no lane_no,b.version_no version_No' || ' FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b' || ' WHERE b.buyrateid = a.buyrateid and b.version_no=a.version_no and (b.lane_no=a.lane_no or a.lane_no is null)' || ' AND b.service_level = ' || '''' || p_servicelevel_id || '''' || ' And b.line_no=''0'' ' || ' AND b.carrier_id = ' || '''' || p_carrier_id || '''' || ' AND a.shipment_mode = ' || p_shipment_mode || ' AND a.weight_break = ' || '''' || p_weight_break || '''' || ' AND ' || v_frequency || ' AND a.rate_type = ' || '''' || p_rate_type || '''' || ' AND b.activeinactive IS NULL ' || ' AND b.origin = ' || '''' || p_origin || '''' || ' AND b.destination = ' || '''' || p_destination || '''' || ' AND b.transit_time = ' || '''' || p_transittime || '''' || ' AND to_date(b.valid_upto) = ' || '''' || TO_DATE(p_validate_upto) || ''''||' And b.density_code = '|| '''' ||p_density || ''''||'And a.currency = '||''''|| p_currency ||'''' ;
      -- ||' AND a.terminalid IN (SELECT terminalid FROM FS_FR_TERMINALMASTER  )';


      LOOP

        FETCH v_curr
        -- INTO v_buyrateid, v_laneno;
          INTO v_consoletype,v_buyrateid, v_laneno, v_versionno; --@@Modified for the WPBN issues-146448,146968 on 19/12/08

        EXIT WHEN  v_curr%NOTFOUND ;


          --@@Modified for the WPBN issues-146448,146968 on 19/12/08

          --if upper(i.WEIGHT_BREAK) = upper(p_weight_break) then
          --@@Modified for the WPBN issues-146448,146968 on 19/12/08
          /**QMS_QUOTEPACK_NEW.qms_quote_update(NULL,
          v_buyrateid,
          v_laneno,
          NULL,
          p_newbuyrateid,
          p_newlaneno,
          'BR',
          NULL,
          NULL,
          p_origin || '-' || p_destination || ',' ||
          v_shipmodestr ||
          ' Freight Rates');*/
          -- end if;
          v_newversionno := v_versionno;
          IF v_consoletype!='FCL'
          THEN
          QMS_QUOTEPACK_NEW.qms_quote_update(NULL,
                                             v_buyrateid,
                                             v_laneno,
                                             v_versionno,
                                              v_newversionno,
                                             NULL,
                                             v_buyrateid,
                                             v_laneno,
                                              'BR',
                                             NULL,
                                             NULL,
                                             p_origin || '-' ||
                                             p_destination || ',' ||
                                             v_shipmodestr ||
                                             ' Freight Rates');

          UPDATE QMS_BUYRATES_DTL
             SET activeinactive = 'I'
           WHERE buyrateid = v_buyrateid
             AND lane_no = v_laneno
             AND version_no = v_versionno; --@@Added for the WPBN issues-146448,146968 on 19/12/08

          /*  UPDATE QMS_REC_CON_SELLRATESDTL SET AI_FLAG='I' WHERE BUYRATEID=v_buyrateid AND LANE_NO =v_laneno
          AND REC_CON_ID IN ( SELECT DTL.REC_CON_ID FROM QMS_REC_CON_SELLRATESMASTER MAS, QMS_REC_CON_SELLRATESDTL DTL WHERE MAS.RC_FLAG='C'
           AND MAS.REC_CON_ID=DTL.REC_CON_ID AND DTL.BUYRATEID=v_buyrateid AND LANE_NO= v_laneno AND DTL.AI_FLAG='A');*/

          UPDATE QMS_REC_CON_SELLRATESDTL DTL
             SET ACCEPTANCE_FLAG = 'Y'
           WHERE BUYRATEID = v_buyrateid
             AND LANE_NO = v_laneno
             AND version_no = v_versionno --@@Added for the WPBN issues-146448,146968 on 19/12/08
             AND DTL.AI_FLAG = 'A';

          --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08
        /* Begin*/

          FOR i IN (SELECT REC_CON_ID,
                 ACCESSLEVEL,
                 WEIGHT_CLASS,
                 CURRENCY,
                 MARGIN_TYPE,
                 MARGIN_BASIS,
                 OVERALL_MARGIN,
                 terminalid
        /*    into v_rec_con_id,
                 v_accesslevel,
                 v_weightclass,
                 v_currency,
                 v_overallmargin,
                 v_marginbasis,
                 v_margintype*/
            FROM QMS_REC_CON_SELLRATESMASTER
           WHERE REC_CON_ID IN (SELECT REC_CON_ID
                                  FROM QMS_REC_CON_SELLRATESDTL DTL
                                 WHERE BUYRATEID = v_buyrateid
                                   AND LANE_NO = v_laneno
                                   AND version_no = v_versionno
                                   AND LINE_NO = '0'))
                                   LOOP
      /*   Exception When NO_DATA_FOUND
         Then
         v_rec_con_id :=0;
         End;*/
          IF i.rec_con_id>0
          THEN
        SELECT seq_sellrates_acc.NEXTVAL INTO v_seq FROM DUAL;
          INSERT INTO QMS_REC_CON_SELLRATESMSTR_ACC
            (rec_con_id,
             rc_flag,
             shipment_mode,
             weight_break,
             rate_type,
             currency,
             weight_class,
             accesslevel,
             terminalid)
          VALUES
            (v_seq,
             'R',
             p_shipment_mode,
             p_weight_break,
             p_rate_type,
             v_currency,
             i.weight_class,
             i.accesslevel,
             i.terminalid);
          k := 0;
          v_margin_perc := 0.00000;--subrahmanaym
          FOR j IN (SELECT qbd.chargerate           chargerate,
                           qbd.weight_break_slab    wbslab,
                           qbd.chargerate_indicator ch_indicator,
                           qbd.lowerbound           lbound,
                           qbd.upperbound           ubound,
                           qbd.rate_description     rate_description,
                           qbd.service_level        srvlevel,
                           qbd.carrier_id           carrier,
                           qbd.origin               origin,
                           qbd.destination          dest,
                           qbd.transit_time         transittime,
                           qbm.console_type         consoletype,
                           qsd.origin_country       origin_country,
                           qsd.desti_country        dest_country,
                           /*qsd.margin_perc          perc,*/--subrahmanyam
                           qsd.notes                notes,
                           qbd.line_no              line_no,
                           qbd.frequency            freq
                      FROM QMS_BUYRATES_MASTER      qbm,
                           QMS_BUYRATES_DTL         qbd,
                           QMS_REC_CON_SELLRATESDTL qsd
                     WHERE qbd.buyrateid = qbm.buyrateid
                       AND qbd.version_no = qbm.version_no
                       AND qbd.buyrateid = v_buyrateid
                       AND qbd.lane_no = v_laneno
                       AND qbd.version_no = v_versionno
                       AND qsd.buyrateid = qbm.buyrateid
                       AND qsd.buyrateid = v_buyrateid
                       AND qsd.lane_no = v_laneno
                       AND (qbd.INVALIDATE IS NULL OR
                           UPPER(qbd.INVALIDATE) = 'F')
                       AND (qbd.activeinactive IS NULL OR
                           UPPER(qbd.activeinactive) = 'A')
                     ORDER BY line_no) LOOP
            SELECT seq_sellrates_acc_id.NEXTVAL INTO v_id FROM DUAL;
--Added by subrahmanyam for security charge missing issue
            BEGIN
SELECT rsd.MARGIN_PERC INTO v_margin_perc FROM QMS_REC_CON_SELLRATESDTL rsd WHERE rsd.rec_con_id=i.rec_con_id AND rsd.weightbreakslab=j.wbslab AND rsd.buyrateid=v_buyrateid AND rsd.lane_no=v_laneno AND rsd.version_no=v_versionno;
EXCEPTION WHEN NO_DATA_FOUND THEN
v_margin_perc :=0.00000;
 END;
--Ended by subrahmanyam for security charge missing issue
            INSERT INTO QMS_REC_CON_SELLRATESDTL_ACC
              (rec_con_id,
               carrier_id,
               servicelevel_id,
               chargerate,
               upper_bound,
               lowrer_bound,
               margin_perc,
               weightbreakslab,
               frequency,
               chargerate_indicator,
               lane_no,
               line_no,
               buyrateid,
               transit_time,
               origin,
               destination,
               console_type,
               ID,
               rec_con_id_sell,
               lane_no_sell,
               INVALIDATE,
               ai_flag,
               rate_description,
               version_no,
               origin_country,
               desti_country,
               notes)
            VALUES
              (v_seq,
               j.carrier,
               j.srvlevel,
               j.chargerate,
               j.ubound,
               j.lbound,
               /*j.perc*/--Commented by subrahmanyam for security chage missing issue
               v_margin_perc,--Added by subrahmanyam for security charge missing issue
               j.wbslab,
               j.freq,
               j.ch_indicator,
               v_laneno,
               k,
               v_buyrateid,
               j.transittime,
               j.origin,
               j.dest,
               DECODE(j.consoletype, 'P', NULL, j.consoletype),
               v_id,
               i.rec_con_id,
               --v_lane_no,        --@@Modified by Kameswari for the WPBN issue-122621
               v_laneno,
               'F',
               'A',
               DECODE(j.rate_description,
                      '',
                      'A FREIGHT RATE',
                      j.rate_description),
               v_versionno,
               j.origin_country,
               j.dest_country,
               j.notes);

            UPDATE QMS_BUYRATES_DTL DTL
               SET ACC_FLAG = 'Y'
             WHERE BUYRATEID = v_buyrateid
               AND LANE_NO = v_laneno
               AND VERSION_NO = v_versionno
               AND DTL.ACTIVEINACTIVE IS NULL;
            k := k + 1;
          END LOOP;
        END IF;
          /*
               Inactivating CSR as currently it has no acceptance module
          */

          UPDATE QMS_REC_CON_SELLRATESDTL DTL
             SET DTL.AI_FLAG = 'I'
           WHERE DTL.BUYRATEID = v_buyrateid
             AND DTL.LANE_NO = v_laneno
             AND version_no = v_versionno --@@Added for the WPBN issues-146448,146968 on 19/12/08
             AND DTL.AI_FLAG = 'A'
             AND EXISTS (SELECT 'X'
                    FROM QMS_REC_CON_SELLRATESMASTER MAS
                   WHERE MAS.RC_FLAG = 'C'
                     AND MAS.REC_CON_ID = DTL.REC_CON_ID
                     AND DTL.BUYRATEID = v_buyrateid);
          /*
            --
          */
          END LOOP;
        END IF;

      END LOOP;

      CLOSE v_curr;
      IF v_consoletype!='FCL'
      THEN
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE (rec_con_id, lane_no) IN
             (SELECT a.rec_con_id recid, b.lane_no lane_no
                FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                     QMS_REC_CON_SELLRATESDTL_ACC  b
               WHERE b.rec_con_id = a.rec_con_id
                 AND b.servicelevel_id = p_servicelevel_id
                 AND b.line_no = '0'
                 AND b.carrier_id = p_carrier_id
                 AND a.shipment_mode = p_shipment_mode
                 AND a.weight_break = p_weight_break
                    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
                 AND a.rate_type = p_rate_type
                 AND (b.ai_flag IS NULL OR b.ai_flag = 'A')
                    --AND (B.INVALIDATE='F' OR B.INVALIDATE IS NULL)
                 AND b.origin = p_origin
                 AND b.destination = p_destination);
      /*       AND a.terminalid IN (
      SELECT terminalid
                 FROM FS_FR_TERMINALMASTER
                WHERE actv_flag = 'A'
                  ));*/
        END IF;
      --RETURN '2' --@@Modified for the WPBN issues -146448,146968 on 18/12/08

      IF v_buyrateid IS NOT NULL THEN
        v_return := '2' || ',' || v_buyrateid || ',' || v_laneno;
      ELSE
        OPEN V_CURR1 FOR 'SELECT  a.buyrateid buyrateid, b.lane_no lane_no ' || ' FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b' || ' WHERE b.buyrateid = a.buyrateid AND b.version_no=a.version_no' || ' AND b.service_level = ' || '''' || p_servicelevel_id || '''' || ' And b.line_no=''0'' ' || ' AND b.carrier_id = ' || '''' || p_carrier_id || '''' || ' AND a.shipment_mode = ' || p_shipment_mode || ' AND a.weight_break = ' || '''' || p_weight_break || '''' || ' AND a.rate_type = ' || '''' || p_rate_type || '''' || ' AND b.activeinactive IS NULL ' || ' AND b.origin = ' || '''' || p_origin || '''' || ' AND b.destination = ' || '''' || p_destination || ''''||' And b.density_code = '||''''||p_density ||''''||' And a.currency = '||''''||p_currency||'''';
        LOOP
          FETCH V_CURR1
            INTO v_buyrateid, v_laneno;
              EXIT WHEN V_CURR1%NOTFOUND;
              v_return := '3' || ',' || v_buyrateid || ',' || v_laneno;

        END LOOP;

      END IF;
      RETURN v_return;
    ELSE
      --@@Modified for the WPBN issues-146448,146968 on 19/12/08
      /*OPEN V_CURR FOR 'SELECT  a.buyrateid buyrateid, b.lane_no lane_no
       FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
      WHERE b.buyrateid = a.buyrateid
        AND b.service_level = ' || '''' || p_servicelevel_id || '''' || '
        AND b.line_no=''0''
        AND b.carrier_id = ' || '''' || p_carrier_id || '''' || '
        AND a.shipment_mode = ' || '''' || p_shipment_mode || '''' || '
        AND a.weight_break = ' || '''' || p_weight_break || '''' || '
        AND ' || v_frequency || '
        AND a.rate_type = ' || '''' || p_rate_type || '''' || '
        AND b.activeinactive IS NULL
        AND b.origin = ' || '''' || p_origin || '''' || '
        AND b.destination = ' || '''' || p_destination || '''' || '
        AND a.terminalid IN (
               SELECT     child_terminal_id terminalid
                     FROM FS_FR_TERMINAL_REGN
               CONNECT BY PRIOR child_terminal_id =
                                              parent_terminal_id
               START WITH parent_terminal_id =' || '''' || p_current_terminal_id || '''' || '
               UNION
               SELECT terminalid
                 FROM FS_FR_TERMINALMASTER
                WHERE terminalid = ' || '''' || p_current_terminal_id || '''' || '  )';*/

      OPEN V_CURR FOR 'SELECT  a.console_type,a.buyrateid buyrateid, b.lane_no lane_no,b.version_no version_no
                     FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
                    WHERE b.buyrateid = a.buyrateid
                      AND b.service_level = ' || '''' || p_servicelevel_id || '''' || '
                      AND b.line_no=''0''
                      AND b.carrier_id = ' || '''' || p_carrier_id || '''' || '
                      AND a.shipment_mode = ' || '''' || p_shipment_mode || '''' || '
                      AND a.weight_break = ' || '''' || p_weight_break || '''' || '
                      AND ' || v_frequency || '
                      AND a.rate_type = ' || '''' || p_rate_type || '''' || '
                      AND b.activeinactive IS NULL
                      AND b.origin = ' || '''' || p_origin || '''' || '
                      AND b.destination = ' || '''' || p_destination || '''' || '
                    AND b.transit_time = ' || '''' || p_transittime || '''' || '
                    AND TO_DATE(b.valid_upto) = ' || '''' || to_date(p_validate_upto) || '''' || '
                      AND a.terminalid IN (
                             SELECT     child_terminal_id terminalid
                                   FROM FS_FR_TERMINAL_REGN
                             CONNECT BY PRIOR child_terminal_id =
                                                            parent_terminal_id
                             START WITH parent_terminal_id =' || '''' || p_current_terminal_id || '''' || '
                             UNION
                             SELECT terminalid
                               FROM FS_FR_TERMINALMASTER
                              WHERE terminalid = ' || '''' || p_current_terminal_id || '''' || '  )';

      LOOP

        FETCH v_curr
        --INTO v_buyrateid, v_laneno;
          INTO v_consoletype,v_buyrateid, v_laneno, v_versionno; --@@Modified for the WPBN issues-146448,146968 on 19/12/08
          EXIT WHEN  v_curr%NOTFOUND ;
          --@@Modified for the WPBN issues-146448,146968 on 19/12/08
          --if upper(i.WEIGHT_BREAK) = upper(p_weight_break) then
          /*QMS_QUOTEPACK_NEW.qms_quote_update(NULL,
          v_buyrateid,
          v_laneno,
          NULL,
          p_newbuyrateid,
          p_newlaneno,
          'BR',
          NULL,
          NULL,
          p_origin || '-' || p_destination || ',' ||
          v_shipmodestr ||
          ' Freight Rates');*/
          --@@Modified for the WPBN issues-146448,146968 on 19/12/08
          IF v_consoletype!='FCL'
          THEN
          QMS_QUOTEPACK_NEW.qms_quote_update(NULL,
                                             v_buyrateid,
                                             v_laneno,
                                             v_versionno,
                                                v_versionno + 1,
                                             NULL,
                                             v_buyrateid,
                                             v_laneno,
                                             'BR',
                                             NULL,
                                             NULL,
                                              p_origin || '-' ||
                                             p_destination || ',' ||
                                             v_shipmodestr ||
                                             ' Freight Rates');

          UPDATE QMS_BUYRATES_DTL
             SET activeinactive = 'I'
           WHERE buyrateid = v_buyrateid
             AND lane_no = v_laneno
             AND version_no = v_versionno; --@@Modified for the WPBN issues-146448,146968 on 19/12/08

          UPDATE QMS_REC_CON_SELLRATESDTL DTL
             SET ACCEPTANCE_FLAG = 'Y'
           WHERE BUYRATEID = v_buyrateid
             AND LANE_NO = v_laneno
             AND DTL.AI_FLAG = 'A'
             AND version_no = v_versionno; --@@Modified for the WPBN issues-146448,146968 on 19/12/08

          --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08
       /* Begin*/
       FOR i IN
          (SELECT  REC_CON_ID,
                 ACCESSLEVEL,
                 WEIGHT_CLASS,
                 CURRENCY,
                 MARGIN_TYPE,
                 MARGIN_BASIS,
                 OVERALL_MARGIN,
                 terminalid


          /*  into v_rec_con_id,
                 v_accesslevel,
                 v_weightclass,
                 v_currency,
                 v_overallmargin,
                 v_marginbasis,
                 v_margintype*/
            FROM QMS_REC_CON_SELLRATESMASTER
           WHERE REC_CON_ID IN (SELECT REC_CON_ID
                                  FROM QMS_REC_CON_SELLRATESDTL DTL
                                 WHERE BUYRATEID = v_buyrateid
                                   AND LANE_NO = v_laneno
                                   AND version_no = v_versionno
                                   AND LINE_NO = '0'))
                                   LOOP
     /*  Exception When NO_DATA_FOUND
       Then
       v_rec_con_id :=0;
       End;*/
       IF i.rec_con_id>0
       THEN
           SELECT seq_sellrates_acc.NEXTVAL INTO v_seq FROM DUAL;
          INSERT INTO QMS_REC_CON_SELLRATESMSTR_ACC
            (rec_con_id,
             rc_flag,
             shipment_mode,
             weight_break,
             rate_type,
             currency,
             weight_class,
             accesslevel,
             terminalid)
          VALUES
            (v_seq,
             'R',
             p_shipment_mode,
             p_weight_break,
             p_rate_type,
             v_currency,
             i.weight_class,
             i.accesslevel,
             i.terminalid);
          k := 0;
          v_margin_perc :=0.00000;--Added by subrahmanyam for security charge missing issue
          FOR j IN (SELECT qbd.chargerate           chargerate,
                           qbd.weight_break_slab    wbslab,
                           qbd.chargerate_indicator ch_indicator,
                           qbd.lowerbound           lbound,
                           qbd.upperbound           ubound,
                           qbd.rate_description     rate_description,
                           qbd.service_level        srvlevel,
                           qbd.carrier_id           carrier,
                           qbd.origin               origin,
                           qbd.destination          dest,
                           qbd.transit_time         transittime,
                           qbm.console_type         consoletype,
                           qsd.origin_country       origin_country,
                           qsd.desti_country        dest_country,
                           /*qsd.margin_perc          perc,*/--commented by subrahmanyam for security charge missing issue
                           qsd.notes                notes,
                           qbd.line_no              line_no,
                           qbd.frequency            freq
                      FROM QMS_BUYRATES_MASTER      qbm,
                           QMS_BUYRATES_DTL         qbd,
                           QMS_REC_CON_SELLRATESDTL qsd
                     WHERE qbd.buyrateid = qbm.buyrateid
                       AND qbd.version_no = qbm.version_no
                       AND qbd.buyrateid = v_buyrateid
                       AND qbd.lane_no = v_laneno
                       AND qbd.version_no = v_versionno
                       AND qsd.buyrateid = qbm.buyrateid
                       AND qsd.buyrateid = v_buyrateid
                       AND qsd.lane_no = v_laneno
                       AND (qbd.INVALIDATE IS NULL OR
                           UPPER(qbd.INVALIDATE) = 'F')
                       AND (qbd.activeinactive IS NULL OR
                           UPPER(qbd.activeinactive) = 'A')
                     ORDER BY line_no) LOOP
            SELECT seq_sellrates_acc_id.NEXTVAL INTO v_id FROM DUAL;
--Added by subrahmanyam for security charge missing issue
            BEGIN
SELECT rsd.MARGIN_PERC INTO v_margin_perc FROM QMS_REC_CON_SELLRATESDTL rsd WHERE rsd.rec_con_id=i.rec_con_id AND rsd.weightbreakslab=j.wbslab AND rsd.buyrateid=v_buyrateid AND rsd.lane_no=v_laneno AND rsd.version_no=v_versionno;
EXCEPTION WHEN NO_DATA_FOUND THEN
v_margin_perc:=0.00000;
 END;
 --commented by subrahmanyam for security charge missing issue
            INSERT INTO QMS_REC_CON_SELLRATESDTL_ACC
              (rec_con_id,
               carrier_id,
               servicelevel_id,
               chargerate,
               upper_bound,
               lowrer_bound,
               margin_perc,
               weightbreakslab,
               frequency,
               chargerate_indicator,
               lane_no,
               line_no,
               buyrateid,
               transit_time,
               origin,
               destination,
               console_type,
               ID,
               rec_con_id_sell,
               lane_no_sell,
               INVALIDATE,
               ai_flag,
               rate_description,
               version_no,
               origin_country,
               desti_country,
               notes)
            VALUES
              (v_seq,
               j.carrier,
               j.srvlevel,
               j.chargerate,
               j.ubound,
               j.lbound,
               /*j.perc*/--commented by subrahmanyam for security charge missing issue
               v_margin_perc,--Added by subrahmanyam for security charge missing issue
               j.wbslab,
               j.freq,
               j.ch_indicator,
               v_laneno,
               k,
               v_buyrateid,
               j.transittime,
               j.origin,
               j.dest,
               DECODE(j.consoletype, 'P', NULL, j.consoletype),
               v_id,
               i.rec_con_id,
               --v_lane_no,        --@@Modified by Kameswari for the WPBN issue-122621
               v_laneno,
               'F',
               'A',
               DECODE(j.rate_description,
                      '',
                      'A FREIGHT RATE',
                      j.rate_description),
               v_versionno,
               j.origin_country,
               j.dest_country,
               j.notes);

            UPDATE QMS_BUYRATES_DTL DTL
               SET ACC_FLAG = 'Y'
             WHERE BUYRATEID = v_buyrateid
               AND LANE_NO = v_laneno
               AND VERSION_NO = v_versionno
               AND DTL.ACTIVEINACTIVE IS NULL;
            k := k + 1;
          END LOOP;
        END IF;
          /*
               Inactivating CSR as currently it has no acceptance module
          */

          UPDATE QMS_REC_CON_SELLRATESDTL DTL
             SET DTL.AI_FLAG = 'I'
           WHERE DTL.BUYRATEID = v_buyrateid
             AND DTL.LANE_NO = v_laneno
             AND version_no = v_versionno --@@Modified for the WPBN issues-146448,146968 on 19/12/08
             AND DTL.AI_FLAG = 'A'
             AND EXISTS (SELECT 'X'
                    FROM QMS_REC_CON_SELLRATESMASTER MAS
                   WHERE MAS.RC_FLAG = 'C'
                     AND MAS.REC_CON_ID = DTL.REC_CON_ID
                     AND DTL.BUYRATEID = v_buyrateid);
          /*
            --
          */
          END LOOP;

        END IF;

      END LOOP;
      CLOSE v_curr;
      IF v_consoletype!='FCL'
      THEN
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE (rec_con_id, lane_no) IN
             (SELECT a.rec_con_id recid, b.lane_no lane_no
                FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                     QMS_REC_CON_SELLRATESDTL_ACC  b
               WHERE b.rec_con_id = a.rec_con_id
                 AND b.servicelevel_id = p_servicelevel_id
                 AND b.line_no = '0'
                 AND b.carrier_id = p_carrier_id
                 AND a.shipment_mode = p_shipment_mode
                 AND a.weight_break = p_weight_break
                    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
                 AND a.rate_type = p_rate_type
                 AND (b.ai_flag IS NULL OR b.ai_flag = 'A')
                    --AND (B.INVALIDATE='F' OR B.INVALIDATE IS NULL)
                 AND b.origin = p_origin
                 AND b.destination = p_destination
                 AND a.terminalid IN
                     (SELECT child_terminal_id terminalid
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                       START WITH parent_terminal_id = p_current_terminal_id
                      UNION
                      SELECT terminalid
                        FROM FS_FR_TERMINALMASTER
                       WHERE terminalid = p_current_terminal_id));
    END IF;
      --RETURN '2' --@@Modified for the WPBN issues -146448,146968 on 18/12/08


      IF v_buyrateid IS NOT NULL THEN
        v_return := '2' || ',' || v_buyrateid || ',' || v_laneno;
      ELSE
        OPEN V_CURR1 FOR 'SELECT  a.buyrateid buyrateid, b.lane_no lane_no
                     FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
                    WHERE b.buyrateid = a.buyrateid AND b.version_no=a.version_no
                      AND b.service_level = ' || '''' || p_servicelevel_id || '''' || '
                      AND b.line_no=''0''
                      AND b.carrier_id = ' || '''' || p_carrier_id || '''' || '
                      AND a.shipment_mode = ' || '''' || p_shipment_mode || '''' || '
                      AND a.weight_break = ' || '''' || p_weight_break || '''' || '
                         AND a.rate_type = ' || '''' || p_rate_type || '''' || '
                      AND b.activeinactive IS NULL
                      AND b.origin = ' || '''' || p_origin || '''' || '
                      AND b.destination = ' || '''' || p_destination || '''' || '
                      AND a.terminalid IN (
                             SELECT     child_terminal_id terminalid
                                   FROM FS_FR_TERMINAL_REGN
                             CONNECT BY PRIOR child_terminal_id =
                                                            parent_terminal_id
                             START WITH parent_terminal_id =' || '''' || p_current_terminal_id || '''' || '
                             UNION
                             SELECT terminalid
                               FROM FS_FR_TERMINALMASTER
                              WHERE terminalid = ' || '''' || p_current_terminal_id || '''' || '  )';
        LOOP
          FETCH V_CURR1
            INTO v_buyrateid, v_laneno;
               EXIT WHEN V_CURR1%NOTFOUND;
              v_return := '3' || ',' || v_buyrateid || ',' || v_laneno;


        END LOOP;

      END IF;

      RETURN v_return;
    END IF;
    /*EXCEPTION
    WHEN OTHERS
    THEN
    Qms_Quote_Pack.g_err      := '<< ' || SQLERRM || ' >>';
    Qms_Quote_Pack.g_err_code := '<< ' || SQLCODE || ' >>';

    INSERT INTO QMS_OBJECTS_ERRORS
      (ex_date, module_name, errorcode, errormessage)
    VALUES
      (SYSDATE,
       'QMS_QUOTE_PACK->buyrate',
       Qms_Quote_Pack.g_err_code,
       Qms_Quote_Pack.g_err);

    COMMIT;
       RETURN '0';*/
  END;

  FUNCTION comb_freq_func(p_str VARCHAR2) RETURN VARCHAR2 AS
    v_return VARCHAR2(1000) := '( FREQUENCY like  ';
    k        NUMBER;
    f        NUMBER;
    r        NUMBER := 1;
  BEGIN
    k := INSTR(p_str, '~', 1, 1) - 1;
    f := 1;
    r := 1;

    WHILE k > 0 LOOP
      IF r != 1 THEN
        f := f + 1;
      END IF;

      k        := k + 1;
      v_return := v_return || '''' || '%' || SUBSTR(p_str, f, k - f) || '%' || '''' ||
                  ' or FREQUENCY like ';
      f        := INSTR(p_str, '~', 1, r);
      r        := r + 1;
      k        := INSTR(p_str, '~', 1, r) - 1;
    END LOOP;

    v_return := v_return || '''' || '%' ||
                SUBSTR(p_str, INSTR(p_str, '~', -1) + 1, LENGTH(p_str)) || '%' || '''' || ')';
    RETURN v_return;
  END;

  /*  --------------- added by sekar  ----------------------------------------------*/
  PROCEDURE quote_view_proc(p_quoteid NUMBER,
                            p_rs      OUT resultset,
                            p_rs1     OUT resultset,
                            p_rs2     OUT resultset) AS
    v_id   NUMBER;
    v_sql1 VARCHAR2(4000);
    v_sql2 VARCHAR2(4000);
    v_sql3 VARCHAR2(4000);
    v_sql4 VARCHAR2(4000);
    v_sql5 VARCHAR2(4000);
    v_sql6 VARCHAR2(4000);
    v_sql7 VARCHAR2(4000);
  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('truncate table temp_charges');

    SELECT ID
      INTO v_id
      FROM QMS_QUOTE_MASTER
     WHERE quote_id = p_quoteid
       AND version_no = (SELECT MAX(version_no)
                           FROM QMS_QUOTE_MASTER
                          WHERE quote_id = p_quoteid);



    FOR i IN (SELECT quote_id,
                     sell_buy_flag,
                     buyrate_id,
                     sellrate_id,
                     rate_lane_no,
                     charge_id,
                     charge_description,
                     NVL(margin_discount_flag, 'M') margin_discount_flag,
                     margin_type,
                     margin,
                     discount_type,
                     discount,
                     notes,
                     quote_refno,
                     break_point,
                     charge_at,
                     buy_rate,
                     r_sell_rate,
                     rt_plan_id,
                     serial_no,
                     ID
                FROM QMS_QUOTE_RATES
               WHERE quote_id = v_id) LOOP
      IF UPPER(i.sell_buy_flag) = 'BR' THEN

        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR) SELECT distinct ';
        v_sql2 := '''' || i.charge_at || '''' ||
                  ' ,qbd.weight_break_slab, qbm.currency,qbd.chargerate,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', qbd.chargerate + qbd.chargerate * 0.01 *' ||
                  i.margin;
        v_sql4 := ',qbd.chargerate + ' || i.margin ||
                  ' ) ELSE 0.0 END,''Per ''' || '||' || 'qbm.uom,' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',qbm.buyrateid,qbd.effective_date, qbd.valid_upto, qbm.terminalid,qbm.weight_break, qbm.weight_class, qbm.rate_type,qbd.frequency, qbd.transit_time, qbd.notes,' ||
                  ' qbm.uom, FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,qbd.LOWERBOUND,qbd.UPPERBOUND,qbd.CHARGERATE_INDICATOR  FROM qms_buyrates_master qbm, qms_buyrates_dtl qbd,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qbd.buyrateid = qbm.buyrateid AND qbd.buyrateid = ';
        v_sql6 := i.buyrate_id || ' AND qbd.lane_no =' || i.rate_lane_no ||
                  ' AND QBD.service_level not in (''SCH'') AND QBD.WEIGHT_BREAK_SLAB=' || '''' ||
                  i.break_point || '''' || ' AND FRL.SERIAL_NO=' ||
                  i.serial_no ||
                  ' AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=' ||
                  p_quoteid;

        --               || '  ';
        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
      ELSIF (UPPER(i.sell_buy_flag) = 'RSR' OR
            UPPER(i.sell_buy_flag) = 'CSR') THEN

        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,SELLCHARGEID,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR) SELECT  distinct ';
        v_sql2 := '''' || i.charge_at || '''' ||
                  ' ,RSD.WEIGHTBREAKSLAB,RSM.currency,RSD.chargerate,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', RSD.chargerate + RSD.chargerate * 0.01 *' ||
                  i.margin;
        v_sql4 := ',RSD.chargerate + ' || i.margin ||
                  ' ) ELSE 0.0 END,''Per ''' || '||' || 'qbm.uom,' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',RSD.BUYRATEID,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                  ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR  FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,qms_buyrates_master qbm, qms_buyrates_dtl qbd,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';
        v_sql6 := i.sellrate_id || ' AND RSD.LANE_NO =' || i.rate_lane_no ||
                  ' AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID AND  QBD.SERVICE_LEVEL not in (''SCH'') AND RSD.WEIGHTBREAKSLAB=' || '''' ||
                  i.break_point || '''' || ' AND FRL.SERIAL_NO=' ||
                  i.serial_no ||
                  ' AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=' ||
                  p_quoteid;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
      ELSIF UPPER(i.sell_buy_flag) = 'SBR' THEN
        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab,buyrate,sellrate, chargebasis, sel_buy_flag,efrom, validupto,terminalid,weight_break,weight_scale, rate_type,primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR) SELECT  distinct ';
        v_sql2 := '''' || i.charge_at || '''' ||
                  ' ,QSB.WEIGHT_BREAK_SLAB,QSB.CHARGE_RATE,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', QSB.CHARGE_RATE + QSB.CHARGE_RATE * 0.01 *' ||
                  i.margin;
        v_sql4 := ',QSB.CHARGE_RATE + ' || i.margin ||
                  ' ) ELSE 0.0 END,''Per ''' || '||' || 'QSB.UOM,' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',QM.EFFECTIVE_DATE, QM.VALID_TO, QM.TERMINAL_ID,QSB.WEIGHT_BREAK, ''G'', QSB.WEIGHT_BREAK,' ||
                  ' QSB.uom,FRL.ORIG_LOC, FRL.DEST_LOC, FRL.SHPMNT_MODE, QSB.SERVICELEVEL, FRL.SERIAL_NO, QSB.DENSITY_CODE,qsb.LOWER_BOUND,qsb.UPPER_BOUND,decode(upper(QSB.WEIGHT_BREAK_SLAB),''SLAB'',''SLAB'')) FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_SPOTRATES QSB,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE  QSB.QUOTE_ID=QM.ID';
        v_sql6 := ' AND QM.ID=' || i.quote_id ||
                  ' AND QSB.SERVICELEVEL NOT IN (''SCH'') AND QSB.WEIGHT_BREAK_SLAB=' || '''' ||
                  i.break_point || '''' || ' AND FRL.SERIAL_NO=' ||
                  i.serial_no ||
                  ' AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=' ||
                  p_quoteid;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
        -- Freight Rates end

        -- Charges begin
      ELSIF UPPER(i.sell_buy_flag) = 'B' THEN

        v_sql1 := 'INSERT INTO temp_charges (CHARGE_ID, CHARGEDESCID,cost_incurredat, chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR) SELECT distinct ';
        v_sql2 := '''' || i.charge_id || '''' || ',' || '''' ||
                  i.charge_description || '''' || ',' || '''' ||
                  i.charge_at || '''' ||
                  ' ,qcd.CHARGESLAB, qcm.CURRENCY,qcd.CHARGERATE,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', qcd.CHARGERATE + qcd.CHARGERATE * 0.01 *' ||
                  i.margin;
        v_sql4 := ',qcd.CHARGERATE + ' || i.margin || ' ) ELSE 0.0 END,' ||
                  '(select BASIS_DESCRIPTION from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) uom,' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',qcm.BUYSELLCHARGEID, qcm.TERMINALID,qcm.RATE_BREAK, qcm.WEIGHT_CLASS, qcm.rate_type,' ||
                  ' (select PRIMARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) primary_basis, (select SECONDARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) SECONDARY_BASIS,(select TERTIARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) TERTIARY_BASIS,(select BLOCK from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) BLOCK,Qcm.DENSITY_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND,qcd.CHARGERATE_INDICATOR  FROM QMS_BUYSELLCHARGESMASTER qcm,QMS_BUYCHARGESDTL qcd WHERE qcm.BUYSELLCHARGEID=';
        v_sql6 := i.buyrate_id || ' AND Qcd.CHARGESLAB=' || '''' ||
                  i.break_point || '''' ||
                  ' AND QCM.BUYSELLCHARGEID=QCD.BUYSELLCHAEGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
      ELSIF UPPER(i.sell_buy_flag) = 'S' THEN
        v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, CHARGE_ID, CHARGEDESCID,COST_INCURREDAT,chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK,LBOUND, UBOUND, RATE_INDICATOR ) SELECT distinct ';
        v_sql2 := '''' || i.sellrate_id || '''' || ',' || '''' ||
                  i.charge_id || '''' || ',' || '''' ||
                  i.charge_description || '''' || ',' || '''' ||
                  i.charge_at || '''' ||
                  ' ,QSd.CHARGESLAB, qsm.CURRENCY,qsd.CHARGERATE,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', qsd.CHARGERATE + qsd.CHARGERATE * 0.01 *' ||
                  i.margin;
        v_sql4 := ',qsd.CHARGERATE + ' || i.margin || ' ) ELSE 0.0 END,' ||
                  '(select BASIS_DESCRIPTION from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qsm.CHARGEBASIS) basis,' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',(SELECT BUYSELLCHARGEID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGE_ID=' || '''' ||
                  i.charge_id || '''' || ' AND CHARGEDESCID=' || '''' ||
                  i.charge_description || '''' ||
                  ' AND DEL_FLAG=''N'' AND TERMINALID =qsm.TERMINALID), qsm.TERMINALID,qsm.RATE_BREAK, qsm.WEIGHT_CLASS, qsm.rate_type,' ||
                  ' (select PRIMARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qsm.CHARGEBASIS) primary_basis, (select SECONDARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qsm.CHARGEBASIS) SECONDARY_BASIS,(select TERTIARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qsm.CHARGEBASIS) TERTIARY_BASIS,(select BLOCK from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qsm.CHARGEBASIS) BLOCK,qsd.LOWERBOUND, qsd.UPPERBOUND, qsd.CHARGERATE_INDICATOR FROM QMS_SELLCHARGESDTL QSD,QMS_SELLCHARGESMASTER QSM WHERE qsm.SELLCHARGEID=';
        v_sql6 := i.sellrate_id || ' AND Qsd.CHARGESLAB=' || '''' ||
                  i.break_point || '''' ||
                  ' AND QsM.SELLCHARGEID=QsD.SELLCHARGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
      ELSIF UPPER(i.sell_buy_flag) = 'BC' THEN
        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QCD.ZONE_CODE =QM.SHIPPERZONES';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QCD.ZONE_CODE =QM.CONSIGNEEZONES';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (COST_INCURREDAT,chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR) SELECT distinct ';
        v_sql2 := '''' || i.charge_at || '''' ||
                  ' ,QCD.CHARGESLAB, QCM.CURRENCY,QCD.CHARGERATE,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', QCD.CHARGERATE + QCD.CHARGERATE * 0.01 *' ||
                  i.margin;
        v_sql4 := ',QCD.CHARGERATE + ' || i.margin || ' ) ELSE 0.0 END,' || '''' ||
                  'Per ' || '''' || '||qcm.uom' || ',' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',QCD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,' ||
                  'qcm.uom' ||
                  ',QCD.DENSITY_CODE,QCD.ZONE_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND,qcd.CHARGERATE_INDICATOR FROM QMS_CARTAGE_BUYDTL QCD,QMS_CARTAGE_BUYSELLCHARGES QCM,QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QCD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := i.buyrate_id || ' AND QCD.CHARGESLAB=' || '''' ||
                  i.break_point || '''' || ' AND QM.ID=' || i.quote_id ||
                  v_sql7;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6);
      ELSIF UPPER(i.sell_buy_flag) = 'SC' THEN
        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QCD.ZONE_CODE =QM.SHIPPERZONES';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QCD.ZONE_CODE =QM.CONSIGNEEZONES';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, COST_INCURREDAT, chargeslab, currency, buyrate,sellrate, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR) SELECT distinct ';
        v_sql2 := '''' || i.sellrate_id || '''' || i.charge_at || '''' ||
                  ' ,QSD.CHARGESLAB, QCM.CURRENCY,QSD.CHARGERATE,CASE WHEN (   UPPER (';
        v_sql3 := '''' || i.margin_discount_flag || '''' || ') = ' || '''' || 'M' || '''' ||
                  ' ) THEN DECODE (' || '''' || i.margin_type || '''' ||
                  ',''P'', QSD.CHARGERATE + QSD.CHARGERATE * 0.01 *' ||
                  i.margin;
        v_sql4 := ',QSD.CHARGERATE + ' || i.margin || ' ) ELSE 0.0 END,' || '''' ||
                  'Per ' || '''' || '||qcm.uom' || ',' || '''' ||
                  i.sell_buy_flag || '''';
        v_sql5 := ',QSD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,' ||
                  'qcm.uom' ||
                  ',QSD.DENSITY_CODE,QSD.ZONE_CODE,qsd.LOWERBOUND, qsd.UPPERBOUND,decode(upper(QCM.WEIGHT_BREAK),''SLAB'',''SLAB'') FROM QMS_CARTAGE_SELLDTL QSD,QMS_CARTAGE_BUYSELLCHARGES QCM,QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QSD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := i.buyrate_id || ' AND QSD.CHARGESLAB=' || '''' ||
                  i.break_point || '''' || ' AND QM.ID=' || i.quote_id ||
                  v_sql7;
      END IF;
    END LOOP;

    OPEN p_rs FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BR'',''RSR'',''CSR'',''SBR'')';

    OPEN p_rs1 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''B'',''C'')';

    OPEN p_rs2 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BC'',''SC'')';
  EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.put_line(SQLERRM);
  END;

  /* FUNCTION validate_density_ratio (p_density VARCHAR2,p_shipmemt_mode VARCHAR2)
     RETURN NUMBER
  IS
     v_kgs       VARCHAR2 (50);
     v_pnds      VARCHAR2 (50);
     v_dgccode   NUMBER;
  BEGIN
     v_kgs := SUBSTR (p_density, 1, INSTR (p_density, ':') - 1);
     v_pnds :=
           SUBSTR (p_density, INSTR (p_density, ':') + 1,
                   LENGTH (p_density));


     SELECT dgccode
       INTO v_dgccode
       FROM QMS_DENSITY_GROUP_CODE
      WHERE kg_per_m3 = TO_NUMBER (v_kgs)
        AND lb_per_f3 = TO_NUMBER (v_pnds)
        AND DGCCODE = p_shipmemt_mode  AND ( UPPER (INVALIDATE) = 'F' OR INVALIDATE IS NULL);

     RETURN 1;
  EXCEPTION
     WHEN NO_DATA_FOUND
     THEN
        RETURN 0;
  END;*/
  /*  Validates the density ratio.
   *  It is passing the in parameters are
   *  p_density         VARCHAR2,
   *  p_shipmemt_mode   VARCHAR2,
   *  p_uom             VARCHAR2,
   *  out parameter is
   *  RETURN            NUMBER.
  */
  FUNCTION validate_density_ratio(p_density       VARCHAR2,
                                  p_shipmemt_mode VARCHAR2,
                                  p_uom           VARCHAR2) RETURN NUMBER IS

    v_cnt NUMBER(5);

  BEGIN

    /*EXECUTE IMMEDIATE ( 'SELECT COUNT(*) FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE=''F'' AND DGCCODE = '
    ||''''||p_shipmemt_mode||''''||' AND  DECODE('
    ||''''||p_uom||''''
    ||',''KG'',KG_PER_M3,''CBM'',KG_PER_M3,''LB'',LB_PER_F3,''CFT'',LB_PER_F3)= '
    ||''''||p_density||'''') INTO v_cnt;*/
    SELECT COUNT(*)
      INTO v_Cnt
      FROM QMS_DENSITY_GROUP_CODE
     WHERE Invalidate = 'F'
       AND Dgccode = p_Shipmemt_Mode
       AND DECODE(p_Uom,
                  'KG',
                  Kg_Per_M3,
                  'CBM',
                  Kg_Per_M3,
                  'LB',
                  Lb_Per_F3,
                  'CFT',
                  Lb_Per_F3) = p_Density;

    IF v_cnt > 0 THEN
      RETURN 1;
    END IF;

    RETURN 0;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 0;
  END;

  --@@ For BuyRates Upload
  /*  Used for inactivating the lower level buy rates in case of higher level adding them for a
   *  particular combination and calls the quote updating procedures.
   *  It is passing the in parameters are
   *  p_shipment_mode         VARCHAR2,
   *  p_origin                VARCHAR2,
   *  p_destination           VARCHAR2,
   *  p_servicelevel_id       VARCHAR2,
   *  p_carrier_id            VARCHAR2,
   *  p_effective_from        DATE,
   *  p_validate_upto         DATE,
   *  p_weight_break          VARCHAR2,
   *  p_rate_type             VARCHAR2,
   *  p_current_terminal_id   VARCHAR2,
   *  p_frequency             VARCHAR2,
   *  p_newbuyrateid          VARCHAR2,
   *  p_newlaneno             VARCHAR2.
  */
  PROCEDURE Qms_Inactivate_Buyrate(p_Shipment_Mode       VARCHAR2,
                                   p_Origin              VARCHAR2,
                                   p_Destination         VARCHAR2,
                                   p_Servicelevel_Id     VARCHAR2,
                                   p_Carrier_Id          VARCHAR2,
                                   p_Effective_From      DATE,
                                   p_Validate_Upto       DATE,
                                   p_Weight_Break        VARCHAR2,
                                   p_Rate_Type           VARCHAR2,
                                   p_Current_Terminal_Id VARCHAR2,
                                   p_Frequency           VARCHAR2,
                                   p_Newbuyrateid        VARCHAR2,
                                   p_Newlaneno           VARCHAR2) AS
    v_Accesslevel      VARCHAR2(10);
    v_Shipmodestr      VARCHAR2(10);
    v_Curr             Sys_Refcursor;
    v_Buyrateid        VARCHAR2(30);
    v_Laneno           VARCHAR2(10);
    v_Frequency        VARCHAR2(2000);
    v_Service_Level_Id VARCHAR2(8);
    v_Carrier_Id       VARCHAR2(5);
    v_Shipmode         VARCHAR2(1);
    v_Weight_Break     VARCHAR2(8);
    v_Rate_Type        VARCHAR2(8);
    v_Origin           VARCHAR2(5);
    v_Destination      VARCHAR2(5);
    v_Terminal_Id      VARCHAR2(10);
    v_versionno        VARCHAR2(10);
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Accesslevel
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = p_Current_Terminal_Id;
    --v_Frequency := Pkg_Qms_Buyrates.Comb_Freq_Func(p_Frequency);
    IF p_Shipment_Mode = '1' THEN
      v_Shipmodestr := 'Air';
    ELSIF p_Shipment_Mode = '2' THEN
      v_Shipmodestr := 'Sea';
    ELSE
      v_Shipmodestr := 'Truck';
    END IF;
    IF v_Accesslevel = 'H' THEN
      /*OPEN V_CURR FOR  'SELECT  a.buyrateid buyrateid, b.lane_no lane_no'
      ||' FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b'
      ||' WHERE b.buyrateid = a.buyrateid'
      ||' AND b.service_level =:v_service_level_id AND b.line_no=''0'''
      ||' AND b.carrier_id =:v_carrier_id'
      ||' AND a.shipment_mode =:v_shipmode'
      ||' AND a.weight_break =:v_weight_break'
      ||' AND '||v_frequency
      ||' AND a.rate_type =:v_rate_type'
      ||' AND b.activeinactive IS NULL '
      ||' AND b.origin =:v_origin'
      ||' AND b.destination =:v_destination' Using p_servicelevel_id,p_carrier_id,
      p_shipment_mode,p_weight_break,p_rate_type,p_origin,p_destination;*/
      --||' AND a.terminalid IN (SELECT terminalid FROM FS_FR_TERMINALMASTER  )';
      IF p_Shipment_Mode = '2' THEN
        OPEN v_Curr FOR
        /*Select Distinct a.Buyrateid Buyrateid, b.Lane_No Lane_No*/
          SELECT DISTINCT a.Buyrateid  Buyrateid,
                          b.Lane_No    Lane_No,
                          b.version_no version_no --@@Modified for the WPBN issues-146448,146968 on 19/12/08
            FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
           WHERE b.Buyrateid = a.Buyrateid
             AND b.Service_Level = p_Servicelevel_Id
             AND b.line_no = '0'
             AND b.Carrier_Id = p_Carrier_Id
             AND a.Shipment_Mode = p_Shipment_Mode
             AND a.Weight_Break = p_Weight_Break
             AND a.Rate_Type = p_Rate_Type
             AND b.Activeinactive IS NULL
             AND b.Origin = p_Origin
             AND b.Destination = p_Destination
             AND Frequency = p_Frequency;
      ELSE
        OPEN v_Curr FOR
        /*'Select Distinct+ FIRST_ROWS  a.Buyrateid Buyrateid, b.Lane_No Lane_No*/
         'Select Distinct  a.Buyrateid Buyrateid, b.Lane_No Lane_No,b.version_no version_no --@@Modified for the WPBN issues-146448,146968 on 19/12/08
             FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
            WHERE b.Buyrateid = a.Buyrateid
              AND b.Service_Level = :p_Servicelevel_Id
              AND b.line_no=''0''
              AND b.Carrier_Id = :p_Carrier_Id
              AND a.Shipment_Mode = :p_Shipment_Mode
              AND a.Weight_Break = :p_Weight_Break
              AND a.Rate_Type = :p_Rate_Type
              AND b.Activeinactive IS NULL
              AND b.Origin = :p_Origin
              AND b.Destination = :p_Destination
              AND EXISTS
            (SELECT ''X''
                     FROM QMS_BUYRATES_FREQ
                    WHERE Buyrateid = b.Buyrateid
                      AND Lane_No = b.Lane_No
                      AND Frequency IN (' || Qms_Rsr_Rates_Pkg.seperator(p_Frequency) || '))'
          USING p_Servicelevel_Id, p_Carrier_Id, p_Shipment_Mode, p_Weight_Break, p_Rate_Type, p_Origin, p_Destination;

        /*(Select Column_Value
         From Table(Inlist(p_Frequency))
        Where Rownum >= 0));*/
      END IF;
      LOOP
        FETCH v_Curr
        --Into v_Buyrateid, v_Laneno;
          INTO v_Buyrateid, v_Laneno, v_versionno; --@@Modified for the WPBN issues-146448,146968 on 19/12/08
        EXIT WHEN v_Curr%NOTFOUND;
        /*Qms_Quotepack_New.Qms_Quote_Update(Null,
        v_Buyrateid,
        v_Laneno,
        Null,
        p_Newbuyrateid,
        p_Newlaneno,
        'BR',
        Null,
        Null,
        p_Origin || '-' || p_Destination || ',' ||
        v_Shipmodestr ||
        ' Freight Rates');*/
        Qms_Quotepack_New.Qms_Quote_Update(NULL,
                                           v_Buyrateid,
                                           v_Laneno,
                                           v_versionno, --@@Added for the WPBN issues-146448,146968 on 19/12/08
                                           NULL,
                                           p_Newbuyrateid,
                                           p_Newlaneno,
                                           v_versionno + 1, --@@Added for the WPBN issues-146448,146968 on 19/12/08
                                           'BR',
                                           NULL,
                                           NULL,
                                           p_Origin || '-' || p_Destination || ',' ||
                                           v_Shipmodestr ||
                                           ' Freight Rates');

        UPDATE QMS_BUYRATES_DTL
           SET Activeinactive = 'I'
         WHERE Buyrateid = v_Buyrateid
           AND Lane_No = v_Laneno
           AND Version_No = v_versionno; --@@Added for the WPBN issues-146448,146968 on 19/12/08

        UPDATE QMS_REC_CON_SELLRATESDTL Dtl
           SET Acceptance_Flag = 'Y'
         WHERE Buyrateid = v_Buyrateid
           AND Lane_No = v_Laneno
           AND Dtl.Ai_Flag = 'A'
           AND Version_No = v_versionno; --@@Added for the WPBN issues-146448,146968 on 19/12/08
        /*
             Inactivating CSR as currently it has no acceptance module
        */
        UPDATE QMS_REC_CON_SELLRATESDTL Dtl
           SET Dtl.Ai_Flag = 'I'
         WHERE Dtl.Buyrateid = v_Buyrateid
           AND Dtl.Lane_No = v_Laneno
           AND Dtl.Ai_Flag = 'A'
           AND Dtl.version_no = v_versionno --@@Added for the WPBN issues-146448,146968 on 19/12/08
           AND EXISTS (SELECT 'X'
                  FROM QMS_REC_CON_SELLRATESMASTER Mas
                 WHERE Mas.Rc_Flag = 'C'
                   AND Mas.Rec_Con_Id = Dtl.Rec_Con_Id
                   AND Dtl.Buyrateid = v_Buyrateid);
      END LOOP;
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE (Rec_Con_Id, Lane_No) IN
             (SELECT a.Rec_Con_Id Recid, b.Lane_No Lane_No
                FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                     QMS_REC_CON_SELLRATESDTL_ACC  b
               WHERE b.Rec_Con_Id = a.Rec_Con_Id
                 AND b.Servicelevel_Id = p_Servicelevel_Id
                 AND b.line_no = '0'
                 AND b.Carrier_Id = p_Carrier_Id
                 AND a.Shipment_Mode = p_Shipment_Mode
                 AND a.Weight_Break = p_Weight_Break
                    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
                 AND a.Rate_Type = p_Rate_Type
                 AND (b.Ai_Flag IS NULL OR b.Ai_Flag = 'A')
                    --AND (B.INVALIDATE='F' OR B.INVALIDATE IS NULL)
                 AND b.Origin = p_Origin
                 AND b.Destination = p_Destination
              /*AND a.terminalid IN (
                                                                                                                         SELECT DISTINCT terminalid
                                                                                                                                    FROM FS_FR_TERMINALMASTER
                                                                                                                                   WHERE actv_flag = 'A'
                                                                                                                                     AND (   INVALIDATE = 'F'
                                                                                                                                          OR INVALIDATE IS NULL
                                                                                                                                         ))*/
              );
      CLOSE v_Curr;
    ELSE
      /*OPEN V_CURR FOR  'SELECT  a.buyrateid buyrateid, b.lane_no lane_no'
      ||' FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b'
      ||' WHERE b.buyrateid = a.buyrateid'
      ||' AND b.service_level =:v_service_level_id AND b.line_no=''0'''
      ||' AND b.carrier_id =:v_carrier_id'
      ||' AND a.shipment_mode =:v_shipmode'
      ||' AND a.weight_break =:v_weight_break'
      ||' AND '||v_frequency
      ||' AND a.rate_type =:v_rate_type'
      ||' AND b.activeinactive IS NULL '
      ||' AND b.origin =:v_origin'
      ||' AND b.destination =:v_destination'
      ||' AND a.terminalid IN ('
      ||' SELECT  child_terminal_id terminalid '
      ||' FROM FS_FR_TERMINAL_REGN'
      ||' CONNECT BY PRIOR child_terminal_id = parent_terminal_id'
      ||' START WITH parent_terminal_id =:v_terminal_id'
      ||' UNION'
      ||' SELECT terminalid'
      ||' FROM FS_FR_TERMINALMASTER'
      ||' WHERE terminalid =: v_terminal_id'
      ||'  )' Using p_servicelevel_id,p_carrier_id,
       p_shipment_mode,p_weight_break,p_rate_type,p_origin,p_destination,p_current_terminal_id,p_current_terminal_id ;*/
      IF p_Shipment_Mode = '2' THEN
        OPEN v_Curr FOR
        /* Select a.Buyrateid Buyrateid, b.Lane_No Lane_No*/ --@@Modified for the WPBN issues-146448,146968 on 19/12/08
          SELECT a.Buyrateid  Buyrateid,
                 b.Lane_No    Lane_No,
                 b.version_no version_no
            FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
           WHERE b.Buyrateid = a.Buyrateid
             AND b.Service_Level = p_Servicelevel_Id
             AND b.line_no = '0'
             AND b.Carrier_Id = p_Carrier_Id
             AND a.Shipment_Mode = p_Shipment_Mode
             AND a.Weight_Break = p_Weight_Break
             AND a.Rate_Type = p_Rate_Type
             AND b.Activeinactive IS NULL
             AND b.Origin = p_Origin
             AND b.Destination = p_Destination
             AND Frequency = p_Frequency
             AND a.Terminalid IN
                 (SELECT Child_Terminal_Id Terminalid
                    FROM FS_FR_TERMINAL_REGN
                  CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                   START WITH Parent_Terminal_Id = p_Current_Terminal_Id
                  UNION
                  SELECT Terminalid
                    FROM FS_FR_TERMINALMASTER
                   WHERE Terminalid = p_Current_Terminal_Id);
      ELSE
        OPEN v_Curr FOR
        /*'Select   a.Buyrateid Buyrateid, b.Lane_No Lane_No*/ --@@Modified for the WPBN issues-146448,146968 on 19/12/08
         'Select   a.Buyrateid Buyrateid, b.Lane_No Lane_No,b.version_no version_no
             FROM QMS_BUYRATES_MASTER a, QMS_BUYRATES_DTL b
            WHERE b.Buyrateid = a.Buyrateid
              AND b.Service_Level = :p_Servicelevel_Id
               AND b.line_no=''0''
              AND b.Carrier_Id = :p_Carrier_Id
              AND a.Shipment_Mode = :p_Shipment_Mode
              AND a.Weight_Break = :p_Weight_Break
              AND a.Rate_Type = :p_Rate_Type
              AND b.Activeinactive IS NULL
              AND b.Origin = :p_Origin
              AND b.Destination = :p_Destination
              AND EXISTS
            (SELECT ''X''
                     FROM QMS_BUYRATES_FREQ
                    WHERE Buyrateid = b.Buyrateid
                      AND Lane_No = b.Lane_No
                      AND Frequency IN (' || Qms_Rsr_Rates_Pkg.Seperator(p_Frequency) || '))
              AND a.Terminalid IN
                  (SELECT Child_Terminal_Id Terminalid
                     FROM FS_FR_TERMINAL_REGN
                   CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                    START WITH Parent_Terminal_Id = :p_Current_Terminal_Id
                   UNION
                   SELECT Terminalid
                     FROM FS_FR_TERMINALMASTER
                    WHERE Terminalid = :p_Current_Terminal_Id)'
          USING p_Servicelevel_Id, p_Carrier_Id, p_Shipment_Mode, p_Weight_Break, p_Rate_Type, p_Origin, p_Destination, p_Current_Terminal_Id, p_Current_Terminal_Id;

        /*Select Column_Value
         From Table(Inlist(p_Frequency))
        Where Rownum >= 0)*/
      END IF;
      LOOP
        FETCH v_Curr
        --Into v_Buyrateid, v_Laneno;--@@Modified for the WPBN issues-146448,146968 on 19/12/08
          INTO v_Buyrateid, v_Laneno, v_versionno;
        EXIT WHEN v_Curr%NOTFOUND;
        --if upper(i.WEIGHT_BREAK) = upper(p_weight_break) then
        /*Qms_Quotepack_New.Qms_Quote_Update(Null,
        v_Buyrateid,
        v_Laneno,
        Null,
        p_Newbuyrateid,
        p_Newlaneno,
        'BR',
        Null,
        Null,
        p_Origin || '-' || p_Destination || ',' ||
        v_Shipmodestr ||
        ' Freight Rates');*/

        --@@Modified for the WPBN issues-146448,146968 on 19/12/08
        Qms_Quotepack_New.Qms_Quote_Update(NULL,
                                           v_Buyrateid,
                                           v_Laneno,
                                           v_versionno,
                                           NULL,
                                           p_Newbuyrateid,
                                           p_Newlaneno,
                                           v_versionno + 1,
                                           'BR',
                                           NULL,
                                           NULL,
                                           p_Origin || '-' || p_Destination || ',' ||
                                           v_Shipmodestr ||
                                           ' Freight Rates');
        -- end if;
        UPDATE QMS_BUYRATES_DTL
           SET Activeinactive = 'I'
         WHERE Buyrateid = v_Buyrateid
           AND Lane_No = v_Laneno
           AND version_no = v_versionno; --@@Added for the WPBN issues-146448,146968 on 19/12/08
        UPDATE QMS_REC_CON_SELLRATESDTL Dtl
           SET Acceptance_Flag = 'Y'
         WHERE Buyrateid = v_Buyrateid
           AND Lane_No = v_Laneno
           AND Dtl.Ai_Flag = 'A'
           AND version_no = v_versionno; --@@Added for the WPBN issues-146448,146968 on 19/12/08
        /*
             Inactivating CSR as currently it has no acceptance module
        */
        UPDATE QMS_REC_CON_SELLRATESDTL Dtl
           SET Dtl.Ai_Flag = 'I'
         WHERE Dtl.Buyrateid = v_Buyrateid
           AND Dtl.Lane_No = v_Laneno
           AND Dtl.version_no = v_versionno --@@Added for the WPBN issues-146448,146968 on 19/12/08
           AND Dtl.Ai_Flag = 'A'
           AND EXISTS (SELECT 'X'
                  FROM QMS_REC_CON_SELLRATESMASTER Mas
                 WHERE Mas.Rc_Flag = 'C'
                   AND Mas.Rec_Con_Id = Dtl.Rec_Con_Id
                   AND Dtl.Buyrateid = v_Buyrateid);
        /*
          --
        */
      END LOOP;
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE (Rec_Con_Id, Lane_No) IN
             (SELECT a.Rec_Con_Id Recid, b.Lane_No Lane_No
                FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                     QMS_REC_CON_SELLRATESDTL_ACC  b
               WHERE b.Rec_Con_Id = a.Rec_Con_Id
                 AND b.Servicelevel_Id = p_Servicelevel_Id
                 AND b.line_no = '0'
                 AND b.Carrier_Id = p_Carrier_Id
                 AND a.Shipment_Mode = p_Shipment_Mode
                 AND a.Weight_Break = p_Weight_Break
                    --AND ((B.EFFECTIVE_FROM BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) OR (B.VALID_UPTO BETWEEN P_EFFECTIVE_FROM AND P_VALIDATE_UPTO) )
                 AND a.Rate_Type = p_Rate_Type
                 AND (b.Ai_Flag IS NULL OR b.Ai_Flag = 'A')
                    --AND (B.INVALIDATE='F' OR B.INVALIDATE IS NULL)
                 AND b.Origin = p_Origin
                 AND b.Destination = p_Destination
                 AND a.Terminalid IN
                     (SELECT Child_Terminal_Id Terminalid
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                       START WITH Parent_Terminal_Id = p_Current_Terminal_Id
                      UNION
                      SELECT Terminalid
                        FROM FS_FR_TERMINALMASTER
                       WHERE Terminalid = p_Current_Terminal_Id));
      CLOSE v_Curr;
    END IF;
  END;
   PROCEDURE qms_update_new_quote(p_sellbuyflag VARCHAR2,

                                 p_origin VARCHAR2,

                                 p_destination VARCHAR2,

                                 p_servicelevel_id VARCHAR2,

                                 p_carrier_id VARCHAR2,

                                 p_validate_upto DATE,

                                 p_frequency VARCHAR2,

                                 p_transittime VARCHAR2,

                                 p_newbuyrateid VARCHAR2,

                                 p_newsellrateid VARCHAR2,

                                 p_oldsellrateid VARCHAR2,

                                 p_newversionno NUMBER,

                                 p_newlaneno NUMBER,

                                 p_terminalid VARCHAR2) AS--@@Modified by subrahmanyam for issue on 17-apr-09
    v_buyrateid     NUMBER;
    v_laneno        NUMBER;
    v_quote         NUMBER;
    v_count         NUMBER;
    v_temp          NUMBER := 0;
    n               NUMBER := 0;
    v_totaltemp     NUMBER := 0;
    v_totalcount    NUMBER := 0;
    v_validupto     VARCHAR2(32767);
    v_validupto1    VARCHAR2(32767);
    v_marginbasis   VARCHAR2(32767);
    v_margintype    VARCHAR2(32767);
    v_rec_con_id    NUMBER := 0;
    v_weightclass   VARCHAR2(32767);
    v_overallmargin VARCHAR2(32767);
    v_frequency     VARCHAR2(32767);
    v_seq           VARCHAR2(32767);
    v_id            VARCHAR2(32767);
    v_currency      VARCHAR2(32767);
    v_accesslevel   VARCHAR2(32767);
    v_terminalid    VARCHAR2(32767);
    v_shmode        VARCHAR2(32767);
    v_weightbreak   VARCHAR2(32767);
    v_ratetype      VARCHAR2(32767);
    v_transittime   VARCHAR2(32767);
    v_versionno     VARCHAR2(32767);
    m               NUMBER(10);
    v_QuoteId       VARCHAR2(32767);
    v_QuoteId1      VARCHAR2(32767);
    v_mode          VARCHAR2(32767);
    v_changedesc    VARCHAR2(32767);
    v_validCount    VARCHAR2(32767);
    v_curr          sys_refcursor;
    v_newversionno  VARCHAR2(32767);
    v_break          VARCHAR2(32767);
    v_type          VARCHAR2(32767);
    v_validate_upto VARCHAR2(32767);
     v_shipmode        VARCHAR2(32767);
     v_accessflag    VARCHAR2(32767);--@@ Added by Subrahmanyam on 17-apr-09
     v_terminal      VARCHAR2(32767);--@@ Added by Subrahmanyam on 17-apr-09
     v_margin_perc   NUMBER(20,6);----Added by subrahmanyam for security charge missing issue
  BEGIN
    SELECT DISTINCT shipment_mode,weight_break,rate_type
      INTO v_mode,v_break,v_type
      FROM QMS_BUYRATES_MASTER
     WHERE buyrateid = p_newbuyrateid
     AND (lane_no=p_newlaneno OR lane_no IS NULL)
       AND version_no = p_newversionno-1;
  v_newversionno :=p_newversionno-1;
  IF v_mode='1'
  THEN
  v_shmode :='Air';
  ELSIF v_mode='2'
  THEN
  v_shmode :='Sea';
  ELSIF v_mode='4'
  THEN
   v_shmode :='Truck';
   ELSE
   v_shmode :='Multi Modal';
   END IF;
  IF v_mode='1' AND v_break!='LIST'
  THEN
  v_frequency := Pkg_Qms_Buyrates.comb_freq_func(p_frequency);
  ELSE
  v_frequency :=' FREQUENCY = '||''''||p_frequency||'''';
  END IF;
    --only for transit time
    IF p_validate_upto IS NOT NULL
    THEN
     v_validate_upto :=' AND to_date(QBD.valid_upto) = to_date('||''''||p_validate_upto||''''||') ';
    ELSE
     v_validate_upto :=' AND QBD.valid_upto IS NULL ';
    END IF;
    --@@Added by subrahmanyam for the issue - 167860 on 17-apr-09
    SELECT TM.OPER_ADMIN_FLAG INTO v_accessflag FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID=p_terminalid;
    IF v_accessflag ='H'
     THEN
         v_terminal :='SELECT TERMINALID FROM FS_FR_TERMINALMASTER';
       ELSE
       v_terminal :='SELECT  child_terminal_id terminalid
                                   FROM FS_FR_TERMINAL_REGN
                             CONNECT BY PRIOR child_terminal_id =
                                                            parent_terminal_id
                             START WITH parent_terminal_id =' || '''' || p_terminalid || '''' || '
                             UNION
                             SELECT terminalid
                               FROM FS_FR_TERMINALMASTER
                              WHERE terminalid = ' || '''' || p_terminalid || '''' || '';
       END IF;
--@@ Ended by subrahmanyam for the issue -167860 on 17-apr-09
    OPEN v_curr FOR
   ' SELECT max(QBD.buyrateid)    FROM QMS_BUYRATES_DTL QBD ,QMS_BUYRATES_MASTER QBM WHERE QBM.BUYRATEID=QBD.BUYRATEID AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL) AND QBM.VERSION_NO=QBD.VERSION_NO AND QBM.SHIPMENT_MODE = '||''''||v_mode||''''|| ' AND QBM.WEIGHT_BREAK = '||''''||v_break||''''||' AND QBM.RATE_TYPE='||''''||v_type||''''||' AND QBD.ORIGIN = '||''''||p_origin||''''||' AND QBD.DESTINATION = '||''''||p_destination||''''||' AND QBD.CARRIER_ID = '||''''||p_carrier_id||''''||' AND QBD.SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||' AND QBD.VERSION_NO = '||''''|| v_newversionno||''''||' AND QBD.LINE_NO = ''0'' '||' AND ' || v_frequency ||v_validate_upto ||' AND QBM.TERMINALID IN( '||v_terminal ||')';--@@Modified for the WPBN issue - 167860 by subrahmanyam on 17-apr-09
        LOOP
      FETCH  v_curr INTO v_buyrateid;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;
      CLOSE v_curr;
      /* p_origin
         AND DESTINATION = p_destination
         AND CARRIER_ID = p_carrier_id
         AND SERVICE_LEVEL = p_servicelevel_id
         AND VERSION_NO = p_newversionno - 1
         AND (INVALIDATE = 'F' OR INVALIDATE IS NULL)
         AND LINE_NO = '0' ||  v_frequency || v_validupto1;
    Exception
      When NO_DATA_FOUND Then
        v_buyrateid := '';
    End;*/
     /*into v_laneno, v_versionno
      FROM QMS_BUYRATES_DTL
     WHERE   BUYRATEID = v_buyrateid
     AND ORIGIN = p_origin
       AND DESTINATION = p_destination
       AND CARRIER_ID = p_carrier_id
       AND SERVICE_LEVEL = p_servicelevel_id
        AND VERSION_NO = p_newversionno - 1
       AND (INVALIDATE = 'F' OR INVALIDATE IS NULL)
       AND LINE_NO = '0' || v_frequency || v_validupto1;*/

   IF v_buyrateid > 0
   THEN
    OPEN v_curr FOR  ' SELECT LANE_NO, VERSION_NO FROM QMS_BUYRATES_DTL QBD WHERE BUYRATEID='||''''||v_buyrateid||''''||' AND ORIGIN = '||''''||p_origin||''''||' AND DESTINATION = '||''''||p_destination||''''||' AND CARRIER_ID = '||''''||p_carrier_id||''''||' AND SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||' AND VERSION_NO = '||''''||v_newversionno||''''||' AND LINE_NO = ''0'' '||'AND ' || v_frequency ||  v_validate_upto ;
            LOOP
      FETCH  v_curr INTO v_laneno,v_versionno;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;
      CLOSE v_curr;
  END IF;
    IF v_buyrateid > 0 THEN
      FOR j IN (SELECT chargerate, weight_break_slab
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = v_buyrateid
                   AND lane_no = v_laneno
                   AND version_no = v_versionno) LOOP

        SELECT COUNT(*)
          INTO v_count
          FROM QMS_BUYRATES_DTL
         WHERE buyrateid = p_newbuyrateid
           AND lane_no = p_newlaneno
           AND version_no = p_newversionno
           AND weight_break_slab = j.weight_break_slab
           AND chargerate = j.chargerate;
        IF v_count = 0 THEN
          v_temp := v_temp + 1;
        ELSE
          v_totalcount := v_totalcount + v_count;
          v_temp       := v_temp + 1;
        END IF;

      END LOOP;
      IF v_totalcount = v_temp THEN
        FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BUYRATE_ID = v_buyrateid
                     AND RATE_LANE_NO = v_laneno
                     AND SELL_BUY_FLAG = p_sellbuyflag
                     AND LINE_NO = '0'
                     AND (TRANSIT_CHECKED = 'on' OR TRANSIT_CHECKED = 'Y')
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.ACTIVE_FLAG = 'A') LOOP
          SELECT COUNT(*)
            INTO v_quote
            FROM QMS_QUOTES_UPDATED
           WHERE quote_id = k.quote_id
             AND SELL_BUY_FLAG = p_sellbuyflag;
          IF v_quote > 0 THEN
          IF p_sellbuyflag ='BR'
          THEN
            UPDATE QMS_QUOTES_UPDATED
               SET NEW_VERSION_NO = p_newversionno
             WHERE QUOTE_ID = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
          ELSE
           UPDATE QMS_QUOTES_UPDATED
               SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
             WHERE QUOTE_ID = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
          END IF;
          ELSE

            INSERT INTO QMS_QUOTES_UPDATED
              (QUOTE_ID,
               NEW_SELLCHARGE_ID,
               NEW_BUYCHARGE_ID,
               NEW_LANE_NO,
               NEW_VERSION_NO,
               SELL_BUY_FLAG,
               CHARGE_AT,
               CHANGEDESC,
               OLD_SELLCHARGE_ID,
               OLD_BUYCHARGE_ID,
               OLD_LANE_NO,
               OLD_VERSION_NO)
            VALUES
              (k.quote_id,
               p_newsellrateid,
               p_newbuyrateid,
               p_newlaneno,
               p_newversionno,
               p_sellbuyflag,
               'Carrier',
               p_origin || '-' || p_destination || ', Transit Time',
               k.sellrate_id,
               v_buyrateid,
               v_laneno,
               v_versionno);
          END IF;
        END LOOP;

      ELSE

      --  If v_mode = '1' or v_mode = '4' Then
          FOR j IN (SELECT chargerate, weight_break_slab
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = v_buyrateid
                       AND lane_no = v_laneno
                       AND version_no = v_versionno
                       AND service_level NOT IN ('SCH')) LOOP

            SELECT COUNT(*)
              INTO v_count
              FROM QMS_BUYRATES_DTL
             WHERE buyrateid = p_newbuyrateid
               AND lane_no = p_newlaneno
               AND version_no = p_newversionno
               AND weight_break_slab = j.weight_break_slab
               AND chargerate = j.chargerate
               AND service_level NOT IN ('SCH');
            IF v_count = 0 THEN
              v_temp := v_temp + 1;
            ELSE
              v_totalcount := v_totalcount + v_count;
              v_temp       := v_temp + 1;
            END IF;
          END LOOP;
          IF v_totalcount = v_temp THEN
            v_changedesc := p_origin || '-' || p_destination ||
                            ', Surcharges';
          ELSE
            v_changedesc := p_origin || '-' || p_destination ||
                            ',Freight Rates and  Surcharges';
          END IF;

          FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                      FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                     WHERE BUYRATE_ID = v_buyrateid
                       AND RATE_LANE_NO = v_laneno
                       AND SELL_BUY_FLAG = p_sellbuyflag
                       AND LINE_NO = '0'
                       AND QR.QUOTE_ID = QM.ID
                       AND QM.ACTIVE_FLAG = 'A') LOOP
            SELECT COUNT(*)
              INTO v_quote
              FROM QMS_QUOTES_UPDATED
             WHERE quote_id = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
            IF v_quote > 0 THEN
               IF p_sellbuyflag ='BR'
          THEN
            UPDATE QMS_QUOTES_UPDATED
               SET NEW_VERSION_NO = p_newversionno
             WHERE QUOTE_ID = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
          ELSE
           UPDATE QMS_QUOTES_UPDATED
               SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
             WHERE QUOTE_ID = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
          END IF;
            ELSE
               INSERT INTO QMS_QUOTES_UPDATED
              (QUOTE_ID,
               NEW_SELLCHARGE_ID,
               NEW_BUYCHARGE_ID,
               NEW_LANE_NO,
               NEW_VERSION_NO,
               SELL_BUY_FLAG,
               CHARGE_AT,
               CHANGEDESC,
               OLD_SELLCHARGE_ID,
               OLD_BUYCHARGE_ID,
               OLD_LANE_NO,
               OLD_VERSION_NO)
              VALUES
                (k.quote_id,
                p_newsellrateid,
                 p_newbuyrateid,
                 p_newlaneno,
                 p_newversionno,
                 p_sellbuyflag,
                 'Carrier',
                 v_changedesc,
                 k.sellrate_id,
                 v_buyrateid,
                 v_laneno,
                 v_versionno);
            END IF;
          END LOOP;
       -- End If;
      END IF;
    ELSE
   OPEN v_curr FOR
         ' SELECT max(QBD.buyrateid)    FROM QMS_BUYRATES_DTL QBD ,QMS_BUYRATES_MASTER QBM WHERE QBM.BUYRATEID=QBD.BUYRATEID AND  (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL) AND QBM.VERSION_NO=QBD.VERSION_NO AND QBM.WEIGHT_BREAK = '||''''||v_break||''''||' AND QBM.RATE_TYPE='||''''||v_type||''''||' AND QBD.ORIGIN = '||''''||p_origin||''''||' AND QBD.DESTINATION = '||''''||p_destination||''''||' AND QBD.CARRIER_ID = '||''''||p_carrier_id||''''||' AND QBD.SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||'  AND QBD.TRANSIT_TIME = '||''''||p_transittime||''''||' AND QBD.VERSION_NO = '||''''||v_newversionno||''''||' AND QBD.LINE_NO = ''0'' '||'AND ' || v_frequency ||' AND QBM.TERMINALID IN( '||v_terminal ||')';--@@ Modified by subrahmanayam for the WPBN issue- 167860 on 17-apr-09
       LOOP
      FETCH  v_curr INTO v_buyrateid;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;

        /*SELECT max(BUYRATEID)
          into v_buyrateid
          FROM QMS_BUYRATES_DTL
         WHERE ORIGIN = p_origin
           AND DESTINATION = p_destination
           AND CARRIER_ID = p_carrier_id
           AND SERVICE_LEVEL = p_servicelevel_id
           AND TRANSIT_TIME = p_transittime
           AND VERSION_NO = p_newversionno - 1 --added by VLAKSHMI
           AND (INVALIDATE = 'F' OR INVALIDATE IS NULL)
           AND LINE_NO = '0'||v_frequency;*/

      --only for valid upto
       IF v_buyrateid > 0
      THEN
      /*SELECT LANE_NO, VERSION_NO
        into v_laneno, v_versionno
        FROM QMS_BUYRATES_DTL
       WHERE ORIGIN = p_origin
         AND DESTINATION = p_destination
         AND CARRIER_ID = p_carrier_id
         AND SERVICE_LEVEL = p_servicelevel_id
         AND TRANSIT_TIME = p_transittime
         AND VERSION_NO = p_newversionno - 1 --added by VLAKSHMI
         AND (INVALIDATE = 'F' OR INVALIDATE IS NULL)
         AND LINE_NO = '0'
         AND BUYRATEID = v_buyrateid||v_frequency;*/
      OPEN v_curr FOR  ' SELECT LANE_NO, VERSION_NO FROM QMS_BUYRATES_DTL WHERE BUYRATEID='||''''||v_buyrateid||''''||' AND ORIGIN = '||''''||p_origin||''''||' AND DESTINATION = '||''''||p_destination||''''||' AND CARRIER_ID = '||''''||p_carrier_id||''''||' AND SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||' AND VERSION_NO = '||''''||v_newversionno||''''||' AND LINE_NO = ''0'' '||'AND ' || v_frequency;
      LOOP
      FETCH  v_curr INTO v_laneno,v_versionno;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;
      CLOSE v_curr;
       END IF;
      IF v_buyrateid > 0 THEN
        FOR j IN (SELECT chargerate, weight_break_slab
                    FROM QMS_BUYRATES_DTL
                   WHERE buyrateid = v_buyrateid
                     AND lane_no = v_laneno
                     AND version_no = v_versionno) LOOP

          SELECT COUNT(*)
            INTO v_count
            FROM QMS_BUYRATES_DTL
           WHERE buyrateid = p_newbuyrateid
             AND lane_no = p_newlaneno
             AND version_no = p_newversionno
             AND weight_break_slab = j.weight_break_slab
             AND chargerate = j.chargerate;
          IF v_count = 0 THEN
            v_temp := v_temp + 1;
          ELSE
            v_totalcount := v_totalcount + v_count;
            v_temp       := v_temp + 1;
          END IF;

        END LOOP;
        IF v_totalcount = v_temp THEN
          FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                      FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                     WHERE BUYRATE_ID = v_buyrateid
                       AND RATE_LANE_NO = v_laneno
                       AND SELL_BUY_FLAG = p_sellbuyflag
                       AND LINE_NO = '0'
                       AND (VALIDITY_CHECKED = 'on' OR
                           VALIDITY_CHECKED = 'Y')
                       AND QM.ID = QR.QUOTE_ID
                       AND QM.ACTIVE_FLAG = 'A') LOOP
            SELECT COUNT(*)
              INTO v_quote
              FROM QMS_QUOTES_UPDATED
             WHERE quote_id = k.quote_id
               AND SELL_BUY_FLAG = p_sellbuyflag;
            IF v_quote > 0 THEN
               IF p_sellbuyflag ='BR'
              THEN
                UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              ELSE
               UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              END IF;
              --Commented by subrahmanyam for the enhancement 171212
           /* Else
             INSERT INTO QMS_QUOTES_UPDATED---remove this insertion statement when the client asks for the enhancement id: 171212
              (QUOTE_ID,
               NEW_SELLCHARGE_ID,
               NEW_BUYCHARGE_ID,
               NEW_LANE_NO,
               NEW_VERSION_NO,
               SELL_BUY_FLAG,
               CHARGE_AT,
               CHANGEDESC,
               OLD_SELLCHARGE_ID,
               OLD_BUYCHARGE_ID,
               OLD_LANE_NO,
               OLD_VERSION_NO)
              VALUES
                (k.quote_id,
                p_newsellrateid,
                 p_newbuyrateid,
                 p_newlaneno,
                 p_newversionno,
                 p_sellbuyflag,
                 'Carrier',
                 p_origin || '-' || p_destination || ', Freight Rate Validity',
                 k.sellrate_id,
                 v_buyrateid,
                 v_laneno,
                 v_versionno);*/
            END IF;
          END LOOP;

        ELSE
          --If v_mode = '1' or v_mode = '4' Then
            FOR j IN (SELECT chargerate, weight_break_slab
                        FROM QMS_BUYRATES_DTL
                       WHERE buyrateid = v_buyrateid
                         AND lane_no = v_laneno
                         AND version_no = v_versionno
                         AND service_level NOT IN ('SCH')) LOOP

              SELECT COUNT(*)
                INTO v_count
                FROM QMS_BUYRATES_DTL
               WHERE buyrateid = p_newbuyrateid
                 AND lane_no = p_newlaneno
                 AND version_no = p_newversionno
                 AND weight_break_slab = j.weight_break_slab
                 AND chargerate = j.chargerate
                 AND service_level NOT IN ('SCH');
              IF v_count = 0 THEN
                v_temp := v_temp + 1;
              ELSE
                v_totalcount := v_totalcount + v_count;
                v_temp       := v_temp + 1;
              END IF;
            END LOOP;
            IF v_totalcount = v_temp THEN
              v_changedesc := p_origin || '-' || p_destination ||
                              ', '||v_shmode||' Surcharges';
            ELSE
              v_changedesc := p_origin || '-' || p_destination ||
                              ', '||v_shmode||' Freight Rates and  Surcharges';
            END IF;
            FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                        FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                       WHERE BUYRATE_ID = v_buyrateid
                         AND RATE_LANE_NO = v_laneno
                         AND SELL_BUY_FLAG = p_sellbuyflag
                         AND LINE_NO = '0'
                         AND QM.ID = QR.QUOTE_ID
                         AND QM.ACTIVE_FLAG = 'A') LOOP
              SELECT COUNT(*)
                INTO v_quote
                FROM QMS_QUOTES_UPDATED
               WHERE quote_id = k.quote_id
                 AND SELL_BUY_FLAG = p_sellbuyflag;
              IF v_quote > 0 THEN
                IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
              ELSE
           INSERT INTO QMS_QUOTES_UPDATED
              (QUOTE_ID,
               NEW_SELLCHARGE_ID,
               NEW_BUYCHARGE_ID,
               NEW_LANE_NO,
               NEW_VERSION_NO,
               SELL_BUY_FLAG,
               CHARGE_AT,
               CHANGEDESC,
               OLD_SELLCHARGE_ID,
               OLD_BUYCHARGE_ID,
               OLD_LANE_NO,
               OLD_VERSION_NO)
                VALUES
                  (k.quote_id,
                  p_newsellrateid,
                   p_newbuyrateid,
                   p_newlaneno,
                   p_newversionno,
                   p_sellbuyflag,
                   'Carrier',
                   v_changedesc,
                   k.sellrate_id,
                   v_buyrateid,
                   v_laneno,
                   v_versionno);
              END IF;
            END LOOP;
          --End If;
        END IF;

      ELSE
        --both transit time and validity

    OPEN v_curr FOR
   ' SELECT max(QBD.buyrateid)    FROM QMS_BUYRATES_DTL QBD ,QMS_BUYRATES_MASTER QBM WHERE QBM.BUYRATEID=QBD.BUYRATEID AND QBM.VERSION_NO=QBD.VERSION_NO AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL) AND QBM.WEIGHT_BREAK = '||''''||v_break||''''||' AND QBM.RATE_TYPE='||''''||v_type||''''||' AND QBD.ORIGIN = '||''''||p_origin||''''||' AND QBD.DESTINATION = '||''''||p_destination||''''||' AND QBD.CARRIER_ID = '||''''||p_carrier_id||''''||' AND QBD.SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||' AND QBD.VERSION_NO = '||''''||v_newversionno||''''||' AND QBD.LINE_NO = ''0'' '||'AND ' || v_frequency ||' AND QBM.TERMINALID IN( '||v_terminal ||')';--@@ Modified by subrahmanyam for the WPBN issue- 167860 on 17-apr-09
       LOOP
      FETCH  v_curr INTO v_buyrateid;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;
      CLOSE v_curr;
       /* Begin
          SELECT max(BUYRATEID)
            into v_buyrateid
            FROM QMS_BUYRATES_DTL
           WHERE ORIGIN = p_origin
             AND DESTINATION = p_destination
             AND CARRIER_ID = p_carrier_id
             AND SERVICE_LEVEL = p_servicelevel_id
              AND VERSION_NO = p_newversionno - 1||v_frequency;
        Exception
          When NO_DATA_FOUND Then
            v_buyrateid := '';
        End;*/
        IF v_buyrateid > 0
        THEN
        OPEN v_curr FOR  ' SELECT LANE_NO, VERSION_NO FROM QMS_BUYRATES_DTL WHERE BUYRATEID='||''''||v_buyrateid||''''||' AND ORIGIN = '||''''||p_origin||''''||' AND DESTINATION = '||''''||p_destination||''''||' AND CARRIER_ID = '||''''||p_carrier_id||''''||' AND SERVICE_LEVEL = '||''''||p_servicelevel_id||''''||' AND VERSION_NO = '||''''||v_newversionno||''''||' AND LINE_NO = ''0'' '||'AND ' || v_frequency ;
            LOOP
      FETCH  v_curr INTO v_laneno,v_versionno;
      EXIT WHEN v_curr%NOTFOUND;
      END LOOP;
      CLOSE v_curr;
        /*SELECT LANE_NO, VERSION_NO
          into v_laneno, v_versionno
          FROM QMS_BUYRATES_DTL
         WHERE ORIGIN = p_origin
           AND DESTINATION = p_destination
           AND CARRIER_ID = p_carrier_id
           AND SERVICE_LEVEL = p_servicelevel_id
           AND VERSION_NO = p_newversionno - 1
           AND BUYRATEID = v_buyrateid||v_frequency;*/ --added by VLAKSHMI
        END IF;
        IF v_buyrateid > 0

         THEN
          FOR j IN (SELECT chargerate, weight_break_slab
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = v_buyrateid
                       AND lane_no = v_laneno
                       AND version_no = v_versionno) LOOP

            SELECT COUNT(*)
              INTO v_count
              FROM QMS_BUYRATES_DTL
             WHERE buyrateid = p_newbuyrateid
               AND lane_no = p_newlaneno
               AND version_no = p_newversionno
               AND weight_break_slab = j.weight_break_slab
               AND chargerate = j.chargerate;
            IF v_count = 0 THEN
              v_temp := v_temp + 1;
            ELSE
              v_totalcount := v_totalcount + v_count;
              v_temp       := v_temp + 1;
            END IF;

          END LOOP;
          IF v_totalcount = v_temp THEN
            FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                        FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                       WHERE BUYRATE_ID = v_buyrateid
                         AND RATE_LANE_NO = v_laneno
                         AND SELL_BUY_FLAG = p_sellbuyflag
                         AND LINE_NO = '0'
                         AND (TRANSIT_CHECKED = 'on' OR
                             TRANSIT_CHECKED = 'Y')
                         AND (VALIDITY_CHECKED = 'on' OR
                             VALIDITY_CHECKED = 'Y')
                         AND QM.ID = QR.QUOTE_ID
                         AND QM.ACTIVE_FLAG = 'A') LOOP
              SELECT COUNT(*)
                INTO v_quote
                FROM QMS_QUOTES_UPDATED
               WHERE quote_id = k.quote_id
                 AND SELL_BUY_FLAG = p_sellbuyflag;
              IF v_quote > 0 THEN
                 IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
              ELSE
                     INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                VALUES
                  (k.quote_id,
                   p_newsellrateid,
                   p_newbuyrateid,
                   p_newlaneno,
                   p_newversionno,
                   p_sellbuyflag,
                   'Carrier',
                   p_origin || '-' || p_destination ||
                   ', Transit Time, Freight Rate Validity',
                   k.sellrate_id,
                   v_buyrateid,
                   v_laneno,
                   v_versionno);
                v_QuoteId := v_QuoteId || k.quote_id || ',';
              END IF;
            END LOOP;
            v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

            FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                        FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                       WHERE BUYRATE_ID = v_buyrateid
                         AND RATE_LANE_NO = v_laneno
                         AND SELL_BUY_FLAG = p_sellbuyflag
                         AND LINE_NO = '0'
                         AND (TRANSIT_CHECKED = 'on' OR
                             TRANSIT_CHECKED = 'Y')
                         /*AND to_char(QR.QUOTE_ID) not in (v_QuoteId1)*/
                         AND QM.ID = QR.QUOTE_ID
                         AND QM.ACTIVE_FLAG = 'A') LOOP
              SELECT COUNT(*)
                INTO v_quote
                FROM QMS_QUOTES_UPDATED
               WHERE quote_id = k.quote_id
                 AND SELL_BUY_FLAG = p_sellbuyflag;
              IF v_quote > 0 THEN
                  IF p_sellbuyflag ='BR'
              THEN
                UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              ELSE
               UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
             END IF;
              ELSE

                INSERT INTO QMS_QUOTES_UPDATED
                  (QUOTE_ID,
                   NEW_SELLCHARGE_ID,
                   NEW_BUYCHARGE_ID,
                   NEW_LANE_NO,
                   NEW_VERSION_NO,
                   SELL_BUY_FLAG,
                   CHARGE_AT,
                   CHANGEDESC,
                   OLD_SELLCHARGE_ID,
                   OLD_BUYCHARGE_ID,
                   OLD_LANE_NO,
                   OLD_VERSION_NO)
                VALUES
                  (k.quote_id,
                   p_newsellrateid,
                   p_newbuyrateid,
                   p_newlaneno,
                   p_newversionno,
                   p_sellbuyflag,
                   'Carrier',
                   p_origin || '-' || p_destination || ', Transit Time',
                   k.sellrate_id,
                   v_buyrateid,
                   v_laneno,
                   v_versionno);
              END IF;
            END LOOP;
            FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                        FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                       WHERE BUYRATE_ID = v_buyrateid
                         AND RATE_LANE_NO = v_laneno
                         AND SELL_BUY_FLAG = p_sellbuyflag
                         AND LINE_NO = '0'
                         AND (VALIDITY_CHECKED = 'on' OR
                             VALIDITY_CHECKED = 'Y')
                         /*AND to_char(QR.QUOTE_ID) not in (v_QuoteId1)*/
                         AND QM.ID = QR.QUOTE_ID
                         AND QM.ACTIVE_FLAG = 'A') LOOP
              SELECT COUNT(*)
                INTO v_quote
                FROM QMS_QUOTES_UPDATED
               WHERE quote_id = k.quote_id
                 AND SELL_BUY_FLAG = p_sellbuyflag;
              IF v_quote > 0 THEN
                  IF p_sellbuyflag ='BR'
              THEN
                UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              ELSE
               UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              END IF;
              --Commented by subrahmanyam for the enhancement 171212
              /*Else
             INSERT INTO QMS_QUOTES_UPDATED
                (QUOTE_ID,
                 NEW_SELLCHARGE_ID,
                 NEW_BUYCHARGE_ID,
                 NEW_LANE_NO,
                 NEW_VERSION_NO,
                 SELL_BUY_FLAG,
                 CHARGE_AT,
                 CHANGEDESC,
                 OLD_SELLCHARGE_ID,
                 OLD_BUYCHARGE_ID,
                 OLD_LANE_NO,
                 OLD_VERSION_NO)
                VALUES
                  (k.quote_id,
                   p_newsellrateid,
                   p_newbuyrateid,
                   p_newlaneno,
                   p_newversionno,
                   p_sellbuyflag,
                   'Carrier',
                   p_origin || '-' || p_destination ||
                   ', Freight Rate Validity',
                   k.sellrate_id,
                   v_buyrateid,
                   v_laneno,
                   v_versionno);*/
              END IF;
            END LOOP;
          ELSE
            --If v_mode = '1' or v_mode = '4' Then
              FOR j IN (SELECT chargerate, weight_break_slab
                          FROM QMS_BUYRATES_DTL
                         WHERE buyrateid = v_buyrateid
                           AND lane_no = v_laneno
                           AND version_no = v_versionno
                           AND service_level NOT IN ('SCH')) LOOP

                SELECT COUNT(*)
                  INTO v_count
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = p_newbuyrateid
                   AND lane_no = p_newlaneno
                   AND version_no = p_newversionno
                   AND weight_break_slab = j.weight_break_slab
                   AND chargerate = j.chargerate
                   AND service_level NOT IN ('SCH');
                IF v_count = 0 THEN
                  v_temp := v_temp + 1;
                ELSE
                  v_totalcount := v_totalcount + v_count;
                  v_temp       := v_temp + 1;
                END IF;
              END LOOP;
              IF v_totalcount = v_temp THEN
                v_changedesc := p_origin || '-' || p_destination ||
                                ', '||v_shmode||' Surcharges';
              ELSE
                v_changedesc := p_origin || '-' || p_destination ||
                                ','||v_shmode||' Freight Rates and  Surcharges';
              END IF;
              FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                         WHERE BUYRATE_ID = v_buyrateid
                           AND RATE_LANE_NO = v_laneno
                           AND SELL_BUY_FLAG = p_sellbuyflag
                           AND LINE_NO = '0'
                           AND QM.ID = QR.QUOTE_ID
                           AND QM.ACTIVE_FLAG = 'A') LOOP
                SELECT COUNT(*)
                  INTO v_quote
                  FROM QMS_QUOTES_UPDATED
                 WHERE quote_id = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
                IF v_quote > 0 THEN
                   IF p_sellbuyflag ='BR'
              THEN
                UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              ELSE
               UPDATE QMS_QUOTES_UPDATED
                   SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                 WHERE QUOTE_ID = k.quote_id
                   AND SELL_BUY_FLAG = p_sellbuyflag;
              END IF;
                ELSE
           INSERT INTO QMS_QUOTES_UPDATED
                (QUOTE_ID,
                 NEW_SELLCHARGE_ID,
                 NEW_BUYCHARGE_ID,
                 NEW_LANE_NO,
                 NEW_VERSION_NO,
                 SELL_BUY_FLAG,
                 CHARGE_AT,
                 CHANGEDESC,
                 OLD_SELLCHARGE_ID,
                 OLD_BUYCHARGE_ID,
                 OLD_LANE_NO,
                 OLD_VERSION_NO)
                 VALUES
                    (k.quote_id,
                     p_newsellrateid,
                     p_newbuyrateid,
                     p_newlaneno,
                     p_newversionno,
                     p_sellbuyflag,
                     'Carrier',
                     v_changedesc,
                     k.sellrate_id,
                     v_buyrateid,
                     v_laneno,
                     v_versionno);
                END IF;
              END LOOP;
            --End If;
          END IF;

        ELSE
          FOR m IN (SELECT QBD.BUYRATEID,
                           QBD.LANE_NO,
                           QBD.VERSION_NO,
                           TRANSIT_TIME,
                           VALID_UPTO
                      FROM QMS_BUYRATES_DTL QBD,QMS_BUYRATES_MASTER QBM
                     WHERE QBD.ORIGIN = p_origin
                       AND QBD.DESTINATION = p_destination
                       AND QBD.CARRIER_ID = p_carrier_id
                       AND QBD.SERVICE_LEVEL = p_servicelevel_id
                       AND QBM.WEIGHT_BREAK=v_break
                       AND QBM.RATE_TYPE = v_type
                       AND QBD.BUYRATEID=QBM.BUYRATEID
                       AND QBD.VERSION_NO=QBM.VERSION_NO
                       AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL)
                       AND QBD.LINE_NO = '0'
                       AND QBD.VERSION_NO = p_newversionno - 1 --added by VLAKSHMI
                       AND (QBD.INVALIDATE IS NULL OR QBD.INVALIDATE = 'F')) LOOP
            IF (m.transit_time = p_transittime AND
               (v_validCount = 1 OR (m.valid_upto IS NULL AND v_validate_upto ='')OR TO_DATE(m.valid_upto) = TO_DATE(p_validate_upto))) -- both tanstime and valid upto same
             THEN
              FOR j IN (SELECT chargerate, weight_break_slab
                          FROM QMS_BUYRATES_DTL
                         WHERE buyrateid = m.buyrateid
                           AND lane_no = m.lane_no
                           AND version_no = m.version_no) LOOP

                SELECT COUNT(*)
                  INTO v_count
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = p_newbuyrateid
                   AND lane_no = p_newlaneno
                   AND version_no = p_newversionno
                   AND weight_break_slab = j.weight_break_slab
                   AND chargerate = j.chargerate;
                IF v_count = 0 THEN
                  v_temp := v_temp + 1;
                ELSE
                  v_totalcount := v_totalcount + v_count;
                  v_temp       := v_temp + 1;
                END IF;

              END LOOP;
              IF v_totalcount = v_temp THEN
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                      p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Frequency',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                  END IF;
                END LOOP;

              ELSE
                --If v_mode = '1' or v_mode = '4' Then
                  FOR j IN (SELECT chargerate, weight_break_slab
                              FROM QMS_BUYRATES_DTL
                             WHERE buyrateid = v_buyrateid
                               AND lane_no = v_laneno
                               AND version_no = v_versionno
                               AND service_level NOT IN ('SCH')) LOOP

                    SELECT COUNT(*)
                      INTO v_count
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = p_newbuyrateid
                       AND lane_no = p_newlaneno
                       AND version_no = p_newversionno
                       AND weight_break_slab = j.weight_break_slab
                       AND chargerate = j.chargerate
                       AND service_level NOT IN ('SCH');
                    IF v_count = 0 THEN
                      v_temp := v_temp + 1;
                    ELSE
                      v_totalcount := v_totalcount + v_count;
                      v_temp       := v_temp + 1;
                    END IF;
                  END LOOP;
                  IF v_totalcount = v_temp THEN
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Surcharges';
                  ELSE
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Freight Rates and  Surcharges';
                  END IF;
                  FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                              FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                             WHERE BUYRATE_ID = m.buyrateid
                               AND RATE_LANE_NO = m.lane_no
                               AND SELL_BUY_FLAG =p_sellbuyflag
                               AND LINE_NO = '0'
                               AND QM.ID = QR.QUOTE_ID
                               AND QM.ACTIVE_FLAG = 'A') LOOP
                    SELECT COUNT(*)
                      INTO v_quote
                      FROM QMS_QUOTES_UPDATED
                     WHERE quote_id = k.quote_id
                       AND SELL_BUY_FLAG =p_sellbuyflag;
                    IF v_quote > 0 THEN
                       IF p_sellbuyflag ='BR'
                    THEN
                      UPDATE QMS_QUOTES_UPDATED
                         SET NEW_VERSION_NO = p_newversionno
                       WHERE QUOTE_ID = k.quote_id
                         AND SELL_BUY_FLAG = p_sellbuyflag;
                    ELSE
                     UPDATE QMS_QUOTES_UPDATED
                         SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                       WHERE QUOTE_ID = k.quote_id
                         AND SELL_BUY_FLAG = p_sellbuyflag;
                    END IF;
                    ELSE
                      INSERT INTO QMS_QUOTES_UPDATED
                        (QUOTE_ID,
                         NEW_SELLCHARGE_ID,
                         NEW_BUYCHARGE_ID,
                         NEW_LANE_NO,
                         NEW_VERSION_NO,
                         SELL_BUY_FLAG,
                         CHARGE_AT,
                         CHANGEDESC,
                         OLD_SELLCHARGE_ID,
                         OLD_BUYCHARGE_ID,
                         OLD_LANE_NO,
                         OLD_VERSION_NO)
                      VALUES
                        (k.quote_id,
                         p_newsellrateid,
                         p_newbuyrateid,
                         p_newlaneno,
                         p_newversionno,
                         p_sellbuyflag,
                         'Carrier',
                         v_changedesc,
                         k.sellrate_id,
                         m.buyrateid,
                         m.lane_no,
                         m.version_no);
                    END IF;
                  END LOOP;
                --End If;
              END IF;

            ELSIF m.transit_time = p_transittime -- only transit time is same
             THEN
              FOR j IN (SELECT chargerate, weight_break_slab
                          FROM QMS_BUYRATES_DTL
                         WHERE buyrateid = m.buyrateid
                           AND lane_no = m.lane_no
                           AND version_no = m.version_no) LOOP

                SELECT COUNT(*)
                  INTO v_count
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = p_newbuyrateid
                   AND lane_no = p_newlaneno
                   AND version_no = p_newversionno
                   AND weight_break_slab = j.weight_break_slab
                   AND chargerate = j.chargerate;
                IF v_count = 0 THEN
                  v_temp := v_temp + 1;
                ELSE
                  v_totalcount := v_totalcount + v_count;
                  v_temp       := v_temp + 1;
                END IF;

              END LOOP;
              IF v_totalcount = v_temp THEN
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                  IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                INSERT INTO QMS_QUOTES_UPDATED
                  (QUOTE_ID,
                   NEW_SELLCHARGE_ID,
                   NEW_BUYCHARGE_ID,
                   NEW_LANE_NO,
                   NEW_VERSION_NO,
                   SELL_BUY_FLAG,
                   CHARGE_AT,
                   CHANGEDESC,
                   OLD_SELLCHARGE_ID,
                   OLD_BUYCHARGE_ID,
                   OLD_LANE_NO,
                   OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Frequency, Freight Rate Validity',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                --SELECT QUOTE_ID FROM QMS_QUOTES_UPDATED WHERE CONFIRM_FLAG IS NULL
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                           /*  AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                    IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
                  ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                    (QUOTE_ID,
                     NEW_SELLCHARGE_ID,
                     NEW_BUYCHARGE_ID,
                     NEW_LANE_NO,
                     NEW_VERSION_NO,
                     SELL_BUY_FLAG,
                     CHARGE_AT,
                     CHANGEDESC,
                     OLD_SELLCHARGE_ID,
                     OLD_BUYCHARGE_ID,
                     OLD_LANE_NO,
                     OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Frequency',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                            /* AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  --Commented by subrahmanyam for the enhancement 171212
                 /* Else
                   INSERT INTO QMS_QUOTES_UPDATED
                    (QUOTE_ID,
                     NEW_SELLCHARGE_ID,
                     NEW_BUYCHARGE_ID,
                     NEW_LANE_NO,
                     NEW_VERSION_NO,
                     SELL_BUY_FLAG,
                     CHARGE_AT,
                     CHANGEDESC,
                     OLD_SELLCHARGE_ID,
                     OLD_BUYCHARGE_ID,
                     OLD_LANE_NO,
                     OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                      p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Freight Rate Validity',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);*/
                  END IF;
                END LOOP;
              ELSE
                --If v_mode = '1' or v_mode = '4' Then
                  FOR j IN (SELECT chargerate, weight_break_slab
                              FROM QMS_BUYRATES_DTL
                             WHERE buyrateid = v_buyrateid
                               AND lane_no = v_laneno
                               AND version_no = v_versionno
                               AND service_level NOT IN ('SCH')) LOOP

                    SELECT COUNT(*)
                      INTO v_count
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = p_newbuyrateid
                       AND lane_no = p_newlaneno
                       AND version_no = p_newversionno
                       AND weight_break_slab = j.weight_break_slab
                       AND chargerate = j.chargerate
                       AND service_level NOT IN ('SCH');
                    IF v_count = 0 THEN
                      v_temp := v_temp + 1;
                    ELSE
                      v_totalcount := v_totalcount + v_count;
                      v_temp       := v_temp + 1;
                    END IF;
                  END LOOP;
                  IF v_totalcount = v_temp THEN
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Surcharges';
                  ELSE
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Freight Rates and  Surcharges';
                  END IF;
                  FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                              FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                             WHERE BUYRATE_ID = m.buyrateid
                               AND RATE_LANE_NO = m.lane_no
                               AND SELL_BUY_FLAG = p_sellbuyflag
                               AND LINE_NO = '0'
                               AND QM.ID = QR.QUOTE_ID
                               AND QM.ACTIVE_FLAG = 'A') LOOP
                    SELECT COUNT(*)
                      INTO v_quote
                      FROM QMS_QUOTES_UPDATED
                     WHERE quote_id = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                    IF v_quote > 0 THEN
                       IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                    ELSE
                     INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                      VALUES
                        (k.quote_id,
                         p_newsellrateid,
                         p_newbuyrateid,
                         p_newlaneno,
                         p_newversionno,
                         p_sellbuyflag,
                         'Carrier',
                         p_origin || '-' || p_destination ||
                         ', Freight Rates',
                         k.sellrate_id,
                         m.buyrateid,
                         m.lane_no,
                         m.version_no);
                    END IF;
                  END LOOP;
                --End if;
              END IF;

            ELSIF ((m.valid_upto IS NULL AND v_validate_upto ='') OR TO_DATE(m.valid_upto) = TO_DATE(p_validate_upto) OR v_validCount = 1) --valid upto same
             THEN
              FOR j IN (SELECT chargerate, weight_break_slab
                          FROM QMS_BUYRATES_DTL
                         WHERE buyrateid = m.buyrateid
                           AND lane_no = m.lane_no
                           AND version_no = m.version_no) LOOP

                SELECT COUNT(*)
                  INTO v_count
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = p_newbuyrateid
                   AND lane_no = p_newlaneno
                   AND version_no = p_newversionno
                   AND weight_break_slab = j.weight_break_slab
                   AND chargerate = j.chargerate;
                IF v_count = 0 THEN
                  v_temp := v_temp + 1;
                ELSE
                  v_totalcount := v_totalcount + v_count;
                  v_temp       := v_temp + 1;
                END IF;

              END LOOP;
              IF v_totalcount = v_temp THEN
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                             /*AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                      IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Frequency, Transit Time',
                        k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                           /*  AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                      NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                      p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Frequency',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                           /*  AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG =p_sellbuyflag;
                  IF v_quote > 0 THEN
                    IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
                  ELSE
                  INSERT INTO QMS_QUOTES_UPDATED
                    (QUOTE_ID,
                     NEW_SELLCHARGE_ID,
                     NEW_BUYCHARGE_ID,
                     NEW_LANE_NO,
                     NEW_VERSION_NO,
                     SELL_BUY_FLAG,
                     CHARGE_AT,
                     CHANGEDESC,
                     OLD_SELLCHARGE_ID,
                     OLD_BUYCHARGE_ID,
                     OLD_LANE_NO,
                     OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                      p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Transit Time',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                  END IF;
                END LOOP;

              ELSE
                --If v_mode = '1' or v_mode = '4' Then
                  FOR j IN (SELECT chargerate, weight_break_slab
                              FROM QMS_BUYRATES_DTL
                             WHERE buyrateid = v_buyrateid
                               AND lane_no = v_laneno
                               AND version_no = v_versionno
                               AND service_level NOT IN ('SCH')) LOOP

                    SELECT COUNT(*)
                      INTO v_count
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = p_newbuyrateid
                       AND lane_no = p_newlaneno
                       AND version_no = p_newversionno
                       AND weight_break_slab = j.weight_break_slab
                       AND chargerate = j.chargerate
                       AND service_level NOT IN ('SCH');
                    IF v_count = 0 THEN
                      v_temp := v_temp + 1;
                    ELSE
                      v_totalcount := v_totalcount + v_count;
                      v_temp       := v_temp + 1;
                    END IF;
                  END LOOP;
                  IF v_totalcount = v_temp THEN
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Surcharges';
                  ELSE
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Freight Rates and  Surcharges';
                  END IF;
                  FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                              FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                             WHERE BUYRATE_ID = m.buyrateid
                               AND RATE_LANE_NO = m.lane_no
                               AND SELL_BUY_FLAG = p_sellbuyflag
                               AND LINE_NO = '0'
                               AND QM.ID = QR.QUOTE_ID
                               AND QM.ACTIVE_FLAG = 'A') LOOP
                    SELECT COUNT(*)
                      INTO v_quote
                      FROM QMS_QUOTES_UPDATED
                     WHERE quote_id = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                    IF v_quote > 0 THEN
                       IF p_sellbuyflag ='BR'
                      THEN
                        UPDATE QMS_QUOTES_UPDATED
                           SET NEW_VERSION_NO = p_newversionno
                         WHERE QUOTE_ID = k.quote_id
                           AND SELL_BUY_FLAG = p_sellbuyflag;
                      ELSE
                       UPDATE QMS_QUOTES_UPDATED
                           SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                         WHERE QUOTE_ID = k.quote_id
                           AND SELL_BUY_FLAG = p_sellbuyflag;
                      END IF;
                    ELSE
                     INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                      VALUES
                        (k.quote_id,
                        p_newsellrateid,
                         p_newbuyrateid,
                         p_newlaneno,
                         p_newversionno,
                         p_sellbuyflag,
                         'Carrier',
                         p_origin || '-' || p_destination ||
                         ', Freight Rates',
                         k.sellrate_id,
                         m.buyrateid,
                         m.lane_no,
                         m.version_no);
                    END IF;
                  END LOOP;
                --End If;
              END IF;
            ELSE

              FOR j IN (SELECT chargerate, weight_break_slab
                          FROM QMS_BUYRATES_DTL
                         WHERE buyrateid = m.buyrateid
                           AND lane_no = m.lane_no
                           AND version_no = m.version_no) LOOP

                SELECT COUNT(*)
                  INTO v_count
                  FROM QMS_BUYRATES_DTL
                 WHERE buyrateid = p_newbuyrateid
                   AND lane_no = p_newlaneno
                   AND version_no = p_newversionno
                   AND weight_break_slab = j.weight_break_slab
                   AND chargerate = j.chargerate;
                IF v_count = 0 THEN
                  v_temp := v_temp + 1;
                ELSE
                  v_totalcount := v_totalcount + v_count;
                  v_temp       := v_temp + 1;
                END IF;

              END LOOP;
              IF v_totalcount = v_temp THEN
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                              IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                   ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Frequency, Freight Rate Validity,Transit Time',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);
                 FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                            /* AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                    IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                   INSERT INTO QMS_QUOTES_UPDATED
                    (QUOTE_ID,
                     NEW_SELLCHARGE_ID,
                     NEW_BUYCHARGE_ID,
                     NEW_LANE_NO,
                     NEW_VERSION_NO,
                     SELL_BUY_FLAG,
                     CHARGE_AT,
                     CHANGEDESC,
                     OLD_SELLCHARGE_ID,
                     OLD_BUYCHARGE_ID,
                     OLD_LANE_NO,
                     OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Transit Time, Freight Rate Validity',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);
                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             /*AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
                  ELSE
                   INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Transit Time, Frequency',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                            /* AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                      IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
                  ELSE

                   INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||  ', Frequency, Freight Rate Validity',
                        k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                  END IF;
                END LOOP;


                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (FREQUENCY_CHECKED = 'on' OR
                                 FREQUENCY_CHECKED = 'Y')
                             /*AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                THEN
                  UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                ELSE
                 UPDATE QMS_QUOTES_UPDATED
                     SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                   WHERE QUOTE_ID = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                END IF;
                  ELSE
                    INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Frequency',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (VALIDITY_CHECKED = 'on' OR
                                 VALIDITY_CHECKED = 'Y')
                            /* AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                     IF p_sellbuyflag ='BR'
                    THEN
                      UPDATE QMS_QUOTES_UPDATED
                         SET NEW_VERSION_NO = p_newversionno
                       WHERE QUOTE_ID = k.quote_id
                         AND SELL_BUY_FLAG = p_sellbuyflag;
                    ELSE
                     UPDATE QMS_QUOTES_UPDATED
                         SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                       WHERE QUOTE_ID = k.quote_id
                         AND SELL_BUY_FLAG = p_sellbuyflag;
                    END IF;
                    --Commented by subrahmanyam for the enhancement 171212
                 /* Else
                   INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination ||
                       ', Freight Rate Validity',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';*/
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);

                FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                           WHERE BUYRATE_ID = m.buyrateid
                             AND RATE_LANE_NO = m.lane_no
                             AND SELL_BUY_FLAG = p_sellbuyflag
                             AND LINE_NO = '0'
                             AND (TRANSIT_CHECKED = 'on' OR
                                 TRANSIT_CHECKED = 'Y')
                        /*     AND to_char(QR.QUOTE_ID) NOT IN (v_QuoteId1)*/
                             AND QM.ID = QR.QUOTE_ID
                             AND QM.ACTIVE_FLAG = 'A') LOOP
                  SELECT COUNT(*)
                    INTO v_quote
                    FROM QMS_QUOTES_UPDATED
                   WHERE quote_id = k.quote_id
                     AND SELL_BUY_FLAG = p_sellbuyflag;
                  IF v_quote > 0 THEN
                    IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                  ELSE
                   INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                    VALUES
                      (k.quote_id,
                       p_newsellrateid,
                       p_newbuyrateid,
                       p_newlaneno,
                       p_newversionno,
                       p_sellbuyflag,
                       'Carrier',
                       p_origin || '-' || p_destination || ', Transit Time',
                       k.sellrate_id,
                       m.buyrateid,
                       m.lane_no,
                       m.version_no);
                    v_QuoteId := v_QuoteId || k.quote_id || ',';
                  END IF;
                END LOOP;
                v_QuoteId1 := SUBSTR(v_QuoteId, 0, LENGTH(v_QuoteId) - 1);


              ELSE
                --If v_mode = '1' or v_mode = '4' Then
                  FOR j IN (SELECT chargerate, weight_break_slab
                              FROM QMS_BUYRATES_DTL
                             WHERE buyrateid = v_buyrateid
                               AND lane_no = v_laneno
                               AND version_no = v_versionno
                               AND service_level NOT IN ('SCH')) LOOP

                    SELECT COUNT(*)
                      INTO v_count
                      FROM QMS_BUYRATES_DTL
                     WHERE buyrateid = p_newbuyrateid
                       AND lane_no = p_newlaneno
                       AND version_no = p_newversionno
                       AND weight_break_slab = j.weight_break_slab
                       AND chargerate = j.chargerate
                       AND service_level NOT IN ('SCH');
                    IF v_count = 0 THEN
                      v_temp := v_temp + 1;
                    ELSE
                      v_totalcount := v_totalcount + v_count;
                      v_temp       := v_temp + 1;
                    END IF;
                  END LOOP;
                  IF v_totalcount = v_temp THEN
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Surcharges';
                  ELSE
                    v_changedesc := p_origin || '-' || p_destination ||
                                    ', '||v_shmode||' Freight Rates and  Surcharges';
                  END IF;
                  FOR k IN (SELECT QR.QUOTE_ID,QR.SELLRATE_ID
                              FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                             WHERE BUYRATE_ID = m.buyrateid
                               AND RATE_LANE_NO = m.lane_no
                               AND SELL_BUY_FLAG = p_sellbuyflag
                               AND LINE_NO = '0'
                               AND QM.ID = QR.QUOTE_ID
                               AND QM.ACTIVE_FLAG = 'A') LOOP
                    SELECT COUNT(*)
                      INTO v_quote
                      FROM QMS_QUOTES_UPDATED
                     WHERE quote_id = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                    IF v_quote > 0 THEN
                        IF p_sellbuyflag ='BR'
                  THEN
                    UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  ELSE
                   UPDATE QMS_QUOTES_UPDATED
                       SET NEW_VERSION_NO = p_newversionno,NEW_SELLCHARGE_ID=p_newsellrateid
                     WHERE QUOTE_ID = k.quote_id
                       AND SELL_BUY_FLAG = p_sellbuyflag;
                  END IF;
                    ELSE
                  INSERT INTO QMS_QUOTES_UPDATED
                      (QUOTE_ID,
                       NEW_SELLCHARGE_ID,
                       NEW_BUYCHARGE_ID,
                       NEW_LANE_NO,
                       NEW_VERSION_NO,
                       SELL_BUY_FLAG,
                       CHARGE_AT,
                       CHANGEDESC,
                       OLD_SELLCHARGE_ID,
                       OLD_BUYCHARGE_ID,
                       OLD_LANE_NO,
                       OLD_VERSION_NO)
                      VALUES
                        (k.quote_id,
                        p_newsellrateid,
                         p_newbuyrateid,
                         p_newlaneno,
                         p_newversionno,
                         p_sellbuyflag,
                         'Carrier',
                         p_origin || '-' || p_destination ||
                         ', '||v_shmode||' Freight Rates',
                         k.sellrate_id,
                         m.buyrateid,
                         m.lane_no,
                         m.version_no);
                    END IF;
                  END LOOP;
                --End If;
              END IF;

            END IF;


            --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08
            /*Begin*/
            FOR i IN (
              SELECT REC_CON_ID,
                     ACCESSLEVEL,
                     TERMINALID,
                     WEIGHT_CLASS,
                     CURRENCY,
                     MARGIN_TYPE,
                     MARGIN_BASIS,
                     OVERALL_MARGIN,
                     SHIPMENT_MODE,
                     WEIGHT_BREAK,
                     RATE_TYPE
               /* into v_rec_con_id,
                    v_accesslevel,
                     v_terminalid,
                     v_weightclass,
                     v_currency,
                     v_margintype,
                     v_marginbasis,
                     v_overallmargin,
                     v_shipmode,
                     v_weightbreak,
                     v_ratetype*/
                FROM QMS_REC_CON_SELLRATESMASTER
               WHERE REC_CON_ID IN (SELECT REC_CON_ID
                                      FROM QMS_REC_CON_SELLRATESDTL DTL
                                     WHERE BUYRATEID = m.buyrateid
                                       AND LANE_NO = m.lane_no
                                       /*AND version_no = m.version_no*/
                                       AND LINE_NO = '0' AND AI_FLAG='A'))
        LOOP
          /*  Exception
              WHEN NO_DATA_FOUND Then
                v_rec_con_id :=0;
            End;
*/
SELECT currency INTO v_currency FROM QMS_BUYRATES_MASTER WHERE buyrateid=p_newbuyrateid AND version_no=p_newversionno AND (lane_no=p_newlaneno OR lane_no IS NULL);

             IF p_sellbuyflag='BR'
             THEN
            IF i.rec_con_id >0
             THEN
             SELECT seq_sellrates_acc.NEXTVAL INTO v_seq FROM DUAL;
              INSERT INTO QMS_REC_CON_SELLRATESMSTR_ACC
                (rec_con_id,
                 rc_flag,
                 shipment_mode,
                 weight_break,
                 rate_type,
                 currency,
                 weight_class,
                 accesslevel,
                 terminalid,
                 overall_margin,
                 margin_type,
                 margin_basis)
              VALUES
                (v_seq,
                 'R',
                 i.shipment_mode,
                 i.weight_break,
                 i.rate_type,
                 v_currency,
                 i.weight_class,
                 i.accesslevel,
                 i.terminalid,
                 i.overall_margin,
                 i.margin_type,
                 i.margin_basis);
              n := 0;
              v_margin_perc := 0.00000;
              FOR j IN (SELECT DISTINCT qbd.chargerate           chargerate,
                                        qbd.weight_break_slab    wbslab,
                                        qbd.chargerate_indicator ch_indicator,
                                        qbd.lowerbound           lbound,
                                        qbd.upperbound           ubound,
                                        qbd.rate_description     rate_description,
                                        qbd.service_level        srvlevel,
                                        qbd.carrier_id           carrier,
                                        qbd.origin               origin,
                                        qbd.destination          dest,
                                        qbd.transit_time         transittime,
                                        qbm.console_type         consoletype,
                                        qsd.origin_country       origin_country,
                                        qsd.desti_country        dest_country,
                                       /* qsd.margin_perc          perc,*/--commented by subrahmanyam for security charge missing issue
                                        qbd.notes                notes,
                                        qbd.line_no              line_no,
                                        qbd.frequency            freq

                          FROM QMS_BUYRATES_MASTER      qbm,
                               QMS_BUYRATES_DTL         qbd,
                               QMS_REC_CON_SELLRATESDTL qsd
                         WHERE qbd.buyrateid = qbm.buyrateid
                           AND qbd.version_no = qbm.version_no
                           AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL)
                           AND qbd.buyrateid = m.buyrateid
                           AND qbd.lane_no = m.lane_no
                           AND qbd.version_no =p_newversionno
                           AND qsd.buyrateid = qbm.buyrateid
                           AND qsd.buyrateid = m.buyrateid
                           AND qsd.lane_no = m.lane_no
                           AND (qbd.INVALIDATE IS NULL OR
                               UPPER(qbd.INVALIDATE) = 'F')
                               AND qsd.AI_FLAG='A'
--commented by subrahmanyam for security charge missing issue
                          /* AND qsd.margin_perc in
                               (select margin_perc
                                  from QMS_REC_CON_SELLRATESDTL
                                 where buyrateid = qbd.buyrateid
                                   and lane_no = qbd.lane_no
                                   and line_no = qbd.line_no
                                   and ai_flag='A' --@dded by kameswari on 17/02/09
                                   and version_no =
                                       (select max(version_no)
                                          from qms_rec_con_sellratesdtl
                                         where buyrateid = qbd.buyrateid
                                           and lane_no = qbd.lane_no))*/

                         ORDER BY line_no)

                          LOOP
                SELECT seq_sellrates_acc_id.NEXTVAL INTO v_id FROM DUAL;
--added by subrahmanyam for security charge missing issue
                BEGIN
                  SELECT rsd.MARGIN_PERC INTO v_margin_perc FROM QMS_REC_CON_SELLRATESDTL rsd WHERE rsd.rec_con_id= i.rec_con_id AND rsd.weightbreakslab=j.wbslab AND rsd.buyrateid=m.buyrateid AND rsd.lane_no=m.lane_no AND rsd.version_no=(SELECT MAX(version_no)  FROM QMS_REC_CON_SELLRATESDTL   WHERE buyrateid = m.buyrateid AND lane_no = m.lane_no);
                  EXCEPTION WHEN NO_DATA_FOUND THEN
                  v_margin_perc:=0.00000;
                  END;
--ended by subrahmanyam for security charge missing issue

                INSERT INTO QMS_REC_CON_SELLRATESDTL_ACC
                  (rec_con_id,
                   carrier_id,
                   servicelevel_id,
                   chargerate,
                   upper_bound,
                   lowrer_bound,
                   margin_perc,
                   weightbreakslab,
                   frequency,
                   chargerate_indicator,
                   lane_no,
                   line_no,
                   buyrateid,
                   transit_time,
                   origin,
                   destination,
                   console_type,
                   ID,
                   rec_con_id_sell,
                   lane_no_sell,
                   INVALIDATE,
                   ai_flag,
                   rate_description,
                   version_no,
                   origin_country,
                   desti_country,
                   notes)
                VALUES
                  (v_seq,
                   j.carrier,
                   j.srvlevel,
                   j.chargerate,
                   j.ubound,
                   j.lbound,
                   /*j.perc*/  --commented by subrahmanyam for security charge missing issue
                    v_margin_perc,--added by subrahmanyam for security charge missing issue
                   j.wbslab,
                   j.freq,
                   j.ch_indicator,
                   p_newlaneno,
                   --m.lane_no,
                   n,
                   p_newbuyrateid,
                   --m.buyrateid,
                   j.transittime,
                   j.origin,
                   j.dest,
                   DECODE(j.consoletype, 'P', NULL, j.consoletype),
                   v_id,
                   i.rec_con_id,
                   --v_lane_no,        --@@Modified by Kameswari for the WPBN issue-122621
                  p_newlaneno,
                   --m.lane_no,
                   'F',
                   'A',
                   DECODE(j.rate_description,
                          '',
                          'A FREIGHT RATE',
                          j.rate_description),
                   p_newversionno,
                   j.origin_country,
                   j.dest_country,
                   j.notes);

              /*  UPDATE QMS_BUYRATES_DTL DTL
                   SET ACC_FLAG = 'Y'
                 WHERE BUYRATEID = m.buyrateid
                   AND LANE_NO = m.lane_no
                   AND VERSION_NO = p_newversionno
                   AND DTL.ACTIVEINACTIVE IS NULL;*/

                   UPDATE QMS_BUYRATES_DTL DTL
                   SET ACC_FLAG = 'Y'
                 WHERE BUYRATEID = p_newbuyrateid
                   AND LANE_NO = p_newlaneno
                   AND VERSION_NO = p_newversionno
                   AND DTL.ACTIVEINACTIVE IS NULL;
                n := n + 1;
              END LOOP;
            END IF;


            UPDATE QMS_REC_CON_SELLRATESDTL DTL
               SET ACCEPTANCE_FLAG = 'Y'
             WHERE BUYRATEID = m.buyrateid
               AND LANE_NO = m.lane_no
               AND version_no = m.version_no
               AND DTL.AI_FLAG = 'A';

            UPDATE QMS_REC_CON_SELLRATESDTL DTL
               SET DTL.AI_FLAG = 'I'
             WHERE DTL.BUYRATEID = m.buyrateid
               AND DTL.LANE_NO = m.lane_no
               AND version_no <p_newversionno
               AND DTL.AI_FLAG = 'A'
               AND EXISTS (SELECT 'X'
                      FROM QMS_REC_CON_SELLRATESMASTER MAS
                     WHERE MAS.RC_FLAG = 'C'
                       AND MAS.REC_CON_ID = DTL.REC_CON_ID
                       AND DTL.BUYRATEID = m.buyrateid
                       AND DTL.Version_No=m.version_no);

            DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
             WHERE BUYRATEID = m.buyrateid
               AND LANE_NO = m.lane_no
               AND version_no < p_newversionno;
            --End;
        /*   Else
             UPDATE QMS_REC_CON_SELLRATESDTL DTL
               SET DTL.AI_FLAG = 'I'
             WHERE DTL.BUYRATEID = m.buyrateid
               AND DTL.LANE_NO = m.lane_no
               AND DTL.version_no < p_newversionno
              AND DTL.AI_FLAG = 'A';*/
       END IF;
           END LOOP;
              UPDATE QMS_BUYRATES_DTL
               SET activeinactive = 'I'
             WHERE buyrateid = m.buyrateid
               AND lane_no = m.lane_no
               AND version_no <p_newversionno;
          END LOOP;

     END IF;
     END IF;


       END IF;
    -- If v_buyrateid!='' commented by VLAKSHMI
    IF v_buyrateid > 0 THEN


      --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08

      /*Begin*/
      FOR i IN (
        SELECT REC_CON_ID,
               ACCESSLEVEL,
               TERMINALID,
               WEIGHT_CLASS,
               CURRENCY,
               MARGIN_TYPE,
               MARGIN_BASIS,
               OVERALL_MARGIN,
               SHIPMENT_MODE,
               WEIGHT_BREAK,
               RATE_TYPE
         /* into  v_rec_con_id,
               v_accesslevel,
               v_terminalid,
               v_weightclass,
               v_currency,
               v_margintype,
               v_marginbasis,
               v_overallmargin,
               v_shipmode,
               v_weightbreak,
               v_ratetype*/
          FROM QMS_REC_CON_SELLRATESMASTER
         WHERE REC_CON_ID IN (SELECT REC_CON_ID
                                FROM QMS_REC_CON_SELLRATESDTL DTL
                               WHERE BUYRATEID = v_buyrateid
                                 AND LANE_NO = v_laneno
                                 /*AND version_no = v_versionno*/
                                 AND LINE_NO = '0' AND AI_FLAG='A'))
                                 LOOP
    /*  Exception
        WHEN NO_DATA_FOUND Then
          v_rec_con_id := 0;
      End;
*/
SELECT currency INTO v_currency FROM QMS_BUYRATES_MASTER WHERE buyrateid=p_newbuyrateid AND version_no=p_newversionno AND (lane_no=p_newlaneno OR lane_no IS NULL);

      IF p_sellbuyflag='BR'
      THEN
      IF i.rec_con_id >0
       THEN
       SELECT seq_sellrates_acc.NEXTVAL INTO v_seq FROM DUAL;
        INSERT INTO QMS_REC_CON_SELLRATESMSTR_ACC
          (rec_con_id,
           rc_flag,
           shipment_mode,
           weight_break,
           rate_type,
           currency,
           weight_class,
           accesslevel,
           terminalid,
           overall_margin,
           margin_type,
           margin_basis)
        VALUES
          (v_seq,
           'R',
           i.shipment_mode,
           i.weight_break,
           i.rate_type,
           v_currency,
           i.weight_class,
           i.accesslevel,
           i.terminalid,
           i.overall_margin,
           i.margin_type,
           i.margin_basis);
        m := 0;
        FOR j IN (SELECT DISTINCT qbd.chargerate           chargerate,
                         qbd.weight_break_slab    wbslab,
                         qbd.chargerate_indicator ch_indicator,
                         qbd.lowerbound           lbound,
                         qbd.upperbound           ubound,
                         qbd.rate_description     rate_description,
                         qbd.service_level        srvlevel,
                         qbd.carrier_id           carrier,
                         qbd.origin               origin,
                         qbd.destination          dest,
                         qbd.transit_time         transittime,
                         qbm.console_type         consoletype,
                         qsd.origin_country       origin_country,
                         qsd.desti_country        dest_country,
                         /*qsd.margin_perc          perc,*/--commented by subrahmanyam for security charge missing issue
                         qbd.notes                notes,
                         qbd.line_no              line_no,
                         qbd.frequency            freq
                    FROM QMS_BUYRATES_MASTER      qbm,
                         QMS_BUYRATES_DTL         qbd,
                         QMS_REC_CON_SELLRATESDTL qsd
                   WHERE qbd.buyrateid = qbm.buyrateid
                     AND qbd.version_no = qbm.version_no
                     AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL)
                     AND qbd.buyrateid = v_buyrateid
                     AND qbd.lane_no = v_laneno
                        -- AND qbd.version_no=v_versionno-- commented by VLAKSHMI
                     AND qbd.version_no = p_newversionno
                     AND qsd.buyrateid = qbm.buyrateid
                     AND qsd.buyrateid = v_buyrateid
                     AND qsd.AI_FLAG='A'
                     AND qsd.lane_no = v_laneno
--commented by subrahmanyam for security charge missing issue
                    /* AND qsd.margin_perc in
                         (select margin_perc
                            from QMS_REC_CON_SELLRATESDTL
                           where buyrateid = qbd.buyrateid
                             and lane_no = qbd.lane_no
                             and line_no = qbd.line_no
                             and ai_flag='A' --@dded by kameswari on 17/02/09
                             and version_no =
                                 (select max(version_no)
                                    from qms_rec_con_sellratesdtl
                                   where buyrateid = qbd.buyrateid
                                     and lane_no = qbd.lane_no))*/
                     AND (qbd.INVALIDATE IS NULL OR
                         UPPER(qbd.INVALIDATE) = 'F')
                   ORDER BY line_no) LOOP
          SELECT seq_sellrates_acc_id.NEXTVAL INTO v_id FROM DUAL;
--added by subrahmanyam for security charge missing issue
          BEGIN
 SELECT rsd.MARGIN_PERC INTO v_margin_perc FROM QMS_REC_CON_SELLRATESDTL rsd WHERE rsd.rec_con_id=i.rec_con_id AND rsd.weightbreakslab=j.wbslab AND rsd.buyrateid=v_buyrateid AND rsd.lane_no=v_laneno AND rsd.version_no=(SELECT MAX(version_no)  FROM QMS_REC_CON_SELLRATESDTL   WHERE buyrateid = v_buyrateid AND lane_no = v_laneno);
 EXCEPTION WHEN NO_DATA_FOUND THEN
 v_margin_perc :=0.00000;
 END;
 --ended by subrahmanyam for security charge missing issue
          INSERT INTO QMS_REC_CON_SELLRATESDTL_ACC
            (rec_con_id,
             carrier_id,
             servicelevel_id,
             chargerate,
             upper_bound,
             lowrer_bound,
             margin_perc,
             weightbreakslab,
             frequency,
             chargerate_indicator,
             lane_no,
             line_no,
             buyrateid,
             transit_time,
             origin,
             destination,
             console_type,
             ID,
             rec_con_id_sell,
             lane_no_sell,
             INVALIDATE,
             ai_flag,
             rate_description,
             version_no,
             origin_country,
             desti_country,
             notes)
          VALUES
            (v_seq,
             j.carrier,
             j.srvlevel,
             j.chargerate,
             j.ubound,
             j.lbound,
            /* j.perc,*/--commented by subrahmanyam for security charge missing issue
             v_margin_perc,--added by subrahmanyam for security charge missing issue
             j.wbslab,
             j.freq,
             j.ch_indicator,
             --v_laneno,
             p_newlaneno,
             m,
             --v_buyrateid,
             p_newbuyrateid,
             j.transittime,
             j.origin,
             j.dest,
             DECODE(j.consoletype, 'P', NULL, j.consoletype),
             v_id,
             i.rec_con_id,
             --v_lane_no,        --@@Modified by Kameswari for the WPBN issue-122621
             --v_laneno,
             p_newlaneno,
             'F',
             'A',
             DECODE(j.rate_description,
                    '',
                    'A FREIGHT RATE',
                    j.rate_description),
             p_newversionno, -- VLAKSHMI
             --  v_versionno,
             j.origin_country,
             j.dest_country,
             j.notes);

 /*         UPDATE QMS_BUYRATES_DTL DTL
             SET ACC_FLAG = 'Y'
           WHERE BUYRATEID = v_buyrateid
             AND LANE_NO = v_laneno
             AND VERSION_NO = p_newversionno
             AND DTL.ACTIVEINACTIVE IS NULL;*/

             UPDATE QMS_BUYRATES_DTL DTL
             SET ACC_FLAG = 'Y'
           WHERE BUYRATEID = p_newbuyrateid
             AND LANE_NO = p_newlaneno
             AND VERSION_NO = p_newversionno
             AND DTL.ACTIVEINACTIVE IS NULL;
          m := m + 1;
        END LOOP;
      END IF;


      UPDATE QMS_REC_CON_SELLRATESDTL DTL
         SET ACCEPTANCE_FLAG = 'Y'
       WHERE BUYRATEID = v_buyrateid
         AND LANE_NO = v_laneno
         AND version_no < p_newversionno
         AND DTL.AI_FLAG = 'A';

      UPDATE QMS_REC_CON_SELLRATESDTL DTL
         SET DTL.AI_FLAG = 'I'
       WHERE DTL.BUYRATEID = v_buyrateid
         AND DTL.LANE_NO = v_laneno
         AND version_no < p_newversionno
         AND DTL.AI_FLAG = 'A'
         AND EXISTS (SELECT 'X'
                FROM QMS_REC_CON_SELLRATESMASTER MAS
               WHERE MAS.RC_FLAG = 'C'
                 AND MAS.REC_CON_ID = DTL.REC_CON_ID
                 AND DTL.BUYRATEID = v_buyrateid
                 AND DTL.version_no=v_versionno);
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE BUYRATEID = v_buyrateid
         AND LANE_NO = v_laneno
         AND version_no < p_newversionno;
      --End;
  /* Else
     UPDATE QMS_REC_CON_SELLRATESDTL DTL
         SET DTL.AI_FLAG = 'I'
       WHERE DTL.BUYRATEID = v_buyrateid
         AND DTL.LANE_NO = v_laneno
          AND DTL.AI_FLAG = 'A'
         AND DTL.version_no < p_newversionno;*/

    END IF;
     END LOOP;
       UPDATE QMS_BUYRATES_DTL
         SET activeinactive = 'I'
       WHERE buyrateid = v_buyrateid
         AND lane_no = v_laneno
         AND version_no < p_newversionno ;
  END IF;

  END;


END Pkg_Qms_Buyrates;

/

/

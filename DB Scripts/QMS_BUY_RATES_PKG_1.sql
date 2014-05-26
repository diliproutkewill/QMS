--------------------------------------------------------
--  DDL for Package Body QMS_BUY_RATES_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_BUY_RATES_PKG" AS
  /*  Using this Procedure for Add,Modify,View,Invalidate and getting buy rates information
   *  from QMS_BUYRATES_DTL and QMS_BUYRATES_MASTER
   *  It is passing the in parameters are
   *  p_org_locs             VARCHAR2,
   *  p_dest_locs            VARCHAR2,
   *  p_terminal             VARCHAR2,
   *  p_rate_type            VARCHAR2,
   *  p_weight_break         VARCHAR2,
   *  p_srvlevl              VARCHAR2,
   *  p_carrier              VARCHAR2,
   *  p_shmode               VARCHAR2,
   *  p_operation            VARCHAR2,
   *  p_date                 DATE,
   *  p_page_no              NUMBER ,
   *  p_page_rows            NUMBER,
   *  out parameters are
   *  p_tot_rec              NUMBER,
   *  P_tot_pages            NUMBER,
   *  p_rs                   resultset.
  */
  PROCEDURE buy_rates_proc(p_org_locs     VARCHAR2,
                           p_dest_locs    VARCHAR2,
                           p_terminal     VARCHAR2,
                           p_rate_type    VARCHAR2,
                           p_weight_break VARCHAR2,
                           p_srvlevl      VARCHAR2,
                           p_carrier      VARCHAR2,
                           p_shmode       VARCHAR2,
                           p_operation    VARCHAR2,
                           p_page_no      NUMBER DEFAULT 1,
                           p_page_rows    NUMBER DEFAULT 50,
                           p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                           p_tot_rec      OUT NUMBER,
                           P_tot_pages    OUT NUMBER,
                           p_rs           OUT resultset) AS
    v_org_locs     VARCHAR2(32767) := '';
    v_dest_locs    VARCHAR2(32767) := '';
    v_terminal     VARCHAR2(300);
    v_rate_type    VARCHAR2(300);
    v_weight_break VARCHAR2(300);
    --v_ratedesc     VARCHAR2(300); --@@Added by Kameswari for the CR
    v_ratedesc     VARCHAR2(32767);
    v_srvlevl      VARCHAR2(32767) := '';
    v_srvlevl_id   VARCHAR2(32767) := ''; --@@Added by Kameswari issuid:127694
    v_carrier      VARCHAR2(32767) := '';
    v_currency     VARCHAR2(300);
    v_shmode       VARCHAR2(300);
    v_operation    VARCHAR2(300);
    v_type         VARCHAR2(1);
    v_terminals    VARCHAR2(32767);
    v_chargerate   VARCHAR2(400) := '';
    v_sqlIn        VARCHAR2(4000);
    v_sqlnew       VARCHAR2(32767);
    v_sql1         VARCHAR2(32767);
    v_sql_sur      VARCHAR2(32767);
    v_sql2         VARCHAR2(32767);
    v_sql3         VARCHAR2(32767);
    v_sql4         VARCHAR2(32767);
    v_sql5         VARCHAR2(32767);
    v_sql6         VARCHAR2(32767);
    v_sql7         VARCHAR2(32767); --@@Added by Kameswari issuid:127694
    v_base         VARCHAR2(30);
    v_break        VARCHAR2(4000);
    v_temp         VARCHAR2(200);
    v_countryid    VARCHAR2(30);
    v_countryid1   VARCHAR2(30);
    v_temp1        VARCHAR2(10);
    v_temp2        VARCHAR2(10);
    v_temp3        VARCHAR2(10);
    v_temp4        VARCHAR2(300); --@@Added by Kameswari for the CR
    v_ubound       VARCHAR2(1000);
    v_lbound       VARCHAR2(1000);
    v_indicator    VARCHAR2(1000);
    v_qry          VARCHAR2(10);
    v_rc_c1        resultset;
    v_rc_c2        resultset;
    v_break_slab   VARCHAR2(20);
    v_lineno       VARCHAR2(20); --@@Added by Kameswari for Surcharge Enhancement
    v_opr_adm_flag VARCHAR2(2);
    v_exit         NUMBER;
    v_rate         VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancement
    v_desc         VARCHAR2(1000);
    v_org_reg      VARCHAR2(1000):= 'T';
    v_dest_reg     VARCHAR2(1000):= 'T';
    v_chargerate_TEMP VARCHAR2(100);
    v_break_TEMP VARCHAR2(100);
    v_temp4_TEMP VARCHAR2(100);
    v_sch_currency  VARCHAR2(100);  --kishore
    v_temp_currency   VARCHAR2(200); --kishore
    v_surchrge           VARCHAR2(100); --Gowtham
    v_currency_TEMP     VARCHAR2(100);--Gowtham
    v_charge_count     number(2);--govind

    CURSOR c1 IS(
      SELECT parent_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY child_terminal_id = PRIOR parent_terminal_id
       START WITH child_terminal_id = p_terminal
      UNION
      SELECT child_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
       START WITH parent_terminal_id = p_terminal
      UNION
      SELECT terminalid term_id
        FROM FS_FR_TERMINALMASTER
       WHERE oper_admin_flag = 'H'
      UNION
      SELECT p_terminal term_id FROM DUAL);

    CURSOR c2 IS
      SELECT p_terminal term_id FROM DUAL;

    CURSOR C4 IS
      SELECT child_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
       START WITH parent_terminal_id = p_terminal
      UNION
      SELECT p_terminal term_id FROM DUAL;

    CURSOR C3 IS
      SELECT DISTINCT terminalid term_id
        FROM FS_FR_TERMINALMASTER
       WHERE actv_flag = 'A'
         AND (INVALIDATE = 'F' OR INVALIDATE IS NULL);

  BEGIN
    v_terminal     := '''' || UPPER(p_terminal) || '''';
    v_rate_type    := '''' || UPPER(p_rate_type) || '''';
    v_weight_break := '''' || UPPER(p_weight_break) || '''';
    v_shmode       := '''' || p_shmode || '''';

    SELECT oper_admin_flag
      INTO v_opr_adm_flag
      FROM FS_FR_TERMINALMASTER
     WHERE terminalid = p_terminal;

    IF UPPER(v_opr_adm_flag) <> 'H' THEN
      IF UPPER(TRIM(p_operation)) = 'MODIFY' OR
         UPPER(TRIM(p_operation)) = 'INVALIDE' THEN
        Dbms_Session.set_context('BUYRATES_CONTEXT',
                                 'v_terminal_id',
                                 p_terminal);

        v_terminals := 'SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN
                            CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                            START WITH parent_terminal_id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                            UNION SELECT sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'') term_id FROM DUAL';
        /*FOR i IN C4
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;*/
      END IF;

      IF UPPER(TRIM(p_operation)) = 'VIEW' OR
         UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN
        Dbms_Session.set_context('BUYRATES_CONTEXT',
                                 'v_terminal_id',
                                 p_terminal);
        v_terminals := 'Select Parent_Terminal_Id Term_Id
                            FROM FS_FR_TERMINAL_REGN
                          CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
                           START WITH Child_Terminal_Id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                          UNION
                          SELECT Child_Terminal_Id Term_Id
                            FROM FS_FR_TERMINAL_REGN
                          CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                           START WITH Parent_Terminal_Id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                          UNION
                          SELECT Terminalid Term_Id
                            FROM FS_FR_TERMINALMASTER
                           WHERE Oper_Admin_Flag = ''H''
                          UNION
                          SELECT sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'') Term_Id FROM Dual';
        /*FOR i IN c1
        LOOP
           v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
        END LOOP;*/
      END IF;
    ELSE
      v_terminals := 'Select Terminalid
                       FROM FS_FR_TERMINALMASTER
                       WHERE Actv_Flag = ''A''
                         AND (Invalidate = ''F'' OR Invalidate IS NULL)';
      /*FOR i IN c3
      LOOP
         v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
      END LOOP;*/
    END IF;

    IF UPPER(TRIM(p_operation)) = 'MODIFY' OR
       UPPER(TRIM(p_operation)) = 'VIEW' OR
       UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN
      v_qry := '=' || '''' || 'F' || '''';
    ELSE
      v_qry := 'like ' || '''' || '%' || '''';
    END IF;

    EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data_1');

    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_1');

    --v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
    --DBMS_OUTPUT.put_line ('Comman Proc hi ---->' || v_qry);

    IF p_org_locs IS NOT NULL THEN
     /*v_org_locs := ' AND qbd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';*/
  v_org_reg :=' AND qbd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';
    END IF;
    PRINT_OUT('LENGTH'||LENGTH ((v_org_reg )));
    IF (  p_org_countries IS NOT NULL  AND LENGTH ((v_org_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_org_countries) || '' || ') )';

    END IF;

  IF (  p_org_regions IS NOT NULL  AND LENGTH (TRIM (v_org_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_org_regions) || '' || ') )';

    END IF;
    IF p_dest_locs IS NOT NULL THEN
     /* v_dest_locs := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';*/
     v_dest_reg := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';
    END IF;
    IF (  p_dest_countries IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_dest_reg := ' AND qbd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_dest_reg :=  ' AND qbd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_dest_reg := v_dest_reg || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_dest_countries) || '' || ') )';

    END IF;

 IF (  p_dest_regions IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_dest_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_dest_reg :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_dest_reg := v_dest_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_dest_regions) || '' || ') )';

    END IF;
    IF p_srvlevl IS NOT NULL THEN
      v_srvlevl := '  AND qbd.service_level IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_srvlevl) || '' ||
                   ',''SCH'')';
    END IF;
    ----@@Added by Kameswari issuid:127694
    IF p_srvlevl IS NOT NULL THEN
      v_srvlevl_id := '  AND qbd.service_level IN (' || '' ||
                      Qms_Rsr_Rates_Pkg.seperator(p_srvlevl) || '' || ')';
    END IF;
    --@Added End
    IF p_carrier IS NOT NULL THEN
      v_carrier := '   AND qbd.carrier_id IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_carrier) || '' || ' ) ';
    END IF;

    IF UPPER(p_weight_break) = 'FLAT' THEN
      v_break := 'Min,Flat,';
    END IF;

    v_sqlIn := ' AND (qbd.buyrateid, qbd.lane_no,qbd.version_No ) in (SELECT  buyrateid, lane_no ,version_No FROM (SELECT t1.*, ROWNUM rn FROM (SELECT DISTINCT qbd.buyrateid, qbd.lane_no ,qbd.version_No';

    v_sql1 := 'insert into temp_data_1(BUYRATEID, VERSION_NO,WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO,LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION, SUR_CHARGE_CURRENCY) SELECT  qbd.buyrateid,qbd.version_no,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,DECODE(QBD.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',QBD.RATE_DESCRIPTION)RATE_DESCRIPTION, DECODE(QBD.SUR_CHARGE_CURRENCY,'''',''-'',QBD.SUR_CHARGE_CURRENCY) '; --@@Modified by Kameswari for the CR
    v_sql6 := '  FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid and qbd.version_no=qbm.version_no and (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) ';
             -- v_org_locs; commented and modified by phani sekhar for wpbn 171213
     IF v_org_reg <>'T' THEN
     v_sql6 := v_sql6 ||v_org_reg;
     END IF;

     IF v_dest_reg <>'T' THEN
     v_sql4 := v_sql4 || v_dest_reg;
     v_sql7 := v_sql7  || v_dest_reg;
     END IF;

    v_sql4 := v_sql4 || v_srvlevl ||--replace v_dest_locs with v_dest_reg by phani for 171213
              '   AND qbd.activeinactive IS NULL AND (qbd.INVALIDATE IS NULL OR qbd.INVALIDATE  ' ||
              v_qry || ') ';
    ----@@Added by Kameswari issuid:127694
    v_sql7 := v_sql7 || v_srvlevl_id ||--replace v_dest_locs with v_dest_reg by phani for wpbn 171213
              '   AND qbd.activeinactive IS NULL AND (qbd.INVALIDATE IS NULL OR qbd.INVALIDATE  ' ||
              v_qry || ') ';
    --Added End
    v_sql2 := '   AND qbm.shipment_mode =' || v_shmode ||
              '  AND qbm.weight_break =' || v_weight_break;
    /* v_sql3 := '  AND qbm.rate_type =' || v_rate_type || v_carrier ||
    '   AND qbm.terminalid IN (' || v_terminals ||
    ') and ( qbd.VALID_UPTO>=' || '''' || p_date || '''' ||
    ' or qbd.VALID_UPTO is null ) ';*/
    v_sql3 := '  AND qbm.rate_type =' || v_rate_type || v_carrier ||
              '   AND qbm.terminalid IN (' || v_terminals || ') ';

    IF UPPER(p_operation) = 'DOWNLOAD' THEN
      /* v_sql5 :=
         'insert into base_data_1(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,TERMINALID,VALIDUPTO,INVALIDATE,NOTES,CURRENCY,EFROM,DENSITY_CODE) select  distinct qbd.buyrateid buyrateid, qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate,
         qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.TERMINALID,qbd.VALID_UPTO,qbd.INVALIDATE,qbd.NOTES,qbm.CURRENCY,qbd.EFFECTIVE_FROM,qbd.DENSITY_CODE   FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid   '
      || v_org_locs;*/ --@@Modified by Kameswari for the WPBN issue-77821
      v_sql5 := 'insert into base_data_1(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,TERMINALID,VALIDUPTO,INVALIDATE,NOTES,CURRENCY,EFROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES, SUR_CHARGE_CURRENCY) select   qbd.buyrateid buyrateid, qbd.version_no ,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate,
            qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.TERMINALID,qbd.VALID_UPTO,qbd.INVALIDATE,qbd.NOTES,qbm.CURRENCY,qbd.EFFECTIVE_FROM,qbd.DENSITY_CODE,qbd.RATE_DESCRIPTION,qbd.EXTERNAL_NOTES, qbd.SUR_CHARGE_CURRENCY FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.line_no=''0'' AND qbd.buyrateid = qbm.buyrateid   AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND qbd.version_no=qbm.version_no AND (qbd.rate_description=''A FREIGHT RATE''OR qbd.rate_description IS NULL) ' ;
               -- v_org_locs;commented and modified by phani sekhar for wpbn 171213
               -- v_org_reg;
               -- SUR_CHARGE_CURRENCY was added by kishorep in v_sql5
      IF v_org_reg <>'T' THEN
      v_sql5 := v_sql5 ||v_org_reg;
      END IF;
    ELSE
      v_sql5 := 'insert into base_data_1(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,TERMINALID,VALIDUPTO,INVALIDATE,NOTES,CURRENCY,EFROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY) select buyrateid, version_no,carrier_id, origin , destination ,service_level,weight_break_slab,transit_time, frequency, chargerate,
            lane_no,org_countryid,Dest_countryid,TERMINALID,VALID_UPTO,INVALIDATE,NOTES,CURRENCY,EFFECTIVE_FROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY FROM (SELECT t1.*, ROWNUM rn FROM ( ';
    END IF;

    v_sqlnew := ' select  distinct qbd.buyrateid buyrateid, qbd.version_no version_no,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate,
            qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.TERMINALID,qbd.VALID_UPTO,qbd.INVALIDATE,qbd.NOTES,qbm.CURRENCY,qbd.EFFECTIVE_FROM,qbd.DENSITY_CODE,qbd.RATE_DESCRIPTION RATE_DESCRIPTION,qbd.EXTERNAL_NOTES EXTERNAL_NOTES, qbd.SUR_CHARGE_CURRENCY FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  AND qbd.version_no=qbm.version_no AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND (qbd.rate_description=''A FREIGHT RATE''OR qbd.rate_description IS NULL) ' ;
                --v_org_locs; commented and modified by phani sekhar for wpbn 171213
          --      v_org_reg;
           IF v_org_reg <>'T' THEN
      v_sqlnew := v_sqlnew ||v_org_reg;
      END IF;

    IF UPPER(p_operation) = 'DOWNLOAD' THEN
      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);
      PRINT_OUT(v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);
    ELSE
      /* EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                              v_sqlIn || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                              ' ORDER BY buyrateid,lane_no) t1
                                 WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                                 WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ')
              Using p_page_no, p_page_rows, p_page_rows, p_page_no, p_page_rows; --Modified bu Kameswari for the WPBN issue-70107
      */

      ----@@Modified by Kameswari issuid:127694
      print_out(v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                        v_sqlIn || v_sql6 || v_sql7 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid,lane_no) t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ');
      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                        v_sqlIn || v_sql6 || v_sql7 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid,lane_no) t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ')
        USING p_page_no, p_page_rows, p_page_rows, p_page_no, p_page_rows;
      -- end
    END IF;

     --DBMS_OUTPUT.put_line ('hi');
    IF UPPER(p_operation) = 'DOWNLOAD' THEN
     print_out(v_sql5 || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no,weight_break_slab');
      EXECUTE IMMEDIATE (v_sql5 || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no,weight_break_slab');
    ELSE
    print_out('---------------');
    print_out(v_sql5 || v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no' ||
                        ') t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)');

      EXECUTE IMMEDIATE (v_sql5 || v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no' ||
                        ') t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)')
        USING p_page_no, p_page_rows, p_page_rows, p_page_no, p_page_rows;
    END IF;
 --  EXECUTE IMMEDIATE ('iNSERT INTO TEMP_DATA_1_test SELECT * FROM temp_data_1');
    COMMIT;
    --DBMS_OUTPUT.put_line ('hi');
    -- DBMS_OUTPUT.put_line ('hi');

    FOR j IN (SELECT buyrateid,
                  version_no,
                     weight_break_slab,
                     chargerate,
                     lane_no,
                     origin,
                     destination
                FROM BASE_DATA_1
               ORDER BY buyrateid) LOOP
      v_chargerate := '';
      v_ratedesc   := '';
      --DBMS_OUTPUT.put_line ('hi ');
      v_break := '';
v_chargerate_TEMP :='';
v_break_TEMP :='';
v_temp4_TEMP :='';
v_charge_count:=0;
      v_currency_TEMP       := '';-- Gowtham
      v_sch_currency    := '';
    --  IF (UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1) = FALSE THEN
      IF (UPPER(p_weight_break) = 'LIST' ) = FALSE THEN
-- DBMS_OUTPUT.put_line ('j.Buyrateid ' || j.Buyrateid);
  --DBMS_OUTPUT.put_line ('j.Lane_No ' || j.Lane_No);
       IF v_charge_count =0 THEN
       BEGIN
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=0 AND WEIGHT_BREAK_SLAB=''BASIC''' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;

          v_lbound    := v_temp1 || ',';
        v_ubound    := v_temp2 || ',';
        v_indicator := v_temp3 || ',';
         v_chargerate_TEMP := v_chargerate || ',';
         v_break_TEMP := v_break || ',';
         v_temp4_TEMP := v_temp4 || ',';
        v_currency_TEMP    := v_temp_currency || ',';

        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=1 AND UPPER(WEIGHT_BREAK_SLAB)=''MIN''' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;

          v_lbound    := v_lbound||v_temp1 || ',';
        v_ubound    := v_ubound||v_temp2 || ',';
        v_indicator := v_indicator||v_temp3 || ',';
         v_chargerate := v_chargerate_TEMP||v_chargerate ;
         v_break := v_break_TEMP||v_break ;
         v_temp4 := v_temp4_TEMP||v_temp4 ;
        v_currency_TEMP    := v_currency_TEMP||v_temp_currency ;



        v_charge_count := v_charge_count +1;

       EXCEPTION
       WHEN NO_DATA_FOUND THEN
       v_charge_count := v_charge_count+1;
       v_lbound       := '-'||',';
        v_ubound      := '-'|| ',';
        v_indicator   := '-' || ',';
         v_chargerate_TEMP := '-' || ',';
         v_break_TEMP      := 'BASIC' || ',';
         v_temp4_TEMP      := 'A FREIGHT RATE' || ',';
        v_currency_TEMP    := '-' || ',';
       EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=0 AND UPPER(WEIGHT_BREAK_SLAB)=''MIN''' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;

        v_lbound    := v_lbound|| v_temp1 || ',';
        v_ubound    := v_lbound|| v_temp2 || ',';
        v_indicator := v_indicator|| v_temp3 || ',';
         v_chargerate := v_chargerate_TEMP||v_chargerate ;
         v_break := v_break_TEMP|| v_break ;
         v_temp4 := v_temp4_TEMP|| v_temp4 ;
        v_currency_TEMP    := v_currency_TEMP|| v_temp_currency ;
        v_charge_count := v_charge_count +1;
     END;
ELSE
        BEGIN
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=1 AND UPPER(WEIGHT_BREAK_SLAB)=''MIN''' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;
         EXCEPTION
        WHEN NO_DATA_FOUND THEN

        v_charge_count := v_charge_count+1;
       v_lbound       := '-'||',';
        v_ubound      := '-'|| ',';
        v_indicator   := '-' || ',';
         v_chargerate_TEMP := '-' || ',';
         v_break_TEMP      := 'MIN' || ',';
         v_temp4_TEMP      := 'A FREIGHT RATE' || ',';
        v_currency_TEMP    := '-' || ',';

  EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=1' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;

        v_lbound    := v_lbound || v_temp1||',';
        v_ubound    := v_ubound || v_temp2||',';
        v_indicator := v_indicator ||v_temp3||',';
        v_chargerate :=  v_chargerate_TEMP|| v_chargerate;
         v_break := v_break_TEMP||v_break ;
         v_temp4 := v_break_TEMP||v_temp4 ;
        v_currency_TEMP := v_currency_TEMP || v_temp_currency; -- Gowtham
       -- v_sch_currency := v_sch_currency ||v_temp_currency || ','; -- Gowtham

        --v_ratedesc := v_temp4 || ',';
        --DBMS_OUTPUT.put_line(v_ratedesc);
        END;
        END IF;
      ELSE
        v_temp1     := '';
        v_temp2     := '';
        v_temp3     := '';
        v_temp4     := '';
        v_lbound    := '';
        v_ubound    := '';
        v_indicator := '';
        v_temp_currency := ''; -- Gowtham
      END IF;

      IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_base       := v_chargerate;
        v_chargerate := '';
        v_rate       := v_temp4;
        v_temp4      := '';
        v_surchrge       := v_temp_currency;
        v_temp_currency := '';
      ELSIF UPPER(p_weight_break) = 'FLAT' OR
            UPPER(p_weight_break) = 'SLAB' THEN
        v_chargerate := v_chargerate || ',';
        v_ratedesc   := v_temp4 || ',';
        v_sch_currency := v_currency_TEMP || ',';--v_temp_currency || ',';
      END IF;

      --DBMS_OUTPUT.put_line (v_base);

      /*IF (UPPER (p_weight_break) <> 'FLAT')
      THEN*/
      --@@ Commented and Added by GOvind for 219973
/*      IF UPPER(p_weight_break) = 'LIST' and p_shmode = 1 THEN
        v_sql1 := 'SELECT DISTINCT  weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 0   ORDER BY break_slab  ';
      ELSIF UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1 THEN
        v_sql1 := 'SELECT DISTINCT  weight_break_slab break_slab,rate_description   FROM temp_data_1  ORDER BY break_slab  ';
      ELSIF UPPER(p_weight_break) = 'FLAT' THEN
   --@@ Commented and added by SUbrahmanyam for the CR... 219973
    --    v_sql1 := 'SELECT DISTINCT  weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE line_no > 0   ORDER BY rate_description  ';
        v_sql1 := 'SELECT   weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE line_no > 0   ORDER BY   line_no';

      ELSE
        v_sql1 :=  -- 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0 ORDER BY break_slab ';
        --@@ Commented and added by SUbrahmanyam for the CR... 219973
        -- 'SELECT DISTINCT  weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 0 ORDER BY rate_description ';
         'SELECT   weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 0 ORDER BY line_no ';
      END IF;
*/


      IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE  RATE_DESCRIPTION=''A FREIGHT RATE''  ORDER BY  break_slab';
      ELSIF UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1 THEN
        v_sql1 := 'SELECT    DISTINCT weight_break_slab break_slab   FROM temp_data_1 WHERE  RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY  break_slab  ';
      ELSIF UPPER(p_weight_break) = 'FLAT' THEN
        v_sql1 := 'SELECT    DISTINCT  weight_break_slab break_slab   FROM temp_data_1  WHERE line_no > 1   AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY    break_slab';
      ELSE
        v_sql1 :=   'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no >=0 AND RATE_DESCRIPTION=''A FREIGHT RATE'' AND UPPER(weight_break_slab) NOT IN(''BASIC'',''MIN'')  ORDER BY  break_slab ';
      END IF;

          IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 0 AND RATE_DESCRIPTION<>''A FREIGHT RATE''  and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      ELSIF UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1 THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE  RATE_DESCRIPTION<>''A FREIGHT RATE'' and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      ELSIF UPPER(p_weight_break) = 'FLAT' THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE line_no > 1  AND RATE_DESCRIPTION<>''A FREIGHT RATE''  and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';

      ELSE
        v_sql_sur :=   'SELECT   weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 1 AND RATE_DESCRIPTION<>''A FREIGHT RATE'' and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      END IF;

--Ended for 219973
      --DBMS_OUTPUT.put_line (v_sql1);

      OPEN v_rc_c1 FOR v_sql1;

      LOOP
        --DBMS_OUTPUT.put_line (' Here..');

        FETCH v_rc_c1
          INTO v_break_slab;

        EXIT WHEN v_rc_c1%NOTFOUND;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp := '-';
        END;
        --@@Added by Kameswari for the CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp4
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp4 := '-';
          --DBMS_OUTPUT.put_line (v_temp4);
        END;

        --@@CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_temp1
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp1 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_temp2
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp2 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION =''A FREIGHT RATE''')
            INTO v_temp3
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;

          IF v_temp3 IS NULL THEN
            v_temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp3 := '-';
        END;
        --kishore
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(SUR_CHARGE_CURRENCY,'''',''-'',NVL(SUR_CHARGE_CURRENCY,''-''))SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp_currency
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp_currency := '-';
        END;
        --kishore

        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE WEIGHT_BREAK_SLAB=:V_BREAK_SLAB AND
        BUYRATEID=:V_BUYRATEID AND VERSION_NO=:V_VERSION_NO AND LANE_NO=:V_LANE_NO AND RATE_DESCRIPTION=''A FREIGHT RATE''')
        USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        v_chargerate := v_chargerate || v_temp || ',';

        v_lbound := v_lbound || v_temp1 || ',';
        v_ubound := v_ubound || v_temp2 || ',';
        v_indicator := v_indicator || v_temp3 || ',';
         v_ratedesc := v_ratedesc || v_temp4 || ',';
         v_sch_currency := v_sch_currency || v_temp_currency || ','; --kishore

      END LOOP;

      IF UPPER(p_weight_break) = 'LIST' AND P_SHMODE = 1 THEN
        --v_chargerate := v_chargerate || v_base || ',';
        IF v_base <>'' THEN
        v_chargerate := v_base || ',' || v_chargerate;
        ELSE
        v_chargerate := v_base ||  v_chargerate;
        END IF;
        --v_ratedesc := v_ratedesc || v_rate|| ',';
        IF v_rate <>'' THEN
        v_ratedesc := v_rate || ',' || v_ratedesc;
        ELSE
              v_ratedesc := v_rate ||  v_ratedesc;
        END IF;
        -- Added by Gowtham
        IF v_surchrge <> '' THEN
           v_sch_currency := v_surchrge ||','|| v_sch_currency;
        ELSE
            v_sch_currency := v_surchrge||v_sch_currency;
        END IF;
        -- End Gowtham
      END IF;

      v_base  := v_break;
      v_break := '';
      --DBMS_OUTPUT.put_line (v_base || '  ' || v_chargerate);

      CLOSE v_rc_c1;

      OPEN v_rc_c1 FOR v_sql1;

      LOOP
        FETCH v_rc_c1
          INTO v_break_slab;

        EXIT WHEN v_rc_c1%NOTFOUND;
        v_break := v_break || ',' || v_break_slab;
      END LOOP;

      CLOSE v_rc_c1;
--@@Added by Subrahmanyam for 219973   for Handling surcharge Cursor separately


 OPEN v_rc_c2 FOR v_sql_sur;

      LOOP
        --DBMS_OUTPUT.put_line (' Here..');

        FETCH v_rc_c2
          INTO v_break_slab, v_desc;

        EXIT WHEN v_rc_c2%NOTFOUND;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp := '-';
        END;
        --@@Added by Kameswari for the CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(SUR_CHARGE_CURRENCY,'''',''-'',SUR_CHARGE_CURRENCY)SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp_currency
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_desc <> 'A FREIGHT RATE' THEN
          v_temp_currency := '**';
          ELSE
            v_temp_currency := '-';
            END IF;

        END;


         BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp4
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_desc <> 'A FREIGHT RATE' THEN
          v_temp4 := '**';
          ELSE
            v_temp4 := '-';
            END IF;

          --DBMS_OUTPUT.put_line (v_temp4);
        END;



        --@@CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp1
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp1 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp2
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp2 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION =:v_desc')
            INTO v_temp3
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;

          IF v_temp3 IS NULL THEN
            v_temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp3 := '-';
        END;
        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE WEIGHT_BREAK_SLAB=:V_BREAK_SLAB AND
        BUYRATEID=:V_BUYRATEID AND VERSION_NO=:V_VERSION_NO AND LANE_NO=:V_LANE_NO  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:V_RATE_DESC')
        USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        v_chargerate := v_chargerate || v_temp || ',';

        v_lbound := v_lbound || v_temp1 || ',';
        v_ubound := v_ubound || v_temp2 || ',';
        v_indicator := v_indicator || v_temp3 || ',';
         v_ratedesc := v_ratedesc || v_temp4 || ',';
         v_sch_currency := v_sch_currency || v_temp_currency || ','; --kishore

      END LOOP;

/*      IF UPPER(p_weight_break) = 'LIST' AND P_SHMODE = 1 THEN
        --v_chargerate := v_chargerate || v_base || ',';
        v_chargerate := v_base || ',' || v_chargerate;
        --v_ratedesc := v_ratedesc || v_rate|| ',';
        v_ratedesc := v_rate || ',' || v_ratedesc;
      END IF;

      v_base  := v_break;
      v_break := '';
      --DBMS_OUTPUT.put_line (v_base || '  ' || v_chargerate);*/

      CLOSE v_rc_c2;

      OPEN v_rc_c2 FOR v_sql_sur;

      LOOP
        FETCH v_rc_c2
          INTO v_break_slab, v_desc;

        EXIT WHEN v_rc_c2%NOTFOUND;
        v_break := v_break || ',' || v_break_slab;
      END LOOP;

      CLOSE v_rc_c2;
--@@Ended by Subrahmanyam for 219973   for Handling surcharge Cursor separately

      IF (UPPER(p_weight_break) = 'SLAB' OR UPPER(p_weight_break) = 'FLAT') THEN
        v_break := v_base || v_break;
      ELSIF UPPER(p_weight_break) = 'LIST' AND P_SHMODE = 1 THEN
        --v_break := v_break || ',' || v_base;
        --v_break := SUBSTR (v_break, 2, LENGTH (v_break));
        v_break := v_base || v_break;
      ELSE
        v_break := SUBSTR(v_break, 2, LENGTH(v_break));
      END IF;
      /* ELSE
      SELECT weight_break_slab, chargerate
        INTO v_base, v_temp
        FROM temp_data_1
       WHERE line_no > 0
         AND buyrateid = j.buyrateid
         AND lane_no = j.lane_no;

      v_break := v_break || ',' || v_base;
      v_chargerate := v_chargerate || v_temp || ',';*/
      --END IF;
      COMMIT;--@@Added by Kameswari fo the WPBN issue-160743
      UPDATE BASE_DATA_1
         SET weight_break_slab = v_break,
             chargerate        = SUBSTR(v_chargerate,
                                        1,
                                        LENGTH(v_chargerate) - 1),
             ubound            = SUBSTR(v_ubound, 1, LENGTH(v_ubound) - 1),
             lbound            = SUBSTR(v_lbound, 1, LENGTH(v_lbound) - 1),
             c_indicator       = SUBSTR(v_indicator,
                                        1,
                                        LENGTH(v_indicator) - 1),
             rate_description  = SUBSTR(v_ratedesc,
                                        1,
                                        LENGTH(v_ratedesc) - 1),
--- Gowtham Start
             sur_charge_currency =SUBSTR(v_sch_currency,
                                          1,
                                           LENGTH(v_sch_currency) - 1)
--- End  Gowtham
       WHERE buyrateid = j.buyrateid
         AND lane_no = j.lane_no;

      IF p_shmode <> 2 THEN
        BEGIN
          SELECT countryid
            INTO v_countryid
            FROM FS_FR_LOCATIONMASTER
           WHERE locationid = j.origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid := '';
        END;
      ELSE
        BEGIN
          SELECT countryid
            INTO v_countryid
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = j.origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid := '';
        END;
      END IF;

      IF p_shmode <> 2 THEN
        BEGIN
          SELECT countryid
            INTO v_countryid1
            FROM FS_FR_LOCATIONMASTER
           WHERE LOCATIONID = j.destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid1 := '';
        END;
      ELSE
        BEGIN
          SELECT countryid
            INTO v_countryid1
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = j.destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid1 := '';
        END;
      END IF;

      UPDATE BASE_DATA_1
         SET org_countryid = v_countryid, dest_countryid = v_countryid1
       WHERE buyrateid = j.buyrateid
         AND lane_no = j.lane_no
         AND version_no =j.version_no;
    END LOOP;
 COMMIT;--@@Added by Kameswari fo the WPBN issue-160743
    /*    FOR i IN (SELECT weight_break_slab, chargerate
                FROM base_data_1)
    LOOP
       DBMS_OUTPUT.put_line (   i.weight_break_slab
                             || '              '
                             || i.chargerate
                            );
    END LOOP;*/

    IF UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN

      OPEN p_rs FOR
        SELECT * FROM BASE_DATA_1;

    ELSE

      /*OPEN p_rs
      FOR
         SELECT *
           FROM (SELECT t1.*, ROWNUM rn
                   FROM (SELECT *
                           FROM base_data_1) t1
                  WHERE ROWNUM <=
                                ((p_page_no - 1) * p_page_rows) + p_page_rows)
          WHERE rn > ((p_page_no - 1) * p_page_rows);*/
      OPEN p_rs FOR
        SELECT * FROM BASE_DATA_1 ORDER BY BUYRATEid DESC;

      /*SELECT COUNT (*)
      INTO p_tot_rec
      FROM base_data_1;*/

      EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM ( SELECT t1.*, ROWNUM rn FROM ( ' ||
                        v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ) t1) ')
        INTO p_tot_rec;

      /*SELECT CEIL ((p_tot_rec / p_page_rows))
      INTO p_tot_pages
      FROM DUAL;*/
      p_tot_pages := CEIL(p_tot_rec / p_page_rows);

    END IF;
 COMMIT;--@@Added by Kameswari fo the WPBN issue-160743
    /*EXCEPTION
    WHEN OTHERS
    THEN
       Qms_Rsr_Rates_Pkg.g_err := '<< ' || SQLERRM || ' >>';
       Qms_Rsr_Rates_Pkg.g_err_code := '<< ' || SQLCODE || ' >>';

       INSERT INTO QMS_OBJECTS_ERRORS
                   (ex_date, module_name,
                    errorcode, errormessage
                   )
            VALUES (SYSDATE, 'Qms_Buy_Rates_Pkg->buy_rates_proc',
                    Qms_Rsr_Rates_Pkg.g_err_code, Qms_Rsr_Rates_Pkg.g_err
                   );

       COMMIT;*/
  END;
  /*  Used for insert buyrates and sellrates
   *  information into QMS_REC_CON_SELLRATESDTL_ACC and QMS_REC_CON_SELLRATESMSTR_ACC tables.
   *  It is passing the in parameter is
   *  p_buyrateid NUMBER
  */
  PROCEDURE sellratesmstr_acc_proc_old(p_buyrateid NUMBER,p_versionno NUMBER) AS
    CURSOR c1(v_terminal VARCHAR2) IS(
      SELECT v_terminal term_id
        FROM DUAL
      UNION
      SELECT child_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
       START WITH parent_terminal_id = v_terminal);

    CURSOR c2 IS
      SELECT DISTINCT terminalid term_id
        FROM FS_FR_TERMINALMASTER
       WHERE actv_flag = 'A'
         AND (INVALIDATE = 'F' OR INVALIDATE IS NULL);

    v_flag        NUMBER := 0;
    v_terminals   VARCHAR2(32767);
    k_accesslevel VARCHAR2(50);
    k_terminal    VARCHAR2(30);
    v_sql1        VARCHAR2(32767);
    v_sql2        VARCHAR2(32767);
    v_sql3        VARCHAR2(32767);
    v_sql4        VARCHAR2(32767);
    v_sql5        VARCHAR2(32767);
    v_rc_c1       resultset;
    v_rec_con_id  VARCHAR2(30);
    v_lane_no     VARCHAR2(30);
    v_terminalid  VARCHAR2(30);
    v_currency    VARCHAR2(30);
    v_seq         NUMBER;
    v_id          NUMBER;
    v_acclevel    VARCHAR2(30);
    k             NUMBER(16);
    v_margin      VARCHAR2(10);
    v_type        VARCHAR2(10);
    v_basis       VARCHAR2(10);
    org_country   VARCHAR2(10);
    dest_country  VARCHAR2(10);

    v_Weight_break VARCHAR2(8);
    v_rate_type    VARCHAR2(8);
    v_shmode       VARCHAR2(1);
    v_origin       VARCHAR2(16);
    v_dest         VARCHAR2(16);
    v_carrier      VARCHAR2(5);
    v_srvclevel    VARCHAR2(8);
    v_frequency    VARCHAR2(15);
    v_console_type VARCHAR2(3);
    v_transittime  VARCHAR2(15);
  v_versionno         VARCHAR2(15);
  BEGIN
    /*SELECT accesslevel, terminalid
      INTO k_accesslevel, k_terminal
      FROM QMS_BUYRATES_MASTER
     WHERE buyrateid = p_buyrateid;

    IF k_accesslevel = 'H'
    THEN
       FOR i IN c2
       LOOP
          v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
       END LOOP;
    ELSE
       FOR i IN c1 (k_terminal)
       LOOP
          v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
       END LOOP;
    END IF;

    v_terminals := SUBSTR (v_terminals, 2, LENGTH (v_terminals) - 3);*/

    FOR i IN (SELECT DISTINCT qbd.buyrateid buyrateid,
                              qbd.lane_no lane_no,
                              qbd.version_no version_no,
                              NVL(qbm.console_type, 'P') consoletype,
                              qbm.weight_break weight_break,
                              qbm.rate_type rate_type,
                              qbm.shipment_mode shmode,
                              qbm.terminalid terminalid,
                              qbm.created_by created_by,
                              --qbd.createdtime created_time,
                              qbd.origin        origin,
                              qbd.destination   dest,
                              qbd.carrier_id    carrier,
                              qbd.service_level srvlevel,
                              qbd.frequency     freq,
                              qbm.currency      currency,
                              qbm.weight_class  weight_class,
                              qbd.transit_time  transittime

                FROM QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd
               WHERE qbm.buyrateid = qbd.buyrateid
                 AND qbd.service_level NOT IN ('SCH')
                 AND qbm.buyrateid = p_buyrateid
                 AND qbm.version_no = p_versionno
                 AND qbd.version_no=qbm.version_no
                 AND (qbd.INVALIDATE IS NULL OR UPPER(qbd.INVALIDATE) = 'F')
                 AND (qbd.activeinactive IS NULL OR
                     UPPER(qbd.activeinactive) = 'A')) LOOP
      v_sql1 := 'SELECT distinct qms.REC_CON_ID,qsd.LANE_NO,qsd.version_no,qms.TERMINALID, qms.ACCESSLEVEL  FROM QMS_REC_CON_SELLRATESMASTER qms,QMS_REC_CON_SELLRATESDTL  qsd ';

      /* v_sql1 := 'SELECT distinct qms.REC_CON_ID,qms.TERMINALID, qms.ACCESSLEVEL  FROM QMS_REC_CON_SELLRATESMASTER qms,QMS_REC_CON_SELLRATESDTL  qsd ';
      */

      v_sql2 := '  WHERE qms.RC_FLAG=''R'' AND qms.REC_CON_ID=qsd.REC_CON_ID AND qms.WEIGHT_BREAK=:v_Weight_break AND qms.RATE_TYPE=:v_rate_type AND ';
      v_sql3 := 'qms.SHIPMENT_MODE=:v_shmode';
      /*|| '  AND TERMINALID IN ('
      ||''''
      || v_terminals
      ||''''
      || ')'*/
      v_sql4 := ' and qsd.origin=:v_origin and qsd.DESTINATION=:v_dest and qsd.CARRIER_ID=:v_carrier
            AND qsd.transit_time=:v_transittime AND qsd.FREQUENCY=:v_frequency AND NVL(qsd.CONSOLE_TYPE,''P'')=:v_console_type AND (qsd.invalidate=''F'' OR qsd.invalidate IS NULL) AND qsd.SERVICELEVEL_ID=:v_srvclevel AND QSD.ai_flag=''A''';

      --  i.Srvlevel,


      OPEN v_rc_c1 FOR v_sql1 || v_sql2 || v_sql3 || v_sql4
        USING i.Weight_Break, i.Rate_Type, i.Shmode, i.Origin, i.Dest, i.Carrier, i.transittime, i.Freq, i.Consoletype, i.Srvlevel;

      LOOP
        FETCH v_rc_c1
          INTO v_rec_con_id, v_lane_no, v_versionno,v_terminalid, v_acclevel; --@@Modified by Kameswari for the WPBN issue-122621
        /*       INTO v_rec_con_id, v_terminalid, v_acclevel;
        */
        EXIT WHEN v_rc_c1%NOTFOUND;

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
           i.shmode,
           i.weight_break,
           i.rate_type,
           i.currency,
           i.weight_class,
           v_acclevel,
           v_terminalid);

        k := 0;

        FOR j IN (SELECT qbd.chargerate           chargerate,
                         qbd.weight_break_slab    wbslab,
                         qbd.chargerate_indicator ch_indicator,
                         qbd.lowerbound           lbound,
                         qbd.upperbound           ubound,
                         qbd.rate_description     rate_description,
                         qbd.service_level        srvlevel
                    FROM QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd
                   WHERE qbd.buyrateid = qbm.buyrateid
                     AND qbd.buyrateid = i.buyrateid
                     AND qbd.lane_no = i.lane_no
                     AND (qbd.INVALIDATE IS NULL OR
                         UPPER(qbd.INVALIDATE) = 'F')
                     AND (qbd.activeinactive IS NULL OR
                         UPPER(qbd.activeinactive) = 'A')
                   ORDER BY line_no) LOOP
          SELECT seq_sellrates_acc_id.NEXTVAL INTO v_id FROM DUAL;

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
             version_no,
             transit_time,
             origin,
             destination,
             console_type,
             ID,
             rec_con_id_sell,
             lane_no_sell,
             INVALIDATE,
             ai_flag,
             rate_description)
          VALUES
            (v_seq,
             i.carrier,
             j.srvlevel,
             j.chargerate,
             j.ubound,
             j.lbound,
             0.0,
             j.wbslab,
             i.freq,
             j.ch_indicator,
             i.lane_no,
             k,
             p_buyrateid,
             i.version_no,
             i.transittime,
             i.origin,
             i.dest,
             DECODE(i.consoletype, 'P', NULL, i.consoletype),
             v_id,
             v_rec_con_id,
             --v_lane_no,        --@@Modified by Kameswari for the WPBN issue-122621
             i.lane_no,
             'F',
             'A',
             DECODE(j.rate_description,
                    '',
                    'A FREIGHT RATE',
                    j.rate_description));

          UPDATE QMS_BUYRATES_DTL DTL
             SET ACC_FLAG = 'Y'
           WHERE BUYRATEID = p_buyrateid
             AND LANE_NO = i.lane_no
             AND DTL.ACTIVEINACTIVE IS NULL;

          k := k + 1;
        END LOOP;

        SELECT overall_margin, margin_type, margin_basis
          INTO v_margin, v_type, v_basis
          FROM QMS_REC_CON_SELLRATESMASTER
         WHERE rec_con_id = v_rec_con_id
           AND terminalid = v_terminalid;

        UPDATE QMS_REC_CON_SELLRATESMSTR_ACC
           SET overall_margin = v_margin,
               margin_type    = v_type,
               margin_basis   = v_basis
         WHERE rec_con_id = v_seq;

        FOR m IN (SELECT qsd.margin_perc perc,
                         qsd.notes notes,
                         qsm.overall_margin omargin,
                         qsm.margin_type mtype,
                         qsm.margin_basis mbasis,
                         qsd.WEIGHTBREAKSLAB,
                         qsd.origin_country org_country,
                         qsd.desti_country dest_country
                    FROM QMS_REC_CON_SELLRATESDTL    qsd,
                         QMS_REC_CON_SELLRATESMASTER qsm
                   WHERE qsm.rec_con_id = qsd.rec_con_id
                     AND qsm.rec_con_id = v_rec_con_id
                     AND qsd.lane_no = v_lane_no
                        --AND qsd.lane_no = i.lane_no

                     AND qsd.origin = i.origin
                        /*AND qsd.destination = i.dest
                                                                                                                                                                                                                                                                     AND qsd.carrier_id=i.carrier
                                                                                                                                                                                                                                                                     AND qsd.servicelevel_id=i.srvlevel
                                                                                                                                                                                                                                                                     AND qsd.frequency=i.freq
                                                                                                                                                                                                                                                                     AND qsd.transit_time=i.transittime*/
                     AND qsm.terminalid = v_terminalid) LOOP

          UPDATE QMS_REC_CON_SELLRATESDTL_ACC
             SET margin_perc    = m.perc,
                 notes          = m.notes,
                 origin_country = m.org_country,
                 desti_country  = m.dest_country
           WHERE rec_con_id = v_seq
             AND lane_no = i.lane_no
             AND WEIGHTBREAKSLAB = m.WEIGHTBREAKSLAB;
        END LOOP;
      END LOOP;
      CLOSE v_rc_c1;
    END LOOP;

  END;
  /*FUNCTION IND_ELEMENTS(P_IND VARCHAR2) RETURN VARCHAR2
  BEGIN
  END;*/
     PROCEDURE sellratesmstr_acc_proc(p_buyrateid NUMBER,p_newversionno NUMBER,p_laneno NUMBER) AS
     v_buyrateid     NUMBER;
    v_laneno        NUMBER;
    v_quote         NUMBER;
    v_count         NUMBER;
    v_temp          NUMBER := 0;
    n               NUMBER := 0;
    v_totaltemp     NUMBER := 0;
    v_totalcount    NUMBER := 0;
    v_validupto     VARCHAR2(2000);
    v_validupto1    VARCHAR2(2000);
    v_marginbasis   VARCHAR2(100);
    v_margintype    VARCHAR2(100);
    v_rec_con_id    VARCHAR2(100);
    v_weightclass   VARCHAR2(100);
    v_overallmargin VARCHAR2(100);
    v_frequency     VARCHAR2(100);
    v_seq           VARCHAR2(1000);
    v_id            VARCHAR2(2000);
    v_currency      VARCHAR2(10);
    v_accesslevel   VARCHAR2(10);
    v_terminalid    VARCHAR2(10);
    v_shmode        VARCHAR2(10);
    v_weightbreak   VARCHAR2(10);
    v_ratetype      VARCHAR2(10);
    v_transittime   VARCHAR2(10);
    v_versionno     VARCHAR2(10);
    m               NUMBER(10);
    v_QuoteId       VARCHAR2(100);
    v_QuoteId1      VARCHAR2(100);
    v_mode          VARCHAR2(10);
    v_changedesc    VARCHAR2(1000);
    v_validCount    VARCHAR2(3);
    v_curr          sys_refcursor;
    v_newversionno  VARCHAR2(10);
    v_margin_perc   NUMBER(20,6);--added by subrahmanyam
  BEGIN

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
        /*  into v_rec_con_id,
               v_accesslevel,
               v_terminalid,
               v_weightclass,
               v_currency,
               v_margintype,
               v_marginbasis,
               v_overallmargin,
               v_shmode,
               v_weightbreak,
               v_ratetype*/
          FROM QMS_REC_CON_SELLRATESMASTER
         WHERE REC_CON_ID IN (SELECT REC_CON_ID
                                FROM QMS_REC_CON_SELLRATESDTL DTL
                               WHERE BUYRATEID = p_buyrateid
                                 AND LANE_NO = p_laneno
                                 AND version_no < p_newversionno
                                 AND LINE_NO = '0' AND AI_FLAG='A'))
  LOOP
    SELECT seq_sellrates_acc.NEXTVAL INTO v_seq FROM DUAL;
   /*   Exception
        WHEN NO_DATA_FOUND Then
          v_accesslevel := '';
      End;*/
     SELECT currency INTO v_currency FROM QMS_BUYRATES_MASTER WHERE buyrateid=p_buyrateid AND version_no=p_newversionno AND (lane_no=p_laneno OR lane_no IS NULL);
      -- dbms_output.put_line('v_accesslevel'||v_accesslevel);
      --If v_accesslevel!='' -- commented by VLAKSHMI
      IF i.rec_con_id >0 -- added by VLAKSHMI
       THEN
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
           i.currency,
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
                         /*qsd.margin_perc          perc,*/--@@ commented by subrahmanyam
                         qbd.notes                notes,
                         qbd.line_no              line_no,
                         qbd.frequency            freq,
												 qbd.EXTERNAL_NOTES       external_notes
                    FROM QMS_BUYRATES_MASTER      qbm,
                         QMS_BUYRATES_DTL         qbd,
                         QMS_REC_CON_SELLRATESDTL qsd
                   WHERE qbd.buyrateid = qbm.buyrateid
                     AND qbd.version_no = qbm.version_no
                     AND (QBM.LANE_NO=QBD.lANE_NO OR QBM.LANE_NO IS NULL)
                     AND qbd.buyrateid = p_buyrateid
                     AND qbd.lane_no = p_laneno
                        -- AND qbd.version_no=v_versionno-- commented by VLAKSHMI
                     AND qbd.version_no = p_newversionno
                     AND qsd.buyrateid = qbm.buyrateid
                     AND qsd.buyrateid = p_buyrateid
                     AND qsd.ai_flag='A'
                     AND qsd.lane_no = p_laneno
--@@ Commented by subrahmanyam for security charge issue:
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
 SELECT rsd.MARGIN_PERC INTO v_margin_perc FROM QMS_REC_CON_SELLRATESDTL rsd WHERE rsd.rec_con_id=i.rec_con_id AND rsd.weightbreakslab=j.wbslab AND rsd.buyrateid=p_buyrateid AND rsd.lane_no=p_laneno AND rsd.version_no=(SELECT MAX(version_no)  FROM QMS_REC_CON_SELLRATESDTL   WHERE buyrateid = p_buyrateid AND lane_no = p_laneno) AND rate_description=j.rate_description;
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
             notes,
						 EXTERNAL_NOTES)
          VALUES
            (v_seq,
             j.carrier,
             j.srvlevel,
             j.chargerate,
             j.ubound,
             j.lbound,
             /*j.perc,*/--commented by subrahmanyam for security charge missing issue:
             v_margin_perc,--@dded by subrahmanyam
             j.wbslab,
             j.freq,
             j.ch_indicator,
             p_laneno,
             m,
             p_buyrateid,
             j.transittime,
             j.origin,
             j.dest,
             DECODE(j.consoletype, 'P', NULL, j.consoletype),
             v_id,
             i.rec_con_id,
             --p_laneno,        --@@Modified by Kameswari for the WPBN issue-122621
             p_laneno,
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
             j.notes,
						 j.external_notes);


          m := m + 1;
        END LOOP;
       UPDATE QMS_BUYRATES_DTL DTL
             SET ACC_FLAG = 'Y'
           WHERE BUYRATEID = p_buyrateid
             AND LANE_NO = p_laneno
             AND VERSION_NO = p_newversionno
             AND DTL.ACTIVEINACTIVE IS NULL;


      UPDATE QMS_REC_CON_SELLRATESDTL DTL
         SET ACCEPTANCE_FLAG = 'Y'
       WHERE BUYRATEID = p_buyrateid
         AND LANE_NO = p_laneno
         AND version_no < p_newversionno
         AND DTL.AI_FLAG = 'A';

      UPDATE QMS_REC_CON_SELLRATESDTL DTL
         SET DTL.AI_FLAG = 'I'
       WHERE DTL.BUYRATEID = p_buyrateid
         AND DTL.LANE_NO = p_laneno
         AND version_no < p_newversionno
         AND DTL.AI_FLAG = 'A'
         AND EXISTS (SELECT 'X'
                FROM QMS_REC_CON_SELLRATESMASTER MAS
               WHERE MAS.RC_FLAG = 'C'
                 AND MAS.REC_CON_ID = DTL.REC_CON_ID
                 AND DTL.BUYRATEID = p_buyrateid);
      DELETE FROM QMS_REC_CON_SELLRATESDTL_ACC
       WHERE BUYRATEID = p_buyrateid
         AND LANE_NO = p_laneno
         AND version_no < p_newversionno;
          END IF;

         END LOOP;
              UPDATE QMS_BUYRATES_DTL
         SET activeinactive = 'I'
       WHERE buyrateid = p_buyrateid
         AND lane_no = p_laneno
         AND version_no < p_newversionno;


     --  Added By Kishore To update the   Surcharge_id and Surcharge_currency to for updated Buy Rate
      /*for j in (SELECT bd.buyrateid, bd.sur_charge_currency, bd.surcharge_id, bd.line_no
                FROM QMS_BUYRATES_DTL BD
                 WHERE bd.buyrateid = p_buyrateid
                    and bd.version_no = (p_newversionno-1)
                     and bd.rate_description <> 'A FREIGHT RATE')
     loop
         update QMS_BUYRATES_DTL Bd
         set bd.sur_charge_currency = j.sur_charge_currency, bd.surcharge_id = j.surcharge_id
         WHERE bd.buyrateid = p_buyrateid
             and bd.version_no = p_newversionno
             and bd.line_no= j.line_no
              and bd.rate_description <> 'A FREIGHT RATE';


     end loop;*/
     --  End Of Kishore To update the   Surcharge_id and Surcharge_currency to for updated Buy Rate
END;


---------------------------------------------------------------------------


PROCEDURE buy_rates_Expiry_proc(
                               p_org_locs              VARCHAR2,
                               p_dest_locs             VARCHAR2,
                               p_terminal              VARCHAR2,
                               p_rate_type             VARCHAR2,
                               p_weight_break          VARCHAR2,
                               p_srvlevl               VARCHAR2,
                               p_carrier               VARCHAR2,
                               p_shmode                VARCHAR2,
                               p_operation             VARCHAR2,
                               p_page_no               NUMBER DEFAULT 1,
                               p_page_rows             NUMBER DEFAULT 50,
                               p_org_countries         VARCHAR2,
                               p_dest_countries        VARCHAR2,
                               p_org_regions           VARCHAR2,
                               p_dest_regions          VARCHAR2,
                               p_from_date               DATE,
                               p_TO_DATE                 DATE,
                               p_tot_rec               OUT NUMBER,
                               P_tot_pages             OUT NUMBER,
                               p_rs                    OUT resultset) AS
    v_org_locs     VARCHAR2(32767) := '';
    v_dest_locs    VARCHAR2(32767) := '';
    v_terminal     VARCHAR2(300);
    v_rate_type    VARCHAR2(300);
    v_weight_break VARCHAR2(300);
    --v_ratedesc     VARCHAR2(300); --@@Added by Kameswari for the CR
    v_ratedesc     VARCHAR2(32767);
    v_srvlevl      VARCHAR2(32767) := '';
    v_srvlevl_id   VARCHAR2(32767) := ''; --@@Added by Kameswari issuid:127694
    v_carrier      VARCHAR2(32767) := '';
    v_currency     VARCHAR2(300);
    v_shmode       VARCHAR2(300);
    v_operation    VARCHAR2(300);
    v_type         VARCHAR2(1);
    v_terminals    VARCHAR2(32767);
    v_chargerate   VARCHAR2(400) := '';
    v_sqlIn        VARCHAR2(4000);
    v_sqlnew       VARCHAR2(32767);
    v_sql1         VARCHAR2(32767);
    v_sql_sur      VARCHAR2(32767);
    v_sql2         VARCHAR2(32767);
    v_sql3         VARCHAR2(32767);
    v_sql4         VARCHAR2(32767);
    v_sql5         VARCHAR2(32767);
    v_sql6         VARCHAR2(32767);
    v_sql7         VARCHAR2(32767); --@@Added by Kameswari issuid:127694
    v_base         VARCHAR2(30);
    v_break        VARCHAR2(4000);
    v_temp         VARCHAR2(200);
    v_countryid    VARCHAR2(30);
    v_countryid1   VARCHAR2(30);
    v_temp1        VARCHAR2(10);
    v_temp2        VARCHAR2(10);
    v_temp3        VARCHAR2(10);
    v_temp4        VARCHAR2(300); --@@Added by Kameswari for the CR
    v_ubound       VARCHAR2(1000);
    v_lbound       VARCHAR2(1000);
    v_indicator    VARCHAR2(1000);
    v_qry          VARCHAR2(10);
    v_rc_c1        resultset;
    v_rc_c2        resultset;
    v_break_slab   VARCHAR2(20);
    v_lineno       VARCHAR2(20); --@@Added by Kameswari for Surcharge Enhancement
    v_opr_adm_flag VARCHAR2(2);
    v_exit         NUMBER;
    v_rate         VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancement
    v_desc         VARCHAR2(1000);
    v_org_reg      VARCHAR2(1000):= 'T';
    v_dest_reg     VARCHAR2(1000):= 'T';
    v_chargerate_TEMP VARCHAR2(100);
    v_break_TEMP VARCHAR2(100);
    v_temp4_TEMP VARCHAR2(100);
    v_sch_currency  VARCHAR2(100);  --kishore
    v_temp_currency   VARCHAR2(200); --kishore
    v_surchrge           VARCHAR2(100); --Gowtham
    v_currency_TEMP     VARCHAR2(100);--Gowtham

    CURSOR c1 IS(
      SELECT parent_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY child_terminal_id = PRIOR parent_terminal_id
       START WITH child_terminal_id = p_terminal
      UNION
      SELECT child_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
       START WITH parent_terminal_id = p_terminal
      UNION
      SELECT terminalid term_id
        FROM FS_FR_TERMINALMASTER
       WHERE oper_admin_flag = 'H'
      UNION
      SELECT p_terminal term_id FROM DUAL);

    CURSOR c2 IS
      SELECT p_terminal term_id FROM DUAL;

    CURSOR C4 IS
      SELECT child_terminal_id term_id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
       START WITH parent_terminal_id = p_terminal
      UNION
      SELECT p_terminal term_id FROM DUAL;

    CURSOR C3 IS
      SELECT DISTINCT terminalid term_id
        FROM FS_FR_TERMINALMASTER
       WHERE actv_flag = 'A'
         AND (INVALIDATE = 'F' OR INVALIDATE IS NULL);

  BEGIN
    v_terminal     := '''' || UPPER(p_terminal) || '''';
    v_rate_type    := '''' || UPPER(p_rate_type) || '''';
    v_weight_break := '''' || UPPER(p_weight_break) || '''';
    v_shmode       := '''' || p_shmode || '''';

    SELECT oper_admin_flag
      INTO v_opr_adm_flag
      FROM FS_FR_TERMINALMASTER
     WHERE terminalid = p_terminal;

    IF UPPER(v_opr_adm_flag) <> 'H' THEN
      IF UPPER(TRIM(p_operation)) = 'INVALIDE' THEN
        Dbms_Session.set_context('BUYRATES_CONTEXT',
                                 'v_terminal_id',
                                 p_terminal);

        v_terminals := 'SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN
                            CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                            START WITH parent_terminal_id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                            UNION SELECT sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'') term_id FROM DUAL';

      END IF;

      IF UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN
        Dbms_Session.set_context('BUYRATES_CONTEXT',
                                 'v_terminal_id',
                                 p_terminal);
        v_terminals := 'Select Parent_Terminal_Id Term_Id
                            FROM FS_FR_TERMINAL_REGN
                          CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
                           START WITH Child_Terminal_Id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                          UNION
                          SELECT Child_Terminal_Id Term_Id
                            FROM FS_FR_TERMINAL_REGN
                          CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                           START WITH Parent_Terminal_Id = sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'')
                          UNION
                          SELECT Terminalid Term_Id
                            FROM FS_FR_TERMINALMASTER
                           WHERE Oper_Admin_Flag = ''H''
                          UNION
                          SELECT sys_context(''BUYRATES_CONTEXT'',''v_terminal_id'') Term_Id FROM Dual';

      END IF;
    ELSE
      v_terminals := 'Select Terminalid
                       FROM FS_FR_TERMINALMASTER
                       WHERE Actv_Flag = ''A''
                         AND (Invalidate = ''F'' OR Invalidate IS NULL)';
      /*FOR i IN c3
      LOOP
         v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
      END LOOP;*/
    END IF;

    IF  UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN
      v_qry := '=' || '''' || 'F' || '''';
    ELSE
      v_qry := 'like ' || '''' || '%' || '''';
    END IF;

    EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data_1');

    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_1');

    --v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
    --DBMS_OUTPUT.put_line ('Comman Proc hi ---->' || v_qry);

    IF p_org_locs IS NOT NULL THEN
     /*v_org_locs := ' AND qbd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';*/
  v_org_reg :=' AND qbd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';
    END IF;
    PRINT_OUT('LENGTH'||LENGTH ((v_org_reg )));
    IF (  p_org_countries IS NOT NULL  AND LENGTH ((v_org_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_org_countries) || '' || ') )';

    END IF;

  IF (  p_org_regions IS NOT NULL  AND LENGTH (TRIM (v_org_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_org_regions) || '' || ') )';

    END IF;
    IF p_dest_locs IS NOT NULL THEN
     /* v_dest_locs := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';*/
     v_dest_reg := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';
    END IF;
    IF (  p_dest_countries IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_dest_reg := ' AND qbd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_dest_reg :=  ' AND qbd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_dest_reg := v_dest_reg || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_dest_countries) || '' || ') )';

    END IF;

 IF (  p_dest_regions IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_shmode='1') THEN

    v_dest_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_dest_reg :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_dest_reg := v_dest_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_dest_regions) || '' || ') )';

    END IF;
    IF p_srvlevl IS NOT NULL THEN
      v_srvlevl := '  AND qbd.service_level IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_srvlevl) || '' ||
                   ',''SCH'')';
    END IF;
    ----@@Added by Kameswari issuid:127694
    IF p_srvlevl IS NOT NULL THEN
      v_srvlevl_id := '  AND qbd.service_level IN (' || '' ||
                      Qms_Rsr_Rates_Pkg.seperator(p_srvlevl) || '' || ')';
    END IF;
    --@Added End
    IF p_carrier IS NOT NULL THEN
      v_carrier := '   AND qbd.carrier_id IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_carrier) || '' || ' ) ';
    END IF;

    IF UPPER(p_weight_break) = 'FLAT' THEN
      v_break := 'Min,Flat,';
    END IF;

    v_sqlIn := ' AND (qbd.buyrateid, qbd.lane_no,qbd.version_No ) in (SELECT  buyrateid, lane_no ,version_No FROM (SELECT t1.*, ROWNUM rn FROM (SELECT DISTINCT qbd.buyrateid, qbd.lane_no ,qbd.version_No';

    v_sql1 := 'insert into temp_data_1(BUYRATEID, VERSION_NO,WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO,LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION, SUR_CHARGE_CURRENCY) SELECT  qbd.buyrateid,qbd.version_no,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,DECODE(QBD.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',QBD.RATE_DESCRIPTION)RATE_DESCRIPTION, DECODE(QBD.SUR_CHARGE_CURRENCY,'''',''-'',QBD.SUR_CHARGE_CURRENCY) '; --@@Modified by Kameswari for the CR
    v_sql6 := '  FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid and qbd.version_no=qbm.version_no and (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) ';
             -- v_org_locs; commented and modified by phani sekhar for wpbn 171213
     IF v_org_reg <>'T' THEN
     v_sql6 := v_sql6 ||v_org_reg;
     END IF;

     IF v_dest_reg <>'T' THEN
     v_sql4 := v_sql4 || v_dest_reg;
     v_sql7 := v_sql7  || v_dest_reg;
     END IF;

    v_sql4 := v_sql4 || v_srvlevl ||--replace v_dest_locs with v_dest_reg by phani for 171213
              '   AND qbd.activeinactive IS NULL AND (qbd.INVALIDATE IS NULL OR qbd.INVALIDATE  ' ||
              v_qry || ') ';

    v_sql7 := v_sql7 || v_srvlevl_id ||--replace v_dest_locs with v_dest_reg by phani for wpbn 171213
              '   AND qbd.activeinactive IS NULL AND (qbd.INVALIDATE IS NULL OR qbd.INVALIDATE  ' ||
              v_qry || ') ';

    v_sql2 := '   AND qbm.shipment_mode =' || v_shmode ||
              '  AND qbm.weight_break =' || v_weight_break;
     v_sql3 := '  AND qbm.rate_type =' || v_rate_type || v_carrier ||
               '  AND qbm.terminalid IN (' || v_terminals || ') '||
               '  AND TRUNC(QBD.VALID_UPTO) BETWEEN  TRUNC(TO_DATE('||
               ''''||
               P_from_date
               || ''''
               || ',''DD-MM-YY'' )) AND TRUNC(TO_DATE('||
               ''''||
               p_TO_DATE
               ||''''
               ||',''DD-MM-YY'' )) ';

    IF UPPER(p_operation) = 'DOWNLOAD' THEN
      v_sql5 := 'insert into base_data_1(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,TERMINALID,VALIDUPTO,INVALIDATE,NOTES,CURRENCY,EFROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES, SUR_CHARGE_CURRENCY) select   qbd.buyrateid buyrateid, qbd.version_no ,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate,
            qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.TERMINALID,qbd.VALID_UPTO,qbd.INVALIDATE,qbd.NOTES,qbm.CURRENCY,qbd.EFFECTIVE_FROM,qbd.DENSITY_CODE,qbd.RATE_DESCRIPTION,qbd.EXTERNAL_NOTES, qbd.SUR_CHARGE_CURRENCY FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.line_no=''0'' AND qbd.buyrateid = qbm.buyrateid   AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND qbd.version_no=qbm.version_no AND (qbd.rate_description=''A FREIGHT RATE''OR qbd.rate_description IS NULL) ' ;
      IF v_org_reg <>'T' THEN
      v_sql5 := v_sql5 ||v_org_reg;
      END IF;
    ELSE
      v_sql5 := 'insert into base_data_1(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,TERMINALID,VALIDUPTO,INVALIDATE,NOTES,CURRENCY,EFROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY) select buyrateid, version_no,carrier_id, origin , destination ,service_level,weight_break_slab,transit_time, frequency, chargerate,
            lane_no,org_countryid,Dest_countryid,TERMINALID,VALID_UPTO,INVALIDATE,NOTES,CURRENCY,EFFECTIVE_FROM,DENSITY_CODE,RATE_DESCRIPTION,EXTERNAL_NOTES,SUR_CHARGE_CURRENCY FROM (SELECT t1.*, ROWNUM rn FROM ( ';
    END IF;

    v_sqlnew := ' select  distinct qbd.buyrateid buyrateid, qbd.version_no version_no,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate,
            qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.TERMINALID,qbd.VALID_UPTO,qbd.INVALIDATE,qbd.NOTES,qbm.CURRENCY,qbd.EFFECTIVE_FROM,qbd.DENSITY_CODE,qbd.RATE_DESCRIPTION RATE_DESCRIPTION,qbd.EXTERNAL_NOTES EXTERNAL_NOTES, qbd.SUR_CHARGE_CURRENCY FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  AND qbd.version_no=qbm.version_no AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND (qbd.rate_description=''A FREIGHT RATE''OR qbd.rate_description IS NULL) ' ;

           IF v_org_reg <>'T' THEN
      v_sqlnew := v_sqlnew ||v_org_reg;
      END IF;

    IF UPPER(p_operation) = 'DOWNLOAD' THEN
      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);
      PRINT_OUT(v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);
    ELSE


      ----@@Modified by Kameswari issuid:127694
      print_out(v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                        v_sqlIn || v_sql6 || v_sql7 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid,lane_no) t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ');
      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3 ||
                        v_sqlIn || v_sql6 || v_sql7 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid,lane_no) t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ')
        USING p_page_no, p_page_rows, p_page_rows, p_page_no, p_page_rows;
      -- end
    END IF;

     --DBMS_OUTPUT.put_line ('hi');
    IF UPPER(p_operation) = 'DOWNLOAD' THEN
     print_out(v_sql5 || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no,weight_break_slab');
      EXECUTE IMMEDIATE (v_sql5 || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no,weight_break_slab');
    ELSE
    print_out('---------------');
    print_out(v_sql5 || v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no' ||
                        ') t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)');

      EXECUTE IMMEDIATE (v_sql5 || v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ORDER BY buyrateid, lane_no' ||
                        ') t1
                           WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                           WHERE rn > ((:v_page_no - 1) * :v_page_rows)')
        USING p_page_no, p_page_rows, p_page_rows, p_page_no, p_page_rows;
    END IF;

    COMMIT;
    --DBMS_OUTPUT.put_line ('hi');
    -- DBMS_OUTPUT.put_line ('hi');

    FOR j IN (SELECT buyrateid,
                  version_no,
                     weight_break_slab,
                     chargerate,
                     lane_no,
                     origin,
                     destination
                FROM BASE_DATA_1
               ORDER BY buyrateid) LOOP
      v_chargerate := '';
      v_ratedesc   := '';
      --DBMS_OUTPUT.put_line ('hi ');
      v_break := '';
v_chargerate_TEMP :='';
v_break_TEMP :='';
v_temp4_TEMP :='';
      v_currency_TEMP       := '';-- Gowtham
      v_sch_currency    := '';
        IF (UPPER(p_weight_break) = 'LIST' ) = FALSE THEN

       EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=0' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;



        v_lbound    := v_temp1 || ',';
        v_ubound    := v_temp2 || ',';
        v_indicator := v_temp3 || ',';
         v_chargerate_TEMP := v_chargerate || ',';
         v_break_TEMP := v_break || ',';
         v_temp4_TEMP := v_temp4 || ',';
        v_currency_TEMP    := v_temp_currency || ',';

                EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''), nvl(SUR_CHARGE_CURRENCY,''-'') from temp_data_1 where  buyrateid =:v_buy_rate_id
                                AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=1' --@@Modified by Kameswari
                          )
          INTO v_break, v_temp4, v_chargerate, v_temp1, v_temp2, v_temp3, v_temp_currency
          USING j.Buyrateid,j.version_no,j.Lane_No;



        v_lbound    := v_lbound || v_temp1||',';
        v_ubound    := v_ubound || v_temp2||',';
        v_indicator := v_indicator ||v_temp3||',';
        v_chargerate :=  v_chargerate_TEMP|| v_chargerate;
         v_break := v_break_TEMP||v_break ;
         v_temp4 := v_temp4_TEMP||v_temp4 ;
        v_temp_currency := v_currency_TEMP || v_temp_currency; -- Gowtham

      ELSE
        v_temp1     := '';
        v_temp2     := '';
        v_temp3     := '';
        v_temp4     := '';
        v_lbound    := '';
        v_ubound    := '';
        v_indicator := '';
        v_temp_currency := ''; -- Gowtham
      END IF;

      IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_base       := v_chargerate;
        v_chargerate := '';
        v_rate       := v_temp4;
        v_temp4      := '';
        v_surchrge       := v_temp_currency;
        v_temp_currency := '';
      ELSIF UPPER(p_weight_break) = 'FLAT' OR
            UPPER(p_weight_break) = 'SLAB' THEN
        v_chargerate := v_chargerate || ',';
        v_ratedesc   := v_temp4 || ',';
        v_sch_currency := v_temp_currency || ',';
      END IF;



      IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE  RATE_DESCRIPTION=''A FREIGHT RATE''  ORDER BY  break_slab';
      ELSIF UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1 THEN
        v_sql1 := 'SELECT    DISTINCT weight_break_slab break_slab   FROM temp_data_1 WHERE  RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY  break_slab  ';
      ELSIF UPPER(p_weight_break) = 'FLAT' THEN
        v_sql1 := 'SELECT    DISTINCT  weight_break_slab break_slab   FROM temp_data_1  WHERE line_no > 1   AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY    break_slab';
      ELSE
        v_sql1 :=   'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 1 AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY  break_slab ';
      END IF;

          IF UPPER(p_weight_break) = 'LIST' AND p_shmode = 1 THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 0 AND RATE_DESCRIPTION<>''A FREIGHT RATE''  and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      ELSIF UPPER(p_weight_break) = 'LIST' AND p_shmode <> 1 THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE  RATE_DESCRIPTION<>''A FREIGHT RATE'' and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      ELSIF UPPER(p_weight_break) = 'FLAT' THEN
        v_sql_sur := 'SELECT   weight_break_slab break_slab,rate_description   FROM temp_data_1  WHERE line_no > 1  AND RATE_DESCRIPTION<>''A FREIGHT RATE''  and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';

      ELSE
        v_sql_sur :=   'SELECT   weight_break_slab break_slab,rate_description  FROM temp_data_1  WHERE line_no > 1 AND RATE_DESCRIPTION<>''A FREIGHT RATE'' and buyrateid='||j.buyrateid ||'and lane_no='||j.lane_no||' ORDER BY line_no ';
      END IF;

--Ended for 219973
      --DBMS_OUTPUT.put_line (v_sql1);

      OPEN v_rc_c1 FOR v_sql1;

      LOOP
        --DBMS_OUTPUT.put_line (' Here..');

        FETCH v_rc_c1
          INTO v_break_slab;

        EXIT WHEN v_rc_c1%NOTFOUND;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp := '-';
        END;
        --@@Added by Kameswari for the CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp4
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp4 := '-';
          --DBMS_OUTPUT.put_line (v_temp4);
        END;

        --@@CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_temp1
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp1 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_temp2
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp2 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION =''A FREIGHT RATE''')
            INTO v_temp3
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;

          IF v_temp3 IS NULL THEN
            v_temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp3 := '-';
        END;
        --kishore
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(SUR_CHARGE_CURRENCY,'''',''-'',SUR_CHARGE_CURRENCY)SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_temp_currency
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp_currency := '-';
        END;
        --kishore

        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE WEIGHT_BREAK_SLAB=:V_BREAK_SLAB AND
        BUYRATEID=:V_BUYRATEID AND VERSION_NO=:V_VERSION_NO AND LANE_NO=:V_LANE_NO AND RATE_DESCRIPTION=''A FREIGHT RATE''')
        USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No;
        v_chargerate := v_chargerate || v_temp || ',';

        v_lbound := v_lbound || v_temp1 || ',';
        v_ubound := v_ubound || v_temp2 || ',';
        v_indicator := v_indicator || v_temp3 || ',';
         v_ratedesc := v_ratedesc || v_temp4 || ',';
         v_sch_currency := v_sch_currency || v_temp_currency || ','; --kishore

      END LOOP;

      IF UPPER(p_weight_break) = 'LIST' AND P_SHMODE = 1 THEN
        --v_chargerate := v_chargerate || v_base || ',';
        IF v_base <>'' THEN
        v_chargerate := v_base || ',' || v_chargerate;
        ELSE
        v_chargerate := v_base ||  v_chargerate;
        END IF;
        --v_ratedesc := v_ratedesc || v_rate|| ',';
        IF v_rate <>'' THEN
        v_ratedesc := v_rate || ',' || v_ratedesc;
        ELSE
              v_ratedesc := v_rate ||  v_ratedesc;
        END IF;
        -- Added by Gowtham
        IF v_surchrge <> '' THEN
           v_sch_currency := v_surchrge ||','|| v_sch_currency;
        ELSE
            v_sch_currency := v_surchrge||v_sch_currency;
        END IF;
        -- End Gowtham
      END IF;

      v_base  := v_break;
      v_break := '';
      --DBMS_OUTPUT.put_line (v_base || '  ' || v_chargerate);

      CLOSE v_rc_c1;

      OPEN v_rc_c1 FOR v_sql1;

      LOOP
        FETCH v_rc_c1
          INTO v_break_slab;

        EXIT WHEN v_rc_c1%NOTFOUND;
        v_break := v_break || ',' || v_break_slab;
      END LOOP;

      CLOSE v_rc_c1;
--@@Added by Subrahmanyam for 219973   for Handling surcharge Cursor separately

 OPEN v_rc_c2 FOR v_sql_sur;

      LOOP
        --DBMS_OUTPUT.put_line (' Here..');

        FETCH v_rc_c2
          INTO v_break_slab, v_desc;

        EXIT WHEN v_rc_c2%NOTFOUND;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N'' AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp := '-';
        END;
        --@@Added by Kameswari for the CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(SUR_CHARGE_CURRENCY,'''',''-'',SUR_CHARGE_CURRENCY)SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp_currency
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_desc <> 'A FREIGHT RATE' THEN
          v_temp_currency := '**';
          ELSE
            v_temp_currency := '-';
            END IF;

        END;


         BEGIN
          EXECUTE IMMEDIATE (' SELECT  DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp4
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_desc <> 'A FREIGHT RATE' THEN
          v_temp4 := '**';
          ELSE
            v_temp4 := '-';
            END IF;

          --DBMS_OUTPUT.put_line (v_temp4);
        END;



        --@@CR
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp1
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp1 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_desc')
            INTO v_temp2
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp2 := '-';
        END;

        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                         AND buyrateid =:v_buy_rate_id AND version_no=:v_versionno AND LANE_NO=:v_lane_no AND get_flag=''N''  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION =:v_desc')
            INTO v_temp3
            USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;

          IF v_temp3 IS NULL THEN
            v_temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_temp3 := '-';
        END;
        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE WEIGHT_BREAK_SLAB=:V_BREAK_SLAB AND
        BUYRATEID=:V_BUYRATEID AND VERSION_NO=:V_VERSION_NO AND LANE_NO=:V_LANE_NO  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND RATE_DESCRIPTION=:V_RATE_DESC')
        USING v_break_slab, j.Buyrateid,j.version_no, j.Lane_No,v_desc;
        v_chargerate := v_chargerate || v_temp || ',';

        v_lbound := v_lbound || v_temp1 || ',';
        v_ubound := v_ubound || v_temp2 || ',';
        v_indicator := v_indicator || v_temp3 || ',';
         v_ratedesc := v_ratedesc || v_temp4 || ',';
         v_sch_currency := v_sch_currency || v_temp_currency || ','; --kishore

      END LOOP;



      CLOSE v_rc_c2;

      OPEN v_rc_c2 FOR v_sql_sur;

      LOOP
        FETCH v_rc_c2
          INTO v_break_slab, v_desc;

        EXIT WHEN v_rc_c2%NOTFOUND;
        v_break := v_break || ',' || v_break_slab;
      END LOOP;

      CLOSE v_rc_c2;


      IF (UPPER(p_weight_break) = 'SLAB' OR UPPER(p_weight_break) = 'FLAT') THEN
        v_break := v_base || v_break;
      ELSIF UPPER(p_weight_break) = 'LIST' AND P_SHMODE = 1 THEN
        v_break := v_base || v_break;
      ELSE
        v_break := SUBSTR(v_break, 2, LENGTH(v_break));
      END IF;

      COMMIT;--@@Added by Kameswari fo the WPBN issue-160743
      UPDATE BASE_DATA_1
         SET weight_break_slab = v_break,
             chargerate        = SUBSTR(v_chargerate,
                                        1,
                                        LENGTH(v_chargerate) - 1),
             ubound            = SUBSTR(v_ubound, 1, LENGTH(v_ubound) - 1),
             lbound            = SUBSTR(v_lbound, 1, LENGTH(v_lbound) - 1),
             c_indicator       = SUBSTR(v_indicator,
                                        1,
                                        LENGTH(v_indicator) - 1),
             rate_description  = SUBSTR(v_ratedesc,
                                        1,
                                        LENGTH(v_ratedesc) - 1),
--- Gowtham Start
             sur_charge_currency =SUBSTR(v_sch_currency,
                                          1,
                                           LENGTH(v_sch_currency) - 1)
--- End  Gowtham
       WHERE buyrateid = j.buyrateid
         AND lane_no = j.lane_no;

      IF p_shmode <> 2 THEN
        BEGIN
          SELECT countryid
            INTO v_countryid
            FROM FS_FR_LOCATIONMASTER
           WHERE locationid = j.origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid := '';
        END;
      ELSE
        BEGIN
          SELECT countryid
            INTO v_countryid
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = j.origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid := '';
        END;
      END IF;

      IF p_shmode <> 2 THEN
        BEGIN
          SELECT countryid
            INTO v_countryid1
            FROM FS_FR_LOCATIONMASTER
           WHERE LOCATIONID = j.destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid1 := '';
        END;
      ELSE
        BEGIN
          SELECT countryid
            INTO v_countryid1
            FROM FS_FRS_PORTMASTER
           WHERE PORTID = j.destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_countryid1 := '';
        END;
      END IF;

      UPDATE BASE_DATA_1
         SET org_countryid = v_countryid, dest_countryid = v_countryid1
       WHERE buyrateid = j.buyrateid
         AND lane_no = j.lane_no
         AND version_no =j.version_no;
    END LOOP;
 COMMIT;--@@Added by Kameswari fo the WPBN issue-160743


    IF UPPER(TRIM(p_operation)) = 'DOWNLOAD' THEN

      OPEN p_rs FOR
        SELECT * FROM BASE_DATA_1;

    ELSE


      OPEN p_rs FOR
        SELECT * FROM BASE_DATA_1;



      EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM ( SELECT t1.*, ROWNUM rn FROM ( ' ||
                        v_sqlnew || v_sql4 || v_sql2 || v_sql3 ||
                        ' ) t1) ')
        INTO p_tot_rec;


      p_tot_pages := CEIL(p_tot_rec / p_page_rows);

    END IF;
 COMMIT;
  END;
END;

/

/

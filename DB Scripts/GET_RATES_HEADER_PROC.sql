--------------------------------------------------------
--  DDL for Procedure GET_RATES_HEADER_PROC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "GET_RATES_HEADER_PROC" (p_org_locs      VARCHAR2,
                                                  p_dest_locs     VARCHAR2,
                                                  p_rate_type     VARCHAR2,
                                                  p_weight_break  VARCHAR2,
                                                  p_srvlevl       VARCHAR2,
                                                  p_shmode        VARCHAR2,
                                                  p_customerid    VARCHAR2,
                                                  P_FROM_DATE     IN DATE,
                                                  P_TO_DATE       IN DATE,
                                                  p_terminal      IN VARCHAR2,
                                                  p_origincountry IN VARCHAR2,
                                                  p_destcountry   IN VARCHAR2,
                                                  p_rs            OUT SYS_REFCURSOR) AS

  v_sqlIn        VARCHAR2(4000);
  v_sqlwh        VARCHAR2(1000);
  v_wghtBreak    VARCHAR2(1000);
  v_staticQry1   varchar2(1000);
  v_staticQry2   varchar2(1000);
  v_opr_adm_flag VARCHAR2(2);
  v_terminals    VARCHAR2(32767);
BEGIN
  v_sqlIn := 'SELECT DISTINCT QBD.WEIGHT_BREAK_SLAB FROM  QMS_BUYRATES_DTL QBD ,QMS_QUOTE_RATES  QR, QMS_QUOTE_MASTER    QM, QMS_BUYRATES_MASTER QBM  WHERE QM.ACTIVE_FLAG =''A'' ';

  v_sqlIn := v_sqlIn ||
             ' AND QM.ID = QR.QUOTE_ID  AND QR.BUYRATE_ID = QBM.BUYRATEID ';
  v_sqlin := v_sqlIn || ' AND TO_DATE(QM.CREATED_TSTMP)>= TO_DATE(' || '''' ||
             P_FROM_DATE || '''' ||
             ') AND TO_DATE(QM.CREATED_TSTMP)<= TO_DATE(' || '''' ||
             P_TO_DATE || '''' || ')';

  v_sqlwh := ' AND (QR.RATE_LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL) AND QR.VERSION_NO = QBM.VERSION_NO AND QR.SELL_BUY_FLAG IN (''RSR'', ''BR'', ''CSR'')';

  v_sqlwh      := v_sqlwh ||
                  ' AND QBM.BUYRATEID = QBD.BUYRATEID AND (QBM.LANE_NO = QBD.LANE_NO OR QBM.LANE_NO IS NULL) AND QBM.VERSION_NO = QBD.VERSION_NO ';
  v_staticQry1 := 'insert into RATES_HEADER select ''MIN'' from dual';
  v_staticQry2 := 'insert into RATES_HEADER select ''FLAT'' from dual';

  EXECUTE IMMEDIATE ('TRUNCATE TABLE RATES_HEADER');

  SELECT oper_admin_flag
    INTO v_opr_adm_flag
    FROM FS_FR_TERMINALMASTER
   WHERE terminalid = p_terminal;

  IF UPPER(v_opr_adm_flag) <> 'H' THEN
    v_terminals := ' SELECT ' || '''' || p_terminal || '''' ||
                   'FROM DUAL UNION' ||
                   ' SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN ' ||
                   ' CONNECT BY PRIOR child_terminal_id = parent_terminal_id ' ||
                   ' START WITH parent_terminal_id =' || '''' || p_terminal || '''';
  ELSE
    v_terminals := ' SELECT terminalid FROM FS_FR_TERMINALMASTER ';
  END IF;

  v_sqlIn := v_sqlIn || '  AND QM.Terminal_Id IN (' || v_terminals || ') ';

  IF p_org_locs IS NOT NULL THEN

    IF p_shmode = 1 THEN
      v_sqlIn := v_sqlIn || ' AND QM.ORIGIN_LOCATION = ' || '''' ||
                 p_org_locs || '''';
    ELSE
      v_sqlIn := v_sqlIn || ' AND QM.ORIGIN_PORT = ' || '''' || p_org_locs || '''';
    END IF;

  END IF;

  IF p_org_locs IS NULL AND p_origincountry IS NOT NULL THEN
    IF p_shmode = 1 THEN
      v_sqlIn := v_sqlIn ||
                 ' AND QM.ORIGIN_LOCATION IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
                 p_origincountry || '''' || ') ';
    ELSE
      v_sqlIn := v_sqlIn ||
                 ' AND QM.ORIGIN_PORT IN( SELECT portid FROM fs_frs_portmaster  WHERE countryid = ' || '''' ||
                 p_origincountry || '''' || ') ';
    END IF;
  END IF;
  IF p_dest_locs IS NULL AND p_destcountry IS NOT NULL THEN
    IF p_shmode = 1 THEN
      v_sqlIn := v_sqlIn ||
                 ' AND QM.DEST_LOCATION IN( select locationid from fs_fr_locationmaster where countryid = ' || '''' ||
                 p_destcountry || '''' || ') ';
    ELSE
      v_sqlIn := v_sqlIn ||
                 ' AND QM.DESTIONATION_PORT IN( SELECT portid FROM fs_frs_portmaster  WHERE countryid  =' || '''' ||
                 p_origincountry || '''' || ') ';

    END IF;
  END IF;

  IF p_dest_locs IS NOT NULL THEN
    IF p_shmode = 1 THEN
      v_sqlIn := v_sqlIn || '  AND QM.Dest_Location = ' || '''' ||
                 p_dest_locs || '''';
    ELSE
      v_sqlIn := v_sqlIn || ' AND QM.DESTIONATION_PORT = ' || '''' ||
                 p_dest_locs || '''';

    END IF;
  END IF;

  IF p_srvlevl IS NOT NULL THEN
    v_sqlIn := v_sqlIn || '  AND qbd.service_level IN( ''SCH'', ' || '''' ||
               p_srvlevl || '''' || ')';

  END IF;

  IF p_customerid IS NOT NULL THEN
    v_sqlIn := v_sqlIn || '   AND qm.customer_id like ' || '''' ||
               p_customerid || '%' || '''';
  END IF;

  IF p_shmode IS NOT NULL THEN
    v_sqlIn := v_sqlIn || '   AND qm.shipment_mode = ' || '''' || p_shmode || '''';
  END IF;

  IF (p_weight_break) <> 'LIST' THEN
    EXECUTE IMMEDIATE (v_staticQry1);
  END IF;

  IF (p_weight_break) = 'FLAT' THEN
    EXECUTE IMMEDIATE (v_staticQry2);
  END IF;

  IF (p_weight_break) = 'SLAB' THEN
    v_sqlwh := v_sqlwh || ' AND QBM.WEIGHT_BREAK =' || '''' ||
               p_weight_break || '''';

    v_wghtBreak := '  AND QBD.RATE_DESCRIPTION  IN (''A FREIGHT RATE'') AND QBM.RATE_TYPE =' || '''' ||
                   p_rate_type || '''' ||
                 --  ' AND QBD.WEIGHT_BREAK_SLAB<>''MIN'' ORDER BY TO_NUMBER (qbd.weight_break_slab)';Commented by govind for the issue 263250
                   ' AND (UPPER(QBD.WEIGHT_BREAK_SLAB) NOT IN (''MIN'',''BASIC'')) ORDER BY TO_NUMBER (QBD.WEIGHT_BREAK_SLAB)';

    EXECUTE IMMEDIATE ('insert into RATES_HEADER ' || v_sqlIn || v_sqlwh ||
                      v_wghtBreak);
  END IF;

  IF (p_weight_break) = 'LIST' THEN
    v_sqlwh := v_sqlwh || ' AND QBM.WEIGHT_BREAK =' || '''' ||
               p_weight_break || '''';

    v_wghtBreak := '  AND QBD.RATE_DESCRIPTION IN(''A FREIGHT RATE'') AND QBM.RATE_TYPE =' || '''' ||
                   p_rate_type || '''' ||
                   ' AND QBD.WEIGHT_BREAK_SLAB <>''OVERPIVOT'' ORDER BY qbd.weight_break_slab';

    print_out('insert into RATES_HEADER ' || v_sqlIn || v_sqlwh ||
              v_wghtBreak);
    EXECUTE IMMEDIATE ('insert into RATES_HEADER ' || v_sqlIn || v_sqlwh ||
                      v_wghtBreak);
  END IF;

  IF (p_weight_break) = 'LIST' AND p_shmode = '1' THEN
    EXECUTE IMMEDIATE ('insert into RATES_HEADER select ''OVERPIVOT'' from dual');
  END IF;

  v_sqlwh := v_sqlwh || ' AND QBM.WEIGHT_BREAK =' || '''' || p_weight_break || '''';

  v_wghtBreak := ' AND QBD.RATE_DESCRIPTION NOT IN( ''A FREIGHT RATE'')  AND QBM.RATE_TYPE =' || '''' ||
          --       p_rate_type || '''' || ' ORDER BY qbd.weight_break_slab ';
                   p_rate_type || '''' || ' ORDER BY SUBSTR(WEIGHT_BREAK_SLAB,0,5),
                              DECODE(is_Number(WEIGHT_BREAK_SLAB) ,''TRUE'',TO_NUMBER(WEIGHT_BREAK_SLAB), DECODE(SUBSTR(UPPER(WEIGHT_BREAK_SLAB),6),''BASIC'',TO_NUMBER(1),''MIN'',TO_NUMBER(2),''FLAT'',TO_NUMBER(3),''ABSOLUTE'',TO_NUMBER(4),''PERCENT'',TO_NUMBER(5),DECODE(UPPER(WEIGHT_BREAK_SLAB),''BASIC'',
                            TO_NUMBER(1),
                            ''MIN'',
                            TO_NUMBER(-9999),
                            ''FLAT'',
                            TO_NUMBER(3),
                            ''ABSOLUTE'',
                            TO_NUMBER(4),
                            ''PERCENT'',TO_NUMBER(5)))) ';

  EXECUTE IMMEDIATE ('insert into RATES_HEADER ' || v_sqlIn || v_sqlwh ||
                    v_wghtBreak);

  OPEN p_rs FOR
    SELECT * FROM RATES_HEADER;

  COMMIT;
END;

/

/

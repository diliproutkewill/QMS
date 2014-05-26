--------------------------------------------------------
--  DDL for Package Body QMS_RSR_RATES_PKG_ACCEPTANCE
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_RSR_RATES_PKG_ACCEPTANCE" AS
  /*  Using this Procedure for acceptance and getting the modified buy rates information from QMS_REC_CON_SELLRATESmstr_ACC,
   *  QMS_REC_CON_SELLRATESDTL_ACC tables according to accesslevel and passing the in parameters are
   *  p_shmode             VARCHAR2;
   *  p_consoltype         VARCHAR2;
   *  p_terminal           VARCHAR2;
   *  p_page_no            NUMBER;
   *  p_page_rows          NUMBER;
   *  sortBy               VARCHAR2;
   *  sortOrder            VARCHAR2;
   *  and out parameters are
   *  p_tot_rec            NUMBER;
   *  p_tot_pages          NUMBER;
   *  p_rs                 resultset;
  */
  PROCEDURE comman_proc(p_shmode     VARCHAR2,
                        p_consoltype VARCHAR2 DEFAULT NULL,
                        p_terminal   VARCHAR2,
                        p_page_no    NUMBER DEFAULT 1,
                        p_page_rows  NUMBER DEFAULT 50,
                        sortby       VARCHAR2,
                        sortorder    VARCHAR2,
                        p_tot_rec    OUT NUMBER,
                        p_tot_pages  OUT NUMBER,
                        p_origin     VARCHAR2,
                        p_rs         OUT resultset) AS
    v_id1             VARCHAR2(200);
    v_id2             VARCHAR2(200);
    v_id3             VARCHAR2(200);
    v_id4             VARCHAR2(200);
    v_id5             VARCHAR2(200);
    v_id6             VARCHAR2(200);
    v_id7             VARCHAR2(200);
    v_id8             VARCHAR2(200);
    v_id9             VARCHAR2(200);
    v_id10            VARCHAR2(200);
    v_id11            VARCHAR2(200);
    v_id12            VARCHAR2(200);
    v_id13            VARCHAR2(200);
    v_id14            VARCHAR2(200);
    v_id15            VARCHAR2(200);
    v_id16            VARCHAR2(200);
    v_id17            VARCHAR2(200);
    v_id18            VARCHAR2(200);
    v_id19            VARCHAR2(200);
    v_id20            VARCHAR2(200);
    v_id21            VARCHAR2(200);
    v_id22            VARCHAR2(200);
    v_id23            VARCHAR2(200);
    v_id24            VARCHAR2(400);
    v_id25            VARCHAR2(200);
    p_id              VARCHAR2(2000) := ' ';
    p_id1             VARCHAR2(2000) := ' ';
    p_id2             VARCHAR2(2000) := ' ';
    p_id3             VARCHAR2(2000) := ' ';
    p_id4             VARCHAR2(2000) := ' ';
    p_id5             VARCHAR2(2000);
    p_id6             VARCHAR2(4000);
    p_id7             VARCHAR2(2000);
    v_sql1            VARCHAR2(2000);
    v_terminal        VARCHAR2(32767);
    v_shmode          VARCHAR2(300);
    v_origin          VARCHAR(300);
    v_consoltype      VARCHAR2(300);
    v_operation       VARCHAR2(300);
    v_type            VARCHAR2(1);
    v_terminals       VARCHAR2(32767);
    v_chargerate      VARCHAR2(400);
    v_opr_adm_flag    VARCHAR2(10);
    v_console_type    VARCHAR2(300);
    v_curs            sys_refcursor;
    v_curs1           sys_refcursor;
    v_buyrateid       VARCHAR2(50);
    v_laneno          VARCHAR2(50);
    v_rcid            VARCHAR2(50);
    v_weightbreakslab VARCHAR2(20);
    v_Ratedesc        VARCHAR2(1000);
    v_buyrateid1      VARCHAR2(50);
    v_rec_con_id      VARCHAR2(50);
    v_lane_no         VARCHAR2(50);
    v_orderby         VARCHAR2(50);
    v_lineno          VARCHAR2(50);
     v_versionno          NUMBER(10);
     v_id26             VARCHAR2(1000);--subrahmanyam for notes
		 v_ext_notes							VARCHAR2(1000);--Added by Mohan for External Notes


    CURSOR c1 IS(
      SELECT child_terminal_id termid
        FROM FS_FR_TERMINAL_REGN
       WHERE parent_terminal_id = p_terminal
      UNION
      SELECT p_terminal termid FROM DUAL);

    /*
    (SELECT parent_terminal_id termid
       FROM fs_fr_terminal_regn
      WHERE child_terminal_id = p_terminal
     UNION
     SELECT terminalid termid
       FROM fs_fr_terminalmaster
      WHERE oper_admin_flag = 'H'
     UNION
     SELECT p_terminal termid
       FROM DUAL);*/

    CURSOR c2 IS(
      SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER);
  BEGIN
    IF (sortby = 'Origin') THEN
      v_orderby := ' origin ';
    ELSIF (sortby = 'OriginCountry') THEN
      v_orderby := ' org_countryid ';
    ELSIF (sortby = 'Destination') THEN
      v_orderby := ' destination ';
    ELSIF (sortby = 'DestinationCountry') THEN
      v_orderby := ' dest_countryid ';
    ELSIF (sortby = 'Carrier') THEN
      v_orderby := ' carrier_id ';
    ELSIF (sortby = 'ServiceLevel') THEN
      v_orderby := ' service_level ';
    ELSIF (sortby = 'Frequency') THEN
      v_orderby := ' frequency ';
    ELSE
      v_orderby := ' origin ';
    END IF;

    v_orderby := ' ORDER BY ' || v_orderby || sortorder;

    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_acceptance');

    SELECT oper_admin_flag
      INTO v_opr_adm_flag
      FROM FS_FR_TERMINALMASTER
     WHERE terminalid = p_terminal;

    IF UPPER(TRIM(v_opr_adm_flag)) = 'H' THEN
     /* FOR k IN c2 LOOP

        v_terminals := v_terminals || '''' || k.terminalid || '''' || ',';
      END LOOP;*/
     v_terminals := v_terminals ||'''' || p_terminal|| '''' || ',';

    ELSE
      FOR k IN c1 LOOP

        v_terminals := v_terminals || '''' || k.termid || '''' || ',';
      END LOOP;
    END IF;

    v_terminals := SUBSTR(v_terminals, 1, LENGTH(v_terminals) - 1);


    IF p_consoltype IS NULL THEN
      v_console_type := ' AND a.CONSOLE_TYPE IS NULL';
    ELSE
      v_console_type := ' AND a.CONSOLE_TYPE=' || '''' || p_consoltype || '''';
    END IF;

      IF p_origin IS NULL THEN
      v_origin := ' AND a.origin LIKE '||'''%''';
    ELSE
      v_origin := ' AND a.origin=' || '''' || p_origin || '''';
    END IF;

   /* OPEN v_curs FOR('SELECT  buyrateid,LANE_NO,B.rec_con_id rec_con_id
                           FROM QMS_REC_CON_SELLRATESDTL_ACC a,QMS_REC_CON_SELLRATESmstr_ACC b WHERE a.rec_con_id=b.rec_con_id  AND b.terminalid IN (' ||
                    v_terminals || ')' || 'AND b.shipment_mode =' ||
                    p_shmode || v_console_type ||
                    ' AND A.AI_FLAG=''A'' and a.line_no=''0''');*/

  /* print_out('SELECT  buyrateid,version_no,LANE_NO,B.rec_con_id rec_con_id
                           FROM QMS_REC_CON_SELLRATESDTL_ACC a,QMS_REC_CON_SELLRATESMSTR_ACC b WHERE a.rec_con_id=b.rec_con_id  AND b.terminalid IN (' ||
                    v_terminals || ')' || 'AND b.shipment_mode =' ||
                    p_shmode || v_console_type || v_origin ||
                    ' AND A.AI_FLAG=''A'' and a.line_no=''0''');*/


OPEN v_curs FOR('SELECT  buyrateid,version_no,LANE_NO,B.rec_con_id rec_con_id
                           FROM QMS_REC_CON_SELLRATESDTL_ACC a,QMS_REC_CON_SELLRATESMSTR_ACC b WHERE a.rec_con_id=b.rec_con_id  AND b.terminalid IN (' ||
                    v_terminals || ')' || 'AND b.shipment_mode =' ||
                    p_shmode || v_console_type || v_origin ||
                    ' AND A.AI_FLAG=''A'' and a.line_no=''0''');
    /*FOR i IN (SELECT DISTINCT buyrateid,LANE_NO
    FROM QMS_REC_CON_SELLRATESDTL_ACC a,QMS_REC_CON_SELLRATESmstr_ACC b WHERE a.rec_con_id=b.rec_con_id AND b.shipment_mode = p_shmode)*/
    LOOP
      FETCH v_curs
        --INTO v_buyrateid, v_laneno, v_rcid;
        INTO v_buyrateid, v_versionno ,v_laneno, v_rcid;--@@Modified for the WPBN issue-146448 on 23/12/08

      EXIT WHEN v_curs%NOTFOUND;
      --DBMS_OUTPUT.PUT_LINE('1');
      v_id1  := ' ';
      v_id2  := ' ';
      v_id3  := ' ';
      v_id4  := ' ';
      v_id5  := ' ';
      v_id6  := ' ';
      v_id7  := ' ';
      v_id8  := ' ';
      v_id9  := ' ';
      v_id10 := ' ';
      v_id11 := ' ';
      v_id12 := ' ';
      v_id13 := ' ';
      v_id14 := ' ';
      v_id15 := ' ';
      v_id16 := ' ';
      v_id17 := ' ';
      v_id18 := ' ';
      v_id19 := ' ';
      v_id20 := ' ';
      v_id21 := ' ';
      v_id22 := ' ';
      v_id23 := ' ';
      --v_id24 := ' ';
      v_id25 := ' ';
      p_id  := '';
      p_id1 := ' ';
      p_id2 := ' ';
      p_id3 := ' ';
      p_id4 := ' ';
      p_id5 := ' ';
      p_id6 := '';
      p_id7 := 'service';
      v_id26 :='';
			v_ext_notes :='';

         v_terminal   := '''' || p_terminal || '''';
      v_shmode     := '''' || p_shmode || '''';
      v_origin     := '''' || p_origin || '''';
      v_consoltype := '''' || p_consoltype || '''';

  /*    OPEN v_curs1 FOR('SELECT DISTINCT a.buyrateid buyrateid1, a.version_no version_no,a.rec_con_id, a.lane_no, a.weightbreakslab,' ||
                       '  a.line_no FROM QMS_REC_CON_SELLRATESDTL_ACC a,QMS_REC_CON_SELLRATESMSTR_ACC b ' ||
                       '  WHERE a.REC_CON_ID =b.REC_CON_ID and buyrateid IN (SELECT qbd.buyrateid buyrateid ' ||
                       '  FROM QMS_REC_CON_SELLRATESDTL_ACC qbd,QMS_REC_CON_SELLRATESMSTR_ACC qbm WHERE qbd.rec_con_id = qbm.rec_con_id ' ||
                       '  AND a.buyrateid = qbd.buyrateid  AND a.version_no=qbd.version_no AND a.buyrateid = ' || '''' ||
                       v_buyrateid || '''' || '  AND a.version_no= ' || '''' ||
                       v_versionno || '''' ||' AND a.lane_no= ' || '''' ||
                       v_laneno || '''' || ' )  and b.terminalid in (' ||
                       v_terminals || ') ' || 'and b.rec_con_id = ' ||
                       v_rcid ||
                       '  ORDER BY buyrateid, lane_no, line_no');
*/
 OPEN v_curs1 FOR('SELECT  a.buyrateid buyrateid1, a.version_no version_no,a.rec_con_id, a.lane_no, a.weightbreakslab,' ||
                       '  a.line_no,a.rate_description FROM QMS_REC_CON_SELLRATESDTL_ACC a ' ||
                       '  WHERE a.REC_CON_ID ='||v_rcid|| ' and a.buyrateid='|| v_buyrateid||' and a.lane_no='||v_laneno||' and a.version_no='||v_versionno ||'  ORDER BY buyrateid, lane_no, line_no');

      --AND b.rec_con_id = v_rcid
      LOOP
        FETCH v_curs1
          INTO v_buyrateid1,v_versionno, v_rec_con_id, v_lane_no, v_weightbreakslab, v_lineno,v_Ratedesc;
 --PRINT_OUT(v_buyrateid1||'-----'||v_rec_con_id||'---------'||'---------'||v_lane_no||'----------'||v_Ratedesc);

        EXIT WHEN v_curs1%NOTFOUND;

      /*  IF p_consoltype IS NULL THEN*/

          /*EXECUTE IMMEDIATE (' select  a.notes,a.buyrateid,a.version_no,A.CARRIER_ID,A.ORIGIN,a.destination,A.SERVICELEVEL_ID,A.WEIGHTBREAKSLAB,b.weight_class,b.rate_type,b.weight_break,a.transit_time,a.frequency,a.chargerate,a.lane_no,a.origin_country,a.desti_country,b.currency,a.upper_bound,a.lowrer_bound,a.chargerate_indicator,a.margin_perc,b.overall_margin,b.margin_type,a.rec_con_id,Decode(a.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',a.RATE_DESCRIPTION)RATE_DESCRIPTION  from QMS_rec_con_sellratesdtl_acc a, QMS_rec_con_sellratesmstr_acc b where ' --@@Modified by kameswari for Surcharge Enhancements
                            || '''' || v_weightbreakslab || '''' ||
                            '=a.weightbreakslab and a.rec_con_id=b.rec_con_id and a.buyrateid=' ||
                            v_buyrateid1 ||' and a.rec_con_id=' ||
                            v_rec_con_id || ' and a.lane_no=' ||
                            v_lane_no || '  AND a.version_no='  ||
                            v_versionno
                              )
            INTO v_id26,v_id1, v_id25,v_id2, v_id3, v_id4, v_id5, v_id6, v_id7, v_id8, v_id9, v_id10, v_id11, v_id12, v_id13, v_id14, v_id15, v_id16, v_id17, v_id18, v_id19, v_id20, v_id21, v_id22, v_id23, v_id24;

        ELSE*/


          EXECUTE IMMEDIATE (' select  trim(a.notes),a.buyrateid,a.version_no,A.CARRIER_ID,A.ORIGIN,a.destination,A.SERVICELEVEL_ID,A.WEIGHTBREAKSLAB,b.weight_class,b.rate_type,b.weight_break,a.transit_time,a.frequency,a.chargerate,a.lane_no,a.origin_country,a.desti_country,b.currency,a.upper_bound,a.lowrer_bound,a.chargerate_indicator,a.margin_perc,b.overall_margin,b.margin_type,a.rec_con_id,Decode(a.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',a.RATE_DESCRIPTION)RATE_DESCRIPTION, trim(a.EXTERNAL_NOTES) from QMS_rec_con_sellratesdtl_acc a, QMS_rec_con_sellratesmstr_acc b where ' --@@Modified by kameswari for Surcharge Enhancements
                            || '''' || v_weightbreakslab || '''' ||
                            '=a.weightbreakslab and a.rec_con_id=b.rec_con_id and a.buyrateid=' ||v_buyrateid1 ||
                            ' and a.rec_con_id=' || v_rec_con_id ||
                            ' and a.lane_no=' || v_lane_no ||
                            ' AND a.version_no='  || v_versionno ||
                            ' AND A.RATE_DESCRIPTION= '||''''||v_Ratedesc||''''
                              )
            INTO v_id26,v_id1, v_id25,v_id2, v_id3, v_id4, v_id5, v_id6, v_id7, v_id8, v_id9, v_id10, v_id11, v_id12, v_id13, v_id14, v_id15, v_id16, v_id17, v_id18, v_id19, v_id20, v_id21, v_id22, v_id23, v_id24,v_ext_notes;
      --PRINT_OUT(v_id26||'-----'||v_ext_notes);
      /*  END IF;*/

        IF p_id7 = 'service' THEN
          p_id7 := v_id5;
        END IF;

        p_id  := p_id || v_id6 || ',';
        p_id1 := p_id1 || v_id12 || ',';
        p_id2 := p_id2 || v_id17 || ',';
        p_id3 := p_id3 || v_id18 || ',';
        /*  IF p_id6 IS NOT NULL
         THEN
        p_id6 := p_id6 || v_id24 || ',';
        ELSE
         p_id6 := v_id24 || ',';
         END IF;*/
        p_id6 := p_id6 || v_id24 || ',';
        IF v_id19 IS NOT NULL THEN
          p_id4 := p_id4 || v_id19 || ',';
        END IF;

        p_id5 := p_id5 || v_id20 || ',';
      END LOOP;

      INSERT INTO BASE_DATA_ACCEPTANCE
        (buyrateid,
        version_no,
         carrier_id,
         origin,
         destination,
         service_level,
         weight_break_slab,
         wt_class,
         rate_type,
         weight_break,
         transit_time,
         frequency,
         chargerate,
         lane_no,
         org_countryid,
         dest_countryid,
         currency,
         ubound,
         lbound,
         c_indicator,
         marginperc,
         overall_margin,
         margin_type,
         rec_buyrate_id,
         rate_description,
         notes,
				 EXTERNAL_NOTES)
      VALUES
        (v_id1,
        v_id25,
         v_id2,
         v_id3,
         v_id4,
         p_id7,
         RTRIM(p_id, ','),
         v_id7,
         v_id8,
         v_id9,
         v_id10,
         v_id11,
         RTRIM(p_id1, ','),
         TO_NUMBER(v_id13),
         v_id14,
         v_id15,
         v_id16,
         RTRIM(p_id2, ','),
         RTRIM(p_id3, ','),
         RTRIM(p_id4, ','),
         RTRIM(p_id5, ','),
         v_id21,
         v_id22,
         v_id23,
         RTRIM(p_id6, ','),
         v_id26,
				 v_ext_notes);

    END LOOP;
    CLOSE v_curs;
    COMMIT;
    v_id25 := 'SELECT *  FROM (SELECT t1.*, ROWNUM rn FROM (SELECT * FROM base_data_acceptance ' ||
              v_orderby || ' ) t1 WHERE ROWNUM <=  (( ' || p_page_no ||
              '- 1) * ' || p_page_rows || ') + ' || p_page_rows ||
              ') WHERE rn > (( ' || p_page_no || '- 1) *' || p_page_rows || ')';

    OPEN p_rs FOR v_id25;

    /*OPEN p_rs
    FOR
     SELECT *
         FROM (SELECT t1.*, ROWNUM rn
                 FROM (SELECT *
                         FROM base_data_acceptance v_orderby ) t1
                WHERE ROWNUM <=
                              ((p_page_no - 1) * 2) + 2)
        WHERE rn > ((p_page_no - 1) * 2); */
    SELECT COUNT(*) INTO p_tot_rec FROM BASE_DATA_ACCEPTANCE;

    /*SELECT CEIL ((p_tot_rec / p_page_rows))
    INTO p_tot_pages
    FROM DUAL;*/
    p_tot_pages := CEIL(p_tot_rec / p_page_rows);

    EXCEPTION
    WHEN OTHERS
    THEN

     DBMS_OUTPUT.put_line ('399--------'||p_id);
       DBMS_OUTPUT.put_line ('400--------'||v_id26);

       DBMS_OUTPUT.put_line ('402--------'||v_rec_con_id);
  END;
END;

/

/

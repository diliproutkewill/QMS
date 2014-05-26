--------------------------------------------------------
--  DDL for Package Body SELL_RATES_PROC
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "SELL_RATES_PROC" 
AS
   PROCEDURE buy_sell_rates (
      p_org_locs             VARCHAR2,
      p_dest_locs            VARCHAR2,
      p_terminal             VARCHAR2,
      p_acclevel             VARCHAR2,
      p_rate_type            VARCHAR2,
      p_weight_break         VARCHAR2,
      p_srvlevl              VARCHAR2,
      p_carrier              VARCHAR2,
      p_currency             VARCHAR2,
      p_shmode               VARCHAR2,
      p_operation            VARCHAR2,
      p_page_no              NUMBER DEFAULT 1,
      p_page_rows            NUMBER DEFAULT 50,
      p_tot_rec        OUT   NUMBER,
      p_tot_pages      OUT   NUMBER,
      p_rs             OUT   Qms_Rsr_Rates_Pkg.resultset
   )
   IS
      v_org_locs    VARCHAR2 (1000);
      v_dest_locs   VARCHAR2 (1000);
      v_srvlevl     VARCHAR2 (1000);
      v_carrier     VARCHAR2 (1000);
   BEGIN
      IF (UPPER (p_operation) = 'ADD')
      THEN
         DBMS_OUTPUT.put_line ('Calling the comman_proc');
         Qms_Rsr_Rates_Pkg.comman_proc (p_operation,
                                        p_org_locs,
                                        p_dest_locs,
                                        p_terminal,
                                        p_acclevel,
                                        p_rate_type,
                                        p_weight_break,
                                        p_srvlevl,
                                        p_carrier,
                                        p_currency,
                                        p_shmode,
                                        'IS NULL',
                                        p_page_no,
                                        p_page_rows,
                                        p_tot_rec,
                                        p_tot_pages,
                                        p_rs
                                       );
      END IF;

      IF (UPPER (p_operation) = 'MODIFY')
      THEN
         Qms_Rsr_Rates_Pkg.comman_proc (p_operation,
                                        p_org_locs,
                                        p_dest_locs,
                                        p_terminal,
                                        p_acclevel,
                                        p_rate_type,
                                        p_weight_break,
                                        p_srvlevl,
                                        p_carrier,
                                        p_currency,
                                        p_shmode,
                                        'Y',
                                        p_page_no,
                                        p_page_rows,
                                        p_tot_rec,
                                        p_tot_pages,
                                        p_rs
                                       );
      ELSIF (UPPER (p_operation) = 'VIEW')
      THEN
         DBMS_OUTPUT.put_line ('...Before Calling View');
         Qms_Rsr_Rates_Pkg.sell_rates_proc (p_org_locs,
                                            p_dest_locs,
                                            p_terminal,
                                            p_acclevel,
                                            p_rate_type,
                                            p_weight_break,
                                            p_srvlevl,
                                            p_carrier,
                                            p_currency,
                                            p_shmode,
                                            'T',
                                            p_page_no,
                                            p_page_rows,
                                            p_tot_rec,
                                            p_tot_pages,
                                            p_rs
                                           );
      ELSIF (UPPER (p_operation) = 'INVALIDATE')
      THEN
         Qms_Rsr_Rates_Pkg.sell_rates_proc (p_org_locs,
                                            p_dest_locs,
                                            p_terminal,
                                            p_acclevel,
                                            p_rate_type,
                                            p_weight_break,
                                            p_srvlevl,
                                            p_carrier,
                                            p_currency,
                                            p_shmode,
                                            'I',
                                            p_page_no,
                                            p_page_rows,
                                            p_tot_rec,
                                            p_tot_pages,
                                            p_rs
                                           );
      END IF;
   EXCEPTION
      WHEN OTHERS
      THEN
         Qms_Rsr_Rates_Pkg.g_err := '<< ' || SQLERRM || ' >>';
         Qms_Rsr_Rates_Pkg.g_err_code := '<< ' || SQLCODE || ' >>';

         INSERT INTO QMS_OBJECTS_ERRORS
                     (ex_date, module_name,
                      errorcode, errormessage
                     )
              VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->Buy_Sell_Rates',
                      Qms_Rsr_Rates_Pkg.g_err_code, Qms_Rsr_Rates_Pkg.g_err
                     );

         COMMIT;
   END;

   PROCEDURE sell_rates_proc (
      p_org_locs             VARCHAR2,
      p_dest_locs            VARCHAR2,
      p_terminal             VARCHAR2,
      p_acclevel             VARCHAR2,
      p_rate_type            VARCHAR2,
      p_weight_break         VARCHAR2,
      p_srvlevl              VARCHAR2,
      p_carrier              VARCHAR2,
      p_currency             VARCHAR2,
      p_shmode               VARCHAR2,
      p_qry                  VARCHAR2,
      p_page_no              NUMBER DEFAULT 1,
      p_page_rows            NUMBER DEFAULT 50,
      p_tot_rec        OUT   NUMBER,
      p_tot_pages      OUT   NUMBER,
      p_rs             OUT   resultset
   )
   AS
      v_org_locs       VARCHAR2 (300);
      v_dest_locs      VARCHAR2 (300);
      v_terminal       VARCHAR2 (300);
      v_acclevel       VARCHAR2 (300);
      v_rate_type      VARCHAR2 (300);
      v_weight_break   VARCHAR2 (300);
      v_srvlevl        VARCHAR2 (300);
      v_carrier        VARCHAR2 (300);
      v_currency       VARCHAR2 (300);
      v_shmode         VARCHAR2 (300);
      v_operation      VARCHAR2 (300);
      v_type           VARCHAR2 (1);
      v_terminals      VARCHAR2 (300);
      v_chargerate     VARCHAR2 (400)  := '';
      k                NUMBER          := 0;
      v_sql1           VARCHAR (2000);
      v_sql2           VARCHAR (2000);
      v_sql3           VARCHAR (2000);
      v_sql4           VARCHAR (2000);
      v_sql5           VARCHAR2 (2000);
      v_sql6           VARCHAR2 (2000);
      v_base           VARCHAR2 (30);
      v_break          VARCHAR2 (4000);
      v_temp           VARCHAR2 (200);
      v_countryid      VARCHAR2 (30);
      v_countryid1     VARCHAR2 (30);
      v_opr_adm_flag   VARCHAR2 (10);
      v_inv            VARCHAR2 (20);
      v_rc_c1          resultset;
      v_break_slab     VARCHAR2 (40);

      CURSOR c1
      IS
         (SELECT parent_terminal_id termid
            FROM FS_FR_TERMINAL_REGN
           WHERE child_terminal_id = p_terminal
          UNION
          SELECT terminalid termid
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminal termid
            FROM DUAL);

      CURSOR c2
      IS
         (SELECT DISTINCT terminalid
                     FROM FS_FR_TERMINALMASTER);
   BEGIN
      v_terminal := '''' || p_terminal || '''';
      v_acclevel := '''' || p_acclevel || '''';
      v_rate_type := '''' || UPPER (p_rate_type) || '''';
      v_weight_break := '''' || UPPER (p_weight_break) || '''';
      v_currency := '''' || p_currency || '''';
      v_shmode := '''' || p_shmode || '''';

      IF p_org_locs IS NOT NULL
      THEN
         v_org_locs :=
               ' AND sd.origin IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_org_locs)
            || ''
            || ')';
      END IF;

      IF p_dest_locs IS NOT NULL
      THEN
         v_dest_locs :=
               '  AND sd.destination IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_dest_locs)
            || ''
            || ') ';
      END IF;

      IF p_srvlevl IS NOT NULL
      THEN
         v_srvlevl :=
               '  AND sd.SERVICELEVEL_ID IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_srvlevl)
            || ''
            || ')';
      END IF;

      IF p_carrier IS NOT NULL
      THEN
         v_carrier :=
               '   AND sd.carrier_id IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_carrier)
            || ''
            || ' ) ';
      END IF;

      IF UPPER (TRIM (p_qry)) = 'T'
      THEN
         v_inv := '<>' || '''' || 'T' || '''';
      ELSE
         v_inv :=
            '  in (' || '''' || 'T' || '''' || ',' || '''' || 'F' || ''''
            || ')';
      END IF;

      DBMS_OUTPUT.put_line (v_inv);

      EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data');

      EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data');

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminal;

      --DBMS_OUTPUT.put_line ('<< ' || v_opr_adm_flag || ' >>');

      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.termid || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
      DBMS_OUTPUT.put_line ('hi');

      IF UPPER (p_weight_break) = 'FLAT'
      THEN
         v_break := 'Min,Flat,';
      END IF;

      v_sql1 :=
         'insert into temp_data(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO,rec_buyrateid) SELECT sm.rec_con_id rec_con_id, sd.weightbreakslab weightbreakslab,chargerate chargerate,sd.lane_no lane_no,sd.LINE_NO line_no,sd.buyrateid  FROM QMS_REC_CON_SELLRATESMASTER sm, QMS_REC_CON_SELLRATESDTL sd WHERE sm.rec_con_id = sd.rec_con_id ';
      v_sql6 := v_org_locs || v_dest_locs || v_srvlevl;
      v_sql4 :=
            '  AND sd.ai_flag <>''I''  and sd.INVALIDATE '
         || v_inv
         || '  AND sm.rc_flag = ''R''  ';
      v_sql2 :=
            '   AND sm.shipment_mode ='
         || v_shmode
         || '  AND sm.weight_break ='
         || v_weight_break;
      v_sql3 :=
            '  AND sm.rate_type ='
         || v_rate_type
         || v_carrier
         || ' AND sm.terminalid IN ('
         || v_terminals
         || ')';
      v_sql5 :=
            'insert into base_data(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE,LANE_NO,INVALIDATE,REC_BUYRATE_ID,CURRENCY,DENSITY_CODE,NOTES)   SELECT DISTINCT  sm.rec_con_id  buyrateid, sd.carrier_id carrier_id, sd.origin origin, sd.destination destination,sd.servicelevel_id servicelevel_id,''a''  weight_break_slab,sd.transit_time transit_time, sd.frequency frequency,''a ''  chargerate,sd.LANE_NO,SD.INVALIDATE INVALIDATE,sd.buyrateid  REC_CON_ID,sm.CURRENCY,sd.DENSITY_CODE,sd.NOTES  FROM QMS_REC_CON_SELLRATESMASTER sm, QMS_REC_CON_SELLRATESDTL sd  WHERE sd.REC_CON_ID = sm.REC_CON_ID   '
         || v_sql6;
--      DBMS_OUTPUT.put_line (v_sql1);
      --DBMS_OUTPUT.put_line (v_sql6);
      --DBMS_OUTPUT.put_line (v_sql4);
      --DBMS_OUTPUT.put_line (v_sql2);
      --DBMS_OUTPUT.put_line (v_sql3);

      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);

      EXECUTE IMMEDIATE (   v_sql5
                         || v_sql4
                         || v_sql2
                         || v_sql3
                         || ' ORDER BY buyrateid,lane_no'
                        );

      COMMIT;
      DBMS_OUTPUT.put_line ('hi');
      DBMS_OUTPUT.put_line ('hi');

      FOR j IN (SELECT   buyrateid, weight_break_slab, chargerate, lane_no,
                         origin, destination, rec_buyrate_id
                    FROM BASE_DATA
                ORDER BY buyrateid, rec_buyrate_id, lane_no)
      LOOP
         v_chargerate := '';
         k := 1;
         DBMS_OUTPUT.put_line ('hi' || j.buyrateid || '          '
                               || j.lane_no
                              );
         v_break := '';

         EXECUTE IMMEDIATE (   'select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE) from temp_data where  buyrateid ='
                            || ''''
                            || j.buyrateid
                            || ''''
                            || ' AND LANE_NO='
                            || ''''
                            || j.lane_no
                            || ''''
                            || ' and line_no=0 '
                            || ' and rec_buyrateid='
                            || ''''
                            || j.rec_buyrate_id
                            || ''''
                           )
                      INTO v_break, v_chargerate;

         DBMS_OUTPUT.put_line (v_break || v_chargerate);

         IF UPPER (p_weight_break) = 'LIST'
         THEN
            v_base := v_chargerate;
            v_chargerate := '';
         ELSE
            v_chargerate := v_chargerate || ',';
         END IF;

         DBMS_OUTPUT.put_line (v_base);

         IF (UPPER (p_weight_break) <> 'FLAT')
         THEN
            IF UPPER (p_weight_break) = 'LIST'
            THEN
               v_sql1 :=
                  'SELECT DISTINCT  weight_break_slab break_slab  FROM TEMP_DATA  WHERE line_no > 0   ORDER BY break_slab  ';
            ELSE
               v_sql1 :=
                  'SELECT DISTINCT  weight_break_slab break_slab FROM TEMP_DATA  WHERE line_no > 0  order by  TO_NUMBER(break_slab)';
            END IF;

            DBMS_OUTPUT.put_line (v_sql1);

            OPEN v_rc_c1
             FOR v_sql1;

            LOOP
               DBMS_OUTPUT.put_line (' Here..');

               FETCH v_rc_c1
                INTO v_break_slab;

               DBMS_OUTPUT.put_line (   v_break_slab
                                     || '    '
                                     || j.buyrateid
                                     || '     '
                                     || j.lane_no
                                    );
               EXIT WHEN v_rc_c1%NOTFOUND;

               BEGIN
                  EXECUTE IMMEDIATE (   ' SELECT  TO_CHAR(chargerate)  FROM temp_data   WHERE weight_break_slab ='
                                     || ''''
                                     || v_break_slab
                                     || ''''
                                     || '  AND buyrateid ='
                                     || ''''
                                     || j.buyrateid
                                     || ''''
                                     || ' and LANE_NO='
                                     || ''''
                                     || j.lane_no
                                     || ''''
                                     || ' and rec_buyrateid='
                                     || ''''
                                     || j.rec_buyrate_id
                                     || ''''
                                    )
                               INTO v_temp;

                  DBMS_OUTPUT.put_line (v_temp);
                  v_chargerate := v_chargerate || v_temp || ',';
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN
                     v_chargerate := v_chargerate || '0,';
                  WHEN OTHERS
                  THEN
                     DBMS_OUTPUT.put_line (SQLERRM);
               END;
            END LOOP;

            IF UPPER (p_weight_break) = 'LIST'
            THEN
               v_chargerate := v_chargerate || v_base || ',';
            END IF;

            v_base := v_break;
            v_break := '';
            DBMS_OUTPUT.put_line ('After Loop ' || v_base || '  '
                                  || v_chargerate
                                 );

            CLOSE v_rc_c1;

            OPEN v_rc_c1
             FOR v_sql1;

            LOOP
               DBMS_OUTPUT.put_line (' Here..');

               FETCH v_rc_c1
                INTO v_break_slab;

               DBMS_OUTPUT.put_line (   v_break_slab
                                     || '    '
                                     || j.buyrateid
                                     || '     '
                                     || j.lane_no
                                    );
               EXIT WHEN v_rc_c1%NOTFOUND;
               v_break := v_break || ',' || v_break_slab;
            END LOOP;

            IF (UPPER (p_weight_break) = 'SLAB')
            THEN
               v_break := v_base || v_break;
            ELSIF UPPER (p_weight_break) = 'LIST'
            THEN
               v_break := v_break || ',' || v_base;
               v_break := SUBSTR (v_break, 2, LENGTH (v_break));
            END IF;
         ELSE
            SELECT weight_break_slab, chargerate
              INTO v_base, v_temp
              FROM TEMP_DATA
             WHERE line_no > 0
               AND buyrateid = j.buyrateid
               AND lane_no = j.lane_no
               AND rec_buyrateid = j.rec_buyrate_id;

            v_break := v_break || ',' || v_base;
            v_chargerate := v_chargerate || v_temp || ',';
         END IF;

         UPDATE BASE_DATA
            SET weight_break_slab = v_break,
                chargerate =
                            SUBSTR (v_chargerate, 1, LENGTH (v_chargerate) - 1)
          WHERE buyrateid = j.buyrateid
            AND lane_no = j.lane_no
            AND rec_buyrate_id = j.rec_buyrate_id;

         IF p_shmode <> 2
         THEN
            BEGIN
               SELECT countryid
                 INTO v_countryid
                 FROM FS_FR_LOCATIONMASTER
                WHERE locationid = j.origin;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid := '';
            END;
         ELSE
            BEGIN
               SELECT countryid
                 INTO v_countryid
                 FROM FS_FRS_PORTMASTER
                WHERE portid = j.origin;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid := '';
            END;
         END IF;

         IF p_shmode <> 2
         THEN
            BEGIN
               SELECT countryid
                 INTO v_countryid1
                 FROM FS_FR_LOCATIONMASTER
                WHERE locationid = j.destination;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid1 := '';
            END;
         ELSE
            BEGIN
               SELECT countryid
                 INTO v_countryid1
                 FROM FS_FRS_PORTMASTER
                WHERE portid = j.destination;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid1 := '';
            END;
         END IF;

         UPDATE BASE_DATA
            SET org_countryid = v_countryid,
                dest_countryid = v_countryid1
          WHERE buyrateid = j.buyrateid
            AND lane_no = j.lane_no
            AND rec_buyrate_id = j.rec_buyrate_id;
      END LOOP;

      OPEN p_rs
       FOR
          SELECT *
            FROM (SELECT t1.*, ROWNUM rn
                    FROM (SELECT *
                            FROM BASE_DATA) t1
                   WHERE ROWNUM <=
                                 ((p_page_no - 1) * p_page_rows) + p_page_rows)
           WHERE rn > ((p_page_no - 1) * p_page_rows);

      SELECT COUNT (*)
        INTO p_tot_rec
        FROM BASE_DATA;

      SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         p_tot_rec := 0;
         p_tot_pages := 0;
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.put_line (SQLERRM);
         Qms_Rsr_Rates_Pkg.g_err := '<< ' || SQLERRM || ' >>';
         Qms_Rsr_Rates_Pkg.g_err_code := '<< ' || SQLCODE || ' >>';

         INSERT INTO QMS_OBJECTS_ERRORS
                     (ex_date, module_name,
                      errorcode, errormessage
                     )
              VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->Sell_buy_proc',
                      Qms_Rsr_Rates_Pkg.g_err_code, Qms_Rsr_Rates_Pkg.g_err
                     );

         COMMIT;
   END;

   PROCEDURE comman_proc (
      p_operation            VARCHAR2,
      p_org_locs             VARCHAR2,
      p_dest_locs            VARCHAR2,
      p_terminal             VARCHAR2,
      p_acclevel             VARCHAR2,
      p_rate_type            VARCHAR2,
      p_weight_break         VARCHAR2,
      p_srvlevl              VARCHAR2,
      p_carrier              VARCHAR2,
      p_currency             VARCHAR2,
      p_shmode               VARCHAR2,
      p_qry                  VARCHAR2,
      p_page_no              NUMBER DEFAULT 1,
      p_page_rows            NUMBER DEFAULT 50,
      p_tot_rec        OUT   NUMBER,
      p_tot_pages      OUT   NUMBER,
      p_rs             OUT   resultset
   )
   AS
      v_org_locs       VARCHAR2 (1000);
      v_dest_locs      VARCHAR2 (1000);
      v_terminal       VARCHAR2 (300);
      v_acclevel       VARCHAR2 (300);
      v_rate_type      VARCHAR2 (300);
      v_weight_break   VARCHAR2 (300);
      v_srvlevl        VARCHAR2 (1000);
      v_carrier        VARCHAR2 (1000);
      v_currency       VARCHAR2 (300);
      v_shmode         VARCHAR2 (300);
      v_operation      VARCHAR2 (300);
      v_type           VARCHAR2 (1);
      v_terminals      VARCHAR2 (300);
      v_chargerate     VARCHAR2 (400)  := '';
      k                NUMBER          := 0;
      v_sql1           VARCHAR (2000);
      v_sql2           VARCHAR (2000);
      v_sql3           VARCHAR (2000);
      v_sql4           VARCHAR (2000);
      v_sql5           VARCHAR2 (2000);
      v_sql6           VARCHAR2 (2000);
      v_base           VARCHAR2 (30);
      v_break          VARCHAR2 (4000);
      v_temp           VARCHAR2 (200);
      v_countryid      VARCHAR2 (30);
      v_countryid1     VARCHAR2 (30);
      v_temp1          VARCHAR2 (10);
      v_temp2          VARCHAR2 (10);
      v_temp3          VARCHAR2 (10);
      v_ubound         VARCHAR2 (100);
      v_lbound         VARCHAR2 (100);
      v_indicator      VARCHAR2 (100);
      v_qry            VARCHAR2 (10);
      v_rc_c1          resultset;
      v_break_slab     VARCHAR2 (20);

      CURSOR c1
      IS
         (SELECT parent_terminal_id termid
            FROM FS_FR_TERMINAL_REGN
           WHERE child_terminal_id = p_terminal
          UNION
          SELECT terminalid termid
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminal termid
            FROM DUAL);

      CURSOR c2
      IS
        (SELECT p_terminal termid
           FROM DUAL
          UNION
         SELECT child_terminal_id termid
           FROM FS_FR_TERMINAL_REGN
         CONNECT BY PRIOR child_terminal_id = parent_terminal_id
         START WITH parent_terminal_id = p_terminal);
   BEGIN
      IF p_org_locs IS NOT NULL
      THEN
         v_org_locs :=
               ' AND qbd.origin IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_org_locs)
            || ''
            || ')';
      END IF;

      IF p_dest_locs IS NOT NULL
      THEN
         v_dest_locs :=
               '  AND qbd.destination IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_dest_locs)
            || ''
            || ') ';
      END IF;

      IF p_srvlevl IS NOT NULL
      THEN
         v_srvlevl :=
               '  AND qbd.SERVICE_LEVEL  IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_srvlevl)
            || ''
            || ')';
      END IF;

      IF p_carrier IS NOT NULL
      THEN
         v_carrier :=
               '   AND qbd.carrier_id IN ('
            || ''
            || Qms_Rsr_Rates_Pkg.seperator (p_carrier)
            || ''
            || ' ) ';
      END IF;

      v_terminal := '''' || p_terminal || '''';
      v_acclevel := '''' || p_acclevel || '''';
      v_rate_type := '''' || p_rate_type || '''';
      v_weight_break := '''' || p_weight_break || '''';
      v_currency := '''' || p_currency || '''';
      v_shmode := '''' || p_shmode || '''';

      IF UPPER (TRIM (p_qry)) <> 'IS NULL'
      THEN
         v_qry := '=' || '''' || p_qry || '''';
      ELSE
         v_qry := p_qry;
      END IF;

      EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data');

      EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data');
      IF UPPER (p_operation) = 'ADD'
      THEN
        FOR i IN c1
        LOOP
           v_terminals := v_terminals || '''' || i.termid || '''' || ',';
        END LOOP;
      ELSIF UPPER (p_operation) = 'MODIFY'
      THEN
        FOR i IN c2
        LOOP
           v_terminals := v_terminals || '''' || i.termid || '''' || ',';
        END LOOP;
      END IF;
      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
      DBMS_OUTPUT.put_line ('Comman Proc hi ---->' || v_qry);

      IF UPPER (p_weight_break) = 'FLAT'
      THEN
         v_break := 'Min,Flat,';
      END IF;

      DBMS_OUTPUT.put_line (p_org_locs);
      DBMS_OUTPUT.put_line (p_dest_locs);
      DBMS_OUTPUT.put_line (p_srvlevl);
      DBMS_OUTPUT.put_line (p_carrier);
      v_sql1 :=
         'insert into temp_data(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO,LBOUND, UBOUND, C_INDICATOR) SELECT qbd.buyrateid,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR  FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid ';
      v_sql6 := v_org_locs || v_dest_locs || v_srvlevl;
      v_sql4 :=
            '  AND qbd.activeinactive IS NULL AND qbd.INVALIDATE IS NULL'
         || '  AND qbd.generated_flag '
         || v_qry;
      v_sql2 :=
            '   AND qbm.shipment_mode ='
         || v_shmode
         || '  AND qbm.weight_break ='
         || v_weight_break;
      v_sql3 :=
            '  AND qbm.rate_type ='
         || v_rate_type
         || v_carrier
         || '   AND qbm.terminalid IN ('
         || v_terminals
         || ')';
      v_sql5 :=
            'insert into base_data(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,CURRENCY,DENSITY_CODE,NOTES) select  distinct qbd.buyrateid buyrateid, qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ''a ''  chargerate, qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.CURRENCY,qbd.DENSITY_CODE,qbd.NOTES   FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  '
         || v_sql6;
--      DBMS_OUTPUT.put_line (v_sql1);
      DBMS_OUTPUT.put_line (v_sql6);
      DBMS_OUTPUT.put_line (v_sql4);
      DBMS_OUTPUT.put_line (v_sql2);
      DBMS_OUTPUT.put_line (v_sql3);

      EXECUTE IMMEDIATE (v_sql1 || v_sql6 || v_sql4 || v_sql2 || v_sql3);

      EXECUTE IMMEDIATE (   v_sql5
                         || v_sql4
                         || v_sql2
                         || v_sql3
                         || ' ORDER BY buyrateid, lane_no'
                        );

      COMMIT;
      DBMS_OUTPUT.put_line ('hi');
      DBMS_OUTPUT.put_line ('hi');

      FOR j IN (SELECT   buyrateid, weight_break_slab, chargerate, lane_no,
                         origin, destination
                    FROM BASE_DATA
                ORDER BY buyrateid)
      LOOP
         v_chargerate := '';
         k := 1;
         DBMS_OUTPUT.put_line ('hi ');
         v_break := '';

         EXECUTE IMMEDIATE (   'select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-'') from temp_data where  buyrateid ='
                            || ''''
                            || j.buyrateid
                            || ''''
                            || ' AND LANE_NO='
                            || ''''
                            || j.lane_no
                            || ''''
                            || ' and line_no=0'
                           )
                      INTO v_break, v_chargerate, v_temp1, v_temp2, v_temp3;

         DBMS_OUTPUT.put_line (v_break || v_chargerate);
         v_lbound := v_temp1 || ',';
         v_ubound := v_temp2 || ',';
         v_indicator := v_temp3 || ',';

         IF UPPER (p_weight_break) = 'LIST'
         THEN
            v_base := v_chargerate;
            v_chargerate := '';
         ELSE
            v_chargerate := v_chargerate || ',';
         END IF;

         DBMS_OUTPUT.put_line (v_base);

         IF (UPPER (p_weight_break) <> 'FLAT')
         THEN
            IF UPPER (p_weight_break) = 'LIST'
            THEN
               v_sql1 :=
                  'SELECT DISTINCT  weight_break_slab break_slab  FROM TEMP_DATA  WHERE line_no > 0   ORDER BY break_slab  ';
            ELSE
               v_sql1 :=
                  'SELECT DISTINCT  weight_break_slab break_slab FROM TEMP_DATA  WHERE line_no > 0  order by  TO_NUMBER(break_slab)';
            END IF;

            DBMS_OUTPUT.put_line (v_sql1);

            OPEN v_rc_c1
             FOR v_sql1;

            LOOP
               DBMS_OUTPUT.put_line (' Here..');

               FETCH v_rc_c1
                INTO v_break_slab;

               DBMS_OUTPUT.put_line (v_break_slab);
               EXIT WHEN v_rc_c1%NOTFOUND;

               BEGIN
                  EXECUTE IMMEDIATE (   ' SELECT  TO_CHAR(chargerate)  FROM temp_data   WHERE weight_break_slab ='
                                     || ''''
                                     || v_break_slab
                                     || ''''
                                     || '  AND buyrateid ='
                                     || ''''
                                     || j.buyrateid
                                     || ''''
                                     || ' and LANE_NO='
                                     || ''''
                                     || j.lane_no
                                     || ''''
                                    )
                               INTO v_temp;
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN
                     v_temp := '-';
                  WHEN OTHERS
                  THEN
                     DBMS_OUTPUT.put_line (SQLERRM);
               END;

               BEGIN
                  EXECUTE IMMEDIATE (   ' SELECT  LBOUND  FROM temp_data   WHERE weight_break_slab ='
                                     || ''''
                                     || v_break_slab
                                     || ''''
                                     || '  AND buyrateid ='
                                     || ''''
                                     || j.buyrateid
                                     || ''''
                                     || ' and LANE_NO='
                                     || ''''
                                     || j.lane_no
                                     || ''''
                                    )
                               INTO v_temp1;
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN
                     v_temp1 := '-';
                  WHEN OTHERS
                  THEN
                     DBMS_OUTPUT.put_line (SQLERRM);
               END;

               BEGIN
                  EXECUTE IMMEDIATE (   ' SELECT UBOUND  FROM temp_data   WHERE weight_break_slab ='
                                     || ''''
                                     || v_break_slab
                                     || ''''
                                     || '  AND buyrateid ='
                                     || ''''
                                     || j.buyrateid
                                     || ''''
                                     || ' and LANE_NO='
                                     || ''''
                                     || j.lane_no
                                     || ''''
                                    )
                               INTO v_temp2;
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN
                     v_temp2 := '-';
                  WHEN OTHERS
                  THEN
                     DBMS_OUTPUT.put_line (SQLERRM);
               END;

               BEGIN
                  EXECUTE IMMEDIATE (   ' SELECT C_INDICATOR  FROM temp_data   WHERE weight_break_slab ='
                                     || ''''
                                     || v_break_slab
                                     || ''''
                                     || '  AND buyrateid ='
                                     || ''''
                                     || j.buyrateid
                                     || ''''
                                     || ' and LANE_NO='
                                     || ''''
                                     || j.lane_no
                                     || ''''
                                    )
                               INTO v_temp3;

                  IF v_temp3 IS NULL
                  THEN
                     v_temp3 := '-';
                  END IF;
               EXCEPTION
                  WHEN NO_DATA_FOUND
                  THEN
                     v_temp3 := '-';
               END;

               v_chargerate := v_chargerate || v_temp || ',';
               v_lbound := v_lbound || v_temp1 || ',';
               v_ubound := v_ubound || v_temp2 || ',';
               v_indicator := v_indicator || v_temp3 || ',';
            END LOOP;

            IF UPPER (p_weight_break) = 'LIST'
            THEN
               v_chargerate := v_chargerate || v_base || ',';
            END IF;

            v_base := v_break;
            v_break := '';
            DBMS_OUTPUT.put_line (v_base || '  ' || v_chargerate);

            CLOSE v_rc_c1;

            OPEN v_rc_c1
             FOR v_sql1;

            LOOP
               FETCH v_rc_c1
                INTO v_break_slab;

               EXIT WHEN v_rc_c1%NOTFOUND;
               v_break := v_break || ',' || v_break_slab;
            END LOOP;

            CLOSE v_rc_c1;

            IF (UPPER (p_weight_break) = 'SLAB')
            THEN
               v_break := v_base || v_break;
            ELSIF UPPER (p_weight_break) = 'LIST'
            THEN
               v_break := v_break || ',' || v_base;
               v_break := SUBSTR (v_break, 2, LENGTH (v_break));
            END IF;
         ELSE
            SELECT weight_break_slab, chargerate
              INTO v_base, v_temp
              FROM TEMP_DATA
             WHERE line_no > 0
               AND buyrateid = j.buyrateid
               AND lane_no = j.lane_no;

            v_break := v_break || ',' || v_base;
            v_chargerate := v_chargerate || v_temp || ',';
         END IF;

         UPDATE BASE_DATA
            SET weight_break_slab = v_break,
                chargerate =
                            SUBSTR (v_chargerate, 1, LENGTH (v_chargerate) - 1),
                ubound = SUBSTR (v_ubound, 1, LENGTH (v_ubound) - 1),
                lbound = SUBSTR (v_lbound, 1, LENGTH (v_lbound) - 1),
                c_indicator = SUBSTR (v_indicator, 1, LENGTH (v_indicator) - 1)
          WHERE buyrateid = j.buyrateid AND lane_no = j.lane_no;

         IF p_shmode <> 2
         THEN
            BEGIN
               SELECT countryid
                 INTO v_countryid
                 FROM FS_FR_LOCATIONMASTER
                WHERE locationid = j.origin;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid := '';
            END;
         ELSE
            BEGIN
               SELECT countryid
                 INTO v_countryid
                 FROM FS_FRS_PORTMASTER
                WHERE portid = j.origin;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid := '';
            END;
         END IF;

         IF p_shmode <> 2
         THEN
            BEGIN
               SELECT countryid
                 INTO v_countryid1
                 FROM FS_FR_LOCATIONMASTER
                WHERE locationid = j.destination;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid1 := '';
            END;
         ELSE
            BEGIN
               SELECT countryid
                 INTO v_countryid1
                 FROM FS_FRS_PORTMASTER
                WHERE portid = j.destination;
            EXCEPTION
               WHEN NO_DATA_FOUND
               THEN
                  v_countryid1 := '';
            END;
         END IF;

         UPDATE BASE_DATA
            SET org_countryid = v_countryid,
                dest_countryid = v_countryid1
          WHERE buyrateid = j.buyrateid AND lane_no = j.lane_no;
      END LOOP;

      FOR i IN (SELECT weight_break_slab, chargerate
                  FROM BASE_DATA)
      LOOP
         DBMS_OUTPUT.put_line (   i.weight_break_slab
                               || '              '
                               || i.chargerate
                              );
      END LOOP;

      OPEN p_rs
       FOR
          SELECT *
            FROM (SELECT t1.*, ROWNUM rn
                    FROM (SELECT *
                            FROM BASE_DATA) t1
                   WHERE ROWNUM <=
                                 ((p_page_no - 1) * p_page_rows) + p_page_rows)
           WHERE rn > ((p_page_no - 1) * p_page_rows);

      SELECT COUNT (*)
        INTO p_tot_rec
        FROM BASE_DATA;

      SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         p_tot_rec := 0;
         p_tot_pages := 0;
      WHEN OTHERS
      THEN
         Qms_Rsr_Rates_Pkg.g_err := '<< ' || SQLERRM || ' >>';
         Qms_Rsr_Rates_Pkg.g_err_code := '<< ' || SQLCODE || ' >>';

         INSERT INTO QMS_OBJECTS_ERRORS
                     (ex_date, module_name,
                      errorcode, errormessage
                     )
              VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->comman_proc',
                      Qms_Rsr_Rates_Pkg.g_err_code, Qms_Rsr_Rates_Pkg.g_err
                     );

         COMMIT;
   END;

   FUNCTION seperator (p_str VARCHAR2)
      RETURN VARCHAR2
   AS
      v_return   VARCHAR2 (1000) := '';
      k          NUMBER;
      f          NUMBER;
      r          NUMBER          := 1;
   BEGIN
      k := INSTR (p_str, '~', 1, 1) - 1;
      f := 1;
      r := 1;

      WHILE k > 0
      LOOP
         IF r != 1
         THEN
            f := f + 1;
         END IF;

         k := k + 1;
--dbms_output.put_line(f ||'  '||k);
         v_return :=
                    v_return || '''' || SUBSTR (p_str, f, k - f) || ''''
                    || ',';
         f := INSTR (p_str, '~', 1, r);
         r := r + 1;
         k := INSTR (p_str, '~', 1, r) - 1;
      END LOOP;

      v_return :=
            v_return
         || ''''
         || SUBSTR (p_str, INSTR (p_str, '~', -1) + 1, LENGTH (p_str))
         || '''';
      RETURN v_return;
   EXCEPTION
      WHEN OTHERS
      THEN
         Qms_Rsr_Rates_Pkg.g_err := '<< ' || SQLERRM || ' >>';
         Qms_Rsr_Rates_Pkg.g_err_code := '<< ' || SQLCODE || ' >>';

         INSERT INTO QMS_OBJECTS_ERRORS
                     (ex_date, module_name,
                      errorcode, errormessage
                     )
              VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->Seperator',
                      Qms_Rsr_Rates_Pkg.g_err_code, Qms_Rsr_Rates_Pkg.g_err
                     );

         COMMIT;
   END;

   FUNCTION qms_sell_rate_validation (
      p_origin                VARCHAR2,
      p_destination           VARCHAR2,
      p_serv_level            VARCHAR2,
      p_carrier               VARCHAR2,
      p_frequency             VARCHAR2,
      p_current_terminal_id   VARCHAR2,
      p_wet_break             VARCHAR2,
      p_rate_type             VARCHAR2,
      p_shipmentmode          VARCHAR2,
      p_newsellrateid         NUMBER
   )
      RETURN NUMBER
   AS
      str             VARCHAR2 (10) := '';
      v_accesslevel   VARCHAR2 (10) := '';
      v_shipmodestr   VARCHAR2 (30) := '';
   BEGIN
      str :=
         validate_sellrate (p_origin,
                            p_destination,
                            p_serv_level,
                            p_carrier,
                            p_frequency,
                            p_current_terminal_id,
                            p_wet_break,
                            p_rate_type,
                            p_shipmentmode
                           );
      DBMS_OUTPUT.put_line (str);

      IF str = 'TRUE'
      THEN
         RETURN 5;
      END IF;

      SELECT oper_admin_flag
        INTO v_accesslevel
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_current_terminal_id;

      IF p_shipmentmode = '1'
      THEN
         v_shipmodestr := 'Air';
      ELSIF p_shipmentmode = '2'
      THEN
         v_shipmodestr := 'Sea';
      ELSE
         v_shipmodestr := 'Truck';
      END IF;

      IF v_accesslevel = 'H'
      THEN
         FOR i IN
            (SELECT a.rec_con_id rec_con_id, b.buyrateid, b.lane_no,
                    b.origin, b.destination
               FROM QMS_REC_CON_SELLRATESMASTER a,
                    QMS_REC_CON_SELLRATESDTL b
              WHERE b.rec_con_id = a.rec_con_id
                AND b.origin = p_origin
                AND b.destination = p_destination
                AND b.servicelevel_id = p_serv_level
                AND b.carrier_id = p_carrier
                AND b.frequency = p_frequency
                AND b.rec_con_id IN (
                       SELECT dtl.rec_con_id
                         FROM QMS_REC_CON_SELLRATESMASTER mas,
                              QMS_REC_CON_SELLRATESDTL dtl
                        WHERE mas.rc_flag = 'R'
                          AND mas.rec_con_id = dtl.rec_con_id)
                AND a.weight_break = p_wet_break
                AND a.rate_type = p_rate_type
                AND a.shipment_mode = p_shipmentmode
                AND b.ai_flag = 'A'
                AND a.terminalid IN (
                       SELECT DISTINCT terminalid
                                  FROM FS_FR_TERMINALMASTER
                                       ))
         LOOP
            Qms_Quotepack_New.qms_quote_update (i.rec_con_id,
                                                i.buyrateid,
                                                i.lane_no,
                                                p_newsellrateid,
                                                i.buyrateid,
                                                i.lane_no,
                                                'RSR',
                                                NULL,
                                                NULL,
                                                   i.origin
                                                || '-'
                                                || i.destination
                                                || ','
                                                || v_shipmodestr
                                                || ' Freight Rates'
                                               );

            UPDATE QMS_REC_CON_SELLRATESDTL
               SET ai_flag = 'I'
             WHERE rec_con_id = i.rec_con_id
               AND buyrateid = i.buyrateid
               AND lane_no = i.lane_no;
         END LOOP;
      ELSE
         FOR i IN
            (SELECT DISTINCT a.rec_con_id rec_con_id, b.buyrateid, b.lane_no,
                             b.origin, b.destination
                        FROM QMS_REC_CON_SELLRATESMASTER a,
                             QMS_REC_CON_SELLRATESDTL b
                       WHERE b.rec_con_id = a.rec_con_id
                         AND b.origin = p_origin
                         AND b.destination = p_destination
                         AND b.servicelevel_id = p_serv_level
                         AND b.carrier_id = p_carrier
                         AND b.frequency = p_frequency
                         AND b.rec_con_id IN (
                                SELECT dtl.rec_con_id
                                  FROM QMS_REC_CON_SELLRATESMASTER mas,
                                       QMS_REC_CON_SELLRATESDTL dtl
                                 WHERE mas.rc_flag = 'R'
                                   AND mas.rec_con_id = dtl.rec_con_id)
                         AND a.weight_break = p_wet_break
                         AND a.rate_type = p_rate_type
                         AND a.shipment_mode = p_shipmentmode
                         AND b.ai_flag = 'A'
                         AND a.terminalid IN (
                                SELECT     child_terminal_id terminalid
                                      FROM FS_FR_TERMINAL_REGN
                                CONNECT BY PRIOR child_terminal_id =
                                                            parent_terminal_id
                                START WITH parent_terminal_id =
                                                         p_current_terminal_id
                                UNION
                                SELECT terminalid
                                  FROM FS_FR_TERMINALMASTER
                                 WHERE terminalid = p_current_terminal_id))
         LOOP
            DBMS_OUTPUT.put_line (i.rec_con_id);
            DBMS_OUTPUT.put_line (i.buyrateid);
            DBMS_OUTPUT.put_line (i.lane_no);
            DBMS_OUTPUT.put_line (p_newsellrateid);
            DBMS_OUTPUT.put_line (i.buyrateid);
            DBMS_OUTPUT.put_line (i.lane_no);
            Qms_Quotepack_New.qms_quote_update (i.rec_con_id,
                                                i.buyrateid,
                                                i.lane_no,
                                                p_newsellrateid,
                                                i.buyrateid,
                                                i.lane_no,
                                                'RSR',
                                                NULL,
                                                NULL,
                                                   i.origin
                                                || '-'
                                                || i.destination
                                                || ','
                                                || v_shipmodestr
                                                || ' Freight Rates'
                                               );

            UPDATE QMS_REC_CON_SELLRATESDTL
               SET ai_flag = 'I'
             WHERE rec_con_id = i.rec_con_id
               AND buyrateid = i.buyrateid
               AND lane_no = i.lane_no;
         END LOOP;
      END IF;

      RETURN 6;
   EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.put_line (SQLERRM);
         RETURN 0;
   END;

   FUNCTION validate_sellrate (
      p_origin                VARCHAR2,
      p_destination           VARCHAR2,
      p_serv_level            VARCHAR2,
      p_carrier               VARCHAR2,
      p_frequency             VARCHAR2,
      p_current_terminal_id   VARCHAR2,
      p_weight_break          VARCHAR2,
      p_rate_type             VARCHAR2,
      p_shipmentmode          VARCHAR2
   )
      RETURN VARCHAR2
   IS
      v_origin   VARCHAR2 (50);
   BEGIN
      SELECT DISTINCT b.origin origin
                 INTO v_origin
                 FROM QMS_REC_CON_SELLRATESMASTER a,
                      QMS_REC_CON_SELLRATESDTL b
                WHERE b.rec_con_id = a.rec_con_id
                  AND b.servicelevel_id = p_serv_level
                  AND b.carrier_id = p_carrier
                  AND b.ai_flag = 'A'
                  AND b.INVALIDATE = 'F'
                  AND b.origin = p_origin
                  AND b.destination = p_destination
                  AND a.weight_break = p_weight_break
                  AND a.rate_type = p_rate_type
                  AND a.shipment_mode = p_shipmentmode
                  AND a.rc_flag = 'R'
                  AND a.terminalid IN (
                         SELECT     parent_terminal_id terminalid
                               FROM FS_FR_TERMINAL_REGN
                         CONNECT BY PRIOR parent_terminal_id =
                                                             child_terminal_id
                         START WITH child_terminal_id = p_current_terminal_id
                         UNION
                         SELECT terminalid
                           FROM FS_FR_TERMINALMASTER
                          WHERE oper_admin_flag = 'H'
                            AND terminalid <> p_current_terminal_id);

      IF SQL%FOUND
      THEN
         RETURN 'TRUE';
      ELSE
         RETURN 'FALSE';
      END IF;
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         RETURN 'FALSE';
      WHEN OTHERS
      THEN
         RETURN 'FALSE';
   END;
END;

/

/

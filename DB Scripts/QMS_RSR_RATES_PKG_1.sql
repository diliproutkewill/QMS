--------------------------------------------------------
--  DDL for Package Body QMS_RSR_RATES_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_RSR_RATES_PKG" AS

  /*  Using this Procedure for getting the sell rate information and having
   *  comman_proc();
   *  comman_proc_modify();
   *  sell_rates_proc(); procedurs
   *  and
   *  seperator();
   *  qms_sell_rate_validation();
   *  validate_sellrate(); functions.
  */
  PROCEDURE Buy_Sell_Rates(p_Org_Locs     VARCHAR2,
                           p_Dest_Locs    VARCHAR2,
                           p_Terminal     VARCHAR2,
                           p_Acclevel     VARCHAR2,
                           p_Rate_Type    VARCHAR2,
                           p_Weight_Break VARCHAR2,
                           p_Srvlevl      VARCHAR2,
                           p_Carrier      VARCHAR2,
                           p_Currency     VARCHAR2,
                           p_Shmode       VARCHAR2,
                           p_Operation    VARCHAR2,
                           p_Page_No      NUMBER DEFAULT 1,
                           p_Page_Rows    NUMBER DEFAULT 50,
                           p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                           p_Tot_Rec      OUT NUMBER,
                           p_Tot_Pages    OUT NUMBER,
                           p_Rs           OUT Qms_Rsr_Rates_Pkg.Resultset) IS
    v_Org_Locs  VARCHAR2(1000);
    v_Dest_Locs VARCHAR2(1000);
    v_Srvlevl   VARCHAR2(1000);
    v_Carrier   VARCHAR2(1000);
  BEGIN

    IF (UPPER(p_Operation) = 'ADD') THEN
      --print_out('Calling the comman_proc');
      Qms_Rsr_Rates_Pkg.Comman_Proc(p_Operation,
                                    p_Org_Locs,
                                    p_Dest_Locs,
                                    p_Terminal,
                                    p_Acclevel,
                                    p_Rate_Type,
                                    p_Weight_Break,
                                    p_Srvlevl,
                                    p_Carrier,
                                    p_Currency,
                                    p_Shmode,
                                    'IS NULL',
                                    p_Page_No,
                                    p_Page_Rows,
                                    p_org_countries,
                                    p_dest_countries,
                                    p_org_regions,
                                    p_dest_regions,
                                    p_Tot_Rec,
                                    p_Tot_Pages,
                                    p_Rs);
    END IF;
    IF (UPPER(p_Operation) = 'MODIFY') THEN
      Qms_Rsr_Rates_Pkg.Comman_Proc_Modify(p_Operation,
                                           p_Org_Locs,
                                           p_Dest_Locs,
                                           p_Terminal,
                                           p_Acclevel,
                                           p_Rate_Type,
                                           p_Weight_Break,
                                           p_Srvlevl,
                                           p_Carrier,
                                           p_Currency,
                                           p_Shmode,
                                           'IS NULL',
                                           p_Page_No,
                                           p_Page_Rows,
                                           p_org_countries,
                                            p_dest_countries,
                                            p_org_regions,
                                            p_dest_regions,
                                           p_Tot_Rec,
                                           p_Tot_Pages,
                                           p_Rs);
    ELSIF (UPPER(p_Operation) = 'VIEW') THEN

      Qms_Rsr_Rates_Pkg.Sell_Rates_Proc(p_Org_Locs,
                                        p_Dest_Locs,
                                        p_Terminal,
                                        p_Acclevel,
                                        p_Rate_Type,
                                        p_Weight_Break,
                                        p_Srvlevl,
                                        p_Carrier,
                                        p_Currency,
                                        p_Shmode,
                                        'T',
                                        p_Page_No,
                                        p_Page_Rows,
                                        p_org_countries,
                                            p_dest_countries,
                                            p_org_regions,
                                            p_dest_regions,
                                        p_Tot_Rec,
                                        p_Tot_Pages,
                                        p_Rs);
    ELSIF (UPPER(p_Operation) = 'INVALIDATE') THEN
      Qms_Rsr_Rates_Pkg.Sell_Rates_Proc(p_Org_Locs,
                                        p_Dest_Locs,
                                        p_Terminal,
                                        p_Acclevel,
                                        p_Rate_Type,
                                        p_Weight_Break,
                                        p_Srvlevl,
                                        p_Carrier,
                                        p_Currency,
                                        p_Shmode,
                                        'I',
                                        p_Page_No,
                                        p_Page_Rows,
                                        p_org_countries,
                                            p_dest_countries,
                                            p_org_regions,
                                            p_dest_regions,
                                        p_Tot_Rec,
                                        p_Tot_Pages,
                                        p_Rs);
    END IF;
    /*EXCEPTION
    WHEN OTHERS
    THEN
       qms_rsr_rates_pkg.g_err := '<< ' || SQLERRM || ' >>';
       qms_rsr_rates_pkg.g_err_code := '<< ' || SQLCODE || ' >>';
       INSERT INTO qms_objects_errors
                   (ex_date, module_name,
                    errorcode, errormessage
                   )
            VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->Buy_Sell_Rates',
                    qms_rsr_rates_pkg.g_err_code, qms_rsr_rates_pkg.g_err
                   );
       COMMIT;*/
  END;

  /*  Using this Procedure for view,invalidate and getting the sell rates information from QMS_REC_CON_SELLRATESMASTER,
   *  QMS_REC_CON_SELLRATESDTL tables according to accesslevel and passing the in parameters are
   *  p_org_locs             VARCHAR2;
   *  p_dest_locs            VARCHAR2;
   *  p_terminal             VARCHAR2;
   *  p_acclevel             VARCHAR2;
   *  p_rate_type            VARCHAR2;
   *  p_weight_break         VARCHAR2;
   *  p_srvlevl              VARCHAR2;
   *  p_carrier              VARCHAR2;
   *  p_currency             VARCHAR2;
   *  p_shmode               VARCHAR2;
   *  p_qry                  VARCHAR2;
   *  p_page_no              NUMBER ;
   *  p_page_rows            NUMBER ;
   *  and out parameters are
   *   p_tot_rec              NUMBER,
   *   p_tot_pages            NUMBER,
   *   p_rs                   resultset
  */
  PROCEDURE Sell_Rates_Proc(p_Org_Locs     VARCHAR2,
                            p_Dest_Locs    VARCHAR2,
                            p_Terminal     VARCHAR2,
                            p_Acclevel     VARCHAR2,
                            p_Rate_Type    VARCHAR2,
                            p_Weight_Break VARCHAR2,
                            p_Srvlevl      VARCHAR2,
                            p_Carrier      VARCHAR2,
                            p_Currency     VARCHAR2,
                            p_Shmode       VARCHAR2,
                            p_Qry          VARCHAR2,
                            p_Page_No      NUMBER DEFAULT 1,
                            p_Page_Rows    NUMBER DEFAULT 50,
                            p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                            p_Tot_Rec      OUT NUMBER,
                            p_Tot_Pages    OUT NUMBER,
                            p_Rs           OUT Resultset) AS
    v_Org_Locs     VARCHAR2(32767);
    v_Dest_Locs    VARCHAR2(32767);
    v_Terminal     VARCHAR2(32767);
    v_Acclevel     VARCHAR2(300);
    v_Rate_Type    VARCHAR2(300);
    v_Weight_Break VARCHAR2(300);
    v_Srvlevl      VARCHAR2(32767);
    v_Carrier      VARCHAR2(32767);
    v_Currency     VARCHAR2(300);
    v_Shmode       VARCHAR2(300);
    v_Operation    VARCHAR2(300);
    v_Type         VARCHAR2(1);
    v_Terminals    VARCHAR2(32767);
    v_Chargerate   VARCHAR2(32767) := '';
    /*v_Ratedesc     Varchar2(400) := '';*/
    v_Ratedesc     VARCHAR2(32767) := '';
    k              NUMBER := 0;
    v_Sql1         VARCHAR(2000);
    v_Sql_SUR         VARCHAR(2000);
    v_Sql2         VARCHAR(2000);
    v_Sql3         VARCHAR(32767);
    v_Sql4         VARCHAR(2000);
    v_Sql5         VARCHAR2(32767);
    v_Sql6         VARCHAR2(32767);
    v_Base         VARCHAR2(30);
    v_Break        VARCHAR2(4000);
    v_Temp         VARCHAR2(200);
    v_Temp4        VARCHAR2(200);
    v_src_temp_curr VARCHAR2(4000);
    v_src_curr VARCHAR2(4000);
    v_Countryid    VARCHAR2(30);
    v_Countryid1   VARCHAR2(30);
    v_Opr_Adm_Flag VARCHAR2(10);
    v_Inv          VARCHAR2(20);
    v_Rc_C1        Resultset;
    v_Rc_C2        Resultset;
    v_Break_Slab   VARCHAR2(40);
    v_rate_desc   VARCHAR2(100);
        v_org_reg      VARCHAR2(1000):= 'T';
    v_dest_reg     VARCHAR2(1000):= 'T';
    v_temp_break   VARCHAR2(1000);
    v_temp_rate   VARCHAR2(1000);
    v_temp_desc   VARCHAR2(1000);
    v_temp_count      NUMBER;
    --Added by Kishore For SurCharge Currency
    v_schCurreny  VARCHAR2(10000) := '';


    --     v_lineno       Varchar2(20);--@@Added by Kameswari for Surcharge Enhancements
    CURSOR C1 IS(
      SELECT Parent_Terminal_Id Termid
        FROM FS_FR_TERMINAL_REGN
       WHERE Child_Terminal_Id = p_Terminal
      UNION
      SELECT Terminalid Termid
        FROM FS_FR_TERMINALMASTER
       WHERE Oper_Admin_Flag = 'H'
      UNION
      SELECT p_Terminal Termid FROM Dual);
    CURSOR C2 IS(
      SELECT DISTINCT Terminalid FROM FS_FR_TERMINALMASTER);
  BEGIN
    v_Terminal     := '''' || p_Terminal || '''';
    v_Acclevel     := '''' || p_Acclevel || '''';
    v_Rate_Type    := '''' || UPPER(p_Rate_Type) || '''';
    v_Weight_Break := '''' || UPPER(p_Weight_Break) || '''';
    v_Currency     := '''' || p_Currency || '''';
    v_Shmode       := '''' || p_Shmode || '''';

    IF p_Org_Locs IS NOT NULL THEN
      v_Org_Locs := ' AND sd.origin IN (' || '' ||
                    Qms_Rsr_Rates_Pkg.Seperator(p_Org_Locs) || '' || ')';
       v_org_reg :=' AND sd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_Org_Locs) || '' || ')';
    END IF;
        IF (  p_org_countries IS NOT NULL  AND LENGTH ((v_org_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_org_reg  :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_org_countries) || '' || ') )';

    END IF;

  IF (  p_org_regions IS NOT NULL  AND LENGTH (TRIM (v_org_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_org_reg  :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_org_regions) || '' || ') )';

    END IF;
    IF p_Dest_Locs IS NOT NULL THEN
      v_Dest_Locs := '  AND sd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.Seperator(p_Dest_Locs) || '' || ') ';
       v_dest_reg := '  AND sd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';
    END IF;
          IF (  p_dest_countries IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_dest_reg :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_dest_reg := v_dest_reg || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_dest_countries) || '' || ') )';

    END IF;

 IF (  p_dest_regions IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg  := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_dest_reg :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_dest_reg := v_dest_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_dest_regions) || '' || ') )';

    END IF;
    IF p_Srvlevl IS NOT NULL THEN
      v_Srvlevl := '  AND sd.SERVICELEVEL_ID IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Srvlevl) || '' || ')';
    END IF;
    IF p_Carrier IS NOT NULL THEN
      v_Carrier := '   AND sd.carrier_id IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Carrier) || '' || ' ) ';
    END IF;

    IF UPPER(Trim(p_Qry)) = 'T' THEN
      v_Inv := '<>' || '''' || 'T' || '''';
    ELSE
      v_Inv := '  in (' || '''' || 'T' || '''' || ',' || '''' || 'F' || '''' || ')';
    END IF;

    EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data_1');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_1');
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal;

    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      FOR i IN C2 LOOP
        v_Terminals := v_Terminals || '''' || i.Terminalid || '''' || ',';
      END LOOP;
    ELSE
      FOR i IN C1 LOOP
        v_Terminals := v_Terminals || '''' || i.Termid || '''' || ',';
      END LOOP;
    END IF;
    v_Terminals := SUBSTR(v_Terminals, 1, LENGTH(v_Terminals) - 1);

    /* If Upper(p_Weight_Break) = 'FLAT' Then
      v_Break := 'Min,Flat,';
    End If;*/
    v_Sql1 := 'insert into temp_data_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO,rec_buyrateid,RATE_DESCRIPTION,SUR_CHARGE_CURRENCY) SELECT sm.rec_con_id rec_con_id, sd.weightbreakslab weightbreakslab,chargerate chargerate,sd.lane_no lane_no,sd.LINE_NO line_no,sd.buyrateid,DECODE(sd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',sd.RATE_DESCRIPTION)RATE_DESCRIPTION ,       DECODE(RATE_DESCRIPTION,''A FREIGHT RATE'',SM.CURRENCY,NVL((SELECT DT.SUR_CHARGE_CURRENCY FROM QMS_BUYRATES_DTL DT WHERE DT.BUYRATEID=SD.BUYRATEID AND DT.LANE_NO=SD.LANE_NO AND DT.VERSION_NO=SD.VERSION_NO AND DT.WEIGHT_BREAK_SLAB = SD.WEIGHTBREAKSLAB AND DT.ACTIVEINACTIVE IS NULL),SM.CURRENCY)) SUR_CHARGE_CURRENCY FROM QMS_REC_CON_SELLRATESMASTER sm, QMS_REC_CON_SELLRATESDTL sd WHERE sm.rec_con_id = sd.rec_con_id ';
    --v_Sql6 := v_Org_Locs || v_Dest_Locs || v_Srvlevl;

      IF v_org_reg <>'T' THEN
    v_Sql6 := v_Sql6 || v_org_reg;
    END IF;

    IF v_dest_reg <>'T' THEN
    v_Sql6 := v_Sql6 || v_dest_reg;
    END IF;
   -- v_Sql6 := v_Org_Locs || v_Dest_Locs || v_Srvlevl;

   v_Sql6 := v_Sql6 || v_Srvlevl;
    v_Sql4 := '  AND sd.ai_flag =''A''  and sd.INVALIDATE ' || v_Inv ||
              '  AND sm.rc_flag = ''R''  ';
    v_Sql2 := '   AND sm.shipment_mode =' || v_Shmode ||
              '  AND sm.weight_break =' || v_Weight_Break;
    v_Sql3 := '  AND sm.rate_type =' || v_Rate_Type || v_Carrier ||
              ' AND sm.terminalid IN (' || v_Terminals || ')';
    v_Sql5 := 'insert into base_data_1(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE,LANE_NO,INVALIDATE,REC_BUYRATE_ID,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION,VERSION_NO,EXTERNAL_NOTES)   SELECT DISTINCT  sm.rec_con_id  buyrateid, sd.carrier_id carrier_id, sd.origin origin, sd.destination destination,sd.servicelevel_id servicelevel_id,''a''  weight_break_slab,sd.transit_time transit_time, sd.frequency frequency,''a ''  chargerate,sd.LANE_NO,SD.INVALIDATE INVALIDATE,sd.buyrateid  REC_CON_ID,sm.CURRENCY,sd.DENSITY_CODE,sd.NOTES,DECODE(sd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',sd.RATE_DESCRIPTION)RATE_DESCRIPTION , sd.VERSION_NO,sd.EXTERNAL_NOTES  FROM QMS_REC_CON_SELLRATESMASTER sm, QMS_REC_CON_SELLRATESDTL sd  WHERE sd.REC_CON_ID = sm.REC_CON_ID   ' || -- modified by phani sekhar for wpbn 180129 on 20090821
              v_Sql6;
              print_out(v_Sql1 || v_Sql6 || v_Sql4 || v_Sql2 || v_Sql3);
              print_out('-----------');
              print_out(v_Sql5 || v_Sql4 || v_Sql2 || v_Sql3 ||
                      ' and( sd.rate_description =''A FREIGHT RATE'' or sd.rate_description IS NULL) ORDER BY buyrateid,lane_no');
    EXECUTE IMMEDIATE (v_Sql1 || v_Sql6 || v_Sql4 || v_Sql2 || v_Sql3);
    EXECUTE IMMEDIATE (v_Sql5 || v_Sql4 || v_Sql2 || v_Sql3 ||
                      ' and( sd.rate_description =''A FREIGHT RATE'' or sd.rate_description IS NULL) ORDER BY buyrateid,lane_no');
    /**print_out(v_Sql5 || v_Sql4 || v_Sql2 || v_Sql3 ||
    ' and( sd.rate_description =''FREIGHT RATE'' or sd.rate_description IS NULL) ORDER BY buyrateid,lane_no');*/
    COMMIT;

    FOR j IN (SELECT Buyrateid,
                     Weight_Break_Slab,
                     Chargerate,
                     Lane_No,
                     Origin,
                     Destination,
                     Rec_Buyrate_Id,
                     version_no
                FROM BASE_DATA_1
               ORDER BY Rec_Buyrate_Id, Buyrateid, Lane_No) LOOP
      v_Chargerate := '';
       v_Ratedesc := '';
      k            := 1;

      v_Break := '';
      v_temp_count :=0;
      v_temp_desc:='';
      IF (UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1) = FALSE THEN
      BEGIN
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),to_char(RATE_DESCRIPTION),SUR_CHARGE_CURRENCY from temp_data_1 where  buyrateid =:v_buy_rate_id
                            AND LANE_NO=:v_lane_no AND line_no=0 AND weight_break_slab=''BASIC'' AND rec_buyrateid=:v_rec_buy_rate_id')
          INTO v_Temp_Break, v_Temp_Rate, v_temp_desc, v_src_temp_curr
          USING j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;

          v_Chargerate := v_Temp_Rate ||',';
          v_Break      := v_Temp_Break ||',';
          v_Ratedesc   := v_temp_desc ||',';
          v_src_curr   := v_src_temp_curr || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          v_temp_count := v_temp_count +1;
          v_Chargerate := '0' ||',';
          v_Break      := 'BASIC' ||',';
          v_Ratedesc   := 'A FREIGHT RATE' ||',';
          v_src_curr   := '-' || ',';
                  EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),to_char(RATE_DESCRIPTION),SUR_CHARGE_CURRENCY from temp_data_1 where  buyrateid =:v_buy_rate_id
                            AND LANE_NO=:v_lane_no AND line_no=0 AND weight_break_slab=''MIN'' AND rec_buyrateid=:v_rec_buy_rate_id')
          INTO v_Temp_Break, v_Temp_Rate, v_temp_desc, v_src_temp_curr
          USING j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;

          v_Chargerate := v_Chargerate || v_Temp_Rate ;
          v_Break      := v_Break||v_Temp_Break;
          v_Ratedesc   := v_Ratedesc||v_temp_desc ;
          v_src_curr   := v_src_curr|| v_src_temp_curr;
          END;
          IF v_temp_count =0 THEN
                 EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),to_char(RATE_DESCRIPTION),SUR_CHARGE_CURRENCY from temp_data_1 where  buyrateid =:v_buy_rate_id
                            AND LANE_NO=:v_lane_no AND line_no=1 AND weight_break_slab=''MIN'' AND rec_buyrateid=:v_rec_buy_rate_id')
          INTO v_Temp_Break, v_Temp_Rate, v_temp_desc, v_src_temp_curr
          USING j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;

          v_Chargerate := v_Chargerate || v_Temp_Rate ;
          v_Break      := v_Break || v_Temp_Break;
          v_Ratedesc   := v_Ratedesc || v_temp_desc;
           v_src_curr  := v_src_curr|| v_src_temp_curr;
          END IF;
      END IF;



      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Base       := v_Chargerate;
        v_Chargerate := '';
      ELSIF UPPER(p_Weight_Break) = 'FLAT' OR
            UPPER(p_Weight_Break) = 'SLAB' THEN
        v_Chargerate := v_Chargerate || ',';
        v_Ratedesc := v_Ratedesc ||  ',';
        v_src_curr := v_src_curr || ',';
      END IF;
--Commented and added by subrahmanyam for the CR-219773
/*      --If (Upper(p_Weight_Break) <> 'FLAT') Then
      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0   ORDER BY break_slab  ';
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0   ORDER BY break_slab  ';
      Elsif Upper(p_Weight_Break) = 'LIST' And p_Shmode <> 1 Then
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  ORDER BY break_slab  ';
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  ORDER BY break_slab  ';
      Else
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  order by  TO_NUMBER(break_slab)';
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  order by  break_slab';
      End If;
*/

      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0  AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY break_slab  ';
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
        v_Sql1 := 'SELECT  DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE  RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY break_slab  ';
      ELSE
        v_Sql1 := 'SELECT  DISTINCT weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION=''A FREIGHT RATE'' order by  break_slab';
      END IF;

      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Sql_SUR := 'SELECT   weight_break_slab break_slab,RATE_DESCRIPTION  FROM temp_data_1  WHERE line_no > 0   AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
        v_Sql_SUR := 'SELECT  weight_break_slab break_slab,RATE_DESCRIPTION  FROM temp_data_1  WHERE  RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
      ELSE
        v_Sql_SUR := 'SELECT  weight_break_slab break_slab,RATE_DESCRIPTION FROM temp_data_1  WHERE line_no > 0  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
      END IF;
-- and added by subrahmanyam for the CR-219773
      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP

        FETCH v_Rc_C1
          INTO v_Break_Slab;

        EXIT WHEN v_Rc_C1%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                 AND rec_buyrateid=:v_rec_buy_rate_id AND get_flag=''N'' AND  rate_description=''A FREIGHT RATE'' ')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;

          v_Chargerate := v_Chargerate || v_Temp || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Chargerate := v_Chargerate || '0,';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
          /*
           Execute Immediate (' SELECT to_char(RATE_DESCRIPTION)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                  AND buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no'
                                  )

          */
          --@@Modified by Kameswari for the WPBN issue-127694
          EXECUTE IMMEDIATE (' SELECT to_char(RATE_DESCRIPTION)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                AND rec_buyrateid=:v_rec_buy_rate_id AND  get_flag=''N'' AND  rate_description=''A FREIGHT RATE'' ')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;
                  --  v_Ratedesc := v_Ratedesc || v_Temp4 || ',';
--                         v_Ratedesc := v_Ratedesc ||','||v_Temp4;

          --@@ WPBN issue-127694
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
  --                           v_Ratedesc := v_Ratedesc || ',-';
                           --v_Ratedesc := v_Ratedesc || '-'|| ',';
                           v_Temp4     :='-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;

        v_Ratedesc := v_Ratedesc || v_Temp4 || ',';
        --ADDED BY GOVIND FOR SURCHARGE CURRENCY--

        BEGIN
           EXECUTE IMMEDIATE (' SELECT SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                AND rec_buyrateid=:v_rec_buy_rate_id AND  get_flag=''N'' AND  rate_description=''A FREIGHT RATE'' ')
            INTO v_src_temp_curr
            USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id;

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
                    v_src_temp_curr     :='-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        v_src_curr := v_src_curr || v_src_temp_curr || ',';
        --- Govind end -------------
        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND  rate_description=''A FREIGHT RATE''
        AND rec_buyrateid=:v_rec_buy_rate_id AND weight_break_slaB=:V_BRAK_SLAB ')
        USING j.Buyrateid, j.Lane_No,j.Rec_Buyrate_Id,v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C1;

      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Chargerate := v_Chargerate || v_Base || ',';
      END IF;
      v_Base  := v_Break;
      v_Break := '';

      --        Close v_Rc_C1;
      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP
        --Dbms_Output.Put_Line(' Here..');
        FETCH v_Rc_C1
          INTO v_Break_Slab;

        EXIT WHEN v_Rc_C1%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C1;
--- Cursor For SurCharges

     OPEN v_Rc_C2 FOR v_Sql_SUR;
      LOOP

        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc;

        EXIT WHEN v_Rc_C2%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                 AND rec_buyrateid=:v_rec_buy_rate_id AND get_flag=''N'' AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id,v_rate_desc;

          v_Chargerate := v_Chargerate || v_Temp || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Chargerate := v_Chargerate || '0,';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
          /*
           Execute Immediate (' SELECT to_char(RATE_DESCRIPTION)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                  AND buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no'
                                  )

          */
          --@@Modified by Kameswari for the WPBN issue-127694
          EXECUTE IMMEDIATE (' SELECT to_char(RATE_DESCRIPTION)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                AND rec_buyrateid=:v_rec_buy_rate_id AND  get_flag=''N'' AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc ')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id,v_rate_desc;
         -- v_Ratedesc := v_Ratedesc || ',' || v_Temp4;
          --@@ WPBN issue-127694
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_rate_desc <> 'A FREIGHT RATE' THEN
          --    v_Ratedesc := v_Ratedesc || ',**';
          v_Temp4 :='**';
          ELSE
                    --  v_Ratedesc := v_Ratedesc || ',-';
                     v_Temp4 :='-';
            END IF;
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        v_Ratedesc := v_Ratedesc || v_Temp4 || ',';
        --ADDED BY GOVIND FOR SURCHARGE CURRENCY--

        BEGIN
           EXECUTE IMMEDIATE (' SELECT SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no
                                AND rec_buyrateid=:v_rec_buy_rate_id AND  get_flag=''N'' AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc ')
            INTO v_src_temp_curr
             USING v_Break_Slab, j.Buyrateid, j.Lane_No, j.Rec_Buyrate_Id,v_rate_desc;

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
                    v_src_temp_curr     :='-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
         v_src_curr := v_src_curr || v_src_temp_curr || ',';
        --- Govind end -------------



                EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND RATE_DESCRIPTION<>''A FREIGHT RATE'' and rate_description=:v_rate_desc
        AND rec_buyrateid=:v_rec_buy_rate_id AND weight_break_slaB=:V_BRAK_SLAB ')
        USING j.Buyrateid, j.Lane_No,v_rate_desc,j.Rec_Buyrate_Id,v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C2;

/*      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
        v_Chargerate := v_Chargerate || v_Base || ',';
      End If;
      v_Base  := v_Break;
      v_Break := '';*/

      --        Close v_Rc_C1;
      OPEN v_Rc_C2 FOR v_Sql_SUR;
      LOOP
        --Dbms_Output.Put_Line(' Here..');
        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc;

        EXIT WHEN v_Rc_C2%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C2;

--End Cursor For Surcharges
      IF (UPPER(p_Weight_Break) = 'SLAB' OR UPPER(p_Weight_Break) = 'FLAT') THEN
        v_Break := v_Base || v_Break;
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Break := v_Break || ',' || v_Base;
        v_Break := SUBSTR(v_Break, 2, LENGTH(v_Break));
      ELSE
        v_Break := SUBSTR(v_Break, 2, LENGTH(v_Break));
      END IF;
      /*Else
        Select Weight_Break_Slab, Chargerate
          Into v_Base, v_Temp
          From Temp_Data_1
         Where Line_No > 0
           And Buyrateid = j.Buyrateid
           And Lane_No = j.Lane_No
           And Rec_Buyrateid = j.Rec_Buyrate_Id;
        v_Break      := v_Break || ',' || v_Base;
        v_Chargerate := v_Chargerate || v_Temp || ',';
      End If;*/

      UPDATE BASE_DATA_1
         SET Weight_Break_Slab = v_Break,
             Chargerate        = SUBSTR(v_Chargerate,
                                        1,
                                        LENGTH(v_Chargerate) - 1),
             rate_description  = v_Ratedesc,
             sur_charge_currency = SUBSTR(v_src_curr,1,LENGTH(v_src_curr) - 1)
       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No
         AND Rec_Buyrate_Id = j.Rec_Buyrate_Id;

      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      END IF;
      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      END IF;
      UPDATE BASE_DATA_1
         SET Org_Countryid = v_Countryid, Dest_Countryid = v_Countryid1
       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No
         AND Rec_Buyrate_Id = j.Rec_Buyrate_Id;
    END LOOP;


     /* -- Added By Kishore For SurCharge Currency
        INSERT INTO TEMP_BASE_DATA_1 SELECT * FROM BASE_DATA_1; --testing

         FOR i IN ( SELECT b.rec_buyrate_id, b.buyrateid, b.version_no, b.lane_no, b.origin, b.destination ,b.weight_break_slab FROM  BASE_DATA_1 b)
             LOOP
                 FOR j IN( \*SELECT nvl(bd.sur_charge_currency,'-') sur_charge_currency
                            FROM QMS_BUYRATES_DTL BD, QMS_REC_CON_SELLRATESDTL SD
                            WHERE sd.rec_con_id = i.buyrateid
                                and bd.buyrateid = sd.buyrateid
                                and bd.line_no = sd.line_no
                                and bd.activeinactive is null
                                and bd.version_no = i.version_no*\

                           SELECT NVL(bd.sur_charge_currency,(SELECT DISTINCT CURRENCY FROM QMS_BUYRATES_MASTER BM WHERE BM.BUYRATEID = I.REC_BUYRATE_ID AND BM.VERSION_NO = I.VERSION_NO))  sur_charge_currency,bd.line_no,bd.weight_break_slab
                                   --bd.buyrateid,bd.weight_break_slab, bd.sur_charge_currency,bd.activeinactive,bd.lane_no, bd.version_no
                                  FROM QMS_BUYRATES_DTL BD
                                WHERE bd.buyrateid=i.rec_buyrate_id
                                     AND bd.version_no=i.version_no
                                     AND bd.origin=i.origin
                                     AND bd.destination=i.destination
                                     AND bd.lane_no=i.lane_no
                         )
                    LOOP
                        IF(j.line_no=0 AND j.weight_break_slab='MIN') THEN
                           v_schCurreny := '-,';
                        END IF;

                         v_schCurreny := v_schCurreny || j.sur_charge_currency||',';
                           --print_out('-----Kishore-----123'|| v_schCurreny || '-------' || j.sur_charge_currency);
                    END LOOP;
                           --print_out('-----Kishore-----'|| v_schCurreny);

                    UPDATE BASE_DATA_1 BDT
                        SET  SUR_CHARGE_CURRENCY = v_schCurreny
                    WHERE Buyrateid = i.Buyrateid
                    AND BDT.ORIGIN = I.ORIGIN
                    AND BDT.DESTINATION = I.DESTINATION
                    AND BDT.VERSION_NO= I.VERSION_NO
                    AND BDT.LANE_NO = I.LANE_NO;


                      v_schCurreny := '';

             END LOOP;

       --End Of Kishore For SurCharge Currency
*/
    OPEN p_Rs FOR
      SELECT *
        FROM (SELECT T1.*, ROWNUM Rn
                FROM (SELECT * FROM BASE_DATA_1) T1
               WHERE ROWNUM <= ((p_Page_No - 1) * p_Page_Rows) + p_Page_Rows)
       WHERE Rn > ((p_Page_No - 1) * p_Page_Rows);
    SELECT COUNT(*) INTO p_Tot_Rec FROM BASE_DATA_1;
    --Select Ceil((p_Tot_Rec / p_Page_Rows)) Into p_Tot_Pages From Dual;
    p_tot_pages := CEIL(p_tot_rec / p_page_rows);
    /*EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
       p_tot_rec := 0;
       p_tot_pages := 0;
    WHEN OTHERS
    THEN
       DBMS_OUTPUT.put_line (SQLERRM);
       qms_rsr_rates_pkg.g_err := '<< ' || SQLERRM || ' >>';
       qms_rsr_rates_pkg.g_err_code := '<< ' || SQLCODE || ' >>';
       INSERT INTO qms_objects_errors
                   (ex_date, module_name,
                    errorcode, errormessage
                   )
            VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->Sell_buy_proc',
                    qms_rsr_rates_pkg.g_err_code, qms_rsr_rates_pkg.g_err
                   );

       COMMIT;*/
  END;

  /*  Using this Procedure for Add and getting the Buy rates information from QMS_BUYRATES_MASTER,
   *  QMS_BUYRATES_DTL tables according to accesslevel and passing the in parameters are
   *  p_org_locs             VARCHAR2;
   *  p_dest_locs            VARCHAR2;
   *  p_terminal             VARCHAR2;
   *  p_acclevel             VARCHAR2;
   *  p_rate_type            VARCHAR2;
   *  p_weight_break         VARCHAR2;
   *  p_srvlevl              VARCHAR2;
   *  p_carrier              VARCHAR2;
   *  p_currency             VARCHAR2;
   *  p_shmode               VARCHAR2;
   *  p_qry                  VARCHAR2;
   *  p_page_no              NUMBER ;
   *  p_page_rows            NUMBER ;
   *  and out parameters are
   *   p_tot_rec              NUMBER,
   *   p_tot_pages            NUMBER,
   *   p_rs                   resultset
  */
  PROCEDURE Comman_Proc(p_Operation    VARCHAR2,
                        p_Org_Locs     VARCHAR2,
                        p_Dest_Locs    VARCHAR2,
                        p_Terminal     VARCHAR2,
                        p_Acclevel     VARCHAR2,
                        p_Rate_Type    VARCHAR2,
                        p_Weight_Break VARCHAR2,
                        p_Srvlevl      VARCHAR2,
                        p_Carrier      VARCHAR2,
                        p_Currency     VARCHAR2,
                        p_Shmode       VARCHAR2,
                        p_Qry          VARCHAR2,
                        p_Page_No      NUMBER DEFAULT 1,
                        p_Page_Rows    NUMBER DEFAULT 50,
      			p_org_countries VARCHAR2,
     			p_dest_countries VARCHAR2,
			p_org_regions VARCHAR2,
       			p_dest_regions VARCHAR2,
                        p_Tot_Rec      OUT NUMBER,
                        p_Tot_Pages    OUT NUMBER,
                        p_Rs           OUT Resultset) AS
    v_Org_Locs     VARCHAR2(32767);
    v_Dest_Locs    VARCHAR2(32767);
    v_Terminal     VARCHAR2(32767);
    v_Acclevel     VARCHAR2(300);
    v_Rate_Type    VARCHAR2(300);
    v_Weight_Break VARCHAR2(300);
    v_Srvlevl      VARCHAR2(32767);
    v_srvlevl_id   VARCHAR2(32767);
    v_Carrier      VARCHAR2(32767);
    v_Currency     VARCHAR2(300);
    v_Shmode       VARCHAR2(300);
    v_Operation    VARCHAR2(300);
    v_Type         VARCHAR2(1);
    v_Terminals    VARCHAR2(32767);
    v_Chargerate   VARCHAR2(400) := '';
    k              NUMBER := 0;
    v_Sqlcnt       VARCHAR2(2000);
    v_Sqlnew       VARCHAR2(2000);
    v_Sql1         VARCHAR(2000);
    v_Sql_sur         VARCHAR(2000);
    v_Sql2         VARCHAR(2000);
    v_Sql3         VARCHAR(32767);
    v_Sql4         VARCHAR(32767);
    v_Sql5         VARCHAR2(2000);
    v_Sql6         VARCHAR2(32767);
    v_Sql7         VARCHAR2(2000);
    v_Sql8         VARCHAR2(2000);
    v_sq20         VARCHAR2(32767);
    v_Base         VARCHAR2(30);
    v_Break        VARCHAR2(4000);
    v_Temp         VARCHAR2(200);
    v_Countryid    VARCHAR2(30);
    v_Countryid1   VARCHAR2(30);
    v_Temp1        VARCHAR2(10);
    v_Temp2        VARCHAR2(10);
    v_Temp3        VARCHAR2(10);
    v_Temp4        VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancements, --Modified by subrahmanyam by for the RSR-219973 CR
    v_Ubound       VARCHAR2(32767);
    v_Lbound       VARCHAR2(32767);
    v_Indicator    VARCHAR2(100);
    -- v_Ratedesc     Varchar2(300); --@@Added by Kameswari for Surcharge Enhancements
    v_Ratedesc   VARCHAR2(32767);
    v_Rate       VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancements
    v_Qry        VARCHAR2(100);
    v_Rc_C1      Resultset;
    v_Rc_C2      Resultset;
    v_Break_Slab VARCHAR2(20);
    v_lineno     VARCHAR2(20); --@@Added by Kameswari for Surcharge Enhancements
    v_No_Rows    NUMBER;
     v_org_reg      VARCHAR2(1000):= 'T'; --@@ added by phani sekhar for wpbn 179350
    v_dest_reg     VARCHAR2(1000):= 'T'; --@@ added by phani sekhar for wpbn 179350
    v_rate_desc    VARCHAR2(100);
    v_temp_count   NUMBER;
    v_temp_Break   VARCHAR2(1000);
    v_temp_rate    VARCHAR2(1000);
    v_temp_desc   VARCHAR2(1000);
    -- Added by Kishore for SurCharge Currency
     v_SchCurrency VARCHAR2(32767);
     v_sch_currency VARCHAR2(100);
     v_Sch          VARCHAR2(100);
     v_temp_curr   VARCHAR2(1000);
     v_Temp5        VARCHAR2(100);
    --v_opr_adm_flag   Varchar2(20);
    CURSOR C1 IS(
      SELECT Parent_Terminal_Id Termid
        FROM FS_FR_TERMINAL_REGN
       WHERE Child_Terminal_Id = p_Terminal
      UNION
      SELECT Terminalid Termid
        FROM FS_FR_TERMINALMASTER
       WHERE Oper_Admin_Flag = 'H'
      UNION
      SELECT p_Terminal Termid FROM Dual);
    CURSOR C2 IS(
      SELECT p_Terminal Termid
        FROM Dual
      UNION
      SELECT Child_Terminal_Id Termid
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
       START WITH Parent_Terminal_Id = p_Terminal
      UNION
      SELECT p_Terminal Termid FROM Dual);
    /*Cursor C3 Is(
    Select Terminalid Termid
                     From Fs_Fr_Terminalmaster
                     Where Actv_Flag = 'A'
                       And (Invalidate = 'F' Or Invalidate Is Null));*/
  BEGIN

    /*SELECT oper_admin_flag
     INTO v_opr_adm_flag
     FROM FS_FR_TERMINALMASTER
    WHERE terminalid = p_terminal;*/
    IF p_Org_Locs IS NOT NULL THEN
     /* v_Org_Locs := ' AND qbd.origin IN (' || '' ||
                    Qms_Rsr_Rates_Pkg.Seperator(p_Org_Locs) || '' || ')'; */
	v_org_reg :=' AND qbd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';
    END IF;
    --added by phani sekhar for wpbn 179350
        IF (  p_org_countries IS NOT NULL  AND LENGTH ((v_org_reg )) <=1)  THEN

    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
   v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;
   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_org_countries) || '' || ') )';
    END IF;

  IF (  p_org_regions IS NOT NULL  AND LENGTH (TRIM (v_org_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND qbd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_org_reg  :=  ' AND qbd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_org_regions) || '' || ') )';

    END IF;
    --ends 179350
    IF p_Dest_Locs IS NOT NULL THEN
      v_Dest_Locs := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.Seperator(p_Dest_Locs) || '' || ') ';
	v_dest_reg := '  AND qbd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';
    END IF;

  IF (  p_dest_countries IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg := ' AND qbd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_dest_reg :=  ' AND qbd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_dest_reg := v_dest_reg || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_dest_countries) || '' || ') )';

    END IF;

 IF (  p_dest_regions IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg  := ' AND qbd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_dest_reg :=  ' AND qbd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_dest_reg := v_dest_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_dest_regions) || '' || ') )';

    END IF;

    IF p_Srvlevl IS NOT NULL THEN
      v_Srvlevl := '  AND qbd.SERVICE_LEVEL  IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Srvlevl) || '' ||
                   ',''SCH'' ' || ')';
    END IF;
    ----@@Added by Kameswari issuid:127694
    IF p_srvlevl IS NOT NULL THEN
      v_srvlevl_id := '  AND qbd.service_level IN (' || '' ||
                      Qms_Rsr_Rates_Pkg.seperator(p_srvlevl) || '' || ')';
    END IF;
    --@Added End

    IF p_Carrier IS NOT NULL THEN
      v_Carrier := '   AND qbd.carrier_id IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Carrier) || '' || ' ) ';
    END IF;

    v_Terminal := '''' || p_Terminal || '''';

    v_Acclevel     := '''' || p_Acclevel || '''';
    v_Rate_Type    := '''' || p_Rate_Type || '''';
    v_Weight_Break := '''' || p_Weight_Break || '''';
    v_Currency     := '''' || p_Currency || '''';
    v_Shmode       := '''' || p_Shmode || '''';
    IF UPPER(Trim(p_Qry)) <> 'IS NULL' THEN
      v_Qry := '=' || '''' || p_Qry || '''';
    ELSE
      v_Qry := p_Qry;
    END IF;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data_1');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_1');
    /*If Upper(v_opr_adm_flag)<>'H'
    Then*/
    IF UPPER(p_Operation) = 'ADD' THEN
      FOR i IN C1 LOOP

        v_Terminals := v_Terminals || '''' || i.Termid || '''' || ',';
      END LOOP;
    ELSIF UPPER(p_Operation) = 'MODIFY' THEN
      FOR i IN C2 LOOP
        v_Terminals := v_Terminals || '''' || i.Termid || '''' || ',';
      END LOOP;
    END IF;
    /*Else
    For i In C3 Loop

        v_Terminals := v_Terminals || '''' || i.Termid || '''' || ',';
      End Loop;
    End If;*/
    v_Terminals := SUBSTR(v_Terminals, 1, LENGTH(v_Terminals) - 1);

    ----@@Added by Kameswari issueid:127694
   /* v_sq20 := v_Org_Locs || v_Dest_Locs || v_srvlevl_id; commented by phani sekhar for wpbn 179350 */
    --@@added by phani 179350
	IF v_org_reg <>'T' THEN
	v_sq20 := v_sq20 || v_org_reg;
	END IF;
	IF v_dest_reg <>'T' THEN
	v_sq20 := v_sq20 || v_dest_reg;
	END IF;

	v_sq20 := v_sq20 || v_srvlevl_id;

    --Added End
    IF UPPER(p_Weight_Break) = 'FLAT' THEN
      v_Break := 'Min,Flat,';
    END IF;

    /*v_Sqlcnt := ' AND (qbd.buyrateid, qbd.lane_no) in (SELECT buyrateid, lane_no FROM (SELECT t1.*, ROWNUM rn FROM (SELECT DISTINCT qbd.buyrateid, qbd.lane_no ';*/
    --@@Modified for the WPBN issue-146448 on 23/12/08
   v_Sqlcnt := ' AND (qbd.buyrateid, qbd.lane_no,qbd.version_no) in (SELECT buyrateid, lane_no,version_no FROM (SELECT t1.*, ROWNUM rn FROM (SELECT DISTINCT qbd.buyrateid, qbd.lane_no ,qbd.version_no';
    --v_Sql1   := ' insert into temp_data_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO, LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION) SELECT qbd.buyrateid,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,QBD.RATE_DESCRIPTION ';
   --@@Modified for the WPBN issue-146448 on 23/12/08
    -- Modified by Kishore for SurCharge Currency
   v_Sql1   := ' insert into temp_data_1(BUYRATEID,VERSION_NO,WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO, LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION,SUR_CHARGE_CURRENCY) SELECT qbd.buyrateid,qbd.version_no,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,QBD.RATE_DESCRIPTION, NVL(qbd.sur_charge_currency,qbm.currency) sur_charge_currency';
   -- v_Sql7   := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid ';
     --@@Modified for the WPBN issue-146448 on 23/12/08
    v_Sql7   := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid and qbd.version_no=qbm.version_no and (qbm.lane_no=qbd.lane_no or qbm.lane_no is null)';
   /* v_Sql6   := v_Org_Locs || v_Dest_Locs || v_Srvlevl; commented by phani sekhar for wpbn 179350*/
	 --@@added by phani 179350
	IF v_org_reg <>'T' THEN
	v_Sql6 := v_Sql6 || v_org_reg;
	END IF;
	IF v_dest_reg <>'T' THEN
	v_Sql6 := v_Sql6 || v_dest_reg;
	END IF;
	v_Sql6 := v_Sql6 || v_Srvlevl;

    v_Sql4   := '  AND qbd.activeinactive IS NULL AND qbd.INVALIDATE IS NULL AND qbd.ACC_FLAG IS NULL' ||
                '  AND qbd.generated_flag ' || v_Qry
               /* || '  AND NOT EXISTS (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL SD, QMS_REC_CON_SELLRATESMASTER SM WHERE SD.BUYRATEID=qbd.buyrateid AND SD.LANE_NO=QBD.LANE_NO AND SM.REC_CON_ID=SD.REC_CON_ID AND SM.TERMINALID='||v_terminal||' AND SD.AI_FLAG=''A'' ) ';*/
                ||
                '  AND NOT EXISTS (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL SD, QMS_REC_CON_SELLRATESMASTER SM WHERE SD.BUYRATEID=qbd.buyrateid AND SD.VERSION_NO=QBD.VERSION_NO AND SD.LANE_NO=QBD.LANE_NO AND SM.REC_CON_ID=SD.REC_CON_ID AND SM.TERMINALID IN(' ||
                v_Terminals ||
                ') AND SD.AI_FLAG=''A''  AND SD.invalidate=''F'' And sm.Rc_Flag = ''R'') ';
    v_Sql2   := ' AND qbm.shipment_mode =' || v_Shmode || --@@Modified by Kameswari for Surcharge Enhancement
                '  AND qbm.weight_break =' || v_Weight_Break ||
                ' And qbm.currency =' || v_Currency;
    --' And qbm.currency =' || v_Currency;
    v_Sql3   := '  AND qbm.rate_type =' || v_Rate_Type || v_Carrier ||
                '   AND qbm.terminalid IN (' || v_Terminals || ')';
    /*v_Sql5   := ' insert into base_data_1(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION) SELECT buyrateid,carrier_id, origin,destination,service_level, weight_break_slab, transit_time, frequency,chargerate,lane_no,org_countryid, Dest_countryid,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION ' ||
                '  FROM (SELECT t1.*, ROWNUM rn FROM (';
                v_Sqlnew := ' select  distinct qbd.buyrateid buyrateid, qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ' ||
                ' ''a''  chargerate, qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.CURRENCY,qbd.DENSITY_CODE,qbd.NOTES,qbd.RATE_DESCRIPTION '; --FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  '
*/
    --@@Modified for the WPBN issue-146448 on 23/12/08
    v_Sql5   := ' insert into base_data_1(BUYRATEID,VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION,EXTERNAL_NOTES) SELECT buyrateid,version_no,carrier_id, origin,destination,service_level, weight_break_slab, transit_time, frequency,chargerate,lane_no,org_countryid, Dest_countryid,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION,EXTERNAL_NOTES ' ||
                '  FROM (SELECT t1.*, ROWNUM rn FROM (';
    v_Sqlnew := ' select  distinct qbd.buyrateid buyrateid, qbd.version_no,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ' ||
                ' ''a''  chargerate, qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.CURRENCY,qbd.DENSITY_CODE,qbd.NOTES,qbd.RATE_DESCRIPTION,qbd.EXTERNAL_NOTES'; --FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  '--Added by Rakesh
    --v_Sql8   := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm, qms_rec_con_sellratesm WHERE qbd.buyrateid = qbm.buyrateid ';

--@@Modified for the WPBN issue-146448 on 23/12/08
v_Sql8   := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm, qms_rec_con_sellratesm WHERE qbd.buyrateid = qbm.buyrateid and qbd.version_no=qbm.version_no';
    --@@Modified by Kameswari for the WPBN issue-127694

    /* Execute Immediate (v_Sql1 || v_Sql7 || v_Sql6 || v_Sql4 || v_Sql2 ||
                          v_Sql3 || v_Sqlcnt || v_Sql7 || v_Sql6 || v_Sql4 ||
                          v_Sql2 || v_Sql3 || ' ORDER BY buyrateid, lane_no' ||
                          ') t1
                          WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                          WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ')
          Using p_Page_No, p_Page_Rows, p_page_rows, p_Page_No, p_Page_Rows;
    */
    --@@WPBN issue-127694
PRINT_OUT(v_Sql1 || v_Sql7 || v_Sql6 || v_Sql4 || v_Sql2 ||
                      v_Sql3 || v_Sqlcnt || v_Sql7 || v_sq20 || v_Sql4 ||
                      v_Sql2 || v_Sql3 || ' ORDER BY buyrateid, lane_no' ||
                      ') t1
                      WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                      WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ');
    EXECUTE IMMEDIATE (v_Sql1 || v_Sql7 || v_Sql6 || v_Sql4 || v_Sql2 ||
                      v_Sql3 || v_Sqlcnt || v_Sql7 || v_sq20 || v_Sql4 ||
                      v_Sql2 || v_Sql3 || ' ORDER BY buyrateid, lane_no' ||
                      ') t1
                      WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                      WHERE rn > ((:v_page_no - 1) * :v_page_rows)) ')
      USING p_Page_No, p_Page_Rows, p_page_rows, p_Page_No, p_Page_Rows;
PRINT_OUT('------------------');
PRINT_OUT(v_Sql5 || v_Sqlnew || v_Sql7 || v_Sql6 || v_Sql4 ||
                      v_Sql2 || v_Sql3 ||
                      'And (qbd.rate_description = ''A FREIGHT RATE'' or qbd.rate_description IS NULL)' ||
                      ' ORDER BY buyrateid, lane_no' ||
                      ') t1
                      WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                      WHERE rn > ((:v_page_no - 1) * :v_page_rows) ');

    EXECUTE IMMEDIATE (v_Sql5 || v_Sqlnew || v_Sql7 || v_Sql6 || v_Sql4 ||
                      v_Sql2 || v_Sql3 ||
                      'And (qbd.rate_description = ''A FREIGHT RATE'' or qbd.rate_description IS NULL)' ||
                      ' ORDER BY buyrateid, lane_no' ||
                      ') t1
                      WHERE ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
                      WHERE rn > ((:v_page_no - 1) * :v_page_rows) ')
      USING p_Page_No, p_Page_Rows, p_page_rows, p_Page_No, p_Page_Rows;
    COMMIT;

    FOR j IN (SELECT Buyrateid,
                     version_no,    --@@Added  by Kameswari for the WPBN issue-146448 on 23/12/08
                     Weight_Break_Slab,
                     Chargerate,
                     Lane_No,
                     Origin,
                     Destination,
                     Rec_buyrate_id
                FROM BASE_DATA_1
               ORDER BY Buyrateid) LOOP
      v_Chargerate := '';
      k            := 1;
      v_temp_count :=0;
      v_Break := '';
        v_Ratedesc := '';
        v_temp_desc:='';
        -- Modified by Kishore for SurCharge Currency
        v_SchCurrency:='';
        v_temp_curr:='';
      IF (UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1) = FALSE THEN
      BEGIN
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION ,sur_charge_currency from temp_data_1 where  buyrateid =:v_buy_rate_id
                           AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND line_no=0 AND WEIGHT_BREAK_SLAB=''BASIC''')
          INTO v_temp_Break, v_temp_rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4,v_temp_curr
          USING j.Buyrateid, j.Lane_No,j.version_no;
         v_Lbound    := v_Temp1 || ',';
        v_Ubound    := v_Temp2 || ',';
        v_Indicator := v_Temp3 || ',';
        v_Break := v_temp_Break || ',';
        v_Chargerate := v_temp_rate || ',';
        v_temp_desc :=  v_Temp4 ||',';
        v_temp_curr:= v_temp_curr || ',';-- Modified by Kishore for SurCharge Currency

        EXCEPTION WHEN NO_DATA_FOUND THEN
        v_temp_count   :=v_temp_count+1;
        v_Lbound    := '0' || ',';
        v_Ubound    := '0' || ',';
        v_Indicator := '-' || ',';
        v_Break := 'BASIC' || ',';
        v_Chargerate := '-' || ',';
        v_temp_desc :=  'A FREIGHT RATE' ||',';
        v_temp_curr:= '-,';-- Modified by Kishore for SurCharge Currency

         EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION,SUR_CHARGE_CURRENCY from temp_data_1 where  buyrateid =:v_buy_rate_id
                           AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND line_no>=0 AND WEIGHT_BREAK_SLAB=''MIN''')
          INTO v_temp_Break, v_temp_rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4,v_Temp5
          USING j.Buyrateid, j.Lane_No,j.version_no;
        v_Lbound    := v_Lbound || v_Temp1 || ',';
        v_Ubound    := v_Ubound ||v_Temp2 || ',';
        v_Indicator := v_Indicator || v_Temp3 || ',';
         v_Break := v_Break || v_temp_Break ;
         v_Chargerate := v_Chargerate || v_temp_rate ;
          v_Temp4 :=  v_temp_desc ||v_Temp4;
          v_temp_curr := v_temp_curr||v_Temp5; -- Modified by Kishore for SurCharge Currency

        END;
 /*       v_Lbound    := v_Temp1 || ',';
        v_Ubound    := v_Temp2 || ',';
        v_Indicator := v_Temp3 || ',';
*/        IF v_temp_count = 0 THEN
        v_Temp5 := v_temp_curr;
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION ,SUR_CHARGE_CURRENCY from temp_data_1 where  buyrateid =:v_buy_rate_id
                           AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND line_no=1 AND upper(WEIGHT_BREAK_SLAB)=''MIN''')
          INTO v_temp_Break, v_temp_rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4,v_temp_curr
          USING j.Buyrateid, j.Lane_No,j.version_no;
          v_Break := v_Break||v_temp_Break ;
          v_Chargerate := v_Chargerate||v_temp_rate ;
         v_Lbound    :=  v_Lbound || v_Temp1 || ',';
        v_Ubound    := v_Ubound || v_Temp2 || ',';
        v_Indicator := v_Indicator || v_Temp3 || ',';
        v_Temp4     := v_temp_desc || v_Temp4;
        v_Temp5 :=  v_Temp5 ||v_temp_curr;  -- Modified by Kishore for SurCharge Currency
        END IF;
        -- v_Ratedesc  := v_Temp4 || ',';
      ELSE
        v_Temp1     := '';
        v_Temp2     := '';
        v_Temp3     := '';
        v_Lbound    := '';
        v_Ubound    := '';
        v_Indicator := '';
        v_Ratedesc  := '';
        -- Modified by Kishore for SurCharge Currency
        v_SchCurrency :='';
        v_Temp5       := '';
      END IF;

      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Base       := v_Chargerate;
        v_Chargerate := '';
        v_Rate       := v_Temp4;
        v_Temp4      := '';
        v_Sch        := v_Temp5;
        v_Temp5      := '';    -- Modified by Kishore for SurCharge Currency
      ELSIF UPPER(p_Weight_Break) = 'FLAT' OR
            UPPER(p_Weight_Break) = 'SLAB' THEN
        v_Chargerate := v_Chargerate || ',';
        v_Ratedesc   := v_Temp4 || ',';
        IF v_temp_count = 0 THEN
        v_SchCurrency := v_Temp5 || ',';  -- Modified by Kishore for SurCharge Currency
        ELSE
         v_SchCurrency := v_temp_curr || ',';--v_Temp5 || ',';  -- Modified by Kishore for SurCharge Currency
        END IF;

      END IF;
--Commented and added by subrahmanyam for the CR 219973

/*      --If (Upper(p_Weight_Break) <> 'FLAT') Then
      --@@Modified by Kameswari for Surcharge Enhancements
      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
--Commented and added by subrahmanyam for the CR 219973
--        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0   ORDER BY weight_break_slab  ';
        v_Sql1 := 'SELECT   weight_break_slab break_slab,RATE_DESCRIPTION  FROM temp_data_1  WHERE line_no > 0   ORDER BY line_no  ';
      Elsif Upper(p_Weight_Break) = 'LIST' And p_Shmode <> 1 Then
--Commented and added by subrahmanyam for the CR 219973
       -- v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  ORDER BY weight_break_slab  ';
        v_Sql1 := 'SELECT   weight_break_slab break_slab ,RATE_DESCRIPTION  FROM temp_data_1   ORDER BY line_no  ';
      Else
--Commented and added by subrahmanyam for the CR 219973
--        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  order by  weight_break_slab';
       v_Sql1 := 'SELECT   weight_break_slab break_slab ,RATE_DESCRIPTION FROM temp_data_1  WHERE line_no > 0   ORDER BY line_no  ';
      End If;*/

            IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
                    v_Sql1 := 'SELECT   DISTINCT weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0 AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY break_slab  ';
            ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
                   v_Sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1   WHERE RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY break_slab  ';
            ELSE
                    v_Sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0  AND weight_break_slab NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION=''A FREIGHT RATE'' ORDER BY break_slab  ';
            END IF;

           IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
                    v_Sql_sur := 'SELECT   weight_break_slab break_slab,RATE_DESCRIPTION  FROM temp_data_1  WHERE line_no > 0  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
            ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
                   v_Sql_sur := 'SELECT   weight_break_slab break_slab ,RATE_DESCRIPTION  FROM temp_data_1  WHERE  RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
            ELSE
                    v_Sql_sur := 'SELECT   weight_break_slab break_slab ,RATE_DESCRIPTION FROM temp_data_1  WHERE line_no > 0  AND RATE_DESCRIPTION<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
            END IF;

      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP

        FETCH v_Rc_C1
          INTO v_Break_Slab; --@@Modified by Kameswari for Surcharge Enhancements
        EXIT WHEN v_Rc_C1%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id  AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
            INTO v_Temp1
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp1 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_Temp2
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp2 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_Temp3
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;

          IF v_Temp3 IS NULL THEN
            v_Temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp3 := '-';
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
     --   PRINT_OUT('v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;...'||v_Break_Slab||' , '|| j.Buyrateid ||' , '|| j.Lane_No ||' , '|| j.version_no);
       -- PRINT_OUT(' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab ='||''''||v_Break_Slab||''''||
         --                      ' AND buyrateid ='||j.Buyrateid||' and LANE_NO='||j.Lane_No||' AND version_no='||j.version_no);
          EXECUTE IMMEDIATE (' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
                  v_Temp4 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        -- Added by Kishore for SurCharge Currency
     Begin
          Execute Immediate (' SELECT  SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND version_no=:v_versionno and GET_FLAG=''N'' AND RATE_DESCRIPTION=''A FREIGHT RATE''')
         /*   Execute Immediate ('SELECT nvl(dt.sur_charge_currency,bm.currency)
  FROM temp_data_1 t1,
       qms_buyrates_dtl dt,
       qms_buyrates_master bm
 WHERE  t1.rec_buyrateid = bm.buyrateid
       and t1.rec_buyrateid = dt.buyrateid
       and dt.version_no =: v_versionno
       and dt.lane_no =:v_lane_no
       and dt.activeinactive is null
       and t1.weight_break_slab =: v_break_slab
       and dt.weight_break_slab =: v_break_slab
   AND t1.buyrateid =: v_buy_rate_id
   and t1.LANE_NO =: v_lane_no
   AND t1.version_no =: v_versionno
   and t1.GET_FLAG = ''N'' ')*/



            Into v_Temp5
            Using v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        Exception
          When No_Data_Found Then
            v_Temp5 := '-';
          When Others Then
            Dbms_Output.Put_Line(Sqlerrm);
        End;
  --      v_Temp5 := '-';
        -- End of Kishore for SurCharge Currency

       EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND version_no=:v_versionno and rate_description=:v_rate_desc
        AND weight_break_slaB=:V_BRAK_SLAB')
        USING j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc,v_Break_Slab;

        v_Chargerate := v_Chargerate || v_Temp || ',';
        v_Lbound     := v_Lbound || v_Temp1 || ',';
        v_Ubound     := v_Ubound || v_Temp2 || ',';
        v_Indicator  := v_Indicator || v_Temp3 || ',';
        v_Ratedesc   := v_Ratedesc || v_Temp4 || ','; --@@Added by Kameswari for Surcharge Enhancements
        v_SchCurrency := v_SchCurrency || v_Temp5 || ',';   -- Modified of Kishore for SurCharge Currency

      END LOOP;
      --Close v_Rc_C1;
      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        --v_Chargerate := v_Chargerate || v_Base || ',';
        --v_Ratedesc   :=v_Ratedesc    ||v_Rate||',';
        v_Chargerate := v_Base || ',' || v_Chargerate;
        v_Ratedesc   := v_Rate || ',' || v_Ratedesc;
         v_SchCurrency := v_Sch|| ','|| v_SchCurrency ;  -- Modified of Kishore for SurCharge Currency

      END IF;
      v_Base  := v_Break;
      v_Break := '';
     CLOSE v_Rc_C1;

     ---Cursor for Surcharges
     OPEN v_Rc_C2 FOR v_Sql_sur;
      LOOP

        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc; --@@Modified by Kameswari for Surcharge Enhancements
        EXIT WHEN v_Rc_C2%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_ratedesc')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id  AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_ratedesc')
            INTO v_Temp1
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp1 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_ratedesc')
            INTO v_Temp2
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp2 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_ratedesc')
            INTO v_Temp3
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;

          IF v_Temp3 IS NULL THEN
            v_Temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp3 := '-';
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
    --    PRINT_OUT('v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;...'||v_Break_Slab||' , '|| j.Buyrateid ||' , '|| j.Lane_No ||' , '|| j.version_no);
      --  PRINT_OUT(' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab ='||''''||v_Break_Slab||''''||
        --                       ' AND buyrateid ='||j.Buyrateid||' and LANE_NO='||j.Lane_No||' AND version_no='||j.version_no);
          EXECUTE IMMEDIATE (' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_rate_desc')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_rate_desc <> 'A FREIGHT RATE' THEN
                  v_Temp4 := '**';
             ELSE
                  v_Temp4 := '-';
            END IF;
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;

         -- Modified  of Kishore for SurCharge Currency
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  SUR_CHARGE_CURRENCY  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND GET_FLAG=''N'' AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' AND RATE_DESCRIPTION=:v_ratedesc')
            INTO v_Temp5
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp5 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
         -- End of Kishore for SurCharge Currency

       EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND version_no=:v_versionno AND RATE_DESCRIPTION<> ''A FREIGHT RATE'' and rate_description=:v_rate_desc
        AND weight_break_slaB=:V_BRAK_SLAB')
        USING j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc,v_Break_Slab;

        v_Chargerate := v_Chargerate || v_Temp || ',';
        v_Lbound     := v_Lbound || v_Temp1 || ',';
        v_Ubound     := v_Ubound || v_Temp2 || ',';
        v_Indicator  := v_Indicator || v_Temp3 || ',';
        v_Ratedesc   := v_Ratedesc || v_Temp4 || ','; --@@Added by Kameswari for Surcharge Enhancements
        v_SchCurrency:= v_SchCurrency || v_Temp5 || ','; -- Added by Kishore for SurCharge Currency

      END LOOP;
/*      --Close v_Rc_C1;
      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
        --v_Chargerate := v_Chargerate || v_Base || ',';
        --v_Ratedesc   :=v_Ratedesc    ||v_Rate||',';
        v_Chargerate := v_Base || ',' || v_Chargerate;
        v_Ratedesc   := v_Rate || ',' || v_Ratedesc;
      End If;
      v_Base  := v_Break;
      v_Break := '';*/
     CLOSE v_Rc_C2;
     --end Cursor for SurCharges
      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP
        FETCH v_Rc_C1
          INTO v_Break_Slab;
        EXIT WHEN v_Rc_C1%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C1;

       OPEN v_Rc_C2 FOR v_Sql_sur;
      LOOP
        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc;
        EXIT WHEN v_Rc_C2%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C2;
      IF (UPPER(p_Weight_Break) = 'SLAB' OR UPPER(p_Weight_Break) = 'FLAT') THEN
        v_Break := v_Base || v_Break;
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        --v_Break := v_Break || ',' || v_Base;
        v_Break := v_Base || v_Break;
        --v_Break := Substr(v_Break, 2, Length(v_Break));
      ELSE
        v_Break := SUBSTR(v_Break, 2, LENGTH(v_Break));
      END IF;
      /* Else
        Select Weight_Break_Slab, Chargerate
          Into v_Base, v_Temp
          From Temp_Data_1
         Where Line_No > 0
           And Buyrateid = j.Buyrateid
           And Lane_No = j.Lane_No;
        v_Break      := v_Break || ',' || v_Base;
        v_Chargerate := v_Chargerate || v_Temp || ',';
      End If;*/
      UPDATE BASE_DATA_1
         SET Weight_Break_Slab = v_Break,
             Chargerate        = SUBSTR(v_Chargerate,
                                        1,
                                        LENGTH(v_Chargerate) - 1),
             Ubound            = SUBSTR(v_Ubound, 1, LENGTH(v_Ubound) - 1),
             Lbound            = SUBSTR(v_Lbound, 1, LENGTH(v_Lbound) - 1),
             c_Indicator       = SUBSTR(v_Indicator,
                                        1,
                                        LENGTH(v_Indicator) - 1),
             rate_description  = v_Ratedesc,
             SUR_CHARGE_CURRENCY = SUBSTR(v_SchCurrency,1,LENGTH(v_SchCurrency)-1) --Added By Kishore Podili For SCH Currency

       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No;
      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      END IF;
      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      END IF;
      UPDATE BASE_DATA_1
         SET Org_Countryid = v_Countryid, Dest_Countryid = v_Countryid1
       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No;
    END LOOP;
    /*OPEN p_rs
    FOR
       SELECT *
         FROM (SELECT t1.*, ROWNUM rn
                 FROM (SELECT *
                         FROM base_data_1) t1
                WHERE ROWNUM <=
                              ((p_page_no - 1) * p_page_rows) + p_page_rows)
        WHERE rn > ((p_page_no - 1) * p_page_rows);*/
    OPEN p_Rs FOR
      SELECT * FROM BASE_DATA_1;
    /*SELECT COUNT (*)
    INTO p_tot_rec
    FROM base_data_1;*/

    EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM ( SELECT t1.*, ROWNUM rn FROM ( ' ||
                      v_Sqlnew || v_Sql7 || v_Sql6 || v_Sql4 || v_Sql2 ||
                      v_Sql3 ||
                      ' And (qbd.rate_description=''A FREIGHT RATE'' or qbd.rate_description  IS NULL)) t1) ')
      INTO p_Tot_Rec;
    --Select Ceil((p_Tot_Rec / p_Page_Rows)) Into p_Tot_Pages From Dual;
    p_tot_pages := CEIL(p_tot_rec / p_page_rows);
    /*EXCEPTION
    WHEN NO_DATA_FOUND
    THEN
       p_tot_rec := 0;
       p_tot_pages := 0;
    WHEN OTHERS
    THEN
       qms_rsr_rates_pkg.g_err := '<< ' || SQLERRM || ' >>';
       qms_rsr_rates_pkg.g_err_code := '<< ' || SQLCODE || ' >>';
       INSERT INTO qms_objects_errors
                   (ex_date, module_name,
                    errorcode, errormessage
                   )
            VALUES (SYSDATE, 'Qms_Rsr_Rates_Pkg->comman_proc',
                    qms_rsr_rates_pkg.g_err_code, qms_rsr_rates_pkg.g_err
                   );
       COMMIT;*/
  END;

  /*  Using this Procedure for Modify and getting the Buy rates information from QMS_BUYRATES_MASTER,
   *  QMS_BUYRATES_DTL tables according to accesslevel and passing the in parameters are
   *  p_org_locs             VARCHAR2;
   *  p_dest_locs            VARCHAR2;
   *  p_terminal             VARCHAR2;
   *  p_acclevel             VARCHAR2;
   *  p_rate_type            VARCHAR2;
   *  p_weight_break         VARCHAR2;
   *  p_srvlevl              VARCHAR2;
   *  p_carrier              VARCHAR2;
   *  p_currency             VARCHAR2;
   *  p_shmode               VARCHAR2;
   *  p_qry                  VARCHAR2;
   *  p_page_no              NUMBER ;
   *  p_page_rows            NUMBER ;
   *  and out parameters are
   *   p_tot_rec              NUMBER,
   *   p_tot_pages            NUMBER,
   *   p_rs                   resultset
  */
  PROCEDURE Comman_Proc_Modify(p_Operation    VARCHAR2,
                               p_Org_Locs     VARCHAR2,
                               p_Dest_Locs    VARCHAR2,
                               p_Terminal     VARCHAR2,
                               p_Acclevel     VARCHAR2,
                               p_Rate_Type    VARCHAR2,
                               p_Weight_Break VARCHAR2,
                               p_Srvlevl      VARCHAR2,
                               p_Carrier      VARCHAR2,
                               p_Currency     VARCHAR2,
                               p_Shmode       VARCHAR2,
                               p_Qry          VARCHAR2,
                               p_Page_No      NUMBER DEFAULT 1,
                               p_Page_Rows    NUMBER DEFAULT 50,
                               p_org_countries VARCHAR2,
	                       p_dest_countries VARCHAR2,
                               p_org_regions VARCHAR2,
                               p_dest_regions VARCHAR2,
                               p_Tot_Rec      OUT NUMBER,
                               p_Tot_Pages    OUT NUMBER,
                               p_Rs           OUT Resultset) AS
    v_Org_Locs     VARCHAR2(32767);
    v_src_Locs     VARCHAR2(32767);
    v_Dest_Locs    VARCHAR2(32767);
    v_src_Locs     VARCHAR2(32767);
    v_Terminal     VARCHAR2(32767);
    v_Acclevel     VARCHAR2(300);
    v_Rate_Type    VARCHAR2(300);
    v_Weight_Break VARCHAR2(300);
    v_Srvlevl      VARCHAR2(32767);
    v_Carrier      VARCHAR2(32767);
    v_Currency     VARCHAR2(300);
    v_Shmode       VARCHAR2(300);
    v_Operation    VARCHAR2(300);
    v_Type         VARCHAR2(1);
    v_Terminals    VARCHAR2(32767);
    v_Chargerate   VARCHAR2(400) := '';
    k              NUMBER := 0;
    v_Sql1         VARCHAR(2000);
    v_Sql_sur         VARCHAR(2000);
    v_Sql2         VARCHAR(2000);
    v_Sql3         VARCHAR(32767);
    v_Sql4         VARCHAR(2000);
    v_Sql5         VARCHAR2(2000);
    v_Sql6         VARCHAR2(32767);
    v_Sql7         VARCHAR2(2000);
    v_Sql8         VARCHAR2(2000);
    v_Sql9         VARCHAR2(2000);
    v_Base         VARCHAR2(30);
    v_Break        VARCHAR2(4000);
    v_Temp         VARCHAR2(200);
    v_Countryid    VARCHAR2(30);
    v_Countryid1   VARCHAR2(30);
    v_Temp1        VARCHAR2(10);
    v_Temp2        VARCHAR2(10);
    v_Temp3        VARCHAR2(10);
    v_Temp4        VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancements
    v_Temp5        VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancements
    --v_Ratedesc     Varchar2(300); --@@Added by Kameswari for Surcharge Enhancements
    v_Ratedesc   VARCHAR2(32767);
    v_Ubound     VARCHAR2(100);
    v_Lbound     VARCHAR2(100);
    v_Indicator  VARCHAR2(100);
    v_Qry        VARCHAR2(10);
    v_Rc_C1      Resultset;
    v_Rc_C2      Resultset;
    v_Break_Slab VARCHAR2(20);
    v_rate_desc  VARCHAR2(100);
    --v_versionno  Varchar2(20);
    v_lineno     VARCHAR2(20); --@@Added by Kameswari for Surcharge Enhancements
    v_Rate       VARCHAR2(100); --@@Added by Kameswari for Surcharge Enhancements
    v_org_reg      VARCHAR2(1000):= 'T';
    v_dest_reg     VARCHAR2(1000):= 'T';
    v_Temp_Break  VARCHAR2(1000);
    v_Temp_Rate  VARCHAR2(1000);
    v_Temp_desc  VARCHAR2(1000);
    v_temp_count NUMBER;
    v_schCurreny  VARCHAR2(15000);
  BEGIN
    IF p_Org_Locs IS NOT NULL THEN
      v_Org_Locs := ' AND sd.origin IN (' || '' ||
                    Qms_Rsr_Rates_Pkg.Seperator(p_Org_Locs) || '' || ')';
     v_org_reg :=' AND sd.origin IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.seperator(p_org_locs) || '' || ')';
    END IF;
     IF (  p_org_countries IS NOT NULL  AND LENGTH ((v_org_reg )) <=1)  THEN

    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
   v_org_reg  :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;
   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_org_countries) || '' || ') )';
    END IF;

  IF (  p_org_regions IS NOT NULL  AND LENGTH (TRIM (v_org_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_org_reg  := ' AND sd.origin IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_org_reg  :=  ' AND sd.origin IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_org_reg  := v_org_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_org_regions) || '' || ') )';

    END IF;

    IF p_Dest_Locs IS NOT NULL THEN
      v_Dest_Locs := '  AND sd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.Seperator(p_Dest_Locs) || '' || ') ';
       v_dest_reg := '  AND sd.destination IN (' || '' ||
                     Qms_Rsr_Rates_Pkg.seperator(p_dest_locs) || '' || ') ';
    END IF;

           IF (  p_dest_countries IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg := ' AND sd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE

   v_dest_reg :=  ' AND sd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;


   END IF;
   v_dest_reg := v_dest_reg || ' AND CON.COUNTRYID IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator(p_dest_countries) || '' || ') )';

    END IF;

 IF (  p_dest_regions IS NOT NULL  AND LENGTH (TRIM (v_dest_reg )) <=1)  THEN
    IF( p_Shmode='1') THEN

    v_dest_reg  := ' AND sd.destination IN ( SELECT DISTINCT LOC.LOCATIONID FROM FS_FR_LOCATIONMASTER   LOC,FS_COUNTRYMASTER  CON, FS_FR_TERMINALLOCATION TL ' ||
 ' WHERE LOC.COUNTRYID = CON.COUNTRYID AND LOC.LOCATIONID = TL.LOCATIONID AND (LOC.INVALIDATE = ''F'' OR LOC.INVALIDATE IS NULL )';
   ELSE
     v_dest_reg :=  ' AND sd.destination IN (  select distinct portid  from fs_frs_portmaster pm,FS_COUNTRYMASTER  CON where pm.countryid=con.countryid ' ;

   END IF;
   v_dest_reg := v_dest_reg  || ' AND CON.REGION  IN ('||  '' || Qms_Rsr_Rates_Pkg.seperator( p_dest_regions) || '' || ') )';

    END IF;

    IF p_Srvlevl IS NOT NULL THEN
      v_Srvlevl := '  AND sd.SERVICELEVEL_ID  IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Srvlevl) || '' || ')';
    END IF;
    IF p_Carrier IS NOT NULL THEN
      v_Carrier := '   AND sd.CARRIER_ID IN (' || '' ||
                   Qms_Rsr_Rates_Pkg.Seperator(p_Carrier) || '' || ' ) ';
    END IF;
    v_Terminal     := '''' || p_Terminal || '''';
    v_Acclevel     := '''' || p_Acclevel || '''';
    v_Rate_Type    := '''' || p_Rate_Type || '''';
    v_Weight_Break := '''' || p_Weight_Break || '''';
    v_Currency     := '''' || p_Currency || '''';
    v_Shmode       := '''' || p_Shmode || '''';
    IF UPPER(Trim(p_Qry)) <> 'IS NULL' THEN
      v_Qry := '=' || '''' || p_Qry || '''';
    ELSE
      v_Qry := p_Qry;
    END IF;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_data_1');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE base_data_1');

    IF UPPER(p_Weight_Break) = 'FLAT' THEN
      v_Break := 'Min,Flat,';
    END IF;

   /*v_Sql1 := ' insert into temp_data_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO, LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION) SELECT qbd.buyrateid,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,DECODE(qbd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',qbd.RATE_DESCRIPTION)RATE_DESCRIPTION ';
    v_Sql7 := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid  ';*/
    --@@Modified by kameswari for the WPBN issue-146448 on 23/12/08
    v_Sql1 := ' insert into temp_data_1(BUYRATEID, VERSION_NO,WEIGHT_BREAK_SLAB, CHARGERATE, LANE_NO, LINE_NO, LBOUND, UBOUND, C_INDICATOR,RATE_DESCRIPTION,sur_charge_currency) SELECT distinct qbd.buyrateid,qbd.version_no,qbd.weight_break_slab weight_break_slab, qbd.chargerate chargerate,qbd.lane_no,QBD.LINE_NO,QBD.LOWERBOUND, QBD.UPPERBOUND, QBD.CHARGERATE_INDICATOR,DECODE(qbd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',qbd.RATE_DESCRIPTION)RATE_DESCRIPTION,nvl(qbd.sur_charge_currency,qbm.currency) ';
    v_Sql7 := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm WHERE qbd.buyrateid = qbm.buyrateid  ';
    IF v_org_reg <>'T' THEN
    v_Sql6 := v_Sql6 || v_org_reg;
    END IF;

    IF v_dest_reg <>'T' THEN
    v_Sql6 := v_Sql6 || v_dest_reg;
    END IF;

    v_Sql6 := v_Sql6 || v_Srvlevl;-- replace v_org_locs wth v_org_reg, v_Dest_Locs wth v_dest_reg by phani sekhar for wpbn 171213
    --@@Modified by kameswari for the WPBN issue-146448 on 23/12/08
    /*v_Sql9 := ' AND  (qBD.BUYRATEID,qbd.lane_no) IN(select buyrateid,lane_NO from QMS_REC_CON_SELLRATESMASTER SM,QMS_REC_CON_SELLRATESDTL SD where SM.REC_CON_ID=SD.REC_CON_ID ' ||
              v_Sql6 || ' AND SM.SHIPMENT_MODE=' || v_Shmode ||
              ' AND SM.TERMINALID=' || '''' || p_Terminal || '''' ||
              ' and sd.AI_FLAG=''A'' AND sd.INVALIDATE=''F'' AND sm.weight_break=' ||
              v_Weight_Break || ' AND sm.rate_type =' || v_Rate_Type ||
              v_Carrier || ' And sm.currency =' || v_Currency || ')';*/
    v_Sql9 := 'and (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbm.versioN_no=qbd.version_no  AND  (qBD.BUYRATEID,qbd.lane_no,qbd.version_no) IN(select buyrateid,lane_NO,version_No from QMS_REC_CON_SELLRATESMASTER SM,QMS_REC_CON_SELLRATESDTL SD where SM.REC_CON_ID=SD.REC_CON_ID ' ||
        v_Sql6 || ' AND SM.SHIPMENT_MODE=' || v_Shmode ||
        ' AND SM.TERMINALID=' || '''' || p_Terminal || '''' ||
        ' and sd.AI_FLAG=''A'' AND sd.INVALIDATE=''F'' AND sm.weight_break=' ||
        v_Weight_Break || ' AND sm.rate_type =' || v_Rate_Type ||
        v_Carrier || ' And sm.currency =' || v_Currency || ')';

    v_Sql4 := '  AND qbd.activeinactive IS NULL AND qbd.INVALIDATE IS NULL' ||
              '  AND qbd.generated_flag ' || v_Qry;
    v_Sql2 := '   AND qbm.shipment_mode =' || v_Shmode;
    /*v_Sql5 := ' insert into base_data_1(BUYRATEID, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION) select  distinct qbd.buyrateid buyrateid, qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ' ||
              ' ''a''  chargerate, qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.CURRENCY,qbd.DENSITY_CODE,qbd.NOTES,DECODE(qbd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',qbd.RATE_DESCRIPTION)RATE_DESCRIPTION ';*/ --FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  '
     --@@Modified by kameswari for the WPBN issue-146448 on 23/12/08
     v_Sql5 := ' insert into base_data_1(BUYRATEID,VERSION_NO, CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL, WEIGHT_BREAK_SLAB, TRANSIT_TIME, FREQUENCY, CHARGERATE, LANE_NO, ORG_COUNTRYID, DEST_COUNTRYID,CURRENCY,DENSITY_CODE,NOTES,RATE_DESCRIPTION,EXTERNAL_NOTES) select  distinct qbd.buyrateid buyrateid, qbd.version_no version_no,qbd.carrier_id carrier_id, qbd.origin origin, qbd.destination destination,qbd.service_level service_level,''a''   weight_break_slab,qbd.transit_time transit_time, qbd.frequency frequency, ' ||
              ' ''a''  chargerate, qbd.lane_no lane_no,''a''  org_countryid,''a'' Dest_countryid,qbm.CURRENCY,qbd.DENSITY_CODE,qbd.NOTES,DECODE(qbd.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',qbd.RATE_DESCRIPTION)RATE_DESCRIPTION,qbd.EXTERNAL_NOTES EXTERNAL_NOTES '; --FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm  WHERE qbd.buyrateid = qbm.buyrateid  '

    v_Sql8 := ' FROM QMS_BUYRATES_DTL qbd, QMS_BUYRATES_MASTER qbm, qms_rec_con_sellratesm WHERE qbd.buyrateid = qbm.buyrateid and  qbm.version_no=qbd.version_no and (qbm.lane_no=qbd.lane_no or qbm.lane_no is null)';
print_out(v_Sql1 || v_Sql7 || v_Sql9 || v_Sql4 || v_Sql2);
print_out(v_Sql5 || v_Sql7 || v_Sql9 || v_Sql4 || v_Sql2 ||
                      ' and qbd.service_level not in (''SCH'') ORDER BY buyrateid, lane_no');
    EXECUTE IMMEDIATE (v_Sql1 || v_Sql7 || v_Sql9 || v_Sql4 || v_Sql2);
    EXECUTE IMMEDIATE (v_Sql5 || v_Sql7 || v_Sql9 || v_Sql4 || v_Sql2 ||
                      ' and qbd.service_level not in (''SCH'') ORDER BY buyrateid, lane_no');
    COMMIT;
    FOR j IN (SELECT Buyrateid,
                     version_no,  --@@Added by Kameswari for the WPBN issue-146448 on 23/12/08
                     Weight_Break_Slab,
                     Chargerate,
                     Lane_No,
                     Origin,
                     Destination
                FROM BASE_DATA_1
               ORDER BY Buyrateid) LOOP
      v_Chargerate := '';
      k            := 1;
      v_Break      := '';
      v_Ratedesc   := '';
      v_temp_count :=0;
      IF (UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1) = FALSE THEN
      BEGIN
        EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),RATE_DESCRIPTION ,sur_charge_currency from temp_data_1
                            WHERE  buyrateid =:v_buy_rate_id AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=0 AND WEIGHT_BREAK_SLAB=''BASIC'' ')
          INTO v_Temp_Break, v_Temp_Rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4,v_Temp5
           --Using j.Buyrateid, j.Lane_No;--@@modified by kameswari for the WPBN issue-146448 on 23/12/08
          USING j.Buyrateid, j.version_no,j.Lane_No;         --where  buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no and line_no=0')--@@Modified by Kameswari for the WPBN issue-146448 on 23/12/08


        v_Lbound    := v_Temp1 || ',';
        v_Ubound    := v_Temp2 || ',';
        v_Indicator := v_Temp3 || ',';
        v_Break     := v_Temp_Break || ',';
        v_Chargerate := v_Temp_Rate ||',';
        v_Temp_desc  := v_Temp4   ||',';
        v_schCurreny := v_Temp5   ||',';
        EXCEPTION WHEN NO_DATA_FOUND THEN
        v_temp_count := v_temp_count +1;

        v_Lbound    := '0' || ',';
        v_Ubound    := '0' || ',';
        v_Indicator := '-' || ',';
        v_Break     := 'BASIC' || ',';
        v_Chargerate := '-' ||',';
        v_Temp_desc  := 'A FREIGHT RATE'  ||',';
        v_schCurreny := '-' ||',';

               EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),RATE_DESCRIPTION,sur_charge_currency from temp_data_1
                            WHERE  buyrateid =:v_buy_rate_id AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=0 AND WEIGHT_BREAK_SLAB=''MIN'' ')
          INTO v_Temp_Break, v_Temp_Rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4,v_Temp5
           --Using j.Buyrateid, j.Lane_No;--@@modified by kameswari for the WPBN issue-146448 on 23/12/08
          USING j.Buyrateid, j.version_no,j.Lane_No;         --where  buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no and line_no=0')--@@Modified by Kameswari for the WPBN issue-146448 on 23/12/08


        v_Lbound    := v_Lbound||v_Temp1 || ',';
        v_Ubound    := v_Ubound||v_Temp2 || ',';
        v_Indicator := v_Indicator||v_Temp3 || ',';
        v_Break     := v_Break||v_Temp_Break ;
        v_Chargerate := v_Chargerate||v_Temp_Rate ;
        v_Temp4  := v_Temp_desc||v_Temp4   ;
        v_schCurreny := v_schCurreny || v_Temp5||',' ;
        END;
        IF v_temp_count = 0 THEN
                EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE),NVL(LBOUND,''-''),NVL(UBOUND,''-''), nvl(C_INDICATOR,''-''),RATE_DESCRIPTION,sur_charge_currency from temp_data_1
                            WHERE  buyrateid =:v_buy_rate_id AND version_no =:v_versionno AND LANE_NO=:v_lane_no AND line_no=1 AND WEIGHT_BREAK_SLAB=''MIN'' ')
          INTO v_Temp_Break, v_Temp_Rate, v_Temp1, v_Temp2, v_Temp3, v_Temp4, v_Temp5
           --Using j.Buyrateid, j.Lane_No;--@@modified by kameswari for the WPBN issue-146448 on 23/12/08
          USING j.Buyrateid, j.version_no,j.Lane_No;         --where  buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no and line_no=0')--@@Modified by Kameswari for the WPBN issue-146448 on 23/12/08


        v_Lbound    := v_Lbound||v_Temp1 || ',';
        v_Ubound    := v_Ubound||v_Temp2 || ',';
        v_Indicator := v_Indicator||v_Temp3 || ',';
        v_Break     := v_Break||v_Temp_Break;
        v_Chargerate := v_Chargerate||v_Temp_Rate ;
        v_Temp4  := v_Temp_desc ||v_Temp4;
        v_schCurreny := v_schCurreny || v_Temp5 ||',';

        END IF;
        --v_Ratedesc  := v_Temp4 || ',';
      ELSE
        v_Temp1     := '';
        v_Temp2     := '';
        v_Temp3     := '';
        v_Lbound    := '';
        v_Ubound    := '';
        v_Indicator := '';
        v_Ratedesc  := '';
        v_schCurreny := '';
--        v_Ratedesc  := ',';
      END IF;
      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Base       := v_Chargerate;
        v_Chargerate := '';
        v_Rate       := v_Temp4;
        v_Temp4      := '';
      ELSIF UPPER(p_Weight_Break) = 'FLAT' OR
            UPPER(p_Weight_Break) = 'SLAB' THEN
        v_Chargerate := v_Chargerate || ',';
        v_Ratedesc   := v_Temp4 || ',';
      END IF;
--commented and added by subrahmanyam for CR-219973
/*      -- If (Upper(p_Weight_Break) <> 'FLAT') Then
      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0   ORDER BY break_slab  ';
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0   ORDER BY break_slab  ';
      Elsif Upper(p_Weight_Break) = 'LIST' And p_Shmode <> 1 Then
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab  FROM temp_data_1  ORDER BY break_slab  ';
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  ORDER BY break_slab  ';
      Else
        v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  order by  break_slab';
        --v_Sql1 := 'SELECT DISTINCT  weight_break_slab break_slab FROM temp_data_1  WHERE line_no > 0  order by  break_slab';

      End If;*/

      IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Sql1 := 'SELECT   DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0  and rate_description=''A FREIGHT RATE'' ORDER BY break_slab  ';
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
        v_Sql1 := 'SELECT   DISTINCT  weight_break_slab break_slab  FROM temp_data_1  WHERE  rate_description=''A FREIGHT RATE'' ORDER BY break_slab  ';
      ELSE
        v_Sql1 := 'SELECT    DISTINCT weight_break_slab break_slab  FROM temp_data_1  WHERE line_no > 0  and weight_break_slab not in(''BASIC'',''MIN'') and rate_description=''A FREIGHT RATE'' ORDER BY break_slab  ';

      END IF;
            IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Sql_sur := 'SELECT     weight_break_slab break_slab ,RATE_DESCRIPTION FROM temp_data_1  WHERE line_no > 0  and rate_description<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode <> 1 THEN
        v_Sql_sur := 'SELECT     weight_break_slab break_slab,RATE_DESCRIPTION FROM temp_data_1  WHERE  rate_description<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';
      ELSE
        v_Sql_sur := 'SELECT    weight_break_slab break_slab,RATE_DESCRIPTION  FROM temp_data_1  WHERE line_no > 0  and rate_description<>''A FREIGHT RATE'' AND BUYRATEID='||j.buyrateid ||' and lane_no='||j.lane_no ||'ORDER BY LINE_NO  ';

      END IF;
--ended for 219973
      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP

        FETCH v_Rc_C1
          INTO v_Break_Slab;

        EXIT WHEN v_Rc_C1%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
            INTO v_Temp1
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp1 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
            INTO v_Temp2
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp2 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
            INTO v_Temp3
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;

          IF v_Temp3 IS NULL THEN
            v_Temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp3 := '-';
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
       -- print_out('QUERY.........');
      --  PRINT_OUT(' SELECT DECODE(RATE_DESCRIPTION,'''',''''A FREIGHT RATE'''',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab ='||v_break_slab
--                             ||'   AND buyrateid ='||j.Buyrateid|| 'and LANE_NO='||j.Lane_No ||'AND version_no='||j.version_no||' and rate_description='||v_rate_desc);

          EXECUTE IMMEDIATE (' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                  AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
             v_Temp4 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
        EXECUTE IMMEDIATE (' SELECT SUR_CHARGE_CURRENCY FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                  AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description=''A FREIGHT RATE''')
       INTO v_Temp5
       USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no;
       EXCEPTION
       WHEN NO_DATA_FOUND THEN
       v_Temp5 := '-';
       END;



        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND version_no=:v_versionno and rate_description=''A FREIGHT RATE''
        AND weight_break_slaB=:V_BRAK_SLAB')
        USING j.Buyrateid, j.Lane_No,j.version_no,v_Break_Slab;
        v_Chargerate := v_Chargerate || v_Temp || ',';
        v_Lbound     := v_Lbound || v_Temp1 || ',';
        v_Ubound     := v_Ubound || v_Temp2 || ',';
        v_Indicator  := v_Indicator || v_Temp3 || ',';
        v_Ratedesc   := v_Ratedesc || v_Temp4 || ',';
        v_schCurreny := v_schCurreny|| v_Temp5 || ',';
      END LOOP;

            IF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        --  v_Chargerate := v_Chargerate || v_Base || ',';
        --    v_Ratedesc := v_Ratedesc || v_Rate || ',';
        v_Chargerate := v_Base || ',' || v_Chargerate;
        v_Ratedesc   := v_Rate || ',' || v_Ratedesc;
      END IF;
      v_Base  := v_Break;
      v_Break := '';

      CLOSE v_Rc_C1;
       OPEN v_Rc_C2 FOR v_Sql_sur;
      LOOP

        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc;

        EXIT WHEN v_Rc_C2%NOTFOUND;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate)  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                 AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT  LBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp1
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp1 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT UBOUND  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp2
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp2 := '-';
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
          EXECUTE IMMEDIATE (' SELECT C_INDICATOR  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp3
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;

          IF v_Temp3 IS NULL THEN
            v_Temp3 := '-';
          END IF;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Temp3 := '-';
        END;
        --@@Added by kameswari for Surcharge Enhancements
        BEGIN
        --print_out('QUERY.........');
       -- PRINT_OUT(' SELECT DECODE(RATE_DESCRIPTION,'''',''''A FREIGHT RATE'''',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab ='||v_break_slab
                             --||'   AND buyrateid ='||j.Buyrateid|| 'and LANE_NO='||j.Lane_No ||'AND version_no='||j.version_no||' and rate_description='||v_rate_desc);

          EXECUTE IMMEDIATE (' SELECT DECODE(RATE_DESCRIPTION,'''',''A FREIGHT RATE'',RATE_DESCRIPTION)RATE_DESCRIPTION  FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
                                  AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
            INTO v_Temp4
            USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          IF v_rate_desc <> 'A FREIGHT RATE' THEN
            v_Temp4 := '**';
            ELSE
            v_Temp4 := '-';
            END IF;
          WHEN OTHERS THEN
            Dbms_Output.Put_Line(SQLERRM);
        END;
        BEGIN
        EXECUTE IMMEDIATE (' SELECT SUR_CHARGE_CURRENCY FROM temp_data_1   WHERE weight_break_slab =:v_break_slab
          AND buyrateid =:v_buy_rate_id AND LANE_NO=:v_lane_no AND version_no=:v_versionno AND get_flag=''N'' AND rate_description<>''A FREIGHT RATE'' AND rate_description=:v_rate_desc')
         INTO v_Temp5
         USING v_Break_Slab, j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc;
         exception
         WHEN NO_DATA_FOUND THEN
          v_Temp5:='-';
          end;

        EXECUTE IMMEDIATE('UPDATE TEMP_DATA_1 SET GET_FLAG=''Y'' WHERE buyrateid =:v_buy_rate_id and LANE_NO=:v_lane_no AND version_no=:v_versionno and rate_description=:v_rate_desc
        AND weight_break_slaB=:V_BRAK_SLAB')
        USING j.Buyrateid, j.Lane_No,j.version_no,v_rate_desc,v_Break_Slab;
        v_Chargerate := v_Chargerate || v_Temp || ',';
        v_Lbound     := v_Lbound || v_Temp1 || ',';
        v_Ubound     := v_Ubound || v_Temp2 || ',';
        v_Indicator  := v_Indicator || v_Temp3 || ',';
        v_Ratedesc   := v_Ratedesc || v_Temp4 || ',';
        v_schCurreny := v_schCurreny || v_Temp5 || ',';
      END LOOP;

/*      --Close v_Rc_C1;
      If Upper(p_Weight_Break) = 'LIST' And p_Shmode = 1 Then
        --  v_Chargerate := v_Chargerate || v_Base || ',';
        --    v_Ratedesc := v_Ratedesc || v_Rate || ',';
        v_Chargerate := v_Base || ',' || v_Chargerate;
        v_Ratedesc   := v_Rate || ',' || v_Ratedesc;
      End If;
      v_Base  := v_Break;
      v_Break := '';*/

      CLOSE v_Rc_C2;

      OPEN v_Rc_C1 FOR v_Sql1;
      LOOP
        FETCH v_Rc_C1
          INTO v_Break_Slab;
        EXIT WHEN v_Rc_C1%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C1;
            OPEN v_Rc_C2 FOR v_Sql_sur;
      LOOP
        FETCH v_Rc_C2
          INTO v_Break_Slab,v_rate_desc;
        EXIT WHEN v_Rc_C2%NOTFOUND;
        v_Break := v_Break || ',' || v_Break_Slab;
      END LOOP;
      CLOSE v_Rc_C2;
      IF (UPPER(p_Weight_Break) = 'SLAB' OR UPPER(p_Weight_Break) = 'FLAT') THEN
        v_Break := v_Base || v_Break;
      ELSIF UPPER(p_Weight_Break) = 'LIST' AND p_Shmode = 1 THEN
        v_Break := v_Break || ',' || v_Base;
        v_Break := SUBSTR(v_Break, 2, LENGTH(v_Break));
      ELSE
        v_Break := SUBSTR(v_Break, 2, LENGTH(v_Break));
      END IF;
      /* Else
        Select Weight_Break_Slab, Chargerate
          Into v_Base, v_Temp
          From Temp_Data_1
         Where Line_No > 0
           And Buyrateid = j.Buyrateid
           And Lane_No = j.Lane_No;
        v_Break      := v_Break || ',' || v_Base;
        v_Chargerate := v_Chargerate || v_Temp || ',';
      End If;*/
      UPDATE BASE_DATA_1
         SET Weight_Break_Slab = v_Break,
             Chargerate        = SUBSTR(v_Chargerate,
                                        1,
                                        LENGTH(v_Chargerate) - 1),
             Ubound            = SUBSTR(v_Ubound, 1, LENGTH(v_Ubound) - 1),
             Lbound            = SUBSTR(v_Lbound, 1, LENGTH(v_Lbound) - 1),
             c_Indicator       = SUBSTR(v_Indicator,
                                        1,
                                        LENGTH(v_Indicator) - 1),
             rate_description  = SUBSTR(v_Ratedesc,
                                        1,
                                        LENGTH(v_Ratedesc) - 1),
            sur_charge_currency = SUBSTR(v_schCurreny,1,LENGTH(v_schCurreny)-1)
       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No;
      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Origin;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid := '';
        END;
      END IF;
      IF p_Shmode <> 2 THEN
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FR_LOCATIONMASTER
           WHERE Locationid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      ELSE
        BEGIN
          SELECT Countryid
            INTO v_Countryid1
            FROM FS_FRS_PORTMASTER
           WHERE Portid = j.Destination;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_Countryid1 := '';
        END;
      END IF;
      UPDATE BASE_DATA_1
         SET Org_Countryid = v_Countryid, Dest_Countryid = v_Countryid1
       WHERE Buyrateid = j.Buyrateid
         AND Lane_No = j.Lane_No;
    END LOOP;

    -- Added By Kishore For SurCharge Currency

        /* FOR i IN ( SELECT b.buyrateid, b.version_no,b.lane_no,b.currency,b.weight_break_slab,b.origin,b.destination FROM  BASE_DATA_1 b)
             LOOP
                \*  FOR j IN(
                    SELECT NVL(bd.sur_charge_currency,i.currency) sur_charge_currency
                            FROM QMS_BUYRATES_DTL BD
                            WHERE bd.buyrateid = i.buyrateid
                                AND bd.activeinactive IS NULL
                                and bd.lane_no=i.lane_no --@@Added by Kameswari for the WPBN issue - 259371 on 25/07/2011
                                AND bd.version_no = i.version_no
                               AND BD.ORIGIN  = i.origin
                             and bd.destination = i.destination
                    LOOP*\



                      SELECT wm_concat(NVL(bd.sur_charge_currency,i.currency)) into v_schCurreny
                            FROM QMS_BUYRATES_DTL BD
                            WHERE bd.buyrateid = i.buyrateid
                                AND bd.activeinactive IS NULL
                                AND bd.version_no = i.version_no
                                AND bd.lane_no  =  i.lane_no
                                AND BD.ORIGIN  = i.origin
                             and bd.destination = i.destination  ;

    print_out('buyrated_id***'||i.buyrateid||'version_no****'||i.version_no||'lane_no****'||i.lane_no||'ORIGIN****'||i.origin||'destination****'||i.destination||'v_schCurreny****'||v_schCurreny ) ;


                     --    v_schCurreny := v_schCurreny || j.sur_charge_currency||',';



                    IF INSTR(I.WEIGHT_BREAK_SLAB,'BASIC') != 0 THEN
                      IF LENGTH(v_schCurreny)-1 != LENGTH(I.WEIGHT_BREAK_SLAB) THEN
                      UPDATE BASE_DATA_1 BD
                        SET  SUR_CHARGE_CURRENCY = '-,'||v_schCurreny
                   WHERE Buyrateid = i.Buyrateid
                    AND  BD.VERSION_NO = i.version_no
                    AND BD.LANE_NO  =  i.lane_no
                    AND BD.ORIGIN = i.origin
                    AND BD.DESTINATION = i.destination;
                    ELSE
                     UPDATE BASE_DATA_1 BD
                        SET  SUR_CHARGE_CURRENCY = v_schCurreny
                    WHERE Buyrateid = i.Buyrateid
                    AND  BD.VERSION_NO = i.version_no
                    AND BD.LANE_NO  =  i.lane_no
                    AND BD.ORIGIN = i.origin
                    AND BD.DESTINATION = i.destination;
                      END IF;
                      ELSE
                   UPDATE BASE_DATA_1 BD
                        SET  SUR_CHARGE_CURRENCY = v_schCurreny
                    WHERE Buyrateid = i.Buyrateid
                    AND  BD.VERSION_NO = i.version_no
                    and BD.lane_no=i.lane_no   ; --@@Added by Kameswari for the WPBN issue - 259371 on 25/07/2011
                     AND BD.ORIGIN = i.origin
                    AND BD.DESTINATION = i.destination;

                    END IF;


                    v_schCurreny:='';

  END LOOP;*/
       --End Of Kishore For SurCharge Currency

    OPEN p_Rs FOR
      SELECT *
        FROM (SELECT T1.*, ROWNUM Rn
                FROM (SELECT * FROM BASE_DATA_1) T1
               WHERE ROWNUM <= ((p_Page_No - 1) * p_Page_Rows) + p_Page_Rows)
       WHERE Rn > ((p_Page_No - 1) * p_Page_Rows);
    SELECT COUNT(*) INTO p_Tot_Rec FROM BASE_DATA_1;
    --Select Ceil((p_Tot_Rec / p_Page_Rows)) Into p_Tot_Pages From Dual;
    p_tot_pages := CEIL(p_tot_rec / p_page_rows);
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      p_Tot_Rec   := 0;
      p_Tot_Pages := 0;
    WHEN CURSOR_ALREADY_OPEN
 THEN
      Qms_Rsr_Rates_Pkg.g_Err      := '<< ' || SQLERRM || ' >>';
      Qms_Rsr_Rates_Pkg.g_Err_Code := '<< ' || SQLCODE || ' >>';
      INSERT INTO QMS_OBJECTS_ERRORS
        (Ex_Date, Module_Name, Errorcode, Errormessage)
      VALUES
        (SYSDATE,
         'Qms_Rsr_Rates_Pkg->comman_proc',
         Qms_Rsr_Rates_Pkg.g_Err_Code,
         Qms_Rsr_Rates_Pkg.g_Err);
      COMMIT;
  END;

  /*  Using this Function for replacing "~" oprater to "," and returning string
   *  p_str       VARCHAR2;
   *  RETURN      VARCHAR2
  */
  FUNCTION Seperator(p_Str VARCHAR2) RETURN VARCHAR2 AS
    v_Return VARCHAR2(32767) := '';
    k        NUMBER;
    f        NUMBER;
    r        NUMBER := 1;
  BEGIN
    k := INSTR(p_Str, '~', 1, 1) - 1;
    f := 1;
    r := 1;
    WHILE k > 0 LOOP
      IF r != 1 THEN
        f := f + 1;
      END IF;
      k := k + 1;

      v_Return := v_Return || '''' || SUBSTR(p_Str, f, k - f) || '''' || ',';
      f        := INSTR(p_Str, '~', 1, r);
      r        := r + 1;
      k        := INSTR(p_Str, '~', 1, r) - 1;
    END LOOP;
    v_Return := v_Return || '''' ||
                SUBSTR(p_Str, INSTR(p_Str, '~', -1) + 1, LENGTH(p_Str)) || '''';
    RETURN v_Return;
  EXCEPTION
    WHEN OTHERS THEN
      Qms_Rsr_Rates_Pkg.g_Err      := '<< ' || SQLERRM || ' >>';
      Qms_Rsr_Rates_Pkg.g_Err_Code := '<< ' || SQLCODE || ' >>';
      INSERT INTO QMS_OBJECTS_ERRORS
        (Ex_Date, Module_Name, Errorcode, Errormessage)
      VALUES
        (SYSDATE,
         'Qms_Rsr_Rates_Pkg->Seperator',
         Qms_Rsr_Rates_Pkg.g_Err_Code,
         Qms_Rsr_Rates_Pkg.g_Err);
      COMMIT;
  END;

  /*  This function validates the occurrence of the sell rates as per the rules defined in data
   *  access-business rules doc in the repository and inactivates that data if present and
   *  then calls the quote updating procedure.
   *  p_origin                VARCHAR2;
   *  p_destination           VARCHAR2;
   *  p_serv_level            VARCHAR2;
   *  p_carrier               VARCHAR2;
   *  p_frequency             VARCHAR2;
   *  p_current_terminal_id   VARCHAR2;
   *  p_wet_break             VARCHAR2;
   *  p_rate_type             VARCHAR2;
   *  p_shipmentmode          VARCHAR2;
   *  p_newsellrateid         NUMBER;
   *  RETURN                  NUMBER;
  */
   FUNCTION Qms_Sell_Rate_Validation(p_Origin              VARCHAR2,
                                    p_Destination         VARCHAR2,
                                    p_Serv_Level          VARCHAR2,
                                    p_Carrier             VARCHAR2,
                                    p_Frequency           VARCHAR2,
                                    p_Current_Terminal_Id VARCHAR2,
                                    p_Wet_Break           VARCHAR2,
                                    p_Rate_Type           VARCHAR2,
                                    p_Shipmentmode        VARCHAR2,
                                    p_Process             VARCHAR2,
                                    p_Newsellrateid       NUMBER,
                                    p_Newbuyrateid        VARCHAR2,
                                    p_New_Lane_No         NUMBER,
                                    p_operation           VARCHAR2)
    RETURN NUMBER AS
    Str           VARCHAR2(32767) := '';
    v_Accesslevel VARCHAR2(32767) := '';
    v_Shipmodestr VARCHAR2(32767) := '';
    v_changedesc VARCHAR2(32767) := '';
     v_changedesc1 VARCHAR2(32767) := '';
    v_quoteid    VARCHAR2(32767) := '';
    v_transittime VARCHAR2(32767) := '';
    v_count      NUMBER(10) := 0;
    v_validupto  DATE ;
    v_sellbuyflag  VARCHAR2(5) := 'RSR';
    v_newversionno  NUMBER;
    v_countupdate   NUMBER(10) := 0;
    V_COUNTQUOTE    NUMBER(10);
    v_oldversionno  NUMBER(10);
  BEGIN
    Str := Validate_Sellrate(p_Origin,
                             p_Destination,
                             p_Serv_Level,
                             p_Carrier,
                             p_Frequency,
                             p_Current_Terminal_Id,
                             p_Wet_Break,
                             p_Rate_Type,
                             p_Shipmentmode);
    IF Str = 'TRUE' THEN
      RETURN 5;
    END IF;
    SELECT Oper_Admin_Flag
      INTO v_Accesslevel
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = p_Current_Terminal_Id;
    IF p_Shipmentmode = '1' THEN
      v_Shipmodestr := 'Air';
    ELSIF p_Shipmentmode = '2' THEN
      v_Shipmodestr := 'Sea';
    ELSE
      v_Shipmodestr := 'Truck';
    END IF;
    IF v_Accesslevel = 'H' THEN
      IF p_operation='Acceptance'
        THEN
      FOR i IN (SELECT  b.Rec_Con_Id_sell Rec_Con_Id,
                                b.Buyrateid,
                                b.Lane_No,
                                b.Origin,
                                b.version_no,      --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                b.Destination

                  FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                       QMS_REC_CON_SELLRATESDTL_ACC    b
                 WHERE b.Rec_Con_Id = a.Rec_Con_Id
                   AND b.Origin = p_Origin
                   AND b.Destination = p_Destination
                   AND b.Servicelevel_Id = p_Serv_Level
                   AND b.Carrier_Id = p_Carrier
                   AND b.line_no=0
                  /* And b.Frequency = p_Frequency*/
                   /*And b.Rec_Con_Id In
                       (Select Dtl.Rec_Con_Id
                          From Qms_Rec_Con_Sellratesmaster Mas,
                               Qms_Rec_Con_Sellratesdtl    Dtl
                         Where Mas.Rc_Flag = 'R'
                           And Mas.Rec_Con_Id = Dtl.Rec_Con_Id)*/
                   AND a.Weight_Break = p_Wet_Break
                   AND a.Rate_Type = p_Rate_Type
                   AND a.Shipment_Mode = p_Shipmentmode
                  AND b.Ai_Flag = 'A'
                   AND a.Terminalid IN
                       (SELECT DISTINCT Terminalid FROM FS_FR_TERMINALMASTER)) LOOP

         SELECT MAX(version_no) INTO v_newversionno FROM QMS_BUYRATES_DTL WHERE buyrateid=i.buyrateid AND lane_no=i.lane_no AND line_no='0';

        v_countupdate :=0;
        v_count       :=0;
        v_changedesc  :='';

        IF p_Shipmentmode='2' AND p_Wet_Break = 'LIST'
        THEN
       /* for j in (select weight_break_slab,chargerate from qms_buyrates_dtl where buyrateid=i.Buyrateid and version_no=v_newversionno and lane_no=i.lane_no)
        loop
          v_changedesc  :=j.weight_break_slab||','||p_Origin||'-'||p_Destination||', Sea Freight Rates and Surcharges';
           for m in (SELECT QUOTE_ID,CHANGEDESC  FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID IN (SELECT QR.QUOTE_ID FROM QMS_QUOTE_RATES QR,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND SELL_BUY_FLAG='RSR' AND BUYRATE_ID=i.buyrateid AND RATE_LANE_NO=i.lane_no AND BREAK_POINT =j.weight_break_slab  AND BUY_RATE NOT IN (j.chargerate) ) AND CONFIRM_FLAG IS NULL)
          Loop
          If m.changedesc = v_changedesc
          Then
            v_changedesc :=m.changedesc;
          Else
         v_changedesc :=j.weight_break_slab||','||m.changedesc;
         End If;
        UPDATE QMS_QUOTES_UPDATED SET new_sellcharge_id=p_Newsellrateid, CHANGEDESC= v_changedesc ,NEW_VERSION_NO=v_newversionno WHERE QUOTE_ID=m.quote_id AND SELL_BUY_FLAG='RSR';
        End Loop;
        v_changedesc  :=j.weight_break_slab||','||p_Origin||'-'||p_Destination||', Sea Freight Rates and Surcharges';
        for k in (SELECT  QR.QUOTE_ID QUOTEID,QR.SELLRATE_ID SELLRATEID
                   From Qms_Quote_Rates Qr,QMS_QUOTE_MASTER QM WHERE QM.ACTIVE_FLAG='A' AND QM.ID=QR.QUOTE_ID AND Qr.Sell_Buy_Flag ='RSR' And Qr.Buyrate_Id =i.Buyrateid

                  And Qr.Rate_Lane_No =i.Lane_No  AND QR.BREAK_POINT =j.weight_break_slab AND QR.BUY_RATE NOT IN (j.chargerate))
       Loop

       SELECT COUNT(*) into v_count FROM QMS_QUOTES_UPDATED WHERE QUOTE_ID =k.quoteid AND SELL_BUY_FLAG='RSR' AND CONFIRM_FLAG IS NULL;
       If v_count=0
       Then
       INSERT INTO QMS_QUOTES_UPDATED (QUOTE_ID,NEW_SELLCHARGE_ID,
            NEW_BUYCHARGE_ID, NEW_LANE_NO, SELL_BUY_FLAG, CHANGEDESC, OLD_SELLCHARGE_ID,OLD_BUYCHARGE_ID, OLD_LANE_NO,OLD_VERSION_NO,NEW_VERSION_NO)
         Values(k.quoteid,p_Newsellrateid,p_Newbuyrateid,p_New_Lane_No,'RSR',v_changedesc,k.sellrateid,i.buyrateid,i.lane_no,i.version_no,v_newversionno);
        v_countupdate :=v_countupdate+1;
        End If;
        End Loop;

        End loop;

*/
  FOR M IN (SELECT t2.* FROM (SELECT QR.QUOTE_ID QUOTEID,Qr.Version_No,QR.BREAK_POINT BREAK_POINT,BUY_RATE,SELLRATE_ID ,BUYRATE_ID,RATE_LANE_NO,(SELECT 'X' FROM QMS_QUOTE_MASTER WHERE ID=QR.QUOTE_ID AND ACTIVE_FLAG='A')t1
					      FROM   QMS_QUOTE_RATES QR,QMS_BUYRATES_DTL QBD
				       	WHERE    QR.BUYRATE_ID = i.Buyrateid
						    AND    QR.RATE_LANE_NO = i.lane_no
                AND QR.SELL_BUY_FLAG='RSR'
				        AND  BUYRATEID=QR.BUYRATE_ID AND LANE_NO=QR.RATE_LANE_NO AND QBD.VERSION_NO=v_newversionno AND WEIGHT_BREAK_SLAB=QR.BREAK_POINT AND CHARGERATE<>QR.BUY_RATE)t2 WHERE t1 IS NOT NULL

                )
				    LOOP
        	 BEGIN
           v_countupdate :=v_countupdate+1;
            SELECT COUNT(quote_id) INTO   V_COUNTQUOTE
              FROM
             QMS_QUOTES_UPDATED QU WHERE  QUOTE_ID = M.QUOTEID	   AND QU.SELL_BUY_FLAG = 'RSR'

             	 AND CONFIRM_FLAG IS NULL;
               EXCEPTION WHEN NO_DATA_FOUND
               THEN
               V_COUNTQUOTE :=0;
               END;

					  IF V_COUNTQUOTE = 0 THEN

						INSERT INTO QMS_QUOTES_UPDATED(QUOTE_ID,new_sellcharge_id,New_Buycharge_Id,New_Lane_No,sell_buy_flag,changedesc,old_sellcharge_id,old_buycharge_id,old_lane_no,old_version_no,new_version_no)
						VALUES
						      (M.QUOTEID,
                 p_Newsellrateid,
						       i.buyrateid, i.lane_no,
						       'RSR',
                     i.origin||'-'|| i.destination||','||M.BREAK_POINT ||'Freight Rates and Surcharges',
						       M.SELLRATE_ID,
                    i.buyrateid,
						       i.lane_no,M.Version_No ,
						       v_newversionno );

					  ELSE
            v_changedesc1 :=M.BREAK_POINT||','||p_Origin||'-'||p_Destination||', Sea Freight Rates and Surcharges';
             SELECT CHANGEDESC INTO V_CHANGEDESC FROM  QMS_QUOTES_UPDATED
					  WHERE  QUOTE_ID = M.QUOTEID
						 AND SELL_BUY_FLAG = 'RSR'
						 AND CONFIRM_FLAG IS NULL;
             --If v_changedesc1=V_CHANGEDESC -- @@ commented and added by subrahmanyam for 187878
             dbms_output.put_line(V_CHANGEDESC);
             dbms_output.put_line(INSTR(v_changedesc1,M.BREAK_POINT));
             IF (INSTR(v_changedesc1,M.BREAK_POINT)<>0)
             THEN
                V_CHANGEDESC :=V_CHANGEDESC;
              ELSE
                V_CHANGEDESC := M.BREAK_POINT || ',' ||V_CHANGEDESC;
              END IF;
              dbms_output.put_line('2'||V_CHANGEDESC);
             UPDATE  QMS_QUOTES_UPDATED SET CHANGEDESC= V_CHANGEDESC,new_Sellcharge_id= p_Newsellrateid,new_version_no=v_newversionno WHERE  QUOTE_ID = M.QUOTEID
						 AND SELL_BUY_FLAG = 'RSR'
						 AND CONFIRM_FLAG IS NULL;
            END IF;


				    END LOOP;

 ELSE
        SELECT valid_upto,transit_time INTO v_validupto,v_transittime FROM QMS_BUYRATES_DTL WHERE buyrateid = i.buyrateid AND lane_no=i.lane_no AND version_no=v_newversionno AND line_no='0';

        pkg_qms_buyrates.qms_update_new_quote( v_sellbuyflag,

                                               p_origin,

                                               p_destination,

                                               p_Serv_Level,

                                               p_Carrier,

                                               v_validupto,

                                               p_frequency,

                                               v_transittime,

                                                i.Buyrateid,

                                                p_Newsellrateid,

                                                i.rec_con_id,

                                               v_newversionno,

                                               i.lane_no,
                                               p_Current_Terminal_Id);

      END IF;

      IF v_countupdate =0  AND  p_Shipmentmode='2' AND p_Wet_Break = 'LIST'
      THEN
      SELECT valid_upto,transit_time INTO v_validupto,v_transittime FROM QMS_BUYRATES_DTL WHERE buyrateid = i.buyrateid AND lane_no=i.lane_no AND version_no=v_newversionno AND line_no='0';

        pkg_qms_buyrates.qms_update_new_quote( v_sellbuyflag,

                                               p_origin,

                                               p_destination,

                                               p_Serv_Level,

                                               p_Carrier,

                                               v_validupto,

                                               p_frequency,

                                               v_transittime,

                                                i.Buyrateid,

                                                p_Newsellrateid,

                                                i.rec_con_id,

                                               v_newversionno,

                                               i.lane_no,
                                               p_Current_Terminal_Id);
       END IF;
          UPDATE QMS_REC_CON_SELLRATESDTL Rd
           SET Rd.Ai_Flag = 'I'
         WHERE Rd.Rec_Con_Id = i.Rec_Con_Id
           AND Rd.Buyrateid = i.Buyrateid
           AND Rd.Lane_No = i.Lane_No
           /*And Rd.Version_No<v_newversionno*/
           AND Rd.AI_flag='A';
      END LOOP;
     ELSIF p_operation='Modify'
  THEN
          FOR i IN (SELECT  b.Rec_Con_Id,
                                b.Buyrateid,
                                b.Lane_No,
                                b.Origin,
                                b.version_no,      --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                b.Destination

                  FROM QMS_REC_CON_SELLRATESMASTER a,
                       QMS_REC_CON_SELLRATESDTL    b
                 WHERE b.Rec_Con_Id = a.Rec_Con_Id
                   AND b.Origin = p_Origin
                   AND b.Destination = p_Destination
                   AND b.Servicelevel_Id = p_Serv_Level
                   AND b.Carrier_Id = p_Carrier
                   AND b.line_no=0
                  /* And b.Frequency = p_Frequency*/
                   /*And b.Rec_Con_Id In
                       (Select Dtl.Rec_Con_Id
                          From Qms_Rec_Con_Sellratesmaster Mas,
                               Qms_Rec_Con_Sellratesdtl    Dtl
                         Where Mas.Rc_Flag = 'R'
                           And Mas.Rec_Con_Id = Dtl.Rec_Con_Id)*/
                   AND a.Weight_Break = p_Wet_Break
                   AND a.Rate_Type = p_Rate_Type
                   AND a.Shipment_Mode = p_Shipmentmode
                   AND b.Ai_Flag = 'A' AND a.Terminalid IN
                       (SELECT DISTINCT Terminalid FROM FS_FR_TERMINALMASTER))
                   LOOP

          Qms_Quotepack_New.Qms_Quote_Update(i.Rec_Con_Id,
                                             i.Buyrateid,
                                             i.Lane_No,
                                              i.version_no ,         --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                             -- v_newversionno,--@@ Commented and added below line by subrahmanyam for the updated report Issue.
                                             i.version_no,
                                             p_Newsellrateid,
                                             i.Buyrateid,
                                             i.Lane_No,
                                             'RSR',
                                             NULL,
                                             NULL,
                                             i.Origin || '-' ||
                                             i.Destination || ',' ||
                                             v_Shipmodestr ||
                                             ' Freight Rates and Surcharges');


        UPDATE QMS_REC_CON_SELLRATESDTL Rd
           SET Rd.Ai_Flag = 'I'
         WHERE Rd.Rec_Con_Id = i.Rec_Con_Id
           AND Rd.Buyrateid = i.Buyrateid
           AND Rd.Lane_No = i.Lane_No
           /*And Rd.Version_No<v_newversionno*/
           AND Rd.AI_flag='A';
      END LOOP;
      END IF;
    ELSE
     IF p_operation='Acceptance'
        THEN
      FOR i IN (SELECT  b.Rec_Con_Id_sell Rec_Con_Id,
                                b.Buyrateid,
                                b.Lane_No,
                                b.Origin,
                                b.version_no,      --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                b.Destination

                  FROM QMS_REC_CON_SELLRATESMSTR_ACC a,
                       QMS_REC_CON_SELLRATESDTL_ACC    b
                 WHERE b.Rec_Con_Id = a.Rec_Con_Id
                   AND b.Origin = p_Origin
                   AND b.Destination = p_Destination
                   AND b.Servicelevel_Id = p_Serv_Level
                   AND b.Carrier_Id = p_Carrier
                   AND b.line_no=0
                  /* And b.Frequency = p_Frequency*/
                   /*And b.Rec_Con_Id In
                       (Select Dtl.Rec_Con_Id
                          From Qms_Rec_Con_Sellratesmaster Mas,
                               Qms_Rec_Con_Sellratesdtl    Dtl
                         Where Mas.Rc_Flag = 'R'
                           And Mas.Rec_Con_Id = Dtl.Rec_Con_Id)*/
                   AND a.Weight_Break = p_Wet_Break
                   AND a.Rate_Type = p_Rate_Type
                   AND a.Shipment_Mode = p_Shipmentmode
                   AND b.Ai_Flag = 'A'
                 /*  And a.Terminalid In
                       (Select Child_Terminal_Id Terminalid
                          From Fs_Fr_Terminal_Regn
                        Connect By Prior
                                    Child_Terminal_Id = Parent_Terminal_Id
                         Start With Parent_Terminal_Id =
                                    p_Current_Terminal_Id
                        Union
                        Select Terminalid
                          From Fs_Fr_Terminalmaster
                         Where Terminalid = p_Current_Terminal_Id)*/) LOOP

--select max(version_no) into v_newversionno from qms_buyrates_dtl where buyrateid=i.buyrateid and lane_no=i.lane_no and line_no='0';
  v_newversionno :=i.version_no;
        v_countupdate :=0;
        v_count       :=0;
        v_changedesc  :='';


       IF p_Shipmentmode='2' AND p_Wet_Break = 'LIST'
        THEN

                FOR M IN (SELECT t2.* FROM (SELECT QR.QUOTE_ID QUOTEID,Qr.Version_No,QR.BREAK_POINT BREAK_POINT,BUY_RATE,SELLRATE_ID ,BUYRATE_ID,RATE_LANE_NO,(SELECT 'X' FROM QMS_QUOTE_MASTER WHERE ID=QR.QUOTE_ID AND ACTIVE_FLAG='A')t1
					      FROM   QMS_QUOTE_RATES QR,QMS_BUYRATES_DTL QBD
				       	WHERE    QR.BUYRATE_ID = i.Buyrateid
						    AND    QR.RATE_LANE_NO = i.lane_no
                 AND QR.SELL_BUY_FLAG='RSR'
				        AND  BUYRATEID=QR.BUYRATE_ID AND LANE_NO=QR.RATE_LANE_NO AND QBD.VERSION_NO=v_newversionno AND WEIGHT_BREAK_SLAB=QR.BREAK_POINT AND CHARGERATE<>QR.BUY_RATE)t2 WHERE t1 IS NOT NULL

                )
				    LOOP
        	 BEGIN
           v_countupdate :=v_countupdate+1;
            SELECT COUNT(quote_id) INTO   V_COUNTQUOTE
              FROM
             QMS_QUOTES_UPDATED QU WHERE  QUOTE_ID = M.QUOTEID	   AND QU.SELL_BUY_FLAG = 'RSR'

             	 AND CONFIRM_FLAG IS NULL;
               EXCEPTION WHEN NO_DATA_FOUND
               THEN
               V_COUNTQUOTE :=0;
               END;

					  IF V_COUNTQUOTE = 0 THEN

						INSERT INTO QMS_QUOTES_UPDATED(QUOTE_ID,new_sellcharge_id,New_Buycharge_Id,New_Lane_No,sell_buy_flag,changedesc,old_sellcharge_id,old_buycharge_id,old_lane_no,old_version_no,new_version_no)
						VALUES
						      (M.QUOTEID,
                 p_Newsellrateid,
						       i.buyrateid, i.lane_no,
						       'RSR',
                     i.origin||'-'|| i.destination||','||M.BREAK_POINT ||'Freight Rates and Surcharges',
						       M.SELLRATE_ID,
                    i.buyrateid,
						       i.lane_no,M.Version_No ,
						       v_newversionno );

					  ELSE
            v_changedesc1 :=M.BREAK_POINT||','||p_Origin||'-'||p_Destination||', Sea Freight Rates and Surcharges';
             SELECT CHANGEDESC INTO V_CHANGEDESC FROM  QMS_QUOTES_UPDATED
					  WHERE  QUOTE_ID = M.QUOTEID
						 AND SELL_BUY_FLAG = 'RSR'
						 AND CONFIRM_FLAG IS NULL;
             --If v_changedesc1=V_CHANGEDESC --@@ commented and added by subrahmanyam for 187878
             IF( INSTR(v_changedesc1,M.BREAK_POINT)<>0)
             THEN
                V_CHANGEDESC :=V_CHANGEDESC;
              ELSE
                V_CHANGEDESC := M.BREAK_POINT || ',' ||V_CHANGEDESC;
              END IF;
             UPDATE  QMS_QUOTES_UPDATED SET CHANGEDESC= V_CHANGEDESC,new_Sellcharge_id= p_Newsellrateid,new_version_no=v_newversionno WHERE  QUOTE_ID = M.QUOTEID
						 AND SELL_BUY_FLAG = 'RSR'
						 AND CONFIRM_FLAG IS NULL;
            END IF;


				    END LOOP;

 ELSE
        SELECT valid_upto,transit_time INTO v_validupto,v_transittime FROM QMS_BUYRATES_DTL WHERE buyrateid = i.buyrateid AND lane_no=i.lane_no AND version_no=v_newversionno AND line_no='0';

        pkg_qms_buyrates.qms_update_new_quote( v_sellbuyflag,

                                               p_origin,

                                               p_destination,

                                               p_Serv_Level,

                                               p_Carrier,

                                               v_validupto,

                                               p_frequency,

                                               v_transittime,

                                                i.Buyrateid,

                                                p_Newsellrateid,

                                                i.rec_con_id,

                                               v_newversionno,

                                               i.lane_no,
                                               p_Current_Terminal_Id);

             END IF;
       IF v_countupdate =0  AND  p_Shipmentmode='2' AND p_Wet_Break = 'LIST'
      THEN
      SELECT valid_upto,transit_time INTO v_validupto,v_transittime FROM QMS_BUYRATES_DTL WHERE buyrateid = i.buyrateid AND lane_no=i.lane_no AND version_no=v_newversionno AND line_no='0';

        pkg_qms_buyrates.qms_update_new_quote( v_sellbuyflag,

                                               p_origin,

                                               p_destination,

                                               p_Serv_Level,

                                               p_Carrier,

                                               v_validupto,

                                               p_frequency,

                                               v_transittime,

                                                i.Buyrateid,

                                                p_Newsellrateid,

                                                i.rec_con_id,

                                               v_newversionno,

                                               i.lane_no,
                                               p_Current_Terminal_Id);

      END IF;
      UPDATE QMS_REC_CON_SELLRATESDTL Rd
           SET Rd.Ai_Flag = 'I'
         WHERE Rd.Rec_Con_Id = i.rec_con_id
           AND Rd.Buyrateid = i.Buyrateid
           AND Rd.Lane_No = i.Lane_No
            /*  And Rd.Version_No<v_newversionno*/
           AND Rd.Ai_flag='A';
      END LOOP;
  ELSIF p_operation='Modify'
  THEN
   FOR i IN (SELECT  b.Rec_Con_Id,
                                b.Buyrateid,
                                b.Lane_No,
                                b.Origin,
                                b.version_no,      --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                b.Destination

                  FROM QMS_REC_CON_SELLRATESMASTER a,
                       QMS_REC_CON_SELLRATESDTL    b
                 WHERE b.Rec_Con_Id = a.Rec_Con_Id
                   AND b.Origin = p_Origin
                   AND b.Destination = p_Destination
                   AND b.Servicelevel_Id = p_Serv_Level
                   AND b.Carrier_Id = p_Carrier
                   AND b.line_no=0
                  /* And b.Frequency = p_Frequency*/
                   /*And b.Rec_Con_Id In
                       (Select Dtl.Rec_Con_Id
                          From Qms_Rec_Con_Sellratesmaster Mas,
                               Qms_Rec_Con_Sellratesdtl    Dtl
                         Where Mas.Rc_Flag = 'R'
                           And Mas.Rec_Con_Id = Dtl.Rec_Con_Id)*/
                   AND a.Weight_Break = p_Wet_Break
                   AND a.Rate_Type = p_Rate_Type
                   AND a.Shipment_Mode = p_Shipmentmode
                   AND b.Ai_Flag = 'A'
                 /*  And a.Terminalid In
                       (Select Child_Terminal_Id Terminalid
                          From Fs_Fr_Terminal_Regn
                        Connect By Prior
                                    Child_Terminal_Id = Parent_Terminal_Id
                         Start With Parent_Terminal_Id =
                                    p_Current_Terminal_Id
                        Union
                        Select Terminalid
                          From Fs_Fr_Terminalmaster
                         Where Terminalid = p_Current_Terminal_Id)*/) LOOP

          SELECT version_no INTO v_oldversionno FROM QMS_REC_CON_SELLRATESDTL WHERE rec_con_id=i.rec_con_id AND buyrateid=i.Buyrateid AND lane_no=i.lane_no AND line_no=0;
          Qms_Quotepack_New.Qms_Quote_Update(i.Rec_Con_Id,
                                             i.Buyrateid,
                                             i.Lane_No,
                                              v_oldversionno ,         --@@Added by Kameswari for the WPBN issue-146448 on 22/1208
                                              v_oldversionno,
                                             p_Newsellrateid,
                                             i.Buyrateid,
                                             i.Lane_No,
                                             'RSR',
                                             NULL,
                                             NULL,
                                             i.Origin || '-' ||
                                             i.Destination || ',' ||
                                             v_Shipmodestr ||
                                             ' Freight Rates and Surcharges');
          UPDATE QMS_REC_CON_SELLRATESDTL Rd
           SET Rd.Ai_Flag = 'I'
         WHERE Rd.Rec_Con_Id = i.Rec_Con_Id
           AND Rd.Buyrateid = i.Buyrateid
           AND Rd.Lane_No = i.Lane_No
           /*And Rd.Version_No<v_newversionno*/
           AND Rd.AI_flag='A';
      END LOOP;

  END IF;

    END IF;
    RETURN 6;
  EXCEPTION
    WHEN OTHERS THEN
      Dbms_Output.Put_Line(SQLERRM);
      Qms_Rsr_Rates_Pkg.g_Err      := '<< ' || SQLERRM || ' >>';
      Qms_Rsr_Rates_Pkg.g_Err_Code := '<< ' || SQLCODE || ' >>';
      INSERT INTO QMS_OBJECTS_ERRORS
        (Ex_Date, Module_Name, Errorcode, Errormessage)
      VALUES
        (SYSDATE,
         'Qms_Rsr_Rates_Pkg->Seperator',
         Qms_Rsr_Rates_Pkg.g_Err_Code,
         Qms_Rsr_Rates_Pkg.g_Err);
      COMMIT;
      RETURN 0;
  END;


  /*  This function checks for the hierarchy validation required as per
   *  the data access-business rules doc in the repository.
   *  p_origin                VARCHAR2;
   *  p_destination           VARCHAR2;
   *  p_serv_level            VARCHAR2;
   *  p_carrier               VARCHAR2;
   *  p_frequency             VARCHAR2;
   *  p_current_terminal_id   VARCHAR2;
   *  p_wet_break             VARCHAR2;
   *  p_rate_type             VARCHAR2;
   *  p_shipmentmode          VARCHAR2;
   *  RETURN                  VARCHAR2;
  */
  FUNCTION Validate_Sellrate(p_Origin              VARCHAR2,
                             p_Destination         VARCHAR2,
                             p_Serv_Level          VARCHAR2,
                             p_Carrier             VARCHAR2,
                             p_Frequency           VARCHAR2,
                             p_Current_Terminal_Id VARCHAR2,
                             p_Weight_Break        VARCHAR2,
                             p_Rate_Type           VARCHAR2,
                             p_Shipmentmode        VARCHAR2) RETURN VARCHAR2 IS
    v_Origin VARCHAR2(50);
  BEGIN
    SELECT DISTINCT b.Origin Origin
      INTO v_Origin
      FROM QMS_REC_CON_SELLRATESMASTER a, QMS_REC_CON_SELLRATESDTL b
     WHERE b.Rec_Con_Id = a.Rec_Con_Id
       AND b.Servicelevel_Id = p_Serv_Level
       AND b.Carrier_Id = p_Carrier
       AND b.Ai_Flag = 'A'
       AND b.Invalidate = 'F'
       AND b.Origin = p_Origin
       AND b.Destination = p_Destination
       AND b.Frequency = p_Frequency
       AND a.Weight_Break = p_Weight_Break
       AND a.Rate_Type = p_Rate_Type
       AND a.Shipment_Mode = p_Shipmentmode
       AND a.Rc_Flag = 'R'
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
    IF SQL%FOUND THEN
      RETURN 'TRUE';
    ELSE
      RETURN 'FALSE';
    END IF;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      RETURN 'FALSE';
    WHEN OTHERS THEN
      RETURN 'FALSE';
  END;

END;

/

/

--------------------------------------------------------
--  DDL for Package Body QMS_QUOTE_PACK
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_QUOTE_PACK" AS

  /*
  This procedure is used for fetching the BR/RSR/CSR for different weight breaks for Quote 2nd Screen.
  It Returns a resultset object.
  The IN Parameters are:
   p_org_loc            VARCHAR2,
   p_dest_loc           VARCHAR2,
   p_terminal           VARCHAR2,
   p_srvlevel           VARCHAR2,
   p_shmode             VARCHAR2,
   p_permission         VARCHAR2 DEFAULT 'Y',
   p_operation          VARCHAR2,
   p_quote_id           VARCHAR2
  The OUT Parameter is:
   p_rs           OUT   resultset
  */

  PROCEDURE MultiLane_Buryates_dtl_Proc(P_RS OUT RESULTSET) AS
    v_count                  NUMBER;
    v_count1                 NUMBER;
    V_Temp_Count             NUMBER;
    v_console_type           VARCHAR2(100);
    v_TTR                    VARCHAR2(100);
    V_WEIGHT_BREAK_SLAB      VARCHAR2(2000);
    V_BUY_RATE               VARCHAR2(2000);
    V_SELL_RATE              VARCHAR2(2000);
    V_MARGIN_TYPE            VARCHAR2(2000);
    V_MARGIN_VALUE           VARCHAR2(2000);
    V_CHARGE_BASIS           VARCHAR2(2000);
    V_SERVICE_LEVEL          VARCHAR2(2000);
    V_RATE_DESCRIPTION       VARCHAR2(2000);
    V_CHANGE_FLAG            VARCHAR2(2000);
    V_WEIGHT_BREAK           VARCHAR2(2000);
    V_RATE_TYPE              VARCHAR2(2000);
    V_SHMODE                 VARCHAR2(10);
    V_line_no                VARCHAR2(2000);
    V_MARGIN_DISCOUNT_FLAG   VARCHAR2(2000);
    V_TEMP_WEIGHT_BREAK_SLAB VARCHAR2(2000);
    V_TEMP_BUY_RATE          VARCHAR2(2000);
    V_TEMP_SELL_RATE         VARCHAR2(2000);
    V_TEMP_MARGIN_TYPE       VARCHAR2(2000);
    V_TEMP_MARGIN_VALUE      VARCHAR2(2000);
    V_TEMP_CHARGE_BASIS      VARCHAR2(2000);
    V_TEMP_SERVICE_LEVEL     VARCHAR2(2000);
    V_TEMP_RATE_DESCRIPTION  VARCHAR2(2000);
    V_TEMP_CHANGE_FLAG       VARCHAR2(2000);
    V_TEMP_WEIGHT_BREAK      VARCHAR2(2000);
    V_TEMP_RATE_TYPE         VARCHAR2(2000);
    V_temp_line_no           VARCHAR2(2000);
    V_CHECKED_FLAG          VARCHAR2(2000);
    V_TEMP_CHECKED_FLAG          VARCHAR2(2000);

    V_temp_MARGIN_DISCOUNT_FLAG VARCHAR2(2000);
    v_surcharge_count           NUMBER;
    V_SQL_SUR                   VARCHAR2(4000) := '';
    V_SQL1                      VARCHAR2(4000) := '';
    V_RC_C1                     RESULTSET;
    V_OPERATION                 VARCHAR2(50);
    VIEW_COUNT               NUMBER;
    V_TEMP1_COUNT               NUMBER;--Added by Anil.k for Spot Rates
    V_LowerBound                NUMBER(20,5);--Added by Anil.k for Spot Rates
    V_UpperBound                NUMBER(20,5);--Added by Anil.k for Spot Rates
  BEGIN
    v_count1 := 0;
    V_Temp_Count :=0;
    v_count  := 0;
    V_SHMODE := '';
    VIEW_COUNT := 0;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_MULTI_LANE_TEMP_CHARGES');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_MULTI_LANE_CHARGES_FINAL');
   -- EXECUTE IMMEDIATE ('TRUNCATE TABLE LANE_TEMP_CHARGES_test');
    EXECUTE IMMEDIATE ('TRUNCATE TABLE  GT_MULTI_LANE_TEMP_DTL');

    BEGIN
      /*for j in (select *
                  from temp_buyratesdtl_proc_table mlane
                /*WHERE mlane.shimpment_mode = 2*/
                /*) loop

        V_SHMODE := j.shimpment_mode;
        V_OPERATION := J.OPERATION;
        QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(J.ORIGIN_LOCATION,
                                                    J.DEST_LOCATION,
                                                    J.SELL_BUY_RATE_ID,
                                                    j.buyrate_id,
                                                    J.LANE_NO,
                                                    J.SALES_PERSON,
                                                    J.TERMINAL_ID,
                                                    J.PERMISSION,
                                                    J.SELLRATE_FLAG,
                                                    J.MARGIN,
                                                    J.OPERATION,
                                                    J.QUOTE_ID,
                                                    J.CUSTOMER_ID,
                                                    J.QUOTE_ORIGIN,
                                                    J.QUOTE_DEST,
                                                    J.CARRIER,
                                                    J.SERVICE_LEVEL,
                                                    J.FREQUENCY,
                                                    J.FREIGHT_TERMINAL,
                                                    J.SHIMPMENT_MODE,
                                                    P_RS);

        select count(*) into v_count from TEMP_CHARGES;
        dbms_output.put_line('total values are:::::::::::::::: ' ||
                             v_count);
        insert into GT_MULTI_LANE_TEMP_CHARGES
          (SELLCHARGEID,
           CHARGE_ID,
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
           ZONE,
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           PRIMARY_BASIS,
           SECONDARY_BASIS,
           TERTIARY_BASIS,
           BLOCK,
           RATE_TYPE,
           WEIGHT_SCALE,
           NOTES,
           FREQUENCY,
           TRANSITTIME,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           SELECTED_FLAG,
           LBOUND,
           UBOUND,
           RATE_INDICATOR,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           MARGIN_TEST_FLAG,
           CHARGE_GROUP_ID,
           SHIPMENTMODE,
           ID,
           RATE_DESCRIPTION,
           CONSOLE_TYPE,
           CARRIER,
           FREQUENCY_CHECKED,
           TRANSITTIME_CHECKED,
           CARRIER_CHECKED,
           RATEVALIDITY_CHECKED,
           VERSION_NO,
           CHANGE_FLAG,
           CHECKED_FLAG,
           QUOTE_ID, -- Added By Kishroe For Compatabilty QuoteGroupExcelReport (temp_charges table columns)
           QUOTE_UNIQUE_ID)
          SELECT * FROM TEMP_CHARGES tc;



        update GT_MULTI_LANE_TEMP_CHARGES TC
           SET TC.ORIGIN_PORT           = J.ORIGIN_LOCATION,
               TC.DEST_PORT             = J.DEST_LOCATION,
               TC.Org                   = J.QUOTE_ORIGIN,
               TC.DEST                  = J.QUOTE_DEST,
               TC.INCO_TERMS            = J.INCO_TERMS,
               TC.FREIGHT_RATE_VALIDITY = J.FREIGHT_RATE_VALIDITY,
               TC.TRANSITTIME           = J.TRANSIT_TIME,
               TC.CARRIER_ID            = J.CARRIER,
               TC.SERVICE_LEVEL_ID      = J.SERVICE_LEVEL,
               TC.Frequency_Checked     = 'on',
               TC.TRANSITTIME_CHECKED   = 'on',
               TC.CARRIER_CHECKED       = 'on',
               TC.RATEVALIDITY_CHECKED  = 'on',
               TC.Rate_Indicator        = ''
         WHERE (TC.BUY_CHARGE_ID = J.BUYRATE_ID OR TC.BUY_CHARGE_ID IS NULL)
           AND TC.SELLCHARGEID = J.SELL_BUY_RATE_ID
           AND TC.LANE_NO = J.LANE_NO;
      end loop; */-- END OF j LOOP
      FOR j IN (SELECT *
                  FROM TEMP_BUYRATESDTL_PROC_TABLE mlane
                /*WHERE mlane.shimpment_mode = 2*/
                ) LOOP

        V_SHMODE := j.shimpment_mode;
        V_OPERATION := J.OPERATION;
        IF( UPPER(V_OPERATION) = 'VIEW' ) THEN

        IF( VIEW_COUNT = 0)THEN

        QMS_QUOTE_PACK.MULTI_QUOTE_DETAILED_VIEW_PROC(J.QUOTE_ID,P_RS);
         INSERT INTO GT_MULTI_LANE_TEMP_CHARGES
          (SELLCHARGEID,
           CHARGE_ID,
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
           ZONE,
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           PRIMARY_BASIS,
           SECONDARY_BASIS,
           TERTIARY_BASIS,
           BLOCK,
           RATE_TYPE,
           WEIGHT_SCALE,
           NOTES,
           FREQUENCY,
           TRANSITTIME,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           SELECTED_FLAG,
           LBOUND,
           UBOUND,
           RATE_INDICATOR,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           MARGIN_TEST_FLAG,
           CHARGE_GROUP_ID,
           SHIPMENTMODE,
           ID,
           RATE_DESCRIPTION,
           CONSOLE_TYPE,
           CARRIER,
           FREQUENCY_CHECKED,
           TRANSITTIME_CHECKED,
           CARRIER_CHECKED,
           RATEVALIDITY_CHECKED,
          -- SERVICE_CHECKED,-- kiran.v
           VERSION_NO,
           CHANGE_FLAG,
           CHECKED_FLAG,
           QUOTE_ID, -- Added By Kishroe For Compatabilty QuoteGroupExcelReport (temp_charges table columns)
           QUOTE_UNIQUE_ID)
          SELECT DISTINCT tc.* FROM TEMP_CHARGES tc;

          VIEW_COUNT := VIEW_COUNT+1;
          END IF;
           UPDATE GT_MULTI_LANE_TEMP_CHARGES TC
           SET TC.ORIGIN_PORT           = J.ORIGIN_LOCATION,
               TC.DEST_PORT             = J.DEST_LOCATION,
               TC.Org                   = J.QUOTE_ORIGIN,
               TC.DEST                  = J.QUOTE_DEST,
               TC.INCO_TERMS            = J.INCO_TERMS,
               TC.FREIGHT_RATE_VALIDITY = J.FREIGHT_RATE_VALIDITY,
               TC.TRANSITTIME           = J.TRANSIT_TIME,
               TC.CARRIER_ID            = J.CARRIER,
               TC.SERVICE_LEVEL_ID      = J.SERVICE_LEVEL,
               TC.MULTIQUOTE_SERIALNO   = J.MULTIQUOTE_SERIALNO,--@Added by govind
             --  TC.Frequency_Checked     = 'on',
             --  TC.TRANSITTIME_CHECKED   = 'on',
             --  TC.CARRIER_CHECKED       = 'on',
              -- TC.RATEVALIDITY_CHECKED  = 'on',
               TC.Rate_Indicator        = '',
               TC.lbound                = 0.00000,
               TC.Ubound                = 0.00000

         WHERE (TC.BUY_CHARGE_ID = J.BUYRATE_ID OR TC.BUY_CHARGE_ID IS NULL)
           AND TC.SELLCHARGEID = NVL(J.SELL_BUY_RATE_ID,J.BUYRATE_ID) OR TC.BUY_CHARGE_ID = NVL(J.SELL_BUY_RATE_ID,J.BUYRATE_ID)
           AND TC.LANE_NO = J.LANE_NO;

        ELSE
        QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(J.ORIGIN_LOCATION,
                                                    J.DEST_LOCATION,
                                                    J.SELL_BUY_RATE_ID,
                                                    j.buyrate_id,
                                                    J.LANE_NO,
                                                    J.SALES_PERSON,
                                                    J.TERMINAL_ID,
                                                    J.PERMISSION,
                                                    J.SELLRATE_FLAG,
                                                    J.MARGIN,
                                                    J.OPERATION,
                                                    J.QUOTE_ID,
                                                    J.CUSTOMER_ID,
                                                    J.QUOTE_ORIGIN,
                                                    J.QUOTE_DEST,
                                                    J.CARRIER,
                                                    J.SERVICE_LEVEL,
                                                    J.FREQUENCY,
                                                    J.FREIGHT_TERMINAL,
                                                    J.SHIMPMENT_MODE,
                                                    P_RS);

        SELECT COUNT(*) INTO v_count FROM TEMP_CHARGES;
        dbms_output.put_line('total values are:::::::::::::::: ' ||
                             v_count);
        INSERT INTO GT_MULTI_LANE_TEMP_CHARGES
          (SELLCHARGEID,
           CHARGE_ID,
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
           ZONE,
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           PRIMARY_BASIS,
           SECONDARY_BASIS,
           TERTIARY_BASIS,
           BLOCK,
           RATE_TYPE,
           WEIGHT_SCALE,
           NOTES,
           FREQUENCY,
           TRANSITTIME,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           SELECTED_FLAG,
           LBOUND,
           UBOUND,
           RATE_INDICATOR,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           MARGIN_TEST_FLAG,
           CHARGE_GROUP_ID,
           SHIPMENTMODE,
           ID,
           RATE_DESCRIPTION,
           CONSOLE_TYPE,
           CARRIER,
           FREQUENCY_CHECKED,
           TRANSITTIME_CHECKED,
           CARRIER_CHECKED,
           RATEVALIDITY_CHECKED,
           --SERVICE_CHECKED,-- kiran.v
           VERSION_NO,
           CHANGE_FLAG,
           CHECKED_FLAG,
           QUOTE_ID, -- Added By Kishroe For Compatabilty QuoteGroupExcelReport (temp_charges table columns)
           QUOTE_UNIQUE_ID)
          SELECT * FROM TEMP_CHARGES tc;


        IF(J.SHIMPMENT_MODE = 2)THEN
        UPDATE GT_MULTI_LANE_TEMP_CHARGES TC SET TC.CHARGEBASIS = DECODE(UPPER(TC.CHARGESLAB),UPPER('CBM'),'Per Container','MIN','Per Shipment','BASIC','Per Shipment' ,TC.CHARGEBASIS );
        ELSE
        UPDATE GT_MULTI_LANE_TEMP_CHARGES TC SET TC.CHARGEBASIS = DECODE(UPPER(TC.CHARGESLAB),'MIN','Per Shipment','BASIC','Per Shipment' ,TC.CHARGEBASIS);

        END IF;
        UPDATE GT_MULTI_LANE_TEMP_CHARGES TC
           SET TC.ORIGIN_PORT           = J.ORIGIN_LOCATION,
               TC.DEST_PORT             = J.DEST_LOCATION,
               TC.Org                   = J.QUOTE_ORIGIN,
               TC.DEST                  = J.QUOTE_DEST,
               TC.INCO_TERMS            = J.INCO_TERMS,
               TC.FREIGHT_RATE_VALIDITY = J.FREIGHT_RATE_VALIDITY,
               TC.TRANSITTIME           = J.TRANSIT_TIME,
               TC.CARRIER_ID            = J.CARRIER,
               TC.SERVICE_LEVEL_ID      = J.SERVICE_LEVEL,
               TC.MULTIQUOTE_SERIALNO   = J.MULTIQUOTE_SERIALNO,--@Added by govind
              -- TC.Frequency_Checked     = 'on',
              -- TC.TRANSITTIME_CHECKED   = 'on',
              -- TC.CARRIER_CHECKED       = 'on',
              -- TC.RATEVALIDITY_CHECKED  = 'on',
               TC.Rate_Indicator        = ''
         WHERE (TC.BUY_CHARGE_ID = J.BUYRATE_ID OR TC.BUY_CHARGE_ID IS NULL)
           AND TC.SELLCHARGEID = J.SELL_BUY_RATE_ID
           AND TC.LANE_NO = J.LANE_NO;
            END IF;
      END LOOP; -- END OF j LOOP

    EXCEPTION
      WHEN OTHERS THEN
        EXECUTE IMMEDIATE ('TRUNCATE TABLE  temp_buyratesdtl_proc_table');
    END;

     --Added by Anil.k for Spot Rates
    --IF( UPPER(V_OPERATION) <> 'VIEW' ) THEN
    SELECT COUNT(1) INTO V_TEMP1_COUNT FROM SPOTRATE_CHARGES_PROC_TABLE;
    IF V_TEMP1_COUNT>0 THEN
    SELECT DISTINCT S.Shmode INTO V_SHMODE FROM SPOTRATE_CHARGES_PROC_TABLE S ;
    IF(V_SHMODE IS NOT NULL ) THEN
    IF V_SHMODE = 2 THEN
        UPDATE SPOTRATE_CHARGES_PROC_TABLE SBR SET SBR.CHARGEBASIS = DECODE(UPPER(SBR.CHARGEBASIS),'CBM','Per Container','MIN','Per Shipment','BASIC','Per Shipment' ,'Per'||SBR.CHARGEBASIS );
        ELSE
        UPDATE SPOTRATE_CHARGES_PROC_TABLE SBR SET SBR.CHARGEBASIS = DECODE(UPPER(SBR.CHARGESLAB),'MIN','Per Shipment','BASIC','Per Shipment' ,'Per'||SBR.CHARGEBASIS);
        END IF;
        UPDATE SPOTRATE_CHARGES_PROC_TABLE SBR SET SBR.CHARGEBASIS = MULTI_QUOTE_BASIS(V_SHMODE,SBR.CONSOLE_TYPE,SBR.CHARGESLAB);


        INSERT INTO GT_MULTI_LANE_TEMP_CHARGES  SELECT * FROM SPOTRATE_CHARGES_PROC_TABLE;

        DELETE FROM SPOTRATE_CHARGES_PROC_TABLE;
    ELSE
        V_SHMODE := '';
    END IF;
    END IF;
    --END IF;
    --Ended by Anil.k for Spot Rates

    SELECT COUNT(1)INTO V_Temp_Count FROM (SELECT DISTINCT SELECTED_FLAG  FROM GT_MULTI_LANE_TEMP_CHARGES);
    /*dbms_output.put_line('total V_RC_Checked_Flag%ROWCOUNT:::::::::::::::: ' ||V_Temp_Count);*/  -- Commented By Kishore For LoadTest
       IF V_Temp_Count >1  THEN
     UPDATE GT_MULTI_LANE_TEMP_CHARGES TC
           SET  TC.CHECKED_FLAG = '';
    END IF;
    FOR J IN (SELECT DISTINCT ML.SELLCHARGEID,
                              ML.BUY_CHARGE_ID,
                              ML.LANE_NO,
                              ML.VERSION_NO,
                              ML.WEIGHT_BREAK,
                              ML.RATE_TYPE
                FROM GT_MULTI_LANE_TEMP_CHARGES ML
               WHERE ML.RATE_DESCRIPTION LIKE 'A FREIGHT RATE') LOOP
      INSERT INTO GT_MULTI_LANE_TEMP_DTL
        (SELLCHARGEID,
         BUY_CHARGE_ID,
         LANE_NO,
         VERSION_NO,
         WEIGHT_BREAK,
         RATE_TYPE)
      VALUES
        (J.SELLCHARGEID,
         J.BUY_CHARGE_ID,
         J.LANE_NO,
         J.VERSION_NO,
         J.WEIGHT_BREAK,
         J.RATE_TYPE);
    END LOOP;
    SELECT COUNT(*) INTO v_count FROM GT_MULTI_LANE_TEMP_CHARGES;
    -- dbms_output.put_line('GT_MULTI_LANE_TEMP_CHARGES:::::::::: ' ||
    --   v_count);
    SELECT COUNT(*) INTO v_count FROM GT_MULTI_LANE_TEMP_DTL;
    -- dbms_output.put_line('GT_MULTI_LANE_TEMP_DTL::::::::::::::: ' ||
    --    v_count);
    EXECUTE IMMEDIATE ('TRUNCATE TABLE  temp_buyratesdtl_proc_table');
    EXECUTE IMMEDIATE ('INSERT INTO GT_MULTI_LANE_CHARGES_FINAL SELECT * FROM GT_MULTI_LANE_TEMP_CHARGES ');
  -- EXECUTE IMMEDIATE ('INSERT INTO LANE_TEMP_CHARGES_TEST SELECT * FROM GT_MULTI_LANE_TEMP_CHARGES ');

    SELECT COUNT(*) INTO v_count FROM GT_MULTI_LANE_CHARGES_FINAL;
    -- dbms_output.put_line('temp_test::::::::::::::: ' ||   v_count);

    FOR J IN (SELECT DISTINCT ML.SELLCHARGEID,
                              ML.BUY_CHARGE_ID,
                              ML.LANE_NO,
                              ML.VERSION_NO,
                              ML.WEIGHT_BREAK,
                              ML.RATE_TYPE
                FROM GT_MULTI_LANE_TEMP_DTL ML) LOOP
      dbms_output.put_line(j.sellchargeid);
      V_WEIGHT_BREAK_SLAB         := '';
      V_BUY_RATE                  := '';
      V_SELL_RATE                 := '';
      V_MARGIN_TYPE               := '';
      V_MARGIN_VALUE              := '';
      V_CHARGE_BASIS              := '';
      V_SERVICE_LEVEL             := '';
      V_RATE_DESCRIPTION          := '';
      V_CHANGE_FLAG               := '';
      V_WEIGHT_BREAK              := '';
      V_RATE_TYPE                 := '';
      V_MARGIN_DISCOUNT_FLAG      := '';
      V_CHECKED_FLAG              := '';
      V_TEMP_WEIGHT_BREAK_SLAB    := '';
      V_TEMP_BUY_RATE             := '';
      V_TEMP_SELL_RATE            := '';
      V_TEMP_MARGIN_TYPE          := '';
      V_TEMP_MARGIN_VALUE         := '';
      V_TEMP_CHARGE_BASIS         := '';
      V_TEMP_SERVICE_LEVEL        := '';
      V_TEMP_RATE_DESCRIPTION     := '';
      V_TEMP_CHANGE_FLAG          := '';
      V_TEMP_WEIGHT_BREAK         := '';
      V_TEMP_RATE_TYPE            := '';
      V_TEMP_CHECKED_FLAG         := '';
      V_temp_MARGIN_DISCOUNT_FLAG := '';
      v_surcharge_count           := 0;
      DBMS_OUTPUT.put_line('');

      IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1) = FALSE THEN
        BEGIN
          EXECUTE IMMEDIATE ('select  CHARGESLAB,to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,
      CHARGEBASIS,RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO ,NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''-'') FROM GT_MULTI_LANE_TEMP_CHARGES WHERE  SELLCHARGEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND UPPER(CHARGESLAB)=''BASIC'' ')
            INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
            USING J.SELLCHARGEID, J.LANE_NO;

          V_WEIGHT_BREAK_SLAB    := V_TEMP_WEIGHT_BREAK_SLAB;
          V_BUY_RATE             := V_TEMP_BUY_RATE;
          V_SELL_RATE            := V_TEMP_SELL_RATE;
          V_MARGIN_TYPE          := V_TEMP_MARGIN_TYPE;
          V_MARGIN_VALUE         := V_TEMP_MARGIN_VALUE;
          V_CHARGE_BASIS         := V_TEMP_CHARGE_BASIS;
          V_SERVICE_LEVEL        := V_TEMP_SERVICE_LEVEL;
          V_RATE_DESCRIPTION     := V_TEMP_RATE_DESCRIPTION;
          V_CHANGE_FLAG          := V_TEMP_CHANGE_FLAG;
          V_WEIGHT_BREAK         := V_TEMP_WEIGHT_BREAK;
          V_RATE_TYPE            := V_TEMP_RATE_TYPE;
          V_MARGIN_DISCOUNT_FLAG := V_temp_MARGIN_DISCOUNT_FLAG;
          V_CHECKED_FLAG         := V_TEMP_CHECKED_FLAG;
          v_line_no              := V_temp_line_no;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count + 1;

            V_WEIGHT_BREAK_SLAB    := 'BASIC';
            V_BUY_RATE             := '-';
            V_SELL_RATE            := '-';
            V_MARGIN_TYPE          := '-';
            V_MARGIN_VALUE         := '-';
            V_CHARGE_BASIS         := 'Per Shipment';
            V_SERVICE_LEVEL        := '-';
            V_RATE_DESCRIPTION     := 'A FREIGHT RATE';
            V_CHANGE_FLAG          := '-';
            V_WEIGHT_BREAK         := '-';
            V_RATE_TYPE            := '-';
            V_MARGIN_DISCOUNT_FLAG := '-';
            V_CHECKED_FLAG         := '-';
            v_line_no              := '-';
          BEGIN
            EXECUTE IMMEDIATE ('select  CHARGESLAB,to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,
      DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO , NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''-'') FROM GT_MULTI_LANE_TEMP_CHARGES WHERE  SELLCHARGEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no >= 0  AND UPPER(CHARGESLAB)=''MIN'' ')
              INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
              USING J.SELLCHARGEID, J.LANE_NO;

            V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                      V_TEMP_WEIGHT_BREAK_SLAB;
            V_BUY_RATE             := V_BUY_RATE || ',' || V_TEMP_BUY_RATE;
            V_SELL_RATE            := V_SELL_RATE || ',' ||
                                      V_TEMP_SELL_RATE;
            V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                      V_TEMP_MARGIN_TYPE;
            V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                      V_TEMP_MARGIN_VALUE;
            V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                      V_TEMP_CHARGE_BASIS;
            V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                      V_TEMP_SERVICE_LEVEL;
            V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                      V_TEMP_RATE_DESCRIPTION;
            V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                      V_TEMP_CHANGE_FLAG;
            V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                      V_TEMP_WEIGHT_BREAK;
            V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                      V_TEMP_RATE_TYPE;
            V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                      V_temp_MARGIN_DISCOUNT_FLAG;
            V_CHECKED_FLAG        := V_CHECKED_FLAG || ',' || V_TEMP_CHECKED_FLAG;
            V_line_no              := V_line_no || ',' || V_temp_line_no;

            EXCEPTION WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count+1;
            END;
        END;
        IF v_surcharge_count = 0 THEN
        BEGIN
               EXECUTE IMMEDIATE ('select  CHARGESLAB,to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,
      DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO , NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''-'') FROM GT_MULTI_LANE_TEMP_CHARGES WHERE  SELLCHARGEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=1  AND CHARGESLAB=''MIN'' ')
            INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
            USING J.SELLCHARGEID, J.LANE_NO;

          V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                    V_TEMP_WEIGHT_BREAK_SLAB;
          V_BUY_RATE             := V_BUY_RATE || ',' || V_TEMP_BUY_RATE;
          V_SELL_RATE            := V_SELL_RATE || ',' || V_TEMP_SELL_RATE;
          V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                    V_TEMP_MARGIN_TYPE;
          V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                    V_TEMP_MARGIN_VALUE;
          V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                    V_TEMP_CHARGE_BASIS;
          V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                    V_TEMP_SERVICE_LEVEL;
          V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                    V_TEMP_RATE_DESCRIPTION;
          V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                    V_TEMP_CHANGE_FLAG;
          V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                    V_TEMP_WEIGHT_BREAK;
          V_RATE_TYPE            := V_RATE_TYPE || ',' || V_TEMP_RATE_TYPE;
          V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                    V_temp_MARGIN_DISCOUNT_FLAG;
          V_CHECKED_FLAG        := V_CHECKED_FLAG || ',' || V_TEMP_CHECKED_FLAG ;
          V_line_no              := V_line_no || ',' || V_temp_line_no;
       EXCEPTION WHEN NO_DATA_FOUND THEN
       print_out('HAI');
       END;
        END IF;
      END IF;

      IF (J.WEIGHT_BREAK <> 'FLAT' AND J.WEIGHT_BREAK <> 'Flat') THEN
        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE = 1 THEN
          V_SQL1 := ' AND line_no > 0 AND UPPER(CHARGESLAB) NOT IN(''BASIC'',''MIN'') order by break_slab';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1 THEN
          V_SQL1 := '  order by break_slab';
        ELSE
          V_SQL1 := ' AND line_no > 0 AND UPPER(CHARGESLAB) NOT IN(''BASIC'',''MIN'') order by to_number(break_slab)';
        END IF;
        OPEN V_RC_C1 FOR 'SELECT DISTINCT CHARGESLAB break_slab FROM GT_MULTI_LANE_TEMP_CHARGES WHERE rate_description=''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL1
          USING J.WEIGHT_BREAK;
        LOOP

          FETCH V_RC_C1
            INTO V_TEMP_WEIGHT_BREAK_SLAB;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT distinct to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE, DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO ,NVL(MARGIN_DISCOUNT_FLAG,''-'') , NVL(CHECKED_FLAG,''-'') from GT_MULTI_LANE_TEMP_CHARGES    WHERE CHARGESLAB =:CHARGESLAB
					  AND SELLCHARGEID =:buy_rate_id AND LANE_NO=:lane_no
					 AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
              INTO V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG ,V_TEMP_CHECKED_FLAG
              USING V_TEMP_WEIGHT_BREAK_SLAB, J.SELLCHARGEID, J.LANE_NO;
            IF V_WEIGHT_BREAK_SLAB IS NULL THEN
              V_WEIGHT_BREAK_SLAB    := V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_TEMP_BUY_RATE;
              V_SELL_RATE            := V_TEMP_SELL_RATE;
              V_MARGIN_TYPE          := V_TEMP_MARGIN_TYPE;
              V_MARGIN_VALUE         := V_TEMP_MARGIN_VALUE;
              V_CHARGE_BASIS         := V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_TEMP_SERVICE_LEVEL;
              V_RATE_DESCRIPTION     := V_TEMP_RATE_DESCRIPTION;
              V_CHANGE_FLAG          := V_TEMP_CHANGE_FLAG;
              V_WEIGHT_BREAK         := V_TEMP_WEIGHT_BREAK;
              V_RATE_TYPE            := V_TEMP_RATE_TYPE;
              V_MARGIN_DISCOUNT_FLAG := V_TEMP_MARGIN_DISCOUNT_FLAG ;
              V_CHECKED_FLAG        := V_TEMP_CHECKED_FLAG  ;
              V_line_no              := V_temp_line_no;

            ELSE
              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' ||
                                        V_TEMP_BUY_RATE;
              V_SELL_RATE            := V_SELL_RATE || ',' ||
                                        V_TEMP_SELL_RATE;
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                        V_TEMP_MARGIN_TYPE;
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                        V_TEMP_MARGIN_VALUE;
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                        V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                        V_TEMP_SERVICE_LEVEL;
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                        V_TEMP_RATE_DESCRIPTION;
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                        V_TEMP_CHANGE_FLAG;
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                        V_TEMP_WEIGHT_BREAK;
              V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                        V_TEMP_RATE_TYPE;
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                        V_temp_MARGIN_DISCOUNT_FLAG;
              V_CHECKED_FLAG         := V_CHECKED_FLAG ||',' || V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || V_temp_line_no;
            END IF;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
             IF V_WEIGHT_BREAK_SLAB IS NULL THEN

             V_WEIGHT_BREAK_SLAB    :=  V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             :=  '-';
              V_SELL_RATE            :=  '-';
              V_MARGIN_TYPE          :=  '-';
              V_MARGIN_VALUE         :=  '-';
              V_CHARGE_BASIS         :=  '-';
              V_SERVICE_LEVEL        :=  '-';
              V_RATE_DESCRIPTION     :=  'A FREIGHT RATE';
              V_CHANGE_FLAG          :=  '-';
              V_WEIGHT_BREAK         :=  '-';
              V_RATE_TYPE            :=  '-';
              V_MARGIN_DISCOUNT_FLAG := '-';
              V_CHECKED_FLAG        :=  '-' ;
              V_line_no              :=  '-';

             ELSE
              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' || '-';
              V_SELL_RATE            := V_SELL_RATE || ',' || '-';
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' || '-';
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' || '-';
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' || V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' || '-';
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' || 'A FREIGHT RATE';
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' || '-';
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' || '-';
              V_RATE_TYPE            := V_RATE_TYPE || ',' || '-';
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' || '-';
              V_CHECKED_FLAG        := V_CHECKED_FLAG ||',' || V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || '-';
            END IF;
          END;
        END LOOP;
        CLOSE V_RC_C1;

        V_TEMP_WEIGHT_BREAK_SLAB        := '';
        V_TEMP_BUY_RATE                := '';
        V_TEMP_SELL_RATE               := '';
        V_TEMP_MARGIN_TYPE             := '';
        V_TEMP_MARGIN_VALUE            := '';
        V_TEMP_CHARGE_BASIS            := '';
        V_TEMP_SERVICE_LEVEL           := '';
        V_TEMP_RATE_DESCRIPTION        := '';
        V_TEMP_CHANGE_FLAG             := '';
        V_TEMP_WEIGHT_BREAK            := '';
        V_TEMP_RATE_TYPE               := '';
        V_TEMP_MARGIN_DISCOUNT_FLAG    := '';
        V_TEMP_CHECKED_FLAG            := '';
        V_TEMP_line_no                 := '';

        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE = 1 THEN
          --   V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and SELLCHARGEID='||j.SELLCHARGEID ||' and lane_no='||j.lane_no ||' order by line_no ';
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  ORDER BY RATE_DESCRIPTION,CHARGESLAB ';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1 THEN
          --V_SQL_SUR := ' AND SELLCHARGEID='||J.SELLCHARGEID ||' AND LANE_NO='|| J.LANE_NO ||' order by break_slab ';
          V_SQL_SUR := 'ORDER BY RATE_DESCRIPTION,CHARGESLAB';
        ELSE
          --V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and SELLCHARGEID='||j.SELLCHARGEID ||' and lane_no='||j.lane_no ||' order by line_no';
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' order by  RATE_DESCRIPTION ,
                           DECODE(is_Number(CHARGESLAB) ,''TRUE'',TO_NUMBER(CHARGESLAB), DECODE(SUBSTR(UPPER(CHARGESLAB),6),''BASIC'',TO_NUMBER(1),''MIN'',TO_NUMBER(2),''FLAT'',TO_NUMBER(3),''ABSOLUTE'',TO_NUMBER(4),''PERCENT'',TO_NUMBER(5),DECODE(UPPER(CHARGESLAB),''BASIC'',
                        TO_NUMBER(1),
                        ''MIN'',
                        TO_NUMBER(-9999),
                       ''FLAT'',
                        TO_NUMBER(3),
                        ''ABSOLUTE'',
                        TO_NUMBER(4),
                        ''PERCENT'',TO_NUMBER(5))))';
        END IF;
      -------------Added By govind for Ordering of surcharges when slab or both is taken

        OPEN V_RC_C1 FOR 'SELECT  DISTINCT CHARGESLAB,RATE_DESCRIPTION,CHARGEBASIS,NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
          USING J.WEIGHT_BREAK;
        LOOP
          FETCH V_RC_C1
            INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_RATE_DESCRIPTION, V_TEMP_CHARGE_BASIS, V_TEMP_CHECKED_FLAG ;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT distinct to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,  DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO, NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES   WHERE CHARGESLAB =:weight_break_slab
					  AND SELLCHARGEID =:SELLCHARGEID AND LANE_NO=:lane_no
					   AND RATE_DESCRIPTION=:V_TEMP_RATE_DESCRIPTION
             ')
              INTO V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
              USING V_TEMP_WEIGHT_BREAK_SLAB, J.SELLCHARGEID, J.LANE_NO, V_TEMP_RATE_DESCRIPTION ;

            V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                      V_TEMP_WEIGHT_BREAK_SLAB;
            V_BUY_RATE             := V_BUY_RATE || ',' || V_TEMP_BUY_RATE;
            V_SELL_RATE            := V_SELL_RATE || ',' ||
                                      V_TEMP_SELL_RATE;
            V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                      V_TEMP_MARGIN_TYPE;
            V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                      V_TEMP_MARGIN_VALUE;
            V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                      V_TEMP_CHARGE_BASIS;
            V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                      V_TEMP_SERVICE_LEVEL;
            V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                      V_TEMP_RATE_DESCRIPTION;
            V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                      V_TEMP_CHANGE_FLAG;
            V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                      V_TEMP_WEIGHT_BREAK;
            V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                      V_TEMP_RATE_TYPE;
            V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                      V_temp_MARGIN_DISCOUNT_FLAG;
            V_CHECKED_FLAG        := V_CHECKED_FLAG ||',' || V_TEMP_CHECKED_FLAG ;
            V_line_no              := V_line_no || ',' || V_temp_line_no;

          EXCEPTION
            WHEN NO_DATA_FOUND THEN

              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' || '-';
              V_SELL_RATE            := V_SELL_RATE || ',' || '-';
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' || '-';
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' || '-';
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' || V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' || '-';
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' || '-';
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' || '-';
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' || '-';
              V_RATE_TYPE            := V_RATE_TYPE || ',' || '-';
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' || '-';
              V_CHECKED_FLAG        := V_CHECKED_FLAG ||',' || V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || V_temp_line_no;
          END;

        END LOOP;
        CLOSE V_RC_C1;

      ELSE

      BEGIN
        SELECT CHARGESLAB,
               TO_CHAR(BUYRATE),
               TO_CHAR(SELLRATE),
               NVL(MARGIN_TYPE,'-')MARGIN_TYPE,
               MARGINVALUE,
               CHARGEBASIS,
               RATE_DESCRIPTION,
               SRV_LEVEL,
               CHANGE_FLAG,
               WEIGHT_BREAK,
               RATE_TYPE,
               LINE_NO,
              NVL(MARGIN_DISCOUNT_FLAG,'-')MARGIN_DISCOUNT_FLAG,
              NVL(CHECKED_FLAG,'-')CHECKED_FLAG
          INTO V_TEMP_WEIGHT_BREAK_SLAB,
               V_TEMP_BUY_RATE,
               V_TEMP_SELL_RATE,
               V_TEMP_MARGIN_TYPE,
               V_TEMP_MARGIN_VALUE,
               V_TEMP_CHARGE_BASIS,
               V_TEMP_RATE_DESCRIPTION,
               V_TEMP_SERVICE_LEVEL,
               V_TEMP_CHANGE_FLAG,
               V_TEMP_WEIGHT_BREAK,
               V_TEMP_RATE_TYPE,
               V_temp_line_no,
               V_temp_MARGIN_DISCOUNT_FLAG,
               V_TEMP_CHECKED_FLAG
          FROM GT_MULTI_LANE_TEMP_CHARGES
         WHERE LINE_NO > 0
           AND CHARGESLAB NOT IN ('BASIC', 'MIN')
           AND SELLCHARGEID = J.SELLCHARGEID
           AND LANE_NO = J.LANE_NO
           AND RATE_DESCRIPTION = 'A FREIGHT RATE';

       EXCEPTION
            WHEN NO_DATA_FOUND THEN
        V_TEMP_WEIGHT_BREAK_SLAB :='';
        V_TEMP_BUY_RATE             := '';
        V_TEMP_SELL_RATE            := '' ;
        V_TEMP_MARGIN_TYPE          := '' ;
        V_TEMP_MARGIN_VALUE         := '' ;
        V_TEMP_CHARGE_BASIS         := '' ;
        V_TEMP_SERVICE_LEVEL        := '' ;
        V_TEMP_RATE_DESCRIPTION     := '' ;
        V_TEMP_CHANGE_FLAG          := '' ;
        V_TEMP_WEIGHT_BREAK         := '' ;
        V_TEMP_RATE_TYPE            := '' ;
        V_TEMP_MARGIN_DISCOUNT_FLAG := '' ;
        V_TEMP_CHECKED_FLAG         := '';
        V_TEMP_line_no              := '' ;





           END;

        V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                  V_TEMP_WEIGHT_BREAK_SLAB;
        V_BUY_RATE             := V_BUY_RATE || ',' || V_TEMP_BUY_RATE;
        V_SELL_RATE            := V_SELL_RATE || ',' || V_TEMP_SELL_RATE;
        V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                  V_TEMP_MARGIN_TYPE;
        V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                  V_TEMP_MARGIN_VALUE;
        V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                  V_TEMP_CHARGE_BASIS;
        V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                  V_TEMP_SERVICE_LEVEL;
        V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                  V_TEMP_RATE_DESCRIPTION;
        V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                  V_TEMP_CHANGE_FLAG;
        V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                  V_TEMP_WEIGHT_BREAK;
        V_RATE_TYPE            := V_RATE_TYPE || ',' || V_TEMP_RATE_TYPE;
        V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                  V_temp_MARGIN_DISCOUNT_FLAG;
        V_CHECKED_FLAG         := V_CHECKED_FLAG||','||V_TEMP_CHECKED_FLAG ;
        V_line_no              := V_line_no || ',' || V_TEMP_line_no;

        IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND V_SHMODE = 2 THEN

          V_TEMP_WEIGHT_BREAK_SLAB    := '';
          V_TEMP_BUY_RATE             := '';
          V_TEMP_SELL_RATE            := '';
          V_TEMP_MARGIN_TYPE          := '';
          V_TEMP_MARGIN_VALUE         := '';
          V_TEMP_CHARGE_BASIS         := '';
          V_TEMP_SERVICE_LEVEL        := '';
          V_TEMP_RATE_DESCRIPTION     := '';
          V_TEMP_CHANGE_FLAG          := '';
          V_TEMP_WEIGHT_BREAK         := '';
          V_TEMP_RATE_TYPE            := '';
          V_TEMP_MARGIN_DISCOUNT_FLAG := '';
          V_TEMP_CHECKED_FLAG         := '';
          V_temp_line_no              := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE = 2 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''   ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1 THEN
            --V_SQL_SUR := ' AND SELLCHARGEID='||J.SELLCHARGEID ||' AND LANE_NO='|| J.LANE_NO ||' order by break_slab ';
            V_SQL_SUR := '';
          ELSE
            --V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and SELLCHARGEID='||j.SELLCHARGEID ||' and lane_no='||j.lane_no ||' order by line_no';
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' order by  RATE_DESCRIPTION ,
                           DECODE(is_Number(CHARGESLAB) ,''TRUE'',TO_NUMBER(CHARGESLAB), DECODE(SUBSTR(UPPER(CHARGESLAB),6),''BASIC'',TO_NUMBER(1),''MIN'',TO_NUMBER(2),''FLAT'',TO_NUMBER(3),''ABSOLUTE'',TO_NUMBER(4),''PERCENT'',TO_NUMBER(5),DECODE(UPPER(CHARGESLAB),''BASIC'',
                        TO_NUMBER(1),
                        ''MIN'',
                        TO_NUMBER(-9999),
                       ''FLAT'',
                        TO_NUMBER(3),
                        ''ABSOLUTE'',
                        TO_NUMBER(4),
                        ''PERCENT'',TO_NUMBER(5))))';
          END IF;

          OPEN V_RC_C1 FOR 'SELECT  DISTINCT CHARGESLAB,RATE_DESCRIPTION,CHARGEBASIS,NVL(CHECKED_FLAG,''-'') from GT_MULTI_LANE_TEMP_CHARGES WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_RATE_DESCRIPTION, V_TEMP_CHARGE_BASIS,V_TEMP_CHECKED_FLAG;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT distinct to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,  DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO ,NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES   WHERE CHARGESLAB =:weight_break_slab
					  AND SELLCHARGEID =:SELLCHARGEID AND LANE_NO=:lane_no
					   AND RATE_DESCRIPTION=:V_TEMP_RATE_DESCRIPTION ')
                INTO V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
                USING V_TEMP_WEIGHT_BREAK_SLAB, J.SELLCHARGEID, J.LANE_NO, V_TEMP_RATE_DESCRIPTION;

              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' ||
                                        V_TEMP_BUY_RATE;
              V_SELL_RATE            := V_SELL_RATE || ',' ||
                                        V_TEMP_SELL_RATE;
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                        V_TEMP_MARGIN_TYPE;
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                        V_TEMP_MARGIN_VALUE;
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                        V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                        V_TEMP_SERVICE_LEVEL;
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                        V_TEMP_RATE_DESCRIPTION;
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                        V_TEMP_CHANGE_FLAG;
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                        V_TEMP_WEIGHT_BREAK;
              V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                        V_TEMP_RATE_TYPE;
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                        V_temp_MARGIN_DISCOUNT_FLAG;
              V_CHECKED_FLAG        := V_CHECKED_FLAG||','||V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || V_temp_line_no;

            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                          V_TEMP_WEIGHT_BREAK_SLAB;
                V_BUY_RATE             := V_BUY_RATE || ',' || '-';
                V_SELL_RATE            := V_SELL_RATE || ',' || '-';
                V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' || '-';
                V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' || '-';
                V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' || V_TEMP_CHARGE_BASIS;
                V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' || '-';
                V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' || '-';
                V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' || '-';
                V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' || '-';
                V_RATE_TYPE            := V_RATE_TYPE || ',' || '-';
                V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' || '-';
                V_CHECKED_FLAG        := V_CHECKED_FLAG||','|| V_TEMP_CHECKED_FLAG ;
                V_line_no              := V_line_no || ',' || '-';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF V_SHMODE = 1 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          V_TEMP_WEIGHT_BREAK_SLAB    := '';
          V_TEMP_BUY_RATE             := '';
          V_TEMP_SELL_RATE            := '';
          V_TEMP_MARGIN_TYPE          := '';
          V_TEMP_MARGIN_VALUE         := '';
          V_TEMP_CHARGE_BASIS         := '';
          V_TEMP_SERVICE_LEVEL        := '';
          V_TEMP_RATE_DESCRIPTION     := '';
          V_TEMP_CHANGE_FLAG          := '';
          V_TEMP_WEIGHT_BREAK         := '';
          V_TEMP_RATE_TYPE            := '';
          V_TEMP_MARGIN_DISCOUNT_FLAG := '';
          V_TEMP_CHECKED_FLAG              := '';
          V_TEMP_line_no              := '';

          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' order by  RATE_DESCRIPTION ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1 THEN
            --V_SQL_SUR := ' AND SELLCHARGEID='||J.SELLCHARGEID ||' AND LANE_NO='|| J.LANE_NO ||' order by break_slab ';
            V_SQL_SUR := 'order by  RATE_DESCRIPTION ';
          ELSE
            --V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and SELLCHARGEID='||j.SELLCHARGEID ||' and lane_no='||j.lane_no ||' order by line_no';
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' order by  RATE_DESCRIPTION ,
                           DECODE(is_Number(CHARGESLAB) ,''TRUE'',TO_NUMBER(CHARGESLAB), DECODE(SUBSTR(UPPER(CHARGESLAB),6),''BASIC'',TO_NUMBER(1),''MIN'',TO_NUMBER(2),''FLAT'',TO_NUMBER(3),''ABSOLUTE'',TO_NUMBER(4),''PERCENT'',TO_NUMBER(5),DECODE(UPPER(CHARGESLAB),''BASIC'',
                        TO_NUMBER(1),
                        ''MIN'',
                        TO_NUMBER(-9999),
                       ''FLAT'',
                        TO_NUMBER(3),
                        ''ABSOLUTE'',
                        TO_NUMBER(4),
                        ''PERCENT'',TO_NUMBER(5))))';
          END IF;

          OPEN V_RC_C1 FOR 'SELECT  DISTINCT CHARGESLAB,RATE_DESCRIPTION,CHARGEBASIS,NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_RATE_DESCRIPTION, V_TEMP_CHARGE_BASIS, V_TEMP_CHECKED_FLAG;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN
              dbms_output.put_line('V_TEMP_WEIGHT_BREAK_SLAB : ' ||
                                   V_TEMP_WEIGHT_BREAK_SLAB);
              dbms_output.put_line('V_TEMP_RATE_DESCRIPTION : ' ||
                                   V_TEMP_RATE_DESCRIPTION);
              EXECUTE IMMEDIATE (' SELECT distinct to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,  DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE,LINE_NO ,NVL(MARGIN_DISCOUNT_FLAG,''-'') ,NVL(CHECKED_FLAG,''N'')  from GT_MULTI_LANE_TEMP_CHARGES   WHERE CHARGESLAB =:weight_break_slab
					  AND SELLCHARGEID =:SELLCHARGEID AND LANE_NO=:lane_no
					   AND RATE_DESCRIPTION=:V_TEMP_RATE_DESCRIPTION ')
                INTO V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG ,V_TEMP_CHECKED_FLAG
                USING V_TEMP_WEIGHT_BREAK_SLAB, J.SELLCHARGEID, J.LANE_NO, V_TEMP_RATE_DESCRIPTION;

              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' ||
                                        V_TEMP_BUY_RATE;
              V_SELL_RATE            := V_SELL_RATE || ',' ||
                                        V_TEMP_SELL_RATE;
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                        V_TEMP_MARGIN_TYPE;
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                        V_TEMP_MARGIN_VALUE;
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                        V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                        V_TEMP_SERVICE_LEVEL;
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                        V_TEMP_RATE_DESCRIPTION;
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                        V_TEMP_CHANGE_FLAG;
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                        V_TEMP_WEIGHT_BREAK;
              V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                        V_TEMP_RATE_TYPE;
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                        V_temp_MARGIN_DISCOUNT_FLAG;
             V_CHECKED_FLAG         := V_CHECKED_FLAG||','||V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || V_temp_line_no;

            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                          V_TEMP_WEIGHT_BREAK_SLAB;
                V_BUY_RATE             := V_BUY_RATE || ',' || '-';
                V_SELL_RATE            := V_SELL_RATE || ',' || '-';
                V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' || '-';
                V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' || '-';
                V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' || V_TEMP_CHARGE_BASIS;
                V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' || '-';
                V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' || '-';
                V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' || '-';
                V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' || '-';
                V_RATE_TYPE            := V_RATE_TYPE || ',' || '-';
                V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' || '-';
                V_CHECKED_FLAG        := V_CHECKED_FLAG||','||V_TEMP_CHECKED_FLAG;
                V_line_no              := V_line_no || ',' || '-';
            END;

          END LOOP;
          CLOSE V_RC_C1;
        ELSIF V_SHMODE = 4 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          V_TEMP_WEIGHT_BREAK_SLAB    := '';
          V_TEMP_BUY_RATE             := '';
          V_TEMP_SELL_RATE            := '';
          V_TEMP_MARGIN_TYPE          := '';
          V_TEMP_MARGIN_VALUE         := '';
          V_TEMP_CHARGE_BASIS         := '';
          V_TEMP_SERVICE_LEVEL        := '';
          V_TEMP_RATE_DESCRIPTION     := '';
          V_TEMP_CHANGE_FLAG          := '';
          V_TEMP_WEIGHT_BREAK         := '';
          V_TEMP_RATE_TYPE            := '';
          V_TEMP_MARGIN_DISCOUNT_FLAG := '';
          V_TEMP_CHECKED_FLAG              := '';
          V_temp_line_no              := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''   order by ' ||
                         J.SELLCHARGEID || ',' || J.LANE_NO;
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND V_SHMODE <> 1 THEN
            --V_SQL_SUR := ' AND SELLCHARGEID='||J.SELLCHARGEID ||' AND LANE_NO='|| J.LANE_NO ||' order by break_slab ';
            V_SQL_SUR := ' order by ' || J.SELLCHARGEID || ',' || J.LANE_NO;
          ELSE
            --V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and SELLCHARGEID='||j.SELLCHARGEID ||' and lane_no='||j.lane_no ||' order by line_no';
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  order by line_no ' ||
                         J.SELLCHARGEID || ',' || J.LANE_NO;
          END IF;

          OPEN V_RC_C1 FOR 'SELECT  DISTINCT CHARGESLAB,RATE_DESCRIPTION,CHARGEBASIS,NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_TEMP_WEIGHT_BREAK_SLAB, V_TEMP_RATE_DESCRIPTION,V_TEMP_CHARGE_BASIS,V_TEMP_CHECKED_FLAG;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT distinct to_char(BUYRATE),to_char(SELLRATE),NVL(MARGIN_TYPE,''-'')MARGIN_TYPE,MARGINVALUE,  DECODE(CHARGEBASIS ,''Per CBM'',''Per Container'',CHARGEBASIS),RATE_DESCRIPTION,SRV_LEVEL,CHANGE_FLAG,WEIGHT_BREAK,RATE_TYPE ,LINE_NO, NVL(MARGIN_DISCOUNT_FLAG,''-''),NVL(CHECKED_FLAG,''N'') from GT_MULTI_LANE_TEMP_CHARGES   WHERE CHARGESLAB =:weight_break_slab
					  AND SELLCHARGEID =:SELLCHARGEID AND LANE_NO=:lane_no
					   AND RATE_DESCRIPTION=:V_TEMP_RATE_DESCRIPTION ')
                INTO V_TEMP_BUY_RATE, V_TEMP_SELL_RATE, V_TEMP_MARGIN_TYPE, V_TEMP_MARGIN_VALUE, V_TEMP_CHARGE_BASIS, V_TEMP_RATE_DESCRIPTION, V_TEMP_SERVICE_LEVEL, V_TEMP_CHANGE_FLAG, V_TEMP_WEIGHT_BREAK, V_TEMP_RATE_TYPE, V_temp_line_no, V_temp_MARGIN_DISCOUNT_FLAG,V_TEMP_CHECKED_FLAG
                USING V_TEMP_WEIGHT_BREAK_SLAB, J.SELLCHARGEID, J.LANE_NO, V_TEMP_RATE_DESCRIPTION;

              V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                        V_TEMP_WEIGHT_BREAK_SLAB;
              V_BUY_RATE             := V_BUY_RATE || ',' ||
                                        V_TEMP_BUY_RATE;
              V_SELL_RATE            := V_SELL_RATE || ',' ||
                                        V_TEMP_SELL_RATE;
              V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' ||
                                        V_TEMP_MARGIN_TYPE;
              V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' ||
                                        V_TEMP_MARGIN_VALUE;
              V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' ||
                                        V_TEMP_CHARGE_BASIS;
              V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' ||
                                        V_TEMP_SERVICE_LEVEL;
              V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' ||
                                        V_TEMP_RATE_DESCRIPTION;
              V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' ||
                                        V_TEMP_CHANGE_FLAG;
              V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' ||
                                        V_TEMP_WEIGHT_BREAK;
              V_RATE_TYPE            := V_RATE_TYPE || ',' ||
                                        V_TEMP_RATE_TYPE;
              V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' ||
                                        V_temp_MARGIN_DISCOUNT_FLAG;
              V_CHECKED_FLAG        := V_CHECKED_FLAG||','||V_TEMP_CHECKED_FLAG ;
              V_line_no              := V_line_no || ',' || V_temp_line_no;

            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_WEIGHT_BREAK_SLAB    := V_WEIGHT_BREAK_SLAB || ',' ||
                                          V_TEMP_WEIGHT_BREAK_SLAB;
                V_BUY_RATE             := V_BUY_RATE || ',' || '-';
                V_SELL_RATE            := V_SELL_RATE || ',' || '-';
                V_MARGIN_TYPE          := V_MARGIN_TYPE || ',' || '-';
                V_MARGIN_VALUE         := V_MARGIN_VALUE || ',' || '-';
                V_CHARGE_BASIS         := V_CHARGE_BASIS || ',' || V_TEMP_CHARGE_BASIS;
                V_SERVICE_LEVEL        := V_SERVICE_LEVEL || ',' || '-';
                V_RATE_DESCRIPTION     := V_RATE_DESCRIPTION || ',' || '-';
                V_CHANGE_FLAG          := V_CHANGE_FLAG || ',' || '-';
                V_WEIGHT_BREAK         := V_WEIGHT_BREAK || ',' || '-';
                V_RATE_TYPE            := V_RATE_TYPE || ',' || '-';
                V_MARGIN_DISCOUNT_FLAG := V_MARGIN_DISCOUNT_FLAG || ',' || '-';
                V_CHECKED_FLAG        := V_CHECKED_FLAG||','|| V_TEMP_CHECKED_FLAG ;
                v_line_no              := v_line_no || ',' || '-';

            END;

          END LOOP;
          CLOSE V_RC_C1;
        END IF;
      END IF;
      --   dbms_output.put_line('total V_WEIGHT_BREAK_SLAB.....: ' ||
      --         V_WEIGHT_BREAK_SLAB);
      DELETE FROM GT_MULTI_LANE_CHARGES_FINAL ML WHERE ML.LINE_NO >(SELECT MIN(TST.LINE_NO) FROM  GT_MULTI_LANE_CHARGES_FINAL TST WHERE TST.SELLCHARGEID=J.SELLCHARGEID and TST.LANE_NO = J.LANE_NO AND TST.VERSION_NO = J.VERSION_NO)
         AND ML.SELLCHARGEID = J.SELLCHARGEID
         AND ML.LANE_NO = J.LANE_NO
         AND ML.VERSION_NO = J.VERSION_NO;
      UPDATE GT_MULTI_LANE_CHARGES_FINAL ML
         SET ML.CHARGESLAB           = V_WEIGHT_BREAK_SLAB,
             ML.BUYRATE              = V_BUY_RATE,
             ML.SELLRATE             = V_SELL_RATE,
             ML.MARGIN_TYPE          = V_MARGIN_TYPE,
             ML.MARGINVALUE          = V_MARGIN_VALUE,
             ML.CHARGEBASIS          = V_CHARGE_BASIS,
             ML.SRV_LEVEL            = V_SERVICE_LEVEL,
             ML.RATE_DESCRIPTION     = V_RATE_DESCRIPTION,
             ML.CHANGE_FLAG          = V_CHANGE_FLAG,
             ml.line_no              = v_line_no,
             ml.margin_discount_flag = V_MARGIN_DISCOUNT_FLAG,
             ML.CHECKED_FLAG         = NVL(V_CHECKED_FLAG,'-'),   /*,ML.WEIGHT_BREAK=V_WEIGHT_BREAK,
            ML.RATE_TYPE=V_RATE_TYPE*/
             ML.Lbound                 = 0,
            ML.Ubound                 = 0
       WHERE ML.SELLCHARGEID = J.SELLCHARGEID
         AND ML.LANE_NO = J.LANE_NO
         AND ML.VERSION_NO = J.VERSION_NO;
         ---govind

    END LOOP; -- END OF J LOOP

    SELECT COUNT(*) INTO v_count1 FROM GT_MULTI_LANE_CHARGES_FINAL;
    --     dbms_output.put_line('total GT_MULTI_LANE_TEMP_CHARGES.....: ' ||
    --         v_count1);
    /* OPEN P_RS FOR
    SELECT DISTINCT MF.SELLCHARGEID,MF.CHARGE_ID,MF.CHARGEDESCID,MF.COST_INCURREDAT,MF.CHARGESLAB,
    MF.CURRENCY,MF.BUYRATE,MF.SELLRATE,MF.MARGIN_TYPE,MF.MARGINVALUE,MF.CHARGEBASIS,MF.SEL_BUY_FLAG,
    MF.BUY_CHARGE_ID,MF.ZONE,MF.EFROM,MF.VALIDUPTO,MF.TERMINALID,MF.WEIGHT_BREAK,MF.PRIMARY_BASIS,
    MF.SECONDARY_BASIS,MF.TERTIARY_BASIS,MF.BLOCK,MF.RATE_TYPE,MF.WEIGHT_SCALE,MF.NOTES,
    MF.FREQUENCY,MF.TRANSITTIME,MF.ORG,MF.DEST,MF.SHMODE,MF.SRV_LEVEL,MF.LEG_SL_NO,
    MF.DENSITY_RATIO,MF.SELECTED_FLAG,MF.LBOUND,MF.UBOUND,MF.RATE_INDICATOR,MF.MARGIN_DISCOUNT_FLAG,
    MF.LANE_NO,\*MF.LINE_NO,*\MF.INT_CHARGE_NAME,MF.EXT_CHARGE_NAME,MF.MARGIN_TEST_FLAG,MF.CHARGE_GROUP_ID,
    MF.SHIPMENTMODE,MF.ID,MF.RATE_DESCRIPTION,MF.CONSOLE_TYPE,MF.CARRIER,MF.FREQUENCY_CHECKED,
    MF.TRANSITTIME_CHECKED,MF.CARRIER_CHECKED,MF.RATEVALIDITY_CHECKED,MF.VERSION_NO,MF.CHANGE_FLAG FROM GT_MULTI_LANE_CHARGES_FINAL MF
     ORDER BY --COST_INCURREDAT,
              SELLCHARGEID,
              LANE_NO
              --RATE_DESCRIPTION
              --LINE_NO
              ;*/

    OPEN P_RS FOR
      SELECT DISTINCT ML.* FROM GT_MULTI_LANE_CHARGES_FINAL ML ORDER BY ML.MULTIQUOTE_SERIALNO;

  END;

  PROCEDURE MULTI_QUOTE_SEL_BUY_RATE_PROC(
    P_ORG_LOC      VARCHAR2,
    P_DEST_LOC     VARCHAR2,
    P_TERMINAL     VARCHAR2,
    P_SRVLEVEL     VARCHAR2,
    P_SHMODE       VARCHAR2,
    P_PERMISSION   VARCHAR2 DEFAULT 'Y',
    P_OPERATION    VARCHAR2,
    P_QUOTE_ID     VARCHAR2,
    P_WEIGHT_BREAK VARCHAR2,
    P_RS           OUT RESULTSET) AS
    V_RC_C1      RESULTSET;
    V_TERMINALS  VARCHAR2(32000);
    V_CHARGERATE VARCHAR2(400) := '';
    K            NUMBER := 0;
    V_SQL1       VARCHAR2(2000);
    V_SQLBNEW    VARCHAR2(2000);
    V_SQLSNEW    VARCHAR2(2000);
    V_SQLTBNEW   VARCHAR2(2000);
    V_SQLTSNEW   VARCHAR2(2000);
    V_SQL2       VARCHAR2(2000);
    V_SQL4       VARCHAR2(2000) := '';
    V_SQL5       VARCHAR2(2000);
    V_SQL6       VARCHAR2(32767);
    V_SQL7       VARCHAR2(2000);
    V_SQL10      VARCHAR2(2000);
    V_SQL11      VARCHAR2(2000);
    V_SQL12      VARCHAR2(32767);
    V_SQL13      VARCHAR2(2000);
    V_SQL14      VARCHAR2(2000);
    V_SQL15      VARCHAR2(2000);
    V_SQL16      VARCHAR2(2000);
    V_SQL17      VARCHAR2(2000);
    V_SQL18      VARCHAR2(2000);
    V_SQL19      VARCHAR2(2000);
    V_BASE       VARCHAR2(30);
    V_BREAK      VARCHAR2(4000);
    V_BREAK1     VARCHAR2(4000);
    V_TEMP       VARCHAR2(200);
    V_BREAK_SLAB VARCHAR2(40);
    --V_BREAK_SLAB     VARCHAR2(40);
    V_BREAK_SLAB_BAF  VARCHAR2(40);
    V_BREAK_SLAB_CAF  VARCHAR2(40);
    V_BREAK_SLAB_CSF  VARCHAR2(40);
    V_BREAK_SLAB_PSS  VARCHAR2(40);
    V_OPR_ADM_FLAG    VARCHAR2(3);
    V_RCB_FLAG        VARCHAR2(10);
    V_SELLBUYFLAG     VARCHAR2(10);
    V_SELLRATEID      NUMBER;
    V_BUYRATEID       NUMBER;
    V_LANENO          NUMBER;
    V_ID              NUMBER;
    V_QUOTERATEID     NUMBER;
    V_CON_BUYRATEID   NUMBER;
    V_CON_SELLRATEID  NUMBER;
    V_CON_LANENO      NUMBER;
    V_FLAG            VARCHAR2(200);
    V_CHECKEDFLAG     VARCHAR2(200);
    V_VERSIONNO       NUMBER;
    V_TEMP1           NUMBER;
    V_BASE1           NUMBER;
    v_rd              VARCHAR2(200); --added for 180164
    v_surcharge_count NUMBER;
    V_TEMP_BREAK      VARCHAR2(4000);
    V_TEMP_DESC       VARCHAR2(4000);
    V_RATE_DESC       VARCHAR2(4000);
    V_TEMP_CHARGERATE VARCHAR2(400) := '';
    V_SQL_SUR         VARCHAR2(4000) := '';
    TYPE ARRAY IS TABLE OF QMS_QUOTE_RATES.BREAK_POINT%TYPE;
    BREAK_POINT_LIST ARRAY;
    TYPE DESC_ARRAY IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    CHARGE_DESC_LIST DESC_ARRAY;

  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINAL;
    IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
      V_TERMINALS := 'SELECT 1 FROM FS_FR_TERMINALMASTER TM where TM.terminalid= ';
    ELSE
      DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
                               'v_terminal_id',
                               P_TERMINAL);
      V_TERMINALS := 'SELECT 1 FROM FETCH_TERMINAL_ID_VIEW TV where TV.term_id = ';
      --Union Can be replaced with union all: Sreenadh
    END IF;
    /*V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID) ';*/ --@@Modified by Kameswari for Surcharge Enhancements
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID,TEMP_CHECK,RATE_DESCRIPTION) '; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL2 := 'SELECT distinct to_number(qbd.buyrateid) buyrateid, upper(qbd.weight_break_slab)  weight_break_slab,';
    --COMMENTED BY SUBRAHMANYAM FOR 180164
    --V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check';
    V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL5 := '  FROM qms_buyrates_dtl qbd, qms_buyrates_master qbm WHERE qbd.buyrateid = qbm.buyrateid AND (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbd.origin =:v_org_loc AND qbd.destination=:v_dest_loc
	    AND qbd.service_level LIKE :v_srvlevel';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no  AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    --v_sql6 modified with exists :Sreenadh
    V_SQLTBNEW := ' SELECT  buyrateid,weight_break_slab,CHARGERATE, lane_no,line_no,id_flag,wtbreak ,REC_BUYRATEID,test_check,RATE_DESCRIPTION from (';
    V_SQL7     := ' SELECT distinct to_number(sm.rec_con_id) buyrateid, UPPER(sd.weightbreakslab) weight_break_slab,';
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check';
    V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAMF FOR 180164
    V_SQL11 := ' FROM qms_rec_con_sellratesmaster sm, qms_rec_con_sellratesdtl sd, qms_buyrates_dtl qbd WHERE sm.rec_con_id = sd.rec_con_id  AND sd.origin =: v_org_loc AND sd.destination=:v_dest_loc AND sd.servicelevel_id LIKE :v_srvlevel
	    AND sd.buyrateid=qbd.buyrateid  AND sd.version_no=qbd.version_no AND sd.lane_no=qbd.lane_no AND sd.line_no=qbd.line_no ';
    /* V_SQL12 := ' AND sd.ai_flag =''A'' and exists (select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')  AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID ) UNION all ';*/
    --v_sql12 modified with exists :Sreenadh
    -- commented for 180164
    /*V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID )) where test_check is not null  UNION all ';*/
    --added for 180164
    V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode AND SM.WEIGHT_BREAK = :v_wt_brk
	    AND  EXISTS (' || V_TERMINALS ||
               'Sm.TERMINALID )) where test_check is not null  UNION all ';
    -- ended for 180164

    /*V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE)';*/
    --       V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK)';-- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE)'; -- ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR THE 180164
    --      V_SQLBNEW :='select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check from ('; --@@Added for the performance issue on 20/03/09
    V_SQLBNEW := 'select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE from ('; --ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check'; --@@Modified by Kameswari for Surcharge Enhancements
    V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION,QBD.EXTERNAL_NOTES,QBD.DENSITY_CODE'; --@@ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL15 := 'SELECT distinct to_char(sD.REC_CON_ID) BUYRATEID,sd.version_no version_no,sD.CARRIER_ID carrier_id, sD.ORIGIN,sD.DESTINATION,sD.SERVICELEVEL_ID SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=sD.SERVICELEVEL_ID)SDESC,''a'' weight_break_slab,sD.TRANSIT_TIME,sD.FREQUENCY,''b''  charge_rate,sD.LANE_NO,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') RCB_FLAG, to_char(sD.BUYRATEID) REC_BUYRATE_ID,sD.NOTES,UPPER(SM.WEIGHT_BREAK) UWGT,SM.TERMINALID,sm.CURRENCY,sm.WEIGHT_CLASS,SD.EXTERNAL_NOTES,QBD.DENSITY_CODE ';
    V_SQL16 := ', (select DISTINCT qbd.EFFECTIVE_FROM FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no ) efrom';
    /*V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type';*/
    V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION';
    --commented for 180164
    /* V_SQL18 := '  \*AND NOT EXISTS
        (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
       WHERE qsd.ai_flag = ''A''
         AND QBD.BUYRATEID = QSD.BUYRATEID
         AND QBD.LANE_NO = QSD.LANE_NO
         AND QSD.ACCEPTANCE_FLAG IS NULL
         AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*\  AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE''or QBD.RATE_DESCRIPTION IS NULL)) where test_check is not null order by buyrateid,lane_no ';
    */
    -- added for 180164
    V_SQL18 := '  /*AND NOT EXISTS
	  (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
	 WHERE qsd.ai_flag = ''A''
	   AND QBD.BUYRATEID = QSD.BUYRATEID
	   AND QBD.LANE_NO = QSD.LANE_NO
	   AND QSD.ACCEPTANCE_FLAG IS NULL
	   AND (Qsd.INVALIDATE = ''F'' OR Qsd.INVALIDATE IS NULL))*/ ) WHERE test_check IS NOT NULL ORDER BY buyrateid,lane_no ';
    -- ended for 180164
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_BASE_DATA');
    IF UPPER(P_OPERATION) = 'VIEW' THEN
    FOR K IN
      (SELECT ID
        FROM QMS_QUOTE_MASTER
       WHERE QUOTE_ID = P_QUOTE_ID
         AND VERSION_NO = (SELECT MAX(VERSION_NO)
                             FROM QMS_QUOTE_MASTER
                            WHERE QUOTE_ID = P_QUOTE_ID)
         AND ORIGIN_PORT  = P_ORG_LOC
         AND DESTIONATION_PORT = P_DEST_LOC                   ) LOOP
          V_QUOTERATEID := K.ID;
    FOR J IN (SELECT DISTINCT SELL_BUY_FLAG,
                      SELLRATE_ID,
                      BUYRATE_ID,
                      RATE_LANE_NO,
                      QR.VERSION_NO
        FROM QMS_QUOTE_RATES  QR,
             QMS_QUOTE_MASTER QM,
             FS_RT_PLAN       PL,
             FS_RT_LEG        LEG
       WHERE QR.QUOTE_ID = V_QUOTERATEID
       /*  AND QM.QUOTE_ID = PL.QUOTE_ID*/
         AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
         AND LEG.ORIG_LOC = P_ORG_LOC
         AND LEG.DEST_LOC = P_DEST_LOC
        /*AND LEG.SERIAL_NO = QR.SERIAL_NO*/
         AND QM.QUOTE_ID = P_QUOTE_ID
         AND QM.VERSION_NO = (SELECT MAX(VERSION_NO)
                                FROM QMS_QUOTE_MASTER
                               WHERE QUOTE_ID = P_QUOTE_ID)
         AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')) LOOP

             V_SELLBUYFLAG := J.SELL_BUY_FLAG;
             V_SELLRATEID  := J.SELLRATE_ID;
             V_BUYRATEID   := J.BUYRATE_ID;
             V_LANENO      := J.RATE_LANE_NO;
             V_VERSIONNO   := J.VERSION_NO;

      IF V_SELLBUYFLAG = 'BR' THEN
        /*  SELECT BREAK_POINT BULK COLLECT -- commented and modified by phani sekhar for wpbn 178377 on 20090803
        INTO   BREAK_POINT_LIST
        FROM   QMS_QUOTE_RATES
        WHERE  QUOTE_ID = V_QUOTERATEID
               AND SELL_BUY_FLAG = 'BR';*/ -- added by phani sekhar for wpbn 178377 on 20090803
        SELECT DISTINCT BREAK_POINT, QR.CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES  QR,
               QMS_QUOTE_MASTER QM,
               FS_RT_PLAN       PL,
               FS_RT_LEG        LEG
         WHERE QR.QUOTE_ID = QM.ID
          /* AND QM.QUOTE_ID = PL.QUOTE_ID*/
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
         /*  AND LEG.SERIAL_NO = QR.SERIAL_NO*/
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
               (SELECT MAX(VERSION_NO)
                  FROM QMS_QUOTE_MASTER
                 WHERE QUOTE_ID = P_QUOTE_ID)
           AND QR.SELL_BUY_FLAG IN ('BR'); --ends for 178377
        FORALL I IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT BD.BUYRATEID,
                   BD.WEIGHT_BREAK_SLAB,
                   BD.CHARGERATE,
                   BD.LANE_NO,
                   BD.LINE_NO,
                   'BR',
                   UPPER(BM.WEIGHT_BREAK),
                   '',
                   BD.RATE_DESCRIPTION
              FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
             WHERE BD.WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(I)
               AND BD.BUYRATEID = V_BUYRATEID
               AND BD.LANE_NO = V_LANENO
               AND BD.VERSION_NO = BM.VERSION_NO
               AND BD.VERSION_NO = V_VERSIONNO
               AND BM.BUYRATEID = BD.BUYRATEID
               AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
               AND BD.RATE_DESCRIPTION = CHARGE_DESC_LIST(I)
            -- for 180164
            /*AND (BD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         BD.RATE_DESCRIPTION IS NULL)*/
            ;
         INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           EXTERNAL_NOTES)
          SELECT DISTINCT BD.BUYRATEID,
                          V_VERSIONNO,
                          BD.CARRIER_ID,
                          BD.ORIGIN,
                          BD.DESTINATION,
                          BD.SERVICE_LEVEL,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = BD.SERVICE_LEVEL) SERVICE_LEVEL_DESC,
                          'a',
                          BD.TRANSIT_TIME,
                          BD.FREQUENCY,
                          'b',
                          BD.LANE_NO,
                          'BR',
                          '',
                          BD.NOTES,
                          UPPER(BM.WEIGHT_BREAK),
                          BM.TERMINALID,
                          BM.CURRENCY,
                          BM.WEIGHT_CLASS,
                          BD.EFFECTIVE_FROM,
                          /*BD.VALID_UPTO,*/
                          (SELECT DISTINCT BD.VALID_UPTO
                             FROM QMS_BUYRATES_DTL    BD,
                                  QMS_BUYRATES_MASTER BM,
                                  QMS_QUOTE_RATES     QR
                            WHERE QR.BUYRATE_ID = BD.BUYRATEID
                                 /*AND QR.VERSION_NO = BD.VERSION_NO*/
                              AND QR.RATE_LANE_NO = BD.LANE_NO
                              AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
                              AND BD.BUYRATEID = V_BUYRATEID
                                 --  AND BD.VERSION_NO = V_VERSIONNO
                              AND BD.VERSION_NO = BM.VERSION_NO
                              AND BD.LANE_NO = V_LANENO
                              AND QR.QUOTE_ID = V_QUOTERATEID
                              AND (BM.LANE_NO = BD.LANE_NO OR
                                  BM.LANE_NO IS NULL)
                              AND BM.BUYRATEID = BD.BUYRATEID
                              AND BD.VERSION_NO =
                                  (SELECT MAX(VERSION_NO)
                                     FROM QMS_BUYRATES_DTL
                                    WHERE BUYRATEID = BD.BUYRATEID
                                      AND LANE_NO = BD.LANE_NO)),
                          'Y',
                          BM.CONSOLE_TYPE,
                          BD.RATE_DESCRIPTION,
                          BD.external_notes
            FROM QMS_BUYRATES_DTL    BD,
                 QMS_BUYRATES_MASTER BM,
                 QMS_QUOTE_RATES     QR
           WHERE QR.BUYRATE_ID = BD.BUYRATEID
             AND QR.VERSION_NO = BD.VERSION_NO
             AND QR.RATE_LANE_NO = BD.LANE_NO
             AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
             AND BD.BUYRATEID = V_BUYRATEID
             AND BD.VERSION_NO = V_VERSIONNO
             AND BD.VERSION_NO = BM.VERSION_NO
             AND BD.LANE_NO = V_LANENO
             AND QR.QUOTE_ID = V_QUOTERATEID
             AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.BUYRATEID = BD.BUYRATEID
          -- for 180164
          /*AND
                       (BD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       BD.RATE_DESCRIPTION IS NULL)*/
          ;

      ELSE
        SELECT DISTINCT BREAK_POINT, CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES
         WHERE QUOTE_ID = V_QUOTERATEID
           AND SELL_BUY_FLAG = 'RSR';
        FORALL J IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT SD.REC_CON_ID,
                   SD.WEIGHTBREAKSLAB,
                   SD.CHARGERATE,
                   SD.LANE_NO,
                   SD.LINE_NO,
                   DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                   UPPER(SM.WEIGHT_BREAK),
                   SD.BUYRATEID,
                   SD.RATE_DESCRIPTION
              FROM QMS_REC_CON_SELLRATESDTL    SD,
                   QMS_REC_CON_SELLRATESMASTER SM
             WHERE SD.WEIGHTBREAKSLAB = BREAK_POINT_LIST(J)
               AND SD.RATE_DESCRIPTION = CHARGE_DESC_LIST(J)
               AND SD.REC_CON_ID = V_SELLRATEID
               AND SD.BUYRATEID = V_BUYRATEID
               AND SD.LANE_NO = V_LANENO
               AND SD.VERSION_NO = V_VERSIONNO
               AND SD.REC_CON_ID = SM.REC_CON_ID
               AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') =
                   V_SELLBUYFLAG
            -- for 180164
            /*AND (SD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         SD.RATE_DESCRIPTION IS NULL)*/
            ;
        INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           external_notes)
          SELECT DISTINCT SD.REC_CON_ID,
                          V_VERSIONNO,
                          SD.CARRIER_ID,
                          SD.ORIGIN,
                          SD.DESTINATION,
                          SD.SERVICELEVEL_ID,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = SD.SERVICELEVEL_ID) SERVICE_LEVEL_DESC,
                          'a',
                          SD.TRANSIT_TIME,
                          SD.FREQUENCY,
                          'b',
                          SD.LANE_NO,
                          DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                          SD.BUYRATEID,
                          SD.NOTES,
                          UPPER(SM.WEIGHT_BREAK),
                          SM.TERMINALID,
                          SM.CURRENCY,
                          SM.WEIGHT_CLASS,
                          (SELECT DISTINCT QBD.EFFECTIVE_FROM
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.REC_CON_ID = SD.REC_CON_ID
                              AND QSD.BUYRATEID = SD.BUYRATEID
                              AND QSD.LANE_NO = SD.LANE_NO
                              AND QSD.VERSION_NO = SD.VERSION_NO
                              AND QSD.VERSION_NO = QBD.VERSION_NO),
                          (SELECT DISTINCT QBD.VALID_UPTO
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.VERSION_NO =
                                  (SELECT MAX(RSD1.VERSION_NO)
                                     FROM QMS_REC_CON_SELLRATESDTL RSD1
                                    WHERE RSD1.BUYRATEID = QSD.BUYRATEID
                                      AND RSD1.LANE_NO = QSD.LANE_NO
                                      AND RSD1.BUYRATEID = V_BUYRATEID
                                      AND RSD1.LANE_NO = V_LANENO)
                              AND QBD.VERSION_NO = QSD.VERSION_NO),
                          'Y',
                          SD.CONSOLE_TYPE,
                          SD.RATE_DESCRIPTION,
                          sd.external_notes
            FROM QMS_REC_CON_SELLRATESDTL    SD,
                 QMS_REC_CON_SELLRATESMASTER SM,
                 QMS_QUOTE_RATES             QR
           WHERE QR.BUYRATE_ID = SD.BUYRATEID
             AND QR.SELLRATE_ID = SD.REC_CON_ID
             AND QR.VERSION_NO = SD.VERSION_NO
             AND QR.RATE_LANE_NO = SD.LANE_NO
             AND QR.BREAK_POINT = SD.WEIGHTBREAKSLAB
             AND SD.REC_CON_ID = V_SELLRATEID
             AND SD.BUYRATEID = V_BUYRATEID
             AND SD.LANE_NO = V_LANENO
             AND SD.VERSION_NO = V_VERSIONNO
             AND SD.REC_CON_ID = SM.REC_CON_ID
             AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') = V_SELLBUYFLAG
          -- for 180164
          /* AND
                       (SD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       SD.RATE_DESCRIPTION IS NULL)*/
          ;
      END IF;--VIEW IF END
      END LOOP; -- SELLCHARGE LOOP END
      END LOOP;--ID LOOP END
    ELSE

      /*EXECUTE IMMEDIATE (V_SQL1 || V_SQL7 || V_SQL10 || V_SQL11 ||
            V_SQL12 || V_SQL2 || V_SQL4 || V_SQL5 ||
            V_SQL6 || V_SQL18 || ',LINE_NO')
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/

      print_out('------P_WEIGHT_BREAK-------' || P_WEIGHT_BREAK);
      print_out(V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 || V_SQL11 ||
                V_SQL12 || V_SQLTBNEW || V_SQL2 || V_SQL4 || V_SQL5 ||
                V_SQL6 || V_SQL18 || ',LINE_NO');

      EXECUTE IMMEDIATE (V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 ||
                        V_SQL11 || V_SQL12 || V_SQLTBNEW || V_SQL2 ||
                        V_SQL4 || V_SQL5 || V_SQL6 || V_SQL18 ||
                        ',LINE_NO')
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;
      /*EXECUTE IMMEDIATE (V_SQL13 || V_SQL15 || V_SQL16 || V_SQL17 ||
            V_SQL11 || V_SQL12 || V_SQL14 || V_SQL5 ||
            V_SQL6 || V_SQL18)
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/
      DBMS_OUTPUT.put_line('--------2--------------------');
      print_out(V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 || V_SQL17 ||
                V_SQL11 || V_SQL12 || V_SQLBNEW || V_SQL14 || V_SQL5 ||
                V_SQL6 || V_SQL18);

      EXECUTE IMMEDIATE (V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 ||
                        V_SQL17 || V_SQL11 || V_SQL12 || V_SQLBNEW ||
                        V_SQL14 || V_SQL5 || V_SQL6 || V_SQL18)
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;
    END IF;
    --@@ Commented by subrahmanyam for the 219973 for Dynamic SurCharges.
    DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164

    IF P_OPERATION = 'Modify' THEN
      BEGIN
        FOR K IN (SELECT DISTINCT QR.SELL_BUY_FLAG,
                                  QM.ID,
                                  QR.BUYRATE_ID,
                                  QR.SELLRATE_ID,
                                  QR.RATE_LANE_NO
                    FROM QMS_QUOTE_RATES  QR,
                         QMS_QUOTE_MASTER QM,
                         FS_RT_PLAN       PL,
                         FS_RT_LEG        LEG
                   WHERE QR.QUOTE_ID = QM.ID
                     AND QM.QUOTE_ID = PL.QUOTE_ID
                     AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
                     AND LEG.ORIG_LOC = P_ORG_LOC
                     AND LEG.DEST_LOC = P_DEST_LOC
                     AND LEG.SERIAL_NO = QR.SERIAL_NO
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QM.VERSION_NO =
                         (SELECT MAX(VERSION_NO)
                            FROM QMS_QUOTE_MASTER
                           WHERE QUOTE_ID = P_QUOTE_ID)
                     AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')) LOOP

          V_SELLBUYFLAG    := K.SELL_BUY_FLAG;
          V_ID             := K.ID;
          V_CON_BUYRATEID  := K.BUYRATE_ID;
          V_CON_SELLRATEID := K.SELLRATE_ID;
          V_CON_LANENO     := K.RATE_LANE_NO;

          /*  SELECT DISTINCT QR.SELL_BUY_FLAG, QM.ID, QR.BUYRATE_ID,
              QR.SELLRATE_ID, QR.RATE_LANE_NO
          INTO   V_SELLBUYFLAG, V_ID, V_CON_BUYRATEID,
                 V_CON_SELLRATEID, V_CON_LANENO
          /*FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM -- commented and modified by phani sekhar for wpbn 178377 on 20090803
          WHERE  QR.QUOTE_ID = QM.ID
                 AND QM.QUOTE_ID = P_QUOTE_ID
                 AND QM.ACTIVE_FLAG = 'A'
                 AND
                 QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');
                 -- added by phani sekhar for wpbn 178377 on 20090803
                  FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM, FS_RT_PLAN PL,
           FS_RT_LEG LEG
          WHERE  QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
           (SELECT MAX(VERSION_NO)
                FROM   QMS_QUOTE_MASTER
                WHERE  QUOTE_ID = P_QUOTE_ID)
          AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');*/

          --ends 178377

          SELECT BREAK_POINT, charge_description BULK COLLECT
            INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
            FROM QMS_QUOTE_RATES
           WHERE QUOTE_ID = V_ID
             AND SELL_BUY_FLAG = V_SELLBUYFLAG;
          FORALL K IN 1 .. BREAK_POINT_LIST.COUNT
            UPDATE GT_TEMP_DATA_1
               SET CHECKED_FLAG = 'Y'
             WHERE BUYRATEID = (CASE WHEN V_SELLBUYFLAG = 'BR' THEN
                    V_CON_BUYRATEID ELSE V_CON_SELLRATEID END)
               AND LANE_NO = V_CON_LANENO
               AND WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(K)
               AND RATE_DESCRIPTION = CHARGE_DESC_LIST(K);
        END LOOP;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_SELLBUYFLAG := '';

      END;
    END IF;
    FOR J IN (SELECT DISTINCT BUYRATEID,
                              VERSION_NO,
                              LANE_NO,
                              WEIGHT_BREAK,
                              RCB_FLAG,
                              NVL(REC_BUYRATE_ID, 'P') REC_BUYRATE_ID
                FROM GT_BASE_DATA
               WHERE RATE_DESCRIPTION LIKE 'A FREIGHT RATE'
               ORDER BY BUYRATEID, LANE_NO) LOOP
      V_CHARGERATE      := '';
      V_CHECKEDFLAG     := '';
      K                 := 1;
      V_BREAK           := '';
      v_surcharge_count := 0;
      V_TEMP_BREAK      := '';
      V_TEMP_CHARGERATE := '';
      V_RATE_DESC       := '';
      IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1) = FALSE THEN
        BEGIN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND WEIGHT_BREAK_SLAB=''BASIC'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_TEMP_BREAK;
          V_CHARGERATE := V_TEMP_CHARGERATE || ',';
          V_RATE_DESC  := V_TEMP_DESC || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count + 1;
            V_BREAK           := 'BASIC';
            V_CHARGERATE      := '-' || ',';
            V_RATE_DESC       := 'A FREIGHT RATE' || ',';
            BEGIN
            EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no>=0 AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id AND rate_description LIKE ''A FREIGHT RATE'' ')
              INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
              USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
            V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
            V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
             EXCEPTION
             WHEN NO_DATA_FOUND THEN
            V_BREAK      := V_BREAK || ',' || 'MIN';
            V_CHARGERATE := V_CHARGERATE ||'-';
            V_RATE_DESC  := V_RATE_DESC || 'A FREIGHT RATE' || ',';
            END;
        END;
        IF v_surcharge_count = 0 THEN
        BEGIN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE)  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=1  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
          V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
          V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
             WHEN NO_DATA_FOUND THEN
             V_BREAK      := V_BREAK || ',' || 'MIN';
            V_CHARGERATE := V_CHARGERATE ||'-';
            V_RATE_DESC  := V_RATE_DESC || 'A FREIGHT RATE' || ',';
            END;
         END IF;
      END IF;
      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      ELSIF UPPER(J.WEIGHT_BREAK) = 'FLAT' OR
            UPPER(J.WEIGHT_BREAK) = 'SLAB' THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      END IF;
      IF (J.WEIGHT_BREAK <> 'FLAT') THEN
        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          --            V_SQL1 := ' AND line_no > 0 order by break_slab';
          V_SQL1 := ' AND line_no > 0 AND UPPER(WEIGHT_BREAK_SLAB) NOT IN(''BASIC'',''MIN'') order by break_slab';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          V_SQL1 := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                    J.LANE_NO || ' order by break_slab';
        ELSE
          --           V_SQL1 := ' AND line_no > 0 order by to_number(break_slab)';-- COMMENTED FOR 180164
          V_SQL1 := ' AND line_no > 0 AND UPPER(WEIGHT_BREAK_SLAB) NOT IN(''BASIC'',''MIN'') order by to_number(break_slab)';
          -- V_SQL1 := ' AND line_no > 0 order by LINE_NO';
        END IF;
        -- COMMENTED FOR 180164
        --  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1 -- commented by subrahmanyam for 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL1
          USING J.WEIGHT_BREAK;

        --ADDED FOR 180164
        /*  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
                    USING J.WEIGHT_BREAK;
        */
        LOOP
          FETCH V_RC_C1
          --INTO V_BREAK_SLAB,V_LINE_NO ;--ADDED FOR 180164
            INTO V_BREAK_SLAB; -- COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            /*            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
            AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
            and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
            */
            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag,RATE_DESCRIPTION FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
              INTO V_TEMP, V_FLAG, V_TEMP_DESC
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-' || ',';
          END;
          /*If UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2
           Then
           V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            BEGIN
            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
           AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_BAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CSF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_PSS, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;

           End If;*/
        END LOOP;
        CLOSE V_RC_C1;

        --@@ Added by subrahmanyam for the 180164
        V_TEMP_DESC := '';
        /*      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
                    V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
                    V_SQL_SUR := ' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSE
                   V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'')  AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and  '||j.buyrateid ||' and '||j.lane_no ||' and to_number(break_slab)';
              END IF;
        */

        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_SQL_SUR := ' AND line_no > 0 AND /*WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND*/ RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no ';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          --            V_SQL_SUR := ' and buyrateid='||j.buyrateid ||' and lane_no='||j.lane_no ||' order by line_no  order by line_no ';
          V_SQL_SUR := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                       J.LANE_NO || ' order by break_slab ';
        ELSE
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no';
        END IF;
        V_TEMP_BREAK := '';
        OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
          USING J.WEIGHT_BREAK;
        LOOP
          FETCH V_RC_C1
            INTO V_BREAK_SLAB, V_TEMP_DESC;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
              INTO V_TEMP, V_FLAG
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
            V_TEMP_BREAK  := V_TEMP_BREAK || V_BREAK_SLAB || ',';
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_TEMP_BREAK  := V_BREAK_SLAB || ',';
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-,';
          END;

        END LOOP;
        CLOSE V_RC_C1;
        /*   IF P_SHMODE =1 THEN
                   \* V_BREAK_SLAB:='FSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB:='FSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='FSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB    :='SSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;*\
                    DBMS_OUTPUT.put_line('----FOR AIR-----');
                  ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE =2
                  THEN
                      V_BREAK_SLAB    :='CAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CAF%';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CSF';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                ELSIF P_SHMODE =4 THEN
                V_BREAK_SLAB    :='SURCHARGE';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
             END IF;
        */
        --@@ Ended by subrahmanyam for the 180164

        V_BASE  := V_BREAK;
        V_BREAK := '';
        -- COMMENTED FOR 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and weight_break =:weight_break' || V_SQL1
          USING J.WEIGHT_BREAK;
        -- ADDED FOR 180164
        /* OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
        USING J.WEIGHT_BREAK;*/
        LOOP
          FETCH V_RC_C1
          -- INTO V_BREAK_SLAB,V_LINE_NO;--ADDED FOR 180164
            INTO V_BREAK_SLAB; --COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
            /*V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';*/

            -- V_BREAK :=V_BREAK||','||V_BREAK_SLAB_BAF||','||V_BREAK_SLAB_CAF||','||V_BREAK_SLAB_CSF||','||V_BREAK_SLAB_PSS;
          ELSIF P_SHMODE = 1 OR P_SHMODE = 4 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          END IF;
        END LOOP;
        CLOSE V_RC_C1;
        --@@Added by subrahmanyam for 180164
        IF P_SHMODE = 1 THEN
          --V_BREAK := V_BREAK || ',' ||'FSBASIC'||','||'FSMIN'||','||'FSKG'||','||'SSBASIC'||','||'SSMIN'||','||'SSKG';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
          -- ELSIF UPPER(J.WEIGHT_BREAK) != 'LIST' AND P_SHMODE = 2 THEN
        ELSIF P_SHMODE = 2 THEN
          -- V_BREAK := V_BREAK ||','||'CAFMIN'||','||'CAF%'||','||'BAFMIN'||','||'BAFM3'||','||'PSSMIN'||','||'PSSM3'||','||'CSF';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        ELSIF P_SHMODE = 4 THEN
         /* V_BREAK := V_BREAK || ',' || 'SURCHARGE'; COMMENTED BY GOVIND FOR TRUCK RATES ORDERING*/
            V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        END IF;

        --@@ Ended by subrahmanyam for 180164
        IF (UPPER(J.WEIGHT_BREAK) = 'SLAB') THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSE
          V_BREAK := SUBSTR(V_BREAK, 2, LENGTH(V_BREAK));
        END IF;
      ELSE
      BEGIN
        SELECT DISTINCT WEIGHT_BREAK_SLAB, CHARGERATE, RATE_DESCRIPTION
          INTO V_BASE, V_TEMP, V_TEMP_DESC
          FROM GT_TEMP_DATA_1
         WHERE LINE_NO > 0
           AND WEIGHT_BREAK_SLAB NOT IN ('BASIC','MIN')
           AND BUYRATEID = J.BUYRATEID
           AND LANE_NO = J.LANE_NO
           AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
           AND RATE_DESCRIPTION IN ('A FREIGHT RATE','Freight Rate','A Freight Rate');
        V_BREAK      := V_BREAK || ',' || V_BASE;
        V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
        V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        EXCEPTION WHEN NO_DATA_FOUND THEN
        V_BREAK      := V_BREAK || ',' || 'FLAT';
        V_CHARGERATE := V_CHARGERATE ||'0.00'||',';
        V_RATE_DESC  := V_RATE_DESC || 'A FREIGHT RATE'||',';
        END;
        IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2 THEN
          /*BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAF%' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN

            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
              dbms_output.put_line('V_BREAK'||V_BREAK);
            EXCEPTION WHEN NO_DATA_FOUND THEN
              V_BREAK      := V_BREAK || ',' || 'BAFM3';
              V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
          --  dbms_output.put_line('V_BREAK'||V_BREAK);
            dbms_output.put_line('V_CHARGERATE'||V_CHARGERATE);
            dbms_output.put_line('J.BUYRATEID'||J.BUYRATEID);
             dbms_output.put_line('J.LANE_NO'||J.LANE_NO);
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

             V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CSF' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;*/

          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ;
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 1 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          --COMMENTED BY SUBRAHMANYAM FOR 219973
          -- BEGIN

          DBMS_OUTPUT.put_line('--------COMMENTED BY SUBRAHMANYAM-------');
          /* SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;*/
          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no';
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 4 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN
          BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
              INTO V_BASE, V_TEMP
              FROM GT_TEMP_DATA_1
             WHERE LINE_NO > 0
               AND BUYRATEID = J.BUYRATEID
               AND LANE_NO = J.LANE_NO
               AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
               AND RATE_DESCRIPTION <> 'A FREIGHT RATE'
               AND WEIGHT_BREAK_SLAB = 'SURCHARGE';
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE := V_CHARGERATE || '-,';
          END;

        END IF;
      END IF;
      --@@ Commented by subrahmanyam for 219973
      /*  IF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =2) THEN
      V_BREAK := 'MIN,FLAT,CAFMIN,CAF%,BAFMIN,BAFM3,PSSMIN,PSSM3,CSF';
      ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =1) THEN
        V_BREAK := 'MIN,FLAT,FSBASIC,FSMIN,FSKG,,SSBASIC,SSMIN,SSKG';
        ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =4) THEN
          V_BREAK := 'MIN,FLAT,SURCHARGE';
      END IF;*/

      UPDATE GT_BASE_DATA
         SET WEIGHT_BREAK_SLAB = V_BREAK,
             RATE_DESCRIPTION  = V_RATE_DESC,
             CHARGERATE        = SUBSTR(V_CHARGERATE,
                                        1,
                                        LENGTH(V_CHARGERATE) - 1),
             CHECKED_FLAG      = SUBSTR(V_CHECKEDFLAG,
                                        1,
                                        LENGTH(V_CHECKEDFLAG) - 1)
       WHERE BUYRATEID = J.BUYRATEID
         AND LANE_NO = J.LANE_NO
         AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID;
      DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164
      IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'COPY' THEN
        print_out('-------ERROR------');
        DBMS_OUTPUT.put_line('SELECT RCB_FLAG
			INTO   V_RCB_FLAG
			FROM   GT_BASE_DATA
			WHERE  BUYRATEID = ' || J.BUYRATEID || '
			       AND LANE_NO =' || J.LANE_NO || '
			       AND VERSION_NO =' || J.VERSION_NO || '
			       AND NVL(REC_BUYRATE_ID
				      ,''P'') =' || J.REC_BUYRATE_ID || '
               AND RATE_DESCRIPTION = ' ||
                             '''A FREIGHT RATE''''');
        BEGIN
          V_RCB_FLAG := '';
          SELECT DISTINCT RCB_FLAG
            INTO V_RCB_FLAG
            FROM GT_BASE_DATA
           WHERE BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO
             AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID
          --  AND RATE_DESCRIPTION = 'A FREIGHT RATE'
          ;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('V_RCB_FLAG..' || V_RCB_FLAG);
        END;
        IF (V_RCB_FLAG = 'BR') THEN
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.BUYRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG = 'BR'
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        ELSE
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.SELLRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.REC_BUYRATE_ID = QR.BUYRATE_ID
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG IN ('RSR', 'CSR')
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        END IF;
      END IF;
    END LOOP;

    -- @@ Commented And Added for 219973
    /*       IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA WHERE RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID;-- COMMENTED BY SUBRAHMANYAM FOR 179985
        ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      ELSE
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA
      WHERE  RCB_FLAG <> 'BR' AND RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID; -- COMMENTED BY SUBRAHMANYAM FOR 179855
              ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      END IF;*/
  IF UPPER(P_OPERATION) = 'VIEW' THEN

  IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
          WHERE GBD.ORIGIN =P_ORG_LOC
              AND GBD.DESTINATION =P_DEST_LOC
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE

      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
             AND GBD.ORIGIN =P_ORG_LOC
              AND GBD.DESTINATION =P_DEST_LOC
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
              TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
               END IF;
  ELSE

    IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
    END IF;

    END IF;
    --@@ Ended for 219973
  END;

  PROCEDURE QUOTE_SELL_BUY_RATES_PROC(P_ORG_LOC    VARCHAR2,
                                      P_DEST_LOC   VARCHAR2,
                                      P_TERMINAL   VARCHAR2,
                                      P_SRVLEVEL   VARCHAR2,
                                      P_SHMODE     VARCHAR2,
                                      P_PERMISSION VARCHAR2 DEFAULT 'Y',
                                      P_OPERATION  VARCHAR2,
                                      P_QUOTE_ID   VARCHAR2,
                                      P_RS         OUT RESULTSET) AS
    V_RC_C1      RESULTSET;
    V_TERMINALS  VARCHAR2(32000);
    V_CHARGERATE VARCHAR2(400) := '';
    K            NUMBER := 0;
    V_SQL1       VARCHAR2(2000);
    V_SQLBNEW    VARCHAR2(2000);
    V_SQLSNEW    VARCHAR2(2000);
    V_SQLTBNEW   VARCHAR2(2000);
    V_SQLTSNEW   VARCHAR2(2000);
    V_SQL2       VARCHAR2(2000);
    V_SQL4       VARCHAR2(2000) := '';
    V_SQL5       VARCHAR2(2000);
    V_SQL6       VARCHAR2(32767);
    V_SQL7       VARCHAR2(2000);
    V_SQL10      VARCHAR2(2000);
    V_SQL11      VARCHAR2(2000);
    V_SQL12      VARCHAR2(32767);
    V_SQL13      VARCHAR2(2000);
    V_SQL14      VARCHAR2(2000);
    V_SQL15      VARCHAR2(2000);
    V_SQL16      VARCHAR2(2000);
    V_SQL17      VARCHAR2(2000);
    V_SQL18      VARCHAR2(2000);
    V_SQL19      VARCHAR2(2000);
    V_BASE       VARCHAR2(30);
    V_BREAK      VARCHAR2(4000);
    V_BREAK1     VARCHAR2(4000);
    V_TEMP       VARCHAR2(200);
    V_BREAK_SLAB VARCHAR2(40);
    --V_BREAK_SLAB     VARCHAR2(40);
    V_BREAK_SLAB_BAF  VARCHAR2(40);
    V_BREAK_SLAB_CAF  VARCHAR2(40);
    V_BREAK_SLAB_CSF  VARCHAR2(40);
    V_BREAK_SLAB_PSS  VARCHAR2(40);
    V_OPR_ADM_FLAG    VARCHAR2(3);
    V_RCB_FLAG        VARCHAR2(10);
    V_SELLBUYFLAG     VARCHAR2(10);
    V_SELLRATEID      NUMBER;
    V_BUYRATEID       NUMBER;
    V_LANENO          NUMBER;
    V_ID              NUMBER;
    V_QUOTERATEID     NUMBER;
    V_CON_BUYRATEID   NUMBER;
    V_CON_SELLRATEID  NUMBER;
    V_CON_LANENO      NUMBER;
    V_FLAG            VARCHAR2(200);
    V_CHECKEDFLAG     VARCHAR2(200);
    V_VERSIONNO       NUMBER;
    V_TEMP1           NUMBER;
    V_BASE1           NUMBER;
    v_rd              VARCHAR2(200); --added for 180164
    v_surcharge_count NUMBER;
    V_TEMP_BREAK      VARCHAR2(4000);
    V_TEMP_DESC       VARCHAR2(4000);
    V_RATE_DESC       VARCHAR2(4000);
    V_TEMP_CHARGERATE VARCHAR2(400) := '';
    V_SQL_SUR         VARCHAR2(4000) := '';
    v_count           NUMBER;
    TYPE ARRAY IS TABLE OF QMS_QUOTE_RATES.BREAK_POINT%TYPE;
    BREAK_POINT_LIST ARRAY;
    TYPE DESC_ARRAY IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    CHARGE_DESC_LIST DESC_ARRAY;

  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINAL;
    IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
      V_TERMINALS := 'SELECT 1 FROM FS_FR_TERMINALMASTER TM where TM.terminalid= ';
    ELSE
      DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
                               'v_terminal_id',
                               P_TERMINAL);
      V_TERMINALS := 'SELECT 1 FROM FETCH_TERMINAL_ID_VIEW TV where TV.term_id = ';
      --Union Can be replaced with union all: Sreenadh
    END IF;
    /*V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID) ';*/ --@@Modified by Kameswari for Surcharge Enhancements
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID,TEMP_CHECK,RATE_DESCRIPTION) '; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL2 := 'SELECT distinct to_number(qbd.buyrateid) buyrateid, upper(qbd.weight_break_slab)  weight_break_slab,';
    --COMMENTED BY SUBRAHMANYAM FOR 180164
    --V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check';
    V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL5 := '  FROM qms_buyrates_dtl qbd, qms_buyrates_master qbm WHERE qbd.buyrateid = qbm.buyrateid AND (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbd.origin =:v_org_loc AND qbd.destination=:v_dest_loc
	    AND qbd.service_level LIKE :v_srvlevel';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no  AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND  exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode  and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    --v_sql6 modified with exists :Sreenadh
    V_SQLTBNEW := ' SELECT  buyrateid,weight_break_slab,CHARGERATE, lane_no,line_no,id_flag,wtbreak ,REC_BUYRATEID,test_check,RATE_DESCRIPTION from (';
    V_SQL7     := ' SELECT distinct to_number(sm.rec_con_id) buyrateid, UPPER(sd.weightbreakslab) weight_break_slab,';
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check';
    V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAMF FOR 180164
    V_SQL11 := ' FROM qms_rec_con_sellratesmaster sm, qms_rec_con_sellratesdtl sd, qms_buyrates_dtl qbd WHERE sm.rec_con_id = sd.rec_con_id  AND sd.origin =: v_org_loc AND sd.destination=:v_dest_loc AND sd.servicelevel_id LIKE :v_srvlevel
	    AND sd.buyrateid=qbd.buyrateid  AND sd.version_no=qbd.version_no AND sd.lane_no=qbd.lane_no AND sd.line_no=qbd.line_no ';
    /* V_SQL12 := ' AND sd.ai_flag =''A'' and exists (select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')  AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID ) UNION all ';*/
    --v_sql12 modified with exists :Sreenadh
    -- commented for 180164
    /*V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID )) where test_check is not null  UNION all ';*/
    --added for 180164
    V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
	    AND  EXISTS (' || V_TERMINALS ||
               'Sm.TERMINALID )) where test_check is not null  UNION all ';
    -- ended for 180164

    /*V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE)';*/
    --       V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK)';-- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE)'; -- ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR THE 180164
    --      V_SQLBNEW :='select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check from ('; --@@Added for the performance issue on 20/03/09
    V_SQLBNEW := 'select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE from ('; --ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check'; --@@Modified by Kameswari for Surcharge Enhancements
    V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION,QBD.EXTERNAL_NOTES,QBD.DENSITY_CODE'; --@@ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL15 := 'SELECT distinct to_char(sD.REC_CON_ID) BUYRATEID,sd.version_no version_no,sD.CARRIER_ID carrier_id, sD.ORIGIN,sD.DESTINATION,sD.SERVICELEVEL_ID SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=sD.SERVICELEVEL_ID)SDESC,''a'' weight_break_slab,sD.TRANSIT_TIME,sD.FREQUENCY,''b''  charge_rate,sD.LANE_NO,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') RCB_FLAG, to_char(sD.BUYRATEID) REC_BUYRATE_ID,sD.NOTES,UPPER(SM.WEIGHT_BREAK) UWGT,SM.TERMINALID,sm.CURRENCY,sm.WEIGHT_CLASS,SD.EXTERNAL_NOTES,QBD.DENSITY_CODE ';
    V_SQL16 := ', (select DISTINCT qbd.EFFECTIVE_FROM FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no ) efrom';
    /*V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type';*/
    V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION';
    --commented for 180164
    /* V_SQL18 := '  \*AND NOT EXISTS
        (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
       WHERE qsd.ai_flag = ''A''
         AND QBD.BUYRATEID = QSD.BUYRATEID
         AND QBD.LANE_NO = QSD.LANE_NO
         AND QSD.ACCEPTANCE_FLAG IS NULL
         AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*\  AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE''or QBD.RATE_DESCRIPTION IS NULL)) where test_check is not null order by buyrateid,lane_no ';
    */
    -- added for 180164
    V_SQL18 := '  /*AND NOT EXISTS
	  (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
	 WHERE qsd.ai_flag = ''A''
	   AND QBD.BUYRATEID = QSD.BUYRATEID
	   AND QBD.LANE_NO = QSD.LANE_NO
	   AND QSD.ACCEPTANCE_FLAG IS NULL
	   AND (Qsd.INVALIDATE = ''F'' OR Qsd.INVALIDATE IS NULL))*/ ) WHERE test_check IS NOT NULL ORDER BY buyrateid,lane_no ';
    -- ended for 180164 --kishore
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_BASE_DATA');
    IF UPPER(P_OPERATION) = 'VIEW' THEN
  FOR J IN (    SELECT ID

        FROM QMS_QUOTE_MASTER
       WHERE QUOTE_ID = P_QUOTE_ID
         AND VERSION_NO = (SELECT MAX(VERSION_NO)
                             FROM QMS_QUOTE_MASTER
                            WHERE QUOTE_ID = P_QUOTE_ID)) LOOP
         V_QUOTERATEID := J.ID;

     FOR K IN ( SELECT DISTINCT SELL_BUY_FLAG,
                      SELLRATE_ID,
                      BUYRATE_ID,
                      RATE_LANE_NO,
                      QR.VERSION_NO

        FROM QMS_QUOTE_RATES  QR,
             QMS_QUOTE_MASTER QM,
             FS_RT_PLAN       PL,
             FS_RT_LEG        LEG
       WHERE QR.QUOTE_ID = QM.ID
         AND QM.QUOTE_ID = PL.QUOTE_ID
         AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
         AND LEG.ORIG_LOC = P_ORG_LOC
         AND LEG.DEST_LOC = P_DEST_LOC
         AND LEG.SERIAL_NO = QR.SERIAL_NO
         AND QM.QUOTE_ID = P_QUOTE_ID
         AND QM.VERSION_NO = (SELECT MAX(VERSION_NO)
                                FROM QMS_QUOTE_MASTER
                               WHERE QUOTE_ID = P_QUOTE_ID)
         AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')) LOOP

             V_SELLBUYFLAG :=  K.SELL_BUY_FLAG;
             V_SELLRATEID  :=  K.SELLRATE_ID;
             V_BUYRATEID   :=  K.BUYRATE_ID;
             V_LANENO      :=  K.RATE_LANE_NO;
             V_VERSIONNO   :=  K.VERSION_NO;

      IF V_SELLBUYFLAG = 'BR' THEN
        /*  SELECT BREAK_POINT BULK COLLECT -- commented and modified by phani sekhar for wpbn 178377 on 20090803
        INTO   BREAK_POINT_LIST
        FROM   QMS_QUOTE_RATES
        WHERE  QUOTE_ID = V_QUOTERATEID
               AND SELL_BUY_FLAG = 'BR';*/ -- added by phani sekhar for wpbn 178377 on 20090803
        SELECT BREAK_POINT, DECODE(QR.CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',QR.CHARGE_DESCRIPTION)CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES  QR,
               QMS_QUOTE_MASTER QM,
               FS_RT_PLAN       PL,
               FS_RT_LEG        LEG
         WHERE QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
               (SELECT MAX(VERSION_NO)
                  FROM QMS_QUOTE_MASTER
                 WHERE QUOTE_ID = P_QUOTE_ID)
           AND QR.SELL_BUY_FLAG IN ('BR'); --ends for 178377
        FORALL I IN 1 .. BREAK_POINT_LIST.COUNT
         INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT BD.BUYRATEID,
                   BD.WEIGHT_BREAK_SLAB,
                   BD.CHARGERATE,
                   BD.LANE_NO,
                   BD.LINE_NO,
                   'BR',
                   UPPER(BM.WEIGHT_BREAK),
                   '',
                   BD.RATE_DESCRIPTION
              FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
             WHERE BD.WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(I)
               AND BD.BUYRATEID = V_BUYRATEID
               AND BD.LANE_NO = V_LANENO
               AND BD.VERSION_NO = BM.VERSION_NO
               AND BD.VERSION_NO = V_VERSIONNO
               AND BM.BUYRATEID = BD.BUYRATEID
               AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
               AND BD.RATE_DESCRIPTION = CHARGE_DESC_LIST(I)
            -- for 180164
            /*AND (BD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         BD.RATE_DESCRIPTION IS NULL)*/
            ;

    --gOVIND


  -- commit;


 --COMMIT;





    ---GOVIND END
      INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           EXTERNAL_NOTES)
          SELECT DISTINCT BD.BUYRATEID,
                          V_VERSIONNO,
                          BD.CARRIER_ID,
                          BD.ORIGIN,
                          BD.DESTINATION,
                          BD.SERVICE_LEVEL,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = BD.SERVICE_LEVEL) SERVICE_LEVEL_DESC,
                          'a',
                          BD.TRANSIT_TIME,
                          BD.FREQUENCY,
                          'b',
                          BD.LANE_NO,
                          'BR',
                          '',
                          BD.NOTES,
                          UPPER(BM.WEIGHT_BREAK),
                          BM.TERMINALID,
                          BM.CURRENCY,
                          BM.WEIGHT_CLASS,
                          BD.EFFECTIVE_FROM,
                          /*BD.VALID_UPTO,*/
                          (SELECT DISTINCT BD.VALID_UPTO
                             FROM QMS_BUYRATES_DTL    BD,
                                  QMS_BUYRATES_MASTER BM,
                                  QMS_QUOTE_RATES     QR
                            WHERE QR.BUYRATE_ID = BD.BUYRATEID
                                 /*AND QR.VERSION_NO = BD.VERSION_NO*/
                              AND QR.RATE_LANE_NO = BD.LANE_NO
                              AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
                              AND BD.BUYRATEID = V_BUYRATEID
                                 --  AND BD.VERSION_NO = V_VERSIONNO
                              AND BD.VERSION_NO = BM.VERSION_NO
                              AND BD.LANE_NO = V_LANENO
                              AND QR.QUOTE_ID = V_QUOTERATEID
                              AND (BM.LANE_NO = BD.LANE_NO OR
                                  BM.LANE_NO IS NULL)
                              AND BM.BUYRATEID = BD.BUYRATEID
                              AND BD.VERSION_NO =
                                  (SELECT MAX(VERSION_NO)
                                     FROM QMS_BUYRATES_DTL
                                    WHERE BUYRATEID = BD.BUYRATEID
                                      AND LANE_NO = BD.LANE_NO)),
                          'Y',
                          BM.CONSOLE_TYPE,
                          BD.RATE_DESCRIPTION,
                          BD.external_notes
            FROM QMS_BUYRATES_DTL    BD,
                 QMS_BUYRATES_MASTER BM,
                 QMS_QUOTE_RATES     QR
           WHERE QR.BUYRATE_ID = BD.BUYRATEID
             AND QR.VERSION_NO = BD.VERSION_NO
             AND QR.RATE_LANE_NO = BD.LANE_NO
             AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
             AND BD.BUYRATEID = V_BUYRATEID
             AND BD.VERSION_NO = V_VERSIONNO
             AND BD.VERSION_NO = BM.VERSION_NO
             AND BD.LANE_NO = V_LANENO
             AND QR.QUOTE_ID = V_QUOTERATEID
             AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.BUYRATEID = BD.BUYRATEID
          -- for 180164
          /*AND
                       (BD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       BD.RATE_DESCRIPTION IS NULL)*/
          ;

      ELSE
        SELECT BREAK_POINT, CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES
         WHERE QUOTE_ID = V_QUOTERATEID
           AND SELL_BUY_FLAG = 'RSR';
        FORALL J IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT SD.REC_CON_ID,
                   SD.WEIGHTBREAKSLAB,
                   SD.CHARGERATE,
                   SD.LANE_NO,
                   SD.LINE_NO,
                   DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                   UPPER(SM.WEIGHT_BREAK),
                   SD.BUYRATEID,
                   SD.RATE_DESCRIPTION
              FROM QMS_REC_CON_SELLRATESDTL    SD,
                   QMS_REC_CON_SELLRATESMASTER SM
             WHERE SD.WEIGHTBREAKSLAB = BREAK_POINT_LIST(J)
               AND SD.RATE_DESCRIPTION = CHARGE_DESC_LIST(J)
               AND SD.REC_CON_ID = V_SELLRATEID
               AND SD.BUYRATEID = V_BUYRATEID
               AND SD.LANE_NO = V_LANENO
               AND SD.VERSION_NO = V_VERSIONNO
               AND SD.REC_CON_ID = SM.REC_CON_ID
               AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') =
                   V_SELLBUYFLAG
            -- for 180164
            /*AND (SD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         SD.RATE_DESCRIPTION IS NULL)*/
            ;
        INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           external_notes)
          SELECT DISTINCT SD.REC_CON_ID,
                          V_VERSIONNO,
                          SD.CARRIER_ID,
                          SD.ORIGIN,
                          SD.DESTINATION,
                          SD.SERVICELEVEL_ID,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = SD.SERVICELEVEL_ID) SERVICE_LEVEL_DESC,
                          'a',
                          SD.TRANSIT_TIME,
                          SD.FREQUENCY,
                          'b',
                          SD.LANE_NO,
                          DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                          SD.BUYRATEID,
                          SD.NOTES,
                          UPPER(SM.WEIGHT_BREAK),
                          SM.TERMINALID,
                          SM.CURRENCY,
                          SM.WEIGHT_CLASS,
                          (SELECT DISTINCT QBD.EFFECTIVE_FROM
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.REC_CON_ID = SD.REC_CON_ID
                              AND QSD.BUYRATEID = SD.BUYRATEID
                              AND QSD.LANE_NO = SD.LANE_NO
                              AND QSD.VERSION_NO = SD.VERSION_NO
                              AND QSD.VERSION_NO = QBD.VERSION_NO),
                          (SELECT DISTINCT QBD.VALID_UPTO
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.VERSION_NO =
                                  (SELECT MAX(RSD1.VERSION_NO)
                                     FROM QMS_REC_CON_SELLRATESDTL RSD1
                                    WHERE RSD1.BUYRATEID = QSD.BUYRATEID
                                      AND RSD1.LANE_NO = QSD.LANE_NO
                                      AND RSD1.BUYRATEID = V_BUYRATEID
                                      AND RSD1.LANE_NO = V_LANENO)
                              AND QBD.VERSION_NO = QSD.VERSION_NO),
                          'Y',
                          SD.CONSOLE_TYPE,
                          SD.RATE_DESCRIPTION,
                          sd.external_notes
            FROM QMS_REC_CON_SELLRATESDTL    SD,
                 QMS_REC_CON_SELLRATESMASTER SM,
                 QMS_QUOTE_RATES             QR
           WHERE QR.BUYRATE_ID = SD.BUYRATEID
             AND QR.SELLRATE_ID = SD.REC_CON_ID
             AND QR.VERSION_NO = SD.VERSION_NO
             AND QR.RATE_LANE_NO = SD.LANE_NO
             AND QR.BREAK_POINT = SD.WEIGHTBREAKSLAB
             AND SD.REC_CON_ID = V_SELLRATEID
             AND SD.BUYRATEID = V_BUYRATEID
             AND SD.LANE_NO = V_LANENO
             AND SD.VERSION_NO = V_VERSIONNO
             AND SD.REC_CON_ID = SM.REC_CON_ID
             AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') = V_SELLBUYFLAG
          -- for 180164
          /* AND
                       (SD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       SD.RATE_DESCRIPTION IS NULL)*/
          ;
      END IF;
      END LOOP;-- SELL_BUY_FLAG LOOP END
     END LOOP; --id loop end
    ELSE--VIEW ELSE END

      /*EXECUTE IMMEDIATE (V_SQL1 || V_SQL7 || V_SQL10 || V_SQL11 ||
            V_SQL12 || V_SQL2 || V_SQL4 || V_SQL5 ||
            V_SQL6 || V_SQL18 || ',LINE_NO')
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/

      print_out('-------------');
      print_out(V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 || V_SQL11 ||
                V_SQL12 || V_SQLTBNEW || V_SQL2 || V_SQL4 || V_SQL5 ||
                V_SQL6 || V_SQL18 || ',LINE_NO');

      EXECUTE IMMEDIATE (V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 ||
                        V_SQL11 || V_SQL12 || V_SQLTBNEW || V_SQL2 ||
                        V_SQL4 || V_SQL5 || V_SQL6 || V_SQL18 ||
                        ',LINE_NO')
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;
      /*EXECUTE IMMEDIATE (V_SQL13 || V_SQL15 || V_SQL16 || V_SQL17 ||
            V_SQL11 || V_SQL12 || V_SQL14 || V_SQL5 ||
            V_SQL6 || V_SQL18)
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/
      DBMS_OUTPUT.put_line('--------2--------------------');
      print_out(V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 || V_SQL17 ||
                V_SQL11 || V_SQL12 || V_SQLBNEW || V_SQL14 || V_SQL5 ||
                V_SQL6 || V_SQL18);

      EXECUTE IMMEDIATE (V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 ||
                        V_SQL17 || V_SQL11 || V_SQL12 || V_SQLBNEW ||
                        V_SQL14 || V_SQL5 || V_SQL6 || V_SQL18)
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;

    END IF;
    --@@ Commented by subrahmanyam for the 219973 for Dynamic SurCharges.
    DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164

    IF P_OPERATION = 'Modify' THEN
      BEGIN
        SELECT DISTINCT QR.SELL_BUY_FLAG,
                        QM.ID,
                        QR.BUYRATE_ID,
                        QR.SELLRATE_ID,
                        QR.RATE_LANE_NO
          INTO V_SELLBUYFLAG,
               V_ID,
               V_CON_BUYRATEID,
               V_CON_SELLRATEID,
               V_CON_LANENO
        /*FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM -- commented and modified by phani sekhar for wpbn 178377 on 20090803
              WHERE  QR.QUOTE_ID = QM.ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QM.ACTIVE_FLAG = 'A'
                     AND
                     QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');*/
        -- added by phani sekhar for wpbn 178377 on 20090803
          FROM QMS_QUOTE_RATES  QR,
               QMS_QUOTE_MASTER QM,
               FS_RT_PLAN       PL,
               FS_RT_LEG        LEG
         WHERE QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
               (SELECT MAX(VERSION_NO)
                  FROM QMS_QUOTE_MASTER
                 WHERE QUOTE_ID = P_QUOTE_ID)
           AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');
        --ends 178377
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_SELLBUYFLAG := '';
      END;
      SELECT BREAK_POINT, charge_description BULK COLLECT
        INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
        FROM QMS_QUOTE_RATES
       WHERE QUOTE_ID = V_ID
         AND SELL_BUY_FLAG = V_SELLBUYFLAG;
      FORALL K IN 1 .. BREAK_POINT_LIST.COUNT
        UPDATE GT_TEMP_DATA_1
           SET CHECKED_FLAG = 'Y'
         WHERE BUYRATEID = (CASE WHEN V_SELLBUYFLAG = 'BR' THEN
                V_CON_BUYRATEID ELSE V_CON_SELLRATEID END)
           AND LANE_NO = V_CON_LANENO
           AND WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(K)
           AND RATE_DESCRIPTION = CHARGE_DESC_LIST(K);
    END IF;
    SELECT COUNT(*) INTO v_count FROM GT_TEMP_DATA_1;
     DBMS_OUTPUT.put_line('count123----'||v_count);
    FOR J IN (SELECT DISTINCT BUYRATEID,
                              VERSION_NO,
                              LANE_NO,
                              WEIGHT_BREAK,
                              RCB_FLAG,
                              NVL(REC_BUYRATE_ID, 'P') REC_BUYRATE_ID
                FROM GT_BASE_DATA
               WHERE (RATE_DESCRIPTION LIKE 'A FREIGHT RATE' OR RATE_DESCRIPTION LIKE 'Freight Rate')
               ORDER BY BUYRATEID, LANE_NO) LOOP
      V_CHARGERATE      := '';
      V_CHECKEDFLAG     := '';
      K                 := 1;
      V_BREAK           := '';
      v_surcharge_count := 0;
      V_TEMP_BREAK      := '';
      V_TEMP_CHARGERATE := '';
      V_RATE_DESC       := '';
      IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1) = FALSE THEN
        BEGIN
          EXECUTE IMMEDIATE ('select distinct WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND WEIGHT_BREAK_SLAB=''BASIC'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_TEMP_BREAK;
          V_CHARGERATE := V_TEMP_CHARGERATE || ',';
          V_RATE_DESC  := V_TEMP_DESC || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count + 1;
            V_BREAK           := 'BASIC';
            V_CHARGERATE      := '-' || ',';
            V_RATE_DESC       := 'A FREIGHT RATE' || ',';
            DBMS_OUTPUT.put_line( 'select distinct WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID ='||J.BUYRATEID||'
			       AND LANE_NO='||J.LANE_NO||' AND line_no=0  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')='||J.REC_BUYRATE_ID );
            EXECUTE IMMEDIATE ('select distinct WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no>=0  AND WEIGHT_BREAK_SLAB=''MIN''AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
              INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
              USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
            V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
            V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        END;
        IF v_surcharge_count = 0 THEN
          EXECUTE IMMEDIATE ('select distinct WEIGHT_BREAK_SLAB,to_char(CHARGERATE)  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=1  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
          V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
          V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';

        END IF;
      END IF;
      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      ELSIF UPPER(J.WEIGHT_BREAK) = 'FLAT' OR
            UPPER(J.WEIGHT_BREAK) = 'SLAB' THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      END IF;
      IF (J.WEIGHT_BREAK <> 'FLAT') THEN
        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          --            V_SQL1 := ' AND line_no > 0 order by break_slab';
          V_SQL1 := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') order by break_slab';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          V_SQL1 := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                    J.LANE_NO || ' order by break_slab';
        ELSE
          --           V_SQL1 := ' AND line_no > 0 order by to_number(break_slab)';-- COMMENTED FOR 180164
          V_SQL1 := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') order by to_number(break_slab)';
          -- V_SQL1 := ' AND line_no > 0 order by LINE_NO';
        END IF;
        -- COMMENTED FOR 180164
        --  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1 -- commented by subrahmanyam for 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL1
          USING J.WEIGHT_BREAK;

        --ADDED FOR 180164
        /*  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
                    USING J.WEIGHT_BREAK;
        */
        LOOP
          FETCH V_RC_C1
          --INTO V_BREAK_SLAB,V_LINE_NO ;--ADDED FOR 180164
            INTO V_BREAK_SLAB; -- COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            /*            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
            AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
            and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
            */
            EXECUTE IMMEDIATE (' SELECT distinct TO_CHAR(chargerate), checked_flag,RATE_DESCRIPTION FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
              INTO V_TEMP, V_FLAG, V_TEMP_DESC
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-' || ',';
          END;
          /*If UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2
           Then
           V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            BEGIN
            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
           AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_BAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CSF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_PSS, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;

           End If;*/
        END LOOP;
        CLOSE V_RC_C1;

        --@@ Added by subrahmanyam for the 180164
        V_TEMP_DESC := '';
        /*      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
                    V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
                    V_SQL_SUR := ' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSE
                   V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'')  AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and  '||j.buyrateid ||' and '||j.lane_no ||' and to_number(break_slab)';
              END IF;
        */

        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_SQL_SUR := ' AND line_no > 0 AND /*WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND*/ RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no ';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          --            V_SQL_SUR := ' and buyrateid='||j.buyrateid ||' and lane_no='||j.lane_no ||' order by line_no  order by line_no ';
          V_SQL_SUR := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                       J.LANE_NO || ' order by break_slab ';
        ELSE
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no';
        END IF;
        V_TEMP_BREAK := '';
        OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
          USING J.WEIGHT_BREAK;
        LOOP
          FETCH V_RC_C1
            INTO V_BREAK_SLAB, V_TEMP_DESC;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
              INTO V_TEMP, V_FLAG
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
            V_TEMP_BREAK  := V_TEMP_BREAK || V_BREAK_SLAB || ',';
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_TEMP_BREAK  := V_BREAK_SLAB || ',';
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-,';
          END;

        END LOOP;
        CLOSE V_RC_C1;
        /*   IF P_SHMODE =1 THEN
                   \* V_BREAK_SLAB:='FSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB:='FSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='FSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB    :='SSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;*\
                    DBMS_OUTPUT.put_line('----FOR AIR-----');
                  ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE =2
                  THEN
                      V_BREAK_SLAB    :='CAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CAF%';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CSF';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                ELSIF P_SHMODE =4 THEN
                V_BREAK_SLAB    :='SURCHARGE';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
             END IF;
        */
        --@@ Ended by subrahmanyam for the 180164

        V_BASE  := V_BREAK;
        V_BREAK := '';
        -- COMMENTED FOR 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and weight_break =:weight_break' || V_SQL1
          USING J.WEIGHT_BREAK;
        -- ADDED FOR 180164
        /* OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
        USING J.WEIGHT_BREAK;*/
        LOOP
          FETCH V_RC_C1
          -- INTO V_BREAK_SLAB,V_LINE_NO;--ADDED FOR 180164
            INTO V_BREAK_SLAB; --COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
            /*V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';*/

            -- V_BREAK :=V_BREAK||','||V_BREAK_SLAB_BAF||','||V_BREAK_SLAB_CAF||','||V_BREAK_SLAB_CSF||','||V_BREAK_SLAB_PSS;
          ELSIF P_SHMODE = 1 OR P_SHMODE = 4 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          END IF;
        END LOOP;
        CLOSE V_RC_C1;
        --@@Added by subrahmanyam for 180164
        IF P_SHMODE = 1 THEN
          --V_BREAK := V_BREAK || ',' ||'FSBASIC'||','||'FSMIN'||','||'FSKG'||','||'SSBASIC'||','||'SSMIN'||','||'SSKG';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
          -- ELSIF UPPER(J.WEIGHT_BREAK) != 'LIST' AND P_SHMODE = 2 THEN
        ELSIF P_SHMODE = 2 THEN
          -- V_BREAK := V_BREAK ||','||'CAFMIN'||','||'CAF%'||','||'BAFMIN'||','||'BAFM3'||','||'PSSMIN'||','||'PSSM3'||','||'CSF';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        ELSIF P_SHMODE = 4 THEN
          V_BREAK := V_BREAK || ',' || 'SURCHARGE';
        END IF;

        --@@ Ended by subrahmanyam for 180164
        IF (UPPER(J.WEIGHT_BREAK) = 'SLAB') THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSE
          V_BREAK := SUBSTR(V_BREAK, 2, LENGTH(V_BREAK));
        END IF;
      ELSE
        SELECT WEIGHT_BREAK_SLAB, CHARGERATE, RATE_DESCRIPTION
          INTO V_BASE, V_TEMP, V_TEMP_DESC
          FROM GT_TEMP_DATA_1
         WHERE LINE_NO > 0
           AND WEIGHT_BREAK_SLAB NOT IN ('BASIC', 'MIN')
           AND BUYRATEID = J.BUYRATEID
           AND LANE_NO = J.LANE_NO
           AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
           AND RATE_DESCRIPTION = 'A FREIGHT RATE';
        V_BREAK      := V_BREAK || ',' || V_BASE;
        V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
        V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2 THEN
          /*BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAF%' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN

            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
              dbms_output.put_line('V_BREAK'||V_BREAK);
            EXCEPTION WHEN NO_DATA_FOUND THEN
              V_BREAK      := V_BREAK || ',' || 'BAFM3';
              V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
          --  dbms_output.put_line('V_BREAK'||V_BREAK);
            dbms_output.put_line('V_CHARGERATE'||V_CHARGERATE);
            dbms_output.put_line('J.BUYRATEID'||J.BUYRATEID);
             dbms_output.put_line('J.LANE_NO'||J.LANE_NO);
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

             V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CSF' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;*/

          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no';
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 1 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          --COMMENTED BY SUBRAHMANYAM FOR 219973
          -- BEGIN

          DBMS_OUTPUT.put_line('--------COMMENTED BY SUBRAHMANYAM-------');
          /* SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;*/
          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no';
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 4 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN
          BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
              INTO V_BASE, V_TEMP
              FROM GT_TEMP_DATA_1
             WHERE LINE_NO > 0
               AND BUYRATEID = J.BUYRATEID
               AND LANE_NO = J.LANE_NO
               AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
               AND RATE_DESCRIPTION <> 'A FREIGHT RATE'
               AND WEIGHT_BREAK_SLAB = 'SURCHARGE';
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE := V_CHARGERATE || '-,';
          END;

        END IF;
      END IF;
      --@@ Commented by subrahmanyam for 219973
      /*  IF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =2) THEN
      V_BREAK := 'MIN,FLAT,CAFMIN,CAF%,BAFMIN,BAFM3,PSSMIN,PSSM3,CSF';
      ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =1) THEN
        V_BREAK := 'MIN,FLAT,FSBASIC,FSMIN,FSKG,,SSBASIC,SSMIN,SSKG';
        ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =4) THEN
          V_BREAK := 'MIN,FLAT,SURCHARGE';
      END IF;*/

      UPDATE GT_BASE_DATA
         SET WEIGHT_BREAK_SLAB = V_BREAK,
             RATE_DESCRIPTION  = V_RATE_DESC,
             CHARGERATE        = SUBSTR(V_CHARGERATE,
                                        1,
                                        LENGTH(V_CHARGERATE) - 1),
             CHECKED_FLAG      = SUBSTR(V_CHECKEDFLAG,
                                        1,
                                        LENGTH(V_CHECKEDFLAG) - 1)
       WHERE BUYRATEID = J.BUYRATEID
         AND LANE_NO = J.LANE_NO
         AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID;
      DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164
      IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'COPY' THEN
        print_out('-------ERROR------');
        DBMS_OUTPUT.put_line('SELECT RCB_FLAG
			INTO   V_RCB_FLAG
			FROM   GT_BASE_DATA
			WHERE  BUYRATEID = ' || J.BUYRATEID || '
			       AND LANE_NO =' || J.LANE_NO || '
			       AND VERSION_NO =' || J.VERSION_NO || '
			       AND NVL(REC_BUYRATE_ID
				      ,''P'') =' || J.REC_BUYRATE_ID || '
               AND RATE_DESCRIPTION = ' ||
                             '''A FREIGHT RATE''''');
        BEGIN
          V_RCB_FLAG := '';
          SELECT DISTINCT RCB_FLAG
            INTO V_RCB_FLAG
            FROM GT_BASE_DATA
           WHERE BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO
             AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID
          --  AND RATE_DESCRIPTION = 'A FREIGHT RATE'
          ;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('V_RCB_FLAG..' || V_RCB_FLAG);
        END;
        IF (V_RCB_FLAG = 'BR') THEN
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.BUYRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG = 'BR'
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        ELSE
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.SELLRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.REC_BUYRATE_ID = QR.BUYRATE_ID
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG IN ('RSR', 'CSR')
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        END IF;
      END IF;
    END LOOP;
    -- @@ Commented And Added for 219973
    /*       IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA WHERE RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID;-- COMMENTED BY SUBRAHMANYAM FOR 179985
        ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      ELSE
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA
      WHERE  RCB_FLAG <> 'BR' AND RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID; -- COMMENTED BY SUBRAHMANYAM FOR 179855
              ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      END IF;*/

    IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
    END IF;
    --@@ Ended for 219973
  END;

  /*
  This procedure is used for fetching the Buy/Sell Charges for Quote 3rd Screen.
  The Procedure for fetching Tied Customer Info (if any) is called from within this procedure.
  It Returns a resultset object.
  The IN Parameters are:
   p_chargegrps              VARCHAR2,
     p_salespersoncode         VARCHAR2,
     p_terminalid              VARCHAR2,
     p_permission              VARCHAR2 DEFAULT 'Y',
     p_margin                  NUMBER,
     p_origin                  VARCHAR2,
     p_destination             VARCHAR2,
     p_shmode                  VARCHAR2,
     p_customerid              VARCHAR2,
     p_operation               VARCHAR2,
     p_quote_id                VARCHAR2
  The OUT Parameter is:
   p_rs           OUT   resultset
  */
  PROCEDURE quote_sell_buy_charges_proc(p_chargegrps      VARCHAR2,
                                        p_salespersoncode VARCHAR2,
                                        p_terminalid      VARCHAR2,
                                        p_permission      VARCHAR2 DEFAULT 'Y',
                                        p_margin          NUMBER,
                                        p_discount        NUMBER,
                                        p_origin          VARCHAR2,
                                        p_destination     VARCHAR2,
                                        p_shmode          VARCHAR2,
                                        p_customerid      VARCHAR2,
                                        p_operation       VARCHAR2,
                                        p_quote_id        VARCHAR2,
                                        p_rs              OUT resultset) AS
    v_sql1  VARCHAR2(4000) := '';
    v_sql2  VARCHAR2(4000) := '';
    v_sql3  VARCHAR2(4000) := '';
    v_sql4  VARCHAR2(4000) := '';
    v_sql5  VARCHAR2(32767) := '';
    v_sql6  VARCHAR2(4000) := '';
    v_sql7  VARCHAR2(4000) := '';
    v_sql8  VARCHAR2(32767) := '';
    v_sql9  VARCHAR2(4000) := '';
    v_sql10 VARCHAR2(32767) := '';
    v_sql11 VARCHAR2(4000) := '';
    --    v_margin               NUMBER(8, 2) := 0; --@@ commented and added by subrahmanyam  for 201370
    v_margin               NUMBER(20, 5) := 0;
    v_terminals            VARCHAR2(32000);
    v_opr_adm_flag         VARCHAR2(3);
    v_marginid             VARCHAR2(10);
    v_margin_discount_flag VARCHAR2(10);
    v_margin_type          VARCHAR2(10);
    v_discount_type        VARCHAR2(10);
    -- v_discount             NUMBER(8, 2) := 0.0; --@@ commented and added by subrahmanyam  for 201370
    v_discount NUMBER(20, 5) := 0.0;
    -------------------For Ordering the Charges---------------
    v_Pos           NUMBER(3);
    v_Pos1          NUMBER(3) := 1;
    v_Occ_Num       NUMBER(3) := 1;
    v_Start         NUMBER(3) := 0;
    v_Char          VARCHAR2(100);
    v_lane_no       NUMBER(3) := 1;
    v_charge_groups VARCHAR2(32767);
    -----------------------------------------------------------
    v_count NUMBER(2) := 0;
    --C1_QUOTEID        sys_refcursor;
    v_countquoteid VARCHAR2(10);
    CURSOR C1_DEST_OPER IS(
      SELECT NVL(Margin_Discount_Flag, 'M') Margin_Discount_Flag,
             Margin_Type,
             Margin,
             Discount_Type,
             Discount,
             Break_Point,
             Buyrate_Id,
             Sellrate_Id,
             Charge_Id,
             Charge_Description
        FROM QMS_QUOTE_RATES Qr
       WHERE Quote_Id =
             (SELECT MAX(Qm.Id)
                FROM QMS_QUOTE_RATES Iqr, QMS_QUOTE_MASTER Qm
               WHERE Qm.Id = Iqr.Quote_Id
                 AND Qm.Dest_Location = p_destination
                 AND Qm.Customer_Id = p_customerid
                 AND Qm.Shipment_Mode = p_shmode
                 AND Qm.Ie_Flag = 'E'
                 AND ((qm.active_flag = 'I' AND
                     qm.quote_status NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                     (qm.active_flag = 'A'))
                 AND Iqr.Sell_Buy_Flag IN ('B', 'S')
                 AND UPPER(Charge_At) = 'DESTINATION'
                 AND Qr.Charge_Id = Iqr.Charge_Id
                 AND Qr.Charge_Description = Iqr.Charge_Description
                 AND Qm.Terminal_Id IN
                     (SELECT Parent_Terminal_Id Term_Id
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
                       START WITH Child_Terminal_Id = p_terminalid
                      UNION ALL
                      SELECT Terminalid Term_Id
                        FROM FS_FR_TERMINALMASTER
                       WHERE Oper_Admin_Flag = 'H'
                      UNION ALL
                      SELECT p_terminalid Term_Id
                        FROM Dual
                      UNION ALL
                      SELECT Child_Terminal_Id Term_Id
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                       START WITH Parent_Terminal_Id = p_terminalid)));

    CURSOR C1_DEST_HO IS(
      SELECT NVL(Margin_Discount_Flag, 'M') Margin_Discount_Flag,
             Margin_Type,
             Margin,
             Discount_Type,
             Discount,
             Break_Point,
             Buyrate_Id,
             Sellrate_Id,
             Charge_Id,
             Charge_Description
        FROM QMS_QUOTE_RATES Qr
       WHERE Quote_Id =
             (SELECT MAX(Qm.Id)
                FROM QMS_QUOTE_RATES Iqr, QMS_QUOTE_MASTER Qm
               WHERE Qm.Id = Iqr.Quote_Id
                 AND Qm.Dest_Location = p_destination
                 AND Qm.Customer_Id = p_customerid
                 AND Qm.Shipment_Mode = p_shmode
                 AND Qm.Ie_Flag = 'E'
                 AND ((qm.active_flag = 'I' AND
                     qm.quote_status NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                     (qm.active_flag = 'A'))
                 AND Iqr.Sell_Buy_Flag IN ('B', 'S')
                 AND UPPER(Charge_At) = 'DESTINATION'
                 AND Qr.Charge_Id = Iqr.Charge_Id
                 AND Qr.Charge_Description = Iqr.Charge_Description
                 AND Qm.Terminal_Id IN
                     (SELECT terminalid FROM FS_FR_TERMINALMASTER)));

    CURSOR C1_ORIGIN_OPER IS(
      SELECT NVL(Margin_Discount_Flag, 'M') Margin_Discount_Flag,
             Margin_Type,
             Margin,
             Discount_Type,
             Discount,
             Break_Point,
             Buyrate_Id,
             Sellrate_Id,
             Charge_Id,
             Charge_Description
        FROM QMS_QUOTE_RATES Qr
       WHERE Quote_Id =
             (SELECT MAX(Qm.Id)
                FROM QMS_QUOTE_RATES Iqr, QMS_QUOTE_MASTER Qm
               WHERE Qm.Id = Iqr.Quote_Id
                 AND Qm.Origin_Location = p_origin
                 AND Qm.Customer_Id = p_customerid
                 AND Qm.Shipment_Mode = p_shmode
                 AND Qm.Ie_Flag = 'E'
                 AND ((qm.active_flag = 'I' AND
                     qm.quote_status NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                     (qm.active_flag = 'A'))
                 AND Iqr.Sell_Buy_Flag IN ('B', 'S')
                 AND UPPER(Charge_At) = 'ORIGIN'
                 AND Qr.Charge_Id = Iqr.Charge_Id
                 AND Qr.Charge_Description = Iqr.Charge_Description
                 AND Qm.Terminal_Id IN
                     (SELECT Parent_Terminal_Id Term_Id
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
                       START WITH Child_Terminal_Id = p_terminalid
                      UNION
                      SELECT Terminalid Term_Id
                        FROM FS_FR_TERMINALMASTER
                       WHERE Oper_Admin_Flag = 'H'
                      UNION
                      SELECT p_terminalid Term_Id
                        FROM Dual
                      UNION
                      SELECT Child_Terminal_Id Term_Id
                        FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                       START WITH Parent_Terminal_Id = p_terminalid)));

    CURSOR C1_ORIGIN_HO IS(
      SELECT NVL(Margin_Discount_Flag, 'M') Margin_Discount_Flag,
             Margin_Type,
             Margin,
             Discount_Type,
             Discount,
             Break_Point,
             Buyrate_Id,
             Sellrate_Id,
             Charge_Id,
             Charge_Description

        FROM QMS_QUOTE_RATES Qr
       WHERE Quote_Id =
             (SELECT MAX(Qm.Id)
                FROM QMS_QUOTE_RATES Iqr, QMS_QUOTE_MASTER Qm
               WHERE Qm.Id = Iqr.Quote_Id
                 AND Qm.Origin_Location = p_origin
                 AND Qm.Customer_Id = p_customerid
                 AND Qm.Shipment_Mode = p_shmode
                 AND Qm.Ie_Flag = 'E'
                 AND ((qm.active_flag = 'I' AND
                     qm.quote_status NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                     (qm.active_flag = 'A'))
                 AND Iqr.Sell_Buy_Flag IN ('B', 'S')
                 AND UPPER(Charge_At) = 'ORIGIN'
                 AND Qr.Charge_Id = Iqr.Charge_Id
                 AND Qr.Charge_Description = Iqr.Charge_Description
                 AND Qm.Terminal_Id IN
                     (SELECT terminalid FROM FS_FR_TERMINALMASTER)));

    CURSOR C3(v_char2 VARCHAR2) IS
      SELECT Charge_Id, Chargedescid, Terminalid
        FROM QMS_CHARGE_GROUPSMASTER
       WHERE Chargegroup_Id = v_char2
         AND Inactivate = 'N';

    --------------------------------------------------- variable to store the DO data ------------------------
    TYPE DO_Margin_Discount_FlagTab IS TABLE OF QMS_QUOTE_RATES.Margin_Discount_Flag%TYPE;
    TYPE DO_Margin_TypeTab IS TABLE OF QMS_QUOTE_RATES.Margin_Type%TYPE;
    TYPE DO_MarginTab IS TABLE OF QMS_QUOTE_RATES.Margin%TYPE;
    TYPE DO_Discount_TypeTab IS TABLE OF QMS_QUOTE_RATES.Discount_Type%TYPE;
    TYPE DO_DiscountTab IS TABLE OF QMS_QUOTE_RATES.Discount%TYPE;
    TYPE DO_Break_PointTab IS TABLE OF QMS_QUOTE_RATES.Break_Point%TYPE;
    TYPE DO_Buyrate_IdTab IS TABLE OF QMS_QUOTE_RATES.Buyrate_Id%TYPE;
    TYPE DO_Sellrate_IdTab IS TABLE OF QMS_QUOTE_RATES.Sellrate_Id%TYPE;
    TYPE DO_Charge_IdTab IS TABLE OF QMS_QUOTE_RATES.Charge_Id%TYPE;
    TYPE DO_Charge_DescriptionTab IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    -----------------------------------------------------------------------------------------------------------------
    --------------------------------------------------- variable to store the 0O data ------------------------
    TYPE OO_Margin_Discount_FlagTab IS TABLE OF QMS_QUOTE_RATES.Margin_Discount_Flag%TYPE;
    TYPE OO_Margin_TypeTab IS TABLE OF QMS_QUOTE_RATES.Margin_Type%TYPE;
    TYPE OO_MarginTab IS TABLE OF QMS_QUOTE_RATES.Margin%TYPE;
    TYPE OO_Discount_TypeTab IS TABLE OF QMS_QUOTE_RATES.Discount_Type%TYPE;
    TYPE OO_DiscountTab IS TABLE OF QMS_QUOTE_RATES.Discount%TYPE;
    TYPE OO_Break_PointTab IS TABLE OF QMS_QUOTE_RATES.Break_Point%TYPE;
    TYPE OO_Buyrate_IdTab IS TABLE OF QMS_QUOTE_RATES.Buyrate_Id%TYPE;
    TYPE OO_Sellrate_IdTab IS TABLE OF QMS_QUOTE_RATES.Sellrate_Id%TYPE;
    TYPE OO_Charge_IdTab IS TABLE OF QMS_QUOTE_RATES.Charge_Id%TYPE;
    TYPE OO_Charge_DescriptionTab IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    -----------------------------------------------------------------------------------------------------------------
    --------------------------------------------------- variable to store the DO data ------------------------
    DO_Margin_Discount_Flag DO_Margin_Discount_FlagTab;
    DO_Margin_Type          DO_Margin_TypeTab;
    DO_Margin               DO_MarginTab;
    DO_Discount_Type        DO_Discount_TypeTab;
    DO_Discount             DO_DiscountTab;
    DO_Break_Point          DO_Break_PointTab;
    DO_Buyrate_Id           DO_Buyrate_IdTab;
    DO_Sellrate_Id          DO_Sellrate_IdTab;
    DO_Charge_Id            DO_Charge_IdTab;
    DO_Charge_Description   DO_Charge_DescriptionTab;
    -----------------------------------------------------------------------------------------------------------------
    --------------------------------------------------- variable to store the 0O data ------------------------
    OO_Margin_Discount_Flag OO_Margin_Discount_FlagTab;
    OO_Margin_Type          OO_Margin_TypeTab;
    OO_Margin               OO_MarginTab;
    OO_Discount_Type        OO_Discount_TypeTab;
    OO_Discount             OO_DiscountTab;
    OO_Break_Point          OO_Break_PointTab;
    OO_Buyrate_Id           OO_Buyrate_IdTab;
    OO_Sellrate_Id          OO_Sellrate_IdTab;
    OO_Charge_Id            OO_Charge_IdTab;
    OO_Charge_Description   OO_Charge_DescriptionTab;
    -----------------------------------------------------------------------------------------------------------------
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = p_Terminalid;

    OPEN C1_DEST_OPER;
    FETCH C1_DEST_OPER BULK COLLECT
      INTO DO_Margin_Discount_Flag, DO_Margin_Type, DO_Margin, DO_Discount_Type, DO_Discount, DO_Break_Point, DO_Buyrate_Id, DO_Sellrate_Id, DO_Charge_Id, DO_Charge_Description;
    CLOSE C1_DEST_OPER;

    OPEN C1_ORIGIN_OPER;
    FETCH C1_ORIGIN_OPER BULK COLLECT
      INTO OO_Margin_Discount_Flag, OO_Margin_Type, OO_Margin, OO_Discount_Type, OO_Discount, OO_Break_Point, OO_Buyrate_Id, OO_Sellrate_Id, OO_Charge_Id, OO_Charge_Description;
    CLOSE C1_ORIGIN_OPER;

    IF UPPER(TRIM(v_opr_adm_flag)) = 'H' THEN
      v_terminals := 'SELECT TERMINALID FROM FS_FR_TERMINALMASTER';

    ELSE

      Dbms_Session.set_context('QUOTE_CONTEXT',
                               'v_terminal_id',
                               p_terminalid);

      v_terminals := 'SELECT     parent_terminal_id term_id
                            FROM FS_FR_TERMINAL_REGN
                      CONNECT BY child_terminal_id = PRIOR parent_terminal_id
                      START WITH child_terminal_id = sys_context(''QUOTE_CONTEXT'',''v_terminal_id'')
                      UNION
                      SELECT terminalid term_id
                        FROM FS_FR_TERMINALMASTER
                       WHERE oper_admin_flag = ''H''
                      UNION
                      SELECT sys_context(''QUOTE_CONTEXT'',''v_terminal_id'') term_id
                        FROM DUAL
                      UNION
                      SELECT     child_terminal_id term_id
                            FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                      START WITH parent_terminal_id = sys_context(''QUOTE_CONTEXT'',''v_terminal_id'')';
    END IF;

    v_margin := p_margin;

    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');

    v_sql1 := 'INSERT INTO TEMP_CHARGES(SELLCHARGEID, ID,CHARGE_ID,CHARGEDESCID, COST_INCURREDAT, CHARGESLAB, CURRENCY, BUYRATE, SELLRATE, MARGIN_TYPE, MARGINVALUE, CHARGEBASIS, SEL_BUY_FLAG,BUY_CHARGE_ID,TERMINALID,DENSITY_RATIO,line_no,RATE_INDICATOR,WEIGHT_BREAK,RATE_TYPE,INT_CHARGE_NAME,EXT_CHARGE_NAME,RATE_DESCRIPTION) ';
    v_sql2 := 'SELECT sm.sellchargeid,  (select max(id) from qms_charge_groupsmaster where chargegroup_id IN (' ||
              qms_rsr_rates_pkg.seperator(p_chargegrps) ||
              ') and charge_id = sm.charge_id and chargedescid = sm.chargedescid)id , sm.charge_id, sm.chargedescid, (SELECT cost_incurredat  FROM QMS_CHARGESMASTER   WHERE charge_id = sm.charge_id) cost_incurredat, sd.chargeslab,  sm.currency, chargerate buyrate,';

    v_sql3 := 'DECODE (margin_type,''A'', chargerate + marginvalue, ''P'', chargerate + (marginvalue * .01 * chargerate) ) sellrate, margin_type,' ||
              v_discount || ' marginvalue, ';
    v_sql4 := '(SELECT basis_description  FROM QMS_CHARGE_BASISMASTER   WHERE chargebasis = sm.chargebasis) chargebasis,''S'' Sel_buy_flag,(select to_char(buysellchargeid) from QMS_BUYSELLCHARGESMASTER bs where bs.buysellchargeid=sm.buychargeid) buychargeid,sm.TERMINALID,(select distinct DENSITY_CODE  from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=buychargeid ) dcode,sd.lane_no line_no,sd.CHARGERATE_INDICATOR,sm.RATE_BREAK,sm.RATE_TYPE,
          (SELECT REMARKS FROM QMS_CHARGEDESCMASTER qcd, QMS_SELLCHARGESMASTER sm WHERE qcd.CHARGEID=sm.charge_id AND qcd.CHARGEDESCID=sm.chargedescid  AND qcd.INACTIVATE=''N'' AND qcd.terminalid IN(' ||
              v_terminals ||
              ') and sm.sellchargeid = sd.sellchargeid)INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd, QMS_SELLCHARGESMASTER sm WHERE qcd.CHARGEID=sm.charge_id AND qcd.CHARGEDESCID=sm.chargedescid  AND qcd.INACTIVATE=''N'' and qcd.terminalid in(' ||
              v_terminals ||
              ') and sm.sellchargeid = sd.sellchargeid)EXT_NAME,''FREIGHT RATE'' FROM QMS_SELLCHARGESMASTER sm, QMS_SELLCHARGESDTL sd ';
    v_sql5 := '  WHERE sm.sellchargeid = sd.sellchargeid AND SM.IE_FLAG=''A'' AND SM.TERMINALID IN (' ||
              v_terminals ||
              ') and (sm.CHARGE_ID ,sm.CHARGEDESCID) not in (select distinct CHARGE_ID, CHARGEDESCID from temp_charges) ' ||
              ' AND (sm.charge_id, sm.chargedescid) IN ( SELECT cg.charge_id, UPPER (cg.chargedescid)  FROM QMS_CHARGE_GROUPSMASTER cg   WHERE cg.chargegroup_id IN  (' ||
              qms_rsr_rates_pkg.seperator(p_chargegrps);
    v_sql6 := ')  And Cg.Inactivate =''N'' AND TERMINALID IN (' ||
              v_terminals || ') )' ||
              ' UNION ALL SELECT sm.buysellchargeid sellchargeid,  (select max(id) from qms_charge_groupsmaster where chargegroup_id IN (' ||
              qms_rsr_rates_pkg.seperator(p_chargegrps) ||
              ') and charge_id = sm.charge_id and chargedescid = sm.chargedescid)id ,sm.charge_id, sm.chargedescid, (SELECT cost_incurredat   FROM QMS_CHARGESMASTER  WHERE charge_id = sm.charge_id) cost_incurredat, sd.chargeslab,  sm.currency, chargerate buyrate, 0.0 sellrate, ''P'' margin_type, ' ||
             -- v_margin || ' marginvalue, (SELECT basis_description';
               'TO_NUMBER(0) marginvalue, (SELECT basis_description';

    v_sql7 := '   FROM QMS_CHARGE_BASISMASTER  WHERE chargebasis = sm.chargebasis) chargebasis,''B'' Sel_buy_flag,'''' buychargeid,sm.TERMINALID,sm.DENSITY_CODE, sd.lane_no line_no,sd.CHARGERATE_INDICATOR,sm.RATE_BREAK,sm.RATE_TYPE,
             (SELECT REMARKS FROM QMS_CHARGEDESCMASTER qcd,QMS_BUYSELLCHARGESMASTER sm WHERE qcd.CHARGEID=sm.charge_id AND qcd.CHARGEDESCID=sm.chargedescid  AND qcd.INACTIVATE=''N'' AND qcd.terminalid IN(' ||
              v_terminals ||
              ') and sm.buysellchargeid = sd.buysellchaegeid)INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd, QMS_BUYSELLCHARGESMASTER sm WHERE qcd.CHARGEID=sm.charge_id AND qcd.CHARGEDESCID=sm.chargedescid AND qcd.INACTIVATE=''N'' and qcd.terminalid in(' ||
              v_terminals ||
              ') and sm.buysellchargeid = sd.buysellchaegeid)EXT_NAME,''FREIGHT RATE''  FROM QMS_BUYSELLCHARGESMASTER sm, QMS_BUYCHARGESDTL sd ';
    v_sql8 := ' WHERE sm.buysellchargeid = sd.buysellchaegeid AND SM.DEL_FLAG=''N'' AND SM.TERMINALID IN (' ||
              v_terminals || ') ' ||
              ' AND (sm.charge_id, sm.chargedescid) IN (SELECT cg.charge_id, UPPER (cg.chargedescid)';
    --|| ' And Cgm.Charge_Id = Sm.Charge_Id And Cgm.Chargedescid = Sm.Chargedescid And Cgm.Chargegroup_Id In (';
    v_sql9 := ' FROM QMS_CHARGE_GROUPSMASTER cg WHERE cg.chargegroup_id IN (' ||
              qms_rsr_rates_pkg.seperator(p_chargegrps) ||
              ') AND Cg.INACTIVATE=''N'' AND TERMINALID IN (' ||
              v_terminals ||
              ')) and (sm.CHARGE_ID ,sm.CHARGEDESCID) not in (select distinct CHARGE_ID, CHARGEDESCID from temp_charges) AND (sm.charge_id, sm.chargedescid) NOT IN ( SELECT sm.charge_id, sm.chargedescid FROM qms_sellchargesmaster sm, qms_sellchargesdtl sd';
    --' FROM QMS_CHARGE_GROUPSMASTER cg WHERE cg.chargegroup_id IN ('

    v_sql10 := ' WHERE sm.sellchargeid = sd.sellchargeid AND SM.TERMINALID IN (' ||
               v_terminals ||
               ') AND (sm.charge_id, sm.chargedescid) IN (SELECT cg.charge_id, UPPER (cg.chargedescid) FROM qms_charge_groupsmaster cg ';
    v_sql11 := ' WHERE cg.chargegroup_id IN ( ' ||
               qms_rsr_rates_pkg.seperator(p_chargegrps) ||
               ') AND INACTIVATE=''N'' AND TERMINALID IN (' || v_terminals ||
               ')))';
    print_out(v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 || v_sql6 ||
              v_sql7 || v_sql8 || v_sql9 || v_sql10 || v_sql11);
    EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                      v_sql6 || v_sql7 || v_sql8 || v_sql9 || v_sql10 ||
                      v_sql11);
    COMMIT;
    IF UPPER(p_operation) = 'MODIFY' THEN
      FOR j IN (SELECT Buy_Charge_Id,
                       Charge_Id,
                       Chargedescid,
                       Sellchargeid,
                       Sel_Buy_Flag
                  FROM TEMP_CHARGES
                 ORDER BY Buy_Charge_Id) LOOP
        IF (j.sel_buy_flag = 'B') THEN
          UPDATE TEMP_CHARGES
             SET Selected_Flag = 'Y'
           WHERE (Charge_Id, Chargedescid, Sellchargeid) IN
                 (SELECT Charge_Id, Charge_Description, Buyrate_Id
                    FROM QMS_QUOTE_RATES
                   WHERE Quote_Id IN
                         (SELECT Id
                            FROM QMS_QUOTE_MASTER a
                           WHERE Quote_Id = p_Quote_Id
                             AND Version_No =
                                 (SELECT MAX(Version_No)
                                    FROM QMS_QUOTE_MASTER b
                                   WHERE a.Quote_Id = b.Quote_Id
                                     AND a.multi_quote_lane_no =
                                         b.multi_quote_lane_no)))
             AND Sellchargeid = j.Sellchargeid
             AND Charge_Id = j.Charge_Id
             AND Chargedescid = j.Chargedescid;
        ELSE
          UPDATE TEMP_CHARGES
             SET Selected_Flag = 'Y'
           WHERE (Charge_Id, Chargedescid, Buy_Charge_Id, Sellchargeid) IN
                 (SELECT Charge_Id,
                         Charge_Description,
                         Buyrate_Id,
                         Sellrate_Id
                    FROM QMS_QUOTE_RATES
                   WHERE Quote_Id IN
                         (SELECT Id
                            FROM QMS_QUOTE_MASTER a
                           WHERE Quote_Id = p_Quote_Id
                             AND Version_No =
                                 (SELECT MAX(Version_No)
                                    FROM QMS_QUOTE_MASTER b
                                   WHERE a.Quote_Id = b.Quote_Id)))
             AND Buy_Charge_Id = j.Buy_Charge_Id
             AND Sellchargeid = j.Sellchargeid
             AND Charge_Id = j.Charge_Id
             AND Chargedescid = j.Chargedescid;

        END IF;
      END LOOP;
      COMMIT;
    END IF;

    IF UPPER(p_operation) = 'ADD' OR UPPER(p_operation) = 'COPY' THEN
      FOR j IN (SELECT Buy_Charge_Id,
                       Charge_Id,
                       Chargedescid,
                       Sellchargeid,
                       Sel_Buy_Flag,
                       cost_incurredat,
                       chargeslab
                  FROM TEMP_CHARGES
                 ORDER BY Buy_Charge_Id) LOOP

        IF (j.sel_buy_flag = 'B') THEN
          IF UPPER(TRIM(v_opr_adm_flag)) = 'H' THEN
            IF UPPER(j.cost_incurredat) = 'ORIGIN' THEN
              FOR sto IN C1_ORIGIN_HO LOOP
                IF (sto.charge_id = j.charge_id AND
                   sto.charge_description = j.chargedescid AND
                   sto.buyrate_id = j.sellchargeid AND
                   sto.Break_Point = j.chargeslab) THEN
                  UPDATE TEMP_CHARGES
                     SET Selected_Flag        = 'Y',
                         Margin_Type          = DECODE(sto.Margin_Discount_Flag,
                                                       'M',
                                                       sto.Margin_Type,
                                                       sto.Discount_Type),
                         Marginvalue          = DECODE(sto.Margin_Discount_Flag,
                                                       'M',
                                                       sto.Margin,
                                                       sto.Discount),
                         Margin_Discount_Flag = sto.Margin_Discount_Flag
                   WHERE Chargeslab = j.Chargeslab
                     AND Sellchargeid = j.Sellchargeid
                     AND Charge_Id = j.Charge_Id
                     AND Chargedescid = j.Chargedescid;
                END IF;
              END LOOP;
            ELSE
              FOR str IN C1_DEST_HO LOOP
                IF (str.charge_id = j.charge_id AND
                   str.charge_description = j.chargedescid AND
                   str.buyrate_id = j.sellchargeid AND
                   str.Break_Point = j.chargeslab) THEN
                  UPDATE TEMP_CHARGES
                     SET Selected_Flag = 'Y',

                         Margin_Type          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin_Type,
                                                       str.Discount_Type),
                         Marginvalue          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin,
                                                       str.Discount),
                         Margin_Discount_Flag = str.Margin_Discount_Flag
                   WHERE Chargeslab = j.Chargeslab
                     AND Sellchargeid = j.Sellchargeid
                     AND Charge_Id = j.Charge_Id
                     AND Chargedescid = j.Chargedescid;
                END IF;
              END LOOP;

            END IF;
          ELSE
            IF UPPER(j.cost_incurredat) = 'ORIGIN' THEN
              IF OO_Margin_Discount_Flag.COUNT() > 0 THEN
                FOR i IN OO_charge_id.FIRST .. OO_charge_id.LAST LOOP
                  IF (OO_charge_id(i) = j.charge_id AND
                     OO_charge_description(i) = j.chargedescid AND
                     OO_buyrate_id(i) = j.sellchargeid AND
                     OO_Break_Point(i) = j.chargeslab) THEN
                    UPDATE TEMP_CHARGES
                       SET Selected_Flag = 'Y',

                           Margin_Type          = DECODE(OO_Margin_Discount_Flag(i),
                                                         'M',
                                                         OO_Margin_Type(i),
                                                         OO_Discount_Type(i)),
                           Marginvalue          = DECODE(OO_Margin_Discount_Flag(i),
                                                         'M',
                                                         OO_Margin(i),
                                                         OO_Discount(i)),
                           Margin_Discount_Flag = OO_Margin_Discount_Flag(i)
                     WHERE Chargeslab = j.Chargeslab
                       AND Sellchargeid = j.Sellchargeid
                       AND Charge_Id = j.Charge_Id
                       AND Chargedescid = j.Chargedescid;
                  END IF;
                END LOOP;
              END IF;
            ELSE
              IF DO_Margin_Discount_Flag.COUNT() > 0 THEN
                FOR i IN DO_charge_id.FIRST .. DO_charge_id.LAST LOOP
                  IF (DO_charge_id(i) = j.charge_id AND
                     DO_charge_description(i) = j.chargedescid AND
                     DO_buyrate_id(i) = j.sellchargeid AND
                     DO_Break_Point(i) = j.chargeslab) THEN
                    UPDATE TEMP_CHARGES
                       SET Selected_Flag = 'Y',

                           Margin_Type          = DECODE(DO_Margin_Discount_Flag(i),
                                                         'M',
                                                         DO_Margin_Type(i),
                                                         DO_Discount_Type(i)),
                           Marginvalue          = DECODE(DO_Margin_Discount_Flag(i),
                                                         'M',
                                                         DO_Margin(i),
                                                         DO_Discount(i)),
                           Margin_Discount_Flag = DO_Margin_Discount_Flag(i)
                     WHERE Chargeslab = j.Chargeslab
                       AND Sellchargeid = j.Sellchargeid
                       AND Charge_Id = j.Charge_Id
                       AND Chargedescid = j.Chargedescid;
                  END IF;
                END LOOP;
              END IF;
            END IF;
          END IF;
        ELSE
          IF UPPER(TRIM(v_opr_adm_flag)) = 'H' THEN
            IF UPPER(j.cost_incurredat) = 'ORIGIN' THEN
              FOR str IN C1_ORIGIN_HO LOOP
                IF (str.charge_id = j.charge_id AND
                   str.charge_description = j.chargedescid AND
                   str.buyrate_id = j.buy_charge_id AND
                   str.sellrate_id = j.sellchargeid AND
                   str.Break_Point = j.chargeslab) THEN
                  UPDATE TEMP_CHARGES
                     SET Selected_Flag = 'Y',

                         Margin_Type          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin_Type,
                                                       str.Discount_Type),
                         Marginvalue          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin,
                                                       str.Discount),
                         Margin_Discount_Flag = str.Margin_Discount_Flag
                   WHERE Chargeslab = j.Chargeslab
                     AND Buy_Charge_Id = j.Buy_Charge_Id
                     AND Sellchargeid = j.Sellchargeid
                     AND Charge_Id = j.Charge_Id
                     AND Chargedescid = j.Chargedescid;
                END IF;
              END LOOP;
            ELSE
              FOR str IN C1_DEST_HO LOOP
                IF (str.charge_id = j.charge_id AND
                   str.charge_description = j.chargedescid AND
                   str.buyrate_id = j.buy_charge_id AND
                   str.sellrate_id = j.sellchargeid AND
                   str.Break_Point = j.chargeslab AND
                   str.Break_Point = j.chargeslab) THEN
                  UPDATE TEMP_CHARGES
                     SET Selected_Flag = 'Y',

                         Margin_Type          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin_Type,
                                                       str.Discount_Type),
                         Marginvalue          = DECODE(str.Margin_Discount_Flag,
                                                       'M',
                                                       str.Margin,
                                                       str.Discount),
                         Margin_Discount_Flag = str.Margin_Discount_Flag
                   WHERE Chargeslab = j.Chargeslab
                     AND Buy_Charge_Id = j.Buy_Charge_Id
                     AND Sellchargeid = j.Sellchargeid
                     AND Charge_Id = j.Charge_Id
                     AND Chargedescid = j.Chargedescid;

                END IF;
              END LOOP;
            END IF;
          ELSE
            IF UPPER(j.cost_incurredat) = 'ORIGIN' THEN
              IF OO_Margin_Discount_Flag.COUNT() > 0 THEN
                FOR i IN OO_charge_id.FIRST .. OO_charge_id.LAST LOOP

                  IF (OO_charge_id(i) = j.charge_id AND
                     OO_charge_description(i) = j.chargedescid AND
                     OO_buyrate_id(i) = j.buy_charge_id AND
                     OO_sellrate_id(i) = j.sellchargeid AND
                     OO_Break_Point(i) = j.chargeslab) THEN
                    UPDATE TEMP_CHARGES
                       SET Selected_Flag = 'Y',

                           Margin_Type          = DECODE(OO_Margin_Discount_Flag(i),
                                                         'M',
                                                         OO_Margin_Type(i),
                                                         OO_Discount_Type(i)),
                           Marginvalue          = DECODE(OO_Margin_Discount_Flag(i),
                                                         'M',
                                                         OO_Margin(i),
                                                         OO_Discount(i)),
                           Margin_Discount_Flag = OO_Margin_Discount_Flag(i)
                     WHERE Chargeslab = j.Chargeslab
                       AND Buy_Charge_Id = j.Buy_Charge_Id
                       AND Sellchargeid = j.Sellchargeid
                       AND Charge_Id = j.Charge_Id
                       AND Chargedescid = j.Chargedescid;

                  END IF;
                END LOOP;
              END IF;
            ELSE
              IF DO_Margin_Discount_Flag.COUNT() > 0 THEN
                FOR i IN DO_charge_id.FIRST .. DO_charge_id.LAST LOOP
                  IF (DO_charge_id(i) = j.charge_id AND
                     DO_Charge_Description(i) = j.chargedescid AND
                     DO_buyrate_id(i) = j.buy_charge_id AND
                     DO_sellrate_id(i) = j.sellchargeid AND
                     DO_Break_Point(i) = j.chargeslab) THEN
                    UPDATE TEMP_CHARGES
                       SET Selected_Flag        = 'Y',
                           Margin_Type          = DECODE(DO_Margin_Discount_Flag(i),
                                                         'M',
                                                         DO_Margin_Type(i),
                                                         DO_Discount_Type(i)),
                           Marginvalue          = DECODE(DO_Margin_Discount_Flag(i),
                                                         'M',
                                                         DO_Margin(i),
                                                         DO_Discount(i)),
                           Margin_Discount_Flag = DO_Margin_Discount_Flag(i)
                     WHERE Chargeslab = j.Chargeslab
                       AND Buy_Charge_Id = j.Buy_Charge_Id
                       AND Sellchargeid = j.Sellchargeid
                       AND Charge_Id = j.Charge_Id
                       AND Chargedescid = j.Chargedescid;
                  END IF;
                END LOOP;
              END IF;
            END IF;
          END IF;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(p_operation) = 'MODIFY' THEN
      FOR i IN (SELECT charge_id,
                       chargedescid,
                       buy_charge_id,
                       sellchargeid,
                       sel_buy_flag,
                       chargeslab
                  FROM TEMP_CHARGES
                 WHERE selected_flag = 'Y') LOOP
        IF UPPER(i.sel_buy_flag) = 'S' THEN
          BEGIN
            --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
            SELECT DISTINCT NVL(Margin_Discount_Flag, 'M'),
                            Margin_Type,
                            Margin,
                            Discount_Type,
                            Discount
              INTO v_Margin_Discount_Flag,
                   v_Margin_Type,
                   v_Margin,
                   v_Discount_Type,
                   v_Discount
              FROM QMS_QUOTE_RATES
             WHERE Quote_Id IN (SELECT Id
                                  FROM QMS_QUOTE_MASTER a
                                 WHERE Quote_Id = p_Quote_Id
                                   AND a.Active_Flag = 'A')
               AND Break_Point = i.Chargeslab
               AND Buyrate_Id = TO_NUMBER(i.Buy_Charge_Id)
               AND Sellrate_Id = TO_NUMBER(i.Sellchargeid)
               AND Charge_Id = i.Charge_Id
               AND Charge_Description = i.Chargedescid;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_Margin_Discount_Flag := 'D';
              v_Margin_Type          := 'P';
              v_Discount_Type        := 'P';
              v_Margin               := v_Margin;
              v_Discount             := v_Discount;
          END;
          UPDATE TEMP_CHARGES
             SET Margin_Type          = DECODE(v_Margin_Discount_Flag,
                                               'M',
                                               v_Margin_Type,
                                               v_Discount_Type),
                 Marginvalue          = DECODE(v_Margin_Discount_Flag,
                                               'M',
                                               v_Margin,
                                               v_Discount),
                 Margin_Discount_Flag = v_Margin_Discount_Flag
           WHERE Chargeslab = i.Chargeslab
             AND Buy_Charge_Id = i.Buy_Charge_Id
             AND Sellchargeid = i.Sellchargeid
             AND Charge_Id = i.Charge_Id
             AND Chargedescid = i.Chargedescid;

        ELSIF UPPER(i.sel_buy_flag) = 'B' THEN
          BEGIN
            SELECT DISTINCT NVL(Margin_Discount_Flag, 'M'),
                            Margin_Type,
                            Margin,
                            Discount_Type,
                            Discount
              INTO v_Margin_Discount_Flag,
                   v_Margin_Type,
                   v_Margin,
                   v_Discount_Type,
                   v_Discount
              FROM QMS_QUOTE_RATES
             WHERE Quote_Id IN
                   (SELECT Id
                      FROM QMS_QUOTE_MASTER a
                     WHERE Quote_Id = p_Quote_Id
                       AND Version_No =
                           (SELECT MAX(Version_No)
                              FROM QMS_QUOTE_MASTER b
                             WHERE a.Quote_Id = b.Quote_Id))
               AND Break_Point = i.Chargeslab
               AND Buyrate_Id = TO_NUMBER(i.Sellchargeid)
               AND Charge_Id = i.Charge_Id
               AND Charge_Description = i.Chargedescid;
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              v_Margin_Discount_Flag := 'M';
              v_Margin_Type          := 'P';
              v_Discount_Type        := 'P';
              v_Margin               := v_Margin;
              v_Discount             := v_Discount;
          END;
          UPDATE TEMP_CHARGES
             SET Margin_Type          = DECODE(v_Margin_Discount_Flag,
                                               'M',
                                               v_Margin_Type,
                                               v_Discount_Type),
                 Marginvalue          = DECODE(v_Margin_Discount_Flag,
                                               'M',
                                               v_Margin,
                                               v_Discount),
                 Margin_Discount_Flag = v_Margin_Discount_Flag
           WHERE Chargeslab = i.Chargeslab
             AND Sellchargeid = i.Sellchargeid
             AND Charge_Id = i.Charge_Id
             AND Chargedescid = i.Chargedescid;

        END IF;
      END LOOP;
      COMMIT;
    END IF;

    --------------------------To Order the Charges----------------------
    v_charge_groups := p_chargegrps || '~';

    BEGIN
      LOOP
        v_Pos := INSTR(v_charge_groups, '~', 1, v_Occ_Num);
        EXIT WHEN v_Pos = 0;
        v_Char := SUBSTR(v_charge_groups, v_Start, v_Pos - v_Pos1);
        FOR i IN C3(v_char) LOOP
          EXECUTE IMMEDIATE ('UPDATE TEMP_CHARGES SET LANE_NO =:v_laneNo
                             WHERE CHARGE_ID=:v_charge_id AND CHARGEDESCID =: v_charge_desc
                             AND TERMINALID=:v_terminal_id')
            USING v_lane_no, i.Charge_Id, i.Chargedescid, i.Terminalid;
        END LOOP;
        v_Occ_Num := v_Occ_Num + 1;
        v_Start   := v_Pos + 1;
        v_Pos1    := v_Pos + 1;
        v_Lane_No := v_Lane_No + 1;
      END LOOP;
    END;
    ----------------------------------------------------------------------
    COMMIT;
    IF UPPER(p_permission) = 'Y' THEN
      OPEN p_rs FOR
        SELECT *
          FROM TEMP_CHARGES
         ORDER BY Lane_No, Cost_Incurredat, Id, Line_No;
    ELSE
      OPEN p_rs FOR
        SELECT *
          FROM TEMP_CHARGES
         WHERE Sel_Buy_Flag NOT IN ('B')
         ORDER BY Lane_No, Cost_Incurredat, Id, Line_No;
    END IF;
  EXCEPTION
    WHEN OTHERS THEN
      Dbms_Output.put_line(SQLERRM);
  END;

  /*
  This procedure returns the details of the selected freight rate (selected in 2nd screen) for a single leg.
  These details are displayed in Quote 3rd Screen.
  It Returns a resultset object.
  The IN Parameters are:
     p_orgloc                  VARCHAR2,---Leg Origin
     p_destloc                 VARCHAR2,---Leg Destination
     p_sell_buy_rateid         VARCHAR2,
     p_buy_rate_id             VARCHAR2,
     p_laneno                  VARCHAR2,
     p_salespersoncode         VARCHAR2,
     p_terminalid              VARCHAR2,
     p_permission              VARCHAR2 DEFAULT 'Y',--BR View Permission Flag
     p_sellrate_flag           VARCHAR2 DEFAULT 'Y',
     p_margin                  NUMBER,
     p_operation               VARCHAR2,
     p_quote_id                VARCHAR2,
     p_customerid              VARCHAR2,
     p_quote_org               VARCHAR2,---Initial Origin
     p_quote_dest              VARCHAR2,---Final Destination
     p_carrier                 VARCHAR2,
     p_servicelevel            VARCHAR2,
     p_frequency               VARCHAR2,
     p_freightRateTerm         VARCHAR2,
     p_shmode                  VARCHAR2
  The OUT Parameter is:
   p_rs           OUT   resultset
  */

  PROCEDURE QUOTE_SELL_BUY_RATESDTL_PROC(P_ORGLOC          VARCHAR2,
                                         P_DESTLOC         VARCHAR2,
                                         P_SELL_BUY_RATEID VARCHAR2,
                                         P_BUY_RATE_ID     VARCHAR2,
                                         P_LANENO          VARCHAR2,
                                         P_SALESPERSONCODE VARCHAR2,
                                         P_TERMINALID      VARCHAR2,
                                         P_PERMISSION      VARCHAR2 DEFAULT 'Y',
                                         P_SELLRATE_FLAG   VARCHAR2 DEFAULT 'Y',
                                         P_MARGIN          NUMBER,
                                         P_OPERATION       VARCHAR2,
                                         P_QUOTE_ID        VARCHAR2,
                                         P_CUSTOMERID      VARCHAR2,
                                         P_QUOTE_ORG       VARCHAR2,
                                         P_QUOTE_DEST      VARCHAR2,
                                         P_CARRIER         VARCHAR2,
                                         P_SERVICELEVEL    VARCHAR2,
                                         P_FREQUENCY       VARCHAR2,
                                         P_FREIGHTRATETERM VARCHAR2,
                                         P_SHMODE          VARCHAR2,
                                         P_RS              OUT RESULTSET) AS
    V_MARGIN               NUMBER(8, 2) := 0;
    V_MARGINID             VARCHAR2(10);
    V_MARGIN_DISCOUNT_FLAG VARCHAR2(10);
    V_MARGIN_TYPE          VARCHAR2(10);
    V_DISCOUNT_TYPE        VARCHAR2(10);
    V_DISCOUNT             NUMBER(8, 2);
    V_CURR                 SYS_REFCURSOR;
    V_COUNT                NUMBER(8, 2) := 0;
    V_WEIGHTBREAK          VARCHAR2(10);
    V_MARGINVALUE          VARCHAR2(10);
    V_RATEDESC             VARCHAR2(200);
    V_OPR_ADM_FLAG         VARCHAR2(10);
    V_FREQUENCY            VARCHAR2(10);
    V_CARRIER              VARCHAR2(10);
    V_TRANSITTIME          VARCHAR2(10);
    V_RATEVALIDITY         VARCHAR2(10);
    V_FREQUENCYCHECKED     VARCHAR2(10);
    V_CARRIERCHECKED       VARCHAR2(10);
    V_TRANSITCHECKED       VARCHAR2(10);
    V_VALIDITYCHECKED      VARCHAR2(10);
    V_TERMINALS            VARCHAR2(2000);
  BEGIN
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    V_MARGIN := P_MARGIN;
    /* If p_Sellrate_Flag <> 'SBR' Then
      Tied_Custinfo_Freight_Proc(p_Customerid,
                                 p_Orgloc,
                                 p_Destloc,
                                 p_Carrier,
                                 p_Servicelevel,
                                 p_Frequency,
                                 p_Freightrateterm,
                                 p_Terminalid,
                                 p_Shmode,
                                 p_Sellrate_Flag,
                                 p_Quote_Org,
                                 p_Quote_Dest,
                                 p_Operation,
                                 p_Quote_Id);
    End If;*/
    --sell or buyrates
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    /*If v_Count = 0 Then*/
    IF P_SELLRATE_FLAG = 'Y' THEN
      --BEGIN
      --Sell Rates
      INSERT INTO TEMP_CHARGES
        (SELLCHARGEID,
         VERSION_NO,
         --@@Added for the WPBN issues-146448,146968 on 19/12/08
         CHARGEDESCID,
         COST_INCURREDAT,
         CHARGESLAB,
         CURRENCY,
         BUYRATE,
         SELLRATE,
         MARGIN_TYPE,
         MARGINVALUE,
         SEL_BUY_FLAG,
         BUY_CHARGE_ID,
         EFROM,
         VALIDUPTO,
         TERMINALID,
         DENSITY_RATIO,
         LANE_NO,
         LINE_NO,
         WEIGHT_BREAK,
         RATE_TYPE,
         RATE_INDICATOR,
         SRV_LEVEL,
         FREQUENCY,
         --@@Added by Kameswari for the WPBN issue-146448
         TRANSITTIME,
         CARRIER,
         FREQUENCY_CHECKED,
         CARRIER_CHECKED,
         TRANSITTIME_CHECKED,
         RATEVALIDITY_CHECKED,
         RATE_DESCRIPTION,
         CONSOLE_TYPE,
         SHIPMENTMODE,--@@Added by Kameswari for Surcharge Enhancements
         CHARGEBASIS)
        SELECT TO_CHAR(SD.REC_CON_ID) SELLCHARGEID,
               SD.VERSION_NO,
               --@@Added for the WPBN issues-146448,146968 on 19/12/08
               'Freight Rate' CHARGEDESCID,
               'Carrier' COST_INCURREDAT,
               SD.WEIGHTBREAKSLAB CHARGESLAB,
               SM.CURRENCY CURRENCY,
               TO_NUMBER(SD.BUY_RATE_AMT) BUYRATE,
               TO_NUMBER(SD.CHARGERATE) SELLRATE,
               DECODE(SM.RC_FLAG, 'R', SM.MARGIN_TYPE, 'P') MARGIN_TYPE,
               --To_Number(Sd.Margin_Perc) Marginvalue,
               --v_Margin Marginvalue,
               0.0 MARGINVALUE,
               --@@Modified by kameswari for the WPBN issue-
               /*DECODE(UPPER(WEIGHTBREAKSLAB),
                      'MIN',
                      'Per Shipment',
                      (SELECT DISTINCT 'Per ' || BM.UOM
                         FROM QMS_BUYRATES_MASTER BM, QMS_BUYRATES_DTL BD
                        WHERE BD.BUYRATEID = SD.BUYRATEID
                          AND BD.LANE_NO = SD.LANE_NO
                          AND BD.VERSION_NO = SD.VERSION_NO
                          AND BD.BUYRATEID = BM.BUYRATEID)) CHARGEBASIS,*/
               DECODE(SM.RC_FLAG, 'R', 'RSR', 'CSR') SEL_BUY_FLAG,
               TO_CHAR(SD.BUYRATEID) BUY_CHARGE_ID,
               (SELECT DISTINCT EFFECTIVE_FROM
                  FROM QMS_BUYRATES_DTL QBD
                 WHERE QBD.BUYRATEID = SD.BUYRATEID
                   AND QBD.LANE_NO = SD.LANE_NO
                   AND QBD.VERSION_NO = SD.VERSION_NO),
               (SELECT DISTINCT VALID_UPTO
                  FROM QMS_BUYRATES_DTL QBD
                 WHERE QBD.BUYRATEID = SD.BUYRATEID
                   AND QBD.LANE_NO = SD.LANE_NO
                   AND QBD.VERSION_NO = SD.VERSION_NO),
               SM.TERMINALID,
               (SELECT DISTINCT DENSITY_CODE
                  FROM QMS_BUYRATES_DTL QBD
                 WHERE QBD.BUYRATEID = SD.BUYRATEID
                   AND QBD.LANE_NO = SD.LANE_NO
                   AND QBD.VERSION_NO = SD.VERSION_NO),
               SD.LANE_NO,
               SD.LINE_NO,
               SM.WEIGHT_BREAK,
               SM.RATE_TYPE,
               SD.CHARGERATE_INDICATOR,
               SD.SERVICELEVEL_ID,
               SD.FREQUENCY,
               --@@Added by Kameswari for the WPBN issue-146448
               SD.TRANSIT_TIME,
               (SELECT CARRIERNAME
                  FROM FS_FR_CAMASTER
                 WHERE CARRIERID = SD.CARRIER_ID),
               --Sd.carrier_id,
               (SELECT DISTINCT FREQUENCY
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND FREQUENCY = 'on'),
               (SELECT DISTINCT CARRIER
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND CARRIER = 'on'),
               (SELECT DISTINCT TRANSITTIME
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND TRANSITTIME = 'on'),
               (SELECT DISTINCT RATEVALIDITY
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND RATEVALIDITY = 'on'),
               DECODE(SD.RATE_DESCRIPTION,
                      '',
                      'A FREIGHT RATE',
                      SD.RATE_DESCRIPTION) RATE_DESCRIPTION,
               --@@Added by Kameswari for Surcharge Enhancements
               (SELECT DISTINCT CONSOLE_TYPE
                  FROM QMS_BUYRATES_MASTER QBM
                 WHERE QBM.BUYRATEID = SD.BUYRATEID
                   AND QBM.VERSION_NO = SD.VERSION_NO
                   AND (QBM.LANE_NO = SD.LANE_NO OR QBM.LANE_NO IS NULL))CONSOLE_TYPE, --@@Added by Kameswari for the WPBN issue-112348
                  SM.SHIPMENT_MODE SHIPMENTMODE,
                     DECODE(UPPER(WEIGHTBREAKSLAB),
                      'MIN',
                      'Per Shipment',
                      'BASIC',
                      'Per Shipment',
                      (SELECT DISTINCT 'Per ' || BM.UOM
                         FROM QMS_BUYRATES_MASTER BM, QMS_BUYRATES_DTL BD
                        WHERE BD.BUYRATEID = SD.BUYRATEID
                          AND BD.LANE_NO = SD.LANE_NO
                          AND BD.VERSION_NO = SD.VERSION_NO
                          AND BD.BUYRATEID = BM.BUYRATEID))
                         /* (MULTI_QUOTE_BASIS(SM.SHIPMENT_MODE,CONSOLE_TYPE,SM.WEIGHT_BREAK))*/
                          CHARGEBASIS

          FROM QMS_REC_CON_SELLRATESDTL SD, QMS_REC_CON_SELLRATESMASTER SM
         WHERE SD.REC_CON_ID = P_SELL_BUY_RATEID
           AND SD.BUYRATEID = P_BUY_RATE_ID
           AND SD.LANE_NO = P_LANENO
           AND SD.VERSION_NO IN (SELECT VERSION_NO
                                   FROM QMS_REC_CON_SELLRATESDTL
                                  WHERE BUYRATEID = SD.BUYRATEID
                                    AND LANE_NO = SD.LANE_NO
                                    AND AI_FLAG = 'A')
           AND SD.REC_CON_ID = SM.REC_CON_ID
           AND SD.CHARGERATE NOT IN
               (SELECT SD.CHARGERATE
                  FROM QMS_REC_CON_SELLRATESDTL SD
                 WHERE SD.REC_CON_ID = P_SELL_BUY_RATEID
                   AND SD.BUYRATEID = P_BUY_RATE_ID
                   AND SD.LANE_NO = P_LANENO
                   AND SD.REC_CON_ID = SM.REC_CON_ID
                   AND SD.VERSION_NO IN
                       (SELECT VERSION_NO
                          FROM QMS_REC_CON_SELLRATESDTL
                         WHERE BUYRATEID = SD.BUYRATEID
                           AND LANE_NO = SD.LANE_NO
                           AND AI_FLAG = 'A')
                  -- AND SD.RATE_DESCRIPTION NOT IN ('A FREIGHT RATE')
                   AND SD.RATE_DESCRIPTION IS NOT NULL
                   AND SD.CHARGERATE IN ('0.0','0','0.00000','0.00'))
           AND (SD.REC_CON_ID, SD.BUYRATEID, SD.LANE_NO, SD.VERSION_NO) NOT IN
               (SELECT SELLCHARGEID, BUY_CHARGE_ID, LANE_NO, VERSION_NO
                  FROM TEMP_CHARGES)
         ORDER BY SD.REC_CON_ID, SD.LANE_NO, SD.WEIGHTBREAKSLAB;
    ELSIF P_SELLRATE_FLAG = 'N' THEN
      INSERT INTO TEMP_CHARGES
        (SELLCHARGEID,
         VERSION_NO,
         --@@Added for the WPBN issues-146448,146968 on 19/12/08
         CHARGEDESCID,
         COST_INCURREDAT,
         CHARGESLAB,
         CURRENCY,
         BUYRATE,
         SELLRATE,
         MARGIN_TYPE,
         MARGINVALUE,
         SEL_BUY_FLAG,
         BUY_CHARGE_ID,
         EFROM,
         VALIDUPTO,
         TERMINALID,
         DENSITY_RATIO,
         LANE_NO,
         LINE_NO,
         WEIGHT_BREAK,
         RATE_TYPE,
         RATE_INDICATOR,
         SRV_LEVEL,
         FREQUENCY,
         TRANSITTIME,
         --@@Added by Kameswari for the WPBN issue-146448
         CARRIER,
         FREQUENCY_CHECKED,
         CARRIER_CHECKED,
         TRANSITTIME_CHECKED,
         RATEVALIDITY_CHECKED,
         RATE_DESCRIPTION,
         CONSOLE_TYPE,
         SHIPMENTMODE,
         CHARGEBASIS
         )
        SELECT TO_CHAR(BD.BUYRATEID) SELLCHARGEID,
               BD.VERSION_NO,
               --@@Added for the WPBN issues-146448,146968 on 19/12/08
               'Freight Rate' CHARGEDESCID,
               'Carrier' COST_INCURREDAT,
               BD.WEIGHT_BREAK_SLAB CHARGESLAB,
               --BM.CURRENCY CURRENCY,
               NVL(BD.SUR_CHARGE_CURRENCY,BM.CURRENCY)CURRENCY,
               BD.CHARGERATE BUYRATE,
               TO_NUMBER('0') SELLRATE,
               'P' MARGIN_TYPE,
               V_MARGIN MARGINVALUE,
              /* DECODE(UPPER(WEIGHT_BREAK_SLAB),
                      'MIN',
                      'Per Shipment',
                      'Per ' || BM.UOM) CHARGEBASIS,*/
               'BR' SEL_BUY_FLAG,
               '' BUY_CHARGE_ID,
               BD.EFFECTIVE_FROM,
               BD.VALID_UPTO,
               BM.TERMINALID,
               BD.DENSITY_CODE,
               BD.LANE_NO,
               BD.LINE_NO,
               BM.WEIGHT_BREAK,
               BM.RATE_TYPE,
               BD.CHARGERATE_INDICATOR,
               BD.SERVICE_LEVEL,
               --@@Added by Kameswari for the WPBN issue-146448
               BD.FREQUENCY,
               BD.TRANSIT_TIME,
               --Bd.carrier_id,
               (SELECT CARRIERNAME
                  FROM FS_FR_CAMASTER
                 WHERE CARRIERID = BD.CARRIER_ID),
               (SELECT DISTINCT FREQUENCY
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND FREQUENCY = 'on'),
               (SELECT DISTINCT CARRIER
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND CARRIER = 'on'),
               (SELECT DISTINCT TRANSITTIME
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND TRANSITTIME = 'on'),
               (SELECT DISTINCT RATEVALIDITY
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                         WHERE CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT P_TERMINALID FROM DUAL)
                   AND RATEVALIDITY = 'on'),
               DECODE(BD.RATE_DESCRIPTION, --@@Added by Kameswari for Surcharge Enhancements
                      '',
                      'A FREIGHT RATE',
                      BD.RATE_DESCRIPTION),
               BM.CONSOLE_TYPE ,
               BM.SHIPMENT_MODE,-- --@@Added by Kameswari for the WPBN issue-112348
                DECODE(UPPER(WEIGHT_BREAK_SLAB),
                      'MIN',
                      'Per Shipment',
                      'BASIC',
                      'Per Shipment',
                      'Per ' || BM.UOM) CHARGEBASIS
              /*(MULTI_QUOTE_BASIS( BM.SHIPMENT_MODE,BM.CONSOLE_TYPE, BM.WEIGHT_BREAK)) CHARGEBASIS*/

          FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
         WHERE BD.BUYRATEID = P_SELL_BUY_RATEID
           AND BD.BUYRATEID = BM.BUYRATEID
           AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL) --@@Added by Kameswari on 04/02/09
           AND BD.LANE_NO = P_LANENO
           And (Bd.Chargerate not in ('0.0') or Bd.Chargerate not in ('0') OR Bd.Chargerate not in ('0.00'))
           AND BM.VERSION_NO = BD.VERSION_NO
           AND BD.VERSION_NO IN
               (SELECT VERSION_NO
                  FROM QMS_BUYRATES_DTL
                 WHERE BUYRATEID = BD.BUYRATEID
                   AND LANE_NO = BD.LANE_NO
                   AND ACTIVEINACTIVE IS NULL)
           AND BD.CHARGERATE NOT IN
               (SELECT BD.CHARGERATE
                  FROM QMS_BUYRATES_DTL BD
                 WHERE BD.BUYRATEID = P_SELL_BUY_RATEID
                   AND BD.BUYRATEID = BM.BUYRATEID
                   AND BD.LANE_NO = P_LANENO
                   AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL) --@@Added by Kameswari on 04/02/09
                   AND BD.VERSION_NO IN
                       (SELECT VERSION_NO
                          FROM QMS_BUYRATES_DTL
                         WHERE BUYRATEID = BD.BUYRATEID
                           AND LANE_NO = BD.LANE_NO
                           AND ACTIVEINACTIVE IS NULL)
                   AND BD.RATE_DESCRIPTION NOT IN ('A FREIGHT RATE')
                   AND BD.RATE_DESCRIPTION IS NOT NULL
                   AND BD.CHARGERATE IN ('0.0', '0'))
           AND (BD.BUYRATEID, BD.LANE_NO, BD.VERSION_NO) NOT IN
               (SELECT SELLCHARGEID, LANE_NO, VERSION_NO FROM TEMP_CHARGES)
         ORDER BY BD.BUYRATEID, BD.LANE_NO, BD.WEIGHT_BREAK_SLAB;
    ELSE
      IF UPPER(P_OPERATION) = 'MODIFY' THEN
        INSERT INTO TEMP_CHARGES
          (SELLCHARGEID,
           CHARGEDESCID,
           COST_INCURREDAT,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           CHARGEBASIS,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           DENSITY_RATIO,
           LANE_NO,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_DESCRIPTION) --@@Added by kameswari for Surcharge Enhancements
          SELECT 'X' SELLCHARGEID,
                 'Freight Rate' CHARGEDESCID,
                 'Carrier' COST_INCURREDAT,
                 QS.WEIGHT_BREAK_SLAB CHARGESLAB,
                 QS.CHARGE_RATE BUYRATE,
                 TO_NUMBER('0') SELLRATE,
                 'P' MARGIN_TYPE,
                 V_MARGIN MARGINVALUE,
                 DECODE(UPPER(WEIGHT_BREAK_SLAB),
                        'MIN',
                        'Per Shipment',
                        'Per ' || QS.UOM) CHARGEBASIS,
                 'SBR' SEL_BUY_FLAG,
                 '' BUY_CHARGE_ID,
                 QM.TERMINAL_ID,
                 QS.DENSITY_CODE,
                 QS.LANE_NO,
                 QS.LINE_NO,
                 QS.WEIGHT_BREAK,
                 DECODE(QS.RATE_DESCRIPTION,
                        '',
                        'A FREIGHT RATE',
                        QS.RATE_DESCRIPTION) RATE_DESCRIPTION --@@Added by kameswari for Surcharge Enhancements
            FROM QMS_QUOTE_SPOTRATES QS, QMS_QUOTE_MASTER QM
           WHERE QS.QUOTE_ID = QM.ID
             AND QM.QUOTE_ID = P_QUOTE_ID
                /*And (Qs.Charge_Rate not in ('0.0') or
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           Qs.Charge_Rate not in ('0'))*/
             AND QS.CHARGE_RATE NOT IN
                 (SELECT Q.CHARGE_RATE
                    FROM QMS_QUOTE_SPOTRATES Q
                   WHERE Q.QUOTE_ID = QM.ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND Q.RATE_DESCRIPTION NOT IN ('A FREIGHT RATE', NULL)
                     AND Q.CHARGE_RATE IN ('0.0', '0'))
             AND QM.ACTIVE_FLAG = 'A'
             AND QS.LANE_NO = P_LANENO
           ORDER BY QS.LANE_NO, QS.LINE_NO, QS.WEIGHT_BREAK_SLAB;
      END IF;
    END IF;
    COMMIT;
    SELECT COUNT(*) INTO V_COUNT FROM TEMP_CHARGES;
    --@@Added  by Kameswari for the WPBN issue-143250
    IF UPPER(P_OPERATION) = 'MODIFY' THEN
      FOR J IN (SELECT BUY_CHARGE_ID,
                       SELLCHARGEID,
                       LANE_NO,
                       VERSION_NO,
                       --@@Added for the WPBN issues-146448,146968 on 19/12/08
                       SEL_BUY_FLAG
                  FROM TEMP_CHARGES
                 ORDER BY BUY_CHARGE_ID) LOOP
        IF (J.SEL_BUY_FLAG = 'BR') THEN
          UPDATE TEMP_CHARGES
             SET SELECTED_FLAG = 'Y'
          --Where ( Sellchargeid,lane_no) In
           WHERE (SELLCHARGEID, LANE_NO, VERSION_NO) IN --@@Modified for the WPBN issues-146448,146968 on 19/12/08
                --(Select  Buyrate_Id,rate_lane_no
                 (SELECT BUYRATE_ID, RATE_LANE_NO, VERSION_NO
                    FROM QMS_QUOTE_RATES
                   WHERE QUOTE_ID IN
                         (SELECT ID
                            FROM QMS_QUOTE_MASTER A
                           WHERE QUOTE_ID = P_QUOTE_ID
                             AND VERSION_NO =
                                 (SELECT MAX(VERSION_NO)
                                    FROM QMS_QUOTE_MASTER B
                                   WHERE A.QUOTE_ID = B.QUOTE_ID)))
             AND SELLCHARGEID = J.SELLCHARGEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08
          --COMMIT;
        ELSE
          UPDATE TEMP_CHARGES
             SET SELECTED_FLAG = 'Y'
          --Where ( Buy_Charge_Id, Sellchargeid,lane_no) In
           WHERE (BUY_CHARGE_ID, SELLCHARGEID, LANE_NO, VERSION_NO) IN --@@Modified for the WPBN issues-146448,146968 on 19/12/08
                 (SELECT BUYRATE_ID, SELLRATE_ID, RATE_LANE_NO, VERSION_NO
                    FROM QMS_QUOTE_RATES
                   WHERE QUOTE_ID IN
                         (SELECT ID
                            FROM QMS_QUOTE_MASTER A
                           WHERE QUOTE_ID = P_QUOTE_ID
                             AND VERSION_NO =
                                 (SELECT MAX(VERSION_NO)
                                    FROM QMS_QUOTE_MASTER B
                                   WHERE A.QUOTE_ID = B.QUOTE_ID)))
             AND BUY_CHARGE_ID = J.BUY_CHARGE_ID
             AND SELLCHARGEID = J.SELLCHARGEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(P_OPERATION) = 'ADD' OR UPPER(P_OPERATION) = 'COPY' THEN
      FOR J IN (SELECT BUY_CHARGE_ID,
                       SELLCHARGEID,
                       LANE_NO,
                       SEL_BUY_FLAG,
                       VERSION_NO,
                       CHARGESLAB --@@Added for the WPBN issues-146448,146968 on 19/12/08
                  FROM TEMP_CHARGES
                 ORDER BY BUY_CHARGE_ID) LOOP
        IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
          IF J.SEL_BUY_FLAG = 'BR' THEN

            UPDATE TEMP_CHARGES
               SET SELECTED_FLAG = 'Y'
            --Where (  Sellchargeid,lane_no) In
             WHERE (SELLCHARGEID, LANE_NO, VERSION_NO) IN
                   (SELECT BUYRATE_ID, RATE_LANE_NO, VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
                      FROM QMS_QUOTE_RATES QR
                     WHERE QUOTE_ID =
                           (SELECT MAX(QM.ID)
                              FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                             WHERE QM.ID = IQR.QUOTE_ID
                               AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                               AND QM.DEST_LOCATION = P_QUOTE_DEST
                               AND QM.CUSTOMER_ID = P_CUSTOMERID
                               AND QM.SHIPMENT_MODE = P_SHMODE
                               AND QM.IE_FLAG = 'E'
                               AND ((QM.ACTIVE_FLAG = 'I' AND
                                   QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                                   (QM.ACTIVE_FLAG = 'A'))
                               AND IQR.SELL_BUY_FLAG = 'BR'
                               AND QM.TERMINAL_ID IN
                                   (SELECT TERMINALID
                                      FROM FS_FR_TERMINALMASTER)
                               AND IQR.BUYRATE_ID = J.SELLCHARGEID
                               AND IQR.RATE_LANE_NO = J.LANE_NO
                               AND IQR.VERSION_NO = J.VERSION_NO)
                       AND QR.SELL_BUY_FLAG = 'BR') --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND SELLCHARGEID = J.SELLCHARGEID
               AND LANE_NO = J.LANE_NO
               AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08






          ELSE
            UPDATE TEMP_CHARGES
               SET SELECTED_FLAG = 'Y'
            --Where ( buy_charge_id,Sellchargeid,lane_no) In
             WHERE (BUY_CHARGE_ID, SELLCHARGEID, LANE_NO, VERSION_NO) IN --@@Modified for the WPBN issues-146448,146968 on 19/12/08
                   (SELECT BUYRATE_ID, SELLRATE_ID, RATE_LANE_NO, VERSION_NO
                      FROM QMS_QUOTE_RATES QR
                     WHERE QUOTE_ID =
                           (SELECT MAX(QM.ID)
                              FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                             WHERE QM.ID = IQR.QUOTE_ID
                               AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                               AND QM.DEST_LOCATION = P_QUOTE_DEST
                               AND QM.CUSTOMER_ID = P_CUSTOMERID
                               AND QM.SHIPMENT_MODE = P_SHMODE
                               AND QM.IE_FLAG = 'E'
                               AND ((QM.ACTIVE_FLAG = 'I' AND
                                   QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                                   (QM.ACTIVE_FLAG = 'A'))
                               AND IQR.SELL_BUY_FLAG = 'RSR'
                               AND QM.TERMINAL_ID IN
                                   (SELECT TERMINALID
                                      FROM FS_FR_TERMINALMASTER)
                               AND IQR.SELLRATE_ID = J.SELLCHARGEID
                               AND IQR.BUYRATE_ID = J.BUY_CHARGE_ID
                               AND IQR.RATE_LANE_NO = J.LANE_NO
                               AND IQR.VERSION_NO = J.VERSION_NO)
                       AND QR.SELL_BUY_FLAG = 'RSR') --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND BUY_CHARGE_ID = J.BUY_CHARGE_ID
               AND SELLCHARGEID = J.SELLCHARGEID
               AND LANE_NO = J.LANE_NO
               AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08



          END IF;
        ELSE
          IF J.SEL_BUY_FLAG = 'BR' THEN
            UPDATE TEMP_CHARGES
               SET SELECTED_FLAG = 'Y'
            --Where (  Sellchargeid,lane_no) In
             WHERE (SELLCHARGEID, LANE_NO, VERSION_NO) IN --@@Modified for the WPBN issues-146448,146968 on 19/12/08
                   (SELECT BUYRATE_ID, RATE_LANE_NO, VERSION_NO
                      FROM QMS_QUOTE_RATES QR
                     WHERE QUOTE_ID =
                           (SELECT MAX(QM.ID)
                              FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                             WHERE QM.ID = IQR.QUOTE_ID
                               AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                               AND QM.DEST_LOCATION = P_QUOTE_DEST
                               AND QM.CUSTOMER_ID = P_CUSTOMERID
                               AND QM.SHIPMENT_MODE = P_SHMODE
                               AND QM.IE_FLAG = 'E'
                               AND ((QM.ACTIVE_FLAG = 'I' AND
                                   QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                                   (QM.ACTIVE_FLAG = 'A'))
                               AND IQR.SELL_BUY_FLAG = 'BR'
                               AND QM.TERMINAL_ID IN
                                   (SELECT PARENT_TERMINAL_ID TERM_ID
                                      FROM FS_FR_TERMINAL_REGN
                                    CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                               PARENT_TERMINAL_ID
                                     START WITH CHILD_TERMINAL_ID =
                                                P_TERMINALID
                                    UNION
                                    SELECT TERMINALID TERM_ID
                                      FROM FS_FR_TERMINALMASTER
                                     WHERE OPER_ADMIN_FLAG = 'H'
                                    UNION
                                    SELECT P_TERMINALID TERM_ID
                                      FROM DUAL
                                    UNION
                                    SELECT CHILD_TERMINAL_ID TERM_ID
                                      FROM FS_FR_TERMINAL_REGN
                                    CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                                PARENT_TERMINAL_ID
                                     START WITH PARENT_TERMINAL_ID =
                                                P_TERMINALID)
                               AND IQR.BUYRATE_ID = J.SELLCHARGEID
                               AND IQR.RATE_LANE_NO = J.LANE_NO
                               AND IQR.VERSION_NO = J.VERSION_NO)
                       AND QR.SELL_BUY_FLAG = 'BR') --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND SELLCHARGEID = J.SELLCHARGEID
               AND LANE_NO = J.LANE_NO
               AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08




          ELSE
            UPDATE TEMP_CHARGES
               SET SELECTED_FLAG = 'Y'
            --Where ( buy_charge_id,Sellchargeid,lane_no) In
             WHERE (BUY_CHARGE_ID, SELLCHARGEID, LANE_NO, VERSION_NO) IN --@@Modified for the WPBN issues-146448,146968 on 19/12/08
                   (SELECT BUYRATE_ID, SELLRATE_ID, RATE_LANE_NO, VERSION_NO
                      FROM QMS_QUOTE_RATES QR
                     WHERE QUOTE_ID =
                           (SELECT MAX(QM.ID)
                              FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                             WHERE QM.ID = IQR.QUOTE_ID
                               AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                               AND QM.DEST_LOCATION = P_QUOTE_DEST
                               AND QM.CUSTOMER_ID = P_CUSTOMERID
                               AND QM.SHIPMENT_MODE = P_SHMODE
                               AND QM.IE_FLAG = 'E'
                               AND ((QM.ACTIVE_FLAG = 'I' AND
                                   QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                                   (QM.ACTIVE_FLAG = 'A'))
                               AND IQR.SELL_BUY_FLAG = 'RSR'
                               AND QM.TERMINAL_ID IN
                                   (SELECT PARENT_TERMINAL_ID TERM_ID
                                      FROM FS_FR_TERMINAL_REGN
                                    CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                               PARENT_TERMINAL_ID
                                     START WITH CHILD_TERMINAL_ID =
                                                P_TERMINALID
                                    UNION
                                    SELECT TERMINALID TERM_ID
                                      FROM FS_FR_TERMINALMASTER
                                     WHERE OPER_ADMIN_FLAG = 'H'
                                    UNION
                                    SELECT P_TERMINALID TERM_ID
                                      FROM DUAL
                                    UNION
                                    SELECT CHILD_TERMINAL_ID TERM_ID
                                      FROM FS_FR_TERMINAL_REGN
                                    CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                                PARENT_TERMINAL_ID
                                     START WITH PARENT_TERMINAL_ID =
                                                P_TERMINALID)
                               AND IQR.SELLRATE_ID = J.SELLCHARGEID
                               AND IQR.BUYRATE_ID = J.BUY_CHARGE_ID
                               AND IQR.RATE_LANE_NO = J.LANE_NO
                               AND IQR.VERSION_NO = J.VERSION_NO)
                       AND QR.SELL_BUY_FLAG = 'RSR') --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND BUY_CHARGE_ID = J.BUY_CHARGE_ID
               AND SELLCHARGEID = J.SELLCHARGEID
               AND LANE_NO = J.LANE_NO
               AND VERSION_NO = J.VERSION_NO; --@@Added for the WPBN issues-146448,146968 on 19/12/08

                 UPDATE TEMP_CHARGES
               SET CHECKED_FLAG = 'Y'
            --Where (  Sellchargeid,lane_no) In
             WHERE (SELLCHARGEID, LANE_NO, VERSION_NO) IN
                   (SELECT BUYRATE_ID, RATE_LANE_NO, VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
                      FROM QMS_QUOTE_RATES QR
                     WHERE QUOTE_ID =
                           (SELECT MAX(QM.ID)
                              FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                             WHERE QM.ID = IQR.QUOTE_ID
                               AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                               AND QM.DEST_LOCATION = P_QUOTE_DEST
                               AND QM.CUSTOMER_ID = P_CUSTOMERID
                               AND QM.SHIPMENT_MODE = P_SHMODE
                               AND QM.IE_FLAG = 'E'
                               AND ((QM.ACTIVE_FLAG = 'I' AND
                                   QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                                   (QM.ACTIVE_FLAG = 'A'))
                               AND IQR.SELL_BUY_FLAG = 'BR'
                               AND QM.TERMINAL_ID IN
                                   (SELECT TERMINALID
                                      FROM FS_FR_TERMINALMASTER)
                               AND IQR.BUYRATE_ID = J.SELLCHARGEID
                               AND IQR.RATE_LANE_NO = J.LANE_NO
                               AND IQR.VERSION_NO = J.VERSION_NO
                               AND IQR.Break_Point = J.Chargeslab)
                       AND QR.SELL_BUY_FLAG = 'BR') --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND SELLCHARGEID = J.SELLCHARGEID
               AND LANE_NO = J.LANE_NO
               AND VERSION_NO = J.VERSION_NO;


          END IF;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    --@@WPBN issue-143250
    IF UPPER(P_OPERATION) = 'MODIFY' THEN
      FOR I IN (SELECT BUY_CHARGE_ID, SELLCHARGEID, SEL_BUY_FLAG, CHARGESLAB
                  FROM TEMP_CHARGES) LOOP
        --exit when no_data_found
        IF (UPPER(I.SEL_BUY_FLAG) = 'RSR' OR UPPER(I.SEL_BUY_FLAG) = 'CSR') THEN
          OPEN V_CURR FOR
            SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                   MARGIN_TYPE,
                   MARGIN,
                   DISCOUNT_TYPE,
                   DISCOUNT,
                   FREQUENCY_CHECKED,
                   CARRIER_CHECKED,
                   TRANSIT_CHECKED,
                   VALIDITY_CHECKED
              FROM QMS_QUOTE_RATES
             WHERE QUOTE_ID IN
                   (SELECT ID
                      FROM QMS_QUOTE_MASTER A
                     WHERE QUOTE_ID = P_QUOTE_ID
                       AND VERSION_NO =
                           (SELECT MAX(VERSION_NO)
                              FROM QMS_QUOTE_MASTER B
                             WHERE A.QUOTE_ID = B.QUOTE_ID))
               AND BREAK_POINT = I.CHARGESLAB
               AND BUYRATE_ID = TO_NUMBER(I.BUY_CHARGE_ID)
               AND SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID);
          FETCH V_CURR
            INTO V_MARGIN_DISCOUNT_FLAG, V_MARGIN_TYPE, V_MARGIN, V_DISCOUNT_TYPE, V_DISCOUNT, V_FREQUENCYCHECKED, V_CARRIERCHECKED, V_TRANSITCHECKED, V_VALIDITYCHECKED;
          IF V_CURR%FOUND THEN
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   CHECKED_FLAG         = DECODE(V_MARGIN_DISCOUNT_FLAG,'M','Y','D','Y','N'),
                   FREQUENCY_CHECKED    = V_FREQUENCYCHECKED,
                   CARRIER_CHECKED      = V_CARRIERCHECKED,
                   TRANSITTIME_CHECKED  = V_TRANSITCHECKED,
                   RATEVALIDITY_CHECKED = V_VALIDITYCHECKED
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
          CLOSE V_CURR;
        ELSIF (UPPER(I.SEL_BUY_FLAG) = 'BR') THEN
          OPEN V_CURR FOR
            SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                   MARGIN_TYPE,
                   MARGIN,
                   DISCOUNT_TYPE,
                   DISCOUNT,
                   FREQUENCY_CHECKED,
                   CARRIER_CHECKED,
                   TRANSIT_CHECKED,
                   VALIDITY_CHECKED
              FROM QMS_QUOTE_RATES
             WHERE QUOTE_ID IN
                   (SELECT ID
                      FROM QMS_QUOTE_MASTER A
                     WHERE QUOTE_ID = P_QUOTE_ID
                       AND VERSION_NO =
                           (SELECT MAX(VERSION_NO)
                              FROM QMS_QUOTE_MASTER B
                             WHERE A.QUOTE_ID = B.QUOTE_ID))
               AND BREAK_POINT = I.CHARGESLAB
               AND BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID);
          FETCH V_CURR
            INTO V_MARGIN_DISCOUNT_FLAG, V_MARGIN_TYPE, V_MARGIN, V_DISCOUNT_TYPE, V_DISCOUNT, V_FREQUENCYCHECKED, V_CARRIERCHECKED, V_TRANSITCHECKED, V_VALIDITYCHECKED;
          IF V_CURR%FOUND THEN
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   CHECKED_FLAG         = DECODE(V_MARGIN_DISCOUNT_FLAG,'M','Y','D','Y','N'),
                   FREQUENCY_CHECKED    = V_FREQUENCYCHECKED,
                   CARRIER_CHECKED      = V_CARRIERCHECKED,
                   TRANSITTIME_CHECKED  = V_TRANSITCHECKED,
                   RATEVALIDITY_CHECKED = V_VALIDITYCHECKED
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
          CLOSE V_CURR;
        ELSE
          OPEN V_CURR FOR
            SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                   MARGIN_TYPE,
                   MARGIN,
                   DISCOUNT_TYPE,
                   DISCOUNT
              FROM QMS_QUOTE_RATES
             WHERE QUOTE_ID IN (SELECT ID
                                  FROM QMS_QUOTE_MASTER A
                                 WHERE QUOTE_ID = P_QUOTE_ID
                                   AND ACTIVE_FLAG = 'A')
               AND SELL_BUY_FLAG = I.SEL_BUY_FLAG
               AND BREAK_POINT = I.CHARGESLAB;
          FETCH V_CURR
            INTO V_MARGIN_DISCOUNT_FLAG, V_MARGIN_TYPE, V_MARGIN, V_DISCOUNT_TYPE, V_DISCOUNT;
          IF V_CURR%FOUND THEN
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   CHECKED_FLAG         = DECODE(V_MARGIN_DISCOUNT_FLAG,'M','Y','D','Y','N')
             WHERE CHARGESLAB = I.CHARGESLAB
               AND I.SEL_BUY_FLAG = I.SEL_BUY_FLAG;
          END IF;
          CLOSE V_CURR;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(P_OPERATION) = 'ADD' OR UPPER(P_OPERATION) = 'COPY' THEN
      FOR I IN (SELECT BUY_CHARGE_ID, SELLCHARGEID, SEL_BUY_FLAG, CHARGESLAB
                  FROM TEMP_CHARGES
                 WHERE SELECTED_FLAG = 'Y') LOOP
        --exit when no_data_found
        IF (UPPER(I.SEL_BUY_FLAG) = 'RSR' OR UPPER(I.SEL_BUY_FLAG) = 'CSR') THEN
          OPEN V_CURR FOR
            SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                   MARGIN_TYPE,
                   MARGIN,
                   DISCOUNT_TYPE,
                   DISCOUNT,
                   FREQUENCY_CHECKED,
                   CARRIER_CHECKED,
                   TRANSIT_CHECKED,
                   VALIDITY_CHECKED
              FROM QMS_QUOTE_RATES
             WHERE QUOTE_ID =
                   (SELECT MAX(QM.ID)
                      FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                     WHERE QM.ID = IQR.QUOTE_ID
                       AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                       AND QM.DEST_LOCATION = P_QUOTE_DEST
                       AND QM.CUSTOMER_ID = P_CUSTOMERID
                       AND QM.SHIPMENT_MODE = P_SHMODE
                       AND QM.IE_FLAG = 'E'
                       AND ((QM.ACTIVE_FLAG = 'I' AND
                           QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                           (QM.ACTIVE_FLAG = 'A'))
                       AND IQR.SELL_BUY_FLAG IN ('RSR', 'CSR')
                       AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER')
                       AND IQR.BUYRATE_ID = TO_NUMBER(I.BUY_CHARGE_ID)
                       AND IQR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)) --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND BREAK_POINT = I.CHARGESLAB
               AND BUYRATE_ID = TO_NUMBER(I.BUY_CHARGE_ID)
               AND SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID);
          FETCH V_CURR
            INTO V_MARGIN_DISCOUNT_FLAG, V_MARGIN_TYPE, V_MARGIN, V_DISCOUNT_TYPE, V_DISCOUNT, V_FREQUENCYCHECKED, V_CARRIERCHECKED, V_TRANSITCHECKED, V_VALIDITYCHECKED;
          IF V_CURR%FOUND THEN
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   CHECKED_FLAG         = DECODE(V_MARGIN_DISCOUNT_FLAG,'M','Y','D','Y','N'),
                   FREQUENCY_CHECKED    = V_FREQUENCYCHECKED,
                   CARRIER_CHECKED      = V_CARRIERCHECKED,
                   TRANSITTIME_CHECKED  = V_TRANSITCHECKED,
                   RATEVALIDITY_CHECKED = V_VALIDITYCHECKED
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
          CLOSE V_CURR;
        ELSIF (UPPER(I.SEL_BUY_FLAG) = 'BR') THEN
          OPEN V_CURR FOR
            SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                   MARGIN_TYPE,
                   MARGIN,
                   DISCOUNT_TYPE,
                   DISCOUNT,
                   FREQUENCY_CHECKED,
                   CARRIER_CHECKED,
                   TRANSIT_CHECKED,
                   VALIDITY_CHECKED
              FROM QMS_QUOTE_RATES
             WHERE QUOTE_ID =
                   (SELECT MAX(QM.ID)
                      FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                     WHERE QM.ID = IQR.QUOTE_ID
                       AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                       AND QM.DEST_LOCATION = P_QUOTE_DEST
                       AND QM.CUSTOMER_ID = P_CUSTOMERID
                       AND QM.SHIPMENT_MODE = P_SHMODE
                       AND QM.IE_FLAG = 'E'
                       AND ((QM.ACTIVE_FLAG = 'I' AND
                           QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                           (QM.ACTIVE_FLAG = 'A'))
                       AND IQR.SELL_BUY_FLAG IN ('BR')
                       AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER')
                       AND IQR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)) --@@Added by Kameswari for the WPBN issue-171562 on 28/05/09
               AND BREAK_POINT = I.CHARGESLAB
               AND BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID);
          FETCH V_CURR
            INTO V_MARGIN_DISCOUNT_FLAG, V_MARGIN_TYPE, V_MARGIN, V_DISCOUNT_TYPE, V_DISCOUNT, V_FREQUENCYCHECKED, V_CARRIERCHECKED, V_TRANSITCHECKED, V_VALIDITYCHECKED;
          IF V_CURR%FOUND THEN
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   CHECKED_FLAG         = DECODE(V_MARGIN_DISCOUNT_FLAG,'M','Y','D','Y','N'),
                   FREQUENCY_CHECKED    = V_FREQUENCYCHECKED,
                   CARRIER_CHECKED      = V_CARRIERCHECKED,
                   TRANSITTIME_CHECKED  = V_TRANSITCHECKED,
                   RATEVALIDITY_CHECKED = V_VALIDITYCHECKED
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
          CLOSE V_CURR;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    /*
    For i In (Select Buy_Charge_Id, Sellchargeid, Sel_Buy_Flag, Chargeslab
                From Temp_Charges) Loop

      select marginvalue, rate_description
        into v_marginvalue, v_ratedesc
        from temp_charges
       Where Sel_Buy_Flag = i.Sel_Buy_Flag
         and Chargeslab = i.Chargeslab;

      Update Temp_Charges
         Set Marginvalue = Decode(v_ratedesc,
                                  'A FREIGHT RATE',
                                  v_marginvalue,
                                  '0.0')
       Where Sel_Buy_Flag = i.Sel_Buy_Flag
         And Chargeslab = i.Chargeslab;
         */
    --@@Commented and Modified by Kameswari for the WPBN issue-143250
    --@@ Commented And added by subrahmanyam for 219973
    /*            FOR I IN (SELECT BUY_CHARGE_ID, SELLCHARGEID, VERSION_NO,
               SEL_BUY_FLAG, CHARGESLAB, SELECTED_FLAG
              FROM   TEMP_CHARGES)
    */
    FOR I IN (SELECT BUY_CHARGE_ID,
                     SELLCHARGEID,
                     VERSION_NO,
                     SEL_BUY_FLAG,
                     CHARGESLAB,
                     SELECTED_FLAG,
                     RATE_DESCRIPTION
                FROM TEMP_CHARGES) LOOP
      SELECT MARGINVALUE, RATE_DESCRIPTION
        INTO V_MARGINVALUE, V_RATEDESC
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG = I.SEL_BUY_FLAG
         AND CHARGESLAB = I.CHARGESLAB
         AND RATE_DESCRIPTION = I.RATE_DESCRIPTION; --added by subrahmanyam for 219973 on Nov-24

      UPDATE TEMP_CHARGES
         SET MARGINVALUE = DECODE(V_RATEDESC,
                                  'A FREIGHT RATE',
                                  V_MARGINVALUE,
                                  '0.0')
       WHERE SEL_BUY_FLAG = I.SEL_BUY_FLAG
         AND CHARGESLAB = I.CHARGESLAB
         AND SELECTED_FLAG = 'N';
    END LOOP;
    SELECT COUNT(*) INTO V_COUNT FROM TEMP_CHARGES;
    IF V_COUNT > 0 THEN
      SELECT DISTINCT WEIGHT_BREAK
        INTO V_WEIGHTBREAK
        FROM TEMP_CHARGES
       WHERE LINE_NO IN ('0','1');
    END IF;
    COMMIT;

    FOR BASIS IN (SELECT TC.SHIPMENTMODE,TC.CONSOLE_TYPE,TC.CHARGESLAB  FROM TEMP_CHARGES TC) LOOP
    UPDATE TEMP_CHARGES TC SET TC.CHARGEBASIS = MULTI_QUOTE_BASIS(BASIS.SHIPMENTMODE,BASIS.CONSOLE_TYPE,BASIS.CHARGESLAB) WHERE TC.CHARGESLAB =BASIS.CHARGESLAB AND TC.SHIPMENTMODE = BASIS.SHIPMENTMODE;

    END LOOP;
    COMMIT;

    IF UPPER(P_PERMISSION) = 'Y' THEN
      IF (V_WEIGHTBREAK = 'LIST' AND P_SHMODE = '2') THEN
        OPEN P_RS FOR
          SELECT *
            FROM TEMP_CHARGES
           ORDER BY COST_INCURREDAT,
                    SELLCHARGEID,
                    LANE_NO,
                    RATE_DESCRIPTION,
                    CHARGESLAB;
      ELSE
        OPEN P_RS FOR
          SELECT *
            FROM TEMP_CHARGES
           ORDER BY COST_INCURREDAT,
                    SELLCHARGEID,
                    LANE_NO,
                    RATE_DESCRIPTION,
                    LINE_NO;
      END IF;
    ELSE
      IF (V_WEIGHTBREAK = 'LIST' AND P_SHMODE = '2') THEN
        OPEN P_RS FOR
          SELECT *
            FROM TEMP_CHARGES
           WHERE SEL_BUY_FLAG NOT IN ('BR')
           ORDER BY COST_INCURREDAT,
                    SELLCHARGEID,
                    LANE_NO,
                    RATE_DESCRIPTION,
                    CHARGESLAB;
      ELSE
        OPEN P_RS FOR
          SELECT *
            FROM TEMP_CHARGES
           WHERE SEL_BUY_FLAG NOT IN ('BR')
           ORDER BY COST_INCURREDAT,
                    SELLCHARGEID,
                    LANE_NO,
                    RATE_DESCRIPTION,
                    LINE_NO;
      END IF;
    END IF;
  END;

  /*
  This procedure returns the Sell/Buy Cartage Rates and the Zone and Zip Code Mapping for Both Origin(Pickup) and Destination(Delivery).
  These details are displayed in Quote 3rd Screen if it is for a single Zone/Zip Code.
  If Multiple Zone Codes are selected, an annexure containing these details is attached along with the Quote.
  It Returns 2 resultset objects, one containing the rates and the other containing Zone-Zip Mapping.
  The IN Parameters are:
     p_shzipcode                 VARCHAR2 DEFAULT '',
     p_shzones                   VARCHAR2,
     p_consignzipcode            VARCHAR2 DEFAULT '',
     p_consignzones              VARCHAR2,
     p_salespersoncode           VARCHAR2,
     p_terminalid                VARCHAR2,
     p_permission                VARCHAR2 DEFAULT 'Y',
     p_quote_origin              VARCHAR2,
     p_quote_destination         VARCHAR2,
     p_margin                    NUMBER,
     p_customerid                VARCHAR2,
     p_shmode                    VARCHAR2,
     p_operation                 VARCHAR2,
     p_quote_id                  VARCHAR2
  The OUT Parameters are:
     p_rs                  OUT   resultset,
     p_rs2                 OUT   resultset
  */
  PROCEDURE QUOTE_SELL_BUY_CARTAGES_PROC(P_SHZIPCODE          VARCHAR2 DEFAULT '',
                                         P_SHZONES            VARCHAR2,
                                         P_CONSIGNZIPCODE     VARCHAR2 DEFAULT '',
                                         P_CONSIGNZONES       VARCHAR2,
                                         P_SALESPERSONCODE    VARCHAR2,
                                         P_TERMINALID         VARCHAR2,
                                         P_PERMISSION         VARCHAR2 DEFAULT 'Y',
                                         P_QUOTE_ORIGIN       VARCHAR2,
                                         P_QUOTE_DESTINATION  VARCHAR2,
                                         P_MARGIN             NUMBER,
                                         P_DISCOUNT           NUMBER,
                                         P_CUSTOMERID         VARCHAR2,
                                         P_SHMODE             VARCHAR2,
                                         P_SHIPPER_MODE       VARCHAR2,
                                         P_SHIPPR_CNSL_TYPE   VARCHAR2,
                                         P_CONSIGNEE_MODE     VARCHAR2,
                                         P_CONSGNEE_CNSL_TYPE VARCHAR2,
                                         P_OPERATION          VARCHAR2,
                                         P_QUOTE_ID           VARCHAR2,
                                         P_RS                 OUT RESULTSET,
                                         P_RS2                OUT RESULTSET,
                                         P_RS3                OUT RESULTSET,
                                         --Distinct Pickup Charge slabs
                                         P_RS4 OUT RESULTSET
                                         --Distinct Delivery Charge slabs
                                         ) AS
    V_SQL1                 VARCHAR2(4000) := '';
    V_SQL2                 VARCHAR2(4000) := '';
    V_SQL3                 VARCHAR2(4000) := '';
    V_SQL4                 VARCHAR2(4000) := '';
    V_SQL5                 VARCHAR2(4000) := '';
    V_SQL6                 VARCHAR2(4000) := '';
    V_SQL7                 VARCHAR2(4000) := '';
    V_SQL8                 VARCHAR2(4000) := '';
    V_SQL9                 VARCHAR2(4000) := '';
    V_SQL10                VARCHAR2(4000) := '';
    V_SQL11                VARCHAR2(4000) := '';
    V_SQL12                VARCHAR2(4000) := '';
    V_SQL13                VARCHAR2(4000) := '';
    V_SQL14                VARCHAR2(4000) := '';
    V_SQL15                VARCHAR2(4000) := '';
    V_SQL16                VARCHAR2(4000) := '';
    V_SQL17                VARCHAR2(4000) := '';
    V_SQL18                VARCHAR2(4000) := '';
    V_SQL19                VARCHAR2(4000) := '';
    V_MARGIN               NUMBER(8, 2) := 0;
    V_CZONE                VARCHAR2(4000) := '';
    V_SZONE                VARCHAR2(4000) := '';
    V_CNZONE               VARCHAR2(4000) := '';
    V_SHZONE               VARCHAR2(4000) := '';
    V_UNION                VARCHAR2(4000) := '';
    V_UNION1               VARCHAR2(4000) := '';
    V_EXTRA                VARCHAR2(4000) := '';
    V_LOC                  VARCHAR2(4000) := '';
    V_MSZONE               VARCHAR2(4000) := '';
    V_MCZONE               VARCHAR2(4000) := '';
    V_SQLINSERT            VARCHAR2(4000) := '';
    V_TERMINALS            VARCHAR2(32000);
    V_OPR_ADM_FLAG         VARCHAR2(3);
    V_MARGINID             VARCHAR2(10);
    V_QUOTE_ID             NUMBER;
    V_RCOUNT               NUMBER;
    V_MARGIN_DISCOUNT_FLAG VARCHAR2(10);
    V_MARGIN_TYPE          VARCHAR2(10);
    V_Margin_Test          VARCHAR2(2); --kiran.v
    V_DISCOUNT_TYPE        VARCHAR2(10);
    V_DISCOUNT             NUMBER(8, 2) := 0.0;
    V_SHIPPERALPHA         VARCHAR2(50);
    V_SHZIPCODE            VARCHAR2(50);
    V_CONSIGNZIPCODE       VARCHAR2(50);
    V_CONSIGNEEALPHA       VARCHAR2(50);
    V_WHERE                VARCHAR2(50);
    V_POS                  NUMBER;
    V_ORIGIN_COUNTRY_ID    FS_FR_LOCATIONMASTER.COUNTRYID%TYPE;
    V_DEST_COUNTRY_ID      FS_FR_LOCATIONMASTER.COUNTRYID%TYPE;
    V_COUNT                VARCHAR2(20);
    --v_pick_wt_break          Varchar2(50);
    --v_del_wt_break           Varchar2(50);
    /*     CURSOR c1
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
          FROM DUAL
        UNION
        SELECT     child_terminal_id term_id
              FROM FS_FR_TERMINAL_REGN
        CONNECT BY PRIOR child_terminal_id = parent_terminal_id
        START WITH parent_terminal_id = p_terminalid);*/
    /*CURSOR c2
    IS
       (SELECT DISTINCT terminalid
                   FROM FS_FR_TERMINALMASTER
                  WHERE oper_admin_flag <> 'H'
        UNION
        SELECT terminalid term_id
          FROM FS_FR_TERMINALMASTER
         WHERE oper_admin_flag = 'H');*/
  BEGIN
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
      V_TERMINALS := 'SELECT TERMINALID FROM FS_FR_TERMINALMASTER';
      /*FOR i IN c2
      LOOP
         v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
      END LOOP;*/
    ELSE
      DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
                               'v_terminal_id',
                               P_TERMINALID);
      V_TERMINALS := 'Select Parent_Terminal_Id Term_Id
		       FROM FS_FR_TERMINAL_REGN
		     CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
		      START WITH Child_Terminal_Id = sys_context(''QUOTE_CONTEXT'',''v_terminal_id'')
		     UNION
		     SELECT Terminalid Term_Id
		       FROM FS_FR_TERMINALMASTER
		      WHERE Oper_Admin_Flag = ''H''
		     UNION
		     SELECT sys_context(''QUOTE_CONTEXT'',''v_terminal_id'') Term_Id
		       FROM Dual
		     UNION
		     SELECT Child_Terminal_Id Term_Id
		       FROM FS_FR_TERMINAL_REGN
		     CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
		      START WITH Parent_Terminal_Id = sys_context(''QUOTE_CONTEXT'',''v_terminal_id'')';
    END IF;
    V_TERMINALS := SUBSTR(V_TERMINALS, 1, LENGTH(V_TERMINALS) - 1);
    V_MARGIN    := P_MARGIN;
    --v_discount := p_discount;
    --Cartages
    -- If Shipperzip code is sended then
    SELECT COUNTRYID
      INTO V_ORIGIN_COUNTRY_ID
      FROM FS_FR_LOCATIONMASTER LM
     WHERE LM.LOCATIONID = P_QUOTE_ORIGIN;
    --Destination Country
    SELECT COUNTRYID
      INTO V_DEST_COUNTRY_ID
      FROM FS_FR_LOCATIONMASTER LM
     WHERE LM.LOCATIONID = P_QUOTE_DESTINATION;
    IF P_SHZIPCODE IS NOT NULL OR TRIM(P_SHZIPCODE) <> '' THEN
      -- BEGIN
      V_LOC := '''' || P_QUOTE_ORIGIN || '''';
      IF V_ORIGIN_COUNTRY_ID = 'CA' THEN
        --V_SHZIPCODE := '''' || P_SHZIPCODE || '''';  --@@Commented and Modified by Kameswari for the WPBN issue-177677 on 24/07/09
        V_SHZIPCODE := P_SHZIPCODE;
      ELSE
        V_SHZIPCODE := P_SHZIPCODE;
      END IF;
      IF INSTR(V_SHZIPCODE, '-') != 0 THEN
        V_SHIPPERALPHA := SUBSTR(V_SHZIPCODE,
                                 1,
                                 INSTR(V_SHZIPCODE, '-') - 1);
        V_SHZIPCODE    := SUBSTR(V_SHZIPCODE,
                                 INSTR(V_SHZIPCODE, '-') + 1,
                                 LENGTH(V_SHZIPCODE));
      END IF;
      IF V_SHIPPERALPHA IS NOT NULL OR TRIM(V_SHIPPERALPHA) <> '' THEN
        V_WHERE := ' AND D.ALPHANUMERIC= ' || '''' || V_SHIPPERALPHA || '''';
        /*ELSE
        v_where := ' AND D.ALPHANUMERIC IS NULL ';*/
      END IF;
      --Origin Country
      IF V_ORIGIN_COUNTRY_ID = 'CA' THEN
        EXECUTE IMMEDIATE ('SELECT distinct d.ZONE FROM qms_zone_code_master_ca m, qms_zone_code_dtl_ca d WHERE d.zone_code = m.zone_code AND m.location_id =' ||
                          V_LOC || ' AND ' || '''' || V_SHZIPCODE || '''' ||
                          ' BETWEEN d.from_zipcode AND d.to_zipcode AND D.ZONE=' || '''' ||
                          P_SHZONES || '''' || ' AND M.SHIPMENT_MODE = ' || '''' ||
                          P_SHIPPER_MODE ||
                          ''' AND NVL(M.CONSOLE_TYPE,''~'')= ' || '''' ||
                          P_SHIPPR_CNSL_TYPE || '''')
          INTO V_SZONE;
      ELSE
        EXECUTE IMMEDIATE ('SELECT distinct d.ZONE FROM qms_zone_code_master m, qms_zone_code_dtl d WHERE d.zone_code = m.zone_code AND m.origin_location =' ||
                          V_LOC || ' AND TO_NUMBER (' || V_SHZIPCODE ||
                          ') BETWEEN TO_NUMBER (d.from_zipcode) AND TO_NUMBER (d.to_zipcode)' ||
                          V_WHERE || ' AND M.SHIPMENT_MODE = ' || '''' ||
                          P_SHIPPER_MODE ||
                          ''' AND NVL(M.CONSOLE_TYPE,''~'')= ' || '''' ||
                          P_SHIPPR_CNSL_TYPE || '''' || ' AND d.zone IN (' || '''' ||
                          P_SHZONES || '''' || ' )')
          INTO V_SZONE;
      END IF;
      V_SHZONE := V_SZONE;
      V_SZONE  := ' AND csd.zone_code IN (' || '''' || V_SZONE || '''' || ')';
    ELSIF P_SHZONES IS NOT NULL OR TRIM(P_SHZONES) <> '' THEN
      V_SZONE  := ' AND csd.zone_code IN ( ' ||
                  QMS_RSR_RATES_PKG.SEPERATOR(P_SHZONES) || ')';
      V_MSZONE := ' And d.ZONE IN (' ||
                  QMS_RSR_RATES_PKG.SEPERATOR(P_SHZONES) || ' )';
      V_POS    := INSTR(P_SHZONES, '~', 1, 1);
      IF (V_POS = 0) THEN
        V_SHZONE := P_SHZONES;
      ELSE
         V_SHZONE := QMS_RSR_RATES_PKG.SEPERATOR(P_SHZONES) || ')';
      END IF;
    END IF;
    IF (P_SHZONES IS NULL OR TRIM(P_SHZONES) = '') AND
       (P_SHZIPCODE IS NULL OR TRIM(P_SHZIPCODE) = '') THEN
      V_SZONE := '';
    END IF;
    -- If Consignee zipCode is sended then
    V_LOC := '''' || P_QUOTE_DESTINATION || '''';
    IF P_CONSIGNZIPCODE IS NOT NULL OR TRIM(P_CONSIGNZIPCODE) <> '' THEN
      --BEGIN
      V_WHERE := '';
      IF V_DEST_COUNTRY_ID = 'CA' THEN
        --  V_CONSIGNZIPCODE := '''' || P_CONSIGNZIPCODE || '''';--@@Commented and Modified by Kameswari for the WPBN issue-177677 on 24/07/09
        V_CONSIGNZIPCODE := P_CONSIGNZIPCODE;
      ELSE
        V_CONSIGNZIPCODE := P_CONSIGNZIPCODE;
      END IF;
      IF INSTR(V_CONSIGNZIPCODE, '-') != 0 THEN
        V_CONSIGNEEALPHA := SUBSTR(V_CONSIGNZIPCODE,
                                   1,
                                   INSTR(V_CONSIGNZIPCODE, '-') - 1);
        V_CONSIGNZIPCODE := SUBSTR(V_CONSIGNZIPCODE,
                                   INSTR(V_CONSIGNZIPCODE, '-') + 1,
                                   LENGTH(V_CONSIGNZIPCODE));
      END IF;
      IF V_CONSIGNEEALPHA IS NOT NULL OR TRIM(V_CONSIGNEEALPHA) <> '' THEN
        V_WHERE := ' AND D.ALPHANUMERIC= ' || '''' || V_CONSIGNEEALPHA || '''';
        /*ELSE
        v_where := ' AND D.ALPHANUMERIC IS NULL ';*/
      END IF;
      IF V_DEST_COUNTRY_ID = 'CA' THEN

        EXECUTE IMMEDIATE ('SELECT distinct d.ZONE FROM qms_zone_code_master_ca m, qms_zone_code_dtl_ca d WHERE d.zone_code = m.zone_code AND m.location_id = ' ||
                          V_LOC || ' AND ' || '''' || V_CONSIGNZIPCODE || '''' ||
                          '  BETWEEN d.from_zipcode AND d.to_zipcode AND D.ZONE=' || '''' ||
                          P_CONSIGNZONES || '''' ||
                          ' AND M.SHIPMENT_MODE = ' || '''' ||
                          P_CONSIGNEE_MODE ||
                          ''' AND NVL(M.CONSOLE_TYPE,''~'')= ' || '''' ||
                          P_CONSGNEE_CNSL_TYPE || '''')
          INTO V_CZONE;
      ELSE
        EXECUTE IMMEDIATE ('SELECT distinct d.ZONE FROM qms_zone_code_master m, qms_zone_code_dtl d WHERE d.zone_code = m.zone_code AND m.origin_location = ' ||
                          V_LOC || ' AND TO_NUMBER (' || V_CONSIGNZIPCODE ||
                          ')  BETWEEN TO_NUMBER (d.from_zipcode) AND TO_NUMBER (d.to_zipcode)' ||
                          V_WHERE || ' AND M.SHIPMENT_MODE = ' || '''' ||
                          P_CONSIGNEE_MODE ||
                          ''' AND NVL(M.CONSOLE_TYPE,''~'')= ' || '''' ||
                          P_CONSGNEE_CNSL_TYPE || '''' ||
                          ' AND d.zone IN (' || '''' || P_CONSIGNZONES || '''' || ' )')
          INTO V_CZONE;
      END IF;
      V_CNZONE := V_CZONE;
      V_CZONE  := ' AND csd.zone_code IN (' || '''' || V_CZONE || '''' || ')';
    ELSIF P_CONSIGNZONES IS NOT NULL OR TRIM(P_CONSIGNZONES) <> '' THEN
      V_CZONE  := ' AND csd.zone_code IN ( ' ||
                  QMS_RSR_RATES_PKG.SEPERATOR(P_CONSIGNZONES) || ')';
      V_MCZONE := ' And d.ZONE IN (' ||
                  QMS_RSR_RATES_PKG.SEPERATOR(P_CONSIGNZONES) || ' )';
      V_POS    := 0;
      V_POS    := INSTR(P_CONSIGNZONES, '~', 1, 1);
      IF (V_POS = 0) THEN
        V_CNZONE := P_CONSIGNZONES;
      ELSE
        V_CNZONE :=  QMS_RSR_RATES_PKG.SEPERATOR(P_CONSIGNZONES) || ' )';
      END IF;
    END IF;
    IF (P_CONSIGNZIPCODE IS NULL OR TRIM(P_CONSIGNZIPCODE) = '') AND
       (P_CONSIGNZONES IS NULL OR TRIM(P_CONSIGNZONES) = '') THEN
      V_CZONE := '';
    END IF;
    /*tied_custinfo_cartages_proc(p_customerid,
    p_quote_origin,
    p_quote_destination,
    p_terminalid,
    p_shmode,
    p_permission,
    v_shzone,
    v_cnzone,
    p_shipper_mode,
    p_shippr_cnsl_type,
    p_consignee_mode,
    p_consgnee_cnsl_type,
    p_operation,
    p_quote_id);*/
    SELECT COUNT(*) INTO V_COUNT FROM TEMP_CHARGES;
    V_SQLINSERT := 'Insert Into Temp_Charges
	   (Sellchargeid,
	    Chargedescid,
	    Cost_Incurredat,
	    Chargeslab,
	    Currency,
	    Buyrate,
	    Sellrate,
	    Margin_Type,
	    Marginvalue,
	    Chargebasis,
	    Sel_Buy_Flag,
	    Buy_Charge_Id,
	    ZONE,
	    Terminalid,
	    Density_Ratio,
	    Efrom,
	    Validupto,
	    Line_No,
	    Weight_Break,
	    Rate_Type,
	    Rate_Indicator
	    )';
    V_SQL1      := 'SELECT  csd.sell_cartage_id sellchargeid,';
    V_SQL19     := 'SELECT csd.cartage_id sellchargeid,';
    V_SQL2      := '''Pickup Charges'' chargedescid, ''Pickup'' cost_incurredat,';
    V_SQL3      := ' csd.chargeslab, csd.currency,csd.buyrate_amt buyrate, csd.chargerate sellrate,';
    --v_sql4  := ' csd.margin_type, to_number(csd.margin) marginvalue, decode(upper(chargeslab),''MIN'',''PER SHIPMENT'', (Select BASIS_DESCRIPTION From Qms_Charge_Basismaster Where CHARGEBASIS=Csd.Charge_Basis)) chargebasis, ''SC'' sel_buy_flag,to_char(csd.cartage_id) buychargeid,CSD.ZONE_CODE, CM.TERMINALID';
    V_SQL4   := ' csd.margin_type,to_number(0)'|| --|| V_DISCOUNT ||commented by govind for getting default margin
                ' marginvalue, decode(upper(chargeslab),''MIN'',''PER SHIPMENT'', (Select BASIS_DESCRIPTION From Qms_Charge_Basismaster Where CHARGEBASIS=Csd.Charge_Basis)) chargebasis, ''SC'' sel_buy_flag,to_char(csd.cartage_id) buychargeid,CSD.ZONE_CODE, CM.TERMINALID';
    V_SQL13  := ' ,(select DISTINCT DENSITY_CODE from QMS_CARTAGE_BUYDTL qbd,QMS_CARTAGE_SELLDTL qcs  where qcs.CARTAGE_ID=csd.cartage_id and qcs.CARTAGE_ID=qbd.CARTAGE_ID AND QCS.ZONE_CODE=QBD.ZONE_CODE AND QCS.ZONE_CODE=csd.ZONE_CODE AND qbd.CHARGE_TYPE=csd.CHARGE_TYPE) densityratio';
    V_SQL15  := ',csd.EFFECTIVE_FROM efrom,csd.valid_upto validupto, csd.line_no, cm.weight_break,cm.rate_type,csd.CHARGERATE_INDICATOR ';
    V_SQL5   := ' FROM qms_cartage_buysellcharges cm,qms_cartage_selldtl csd WHERE cm.cartage_id = csd.cartage_id and (csd.ACTIVEINACTIVE=''A'' or csd.ACTIVEINACTIVE is null) AND CSD.CHARGE_TYPE NOT IN (SELECT COST_INCURREDAT FROM TEMP_CHARGES) AND csd.ACCEPTANCE_FLAG IS NULL ';
    V_SQL6   := V_SZONE ||
                ' AND cm.location_id =:v_origin AND CM.SHIPMENT_MODE =:v_ship_mode
	      AND NVL(CM.CONSOLE_TYPE,''~'') =:v_shppr_cnsl_type AND UPPER(csd.charge_type) = ';
    V_UNION  := '''PICKUP'' union ';
    V_SQL7   := '''P'' margin_type,to_number(0)' ||--|| V_MARGIN || commented by govind for getting default margin
                ' marginvalue, decode(upper(chargeslab),''MIN'',''PER SHIPMENT'',(Select BASIS_DESCRIPTION From Qms_Charge_Basismaster Where CHARGEBASIS=Csd.Charge_Basis)) chargebasis, ''BC'' sel_buy_flag,'''' buychargeid, CSD.ZONE_CODE, CM.TERMINALID';
    V_SQL16  := ' ,(select DISTINCT DENSITY_CODE from QMS_CARTAGE_BUYSELLCHARGES qbc,QMS_CARTAGE_BUYDTL qbd  where qbc.CARTAGE_ID=csd.cartage_id and qbd.CARTAGE_ID=qbc.CARTAGE_ID and qbd.ZONE_CODE=csd.ZONE_CODE AND qbd.CHARGE_TYPE=csd.CHARGE_TYPE) densityratio';
    V_SQL18  := ' ,csd.effective_from efrom, csd.valid_upto validupto, csd.line_no, cm.weight_break,cm.RATE_TYPE,csd.CHARGERATE_INDICATOR ';
    V_SQL8   := ' FROM qms_cartage_buysellcharges cm,qms_cartage_BUYdtl csd WHERE cm.cartage_id = csd.cartage_id and (csd.ACTIVEINACTIVE=''A'' or csd.ACTIVEINACTIVE is null) And csd.CHARGE_TYPE Not In (Select COST_INCURREDAT From Temp_Charges)';
    V_SQL9   := '  and csd.cartage_id not in ( SELECT csd.cartage_id FROM qms_cartage_buysellcharges cm,qms_cartage_selldtl csd WHERE cm.cartage_id = csd.cartage_id ' ||
                V_SZONE ||
                ' AND cm.location_id =:v_origin AND CM.SHIPMENT_MODE =:v_ship_mode AND NVL(CM.CONSOLE_TYPE,''~'') =:v_shppr_cnsl_type AND UPPER(csd.charge_type) = ' || '''' ||
                'PICKUP' || '''' || ')';
    V_EXTRA  := ' csd.chargeslab, cm.currency,csd.chargerate buyrate, to_number(''0'') sellrate,';
    V_SQL10  := ' ''Delivery Charges'' chargedescid,''Delivery'' cost_incurredat,';
    V_UNION1 := ' ''DELIVERY''  union ';
    V_SQL11  := ' and csd.cartage_id not in ( SELECT csd.cartage_id FROM qms_cartage_buysellcharges cm,qms_cartage_selldtl csd WHERE cm.cartage_id = csd.cartage_id ' ||
                V_CZONE ||
                ' AND cm.location_id =:v_quote_dest AND CM.SHIPMENT_MODE =:v_cnsgne_mode AND NVL(CM.CONSOLE_TYPE,''~'') =:v_cnsgne_cnsl_type AND UPPER(csd.charge_type) = ' || '''' ||
                'DELIVERY' || '''' || ')';
    V_SQL12  := V_CZONE || ' AND cm.location_id =:v_quote_dest AND CM.SHIPMENT_MODE =:v_cnsgne_mode AND NVL(CM.CONSOLE_TYPE,''~'') =:v_cnsgne_cnsl_type
	     AND UPPER(csd.charge_type) = ';
    BEGIN
      IF V_SZONE IS NOT NULL AND LENGTH(TRIM(V_SZONE)) != 0 AND
         V_CZONE IS NOT NULL AND LENGTH(TRIM(V_CZONE)) != 0 THEN
        print_out(V_SQLINSERT || V_SQL1 || V_SQL2 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL6 || V_UNION || V_SQL19 || V_SQL2 ||
                          V_EXTRA || V_SQL7 || V_SQL16 || V_SQL17 ||
                          V_SQL18 || V_SQL8 || V_SQL6 || '''PICKUP''' ||
                          V_SQL9 || ' union ' || V_SQL1 || V_SQL10 ||
                          V_SQL3 || V_SQL4 || V_SQL13 || V_SQL14 ||
                          V_SQL15 || V_SQL5 || V_SQL12 || V_UNION1 ||
                          V_SQL19 || V_SQL10 || V_EXTRA || V_SQL7 ||
                          V_SQL16 || V_SQL17 || V_SQL18 || V_SQL8 ||
                          V_SQL12 || '''DELIVERY''' || V_SQL11);

        EXECUTE IMMEDIATE (V_SQLINSERT || V_SQL1 || V_SQL2 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL6 || V_UNION || V_SQL19 || V_SQL2 ||
                          V_EXTRA || V_SQL7 || V_SQL16 || V_SQL17 ||
                          V_SQL18 || V_SQL8 || V_SQL6 || '''PICKUP''' ||
                          V_SQL9 || ' union ' || V_SQL1 || V_SQL10 ||
                          V_SQL3 || V_SQL4 || V_SQL13 || V_SQL14 ||
                          V_SQL15 || V_SQL5 || V_SQL12 || V_UNION1 ||
                          V_SQL19 || V_SQL10 || V_EXTRA || V_SQL7 ||
                          V_SQL16 || V_SQL17 || V_SQL18 || V_SQL8 ||
                          V_SQL12 || '''DELIVERY''' || V_SQL11)
          USING P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE;
      ELSIF (V_SZONE IS NULL OR
            (V_SZONE IS NOT NULL AND LENGTH(TRIM(V_SZONE)) = 0)) THEN
dbms_output.put_line(V_SQLINSERT || V_SQL1 || V_SQL10 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL12 || V_UNION1 || V_SQL19 ||
                          V_SQL10 || V_EXTRA || V_SQL7 || V_SQL16 ||
                          V_SQL17 || V_SQL18 || V_SQL8 || V_SQL12 ||
                          '''DELIVERY''' || V_SQL11);
        EXECUTE IMMEDIATE (V_SQLINSERT || V_SQL1 || V_SQL10 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL12 || V_UNION1 || V_SQL19 ||
                          V_SQL10 || V_EXTRA || V_SQL7 || V_SQL16 ||
                          V_SQL17 || V_SQL18 || V_SQL8 || V_SQL12 ||
                          '''DELIVERY''' || V_SQL11)
          USING P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE;
      ELSE
       print_out(V_SQLINSERT || V_SQL1 || V_SQL2 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL6 || V_UNION || V_SQL19 || V_SQL2 ||
                          V_EXTRA || V_SQL7 || V_SQL16 || V_SQL17 ||
                          V_SQL18 || V_SQL8 || V_SQL6 || '''PICKUP''' ||
                          V_SQL9);
                          dbms_output.put_line(V_SQLINSERT || V_SQL1 || V_SQL2 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL6 || V_UNION || V_SQL19 || V_SQL2 ||
                          V_EXTRA || V_SQL7 || V_SQL16 || V_SQL17 ||
                          V_SQL18 || V_SQL8 || V_SQL6 || '''PICKUP''' ||
                          V_SQL9);
        EXECUTE IMMEDIATE (V_SQLINSERT || V_SQL1 || V_SQL2 || V_SQL3 ||
                          V_SQL4 || V_SQL13 || V_SQL14 || V_SQL15 ||
                          V_SQL5 || V_SQL6 || V_UNION || V_SQL19 || V_SQL2 ||
                          V_EXTRA || V_SQL7 || V_SQL16 || V_SQL17 ||
                          V_SQL18 || V_SQL8 || V_SQL6 || '''PICKUP''' ||
                          V_SQL9)
          USING P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE;
      END IF;
    END;
    SELECT COUNT(*) INTO V_COUNT FROM TEMP_CHARGES;
    COMMIT;
    IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'VIEW' THEN
      ---PROCESS FOR MODIFY BEGINS HERE (SETTING A Y FLAG IF IT ALREADY EXISTS IN QUOTE_RATES TABLE)
      FOR CHARGE IN (SELECT ID

                       FROM QMS_QUOTE_MASTER A
                      WHERE QUOTE_ID = P_QUOTE_ID
                        AND VERSION_NO =
                            (SELECT MAX(VERSION_NO)
                               FROM QMS_QUOTE_MASTER B
                              WHERE A.QUOTE_ID = B.QUOTE_ID)) LOOP
             V_QUOTE_ID := CHARGE.ID;
        FOR J IN (SELECT SELLCHARGEID,
                         ZONE,
                         SEL_BUY_FLAG,
                         COST_INCURREDAT,
                         SELECTED_FLAG
                    FROM TEMP_CHARGES
                   WHERE SEL_BUY_FLAG IN ('SC', 'BC')) LOOP
          IF (J.SEL_BUY_FLAG = 'BC') THEN
            IF (J.COST_INCURREDAT) = 'Pickup' AND V_SHZONE IS NOT NULL THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID = V_QUOTE_ID
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.SHIPPERZONES ,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT;
              IF (V_RCOUNT > 0) THEN
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'Y'
                 WHERE SELLCHARGEID = J.SELLCHARGEID;
              ELSE
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'N'
                 WHERE SELLCHARGEID = J.SELLCHARGEID
                   AND SELECTED_FLAG IS NULL;
              END IF;
            ELSIF (J.COST_INCURREDAT) = 'Delivery' AND V_CNZONE IS NOT NULL THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID = V_QUOTE_ID
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT;
              IF (V_RCOUNT > 0) THEN
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'Y'
                 WHERE SELLCHARGEID = J.SELLCHARGEID;
              ELSE
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'N'
                 WHERE SELLCHARGEID = J.SELLCHARGEID
                   AND SELECTED_FLAG IS NULL;
              END IF;
            END IF;
          ELSE
            IF (J.COST_INCURREDAT) = 'Pickup' AND V_SHZONE IS NOT NULL THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID = V_QUOTE_ID
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.SHIPPERZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT;
              IF (V_RCOUNT > 0) THEN
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'Y'
                 WHERE SELLCHARGEID = J.SELLCHARGEID;
              ELSE
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'N'
                 WHERE SELLCHARGEID = J.SELLCHARGEID
                   AND SELECTED_FLAG IS NULL;
              END IF;
            ELSIF (J.COST_INCURREDAT) = 'Delivery' AND V_CNZONE IS NOT NULL THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID = V_QUOTE_ID
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT;

              IF (V_RCOUNT > 0) THEN
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'Y'
                 WHERE SELLCHARGEID = J.SELLCHARGEID;
              ELSE
                UPDATE TEMP_CHARGES
                   SET SELECTED_FLAG = 'N'
                 WHERE SELLCHARGEID = J.SELLCHARGEID
                   AND SELECTED_FLAG IS NULL;
              END IF;
            END IF;
          END IF;
        END LOOP;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(P_OPERATION) = 'ADD' OR UPPER(P_OPERATION) = 'COPY' THEN
      FOR J IN (SELECT SELLCHARGEID,
                       ZONE,
                       SEL_BUY_FLAG,
                       COST_INCURREDAT,
                       SELECTED_FLAG
                  FROM TEMP_CHARGES
                 WHERE SEL_BUY_FLAG IN ('SC', 'BC')) LOOP
        IF (J.SEL_BUY_FLAG = 'BC') THEN
          IF (J.COST_INCURREDAT) = 'Pickup' AND V_SHZONE IS NOT NULL THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                         AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                         AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                             P_SHIPPR_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND ((QM.ACTIVE_FLAG = 'I' AND
                             QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                             (QM.ACTIVE_FLAG = 'A'))
                         AND IQR.SELL_BUY_FLAG = 'BC'
                         AND UPPER(CHARGE_AT) = 'PICKUP'
                         AND QM.TERMINAL_ID IN
                             (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND MAS.SHIPPERZONES = J.ZONE
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                  AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~') ;--GOVIND
            ELSE
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                         AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                         AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                             P_SHIPPR_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND ((QM.ACTIVE_FLAG = 'I' AND
                             QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                             (QM.ACTIVE_FLAG = 'A'))
                         AND IQR.SELL_BUY_FLAG = 'BC'
                         AND UPPER(CHARGE_AT) = 'PICKUP'
                         AND QM.TERMINAL_ID IN
                             (SELECT PARENT_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                         PARENT_TERMINAL_ID
                               START WITH CHILD_TERMINAL_ID = P_TERMINALID
                              UNION
                              SELECT TERMINALID TERM_ID
                                FROM FS_FR_TERMINALMASTER
                               WHERE OPER_ADMIN_FLAG = 'H'
                              UNION
                              SELECT P_TERMINALID TERM_ID
                                FROM DUAL
                              UNION
                              SELECT CHILD_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                          PARENT_TERMINAL_ID
                               START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.SHIPPERZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~') ;--GOVIND
            END IF;
            IF (V_RCOUNT > 0) THEN
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'Y'
               WHERE SELLCHARGEID = J.SELLCHARGEID;
            ELSE
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'N'
               WHERE SELLCHARGEID = J.SELLCHARGEID
                 AND SELECTED_FLAG IS NULL;
            END IF;
          ELSIF (J.COST_INCURREDAT) = 'Delivery' AND V_CNZONE IS NOT NULL THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                         AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                         AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                             P_CONSGNEE_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                         AND UPPER(CHARGE_AT) = 'DELIVERY'
                         AND QM.TERMINAL_ID IN
                             (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~'); --GOVIND;
            ELSE
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                         AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                         AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                             P_CONSGNEE_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                         AND UPPER(CHARGE_AT) = 'DELIVERY'
                         AND QM.TERMINAL_ID IN
                             (SELECT PARENT_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                         PARENT_TERMINAL_ID
                               START WITH CHILD_TERMINAL_ID = P_TERMINALID
                              UNION
                              SELECT TERMINALID TERM_ID
                                FROM FS_FR_TERMINALMASTER
                               WHERE OPER_ADMIN_FLAG = 'H'
                              UNION
                              SELECT P_TERMINALID TERM_ID
                                FROM DUAL
                              UNION
                              SELECT CHILD_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                          PARENT_TERMINAL_ID
                               START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                 AND RT.SELL_BUY_FLAG = 'BC'
                 AND RT.BUYRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~') ;--GOVIND;
            END IF;
            IF (V_RCOUNT > 0) THEN
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'Y'
               WHERE SELLCHARGEID = J.SELLCHARGEID;
            ELSE
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'N'
               WHERE SELLCHARGEID = J.SELLCHARGEID
                 AND SELECTED_FLAG IS NULL;
            END IF;
          END IF;
        ELSE
          IF (J.COST_INCURREDAT) = 'Pickup' AND V_SHZONE IS NOT NULL THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                         AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                         AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                             P_SHIPPR_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND ((QM.ACTIVE_FLAG = 'I' AND
                             QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                             (QM.ACTIVE_FLAG = 'A'))
                         AND IQR.SELL_BUY_FLAG = 'SC'
                         AND UPPER(CHARGE_AT) = 'PICKUP'
                         AND QM.TERMINAL_ID IN
                             (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.SHIPPERZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~'); --GOVIND;
            ELSE
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                         AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                         AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                             P_SHIPPR_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND ((QM.ACTIVE_FLAG = 'I' AND
                             QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                             (QM.ACTIVE_FLAG = 'A'))
                         AND IQR.SELL_BUY_FLAG = 'SC'
                         AND UPPER(CHARGE_AT) = 'PICKUP'
                         AND QM.TERMINAL_ID IN
                             (SELECT PARENT_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                         PARENT_TERMINAL_ID
                               START WITH CHILD_TERMINAL_ID = P_TERMINALID
                              UNION
                              SELECT TERMINALID TERM_ID
                                FROM FS_FR_TERMINALMASTER
                               WHERE OPER_ADMIN_FLAG = 'H'
                              UNION
                              SELECT P_TERMINALID TERM_ID
                                FROM DUAL
                              UNION
                              SELECT CHILD_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                          PARENT_TERMINAL_ID
                               START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.SHIPPERZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~'); --GOVIND;
            END IF;
            IF (V_RCOUNT > 0) THEN
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'Y'
               WHERE SELLCHARGEID = J.SELLCHARGEID;
            ELSE
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'N'
               WHERE SELLCHARGEID = J.SELLCHARGEID
                 AND SELECTED_FLAG IS NULL;
            END IF;
          ELSIF (J.COST_INCURREDAT) = 'Delivery' AND V_CNZONE IS NOT NULL THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                         AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                         AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                             P_CONSGNEE_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND IQR.SELL_BUY_FLAG = 'SC'
                         AND UPPER(CHARGE_AT) = 'DELIVERY'
                         AND QM.TERMINAL_ID IN
                             (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~');--GOVIND ;
            ELSE
              SELECT COUNT(*)
                INTO V_RCOUNT
                FROM QMS_QUOTE_RATES RT, QMS_QUOTE_MASTER MAS
               WHERE RT.QUOTE_ID = MAS.ID
                 AND RT.QUOTE_ID =
                     (SELECT MAX(QM.ID)
                        FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                       WHERE QM.ID = IQR.QUOTE_ID
                         AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                         AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                         AND QM.CUSTOMER_ID = P_CUSTOMERID
                         AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                         AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                             P_CONSGNEE_CNSL_TYPE
                         AND QM.IE_FLAG = 'E'
                         AND IQR.SELL_BUY_FLAG = 'SC'
                         AND UPPER(CHARGE_AT) = 'DELIVERY'
                         AND QM.TERMINAL_ID IN
                             (SELECT PARENT_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                         PARENT_TERMINAL_ID
                               START WITH CHILD_TERMINAL_ID = P_TERMINALID
                              UNION
                              SELECT TERMINALID TERM_ID
                                FROM FS_FR_TERMINALMASTER
                               WHERE OPER_ADMIN_FLAG = 'H'
                              UNION
                              SELECT P_TERMINALID TERM_ID
                                FROM DUAL
                              UNION
                              SELECT CHILD_TERMINAL_ID TERM_ID
                                FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                          PARENT_TERMINAL_ID
                               START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                 AND RT.SELL_BUY_FLAG = 'SC'
                 AND RT.SELLRATE_ID = J.SELLCHARGEID
                 AND INSTR(MAS.CONSIGNEEZONES,J.ZONE)!=0
                 AND RT.CHARGE_AT = J.COST_INCURREDAT
                 AND NVL(RT.ZONE_CODE,'~') = NVL(J.ZONE,'~') ;--GOVIND;
            END IF;
            IF (V_RCOUNT > 0) THEN
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'Y'
               WHERE SELLCHARGEID = J.SELLCHARGEID;
            ELSE
              UPDATE TEMP_CHARGES
                 SET SELECTED_FLAG = 'N'
               WHERE SELLCHARGEID = J.SELLCHARGEID
                 AND SELECTED_FLAG IS NULL;
            END IF;
          END IF;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'VIEW' THEN
      FOR I IN (SELECT SELLCHARGEID,
                       ZONE,
                       SEL_BUY_FLAG,
                       COST_INCURREDAT,
                       SELECTED_FLAG,
                       CHARGESLAB
                  FROM TEMP_CHARGES
                 WHERE SELECTED_FLAG = 'Y') LOOP
        IF (I.SEL_BUY_FLAG = 'BC') THEN
          IF (I.COST_INCURREDAT) = 'Pickup' THEN
            BEGIN
              SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                              MARGIN_TYPE,
                              MARGIN,
                              DISCOUNT_TYPE,
                              DISCOUNT,MARGIN_TEST_FLAG --kiran.v
                INTO V_MARGIN_DISCOUNT_FLAG,
                     V_MARGIN_TYPE,
                     V_MARGIN,
                     V_DISCOUNT_TYPE,
                     V_DISCOUNT,V_Margin_Test

                FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
               WHERE QR.QUOTE_ID = QM.ID
                 AND QR.QUOTE_ID = V_QUOTE_ID
                 AND QR.BREAK_POINT = I.CHARGESLAB
                 AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                 AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                 AND QR.CHARGE_AT = I.COST_INCURREDAT
                 AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~'); --GOVIND;
            EXCEPTION
              --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
              WHEN NO_DATA_FOUND THEN
                V_MARGIN_DISCOUNT_FLAG := 'M';
                V_MARGIN_TYPE          := 'P';
                V_DISCOUNT_TYPE        := 'P';
                V_MARGIN               := V_MARGIN;
                V_DISCOUNT             := V_DISCOUNT;
                V_Margin_Test          :='N';-- KIRAN.V
            END;
            --@@WPBN issue-149317
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          ELSE
            BEGIN
              SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                     MARGIN_TYPE,
                     MARGIN,
                     DISCOUNT_TYPE,
                     DISCOUNT,MARGIN_TEST_FLAG
                INTO V_MARGIN_DISCOUNT_FLAG,
                     V_MARGIN_TYPE,
                     V_MARGIN,
                     V_DISCOUNT_TYPE,
                     V_DISCOUNT,V_Margin_Test --KIRAN.V
                FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
               WHERE QR.QUOTE_ID = QM.ID
                 AND QR.QUOTE_ID = V_QUOTE_ID
                 AND QR.BREAK_POINT = I.CHARGESLAB
                 AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                 AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                 AND QR.CHARGE_AT = I.COST_INCURREDAT
                 AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
            EXCEPTION
              --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
              WHEN NO_DATA_FOUND THEN
                V_MARGIN_DISCOUNT_FLAG := 'M';
                V_MARGIN_TYPE          := 'P';
                V_DISCOUNT_TYPE        := 'P';
                V_MARGIN               := V_MARGIN;
                V_DISCOUNT             := V_DISCOUNT;
                V_Margin_Test          :='N';
            END;
            --@@WPBN issue-149317
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
        ELSE
          IF (I.COST_INCURREDAT) = 'Pickup' THEN
            BEGIN
              SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                              MARGIN_TYPE,
                              MARGIN,
                              DISCOUNT_TYPE,
                              DISCOUNT,MARGIN_TEST_FLAG --KIRAN.V
                INTO V_MARGIN_DISCOUNT_FLAG,
                     V_MARGIN_TYPE,
                     V_MARGIN,
                     V_DISCOUNT_TYPE,
                     V_DISCOUNT,V_Margin_Test
                FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
               WHERE QR.QUOTE_ID = QM.ID
                 AND QR.QUOTE_ID = V_QUOTE_ID
                 AND QR.BREAK_POINT = I.CHARGESLAB
                 AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                 AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                 AND QR.CHARGE_AT = I.COST_INCURREDAT
                 AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;
            EXCEPTION
              --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
              WHEN NO_DATA_FOUND THEN
                V_MARGIN_DISCOUNT_FLAG := 'D';
                V_MARGIN_TYPE          := 'P';
                V_DISCOUNT_TYPE        := 'P';
                V_MARGIN               := V_MARGIN;
                V_DISCOUNT             := V_DISCOUNT;
                V_Margin_Test          :='N';
            END;
            --@@WPBN issue-149317
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          ELSE
            BEGIN
              SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                     MARGIN_TYPE,
                     MARGIN,
                     DISCOUNT_TYPE,
                     DISCOUNT,MARGIN_TEST_FLAG --KIRAN.V
                INTO V_MARGIN_DISCOUNT_FLAG,
                     V_MARGIN_TYPE,
                     V_MARGIN,
                     V_DISCOUNT_TYPE,
                     V_DISCOUNT,V_Margin_Test
                FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
               WHERE QR.QUOTE_ID = QM.ID
                 AND QR.QUOTE_ID = V_QUOTE_ID
                 AND QR.BREAK_POINT = I.CHARGESLAB
                 AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                 AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                 AND QR.CHARGE_AT = I.COST_INCURREDAT
                 AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;
            EXCEPTION
              --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
              WHEN NO_DATA_FOUND THEN
                V_MARGIN_DISCOUNT_FLAG := 'D';
                V_MARGIN_TYPE          := 'P';
                V_DISCOUNT_TYPE        := 'P';
                V_MARGIN               := V_MARGIN;
                V_DISCOUNT             := V_DISCOUNT;
                V_Margin_Test          :='N';
            END;
            --@@WPBN issue-149317
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    IF UPPER(P_OPERATION) = 'ADD' OR UPPER(P_OPERATION) = 'COPY' THEN
      FOR I IN (SELECT SELLCHARGEID,
                       ZONE,
                       SEL_BUY_FLAG,
                       COST_INCURREDAT,
                       SELECTED_FLAG,
                       CHARGESLAB
                  FROM TEMP_CHARGES
                 WHERE SELECTED_FLAG = 'Y') LOOP
        IF (I.SEL_BUY_FLAG = 'BC') THEN
          IF (I.COST_INCURREDAT) = 'Pickup' THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              BEGIN
                SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                                MARGIN_TYPE,
                                MARGIN,
                                DISCOUNT_TYPE,
                                DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                           AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                               P_SHIPPR_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND ((QM.ACTIVE_FLAG = 'I' AND
                               QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                               (QM.ACTIVE_FLAG = 'A'))
                           AND IQR.SELL_BUY_FLAG = 'BC'
                           AND UPPER(CHARGE_AT) = 'PICKUP'
                           AND QM.TERMINAL_ID IN
                               (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'M';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            ELSE
              BEGIN
                SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                                MARGIN_TYPE,
                                MARGIN,
                                DISCOUNT_TYPE,
                                DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test -- KIRAN.V
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                           AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                               P_SHIPPR_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND ((QM.ACTIVE_FLAG = 'I' AND
                               QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                               (QM.ACTIVE_FLAG = 'A'))
                           AND IQR.SELL_BUY_FLAG = 'BC'
                           AND UPPER(CHARGE_AT) = 'PICKUP'
                           AND QM.TERMINAL_ID IN
                               (SELECT PARENT_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                           PARENT_TERMINAL_ID
                                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                                UNION
                                SELECT TERMINALID TERM_ID
                                  FROM FS_FR_TERMINALMASTER
                                 WHERE OPER_ADMIN_FLAG = 'H'
                                UNION
                                SELECT P_TERMINALID TERM_ID
                                  FROM DUAL
                                UNION
                                SELECT CHILD_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                            PARENT_TERMINAL_ID
                                 START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'M';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            END IF;
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          ELSE
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              BEGIN
                SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                       MARGIN_TYPE,
                       MARGIN,
                       DISCOUNT_TYPE,
                       DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                           AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                               P_CONSGNEE_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND IQR.SELL_BUY_FLAG = 'BC'
                           AND UPPER(CHARGE_AT) = 'DELIVERY'
                           AND QM.TERMINAL_ID IN
                               (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND ;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'M';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            ELSE
              BEGIN
                SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                       MARGIN_TYPE,
                       MARGIN,
                       DISCOUNT_TYPE,
                       DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                           AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                               P_CONSGNEE_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND IQR.SELL_BUY_FLAG = 'BC'
                           AND UPPER(CHARGE_AT) = 'DELIVERY'
                           AND QM.TERMINAL_ID IN
                               (SELECT PARENT_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                           PARENT_TERMINAL_ID
                                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                                UNION
                                SELECT TERMINALID TERM_ID
                                  FROM FS_FR_TERMINALMASTER
                                 WHERE OPER_ADMIN_FLAG = 'H'
                                UNION
                                SELECT P_TERMINALID TERM_ID
                                  FROM DUAL
                                UNION
                                SELECT CHILD_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                            PARENT_TERMINAL_ID
                                 START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.BUYRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'M';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            END IF;
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
        ELSE
          IF (I.COST_INCURREDAT) = 'Pickup' THEN
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              BEGIN
                SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                                MARGIN_TYPE,
                                MARGIN,
                                DISCOUNT_TYPE,
                                DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                           AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                               P_SHIPPR_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND ((QM.ACTIVE_FLAG = 'I' AND
                               QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                               (QM.ACTIVE_FLAG = 'A'))
                           AND IQR.SELL_BUY_FLAG = 'SC'
                           AND UPPER(CHARGE_AT) = 'PICKUP'
                           AND QM.TERMINAL_ID IN
                               (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'D';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            ELSE
              BEGIN
                SELECT DISTINCT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                                MARGIN_TYPE,
                                MARGIN,
                                DISCOUNT_TYPE,
                                DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.ORIGIN_LOCATION = P_QUOTE_ORIGIN
                           AND QM.SHIPPERZONES IN V_SHZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                               P_SHIPPR_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND ((QM.ACTIVE_FLAG = 'I' AND
                               QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                               (QM.ACTIVE_FLAG = 'A'))
                           AND IQR.SELL_BUY_FLAG = 'SC'
                           AND UPPER(CHARGE_AT) = 'PICKUP'
                           AND QM.TERMINAL_ID IN
                               (SELECT PARENT_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                           PARENT_TERMINAL_ID
                                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                                UNION
                                SELECT TERMINALID TERM_ID
                                  FROM FS_FR_TERMINALMASTER
                                 WHERE OPER_ADMIN_FLAG = 'H'
                                UNION
                                SELECT P_TERMINALID TERM_ID
                                  FROM DUAL
                                UNION
                                SELECT CHILD_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                            PARENT_TERMINAL_ID
                                 START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.SHIPPERZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'D';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            END IF;
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          ELSE
            IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
              BEGIN
                SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                       MARGIN_TYPE,
                       MARGIN,
                       DISCOUNT_TYPE,
                       DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test --KIRAN.V
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                           AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                               P_CONSGNEE_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND IQR.SELL_BUY_FLAG = 'SC'
                           AND UPPER(CHARGE_AT) = 'DELIVERY'
                           AND QM.TERMINAL_ID IN
                               (SELECT TERMINALID FROM FS_FR_TERMINALMASTER))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'D';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            ELSE
              BEGIN
                SELECT NVL(MARGIN_DISCOUNT_FLAG, 'M'),
                       MARGIN_TYPE,
                       MARGIN,
                       DISCOUNT_TYPE,
                       DISCOUNT,MARGIN_TEST_FLAG
                  INTO V_MARGIN_DISCOUNT_FLAG,
                       V_MARGIN_TYPE,
                       V_MARGIN,
                       V_DISCOUNT_TYPE,
                       V_DISCOUNT,V_Margin_Test --KIRAN.V
                  FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                 WHERE QR.QUOTE_ID = QM.ID
                   AND QR.QUOTE_ID =
                       (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.DEST_LOCATION = P_QUOTE_DESTINATION
                           AND QM.CONSIGNEEZONES IN V_CNZONE--GOVIND
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.CONSIGNEE_MODE = P_CONSIGNEE_MODE
                           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                               P_CONSGNEE_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND IQR.SELL_BUY_FLAG = 'SC'
                           AND UPPER(CHARGE_AT) = 'DELIVERY'
                           AND QM.TERMINAL_ID IN
                               (SELECT PARENT_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                           PARENT_TERMINAL_ID
                                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                                UNION
                                SELECT TERMINALID TERM_ID
                                  FROM FS_FR_TERMINALMASTER
                                 WHERE OPER_ADMIN_FLAG = 'H'
                                UNION
                                SELECT P_TERMINALID TERM_ID
                                  FROM DUAL
                                UNION
                                SELECT CHILD_TERMINAL_ID TERM_ID
                                  FROM FS_FR_TERMINAL_REGN
                                CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                            PARENT_TERMINAL_ID
                                 START WITH PARENT_TERMINAL_ID = P_TERMINALID))
                   AND QR.BREAK_POINT = I.CHARGESLAB
                   AND QR.SELLRATE_ID = TO_NUMBER(I.SELLCHARGEID)
                   AND INSTR(QM.CONSIGNEEZONES,I.ZONE)!=0
                   AND QR.CHARGE_AT = I.COST_INCURREDAT
                   AND NVL(QR.ZONE_CODE,'~') = NVL(I.ZONE,'~') ;--GOVIND;
              EXCEPTION
                --@@Added by Kameswari for the WPBN issue-149317 on 11/12/08
                WHEN NO_DATA_FOUND THEN
                  V_MARGIN_DISCOUNT_FLAG := 'D';
                  V_MARGIN_TYPE          := 'P';
                  V_DISCOUNT_TYPE        := 'P';
                  V_MARGIN               := V_MARGIN;
                  V_DISCOUNT             := V_DISCOUNT;
                  V_Margin_Test          :='N';
              END;
              --@@WPBN issue-149317
            END IF;
            UPDATE TEMP_CHARGES
               SET MARGIN_TYPE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN_TYPE,
                                                 V_DISCOUNT_TYPE),
                   MARGINVALUE          = DECODE(V_MARGIN_DISCOUNT_FLAG,
                                                 'M',
                                                 V_MARGIN,
                                                 V_DISCOUNT),
                   MARGIN_DISCOUNT_FLAG = V_MARGIN_DISCOUNT_FLAG,
                   MARGIN_TEST_FLAG   = V_Margin_Test
             WHERE CHARGESLAB = I.CHARGESLAB
               AND SELLCHARGEID = I.SELLCHARGEID;
          END IF;
        END IF;
      END LOOP;
      COMMIT;
    END IF;
    --@@Added by Kameswari for the WPBN issue - 166729 on 08/04/09
    DELETE FROM TEMP_CHARGES
     WHERE CHARGESLAB = 'MAX'
       AND BUYRATE IN ('0', '0.0');
   COMMIT;
    --@@WPBN issue - 166729 on 08/04/09
    COMMIT;
    IF UPPER(P_PERMISSION) = 'Y' THEN
      OPEN P_RS FOR
        SELECT *
          FROM TEMP_CHARGES
         ORDER BY COST_INCURREDAT, ZONE, SELLCHARGEID, LINE_NO;
      OPEN P_RS3 FOR
        --SELECT CHARGESLAB,CHARGEBASIS
        SELECT CHARGESLAB,CHARGEBASIS,RATE_INDICATOR,rate_type,Weight_Break  	--@@Modified by Kameswari for the WPBN issue - 265671 on 25/08/2011
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS, T.RATE_INDICATOR,T.rate_type,T.Weight_Break
                   FROM TEMP_CHARGES T
                  WHERE IS_NUMBER(CHARGESLAB) = 'true' --NOT IN ('MIN', 'MAX', 'FLAT') commented by govind for getting problem in order by to_number(chargeslab)
                    AND UPPER(COST_INCURREDAT) = 'PICKUP'
                    AND UPPER(WEIGHT_BREAK) <> 'LIST') ORDER BY
                TO_NUMBER(CHARGESLAB))
        UNION ALL
        SELECT CHARGESLAB,CHARGEBASIS,RATE_INDICATOR,rate_type,Weight_Break	--@@Modified by Kameswari for the WPBN issue - 265671 on 25/08/2011
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS, T.RATE_INDICATOR,T.rate_type,T.Weight_Break
                   FROM TEMP_CHARGES T
                  WHERE UPPER(COST_INCURREDAT) = 'PICKUP'
                    AND UPPER(WEIGHT_BREAK) = 'LIST') ORDER BY CHARGESLAB);
      OPEN P_RS4 FOR
        SELECT CHARGESLAB,CHARGEBASIS,RATE_INDICATOR,rate_type,Weight_Break	--@@Modified by Kameswari for the WPBN issue - 265671 on 25/08/2011
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS, T.RATE_INDICATOR,T.rate_type,T.Weight_Break
                   FROM TEMP_CHARGES T
                  WHERE IS_NUMBER(CHARGESLAB) = 'true' --NOT IN ('MIN', 'MAX', 'FLAT') commented by govind for getting problem in order by to_number(chargeslab)NOT IN ('MIN', 'MAX', 'FLAT')
                    AND UPPER(COST_INCURREDAT) = 'DELIVERY'
                    AND UPPER(WEIGHT_BREAK) <> 'LIST') ORDER BY
                TO_NUMBER(CHARGESLAB))
        UNION ALL
        SELECT CHARGESLAB,CHARGEBASIS,RATE_INDICATOR,rate_type,Weight_Break
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS, T.RATE_INDICATOR,T.rate_type,T.Weight_Break	--@@Modified by Kameswari for the WPBN issue - 265671 on 25/08/2011
                   FROM TEMP_CHARGES T
                  WHERE UPPER(COST_INCURREDAT) = 'DELIVERY'
                    AND UPPER(WEIGHT_BREAK) = 'LIST') ORDER BY CHARGESLAB);
    ELSE
      OPEN P_RS FOR
        SELECT *
          FROM TEMP_CHARGES
         WHERE SEL_BUY_FLAG NOT IN ('BC')
         ORDER BY COST_INCURREDAT, ZONE, SELLCHARGEID, LINE_NO;
      OPEN P_RS3 FOR
        SELECT CHARGESLAB,CHARGEBASIS
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS
                   FROM TEMP_CHARGES
                  WHERE IS_NUMBER(CHARGESLAB) = 'true' --NOT IN ('MIN', 'MAX', 'FLAT') commented by govind for getting problem in order by to_number(chargeslab)NOT IN ('MIN', 'MAX', 'FLAT')
                    AND UPPER(COST_INCURREDAT) = 'PICKUP'
                    AND UPPER(WEIGHT_BREAK) <> 'LIST'
                    AND SEL_BUY_FLAG <> 'BC') ORDER BY TO_NUMBER(CHARGESLAB))
        UNION ALL
        SELECT CHARGESLAB,CHARGEBASIS
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS
                   FROM TEMP_CHARGES
                  WHERE UPPER(COST_INCURREDAT) = 'PICKUP'
                    AND UPPER(WEIGHT_BREAK) = 'LIST'
                    AND SEL_BUY_FLAG <> 'BC') ORDER BY CHARGESLAB);
      OPEN P_RS4 FOR
        SELECT CHARGESLAB,CHARGEBASIS
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS
                   FROM TEMP_CHARGES
                  WHERE IS_NUMBER(CHARGESLAB) = 'true' --NOT IN ('MIN', 'MAX', 'FLAT') commented by govind for getting problem in order by to_number(chargeslab)NOT IN ('MIN', 'MAX', 'FLAT')
                    AND UPPER(COST_INCURREDAT) = 'DELIVERY'
                    AND UPPER(WEIGHT_BREAK) <> 'LIST'
                    AND SEL_BUY_FLAG <> 'BC') ORDER BY TO_NUMBER(CHARGESLAB))
        UNION ALL
        SELECT CHARGESLAB,CHARGEBASIS
          FROM ((SELECT DISTINCT CHARGESLAB,CHARGEBASIS
                   FROM TEMP_CHARGES
                  WHERE UPPER(COST_INCURREDAT) = 'DELIVERY'
                    AND UPPER(WEIGHT_BREAK) = 'LIST'
                    AND SEL_BUY_FLAG <> 'BC') ORDER BY CHARGESLAB);
    END IF;
    IF V_ORIGIN_COUNTRY_ID = 'CA' AND V_DEST_COUNTRY_ID = 'CA' THEN
      V_SQL1 := 'Select d.Zone,
		NULL Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Pickup'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Location_Id = :v_Origin
	    AND m.Shipment_Mode = :v_Shipper_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Shipper_Console';
      V_SQL3 := 'Select d.Zone,
		NULL Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Delivery'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Location_id = :v_Destination
	    AND m.Shipment_Mode = :v_Consignee_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Consignee_Console';
    ELSIF V_ORIGIN_COUNTRY_ID = 'CA' THEN
      V_SQL1 := 'Select d.Zone,
		NULL Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Pickup'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Location_id = :v_Origin
	    AND m.Shipment_Mode = :v_Shipper_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Shipper_Console';
      V_SQL3 := 'Select d.Zone,
		d.Alphanumeric Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Delivery'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Origin_Location = :v_Destination
	    AND m.Shipment_Mode = :v_Consignee_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Consignee_Console';
    ELSIF V_DEST_COUNTRY_ID = 'CA' THEN
      V_SQL1 := 'Select d.Zone,
		d.Alphanumeric Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Pickup'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Origin_Location = :v_Origin
	    AND m.Shipment_Mode = :v_Shipper_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Shipper_Console';
      V_SQL3 := 'Select d.Zone,
		NULL Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Delivery'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Location_id = :v_Destination
	    AND m.Shipment_Mode = :v_Consignee_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Consignee_Console';
    ELSE
      V_SQL1 := 'Select d.Zone,
		d.Alphanumeric Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Pickup'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Origin_Location = :v_Origin
	    AND m.Shipment_Mode = :v_Shipper_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Shipper_Console';
      V_SQL3 := 'Select d.Zone,
		d.Alphanumeric Alphanumeric,
		d.To_Zipcode,
		d.From_Zipcode,
		''Delivery'' Charge_Type
	   FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	  WHERE d.Zone_Code = m.Zone_Code
	    AND m.Origin_Location = :v_Destination
	    AND m.Shipment_Mode = :v_Consignee_Mode
	    AND NVL(m.Console_Type, ''~'') = :v_Consignee_Console';
    END IF;
    V_SQL2 := V_MSZONE;
    V_SQL4 := V_MCZONE;
    IF V_SZONE IS NOT NULL AND V_CZONE IS NOT NULL THEN
      OPEN P_RS2 FOR V_SQL1 || V_SQL2 || ' Union ' || V_SQL3 || V_SQL4 || ' Order By Zone, Alphanumeric, From_Zipcode'
        USING P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE, P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE;
    ELSIF (V_SZONE IS NULL OR
          (V_SZONE IS NOT NULL AND LENGTH(TRIM(V_SZONE)) = 0)) THEN
      IF V_DEST_COUNTRY_ID = 'CA' THEN
        OPEN P_RS2 FOR 'Select d.Zone,
		  NULL Alphanumeric,
		  d.To_Zipcode,
		  d.From_Zipcode,
		  ''Delivery'' Charge_Type
	     FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	    WHERE d.Zone_Code = m.Zone_Code
	      AND m.Location_Id = :p_Quote_Destination
	      AND m.Shipment_Mode = :p_consignee_mode
	      AND NVL(m.Console_Type,''~'') = :p_consgnee_cnsl_type
	      AND d.ZONE IN (' || QMS_RSR_RATES_PKG.SEPERATOR(P_CONSIGNZONES) || ') ORDER BY ZONE, Alphanumeric, From_Zipcode'
          USING P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE;
        /*(Select Column_Value From Table(Inlist(p_Consignzones)) Where rownum>=0)*/
      ELSE
        OPEN P_RS2 FOR 'Select d.Zone,
		 d.Alphanumeric Alphanumeric,
		 d.To_Zipcode,
		 d.From_Zipcode,
		 ''Delivery'' Charge_Type
	    FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	   WHERE d.Zone_Code = m.Zone_Code
	     AND m.Origin_Location = :p_Quote_Destination
	     AND m.Shipment_Mode = :p_consignee_mode
	      AND NVL(m.Console_Type,''~'') = :p_consgnee_cnsl_type
	     AND d.ZONE IN (' || QMS_RSR_RATES_PKG.SEPERATOR(P_CONSIGNZONES) || ') ORDER BY ZONE, Alphanumeric, From_Zipcode'
          USING P_QUOTE_DESTINATION, P_CONSIGNEE_MODE, P_CONSGNEE_CNSL_TYPE;
        /*(Select Column_Value From Table(Inlist(p_Consignzones)) Where rownum>=0)*/
      END IF;
      /*OPEN p_rs2
      FOR v_sql3 || v_sql4 || ' ORDER BY zone,ALPHANUMERIC,FROM_ZIPCODE'
      Using p_quote_destination;*/
    ELSE
      IF V_ORIGIN_COUNTRY_ID = 'CA' THEN
        OPEN P_RS2 FOR 'Select d.Zone,
		   NULL Alphanumeric,
		   d.To_Zipcode,
		   d.From_Zipcode,
		   ''Pickup'' Charge_Type
	      FROM QMS_ZONE_CODE_MASTER_CA m, QMS_ZONE_CODE_DTL_CA d
	     WHERE d.Zone_Code = m.Zone_Code
	       AND m.Location_Id = :p_Quote_Origin
	       AND m.Shipment_Mode = :p_shipper_mode
	      AND NVL(m.Console_Type,''~'')=:p_shippr_cnsl_type
	       AND d.ZONE IN (' || QMS_RSR_RATES_PKG.SEPERATOR(P_SHZONES) || ')
	     ORDER BY ZONE, Alphanumeric, From_Zipcode'
          USING P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE;
        /*(Select Column_Value From Table(Inlist(p_Shzones)) Where rownum>=0)*/
      ELSE
        OPEN P_RS2 FOR 'Select d.Zone,
		 d.Alphanumeric Alphanumeric,
		 d.To_Zipcode,
		 d.From_Zipcode,
		 ''Pickup'' Charge_Type
	    FROM QMS_ZONE_CODE_MASTER m, QMS_ZONE_CODE_DTL d
	   WHERE d.Zone_Code = m.Zone_Code
	     AND m.Origin_Location = :p_Quote_Origin
	     AND m.Shipment_Mode = :p_shipper_mode
	      AND NVL(m.Console_Type,''~'')=:p_shippr_cnsl_type
	     AND d.ZONE IN(' || QMS_RSR_RATES_PKG.SEPERATOR(P_SHZONES) || ') ORDER BY ZONE, Alphanumeric, From_Zipcode'
          USING P_QUOTE_ORIGIN, P_SHIPPER_MODE, P_SHIPPR_CNSL_TYPE;
        /*(Select Column_Value From Table(Inlist(p_Shzones)) Where rownum>=0)*/
      END IF;
      /*OPEN p_rs2
      FOR v_sql1 || v_sql2 || ' ORDER BY zone,ALPHANUMERIC,FROM_ZIPCODE'
      Using p_quote_origin;*/
    END IF;
  END;

  /*
  This procedure returns 4 Resultset Objects containing the rate details (Charges,Freight,Cartages) and notes for a QuoteId.
  This is used for Costing and Quote View (Both Detailed and Charges Details View)
  The IN Parameter is:
     p_quoteid         NUMBER

  The OUT Parameters are:
     p_rs        OUT   resultset,
     p_rs1       OUT   resultset,
     p_rs2       OUT   resultset,
     p_rs3       OUT   resultset
  */
  PROCEDURE QUOTE_VIEW_PROC(P_QUOTEID VARCHAR2,
                            P_LANE_NO VARCHAR2,
                            --modified by subrahmanyam for the wpbn id:146971
                            P_RS  OUT RESULTSET,
                            P_RS1 OUT RESULTSET,
                            P_RS2 OUT RESULTSET,
                            P_RS3 OUT RESULTSET) AS
    V_ID NUMBER;
    V_IS_MULTI_QUOTE VARCHAR2(100);
    -------------------For Ordering the Charges---------------
    V_LANE_NO     NUMBER(3) := 1;
    V_COUNT       NUMBER(3);
    V_COUNT1      NUMBER(3);
    V_WEIGHTBREAK VARCHAR2(100) := '';
    V_SHMODE      VARCHAR2(100) := '';
    -----------------------------------------------------------

  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    SELECT ID,IS_MULTI_QUOTE
      INTO V_ID,V_IS_MULTI_QUOTE
      FROM QMS_QUOTE_MASTER
     WHERE QUOTE_ID = P_QUOTEID
       AND MULTI_QUOTE_LANE_NO = P_LANE_NO --Added by Mohan
       AND VERSION_NO =
           (SELECT MAX(VERSION_NO)
              FROM QMS_QUOTE_MASTER
             WHERE QUOTE_ID = P_QUOTEID
               AND MULTI_QUOTE_LANE_NO = P_LANE_NO); --Added by Mohan
    FOR I IN (SELECT DISTINCT QUOTE_ID,
                              SELL_BUY_FLAG,
                              BUYRATE_ID,
                              SELLRATE_ID,
                              RATE_LANE_NO,
                              VERSION_NO,
                              --@@Added for the WPBN issues-146448,146968 on 19/12/08
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
                              --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                              CARRIER,
                              FREQUENCY_CHECKED,
                              CARRIER_CHECKED,
                              TRANSIT_CHECKED,
                              VALIDITY_CHECKED,
                              zone_code, -- Added by Kishore FOr Quote View PDF Duplicates
                              FREQUENCY
                FROM QMS_QUOTE_RATES
               WHERE QUOTE_ID = V_ID
                 AND MULTI_QUOTE_LANE_NO = P_LANE_NO) --Added by Mohan
     LOOP

      IF UPPER(I.SELL_BUY_FLAG) = 'BR' THEN
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           CHARGESLAB,
           CURRENCY,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           CHARGEBASIS,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           VERSION_NO,
           --@@Added for the WPBN issues-146448,146968 on 19/12/08
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           WEIGHT_SCALE,
           RATE_TYPE,
           FREQUENCY,
           TRANSITTIME,
           NOTES,
           PRIMARY_BASIS,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           LBOUND,
           UBOUND,
           RATE_INDICATOR,
           --@@Added by Kameswari for the WPBN issue-146448
           CARRIER,
           FREQUENCY_CHECKED,
           CARRIER_CHECKED,
           TRANSITTIME_CHECKED,
           RATEVALIDITY_CHECKED,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           MARGIN_TEST_FLAG,
           RATE_DESCRIPTION,
           CONSOLE_TYPE,
           SELECTED_FLAG)
          SELECT I.CHARGE_AT,
                 QBD.WEIGHT_BREAK_SLAB,
                 /*QBM.CURRENCY*/
               /*  DECODE(QBD.RATE_DESCRIPTION,
                        'A FREIGHT RATE',
                        QBM.CURRENCY,
                        NVL(DECODE(QBD.SUR_CHARGE_CURRENCY,'-',QBM.CURRENCY),QBM.CURRENCY)) CURRENCY,*/
                 DECODE(QBD.SUR_CHARGE_CURRENCY,NULL,QBM.CURRENCY,QBD.SUR_CHARGE_CURRENCY) CURRENCY,
                 QBD.CHARGERATE,
                 0,
                 DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN_TYPE),
                 DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                 'Per ' || QBM.UOM,
                 I.SELL_BUY_FLAG,
                 QBM.BUYRATEID,
                 QBM.VERSION_NO,
                 --@@Added for the WPBN issues-146448,146968 on 19/12/08
                 QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                 (SELECT DISTINCT BD.VALID_UPTO
                    FROM QMS_BUYRATES_DTL BD
                   WHERE BD.BUYRATEID = I.BUYRATE_ID
                     AND BD.LANE_NO = I.RATE_LANE_NO
                     AND BD.VERSION_NO =
                         (SELECT MAX(BD1.VERSION_NO)
                            FROM QMS_BUYRATES_DTL BD1
                           WHERE BD1.BUYRATEID = BD.BUYRATEID
                             AND BD1.LANE_NO = BD.LANE_NO)),
                 QBM.TERMINALID,
                 QBM.WEIGHT_BREAK,
                 QBM.WEIGHT_CLASS,
                 QBM.RATE_TYPE,
                 INITCAP(QBD.FREQUENCY),
                 QBD.TRANSIT_TIME,
                 QBD.NOTES,
                 QBM.UOM,
                 FRL.ORIG_LOC,
                 FRL.DEST_LOC,
                 QBM.SHIPMENT_MODE,
                 QBD.SERVICE_LEVEL,
                 FRL.SERIAL_NO,
                 QBD.DENSITY_CODE,
                 QBD.LOWERBOUND,
                 QBD.UPPERBOUND,
                 QBD.CHARGERATE_INDICATOR,
                 --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                 (SELECT CARRIERNAME
                    FROM FS_FR_CAMASTER
                   WHERE CARRIERID = QBD.CARRIER_ID),
                 --@@Modified by kameswari on 18/02/09
                 I.FREQUENCY_CHECKED,
                 I.CARRIER_CHECKED,
                 I.TRANSIT_CHECKED,
                 I.VALIDITY_CHECKED,
                 I.MARGIN_DISCOUNT_FLAG,
                 QBD.LANE_NO,
                 QBD.LINE_NO,
                 I.MARGIN_TEST_FLAG,
                 DECODE(QBD.RATE_DESCRIPTION,
                        '',
                        'A FREIGHT RATE',
                        QBD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                 QBM.CONSOLE_TYPE,
                 'Y' --@@Added by Kameswari for the WPBN issue-112348
            FROM QMS_BUYRATES_MASTER QBM,
                 QMS_BUYRATES_DTL    QBD,
                 FS_RT_LEG           FRL,
                 FS_RT_PLAN          FRP
           WHERE QBD.BUYRATEID = QBM.BUYRATEID
             AND QBD.VERSION_NO = QBM.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
             AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
             AND QBD.BUYRATEID = I.BUYRATE_ID
             AND QBD.LANE_NO = I.RATE_LANE_NO
             AND QBD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
             AND QBD.WEIGHT_BREAK_SLAB = I.BREAK_POINT
             AND QBD.RATE_DESCRIPTION = DECODE(I.CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',I.CHARGE_DESCRIPTION)
             AND FRL.SERIAL_NO = I.SERIAL_NO
             AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
             AND (QBD.CHARGERATE NOT IN ('0.0') OR
                 QBD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
             AND FRP.QUOTE_ID = P_QUOTEID
             AND qbd.rate_description = DECODE(I.CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',I.CHARGE_DESCRIPTION);--kishore for quote view(update) duplicates breaks
      ELSIF (UPPER(I.SELL_BUY_FLAG) = 'RSR' OR
            UPPER(I.SELL_BUY_FLAG) = 'CSR') THEN
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           CHARGESLAB,
           CURRENCY,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           CHARGEBASIS,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           VERSION_NO,
           --@@Added for the WPBN issues-146448,146968 on 19/12/08
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           WEIGHT_SCALE,
           RATE_TYPE,
           FREQUENCY,
           TRANSITTIME,
           NOTES,
           PRIMARY_BASIS,
           SELLCHARGEID,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           LBOUND,
           UBOUND,
           RATE_INDICATOR,
           --@@Added by Kameswari for the WPBN issue-146448
           CARRIER,
           FREQUENCY_CHECKED,
           CARRIER_CHECKED,
           TRANSITTIME_CHECKED,
           RATEVALIDITY_CHECKED,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           MARGIN_TEST_FLAG,
           RATE_DESCRIPTION,
           CONSOLE_TYPE,
           SELECTED_FLAG)
          SELECT I.CHARGE_AT,
                 RSD.WEIGHTBREAKSLAB,
                 /*RSM.CURRENCY,*/
                /* DECODE(QBD.RATE_DESCRIPTION,
                        'A FREIGHT RATE',
                        RSM.CURRENCY,
                        NVL(DECODE(QBD.SUR_CHARGE_CURRENCY,'-',RSM.CURRENCY),RSM.CURRENCY) )CURRENCY,*/
                        DECODE(QBD.SUR_CHARGE_CURRENCY,NULL,RSM.CURRENCY,'-',RSM.CURRENCY,QBD.SUR_CHARGE_CURRENCY) CURRENCY,
                 RSD.BUY_RATE_AMT,
                 RSD.CHARGERATE,
                 DECODE(I.MARGIN_DISCOUNT_FLAG,
                        'M',
                        I.MARGIN_TYPE,
                        I.DISCOUNT_TYPE),
                 DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                 'Per ' || QBM.UOM,
                 I.SELL_BUY_FLAG,
                 RSD.BUYRATEID,
                 RSD.VERSION_NO,
                 --@@Added for the WPBN issues-146448,146968 on 19/12/08
                 QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                 (SELECT DISTINCT BD.VALID_UPTO
                    FROM QMS_BUYRATES_DTL BD, QMS_REC_CON_SELLRATESDTL RSD
                   WHERE BD.BUYRATEID = RSD.BUYRATEID
                     AND BD.LANE_NO = RSD.LANE_NO
                     AND BD.BUYRATEID = I.BUYRATE_ID
                     AND BD.LANE_NO = I.RATE_LANE_NO
                     AND RSD.VERSION_NO =
                         (SELECT MAX(VERSION_NO)
                            FROM QMS_REC_CON_SELLRATESDTL
                           WHERE BUYRATEID = I.BUYRATE_ID
                             AND LANE_NO = I.RATE_LANE_NO)
                     AND BD.VERSION_NO = RSD.VERSION_NO),
                 RSM.TERMINALID,
                 RSM.WEIGHT_BREAK,
                 RSM.WEIGHT_CLASS,
                 RSM.RATE_TYPE,
                 INITCAP(RSD.FREQUENCY),
                 RSD.TRANSIT_TIME,
                 RSD.NOTES,
                 QBM.UOM,
                 RSD.REC_CON_ID,
                 FRL.ORIG_LOC,
                 FRL.DEST_LOC,
                 QBM.SHIPMENT_MODE,
                 QBD.SERVICE_LEVEL,
                 FRL.SERIAL_NO,
                 QBD.DENSITY_CODE,
                 RSD.LOWRER_BOUND,
                 RSD.UPPER_BOUND,
                 RSD.CHARGERATE_INDICATOR,
                 --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                 (SELECT CARRIERNAME
                    FROM FS_FR_CAMASTER
                   WHERE CARRIERID = QBD.CARRIER_ID),
                 --@@Modified by kameswari on 18/02/09
                 I.FREQUENCY_CHECKED,
                 I.CARRIER_CHECKED,
                 I.TRANSIT_CHECKED,
                 I.VALIDITY_CHECKED,
                 I.MARGIN_DISCOUNT_FLAG,
                 RSD.LANE_NO,
                 RSD.LINE_NO,
                 I.MARGIN_TEST_FLAG,
                 DECODE(RSD.RATE_DESCRIPTION,
                        '',
                        'A FREIGHT RATE',
                        RSD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                 QBM.CONSOLE_TYPE ,--@@Added by Kameswari for the WPBN issue-112348
                 'Y'
            FROM QMS_REC_CON_SELLRATESMASTER RSM,
                 QMS_REC_CON_SELLRATESDTL    RSD,
                 QMS_BUYRATES_MASTER         QBM,
                 QMS_BUYRATES_DTL            QBD,
                 FS_RT_LEG                   FRL,
                 FS_RT_PLAN                  FRP
           WHERE RSD.REC_CON_ID = RSM.REC_CON_ID
             AND RSD.REC_CON_ID = I.SELLRATE_ID
             AND RSD.LANE_NO = I.RATE_LANE_NO
             AND RSD.BUYRATEID = I.BUYRATE_ID
             AND RSD.BUYRATEID = QBD.BUYRATEID
             AND RSD.LANE_NO = QBD.LANE_NO
             AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
             AND QBD.BUYRATEID = QBM.BUYRATEID
             AND RSD.VERSION_NO = QBD.VERSION_NO
             AND QBD.VERSION_NO = QBM.VERSION_NO
             AND RSD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
             AND RSD.WEIGHTBREAKSLAB = I.BREAK_POINT
             AND RSD.WEIGHTBREAKSLAB = QBD.WEIGHT_BREAK_SLAB --@@Added by Kameswari for Surcharge Enhancements
             AND FRL.SERIAL_NO = I.SERIAL_NO
             AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
             AND (RSD.CHARGERATE NOT IN ('0.0') OR
                 RSD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
                -- And Rsd.Ai_Flag='A'
             AND FRP.QUOTE_ID = P_QUOTEID
         AND rsd.rate_description = DECODE(I.CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',I.CHARGE_DESCRIPTION)
         AND qbd.rate_description=DECODE(I.CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',I.CHARGE_DESCRIPTION);--kishore for quote view(update) duplicates breaks;
      ELSIF UPPER(I.SELL_BUY_FLAG) = 'SBR' AND UPPER(V_IS_MULTI_QUOTE) <> 'Y' THEN
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           CHARGESLAB,
           CURRENCY,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           CHARGEBASIS,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           EFROM,
           VALIDUPTO,
           TERMINALID,
           WEIGHT_BREAK,
           WEIGHT_SCALE,
           RATE_TYPE,
           PRIMARY_BASIS,
           ORG,
           DEST,
           SHMODE,
           SRV_LEVEL,
           LEG_SL_NO,
           DENSITY_RATIO,
           LBOUND,
           UBOUND,
           MARGIN_DISCOUNT_FLAG,
           LANE_NO,
           LINE_NO,
           MARGIN_TEST_FLAG,
           RATE_DESCRIPTION,
           SELECTED_FLAG)
          SELECT I.CHARGE_AT,
                 QSB.WEIGHT_BREAK_SLAB,
                 QSB.CURRENCYID,
                 QSB.CHARGE_RATE,
                 0.0,
                 DECODE(I.MARGIN_DISCOUNT_FLAG,
                        'M',
                        I.MARGIN_TYPE,
                        I.DISCOUNT_TYPE),
                 DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                 'Per ' || QSB.UOM,
                 I.SELL_BUY_FLAG,
                 'X',
                 QM.EFFECTIVE_DATE,
                 QM.VALID_TO,
                 QM.TERMINAL_ID,
                 QSB.WEIGHT_BREAK,
                 'G',
                 QSB.WEIGHT_BREAK,
                 QSB.UOM,
                 FRL.ORIG_LOC,
                 FRL.DEST_LOC,
                 FRL.SHPMNT_MODE,
                 QSB.SERVICELEVEL,
                 FRL.SERIAL_NO,
                 QSB.DENSITY_CODE,
                 QSB.LOWER_BOUND,
                 QSB.UPPER_BOUND,
                 I.MARGIN_DISCOUNT_FLAG,
                 QSB.LANE_NO,
                 QSB.LINE_NO,
                 I.MARGIN_TEST_FLAG,
                 DECODE(QSB.RATE_DESCRIPTION,
                        '',
                        'A FREIGHT RATE',
                        QSB.RATE_DESCRIPTION) RATE_DESCRIPTION,
                        'Y'
            FROM QMS_QUOTE_MASTER    QM,
                 QMS_QUOTE_SPOTRATES QSB,
                 FS_RT_LEG           FRL,
                 FS_RT_PLAN          FRP
           WHERE QSB.QUOTE_ID = QM.ID
             AND QM.ID = I.QUOTE_ID
             AND QSB.WEIGHT_BREAK_SLAB = I.BREAK_POINT
             AND FRL.SERIAL_NO = I.SERIAL_NO
             AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
             AND FRP.QUOTE_ID = P_QUOTEID
             AND (QSB.CHARGE_RATE NOT IN ('0.0') OR
                 QSB.CHARGE_RATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
             AND QSB.LANE_NO = FRL.SERIAL_NO - 1;
      ELSIF UPPER(I.SELL_BUY_FLAG) = 'SBR' AND UPPER(V_IS_MULTI_QUOTE) = 'Y' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             PRIMARY_BASIS,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             SELECTED_FLAG,
             CHECKED_FLAG,
             SELLCHARGEID,
             FREQUENCY_CHECKED,
             TRANSITTIME_CHECKED,
             CARRIER_CHECKED,
             RATEVALIDITY_CHECKED,
             FREQUENCY)
            SELECT I.CHARGE_AT,
                   QSB.WEIGHT_BREAK_SLAB,
                   QSB.CURRENCYID,
                  /* DECODE((SELECT DISTINCT RATE_DESCRIPTION FROM QMS_QUOTE_SPOTRATES QQSR WHERE
                    QUOTE_ID=I.QUOTE_ID AND QQSR.RATE_DESCRIPTION='A FREIGHT RATE'),
                    'A FREIGHT RATE',(SELECT DISTINCT QQSR.CURRENCYID FROM QMS_QUOTE_SPOTRATES QQSR WHERE
                    QUOTE_ID=I.QUOTE_ID AND QQSR.RATE_DESCRIPTION='A FREIGHT RATE')),*/
                   QSB.CHARGE_RATE,
                   0.0,
                   DECODE(I.MARGIN_DISCOUNT_FLAG,
                          'M',
                          I.MARGIN_TYPE,
                          I.DISCOUNT_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                   'Per ' || QSB.UOM,
                   I.SELL_BUY_FLAG,
                   V_ID,
                   QM.EFFECTIVE_DATE,
                   QM.VALID_TO,
                   QM.TERMINAL_ID,
                   QSB.WEIGHT_BREAK,
                   'G',
                   QSB.WEIGHT_BREAK,
                   QSB.UOM,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   FRL.SHPMNT_MODE,
                   QSB.SERVICELEVEL,
                   FRL.SERIAL_NO,
                   QSB.DENSITY_CODE,
                   QSB.LOWER_BOUND,
                   QSB.UPPER_BOUND,
                   I.MARGIN_DISCOUNT_FLAG,
                   QSB.LANE_NO,
                   QSB.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(QSB.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          QSB.RATE_DESCRIPTION) RATE_DESCRIPTION,
                          'Y',
                          'Y',
                          V_ID,
                          'on',
                          'on',
                          'on',
                          'on',
                          I.FREQUENCY
              FROM QMS_QUOTE_MASTER    QM,
                   QMS_QUOTE_SPOTRATES QSB,
                   FS_RT_LEG           FRL,
                   FS_RT_PLAN          FRP
             WHERE QSB.QUOTE_ID = QM.ID
               AND QM.ID = I.QUOTE_ID
               AND QSB.WEIGHT_BREAK_SLAB = I.BREAK_POINT
               AND QSB.RATE_DESCRIPTION = I.CHARGE_DESCRIPTION
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND FRP.QUOTE_ID = P_QUOTEID;
               --AND (QSB.CHARGE_RATE NOT IN ('0.0') OR
                  -- QSB.CHARGE_RATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
               --AND QSB.LANE_NO = FRL.SERIAL_NO - 1;
      ELSIF UPPER(I.SELL_BUY_FLAG) = 'B' THEN
        INSERT INTO TEMP_CHARGES
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
           SELECTED_FLAG)
          SELECT I.CHARGE_ID,
                 I.CHARGE_DESCRIPTION,
                 (SELECT MAX(ID)
                    FROM QMS_CHARGE_GROUPSMASTER
                   WHERE CHARGEGROUP_ID IN
                         (SELECT CHARGEGROUPID
                            FROM QMS_QUOTE_CHARGEGROUPDTL
                           WHERE QUOTE_ID = V_ID)
                     AND CHARGE_ID = I.CHARGE_ID
                     AND CHARGEDESCID = I.CHARGE_DESCRIPTION) ID,
                 --@@Added by Kameswari for the WPBN issue-130538
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
                 /*(Select Remarks
                      From Qms_Chargedescmaster
                         Where Chargeid = i.Charge_Id
                           And Chargedescid = i.Charge_Description
                           And Inactivate = 'N') Int_Name,
                       (Select Ext_Charge_Name
                          From Qms_Chargedescmaster
                         Where Chargeid = i.Charge_Id
                           And Chargedescid = i.Charge_Description
                           And Inactivate = 'N') Ext_Name,*/
                 ---@@Modified by Kameswari for the WPBN issue-87001
                 (SELECT REMARKS
                    FROM QMS_CHARGEDESCMASTER     QCD,
                         QMS_BUYSELLCHARGESMASTER BCM
                   WHERE QCD.CHARGEID = I.CHARGE_ID
                     AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                     AND QCD.TERMINALID = BCM.TERMINALID --commented by govind for not getting charge names when created by two terminals
                     AND BCM.BUYSELLCHARGEID = I.BUYRATE_ID
                     AND QCD.INACTIVATE = 'N') INT_NAME,
                 (SELECT EXT_CHARGE_NAME
                    FROM QMS_CHARGEDESCMASTER     QCD,
                         QMS_BUYSELLCHARGESMASTER BCM
                   WHERE QCD.CHARGEID = I.CHARGE_ID
                     AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                     AND QCD.TERMINALID = BCM.TERMINALID --TERMINALID commented by govind for not getting charge names when created by two terminals
                     AND BCM.BUYSELLCHARGEID = I.BUYRATE_ID
                     AND INACTIVATE = 'N') EXT_NAME,
                 I.MARGIN_TEST_FLAG,
                 'FREIGHT RATE',--I.CHARGE_DESCRIPTION--'FREIGHT RATE' --KISHORE
                 'Y'
            FROM QMS_BUYSELLCHARGESMASTER QCM, QMS_BUYCHARGESDTL QCD
           WHERE QCM.BUYSELLCHARGEID = I.BUYRATE_ID
             AND QCD.CHARGESLAB = I.BREAK_POINT
             AND QCM.BUYSELLCHARGEID = QCD.BUYSELLCHAEGEID;
      ELSIF UPPER(I.SELL_BUY_FLAG) = 'S' THEN
        INSERT INTO TEMP_CHARGES
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
           SELECTED_FLAG)
          SELECT I.SELLRATE_ID,
                 I.CHARGE_ID,
                 I.CHARGE_DESCRIPTION,
                 (SELECT MAX(ID)
                    FROM QMS_CHARGE_GROUPSMASTER
                   WHERE CHARGEGROUP_ID IN
                         (SELECT CHARGEGROUPID
                            FROM QMS_QUOTE_CHARGEGROUPDTL
                           WHERE QUOTE_ID = V_ID)
                     AND CHARGE_ID = I.CHARGE_ID
                     AND CHARGEDESCID = I.CHARGE_DESCRIPTION) ID,
                 --@@Added by Kameswari for the WPBN issue-130538
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
                 /*(Select Remarks
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              From Qms_Chargedescmaster
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               And Inactivate = 'N') Ext_Name,*/
                 (SELECT REMARKS
                    FROM QMS_CHARGEDESCMASTER QCD, QMS_SELLCHARGESMASTER SCM --@@Modified by Kameswari for the WPBN issue-87001
                   WHERE QCD.CHARGEID = I.CHARGE_ID
                     AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                     AND QCD.TERMINALID = SCM.TERMINALID --@@--TERMINALID commented by govind for not getting charge names when created by two terminals
                     AND SCM.SELLCHARGEID = I.SELLRATE_ID
                     AND QCD.INACTIVATE = 'N') INT_NAME,
                 (SELECT EXT_CHARGE_NAME
                    FROM QMS_CHARGEDESCMASTER QCD, QMS_SELLCHARGESMASTER SCM
                   WHERE QCD.CHARGEID = I.CHARGE_ID
                     AND QCD.CHARGEDESCID = I.CHARGE_DESCRIPTION
                     AND QCD.TERMINALID = SCM.TERMINALID  --@@--TERMINALID commented by govind for not getting charge names when created by two terminals
                     AND SCM.SELLCHARGEID = I.SELLRATE_ID
                     AND INACTIVATE = 'N') EXT_NAME,
                 I.MARGIN_TEST_FLAG,
                 'FREIGHT RATE', -- I.CHARGE_DESCRIPTION --'FREIGHT RATE'  KISHORE
                 'Y'
            FROM QMS_SELLCHARGESDTL QSD, QMS_SELLCHARGESMASTER QSM
           WHERE QSM.SELLCHARGEID = I.SELLRATE_ID
             AND QSD.CHARGESLAB = I.BREAK_POINT
             AND QSM.SELLCHARGEID = QSD.SELLCHARGEID;

      ELSIF UPPER(I.SELL_BUY_FLAG) = 'BC' THEN
        IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
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
             INT_CHARGE_NAME,--GOVIND
             SELECTED_FLAG)
            SELECT DISTINCT I.CHARGE_AT,
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
                            'FREIGHT RATE',--I.CHARGE_DESCRIPTION --'FREIGHT RATE'  KISHORE
                            I.CHARGE_DESCRIPTION, -- GOVIND
                            'Y'
              FROM QMS_CARTAGE_BUYDTL         QCD,
                   QMS_CARTAGE_BUYSELLCHARGES QCM,
                   QMS_QUOTE_MASTER           QM
             WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
               AND QCM.CARTAGE_ID = I.BUYRATE_ID
               AND QCD.CHARGESLAB = I.BREAK_POINT
               AND QM.ID = I.QUOTE_ID
               AND QCM.LOCATION_ID = QM.ORIGIN_LOCATION
               --Added by Kishore For Multiple ZoneCodes
               --AND QCD.ZONE_CODE = QM.SHIPPERZONES
               AND INSTR(QM.SHIPPERZONES,QCD.ZONE_CODE) != 0
               AND qcd.activeinactive ='A'
               AND nvl2(i.zone_code,DECODE(INSTR(qcd.zone_code,i.zone_code),1,qcd.zone_code,SUBSTR(qcd.zone_code,0,1)),'~')= NVL(i.zone_code,'~')
               AND QCD.CHARGE_TYPE = 'Pickup'
               AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE
               AND NVL(QCM.CONSOLE_TYPE, '~') =
                   NVL(QM.SHIPPER_CONSOLE_TYPE, '~');
        ELSE
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
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
             INT_CHARGE_NAME,--GOVIND
             SELECTED_FLAG)
            SELECT DISTINCT I.CHARGE_AT,
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
                           'FREIGHT RATE' ,-- I.CHARGE_DESCRIPTION --'FREIGHT RATE'  KISHORE
                           I.CHARGE_DESCRIPTION,-- GOVIND
                           'Y'
              FROM QMS_CARTAGE_BUYDTL         QCD,
                   QMS_CARTAGE_BUYSELLCHARGES QCM,
                   QMS_QUOTE_MASTER           QM
             WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
               AND QCM.CARTAGE_ID = I.BUYRATE_ID
               AND QCD.CHARGESLAB = I.BREAK_POINT
               AND QM.ID = I.QUOTE_ID
               AND QCM.LOCATION_ID = QM.DEST_LOCATION
               --Added by Kishore For Multiple ZoneCodes
               --AND QCD.ZONE_CODE = QM.CONSIGNEEZONES
               AND INSTR(QM.CONSIGNEEZONES,QCD.ZONE_CODE) != 0
              AND nvl2(i.zone_code,DECODE(INSTR(qcd.zone_code,i.zone_code),1,qcd.zone_code,SUBSTR(qcd.zone_code,0,1)),'~') = NVL(i.zone_code,'~')
               AND QCD.CHARGE_TYPE = 'Delivery'
                AND qcd.activeinactive ='A'
               AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE
               AND NVL(QCM.CONSOLE_TYPE, '~') =
                   NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~');
        END IF;
      ELSIF UPPER(I.SELL_BUY_FLAG) = 'SC' THEN
        IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
          INSERT INTO TEMP_CHARGES
            (SELLCHARGEID,
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
             INT_CHARGE_NAME,
             SELECTED_FLAG)
            SELECT DISTINCT I.SELLRATE_ID,
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
                            'FREIGHT RATE' ,--I.CHARGE_DESCRIPTION --'FREIGHT RATE'  KISHORE
                            I.CHARGE_DESCRIPTION,--GOVIND
                            'Y'
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
              --Added by Kishore For Multiple ZoneCodes
               --AND QCD.ZONE_CODE = QM.SHIPPERZONES
               AND INSTR(QM.SHIPPERZONES,QCD.ZONE_CODE) != 0
                AND qcd.activeinactive ='A'
               AND nvl2(i.zone_code,DECODE(INSTR(qcd.zone_code,i.zone_code),1,qcd.zone_code,SUBSTR(qcd.zone_code,0,1)),'~') = NVL(i.zone_code,'~')
               AND QSD.CHARGE_TYPE = 'Pickup'
               AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE
               AND NVL(QCM.CONSOLE_TYPE, '~') =
                   NVL(QM.SHIPPER_CONSOLE_TYPE, '~')
               AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
        ELSE
          INSERT INTO TEMP_CHARGES
            (SELLCHARGEID,
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
             INT_CHARGE_NAME,--GOVIND
             SELECTED_FLAG)
            SELECT DISTINCT I.SELLRATE_ID,
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
                            'FREIGHT RATE' ,--I.CHARGE_DESCRIPTION --'FREIGHT RATE'  KISHORE
                            I.CHARGE_DESCRIPTION,-- GOVIND
                            'Y'
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
              --Modified by Kishore For Multiple ZoneCodes
              --AND QCD.ZONE_CODE = QM.CONSIGNEEZONES
               AND INSTR(QM.CONSIGNEEZONES,QCD.ZONE_CODE) != 0
               AND qcd.activeinactive ='A'
               AND nvl2(i.zone_code,DECODE(INSTR(qcd.zone_code,i.zone_code),1,qcd.zone_code,SUBSTR(qcd.zone_code,0,1)),'~') = NVL(i.zone_code,'~')
               AND QSD.CHARGE_TYPE = 'Delivery'
               AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE
               AND NVL(QCM.CONSOLE_TYPE, '~') =
                   NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~')
               AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
        END IF;
     END IF;

    END LOOP;

    ----------------------------For Ordering Charges--------------------------------
    /*For i In (Select Chargegroupid
                From Qms_Quote_Chargegroupdtl
               Where Quote_Id = v_Id
               Order By Id) Loop
      For j In (Select Charge_Id, Chargedescid, Terminalid, Id
                  From Qms_Charge_Groupsmaster
                 Where Chargegroup_Id = i.Chargegroupid
                   And Inactivate = 'N') Loop
        Update Temp_Charges
           Set Lane_No = v_Lane_No, Id = j.Id
         Where Charge_Id = j.Charge_Id
           And Chargedescid = j.Chargedescid
           And Terminalid = j.Terminalid;

      End Loop;
      v_Lane_No := v_Lane_No + 1;
    End Loop;*/
    FOR I IN (SELECT CHARGEGROUPID
                FROM QMS_QUOTE_CHARGEGROUPDTL
               WHERE QUOTE_ID = V_ID
               ORDER BY ID) LOOP
      FOR J IN (SELECT CHARGE_ID, CHARGEDESCID, TERMINALID
                  FROM QMS_CHARGE_GROUPSMASTER
                 WHERE CHARGEGROUP_ID = I.CHARGEGROUPID) LOOP
        UPDATE TEMP_CHARGES
           SET LANE_NO = V_LANE_NO
         WHERE CHARGE_ID = J.CHARGE_ID
           AND CHARGEDESCID = J.CHARGEDESCID
           AND TERMINALID = J.TERMINALID;
      END LOOP;
      V_LANE_NO := V_LANE_NO + 1;
    END LOOP;
    --@@Added by Kameswari for the WPBN issue - 166729 on 08/04/09
    DELETE FROM TEMP_CHARGES
     WHERE CHARGESLAB = 'MAX'
       AND BUYRATE IN ('0.0', '0');

    COMMIT;
    --@@WPBN issue - 166729 on 08/04/09
    -------------------------------------------------------------------------------------
    OPEN P_RS FOR
      SELECT *
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')
       ORDER BY SEL_BUY_FLAG,
                COST_INCURREDAT,
                SELLCHARGEID,
                LANE_NO,
                RATE_DESCRIPTION,
                LINE_NO;
    /* Open p_Rs1 For
    Select *
      From Temp_Charges
     Where Sel_Buy_Flag In ('B', 'S')
     Order By Lane_No, Id, Buy_Charge_Id, Cost_Incurredat, Line_No;*/
    --@@Commented and Modified by Kameswari for the WPBN issue-130538
    OPEN P_RS1 FOR
      SELECT *
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG IN ('B', 'S')
       ORDER BY LANE_NO, COST_INCURREDAT, ID, LINE_NO;
    OPEN P_RS2 FOR
       --MOdified by kishore for multi zone codes
  SELECT DISTINCT tc.*
         FROM TEMP_CHARGES tc
       WHERE SEL_BUY_FLAG IN ('BC', 'SC')
       ORDER BY BUY_CHARGE_ID, COST_INCURREDAT, INT_CHARGE_NAME,LINE_NO;
    OPEN P_RS3 FOR
      SELECT INTERNAL_NOTES, EXTERNAL_NOTES
        FROM QMS_QUOTE_NOTES
       WHERE QUOTE_ID = V_ID;
  END;

  /*
  This procedure is called from quote_sell_buy_charges_proc.
  It is used for fetching charges (Origin and Destination)
  which might have been used in a previous quote for the same customer between the same trade lane(Tied Customer Info).

  The IN Parameters are:
   p_customerid    VARCHAR2,
   p_origin        VARCHAR2,
   p_destination   VARCHAR2,
   p_terminalid    VARCHAR2,
   p_shmode        VARCHAR2,
   p_flag          VARCHAR2,
   p_operation     VARCHAR2,
   p_quote_id      VARCHAR2

  It Contains no OUT Parameter as the data is stored in a global temporary table TEMP_CHARGES.

  */
  PROCEDURE TIED_CUSTINFO_CHARGES_PROC(P_CUSTOMERID  VARCHAR2,
                                       P_ORIGIN      VARCHAR2,
                                       P_DESTINATION VARCHAR2,
                                       P_TERMINALID  VARCHAR2,
                                       P_SHMODE      VARCHAR2,
                                       P_FLAG        VARCHAR2,
                                       P_OPERATION   VARCHAR2,
                                       P_QUOTE_ID    VARCHAR2,
                                       P_CHARGEGRPS  VARCHAR2
                                       --@@Added by Kameswari for the WPBN issues-80162,80018
                                       ) IS
    V_OPR_ADM_FLAG  VARCHAR2(5);
    V_TERMINALS     VARCHAR2(32000);
    V_SQL1          VARCHAR2(4000);
    V_SQL2          VARCHAR2(4000);
    V_SQL3          VARCHAR2(4000);
    V_SQL4          VARCHAR2(4000);
    V_SQL5          VARCHAR2(4000);
    V_SQL5_1        VARCHAR2(4000);
    V_SQL6          VARCHAR2(4000);
    V_SQL7          VARCHAR2(4000);
    V_SQL8          VARCHAR2(4000);
    V_SQL9          VARCHAR2(4000);
    V_SQL10         VARCHAR2(4000);
    V_SQL11         VARCHAR2(4000);
    V_SQL12         VARCHAR2(4000);
    V_SQL13         VARCHAR2(4000);
    V_SQL14         VARCHAR2(4000);
    V_SQL15         VARCHAR2(4000);
    V_SQL16         VARCHAR2(4000);
    V_SQL17         VARCHAR2(4000);
    V_SQL18         VARCHAR2(4000);
    V_SQL19         VARCHAR2(4000);
    V_SQL20         VARCHAR2(4000);
    V_CURR          SYS_REFCURSOR;
    V_CHARGE_ID     VARCHAR2(1000);
    V_CHARGE_DESC   VARCHAR2(1000);
    V_TERMINAL_ID   VARCHAR2(1000);
    V_SELL_BUY_FLAG VARCHAR2(1000);
    V_CHARGEGROUP   VARCHAR2(32327);
  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    V_CHARGEGROUP := QMS_RSR_RATES_PKG.SEPERATOR(P_CHARGEGRPS);
    IF V_OPR_ADM_FLAG <> 'H' THEN
      OPEN V_CURR FOR
        SELECT DISTINCT /*+ FIRST_ROWS */ QR.CHARGE_ID          CHARGE_ID,
                        QR.CHARGE_DESCRIPTION CHARGE_DESC,
                        QR.SELL_BUY_FLAG      SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_ORIGIN
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.TERMINAL_ID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H'
                UNION ALL
                SELECT P_TERMINALID TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = P_TERMINALID)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('B', 'S')
           AND UPPER(CHARGE_AT) = 'ORIGIN'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_ORIGIN
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                      /*And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                            (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                                                             And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                          (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                   AND UPPER(CHARGE_AT) = 'ORIGIN'
                   AND QR.CHARGE_ID = IQR.CHARGE_ID
                   AND QR.CHARGE_DESCRIPTION = IQR.CHARGE_DESCRIPTION
                   AND QM.TERMINAL_ID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                   PARENT_TERMINAL_ID
                         START WITH CHILD_TERMINAL_ID = P_TERMINALID
                        UNION ALL
                        SELECT TERMINALID TERM_ID
                          FROM FS_FR_TERMINALMASTER
                         WHERE OPER_ADMIN_FLAG = 'H'
                        UNION ALL
                        SELECT P_TERMINALID TERM_ID
                          FROM DUAL
                        UNION ALL
                        SELECT CHILD_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY PRIOR
                                    CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                         START WITH PARENT_TERMINAL_ID = P_TERMINALID));
      /*And Qr.Charge_Description in (select chargedescid from
      qms_charge_groupsmaster where chargegroup_id=p_chargegrps);*/ --@@Modified by Kameswari for the WPBN issues-80162,80018
      /*Union All
      Select Distinct Qr.Charge_Id          Charge_Id,
                      Qr.Charge_Description Charge_Desc,
                      Qr.Sell_Buy_Flag      Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Origin_Location = p_Origin
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipment_Mode = p_Shmode
         And Qm.Terminal_Id In
             (Select Parent_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Child_Terminal_Id = Prior Parent_Terminal_Id
               Start With Child_Terminal_Id = p_Terminalid
              Union All
              Select Terminalid Term_Id
                From Fs_Fr_Terminalmaster
               Where Oper_Admin_Flag = 'H'
              Union All
              Select p_Terminalid Term_Id
                From Dual
              Union All
              Select Child_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
               Start With Parent_Terminal_Id = p_Terminalid)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'B'
         And Upper(Charge_At) = 'ORIGIN'
         And (Qr.Charge_Id, Qr.Charge_Description) Not In
             (Select Sm.Charge_Id, Sm.Chargedescid
                From Qms_Sellchargesmaster Sm, Qms_Sellchargesdtl Sd
               Where Sm.Sellchargeid = Sd.Sellchargeid
                 And Sm.Ie_Flag = 'A'
                 And Sm.Terminalid In
                     (Select Parent_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Child_Terminal_Id = Prior
                                 Parent_Terminal_Id
                       Start With Child_Terminal_Id = p_Terminalid
                      Union All
                      Select Terminalid Term_Id
                        From Fs_Fr_Terminalmaster
                       Where Oper_Admin_Flag = 'H'
                      Union All
                      Select p_Terminalid Term_Id
                        From Dual
                      Union All
                      Select Child_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Prior Child_Terminal_Id =
                                  Parent_Terminal_Id
                       Start With Parent_Terminal_Id = p_Terminalid));*/
      /*AND qm.ie_flag = 'E'
      AND qr.sell_buy_flag ='S'
      AND UPPER (qr.charge_at) = 'ORIGIN'*/
      /*AND qm.created_tstmp =  (SELECT MAX (qm.created_tstmp)
       FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm
      WHERE qm.ID = iqr.quote_id
        AND qm.origin_location =p_origin
        AND qm.customer_id =p_customerid
        AND qm.shipment_mode =p_shmode
        AND qm.ie_flag = 'E'
        AND iqr.sell_buy_flag IN ('B', 'S')
        AND UPPER (charge_at) = 'ORIGIN'
        AND qr.charge_id =iqr.charge_id
        AND QR.CHARGE_DESCRIPTION=iQR.CHARGE_DESCRIPTION);*/
    ELSE
      OPEN V_CURR FOR
        SELECT DISTINCT /*+ FIRST_ROWS */ QR.CHARGE_ID          CHARGE_ID,
                        QR.CHARGE_DESCRIPTION CHARGE_DESC,
                        QR.SELL_BUY_FLAG      SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_ORIGIN
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('B', 'S')
           AND UPPER(QR.CHARGE_AT) = 'ORIGIN'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_ORIGIN
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                      /*And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                             (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                                                     And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                          (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                   AND UPPER(CHARGE_AT) = 'ORIGIN'
                   AND QR.CHARGE_ID = IQR.CHARGE_ID
                   AND QR.CHARGE_DESCRIPTION = IQR.CHARGE_DESCRIPTION);
      /*And Qr.Charge_Description in (select chargedescid
      from qms_charge_groupsmaster where chargegroup_id=p_chargegrps);*/ --@@Modified by Kameswari for the WPBN issues-80162,80018
      /*    And Qr.Charge_Description in (select chargedescid
            from qms_charge_groupsmaster where chargegroup_id in (
                       qms_rsr_rates_pkg.seperator(p_chargegrps) ));--@@Modified by Kameswari for the WPBN issues-80162,80018
      */
      --);
      /*Union All
      Select Distinct Qr.Charge_Id          Charge_Id,
                      Qr.Charge_Description Charge_Desc,
                      Qr.Sell_Buy_Flag      Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Origin_Location = p_Origin
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipment_Mode = p_Shmode
         \*And Qm.Terminal_Id In
             (Select Distinct Terminalid From Fs_Fr_Terminalmaster)*\
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'B'
         And Upper(Qr.Charge_At) = 'ORIGIN'
         And (Qr.Charge_Id, Qr.Charge_Description) Not In
             (Select Sm.Charge_Id, Sm.Chargedescid
                From Qms_Sellchargesmaster Sm, Qms_Sellchargesdtl Sd
               Where Sm.Sellchargeid = Sd.Sellchargeid
                 And Sm.Ie_Flag = 'A'
                 \*And Sm.Terminalid In
                     (Select Distinct Terminalid
                        From Fs_Fr_Terminalmaster)*\
              \*AND qm.ie_flag = 'E'
                                       AND qr.sell_buy_flag ='S'
                                       AND UPPER (qr.charge_at) = 'ORIGIN'*\*/
    END IF;
    LOOP
      FETCH V_CURR
        INTO V_CHARGE_ID, V_CHARGE_DESC, V_SELL_BUY_FLAG;
      EXIT WHEN V_CURR%NOTFOUND;
      /*  v_sql1 := 'INSERT INTO TEMP_CHARGES (SELLCHARGEID, CHARGE_ID, CHARGEDESCID, COST_INCURREDAT, CHARGESLAB, BUYRATE, SELLRATE, MARGIN_TYPE, MARGINVALUE,  SEL_BUY_FLAG, BUY_CHARGE_ID, TERMINALID,CHARGEBASIS,CURRENCY,DENSITY_RATIO,SELECTED_FLAG,MARGIN_DISCOUNT_FLAG,line_no,WEIGHT_BREAK,RATE_TYPE,RATE_INDICATOR,INT_CHARGE_NAME,EXT_CHARGE_NAME) ';
         v_sql2 := ' SELECT distinct decode(upper(qr.sell_buy_flag),''S'',qr.sellrate_id,''B'',qr.buyrate_id) sbid, qr.charge_id, qr.charge_description, qr.charge_at,qr.break_point, qr.buy_rate,';
      */ --@@Commented and Modified by Kameswari for the WPBN issue-130538
      /*     v_sql10 := '  AND qr.charge_id =:v_chargeid AND QR.CHARGE_DESCRIPTION=:v_chargedesc_id'
                      --|| ' And QMC.Chargeid = QR.Charge_Id And Qmc.Chargedescid = Qr.Charge_Description And Inactivate = ''N''';
                       ||
                       ' And (Qr.Charge_Id, Qr.Charge_Description) In (Select Chargeid, Chargedescid From Qms_Chargedescmaster Qmc Where Qr.Charge_Id = Qmc.Chargeid And Qr.Charge_Description = Qmc.Chargedescid And Inactivate = ''N'') ' ||
                       ' And Qr.Charge_Description in (select  chargedescid from
                      qms_charge_groupsmaster qc where qc.chargegroup_id in (' ||
                       qms_rsr_rates_pkg.seperator(p_chargegrps) || '))';
      */
      /*v_sql20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=QR.CHARGE_ID AND CHARGEDESCID=QR.CHARGE_DESCRIPTION AND INACTIVATE=''N'')INT_NAME,
      (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=QR.CHARGE_ID AND CHARGEDESCID=QR.CHARGE_DESCRIPTION AND INACTIVATE=''N'')EXT_NAME ';*/
      /* v_sql1 := 'INSERT INTO TEMP_CHARGES (SELLCHARGEID, ID,CHARGE_ID, CHARGEDESCID, COST_INCURREDAT, CHARGESLAB, BUYRATE, SELLRATE, MARGIN_TYPE, MARGINVALUE,  SEL_BUY_FLAG, BUY_CHARGE_ID, TERMINALID,CHARGEBASIS,CURRENCY,DENSITY_RATIO,SELECTED_FLAG,MARGIN_DISCOUNT_FLAG,line_no,WEIGHT_BREAK,RATE_TYPE,RATE_INDICATOR,INT_CHARGE_NAME,EXT_CHARGE_NAME) ';
      v_sql2 := ' SELECT distinct decode(upper(qr.sell_buy_flag),''S'',qr.sellrate_id,''B'',qr.buyrate_id) sbid, (select max(id)
                   from qms_charge_groupsmaster  where chargegroup_id IN (' ||
                qms_rsr_rates_pkg.seperator(p_chargegrps) ||
                ' ) and charge_id = qr.charge_id  and chargedescid = qr.charge_description)id,qr.charge_id, qr.charge_description, qr.charge_at,qr.break_point, qr.buy_rate,';
      v_sql3 := '';
      v_sql4 := '';
      v_sql5 := 'qr.r_sell_rate sellrate,decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin_type,qr.discount_type), decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin,qr.discount), qr.sell_buy_flag, decode(upper(qr.sell_buy_flag),''S'',qr.buyrate_id,''B'',''''), '; --qm.terminal_id ';

      If v_sell_buy_flag = 'B' Then
        v_sql5_1 := '(SELECT DISTINCT TERMINALID from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id)';
      Else
        v_sql5_1 := '(select DISTINCT TERMINALID from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID)';
      End If;
      v_sql5 := v_sql5 || v_sql5_1;

      v_sql6 := '   FROM QMS_QUOTE_RATES qr, QMS_QUOTE_MASTER qm  WHERE qm.ID = qr.quote_id  ';
      v_sql7 := ' and qm.origin_location =:v_origin';
      v_sql8 := '  AND qm.customer_id =:v_customer_id AND qm.shipment_mode =:v_shmode
                   AND qm.ie_flag = ''E''   AND qr.sell_buy_flag IN (''B'', ''S'') ';
      v_sql9 := '  AND UPPER (charge_at) = ''ORIGIN'' ';

      v_sql10 := ' And (Qr.Charge_Id, Qr.Charge_Description) in (select  qc.charge_id,qc.chargedescid from
                qms_charge_groupsmaster qc where qc.chargedescid=:v_chargedesc_id and qc.charge_id=:v_chargeid and qc.inactivate =''N'' and qc.invalidate=''F'' and qc.chargegroup_id in (' ||
                 qms_rsr_rates_pkg.seperator(p_chargegrps) || '))';
      v_sql11 := '   AND qm.ID =  (SELECT MAX (qm.id)  FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm  WHERE qm.ID = iqr.quote_id';

      v_sql16 := ' and qm.origin_location =:v_origin';
      v_sql17 := '  AND qm.customer_id =:v_customer_id AND qm.shipment_mode =:v_shmode
                  AND qm.ie_flag = ''E''   AND iqr.sell_buy_flag IN (''B'', ''S'') ';
      v_sql18 := '  AND UPPER (charge_at) = ''ORIGIN'' ';
      v_sql19 := '  AND iqr.charge_id =:v_chargeid AND iQR.CHARGE_DESCRIPTION=:v_chargedesc_id';


      --   v_sql 7 and v_sql8  and   v_sql 9 v_sql10
      IF v_sell_buy_flag = 'B' THEN
        v_sql20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER qcd,qms_buysellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.BUYSELLCHARGEID=qr.buyrate_id)INT_NAME,
                     (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd,qms_buysellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.BUYSELLCHARGEID=qr.buyrate_id)EXT_NAME '; --@@Modified by Kameswari for the WPBN issue-87001

        v_sql12 := ' , (select qcbm.BASIS_DESCRIPTION from qms_buysellchargesmaster qbc,Qms_Charge_Basismaster qcbm  where qbc.BUYSELLCHARGEID=qr.buyrate_id And qcbm.CHARGEBASIS = qbc.CHARGEBASIS)CHARGEBASIS';
        v_sql13 := ' ,(select  CURRENCY from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id)';
        v_sql14 := ' ,(select density_code from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),''T'',qr.margin_discount_flag, qr.line_no ' ||
                   ',(SELECT qbc.RATE_BREAK from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),(SELECT qbc.RATE_TYPE from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),
                  (Select Chargerate_Indicator From Qms_Buychargesdtl Where Buysellchaegeid = Qr.Buyrate_Id And Lane_No = Qr.Line_No),';
      ELSE
        v_sql20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER qcd,qms_sellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.SELLCHARGEID=qr.SELLRATE_ID)INT_NAME,
                     (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd,qms_sellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.SELLCHARGEID=qr.SELLRATE_ID)EXT_NAME '; --@@Modified by Kameswari for the WPBN issue-87001

        v_sql12 := ' , (select qcbm.BASIS_DESCRIPTION from qms_sellchargesmaster qbc,Qms_Charge_Basismaster qcbm  where qbc.SELLCHARGEID=qr.SELLRATE_ID And qcbm.CHARGEBASIS = qbc.CHARGEBASIS)CHARGEBASIS';
        v_sql13 := ' ,(select  CURRENCY from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID)';
        v_sql14 := ' ,(select density_code from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),''T'',qr.margin_discount_flag, qr.line_no ' ||
                   ',(SELECT qbc.RATE_BREAK from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID),(SELECT qbc.RATE_TYPE from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID),
               (Select Chargerate_Indicator From Qms_Sellchargesdtl Where SELLCHARGEID = Qr.Sellrate_Id And Lane_No = Qr.Line_No),';
      END IF;*/
      /*EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                              v_sql12 || v_sql13 || v_sql14 || v_sql20 || v_sql6 ||
                              v_sql15 || v_sql7 || v_sql8 || v_sql9 || v_sql10 ||
                              v_sql11 || v_sql16 || v_sql17 || v_sql18 ||
                              v_sql19 || ')')
              Using p_origin, p_customerid, p_shmode, v_charge_id, v_charge_desc, p_origin, p_customerid, p_shmode, v_charge_id, v_charge_desc;
      */
      IF V_SELL_BUY_FLAG = 'B' THEN
        INSERT INTO TEMP_CHARGES
          (SELLCHARGEID,
           ID,
           CHARGE_ID,
           CHARGEDESCID,
           COST_INCURREDAT,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           RATE_INDICATOR,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME)
          SELECT DISTINCT DECODE(UPPER(QR.SELL_BUY_FLAG),
                                 'S',
                                 QR.SELLRATE_ID,
                                 'B',
                                 QR.BUYRATE_ID) SBID,
                          (SELECT MAX(ID)
                             FROM QMS_CHARGE_GROUPSMASTER
                            WHERE CHARGEGROUP_ID IN (V_CHARGEGROUP)
                              AND CHARGE_ID = QR.CHARGE_ID
                              AND CHARGEDESCID = QR.CHARGE_DESCRIPTION) ID,
                          QR.CHARGE_ID,
                          QR.CHARGE_DESCRIPTION,
                          QR.CHARGE_AT,
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(UPPER(QR.SELL_BUY_FLAG),
                                 'S',
                                 QR.BUYRATE_ID,
                                 'B',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          (SELECT QCBM.BASIS_DESCRIPTION
                             FROM QMS_BUYSELLCHARGESMASTER QBC,
                                  QMS_CHARGE_BASISMASTER   QCBM
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID
                              AND QCBM.CHARGEBASIS = QBC.CHARGEBASIS) CHARGEBASIS,
                          (SELECT CURRENCY
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          (SELECT DENSITY_CODE
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT QBC.RATE_BREAK
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          (SELECT QBC.RATE_TYPE
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_BUYCHARGESDTL
                            WHERE BUYSELLCHAEGEID = QR.BUYRATE_ID
                              AND LANE_NO = QR.LINE_NO),
                          (SELECT DISTINCT REMARKS
                             FROM QMS_CHARGEDESCMASTER     QCD,
                                  QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QCD.CHARGEID = QR.CHARGE_ID
                              AND QCD.CHARGEDESCID = QR.CHARGE_DESCRIPTION
                              AND QCD.INACTIVATE = 'N'
                              AND QCD.TERMINALID = QBC.TERMINALID
                              AND QBC.BUYSELLCHARGEID = QR.BUYRATE_ID) INT_NAME,
                          (SELECT DISTINCT EXT_CHARGE_NAME
                             FROM QMS_CHARGEDESCMASTER     QCD,
                                  QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QCD.CHARGEID = QR.CHARGE_ID
                              AND QCD.CHARGEDESCID = QR.CHARGE_DESCRIPTION
                              AND QCD.INACTIVATE = 'N'
                              AND QCD.TERMINALID = QBC.TERMINALID
                              AND QBC.BUYSELLCHARGEID = QR.BUYRATE_ID) EXT_NAME
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.ORIGIN_LOCATION = P_ORIGIN
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.SHIPMENT_MODE = P_SHMODE
             AND QM.IE_FLAG = 'E'
             AND QR.SELL_BUY_FLAG IN ('B', 'S')
             AND UPPER(CHARGE_AT) = 'ORIGIN'
             AND (QR.CHARGE_ID, QR.CHARGE_DESCRIPTION) IN
                 (SELECT QC.CHARGE_ID, QC.CHARGEDESCID
                    FROM QMS_CHARGE_GROUPSMASTER QC
                   WHERE QC.CHARGEDESCID = V_CHARGE_DESC
                     AND QC.CHARGE_ID = V_CHARGE_ID
                     AND QC.INACTIVATE = 'N'
                     AND QC.INVALIDATE = 'F' /*and qc.chargegroup_id in (v_chargegroup)*/
                  )
             AND QM.ID =
                 (SELECT MAX(QM.ID)
                    FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                   WHERE QM.ID = IQR.QUOTE_ID
                     AND QM.ORIGIN_LOCATION = P_ORIGIN
                     AND QM.CUSTOMER_ID = P_CUSTOMERID
                     AND QM.SHIPMENT_MODE = P_SHMODE
                     AND QM.IE_FLAG = 'E'
                     AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                     AND UPPER(CHARGE_AT) = 'ORIGIN'
                     AND IQR.CHARGE_ID = V_CHARGE_ID
                     AND IQR.CHARGE_DESCRIPTION = V_CHARGE_DESC);
      ELSE
        INSERT INTO TEMP_CHARGES
          (SELLCHARGEID,
           ID,
           CHARGE_ID,
           CHARGEDESCID,
           COST_INCURREDAT,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           RATE_INDICATOR,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME)
          SELECT DISTINCT DECODE(UPPER(QR.SELL_BUY_FLAG),
                                 'S',
                                 QR.SELLRATE_ID,
                                 'B',
                                 QR.BUYRATE_ID) SBID,
                          (SELECT MAX(ID)
                             FROM QMS_CHARGE_GROUPSMASTER
                            WHERE CHARGEGROUP_ID IN (V_CHARGEGROUP)
                              AND CHARGE_ID = QR.CHARGE_ID
                              AND CHARGEDESCID = QR.CHARGE_DESCRIPTION) ID,
                          QR.CHARGE_ID,
                          QR.CHARGE_DESCRIPTION,
                          QR.CHARGE_AT,
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(UPPER(QR.SELL_BUY_FLAG),
                                 'S',
                                 QR.BUYRATE_ID,
                                 'B',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_SELLCHARGESMASTER QBC
                            WHERE QBC.SELLCHARGEID = QR.SELLRATE_ID),
                          (SELECT QCBM.BASIS_DESCRIPTION
                             FROM QMS_BUYSELLCHARGESMASTER QBC,
                                  QMS_CHARGE_BASISMASTER   QCBM
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID
                              AND QCBM.CHARGEBASIS = QBC.CHARGEBASIS) CHARGEBASIS,
                          (SELECT CURRENCY
                             FROM QMS_SELLCHARGESMASTER QBC
                            WHERE QBC.SELLCHARGEID = QR.SELLRATE_ID),
                          (SELECT DENSITY_CODE
                             FROM QMS_BUYSELLCHARGESMASTER QBC
                            WHERE QBC.BUYSELLCHARGEID = QR.BUYRATE_ID),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT QBC.RATE_BREAK
                             FROM QMS_SELLCHARGESMASTER QBC
                            WHERE QBC.SELLCHARGEID = QR.SELLRATE_ID),
                          (SELECT QBC.RATE_TYPE
                             FROM QMS_SELLCHARGESMASTER QBC
                            WHERE QBC.SELLCHARGEID = QR.SELLRATE_ID),
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_SELLCHARGESDTL
                            WHERE SELLCHARGEID = QR.SELLRATE_ID
                              AND LANE_NO = QR.LINE_NO),
                          (SELECT DISTINCT REMARKS
                             FROM QMS_CHARGEDESCMASTER  QCD,
                                  QMS_SELLCHARGESMASTER QBC
                            WHERE QCD.CHARGEID = QR.CHARGE_ID
                              AND QCD.CHARGEDESCID = QR.CHARGE_DESCRIPTION
                              AND QCD.INACTIVATE = 'N'
                              AND QCD.TERMINALID = QBC.TERMINALID
                              AND QBC.SELLCHARGEID = QR.SELLRATE_ID) INT_NAME,
                          (SELECT DISTINCT EXT_CHARGE_NAME
                             FROM QMS_CHARGEDESCMASTER  QCD,
                                  QMS_SELLCHARGESMASTER QBC
                            WHERE QCD.CHARGEID = QR.CHARGE_ID
                              AND QCD.CHARGEDESCID = QR.CHARGE_DESCRIPTION
                              AND QCD.INACTIVATE = 'N'
                              AND QCD.TERMINALID = QBC.TERMINALID
                              AND QBC.SELLCHARGEID = QR.SELLRATE_ID) EXT_NAME
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.ORIGIN_LOCATION = P_ORIGIN
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.SHIPMENT_MODE = P_SHMODE
             AND QM.IE_FLAG = 'E'
             AND QR.SELL_BUY_FLAG IN ('B', 'S')
             AND UPPER(CHARGE_AT) = 'ORIGIN'
             AND (QR.CHARGE_ID, QR.CHARGE_DESCRIPTION) IN
                 (SELECT QC.CHARGE_ID, QC.CHARGEDESCID
                    FROM QMS_CHARGE_GROUPSMASTER QC
                   WHERE QC.CHARGEDESCID = V_CHARGE_DESC
                     AND QC.CHARGE_ID = V_CHARGE_ID
                     AND QC.INACTIVATE = 'N'
                     AND QC.INVALIDATE = 'F'
                     AND QC.CHARGEGROUP_ID IN (V_CHARGEGROUP))
             AND QM.ID =
                 (SELECT MAX(QM.ID)
                    FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                   WHERE QM.ID = IQR.QUOTE_ID
                     AND QM.ORIGIN_LOCATION = P_ORIGIN
                     AND QM.CUSTOMER_ID = P_CUSTOMERID
                     AND QM.SHIPMENT_MODE = P_SHMODE
                     AND QM.IE_FLAG = 'E'
                     AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                     AND UPPER(CHARGE_AT) = 'ORIGIN'
                     AND IQR.CHARGE_ID = V_CHARGE_ID
                     AND IQR.CHARGE_DESCRIPTION = V_CHARGE_DESC);
      END IF;
    END LOOP;
    CLOSE V_CURR;
    IF V_OPR_ADM_FLAG <> 'H' THEN
      OPEN V_CURR FOR
        SELECT DISTINCT QR.CHARGE_ID          CHARGE_ID,
                        QR.CHARGE_DESCRIPTION CHARGE_DESC,
                        QR.SELL_BUY_FLAG      SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.DEST_LOCATION = P_DESTINATION
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.TERMINAL_ID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H'
                UNION ALL
                SELECT P_TERMINALID TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = P_TERMINALID)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('B', 'S')
           AND UPPER(CHARGE_AT) = 'DESTINATION'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.DEST_LOCATION = P_DESTINATION
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                   AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                   AND UPPER(CHARGE_AT) = 'DESTINATION'
                   AND QR.CHARGE_ID = IQR.CHARGE_ID
                   AND QR.CHARGE_DESCRIPTION = IQR.CHARGE_DESCRIPTION
                   AND QM.TERMINAL_ID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                   PARENT_TERMINAL_ID
                         START WITH CHILD_TERMINAL_ID = P_TERMINALID
                        UNION ALL
                        SELECT TERMINALID TERM_ID
                          FROM FS_FR_TERMINALMASTER
                         WHERE OPER_ADMIN_FLAG = 'H'
                        UNION ALL
                        SELECT P_TERMINALID TERM_ID
                          FROM DUAL
                        UNION ALL
                        SELECT CHILD_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY PRIOR
                                    CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                         START WITH PARENT_TERMINAL_ID = P_TERMINALID));
      /*And Qr.Charge_Description in (select chargedescid
      from qms_charge_groupsmaster where chargegroup_id=p_chargegrps);*/ --@@Modified by Kameswari for the WPBN issues-80162,80018
      /*Union All
      Select Distinct Qr.Charge_Id          Charge_Id,
                      Qr.Charge_Description Charge_Desc,
                      Qr.Sell_Buy_Flag      Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Dest_Location = p_Destination
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipment_Mode = p_Shmode
         And Qm.Terminal_Id In
             (Select Parent_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Child_Terminal_Id = Prior Parent_Terminal_Id
               Start With Child_Terminal_Id = p_Terminalid
              Union All
              Select Terminalid Term_Id
                From Fs_Fr_Terminalmaster
               Where Oper_Admin_Flag = 'H'
              Union All
              Select p_Terminalid Term_Id
                From Dual
              Union All
              Select Child_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
               Start With Parent_Terminal_Id = p_Terminalid)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'B'
         And Upper(Charge_At) = 'DESTINATION'
         And (Qr.Charge_Id, Qr.Charge_Description) Not In
             (Select Sm.Charge_Id, Sm.Chargedescid
                From Qms_Sellchargesmaster Sm, Qms_Sellchargesdtl Sd
               Where Sm.Sellchargeid = Sd.Sellchargeid
                 And Sm.Ie_Flag = 'A'
                 And Sm.Terminalid In
                     (Select Parent_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Child_Terminal_Id = Prior
                                 Parent_Terminal_Id
                       Start With Child_Terminal_Id = p_Terminalid
                      Union All
                      Select Terminalid Term_Id
                        From Fs_Fr_Terminalmaster
                       Where Oper_Admin_Flag = 'H'
                      Union All
                      Select p_Terminalid Term_Id
                        From Dual
                      Union All
                      Select Child_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Prior Child_Terminal_Id =
                                  Parent_Terminal_Id
                       Start With Parent_Terminal_Id = p_Terminalid)*/
      /*AND qm.ie_flag = 'E'
      AND qr.sell_buy_flag ='S'
      AND UPPER (qr.charge_at) = 'DESTINATION'*/
      /*AND qm.created_tstmp =  (SELECT MAX (qm.created_tstmp)
       FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm
      WHERE qm.ID = iqr.quote_id
        AND qm.dest_location =p_destination
        AND qm.customer_id =p_customerid
        AND qm.shipment_mode =p_shmode
        AND qm.ie_flag = 'E'
        AND iqr.sell_buy_flag IN ('B', 'S')
        AND UPPER (charge_at) = 'DESTINATION'
        AND qr.charge_id =iqr.charge_id
        AND QR.CHARGE_DESCRIPTION=iQR.CHARGE_DESCRIPTION)*/
    ELSE
      OPEN V_CURR FOR
        SELECT DISTINCT QR.CHARGE_ID          CHARGE_ID,
                        QR.CHARGE_DESCRIPTION CHARGE_DESC,
                        QR.SELL_BUY_FLAG      SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.DEST_LOCATION = P_DESTINATION
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('B', 'S')
           AND UPPER(CHARGE_AT) = 'DESTINATION'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.DEST_LOCATION = P_DESTINATION
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                   AND IQR.SELL_BUY_FLAG IN ('B', 'S')
                   AND UPPER(CHARGE_AT) = 'DESTINATION'
                   AND QR.CHARGE_ID = IQR.CHARGE_ID
                   AND QR.CHARGE_DESCRIPTION = IQR.CHARGE_DESCRIPTION);
      /*And Qr.Charge_Description in (select chargedescid from
      qms_charge_groupsmaster where chargegroup_id=p_chargegrps);*/ --@@Modified by Kameswari for the WPBN issues-80162,80018
      /* And Qr.Charge_Description in (select chargedescid
            from qms_charge_groupsmaster where chargegroup_id in (
                       qms_rsr_rates_pkg.seperator(p_chargegrps) )); --@@Modified by Kameswari for the WPBN issues-80162,80018
      */
      /*Union All
      Select Distinct Qr.Charge_Id          Charge_Id,
                      Qr.Charge_Description Charge_Desc,
                      Qr.Sell_Buy_Flag      Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Dest_Location = p_Destination
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipment_Mode = p_Shmode
         \*And Qm.Terminal_Id In
             (Select Distinct Terminalid From Fs_Fr_Terminalmaster)*\
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'B'
         And Upper(Charge_At) = 'DESTINATION'
         And (Qr.Charge_Id, Qr.Charge_Description) Not In
             (Select Sm.Charge_Id, Sm.Chargedescid
                From Qms_Sellchargesmaster Sm, Qms_Sellchargesdtl Sd
               Where Sm.Sellchargeid = Sd.Sellchargeid
                 And Sm.Ie_Flag = 'A'
                 \*And Sm.Terminalid In
                     (Select Distinct Terminalid
                        From Fs_Fr_Terminalmaster)*\
              \*AND qm.ie_flag = 'E'
                                       AND qr.sell_buy_flag ='S'
                                       AND UPPER (qr.charge_at) = 'DESTINATION'*\
              );*/
      /*AND qm.created_tstmp =  (SELECT MAX (qm.created_tstmp)
       FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm
      WHERE qm.ID = iqr.quote_id
        AND qm.dest_location =p_destination
        AND qm.customer_id =p_customerid
        AND qm.shipment_mode =p_shmode
        AND qm.ie_flag = 'E'
        AND iqr.sell_buy_flag IN ('B', 'S')
        AND UPPER (charge_at) = 'DESTINATION'
        AND qr.charge_id =iqr.charge_id
        AND QR.CHARGE_DESCRIPTION=iQR.CHARGE_DESCRIPTION)*/
    END IF;
    LOOP
      FETCH V_CURR
        INTO V_CHARGE_ID, V_CHARGE_DESC, V_SELL_BUY_FLAG;
      EXIT WHEN V_CURR%NOTFOUND;
      /* v_sql1 := 'INSERT INTO TEMP_CHARGES (SELLCHARGEID, CHARGE_ID, CHARGEDESCID, COST_INCURREDAT, CHARGESLAB, BUYRATE, SELLRATE, MARGIN_TYPE, MARGINVALUE,  SEL_BUY_FLAG, BUY_CHARGE_ID, TERMINALID,CHARGEBASIS,CURRENCY,DENSITY_RATIO,SELECTED_FLAG,MARGIN_DISCOUNT_FLAG,line_no,WEIGHT_BREAK,RATE_TYPE,RATE_INDICATOR,INT_CHARGE_NAME,EXT_CHARGE_NAME) ';
            v_sql2 := ' SELECT distinct decode(upper(qr.sell_buy_flag),''S'',qr.sellrate_id,''B'',qr.buyrate_id), qr.charge_id, qr.charge_description, qr.charge_at,qr.break_point, qr.buy_rate,';
      */ --@@Commented and Modified by Kameswari for the WPBN issue-130538
      V_SQL1 := 'INSERT INTO TEMP_CHARGES (SELLCHARGEID, ID,CHARGE_ID, CHARGEDESCID, COST_INCURREDAT, CHARGESLAB, BUYRATE, SELLRATE, MARGIN_TYPE, MARGINVALUE,  SEL_BUY_FLAG, BUY_CHARGE_ID, TERMINALID,CHARGEBASIS,CURRENCY,DENSITY_RATIO,SELECTED_FLAG,MARGIN_DISCOUNT_FLAG,line_no,WEIGHT_BREAK,RATE_TYPE,RATE_INDICATOR,INT_CHARGE_NAME,EXT_CHARGE_NAME) ';
      V_SQL2 := ' SELECT distinct decode(upper(qr.sell_buy_flag),''S'',qr.sellrate_id,''B'',qr.buyrate_id),(select max(id)  from qms_charge_groupsmaster where chargegroup_id IN (' ||
                QMS_RSR_RATES_PKG.SEPERATOR(P_CHARGEGRPS) ||
                ' ) and charge_id = qr.charge_id and chargedescid = qr.charge_description)id, qr.charge_id, qr.charge_description, qr.charge_at,qr.break_point, qr.buy_rate,';
      V_SQL3 := '';
      V_SQL4 := '';
      V_SQL5 := 'qr.r_sell_rate sellrate,decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin_type,qr.discount_type), decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin,qr.discount), qr.sell_buy_flag, decode(upper(qr.sell_buy_flag),''S'',qr.buyrate_id,''B'',''''), '; --qm.terminal_id ';
      IF V_SELL_BUY_FLAG = 'B' THEN
        V_SQL5_1 := '(SELECT DISTINCT TERMINALID from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id)';
      ELSE
        V_SQL5_1 := '(select DISTINCT TERMINALID from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID)';
      END IF;
      V_SQL5 := V_SQL5 || V_SQL5_1;
      V_SQL6 := '   FROM QMS_QUOTE_RATES qr, QMS_QUOTE_MASTER qm WHERE qm.ID = qr.quote_id  ';
      V_SQL7 := ' and qm.DEST_LOCATION =:v_dest';
      V_SQL8 := '  AND qm.customer_id =:v_customer_id AND qm.shipment_mode =:v_shmode
		  AND qm.ie_flag = ''E''   AND qr.sell_buy_flag IN (''B'', ''S'') ';
      V_SQL9 := '  AND UPPER (charge_at) = ''DESTINATION'' ';
      /*v_sql10 := '  AND qr.charge_id =:v_charge_id AND QR.CHARGE_DESCRIPTION=:v_charge_desc'
                      --|| ' And QMC.Chargeid = QR.Charge_Id And Qmc.Chargedescid = Qr.Charge_Description And Inactivate = ''N''';
                       ||
                       ' And (Qr.Charge_Id, Qr.Charge_Description) In (Select Chargeid, Chargedescid From Qms_Chargedescmaster Qmc Where Qr.Charge_Id = Qmc.Chargeid And Qr.Charge_Description = Qmc.Chargedescid And Inactivate = ''N'') ' ||
                       ' And Qr.Charge_Description in (select  chargedescid from
                      qms_charge_groupsmaster qc where qc.chargegroup_id in (' ||
                       qms_rsr_rates_pkg.seperator(p_chargegrps) || '))';
      */
      V_SQL10 := ' And (Qr.Charge_Id, Qr.Charge_Description) in (select  qc.charge_id,qc.chargedescid from
		QMS_CHARGE_GROUPSMASTER qc WHERE qc.chargedescid=:v_chargedesc_id AND qc.charge_id=:v_chargeid AND qc.inactivate =''N'' AND qc.invalidate=''F'' AND qc.chargegroup_id IN (' ||
                 QMS_RSR_RATES_PKG.SEPERATOR(P_CHARGEGRPS) || '))';
      V_SQL11 := '   AND qm.ID =  (SELECT MAX (qm.ID)  FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm  WHERE qm.ID = iqr.quote_id';
      V_SQL16 := ' and qm.DEST_LOCATION =:v_dest';
      V_SQL17 := '  AND qm.customer_id =:v_customer_id AND qm.shipment_mode =:v_shmode
		AND qm.ie_flag = ''E''   AND iqr.sell_buy_flag IN (''B'', ''S'') ';
      V_SQL18 := '  AND UPPER (charge_at) = ''DESTINATION'' ';
      V_SQL19 := '  AND iqr.charge_id =:v_charge_id AND iQR.CHARGE_DESCRIPTION=:v_charge_desc';
      /* v_sql20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=QR.CHARGE_ID AND CHARGEDESCID=QR.CHARGE_DESCRIPTION AND INACTIVATE=''N'')INT_NAME,
      (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=QR.CHARGE_ID AND CHARGEDESCID=QR.CHARGE_DESCRIPTION AND INACTIVATE=''N'')EXT_NAME ';*/
      --   v_sql 7 and v_sql8  and   v_sql 9 v_sql10
      IF V_SELL_BUY_FLAG = 'B' THEN
        V_SQL20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER qcd,qms_buysellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.BUYSELLCHARGEID=qr.buyrate_id)INT_NAME,
		     (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd,QMS_BUYSELLCHARGESMASTER qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' AND qcd.terminalid = qbc.terminalid  AND qbc.BUYSELLCHARGEID=qr.buyrate_id)EXT_NAME '; --@@Modified by Kameswari for the WPBN issue-87001
        V_SQL12 := ' , (select qcbm.BASIS_DESCRIPTION from qms_buysellchargesmaster qbc,Qms_Charge_Basismaster qcbm  where qbc.BUYSELLCHARGEID=qr.buyrate_id And qcbm.chargebasis = QBC.chargebasis)';
        V_SQL13 := ' ,(select  CURRENCY from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id)';
        V_SQL14 := ' ,(select density_code from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),''T'',qr.margin_discount_flag, qr.line_no ' ||
                   ',(SELECT qbc.RATE_BREAK from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),(SELECT qbc.RATE_TYPE from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),
	       (SELECT Chargerate_Indicator FROM QMS_BUYCHARGESDTL WHERE Buysellchaegeid = Qr.Buyrate_Id AND Lane_No = Qr.Line_No),';
      ELSE
        V_SQL20 := '(SELECT DISTINCT REMARKS FROM QMS_CHARGEDESCMASTER qcd,qms_sellchargesmaster qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' and qcd.terminalid = qbc.terminalid  and qbc.SELLCHARGEID=qr.SELLRATE_ID)INT_NAME,
		     (SELECT DISTINCT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER qcd,QMS_SELLCHARGESMASTER qbc WHERE qcd.CHARGEID=QR.CHARGE_ID AND qcd.CHARGEDESCID=QR.CHARGE_DESCRIPTION AND qcd.INACTIVATE=''N'' AND qcd.terminalid = qbc.terminalid  AND qbc.SELLCHARGEID=qr.SELLRATE_ID)EXT_NAME '; --@@Modified by Kameswari for the WPBN issue-87001
        V_SQL12 := ' , (select qcbm.BASIS_DESCRIPTION from qms_sellchargesmaster qbc,Qms_Charge_Basismaster qcbm  where qbc.SELLCHARGEID=qr.SELLRATE_ID And qcbm.chargebasis = QBC.chargebasis)';
        V_SQL13 := ' ,(select  CURRENCY from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID)';
        V_SQL14 := ' ,(select density_code from qms_buysellchargesmaster qbc  where qbc.BUYSELLCHARGEID=qr.buyrate_id),''T'',qr.margin_discount_flag, qr.line_no' ||
                   ',(SELECT qbc.RATE_BREAK from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID),(SELECT qbc.RATE_TYPE from qms_sellchargesmaster qbc  where qbc.SELLCHARGEID=qr.SELLRATE_ID),
	       (SELECT Chargerate_Indicator FROM QMS_SELLCHARGESDTL WHERE SELLCHARGEID = Qr.Sellrate_Id AND Lane_No = Qr.Line_No),';
      END IF;
      /*v_sql7 :=
            ' and qm.DEST_LOCATION = ' || '''' || p_destination || '''';
      v_sql9 := '  AND UPPER (charge_at) = ''DESTINATION'' ';*/
      EXECUTE IMMEDIATE (V_SQL1 || V_SQL2 || V_SQL3 || V_SQL4 || V_SQL5 ||
                        V_SQL12 || V_SQL13 || V_SQL14 || V_SQL20 || V_SQL6 ||
                        V_SQL15 || V_SQL7 || V_SQL8 || V_SQL9 || V_SQL10 ||
                        V_SQL11 || V_SQL16 || V_SQL17 || V_SQL18 ||
                        V_SQL19 || ')')
        USING P_DESTINATION, P_CUSTOMERID, P_SHMODE, V_CHARGE_ID, V_CHARGE_DESC, P_DESTINATION, P_CUSTOMERID, P_SHMODE, V_CHARGE_ID, V_CHARGE_DESC;
    END LOOP;
    CLOSE V_CURR;
  END;

  /*
  This procedure is called from quote_sell_buy_ratesdtl_proc.
  If the freight rate selected in the second screen matches any other quote's freight rate
  defined for the same customer between the same trade lane (Tied Customer Info), the previous rate
  will be used instead of the current one.

  The IN Parameters are:
     p_customerid          VARCHAR2,
     p_origin              VARCHAR2,
     p_destination         VARCHAR2,
     p_carrier             VARCHAR2,
     p_servicelevel        VARCHAR2,
     p_frequency           VARCHAR2,
     p_freightRateTerm     VARCHAR2,
     p_terminalid          VARCHAR2,
     p_shmode              VARCHAR2,
     p_flag                VARCHAR2,
     p_quote_org           VARCHAR2,
     p_quote_dest          VARCHAR2,
     p_operation           VARCHAR2,
     p_quote_id            VARCHAR2

  It Contains no OUT Parameter as the data is stored in a global temporary table TEMP_CHARGES.

  */
  PROCEDURE TIED_CUSTINFO_FREIGHT_PROC(P_CUSTOMERID      VARCHAR2,
                                       P_ORIGIN          VARCHAR2,
                                       P_DESTINATION     VARCHAR2,
                                       P_CARRIER         VARCHAR2,
                                       P_SERVICELEVEL    VARCHAR2,
                                       P_FREQUENCY       VARCHAR2,
                                       P_FREIGHTRATETERM VARCHAR2,
                                       P_TERMINALID      VARCHAR2,
                                       P_SHMODE          VARCHAR2,
                                       P_FLAG            VARCHAR2,
                                       P_QUOTE_ORG       VARCHAR2,
                                       P_QUOTE_DEST      VARCHAR2,
                                       P_OPERATION       VARCHAR2,
                                       P_QUOTE_ID        VARCHAR2) IS
    V_OPR_ADM_FLAG  VARCHAR2(5);
    V_TERMINALS     VARCHAR2(32000);
    V_SQL1          VARCHAR2(4000);
    V_SQL2          VARCHAR2(4000);
    V_SQL3          VARCHAR2(4000);
    V_SQL4          VARCHAR2(4000);
    V_SQL5          VARCHAR2(4000);
    V_SQL5_1        VARCHAR2(4000);
    V_SQL6          VARCHAR2(4000);
    V_SQL7          VARCHAR2(4000);
    V_SQL8          VARCHAR2(4000);
    V_SQL9          VARCHAR2(4000);
    V_SQL10         VARCHAR2(4000);
    V_SQL11         VARCHAR2(4000);
    V_SQL12         VARCHAR2(4000);
    V_SQL13         VARCHAR2(4000);
    V_SQL14         VARCHAR2(4000);
    V_SQL15         VARCHAR2(4000);
    V_SQL16         VARCHAR2(100);
    V_SQL17         VARCHAR2(4000);
    V_SQL18         VARCHAR2(4000);
    V_SQL19         VARCHAR2(4000);
    V_SQL20         VARCHAR2(4000);
    V_SQL21         VARCHAR2(4000);
    V_SQL22         VARCHAR2(4000);
    V_SQL23         VARCHAR2(4000);
    V_CURR          SYS_REFCURSOR;
    V_SELLRATEID    NUMBER;
    V_BUYRATEID     NUMBER;
    V_TERMINAL_ID   VARCHAR2(100);
    V_SELL_BUY_FLAG VARCHAR2(100);
    V_RATELANENO    NUMBER;
    V_SERIAL_NO     NUMBER;
  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    IF V_OPR_ADM_FLAG <> 'H' THEN
      OPEN V_CURR FOR
        SELECT DISTINCT QR.SELLRATE_ID SELLRATEID,
                        QR.BUYRATE_ID BUYRATEID,
                        QR.RATE_LANE_NO,
                        QR.SELL_BUY_FLAG SELL_BUY_FLAG,
                        QR.SERIAL_NO
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
           AND QM.DEST_LOCATION = P_QUOTE_DEST
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.TERMINAL_ID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                UNION
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H'
                UNION
                SELECT P_TERMINALID TERM_ID
                  FROM DUAL
                UNION
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = P_TERMINALID)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')
           AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER')
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                   AND QM.DEST_LOCATION = P_QUOTE_DEST
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                      /* And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                          (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                             And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                         (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')
                   AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER'));
    ELSE
      OPEN V_CURR FOR
        SELECT DISTINCT QR.SELLRATE_ID SELLRATEID,
                        QR.BUYRATE_ID BUYRATEID,
                        QR.RATE_LANE_NO,
                        DECODE(QR.SELL_BUY_FLAG, 'BR', 'BR', 'K') SELL_BUY_FLAG,
                        QR.SERIAL_NO
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
           AND QM.DEST_LOCATION = P_QUOTE_DEST
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPMENT_MODE = P_SHMODE
           AND QM.TERMINAL_ID IN
               (SELECT DISTINCT TERMINALID FROM FS_FR_TERMINALMASTER)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')
           AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER')
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_QUOTE_ORG
                   AND QM.DEST_LOCATION = P_QUOTE_DEST
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPMENT_MODE = P_SHMODE
                   AND QM.IE_FLAG = 'E'
                      /*And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR')
                   AND UPPER(CHARGE_AT) IN ('FREIGHT', 'CARRIER'));
    END IF;
    LOOP
      FETCH V_CURR
        INTO V_SELLRATEID, V_BUYRATEID, V_RATELANENO, V_SELL_BUY_FLAG, V_SERIAL_NO;
      EXIT WHEN V_CURR%NOTFOUND;
      V_SQL1 := 'INSERT INTO TEMP_CHARGES (COST_INCURREDAT,CHARGEDESCID,SELLCHARGEID,LEG_SL_NO, CHARGESLAB,BUYRATE,SELLRATE,MARGIN_TYPE,MARGINVALUE,SEL_BUY_FLAG,BUY_CHARGE_ID,TERMINALID,CHARGEBASIS,CURRENCY,DENSITY_RATIO,EFROM, VALIDUPTO,SELECTED_FLAG,MARGIN_DISCOUNT_FLAG,lane_no,line_no,SRV_LEVEL,WEIGHT_BREAK,RATE_TYPE,RATE_INDICATOR,RATE_DESCRIPTION,CONSOLE_TYPE) '; --@@Modified by Kameswari for the WPBN issue-112348
      V_SQL2 := ' SELECT   distinct qr.CHARGE_AT, qr.charge_description, decode(qr.sell_buy_flag,''RSR'', qr.sellrate_id,''CSR'', qr.sellrate_id, ''BR'', qr.buyrate_id), qr.SERIAL_NO,qr.break_point, qr.buy_rate, ';
      V_SQL3 := '';
      V_SQL4 := '';
      V_SQL5 := ' qr.r_sell_rate sellrate,decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin_type,qr.discount_type), decode(nvl(qr.margin_discount_flag,''M''),''M'',qr.margin,qr.discount), qr.sell_buy_flag, decode(qr.sell_buy_flag,''RSR'', qr.buyrate_id,''CSR'', qr.buyrate_id, ''BR'', ''''), '; --qm.terminal_id ';
      IF V_SELL_BUY_FLAG = 'BR' THEN
        V_SQL5_1 := '(select  DISTINCT TERMINALID from QMS_BUYRATES_MASTER qbc  where qbc.BUYRATEID=qr.buyrate_id)';
      ELSE
        V_SQL5_1 := '(select  DISTINCT TERMINALID from QMS_REC_CON_SELLRATESMASTER qrc  where qrc.REC_CON_ID=qr.SELLRATE_ID)';
      END IF;
      V_SQL5  := V_SQL5 || V_SQL5_1;
      V_SQL6  := ' FROM QMS_QUOTE_RATES qr, QMS_QUOTE_MASTER qm,FS_RT_PLAN frp,FS_RT_LEG frl  WHERE qm.ID = qr.quote_id ';
      V_SQL7  := '    AND qm.origin_location =:v_quote_origin AND qm.dest_location =:v_quote_dest
		    AND qm.customer_id =:v_customer_id AND qm.shipment_mode =:v_shmode
		    AND qm.ie_flag = ''E'' ';
      V_SQL8  := '  AND UPPER (charge_at) IN( ''FREIGHT'',''CARRIER'') ';
      V_SQL17 := ' AND qr.sell_buy_flag IN (''BR'', ''RSR'',''CSR'') AND qr.BUYRATE_ID =:v_buyrateid
		 AND qr.RATE_LANE_NO =:v_rate_lane_no';
      V_SQL9  := '  AND frp.QUOTE_ID = qm.QUOTE_ID  AND frl.RT_PLAN_ID=frp.RT_PLAN_ID  AND frl.ORIG_LOC=:v_origin
		  AND frl.DEST_LOC=:v_destination AND frl.SERIAL_NO=qr.SERIAL_NO AND qm.ID = ';
      V_SQL10 := ' (SELECT MAX (qm.ID)  FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm WHERE qm.ID = iqr.quote_id ';
      V_SQL23 := ' AND iqr.sell_buy_flag IN (''BR'', ''RSR'',''CSR'') AND iqr.BUYRATE_ID =:v_buyrate_id
		 AND iqr.RATE_LANE_NO =:v_rate_lane_no ';
      --   v_sql 6 and v_sql7
      /*IF UPPER (p_operation) = 'MODIFY' OR UPPER (p_operation) = 'COPY'
      THEN*/
      V_SQL19 := 'AND qm.quote_id <>:v_quote_id';
      /* END IF;*/
      IF V_SELL_BUY_FLAG = 'BR' THEN
        V_SQL11 := ' , (select DISTINCT ''Per ''|| qbc.UOM  from qms_buyRATES_MASTER qbc  where qbc.BUYRATEID=qr.buyrate_id )';
        V_SQL12 := ' ,(select  DISTINCT CURRENCY from qms_buyRATES_MASTER qbc  where qbc.BUYRATEID=qr.buyrate_id)';
        V_SQL13 := ' ,(select DISTINCT DENSITY_CODE from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO )';
        V_SQL14 := ', (select  DISTINCT EFFECTIVE_FROM from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO)';
        V_SQL15 := ', (select  DISTINCT VALID_UPTO from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO),''T'',qr.margin_discount_flag,qr.rate_lane_no,qr.line_no';
        V_SQL16 := '';
        V_SQL18 := 'AND iqr.sell_buy_flag IN (''BR'', ''RSR'', ''CSR'')';
        /*
        AND iqr.buyrate_id = '
           || ''''
           || v_buyrateid
           || ''''
           || '  AND iqr.rate_lane_no = '
           || ''''
           || v_ratelaneno
           || '''
        */
        V_SQL20 := ',(select DISTINCT qbd.SERVICE_LEVEL from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO and (qbd.RATE_DESCRIPTION=''A FREIGHT RATE'' or  qbd.RATE_DESCRIPTION IS NULL))'; --@@MOdified by kameswari for Surcharge Enhancements
        V_SQL21 := ',(select DISTINCT qbc.WEIGHT_BREAK from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO ),(select DISTINCT qbc.RATE_TYPE from qms_buyRATES_MASTER qbc,qms_buyrates_dtl qbd  where qbc.BUYRATEID=qr.buyrate_id and qbd.BUYRATEID=qbc.BUYRATEID and qbd.LANE_NO=qr.RATE_LANE_NO )
			,(SELECT CHARGERATE_INDICATOR  FROM QMS_BUYRATES_DTL QBD WHERE QBD.BUYRATEID= qr.buyrate_id AND QBD.LANE_NO=QR.RATE_LANE_NO AND QBD.LINE_NO = QR.LINE_NO) ,(SELECT DECODE(QBD.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',QBD.RATE_DESCRIPTION)RATE_DESCRIPTION  FROM QMS_BUYRATES_DTL QBD WHERE  QBD.BUYRATEID= qr.buyrate_id AND QBD.LANE_NO=QR.RATE_LANE_NO AND QBD.LINE_NO = QR.LINE_NO ),(SELECT console_type FROM QMS_BUYRATES_MASTER qbm WHERE qbm.buyrateid=qr.buyrate_id)'; --@@Modified by kameswari for Surcharge Enhancements
        V_SQL22 := ' AND (qr.buyrate_id, qr.rate_lane_no) in (select bd.buyrateid, bd.lane_no from qms_buyrates_dtl bd, qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bd.origin=:v_origin and bd.destination=:v_destination and bd.carrier_id=:v_carrier_id and bd.service_level=:v_srvc_level and bd.frequency=:v_frequency and bm.terminalid=:v_freightRateTerm)';
        --(SELECT CHARGERATE_INDICATOR,DECODE(qbd.RATE_DESCRIPTION,'''',''FREIGHT RATE'',qbd.RATE_DESCRIPTION)RATE_DESCRIPTION FROM QMS_BUYRATES_DTL QBD WHERE QBD.BUYRATEID= qr.buyrate_id AND QBD.LANE_NO=QR.RATE_LANE_NO AND QBD.LINE_NO = QR.LINE_NO)';--@@Modified by kameswari for Surcharge Enhancements
      ELSE
        V_SQL11 := ' , (select DISTINCT ''Per''||qbc.UOM   from QMS_REC_CON_SELLRATESMASTER qrc,qms_buyrates_master qbc,QMS_REC_CON_SELLRATESdtl qrd  where qrc.REC_CON_ID=qr.SELLRATE_ID and qrc.REC_CON_ID=qrd.REC_CON_ID and qbc.BUYRATEID=qr.BUYRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO )';
        V_SQL12 := ' ,(select  DISTINCT CURRENCY from QMS_REC_CON_SELLRATESMASTER qrc  where qrc.REC_CON_ID=qr.SELLRATE_ID)';
        V_SQL13 := ' ,(select  DISTINCT qbd.density_code from qms_buyrates_dtl qbd,QMS_REC_CON_SELLRATESdtl qrd  where qrd.REC_CON_ID =qr.SELLRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO and qrd.BUYRATEID=qr.BUYRATE_ID and qrd.BUYRATEID=qbd.BUYRATEID and qbd.LANE_NO=qrd.LANE_NO)';
        V_SQL14 := ' ,(select  DISTINCT qbd.EFFECTIVE_FROM from qms_buyrates_dtl qbd,QMS_REC_CON_SELLRATESdtl qrd  where qrd.REC_CON_ID =qr.SELLRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO and qrd.BUYRATEID=qr.BUYRATE_ID and qrd.BUYRATEID=qbd.BUYRATEID and qbd.LANE_NO=qrd.LANE_NO)';
        V_SQL15 := ' ,(select  DISTINCT qbd.VALID_UPTO from qms_buyrates_dtl qbd,QMS_REC_CON_SELLRATESdtl qrd  where qrd.REC_CON_ID =qr.SELLRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO and qrd.BUYRATEID=qr.BUYRATE_ID and qrd.BUYRATEID=qbd.BUYRATEID and qbd.LANE_NO=qrd.LANE_NO),''T'',qr.margin_discount_flag,qr.rate_lane_no, qr.line_no';
        V_SQL16 := ' and qr.SELLRATE_ID=:v_sell_rate_id';
        V_SQL18 := 'AND iqr.sell_buy_flag IN (''BR'', ''RSR'', ''CSR'')';
        /*
        AND iqr.buyrate_id = '
           || ''''
           || v_buyrateid
           || ''''
           || '  AND iqr.rate_lane_no = '
           || ''''
           || v_ratelaneno
           || ''''
           || ' and iqr.SELLRATE_ID='
           || ''''
           || v_sellrateid
           || '''
        */
        V_SQL20 := ',(select  DISTINCT qrd.SERVICELEVEL_ID from QMS_REC_CON_SELLRATESdtl qrd  where qrd.REC_CON_ID =qr.SELLRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO and qrd.BUYRATEID=qr.BUYRATE_ID and (qrd.RATE_DESCRIPTION=''A FREIGHT RATE'' or  qrd.RATE_DESCRIPTION IS NULL))';
        V_SQL21 := ',(select  DISTINCT sm.WEIGHT_BREAK from QMS_REC_CON_SELLRATESmaster sm,QMS_REC_CON_SELLRATESdtl qrd  where qrd.REC_CON_ID =qr.SELLRATE_ID and qrd.LANE_NO=qr.RATE_LANE_NO and qrd.BUYRATEID=qr.BUYRATE_ID and sm.REC_CON_ID=qrd.REC_CON_ID)
			,(SELECT  DISTINCT sm.RATE_TYPE FROM QMS_REC_CON_SELLRATESMASTER sm,QMS_REC_CON_SELLRATESDTL qrd  WHERE qrd.REC_CON_ID =qr.SELLRATE_ID AND qrd.LANE_NO=qr.RATE_LANE_NO AND qrd.BUYRATEID=qr.BUYRATE_ID AND sm.REC_CON_ID=qrd.REC_CON_ID)
			,(SELECT CHARGERATE_INDICATOR  FROM QMS_REC_CON_SELLRATESDTL QSD WHERE QSD.REC_CON_ID=QR.SELLRATE_ID AND QSD.BUYRATEID= qr.buyrate_id AND QSD.LANE_NO=QR.RATE_LANE_NO AND QSD.LINE_NO = QR.LINE_NO),(SELECT DECODE(QSD.RATE_DESCRIPTION,'''',''A FREIGHT RATE'',QSD.RATE_DESCRIPTION)RATE_DESCRIPTION  FROM QMS_REC_CON_SELLRATESDTL QSD WHERE QSD.REC_CON_ID=QR.SELLRATE_ID AND QSD.BUYRATEID= qr.buyrate_id AND QSD.LANE_NO=QR.RATE_LANE_NO AND QSD.LINE_NO = QR.LINE_NO ),(SELECT console_type FROM QMS_BUYRATES_MASTER qbm WHERE qbm.buyrateid=qr.buyrate_id)'; --@@Modfied by Kameswari for Surcharge Enhancements
        V_SQL22 := ' AND (qr.sellrate_id,qr.buyrate_id, qr.rate_lane_no) in
			 (SELECT sd.rec_con_id, sd.buyrateid, sd.lane_no FROM QMS_REC_CON_SELLRATESDTL sd, QMS_REC_CON_SELLRATESMASTER sm WHERE sd.rec_con_id=sm.rec_con_id AND sd.origin=:v_origin AND sd.destination=:v_destination AND sd.carrier_id=:v_carrier_id AND sd.SERVICELEVEL_ID=:v_srvc_level
			 AND sd.frequency=:v_frequency AND sm.terminalid=:v_freightRateTerm AND (sd.Rate_Description=''A FREIGHT RATE'' OR sd.Rate_Description IS NULL))';
      END IF;
      --,(SELECT CHARGERATE_INDICATOR,DECODE(qrd.RATE_DESCRIPTION,'''',''FREIGHT RATE'',qrd.RATE_DESCRIPTION)RATE_DESCRIPTION FROM QMS_REC_CON_SELLRATESDTL QSD WHERE QSD.REC_CON_ID=QR.SELLRATE_ID AND QSD.BUYRATEID= qr.buyrate_id AND QSD.LANE_NO=QR.RATE_LANE_NO AND QSD.LINE_NO = QR.LINE_NO)';--@@Modfied by Kameswari for Surcharge Enhancements
      IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'COPY' THEN
        IF V_SELL_BUY_FLAG = 'BR' THEN
          EXECUTE IMMEDIATE (V_SQL1 || V_SQL2 || V_SQL3 || V_SQL4 || V_SQL5 ||
                            V_SQL11 || V_SQL12 || V_SQL13 || V_SQL14 ||
                            V_SQL15 || V_SQL20 || V_SQL21 || V_SQL6 ||
                            V_SQL7 || V_SQL19 || V_SQL8 || V_SQL17 ||
                            V_SQL22 || V_SQL9 || V_SQL10 || V_SQL7 ||
                            V_SQL19 || V_SQL8 || V_SQL23 || V_SQL18 || ')')
            USING P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, P_QUOTE_ID, V_BUYRATEID, V_RATELANENO, P_ORIGIN, P_DESTINATION, P_CARRIER, P_SERVICELEVEL, P_FREQUENCY, P_FREIGHTRATETERM, P_ORIGIN, P_DESTINATION, P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, P_QUOTE_ID, V_BUYRATEID, V_RATELANENO;
        ELSE
          EXECUTE IMMEDIATE (V_SQL1 || V_SQL2 || V_SQL3 || V_SQL4 || V_SQL5 ||
                            V_SQL11 || V_SQL12 || V_SQL13 || V_SQL14 ||
                            V_SQL15 || V_SQL20 || V_SQL21 || V_SQL6 ||
                            V_SQL7 || V_SQL19 || V_SQL8 || V_SQL17 ||
                            V_SQL16 || V_SQL22 || V_SQL9 || V_SQL10 ||
                            V_SQL7 || V_SQL19 || V_SQL8 || V_SQL23 ||
                            V_SQL18 || ')')
            USING P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, P_QUOTE_ID, V_BUYRATEID, V_RATELANENO, V_SELLRATEID, P_ORIGIN, P_DESTINATION, P_CARRIER, P_SERVICELEVEL, P_FREQUENCY, P_FREIGHTRATETERM, P_ORIGIN, P_DESTINATION, P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, P_QUOTE_ID, V_BUYRATEID, V_RATELANENO;
        END IF;
      ELSE
        IF V_SELL_BUY_FLAG = 'BR' THEN
          EXECUTE IMMEDIATE (V_SQL1 || V_SQL2 || V_SQL3 || V_SQL4 || V_SQL5 ||
                            V_SQL11 || V_SQL12 || V_SQL13 || V_SQL14 ||
                            V_SQL15 || V_SQL20 || V_SQL21 || V_SQL6 ||
                            V_SQL7 || V_SQL8 || V_SQL17 || V_SQL22 ||
                            V_SQL9 || V_SQL10 || V_SQL7 || V_SQL8 ||
                            V_SQL23 || V_SQL18 || ')')
            USING P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, V_BUYRATEID, V_RATELANENO, P_ORIGIN, P_DESTINATION, P_CARRIER, P_SERVICELEVEL, P_FREQUENCY, P_FREIGHTRATETERM, P_ORIGIN, P_DESTINATION, P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, V_BUYRATEID, V_RATELANENO;
        ELSE
          EXECUTE IMMEDIATE (V_SQL1 || V_SQL2 || V_SQL3 || V_SQL4 || V_SQL5 ||
                            V_SQL11 || V_SQL12 || V_SQL13 || V_SQL14 ||
                            V_SQL15 || V_SQL20 || V_SQL21 || V_SQL6 ||
                            V_SQL7 || V_SQL8 || V_SQL17 || V_SQL16 ||
                            V_SQL22 || V_SQL9 || V_SQL10 || V_SQL7 ||
                            V_SQL8 || V_SQL23 || V_SQL18 || ')')
            USING P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, V_BUYRATEID, V_RATELANENO, V_SELLRATEID, P_ORIGIN, P_DESTINATION, P_CARRIER, P_SERVICELEVEL, P_FREQUENCY, P_FREIGHTRATETERM, P_ORIGIN, P_DESTINATION, P_QUOTE_ORG, P_QUOTE_DEST, P_CUSTOMERID, P_SHMODE, V_BUYRATEID, V_RATELANENO;
        END IF;
      END IF;
    END LOOP;
    CLOSE V_CURR;
  END;

  /*
  This procedure is called from quote_sell_buy_cartages_proc.
  It is used for fetching cartage charges (Pickup and Delivery)
  which might have been used in a previous quote for the same customer between the same trade lane(Tied Customer Info).
  The previous charge will be used instead of the current one.

  The IN Parameters are:
     p_customerid    VARCHAR2,
     p_origin        VARCHAR2,
     p_destination   VARCHAR2,
     p_terminalid    VARCHAR2,
     p_shmode        VARCHAR2,
     p_flag          VARCHAR2,--Buy Charge Visibility Permission Flag
     p_shzone        VARCHAR2,
     p_conszone      VARCHAR2,
     p_operation     VARCHAR2,
     p_quote_id      VARCHAR2

  It Contains no OUT Parameter as the data is stored in a global temporary table TEMP_CHARGES.

  */
  PROCEDURE TIED_CUSTINFO_CARTAGES_PROC(P_CUSTOMERID        VARCHAR2,
                                        P_ORIGIN            VARCHAR2,
                                        P_DESTINATION       VARCHAR2,
                                        P_TERMINALID        VARCHAR2,
                                        P_SHMODE            VARCHAR2,
                                        P_FLAG              VARCHAR2,
                                        P_SHZONE            VARCHAR2,
                                        P_CONSZONE          VARCHAR2,
                                        P_SHIPPER_MODE      VARCHAR2,
                                        P_SHPPR_CNSL_TYPE   VARCHAR2,
                                        P_CNSGNEE_MODE      VARCHAR2,
                                        P_CNSGNEE_CNSL_TYPE VARCHAR2,
                                        P_OPERATION         VARCHAR2,
                                        P_QUOTE_ID          VARCHAR2) IS
    V_OPR_ADM_FLAG  VARCHAR2(5);
    V_CURR          SYS_REFCURSOR;
    V_SELLRATEID    NUMBER;
    V_BUYRATEID     NUMBER;
    V_TERMINAL_ID   VARCHAR2(100);
    V_SELL_BUY_FLAG VARCHAR2(100);
    V_RATELANENO    NUMBER;
    V_SERIAL_NO     NUMBER;
  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');
    IF V_OPR_ADM_FLAG <> 'H' THEN
      OPEN V_CURR FOR
        SELECT DISTINCT /*+ FIRST_ROWS */ QR.SELLRATE_ID   SELLRATEID,
                        QR.BUYRATE_ID    BUYRATEID,
                        QR.SELL_BUY_FLAG SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_ORIGIN
           AND QM.SHIPPERZONES = P_SHZONE
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
           AND QM.TERMINAL_ID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                UNION
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H'
                UNION
                SELECT P_TERMINALID TERM_ID
                  FROM DUAL
                UNION
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = P_TERMINALID)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
           AND UPPER(CHARGE_AT) = 'PICKUP'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_ORIGIN
                   AND QM.SHIPPERZONES = P_SHZONE
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                   AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
                   AND QM.IE_FLAG = 'E'
                      /*And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                       (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                                                                                                   And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                       (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                   AND UPPER(CHARGE_AT) = 'PICKUP'
                   AND QM.TERMINAL_ID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                   PARENT_TERMINAL_ID
                         START WITH CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT TERMINALID TERM_ID
                          FROM FS_FR_TERMINALMASTER
                         WHERE OPER_ADMIN_FLAG = 'H'
                        UNION
                        SELECT P_TERMINALID TERM_ID
                          FROM DUAL
                        UNION
                        SELECT CHILD_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY PRIOR
                                    CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                         START WITH PARENT_TERMINAL_ID = P_TERMINALID));
      /*Union All
      Select Distinct Qr.Sellrate_Id   Sellrateid,
                      Qr.Buyrate_Id    Buyrateid,
                      Qr.Sell_Buy_Flag Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Origin_Location = p_Origin
         And Qm.Shipperzones = p_Shzone
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipper_Mode = p_Shipper_Mode
         And nvl(Qm.Shipper_Console_Type,'~') = p_Shppr_Cnsl_Type
            --AND qm.shipment_mode = p_shmode
         And Qm.Terminal_Id In
             (Select Parent_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Child_Terminal_Id = Prior Parent_Terminal_Id
               Start With Child_Terminal_Id = p_Terminalid
              Union All
              Select Terminalid Term_Id
                From Fs_Fr_Terminalmaster
               Where Oper_Admin_Flag = 'H'
              Union All
              Select p_Terminalid Term_Id
                From Dual
              Union All
              Select Child_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
               Start With Parent_Terminal_Id = p_Terminalid)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'BC'
         And Upper(Charge_At) = 'PICKUP'
         And (Qr.Buyrate_Id, Qm.Shipperzones, Qr.Charge_At) Not In
             (Select Cm.Cartage_Id, Csd.Zone_Code, Csd.Charge_Type
                From Qms_Cartage_Buysellcharges Cm,
                     Qms_Cartage_Selldtl        Csd
               Where Cm.Location_Id = p_Origin
                 And Csd.Zone_Code = p_Shzone
                 And Upper(Csd.Charge_Type) = 'PICKUP'
                 And Cm.Shipment_Mode = p_Shipper_Mode
                 And nvl(Cm.Console_Type,'~') = p_Shppr_Cnsl_Type
                 And Cm.Cartage_Id = Csd.Cartage_Id
                 And (Csd.Activeinactive Is Null Or
                     Csd.Activeinactive = 'A')
                 And Cm.Terminalid In
                     (Select Parent_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Child_Terminal_Id = Prior
                                 Parent_Terminal_Id
                       Start With Child_Terminal_Id = p_Terminalid
                      Union
                      Select Terminalid Term_Id
                        From Fs_Fr_Terminalmaster
                       Where Oper_Admin_Flag = 'H'
                      Union
                      Select p_Terminalid Term_Id
                        From Dual
                      Union
                      Select Child_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Prior
                                  Child_Terminal_Id = Parent_Terminal_Id
                       Start With Parent_Terminal_Id = p_Terminalid));*/
    ELSE
      OPEN V_CURR FOR
        SELECT DISTINCT QR.SELLRATE_ID   SELLRATEID,
                        QR.BUYRATE_ID    BUYRATEID,
                        QR.SELL_BUY_FLAG SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.ORIGIN_LOCATION = P_ORIGIN
           AND QM.SHIPPERZONES = P_SHZONE
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.SHIPPER_MODE = P_SHIPPER_MODE
           AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
           AND UPPER(CHARGE_AT) IN ('PICKUP')
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.ORIGIN_LOCATION = P_ORIGIN
                   AND QM.SHIPPERZONES = P_SHZONE
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                   AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
                   AND QM.IE_FLAG = 'E'
                      /* And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                       (Decode(Qm.Active_Flag, 'I', 'NAC')) --@@Added by Kameswari for the WPBN issue-129215
                                                                                                                                                                                                                                                                                                                                                                                                                                                   And Qm.Quote_Status not in
                                                                                                                                                                                                                                                                                                                                                                                                                                                       (Decode(Qm.Active_Flag, 'I', 'ACC'))*/
                   AND ((QM.ACTIVE_FLAG = 'I' AND
                       QM.QUOTE_STATUS NOT IN ('NAC', 'ACC')) OR --@@Added by Kameswari for the WPBN issue-132225
                       (QM.ACTIVE_FLAG = 'A'))
                   AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                   AND UPPER(CHARGE_AT) = 'PICKUP');
      /*Union All
      Select Distinct Qr.Sellrate_Id   Sellrateid,
                      Qr.Buyrate_Id    Buyrateid,
                      Qr.Sell_Buy_Flag Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Origin_Location = p_Origin
         And Qm.Shipperzones = p_Shzone
         And Qm.Customer_Id = p_Customerid
         And Qm.Shipper_Mode = p_Shipper_Mode
         And nvl(Qm.Shipper_Console_Type,'~') = p_Shppr_Cnsl_Type
            --AND qm.shipment_mode = p_shmode
         And Qm.Terminal_Id In
             (Select Distinct Terminalid From Fs_Fr_Terminalmaster)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'BC'
         And Upper(Charge_At) In ('PICKUP')
            -- and (qm.origin_location,qm.shipperzones,qr.sellrate_id,qr.buyrate_id)
         And (Qr.Buyrate_Id, Qm.Shipperzones, Qr.Charge_At) Not In
             (Select Cm.Cartage_Id, Csd.Zone_Code, Csd.Charge_Type
                From Qms_Cartage_Buysellcharges Cm,
                     Qms_Cartage_Selldtl        Csd
               Where Cm.Location_Id = p_Origin
                 And Csd.Zone_Code = p_Shzone
                 And Upper(Csd.Charge_Type) = 'PICKUP'
                 And Cm.Shipment_Mode = p_Shipper_Mode
                 And nvl(Cm.Console_Type,'~') = p_Shppr_Cnsl_Type
                 And Cm.Cartage_Id = Csd.Cartage_Id
                 And (Csd.Activeinactive Is Null Or
                     Csd.Activeinactive = 'A')
                 And Cm.Terminalid In
                     (Select Terminalid From Fs_Fr_Terminalmaster));*/
    END IF;
    LOOP
      FETCH V_CURR
        INTO V_SELLRATEID, V_BUYRATEID, V_SELL_BUY_FLAG;
      EXIT WHEN V_CURR%NOTFOUND;
      IF V_SELL_BUY_FLAG = 'BC' THEN
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           SELLCHARGEID,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           ZONE,
           RATE_INDICATOR)
          SELECT DISTINCT QR.CHARGE_AT,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.SELLRATE_ID,
                                 'BC',
                                 QR.BUYRATE_ID),
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.BUYRATE_ID,
                                 'BC',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          DECODE(QR.BREAK_POINT,
                                 'MIN',
                                 'PER SHIPMENT',
                                 (SELECT DISTINCT BASIS_DESCRIPTION
                                    FROM QMS_CARTAGE_BUYDTL     CBD,
                                         QMS_CHARGE_BASISMASTER CBM
                                   WHERE CBM.CHARGEBASIS = CBD.CHARGE_BASIS
                                     AND CBD.CARTAGE_ID = QR.BUYRATE_ID
                                     AND CBD.ZONE_CODE = QM.SHIPPERZONES
                                     AND UPPER(CBD.CHARGE_TYPE) = 'PICKUP')),
                          (SELECT DISTINCT CURRENCY
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT DENSITY_CODE
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          (SELECT DISTINCT EFFECTIVE_FROM
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          (SELECT DISTINCT VALID_UPTO
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT DISTINCT QBC.WEIGHT_BREAK
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT QBC.RATE_TYPE
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          QM.SHIPPERZONES,
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_CARTAGE_BUYDTL
                            WHERE CARTAGE_ID = QR.BUYRATE_ID
                              AND ZONE_CODE = QM.SHIPPERZONES
                              AND CHARGE_TYPE = 'Pickup'
                              AND LINE_NO = QR.LINE_NO)
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.ORIGIN_LOCATION = P_ORIGIN
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.SHIPPER_MODE = P_SHIPPER_MODE
             AND QM.SHIPPERZONES = P_SHZONE --@@Added by Kameswari for the WPBN issue-83894
             AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
             AND QM.IE_FLAG = 'E'
             AND UPPER(CHARGE_AT) = 'PICKUP'
             AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
             AND QR.BUYRATE_ID = V_BUYRATEID
             AND QM.ID = (SELECT MAX(QM.ID)
                            FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                           WHERE QM.ID = IQR.QUOTE_ID
                             AND QM.ORIGIN_LOCATION = P_ORIGIN
                             AND QM.CUSTOMER_ID = P_CUSTOMERID
                             AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                             AND QM.SHIPPERZONES = P_SHZONE --@@Added by Kameswari for the WPBN issue-83894
                             AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                                 P_SHPPR_CNSL_TYPE
                             AND QM.IE_FLAG = 'E'
                             AND UPPER(CHARGE_AT) = 'PICKUP');
      ELSE
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           SELLCHARGEID,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           ZONE,
           RATE_INDICATOR)
          SELECT DISTINCT QR.CHARGE_AT,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.SELLRATE_ID,
                                 'BC',
                                 QR.BUYRATE_ID),
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.BUYRATE_ID,
                                 'BC',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          DECODE(QR.BREAK_POINT,
                                 'MIN',
                                 'PER SHIPMENT',
                                 (SELECT DISTINCT BASIS_DESCRIPTION
                                    FROM QMS_CARTAGE_BUYDTL     CBD,
                                         QMS_CHARGE_BASISMASTER CBM
                                   WHERE CBM.CHARGEBASIS = CBD.CHARGE_BASIS
                                     AND CBD.CARTAGE_ID = QR.BUYRATE_ID
                                     AND CBD.ZONE_CODE = QM.SHIPPERZONES
                                     AND UPPER(CBD.CHARGE_TYPE) = 'PICKUP')),
                          (SELECT DISTINCT CURRENCY
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT DENSITY_CODE
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          (SELECT DISTINCT EFFECTIVE_FROM
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          (SELECT DISTINCT VALID_UPTO
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.SHIPPERZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'PICKUP'),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT DISTINCT QBC.WEIGHT_BREAK
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT QBC.RATE_TYPE
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          QM.SHIPPERZONES,
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_CARTAGE_BUYDTL
                            WHERE CARTAGE_ID = QR.BUYRATE_ID
                              AND ZONE_CODE = QM.SHIPPERZONES
                              AND CHARGE_TYPE = 'Pickup'
                              AND LINE_NO = QR.LINE_NO)
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.ORIGIN_LOCATION = P_ORIGIN
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.SHIPPER_MODE = P_SHIPPER_MODE
             AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') = P_SHPPR_CNSL_TYPE
             AND QM.SHIPPERZONES = P_SHZONE --@@Added by Kameswari for the WPBN issue-83894
             AND QM.IE_FLAG = 'E'
             AND UPPER(CHARGE_AT) = 'PICKUP'
             AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
             AND QR.BUYRATE_ID = V_BUYRATEID
             AND QR.SELLRATE_ID = V_SELLRATEID
             AND QM.ID = (SELECT MAX(QM.ID)
                            FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                           WHERE QM.ID = IQR.QUOTE_ID
                             AND QM.ORIGIN_LOCATION = P_ORIGIN
                             AND QM.CUSTOMER_ID = P_CUSTOMERID
                             AND QM.SHIPPER_MODE = P_SHIPPER_MODE
                             AND NVL(QM.SHIPPER_CONSOLE_TYPE, '~') =
                                 P_SHPPR_CNSL_TYPE
                             AND QM.SHIPPERZONES = P_SHZONE --@@Added by Kameswari for the WPBN issue-83894
                             AND QM.IE_FLAG = 'E'
                             AND UPPER(CHARGE_AT) = 'PICKUP');
      END IF;
    END LOOP;
    CLOSE V_CURR;
    -- For Destination delivery Charges
    IF V_OPR_ADM_FLAG <> 'H' THEN
      OPEN V_CURR FOR
        SELECT DISTINCT QR.SELLRATE_ID   SELLRATEID,
                        QR.BUYRATE_ID    BUYRATEID,
                        QR.SELL_BUY_FLAG SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.DEST_LOCATION = P_DESTINATION
           AND QM.CONSIGNEEZONES = P_CONSZONE
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') = P_CNSGNEE_CNSL_TYPE
           AND QM.TERMINAL_ID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = P_TERMINALID
                UNION
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H'
                UNION
                SELECT P_TERMINALID TERM_ID
                  FROM DUAL
                UNION
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = P_TERMINALID)
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
           AND UPPER(CHARGE_AT) = 'DELIVERY'
           AND QM.ID =
               (SELECT MAX(QM.ID)
                  FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = IQR.QUOTE_ID
                   AND QM.DEST_LOCATION = P_DESTINATION
                   AND QM.CONSIGNEEZONES = P_CONSZONE
                   AND QM.CUSTOMER_ID = P_CUSTOMERID
                   AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
                   AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                       P_CNSGNEE_CNSL_TYPE
                   AND QM.IE_FLAG = 'E'
                   AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                   AND UPPER(CHARGE_AT) = 'DELIVERY'
                   AND QM.TERMINAL_ID IN
                       (SELECT PARENT_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY CHILD_TERMINAL_ID = PRIOR
                                   PARENT_TERMINAL_ID
                         START WITH CHILD_TERMINAL_ID = P_TERMINALID
                        UNION
                        SELECT TERMINALID TERM_ID
                          FROM FS_FR_TERMINALMASTER
                         WHERE OPER_ADMIN_FLAG = 'H'
                        UNION
                        SELECT P_TERMINALID TERM_ID
                          FROM DUAL
                        UNION
                        SELECT CHILD_TERMINAL_ID TERM_ID
                          FROM FS_FR_TERMINAL_REGN
                        CONNECT BY PRIOR
                                    CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                         START WITH PARENT_TERMINAL_ID = P_TERMINALID));
      /*Union All
      Select Distinct Qr.Sellrate_Id   Sellrateid,
                      Qr.Buyrate_Id    Buyrateid,
                      Qr.Sell_Buy_Flag Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Dest_Location = p_Destination
         And Qm.Consigneezones = p_Conszone
         And Qm.Customer_Id = p_Customerid
            --AND qm.shipment_mode = p_shmode
         And Qm.Consignee_Mode = p_Cnsgnee_Mode
         And nvl(Qm.Consignee_Console_Type,'~') = p_Cnsgnee_Cnsl_Type
         And Qm.Terminal_Id In
             (Select Parent_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Child_Terminal_Id = Prior Parent_Terminal_Id
               Start With Child_Terminal_Id = p_Terminalid
              Union
              Select Terminalid Term_Id
                From Fs_Fr_Terminalmaster
               Where Oper_Admin_Flag = 'H'
              Union
              Select p_Terminalid Term_Id
                From Dual
              Union
              Select Child_Terminal_Id Term_Id
                From Fs_Fr_Terminal_Regn
              Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
               Start With Parent_Terminal_Id = p_Terminalid)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'BC'
         And Upper(Charge_At) In ('DELIVERY')
            --and (qm.dest_location,qm.CONSIGNEEZONES,qr.sellrate_id,qr.buyrate_id)
         And (Qr.Buyrate_Id, Qm.Consigneezones, Qr.Charge_At) Not In
             (Select Cm.Cartage_Id, Csd.Zone_Code, Csd.Charge_Type
                From Qms_Cartage_Buysellcharges Cm,
                     Qms_Cartage_Selldtl        Csd
               Where Cm.Location_Id = p_Destination
                 And Csd.Zone_Code = p_Conszone
                 And Cm.Shipment_Mode = p_Cnsgnee_Mode
                 And nvl(Cm.Console_Type,'~') = p_Cnsgnee_Cnsl_Type
                 And Upper(Csd.Charge_Type) = 'DELIVERY'
                 And Cm.Cartage_Id = Csd.Cartage_Id
                 And (Csd.Activeinactive Is Null Or
                     Csd.Activeinactive = 'A')
                 And Cm.Terminalid In
                     (Select Parent_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Child_Terminal_Id = Prior
                                 Parent_Terminal_Id
                       Start With Child_Terminal_Id = p_Terminalid
                      Union
                      Select Terminalid Term_Id
                        From Fs_Fr_Terminalmaster
                       Where Oper_Admin_Flag = 'H'
                      Union
                      Select p_Terminalid Term_Id
                        From Dual
                      Union
                      Select Child_Terminal_Id Term_Id
                        From Fs_Fr_Terminal_Regn
                      Connect By Prior
                                  Child_Terminal_Id = Parent_Terminal_Id
                       Start With Parent_Terminal_Id = p_Terminalid));*/
    ELSE
      OPEN V_CURR FOR
        SELECT DISTINCT QR.SELLRATE_ID   SELLRATEID,
                        QR.BUYRATE_ID    BUYRATEID,
                        QR.SELL_BUY_FLAG SELL_BUY_FLAG
          FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
         WHERE QM.ID = QR.QUOTE_ID
           AND QM.DEST_LOCATION = P_DESTINATION
           AND QM.CONSIGNEEZONES = P_CONSZONE
           AND QM.CUSTOMER_ID = P_CUSTOMERID
           AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') = P_CNSGNEE_CNSL_TYPE
           AND QM.IE_FLAG = 'E'
           AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
           AND UPPER(CHARGE_AT) = 'DELIVERY'
           AND QM.ID = (SELECT MAX(QM.ID)
                          FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                         WHERE QM.ID = IQR.QUOTE_ID
                           AND QM.DEST_LOCATION = P_DESTINATION
                           AND QM.CONSIGNEEZONES = P_CONSZONE
                           AND QM.CUSTOMER_ID = P_CUSTOMERID
                           AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
                           AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                               P_CNSGNEE_CNSL_TYPE
                           AND QM.IE_FLAG = 'E'
                           AND IQR.SELL_BUY_FLAG IN ('BC', 'SC')
                           AND UPPER(CHARGE_AT) = 'DELIVERY');
      /*Union All
      Select Distinct Qr.Sellrate_Id   Sellrateid,
                      Qr.Buyrate_Id    Buyrateid,
                      Qr.Sell_Buy_Flag Sell_Buy_Flag
        From Qms_Quote_Rates Qr, Qms_Quote_Master Qm
       Where Qm.Id = Qr.Quote_Id
         And Qm.Dest_Location = p_Destination
         And Qm.Consigneezones = p_Conszone
         And Qm.Customer_Id = p_Customerid
            --AND qm.shipment_mode = p_shmode
         And Qm.Consignee_Mode = p_Cnsgnee_Mode
         And nvl(Qm.Consignee_Console_Type,'~') = p_Cnsgnee_Cnsl_Type
         And Qm.Terminal_Id In
             (Select Distinct Terminalid From Fs_Fr_Terminalmaster)
         And Qm.Ie_Flag = 'E'
         And Qr.Sell_Buy_Flag = 'BC'
         And Upper(Charge_At) In ('DELIVERY')
         And (Qr.Buyrate_Id, Qm.Consigneezones, Qr.Charge_At) Not In
             (Select Cm.Cartage_Id, Csd.Zone_Code, Csd.Charge_Type
                From Qms_Cartage_Buysellcharges Cm,
                     Qms_Cartage_Selldtl        Csd
               Where Cm.Location_Id = p_Destination
                 And Csd.Zone_Code = p_Conszone
                 And Cm.Shipment_Mode = p_Cnsgnee_Mode
                 And nvl(Cm.Console_Type,'~') = p_Cnsgnee_Cnsl_Type
                 And Upper(Csd.Charge_Type) = 'DELIVERY'
                 And Cm.Cartage_Id = Csd.Cartage_Id
                 And (Csd.Activeinactive Is Null Or
                     Csd.Activeinactive = 'A')
                 And Cm.Terminalid In
                     (Select Terminalid From Fs_Fr_Terminalmaster));*/
    END IF;
    LOOP
      FETCH V_CURR
        INTO V_SELLRATEID, V_BUYRATEID, V_SELL_BUY_FLAG;
      EXIT WHEN V_CURR%NOTFOUND;
      IF V_SELL_BUY_FLAG = 'BC' THEN
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           SELLCHARGEID,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           ZONE,
           RATE_INDICATOR)
          SELECT DISTINCT QR.CHARGE_AT,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.SELLRATE_ID,
                                 'BC',
                                 QR.BUYRATE_ID),
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.BUYRATE_ID,
                                 'BC',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          DECODE(QR.BREAK_POINT,
                                 'MIN',
                                 'PER SHIPMENT',
                                 (SELECT DISTINCT BASIS_DESCRIPTION
                                    FROM QMS_CARTAGE_BUYDTL     CBD,
                                         QMS_CHARGE_BASISMASTER CBM
                                   WHERE CBM.CHARGEBASIS = CBD.CHARGE_BASIS
                                     AND CBD.CARTAGE_ID = QR.BUYRATE_ID
                                     AND CBD.ZONE_CODE = QM.CONSIGNEEZONES
                                     AND UPPER(CBD.CHARGE_TYPE) = 'DELIVERY')),
                          (SELECT DISTINCT CURRENCY
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT DENSITY_CODE
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          (SELECT DISTINCT EFFECTIVE_FROM
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          (SELECT DISTINCT VALID_UPTO
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT DISTINCT QBC.WEIGHT_BREAK
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT QBC.RATE_TYPE
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          QM.CONSIGNEEZONES,
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_CARTAGE_BUYDTL
                            WHERE CARTAGE_ID = QR.BUYRATE_ID
                              AND ZONE_CODE = QM.CONSIGNEEZONES
                              AND CHARGE_TYPE = 'Delivery'
                              AND LINE_NO = QR.LINE_NO
                              AND ROWNUM = 1)
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.DEST_LOCATION = P_DESTINATION
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
             AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') = P_CNSGNEE_CNSL_TYPE
             AND QM.CONSIGNEEZONES = P_CONSZONE --@@Added by Kameswari for the WPBN issue-83894
             AND QM.IE_FLAG = 'E'
             AND UPPER(CHARGE_AT) = 'DELIVERY'
             AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
             AND QR.BUYRATE_ID = V_BUYRATEID
             AND QM.ID = (SELECT MAX(QM.ID)
                            FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                           WHERE QM.ID = IQR.QUOTE_ID
                             AND QM.DEST_LOCATION = P_DESTINATION
                             AND QM.CUSTOMER_ID = P_CUSTOMERID
                             AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
                             AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                                 P_CNSGNEE_CNSL_TYPE
                             AND QM.CONSIGNEEZONES = P_CONSZONE --@@Added by Kameswari for the WPBN issue-83894
                             AND QM.IE_FLAG = 'E'
                             AND UPPER(CHARGE_AT) = 'DELIVERY');
      ELSE
        INSERT INTO TEMP_CHARGES
          (COST_INCURREDAT,
           SELLCHARGEID,
           CHARGESLAB,
           BUYRATE,
           SELLRATE,
           MARGIN_TYPE,
           MARGINVALUE,
           SEL_BUY_FLAG,
           BUY_CHARGE_ID,
           TERMINALID,
           CHARGEBASIS,
           CURRENCY,
           DENSITY_RATIO,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           MARGIN_DISCOUNT_FLAG,
           LINE_NO,
           WEIGHT_BREAK,
           RATE_TYPE,
           ZONE,
           RATE_INDICATOR)
          SELECT DISTINCT QR.CHARGE_AT,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.SELLRATE_ID,
                                 'BC',
                                 QR.BUYRATE_ID),
                          QR.BREAK_POINT,
                          QR.BUY_RATE,
                          QR.R_SELL_RATE SELLRATE,
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN_TYPE,
                                 QR.DISCOUNT_TYPE),
                          DECODE(NVL(QR.MARGIN_DISCOUNT_FLAG, 'M'),
                                 'M',
                                 QR.MARGIN,
                                 QR.DISCOUNT),
                          QR.SELL_BUY_FLAG,
                          DECODE(QR.SELL_BUY_FLAG,
                                 'SC',
                                 QR.BUYRATE_ID,
                                 'BC',
                                 ''),
                          (SELECT DISTINCT TERMINALID
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          DECODE(QR.BREAK_POINT,
                                 'MIN',
                                 'PER SHIPMENT',
                                 (SELECT DISTINCT BASIS_DESCRIPTION
                                    FROM QMS_CARTAGE_BUYDTL     CBD,
                                         QMS_CHARGE_BASISMASTER CBM
                                   WHERE CBM.CHARGEBASIS = CBD.CHARGE_BASIS
                                     AND CBD.CARTAGE_ID = QR.BUYRATE_ID
                                     AND CBD.ZONE_CODE = QM.CONSIGNEEZONES
                                     AND UPPER(CBD.CHARGE_TYPE) = 'DELIVERY')),
                          (SELECT DISTINCT CURRENCY
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT DENSITY_CODE
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          (SELECT DISTINCT EFFECTIVE_FROM
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          (SELECT DISTINCT VALID_UPTO
                             FROM QMS_CARTAGE_BUYDTL QBD
                            WHERE QBD.CARTAGE_ID = QR.BUYRATE_ID
                              AND QBD.ZONE_CODE = QM.CONSIGNEEZONES
                              AND UPPER(QBD.CHARGE_TYPE) = 'DELIVERY'),
                          'T',
                          QR.MARGIN_DISCOUNT_FLAG,
                          QR.LINE_NO,
                          (SELECT DISTINCT QBC.WEIGHT_BREAK
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          (SELECT DISTINCT QBC.RATE_TYPE
                             FROM QMS_CARTAGE_BUYSELLCHARGES QBC
                            WHERE QBC.CARTAGE_ID = QR.BUYRATE_ID),
                          QM.CONSIGNEEZONES,
                          (SELECT CHARGERATE_INDICATOR
                             FROM QMS_CARTAGE_BUYDTL
                            WHERE CARTAGE_ID = QR.BUYRATE_ID
                              AND ZONE_CODE = QM.CONSIGNEEZONES
                              AND CHARGE_TYPE = 'Delivery'
                              AND LINE_NO = QR.LINE_NO
                              AND ROWNUM = 1)
            FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
           WHERE QM.ID = QR.QUOTE_ID
             AND QM.DEST_LOCATION = P_DESTINATION
             AND QM.CUSTOMER_ID = P_CUSTOMERID
             AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
             AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') = P_CNSGNEE_CNSL_TYPE
             AND QM.CONSIGNEEZONES = P_CONSZONE --@@Added by Kameswari for the WPBN issue-83894
             AND QM.IE_FLAG = 'E'
             AND UPPER(CHARGE_AT) = 'DELIVERY'
             AND QR.SELL_BUY_FLAG IN ('BC', 'SC')
             AND QR.BUYRATE_ID = V_BUYRATEID
             AND QR.SELLRATE_ID = V_SELLRATEID
             AND QM.ID = (SELECT MAX(QM.ID)
                            FROM QMS_QUOTE_RATES IQR, QMS_QUOTE_MASTER QM
                           WHERE QM.ID = IQR.QUOTE_ID
                             AND QM.DEST_LOCATION = P_DESTINATION
                             AND QM.CUSTOMER_ID = P_CUSTOMERID
                             AND QM.CONSIGNEE_MODE = P_CNSGNEE_MODE
                             AND NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~') =
                                 P_CNSGNEE_CNSL_TYPE
                             AND QM.CONSIGNEEZONES = P_CONSZONE --@@Added by Kameswari for the WPBN issue-83894
                             AND QM.IE_FLAG = 'E'
                             AND UPPER(CHARGE_AT) = 'DELIVERY');
      END IF;
    END LOOP;
    CLOSE V_CURR;
  END;

  /*
    This Procedure is used to get the count for updated quotes grouped on the change description
    and based on the terminal Ids.

    The IN Parameter is the Logged in Terminal Id
    The OUT Parameter is a resultset object
  */
  PROCEDURE QMS_UPDATED_QUOTES_COUNT(P_TERMINALID VARCHAR2,
                                     P_USERID     VARCHAR2,
                                     P_EMPID      VARCHAR2,
                                     P_RS         OUT RESULTSET) AS
    V_OPR_ADM_FLAG VARCHAR2(10);
    --v_terminals      VARCHAR2 (32767);
    /*CURSOR c1
    IS
       (SELECT p_terminalid term_id
          FROM DUAL
        UNION
        SELECT     child_terminal_id term_id
              FROM fs_fr_terminal_regn
        CONNECT BY PRIOR child_terminal_id = parent_terminal_id
        START WITH parent_terminal_id = p_terminalid);

    CURSOR c2
    IS
       (SELECT DISTINCT terminalid term_id
                   FROM fs_fr_terminalmaster);*/
  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    /*IF v_opr_adm_flag <> 'H'
    THEN
       FOR i IN c1
       LOOP
          v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
       END LOOP;
    ELSE
       FOR i IN c2
       LOOP
          v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
       END LOOP;
    END IF;

    v_terminals := SUBSTR (v_terminals, 2, LENGTH (v_terminals) - 3);*/
    IF P_USERID IS NULL THEN
      IF V_OPR_ADM_FLAG <> 'H' THEN
        OPEN P_RS FOR
          SELECT CHANGEDESC, UPDATED_QUOTES, CONFIRMED_QUOTES
            FROM (SELECT DISTINCT QU.CHANGEDESC,
                                  (SELECT COUNT(*)
                                     FROM QMS_QUOTES_UPDATED A,
                                          QMS_QUOTE_MASTER   B
                                    WHERE B.ID = A.QUOTE_ID
                                      AND A.CHANGEDESC = QU.CHANGEDESC
                                      AND B.TERMINAL_ID IN
                                          (SELECT P_TERMINALID TERM_ID
                                             FROM DUAL
                                           UNION
                                           SELECT CHILD_TERMINAL_ID TERM_ID
                                             FROM FS_FR_TERMINAL_REGN
                                           CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                                       PARENT_TERMINAL_ID
                                            START WITH PARENT_TERMINAL_ID =
                                                       P_TERMINALID)) UPDATED_QUOTES,
                                  (SELECT COUNT(*)
                                     FROM QMS_QUOTES_UPDATED A,
                                          QMS_QUOTE_MASTER   B
                                    WHERE B.ID = A.QUOTE_ID
                                      AND A.CHANGEDESC = QU.CHANGEDESC
                                      AND B.TERMINAL_ID IN
                                          (SELECT P_TERMINALID TERM_ID
                                             FROM DUAL
                                           UNION
                                           SELECT CHILD_TERMINAL_ID TERM_ID
                                             FROM FS_FR_TERMINAL_REGN
                                           CONNECT BY PRIOR CHILD_TERMINAL_ID =
                                                       PARENT_TERMINAL_ID
                                            START WITH PARENT_TERMINAL_ID =
                                                       P_TERMINALID)
                                      AND A.CONFIRM_FLAG = 'C') CONFIRMED_QUOTES
                    FROM QMS_QUOTES_UPDATED QU, QMS_QUOTE_MASTER QM
                   WHERE QM.ID = QU.QUOTE_ID
                     AND QM.TERMINAL_ID IN
                         (SELECT P_TERMINALID TERM_ID
                            FROM DUAL
                          UNION
                          SELECT CHILD_TERMINAL_ID TERM_ID
                            FROM FS_FR_TERMINAL_REGN
                          CONNECT BY PRIOR
                                      CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                           START WITH PARENT_TERMINAL_ID = P_TERMINALID))
           WHERE UPDATED_QUOTES <> CONFIRMED_QUOTES;
      ELSE
        OPEN P_RS FOR
          SELECT CHANGEDESC, UPDATED_QUOTES, CONFIRMED_QUOTES
            FROM (SELECT DISTINCT QU.CHANGEDESC,
                                  (SELECT COUNT(*)
                                     FROM QMS_QUOTES_UPDATED A,
                                          QMS_QUOTE_MASTER   B
                                    WHERE B.ID = A.QUOTE_ID
                                      AND A.CHANGEDESC = QU.CHANGEDESC) UPDATED_QUOTES,
                                  (SELECT COUNT(*)
                                     FROM QMS_QUOTES_UPDATED A,
                                          QMS_QUOTE_MASTER   B
                                    WHERE B.ID = A.QUOTE_ID
                                      AND A.CHANGEDESC = QU.CHANGEDESC
                                      AND A.CONFIRM_FLAG = 'C') CONFIRMED_QUOTES
                    FROM QMS_QUOTES_UPDATED QU, QMS_QUOTE_MASTER QM
                   WHERE QM.ID = QU.QUOTE_ID)
           WHERE UPDATED_QUOTES <> CONFIRMED_QUOTES;
      END IF;
    ELSE
      OPEN P_RS FOR
        SELECT CHANGEDESC, UPDATED_QUOTES, CONFIRMED_QUOTES
          FROM (SELECT DISTINCT QU.CHANGEDESC,
                                (SELECT COUNT(*)
                                   FROM QMS_QUOTES_UPDATED A,
                                        QMS_QUOTE_MASTER   B
                                  WHERE B.ID = A.QUOTE_ID
                                    AND A.CHANGEDESC = QU.CHANGEDESC
                                    AND ((B.CREATED_BY = P_USERID AND
                                        B.TERMINAL_ID = P_TERMINALID) OR
                                        B.SALES_PERSON = P_EMPID)) UPDATED_QUOTES,
                                (SELECT COUNT(*)
                                   FROM QMS_QUOTES_UPDATED A,
                                        QMS_QUOTE_MASTER   B
                                  WHERE B.ID = A.QUOTE_ID
                                    AND A.CHANGEDESC = QU.CHANGEDESC
                                    AND ((B.CREATED_BY = P_USERID AND
                                        B.TERMINAL_ID = P_TERMINALID) OR
                                        B.SALES_PERSON = P_EMPID)
                                    AND A.CONFIRM_FLAG = 'C') CONFIRMED_QUOTES
                  FROM QMS_QUOTES_UPDATED QU, QMS_QUOTE_MASTER QM
                 WHERE QM.ID = QU.QUOTE_ID
                   AND ((QM.CREATED_BY = P_USERID AND
                       QM.TERMINAL_ID = P_TERMINALID) OR
                       QM.SALES_PERSON = P_EMPID))
         WHERE UPDATED_QUOTES <> CONFIRMED_QUOTES;
    END IF;
  END QMS_UPDATED_QUOTES_COUNT;

  /*
    This Procedure returns the detailed information about the Quote Ids which have been updated
    based on the selected change description and the logged in Terminal ID.
    This procedure also implements the Paging and Sorting in the report.

    The IN Parameters are
     p_change_desc         VARCHAR2,
     p_terminalid          VARCHAR2,
     p_page_no             NUMBER DEFAULT 1,
     p_page_rows           NUMBER DEFAULT 20,
     sortBy                VARCHAR2,
     sortOrder             VARCHAR2

    The OUT Parameters are
     sortBy                VARCHAR2,
     sortOrder             VARCHAR2,
     p_tot_rec       OUT   NUMBER,
     p_tot_pages     OUT   NUMBER,
     p_rs            OUT   resultset
  */
  PROCEDURE QMS_UPDATED_QUOTES_INFO(P_CHANGE_DESC VARCHAR2,
                                    P_TERMINALID  VARCHAR2,
                                    P_PAGE_NO     NUMBER DEFAULT 1,
                                    P_PAGE_ROWS   NUMBER DEFAULT 20,
                                    SORTBY        VARCHAR2,
                                    SORTORDER     VARCHAR2,
                                    P_USERID      VARCHAR2,
                                    P_EMPID       VARCHAR2,
                                    P_TOT_REC     OUT NUMBER,
                                    P_TOT_PAGES   OUT NUMBER,
                                    P_RS          OUT RESULTSET) AS
    V_OPR_ADM_FLAG VARCHAR2(10);
    V_TERMINALS    VARCHAR2(32000);
    V_TERMINAL_QRY VARCHAR2(500);
    V_ORDERBY      VARCHAR2(50);
    /*CURSOR c1
    IS
       (SELECT p_terminalid term_id
          FROM DUAL
        UNION
        SELECT     child_terminal_id term_id
              FROM FS_FR_TERMINAL_REGN
        CONNECT BY PRIOR child_terminal_id = parent_terminal_id
        START WITH parent_terminal_id = p_terminalid);

    CURSOR c2
    IS
       (SELECT DISTINCT terminalid term_id
                   FROM FS_FR_TERMINALMASTER);*/
  BEGIN
    IF (SORTBY = 'Important') THEN
      V_ORDERBY := ' IU_FLAG ';
    ELSIF (SORTBY = 'CustomerId') THEN
      V_ORDERBY := ' CUSTOMER_ID ';
    ELSIF (SORTBY = 'CustomerName') THEN
      V_ORDERBY := ' COMPANYNAME ';
    ELSIF (SORTBY = 'QuoteId') THEN
      V_ORDERBY := ' QM.QUOTE_ID ';
    ELSIF (SORTBY = 'Mode') THEN
      V_ORDERBY := ' SMODE ';
    ELSIF (SORTBY = 'ServiceLevel') THEN
      V_ORDERBY := ' SERVICE ';
    ELSIF (SORTBY = 'FromCountry') THEN
      V_ORDERBY := ' ORIGIN_COUNTRY ';
    ELSIF (SORTBY = 'FromLocation') THEN
      V_ORDERBY := ' ORIGIN_LOCATION ';
    ELSIF (SORTBY = 'ToCountry') THEN
      V_ORDERBY := ' DEST_COUNTRY ';
    ELSIF (SORTBY = 'ToLocation') THEN
      V_ORDERBY := ' DEST_LOCATION ';
    ELSE
      V_ORDERBY := ' QM.QUOTE_ID ';
    END IF;
    V_ORDERBY := ' ORDER BY ' || V_ORDERBY || SORTORDER;
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    IF V_OPR_ADM_FLAG <> 'H' THEN
      IF P_USERID IS NOT NULL THEN
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_userid', P_USERID);
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_empId', P_EMPID);
        V_TERMINAL_QRY := 'And ((Qm.Created_By =sys_context(''quote_context'',''v_userid'')
				 AND Qm.TERMINAL_ID=sys_context(''quote_context'',''v_terminalid''))
				 OR Qm.SALES_PERSON =sys_context(''quote_context'',''v_empId''))';
      ELSE
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        V_TERMINAL_QRY := ' AND QM.TERMINAL_ID IN (SELECT sys_context(''quote_context'',''v_terminalid'')
				 term_id FROM DUAL UNION SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN
				 CONNECT BY PRIOR child_terminal_id = parent_terminal_id
				 START WITH parent_terminal_id =sys_context(''quote_context'',''v_terminalid''))';
      END IF;
    ELSE
      IF P_USERID IS NOT NULL THEN
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_userid', P_USERID);
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_empId', P_EMPID);
        V_TERMINAL_QRY := 'And ((Qm.Created_By =sys_context(''quote_context'',''v_userid'')
				 AND Qm.TERMINAL_ID=sys_context(''quote_context'',''v_terminalid''))
				 OR Qm.SALES_PERSON =sys_context(''quote_context'',''v_empId''))';
      ELSE
        V_TERMINAL_QRY := '';
      END IF;
    END IF;
   /* print_out('select * from (select t1.*, rownum rn from (SELECT QM.IU_FLAG, QM.CUSTOMER_ID, QM.QUOTE_ID,QM.ID,
		    DECODE((SELECT COUNT(*)
			      FROM FS_RT_LEG LG, FS_RT_PLAN RP
			     WHERE RP.QUOTE_ID=QM.QUOTE_ID
			       AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1, QM.SHIPMENT_MODE, 100) SMODE,
		    DECODE((SELECT COUNT(*)
			      FROM FS_RT_LEG LG, FS_RT_PLAN RP
			     WHERE RP.QUOTE_ID=QM.QUOTE_ID
			     AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1,(SELECT   DISTINCT SERVICE_LEVEL
				  FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
				 WHERE BD.BUYRATEID=QR.BUYRATE_ID
				 AND BD.LANE_NO=QR.RATE_LANE_NO
				 AND QR.QUOTE_ID=QM.ID
				 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'') AND BD.LINE_NO IN (''0'')), ''Multi-Modal'') SERVICE,
		    QM.ORIGIN_LOCATION,OLM.COUNTRYID ORIGIN_COUNTRY,QM.DEST_LOCATION,DLM.COUNTRYID DEST_COUNTRY,QU.SELL_BUY_FLAG SELL_BUY_FLAG,QM.EMAIL_FLAG EMAIL_FLAG,QM.FAX_FLAG FAX_FLAG,QM.PRINT_FLAG PRINT_FLAG, C.COMPANYNAME COMPANYNAME,QM.IS_MULTI_QUOTE
		      FROM QMS_QUOTE_MASTER QM,FS_FR_LOCATIONMASTER OLM, FS_FR_LOCATIONMASTER DLM,
		      QMS_QUOTES_UPDATED QU, FS_FR_CUSTOMERMASTER C
		      WHERE QM.ID = QU.QUOTE_ID AND QU.CHANGEDESC=:v_change_desc
		      AND QU.CONFIRM_FLAG IS NULL AND QM.ORIGIN_LOCATION=OLM.LOCATIONID
		AND QM.DEST_LOCATION=DLM.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID ' || V_TERMINAL_QRY || V_ORDERBY || ') t1) t2 where t2.rn <= (:v_page_no - 1) * :v_page_rows + :v_page_rows
	    and t2.rn > (:v_page_no- 1) * :v_page_rows'); */
      print_out('select * from (select t1.*, rownum rn from (SELECT QM.IU_FLAG, QM.CUSTOMER_ID, QM.QUOTE_ID,QM.ID,
		    DECODE((SELECT COUNT(*)
			      FROM FS_RT_LEG LG, FS_RT_PLAN RP
			     WHERE RP.QUOTE_ID=QM.QUOTE_ID
			       AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1, QM.SHIPMENT_MODE, 100) SMODE,
		    DECODE((SELECT COUNT(*)
			       FROM FS_RT_LEG LG, FS_RT_PLAN RP
			      WHERE RP.QUOTE_ID=QM.QUOTE_ID
			     AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1,(SELECT   DISTINCT SERVICE_LEVEL
				  FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
				 WHERE BD.BUYRATEID=QR.BUYRATE_ID
				 AND BD.LANE_NO=QR.RATE_LANE_NO
				 AND QR.QUOTE_ID=QM.ID
				 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'') AND BD.LINE_NO IN (''0'')), ''Multi-Modal'') SERVICE,
		    QM.ORIGIN_LOCATION,OLM.COUNTRYID ORIGIN_COUNTRY,QM.DEST_LOCATION,DLM.COUNTRYID DEST_COUNTRY,QU.SELL_BUY_FLAG SELL_BUY_FLAG,QM.EMAIL_FLAG EMAIL_FLAG,QM.FAX_FLAG FAX_FLAG,QM.PRINT_FLAG PRINT_FLAG, C.COMPANYNAME COMPANYNAME,QM.IS_MULTI_QUOTE
		      FROM QMS_QUOTE_MASTER QM,FS_FR_LOCATIONMASTER OLM, FS_FR_LOCATIONMASTER DLM,
		      QMS_QUOTES_UPDATED QU, FS_FR_CUSTOMERMASTER C
		      WHERE QM.ID = QU.QUOTE_ID AND QU.CHANGEDESC=:v_change_desc
		      AND QU.CONFIRM_FLAG IS NULL AND QM.ORIGIN_LOCATION=OLM.LOCATIONID
		AND QM.DEST_LOCATION=DLM.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID ' || V_TERMINAL_QRY || V_ORDERBY || ') t1) t2 WHERE t2.rn <= (:v_page_no - 1) * :v_page_rows + :v_page_rows
	    AND t2.rn > (:v_page_no- 1) * :v_page_rows');
    OPEN P_RS FOR 'select * from (select t1.*, rownum rn from (SELECT QM.IU_FLAG, QM.CUSTOMER_ID, QM.QUOTE_ID,QM.ID,
		    DECODE((SELECT COUNT(*)
                FROM FS_RT_LEG LG, FS_RT_PLAN RP
               WHERE RP.QUOTE_ID = QM.QUOTE_ID
                 AND LG.RT_PLAN_ID = RP.RT_PLAN_ID),
              1,
              QM.SHIPMENT_MODE,
              DECODE((SELECT DISTINCT QMS.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = QM.QUOTE_ID AND QMS.ID = QU.QUOTE_ID)
             ,''Y'',(SELECT DISTINCT QMS.SHIPMENT_MODE FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = QM.QUOTE_ID
             AND QMS.ID =QM.ID AND QMS.IS_MULTI_QUOTE = ''Y''),100)) SMODE,
		   DECODE((SELECT COUNT(*)
                FROM FS_RT_LEG LG, FS_RT_PLAN RP
               WHERE RP.QUOTE_ID = QM.QUOTE_ID
                 AND LG.RT_PLAN_ID = RP.RT_PLAN_ID),
              1,
              (SELECT DISTINCT SERVICE_LEVEL
                 FROM QMS_BUYRATES_DTL BD, QMS_QUOTE_RATES QR
                WHERE BD.BUYRATEID = QR.BUYRATE_ID
                  AND BD.LANE_NO = QR.RATE_LANE_NO
                  AND BD.VERSION_NO = QR.VERSION_NO
                  AND QR.QUOTE_ID = QM.ID
                  AND QR.SELL_BUY_FLAG IN (''BR'', ''RSR'', ''CSR'')
                  AND BD.LINE_NO IN (''0'')),DECODE((SELECT DISTINCT QMS.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMS
                  WHERE QMS.QUOTE_ID = QM.QUOTE_ID AND QMS.ID = QU.QUOTE_ID ) ,''Y'',(SELECT DISTINCT QMS.MULTI_QUOTE_SERVICE_LEVEL FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = QM.QUOTE_ID
             AND QMS.ID =QM.ID AND QMS.IS_MULTI_QUOTE = ''Y''),''Multi-Modal'')) SERVICE,
		    QM.ORIGIN_LOCATION,OLM.COUNTRYID ORIGIN_COUNTRY,QM.DEST_LOCATION,DLM.COUNTRYID DEST_COUNTRY,QU.SELL_BUY_FLAG SELL_BUY_FLAG,QM.EMAIL_FLAG EMAIL_FLAG,QM.FAX_FLAG FAX_FLAG,QM.PRINT_FLAG PRINT_FLAG, C.COMPANYNAME COMPANYNAME,QM.IS_MULTI_QUOTE
		      FROM QMS_QUOTE_MASTER QM,FS_FR_LOCATIONMASTER OLM, FS_FR_LOCATIONMASTER DLM,
		      QMS_QUOTES_UPDATED QU, FS_FR_CUSTOMERMASTER C
		      WHERE QM.ID = QU.QUOTE_ID AND QU.CHANGEDESC=:v_change_desc
		      AND QU.CONFIRM_FLAG IS NULL AND QM.ORIGIN_LOCATION=OLM.LOCATIONID
		AND QM.DEST_LOCATION=DLM.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID ' || V_TERMINAL_QRY || V_ORDERBY || ') t1) t2 WHERE t2.rn <= (:v_page_no - 1) * :v_page_rows + :v_page_rows
	    AND t2.rn > (:v_page_no- 1) * :v_page_rows'
      USING P_CHANGE_DESC, P_PAGE_NO, P_PAGE_ROWS, P_PAGE_ROWS, P_PAGE_NO, P_PAGE_ROWS;
    /*END IF;*/
    /*IF v_opr_adm_flag = 'H'
    THEN*/
    EXECUTE IMMEDIATE ('select COUNT(*) from (SELECT QM.ID FROM QMS_QUOTES_UPDATED QU, QMS_QUOTE_MASTER QM
				   WHERE QU.CHANGEDESC=:v_change_desc
				   AND QU.QUOTE_ID=QM.ID
				   AND QU.CONFIRM_FLAG  IS NULL ' ||
                      V_TERMINAL_QRY || ' )')
      INTO P_TOT_REC
      USING P_CHANGE_DESC;
    /*SELECT CEIL ((p_tot_rec / p_page_rows))
    INTO p_tot_pages
    FROM DUAL;*/
    P_TOT_PAGES := CEIL(P_TOT_REC / P_PAGE_ROWS);
  END QMS_UPDATED_QUOTES_INFO;

  /*
       This Procedure returns the detailed information about the Quote Ids which have been updated
       based on the selected change description and the logged-in Terminal ID.
       Same as the previous procedure, except that paging or sorting is not needed as the output would be in Excel Format.

       The IN Parameters are
        p_change_desc         VARCHAR2,
        p_terminalid          VARCHAR2

       The OUT Parameter is
        p_rs            OUT   resultset
  */
  PROCEDURE QMS_UPDATED_QUOTES_INFO_EXCEL(P_CHANGE_DESC VARCHAR2,
                                          P_TERMINALID  VARCHAR2,
                                          P_USERID      VARCHAR2,
                                          P_EMPID       VARCHAR2,
                                          P_RS          OUT RESULTSET) AS
    V_OPR_ADM_FLAG VARCHAR2(10);
    V_TERMINALS    VARCHAR2(32000);
    V_TERMINAL_QRY VARCHAR2(500);
    /*CURSOR c1
    IS
       (SELECT p_terminalid term_id
          FROM DUAL
        UNION
        SELECT     child_terminal_id term_id
              FROM FS_FR_TERMINAL_REGN
        CONNECT BY PRIOR child_terminal_id = parent_terminal_id
        START WITH parent_terminal_id = p_terminalid);

    CURSOR c2
    IS
       (SELECT DISTINCT terminalid term_id
                   FROM FS_FR_TERMINALMASTER);*/
  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINALID;
    IF V_OPR_ADM_FLAG <> 'H' THEN
      IF P_USERID IS NOT NULL THEN
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_userid', P_USERID);
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_empId', P_EMPID);
        V_TERMINAL_QRY := 'And ((Qm.Created_By =sys_context(''quote_context'',''v_userid'')
				 AND Qm.TERMINAL_ID=sys_context(''quote_context'',''v_terminalid''))
				 OR Qm.SALES_PERSON =sys_context(''quote_context'',''v_empId''))';
      ELSE
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        V_TERMINAL_QRY := ' AND QM.TERMINAL_ID IN (SELECT sys_context(''quote_context'',''v_terminalid'')
				 term_id FROM DUAL UNION SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN
				 CONNECT BY PRIOR child_terminal_id = parent_terminal_id
				 START WITH parent_terminal_id =sys_context(''quote_context'',''v_terminalid''))';
      END IF;
    ELSE
      IF P_USERID IS NOT NULL THEN
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_userid', P_USERID);
        DBMS_SESSION.SET_CONTEXT('quote_context',
                                 'v_terminalid',
                                 P_TERMINALID);
        DBMS_SESSION.SET_CONTEXT('quote_context', 'v_empId', P_EMPID);
        V_TERMINAL_QRY := 'And ((Qm.Created_By =sys_context(''quote_context'',''v_userid'')
				 AND Qm.TERMINAL_ID=sys_context(''quote_context'',''v_terminalid''))
				 OR Qm.SALES_PERSON =sys_context(''quote_context'',''v_empId''))';
      ELSE
        V_TERMINAL_QRY := '';
      END IF;
    END IF;
    --v_terminals := SUBSTR (v_terminals, 2, LENGTH (v_terminals) - 3);
    --Modified by Kameswari for the WPBN issue-30313
    OPEN P_RS FOR 'SELECT QM.IU_FLAG, QM.CUSTOMER_ID, QM.QUOTE_ID,QM.ID,
		    DECODE((SELECT COUNT(*)
			      FROM FS_RT_LEG LG, FS_RT_PLAN RP
			     WHERE RP.QUOTE_ID=QM.QUOTE_ID
			       AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1, QM.SHIPMENT_MODE, 100) SMODE,
		    DECODE((SELECT COUNT(*)
			      FROM FS_RT_LEG LG, FS_RT_PLAN RP
			     WHERE RP.QUOTE_ID=QM.QUOTE_ID
			     AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
			     1,(SELECT  DISTINCT SERVICE_LEVEL
				  FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
				 WHERE BD.BUYRATEID=QR.BUYRATE_ID
				 AND BD.LANE_NO=QR.RATE_LANE_NO
				 AND QR.QUOTE_ID=QM.ID
				   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'') AND BD.LINE_NO IN (''0'')), ''Multi-Modal'') SERVICE,
		    QM.ORIGIN_LOCATION,QM.ORIGIN_LOCATION,OLM.COUNTRYID ORIGIN_COUNTRY,QM.DEST_LOCATION,DLM.COUNTRYID DEST_COUNTRY,
		    QU.SELL_BUY_FLAG SELL_BUY_FLAG,QM.EMAIL_FLAG EMAIL_FLAG,QM.FAX_FLAG FAX_FLAG,QM.PRINT_FLAG PRINT_FLAG, C.COMPANYNAME COMPANYNAME
        ,QM.CREATED_BY,QM.SALES_PERSON,   TO_CHAR(QM.CREATED_DATE, ''DD-MON-YYYY'') CREATED_DATE
		      FROM QMS_QUOTE_MASTER QM,FS_FR_LOCATIONMASTER OLM, FS_FR_LOCATIONMASTER DLM,
		      QMS_QUOTES_UPDATED QU, FS_FR_CUSTOMERMASTER C
		      WHERE QM.ID = QU.QUOTE_ID AND QU.CHANGEDESC=:v_change_desc
		      AND QU.CONFIRM_FLAG IS NULL AND QM.ORIGIN_LOCATION=OLM.LOCATIONID
		AND QM.DEST_LOCATION=DLM.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID ' || V_TERMINAL_QRY || ' ORDER BY QM.QUOTE_ID'
      USING P_CHANGE_DESC;
    --@@End of WPBN issue-30313
  END QMS_UPDATED_QUOTES_INFO_EXCEL;

  /*
       This Procedure is called from qms_quotepack_new.qms_updated_modify_quote when the user clicks on a Quote ID from the
       updated quotes report to modify the updated quote. This returns 4 resultset objects detailing
       the rate info and notes of a particular quote. This is different from quote_view_proc as
       the data is fetched from QMS_QUOTE_RATES_TEMP(global temporary table) containing updated rates (updated in qms_quotepack_new.qms_updated_modify_quote)
       instead of the old rates (QMS_QUOTE_RATES).

       The IN Parameter is:
        p_quoteid         NUMBER

       The OUT Parameters are:
        p_rs        OUT   resultset,
        p_rs1       OUT   resultset,
        p_rs2       OUT   resultset,
        p_rs3       OUT   resultset
  */
  PROCEDURE updated_quote_info_modify(p_quoteid     VARCHAR2,
                                      p_Sellbuyflag VARCHAR2, --modified by subrahmanyam for 146971 , 146970
                                      p_terminalid  VARCHAR2,
                                     -- p_unique_id   number, --kishore
                                      p_rs          OUT resultset,
                                      p_rs1         OUT resultset,
                                      p_rs2         OUT resultset,
                                      p_rs3         OUT resultset) AS
    v_id NUMBER;
    v_terminalid VARCHAR2(4000);
    v_sql1 VARCHAR2(4000);
    v_sql2 VARCHAR2(4000);
    v_sql3 VARCHAR2(4000);
    v_sql4 VARCHAR2(4000);
    v_sql5 VARCHAR2(4000);
    v_sql6 VARCHAR2(4000);
    v_sql7 VARCHAR2(4000);
    v_sql8 VARCHAR2(4000);
    -------------------For Ordering the Charges---------------
    v_lane_no NUMBER(5) := 1;
    -----------------------------------------------------------
    v_Count       NUMBER(3);
    v_weightbreak VARCHAR2(100) := '';
    v_Shmode      VARCHAR2(100) := '';
    v_count1      NUMBER(3);
    v_flag        VARCHAR2(10);
  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('truncate table temp_charges');

    --v_id := p_unique_id; --kishore
    SELECT ID
      INTO v_id
      FROM QMS_QUOTE_MASTER
     WHERE quote_id = p_quoteid
       AND version_no = (SELECT MAX(version_no)
                           FROM QMS_QUOTE_MASTER
                          WHERE quote_id = p_quoteid);
    SELECT ID,terminal_id
          INTO v_id , v_terminalid
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
                     version_no,
                     charge_id,
                     charge_description,
                     NVL(margin_discount_flag, 'M') margin_discount_flag,
                     margin_type,
                     NVL(margin, 0) margin,
                     discount_type,
                     NVL(discount, 0) discount,
                     notes,
                     quote_refno,
                     break_point,
                     charge_at,
                     buy_rate,
                     r_sell_rate,
                     rt_plan_id,
                     serial_no,
                     ID,
                     Rate_Description,
                     frequency,
                     carrier,
                     transit_time,
                     rate_validity,
                     frequency_checked,
                     carrier_checked,
                     transit_checked,
                     validity_checked,
                     change_flag --@@Added by Kameswari for the WPBN issue-154398
                FROM QMS_QUOTE_RATES_TEMP
               WHERE quote_id = v_id) LOOP
      IF UPPER(i.sell_buy_flag) = 'BR' THEN

        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,version_no,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,Rate_Description,Console_type,carrier,frequency_checked,carrier_checked,transittime_checked,ratevalidity_checked)
                SELECT DISTINCT ';
        v_sql2 := ':v_charge_at ,qbd.weight_break_slab, qbm.currency,qbd.chargerate,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),decode(:v_margin_disc_flag,''M'',:v_margin),' ||
                  '''Per ''' || '||' || 'qbm.uom,:v_sell_buy_flag';
        IF p_Sellbuyflag = 'BR' THEN
          v_sql5 := ',qbm.buyrateid,qbm.version_no,qbd.effective_date, qbd.valid_upto, qbm.terminalid,qbm.weight_break, qbm.weight_class, qbm.rate_type,qbd.frequency, qbd.transit_time, qbd.notes,' ||
                    ' qbm.uom, FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,qbd.LOWERBOUND,qbd.UPPERBOUND,qbd.CHARGERATE_INDICATOR,:v_margin_disc_flag' ||
                    ',qbd.lane_no, qbd.line_no,qbd.rate_description,qbm.console_type,qbd.carrier_Id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked  FROM qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_rates_TEMP qr ,qms_quote_master qm,FS_RT_LEG FRL,FS_RT_PLAN FRP,  Qms_Quotes_Updated  qu WHERE  qu.quote_id=qr.quote_id And qu.old_buycharge_id=qr.buyrate_id And qu.old_lane_no=qr.rate_lane_no And qu.new_buycharge_id=qbd.buyrateid  And qu.new_lane_no=qbd.lane_no  And qu.new_version_no=qbd.version_no And qm.quote_id=FRP.quote_id and qm.id=qr.quote_id  and qbd.buyrateid = qbm.buyrateid And qbd.version_no = qbm.version_no AND (qbd.lane_no=qbm.lane_no or qbm.lane_no is null) and qbd.buyrateid = ';
        ELSE
          v_sql5 := ',qbm.buyrateid,qbm.version_no,qbd.effective_date, qbd.valid_upto, qbm.terminalid,qbm.weight_break, qbm.weight_class, qbm.rate_type,qbd.frequency, qbd.transit_time, qbd.notes,' ||
                    ' qbm.uom, FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,qbd.LOWERBOUND,qbd.UPPERBOUND,qbd.CHARGERATE_INDICATOR,:v_margin_disc_flag' ||
                    ',qbd.lane_no, qbd.line_no,qbd.rate_description,qbm.console_type,qbd.carrier_Id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked  FROM qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_rates_TEMP qr ,qms_quote_master qm,FS_RT_LEG FRL,FS_RT_PLAN FRP,  Qms_Quotes_Updated  qu WHERE  qu.quote_id=qr.quote_id    And qm.quote_id=FRP.quote_id and qm.id=qr.quote_id  and qr.buyrate_id=qbd.buyrateid and qr.rate_lane_no=qbd.lane_no and qr.version_no=qbd.version_No and qbd.buyrateid = qbm.buyrateid And qbd.version_no = qbm.version_no AND (qbd.lane_no=qbm.lane_no or qbm.lane_no is null) AND qbd.buyrateid = ';

        END IF;
        /*   v_sql6 := ':v_buy_rate_id and qbd.service_level not in (''SCH'')AND qbd.lane_no =:v_rate_lane_no AND qbd.version_no=qbm.version_no and qbd.version_no=:v_versionno AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
                       AND FRL.SERIAL_NO=:v_sl_no AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id And (qbd.chargerate not in (''0.0'') or qbd.chargerate not in (''0'')) ';
        */
        /**v_sql6 := ':v_buy_rate_id AND qbd.lane_no =:v_rate_lane_no AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
          AND FRL.SERIAL_NO=:v_sl_no and qbd.service_level not in (''SCH'') AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id And qbd.Chargerate not in
        (Select Bd.Chargerate
           from Qms_Buyrates_Dtl Bd
          where Bd.Buyrateid = qbd.buyrateid
            And Bd.Buyrateid = qbm.Buyrateid
            And Bd.Lane_No = qbd.lane_no
            And Bd.Rate_Description not in (''A FREIGHT RATE'', null)
            And Bd.Chargerate in (''0.0'', ''0'')) ';*/ --@@Commented and Modified by Kameswari for the WPBN issue-136658

        v_sql6 := ':v_buy_rate_id AND qbd.lane_no =:v_rate_lane_no AND qbd.version_no=:v_versionno and QBD.WEIGHT_BREAK_SLAB=qr.break_point AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
                   AND FRL.SERIAL_NO=:v_sl_no /*and qbd.service_level not in (''SCH'')*/ AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND qbd.Chargerate NOT IN
                 (SELECT Bd.Chargerate
                    FROM QMS_BUYRATES_DTL Bd
                   WHERE Bd.Buyrateid = qbd.buyrateid
                     AND Bd.Buyrateid = qbm.Buyrateid
                     AND Bd.version_no=qbd.version_no
                     AND Bd.version_no=qbm.version_no
                      AND (Bd.Lane_No=qbm.Lane_No OR qbm.Lane_No IS NULL)--@@Added by Kameswari on 04/02/09
                     AND Bd.Lane_No = qbd.lane_no
                     AND ( Bd.Rate_Description NOT IN (''A FREIGHT RATE'') OR Bd.Rate_Description IS NOT NULL)
                     AND Bd.Chargerate IN (''0.0'', ''0''))
                 AND qbd.rate_description =:v_rate_description'; --kishore for quote view(update) duplicates break

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)

          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.margin, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.rate_lane_no, i.version_no, i.break_point, i.serial_no, p_quoteid,i.rate_description; --kishore for quote view(update) duplicates break
      ELSIF (UPPER(i.sell_buy_flag) = 'RSR' OR
            UPPER(i.sell_buy_flag) = 'CSR') THEN

        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,version_no,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,SELLCHARGEID,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,rate_description,console_type,carrier,frequency_checked,carrier_checked,transittime_checked,ratevalidity_checked)
                SELECT  DISTINCT ';
        v_sql2 := ':v_charge_at ,RSD.WEIGHTBREAKSLAB,RSM.currency,RSD.buy_rate_amt,';
        v_sql3 := '';
        v_sql4 := ' RSD.chargerate ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                  DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_disc),''Per ''' || '||' ||
                  'qbm.uom,:v_sell_buy_flag';
        IF p_Sellbuyflag = 'RSR' OR p_Sellbuyflag = 'CSR' THEN
          /*
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                     ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                            rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_master qm,qms_quote_rates qr,qms_quotes_updated qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  And qu.old_buycharge_id=qr.buyrate_id  And qu.old_sellcharge_id=qr.sellrate_id And qu.old_lane_no=qr.rate_lane_no And qu.new_buycharge_id =qbd.buyrateid and qu.new_lane_no=qbd.lane_no and qu.new_version_no =qbd.version_no and qu.new_sellcharge_id =rsd.rec_con_id and RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';
          */
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                    ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                         rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd,QMS_QUOTE_MASTER qm,QMS_QUOTE_RATES_TEMP qr,QMS_QUOTES_UPDATED qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  AND qu.old_buycharge_id=qr.buyrate_id  AND  qu.old_lane_no=qr.rate_lane_no AND qu.new_buycharge_id =qbd.buyrateid AND qu.new_lane_no=qbd.lane_no AND qu.new_version_no =qbd.version_no AND qu.new_sellcharge_id =rsd.rec_con_id AND RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';

        ELSE
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                    ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                  rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd,QMS_QUOTE_MASTER qm,QMS_QUOTE_RATES_TEMP qr,QMS_QUOTES_UPDATED qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  AND qbd.buyrateid=qr.buyrate_id  AND rsd.rec_con_id=qr.sellrate_id AND qbd.lane_no=qr.rate_lane_no AND RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';
        END IF;
        /*  v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no
                     AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID  And qbd.version_no = qbm.version_no
                      AND RSD.WEIGHTBREAKSLAB=:v_break_point  and qbd.service_level not in (''SCH'') and FRL.SERIAL_NO=:v_serial_no   And rsd.version_no=qbd.version_no
                     AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id  And (rsd.Chargerate not in (''0.0'') or rsd.Chargerate not in ('''')) ';
        */
        /*v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no
          AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID
           AND RSD.WEIGHTBREAKSLAB=:v_break_point   and FRL.SERIAL_NO=:v_serial_no
          AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id  And rsd.Chargerate not in
        (Select Sd.Chargerate
           from qms_rec_con_sellratesdtl Sd
          where Sd.Rec_Con_Id = RSD.REC_CON_ID
            And Sd.Buyrateid = RSD.BUYRATEID
            And Sd.Lane_No = RSD.LANE_NO
            And Sd.Rec_Con_Id = RSM.Rec_Con_Id
            And Sd.Rate_Description not in (''A FREIGHT RATE'', null)
            And Sd.Chargerate in (''0.0'', ''0'')) ';*/ --@@Commented and Modified by Kameswari for the WPBN issue-136658
        v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no AND rsd.version_no=:v_versionno
                   AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID AND qbm.version_no=qbd.version_no AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND qm.quote_id=frp.quote_id AND qr.quote_id=qm.id AND qr.break_point=qbd.weight_break_slab
                    AND   QBD.WEIGHT_BREAK_SLAB=qr.break_point AND QBD.Weight_Break_Slab= RSD.WEIGHTBREAKSLAB AND QBD.WEIGHT_BREAK_SLAB =:v_break_point /*and qbd.service_level not in (''SCH'')*/   AND FRL.SERIAL_NO=:v_serial_no
                   AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND rsd.version_no=qbd.version_no AND rsd.Chargerate NOT IN
                 (SELECT Sd.Chargerate
                    FROM QMS_REC_CON_SELLRATESDTL Sd
                   WHERE Sd.Rec_Con_Id = RSD.REC_CON_ID
                     AND Sd.Buyrateid = RSD.BUYRATEID
                     AND Sd.Lane_No = RSD.LANE_NO
                     AND Sd.version_no=RSD.version_no
                     AND Sd.Rec_Con_Id = RSM.Rec_Con_Id
                     AND ( Sd.Rate_Description NOT IN (''A FREIGHT RATE'') OR Sd.Rate_Description IS NOT NULL)
                     AND Sd.Chargerate IN (''0.0'', ''0''))
                     AND rsd.rate_description =:v_rate_description'; --kishore for quote view(update) duplicates break


       EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.margin_discount_flag, i.sellrate_id, i.rate_lane_no, i.version_no, i.break_point, i.serial_no, p_quoteid,i.charge_description;--i.rate_description; --kishore for quote view(update) duplicates break
      ELSIF UPPER(i.sell_buy_flag) = 'SBR' THEN

        /*      v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab,Currency,buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto,terminalid,weight_break,weight_scale, rate_type,primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, MARGIN_DISCOUNT_FLAG,LANE_NO, line_no)
        SELECT  distinct ';*/
        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab,Currency,buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto,terminalid,weight_break,weight_scale, rate_type,primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,rate_description)
         SELECT  DISTINCT ';
        v_sql2 := ':v_charge_at ,QSB.WEIGHT_BREAK_SLAB,QSB.CURRENCYID,QSB.CHARGE_RATE,';
        v_sql3 := '';
        v_sql4 := ' 0.0 ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                    DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount)' ||
                  ',''Per ''' || '||' || 'QSB.UOM,:v_sell_buy_flag';
        v_sql5 := ',''X'',QM.EFFECTIVE_DATE, QM.VALID_TO, QM.TERMINAL_ID,QSB.WEIGHT_BREAK, ''G'', QSB.WEIGHT_BREAK,' ||
                  ' QSB.uom,FRL.ORIG_LOC, FRL.DEST_LOC, FRL.SHPMNT_MODE, QSB.SERVICELEVEL, FRL.SERIAL_NO, QSB.DENSITY_CODE, QSB.LOWER_BOUND, QSB.UPPER_BOUND,:v_margin_disc_flag, QSB.LANE_NO,QSB.LINE_NO,QSB.RATE_DESCRIPTION FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_SPOTRATES QSB,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE  QSB.QUOTE_ID=QM.ID';
        v_sql6 := ' AND QM.ID=:v_quote_id AND QSB.WEIGHT_BREAK_SLAB=:v_break_point AND FRL.SERIAL_NO=:v_serial_no
                   AND Qsb.Lane_No = (Frl.Serial_No - 1) AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND (QSB.CHARGE_RATE NOT IN (''0.0'') OR QSB.CHARGE_RATE NOT IN (''0'')) ';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.Discount, i.Sell_Buy_Flag, i.margin_discount_flag, i.quote_id, i.break_point, i.Serial_No, p_quoteid;
      ELSIF UPPER(i.sell_buy_flag) = 'B' THEN

        /*v_sql1 := 'INSERT INTO temp_charges (CHARGE_ID, CHARGEDESCID,cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME)
        SELECT distinct ';*/ --@@Modified by Kameswari for the WPBN issue-154398 on 21/02/09
        v_sql1 := 'INSERT INTO temp_charges (CHANGE_FLAG,CHARGE_ID, CHARGEDESCID,cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME)
               SELECT DISTINCT :v_changeflag,';
        v_sql2 := ':v_charge_id,:v_charge_desc_id,:v_charge_at ,qcd.CHARGESLAB, qcm.CURRENCY,qcd.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),decode(:v_margin_disc_flag,''M'',:v_margin),
                  (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) uom,:v_sell_buy_flag';
        v_sql5 := ',qcm.BUYSELLCHARGEID, qcm.TERMINALID,qcm.RATE_BREAK, qcm.WEIGHT_CLASS, qcm.rate_type,' ||
                  ' (select PRIMARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) primary_basis,
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) SECONDARY_BASIS,
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) TERTIARY_BASIS,
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) BLOCK,
                    Qcm.DENSITY_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND, qcd.CHARGERATE_INDICATOR,:v_margin_disc_flag, qcd.lane_no,';
        /* v_sql8 := '(SELECT REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc_id
                        AND INACTIVATE=''N'')INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                        AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'')EXT_NAME FROM QMS_BUYSELLCHARGESMASTER qcm,QMS_BUYCHARGESDTL qcd WHERE qcm.BUYSELLCHARGEID=';
        */
        v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc_id
                    AND INACTIVATE=''N''
               AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  ''' ))INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                    AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N''
                  AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = ' || '''' ||
                  p_terminalid ||
                  ''' ))EXT_NAME FROM QMS_BUYSELLCHARGESMASTER qcm,QMS_BUYCHARGESDTL qcd WHERE qcm.BUYSELLCHARGEID=';

        v_sql6 := ':v_buy_rate_id AND Qcd.CHARGESLAB=:v_break_point
                    AND QCM.BUYSELLCHARGEID=QCD.BUYSELLCHAEGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql8 || v_sql6)
          USING i.change_flag, i.charge_id, i.charge_description, i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.Margin, i.sell_buy_flag, i.margin_discount_flag, i.charge_id, i.charge_description, i.charge_id, i.charge_description, i.buyrate_id, i.break_point;

      ELSIF UPPER(i.sell_buy_flag) = 'S' THEN

        /* v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, CHARGE_ID, CHARGEDESCID,COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME )
        SELECT distinct ';*/ --@@Modified by Kameswari for the WPBN issue-154398 on 21/02/09
        v_sql1 := 'INSERT INTO temp_charges (CHANGE_FLAG,SELLCHARGEID, CHARGE_ID, CHARGEDESCID,COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME )
               SELECT DISTINCT :v_changeflag,';
        v_sql2 := ':v_sell_rate_id,:v_charge_id,:v_charge_desc,:v_charge_at,
                  QSd.CHARGESLAB, qsm.CURRENCY,qsd.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := 'decode(qsm.margin_type,''M'',qsd.chargerate+(qsd.chargerate*qsd.marginvalue*0.01),qsd.chargerate+qsd.marginvalue),
                      DECODE(:v_margin_disc_flag,''M'',:v_margin_type,:v_discount_type),
                      DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount),
                      (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) basis,:v_sell_buy_flag';
        v_sql5 := ',(SELECT BUYSELLCHARGEID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                   AND CHARGEDESCID=:v_charge_desc AND DEL_FLAG=''N'' AND TERMINALID =qsm.TERMINALID), qsm.TERMINALID,qsm.RATE_BREAK, qsm.WEIGHT_CLASS, qsm.rate_type,
                    (SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) primary_basis,
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) SECONDARY_BASIS,
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) TERTIARY_BASIS,
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) BLOCK,
                    (SELECT DENSITY_CODE FROM QMS_BUYSELLCHARGESMASTER WHERE BUYSELLCHARGEID=QSM.BUYCHARGEID), qsd.LOWERBOUND,
                    qsd.UPPERBOUND, qsd.CHARGERATE_INDICATOR,:v_margin_disc_flag, qsd.lane_no,';

        /*v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc
        AND INACTIVATE=''N''))INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
        AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'' ))EXT_NAME FROM QMS_SELLCHARGESDTL QSD,QMS_SELLCHARGESMASTER QSM WHERE qsm.SELLCHARGEID=';*/

        v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc
                    AND INACTIVATE=''N''
                    AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  '''))INT_NAME,(SELECT  EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                    AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'' AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  '''))EXT_NAME FROM QMS_SELLCHARGESDTL QSD,QMS_SELLCHARGESMASTER QSM WHERE qsm.SELLCHARGEID=';

        v_sql6 := ':v_sell_rate_id AND Qsd.CHARGESLAB=:v_break_point AND QsM.SELLCHARGEID=QsD.SELLCHARGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql8 || v_sql6)
          USING i.change_flag, i.sellrate_id, i.charge_id, i.charge_description, i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.charge_id, i.charge_description, i.margin_discount_flag, i.charge_id, i.charge_description, i.charge_id, i.charge_description, i.sellrate_id, i.break_point;

      ELSIF UPPER(i.sell_buy_flag) = 'BC' THEN

        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QCD.ZONE_CODE =QM.SHIPPERZONES AND QCD.CHARGE_TYPE=''Pickup'' AND QCM.SHIPMENT_MODE=QM.SHIPPER_MODE AND nvl(QCM.CONSOLE_TYPE,''~'')=NVL(QM.SHIPPER_CONSOLE_TYPE,''~'') ';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QCD.ZONE_CODE =QM.CONSIGNEEZONES AND QCD.CHARGE_TYPE=''Delivery'' AND QCM.SHIPMENT_MODE=QM.CONSIGNEE_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.CONSIGNEE_CONSOLE_TYPE,''~'') ';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS,BLOCK,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no) SELECT distinct ';
        v_sql2 := ':v_charge_at,QCD.CHARGESLAB, QCM.CURRENCY,QCD.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),
                     DECODE(:v_margin_disc_flag,''M'',:v_margin),
                     (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS),:v_sell_buy_flag';
        v_sql5 := ',QCD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,
                    (SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',QCD.DENSITY_CODE,QCD.ZONE_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND,qcd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                   qcd.line_no FROM QMS_CARTAGE_BUYDTL QCD,QMS_CARTAGE_BUYSELLCHARGES QCM,QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QCD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := ':v_buy_rate_id AND QCD.CHARGESLAB=:v_break_point AND QM.ID=:v_quote_id' ||
                  v_sql7;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.margin, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.break_point, i.quote_id;

      ELSIF UPPER(i.sell_buy_flag) = 'SC' THEN

        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QSD.ZONE_CODE =QM.SHIPPERZONES AND QSD.CHARGE_TYPE=''Pickup'' AND QCM.SHIPMENT_MODE=QM.SHIPPER_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.SHIPPER_CONSOLE_TYPE,''~'') ' ||
                    ' AND QSD.SELL_CARTAGE_ID=:v_sell_rate_id';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QSD.ZONE_CODE =QM.CONSIGNEEZONES AND QSD.CHARGE_TYPE=''Delivery'' AND QCM.SHIPMENT_MODE=QM.CONSIGNEE_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.CONSIGNEE_CONSOLE_TYPE,''~'') ' ||
                    ' AND QSD.SELL_CARTAGE_ID=:v_sell_rate_id';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, COST_INCURREDAT, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS,BLOCK,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no)
                SELECT DISTINCT ';
        v_sql2 := ':v_sell_rate_id,:v_charge_at,QSD.CHARGESLAB, QCM.CURRENCY,QSD.BUYRATE_AMT,';
        v_sql3 := '';
        v_sql4 := 'QSD.CHARGERATE ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                                   DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount),
                (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),:v_sell_buy_flag';
        v_sql5 := ',QSD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,
                    (SELECT Primary_Basis FROM QMS_CHARGE_BASISMASTER WHERE Chargebasis = Qsd.Charge_Basis),
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                     QCD.DENSITY_CODE,QSD.ZONE_CODE,qsd.LOWERBOUND, qsd.UPPERBOUND,QSD.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                    qsd.line_no FROM QMS_CARTAGE_SELLDTL QSD, QMS_CARTAGE_BUYSELLCHARGES QCM, QMS_CARTAGE_BUYDTL QCD, QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QSD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := ':v_buy_rate_id AND QSD.CHARGESLAB=:v_break_point
                  AND QSD.CARTAGE_ID=QCD.CARTAGE_ID AND QCD.CHARGE_TYPE=QSD.CHARGE_TYPE AND QCD.ZONE_CODE=QSD.ZONE_CODE
                  AND QM.ID=:v_quote_id' || v_sql7;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.sellrate_id, i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.break_point, i.quote_id, i.sellrate_id;
      END IF;
    END LOOP;

    UPDATE TEMP_CHARGES SET selected_flag = 'Y';

    ----------------------------For Ordering Charges--------------------------------
    FOR i IN (SELECT Chargegroupid
                FROM QMS_QUOTE_CHARGEGROUPDTL
               WHERE Quote_Id = v_id
               ORDER BY Id) LOOP
      FOR j IN (SELECT Charge_Id, Chargedescid, Terminalid, Id
                  FROM QMS_CHARGE_GROUPSMASTER
                 WHERE Chargegroup_Id = i.Chargegroupid
                   AND Inactivate = 'N') LOOP

        EXECUTE IMMEDIATE ('UPDATE TEMP_CHARGES SET LANE_NO =:v_lane_no,ID=:v_id
                               WHERE CHARGE_ID=:v_charge_id AND CHARGEDESCID =:v_charge_desc_id
                               AND TERMINALID=:v_terminal_id')
          USING v_lane_no, j.Id, j.Charge_Id, j.Chargedescid, j.Terminalid;
      END LOOP;

      v_Lane_No := v_Lane_No + 1;

    END LOOP;
    ------------------------------------------------------------------------------------

    OPEN p_rs FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BR'',''RSR'',''CSR'',''SBR'') order by LEG_SL_NO, line_no';

    OPEN p_rs1 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''B'',''S'') order by LANE_NO,id,BUY_CHARGE_ID,COST_INCURREDAT, line_no';

    OPEN p_rs2 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BC'',''SC'') order by BUY_CHARGE_ID,COST_INCURREDAT, line_no';

    OPEN p_rs3 FOR 'SELECT EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID= ' || v_id;
    /* EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.put_line(SQLERRM);*/
  END;

  PROCEDURE MULTI_QUOTE_VIEW_PROC(P_QUOTEID VARCHAR2,
                                  --modified by subrahmanyam for the wpbn id:146971
                                  P_RS  OUT RESULTSET,
                                  P_RS1 OUT RESULTSET,
                                  P_RS2 OUT RESULTSET,
                                  P_RS3 OUT RESULTSET) AS
    V_ID NUMBER;
    -------------------For Ordering the Charges---------------
    V_LANE_NO     NUMBER(3) := 1;
    V_COUNT       NUMBER(3);
    V_COUNT1      NUMBER(3);
    V_WEIGHTBREAK VARCHAR2(100) := '';
    V_SHMODE      VARCHAR2(100) := '';
    -----------------------------------------------------------
  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');

    FOR LANE IN (SELECT ID
                   FROM QMS_QUOTE_MASTER
                  WHERE QUOTE_ID = P_QUOTEID
                    AND VERSION_NO =
                        (SELECT MAX(VERSION_NO)
                           FROM QMS_QUOTE_MASTER
                          WHERE QUOTE_ID = P_QUOTEID)) LOOP
      --Added by Mohan
      FOR I IN (SELECT DISTINCT QUOTE_ID,
                                SELL_BUY_FLAG,
                                BUYRATE_ID,
                                SELLRATE_ID,
                                RATE_LANE_NO,
                                VERSION_NO,
                                --@@Added for the WPBN issues-146448,146968 on 19/12/08
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
                                --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                                CARRIER,
                                FREQUENCY_CHECKED,
                                CARRIER_CHECKED,
                                -- SERVICE_CHECKED,-- kiran.v
                                TRANSIT_CHECKED,
                                VALIDITY_CHECKED,
                                zone_code -- Added by Kishore FOr Quote View PDF Duplicates
                  FROM QMS_QUOTE_RATES
                 WHERE QUOTE_ID = LANE.ID) --Added by Mohan
       LOOP
        IF UPPER(I.SELL_BUY_FLAG) = 'BR' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             VERSION_NO,
             --@@Added for the WPBN issues-146448,146968 on 19/12/08
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             FREQUENCY,
             TRANSITTIME,
             NOTES,
             PRIMARY_BASIS,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             RATE_INDICATOR,
             --@@Added by Kameswari for the WPBN issue-146448
             CARRIER,
             FREQUENCY_CHECKED,
             CARRIER_CHECKED,
              --SERVICE_CHECKED,-- kiran.v
             TRANSITTIME_CHECKED,
             RATEVALIDITY_CHECKED,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             CONSOLE_TYPE)
            SELECT I.CHARGE_AT,
                   QBD.WEIGHT_BREAK_SLAB,
                   /*QBM.CURRENCY*/
                   DECODE(QBD.RATE_DESCRIPTION,
                          'A FREIGHT RATE',
                          QBM.CURRENCY,
                          QBD.SUR_CHARGE_CURRENCY) CURRENCY,
                   QBD.CHARGERATE,
                   0,
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                   'Per ' || QBM.UOM,
                   I.SELL_BUY_FLAG,
                   QBM.BUYRATEID,
                   QBM.VERSION_NO,
                   --@@Added for the WPBN issues-146448,146968 on 19/12/08
                   QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                   (SELECT DISTINCT BD.VALID_UPTO
                      FROM QMS_BUYRATES_DTL BD
                     WHERE BD.BUYRATEID = I.BUYRATE_ID
                       AND BD.LANE_NO = I.RATE_LANE_NO
                       AND BD.VERSION_NO =
                           (SELECT MAX(BD1.VERSION_NO)
                              FROM QMS_BUYRATES_DTL BD1
                             WHERE BD1.BUYRATEID = BD.BUYRATEID
                               AND BD1.LANE_NO = BD.LANE_NO)),
                   QBM.TERMINALID,
                   QBM.WEIGHT_BREAK,
                   QBM.WEIGHT_CLASS,
                   QBM.RATE_TYPE,
                   INITCAP(QBD.FREQUENCY),
                   QBD.TRANSIT_TIME,
                   QBD.NOTES,
                   QBM.UOM,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   QBM.SHIPMENT_MODE,
                   QBD.SERVICE_LEVEL,
                   FRL.SERIAL_NO,
                   QBD.DENSITY_CODE,
                   QBD.LOWERBOUND,
                   QBD.UPPERBOUND,
                   QBD.CHARGERATE_INDICATOR,
                   --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                   --(SELECT CARRIERNAME
                    --  FROM FS_FR_CAMASTER
                    -- WHERE CARRIERID = QBD.CARRIER_ID),
                   --@@Modified by kameswari on 18/02/09
                   QBD.CARRIER_ID,-- kiran.v
                   I.FREQUENCY_CHECKED,
                   I.CARRIER_CHECKED,
                  -- I.SERVICE_CHECKED,-- kiran.v
                   I.TRANSIT_CHECKED,
                   I.VALIDITY_CHECKED,
                   I.MARGIN_DISCOUNT_FLAG,
                   QBD.LANE_NO,
                   QBD.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(QBD.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          QBD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                   QBM.CONSOLE_TYPE --@@Added by Kameswari for the WPBN issue-112348
              FROM QMS_BUYRATES_MASTER QBM,
                   QMS_BUYRATES_DTL    QBD,
                   FS_RT_LEG           FRL,
                   FS_RT_PLAN          FRP
             WHERE QBD.BUYRATEID = QBM.BUYRATEID
               AND QBD.VERSION_NO = QBM.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
               AND QBD.BUYRATEID = I.BUYRATE_ID
               AND QBD.LANE_NO = I.RATE_LANE_NO
               AND QBD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND QBD.WEIGHT_BREAK_SLAB = I.BREAK_POINT
               AND QBD.RATE_DESCRIPTION = I.CHARGE_DESCRIPTION
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND (QBD.CHARGERATE NOT IN ('0.0') OR
                   QBD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
               AND FRP.QUOTE_ID = P_QUOTEID;
        ELSIF (UPPER(I.SELL_BUY_FLAG) = 'RSR' OR
              UPPER(I.SELL_BUY_FLAG) = 'CSR') THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             VERSION_NO,
             --@@Added for the WPBN issues-146448,146968 on 19/12/08
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             FREQUENCY,
             TRANSITTIME,
             NOTES,
             PRIMARY_BASIS,
             SELLCHARGEID,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             RATE_INDICATOR,
             --@@Added by Kameswari for the WPBN issue-146448
             CARRIER,
             FREQUENCY_CHECKED,
             CARRIER_CHECKED,
             --SERVICE_CHECKED,-- kiran.v
             TRANSITTIME_CHECKED,
             RATEVALIDITY_CHECKED,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             CONSOLE_TYPE)
            SELECT I.CHARGE_AT,
                   RSD.WEIGHTBREAKSLAB,
                   /*RSM.CURRENCY,*/
                   DECODE(QBD.RATE_DESCRIPTION,
                          'A FREIGHT RATE',
                          RSM.CURRENCY,
                          QBD.SUR_CHARGE_CURRENCY) CURRENCY,
                   RSD.BUY_RATE_AMT,
                   RSD.CHARGERATE,
                   DECODE(I.MARGIN_DISCOUNT_FLAG,
                          'M',
                          I.MARGIN_TYPE,
                          I.DISCOUNT_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                   'Per ' || QBM.UOM,
                   I.SELL_BUY_FLAG,
                   RSD.BUYRATEID,
                   RSD.VERSION_NO,
                   --@@Added for the WPBN issues-146448,146968 on 19/12/08
                   QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                   (SELECT DISTINCT BD.VALID_UPTO
                      FROM QMS_BUYRATES_DTL BD, QMS_REC_CON_SELLRATESDTL RSD
                     WHERE BD.BUYRATEID = RSD.BUYRATEID
                       AND BD.LANE_NO = RSD.LANE_NO
                       AND BD.BUYRATEID = I.BUYRATE_ID
                       AND BD.LANE_NO = I.RATE_LANE_NO
                       AND RSD.VERSION_NO =
                           (SELECT MAX(VERSION_NO)
                              FROM QMS_REC_CON_SELLRATESDTL
                             WHERE BUYRATEID = I.BUYRATE_ID
                               AND LANE_NO = I.RATE_LANE_NO)
                       AND BD.VERSION_NO = RSD.VERSION_NO),
                   RSM.TERMINALID,
                   RSM.WEIGHT_BREAK,
                   RSM.WEIGHT_CLASS,
                   RSM.RATE_TYPE,
                   INITCAP(RSD.FREQUENCY),
                   RSD.TRANSIT_TIME,
                   RSD.NOTES,
                   QBM.UOM,
                   RSD.REC_CON_ID,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   QBM.SHIPMENT_MODE,
                   QBD.SERVICE_LEVEL,
                   FRL.SERIAL_NO,
                   QBD.DENSITY_CODE,
                   RSD.LOWRER_BOUND,
                   RSD.UPPER_BOUND,
                   RSD.CHARGERATE_INDICATOR,
                   --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                  -- (SELECT CARRIERNAME
                   --   FROM FS_FR_CAMASTER
                   --  WHERE CARRIERID = QBD.CARRIER_ID),
                   --@@Modified by kameswari on 18/02/09
                   QBD.CARRIER_ID,--kiran.v
                   I.FREQUENCY_CHECKED,
                   I.CARRIER_CHECKED,
                  --  I.SERVICE_CHECKED,-- kiran.v
                   I.TRANSIT_CHECKED,
                   I.VALIDITY_CHECKED,
                   I.MARGIN_DISCOUNT_FLAG,
                   RSD.LANE_NO,
                   RSD.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(RSD.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          RSD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                   QBM.CONSOLE_TYPE --@@Added by Kameswari for the WPBN issue-112348
              FROM QMS_REC_CON_SELLRATESMASTER RSM,
                   QMS_REC_CON_SELLRATESDTL    RSD,
                   QMS_BUYRATES_MASTER         QBM,
                   QMS_BUYRATES_DTL            QBD,
                   FS_RT_LEG                   FRL,
                   FS_RT_PLAN                  FRP
             WHERE RSD.REC_CON_ID = RSM.REC_CON_ID
               AND RSD.REC_CON_ID = I.SELLRATE_ID
               AND RSD.LANE_NO = I.RATE_LANE_NO
               AND RSD.BUYRATEID = I.BUYRATE_ID
               AND RSD.BUYRATEID = QBD.BUYRATEID
               AND RSD.LANE_NO = QBD.LANE_NO
               AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
               AND QBD.BUYRATEID = QBM.BUYRATEID
               AND RSD.VERSION_NO = QBD.VERSION_NO
               AND QBD.VERSION_NO = QBM.VERSION_NO
               AND RSD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND RSD.WEIGHTBREAKSLAB = I.BREAK_POINT
               AND RSD.WEIGHTBREAKSLAB = QBD.WEIGHT_BREAK_SLAB --@@Added by Kameswari for Surcharge Enhancements
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND (RSD.CHARGERATE NOT IN ('0.0') OR
                   RSD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
                  -- And Rsd.Ai_Flag='A'
               AND FRP.QUOTE_ID = P_QUOTEID;
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'SBR' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             PRIMARY_BASIS,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             FREQUENCY_CHECKED, --@@ Added by kiran.v 0n 05/09/2011
             CARRIER_CHECKED,
             --SERVICE_CHECKED,-- kiran.v
             TRANSITTIME_CHECKED,
             RATEVALIDITY_CHECKED)
            SELECT I.CHARGE_AT,
                   QSB.WEIGHT_BREAK_SLAB,
                   QSB.CURRENCYID,
                   QSB.CHARGE_RATE,
                   0.0,
                   DECODE(I.MARGIN_DISCOUNT_FLAG,
                          'M',
                          I.MARGIN_TYPE,
                          I.DISCOUNT_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                   'Per ' || QSB.UOM,
                   I.SELL_BUY_FLAG,
                   'X',
                   QM.EFFECTIVE_DATE,
                   QM.VALID_TO,
                   QM.TERMINAL_ID,
                   QSB.WEIGHT_BREAK,
                   'G',
                   QSB.WEIGHT_BREAK,
                   QSB.UOM,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   FRL.SHPMNT_MODE,
                   QSB.SERVICELEVEL,
                   FRL.SERIAL_NO,
                   QSB.DENSITY_CODE,
                   QSB.LOWER_BOUND,
                   QSB.UPPER_BOUND,
                   I.MARGIN_DISCOUNT_FLAG,
                   QSB.LANE_NO,
                   QSB.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(QSB.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          QSB.RATE_DESCRIPTION) RATE_DESCRIPTION,
                           I.FREQUENCY_CHECKED,
                   I.CARRIER_CHECKED,
                  -- I.SERVICE_CHECKED,-- kiran.v
                   I.TRANSIT_CHECKED,
                   I.VALIDITY_CHECKED
              FROM QMS_QUOTE_MASTER    QM,
                   QMS_QUOTE_SPOTRATES QSB,
                   FS_RT_LEG           FRL,
                   FS_RT_PLAN          FRP
             WHERE QSB.QUOTE_ID = QM.ID
               AND QM.ID = I.QUOTE_ID
               AND QSB.WEIGHT_BREAK_SLAB = I.BREAK_POINT
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND FRP.QUOTE_ID = P_QUOTEID
               AND (QSB.CHARGE_RATE NOT IN ('0.0') OR
                   QSB.CHARGE_RATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
              /* AND QSB.LANE_NO = FRL.SERIAL_NO - 1;*/
              AND QSB.LANE_NO = FRL.SERIAL_NO ; -- kiran.v
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'B' THEN
          INSERT INTO TEMP_CHARGES
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
             DEST)
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
                   --@@Added by Kameswari for the WPBN issue-130538
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
                   /*(Select Remarks
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                From Qms_Chargedescmaster
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Where Chargeid = i.Charge_Id
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 And Chargedescid = i.Charge_Description
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 And Inactivate = 'N') Int_Name,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             (Select Ext_Charge_Name
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                From Qms_Chargedescmaster
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               Where Chargeid = i.Charge_Id
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 And Chargedescid = i.Charge_Description
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 And Inactivate = 'N') Ext_Name,*/
                   ---@@Modified by Kameswari for the WPBN issue-87001
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
                   'FREIGHT RATE',
                   (SELECT y.origin_port--Y.Origin_Location --kishore
                      FROM QMS_QUOTE_MASTER Y
                     WHERE y.id = lane.id),
                   (SELECT y.destionation_port --Y.Dest_Location --kishore
                      FROM QMS_QUOTE_MASTER Y
                     WHERE y.id = lane.id)
              FROM QMS_BUYSELLCHARGESMASTER QCM, QMS_BUYCHARGESDTL QCD
             WHERE QCM.BUYSELLCHARGEID = I.BUYRATE_ID
               AND QCD.CHARGESLAB = I.BREAK_POINT
               AND QCM.BUYSELLCHARGEID = QCD.BUYSELLCHAEGEID;
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'S' THEN
          INSERT INTO TEMP_CHARGES
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
             DEST)
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
                   --@@Added by Kameswari for the WPBN issue-130538
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
                   /*(Select Remarks
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                From Qms_Chargedescmaster
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 And Inactivate = 'N') Ext_Name,*/
                   (SELECT REMARKS
                      FROM QMS_CHARGEDESCMASTER  QCD,
                           QMS_SELLCHARGESMASTER SCM --@@Modified by Kameswari for the WPBN issue-87001
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
                   'FREIGHT RATE',
                   (SELECT y.origin_port --Y.Origin_Location --kishore
                      FROM QMS_QUOTE_MASTER Y
                     WHERE y.id = lane.id),
                   (SELECT y.destionation_port --Y.Dest_Location  --kishore
                      FROM QMS_QUOTE_MASTER Y
                     WHERE y.id = lane.id)
              FROM QMS_SELLCHARGESDTL QSD, QMS_SELLCHARGESMASTER QSM
             WHERE QSM.SELLCHARGEID = I.SELLRATE_ID
               AND QSD.CHARGESLAB = I.BREAK_POINT
               AND QSM.SELLCHARGEID = QSD.SELLCHARGEID;
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'BC' THEN
          IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
            INSERT INTO TEMP_CHARGES
              (COST_INCURREDAT,
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
               DEST)
              SELECT DISTINCT I.CHARGE_AT,
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
                              I.CHARGE_DESCRIPTION,--'FREIGHT RATE',
                              (SELECT Y.Origin_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id),
                              (SELECT Y.Dest_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id)
                FROM QMS_CARTAGE_BUYDTL         QCD,
                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                     QMS_QUOTE_MASTER           QM
               WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                 AND QCD.CHARGESLAB = I.BREAK_POINT
                 AND QM.ID = I.QUOTE_ID
                 AND QCM.LOCATION_ID = QM.ORIGIN_LOCATION
                 -- Added by Kishore For Multiple ZoneCodes
                 -- AND QCD.ZONE_CODE = QM.SHIPPERZONES
                AND nvl2(i.zone_code,qcd.zone_code,'~') = NVL(i.zone_code,'~')
                 AND INSTR(QM.SHIPPERZONES,QCD.ZONE_CODE) != 0
                 AND qcd.activeinactive ='A'
                 AND QCD.CHARGE_TYPE = 'Pickup'
                 AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE
                 /*AND NVL(QCM.CONSOLE_TYPE, '~') =
                     NVL(QM.SHIPPER_CONSOLE_TYPE, '~')*/; -- Commented by Gowtham to show Origin and Destination Charges in PDF View
          ELSE
            INSERT INTO TEMP_CHARGES
              (COST_INCURREDAT,
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
               DEST)
              SELECT DISTINCT I.CHARGE_AT,
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
                              I.CHARGE_DESCRIPTION,--'FREIGHT RATE',
                              (SELECT Y.Origin_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id),
                              (SELECT Y.Dest_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id)
                FROM QMS_CARTAGE_BUYDTL         QCD,
                     QMS_CARTAGE_BUYSELLCHARGES QCM,
                     QMS_QUOTE_MASTER           QM
               WHERE QCM.CARTAGE_ID = QCD.CARTAGE_ID
                 AND QCM.CARTAGE_ID = I.BUYRATE_ID
                 AND QCD.CHARGESLAB = I.BREAK_POINT
                 AND QM.ID = I.QUOTE_ID
                 AND QCM.LOCATION_ID = QM.DEST_LOCATION
                 -- Added by Kishore For Multiple ZoneCodes
                 --AND AND QCD.ZONE_CODE = QM.CONSIGNEEZONES
                 AND nvl2(i.zone_code,qcd.zone_code,'~') = NVL(i.zone_code,'~')
                 AND INSTR(QM.CONSIGNEEZONES,QCD.ZONE_CODE) != 0
                 AND qcd.activeinactive ='A'
                 AND QCD.CHARGE_TYPE = 'Delivery'
                 AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE;
               --  AND NVL(QCM.CONSOLE_TYPE, '~') = NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~'); -- Commented by Gowtham to show Origin and Destination Charges in PDF View
          END IF;
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'SC' THEN
          IF UPPER(I.CHARGE_AT) = 'PICKUP' THEN
            INSERT INTO TEMP_CHARGES
              (SELLCHARGEID,
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
               DEST)
              SELECT DISTINCT I.SELLRATE_ID,
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
                              'FREIGHT RATE',
                              (SELECT Y.Origin_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id),
                              (SELECT Y.Dest_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id)
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
                 -- Added by Kishore For Multiple ZoneCodes
                 --AND QSD.ZONE_CODE = QM.SHIPPERZONES
                 AND nvl2(i.zone_code,qcd.zone_code,'~') = NVL(i.zone_code,'~')
                 AND INSTR(QM.SHIPPERZONES,QCD.ZONE_CODE) != 0
                 AND qcd.activeinactive ='A'
                 AND QSD.CHARGE_TYPE = 'Pickup'
                 AND QCM.SHIPMENT_MODE = QM.SHIPPER_MODE
                 /*AND NVL(QCM.CONSOLE_TYPE, '~') =
                     NVL(QM.SHIPPER_CONSOLE_TYPE, '~')*/-- Commented by Gowtham to show Origin and Destination Charges in PDF View
                 AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
          ELSE
            INSERT INTO TEMP_CHARGES
              (SELLCHARGEID,
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
               DEST)
              SELECT DISTINCT I.SELLRATE_ID,
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
                              I.CHARGE_DESCRIPTION, --'FREIGHT RATE',
                              (SELECT Y.Origin_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id),
                              (SELECT Y.Dest_Location
                                 FROM QMS_QUOTE_MASTER Y
                                WHERE y.id = lane.id)
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
                 -- Added by Kishore For Multiple ZoneCodes
                 --AND AND QCD.ZONE_CODE = QM.CONSIGNEEZONES
                 AND nvl2(i.zone_code,qcd.zone_code,'~') = NVL(i.zone_code,'~')
                 AND INSTR(QM.CONSIGNEEZONES,QCD.ZONE_CODE) != 0
                 AND qcd.activeinactive ='A'
                 AND QSD.CHARGE_TYPE = 'Delivery'
                 AND QCM.SHIPMENT_MODE = QM.CONSIGNEE_MODE
                 /*AND NVL(QCM.CONSOLE_TYPE, '~') =
                     NVL(QM.CONSIGNEE_CONSOLE_TYPE, '~')*/-- Commented by Gowtham to show Origin and Destination Charges in PDF View
                 AND QSD.SELL_CARTAGE_ID = I.SELLRATE_ID;
          END IF;
        END IF;
      END LOOP;

      ----------------------------For Ordering Charges--------------------------------
      /*For i In (Select Chargegroupid
                  From Qms_Quote_Chargegroupdtl
                 Where Quote_Id = v_Id
                 Order By Id) Loop
        For j In (Select Charge_Id, Chargedescid, Terminalid, Id
                    From Qms_Charge_Groupsmaster
                   Where Chargegroup_Id = i.Chargegroupid
                     And Inactivate = 'N') Loop
          Update Temp_Charges
             Set Lane_No = v_Lane_No, Id = j.Id
           Where Charge_Id = j.Charge_Id
             And Chargedescid = j.Chargedescid
             And Terminalid = j.Terminalid;

        End Loop;
        v_Lane_No := v_Lane_No + 1;
      End Loop;*/
      FOR I IN (SELECT CHARGEGROUPID
                  FROM QMS_QUOTE_CHARGEGROUPDTL
                 WHERE QUOTE_ID = LANE.ID
                 ORDER BY ID) LOOP
        FOR J IN (SELECT CHARGE_ID, CHARGEDESCID, TERMINALID
                    FROM QMS_CHARGE_GROUPSMASTER
                   WHERE CHARGEGROUP_ID = I.CHARGEGROUPID) LOOP
          UPDATE TEMP_CHARGES
             SET LANE_NO = V_LANE_NO
           WHERE CHARGE_ID = J.CHARGE_ID
             AND CHARGEDESCID = J.CHARGEDESCID
             AND TERMINALID = J.TERMINALID;
        END LOOP;
        V_LANE_NO := V_LANE_NO + 1;
      END LOOP;
      --@@Added by Kameswari for the WPBN issue - 166729 on 08/04/09
      DELETE FROM TEMP_CHARGES
       WHERE CHARGESLAB = 'MAX'
         AND BUYRATE IN ('0.0', '0');

      COMMIT;
    END LOOP;
    --@@WPBN issue - 166729 on 08/04/09
    -------------------------------------------------------------------------------------
    OPEN P_RS FOR
      SELECT  *
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')
       ORDER BY LEG_SL_NO, LINE_NO;
    /* Open p_Rs1 For
    Select *
      From Temp_Charges
     Where Sel_Buy_Flag In ('B', 'S')
     Order By Lane_No, Id, Buy_Charge_Id, Cost_Incurredat, Line_No;*/
    --@@Commented and Modified by Kameswari for the WPBN issue-130538
    OPEN P_RS1 FOR
      SELECT DISTINCT * -- Added by Gowtham
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG IN ('B', 'S')
--          ORDER BY LANE_NO, COST_INCURREDAT, ID, LINE_NO; Commented by govind for the issue 258189
       ORDER BY ORG,DEST,LANE_NO, COST_INCURREDAT, ID, LINE_NO;
  -- modified by Kishore For Duplicates in MultiQuote View PDF
    OPEN P_RS2 FOR
      SELECT DISTINCT * -- added by Gowtham
        FROM TEMP_CHARGES
       WHERE SEL_BUY_FLAG IN ('BC', 'SC')
       ORDER BY ORG,DEST,BUY_CHARGE_ID, COST_INCURREDAT,RATE_DESCRIPTION, LINE_NO;
--       ORDER BY  BUY_CHARGE_ID, COST_INCURREDAT,RATE_DESCRIPTION, LINE_NO;Commented by govind for the issue 258189

   /* OPEN P_RS3 FOR
      SELECT DISTINCT INTERNAL_NOTES, EXTERNAL_NOTES
        FROM QMS_QUOTE_NOTES
       WHERE QUOTE_ID IN
             (SELECT ID
                FROM QMS_QUOTE_MASTER
               WHERE QUOTE_ID = P_QUOTEID
                 AND VERSION_NO = (SELECT MAX(VERSION_NO)
                                     FROM QMS_QUOTE_MASTER
                                    WHERE QUOTE_ID = P_QUOTEID));*/
 --Modified by kiran
OPEN P_RS3 FOR
      SELECT  INTERNAL_NOTES, EXTERNAL_NOTES
        FROM QMS_QUOTE_NOTES
       WHERE QUOTE_ID IN
             (SELECT MAX(ID)
                FROM QMS_QUOTE_MASTER
               WHERE QUOTE_ID = P_QUOTEID
                 AND VERSION_NO = (SELECT MAX(VERSION_NO)
                                     FROM QMS_QUOTE_MASTER
                                    WHERE QUOTE_ID = P_QUOTEID));
  END;
  --Added on 04Feb2011
  PROCEDURE updated_multi_quote_inf_modify(p_id    VARCHAR2,
                                       p_quoteid    VARCHAR2,
                                      p_Sellbuyflag VARCHAR2, --modified by subrahmanyam for 146971 , 146970
                                      p_terminalid  VARCHAR2,
                                     -- p_unique_id   number, --kishore
                                      p_rs          OUT resultset,
                                      p_rs1         OUT resultset,
                                      p_rs2         OUT resultset,
                                      p_rs3         OUT resultset) AS
    v_id NUMBER;
    v_terminalid VARCHAR2(4000);
    v_sql1 VARCHAR2(4000);
    v_sql2 VARCHAR2(4000);
    v_sql3 VARCHAR2(4000);
    v_sql4 VARCHAR2(4000);
    v_sql5 VARCHAR2(4000);
    v_sql6 VARCHAR2(4000);
    v_sql7 VARCHAR2(4000);
    v_sql8 VARCHAR2(4000);
    -------------------For Ordering the Charges---------------
    v_lane_no NUMBER(5) := 1;
    -----------------------------------------------------------
    v_Count       NUMBER(3);
    v_weightbreak VARCHAR2(100) := '';
    v_Shmode      VARCHAR2(100) := '';
    v_count1      NUMBER(3);
    v_flag        VARCHAR2(10);
  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('truncate table temp_charges');

    --v_id := p_unique_id; --kishore
    /*SELECT ID
      INTO v_id
      FROM QMS_QUOTE_MASTER
     WHERE quote_id = p_quoteid
       AND version_no = (SELECT MAX(version_no)
                           FROM QMS_QUOTE_MASTER
                          WHERE quote_id = p_quoteid);*/
    SELECT ID,terminal_id
          INTO v_id , v_terminalid
          FROM QMS_QUOTE_MASTER
         WHERE quote_id = p_quoteid AND id=p_id
           AND version_no = (SELECT MAX(version_no)
                               FROM QMS_QUOTE_MASTER
                              WHERE quote_id = p_quoteid AND id=p_id);



    FOR i IN (SELECT quote_id,
                     sell_buy_flag,
                     buyrate_id,
                     sellrate_id,
                     rate_lane_no,
                     version_no,
                     charge_id,
                     charge_description,
                     NVL(margin_discount_flag, 'M') margin_discount_flag,
                     margin_type,
                     NVL(margin, 0) margin,
                     discount_type,
                     NVL(discount, 0) discount,
                     notes,
                     quote_refno,
                     break_point,
                     charge_at,
                     buy_rate,
                     r_sell_rate,
                     rt_plan_id,
                     serial_no,
                     ID,
                     nvl(Rate_Description,charge_description)Rate_Description,
                     frequency,
                     carrier,
                     transit_time,
                     rate_validity,
                     frequency_checked,
                     carrier_checked,
                      --SERVICE_CHECKED,-- kiran.v
                     transit_checked,
                     validity_checked,
                     change_flag --@@Added by Kameswari for the WPBN issue-154398
                FROM QMS_QUOTE_RATES_TEMP
               WHERE quote_id = v_id) LOOP
      IF UPPER(i.sell_buy_flag) = 'BR' THEN
-- kiran.v
        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,version_no,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,Rate_Description,Console_type,carrier,frequency_checked,carrier_checked,transittime_checked,ratevalidity_checked)
                SELECT DISTINCT ';
        v_sql2 := ':v_charge_at ,qbd.weight_break_slab, qbm.currency,qbd.chargerate,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),decode(:v_margin_disc_flag,''M'',:v_margin),' ||
                  '''Per ''' || '||' || 'qbm.uom,:v_sell_buy_flag';
        IF p_Sellbuyflag = 'BR' THEN
         --kiran.v
          v_sql5 := ',qbm.buyrateid,qbm.version_no,qbd.effective_date, qbd.valid_upto, qbm.terminalid,qbm.weight_break, qbm.weight_class, qbm.rate_type,qbd.frequency, qbd.transit_time, qbd.notes,' ||
                    ' qbm.uom, FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,qbd.LOWERBOUND,qbd.UPPERBOUND,qbd.CHARGERATE_INDICATOR,:v_margin_disc_flag' ||
                    ',qbd.lane_no, qbd.line_no,qbd.rate_description,qbm.console_type,qbd.carrier_Id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked  FROM qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_rates_TEMP qr ,qms_quote_master qm,FS_RT_LEG FRL,FS_RT_PLAN FRP,  Qms_Quotes_Updated  qu WHERE  qu.quote_id=qr.quote_id And qu.old_buycharge_id=qr.buyrate_id And qu.old_lane_no=qr.rate_lane_no And qu.new_buycharge_id=qbd.buyrateid  And qu.new_lane_no=qbd.lane_no  And qu.new_version_no=qbd.version_no And qm.quote_id=FRP.quote_id and qm.id=qr.quote_id  and qbd.buyrateid = qbm.buyrateid And qbd.version_no = qbm.version_no AND (qbd.lane_no=qbm.lane_no or qbm.lane_no is null) and qbd.buyrateid = ';
        ELSE
          v_sql5 := ',qbm.buyrateid,qbm.version_no,qbd.effective_date, qbd.valid_upto, qbm.terminalid,qbm.weight_break, qbm.weight_class, qbm.rate_type,qbd.frequency, qbd.transit_time, qbd.notes,' ||
                    ' qbm.uom, FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,qbd.LOWERBOUND,qbd.UPPERBOUND,qbd.CHARGERATE_INDICATOR,:v_margin_disc_flag' ||
                    ',qbd.lane_no, qbd.line_no,qbd.rate_description,qbm.console_type,qbd.carrier_Id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked  FROM qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_rates_TEMP qr ,qms_quote_master qm,FS_RT_LEG FRL,FS_RT_PLAN FRP,  Qms_Quotes_Updated  qu WHERE  qu.quote_id=qr.quote_id    And qm.quote_id=FRP.quote_id and qm.id=qr.quote_id  and qr.buyrate_id=qbd.buyrateid and qr.rate_lane_no=qbd.lane_no and qr.version_no=qbd.version_No and qbd.buyrateid = qbm.buyrateid And qbd.version_no = qbm.version_no AND (qbd.lane_no=qbm.lane_no or qbm.lane_no is null) AND qbd.buyrateid = ';

        END IF;
        /*   v_sql6 := ':v_buy_rate_id and qbd.service_level not in (''SCH'')AND qbd.lane_no =:v_rate_lane_no AND qbd.version_no=qbm.version_no and qbd.version_no=:v_versionno AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
                       AND FRL.SERIAL_NO=:v_sl_no AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id And (qbd.chargerate not in (''0.0'') or qbd.chargerate not in (''0'')) ';
        */
        /**v_sql6 := ':v_buy_rate_id AND qbd.lane_no =:v_rate_lane_no AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
          AND FRL.SERIAL_NO=:v_sl_no and qbd.service_level not in (''SCH'') AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id And qbd.Chargerate not in
        (Select Bd.Chargerate
           from Qms_Buyrates_Dtl Bd
          where Bd.Buyrateid = qbd.buyrateid
            And Bd.Buyrateid = qbm.Buyrateid
            And Bd.Lane_No = qbd.lane_no
            And Bd.Rate_Description not in (''A FREIGHT RATE'', null)
            And Bd.Chargerate in (''0.0'', ''0'')) ';*/ --@@Commented and Modified by Kameswari for the WPBN issue-136658

        v_sql6 := ':v_buy_rate_id AND qbd.lane_no =:v_rate_lane_no AND qbd.version_no=:v_versionno and QBD.WEIGHT_BREAK_SLAB=qr.break_point AND QBD.WEIGHT_BREAK_SLAB=:v_break_point
                   AND FRL.SERIAL_NO=:v_sl_no /*and qbd.service_level not in (''SCH'')*/ AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND qbd.Chargerate NOT IN
                 (SELECT Bd.Chargerate
                    FROM QMS_BUYRATES_DTL Bd
                   WHERE Bd.Buyrateid = qbd.buyrateid
                     AND Bd.Buyrateid = qbm.Buyrateid
                     AND Bd.version_no=qbd.version_no
                     AND Bd.version_no=qbm.version_no
                      AND (Bd.Lane_No=qbm.Lane_No OR qbm.Lane_No IS NULL)--@@Added by Kameswari on 04/02/09
                     AND Bd.Lane_No = qbd.lane_no
                     AND ( Bd.Rate_Description NOT IN (''A FREIGHT RATE'') OR Bd.Rate_Description IS NOT NULL)
                     AND Bd.Chargerate IN (''0.0'', ''0''))
                 AND qbd.rate_description =:v_rate_description'; --kishore for quote view(update) duplicates break

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)

          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.margin, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.rate_lane_no, i.version_no, i.break_point, i.serial_no, p_quoteid,i.rate_description; --kishore for quote view(update) duplicates break
      ELSIF (UPPER(i.sell_buy_flag) = 'RSR' OR
            UPPER(i.sell_buy_flag) = 'CSR') THEN
-- kiran.v
        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,version_no,efrom, validupto, terminalid, weight_break,weight_scale, rate_type, frequency, transittime,notes, primary_basis,SELLCHARGEID,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,rate_description,console_type,carrier,frequency_checked,carrier_checked,transittime_checked,ratevalidity_checked)
                SELECT  DISTINCT ';
        v_sql2 := ':v_charge_at ,RSD.WEIGHTBREAKSLAB,RSM.currency,RSD.buy_rate_amt,';
        v_sql3 := '';
        v_sql4 := ' RSD.chargerate ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                  DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_disc),''Per ''' || '||' ||
                  'qbm.uom,:v_sell_buy_flag';
        IF p_Sellbuyflag = 'RSR' OR p_Sellbuyflag = 'CSR' THEN
          /*
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                     ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                            rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,qms_buyrates_master qbm, qms_buyrates_dtl qbd,qms_quote_master qm,qms_quote_rates qr,qms_quotes_updated qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  And qu.old_buycharge_id=qr.buyrate_id  And qu.old_sellcharge_id=qr.sellrate_id And qu.old_lane_no=qr.rate_lane_no And qu.new_buycharge_id =qbd.buyrateid and qu.new_lane_no=qbd.lane_no and qu.new_version_no =qbd.version_no and qu.new_sellcharge_id =rsd.rec_con_id and RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';
          */
          --kiran.v
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                    ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                         rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd,QMS_QUOTE_MASTER qm,QMS_QUOTE_RATES_TEMP qr,QMS_QUOTES_UPDATED qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  AND qu.old_buycharge_id=qr.buyrate_id  AND  qu.old_lane_no=qr.rate_lane_no AND qu.new_buycharge_id =qbd.buyrateid AND qu.new_lane_no=qbd.lane_no AND qu.new_version_no =qbd.version_no AND qu.new_sellcharge_id =rsd.rec_con_id AND RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';

        ELSE
          v_sql5 := ',RSD.BUYRATEID,rsd.version_no,qbd.effective_date, qbd.valid_upto, RSM.TERMINALID,RSM.WEIGHT_BREAK, RSM.WEIGHT_CLASS, RSM.RATE_TYPE,RSD.FREQUENCY,RSD.TRANSIT_TIME, RSD.notes,' ||
                    ' qbm.uom,rsd.REC_CON_ID,FRL.ORIG_LOC, FRL.DEST_LOC, QBM.SHIPMENT_MODE, QBD.SERVICE_LEVEL, FRL.SERIAL_NO, QBD.DENSITY_CODE,rsd.LOWRER_BOUND,rsd.UPPER_BOUND,rsd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                  rsd.lane_no, rsd.line_no,rsd.rate_description,qbm.console_type ,qbd.carrier_id,qr.frequency_checked,qr.carrier_checked,qr.transit_checked,qr.validity_checked FROM QMS_REC_CON_SELLRATESMASTER RSM,QMS_REC_CON_SELLRATESDTL RSD,QMS_BUYRATES_MASTER qbm, QMS_BUYRATES_DTL qbd,QMS_QUOTE_MASTER qm,QMS_QUOTE_RATES_TEMP qr,QMS_QUOTES_UPDATED qu ,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE qu.quote_id=qr.quote_id  AND qbd.buyrateid=qr.buyrate_id  AND rsd.rec_con_id=qr.sellrate_id AND qbd.lane_no=qr.rate_lane_no AND RSD.REC_CON_ID = RSM.REC_CON_ID AND RSD.REC_CON_ID = ';
        END IF;
        /*  v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no
                     AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID  And qbd.version_no = qbm.version_no
                      AND RSD.WEIGHTBREAKSLAB=:v_break_point  and qbd.service_level not in (''SCH'') and FRL.SERIAL_NO=:v_serial_no   And rsd.version_no=qbd.version_no
                     AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id  And (rsd.Chargerate not in (''0.0'') or rsd.Chargerate not in ('''')) ';
        */
        /*v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no
          AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID
           AND RSD.WEIGHTBREAKSLAB=:v_break_point   and FRL.SERIAL_NO=:v_serial_no
          AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id  And rsd.Chargerate not in
        (Select Sd.Chargerate
           from qms_rec_con_sellratesdtl Sd
          where Sd.Rec_Con_Id = RSD.REC_CON_ID
            And Sd.Buyrateid = RSD.BUYRATEID
            And Sd.Lane_No = RSD.LANE_NO
            And Sd.Rec_Con_Id = RSM.Rec_Con_Id
            And Sd.Rate_Description not in (''A FREIGHT RATE'', null)
            And Sd.Chargerate in (''0.0'', ''0'')) ';*/ --@@Commented and Modified by Kameswari for the WPBN issue-136658
        v_sql6 := ':v_sell_rate_id AND RSD.LANE_NO =:v_rate_lane_no AND rsd.version_no=:v_versionno
                   AND RSD.BUYRATEID=QBD.BUYRATEID AND RSD.LANE_NO=QBD.LANE_NO AND QBD.BUYRATEID=QBM.BUYRATEID AND qbm.version_no=qbd.version_no AND (qbm.lane_no=qbd.lane_no OR qbm.lane_no IS NULL) AND qm.quote_id=frp.quote_id AND qr.quote_id=qm.id AND qr.break_point=qbd.weight_break_slab
                    AND   QBD.WEIGHT_BREAK_SLAB=qr.break_point AND QBD.Weight_Break_Slab= RSD.WEIGHTBREAKSLAB AND QBD.WEIGHT_BREAK_SLAB =:v_break_point /*and qbd.service_level not in (''SCH'')*/   AND FRL.SERIAL_NO=:v_serial_no
                   AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND rsd.version_no=qbd.version_no AND rsd.Chargerate NOT IN
                 (SELECT Sd.Chargerate
                    FROM QMS_REC_CON_SELLRATESDTL Sd
                   WHERE Sd.Rec_Con_Id = RSD.REC_CON_ID
                     AND Sd.Buyrateid = RSD.BUYRATEID
                     AND Sd.Lane_No = RSD.LANE_NO
                     AND Sd.version_no=RSD.version_no
                     AND Sd.Rec_Con_Id = RSM.Rec_Con_Id
                     AND ( Sd.Rate_Description NOT IN (''A FREIGHT RATE'') OR Sd.Rate_Description IS NOT NULL)
                     AND Sd.Chargerate IN (''0.0'', ''0''))
                     AND rsd.rate_description =:v_rate_description'; --kishore for quote view(update) duplicates break

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.margin_discount_flag, i.sellrate_id, i.rate_lane_no, i.version_no, i.break_point, i.serial_no, p_quoteid,i.rate_description; --kishore for quote view(update) duplicates break
      ELSIF UPPER(i.sell_buy_flag) = 'SBR' THEN

        /*      v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab,Currency,buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto,terminalid,weight_break,weight_scale, rate_type,primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, MARGIN_DISCOUNT_FLAG,LANE_NO, line_no)
        SELECT  distinct ';*/
        v_sql1 := 'INSERT INTO temp_charges (cost_incurredat, chargeslab,Currency,buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,efrom, validupto,terminalid,weight_break,weight_scale, rate_type,primary_basis,ORG, DEST, SHMODE, SRV_LEVEL, LEG_SL_NO, DENSITY_RATIO,LBOUND, UBOUND, MARGIN_DISCOUNT_FLAG,LANE_NO, line_no,rate_description)
         SELECT  DISTINCT ';
        v_sql2 := ':v_charge_at ,QSB.WEIGHT_BREAK_SLAB,QSB.CURRENCYID,QSB.CHARGE_RATE,';
        v_sql3 := '';
        v_sql4 := ' 0.0 ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                    DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount)' ||
                  ',''Per ''' || '||' || 'QSB.UOM,:v_sell_buy_flag';
        v_sql5 := ',''X'',QM.EFFECTIVE_DATE, QM.VALID_TO, QM.TERMINAL_ID,QSB.WEIGHT_BREAK, ''G'', QSB.WEIGHT_BREAK,' ||
                  ' QSB.uom,FRL.ORIG_LOC, FRL.DEST_LOC, FRL.SHPMNT_MODE, QSB.SERVICELEVEL, FRL.SERIAL_NO, QSB.DENSITY_CODE, QSB.LOWER_BOUND, QSB.UPPER_BOUND,:v_margin_disc_flag, QSB.LANE_NO,QSB.LINE_NO,QSB.RATE_DESCRIPTION FROM QMS_QUOTE_MASTER QM,QMS_QUOTE_SPOTRATES QSB,FS_RT_LEG FRL,FS_RT_PLAN FRP WHERE  QSB.QUOTE_ID=QM.ID';
        v_sql6 := ' AND QM.ID=:v_quote_id AND QSB.WEIGHT_BREAK_SLAB=:v_break_point AND FRL.SERIAL_NO=:v_serial_no
                   AND Qsb.Lane_No = (Frl.Serial_No - 1) AND FRL.RT_PLAN_ID=FRP.RT_PLAN_ID AND FRP.QUOTE_ID=:v_quote_id AND (QSB.CHARGE_RATE NOT IN (''0.0'') OR QSB.CHARGE_RATE NOT IN (''0'')) ';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.Discount, i.Sell_Buy_Flag, i.margin_discount_flag, i.quote_id, i.break_point, i.Serial_No, p_quoteid;
      ELSIF UPPER(i.sell_buy_flag) = 'B' THEN

        /*v_sql1 := 'INSERT INTO temp_charges (CHARGE_ID, CHARGEDESCID,cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME)
        SELECT distinct ';*/ --@@Modified by Kameswari for the WPBN issue-154398 on 21/02/09
        v_sql1 := 'INSERT INTO temp_charges (CHANGE_FLAG,CHARGE_ID, CHARGEDESCID,cost_incurredat, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME)
               SELECT DISTINCT :v_changeflag,';
        v_sql2 := ':v_charge_id,:v_charge_desc_id,:v_charge_at ,qcd.CHARGESLAB, qcm.CURRENCY,qcd.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),decode(:v_margin_disc_flag,''M'',:v_margin),
                  (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) uom,:v_sell_buy_flag';
        v_sql5 := ',qcm.BUYSELLCHARGEID, qcm.TERMINALID,qcm.RATE_BREAK, qcm.WEIGHT_CLASS, qcm.rate_type,' ||
                  ' (select PRIMARY_BASIS from QMS_CHARGE_BASISMASTER where CHARGEBASIS=qcm.CHARGEBASIS) primary_basis,
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) SECONDARY_BASIS,
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) TERTIARY_BASIS,
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qcm.CHARGEBASIS) BLOCK,
                    Qcm.DENSITY_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND, qcd.CHARGERATE_INDICATOR,:v_margin_disc_flag, qcd.lane_no,';
        /* v_sql8 := '(SELECT REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc_id
                        AND INACTIVATE=''N'')INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                        AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'')EXT_NAME FROM QMS_BUYSELLCHARGESMASTER qcm,QMS_BUYCHARGESDTL qcd WHERE qcm.BUYSELLCHARGEID=';
        */
        v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc_id
                    AND INACTIVATE=''N''
               AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  ''' ))INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                    AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N''
                  AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID = ' || '''' ||
                  p_terminalid ||
                  ''' ))EXT_NAME FROM QMS_BUYSELLCHARGESMASTER qcm,QMS_BUYCHARGESDTL qcd WHERE qcm.BUYSELLCHARGEID=';

        v_sql6 := ':v_buy_rate_id AND Qcd.CHARGESLAB=:v_break_point
                    AND QCM.BUYSELLCHARGEID=QCD.BUYSELLCHAEGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql8 || v_sql6)
          USING i.change_flag, i.charge_id, i.charge_description, i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.Margin, i.sell_buy_flag, i.margin_discount_flag, i.charge_id, i.charge_description, i.charge_id, i.charge_description, i.buyrate_id, i.break_point;

      ELSIF UPPER(i.sell_buy_flag) = 'S' THEN

        /* v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, CHARGE_ID, CHARGEDESCID,COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME )
        SELECT distinct ';*/ --@@Modified by Kameswari for the WPBN issue-154398 on 21/02/09
        v_sql1 := 'INSERT INTO temp_charges (CHANGE_FLAG,SELLCHARGEID, CHARGE_ID, CHARGEDESCID,COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS, BLOCK, DENSITY_RATIO,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no,INT_CHARGE_NAME,EXT_CHARGE_NAME )
               SELECT DISTINCT :v_changeflag,';
        v_sql2 := ':v_sell_rate_id,:v_charge_id,:v_charge_desc,:v_charge_at,
                  QSd.CHARGESLAB, qsm.CURRENCY,qsd.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := 'decode(qsm.margin_type,''M'',qsd.chargerate+(qsd.chargerate*qsd.marginvalue*0.01),qsd.chargerate+qsd.marginvalue),
                      DECODE(:v_margin_disc_flag,''M'',:v_margin_type,:v_discount_type),
                      DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount),
                      (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) basis,:v_sell_buy_flag';
        v_sql5 := ',(SELECT BUYSELLCHARGEID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                   AND CHARGEDESCID=:v_charge_desc AND DEL_FLAG=''N'' AND TERMINALID =qsm.TERMINALID), qsm.TERMINALID,qsm.RATE_BREAK, qsm.WEIGHT_CLASS, qsm.rate_type,
                    (SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) primary_basis,
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) SECONDARY_BASIS,
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) TERTIARY_BASIS,
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=qsm.CHARGEBASIS) BLOCK,
                    (SELECT DENSITY_CODE FROM QMS_BUYSELLCHARGESMASTER WHERE BUYSELLCHARGEID=QSM.BUYCHARGEID), qsd.LOWERBOUND,
                    qsd.UPPERBOUND, qsd.CHARGERATE_INDICATOR,:v_margin_disc_flag, qsd.lane_no,';

        /*v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc
        AND INACTIVATE=''N''))INT_NAME,(SELECT EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
        AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'' ))EXT_NAME FROM QMS_SELLCHARGESDTL QSD,QMS_SELLCHARGESMASTER QSM WHERE qsm.SELLCHARGEID=';*/

        v_sql8 := '(SELECT  REMARKS FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id AND CHARGEDESCID=:v_charge_desc
                    AND INACTIVATE=''N''
                    AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  '''))INT_NAME,(SELECT  EXT_CHARGE_NAME FROM QMS_CHARGEDESCMASTER WHERE CHARGEID=:v_charge_id
                    AND CHARGEDESCID=:v_charge_desc AND INACTIVATE=''N'' AND TERMINALID IN
               (SELECT PARENT_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = ' || '''' ||
                  p_terminalid || '''
                UNION ALL
                SELECT TERMINALID TERM_ID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = ''H''
                UNION ALL
                SELECT ' || '''' || p_terminalid ||
                  '''  TERM_ID
                  FROM DUAL
                UNION ALL
                SELECT CHILD_TERMINAL_ID TERM_ID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID
                 START WITH PARENT_TERMINAL_ID =' || '''' ||
                  p_terminalid ||
                  '''))EXT_NAME FROM QMS_SELLCHARGESDTL QSD,QMS_SELLCHARGESMASTER QSM WHERE qsm.SELLCHARGEID=';

        v_sql6 := ':v_sell_rate_id AND Qsd.CHARGESLAB=:v_break_point AND QsM.SELLCHARGEID=QsD.SELLCHARGEID';

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql8 || v_sql6)
          USING i.change_flag, i.sellrate_id, i.charge_id, i.charge_description, i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.charge_id, i.charge_description, i.margin_discount_flag, i.charge_id, i.charge_description, i.charge_id, i.charge_description, i.sellrate_id, i.break_point;

      ELSIF UPPER(i.sell_buy_flag) = 'BC' THEN

        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QCD.ZONE_CODE =QM.SHIPPERZONES AND QCD.CHARGE_TYPE=''Pickup'' AND QCM.SHIPMENT_MODE=QM.SHIPPER_MODE AND nvl(QCM.CONSOLE_TYPE,''~'')=NVL(QM.SHIPPER_CONSOLE_TYPE,''~'') ';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QCD.ZONE_CODE =QM.CONSIGNEEZONES AND QCD.CHARGE_TYPE=''Delivery'' AND QCM.SHIPMENT_MODE=QM.CONSIGNEE_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.CONSIGNEE_CONSOLE_TYPE,''~'') ';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (COST_INCURREDAT,chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS,BLOCK,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no) SELECT distinct ';
        v_sql2 := ':v_charge_at,QCD.CHARGESLAB, QCM.CURRENCY,QCD.CHARGERATE,';
        v_sql3 := '';
        v_sql4 := '0,decode(:v_margin_disc_flag,''M'',:v_margin_type),
                     DECODE(:v_margin_disc_flag,''M'',:v_margin),
                     (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS),:v_sell_buy_flag';
        v_sql5 := ',QCD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,
                    (SELECT PRIMARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',(SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QCD.CHARGE_BASIS)' ||
                  ',QCD.DENSITY_CODE,QCD.ZONE_CODE,qcd.LOWERBOUND,qcd.UPPERBOUND,qcd.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                   qcd.line_no FROM QMS_CARTAGE_BUYDTL QCD,QMS_CARTAGE_BUYSELLCHARGES QCM,QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QCD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := ':v_buy_rate_id AND QCD.CHARGESLAB=:v_break_point AND QM.ID=:v_quote_id' ||
                  v_sql7;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.charge_at, i.margin_discount_flag, i.margin_type, i.margin_discount_flag, i.margin, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.break_point, i.quote_id;

      ELSIF UPPER(i.sell_buy_flag) = 'SC' THEN

        IF UPPER(i.charge_at) = 'PICKUP' THEN
          v_sql7 := ' AND QCM.LOCATION_ID=QM.ORIGIN_LOCATION AND QSD.ZONE_CODE =QM.SHIPPERZONES AND QSD.CHARGE_TYPE=''Pickup'' AND QCM.SHIPMENT_MODE=QM.SHIPPER_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.SHIPPER_CONSOLE_TYPE,''~'') ' ||
                    ' AND QSD.SELL_CARTAGE_ID=:v_sell_rate_id';
        ELSE
          v_sql7 := ' AND QCM.LOCATION_ID=QM.DEST_LOCATION AND QSD.ZONE_CODE =QM.CONSIGNEEZONES AND QSD.CHARGE_TYPE=''Delivery'' AND QCM.SHIPMENT_MODE=QM.CONSIGNEE_MODE AND NVL(QCM.CONSOLE_TYPE,''~'')=NVL(QM.CONSIGNEE_CONSOLE_TYPE,''~'') ' ||
                    ' AND QSD.SELL_CARTAGE_ID=:v_sell_rate_id';
        END IF;

        v_sql1 := 'INSERT INTO temp_charges (SELLCHARGEID, COST_INCURREDAT, chargeslab, currency, buyrate,sellrate,MARGIN_TYPE,MARGINVALUE, chargebasis, sel_buy_flag, buy_charge_id,terminalid, weight_break,WEIGHT_SCALE, rate_type, primary_basis,SECONDARY_BASIS,TERTIARY_BASIS,BLOCK,DENSITY_RATIO,ZONE,LBOUND, UBOUND, RATE_INDICATOR,MARGIN_DISCOUNT_FLAG, line_no)
                SELECT DISTINCT ';
        v_sql2 := ':v_sell_rate_id,:v_charge_at,QSD.CHARGESLAB, QCM.CURRENCY,QSD.BUYRATE_AMT,';
        v_sql3 := '';
        v_sql4 := 'QSD.CHARGERATE ,decode(:v_margin_disc_flag,''M'',:v_margin_type,:v_disc_type),
                                   DECODE(:v_margin_disc_flag,''M'',:v_margin,:v_discount),
                (SELECT BASIS_DESCRIPTION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),:v_sell_buy_flag';
        v_sql5 := ',QSD.CARTAGE_ID, QCM.TERMINALID,QCM.WEIGHT_BREAK, ''G'', QCM.RATE_TYPE,
                    (SELECT Primary_Basis FROM QMS_CHARGE_BASISMASTER WHERE Chargebasis = Qsd.Charge_Basis),
                    (SELECT SECONDARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                    (SELECT TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                    (SELECT BLOCK FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS=QSD.CHARGE_BASIS),
                     QCD.DENSITY_CODE,QSD.ZONE_CODE,qsd.LOWERBOUND, qsd.UPPERBOUND,QSD.CHARGERATE_INDICATOR,:v_margin_disc_flag,
                    qsd.line_no FROM QMS_CARTAGE_SELLDTL QSD, QMS_CARTAGE_BUYSELLCHARGES QCM, QMS_CARTAGE_BUYDTL QCD, QMS_QUOTE_MASTER QM WHERE QCM.CARTAGE_ID=QSD.CARTAGE_ID AND QCM.CARTAGE_ID=';
        v_sql6 := ':v_buy_rate_id AND QSD.CHARGESLAB=:v_break_point
                  AND QSD.CARTAGE_ID=QCD.CARTAGE_ID AND QCD.CHARGE_TYPE=QSD.CHARGE_TYPE AND QCD.ZONE_CODE=QSD.ZONE_CODE
                  AND QM.ID=:v_quote_id' || v_sql7;

        EXECUTE IMMEDIATE (v_sql1 || v_sql2 || v_sql3 || v_sql4 || v_sql5 ||
                          v_sql6)
          USING i.sellrate_id, i.charge_at, i.margin_discount_flag, i.margin_type, i.discount_type, i.margin_discount_flag, i.margin, i.discount, i.sell_buy_flag, i.margin_discount_flag, i.buyrate_id, i.break_point, i.quote_id, i.sellrate_id;
      END IF;
    END LOOP;

    UPDATE TEMP_CHARGES SET selected_flag = 'Y';

    ----------------------------For Ordering Charges--------------------------------
    FOR i IN (SELECT Chargegroupid
                FROM QMS_QUOTE_CHARGEGROUPDTL
               WHERE Quote_Id = v_id
               ORDER BY Id) LOOP
      FOR j IN (SELECT Charge_Id, Chargedescid, Terminalid, Id
                  FROM QMS_CHARGE_GROUPSMASTER
                 WHERE Chargegroup_Id = i.Chargegroupid
                   AND Inactivate = 'N') LOOP

        EXECUTE IMMEDIATE ('UPDATE TEMP_CHARGES SET LANE_NO =:v_lane_no,ID=:v_id
                               WHERE CHARGE_ID=:v_charge_id AND CHARGEDESCID =:v_charge_desc_id
                               AND TERMINALID=:v_terminal_id')
          USING v_lane_no, j.Id, j.Charge_Id, j.Chargedescid, j.Terminalid;
      END LOOP;

      v_Lane_No := v_Lane_No + 1;

    END LOOP;
    ------------------------------------------------------------------------------------

    OPEN p_rs FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BR'',''RSR'',''CSR'',''SBR'') order by LEG_SL_NO, line_no';

    OPEN p_rs1 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''B'',''S'') order by LANE_NO,id,BUY_CHARGE_ID,COST_INCURREDAT, line_no';

    OPEN p_rs2 FOR 'SELECT * FROM TEMP_CHARGES WHERE sel_buy_flag IN (''BC'',''SC'') order by BUY_CHARGE_ID,COST_INCURREDAT, line_no';

    OPEN p_rs3 FOR 'SELECT EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID= ' || v_id;
    /* EXCEPTION
    WHEN OTHERS THEN
      DBMS_OUTPUT.put_line(SQLERRM);*/
  END;--Ended on 04Feb2011
  --Added on 18Feb2011 for Update
  PROCEDURE MULTI_QUOTE_SEL_BUY_RTE_PROC(P_ORG_LOC      VARCHAR2,
                                          P_DEST_LOC     VARCHAR2,
                                          P_TERMINAL     VARCHAR2,
                                          P_SRVLEVEL     VARCHAR2,
                                          P_SHMODE       VARCHAR2,
                                          P_PERMISSION   VARCHAR2 DEFAULT 'Y',
                                          P_OPERATION    VARCHAR2,
                                          P_QUOTE_ID     VARCHAR2,
                                          P_WEIGHT_BREAK VARCHAR2,
                                          P_RS           OUT RESULTSET) AS
    V_RC_C1      RESULTSET;
    V_TERMINALS  VARCHAR2(32000);
    V_CHARGERATE VARCHAR2(400) := '';
    K            NUMBER := 0;
    V_SQL1       VARCHAR2(2000);
    V_SQLBNEW    VARCHAR2(2000);
    V_SQLSNEW    VARCHAR2(2000);
    V_SQLTBNEW   VARCHAR2(2000);
    V_SQLTSNEW   VARCHAR2(2000);
    V_SQL2       VARCHAR2(2000);
    V_SQL4       VARCHAR2(2000) := '';
    V_SQL5       VARCHAR2(2000);
    V_SQL6       VARCHAR2(32767);
    V_SQL7       VARCHAR2(2000);
    V_SQL10      VARCHAR2(2000);
    V_SQL11      VARCHAR2(2000);
    V_SQL12      VARCHAR2(32767);
    V_SQL13      VARCHAR2(2000);
    V_SQL14      VARCHAR2(2000);
    V_SQL15      VARCHAR2(2000);
    V_SQL16      VARCHAR2(2000);
    V_SQL17      VARCHAR2(2000);
    V_SQL18      VARCHAR2(2000);
    V_SQL19      VARCHAR2(2000);
    V_BASE       VARCHAR2(30);
    V_BREAK      VARCHAR2(4000);
    V_BREAK1     VARCHAR2(4000);
    V_TEMP       VARCHAR2(200);
    V_BREAK_SLAB VARCHAR2(40);
    --V_BREAK_SLAB     VARCHAR2(40);
    V_BREAK_SLAB_BAF  VARCHAR2(40);
    V_BREAK_SLAB_CAF  VARCHAR2(40);
    V_BREAK_SLAB_CSF  VARCHAR2(40);
    V_BREAK_SLAB_PSS  VARCHAR2(40);
    V_OPR_ADM_FLAG    VARCHAR2(3);
    V_RCB_FLAG        VARCHAR2(10);
    V_SELLBUYFLAG     VARCHAR2(10);
    V_SELLRATEID      NUMBER;
    V_BUYRATEID       NUMBER;
    V_LANENO          NUMBER;
    V_ID              NUMBER;
    V_QUOTERATEID     NUMBER;
    V_CON_BUYRATEID   NUMBER;
    V_CON_SELLRATEID  NUMBER;
    V_CON_LANENO      NUMBER;
    V_FLAG            VARCHAR2(200);
    V_CHECKEDFLAG     VARCHAR2(200);
    V_VERSIONNO       NUMBER;
    V_TEMP1           NUMBER;
    V_BASE1           NUMBER;
    v_rd              VARCHAR2(200); --added for 180164
    v_surcharge_count NUMBER;
    V_TEMP_BREAK      VARCHAR2(4000);
    V_TEMP_DESC       VARCHAR2(4000);
    V_RATE_DESC       VARCHAR2(4000);
    V_TEMP_CHARGERATE VARCHAR2(400) := '';
    V_SQL_SUR         VARCHAR2(4000) := '';
    TYPE ARRAY IS TABLE OF QMS_QUOTE_RATES.BREAK_POINT%TYPE;
    BREAK_POINT_LIST ARRAY;
    TYPE DESC_ARRAY IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    CHARGE_DESC_LIST DESC_ARRAY;

  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINAL;
    IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
      V_TERMINALS := 'SELECT 1 FROM FS_FR_TERMINALMASTER TM where TM.terminalid= ';
    ELSE
      DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
                               'v_terminal_id',
                               P_TERMINAL);
      V_TERMINALS := 'SELECT 1 FROM FETCH_TERMINAL_ID_VIEW TV where TV.term_id = ';
      --Union Can be replaced with union all: Sreenadh
    END IF;
    /*V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID) ';*/ --@@Modified by Kameswari for Surcharge Enhancements
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID,TEMP_CHECK,RATE_DESCRIPTION) '; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL2 := 'SELECT distinct to_number(qbd.buyrateid) buyrateid, upper(qbd.weight_break_slab)  weight_break_slab,';
    --COMMENTED BY SUBRAHMANYAM FOR 180164
    --V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check';
    V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL5 := '  FROM qms_buyrates_dtl qbd, qms_buyrates_master qbm WHERE qbd.buyrateid = qbm.buyrateid AND (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbd.origin =:v_org_loc AND qbd.destination=:v_dest_loc
	    AND qbd.service_level LIKE :v_srvlevel';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no  AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    --v_sql6 modified with exists :Sreenadh
    V_SQLTBNEW := ' SELECT  buyrateid,weight_break_slab,CHARGERATE, lane_no,line_no,id_flag,wtbreak ,REC_BUYRATEID,test_check,RATE_DESCRIPTION from (';
    V_SQL7     := ' SELECT distinct to_number(sm.rec_con_id) buyrateid, UPPER(sd.weightbreakslab) weight_break_slab,';
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check';
    V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAMF FOR 180164
    V_SQL11 := ' FROM qms_rec_con_sellratesmaster sm, qms_rec_con_sellratesdtl sd, qms_buyrates_dtl qbd WHERE sm.rec_con_id = sd.rec_con_id  AND sd.origin =: v_org_loc AND sd.destination=:v_dest_loc AND sd.servicelevel_id LIKE :v_srvlevel
	    AND sd.buyrateid=qbd.buyrateid  AND sd.version_no=qbd.version_no AND sd.lane_no=qbd.lane_no AND sd.line_no=qbd.line_no ';
    /* V_SQL12 := ' AND sd.ai_flag =''A'' and exists (select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')  AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID ) UNION all ';*/
    --v_sql12 modified with exists :Sreenadh
    -- commented for 180164
    /*V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID )) where test_check is not null  UNION all ';*/
    --added for 180164
    V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode AND SM.WEIGHT_BREAK = :v_wt_brk
	    AND  EXISTS (' || V_TERMINALS ||
               'Sm.TERMINALID )) where test_check is not null  UNION all ';
    -- ended for 180164

    /*V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE)';*/
    --       V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK)';-- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK,RATE_DESCRIPTION,EXTERNAL_NOTES)'; -- ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR THE 180164
    --      V_SQLBNEW :='select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check from ('; --@@Added for the performance issue on 20/03/09
    V_SQLBNEW := 'select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check,RATE_DESCRIPTION,EXTERNAL_NOTES from ('; --ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check'; --@@Modified by Kameswari for Surcharge Enhancements
    V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION,QBD.EXTERNAL_NOTES'; --@@ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL15 := 'SELECT distinct to_char(sD.REC_CON_ID) BUYRATEID,sd.version_no version_no,sD.CARRIER_ID carrier_id, sD.ORIGIN,sD.DESTINATION,sD.SERVICELEVEL_ID SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=sD.SERVICELEVEL_ID)SDESC,''a'' weight_break_slab,sD.TRANSIT_TIME,sD.FREQUENCY,''b''  charge_rate,sD.LANE_NO,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') RCB_FLAG, to_char(sD.BUYRATEID) REC_BUYRATE_ID,sD.NOTES,UPPER(SM.WEIGHT_BREAK) UWGT,SM.TERMINALID,sm.CURRENCY,sm.WEIGHT_CLASS,SD.EXTERNAL_NOTES ';
    V_SQL16 := ', (select DISTINCT qbd.EFFECTIVE_FROM FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no ) efrom';
    /*V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type';*/
    V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION';
    --commented for 180164
    /* V_SQL18 := '  \*AND NOT EXISTS
        (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
       WHERE qsd.ai_flag = ''A''
         AND QBD.BUYRATEID = QSD.BUYRATEID
         AND QBD.LANE_NO = QSD.LANE_NO
         AND QSD.ACCEPTANCE_FLAG IS NULL
         AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*\  AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE''or QBD.RATE_DESCRIPTION IS NULL)) where test_check is not null order by buyrateid,lane_no ';
    */
    -- added for 180164
    V_SQL18 := '  /*AND NOT EXISTS
	  (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
	 WHERE qsd.ai_flag = ''A''
	   AND QBD.BUYRATEID = QSD.BUYRATEID
	   AND QBD.LANE_NO = QSD.LANE_NO
	   AND QSD.ACCEPTANCE_FLAG IS NULL
	   AND (Qsd.INVALIDATE = ''F'' OR Qsd.INVALIDATE IS NULL))*/ ) WHERE test_check IS NOT NULL ORDER BY buyrateid,lane_no ';
    -- ended for 180164
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_BASE_DATA');
    IF UPPER(P_OPERATION) = 'VIEW' THEN
    FOR K IN
      (SELECT ID
        FROM QMS_QUOTE_MASTER
       WHERE QUOTE_ID = P_QUOTE_ID
         AND VERSION_NO = (SELECT MAX(VERSION_NO)
                             FROM QMS_QUOTE_MASTER
                            WHERE QUOTE_ID = P_QUOTE_ID)
       )LOOP
          V_QUOTERATEID := K.ID;
      SELECT DISTINCT SELL_BUY_FLAG,
                      SELLRATE_ID,
                      BUYRATE_ID,
                      RATE_LANE_NO,
                      QR.VERSION_NO
        INTO V_SELLBUYFLAG,
             V_SELLRATEID,
             V_BUYRATEID,
             V_LANENO,
             V_VERSIONNO
        FROM QMS_QUOTE_RATES  QR,
             QMS_QUOTE_MASTER QM,
             FS_RT_PLAN       PL,
             FS_RT_LEG        LEG
       WHERE QR.QUOTE_ID = V_QUOTERATEID
         AND QM.QUOTE_ID = PL.QUOTE_ID
         AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
         AND LEG.ORIG_LOC = P_ORG_LOC
         AND LEG.DEST_LOC = P_DEST_LOC
         /*AND LEG.SERIAL_NO = QR.SERIAL_NO*/
         AND QM.QUOTE_ID = P_QUOTE_ID
         AND QM.VERSION_NO = (SELECT MAX(VERSION_NO)
                                FROM QMS_QUOTE_MASTER
                               WHERE QUOTE_ID = P_QUOTE_ID)
         AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR');
      IF V_SELLBUYFLAG = 'BR' THEN
        /*  SELECT BREAK_POINT BULK COLLECT -- commented and modified by phani sekhar for wpbn 178377 on 20090803
        INTO   BREAK_POINT_LIST
        FROM   QMS_QUOTE_RATES
        WHERE  QUOTE_ID = V_QUOTERATEID
               AND SELL_BUY_FLAG = 'BR';*/ -- added by phani sekhar for wpbn 178377 on 20090803
        SELECT DISTINCT BREAK_POINT, QR.CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES  QR,
               QMS_QUOTE_MASTER QM,
               FS_RT_PLAN       PL,
               FS_RT_LEG        LEG
         WHERE QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
               (SELECT MAX(VERSION_NO)
                  FROM QMS_QUOTE_MASTER
                 WHERE QUOTE_ID = P_QUOTE_ID)
           AND QR.SELL_BUY_FLAG IN ('BR'); --ends for 178377
        FORALL I IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT BD.BUYRATEID,
                   BD.WEIGHT_BREAK_SLAB,
                   BD.CHARGERATE,
                   BD.LANE_NO,
                   BD.LINE_NO,
                   'BR',
                   UPPER(BM.WEIGHT_BREAK),
                   '',
                   BD.RATE_DESCRIPTION
              FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
             WHERE BD.WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(I)
               AND BD.BUYRATEID = V_BUYRATEID
               AND BD.LANE_NO = V_LANENO
               AND BD.VERSION_NO = BM.VERSION_NO
               AND BD.VERSION_NO = V_VERSIONNO
               AND BM.BUYRATEID = BD.BUYRATEID
               AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
               AND BD.RATE_DESCRIPTION = CHARGE_DESC_LIST(I)
            -- for 180164
            /*AND (BD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         BD.RATE_DESCRIPTION IS NULL)*/
            ;
      INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           EXTERNAL_NOTES)
          SELECT DISTINCT BD.BUYRATEID,
                          V_VERSIONNO,
                          BD.CARRIER_ID,
                          BD.ORIGIN,
                          BD.DESTINATION,
                          BD.SERVICE_LEVEL,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = BD.SERVICE_LEVEL) SERVICE_LEVEL_DESC,
                          'a',
                          BD.TRANSIT_TIME,
                          BD.FREQUENCY,
                          'b',
                          BD.LANE_NO,
                          'BR',
                          '',
                          BD.NOTES,
                          UPPER(BM.WEIGHT_BREAK),
                          BM.TERMINALID,
                          BM.CURRENCY,
                          BM.WEIGHT_CLASS,
                          BD.EFFECTIVE_FROM,
                          /*BD.VALID_UPTO,*/
                          (SELECT DISTINCT BD.VALID_UPTO
                             FROM QMS_BUYRATES_DTL    BD,
                                  QMS_BUYRATES_MASTER BM,
                                  QMS_QUOTE_RATES     QR
                            WHERE QR.BUYRATE_ID = BD.BUYRATEID
                                 /*AND QR.VERSION_NO = BD.VERSION_NO*/
                              AND QR.RATE_LANE_NO = BD.LANE_NO
                              AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
                              AND BD.BUYRATEID = V_BUYRATEID
                                 --  AND BD.VERSION_NO = V_VERSIONNO
                              AND BD.VERSION_NO = BM.VERSION_NO
                              AND BD.LANE_NO = V_LANENO
                              AND QR.QUOTE_ID = V_QUOTERATEID
                              AND (BM.LANE_NO = BD.LANE_NO OR
                                  BM.LANE_NO IS NULL)
                              AND BM.BUYRATEID = BD.BUYRATEID
                              AND BD.VERSION_NO =
                                  (SELECT MAX(VERSION_NO)
                                     FROM QMS_BUYRATES_DTL
                                    WHERE BUYRATEID = BD.BUYRATEID
                                      AND LANE_NO = BD.LANE_NO)),
                          'Y',
                          BM.CONSOLE_TYPE,
                          BD.RATE_DESCRIPTION,
                          BD.external_notes
            FROM QMS_BUYRATES_DTL    BD,
                 QMS_BUYRATES_MASTER BM,
                 QMS_QUOTE_RATES     QR
           WHERE QR.BUYRATE_ID = BD.BUYRATEID
             AND QR.VERSION_NO = BD.VERSION_NO
             AND QR.RATE_LANE_NO = BD.LANE_NO
             AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
             AND BD.BUYRATEID = V_BUYRATEID
             AND BD.VERSION_NO = V_VERSIONNO
             AND BD.VERSION_NO = BM.VERSION_NO
             AND BD.LANE_NO = V_LANENO
             AND QR.QUOTE_ID = V_QUOTERATEID
             AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.BUYRATEID = BD.BUYRATEID
          -- for 180164
          /*AND
                       (BD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       BD.RATE_DESCRIPTION IS NULL)*/
          ;

      ELSE
        SELECT DISTINCT BREAK_POINT, CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES
         WHERE QUOTE_ID = V_QUOTERATEID
           AND SELL_BUY_FLAG = 'RSR';
        FORALL J IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT SD.REC_CON_ID,
                   SD.WEIGHTBREAKSLAB,
                   SD.CHARGERATE,
                   SD.LANE_NO,
                   SD.LINE_NO,
                   DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                   UPPER(SM.WEIGHT_BREAK),
                   SD.BUYRATEID,
                   SD.RATE_DESCRIPTION
              FROM QMS_REC_CON_SELLRATESDTL    SD,
                   QMS_REC_CON_SELLRATESMASTER SM
             WHERE SD.WEIGHTBREAKSLAB = BREAK_POINT_LIST(J)
               AND SD.RATE_DESCRIPTION = CHARGE_DESC_LIST(J)
               AND SD.REC_CON_ID = V_SELLRATEID
               AND SD.BUYRATEID = V_BUYRATEID
               AND SD.LANE_NO = V_LANENO
               AND SD.VERSION_NO = V_VERSIONNO
               AND SD.REC_CON_ID = SM.REC_CON_ID
               AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') =
                   V_SELLBUYFLAG
            -- for 180164
            /*AND (SD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         SD.RATE_DESCRIPTION IS NULL)*/
            ;
        INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           external_notes)
          SELECT DISTINCT SD.REC_CON_ID,
                          V_VERSIONNO,
                          SD.CARRIER_ID,
                          SD.ORIGIN,
                          SD.DESTINATION,
                          SD.SERVICELEVEL_ID,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = SD.SERVICELEVEL_ID) SERVICE_LEVEL_DESC,
                          'a',
                          SD.TRANSIT_TIME,
                          SD.FREQUENCY,
                          'b',
                          SD.LANE_NO,
                          DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                          SD.BUYRATEID,
                          SD.NOTES,
                          UPPER(SM.WEIGHT_BREAK),
                          SM.TERMINALID,
                          SM.CURRENCY,
                          SM.WEIGHT_CLASS,
                          (SELECT DISTINCT QBD.EFFECTIVE_FROM
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.REC_CON_ID = SD.REC_CON_ID
                              AND QSD.BUYRATEID = SD.BUYRATEID
                              AND QSD.LANE_NO = SD.LANE_NO
                              AND QSD.VERSION_NO = SD.VERSION_NO
                              AND QSD.VERSION_NO = QBD.VERSION_NO),
                          (SELECT DISTINCT QBD.VALID_UPTO
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.VERSION_NO =
                                  (SELECT MAX(RSD1.VERSION_NO)
                                     FROM QMS_REC_CON_SELLRATESDTL RSD1
                                    WHERE RSD1.BUYRATEID = QSD.BUYRATEID
                                      AND RSD1.LANE_NO = QSD.LANE_NO
                                      AND RSD1.BUYRATEID = V_BUYRATEID
                                      AND RSD1.LANE_NO = V_LANENO)
                              AND QBD.VERSION_NO = QSD.VERSION_NO),
                          'Y',
                          SD.CONSOLE_TYPE,
                          SD.RATE_DESCRIPTION,
                          sd.external_notes
            FROM QMS_REC_CON_SELLRATESDTL    SD,
                 QMS_REC_CON_SELLRATESMASTER SM,
                 QMS_QUOTE_RATES             QR
           WHERE QR.BUYRATE_ID = SD.BUYRATEID
             AND QR.SELLRATE_ID = SD.REC_CON_ID
             AND QR.VERSION_NO = SD.VERSION_NO
             AND QR.RATE_LANE_NO = SD.LANE_NO
             AND QR.BREAK_POINT = SD.WEIGHTBREAKSLAB
             AND SD.REC_CON_ID = V_SELLRATEID
             AND SD.BUYRATEID = V_BUYRATEID
             AND SD.LANE_NO = V_LANENO
             AND SD.VERSION_NO = V_VERSIONNO
             AND SD.REC_CON_ID = SM.REC_CON_ID
             AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') = V_SELLBUYFLAG
          -- for 180164
          /* AND
                       (SD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       SD.RATE_DESCRIPTION IS NULL)*/
          ;
      END IF;
      END LOOP;
    ELSE

      /*EXECUTE IMMEDIATE (V_SQL1 || V_SQL7 || V_SQL10 || V_SQL11 ||
            V_SQL12 || V_SQL2 || V_SQL4 || V_SQL5 ||
            V_SQL6 || V_SQL18 || ',LINE_NO')
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/

      print_out('------P_WEIGHT_BREAK-------' || P_WEIGHT_BREAK);
      print_out(V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 || V_SQL11 ||
                V_SQL12 || V_SQLTBNEW || V_SQL2 || V_SQL4 || V_SQL5 ||
                V_SQL6 || V_SQL18 || ',LINE_NO');

      EXECUTE IMMEDIATE (V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 ||
                        V_SQL11 || V_SQL12 || V_SQLTBNEW || V_SQL2 ||
                        V_SQL4 || V_SQL5 || V_SQL6 || V_SQL18 ||
                        ',LINE_NO')
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;
      /*EXECUTE IMMEDIATE (V_SQL13 || V_SQL15 || V_SQL16 || V_SQL17 ||
            V_SQL11 || V_SQL12 || V_SQL14 || V_SQL5 ||
            V_SQL6 || V_SQL18)
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/
      DBMS_OUTPUT.put_line('--------2--------------------');
      print_out(V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 || V_SQL17 ||
                V_SQL11 || V_SQL12 || V_SQLBNEW || V_SQL14 || V_SQL5 ||
                V_SQL6 || V_SQL18);

      EXECUTE IMMEDIATE (V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 ||
                        V_SQL17 || V_SQL11 || V_SQL12 || V_SQLBNEW ||
                        V_SQL14 || V_SQL5 || V_SQL6 || V_SQL18)
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;

    END IF;
    --@@ Commented by subrahmanyam for the 219973 for Dynamic SurCharges.
    DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164

    IF P_OPERATION = 'Modify' THEN
      BEGIN
        FOR K IN (SELECT DISTINCT QR.SELL_BUY_FLAG,
                                  QM.ID,
                                  QR.BUYRATE_ID,
                                  QR.SELLRATE_ID,
                                  QR.RATE_LANE_NO
                    FROM QMS_QUOTE_RATES  QR,
                         QMS_QUOTE_MASTER QM,
                         FS_RT_PLAN       PL,
                         FS_RT_LEG        LEG
                   WHERE QR.QUOTE_ID = QM.ID
                     AND QM.QUOTE_ID = PL.QUOTE_ID
                     AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
                     AND LEG.ORIG_LOC = P_ORG_LOC
                     AND LEG.DEST_LOC = P_DEST_LOC
                     AND LEG.SERIAL_NO = QR.SERIAL_NO
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QM.VERSION_NO =
                         (SELECT MAX(VERSION_NO)
                            FROM QMS_QUOTE_MASTER
                           WHERE QUOTE_ID = P_QUOTE_ID)
                     AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')) LOOP

          V_SELLBUYFLAG    := K.SELL_BUY_FLAG;
          V_ID             := K.ID;
          V_CON_BUYRATEID  := K.BUYRATE_ID;
          V_CON_SELLRATEID := K.SELLRATE_ID;
          V_CON_LANENO     := K.RATE_LANE_NO;

          /*  SELECT DISTINCT QR.SELL_BUY_FLAG, QM.ID, QR.BUYRATE_ID,
              QR.SELLRATE_ID, QR.RATE_LANE_NO
          INTO   V_SELLBUYFLAG, V_ID, V_CON_BUYRATEID,
                 V_CON_SELLRATEID, V_CON_LANENO
          /*FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM -- commented and modified by phani sekhar for wpbn 178377 on 20090803
          WHERE  QR.QUOTE_ID = QM.ID
                 AND QM.QUOTE_ID = P_QUOTE_ID
                 AND QM.ACTIVE_FLAG = 'A'
                 AND
                 QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');
                 -- added by phani sekhar for wpbn 178377 on 20090803
                  FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM, FS_RT_PLAN PL,
           FS_RT_LEG LEG
          WHERE  QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
           (SELECT MAX(VERSION_NO)
                FROM   QMS_QUOTE_MASTER
                WHERE  QUOTE_ID = P_QUOTE_ID)
          AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');*/

          --ends 178377

          SELECT BREAK_POINT, charge_description BULK COLLECT
            INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
            FROM QMS_QUOTE_RATES
           WHERE QUOTE_ID = V_ID
             AND SELL_BUY_FLAG = V_SELLBUYFLAG;
          FORALL K IN 1 .. BREAK_POINT_LIST.COUNT
            UPDATE GT_TEMP_DATA_1
               SET CHECKED_FLAG = 'Y'
             WHERE BUYRATEID = (CASE WHEN V_SELLBUYFLAG = 'BR' THEN
                    V_CON_BUYRATEID ELSE V_CON_SELLRATEID END)
               AND LANE_NO = V_CON_LANENO
               AND WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(K)
               AND RATE_DESCRIPTION = CHARGE_DESC_LIST(K);
        END LOOP;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_SELLBUYFLAG := '';

      END;
    END IF;
    FOR J IN (SELECT DISTINCT BUYRATEID,
                              VERSION_NO,
                              LANE_NO,
                              WEIGHT_BREAK,
                              RCB_FLAG,
                              NVL(REC_BUYRATE_ID, 'P') REC_BUYRATE_ID
                FROM GT_BASE_DATA
               WHERE RATE_DESCRIPTION LIKE 'A FREIGHT RATE'
               ORDER BY BUYRATEID, LANE_NO) LOOP
      V_CHARGERATE      := '';
      V_CHECKEDFLAG     := '';
      K                 := 1;
      V_BREAK           := '';
      v_surcharge_count := 0;
      V_TEMP_BREAK      := '';
      V_TEMP_CHARGERATE := '';
      V_RATE_DESC       := '';
      IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1) = FALSE THEN
        BEGIN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND WEIGHT_BREAK_SLAB=''BASIC'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id  ' )
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_TEMP_BREAK;
          V_CHARGERATE := V_TEMP_CHARGERATE || ',';
          V_RATE_DESC  := V_TEMP_DESC || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count + 1;
            V_BREAK           := 'BASIC';
            V_CHARGERATE      := '-' || ',';
            V_RATE_DESC       := 'A FREIGHT RATE' || ',';
            EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no IN (0,1)  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
              INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
              USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
            V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
            V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        END;
        IF v_surcharge_count = 0 THEN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE)  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=1  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
          V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
          V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';

        END IF;
      END IF;
      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      ELSIF UPPER(J.WEIGHT_BREAK) = 'FLAT' OR
            UPPER(J.WEIGHT_BREAK) = 'SLAB' THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      END IF;
      IF (J.WEIGHT_BREAK <> 'FLAT') THEN
        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          --            V_SQL1 := ' AND line_no > 0 order by break_slab';
          V_SQL1 := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') order by break_slab';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          V_SQL1 := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                    J.LANE_NO || ' order by break_slab';
        ELSE
          --           V_SQL1 := ' AND line_no > 0 order by to_number(break_slab)';-- COMMENTED FOR 180164
          V_SQL1 := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') order by to_number(break_slab)';
          -- V_SQL1 := ' AND line_no > 0 order by LINE_NO';
        END IF;
        -- COMMENTED FOR 180164
        --  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1 -- commented by subrahmanyam for 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL1
          USING J.WEIGHT_BREAK;

        --ADDED FOR 180164
        /*  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
                    USING J.WEIGHT_BREAK;
        */
        LOOP
          FETCH V_RC_C1
          --INTO V_BREAK_SLAB,V_LINE_NO ;--ADDED FOR 180164
            INTO V_BREAK_SLAB; -- COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            /*            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
            AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
            and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
            */
            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag,RATE_DESCRIPTION FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
              INTO V_TEMP, V_FLAG, V_TEMP_DESC
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-' || ',';
          END;
          /*If UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2
           Then
           V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            BEGIN
            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
           AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_BAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CSF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_PSS, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;

           End If;*/
        END LOOP;
        CLOSE V_RC_C1;

        --@@ Added by subrahmanyam for the 180164
        V_TEMP_DESC := '';
        /*      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
                    V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
                    V_SQL_SUR := ' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSE
                   V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'')  AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and  '||j.buyrateid ||' and '||j.lane_no ||' and to_number(break_slab)';
              END IF;
        */

        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_SQL_SUR := ' AND line_no > 0 AND /*WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND*/ RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no ';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          --            V_SQL_SUR := ' and buyrateid='||j.buyrateid ||' and lane_no='||j.lane_no ||' order by line_no  order by line_no ';
          V_SQL_SUR := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                       J.LANE_NO || ' order by break_slab ';
        ELSE
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no';
        END IF;
        V_TEMP_BREAK := '';
        OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
          USING J.WEIGHT_BREAK;
        LOOP
          FETCH V_RC_C1
            INTO V_BREAK_SLAB, V_TEMP_DESC;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
              INTO V_TEMP, V_FLAG
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
            V_TEMP_BREAK  := V_TEMP_BREAK || V_BREAK_SLAB || ',';
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_TEMP_BREAK  := V_BREAK_SLAB || ',';
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-,';
          END;

        END LOOP;
        CLOSE V_RC_C1;
        /*   IF P_SHMODE =1 THEN
                   \* V_BREAK_SLAB:='FSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB:='FSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='FSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB    :='SSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;*\
                    DBMS_OUTPUT.put_line('----FOR AIR-----');
                  ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE =2
                  THEN
                      V_BREAK_SLAB    :='CAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CAF%';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CSF';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                ELSIF P_SHMODE =4 THEN
                V_BREAK_SLAB    :='SURCHARGE';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
             END IF;
        */
        --@@ Ended by subrahmanyam for the 180164

        V_BASE  := V_BREAK;
        V_BREAK := '';
        -- COMMENTED FOR 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and weight_break =:weight_break' || V_SQL1
          USING J.WEIGHT_BREAK;
        -- ADDED FOR 180164
        /* OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
        USING J.WEIGHT_BREAK;*/
        LOOP
          FETCH V_RC_C1
          -- INTO V_BREAK_SLAB,V_LINE_NO;--ADDED FOR 180164
            INTO V_BREAK_SLAB; --COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
            /*V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';*/

            -- V_BREAK :=V_BREAK||','||V_BREAK_SLAB_BAF||','||V_BREAK_SLAB_CAF||','||V_BREAK_SLAB_CSF||','||V_BREAK_SLAB_PSS;
          ELSIF P_SHMODE = 1 OR P_SHMODE = 4 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          END IF;
        END LOOP;
        CLOSE V_RC_C1;
        --@@Added by subrahmanyam for 180164
        IF P_SHMODE = 1 THEN
          --V_BREAK := V_BREAK || ',' ||'FSBASIC'||','||'FSMIN'||','||'FSKG'||','||'SSBASIC'||','||'SSMIN'||','||'SSKG';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
          -- ELSIF UPPER(J.WEIGHT_BREAK) != 'LIST' AND P_SHMODE = 2 THEN
        ELSIF P_SHMODE = 2 THEN
          -- V_BREAK := V_BREAK ||','||'CAFMIN'||','||'CAF%'||','||'BAFMIN'||','||'BAFM3'||','||'PSSMIN'||','||'PSSM3'||','||'CSF';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        ELSIF P_SHMODE = 4 THEN
          V_BREAK := V_BREAK || ',' || 'SURCHARGE';
        END IF;

        --@@ Ended by subrahmanyam for 180164
        IF (UPPER(J.WEIGHT_BREAK) = 'SLAB') THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSE
          V_BREAK := SUBSTR(V_BREAK, 2, LENGTH(V_BREAK));
        END IF;
      ELSE
      BEGIN
        SELECT DISTINCT WEIGHT_BREAK_SLAB, CHARGERATE, RATE_DESCRIPTION
          INTO V_BASE, V_TEMP, V_TEMP_DESC
          FROM GT_TEMP_DATA_1
         WHERE LINE_NO > 0
           AND WEIGHT_BREAK_SLAB NOT IN ('BASIC', 'MIN')
           AND BUYRATEID = J.BUYRATEID
           AND LANE_NO = J.LANE_NO
           AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
           AND RATE_DESCRIPTION IN ('A FREIGHT RATE','Freight Rate','A Freight Rate');
        V_BREAK      := V_BREAK || ',' || V_BASE;
        V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
        V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        EXCEPTION WHEN NO_DATA_FOUND THEN
        V_BREAK      := V_BREAK || ',' || 'FLAT';
        V_CHARGERATE := V_CHARGERATE ||'0.00'||',';
        V_RATE_DESC  := V_RATE_DESC || 'A FREIGHT RATE'||',';
        END;
        IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2 THEN
          /*BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAF%' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN

            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
              dbms_output.put_line('V_BREAK'||V_BREAK);
            EXCEPTION WHEN NO_DATA_FOUND THEN
              V_BREAK      := V_BREAK || ',' || 'BAFM3';
              V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
          --  dbms_output.put_line('V_BREAK'||V_BREAK);
            dbms_output.put_line('V_CHARGERATE'||V_CHARGERATE);
            dbms_output.put_line('J.BUYRATEID'||J.BUYRATEID);
             dbms_output.put_line('J.LANE_NO'||J.LANE_NO);
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

             V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CSF' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;*/

          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ;
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 1 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          --COMMENTED BY SUBRAHMANYAM FOR 219973
          -- BEGIN

          DBMS_OUTPUT.put_line('--------COMMENTED BY SUBRAHMANYAM-------');
          /* SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;*/
          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no';
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 4 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN
          BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
              INTO V_BASE, V_TEMP
              FROM GT_TEMP_DATA_1
             WHERE LINE_NO > 0
               AND BUYRATEID = J.BUYRATEID
               AND LANE_NO = J.LANE_NO
               AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
               AND RATE_DESCRIPTION <> 'A FREIGHT RATE'
               AND WEIGHT_BREAK_SLAB = 'SURCHARGE';
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE := V_CHARGERATE || '-,';
          END;

        END IF;
      END IF;
      --@@ Commented by subrahmanyam for 219973
      /*  IF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =2) THEN
      V_BREAK := 'MIN,FLAT,CAFMIN,CAF%,BAFMIN,BAFM3,PSSMIN,PSSM3,CSF';
      ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =1) THEN
        V_BREAK := 'MIN,FLAT,FSBASIC,FSMIN,FSKG,,SSBASIC,SSMIN,SSKG';
        ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =4) THEN
          V_BREAK := 'MIN,FLAT,SURCHARGE';
      END IF;*/

      UPDATE GT_BASE_DATA
         SET WEIGHT_BREAK_SLAB = V_BREAK,
             RATE_DESCRIPTION  = V_RATE_DESC,
             CHARGERATE        = SUBSTR(V_CHARGERATE,
                                        1,
                                        LENGTH(V_CHARGERATE) - 1),
             CHECKED_FLAG      = SUBSTR(V_CHECKEDFLAG,
                                        1,
                                        LENGTH(V_CHECKEDFLAG) - 1)
       WHERE BUYRATEID = J.BUYRATEID
         AND LANE_NO = J.LANE_NO
         AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID;
      DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164
      IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'COPY' THEN
        print_out('-------ERROR------');
        DBMS_OUTPUT.put_line('SELECT RCB_FLAG
			INTO   V_RCB_FLAG
			FROM   GT_BASE_DATA
			WHERE  BUYRATEID = ' || J.BUYRATEID || '
			       AND LANE_NO =' || J.LANE_NO || '
			       AND VERSION_NO =' || J.VERSION_NO || '
			       AND NVL(REC_BUYRATE_ID
				      ,''P'') =' || J.REC_BUYRATE_ID || '
               AND RATE_DESCRIPTION = ' ||
                             '''A FREIGHT RATE''''');
        BEGIN
          V_RCB_FLAG := '';
          SELECT DISTINCT RCB_FLAG
            INTO V_RCB_FLAG
            FROM GT_BASE_DATA
           WHERE BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO
             AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID
          --  AND RATE_DESCRIPTION = 'A FREIGHT RATE'
          ;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('V_RCB_FLAG..' || V_RCB_FLAG);
        END;
        IF (V_RCB_FLAG = 'BR') THEN
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.BUYRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG = 'BR'
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        ELSE
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.SELLRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.REC_BUYRATE_ID = QR.BUYRATE_ID
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG IN ('RSR', 'CSR')
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        END IF;
      END IF;
    END LOOP;

    -- @@ Commented And Added for 219973
    /*       IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA WHERE RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID;-- COMMENTED BY SUBRAHMANYAM FOR 179985
        ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      ELSE
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA
      WHERE  RCB_FLAG <> 'BR' AND RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID; -- COMMENTED BY SUBRAHMANYAM FOR 179855
              ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      END IF;*/
  IF UPPER(P_OPERATION) = 'VIEW' THEN

  IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
          WHERE GBD.ORIGIN =P_ORG_LOC
              AND GBD.DESTINATION =P_DEST_LOC
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE

      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
             AND GBD.ORIGIN =P_ORG_LOC
              AND GBD.DESTINATION =P_DEST_LOC
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
              TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
               END IF;
  ELSE

    IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
    END IF;

    END IF;
    --@@ Ended for 219973
  END;
  -- Added By Kishroe Podili for 25Feb11
  PROCEDURE SINGLE_QUOTE_SEL_BUY_RATE_PROC(
    P_ORG_LOC      VARCHAR2,
    P_DEST_LOC     VARCHAR2,
    P_TERMINAL     VARCHAR2,
    P_SRVLEVEL     VARCHAR2,
    P_SHMODE       VARCHAR2,
    P_PERMISSION   VARCHAR2 DEFAULT 'Y',
    P_OPERATION    VARCHAR2,
    P_QUOTE_ID     VARCHAR2,
    P_WEIGHT_BREAK VARCHAR2,
    P_RS           OUT RESULTSET) AS
    V_RC_C1      RESULTSET;
    V_TERMINALS  VARCHAR2(32000);
    V_CHARGERATE VARCHAR2(400) := '';
    K            NUMBER := 0;
    V_SQL1       VARCHAR2(2000);
    V_SQLBNEW    VARCHAR2(2000);
    V_SQLSNEW    VARCHAR2(2000);
    V_SQLTBNEW   VARCHAR2(2000);
    V_SQLTSNEW   VARCHAR2(2000);
    V_SQL2       VARCHAR2(2000);
    V_SQL4       VARCHAR2(2000) := '';
    V_SQL5       VARCHAR2(2000);
    V_SQL6       VARCHAR2(32767);
    V_SQL7       VARCHAR2(2000);
    V_SQL10      VARCHAR2(2000);
    V_SQL11      VARCHAR2(2000);
    V_SQL12      VARCHAR2(32767);
    V_SQL13      VARCHAR2(2000);
    V_SQL14      VARCHAR2(2000);
    V_SQL15      VARCHAR2(2000);
    V_SQL16      VARCHAR2(2000);
    V_SQL17      VARCHAR2(2000);
    V_SQL18      VARCHAR2(2000);
    V_SQL19      VARCHAR2(2000);
    V_BASE       VARCHAR2(30);
    V_BREAK      VARCHAR2(4000);
    V_BREAK1     VARCHAR2(4000);
    V_TEMP       VARCHAR2(200);
    V_BREAK_SLAB VARCHAR2(40);
    --V_BREAK_SLAB     VARCHAR2(40);
    V_BREAK_SLAB_BAF  VARCHAR2(40);
    V_BREAK_SLAB_CAF  VARCHAR2(40);
    V_BREAK_SLAB_CSF  VARCHAR2(40);
    V_BREAK_SLAB_PSS  VARCHAR2(40);
    V_OPR_ADM_FLAG    VARCHAR2(3);
    V_RCB_FLAG        VARCHAR2(10);
    V_SELLBUYFLAG     VARCHAR2(10);
    V_SELLRATEID      NUMBER;
    V_BUYRATEID       NUMBER;
    V_LANENO          NUMBER;
    V_ID              NUMBER;
    V_QUOTERATEID     NUMBER;
    V_CON_BUYRATEID   NUMBER;
    V_CON_SELLRATEID  NUMBER;
    V_CON_LANENO      NUMBER;
    V_FLAG            VARCHAR2(200);
    V_CHECKEDFLAG     VARCHAR2(200);
    V_VERSIONNO       NUMBER;
    V_TEMP1           NUMBER;
    V_BASE1           NUMBER;
    v_rd              VARCHAR2(200); --added for 180164
    v_surcharge_count NUMBER;
    V_TEMP_BREAK      VARCHAR2(4000);
    V_TEMP_DESC       VARCHAR2(4000);
    V_RATE_DESC       VARCHAR2(4000);
    V_TEMP_CHARGERATE VARCHAR2(400) := '';
    V_SQL_SUR         VARCHAR2(4000) := '';
    TYPE ARRAY IS TABLE OF QMS_QUOTE_RATES.BREAK_POINT%TYPE;
    BREAK_POINT_LIST ARRAY;
    TYPE DESC_ARRAY IS TABLE OF QMS_QUOTE_RATES.Charge_Description%TYPE;
    CHARGE_DESC_LIST DESC_ARRAY;

  BEGIN
    SELECT OPER_ADMIN_FLAG
      INTO V_OPR_ADM_FLAG
      FROM FS_FR_TERMINALMASTER
     WHERE TERMINALID = P_TERMINAL;
    IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
      V_TERMINALS := 'SELECT 1 FROM FS_FR_TERMINALMASTER TM where TM.terminalid= ';
    ELSE
      DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
                               'v_terminal_id',
                               P_TERMINAL);
      V_TERMINALS := 'SELECT 1 FROM FETCH_TERMINAL_ID_VIEW TV where TV.term_id = ';
      --Union Can be replaced with union all: Sreenadh
    END IF;
    /*V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID) ';*/ --@@Modified by Kameswari for Surcharge Enhancements
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL1 := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID,TEMP_CHECK,RATE_DESCRIPTION) '; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL2 := 'SELECT distinct to_number(qbd.buyrateid) buyrateid, upper(qbd.weight_break_slab)  weight_break_slab,';
    --COMMENTED BY SUBRAHMANYAM FOR 180164
    --V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check';
    V_SQL4 := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL5 := '  FROM qms_buyrates_dtl qbd, qms_buyrates_master qbm WHERE qbd.buyrateid = qbm.buyrateid AND (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbd.origin =:v_org_loc AND qbd.destination=:v_dest_loc
	    AND qbd.service_level LIKE :v_srvlevel';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no  AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    V_SQL6 := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode AND QBM.WEIGHT_BREAK=:v_wt_brk and exists(' ||
              V_TERMINALS || 'qbm.TERMINALID)  ';
    --v_sql6 modified with exists :Sreenadh
    V_SQLTBNEW := ' SELECT  buyrateid,weight_break_slab,CHARGERATE, lane_no,line_no,id_flag,wtbreak ,REC_BUYRATEID,test_check,RATE_DESCRIPTION from (';
    V_SQL7     := ' SELECT distinct to_number(sm.rec_con_id) buyrateid, UPPER(sd.weightbreakslab) weight_break_slab,';
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check';
    V_SQL10 := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAMF FOR 180164
    V_SQL11 := ' FROM qms_rec_con_sellratesmaster sm, qms_rec_con_sellratesdtl sd, qms_buyrates_dtl qbd WHERE sm.rec_con_id = sd.rec_con_id  AND sd.origin =: v_org_loc AND sd.destination=:v_dest_loc AND sd.servicelevel_id LIKE :v_srvlevel
	    AND sd.buyrateid=qbd.buyrateid  AND sd.version_no=qbd.version_no AND sd.lane_no=qbd.lane_no /*AND sd.line_no=qbd.line_no */';
    /* V_SQL12 := ' AND sd.ai_flag =''A'' and exists (select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')  AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID ) UNION all ';*/
    --v_sql12 modified with exists :Sreenadh
    -- commented for 180164
    /*V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode
    AND (sd.RATE_DESCRIPTION=''A FREIGHT RATE'' or sd.RATE_DESCRIPTION IS NULL)AND  EXISTS (' ||
         V_TERMINALS || 'Sm.TERMINALID )) where test_check is not null  UNION all ';*/
    --added for 180164
    V_SQL12 := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode AND SM.WEIGHT_BREAK = :v_wt_brk
	    AND  EXISTS (' || V_TERMINALS ||
               'Sm.TERMINALID )) where test_check is not null  UNION all ';
    -- ended for 180164

    /*V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE)';*/
    --       V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK)';-- COMMENTED BY SUBRAHMANYAM FOR 180164
    V_SQL13 := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE)'; -- ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR THE 180164
    --      V_SQLBNEW :='select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check from ('; --@@Added for the performance issue on 20/03/09
    V_SQLBNEW := 'select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, UWGT,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check,RATE_DESCRIPTION,EXTERNAL_NOTES,DENSITY_CODE from ('; --ADDED BY SUBRAHMANYAM FOR 180164
    -- COMMENTED BY SUBRAHMANYAM FOR 180164
    --      V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check'; --@@Modified by Kameswari for Surcharge Enhancements
    V_SQL14 := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) UWGT,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION,QBD.EXTERNAL_NOTES,QBD.DENSITY_CODE'; --@@ADDED BY SUBRAHMANYAM FOR 180164
    V_SQL15 := 'SELECT distinct to_char(sD.REC_CON_ID) BUYRATEID,sd.version_no version_no,sD.CARRIER_ID carrier_id, sD.ORIGIN,sD.DESTINATION,sD.SERVICELEVEL_ID SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=sD.SERVICELEVEL_ID)SDESC,''a'' weight_break_slab,sD.TRANSIT_TIME,sD.FREQUENCY,''b''  charge_rate,sD.LANE_NO,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') RCB_FLAG, to_char(sD.BUYRATEID) REC_BUYRATE_ID,sD.NOTES,UPPER(SM.WEIGHT_BREAK) UWGT,SM.TERMINALID,sm.CURRENCY,sm.WEIGHT_CLASS,SD.EXTERNAL_NOTES,QBD.DENSITY_CODE';
    V_SQL16 := ', (select DISTINCT qbd.EFFECTIVE_FROM FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no ) efrom';
    /*V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type';*/
    V_SQL17 := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION';
    --commented for 180164
    /* V_SQL18 := '  \*AND NOT EXISTS
        (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
       WHERE qsd.ai_flag = ''A''
         AND QBD.BUYRATEID = QSD.BUYRATEID
         AND QBD.LANE_NO = QSD.LANE_NO
         AND QSD.ACCEPTANCE_FLAG IS NULL
         AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*\  AND (QBD.RATE_DESCRIPTION=''A FREIGHT RATE''or QBD.RATE_DESCRIPTION IS NULL)) where test_check is not null order by buyrateid,lane_no ';
    */
    -- added for 180164
    V_SQL18 := '  /*AND NOT EXISTS
	  (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd
	 WHERE qsd.ai_flag = ''A''
	   AND QBD.BUYRATEID = QSD.BUYRATEID
	   AND QBD.LANE_NO = QSD.LANE_NO
	   AND QSD.ACCEPTANCE_FLAG IS NULL
	   AND (Qsd.INVALIDATE = ''F'' OR Qsd.INVALIDATE IS NULL))*/ ) WHERE test_check IS NOT NULL ORDER BY buyrateid,lane_no ';
    -- ended for 180164
    EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_BASE_DATA');
    IF UPPER(P_OPERATION) = 'VIEW' THEN
      SELECT ID
        INTO V_QUOTERATEID
        FROM QMS_QUOTE_MASTER
       WHERE QUOTE_ID = P_QUOTE_ID
         AND VERSION_NO = (SELECT MAX(VERSION_NO)
                             FROM QMS_QUOTE_MASTER
                            WHERE QUOTE_ID = P_QUOTE_ID);
      SELECT DISTINCT SELL_BUY_FLAG,
                      SELLRATE_ID,
                      BUYRATE_ID,
                      RATE_LANE_NO,
                      QR.VERSION_NO
        INTO V_SELLBUYFLAG,
             V_SELLRATEID,
             V_BUYRATEID,
             V_LANENO,
             V_VERSIONNO
        FROM QMS_QUOTE_RATES  QR,
             QMS_QUOTE_MASTER QM,
             FS_RT_PLAN       PL,
             FS_RT_LEG        LEG
       WHERE QR.QUOTE_ID = QM.ID
         AND QM.QUOTE_ID = PL.QUOTE_ID
         AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
         AND LEG.ORIG_LOC = P_ORG_LOC
         AND LEG.DEST_LOC = P_DEST_LOC
         AND LEG.SERIAL_NO = QR.SERIAL_NO
         AND QM.QUOTE_ID = P_QUOTE_ID
         AND QM.VERSION_NO = (SELECT MAX(VERSION_NO)
                                FROM QMS_QUOTE_MASTER
                               WHERE QUOTE_ID = P_QUOTE_ID)
         AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR');
      IF V_SELLBUYFLAG = 'BR' THEN
        /*  SELECT BREAK_POINT BULK COLLECT -- commented and modified by phani sekhar for wpbn 178377 on 20090803
        INTO   BREAK_POINT_LIST
        FROM   QMS_QUOTE_RATES
        WHERE  QUOTE_ID = V_QUOTERATEID
               AND SELL_BUY_FLAG = 'BR';*/ -- added by phani sekhar for wpbn 178377 on 20090803
        SELECT BREAK_POINT, QR.CHARGE_DESCRIPTION BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES  QR,
               QMS_QUOTE_MASTER QM,
               FS_RT_PLAN       PL,
               FS_RT_LEG        LEG
         WHERE QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
               (SELECT MAX(VERSION_NO)
                  FROM QMS_QUOTE_MASTER
                 WHERE QUOTE_ID = P_QUOTE_ID)
           AND QR.SELL_BUY_FLAG IN ('BR'); --ends for 178377
        FORALL I IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT BD.BUYRATEID,
                   BD.WEIGHT_BREAK_SLAB,
                   BD.CHARGERATE,
                   BD.LANE_NO,
                   BD.LINE_NO,
                   'BR',
                   UPPER(BM.WEIGHT_BREAK),
                   '',
                   BD.RATE_DESCRIPTION
              FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
             WHERE BD.WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(I)
               AND BD.BUYRATEID = V_BUYRATEID
               AND BD.LANE_NO = V_LANENO
               AND BD.VERSION_NO = BM.VERSION_NO
               AND BD.VERSION_NO = V_VERSIONNO
               AND BM.BUYRATEID = BD.BUYRATEID
               AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
               AND BD.RATE_DESCRIPTION = CHARGE_DESC_LIST(I)
            -- for 180164
            /*AND (BD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         BD.RATE_DESCRIPTION IS NULL)*/
            ;
      INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           EXTERNAL_NOTES,
           DENSITY_CODE)
          SELECT DISTINCT BD.BUYRATEID,
                          V_VERSIONNO,
                          BD.CARRIER_ID,
                          BD.ORIGIN,
                          BD.DESTINATION,
                          BD.SERVICE_LEVEL,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = BD.SERVICE_LEVEL) SERVICE_LEVEL_DESC,
                          'a',
                          BD.TRANSIT_TIME,
                          BD.FREQUENCY,
                          'b',
                          BD.LANE_NO,
                          'BR',
                          '',
                          BD.NOTES,
                          UPPER(BM.WEIGHT_BREAK),
                          BM.TERMINALID,
                          BM.CURRENCY,
                          BM.WEIGHT_CLASS,
                          BD.EFFECTIVE_FROM,
                          /*BD.VALID_UPTO,*/
                          (SELECT DISTINCT BD.VALID_UPTO
                             FROM QMS_BUYRATES_DTL    BD,
                                  QMS_BUYRATES_MASTER BM,
                                  QMS_QUOTE_RATES     QR
                            WHERE QR.BUYRATE_ID = BD.BUYRATEID
                                 /*AND QR.VERSION_NO = BD.VERSION_NO*/
                              AND QR.RATE_LANE_NO = BD.LANE_NO
                              AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
                              AND BD.BUYRATEID = V_BUYRATEID
                                 --  AND BD.VERSION_NO = V_VERSIONNO
                              AND BD.VERSION_NO = BM.VERSION_NO
                              AND BD.LANE_NO = V_LANENO
                              AND QR.QUOTE_ID = V_QUOTERATEID
                              AND (BM.LANE_NO = BD.LANE_NO OR
                                  BM.LANE_NO IS NULL)
                              AND BM.BUYRATEID = BD.BUYRATEID
                              AND BD.VERSION_NO =
                                  (SELECT MAX(VERSION_NO)
                                     FROM QMS_BUYRATES_DTL
                                    WHERE BUYRATEID = BD.BUYRATEID
                                      AND LANE_NO = BD.LANE_NO)),
                          'Y',
                          BM.CONSOLE_TYPE,
                          BD.RATE_DESCRIPTION,
                          BD.external_notes,
                          BD.DENSITY_CODE
            FROM QMS_BUYRATES_DTL    BD,
                 QMS_BUYRATES_MASTER BM,
                 QMS_QUOTE_RATES     QR
           WHERE QR.BUYRATE_ID = BD.BUYRATEID
             AND QR.VERSION_NO = BD.VERSION_NO
             AND QR.RATE_LANE_NO = BD.LANE_NO
             AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
             AND BD.BUYRATEID = V_BUYRATEID
             AND BD.VERSION_NO = V_VERSIONNO
             AND BD.VERSION_NO = BM.VERSION_NO
             AND BD.LANE_NO = V_LANENO
             AND QR.QUOTE_ID = V_QUOTERATEID
             AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
             AND BM.BUYRATEID = BD.BUYRATEID
          -- for 180164
          /*AND
                       (BD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       BD.RATE_DESCRIPTION IS NULL)*/
          ;

      ELSE
        SELECT BREAK_POINT, DECODE(CHARGE_DESCRIPTION,'Freight Rate','A FREIGHT RATE',CHARGE_DESCRIPTION) BULK COLLECT
          INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
          FROM QMS_QUOTE_RATES
         WHERE QUOTE_ID = V_QUOTERATEID
           AND SELL_BUY_FLAG = 'RSR';
        FORALL J IN 1 .. BREAK_POINT_LIST.COUNT
          INSERT INTO GT_TEMP_DATA_1
            (BUYRATEID,
             WEIGHT_BREAK_SLAB,
             CHARGERATE,
             LANE_NO,
             LINE_NO,
             ID_FLAG,
             WEIGHT_BREAK,
             REC_BUYRATEID,
             RATE_DESCRIPTION)
            SELECT SD.REC_CON_ID,
                   SD.WEIGHTBREAKSLAB,
                   SD.CHARGERATE,
                   SD.LANE_NO,
                   SD.LINE_NO,
                   DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                   UPPER(SM.WEIGHT_BREAK),
                   SD.BUYRATEID,
                   SD.RATE_DESCRIPTION
              FROM QMS_REC_CON_SELLRATESDTL    SD,
                   QMS_REC_CON_SELLRATESMASTER SM
             WHERE SD.WEIGHTBREAKSLAB = BREAK_POINT_LIST(J)
               AND SD.RATE_DESCRIPTION = CHARGE_DESC_LIST(J)
               AND SD.REC_CON_ID = V_SELLRATEID
               AND SD.BUYRATEID = V_BUYRATEID
               AND SD.LANE_NO = V_LANENO
               AND SD.VERSION_NO = V_VERSIONNO
               AND SD.REC_CON_ID = SM.REC_CON_ID
               AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') =
                   V_SELLBUYFLAG
            -- for 180164
            /*AND (SD.RATE_DESCRIPTION =
                         'A FREIGHT RATE' OR
                         SD.RATE_DESCRIPTION IS NULL)*/
            ;
        INSERT INTO GT_BASE_DATA
          (BUYRATEID,
           VERSION_NO,
           CARRIER_ID,
           ORIGIN,
           DESTINATION,
           SERVICE_LEVEL,
           SERVICE_LEVEL_DESC,
           WEIGHT_BREAK_SLAB,
           TRANSIT_TIME,
           FREQUENCY,
           CHARGERATE,
           LANE_NO,
           RCB_FLAG,
           REC_BUYRATE_ID,
           NOTES,
           WEIGHT_BREAK,
           TERMINALID,
           CURRENCY,
           WT_CLASS,
           EFROM,
           VALIDUPTO,
           SELECTED_FLAG,
           CONSOLE_TYPE,
           RATE_DESCRIPTION,
           external_notes,
           DENSITY_CODE)
          SELECT DISTINCT SD.REC_CON_ID,
                          V_VERSIONNO,
                          SD.CARRIER_ID,
                          SD.ORIGIN,
                          SD.DESTINATION,
                          SD.SERVICELEVEL_ID,
                          (SELECT DISTINCT SERVICELEVELDESC
                             FROM FS_FR_SERVICELEVELMASTER
                            WHERE SERVICELEVELID = SD.SERVICELEVEL_ID) SERVICE_LEVEL_DESC,
                          'a',
                          SD.TRANSIT_TIME,
                          SD.FREQUENCY,
                          'b',
                          SD.LANE_NO,
                          DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
                          SD.BUYRATEID,
                          SD.NOTES,
                          UPPER(SM.WEIGHT_BREAK),
                          SM.TERMINALID,
                          SM.CURRENCY,
                          SM.WEIGHT_CLASS,
                          (SELECT DISTINCT QBD.EFFECTIVE_FROM
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.REC_CON_ID = SD.REC_CON_ID
                              AND QSD.BUYRATEID = SD.BUYRATEID
                              AND QSD.LANE_NO = SD.LANE_NO
                              AND QSD.VERSION_NO = SD.VERSION_NO
                              AND QSD.VERSION_NO = QBD.VERSION_NO),
                          (SELECT DISTINCT QBD.VALID_UPTO
                             FROM QMS_REC_CON_SELLRATESDTL QSD,
                                  QMS_BUYRATES_DTL         QBD
                            WHERE QSD.BUYRATEID = QBD.BUYRATEID
                              AND QSD.LANE_NO = QBD.LANE_NO
                              AND QSD.VERSION_NO =
                                  (SELECT MAX(RSD1.VERSION_NO)
                                     FROM QMS_REC_CON_SELLRATESDTL RSD1
                                    WHERE RSD1.BUYRATEID = QSD.BUYRATEID
                                      AND RSD1.LANE_NO = QSD.LANE_NO
                                      AND RSD1.BUYRATEID = V_BUYRATEID
                                      AND RSD1.LANE_NO = V_LANENO)
                              AND QBD.VERSION_NO = QSD.VERSION_NO),
                          'Y',
                          SD.CONSOLE_TYPE,
                          SD.RATE_DESCRIPTION,
                          sd.external_notes,
                          SD.DENSITY_CODE
            FROM QMS_REC_CON_SELLRATESDTL    SD,
                 QMS_REC_CON_SELLRATESMASTER SM,
                 QMS_QUOTE_RATES             QR
           WHERE QR.BUYRATE_ID = SD.BUYRATEID
             AND QR.SELLRATE_ID = SD.REC_CON_ID
             AND QR.VERSION_NO = SD.VERSION_NO
             AND QR.RATE_LANE_NO = SD.LANE_NO
             AND QR.BREAK_POINT = SD.WEIGHTBREAKSLAB
             AND SD.REC_CON_ID = V_SELLRATEID
             AND SD.BUYRATEID = V_BUYRATEID
             AND SD.LANE_NO = V_LANENO
             AND SD.VERSION_NO = V_VERSIONNO
             AND SD.REC_CON_ID = SM.REC_CON_ID
             AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') = V_SELLBUYFLAG
          -- for 180164
          /* AND
                       (SD.RATE_DESCRIPTION = 'A FREIGHT RATE' OR
                       SD.RATE_DESCRIPTION IS NULL)*/
          ;
      END IF;
    ELSE

      /*EXECUTE IMMEDIATE (V_SQL1 || V_SQL7 || V_SQL10 || V_SQL11 ||
            V_SQL12 || V_SQL2 || V_SQL4 || V_SQL5 ||
            V_SQL6 || V_SQL18 || ',LINE_NO')
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/

      print_out('------P_WEIGHT_BREAK-------' || P_WEIGHT_BREAK);
      print_out(V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 || V_SQL11 ||
                V_SQL12 || V_SQLTBNEW || V_SQL2 || V_SQL4 || V_SQL5 ||
                V_SQL6 || V_SQL18 || ',LINE_NO');

      EXECUTE IMMEDIATE (V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 ||
                        V_SQL11 || V_SQL12 || V_SQLTBNEW || V_SQL2 ||
                        V_SQL4 || V_SQL5 || V_SQL6 || V_SQL18 ||
                        ',LINE_NO')
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;
      /*EXECUTE IMMEDIATE (V_SQL13 || V_SQL15 || V_SQL16 || V_SQL17 ||
            V_SQL11 || V_SQL12 || V_SQL14 || V_SQL5 ||
            V_SQL6 || V_SQL18)
      USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE;*/
      DBMS_OUTPUT.put_line('--------2--------------------');
      print_out(V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 || V_SQL17 ||
                V_SQL11 || V_SQL12 || V_SQLBNEW || V_SQL14 || V_SQL5 ||
                V_SQL6 || V_SQL18);

      EXECUTE IMMEDIATE (V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 ||
                        V_SQL17 || V_SQL11 || V_SQL12 || V_SQLBNEW ||
                        V_SQL14 || V_SQL5 || V_SQL6 || V_SQL18)
        USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE, P_WEIGHT_BREAK;

    END IF;


    --@@ Commented by subrahmanyam for the 219973 for Dynamic SurCharges.
    DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164

    IF P_OPERATION = 'Modify' THEN
      BEGIN
        FOR K IN (SELECT DISTINCT QR.SELL_BUY_FLAG,
                                  QM.ID,
                                  QR.BUYRATE_ID,
                                  QR.SELLRATE_ID,
                                  QR.RATE_LANE_NO
                    FROM QMS_QUOTE_RATES  QR,
                         QMS_QUOTE_MASTER QM,
                         FS_RT_PLAN       PL,
                         FS_RT_LEG        LEG
                   WHERE QR.QUOTE_ID = QM.ID
                     AND QM.QUOTE_ID = PL.QUOTE_ID
                     AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
                     AND LEG.ORIG_LOC = P_ORG_LOC
                     AND LEG.DEST_LOC = P_DEST_LOC
                     AND LEG.SERIAL_NO = QR.SERIAL_NO
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QM.VERSION_NO =
                         (SELECT MAX(VERSION_NO)
                            FROM QMS_QUOTE_MASTER
                           WHERE QUOTE_ID = P_QUOTE_ID)
                     AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')) LOOP

          V_SELLBUYFLAG    := K.SELL_BUY_FLAG;
          V_ID             := K.ID;
          V_CON_BUYRATEID  := K.BUYRATE_ID;
          V_CON_SELLRATEID := K.SELLRATE_ID;
          V_CON_LANENO     := K.RATE_LANE_NO;

          /*  SELECT DISTINCT QR.SELL_BUY_FLAG, QM.ID, QR.BUYRATE_ID,
              QR.SELLRATE_ID, QR.RATE_LANE_NO
          INTO   V_SELLBUYFLAG, V_ID, V_CON_BUYRATEID,
                 V_CON_SELLRATEID, V_CON_LANENO
          /*FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM -- commented and modified by phani sekhar for wpbn 178377 on 20090803
          WHERE  QR.QUOTE_ID = QM.ID
                 AND QM.QUOTE_ID = P_QUOTE_ID
                 AND QM.ACTIVE_FLAG = 'A'
                 AND
                 QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');
                 -- added by phani sekhar for wpbn 178377 on 20090803
                  FROM   QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM, FS_RT_PLAN PL,
           FS_RT_LEG LEG
          WHERE  QR.QUOTE_ID = QM.ID
           AND QM.QUOTE_ID = PL.QUOTE_ID
           AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
           AND LEG.ORIG_LOC = P_ORG_LOC
           AND LEG.DEST_LOC = P_DEST_LOC
           AND LEG.SERIAL_NO = QR.SERIAL_NO
           AND QM.QUOTE_ID = P_QUOTE_ID
           AND QM.VERSION_NO =
           (SELECT MAX(VERSION_NO)
                FROM   QMS_QUOTE_MASTER
                WHERE  QUOTE_ID = P_QUOTE_ID)
          AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');*/

          --ends 178377

          SELECT BREAK_POINT, charge_description BULK COLLECT
            INTO BREAK_POINT_LIST, CHARGE_DESC_LIST
            FROM QMS_QUOTE_RATES
           WHERE QUOTE_ID = V_ID
             AND SELL_BUY_FLAG = V_SELLBUYFLAG;
          FORALL K IN 1 .. BREAK_POINT_LIST.COUNT
            UPDATE GT_TEMP_DATA_1
               SET CHECKED_FLAG = 'Y'
             WHERE BUYRATEID = (CASE WHEN V_SELLBUYFLAG = 'BR' THEN
                    V_CON_BUYRATEID ELSE V_CON_SELLRATEID END)
               AND LANE_NO = V_CON_LANENO
               AND WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(K)
               AND RATE_DESCRIPTION = CHARGE_DESC_LIST(K);
        END LOOP;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_SELLBUYFLAG := '';

      END;
    END IF;
    FOR J IN (SELECT DISTINCT BUYRATEID,
                              VERSION_NO,
                              LANE_NO,
                              WEIGHT_BREAK,
                              RCB_FLAG,
                              NVL(REC_BUYRATE_ID, 'P') REC_BUYRATE_ID
                FROM GT_BASE_DATA
               WHERE RATE_DESCRIPTION LIKE 'A FREIGHT RATE'
               ORDER BY BUYRATEID, LANE_NO) LOOP
      V_CHARGERATE      := '';
      V_CHECKEDFLAG     := '';
      K                 := 1;
      V_BREAK           := '';
      v_surcharge_count := 0;
      V_TEMP_BREAK      := '';
      V_TEMP_CHARGERATE := '';
      V_RATE_DESC       := '';
      IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1) = FALSE THEN
        BEGIN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND WEIGHT_BREAK_SLAB=''BASIC'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_TEMP_BREAK;
          V_CHARGERATE := V_TEMP_CHARGERATE || ',';
          V_RATE_DESC  := V_TEMP_DESC || ',';

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            v_surcharge_count := v_surcharge_count + 1;
            V_BREAK           := 'BASIC';
            V_CHARGERATE      := '-' || ',';
            V_RATE_DESC       := 'A FREIGHT RATE' || ',';
            BEGIN
            EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE),RATE_DESCRIPTION  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no>=0  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
              INTO V_TEMP_BREAK, V_TEMP_CHARGERATE, V_TEMP_DESC
              USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
            V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
            V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
             WHEN NO_DATA_FOUND THEN
             V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
            V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
            V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
            END;
        END;
        IF v_surcharge_count = 0 THEN
          EXECUTE IMMEDIATE ('select DISTINCT WEIGHT_BREAK_SLAB,to_char(CHARGERATE)  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=1  AND WEIGHT_BREAK_SLAB=''MIN'' AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
            INTO V_TEMP_BREAK, V_TEMP_CHARGERATE
            USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_BREAK      := V_BREAK || ',' || V_TEMP_BREAK;
          V_CHARGERATE := V_CHARGERATE || V_TEMP_CHARGERATE;
          V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';

        END IF;
      END IF;
      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      ELSIF UPPER(J.WEIGHT_BREAK) = 'FLAT' OR
            UPPER(J.WEIGHT_BREAK) = 'SLAB' THEN
        V_CHARGERATE := V_CHARGERATE || ',';
      END IF;
      IF (J.WEIGHT_BREAK <> 'FLAT') THEN
        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          --            V_SQL1 := ' AND line_no > 0 order by break_slab';
          V_SQL1 := ' AND line_no > 0 AND UPPER(WEIGHT_BREAK_SLAB) NOT IN(''BASIC'',''MIN'') order by break_slab';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          V_SQL1 := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                    J.LANE_NO || ' order by break_slab';
        ELSE
          --           V_SQL1 := ' AND line_no > 0 order by to_number(break_slab)';-- COMMENTED FOR 180164
          V_SQL1 := ' AND line_no > 0 AND UPPER(WEIGHT_BREAK_SLAB) NOT IN(''BASIC'',''MIN'') order by to_number(break_slab)';
          -- V_SQL1 := ' AND line_no > 0 order by LINE_NO';
        END IF;
        -- COMMENTED FOR 180164
        --  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1 -- commented by subrahmanyam for 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL1
          USING J.WEIGHT_BREAK;

        --ADDED FOR 180164
        /*  OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
                    USING J.WEIGHT_BREAK;
        */
        LOOP
          FETCH V_RC_C1
          --INTO V_BREAK_SLAB,V_LINE_NO ;--ADDED FOR 180164
            INTO V_BREAK_SLAB; -- COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            /*            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
            AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
            and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
            */
            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag,RATE_DESCRIPTION FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=''A FREIGHT RATE'' ')
              INTO V_TEMP, V_FLAG, V_TEMP_DESC
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-' || ',';
          END;
          /*If UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2
           Then
           V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            BEGIN
            EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
           AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_BAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CAF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_CSF, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';
            BEGIN
          EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
          AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
          and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
          INTO V_TEMP, V_FLAG
          USING V_BREAK_SLAB_PSS, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
          V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
          V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
          V_CHARGERATE  := V_CHARGERATE || '-,';
          V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
          END;

           End If;*/
        END LOOP;
        CLOSE V_RC_C1;

        --@@ Added by subrahmanyam for the 180164
        V_TEMP_DESC := '';
        /*      IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
                    V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
                    V_SQL_SUR := ' order by line_no and '||j.buyrateid ||' and '||j.lane_no;
              ELSE
                   V_SQL_SUR := ' AND line_no > 0 AND WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'')  AND RATE_DESCRIPTION <>''A FREIGHT RATE'' order by line_no and  '||j.buyrateid ||' and '||j.lane_no ||' and to_number(break_slab)';
              END IF;
        */

        IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_SQL_SUR := ' AND line_no > 0 AND /*WEIGHT_BREAK_SLAB NOT IN(''BASIC'',''MIN'') AND*/ RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no ';
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
          --            V_SQL_SUR := ' and buyrateid='||j.buyrateid ||' and lane_no='||j.lane_no ||' order by line_no  order by line_no ';
          V_SQL_SUR := ' AND BUYRATEID=' || J.BUYRATEID || ' AND LANE_NO=' ||
                       J.LANE_NO || ' order by break_slab ';
        ELSE
          V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                       j.buyrateid || ' and lane_no=' || j.lane_no ||
                       ' order by line_no';
        END IF;
        V_TEMP_BREAK := '';
        OPEN V_RC_C1 FOR 'SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
          USING J.WEIGHT_BREAK;
        LOOP
          FETCH V_RC_C1
            INTO V_BREAK_SLAB, V_TEMP_DESC;
          EXIT WHEN V_RC_C1%NOTFOUND;
          BEGIN

            EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
              INTO V_TEMP, V_FLAG
              USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
            V_TEMP_BREAK  := V_TEMP_BREAK || V_BREAK_SLAB || ',';
            V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
            V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
            V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_TEMP_BREAK  := V_BREAK_SLAB || ',';
              V_CHARGERATE  := V_CHARGERATE || '-,';
              V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
              V_RATE_DESC   := V_RATE_DESC || '-,';
          END;

        END LOOP;
        CLOSE V_RC_C1;
        /*   IF P_SHMODE =1 THEN
                   \* V_BREAK_SLAB:='FSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB:='FSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='FSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSBASIC';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB :='SSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                     V_BREAK_SLAB    :='SSKG';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;*\
                    DBMS_OUTPUT.put_line('----FOR AIR-----');
                  ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE =2
                  THEN
                      V_BREAK_SLAB    :='CAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CAF%';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='BAFM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSMIN';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='PSSM3';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                    V_BREAK_SLAB    :='CSF';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
                ELSIF P_SHMODE =4 THEN
                V_BREAK_SLAB    :='SURCHARGE';
                     BEGIN

                    EXECUTE IMMEDIATE (' SELECT  TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
                    AND BUYRATEID =:buy_rate_id and LANE_NO=:lane_no
                    and nvl(REC_BUYRATEID,''P'')=:rec_buyrate_id')
                    INTO V_TEMP, V_FLAG
                    USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
                    V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                    V_CHARGERATE  := V_CHARGERATE || '-,';
                    V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                    END;
             END IF;
        */
        --@@ Ended by subrahmanyam for the 180164

        V_BASE  := V_BREAK;
        V_BREAK := '';
        -- COMMENTED FOR 180164
        OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab FROM GT_TEMP_DATA_1 WHERE rate_description=''A FREIGHT RATE'' and weight_break =:weight_break' || V_SQL1
          USING J.WEIGHT_BREAK;
        -- ADDED FOR 180164
        /* OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,LINE_NO FROM GT_TEMP_DATA_1 WHERE weight_break =:weight_break' || V_SQL1
        USING J.WEIGHT_BREAK;*/
        LOOP
          FETCH V_RC_C1
          -- INTO V_BREAK_SLAB,V_LINE_NO;--ADDED FOR 180164
            INTO V_BREAK_SLAB; --COMMENTED FOR 180164
          EXIT WHEN V_RC_C1%NOTFOUND;
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
            /*V_BREAK_SLAB_BAF :=V_BREAK_SLAB||'BAF';
            V_BREAK_SLAB_CAF :=V_BREAK_SLAB||'CAF';
            V_BREAK_SLAB_CSF :=V_BREAK_SLAB||'CSF';
            V_BREAK_SLAB_PSS :=V_BREAK_SLAB||'PSS';*/

            -- V_BREAK :=V_BREAK||','||V_BREAK_SLAB_BAF||','||V_BREAK_SLAB_CAF||','||V_BREAK_SLAB_CSF||','||V_BREAK_SLAB_PSS;
          ELSIF P_SHMODE = 1 OR P_SHMODE = 4 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          ELSIF UPPER(J.WEIGHT_BREAK) = 'SLAB' AND P_SHMODE = 2 THEN
            V_BREAK := V_BREAK || ',' || V_BREAK_SLAB;
          END IF;
        END LOOP;
        CLOSE V_RC_C1;
        --@@Added by subrahmanyam for 180164
        IF P_SHMODE = 1 THEN
          --V_BREAK := V_BREAK || ',' ||'FSBASIC'||','||'FSMIN'||','||'FSKG'||','||'SSBASIC'||','||'SSMIN'||','||'SSKG';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
          -- ELSIF UPPER(J.WEIGHT_BREAK) != 'LIST' AND P_SHMODE = 2 THEN
        ELSIF P_SHMODE = 2 THEN
          -- V_BREAK := V_BREAK ||','||'CAFMIN'||','||'CAF%'||','||'BAFMIN'||','||'BAFM3'||','||'PSSMIN'||','||'PSSM3'||','||'CSF';
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        ELSIF P_SHMODE = 4 THEN
 /*          V_BREAK := V_BREAK || ',' || 'SURCHARGE';COMMENTED BY gOVIND FOR TRUCK RATES*/
          V_BREAK := V_BREAK || ',' || V_TEMP_BREAK;
        END IF;

        --@@ Ended by subrahmanyam for 180164
        IF (UPPER(J.WEIGHT_BREAK) = 'SLAB') THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
          V_BREAK := V_BASE || V_BREAK;
        ELSE
          V_BREAK := SUBSTR(V_BREAK, 2, LENGTH(V_BREAK));
        END IF;
      ELSE
        BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE, RATE_DESCRIPTION
          INTO V_BASE, V_TEMP, V_TEMP_DESC
          FROM GT_TEMP_DATA_1
         WHERE LINE_NO > 0
           AND WEIGHT_BREAK_SLAB NOT IN ('BASIC', 'MIN')
           AND BUYRATEID = J.BUYRATEID
           AND LANE_NO = J.LANE_NO
           AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
           AND RATE_DESCRIPTION = 'A FREIGHT RATE';
        V_BREAK      := V_BREAK || ',' || V_BASE;
        V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
        V_RATE_DESC  := V_RATE_DESC || V_TEMP_DESC || ',';
        EXCEPTION
                    WHEN NO_DATA_FOUND THEN
        V_BREAK      := V_BREAK || ',' ;
        V_CHARGERATE := V_CHARGERATE  || ',';
        V_RATE_DESC  := V_RATE_DESC || ',';
        END;
        IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2 THEN
          /*BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CAF%' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
             BEGIN

            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='BAFM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
              dbms_output.put_line('V_BREAK'||V_BREAK);
            EXCEPTION WHEN NO_DATA_FOUND THEN
              V_BREAK      := V_BREAK || ',' || 'BAFM3';
              V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
          --  dbms_output.put_line('V_BREAK'||V_BREAK);
            dbms_output.put_line('V_CHARGERATE'||V_CHARGERATE);
            dbms_output.put_line('J.BUYRATEID'||J.BUYRATEID);
             dbms_output.put_line('J.LANE_NO'||J.LANE_NO);
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSMIN' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN

             V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='PSSM3' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;
            BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
            INTO   V_BASE, V_TEMP
            FROM   GT_TEMP_DATA_1
            WHERE  LINE_NO > 0
                   AND BUYRATEID = J.BUYRATEID
                   AND LANE_NO = J.LANE_NO
                   AND NVL(REC_BUYRATEID
                    ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='CSF' ;
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
            EXCEPTION WHEN NO_DATA_FOUND THEN
            V_CHARGERATE := V_CHARGERATE ||'-,';
            END;*/

          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ;
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT DISTINCT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 1 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN

          --COMMENTED BY SUBRAHMANYAM FOR 219973
          -- BEGIN

          DBMS_OUTPUT.put_line('--------COMMENTED BY SUBRAHMANYAM-------');
          /* SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='FSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSBASIC' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSMIN' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;
          BEGIN
          SELECT WEIGHT_BREAK_SLAB, CHARGERATE
          INTO   V_BASE, V_TEMP
          FROM   GT_TEMP_DATA_1
          WHERE  LINE_NO > 0
                 AND BUYRATEID = J.BUYRATEID
                 AND LANE_NO = J.LANE_NO
                 AND NVL(REC_BUYRATEID
                  ,'P') = J.REC_BUYRATE_ID AND RATE_DESCRIPTION <> 'A FREIGHT RATE' AND WEIGHT_BREAK_SLAB='SSKG' ;
          V_BREAK      := V_BREAK || ',' || V_BASE;
          V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION WHEN NO_DATA_FOUND THEN
          V_CHARGERATE := V_CHARGERATE ||'-,';
          END;*/
          V_TEMP_DESC := '';
          IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE''  and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no ';
          ELSIF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1 THEN
            V_SQL_SUR := ' and buyrateid=' || j.buyrateid ||
                         ' and lane_no=' || j.lane_no ||
                         ' order by line_no  order by line_no ';
          ELSE
            V_SQL_SUR := ' AND line_no > 0 AND  RATE_DESCRIPTION <>''A FREIGHT RATE'' and buyrateid=' ||
                         j.buyrateid || ' and lane_no=' || j.lane_no ||
                         ' order by line_no';
          END IF;

          print_out('----- SELECT  weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' ||
                    V_SQL_SUR);
          OPEN V_RC_C1 FOR 'SELECT weight_break_slab break_slab,rate_description FROM GT_TEMP_DATA_1 WHERE rate_description<>''A FREIGHT RATE'' and  weight_break =:weight_break ' || V_SQL_SUR
            USING J.WEIGHT_BREAK;
          LOOP
            FETCH V_RC_C1
              INTO V_BREAK_SLAB, V_TEMP_DESC;
            EXIT WHEN V_RC_C1%NOTFOUND;
            BEGIN

              EXECUTE IMMEDIATE (' SELECT DISTINCT TO_CHAR(chargerate), checked_flag FROM GT_TEMP_DATA_1   WHERE weight_break_slab =:weight_break_slab
					  AND BUYRATEID =:buy_rate_id AND LANE_NO=:lane_no
					  AND NVL(REC_BUYRATEID,''P'')=:rec_buyrate_id AND RATE_DESCRIPTION=:V_TEMP_DESC ')
                INTO V_TEMP, V_FLAG
                USING V_BREAK_SLAB, J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID, V_TEMP_DESC;
              V_BREAK       := V_BREAK || ',' || V_BREAK_SLAB;
              V_CHARGERATE  := V_CHARGERATE || V_TEMP || ',';
              V_CHECKEDFLAG := V_CHECKEDFLAG || V_FLAG || ',';
              V_RATE_DESC   := V_RATE_DESC || V_TEMP_DESC || ',';
            EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_BREAK       := V_BREAK_SLAB || ',';
                V_CHARGERATE  := V_CHARGERATE || '-,';
                V_CHECKEDFLAG := V_CHECKEDFLAG || '-,';
                V_RATE_DESC   := V_RATE_DESC || '-,';
            END;

          END LOOP;
          CLOSE V_RC_C1;

        ELSIF P_SHMODE = 4 AND UPPER(J.WEIGHT_BREAK) = 'FLAT' THEN
          BEGIN
            SELECT WEIGHT_BREAK_SLAB, CHARGERATE
              INTO V_BASE, V_TEMP
              FROM GT_TEMP_DATA_1
             WHERE LINE_NO > 0
               AND BUYRATEID = J.BUYRATEID
               AND LANE_NO = J.LANE_NO
               AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
               AND RATE_DESCRIPTION <> 'A FREIGHT RATE'
               AND WEIGHT_BREAK_SLAB = 'SURCHARGE';
            V_BREAK      := V_BREAK || ',' || V_BASE;
            V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_CHARGERATE := V_CHARGERATE || ',';
          END;

        END IF;
      END IF;
      --@@ Commented by subrahmanyam for 219973
      /*  IF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =2) THEN
      V_BREAK := 'MIN,FLAT,CAFMIN,CAF%,BAFMIN,BAFM3,PSSMIN,PSSM3,CSF';
      ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =1) THEN
        V_BREAK := 'MIN,FLAT,FSBASIC,FSMIN,FSKG,,SSBASIC,SSMIN,SSKG';
        ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE =4) THEN
          V_BREAK := 'MIN,FLAT,SURCHARGE';
      END IF;*/

      UPDATE GT_BASE_DATA
         SET WEIGHT_BREAK_SLAB = V_BREAK,
             RATE_DESCRIPTION  = V_RATE_DESC,
             CHARGERATE        = SUBSTR(V_CHARGERATE,
                                        1,
                                        LENGTH(V_CHARGERATE) - 1),
             CHECKED_FLAG      = SUBSTR(V_CHECKEDFLAG,
                                        1,
                                        LENGTH(V_CHECKEDFLAG) - 1)
       WHERE BUYRATEID = J.BUYRATEID
         AND LANE_NO = J.LANE_NO
         AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID;
      DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164
      IF UPPER(P_OPERATION) = 'MODIFY' OR UPPER(P_OPERATION) = 'COPY' THEN
        print_out('-------ERROR------');
        DBMS_OUTPUT.put_line('SELECT RCB_FLAG
			INTO   V_RCB_FLAG
			FROM   GT_BASE_DATA
			WHERE  BUYRATEID = ' || J.BUYRATEID || '
			       AND LANE_NO =' || J.LANE_NO || '
			       AND VERSION_NO =' || J.VERSION_NO || '
			       AND NVL(REC_BUYRATE_ID
				      ,''P'') =' || J.REC_BUYRATE_ID || '
               AND RATE_DESCRIPTION = ' ||
                             '''A FREIGHT RATE''''');
        BEGIN
          V_RCB_FLAG := '';
          SELECT DISTINCT RCB_FLAG
            INTO V_RCB_FLAG
            FROM GT_BASE_DATA
           WHERE BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO
             AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID
          --  AND RATE_DESCRIPTION = 'A FREIGHT RATE'
          ;
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
            DBMS_OUTPUT.put_line('V_RCB_FLAG..' || V_RCB_FLAG);
        END;
        IF (V_RCB_FLAG = 'BR') THEN
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.BUYRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG = 'BR'
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        ELSE
          UPDATE GT_BASE_DATA BD
             SET BD.SELECTED_FLAG = 'Y'
           WHERE EXISTS (SELECT /*+ nl_SJ*/
                   1
                    FROM QMS_QUOTE_RATES QR, QMS_QUOTE_MASTER QM
                   WHERE BD.BUYRATEID = QR.SELLRATE_ID
                     AND BD.LANE_NO = QR.RATE_LANE_NO
                     AND BD.REC_BUYRATE_ID = QR.BUYRATE_ID
                     AND BD.VERSION_NO = QR.VERSION_NO
                     AND QM.ID = QR.QUOTE_ID
                     AND QM.QUOTE_ID = P_QUOTE_ID
                     AND QR.SELL_BUY_FLAG IN ('RSR', 'CSR')
                     AND QM.ACTIVE_FLAG = 'A')
             AND BUYRATEID = J.BUYRATEID
             AND LANE_NO = J.LANE_NO
             AND VERSION_NO = J.VERSION_NO;
        END IF;
      END IF;
    END LOOP;

    -- @@ Commented And Added for 219973
    /*       IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA WHERE RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID;-- COMMENTED BY SUBRAHMANYAM FOR 179985
        ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      ELSE
      OPEN P_RS FOR
      SELECT *
      FROM   GT_BASE_DATA
      WHERE  RCB_FLAG <> 'BR' AND RATE_DESCRIPTION = 'A FREIGHT RATE'
    --  ORDER  BY WEIGHT_BREAK, BUYRATEID; -- COMMENTED BY SUBRAHMANYAM FOR 179855
              ORDER  BY WEIGHT_BREAK,ORIGIN,DESTINATION,CARRIER_ID,SERVICE_LEVEL,RCB_FLAG,TERMINALID;--ADDED BY SUBRAHMANYAM FOR THE 180164
      END IF;*/

    IF P_PERMISSION = 'Y' THEN
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID;
    ELSE
      OPEN P_RS FOR
        SELECT DISTINCT GBD.*
          FROM GT_BASE_DATA GBD
         WHERE RCB_FLAG <> 'BR'
         ORDER BY WEIGHT_BREAK,
                  ORIGIN,
                  DESTINATION,
                  CARRIER_ID,
                  SERVICE_LEVEL,
                  RCB_FLAG,
                  TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
    END IF;
    --ROLLBACK;
    --@@ Ended for 219973
  END;

  PROCEDURE MULTI_QUOTE_DETAILED_VIEW_PROC(P_QUOTEID VARCHAR2,
                                  --modified by subrahmanyam for the wpbn id:146971
                                  P_RS  OUT RESULTSET
                                 ) AS
    V_ID NUMBER;
    -------------------For Ordering the Charges---------------
    V_LANE_NO     NUMBER(3) := 1;
    V_COUNT       NUMBER(3);
    V_COUNT1      NUMBER(3);
    V_WEIGHTBREAK VARCHAR2(100) := '';
    V_SHMODE      VARCHAR2(100) := '';
    -----------------------------------------------------------
  BEGIN
    -- Freight Rates Begin
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TEMP_CHARGES');

    FOR LANE IN (SELECT ID
                   FROM QMS_QUOTE_MASTER
                  WHERE QUOTE_ID = P_QUOTEID
                    AND VERSION_NO =
                        (SELECT MAX(VERSION_NO)
                           FROM QMS_QUOTE_MASTER
                          WHERE QUOTE_ID = P_QUOTEID)) LOOP
      --Added by Mohan
      FOR I IN (SELECT DISTINCT QUOTE_ID,
                                SELL_BUY_FLAG,
                                BUYRATE_ID,
                                SELLRATE_ID,
                                RATE_LANE_NO,
                                VERSION_NO,
                                --@@Added for the WPBN issues-146448,146968 on 19/12/08
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
                                --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                                CARRIER,
                                FREQUENCY_CHECKED,
                                CARRIER_CHECKED,
                                -- SERVICE_CHECKED,-- kiran.v
                                TRANSIT_CHECKED,
                                VALIDITY_CHECKED,
                                FREQUENCY
                  FROM QMS_QUOTE_RATES
                 WHERE QUOTE_ID = LANE.ID) --Added by Mohan
       LOOP
        IF UPPER(I.SELL_BUY_FLAG) = 'BR' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             SELLCHARGEID,
             VERSION_NO,
             --@@Added for the WPBN issues-146448,146968 on 19/12/08
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             FREQUENCY,
             TRANSITTIME,
             NOTES,
             PRIMARY_BASIS,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             RATE_INDICATOR,
             --@@Added by Kameswari for the WPBN issue-146448
             CARRIER,
             FREQUENCY_CHECKED,
             CARRIER_CHECKED,
              --SERVICE_CHECKED,-- kiran.v
             TRANSITTIME_CHECKED,
             RATEVALIDITY_CHECKED,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             CONSOLE_TYPE,
             SELECTED_FLAG,
             CHECKED_FLAG)
            SELECT I.CHARGE_AT,
                   QBD.WEIGHT_BREAK_SLAB,
                   /*QBM.CURRENCY*/
                    QBM.CURRENCY,
                   QBD.CHARGERATE,
                   0,
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN),
                   'Per ' || QBM.UOM,
                   I.SELL_BUY_FLAG,
                   QBM.BUYRATEID,
                   QBM.BUYRATEID,
                   QBM.VERSION_NO,
                   --@@Added for the WPBN issues-146448,146968 on 19/12/08
                   QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                   (SELECT DISTINCT BD.VALID_UPTO
                      FROM QMS_BUYRATES_DTL BD
                     WHERE BD.BUYRATEID = I.BUYRATE_ID
                       AND BD.LANE_NO = I.RATE_LANE_NO
                       AND BD.VERSION_NO =
                           (SELECT MAX(BD1.VERSION_NO)
                              FROM QMS_BUYRATES_DTL BD1
                             WHERE BD1.BUYRATEID = BD.BUYRATEID
                               AND BD1.LANE_NO = BD.LANE_NO)),
                   QBM.TERMINALID,
                   QBM.WEIGHT_BREAK,
                   QBM.WEIGHT_CLASS,
                   QBM.RATE_TYPE,
                   INITCAP(QBD.FREQUENCY),
                   QBD.TRANSIT_TIME,
                   QBD.NOTES,
                   QBM.UOM,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   QBM.SHIPMENT_MODE,
                   QBD.SERVICE_LEVEL,
                   FRL.SERIAL_NO,
                   QBD.DENSITY_CODE,
                   QBD.LOWERBOUND,
                   QBD.UPPERBOUND,
                   QBD.CHARGERATE_INDICATOR,
                   --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                   (SELECT CARRIERNAME
                      FROM FS_FR_CAMASTER
                     WHERE CARRIERID = QBD.CARRIER_ID),
                   --@@Modified by kameswari on 18/02/09
                   I.FREQUENCY_CHECKED,
                   I.CARRIER_CHECKED,
                  --  I.SERVICE_CHECKED,-- kiran.v
                   I.TRANSIT_CHECKED,
                   I.VALIDITY_CHECKED,
                   I.MARGIN_DISCOUNT_FLAG,
                   QBD.LANE_NO,
                   QBD.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(QBD.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          QBD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                   QBM.CONSOLE_TYPE ,--@@Added by Kameswari for the WPBN issue-112348
                   'Y',
                   'Y'
              FROM QMS_BUYRATES_MASTER QBM,
                   QMS_BUYRATES_DTL    QBD,
                   FS_RT_LEG           FRL,
                   FS_RT_PLAN          FRP
             WHERE QBD.BUYRATEID = QBM.BUYRATEID
               AND QBD.VERSION_NO = QBM.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
               AND QBD.BUYRATEID = I.BUYRATE_ID
               AND QBD.LANE_NO = I.RATE_LANE_NO
               AND QBD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND QBD.WEIGHT_BREAK_SLAB = I.BREAK_POINT
               AND QBD.RATE_DESCRIPTION = I.CHARGE_DESCRIPTION
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND (QBD.CHARGERATE NOT IN ('0.0') OR
                   QBD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
               AND FRP.QUOTE_ID = P_QUOTEID;
        ELSIF (UPPER(I.SELL_BUY_FLAG) = 'RSR' OR
              UPPER(I.SELL_BUY_FLAG) = 'CSR') THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             VERSION_NO,
             --@@Added for the WPBN issues-146448,146968 on 19/12/08
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             FREQUENCY,
             TRANSITTIME,
             NOTES,
             PRIMARY_BASIS,
             SELLCHARGEID,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             RATE_INDICATOR,
             --@@Added by Kameswari for the WPBN issue-146448
             CARRIER,
             FREQUENCY_CHECKED,
             CARRIER_CHECKED,
              --SERVICE_CHECKED,-- kiran.v
             TRANSITTIME_CHECKED,
             RATEVALIDITY_CHECKED,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             CONSOLE_TYPE,
             SELECTED_FLAG,
             CHECKED_FLAG)
            SELECT I.CHARGE_AT,
                   RSD.WEIGHTBREAKSLAB,
                   /*RSM.CURRENCY,*/
                   QBM.CURRENCY,
                   RSD.BUY_RATE_AMT,
                   RSD.CHARGERATE,
                   DECODE(I.MARGIN_DISCOUNT_FLAG,
                          'M',
                          I.MARGIN_TYPE,
                          I.DISCOUNT_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                   'Per ' || QBM.UOM,
                   I.SELL_BUY_FLAG,
                   RSD.BUYRATEID,
                   RSD.VERSION_NO,
                   --@@Added for the WPBN issues-146448,146968 on 19/12/08
                   QBD.EFFECTIVE_DATE, /*QBD.VALID_UPTO*/
                   (SELECT DISTINCT BD.VALID_UPTO
                      FROM QMS_BUYRATES_DTL BD, QMS_REC_CON_SELLRATESDTL RSD
                     WHERE BD.BUYRATEID = RSD.BUYRATEID
                       AND BD.LANE_NO = RSD.LANE_NO
                       AND BD.BUYRATEID = I.BUYRATE_ID
                       AND BD.LANE_NO = I.RATE_LANE_NO
                       AND RSD.VERSION_NO =
                           (SELECT MAX(VERSION_NO)
                              FROM QMS_REC_CON_SELLRATESDTL
                             WHERE BUYRATEID = I.BUYRATE_ID
                               AND LANE_NO = I.RATE_LANE_NO)
                       AND BD.VERSION_NO = RSD.VERSION_NO),
                   RSM.TERMINALID,
                   RSM.WEIGHT_BREAK,
                   RSM.WEIGHT_CLASS,
                   RSM.RATE_TYPE,
                   INITCAP(RSD.FREQUENCY),
                   RSD.TRANSIT_TIME,
                   RSD.NOTES,
                   QBM.UOM,
                   RSD.REC_CON_ID,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   QBM.SHIPMENT_MODE,
                   QBD.SERVICE_LEVEL,
                   FRL.SERIAL_NO,
                   QBD.DENSITY_CODE,
                   RSD.LOWRER_BOUND,
                   RSD.UPPER_BOUND,
                   RSD.CHARGERATE_INDICATOR,
                   --@@Added by Kameswari for the WPBN issue-146448 on 04/12/08
                   (SELECT CARRIERNAME
                      FROM FS_FR_CAMASTER
                     WHERE CARRIERID = QBD.CARRIER_ID),
                   --@@Modified by kameswari on 18/02/09
                   I.FREQUENCY_CHECKED,
                   I.CARRIER_CHECKED,
                   -- I.SERVICE_CHECKED,-- kiran.v
                   I.TRANSIT_CHECKED,
                   I.VALIDITY_CHECKED,
                   I.MARGIN_DISCOUNT_FLAG,
                   RSD.LANE_NO,
                   RSD.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(RSD.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          RSD.RATE_DESCRIPTION) RATE_DESCRIPTION,
                   QBM.CONSOLE_TYPE, --@@Added by Kameswari for the WPBN issue-112348
                   'Y',
                   'Y'
              FROM QMS_REC_CON_SELLRATESMASTER RSM,
                   QMS_REC_CON_SELLRATESDTL    RSD,
                   QMS_BUYRATES_MASTER         QBM,
                   QMS_BUYRATES_DTL            QBD,
                   FS_RT_LEG                   FRL,
                   FS_RT_PLAN                  FRP
             WHERE RSD.REC_CON_ID = RSM.REC_CON_ID
               AND RSD.REC_CON_ID = I.SELLRATE_ID
               AND RSD.LANE_NO = I.RATE_LANE_NO
               AND RSD.BUYRATEID = I.BUYRATE_ID
               AND RSD.BUYRATEID = QBD.BUYRATEID
               AND RSD.LANE_NO = QBD.LANE_NO
               AND (QBD.LANE_NO = QBM.LANE_NO OR QBM.LANE_NO IS NULL)
               AND QBD.BUYRATEID = QBM.BUYRATEID
               AND RSD.VERSION_NO = QBD.VERSION_NO
               AND QBD.VERSION_NO = QBM.VERSION_NO
               AND RSD.VERSION_NO = I.VERSION_NO --@@Added for the WPBN issues-146448,146968 on 19/12/08
               AND RSD.WEIGHTBREAKSLAB = I.BREAK_POINT
               AND RSD.WEIGHTBREAKSLAB = QBD.WEIGHT_BREAK_SLAB --@@Added by Kameswari for Surcharge Enhancements
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND (RSD.CHARGERATE NOT IN ('0.0') OR
                   RSD.CHARGERATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
                  -- And Rsd.Ai_Flag='A'
               AND FRP.QUOTE_ID = P_QUOTEID;
        ELSIF UPPER(I.SELL_BUY_FLAG) = 'SBR' THEN
          INSERT INTO TEMP_CHARGES
            (COST_INCURREDAT,
             CHARGESLAB,
             CURRENCY,
             BUYRATE,
             SELLRATE,
             MARGIN_TYPE,
             MARGINVALUE,
             CHARGEBASIS,
             SEL_BUY_FLAG,
             BUY_CHARGE_ID,
             EFROM,
             VALIDUPTO,
             TERMINALID,
             WEIGHT_BREAK,
             WEIGHT_SCALE,
             RATE_TYPE,
             PRIMARY_BASIS,
             ORG,
             DEST,
             SHMODE,
             SRV_LEVEL,
             LEG_SL_NO,
             DENSITY_RATIO,
             LBOUND,
             UBOUND,
             MARGIN_DISCOUNT_FLAG,
             LANE_NO,
             LINE_NO,
             MARGIN_TEST_FLAG,
             RATE_DESCRIPTION,
             SELECTED_FLAG,
             CHECKED_FLAG,
             SELLCHARGEID,
             FREQUENCY_CHECKED,
             TRANSITTIME_CHECKED,
             CARRIER_CHECKED,
             -- SERVICE_CHECKED,-- kiran.v
             RATEVALIDITY_CHECKED,
             FREQUENCY,
             CONSOLE_TYPE)
            SELECT I.CHARGE_AT,
                   QSB.WEIGHT_BREAK_SLAB,
                   DECODE((SELECT DISTINCT RATE_DESCRIPTION FROM QMS_QUOTE_SPOTRATES QQSR WHERE
                    QUOTE_ID=I.QUOTE_ID AND QQSR.RATE_DESCRIPTION='A FREIGHT RATE'),
                    'A FREIGHT RATE',(SELECT DISTINCT QQSR.CURRENCYID FROM QMS_QUOTE_SPOTRATES QQSR WHERE
                    QUOTE_ID=I.QUOTE_ID AND QQSR.RATE_DESCRIPTION='A FREIGHT RATE')),
                   QSB.CHARGE_RATE,
                   0.0,
                   DECODE(I.MARGIN_DISCOUNT_FLAG,
                          'M',
                          I.MARGIN_TYPE,
                          I.DISCOUNT_TYPE),
                   DECODE(I.MARGIN_DISCOUNT_FLAG, 'M', I.MARGIN, I.DISCOUNT),
                   'Per ' || QSB.UOM,
                   I.SELL_BUY_FLAG,
                   LANE.ID,
                   QM.EFFECTIVE_DATE,
                   QM.VALID_TO,
                   QM.TERMINAL_ID,
                   QSB.WEIGHT_BREAK,
                   'G',
                   QSB.WEIGHT_BREAK,
                   QSB.UOM,
                   FRL.ORIG_LOC,
                   FRL.DEST_LOC,
                   FRL.SHPMNT_MODE,
                   QSB.SERVICELEVEL,
                   FRL.SERIAL_NO,
                   QSB.DENSITY_CODE,
                   QSB.LOWER_BOUND,
                   QSB.UPPER_BOUND,
                   I.MARGIN_DISCOUNT_FLAG,
                   QSB.LANE_NO,
                   QSB.LINE_NO,
                   I.MARGIN_TEST_FLAG,
                   DECODE(QSB.RATE_DESCRIPTION,
                          '',
                          'A FREIGHT RATE',
                          QSB.RATE_DESCRIPTION) RATE_DESCRIPTION,
                          'Y',
                          'Y',
                          LANE.ID,
                         -- 'on',
                         -- 'on',
                         -- 'on',
                         -- 'on',--kiran.v
                          I.FREQUENCY_CHECKED,
                          I.TRANSIT_CHECKED,
                          I.CARRIER_CHECKED,
                          I.VALIDITY_CHECKED,
                          I.FREQUENCY,
                          DECODE(UPPER(QM.MULTI_QUOTE_WEIGHT_BREAK),'LIST','FCL','')
              FROM QMS_QUOTE_MASTER    QM,
                   QMS_QUOTE_SPOTRATES QSB,
                   FS_RT_LEG           FRL,
                   FS_RT_PLAN          FRP
             WHERE QSB.QUOTE_ID = QM.ID
               AND QM.ID = I.QUOTE_ID
               AND QSB.WEIGHT_BREAK_SLAB = I.BREAK_POINT
               AND QSB.RATE_DESCRIPTION = I.CHARGE_DESCRIPTION
               AND FRL.SERIAL_NO = I.SERIAL_NO
               AND FRL.RT_PLAN_ID = FRP.RT_PLAN_ID
               AND FRP.QUOTE_ID = P_QUOTEID;
               --AND (QSB.CHARGE_RATE NOT IN ('0.0') OR
                  -- QSB.CHARGE_RATE NOT IN ('0')) --@@Added by Kameswari for Surcharge Enhancements
               --AND QSB.LANE_NO = FRL.SERIAL_NO - 1;


        END IF;
      END LOOP;--sell_buy_flag loop end loop end


     END LOOP;
   FOR BASIS IN (SELECT TC.SHMODE,TC.CONSOLE_TYPE,TC.CHARGESLAB  FROM TEMP_CHARGES TC) LOOP
    UPDATE TEMP_CHARGES TC SET TC.CHARGEBASIS = MULTI_QUOTE_BASIS(BASIS.SHMODE,BASIS.CONSOLE_TYPE,BASIS.CHARGESLAB) WHERE TC.CHARGESLAB =BASIS.CHARGESLAB AND TC.SHMODE = BASIS.SHMODE;

    END LOOP;
    COMMIT;



    OPEN P_RS FOR
      SELECT  DISTINCT tc.*
        FROM TEMP_CHARGES tc
       WHERE SEL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR')
       ORDER BY LEG_SL_NO, LINE_NO;
   END;

   -------------------------------------------------------------------------------------

   PROCEDURE MULTI_QUOTE_SPOT_RATE_PROC(
    P_ORG_LOC      VARCHAR2,
    P_DEST_LOC     VARCHAR2,
    P_TERMINAL     VARCHAR2,
    P_SRVLEVEL     VARCHAR2,
    P_SHMODE       VARCHAR2,
    P_PERMISSION   VARCHAR2 DEFAULT 'Y',
    P_OPERATION    VARCHAR2,
    P_QUOTE_ID     VARCHAR2,
    P_WEIGHT_BREAK VARCHAR2,
    P_LANENO       NUMBER,
    singleReturn   OUT RESULTSET,
    multipleReturn OUT RESULTSET) AS

  BEGIN
    OPEN singleReturn FOR
    SELECT DISTINCT SERVICELEVEL,
                    UOM,
                    DENSITY_CODE,
                    QR.FREQUENCY,
                    QR.TRANSIT_TIME,
                    QR.CARRIER,
                    QS.CURRENCYID,
                    QS.WEIGHT_BREAK
      FROM QMS_QUOTE_SPOTRATES QS, QMS_QUOTE_MASTER QMS, QMS_QUOTE_RATES QR
     WHERE QS.RATE_DESCRIPTION = 'A FREIGHT RATE'
       AND QMS.QUOTE_ID = P_QUOTE_ID
       AND QS.QUOTE_ID = QMS.ID
       AND QR.QUOTE_ID = QS.QUOTE_ID
       AND QMS.Origin_Port = P_ORG_LOC
       AND QMS.Destionation_Port = P_DEST_LOC
       AND QMS.SPOT_RATES_FLAG = 'Y'
       AND QMS.ACTIVE_FLAG = 'A' AND QS.LANE_NO=P_LANENO
       AND QR.SELL_BUY_FLAG IN ('BR','RSR','CSR','SBR');

    OPEN multipleReturn FOR
    SELECT DECODE((SELECT QSM.SURCHARGE_ID
                    FROM QMS_SURCHARGE_MASTER QSM
                   WHERE QSM.SURCHARGE_ID = QQS.SURCHARGE_ID
                     AND QSM.SHIPMENT_MODE = QQS.SHIPMENT_MODE),
                  QQS.SURCHARGE_ID,
                  (SELECT QSMS.WEIGHT_BREAKS
                     FROM QMS_SURCHARGE_MASTER QSMS
                    WHERE QSMS.SURCHARGE_ID = QQS.SURCHARGE_ID
                      AND QSMS.SHIPMENT_MODE = QQS.SHIPMENT_MODE),
                  '') WEIGHT_BREAKS,
           QQS.UPPER_BOUND,
           QQS.LOWER_BOUND,
           QQS.CHARGE_RATE,
           QQS.WEIGHT_BREAK_SLAB,
           QQS.CURRENCYID,
           QQS.RATE_DESCRIPTION,
           QQS.SURCHARGE_ID,
           QQS.CHARGERATE_INDICATOR,
           DECODE((SELECT BREAK_POINT
                    FROM QMS_QUOTE_RATES QQR
                   WHERE QQR.QUOTE_ID = QMS.ID
                     AND QQS.WEIGHT_BREAK_SLAB = QQR.BREAK_POINT
                     AND (DECODE(QQR.Charge_Description,'-',QQS.RATE_DESCRIPTION,QQR.Charge_Description) = QQS.RATE_DESCRIPTION )
                     AND QQS.QUOTE_ID = QQR.QUOTE_ID),
                  QQS.WEIGHT_BREAK_SLAB,
                  'Y',
                  'N') CHECKEDFLAG,
           DECODE((SELECT MARGIN_TYPE
                    FROM QMS_QUOTE_RATES QQR2
                   WHERE QUOTE_ID = QQS.QUOTE_ID
                     AND QQR2.BREAK_POINT = QQS.WEIGHT_BREAK_SLAB
                     AND QQR2.Charge_Description = QQS.RATE_DESCRIPTION),
                  '',
                  'A',
                  (SELECT MARGIN_TYPE
                     FROM QMS_QUOTE_RATES QQR2
                    WHERE QUOTE_ID = QQS.QUOTE_ID
                      AND QQR2.BREAK_POINT = QQS.WEIGHT_BREAK_SLAB
                      AND QQR2.Charge_Description = QQS.RATE_DESCRIPTION)) MARGIN_TYPE,
           DECODE((SELECT MARGIN
                    FROM QMS_QUOTE_RATES QQR2
                   WHERE QUOTE_ID = QQS.QUOTE_ID
                     AND QQR2.BREAK_POINT = QQS.WEIGHT_BREAK_SLAB
                     AND QQR2.Charge_Description = QQS.RATE_DESCRIPTION),
                  '',
                  0,
                  (SELECT MARGIN
                     FROM QMS_QUOTE_RATES QQR2
                    WHERE QUOTE_ID = QQS.QUOTE_ID
                      AND QQR2.BREAK_POINT = QQS.WEIGHT_BREAK_SLAB
                      AND QQR2.Charge_Description = QQS.RATE_DESCRIPTION)) MARGIN
      FROM QMS_QUOTE_SPOTRATES QQS, QMS_QUOTE_MASTER QMS
     WHERE QMS.QUOTE_ID = P_QUOTE_ID
       AND QQS.QUOTE_ID = QMS.ID
       AND QMS.Origin_Port = P_ORG_LOC
       AND QMS.Destionation_Port = P_DEST_LOC
       AND QMS.SPOT_RATES_FLAG = 'Y' AND QQS.LANE_NO=P_LANENO
       AND QMS.ACTIVE_FLAG = 'A';

  END;



  -----------------------------------------------------------------------------------
  -- ADDED PROCEDURE TO GET BASIS AS PER THE NEW CHANGES IN NEW SURCHARGES
  -----------------------------------------------------------------------------------
FUNCTION MULTI_QUOTE_BASIS(   P_SHIPMENT_MODE  VARCHAR2,
                               P_CONSOLE_TYPE  VARCHAR2,
                               P_WEIGHTBREAK  VARCHAR2
                               )
                              RETURN VARCHAR2  IS
    P_BASIS                    VARCHAR2(50);
    V_BREAK                    VARCHAR2(50);
    V_BREAKTYPE                VARCHAR2(2);
  BEGIN

       IF P_WEIGHTBREAK IS NOT  NULL AND P_SHIPMENT_MODE = '1' THEN
          IF P_WEIGHTBREAK IS NOT NULL AND LENGTH(P_WEIGHTBREAK) >= 6 THEN
              IF UPPER(P_WEIGHTBREAK) = 'PERCENT' THEN
                P_BASIS := 'Percent of Freight Rate';
                END IF;
              IF UPPER(P_WEIGHTBREAK) = 'ABSOLUTE' THEN
                   P_BASIS := 'Per Shipment';
                ELSE IF UPPER(IS_NUMBER(P_WEIGHTBREAK))= 'TRUE'  THEN
                     IF  P_SHIPMENT_MODE <> '1' THEN
                     P_BASIS := 'Per Shipment';
                     ELSE
                     P_BASIS := 'Per Kilogram';
                     END IF;

                ELSE
                  V_BREAK := UPPER(SUBSTR(P_WEIGHTBREAK,6));
                  V_BREAKTYPE := UPPER(SUBSTR(P_WEIGHTBREAK,4,2));
                IF UPPER(V_BREAK) =  'MIN' OR UPPER(V_BREAK) ='BASIC' THEN
                  P_BASIS := 'Per Shipment';
                ELSE
                  IF UPPER(V_BREAK) = 'FLAT' AND  V_BREAKTYPE = 'SF' THEN
                         P_BASIS := 'Per Shipment';
                ELSE
                     P_BASIS := 'Per Kilogram';
                        END IF;
              END IF;

          END IF;
       END IF;
       ELSE
       IF (UPPER(P_WEIGHTBREAK)='MIN' OR UPPER(P_WEIGHTBREAK)='BASIC') AND UPPER(IS_NUMBER(P_WEIGHTBREAK)) <> 'TRUE'  THEN
                     P_BASIS := 'Per Shipment';
        ELSE
         IF UPPER(P_WEIGHTBREAK)='FLAT' AND UPPER(IS_NUMBER(P_WEIGHTBREAK)) <> 'TRUE' THEN
                    P_BASIS := 'Per Kilogram';

          ---END IF;
       ELSE
              IF UPPER(IS_NUMBER(P_WEIGHTBREAK)) = 'TRUE'  AND  P_SHIPMENT_MODE = '1' THEN
              P_BASIS := 'Per Kilogram';
              ELSE
              P_BASIS := 'Per Shipment';
                     END IF;
              END IF;
              END IF;
     END IF;

     ELSE  IF P_SHIPMENT_MODE = '2' AND UPPER(P_CONSOLE_TYPE) = 'FCL'THEN
            V_BREAKTYPE := UPPER(SUBSTR(P_WEIGHTBREAK,(LENGTH(P_WEIGHTBREAK)-1)));
          IF V_BREAKTYPE = 'LP' THEN
               P_BASIS := 'Percent of Freight Rate ';
         ELSE
               P_BASIS := 'Per Container';
         END IF;
         ELSIF P_SHIPMENT_MODE =4 AND UPPER(P_WEIGHTBREAK)='FLAT' THEN
         P_BASIS := 'Per Weight Measurement'; --@@Added by kiran.v
          ELSE
          IF P_WEIGHTBREAK IS NOT  NULL AND LENGTH(P_WEIGHTBREAK) >= 6 THEN
                  V_BREAK := UPPER(SUBSTR(P_WEIGHTBREAK,6));
                  V_BREAKTYPE := UPPER(SUBSTR(P_WEIGHTBREAK,4,2));
                  IF UPPER(V_BREAK) =  'MIN' OR UPPER(V_BREAK) ='BASIC' OR UPPER(V_BREAK) ='ABSOLUTE'  THEN
                        P_BASIS := 'Per Shipment';
                 ELSE
                 IF UPPER(V_BREAK) ='PERCENT' THEN
                 P_BASIS := 'Percent of Freight Rate';
                 ELSE
                  IF V_BREAKTYPE = 'FF' OR V_BREAKTYPE= 'SB' OR V_BREAKTYPE='SS'  THEN
                        P_BASIS := 'Per Weight Measurement';
                        ELSE
                   IF V_BREAKTYPE ='SF' OR V_BREAKTYPE='AF' THEN
                       P_BASIS := 'Per Shipment';
                  ELSE
                  IF V_BREAKTYPE='FP' OR V_BREAKTYPE='SP' THEN
                         P_BASIS := 'Percent Of Freight Rates';
              ELSE
                 P_BASIS := 'Per Weight Measurement';
                 END IF;
                 END IF;
           END IF;
           END IF;
        END IF;
         ELSE IF P_SHIPMENT_MODE = '2' AND (UPPER(P_WEIGHTBREAK) = 'MIN' OR UPPER(P_WEIGHTBREAK) ='BASIC' OR UPPER(P_WEIGHTBREAK)='ABSOLUTE' ) THEN
                P_BASIS := 'Per Shipment';
                 ELSE
                 IF  UPPER(P_WEIGHTBREAK) = 'PERCENT' THEN
                  P_BASIS := 'Percent of Freight Rate';
                  ELSE
                  IF P_SHIPMENT_MODE = '2' AND (UPPER(P_WEIGHTBREAK) = 'FLAT' OR  UPPER(IS_NUMBER(P_WEIGHTBREAK)) = 'TRUE' )THEN
                  P_BASIS := 'Per Weight Measurement';
                  ELSE
                  P_BASIS := 'Per Shipment';
                  END IF;
                  END IF;
            END IF;
      END IF;

       END IF;
   END IF;
   RETURN P_BASIS;
  END;

  -------------------------------------------------------------------------------------------
  ---PROCEDURE MULTI_QUOTE_BASIS   END
  -------------------------------------------------------------------------------------------

END;

/

/

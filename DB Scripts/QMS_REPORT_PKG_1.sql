--------------------------------------------------------
--  DDL for Package Body QMS_REPORT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_REPORT_PKG" 
AS
 PROCEDURE qms_byrate_expiry_report (
      ship_mode             VARCHAR2,
      carrier_id            VARCHAR2,
      service_level         VARCHAR2,
      basis                 VARCHAR2,
      flage                 VARCHAR2,
      from_date             DATE,
      TO_DATE               DATE,
      p_page_no             NUMBER DEFAULT 1,
      p_page_rows           NUMBER DEFAULT 50,
      sortBy                VARCHAR2,
      sortOrder             VARCHAR2,
      p_tot_rec       OUT   NUMBER,
      p_tot_pages     OUT   NUMBER,
      dhl_ref         OUT   sys_refcursor
   )
   IS
      v_sql1         VARCHAR2 (4000);
      v_shipmode     VARCHAR2 (300);
      v_strqry       VARCHAR2 (32767);
      v_strcount     VARCHAR2 (32767);
      v_strsearch    VARCHAR2 (200);
      v_strcountry   VARCHAR2 (200);
      v_orderby      VARCHAR2 (50);
      v_carrierid    VARCHAR2 (4000);
      v_servicelevel  VARCHAR2 (4000);
   BEGIN

      IF (sortBy='FromCountry') THEN
        v_orderby := ' COUNTRYID ';
      ELSIF (sortBy='FromLocation') THEN
        v_orderby := ' ORIGIN ';
      ELSIF (sortBy='ToCountry') THEN
        v_orderby := ' DESTCOUNTRYID ';
      ELSIF (sortBy='ToLocation') THEN
        v_orderby := ' DESTINATION ';
      ELSIF (sortBy='CarrierId') THEN
        v_orderby := ' CARRIER_ID ';
      ELSIF (sortBy='ServiceLevel') THEN
        v_orderby := ' SERVICE_LEVEL ';
      ELSIF (sortBy='CreatedDate') THEN
        v_orderby := ' CREATEDTIME ';
      ELSIF (sortBy='EffectiveFrom') THEN
        v_orderby := ' EFFECTIVE_FROM ';
      ELSIF (sortBy='ValidUpto') THEN
        v_orderby := ' VALID_UPTO ';
      ELSE
        v_orderby := ' COUNTRYID ';
      END IF;
      v_orderby := ' ORDER BY ' || v_orderby || sortOrder ;

       --@@Added by Kameswari for the WPBN issue-146451 on 21/02/09
      IF carrier_id IS NOT NULL
      THEN
      --v_carrierid :=' AND CARRIER_ID IN(qms_rsr_rates_pkg.seperator (carrier_id)) ';--COMMENTED BY SUBRAHMANYAM FOR 146451
       v_carrierid :=' AND CARRIER_ID IN ('|| qms_rsr_rates_pkg.seperator (carrier_id)||')' ;  --ADDED BY SUBRAHMANYAM FOR 146451
      END IF;

      IF service_level IS NOT NULL
      THEN
       --v_servicelevel :=' AND SERVICE_LEVEL IN(qms_rsr_rates_pkg.seperator (service_level)) ';--COMMENTED BY SUBRAHMANYAM FOR 146451
          v_servicelevel :=' AND SERVICE_LEVEL IN ('|| qms_rsr_rates_pkg.seperator (service_level)||')' ;  --ADDED BY SUBRAHMANYAM FOR 146451
       END IF;
        --@@ WPBN issue-146451 on 21/02/09
      IF ship_mode = '1' OR ship_mode = '4'
      THEN
         v_strcountry :=
            ' ( SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ';
      ELSE
         v_strcountry :=
                ' ( SELECT COUNTRYID FROM FS_FRS_PORTMASTER WHERE PORTID  = ';
      END IF;

      v_strqry :=
            ' select * from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || v_carrierid
        /* || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)*/           --@@Commented and Modified by Kameswari for the WPBN issue-146451 on 21/02/09
         || ''
         || ' AND ';


        /* if carrier_id is not null
         then
         v_strqry :=
            ' select * from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)
         || ''
         || ') AND ';
         dbms_output.put_line('v_strqry..'||v_strqry);
         else
         v_strqry :=
            ' select * from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID like ( '
         || ''''
         || '%'

         || ''''
         || ' ) AND ';

         end if;*/

      v_strcount :=
      ' select count(*) from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         ||v_carrierid              --@@Commented and Modified by Kameswari for the WPBN issue-146451 on 21/02/09
         /*|| '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)*/
         || ''
         || ' AND ';

         /*if carrier_id is not null
         then
         v_strcount :=
      ' select count(*) from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)
         || ''
         || ') AND ';

         else
         v_strcount :=
      ' select count(*) from   (select t1.*, rownum rn from   (SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID like ( '
         || ''''
         || '%'
         || ''''
         || ' ) AND ';
           end if;*/


      IF UPPER (flage) = 'Y'
      THEN
         IF basis = '>='
         THEN
            v_strsearch :=
                  ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') >= TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                  ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') BETWEEN TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         END IF;
      ELSE
         IF basis = '>='
         THEN
            v_strsearch :=
                  ' TO_DATE(EFFECTIVE_FROM,''DD-MM-YYYY HH24:MI:SS'') <= TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                  ' TO_DATE(EFFECTIVE_FROM,''DD-MM-YYYY HH24:MI:SS'') BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' ) ';
         END IF;
      END IF;
-- Commented by subrahmanyam for 146451
      v_strqry :=
            v_strqry
         || v_strsearch
         /*|| '   AND SERVICE_LEVEL IN( '
         || ''
         || qms_rsr_rates_pkg.seperator (service_level)*/
         ||v_servicelevel              --@@Commented and Modified by Kameswari for the WPBN issue-146451 on 21/02/09
         || ''
         || v_orderby || ')t1  where   rownum <= (('
         /*|| ')' || v_orderby || ')t1  where   rownum <= (('*/
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ') + '
         || p_page_rows
         || ') '
         || '  where   rn > (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ')';

         /*  if service_level is not null
           then
           v_strqry :=
            v_strqry
         || v_strsearch
         || '   AND SERVICE_LEVEL IN( '
         || ''
         || qms_rsr_rates_pkg.seperator (service_level)
         || ''
         || ')' || v_orderby || ')t1  where   rownum <= (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ') + '
         || p_page_rows
         || ') '
         || '  where   rn > (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ')';
         else
         v_strqry :=
            v_strqry
         || v_strsearch
         || '   AND SERVICE_LEVEL like( '
         || ''''
         || '%'
         || ''''
         || ' )' || v_orderby || ')t1  where   rownum <= (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ') + '
         || p_page_rows
         || ') '
         || '  where   rn > (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ')';
         end if;*/

      v_strcount :=
            v_strcount
         || v_strsearch
         ||v_servicelevel  --@@Commented and Modified by Kameswari for the WPBN issue-146451 on 21/02/09
         /*|| '   AND SERVICE_LEVEL IN( '
         || qms_rsr_rates_pkg.seperator (service_level)*/
         || ')t1 )';

       /*   if service_level is not null
          then
              v_strcount :=
            v_strcount
         || v_strsearch
         || '   AND SERVICE_LEVEL IN( '
         || qms_rsr_rates_pkg.seperator (service_level)
         || '))t1 )';
         else
         v_strcount :=
            v_strcount
         || v_strsearch
         || '   AND SERVICE_LEVEL like( '
         ||''''
         || '%'
         ||''''
         || ' ))t1 )';
          end if;*/
  DBMS_OUTPUT.put_line('v_strqry...'||v_strqry);
   DBMS_OUTPUT.put_line('v_strcount...'||v_strcount);
      OPEN dhl_ref
       FOR v_strqry;
           DBMS_OUTPUT.put_line('v_strqry...'||v_strqry);
      EXECUTE IMMEDIATE (v_strcount)
                   INTO p_tot_rec;

      /*SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;*/
      p_tot_pages := CEIL(p_tot_rec/p_page_rows);
   EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.put_line (SQLERRM);
   END qms_byrate_expiry_report;

   PROCEDURE qms_byrate_expiry_report_excel (
      ship_mode             VARCHAR2,
      carrier_id            VARCHAR2,
      service_level         VARCHAR2,
      basis                 VARCHAR2,
      flage                 VARCHAR2,
      from_date             DATE,
      TO_DATE               DATE,
      weight_break           VARCHAR2,
      rate_type              VARCHAR2,
      dhl_ref         OUT   Resultset
   )
   IS
      v_sql1         VARCHAR2 (4000);
      v_shipmode     VARCHAR2 (300);
      v_strqry       VARCHAR2 (32767);
      v_strcount     VARCHAR2 (32767);
      v_strsearch    VARCHAR2 (200);
      v_strcountry   VARCHAR2 (200);
      v_orderby      VARCHAR2 (50);
      v_carrierid    VARCHAR2 (4000);
      v_servicelevel VARCHAR2 (4000);
   BEGIN

        --@@Added by Kameswari for the WPBN issue-146451 on 21/02/09
      IF carrier_id IS NOT NULL
      THEN
         -- v_carrierid :=' AND CARRIER_ID IN ( qms_rsr_rates_pkg.seperator (carrier_id)) ';  --COMMENTED BY SUBRAHMANYAM FOR 146451
         v_carrierid :=' AND CARRIER_ID IN ('|| qms_rsr_rates_pkg.seperator (carrier_id)||')' ;  --ADDED BY SUBRAHMANYAM FOR 146451
      END IF;

      IF service_level IS NOT NULL
      THEN
          --v_servicelevel :=' AND SERVICE_LEVEL IN(qms_rsr_rates_pkg.seperator (service_level)) ';--COMMENTED BY SUBRAHMANYAM FOR 146451
           v_servicelevel :=' AND SERVICE_LEVEL IN ('|| qms_rsr_rates_pkg.seperator (service_level)||')' ;  --ADDED BY SUBRAHMANYAM FOR 146451
       END IF;
        --@@ WPBN issue-146451 on 21/02/09
      IF ship_mode = '1' OR ship_mode = '4'
      THEN
         v_strcountry :=
            ' ( SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ';
      ELSE
         v_strcountry :=
                ' ( SELECT COUNTRYID FROM FS_FRS_PORTMASTER WHERE PORTID  = ';
      END IF;

      v_strqry :=
          ' SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO,B.CURRENCY,A.FREQUENCY,B.TERMINALID,A.NOTES FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'--Modified by Rakesh
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || ' AND UPPER(B.WEIGHT_BREAK) = '
         ||''''
         || UPPER(weight_break)
         ||''''
         ||' AND UPPER(B.RATE_TYPE) = '
         ||''''
         || UPPER(rate_type)
         ||''''
         || v_carrierid   --@@Commented and Modified by Kameswari for  the WPBN issue-146451 on 21/02/09
        /* || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)*/
         || ''
         || ' AND ';

       /*  if carrier_id is not null
         then
             v_strqry :=
          ' SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)
         || ''
         || ') AND ';
         else
             v_strqry :=
          ' SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND B.SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID like ( '
         || ''''
         || '%'
         || ''''
         || ' ) AND ';
         end if;*/

      v_strcount :=
         'SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || ' AND UPPER(B.WEIGHT_BREAK) = '
         ||''''
         ||UPPER(weight_break)
         ||''''
         || ' AND UPPER(B.RATE_TYPE) = '
         ||''''
         ||UPPER(rate_type)
         ||''''
         || v_carrierid   --@@Commented and Modified by Kameswari for  the WPBN issue-146451 on 21/02/09
         /*|| '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)*/
         || ''
         || ' AND ';

       /*if carrier_id is not null
          then
              v_strcount :=
         'SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID IN ( '
         || ''
         || qms_rsr_rates_pkg.seperator (carrier_id)
         || ''
         || ') AND ';
         else
         v_strcount :=
         'SELECT DISTINCT A.ORIGIN,'
         || v_strcountry
         || ' A.ORIGIN) COUNTRYID,A.CARRIER_ID,B.SHIPMENT_MODE,DESTINATION,'
         || v_strcountry
         || ' A.DESTINATION) DESTCOUNTRYID,SERVICE_LEVEL,EFFECTIVE_DATE,'
         || ' CREATEDTIME,EFFECTIVE_FROM,VALID_UPTO FROM QMS_BUYRATES_DTL A, QMS_BUYRATES_MASTER B'
         || ' WHERE (a.activeinactive is null or a.activeinactive=''A'') and A.BUYRATEID = B.BUYRATEID AND SHIPMENT_MODE ='
         || ''''
         || ship_mode
         || ''''
         || '   AND CARRIER_ID like ( '
         || ''''
         || '%'
         || ''''
         || ' ) AND ';
         end if;*/
      IF UPPER (flage) = 'Y'
      THEN
      --ended by subrahmanyam for 146451
      -- Commented by subrahmanyam for the 146451
         /*IF basis = '>='
         THEN
            v_strsearch :=
                  ' VALID_UPTO >= TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                  ' VALID_UPTO BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         END IF;*/
         -- Added by SUBRAHMANYAM for 146451
         IF basis = '>='
         THEN
            v_strsearch :=
                  ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') >= TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                  ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') BETWEEN TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         END IF;
--Ended by subrahmanyam for the enhancement 146451
      ELSE
      -- Commented by subrahmanyam for 146451
         /*IF basis = '>='
         THEN
            v_strsearch :=
                  ' EFFECTIVE_FROM <= TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                  ' EFFECTIVE_FROM BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YY HH24:MI:SS'' ) ';
         END IF;*/
         --Added by subrahmanyam for 146451
         IF basis = '>='
         THEN
            v_strsearch :=
                  -- Added By Silpa
                  --' TO_DATE(EFFECTIVE_FROM,''DD-MM-YYYY HH24:MI:SS'') <= TO_DATE('
                  ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') <= TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )';
         ELSE
            v_strsearch :=
                 -- Added By Silpa
                 -- ' TO_DATE(EFFECTIVE_FROM,''DD-MM-YYYY HH24:MI:SS'') BETWEEN   TO_DATE('
                 ' TO_DATE(VALID_UPTO,''DD-MM-YYYY HH24:MI:SS'') BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' )  AND   TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ',''DD-MM-YYYY HH24:MI:SS'' ) ';
         END IF;
         --ended by subrahmanyam for 146451
      END IF;

      v_strqry :=
            v_strqry
         || v_strsearch
         || v_servicelevel   --@@Commented and Modified by Kameswari for  the WPBN issue-146451 on 21/02/09
         /*|| '   AND SERVICE_LEVEL IN( '
         || ''
         || qms_rsr_rates_pkg.seperator (service_level)*/
        /* || ')'*/;
        /*if service_level is not null
        then
        v_strqry :=
            v_strqry
         || v_strsearch
         || '   AND SERVICE_LEVEL IN( '
         || ''
         || qms_rsr_rates_pkg.seperator (service_level)
         || ')';
         else
         v_strqry :=
            v_strqry
         || v_strsearch
         || '   AND SERVICE_LEVEL like( '
         || ''''
         || '%'
         || ''''
         || ' )';
         end if;*/

        print_out(v_strqry);
      OPEN dhl_ref
       FOR v_strqry;

   END qms_byrate_expiry_report_EXCEL;

--salesPersionId,user_Id Included by Shyam for DHL
   PROCEDURE qms_quote_expiry_report (
      ship_mode             VARCHAR2,
      customer_id           VARCHAR2,
      service_level         VARCHAR2,
      terminal_id           VARCHAR2,
      basis                 VARCHAR2,
      flage                 VARCHAR2,
      from_date             VARCHAR2,
      TO_DATE               VARCHAR2,
      p_userdateformat      VARCHAR2,
      p_page_no             NUMBER DEFAULT 1,
      p_page_rows           NUMBER DEFAULT 20,
      sortBy                VARCHAR2,
      sortOrder             VARCHAR2,
      salesPersonId        VARCHAR2,
      user_Id                VARCHAR2,
      p_tot_rec       OUT   NUMBER,
      p_tot_pages     OUT   NUMBER,
      dhl_ref1        OUT   sys_refcursor
   )
   IS

   /* CURSOR c1
      IS
         (SELECT terminal_id term_id
            FROM DUAL
          UNION
          SELECT     child_terminal_id term_id
                FROM fs_fr_terminal_regn
          CONNECT BY PRIOR child_terminal_id = parent_terminal_id
          START WITH parent_terminal_id = terminal_id);*/

      /*CURSOR c2
      IS
         (SELECT DISTINCT terminalid term_id
                     FROM fs_fr_terminalmaster);*/

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql2           VARCHAR2 (1000);
      v_sql1        VARCHAR2 (4000);
      v_shipmode    VARCHAR2 (300);
      v_terminal    VARCHAR2 (300);
      v_strqry      VARCHAR2 (32767);
      v_strcount    VARCHAR2 (32767);
      v_strsearch   VARCHAR2 (32767);

      v_orderby     VARCHAR2(50);


   BEGIN

      IF (sortBy='Important') THEN
        v_orderby := ' IU_FLAG ';
      ELSIF (sortBy='CustomerId') THEN
        v_orderby := ' CUSTOMER_ID ';
      ELSIF (sortBy='CustomerName') THEN --@@added by kameswari for the WPBN issue-30313
       v_orderby := ' COMPANYNAME ';
      ELSIF (sortBy='QuoteId') THEN
        v_orderby := ' QUOTE_ID ';
      ELSIF (sortBy='ServiceLevel') THEN
        v_orderby := ' SERVICE_LEVEL_ID ';
      ELSIF (sortBy='FromCountry') THEN
        v_orderby := ' ORIGINCOUNTRYID ';
      ELSIF (sortBy='FromLocation') THEN
        v_orderby := ' ORIGIN_LOCATION ';
      ELSIF (sortBy='ToCountry') THEN
        v_orderby := ' DESTCOUNTRYID ';
      ELSIF (sortBy='ToLocation') THEN
        v_orderby := ' DEST_LOCATION ';
      ELSE
        v_orderby := ' QUOTE_ID ';
      END IF;
      v_orderby := ' ORDER BY ' || v_orderby || sortOrder ;

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE terminal_id;

      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals := 'Select Terminalid From Fs_Fr_Terminalmaster';
         /*FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',terminal_id);
         v_terminals := 'Select Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')
                           FROM Dual
                         UNION
                         SELECT Child_Terminal_Id Term_Id
                           FROM FS_FR_TERMINAL_REGN
                         CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                          START WITH Parent_Terminal_Id =
                                     Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')';
         /*FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      END IF;

      --v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      IF (ship_mode='1')
      THEN
        IF service_level IS NULL
        THEN

        --  v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'')) ';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID = QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BM.BUYRATEID=QR.BUYRATE_ID AND   BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''1'')UNION select qr1.quote_id from
                               QMS_QUOTE_RATES qr1
                                WHERE qr1.QUOTE_ID =  QM.ID
                                AND qr1.sell_buy_flag IN (''BC'',''SC'',''B'',''S'')) ';
        ELSE
        --  v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID = QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BM.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSIF (ship_mode='2')
      THEN
        IF service_level IS NULL
        THEN
        --  v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''))';
            v_sql2:=' and EXISTS   ((select qr.quote_id from qms_quote_rates qr where  QR.QUOTE_ID = QM.ID AND EXISTS  (select 1  from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2''))';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS   ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID = QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSIF (ship_mode='4')
      THEN
        IF service_level IS NULL
        THEN
       --   v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4''))';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
             v_sql2:='and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSE
        IF service_level IS NULL
        THEN
             --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 )) union (select qs.quote_id from qms_quote_spotrates qs ))';
               v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND (QR.LINE_NO=0 OR QR.LINE_NO=1))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id) UNION select qr1.quote_id from
                               QMS_QUOTE_RATES qr1
                                WHERE qr1.QUOTE_ID =  QM.ID
                                AND qr1.sell_buy_flag IN (''BC'',''SC'',''B'',''S'')) ';
         ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO= QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')) UNION select qr1.quote_id from
                               QMS_QUOTE_RATES qr1
                                WHERE qr1.QUOTE_ID =  QM.ID
                                AND qr1.sell_buy_flag IN (''BC'',''SC'',''B'',''S'')) ';
        END IF;
      END IF;

      v_shipmode := '''' || ship_mode || '''';
      v_terminal := '''' || terminal_id || '''';

        --  v_sql2:='';
    IF customer_id IS NULL OR LENGTH (trim(customer_id)) = 0 -- Included by Shyam for DHL
      THEN
      --@@Modified by Kameswari for the WPBN issue-30313
      --@@Modified by Anil.k on 18-Jan-2011
        v_strqry :=
              ' select * from   (select t1.*, rownum rn from  '
           || ' (SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID AND QMSMQ.ACTIVE_FLAG=''A''),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
                                   DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE_LEVEL_ID,ORIGIN_LOCATION,VALID_TO,SHIPMENT_MODE,'
           || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
           || ' ORIGINCOUNTRYID,DEST_LOCATION,'
           || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
           || ' ) DESTCOUNTRYID ,COMPANYNAME,CREATED_DATE,IS_MULTI_QUOTE,'
           || ' (SELECT COUNT(1) FROM QMS_QUOTES_UPDATED UP WHERE UP.QUOTE_ID IN (SELECT ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = QM.QUOTE_ID) AND UP.CONFIRM_FLAG IS NULL)UPDATE_FLAG FROM QMS_QUOTE_MASTER qm, FS_FR_CUSTOMERMASTER C WHERE C.CUSTOMERID=qm.CUSTOMER_ID AND '
           || ' qm.active_flag=''A''  and qm.complete_flag=''C'' and ';/*TERMINAL_ID IN('
           || v_terminals
           || ' ) AND ';*/
      ELSE --Included by Shyam for DHL
      --@@Modified by Kameswari for the WPBN issue-30313
      --@@Modified by Anil.k on 18-Jan-2011
       v_strqry :=
            ' select * from   (select t1.*, rownum rn from  '
         || ' (SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID AND AND QMSMQ.ACTIVE_FLAG= ''A''),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
                                    DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE_LEVEL_ID,ORIGIN_LOCATION,VALID_TO,SHIPMENT_MODE,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
         || ' ORIGINCOUNTRYID,DEST_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
         || ' ) DESTCOUNTRYID, COMPANYNAME,CREATED_DATE,IS_MULTI_QUOTE,'
         || ' (SELECT COUNT(1) FROM QMS_QUOTES_UPDATED UP WHERE UP.QUOTE_ID IN (SELECT ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = QM.QUOTE_ID) AND UP.CONFIRM_FLAG IS NULL)UPDATE_FLAG FROM QMS_QUOTE_MASTER qm, FS_FR_CUSTOMERMASTER C WHERE C.CUSTOMERID=qm.CUSTOMER_ID AND SHIPMENT_MODE ='
         || v_shipmode
         || ' AND qm.active_flag=''A'' and qm.complete_flag=''C''  AND CUSTOMER_ID IN ('
         || qms_rsr_rates_pkg.seperator (customer_id)
         || ') AND ';
      END IF; -- Included by Shyam for DHL

     IF customer_id IS NULL -- Included by Shyam for DHL
      THEN

        v_strcount :=
              ' select count(*) from   (select t1.*, rownum rn from  '
           || ' (SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,SERVICE_LEVEL_ID,ORIGIN_LOCATION,'
           || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
           || ' ORIGINCOUNTRYID,DEST_LOCATION,'
           || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
           || ' ) DESTCOUNTRYID FROM QMS_QUOTE_MASTER qm WHERE '
           || ' qm.active_flag=''A'' and qm.complete_flag=''C'' and ';/*TERMINAL_ID IN ('
           || v_terminals
           || ') AND ';*/
      ELSE
        v_strcount :=
            ' select count(*) from   (select t1.*, rownum rn from  '
         || ' (SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,SERVICE_LEVEL_ID,ORIGIN_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
         || ' ORIGINCOUNTRYID,DEST_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
         || ' ) DESTCOUNTRYID FROM QMS_QUOTE_MASTER qm WHERE  SHIPMENT_MODE ='
         || v_shipmode
         || '  AND qm.active_flag=''A'' and qm.complete_flag=''C'' and CUSTOMER_ID IN ('
         || qms_rsr_rates_pkg.seperator (customer_id)
         || ') AND ';
         /*'TERMINAL_ID IN ('
         || v_terminals      --@@Commented by Kameswari for the WPBN issue-140194
         || ')  AND */
      END IF;


      IF UPPER (flage) = 'Y'
      THEN
         IF basis = '>='
         THEN
            v_strsearch :=
                  '( VALID_TO >= TO_DATE ('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')  ) and TERMINAL_ID IN('
         || v_terminals
         || ' )';--OR VALID_TO IS NULL
          ELSIF basis IS NULL --Included by Shyam for DHL starts here
          THEN

             v_strsearch :=
                  'VALID_TO IS NOT NULL AND VALID_TO < TO_DATE ('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')'
               || ' and  ((qm.SALES_PERSON ='
               || ''''
               || salesPersonId
               || ''''
               || ' ) OR (CREATED_BY='
               || ''''
               || user_Id
               || ''''
               || ' AND TERMINAL_ID='
               || ''''
               || terminal_id
               || ''''
               || '))';

         --Included by Shyam for DHL ends here
          ELSE
            v_strsearch :=
                  ' ( VALID_TO BETWEEN   TO_DATE ('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')  AND TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')) and TERMINAL_ID IN('
         || v_terminals
         || ' )';--OR VALID_TO IS NULL
         END IF;
      ELSE
         IF basis = '>='
         THEN
            v_strsearch :=
                  ' VALID_TO <= TO_DATE ('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') and TERMINAL_ID IN('
         || v_terminals
         || ' )';
         ELSE
            v_strsearch :=
                  ' VALID_TO BETWEEN   TO_DATE ('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') AND   TO_DATE ('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') and TERMINAL_ID IN('
         || v_terminals
         || ' )';
         END IF;
      END IF;


      v_strqry := v_strqry || v_strsearch||v_sql2;

      /*IF LENGTH (trim(service_level)) > 0
      THEN
         v_strqry :=
               v_strqry
            || '   AND  SERVICE_LEVEL_ID IN('
            || qms_rsr_rates_pkg.seperator (service_level)
            || ')';
      END IF;*/
      v_strqry := v_strqry || v_orderby ;

      v_strqry :=
            v_strqry
         || ')t1  where rownum <= (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ') + '
         || p_page_rows
         || ') '
         || '  where   rn > (('
         || p_page_no
         || ' - 1) * '
         || p_page_rows
         || ')';
      v_strcount := v_strcount || v_strsearch||v_sql2;

      /*IF LENGTH (service_level) > 0
      THEN
         v_strcount :=
               v_strcount
            || '   AND SERVICE_LEVEL_ID IN('
            || qms_rsr_rates_pkg.seperator (service_level)
            || ')';
      END IF;*/

      v_strcount := v_strcount || ')t1 )';
      print_out('********'||v_strqry);
      OPEN dhl_ref1
       FOR v_strqry;

      EXECUTE IMMEDIATE (v_strcount)
                   INTO p_tot_rec;

      /*SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;*/
       p_tot_pages := CEIL(p_tot_rec/p_page_rows);
   /*EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.put_line (SQLERRM);*/
   END qms_quote_expiry_report;

   PROCEDURE qms_quote_expiry_report_excl (
      ship_mode             VARCHAR2,
      customer_id           VARCHAR2,
      service_level         VARCHAR2,
      terminal_id           VARCHAR2,
      basis                 VARCHAR2,
      flage                 VARCHAR2,
      from_date             VARCHAR2,
      TO_DATE               VARCHAR2,
      p_userdateformat      VARCHAR2,
      dhl_ref1        OUT   sys_refcursor
   )
   IS

    CURSOR c1
      IS
         (SELECT terminal_id term_id
            FROM DUAL
          UNION
          SELECT     child_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY PRIOR child_terminal_id = parent_terminal_id
          START WITH parent_terminal_id = terminal_id);

      CURSOR c2
      IS
         (SELECT DISTINCT terminalid term_id
                     FROM FS_FR_TERMINALMASTER);

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql2           VARCHAR2 (1000);
      v_sql1        VARCHAR2 (4000);
      v_shipmode    VARCHAR2 (300);
      v_terminal    VARCHAR2 (300);
      v_strqry      VARCHAR2 (32767);
      v_strcount    VARCHAR2 (32767);
      v_strsearch   VARCHAR2 (200);

   BEGIN



      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE terminal_id;

      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      IF (ship_mode='1')
      THEN
        IF service_level IS NULL
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'')) ';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''1'')) ';
        ELSE
           --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
             v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSIF (ship_mode='2')
      THEN
        IF service_level IS NULL
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2''))';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSIF (ship_mode='4')
      THEN
        IF service_level IS NULL
        THEN
            --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4''))';
              v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4''))';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      ELSE
        IF service_level IS NULL
        THEN
          v_sql2:='';
        ELSE
        --  v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BM.LANE_NO=BD.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO AND BD.LINE_NO=0 AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''SBR'') AND QR.LINE_NO=0 and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (service_level)||')))';
        END IF;
      END IF;

      v_shipmode := '''' || ship_mode || '''';
      v_terminal := '''' || terminal_id || '''';
     --@@Modified by Kameswari for the WPBN issue 30313
     IF customer_id IS NULL OR LENGTH (trim(customer_id)) = 0 THEN
      v_strqry :=
            ' SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'') SERVICE_LEVEL_ID,ORIGIN_LOCATION,VALID_TO,SHIPMENT_MODE,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
         || ' ORIGINCOUNTRYID,DEST_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
         || ' ) DESTCOUNTRYID,COMPANYNAME,TO_CHAR(qm.CREATED_DATE, ''DD-MON-YYYY'')CREATED_DATE,QUOTE_STATUS FROM QMS_QUOTE_MASTER qm ,FS_FR_CUSTOMERMASTER C WHERE C.CUSTOMERID=qm.CUSTOMER_ID AND SHIPMENT_MODE ='
         || v_shipmode
         || ' AND qm.active_flag=''A'' and qm.complete_flag=''C'' and TERMINAL_ID IN('
         || v_terminals
         || ' ) AND QM.CUSTOMER_ID=C.CUSTOMERID AND ';


         ELSE
         v_strqry :=
            ' SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'') SERVICE_LEVEL_ID,ORIGIN_LOCATION,VALID_TO,SHIPMENT_MODE,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
         || ' ORIGINCOUNTRYID,DEST_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
         || ' ) DESTCOUNTRYID,COMPANYNAME,TO_CHAR(qm.CREATED_DATE, ''DD-MON-YYYY'')CREATED_DATE,QUOTE_STATUS FROM QMS_QUOTE_MASTER qm ,FS_FR_CUSTOMERMASTER C WHERE C.CUSTOMERID=qm.CUSTOMER_ID AND SHIPMENT_MODE ='
         || v_shipmode
         || ' AND qm.active_flag=''A'' and qm.complete_flag=''C'' and TERMINAL_ID IN('
         || v_terminals
         || ' ) AND CUSTOMER_ID IN ('
         || qms_rsr_rates_pkg.seperator (customer_id)
         || ') AND ';
         END IF;


       /* v_strcount :=
            ' SELECT IU_FLAG,CUSTOMER_ID,QUOTE_ID ,SERVICE_LEVEL_ID,ORIGIN_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = ORIGIN_LOCATION) '
         || ' ORIGINCOUNTRYID,DEST_LOCATION,'
         || ' (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = DEST_LOCATION '
         || ' ) DESTCOUNTRYID FROM QMS_QUOTE_MASTER qm WHERE  SHIPMENT_MODE ='
         || v_shipmode
         || '  AND qm.active_flag=''A'' and qm.complete_flag=''C'' and TERMINAL_ID IN ('
         || v_terminals
         || ')  AND CUSTOMER_ID IN ('
         || qms_rsr_rates_pkg.seperator (customer_id)
         || ') AND ';*/



      IF UPPER (flage) = 'Y'
      THEN
         IF basis = '>='
         THEN
            v_strsearch :=
                  '( VALID_TO >= TO_DATE('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || '))';
          ELSE
            v_strsearch :=
                  ' ( VALID_TO BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')  AND TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')  )';--OR VALID_TO IS NULL
         END IF;
      ELSE
         IF basis = '>='
         THEN
            v_strsearch :=
                  ' VALID_TO <= TO_DATE('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')';
         ELSE
            v_strsearch :=
                  ' VALID_TO BETWEEN   TO_DATE('
               || ''''
               || from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') AND   TO_DATE ('
               || ''''
               || TO_DATE
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') ';
         END IF;
      END IF;

       v_strqry := v_strqry || v_strsearch || v_sql2
      --@@Modified by kameswari for the WPBN issue-38116
      ||'ORDER BY QUOTE_ID';
--print_out(v_strqry);
      OPEN dhl_ref1
       FOR v_strqry;

      /*EXECUTE IMMEDIATE (v_strcount)
                   INTO p_tot_rec;
      SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;*/
   /*EXCEPTION
      WHEN OTHERS
      THEN
         DBMS_OUTPUT.put_line (SQLERRM);*/
   END qms_quote_expiry_report_excl;

   /*PROCEDURE qms_active_report (
      ship_mode               VARCHAR2,
      customer_id             VARCHAR2,
      sales_person_id         VARCHAR2,
      quote_status            VARCHAR2,
      from_location           VARCHAR2,
      dest_location           VARCHAR2,
      service_lev_id          VARCHAR2,
      exp_vdate               DATE,
      exp_edate               DATE,
      p_page_no               NUMBER DEFAULT 1,
      p_page_rows             NUMBER DEFAULT 50,
      p_tot_rec         OUT   NUMBER,
      p_tot_pages       OUT   NUMBER,
      dhl_ref           OUT   sys_refcursor
   )
   IS
      v_sql1       VARCHAR2 (4000);
      v_shipmode   VARCHAR2 (300);
   BEGIN
      v_shipmode := '''' || ship_mode || '''';

      OPEN dhl_ref
       FOR    ' select * from   (select t1.*, rownum rn from  '
           || ' (SELECT  A.ORIGIN_LOCATION ,(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=A.ORIGIN_LOCATION) COUNTRYID,
                SHIPMENT_MODE,DEST_LOCATION, (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=A.DEST_LOCATION ) COUNTRYID ,
                SERVICE_LEVEL_ID,SALESPERSON_NAME,SHIPMENT_MODE ,CREATED_TSTMP,QUOTE_STATUS  FROM QMS_QUOTE_MASTER A,QMS_SALESPERSON_REG B
                WHERE A.SALES_PERSON=B.SALESPERSON_CODE AND SHIPMENT_MODE ='
           || v_shipmode
           || '
                AND CUSTOMER_ID IN ('
           || qms_rsr_rates_pkg.seperator (customer_id)
           || ')
                AND ORGIN_LOCATION IN ('
           || qms_rsr_rates_pkg.seperator (from_location)
           || ')
                AND DEST_LOCATION IN ('
           || qms_rsr_rates_pkg.seperator (dest_location)
           || ')
                AND VALID_UPTO < '
           || ''''
           || exp_vdate
           || ''''
           || '
                AND EFFECTIVE_DATE > '
           || ''''
           || exp_edate
           || ''''
           || '
                AND SERVICE_LEVEL_ID IN('
           || qms_rsr_rates_pkg.seperator (service_lev_id)
           || '))t1  where   rownum <= (('
           || p_page_no
           || ' - 1) * '
           || p_page_rows
           || ') + '
           || p_page_rows
           || ') '
           || '  where   rn > (('
           || p_page_no
           || ' - 1) * '
           || p_page_rows
           || ')';

      EXECUTE IMMEDIATE (   ' select * from   (select t1.*, rownum rn from  '
                         || ' (SELECT  A.ORIGIN_LOCATION ,(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=A.ORIGIN_LOCATION) COUNTRYID,
                              SHIPMENT_MODE,DEST_LOCATION, (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID=A.DEST_LOCATION ) COUNTRYID ,
                              SERVICE_LEVEL_ID,SALESPERSON_NAME,SHIPMENT_MODE ,CREATED_TSTMP,QUOTE_STATUS  FROM QMS_QUOTE_MASTER A,QMS_SALESPERSON_REG B
                              WHERE A.SALES_PERSON=B.SALESPERSON_CODE AND SHIPMENT_MODE ='
                         || v_shipmode
                         || '
                              AND CUSTOMER_ID IN ('
                         || qms_rsr_rates_pkg.seperator (customer_id)
                         || ')
                              AND ORGIN_LOCATION IN ('
                         || qms_rsr_rates_pkg.seperator (from_location)
                         || ')
                              AND DEST_LOCATION IN ('
                         || qms_rsr_rates_pkg.seperator (dest_location)
                         || ')
                              AND VALID_UPTO < '
                         || ''''
                         || exp_vdate
                         || ''''
                         || '
                              AND EFFECTIVE_DATE > '
                         || ''''
                         || exp_edate
                         || ''''
                         || '
                              AND SERVICE_LEVEL_ID IN('
                         || qms_rsr_rates_pkg.seperator (service_lev_id)
                         || '))t1 )'
                        )
                   INTO p_tot_rec;

      SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;
   END qms_active_report;*/

   PROCEDURE qms_pending_report (
      p_shmode                 VARCHAR2,
      p_consoletype            VARCHAR2,
      p_option                 VARCHAR2,
      p_frmdate                VARCHAR2,
      p_todate                 VARCHAR2,
      p_terminalid             VARCHAR2,
      p_userdateformat         VARCHAR2,
      p_page_no                NUMBER DEFAULT 1,
      p_page_rows              NUMBER DEFAULT 50,
      sortBy                   VARCHAR2,
      sortOrder                VARCHAR2,
      p_tot_rec          OUT   NUMBER,
      p_tot_pages        OUT   NUMBER,
      p_rs               OUT   sys_refcursor
   )
   IS
      /*CURSOR c1
      IS
         (SELECT p_terminalid term_id
            FROM DUAL
          UNION
          SELECT child_terminal_id term_id
                FROM fs_fr_terminal_regn
          CONNECT BY PRIOR child_terminal_id = parent_terminal_id
          START WITH parent_terminal_id = p_terminalid
          UNION
          SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = p_terminalid
          UNION
          SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H');*/

      /*CURSOR c2
      IS
         (SELECT DISTINCT terminalid term_id
                     FROM fs_fr_terminalmaster);*/

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql1           VARCHAR2 (1000);
      v_sql2           VARCHAR2 (1000);
      v_orderby        VARCHAR2(50);
   BEGIN

      IF (sortBy='Important') THEN
        v_orderby := ' IU_FLAG ';
      ELSIF (sortBy='CustomerId') THEN
        v_orderby := ' CUSTOMER_ID ';
     ELSIF (sortBy='CustomerName') THEN  --@@Added by Kameswari for the WPBN issue-30313
       v_orderby := ' COMPANYNAME ';
      ELSIF (sortBy='CreatedDate') THEN  --@@Added by Kameswari for enhancements
       v_orderby := ' CREATED_DATE ';
      ELSIF (sortBy='QuoteId') THEN
        v_orderby := ' QUOTE_ID ';
      ELSIF (sortBy='ServiceLevel') THEN
        v_orderby := ' SERVICE_LEVEL_ID ';
      ELSIF (sortBy='FromCountry') THEN
        v_orderby := ' org_country ';
      ELSIF (sortBy='FromLocation') THEN
        v_orderby := ' ORIGIN_LOCATION ';
      ELSIF (sortBy='ToCountry') THEN
        v_orderby := ' dest_country ';
      ELSIF (sortBy='ToLocation') THEN
        v_orderby := ' DEST_LOCATION ';
      ELSE
        v_orderby := ' QUOTE_ID ';
      END IF;
      v_orderby := ' ORDER BY ' || v_orderby || sortOrder ;

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminalid;


      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals:= 'SELECT TERMINALID FROM FS_FR_TERMINALMASTER';
         /*FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);

         v_terminals:= 'SELECT sys_context(''REPORT_CONTEXT'',''v_terminal_id'') term_id
                        FROM DUAL
                      UNION
                      SELECT child_terminal_id term_id
                            FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                      START WITH parent_terminal_id = sys_context(''REPORT_CONTEXT'',''v_terminal_id'')';
                     /*  UNION
                      SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = ''H'' */
                     /*UNION
                      SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN
                      CONNECT BY child_terminal_id = PRIOR parent_terminal_id
                      START WITH child_terminal_id = sys_context(''REPORT_CONTEXT'',''v_terminal_id'')

         /*FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      END IF;
      --@@Added by Kameswari for the WPBN issue-140194
       v_terminals:=' AND QM.TERMINAL_ID IN ( ' || v_terminals  || ')';


      IF (p_shmode='1')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         /* v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in
          (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'')) union
          (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1''))';
*/
          v_sql2:=' and EXISTS  ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''1''))';
          ELSE
   --       v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where (QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
        END IF;
      ELSIF (p_shmode='2' AND p_consoletype='LCL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST''))';
        ELSE
--          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
        END IF;
      ELSIF (p_shmode='2' AND p_consoletype='FCL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.quote_id=qm.id and qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST''))';
        ELSE
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
        END IF;
      ELSIF (p_shmode='4' AND p_consoletype='LTL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''))';
          v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''))';
        ELSE
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH''))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
        END IF;
      ELSIF (p_shmode='4' AND p_consoletype='FTL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''))';
        ELSE
       --   v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||',''SCH'')))';
        END IF;
      ELSE
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
          v_sql2:='';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO  AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs QS.QUOTE_ID=QM.ID AND where qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      END IF;

      /*IF UPPER (p_option) <> 'SERVICELEVELWISE'
      THEN*/
        IF UPPER (p_option) = 'W'
        THEN
           v_sql1 :=
                  ' and QM.CREATED_TSTMP BETWEEN TO_DATE ('
               || ''''
               || p_frmdate
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI'
               || ''''
               || ') '
               || ' AND TO_DATE ('
               || ''''
               || p_todate
               || ''''
               || ','
               || ''''
               || p_userdateformat
               ||' HH24:MI'
               || ''''
               || ') ';
        ELSIF UPPER (p_option) = 'C'
        THEN
           v_sql1 :=
                 ' and QM.CUSTOMER_ID IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') ';
        ELSIF UPPER (p_option) = 'L'
        THEN
           IF p_shmode = '2'
           THEN
           v_sql1 :=
                 ' and QM.ORIGIN_PORT IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') AND QM.DESTIONATION_PORT IN ('
              || qms_rsr_rates_pkg.seperator (p_todate)
              || ')';
           ELSE
           v_sql1 :=
                 ' and QM.ORIGIN_LOCATION IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') AND QM.DEST_LOCATION IN ('
              || qms_rsr_rates_pkg.seperator (p_todate)
              || ')';
            END IF;
        ELSIF UPPER (p_option) = 'SP'
        THEN
           v_sql1 :=
                 ' and (QM.SALES_PERSON IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') OR QM.CREATED_BY IN (SELECT USERID FROM FS_USERMASTER WHERE EMPID IN ('||qms_rsr_rates_pkg.seperator (p_frmdate)||') AND LOCATIONID=QM.TERMINAL_ID))';
         --@@Added by Kameswari for the WPBN issue-140194
          v_terminals :='';
        /*ELSIF UPPER (p_option) = 'SERVICELEVELWISE'
        THEN
           v_sql1 :=
                 ' QM.SERVICE_LEVEL_ID IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ')';*/
        END IF;

   --@@Modified by Kameswari for the WPBN issue-30313
                print_out(' select * from   (select t1.*, rownum rn from ( select QM.QUOTE_ID, QM.IU_FLAG, QM.CUSTOMER_ID, DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
                                   DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE_LEVEL_ID, QM.ORIGIN_LOCATION, QM.DEST_LOCATION,QM.VERSION_NO,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country ,QM.CREATED_DATE,C.COMPANYNAME,QM.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QM, FS_FR_CUSTOMERMASTER C WHERE '||'C.CUSTOMERID=QM.CUSTOMER_ID AND'
          --@@Modified by Kameswari for the WPBN issue-124667
            --|| ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
             || ' qm.id not in (select distinct quote_id from qms_quotes_updated) and qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  '
             || v_terminals
             || v_sql1
             || ' and QM.ACTIVE_FLAG <> ''I'' AND QM.ACTIVE_FLAG=''A'' AND UPPER(QUOTE_STATUS)=''PEN'' '||v_sql2 || v_orderby || ')t1
             WHERE   ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
             WHERE   rn > ((:v_page_no - 1) * :v_page_rows)');
        OPEN p_rs
         FOR    ' select * from   (select t1.*, rownum rn from ( select QM.QUOTE_ID, QM.IU_FLAG, QM.CUSTOMER_ID, DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID AND QMSMQ.ACTIVE_FLAG=''A''),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
                                   DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE_LEVEL_ID, QM.ORIGIN_LOCATION, QM.DEST_LOCATION,QM.VERSION_NO,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country ,QM.CREATED_DATE,C.COMPANYNAME,QM.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QM, FS_FR_CUSTOMERMASTER C WHERE '||'C.CUSTOMERID=QM.CUSTOMER_ID AND'
          --@@Modified by Kameswari for the WPBN issue-124667
            --|| ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
             || ' qm.id not in (select distinct quote_id from qms_quotes_updated) and qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  '
             || v_terminals
             || v_sql1
             || ' and QM.ACTIVE_FLAG <> ''I'' AND QM.ACTIVE_FLAG=''A'' AND UPPER(QUOTE_STATUS)=''PEN'' AND QM.VALID_TO IS NOT NULL AND TO_DATE(QM.VALID_TO,''DD-MON-YYYY'')>=ANY(SELECT TO_DATE(SYSDATE,''DD-MON-YYYY'') FROM DUAL)'||v_sql2 || v_orderby || ')t1
             WHERE   ROWNUM <= ((:v_page_no - 1) * :v_page_rows) + :v_page_rows)
             WHERE   rn > ((:v_page_no - 1) * :v_page_rows)'
             USING p_page_no,p_page_rows,p_page_rows,p_page_no,p_page_rows;

        EXECUTE IMMEDIATE (   ' select count(*) from   (select t1.*, rownum rn from ( select QM.QUOTE_ID, QM.IU_FLAG, QM.CUSTOMER_ID,
                               DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
 DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE_LEVEL_ID, QM.ORIGIN_LOCATION, QM.DEST_LOCATION,QM.VERSION_NO,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,
                                   (SELECT countryid FROM FS_FR_LOCATIONMASTER  WHERE locationid = qm.dest_location) dest_country,QM.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QM WHERE '
                     --@@Modified by Kameswari for the WPBN issue-124667
            --|| ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
             || ' qm.id not in (select distinct quote_id from qms_quotes_updated) and qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  '
              || v_terminals

                           || v_sql1
                           || ' and ACTIVE_FLAG <>''I'' AND QM.ACTIVE_FLAG=''A'' AND UPPER(QUOTE_STATUS)=''PEN'' AND QM.VALID_TO IS NOT NULL AND TO_DATE(QM.VALID_TO,''DD-MON-YYYY'')>=ANY(SELECT TO_DATE(SYSDATE,''DD-MON-YYYY'') FROM DUAL)'||v_sql2|| ')t1 )'
                          )
                     INTO p_tot_rec;

        IF (p_page_rows > 0)
        THEN
           /*SELECT CEIL ((p_tot_rec / p_page_rows))
             INTO p_tot_pages
             FROM DUAL;*/
             p_tot_pages := CEIL(p_tot_rec/p_page_rows);
        ELSE
           p_tot_pages :=0;
           /*SELECT 0
             INTO p_tot_pages
             FROM DUAL;*/
        END IF;
   END;

   PROCEDURE qms_pending_report_excel (
      p_shmode                 VARCHAR2,
      p_consoletype            VARCHAR2,
      p_option                 VARCHAR2,
      p_frmdate                VARCHAR2,
      p_todate                 VARCHAR2,
      p_terminalid             VARCHAR2,
      p_userdateformat         VARCHAR2,
      p_rs               OUT   sys_refcursor
   )
   IS
      CURSOR c1
      IS
         (SELECT p_terminalid term_id
            FROM DUAL
          UNION
          SELECT child_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY PRIOR child_terminal_id = parent_terminal_id
          START WITH parent_terminal_id = p_terminalid);
         /* UNION
          SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = p_terminalid
          UNION
          SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H');*/

      CURSOR c2
      IS
         (SELECT DISTINCT terminalid term_id
                     FROM FS_FR_TERMINALMASTER);

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql1           VARCHAR2 (1000);
      v_sql2           VARCHAR2 (1000);
   BEGIN

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminalid;



      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
      --@@Added by Kameswari for the WPBN issue-140194
       v_terminals := ' AND QM.TERMINAL_ID IN (' || v_terminals || ')';

      IF (p_shmode='1')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''1''))';
        ELSE
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
          v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS  (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''1'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''1'' and qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      ELSIF (p_shmode='2' AND p_consoletype='LCL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST''))';
        ELSE
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where  QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK<>''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      ELSIF (p_shmode='2' AND p_consoletype='FCL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST''))';
           v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND  bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST''))';
        ELSE
        -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
           v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND  bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''2'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''2'' AND QS.WEIGHT_BREAK =''LIST'' AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      ELSIF (p_shmode='4' AND p_consoletype='LTL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''))';
        ELSE
         -- v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND  bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK<>''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK<>''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      ELSIF (p_shmode='4' AND p_consoletype='FTL')
      THEN
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND  bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'')) union (select qs.quote_id from qms_quote_spotrates qs where QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''))';
        ELSE
--          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO  and bm.SHIPMENT_MODE=''4'' AND BM.WEIGHT_BREAK =''LIST'' and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where  QS.QUOTE_ID=QM.ID AND  qs.SHIPMENT_MODE=''4'' AND QS.WEIGHT_BREAK =''LIST''  AND qs.SERVICELEVEL IN ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      ELSE
        IF UPPER (p_option) <> 'SERVICELEVELWISE'
        THEN
          v_sql2:='';
        ELSE
          --v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
            v_sql2:=' and EXISTS ((select qr.quote_id from qms_quote_rates qr where QR.QUOTE_ID=QM.ID AND EXISTS (select 1 from qms_buyrates_dtl bd,qms_buyrates_master bm where BD.BUYRATEID=QR.BUYRATE_ID AND BD.LANE_NO=QR.RATE_LANE_NO AND  bd.buyrateid=bm.buyrateid AND (BD.LANE_NO=BM.LANE_NO OR BM.LANE_NO IS NULL ) AND BD.VERSION_NO=BM.VERSION_NO and bd.SERVICE_LEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||'))) union (select qs.quote_id from qms_quote_spotrates qs where  QS.QUOTE_ID=QM.ID AND qs.SERVICELEVEL in ('|| qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
        END IF;
      END IF;

      /*IF UPPER (p_option) <> 'SERVICELEVELWISE'
      THEN*/
        IF UPPER (p_option) = 'W'
        THEN
            v_sql1 :=
                  ' and QM.CREATED_TSTMP BETWEEN TO_DATE ('
               || ''''
               || p_frmdate
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI'
               || ''''
               || ') '
               || ' AND TO_DATE ('
               || ''''
               || p_todate
               || ''''
               || ','
               || ''''
               || p_userdateformat
               ||' HH24:MI'
               || ''''
               || ') ';

        ELSIF UPPER (p_option) = 'C'
        THEN
           v_sql1 :=
                 ' and QM.CUSTOMER_ID IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') ';
        ELSIF UPPER (p_option) = 'L'
        THEN
           v_sql1 :=
                 ' and QM.ORIGIN_LOCATION IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') AND QM.DEST_LOCATION IN ('
              || qms_rsr_rates_pkg.seperator (p_todate)
              || ')';
        ELSIF UPPER (p_option) = 'SP'
        THEN
           v_sql1 :=
                 ' and (QM.SALES_PERSON IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ') OR QM.CREATED_BY IN (SELECT USERID FROM FS_USERMASTER WHERE EMPID IN ('||qms_rsr_rates_pkg.seperator (p_frmdate)||')))';
           --@@Added by Kameswari for the WPBN issue-140194
           v_terminals :='';
        /*ELSIF UPPER (p_option) = 'SERVICELEVELWISE'
        THEN
           v_sql1 :=
                 ' QM.SERVICE_LEVEL_ID IN ('
              || qms_rsr_rates_pkg.seperator (p_frmdate)
              || ')';*/
        END IF;

        --@@Modified by Kameswari for the WPBN issue-30313
        OPEN p_rs
         FOR    'select QM.QUOTE_ID, QM.IU_FLAG, QM.CUSTOMER_ID, DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')) ,(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                    AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'') SERVICE_LEVEL_ID, QM.ORIGIN_LOCATION, QM.DEST_LOCATION,QM.VERSION_NO,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country,CREATED_DATE,COMPANYNAME FROM QMS_QUOTE_MASTER QM ,FS_FR_CUSTOMERMASTER C WHERE C.CUSTOMERID=QM.CUSTOMER_ID AND'
            --@@Modified by Kameswari for the WPBN issue-124667
            --|| ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
             || ' qm.id not in (select distinct quote_id from qms_quotes_updated) and qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  /*AND QM.TERMINAL_ID IN (*/'--@@Commented by Kameswari for the WPBN issue-140194
             || v_terminals
             /*|| ')'*/
             || v_sql1
             --@@Modified by kameswari for the WPBN issue-38116
             || ' and QM.ACTIVE_FLAG <> ''I'' AND QM.ACTIVE_FLAG=''A'' AND UPPER(QUOTE_STATUS)=''PEN'' '||v_sql2 ||'ORDER BY QM.QUOTE_ID';

       /* EXECUTE IMMEDIATE (   ' select count(*) from   (select t1.*, rownum rn from ( select QM.QUOTE_ID, QM.IU_FLAG, QM.CUSTOMER_ID, DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'')), ''Multi-Modal'') SERVICE_LEVEL_ID, QM.ORIGIN_LOCATION, QM.DEST_LOCATION,QM.VERSION_NO,(SELECT countryid FROM fs_fr_locationmaster WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM fs_fr_locationmaster  WHERE locationid = qm.dest_location) dest_country FROM qms_quote_master QM WHERE '
                --@@Modified by Kameswari for the WPBN issue-124667
            --|| ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
             || ' qm.id not in (select distinct quote_id from qms_quotes_updated) and qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND QM.TERMINAL_ID IN ('
                   || v_terminals
                           || ')'
                           || v_sql1
                           || ' and ACTIVE_FLAG <>''I'' AND QM.ACTIVE_FLAG=''A'' AND UPPER(QUOTE_STATUS)=''PEN'' '||v_sql2|| ')t1 )'
                          )
                     INTO p_tot_rec;*/


   END;

   PROCEDURE qms_app_rej_report (
      p_userid              VARCHAR2,
      p_terminalid          VARCHAR2,
      p_type                VARCHAR2,
      p_aprovalflag         VARCHAR2,
      p_page_no             NUMBER DEFAULT 1,
      p_page_rows           NUMBER DEFAULT 50,
      sortBy                VARCHAR2,
      sortOrder             VARCHAR2,
      p_tot_rec       OUT   NUMBER,
      p_tot_pages     OUT   NUMBER,
      p_rs            OUT   sys_refcursor

   )
   IS
      v_empid          VARCHAR2 (16);

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

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql1           VARCHAR2 (1000);
      v_sql2           VARCHAR2 (32767);
      v_orderby        VARCHAR2 (50);
      v_sql5           VARCHAR2 (50);  --@@Added by Kameswari for the WPBN issue-
   BEGIN
      IF (sortBy='Important') THEN
        v_orderby := ' IU_FLAG ';
      ELSIF (sortBy='CustomerId') THEN
        v_orderby := ' CUSTOMER_ID ';
     ELSIF (sortBy='CustomerName') THEN  --@@Added by Kameswari for the WPBN issue-30313
       v_orderby := ' COMPANYNAME ';
      ELSIF (sortBy='QuoteId') THEN
        v_orderby := ' QUOTE_ID ';
      ELSIF (sortBy='ShimentMode') THEN
        v_orderby := ' SHIPMENT_MODE ';
      ELSIF (sortBy='ServiceLevel') THEN
        v_orderby := ' SERVICE_LEVEL_ID ';
      ELSIF (sortBy='FromCountry') THEN
        v_orderby := ' org_country ';
      ELSIF (sortBy='FromLocation') THEN
        v_orderby := ' origin_location ';
      ELSIF (sortBy='ToCountry') THEN
        v_orderby := ' dest_country ';
      ELSIF (sortBy='ToLocation') THEN
        v_orderby := ' dest_location ';
      ELSIF (sortBy='DueDate') THEN
        v_orderby := ' DUE_DATE ';
      ELSIF (sortBy='RejectedBy') THEN
        v_orderby := ' ESCALATED_TO ';
      ELSIF (sortBy='RejectedDate') THEN
        v_orderby := ' APP_REJ_TSTMP ';
      ELSIF (sortBy='InternalNotes') THEN
        v_orderby := ' internal_notes ';
      ELSIF (sortBy='ExternalNotes') THEN
        v_orderby := ' external_notes ';
      ELSE
        v_orderby := ' QUOTE_ID ';
      END IF;
      v_orderby := ' ORDER BY ' || v_orderby || sortOrder ;

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminalid;


      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals:='SELECT TERMINALID FROM FS_FR_TERMINALMASTER';
         /*FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         v_terminals:= 'SELECT sys_context(''REPORT_CONTEXT'',''v_terminal_id'') term_id
                        FROM DUAL
                      UNION SELECT  child_terminal_id term_id
                            FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                      START WITH parent_terminal_id = sys_context(''REPORT_CONTEXT'',''v_terminal_id'')';
         /*FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      END IF;

      --v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      SELECT empid
              INTO v_empid
              FROM FS_USERMASTER
             WHERE userid = p_userid AND locationid = p_terminalid;

      IF p_type = 'ESC'
      THEN
         IF p_aprovalflag = 'S'
         THEN
            Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);
            v_sql1 :=
              ' AND ESCALATION_FLAG=''Y''  AND QUOTE_STATUS=''QUE''  AND QM.ESCALATED_TO =sys_context(''REPORT_CONTEXT'',''v_emp_id'')';
        v_sql5 := ' AND Ur.USERID=Qm.ESCALATED_TO';
         ELSIF p_aprovalflag = 'O'
         THEN
            Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
            Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
            Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

            v_sql1 :=
                  ' AND ESCALATION_FLAG=''Y''  AND QUOTE_STATUS=''QUE''  AND ((QM.CREATED_BY=
                  sys_context(''REPORT_CONTEXT'',''v_user_id'') AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                  OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';
            v_sql5 := ' AND Ur.USERID = Qm.SALES_PERSON'; --@@Added by Kameswari for the WPBN issue-
         END IF;
      ELSIF p_type = 'APP'
      THEN
         Dbms_Session.set_context('REPORT_CONTEXT','v_type',p_type);
         Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

         v_sql1 :=
               ' AND UPPER(QUOTE_STATUS)=sys_context(''REPORT_CONTEXT'',''v_type'')
                 AND (SENT_FLAG IS NULL OR SENT_FLAG =''U'') AND
                 ((QM.CREATED_BY =sys_context(''REPORT_CONTEXT'',''v_user_id'')
                 AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                 OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';
      v_sql2:='';
v_sql5 := ' AND Ur.USERID = Qm.SALES_PERSON';
        --@@Commented by Kameswari for the WPBN issue-140194
        /* v_sql2 :=
               ' and  QM.TERMINAL_ID IN ('
            || v_terminals
            || ')';*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_type',p_type);
         Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

         v_sql1 :=
               'AND UPPER(QUOTE_STATUS)=sys_context(''REPORT_CONTEXT'',''v_type'')
                AND ((QM.CREATED_BY =sys_context(''REPORT_CONTEXT'',''v_user_id'')
                AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';
        v_sql5 := ' AND Ur.USERID = Qm.SALES_PERSON';
        --@@Commented by Kameswari for the WPBN issue-140194
        /*  v_sql2 :=
               ' and  QM.TERMINAL_ID IN ('
            || v_terminals
            || ')';*/
      END IF;

   print_out('select * from   (select t1.*, rownum rn from ( select  qm.QUOTE_ID, DECODE((SELECT COUNT(*)
                              FROM FS_RT_LEG LG, FS_RT_PLAN RP
                             WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                             1, QM.SHIPMENT_MODE,CASE (SELECT DISTINCT QMS.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =QM.QUOTE_ID) WHEN ''Y'' THEN QM.SHIPMENT_MODE ELSE 100 END) SHIPMENT_MODE, qm.IU_FLAG, qm.CUSTOMER_ID, DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal''), SERVICE_LEVEL_ID, qm.ESCALATED_TO, qm.MODIFIED_DATE,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country,qm.internal_notes,qm.external_notes,qm.VERSION_NO,C.OPERATIONS_EMAILID,qm.origin_location,qm.dest_location,qm.APP_REJ_TSTMP,
                                   '

           || ' to_date(days_cal_fun((SELECT Rp.ALLOTED_TIME || '' :00 '' FROM FS_USERMASTER Ur, fs_rep_officers_master Rp WHERE Rp.Userid = Ur.Userid and Rp.Locationid = Ur.Locationid AND(rp.shipment_mode = qm.shipment_mode or
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 5, 2, 3, 1, 3)) OR
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 6, 2, 6, 1, 5)) OR
 rp.shipment_mode = 7)  '|| v_sql5|| '),TO_CHAR(NVL(qm.APP_REJ_TSTMP,SYSDATE),''DD-MON-YY HH24:MI:SS'')),''DD-MM-YY HH24:MI:SS'') DUE_DATE,DECODE(qm.EMAIL_FLAG,''Y'',''EMAIL,'')EMAIL_FLAG,DECODE(qm.FAX_FLAG,''Y'',''FAX,'')FAX_FLAG,DECODE(qm.PRINT_FLAG,''Y'',''PRINT'')PRINT_FLAG ,C.COMPANYNAME,qm.IS_MULTI_QUOTE FROM  QMS_QUOTE_MASTER qm,FS_FR_CUSTOMERMASTER C WHERE'
             || ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND (QM.ACTIVE_FLAG <>''I'' OR QM.ACTIVE_FLAG IS NULL) AND QM.CUSTOMER_ID=C.CUSTOMERID '
           || v_sql2
           || v_sql1
           || v_orderby || ') t1  where   rownum <= ((:v_page_no- 1) * :v_page_rows) + :v_page_rows )
            WHERE   rn > ((:v_page_no - 1) * :v_page_rows)');

    --@@Modified by Kameswari for the WPBN issue-30313
      OPEN p_rs
       FOR    'select * from   (select t1.*, rownum rn from ( select  qm.QUOTE_ID, DECODE((SELECT COUNT(*)
                              FROM FS_RT_LEG LG, FS_RT_PLAN RP
                             WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                             1, QM.SHIPMENT_MODE,CASE (SELECT DISTINCT QMS.IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =QM.QUOTE_ID) WHEN ''Y'' THEN QM.SHIPMENT_MODE ELSE 100 END) SHIPMENT_MODE, qm.IU_FLAG, qm.CUSTOMER_ID, DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal''), SERVICE_LEVEL_ID, qm.ESCALATED_TO, qm.MODIFIED_DATE,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country,qm.internal_notes,qm.external_notes,qm.VERSION_NO,C.OPERATIONS_EMAILID,qm.origin_location,qm.dest_location,qm.APP_REJ_TSTMP,
                                   '

           || ' to_date(days_cal_fun((SELECT Rp.ALLOTED_TIME || '' :00 '' FROM FS_USERMASTER Ur, fs_rep_officers_master Rp WHERE Rp.Userid = Ur.Userid and Rp.Locationid = Ur.Locationid AND(rp.shipment_mode = qm.shipment_mode or
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 5, 2, 3, 1, 3)) OR
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 6, 2, 6, 1, 5)) OR
 rp.shipment_mode = 7)  '|| v_sql5|| ' AND UR.LOCATIONID IN (SELECT  R.CHILD_TERMINAL_ID FROM fs_fr_terminal_regn R
               WHERE R.PARENT_TERMINAL_ID IN
                     (SELECT R.PARENT_TERMINAL_ID term_id
                        FROM fs_fr_terminal_regn R
                       WHERE R.CHILD_TERMINAL_ID =sys_context(''REPORT_CONTEXT'',''v_terminal_id'') )) ),TO_CHAR(NVL(qm.APP_REJ_TSTMP,SYSDATE),''DD-MON-YY HH24:MI:SS'')),''DD-MM-YY HH24:MI:SS'') DUE_DATE,DECODE(qm.EMAIL_FLAG,''Y'',''EMAIL,'')EMAIL_FLAG,DECODE(qm.FAX_FLAG,''Y'',''FAX,'')FAX_FLAG,DECODE(qm.PRINT_FLAG,''Y'',''PRINT'')PRINT_FLAG ,C.COMPANYNAME,qm.IS_MULTI_QUOTE FROM  QMS_QUOTE_MASTER qm,FS_FR_CUSTOMERMASTER C WHERE'
             || ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND (QM.ACTIVE_FLAG <>''I'' OR QM.ACTIVE_FLAG IS NULL) AND QM.CUSTOMER_ID=C.CUSTOMERID '
           || v_sql2
           || v_sql1
           || v_orderby || ') t1  where   rownum <= ((:v_page_no- 1) * :v_page_rows) + :v_page_rows )
            WHERE   rn > ((:v_page_no - 1) * :v_page_rows)'
            USING p_page_no,p_page_rows,p_page_rows,p_page_no,p_page_rows;



      EXECUTE IMMEDIATE (   '  select count(*) from   (select t1.*, rownum rn from  (select qm.QUOTE_ID, qm.SHIPMENT_MODE, qm.IU_FLAG, qm.CUSTOMER_ID, qm.SERVICE_LEVEL_ID, qm.ESCALATED_TO, qm.MODIFIED_DATE,(SELECT countryid FROM fs_fr_locationmaster WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM fs_fr_locationmaster WHERE locationid = qm.dest_location) dest_country,''Internal'',''External'',qm.VERSION_NO from qms_quote_master qm, FS_FR_CUSTOMERMASTER C where '
                         || ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id) and (QM.ACTIVE_FLAG <>''I'' OR QM.ACTIVE_FLAG IS NULL) AND QM.CUSTOMER_ID=C.CUSTOMERID '
/*                         || ' AND QM.TERMINAL_ID IN ('
                         || v_terminals
                         || ')'*/
                         || v_sql2
                         || v_sql1
                         || ' ) t1 )'
                        )
                   INTO p_tot_rec;


      /*SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;*/
      p_tot_pages := CEIL(p_tot_rec/p_page_rows);


   END;

   PROCEDURE qms_app_rej_report_excel (
      p_userid              VARCHAR2,
      p_terminalid          VARCHAR2,
      p_type                VARCHAR2,
      p_aprovalflag         VARCHAR2,
      p_rs            OUT   sys_refcursor
   )
   IS
      v_empid          VARCHAR2 (16);

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

      v_opr_adm_flag   VARCHAR2 (10);
      v_terminals      VARCHAR2 (32767);
      v_sql1           VARCHAR2 (1000);
      v_sql2           VARCHAR2 (32767);
      --v_orderby        VARCHAR2 (50);

   BEGIN


      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_terminalid;


      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals:='SELECT TERMINALID FROM FS_FR_TERMINALMASTER';
         /*FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         v_terminals:= 'SELECT sys_context(''REPORT_CONTEXT'',''v_terminal_id'') term_id
                        FROM DUAL
                      UNION SELECT  child_terminal_id term_id
                            FROM FS_FR_TERMINAL_REGN
                      CONNECT BY PRIOR child_terminal_id = parent_terminal_id
                      START WITH parent_terminal_id = sys_context(''REPORT_CONTEXT'',''v_terminal_id'')';
         /*FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;*/
      END IF;

      --v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);

      SELECT empid
              INTO v_empid
              FROM FS_USERMASTER
             WHERE userid = p_userid AND locationid = p_terminalid;

      IF p_type = 'ESC'
      THEN
         IF p_aprovalflag = 'S'
         THEN
            Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);
            v_sql1 :=
              ' AND ESCALATION_FLAG=''Y''  AND QUOTE_STATUS=''QUE''  AND QM.ESCALATED_TO =sys_context(''REPORT_CONTEXT'',''v_emp_id'')';
         ELSIF p_aprovalflag = 'O'
         THEN
            Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
            Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
            Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

            v_sql1 :=
                  ' AND ESCALATION_FLAG=''Y''  AND QUOTE_STATUS=''QUE''  AND ((QM.CREATED_BY=
                  sys_context(''REPORT_CONTEXT'',''v_user_id'') AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                  OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';
         END IF;
      ELSIF p_type = 'APP'
      THEN
         Dbms_Session.set_context('REPORT_CONTEXT','v_type',p_type);
         Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

         v_sql1 :=
               ' AND UPPER(QUOTE_STATUS)=sys_context(''REPORT_CONTEXT'',''v_type'')
                 AND (SENT_FLAG IS NULL OR SENT_FLAG =''U'') AND
                 ((QM.CREATED_BY =sys_context(''REPORT_CONTEXT'',''v_user_id'')
                 AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                 OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';
--@@Commented by Kameswari for the WPBN issue-140194
         /*v_sql2 :=
               ' and  QM.TERMINAL_ID IN ('
            || v_terminals
            || ')';*/
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_type',p_type);
         Dbms_Session.set_context('REPORT_CONTEXT','v_user_id',p_userid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_terminalid);
         Dbms_Session.set_context('REPORT_CONTEXT','v_emp_id',v_empid);

         v_sql1 :=
               'AND UPPER(QUOTE_STATUS)=sys_context(''REPORT_CONTEXT'',''v_type'')
                AND ((QM.CREATED_BY =sys_context(''REPORT_CONTEXT'',''v_user_id'')
                AND QM.TERMINAL_ID=sys_context(''REPORT_CONTEXT'',''v_terminal_id''))
                OR QM.SALES_PERSON =sys_context(''REPORT_CONTEXT'',''v_emp_id''))';

--@@Commented by Kameswari for the WPBN issue-140194
         /* v_sql2 :=
               ' and  QM.TERMINAL_ID IN ('
            || v_terminals
            || ')';*/
      END IF;

   --p_pagerows := 2;
   --@@Modified by Kameswari for the WPBN issue-30313
      OPEN p_rs
       FOR    ' select qm.QUOTE_ID, DECODE((SELECT COUNT(*)
                              FROM FS_RT_LEG LG, FS_RT_PLAN RP
                             WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                             1, QM.SHIPMENT_MODE, 100) SHIPMENT_MODE, qm.IU_FLAG, qm.CUSTOMER_ID, DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'') SERVICE_LEVEL_ID, qm.ESCALATED_TO, qm.MODIFIED_DATE,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid = qm.dest_location) dest_country,qm.internal_notes,qm.external_notes,qm.VERSION_NO,C.OPERATIONS_EMAILID,qm.origin_location,qm.dest_location,qm.APP_REJ_TSTMP '
           || ' ,to_date(days_cal_fun((SELECT Rp.ALLOTED_TIME || '' :00 '' FROM FS_USERMASTER Ur, fs_rep_officers_master Rp WHERE Rp.Userid = Ur.Userid and Rp.Locationid = Ur.Locationid AND (rp.shipment_mode = qm.shipment_mode or
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 5, 2, 3, 1, 3)) OR
 rp.shipment_mode IN (DECODE(qm.shipment_mode, 4, 6, 2, 6, 1, 5)) OR
 rp.shipment_mode = 7)  AND Ur.EMPID=qm.ESCALATED_TO),TO_CHAR(NVL(qm.APP_REJ_TSTMP,SYSDATE),''DD-MON-YY HH24:MI:SS'')),''DD-MM-YY HH24:MI:SS'') DUE_DATE,C.COMPANYNAME FROM  QMS_QUOTE_MASTER qm,FS_FR_CUSTOMERMASTER C WHERE '
           || ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id)  AND (QM.ACTIVE_FLAG <>''I'' OR QM.ACTIVE_FLAG IS NULL) AND QM.CUSTOMER_ID=C.CUSTOMERID '
           /*|| ' and  QM.TERMINAL_ID IN ('
           || v_terminals
           || ')'*/
           || v_sql2
           --@@Modified by kameswari for the WPBN issue-38116
           || v_sql1||'ORDER BY qm.QUOTE_ID';
        --   || ')';

     /* EXECUTE IMMEDIATE (   '  select count(*) from   (select t1.*, rownum rn from  (select qm.QUOTE_ID, qm.SHIPMENT_MODE, qm.IU_FLAG, qm.CUSTOMER_ID, qm.SERVICE_LEVEL_ID, qm.ESCALATED_TO, qm.MODIFIED_DATE,(SELECT countryid FROM fs_fr_locationmaster WHERE locationid = qm.origin_location) org_country,(SELECT countryid FROM fs_fr_locationmaster WHERE locationid = qm.dest_location) dest_country,''Internal'',''External'',qm.VERSION_NO from qms_quote_master qm, FS_FR_CUSTOMERMASTER C where '
                         || ' qm.version_no = (SELECT MAX (version_no) FROM qms_quote_master WHERE quote_id = qm.quote_id) and (QM.ACTIVE_FLAG <>''I'' OR QM.ACTIVE_FLAG IS NULL) AND QM.CUSTOMER_ID=C.CUSTOMERID AND QM.TERMINAL_ID IN ('
                         || v_terminals
                         || ')'
                         || v_sql1
                         || ' ) t1 )'
                        )
                   INTO p_tot_rec;
*/
      DBMS_OUTPUT.put_line ('<< ' || v_opr_adm_flag || ' >>');


      /*SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;

      DBMS_OUTPUT.put_line (p_tot_pages);*/
   END;

   PROCEDURE activity_report (
      p_from_date             VARCHAR2,
      p_to_date               VARCHAR2,
      p_userdateformat        VARCHAR2,
      p_shipment_mode         VARCHAR2,
      p_emp_id                VARCHAR2,
      p_customer_id           VARCHAR2,
      p_quote_status          VARCHAR2,
      p_origin                VARCHAR2,
      p_destination           VARCHAR2,
      p_service_level         VARCHAR2,
      p_terminalId            VARCHAR2,
      p_loginTerminal         VARCHAR2,
      p_fromCountry           VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
      p_toCountry             VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
      p_page_no               NUMBER DEFAULT 1,
      p_page_rows             NUMBER DEFAULT 50,
      sortBy                  VARCHAR2,
      sortOrder               VARCHAR2,
      p_autoUpdated           VARCHAR2, --//@@Added by VenkataLakshmi for the WPBN issue-154390 on 21/02/09
      p_tot_rec         OUT   NUMBER,
      p_tot_pages       OUT   NUMBER,
      p_result          OUT   resultset
   )
   IS
      v_terminals      VARCHAR2 (32767);
      v_opr_adm_flag   VARCHAR2 (10);
      v_sql_1    VARCHAR2 (4000);
      v_sql_2    VARCHAR2 (4000);
      v_sql_3    VARCHAR2 (4000);
      v_sql_4    VARCHAR2 (4000);
      v_sql_5    VARCHAR2 (4000);
      v_sql_6    VARCHAR2 (4000);
      v_sql_7    VARCHAR2 (4000);
      v_sql_8    VARCHAR2 (4000);
      v_sql_11   VARCHAR2 (4000);
      v_sql_12   VARCHAR2 (4000);
      v_sql_13   VARCHAR2 (4000);
      v_sql_14   VARCHAR2 (4000);--//@@Added by RajKumari for the WPBN issue-143517
      v_sql_15   VARCHAR2 (4000);--//@@Added by RajKumari for the WPBN issue-143517
      v_sql2      VARCHAR2 (4000);
      v_sql3      VARCHAR2 (4000);
      v_sql4      VARCHAR2 (4000);
      v_sql5      VARCHAR2 (4000);
      v_orderby        VARCHAR2 (50);
      v_sql_u      VARCHAR2 (32767);
   BEGIN

      --DBMS_OUTPUT.PUT_LINE('SDSDSD');
        IF p_autoUpdated='UPD'
       THEN
          v_sql_u  :='';
        ELSE
          v_sql_u  :=' and instr(QM.QUOTE_ID,''_'',1,2)=0 and to_date(QM.CREATED_DATE)=to_Date(QM.CREATED_TSTMP)';
       END IF;
      IF (sortBy='SalesPerson') THEN
        v_orderby := ' QM.SALES_PERSON ';
--@@Added by subrahmanyam for 173831
      ELSIF (sortBy='CreatedBy') THEN
        v_orderby := ' QM.CREATED_BY ';  --for 173831
      ELSIF (sortBy='CustomerId') THEN
        v_orderby := ' QM.CUSTOMER_ID ';
     ELSIF (sortBy='CustomerName') THEN  --@@Added by Kameswari for the WPBN issue-30313
       v_orderby := ' COMPANYNAME ';
      ELSIF (sortBy='QuoteDate') THEN
        v_orderby := ' CREATED_DATE ';
      ELSIF (sortBy='QuoteId') THEN
        v_orderby := ' QM.QUOTE_ID ';
      ELSIF (sortBy='QuoteStatus') THEN
        v_orderby := ' QM.QUOTE_STATUS ';
      ELSIF (sortBy='Mode') THEN
        v_orderby := ' SMODE ';
      ELSIF (sortBy='ServiceLevel') THEN
        v_orderby := ' SERVICE ';
      ELSIF (sortBy='FromCountry') THEN
        v_orderby := ' OL.COUNTRYID ';
      ELSIF (sortBy='FromLocation') THEN
        v_orderby := ' QM.ORIGIN_LOCATION ';
      ELSIF (sortBy='ToCountry') THEN
        v_orderby := ' DL.COUNTRYID ';
      ELSIF (sortBy='ToLocation') THEN
        v_orderby := ' QM.DEST_LOCATION ';
      ELSIF (sortBy='AverageYield') THEN
        v_orderby := ' AVG_YIELD ';
      ELSE
        v_orderby := ' QM.QUOTE_ID ';
      END IF;
      v_orderby := ' ORDER BY ' || v_orderby || sortOrder ;

       IF (p_terminalId IS NOT NULL AND LENGTH (TRIM (p_terminalId)) > 0)
       THEN
        v_terminals :=' and QM.TERMINAL_ID in ( '||p_terminalId ||')';
       ELSE
        SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_loginTerminal;

      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals := 'and exists (Select Terminalid From Fs_Fr_Terminalmaster)';
      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_loginTerminal);
        /* v_terminals := 'Select Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')
                           From Dual
                         Union
                         Select Child_Terminal_Id Term_Id
                           From Fs_Fr_Terminal_Regn
                         Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
                          Start With Parent_Terminal_Id =
                                     Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')';*/



      v_terminals :='and exists ( SELECT 1 FROM FETCH_TERMINAL_ID_VIEW_REPORT WHERE TERM_id=QM.TERMINAL_ID )'; --@@Added by Kameswari for the WPBN issue-172224 on 01/06/09
       END IF;
       END IF;
--@@ Commened by subrahmanyam for --173831
      /*v_sql_1 :=
         'select * from (select t1.*, rownum rn from (SELECT QM.TERMINAL_ID,QM.SALES_PERSON, QM.CUSTOMER_ID, QM.QUOTE_ID, QM.QUOTE_STATUS, ';*/
--@@ Added by subrahmanyam for 173831
         v_sql_1 :=
         'select * from (select t1.*, rownum rn from (SELECT QM.TERMINAL_ID,QM.CREATED_BY,QM.SALES_PERSON, QM.CUSTOMER_ID, QM.QUOTE_ID, QM.QUOTE_STATUS, ';
     --173831
      v_sql_1 :=
            v_sql_1
         || ' DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE RP.QUOTE_ID=QM.QUOTE_ID  AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1, QM.SHIPMENT_MODE, 100) SMODE, ';

      v_sql_1 :=
            v_sql_1
         || ' DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''  AND BD.VERSION_NO=QR.VERSION_NO
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
 DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''  AND BD.VERSION_NO=QR.VERSION_NO
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE, ';
     --@@Modified by Kameswari for the WPBN issue -30313
      v_sql_1 :=
            v_sql_1
         || ' QM.ORIGIN_LOCATION, OL.COUNTRYID ORG_COUNTRY, QM.DEST_LOCATION, DL.COUNTRYID DEST_COUNTRY,QM.CREATED_TSTMP CREATED_DATE,qms_report_pkg.yeild_average_fun(qm.QUOTE_ID) AVG_YIELD, QM.ACTIVE_FLAG,C.COMPANYNAME,QM.IS_MULTI_QUOTE, QM.STATUS_REASON ' --@@Modified by (Anil.k for Multiquote, Kishore For StatusReason)
         || ' FROM QMS_QUOTE_MASTER QM, FS_FR_LOCATIONMASTER OL, FS_FR_LOCATIONMASTER DL,FS_FR_CUSTOMERMASTER C ';
      --@@Modified by Kameswari for the WPBN issue-30313
      v_sql4 := ' select count(*) from   (select t1.*, rownum rn from  (SELECT QM.QUOTE_ID ,QM.TERMINAL_ID,C.CUSTOMERID FROM QMS_QUOTE_MASTER QM, FS_FR_LOCATIONMASTER OL, FS_FR_LOCATIONMASTER DL ,FS_FR_CUSTOMERMASTER C';
     --@@Modified by Kameswari for the WPBN issue-30313
      v_sql_11 :=
            ' WHERE QM.ORIGIN_LOCATION = OL.LOCATIONID AND QM.DEST_LOCATION = DL.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID  '
         || ' AND  (QM.CREATED_TSTMP IS NULL ';
      /*v_sql_12 :=
            ' OR ( QM.CREATED_TSTMP >= '
         || ''''
         || p_from_date
         || ''''
         || ' AND QM.CREATED_TSTMP <='
         || ''''
         || p_to_date
         || ''''
         || ' )';*/
      Dbms_Session.set_context('REPORT_CONTEXT','v_from_date',p_from_date);
      Dbms_Session.set_context('REPORT_CONTEXT','v_user_date_format',p_userdateformat||' HH24:MI:SS');
      Dbms_Session.set_context('REPORT_CONTEXT','v_to_date',p_to_date);

      v_sql_12 :=
            ' OR (QM.CREATED_TSTMP BETWEEN TO_DATE (sys_context(''REPORT_CONTEXT'',''v_from_date''),
                  sys_context(''REPORT_CONTEXT'',''v_user_date_format''))
                  AND TO_DATE (sys_context(''REPORT_CONTEXT'',''v_to_date''),
                  sys_context(''REPORT_CONTEXT'',''v_user_date_format''))) ';


     v_sql_13 := ' ) AND (((QM.ACTIVE_FLAG = ''A'' OR QM.ACTIVE_FLAG=''I'') AND QM.QUOTE_STATUS in (''ACC'',''NAC'')) OR
                      (QM.ACTIVE_FLAG=''A'' AND QM.QUOTE_STATUS NOT IN (''ACC'',''NAC'')))
                      AND QM.COMPLETE_FLAG=''C'' ';


      IF (p_emp_id IS NOT NULL AND LENGTH (TRIM (p_emp_id)) > 0)
      THEN
         v_sql_2 := 'AND QM.SALES_PERSON IN (' || p_emp_id || ')';
      ELSE
         v_sql_2 := '';
      END IF;

      IF (p_customer_id IS NOT NULL AND LENGTH (TRIM (p_customer_id)) > 0)
      THEN
         v_sql_3 := 'AND QM.CUSTOMER_ID IN (' || p_customer_id || ')';
      ELSE
         v_sql_3 := '';
      END IF;

      IF (p_origin IS NOT NULL AND LENGTH (TRIM (p_origin)) > 0)
      THEN
         v_sql_4 := 'AND QM.ORIGIN_LOCATION IN (' || p_origin || ')';
      ELSE
         v_sql_4 := '';
      END IF;

      IF (p_destination IS NOT NULL AND LENGTH (TRIM (p_destination)) > 0)
      THEN
         v_sql_5 := 'AND QM.DEST_LOCATION IN (' || p_destination || ')';
      ELSE
         v_sql_5 := '';
      END IF;

       --//@@Added by RajKumari for the WPBN issue-143517
      IF (p_fromCountry IS NOT NULL AND LENGTH (TRIM (p_fromCountry)) > 0)
      THEN
         v_sql_14 := 'AND OL.COUNTRYID IN (' || p_fromCountry || ')';
      ELSE
         v_sql_14 := '';
      END IF;

      IF (p_toCountry IS NOT NULL AND LENGTH (TRIM (p_toCountry)) > 0)
      THEN
         v_sql_15 := 'AND DL.COUNTRYID IN (' || p_toCountry || ')';
      ELSE
         v_sql_15 := '';
      END IF;
      /*IF (LENGTH (TRIM (p_service_level)) > 0)
      THEN
         v_sql_6 := 'AND QM.SERVICE_LEVEL_ID IN (' || p_service_level || ')';
      ELSE
         v_sql_6 := '';
      END IF;*/
      v_sql_6 := '';
      IF ( p_quote_status IS NOT NULL AND LENGTH (TRIM (p_quote_status)) > 0)
      THEN
         v_sql_7 := 'AND QM.QUOTE_STATUS IN (' || p_quote_status || ')';
      ELSE
         v_sql_7 := '';
      END IF;

      /*IF (LENGTH (TRIM (p_shipment_mode)) > 0)
      THEN
         v_sql_8 := 'AND QM.SHIPMENT_MODE = ''' || p_shipment_mode || '''';
      ELSE
         v_sql_8 := '';
      END IF;*/
      v_sql_8 := '';

--@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09
      IF (p_shipment_mode IS NOT NULL)
      THEN
       v_sql2:=' and qm.shipment_mode ='||p_shipment_mode;
        IF (p_service_level IS NOT NULL OR LENGTH(trim(p_service_level)) <> 0)
        THEN
          v_sql2:=v_sql2 ||' and qm.id in ((select qr.quote_id from qms_quote_rates qr ,qms_buyrates_dtl bd where qr.buyrate_id =BD.BUYRATEID AND qr.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO' ||
                             ' AND BD.LINE_NO=QR.LINE_NO AND BD.LINE_NO=''0'' AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'') and bd.SERVICE_LEVEL IN ('||p_service_level||')) union  (select qs.quote_id  from qms_quote_spotrates qs where  QS.LINE_NO=''0''    AND qs.SERVICELEVEL IN ('||p_service_level||')))';

      --        v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'')) ';

        END IF;
        END IF;
   /*   ELSIF (p_shipment_mode='2')
      THEN
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''))';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      ELSIF (p_shipment_mode='4')
      THEN
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4''))';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      ELSE
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:='';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      END IF;
*/
      v_sql3:=' )t1  where   rownum <= (('
           || p_page_no
           || ' - 1) * '
           || p_page_rows
           || ') + '
           || p_page_rows
           || ') '
           || '  where   rn > (('
           || p_page_no
           || ' - 1) * '
           || p_page_rows
           || ')';

      v_sql5:=' )t1 )';

    print_out( v_sql_1
           || v_sql_11
           || v_sql_12
           || v_sql_13
           || v_sql_2
           || v_sql_3
           || v_sql_4
           || v_sql_5
           || v_sql_14--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_15--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_6
           || v_sql_7
           || v_sql_8
           || v_sql2
           ||v_sql_u         --@@Added by Kameswari for the WPBN issue-154390
          -- ||'and qm.terminal_id in ( '  --@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09

           ||v_terminals

           || v_orderby
           || v_sql3);
      OPEN p_result
       FOR    v_sql_1
           || v_sql_11
           || v_sql_12
           || v_sql_13
           || v_sql_2
           || v_sql_3
           || v_sql_4
           || v_sql_5
           || v_sql_14--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_15--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_6
           || v_sql_7
           || v_sql_8
           || v_sql2
           ||v_sql_u         --@@Added by Kameswari for the WPBN issue-154390
          -- ||'and qm.terminal_id in ( '  --@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09

           ||v_terminals

           || v_orderby
           || v_sql3;

   EXECUTE IMMEDIATE(
              v_sql4
           || v_sql_11
           || v_sql_12
           || v_sql_13
           || v_sql_2
           || v_sql_3
           || v_sql_4
           || v_sql_5
           || v_sql_14--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_15--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_6
           || v_sql_7
           || v_sql_8
           || v_sql2
           ||v_sql_u         --@@Added by Kameswari for the WPBN issue-154390
           --||'and qm.terminal_id in ('  --@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09

           ||v_terminals

           || v_sql5) INTO p_tot_rec;


   /*SELECT CEIL ((p_tot_rec / p_page_rows))
        INTO p_tot_pages
        FROM DUAL;*/
    p_tot_pages := CEIL(p_tot_rec/p_page_rows);
-- EXECUTE IMMEDIATE (v_sql_1 || v_sql_11 || v_sql_13 || v_sql_2 || v_sql_3 || v_sql_4 || v_sql_5 || v_sql_6 || v_sql_7 || v_sql_8 ) INTO p_result;
   END;

   PROCEDURE activity_report_excel (
      p_from_date             VARCHAR2,
      p_to_date               VARCHAR2,
      p_userdateformat        VARCHAR2,
      p_shipment_mode         VARCHAR2,
      p_emp_id                VARCHAR2,
      p_customer_id           VARCHAR2,
      p_quote_status          VARCHAR2,
      p_origin                VARCHAR2,
      p_destination           VARCHAR2,
      p_service_level         VARCHAR2,
      p_terminalId            VARCHAR2,
      p_loginTerminal         VARCHAR2,
      p_fromCountry           VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
      p_toCountry             VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
      p_autoUpdated           VARCHAR2, --//@@Added by VenkataLakshmi for the WPBN issue-154390 on 21/02/09
      p_result          OUT   resultset
   )
   IS
      v_terminals      VARCHAR2 (32767);
      v_opr_adm_flag   VARCHAR2 (10);
      v_sql_1    VARCHAR2 (4000);
      v_sql_1_1    VARCHAR2 (4000);
      v_sql_2    VARCHAR2 (4000);
      v_sql_3    VARCHAR2 (4000);
      v_sql_4    VARCHAR2 (4000);
      v_sql_5    VARCHAR2 (4000);
      v_sql_6    VARCHAR2 (4000);
      v_sql_7    VARCHAR2 (4000);
      v_sql_8    VARCHAR2 (4000);
      v_sql_11   VARCHAR2 (4000);
      v_sql_12   VARCHAR2 (4000);
      v_sql_13   VARCHAR2 (4000);
      v_sql_14   VARCHAR2 (4000);--//@@Added by RajKumari for the WPBN issue-143517
      v_sql_15   VARCHAR2 (4000);--//@@Added by RajKumari for the WPBN issue-143517
      v_sql2     VARCHAR2 (4000);
      V_IDQRY    VARCHAR2 (4000);
      v_sql_ID1   VARCHAR2 (32767);
      v_sql_ID2   VARCHAR2 (4000);
      v_sql3      VARCHAR2 (4000);
      v_sql4      VARCHAR2 (4000);
      v_sql5      VARCHAR2 (4000);
      v_sql_u     VARCHAR2(32767);
      V_ID1        VARCHAR2(100);
      V_ID2        VARCHAR2(100);
      V_Quote_ID1        VARCHAR2(32767);
      V_Quote_ID2         VARCHAR2(32767);
      V_QUOTEID       VARCHAR2(100);
      V_MULTI_QUOTE_CARRIER_ID    VARCHAR2(50);
      V_MULTI_QUOTE_LANE_NO    NUMBER(10);
      V_CUR           RESULTSET;
      I               RESULTSET;
      V_CREATED_BY                       varchar2(25);
      V_SALES_PERSON                	 varchar2(25);
      V_CUSTOMER_ID			varchar2(25);
      V_QUOTE_ID				 varchar2(100);
      V_QUOTE_STATUS	            	 VARCHAR2(3);
      V_SMODE					NUMBER;
      V_SERVICE					varchar2(100);
      V_ORIGIN_LOCATION		 VARCHAR2(3);
      V_ORG_COUNTRY			VARCHAR2(2);
      V_DEST_LOCATION			VARCHAR2(3);
      V_DEST_COUNTRY			 VARCHAR2(2);
      V_CREATED_DATE			 TIMESTAMP;
      V_ACTIVE_FLAG				 VARCHAR2(1);
      V_COMPANYNAME			 VARCHAR2(50);
      V_STATUS_REASON			 VARCHAR2(250);
      V_Modified_Date				     DATE;
      V_Inco_Terms_Id			      VARCHAR2(10);
      V_TERMINAL_ID				      VARCHAR2(20);
      V_COUNTRY_ID					VARCHAR2(30);
      V_CUST_REQUESTED_DATE		DATE;
      V_VALID_TO						DATE;
      V_VERSION_NO					NUMBER(3);
      V_MAXVERSIONNO				NUMBER(3);
      V_CUST_REQUESTED_TIME		VARCHAR2(6);
      V_IS_MULTI_QUOTE				VARCHAR2(1);
      V_CREATED_TMSTMP        DATE;
      V_FINAL_CR_DATE         DATE;


   BEGIN
   EXECUTE IMMEDIATE ('TRUNCATE TABLE temp_activityreport ');
       IF (p_terminalId IS NOT NULL AND LENGTH (TRIM (p_terminalId)) > 0)
       THEN
        v_terminals :=' and QM.TERMINAL_ID in ( '||p_terminalId ||')';
      ELSE
       SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid LIKE p_loginTerminal;

      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         v_terminals := 'and exists ( Select Terminalid From Fs_Fr_Terminalmaster)';

      ELSE
         Dbms_Session.set_context('REPORT_CONTEXT','v_terminal_id',p_loginTerminal);
    /*     v_terminals := 'Select Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')
                           From Dual
                         Union
                         Select Child_Terminal_Id Term_Id
                           From Fs_Fr_Terminal_Regn
                         Connect By Prior Child_Terminal_Id = Parent_Terminal_Id
                          Start With Parent_Terminal_Id =
                                     Sys_Context(''REPORT_CONTEXT'', ''v_terminal_id'')';
*/
    v_terminals :='and exists ( SELECT 1 FROM FETCH_TERMINAL_ID_VIEW_REPORT WHERE TERM_id=QM.TERMINAL_ID )'; --@@Added by Kameswari for the WPBN issue-172224 on 01/06/09

      END IF;
       END IF;
       --Added By kishore podili
       IF p_autoUpdated='UPD'
       THEN
          v_sql_u  :='';
        ELSE
            IF  ( p_quote_status IS NULL )
               THEN
                    v_sql_u  :='';
            ELSE
          v_sql_u  :=' and instr(QM.QUOTE_ID,''_'',1,2)=0 and to_date(QM.CREATED_DATE)=to_Date(QM.CREATED_TSTMP)';
             END IF;
       END IF;
--@@Commented by subrahmanyam for 173831
      /*v_sql_1 :=
         'SELECT QM.SALES_PERSON, QM.CUSTOMER_ID, QM.QUOTE_ID, QM.QUOTE_STATUS, ';*/
--@@Added by subrahmanyam for 173831
        v_sql_1 :=
         'SELECT QM.CREATED_BY,QM.SALES_PERSON, QM.CUSTOMER_ID, QM.QUOTE_ID, QM.QUOTE_STATUS, ';
      --@@ended for 173831
      v_sql_1 :=
            v_sql_1
         || ' DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE RP.QUOTE_ID=QM.QUOTE_ID  AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1, QM.SHIPMENT_MODE, QM.SHIPMENT_MODE) SMODE, '; --Modified by Rakesh on 22-03-2011

      v_sql_1 :=
            v_sql_1
         || ' DECODE((SELECT DISTINCT IS_MULTI_QUOTE FROM QMS_QUOTE_MASTER QMSMQ WHERE QMSMQ.Quote_Id=QM.QUOTE_ID /*AND QMSMQ.ACTIVE_FLAG=''A''*/),''Y'',DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))),''N'',
                     DECODE((SELECT COUNT(*)
                                FROM FS_RT_LEG LG, FS_RT_PLAN RP
                               WHERE RP.QUOTE_ID=QM.QUOTE_ID
                               AND LG.RT_PLAN_ID=RP.RT_PLAN_ID),
                               1,DECODE((SELECT DISTINCT QR.SELL_BUY_FLAG FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=QM.ID AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'',''CSR'',''SBR'')), ''SBR'',(SELECT DISTINCT SERVICELEVEL FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=QM.ID AND SERVICELEVEL NOT IN (''SCH'')),(SELECT DISTINCT SERVICE_LEVEL
                                    FROM QMS_BUYRATES_DTL BD,QMS_QUOTE_RATES QR
                                   WHERE BD.BUYRATEID=QR.BUYRATE_ID
                                   AND BD.LANE_NO=QR.RATE_LANE_NO
                                   AND BD.LINE_NO=''0''
                                   AND QR.QUOTE_ID=QM.ID
                                   AND QR.SELL_BUY_FLAG IN(''BR'',''RSR'',''CSR''))), ''Multi-Modal'')) SERVICE, ';
      --@@Modified by Kameswari for the WPBN issue-30313
      v_sql_1 :=
            v_sql_1
     --    || ' QM.ORIGIN_LOCATION, OL.COUNTRYID ORG_COUNTRY, QM.DEST_LOCATION, DL.COUNTRYID DEST_COUNTRY,QM.CREATED_DATE CREATED_DATE,qms_report_pkg.yeild_average_fun(qm.QUOTE_ID) AVG_YIELD, QM.ACTIVE_FLAG,C.COMPANYNAME,(SELECT SR.STATUS_REASON FROM QMS_STATUS_REASON SR WHERE SR.ID=QM.STATUS_REASON) STATUS_REASON,QM.MODIFIED_DATE Modified_Date,QM.Inco_Terms_Id, QM.TERMINAL_ID,(SELECT ADDRESS.COUNTRYID FROM FS_ADDRESS ADDRESS WHERE ADDRESS.ADDRESSID=(SELECT TMASTER.CONTACTADDRESSID FROM FS_FR_TERMINALMASTER TMASTER WHERE TMASTER.TERMINALID=QM.TERMINAL_ID)) COUNTRY_ID, QM.CUST_REQUESTED_DATE, QM.VALID_TO,QM.VERSION_NO,(SELECT MAX(MASTER.VERSION_NO) FROM QMS_QUOTE_MASTER MASTER WHERE
           || ' QM.ORIGIN_LOCATION, OL.COUNTRYID ORG_COUNTRY, QM.DEST_LOCATION, DL.COUNTRYID DEST_COUNTRY,QM.CREATED_DATE CREATED_DATE ,';
     v_sql_1_1 := ' QM.ACTIVE_FLAG,C.COMPANYNAME,(SELECT SR.STATUS_REASON FROM QMS_STATUS_REASON SR WHERE SR.ID=QM.STATUS_REASON) STATUS_REASON,QM.MODIFIED_DATE Modified_Date,QM.Inco_Terms_Id, QM.TERMINAL_ID,(SELECT ADDRESS.COUNTRYID FROM FS_ADDRESS ADDRESS WHERE ADDRESS.ADDRESSID=(SELECT TMASTER.CONTACTADDRESSID FROM FS_FR_TERMINALMASTER TMASTER WHERE TMASTER.TERMINALID=QM.TERMINAL_ID)) COUNTRY_ID, QM.CUST_REQUESTED_DATE, QM.VALID_TO,QM.VERSION_NO,(SELECT MAX(MASTER.VERSION_NO) FROM QMS_QUOTE_MASTER MASTER WHERE
           MASTER.QUOTE_ID=QM.QUOTE_ID) MAXVERSIONNO,QM.CUST_REQUESTED_TIME,QM.IS_MULTI_QUOTE' -- Added By Kishore Podili  Modified by Rakesh on 22-02-2011,22-03-2011 for Issue:236359
         || ' FROM QMS_QUOTE_MASTER QM, FS_FR_LOCATIONMASTER OL, FS_FR_LOCATIONMASTER DL ,FS_FR_CUSTOMERMASTER C  '; -- --,QMS_STATUS_REASON SR Added By Kishore Podili

     --@@Modified by Kameswari for the WPBN issue-30313
      v_sql4 := 'SELECT QM.QUOTE_ID ,C.CUSTOMERID FROM QMS_QUOTE_MASTER QM, FS_FR_LOCATIONMASTER OL, FS_FR_LOCATIONMASTER DL,FS_FR_CUSTOMERMASTER C ';
      --@@Modified by Kameswari for the WPBN issue-30313
      v_sql_11 :=
            ' WHERE QM.ORIGIN_LOCATION = OL.LOCATIONID AND QM.DEST_LOCATION = DL.LOCATIONID AND C.CUSTOMERID=QM.CUSTOMER_ID'
             --|| 'and (  QM.STATUS_REASON is null or SR.ID = QM.STATUS_REASON )  AND  (QM.CREATED_TSTMP IS NULL '; -- Added By Kishore Podili */
             -- || ' AND (QM.STATUS_REASON IS NULL OR SR.ID = QM.STATUS_REASON)'  -- Added By Kishore Podili
              || ' AND  (QM.CREATED_TSTMP IS NULL ';

      /*v_sql_12 :=
            ' OR ( QM.CREATED_TSTMP BETWEEN '
         || ''''
         || p_from_date
         || ''''
         || ' AND '
         || ''''
         || p_to_date
         || ''''
         || ' )';*/
      v_sql_12 :=
            ' OR (QM.CREATED_TSTMP BETWEEN TO_DATE ('
               || ''''
               || p_from_date
               || ''''
               || ','
               || ''''
               ||  p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ') '
               || ' AND TO_DATE ('
               || ''''
               || p_to_date
               || ''''
               || ','
               || ''''
               || p_userdateformat
               ||' HH24:MI:SS'
               || ''''
               || ')) ';
     /* v_sql_13 := ' ) AND (((QM.ACTIVE_FLAG = ''A'' OR QM.ACTIVE_FLAG=''I'') AND QM.QUOTE_STATUS=''NAC'') OR
                      (QM.ACTIVE_FLAG=''A'' AND QM.QUOTE_STATUS<>''NAC''))
                      AND QM.COMPLETE_FLAG=''C'' ';          Modified by Rakesh
*/      v_sql_13 := ' ) AND (((QM.ACTIVE_FLAG = ''A'' OR QM.ACTIVE_FLAG=''I'') AND QM.QUOTE_STATUS in (''ACC'',''NAC'',''GEN'',''PEN'')) OR
                      (QM.ACTIVE_FLAG=''A'' AND QM.QUOTE_STATUS NOT IN (''ACC'',''NAC'')))
                      AND QM.COMPLETE_FLAG=''C'' ';

      /*   v_sql_13 := ') AND QM.COMPLETE_FLAG=''C'' ';*/
      IF (p_emp_id IS NOT NULL AND LENGTH (TRIM (p_emp_id)) > 0)
      THEN
         v_sql_2 := 'AND QM.SALES_PERSON IN (' || p_emp_id || ')';
      ELSE
         v_sql_2 := '';
      END IF;

      IF (p_customer_id IS NOT NULL AND LENGTH (TRIM (p_customer_id)) > 0)
      THEN
         v_sql_3 := 'AND QM.CUSTOMER_ID IN (' || p_customer_id || ')';
      ELSE
         v_sql_3 := '';
      END IF;

      IF (p_origin IS NOT NULL AND LENGTH (TRIM (p_origin)) > 0)
      THEN
         v_sql_4 := 'AND QM.ORIGIN_LOCATION IN (' || p_origin || ')';
      ELSE
         v_sql_4 := '';
      END IF;

      IF (p_destination IS NOT NULL AND LENGTH (TRIM (p_destination)) > 0)
      THEN
         v_sql_5 := 'AND QM.DEST_LOCATION IN (' || p_destination || ')';
      ELSE
         v_sql_5 := '';
      END IF;

      /*IF (LENGTH (TRIM (p_service_level)) > 0)
      THEN
         v_sql_6 := 'AND QM.SERVICE_LEVEL_ID IN (' || p_service_level || ')';
      ELSE
         v_sql_6 := '';
      END IF;*/
      v_sql_6 := '';
      IF ( p_quote_status IS NOT NULL AND LENGTH (TRIM (p_quote_status)) > 0)
      THEN
         v_sql_7 := 'AND QM.QUOTE_STATUS IN (' || p_quote_status || ')';
      ELSE
          -- Modifed by Kishore Podili on 07-Apr-11
          IF (p_quote_status IS NULL  AND p_autoUpdated IS NULL) THEN
          v_sql_7 := 'AND QM.QUOTE_STATUS IN (''QUE'', ''NAC'', ''ACC'', ''PEN'', ''GEN'')';
          ELSE
         v_sql_7 := '';
             END IF;
      END IF;

       --//@@Added by RajKumari for the WPBN issue-143517
      IF (p_fromCountry IS NOT NULL AND LENGTH (TRIM (p_fromCountry)) > 0)
      THEN
         v_sql_14 := 'AND OL.COUNTRYID IN (' || p_fromCountry || ')';
      ELSE
         v_sql_14 := '';
      END IF;

      IF (p_toCountry IS NOT NULL AND LENGTH (TRIM (p_toCountry)) > 0)
      THEN
         v_sql_15 := 'AND DL.COUNTRYID IN (' || p_toCountry || ')';
      ELSE
         v_sql_15 := '';
      END IF;

      /*IF (LENGTH (TRIM (p_shipment_mode)) > 0)
      THEN
         v_sql_8 := 'AND QM.SHIPMENT_MODE = ''' || p_shipment_mode || '''';
      ELSE
         v_sql_8 := '';
      END IF;*/
      v_sql_8 := '';


    --@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09
      IF (p_shipment_mode IS NOT NULL)
      THEN
       v_sql2:=' and qm.shipment_mode ='||p_shipment_mode;
        IF (p_service_level IS NOT NULL OR LENGTH(trim(p_service_level)) <> 0)
        THEN
          v_sql2:=v_sql2 ||' and qm.id in ((select qr.quote_id from qms_quote_rates qr ,qms_buyrates_dtl bd where qr.buyrate_id =BD.BUYRATEID AND qr.RATE_LANE_NO=BD.LANE_NO AND QR.VERSION_NO=BD.VERSION_NO' ||
                             ' AND BD.LINE_NO=QR.LINE_NO AND BD.LINE_NO=''0'' AND QR.SELL_BUY_FLAG IN (''BR'',''RSR'') and bd.SERVICE_LEVEL IN ('||p_service_level||')) union  (select qs.quote_id  from qms_quote_spotrates qs where  QS.LINE_NO=''0''    AND qs.SERVICELEVEL IN ('||p_service_level||')))';

      --        v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''1'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''1'')) ';

        END IF;
        END IF;

    /*  ELSIF (p_shipment_mode='2')
      THEN
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''))';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''2''and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''2''and qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      ELSIF (p_shipment_mode='4')
      THEN
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'')) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4''))';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bm.SHIPMENT_MODE=''4'' and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SHIPMENT_MODE=''4'' and qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      ELSE
        IF (p_service_level IS NULL OR LENGTH(trim(p_service_level)) = 0)
        THEN
          v_sql2:='';
        ELSE
          v_sql2:=' and qm.id in ((select qr.quote_id from qms_quote_rates qr where (qr.buyrate_id, qr.RATE_LANE_NO) in (select bm.buyrateid,bd.lane_no from qms_buyrates_dtl bd,qms_buyrates_master bm where bd.buyrateid=bm.buyrateid and bd.SERVICE_LEVEL in ('||p_service_level||'))) union (select qs.quote_id from qms_quote_spotrates qs where qs.SERVICELEVEL in ('||p_service_level||')))';
        END IF;
      END IF;

*/      --v_sql3:=')';

      --v_sql5:=' )';

print_out(v_sql_1
                                                       ||v_sql_1_1
                                                       || v_sql_11
                                                       || v_sql_12
                                                       || v_sql_13
                                                       || v_sql_2
                                                       || v_sql_3
                                                       || v_sql_4
                                                       || v_sql_5
                                                       || v_sql_14
                                                       || v_sql_15
                                                       || v_sql_6
                                                       || v_sql_7
                                                       || v_sql_8
                                                       || v_sql2
                                                       ||v_sql_u
                                                       ||v_terminals
                                                       || v_sql3
                                                      -- || 'AND QM.ID IN ('||V_ID1||','||V_ID2||')'
                                                      || V_IDQRY
                                                       ||'ORDER BY QM.QUOTE_ID,QM.VERSION_NO');
         OPEN V_CUR FOR 'SELECT QM.QUOTE_ID,QM.MULTI_QUOTE_CARRIER_ID ,QM.MULTI_QUOTE_LANE_NO
                         FROM QMS_QUOTE_MASTER     QM,
                              FS_FR_LOCATIONMASTER OL,
                              FS_FR_LOCATIONMASTER DL,
                              FS_FR_CUSTOMERMASTER C '
                              || v_sql_11
                              || v_sql_12
                              || v_sql_13
                              || v_sql_2
                              || v_sql_3
                              || v_sql_4
                              || v_sql_5
                              || v_sql_14
                              || v_sql_15
                              || v_sql_6
                              || v_sql_7
                              || v_sql_8
                              || v_sql2
                              || v_sql_u
                              || v_terminals
                              || v_sql3
                              ||'ORDER BY QM.QUOTE_ID,QM.VERSION_NO';
         LOOP
           FETCH   V_CUR INTO V_QUOTEID,V_MULTI_QUOTE_CARRIER_ID ,V_MULTI_QUOTE_LANE_NO;
           EXIT WHEN V_CUR%NOTFOUND;
         IF V_QUOTEID != '0' AND instr(V_QUOTEID, '_', 1, 2) = 0 THEN
      SELECT ID INTO V_ID1 FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID=V_QUOTEID AND QM.VERSION_NO=( SELECT MIN(VERSION_NO) FROM QMS_QUOTE_MASTER QM1 WHERE QM1.QUOTE_ID=V_QUOTEID AND (QM1.MULTI_QUOTE_CARRIER_ID =V_MULTI_QUOTE_CARRIER_ID OR QM1.MULTI_QUOTE_CARRIER_ID IS NULL) AND QM1.MULTI_QUOTE_LANE_NO = V_MULTI_QUOTE_LANE_NO ) AND  (QM.MULTI_QUOTE_CARRIER_ID =V_MULTI_QUOTE_CARRIER_ID  OR QM.MULTI_QUOTE_CARRIER_ID IS NULL) AND QM.MULTI_QUOTE_LANE_NO = V_MULTI_QUOTE_LANE_NO;
      SELECT ID INTO V_ID2 FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID=V_QUOTEID AND QM.VERSION_NO=( SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER QM1 WHERE QM1.QUOTE_ID=V_QUOTEID AND (QM1.MULTI_QUOTE_CARRIER_ID =V_MULTI_QUOTE_CARRIER_ID OR QM1.MULTI_QUOTE_CARRIER_ID IS NULL) AND QM1.MULTI_QUOTE_LANE_NO = V_MULTI_QUOTE_LANE_NO) AND    (QM.MULTI_QUOTE_CARRIER_ID = V_MULTI_QUOTE_CARRIER_ID OR QM.MULTI_QUOTE_CARRIER_ID IS NULL) AND QM.MULTI_QUOTE_LANE_NO = V_MULTI_QUOTE_LANE_NO;

     ELSE
    IF V_QUOTEID != '0' THEN
     SELECT SUBSTR(V_QUOTEID,0,instr(V_QUOTEID, '_', 1, 2)-1 ) INTO V_QUOTEID FROM DUAL ;

     EXECUTE IMMEDIATE ('SELECT MIN(ID),MAX(ID) FROM QMS_QUOTE_MASTER QM1'  || ' WHERE QM1.QUOTE_ID LIKE '||''''||V_QUOTEID||'%'||''''|| 'AND (QM1.MULTI_QUOTE_CARRIER_ID ='||''''||V_MULTI_QUOTE_CARRIER_ID||''''||'OR QM1.MULTI_QUOTE_CARRIER_ID IS NULL)')
    INTO V_ID1,V_ID2;
    END IF;
      END IF;
       IF V_ID1 IS NOT NULL AND V_ID2 IS NOT NULL THEN
     V_IDQRY := 'AND QM.ID IN ('||V_ID1||','||V_ID2||')';
      ELSE
      IF V_ID1 IS NOT NULL AND V_ID2 IS  NULL THEN
      V_IDQRY:='AND QM.ID IN ('||V_ID1||')';
      ELSE
      IF V_ID1 IS NULL AND V_ID2 IS NOT NULL THEN
      V_IDQRY:=  'AND QM.ID IN ('||V_ID2||')';
      END IF;
        END IF;
        END IF;



    OPEN I FOR (v_sql_1
                                                       ||v_sql_1_1
                                                       || v_sql_11
                                                       || v_sql_12
                                                       || v_sql_13
                                                       || v_sql_2
                                                       || v_sql_3
                                                       || v_sql_4
                                                       || v_sql_5
                                                       || v_sql_14
                                                       || v_sql_15
                                                       || v_sql_6
                                                       || v_sql_7
                                                       || v_sql_8
                                                       || v_sql2
                                                       ||v_sql_u
                                                       ||v_terminals
                                                       || v_sql3
                                                      -- || 'AND QM.ID IN ('||V_ID1||','||V_ID2||')'
                                                      || V_IDQRY
                                                       ||'ORDER BY QM.QUOTE_ID,QM.VERSION_NO') ;
                                                       LOOP
     FETCH I INTO V_CREATED_BY,
V_SALES_PERSON,
V_CUSTOMER_ID,
V_QUOTE_ID,
V_QUOTE_STATUS	,
V_SMODE	,
V_SERVICE,
V_ORIGIN_LOCATION,
V_ORG_COUNTRY,
V_DEST_LOCATION,
V_DEST_COUNTRY,
V_CREATED_DATE	,
V_ACTIVE_FLAG,
V_COMPANYNAME,
V_STATUS_REASON,
V_Modified_Date,
V_Inco_Terms_Id,
V_TERMINAL_ID,
V_COUNTRY_ID,
V_CUST_REQUESTED_DATE,
V_VALID_TO,
V_VERSION_NO,
V_MAXVERSIONNO	,
V_CUST_REQUESTED_TIME,
V_IS_MULTI_QUOTE;
 EXIT WHEN I%NOTFOUND;

    INSERT INTO temp_activityreport VALUES (
                            V_CREATED_BY,
V_SALES_PERSON,
V_CUSTOMER_ID,
V_QUOTE_ID,
V_QUOTE_STATUS	,
V_SMODE	,
V_SERVICE,
V_ORIGIN_LOCATION,
V_ORG_COUNTRY,
V_DEST_LOCATION,
V_DEST_COUNTRY,
(SELECT DISTINCT NVL(QMS.CREATED_DATE,QMS.CREATED_TSTMP) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID =V_QUOTE_ID AND QMS.VERSION_NO = V_VERSION_NO ) ,
V_ACTIVE_FLAG,
V_COMPANYNAME,
V_STATUS_REASON,
V_Modified_Date,
V_Inco_Terms_Id,
V_TERMINAL_ID,
V_COUNTRY_ID,
V_CUST_REQUESTED_DATE,
V_VALID_TO,
V_VERSION_NO,
V_MAXVERSIONNO	,
V_CUST_REQUESTED_TIME,
V_IS_MULTI_QUOTE
);

           /*  IF V_ID1 != V_ID2 THEN
             V_Quote_ID1 := V_Quote_ID1||','||V_ID1;
             V_Quote_ID2 := V_Quote_ID2||','||V_ID2;
             END IF;*/

    END LOOP;
CLOSE I;
   END LOOP;
   CLOSE V_CUR;
--print_out('V_Quote_ID1'||V_Quote_ID1);
--print_out('V_Quote_ID2'||V_Quote_ID2);



     /* OPEN p_result
       FOR    v_sql_1
           || v_sql_11
           || v_sql_12
           || v_sql_13
           || v_sql_2
           || v_sql_3
           || v_sql_4
           || v_sql_5
           || v_sql_14--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_15--//@@Added by RajKumari for the WPBN issue-143517
           || v_sql_6
           || v_sql_7
           || v_sql_8
           || v_sql2
           ||v_sql_u         --@@Added by Kameswari for the WPBN issue-154390
             -- ||'and qm.terminal_id in ( '  --@@Commented and Modified by Kameswari for the WPBN issue-172224 on 01/06/09
             ||v_terminals
           --|| v_orderby
           || v_sql3
           || 'AND QM.ID IN ('||V_Quote_ID1||','||V_Quote_ID2||')'
           --@@Modified by kameswari for the WPBN issue-38116
           ||'ORDER BY QM.QUOTE_ID,QM.VERSION_NO';--Modified by Rakesh
*/
       OPEN p_result
       FOR SELECT DISTINCT ACT.* FROM temp_activityreport ACT;



END;

-- EXECUTE IMMEDIATE (v_sql_1 || v_sql_11 || v_sql_13 || v_sql_2 || v_sql_3 || v_sql_4 || v_sql_5 || v_sql_6 || v_sql_7 || v_sql_8 ) INTO p_result;


   FUNCTION yeild_average_fun (p_quoteid VARCHAR2)
      RETURN FLOAT
   AS
      v_yeild               NUMBER (25, 5);
      v_charges_count       NUMBER;
      v_yeild_curs          sys_refcursor;
      v_charges_count_curs  sys_refcursor;
      v_total               NUMBER (25, 5):=0;
      v_noOflegs            NUMBER:=0;
   BEGIN


   OPEN v_yeild_curs FOR
      SELECT SUM
                (CASE
                    WHEN UPPER (qr.margin_discount_flag) = 'M'
                       THEN CASE
                    WHEN UPPER (qr.margin_type) = 'P'
                       THEN (qr.buy_rate * 0.01 * qr.margin)
                    ELSE qr.margin
                 END
                    ELSE CASE
                    WHEN UPPER (qr.discount_type) = 'P'
                       THEN   qr.r_sell_rate
                            - (qr.buy_rate * 0.01 * qr.discount)
                            - qr.buy_rate
                    ELSE qr.r_sell_rate - qr.discount - qr.buy_rate
                 END
                 END
                )
        FROM QMS_QUOTE_MASTER qm,
             QMS_QUOTE_RATES qr,
             FS_RT_PLAN rp,
             FS_RT_LEG rl
       WHERE qm.ID = qr.quote_id
         AND rp.quote_id = qm.quote_id
         AND qr.serial_no = rl.serial_no
         AND rp.rt_plan_id = rl.rt_plan_id
         AND qm.quote_id = p_quoteid
         AND qr.sell_buy_flag IN ('RSR', 'BR', 'CSR', 'SBR')
         AND qr.break_point <> 'MIN'
         AND qm.active_flag = 'A'
         GROUP BY rl.serial_no;

      OPEN v_charges_count_curs FOR
      SELECT   COUNT (*)
          FROM QMS_QUOTE_RATES iqr, QMS_QUOTE_MASTER qm
         WHERE iqr.sell_buy_flag IN ('RSR', 'CSR', 'BR', 'SBR')
           AND iqr.quote_id = qm.ID
           AND qm.quote_id = p_quoteid
           AND qm.active_flag = 'A'
           AND iqr.break_point <> 'MIN'
      GROUP BY iqr.serial_no;

      LOOP
      FETCH v_yeild_curs INTO v_yeild;
      FETCH v_charges_count_curs INTO v_charges_count;
      EXIT WHEN v_yeild_curs%NOTFOUND;

        IF v_charges_count=0
        THEN v_charges_count:=1;
        END IF;

        v_total:=v_total+(v_yeild/v_charges_count);
        v_noOflegs:=v_noOflegs+1;
      END LOOP;

      IF v_noOflegs=0
      THEN v_noOflegs:=1;
      END IF;
      CLOSE v_charges_count_curs;
      CLOSE v_yeild_curs;
      RETURN v_total / v_noOflegs;
   END;

   PROCEDURE yeild_dtls_proc (p_quoteid VARCHAR2, p_rs OUT resultset)
   AS
   BEGIN
      OPEN p_rs
       FOR
          SELECT qm.quote_id, qr.quote_id, qr.buyrate_id, qr.sellrate_id,
                 qr.rate_lane_no, qr.break_point, rl.orig_loc, rl.dest_loc,
                 rl.serial_no, qm.customer_id,c.companyname, rl.shpmnt_mode,
                 DECODE(DECODE(qr.sell_buy_flag,'BR','K','RSR','K','CSR','K','SBR','SBR'),
                 'K',
                 (SELECT DISTINCT service_level
                    FROM QMS_BUYRATES_DTL
                   WHERE buyrateid=qr.buyrate_id
                     AND lane_no=qr.rate_lane_no
                     AND service_level NOT IN ('SCH')),
                 'SBR',
                 (SELECT DISTINCT servicelevel
                    FROM QMS_QUOTE_SPOTRATES
                   WHERE quote_id=qm.id AND servicelevel NOT IN ('SCH'))) servicelevel,
                 DECODE(rl.shpmnt_mode,2,(SELECT countryid FROM FS_FRS_PORTMASTER WHERE portid=rl.orig_loc),(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid=rl.orig_loc)) org_country,
                 DECODE(rl.shpmnt_mode,2,(SELECT countryid FROM FS_FRS_PORTMASTER WHERE portid=rl.dest_loc),(SELECT countryid FROM FS_FR_LOCATIONMASTER WHERE locationid=rl.dest_loc)) dest_country,
                 CASE
                    WHEN UPPER (qr.margin_discount_flag) = 'M'
                       THEN CASE
                    WHEN UPPER (qr.margin_type) = 'P'
                       THEN (qr.buy_rate * 0.01 * qr.margin)
                    ELSE qr.margin
                 END
                    ELSE CASE
                    WHEN UPPER (qr.discount_type) = 'P'
                       THEN   qr.r_sell_rate
                            - (qr.buy_rate * 0.01 * qr.discount)
                            - qr.buy_rate
                    ELSE qr.r_sell_rate - qr.discount - qr.buy_rate
                 END
                 END yeild
            FROM QMS_QUOTE_MASTER qm,
                 QMS_QUOTE_RATES qr,
                 FS_FR_CUSTOMERMASTER c,
                 FS_RT_PLAN rp,
                 FS_RT_LEG rl
           WHERE qm.ID = qr.quote_id
             AND c.customerid=qm.customer_id
             AND rp.quote_id = qm.quote_id
             AND qr.serial_no = rl.serial_no
             AND rp.rt_plan_id = rl.rt_plan_id
             AND qm.quote_id = p_quoteid
             AND qr.sell_buy_flag IN ('RSR', 'BR', 'CSR', 'SBR')
             AND qm.active_flag = 'A' ORDER BY rl.serial_no;
   END;
END qms_report_pkg;

/

/

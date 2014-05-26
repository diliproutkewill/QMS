--------------------------------------------------------
--  DDL for Package Body QMS_SETUPS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_SETUPS" AS

  /*
  *  Using this function for getting the countryId,CountryNames information from
  *  FS_COUNTRYMASTER and passing the in parameters are
  *  p_terminal_id   VARCHAR2;
  *  p_operation     VARCHAR2;
  *  p_country       VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Country(p_Terminal_Id VARCHAR2,
                       p_Operation   VARCHAR2,
                       p_Country     VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER WHERE COUNTRYID  LIKE   ' || '''' || p_Country || '''' || '   AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL  ) ORDER BY COUNTRYNAME';
    /*AND TERMINALID IN ('||STR||')  */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CountryId,CountryName information from
  *  FS_COUNTRYMASTER but which our countries mapped to locations and
  *  passing the in parameters are
  *  p_country VARCHAR2;
  *  p_location VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Country(p_Country VARCHAR2, p_Location VARCHAR2)
    RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF p_Country IS NOT NULL AND p_Country != '' THEN
      Str1 := 'AND CON.COUNTRYID LIKE (' || p_Country || ') ';
    ELSE
      Str1 := NULL;
    END IF;
    OPEN c_1 FOR 'SELECT CON.COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC WHERE LOC.COUNTRYID=CON.COUNTRYID AND LOC.LOCATIONID LIKE   ' || '''' || p_Location || '''' || '' || Str1 || '  AND ( CON.INVALIDATE=' || '''' || 'F' || '''' || ' or CON.INVALIDATE is null ) ORDER BY COUNTRYNAME';
    /*AND TERMINALID IN ('||STR||')*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CountryId,countryName information from
  *  FS_COUNTRYMASTER but which our countries mapped to locations or ports and
  *  passing the in parameters are
  *  p_location VARCHAR2;
  *  p_country VARCHAR2;
  *  p_shipmentMode VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Country_1(p_Location     VARCHAR2,
                         p_Country      VARCHAR2,
                         p_Shipmentmode VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    --SQL1 VARCHAR2(1000):='';
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(1000) := '';
    Str2 VARCHAR2(1000) := '';
  BEGIN
    --STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);
    IF p_Country IS NOT NULL OR Trim(p_Country) <> '' THEN
      Str1 := ' AND CON.COUNTRYID IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_Country) || ') ';

    ELSE
      Str1 := '';
    END IF;
    --DBMS_OUTPUT.put_line ('str1'||qms_rsr_rates_pkg.seperator (p_country));
    IF p_Location IS NOT NULL OR Trim(p_Location) <> '' THEN
      IF p_Shipmentmode = '2' OR UPPER(p_Shipmentmode) = 'SEA' THEN
        Str2 := ' AND  port.portid  IN (' ||
                Qms_Rsr_Rates_Pkg.Seperator(p_Location) || ') ';
      ELSE
        Str2 := ' AND  LOC.LOCATIONID IN (' ||
                Qms_Rsr_Rates_Pkg.Seperator(p_Location) || ') ';
      END IF;
    ELSE
      Str2 := '';
    END IF;
    IF p_Shipmentmode = '2' OR UPPER(p_Shipmentmode) = 'SEA' THEN
      OPEN c_1 FOR 'SELECT DISTINCT CON.COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER CON,fs_frs_portmaster port where port.countryid=con.countryid ' || Str1 || Str2 || '  AND CON.INVALIDATE=' || '''' || 'F' || '''' || ' ORDER BY COUNTRYNAME';
    ELSE
      OPEN c_1 FOR 'SELECT DISTINCT CON.COUNTRYID,COUNTRYNAME FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC WHERE LOC.COUNTRYID=CON.COUNTRYID' || Str1 || Str2 || '  AND CON.INVALIDATE=' || '''' || 'F' || '''' || ' ORDER BY COUNTRYNAME';
    END IF;
    --AND TERMINALID IN ('||STR||')
    RETURN c_1;
    --RETURN STR;
  END;

  /*
  *  Using this function for getting the CommodityId,commodityDescription information from
  *  FS_FR_COMODITYMASTER and passing the in parameters are
  *  p_terminal_id   VARCHAR2;
  *  p_operation     VARCHAR2;
  *  p_commodity     VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Commodity(p_Terminal_Id VARCHAR2,
                         p_Operation   VARCHAR2,
                         p_Commodity   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT COMODITYID,COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER ' || ' WHERE  COMODITYID  LIKE ' || '''' || p_Commodity || '''' || ' AND INVALIDATE=' || '''' || 'F' || '''' || '  ORDER BY COMODITYID';
    /*AND TERMINALID IN ('||STR||')  */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PortId,PortName information from
  *  FS_FRS_PORTMASTER and passing the in parameters are
  *  p_terminal_id   VARCHAR2;
  *  p_portid        VARCHAR2;
  *  p_operation     VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Port(p_Terminal_Id VARCHAR2,
                    p_Portid      VARCHAR2,
                    p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT DISTINCT PORTID,PORTNAME FROM FS_FRS_PORTMASTER  WHERE PORTID  LIKE' || '''' || p_Portid || '''' || ' AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL) ' || ' ORDER BY PORTID';
    /*AND  TERMINALID IN ('||STR||')  */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE
  *  information from FS_FR_SERVICELEVELMASTER based on shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_shipment_mode     VARCHAR2;
  *  p_servicelevelid    VARCHAR2;
  *  p_operation        VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Servicelevel(p_Terminal_Id    VARCHAR2,
                            p_Shipment_Mode  VARCHAR2,
                            p_Servicelevelid VARCHAR2,
                            p_Operation      VARCHAR2) RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(200);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    IF p_Shipment_Mode = '4' THEN
      Str1 := 'AND SHIPMENTMODE IN (4,5,6,7)';
    ELSIF p_Shipment_Mode = '1' THEN
      Str1 := 'AND SHIPMENTMODE IN (1,3,5,7)';
    ELSIF p_Shipment_Mode = '2' THEN
      Str1 := 'AND SHIPMENTMODE IN (2,3,6,7)';
    ELSE
      Str1 := 'AND SHIPMENTMODE IN (1,2,3,4,5,6,7)';
    END IF;
    OPEN c_1 FOR ' SELECT SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID LIKE' || '''' || p_Servicelevelid || '''' || Str1 || '  AND INVALIDATE=' || '''' || 'F' || ''''
    --|| '  AND  TERMINALID IN ('
    -- || str
    --|| ')
    || ' AND SERVICELEVELID NOT IN (''SCH'') ORDER BY SERVICELEVELID ';
    /*  */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE
  *  information from FS_FR_SERVICELEVELMASTER based on shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_shipment_mode     VARCHAR2;
  *  p_servicelevelid    VARCHAR2;
  *  p_operation        VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Servicelevel_Buyrate(p_Terminal_Id    VARCHAR2,
                                    p_Shipment_Mode  VARCHAR2,
                                    p_Servicelevelid VARCHAR2,
                                    p_Operation      VARCHAR2)
    RETURN g_Ref_Cur IS
    c_1    g_Ref_Cur;
    Str    VARCHAR2(1000);
    Str1   VARCHAR2(200);
    v_Temp VARCHAR2(1000) := '';
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    IF p_Shipment_Mode = '4' THEN
      Str1 := ' SHIPMENTMODE IN (4,5,6,7)';
    ELSIF p_Shipment_Mode = '1' THEN
      Str1 := ' SHIPMENTMODE IN (1,3,5,7)';
    ELSIF p_Shipment_Mode = '2' THEN
      Str1 := ' SHIPMENTMODE IN (2,3,6,7)';
    ELSE
      Str1 := ' SHIPMENTMODE IN (1,2,3,4,5,6,7)';
    END IF;
    IF (LENGTH(p_Servicelevelid) > 0) THEN
      /**v_Temp := ' AND SERVICELEVELID in (' || '''' || p_Servicelevelid ||
      ''')';*/ --@@Modified by Kameswari for the internal issue
      v_Temp := ' AND SERVICELEVELID like ' || '''' || p_Servicelevelid || '%' || '''';

    END IF;
    OPEN c_1 FOR ' SELECT SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER WHERE' || Str1 || v_Temp || '  AND INVALIDATE=' || '''' || 'F' || '''' || '  AND SERVICELEVELID NOT IN (''SCH'') ORDER BY SERVICELEVELID ';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE
  *  information from FS_FR_SERVICELEVELMASTER based on shipmentmode,terminals and
  *  passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_shipment_mode     VARCHAR2;
  *  p_servicelevelid    VARCHAR2;
  *  p_operation        VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Servicelevel_Hirarchy(p_Terminal_Id    VARCHAR2,
                                     p_Shipment_Mode  VARCHAR2,
                                     p_Servicelevelid VARCHAR2,
                                     p_Operation      VARCHAR2)
    RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(200);
  BEGIN
    --str := get_string (p_terminal_id, p_operation);
    IF p_Shipment_Mode = '4' THEN
      Str1 := 'AND SHIPMENTMODE IN (4,5,6,7)';
    ELSIF p_Shipment_Mode = '1' THEN
      Str1 := 'AND SHIPMENTMODE IN (1,3,5,7)';
    ELSIF p_Shipment_Mode = '2' THEN
      Str1 := 'AND SHIPMENTMODE IN (2,3,6,7)';
    ELSE
      Str1 := 'AND SHIPMENTMODE IN (1,2,3,4,5,6,7)';
    END IF;
    OPEN c_1 FOR ' SELECT SERVICELEVELID,SERVICELEVELDESC,SHIPMENTMODE FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID LIKE ' || '''' || p_Servicelevelid || '''' || Str1 || '  AND INVALIDATE=' || '''' || 'F' || '''' || ' AND  SERVICELEVELID NOT IN (''SCH'') AND  TERMINALID IN (' || ' select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID=' || '''' || p_Terminal_Id || '''' || '   UNION SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=' || '''' || p_Terminal_Id || '''' || '   union SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID=' || '''' || p_Terminal_Id || '''' || ' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=' || '''' || 'H' || '''' || ') ' || ' ORDER BY SERVICELEVELID ';
    /*  */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the KG_PER_M3 information from QMS_DENSITY_GROUP_CODE and
  *  passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_dgccode           VARCHAR2;
  *  p_invalidate        VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Densitygroupcode(p_Terminal_Id VARCHAR2,
                                p_Dgccode     VARCHAR2,
                                p_Invalidate  VARCHAR2,
                                p_kgm3        VARCHAR2,
                                p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    /*   Open c_1 For ' SELECT KG_PER_M3 FROM QMS_DENSITY_GROUP_CODE  WHERE DGCCODE =' || '''' || p_Dgccode || '''' || '  AND INVALIDATE=' || '''' || p_Invalidate || '''' || '  ORDER BY KG_PER_M3'; AND TERMINALID IN ('||STR||')
    */
    OPEN c_1 FOR ' SELECT KG_PER_M3 FROM QMS_DENSITY_GROUP_CODE  WHERE DGCCODE =' || '''' || p_Dgccode || '''' || ' AND KG_PER_M3 like ' || '''' || p_kgm3 || '%' || '''' || '  AND INVALIDATE=' || '''' || p_Invalidate || '''' || '  ORDER BY KG_PER_M3';

    /*AND TERMINALID IN ('||STR||')*/

    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CONTENTID information from QMS_CONTENTDTLS based on
  *  shipmentmode,terminal and passing the out parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_contentid         VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_shimentmode       VARCHAR2;.
  *  RETURN g_ref_cur;
  */
 FUNCTION Get_Contentdescription(p_Terminal_Id VARCHAR2,
                                  p_Contentid   VARCHAR2,
                                  p_Operation   VARCHAR2,
                                  p_Shimentmode VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    -- DBMS_OUTPUT.put_line ('STR');
    IF UPPER(p_Operation) = 'QUOTE' THEN
      Str := Get_String(p_Terminal_Id, 'VIEW');
      OPEN c_1 FOR 'SELECT CONTENTID FROM  QMS_CONTENTDTLS WHERE SHIPMENTMODE IN (' || p_Shimentmode || ') AND CONTENTID LIKE' || '''' || p_Contentid || '''' || 'AND INVALIDATE=' || '''' || 'F' || '''' || ' AND FLAG=''F'' AND TEMINALID IN (' || Str || ') ORDER BY CONTENTID ';
    ELSE
     --Added made by VLAKSHMI for internal issue #174301
     Str := Get_String(p_Terminal_Id, UPPER(p_Operation));
      OPEN c_1 FOR 'SELECT CONTENTID FROM  QMS_CONTENTDTLS WHERE SHIPMENTMODE IN (' || p_Shimentmode || ') AND CONTENTID LIKE' || '''' || p_Contentid || '''' || 'AND INVALIDATE=' || '''' || 'F' || '''' || ' AND TEMINALID IN ('|| STR ||') ORDER BY CONTENTID '; /*AND TEMINALID IN (('||STR||')*/
    END IF;
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the TAXID,TAXDESC,TRML_ID information from FS_FR_TAXMASTER
  *  and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_taxid             VARCHAR2;
  *  p_operation         VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Taxes(p_Terminal_Id VARCHAR2,
                     p_Taxid       VARCHAR2,
                     p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT TAXID,TAXDESC,TRML_ID FROM FS_FR_TAXMASTER WHERE  TRML_ID=' || '''' || p_Terminal_Id || '''' || 'AND INVALIDATE=' || '''' || 'F' || '''' || ' AND TAXID LIKE P_TAXID AND  TRML_ID IN (' || Str || ')  ORDER BY TAXID';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the LIST_TYPE information from QMS_LISTMASTER
  *  and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_shipmentmode      VARCHAR2;
  *  p_searchstring      VARCHAR2;
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Listmaster(p_Terminal_Id  VARCHAR2,
                          p_Operation    VARCHAR2,
                          p_Shipmentmode VARCHAR2,
                          p_Searchstring VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT LIST_TYPE FROM QMS_LISTMASTER WHERE LIST_TYPE LIKE ' || '''' || p_Searchstring || '''' || ' AND SHIPMENT_MODE=' || '''' || p_Shipmentmode || '''' || 'AND INVALIDATE=' || '''' || 'F' || '''' || ' ORDER BY LIST_TYPE '; /*AND  TERMINALID IN ('||STR||')*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CARRIERID,CARRIERNAME,SHIPMENTMODE information
  *  from FS_FR_CAMASTER based on teminals,shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_carrierid         VARCHAR2;
  *  p_shipment_mode     VARCHAR2;.
  *  and following accesslevel.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Carrier(p_Terminal_Id   VARCHAR2,
                       p_Operation     VARCHAR2,
                       p_Carrierid     VARCHAR2,
                       p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur IS
    CURSOR C2 IS(
      SELECT Parent_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
       START WITH Child_Terminal_Id = p_Terminal_Id
      UNION
      SELECT Terminalid Term_Id
        FROM FS_FR_TERMINALMASTER
       WHERE Oper_Admin_Flag = 'H'
      UNION
      SELECT p_Terminal_Id Term_Id
        FROM Dual
      UNION
      SELECT Child_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
       START WITH Parent_Terminal_Id = p_Terminal_Id);
    CURSOR C3 IS(
      SELECT DISTINCT Terminalid Term_Id FROM FS_FR_TERMINALMASTER);
    c_1            g_Ref_Cur;
    Str            VARCHAR2(1000);
    Str1           VARCHAR2(1000);
    v_Opr_Adm_Flag VARCHAR2(10);
    v_Terminals    VARCHAR2(32767);
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal_Id;
    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      FOR i IN C3 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    ELSE
      FOR i IN C2 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    END IF;
    v_Terminals := SUBSTR(v_Terminals, 1, LENGTH(v_Terminals) - 1);
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    /*IF p_shipment_mode = 'Air'
    THEN
       str1 := 'AND SHIPMENTMODE IN (1,3,5,7)';
    ELSIF p_shipment_mode = 'Truck'
    THEN
       str1 := 'AND SHIPMENTMODE IN (4,5,6,7)';
    ELSIF p_shipment_mode = 'ALL'
    THEN
       str1 := 'AND SHIPMENTMODE IN (1,2,3,4,5,6,7)';
    ELSIF p_shipment_mode = 'Sea'
    THEN
       str1 := 'AND SHIPMENTMODE IN (2,3,6,7)';
    END IF;*/
    OPEN c_1 FOR 'SELECT DISTINCT A.CARRIERID,A.CARRIERNAME,A.SHIPMENTMODE FROM FS_FR_CAMASTER A, FS_ADDRESS B  WHERE A.CARRIERID  LIKE' || '''' || p_Carrierid || '''' || ' AND SHIPMENTMODE IN ' || p_Shipment_Mode || ' AND TERMINALID IN (' || v_Terminals || ')' || '   ORDER BY A.CARRIERID'; /*AND  TERMINALID IN ('||STR||')*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CARRIERID,CARRIERNAME,SHIPMENTMODE information
  *  from FS_FR_CAMASTER based on teminals,shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_carrierid         VARCHAR2;
  *  p_shipment_mode     VARCHAR2;.
  *  and following accesslevel.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Carrier_Buyrate(p_Terminal_Id   VARCHAR2,
                               p_Operation     VARCHAR2,
                               p_Carrierid     VARCHAR2,
                               p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur IS
    CURSOR C2 IS(
      SELECT Parent_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
       START WITH Child_Terminal_Id = p_Terminal_Id
      UNION
      SELECT Terminalid Term_Id
        FROM FS_FR_TERMINALMASTER
       WHERE Oper_Admin_Flag = 'H'
      UNION
      SELECT p_Terminal_Id Term_Id
        FROM Dual
      UNION
      SELECT Child_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
       START WITH Parent_Terminal_Id = p_Terminal_Id);
    CURSOR C3 IS(
      SELECT DISTINCT Terminalid Term_Id FROM FS_FR_TERMINALMASTER);
    c_1            g_Ref_Cur;
    Str            VARCHAR2(1000);
    Str1           VARCHAR2(1000);
    v_Opr_Adm_Flag VARCHAR2(10);
    v_Terminals    VARCHAR2(32767);
    v_Carrier      VARCHAR2(1000) := '';
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal_Id;
    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      FOR i IN C3 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    ELSE
      FOR i IN C2 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    END IF;
    v_Terminals := SUBSTR(v_Terminals, 1, LENGTH(v_Terminals) - 1);
    IF (p_Carrierid IS NOT NULL AND LENGTH(p_Carrierid) > 0) THEN
      --v_Carrier := ' AND A.CARRIERID  IN (' || '''' || p_Carrierid || ''')';
      v_Carrier := ' AND A.CARRIERID  like ' || '''' || p_Carrierid || '%' || '''';

    END IF;
    /*IF (p_operation ='CSR') THEN
      v_carrier := ' AND A.CARRIERID  IN (SELECT DISTINCT CARRIER_ID FROM QMS_BUYRATES_DTL) ';
    END IF;*/
    -- DBMS_OUTPUT.PUT_LINE(v_carrier);
    OPEN c_1 FOR 'SELECT DISTINCT A.CARRIERID,A.CARRIERNAME,A.SHIPMENTMODE FROM FS_FR_CAMASTER A WHERE ' || ' SHIPMENTMODE IN ' || p_Shipment_Mode || v_Carrier || ' AND TERMINALID IN (' || v_Terminals || ')' || '   ORDER BY A.CARRIERID';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CARRIERID,CARRIERNAME,SHIPMENTMODE information
  *  from FS_FR_CAMASTER based on teminals,shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_carrierid         VARCHAR2;
  *  p_shipment_mode     VARCHAR2;.
  *  and following accesslevel.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Carrier_Csr(p_Terminal_Id   VARCHAR2,
                           p_Originloc     VARCHAR2,
                           p_Destloc       VARCHAR2,
                           p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur IS
    CURSOR C2 IS(
      SELECT Parent_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY Child_Terminal_Id = PRIOR Parent_Terminal_Id
       START WITH Child_Terminal_Id = p_Terminal_Id
      UNION
      SELECT Terminalid Term_Id
        FROM FS_FR_TERMINALMASTER
       WHERE Oper_Admin_Flag = 'H'
      UNION
      SELECT p_Terminal_Id Term_Id
        FROM Dual
      UNION
      SELECT Child_Terminal_Id Term_Id
        FROM FS_FR_TERMINAL_REGN
      CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
       START WITH Parent_Terminal_Id = p_Terminal_Id);
    CURSOR C3 IS(
      SELECT DISTINCT Terminalid Term_Id FROM FS_FR_TERMINALMASTER);
    c_1            g_Ref_Cur;
    Str            VARCHAR2(1000);
    Str1           VARCHAR2(1000);
    v_Opr_Adm_Flag VARCHAR2(10);
    v_Terminals    VARCHAR2(32767);
    v_Carrier      VARCHAR2(1000) := '';
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal_Id;
    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      FOR i IN C3 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    ELSE
      FOR i IN C2 LOOP
        v_Terminals := v_Terminals || '''' || i.Term_Id || '''' || ',';
      END LOOP;
    END IF;
    v_Terminals := SUBSTR(v_Terminals, 1, LENGTH(v_Terminals) - 1);
    -- DBMS_OUTPUT.PUT_LINE(v_carrier);
    OPEN c_1 FOR 'SELECT DISTINCT A.CARRIERID,A.CARRIERNAME,A.SHIPMENTMODE FROM FS_FR_CAMASTER A WHERE ' || ' SHIPMENTMODE IN ' || p_Shipment_Mode || ' AND A.CARRIERID  IN (SELECT CARRIER_ID FROM QMS_BUYRATES_DTL WHERE ORIGIN LIKE' || '''' || p_Originloc || '''' || ' AND DESTINATION LIKE' || '''' || p_Destloc || '''' || ') AND TERMINALID IN (' || v_Terminals || ')' || '   ORDER BY A.CARRIERID';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the COMPANYID,COMPANYNAME information
  *  from FS_COMPANYINFO based on AGENTJVIND and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_company_id        VARCHAR2;
  *  p_str               VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Company(p_Terminal_Id VARCHAR2,
                       p_Company_Id  VARCHAR2,
                       p_Str         VARCHAR2,
                       p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(10);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF p_Str = 'JV' THEN
      Str1 := '(' || '''' || 'J' || '''' || ',' || '''' || 'A' || '''' || ')';
    ELSE
      Str1 := '(' || '''' || 'C' || '''' || ')';
    END IF;
    Dbms_Output.Put_Line(Str1);
    OPEN c_1 FOR 'SELECT COMPANYID,COMPANYNAME FROM FS_COMPANYINFO WHERE COMPANYID LIKE' || '''' || p_Company_Id || '''' || 'AND AGENTJVIND IN' || Str1 || '    ORDER BY COMPANYID'; /* AND  TERMINALID IN ('||STR||')*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the INDUSTRY_ID,DESCRIPTION,INVALIDATE information
  *  from QMS_INDUSTRY_REG and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_industry_id       VARCHAR2;
  *  p_str1              VARCHAR2;
  *  p_str2              VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Industryregistration(p_Terminal_Id VARCHAR2,
                                    p_Industry_Id VARCHAR2,
                                    p_Str1        VARCHAR2,
                                    p_Str2        VARCHAR2,
                                    p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(1000);
    Str1 VARCHAR2(20);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    IF (p_Str1 != 'NULL' AND p_Str2 = 'INVALIDATE') THEN
      Str1 := 'INVALIDATE LIKE  ' || '''' || '%' || '''';
    ELSE
      Str1 := 'INVALIDATE LIKE  ' || '''' || 'F' || '''';
    END IF;
    Dbms_Output.Put_Line(Str1);
    OPEN c_1 FOR 'SELECT INDUSTRY_ID,DESCRIPTION,INVALIDATE  FROM  QMS_INDUSTRY_REG  WHERE INDUSTRY_ID LIKE   ' || '''' || p_Industry_Id || ''''
    -- || ' AND (INVALIDATE='
    -- || ''''
    -- || 'F'
    --|| ''''
    --|| ' OR INVALIDATE IS NULL)  '
    || ' ORDER BY INDUSTRY_ID'; /* AND  TERMINALID IN|| str

                             || (*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE information
  *  from fs_frs_portmaster based on shipmentmode and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Location_Port(p_Terminal_Id VARCHAR2,
                             p_Operation   VARCHAR2,
                             p_Location    VARCHAR2,
                             p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    Str1                VARCHAR2(1000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    IF p_Shipmode = '1' OR UPPER(p_Shipmode) = UPPER('AIR') THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = UPPER('SEA') THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;
    OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,SHIPMENTMODE from fs_frs_portmaster where portid like ' || '''' || p_Location || '%' || '''' || ' AND SHIPMENTMODE IN ' || p_Shipment_Mode_Str || '  AND ( invalidate=''F'' OR invalidate IS NULL )';
    --str := get_string (p_terminal_id, p_operation);
    /* AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE or LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from fs_frs_portmaster or FS_FR_LOCATIONMASTER  according to conditions and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Location(p_Terminal_Id VARCHAR2,
                        p_Operation   VARCHAR2,
                        p_Location    VARCHAR2,
                        p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    Str1                VARCHAR2(1000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    IF p_Shipmode = '1' OR UPPER(p_Shipmode) = UPPER('AIR') THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = UPPER('SEA') THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSIF p_Shipmode = '4' OR UPPER(p_Shipmode) = UPPER('TRUCK') THEN
      p_Shipment_Mode_Str := '(''4'',''5'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;
    IF p_Shipmode = '2' THEN
      OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,COUNTRYID SHIPMENTMODE from fs_frs_portmaster where portid like ' || '''' || p_Location || '%' || '''' || '   AND ( invalidate=''F'' OR invalidate IS NULL ) order by LOCATIONID'; --@@Modified by Kameswari for the  issue
    ELSIF (p_Shipmode = '1' OR p_Shipmode = '4') THEN
      OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE L.LOCATIONID LIKE ' || '''' || p_Location || '''' || '  AND SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND   L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) ORDER BY LOCATIONID ';
    ELSE
      OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE L.LOCATIONID LIKE ' || '''' || p_Location || '''' || '    AND   L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL )ORDER BY LOCATIONID ';
    END IF;
    --str := get_string (p_terminal_id, p_operation);
    /* AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE or LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from fs_frs_portmaster or FS_FR_LOCATIONMASTER  according to conditions and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Locationport_Buyrates(p_Terminal_Id VARCHAR2,
                                     p_Operation   VARCHAR2,
                                     p_Location    VARCHAR2,
                                     p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    Str1                VARCHAR2(1000) := '';
    v_Searchstr         VARCHAR2(2000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    IF p_Shipmode = '1' OR UPPER(p_Shipmode) = UPPER('AIR') THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = UPPER('SEA') THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;
    IF LENGTH(p_Location) > 0 THEN
      v_Searchstr := ' AND portid in (' || '''' || p_Location || '''' || ')';
    END IF;
    OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,SHIPMENTMODE from fs_frs_portmaster where ' || ' SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND  ( invalidate=''F'' OR invalidate IS NULL ) ' || v_Searchstr || 'order by LOCATIONID'; --@@Modified by Kameswari for the  issue
    --str := get_string (p_terminal_id, p_operation);
    /* AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE or LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from fs_frs_portmaster or FS_FR_LOCATIONMASTER  according to conditions and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Location_Buyrates(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Location    VARCHAR2,
                                 p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    Str1                VARCHAR2(1000) := '';
    v_Searchstr         VARCHAR2(2000) := '';
    v_terminalQry       VARCHAR2(1000) := '';
    v_access_level      VARCHAR2(1) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    IF UPPER(p_Operation) = 'CARTAGEACCEPT' THEN
      SELECT Oper_Admin_Flag
        INTO v_Access_Level
        FROM FS_FR_TERMINALMASTER
       WHERE Terminalid = p_Terminal_Id;

      IF v_access_level <> 'H' THEN
        v_terminalQry := ' AND TL.TERMINALID IN (  SELECT ' || '''' ||
                         p_Terminal_Id || '''' ||
                         ' TERM_ID  FROM DUAL  UNION  SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN  CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID  START WITH PARENT_TERMINAL_ID = ' || '''' ||
                         p_Terminal_Id || '''' || ' )';
                         DBMS_OUTPUT.PUT_LINE(v_terminalQry);
      END IF;

    END IF;

    IF p_Shipmode = '1' OR UPPER(p_Shipmode) = 'AIR' THEN
      p_Shipment_Mode_Str := '(1,3,5,7)';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = 'SEA' THEN
      p_Shipment_Mode_Str := '(2,3,6,7)';
    ELSIF p_Shipmode = '4' OR UPPER(p_Shipmode) = 'TRUCK' THEN
      p_Shipment_Mode_Str := '(4,5,6,7)';
    ELSE
      p_Shipment_Mode_Str := '(1,2,3,4,5,6,7)';
    END IF;
    IF LENGTH(p_Location) > 0 THEN
      IF p_Shipmode = '2' THEN
      IF LENGTH(p_Location) > 5 THEN
      v_Searchstr := ' AND portid in (' || '''' || p_Location || '''' || ')';--@@Modified by Kameswari for the internal issue
      ELSE
        v_Searchstr := ' AND portid like ' || '''' || p_Location || '%' || '''';
     END IF;
      ELSE
       IF LENGTH(p_Location) > 3 THEN
        v_Searchstr := ' AND L.LOCATIONID  in (' || '''' || p_Location || '''' || ')';--@@Modified by Kameswari for the internal issue
       ELSE
        v_Searchstr := ' AND L.LOCATIONID  like ' || '''' || p_Location || '%' || '''';
      END IF;
      END IF;
    END IF;
    IF p_Shipmode = '2' THEN
      OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,COUNTRYID SHIPMENTMODE from fs_frs_portmaster where ( invalidate=''F'' OR invalidate IS NULL ) ' || v_Searchstr || 'order by LOCATIONID'; --@@Modified by Kameswari for the  issue
    ELSIF (p_Shipmode = '1' OR p_Shipmode = '4') THEN
      OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND   L.LOCATIONID=TL.LOCATIONID ' || v_Searchstr || ' AND ( INVALIDATE=''F'' OR INVALIDATE IS NULL ) ' || v_terminalQry || ' ORDER BY LOCATIONID ';
    ELSE

      IF UPPER(p_Operation) = 'CANADA' THEN
        OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND   L.LOCATIONID=TL.LOCATIONID ' || v_Searchstr || ' AND L.COUNTRYID=''CA'' AND ( INVALIDATE=''F'' OR INVALIDATE IS NULL ) ' || v_terminalQry || ' ORDER BY LOCATIONID ';
      ELSE
        OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE  ' || ' SHIPMENTMODE IN ' || p_Shipment_Mode_Str || v_Searchstr || '    AND   L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) ' || v_terminalQry || ' ORDER BY LOCATIONID ';
      END IF;
    END IF;
    --str := get_string (p_terminal_id, p_operation);
    /* AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from FS_FR_LOCATIONMASTER based on countryId and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_countryid         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Locationidsforcountry(p_Terminal_Id VARCHAR2,
                                     p_Operation   VARCHAR2,
                                     p_Location    VARCHAR2,
                                     p_Countryid   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    OPEN c_1
    /*  FOR    'SELECT LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM  FS_FR_LOCATIONMASTER WHERE COUNTRYID LIKE '

                                                 || ''''

                                                 || p_countryid

                                                 || ''''

                                                 || ' AND LOCATIONID LIKE '

                                                 || ''''

                                                 || p_location

                                                 || ''''

                                                 || ' AND TERMINALID IN ('

                                                 || str

                                                 || ')  ORDER BY LOCATIONID ';

                                                                           /* */
    FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE COUNTRYID LIKE ' || '''' || p_Countryid || '''' || ' AND L.LOCATIONID LIKE ' || '''' || p_Location || '''' || ' AND L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) ORDER BY LOCATIONID ';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE or LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from fs_frs_portmaster or FS_FR_LOCATIONMASTER  according to conditions and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_countryid         VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Locids(p_Terminal_Id VARCHAR2,
                      p_Operation   VARCHAR2,
                      p_Location    VARCHAR2,
                      p_Countryid   VARCHAR2,
                      p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    v_Locstr            VARCHAR2(5000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    --str := get_string (p_terminal_id, p_operation);
    /*  OPEN c_1

    FOR    'SELECT DISTINCT LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON WHERE LOC.COUNTRYID=CON.COUNTRYID AND CON.COUNTRYID IN '

          || ''''

          || p_countryid

          || ''''

          || ' AND TERMINALID IN ('

          || str

          || ') ORDER BY LOCATIONID ';







    FOR    'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE L.LOCATIONID LIKE '

        || ''''

        || p_location

        || ''''

        || ' AND L.LOCATIONID=TL.LOCATIONID  AND INVALIDATE='

        || ''''

        || 'F'

        || ''''

        || ' ORDER BY LOCATIONID ';*/
    IF p_Shipmode = '1' OR UPPER(p_Shipmode) = UPPER('AIR') THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = UPPER('SEA') THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSIF p_Shipmode = '4' OR UPPER(p_Shipmode) = UPPER('TRUCK') THEN
      p_Shipment_Mode_Str := '(''4'',''5'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;
    IF p_Shipmode = '2' THEN
      IF LENGTH(Trim(p_Location)) > 0 THEN
        v_Locstr := ' AND portid in ( ' || '''' || p_Location || '''' || ' )';
      END IF;
      OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,SHIPMENTMODE from fs_frs_portmaster where ' || ' COUNTRYID IN (' || '''' || p_Countryid || '''' || ') ' || v_Locstr || ' AND (invalidate=''F'' OR invalidate IS NULL)'; --AND SHIPMENTMODE IN ' || p_Shipment_Mode_Str || //Commented by Yuvraj


    ELSIF (p_Shipmode = '1' OR p_Shipmode = '4') THEN
      IF LENGTH(Trim(p_Location)) > 0 THEN
        v_Locstr := ' AND LOC.LOCATIONID in ( ' || '''' || p_Location || '''' || ' )';
      END IF;
      OPEN c_1 FOR 'SELECT DISTINCT LOC.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON ,FS_FR_TERMINALLOCATION TL  WHERE LOC.COUNTRYID=CON.COUNTRYID AND CON.COUNTRYID IN (' || '''' || p_Countryid || '''' || ' )' || v_Locstr || ' AND SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND   LOC.LOCATIONID=TL.LOCATIONID AND ( LOC.INVALIDATE=' || '''' || 'F' || '''' || ' OR  LOC.INVALIDATE IS NULL )ORDER BY LOC.LOCATIONID ';
    ELSE
      IF p_Location IS NOT NULL THEN
        v_Locstr := ' AND LOC.LOCATIONID in ( ' || '''' || p_Location || '''' ||
                    ' )AND portid LOCATIONID in ( ' || '''' || p_Location || '''' || ' )';
      END IF;

      OPEN c_1 FOR 'SELECT DISTINCT LOC.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON ,FS_FR_TERMINALLOCATION TL  WHERE LOC.COUNTRYID=CON.COUNTRYID AND CON.COUNTRYID IN (' || '''' || p_Countryid || '''' || ')' || v_Locstr || ' AND LOC.LOCATIONID=TL.LOCATIONID AND ( LOC.INVALIDATE=' || '''' || 'F' || '''' || ' OR LOC.INVALIDATE IS NULL) ORDER BY LOCATIONID ';
    END IF;
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID ,PORTNAME ,SHIPMENTMODE or LOCATIONID,LOCATIONNAME,SHIPMENTMODE information
  *  from fs_frs_portmaster or FS_FR_LOCATIONMASTER  according to conditions and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_location          VARCHAR2;
  *  p_shipmode          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Locidswithportids(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Location    VARCHAR2,
                                 p_Shipmode    VARCHAR2) RETURN g_Ref_Cur IS
    c_1                 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    Str1                VARCHAR2(1000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
    IF p_Shipmode = '1' THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSIF p_Shipmode = '4' THEN
      p_Shipment_Mode_Str := '(''4'',''5'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;
    IF p_Shipmode = '2' THEN
      OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,'' '' SHIPMENTMODE,'' '' CITY from fs_frs_portmaster where portid like ' || '''' || p_Location || '%' || '''' || '   AND (invalidate=''F''';
    ELSIF (p_Shipmode = '1' OR p_Shipmode = '4') THEN
      OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE L.LOCATIONID LIKE ' || '''' || p_Location || '''' || '  AND SHIPMENTMODE IN ' || p_Shipment_Mode_Str || ' AND   L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) ORDER BY LOCATIONID ';
    ELSE
      OPEN c_1 FOR 'SELECT DISTINCT L.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM  FS_FR_LOCATIONMASTER  L ,FS_FR_TERMINALLOCATION TL WHERE L.LOCATIONID LIKE ' || '''' || p_Location || '''' || '    AND   L.LOCATIONID=TL.LOCATIONID AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) ORDER BY LOCATIONID ';
    END IF;
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the DESIGNATION_ID,DESCRIPTION,LEVEL_NO information
  *  from QMS_DESIGNATION and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_designation_id    VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Designation(p_Terminal_Id    VARCHAR2,
                           p_Designation_Id VARCHAR2,
                           p_Operation      VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT DESIGNATION_ID,DESCRIPTION,LEVEL_NO FROM QMS_DESIGNATION  WHERE DESIGNATION_ID LIKE ' || '''' || p_Designation_Id || '''' || '   AND INVALIDATE=' || '''' || 'F' || '''' || '  ORDER BY DESIGNATION_ID ';
    /*AND  TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CUSTOMERID,COMPANYNAME  information
  *  from FS_FR_CUSTOMERMASTER based on registered,terminalid,customertype and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_customerid        VARCHAR2;
  *  p_registered        VARCHAR2;
  *  p_registrationlevel VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Customer(p_Terminal_Id       VARCHAR2,
                        p_Operation         VARCHAR2,
                        p_Customerid        VARCHAR2,
                        p_Registered        VARCHAR2,
                        p_Registrationlevel VARCHAR2) RETURN g_Ref_Cur IS
    c_1                  g_Ref_Cur;
    Str                  VARCHAR2(1000);
    Sqlregistrationlevel VARCHAR2(1000) := '';
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF UPPER(p_Registrationlevel) = 'T' THEN
      IF UPPER(p_Registered) = 'R' OR UPPER(p_Registered) = 'U' THEN
        Sqlregistrationlevel := ' AND CUSTOMERTYPE=''Customer'' AND TERMINALID=' || '''' ||
                                p_Terminal_Id || ''' AND REGISTERED=' || '''' ||
                                p_Registered || '''';
      END IF;
    ELSIF UPPER(p_Registrationlevel) = 'C' THEN
      IF UPPER(p_Operation) != 'VIEW' THEN
        Sqlregistrationlevel := ' AND CUSTOMERTYPE=''Corporate'' AND TERMINALID=' || '''' ||
                                p_Terminal_Id || '''';
      ELSE
        Sqlregistrationlevel := ' AND CUSTOMERTYPE=''Corporate''';
      END IF;
    END IF;
    OPEN c_1 FOR 'SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE CUSTOMERID LIKE ' || '''' || p_Customerid || '''' || ' AND A.CUSTOMERADDRESSID=B.ADDRESSID    AND A.REGISTERED_TERMINALID IS NULL ' || Sqlregistrationlevel;
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CUSTOMERID,COMPANYNAME  information
  *  from FS_FR_CUSTOMERMASTER based on flag and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_customer_type     VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_customerid        VARCHAR2;
  *  p_flag              VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Customer_1(p_Terminal_Id   VARCHAR2,
                          p_Customer_Type VARCHAR2,
                          p_Operation     VARCHAR2,
                          p_Customerid    VARCHAR2,
                          p_Flag          VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF p_Flag = 'TRUE' THEN
      OPEN c_1 FOR 'SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE CUSTOMERID LIKE ' || '''' || p_Customerid || '''' || ' AND A.CUSTOMERADDRESSID=B.ADDRESSID  AND  TERMINALID =' || '''' || p_Terminal_Id || '''' || ' AND CUSTOMERTYPE=' || '''' || p_Customer_Type || '''' || '  AND A.REGISTERED_TERMINALID IS NULL   ';
      RETURN c_1;
    ELSE
      OPEN c_1 FOR 'SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE CUSTOMERID LIKE ' || '''' || p_Customerid || '''' || ' AND A.CUSTOMERADDRESSID=B.ADDRESSID    AND A.REGISTERED_TERMINALID IS NULL   ';
      RETURN c_1;
    END IF;
  END;

  FUNCTION Get_Customer_2(p_Customerid VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    -- X VARCHAR2(1000):='';
  BEGIN
    OPEN c_1 FOR 'SELECT DISTINCT A.CUSTOMERID,COMPANYNAME FROM FS_FR_CUSTOMERMASTER A, FS_ADDRESS B WHERE  A.CUSTOMERADDRESSID=B.ADDRESSID  AND '
    --CUSTOMERID='
    --|| ''''
    || Trim(p_Customerid);
    --|| '''';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CODEIDNAME  information
  *  from FS_FR_CONFIGPARAM and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_codeidname        VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Codecustomization(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Codeidname  VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT CODEIDNAME NEWID FROM  FS_FR_CONFIGPARAM  WHERE  CODEIDNAME LIKE ' || '''' || p_Codeidname || ''''; /*AND TERMINALID IN ('||STR||') ORDER BY SUBSTR(CODEIDNAME,4,LENGTH(CODEIDNAME)-5)*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the TERMINALID  information
  *  from FS_FR_TERMINALMASTER based on SHIPMENTMODE,companyid and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_companyid         VARCHAR2;
  *  p_shipmentmode      VARCHAR2;
  *  p_searchstring      VARCHAR2;
  *  p_terminaltype      VARCHAR2;.
  *  RETURN g_ref_cur;
  */
   FUNCTION Get_Terminal_1(p_Terminal_Id  VARCHAR2,
                          p_Companyid    VARCHAR2,
                          p_Shipmentmode VARCHAR2,
                          p_Searchstring VARCHAR2,
                          p_Terminaltype VARCHAR2) RETURN g_Ref_Cur IS
    c_1             g_Ref_Cur;
    Sql1            VARCHAR2(4000);
    Str1            VARCHAR2(1000);
    Sqlcompany      VARCHAR2(1000);
    Sqlterminal     VARCHAR2(1000);
    Sqlsearchstring VARCHAR2(1000);
    v_Searchstring  VARCHAR2(20) := p_Searchstring || '%';
    v_Companyid     VARCHAR2(20) := p_Companyid || '%';
    v_Terminalid    VARCHAR2(20) := p_Terminal_Id || '%';
    v_TerminalType  VARCHAR2(2)  := p_Terminaltype;
  BEGIN
    IF p_Shipmentmode = '1' THEN
      Str1 := 'AND SHIPMENTMODE IN (1,3,5,7)';
    ELSIF p_Shipmentmode = '4' THEN
      Str1 := 'AND SHIPMENTMODE IN (4,5,6,7)';
    ELSIF p_Shipmentmode = 'ALL' THEN
      Str1 := 'AND SHIPMENTMODE IN (1,2,3,4,5,6,7)';
    ELSIF p_Shipmentmode = '2' THEN
      Str1 := 'AND SHIPMENTMODE IN (2,3,6,7)';
    ELSE
      Str1 := '';
    END IF;
    IF UPPER(Trim(p_Companyid)) <> '' AND p_Companyid IS NOT NULL THEN
      Sqlcompany := 'AND TERMINALID LIKE ' || '''' || v_Companyid || '''';
    ELSE
      Sqlcompany := '';
    END IF;
   -- dbms_output.put_line(Upper(Trim(p_Terminal_Id)));
   --chnges made by VLAKSHMI for internal issue #173655
    IF UPPER(Trim(p_Terminal_Id)) IS NULL OR UPPER(Trim(v_TerminalType))='H'
       OR (UPPER(Trim(v_TerminalType))='A' AND UPPER(Trim(p_Terminal_Id))='DHLCORP') THEN
      Sqlterminal := '';
    ELSIF UPPER(Trim(v_TerminalType))='A' THEN
     Sqlterminal := 'AND TERMINALID=  ' || '''' || p_Terminal_Id || '''';
       -- end
    END IF;
   IF  UPPER(Trim(p_Terminal_Id)) IS NULL THEN --chnges made by VLAKSHMI for internal issue #173655
    IF UPPER(Trim(p_Searchstring)) = '' THEN
      Sqlsearchstring := '';
    ELSE
      Sqlsearchstring := 'AND TERMINALID LIKE ' || '''' || v_Searchstring || '''';
    END IF;
     END IF;
   OPEN c_1 FOR 'SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=' || '''' || p_Terminaltype || '''' || Str1 || Sqlsearchstring
    --uncommented by VLAKSHMI for internal issue #173655
 || sqlterminal  || Sqlcompany || '  AND (INVALIDATE=''F'' OR INVALIDATE IS NULL ) ORDER BY TERMINALID';

    RETURN c_1;
    --RETURN SQL1;
  END;

  /*
  *  Using this function for getting the TERMINALID  information
  *  from FS_FR_TERMINALLOCATION based on LOCATIONID and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_location         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Terminal_2(p_Terminal_Id VARCHAR2, p_Location VARCHAR2)
    RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
  BEGIN
    OPEN c_1 FOR 'SELECT TERMINALID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID =' || '''' || p_Location || '''' || ' AND TERMINALID LIKE  ' || '''' || p_Terminal_Id || '''' || ' INVALIDATE=''F'' ORDER BY TERMINALID'; /*AND INVALIDATE='||''''||'F'||''''||'*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CURRENCYID  information
  *  from FS_COUNTRYMASTER and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_currency_id       VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Currency_1(p_Terminal_Id VARCHAR2,
                          p_Currency_Id VARCHAR2,
                          p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT DISTINCT      CURRENCYID  FROM FS_COUNTRYMASTER WHERE CURRENCYID LIKE' || '''' || p_Currency_Id || '''' || '  ORDER BY CURRENCYID'; /*AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CURRENCYID,COUNTRYNAME information
  *  from FS_COUNTRYMASTER and passing the in parameters are
  *  p_currency_id       VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Currencyids(p_Currency_Id VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT CURRENCYID,COUNTRYNAME FROM FS_COUNTRYMASTER WHERE CURRENCYID IN (SELECT CURRENCY1 FROM FS_FR_CURRENCYMASTER) AND CURRENCYID LIKE ' || '''' || p_Currency_Id || '''' || '   ORDER BY CURRENCYID'; /*AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CURRENCYID,COUNTRYNAME information
  *  from FS_COUNTRYMASTER and passing the out parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_currency_id       VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Currency_2(p_Terminal_Id VARCHAR2,
                          p_Currency_Id VARCHAR2,
                          p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT DISTINCT      CURRENCYID,COUNTRYNAME  FROM FS_COUNTRYMASTER WHERE CURRENCYID LIKE' || '''' || p_Currency_Id || '''' || '  ORDER BY CURRENCYID'; /*AND TERMINALID IN ('||STR||') */
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the CURRENCYID information
  *  from FS_COUNTRYMASTER which ouer currencies mapped to countries and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_currency_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_radio             VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Selectedcurrency(p_Terminal_Id VARCHAR2,
                                p_Currency_Id VARCHAR2,
                                p_Operation   VARCHAR2,
                                p_Radio       VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF UPPER(Trim(p_Radio)) = 'I' THEN
      OPEN c_1 FOR 'SELECT DISTINCT CURRENCYID FROM FS_COUNTRYMASTER WHERE CURRENCYID NOT IN (' || 'SELECT CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1=' || '''' || p_Currency_Id || '''' || 'AND IEFLAG=' || '''' || 'I' || '''' || ')' || '  ORDER BY CURRENCYID'; /*AND TERMINALID IN ('||STR||') */
      RETURN c_1;
    ELSIF UPPER(Trim(p_Radio)) = 'E' THEN
      OPEN c_1 FOR 'SELECT DISTINCT CM.CURRENCYID FROM FS_COUNTRYMASTER CM,FS_FR_CURRENCYMASTER C  WHERE    CM.CURRENCYID NOT IN (' || 'SELECT CURRENCY2  FROM FS_FR_CURRENCYMASTER WHERE     CURRENCY1=' || '''' || p_Currency_Id || '''' || 'AND IEFLAG=' || '''' || 'E' || '''' || ')' || '   ORDER BY CURRENCYID'; /*AND TERMINALID IN ('||STR||')*/
      RETURN c_1;
    END IF;
  END;

  /*
  *  Using this function for getting the CURRENCY2 information
  *  from FS_COUNTRYMASTER based on IEFLAG and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_currency_id       VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_radio             VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Modifiedcurrency(p_Terminal_Id VARCHAR2,
                                p_Currency_Id VARCHAR2,
                                p_Operation   VARCHAR2,
                                p_Radio       VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    IF UPPER(Trim(p_Radio)) = 'I' THEN
      OPEN c_1 FOR 'SELECT DISTINCT   CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1=' || '''' || p_Currency_Id || '''' || ' AND IEFLAG=' || '''' || 'I' || '''' || '  ORDER BY CURRENCY2'; /*AND TERMINALID IN ('||STR||') */
      RETURN c_1;
    ELSIF UPPER(Trim(p_Radio)) = 'E' THEN
      OPEN c_1 FOR 'SELECT DISTINCT CURRENCY2 FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1=' || '''' || p_Currency_Id || '''' || ' AND IEFLAG=' || '''' || 'E' || '''' || '  ORDER BY CURRENCY2'; /*AND TERMINALID IN ('||STR||') */
      RETURN c_1;
    END IF;
  END;

  /*
  *  Using this function for getting the CURRENCY2,COUNTRYNAME information
  *  from FS_COUNTRYMASTER and passing the in parameters are
  *  p_terminal_id       VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Currentlist(p_Terminal_Id VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    OPEN c_1 FOR 'SELECT DISTINCT   C.CURRENCY2,CM.COUNTRYNAME FROM     FS_FR_CURRENCYMASTER C,      FS_COUNTRYMASTER  CM WHERE C.CURRENCY2=CM.CURRENCYID ORDER BY     CURRENCY2';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the LEVELNO information
  *  from QMS_MARGIN_LIMIT_DTL and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_level_no          VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_marginid          VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Marginlimitmaster_1(p_Terminal_Id VARCHAR2,
                                   p_Level_No    VARCHAR2,
                                   p_Operation   VARCHAR2,
                                   p_Marginid    VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
    v_opr_adm_flag VARCHAR2(2);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    --@@ Commented by subrahmanyam for the issue id:203354
    -- Open c_1 For 'SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO LIKE  ' || '''' || p_Level_No || '''' || ' AND MARGIN_ID IN (' || p_Marginid || ') AND INVALIDATE=' || '''' || 'F' || '''' || ''; /*AND TERMINALID IN ('||STR||')*/
    IF(p_Operation <>'Invalidate' ) THEN
         OPEN c_1 FOR 'SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO LIKE  ' || '''' || p_Level_No || '''' || ' AND MARGIN_ID IN (' || p_Marginid || ') AND INVALIDATE=' || '''' || 'F' || '''' || ''; /*AND TERMINALID IN ('||STR||')*/
    ELSE
          SELECT OPER_ADMIN_FLAG INTO V_OPR_ADM_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID=p_Terminal_Id;

        IF  V_OPR_ADM_FLAG='A'  THEN

                 OPEN c_1 FOR 'SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO LIKE  ' || '''' || p_Level_No || '''' || ' AND MARGIN_ID IN (' || p_Marginid || ') AND INVALIDATE=' || '''' || 'F' || '''' || ''|| ' AND TERMINALID IN (SELECT CHILD_TERMINAL_ID '
                              || ' FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='||''''||p_Terminal_Id||''''||' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='||''''||p_Terminal_Id||''''||')';

        ELSIF V_OPR_ADM_FLAG='O' THEN
             OPEN c_1 FOR 'SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO LIKE  ' || '''' || p_Level_No || '''' || ' AND MARGIN_ID IN (' || p_Marginid || ') AND INVALIDATE=' || '''' || 'F' || '''' || ''|| ' AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='||''''||p_Terminal_Id||''''||')';

        ELSE
                 OPEN c_1 FOR 'SELECT DISTINCT LEVELNO FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO LIKE  ' || '''' || p_Level_No || '''' || ' AND MARGIN_ID IN (' || p_Marginid || ') AND INVALIDATE=' || '''' || 'F' || '''' || ''; /*AND TERMINALID IN ('||STR||')*/

        END IF;
    END IF;
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the LEVEL_NO information
  *  from QMS_DESIGNATION and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_level_no          VARCHAR2;
  *  p_operation         VARCHAR2;
  *  p_levelno           VARCHAR2;.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Marginlimitmaster_2(p_Terminal_Id VARCHAR2,
                                   p_Level_No    VARCHAR2,
                                   p_Operation   VARCHAR2,
                                   p_Levelno     VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    /*STR:=GET_STRING(P_TERMINAL_ID,P_OPERATION);*/
    OPEN c_1 FOR 'SELECT DISTINCT LEVEL_NO LEVELNO FROM QMS_DESIGNATION WHERE LEVEL_NO  LIKE  ' || '''' || p_Levelno || '''' || ' AND ( INVALIDATE =' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL ) '; /*AND TERMINALID IN ('||STR||')*/
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the SALESPERSON_CODE information
  *  from QMS_SALESPERSON_REG and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_salesperson_code  VARCHAR2;
  *  p_operation         VARCHAR2;
  *  and following accesslevel.
  *  RETURN g_ref_cur;
  */
  FUNCTION Get_Salesperson(p_Terminal_Id      VARCHAR2,
                           p_Salesperson_Code VARCHAR2,
                           p_Operation        VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(1000);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    OPEN c_1 FOR 'SELECT SALESPERSON_CODE  FROM QMS_SALESPERSON_REG  WHERE  SALESPERSON_CODE LIKE' || '''' || p_Salesperson_Code || '''' || ' AND INVALIDATE=' || '''' || 'F' || '''' || ' AND TERMINAL_ID IN (' || Str || ')';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the TERMINALID information
  *  from FS_FR_TERMINALMASTER based on accesslevel and passing the in parameters are
  *  p_terminal_id       VARCHAR2;
  *  p_operation         VARCHAR2;.
  *  RETURN VARCHAR2;
  */
  FUNCTION Get_String(p_Terminal_Id VARCHAR2, p_Operation VARCHAR2)
    RETURN VARCHAR2 IS
    Str            VARCHAR2(1000);
    v_Opr_Adm_Flag VARCHAR(100);
  BEGIN
    SELECT Oper_Admin_Flag
      INTO v_Opr_Adm_Flag
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid LIKE p_Terminal_Id;
    IF UPPER(Trim(v_Opr_Adm_Flag)) = 'H' THEN
      Str := ' SELECT Distinct CHILD_TERMINAL_ID terminalid  FROM FS_FR_TERMINAL_REGN ' ||
             ' union ' ||
             ' SELECT terminalid from FS_FR_TERMINALMASTER where OPER_ADMIN_FLAG =''H''';
    ELSE
      IF p_Operation = 'MODIFY' OR p_Operation = 'DELETE' THEN
        Str := ' SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN

                  CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=' || '''' ||
               p_Terminal_Id || '''' || ' UNION ' ||
               'SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID =' || '''' ||
               p_Terminal_Id || '''';
      ELSE
        Str := ' SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ' || '''' ||
               p_Terminal_Id || '''' || '  UNION ' ||
               '  SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN ' ||
               '  CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=' || '''' ||
               p_Terminal_Id || '''' || '  UNION  ' ||
               '  SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN ' ||
               '  CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=' || '''' ||
               p_Terminal_Id || '''';
      END IF;
    END IF;
    RETURN(Str);
  END;

  /*
  *  Using this function for getting the from_zipcode, to_zipcode information
  *  from qms_zone_code_master,qms_zone_code_dtl based on ZIPCODE_TYPE and passing the in parameters are
  *  p_from_zipcode       VARCHAR2;
  *  p_to_zipcode         VARCHAR2;
  *  p_zone               VARCHAR2;
  *  p_alpha              VARCHAR2;
  *  p_location           VARCHAR2;
  *  p_zoncode_type       VARCHAR2;
  *  and following accesslevel.
  *  RETURN VARCHAR2;
  */
  FUNCTION Zone_Code_Dtl_Fun(p_From_Zipcode  VARCHAR2,
                             p_To_Zipcode    VARCHAR2,
                             p_Zone          VARCHAR2,
                             p_Alpha         VARCHAR2,
                             p_Location      VARCHAR2,
                             p_Zoncode_Type  VARCHAR2,
                             p_shipment_mode VARCHAR2,
                             p_console_type  VARCHAR2) RETURN NUMBER IS

    v_Rc           Sys_Refcursor;
    v_Sql          VARCHAR2(50);
    v_From_Zipcode VARCHAR2(50);
    v_To_Zipcode   VARCHAR2(50);

  BEGIN
    OPEN v_Rc FOR
      SELECT From_Zipcode, To_Zipcode
        FROM QMS_ZONE_CODE_DTL Dtl, QMS_ZONE_CODE_MASTER Mas
       WHERE Mas.Zone_Code = Dtl.Zone_Code
         AND UPPER(Dtl.Invalidate) = 'F'
         AND Origin_Location = p_Location
         AND Zipcode_Type = p_Zoncode_Type
         AND NVL(Alphanumeric, '~') = NVL(p_Alpha, '~')
         AND Mas.Shipment_Mode = p_Shipment_Mode
         AND NVL(Mas.Console_Type, '~') = NVL(p_Console_Type, '~');
    LOOP
      FETCH v_Rc
        INTO v_From_Zipcode, v_To_Zipcode;
      EXIT WHEN v_Rc%NOTFOUND;

      IF (TO_NUMBER(p_From_Zipcode) >= TO_NUMBER(v_From_Zipcode) AND
         TO_NUMBER(p_From_Zipcode) <= TO_NUMBER(v_To_Zipcode)) OR
         (TO_NUMBER(p_To_Zipcode) >= TO_NUMBER(v_From_Zipcode) AND
         TO_NUMBER(p_To_Zipcode) <= TO_NUMBER(v_To_Zipcode)) THEN
        RETURN 0;
      END IF;
    END LOOP;
    RETURN 1;
  END;

  /*
  *  Using this function for getting the INDUSTRY_ID,DESCRIPTION,INVALIDATE information
  *  from QMS_INDUSTRY_REG based on terminalid and passing the in parameters are
  *  p_terminal_id         VARCHAR2;
  *  p_industry_id         VARCHAR2;
  *  p_str1                VARCHAR2;
  *  p_str2                VARCHAR2;
  *  p_operation           VARCHAR2;.
  *  and following accesslevel.
  *  RETURN VARCHAR2;
  */
  FUNCTION Get_Industryregistration1(p_Terminal_Id VARCHAR2,
                                     p_Industry_Id VARCHAR2,
                                     p_Str1        VARCHAR2,
                                     p_Str2        VARCHAR2,
                                     p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1  g_Ref_Cur;
    Str  VARCHAR2(32767);
    Str1 VARCHAR2(20);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    /*IF (p_str1 != 'NULL' AND p_str2 = 'INVALIDATE')

    THEN

       str1 := ' AND INVALIDATE LIKE  ' || '''' || '%' || '''';

    ELSE

       str1 := ' AND INVALIDATE LIKE  ' || '''' || 'F' || '''';

    END IF;*/

    OPEN c_1 FOR 'SELECT INDUSTRY_ID,DESCRIPTION,INVALIDATE  FROM  QMS_INDUSTRY_REG  WHERE INDUSTRY_ID LIKE   ' || '''' || p_Industry_Id || '''' || ' AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL) ' || ' AND TERMINALID IN (' || Str || ')' || ' ORDER BY INDUSTRY_ID ';
    RETURN c_1;
  END;

  /*
  *  Using this function for getting the PORTID,PORTNAME information
  *  from FS_FRS_PORTMASTER based on terminalid and passing the in parameters are
  *  p_terminal_id         VARCHAR2;
  *  p_portid              VARCHAR2;
  *  p_operation           VARCHAR2;.
  *  and following accesslevel.
  *  RETURN VARCHAR2;
  */
  FUNCTION Get_Port1(p_Terminal_Id VARCHAR2,
                     p_Portid      VARCHAR2,
                     p_Operation   VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str VARCHAR2(32767);
  BEGIN
    Str := Get_String(p_Terminal_Id, p_Operation);
    OPEN c_1 FOR 'SELECT DISTINCT PORTID,PORTNAME FROM FS_FRS_PORTMASTER  WHERE PORTID  LIKE' || '''' || p_Portid || '''' || ' AND ( INVALIDATE=' || '''' || 'F' || '''' || ' OR INVALIDATE IS NULL) ' || ' AND  TERMINALID IN (' || Str || ')' || ' ORDER BY PORTID';
    RETURN c_1;
      END;

FUNCTION Get_Regions(p_Regions      VARCHAR2,
                     p_Location     VARCHAR2,
                     p_Country      VARCHAR2,
                     p_Shipmentmode VARCHAR2) RETURN g_Ref_Cur IS
  c_1  g_Ref_Cur;
  Str  VARCHAR2(1000);
  Str1 VARCHAR2(1000) := '';
  Str2 VARCHAR2(1000) := '';
   Str3 VARCHAR2(1000) := '';
BEGIN
  IF p_Country IS NULL AND p_Location IS NULL THEN
   IF p_Regions IS NOT NULL OR Trim(p_Regions) <> '' THEN
     /* Str3 := ' AND CON.REGION IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_Regions) || ') ';*/
     Str3 := ' AND CON.REGION LIKE ' || '''' ||p_Regions ||'%' ||'''' ;
    ELSE
      Str3 := '';
    END IF;
   PRINT_OUT('SELECT DISTINCT REGION FROM  FS_COUNTRYMASTER   WHERE CON.INVALIDATE=' || '''' || 'F' || ''''|| Str3);
    OPEN c_1 FOR 'SELECT DISTINCT REGION FROM  FS_COUNTRYMASTER CON   WHERE CON.INVALIDATE=' || '''' || 'F' || ''''|| Str3;
  ELSE

    IF p_Country IS NOT NULL OR Trim(p_Country) <> '' THEN
      Str1 := ' AND CON.COUNTRYID IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_Country) || ') ';
    ELSE
      Str1 := '';
    END IF;
  --ADD THIS CONDITION FOR Get_Country_1
   IF p_Regions IS NOT NULL OR Trim(p_Regions) <> '' THEN
      Str3 := ' AND CON.REGION IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_Regions) || ') ';
    ELSE
      Str3 := '';
    END IF;
--ENDS
    IF p_Location IS NOT NULL OR Trim(p_Location) <> '' THEN
      IF p_Shipmentmode = '2' OR UPPER(p_Shipmentmode) = 'SEA' THEN
        Str2 := ' AND  port.portid  IN (' ||
                Qms_Rsr_Rates_Pkg.Seperator(p_Location) || ') ';
      ELSE
        Str2 := ' AND  LOC.LOCATIONID IN (' ||
                Qms_Rsr_Rates_Pkg.Seperator(p_Location) || ') ';
      END IF;
    ELSE
      Str2 := '';
    END IF;
     print_out('SELECT DISTINCT CON.REGION FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC WHERE LOC.COUNTRYID=CON.COUNTRYID' || Str3 || Str1 || Str2 || '  AND CON.INVALIDATE=' || '''' || 'F' || '''');
    IF p_Shipmentmode = '2' OR UPPER(p_Shipmentmode) = 'SEA' THEN
      OPEN c_1 FOR 'SELECT DISTINCT CON.REGION FROM FS_COUNTRYMASTER CON,fs_frs_portmaster port where port.countryid=con.countryid '|| Str3 || Str1 || Str2 || '  AND CON.INVALIDATE=' || '''' || 'F' || '''';
    ELSE
      OPEN c_1 FOR 'SELECT DISTINCT CON.REGION FROM FS_COUNTRYMASTER CON,FS_FR_LOCATIONMASTER LOC WHERE LOC.COUNTRYID=CON.COUNTRYID' || Str3 || Str1 || Str2 || '  AND CON.INVALIDATE=' || '''' || 'F' || '''';
    END IF;
  END IF;
  RETURN c_1;
END;

 FUNCTION Get_Region_countryIds(p_Location     VARCHAR2,
                                p_Country      VARCHAR2,
                                p_regionId      VARCHAR2,
                                p_Shipmentmode VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;

   Str1 VARCHAR2(1000);
  BEGIN

    IF p_regionId IS NOT NULL OR Trim(p_regionId) <> '' THEN
      Str1 := ' AND CON.REGION IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_regionId) || ') ';

    ELSIF p_Country IS NOT NULL OR Trim(p_Country) <> '' THEN
    --  Str1 := '';
      Str1 := Str1 ||' AND CON.COUNTRYID IN (' ||
              Qms_Rsr_Rates_Pkg.Seperator(p_Country) || ') ';

    END IF;
DBMS_OUTPUT.put_line('SELECT DISTINCT CON.COUNTRYID, COUNTRYNAME FROM FS_COUNTRYMASTER CON where CON.INVALIDATE=' || '''' || 'F' || '''' ||   Str1 || ' ORDER BY COUNTRYNAME');
        OPEN c_1 FOR 'SELECT DISTINCT CON.COUNTRYID, COUNTRYNAME FROM FS_COUNTRYMASTER CON where CON.INVALIDATE=' || '''' || 'F' || '''' ||   Str1 || ' ORDER BY COUNTRYNAME';


    RETURN c_1;

  END;

FUNCTION Get_Region_LocationIds(p_regionId      VARCHAR2,
                                p_Shipmode VARCHAR2) RETURN g_Ref_Cur IS
    c_1 g_Ref_Cur;
    Str                 VARCHAR2(1000);
    v_region_qry            VARCHAR2(5000) := '';
    p_Shipment_Mode_Str VARCHAR2(1000);
  BEGIN
        IF p_Shipmode = '1' OR UPPER(p_Shipmode) = UPPER('AIR') THEN
      p_Shipment_Mode_Str := '(''1'',''3'',''5'',''7'')';
    ELSIF p_Shipmode = '2' OR UPPER(p_Shipmode) = UPPER('SEA') THEN
      p_Shipment_Mode_Str := '(''2'',''3'',''6'',''7'')';
    ELSIF p_Shipmode = '4' OR UPPER(p_Shipmode) = UPPER('TRUCK') THEN
      p_Shipment_Mode_Str := '(''4'',''5'',''6'',''7'')';
    ELSE
      p_Shipment_Mode_Str := '(''1'',''2'',''3'',''4'',''5'',''6'',''7'')';
    END IF;

IF (p_regionid IS NOT NULL) THEN
v_region_qry := ' SELECT DISTINCT CON.COUNTRYID FROM FS_COUNTRYMASTER CON  where  CON.INVALIDATE = ''F'' and con.region in ( '||
              Qms_Rsr_Rates_Pkg.Seperator(p_regionid) || ') ';
ELSE
v_region_qry := '';
END IF;

PRINT_OUT('SELECT DISTINCT LOC.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON ,FS_FR_TERMINALLOCATION TL  WHERE LOC.COUNTRYID=CON.COUNTRYID AND CON.COUNTRYID IN ('  || v_region_qry || ' )' || ' AND   LOC.LOCATIONID=TL.LOCATIONID AND ( LOC.INVALIDATE=' || '''' || 'F' || '''' || ' OR  LOC.INVALIDATE IS NULL )ORDER BY LOC.LOCATIONID ');
    IF (p_Shipmode = '2') THEN
      OPEN c_1 FOR 'select portid LOCATIONID ,PORTNAME  LOCATIONNAME,SHIPMENTMODE from fs_frs_portmaster where ' || ' COUNTRYID IN (' ||   v_region_qry  || ') '  || ' AND (invalidate=''F'' OR invalidate IS NULL)';
     ELSE
      OPEN c_1 FOR 'SELECT DISTINCT LOC.LOCATIONID,LOCATIONNAME,SHIPMENTMODE,CITY FROM   FS_FR_LOCATIONMASTER LOC, FS_COUNTRYMASTER CON ,FS_FR_TERMINALLOCATION TL  WHERE LOC.COUNTRYID=CON.COUNTRYID AND CON.COUNTRYID IN ('  || v_region_qry  || ' )' || ' AND   LOC.LOCATIONID=TL.LOCATIONID AND ( LOC.INVALIDATE=' || '''' || 'F' || '''' || ' OR  LOC.INVALIDATE IS NULL )ORDER BY LOC.LOCATIONID ';
    END IF;



    RETURN c_1;

  END;

END Qms_Setups;

/

/

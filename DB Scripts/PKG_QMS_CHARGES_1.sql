--------------------------------------------------------
--  DDL for Package Body PKG_QMS_CHARGES
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "PKG_QMS_CHARGES" AS
/*
This procedure is used for performing server side validation of the data entered by the user
in case of Buy Charges Add.
This procedure also updates any corresponding quotes based on these buy charges by
calling the QMS_QUOTEPACK_NEW.qms_quote_update procedure.

It returns a resultset object with information about any corresponding sell charges that might exist
in the lower terminals than the logged in terminal in the hierarchy.

The IN Parameters are

V_CHARGE_ID VARCHAR2,
V_CHARGE_DESC_ID VARCHAR2,
V_CHARGE_BASIS_ID VARCHAR2,
P_RATEBREAK       VARCHAR2,
P_RATETYPE        VARCHAR2,
V_TERMINALID VARCHAR2,
p_densityratio VARCHAR2,
v_currencyId   VARCHAR2,
V_NEWBUYCHARGEID NUMBER

The OUT Parameter is
P_RS
*/
FUNCTION BUYCHARGESADD(V_CHARGE_ID VARCHAR2,
                        V_CHARGE_DESC_ID VARCHAR2,
                        V_CHARGE_BASIS_ID VARCHAR2,
                        P_RATEBREAK       VARCHAR2,
                        P_RATETYPE        VARCHAR2,
                        V_TERMINALID VARCHAR2,
                        p_densityratio VARCHAR2,
                        v_currencyId   VARCHAR2,
                        V_NEWBUYCHARGEID NUMBER,
                        P_RS OUT CSCOLUMNS)
                        RETURN VARCHAR2
IS
CURSOR C0 IS
   SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A';

CURSOR C1 IS
    SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=V_TERMINALID;

CURSOR C2 IS
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = V_TERMINALID
    UNION
    SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=V_TERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' AND TERMINALID <> V_TERMINALID;

CURSOR C3 IS
    SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=V_TERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' AND TERMINALID <> V_TERMINALID;


C_TERMINAL VARCHAR2(25);
C_CHARGEDESCID VARCHAR2(50);
V_COUNT NUMBER(9);
CHARGE_BASIS VARCHAR2(25);
V_CHARGE VARCHAR2(25);
v_accesslevel VARCHAR2(10);
v_terminals VARCHAR2(32767);
v_oldbuychargeid VARCHAR2(20);
v_ratebreak      VARCHAR2(20);
v_ratetype      VARCHAR2(20);
v_changedesc    VARCHAR2(50);
v_density       VARCHAR2(10);
BEGIN
  BEGIN
    SELECT COUNT(*) INTO V_COUNT FROM QMS_CHARGESMASTER WHERE CHARGE_ID = V_CHARGE_ID AND (INVALIDATE='F' OR INVALIDATE IS NULL);
    --DBMS_OUTPUT.PUT_LINE('V_COUNT V_COUNT '||V_COUNT);
    IF V_COUNT = 0 THEN
      RETURN '1';
    END IF;
  /*EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN '100';*/
  END;
  BEGIN
    SELECT COUNT(*) INTO V_COUNT FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS = V_CHARGE_BASIS_ID AND (INVALIDATE='F' OR INVALIDATE IS NULL);
    --DBMS_OUTPUT.PUT_LINE('V_COUNT V_COUNT '||V_COUNT);
    IF V_COUNT <= 0 THEN
      RETURN '2';
    END IF;
  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END;

    BEGIN
    v_terminals:='';
    FOR i IN C2 LOOP
        v_terminals := v_terminals ||''''||i.terminalid||''''||',';
    END LOOP;
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --FOR I IN C2 LOOP
      EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM QMS_CHARGEDESCMASTER  WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
      ' TERMINALID IN ('||''''||v_terminals||''''||') AND (INVALIDATE=''F'' OR INVALIDATE IS NULL)') INTO V_COUNT;
      --EXIT WHEN V_COUNT > 0;
    --END LOOP;
    --DBMS_OUTPUT.PUT_LINE('V_COUNT V_COUNT '||V_COUNT);
    IF V_COUNT <= 0 THEN
      RETURN '3';
    END IF;
  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END;

  BEGIN
    SELECT COUNT(*) INTO V_COUNT FROM QMS_BUYSELLCHARGESMASTER
    WHERE CHARGEDESCID=V_CHARGE_DESC_ID AND TERMINALID = V_TERMINALID AND DEL_FLAG='N';
    --DBMS_OUTPUT.PUT_LINE('V_COUNT V_COUNT '||V_COUNT);
    IF V_COUNT >0 THEN
      RETURN '4';
    END IF;
  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END;

  EXECUTE IMMEDIATE(' SELECT COUNT(*) FROM FS_COUNTRYMASTER WHERE currencyid ='||''''||v_currencyId||'''') INTO V_COUNT;

    IF V_COUNT <= 0 THEN
      RETURN '8';
    END IF;

  --validated by ramakrishna
     IF (LENGTH (p_densityratio) > 0)
      THEN
         v_density := validate_densityratio (p_densityratio,V_CHARGE_BASIS_ID);

         IF v_density = 'FALSE'
         THEN
            RETURN '7';
         END IF;
      END IF;




  BEGIN
   v_terminals:='';
    FOR i IN C3 LOOP
        v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
    END LOOP;
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);

  --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --FOR J IN C3 LOOP
      EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM QMS_BUYSELLCHARGESMASTER'||
      ' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N''') INTO V_COUNT ;
      --DBMS_OUTPUT.PUT_LINE('V_COUNT V_COUNT '||V_COUNT);
        IF V_COUNT >0 THEN
          RETURN  'H_';
        END IF;
    --END LOOP;
  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END;

  BEGIN
    SELECT oper_admin_flag INTO v_accesslevel FROM FS_FR_TERMINALMASTER WHERE TERMINALID=V_TERMINALID;
    IF v_accesslevel='H' THEN
    v_terminals:='';
    FOR i IN C0 LOOP
        v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
    END LOOP;
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);

    OPEN P_RS FOR 'SELECT BUYSELLCHARGEID,RATE_BREAK,RATE_TYPE,CHARGEDESCID  FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN('||''''||v_terminals||''''||') AND DEL_FLAG=''N''' ;

    LOOP
      FETCH  P_RS INTO v_oldbuychargeid,v_ratebreak,v_ratetype,v_changedesc;
    EXIT WHEN P_RS%NOTFOUND;
      IF v_ratebreak = P_RATEBREAK THEN
        QMS_QUOTEPACK_NEW.qms_quote_update(NULL,v_oldbuychargeid,NULL,NULL,NULL,NULL,V_NEWBUYCHARGEID,NULL,'B',NULL,NULL,v_changedesc);
      END IF;

    END LOOP;


      OPEN P_RS FOR 'SELECT BUYSELLCHARGEID,RATE_BREAK,RATE_TYPE  FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN('||''''||v_terminals||''''||') AND DEL_FLAG=''N''' ;


   -- FOR K IN C0 LOOP
      EXECUTE IMMEDIATE ('UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG=''Y'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N''') ;

    --END LOOP;
    ELSE
    v_terminals:='';
    FOR i IN C1 LOOP
        v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
    END LOOP;

    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) -3);
    OPEN P_RS FOR 'SELECT BUYSELLCHARGEID,RATE_BREAK,RATE_TYPE,CHARGEDESCID  FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN('||''''|| v_terminals||''''||') AND DEL_FLAG=''N''' ;

    LOOP
      FETCH  P_RS INTO v_oldbuychargeid,v_ratebreak,v_ratetype,v_changedesc;
    EXIT WHEN P_RS%NOTFOUND;
      IF v_ratebreak = P_RATEBREAK THEN
        QMS_QUOTEPACK_NEW.qms_quote_update(NULL,v_oldbuychargeid,NULL,NULL,NULL,NULL,V_NEWBUYCHARGEID,NULL,'B',NULL,NULL,v_changedesc);
      END IF;

    END LOOP;


      OPEN P_RS FOR 'SELECT BUYSELLCHARGEID,RATE_BREAK,RATE_TYPE  FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN('||''''|| v_terminals||''''||') AND DEL_FLAG=''N''' ;

    --FOR K IN C1 LOOP
      EXECUTE IMMEDIATE ('UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG=''Y'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||
      ' AND TERMINALID IN ('||''''|| v_terminals||''''||') AND DEL_FLAG=''N''') ;

    --END LOOP;
    END IF;
  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END;

  RETURN '6';

  /*EXCEPTION WHEN OTHERS THEN
    RETURN '100';*/
  END BUYCHARGESADD;

/*
This procedure is used for getting the list of terminals depending on the operation
and the logged-in terminal.

The IN Parameters are

V_OPERATION VARCHAR2,
V_SEARCHSTR VARCHAR2,
V_CURTERMINALID VARCHAR2

It Returns a resultset object CSCOLUMNS
*/

  FUNCTION GETTERMINALLISTBRSCHARGES(V_OPERATION VARCHAR2,
                                    V_SEARCHSTR VARCHAR2,
                                    V_CURTERMINALID VARCHAR2)RETURN CSCOLUMNS
  AS
  RETURNLIST CSCOLUMNS;
  SQLSTR0 VARCHAR2(1000):='';
  SQLSTR1 VARCHAR2(1000):='';
  SQLSTR2 VARCHAR2(1000):='';
  v_accesslevel  VARCHAR2(10);
  BEGIN

  SQLSTR0 := 'SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE TERMINALID LIKE '||''''||V_SEARCHSTR||'%'||''' ORDER BY TERMINALID';

  SQLSTR1 :=' select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn
              CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='
              ||''''||V_CURTERMINALID||''''||' AND  CHILD_TERMINAL_ID LIKE '||''''||V_SEARCHSTR||'%'||'''' ||
              ' UNION '||
              'SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID ='
              ||''''||V_CURTERMINALID||''''||' AND TERMINALID LIKE '||''''||V_SEARCHSTR||'%'||''' ORDER BY TERMINALID';

  SQLSTR2 :=' SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '||
              ''''||V_CURTERMINALID||''''||' AND TERMINALID LIKE '||''''||V_SEARCHSTR||'%'||''''||
            '  UNION '||
            '  select CHILD_TERMINAL_ID TERMINALID from fs_fr_terminal_regn '||
            '  connect by prior CHILD_TERMINAL_ID=PARENT_TERMINAL_ID start with PARENT_TERMINAL_ID='||
            ''''||V_CURTERMINALID||''''||' AND  CHILD_TERMINAL_ID LIKE '||''''||V_SEARCHSTR||'%'||''''||
            '  UNION  '||
            '  SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn '||
            '  connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID='||
            ''''||V_CURTERMINALID||''''||' AND  PARENT_TERMINAL_ID LIKE '||''''||V_SEARCHSTR||'%'||''''||
            '  UNION '||
            '  SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=''H'' AND  TERMINALID LIKE '||''''||V_SEARCHSTR||'%'||''' ORDER BY TERMINALID';

print_out(SQLSTR0);
SELECT oper_admin_flag INTO v_accesslevel FROM FS_FR_TERMINALMASTER WHERE TERMINALID=V_CURTERMINALID;

  IF(LENGTH(V_OPERATION)>0 AND ( V_OPERATION='Modify' OR v_operation = 'Delete') )THEN
    IF v_accesslevel='H' THEN
    OPEN RETURNLIST FOR SQLSTR0;
    ELSE
      OPEN RETURNLIST FOR SQLSTR1;
     END IF;
  ELSIF(LENGTH(V_OPERATION)>0 AND V_OPERATION='View')THEN
    IF v_accesslevel='H' THEN
      OPEN RETURNLIST FOR SQLSTR0;
    ELSE
     OPEN RETURNLIST FOR SQLSTR2;
    END IF;
  END IF;

  RETURN RETURNLIST;
  END GETTERMINALLISTBRSCHARGES;

/*
This procedure is used for performing server-side validation for operating terminal
w.r.t the logged-in terminal based on the operation.

The IN Parameters are

V_OPERATION VARCHAR2,
V_SEARCHSTR VARCHAR2,
V_CURTERMINALID VARCHAR2

It Returns a varchar
*/

 PROCEDURE ISEXISTINTHEHIRARCHY(V_OPERATION VARCHAR2,
                                V_TERMINALID VARCHAR2,
                                V_CURTERMINALID VARCHAR2,
                                V_RETURNVAL OUT VARCHAR2)
 AS

 CURSOR C0 IS
   SELECT TERMINALID  FROM FS_FR_TERMINALMASTER ;

  CURSOR C1 IS
    SELECT TERMINALID FROM (SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=V_CURTERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = V_CURTERMINALID) WHERE  TERMINALID=V_TERMINALID;

  CURSOR C2 IS
    SELECT TERMINALID FROM (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = V_CURTERMINALID
    UNION
    SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=V_CURTERMINALID
    UNION
    SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=V_CURTERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' ) WHERE TERMINALID=V_TERMINALID;

  v_accesslevel  VARCHAR2(10);
 BEGIN
  BEGIN
       SELECT oper_admin_flag INTO v_accesslevel FROM FS_FR_TERMINALMASTER WHERE TERMINALID=V_CURTERMINALID;

       IF(LENGTH(V_OPERATION)>0 AND (V_OPERATION='Modify' OR V_OPERATION='Delete') )THEN
          IF v_accesslevel='H' THEN
          FOR I IN C0
              LOOP
                 V_RETURNVAL:=1;
                 EXIT;
              END LOOP;
          ELSE
              FOR I IN C1
              LOOP
                 V_RETURNVAL:=1;
                 EXIT;
              END LOOP;
          END IF;
       ELSIF(LENGTH(V_OPERATION)>0 AND UPPER(V_OPERATION)='VIEW')THEN
         IF v_accesslevel='H' THEN
             FOR J IN C0
             LOOP
               V_RETURNVAL:=1;
               EXIT;
             END LOOP;
         ELSE
              FOR J IN C2
             LOOP
               V_RETURNVAL:=1;
               EXIT;
             END LOOP;
         END IF;
       END IF;
   EXCEPTION WHEN OTHERS THEN
    V_RETURNVAL:=2;
   END;

 END ISEXISTINTHEHIRARCHY;

 /*
This procedure is used to fetch Buy/Sell Charge Ids for Enter-Id Screen LOV's.

The IN Parameters are

V_CHARGEID VARCHAR2,
V_FROMWHERE VARCHAR2,
V_CHARGEDESCID VARCHAR2,
V_TERMINALID  VARCHAR2

It Returns a Resultset Object
*/

  FUNCTION GETBUYSELLCHARGEIDS(V_CHARGEID VARCHAR2,
                           V_FROMWHERE VARCHAR2,
                           V_CHARGEDESCID VARCHAR2,
                           V_TERMINALID  VARCHAR2,
                           V_CHARGEGROUPID VARCHAR2)--added by VLAKSHMI for CR #170761
                           RETURN CSCOLUMNS
 AS
  RETURNLIST CSCOLUMNS;
  SQLSTR    VARCHAR2(1000):='';

 BEGIN
  SQLSTR:=' SELECT DISTINCT BM.CHARGE_ID ||''--''|| CM.CHARGE_DESCRIPTION FROM QMS_CHARGESMASTER CM';

  IF(V_FROMWHERE='buychargesenterid') THEN
    SQLSTR:=SQLSTR || ' ,QMS_BUYSELLCHARGESMASTER  BM  ';
  ELSIF(V_FROMWHERE='sellchargesenterid') THEN
    SQLSTR:=SQLSTR || ' ,QMS_SELLCHARGESMASTER  BM  ';
  END IF;

  IF( LENGTH(V_CHARGEDESCID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' ,QMS_CHARGEDESCMASTER CBM  ';
    END IF;
    IF( LENGTH(V_CHARGEGROUPID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' , QMS_CHARGE_GROUPSMASTER  GM  ';--added by VLAKSHMI for CR #170761
  END IF;

  SQLSTR:=SQLSTR || ' WHERE BM.CHARGE_ID = CM.CHARGE_ID  AND BM.TERMINALID='||''''|| V_TERMINALID ||'''';
IF( LENGTH(V_CHARGEGROUPID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' AND GM.Charge_Id = BM.Charge_Id  ';--added by VLAKSHMI for CR #170761
  END IF;
  IF( LENGTH(V_CHARGEDESCID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' AND BM.CHARGEDESCID=  '||''''|| V_CHARGEDESCID ||'''';
    SQLSTR:=SQLSTR || ' AND CBM.CHARGEDESCID= '||''''|| V_CHARGEDESCID ||'''';
  END IF;
IF( LENGTH(V_CHARGEGROUPID) > 0 ) THEN --added by VLAKSHMI for CR #170761
    SQLSTR:=SQLSTR || ' AND GM.CHARGEGROUP_ID=  '||''''|| V_CHARGEGROUPID ||'''';--added by VLAKSHMI for CR #170761
  END IF;
  SQLSTR:=SQLSTR || ' AND BM.CHARGE_ID LIKE '||''''||V_CHARGEID||'%'||'''';

  IF(V_FROMWHERE='buychargesenterid') THEN
    SQLSTR:=SQLSTR || ' AND BM.DEL_FLAG =''N'' ';
  ELSIF(V_FROMWHERE='sellchargesenterid') THEN
    SQLSTR:=SQLSTR || 'AND BM.IE_FLAG=''A''  ';
  END IF;
  --SQLSTR:=SQLSTR || ' ORDER BY BM.CHARGE_ID' ;

  dbms_output.put_line('SQLSTR' || SQLSTR);

  OPEN RETURNLIST FOR SQLSTR;

  RETURN RETURNLIST;

 END GETBUYSELLCHARGEIDS;


/*
This procedure is used to fetch Buy/Sell Charge Description Ids for Enter-Id Screen LOV's.

The IN Parameters are

V_CHARGEDESCID VARCHAR2,
V_FROMWHERE VARCHAR2,
V_CHARGEDESCID VARCHAR2,
V_TERMINALID  VARCHAR2

It Returns a Resultset Object
*/

FUNCTION GETBUYSELLCHARGEDESCIDS(V_CHARGEDESCID  VARCHAR2,
                           V_FROMWHERE VARCHAR2,
                           V_CHARGEID VARCHAR2,
                           V_TERMINALID  VARCHAR2,
                           V_CHARGE_GROUP_ID VARCHAR2)--@@Added by subrahmanyam for 195270 on 20-Jan-10
                           RETURN CSCOLUMNS
 AS
  RETURNLIST CSCOLUMNS;
  SQLSTR    VARCHAR2(1000):='';

 BEGIN
 --@@Commente by subrahmanyam for 195270 on 20-Jan-10
 --SQLSTR:=' SELECT DISTINCT BM.CHARGEDESCID FROM QMS_CHARGEDESCMASTER CBM,QMS_CHARGE_GROUPSMASTER CGM';
 --@@Added by subrahmanyam for 195270 on 20-Jan-10
 IF ( V_CHARGE_GROUP_ID IS NULL OR V_CHARGE_GROUP_ID='' ) THEN
  SQLSTR:=' SELECT DISTINCT BM.CHARGEDESCID FROM QMS_CHARGEDESCMASTER CBM';
ELSE
 SQLSTR:=' SELECT DISTINCT BM.CHARGEDESCID FROM QMS_CHARGEDESCMASTER CBM,QMS_CHARGE_GROUPSMASTER CGM';
END IF;
--@@ended by subrahmanyam for 195270 on 20-Jan-10
  IF(V_FROMWHERE='buychargesenterid') THEN
    SQLSTR:=SQLSTR || ' ,QMS_BUYSELLCHARGESMASTER  BM  ';
  ELSIF(V_FROMWHERE='sellchargesenterid') THEN
    SQLSTR:=SQLSTR || ' ,QMS_SELLCHARGESMASTER  BM  ';
  END IF;

  IF( LENGTH(V_CHARGEID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' ,QMS_CHARGESMASTER CM  ';
  END IF;
   --@@Commented by subrahmanyam for 195270 on 20-Jan-10
  --  SQLSTR:=SQLSTR || ' WHERE BM.CHARGEDESCID = CBM.CHARGEDESCID  AND BM.TERMINALID='||''''|| V_TERMINALID ||'''';
  --@@added by subrahmanyam for 195270 on 20-Jan-10
   IF ( V_CHARGE_GROUP_ID IS NULL OR V_CHARGE_GROUP_ID='' ) THEN
  SQLSTR:=SQLSTR || ' WHERE BM.CHARGEDESCID = CBM.CHARGEDESCID  AND BM.TERMINALID='||''''|| V_TERMINALID ||'''';
  ELSE
 --Commented and Added by subrahmanyam for the wpbn id:219544 on 29-sept-10
 /*   SQLSTR:=SQLSTR || ' WHERE BM.CHARGEDESCID = CBM.CHARGEDESCID AND  CBM.CHARGEDESCID = CGM.CHARGEDESCID AND  CGM.CHARGE_ID = CBM.CHARGEID AND BM.TERMINALID='||''''|| V_TERMINALID ||''''||
    'AND CGM.CHARGEGROUP_ID = '||''''||V_CHARGE_GROUP_ID||'''';*/
       SQLSTR:=SQLSTR || ' WHERE BM.CHARGEDESCID = CBM.CHARGEDESCID AND  CBM.CHARGEDESCID = CGM.CHARGEDESCID AND CGM.INACTIVATE=''N'' AND CGM.INVALIDATE=''F'' AND CGM.CHARGE_ID = CBM.CHARGEID AND BM.TERMINALID='||''''|| V_TERMINALID ||''''||
    'AND CGM.CHARGEGROUP_ID = '||''''||V_CHARGE_GROUP_ID||'''';
  END IF;
  --@@ended by subrahmanyam for 195270 on 20-Jan-10
  IF( LENGTH(V_CHARGEID) > 0 ) THEN
    SQLSTR:=SQLSTR || ' AND BM.CHARGE_ID =  '||''''|| V_CHARGEID ||'''';
    SQLSTR:=SQLSTR || ' AND CM.CHARGE_ID = '||''''|| V_CHARGEID ||'''';
  END IF;

  SQLSTR:=SQLSTR || '  AND BM.CHARGEDESCID LIKE '||''''||V_CHARGEDESCID||'%'||'''';

  IF(V_FROMWHERE='buychargesenterid') THEN
    SQLSTR:=SQLSTR || ' AND BM.DEL_FLAG =''N'' ';
  ELSIF(V_FROMWHERE='sellchargesenterid') THEN
    SQLSTR:=SQLSTR || 'AND BM.IE_FLAG=''A''  ';
  END IF;
  SQLSTR:=SQLSTR || ' AND (BM.CHARGE_ID,BM.CHARGEDESCID) NOT IN (SELECT CHARGE_ID,CHARGEDESCID FROM QMS_SELLCHARGESMASTER_ACC) ';

 /**SQLSTR:=SQLSTR || ' ORDER BY BM.CHARGE_ID'||'''';**/
  OPEN RETURNLIST FOR SQLSTR;

  RETURN RETURNLIST;

 END GETBUYSELLCHARGEDESCIDS;


/*
This procedure is used for performing server side validation of the data entered by the user
in case of Sell Charges Add.

The IN Parameters are

V_CHARGE_ID       VARCHAR2,
V_CHARGE_DESC_ID  VARCHAR2,
V_CHARGE_BASIS_ID VARCHAR2,
p_densityratio    VARCHAR2,
v_currencyId      VARCHAR2,
V_TERMINALID      VARCHAR2

It Returns a VARCHAR
*/

FUNCTION VALIDATE_SELLCHARGESADD( V_CHARGE_ID       VARCHAR2,
                                    V_CHARGE_DESC_ID  VARCHAR2,
                                    V_CHARGE_BASIS_ID VARCHAR2,
                                    p_densityratio    VARCHAR2,
                                    v_currencyId      VARCHAR2,
                                    V_TERMINALID      VARCHAR2)
RETURN VARCHAR2
IS
v_terminals VARCHAR2(32767);
v_rs  CSCOLUMNS;
v_ratebreak VARCHAR2(20);
v_sellchargeid NUMBER(10);
v_buychargeid NUMBER(10);
v_density    VARCHAR2(10);
CURSOR C0 IS
   SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG='A' ;

CURSOR C1 IS
    SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=V_TERMINALID;

CURSOR C2 IS
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = V_TERMINALID
    UNION
    SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=V_TERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' AND TERMINALID <> V_TERMINALID;

CURSOR C3 IS
    SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=V_TERMINALID
    UNION
    SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H' AND TERMINALID <> V_TERMINALID;


C_TERMINAL VARCHAR2(25);
C_CHARGEDESCID VARCHAR2(50);
V_COUNT NUMBER(5);
CHARGE_BASIS VARCHAR2(25);
V_CHARGE VARCHAR2(25);
v_accesslevel VARCHAR2(10);
v_changedesc  VARCHAR2(50);
V_RETSTR      VARCHAR2(100):='';
BEGIN

  --BEGIN
      BEGIN
        SELECT COUNT(*)
          INTO v_Count
          FROM QMS_CHARGESMASTER
         WHERE Charge_Id = v_Charge_Id
           AND (Invalidate = 'F' OR Invalidate IS NULL);
        IF V_COUNT = 0 THEN
          V_RETSTR := 'CHARGEID,';
          --RETURN 1;
        END IF;
      EXCEPTION WHEN NO_DATA_FOUND THEN
        RETURN 100;
      END;

     IF LENGTH(TRIM(V_CHARGE_BASIS_ID)) > 0 THEN
        BEGIN
          SELECT COUNT(*)
            INTO v_Count
            FROM QMS_CHARGE_BASISMASTER
           WHERE Chargebasis = v_Charge_Basis_Id
             AND (Invalidate = 'F' OR Invalidate IS NULL);
          IF V_COUNT <= 0 THEN
            V_RETSTR := V_RETSTR  || 'CHARGEBASISID,';
            --RETURN 2;
          END IF;
        EXCEPTION WHEN OTHERS THEN
          RETURN 100;
        END;
     END IF;

      BEGIN
       /* v_terminals:='';
        FOR i IN C2 LOOP
            v_terminals := v_terminals ||''''||i.terminalid||''''||',';
        END LOOP;
        --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
        --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
        v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);
        --FOR I IN C2 LOOP*/

         /* EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM QMS_CHARGEDESCMASTER  WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
          ' TERMINALID IN ('||''''||v_terminals||''''||') AND (INVALIDATE=''F'' OR INVALIDATE IS NULL)') INTO V_COUNT ;*/
          --EXIT WHEN V_COUNT > 0;
        --END LOOP;

    SELECT COUNT(*) INTO V_COUNT
      FROM QMS_CHARGEDESCMASTER
     WHERE Chargedescid = v_Charge_Desc_Id
       AND Terminalid IN
           (SELECT Terminalid
              FROM FS_FR_TERMINALMASTER
             WHERE Terminalid = v_Terminalid
            UNION
            SELECT Parent_Terminal_Id Terminalid
              FROM FS_FR_TERMINAL_REGN
            CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
             START WITH Child_Terminal_Id = v_Terminalid
            UNION
            SELECT Terminalid
              FROM FS_FR_TERMINALMASTER
             WHERE Oper_Admin_Flag = 'H'
               AND Terminalid <> v_Terminalid)
       AND (Invalidate = 'F' OR Invalidate IS NULL) ;

        IF V_COUNT <= 0 THEN
          V_RETSTR := V_RETSTR  || 'CHARGE DESCRIPTIONID,';
          --RETURN 3;
        END IF;

      EXCEPTION WHEN OTHERS THEN
        RETURN 100;
      END;

     IF LENGTH(TRIM(v_currencyId))>0 THEN
     --print_out(' SELECT COUNT(*) FROM FS_COUNTRYMASTER WHERE currencyid ='||''''||v_currencyId||'''');
      SELECT COUNT(*)
        INTO v_Count
        FROM FS_COUNTRYMASTER
       WHERE Currencyid = v_Currencyid;

      IF V_COUNT <= 0 THEN
        V_RETSTR := V_RETSTR  || 'CURRENCYID,';
        --RETURN '8';
      END IF;
    END IF;
--      IF V_DUMMYFLAG = 'T'
   --     THEN
          IF (LENGTH (p_densityratio) > 0)
          THEN
             v_density := validate_densityratio (p_densityratio,V_CHARGE_BASIS_ID);

             IF v_density = 'FALSE'
             THEN
                V_RETSTR := V_RETSTR  || 'DENSITY RATIO,';
                --RETURN '7';
             END IF;
          END IF;
  --     END IF;


       IF LENGTH(TRIM(V_RETSTR))>0 THEN
        V_RETSTR := SUBSTR(V_RETSTR,0,LENGTH(V_RETSTR)-1);
       END IF;

       RETURN V_RETSTR;

    /*EXCEPTION WHEN OTHERS Then
    Dbms_Output.put_line('SQLERRM <<'|| Sqlerrm || '>>');
       RETURN 100;
    END;*/
END VALIDATE_SELLCHARGESADD;

/*
This procedure updates any corresponding quotes based on these sell charges by
calling the QMS_QUOTEPACK_NEW.qms_quote_update procedure.

It returns a VARCHAR

The IN Parameters are

V_CHARGE_ID VARCHAR2,
V_CHARGE_DESC_ID VARCHAR2,
V_CHARGE_BASIS_ID VARCHAR2,
P_Rate_break VARCHAR2,
P_new_sellchargeId NUMBER,
P_new_buychargeId NUMBER,
V_TERMINALID VARCHAR2,
V_DUMMYFLAG VARCHAR2,
p_densityratio  VARCHAR2

*/

 FUNCTION SELLCHARGESADD(V_CHARGE_ID VARCHAR2, V_CHARGE_DESC_ID VARCHAR2,
                          V_CHARGE_BASIS_ID VARCHAR2,
                          P_Rate_break VARCHAR2,
                          P_new_sellchargeId NUMBER,
                          P_new_buychargeId NUMBER,
                          V_TERMINALID VARCHAR2,
                          V_DUMMYFLAG VARCHAR2,
                          p_densityratio  VARCHAR2)
RETURN VARCHAR2
IS
v_terminals VARCHAR2(32767);
v_rs  CSCOLUMNS;
v_ratebreak VARCHAR2(20);
v_sellchargeid NUMBER(10);
v_buychargeid NUMBER(10);
v_density    VARCHAR2(10);
CURSOR C0 IS
   SELECT Terminalid FROM FS_FR_TERMINALMASTER WHERE Actv_Flag = 'A';

CURSOR C1 IS
    SELECT Child_Terminal_Id Terminalid
      FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
     START WITH Parent_Terminal_Id = v_Terminalid;

CURSOR C2 IS
    SELECT Terminalid
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = v_Terminalid
    UNION
    SELECT Parent_Terminal_Id Terminalid
      FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
     START WITH Child_Terminal_Id = v_Terminalid
    UNION
    SELECT Terminalid
      FROM FS_FR_TERMINALMASTER
     WHERE Oper_Admin_Flag = 'H'
       AND Terminalid <> v_Terminalid;

CURSOR C3 IS
    SELECT Parent_Terminal_Id Terminalid
      FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
     START WITH Child_Terminal_Id = v_Terminalid
    UNION
    SELECT Terminalid
      FROM FS_FR_TERMINALMASTER
     WHERE Oper_Admin_Flag = 'H'
       AND Terminalid <> v_Terminalid;


C_TERMINAL VARCHAR2(25);
C_CHARGEDESCID VARCHAR2(50);
V_COUNT NUMBER(2);
CHARGE_BASIS VARCHAR2(25);
V_CHARGE VARCHAR2(25);
v_accesslevel VARCHAR2(10);
v_changedesc  VARCHAR2(50);

BEGIN
 /* BEGIN
    SELECT COUNT(*) INTO V_COUNT FROM QMS_CHARGESMASTER WHERE CHARGE_ID = V_CHARGE_ID AND (INVALIDATE='F' OR INVALIDATE IS NULL);
    IF V_COUNT = 0 THEN
      RETURN 1;
    END IF;
  EXCEPTION WHEN NO_DATA_FOUND THEN
    RETURN 100;
  END;

  BEGIN
    SELECT COUNT(*) INTO V_COUNT FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS = V_CHARGE_BASIS_ID AND (INVALIDATE='F' OR INVALIDATE IS NULL);
    IF V_COUNT <= 0 THEN
      RETURN 2;
    END IF;
  EXCEPTION WHEN OTHERS THEN
    RETURN 100;
  END;

  BEGIN
    v_terminals:='';
    FOR i IN C2 LOOP
        v_terminals := v_terminals ||''''||i.terminalid||''''||',';
    END LOOP;
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);
    --FOR I IN C2 LOOP
      EXECUTE IMMEDIATE ('SELECT COUNT(*) FROM QMS_CHARGEDESCMASTER  WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
      ' TERMINALID IN ('||''''||v_terminals||''''||') AND (INVALIDATE=''F'' OR INVALIDATE IS NULL)') INTO V_COUNT ;
      --EXIT WHEN V_COUNT > 0;
    --END LOOP;

    IF V_COUNT <= 0 THEN
      RETURN 3;
    END IF;

  EXCEPTION WHEN OTHERS THEN
    RETURN 100;
  END;*/

  BEGIN
    SELECT COUNT(*)
      INTO v_Count
      FROM QMS_SELLCHARGESMASTER
     WHERE Chargedescid = v_Charge_Desc_Id
       AND Terminalid = v_Terminalid
       AND Ie_Flag = 'A';
    IF V_COUNT >0 THEN
      RETURN 4;
    END IF;
  EXCEPTION WHEN OTHERS THEN
  RETURN 100;
  END;

 /* IF V_DUMMYFLAG = 'T'
    THEN
      IF (LENGTH (p_densityratio) > 0)
      THEN
         v_density := validate_densityratio (p_densityratio,V_CHARGE_BASIS_ID);

         IF v_density = 'FALSE'
         THEN
            RETURN '7';
         END IF;
      END IF;


  END IF;

*/

  BEGIN
    v_terminals:='';
    FOR i IN C3 LOOP
        v_terminals := v_terminals ||''''||i.terminalid||''''||',';
    END LOOP;
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);

    --FOR J IN C3 LOOP
      EXECUTE IMMEDIATE ( ' SELECT COUNT(*) FROM QMS_SELLCHARGESMASTER'||
      ' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND TERMINALID IN ('||''''||v_terminals||''''||') AND IE_FLAG=''A''' ) INTO V_COUNT ;
        IF V_COUNT >0 THEN
          RETURN 'H_';
        END IF;
    --END LOOP;
  EXCEPTION WHEN OTHERS THEN
  RETURN 100;
  END;

  BEGIN
  SELECT Oper_Admin_Flag
    INTO v_Accesslevel
    FROM FS_FR_TERMINALMASTER
   WHERE Terminalid = v_Terminalid;
    IF v_accesslevel='H' THEN

    v_terminals:='';
    FOR i IN C0 LOOP
        v_terminals := v_terminals ||''''||i.terminalid||''''||',';
    END LOOP;
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);

    OPEN v_rs FOR ('SELECT SELLCHARGEID, BUYCHARGEID, RATE_BREAK,CHARGEDESCID  FROM QMS_SELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
      ' TERMINALID IN ('||''''||v_terminals||''''||') AND IE_FLAG=''A'' ');

    LOOP
    FETCH v_rs INTO v_sellchargeid, v_buychargeid,v_ratebreak,v_changedesc;
    EXIT WHEN v_rs%NOTFOUND;
      IF v_ratebreak = P_RATE_BREAK THEN
        QMS_QUOTEPACK_NEW.qms_quote_update(v_sellchargeid,v_buychargeid,NULL,NULL,NULL,P_new_sellchargeId,P_new_buychargeId,NULL,'S',NULL,NULL,v_changedesc);
      END IF;
    END LOOP;
    CLOSE v_rs;
    --FOR K IN C0 LOOP
      EXECUTE IMMEDIATE ('UPDATE QMS_SELLCHARGESMASTER  SET IE_FLAG=''I'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
      ' TERMINALID IN ('||''''||v_terminals||''''||') AND IE_FLAG=''A''');
      DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);

      IF V_DUMMYFLAG = 'T'
        THEN
        DBMS_OUTPUT.PUT_LINE('V_DUMMYFLAG '||V_DUMMYFLAG);
              OPEN v_rs FOR ('SELECT BUYSELLCHARGEID, RATE_BREAK,CHARGEDESCID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
                ' TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N'' ');
              DBMS_OUTPUT.PUT_LINE('V_DUMMYFLAG '||V_DUMMYFLAG);
              LOOP
              FETCH v_rs INTO v_buychargeid,v_ratebreak,v_changedesc;
              DBMS_OUTPUT.PUT_LINE('v_ratebreak '||v_ratebreak);
              EXIT WHEN v_rs%NOTFOUND;
                IF v_ratebreak = P_RATE_BREAK THEN
                  QMS_QUOTEPACK_NEW.qms_quote_update(NULL,v_buychargeid,NULL,NULL,NULL,NULL,P_new_buychargeId,NULL,'B',NULL,NULL,v_changedesc);
                END IF;
              END LOOP;
              CLOSE v_rs;
              DBMS_OUTPUT.PUT_LINE('V_DUMMYFLAG '||V_DUMMYFLAG);
         EXECUTE IMMEDIATE ('UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG=''Y'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
         ' TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N''');
      END IF;
    --END LOOP;

  ELSE

    v_terminals:='';
    FOR i IN C1 LOOP
        v_terminals := v_terminals ||''''||i.terminalid||''''||',';
    END LOOP;
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||v_terminals);
    --DBMS_OUTPUT.PUT_LINE('v_terminals '||LENGTH(v_terminals));
    v_terminals := SUBSTR(v_terminals, 2, LENGTH(v_terminals) - 3);

      OPEN v_rs FOR ('SELECT SELLCHARGEID, BUYCHARGEID, RATE_BREAK,CHARGEDESCID FROM QMS_SELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
        ' TERMINALID IN ('||''''||v_terminals||''''||') AND IE_FLAG=''A'' ');

      LOOP
      FETCH v_rs INTO v_sellchargeid, v_buychargeid,v_ratebreak,v_changedesc;
      EXIT WHEN v_rs%NOTFOUND;
        IF v_ratebreak = P_RATE_BREAK THEN
          QMS_QUOTEPACK_NEW.qms_quote_update(v_sellchargeid,v_buychargeid,NULL,NULL,NULL,P_new_sellchargeId,P_new_buychargeId,NULL,'S',NULL,NULL,v_changedesc);
        END IF;
      END LOOP;
      CLOSE v_rs;
     --FOR K IN C1 LOOP
      EXECUTE IMMEDIATE ('UPDATE QMS_SELLCHARGESMASTER  SET IE_FLAG=''I'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
      ' TERMINALID IN ('||''''||v_terminals||''''||') AND IE_FLAG=''A''');
      IF V_DUMMYFLAG = 'T'
        THEN
           OPEN v_rs FOR ('SELECT BUYSELLCHARGEID, RATE_BREAK,CHARGEDESCID FROM QMS_BUYSELLCHARGESMASTER WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
            ' TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N'' ');

           LOOP
            FETCH v_rs INTO v_buychargeid,v_ratebreak,v_changedesc;
            EXIT WHEN v_rs%NOTFOUND;
            IF v_ratebreak = P_RATE_BREAK THEN
              QMS_QUOTEPACK_NEW.qms_quote_update(NULL,v_buychargeid,NULL,NULL,NULL,NULL,P_new_buychargeId,NULL,'B',NULL,NULL,v_changedesc);
            END IF;
          END LOOP;
          CLOSE v_rs;
         EXECUTE IMMEDIATE ('UPDATE QMS_BUYSELLCHARGESMASTER SET DEL_FLAG=''Y'' WHERE CHARGEDESCID='||''''||V_CHARGE_DESC_ID||''''||' AND'||
         ' TERMINALID IN ('||''''||v_terminals||''''||') AND DEL_FLAG=''N''');
      END IF;
    --END LOOP;
  END IF;
  EXCEPTION WHEN OTHERS THEN
    RETURN 100;
  END;

  RETURN 6;

  EXCEPTION WHEN OTHERS THEN
    RETURN 100;
  END SELLCHARGESADD;

/*
This procedure returns buy charges for the corresponding charge Id and
Charge Description Id in Sell Charges Add.

The IN Parameters are

V_CHARGE_ID VARCHAR2,
V_CHARGE_DESC_ID VARCHAR2,
V_TERMINALID VARCHAR2

The OUT Parameters are
MSTRLIST OUT PKG_QMS_CHARGES.CSCOLUMNS,
CHLDLIST OUT PKG_QMS_CHARGES.CSCOLUMNS

*/

PROCEDURE LOADBUYCHARGEDTLSFORSELL(V_CHARGE_ID VARCHAR2,
                                  V_CHARGE_DESC_ID VARCHAR2,
                                  V_TERMINALID VARCHAR2,
                                  MSTRLIST OUT PKG_QMS_CHARGES.CSCOLUMNS,
                                  CHLDLIST OUT PKG_QMS_CHARGES.CSCOLUMNS)

AS

MASTERQRY VARCHAR2(1000):='';
CHLDQRY VARCHAR2(1000):='';
V_COUNT NUMBER:=0;
DATATERMID  VARCHAR2(10):='';
BUYCHARGEID NUMBER:=0;
TEMPMSTRLIST PKG_QMS_CHARGES.CSCOLUMNS;


TEMP_CUR PKG_QMS_CHARGES.CSCOLUMNS;
CURSOR C2 IS
    SELECT Terminalid
      FROM FS_FR_TERMINALMASTER
     WHERE Terminalid = v_Terminalid
    UNION
    SELECT Parent_Terminal_Id Terminalid
      FROM FS_FR_TERMINAL_REGN
    CONNECT BY PRIOR Parent_Terminal_Id = Child_Terminal_Id
     START WITH Child_Terminal_Id = v_Terminalid
    UNION
    SELECT Terminalid
      FROM FS_FR_TERMINALMASTER
     WHERE Oper_Admin_Flag = 'H'
       AND Terminalid <> v_Terminalid;

CURSOR C3(TERM_ID VARCHAR2) IS
    SELECT Bsm.Buysellchargeid Buysellchargeid
      FROM QMS_BUYSELLCHARGESMASTER Bsm, QMS_CHARGE_BASISMASTER Bm
     WHERE Bsm.Charge_Id = v_Charge_Id
       AND Bsm.Chargedescid = v_Charge_Desc_Id
       AND Bsm.Chargebasis = Bm.Chargebasis
       AND Bsm.Del_Flag = 'N'
       AND Bsm.Terminalid = Term_Id;
BEGIN
  BEGIN
    MASTERQRY :=    'Select Bsm.Buysellchargeid,
                       Bsm.Charge_Id,
                       Bsm.Chargedescid,
                       Bsm.Chargebasis,
                       Bm.Basis_Description,
                       Bsm.Rate_Break,
                       Bsm.Rate_Type,
                       Bsm.Currency,
                       Bsm.Weight_Class,
                       Bsm.Density_Code,
                       Cb.Primary_Basis
                  FROM QMS_BUYSELLCHARGESMASTER Bsm,
                       QMS_CHARGE_BASISMASTER   Bm,
                       QMS_CHARGE_BASISMASTER   Cb
                 WHERE Bsm.Charge_Id = :v_Charge_Id
                   AND Bsm.Chargedescid = :v_Charge_Desc_Id
                   AND Bsm.Chargebasis = Bm.Chargebasis
                   AND Bsm.Del_Flag = ''N''
                   AND Cb.Chargebasis = Bsm.Chargebasis';

    CHLDQRY := 'Select Buysellchaegeid,
                          Chargerate,
                          Chargeslab,
                          Lowerbound,
                          Upperbound,
                          Chargerate_Indicator
                     FROM QMS_BUYCHARGESDTL';


      FOR I IN C2
      LOOP

       SELECT COUNT(*)
         INTO v_Count
         FROM QMS_BUYSELLCHARGESMASTER
        WHERE Charge_Id = v_Charge_Id
          AND Chargedescid = v_Charge_Desc_Id
          AND Terminalid = i.Terminalid
          AND Del_Flag = 'N';

        IF( V_COUNT > 0 ) THEN

            --OPEN TEMPMSTRLIST FOR MASTERQRY || ' AND BSM.TERMINALID='||''''||I.TERMINALID||'''';
            DATATERMID := I.TERMINALID;
            FOR J IN C3(I.TERMINALID)
            LOOP
              --OPEN TEMPCHLDLIST FOR CHLDQRY ||' WHERE BUYSELLCHAEGEID='||''''||J.BUYSELLCHARGEID||''''||' ORDER BY LANE_NO' ;
              BUYCHARGEID:=J.BUYSELLCHARGEID;
              EXIT;
            END LOOP;
        END IF;
      END LOOP;

      OPEN MSTRLIST FOR
                MASTERQRY || ' AND BSM.TERMINALID=:v_data_term_id'
                USING V_CHARGE_ID,V_CHARGE_DESC_ID,DATATERMID;

      OPEN CHLDLIST FOR
                CHLDQRY ||' WHERE BUYSELLCHAEGEID=:v_buy_charge_id ORDER BY LANE_NO' USING BUYCHARGEID;

  EXCEPTION WHEN OTHERS THEN
      MSTRLIST:=NULL;
      CHLDLIST:=NULL;
  END;
END LOADBUYCHARGEDTLSFORSELL;

/*
This procedure returns charge description Ids for
corresponding charge Id for the entire hierarchy based on the logged-in terminal.

The IN Parameters are

V_CHARGE_DESC_ID VARCHAR2,
V_CHARGE_ID VARCHAR2,
V_SHIPMODE VARCHAR2,
V_TERMINALID VARCHAR2

It Returns a resultset object CSCOLUMNS

*/

FUNCTION GETCHARGEDESCIDSALLLEVELS(P_CHARGE_DESC_ID VARCHAR2,
                                  P_CHARGE_ID VARCHAR2,
                                  P_SHIPMODE VARCHAR2,
                                  P_TERMINALID VARCHAR2,
                                  P_CHARGE_GRUOP_ID VARCHAR2)--@@Added by subrahmanyam for 195270 on 20-Jan-10
                                  RETURN PKG_QMS_CHARGES.CSCOLUMNS
AS
MASTERQRY VARCHAR2(2000):='';
V_CHARGE_ID QMS_CHARGEDESCMASTER.Chargeid%TYPE;
V_CHARGE_DESC_ID QMS_CHARGEDESCMASTER.Chargedescid%TYPE;
V_SHIPMODE   QMS_CHARGEDESCMASTER.Shipmentmode%TYPE;
V_TERMINALID QMS_CHARGEDESCMASTER.Terminalid%TYPE;
V_CHARGE_GROUP_ID QMS_CHARGE_GROUPSMASTER.CHARGEGROUP_ID%TYPE;--@@Added by subrahmanyam for 195270 on 20-Jan-10
RETURN_CUR PKG_QMS_CHARGES.CSCOLUMNS;
BEGIN
/*V_CHARGE_ID      := P_CHARGE_ID;
V_CHARGE_DESC_ID := P_CHARGE_DESC_ID || '%';
V_SHIPMODE       := P_SHIPMODE;
V_TERMINALID     := P_SHIPMODE;*/

--Modified By RajKumari on 13-11-2008 for WPBN 144811 changed the OPER_ADMIN_FLAG from 'H' to 'O'

--@@Commented by subrahmanyam for 195270 on 20-Jan-10

/*MASTERQRY:= ' SELECT DISTINCT CHARGEDESCID  FROM QMS_CHARGEDESCMASTER
             WHERE CHARGEID = :V_CHARGE_ID AND CHARGEDESCID LIKE :V_CHARGE_DESC_ID AND INACTIVATE =''N'' AND INVALIDATE=''F''
              AND TERMINALID  IN ( SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID  =:V_TERMINALID UNION  SELECT PARENT_TERMINAL_ID TERMINALID from fs_fr_terminal_regn
             connect by prior PARENT_TERMINAL_ID=CHILD_TERMINAL_ID start with CHILD_TERMINAL_ID=:V_TERMINALID UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=''H'' ) ';
*/
--@@Added by subrahmanyam for 195270 on 20-Jan-10
IF  (P_CHARGE_GRUOP_ID IS NULL OR P_CHARGE_GRUOP_ID='' ) THEN
MASTERQRY:= ' SELECT DISTINCT CHARGEDESCID  FROM QMS_CHARGEDESCMASTER
             WHERE CHARGEID = :V_CHARGE_ID AND CHARGEDESCID LIKE :V_CHARGE_DESC_ID AND INACTIVATE =''N'' AND INVALIDATE=''F''
              AND TERMINALID  IN ( SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID  =:V_TERMINALID UNION  SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
             CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID=:V_TERMINALID UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG=''H'' ) ';
ELSE
MASTERQRY:=  'SELECT DISTINCT CDM.CHARGEDESCID
               FROM QMS_CHARGEDESCMASTER CDM, QMS_CHARGE_GROUPSMASTER CGM
              WHERE CDM.CHARGEID = :V_CHARGE_ID
                AND CDM.CHARGEDESCID LIKE :V_CHARGE_DESC_ID
                AND CDM.INACTIVATE = ''N''
                AND CDM.INVALIDATE = ''F''
                AND CGM.CHARGEGROUP_ID= :V_CHARGE_GROUP_ID
                AND CGM.CHARGE_ID=CDM.CHARGEID
                AND CGM.CHARGEDESCID=CDM.CHARGEDESCID
                AND CDM.TERMINALID IN
                    (SELECT TERMINALID
                       FROM FS_FR_TERMINALMASTER
                      WHERE TERMINALID = :V_TERMINALID
                     UNION
                     SELECT PARENT_TERMINAL_ID TERMINALID
                       FROM FS_FR_TERMINAL_REGN
                     CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID
                      START WITH CHILD_TERMINAL_ID = :V_TERMINALID
                     UNION
                     SELECT TERMINALID
                       FROM FS_FR_TERMINALMASTER
                      WHERE OPER_ADMIN_FLAG = ''H'') ';
END IF;
--@@ended by subrahmanyam for 195270 on 20-Jan-10
  IF LENGTH(V_SHIPMODE)>0 THEN
      MASTERQRY := MASTERQRY || ' AND SHIPMENTMODE IN('|| V_SHIPMODE || ')';
  END IF;
  IF  (P_CHARGE_GRUOP_ID IS NULL OR P_CHARGE_GRUOP_ID='' ) THEN
  OPEN RETURN_CUR FOR MASTERQRY USING P_CHARGE_ID,P_CHARGE_DESC_ID||'%',P_TERMINALID,P_TERMINALID;
  ELSE
   OPEN RETURN_CUR FOR MASTERQRY USING P_CHARGE_ID,P_CHARGE_DESC_ID||'%',P_CHARGE_GRUOP_ID,P_TERMINALID,P_TERMINALID;
  END IF;
  RETURN RETURN_CUR;

END GETCHARGEDESCIDSALLLEVELS;

/*
This procedure is used to back update the sell charges when a new buy charge is added,
if a corresponding sell charge exists for the previous buy charge.

The IN Parameters are

p_oldbuychargeid         VARCHAR2,
p_newbuychargeid         VARCHAR2,
p_weightbreak            VARCHAR2,
p_ratetype               VARCHAR2,
p_terminal               VARCHAR2

The OUT Parameter is
p_exp              NUMBER

*/

   PROCEDURE buy_chages_bkup_proc (
      p_oldbuychargeid         VARCHAR2,
      p_newbuychargeid         VARCHAR2,
      p_weightbreak            VARCHAR2,
      p_ratetype               VARCHAR2,
      p_terminal               VARCHAR2,
      p_exp              OUT   NUMBER
   )
   IS
      v_rc             sys_refcursor;

      CURSOR c1
      IS
         (SELECT     parent_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY child_terminal_id = PRIOR parent_terminal_id
          START WITH child_terminal_id = p_terminal
          UNION
          SELECT terminalid term_id
            FROM FS_FR_TERMINALMASTER
           WHERE oper_admin_flag = 'H'
          UNION
          SELECT p_terminal term_id
            FROM DUAL
          UNION
          SELECT     child_terminal_id term_id
                FROM FS_FR_TERMINAL_REGN
          CONNECT BY PRIOR child_terminal_id = parent_terminal_id
          START WITH parent_terminal_id = p_terminal);

      CURSOR c2
      IS
         (SELECT DISTINCT terminalid
                     FROM FS_FR_TERMINALMASTER);

      v_opr_adm_flag   VARCHAR2 (30);
      v_terminals      VARCHAR2 (32767);
      v_sellchargeid   VARCHAR2 (30);
      v_omargin        VARCHAR2 (30);
      v_margintype     VARCHAR2 (30);
      v_marginbasis    VARCHAR2 (30);
      v_terminalid     VARCHAR2 (30);
      v_acclevel       VARCHAR2 (30);
      v_seq            NUMBER;
      v_seq1           NUMBER;
      v_rb             VARCHAR2 (40);
      v_rt             VARCHAR2 (40);
      v_mr             VARCHAR2 (40);
      v_ratebreak      VARCHAR2 (40);
      v_ratetype       VARCHAR2 (40);
      v_changedesc     VARCHAR2 (50);
      v_weightbreak    VARCHAR2 (50);
      v_chargebasis    VARCHAR2 (50);
      v_basis          VARCHAR2 (50);
   BEGIN

      p_exp:=1;

      SELECT oper_admin_flag
        INTO v_opr_adm_flag
        FROM FS_FR_TERMINALMASTER
       WHERE terminalid = p_terminal;
        SELECT qbm.rate_break,qbm.chargebasis INTO v_weightbreak,v_chargebasis FROM QMS_BUYSELLCHARGESMASTER qbm WHERE qbm.buysellchargeid=p_newbuychargeid;
      IF UPPER (TRIM (v_opr_adm_flag)) = 'H'
      THEN
         FOR i IN c2
         LOOP
            v_terminals := v_terminals || '''' || i.terminalid || '''' || ',';
         END LOOP;
      ELSE
         FOR i IN c1
         LOOP
            v_terminals := v_terminals || '''' || i.term_id || '''' || ',';
         END LOOP;
      END IF;

      v_terminals := SUBSTR (v_terminals, 1, LENGTH (v_terminals) - 1);
      --DBMS_OUTPUT.put_line('v_terminals '||v_terminals);

      OPEN v_rc
       FOR    'SELECT SELLCHARGEID,CHARGEBASIS,RATE_BREAK,RATE_TYPE,OVERALL_MARGIN, MARGIN_TYPE, MARGIN_BASIS, TERMINALID,ACCESSLEVEL,CHARGEDESCID FROM QMS_SELLCHARGESMASTER WHERE BUYCHARGEID='
           || ''''
           || p_oldbuychargeid
           || ''''
           || ' AND TERMINALID in ('
           || v_terminals
           || ') and upper(IE_FLAG)=''A''';

      --DBMS_OUTPUT.put_line('v_terminals '||v_terminals);

      LOOP
         FETCH v_rc
          INTO v_sellchargeid,v_basis,v_ratebreak,v_ratetype, v_omargin, v_margintype, v_marginbasis,
               v_terminalid, v_acclevel,v_changedesc;

         EXIT WHEN v_rc%NOTFOUND;

        --DBMS_OUTPUT.put_line('v_sellchargeid '||v_sellchargeid);

        /*IF UPPER(p_weightbreak) = UPPER(v_ratebreak)
        THEN*/

                   /**SELECT sellchargeid_seq.NEXTVAL
                     INTO v_seq
                     FROM DUAL;*/--@@Commented by kameswari for the WPBN issue-154398 on 09/03/09
                     SELECT sellchargeaccid_seq.NEXTVAL
                     INTO v_seq
                     FROM DUAL;

          --         DBMS_OUTPUT.put_line (v_seq);
                   /**INSERT INTO qms_sellchargesmaster
                               (sellchargeid, charge_id, chargebasis, rate_break,
                                rate_type, currency, weight_class, created_by,
                                created_tstmp, terminalid, chargedescid, overall_margin,
                                margin_type, margin_basis, ie_flag, buychargeid,
                                dummy_sell_charges_flag)
                      SELECT v_seq, charge_id, chargebasis, rate_break, rate_type,
                             currency, weight_class, last_updated_by,
                             last_updated_tstmp, v_terminalid terminalid, chargedescid,
                             v_omargin, v_margintype, v_marginbasis, 'A' ie_flag,
                             p_newbuychargeid, 'F' dummy_sell_charges_flag
                        FROM qms_buysellchargesmaster
                       WHERE buysellchargeid = p_newbuychargeid;*/
                 INSERT INTO QMS_SELLCHARGESMASTER_ACC
                               (sellchargeaccid, charge_id, chargebasis, rate_break,
                                rate_type, currency, weight_class, created_by,
                                created_tstmp, terminalid, chargedescid, overall_margin,
                                margin_type, margin_basis, ie_flag, buychargeid,sellchargeid
                                )
                      SELECT v_seq, charge_id, chargebasis, rate_break, rate_type,
                             currency, weight_class, last_updated_by,
                             last_updated_tstmp, v_terminalid terminalid, chargedescid,
                             v_omargin, v_margintype, v_marginbasis, 'A' ie_flag,
                             p_newbuychargeid, v_sellchargeid
                        FROM QMS_BUYSELLCHARGESMASTER
                       WHERE buysellchargeid = p_newbuychargeid;
                  --@@Commented by kameswari on 09/02/09
                  --QMS_QUOTEPACK_NEW.qms_quote_update(v_sellchargeid,p_oldbuychargeid,NULL,NULL,NULL,v_seq,p_newbuychargeid,NULL,'S',NULL,NULL,v_changedesc);


                   /*UPDATE qms_sellchargesmaster
                      SET ie_flag = 'I'
                    WHERE sellchargeid = v_sellchargeid;*/
                       --@@Commented and Modified by kameswari on 09/02/09

                    UPDATE QMS_SELLCHARGESMASTER
                      SET acceptance_flag = 'Y'
                    WHERE sellchargeid = v_sellchargeid;

                   FOR k IN (SELECT   v_seq SID, qbd.chargerate, qbd.chargeslab, qbd.lowerbound,
                                      qbd.upperbound, qbd.lane_no, qbd.chargerate_indicator
                                 FROM QMS_BUYCHARGESDTL qbd
                                WHERE qbd.buysellchaegeid = p_newbuychargeid
                                ORDER BY lane_no)
                   LOOP
                      SELECT seq_sellchargesdtl_acc.NEXTVAL
                        INTO v_seq1
                        FROM DUAL;

                      /*INSERT INTO qms_sellchargesdtl
                                  (sellchargeid, chargerate, chargeslab, lowerbound,
                                   upperbound, lane_no, chargerate_indicator,marginvalue,
                                   ID
                                  )
                           VALUES (k.SID, k.chargerate, k.chargeslab, k.lowerbound,
                                   k.upperbound, k.lane_no, k.chargerate_indicator,0.0,
                                   v_seq1
                                  );*/
                    INSERT INTO QMS_SELLCHARGESDTL_ACC
                                     (sellchargeaccid,chargerate, chargeslab, lowerbound,marginvalue,
                                      upperbound, ID, sellchargeid, lane_no
                                     )
                              VALUES (v_seq,k.chargerate, k.chargeslab, k.lowerbound,0.0,
                                      k.upperbound, v_seq1, v_sellchargeid, k.lane_no
                                     );
                   END LOOP;
                   IF UPPER(v_weightbreak)=UPPER(v_ratebreak) AND UPPER(v_chargebasis)=UPPER(v_basis)
                   THEN
                      FOR j IN (SELECT marginvalue,lane_no FROM QMS_SELLCHARGESDTL WHERE sellchargeid=v_sellchargeid)
                      LOOP
                      UPDATE QMS_SELLCHARGESDTL_ACC SET marginvalue=j.marginvalue WHERE sellchargeid=v_sellchargeid AND lane_no=j.lane_no;
                      END LOOP;
                    END IF;
                       /*SELECT rate_break, rate_type, margin_basis
                     INTO v_rb, v_rt, v_mr
                     FROM qms_sellchargesmaster
                    WHERE sellchargeid = v_seq;

                   IF     (UPPER (v_rb) = 'FLAT' OR UPPER (v_rb) = 'FLAT%')
                      AND UPPER (v_rt) = 'FLAT'
                      AND UPPER (v_mr) = 'V'
                   THEN
                      FOR k IN (SELECT   chargeslab, chargerate, lowerbound,marginvalue,
                                         upperbound, lane_no
                                    FROM qms_sellchargesdtl
                                   WHERE sellchargeid = v_sellchargeid
                                     AND UPPER (chargeslab) NOT IN
                                                         ('BASE', 'MIN', 'MAX', 'FLAT')
                                ORDER BY lane_no)
                      LOOP
                         SELECT seq_sellchargesdtl.NEXTVAL
                           INTO v_seq1
                           FROM DUAL;

                         -- DBMS_OUTPUT.put_line (v_seq || '          ' || k.lane_no);
                         INSERT INTO qms_sellchargesdtl
                                     (chargerate, chargeslab, lowerbound,
                                      upperbound, ID, sellchargeid, lane_no
                                     )
                              VALUES (k.chargerate, k.chargeslab, k.lowerbound,
                                      k.upperbound, v_seq1, v_seq, k.lane_no
                                     );
                            INSERT INTO qms_sellchargesdtl_acc
                                     (sellchargeaccid,chargerate, chargeslab, marginvalue,lowerbound,
                                      upperbound, ID, sellchargeid, lane_no
                                     )
                              VALUES (v_seq,k.chargerate, k.chargeslab, NVL(k.marginvalue,0.0),k.lowerbound,
                                      k.upperbound, v_seq1, v_sellchargeid, k.lane_no
                                     );
                      END LOOP;
                   END IF;

                   UPDATE qms_sellchargesdtl sd
                      SET sd.marginvalue =
                             NVL((SELECT sdi.marginvalue
                                FROM qms_sellchargesdtl sdi
                               WHERE sdi.sellchargeid = v_sellchargeid
                                 AND sd.chargeslab=sdi.chargeslab),0.0)
                    WHERE sd.sellchargeid = v_seq;
               ELSE
                  UPDATE qms_sellchargesmaster
                      SET ie_flag = 'I'
                    WHERE sellchargeid = v_sellchargeid;*/


              --END IF;

            END LOOP;
            CLOSE v_rc;
      p_exp := 1;
   EXCEPTION
      WHEN NO_DATA_FOUND
      THEN
         p_exp := 0;
      WHEN OTHERS
      THEN
         p_exp := 0;
   END;
 /*
   Used For Charge Description Upload -- Modify. For validating, updating
   appropriate error message back to the user along with the corresponding data.
 */
PROCEDURE UPDATE_CHARGEDESC_DTLS(p_currterminalid VARCHAR2,
                                   p_rs_1           OUT CSCOLUMNS,
                                   p_rs_2           OUT CSCOLUMNS) AS

  v_returnval    VARCHAR2(10);
  v_shipmentmode VARCHAR2(10);
  v_internalname VARCHAR2(50);
  v_externalname VARCHAR2(50);

  CURSOR c1 IS(
    SELECT CHARGE_ID,
           CHARGEDESCID,
           TERMINALID,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME
      FROM TEMP_CHARGES);
BEGIN
  FOR i IN c1 LOOP
    BEGIN
      ISEXISTINTHEHIRARCHY('Modify',
                           i.TERMINALID,
                           p_currterminalid,
                           v_returnval);
    END;
    IF v_returnval = '1' THEN
      BEGIN
        SELECT SHIPMENTMODE, REMARKS, EXT_CHARGE_NAME
          INTO v_shipmentmode, v_internalname, v_externalname
          FROM QMS_CHARGEDESCMASTER
         WHERE CHARGEID = i.CHARGE_ID
           AND CHARGEDESCID = i.CHARGEDESCID
           AND INVALIDATE = 'F'
           AND INACTIVATE = 'N'
           AND TERMINALID = i.TERMINALID;

        UPDATE QMS_CHARGEDESCMASTER
           SET REMARKS         = i.INT_CHARGE_NAME,
               EXT_CHARGE_NAME = i.EXT_CHARGE_NAME
         WHERE CHARGEID = i.CHARGE_ID
           AND CHARGEDESCID = i.CHARGEDESCID
           AND TERMINALID = i.TERMINALID
           AND INACTIVATE = 'N';

        UPDATE TEMP_CHARGES
           SET SELECTED_FLAG = 'Y'
         WHERE CHARGE_ID = i.CHARGE_ID
           AND CHARGEDESCID = i.CHARGEDESCID;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          UPDATE TEMP_CHARGES
             SET SELECTED_FLAG = 'N',
                 NOTES         = 'The Given Charge Id  Description Id Do Not Belong to this Terminal.'
           WHERE CHARGE_ID = i.CHARGE_ID
             AND CHARGEDESCID = i.CHARGEDESCID;
      END;
    ELSE
      UPDATE TEMP_CHARGES
         SET SELECTED_FLAG = 'N', NOTES = 'The Terminal Id is Invalid or Does Not Belong in this Hierarchy.'
       WHERE CHARGE_ID = i.CHARGE_ID
         AND CHARGEDESCID = i.CHARGEDESCID;
    END IF;
  END LOOP;
  --Opening the Cursor For Charges Which Have Been Updated.
  OPEN p_rs_1 FOR SELECT CHARGE_ID,CHARGEDESCID ,
           TERMINALID,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME
      FROM TEMP_CHARGES WHERE SELECTED_FLAG='Y' ;
  --Opening the Cursor For Charges Which Have Not Been Updated.
  OPEN p_rs_2 FOR SELECT CHARGE_ID,CHARGEDESCID ,
           TERMINALID,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           NOTES
      FROM TEMP_CHARGES WHERE SELECTED_FLAG='N';
END;

PROCEDURE CHARGEGROUP_DTL_PROC(p_process        VARCHAR2,
                               p_currterminalid VARCHAR2,
                               p_rs_1           OUT CSCOLUMNS,
                               p_rs_2           OUT CSCOLUMNS) AS
  v_remarks       VARCHAR2(150);
  v_return_flag   VARCHAR2(1);
  v_shipment_mode VARCHAR2(1);

  CURSOR c1 IS(
    SELECT CHARGE_GROUP_ID,
           CHARGE_ID,
           CHARGEDESCID,
           SHIPMENTMODE,
           TERMINALID,
           ORIGINCOUNTRY,
           DESTINATIONCOUNTRY
      FROM TEMP_CHARGES_GROUP);--@@Temp_Chargers is Modified by Temp_Charges_Group by Anil.k for CR 231214

BEGIN
  FOR i IN c1 LOOP
    BEGIN
      IF UPPER(p_process) = 'ADD' THEN
      --@@Modified by Anil.k for CR 231214 on 25Jan2011
        VALIDATE_CHARGE_GROUP_DTL_ADD(i.CHARGE_GROUP_ID,
                                      i.CHARGE_ID,
                                      i.CHARGEDESCID,
                                      i.SHIPMENTMODE,
                                      i.TERMINALID,
                                      i.ORIGINCOUNTRY,
                                      i.DESTINATIONCOUNTRY,
                                      v_return_flag,
                                      v_remarks);
      ELSE
        VALIDATE_CHARGEGRP_DTL_MODIFY(i.CHARGE_GROUP_ID,
                                      i.CHARGE_ID,
                                      i.CHARGEDESCID,
                                      i.TERMINALID,
                                      p_currterminalid,
                                      i.ORIGINCOUNTRY,
                                      i.DESTINATIONCOUNTRY,
                                      v_return_flag,
                                      v_shipment_mode,
                                      v_remarks);
      END IF;

    END;
    IF v_return_flag = '1'
     THEN
        IF UPPER(p_process) = 'ADD' THEN
          UPDATE TEMP_CHARGES_GROUP
             SET SELECTED_FLAG = 'Y'
           WHERE CHARGE_GROUP_ID = i.CHARGE_GROUP_ID
             AND CHARGE_ID = i.CHARGE_ID
             AND CHARGEDESCID = i.CHARGEDESCID;

        ELSE
          UPDATE TEMP_CHARGES_GROUP
             SET SELECTED_FLAG = 'Y',SHIPMENTMODE = v_shipment_mode
           WHERE CHARGE_GROUP_ID = i.CHARGE_GROUP_ID
             AND CHARGE_ID = i.CHARGE_ID
             AND CHARGEDESCID = i.CHARGEDESCID;
         END IF;
    ELSE
      /*UPDATE TEMP_CHARGES
         SET SELECTED_FLAG = 'N'
       WHERE CHARGE_GROUP_ID = i.CHARGE_GROUP_ID;*/

      UPDATE TEMP_CHARGES_GROUP
         SET NOTES = v_remarks
       WHERE CHARGE_GROUP_ID = i.CHARGE_GROUP_ID
         AND CHARGE_ID = i.CHARGE_ID
         AND CHARGEDESCID = i.CHARGEDESCID;
    END IF;
  END LOOP;
  --@@ Modified by Anil.k for Enhancement 231214 on 25Jan2011
  OPEN p_rs_1 FOR SELECT CHARGE_GROUP_ID,CHARGE_ID ,
           CHARGEDESCID,
           SHIPMENTMODE,
           TERMINALID,
           SHMODE,
           ORIGINCOUNTRY,
           DESTINATIONCOUNTRY,
           SELECTED_FLAG
      FROM TEMP_CHARGES_GROUP WHERE SELECTED_FLAG='Y' ;
  --@@ Modified by Anil.k for Enhancement 231214 on 25Jan2011
  OPEN p_rs_2 FOR SELECT CHARGE_GROUP_ID,CHARGE_ID ,
           CHARGEDESCID,
           SHIPMENTMODE,
           TERMINALID,
           SHMODE,
           NOTES,
           ORIGINCOUNTRY,
           DESTINATIONCOUNTRY,
           SELECTED_FLAG
      FROM TEMP_CHARGES_GROUP WHERE SELECTED_FLAG='N' ;

END;

PROCEDURE VALIDATE_CHARGE_DESC(p_currterminalid VARCHAR2,
                               p_rs_1           OUT CSCOLUMNS,
                               p_rs_2           OUT CSCOLUMNS) AS

  v_opr_adm_flag      VARCHAR2(1);
  v_shipment_mode_str VARCHAR2(50);
  v_charge_id         VARCHAR2(10);
  v_charge_desc_id    VARCHAR2(50);
  v_terminal_id_str   VARCHAR2(400);

  CURSOR c1 IS(
    SELECT CHARGE_ID,
           CHARGEDESCID,
           SHIPMENTMODE,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME
      FROM TEMP_CHARGES);

BEGIN
  SELECT oper_admin_flag
    INTO v_opr_adm_flag
    FROM FS_FR_TERMINALMASTER
   WHERE terminalid = p_currterminalid;

  IF v_opr_adm_flag = 'H' THEN
    v_terminal_id_str := 'SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG=''A'' ';
  ELSE
    v_terminal_id_str := 'SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID=' || '''' ||
                         p_currterminalid || '''';
  END IF;

  FOR i IN c1 LOOP
    IF i.SHIPMENTMODE = '1' THEN
      v_shipment_mode_str := '1,3,5,7';
    ELSIF i.SHIPMENTMODE = '2' THEN
      v_shipment_mode_str := '2,3,6,7';
    ELSIF i.SHIPMENTMODE = '3' THEN
      v_shipment_mode_str := '3,7';
    ELSIF i.SHIPMENTMODE = '4' THEN
      v_shipment_mode_str := '4,5,6,7';
    ELSIF i.SHIPMENTMODE = '5' THEN
      v_shipment_mode_str := '5,7';
    ELSIF i.SHIPMENTMODE = '6' THEN
      v_shipment_mode_str := '6,7';
    ELSIF i.SHIPMENTMODE = '7' THEN
      v_shipment_mode_str := '7';
    END IF;
    BEGIN



      EXECUTE IMMEDIATE ('  SELECT DISTINCT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                            AND SHIPMENT_MODE IN ( '
                           || v_shipment_mode_str
                           || ')'
                           ||'AND (INVALIDATE = ''F'' OR INVALIDATE IS NULL) ')
       INTO v_charge_id USING i.charge_id;


      BEGIN
        SELECT DISTINCT CHARGEDESCID
          INTO v_charge_desc_id
          FROM QMS_CHARGEDESCMASTER
         WHERE CHARGEDESCID = i.CHARGEDESCID
           AND INACTIVATE = 'N'
           AND TERMINALID IN
               (SELECT TERMINALID
                  FROM FS_FR_TERMINALMASTER
                 WHERE TERMINALID = p_currterminalid
                UNION
                SELECT PARENT_TERMINAL_ID TERMINALID
                  FROM FS_FR_TERMINAL_REGN
                CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID
                 START WITH CHILD_TERMINAL_ID = p_currterminalid
                UNION
                SELECT TERMINALID
                  FROM FS_FR_TERMINALMASTER
                 WHERE OPER_ADMIN_FLAG = 'H');

        UPDATE TEMP_CHARGES
           SET SELECTED_FLAG = 'N',
               NOTES         = 'This Charge Description Id Already Exists.'
         WHERE CHARGE_ID = i.CHARGE_ID
           AND CHARGEDESCID = i.CHARGEDESCID;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN


          EXECUTE IMMEDIATE (' UPDATE QMS_CHARGEDESCMASTER SET INACTIVATE = ''Y'' WHERE CHARGEDESCID =:v_charge_desc_id
                               AND TERMINALID IN ('
                               || v_terminal_id_str
                               || ')'
                             ) USING i.CHARGEDESCID;


          UPDATE TEMP_CHARGES
             SET SELECTED_FLAG = 'Y'
           WHERE CHARGE_ID = i.CHARGE_ID
             AND CHARGEDESCID = i.CHARGEDESCID;
      END;

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        --DBMS_OUTPUT.put_line('No Data Found!');
        UPDATE TEMP_CHARGES
           SET SELECTED_FLAG = 'N', NOTES = 'This Charge Id is Invalid.'
         WHERE CHARGE_ID = i.CHARGE_ID
           AND CHARGEDESCID = i.CHARGEDESCID;
    END;
  END LOOP;
  --Opening the Cursor For Charges Which Have Been Updated.
  OPEN p_rs_1 FOR SELECT CHARGE_ID,CHARGEDESCID ,
           SHIPMENTMODE,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           SHMODE
      FROM TEMP_CHARGES WHERE SELECTED_FLAG='Y' ;
  --Opening the Cursor For Charges Which Have Not Been Updated.
  OPEN p_rs_2 FOR SELECT CHARGE_ID,CHARGEDESCID ,
           SHIPMENTMODE,
           INT_CHARGE_NAME,
           EXT_CHARGE_NAME,
           NOTES,
           SHMODE
      FROM TEMP_CHARGES WHERE SELECTED_FLAG='N';
END;

PROCEDURE VALIDATE_CHARGE_GROUP_DTL_ADD(p_charge_group_id VARCHAR2,
                                    p_charge_id       VARCHAR2,
                                    p_charge_desc_id  VARCHAR2,
                                    p_shipment_mode   VARCHAR2,
                                    p_terminal_id     VARCHAR2,
                                    p_success_flag    OUT VARCHAR2,
                                    p_remarks         OUT VARCHAR2) AS
  v_opr_adm_flag         VARCHAR2(1);
  v_charge_group_id_flag VARCHAR2(1);
  v_charge_group_id      VARCHAR2(40);
  v_charge_id_flag       VARCHAR2(1);
  v_charge_id            VARCHAR2(10);
  v_charge_desc_id_flag  VARCHAR2(1);
  v_charge_desc_id       VARCHAR2(50);
  v_shipment_mode_str    VARCHAR2(50);
  v_terminal_id_str      VARCHAR2(400);

BEGIN
  SELECT oper_admin_flag
    INTO v_opr_adm_flag
    FROM FS_FR_TERMINALMASTER
   WHERE terminalid = p_terminal_id;

  /*IF v_opr_adm_flag='H' THEN
     v_terminal_id_str := 'SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG=''A'' ';
  ELSE
     v_terminal_id_str := 'SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='
     ||''''
     ||p_terminal_id
     ||'''';
  END IF;*/

  IF p_shipment_mode = '1' THEN
    v_shipment_mode_str := '1,3,5,7';
  ELSIF p_shipment_mode = '2' THEN
    v_shipment_mode_str := '2,3,6,7';
  ELSIF p_shipment_mode = '3' THEN
    v_shipment_mode_str := '3,7';
  ELSIF p_shipment_mode = '4' THEN
    v_shipment_mode_str := '4,5,6,7';
  ELSIF p_shipment_mode = '5' THEN
    v_shipment_mode_str := '5,7';
  ELSIF p_shipment_mode = '6' THEN
    v_shipment_mode_str := '6,7';
  ELSIF p_shipment_mode = '7' THEN
    v_shipment_mode_str := '7';
  END IF;

  BEGIN
    SELECT DISTINCT CHARGEGROUP_ID
      INTO v_charge_group_id
      FROM QMS_CHARGE_GROUPSMASTER
     WHERE CHARGEGROUP_ID = p_charge_group_id
       AND INACTIVATE = 'N'
       AND TERMINALID IN
           (SELECT TERMINALID
              FROM FS_FR_TERMINALMASTER
             WHERE TERMINALID = p_terminal_id
            UNION
            SELECT PARENT_TERMINAL_ID TERMINALID
              FROM FS_FR_TERMINAL_REGN
            CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID
             START WITH CHILD_TERMINAL_ID = p_terminal_id
            UNION
            SELECT TERMINALID
              FROM FS_FR_TERMINALMASTER
             WHERE OPER_ADMIN_FLAG = 'H');

    v_charge_group_id_flag := '0';
    p_remarks              := 'This Charge Group Id Already Exists.';

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      v_charge_group_id_flag := '1';
      p_remarks              := '';
  END;

  IF v_charge_group_id_flag = 1 THEN
    BEGIN


      EXECUTE IMMEDIATE ('  SELECT DISTINCT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                            AND SHIPMENT_MODE IN ( '
                           || v_shipment_mode_str
                           || ')'
                           ||'AND INVALIDATE = ''F''')
       INTO v_charge_id USING p_charge_id;

      v_charge_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Charge Id Has Been Invalidated or Does Not Exist.';
    END;
    --dbms_output.put_line('p_remarks '||p_remarks);
    BEGIN
      EXECUTE IMMEDIATE ('  SELECT CHARGEDESCID FROM QMS_CHARGEDESCMASTER WHERE CHARGEID =:v_charge_id
                            AND CHARGEDESCID =:v_charge_desc_id AND SHIPMENTMODE IN ( '
                           || v_shipment_mode_str
                           || ') AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER
                               WHERE TERMINALID =:v_terminal_id
                               UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
                               CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID START WITH
                               CHILD_TERMINAL_ID =:v_terminal_id UNION
                               SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = ''H'')
                               AND INACTIVATE = ''N'''
                         )
       INTO v_charge_desc_id USING p_charge_id,p_charge_desc_id,p_terminal_id,p_terminal_id;

      v_charge_desc_id_flag := '1';
      p_remarks             := p_remarks || '';


    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_desc_id_flag := '0';
        p_remarks             := p_remarks ||
                                 'This Charge Description Id is Invalid or Does Not Exist.';
    END;
  END IF;
  IF (v_charge_group_id_flag = '1' AND v_charge_id_flag='1' AND v_charge_desc_id_flag='1') THEN

      IF v_opr_adm_flag='H' THEN
         UPDATE QMS_CHARGE_GROUPSMASTER
            SET Inactivate = 'Y'
          WHERE Chargegroup_Id = p_Charge_Group_Id
            AND Inactivate = 'N';
      ELSE
          UPDATE QMS_CHARGE_GROUPSMASTER
             SET Inactivate = 'Y'
           WHERE Chargegroup_Id = p_Charge_Group_Id
             AND Terminalid IN
                 (SELECT Child_Terminal_Id Terminalid
                    FROM FS_FR_TERMINAL_REGN
                  CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                   START WITH Parent_Terminal_Id = p_Terminal_Id)
             AND Inactivate = 'N';
      END IF;

      /*EXECUTE IMMEDIATE ( ' UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE = ''Y''  WHERE CHARGEGROUP_ID =:v_charge_group_id
                            AND TERMINALID IN ( '
                          || v_terminal_id_str
                          || ' )AND INACTIVATE = ''N'' '
                         ) Using p_charge_group_id;*/

        p_success_flag := '1';
  ELSE
        p_success_flag := '0';
  END IF;
END;

--@@ Added by Amil.k for CR 231214 on 25Jan2011
PROCEDURE VALIDATE_CHARGE_GROUP_DTL_ADD(p_charge_group_id VARCHAR2,
                                    p_charge_id       VARCHAR2,
                                    p_charge_desc_id  VARCHAR2,
                                    p_shipment_mode   VARCHAR2,
                                    p_terminal_id     VARCHAR2,
                                    p_orgCountry_id   VARCHAR2,
                                    p_destCountry_id  VARCHAR2,
                                    p_success_flag    OUT VARCHAR2,
                                    p_remarks         OUT VARCHAR2) AS
  v_opr_adm_flag         VARCHAR2(1);
  v_charge_group_id_flag VARCHAR2(1);
  v_charge_group_id      VARCHAR2(40);
  v_charge_id_flag       VARCHAR2(1);
  v_charge_id            VARCHAR2(10);
  v_charge_desc_id_flag  VARCHAR2(1);
  v_charge_desc_id       VARCHAR2(50);
  v_shipment_mode_str    VARCHAR2(50);
  v_terminal_id_str      VARCHAR2(400);
  v_orgCountry_id        VARCHAR2(60);
  v_destCountry_id       VARCHAR2(60);
  v_orgCountry_id_flag   VARCHAR2(1);
  v_destCountry_id_flag  VARCHAR2(1);

BEGIN
  SELECT oper_admin_flag
    INTO v_opr_adm_flag
    FROM FS_FR_TERMINALMASTER
   WHERE terminalid = p_terminal_id;

  /*IF v_opr_adm_flag='H' THEN
     v_terminal_id_str := 'SELECT TERMINALID  FROM FS_FR_TERMINALMASTER WHERE ACTV_FLAG=''A'' ';
  ELSE
     v_terminal_id_str := 'SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='
     ||''''
     ||p_terminal_id
     ||'''';
  END IF;*/

  IF p_shipment_mode = '1' THEN
    v_shipment_mode_str := '1,3,5,7';
  ELSIF p_shipment_mode = '2' THEN
    v_shipment_mode_str := '2,3,6,7';
  ELSIF p_shipment_mode = '3' THEN
    v_shipment_mode_str := '3,7';
  ELSIF p_shipment_mode = '4' THEN
    v_shipment_mode_str := '4,5,6,7';
  ELSIF p_shipment_mode = '5' THEN
    v_shipment_mode_str := '5,7';
  ELSIF p_shipment_mode = '6' THEN
    v_shipment_mode_str := '6,7';
  ELSIF p_shipment_mode = '7' THEN
    v_shipment_mode_str := '7';
  END IF;

  BEGIN
    SELECT DISTINCT CHARGEGROUP_ID
      INTO v_charge_group_id
      FROM QMS_CHARGE_GROUPSMASTER
     WHERE CHARGEGROUP_ID = p_charge_group_id
       AND INACTIVATE = 'N'
       AND TERMINALID IN
           (SELECT TERMINALID
              FROM FS_FR_TERMINALMASTER
             WHERE TERMINALID = p_terminal_id
            UNION
            SELECT PARENT_TERMINAL_ID TERMINALID
              FROM FS_FR_TERMINAL_REGN
            CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID
             START WITH CHILD_TERMINAL_ID = p_terminal_id
            UNION
            SELECT TERMINALID
              FROM FS_FR_TERMINALMASTER
             WHERE OPER_ADMIN_FLAG = 'H');

    v_charge_group_id_flag := '0';
    p_remarks              := 'This Charge Group Id Already Exists.';

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      v_charge_group_id_flag := '1';
      p_remarks              := '';
  END;

  IF v_charge_group_id_flag = 1 THEN
    BEGIN


      EXECUTE IMMEDIATE ('  SELECT DISTINCT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                            AND SHIPMENT_MODE IN ( '
                           || v_shipment_mode_str
                           || ')'
                           ||'AND INVALIDATE = ''F''')
       INTO v_charge_id USING p_charge_id;

      v_charge_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Charge Id Has Been Invalidated or Does Not Exist.';
    END;
    --dbms_output.put_line('p_remarks '||p_remarks);
    BEGIN
      EXECUTE IMMEDIATE ('  SELECT CHARGEDESCID FROM QMS_CHARGEDESCMASTER WHERE CHARGEID =:v_charge_id
                            AND CHARGEDESCID =:v_charge_desc_id AND SHIPMENTMODE IN ( '
                           || v_shipment_mode_str
                           || ') AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER
                               WHERE TERMINALID =:v_terminal_id
                               UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
                               CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID START WITH
                               CHILD_TERMINAL_ID =:v_terminal_id UNION
                               SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = ''H'')
                               AND INACTIVATE = ''N'''
                         )
       INTO v_charge_desc_id USING p_charge_id,p_charge_desc_id,p_terminal_id,p_terminal_id;

      v_charge_desc_id_flag := '1';
      p_remarks             := p_remarks || '';


    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_desc_id_flag := '0';
        p_remarks             := p_remarks ||
                                 'This Charge Description Id is Invalid or Does Not Exist.';
    END;
  END IF;

  IF v_charge_desc_id_flag = 1 AND v_charge_id_flag = 1 THEN

    BEGIN

      EXECUTE IMMEDIATE (' SELECT COUNTRYID FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID =:
                            p_destCountry_id')
       INTO v_destCountry_id USING p_destCountry_id;

      v_destCountry_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_destCountry_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Country Does Not Exist.';
    END;

    BEGIN

      EXECUTE IMMEDIATE (' SELECT COUNTRYID FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID =:
                            p_orgCountry_id')
       INTO v_orgCountry_id USING p_orgCountry_id;

      v_orgCountry_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_orgCountry_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Country Does Not Exist.';
    END;

    END IF;
  IF (v_charge_group_id_flag = '1' AND v_charge_id_flag='1' AND v_charge_desc_id_flag='1' AND v_orgCountry_id_flag='1' AND v_destCountry_id_flag='1') THEN

      IF v_opr_adm_flag='H' THEN
         UPDATE QMS_CHARGE_GROUPSMASTER
            SET Inactivate = 'Y'
          WHERE Chargegroup_Id = p_Charge_Group_Id
            AND Inactivate = 'N';
      ELSE
          UPDATE QMS_CHARGE_GROUPSMASTER
             SET Inactivate = 'Y'
           WHERE Chargegroup_Id = p_Charge_Group_Id
             AND Terminalid IN
                 (SELECT Child_Terminal_Id Terminalid
                    FROM FS_FR_TERMINAL_REGN
                  CONNECT BY PRIOR Child_Terminal_Id = Parent_Terminal_Id
                   START WITH Parent_Terminal_Id = p_Terminal_Id)
             AND Inactivate = 'N';
      END IF;

      /*EXECUTE IMMEDIATE ( ' UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE = ''Y''  WHERE CHARGEGROUP_ID =:v_charge_group_id
                            AND TERMINALID IN ( '
                          || v_terminal_id_str
                          || ' )AND INACTIVATE = ''N'' '
                         ) Using p_charge_group_id;*/

        p_success_flag := '1';
  ELSE
        p_success_flag := '0';
  END IF;
END;
--@@Ended by Anil.k for CR 231214 on 24Jan2011

PROCEDURE VALIDATE_CHARGEGRP_DTL_MODIFY(p_charge_group_id VARCHAR2,
                                        p_charge_id       VARCHAR2,
                                        p_charge_desc_id  VARCHAR2,
                                        p_terminal_id     VARCHAR2,
                                        p_currterminalid  VARCHAR2,
                                        p_success_flag    OUT VARCHAR2,
                                        p_shipment_mode   OUT VARCHAR2,
                                        p_remarks         OUT VARCHAR2) AS
  v_returnval            VARCHAR2(1);
  v_charge_group_id_flag VARCHAR2(1);
  v_charge_group_id      VARCHAR2(40);
  v_charge_id_flag       VARCHAR2(1);
  v_charge_id            VARCHAR2(10);
  v_charge_desc_id_flag  VARCHAR2(1);
  v_charge_desc_id       VARCHAR2(50);
  v_shipment_mode_str    VARCHAR2(50);
BEGIN

  ISEXISTINTHEHIRARCHY('Modify',
                       p_terminal_id,
                       p_currterminalid,
                       v_returnval);
  IF v_returnval = '1' THEN
    BEGIN
      SELECT DISTINCT CHARGEGROUP_ID
        INTO v_charge_group_id
        FROM QMS_CHARGE_GROUPSMASTER
       WHERE CHARGEGROUP_ID = p_charge_group_id
         AND INACTIVATE = 'N'
         AND TERMINALID = p_terminal_id;

      v_charge_group_id_flag := '1';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_group_id_flag := '0';
        p_remarks              := 'The Charge Group Id is Invalid or Does Not Belong to this Terminal.';
    END;
    IF v_charge_group_id_flag = 1 THEN
    SELECT DISTINCT Shipment_Mode
      INTO p_Shipment_Mode
      FROM QMS_CHARGE_GROUPSMASTER
     WHERE Inactivate = 'N'
       AND Chargegroup_Id = p_Charge_Group_Id;

      IF p_shipment_mode = '1' THEN
        v_shipment_mode_str := '1,3,5,7';
      ELSIF p_shipment_mode = '2' THEN
        v_shipment_mode_str := '2,3,6,7';
      ELSIF p_shipment_mode = '3' THEN
        v_shipment_mode_str := '3,7';
      ELSIF p_shipment_mode = '4' THEN
        v_shipment_mode_str := '4,5,6,7';
      ELSIF p_shipment_mode = '5' THEN
        v_shipment_mode_str := '5,7';
      ELSIF p_shipment_mode = '6' THEN
        v_shipment_mode_str := '6,7';
      ELSIF p_shipment_mode = '7' THEN
        v_shipment_mode_str := '7';
      END IF;

      BEGIN

        EXECUTE IMMEDIATE ('  SELECT DISTINCT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                               AND SHIPMENT_MODE IN ( '
                           || v_shipment_mode_str
                           || ')'
                           ||'AND INVALIDATE = ''F''')
       INTO v_charge_id USING p_charge_id;

        v_charge_id_flag := '1';

        p_remarks := p_remarks || '';

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_charge_id_flag := '0';
          p_remarks        := p_remarks ||
                              'This Charge Id Has Been Invalidated or Does Not Exist.';
      END;
      --dbms_output.put_line('p_remarks '||p_remarks);
      BEGIN

        EXECUTE IMMEDIATE ('  SELECT CHARGEDESCID FROM QMS_CHARGEDESCMASTER WHERE CHARGEID =:v_charge_id
                              AND CHARGEDESCID =:v_charge_desc_id
                              AND SHIPMENTMODE IN ( '
                           || v_shipment_mode_str
                           || ') AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER
                              WHERE TERMINALID =:v_terminal_id UNION
                              SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID =:v_terminal_id
                              UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = ''H'')
                              AND INACTIVATE = ''N'''
                         )
       INTO v_charge_desc_id USING p_charge_id,p_charge_desc_id,p_terminal_id,p_terminal_id;

        v_charge_desc_id_flag := '1';
        p_remarks             := p_remarks || '';

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_charge_desc_id_flag := '0';
          p_remarks             := p_remarks ||
                                   'This Charge Description Id is Invalid or Does Not Exist.';
      END;
      IF (v_charge_group_id_flag = '1' AND v_charge_id_flag = '1' AND
         v_charge_desc_id_flag = '1') THEN
        p_success_flag := '1';
      ELSE
        p_success_flag := '0';
      END IF;
    END IF;
  ELSE
    p_remarks      := p_remarks ||
                      'The Terminal Id is Invalid or Does Not Belong in this Hierarchy.';
    p_success_flag := '0';
  END IF;
END;
--@@Added by Anil.k for CR 231214 on 25Jan2011
PROCEDURE VALIDATE_CHARGEGRP_DTL_MODIFY(p_charge_group_id VARCHAR2,
                                        p_charge_id       VARCHAR2,
                                        p_charge_desc_id  VARCHAR2,
                                        p_terminal_id     VARCHAR2,
                                        p_currterminalid  VARCHAR2,
                                        p_orgCountry_id   VARCHAR2,
                                        p_destCountry_id  VARCHAR2,
                                        p_success_flag    OUT VARCHAR2,
                                        p_shipment_mode   OUT VARCHAR2,
                                        p_remarks         OUT VARCHAR2) AS
  v_returnval            VARCHAR2(1);
  v_charge_group_id_flag VARCHAR2(1);
  v_charge_group_id      VARCHAR2(40);
  v_charge_id_flag       VARCHAR2(1);
  v_charge_id            VARCHAR2(10);
  v_charge_desc_id_flag  VARCHAR2(1);
  v_charge_desc_id       VARCHAR2(50);
  v_shipment_mode_str    VARCHAR2(50);
  v_orgCountry_id        VARCHAR2(60);
  v_destCountry_id       VARCHAR2(60);
  v_orgCountry_id_flag   VARCHAR2(1);
  v_destCountry_id_flag  VARCHAR2(1);
BEGIN

  ISEXISTINTHEHIRARCHY('Modify',
                       p_terminal_id,
                       p_currterminalid,
                       v_returnval);
  IF v_returnval = '1' THEN
    BEGIN
      SELECT DISTINCT CHARGEGROUP_ID
        INTO v_charge_group_id
        FROM QMS_CHARGE_GROUPSMASTER
       WHERE CHARGEGROUP_ID = p_charge_group_id
         AND INACTIVATE = 'N'
         AND TERMINALID = p_terminal_id;

      v_charge_group_id_flag := '1';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_charge_group_id_flag := '0';
        p_remarks              := 'The Charge Group Id is Invalid or Does Not Belong to this Terminal.';
    END;
    IF v_charge_group_id_flag = 1 THEN
    SELECT DISTINCT Shipment_Mode
      INTO p_Shipment_Mode
      FROM QMS_CHARGE_GROUPSMASTER
     WHERE Inactivate = 'N'
       AND Chargegroup_Id = p_Charge_Group_Id;

      IF p_shipment_mode = '1' THEN
        v_shipment_mode_str := '1,3,5,7';
      ELSIF p_shipment_mode = '2' THEN
        v_shipment_mode_str := '2,3,6,7';
      ELSIF p_shipment_mode = '3' THEN
        v_shipment_mode_str := '3,7';
      ELSIF p_shipment_mode = '4' THEN
        v_shipment_mode_str := '4,5,6,7';
      ELSIF p_shipment_mode = '5' THEN
        v_shipment_mode_str := '5,7';
      ELSIF p_shipment_mode = '6' THEN
        v_shipment_mode_str := '6,7';
      ELSIF p_shipment_mode = '7' THEN
        v_shipment_mode_str := '7';
      END IF;

      BEGIN

        EXECUTE IMMEDIATE ('  SELECT DISTINCT CHARGE_ID FROM QMS_CHARGESMASTER WHERE CHARGE_ID=:v_charge_id
                               AND SHIPMENT_MODE IN ( '
                           || v_shipment_mode_str
                           || ')'
                           ||'AND INVALIDATE = ''F''')
       INTO v_charge_id USING p_charge_id;

        v_charge_id_flag := '1';

        p_remarks := p_remarks || '';

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_charge_id_flag := '0';
          p_remarks        := p_remarks ||
                              'This Charge Id Has Been Invalidated or Does Not Exist.';
      END;
      --dbms_output.put_line('p_remarks '||p_remarks);
      BEGIN

        EXECUTE IMMEDIATE ('  SELECT CHARGEDESCID FROM QMS_CHARGEDESCMASTER WHERE CHARGEID =:v_charge_id
                              AND CHARGEDESCID =:v_charge_desc_id
                              AND SHIPMENTMODE IN ( '
                           || v_shipment_mode_str
                           || ') AND TERMINALID IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER
                              WHERE TERMINALID =:v_terminal_id UNION
                              SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN
                              CONNECT BY PRIOR PARENT_TERMINAL_ID = CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID =:v_terminal_id
                              UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = ''H'')
                              AND INACTIVATE = ''N'''
                         )
       INTO v_charge_desc_id USING p_charge_id,p_charge_desc_id,p_terminal_id,p_terminal_id;

        v_charge_desc_id_flag := '1';
        p_remarks             := p_remarks || '';

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_charge_desc_id_flag := '0';
          p_remarks             := p_remarks ||
                                   'This Charge Description Id is Invalid or Does Not Exist.';
      END;
      IF v_charge_desc_id_flag = 1 AND v_charge_id_flag = 1 THEN

    BEGIN

      EXECUTE IMMEDIATE (' SELECT COUNTRYID FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID=:
                            p_destCountry_id')
       INTO v_destCountry_id USING p_destCountry_id;

      v_destCountry_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_destCountry_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Country Does Not Exist.';
    END;

    BEGIN

      EXECUTE IMMEDIATE (' SELECT COUNTRYID FROM FS_COUNTRYMASTER FC WHERE FC.COUNTRYID=:
                            p_orgCountry_id')
       INTO v_orgCountry_id USING p_orgCountry_id;

      v_orgCountry_id_flag := '1';

      p_remarks := p_remarks || '';

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_orgCountry_id_flag := '0';
        p_remarks        := p_remarks ||
                            'This Country Does Not Exist.';
    END;

    END IF;
      IF (v_charge_group_id_flag = '1' AND v_charge_id_flag = '1' AND
         v_charge_desc_id_flag = '1' AND v_orgCountry_id_flag='1' AND v_destCountry_id_flag='1') THEN
        p_success_flag := '1';
      ELSE
        p_success_flag := '0';
      END IF;
    END IF;
  ELSE
    p_remarks      := p_remarks ||
                      'The Terminal Id is Invalid or Does Not Belong in this Hierarchy.';
    p_success_flag := '0';
  END IF;

END;
--@@Ended by Anil.k for CR 231214 on 25Jan2011
/*
This procedure is used to validate the density ratio entered by the user in
Buy/Sell Charges Add.

The IN Parameters are

p_oldbuychargeid         VARCHAR2,
p_newbuychargeid         VARCHAR2,
p_weightbreak            VARCHAR2,
p_ratetype               VARCHAR2,
p_terminal               VARCHAR2

The OUT Parameter is
p_exp              NUMBER

*/

FUNCTION validate_densityratio (p_densityratio VARCHAR2,V_CHARGE_BASIS_ID VARCHAR2)
      RETURN VARCHAR2
   IS
      V_CNT NUMBER(5);

   BEGIN

   --DBMS_OUTPUT.PUT_LINE(p_densityratio);
     SELECT COUNT(*)
       INTO v_Cnt
       FROM QMS_DENSITY_GROUP_CODE
      WHERE Invalidate = 'F'
        AND DECODE((SELECT Primary_Basis
                     FROM QMS_CHARGE_BASISMASTER
                    WHERE Chargebasis = v_Charge_Basis_Id),
                   'KG',
                   Kg_Per_M3,
                   'CBM',
                   Kg_Per_M3,
                   'LB',
                   Lb_Per_F3,
                   'CFT',
                   Lb_Per_F3) = p_Densityratio;

    IF V_CNT >0
      THEN
      RETURN 'TRUE';
    ELSE
      RETURN  'FALSE';
    END IF;

   END;

   FUNCTION  sellcharges_insert( p_Charge_Id       VARCHAR2,
                                 p_Charge_Desc_Id  VARCHAR2,
                                 p_Terminal_Id     VARCHAR2
                             )

      RETURN        NUMBER
 IS
 v_basis           VARCHAR2(50);
 v_break           VARCHAR2(50);
 v_type            VARCHAR2(50);
 v_currency        VARCHAR2(50);
 v_wtclass         VARCHAR2(50);
 v_margin          VARCHAR2(50);
 v_margintype      VARCHAR2(50);
 v_marginbasis     VARCHAR2(50);
 v_createdby       VARCHAR2(50);
 v_accesslevel     VARCHAR2(50);
 v_oldbuychargeid  VARCHAR2(50);
 v_buychargeid      VARCHAR2(50);
 v_sellchargeid     VARCHAR2(50);
 v_seq              VARCHAR2(2000);
 v_seq1             VARCHAR2(2000);
 v_chargeat         VARCHAR2(50);
 v_sysdate          DATE;
 v_count            VARCHAR2(3);
 v_sellchargeaccid  VARCHAR2(50);
 BEGIN
 SELECT SYSDATE INTO v_sysdate FROM dual;
 SELECT sellchargeid_seq.NEXTVAL INTO v_seq FROM dual;
 SELECT sellchargeaccid,buychargeid,sellchargeid,chargebasis,rate_break,rate_type,currency,weight_class,overall_margin,margin_type,margin_basis,created_by,accesslevel  INTO v_sellchargeaccid,v_buychargeid,v_sellchargeid,v_basis,v_break,v_type,v_currency,v_wtclass,v_margin,v_margintype,v_marginbasis,v_createdby,v_accesslevel FROM QMS_SELLCHARGESMASTER_ACC qsm  WHERE qsm.charge_id = p_Charge_Id AND qsm.chargedescid = p_Charge_Desc_Id AND qsm.terminalid = p_Terminal_Id;

 INSERT INTO QMS_SELLCHARGESMASTER(sellchargeid,charge_id,chargebasis,rate_break,rate_type,currency,weight_class,overall_margin,margin_type,margin_basis,created_by,created_tstmp,ie_flag,accesslevel,terminalid,dummy_sell_charges_flag,chargedescid,buychargeid)
 VALUES
   (v_seq,p_Charge_Id,v_basis,v_break,v_type,v_currency,v_wtclass,v_margin,v_margintype,v_marginbasis,v_createdby,v_sysdate,'A',v_accesslevel,p_Terminal_Id,'F',p_Charge_Desc_Id,v_buychargeid);

 FOR i IN (SELECT chargeslab,chargerate,marginvalue,lowerbound,upperbound,chargerate_indicator,lane_no FROM QMS_SELLCHARGESDTL_ACC WHERE sellchargeid=v_sellchargeid )
LOOP
SELECT seq_sellchargesdtl.NEXTVAL INTO v_seq1 FROM dual;
INSERT INTO QMS_SELLCHARGESDTL(sellchargeid,chargeslab,chargerate,marginvalue,lowerbound,upperbound,lane_no,chargerate_indicator,id)VALUES(v_seq,i.chargeslab,i.chargerate,i.marginvalue,i.lowerbound,i.upperbound,i.lane_no,i.chargerate_indicator,v_seq1);
END LOOP;
 SELECT buychargeid INTO v_oldbuychargeid FROM QMS_SELLCHARGESMASTER WHERE sellchargeid=v_sellchargeid;
--select cost_incurredat into v_chargeat from qms_chargesmaster where charge_id= p_Charge_Id;
--dbms_output.put_line('v_chargeat'||v_chargeat);
UPDATE QMS_SELLCHARGESMASTER SET ie_flag='I' WHERE sellchargeid=v_sellchargeid;
DELETE FROM QMS_SELLCHARGESDTL_ACC WHERE sellchargeaccid=v_sellchargeaccid;
DELETE FROM QMS_SELLCHARGESMASTER_ACC WHERE sellchargeaccid=v_sellchargeaccid;
QMS_QUOTEPACK_NEW.qms_Quote_update(v_sellchargeid,
                                     v_oldbuychargeid,
                                     NULL,
                                     NULL,
                                     NULL,
                                     v_seq,
                                     v_buychargeid,
                                      NULL,
                                     'S',
                                     NULL,
                                     NULL,
                                      p_Charge_Desc_Id);
 SELECT COUNT(*) INTO v_count FROM QMS_SELLCHARGESMASTER WHERE sellchargeid=v_seq;
   IF v_count>0
   THEN
       RETURN 1;
   ELSE
       RETURN 0;
   END IF;
 END;

END;

/

/

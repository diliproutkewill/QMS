--------------------------------------------------------
--  DDL for Function GETFREIGHTRATE
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "GETFREIGHTRATE" (P_BUYRATEID  IN NUMBER,
                                          P_WGHTSLAB   IN VARCHAR2,
                                          P_LANENO     IN NUMBER,
                                          P_TYPE       IN VARCHAR2,
                                          P_VERSION    IN NUMBER,
                                          P_SELLRATEID IN NUMBER,
                                          P_QUOTE_ID   IN NUMBER,P_SLNO IN NUMBER)
  RETURN VARCHAR2 AS

   V_CHARGERATE NUMBER(20,5);
 V_RATE   VARCHAR2(32567);
  V_SERVICELEVEL VARCHAR2(40);
  V_CARRIERID    VARCHAR2(40);
  V_RESULT       VARCHAR2(100);

  --V_BUYRATE              VARCHAR2(10):;
  --V_SELLRATE             VARCHAR2(10):=' ';
  V_BUYRATE              NUMBER;
  V_SELLRATE            NUMBER;
  V_MARGIN_DISCOUNT_FLAG VARCHAR2(1);
  V_MARGIN_TYPE          VARCHAR2(1);
  V_DISCOUNT_TYPE        VARCHAR2(1);
  V_MARGIN               NUMBER(20,5);
  V_DISCOUNT             NUMBER(20,5);

BEGIN
  IF P_TYPE = 'BR' THEN
    Begin
      SELECT DTL.CARRIER_ID, DTL.SERVICE_LEVEL
        INTO V_CARRIERID, V_SERVICELEVEL
        FROM QMS_BUYRATES_DTL DTL
       WHERE DTL.WEIGHT_BREAK_SLAB = P_WGHTSLAB
         AND DTL.BUYRATEID = P_BUYRATEID
         AND DTL.VERSION_NO = P_VERSION
         AND DTL.LANE_NO = P_LANENO;

      /*SELECT DTL.CARRIER_ID,DTL.SERVICE_LEVEL INTO V_CARRIERID,V_SERVICELEVEL FROM QMS_BUYRATES_DTL DTL WHERE  DTL.BUYRATEID=P_BUYRATEID AND DTL.VERSION_NO=P_VERSION
      AND  DTL.LANE_NO=P_LANENO and rownum=1;*/
    Exception
      WHEN NO_DATA_FOUND Then
        --  V_CHARGERATE := 'NA';
        V_CARRIERID    := 'NA';
        V_SERVICELEVEL := 'NA';
      WHEN OTHERS Then
        V_CHARGERATE   := '***';
        V_CARRIERID    := '***';
        V_SERVICELEVEL := '***';
    End;
  ELSE
    Begin
      SELECT DTL.CARRIER_ID, DTL.SERVICELEVEL_ID
        INTO V_CARRIERID, V_SERVICELEVEL
        FROM qms_rec_con_sellratesdtl DTL
       WHERE DTL.BUYRATEID = P_BUYRATEID
         AND DTL.LANE_NO = P_LANENO
         AND DTL.WEIGHTBREAKSLAB = P_WGHTSLAB
         AND DTL.VERSION_NO = P_VERSION
         AND DTL.REC_CON_ID = P_SELLRATEID;

      /*  SELECT DTL.CARRIER_ID,DTL.SERVICELEVEL_ID INTO V_CARRIERID,V_SERVICELEVEL FROM qms_rec_con_sellratesdtl DTL WHERE DTL.BUYRATEID=P_BUYRATEID AND DTL.LANE_NO=P_LANENO AND
      DTL.VERSION_NO=P_VERSION AND DTL.REC_CON_ID=P_SELLRATEID and rownum=1;*/
    Exception
      WHEN NO_DATA_FOUND Then
        -- V_CHARGERATE := 'NA';
        V_CARRIERID    := 'NA';
        V_SERVICELEVEL := 'NA';
      WHEN OTHERS Then
        V_CHARGERATE   := '***';
        V_CARRIERID    := '***';
        V_SERVICELEVEL := '***';
    End;

  END IF;
  /*SELECT QR.BUY_RATE,QR.R_SELL_RATE INTO V_BUYRATE,V_SELLRATE
   FROM QMS_QUOTE_RATES QR
  WHERE QR.QUOTE_ID=P_QUOTE_ID
  AND QR.BREAK_POINT=P_WGHTSLAB
  AND QR.RATE_LANE_NO=P_LANENO
  AND QR.VERSION_NO=P_VERSION;*/
  BEGIN
      SELECT QBD.BUY_RATE,
             QBD.R_SELL_RATE,
             QBD.MARGIN_DISCOUNT_FLAG,
             QBD.MARGIN_TYPE,
             QBD.DISCOUNT_TYPE,
             QBD.MARGIN,
             QBD.DISCOUNT
        INTO V_BUYRATE,
             V_SELLRATE,
             V_MARGIN_DISCOUNT_FLAG,
             V_MARGIN_TYPE,
             V_DISCOUNT_TYPE,
             V_MARGIN,
             V_DISCOUNT
        FROM QMS_QUOTE_RATES QBD
       WHERE QBD.QUOTE_ID = P_QUOTE_ID
         AND QBD.SELL_BUY_FLAG IN ('BR', 'RSR')
         AND QBD.BREAK_POINT = P_WGHTSLAB
         AND QBD.SERIAL_NO=P_SLNO;
        -- AND QBD.RATE_LANE_NO=P_LANENO
  --AND QBD.VERSION_NO=P_VERSION;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      V_RATE := 'NA';
     WHEN OTHERS Then
       V_RATE := 'NA';
  END;
  IF V_MARGIN_DISCOUNT_FLAG = 'M' THEN
    IF V_MARGIN_TYPE = 'A' THEN
      V_CHARGERATE := V_BUYRATE + V_MARGIN;
    ELSE
      V_CHARGERATE := V_BUYRATE + (V_BUYRATE * V_MARGIN) / 100;
    END IF;
  ELSE
    IF V_DISCOUNT_TYPE = 'A' THEN
      V_CHARGERATE := V_SELLRATE - V_DISCOUNT;
    ELSE
      V_CHARGERATE := V_SELLRATE - (V_SELLRATE * V_DISCOUNT) / 100;
    END IF;
  END IF;
V_RATE :=V_RATE || V_CHARGERATE ;
  V_RESULT := V_RATE || '#' || V_CARRIERID || '#' || V_SERVICELEVEL;

  RETURN V_RESULT;

END GETFREIGHTRATE;

/

/

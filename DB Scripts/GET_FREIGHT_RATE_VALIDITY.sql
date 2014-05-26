--------------------------------------------------------
--  DDL for Function GET_FREIGHT_RATE_VALIDITY
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "GET_FREIGHT_RATE_VALIDITY" (P_BUYRATEID IN NUMBER,
                                                     P_LANENO    IN NUMBER, P_VERSION IN NUMBER)
  RETURN DATE AS

  V_RESULT DATE;
BEGIN

  SELECT DTL.VALID_UPTO
    INTO V_RESULT
    FROM QMS_BUYRATES_DTL DTL
   WHERE DTL.BUYRATEID = P_BUYRATEID
     AND DTL.VERSION_NO = P_VERSION
     AND DTL.LANE_NO = P_LANENO
     AND DTL.LINE_NO = 0;

  RETURN V_RESULT;

END GET_FREIGHT_RATE_VALIDITY;

/

/

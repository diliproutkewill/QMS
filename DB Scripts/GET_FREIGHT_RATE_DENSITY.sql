--------------------------------------------------------
--  DDL for Function GET_FREIGHT_RATE_DENSITY
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "GET_FREIGHT_RATE_DENSITY" (P_BUYRATEID IN NUMBER,
                                                     P_LANENO    IN NUMBER, P_VERSION IN NUMBER)
  RETURN varchar2 AS

  V_DENSITY varchar2(25);
  V_DENSITY_TYPE VARCHAR2(5);
BEGIN
 Begin
  SELECT DTL.density_code
    INTO V_DENSITY
    FROM QMS_BUYRATES_DTL DTL
   WHERE DTL.BUYRATEID = P_BUYRATEID
     AND DTL.VERSION_NO = P_VERSION
     AND DTL.LANE_NO = P_LANENO
     AND DTL.LINE_NO = 0;

SELECT   Decode ((select count(qdg.kg_per_m3)from qms_density_group_code qdg where qdg.kg_per_m3 = to_number(V_DENSITY)), 0, 'LB', 'KG') INTO V_DENSITY_TYPE FROM dual;
Exception
        WHEN NO_DATA_FOUND Then
        V_DENSITY:='NA';
        V_DENSITY_TYPE:='NA';
        end;
  RETURN V_DENSITY||'#'||V_DENSITY_TYPE;

END GET_FREIGHT_RATE_DENSITY;

/

/

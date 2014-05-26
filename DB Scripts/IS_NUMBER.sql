--------------------------------------------------------
--  DDL for Function IS_NUMBER
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "IS_NUMBER" (input VARCHAR2) RETURN VARCHAR2 IS
  v_input NUMBER;
BEGIN
  v_input := TO_NUMBER(input);
  RETURN 'true';
EXCEPTION
  WHEN OTHERS THEN
    RETURN 'false';
END;

/

/

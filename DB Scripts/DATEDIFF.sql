--------------------------------------------------------
--  DDL for Function DATEDIFF
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DATEDIFF" (
   p_what   IN   VARCHAR2,
   p_d1     IN   DATE,
   p_d2     IN   DATE
)
   RETURN NUMBER
AS
   l_result   NUMBER;
BEGIN
   SELECT   (p_d2 - p_d1)
          * DECODE (UPPER (p_what),
                    'SS', 24 * 60 * 60,
                    'MI', 24 * 60,
                    'HH', 24,
                    NULL
                   )
     INTO l_result
     FROM DUAL;

   RETURN l_result;
END;

/

/

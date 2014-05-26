--------------------------------------------------------
--  DDL for Function DAYS_CAL_FUN
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "DAYS_CAL_FUN" (p_time VARCHAR2, p_date VARCHAR2)

   RETURN VARCHAR2

AS

   v_hrs     NUMBER (5);

   v_days    NUMBER (5);

   v_rdate   VARCHAR2 (3000);

   v_mins    NUMBER (5);

   v_secs    NUMBER (5);

   v_dmins   NUMBER (5);

   v_dsecs   NUMBER (5);

   v_dhrs    NUMBER (5);

BEGIN

   DBMS_OUTPUT.put_line ('Time ' || p_time);

   DBMS_OUTPUT.put_line ('Date ' || p_date);

   v_hrs := SUBSTR (p_time, 1, INSTR (p_time, ':') - 1);

   v_mins :=

        SUBSTR (p_time, INSTR (p_time, ':', 1) + 1,

                INSTR (p_time, ':', 2) - 1);

   v_secs := SUBSTR (p_time, INSTR (p_time, ':', -1) + 1, LENGTH (p_time));

   DBMS_OUTPUT.put_line ('For Time');

   DBMS_OUTPUT.put_line (v_secs);

   DBMS_OUTPUT.put_line (v_mins);

   DBMS_OUTPUT.put_line (v_hrs);

   v_dsecs := SUBSTR (p_date, INSTR (p_date, ':', -1) + 1, LENGTH (p_time));

   v_dmins :=

        SUBSTR (p_date, INSTR (p_date, ':', 1) + 1,

                INSTR (p_time, ':', 2) - 1);

   v_dhrs :=

           SUBSTR (p_date, INSTR (p_date, ' ') + 1,

                   INSTR (p_time, ':', 1) - 1);

   DBMS_OUTPUT.put_line ('For Date');

   DBMS_OUTPUT.put_line (v_dsecs);

   DBMS_OUTPUT.put_line (v_dmins);

   DBMS_OUTPUT.put_line (v_dhrs);



   v_hrs := v_hrs + v_dhrs;

   v_mins := v_mins + v_dmins;

   v_secs := v_secs + v_dsecs;



   v_dmins := TRUNC (v_secs / 60);

   v_secs := MOD (v_secs, 60);

   v_mins := v_mins + v_dmins;

   v_dhrs := TRUNC (v_mins / 60);

   v_hrs := v_hrs + v_dhrs;

   DBMS_OUTPUT.put_line ('Hours    >>' || v_hrs);

   v_mins := MOD (v_mins, 60);

   v_days := TRUNC (v_hrs / 24);

   v_hrs := MOD (v_hrs, 24);



   DBMS_OUTPUT.put_line (v_days);

   DBMS_OUTPUT.put_line (v_hrs);

   DBMS_OUTPUT.put_line (v_mins);

   DBMS_OUTPUT.put_line (v_secs);

   DBMS_OUTPUT.put_line (SUBSTR (p_date, 1, INSTR (p_date, ' ') - 1));



   v_rdate :=

         (TO_DATE (SUBSTR (p_date, 1, INSTR (p_date, ' ') - 1)) + v_days)

      || ' '

      || v_hrs

      || ':'

      || v_mins

      || ':'

      || v_secs;

   DBMS_OUTPUT.put_line (v_rdate);

if(v_rdate=' ::') then
 -- DBMS_OUTPUT.put_line ('kiran'||SUBSTR (p_date, 1, INSTR (p_date, ' ') - 1));
  v_rdate:=SUBSTR (p_date, 1, INSTR (p_date, ' ') - 1)||' 00:00:00';
end if;
   RETURN v_rdate;

END;

/

/

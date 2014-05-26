--------------------------------------------------------
--  DDL for Procedure CURRENCY_ARCHIVAL
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "CURRENCY_ARCHIVAL" AS
BEGIN
  INSERT INTO fs_fr_currencymaster_hstr
    SELECT *
      FROM FS_FR_CURRENCYMASTER a
     WHERE currency_updated_date <>
           (SELECT MAX(currency_updated_date)
              FROM FS_FR_CURRENCYMASTER b
             WHERE a.currency1 = b.currency1
               AND a.currency2 = b.currency2
               AND a.INVALIDATE = 'F');

  DELETE FROM FS_FR_CURRENCYMASTER a
   WHERE currency_updated_date <>
         (SELECT MAX(currency_updated_date)
            FROM FS_FR_CURRENCYMASTER b
           WHERE a.currency1 = b.currency1
             AND a.currency2 = b.currency2
             AND a.INVALIDATE = 'F');

  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    Qms_Quote_Pack.g_err      := '<< ' || SQLERRM || ' >>';
    Qms_Quote_Pack.g_err_code := '<< ' || SQLCODE || ' >>';

    INSERT INTO QMS_OBJECTS_ERRORS
      (ex_date, module_name, errorcode, errormessage)
    VALUES
      (SYSDATE,
       'Currency Moving to History',
       Qms_Quote_Pack.g_err_code,
       Qms_Quote_Pack.g_err);
END;

/

/

--------------------------------------------------------
--  DDL for Package PKG_QMS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "PKG_QMS" AS
TYPE CSCOLUMNS IS REF CURSOR;
FUNCTION GETLOCATIONSINHIERARCHY(PTERMINAL VARCHAR2, SEARCHSTR VARCHAR2) RETURN CSCOLUMNS;
PROCEDURE GETLOCATIONS(PTERMINAL VARCHAR2, SEARCHSTR VARCHAR2, CSLIST OUT PKG_QMS.CSCOLUMNS );
END;

/

/

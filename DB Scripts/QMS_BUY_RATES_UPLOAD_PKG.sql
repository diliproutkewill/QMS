--------------------------------------------------------
--  DDL for Package QMS_BUY_RATES_UPLOAD_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_BUY_RATES_UPLOAD_PKG" AS

      TYPE RESULTSET IS REF CURSOR;

      -- Author  : KAMESWARIP
      -- Created : 03-03-09 4:40:50 PM
      -- Purpose :
      -- Public type declarations
      FUNCTION BUY_RATES_PROC(P_TERMINAL VARCHAR2) RETURN VARCHAR2;

      PROCEDURE VALIDATE_DETAILS(P_TERMINAL VARCHAR2, P_WEIGHT_BREAK VARCHAR2,
				 P_SHIPMENT_MODE VARCHAR2);


      FUNCTION VALIDATEDETAILS RETURN VARCHAR2;

      FUNCTION INSERTDETAILS RETURN VARCHAR2;
        FUNCTION BUY_RATES_DELETE_PROC RETURN VARCHAR2 ;

/*  procedure RATES_DELETION(p_Rs Out Resultset);*/
/* procedure RSR_INSERTIONS(p_Rs Out Resultset);
   procedure rates_updated;*/
END QMS_BUY_RATES_UPLOAD_PKG;

/

/

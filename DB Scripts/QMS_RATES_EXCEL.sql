--------------------------------------------------------
--  DDL for Package QMS_RATES_EXCEL
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_RATES_EXCEL" is

  -- Author  : KAMESWARIP
  -- Created : 28-04-09 11:38:49 AM
  -- Purpose : for quote grouping excel report

  Type Resultset Is Ref Cursor;

   PROCEDURE PROC_RATES_HEADER_EXCEL_DTL(p_v_Customer_Id  IN QMS_QUOTE_MASTER.Customer_Id%Type,
                                        p_v_Sql          IN varchar2,
                                        p_v_shipmentmode number,
                                        p_v_temp         number);


  procedure qms_ratesreport_excel(p_customerid    IN VARCHAR2,
                                  p_origincountry IN VARCHAR2,
                                  p_origincity    IN VARCHAR2,
                                  p_destcountry   IN VARCHAR2,
                                  p_destcity      IN VARCHAR2,
                                  p_shipmentMode  IN VARCHAR2,
                                  p_consoleType   IN VARCHAR2,
                                  p_spotrateflag  IN VARCHAR2,
                                  p_weightBreak   IN VARCHAR2, --Added By Kishore Podili For Report with Charges on 03-Feb-11
                                  RS              out Resultset
                                  ,RS1              out Resultset
                                  );

end qms_rates_excel;

/

/

--------------------------------------------------------
--  DDL for Package QMS_REPORT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_REPORT_PKG" AS

  -- spec
  TYPE Resultset IS REF CURSOR;

  PROCEDURE Qms_Byrate_Expiry_Report(Ship_Mode     VARCHAR2,
                                     Carrier_Id    VARCHAR2,
                                     Service_Level VARCHAR2,
                                     Basis         VARCHAR2,
                                     Flage         VARCHAR2,
                                     From_Date     DATE,
                                     TO_DATE       DATE,
                                     p_Page_No     NUMBER DEFAULT 1,
                                     p_Page_Rows   NUMBER DEFAULT 50,
                                     Sortby        VARCHAR2,
                                     Sortorder     VARCHAR2,
                                     p_Tot_Rec     OUT NUMBER,
                                     p_Tot_Pages   OUT NUMBER,
                                     Dhl_Ref       OUT Sys_Refcursor);

  PROCEDURE Qms_Byrate_Expiry_Report_Excel(Ship_Mode     VARCHAR2,
                                           Carrier_Id    VARCHAR2,
                                           Service_Level VARCHAR2,
                                           Basis         VARCHAR2,
                                           Flage         VARCHAR2,
                                           From_Date     DATE,
                                           TO_DATE       DATE,
                                           weight_break  VARCHAR2,
                                           rate_type     VARCHAR2,
                                           Dhl_Ref       OUT Resultset);

  --salesPersionId,user_Id Included by Shyam for DHL
  PROCEDURE Qms_Quote_Expiry_Report(Ship_Mode        VARCHAR2,
                                    Customer_Id      VARCHAR2,
                                    Service_Level    VARCHAR2,
                                    Terminal_Id      VARCHAR2,
                                    Basis            VARCHAR2,
                                    Flage            VARCHAR2,
                                    From_Date        VARCHAR2,
                                    TO_DATE          VARCHAR2,
                                    p_Userdateformat VARCHAR2,
                                    p_Page_No        NUMBER DEFAULT 1,
                                    p_Page_Rows      NUMBER DEFAULT 20,
                                    Sortby           VARCHAR2,
                                    Sortorder        VARCHAR2,
                                    Salespersonid    VARCHAR2,
                                    User_Id          VARCHAR2,
                                    p_Tot_Rec        OUT NUMBER,
                                    p_Tot_Pages      OUT NUMBER,
                                    Dhl_Ref1         OUT Sys_Refcursor);

  PROCEDURE Qms_Quote_Expiry_Report_Excl(Ship_Mode        VARCHAR2,
                                         Customer_Id      VARCHAR2,
                                         Service_Level    VARCHAR2,
                                         Terminal_Id      VARCHAR2,
                                         Basis            VARCHAR2,
                                         Flage            VARCHAR2,
                                         From_Date        VARCHAR2,
                                         TO_DATE          VARCHAR2,
                                         p_Userdateformat VARCHAR2,
                                         Dhl_Ref1         OUT Sys_Refcursor);

  /* PROCEDURE qms_active_report (
     ship_mode               VARCHAR2,
     customer_id             VARCHAR2,
     sales_person_id         VARCHAR2,
     quote_status            VARCHAR2,
     from_location           VARCHAR2,
     dest_location           VARCHAR2,
     service_lev_id          VARCHAR2,
     exp_vdate               DATE,
     exp_edate               DATE,
     p_page_no               NUMBER DEFAULT 1,
     p_page_rows             NUMBER DEFAULT 50,
     p_tot_rec         OUT   NUMBER,
     p_tot_pages       OUT   NUMBER,
     dhl_ref           OUT   sys_refcursor
  );*/
  PROCEDURE Qms_App_Rej_Report(p_Userid      VARCHAR2,
                               p_Terminalid  VARCHAR2,
                               p_Type        VARCHAR2,
                               p_Aprovalflag VARCHAR2,
                               p_Page_No     NUMBER DEFAULT 1,
                               p_Page_Rows   NUMBER DEFAULT 50,
                               Sortby        VARCHAR2,
                               Sortorder     VARCHAR2,
                               p_Tot_Rec     OUT NUMBER,
                               p_Tot_Pages   OUT NUMBER,
                               p_Rs          OUT Sys_Refcursor);

  PROCEDURE Qms_App_Rej_Report_Excel(p_Userid      VARCHAR2,
                                     p_Terminalid  VARCHAR2,
                                     p_Type        VARCHAR2,
                                     p_Aprovalflag VARCHAR2,
                                     p_Rs          OUT Sys_Refcursor);

  PROCEDURE Qms_Pending_Report(p_Shmode         VARCHAR2,
                               p_Consoletype    VARCHAR2,
                               p_Option         VARCHAR2,
                               p_Frmdate        VARCHAR2,
                               p_Todate         VARCHAR2,
                               p_Terminalid     VARCHAR2,
                               p_Userdateformat VARCHAR2,
                               p_Page_No        NUMBER DEFAULT 1,
                               p_Page_Rows      NUMBER DEFAULT 50,
                               Sortby           VARCHAR2,
                               Sortorder        VARCHAR2,
                               p_Tot_Rec        OUT NUMBER,
                               p_Tot_Pages      OUT NUMBER,
                               p_Rs             OUT Sys_Refcursor);

  PROCEDURE Qms_Pending_Report_Excel(p_Shmode         VARCHAR2,
                                     p_Consoletype    VARCHAR2,
                                     p_Option         VARCHAR2,
                                     p_Frmdate        VARCHAR2,
                                     p_Todate         VARCHAR2,
                                     p_Terminalid     VARCHAR2,
                                     p_Userdateformat VARCHAR2,
                                     p_Rs             OUT Sys_Refcursor);

  PROCEDURE Activity_Report(p_From_Date      VARCHAR2,
                            p_To_Date        VARCHAR2,
                            p_Userdateformat VARCHAR2,
                            p_Shipment_Mode  VARCHAR2,
                            p_Emp_Id         VARCHAR2,
                            p_Customer_Id    VARCHAR2,
                            p_Quote_Status   VARCHAR2,
                            p_Origin         VARCHAR2,
                            p_Destination    VARCHAR2,
                            p_Service_Level  VARCHAR2,
                            p_terminalId     VARCHAR2,
                            p_loginTerminal  VARCHAR2,
                            p_fromCountry    VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
                            p_toCountry      VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
                            p_Page_No        NUMBER DEFAULT 1,
                            p_Page_Rows      NUMBER DEFAULT 50,
                            Sortby           VARCHAR2,
                            Sortorder        VARCHAR2,
                            p_autoUpdated    VARCHAR2,
                            p_Tot_Rec        OUT NUMBER,
                            p_Tot_Pages      OUT NUMBER,
                            p_Result         OUT Resultset);

  PROCEDURE Activity_Report_Excel(p_From_Date      VARCHAR2,
                                  p_To_Date        VARCHAR2,
                                  p_Userdateformat VARCHAR2,
                                  p_Shipment_Mode  VARCHAR2,
                                  p_Emp_Id         VARCHAR2,
                                  p_Customer_Id    VARCHAR2,
                                  p_Quote_Status   VARCHAR2,
                                  p_Origin         VARCHAR2,
                                  p_Destination    VARCHAR2,
                                  p_Service_Level  VARCHAR2,
                                  p_terminalId     VARCHAR2,
                                  p_loginTerminal  VARCHAR2,
                                  p_fromCountry    VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
                                  p_toCountry      VARCHAR2,--//@@Added by RajKumari for the WPBN issue-143517
                                  p_autoUpdated           VARCHAR2,
                                  p_Result         OUT Resultset);

  PROCEDURE Yeild_Dtls_Proc(p_Quoteid VARCHAR2, p_Rs OUT Resultset);

  FUNCTION Yeild_Average_Fun(p_Quoteid VARCHAR2) RETURN FLOAT;

END Qms_Report_Pkg;

/

/

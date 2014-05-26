--------------------------------------------------------
--  DDL for Package QMS_RSR_RATES_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_RSR_RATES_PKG" AS

  TYPE Resultset IS REF CURSOR;

  g_Err VARCHAR2(100);

  g_Err_Code VARCHAR2(100);

  PROCEDURE Buy_Sell_Rates(p_Org_Locs     VARCHAR2,
                           p_Dest_Locs    VARCHAR2,
                           p_Terminal     VARCHAR2,
                           p_Acclevel     VARCHAR2,
                           p_Rate_Type    VARCHAR2,
                           p_Weight_Break VARCHAR2,
                           p_Srvlevl      VARCHAR2,
                           p_Carrier      VARCHAR2,
                           p_Currency     VARCHAR2,
                           p_Shmode       VARCHAR2,
                           p_Operation    VARCHAR2,
                           p_Page_No      NUMBER DEFAULT 1,
                           p_Page_Rows    NUMBER DEFAULT 50,
                           p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                           p_Tot_Rec      OUT NUMBER,
                           p_Tot_Pages    OUT NUMBER,
                           p_Rs           OUT Qms_Rsr_Rates_Pkg.Resultset);

  PROCEDURE Comman_Proc(p_Operation    VARCHAR2,
                        p_Org_Locs     VARCHAR2,
                        p_Dest_Locs    VARCHAR2,
                        p_Terminal     VARCHAR2,
                        p_Acclevel     VARCHAR2,
                        p_Rate_Type    VARCHAR2,
                        p_Weight_Break VARCHAR2,
                        p_Srvlevl      VARCHAR2,
                        p_Carrier      VARCHAR2,
                        p_Currency     VARCHAR2,
                        p_Shmode       VARCHAR2,
                        p_Qry          VARCHAR2,
                        p_Page_No      NUMBER DEFAULT 1,
                        p_Page_Rows    NUMBER DEFAULT 50,
                        p_org_countries VARCHAR2,
                        p_dest_countries VARCHAR2,
                        p_org_regions VARCHAR2,
                        p_dest_regions VARCHAR2,
                        p_Tot_Rec      OUT NUMBER,
                        p_Tot_Pages    OUT NUMBER,
                        p_Rs           OUT Resultset);

  PROCEDURE Comman_Proc_Modify(p_Operation    VARCHAR2,
                               p_Org_Locs     VARCHAR2,
                               p_Dest_Locs    VARCHAR2,
                               p_Terminal     VARCHAR2,
                               p_Acclevel     VARCHAR2,
                               p_Rate_Type    VARCHAR2,
                               p_Weight_Break VARCHAR2,
                               p_Srvlevl      VARCHAR2,
                               p_Carrier      VARCHAR2,
                               p_Currency     VARCHAR2,
                               p_Shmode       VARCHAR2,
                               p_Qry          VARCHAR2,
                               p_Page_No      NUMBER DEFAULT 1,
                               p_Page_Rows    NUMBER DEFAULT 50,
			       p_org_countries VARCHAR2,
			       p_dest_countries VARCHAR2,
			       p_org_regions VARCHAR2,
			       p_dest_regions VARCHAR2,
                               p_Tot_Rec      OUT NUMBER,
                               p_Tot_Pages    OUT NUMBER,
                               p_Rs           OUT Resultset);

  PROCEDURE Sell_Rates_Proc(p_Org_Locs     VARCHAR2,
                            p_Dest_Locs    VARCHAR2,
                            p_Terminal     VARCHAR2,
                            p_Acclevel     VARCHAR2,
                            p_Rate_Type    VARCHAR2,
                            p_Weight_Break VARCHAR2,
                            p_Srvlevl      VARCHAR2,
                            p_Carrier      VARCHAR2,
                            p_Currency     VARCHAR2,
                            p_Shmode       VARCHAR2,
                            p_Qry          VARCHAR2,
                            p_Page_No      NUMBER DEFAULT 1,
                            p_Page_Rows    NUMBER DEFAULT 50,
                            p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                            p_Tot_Rec      OUT NUMBER,
                            p_Tot_Pages    OUT NUMBER,
                            p_Rs           OUT Resultset);

  FUNCTION Seperator(p_Str VARCHAR2) RETURN VARCHAR2;

  FUNCTION Qms_Sell_Rate_Validation(p_Origin              VARCHAR2,
                                    p_Destination         VARCHAR2,
                                    p_Serv_Level          VARCHAR2,
                                    p_Carrier             VARCHAR2,
                                    p_Frequency           VARCHAR2,
                                    p_Current_Terminal_Id VARCHAR2,
                                    p_Wet_Break           VARCHAR2,
                                    p_Rate_Type           VARCHAR2,
                                    p_Shipmentmode        VARCHAR2,
                                    p_Process             VARCHAR2,
                                    p_Newsellrateid       NUMBER,
                                    p_Newbuyrateid        VARCHAR2,
                                    p_New_Lane_No         NUMBER,
                                    p_operation           VARCHAR2)
    RETURN NUMBER;

  FUNCTION Validate_Sellrate(p_Origin              VARCHAR2,
                             p_Destination         VARCHAR2,
                             p_Serv_Level          VARCHAR2,
                             p_Carrier             VARCHAR2,
                             p_Frequency           VARCHAR2,
                             p_Current_Terminal_Id VARCHAR2,
                             p_Weight_Break        VARCHAR2,
                             p_Rate_Type           VARCHAR2,
                             p_Shipmentmode        VARCHAR2) RETURN VARCHAR2;

END;

/

/

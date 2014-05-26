--------------------------------------------------------
--  DDL for Package QMS_SETUPS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_SETUPS" AS

  TYPE g_Ref_Cur IS REF CURSOR;

  FUNCTION Get_Country(p_Terminal_Id VARCHAR2,
                       p_Operation   VARCHAR2,
                       p_Country     VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Country_1(p_Location     VARCHAR2,
                         p_Country      VARCHAR2,
                         p_Shipmentmode VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Country(p_Country VARCHAR2, p_Location VARCHAR2)
    RETURN g_Ref_Cur;

  FUNCTION Get_Commodity(p_Terminal_Id VARCHAR2,
                         p_Operation   VARCHAR2,
                         p_Commodity   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Port(p_Terminal_Id VARCHAR2,
                    p_Portid      VARCHAR2,
                    p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Servicelevel(p_Terminal_Id    VARCHAR2,
                            p_Shipment_Mode  VARCHAR2,
                            p_Servicelevelid VARCHAR2,
                            p_Operation      VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Servicelevel_Buyrate(p_Terminal_Id    VARCHAR2,
                                    p_Shipment_Mode  VARCHAR2,
                                    p_Servicelevelid VARCHAR2,
                                    p_Operation      VARCHAR2)
    RETURN g_Ref_Cur;

  FUNCTION Get_Industryregistration(p_Terminal_Id VARCHAR2,
                                    p_Industry_Id VARCHAR2,
                                    p_Str1        VARCHAR2,
                                    p_Str2        VARCHAR2,
                                    p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Densitygroupcode(p_Terminal_Id VARCHAR2,
                                p_Dgccode     VARCHAR2,
                                p_Invalidate  VARCHAR2,
                                p_kgm3        VARCHAR2,
                                p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Taxes(p_Terminal_Id VARCHAR2,
                     p_Taxid       VARCHAR2,
                     p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Listmaster(p_Terminal_Id  VARCHAR2,
                          p_Operation    VARCHAR2,
                          p_Shipmentmode VARCHAR2,
                          p_Searchstring VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Contentdescription(p_Terminal_Id VARCHAR2,
                                  p_Contentid   VARCHAR2,
                                  p_Operation   VARCHAR2,
                                  p_Shimentmode VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Carrier(p_Terminal_Id   VARCHAR2,
                       p_Operation     VARCHAR2,
                       p_Carrierid     VARCHAR2,
                       p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Carrier_Buyrate(p_Terminal_Id   VARCHAR2,
                               p_Operation     VARCHAR2,
                               p_Carrierid     VARCHAR2,
                               p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Carrier_Csr(p_Terminal_Id   VARCHAR2,
                           p_Originloc     VARCHAR2,
                           p_Destloc       VARCHAR2,
                           p_Shipment_Mode VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Company(p_Terminal_Id VARCHAR2,
                       p_Company_Id  VARCHAR2,
                       p_Str         VARCHAR2,
                       p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Location_Port(p_Terminal_Id VARCHAR2,
                             p_Operation   VARCHAR2,
                             p_Location    VARCHAR2,
                             p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Location(p_Terminal_Id VARCHAR2,
                        p_Operation   VARCHAR2,
                        p_Location    VARCHAR2,
                        p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Location_Buyrates(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Location    VARCHAR2,
                                 p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Locationport_Buyrates(p_Terminal_Id VARCHAR2,
                                     p_Operation   VARCHAR2,
                                     p_Location    VARCHAR2,
                                     p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Locationidsforcountry(p_Terminal_Id VARCHAR2,
                                     p_Operation   VARCHAR2,
                                     p_Location    VARCHAR2,
                                     p_Countryid   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Locids(p_Terminal_Id VARCHAR2,
                      p_Operation   VARCHAR2,
                      p_Location    VARCHAR2,
                      p_Countryid   VARCHAR2,
                      p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Designation(p_Terminal_Id    VARCHAR2,
                           p_Designation_Id VARCHAR2,
                           p_Operation      VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Customer(p_Terminal_Id       VARCHAR2,
                        p_Operation         VARCHAR2,
                        p_Customerid        VARCHAR2,
                        p_Registered        VARCHAR2,
                        p_Registrationlevel VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Customer_1(p_Terminal_Id   VARCHAR2,
                          p_Customer_Type VARCHAR2,
                          p_Operation     VARCHAR2,
                          p_Customerid    VARCHAR2,
                          p_Flag          VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Customer_2(p_Customerid VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Codecustomization(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Codeidname  VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_String(p_Terminal_Id VARCHAR2, p_Operation VARCHAR2)
    RETURN VARCHAR2;

  FUNCTION Get_Terminal_1(p_Terminal_Id  VARCHAR2,
                          p_Companyid    VARCHAR2,
                          p_Shipmentmode VARCHAR2,
                          p_Searchstring VARCHAR2,
                          p_Terminaltype VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Terminal_2(p_Terminal_Id VARCHAR2, p_Location VARCHAR2)
    RETURN g_Ref_Cur;

  FUNCTION Get_Currency_1(p_Terminal_Id VARCHAR2,
                          p_Currency_Id VARCHAR2,
                          p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Currency_2(p_Terminal_Id VARCHAR2,
                          p_Currency_Id VARCHAR2,
                          p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Selectedcurrency(p_Terminal_Id VARCHAR2,
                                p_Currency_Id VARCHAR2,
                                p_Operation   VARCHAR2,
                                p_Radio       VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Modifiedcurrency(p_Terminal_Id VARCHAR2,
                                p_Currency_Id VARCHAR2,
                                p_Operation   VARCHAR2,
                                p_Radio       VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Currentlist(p_Terminal_Id VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Currencyids(p_Currency_Id VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Marginlimitmaster_1(p_Terminal_Id VARCHAR2,
                                   p_Level_No    VARCHAR2,
                                   p_Operation   VARCHAR2,
                                   p_Marginid    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Marginlimitmaster_2(p_Terminal_Id VARCHAR2,
                                   p_Level_No    VARCHAR2,
                                   p_Operation   VARCHAR2,
                                   p_Levelno     VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Salesperson(p_Terminal_Id      VARCHAR2,
                           p_Salesperson_Code VARCHAR2,
                           p_Operation        VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Locidswithportids(p_Terminal_Id VARCHAR2,
                                 p_Operation   VARCHAR2,
                                 p_Location    VARCHAR2,
                                 p_Shipmode    VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Zone_Code_Dtl_Fun(p_From_Zipcode  VARCHAR2,
                             p_To_Zipcode    VARCHAR2,
                             p_Zone          VARCHAR2,
                             p_Alpha         VARCHAR2,
                             p_Location      VARCHAR2,
                             p_Zoncode_Type  VARCHAR2,
                             p_shipment_mode VARCHAR2,
                             p_console_type  VARCHAR2) RETURN NUMBER;

  FUNCTION Get_Servicelevel_Hirarchy(p_Terminal_Id    VARCHAR2,
                                     p_Shipment_Mode  VARCHAR2,
                                     p_Servicelevelid VARCHAR2,
                                     p_Operation      VARCHAR2)
    RETURN g_Ref_Cur;

  FUNCTION Get_Industryregistration1(p_Terminal_Id VARCHAR2,
                                     p_Industry_Id VARCHAR2,
                                     p_Str1        VARCHAR2,
                                     p_Str2        VARCHAR2,
                                     p_Operation   VARCHAR2) RETURN g_Ref_Cur;

  FUNCTION Get_Port1(p_Terminal_Id VARCHAR2,
                     p_Portid      VARCHAR2,
                     p_Operation   VARCHAR2) RETURN g_Ref_Cur;

FUNCTION Get_Regions(p_Regions      VARCHAR2,
                       p_Location     VARCHAR2,
                       p_Country      VARCHAR2,
                       p_Shipmentmode VARCHAR2)  RETURN g_Ref_Cur;

FUNCTION Get_Region_countryIds(p_Location     VARCHAR2,
                               p_Country      VARCHAR2,
                               p_regionId      VARCHAR2,
                               p_Shipmentmode VARCHAR2) RETURN g_Ref_Cur;

FUNCTION Get_Region_LocationIds(p_regionId      VARCHAR2,
                               p_Shipmode VARCHAR2) RETURN g_Ref_Cur;



END Qms_Setups;

/

/

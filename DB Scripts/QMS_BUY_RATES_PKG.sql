--------------------------------------------------------
--  DDL for Package QMS_BUY_RATES_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_BUY_RATES_PKG" AS
  TYPE resultset IS REF CURSOR;

  PROCEDURE buy_rates_proc(p_org_locs     VARCHAR2,
                           p_dest_locs    VARCHAR2,
                           p_terminal     VARCHAR2,
                           p_rate_type    VARCHAR2,
                           p_weight_break VARCHAR2,
                           p_srvlevl      VARCHAR2,
                           p_carrier      VARCHAR2,
                           p_shmode       VARCHAR2,
                           p_operation    VARCHAR2,
                           p_page_no      NUMBER DEFAULT 1,
                           p_page_rows    NUMBER DEFAULT 50,
                           p_org_countries VARCHAR2,
                            p_dest_countries VARCHAR2,
                            p_org_regions VARCHAR2,
                            p_dest_regions VARCHAR2,
                           p_tot_rec      OUT NUMBER,
                           P_tot_pages    OUT NUMBER,
                           p_rs           OUT resultset);

  PROCEDURE sellratesmstr_acc_proc_old(p_buyrateid NUMBER,p_versionno NUMBER);
  PROCEDURE sellratesmstr_acc_proc(p_buyrateid NUMBER,p_newversionno NUMBER,p_laneno NUMBER);
-- FUNCTION IND_ELEMENTS(P_IND VARCHAR2) RETURN VARCHAR2;
   PROCEDURE buy_rates_Expiry_proc(p_org_locs       VARCHAR2,
                                   p_dest_locs      VARCHAR2,
                                   p_terminal       VARCHAR2,
                                   p_rate_type      VARCHAR2,
                                   p_weight_break   VARCHAR2,
                                   p_srvlevl        VARCHAR2,
                                   p_carrier        VARCHAR2,
                                   p_shmode         VARCHAR2,
                                   p_operation      VARCHAR2,
                                   p_page_no        NUMBER DEFAULT 1,
                                   p_page_rows      NUMBER DEFAULT 50,
                                   p_org_countries  VARCHAR2,
                                   p_dest_countries VARCHAR2,
                                   p_org_regions    VARCHAR2,
                                   p_dest_regions   VARCHAR2,
                                   P_from_date        DATE,
                                   P_TO_DATE          DATE,
                                   p_tot_rec      OUT NUMBER,
                                   P_tot_pages    OUT NUMBER,
                                   p_rs           OUT resultset);
END;

/

/

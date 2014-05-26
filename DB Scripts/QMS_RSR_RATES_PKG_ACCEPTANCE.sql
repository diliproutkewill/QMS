--------------------------------------------------------
--  DDL for Package QMS_RSR_RATES_PKG_ACCEPTANCE
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_RSR_RATES_PKG_ACCEPTANCE" AS

  TYPE resultset IS REF CURSOR;

  g_err VARCHAR2(100);

  g_err_code VARCHAR2(100);

  PROCEDURE comman_proc(

                        p_shmode VARCHAR2,

                        p_consoltype VARCHAR2 DEFAULT NULL,

                        p_terminal VARCHAR2,

                        p_page_no NUMBER DEFAULT 1,

                        p_page_rows NUMBER DEFAULT 50,

                        sortBy VARCHAR2,

                        sortOrder VARCHAR2,

                        p_tot_rec OUT NUMBER,

                        P_tot_pages OUT NUMBER,

                        p_origin     VARCHAR2,

                        p_rs OUT resultset

                        );

END;

/

/

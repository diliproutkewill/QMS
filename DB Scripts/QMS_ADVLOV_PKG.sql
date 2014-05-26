--------------------------------------------------------
--  DDL for Package QMS_ADVLOV_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_ADVLOV_PKG" AS

   TYPE resultset IS REF CURSOR;



   g_err        VARCHAR2 (100);

   g_err_code   VARCHAR2 (100);



   PROCEDURE get_lov(

        p_terminal_id              IN  VARCHAR2 DEFAULT NULL,

        p_lov_id                   IN  VARCHAR2,

        p_where                    IN  VARCHAR2 DEFAULT NULL,

        p_page_no                  IN  NUMBER DEFAULT 1,

        p_title                    OUT VARCHAR2,

        p_pagination               OUT VARCHAR2,

        p_page_rows                OUT NUMBER,

        p_tot_col                  OUT NUMBER,

        p_tot_rec                  OUT NUMBER,

        p_width                    OUT NUMBER,

        p_height                   OUT NUMBER,

        p_col_rec                  OUT resultset,

        p_val_rec                  OUT resultset);



END;

/

/

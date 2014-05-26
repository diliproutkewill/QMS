--------------------------------------------------------
--  DDL for Package QMS_QUOTEPACK_NEW
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_QUOTEPACK_NEW" IS

  TYPE Resultset IS REF CURSOR;

  g_Err VARCHAR2(100);

  g_Err_Code VARCHAR2(100);

  PROCEDURE Qms_Quote_Update(p_Old_Sellcharge_Id NUMBER,
                             p_Old_Buycharge_Id  NUMBER,
                             p_Old_Lane_No       NUMBER,
                             p_Old_Version_No    NUMBER,--@@Added for the WPBN issues-146448,146968 on 19/12/08
                             p_New_Version_No    NUMBER,
                              p_New_Sellcharge_Id NUMBER,
                             p_New_Buycharge_Id  NUMBER,
                             p_New_Lane_No       NUMBER,
                             p_Sell_Buy_Flag     VARCHAR2,
                             p_Charge_Type       VARCHAR2,
                             p_Zone_Code         VARCHAR2,
                             p_Changedesc        VARCHAR2);

  /*PROCEDURE qms_updated_quotes_count (
     p_terminali                       d         VARCHAR2,
     p_rs           OUT   resultset
  );

  PROCEDURE qms_updated_quotes_info (
     p_change_desc         VARCHAR2,
     p_terminalid          VARCHAR2,
     p_rs            OUT   resultset
  );*/
  PROCEDURE Qms_Update_Quote(p_Quoteid     NUMBER,
                             p_Modifiedby  VARCHAR2,
                             p_Sellbuyflag VARCHAR2,
                             p_Changedesc  VARCHAR2,
                             p_Newquoteid  VARCHAR2,
                             p_Newuniqueid OUT NUMBER);

  PROCEDURE Qms_Updated_Modify_Quote(p_Quoteid     NUMBER,
                                     p_Sellbuyflag VARCHAR2,
                                     p_Changedesc  VARCHAR2,
                                     p_Rs          OUT Resultset,
                                     p_Rs1         OUT Resultset,
                                     p_Rs2         OUT Resultset,
                                     p_Rs3         OUT Resultset);

END Qms_Quotepack_New;

/

/

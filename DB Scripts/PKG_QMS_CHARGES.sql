--------------------------------------------------------
--  DDL for Package PKG_QMS_CHARGES
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "PKG_QMS_CHARGES" AS

  TYPE Cscolumns IS REF CURSOR;

  FUNCTION Buychargesadd(v_Charge_Id       VARCHAR2,
                         v_Charge_Desc_Id  VARCHAR2,
                         v_Charge_Basis_Id VARCHAR2,
                         p_Ratebreak       VARCHAR2,
                         p_Ratetype        VARCHAR2,
                         v_Terminalid      VARCHAR2,
                         p_Densityratio    VARCHAR2,
                         v_Currencyid      VARCHAR2,
                         v_Newbuychargeid  NUMBER,
                         p_Rs              OUT Cscolumns) RETURN VARCHAR2;

  FUNCTION Getterminallistbrscharges(v_Operation     VARCHAR2,
                                     v_Searchstr     VARCHAR2,
                                     v_Curterminalid VARCHAR2)
    RETURN Cscolumns;

  PROCEDURE Isexistinthehirarchy(v_Operation     VARCHAR2,
                                 v_Terminalid    VARCHAR2,
                                 v_Curterminalid VARCHAR2,
                                 v_Returnval     OUT VARCHAR2);

  FUNCTION Getbuysellchargeids(v_Chargeid     VARCHAR2,
                               v_Fromwhere    VARCHAR2,
                               v_Chargedescid VARCHAR2,
                               v_Terminalid   VARCHAR2,
                               V_CHARGEGROUPID VARCHAR2) RETURN Cscolumns;

  FUNCTION Getbuysellchargedescids(v_Chargedescid VARCHAR2,
                                   v_Fromwhere    VARCHAR2,
                                   v_Chargeid     VARCHAR2,
                                   v_Terminalid   VARCHAR2,
                                   v_charge_group_id VARCHAR2) RETURN Cscolumns;

  FUNCTION Validate_Sellchargesadd(v_Charge_Id       VARCHAR2,
                                   v_Charge_Desc_Id  VARCHAR2,
                                   v_Charge_Basis_Id VARCHAR2,
                                   p_Densityratio    VARCHAR2,
                                   v_Currencyid      VARCHAR2,
                                   v_Terminalid      VARCHAR2)
    RETURN VARCHAR2;

  FUNCTION Sellchargesadd(v_Charge_Id        VARCHAR2,
                          v_Charge_Desc_Id   VARCHAR2,
                          v_Charge_Basis_Id  VARCHAR2,
                          p_Rate_Break       VARCHAR2,
                          p_New_Sellchargeid NUMBER,
                          p_New_Buychargeid  NUMBER,
                          v_Terminalid       VARCHAR2,
                          v_Dummyflag        VARCHAR2,
                          p_Densityratio     VARCHAR2) RETURN VARCHAR2;

  PROCEDURE Loadbuychargedtlsforsell(v_Charge_Id      VARCHAR2,
                                     v_Charge_Desc_Id VARCHAR2,
                                     v_Terminalid     VARCHAR2,
                                     Mstrlist         OUT Pkg_Qms_Charges.Cscolumns,
                                     Chldlist         OUT Pkg_Qms_Charges.Cscolumns);

  FUNCTION Getchargedescidsalllevels(p_Charge_Desc_Id VARCHAR2,
                                     p_Charge_Id      VARCHAR2,
                                     p_Shipmode       VARCHAR2,
                                     p_Terminalid     VARCHAR2,
                                     P_CHARGE_GRUOP_ID VARCHAR2)
    RETURN Pkg_Qms_Charges.Cscolumns;

  PROCEDURE Buy_Chages_Bkup_Proc(p_Oldbuychargeid VARCHAR2,
                                 p_Newbuychargeid VARCHAR2,
                                 p_Weightbreak    VARCHAR2,
                                 p_Ratetype       VARCHAR2,
                                 p_Terminal       VARCHAR2,
                                 p_Exp            OUT NUMBER);

  PROCEDURE Update_Chargedesc_Dtls(p_Currterminalid VARCHAR2,
                                   p_Rs_1           OUT Cscolumns,
                                   p_Rs_2           OUT Cscolumns);

  PROCEDURE Chargegroup_Dtl_Proc(p_Process        VARCHAR2,
                                 p_Currterminalid VARCHAR2,
                                 p_Rs_1           OUT Cscolumns,
                                 p_Rs_2           OUT Cscolumns);

  PROCEDURE Validate_Charge_Desc(p_Currterminalid VARCHAR2,
                                 p_Rs_1           OUT Cscolumns,
                                 p_Rs_2           OUT Cscolumns);

  PROCEDURE Validate_Charge_Group_Dtl_Add(p_Charge_Group_Id VARCHAR2,
                                          p_Charge_Id       VARCHAR2,
                                          p_Charge_Desc_Id  VARCHAR2,
                                          p_Shipment_Mode   VARCHAR2,
                                          p_Terminal_Id     VARCHAR2,
                                          p_Success_Flag    OUT VARCHAR2,
                                          p_Remarks         OUT VARCHAR2);

  --@@Added by Anil.k for CR 231214 on 25Jan2011
  PROCEDURE Validate_Charge_Group_Dtl_Add(p_Charge_Group_Id VARCHAR2,
                                          p_Charge_Id       VARCHAR2,
                                          p_Charge_Desc_Id  VARCHAR2,
                                          p_Shipment_Mode   VARCHAR2,
                                          p_Terminal_Id     VARCHAR2,
                                          p_orgCountry_id   VARCHAR2,
                                          p_destCountry_id  VARCHAR2,
                                          p_Success_Flag    OUT VARCHAR2,
                                          p_Remarks         OUT VARCHAR2);
   --@@Ended by Anil.k for CR 231214 on 25Jan2011

  PROCEDURE Validate_Chargegrp_Dtl_Modify(p_Charge_Group_Id VARCHAR2,
                                          p_Charge_Id       VARCHAR2,
                                          p_Charge_Desc_Id  VARCHAR2,
                                          p_Terminal_Id     VARCHAR2,
                                          p_Currterminalid  VARCHAR2,
                                          p_Success_Flag    OUT VARCHAR2,
                                          p_Shipment_Mode   OUT VARCHAR2,
                                          p_Remarks         OUT VARCHAR2);

   --@@Added by Anil.k for CR 231214 on 25Jan2011
  PROCEDURE  VALIDATE_CHARGEGRP_DTL_MODIFY(p_charge_group_id VARCHAR2,
                                           p_charge_id       VARCHAR2,
                                           p_charge_desc_id  VARCHAR2,
                                           p_terminal_id     VARCHAR2,
                                           p_currterminalid  VARCHAR2,
                                           p_orgCountry_id   VARCHAR2,
                                           p_destCountry_id  VARCHAR2,
                                           p_success_flag    OUT VARCHAR2,
                                           p_shipment_mode   OUT VARCHAR2,
                                           p_remarks         OUT VARCHAR2);
   --@@Ended by Anil.k for CR 231214 on 25Jan2011

  FUNCTION Validate_Densityratio(p_Densityratio    VARCHAR2,
                                 v_Charge_Basis_Id VARCHAR2) RETURN VARCHAR2;

   FUNCTION sellcharges_insert( p_Charge_Id       VARCHAR2,
                               p_Charge_Desc_Id  VARCHAR2,
                               p_Terminal_Id     VARCHAR2
                             ) RETURN        NUMBER;

END;

/

/

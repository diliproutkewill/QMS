--------------------------------------------------------
--  DDL for Package PKG_QMS_CARTAGE_ACC
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "PKG_QMS_CARTAGE_ACC" AS

TYPE resultset IS REF CURSOR;
PROCEDURE cartage_activeinactive(p_cartage_id_old VARCHAR2,
						   p_cartage_id_new VARCHAR2,
						   p_weight_break   VARCHAR2,
						   p_rate_type      VARCHAR2,
						   p_location_id    VARCHAR2,
						   p_zone_code      VARCHAR2,
						   p_charge_basis   VARCHAR2,
						   p_charge_type    VARCHAR2,
               p_shipment_mode  VARCHAR2,
               p_console_type   VARCHAR2,
               p_count          NUMBER);

PROCEDURE cartage_update_sellcharges (p_cartage_id_old VARCHAR2,
						      p_cartage_id_new VARCHAR2,
						      p_location_id    VARCHAR2,
						      p_zone_code      VARCHAR2,
						      p_charge_type    VARCHAR2,
                  p_weight_break     VARCHAR2,
                  p_rate_type        VARCHAR2,
                  p_shipment_mode    VARCHAR2,
                  p_console_type     VARCHAR2 );

PROCEDURE cartage_buycharges (
                  p_operation      VARCHAR2,
						      p_terminalid     VARCHAR2,
						      p_chargeType     VARCHAR2,
						      p_rate_type      VARCHAR2,
                  p_location_id    VARCHAR2,
                  p_shipment_mode  VARCHAR2,
                  p_zonecode       VARCHAR2,
						      p_weightBreak    VARCHAR2,
                  p_dtls_data     OUT       resultset,
                  p_wtbrks_data     OUT       resultset);
/*--------------------------------------------------------------------------------------------

  TYPE cartage_buycharges_rectype IS RECORD
  (
    CARTAGE_ID			          QMS_CARTAGE_BUYSELLCHARGES.CARTAGE_ID%TYPE ,
    MAX_CHARGEPER_TRUCKLOAD   QMS_CARTAGE_BUYSELLCHARGES.MAX_CHARGEPER_TRUCKLOAD%TYPE ,
    ZONE_CODE               	QMS_CARTAGE_BUYDTL.ZONE_CODE%TYPE ,
    CHARGESLAB			          QMS_CARTAGE_BUYDTL.CHARGESLAB%TYPE,
    CHARGERATE               	QMS_CARTAGE_BUYDTL.CHARGERATE%TYPE ,
    CHARGE_TYPE               QMS_CARTAGE_BUYDTL.CHARGE_TYPE%TYPE ,
    UOM                      	QMS_CARTAGE_BUYSELLCHARGES.UOM%TYPE ,
    EFFECTIVE_FROM           	QMS_CARTAGE_BUYSELLCHARGES.EFFECTIVE_FROM%TYPE ,
    VALID_UPTO               	QMS_CARTAGE_BUYSELLCHARGES.VALID_UPTO%TYPE ,
    CHARGERATE_INDICATOR     	QMS_CARTAGE_BUYDTL.CHARGERATE_INDICATOR%TYPE ,
    LOWERBOUND               	QMS_CARTAGE_BUYDTL.LOWERBOUND%TYPE ,
    UPPERBOUND               	QMS_CARTAGE_BUYDTL.UPPERBOUND%TYPE ,
    DENSITY_CODE             	QMS_CARTAGE_BUYDTL.DENSITY_CODE%TYPE
	);

procedure cartage_buycharges1(
                  p_operation      varchar2,
						      p_terminalid     varchar2,
						      p_chargeType     varchar2,
						      p_rate_type      varchar2,
                  p_location_id    varchar2,
                  p_chargeBasis    varchar2,
                  p_zonecode       varchar2,
						      p_weightBreak    varchar2,
                  p_dtls_data     OUT       resultset,
                  p_wtbrks_data     OUT       resultset);


--------------------------------------------------------------------------------------------*/
PROCEDURE cartage_check_for_sellcharges(
                   p_operation      VARCHAR2,
						      p_terminalid     VARCHAR2,
						      p_chargeType     VARCHAR2,
                  p_location_id    VARCHAR2,
                  P_Zone           VARCHAR2,
						      p_dtls_data     OUT   VARCHAR2
                 );
PROCEDURE cartage_check_for_buycharges(
                   p_operation      VARCHAR2,
						      p_terminalid     VARCHAR2,
						      p_chargeType     VARCHAR2,
                  p_location_id    VARCHAR2,
                  P_Zone           VARCHAR2,
						      p_dtls_data     OUT   VARCHAR2
                 );

   PROCEDURE buy_cartage_update_quote (
      p_terminalid          VARCHAR2,
      p_chargetype          VARCHAR2,
      p_location_id         VARCHAR2,
      p_zone                VARCHAR2,
      p_shipment_mode       VARCHAR2,
      p_console_type        VARCHAR2,
      P_NEWBUYCARTAGEID     VARCHAR2,
      p_excep         OUT   VARCHAR2
   );

   PROCEDURE sell_cartage_update_quote (
      p_terminalid          VARCHAR2,
      p_chargetype          VARCHAR2,
      p_location_id         VARCHAR2,
      p_zone                VARCHAR2,
      p_shipment_mode       VARCHAR2,
      p_console_type        VARCHAR2,
      P_NEWSELLCARTAGEID    VARCHAR2,
      p_newbuycartageid     VARCHAR2,
      p_excep         OUT   VARCHAR2
   );

END;

/

/

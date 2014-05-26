--------------------------------------------------------
--  DDL for Package PKG_QMS_BUYRATES
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "PKG_QMS_BUYRATES" AS

  g_err VARCHAR2(100);

  g_err_code VARCHAR2(100);

  PROCEDURE qms_validate_buyrate(

                                 p_shipment_mode VARCHAR2,

                                 p_origin VARCHAR2,

                                 p_destination VARCHAR2,

                                 p_servicelevel_id VARCHAR2,

                                 p_carrier_id VARCHAR2,

                                 p_effective_from DATE,

                                 p_validate_upto DATE,

                                 p_weight_break VARCHAR2,

                                 p_rate_type VARCHAR2,

                                 p_current_terminal_id VARCHAR2,

                                 p_frequency VARCHAR2,

                                 p_density VARCHAR2,

                                 p_uom VARCHAR2

                                 );

  -- RETURN NUMBER;

   FUNCTION validate_buyrate_new(

                            p_shipment_mode VARCHAR2,

                            p_origin VARCHAR2,

                            p_destination VARCHAR2,

                            p_service_level_id VARCHAR2,

                            p_carrier_id VARCHAR2,

                            p_effective_from DATE,

                            p_validate_upto DATE,

                            p_weight_break VARCHAR2,

                            p_rate_type VARCHAR2,

                            p_current_terminal_id VARCHAR2,

                            p_frequency VARCHAR2,

                            p_density    VARCHAR2,

                            p_currency    VARCHAR2)

   RETURN VARCHAR2;










  FUNCTION validate_buyrate(

                            p_shipment_mode VARCHAR2,

                            p_origin VARCHAR2,

                            p_destination VARCHAR2,

                            p_service_level_id VARCHAR2,

                            p_carrier_id VARCHAR2,

                            p_effective_from DATE,

                            p_validate_upto DATE,

                            p_weight_break VARCHAR2,

                            p_rate_type VARCHAR2,

                            p_current_terminal_id VARCHAR2,

                            p_frequency VARCHAR2


                            )

   RETURN VARCHAR2;

  FUNCTION validate_carrier(

                            p_terminal_id VARCHAR2,

                            p_shipment_mode VARCHAR2,

                            p_carrier_id VARCHAR2

                            )

   RETURN VARCHAR2;

  FUNCTION validate_currency_carrier(

                                     p_terminal_id VARCHAR2,

                                     p_shipment_mode_str VARCHAR2,

                                     p_carrier_id VARCHAR2,

                                     p_currency_id VARCHAR2

                                     )

   RETURN VARCHAR2;

  FUNCTION validate_location(

                             p_location_id VARCHAR2,

                             --p_shipment_mode_str   VARCHAR2,

                             p_shipment_mode VARCHAR2

                             )

   RETURN VARCHAR2;

  FUNCTION validate_org_dest_sl(

                                p_origin_id VARCHAR2,

                                p_destination_id VARCHAR2,

                                p_terminal_id VARCHAR2,

                                p_shipment_mode_str VARCHAR2,

                                p_shipmemt_mode VARCHAR2,

                                p_servicelevel_id VARCHAR2,

                                p_density VARCHAR2,

                                p_uom VARCHAR2

                                )

   RETURN VARCHAR2;

  FUNCTION validate_servicelevel(

                                 p_terminal_id VARCHAR2,

                                 p_shipment_mode VARCHAR2,

                                 p_servicelevel_id VARCHAR2

                                 )

   RETURN VARCHAR2;

  FUNCTION validate_upd_insert_buyrate(

                                       p_shipment_mode VARCHAR2,

                                       p_origin VARCHAR2,

                                       p_destination VARCHAR2,

                                       p_servicelevel_id VARCHAR2,

                                       p_carrier_id VARCHAR2,

                                       p_effective_from DATE,

                                       p_validate_upto DATE,

                                       p_weight_break VARCHAR2,

                                       p_rate_type VARCHAR2,

                                       p_current_terminal_id VARCHAR2,

                                       p_frequency VARCHAR2,

                                       p_density VARCHAR2,

                                       p_newbuyrateid VARCHAR2,

                                       p_newlaneno VARCHAR2,

                                      p_transittime         VARCHAR2,

                                      p_type                VARCHAR2,

                                      p_currency            VARCHAR2) --@@Added by Kameswari for the WPBN issue-146448 on 20/12/08

   RETURN VARCHAR2;

  FUNCTION comb_freq_func(p_str VARCHAR2)

   RETURN VARCHAR2;

  TYPE resultset IS REF CURSOR;

  PROCEDURE quote_view_proc(

                            p_quoteid NUMBER,

                            p_rs OUT resultset,

                            p_rs1 OUT resultset,

                            p_rs2 OUT resultset

                            );

  /*FUNCTION validate_density_ratio (p_density VARCHAR2,p_shipmemt_mode VARCHAR2)

     RETURN NUMBER;

  */

  FUNCTION validate_density_ratio(p_density       VARCHAR2,
                                  p_shipmemt_mode VARCHAR2,
                                  p_uom           VARCHAR2)

   RETURN NUMBER;

  PROCEDURE Qms_INACTIVATE_Buyrate(

                                   p_shipment_mode VARCHAR2,

                                   p_origin VARCHAR2,

                                   p_destination VARCHAR2,

                                   p_servicelevel_id VARCHAR2,

                                   p_carrier_id VARCHAR2,

                                   p_effective_from DATE,

                                   p_validate_upto DATE,

                                   p_weight_break VARCHAR2,

                                   p_rate_type VARCHAR2,

                                   p_current_terminal_id VARCHAR2,

                                   p_frequency VARCHAR2,

                                   p_newbuyrateid VARCHAR2,

                                   p_newlaneno VARCHAR2

                                   );



 PROCEDURE qms_update_new_quote(  p_sellbuyflag VARCHAR2,

                                 p_origin VARCHAR2,

                                 p_destination VARCHAR2,

                                 p_servicelevel_id VARCHAR2,

                                 p_carrier_id VARCHAR2,

                                 p_validate_upto DATE,

                                 p_frequency VARCHAR2,

                                 p_transittime VARCHAR2,

                                 p_newbuyrateid  VARCHAR2,

                                  p_newsellrateid VARCHAR2,

                                   p_oldsellrateid VARCHAR2,

                                 p_newversionno   NUMBER,

                                 p_newlaneno   NUMBER,
                                 P_terminalid  VARCHAR2) ;--@@Modified by subrahmanyam for issue on 17-apr-9

END Pkg_Qms_Buyrates;

/

/

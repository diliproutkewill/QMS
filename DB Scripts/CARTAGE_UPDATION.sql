--------------------------------------------------------
--  DDL for Procedure CARTAGE_UPDATION
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "CARTAGE_UPDATION" AS
BEGIN
  FOR i IN (SELECT DISTINCT dtl.cartage_id, qr.QUOTE_ID, qr.BUYRATE_ID, qr.sellrate_id,
                            qr.CHARGE_DESCRIPTION, qr.CHARGE_AT,
                            qr.sell_buy_flag, dtl.sell_cartage_id,
                            DECODE(CHARGE_AT, 'Pickup', qm.shipperzones, 'Delivery', qm.consigneezones)
              FROM qms_quote_rates qr, qms_cartage_selldtl dtl,
                   qms_quote_master qm
             WHERE dtl.cartage_id=qr.sellrate_id
               AND dtl.activeinactive='A'
               AND sell_buy_flag IN ('BC','SC')
               AND qm.id=qr.quote_id AND qm.active_flag='A'
               AND DECODE(CHARGE_AT,'Pickup',qm.shipperzones,'Delivery',qm.consigneezones)=dtl.zone_code
               AND charge_at=dtl.charge_type
              ORDER BY qr.quote_id,qr.sellrate_id)
  LOOP
    UPDATE qms_quote_rates
       SET sellrate_id=i.sell_cartage_id
     WHERE quote_id=i.QUOTE_ID
       AND sell_buy_flag=i.sell_buy_flag
       AND buyrate_id=i.cartage_id
       AND CHARGE_AT=i.CHARGE_AT;
  END LOOP;
END;

/

/

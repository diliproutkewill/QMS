--------------------------------------------------------
--  DDL for Procedure GETMULTIQUOTEMASTERDATA
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "GETMULTIQUOTEMASTERDATA" (p_quote_id       IN VARCHAR2,

                                       p_singleReturn OUT qms_quote_pack.RESULTSET,
                                       p_count OUT qms_quote_pack.RESULTSET,
                                       p_multipleReturn OUT qms_quote_pack.RESULTSET

                                   ) IS

BEGIN

 OPEN p_singleReturn FOR
 SELECT DISTINCT QMS.QUOTE_ID,
                 QMS.SHIPMENT_MODE,
                 QMS.VERSION_NO,
                 QMS.PREQUOTE_ID,
                 QMS.QUOTE_STATUS,
                 QMS.IU_FLAG,
                 QMS.EFFECTIVE_DATE,
                 QMS.VALID_TO,
                 QMS.CREATED_DATE,
                 QMS.MODIFIED_DATE,
                 QMS.ACCEPT_VALIDITYPERIOD,
                 QMS.CUSTOMER_ID,
                 QMS.CUSTOMER_ADDRESSID,
                 QMS.CREATED_BY,
                 QMS.TERMINAL_ID,
                 QMS.SALES_PERSON,
                 QMS.SALES_PERSON_EMAIL_FLAG,
                 QMS.OVERLENGTH_CARGONOTES,
                 QMS.INDUSTRY_ID,
                 QMS.COMMODITY_ID,
                 QMS.HAZARDOUS_IND,
                 QMS.UN_NUMBER,
                 QMS.CLASS,
                 QMS.SERVICE_LEVEL_ID,
                 QMS.QUOTING_STATION,
                 QMS.PN_FLAG,
                 QMS.ACTIVE_FLAG,
                 QMS.COMPLETE_FLAG,
                 QMS.ESCALATION_FLAG,
                 QMS.UPDATE_FLAG,
                 QMS.QUOTE_STATUS,
                 QMS.IE_FLAG,
                 QMS.SENT_FLAG,
                 QMS.EMAIL_FLAG,
                 QMS.FAX_FLAG,
                 QMS.PRINT_FLAG,
                 QMS.ESCALATED_TO,
                 QMS.SHIPPER_CONSOLE_TYPE,
                 QMS.CONSIGNEE_CONSOLE_TYPE,
                 QMS.OVERLENGTH_CARGONOTES,
                 QMS.MULTI_QUOTE_WEIGHT_BREAK,
                 QMS.MULTI_QUOTE_WITH
                 ,QMS.Cust_Requested_Date -- Added by Rakesh on 23-02-2011 for Issue:236359
                 ,QMS.CUST_REQUESTED_TIME -- Added by Rakesh on 23-02-2011 for Issue:236359
                 --,QMS.ID                  -- Added by Rakesh on 17-03-2011 for Issue:240224
   FROM QMS_QUOTE_MASTER QMS
  WHERE QMS.QUOTE_ID = p_quote_id
    AND QMS.VERSION_NO =
        (SELECT MAX(QMS.VERSION_NO)
           FROM QMS_QUOTE_MASTER QMS
          WHERE QMS.QUOTE_ID = p_quote_id);
--kiran.v remove distinct
 OPEN p_count FOR
 SELECT COUNT(1) FROM (SELECT  QMS.ORIGIN_LOCATION, QMS.ORIGIN_PORT, QMS.DEST_LOCATION, QMS.DESTIONATION_PORT,  QMS.SHIPPER_ZIPCODE,  QMS.SHIPPERZONES,  QMS.CONSIGNEE_ZIPCODE,  QMS.CONSIGNEEZONES,  QMS.INCO_TERMS_ID,  QMS.CARGO_ACC_TYPE,  QMS.CARGO_ACC_PLACE,  QMS.ROUTING_ID  FROM QMS_QUOTE_MASTER QMS  WHERE QMS.QUOTE_ID = p_quote_id  AND QMS.VERSION_NO =  (SELECT MAX(QMS.VERSION_NO)  FROM QMS_QUOTE_MASTER QMS  WHERE QMS.QUOTE_ID = p_quote_id));

 OPEN p_multipleReturn FOR
 SELECT DISTINCT QMS.ORIGIN_LOCATION,
                 QMS.ORIGIN_PORT,
                 QMS.DEST_LOCATION,
                 QMS.DESTIONATION_PORT,
                 QMS.SHIPPER_ZIPCODE,
                 QMS.SHIPPERZONES,
                 QMS.CONSIGNEE_ZIPCODE,
                 QMS.CONSIGNEEZONES,
                 QMS.INCO_TERMS_ID,
                 QMS.CARGO_ACC_TYPE,
                 QMS.CARGO_ACC_PLACE,
                 QMS.ROUTING_ID,
                 QMS.SPOT_RATES_FLAG,
                 QMS.MULTI_LANE_ORDER,
                 DECODE(QMS.SPOT_RATES_FLAG,
                        'Y',
                        DECODE((SELECT TO_CHAR(COUNT(DISTINCT(SPR.RATE_DESCRIPTION)))
                           FROM QMS_QUOTE_SPOTRATES SPR
                          WHERE SPR.QUOTE_ID = QMS.ID
                            AND SPR.SURCHARGE_ID IS NOT NULL),0,QMS.SPOT_SUR_COUNT,
                            (SELECT TO_CHAR(COUNT(DISTINCT(SPR.RATE_DESCRIPTION)))
                           FROM QMS_QUOTE_SPOTRATES SPR
                          WHERE SPR.QUOTE_ID = QMS.ID
                            AND SPR.SURCHARGE_ID IS NOT NULL))) SPOTRATESURCHARGECOUNT,
               (SELECT COUNT(1) FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID =QMS.ID AND QMS.QUOTE_ID = p_quote_id AND QMS.Complete_Flag = 'I') INCOMPLETE_SCREEN

   FROM QMS_QUOTE_MASTER QMS
  WHERE QMS.QUOTE_ID = p_quote_id
    AND QMS.VERSION_NO =
        (SELECT MAX(QMS.VERSION_NO)
           FROM QMS_QUOTE_MASTER QMS
          WHERE QMS.QUOTE_ID = p_quote_id)
  ORDER BY QMS.MULTI_LANE_ORDER;


END;

/

/

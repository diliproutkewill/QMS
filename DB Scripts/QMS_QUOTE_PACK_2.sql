--------------------------------------------------------
--  DDL for Package QMS_QUOTE_PACK_2
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "QMS_QUOTE_PACK_2" AS
	TYPE RESULTSET IS REF CURSOR;
	G_ERR      VARCHAR2(100);
	G_ERR_CODE VARCHAR2(100);

	PROCEDURE QUOTE_SELL_BUY_RATES_PROC_2(P_ORG_LOC      VARCHAR2,
																				P_DEST_LOC     VARCHAR2,
																				P_TERMINAL     VARCHAR2,
																				P_SRVLEVEL     VARCHAR2,
																				P_SHMODE       VARCHAR2,
																				P_WEIGHT_BREAK VARCHAR2,
																				P_PERMISSION   VARCHAR2 DEFAULT 'Y',
																				P_OPERATION    VARCHAR2,
																				P_QUOTE_ID     VARCHAR2,
																				P_RS           OUT RESULTSET);
END;

/

/

--------------------------------------------------------
--  DDL for Package UOM_UTILS
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "UOM_UTILS" 
AS
	FUNCTION
	CONVERT_WEIGHT(WEIGHT NUMBER,SOURCE_UOW VARCHAR2,TARGET_UOW VARCHAR2)
	RETURN NUMBER;

	FUNCTION
	CONVERT_VOLUME(VOLUME NUMBER,SOURCE_UOV VARCHAR2,
		      TARGET_UOV VARCHAR2)
	RETURN NUMBER;

	FUNCTION
	CONVERT_VOLUME_TO_WEIGHT(VOLUME NUMBER,SOURCE_UOV VARCHAR2,
		      TARGET_UOW VARCHAR2, TARGET_SHIPMENTMODE NUMBER)
	RETURN NUMBER;

	FUNCTION
	CONVERT_WEIGHT_TO_VOLUME(WEIGHT NUMBER,SOURCE_UOW VARCHAR2,
		      TARGET_UOV VARCHAR2, TARGET_SHIPMENTMODE NUMBER)
	RETURN NUMBER;

END UOM_UTILS;

/

/
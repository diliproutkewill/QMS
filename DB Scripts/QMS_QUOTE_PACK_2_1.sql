--------------------------------------------------------
--  DDL for Package Body QMS_QUOTE_PACK_2
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "QMS_QUOTE_PACK_2" AS
	PROCEDURE QUOTE_SELL_BUY_RATES_PROC_2(P_ORG_LOC      VARCHAR2,
																				P_DEST_LOC     VARCHAR2,
																				P_TERMINAL     VARCHAR2,
																				P_SRVLEVEL     VARCHAR2,
																				P_SHMODE       VARCHAR2,
																				P_WEIGHT_BREAK VARCHAR2,
																				P_PERMISSION   VARCHAR2 DEFAULT 'Y',
																				P_OPERATION    VARCHAR2,
																				P_QUOTE_ID     VARCHAR2,
																				P_RS           OUT RESULTSET) AS
		V_RC_C1      RESULTSET;
		V_TERMINALS  VARCHAR2(32000);
		V_CHARGERATE VARCHAR2(400) := '';
		K            NUMBER := 0;
		V_SQL1       VARCHAR2(2000);
		V_SQLBNEW    VARCHAR2(2000);
		V_SQLSNEW    VARCHAR2(2000);
		V_SQLTBNEW   VARCHAR2(2000);
		V_SQLTSNEW   VARCHAR2(2000);
		V_SQL2       VARCHAR2(2000);
		V_SQL4       VARCHAR2(2000) := '';
		V_SQL5       VARCHAR2(2000);
		V_SQL6       VARCHAR2(32767);
		V_SQL7       VARCHAR2(2000);
		V_SQL10      VARCHAR2(2000);
		V_SQL11      VARCHAR2(2000);
		V_SQL12      VARCHAR2(32767);
		V_SQL13      VARCHAR2(2000);
		V_SQL14      VARCHAR2(2000);
		V_SQL15      VARCHAR2(2000);
		V_SQL16      VARCHAR2(2000);
		V_SQL17      VARCHAR2(2000);
		V_SQL18      VARCHAR2(2000);
		V_SQL19      VARCHAR2(2000);

		V_SQL20      VARCHAR2(2000);
		V_SQL21      VARCHAR2(2000);

		V_BASE       VARCHAR2(30);
		V_BREAK      VARCHAR2(4000);
		V_BREAK1     VARCHAR2(4000);
		V_TEMP       VARCHAR2(200);
		V_BREAK_SLAB VARCHAR2(40);
		V_BREAK_SLAB_BAF VARCHAR2(40);
		V_BREAK_SLAB_CAF VARCHAR2(40);
		V_BREAK_SLAB_CSF VARCHAR2(40);
		V_BREAK_SLAB_PSS VARCHAR2(40);
		V_OPR_ADM_FLAG   VARCHAR2(3);
		V_RCB_FLAG       VARCHAR2(10);
		V_SELLBUYFLAG    VARCHAR2(10);
		V_SELLRATEID     NUMBER;
		V_BUYRATEID      NUMBER;
		V_LANENO         NUMBER;
		V_ID             NUMBER;
		V_QUOTERATEID    NUMBER;
		V_CON_BUYRATEID  NUMBER;
		V_CON_SELLRATEID NUMBER;
		V_CON_LANENO     NUMBER;
		V_FLAG           VARCHAR2(200);
		V_CHECKEDFLAG    VARCHAR2(200);
		V_VERSIONNO      NUMBER;
		V_TEMP1          NUMBER;
		V_BASE1          NUMBER;
		v_rd             VARCHAR2(200); --added for 180164
		TYPE ARRAY IS TABLE OF QMS_QUOTE_RATES.BREAK_POINT%TYPE;
		BREAK_POINT_LIST ARRAY;
	BEGIN
		SELECT OPER_ADMIN_FLAG
			INTO V_OPR_ADM_FLAG
			FROM FS_FR_TERMINALMASTER
		 WHERE TERMINALID = P_TERMINAL;
		IF UPPER(TRIM(V_OPR_ADM_FLAG)) = 'H' THEN
			V_TERMINALS := 'SELECT 1 FROM FS_FR_TERMINALMASTER TM where TM.terminalid= ';
		ELSE
			DBMS_SESSION.SET_CONTEXT('QUOTE_CONTEXT',
															 'v_terminal_id',
															 P_TERMINAL);
			V_TERMINALS := 'SELECT 1 FROM FETCH_TERMINAL_ID_VIEW TV where TV.term_id = ';

		END IF;

		V_SQL1     := 'insert into GT_TEMP_DATA_1(BUYRATEID, WEIGHT_BREAK_SLAB, CHARGERATE,LANE_NO, LINE_NO,ID_FLAG,WEIGHT_BREAK,REC_BUYRATEID,TEMP_CHECK,RATE_DESCRIPTION) '; --ADDED BY SUBRAHMANYAM FOR 180164
		V_SQL2     := 'SELECT distinct to_number(qbd.buyrateid) buyrateid, upper(qbd.weight_break_slab)  weight_break_slab,';
		V_SQL4     := ' qbd.CHARGERATE ,qbd.lane_no lane_no, qbd.line_no line_no,''BR'' id_flag,UPPER(qbm.weight_break)wtbreak ,''''  REC_BUYRATEID ,(select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAM FOR 180164
		V_SQL5     := '  FROM qms_buyrates_dtl qbd, qms_buyrates_master qbm WHERE qbd.buyrateid = qbm.buyrateid AND (qbm.lane_no=qbd.lane_no or qbm.lane_no is null) and qbd.origin =:v_org_loc AND qbd.destination=:v_dest_loc AND qbd.service_level LIKE :v_srvlevel';
		V_SQL6     := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no  AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode and exists(' ||
									V_TERMINALS || 'qbm.TERMINALID)  ';
		V_SQL6     := '  AND (qbd.activeinactive IS NULL OR qbd.activeinactive = ''A'') and qbm.version_no = qbd.version_no AND qbd.generated_flag IS NULL AND qbm.shipment_mode =:v_shmode and exists(' ||
									V_TERMINALS || 'qbm.TERMINALID)  ';
		V_SQLTBNEW := ' SELECT  buyrateid,weight_break_slab,CHARGERATE, lane_no,line_no,id_flag,wtbreak ,REC_BUYRATEID,test_check,RATE_DESCRIPTION from (';
		V_SQL7     := ' SELECT distinct to_number(sm.rec_con_id) buyrateid, UPPER(sd.weightbreakslab) weight_break_slab,';
		V_SQL10    := ' sd.CHARGERATE CHARGERATE, sd.lane_no lane_no, sd.line_no line_no,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') id_flag,upper(sm.weight_break) wtbreak,TO_CHAR(SD.BUYRATEID) REC_BUYRATEID,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION'; --ADDED BY SUBRAHMANYAMF FOR 180164
		V_SQL11    := ' FROM qms_rec_con_sellratesmaster sm, qms_rec_con_sellratesdtl sd, qms_buyrates_dtl qbd WHERE sm.rec_con_id = sd.rec_con_id  AND sd.origin =: v_org_loc AND sd.destination=:v_dest_loc AND sd.servicelevel_id LIKE :v_srvlevel and sd.buyrateid=qbd.buyrateid  and sd.version_no=qbd.version_no and sd.lane_no=qbd.lane_no and sd.line_no=qbd.line_no ';
		V_SQL12    := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode AND  EXISTS (' ||
									V_TERMINALS ||
									'Sm.TERMINALID )) where test_check is not null and RATE_DESCRIPTION LIKE ''A FREIGHT RATE'' AND wtbreak =: v_weightBk  UNION all ';
	  V_SQL20    := ' AND sd.ai_flag =''A''   AND sd.ACCEPTANCE_FLAG IS NULL AND (sd.INVALIDATE=''F'' or sd.INVALIDATE is null) AND sm.shipment_mode =: v_shmode AND  EXISTS (' ||
									V_TERMINALS ||
									'Sm.TERMINALID )) where test_check is not null and wtbreak =: v_weightBk  UNION all ';
		V_SQL13    := ' insert into GT_BASE_DATA(BUYRATEID, VERSION_NO,CARRIER_ID, ORIGIN, DESTINATION, SERVICE_LEVEL,SERVICE_LEVEL_DESC,WEIGHT_BREAK_SLAB, TRANSIT_TIME,FREQUENCY, CHARGERATE, LANE_NO,RCB_FLAG, REC_BUYRATE_ID,NOTES,WEIGHT_BREAK,TERMINALID,currency,wt_class,EFROM,VALIDUPTO,CONSOLE_TYPE,TEMP_CHECK,RATE_DESCRIPTION,EXTERNAL_NOTES)'; -- ADDED BY SUBRAHMANYAM FOR 180164
		V_SQLBNEW  := 'select BUYRATEID,version_no,CARRIER_ID carrier_id,ORIGIN,DESTINATION, SERVICE_LEVEL,SDESC,weight_break_slab,TRANSIT_TIME,FREQUENCY,charge_rate,LANE_NO, RCB_FLAG, REC_BUYRATE_ID,NOTES, wtbreak,TERMINALID,CURRENCY,WEIGHT_CLASS, efrom, validupto,console_type,test_check,RATE_DESCRIPTION,EXTERNAL_NOTES from ('; --ADDED BY SUBRAHMANYAM FOR 180164
		V_SQL14    := ' select distinct to_char(qbd.BUYRATEID) BUYRATEID,qbd.version_no version_no,qbd.CARRIER_ID,qbd.ORIGIN, qbd.DESTINATION,qbd.SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=qbd.SERVICE_LEVEL)SDESC,''a'' weight_break_slab, qbd.TRANSIT_TIME TRANSIT_TIME,qbd.FREQUENCY FREQUENCY,''b'' charge_rate,qbd.lane_no LANE_NO,''BR'' RCB_flag,'''' REC_BUYRATE_ID,qbd.NOTES NOTES,UPPER(QBM.WEIGHT_BREAK) wtbreak,QBM.TERMINALID TERMINALID,qbm.CURRENCY CURRENCY,qbm.WEIGHT_CLASS WEIGHT_CLASS,qbd.EFFECTIVE_FROM efrom, qbd.VALID_UPTO validupto,qbm.console_type console_type , (select 1 from qms_buyrates_dtl b where b.version_no = qbd.version_no and b.buyrateid=qbd.buyrateid and b.lane_no=qbd.lane_no and b.activeinactive IS NULL and b.line_no=''0'' AND (b.INVALIDATE IS NULL OR b.INVALIDATE=''F''))test_check,QBD.RATE_DESCRIPTION,QBD.EXTERNAL_NOTES'; --@@ADDED BY SUBRAHMANYAM FOR 180164
		V_SQL15    := 'SELECT distinct to_char(sD.REC_CON_ID) BUYRATEID,sd.version_no version_no,sD.CARRIER_ID carrier_id, sD.ORIGIN,sD.DESTINATION,sD.SERVICELEVEL_ID SERVICE_LEVEL,(SELECT DISTINCT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID=sD.SERVICELEVEL_ID)SDESC,''a'' weight_break_slab,sD.TRANSIT_TIME,sD.FREQUENCY,''b''  charge_rate,sD.LANE_NO,DECODE(sM.RC_FLAG,''R'',''RSR'',''C'',''CSR'') RCB_FLAG, to_char(sD.BUYRATEID) REC_BUYRATE_ID,sD.NOTES,UPPER(SM.WEIGHT_BREAK) wtbreak,SM.TERMINALID,sm.CURRENCY,sm.WEIGHT_CLASS,SD.EXTERNAL_NOTES ';
		V_SQL16    := ', (select DISTINCT qbd.EFFECTIVE_FROM FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no ) efrom';
		V_SQL17    := ', (select DISTINCT qbd.VALID_UPTO FROM qms_rec_con_sellratesdtl qsd,qms_buyrates_dtl qbd where qsd.BUYRATEID=qbd.BUYRATEID and qsd.LANE_NO=qbd.LANE_NO and qsd.REC_CON_ID=sd.REC_CON_ID AND qsd.BUYRATEID=sd.BUYRATEID AND qsd.lane_no=sd.lane_no AND qsd.version_no=sd.version_no AND qsd.version_no=qbd.version_no) validupto,sd.console_type,(select 1 from qms_rec_con_sellratesdtl b where b.rec_con_id=sd.rec_con_id and b.version_no = sd.version_no and b.buyrateid=sd.buyrateid and b.lane_no=sd.lane_no and b.acceptance_flag IS NULL and (b.invalidate is null or b.invalidate=''F'')and b.ai_flag=''A'' and b.line_no=''0'')test_check,SD.RATE_DESCRIPTION';
		V_SQL18    := '  /*AND NOT EXISTS (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd WHERE qsd.ai_flag = ''A'' AND QBD.BUYRATEID = QSD.BUYRATEID AND QBD.LANE_NO = QSD.LANE_NO AND QSD.ACCEPTANCE_FLAG IS NULL AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*/ ) where test_check is not null and RATE_DESCRIPTION LIKE ''A FREIGHT RATE'' AND wtbreak =: v_weightBk order by buyrateid,lane_no ';
		V_SQL21    := '  /*AND NOT EXISTS (SELECT ''X'' FROM QMS_REC_CON_SELLRATESDTL qsd WHERE qsd.ai_flag = ''A'' AND QBD.BUYRATEID = QSD.BUYRATEID AND QBD.LANE_NO = QSD.LANE_NO AND QSD.ACCEPTANCE_FLAG IS NULL AND (Qsd.INVALIDATE = ''F'' or Qsd.INVALIDATE is null))*/ ) where test_check is not null AND wtbreak =: v_weightBk order by buyrateid,lane_no ';


		EXECUTE IMMEDIATE ('TRUNCATE TABLE GT_BASE_DATA');
		IF UPPER(P_OPERATION) = 'VIEW' THEN
			SELECT ID
				INTO V_QUOTERATEID
				FROM QMS_QUOTE_MASTER
			 WHERE QUOTE_ID = P_QUOTE_ID
				 AND VERSION_NO = (SELECT MAX(VERSION_NO)
														 FROM QMS_QUOTE_MASTER
														WHERE QUOTE_ID = P_QUOTE_ID);
			SELECT DISTINCT SELL_BUY_FLAG,
											SELLRATE_ID,
											BUYRATE_ID,
											RATE_LANE_NO,
											QR.VERSION_NO
				INTO V_SELLBUYFLAG,
						 V_SELLRATEID,
						 V_BUYRATEID,
						 V_LANENO,
						 V_VERSIONNO
				FROM QMS_QUOTE_RATES  QR,
						 QMS_QUOTE_MASTER QM,
						 FS_RT_PLAN       PL,
						 FS_RT_LEG        LEG
			 WHERE QR.QUOTE_ID = QM.ID
				 AND QM.QUOTE_ID = PL.QUOTE_ID
				 AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
				 AND LEG.ORIG_LOC = P_ORG_LOC
				 AND LEG.DEST_LOC = P_DEST_LOC
				 AND LEG.SERIAL_NO = QR.SERIAL_NO
				 AND QM.QUOTE_ID = P_QUOTE_ID
				 AND QM.VERSION_NO = (SELECT MAX(VERSION_NO)
																FROM QMS_QUOTE_MASTER
															 WHERE QUOTE_ID = P_QUOTE_ID)
				 AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR');
			IF V_SELLBUYFLAG = 'BR' THEN

				SELECT BREAK_POINT BULK COLLECT
					INTO BREAK_POINT_LIST
					FROM QMS_QUOTE_RATES  QR,
							 QMS_QUOTE_MASTER QM,
							 FS_RT_PLAN       PL,
							 FS_RT_LEG        LEG
				 WHERE QR.QUOTE_ID = QM.ID
					 AND QM.QUOTE_ID = PL.QUOTE_ID
					 AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
					 AND LEG.ORIG_LOC = P_ORG_LOC
					 AND LEG.DEST_LOC = P_DEST_LOC
					 AND LEG.SERIAL_NO = QR.SERIAL_NO
					 AND QM.QUOTE_ID = P_QUOTE_ID
					 AND QM.VERSION_NO =
							 (SELECT MAX(VERSION_NO)
									FROM QMS_QUOTE_MASTER
								 WHERE QUOTE_ID = P_QUOTE_ID)
					 AND QR.SELL_BUY_FLAG IN ('BR');
				FORALL I IN 1 .. BREAK_POINT_LIST.COUNT
					INSERT INTO GT_TEMP_DATA_1
						(BUYRATEID,
						 WEIGHT_BREAK_SLAB,
						 CHARGERATE,
						 LANE_NO,
						 LINE_NO,
						 ID_FLAG,
						 WEIGHT_BREAK,
						 REC_BUYRATEID,
						 RATE_DESCRIPTION)
						SELECT BD.BUYRATEID,
									 BD.WEIGHT_BREAK_SLAB,
									 BD.CHARGERATE,
									 BD.LANE_NO,
									 BD.LINE_NO,
									 'BR',
									 UPPER(BM.WEIGHT_BREAK),
									 '',
									 BD.RATE_DESCRIPTION
							FROM QMS_BUYRATES_DTL BD, QMS_BUYRATES_MASTER BM
						 WHERE BD.WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(I)
							 AND BD.BUYRATEID = V_BUYRATEID
							 AND BD.LANE_NO = V_LANENO
							 AND BD.VERSION_NO = BM.VERSION_NO
							 AND BD.VERSION_NO = V_VERSIONNO
							 AND BM.BUYRATEID = BD.BUYRATEID
							 AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL);
				INSERT INTO GT_BASE_DATA
					(BUYRATEID,
					 VERSION_NO,
					 CARRIER_ID,
					 ORIGIN,
					 DESTINATION,
					 SERVICE_LEVEL,
					 SERVICE_LEVEL_DESC,
					 WEIGHT_BREAK_SLAB,
					 TRANSIT_TIME,
					 FREQUENCY,
					 CHARGERATE,
					 LANE_NO,
					 RCB_FLAG,
					 REC_BUYRATE_ID,
					 NOTES,
					 WEIGHT_BREAK,
					 TERMINALID,
					 CURRENCY,
					 WT_CLASS,
					 EFROM,
					 VALIDUPTO,
					 SELECTED_FLAG,
					 CONSOLE_TYPE,
					 RATE_DESCRIPTION,
					 EXTERNAL_NOTES)
					SELECT DISTINCT BD.BUYRATEID,
													V_VERSIONNO,
													BD.CARRIER_ID,
													BD.ORIGIN,
													BD.DESTINATION,
													BD.SERVICE_LEVEL,
													(SELECT DISTINCT SERVICELEVELDESC
														 FROM FS_FR_SERVICELEVELMASTER
														WHERE SERVICELEVELID = BD.SERVICE_LEVEL) SERVICE_LEVEL_DESC,
													'a',
													BD.TRANSIT_TIME,
													BD.FREQUENCY,
													'b',
													BD.LANE_NO,
													'BR',
													'',
													BD.NOTES,
													UPPER(BM.WEIGHT_BREAK),
													BM.TERMINALID,
													BM.CURRENCY,
													BM.WEIGHT_CLASS,
													BD.EFFECTIVE_FROM,
													(SELECT DISTINCT BD.VALID_UPTO
														 FROM QMS_BUYRATES_DTL    BD,
																	QMS_BUYRATES_MASTER BM,
																	QMS_QUOTE_RATES     QR
														WHERE QR.BUYRATE_ID = BD.BUYRATEID
															AND QR.RATE_LANE_NO = BD.LANE_NO
															AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
															AND BD.BUYRATEID = V_BUYRATEID
															AND BD.VERSION_NO = BM.VERSION_NO
															AND BD.LANE_NO = V_LANENO
															AND QR.QUOTE_ID = V_QUOTERATEID
															AND (BM.LANE_NO = BD.LANE_NO OR
																	BM.LANE_NO IS NULL)
															AND BM.BUYRATEID = BD.BUYRATEID
															AND BD.VERSION_NO =
																	(SELECT MAX(VERSION_NO)
																		 FROM QMS_BUYRATES_DTL
																		WHERE BUYRATEID = BD.BUYRATEID
																			AND LANE_NO = BD.LANE_NO)),
													'Y',
													BM.CONSOLE_TYPE,
													BD.RATE_DESCRIPTION,
													BD.external_notes
						FROM QMS_BUYRATES_DTL    BD,
								 QMS_BUYRATES_MASTER BM,
								 QMS_QUOTE_RATES     QR
					 WHERE QR.BUYRATE_ID = BD.BUYRATEID
						 AND QR.VERSION_NO = BD.VERSION_NO
						 AND QR.RATE_LANE_NO = BD.LANE_NO
						 AND QR.BREAK_POINT = BD.WEIGHT_BREAK_SLAB
						 AND BD.BUYRATEID = V_BUYRATEID
						 AND BD.VERSION_NO = V_VERSIONNO
						 AND BD.VERSION_NO = BM.VERSION_NO
						 AND BD.LANE_NO = V_LANENO
						 AND QR.QUOTE_ID = V_QUOTERATEID
						 AND (BM.LANE_NO = BD.LANE_NO OR BM.LANE_NO IS NULL)
						 AND BM.BUYRATEID = BD.BUYRATEID;

			ELSE
				SELECT BREAK_POINT BULK COLLECT
					INTO BREAK_POINT_LIST
					FROM QMS_QUOTE_RATES
				 WHERE QUOTE_ID = V_QUOTERATEID
					 AND SELL_BUY_FLAG = 'RSR';
				FORALL J IN 1 .. BREAK_POINT_LIST.COUNT
					INSERT INTO GT_TEMP_DATA_1
						(BUYRATEID,
						 WEIGHT_BREAK_SLAB,
						 CHARGERATE,
						 LANE_NO,
						 LINE_NO,
						 ID_FLAG,
						 WEIGHT_BREAK,
						 REC_BUYRATEID,
						 RATE_DESCRIPTION)
						SELECT SD.REC_CON_ID,
									 SD.WEIGHTBREAKSLAB,
									 SD.CHARGERATE,
									 SD.LANE_NO,
									 SD.LINE_NO,
									 DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
									 UPPER(SM.WEIGHT_BREAK),
									 SD.BUYRATEID,
									 SD.RATE_DESCRIPTION
							FROM QMS_REC_CON_SELLRATESDTL    SD,
									 QMS_REC_CON_SELLRATESMASTER SM
						 WHERE SD.WEIGHTBREAKSLAB = BREAK_POINT_LIST(J)
							 AND SD.REC_CON_ID = V_SELLRATEID
							 AND SD.BUYRATEID = V_BUYRATEID
							 AND SD.LANE_NO = V_LANENO
							 AND SD.VERSION_NO = V_VERSIONNO
							 AND SD.REC_CON_ID = SM.REC_CON_ID
							 AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') =
									 V_SELLBUYFLAG;
				INSERT INTO GT_BASE_DATA
					(BUYRATEID,
					 VERSION_NO,
					 CARRIER_ID,
					 ORIGIN,
					 DESTINATION,
					 SERVICE_LEVEL,
					 SERVICE_LEVEL_DESC,
					 WEIGHT_BREAK_SLAB,
					 TRANSIT_TIME,
					 FREQUENCY,
					 CHARGERATE,
					 LANE_NO,
					 RCB_FLAG,
					 REC_BUYRATE_ID,
					 NOTES,
					 WEIGHT_BREAK,
					 TERMINALID,
					 CURRENCY,
					 WT_CLASS,
					 EFROM,
					 VALIDUPTO,
					 SELECTED_FLAG,
					 CONSOLE_TYPE,
					 RATE_DESCRIPTION,
					 external_notes)
					SELECT DISTINCT SD.REC_CON_ID,
													V_VERSIONNO,
													SD.CARRIER_ID,
													SD.ORIGIN,
													SD.DESTINATION,
													SD.SERVICELEVEL_ID,
													(SELECT DISTINCT SERVICELEVELDESC
														 FROM FS_FR_SERVICELEVELMASTER
														WHERE SERVICELEVELID = SD.SERVICELEVEL_ID) SERVICE_LEVEL_DESC,
													'a',
													SD.TRANSIT_TIME,
													SD.FREQUENCY,
													'b',
													SD.LANE_NO,
													DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR'),
													SD.BUYRATEID,
													SD.NOTES,
													UPPER(SM.WEIGHT_BREAK),
													SM.TERMINALID,
													SM.CURRENCY,
													SM.WEIGHT_CLASS,
													(SELECT DISTINCT QBD.EFFECTIVE_FROM
														 FROM QMS_REC_CON_SELLRATESDTL QSD,
																	QMS_BUYRATES_DTL         QBD
														WHERE QSD.BUYRATEID = QBD.BUYRATEID
															AND QSD.LANE_NO = QBD.LANE_NO
															AND QSD.REC_CON_ID = SD.REC_CON_ID
															AND QSD.BUYRATEID = SD.BUYRATEID
															AND QSD.LANE_NO = SD.LANE_NO
															AND QSD.VERSION_NO = SD.VERSION_NO
															AND QSD.VERSION_NO = QBD.VERSION_NO),
													(SELECT DISTINCT QBD.VALID_UPTO
														 FROM QMS_REC_CON_SELLRATESDTL QSD,
																	QMS_BUYRATES_DTL         QBD
														WHERE QSD.BUYRATEID = QBD.BUYRATEID
															AND QSD.LANE_NO = QBD.LANE_NO
															AND QSD.VERSION_NO =
																	(SELECT MAX(RSD1.VERSION_NO)
																		 FROM QMS_REC_CON_SELLRATESDTL RSD1
																		WHERE RSD1.BUYRATEID = QSD.BUYRATEID
																			AND RSD1.LANE_NO = QSD.LANE_NO
																			AND RSD1.BUYRATEID = V_BUYRATEID
																			AND RSD1.LANE_NO = V_LANENO)
															AND QBD.VERSION_NO = QSD.VERSION_NO),
													'Y',
													SD.CONSOLE_TYPE,
													SD.RATE_DESCRIPTION,
													sd.external_notes
						FROM QMS_REC_CON_SELLRATESDTL    SD,
								 QMS_REC_CON_SELLRATESMASTER SM,
								 QMS_QUOTE_RATES             QR
					 WHERE QR.BUYRATE_ID = SD.BUYRATEID
						 AND QR.SELLRATE_ID = SD.REC_CON_ID
						 AND QR.VERSION_NO = SD.VERSION_NO
						 AND QR.RATE_LANE_NO = SD.LANE_NO
						 AND QR.BREAK_POINT = SD.WEIGHTBREAKSLAB
						 AND SD.REC_CON_ID = V_SELLRATEID
						 AND SD.BUYRATEID = V_BUYRATEID
						 AND SD.LANE_NO = V_LANENO
						 AND SD.VERSION_NO = V_VERSIONNO
						 AND SD.REC_CON_ID = SM.REC_CON_ID
						 AND DECODE(SM.RC_FLAG, 'R', 'RSR', 'C', 'CSR') = V_SELLBUYFLAG;
			END IF;
		ELSE

		DBMS_OUTPUT.put_line('FIRST QRY--'||V_SQL1  );
		DBMS_OUTPUT.put_line('V_SQLTBNEW--  ' || V_SQLTBNEW );
		DBMS_OUTPUT.put_line('V_SQL7-- '|| V_SQL7);
		DBMS_OUTPUT.put_line('V_SQL10-- '||V_SQL10);
		DBMS_OUTPUT.put_line('V_SQL11-- '||V_SQL11);
		DBMS_OUTPUT.put_line('V_SQL12-- '||V_SQL12);
		DBMS_OUTPUT.put_line('V_SQLTBNEW-- '||V_SQLTBNEW);
		DBMS_OUTPUT.put_line('V_SQL2-- '||V_SQL2 );
		DBMS_OUTPUT.put_line('V_SQL4-- '||V_SQL4 );
		DBMS_OUTPUT.put_line('V_SQL5-- '|| V_SQL5);
		DBMS_OUTPUT.put_line('V_SQL6-- '|| V_SQL6);
		DBMS_OUTPUT.put_line('V_SQL18-- '||V_SQL18);

			EXECUTE IMMEDIATE (V_SQL1 || V_SQLTBNEW || V_SQL7 || V_SQL10 ||
												V_SQL11 || V_SQL12 || V_SQLTBNEW || V_SQL2 ||
												V_SQL4 || V_SQL5 || V_SQL6 || V_SQL18 ||
												',LINE_NO')
		USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE,P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE,P_WEIGHT_BREAK;

DBMS_OUTPUT.put_line('Second QRY--'||V_SQL13  );
		DBMS_OUTPUT.put_line('V_SQLBNEW--'||V_SQLBNEW );
		DBMS_OUTPUT.put_line('V_SQL15--'||V_SQL15);
		DBMS_OUTPUT.put_line('V_SQL16--'||V_SQL16);
		DBMS_OUTPUT.put_line('V_SQL17--'||V_SQL17);
		DBMS_OUTPUT.put_line('V_SQL11--'||V_SQL11);
		DBMS_OUTPUT.put_line('V_SQL20--'||V_SQL20);
		DBMS_OUTPUT.put_line('V_SQLBNEW--'||V_SQLBNEW );
		DBMS_OUTPUT.put_line('V_SQL14--'||V_SQL14 );
		DBMS_OUTPUT.put_line('V_SQL5--'|| V_SQL5);
		DBMS_OUTPUT.put_line('V_SQL6--'|| V_SQL6);
		DBMS_OUTPUT.put_line('V_SQL21--'||V_SQL21);

			EXECUTE IMMEDIATE (V_SQL13 || V_SQLBNEW || V_SQL15 || V_SQL16 ||
												V_SQL17 || V_SQL11 || V_SQL20 || V_SQLBNEW ||
												V_SQL14 || V_SQL5 || V_SQL6 || V_SQL21)
				USING P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE,P_WEIGHT_BREAK, P_ORG_LOC, P_DEST_LOC, P_SRVLEVEL || '%', P_SHMODE,P_WEIGHT_BREAK;

		END IF;

		DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH';

		IF P_OPERATION = 'Modify' THEN
			BEGIN
				SELECT DISTINCT QR.SELL_BUY_FLAG,
												QM.ID,
												QR.BUYRATE_ID,
												QR.SELLRATE_ID,
												QR.RATE_LANE_NO
					INTO V_SELLBUYFLAG,
							 V_ID,
							 V_CON_BUYRATEID,
							 V_CON_SELLRATEID,
							 V_CON_LANENO
					FROM QMS_QUOTE_RATES  QR,
							 QMS_QUOTE_MASTER QM,
							 FS_RT_PLAN       PL,
							 FS_RT_LEG        LEG
				 WHERE QR.QUOTE_ID = QM.ID
					 AND QM.QUOTE_ID = PL.QUOTE_ID
					 AND PL.RT_PLAN_ID = LEG.RT_PLAN_ID
					 AND LEG.ORIG_LOC = P_ORG_LOC
					 AND LEG.DEST_LOC = P_DEST_LOC
					 AND LEG.SERIAL_NO = QR.SERIAL_NO
					 AND QM.QUOTE_ID = P_QUOTE_ID
					 AND QM.VERSION_NO =
							 (SELECT MAX(VERSION_NO)
									FROM QMS_QUOTE_MASTER
								 WHERE QUOTE_ID = P_QUOTE_ID)
					 AND QR.SELL_BUY_FLAG IN ('BR', 'RSR', 'CSR', 'SBR');
				V_SELLBUYFLAG := '';
			END;
			SELECT BREAK_POINT BULK COLLECT
				INTO BREAK_POINT_LIST
				FROM QMS_QUOTE_RATES
			 WHERE QUOTE_ID = V_ID
				 AND SELL_BUY_FLAG = V_SELLBUYFLAG;
			FORALL K IN 1 .. BREAK_POINT_LIST.COUNT
				UPDATE GT_TEMP_DATA_1
					 SET CHECKED_FLAG = 'Y'
				 WHERE BUYRATEID = (CASE WHEN V_SELLBUYFLAG = 'BR' THEN
								V_CON_BUYRATEID ELSE V_CON_SELLRATEID END)
					 AND LANE_NO = V_CON_LANENO
					 AND WEIGHT_BREAK_SLAB = BREAK_POINT_LIST(K);
		END IF;

		FOR J IN (SELECT DISTINCT BUYRATEID,
															VERSION_NO,
															LANE_NO,
															WEIGHT_BREAK,
															RCB_FLAG,
															NVL(REC_BUYRATE_ID, 'P') REC_BUYRATE_ID
								FROM GT_BASE_DATA
							 WHERE RATE_DESCRIPTION LIKE 'A FREIGHT RATE'
							 ORDER BY BUYRATEID, LANE_NO) LOOP
			V_CHARGERATE  := '';
			V_CHECKEDFLAG := '';
			K             := 1;
			V_BREAK       := '';
		 dbms_output.put_line('Rates '||J.BUYRATEID);
			IF (UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE <> 1) = FALSE THEN
				EXECUTE IMMEDIATE ('select  WEIGHT_BREAK_SLAB,to_char(CHARGERATE)  from GT_TEMP_DATA_1 where  BUYRATEID =:v_buy_rate_id
			       AND LANE_NO=:v_lane_No AND line_no=0  AND NVL(REC_BUYRATEID,''P'')=:v_rec_buyrate_id')
					INTO V_BREAK, V_CHARGERATE
					USING J.BUYRATEID, J.LANE_NO, J.REC_BUYRATE_ID;
			END IF;
			IF UPPER(J.WEIGHT_BREAK) = 'LIST' AND P_SHMODE = 1 THEN
				V_CHARGERATE := V_CHARGERATE || ',';
			ELSIF UPPER(J.WEIGHT_BREAK) = 'FLAT' OR
						UPPER(J.WEIGHT_BREAK) = 'SLAB' THEN
				V_CHARGERATE := V_CHARGERATE || ',';
			END IF;

			/*	SELECT WEIGHT_BREAK_SLAB, CHARGERATE
					INTO V_BASE, V_TEMP
					FROM GT_TEMP_DATA_1
				 WHERE LINE_NO > 0
					 AND BUYRATEID = J.BUYRATEID
					 AND LANE_NO = J.LANE_NO
					 AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
					 AND RATE_DESCRIPTION = 'A FREIGHT RATE';
				V_BREAK      := V_BREAK || ',' || V_BASE;*/
				V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
				IF UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2 THEN
					BEGIN
						SELECT WEIGHT_BREAK_SLAB, CHARGERATE
							INTO V_BASE, V_TEMP
							FROM GT_TEMP_DATA_1
						 WHERE LINE_NO > 0
							 AND BUYRATEID = J.BUYRATEID
							 AND LANE_NO = J.LANE_NO
							 AND NVL(REC_BUYRATEID, 'P') = J.REC_BUYRATE_ID
							 AND RATE_DESCRIPTION <> 'A FREIGHT RATE';
							-- AND WEIGHT_BREAK_SLAB = 'CAFMIN';
						V_BREAK      := V_BREAK || ',' || V_BASE;
						V_CHARGERATE := V_CHARGERATE || V_TEMP || ',';
					EXCEPTION
						WHEN NO_DATA_FOUND THEN
							V_CHARGERATE := V_CHARGERATE || '-,';
					END;


				END IF;

			IF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 2) THEN
				V_BREAK := 'MIN,FLAT,CAFMIN,CAF%,BAFMIN,BAFM3,PSSMIN,PSSM3,CSF';
			ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 1) THEN
				V_BREAK := 'MIN,FLAT,FSBASIC,FSMIN,FSKG,,SSBASIC,SSMIN,SSKG';
			ELSIF (UPPER(J.WEIGHT_BREAK) = 'FLAT' AND P_SHMODE = 4) THEN
				V_BREAK := 'MIN,FLAT,SURCHARGE';
			END IF;
			UPDATE GT_BASE_DATA
				 SET WEIGHT_BREAK_SLAB = V_BREAK,
						 CHARGERATE        = SUBSTR(V_CHARGERATE,
																				1,
																				LENGTH(V_CHARGERATE) - 1),
						 CHECKED_FLAG      = SUBSTR(V_CHECKEDFLAG,
																				1,
																				LENGTH(V_CHECKEDFLAG) - 1)
			 WHERE BUYRATEID = J.BUYRATEID
				 AND LANE_NO = J.LANE_NO
				 AND NVL(REC_BUYRATE_ID, 'P') = J.REC_BUYRATE_ID;
			DELETE FROM GT_BASE_DATA GBT WHERE GBT.SERVICE_LEVEL = 'SCH'; --ADDED FOR 180164

		END LOOP;
		IF P_PERMISSION = 'Y' THEN
			OPEN P_RS FOR
				SELECT *
					FROM GT_BASE_DATA
				 WHERE RATE_DESCRIPTION = 'A FREIGHT RATE'
				--  ORDER  BY WEIGHT_BREAK, BUYRATEID;-- COMMENTED BY SUBRAHMANYAM FOR 179985
				 ORDER BY WEIGHT_BREAK,
									ORIGIN,
									DESTINATION,
									CARRIER_ID,
									SERVICE_LEVEL,
									RCB_FLAG,
									TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
		ELSE
			OPEN P_RS FOR
				SELECT *
					FROM GT_BASE_DATA
				 WHERE RCB_FLAG <> 'BR'
					 AND RATE_DESCRIPTION = 'A FREIGHT RATE'
				--  ORDER  BY WEIGHT_BREAK, BUYRATEID; -- COMMENTED BY SUBRAHMANYAM FOR 179855
				 ORDER BY WEIGHT_BREAK,
									ORIGIN,
									DESTINATION,
									CARRIER_ID,
									SERVICE_LEVEL,
									RCB_FLAG,
									TERMINALID; --ADDED BY SUBRAHMANYAM FOR THE 180164
		END IF;
	END;
END;

/

/

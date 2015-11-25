package com.qms.operations.quote.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import oracle.jdbc.OracleTypes;

import org.apache.log4j.Logger;

import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.costing.dob.CostingChargeDetailsDOB;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingLegDetailsDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.costing.dob.CostingRateInfoDOB;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import com.qms.operations.quote.dob.QuoteCartageRates;
import com.qms.operations.quote.dob.QuoteChargeInfo;
import com.qms.operations.quote.dob.QuoteCharges;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteFlagsDOB;
import com.qms.operations.quote.dob.QuoteFreightLegSellRates;
import com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;

public class QMSQuoteDAO {
	private transient DataSource dataSource = null;
	private static final String FILE_NAME = "QMSQuoteDAO.java";
	private static Logger logger = null;

	/**
	 * Queries
	 */

	private static final String pkQuery = " SELECT ID FROM QMS_QUOTE_MASTER WHERE ID = ? ";

	private static final String terminalQuery = "SELECT TERMINALID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID=?";

	/*
	 * private static final String masterInsQuery = " INSERT INTO
	 * QMS_QUOTE_MASTER "+ " (QUOTE_ID, SHIPMENT_MODE, PREQUOTE_ID, IU_FLAG,
	 * EFFECTIVE_DATE, VALID_TO, ACCEPT_VALIDITYPERIOD,"+ " CUSTOMER_ID,
	 * CUSTOMER_ADDRESSID, CREATED_DATE, CREATED_BY, SALES_PERSON, INDUSTRY_ID,
	 * COMMODITY_ID,"+ " HAZARDOUS_IND, UN_NUMBER, CLASS, SERVICE_LEVEL_ID,
	 * INCO_TERMS_ID, QUOTING_STATION, ORIGIN_LOCATION,"+ " SHIPPER_ZIPCODE,
	 * ORIGIN_PORT, OVERLENGTH_CARGONOTES, ROUTING_ID, DEST_LOCATION,
	 * CONSIGNEE_ZIPCODE,"+ "
	 * DESTIONATION_PORT,ESCALATED_TO,MODIFIED_DATE,MODIFIED_BY, TERMINAL_ID,
	 * VERSION_NO,BASIS,SHIPPERZONES,"+ " CONSIGNEEZONES,ID,PN_FLAG,
	 * UPDATE_FLAG, ACTIVE_FLAG, SENT_FLAG, COMPLETE_FLAG, QUOTE_STATUS,"+ "
	 * ESCALATION_FLAG,IE_FLAG,EMAIL_FLAG, FAX_FLAG,
	 * PRINT_FLAG,CREATED_TSTMP,SPOT_RATES_FLAG,CARGO_ACC_TYPE,CARGO_ACC_PLACE,"+ "
	 * SHIPPER_MODE,CONSIGNEE_MODE,SHIPPER_CONSOLE_TYPE,CONSIGNEE_CONSOLE_TYPE,SALES_PERSON_EMAIL_FLAG)
	 * VALUES"+ "
	 * (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	 */
	// @@Modified for the internal issue
	private static final String masterInsQuery = " INSERT INTO QMS_QUOTE_MASTER "
			+ " (QUOTE_ID, SHIPMENT_MODE, PREQUOTE_ID, IU_FLAG, EFFECTIVE_DATE, VALID_TO, ACCEPT_VALIDITYPERIOD,"
			+ " CUSTOMER_ID, CUSTOMER_ADDRESSID, CREATED_DATE, CREATED_BY, SALES_PERSON, INDUSTRY_ID, COMMODITY_ID,"
			+ " HAZARDOUS_IND, UN_NUMBER, CLASS, SERVICE_LEVEL_ID, INCO_TERMS_ID, QUOTING_STATION, ORIGIN_LOCATION,"
			+ " SHIPPER_ZIPCODE, ORIGIN_PORT, OVERLENGTH_CARGONOTES, ROUTING_ID, DEST_LOCATION, CONSIGNEE_ZIPCODE,"
			+ " DESTIONATION_PORT,ESCALATED_TO,MODIFIED_DATE,MODIFIED_BY, TERMINAL_ID, VERSION_NO,BASIS,SHIPPERZONES,"
			+ " CONSIGNEEZONES,ID,PN_FLAG, UPDATE_FLAG, ACTIVE_FLAG, SENT_FLAG, COMPLETE_FLAG, QUOTE_STATUS,"
			+ " ESCALATION_FLAG,IE_FLAG,EMAIL_FLAG, FAX_FLAG, PRINT_FLAG,CREATED_TSTMP,SPOT_RATES_FLAG,CARGO_ACC_TYPE,CARGO_ACC_PLACE,"
			+ " SHIPPER_MODE,CONSIGNEE_MODE,SHIPPER_CONSOLE_TYPE,CONSIGNEE_CONSOLE_TYPE,SALES_PERSON_EMAIL_FLAG,APP_REJ_TSTMP,CUST_REQUESTED_DATE,CUST_REQUESTED_TIME,MULTI_QUOTE_WEIGHT_BREAK) VALUES"
			+ // Modified by Rakesh on 23-02-2011 for Issue:236359
			" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/*
	 * private static final String masterInsQuery = " INSERT INTO
	 * QMS_QUOTE_MASTER "+ " (QUOTE_ID, SHIPMENT_MODE, PREQUOTE_ID, IU_FLAG,
	 * EFFECTIVE_DATE, VALID_TO, ACCEPT_VALIDITYPERIOD,"+ " CUSTOMER_ID,
	 * CUSTOMER_ADDRESSID, CREATED_DATE, CREATED_BY, SALES_PERSON, INDUSTRY_ID,
	 * COMMODITY_ID,"+ " HAZARDOUS_IND, UN_NUMBER, CLASS, SERVICE_LEVEL_ID,
	 * INCO_TERMS_ID, QUOTING_STATION, ORIGIN_LOCATION,"+ " SHIPPER_ZIPCODE,
	 * ORIGIN_PORT, OVERLENGTH_CARGONOTES, ROUTING_ID, DEST_LOCATION,
	 * CONSIGNEE_ZIPCODE,"+ "
	 * DESTIONATION_PORT,ESCALATED_TO,MODIFIED_DATE,MODIFIED_BY, TERMINAL_ID,
	 * VERSION_NO,BASIS,SHIPPERZONES,"+ " CONSIGNEEZONES,ID,PN_FLAG,
	 * UPDATE_FLAG, ACTIVE_FLAG, SENT_FLAG, COMPLETE_FLAG, QUOTE_STATUS,"+ "
	 * ESCALATION_FLAG,IE_FLAG,EMAIL_FLAG, FAX_FLAG,
	 * PRINT_FLAG,CREATED_TSTMP,SPOT_RATES_FLAG,CARGO_ACC_TYPE,CARGO_ACC_PLACE,"+ "
	 * SHIPPER_MODE,CONSIGNEE_MODE,SHIPPER_CONSOLE_TYPE,CONSIGNEE_CONSOLE_TYPE,SALES_PERSON_EMAIL_FLAG)
	 * VALUES"+ "
	 * (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	 */

	private static final String masterContactPersonInsQuery = " INSERT INTO QMS_QUOTE_CONTACTDTL (QUOTE_ID, CUSTOMERID,SL_NO,ID) VALUES (?,?,?,SEQ_QUOTE_CONTACTDTL_ID.NEXTVAL)";

	// private static final String masterSpotRatesInsQuery = " INSERT INTO
	// QMS_QUOTE_SPOTRATES (QUOTE_ID, LANE_NO,LINE_NO,WEIGHT_BREAK_SLAB,
	// UPPER_BOUND,
	// LOWER_BOUND,CHARGE_RATE,SHIPMENT_MODE,SERVICELEVEL,ID,UOM,DENSITY_CODE,WEIGHT_BREAK)
	// VALUES (?,?,?,?,?,?,?,?,?,SEQ_QUOTE_SPOTRATES.NEXTVAL,?,?,?)";@@Commented
	// by kameswari for the issue WPBN-30908

	private static final String masterSpotRatesInsQuery = " INSERT INTO QMS_QUOTE_SPOTRATES (QUOTE_ID, LANE_NO,LINE_NO,WEIGHT_BREAK_SLAB, UPPER_BOUND, LOWER_BOUND,CHARGE_RATE,SHIPMENT_MODE,SERVICELEVEL,ID,UOM,DENSITY_CODE,WEIGHT_BREAK,CURRENCYID,RATE_DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?,SEQ_QUOTE_SPOTRATES.NEXTVAL,?,?,?,?,?)";// @@added
																																																																																			// by
																																																																																			// kameswari
																																																																																			// for
																																																																																			// the
																																																																																			// issue
																																																																																			// WPBN-30908

	private static final String masterChargeGroupsInsQuery = " INSERT INTO QMS_QUOTE_CHARGEGROUPDTL (QUOTE_ID, CHARGEGROUPID,ID) VALUES (?,?,SEQ_CHARGEGROUPDTL_ID.NEXTVAL)";

	private static final String masterHeaderFooterInsQuery = " INSERT INTO QMS_QUOTE_HF_DTL (QUOTE_ID, HEADER, CONTENT, CLEVEL, ALIGN,ID) VALUES (?,?,?,?,?,SEQ_QMS_QUOTE_HF_DTL.NEXTVAL)";

	private static final String routePlanInsertQuery = "INSERT INTO FS_RT_PLAN (RT_PLAN_ID,QUOTE_ID,ORIG_TRML_ID,DEST_TRML_ID,ORIG_LOC_ID,DEST_LOC_ID,SHIPPER_ID,PRMY_MODE,CRTD_TIMESTMP,LAST_UPDTD_TIMESTMP) VALUES (?,?,?,?,?,?,?,?,?,?)";

	private static final String routeLegInsertQuery = "INSERT INTO FS_RT_LEG (RT_PLAN_ID,SERIAL_NO,LEG_TYPE,ORIG_LOC,DEST_LOC,SHPMNT_MODE,LEG_VALID_FLAG,ORIG_TRML_ID,DEST_TRML_ID) VALUES (?,?,?,?,?,?,?,?,?)";

	private static final String notesInsertQuery = "INSERT INTO QMS_QUOTE_NOTES (QUOTE_ID,INTERNAL_NOTES,EXTERNAL_NOTES,ID) VALUES (?,?,?,SEQ_QMS_QUOTE_NOTES.NEXTVAL)";

	private static final String selectedRatesInsertQuery = "INSERT INTO QMS_QUOTE_RATES(QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,"
			+ "MARGIN,DISCOUNT_TYPE,DISCOUNT,NOTES,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,	RT_PLAN_ID,	SERIAL_NO,ID,LINE_NO,MARGIN_TEST_FLAG,ZONE_CODE)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_QMS_QUOTE_RATES.NEXTVAL,?,?,?)";
	private static final String selectedRatesInsertQuery1 = "INSERT INTO QMS_QUOTE_RATES(QUOTE_ID,SELL_BUY_FLAG,BUYRATE_ID,SELLRATE_ID,RATE_LANE_NO,CHARGE_ID,CHARGE_DESCRIPTION,MARGIN_DISCOUNT_FLAG,MARGIN_TYPE,"
			+ "MARGIN,DISCOUNT_TYPE,DISCOUNT,NOTES,QUOTE_REFNO,BREAK_POINT,CHARGE_AT,BUY_RATE,R_SELL_RATE,	RT_PLAN_ID,	SERIAL_NO,ID,LINE_NO,MARGIN_TEST_FLAG,SRVLEVEL,FREQUENCY,TRANSIT_TIME,CARRIER,RATE_VALIDITY,FREQUENCY_CHECKED,TRANSIT_CHECKED,CARRIER_CHECKED,VALIDITY_CHECKED,VERSION_NO)"
			+ " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SEQ_QMS_QUOTE_RATES.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?)";

	// ----
	// private static final String getIdQry = " SELECT ID FROM QMS_QUOTE_MASTER
	// WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM
	// QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";

	private static final String getIdQry = " SELECT ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";

	/*
	 * private static final String masterUpdateQry = " UPDATE QMS_QUOTE_MASTER "+ "
	 * SET
	 * QUOTE_ID=?,SHIPMENT_MODE=?,PREQUOTE_ID=?,IU_FLAG=?,EFFECTIVE_DATE=?,VALID_TO=?,ACCEPT_VALIDITYPERIOD=?,"+ "
	 * CUSTOMER_ID=?,CUSTOMER_ADDRESSID=?,CREATED_DATE=?,CREATED_BY=?,SALES_PERSON=?,INDUSTRY_ID=?,COMMODITY_ID=?,"+ "
	 * HAZARDOUS_IND=?,UN_NUMBER=?,CLASS=?, SERVICE_LEVEL_ID=?, INCO_TERMS_ID=?,
	 * QUOTING_STATION=?, ORIGIN_LOCATION=?,"+ " SHIPPER_ZIPCODE=?,
	 * ORIGIN_PORT=?, OVERLENGTH_CARGONOTES=?, ROUTING_ID=?, DEST_LOCATION=?,
	 * CONSIGNEE_ZIPCODE=?,"+ " DESTIONATION_PORT=?, ESCALATED_TO=?,
	 * MODIFIED_DATE=?, MODIFIED_BY=?, TERMINAL_ID=?, VERSION_NO=?, BASIS=?,
	 * SHIPPERZONES=?,"+ " CONSIGNEEZONES=?, PN_FLAG=?, UPDATE_FLAG=?,
	 * ACTIVE_FLAG=?, SENT_FLAG=?, COMPLETE_FLAG=?, QUOTE_STATUS=?,"+ "
	 * ESCALATION_FLAG=?, IE_FLAG=?, EMAIL_FLAG=?, FAX_FLAG=?,
	 * PRINT_FLAG=?,SHIPPER_MODE=?,CONSIGNEE_MODE=?,SHIPPER_CONSOLE_TYPE=?,CONSIGNEE_CONSOLE_TYPE=?
	 * ,SALES_PERSON_EMAIL_FLAG=? WHERE ID=?";
	 */

	private static final String masterUpdateQry = " UPDATE QMS_QUOTE_MASTER "
			+ " SET QUOTE_ID=?,SHIPMENT_MODE=?,PREQUOTE_ID=?,IU_FLAG=?,EFFECTIVE_DATE=?,VALID_TO=?,ACCEPT_VALIDITYPERIOD=?,"
			+ " CUSTOMER_ID=?,CUSTOMER_ADDRESSID=?,CREATED_DATE=?,CREATED_BY=?,SALES_PERSON=?,INDUSTRY_ID=?,COMMODITY_ID=?,"
			+ " HAZARDOUS_IND=?,UN_NUMBER=?,CLASS=?, SERVICE_LEVEL_ID=?, INCO_TERMS_ID=?, QUOTING_STATION=?, ORIGIN_LOCATION=?,"
			+ " SHIPPER_ZIPCODE=?, ORIGIN_PORT=?, OVERLENGTH_CARGONOTES=?, ROUTING_ID=?, DEST_LOCATION=?, CONSIGNEE_ZIPCODE=?,"
			+ " DESTIONATION_PORT=?, ESCALATED_TO=?, MODIFIED_DATE=?, MODIFIED_BY=?,  TERMINAL_ID=?, VERSION_NO=?, BASIS=?, SHIPPERZONES=?,"
			+ " CONSIGNEEZONES=?, PN_FLAG=?, UPDATE_FLAG=?, ACTIVE_FLAG=?, SENT_FLAG=?, COMPLETE_FLAG=?, QUOTE_STATUS=?,"
			+ " ESCALATION_FLAG=?, IE_FLAG=?, EMAIL_FLAG=?, FAX_FLAG=?, PRINT_FLAG=?,SHIPPER_MODE=?,CONSIGNEE_MODE=?,SHIPPER_CONSOLE_TYPE=?,CONSIGNEE_CONSOLE_TYPE=? ,SALES_PERSON_EMAIL_FLAG=?,CUST_REQUESTED_DATE=?,CUST_REQUESTED_TIME=? WHERE ID=?";// Modified
																																																														// by
																																																														// Rakesh
																																																														// on
																																																														// 23-02-2011
																																																														// for
																																																														// Issue:236359

	private static final String masterContactPersonDelQry = "DELETE FROM QMS_QUOTE_CONTACTDTL WHERE QUOTE_ID = ?";
	private static final String masterSpotRatesDelQry = "DELETE FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID=?";
	private static final String masterChargeGroupsDelQry = "DELETE FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID= ?";
	private static final String masterHeaderFooterDelQry = "DELETE FROM QMS_QUOTE_HF_DTL WHERE QUOTE_ID=?";
	private static final String routePlanDelQry = "DELETE FROM FS_RT_PLAN WHERE QUOTE_ID=? AND RT_PLAN_ID=?";
	private static final String routeLegDelQry = "DELETE FROM FS_RT_PLAN WHERE RT_PLAN_ID=?";
	private static final String selectedRatesDelQry = "DELETE FROM QMS_QUOTE_RATES WHERE QUOTE_ID=?";
	private static final String notesDelQry = "DELETE FROM QMS_QUOTE_NOTES WHERE QUOTE_ID=?";

	/**
	 * Default Contructor which Initializes InitialContext DataSource
	 */
	public QMSQuoteDAO() {
		logger = Logger.getLogger(QMSQuoteDAO.class);
		try {
			InitialContext ic = new InitialContext();
			dataSource = (DataSource) ic.lookup("java:comp/env/jdbc/DB");
		} catch (NamingException nmEx) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO(QMSQuoteDAO()) naming
			// exception "+nmEx.toString());
			logger.error(FILE_NAME
					+ "QMSQuoteDAO(QMSQuoteDAO()) naming exception "
					+ nmEx.toString());
		}
	}

	/**
	 * @return Connection gets the connection from the pool
	 */
	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * This implements for the findByPrimaryKey() of the Quote
	 */
	public long findByPrimaryKey(long quoteId) throws SQLException,
			ObjectNotFoundException {

		PreparedStatement pStmtFindPK = null;
		boolean hasRows = false;
		Connection connection = null;
		ResultSet rs = null;
		try {
			connection = this.getConnection();
			pStmtFindPK = connection.prepareStatement(pkQuery);
			pStmtFindPK.setLong(1, quoteId);
			rs = pStmtFindPK.executeQuery();
			if (rs.next()) {
				hasRows = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[findByPrimaryKey(quoteId)] ->
			// "+e.toString());
			logger.error(FILE_NAME
					+ "QMSQuoteDAO[findByPrimaryKey(quoteId)] -> "
					+ e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(connection, pStmtFindPK, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[findByPrimaryKey(quoteId)]-> "+ex.toString());
				logger.error(FILE_NAME
						+ "Finally : QMSQuoteDAO[findByPrimaryKey(quoteId)]-> "
						+ ex.toString());
				// throw new Exception(ex.toString());
			}
		}
		if (hasRows)
			return quoteId;
		else
			throw new ObjectNotFoundException("Could not find bean	with Id "
					+ quoteId);
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * QuoteMaster and its child tables
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	public void create(QuoteFinalDOB finalDOB) throws SQLException {

		Connection connection = null;
		try {
			connection = this.getConnection();
			this.insertQuoteMasterDetails(finalDOB, connection);

		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] ->
			// "+sqEx.toString());
			logger.error(FILE_NAME + "QMSQuoteDAO[create(masterDOB)] -> "
					+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] ->
			// "+e.toString());
			logger.error(FILE_NAME + "QMSQuoteDAO[create(masterDOB)] -> "
					+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(connection);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[create(masterDOB)]-> "+ex.toString());
				logger.error(FILE_NAME
						+ "Finally : QMSQuoteDAO[create(masterDOB)]-> "
						+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * QuoteMaster table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void insertQuoteMasterDetails(QuoteFinalDOB finalDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		QuoteMasterDOB masterDOB = null;
		QuoteFlagsDOB flagsDOB = null;
		ArrayList attachmentIdList = null;// @@Added by kameswari for the WPBN
											// issue-61289
		String location = null;
		String quoteId = null;
		// @@ Added by subrahmanyam for the WPBN ISSUE: 146971 ON 18/12/2008
		int i1 = 0;
		long maxvalue = 0;
		String[] quoteIds = null;
		String[] quotes = null;
		int quoteLen = 0;

		try {

			masterDOB = (QuoteMasterDOB) finalDOB.getMasterDOB();
			pStmt = connection.prepareStatement(masterInsQuery);
			if (finalDOB != null && finalDOB.getUpdatedReportDOB() != null) {
				quoteId = finalDOB.getUpdatedReportDOB().getQuoteId();
			}
			if ("update".equalsIgnoreCase(finalDOB.getUpdate())
					&& quoteId != null && quoteId.trim().length() > 0) {
				quotes = quoteId.split("_");
				if (quotes.length == 2) {
					quoteId = quoteId + "_001";
				} else {
					quoteLen = Integer.parseInt(quotes[2]);// 2nd part of the
															// quoteId
					quoteLen++;

					if (quoteLen < 10) {
						quoteId = quotes[0] + "_" + quotes[1] + "_00"
								+ new Integer(quoteLen).toString();

					} else if (quoteLen >= 10 && quoteLen < 100) {
						quoteId = quotes[0] + "_" + quotes[1] + "_0"
								+ new Integer(quoteLen).toString();

					} else {
						quoteId = quotes[0] + "_" + quotes[1] + "_"
								+ new Integer(quoteLen).toString();

					}

					if (quoteLen < 10) {
						quoteId = quotes[0] + "_" + quotes[1] + "_00"
								+ new Integer(quoteLen).toString();

					} else if (quoteLen >= 10 && quoteLen < 100) {
						quoteId = quotes[0] + "_" + quotes[1] + "_0"
								+ new Integer(quoteLen).toString();

					} else {
						quoteId = quotes[0] + "_" + quotes[1] + "_"
								+ new Integer(quoteLen).toString();

					}
				}
				masterDOB.setQuoteId(quoteId);
				pStmt.setString(1, masterDOB.getQuoteId()); // @@ Added by
															// subrahmanyam for
															// the enhancement
															// 146971 on 1/12/08
				pStmt.setInt(2, masterDOB.getShipmentMode());
				// pStmt.setLong(3,masterDOB.getPreQuoteId()); //@@ Commented by
				// subrahmanyam for the enhancement 146971 on 1/12/08
				pStmt.setString(3, masterDOB.getPreQuoteId()); // @@ Commented
																// by
																// subrahmanyam
																// for the
																// enhancement
																// 146971 on
																// 1/12/08

				if (masterDOB.isImpFlag())
					pStmt.setString(4, "I");
				else
					pStmt.setString(4, "U");

				pStmt.setTimestamp(5, masterDOB.getEffDate());
				pStmt.setTimestamp(6, masterDOB.getValidTo());
				pStmt.setInt(7, masterDOB.getAccValidityPeriod());
				pStmt.setString(8, masterDOB.getCustomerId());
				pStmt.setInt(9, masterDOB.getCustomerAddressId());
				pStmt.setTimestamp(10, masterDOB.getCreatedDate());
				pStmt.setString(11, masterDOB.getCreatedBy());
				pStmt.setString(12, masterDOB.getSalesPersonCode());
				pStmt.setString(13, masterDOB.getIndustryId());
				pStmt.setString(14, masterDOB.getCommodityId());

				if (masterDOB.isHazardousInd())
					pStmt.setString(15, "Y");
				else
					pStmt.setString(15, "N");

				pStmt.setString(16, masterDOB.getUnNumber());
				pStmt.setString(17, masterDOB.getCommodityClass());
				pStmt.setString(18, masterDOB.getServiceLevelId());
				pStmt.setString(19, masterDOB.getIncoTermsId());
				pStmt.setString(20, masterDOB.getQuotingStation());
				pStmt.setString(21, masterDOB.getOriginLocation());
				pStmt.setString(22, masterDOB.getShipperZipCode());
				pStmt.setString(23, masterDOB.getOriginPort());
				pStmt.setString(24, masterDOB.getOverLengthCargoNotes());

				if (masterDOB.getRouteId() != null)
					pStmt.setString(25, masterDOB.getRouteId());
				else
					pStmt.setNull(25, Types.VARCHAR);

				pStmt.setString(26, masterDOB.getDestLocation());
				pStmt.setString(27, masterDOB.getConsigneeZipCode());
				pStmt.setString(28, masterDOB.getDestPort());

				/*
				 * if(masterDOB.getSpotRatesFlag()) { pStmt.setString(29,"Y");
				 * pStmt.setString(30,masterDOB.getSpotRatesType()); } else {
				 * pStmt.setString(29,"N"); pStmt.setNull(30,Types.VARCHAR); }
				 */

				/*
				 * if(masterDOB.isEmailFlag()) pStmt.setString(29,"Y"); else
				 * pStmt.setNull(29,Types.VARCHAR);
				 * 
				 * if(masterDOB.isFaxFlag()) pStmt.setString(30,"Y"); else
				 * pStmt.setNull(30,Types.VARCHAR);
				 * 
				 * if(masterDOB.isPrintFlag()) pStmt.setString(31,"Y"); else
				 * pStmt.setNull(31,Types.VARCHAR);
				 */

				pStmt.setString(29, finalDOB.getEscalatedTo());
				// @@ Commented & Added by subrahmanyam for the pbn id: 212006
				// on 21-Jul-10
				// pStmt.setTimestamp(30,masterDOB.getModifiedDate());
				pStmt.setTimestamp(30,
						masterDOB.getModifiedDate() != null ? masterDOB
								.getModifiedDate() : (new java.sql.Timestamp(
								(new java.util.Date()).getTime())));
				// @@ Ended by subrahmanyam for the pbn id: 212006 on 21-Jul-10

				pStmt.setString(31, masterDOB.getModifiedBy());
				pStmt.setString(32, masterDOB.getTerminalId());
				pStmt.setLong(33, masterDOB.getVersionNo());
				pStmt.setString(34, masterDOB.getBuyRatesPermission());
				pStmt.setString(35, masterDOB.getShipperZones());
				pStmt.setString(36, masterDOB.getConsigneeZones());
				pStmt.setLong(37, masterDOB.getUniqueId());

				if (finalDOB.getFlagsDOB() != null) {
					flagsDOB = finalDOB.getFlagsDOB();
					flagsDOB.setQuoteId(masterDOB.getUniqueId());

					if (flagsDOB.getPNFlag() != null)
						pStmt.setString(38, flagsDOB.getPNFlag());
					else
						pStmt.setNull(38, Types.VARCHAR);

					if (flagsDOB.getUpdateFlag() != null)
						pStmt.setString(39, flagsDOB.getUpdateFlag());
					else
						pStmt.setNull(39, Types.VARCHAR);

					if (flagsDOB.getActiveFlag() != null)
						pStmt.setString(40, flagsDOB.getActiveFlag());
					else
						pStmt.setNull(40, Types.VARCHAR);

					if (flagsDOB.getSentFlag() != null)
						pStmt.setString(41, flagsDOB.getSentFlag());
					else
						pStmt.setNull(41, Types.VARCHAR);

					if (flagsDOB.getCompleteFlag() != null)
						pStmt.setString(42, flagsDOB.getCompleteFlag());
					else
						pStmt.setNull(42, Types.VARCHAR);

					if (flagsDOB.getQuoteStatusFlag() != null)
						pStmt.setString(43, flagsDOB.getQuoteStatusFlag());
					else
						pStmt.setNull(43, Types.VARCHAR);

					pStmt.setString(44, flagsDOB.getEscalationFlag());
					pStmt.setString(45, flagsDOB.getInternalExternalFlag());
					pStmt.setString(46, flagsDOB.getEmailFlag());
					pStmt.setString(47, flagsDOB.getFaxFlag());
					pStmt.setString(48, flagsDOB.getPrintFlag());
					pStmt.setTimestamp(49, new java.sql.Timestamp(
							(new java.util.Date()).getTime()));

				}
				pStmt.setString(50, finalDOB.getSpotRatesFlag());
				pStmt.setString(51, masterDOB.getCargoAcceptance());
				pStmt.setString(52, masterDOB.getCargoAccPlace());
				pStmt.setString(53, masterDOB.getShipperMode());
				pStmt.setString(54, masterDOB.getConsigneeMode());
				if ("2".equalsIgnoreCase(masterDOB.getShipperMode()))
					pStmt.setString(55, masterDOB.getShipperConsoleType());
				else
					pStmt.setNull(55, Types.VARCHAR);

				if ("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
					pStmt.setString(56, masterDOB.getConsigneeConsoleType());
				else
					pStmt.setNull(56, Types.VARCHAR);
				pStmt.setString(57, masterDOB.getSalesPersonFlag());
				if ("APP".equalsIgnoreCase(flagsDOB.getQuoteStatusFlag())) {
					pStmt.setTimestamp(58, new java.sql.Timestamp(
							(new java.util.Date()).getTime()));
				} else {
					pStmt.setNull(58, Types.DATE);
				}
				pStmt.setTimestamp(59, masterDOB.getCustDate());// Added by
																// Rakesh for
																// Issue:236359
				pStmt.setString(60, masterDOB.getCustTime());// Added by
																// Rakesh for
																// Issue:
				pStmt.setString(61, masterDOB.getWeightBreak());
				pStmt.executeUpdate();
			} else {
				// psmt = connection.prepareStatement("SELECT QUOTE_ID FROM
				// QMS_QUOTE_MASTER WHERE ID IN( SELECT MAX(ID) FROM
				// QMS_QUOTE_MASTER WHERE TERMINAL_ID=? AND VERSION_NO=1 AND
				// instr(QUOTE_ID,'_',1,2)=0)");
				/*
				 * psmt = connection.prepareStatement("SELECT QUOTE_ID FROM
				 * QMS_QUOTE_MASTER WHERE ID IN( SELECT MAX(ID) FROM
				 * QMS_QUOTE_MASTER WHERE TERMINAL_ID=? AND VERSION_NO=1 AND
				 * instr(QUOTE_ID,'_',1,2)=0)");
				 * psmt.setString(1,masterDOB.getTerminalId()); rs =
				 * psmt.executeQuery(); if(rs.next()) quoteId=rs.getString(1);
				 * location =masterDOB.getTerminalId().substring(3); //@@ Added
				 * by subrahmanyam for the WPBN issue: 146971 on 18/12/2008
				 * if(quoteId!=null) { quoteIds=quoteId.split("_");
				 * quoteId=quoteIds[1]; i1=Integer.parseInt(quoteId); i1++;
				 * quoteId=location+"_"+new Integer(i1).toString(); } else {
				 * quoteId =location+"_1"; }
				 * if(!("Modify".equalsIgnoreCase(masterDOB.getOperation()))) {
				 * masterDOB.setQuoteId(quoteId); }
				 */
				// @@ Added by Subrahmanyam for the internal Issue: 208931 on
				// 18-Jun-10
				if (!("Modify".equalsIgnoreCase(masterDOB.getOperation()))) {
					// ended for 208931
					psmt = connection.prepareStatement("SELECT "
							+ masterDOB.getTerminalId()
							+ "_SEQ.NEXTVAL FROM DUAL");
					rs = psmt.executeQuery();
					if (rs.next()) {
						maxvalue = rs.getLong(1);
					}
					location = masterDOB.getTerminalId().substring(3);

					quoteId = location + "_" + new Long(maxvalue).toString();
					// @@ Commented by Subrahmanyam for the internal Issue:
					// 208931 on 18-Jun-10
					/*
					 * if(!("Modify".equalsIgnoreCase(masterDOB.getOperation()))) {
					 */
					// Ended for 208931
					masterDOB.setQuoteId(quoteId);
				}
				pStmt.setString(1, masterDOB.getQuoteId()); // @@ Added by
															// subrahmanyam for
															// the enhancement
															// 146971 on 1/12/08
				pStmt.setInt(2, masterDOB.getShipmentMode());
				// pStmt.setLong(3,masterDOB.getPreQuoteId()); //@@ Commented by
				// subrahmanyam for the enhancement 146971 on 1/12/08
				pStmt.setString(3, masterDOB.getPreQuoteId()); // @@ Commented
																// by
																// subrahmanyam
																// for the
																// enhancement
																// 146971 on
																// 1/12/08

				if (masterDOB.isImpFlag())
					pStmt.setString(4, "I");
				else
					pStmt.setString(4, "U");

				pStmt.setTimestamp(5, masterDOB.getEffDate());
				pStmt.setTimestamp(6, masterDOB.getValidTo());
				pStmt.setInt(7, masterDOB.getAccValidityPeriod());
				pStmt.setString(8, masterDOB.getCustomerId());
				pStmt.setInt(9, masterDOB.getCustomerAddressId());
				pStmt.setTimestamp(10, masterDOB.getCreatedDate());
				pStmt.setString(11, masterDOB.getCreatedBy());
				pStmt.setString(12, masterDOB.getSalesPersonCode());
				pStmt.setString(13, masterDOB.getIndustryId());
				pStmt.setString(14, masterDOB.getCommodityId());

				if (masterDOB.isHazardousInd())
					pStmt.setString(15, "Y");
				else
					pStmt.setString(15, "N");

				pStmt.setString(16, masterDOB.getUnNumber());
				pStmt.setString(17, masterDOB.getCommodityClass());
				pStmt.setString(18, masterDOB.getServiceLevelId());
				pStmt.setString(19, masterDOB.getIncoTermsId());
				pStmt.setString(20, masterDOB.getQuotingStation());
				pStmt.setString(21, masterDOB.getOriginLocation());
				pStmt.setString(22, masterDOB.getShipperZipCode());
				pStmt.setString(23, masterDOB.getOriginPort());
				pStmt.setString(24, masterDOB.getOverLengthCargoNotes());

				if (masterDOB.getRouteId() != null)
					pStmt.setString(25, masterDOB.getRouteId());
				else
					pStmt.setNull(25, Types.VARCHAR);

				pStmt.setString(26, masterDOB.getDestLocation());
				pStmt.setString(27, masterDOB.getConsigneeZipCode());
				pStmt.setString(28, masterDOB.getDestPort());

				/*
				 * if(masterDOB.getSpotRatesFlag()) { pStmt.setString(29,"Y");
				 * pStmt.setString(30,masterDOB.getSpotRatesType()); } else {
				 * pStmt.setString(29,"N"); pStmt.setNull(30,Types.VARCHAR); }
				 */

				/*
				 * if(masterDOB.isEmailFlag()) pStmt.setString(29,"Y"); else
				 * pStmt.setNull(29,Types.VARCHAR);
				 * 
				 * if(masterDOB.isFaxFlag()) pStmt.setString(30,"Y"); else
				 * pStmt.setNull(30,Types.VARCHAR);
				 * 
				 * if(masterDOB.isPrintFlag()) pStmt.setString(31,"Y"); else
				 * pStmt.setNull(31,Types.VARCHAR);
				 */

				pStmt.setString(29, finalDOB.getEscalatedTo());
				// @@ Commented & Added by subrahmanyam for the pbn id: 212006
				// on 21-Jul-10
				// pStmt.setTimestamp(30,masterDOB.getModifiedDate());
				pStmt.setTimestamp(30,
						masterDOB.getModifiedDate() != null ? masterDOB
								.getModifiedDate() : (new java.sql.Timestamp(
								(new java.util.Date()).getTime())));
				// @@ Ended by subrahmanyam for the pbn id: 212006 on 21-Jul-10

				pStmt.setString(31, masterDOB.getModifiedBy());
				pStmt.setString(32, masterDOB.getTerminalId());
				pStmt.setLong(33, masterDOB.getVersionNo());
				pStmt.setString(34, masterDOB.getBuyRatesPermission());
				pStmt.setString(35, masterDOB.getShipperZones());
				pStmt.setString(36, masterDOB.getConsigneeZones());
				pStmt.setLong(37, masterDOB.getUniqueId());

				if (finalDOB.getFlagsDOB() != null) {
					flagsDOB = finalDOB.getFlagsDOB();
					flagsDOB.setQuoteId(masterDOB.getUniqueId());

					if (flagsDOB.getPNFlag() != null)
						pStmt.setString(38, flagsDOB.getPNFlag());
					else
						pStmt.setNull(38, Types.VARCHAR);

					if (flagsDOB.getUpdateFlag() != null)
						pStmt.setString(39, flagsDOB.getUpdateFlag());
					else
						pStmt.setNull(39, Types.VARCHAR);

					if (flagsDOB.getActiveFlag() != null)
						pStmt.setString(40, flagsDOB.getActiveFlag());
					else
						pStmt.setNull(40, Types.VARCHAR);

					if (flagsDOB.getSentFlag() != null)
						pStmt.setString(41, flagsDOB.getSentFlag());
					else
						pStmt.setNull(41, Types.VARCHAR);

					if (flagsDOB.getCompleteFlag() != null)
						pStmt.setString(42, flagsDOB.getCompleteFlag());
					else
						pStmt.setNull(42, Types.VARCHAR);

					if (flagsDOB.getQuoteStatusFlag() != null)
						pStmt.setString(43, flagsDOB.getQuoteStatusFlag());
					else
						pStmt.setNull(43, Types.VARCHAR);

					pStmt.setString(44, flagsDOB.getEscalationFlag());
					pStmt.setString(45, flagsDOB.getInternalExternalFlag());
					pStmt.setString(46, flagsDOB.getEmailFlag());
					pStmt.setString(47, flagsDOB.getFaxFlag());
					pStmt.setString(48, flagsDOB.getPrintFlag());
					pStmt.setTimestamp(49, new java.sql.Timestamp(
							(new java.util.Date()).getTime()));

				}
				pStmt.setString(50, finalDOB.getSpotRatesFlag());
				pStmt.setString(51, masterDOB.getCargoAcceptance());
				pStmt.setString(52, masterDOB.getCargoAccPlace());
				pStmt.setString(53, masterDOB.getShipperMode());
				pStmt.setString(54, masterDOB.getConsigneeMode());
				if ("2".equalsIgnoreCase(masterDOB.getShipperMode()))
					pStmt.setString(55, masterDOB.getShipperConsoleType());
				else
					pStmt.setNull(55, Types.VARCHAR);

				if ("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
					pStmt.setString(56, masterDOB.getConsigneeConsoleType());
				else
					pStmt.setNull(56, Types.VARCHAR);
				pStmt.setString(57, masterDOB.getSalesPersonFlag());
				if ("APP".equalsIgnoreCase(flagsDOB.getQuoteStatusFlag())) {
					pStmt.setTimestamp(58, new java.sql.Timestamp(
							(new java.util.Date()).getTime()));
				} else {
					pStmt.setNull(58, Types.DATE);
				}
				pStmt.setTimestamp(59, masterDOB.getCustDate());// Added by
																// Rakesh for
																// Issue:236359
				pStmt.setString(60, masterDOB.getCustTime());// Added by
																// Rakesh for
																// Issue:
				pStmt.setString(61, masterDOB.getWeightBreak());
				pStmt.executeUpdate();
			}
			// pStmt.setLong(1,masterDOB.getQuoteId()); //@@ Commented by
			// subrahmanyam for the enhancement 146971 on 1/12/08

			if (pStmt != null)
				pStmt.close();

			if (masterDOB.getCustomerContacts() != null)
				insertQuoteContactPersons(masterDOB, connection);

			// if(masterDOB.getSpotRatesFlag())
			insertQuoteSpotRates(finalDOB, connection);

			if (masterDOB.getChargeGroupIds() != null)
				insertQuoteChargeGroups(masterDOB, connection);

			if (masterDOB.getContentOnQuote() != null)
				insertQuoteHeaderFooter(masterDOB, connection);

			if (!"Modify".equalsIgnoreCase(masterDOB.getOperation()))
				insertRoutePlanDetails(connection, finalDOB);

			insertSelectedRates(finalDOB, connection);

			if (finalDOB.getExternalNotes() != null)
				insertNotes(connection, finalDOB);

			setTransactionDetails(masterDOB);

			if (finalDOB.getUpdatedReportDOB() != null) {
				UpdatedQuotesReportDOB reportDOB = finalDOB
						.getUpdatedReportDOB();
				reportDOB.setNewQuoteId(finalDOB.getMasterDOB().getUniqueId());
				setConfirmFlag(reportDOB);
			}
			// @@Added by kameswari for the WPBN issue- 61289
			if ("Add".equalsIgnoreCase(masterDOB.getOperation())) {
				if (finalDOB.getAttachmentDOBList() != null) {
					insertAttachmentIdList(finalDOB);

				}
			} else if ("Modify".equalsIgnoreCase(masterDOB.getOperation())) {
				if (finalDOB.getAttachmentDOBList() != null) {
					updateAttachmentIdList(finalDOB);

				}
			}

		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				// ConnectionUtil.closeConnection(null,pStmt,null);//Modified By
				// RajKumari on 24-10-2008 for Connection Leakages.
				ConnectionUtil.closeConnection(null, pStmt, rs);// Commented and
																// added by
																// GOving for
																// the
																// connection
																// Leakages
				ConnectionUtil.closePreparedStatement(psmt);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to updates the master info from QuoteMasterDOB to
	 * QuoteMaster table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void modifyQuoteMasterDetails(QuoteFinalDOB finalDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		QuoteMasterDOB masterDOB = null;
		QuoteFlagsDOB flagsDOB = null;
		long id = 0;
		ArrayList legDetails = null;
		int legSize = 0;
		boolean updateFlag = true;
		QuoteFreightLegSellRates legDOB = null;
		try {
			masterDOB = finalDOB.getMasterDOB();

			pStmt = connection.prepareStatement(getIdQry);
			// @@ Commented by subrahmanyam for the enhancement 146971 on
			// 1/12/08
			/*
			 * pStmt.setLong(1,masterDOB.getQuoteId());
			 * pStmt.setLong(2,masterDOB.getQuoteId());
			 */
			// @@ Added by subrahmanyam for the enhancement 146971 on 1/12/08
			pStmt.setString(1, masterDOB.getQuoteId());
			pStmt.setString(2, masterDOB.getQuoteId());
			// @@ Ended by subrahmanyam for the enhancement 146971 on 1/12/08
			rs = pStmt.executeQuery();
			if (rs.next())
				id = rs.getLong("ID");

			if (rs != null)
				rs.close();
			if (pStmt != null)
				pStmt.close();

			masterDOB = (QuoteMasterDOB) finalDOB.getMasterDOB();
			masterDOB.setUniqueId(id);
			pStmt = connection.prepareStatement(masterUpdateQry);
			pStmt.clearParameters();
			// pStmt.setLong(1,masterDOB.getQuoteId()); //@@ Commented by
			// subrahmanyam for the enhancement 146971 on 1/12/08
			pStmt.setString(1, masterDOB.getQuoteId()); // @@ Added by
														// subrahmanyam for the
														// enhancement 146971 on
														// 1/12/08
			pStmt.setInt(2, masterDOB.getShipmentMode());
			// pStmt.setLong(3,masterDOB.getPreQuoteId()); //@@ Commented by
			// subrahmanyam for the enhancement 146971 on 1/12/08
			pStmt.setString(3, masterDOB.getPreQuoteId()); // @@ Added by
															// subrahmanyam for
															// the enhancement
															// 146971 on 1/12/08

			if (masterDOB.isImpFlag())
				pStmt.setString(4, "I");
			else
				pStmt.setString(4, "U");

			pStmt.setTimestamp(5, masterDOB.getEffDate());
			pStmt.setTimestamp(6, masterDOB.getValidTo());
			pStmt.setInt(7, masterDOB.getAccValidityPeriod());
			pStmt.setString(8, masterDOB.getCustomerId());
			pStmt.setInt(9, masterDOB.getCustomerAddressId());
			pStmt.setTimestamp(10, masterDOB.getCreatedDate());
			pStmt.setString(11, masterDOB.getCreatedBy());
			pStmt.setString(12, masterDOB.getSalesPersonCode());
			pStmt.setString(13, masterDOB.getIndustryId());
			pStmt.setString(14, masterDOB.getCommodityId());

			if (masterDOB.isHazardousInd())
				pStmt.setString(15, "Y");
			else
				pStmt.setString(15, "N");

			if (masterDOB.getUnNumber() != null)
				pStmt.setString(16, masterDOB.getUnNumber());
			else
				pStmt.setNull(16, Types.INTEGER);

			if (masterDOB.getCommodityClass() != null)
				pStmt.setString(17, masterDOB.getCommodityClass());
			else
				pStmt.setNull(17, Types.INTEGER);

			pStmt.setString(18, masterDOB.getServiceLevelId());
			pStmt.setString(19, masterDOB.getIncoTermsId());
			pStmt.setString(20, masterDOB.getQuotingStation());
			pStmt.setString(21, masterDOB.getOriginLocation());
			pStmt.setString(22, masterDOB.getShipperZipCode());
			pStmt.setString(23, masterDOB.getOriginPort());
			pStmt.setString(24, masterDOB.getOverLengthCargoNotes());

			if (masterDOB.getRouteId() != null)
				pStmt.setLong(25, Long.parseLong(masterDOB.getRouteId()));
			else
				pStmt.setNull(25, Types.INTEGER);

			pStmt.setString(26, masterDOB.getDestLocation());
			pStmt.setString(27, masterDOB.getConsigneeZipCode());
			pStmt.setString(28, masterDOB.getDestPort());

			/*
			 * if(masterDOB.getSpotRatesFlag()) { pStmt.setString(29,"Y");
			 * pStmt.setString(30,masterDOB.getSpotRatesType()); } else {
			 * pStmt.setString(29,"N"); pStmt.setNull(30,Types.VARCHAR); }
			 */

			/*
			 * if(masterDOB.isEmailFlag()) pStmt.setString(29,"Y"); else
			 * pStmt.setNull(29,Types.VARCHAR);
			 * 
			 * if(masterDOB.isFaxFlag()) pStmt.setString(30,"Y"); else
			 * pStmt.setNull(30,Types.VARCHAR);
			 * 
			 * if(masterDOB.isPrintFlag()) pStmt.setString(31,"Y"); else
			 * pStmt.setNull(31,Types.VARCHAR);
			 */

			pStmt.setString(29, finalDOB.getEscalatedTo());
			pStmt.setTimestamp(30, new java.sql.Timestamp(
					(new java.util.Date()).getTime()));
			pStmt.setString(31, masterDOB.getUserId());

			pStmt.setString(32, masterDOB.getTerminalId());
			pStmt.setLong(33, masterDOB.getVersionNo());
			pStmt.setString(34, masterDOB.getBuyRatesPermission());
			pStmt.setString(35, masterDOB.getShipperZones());
			pStmt.setString(36, masterDOB.getConsigneeZones());
			// pStmt.setLong(37,masterDOB.getUniqueId());

			if (finalDOB.getFlagsDOB() != null) {

				flagsDOB = finalDOB.getFlagsDOB();

				if (flagsDOB.getPNFlag() != null)
					pStmt.setString(37, flagsDOB.getPNFlag());
				else
					pStmt.setNull(37, Types.VARCHAR);

				if (flagsDOB.getUpdateFlag() != null)
					pStmt.setString(38, flagsDOB.getUpdateFlag());
				else
					pStmt.setNull(38, Types.VARCHAR);

				if (flagsDOB.getActiveFlag() != null)
					pStmt.setString(39, flagsDOB.getActiveFlag());
				else
					pStmt.setNull(39, Types.VARCHAR);

				if (flagsDOB.getSentFlag() != null)
					pStmt.setString(40, flagsDOB.getSentFlag());
				else
					pStmt.setNull(40, Types.VARCHAR);

				if (flagsDOB.getCompleteFlag() != null)
					pStmt.setString(41, flagsDOB.getCompleteFlag());
				else
					pStmt.setNull(41, Types.VARCHAR);

				if (flagsDOB.getQuoteStatusFlag() != null)
					pStmt.setString(42, flagsDOB.getQuoteStatusFlag());
				else
					pStmt.setNull(42, Types.VARCHAR);

				pStmt.setString(43, flagsDOB.getEscalationFlag());
				pStmt.setString(44, flagsDOB.getInternalExternalFlag());
				pStmt.setString(45, flagsDOB.getEmailFlag());
				pStmt.setString(46, flagsDOB.getFaxFlag());
				pStmt.setString(47, flagsDOB.getPrintFlag());
			}
			pStmt.setString(48, masterDOB.getShipperMode());
			pStmt.setString(49, masterDOB.getConsigneeMode());
			pStmt.setString(50, masterDOB.getShipperConsoleType());
			pStmt.setString(51, masterDOB.getConsigneeConsoleType());
			pStmt.setString(52, masterDOB.getSalesPersonFlag());
			pStmt.setTimestamp(53, masterDOB.getCustDate());// Added by Rakesh
															// for Issue:236359
			pStmt.setString(54, masterDOB.getCustTime());// Added by Rakesh
															// for Issue:
			pStmt.setLong(55, id);
			int k = pStmt.executeUpdate();

			if (pStmt != null)
				pStmt.close();

			finalDOB.setMasterDOB(masterDOB);

			if (masterDOB.getCustomerContacts() != null)
				updateQuoteContactPersons(masterDOB, id, connection);

			// if(masterDOB.getSpotRatesFlag())
			updateQuoteSpotRates(finalDOB, id, connection);

			if (masterDOB.getChargeGroupIds() != null)
				updateQuoteChargeGroups(masterDOB, id, connection);

			if (masterDOB.getHeaderFooter() != null)
				updateQuoteHeaderFooter(masterDOB, id, connection);

			legDetails = finalDOB.getLegDetails();
			legSize = legDetails.size();

			for (int i = 0; i < legSize; i++) {
				legDOB = (QuoteFreightLegSellRates) legDetails.get(i);
				if (legDOB.getSelectedFreightChargesListIndices() == null)
					updateFlag = false;
			}
			if (updateFlag)
				updateSelectedRates(finalDOB, id, connection);

			if (finalDOB.getExternalNotes() != null)
				updateNotes(connection, id, finalDOB);

			setTransactionDetails(masterDOB);
			// @@Added by Kameswari for the WPBN issue-61289

			if (finalDOB.getAttachmentDOBList() != null) {
				updateAttachmentIdList(finalDOB);
			}
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[modifyQuoteMasterDetails(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteMasterDetails(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * Quote Contact dtl table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void insertQuoteContactPersons(QuoteMasterDOB masterDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterContactPersonInsQuery);
			int len = masterDOB.getCustomerContacts().length;
			for (int i = 0; i < len; i++) {
				if (masterDOB.getCustomerContacts()[i] != null
						&& masterDOB.getCustomerContacts()[i].trim().length() != 0) {
					pStmt.setLong(1, masterDOB.getUniqueId());
					pStmt.setString(2, masterDOB.getCustomerId());
					pStmt.setInt(3, Integer.parseInt((masterDOB
							.getCustomerContacts())[i]));
					pStmt.addBatch();
				}
			}
			pStmt.executeBatch();
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
			// throw new Exception(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
			// throw new Exception(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]-> "
								+ ex.toString());
			}
		}
	}

	/**
	 * This method is used to updates the master info from QuoteMasterDOB to
	 * Quote Contact dtl table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void updateQuoteContactPersons(QuoteMasterDOB masterDOB, long id,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterContactPersonDelQry);
			pStmt.clearParameters();
			pStmt.setLong(1, id);
			pStmt.executeUpdate();
			if (pStmt != null)
				pStmt.close();

			if (masterDOB.getCustomerContacts() != null)
				insertQuoteContactPersons(masterDOB, connection);
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
			// throw new Exception(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
			// throw new Exception(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteContactPersons(masterDOB,connection)]-> "
								+ ex.toString());
			}
		}
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * Quote Spot Rtaes table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void insertQuoteSpotRates(QuoteFinalDOB finalDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		ArrayList list = null;
		ArrayList legDetails = null;
		QuoteFreightLegSellRates legDOB = null;
		int legSize = 0;
		QuoteMasterDOB masterDOB = null;
		Hashtable spotRates = null;
		ArrayList weightBreaks = null;
		double[] spotRateArray = null;
		boolean isSpotRates = true;

		try {
			pStmt = connection.prepareStatement(masterSpotRatesInsQuery);
			legDetails = finalDOB.getLegDetails();

			if (legDetails != null)
				legSize = legDetails.size();

			masterDOB = finalDOB.getMasterDOB();

			for (int i = 0; i < legSize; i++) {
				legDOB = (QuoteFreightLegSellRates) legDetails.get(i);

				if (legDOB.isSpotRatesFlag()) {
					spotRates = legDOB.getSpotRateDetails();
					weightBreaks = legDOB.getWeightBreaks();

					if (weightBreaks != null) {
						int wtBreakSize = weightBreaks.size();
						for (int j = 0; j < wtBreakSize; j++) {
							spotRateArray = (double[]) spotRates
									.get((String) weightBreaks.get(j));

							pStmt.setDouble(1, masterDOB.getUniqueId());
							pStmt.setInt(2, i);
							pStmt.setInt(3, j);
							pStmt.setString(4, (String) weightBreaks.get(j));
							pStmt.setDouble(5, spotRateArray[0]);
							pStmt.setDouble(6, spotRateArray[1]);
							pStmt.setDouble(7, spotRateArray[2]);
							pStmt.setInt(8, legDOB.getShipmentMode());
							pStmt.setString(9, legDOB.getServiceLevel());
							pStmt.setString(10, legDOB.getUom());
							pStmt.setString(11, legDOB.getDensityRatio());
							pStmt.setString(12, legDOB.getSpotRatesType());
							pStmt.setString(13, legDOB.getCurrency());// @@added
																		// by
																		// kameswari
																		// for
																		// the
																		// issue
																		// WPBN-30908
							// @@added by kameswari for Surcharge Enhancements
							if (weightBreaks.get(j).toString().startsWith("FS"))
								pStmt.setString(14, "FUEL SURCHARGE");
							else if (weightBreaks.get(j).toString().startsWith(
									"SS"))
								pStmt.setString(14, "SECURITY SURCHARGE");
							else if (weightBreaks.get(j).toString().startsWith(
									"CAF"))
								pStmt.setString(14, "C.A.F%");
							else if (weightBreaks.get(j).toString().startsWith(
									"B")
									|| weightBreaks.get(j).toString()
											.startsWith("BAF"))
								pStmt.setString(14, "B.A.F");
							else if (weightBreaks.get(j).toString().startsWith(
									"CSF"))
								pStmt.setString(14, "C.S.F");
							else if (weightBreaks.get(j).toString().startsWith(
									"PSS"))// @@Added by Kameswari for the WPBN
											// issue-135215
								pStmt.setString(14, "P.S.S");
							else if (weightBreaks.get(j).toString().startsWith(
									"SURCHARGE"))
								pStmt.setString(14, "SURCHARGE");
							else if (weightBreaks.get(j).toString().endsWith(
									"CAF")
									|| weightBreaks.get(j).toString().endsWith(
											"caf"))
								pStmt.setString(14, "CAF%");
							else if (weightBreaks.get(j).toString().endsWith(
									"CSF")
									|| weightBreaks.get(j).toString().endsWith(
											"csf"))
								pStmt.setString(14, "CSF");
							else if (weightBreaks.get(j).toString().endsWith(
									"BAF")
									|| weightBreaks.get(j).toString().endsWith(
											"baf"))
								pStmt.setString(14, "BAF");
							else if (weightBreaks.get(j).toString().endsWith(
									"PSS")
									|| weightBreaks.get(j).toString().endsWith(
											"pss"))
								pStmt.setString(14, "PSS");
							else
								pStmt.setString(14, "A FREIGHT RATE");
							// @@Enhancements
							pStmt.addBatch();
						}
					}
				}

			}
			pStmt.executeBatch();
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)] -> "
							+ e.toString());
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Commented
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to updates the master info from QuoteMasterDOB to
	 * Quote Spot Rtaes table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void updateQuoteSpotRates(QuoteFinalDOB finalDOB, long id,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		ArrayList list = null;
		ArrayList legDetails = null;
		QuoteFreightLegSellRates legDOB = null;
		int legSize = 0;
		QuoteMasterDOB masterDOB = null;
		Hashtable spotRates = null;
		ArrayList weightBreaks = null;
		double[] spotRateArray = null;
		boolean isSpotRates = true;

		try {
			pStmt = connection.prepareStatement(masterSpotRatesDelQry);
			pStmt.clearParameters();
			pStmt.setLong(1, id);
			pStmt.executeUpdate();
			if (pStmt != null)
				pStmt.close();

			insertQuoteSpotRates(finalDOB, connection);
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)] -> "
							+ e.toString());
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteSpotRates(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * Quote Charge Group dtl table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void insertQuoteChargeGroups(QuoteMasterDOB masterDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterChargeGroupsInsQuery);
			int len = masterDOB.getChargeGroupIds().length;

			for (int i = 0; i < len; i++) {
				pStmt.setLong(1, masterDOB.getUniqueId());
				pStmt.setString(2, (masterDOB.getChargeGroupIds())[i]);
				pStmt.addBatch();
			}
			pStmt.executeBatch();
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to updates the master info from QuoteMasterDOB to
	 * Quote Charge Group dtl table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void updateQuoteChargeGroups(QuoteMasterDOB masterDOB, long id,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterChargeGroupsDelQry);
			pStmt.clearParameters();
			pStmt.setLong(1, id);
			pStmt.executeUpdate();
			if (pStmt != null)
				pStmt.close();

			if (masterDOB.getChargeGroupIds() != null)
				insertQuoteChargeGroups(masterDOB, connection);
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteChargeGroups(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to insert the master info from QuoteMasterDOB to
	 * Quote Header footer table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void insertQuoteHeaderFooter(QuoteMasterDOB masterDOB,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterHeaderFooterInsQuery);
			int len = masterDOB.getHeaderFooter().length;
			for (int i = 0; i < len; i++) {
				if (masterDOB.getContentOnQuote()[i] != null
						&& masterDOB.getContentOnQuote()[i].trim().length() != 0) {
					pStmt.setLong(1, masterDOB.getUniqueId());
					pStmt.setString(2, (masterDOB.getHeaderFooter())[i]);
					pStmt.setString(3, (masterDOB.getContentOnQuote())[i]);
					pStmt.setString(4,
							(masterDOB.getLevels() != null ? masterDOB
									.getLevels()[i] != null ? masterDOB
									.getLevels()[i] : "" : ""));
					pStmt.setString(5, (masterDOB.getAlign())[i]);
					pStmt.addBatch();
				}
			}
			pStmt.executeBatch();
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/**
	 * This method is used to updates the master info from QuoteMasterDOB to
	 * Quote Header footer table
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	private void updateQuoteHeaderFooter(QuoteMasterDOB masterDOB, long id,
			Connection connection) throws SQLException {
		PreparedStatement pStmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		try {
			pStmt = connection.prepareStatement(masterHeaderFooterDelQry);
			pStmt.clearParameters();
			pStmt.setLong(1, id);
			pStmt.executeUpdate();
			if (pStmt != null)
				pStmt.close();

			if (masterDOB.getHeaderFooter() != null)
				insertQuoteHeaderFooter(masterDOB, connection);
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, null);// Modified
																	// By
																	// RajKumari
																	// on
																	// 24-10-2008
																	// for
																	// Connection
																	// Leakages.
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertQuoteHeaderFooter(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	private void insertRoutePlanDetails(Connection connection,
			QuoteFinalDOB finalDOB) throws SQLException {
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		ArrayList legDetails = null;
		QuoteFreightLegSellRates legDOB = null;
		QuoteMasterDOB masterDOB = null;

		int noOfLegs = 0;
		long rt_plan_id = 0;// RTPLAN_SEQ

		String originTerminalId = null;
		String destTerminalId = null;

		try {
			masterDOB = finalDOB.getMasterDOB();

			// masterDOB = getTerminalForLocation(connection,masterDOB);

			legDetails = finalDOB.getLegDetails();
			noOfLegs = legDetails.size();

			pStmt = connection
					.prepareStatement("SELECT RTPLAN_SEQ.NEXTVAL NEXTVAL FROM DUAL");

			rs = pStmt.executeQuery();

			while (rs.next())
				rt_plan_id = rs.getLong("NEXTVAL");

			if (pStmt != null)
				pStmt.close();

			pStmt = connection.prepareStatement(routePlanInsertQuery);

			originTerminalId = getTerminalForLocation(connection, masterDOB
					.getOriginLocation());
			destTerminalId = getTerminalForLocation(connection, masterDOB
					.getDestLocation());

			pStmt.setLong(1, rt_plan_id);
			// pStmt.setLong(2,masterDOB.getQuoteId());//@@ Commented by
			// subrahmanyam for the enhancement 146971 on 1/12/08
			pStmt.setString(2, masterDOB.getQuoteId());// @@ Added by
														// subrahmanyam for the
														// enhancement 146971 on
														// 1/12/08
			pStmt.setString(3, originTerminalId);
			pStmt.setString(4, masterDOB.getDestinationTerminal());
			pStmt.setString(5, masterDOB.getOriginLocation());
			pStmt.setString(6, masterDOB.getDestLocation());
			pStmt.setString(7, masterDOB.getCustomerId());
			pStmt.setInt(8, masterDOB.getShipmentMode());
			pStmt.setTimestamp(9, masterDOB.getCreatedDate());
			pStmt.setTimestamp(10, masterDOB.getCreatedDate());

			pStmt.executeUpdate();

			if (pStmt != null)
				pStmt.close();

			pStmt = connection.prepareStatement(routeLegInsertQuery);

			for (int i = 0; i < noOfLegs; i++) {
				legDOB = (QuoteFreightLegSellRates) legDetails.get(i);

				originTerminalId = getTerminalForLocation(connection, legDOB
						.getOrigin());
				destTerminalId = getTerminalForLocation(connection, legDOB
						.getDestination());

				pStmt.setLong(1, rt_plan_id);
				pStmt.setInt(2, (i + 1));
				pStmt.setString(3, "LL");
				pStmt.setString(4, legDOB.getOrigin());
				pStmt.setString(5, legDOB.getDestination());
				pStmt.setInt(6, legDOB.getShipmentMode());
				pStmt.setString(7, "Y");// @@Valid_invalid Flag(Y is for Valid)
				pStmt.setString(8, originTerminalId);
				pStmt.setString(9, destTerminalId);

				pStmt.addBatch();
			}
			pStmt.executeBatch();
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error while inserting into Route
			// Plan:"+e);
			logger.error(FILE_NAME + "Error while inserting into Route Plan:"
					+ e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(null, pStmt, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[insertRoutePlanDetails(masterDOB,connection)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[insertRoutePlanDetails(masterDOB,connection)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	private String getTerminalForLocation(Connection conn, String locationId)
			throws SQLException {
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		String terminalId = null;
		try {
			pStmt = conn.prepareStatement(terminalQuery);

			pStmt.setString(1, locationId);

			rs = pStmt.executeQuery();

			if (rs.next())
				terminalId = rs.getString("TERMINALID");
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error while getting Terminal Ids"+e);
			logger.error(FILE_NAME + "Error while getting Terminal Ids" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(null, pStmt, rs);
		}
		return terminalId;
	}

	/**
	 * This method helps in getting the sell rate details between a specified
	 * origin and destination ,for a specified servicelevel and shipment mode
	 * 
	 * @param origin
	 * @param destination
	 * @param serviceLevelId
	 * @param shipmentMode
	 * 
	 * @exception Exception
	 */
	// @@ Commented by subrahmanyam for the enhancement 146971 on 1/12/08
	// public QuoteFreightLegSellRates getFrtLegSellRates(String origin, String
	// destination, String serviceLevelId, int shipmentMode, String
	// terminalId,String permissionFlag,String operation,long quoteId) throws
	// SQLException
	// @@ Added by subrahmanyam for the enhancement 146971 on 1/12/08
	// Method Signature was Modified by Kishore For Weight Break in Single Quote
	public QuoteFreightLegSellRates getFrtLegSellRates(String origin,
			String destination, String serviceLevelId, int shipmentMode,
			String terminalId, String permissionFlag, String operation,
			String quoteId, String weightBreak) throws SQLException {
		Connection connection = null;
		CallableStatement csmt = null;
		// PreparedStatement psmt = null;//Commented By RajKumari on 24-10-2008
		// for Connection Leakages.
		ResultSet rs = null;
		String freightRatesQuery = null;
		QuoteFreightLegSellRates legRateDetails = null;// to maintain the info
														// of each leg
		String[] slabWeightBreaks = null;// to maintain the slab weight
											// breaks in order
		String[] listWeightBreaks = null;// to maintain the list weight
											// breaks in order
		String[] flatWeightBreaks = null;
		ArrayList sellRates = null;// to maintain the list of all rate dobs
		QuoteFreightRSRCSRDOB sellRatesDOB = null;// to maintain one record
													// that is to be displayed
		String[] rateDescriptions = null;
		String wtBreak = null;
		try {
			long start = System.currentTimeMillis();
			connection = this.getConnection();

			// csmt = connection.prepareCall("{CALL
			// QMS_QUOTE_PACK.Quote_Sell_Buy_Rates_Proc(?,?,?,?,?,?,?,?,?)}");
			// Modified by Kishore For Weight Break in Single Quote
			if (("Modify".equalsIgnoreCase(operation)
					|| "VIEW".equalsIgnoreCase(operation) || "COPY"
					.equalsIgnoreCase(operation))
					&& ("".equals(weightBreak) || null == weightBreak))
				csmt = connection
						.prepareCall("{CALL QMS_QUOTE_PACK.Quote_Sell_Buy_Rates_Proc(?,?,?,?,?,?,?,?,?)}");
			else
				csmt = connection
						.prepareCall("{CALL QMS_QUOTE_PACK.SINGLE_QUOTE_SEL_BUY_RATE_PROC(?,?,?,?,?,?,?,?,?,?)}");

			csmt.setString(1, origin);
			csmt.setString(2, destination);
			csmt.setString(3, terminalId);
			csmt.setString(4, serviceLevelId);
			csmt.setInt(5, shipmentMode);
			double d;

			// this is the whether the user has the buy rates permission or not
			// this paticular flag should be retrived from ? and is to be set in
			// the master dob
			// so that it can be used here for now it is defaulted
			csmt.setString(6, permissionFlag);
			csmt.setString(7, operation);
			// csmt.setString(8,quoteId!=0?""+quoteId:""); //@@ Commented by
			// subrahmanyam for the enhancement 146971 on 1/12/08
			csmt.setString(8, quoteId != null ? "" + quoteId : ""); // @@ Added
																	// by
																	// subrahmanyam
																	// for the
																	// enhancement
																	// 146971 on
																	// 1/12/08

			// Modified by Kishore For Weight Break in Single Quote
			if (("Modify".equalsIgnoreCase(operation)
					|| "VIEW".equalsIgnoreCase(operation) || "COPY"
					.equalsIgnoreCase(operation))
					&& ("".equals(weightBreak) || null == weightBreak)) {
				csmt.registerOutParameter(9, OracleTypes.CURSOR);
			} else {
				wtBreak = weightBreak != null ? weightBreak.toUpperCase()
						: null;
				csmt.setString(9, wtBreak);
				csmt.registerOutParameter(10, OracleTypes.CURSOR);
			}

			csmt.execute();

			// get the freight rates
			if (("Modify".equalsIgnoreCase(operation)
					|| "VIEW".equalsIgnoreCase(operation) || "COPY"
					.equalsIgnoreCase(operation))
					&& ("".equals(weightBreak) || null == weightBreak)) {
				rs = (ResultSet) csmt.getObject(9);
			} else {
				rs = (ResultSet) csmt.getObject(10);
			}

			logger
					.info("Time Taken for DB procedure in seconds for 2nd screen (Quote_Sell_Buy_Rates_Proc)  :  "
							+ ((System.currentTimeMillis()) - start)
							+ "   Origin ::"
							+ origin
							+ " Destination::"
							+ destination + " TerminalId :: " + terminalId);
			sellRates = new ArrayList();

			while (rs.next()) {
				// if the sellRate ArrayList is not initialised then initialise
				// it

				// Create a new dob to set the details of the new record
				sellRatesDOB = new QuoteFreightRSRCSRDOB();

				sellRates.add(sellRatesDOB);

				// now set the dob with the data

				if (!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))// only
																		// the
																		// RSR
																		// or
																		// CSR
																		// rates
																		// will
																		// have
																		// a
																		// sell
																		// rate
																		// id
					sellRatesDOB.setSellRateId(rs.getInt("BUYRATEID"));

				sellRatesDOB.setRsrOrCsrFlag(rs.getString("RCB_FLAG"));
				sellRatesDOB.setOrigin(rs.getString("ORIGIN"));
				sellRatesDOB.setDestination(rs.getString("DESTINATION"));
				sellRatesDOB.setCarrierId(rs.getString("CARRIER_ID"));
				sellRatesDOB.setServiceLevelId(rs.getString("SERVICE_LEVEL"));
				sellRatesDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));// added
																			// by
																			// VLAKSHMI
																			// for
																			// issue
																			// 146968
																			// on
																			// 5/12/2008
				sellRatesDOB.setServiceLevelDesc(rs
						.getString("SERVICE_LEVEL_DESC"));// @@Added by
															// Kameswari for the
															// WPBN issue-31330
				sellRatesDOB
						.setFrequency(rs.getString("FREQUENCY") != null ? rs
								.getString("FREQUENCY") : "");
				sellRatesDOB
						.setTransitTime(rs.getString("TRANSIT_TIME") != null ? rs
								.getString("TRANSIT_TIME")
								: "");
				if (!"BR".equalsIgnoreCase(rs.getString("RCB_FLAG")))// if it
																		// is a
																		// RSR
																		// or
																		// CSR
					sellRatesDOB.setBuyRateId(rs.getInt("REC_BUYRATE_ID"));
				else
					sellRatesDOB.setBuyRateId(rs.getInt("BUYRATEID"));// if it
																		// is a
																		// buy
																		// rate

				sellRatesDOB.setLaneNo(rs.getInt("LANE_NO"));
				sellRatesDOB.setNotes(rs.getString("NOTES") != null ? rs
						.getString("NOTES") : "");
				sellRatesDOB
						.setExtNotes(rs.getString("EXTERNAL_NOTES") != null ? rs
								.getString("EXTERNAL_NOTES")
								: "");// Added by Mohan for Issue No.219976 on
										// 04-11-2010
				sellRatesDOB.setWeightBreakType(rs.getString("WEIGHT_BREAK"));
				sellRatesDOB.setDensityRatio(rs.getString("DENSITY_CODE"));// Added
																			// by
																			// govind
																			// for
																			// differentiating
																			// rates
																			// in
																			// second
																			// screen
				if ("G".equalsIgnoreCase(rs.getString("WT_CLASS")))
					sellRatesDOB.setWeightClass("General");
				else
					sellRatesDOB.setWeightClass("WeightScale");

				sellRatesDOB.setCreatedTerminalId(rs.getString("TERMINALID"));
				sellRatesDOB.setCurrency(rs.getString("CURRENCY"));
				sellRatesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
				sellRatesDOB.setEffDate(rs.getTimestamp("EFROM"));
				sellRatesDOB.setValidUpTo(rs.getTimestamp("VALIDUPTO"));

				rateDescriptions = rs.getString("RATE_DESCRIPTION").split(",");
				// if(slabWeightBreaks==null &&
				// "SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
				if ("SLAB".equalsIgnoreCase(rs.getString("WEIGHT_BREAK"))) {
					slabWeightBreaks = rs.getString("WEIGHT_BREAK_SLAB").split(
							",");
				}
				// else if(listWeightBreaks==null &&
				// "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
				else if ("LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK"))) {
					listWeightBreaks = rs.getString("WEIGHT_BREAK_SLAB").split(
							",");
				}
				// ADDED BY SUBRAHMANYAM FOR CR-219973
				// else if(flatWeightBreaks==null &&
				// "FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK")))
				else if ("FLAT".equalsIgnoreCase(rs.getString("WEIGHT_BREAK"))) {
					flatWeightBreaks = rs.getString("WEIGHT_BREAK_SLAB").split(
							",");
				}
				sellRatesDOB.setFlatWeightBreaks(flatWeightBreaks);
				sellRatesDOB.setSlabWeightBreaks(slabWeightBreaks);
				sellRatesDOB.setListWeightBreaks(listWeightBreaks);
				sellRatesDOB.setRateDescriptions(rateDescriptions);
				/*
				 * //@@Added by Kameswari fro the CR- else { flatWeightBreaks =
				 * rs.getString("WEIGHT_BREAK_SLAB").split(","); }
				 */
				sellRatesDOB.setChargeRates(rs.getString("CHARGERATE").split(
						","));
				// added by VLAKSHMI for issue 146968 on 5/12/2008
				if (rs.getString("CHECKED_FLAG") != null
						&& "FCL".equals(rs.getString("CONSOLE_TYPE"))) {
					sellRatesDOB.setCheckedFalg(rs.getString("CHECKED_FLAG")
							.split(","));
				}// end for issue 146968 on 5/12/2008
			}
			// getting the freight rates ends here

			// only if the sell rates are there initialize the
			// QuoteFreightLegSellRates dob and assign the data to it

			if (sellRates != null && sellRates.size() > 0) {

				legRateDetails = new QuoteFreightLegSellRates();

				legRateDetails.setSlabWeightBreaks(slabWeightBreaks);
				legRateDetails.setListWeightBreaks(listWeightBreaks);
				legRateDetails.setFlatWeightBreaks(flatWeightBreaks);
				// legRateDetails.setRateDescriptions(rateDescriptions);
				legRateDetails.setRates(sellRates);
			}
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getFrtLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(connection, csmt, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[getFreightLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[getFreightLegSellRates(origin,dest,servicelevel,shipmentMode,terminalid)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
		return legRateDetails;
	}

	/**
	 * This method helps in getting the charge details for a specified terminal
	 * and shipment mode
	 * 
	 * QuoteFinalDOB finalDOB
	 * 
	 * @exception Exception
	 */
	public QuoteFinalDOB getCharges(QuoteFinalDOB finalDOB) throws SQLException {
		Connection connection = null;
		CallableStatement csmt = null;
		ResultSet rs = null;
		ArrayList originChargesList = null;// to maintain the list of all
											// origin charge dobs
		ArrayList destChargesList = null;// to maintain the list of all
											// origin charge dobs
		QuoteCharges delChargesDOB = null;// @@ To get the Delivery Charge
											// DOB, so that it can be Placed at
											// the end of the list
		QuoteCharges chargesDOB = null;// to maintain one record that is to be
										// displayed
		QuoteChargeInfo chargeInfo = null;
		ArrayList chargeInfoList = null;
		String flag = null;
		QuoteMasterDOB masterDOB = null;

		double sellRate = 0;
		String weightBreak = null;
		String rateType = null;
		// added by phani sekhar for wpbn 170758 on 20090626
		String mType = null;
		String dType = null;
		String terminalFlag = null;
		PreparedStatement pstmt = null;
		PreparedStatement notesPstmt = null;// //Added by Mohan for Issue
											// No.219976 on 04-11-2010
		ResultSet notesRs = null;
		// PreparedStatement pstmt1 = null;//@@ Commented by Govind for the
		// ConnectionLeaks
		String query = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID IN(SELECT RG.PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN RG WHERE RG.CHILD_TERMINAL_ID = ?)";
		String query1 = "SELECT STM.OPER_ADMIN_FLAG FROM Fs_Fr_Terminalmaster STM WHERE STM.TERMINALID =? ";
		String query2 = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID =? ";
		String notesQry = "SELECT INTERNAL_NOTES,EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID =? ";// Added
																											// by
																											// Mohan
																											// for
																											// Issue
																											// No.219976
																											// on
																											// 04-11-2010
		// ends 170758
		try {
			masterDOB = finalDOB.getMasterDOB();
			long start = System.currentTimeMillis();
			connection = this.getConnection();
			// added by phani sekhar for wpbn 170758 20090626
			/*
			 * pstmt1= connection.prepareStatement(query1);
			 * pstmt1.setString(1,masterDOB.getQuotingStation());
			 * rs=pstmt1.executeQuery(); if(rs.next()) {
			 * terminalFlag=rs.getString("OPER_ADMIN_FLAG"); }
			 * ConnectionUtil.closePreparedStatement(pstmt1,rs);
			 */
			terminalFlag = masterDOB.getAccessLevel();
			// if(!"H".equals(terminalFlag))
			// {
			if ("A".equals(terminalFlag) || "H".equals(terminalFlag))
				pstmt = connection.prepareStatement(query2);
			else
				pstmt = connection.prepareStatement(query);
			// pstmt.setString(1,masterDOB.getQuotingStation());
			pstmt.setString(1, masterDOB.getTerminalId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				mType = rs.getString("MARGIN_TYPE");
				dType = rs.getString("DISCOUNT_TYPE");
			}
			ConnectionUtil.closePreparedStatement(pstmt, rs);
			// }
			// ENDS 170758
			connection.setAutoCommit(false);
			// csmt = connection.prepareCall("{CALL
			// QMS_QUOTE_PACK.quote_sell_buy_charges_proc(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			csmt = connection
					.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_charges_proc(?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			originChargesList = finalDOB.getOriginChargesList();
			destChargesList = finalDOB.getDestChargesList();
			// @@Fetching the delivery Charge & Removing it from destination
			// list.
			if (destChargesList != null && destChargesList.size() == 1) {
				delChargesDOB = (QuoteCharges) destChargesList.get(0);
				destChargesList.remove(0);
			}

			String[] chargeGroupIds = masterDOB.getChargeGroupIds();
			StringBuffer strChargeGroups = new StringBuffer();

			if (chargeGroupIds != null) {
				for (int i = 0; i < chargeGroupIds.length - 1; i++) {
					strChargeGroups = strChargeGroups.append(chargeGroupIds[i])
							.append("~");
				}
			}
			if (chargeGroupIds != null) {
				strChargeGroups
						.append(chargeGroupIds[chargeGroupIds.length - 1]);
			}
			csmt.setString(1, strChargeGroups.toString());
			csmt.setString(2, masterDOB.getSalesPersonCode());
			csmt.setString(3, masterDOB.getTerminalId());
			csmt.setString(4, masterDOB.getBuyRatesPermission());
			csmt.setDouble(5, finalDOB.getChargesMargin());
			csmt.setDouble(6, finalDOB.getChargesDiscount());
			csmt.setString(7, masterDOB.getOriginLocation());
			csmt.setString(8, masterDOB.getDestLocation());
			csmt.setString(9, "" + masterDOB.getShipmentMode());
			csmt.setString(10, masterDOB.getCustomerId());
			csmt.setString(11, masterDOB.getOperation());
			// csmt.setString(12,masterDOB.getQuoteId()!=0?""+masterDOB.getQuoteId():"");
			// //@@ Commented by subrahmanyam for the enhancement 146971 on
			// 1/12/08
			csmt.setString(12, masterDOB.getQuoteId() != null ? ""
					+ masterDOB.getQuoteId() : ""); // @@ Commented by
													// subrahmanyam for the
													// enhancement 146971 on
													// 1/12/08

			csmt.registerOutParameter(13, OracleTypes.CURSOR);

			csmt.execute();

			rs = (ResultSet) csmt.getObject(13);
			logger
					.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_charges_proc) :  "
							+ ((System.currentTimeMillis()) - start)
							+ "   UserId ::"
							+ masterDOB.getUserId()
							+ " Origin :: "
							+ masterDOB.getOriginLocation()
							+ " Destination::"
							+ masterDOB.getDestLocation()
							+ " TerminalId :: " + masterDOB.getTerminalId());
			// get the Charges
			while (rs.next()) {
				flag = rs.getString("SEL_BUY_FLAG");

				if (originChargesList == null) {
					originChargesList = new ArrayList();
				}
				if (destChargesList == null) {
					destChargesList = new ArrayList();
				}

				if (chargesDOB != null
						&& (rs.getString("SELLCHARGEID").equalsIgnoreCase(
								chargesDOB.getSellChargeId()) || rs.getString(
								"SELLCHARGEID").equalsIgnoreCase(
								chargesDOB.getBuyChargeId()))) {
					chargeInfo = new QuoteChargeInfo();

					chargeInfoList.add(chargeInfo);

					chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));

					if (rs.getString("CURRENCY") != null
							&& rs.getString("CURRENCY").trim().length() != 0)
						chargeInfo.setCurrency(rs.getString("CURRENCY"));
					else
						chargeInfo.setCurrency(masterDOB.getTerminalCurrency());

					chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
					chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
					chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
					chargeInfo.setSellChargeMarginType(rs
							.getString("MARGIN_TYPE"));
					chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
					// if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag())
					// || chargesDOB.getMarginDiscountFlag()==null)
					if ("B".equalsIgnoreCase(flag)
							|| "M".equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())) {
						chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
						chargeInfo.setTieMarginDiscountValue(rs
								.getDouble("MARGINVALUE"));
						// modified by phani sekhar for wpbn 170758 on 20090626
						if (!"Y".equals(chargesDOB.getSelectedFlag()))
							chargeInfo.setMarginType(mType);
						else
							chargeInfo.setMarginType(rs
									.getString("MARGIN_TYPE"));
						// ends 170758
						// @@ Commented by subrahmanyam for the Enhancement
						// 154381 on 28/01/09
						/*
						 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
						 */
						// @@ Added by subrahmanyam for the enhancement 154381
						// on 28/01/09
						if (rs.getDouble("MARGINVALUE") > 0
								|| "Y".equalsIgnoreCase(rs
										.getString("SELECTED_FLAG"))) {
							// if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modified
							// by phani sekhar for wpbn 170758 on 20090626
							if ("A"
									.equalsIgnoreCase(chargeInfo
											.getMarginType()))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							// else
							// if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modified
							// by phani sekhar for wpbn 170758 on 20090626
							else if ("P".equalsIgnoreCase(chargeInfo
									.getMarginType()))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
							else if ("".equals(chargeInfo.getDiscountType())
									|| chargeInfo.getDiscountType() == null)
								sellRate = rs.getDouble("BUYRATE");
						} else
							sellRate = rs.getDouble("BUYRATE");
						// @@ Ended by subrahmanyam for the Enhancement 154381
						// on 28/01/09
					}
					// else if("S".equalsIgnoreCase(flag))
					else {

						chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
						chargeInfo.setTieMarginDiscountValue(rs
								.getDouble("MARGINVALUE")); // @@Added by
															// subrahmanyam for
															// the wpbn id:
															// 196745 on
															// 02/Feb/10
						// modified by phani sekhar for wpbn 170758 on 20090626
						if (!"Y".equals(chargesDOB.getSelectedFlag()))
							chargeInfo.setDiscountType(dType);
						else
							chargeInfo.setDiscountType(rs
									.getString("MARGIN_TYPE"));
						// ends 170758

						// if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						// modified by phani sekhar for wpbn 170758 on 20090626
						if ("A".equalsIgnoreCase(chargeInfo.getDiscountType()))
							sellRate = rs.getDouble("SELLRATE")
									- rs.getDouble("MARGINVALUE");
						// else
						// if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						// modified by phani sekhar for wpbn 170758 on 20090626
						else if ("P".equalsIgnoreCase(chargeInfo
								.getDiscountType()))
							sellRate = rs.getDouble("SELLRATE")
									- (rs.getDouble("SELLRATE")
											* rs.getDouble("MARGINVALUE") / 100);
						else if ("".equals(chargeInfo.getDiscountType())
								|| chargeInfo.getDiscountType() == null)
							sellRate = rs.getDouble("SELLRATE");
						else
							sellRate = rs.getDouble("BUYRATE");

					}
					weightBreak = rs.getString("WEIGHT_BREAK");
					rateType = rs.getString("RATE_TYPE");
					// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
					if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
							|| "MIN".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| "MAX".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
									.equalsIgnoreCase(rateType))
							|| ("BOTH".equalsIgnoreCase(rateType) && "F"
									.equalsIgnoreCase(chargeInfo
											.getRateIndicator()))) // MODIFIED
																	// FOR
																	// 183812 &
																	// 195552 ON
																	// 25-JAN-10
																	// BY
																	// SUBRAHMANYAM
					{
						chargeInfo.setBasis("Per Shipment");
					} else {
						if ("Per Kg".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Pound");
						} else if ("Per CFT".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Feet");
						} else if ("Per CBM".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Meter");
						} else
							chargeInfo.setBasis(rs.getString("CHARGEBASIS"));

						if (weightBreak != null
								&& ("Percent".equalsIgnoreCase(weightBreak) || weightBreak
										.endsWith("%")))
							chargeInfo.setPercentValue(true);

					}

					chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
					chargeInfo.setLineNumber(rs.getInt("LINE_NO"));

					/*
					 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
					 * sellRate =
					 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
					 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
					 * sellRate =
					 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
					 */

					chargeInfo.setSellRate(sellRate);
					// @@Added by subrahmanyam for the wpbn id: 196745 on
					// 02/Feb/10
					chargeInfo.setTieSellRateValue(sellRate);
					chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));

				} else {
					chargesDOB = new QuoteCharges();

					if ("Origin".equalsIgnoreCase(rs
							.getString("COST_INCURREDAT")))
						originChargesList.add(chargesDOB);
					else if ("Destination".equalsIgnoreCase(rs
							.getString("COST_INCURREDAT")))
						destChargesList.add(chargesDOB);

					// if it is a sell charge/rate
					if ("S".equalsIgnoreCase(flag)) {
						chargesDOB
								.setSellChargeId(rs.getString("SELLCHARGEID"));
						chargesDOB
								.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
					} else if ("B".equalsIgnoreCase(flag)) {
						chargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID"));
					}

					chargesDOB.setSellBuyFlag(flag);

					chargesDOB.setChargeId(rs.getString("CHARGE_ID"));
					chargesDOB.setTerminalId(rs.getString("TERMINALID"));
					chargesDOB.setMarginDiscountFlag(rs
							.getString("MARGIN_DISCOUNT_FLAG"));
					chargesDOB.setChargeDescriptionId(rs
							.getString("CHARGEDESCID"));
					chargesDOB.setInternalName(rs.getString("INT_CHARGE_NAME"));
					chargesDOB.setExternalName(rs.getString("EXT_CHARGE_NAME"));
					chargesDOB.setCostIncurredAt(rs
							.getString("COST_INCURREDAT"));
					chargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));

					chargeInfoList = new ArrayList();
					chargeInfo = new QuoteChargeInfo();
					chargeInfoList.add(chargeInfo);

					chargesDOB.setChargeInfoList(chargeInfoList);

					chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
					if (rs.getString("CURRENCY") != null
							&& rs.getString("CURRENCY").trim().length() != 0)
						chargeInfo.setCurrency(rs.getString("CURRENCY"));
					else
						chargeInfo.setCurrency(masterDOB.getTerminalCurrency());

					chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
					chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
					chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
					chargeInfo.setSellChargeMarginType(rs
							.getString("MARGIN_TYPE"));
					chargeInfo.setRateIndicator(rs.getString("RATE_INDICATOR"));
					weightBreak = rs.getString("WEIGHT_BREAK");
					rateType = rs.getString("RATE_TYPE");
					// chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
					// chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
					if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
							|| "MIN".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| "MAX".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
									.equalsIgnoreCase(rateType))
							|| ("BOTH".equalsIgnoreCase(rateType) && "F"
									.equalsIgnoreCase(chargeInfo
											.getRateIndicator()))) // MODIFIED
																	// FOR
																	// 183812 &
																	// 195552 ON
																	// 25-JAN-10
																	// BY
																	// SUBRAHMANYAM
					{
						chargeInfo.setBasis("Per Shipment");
					} else {
						if ("Per Kg".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Pound");
						} else if ("Per CFT".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Feet");
						} else if ("Per CBM".equalsIgnoreCase(rs
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Meter");
						} else
							chargeInfo.setBasis(rs.getString("CHARGEBASIS"));

						if (weightBreak != null
								&& ("Percent".equalsIgnoreCase(weightBreak) || weightBreak
										.endsWith("%")))
							chargeInfo.setPercentValue(true);
					}
					chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
					chargeInfo.setLineNumber(rs.getInt("LINE_NO"));

					// if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag())
					// || chargesDOB.getMarginDiscountFlag()==null)
					if ("B".equalsIgnoreCase(flag)
							|| "M".equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())) {

						chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
						chargeInfo.setTieMarginDiscountValue(rs
								.getDouble("MARGINVALUE"));
						// modified by phani sekhar for wpbn 170758 on 20090626
						if (!"Y".equals(chargesDOB.getSelectedFlag()))
							chargeInfo.setMarginType(mType);
						else
							chargeInfo.setMarginType(rs
									.getString("MARGIN_TYPE"));
						// ends 170758
						// @@ Commented by subrahmanyam for the Enhancemente
						// 154381 on 28/01/2009
						/*
						 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
						 */
						// @@ Added by subrahmanyam for the Enhancement 154381
						// on 28/01/09
						if (rs.getDouble("MARGINVALUE") > 0
								|| "Y".equalsIgnoreCase(rs
										.getString("SELECTED_FLAG"))) {
							// if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							// modified by phani sekhar for wpbn 170758 on
							// 20090626
							if ("A"
									.equalsIgnoreCase(chargeInfo
											.getMarginType()))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							// else
							// if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))modified
							// by phani sekhar for wpbn 170758 on 20090626
							else if ("P".equalsIgnoreCase(chargeInfo
									.getMarginType()))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
							else if (chargeInfo.getMarginType() == null
									|| "".equals(chargeInfo.getMarginType()))
								sellRate = rs.getDouble("BUYRATE");
						} else
							sellRate = rs.getDouble("BUYRATE");
						// @@ Ended by subrahmanyam for the enhancement 154381
						// on 28/01/09
					}
					// else if("S".equalsIgnoreCase(flag))
					else {
						chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
						chargeInfo.setTieMarginDiscountValue(rs
								.getDouble("MARGINVALUE"));
						// modifed by phani sekhar for wpbn 170758 on 20090626
						if (!"Y".equals(chargesDOB.getSelectedFlag()))
							chargeInfo.setDiscountType(dType);
						else
							chargeInfo.setDiscountType(rs
									.getString("MARGIN_TYPE"));
						// ends 170758
						// if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modifed
						// by phani sekhar for wpbn 170758 on 20090626
						if ("A".equalsIgnoreCase(chargeInfo.getDiscountType()))
							sellRate = rs.getDouble("SELLRATE")
									- rs.getDouble("MARGINVALUE");
						// else
						// if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modifed
						// by phani sekhar for wpbn 170758 on 20090626
						else if ("P".equalsIgnoreCase(chargeInfo
								.getDiscountType()))
							sellRate = rs.getDouble("SELLRATE")
									- (rs.getDouble("SELLRATE")
											* rs.getDouble("MARGINVALUE") / 100);
						else if (chargeInfo.getMarginType() == null
								|| "".equals(chargeInfo.getMarginType()))
							sellRate = rs.getDouble("SELLRATE");
					}

					chargeInfo.setSellRate(sellRate);
					chargeInfo.setSelectedFlag(rs.getString("SELECTED_FLAG"));
					chargeInfo.setTieSellRateValue(sellRate);

				}
			}
			// @@Now putting the Delivery Charge to the end of the list, if it
			// exists.
			if (delChargesDOB != null)
				destChargesList.add(delChargesDOB);
			// @@
			// Added by Mohan for Issue No.219976 on 04-11-2010
			if (masterDOB.getOperation() != null
					&& "Modify".equals(masterDOB.getOperation())) {
				notesPstmt = connection.prepareStatement(notesQry);
				notesPstmt.setLong(1, masterDOB.getUniqueId());
				notesRs = notesPstmt.executeQuery();
				// Modified by Mohan on 30102010
				String[] inotes = null;
				String[] enotes = null;
				ArrayList iNotes = new ArrayList();
				ArrayList eNotes = new ArrayList();
				int notesCount = 0;

				while (notesRs.next()) {
					iNotes
							.add((notesRs.getString("INTERNAL_NOTES") != null) ? notesRs
									.getString("INTERNAL_NOTES")
									: "");
					eNotes
							.add((notesRs.getString("EXTERNAL_NOTES") != null) ? notesRs
									.getString("EXTERNAL_NOTES")
									: "");
				}
				notesCount = iNotes.size();

				inotes = new String[notesCount];
				enotes = new String[notesCount];

				for (int i = 0; i < notesCount; i++) {
					inotes[i] = (String) iNotes.get(i);
					enotes[i] = (String) eNotes.get(i);
				}
				finalDOB.setInternalNotes(inotes);// Modified by Mohan on
													// 30102010
				finalDOB.setExternalNotes(enotes);// Modified by Mohan on
													// 30102010
			}
			finalDOB.setOriginChargesList(originChargesList);
			finalDOB.setDestChargesList(destChargesList);
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closePreparedStatement(pstmt);// Added by Dilip for PMD Correction on 22/09/2015
				ConnectionUtil.closeConnection(connection, csmt, rs);
				ConnectionUtil.closePreparedStatement(notesPstmt, notesRs);// Added
																			// by
																			// Mohan
																			// for
																			// Issue
																			// No.219976
																			// on
																			// 04-11-2010

			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
		return finalDOB;
	}

	/**
	 * This method helps in getting the sell rate details between a specified
	 * origin and destination ,for a specified servicelevel and shipment mode
	 * 
	 * @param origin
	 * @param destination
	 * @param serviceLevelId
	 * @param shipmentMode
	 * 
	 * @exception Exception
	 */
	/*
	 * public QuoteFinalDOB getCartages(QuoteFinalDOB finalDOB) throws
	 * SQLException { Connection connection = null; CallableStatement csmt =
	 * null; ResultSet rs = null; ResultSet zoneRs = null; ArrayList
	 * originChargesList = null;//to maintain the list of all origin charge dobs
	 * ArrayList destChargesList = null;//to maintain the list of all origin
	 * charge dobs QuoteCharges cartageChargesDOB = null;//to maintain one
	 * record that is to be displayed QuoteChargeInfo chargeInfo = null;
	 * ArrayList chargeInfoList = null; String flag = null; QuoteMasterDOB
	 * masterDOB = null; double sellRate = 0; ArrayList pickUpQuoteCartageRates =
	 * null; ArrayList deliveryQuoteCartageRates = null; HashMap
	 * pickUpZoneZipMap = new HashMap();//Added by Sanjay HashMap
	 * deliveryZoneZipMap = new HashMap();//Added by Sanjay HashMap
	 * pickUpZoneCode = new HashMap();//Added by Sanjay HashMap deliveryZoneCode =
	 * new HashMap();//Added by Sanjay QuoteCartageRates pickQuoteCartageRates =
	 * null;//Added by Sanjay QuoteCartageRates delQuoteCartageRates =
	 * null;//Added by Sanjay
	 * 
	 * try { masterDOB = finalDOB.getMasterDOB(); connection =
	 * this.getConnection(); csmt = connection.prepareCall("{CALL
	 * QMS_QUOTE_PACK.quote_sell_buy_cartages_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
	 * 
	 * csmt.setString(1,masterDOB.getShipperZipCode()!=null?masterDOB.getShipperZipCode():"");
	 * csmt.setString(2,(masterDOB.getShipperZones()!=null?masterDOB.getShipperZones():"").replaceAll(",","~"));
	 * csmt.setString(3,masterDOB.getConsigneeZipCode()!=null?masterDOB.getConsigneeZipCode():"");
	 * csmt.setString(4,(masterDOB.getConsigneeZones()!=null?masterDOB.getConsigneeZones():"").replaceAll(",","~"));
	 * csmt.setString(5,masterDOB.getSalesPersonCode());
	 * csmt.setString(6,masterDOB.getTerminalId());
	 * csmt.setString(7,masterDOB.getBuyRatesPermission());
	 * csmt.setString(8,masterDOB.getOriginLocation());
	 * csmt.setString(9,masterDOB.getDestLocation());
	 * csmt.setDouble(10,finalDOB.getCartageMargin());
	 * csmt.setString(11,masterDOB.getCustomerId());
	 * csmt.setString(12,""+masterDOB.getShipmentMode());
	 * csmt.setString(13,masterDOB.getOperation());
	 * csmt.setString(14,masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":"");
	 * 
	 * csmt.registerOutParameter(15,OracleTypes.CURSOR);
	 * csmt.registerOutParameter(16,OracleTypes.CURSOR);
	 * 
	 * csmt.execute();
	 * 
	 * 
	 * rs = (ResultSet)csmt.getObject(15);
	 * 
	 * originChargesList = finalDOB.getOriginChargesList(); destChargesList =
	 * finalDOB.getDestChargesList();
	 * 
	 * while(rs.next()) { if( (masterDOB.getShipperZipCode()!=null &&
	 * masterDOB.getShipperZipCode().trim().length()!=0)
	 * ||(masterDOB.getShipperZones()!=null &&
	 * masterDOB.getShipperZones().indexOf(",")==-1)
	 * ||(masterDOB.getConsigneeZipCode()!=null &&
	 * masterDOB.getConsigneeZipCode().trim().length()!=0)
	 * ||(masterDOB.getConsigneeZones()!=null &&
	 * masterDOB.getConsigneeZones().indexOf(",")==-1) ) {
	 * if(originChargesList==null) { originChargesList = new ArrayList(); }
	 * if(destChargesList==null) { destChargesList = new ArrayList(); }
	 * 
	 * flag = rs.getString("SEL_BUY_FLAG"); if(cartageChargesDOB!=null &&
	 * (rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getSellChargeId()) ||
	 * rs.getString("SELLCHARGEID").equalsIgnoreCase(cartageChargesDOB.getBuyChargeId()))) {
	 * chargeInfo = new QuoteChargeInfo();
	 * 
	 * chargeInfoList.add(chargeInfo);
	 * 
	 * chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
	 * chargeInfo.setCurrency(rs.getString("CURRENCY"));
	 * chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
	 * chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
	 * chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
	 * //chargeInfo.setMargin(rs.getDouble("MARGINVALUE")); //
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
	 * chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
	 * 
	 * if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) ||
	 * cartageChargesDOB.getMarginDiscountFlag()==null) {
	 * chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * else { chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * 
	 * chargeInfo.setSellRate(sellRate);
	 * 
	 * 
	 *  } else { cartageChargesDOB = new QuoteCharges();
	 * 
	 * //if it is a sell charge/rate if("SC".equalsIgnoreCase(flag)) {
	 * cartageChargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
	 * cartageChargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID")); } else
	 * if("BC".equalsIgnoreCase(flag)) {
	 * cartageChargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID")); }
	 * 
	 * cartageChargesDOB.setSellBuyFlag(flag);
	 * 
	 * cartageChargesDOB.setChargeDescriptionId(rs.getString("CHARGEDESCID"));
	 * cartageChargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
	 * cartageChargesDOB.setTerminalId(rs.getString("TERMINALID"));
	 * cartageChargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));
	 * cartageChargesDOB.setMarginDiscountFlag(rs.getString("RATE_INDICATOR"));
	 * chargeInfoList = new ArrayList(); chargeInfo = new QuoteChargeInfo();
	 * chargeInfoList.add(chargeInfo);
	 * 
	 * cartageChargesDOB.setChargeInfoList(chargeInfoList);
	 * 
	 * chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
	 * chargeInfo.setCurrency(rs.getString("CURRENCY"));
	 * chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
	 * chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
	 * chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE")); //
	 * chargeInfo.setMargin(rs.getDouble("MARGINVALUE")); //
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
	 * chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
	 * 
	 * if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag()) ||
	 * cartageChargesDOB.getMarginDiscountFlag()==null) {
	 * chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * else { chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * 
	 * chargeInfo.setSellRate(sellRate);
	 * 
	 * if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT"))) {
	 * if((masterDOB.getShipperZipCode()!=null &&
	 * masterDOB.getShipperZipCode().trim().length()!=0)||(masterDOB.getShipperZones()!=null &&
	 * masterDOB.getShipperZones().indexOf(",")==-1)) {
	 * originChargesList.add(cartageChargesDOB); } } else
	 * if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT"))) {
	 * if((masterDOB.getConsigneeZipCode()!=null &&
	 * masterDOB.getConsigneeZipCode().trim().length()!=0)||(masterDOB.getConsigneeZones()!=null &&
	 * masterDOB.getConsigneeZones().indexOf(",")==-1)) {
	 * destChargesList.add(cartageChargesDOB); } } } } else {//Written by Sanjay
	 * for Cartage Charges //get the pickup and delivery charges as seperate
	 * entities as required for the annexure HashMap charge = null;
	 * if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT"))) {
	 * if(pickUpZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID"))) {
	 * if(rs.getString("CHARGESLAB")!=null &&
	 * rs.getString("SEL_BUY_FLAG").equalsIgnoreCase("SC")) {
	 * pickQuoteCartageRates =
	 * (QuoteCartageRates)pickUpZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
	 * charge = pickQuoteCartageRates.getRates();
	 * charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
	 * pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates); } }
	 * else { pickQuoteCartageRates = new QuoteCartageRates();
	 * pickQuoteCartageRates.setZone(rs.getString("ZONE"));
	 * pickQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID")); charge =
	 * new HashMap(); if(rs.getString("CHARGESLAB")!=null &&
	 * rs.getString("SEL_BUY_FLAG").equalsIgnoreCase("SC")){
	 * charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
	 * pickQuoteCartageRates.setRates(charge);
	 * pickUpZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),pickQuoteCartageRates); } } }
	 * else {
	 * if(deliveryZoneCode.containsKey(rs.getString("ZONE")+rs.getString("SELLCHARGEID"))) {
	 * if(rs.getString("CHARGESLAB")!=null &&
	 * rs.getString("SEL_BUY_FLAG").equalsIgnoreCase("SC")) {
	 * delQuoteCartageRates =
	 * (QuoteCartageRates)deliveryZoneCode.get(rs.getString("ZONE")+rs.getString("SELLCHARGEID"));
	 * charge = delQuoteCartageRates.getRates();
	 * charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
	 * deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates); } }
	 * else { delQuoteCartageRates = new QuoteCartageRates();
	 * delQuoteCartageRates.setZone(rs.getString("ZONE"));
	 * delQuoteCartageRates.setCartageId(rs.getString("SELLCHARGEID")); charge =
	 * new HashMap(); if(rs.getString("CHARGESLAB")!=null &&
	 * rs.getString("SEL_BUY_FLAG").equalsIgnoreCase("SC")) {
	 * charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
	 * delQuoteCartageRates.setRates(charge);
	 * deliveryZoneCode.put(rs.getString("ZONE")+rs.getString("SELLCHARGEID"),delQuoteCartageRates); } } }
	 * }//End }
	 * 
	 * if(originChargesList!=null || destChargesList!=null) {
	 * if(originChargesList!=null)
	 * finalDOB.setOriginChargesList(originChargesList);
	 * if(destChargesList!=null) finalDOB.setDestChargesList(destChargesList); }
	 * if(deliveryZoneCode.values()!=null) { deliveryQuoteCartageRates = new
	 * ArrayList(); deliveryQuoteCartageRates.addAll(deliveryZoneCode.values()); }
	 * if(pickUpZoneCode.values()!=null) { pickUpQuoteCartageRates = new
	 * ArrayList(); pickUpQuoteCartageRates.addAll(pickUpZoneCode.values()); }
	 * if(deliveryQuoteCartageRates!=null)
	 * finalDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
	 * if(pickUpQuoteCartageRates!=null)
	 * finalDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates); //The below
	 * code is to get the ZONE-ZIPCODE Mapping from the CURSOR from 15
	 * OUTPARAMETER
	 * 
	 * zoneRs = (ResultSet)csmt.getObject(16); ArrayList delivZipCodes = null;
	 * ArrayList pickUpZipCodes = null;
	 * 
	 * while(zoneRs.next()) {
	 * if("Pickup".equalsIgnoreCase(zoneRs.getString("charge_type"))) { String
	 * from_toZip = null;
	 * if(pickUpZoneZipMap.containsKey(zoneRs.getString("ZONE"))) {
	 * pickUpZipCodes = (ArrayList)
	 * pickUpZoneZipMap.get(zoneRs.getString("ZONE"));
	 * if(zoneRs.getString("ALPHANUMERIC")!=null) from_toZip =
	 * zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode"); else
	 * from_toZip = zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("to_zipcode"); if(from_toZip!=null)
	 * pickUpZipCodes.add(from_toZip);
	 * pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes); } else {
	 * pickUpZipCodes = new ArrayList();
	 * if(zoneRs.getString("from_zipcode")!=null) {
	 * if(zoneRs.getString("ALPHANUMERIC")!=null) from_toZip =
	 * zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode"); else
	 * from_toZip = zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("to_zipcode"); if(from_toZip!=null)
	 * pickUpZipCodes.add(from_toZip);
	 * pickUpZoneZipMap.put(zoneRs.getString("ZONE"),pickUpZipCodes); } } } else
	 * if("Delivery".equalsIgnoreCase(zoneRs.getString("charge_type"))) { String
	 * from_toZip = null;
	 * if(deliveryZoneZipMap.containsKey(zoneRs.getString("ZONE"))) {
	 * delivZipCodes = (ArrayList)
	 * deliveryZoneZipMap.get(zoneRs.getString("ZONE"));
	 * if(zoneRs.getString("ALPHANUMERIC")!=null) from_toZip =
	 * zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode"); else
	 * from_toZip = zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("to_zipcode"); if(from_toZip!=null)
	 * delivZipCodes.add(from_toZip);
	 * deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes); } else {
	 * delivZipCodes = new ArrayList();
	 * if(zoneRs.getString("ALPHANUMERIC")!=null) from_toZip =
	 * zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("ALPHANUMERIC")+zoneRs.getString("to_zipcode"); else
	 * from_toZip = zoneRs.getString("from_zipcode")+" -
	 * "+zoneRs.getString("to_zipcode"); if(from_toZip!=null)
	 * delivZipCodes.add(from_toZip);
	 * deliveryZoneZipMap.put(zoneRs.getString("ZONE"),delivZipCodes); } } }
	 * finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
	 * finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap); //End
	 *  } catch(SQLException sqEx) { sqEx.printStackTrace();
	 * Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] ->
	 * "+sqEx.toString()); throw new SQLException(sqEx.toString()); }
	 * catch(Exception e) { e.printStackTrace();
	 * Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] ->
	 * "+e.toString()); throw new SQLException(e.toString()); } finally { try {
	 * ConnectionUtil.closeConnection(connection,csmt,rs); } catch(Exception ex) {
	 * Logger.error(FILE_NAME,"Finally :
	 * QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]->
	 * "+ex.toString()); throw new SQLException(ex.toString()); } } return
	 * finalDOB; }
	 */

	/**
	 * This method helps in getting the sell rate details between a specified
	 * origin and destination ,for a specified servicelevel and shipment mode
	 * 
	 * @param origin
	 * @param destination
	 * @param serviceLevelId
	 * @param shipmentMode
	 * 
	 * @exception Exception
	 */
	public QuoteFinalDOB getCartages(QuoteFinalDOB finalDOB)
			throws SQLException {
		Connection connection = null;
		CallableStatement csmt = null;
		ResultSet rs = null;
		ResultSet zoneRs = null;
		ResultSet pickWeightBreaksRS = null;
		ResultSet delWeightBreaksRS = null;
		/*
		 * ResultSet pickWeightBreaksRSList = null; ResultSet
		 * delWeightBreaksRSList = null;
		 */
		ArrayList originChargesList = null;// to maintain the list of all
											// origin charge dobs
		ArrayList temporiginChargesList = new ArrayList();// to maintain the
															// list of all
															// origin charge
															// dobs
		ArrayList destChargesList = null;// to maintain the list of all
											// origin charge dobs
		QuoteCharges cartageChargesDOB = null;// to maintain one record that
												// is to be displayed
		QuoteCharges cartageDelChargesDOB = null;// to maintain one record
													// that is to be displayed
		QuoteChargeInfo chargeInfo = null;
		ArrayList chargeInfoList = null;
		String flag = null;
		String pickWeightBreak = null;
		String pickRateType = null;
		String delWeightBreak = null;
		String delRateType = null;
		QuoteMasterDOB masterDOB = null;
		double sellRate = 0;
		ArrayList pickUpQuoteCartageRates = new ArrayList();
		ArrayList deliveryQuoteCartageRates = new ArrayList();
		ArrayList pickupWeightBreaksList = new ArrayList();
		ArrayList delWeightBreaksList = new ArrayList();
		HashMap pickUpZoneZipMap = new HashMap();// Added by Sanjay
		HashMap deliveryZoneZipMap = new HashMap();// Added by Sanjay
		HashMap pickUpZoneCode = new HashMap();// Added by Sanjay
		HashMap deliveryZoneCode = new HashMap();// Added by Sanjay
		QuoteCartageRates pickQuoteCartageRates = null;// Added by Sanjay
		QuoteCartageRates delQuoteCartageRates = null;// Added by Sanjay
		boolean addToPickupList = true;
		boolean addToDeliveryList = true;
		boolean isPickupMin = false;
		boolean isPickupFlat = false;
		boolean isPickupMax = false;
		boolean isDeliveryMin = false;
		boolean isDeliveryFlat = false;
		boolean isDeliveryMax = false;

		ArrayList pickupChargeBasisList = new ArrayList();
		ArrayList delChargeBasisList = new ArrayList();
		String pickupFlatChargeBasis = "";
		String pickupMinChargeBasis = "";
		String pickupMaxChargeBasis = "";
		String deliveryMinChargeBasis = "";
		String deliveryMaxChargeBasis = "";
		String deliveryFlatChargeBasis = "";

		try {
			// @@for re-initialising all the objects put in session previously.

			finalDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
			finalDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
			finalDOB.setPickupWeightBreaks(pickupWeightBreaksList);
			finalDOB.setDeliveryWeightBreaks(delWeightBreaksList);
			finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
			finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);

			// Added By Kishore For the ChargeBasis in the Annexure PDF on
			// 06-Jun-11
			finalDOB.setPickupChargeBasisList(pickupChargeBasisList);
			finalDOB.setDelChargeBasisList(delChargeBasisList);

			// @@

			masterDOB = finalDOB.getMasterDOB();
			if ("View".equalsIgnoreCase(masterDOB.getOperation())) {
				temporiginChargesList = finalDOB.getOriginChargesList();
				destChargesList = finalDOB.getDestChargesList();
			}
			long start = System.currentTimeMillis();
			connection = this.getConnection();
			connection.setAutoCommit(false);
			csmt = connection
					.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_cartages_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			csmt.setString(1, "");// masterDOB.getShipperZipCode()!=null?masterDOB.getShipperZipCode():""
			csmt.setString(2, (masterDOB.getShipperZones() != null ? masterDOB
					.getShipperZones() : "").replaceAll(",", "~"));
			csmt.setString(3, "");// masterDOB.getConsigneeZipCode()!=null?masterDOB.getConsigneeZipCode():""
			csmt.setString(4,
					(masterDOB.getConsigneeZones() != null ? masterDOB
							.getConsigneeZones() : "").replaceAll(",", "~"));
			csmt.setString(5, masterDOB.getSalesPersonCode());
			csmt.setString(6, masterDOB.getTerminalId());
			csmt.setString(7, masterDOB.getBuyRatesPermission());
			csmt.setString(8, masterDOB.getOriginLocation());
			csmt.setString(9, masterDOB.getDestLocation());
			csmt.setDouble(10, finalDOB.getCartageMargin());
			csmt.setDouble(11, finalDOB.getCartageDiscount());
			csmt.setString(12, masterDOB.getCustomerId());
			csmt.setString(13, "" + masterDOB.getShipmentMode());
			csmt.setString(14, masterDOB.getShipperMode());
			if ("2".equalsIgnoreCase(masterDOB.getShipperMode()))
				csmt.setString(15, masterDOB.getShipperConsoleType());
			else
				csmt.setString(15, "~");

			csmt.setString(16, masterDOB.getConsigneeMode());

			if ("2".equalsIgnoreCase(masterDOB.getConsigneeMode()))
				csmt.setString(17, masterDOB.getConsigneeConsoleType());
			else
				csmt.setString(17, "~");

			csmt.setString(18, masterDOB.getOperation());
			// csmt.setString(19,masterDOB.getQuoteId()!=0?masterDOB.getQuoteId()+"":"");
			// //@@ Commented by subrahmanyam for the enhancement 146971 on
			// 1/12/08
			csmt.setString(19, masterDOB.getQuoteId() != null ? masterDOB
					.getQuoteId()
					+ "" : ""); // @@ Added by subrahmanyam for the enhancement
								// 146971 on 1/12/08

			csmt.registerOutParameter(20, OracleTypes.CURSOR);
			csmt.registerOutParameter(21, OracleTypes.CURSOR);
			csmt.registerOutParameter(22, OracleTypes.CURSOR);// Distinct
																// Pickup Charge
																// slabs
			csmt.registerOutParameter(23, OracleTypes.CURSOR);// Distinct
																// Delivery
																// Charge slabs

			csmt.execute();

			rs = (ResultSet) csmt.getObject(20);
			logger
					.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_cartages_proc) :  "
							+ ((System.currentTimeMillis()) - start)
							+ "   UserId ::"
							+ masterDOB.getUserId()
							+ " Origin :: "
							+ masterDOB.getOriginLocation()
							+ " Destination::"
							+ masterDOB.getDestLocation()
							+ " TerminalId :: " + masterDOB.getTerminalId());
			pickWeightBreaksRS = (ResultSet) csmt.getObject(22);
			delWeightBreaksRS = (ResultSet) csmt.getObject(23);
			/*
			 * delWeightBreaksRS = (ResultSet)csmt.getObject(23);
			 * delWeightBreaksRSList = (ResultSet)csmt.getObject(24);
			 */

			// Commented By Kishore Podili For MultipleZone Codes
			// originChargesList = finalDOB.getOriginChargesList();
			// destChargesList = finalDOB.getDestChargesList();
			while (rs.next()) {
			if(("Y".equalsIgnoreCase(rs.getString("SELECTED_FLAG")) && "View".equalsIgnoreCase(masterDOB.getOperation())) || !"view".equalsIgnoreCase(masterDOB.getOperation())){//modified by silpa.p on 30-06-11 for view problem	
				if ("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT"))) {
					flag = rs.getString("SEL_BUY_FLAG");

					if ((masterDOB.getShipperZipCode() != null && masterDOB
							.getShipperZipCode().trim().length() != 0)
							|| (masterDOB.getShipperZones() != null)) // masterDOB.getShipperZones().indexOf(",")==-1
					// Commented By Kishore for multiple ZoneCodes
					{
						if (originChargesList == null) {
							originChargesList = new ArrayList();
						}

						if (cartageChargesDOB != null
								&& (rs.getString("SELLCHARGEID")
										.equalsIgnoreCase(
												cartageChargesDOB
														.getSellChargeId()) || rs
										.getString("SELLCHARGEID")
										.equalsIgnoreCase(
												cartageChargesDOB
														.getBuyChargeId()))
								&& (rs.getString("ZONE") != null && rs
										.getString("ZONE")
										.equalsIgnoreCase(
												cartageChargesDOB.getZoneCode()))) {
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							chargeInfo
									.setBreakPoint(rs.getString("CHARGESLAB"));

							if (rs.getString("CURRENCY") != null
									&& rs.getString("CURRENCY").trim().length() != 0)
								chargeInfo
										.setCurrency(rs.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs
									.getString("RATE_INDICATOR"));
							pickWeightBreak = rs.getString("WEIGHT_BREAK");
							pickRateType = rs.getString("RATE_TYPE");

							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB"
											.equalsIgnoreCase(pickWeightBreak) && "FLAT"
											.equalsIgnoreCase(pickRateType))
									|| ("BOTH".equalsIgnoreCase(pickRateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) {
								chargeInfo.setBasis("Per Shipment");
							} else {
								if ("LIST".equalsIgnoreCase(rs
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CFT".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else if ("Per CBM".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else
									chargeInfo.setBasis(rs
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
							chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
							chargeInfo.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));
							// if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag())
							// ||
							// cartageChargesDOB.getMarginDiscountFlag()==null)
							if ("BC".equalsIgnoreCase(flag)
									|| "M".equalsIgnoreCase(cartageChargesDOB
											.getMarginDiscountFlag())) {
								chargeInfo.setMargin(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));

								// @@ Commented by subrahmanyam for the
								// Enhancement 154381 on 28/01/09
								/*
								 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
								 */
								// @@ Added by subrahmanyam for the Enhancement
								// 154381 on 28/01/09
								if (rs.getDouble("MARGINVALUE") > 0
										|| "Y".equalsIgnoreCase(rs
												.getString("SELECTED_FLAG"))) {
									if ("A".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ rs.getDouble("MARGINVALUE");
									else if ("P".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ (rs.getDouble("BUYRATE")
														* rs
																.getDouble("MARGINVALUE") / 100);
								} else
									sellRate = rs.getDouble("BUYRATE");
								// @@ Ended by subrahmanyam for the Enhancement
								// 154381 on 28/01/09
							} else if ("SC".equalsIgnoreCase(flag)) {
								chargeInfo.setDiscount(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setTieSellRateValue(sellRate);
							/*
							 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
							 * else
							 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
							 */

						} else {

							cartageChargesDOB = new QuoteCharges();

							// if it is a sell charge/rate
							if ("SC".equalsIgnoreCase(flag)) {
								cartageChargesDOB.setSellChargeId(rs
										.getString("SELLCHARGEID"));
								cartageChargesDOB.setBuyChargeId(rs
										.getString("BUY_CHARGE_ID"));
							} else if ("BC".equalsIgnoreCase(flag)) {
								cartageChargesDOB.setBuyChargeId(rs
										.getString("SELLCHARGEID"));
							}

							cartageChargesDOB.setSellBuyFlag(flag);

							// Added By Kishore for multiple ZoneCodes
							cartageChargesDOB.setZoneCode(rs.getString("ZONE"));
							if (rs.getString("ZONE") != null
									|| !"".equalsIgnoreCase(rs
											.getString("ZONE")))
								cartageChargesDOB
										.setChargeDescriptionId("Zone-"
												+ rs.getString("ZONE")
												+ " Pickup Charges");
							else
								cartageChargesDOB
										.setChargeDescriptionId("Pickup Charges");

							cartageChargesDOB.setCostIncurredAt(rs
									.getString("COST_INCURREDAT"));
							cartageChargesDOB.setTerminalId(rs
									.getString("TERMINALID"));
							cartageChargesDOB.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));
							cartageChargesDOB.setMarginDiscountFlag(rs
									.getString("MARGIN_DISCOUNT_FLAG"));
							chargeInfoList = new ArrayList();
							chargeInfo = new QuoteChargeInfo();
							chargeInfoList.add(chargeInfo);

							cartageChargesDOB.setChargeInfoList(chargeInfoList);

							chargeInfo
									.setBreakPoint(rs.getString("CHARGESLAB"));

							if (rs.getString("CURRENCY") != null
									&& rs.getString("CURRENCY").trim().length() != 0)
								chargeInfo
										.setCurrency(rs.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs
									.getString("RATE_INDICATOR"));
							// chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
							// chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
							// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
							pickWeightBreak = rs.getString("WEIGHT_BREAK");
							pickRateType = rs.getString("RATE_TYPE");
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB"
											.equalsIgnoreCase(pickWeightBreak) && "FLAT"
											.equalsIgnoreCase(pickRateType))
									|| ("BOTH".equalsIgnoreCase(pickRateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) {
								chargeInfo.setBasis("Per Shipment");
							} else {
								if ("LIST".equalsIgnoreCase(rs
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CFT".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else if ("Per CBM".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else
									chargeInfo.setBasis(rs
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
							chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
							chargeInfo.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));
							// if("M".equalsIgnoreCase(cartageChargesDOB.getMarginDiscountFlag())
							// ||
							// cartageChargesDOB.getMarginDiscountFlag()==null)
							if ("BC".equalsIgnoreCase(flag)
									|| "M".equalsIgnoreCase(cartageChargesDOB
											.getMarginDiscountFlag())) {
								chargeInfo.setMargin(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));

								// @@ Commented by subrahmanyam for the
								// Enhancement 154381 on 28/01/09
								/*
								 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
								 */
								// @@ Added by subrahmanyam for the Enhancement
								// 154381 on 28/01/09
								if (rs.getDouble("MARGINVALUE") > 0
										|| "Y".equalsIgnoreCase(rs
												.getString("SELECTED_FLAG"))) {
									if ("A".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ rs.getDouble("MARGINVALUE");
									else if ("P".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ (rs.getDouble("BUYRATE")
														* rs
																.getDouble("MARGINVALUE") / 100);
								} else
									sellRate = rs.getDouble("BUYRATE");
								// @@ Ended by subrahmanyam for the Enhancement
								// 154381 on 28/01/09
							} else {
								chargeInfo.setDiscount(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setTieSellRateValue(sellRate);
							  // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
				            if(rs.getString("margin_test_flag")!=null)
				            {
				              if(rs.getString("margin_test_flag").equals("Y"))
				                chargeInfo.setMarginTestFailed(true);
				              else 
				                chargeInfo.setMarginTestFailed(false);
				            }
				            else
				              chargeInfo.setMarginTestFailed(false);
							// if("Pickup".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
							// {

							if ((masterDOB.getShipperZipCode() != null && masterDOB
									.getShipperZipCode().trim().length() != 0)
									|| (masterDOB.getShipperZones() != null)) // &&
																				// masterDOB.getShipperZones().indexOf(",")==-1))
							// Commented By Kishore for multiple ZoneCodes
							{
								originChargesList.add(cartageChargesDOB);
							}
							// }
						}
					}
					if (masterDOB.getShipperZones().trim().length() > 0)
					// Commented By Kishore for multiple ZoneCodes
					{// Written by Sanjay for Cartage Charges
						// get the pickup and delivery charges as seperate
						// entities as required for the annexure
						HashMap charge = null;
						  // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
			            if(rs.getString("margin_test_flag")!=null)
			            {
			              if(rs.getString("margin_test_flag").equals("Y"))
			                chargeInfo.setMarginTestFailed(true);
			              else 
			                chargeInfo.setMarginTestFailed(false);
			            }
			            else
			              chargeInfo.setMarginTestFailed(false);
						if ("Pickup".equalsIgnoreCase(rs
								.getString("COST_INCURREDAT"))) {
							if (pickUpZoneCode.containsKey(rs.getString("ZONE")
									+ rs.getString("SELLCHARGEID"))) {
								if (rs.getString("CHARGESLAB") != null) {
									if ("MIN".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										//isPickupFlat = true;
										  pickupMinChargeBasis = rs.getString("CHARGEBASIS");
									}
									if ("FLAT".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isPickupFlat = true;
										pickupFlatChargeBasis =  rs.getString("CHARGEBASIS");
									}
									if ("MAX".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isPickupMax = true;
										pickupMaxChargeBasis  = rs
												.getString("CHARGEBASIS");
									}

									pickQuoteCartageRates = (QuoteCartageRates) pickUpZoneCode
											.get(rs.getString("ZONE")
													+ rs
															.getString("SELLCHARGEID"));
									charge = pickQuoteCartageRates.getRates();

									if ("SC".equalsIgnoreCase(flag))
										charge.put(rs.getString("CHARGESLAB"),
												rs.getString("SELLRATE"));
									//else
										//charge.put(rs.getString("CHARGESLAB"),
										//		rs.getString("BUYRATE"));
								else{
									if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
					                	 sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
					                else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
					                	sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
									else
					                	sellRate  = rs.getDouble("BUYRATE");
					                     charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
										//charge.put(rs.getString("CHARGESLAB"),
												//rs.getString("BUYRATE"));
								}

									pickUpZoneCode.put(rs.getString("ZONE")
											+ rs.getString("SELLCHARGEID"),
											pickQuoteCartageRates);
								}
								addToPickupList = false;
							} else {
								pickQuoteCartageRates = new QuoteCartageRates();
								pickQuoteCartageRates.setZone(rs
										.getString("ZONE"));
								pickQuoteCartageRates.setCurrency(rs
										.getString("CURRENCY"));
								pickQuoteCartageRates.setCartageId(rs
										.getString("SELLCHARGEID"));
								pickWeightBreak = rs.getString("WEIGHT_BREAK");
								charge = new HashMap();
								if (rs.getString("CHARGESLAB") != null) {
									if ("MIN".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isPickupMin = true;
										pickupMinChargeBasis = rs
												.getString("CHARGEBASIS");
									}
									if ("SC".equalsIgnoreCase(flag))
										charge.put(rs.getString("CHARGESLAB"),
												rs.getString("SELLRATE"));
									//else
									//	charge.put(rs.getString("CHARGESLAB"),
										//		rs.getString("BUYRATE"));
									else{
										if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
						                	 sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
						                else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
						                	sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
									else
						                	sellRate  = rs.getDouble("BUYRATE");
						                     charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
											//charge.put(rs.getString("CHARGESLAB"),
													//rs.getString("BUYRATE"));
									}
									// charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
									pickQuoteCartageRates.setRates(charge);
									pickUpZoneCode.put(rs.getString("ZONE")
											+ rs.getString("SELLCHARGEID"),
											pickQuoteCartageRates);
								}
								addToPickupList = true;
							}

							if (addToPickupList)
								pickUpQuoteCartageRates
										.add(pickQuoteCartageRates);
						}
					}// End
				}
				if ("Delivery"
						.equalsIgnoreCase(rs.getString("COST_INCURREDAT"))) {
					flag = rs.getString("SEL_BUY_FLAG");

					if ((masterDOB.getConsigneeZipCode() != null && masterDOB
							.getConsigneeZipCode().trim().length() != 0)
							|| (masterDOB.getConsigneeZones() != null)) // &&
																		// masterDOB.getConsigneeZones().indexOf(",")==-1)
					{ // Commented By Kishore for multiple ZoneCodes

						if (destChargesList == null) {
							destChargesList = new ArrayList();
						}

						// Modified By Kishore Podili
						if (cartageDelChargesDOB != null
								&& (rs.getString("SELLCHARGEID")
										.equalsIgnoreCase(
												cartageDelChargesDOB
														.getSellChargeId()) || rs
										.getString("SELLCHARGEID")
										.equalsIgnoreCase(
												cartageDelChargesDOB
														.getBuyChargeId()))
								&& (rs.getString("ZONE") != null && rs
										.getString("ZONE").equalsIgnoreCase(
												cartageDelChargesDOB
														.getZoneCode()))) {
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							chargeInfo
									.setBreakPoint(rs.getString("CHARGESLAB"));
							if (rs.getString("CURRENCY") != null
									&& rs.getString("CURRENCY").trim().length() != 0)
								chargeInfo
										.setCurrency(rs.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs
									.getString("RATE_INDICATOR"));
							delWeightBreak = rs.getString("WEIGHT_BREAK");
							delRateType = rs.getString("RATE_TYPE");
							// chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
							// chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
							// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT"
											.equalsIgnoreCase(delRateType))
									|| ("BOTH".equalsIgnoreCase(delRateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) {
								chargeInfo.setBasis("Per Shipment");
							} else {
								if ("LIST".equalsIgnoreCase(rs
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CFT".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else if ("Per CBM".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else
									chargeInfo.setBasis(rs
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
							chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
							chargeInfo.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));
							// if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag())
							// ||
							// cartageDelChargesDOB.getMarginDiscountFlag()==null)
							if ("BC".equalsIgnoreCase(flag)
									|| "M"
											.equalsIgnoreCase(cartageDelChargesDOB
													.getMarginDiscountFlag())) {
								chargeInfo.setMargin(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));

								// @@ Commented by subrahmanyam for the
								// enhancement 154381 on 30/01/09
								/*
								 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
								 */
								// @@ Added by subrahmanyam for the Enhancement
								// 154381 on 30/01/09
								if (rs.getDouble("MARGINVALUE") > 0
										|| "Y".equalsIgnoreCase(rs
												.getString("SELECTED_FLAG"))) {
									if ("A".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ rs.getDouble("MARGINVALUE");
									else if ("P".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ (rs.getDouble("BUYRATE")
														* rs
																.getDouble("MARGINVALUE") / 100);
								} else
									sellRate = rs.getDouble("BUYRATE");
								// @@ Ended by subrahmanyam for the Enhancement
								// 154381 on 30/01/09
							} else {
								chargeInfo.setDiscount(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setTieSellRateValue(sellRate);
							/*
							 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
							 * else
							 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
							 */

						} else {
							cartageDelChargesDOB = new QuoteCharges();

							// if it is a sell charge/rate
							if ("SC".equalsIgnoreCase(flag)) {
								cartageDelChargesDOB.setSellChargeId(rs
										.getString("SELLCHARGEID"));
								cartageDelChargesDOB.setBuyChargeId(rs
										.getString("BUY_CHARGE_ID"));
							} else if ("BC".equalsIgnoreCase(flag)) {
								cartageDelChargesDOB.setBuyChargeId(rs
										.getString("SELLCHARGEID"));
							}

							cartageDelChargesDOB.setSellBuyFlag(flag);

							// Added By Kishore for multiple ZoneCodes
							cartageDelChargesDOB.setZoneCode(rs
									.getString("ZONE"));
							if (rs.getString("ZONE") != null
									|| !"".equals(rs.getString("ZONE")))
								cartageDelChargesDOB
										.setChargeDescriptionId("Zone-"
												+ rs.getString("ZONE")
												+ " Delivery Charges");
							else
								cartageDelChargesDOB
										.setChargeDescriptionId("Delivery Charges");

							cartageDelChargesDOB.setCostIncurredAt(rs
									.getString("COST_INCURREDAT"));
							cartageDelChargesDOB.setTerminalId(rs
									.getString("TERMINALID"));
							cartageDelChargesDOB.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));
							cartageDelChargesDOB.setMarginDiscountFlag(rs
									.getString("MARGIN_DISCOUNT_FLAG"));
							chargeInfoList = new ArrayList();
							chargeInfo = new QuoteChargeInfo();
							chargeInfoList.add(chargeInfo);
							cartageDelChargesDOB
									.setChargeInfoList(chargeInfoList);

							chargeInfo
									.setBreakPoint(rs.getString("CHARGESLAB"));

							if (rs.getString("CURRENCY") != null
									&& rs.getString("CURRENCY").trim().length() != 0)
								chargeInfo
										.setCurrency(rs.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));

							chargeInfo.setRecOrConSellRrate(rs
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs
									.getString("RATE_INDICATOR"));
							delWeightBreak = rs.getString("WEIGHT_BREAK");
							delRateType = rs.getString("RATE_TYPE");
							// chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
							// chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
							// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(delWeightBreak) && "FLAT"
											.equalsIgnoreCase(delRateType))
									|| ("BOTH".equalsIgnoreCase(delRateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) {
								chargeInfo.setBasis("Per Shipment");
							} else {
								if ("LIST".equalsIgnoreCase(rs
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CFT".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else if ("Per CBM".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else
									chargeInfo.setBasis(rs
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
							chargeInfo.setLineNumber(rs.getInt("LINE_NO"));
							chargeInfo.setSelectedFlag(rs
									.getString("SELECTED_FLAG"));

							// if("M".equalsIgnoreCase(cartageDelChargesDOB.getMarginDiscountFlag())
							// ||
							// cartageDelChargesDOB.getMarginDiscountFlag()==null)
							if ("BC".equalsIgnoreCase(flag)
									|| "M"
											.equalsIgnoreCase(cartageDelChargesDOB
													.getMarginDiscountFlag())) {
								chargeInfo.setMargin(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));

								// @@ Commented by subramanyam for the
								// enhancement 154381 on 05/02/09
								/*
								 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
								 */
								// @@ Added by subrahmanyam for the Enhancement
								// 154381 on 15/02/09
								if (rs.getDouble("MARGINVALUE") > 0
										|| "Y".equalsIgnoreCase(rs
												.getString("SELECTED_FLAG"))) {
									if ("A".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ rs.getDouble("MARGINVALUE");
									else if ("P".equalsIgnoreCase(rs
											.getString("MARGIN_TYPE")))
										sellRate = rs.getDouble("BUYRATE")
												+ (rs.getDouble("BUYRATE")
														* rs
																.getDouble("MARGINVALUE") / 100);
								} else
									sellRate = rs.getDouble("BUYRATE");
								// @@ Ended by subrahmanyam for the Enhancement
								// 154381 on 15/02/09
							} else {
								chargeInfo.setDiscount(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setTieMarginDiscountValue(rs
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setTieSellRateValue(sellRate);
							  // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
				            if(rs.getString("margin_test_flag")!=null)
				            {
				              if(rs.getString("margin_test_flag").equals("Y"))
				                chargeInfo.setMarginTestFailed(true);
				              else 
				                chargeInfo.setMarginTestFailed(false);
				            }
				            else
				              chargeInfo.setMarginTestFailed(false);
							// if("Delivery".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
							// {

							if ((masterDOB.getConsigneeZipCode() != null && masterDOB
									.getConsigneeZipCode().trim().length() != 0)
									|| (masterDOB.getConsigneeZones() != null)) // &&
																				// masterDOB.getConsigneeZones().indexOf(",")==-1))
							{
								// Added By Kishore for multiple ZoneCodes
								destChargesList.add(cartageDelChargesDOB);
							}
							// }
						}
					}
					// Added By Kishore for multiple ZoneCodes
					if (masterDOB.getConsigneeZones().trim().length() > 0) {// Written
																			// by
																			// Sanjay
																			// for
																			// Cartage
																			// Charges
						// get the pickup and delivery charges as seperate
						// entities as required for the annexure
						HashMap charge = null;
						  // Added by kiran.v on 07/09/2011 for Wpbn Issue 266732
			            if(rs.getString("margin_test_flag")!=null)
			            {
			              if(rs.getString("margin_test_flag").equals("Y"))
			                chargeInfo.setMarginTestFailed(true);
			              else 
			                chargeInfo.setMarginTestFailed(false);
			            }
			            else
			              chargeInfo.setMarginTestFailed(false);
						if ("Delivery".equalsIgnoreCase(rs
								.getString("COST_INCURREDAT"))) {
							if (deliveryZoneCode.containsKey(rs
									.getString("ZONE")
									+ rs.getString("SELLCHARGEID"))) {
								if (rs.getString("CHARGESLAB") != null) {
									if ("FLAT".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isDeliveryFlat = true;
										deliveryFlatChargeBasis = rs
												.getString("CHARGEBASIS");
									}
									if ("MAX".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isDeliveryMax = true;
										deliveryMaxChargeBasis = rs
												.getString("CHARGEBASIS");
									}
									delQuoteCartageRates = (QuoteCartageRates) deliveryZoneCode
											.get(rs.getString("ZONE")
													+ rs
															.getString("SELLCHARGEID"));
									charge = delQuoteCartageRates.getRates();
									if ("SC".equalsIgnoreCase(flag))
										charge.put(rs.getString("CHARGESLAB"),
												rs.getString("SELLRATE"));
									else{
										 if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
					                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
					                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
					                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
					                	  else
					                		  sellRate  = rs.getDouble("BUYRATE");
					                      charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
										//charge.put(rs.getString("CHARGESLAB"),
										//		rs.getString("BUYRATE"));
									}
									// charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
									deliveryZoneCode.put(rs.getString("ZONE")
											+ rs.getString("SELLCHARGEID"),
											delQuoteCartageRates);
								}
								addToDeliveryList = false;
							} else {
								delQuoteCartageRates = new QuoteCartageRates();
								delWeightBreak = rs.getString("WEIGHT_BREAK");
								delQuoteCartageRates.setZone(rs
										.getString("ZONE"));
								delQuoteCartageRates.setCurrency(rs
										.getString("CURRENCY"));
								delQuoteCartageRates.setCartageId(rs
										.getString("SELLCHARGEID"));
								charge = new HashMap();
								if (rs.getString("CHARGESLAB") != null) {
									if ("MIN".equalsIgnoreCase(rs
											.getString("CHARGESLAB"))) {
										isDeliveryMin = true;
										deliveryMinChargeBasis = rs
												.getString("CHARGEBASIS");
									}
									if ("SC".equalsIgnoreCase(flag))
										charge.put(rs.getString("CHARGESLAB"),
												rs.getString("SELLRATE"));
									else{
										 if("P".equalsIgnoreCase(rs.getString("margin_type"))&& !"0".equalsIgnoreCase(rs.getString("marginvalue")))
					                		  sellRate  = rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
					                	  else if("A".equalsIgnoreCase(rs.getString("margin_type"))) 
					                		  sellRate  = rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); 
					                	  else
					                		  sellRate  = rs.getDouble("BUYRATE");
					                      charge.put(rs.getString("CHARGESLAB"),Double.toString(sellRate));
										//charge.put(rs.getString("CHARGESLAB"),
												//rs.getString("BUYRATE"));
									}
									// charge.put(rs.getString("CHARGESLAB"),rs.getString("SELLRATE"));
									delQuoteCartageRates.setRates(charge);
									deliveryZoneCode.put(rs.getString("ZONE")
											+ rs.getString("SELLCHARGEID"),
											delQuoteCartageRates);
								}
								addToDeliveryList = true;
							}
							if (addToDeliveryList)
								deliveryQuoteCartageRates
										.add(delQuoteCartageRates);
						}
					}// End
				}
			}
			}

			if (originChargesList != null || destChargesList != null) {
				if (originChargesList != null) {
					if (temporiginChargesList != null
							&& temporiginChargesList.size() > 0) {
						for (Object obj : temporiginChargesList)
							originChargesList.add(obj);
					}
					finalDOB.setOriginChargesList(originChargesList);
				}
				if (destChargesList != null)
					finalDOB.setDestChargesList(destChargesList);
			}
			/*
			 * if(deliveryZoneCode.values()!=null) { deliveryQuoteCartageRates =
			 * new ArrayList();
			 * deliveryQuoteCartageRates.addAll(deliveryZoneCode.values()); }
			 * if(pickUpZoneCode.values()!=null) { pickUpQuoteCartageRates = new
			 * ArrayList();
			 * pickUpQuoteCartageRates.addAll(pickUpZoneCode.values()); }
			 */
			if (deliveryQuoteCartageRates != null
					&& deliveryQuoteCartageRates.size() > 0) {
				// delWeightBreaksRS = (ResultSet)csmt.getObject(23);

				if (isDeliveryMin) {
					delWeightBreaksList.add("MIN");
					delChargeBasisList.add(deliveryMinChargeBasis);
				}

				if (isDeliveryFlat) {
					delWeightBreaksList.add("FLAT");
					delChargeBasisList.add(deliveryFlatChargeBasis);
				}
				while (delWeightBreaksRS.next()) {

					//@@Added by Kameswari for the WPBN issue - 265671 on 25/08/2011
					
					if ("BASE".equalsIgnoreCase(delWeightBreaksRS
							.getString("CHARGESLAB"))
							
							|| ("SLAB".equalsIgnoreCase(delWeightBreaksRS
									.getString("Weight_Break")) && "FLAT"
									.equalsIgnoreCase(delWeightBreaksRS
											.getString("rate_type")))
							|| ("BOTH".equalsIgnoreCase(delWeightBreaksRS
									.getString("rate_type")) && "F"
									.equalsIgnoreCase(delWeightBreaksRS
											.getString("RATE_INDICATOR")))) {
						delWeightBreaksList.add(delWeightBreaksRS
								.getString("CHARGESLAB"));
						delChargeBasisList.add("Per Shipment");
			
					} else {
						if ("LIST".equalsIgnoreCase(delWeightBreaksRS
								.getString("WEIGHT_BREAK"))) {
							delWeightBreaksList.add(delWeightBreaksRS
									.getString("CHARGESLAB"));
							delChargeBasisList.add("Per Container");
						
						} else if ("Per Kg".equalsIgnoreCase(delWeightBreaksRS
								.getString("CHARGEBASIS")) || "Per Kilogram".equalsIgnoreCase(delWeightBreaksRS
										.getString("CHARGEBASIS"))) {
							delWeightBreaksList.add(delWeightBreaksRS
									.getString("CHARGESLAB"));
							delChargeBasisList.add("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(pickWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							delWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							delChargeBasisList.add("Per Pound");
						} else if ("Per CFT".equalsIgnoreCase(delWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							delWeightBreaksList.add(delWeightBreaksRS
									.getString("CHARGESLAB"));
							delChargeBasisList.add("Per Cubic Feet");
					
						} else if ("Per CBM".equalsIgnoreCase(delWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							delWeightBreaksList.add(delWeightBreaksRS
									.getString("CHARGESLAB"));
							delChargeBasisList.add("Per Cubic Meter");
				
						} else{
					delWeightBreaksList.add(delWeightBreaksRS
							.getString("CHARGESLAB"));
					delChargeBasisList.add(delWeightBreaksRS
									.getString("CHARGEBASIS"));
							//@@WPBN issue - 265671 on 25/08/2011
						}
					}
					 // Added By Kishore
														// Podili for
														// ChargeBasis in
														// Cartage PDF
			// Added By Kishore
														// Podili for
														// ChargeBasis in
														// Cartage PDF
				}

				// delWeightBreaksRSList = (ResultSet)csmt.getObject(24);

				/*
				 * while(delWeightBreaksRSList.next()) {
				 * delWeightBreaksList.add(delWeightBreaksRSList.getString("CHARGESLAB")); }
				 */

				if (isDeliveryMax) {
					delWeightBreaksList.add("MAX");
					delChargeBasisList.add(deliveryMaxChargeBasis);
				}

				finalDOB.setDeliveryCartageRatesList(deliveryQuoteCartageRates);
				finalDOB.setDeliveryWeightBreaks(delWeightBreaksList);
				finalDOB.setDelChargeBasisList(delChargeBasisList);
			}
			if (pickUpQuoteCartageRates != null
					&& pickUpQuoteCartageRates.size() > 0) {
				// pickWeightBreaksRS = (ResultSet)csmt.getObject(21);
				// pickWeightBreaksRSList =
				if (isPickupMin) {
					pickupWeightBreaksList.add("MIN");
					pickupChargeBasisList.add(pickupMinChargeBasis);
				}

				if (isPickupFlat) {
					pickupWeightBreaksList.add("FLAT");
					pickupChargeBasisList.add(pickupFlatChargeBasis);
				}

				while (pickWeightBreaksRS.next()) {
					
					//@@Added by Kameswari for the WPBN issue - 265671 on 25/08/2011
					if ("BASE".equalsIgnoreCase(pickWeightBreaksRS
							.getString("CHARGESLAB"))
							|| ("SLAB".equalsIgnoreCase(pickWeightBreaksRS
									.getString("Weight_Break")) && "FLAT"
									.equalsIgnoreCase(pickWeightBreaksRS
											.getString("rate_type")))
							|| ("BOTH".equalsIgnoreCase(pickWeightBreaksRS
									.getString("rate_type")) && "F"
									.equalsIgnoreCase(pickWeightBreaksRS
											.getString("RATE_INDICATOR")))) {
						pickupWeightBreaksList.add(pickWeightBreaksRS
								.getString("CHARGESLAB"));
						pickupChargeBasisList.add("Per Shipment");
			
					} else {
						if ("LIST".equalsIgnoreCase(pickWeightBreaksRS
								.getString("WEIGHT_BREAK"))) {
							pickupWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							pickupChargeBasisList.add("Per Container");
						
						} else if ("Per Kg".equalsIgnoreCase(pickWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							pickupWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							pickupChargeBasisList.add("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(pickWeightBreaksRS //@@ ADded by govind for the issue 267834
								.getString("CHARGEBASIS"))) {
							pickupWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							pickupChargeBasisList.add("Per Pound");
						} else if ("Per CFT".equalsIgnoreCase(pickWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							pickupWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							pickupChargeBasisList.add("Per Cubic Feet");
					
						} else if ("Per CBM".equalsIgnoreCase(pickWeightBreaksRS
								.getString("CHARGEBASIS"))) {
							pickupWeightBreaksList.add(pickWeightBreaksRS
									.getString("CHARGESLAB"));
							pickupChargeBasisList.add("Per Cubic Meter");
				
						} else{
					pickupWeightBreaksList.add(pickWeightBreaksRS
							.getString("CHARGESLAB"));
					pickupChargeBasisList.add(pickWeightBreaksRS
									.getString("CHARGEBASIS"));
						}
						//@@WPBN issue - 265671 on 25/08/2011
					}
					 // Added By Kishore
														// Podili for
														// ChargeBasis in
														// Cartage PDF
				}

				// pickWeightBreaksRSList = (ResultSet)csmt.getObject(22);

				/*
				 * while(pickWeightBreaksRSList.next()) {
				 * pickupWeightBreaksList.add(pickWeightBreaksRSList.getString("CHARGESLAB")); }
				 */

				if (isPickupMax) {
					pickupWeightBreaksList.add("MAX");
					pickupChargeBasisList.add(pickupMaxChargeBasis);
				}

				finalDOB.setPickUpCartageRatesList(pickUpQuoteCartageRates);
				finalDOB.setPickupWeightBreaks(pickupWeightBreaksList);
				finalDOB.setPickupChargeBasisList(pickupChargeBasisList);
			}

			// The below code is to get the ZONE-ZIPCODE Mapping from the CURSOR
			// from 20 OUTPARAMETER

			zoneRs = (ResultSet) csmt.getObject(21);
			ArrayList delivZipCodes = null;
			ArrayList pickUpZipCodes = null;

			while (zoneRs.next()) {
				if ("Pickup".equalsIgnoreCase(zoneRs.getString("charge_type"))) {
					// if( !((masterDOB.getShipperZipCode()!=null &&
					// masterDOB.getShipperZipCode().trim().length()!=0)))
					// {
					String from_toZip = null;
					if (pickUpZoneZipMap.containsKey(zoneRs.getString("ZONE"))) {
						pickUpZipCodes = (ArrayList) pickUpZoneZipMap
								.get(zoneRs.getString("ZONE"));
						if (zoneRs.getString("ALPHANUMERIC") != null)
							from_toZip = zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("from_zipcode") + " - "
									+ zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("to_zipcode");
						else
							from_toZip = zoneRs.getString("from_zipcode")
									+ " - " + zoneRs.getString("to_zipcode");
						if (from_toZip != null)
							pickUpZipCodes.add(from_toZip);
						pickUpZoneZipMap.put(zoneRs.getString("ZONE"),
								pickUpZipCodes);
					} else {
						pickUpZipCodes = new ArrayList();
						if (zoneRs.getString("from_zipcode") != null) {
							if (zoneRs.getString("ALPHANUMERIC") != null)
								from_toZip = zoneRs.getString("ALPHANUMERIC")
										+ zoneRs.getString("from_zipcode")
										+ " - "
										+ zoneRs.getString("ALPHANUMERIC")
										+ zoneRs.getString("to_zipcode");
							else
								from_toZip = zoneRs.getString("from_zipcode")
										+ " - "
										+ zoneRs.getString("to_zipcode");
							if (from_toZip != null)
								pickUpZipCodes.add(from_toZip);
							pickUpZoneZipMap.put(zoneRs.getString("ZONE"),
									pickUpZipCodes);
						}
					}
					// }
				} else if ("Delivery".equalsIgnoreCase(zoneRs
						.getString("charge_type"))) {
					// Commented By Kishore Podili For Multiple Zone Codes:
					// 236286
					// if(!((masterDOB.getConsigneeZipCode()!=null &&
					// masterDOB.getConsigneeZipCode().trim().length()!=0)))
					// {
					String from_toZip = null;
					if (deliveryZoneZipMap
							.containsKey(zoneRs.getString("ZONE"))) {
						delivZipCodes = (ArrayList) deliveryZoneZipMap
								.get(zoneRs.getString("ZONE"));
						if (zoneRs.getString("ALPHANUMERIC") != null)
							from_toZip = zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("from_zipcode") + " - "
									+ zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("to_zipcode");
						else
							from_toZip = zoneRs.getString("from_zipcode")
									+ " - " + zoneRs.getString("to_zipcode");
						if (from_toZip != null)
							delivZipCodes.add(from_toZip);
						deliveryZoneZipMap.put(zoneRs.getString("ZONE"),
								delivZipCodes);
					} else {
						delivZipCodes = new ArrayList();
						if (zoneRs.getString("ALPHANUMERIC") != null)
							from_toZip = zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("from_zipcode") + " - "
									+ zoneRs.getString("ALPHANUMERIC")
									+ zoneRs.getString("to_zipcode");
						else
							from_toZip = zoneRs.getString("from_zipcode")
									+ " - " + zoneRs.getString("to_zipcode");
						if (from_toZip != null)
							delivZipCodes.add(from_toZip);
						deliveryZoneZipMap.put(zoneRs.getString("ZONE"),
								delivZipCodes);
					}
					// }
				}
			}
			finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
			finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);
			// End

		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getCartages(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				if (zoneRs != null)
					zoneRs.close();
				if (pickWeightBreaksRS != null)
					pickWeightBreaksRS.close();
				if (delWeightBreaksRS != null)
					delWeightBreaksRS.close();
				if (rs != null)
					rs.close();
				if (csmt != null)
					csmt.close();
				if (connection != null)
					connection.close();
				// ConnectionUtil.closeConnection(connection,csmt,rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
		return finalDOB;
	}

	/**
	 * This method helps in getting the sell rate details between a specified
	 * origin and destination ,for a specified servicelevel and shipment mode
	 * 
	 * @param origin
	 * @param destination
	 * @param serviceLevelId
	 * @param shipmentMode
	 * 
	 * @exception Exception
	 */
	public QuoteFreightLegSellRates getLegRates(QuoteMasterDOB masterDOB,
			QuoteFreightRSRCSRDOB ratesDOB, String legOrigin, String legDest,
			String sellBuyRateId, String buyRateId, String laneNo,
			String sellBuyFlag, double marginLimit, String shipmentMode,
			String currency, String densityRatio, String[] container)
			throws SQLException {
		Connection connection = null;
		CallableStatement csmt = null;
		ResultSet rs = null;
		ArrayList freightChargesList = null;// to maintain the list of all
											// origin charge dobs
		QuoteFreightLegSellRates legChargesDetails = null;// to maintain the
															// charges info of
															// each leg
		QuoteCharges chargesDOB = null;// to maintain one record that is to be
										// displayed
		QuoteChargeInfo chargeInfo = null;
		ArrayList chargeInfoList = null;
		String flag = null;
		String weightBreak = null;
		String rateType = null;
        String temp_charge_desc = null;
		double sellRate = 0;
		int i = 0;
		String breakPoint = null;
		// added by phani sekhar for wpbn 170758 on 20090626
		String mType = null;
		String dType = null;
		String terminalFlag = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		String query = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID IN(SELECT RG.PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN RG WHERE RG.CHILD_TERMINAL_ID = ?)";
		String query1 = "SELECT STM.OPER_ADMIN_FLAG FROM Fs_Fr_Terminalmaster STM WHERE STM.TERMINALID =? ";
		String query2 = "SELECT MARGIN_TYPE,DISCOUNT_TYPE FROM FS_FR_TERMINALMASTER TM WHERE TM.TERMINALID =? ";

		// ends 170758
		try {
			connection = this.getConnection();
			long start = System.currentTimeMillis();
			// added by phani sekhar for wpbn 170758 on 20090626
			pstmt1 = connection.prepareStatement(query1);
			pstmt1.setString(1, masterDOB.getQuotingStation());
			rs = pstmt1.executeQuery();
			if (rs.next()) {
				terminalFlag = rs.getString("OPER_ADMIN_FLAG");
			}
			/*
			 * ConnectionUtil.closePreparedStatement(pstmt1,rs);
			 * if(!"H".equals(terminalFlag)) {
			 */
			if ("A".equals(terminalFlag) || "H".equals(terminalFlag))
				pstmt = connection.prepareStatement(query2);
			else
				pstmt = connection.prepareStatement(query);
			// pstmt.setString(1,masterDOB.getQuotingStation());
			pstmt.setString(1, masterDOB.getTerminalId());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				mType = rs.getString("MARGIN_TYPE");
				dType = rs.getString("DISCOUNT_TYPE");
			}
			ConnectionUtil.closePreparedStatement(pstmt, rs);
			// }
			// ENDS 170758
			connection.setAutoCommit(false);
			// csmt = connection.prepareCall("{CALL
			// QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			csmt = connection
					.prepareCall("{CALL QMS_QUOTE_PACK.quote_sell_buy_ratesdtl_proc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");

			csmt.setString(1, legOrigin);
			csmt.setString(2, legDest);
			csmt.setString(3, sellBuyRateId);
			csmt.setString(4, buyRateId);
			csmt.setString(5, laneNo);
			csmt.setString(6, masterDOB.getSalesPersonCode());
			csmt.setString(7, masterDOB.getTerminalId());
			csmt.setString(8, masterDOB.getBuyRatesPermission());
			csmt.setString(9, sellBuyFlag);
			csmt.setDouble(10, marginLimit);
			csmt.setString(11, masterDOB.getOperation());
			csmt.setString(12, "" + masterDOB.getQuoteId());
			csmt.setString(13, masterDOB.getCustomerId());
			csmt.setString(14, masterDOB.getOriginLocation());
			csmt.setString(15, masterDOB.getDestLocation());
			// csmt.set
			if ("SBR".equalsIgnoreCase(sellBuyFlag)) {
				csmt.setNull(16, Types.VARCHAR);
				csmt.setNull(17, Types.VARCHAR);
				csmt.setNull(18, Types.VARCHAR);
				csmt.setNull(19, Types.VARCHAR);
				csmt.setNull(20, Types.VARCHAR);
			} else {
				csmt.setString(16, ratesDOB.getCarrierId());
				csmt.setString(17, ratesDOB.getServiceLevelId());
				csmt.setString(18, ratesDOB.getFrequency());
				csmt.setString(19, ratesDOB.getCreatedTerminalId());
				csmt.setString(20, shipmentMode);
			}
			csmt.registerOutParameter(21, OracleTypes.CURSOR);

			csmt.execute();

			rs = (ResultSet) csmt.getObject(21);
			logger
					.info("Time Taken for DB procedure in milli seconds for 3rd screen (quote_sell_buy_ratesdtl_proc) :  "
							+ ((System.currentTimeMillis()) - start)
							+ "    UserId ::"
							+ masterDOB.getUserId()
							+ " Origin :: "
							+ masterDOB.getOriginLocation()
							+ " Destination::"
							+ masterDOB.getDestLocation()
							+ " TerminalId :: " + masterDOB.getTerminalId());
			while (rs.next()) {
				// added by VLAKSHMI for issue 146968 on 5/12/2008
				int count = 0;
				if (!"SBR".equalsIgnoreCase(sellBuyFlag)
						&& "LIST".equalsIgnoreCase(ratesDOB
								.getWeightBreakType())
						&& "FCL".equalsIgnoreCase(ratesDOB.getConsoleType())
						&& container != null) {
					int containerLen = container.length;
					for (int k = 0; k < containerLen; k++) {
						if (container[k] != null
								&& container[k].trim().length() > 0
								&& (rs.getString("CHARGESLAB")
										.startsWith(container[k]))) {

							count++;
							break;
						}

					}
				} else {
					count = 1;
				}// end of for issue 146968 on 5/12/2008
				flag = rs.getString("SEL_BUY_FLAG");

				if (freightChargesList == null) 
					freightChargesList = new ArrayList();
		

				// added by VLAKSHMI for issue 146968 on 5/12/2008
				if (count > 0) {
					if (chargesDOB != null
			&& ((rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getSellChargeId()) || rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getBuyChargeId()))
			 )/*&& (rs.getString("RATE_DESCRIPTION").equalsIgnoreCase(temp_charge_desc))*/) {
						chargeInfo = new QuoteChargeInfo();
						chargeInfoList.add(chargeInfo);

						if ("KG".equalsIgnoreCase(rs.getString("CHARGESLAB"))
								&& "FUEL SURCHARGE".equalsIgnoreCase(rs
										.getString("RATE_DESCRIPTION")))
							chargeInfo.setBreakPoint("FSKG");
						else if ("KG".equalsIgnoreCase(rs
								.getString("CHARGESLAB"))
								&& "SECURITY SURCHARGE".equalsIgnoreCase(rs
										.getString("RATE_DESCRIPTION")))
							chargeInfo.setBreakPoint("SSKG");
						else if ("FMIN".equalsIgnoreCase(rs
								.getString("CHARGESLAB"))
								&& "SECURITY SURCHARGE".equalsIgnoreCase(rs
										.getString("RATE_DESCRIPTION")))
							chargeInfo.setBreakPoint("SSMIN");
						else if ("BASIC".equalsIgnoreCase(rs
								.getString("CHARGESLAB"))
								&& "SECURITY SURCHARGE".equalsIgnoreCase(rs
										.getString("RATE_DESCRIPTION")))
							chargeInfo.setBreakPoint("SSBASIC");
						else

							chargeInfo
									.setBreakPoint(rs.getString("CHARGESLAB"));
						if (rs.getString("CURRENCY") != null
								&& rs.getString("CURRENCY").trim().length() != 0)
							chargeInfo.setCurrency(rs.getString("CURRENCY"));
						else
							chargeInfo.setCurrency(currency);// @@In Case of
																// Spot Rates

						chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
						chargeInfo.setRecOrConSellRrate(rs
								.getDouble("SELLRATE"));
						// chargeInfo.setSellRate(rs.getDouble("SELLRATE"));
						chargeInfo.setSellChargeMargin(rs
								.getDouble("MARGINVALUE"));
						chargeInfo.setSellChargeMarginType(rs
								.getString("MARGIN_TYPE"));

						chargeInfo.setRateIndicator(rs
								.getString("RATE_INDICATOR"));
						if ("A FREIGHT RATE".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("FREIGHT RATE");
						else if ("C.P.S.S".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("P.S.S");
						else
							chargeInfo.setRateDescription(rs
									.getString("RATE_DESCRIPTION"));// @@Added
																	// by
																	// Kameswari
																	// for
																	// enhancements
						// if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag())
						// || chargesDOB.getMarginDiscountFlag()==null)
						if ("BR".equalsIgnoreCase(flag)
								|| "SBR".equalsIgnoreCase(flag)
								|| "M".equalsIgnoreCase(chargesDOB
										.getMarginDiscountFlag())) {

							chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
							chargeInfo.setTieMarginDiscountValue(rs
									.getDouble("MARGINVALUE"));
							// modified by phani sekhar for wpbn 170758 on
							// 20090626
							if (!"Y".equals(chargesDOB.getSelectedFlag()))
								chargeInfo.setMarginType(mType);
							else
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));
							// ends 170758

							// @@ Commented by subrahmanyam for the Enhancement
							// 154381 on 28/01/09
							/*
							 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
							 * else
							 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
							 */
							// @@ Added by subrahmanyam for the Enhancement
							// 154381 on 28/01/09
							if (rs.getDouble("MARGINVALUE") > 0
									|| "Y".equalsIgnoreCase(rs
											.getString("SELECTED_FLAG"))) {
								// if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modified
								// by phani sekhar for wpbn 170758 on 20090626
								if ("A".equalsIgnoreCase(chargeInfo
										.getMarginType()))
									sellRate = rs.getDouble("BUYRATE")
											+ rs.getDouble("MARGINVALUE");
								// else
								// if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))//modified
								// by phani sekhar for wpbn 170758 on 20090626
								else if ("P".equalsIgnoreCase(chargeInfo
										.getMarginType()))// modified by phani
															// sekhar for wpbn
															// 170758 on
															// 20090626
									sellRate = rs.getDouble("BUYRATE")
											+ (rs.getDouble("BUYRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							} else
								sellRate = rs.getDouble("BUYRATE");
							// @@ Endded by subrahmanyam for the Enhancement
							// 154381 on 28/01/09

						} else if ("RSR".equalsIgnoreCase(flag)
								|| "CSR".equalsIgnoreCase(flag)) {
							chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
							chargeInfo.setTieMarginDiscountValue(rs
									.getDouble("MARGINVALUE"));
							// modified by phani sekhar for wpbn 170758 on
							// 20090626
							if (!"Y".equals(chargesDOB.getSelectedFlag()))
								chargeInfo.setDiscountType(dType);
							else
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));
							// ends 170758

							if ("A".equalsIgnoreCase(chargeInfo
									.getDiscountType())) // modified by phani
															// sekhar for wpbn
															// 170758 on
															// 20090626
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(chargeInfo
									.getDiscountType())) // modified by phani
															// sekhar for wpbn
															// 170758 on
															// 20090626
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}

						chargeInfo.setSellRate(sellRate);
						chargeInfo.setTieSellRateValue(sellRate);
						chargeInfo.setSelectedFlag(rs
								.getString("SELECTED_FLAG"));
						weightBreak = rs.getString("WEIGHT_BREAK");
						rateType = rs.getString("RATE_TYPE");
						// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
						/*
						 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * ("SLAB".equalsIgnoreCase(weightBreak) &&
						 * "FLAT".equalsIgnoreCase(rateType)) ||
						 * ("BOTH".equalsIgnoreCase(rateType) &&
						 * "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) {
						 * chargeInfo.setBasis("Per Shipment"); }
						 */
						if ("BASIC"
								.equalsIgnoreCase(chargeInfo.getBreakPoint())
								|| "BASE".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| "MIN".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| "MAX".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"FSBASIC")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSBASIC")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"FSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"CAFMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"BAFMIN")
								|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
										.equalsIgnoreCase(rateType))
								|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
										.equalsIgnoreCase(chargeInfo
												.getRateIndicator()))
								|| chargeInfo.getBreakPoint().endsWith("BASIC")
								|| chargeInfo.getBreakPoint().endsWith("MIN")) // MODIFIED
																				// FOR
																				// 183812
						{

							chargeInfo.setBasis("Per Shipment");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"FSKG")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSKG")
								|| ("1".equalsIgnoreCase(shipmentMode) && chargeInfo
										.getBreakPoint().endsWith("FLAT"))
								|| isNumber(chargeInfo.getBreakPoint())) {
							chargeInfo.setBasis("Per Kilogram");
						} else if (chargeInfo.getBreakPoint().length() > 5
								&& "LF".equalsIgnoreCase(chargeInfo
										.getBreakPoint().substring(3, 5))
								|| (chargeInfo.getBreakPoint() != null && chargeInfo
										.getBreakPoint().toUpperCase().endsWith("PERCENT"))// Added by govind for the issue 262475
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"CAF%")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SURCHARGE")) {

							chargeInfo.setBasis("Percent of Freight");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"BAFM3")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSM3")
								|| ("2".equalsIgnoreCase(shipmentMode) && chargeInfo
										.getBreakPoint().endsWith("FLAT"))) {
							// chargeInfo.setBasis("per Cubic Meter");
							chargeInfo.setBasis("Per Weight Measurement");

						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"CSF")
								|| chargeInfo.getBreakPoint().length() > 5
								&& ("PF".equalsIgnoreCase(chargeInfo
										.getBreakPoint().substring(3, 5)) && !"lIST"
										.equalsIgnoreCase(weightBreak))) {
							chargeInfo.setBasis("Per Shipment");
						}

						// @@Surcharge Enhancements
						else {
							if ("1".equalsIgnoreCase(shipmentMode)
									&& "LIST".equalsIgnoreCase(rs
											.getString("WEIGHT_BREAK"))) {
								chargeInfo.setBasis("Per ULD");
							} else if (("2".equalsIgnoreCase(shipmentMode) || "4"
									.equalsIgnoreCase(shipmentMode))
									&& "LIST".equalsIgnoreCase(rs
											.getString("WEIGHT_BREAK"))) {

								// if(chargeInfo.getBreakPoint().endsWith("CAF")
								// @@Modified by Kameswari for the WPBN issue -
								// on 16/12/08
								if (chargeInfo.getBreakPoint().endsWith("CAF")
										|| chargeInfo.getBreakPoint().endsWith(
												"caf") || ( chargeInfo.getRateDescription()!= null && !"".equals(chargeInfo.getRateDescription())&& chargeInfo.getRateDescription().contains("Currency Adjustment Factor"))) {
									chargeInfo.setBasis("Percent of Freight");
								} else {
									chargeInfo.setBasis("Per Container");
									
								}
							}

							else if ("Per Kg".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Kilogram");
							} else if ("Per Lb".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Pound");
							} else if ("Per CFT".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Feet");
							} else if ("Per CBM".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Meter");
							} else
								chargeInfo
										.setBasis(rs.getString("CHARGEBASIS"));
						}
						chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
						// chargeInfo.setRatio(densityRatio);
						chargeInfo.setLineNumber(rs.getInt("LINE_NO"));

					} else {
						chargesDOB = new QuoteCharges();
						temp_charge_desc = rs.getString("RATE_DESCRIPTION");
						if ("Carrier".equalsIgnoreCase(rs
								.getString("COST_INCURREDAT")))
							freightChargesList.add(chargesDOB);
						chargesDOB.setSellBuyFlag(flag);
						// if it is a sell charge/rate
						if ("RSR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())
								|| "CSR".equalsIgnoreCase(chargesDOB
										.getSellBuyFlag())) {
							chargesDOB.setSellChargeId(rs
									.getString("SELLCHARGEID"));
							chargesDOB.setBuyChargeId(rs
									.getString("BUY_CHARGE_ID"));
						} else {
							chargesDOB.setBuyChargeId(rs
									.getString("SELLCHARGEID"));
						}

						// in case of freight rates
						chargesDOB.setVersionNo(rs.getString("VERSION_NO"));// @@Added
																			// for
																			// the
																			// WPBN
																			// issues-146448,146968
																			// on
																			// 18/12/08
						chargesDOB.setBuyChargeLaneNo(rs.getString("LANE_NO"));
						chargesDOB.setTerminalId(rs.getString("TERMINALID"));
						chargesDOB.setMarginDiscountFlag(rs
								.getString("MARGIN_DISCOUNT_FLAG"));

						chargesDOB.setChargeDescriptionId(rs
								.getString("CHARGEDESCID"));
						// @@Added by Kameswari for the WPBN issue-146448 on
						// 01/12/08

						chargesDOB.setFrequencyChecked(rs
								.getString("FREQUENCY_CHECKED"));
						chargesDOB.setTransitTimeChecked(rs
								.getString("TRANSITTIME_CHECKED"));
						chargesDOB.setCarrierChecked(rs
								.getString("CARRIER_CHECKED"));
						chargesDOB.setRateValidityChecked(rs
								.getString("RATEVALIDITY_CHECKED"));
						chargesDOB.setServicelevel(rs.getString("SRV_LEVEL"));
						chargesDOB.setFrequency(rs.getString("FREQUENCY"));
						chargesDOB.setTransitTime(rs.getString("TRANSITTIME"));
						chargesDOB.setCarrier(rs.getString("CARRIER"));

						chargesDOB.setValidUpto(rs.getTimestamp("VALIDUPTO"));
						// @@WPBN issue-146448
						chargesDOB.setSelectedFlag(rs.getString("SELECTED_FLAG"));// @@Added by
																// Kameswari for
																// the WPBN
																// issue-143250
						chargesDOB.setCostIncurredAt(rs
								.getString("COST_INCURREDAT"));
						chargesDOB.setConsoleType(rs.getString("CONSOLE_TYPE"));
						// chargesDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));
						chargeInfoList = new ArrayList();
						chargeInfo = new QuoteChargeInfo();
						chargeInfoList.add(chargeInfo);

						chargesDOB.setChargeInfoList(chargeInfoList);

						/*
						 * if("KG".equalsIgnoreCase(rs.getString("CHARGESLAB"))&&"FUEL
						 * SURCHARGE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setBreakPoint("FSKG"); else
						 * if("KG".equalsIgnoreCase(rs.getString("CHARGESLAB"))&&"SECURITY
						 * SURCHARGE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setBreakPoint("SSKG"); else
						 * if("FMIN".equalsIgnoreCase(rs.getString("CHARGESLAB"))&&"SECURITY
						 * SURCHARGE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setBreakPoint("SSMIN"); else
						 * if("BASIC".equalsIgnoreCase(rs.getString("CHARGESLAB"))&&"SECURITY
						 * SURCHARGE".equalsIgnoreCase(rs.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setBreakPoint("SSBASIC"); else
						 */
						chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));

						if (rs.getString("CURRENCY") != null
								&& rs.getString("CURRENCY").trim().length() != 0)
							chargeInfo.setCurrency(rs.getString("CURRENCY"));
						else
							chargeInfo.setCurrency(currency);// @@In Case of
																// Spot Rates

						chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
						chargeInfo.setRecOrConSellRrate(rs
								.getDouble("SELLRATE"));
						// chargeInfo.setSellRate(rs.getDouble("SELLRATE"));
						chargeInfo.setSellChargeMargin(rs
								.getDouble("MARGINVALUE"));
						chargeInfo.setSellChargeMarginType(rs
								.getString("MARGIN_TYPE"));
						chargeInfo.setRateIndicator(rs
								.getString("RATE_INDICATOR"));

						if ("A FREIGHT RATE".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("FREIGHT RATE");
						else if ("C.P.S.S".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("P.S.S");
						else
							chargeInfo.setRateDescription(rs
									.getString("RATE_DESCRIPTION"));// @@Added
																	// by
																	// Kameswari
																	// for
																	// enhancements
						// chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
						// chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
						// chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
						weightBreak = rs.getString("WEIGHT_BREAK");
						rateType = rs.getString("RATE_TYPE");
						/*
						 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * ("SLAB".equalsIgnoreCase(weightBreak) &&
						 * "FLAT".equalsIgnoreCase(rateType)) ||
						 * ("BOTH".equalsIgnoreCase(rateType) &&
						 * "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) {
						 * chargeInfo.setBasis("Per Shipment"); }
						 */
						if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
								|| "MIN".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| "MAX".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"FSBASIC")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSBASIC")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"FSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"CAFMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"BAFMIN")
								|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
										.equalsIgnoreCase(rateType))
								|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
										.equalsIgnoreCase(chargeInfo
												.getRateIndicator()))
								|| "BASIC".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| chargeInfo.getBreakPoint().endsWith("MIN")
								|| chargeInfo.getBreakPoint().endsWith("BASIC")) // MODIFIED
																					// FOR
																					// 183812
						{

							chargeInfo.setBasis("Per Shipment");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"FSKG")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSKG")
								|| chargeInfo.getBreakPoint().endsWith("FLAT")
								|| isNumber(chargeInfo.getBreakPoint())) {
							chargeInfo.setBasis("Per Kilogram");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"CAF%")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SURCHARGE")
								|| ("2".equalsIgnoreCase(shipmentMode)
										&& chargeInfo.getBreakPoint().length() > 5 && "PF"
										.equalsIgnoreCase(chargeInfo
												.getBreakPoint()
												.substring(3, 5)))) {

							chargeInfo.setBasis("Percent of Freight");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"BAFM3")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSM3")) {
							// chargeInfo.setBasis("per Cubic Meter");
							chargeInfo.setBasis("Per Weight Measurement");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"CSF")) {
							chargeInfo.setBasis("Per Shipment");
						}
						// @@Surcharge Enhancements
						else {
							if ("1".equalsIgnoreCase(shipmentMode)
									&& "LIST".equalsIgnoreCase(rs
											.getString("WEIGHT_BREAK"))) {
								chargeInfo.setBasis("Per ULD");
							} else if (("2".equalsIgnoreCase(shipmentMode) || "4"
									.equalsIgnoreCase(shipmentMode))
									&& "LIST".equalsIgnoreCase(rs
											.getString("WEIGHT_BREAK"))) {

								// if(chargeInfo.getBreakPoint().endsWith("CAF")
								// @@Modified by Kameswari for the WPBN issue -
								// on 16/12/08
								if (chargeInfo.getBreakPoint().endsWith("CAF")
										|| chargeInfo.getBreakPoint().endsWith(
												"caf")|| ( chargeInfo.getRateDescription()!= null && !"".equals(chargeInfo.getRateDescription())&& chargeInfo.getRateDescription().contains("Currency Adjustment Factor"))) {
									chargeInfo.setBasis("Percent of Freight");
								} else {
									chargeInfo.setBasis("Per Container");
								}
							} else if ("Per Kg".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Kilogram");
							} else if ("Per Lb".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Pound");
							} else if ("Per CFT".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Feet");
							} else if ("Per CBM".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Meter");
							} else
								chargeInfo
										.setBasis(rs.getString("CHARGEBASIS"));
						}
						chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
						// chargeInfo.setRatio(densityRatio);
						chargeInfo.setLineNumber(rs.getInt("LINE_NO"));

						// if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag())
						// || chargesDOB.getMarginDiscountFlag()==null)
						if ("BR".equalsIgnoreCase(flag)
								|| "SBR".equalsIgnoreCase(flag)
								|| "M".equalsIgnoreCase(chargesDOB
										.getMarginDiscountFlag())) {
							chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
							chargeInfo.setTieMarginDiscountValue(rs
									.getDouble("MARGINVALUE"));
							// added by phani sekhar for wpbn 170758 on 20090626
							if (!"Y".equals(chargesDOB.getSelectedFlag()))
								chargeInfo.setMarginType(mType);
							else
								chargeInfo.setMarginType(rs
										.getString("MARGIN_TYPE"));
							// ends 170758

							// @@ Commented by subrahmanyam for the Enhancement
							// for 154381 on 28/01/09 o
							/*
							 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE");
							 * else
							 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
							 * sellRate =
							 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100);
							 */
							// @@ Added by subrahmanyam for the Enhancement
							// 154381 on 28/01/09
							if (rs.getDouble("MARGINVALUE") > 0
									|| "Y".equalsIgnoreCase(rs
											.getString("SELECTED_FLAG"))) {
								if ("A".equalsIgnoreCase(chargeInfo
										.getMarginType()))// modified by phani
															// sekhar for wpbn
															// 170758 on
															// 20090626
									sellRate = rs.getDouble("BUYRATE")
											+ rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(chargeInfo
										.getMarginType()))// modified by phani
															// sekhar for wpbn
															// 170758 on
															// 20090626
									sellRate = rs.getDouble("BUYRATE")
											+ (rs.getDouble("BUYRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							} else
								sellRate = rs.getDouble("BUYRATE");
							// @@ Ended by subrahmanyam for the Enhancement
							// 154381 on 28/01/09
						} else if ("RSR".equalsIgnoreCase(flag)
								|| "CSR".equalsIgnoreCase(flag)) {
							chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
							chargeInfo.setTieMarginDiscountValue(rs
									.getDouble("MARGINVALUE"));
							// added by phani sekhar for wpbn 170758 on 20090626
							if (!"Y".equals(chargesDOB.getSelectedFlag()))
								chargeInfo.setDiscountType(dType);
							else
								chargeInfo.setDiscountType(rs
										.getString("MARGIN_TYPE"));
							// ends 170758

							if ("A".equalsIgnoreCase(chargeInfo
									.getDiscountType()))// modified by phani
														// sekhar for wpbn
														// 170758 on 20090626
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(chargeInfo
									.getDiscountType()))// modified by phani
														// sekhar for wpbn
														// 170758 on 20090626
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);

						}

						chargeInfo.setSellRate(sellRate);
						chargeInfo.setTieSellRateValue(sellRate);
						chargeInfo.setSelectedFlag(rs
								.getString("SELECTED_FLAG"));

					}
				}
			}

			if (freightChargesList != null) {
				legChargesDetails = new QuoteFreightLegSellRates();
				legChargesDetails.setFreightChargesList(freightChargesList);
								
			}

		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+sqEx.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]
			// -> "+e.toString());
			logger
					.error(FILE_NAME
							+ "QMSQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)] -> "
							+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closePreparedStatement(pstmt);// @@ Added by
																// govind on
																// 15-02-2010
																// for
																// connection
																// leakage
				ConnectionUtil.closePreparedStatement(pstmt1);// @@ Added by
																// govind on
																// 15-02-2010
																// for
																// connection
																// leakages
				ConnectionUtil.closeConnection(connection, csmt, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[getLegCharges(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]->
				// "+ex.toString());
				logger
						.error(FILE_NAME
								+ "Finally : QMSQuoteDAO[getLegRates(masterDOB,sellBuyRate,sellBuyFlag,laneNo,origin,dest)]-> "
								+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
		return legChargesDetails;
	}

	private void insertSelectedRates(QuoteFinalDOB finalDOB,
			Connection connection) throws SQLException {

		PreparedStatement pStmt = null;
		PreparedStatement pStmt1 = null;
		ArrayList legDetails = null;
		ArrayList originChargesList = null;
		ArrayList destChargesList = null;
		ArrayList frtChargesList = null;
		QuoteMasterDOB masterDOB = null;
		QuoteFreightLegSellRates legDOB = null;
		QuoteCharges chargesDOB = null;
		ArrayList chargesList = null;
		QuoteChargeInfo chargeInfoDOB = null;
		String chargeDescriptionId = null;
		String zone_code = null;
		int index = 0;
		int originIndex = 0;
		int destinationIndex = 0;
		int[] originIndices = null;
		int[] destIndices = null;
		int[] freightIndices = null;

		int noOfLegs = 0;
		int size = 0;
		int chargeSize = 0;
		String chargeDescription = null;
		try {
			pStmt = connection.prepareStatement(selectedRatesInsertQuery);

			masterDOB = finalDOB.getMasterDOB();

			originChargesList = finalDOB.getOriginChargesList();
			originIndices = finalDOB.getSelectedOriginChargesListIndices();
			logger.info("userid : " + masterDOB.getCreatedBy());
			if (originIndices != null) {
				size = originIndices.length;
				if (size > 1) {
					logger.info("originIndices[0] : " + originIndices[0]);

					logger.info("originIndices[1] : " + originIndices[1]);
				}
			} else
				size = 0;
			chargeDescription = null;
			for (int i = 0; i < size; i++) {
				/*
				 * if(i>0) { chargesDOB =
				 * (QuoteCharges)originChargesList.get(originIndices[i-1]);
				 * chargeDescription = chargesDOB.getChargeDescriptionId(); }
				 */
				if (originIndices[i] != -1) {
					chargesDOB = (QuoteCharges) originChargesList
							.get(originIndices[i]);
					logger.info("Origin insertSelectedRates::" + i + ":"
							+ chargesDOB); // newly added
					chargesList = chargesDOB.getChargeInfoList();
					chargeSize = chargesList.size();
					if (i <= 1) {
						logger.info("chargesDOB.getChargeId : "
								+ chargesDOB.getChargeId());
						logger.info("chargesDOB.getChargeDescriptionId : "
								+ chargesDOB.getChargeDescriptionId());
						logger.info("chargeSize : " + chargeSize);
					}
					chargeSize = chargesList.size();
					logger.info("chargeSize : " + chargeSize);

					/*
					 * if(chargeDescription!=null&&!(chargeDescription.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))) {
					 */
					for (int j = 0; j < chargeSize; j++) {
						chargeInfoDOB = (QuoteChargeInfo) chargesList.get(j);

						if ("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())
								|| "SC".equalsIgnoreCase(chargesDOB
										.getSellBuyFlag())) {
							zone_code = null;
							chargeDescriptionId = chargesDOB
									.getChargeDescriptionId();
							index = chargeDescriptionId != null ? chargeDescriptionId
									.indexOf("-")
									: -1;
							originIndex = chargeDescriptionId != null
									&& index != -1 ? chargeDescriptionId
									.indexOf("Pickup") : 6;
							zone_code = index != -1 ? chargeDescriptionId
									.substring(5, originIndex - 1).trim() : "";

						}

						pStmt.setLong(1, masterDOB.getUniqueId());
						pStmt.setString(2, chargesDOB.getSellBuyFlag());
						pStmt.setString(3, chargesDOB.getBuyChargeId());
						pStmt.setString(4, chargesDOB.getSellChargeId());
						pStmt.setString(5, chargesDOB.getBuyChargeLaneNo());
						pStmt.setString(6, chargesDOB.getChargeId());
						pStmt.setString(7, chargesDOB.getChargeDescriptionId());
						pStmt.setString(8, chargesDOB.getMarginDiscountFlag());
						pStmt.setString(9, chargeInfoDOB.getMarginType());
						pStmt.setDouble(10, chargeInfoDOB.getMargin());
						pStmt.setString(11, chargeInfoDOB.getDiscountType());
						pStmt.setDouble(12, chargeInfoDOB.getDiscount());
						pStmt.setNull(13, Types.VARCHAR);
						pStmt.setNull(14, Types.INTEGER);
						pStmt.setString(15, chargeInfoDOB.getBreakPoint());
						pStmt.setString(16, chargesDOB.getCostIncurredAt());
						pStmt.setDouble(17, chargeInfoDOB.getBuyRate());
						pStmt.setDouble(18, chargeInfoDOB
								.getRecOrConSellRrate());
						pStmt.setNull(19, Types.INTEGER);
						pStmt.setNull(20, Types.INTEGER);
						pStmt.setInt(21, chargeInfoDOB.getLineNumber());
						// System.out.println("chargeInfoDOB.isMarginTestFailed()----");
						pStmt.setString(22,
								chargeInfoDOB.isMarginTestFailed() ? "Y" : "N");// included
																				// by
																				// shyam
																				// for
																				// DHL
						pStmt.setString(23, zone_code != null
								&& "Pickup".equalsIgnoreCase(chargesDOB
										.getCostIncurredAt()) ? zone_code : "");
						pStmt.addBatch();
					}

					// }
				}
			}
			destChargesList = finalDOB.getDestChargesList();
			destIndices = finalDOB.getSelctedDestChargesListIndices();

			if (destIndices != null) {
				size = destIndices.length;
				/*
				 * if(size>1) { logger.info("destIndices[0] : "+destIndices[0]);
				 * logger.info("destIndices[1] : "+destIndices[1]); }
				 */
			} else
				size = 0;
			chargeDescription = null;

			for (int i = 0; i < size; i++) {

				/*
				 * if(i>0) { chargesDOB =
				 * (QuoteCharges)destChargesList.get(destIndices[i-1]);
				 * chargeDescription = chargesDOB.getChargeDescriptionId(); }
				 */
				if (destIndices[i] != -1) {
					chargesDOB = (QuoteCharges) destChargesList
							.get(destIndices[i]);
					logger.info("Destination insertSelectedRates::" + i + ":"
							+ chargesDOB); // newly added
					chargesList = chargesDOB.getChargeInfoList();
					chargeSize = chargesList.size();

					/*
					 * if(chargeDescription!=null&&!(chargeDescription.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))) {
					 */
					for (int j = 0; j < chargeSize; j++) {

						if ("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())
								|| "SC".equalsIgnoreCase(chargesDOB
										.getSellBuyFlag())) {
							zone_code = null;
							chargeDescriptionId = chargesDOB
									.getChargeDescriptionId();
							index = chargeDescriptionId != null ? chargeDescriptionId
									.indexOf("-")
									: -1;
							destinationIndex = chargeDescriptionId != null
									&& index != -1 ? chargeDescriptionId
									.indexOf("Delivery") : 6;
							zone_code = index != -1 ? chargeDescriptionId
									.substring(5, destinationIndex).trim() : "";

						}
						chargeInfoDOB = (QuoteChargeInfo) chargesList.get(j);
						pStmt.setLong(1, masterDOB.getUniqueId());
						pStmt.setString(2, chargesDOB.getSellBuyFlag());
						pStmt.setString(3, chargesDOB.getBuyChargeId());
						pStmt.setString(4, chargesDOB.getSellChargeId());
						pStmt.setString(5, chargesDOB.getBuyChargeLaneNo());
						pStmt.setString(6, chargesDOB.getChargeId());
						pStmt.setString(7, chargesDOB.getChargeDescriptionId());
						pStmt.setString(8, chargesDOB.getMarginDiscountFlag());
						pStmt.setString(9, chargeInfoDOB.getMarginType());
						pStmt.setDouble(10, chargeInfoDOB.getMargin());
						pStmt.setString(11, chargeInfoDOB.getDiscountType());
						pStmt.setDouble(12, chargeInfoDOB.getDiscount());
						pStmt.setNull(13, Types.VARCHAR);
						pStmt.setNull(14, Types.INTEGER);
						pStmt.setString(15, chargeInfoDOB.getBreakPoint());
						pStmt.setString(16, chargesDOB.getCostIncurredAt());
						pStmt.setDouble(17, chargeInfoDOB.getBuyRate());
						pStmt.setDouble(18, chargeInfoDOB
								.getRecOrConSellRrate());
						pStmt.setNull(19, Types.INTEGER);
						pStmt.setNull(20, Types.INTEGER);
						pStmt.setInt(21, chargeInfoDOB.getLineNumber());
						pStmt.setString(22,
								chargeInfoDOB.isMarginTestFailed() ? "Y" : "N");// included
																				// by
																				// shyam
																				// for
																				// DHL
						pStmt.setString(23, zone_code != null
								&& "Delivery".equalsIgnoreCase(chargesDOB
										.getCostIncurredAt()) ? zone_code : "");
						pStmt.addBatch();
					}
				}
			}

			legDetails = finalDOB.getLegDetails();
			noOfLegs = legDetails.size();
			pStmt1 = connection.prepareStatement(selectedRatesInsertQuery1);
			for (int k = 0; k < noOfLegs; k++) {
				legDOB = (QuoteFreightLegSellRates) legDetails.get(k);
				frtChargesList = legDOB.getFreightChargesList();
				freightIndices = legDOB.getSelectedFreightChargesListIndices();

				if (freightIndices != null)
					size = freightIndices.length;
				else
					size = 0;

				for (int i = 0; i < size; i++) {
					chargesDOB = (QuoteCharges) frtChargesList
							.get(freightIndices[i]);
					chargesList = chargesDOB.getChargeInfoList();
					chargeSize = chargesList.size();

					for (int j = 0; j < chargeSize; j++) {
						chargeInfoDOB = (QuoteChargeInfo) chargesList.get(j);

						pStmt1.setLong(1, masterDOB.getUniqueId());
						pStmt1.setString(2, chargesDOB.getSellBuyFlag());
						if ("SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
							pStmt1.setNull(3, Types.VARCHAR);
						else
							pStmt1.setString(3, chargesDOB.getBuyChargeId());
						pStmt1.setString(4, chargesDOB.getSellChargeId());
						pStmt1.setString(5, chargesDOB.getBuyChargeLaneNo());
						pStmt1.setString(6, chargesDOB.getChargeId());
						// pStmt1.setString(7,chargesDOB.getChargeDescriptionId());
						pStmt1
								.setString(
										7,
										"FREIGHT RATE"
												.equalsIgnoreCase(chargeInfoDOB
														.getRateDescription()) ? "A FREIGHT RATE"
												: chargeInfoDOB
														.getRateDescription());
						pStmt1.setString(8, chargesDOB.getMarginDiscountFlag());
						pStmt1.setString(9, chargeInfoDOB.getMarginType());
						pStmt1.setDouble(10, chargeInfoDOB.getMargin());
						pStmt1.setString(11, chargeInfoDOB.getDiscountType());
						pStmt1.setDouble(12, chargeInfoDOB.getDiscount());
						pStmt1.setNull(13, Types.VARCHAR);
						pStmt1.setNull(14, Types.INTEGER);
						pStmt1.setString(15, chargeInfoDOB.getBreakPoint());
						pStmt1.setString(16, chargesDOB.getCostIncurredAt());
						pStmt1.setDouble(17, chargeInfoDOB.getBuyRate());
						pStmt1.setDouble(18, chargeInfoDOB
								.getRecOrConSellRrate());
						pStmt1.setNull(19, Types.INTEGER);
						pStmt1.setInt(20, (k + 1));
						// pStmt1.setInt(21,chargeInfoDOB.getLineNumber());//
						// commented by VLAKSHMI for issue 146968 on 5/12/2008
						pStmt1.setInt(21, j); // added by VLAKSHMI for issue
												// 146968 on 5/12/2008
						pStmt1.setString(22,
								chargeInfoDOB.isMarginTestFailed() ? "Y" : "N");// included
																				// by
																				// shyam
																				// for
																				// DHL
						// @@Added by Kameswari for the WPBN issue-146448 on
						// 02/12/08

						pStmt1.setString(23, chargesDOB.getServicelevel());
						pStmt1.setString(24, chargesDOB.getFrequency());
						pStmt1.setString(25, chargesDOB.getTransitTime());
						pStmt1.setString(26, chargesDOB.getCarrier());
						pStmt1.setTimestamp(27, chargesDOB.getValidUpto());
						pStmt1.setString(28, chargesDOB.getFrequencyChecked());
						pStmt1
								.setString(29, chargesDOB
										.getTransitTimeChecked());
						pStmt1.setString(30, chargesDOB.getCarrierChecked());
						pStmt1.setString(31, chargesDOB
								.getRateValidityChecked());
						pStmt1.setString(32, chargesDOB.getVersionNo());

						// @@WPBN issue-146448
						pStmt1.addBatch();
					}

				}
			}

			pStmt.executeBatch();
			pStmt1.executeBatch();
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error in insertSelectedRates"+e);
			logger.error(FILE_NAME + "Error in insertSelectedRates" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(null, pStmt);
			ConnectionUtil.closeConnection(null, pStmt1);
		}
	}

	private void insertNotes(Connection connection, QuoteFinalDOB finalDOB)
			throws SQLException {
		PreparedStatement pstmt = null;
		QuoteMasterDOB masterDOB = null;
		try {
			pstmt = connection.prepareStatement(notesInsertQuery);
			masterDOB = finalDOB.getMasterDOB();

			for (int i = 0; i < finalDOB.getExternalNotes().length; i++) {
				pstmt.clearParameters();
				pstmt.setLong(1, masterDOB.getUniqueId());

				if (finalDOB.getInternalNotes()[i] != null
						&& finalDOB.getInternalNotes()[i].trim().length() != 0)
					pstmt.setString(2, finalDOB.getInternalNotes()[i]);
				else
					pstmt.setNull(2, Types.VARCHAR);

				if (finalDOB.getExternalNotes()[i] != null
						&& finalDOB.getExternalNotes()[i].trim().length() != 0)
					pstmt.setString(3, finalDOB.getExternalNotes()[i]);
				else
					pstmt.setNull(3, Types.VARCHAR);

				pstmt.addBatch();
			}
			pstmt.executeBatch();
		} catch (SQLException sql) {
			// Logger.error(FILE_NAM E,"SQLException in insertNotes::"+sql);
			logger.error(FILE_NAME + "SQLException in insertNotes::" + sql);
			sql.printStackTrace();
			throw new SQLException(sql.toString(), sql.getSQLState(), sql
					.getErrorCode());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error in insertNotes::"+e);
			logger.error(FILE_NAME + "Error in insertNotes::" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(null, pstmt, null);
		}
	}

	private void updateSelectedRates(QuoteFinalDOB finalDOB, long id,
			Connection connection) throws SQLException {

		PreparedStatement pStmt = null;

		try {
			pStmt = connection.prepareStatement(selectedRatesDelQry);
			pStmt.clearParameters();
			pStmt.setLong(1, id);
			pStmt.executeUpdate();
			if (pStmt != null)
				pStmt.close();

			insertSelectedRates(finalDOB, connection);
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error in insertSelectedRates"+e);
			logger.error(FILE_NAME + "Error in insertSelectedRates" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(null, pStmt);
		}
	}

	private void updateNotes(Connection connection, long id,
			QuoteFinalDOB finalDOB) throws SQLException {
		PreparedStatement pStmt = null;

		try {
			pStmt = connection.prepareStatement(notesDelQry);

			pStmt.clearParameters();

			pStmt.setLong(1, id);
			pStmt.executeUpdate();

			if (pStmt != null)
				pStmt.close();

			insertNotes(connection, finalDOB);
		} catch (SQLException sql) {
			// Logger.error(FILE_NAME,"SQLException in updateNotes"+sql);
			logger.error(FILE_NAME + "SQLException in updateNotes" + sql);
			sql.printStackTrace();
			throw new SQLException(sql.toString(), sql.getSQLState(), sql
					.getErrorCode());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Exception in updateNotes"+e);
			logger.error(FILE_NAME + "Exception in updateNotes" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(null, pStmt);
		}
	}

	public void setTransactionDetails(QuoteMasterDOB masterDOB) {
		// @@This method does not throw any exception. If any error occurs,
		// system will ignore it.
		OperationsImpl operationsImpl = null;
		try {
			operationsImpl = new OperationsImpl();
			operationsImpl.setTransactionDetails(masterDOB.getTerminalId(),
					masterDOB.getUserId(), "Quote", "" + masterDOB.getQuoteId()
							+ ", Version:" + masterDOB.getVersionNo(),
					new java.sql.Timestamp((new java.util.Date()).getTime()),
					masterDOB.getOperation());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error while setting Transaction
			// Details");
			logger.error(FILE_NAME + "Error while setting Transaction Details");
			e.printStackTrace();
		}
	}

	public void setConfirmFlag(UpdatedQuotesReportDOB reportDOB)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int totalUpdated = 0;
		int confirmedQuotes = -1;
		try {
			conn = this.getConnection();
			// Added by Anil.k for CR 231104 on 03Feb2011
			if (reportDOB != null
					&& !"Checked".equalsIgnoreCase(reportDOB.getDontModify())) {// END
				pstmt = conn
						.prepareStatement("UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG='I' WHERE ID=?");// @@Inactivating
																									// the
																									// Old
																									// Id
				if (reportDOB != null) {
					pstmt.setLong(1, reportDOB.getUniqueId());
					pstmt.executeUpdate();

					if (pstmt != null)
						pstmt.close();
				}
			}// Added by Anil.k for CR 231104 on 03Feb2011

			pstmt = conn
					.prepareStatement("UPDATE QMS_QUOTES_UPDATED SET CONFIRM_FLAG='C' WHERE QUOTE_ID=? AND CHANGEDESC=? AND SELL_BUY_FLAG=?");

			if (reportDOB != null) {
				pstmt.setLong(1, reportDOB.getUniqueId());
				pstmt.setString(2, reportDOB.getChangeDesc());
				pstmt.setString(3, reportDOB.getSellBuyFlag());

				pstmt.executeUpdate();

				if (pstmt != null)
					pstmt.close();
				pstmt = conn
						.prepareStatement("UPDATE QMS_QUOTES_UPDATED SET QUOTE_ID=? WHERE QUOTE_ID = ? AND CONFIRM_FLAG IS NULL"); // COMMENTED
																																	// BY
																																	// GOVIND
																																	// FOR
																																	// THE
																																	// ISSUE
																																	// UPDATES
																																	// FOR
																																	// MULTI-QUOTE
				// pstmt = conn.prepareStatement("UPDATE QMS_QUOTES_UPDATED SET
				// QUOTE_ID=? WHERE QUOTE_ID IN (SELECT ID FROM QMS_QUOTE_MASTER
				// QMS WHERE QMS.QUOTE_ID = (SELECT QM.QUOTE_ID FROM
				// QMS_QUOTE_MASTER QM WHERE QM.ID =?)) AND CONFIRM_FLAG IS
				// NULL");
				pstmt.setLong(1, reportDOB.getNewQuoteId());
				pstmt.setLong(2, reportDOB.getUniqueId());

				pstmt.executeUpdate();

				if (pstmt != null)
					pstmt.close();

				pstmt = conn
						.prepareStatement("SELECT COUNT(*)TOT_UPDATED FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=?");

				pstmt.setString(1, reportDOB.getChangeDesc());
				pstmt.setString(2, reportDOB.getSellBuyFlag());

				rs = pstmt.executeQuery();

				if (rs.next())
					totalUpdated = rs.getInt("TOT_UPDATED");

				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();

				pstmt = conn
						.prepareStatement("SELECT COUNT(*)CONFIRMED FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=? AND CONFIRM_FLAG ='C'");

				pstmt.setString(1, reportDOB.getChangeDesc());
				pstmt.setString(2, reportDOB.getSellBuyFlag());

				rs = pstmt.executeQuery();

				if (rs.next())
					confirmedQuotes = rs.getInt("CONFIRMED");

				if (totalUpdated == confirmedQuotes) {
					if (pstmt != null)
						pstmt.close();

					pstmt = conn
							.prepareStatement("DELETE FROM QMS_QUOTES_UPDATED WHERE CHANGEDESC=? AND SELL_BUY_FLAG=?");
					pstmt.setString(1, reportDOB.getChangeDesc());
					pstmt.setString(2, reportDOB.getSellBuyFlag());
					pstmt.executeUpdate();
				}

			}
		} catch (SQLException sql) {
			// Logger.error(FILE_NAME,"SQLException in setConfirmFlag"+sql);
			logger.error(FILE_NAME + "SQLException in setConfirmFlag" + sql);
			sql.printStackTrace();
			throw new SQLException(sql.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Exception in setConfirmFlag"+e);
			logger.error(FILE_NAME + "Exception in setConfirmFlag" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			ConnectionUtil.closeConnection(conn, pstmt, rs);
		}
	}

	public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB costingHDRDOB,
			ESupplyGlobalParameters loginbean) throws Exception {

		CallableStatement cStmt = null;
		ResultSet rs = null;
		ResultSet rs2 = null;// ,rs3 = null;//Commented By RajKumari on
								// 24-10-2008 for Connection Leakages.

		String weightBreak = null;
		String rateType = null;

		ArrayList legDetails = null;
		ArrayList chargeList = null;
		ArrayList destChargeList = null;
		ArrayList rateList = null;
		ArrayList list_exNotes = null;
		ArrayList contactPersonsIds = new ArrayList();
		ArrayList contactPersonsList = new ArrayList();
		ArrayList contactsMailList = new ArrayList();
		ArrayList contactsFaxList = new ArrayList();

		CostingLegDetailsDOB costingLegDetailsDOB = null;
		CostingChargeDetailsDOB costingChargeDetailsDOB = null;
		CostingChargeDetailsDOB delChargeDetailsDOB = null;
		CostingRateInfoDOB costingRateInfoDOB = null;
		CostingMasterDOB costingMasterDOB = null;

		Connection connection = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;// ,pstmt2 = null;//Commented By
										// RajKumari on 24-10-2008 for
										// Connection Leakages.
		ResultSet rs1 = null;
		String department = "";
		// @@ Added by subrahmanyam for Enhancement 167668 on 29/04/09
		String custAddress1 = null;
		String custAddress2 = null;
		String custAddress3 = null;
		String custAddrQry = null;
		String queryNoCustAddr = null;
		// @@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09
		String selectMailid = " SELECT EMAILID FROM fs_usermaster WHERE USERID=? AND LOCATIONID = ?";

		/*
		 * String selectQuote = "SELECT QM.ORIGIN_LOCATION,(SELECT COUNTRYID
		 * FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.ORIGIN_LOCATION)
		 * ORG_COUNTRY ,QM.DEST_LOCATION, (SELECT COUNTRYID FROM
		 * FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.DEST_LOCATION)
		 * DEST_COUNTRY,QM.INCO_TERMS_ID, " +" (SELECT COMODITYDESCRIPTION FROM
		 * FS_FR_COMODITYMASTER CM WHERE QM.COMMODITY_ID = CM.COMODITYID)
		 * COMODITYDESCRIPTION,QM.CUSTOMER_ID,C.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,QM.VERSION_NO," +"
		 * DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE
		 * RP.QUOTE_ID=QM.QUOTE_ID AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1,
		 * to_char(QM.SHIPMENT_MODE), 'Multi-Mode') SHIPMENTMODE, " +"
		 * C.COMPANYNAME,QM.MODIFIED_DATE,QM.EMAIL_FLAG,QM.FAX_FLAG,QM.PRINT_FLAG
		 * FROM QMS_QUOTE_MASTER QM,FS_FR_CUSTOMERMASTER C,FS_ADDRESS AD " +"
		 * WHERE ID IN " +" (SELECT ID FROM QMS_QUOTE_MASTER WHERE VERSION_NO=" +"
		 * (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) AND
		 * QUOTE_ID=?) AND QM.CUSTOMER_ID=C.CUSTOMERID AND
		 * AD.ADDRESSID=C.CUSTOMERADDRESSID";
		 */

		String selectQuote = "SELECT QM.ORIGIN_LOCATION,(SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.ORIGIN_LOCATION) ORG_COUNTRY ,QM.DEST_LOCATION, (SELECT COUNTRYID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID = QM.DEST_LOCATION) DEST_COUNTRY,QM.INCO_TERMS_ID, "
				+ " (SELECT COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER CM WHERE QM.COMMODITY_ID = CM.COMODITYID) COMODITYDESCRIPTION,QM.CUSTOMER_ID,C.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,QM.VERSION_NO,"
				+ " DECODE((SELECT COUNT(*) FROM FS_RT_LEG LG, FS_RT_PLAN RP WHERE RP.QUOTE_ID=QM.QUOTE_ID  AND LG.RT_PLAN_ID=RP.RT_PLAN_ID), 1, to_char(QM.SHIPMENT_MODE), 'Multi-Mode') SHIPMENTMODE, "
				+ " C.COMPANYNAME,QM.CREATED_TSTMP,QM.EMAIL_FLAG,QM.FAX_FLAG,QM.PRINT_FLAG,QM.EFFECTIVE_DATE,QM.VALID_TO FROM QMS_QUOTE_MASTER QM,FS_FR_CUSTOMERMASTER C,FS_ADDRESS AD "
				+ " WHERE ID IN " // Added by Mohan for issue no.219979 on
									// 10122010
				+ " (SELECT ID FROM QMS_QUOTE_MASTER WHERE VERSION_NO="
				+ " (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND MULTI_QUOTE_LANE_NO=?) AND QUOTE_ID=? AND MULTI_QUOTE_LANE_NO=?) AND QM.CUSTOMER_ID=C.CUSTOMERID AND AD.ADDRESSID=C.CUSTOMERADDRESSID";

		String countryNamesSQL = "SELECT UPPER(COUNTRYNAME) ORIGIN,(SELECT UPPER(COUNTRYNAME) FROM FS_COUNTRYMASTER WHERE COUNTRYID = ?)"
				+ " DEST FROM FS_COUNTRYMASTER WHERE COUNTRYID =? ";
		//@@Modified by kiran.v on 02/08/2011 for Wpbn Issue 262471
		String contactsQuery =     " SELECT DISTINCT CD.SL_NO,CD.CONTACTPERSON,CD.EMAILID,CD.FAX "
                +" FROM QMS_CUST_CONTACTDTL  CD,QMS_QUOTE_CONTACTDTL QC "
				+ " WHERE QC.CUSTOMERID = CD.CUSTOMERID "
                 +" AND QC.SL_NO = CD.SL_NO  AND CD.ACTIVE_STATUS = 'A' "    
                 +" AND QC.QUOTE_ID IN "
                    +"  (SELECT ID "
				+ "                        FROM QMS_QUOTE_MASTER "
				+ "                       WHERE QUOTE_ID = ? "
                       +"   AND ACTIVE_FLAG = 'A' "
                       +"   AND MULTI_QUOTE_LANE_NO = ?) "
                                        
                +" ORDER BY SL_NO ";

		// String contactsMailQuery = "SELECT EMAILID,FAX FROM
		// QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND SL_NO=? ORDER BY SL_NO";

		/*
		 * String creatorDtlsQry = "SELECT
		 * (UM.USERNAME||',\n'||DM.DESCRIPTION)CREATOR FROM "+ "FS_USERMASTER
		 * UM,QMS_DESIGNATION DM WHERE UM.DESIGNATION_ID=DM.DESIGNATION_ID AND
		 * UM.USERID=? AND UM.LOCATIONID=?";
		 */
		/*
		 * String creatorDtlsQry = "SELECT U.USERNAME, U.DEPARTMENT,
		 * U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+ "
		 * FS_USERMASTER U,FS_COMPANYINFO FSC WHERE U.USERID=? AND U.LOCATIONID = ?
		 * AND FSC.COMPANYID =U.COMPANYID";
		 */
		String creatorDtlsQry = "SELECT U.USERNAME, U.DEPARTMENT, U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "
				+ "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE  U.USERID=? AND U.LOCATIONID = ?  AND FSC.COMPANYID =U.COMPANYID";
		/*
		 * String addressQry = "SELECT
		 * (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
		 * "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n'||AD.PHONENO||'\n'||AD.FAX)ADDRESS
		 * FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+ ",FS_COUNTRYMASTER CO
		 * WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID
		 * AND TM.TERMINALID=?";
		 */
		/*
		 * String addressQry = "SELECT
		 * (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
		 * "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS
		 * FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+ ",FS_COUNTRYMASTER CO
		 * WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID
		 * AND TM.TERMINALID=?";
		 */
		/*
		 * String addressQry = "SELECT
		 * (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
		 * "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS
		 * FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+ ",FS_COUNTRYMASTER CO
		 * WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID
		 * AND TM.TERMINALID=?";
		 */

		String addressQry = "SELECT (AD.ADDRESSLINE1||'\n\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"
				+ "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME))ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "
				+ ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
		String fromMailId = null;
		double sellRate = 0.0;
		java.text.DecimalFormat deciFormat = null;

		String customerId = null;
		int sl_no = 0;

		String[] contactsEmailIds = null;
		String[] contactsFax = null;
		String[] contactPersonNames = null;
		String[] contactPersonIds = null;
		int n;
		String breakPoint = null;
		try {
			deciFormat = new java.text.DecimalFormat("##0.00");
			connection = this.getConnection();

			pstmt = connection.prepareStatement(selectMailid);
			pstmt.setString(1, loginbean.getUserId());
			pstmt.setString(2, loginbean.getTerminalId());

			rs1 = pstmt.executeQuery();

			if (rs1.next()) {
				fromMailId = rs1.getString("EMAILID");
			}

			if (rs1 != null) {
				rs1.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}

			pstmt = connection.prepareStatement(selectQuote);

			pstmt.setString(1, costingHDRDOB.getQuoteid());
			pstmt.setString(2, costingHDRDOB.getLaneNo());// Added by Mohan
															// for issue
															// no.219979 on
															// 10122010
			pstmt.setString(3, costingHDRDOB.getQuoteid());
			pstmt.setString(4, costingHDRDOB.getLaneNo());// Added by Mohan
															// for issue
															// no.219979 on
															// 10122010
			rs1 = pstmt.executeQuery();
			if (rs1.next()) {
				costingMasterDOB = new CostingMasterDOB();

				costingMasterDOB.setOrigin(rs1.getString("ORIGIN_LOCATION"));
				costingMasterDOB.setDestination(rs1.getString("DEST_LOCATION"));
				costingMasterDOB.setCustomerid(rs1.getString("CUSTOMER_ID"));
				costingMasterDOB.setIncoterms(rs1.getString("INCO_TERMS_ID"));
				costingMasterDOB.setCommodityType(rs1
						.getString("COMODITYDESCRIPTION"));
				costingMasterDOB.setVersionNo(rs1.getString("VERSION_NO"));
				costingMasterDOB
						.setEmailId(rs1.getString("OPERATIONS_EMAILID"));
				costingMasterDOB.setOrginCountry(rs1.getString("ORG_COUNTRY"));
				costingMasterDOB.setDestCountry(rs1.getString("DEST_COUNTRY"));
				costingMasterDOB.setCustomerName(rs1.getString("COMPANYNAME"));
				// costingMasterDOB.setDateOfQuotation(rs1.getTimestamp("MODIFIED_DATE"));
				costingMasterDOB.setDateOfQuotation(rs1
						.getTimestamp("CREATED_TSTMP"));
				costingMasterDOB.setEffectiveFrom(rs1
						.getTimestamp("EFFECTIVE_DATE"));
				costingMasterDOB.setValidityOfQuote(rs1
						.getTimestamp("VALID_TO")); // Added by Gowtham for
													// Costing Report.
				costingMasterDOB.setEmailFlag(rs1.getString("EMAIL_FLAG"));
				costingMasterDOB.setFaxFlag(rs1.getString("FAX_FLAG"));
				costingMasterDOB.setPrintFlag(rs1.getString("PRINT_FLAG"));
				costingMasterDOB.setCustomerFax(rs1.getString("FAX"));
				costingMasterDOB.setCustomerCountryId(rs1
						.getString("COUNTRYID"));

				if (rs1.getString("SHIPMENTMODE").equalsIgnoreCase("1"))
					costingMasterDOB.setShipmentMode("AIR");
				else if (rs1.getString("SHIPMENTMODE").equalsIgnoreCase("2"))
					costingMasterDOB.setShipmentMode("SEA");
				else if (rs1.getString("SHIPMENTMODE").equalsIgnoreCase("4"))
					costingMasterDOB.setShipmentMode("TRUCK");
				else
					costingMasterDOB.setShipmentMode(rs1
							.getString("SHIPMENTMODE"));

				costingMasterDOB.setFromMailId(fromMailId);

				pstmt1 = connection.prepareStatement(countryNamesSQL);

				pstmt1.setString(1, costingMasterDOB.getDestCountry());
				pstmt1.setString(2, costingMasterDOB.getOrginCountry());

				rs2 = pstmt1.executeQuery();

				if (rs2.next()) {
					costingMasterDOB.setOriginCountryName(rs2
							.getString("ORIGIN"));
					costingMasterDOB.setDestCountryName(rs2.getString("DEST"));
				}
				if (rs2 != null)
					rs2.close();
				if (pstmt1 != null)
					pstmt1.close();

				pstmt1 = connection.prepareStatement(contactsQuery);
				// pstmt2 = connection.prepareStatement(contactsMailQuery);

				pstmt1.setString(1, costingHDRDOB.getQuoteid());
				pstmt1.setString(2, costingHDRDOB.getLaneNo());// Added by
																// Mohan for
																// issue
																// no.219979 on
																// 10122010

				rs2 = pstmt1.executeQuery();

				while (rs2.next()) {
					/*
					 * pstmt2.setString(1,costingMasterDOB.getCustomerid());
					 * pstmt2.setString(2,rs2.getString("SL_NO"));
					 * 
					 * rs3 = pstmt2.executeQuery();
					 */

					/*
					 * if(rs3.next()) {
					 */
					contactPersonsIds.add(rs2.getString("SL_NO"));
					contactPersonsList.add(rs2.getString("CONTACTPERSON"));
					contactsMailList.add(rs2.getString("EMAILID"));
					contactsFaxList.add(rs2.getString("FAX"));
					// }

					/*
					 * if(rs3!=null) rs3.close(); pstmt2.clearParameters();
					 */
				}

				/*
				 * if(pstmt2!=null) pstmt2.close();
				 */// Commented By RajKumari on 24-10-2008
										// for Connection Leakages.
				if (rs2 != null)
					rs2.close();
				if (pstmt1 != null)
					pstmt1.close();

				if (contactsMailList != null && contactsMailList.size() > 0) {
					contactsEmailIds = new String[contactsMailList.size()];
					contactPersonIds = new String[contactPersonsIds.size()];
					contactPersonNames = new String[contactsMailList.size()];
					contactsFax = new String[contactsFaxList.size()];
					int contMailSize = contactsMailList.size();
					for (int i = 0; i < contMailSize; i++) {
						contactPersonIds[i] = (String) contactPersonsIds.get(i);
						contactsEmailIds[i] = (String) contactsMailList.get(i);
						contactsFax[i] = (String) contactsFaxList.get(i);
						contactPersonNames[i] = (String) contactPersonsList
								.get(i);
					}
					costingMasterDOB.setContactPersonIds(contactPersonIds);
					costingMasterDOB.setContactPersonNames(contactPersonNames);
					costingMasterDOB.setContactEmailIds(contactsEmailIds);
					costingMasterDOB.setContactsFax(contactsFax);
				}

				pstmt1 = connection.prepareStatement(creatorDtlsQry);

				pstmt1.setString(1, costingHDRDOB.getUserId());
				pstmt1.setString(2, costingHDRDOB.getTerminalId());

				rs2 = pstmt1.executeQuery();

				if (rs2.next()) {
					// @@Added for the WPBN issue-61303
					if (rs2.getString("DEPARTMENT") != null)
						department = rs2.getString("DEPARTMENT");
					costingMasterDOB.setCreatorDetails(rs2
							.getString("USERNAME")
							+ "\n" + department);
					costingMasterDOB.setPhoneNo(rs2.getString("PHONE_NO"));
					costingMasterDOB
							.setMobileNo((rs2.getString("MOBILE_NO") != null) ? rs2
									.getString("MOBILE_NO")
									: "");
					costingMasterDOB
							.setFaxNo((rs2.getString("FAX_NO") != null) ? rs2
									.getString("FAX_NO") : "");
					costingMasterDOB.setCompanyName((rs2
							.getString("COMPANYNAME") != null) ? rs2
							.getString("FAX_NO") : "");
					// @@WPBN issue-61303
				}

				if (rs2 != null)
					rs2.close();
				if (pstmt1 != null)
					pstmt1.close();
				// @@Commented by subrahmanyam for the Enhancement 167668 on
				// 29/04/09
				/*
				 * pstmt1 = connection.prepareStatement(addressQry);
				 * 
				 * pstmt1.setString(1,costingHDRDOB.getTerminalId());
				 * 
				 * rs2 = pstmt1.executeQuery();
				 * 
				 * if(rs2.next())
				 * costingMasterDOB.setTerminalAddress(rs2.getString("ADDRESS"));
				 * if(rs2!=null) rs2.close(); if(pstmt1!=null) pstmt1.close();
				 */
				// @@ Added by subrahmanyam for Enhanement 167668 on 29/04/09
				custAddrQry = " SELECT  CUST_ADDRLINE1 ,CUST_ADDRLINE2 ,CUST_ADDRLINE3  FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=? ";
				queryNoCustAddr = "SELECT AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"
						+ "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "
						+ ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
				pstmt1 = connection.prepareStatement(custAddrQry);
				pstmt1.setString(1, costingHDRDOB.getUserId());
				pstmt1.setString(2, costingHDRDOB.getTerminalId());
				rs2 = pstmt1.executeQuery();
				if (rs2.next()) {
					custAddress1 = rs2.getString("CUST_ADDRLINE1");
					custAddress2 = rs2.getString("CUST_ADDRLINE2");
					custAddress3 = rs2.getString("CUST_ADDRLINE3");
				}
				if (rs2 != null)
					rs2.close();
				if (pstmt1 != null)
					pstmt1.close();
				if (custAddress1 == null && custAddress2 == null
						&& custAddress3 == null) {
					pstmt1 = connection.prepareStatement(addressQry);
					pstmt1.setString(1, costingHDRDOB.getTerminalId());
					rs2 = pstmt1.executeQuery();

					if (rs2.next()) {
						costingMasterDOB.setTerminalAddress(rs2
								.getString("ADDRESS"));

					}
					if (rs2 != null)
						rs2.close();
					if (pstmt1 != null)
						pstmt1.close();
				} else {
					// custAddress1=(custAddress1!=null?custAddress1+"\n":"")+(custAddress2!=null?custAddress2+"\n":"")+(custAddress3!=null?custAddress3+"\n":"");
					custAddress1 = (custAddress1 != null ? custAddress1
							+ "\n\n" : "")
							+ (custAddress2 != null ? custAddress2 + "\n\n"
									: "")
							+ (custAddress3 != null ? custAddress3 + "\n\n"
									: "");
					pstmt1 = connection.prepareStatement(queryNoCustAddr);
					pstmt1.setString(1, costingHDRDOB.getTerminalId());
					rs2 = pstmt1.executeQuery();

					if (rs2.next()) {
						costingMasterDOB.setTerminalAddress(custAddress1
								+ rs2.getString("ADDRESS"));
					}
					if (rs2 != null)
						rs2.close();
					if (pstmt1 != null)
						pstmt1.close();
				}
				// @@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09

				// Modified by Mohan for issue no.219979 on 10122010
				cStmt = connection
						.prepareCall("{ call Qms_Quote_Pack.quote_view_proc(?,?,?,?,?,?) }");

				cStmt.clearParameters();
				// cStmt.setLong(1,Long.parseLong(costingHDRDOB.getQuoteid()));//
				// @@ commented by subrahmanyam for the enhancement 146971 on
				// 1/12/08
				cStmt.setString(1, costingHDRDOB.getQuoteid());// @@ Added by
																// subrahmanyam
																// for the
																// enhancement
																// 146971 on
																// 1/12/08
				cStmt.setString(2, costingHDRDOB.getLaneNo());// Added/Modified
																// by Mohan for
																// issue
																// no.219979 on
																// 10122010
				cStmt.registerOutParameter(3, OracleTypes.CURSOR);// Modified
																	// by Mohan
																	// for issue
																	// no.219979
																	// on
																	// 10122010
				cStmt.registerOutParameter(4, OracleTypes.CURSOR);// Modified
																	// by Mohan
																	// for issue
																	// no.219979
																	// on
																	// 10122010
				cStmt.registerOutParameter(5, OracleTypes.CURSOR);// Modified
																	// by Mohan
																	// for issue
																	// no.219979
																	// on
																	// 10122010
				cStmt.registerOutParameter(6, OracleTypes.CURSOR);// Modified
																	// by Mohan
																	// for issue
																	// no.219979
																	// on
																	// 10122010
				cStmt.execute();

				rs = (ResultSet) cStmt.getObject(3);// Modified by Mohan for
													// issue no.219979 on
													// 10122010

				legDetails = new ArrayList();
				// to get buysell rates//
				int i = 0;
				while (rs.next()) {

					if (costingLegDetailsDOB != null
							&& costingLegDetailsDOB.getLegSerialNo() == rs
									.getInt("LEG_SL_NO")) {
if ((costingRateInfoDOB.getSellRateId() != null && costingRateInfoDOB
								.getSellRateId().equals(
										rs.getString("SELLCHARGEID")))
								|| (costingRateInfoDOB.getBuyRateId() != null && costingRateInfoDOB
										.getBuyRateId().equals(
												rs.getString("BUY_CHARGE_ID"))))// for
																				// FSC
																				// charges
						{
							System.out.println("RATE_DESCRIPTION  :  "
									+ rs.getString("RATE_DESCRIPTION"));
							if ("Carrier Security Fee-FF".equalsIgnoreCase(rs
									.getString("RATE_DESCRIPTION"))) {
								System.out.println("in dao : "
										+ rs.getString("WEIGHT_BREAK"));

							}
							weightBreak = costingChargeDetailsDOB.getWeightBreak();
							rateType = costingChargeDetailsDOB.getRateType();
							costingRateInfoDOB = new CostingRateInfoDOB();
							if ("A FREIGHT RATE".equalsIgnoreCase(rs
									.getString("RATE_DESCRIPTION")))
								costingRateInfoDOB
										.setRateDescription("FREIGHT RATE");
							else if ("C.P.S.S".equalsIgnoreCase(rs
									.getString("RATE_DESCRIPTION")))
								costingRateInfoDOB.setRateDescription("P.S.S");
							else
								costingRateInfoDOB.setRateDescription(rs
										.getString("RATE_DESCRIPTION"));// @@Added
																		// by
																		// Kameswari
																		// for
																		// Surcharge
																		// Enhancements

							costingRateInfoDOB.setWeightBreakSlab(rs
									.getString("CHARGESLAB"));
			if (!("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))) {
			if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CSF")
			|| costingRateInfoDOB.getWeightBreakSlab().contains("CSF")) {
			costingChargeDetailsDOB.setChargeBasis("Per Shipment");
			} else if (costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")
            || costingRateInfoDOB.getWeightBreakSlab().endsWith("CAFLF")					
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("BAF")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("CSF")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("PSS")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("caf")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("baf")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("csf")
			|| costingRateInfoDOB.getWeightBreakSlab().endsWith("pss")
			||(costingRateInfoDOB.getWeightBreakSlab().length() > 5 && "LF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3, 5)))
			|| costingRateInfoDOB.getWeightBreakSlab().startsWith("CAF")) {
	if (costingRateInfoDOB.getWeightBreakSlab().toUpperCase().endsWith("CAF")
	 ||  costingRateInfoDOB.getWeightBreakSlab().toUpperCase().endsWith("CAFLF")		
     ||(costingRateInfoDOB.getWeightBreakSlab().startsWith("CAF") && costingRateInfoDOB.getWeightBreakSlab().endsWith("PERCENT"))) {
			costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
			costingChargeDetailsDOB.setChargeBasisDesc("Percent of Freight");
	} else {
			costingChargeDetailsDOB.setChargeBasis("Per Container");
									}
	} else if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN")
	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("MIN")
	|| "MIN".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab())) {
		costingChargeDetailsDOB.setChargeBasis("Per Shipment");
	} else if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
	|| costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3")
	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("M3")
	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("BAFFFFLAT")) {
if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")
     || costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3")
     || costingRateInfoDOB.getWeightBreakSlab().endsWith("M3")
	|| costingRateInfoDOB.getWeightBreakSlab().endsWith("FLAT")) {
										// costingChargeDetailsDOB.setChargeBasis("per
										// Cubic Meter");
	costingChargeDetailsDOB.setChargeBasis("Per Weight Measurement");
	costingChargeDetailsDOB.setChargeBasisDesc("Per Weight Measurement");
    } else {
    	costingChargeDetailsDOB.setChargeBasis("Percent of Freight"); 
          }
	}

 else if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")
		 || costingRateInfoDOB.getWeightBreakSlab()	.equalsIgnoreCase("SSBASIC")
		 || costingRateInfoDOB.getWeightBreakSlab().endsWith("BASIC")) {
		costingChargeDetailsDOB	.setChargeBasis("Per Shipment");
		} else if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")
         || costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG")
		 || costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("FLAT")
		 || isNumber(costingRateInfoDOB.getWeightBreakSlab())) {
		 costingChargeDetailsDOB.setChargeBasis("Per Kg");
       } else if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase(	"SURCHARGE")) {
		 costingChargeDetailsDOB.setChargeBasis("Percent of Freight");
		 costingChargeDetailsDOB.setChargeBasisDesc("Percent of Freight");
		} else
		  costingChargeDetailsDOB.setChargeBasis("Per Kg");
		} else {
		if ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType))
			costingChargeDetailsDOB.setChargeBasisDesc("Per Shipment");
		else {
		if ("1".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) && "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK"))) {
				costingChargeDetailsDOB.setChargeBasisDesc("Per ULD");
		} else if ("Flat".equalsIgnoreCase(rs.getString("CHARGESLAB"))) {
		if ("1"	.equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()) /*|| "4"	.equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())*/)	
		costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
		else
		costingChargeDetailsDOB.setChargeBasisDesc("Per Weight Measurement");
		} else if (("2".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode())
		 || "4".equalsIgnoreCase(costingChargeDetailsDOB.getShipmentMode()))&& "LIST".equalsIgnoreCase(rs.getString("WEIGHT_BREAK"))) {
			costingChargeDetailsDOB.setChargeBasisDesc("Per Container");
		} else if ("Per Kg".equalsIgnoreCase(rs.getString("CHARGEBASIS"))) {
			costingChargeDetailsDOB.setChargeBasisDesc("Per Kilogram");
		} else if ("Per Lb".equalsIgnoreCase(rs.getString("CHARGEBASIS"))) {
             costingChargeDetailsDOB.setChargeBasisDesc("Per Pound");
        } else if ("Per CBM".equalsIgnoreCase(rs.getString("CHARGEBASIS"))) {
			costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Meter");
		} else if ("Per CFT".equalsIgnoreCase(rs.getString("CHARGEBASIS"))) {
			costingChargeDetailsDOB.setChargeBasisDesc("Per Cubic Feet");
		} else
			costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
		}
	}
							costingRateInfoDOB.setSellRateId(rs
									.getString("SELLCHARGEID"));
							costingRateInfoDOB.setBuyRateId(rs
													.getString("BUY_CHARGE_ID"));

							costingRateInfoDOB.setLowerBound(rs
									.getDouble("LBOUND"));
							costingRateInfoDOB.setUpperBound(rs
									.getDouble("UBOUND"));
							
							if ("FREIGHT RATE"
									.equalsIgnoreCase(costingRateInfoDOB
											.getRateDescription()))

								costingRateInfoDOB.setRateIndicator(rs
										.getString("RATE_INDICATOR"));
							else
								costingRateInfoDOB.setRateIndicator("");
							// Logger.info(FILE_NAME,""+rs.getString("CHARGESLAB"));
							if ("M".equalsIgnoreCase(costingChargeDetailsDOB
									.getMarginDiscountType())
									|| costingChargeDetailsDOB
											.getMarginDiscountType() == null) {
								costingRateInfoDOB.setMargin(rs
										.getDouble("MARGINVALUE"));
								costingRateInfoDOB.setMarginType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("BUYRATE")
											+ rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("BUYRATE")
											+ (rs.getDouble("BUYRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							} else {
								costingRateInfoDOB.setDiscount(rs
										.getDouble("MARGINVALUE"));
								costingRateInfoDOB.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}

							costingRateInfoDOB.setRate(Double
									.parseDouble(deciFormat.format(sellRate)));
							rateList.add(costingRateInfoDOB);

							if (rs.getString("CURRENCY") != null)
								costingRateInfoDOB.setMutilQuoteCurrency(rs
										.getString("CURRENCY"));
							else
								costingRateInfoDOB
										.setMutilQuoteCurrency(loginbean
												.getCurrencyId());
						} else {
							costingChargeDetailsDOB = new CostingChargeDetailsDOB();
							chargeList.add(costingChargeDetailsDOB);

							if (rs.getString("CURRENCY") != null)
								costingChargeDetailsDOB.setCurrency(rs
										.getString("CURRENCY"));
							else
								costingChargeDetailsDOB.setCurrency(loginbean
										.getCurrencyId());

							costingChargeDetailsDOB.setWeightBreak(rs
									.getString("WEIGHT_BREAK"));
							costingChargeDetailsDOB.setRateType((rs
									.getString("RATE_TYPE") != null && !""
									.equals(rs.getString("RATE_TYPE"))) ? rs
									.getString("RATE_TYPE") : rs
									.getString("WEIGHT_BREAK"));
							costingChargeDetailsDOB.setWeightClass(rs
									.getString("WEIGHT_SCALE"));
							costingChargeDetailsDOB.setDensityRatio(rs
									.getString("DENSITY_RATIO"));

							costingChargeDetailsDOB.setShipmentMode(rs
									.getString("SHMODE"));

							weightBreak = costingChargeDetailsDOB.getWeightBreak();
							rateType = costingChargeDetailsDOB.getRateType();
							String breakpoint = costingChargeDetailsDOB
									.getBrkPoint();
							// @@Commented by Kameswari for Surcharge
							// Enhancements
							costingChargeDetailsDOB.setPrimaryBasis(rs
									.getString("PRIMARY_BASIS"));

							costingChargeDetailsDOB.setMarginDiscountType(rs
									.getString("MARGIN_DISCOUNT_FLAG"));
							costingChargeDetailsDOB
									.setTertiaryBasis("Chargeable");
							rateList = new ArrayList();

							costingRateInfoDOB = new CostingRateInfoDOB();

							if (rs.getString("CURRENCY") != null)
								costingRateInfoDOB.setMutilQuoteCurrency(rs
										.getString("CURRENCY"));
							else
								costingRateInfoDOB
										.setMutilQuoteCurrency(loginbean
												.getCurrencyId());

							if ("A FREIGHT RATE".equalsIgnoreCase(rs
									.getString("RATE_DESCRIPTION"))
									|| "".equalsIgnoreCase(rs
											.getString("RATE_DESCRIPTION")))
								costingRateInfoDOB
										.setRateDescription("FREIGHT RATE");
							else if ("C.P.S.S".equalsIgnoreCase(rs
									.getString("RATE_DESCRIPTION")))
								costingRateInfoDOB.setRateDescription("P.S.S");
							else
								costingRateInfoDOB.setRateDescription(rs
										.getString("RATE_DESCRIPTION"));// @@Added
																		// by
																		// Kameswari
																		// for
																		// Surcharge
																		// Enhancements

							costingRateInfoDOB.setWeightBreakSlab(rs
									.getString("CHARGESLAB"));
		if (!("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB.getRateDescription()))) {
	if (costingRateInfoDOB.getWeightBreakSlab().equalsIgnoreCase("CSF")) {
				costingChargeDetailsDOB.setChargeBasis("Per Shipment");
		
		}else if (costingRateInfoDOB.getWeightBreakSlab().endsWith("CAF")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("BAF")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("CSF")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("PSS")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("caf")
        || costingRateInfoDOB.getWeightBreakSlab().endsWith("baf")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("csf")
		|| costingRateInfoDOB.getWeightBreakSlab().endsWith("pss")
		|| (costingRateInfoDOB.getWeightBreakSlab().length() > 5 && "LF".equalsIgnoreCase(costingRateInfoDOB.getWeightBreakSlab().substring(3, 5)))) {
			
									if (costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("CAF")
											|| costingRateInfoDOB
													.getWeightBreakSlab()
													.endsWith("caf")) {
										costingChargeDetailsDOB
												.setChargeBasis("Percent of Freight");
									} else {
										costingChargeDetailsDOB
												.setChargeBasis("Per Container");
									}
								} else if (costingRateInfoDOB
										.getWeightBreakSlab().equalsIgnoreCase(
												"BAFMIN")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("CAFMIN")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("PSSMIN")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("FSMIN")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("SSMIN")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("MIN")) {
									costingChargeDetailsDOB
											.setChargeBasis("Per Shipment");
								} else if (costingRateInfoDOB
										.getWeightBreakSlab().equalsIgnoreCase(
												"BAFM3")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("CAF%")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("PSSM3")
										|| "2"
												.equalsIgnoreCase(costingChargeDetailsDOB
														.getShipmentMode())
										|| costingRateInfoDOB
												.getWeightBreakSlab().endsWith(
														"FLAT")) {
									if (costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("BAFM3")
											|| costingRateInfoDOB
													.getWeightBreakSlab()
													.equalsIgnoreCase("PSSM3")
											|| costingRateInfoDOB
													.getWeightBreakSlab()
													.endsWith("FLAT")) {
										// costingChargeDetailsDOB.setChargeBasis("per
										// Cubic Meter");
										costingChargeDetailsDOB
												.setChargeBasis("Per Weight Measurement");

									} else {
										costingChargeDetailsDOB
												.setChargeBasis("Percent of Freight");
									}
								}

								else if (costingRateInfoDOB
										.getWeightBreakSlab().equalsIgnoreCase(
												"FSBASIC")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("SSBASIC")
										|| costingRateInfoDOB
												.getWeightBreakSlab().endsWith(
														"BASIC")) {
									costingChargeDetailsDOB
											.setChargeBasis("Per Shipment");
								} else if (costingRateInfoDOB
										.getWeightBreakSlab().equalsIgnoreCase(
												"FSKG")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("SSKG")
										|| isNumber(costingRateInfoDOB
												.getWeightBreakSlab())) {
									costingChargeDetailsDOB
											.setChargeBasis("Per Kg");
								} else if (costingRateInfoDOB
										.getWeightBreakSlab().equalsIgnoreCase(
												"SURCHARGE")) {
									costingChargeDetailsDOB
											.setChargeBasis("Percent of Freight");
								}
							} else {
								if ("SLAB".equalsIgnoreCase(weightBreak)
										&& "FLAT".equalsIgnoreCase(rateType))
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Shipment");

								else {
									if ("1"
											.equalsIgnoreCase(costingChargeDetailsDOB
													.getShipmentMode())
											&& "LIST".equalsIgnoreCase(rs
													.getString("WEIGHT_BREAK"))) {
										costingChargeDetailsDOB
												.setChargeBasisDesc("Per ULD");
									} else if (("2"
											.equalsIgnoreCase(costingChargeDetailsDOB
													.getShipmentMode()) || "4"
											.equalsIgnoreCase(costingChargeDetailsDOB
													.getShipmentMode()))
											&& "LIST".equalsIgnoreCase(rs
													.getString("WEIGHT_BREAK"))) {
										costingChargeDetailsDOB
												.setChargeBasisDesc("Per Container");
									} else if ("Per Kg".equalsIgnoreCase(rs
											.getString("CHARGEBASIS"))) {
										costingChargeDetailsDOB
												.setChargeBasisDesc("Per Kilogram");
									} else if ("Per Lb".equalsIgnoreCase(rs
											.getString("CHARGEBASIS"))) {

										costingChargeDetailsDOB
												.setChargeBasisDesc("Per Pound");
									} else if ("Per CBM".equalsIgnoreCase(rs
											.getString("CHARGEBASIS"))) {
										costingChargeDetailsDOB
												.setChargeBasisDesc("Per Cubic Meter");
									} else if ("Per CFT".equalsIgnoreCase(rs
											.getString("CHARGEBASIS"))) {
										costingChargeDetailsDOB
												.setChargeBasisDesc("Per Cubic Feet");
									} else
										costingChargeDetailsDOB
												.setChargeBasisDesc(rs
														.getString("CHARGEBASIS"));
								}

							}

							// costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
							costingRateInfoDOB.setLowerBound(rs
									.getDouble("LBOUND"));
							costingRateInfoDOB.setUpperBound(rs
									.getDouble("UBOUND"));
							if ("FREIGHT RATE"
									.equalsIgnoreCase(costingRateInfoDOB
											.getRateDescription()))
								costingRateInfoDOB.setRateIndicator(rs
										.getString("RATE_INDICATOR"));
							else
								costingRateInfoDOB.setRateIndicator("");
							costingRateInfoDOB.setSellRateId(rs
									.getString("SELLCHARGEID"));
							costingRateInfoDOB.setBuyRateId(rs
									.getString("BUY_CHARGE_ID"));

							if ("M".equalsIgnoreCase(costingChargeDetailsDOB
									.getMarginDiscountType())
									|| costingChargeDetailsDOB
											.getMarginDiscountType() == null) {
								costingRateInfoDOB.setMargin(rs
										.getDouble("MARGINVALUE"));
								costingRateInfoDOB.setMarginType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("BUYRATE")
											+ rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("BUYRATE")
											+ (rs.getDouble("BUYRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							} else {
								costingRateInfoDOB.setDiscount(rs
										.getDouble("MARGINVALUE"));
								costingRateInfoDOB.setDiscountType(rs
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- rs.getDouble("MARGINVALUE");
								else if ("P".equalsIgnoreCase(rs
										.getString("MARGIN_TYPE")))
									sellRate = rs.getDouble("SELLRATE")
											- (rs.getDouble("SELLRATE")
													* rs
															.getDouble("MARGINVALUE") / 100);
							}
							costingRateInfoDOB.setRate(Double
									.parseDouble(deciFormat.format(sellRate)));
							rateList.add(costingRateInfoDOB);
							costingChargeDetailsDOB
									.setCostingRateInfoDOB(rateList);
						}
						System.out.println("in dao getWeightBreak  : "
								+ costingChargeDetailsDOB.getWeightBreak());
						System.out.println("in dao : "
								+ costingChargeDetailsDOB.getRateDescription());

					} else {
						costingLegDetailsDOB = new CostingLegDetailsDOB();
						legDetails.add(costingLegDetailsDOB);

						chargeList = new ArrayList();

						costingLegDetailsDOB
								.setCostingChargeDetailList(chargeList);

						costingLegDetailsDOB.setOrigin(rs.getString("ORG"));
						costingLegDetailsDOB.setDestination(rs
								.getString("DEST"));
						costingLegDetailsDOB.setLegSerialNo(rs
								.getInt("LEG_SL_NO"));
						costingLegDetailsDOB.setFrequency((rs
								.getString("FREQUENCY") != null) ? rs
								.getString("FREQUENCY") : "");// @@ MOdified
																// by kameswari
																// for the wpbn
																// issue 174425
																// on 24-jun-09
						costingLegDetailsDOB.setTransitTime((rs
								.getString("TRANSITTIME") != null) ? rs
								.getString("TRANSITTIME") : "");
						costingLegDetailsDOB.setNotes(rs.getString("NOTES"));
						costingLegDetailsDOB.setFrequencyChecked(rs
								.getString("FREQUENCY_CHECKED"));
						costingLegDetailsDOB.setTransitChecked(rs
								.getString("TRANSITTIME_CHECKED"));
						costingLegDetailsDOB.setTransitChecked(rs
								.getString("TRANSITTIME_CHECKED"));
						// Added by Rakesh for Issue:235800
						costingLegDetailsDOB
								.setCarrier(rs.getString("CARRIER"));
						costingLegDetailsDOB.setServiceLevel(rs
								.getString("SRV_LEVEL"));
						// Ended by Rakesh for Issue:235800

						if (rs.getString("CARRIER") != null
								&& (rs.getString("CARRIER_CHECKED") != null && "on"
										.equalsIgnoreCase(rs
												.getString("CARRIER_CHECKED")))) {
							costingLegDetailsDOB.setCarrierName(rs
									.getString("CARRIER"));
						}
						if (rs.getTimestamp("VALIDUPTO") != null
								&& (rs.getString("RATEVALIDITY_CHECKED") != null && "on"
										.equalsIgnoreCase(rs
												.getString("RATEVALIDITY_CHECKED")))) {
							costingLegDetailsDOB.setRateValidity(rs
									.getTimestamp("VALIDUPTO"));
						}

						/*
						 * if(rs.getTimestamp("VALIDUPTO")!=null) {
						 * costingLegDetailsDOB.setRateValidity(rs.getTimestamp("VALIDUPTO")); }
						 */
						costingChargeDetailsDOB = new CostingChargeDetailsDOB();
						chargeList.add(costingChargeDetailsDOB);
						if (rs.getString("CURRENCY") != null)
							costingChargeDetailsDOB.setCurrency(rs
									.getString("CURRENCY"));
						else
							costingChargeDetailsDOB.setCurrency(loginbean
									.getCurrencyId());

						costingChargeDetailsDOB.setWeightBreak(rs
								.getString("WEIGHT_BREAK"));
						costingChargeDetailsDOB.setRateType((rs
								.getString("RATE_TYPE") != null && !""
								.equals(rs.getString("RATE_TYPE"))) ? rs
								.getString("RATE_TYPE") : rs
								.getString("WEIGHT_BREAK"));
						costingChargeDetailsDOB.setWeightClass(rs
								.getString("WEIGHT_SCALE"));
						costingChargeDetailsDOB.setDensityRatio(rs
								.getString("DENSITY_RATIO"));

						costingChargeDetailsDOB.setShipmentMode(rs
								.getString("SHMODE"));
						// costingChargeDetailsDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added
						// by kameswari for Surcharge Enhancements
						// costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
						weightBreak = costingChargeDetailsDOB.getWeightBreak();
						rateType = costingChargeDetailsDOB.getRateType();
						String breakpoint = costingChargeDetailsDOB
								.getBrkPoint();

						costingChargeDetailsDOB.setPrimaryBasis(rs
								.getString("PRIMARY_BASIS"));

						costingChargeDetailsDOB.setMarginDiscountType(rs
								.getString("MARGIN_DISCOUNT_FLAG"));

						costingChargeDetailsDOB.setTertiaryBasis("Chargeable");// Default
																				// is
																				// chargable
																				// incase
																				// of
																				// frieght

						rateList = new ArrayList();

						costingRateInfoDOB = new CostingRateInfoDOB();
						costingRateInfoDOB.setWeightBreakSlab(rs
								.getString("CHARGESLAB"));
						if ("A FREIGHT RATE".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION"))
								|| "".equalsIgnoreCase(rs
										.getString("RATE_DESCRIPTION")))
							costingRateInfoDOB
									.setRateDescription("FREIGHT RATE");
						else if ("C.P.S.S".equalsIgnoreCase(rs
								.getString("RATE_DESCRIPTION")))
							costingRateInfoDOB.setRateDescription("P.S.S");
						else
							costingRateInfoDOB.setRateDescription(rs
									.getString("RATE_DESCRIPTION"));// @@Added
																	// by
																	// Kameswari
																	// for
																	// Surcharge
																	// Enhancements

						if (!("FREIGHT RATE"
								.equalsIgnoreCase(costingRateInfoDOB
										.getRateDescription()))) {
							if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("CSF")) {
								// costingRateInfoDOB.setWeightBreakSlab("Absolute");
								costingChargeDetailsDOB
										.setChargeBasis("Per Shipment");
							} else if (costingRateInfoDOB.getWeightBreakSlab()
									.endsWith("CAF")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("BAF")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("CSF")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("PSS")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("caf")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("baf")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("csf")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("pss")
									|| (costingRateInfoDOB.getWeightBreakSlab()
											.length() > 5 && "LF"
											.equalsIgnoreCase(costingRateInfoDOB
													.getWeightBreakSlab()
													.substring(3, 5)))) {
								if (costingRateInfoDOB.getWeightBreakSlab()
										.endsWith("CAF")
										|| costingRateInfoDOB
												.getWeightBreakSlab().endsWith(
														"caf")) {
									costingChargeDetailsDOB
											.setChargeBasis("Percent of Freight");
								} else {
									costingChargeDetailsDOB
											.setChargeBasis("Per Container");
								}
							} else if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("BAFMIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("CAFMIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("PSSMIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("FSMIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("SSMIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("MIN")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("MIN")) {
								// costingRateInfoDOB.setWeightBreakSlab("Minimum");
								costingChargeDetailsDOB
										.setChargeBasis("Per Shipment");
							} else if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("BAFM3")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("CAF%")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("PSSM3")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("FLAT")
									|| ("2"
											.equalsIgnoreCase(costingChargeDetailsDOB
													.getShipmentMode()) && costingRateInfoDOB
											.getWeightBreakSlab().endsWith(
													"FLAT"))) {
								if (costingRateInfoDOB.getWeightBreakSlab()
										.equalsIgnoreCase("BAFM3")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("PSSM3")
										|| costingRateInfoDOB
												.getWeightBreakSlab()
												.equalsIgnoreCase("M3")
										|| costingRateInfoDOB
												.getWeightBreakSlab().endsWith(
														"FLAT")) {
									// costingChargeDetailsDOB.setChargeBasis("per
									// Cubic Meter");
									costingChargeDetailsDOB
											.setChargeBasis("Per Weight Measurement");
								} else {
									costingChargeDetailsDOB
											.setChargeBasis("Percent of Freight");
								}
								// costingRateInfoDOB.setWeightBreakSlab("Or");
							}

							else if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("FSBASIC")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("SSBASIC")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("BASIC")) {
								// costingRateInfoDOB.setWeightBreakSlab("Basic");
								costingChargeDetailsDOB
										.setChargeBasis("Per Shipment");
							} else if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("FSKG")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.equalsIgnoreCase("SSKG")
									|| costingRateInfoDOB.getWeightBreakSlab()
											.endsWith("FLAT")
									|| isNumber(costingRateInfoDOB
											.getWeightBreakSlab())) {
								// costingRateInfoDOB.setWeightBreakSlab("Flat");
								costingChargeDetailsDOB
										.setChargeBasis("Per Kg");
							} else if (costingRateInfoDOB.getWeightBreakSlab()
									.equalsIgnoreCase("SURCHARGE")) {
								costingChargeDetailsDOB
										.setChargeBasis("Percent of Freight");
							}
						} else {
							if ("SLAB".equalsIgnoreCase(weightBreak)
									&& "FLAT".equalsIgnoreCase(rateType))
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Shipment");

							else {
								if ("1"
										.equalsIgnoreCase(costingChargeDetailsDOB
												.getShipmentMode())
										&& "LIST".equalsIgnoreCase(rs
												.getString("WEIGHT_BREAK"))) {
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per ULD");
								} else if (("2"
										.equalsIgnoreCase(costingChargeDetailsDOB
												.getShipmentMode()) || "4"
										.equalsIgnoreCase(costingChargeDetailsDOB
												.getShipmentMode()))
										&& "LIST".equalsIgnoreCase(rs
												.getString("WEIGHT_BREAK"))) {
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {

									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs
										.getString("CHARGEBASIS"))) {
									costingChargeDetailsDOB
											.setChargeBasisDesc("Per Cubic Feet");
								} else
									costingChargeDetailsDOB
											.setChargeBasisDesc(rs
													.getString("CHARGEBASIS"));
							}

						}
						if ("M".equalsIgnoreCase(costingChargeDetailsDOB
								.getMarginDiscountType())
								|| costingChargeDetailsDOB
										.getMarginDiscountType() == null) {
							costingRateInfoDOB.setMargin(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setMarginType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						} else {
							costingRateInfoDOB.setDiscount(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setDiscountType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}

						costingRateInfoDOB.setRate(Double
								.parseDouble(deciFormat.format(sellRate)));

						costingRateInfoDOB
								.setLowerBound(rs.getDouble("LBOUND"));
						costingRateInfoDOB
								.setUpperBound(rs.getDouble("UBOUND"));
						if ("FREIGHT RATE".equalsIgnoreCase(costingRateInfoDOB
								.getRateDescription()))
							costingRateInfoDOB.setRateIndicator(rs
									.getString("RATE_INDICATOR"));
						else
							costingRateInfoDOB.setRateIndicator("");
						costingRateInfoDOB.setSellRateId(rs
								.getString("SELLCHARGEID"));
						costingRateInfoDOB.setBuyRateId(rs
								.getString("BUY_CHARGE_ID"));
						// Added by Mohan for issue no.219979 on 10122010
						if (rs.getString("CURRENCY") != null)
							costingRateInfoDOB.setMutilQuoteCurrency(rs
									.getString("CURRENCY"));
						else
							costingRateInfoDOB.setMutilQuoteCurrency(loginbean
									.getCurrencyId());

						rateList.add(costingRateInfoDOB);
						costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);

					}
					i++;
				}

				costingMasterDOB.setCostingLegDetailsList(legDetails);

				if (rs != null) {
					rs.close();
				}

				/*
				 * To Order the charges. rs = (ResultSet)cStmt.getObject(3);
				 */

				// to getBuysell charges
				chargeList = new ArrayList();
				destChargeList = new ArrayList();

				costingChargeDetailsDOB = null;

				/*
				 * while(rs.next()) {
				 * 
				 * if(costingChargeDetailsDOB!=null &&
				 * ((costingChargeDetailsDOB.getBuyChargeId()!=null &&
				 * costingChargeDetailsDOB.getBuyChargeId().equals(rs.getString("BUY_CHARGE_ID"))) ||
				 * (costingChargeDetailsDOB.getSellChargeId()!=null &&
				 * costingChargeDetailsDOB.getSellChargeId().equals(rs.getString("SELLCHARGEID"))) )) {
				 * costingRateInfoDOB = new CostingRateInfoDOB();
				 * costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
				 * //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
				 * costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
				 * costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
				 * costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
				 * 
				 * if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) ||
				 * costingChargeDetailsDOB.getMarginDiscountType()==null) {
				 * costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
				 * costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
				 * 
				 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
				 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
				 * else {
				 * costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
				 * costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
				 * 
				 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
				 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
				 * 
				 * 
				 * 
				 * costingRateInfoDOB.setRate(Double.parseDouble(
				 * deciFormat.format(sellRate)));
				 * 
				 * 
				 * rateList.add(costingRateInfoDOB); }else {
				 * /*if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT"))) {
				 * if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin")) {
				 * costingMasterDOB.setOriginList(chargeList);} else {
				 * costingMasterDOB.setDestinationList(chargeList);}
				 * 
				 * chargeList = new ArrayList(); }
				 */
				/*
				 * costingChargeDetailsDOB = new CostingChargeDetailsDOB();
				 * 
				 * if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Origin")) {
				 * chargeList.add(costingChargeDetailsDOB);}
				 * if(rs.getString("COST_INCURREDAT").equalsIgnoreCase("Destination")) {
				 * destChargeList.add(costingChargeDetailsDOB);}
				 * 
				 * costingChargeDetailsDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID"));
				 * costingChargeDetailsDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
				 * 
				 * costingChargeDetailsDOB.setChargeId(rs.getString("CHARGE_ID"));
				 * costingChargeDetailsDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
				 * costingChargeDetailsDOB.setInternalName(rs.getString("INT_CHARGE_NAME"));
				 * costingChargeDetailsDOB.setExternalName(rs.getString("EXT_CHARGE_NAME"));
				 * costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
				 * costingChargeDetailsDOB.setCostIncurred(rs.getString("COST_INCURREDAT"));
				 * costingChargeDetailsDOB.setCurrency(rs.getString("CURRENCY"));
				 * costingChargeDetailsDOB.setWeightBreak(rs.getString("WEIGHT_BREAK"));
				 * costingChargeDetailsDOB.setRateType((rs.getString("RATE_TYPE")!=null &&
				 * !"".equals(rs.getString("RATE_TYPE")))?rs.getString("RATE_TYPE"):rs.getString("WEIGHT_BREAK"));
				 * costingChargeDetailsDOB.setWeightClass(rs.getString("WEIGHT_SCALE"));
				 * costingChargeDetailsDOB.setDensityRatio(rs.getString("DENSITY_RATIO"));
				 * costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
				 * costingChargeDetailsDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
				 * costingChargeDetailsDOB.setSecondaryBasis(rs.getString("SECONDARY_BASIS"));
				 * costingChargeDetailsDOB.setTertiaryBasis(rs.getString("TERTIARY_BASIS"));
				 * costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
				 * costingChargeDetailsDOB.setMarginDiscountType(rs.getString("MARGIN_DISCOUNT_FLAG"));
				 * 
				 * //getConversionfactor
				 * //costingChargeDetailsDOB.setConvfactor();
				 * 
				 * rateList = new ArrayList();
				 * 
				 * costingRateInfoDOB = new CostingRateInfoDOB();
				 * costingRateInfoDOB.setWeightBreakSlab(rs.getString("CHARGESLAB"));
				 * //costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
				 * costingRateInfoDOB.setLowerBound(rs.getDouble("LBOUND"));
				 * costingRateInfoDOB.setUpperBound(rs.getDouble("UBOUND"));
				 * costingRateInfoDOB.setRateIndicator(rs.getString("RATE_INDICATOR"));
				 * 
				 * if("M".equalsIgnoreCase(costingChargeDetailsDOB.getMarginDiscountType()) ||
				 * costingChargeDetailsDOB.getMarginDiscountType()==null) {
				 * costingRateInfoDOB.setMargin(rs.getDouble("MARGINVALUE"));
				 * costingRateInfoDOB.setMarginType(rs.getString("MARGIN_TYPE"));
				 * 
				 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
				 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
				 * else {
				 * costingRateInfoDOB.setDiscount(rs.getDouble("MARGINVALUE"));
				 * costingRateInfoDOB.setDiscountType(rs.getString("MARGIN_TYPE"));
				 * 
				 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
				 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE")))
				 * sellRate =
				 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
				 * costingRateInfoDOB.setRate(Double.parseDouble(
				 * deciFormat.format(sellRate)));
				 * 
				 * rateList.add(costingRateInfoDOB);
				 * costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);
				 *  } }
				 * 
				 * //getcartage data if(rs!=null) { rs.close();}
				 */

				rs = (ResultSet) cStmt.getObject(5); // Modified by Mohan for
														// issue no.219979 on
														// 10122010

				costingChargeDetailsDOB = null;
				// to get Cartage info

				while (rs.next()) {
					// Modified by Kishore Podili For Multi Zone Codes
					if (costingChargeDetailsDOB != null
							&& ((costingChargeDetailsDOB.getBuyChargeId() != null && costingChargeDetailsDOB
									.getBuyChargeId().equals(
											rs.getString("BUY_CHARGE_ID"))) || (costingChargeDetailsDOB
									.getSellChargeId() != null && costingChargeDetailsDOB
									.getSellChargeId().equals(
											rs.getString("SELLCHARGEID")))) && // (rs.getString("RATE_DESCRIPTION")!=
																				// null
																				// &&
																				// rs.getString("RATE_DESCRIPTION").equals(costingChargeDetailsDOB.getChargeDescId()))
							(rs.getString("INT_CHARGE_NAME") != null && rs
									.getString("INT_CHARGE_NAME").equals(
											costingChargeDetailsDOB
													.getInternalName()))

					) {
						costingRateInfoDOB = new CostingRateInfoDOB();
						costingRateInfoDOB.setWeightBreakSlab(rs
								.getString("CHARGESLAB"));
						// costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
						costingRateInfoDOB
								.setLowerBound(rs.getDouble("LBOUND"));
						costingRateInfoDOB
								.setUpperBound(rs.getDouble("UBOUND"));
						costingRateInfoDOB.setRateIndicator(rs
								.getString("RATE_INDICATOR"));
						costingRateInfoDOB.setRateDescription(rs
								.getString("RATE_DESCRIPTION"));
						if ("M".equalsIgnoreCase(costingChargeDetailsDOB
								.getMarginDiscountType())
								|| costingChargeDetailsDOB
										.getMarginDiscountType() == null) {
							costingRateInfoDOB.setMargin(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setMarginType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						} else {
							costingRateInfoDOB.setDiscount(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setDiscountType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}
						costingRateInfoDOB.setRate(Double
								.parseDouble(deciFormat.format(sellRate)));

						rateList.add(costingRateInfoDOB);
					} else {
						/*
						 * if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT"))) {
						 * if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin")) {
						 * costingMasterDOB.setOriginList(chargeList);} else {
						 * costingMasterDOB.setDestinationList(chargeList);}
						 * 
						 * chargeList = new ArrayList(); }
						 */
						costingChargeDetailsDOB = new CostingChargeDetailsDOB();

						if (rs.getString("COST_INCURREDAT").equalsIgnoreCase(
								"Pickup")) {
							chargeList.add(costingChargeDetailsDOB);
							// Commented By Kishore for multiple ZoneCodes
							// costingChargeDetailsDOB.setChargeDescId("Pickup
							// Charge");
							costingChargeDetailsDOB.setChargeDescId(rs
									.getString("RATE_DESCRIPTION"));
						}
						if (rs.getString("COST_INCURREDAT").equalsIgnoreCase(
								"Delivery")) {
							destChargeList.add(costingChargeDetailsDOB);
							// Commented By Kishore for multiple ZoneCodes
							// costingChargeDetailsDOB.setChargeDescId("Delivery
							// Charge");
							costingChargeDetailsDOB.setChargeDescId(rs
									.getString("RATE_DESCRIPTION"));
						}

						costingChargeDetailsDOB.setBuyChargeId(rs
								.getString("BUY_CHARGE_ID"));
						costingChargeDetailsDOB.setSellChargeId(rs
								.getString("SELLCHARGEID"));
						costingChargeDetailsDOB.setInternalName(rs
								.getString("INT_CHARGE_NAME"));
						costingChargeDetailsDOB.setExternalName(rs
								.getString("INT_CHARGE_NAME"));
						// costingChargeDetailsDOB.setInternalName(costingChargeDetailsDOB.getChargeDescId());COMMENTED
						// BY GOVIND
						// costingChargeDetailsDOB.setExternalName(costingChargeDetailsDOB.getChargeDescId());COMMENTED
						// BY GOVIND
						// costingChargeDetailsDOB.setChargeId(rs.getString("CHARGE_ID"));
						// costingChargeDetailsDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
						costingChargeDetailsDOB.setChargeId(rs
								.getString("COST_INCURREDAT"));
						costingChargeDetailsDOB.setWeightBreak(rs
								.getString("WEIGHT_BREAK"));
						costingChargeDetailsDOB.setRateType((rs
								.getString("RATE_TYPE") != null && !""
								.equals(rs.getString("RATE_TYPE"))) ? rs
								.getString("RATE_TYPE") : rs
								.getString("WEIGHT_BREAK"));
						// costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
						weightBreak = costingChargeDetailsDOB.getWeightBreak();
						rateType = costingChargeDetailsDOB.getRateType();
						// costingChargeDetailsDOB.setRateDescription(rs.getString("RATE_DESCRIPTION"));//@@Added
						// by kameswari for Surcharge Enhancements

						if ("SLAB".equalsIgnoreCase(weightBreak)
								&& "FLAT".equalsIgnoreCase(rateType))
							costingChargeDetailsDOB
									.setChargeBasisDesc("Per Shipment");
						else {
							if ("LIST".equalsIgnoreCase(rs
									.getString("WEIGHT_BREAK"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Container");
							} else if ("Per Kg".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Kilogram");
							} else if ("Per Lb".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Pound");
							} else if ("Per CBM".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Cubic Meter");
							} else if ("Per CFT".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Cubic Feet");
							} else
								costingChargeDetailsDOB.setChargeBasisDesc(rs
										.getString("CHARGEBASIS"));
						}

						costingChargeDetailsDOB.setCostIncurred(rs
								.getString("COST_INCURREDAT"));
						costingChargeDetailsDOB.setCurrency(rs
								.getString("CURRENCY"));
						costingChargeDetailsDOB.setWeightClass(rs
								.getString("WEIGHT_SCALE"));
						costingChargeDetailsDOB.setDensityRatio(rs
								.getString("DENSITY_RATIO"));
						costingChargeDetailsDOB.setPrimaryBasis(rs
								.getString("PRIMARY_BASIS"));

						costingChargeDetailsDOB.setShipmentMode(rs
								.getString("SHMODE"));
						costingChargeDetailsDOB.setMarginDiscountType(rs
								.getString("MARGIN_DISCOUNT_FLAG"));

						/*
						 * if("KG".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) ||
						 * "LB".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) )
						 * costingChargeDetailsDOB.setTertiaryBasis("Actual");
						 * else
						 * if("CBM".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()) ||
						 * "CFT".equalsIgnoreCase(costingChargeDetailsDOB.getPrimaryBasis()))
						 */
						costingChargeDetailsDOB.setSecondaryBasis(rs
								.getString("SECONDARY_BASIS"));
						costingChargeDetailsDOB.setTertiaryBasis(rs
								.getString("TERTIARY_BASIS"));
						costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
						// getConversionfactor
						// costingChargeDetailsDOB.setConvfactor();

						rateList = new ArrayList();

						costingRateInfoDOB = new CostingRateInfoDOB();
						costingRateInfoDOB.setWeightBreakSlab(rs
								.getString("CHARGESLAB"));
						// costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
						costingRateInfoDOB
								.setLowerBound(rs.getDouble("LBOUND"));
						costingRateInfoDOB
								.setUpperBound(rs.getDouble("UBOUND"));
						costingRateInfoDOB.setRateIndicator(rs
								.getString("RATE_INDICATOR"));
						costingRateInfoDOB.setRateDescription(rs
								.getString("RATE_DESCRIPTION"));
						if ("M".equalsIgnoreCase(costingChargeDetailsDOB
								.getMarginDiscountType())
								|| costingChargeDetailsDOB
										.getMarginDiscountType() == null) {
							costingRateInfoDOB.setMargin(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setMarginType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						} else {
							costingRateInfoDOB.setDiscount(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setDiscountType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}
						costingRateInfoDOB.setRate(Double
								.parseDouble(deciFormat.format(sellRate)));

						rateList.add(costingRateInfoDOB);
						costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);

					}
				}

				// get external notes data
				if (rs != null) {
					rs.close();
				}

				if (destChargeList != null && destChargeList.size() == 1) {
					delChargeDetailsDOB = (CostingChargeDetailsDOB) destChargeList
							.get(0);
					destChargeList.remove(0);
				}

				rs = (ResultSet) cStmt.getObject(4);// Modified by Mohan for
													// issue no.219979 on
													// 10122010
				while (rs.next()) {

					if (costingChargeDetailsDOB != null
							&& ((costingChargeDetailsDOB.getBuyChargeId() != null && costingChargeDetailsDOB
									.getBuyChargeId().equals(
											rs.getString("BUY_CHARGE_ID"))) || (costingChargeDetailsDOB
									.getSellChargeId() != null && costingChargeDetailsDOB
									.getSellChargeId().equals(
											rs.getString("SELLCHARGEID"))))) {
						costingRateInfoDOB = new CostingRateInfoDOB();
						costingRateInfoDOB.setWeightBreakSlab(rs
								.getString("CHARGESLAB"));
						// costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
						costingRateInfoDOB
								.setLowerBound(rs.getDouble("LBOUND"));
						costingRateInfoDOB
								.setUpperBound(rs.getDouble("UBOUND"));
						costingRateInfoDOB.setRateIndicator(rs
								.getString("RATE_INDICATOR"));
						costingRateInfoDOB.setRateDescription(rs
								.getString("RATE_DESCRIPTION"));
						if ("M".equalsIgnoreCase(costingChargeDetailsDOB
								.getMarginDiscountType())
								|| costingChargeDetailsDOB
										.getMarginDiscountType() == null) {
							costingRateInfoDOB.setMargin(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setMarginType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						} else {
							costingRateInfoDOB.setDiscount(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setDiscountType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}

						// Commented and added by subrahmanyam for 180161 on
						// 25-aug-09
						// costingRateInfoDOB.setRate(Double.parseDouble(
						// deciFormat.format(sellRate)));
						costingRateInfoDOB.setRate(Double
								.parseDouble(roundDecimal(sellRate)));
						// Ended for 180161

						rateList.add(costingRateInfoDOB);
					} else {
						/*
						 * if(!costingChargeDetailsDOB.getCostIncurred().equals(rs.getString("COST_INCURREDAT"))) {
						 * if(costingChargeDetailsDOB.getCostIncurred().equalsIgnoreCase("Origin")) {
						 * costingMasterDOB.setOriginList(chargeList);} else {
						 * costingMasterDOB.setDestinationList(chargeList);}
						 * 
						 * chargeList = new ArrayList(); }
						 */
						costingChargeDetailsDOB = new CostingChargeDetailsDOB();

						if (rs.getString("COST_INCURREDAT").equalsIgnoreCase(
								"Origin")) {
							chargeList.add(costingChargeDetailsDOB);
						}
						if (rs.getString("COST_INCURREDAT").equalsIgnoreCase(
								"Destination")) {
							destChargeList.add(costingChargeDetailsDOB);
						}

						costingChargeDetailsDOB.setBuyChargeId(rs
								.getString("BUY_CHARGE_ID"));
						costingChargeDetailsDOB.setSellChargeId(rs
								.getString("SELLCHARGEID"));

						costingChargeDetailsDOB.setChargeId(rs
								.getString("CHARGE_ID"));
						costingChargeDetailsDOB.setChargeDescId(rs
								.getString("CHARGEDESCID"));
						costingChargeDetailsDOB.setInternalName(rs
								.getString("INT_CHARGE_NAME"));
						costingChargeDetailsDOB.setExternalName(rs
								.getString("EXT_CHARGE_NAME"));
						// costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
						costingChargeDetailsDOB.setCostIncurred(rs
								.getString("COST_INCURREDAT"));
						costingChargeDetailsDOB.setCurrency(rs
								.getString("CURRENCY"));
						costingChargeDetailsDOB.setWeightBreak(rs
								.getString("WEIGHT_BREAK"));
						costingChargeDetailsDOB.setRateType((rs
								.getString("RATE_TYPE") != null && !""
								.equals(rs.getString("RATE_TYPE"))) ? rs
								.getString("RATE_TYPE") : rs
								.getString("WEIGHT_BREAK"));
						costingChargeDetailsDOB.setWeightClass(rs
								.getString("WEIGHT_SCALE"));
						costingChargeDetailsDOB.setDensityRatio(rs
								.getString("DENSITY_RATIO"));

						weightBreak = costingChargeDetailsDOB.getWeightBreak();
						rateType = costingChargeDetailsDOB.getRateType();

						if ("SLAB".equalsIgnoreCase(weightBreak)
								&& "FLAT".equalsIgnoreCase(rateType))
							costingChargeDetailsDOB
									.setChargeBasisDesc("Per Shipment");
						else {
							if ("Per Kg".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Kilogram");
							} else if ("Per Lb".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Pound");
							} else if ("Per CBM".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Cubic Meter");
							} else if ("Per CFT".equalsIgnoreCase(rs
									.getString("CHARGEBASIS"))) {
								costingChargeDetailsDOB
										.setChargeBasisDesc("Per Cubic Feet");
							} else
								costingChargeDetailsDOB.setChargeBasisDesc(rs
										.getString("CHARGEBASIS"));
						}

						// costingChargeDetailsDOB.setChargeBasisDesc(rs.getString("CHARGEBASIS"));
						costingChargeDetailsDOB.setPrimaryBasis(rs
								.getString("PRIMARY_BASIS"));
						costingChargeDetailsDOB.setSecondaryBasis(rs
								.getString("SECONDARY_BASIS"));
						costingChargeDetailsDOB.setTertiaryBasis(rs
								.getString("TERTIARY_BASIS"));
						costingChargeDetailsDOB.setBlock(rs.getDouble("BLOCK"));
						costingChargeDetailsDOB.setMarginDiscountType(rs
								.getString("MARGIN_DISCOUNT_FLAG"));

						// getConversionfactor
						// costingChargeDetailsDOB.setConvfactor();

						rateList = new ArrayList();

						costingRateInfoDOB = new CostingRateInfoDOB();
						costingRateInfoDOB.setWeightBreakSlab(rs
								.getString("CHARGESLAB"));
						// costingRateInfoDOB.setRate(rs.getDouble("SELLRATE"));
						costingRateInfoDOB
								.setLowerBound(rs.getDouble("LBOUND"));
						costingRateInfoDOB
								.setUpperBound(rs.getDouble("UBOUND"));
						costingRateInfoDOB.setRateIndicator(rs
								.getString("RATE_INDICATOR"));
						costingRateInfoDOB.setRateDescription(rs
								.getString("RATE_DESCRIPTION"));
						if ("M".equalsIgnoreCase(costingChargeDetailsDOB
								.getMarginDiscountType())
								|| costingChargeDetailsDOB
										.getMarginDiscountType() == null) {
							costingRateInfoDOB.setMargin(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setMarginType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("BUYRATE")
										+ (rs.getDouble("BUYRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						} else {
							costingRateInfoDOB.setDiscount(rs
									.getDouble("MARGINVALUE"));
							costingRateInfoDOB.setDiscountType(rs
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- rs.getDouble("MARGINVALUE");
							else if ("P".equalsIgnoreCase(rs
									.getString("MARGIN_TYPE")))
								sellRate = rs.getDouble("SELLRATE")
										- (rs.getDouble("SELLRATE")
												* rs.getDouble("MARGINVALUE") / 100);
						}
						// @@ commented and added by subrahmanyam for 180161
						// costingRateInfoDOB.setRate(Double.parseDouble(
						// deciFormat.format(sellRate)));
						costingRateInfoDOB.setRate(Double
								.parseDouble(roundDecimal(sellRate)));
						// Ended for 180161

						rateList.add(costingRateInfoDOB);
						costingChargeDetailsDOB.setCostingRateInfoDOB(rateList);

					}
				}

				// getcartage data
				if (rs != null) {
					rs.close();
				}

				rs = (ResultSet) cStmt.getObject(6);// Modified by Mohan for
													// issue no.219979 on
													// 10122010

				list_exNotes = new ArrayList();

				while (rs.next()) {
					list_exNotes
							.add((rs.getString("EXTERNAL_NOTES") != null) ? rs
									.getString("EXTERNAL_NOTES").trim() : "");
				}

				costingMasterDOB.setExternalNotes(list_exNotes);

				costingMasterDOB.setOriginList(chargeList);
				if (delChargeDetailsDOB != null)
					destChargeList.add(delChargeDetailsDOB);
				costingMasterDOB.setDestinationList(destChargeList);

				if (rs != null) {
					rs.close();
				}

				legDetails = costingMasterDOB.getCostingLegDetailsList();

			} else {
				throw new Exception("No data found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();

		} finally {
			try {
				if (rs1 != null) {
					rs1.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (cStmt != null) {
					cStmt.close();
				}
				ConnectionUtil.closePreparedStatement(pstmt1,rs2);// Added by Dilip for PMD Correction on 22/09/2015
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// Logger.error(FILE_NAME,"in finally"+e);
				logger.error(FILE_NAME + "in finally" + e);
			}
		}

		return costingMasterDOB;
	}

	public boolean insertCostingHDR(CostingHDRDOB costingHDRDOB)
			throws Exception {
		PreparedStatement pstmt = null;
		// ResultSet rs = null;//Commented By RajKumari on 24-10-2008 for
		// Connection Leakages.
		Connection connection = null;
		int update = 0;
		String insertQry = "INSERT INTO QMS_COSTING_MASTER (ID ,QUOTE_ID,VERSION_NO,CUSTOMER_ID,validtill,userid,terminalid,costingcount ) VALUES (COSTING_MASTER_ID_seq.nextval,?,?,?,?,?,?,?)";

		String updateQry = "UPDATE QMS_COSTING_MASTER SET costingcount = costingcount+1 WHERE QUOTE_ID=? AND VERSION_NO=? AND terminalid = ?";
		try {

			connection = this.getConnection();
			pstmt = connection.prepareStatement(updateQry);

			pstmt.setString(1, costingHDRDOB.getQuoteid());
			pstmt.setString(2, costingHDRDOB.getVersionNo());
			pstmt.setString(3, costingHDRDOB.getTerminalId());

			update = pstmt.executeUpdate();

			if (update <= 0) {
				if (pstmt != null) {
					pstmt.close();
				}

				pstmt = connection.prepareStatement(insertQry);
				pstmt.setString(1, costingHDRDOB.getQuoteid());
				pstmt.setString(2, costingHDRDOB.getVersionNo());
				pstmt.setString(3, costingHDRDOB.getCustomerid());
				pstmt.setTimestamp(4, costingHDRDOB.getValidtill());
				pstmt.setString(5, costingHDRDOB.getUserId());
				pstmt.setString(6, costingHDRDOB.getTerminalId());
				pstmt.setInt(7, 1);

				pstmt.executeUpdate();

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public String validateCostingHDR(CostingHDRDOB costingHDRDOB)
			throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection connection = null;
		/*
		 * String selectQuote = "SELECT QUOTE_ID FROM QMS_QUOTE_MASTER
		 * QM,FS_FR_CUSTOMERMASTER C WHERE QM.CUSTOMER_ID=C.CUSTOMERID AND
		 * QM.CUSTOMER_ID=? AND QUOTE_ID = ?"; String customerIdSql = "SELECT
		 * CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID =?";
		 */
		StringBuffer errorMsg;
		StringBuffer sql = null;
		int count = 0;
		int shipmentMode = 0;
		String customerName = "";
		String shipModeStr = "";
		String sqlQuery = null;
		boolean isOrigin = false;
		boolean isCustomer = false;
		StringBuffer terminalQuery = new StringBuffer("");
		StringBuffer sql1 = new StringBuffer("");
		try {
			sql = new StringBuffer();

			String quoteId = costingHDRDOB.getQuoteid();
			String customerId = costingHDRDOB.getCustomerid();
			String origin = costingHDRDOB.getOrigin();
			String destination = costingHDRDOB.getDestination();
			String terminalId = costingHDRDOB.getTerminalId();
			String empId = costingHDRDOB.getUserId();
			String accessLevel = costingHDRDOB.getAccessLevel();
			customerName = costingHDRDOB.getCompanyName();
			// String basisFlag = costingHDRDOB.getBuyRatesPermission();
			// String operation = costingHDRDOB.getOperation();

			// sql.append("SELECT QUOTE_ID,BASIS,ESCALATION_FLAG,ESCALATED_TO
			// FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=").append(quoteId);

			sql
					.append(
							"SELECT QUOTE_ID,BASIS,ESCALATION_FLAG,ESCALATED_TO FROM QMS_QUOTE_MASTER WHERE QUOTE_ID='")
					.append(quoteId);
			sql.append("'");// @@ Added by subrahmanyam for the enhancement
							// 146971 on 1/12/08
			/*
			 * if(customerId!=null && customerId.trim().length()!=0)
			 * sql.append(" AND CUSTOMER_ID='").append(customerId).append("'");
			 * 
			 * if(origin!=null && origin.trim().length()!=0) sql.append(" AND
			 * ORIGIN_LOCATION='").append(origin).append("'");
			 * 
			 * if(destination!=null && destination.trim().length()!=0)
			 * sql.append(" AND
			 * DEST_LOCATION='").append(destination).append("'");
			 */

			// sql.append(" AND (ACTIVE_FLAG ='A' OR ACTIVE_FLAG IS NULL) AND
			// COMPLETE_FLAG <>'I' AND QUOTE_STATUS='ACC' AND VERSION_NO=(SELECT
			// MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=
			// ").append(quoteId).append(") AND TERMINAL_ID IN ");
			// @@ Commented by subrahmanyam for the enhancement 146971 on
			// 1/12/08
			// sql.append(" AND (ACTIVE_FLAG ='A' OR ACTIVE_FLAG IS NULL) AND
			// COMPLETE_FLAG <>'I' AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM
			// QMS_QUOTE_MASTER WHERE QUOTE_ID=").append(quoteId).append(") AND
			// TERMINAL_ID IN ");
			// @@ Added by subrahmanyam for the enhancement 146971 on 1/12/08
			sql
					.append(
							" AND (ACTIVE_FLAG ='A' OR ACTIVE_FLAG IS NULL) AND COMPLETE_FLAG <>'I'  AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID='")
					.append(quoteId).append("'")
					.append(") AND TERMINAL_ID IN ");

			if ("HO_TERMINAL".equalsIgnoreCase(accessLevel)) {
				sql.append(" (SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
				sql1.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
				terminalQuery
						.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
			} else {

				// @@ Commented by subrahmanyam for the CR_Enhancement_167669 on
				// 27/May/09
				/*
				 * sql.append("( SELECT parent_terminal_id terminalid FROM
				 * fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id =
				 * child_terminal_id START WITH child_terminal_id = '");
				 * sql.append(terminalId).append("' "); sql.append(" UNION
				 * SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN
				 * CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
				 * sql.append("START WITH PARENT_TERMINAL_ID =
				 * '").append(terminalId).append("' UNION SELECT TERMINALID FROM
				 * FS_FR_TERMINALMASTER WHERE
				 * TERMINALID='").append(terminalId).append("' union select
				 * terminalid term_id from fs_fr_terminalmaster where
				 * oper_admin_flag = 'H' )");
				 * 
				 * terminalQuery.append("( SELECT parent_terminal_id terminalid
				 * FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id =
				 * child_terminal_id START WITH child_terminal_id = '");
				 * terminalQuery.append(terminalId).append("' ");
				 * terminalQuery.append(" UNION SELECT CHILD_TERMINAL_ID TERM_ID
				 * FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID =
				 * PARENT_TERMINAL_ID "); terminalQuery.append("START WITH
				 * PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION
				 * SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE
				 * TERMINALID='").append(terminalId).append("' union select
				 * terminalid term_id from fs_fr_terminalmaster where
				 * oper_admin_flag = 'H' )");
				 */
				// @@Added by subrahmanyam for the CR_Enhancemente 167669 on
				// 27/May/09
				if ("OPER_TERMINAL".equalsIgnoreCase(accessLevel)) {
					sql
							.append("( SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id = '");
					sql.append(terminalId).append("' ");
					sql
							.append(" UNION SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
					sql
							.append("START WITH PARENT_TERMINAL_ID = '")
							.append(terminalId)
							.append(
									"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='")
							.append(terminalId)
							.append(
									"' union  select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ");
					sql
							.append(
									" UNION SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='")
							.append(terminalId).append("'))");

					terminalQuery
							.append("( SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id = '");
					terminalQuery.append(terminalId).append("' ");
					terminalQuery
							.append(" UNION SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
					terminalQuery
							.append("START WITH PARENT_TERMINAL_ID = '")
							.append(terminalId)
							.append(
									"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='")
							.append(terminalId)
							.append(
									"' union  select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ");
					terminalQuery
							.append(
									" UNION SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID=(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='")
							.append(terminalId).append("'))");
				} else {
					sql
							.append("( SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id = '");
					sql.append(terminalId).append("' ");
					sql
							.append(" UNION SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
					sql
							.append("START WITH PARENT_TERMINAL_ID = '")
							.append(terminalId)
							.append(
									"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='")
							.append(terminalId)
							.append(
									"' union  select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' )");

					terminalQuery
							.append("( SELECT parent_terminal_id terminalid FROM fs_fr_terminal_regn CONNECT BY PRIOR parent_terminal_id = child_terminal_id  START WITH child_terminal_id = '");
					terminalQuery.append(terminalId).append("' ");
					terminalQuery
							.append(" UNION SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
					terminalQuery
							.append("START WITH PARENT_TERMINAL_ID = '")
							.append(terminalId)
							.append(
									"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='")
							.append(terminalId)
							.append(
									"' union  select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' )");

				}

				// @@Ended by subrahmanyam for the CR_Enhancemente 167669 on
				// 27/May/09

				/*
				 * terminalQuery.append( "(SELECT PARENT_TERMINAL_ID TERM_ID
				 * FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR
				 * PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID =
				 * '").append(terminalId).append("'") .append( " UNION ")
				 * .append( " SELECT TERMINALID TERM_ID FROM
				 * FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ") .append( "
				 * UNION ") .append( " SELECT '").append(terminalId).append("'
				 * TERM_ID FROM DUAL ") .append( " UNION ") .append( " SELECT
				 * CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY
				 * PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH
				 * PARENT_TERMINAL_ID = '").append(terminalId).append("')");
				 */
				sql1
						.append("( SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
				sql1
						.append("START WITH PARENT_TERMINAL_ID = '")
						.append(terminalId)
						.append(
								"' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='")
						.append(terminalId).append("')");

			}

			// System.out.println("in vlida");
			errorMsg = new StringBuffer();
			connection = this.getConnection();

			pstmt = connection.prepareStatement(sql.toString());

			// pstmt.setString(1,costingHDRDOB.getCustomerid());

			rs = pstmt.executeQuery();

			if (!rs.next()) {
				errorMsg.append("Quote Id is ");
			}
			// System.out.println("errorMsg:"+errorMsg.toString());
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			/*
			 * pstmt = connection.prepareStatement(selectQuote);
			 * pstmt.setString(1,costingHDRDOB.getCustomerid());
			 * pstmt.setString(2,costingHDRDOB.getQuoteid());
			 * 
			 * rs = pstmt.executeQuery();
			 * 
			 * if(!rs.next()) { errorMsg.append("QuoteId "); }
			 */

			if (costingHDRDOB.getShipmentMode() == 1)
				// shipModeStr = " AND SHIPMENTMODE IN (1,3,5,7)";
				shipModeStr = "1,3,5,7";
			else if (costingHDRDOB.getShipmentMode() == 2)
				shipModeStr = "2,3,6,7";
			else if (costingHDRDOB.getShipmentMode() == 4)
				shipModeStr = "4,5,6,7";
			if (!"".equals(shipModeStr) && shipModeStr != null) {
				sqlQuery = " SELECT COUNT(*)NO_ROWS FROM FS_RT_PLAN A,FS_RT_LEG B WHERE A.QUOTE_ID= ? AND "
						+ " A.RT_PLAN_ID=B.RT_PLAN_ID AND B.SHPMNT_MODE IN("
						+ shipModeStr + ") ";

				pstmt = connection.prepareStatement(sqlQuery);
				pstmt.setString(1, quoteId);
				// pstmt.sets(2,shipModeStr);

				rs = pstmt.executeQuery();
				if (rs.next())
					count = rs.getInt("NO_ROWS");

				if (count == 0)
					errorMsg
							.append("Please Enter A Valid Quote Id For the Selected Shipment Mode.<BR>");

				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				count = 0;
			}

			if (customerName != null && customerName.trim().length() != 0) {
				isCustomer = true;
				// sqlQuery = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER
				// WHERE COMPANYNAME=? AND TERMINALID IN "+terminalQuery;
				sqlQuery = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE COMPANYNAME=? AND CUSTOMERID=?";
				pstmt = connection.prepareStatement(sqlQuery);
				pstmt.setString(1, customerName);
				pstmt.setString(2, customerId);
				rs = pstmt.executeQuery();
				if (rs.next())
					count = rs.getInt("NO_ROWS");

				if (count == 0)
					errorMsg.append("CUSTOMERNAME is Invalid.<BR>");
				else {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					count = 0;

					sqlQuery = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "
							+ terminalQuery.toString();
					pstmt = connection.prepareStatement(sqlQuery);
					pstmt.setString(1, quoteId);
					pstmt.setString(2, customerId);
					rs = pstmt.executeQuery();
					if (rs.next())
						count = rs.getInt("NO_ROWS");

					if (count == 0)
						errorMsg.append("Quote Id ").append(quoteId).append(
								" & CUSTOMERNAME ").append(customerName)
								.append(" Do Not Match.<BR>");
				}
			}

			/*
			 * if(customerId!=null && customerId.trim().length()!=0) {
			 * isCustomer = true; sqlQuery = "SELECT COUNT(*)NO_ROWS FROM
			 * FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN
			 * "+terminalQuery; pstmt = connection.prepareStatement(sqlQuery);
			 * pstmt.setString(1,customerId); rs = pstmt.executeQuery();
			 * if(rs.next()) count = rs.getInt("NO_ROWS");
			 * 
			 * if(count==0) errorMsg.append("Customer Id is Invalid.<BR>");
			 * else { if(rs!=null) rs.close(); if(pstmt!=null) pstmt.close();
			 * count = 0;
			 * 
			 * sqlQuery = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE
			 * QUOTE_ID=? AND CUSTOMER_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID
			 * IN "+sql1.toString(); pstmt =
			 * connection.prepareStatement(sqlQuery);
			 * pstmt.setString(1,quoteId); pstmt.setString(2,customerId); rs =
			 * pstmt.executeQuery(); if(rs.next()) count = rs.getInt("NO_ROWS");
			 * 
			 * if(count==0) errorMsg.append("Quote Id
			 * ").append(quoteId).append(" & Customer Id
			 * ").append(customerId).append(" Do Not Match.<BR>"); } }
			 */

			if (origin != null && origin.trim().length() != 0) {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				count = 0;
				isOrigin = true;
				sqlQuery = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
				pstmt = connection.prepareStatement(sqlQuery);
				pstmt.setString(1, origin);
				rs = pstmt.executeQuery();

				if (rs.next())
					count = rs.getInt("NO_ROWS");
				if (count == 0)
					errorMsg.append("Origin Location is Invalid.<BR>");
				else {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					count = 0;

					sqlQuery = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND ORIGIN_LOCATION=? AND TERMINAL_ID IN "
							+ terminalQuery.toString();
					pstmt = connection.prepareStatement(sqlQuery);
					pstmt.setString(1, quoteId);
					pstmt.setString(2, origin);
					rs = pstmt.executeQuery();

					if (rs.next())
						count = rs.getInt("NO_ROWS");

					if (count == 0)
						errorMsg.append("Quote Id ").append(quoteId).append(
								" & Origin Location ").append(origin).append(
								" Do Not Match.<BR>");
				}
			}

			if (destination != null && destination.trim().length() != 0) {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				count = 0;

				sqlQuery = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
				pstmt = connection.prepareStatement(sqlQuery);
				pstmt.setString(1, destination);
				rs = pstmt.executeQuery();

				if (rs.next())
					count = rs.getInt("NO_ROWS");
				if (count == 0)
					errorMsg.append("Destination Location is Invalid.<BR>");
				else {
					if (rs != null)
						rs.close();
					if (pstmt != null)
						pstmt.close();
					count = 0;

					sqlQuery = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND DEST_LOCATION=? AND TERMINAL_ID IN "
							+ terminalQuery.toString();
					pstmt = connection.prepareStatement(sqlQuery);
					pstmt.setString(1, quoteId);
					pstmt.setString(2, destination);
					rs = pstmt.executeQuery();

					if (rs.next())
						count = rs.getInt("NO_ROWS");

					if (count == 0)
						errorMsg.append("Quote Id ").append(quoteId).append(
								" & Destination Location ").append(destination)
								.append(" Do Not Match.<BR>");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return errorMsg.toString();
	}

	/**
	 * This method is used to modify the master info from QuoteMasterDOB to
	 * QuoteMaster and its child tables
	 * 
	 * @param masterDOB
	 *            an QuoteMasterDOB object that contains the master information
	 *            of the quote
	 * 
	 * @exception SQLException
	 */
	public void store(QuoteFinalDOB finalDOB) throws SQLException {
		QuoteMasterDOB masterDOB = null;
		Connection connection = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		try {
			connection = this.getConnection();

			if (finalDOB.isCompareFlag())
				this.modifyQuoteMasterDetails(finalDOB, connection);
			else {
				psmt = connection
						.prepareStatement("SELECT QUOTE_MASTER_SEQ.NEXTVAL FROM DUAL");
				rs = psmt.executeQuery();
				masterDOB = finalDOB.getMasterDOB();

				masterDOB.setPreQuoteId(masterDOB.getQuoteId());

				if (rs.next())
					// masterDOB.setUniqueId(rs.getLong(1));
					masterDOB.setUniqueId(rs.getInt(1));

				masterDOB.setVersionNo(masterDOB.getVersionNo() + 1);
				finalDOB.setMasterDOB(masterDOB);
				this.insertQuoteMasterDetails(finalDOB, connection);
			}
		} catch (SQLException sqEx) {
			sqEx.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] ->
			// "+sqEx.toString());
			logger.error(FILE_NAME + "QMSQuoteDAO[create(masterDOB)] -> "
					+ sqEx.toString());
			throw new SQLException(sqEx.toString());
		} catch (Exception e) {
			e.printStackTrace();
			// Logger.error(FILE_NAME,"QMSQuoteDAO[create(masterDOB)] ->
			// "+e.toString());
			logger.error(FILE_NAME + "QMSQuoteDAO[create(masterDOB)] -> "
					+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				ConnectionUtil.closeConnection(connection, psmt, rs);
			} catch (Exception ex) {
				// Logger.error(FILE_NAME,"Finally :
				// QMSQuoteDAO[create(masterDOB)]-> "+ex.toString());
				logger.error(FILE_NAME
						+ "Finally : QMSQuoteDAO[create(masterDOB)]-> "
						+ ex.toString());
				throw new SQLException(ex.toString());
			}
		}
	}

	/*
	 * public QuoteFreightLegSellRates getTiedCustomerFreightInfo(String
	 * customerId,String legOrigin,String legDest,String terminalId,String
	 * shipmentMode,String permissionFlag,String quoteOrigin,String
	 * quoteDestination,String Operation,long quote_id) throws SQLException {
	 * Connection conn = null; CallableStatement csmt = null; ResultSet rs =
	 * null; QuoteMasterDOB masterDOB = null; ArrayList freightChargesList =
	 * null;//to maintain the list of all origin charge dobs
	 * QuoteFreightLegSellRates legChargesDetails = null;//to maintain the
	 * charges info of each leg QuoteCharges chargesDOB = null;//to maintain one
	 * record that is to be displayed QuoteChargeInfo chargeInfo = null;
	 * ArrayList chargeInfoList = null; String flag = null; String weightBreak =
	 * null; String serviceLevel = null;
	 * 
	 * double sellRate = 0;
	 * 
	 * try { conn = this.getConnection();
	 * 
	 * csmt = conn.prepareCall("{CALL
	 * QMS_QUOTE_PACK.tied_custinfo_freight_proc(?,?,?,?,?,?,?,?,?,?,?)}");
	 * 
	 * csmt.setString(1,customerId); csmt.setString(2,legOrigin);
	 * csmt.setString(3,legDest); csmt.setString(4,terminalId);
	 * csmt.setString(5,shipmentMode); csmt.setString(6,permissionFlag);
	 * csmt.setString(7,quoteOrigin); csmt.setString(8,quoteDestination);
	 * csmt.setString(9,Operation); csmt.setString(10,""+quote_id);
	 * 
	 * csmt.registerOutParameter(11,OracleTypes.CURSOR);
	 * 
	 * csmt.execute();
	 * 
	 * double userMargin = 0;
	 * 
	 * rs = (ResultSet)csmt.getObject(11);
	 * 
	 * while(rs.next()) { flag = rs.getString("SEL_BUY_FLAG"); weightBreak =
	 * rs.getString("WEIGHT_BREAK"); serviceLevel = rs.getString("SRV_LEVEL");
	 * if(freightChargesList==null) { freightChargesList = new ArrayList(); }
	 * 
	 * if(chargesDOB!=null &&
	 * (rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getSellChargeId()) ||
	 * rs.getString("SELLCHARGEID").equalsIgnoreCase(chargesDOB.getBuyChargeId()))) {
	 * chargeInfo = new QuoteChargeInfo();
	 * 
	 * chargeInfoList.add(chargeInfo);
	 * 
	 * chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
	 * chargeInfo.setCurrency(rs.getString("CURRENCY"));
	 * chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
	 * chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
	 * chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
	 * /*chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) ||
	 * chargesDOB.getMarginDiscountFlag()==null) {
	 * chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * else { chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * //chargeInfo.setDiscount(); //chargeInfo.setDiscountType();
	 * //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
	 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
	 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
	 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint())) {
	 * chargeInfo.setBasis("Per Shipment"); } else {
	 * chargeInfo.setBasis(rs.getString("CHARGEBASIS")); }
	 * chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
	 * chargeInfo.setSellRate(sellRate); } else { chargesDOB = new
	 * QuoteCharges();
	 * 
	 * if("Carrier".equalsIgnoreCase(rs.getString("COST_INCURREDAT")))
	 * freightChargesList.add(chargesDOB);
	 * 
	 * //if it is a sell charge/rate
	 * if("RSR".equalsIgnoreCase(flag)||"CSR".equalsIgnoreCase(flag)) {
	 * chargesDOB.setSellChargeId(rs.getString("SELLCHARGEID"));
	 * chargesDOB.setBuyChargeId(rs.getString("BUY_CHARGE_ID")); } else
	 * if("BR".equalsIgnoreCase(flag)) {
	 * chargesDOB.setBuyChargeId(rs.getString("SELLCHARGEID")); }
	 * 
	 * //in case of freight rates
	 * chargesDOB.setBuyChargeLaneNo(rs.getString("LEG_SL_NO"));
	 * 
	 * chargesDOB.setSellBuyFlag(flag);
	 * chargesDOB.setTerminalId(rs.getString("TERMINALID"));
	 * chargesDOB.setChargeDescriptionId(rs.getString("CHARGEDESCID"));
	 * chargesDOB.setCostIncurredAt(rs.getString("COST_INCURREDAT"));
	 * chargesDOB.setBuyChargeLaneNo(rs.getString("UBOUND"));//@@Rate Lane
	 * Number
	 * 
	 * chargeInfoList = new ArrayList(); chargeInfo = new QuoteChargeInfo();
	 * chargeInfoList.add(chargeInfo);
	 * 
	 * chargesDOB.setChargeInfoList(chargeInfoList);
	 * chargesDOB.setMarginDiscountFlag(rs.getString("RATE_INDICATOR"));
	 * chargeInfo.setBreakPoint(rs.getString("CHARGESLAB"));
	 * chargeInfo.setCurrency(rs.getString("CURRENCY"));
	 * chargeInfo.setBuyRate(rs.getDouble("BUYRATE"));
	 * chargeInfo.setRecOrConSellRrate(rs.getDouble("SELLRATE"));
	 * chargeInfo.setSellChargeMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setSellChargeMarginType(rs.getString("MARGIN_TYPE"));
	 * //chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * //chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * //chargeInfo.setDiscount(); //chargeInfo.setDiscountType();
	 * if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) ||
	 * chargesDOB.getMarginDiscountFlag()==null) {
	 * chargeInfo.setMargin(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setMarginType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("BUYRATE")+(rs.getDouble("BUYRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * else { chargeInfo.setDiscount(rs.getDouble("MARGINVALUE"));
	 * chargeInfo.setDiscountType(rs.getString("MARGIN_TYPE"));
	 * 
	 * if("A".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-rs.getDouble("MARGINVALUE"); else
	 * if("P".equalsIgnoreCase(rs.getString("MARGIN_TYPE"))) sellRate =
	 * rs.getDouble("SELLRATE")-(rs.getDouble("SELLRATE")*rs.getDouble("MARGINVALUE")/100); }
	 * //chargeInfo.setBasis(rs.getString("CHARGEBASIS"));
	 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
	 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
	 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint())) {
	 * chargeInfo.setBasis("Per Shipment"); } else {
	 * chargeInfo.setBasis(rs.getString("CHARGEBASIS")); }
	 * chargeInfo.setRatio(rs.getString("DENSITY_RATIO"));
	 * chargeInfo.setSellRate(sellRate); } }
	 * 
	 * if(freightChargesList!=null) { legChargesDetails = new
	 * QuoteFreightLegSellRates();
	 * legChargesDetails.setSpotRatesType(weightBreak);
	 * legChargesDetails.setServiceLevel(serviceLevel);
	 * legChargesDetails.setFreightChargesList(freightChargesList); } }
	 * catch(SQLException sq) { Logger.error(FILE_NAME,"Error in
	 * getTiedCustomerFreightInfo"); sq.printStackTrace(); throw new
	 * SQLException(sq.toString()); } catch(Exception e) {
	 * Logger.error(FILE_NAME,"Error in getTiedCustomerFreightInfo");
	 * e.printStackTrace(); throw new SQLException(e.toString()); } finally {
	 * ConnectionUtil.closeConnection(conn,csmt,rs); } return legChargesDetails; }
	 */
	public QuoteFinalDOB getUpdatedQuoteInfo(long quoteId, String changeDesc,
			String sellBuyFlag, QuoteFinalDOB finalDOB, String quoteType)
			throws SQLException {
		Connection connection = null;
		CallableStatement csmt = null;
		PreparedStatement pStmt = null;
		ResultSet rs2 = null, rs3 = null, rs4 = null, rs5 = null, rs6 = null;

		ArrayList originChargesList = null;// to maintain the list of all
											// origin charge dobs
		ArrayList destChargesList = null;// to maintain the list of all
											// origin charge dobs
		QuoteFreightLegSellRates legDOB = null;
		QuoteFreightLegSellRates spotLegDOB = null;
		QuoteCharges chargesDOB = null;// to maintain one record that is to be
										// displayed
		QuoteCharges deliveryChargesDOB = null;
		QuoteChargeInfo chargeInfo = null;
		ArrayList chargeInfoList = null;
		ArrayList legDetails = null;
		ArrayList spotRateDetails = null;
		double sellRate = 0;
		String weightBreak = null;
		String rateType = null;
		ArrayList list_exNotes = null;
		ArrayList freightChargesList = null;
		QuoteMasterDOB masterDOB = null;
		int[] selectedFrtIndices = null;
		QuoteFinalDOB tmpFinalDOB = null;
		String flag = null;
		boolean isShipperZipCode = false;
		boolean isConsigneeZipCode = false;
		boolean isSingleShipperZone = false;
		boolean isSingleConsigneeZone = false;
		double marginValue = 0;// @@Added by kameswari for the WPBN issue-61235
		try {
			masterDOB = finalDOB.getMasterDOB();
			connection = this.getConnection();
			csmt = connection
					.prepareCall("{ call qms_quotepack_new.qms_updated_modify_quote(?,?,?,?,?,?,?) }");
			// pStmt = connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE
			// FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND
			// SELL_BUY_FLAG=?");//@@Added bby kameswari fro the WPBN issue-
			// pStmt = connection.prepareStatement("SELECT BUY_RATE,R_SELL_RATE
			// FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND
			// SELL_BUY_FLAG=?");//@@Added bby kameswari fro the WPBN issue-
			// pStmt = connection.prepareStatement("SELECT
			// BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT FROM
			// QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=?");//@@Added
			// by kameswari for the WPBN issue-
			// pStmt = connection.prepareStatement("select
			// BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT
			// ,charge_id,charge_description FROM QMS_QUOTE_RATES WHERE QUOTE_ID
			// =? AND SELL_BUY_FLAG =? and (charge_id,charge_description) in
			// (select charge_id,chargedescid from qms_charge_groupsmaster where
			// id in (select id from qms_charge_groupsmaster where
			// chargegroup_id in (select chargegroupid from
			// qms_quote_chargegroupdtl where quote_id='45663') group by id) )
			// ");//@@Added by kameswari for the WPBN issue-

			csmt.setLong(1, quoteId);
			csmt.setString(2, sellBuyFlag);
			csmt.setString(3, changeDesc);

			csmt.registerOutParameter(4, OracleTypes.CURSOR);
			csmt.registerOutParameter(5, OracleTypes.CURSOR);
			csmt.registerOutParameter(6, OracleTypes.CURSOR);
			csmt.registerOutParameter(7, OracleTypes.CURSOR);

			csmt.execute();

			rs2 = (ResultSet) csmt.getObject(4);
			rs3 = (ResultSet) csmt.getObject(5);
			rs4 = (ResultSet) csmt.getObject(6);
			rs5 = (ResultSet) csmt.getObject(7);
			// @@rs2 resultset fot fetching Freight Rates
			// @@Added by kameswari for the WPBN issue-61235
			// @@Added by Kameswari for the WPBN issue-13558
			if ("B".equalsIgnoreCase(sellBuyFlag)
					|| "S".equalsIgnoreCase(sellBuyFlag)) {
				// pStmt = connection.prepareStatement("SELECT
				// BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_DESCRIPTION
				// FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=?
				// AND (CHARGE_ID,CHARGE_DESCRIPTION) IN (SELECT
				// CHARGE_ID,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE ID
				// IN (SELECT ID FROM QMS_CHARGE_GROUPSMASTER WHERE
				// CHARGEGROUP_ID IN (SELECT CHARGEGROUPID FROM
				// QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?) GROUP BY ID) )");
				pStmt = connection
						.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_DESCRIPTION FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? AND CHARGE_DESCRIPTION=?");
				pStmt.setLong(1, quoteId);
				pStmt.setString(2, sellBuyFlag);
				pStmt.setString(3, changeDesc);
				rs6 = pStmt.executeQuery();
			} else if ("BC".equalsIgnoreCase(sellBuyFlag)
					|| "SC".equalsIgnoreCase(sellBuyFlag)) {
				pStmt = connection
						.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_AT,LINE_NO FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? ORDER BY CHARGE_AT,LINE_NO");// @@Added
																																																			// by
																																																			// kameswari
																																																			// for
																																																			// the
																																																			// WPBN
																																																			// issue-
				pStmt.setLong(1, quoteId);
				pStmt.setString(2, sellBuyFlag);
				rs6 = pStmt.executeQuery();
			} else {
				pStmt = connection
						.prepareStatement("SELECT BUY_RATE,R_SELL_RATE,SELL_BUY_FLAG,BREAK_POINT,CHARGE_AT,LINE_NO FROM QMS_QUOTE_RATES WHERE QUOTE_ID =? AND SELL_BUY_FLAG=? ORDER BY CHARGE_AT,LINE_NO");// @@Added
																																																			// by
																																																			// kameswari
																																																			// for
																																																			// the
																																																			// WPBN
																																																			// issue-
				pStmt.setLong(1, quoteId);
				pStmt.setString(2, sellBuyFlag);
				rs6 = pStmt.executeQuery();
			}
			// @@WPBN issue-13558
			int n = 0;
			String breakPoint = null;
			legDOB = null;
			// @@WPBN issue-61235

			while (rs2.next()) {

				flag = rs2.getString("SEL_BUY_FLAG");

				if (legDOB != null
						&& legDOB.getLegSerialNo() == rs2.getInt("LEG_SL_NO")) {
					if ((chargesDOB.getSellChargeId() != null && chargesDOB
							.getSellChargeId().equals(
									rs2.getString("SELLCHARGEID")))
							|| (chargesDOB.getBuyChargeId() != null && chargesDOB
									.getBuyChargeId().equals(
											rs2.getString("BUY_CHARGE_ID")))) {
						chargeInfo = new QuoteChargeInfo();
						chargeInfoList.add(chargeInfo);
						chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));

						if (rs2.getString("CURRENCY") != null
								&& rs2.getString("CURRENCY").trim().length() != 0)
							chargeInfo.setCurrency(rs2.getString("CURRENCY"));
						else
							chargeInfo.setCurrency(masterDOB
									.getTerminalCurrency());

						chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
						chargeInfo.setRecOrConSellRrate(rs2
								.getDouble("SELLRATE"));
						chargeInfo.setSellChargeMargin(rs2
								.getDouble("MARGINVALUE"));
						chargeInfo.setSellChargeMarginType(rs2
								.getString("MARGIN_TYPE"));
						chargeInfo.setRateIndicator(rs2
								.getString("RATE_INDICATOR"));

						if ("A FREIGHT RATE".equalsIgnoreCase(rs2
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("FREIGHT RATE");
						else if ("C.P.S.S".equalsIgnoreCase(rs2
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("P.S.S");
						else
							chargeInfo
									.setRateDescription(rs2
											.getString("RATE_DESCRIPTION") != null ? rs2
											.getString("RATE_DESCRIPTION")
											: "FREIGHT RATE");// @@Added by
																// Kameswari for
																// Surcharge
																// Enhancemenst
						if ("M".equalsIgnoreCase(chargesDOB
								.getMarginDiscountFlag())
								|| chargesDOB.getMarginDiscountFlag() == null) {

							chargeInfo.setMarginType(rs2
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {
								// @@Modified by kameswari for the WPBN
								// issue-61235

								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {

									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("BUYRATE")
												+ rs2.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											logger
													.info("buyrate M :"
															+ rs6
																	.getDouble("BUY_RATE"));
											logger
													.info("margin Value M: "
															+ rs2
																	.getDouble("MARGINVALUE"));

											sellRate = rs6
													.getDouble("BUY_RATE")
													+ rs2
															.getDouble("MARGINVALUE");
											marginValue = sellRate
													- rs2.getDouble("BUYRATE");
											logger.info("sellRate M: "
													+ sellRate);
											logger.info("marginValue M: "
													+ marginValue);
											chargeInfo.setMargin(marginValue);
										}
									}
								}

								else {
									sellRate = rs2.getDouble("BUYRATE")
											+ rs2.getDouble("MARGINVALUE");
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							}

							else if ("P".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {

								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("BUYRATE")
												+ (rs2.getDouble("BUYRATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											logger
													.info("buyrate P :"
															+ rs6
																	.getDouble("BUY_RATE"));
											logger
													.info("margin Value P: "
															+ rs2
																	.getDouble("MARGINVALUE"));

											sellRate = rs6
													.getDouble("BUY_RATE")
													+ (rs6
															.getDouble("BUY_RATE")
															* rs2
																	.getDouble("MARGINVALUE") / 100);
											marginValue = (sellRate - rs2
													.getDouble("BUYRATE"))
													* 100
													/ rs2.getDouble("BUYRATE");
											logger.info("sellRate P: "
													+ sellRate);
											logger.info("marginValue P: "
													+ marginValue);

											chargeInfo.setMargin(marginValue);

										}
									}
								} else {
									sellRate = rs2.getDouble("BUYRATE")
											+ (rs2.getDouble("BUYRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							}

						} else {
							chargeInfo.setDiscountType(rs2
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {
								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("SELLRATE")
												- rs2.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											logger
													.info("buyrate 1 :"
															+ rs6
																	.getDouble("R_SELL_RATE"));
											logger
													.info("margin Value 1: "
															+ rs2
																	.getDouble("MARGINVALUE"));

											sellRate = rs6
													.getDouble("R_SELL_RATE")
													- rs2
															.getDouble("MARGINVALUE");
											marginValue = rs2
													.getDouble("SELLRATE")
													- sellRate;

											logger.info("sellRate 1: "
													+ sellRate);
											logger.info("marginValue 1: "
													+ marginValue);

											chargeInfo.setDiscount(marginValue);
										}
									}
								} else {

									sellRate = rs2.getDouble("SELLRATE")
											- rs2.getDouble("MARGINVALUE");
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							}

							else if ("P".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {
								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("SELLRATE")
												- (rs2.getDouble("SELLRATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);

										chargeInfo.setDiscount(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {

											logger
													.info("buyrate 2 :"
															+ rs6
																	.getDouble("R_SELL_RATE"));
											logger
													.info("margin Value 2: "
															+ rs2
																	.getDouble("MARGINVALUE"));

											sellRate = rs6
													.getDouble("R_SELL_RATE")
													- (rs6
															.getDouble("R_SELL_RATE")
															* rs2
																	.getDouble("MARGINVALUE") / 100);
											marginValue = (rs2
													.getDouble("SELLRATE") - sellRate)
													* 100
													/ rs2.getDouble("SELLRATE");
											logger.info("sellRate 2: "
													+ sellRate);
											logger.info("marginValue 2: "
													+ marginValue);

											chargeInfo.setDiscount(marginValue);
										}
									}

								} else {

									sellRate = rs2.getDouble("SELLRATE")
											- (rs2.getDouble("SELLRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));

								}
								// @@WPBN issue-61235
							}
						}
						weightBreak = rs2.getString("WEIGHT_BREAK");
						rateType = rs2.getString("RATE_TYPE");
						// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
						/*
						 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * ("SLAB".equalsIgnoreCase(weightBreak) &&
						 * "FLAT".equalsIgnoreCase(rateType)) ||
						 * ("BOTH".equalsIgnoreCase(rateType) &&
						 * "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) {
						 * chargeInfo.setBasis("Per Shipment"); } else {
						 * //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
						 * if("1".equalsIgnoreCase(rs2.getString("SHMODE")) &&
						 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per ULD"); } else
						 * if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) &&
						 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per Container"); } else if("Per
						 * Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Kilogram"); } else if("Per
						 * Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Pound"); } else if("Per
						 * CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Cubic Meter"); } else
						 * if("Per
						 * CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Cubic Feet"); } else
						 * chargeInfo.setBasis(rs2.getString("CHARGEBASIS")); }
						 */

						if (!("FREIGHT RATE".equalsIgnoreCase(chargeInfo
								.getRateDescription()))) {
							if (chargeInfo.getBreakPoint().equalsIgnoreCase(
									"CSF")) {
								chargeInfo.setBasis("Per Shipment");
							} else if (chargeInfo.getBreakPoint().endsWith(
									"CAF")
									|| chargeInfo.getBreakPoint().endsWith(
											"caf")
									|| chargeInfo.getBreakPoint().endsWith(
											"BAF")
									|| chargeInfo.getBreakPoint().endsWith(
											"baf")
									|| chargeInfo.getBreakPoint().endsWith(
											"CSF")
									|| chargeInfo.getBreakPoint().endsWith(
											"csf")
									|| chargeInfo.getBreakPoint().endsWith(
											"PSS")
									|| chargeInfo.getBreakPoint().endsWith(
											"pss")) {
								// if(chargeInfo.getBreakPoint().endsWith("CAF")
								// //@@Modified for the WPBN issue on 16/12/08
								if (chargeInfo.getBreakPoint().endsWith("CAF")
										|| chargeInfo.getBreakPoint().endsWith(
												"caf")) {
									chargeInfo.setBasis("Percent of Freight");
								} else {
									chargeInfo.setBasis("Per Container");
								}
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("BAFMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("CAFMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("PSSMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("FSMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSMIN")
											
							        || chargeInfo.getBreakPoint()
									.equalsIgnoreCase("FFMIN")
									|| chargeInfo.getBreakPoint()
									.endsWith("MIN")
									||chargeInfo.getBreakPoint()
									.endsWith("BASIC")) {
								chargeInfo.setBasis("Per Shipment");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("BAFM3")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("CAF%")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("PSSM3")) {
								if (chargeInfo.getBreakPoint()
										.equalsIgnoreCase("BAFM3")
										|| chargeInfo.getBreakPoint()
												.equalsIgnoreCase("PSSM3")) {
									// chargeInfo.setBasis("per Cubic Meter");
									chargeInfo
											.setBasis("Per Weight Measurement");
								} else {
									chargeInfo.setBasis("Percent of Freight");
								}
							}else if("FLAT".equalsIgnoreCase(chargeInfo.getBreakPoint())
									 || chargeInfo.getBreakPoint().endsWith("FLAT")){
								if("1".equalsIgnoreCase(rs2.getString("SHMODE")))
									chargeInfo.setBasis("Per Kilogram");
								if("2".equalsIgnoreCase(rs2.getString("SHMODE")))
									chargeInfo.setBasis("Per Weight Measurement");
										 }

							else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("FSBASIC")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSBASIC")) {
								chargeInfo.setBasis("Per Shipment");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("FSKG")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSKG")) {
								chargeInfo.setBasis("Per Kg");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("SURCHARGE")) {
								chargeInfo.setBasis("Percent of Freight");
							}
						} // @@Surcharge Enhancements
						else {
							// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) // MODIFIED
																			// FOR
																			// 183812
							{
								chargeInfo.setBasis("Per Shipment");
							} else {
								if ("1".equalsIgnoreCase(rs2
										.getString("SHMODE"))
										&& "LIST".equalsIgnoreCase(rs2
												.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per ULD");
								} else if (("2".equalsIgnoreCase(rs2
										.getString("SHMODE")) || "4"
										.equalsIgnoreCase(rs2
												.getString("SHMODE")))
										&& "LIST".equalsIgnoreCase(rs2
												.getString("WEIGHT_BREAK"))) {
									// @@Modified by Kameswari for the WPBN
									// issue on 16/12/08
									// if(chargeInfo.getBreakPoint().endsWith("CAF"))
									if (chargeInfo.getBreakPoint().endsWith("CAF")
									|| chargeInfo.getBreakPoint().endsWith("caf") ) {
										chargeInfo
												.setBasis(" Percent of Freight");
									} else {
										chargeInfo.setBasis("Per Container");
									}
								 }else if("FLAT".equalsIgnoreCase(chargeInfo.getBreakPoint())
										 || chargeInfo.getBreakPoint().endsWith("FLAT")){
						if("1".equalsIgnoreCase(rs2.getString("SHMODE")))
							chargeInfo.setBasis("Per Kilogram");
						if("2".equalsIgnoreCase(rs2.getString("SHMODE")))
							chargeInfo.setBasis("Per Weight Measurement");
								 }
								else if ("Per Kg".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs2
											.getString("CHARGEBASIS"));
							}
						}
						chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));

						chargeInfo.setSellRate(sellRate);
						chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));
					}

					else {
						chargesDOB = new QuoteCharges();
						freightChargesList.add(chargesDOB);

						chargesDOB.setBuyChargeId(rs2
								.getString("BUY_CHARGE_ID"));
						chargesDOB.setSellChargeId(rs2
								.getString("SELLCHARGEID"));
						chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
						// @@Added by Kameswari for the WPBN issue-146448 on
						// 24/12/08
						chargesDOB.setFrequency(rs2.getString("FREQUENCY"));
						chargesDOB.setCarrier(rs2.getString("CARRIER"));
						chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
						chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
						chargesDOB.setFrequencyChecked(rs2
								.getString("FREQUENCY_CHECKED"));
						chargesDOB.setCarrierChecked(rs2
								.getString("CARRIER_CHECKED"));
						chargesDOB.setTransitTimeChecked(rs2
								.getString("TRANSITTIME_CHECKED"));
						chargesDOB.setRateValidityChecked(rs2
								.getString("RATEVALIDITY_CHECKED"));
						chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
						chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
						chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
						chargesDOB.setMarginDiscountFlag(rs2
								.getString("MARGIN_DISCOUNT_FLAG"));
						chargesDOB.setSelectedFlag(rs2
								.getString("SELECTED_FLAG"));
						chargesDOB.setSellBuyFlag(flag);
						chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
						chargesDOB.setChargeDescriptionId("Freight Rate");
						chargesDOB.setCostIncurredAt(rs2
								.getString("COST_INCURREDAT"));
						chargesDOB
								.setConsoleType(rs2.getString("CONSOLE_TYPE"));
						chargeInfoList = new ArrayList();
						chargeInfo = new QuoteChargeInfo();
						chargeInfoList.add(chargeInfo);

						chargesDOB.setChargeInfoList(chargeInfoList);

						chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
						if ("A FREIGHT RATE".equalsIgnoreCase(rs2
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("FREIGHT RATE");
						else if ("C.P.S.S".equalsIgnoreCase(rs2
								.getString("RATE_DESCRIPTION")))
							chargeInfo.setRateDescription("P.S.S");
						else
							chargeInfo
									.setRateDescription(rs2
											.getString("RATE_DESCRIPTION") != null ? rs2
											.getString("RATE_DESCRIPTION")
											: "FREIGHT RATE");// @@Added by
																// Kameswari for
																// Surcharge
																// Enhancemenst

						if (rs2.getString("CURRENCY") != null
								&& rs2.getString("CURRENCY").trim().length() != 0)
							chargeInfo.setCurrency(rs2.getString("CURRENCY"));
						else
							chargeInfo.setCurrency(masterDOB
									.getTerminalCurrency());

						chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
						chargeInfo.setRecOrConSellRrate(rs2
								.getDouble("SELLRATE"));
						chargeInfo.setSellChargeMargin(rs2
								.getDouble("MARGINVALUE"));
						chargeInfo.setSellChargeMarginType(rs2
								.getString("MARGIN_TYPE"));
						chargeInfo.setRateIndicator(rs2
								.getString("RATE_INDICATOR"));

						weightBreak = rs2.getString("WEIGHT_BREAK");
						rateType = rs2.getString("RATE_TYPE");
						// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
						/*
						 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
						 * ("SLAB".equalsIgnoreCase(weightBreak) &&
						 * "FLAT".equalsIgnoreCase(rateType)) ||
						 * ("BOTH".equalsIgnoreCase(rateType) &&
						 * "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) {
						 * chargeInfo.setBasis("Per Shipment"); } else {
						 * //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
						 * if("1".equalsIgnoreCase(rs2.getString("SHMODE")) &&
						 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per ULD"); } else
						 * if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) &&
						 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per Container"); } else if("Per
						 * Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Kilogram"); } else if("Per
						 * Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Pound"); } else if("Per
						 * CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Cubic Meter"); } else
						 * if("Per
						 * CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
						 * chargeInfo.setBasis("Per Cubic Feet"); } else
						 * chargeInfo.setBasis(rs2.getString("CHARGEBASIS")); }
						 */
						if (!("FREIGHT RATE".equalsIgnoreCase(chargeInfo
								.getRateDescription()))) {
							/*
							 * if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
							 * ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS"))
							 */// @@Modified
																														// by
																														// Kameswari
																														// for
																														// the
																														// WPBN
																														// issue
																														// on
																														// 16/12/08
							if (chargeInfo.getBreakPoint().equalsIgnoreCase(
									"CSF")) {
								chargeInfo.setBasis("Per Shipment");
							}

							else if (chargeInfo.getBreakPoint().endsWith("CAF")
									|| chargeInfo.getBreakPoint().endsWith(
											"BAF")
									|| chargeInfo.getBreakPoint().endsWith(
											"CSF")
									|| chargeInfo.getBreakPoint().endsWith(
											"PSS")
									|| chargeInfo.getBreakPoint().endsWith(
											"caf")
									|| chargeInfo.getBreakPoint().endsWith(
											"baf")
									|| chargeInfo.getBreakPoint().endsWith(
											"csf")
									|| chargeInfo.getBreakPoint().endsWith(
											"pss")) {
								if (chargeInfo.getBreakPoint().endsWith("CAF")
										|| chargeInfo.getBreakPoint().endsWith(
												"caf")) {
									chargeInfo.setBasis("Percent of Freight");
								} else {
									chargeInfo.setBasis("Per Container");
								}
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("BAFMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("CAFMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("PSSMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("FSMIN")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSMIN")) {
								chargeInfo.setBasis("Per Shipment");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("BAFM3")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("CAF%")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("PSSM3")) {
								if (chargeInfo.getBreakPoint()
										.equalsIgnoreCase("BAFM3")
										|| chargeInfo.getBreakPoint()
												.equalsIgnoreCase("PSSM3")) {
									// chargeInfo.setBasis("per Cubic Meter");
									chargeInfo
											.setBasis("Per Weight Measurement");
								} else {
									chargeInfo.setBasis("Percent of Freight");
								}
							}

							else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("FSBASIC")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSBASIC")) {
								chargeInfo.setBasis("Per Shipment");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("FSKG")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("SSKG")) {
								chargeInfo.setBasis("Per Kg");
							} else if (chargeInfo.getBreakPoint()
									.equalsIgnoreCase("SURCHARGE")) {
								chargeInfo.setBasis("Percent of Freight");
							}
						}
						// @@Surcharge Enhancements
						else {
							// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator())))// MODIFIED
																			// FOR
																			// 183812
							{
								chargeInfo.setBasis("Per Shipment");
							}

							else {
								if ("1".equalsIgnoreCase(rs2
										.getString("SHMODE"))
										&& "LIST".equalsIgnoreCase(rs2
												.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per ULD");
								} else if (("2".equalsIgnoreCase(rs2
										.getString("SHMODE")) || "4"
										.equalsIgnoreCase(rs2
												.getString("SHMODE")))
										&& "LIST".equalsIgnoreCase(rs2
												.getString("WEIGHT_BREAK"))) {
									if (chargeInfo.getBreakPoint().endsWith(
											"CAF")
											|| chargeInfo.getBreakPoint()
													.endsWith("caf")) {
										chargeInfo
												.setBasis(" Percent of Freight");
									} else {
										chargeInfo.setBasis("Per Container");
									}
								} else if ("Per Kg".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs2
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs2
											.getString("CHARGEBASIS"));
							}
						}
						chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));

						/*
						 * if("A FREIGHT
						 * RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setRateDescription("FREIGHT RATE"); else
						 * if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
						 * chargeInfo.setRateDescription("P.S.S"); else
						 * chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT
						 * RATE");//@@Added by Kameswari for Surcharge
						 * Enhancemenst
						 */
						if ("M".equalsIgnoreCase(chargesDOB
								.getMarginDiscountFlag())
								|| chargesDOB.getMarginDiscountFlag() == null) {
							chargeInfo.setMarginType(rs2
									.getString("MARGIN_TYPE"));
							if ("A".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {

								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("BUYRATE")
												+ rs2.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											sellRate = rs6
													.getDouble("BUY_RATE")
													+ rs2
															.getDouble("MARGINVALUE");
											marginValue = sellRate
													- rs2.getDouble("BUYRATE");
											chargeInfo.setMargin(marginValue);
										}
									}
								} else {
									sellRate = rs2.getDouble("BUYRATE")
											+ rs2.getDouble("MARGINVALUE");
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							} else if ("P".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {

								// @@Modified by kameswari for the WPBN
								// issue-61235
								// /
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("BUYRATE")
												+ (rs2.getDouble("BUYRATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											sellRate = rs6
													.getDouble("BUY_RATE")
													+ (rs6
															.getDouble("BUY_RATE")
															* rs2
																	.getDouble("MARGINVALUE") / 100);
											marginValue = (sellRate - rs2
													.getDouble("BUYRATE"))
													* 100
													/ rs2.getDouble("BUYRATE");
											chargeInfo.setMargin(marginValue);
										}
									}
								} else {
									sellRate = rs2.getDouble("BUYRATE")
											+ (rs2.getDouble("BUYRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							}

						} else {

							chargeInfo.setDiscountType(rs2
									.getString("MARGIN_TYPE"));

							if ("A".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {
								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("SELLRATE")
												- rs2.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											sellRate = rs6
													.getDouble("R_SELL_RATE")
													- rs2
															.getDouble("MARGINVALUE");
											marginValue = rs2
													.getDouble("SELLRATE")
													- sellRate;
											chargeInfo.setDiscount(marginValue);
										}
									}
								} else {
									sellRate = rs2.getDouble("SELLRATE")
											- rs2.getDouble("MARGINVALUE");
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							} else if ("P".equalsIgnoreCase(rs2
									.getString("MARGIN_TYPE"))) {

								// @@Modified by kameswari for the WPBN
								// issue-61235
								// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
								if (flag.equalsIgnoreCase(sellBuyFlag)) {
									if ("modifiedQuote"
											.equalsIgnoreCase(quoteType)) {
										sellRate = rs2.getDouble("SELLRATE")
												- (rs2.getDouble("SELLRATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setDiscount(rs2
												.getDouble("MARGINVALUE"));
									} else {
										if (rs6.next()) {
											sellRate = rs6
													.getDouble("R_SELL_RATE")
													- (rs6
															.getDouble("R_SELL_RATE")
															* rs2
																	.getDouble("MARGINVALUE") / 100);
											marginValue = (rs2
													.getDouble("SELLRATE") - sellRate)
													* 100
													/ rs2.getDouble("SELLRATE");
											chargeInfo.setDiscount(marginValue);
										}
									}
								} else {
									sellRate = rs2.getDouble("SELLRATE")
											- (rs2.getDouble("SELLRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));
								}
								// @@WPBN issue-61235
							}
						}
						chargeInfo.setSellRate(sellRate);
						chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));

					}
				} else {
					ArrayList ratesList = new ArrayList();
					QuoteFreightRSRCSRDOB freightDOB = new QuoteFreightRSRCSRDOB();

					if (legDetails == null)
						legDetails = new ArrayList();

					legDOB = new QuoteFreightLegSellRates();
					selectedFrtIndices = new int[1];
					selectedFrtIndices[0] = 0;

					legDetails.add(legDOB);

					freightChargesList = new ArrayList();

					legDOB.setFreightChargesList(freightChargesList);
					legDOB
							.setSelectedFreightChargesListIndices(selectedFrtIndices);
					legDOB.setOrigin(rs2.getString("ORG"));
					legDOB.setDestination(rs2.getString("DEST"));
					legDOB.setLegSerialNo(rs2.getInt("LEG_SL_NO"));
					legDOB.setShipmentMode(rs2.getInt("SHMODE"));
					legDOB.setServiceLevel(rs2.getString("SRV_LEVEL"));

					if ("SBR".equalsIgnoreCase(flag)
							&& finalDOB.getLegDetails() != null) {
						spotRateDetails = finalDOB.getLegDetails();
						spotLegDOB = (QuoteFreightLegSellRates) spotRateDetails
								.get(rs2.getInt("LANE_NO"));// @@Refer
															// getMasterInfo()
															// in
															// QMSQuoteSessionBean;
						legDOB.setDensityRatio(spotLegDOB.getDensityRatio());
						legDOB.setUom(spotLegDOB.getUom());
						legDOB.setSpotRatesType(spotLegDOB.getSpotRatesType());
						legDOB.setWeightBreaks(spotLegDOB.getWeightBreaks());
						legDOB.setCurrency(spotLegDOB.getCurrency());// @@added
																		// by
																		// kameswari
																		// for
																		// the
																		// WPBN
																		// issue-30908
						legDOB.setSpotRateDetails(spotLegDOB
								.getSpotRateDetails());
						legDOB.setSpotRatesFlag(spotLegDOB.isSpotRatesFlag());
					}

					freightDOB.setServiceLevelId(legDOB.getServiceLevel());
					freightDOB
							.setWeightBreakType(rs2.getString("WEIGHT_BREAK"));

					ratesList.add(freightDOB);
					legDOB.setRates(ratesList);

					legDOB.setSelectedFreightSellRateIndex(0);

					chargesDOB = new QuoteCharges();

					freightChargesList.add(chargesDOB);

					chargesDOB.setChargeDescriptionId("Freight Rate");
					chargesDOB.setBuyChargeId(rs2.getString("BUY_CHARGE_ID"));
					chargesDOB.setSellChargeId(rs2.getString("SELLCHARGEID"));
					chargesDOB.setSelectedFlag(rs2.getString("SELECTED_FLAG"));
					chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
					chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
					chargesDOB.setMarginDiscountFlag(rs2
							.getString("MARGIN_DISCOUNT_FLAG"));
					// @@Added by Kameswari for the WPBN issue-146448 on
					// 24/12/08
					chargesDOB.setFrequency(rs2.getString("FREQUENCY"));
					chargesDOB.setCarrier(rs2.getString("CARRIER"));
					chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
					chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
					chargesDOB.setFrequencyChecked(rs2
							.getString("FREQUENCY_CHECKED"));
					chargesDOB.setCarrierChecked(rs2
							.getString("CARRIER_CHECKED"));
					chargesDOB.setTransitTimeChecked(rs2
							.getString("TRANSITTIME_CHECKED"));
					chargesDOB.setRateValidityChecked(rs2
							.getString("RATEVALIDITY_CHECKED"));
					chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
					chargesDOB.setSellBuyFlag(flag);

					chargesDOB.setCostIncurredAt(rs2
							.getString("COST_INCURREDAT"));
					chargesDOB.setConsoleType(rs2.getString("CONSOLE_TYPE"));
					chargeInfoList = new ArrayList();
					chargeInfo = new QuoteChargeInfo();
					chargeInfoList.add(chargeInfo);

					chargesDOB.setChargeInfoList(chargeInfoList);

					chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
					if (rs2.getString("CURRENCY") != null
							&& rs2.getString("CURRENCY").trim().length() != 0)
						chargeInfo.setCurrency(rs2.getString("CURRENCY"));
					else
						chargeInfo.setCurrency(masterDOB.getTerminalCurrency());

					chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
					chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
					chargeInfo
							.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
					chargeInfo.setSellChargeMarginType(rs2
							.getString("MARGIN_TYPE"));
					chargeInfo
							.setRateIndicator(rs2.getString("RATE_INDICATOR"));
					weightBreak = rs2.getString("WEIGHT_BREAK");
					rateType = rs2.getString("RATE_TYPE");
					// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
					/*
					 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
					 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
					 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
					 * ("SLAB".equalsIgnoreCase(weightBreak) &&
					 * "FLAT".equalsIgnoreCase(rateType)) ||
					 * ("BOTH".equalsIgnoreCase(rateType) &&
					 * "F".equalsIgnoreCase(chargeInfo.getRateIndicator()))) {
					 * chargeInfo.setBasis("Per Shipment"); } else {
					 * //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
					 * if("1".equalsIgnoreCase(rs2.getString("SHMODE")) &&
					 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
					 * chargeInfo.setBasis("Per ULD"); } else
					 * if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) &&
					 * "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))) {
					 * chargeInfo.setBasis("Per Container"); } else if("Per
					 * Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
					 * chargeInfo.setBasis("Per Kilogram"); } else if("Per
					 * Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
					 * chargeInfo.setBasis("Per Pound"); } else if("Per
					 * CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
					 * chargeInfo.setBasis("Per Cubic Meter"); } else if("Per
					 * CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS"))) {
					 * chargeInfo.setBasis("Per Cubic Feet"); } else
					 * chargeInfo.setBasis(rs2.getString("CHARGEBASIS")); }
					 */
					if ("A FREIGHT RATE".equalsIgnoreCase(rs2
							.getString("RATE_DESCRIPTION")))
						chargeInfo.setRateDescription("FREIGHT RATE");
					else if ("C.P.S.S".equalsIgnoreCase(rs2
							.getString("RATE_DESCRIPTION")))
						chargeInfo.setRateDescription("P.S.S");
					else
						chargeInfo
								.setRateDescription(rs2
										.getString("RATE_DESCRIPTION") != null ? rs2
										.getString("RATE_DESCRIPTION")
										: "FREIGHT RATE");// @@Added by
															// Kameswari for
															// Surcharge
															// Enhancemenst

					if (!("FREIGHT RATE".equalsIgnoreCase(chargeInfo
							.getRateDescription()))) {

						if (chargeInfo.getBreakPoint().equalsIgnoreCase("CSF")) {
							chargeInfo.setBasis("Per Shipment");
						} else if (chargeInfo.getBreakPoint().endsWith("CAF")
								|| chargeInfo.getBreakPoint().endsWith("BAF")
								|| chargeInfo.getBreakPoint().endsWith("CSF")
								|| chargeInfo.getBreakPoint().endsWith("PSS")
								|| chargeInfo.getBreakPoint().endsWith("caf")
								|| chargeInfo.getBreakPoint().endsWith("baf")
								|| chargeInfo.getBreakPoint().endsWith("csf")
								|| chargeInfo.getBreakPoint().endsWith("pss")) {
							if (chargeInfo.getBreakPoint().endsWith("CAF")
									|| chargeInfo.getBreakPoint().endsWith(
											"caf")) {
								chargeInfo.setBasis("Percent of Freight");
							} else {
								chargeInfo.setBasis("Per Container");
							}
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"BAFMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"CAFMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"FSMIN")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSMIN")) {
							chargeInfo.setBasis("Per Shipment");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"BAFM3")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"CAF%")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"PSSM3")) {
							if (chargeInfo.getBreakPoint().equalsIgnoreCase(
									"BAFM3")
									|| chargeInfo.getBreakPoint()
											.equalsIgnoreCase("PSSM3")) {
								// chargeInfo.setBasis("per Cubic Meter");
								chargeInfo.setBasis("Per Weight Measurement");

							} else {
								chargeInfo.setBasis("Percent of Freight");
							}
						}

						else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"FSBASIC")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSBASIC")) {
							chargeInfo.setBasis("Per Shipment");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"FSKG")
								|| chargeInfo.getBreakPoint().equalsIgnoreCase(
										"SSKG")) {
							chargeInfo.setBasis("Per Kg");
						} else if (chargeInfo.getBreakPoint().equalsIgnoreCase(
								"SURCHARGE")) {
							chargeInfo.setBasis("Percent of Freight");
						}
					}
					// @@Surcharge Enhancements
					else {
						// chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
						if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
								|| "MIN".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| "MAX".equalsIgnoreCase(chargeInfo
										.getBreakPoint())
								|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
										.equalsIgnoreCase(rateType))
								|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
										.equalsIgnoreCase(chargeInfo
												.getRateIndicator())))// MODIFIED
																		// FOR
																		// 183812
						{
							chargeInfo.setBasis("Per Shipment");
						} else {
							if ("1".equalsIgnoreCase(rs2.getString("SHMODE"))
									&& "LIST".equalsIgnoreCase(rs2
											.getString("WEIGHT_BREAK"))) {
								chargeInfo.setBasis("Per ULD");
							} else if (("2".equalsIgnoreCase(rs2
									.getString("SHMODE")) || "4"
									.equalsIgnoreCase(rs2.getString("SHMODE")))
									&& "LIST".equalsIgnoreCase(rs2
											.getString("WEIGHT_BREAK"))) {
								if (chargeInfo.getBreakPoint().endsWith("CAF")
										|| chargeInfo.getBreakPoint().endsWith(
												"caf")) {
									chargeInfo.setBasis("Percent of Freight");
								} else {
									chargeInfo.setBasis("Per Container");
								}
							} else if ("Per Kg".equalsIgnoreCase(rs2
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Kilogram");
							} else if ("Per Lb".equalsIgnoreCase(rs2
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Pound");
							} else if ("Per CBM".equalsIgnoreCase(rs2
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Meter");
							} else if ("Per CFT".equalsIgnoreCase(rs2
									.getString("CHARGEBASIS"))) {
								chargeInfo.setBasis("Per Cubic Feet");
							} else
								chargeInfo.setBasis(rs2
										.getString("CHARGEBASIS"));
						}
					}
					chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));

					/*
					 * if("A FREIGHT
					 * RATE".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
					 * chargeInfo.setRateDescription("FREIGHT RATE"); else
					 * if("C.P.S.S".equalsIgnoreCase(rs2.getString("RATE_DESCRIPTION")))
					 * chargeInfo.setRateDescription("P.S.S"); else
					 * chargeInfo.setRateDescription(rs2.getString("RATE_DESCRIPTION")!=null?rs2.getString("RATE_DESCRIPTION"):"FREIGHT
					 * RATE");//@@Added by Kameswari for Surcharge Enhancemenst
					 */

					if ("M"
							.equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())
							|| chargesDOB.getMarginDiscountFlag() == null) {

						chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));

						if ("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)) {
								if ("modifiedQuote".equalsIgnoreCase(quoteType)) {
									sellRate = rs2.getDouble("BUYRATE")
											+ rs2.getDouble("MARGINVALUE");
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												+ rs2.getDouble("MARGINVALUE");
										marginValue = sellRate
												- rs2.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);
									}
								}
							} else {
								sellRate = rs2.getDouble("BUYRATE")
										+ rs2.getDouble("MARGINVALUE");
								chargeInfo.setMargin(rs2
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						} else if ("P".equalsIgnoreCase(rs2
								.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)) {
								if ("modifiedQuote".equalsIgnoreCase(quoteType)) {
									sellRate = rs2.getDouble("BUYRATE")
											+ (rs2.getDouble("BUYRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setMargin(rs2
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												+ (rs6.getDouble("BUY_RATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);
										marginValue = (sellRate - rs2
												.getDouble("BUYRATE"))
												* 100
												/ rs2.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);
									}
								}
							} else {
								sellRate = rs2.getDouble("BUYRATE")
										+ (rs2.getDouble("BUYRATE")
												* rs2.getDouble("MARGINVALUE") / 100);
								chargeInfo.setMargin(rs2
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						}

					} else {
						chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
						chargeInfo
								.setDiscountType(rs2.getString("MARGIN_TYPE"));

						if ("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)) {
								if ("modifiedQuote".equalsIgnoreCase(quoteType)) {
									sellRate = rs2.getDouble("SELLRATE")
											- rs2.getDouble("MARGINVALUE");
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- rs2.getDouble("MARGINVALUE");
										marginValue = rs2.getDouble("SELLRATE")
												- sellRate;
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs2.getDouble("SELLRATE")
										- rs2.getDouble("MARGINVALUE");
								chargeInfo.setDiscount(rs2
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						} else if ("P".equalsIgnoreCase(rs2
								.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs2.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)) {
								if ("modifiedQuote".equalsIgnoreCase(quoteType)) {
									sellRate = rs2.getDouble("SELLRATE")
											- (rs2.getDouble("SELLRATE")
													* rs2
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setDiscount(rs2
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- (rs6.getDouble("R_SELL_RATE")
														* rs2
																.getDouble("MARGINVALUE") / 100);
										marginValue = (rs2
												.getDouble("SELLRATE") - sellRate)
												* 100
												/ rs2.getDouble("SELLRATE");
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs2.getDouble("SELLRATE")
										- (rs2.getDouble("SELLRATE")
												* rs2.getDouble("MARGINVALUE") / 100);
								chargeInfo.setDiscount(rs2
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						}
					}

					chargeInfo.setSellRate(sellRate);

					chargeInfo.setLineNumber(rs2.getInt("LINE_NO"));

				}
			}
			if (legDetails != null)
				finalDOB.setLegDetails(legDetails);

			/*
			 * chargesDOB = null; //rs3 ResultSet is used for getting Charges
			 * while(rs3.next()) { if(chargesDOB!=null && (
			 * (chargesDOB.getBuyChargeId()!=null &&
			 * chargesDOB.getBuyChargeId().equals(rs3.getString("BUY_CHARGE_ID"))) ||
			 * (chargesDOB.getSellChargeId()!=null &&
			 * chargesDOB.getSellChargeId().equals(rs3.getString("SELLCHARGEID"))) ) ) {
			 * chargeInfo = new QuoteChargeInfo();
			 * 
			 * chargeInfoList.add(chargeInfo);
			 * 
			 * chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
			 * if(rs3.getString("CURRENCY")!=null &&
			 * rs3.getString("CURRENCY").trim().length()!=0)
			 * chargeInfo.setCurrency(rs3.getString("CURRENCY")); else
			 * chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
			 * 
			 * chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
			 * chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
			 * chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
			 * 
			 * if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) ||
			 * chargesDOB.getMarginDiscountFlag()==null) {
			 * chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
			 * 
			 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); else
			 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100); }
			 * else { chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
			 * 
			 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE"); else
			 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100); }
			 * //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
			 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
			 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
			 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint())) {
			 * chargeInfo.setBasis("Per Shipment"); } else {
			 * /*if("1".equalsIgnoreCase(rs3.getString("SHMODE")) &&
			 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
			 * chargeInfo.setBasis("Per ULD"); } else
			 * if(("2".equalsIgnoreCase(rs3.getString("SHMODE"))||"4".equalsIgnoreCase(rs3.getString("SHMODE"))) &&
			 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
			 * chargeInfo.setBasis("Per Container"); } else
			 */
			/*
			 * if("Per Kg".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Kilogram"); } else if("Per
			 * Lb".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Pound"); } else if("Per
			 * CFT".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Cubic Meter"); } else if("Per
			 * CBM".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Cubic Feet"); } else
			 * chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
			 * 
			 * weightBreak = rs3.getString("WEIGHT_BREAK");
			 * 
			 * if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) ||
			 * weightBreak.endsWith("%"))) chargeInfo.setPercentValue(true); }
			 * chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
			 * 
			 * chargeInfo.setSellRate(sellRate);
			 * chargeInfo.setLineNumber(rs3.getInt("LINE_NO"));
			 *  } else { chargesDOB = new QuoteCharges();
			 * 
			 * if("Origin".equalsIgnoreCase(rs3.getString("COST_INCURREDAT"))) {
			 * if(originChargesList==null) originChargesList = new ArrayList();
			 * originChargesList.add(chargesDOB); } else
			 * if("Destination".equalsIgnoreCase(rs3.getString("COST_INCURREDAT"))) {
			 * if(destChargesList==null) destChargesList = new ArrayList();
			 * destChargesList.add(chargesDOB); }
			 * 
			 * chargesDOB.setBuyChargeId(rs3.getString("BUY_CHARGE_ID"));
			 * chargesDOB.setSellChargeId(rs3.getString("SELLCHARGEID"));
			 * chargesDOB.setSellBuyFlag(rs3.getString("SEL_BUY_FLAG"));
			 * 
			 * chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
			 * chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
			 * chargesDOB.setMarginDiscountFlag(rs3.getString("MARGIN_DISCOUNT_FLAG"));
			 * chargesDOB.setChargeDescriptionId(rs3.getString("CHARGEDESCID"));
			 * chargesDOB.setInternalName(rs3.getString("INT_CHARGE_NAME"));
			 * chargesDOB.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
			 * chargesDOB.setCostIncurredAt(rs3.getString("COST_INCURREDAT"));
			 * chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));
			 * 
			 * if(changeDesc.equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
			 * finalDOB.setEmailChargeName(chargesDOB.getExternalName());
			 * 
			 * chargeInfoList = new ArrayList(); chargeInfo = new
			 * QuoteChargeInfo(); chargeInfoList.add(chargeInfo);
			 * 
			 * chargesDOB.setChargeInfoList(chargeInfoList);
			 * 
			 * chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
			 * if(rs3.getString("CURRENCY")!=null &&
			 * rs3.getString("CURRENCY").trim().length()!=0)
			 * chargeInfo.setCurrency(rs3.getString("CURRENCY")); else
			 * chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
			 * chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
			 * chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
			 * chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
			 * //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
			 * if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
			 * "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) ||
			 * "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint())) {
			 * chargeInfo.setBasis("Per Shipment"); } else {
			 * /*if("1".equalsIgnoreCase(rs3.getString("SHMODE")) &&
			 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
			 * chargeInfo.setBasis("Per ULD"); } else
			 * if(("2".equalsIgnoreCase(rs3.getString("SHMODE"))||"4".equalsIgnoreCase(rs3.getString("SHMODE"))) &&
			 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
			 * chargeInfo.setBasis("Per Container"); } else
			 */

			/*
			 * weightBreak = rs3.getString("WEIGHT_BREAK");
			 * 
			 * if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) ||
			 * weightBreak.endsWith("%"))) chargeInfo.setPercentValue(true);
			 * 
			 * if("Per Kg".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Kilogram"); } else if("Per
			 * Lb".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Pound"); } else if("Per
			 * CFT".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Cubic Meter"); } else if("Per
			 * CBM".equalsIgnoreCase(rs3.getString("CHARGEBASIS"))) {
			 * chargeInfo.setBasis("Per Cubic Feet"); } else
			 * chargeInfo.setBasis(rs3.getString("CHARGEBASIS")); }
			 * chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
			 * 
			 * if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) ||
			 * chargesDOB.getMarginDiscountFlag()==null) {
			 * chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
			 * 
			 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE"); else
			 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100); }
			 * else { chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
			 * chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
			 * 
			 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE"); else
			 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) sellRate =
			 * rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100); }
			 * 
			 * chargeInfo.setSellRate(sellRate);
			 * chargeInfo.setLineNumber(rs3.getInt("LINE_NO")); } }
			 */
			chargesDOB = null;
			rs6 = pStmt.executeQuery();
			// rs4 ResultSet is used for Cartage Charges
			while (rs4.next()) {
				flag = rs4.getString("SEL_BUY_FLAG");

				if ("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT"))) {
					if ((masterDOB.getShipperZipCode() != null && masterDOB
							.getShipperZipCode().trim().length() != 0)
							|| (masterDOB.getShipperZones() != null && masterDOB
									.getShipperZones().indexOf(",") == -1)) {
						// isSingleShipperZone = true;
						if (chargesDOB != null
								&& ((chargesDOB.getBuyChargeId() != null && chargesDOB
										.getBuyChargeId().equals(
												rs4.getString("BUY_CHARGE_ID"))) || (chargesDOB
										.getSellChargeId() != null && chargesDOB
										.getSellChargeId().equals(
												rs4.getString("SELLCHARGEID"))))) {
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							chargeInfo.setBreakPoint(rs4
									.getString("CHARGESLAB"));
							if (rs4.getString("CURRENCY") != null
									&& rs4.getString("CURRENCY").trim()
											.length() != 0)
								chargeInfo.setCurrency(rs4
										.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs4
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs4
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs4
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs4
									.getString("RATE_INDICATOR"));
							weightBreak = rs4.getString("WEIGHT_BREAK");
							rateType = rs4.getString("RATE_TYPE");
							// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator())))// MODIFIED
																			// FOR
																			// 183812
							{
								chargeInfo.setBasis("Per Shipment");
							} else {
								// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
								if ("LIST".equalsIgnoreCase(rs4
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs4
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));

							if ("M".equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())
									|| chargesDOB.getMarginDiscountFlag() == null) {
								chargeInfo.setMargin(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs4
										.getString("MARGIN_TYPE"));

								/*
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
								 */
								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														+ rs4
																.getDouble("MARGINVALUE");
												marginValue = sellRate
														- rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ rs4.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ (rs4.getDouble("BUYRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {

												sellRate = rs6
														.getDouble("BUY_RATE")
														- (rs6
																.getDouble("BUY_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("BUYRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ (rs4.getDouble("BUYRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								}
							} else {
								chargeInfo.setDiscount(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs4
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- rs4
																.getDouble("MARGINVALUE");
												marginValue = rs4
														.getDouble("SELLRATE")
														- sellRate;
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- rs4.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- (rs4
															.getDouble("SELLRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- (rs6
																.getDouble("R_SELL_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("SELLRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("SELLRATE");
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- (rs4.getDouble("SELLRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								}
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));

						} else {
							chargesDOB = new QuoteCharges();

							chargesDOB.setSellChargeId(rs4
									.getString("SELLCHARGEID"));
							chargesDOB.setBuyChargeId(rs4
									.getString("BUY_CHARGE_ID"));

							chargesDOB.setSellBuyFlag(rs4
									.getString("SEL_BUY_FLAG"));

							chargesDOB.setChargeDescriptionId("Pickup Charge");
							chargesDOB.setCostIncurredAt(rs4
									.getString("COST_INCURREDAT"));
							chargesDOB.setTerminalId(rs4
									.getString("TERMINALID"));
							chargesDOB.setSelectedFlag(rs4
									.getString("SELECTED_FLAG"));
							chargesDOB.setMarginDiscountFlag(rs4
									.getString("MARGIN_DISCOUNT_FLAG"));

							chargeInfoList = new ArrayList();
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							chargesDOB.setChargeInfoList(chargeInfoList);

							chargeInfo.setBreakPoint(rs4
									.getString("CHARGESLAB"));
							if (rs4.getString("CURRENCY") != null
									&& rs4.getString("CURRENCY").trim()
											.length() != 0)
								chargeInfo.setCurrency(rs4
										.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs4
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs4
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs4
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs4
									.getString("RATE_INDICATOR"));
							weightBreak = rs4.getString("WEIGHT_BREAK");
							rateType = rs4.getString("RATE_TYPE");
							// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator())))// MODIFIED
																			// FOR
																			// 183812
							{
								chargeInfo.setBasis("Per Shipment");
							} else {
								// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
								if ("LIST".equalsIgnoreCase(rs4
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs4
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));

							if ("M".equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())
									|| chargesDOB.getMarginDiscountFlag() == null) {
								chargeInfo.setMargin(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs4
										.getString("MARGIN_TYPE"));

								/*
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 * else {
								 * chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
								 * chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
								 * 
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 */
								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														+ rs4
																.getDouble("MARGINVALUE");
												marginValue = sellRate
														- rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ rs4.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ (rs4.getDouble("BUYRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {

												sellRate = rs6
														.getDouble("BUY_RATE")
														- (rs6
																.getDouble("BUY_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("BUYRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ (rs4.getDouble("BUYRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								}
							} else {
								chargeInfo.setDiscount(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs4
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- rs4
																.getDouble("MARGINVALUE");
												marginValue = rs4
														.getDouble("SELLRATE")
														- sellRate;
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- rs4.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- (rs4
															.getDouble("SELLRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- (rs6
																.getDouble("R_SELL_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("SELLRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("SELLRATE");
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- (rs4.getDouble("SELLRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								}
							}
							chargeInfo.setSellRate(sellRate);
							chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));

							if ("Pickup".equalsIgnoreCase(rs4
									.getString("COST_INCURREDAT"))) {
								if (originChargesList == null)
									originChargesList = new ArrayList();
								originChargesList.add(chargesDOB);
							}
						}
					}
					/*
					 * else { tmpFinalDOB = new QuoteFinalDOB();
					 * tmpFinalDOB.setMasterDOB(masterDOB);
					 * tmpFinalDOB.setOriginChargesList(originChargesList);
					 * 
					 * if(masterDOB.getShipperZones()!=null &&
					 * masterDOB.getShipperZones().trim().length()!=0)
					 * tmpFinalDOB = getCartages(tmpFinalDOB);
					 * 
					 * //finalDOB.setOriginChargesList(tmpFinalDOB.getOriginChargesList());
					 * originChargesList = tmpFinalDOB.getOriginChargesList();
					 * 
					 * if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
					 * finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
					 * 
					 * finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap()); }
					 */
				}
				if ("Delivery".equalsIgnoreCase(rs4
						.getString("COST_INCURREDAT"))) {
					if ((masterDOB.getConsigneeZipCode() != null && masterDOB
							.getConsigneeZipCode().trim().length() != 0)
							|| (masterDOB.getConsigneeZones() != null && masterDOB
									.getConsigneeZones().indexOf(",") == -1)) {
						// isSingleConsigneeZone = true;
						if (deliveryChargesDOB != null
								&& ((deliveryChargesDOB.getBuyChargeId() != null && deliveryChargesDOB
										.getBuyChargeId().equals(
												rs4.getString("BUY_CHARGE_ID"))) || (deliveryChargesDOB
										.getSellChargeId() != null && deliveryChargesDOB
										.getSellChargeId().equals(
												rs4.getString("SELLCHARGEID"))))) {
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							chargeInfo.setBreakPoint(rs4
									.getString("CHARGESLAB"));

							if (rs4.getString("CURRENCY") != null
									&& rs4.getString("CURRENCY").trim()
											.length() != 0)
								chargeInfo.setCurrency(rs4
										.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs4
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs4
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs4
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs4
									.getString("RATE_INDICATOR"));
							// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
							weightBreak = rs4.getString("WEIGHT_BREAK");
							rateType = rs4.getString("RATE_TYPE");
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) // MODIFIED
																			// FOR
																			// 183182
							{
								chargeInfo.setBasis("Per Shipment");
							} else {
								// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
								if ("LIST".equalsIgnoreCase(rs4
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs4
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));

							if ("M".equalsIgnoreCase(deliveryChargesDOB
									.getMarginDiscountFlag())
									|| deliveryChargesDOB
											.getMarginDiscountFlag() == null) {
								chargeInfo.setMargin(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs4
										.getString("MARGIN_TYPE"));

								/*
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 * else {
								 * chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
								 * chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
								 * 
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 */
								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														+ rs4
																.getDouble("MARGINVALUE");
												marginValue = sellRate
														- rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);

											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ rs4.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ (rs4.getDouble("BUYRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														- (rs6
																.getDouble("BUY_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("BUYRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);

											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ (rs4.getDouble("BUYRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));

									}
								}
							} else {
								chargeInfo.setDiscount(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs4
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- rs4
																.getDouble("MARGINVALUE");
												marginValue = rs4
														.getDouble("SELLRATE")
														- sellRate;
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- rs4.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- (rs4
															.getDouble("SELLRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- (rs6
																.getDouble("R_SELL_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("SELLRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("SELLRATE");
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- (rs4.getDouble("SELLRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								}
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));

						} else {
							deliveryChargesDOB = new QuoteCharges();

							deliveryChargesDOB.setSellChargeId(rs4
									.getString("SELLCHARGEID"));
							deliveryChargesDOB.setBuyChargeId(rs4
									.getString("BUY_CHARGE_ID"));

							deliveryChargesDOB.setSellBuyFlag(rs4
									.getString("SEL_BUY_FLAG"));

							deliveryChargesDOB
									.setChargeDescriptionId("Delivery Charge");
							deliveryChargesDOB.setCostIncurredAt(rs4
									.getString("COST_INCURREDAT"));
							deliveryChargesDOB.setTerminalId(rs4
									.getString("TERMINALID"));
							deliveryChargesDOB.setSelectedFlag(rs4
									.getString("SELECTED_FLAG"));
							deliveryChargesDOB.setMarginDiscountFlag(rs4
									.getString("MARGIN_DISCOUNT_FLAG"));

							chargeInfoList = new ArrayList();
							chargeInfo = new QuoteChargeInfo();

							chargeInfoList.add(chargeInfo);

							deliveryChargesDOB
									.setChargeInfoList(chargeInfoList);

							chargeInfo.setBreakPoint(rs4
									.getString("CHARGESLAB"));
							if (rs4.getString("CURRENCY") != null
									&& rs4.getString("CURRENCY").trim()
											.length() != 0)
								chargeInfo.setCurrency(rs4
										.getString("CURRENCY"));
							else
								chargeInfo.setCurrency(masterDOB
										.getTerminalCurrency());

							chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
							chargeInfo.setRecOrConSellRrate(rs4
									.getDouble("SELLRATE"));
							chargeInfo.setSellChargeMargin(rs4
									.getDouble("MARGINVALUE"));
							chargeInfo.setSellChargeMarginType(rs4
									.getString("MARGIN_TYPE"));
							chargeInfo.setRateIndicator(rs4
									.getString("RATE_INDICATOR"));
							weightBreak = rs4.getString("WEIGHT_BREAK");
							rateType = rs4.getString("RATE_TYPE");
							// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
							if ("BASE".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
									|| "MIN".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| "MAX".equalsIgnoreCase(chargeInfo
											.getBreakPoint())
									|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
											.equalsIgnoreCase(rateType))
									|| ("BOTH".equalsIgnoreCase(rateType) && "F"
											.equalsIgnoreCase(chargeInfo
													.getRateIndicator()))) // MODIFIED
																			// FOR
																			// 183812
							{
								chargeInfo.setBasis("Per Shipment");
							} else {
								// chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
								if ("LIST".equalsIgnoreCase(rs4
										.getString("WEIGHT_BREAK"))) {
									chargeInfo.setBasis("Per Container");
								} else if ("Per Kg".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Kilogram");
								} else if ("Per Lb".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Pound");
								} else if ("Per CBM".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Meter");
								} else if ("Per CFT".equalsIgnoreCase(rs4
										.getString("CHARGEBASIS"))) {
									chargeInfo.setBasis("Per Cubic Feet");
								} else
									chargeInfo.setBasis(rs4
											.getString("CHARGEBASIS"));
							}
							chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));

							if ("M".equalsIgnoreCase(deliveryChargesDOB
									.getMarginDiscountFlag())
									|| deliveryChargesDOB
											.getMarginDiscountFlag() == null) {
								chargeInfo.setMargin(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setMarginType(rs4
										.getString("MARGIN_TYPE"));

								/*
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 * else {
								 * chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
								 * chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
								 * 
								 * if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
								 * else
								 * if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
								 * sellRate =
								 * rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100); }
								 */
								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														+ rs4
																.getDouble("MARGINVALUE");
												marginValue = sellRate
														- rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ rs4.getDouble("MARGINVALUE");
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4.getDouble("BUYRATE")
													+ (rs4.getDouble("BUYRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setMargin(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("BUY_RATE")
														- (rs6
																.getDouble("BUY_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("BUYRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("BUYRATE");
												chargeInfo
														.setMargin(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("BUYRATE")
												+ (rs4.getDouble("BUYRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setMargin(rs4
												.getDouble("MARGINVALUE"));
									}
								}
							} else {
								chargeInfo.setDiscount(rs4
										.getDouble("MARGINVALUE"));
								chargeInfo.setDiscountType(rs4
										.getString("MARGIN_TYPE"));

								if ("A".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- rs4
															.getDouble("MARGINVALUE");
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- rs4
																.getDouble("MARGINVALUE");
												marginValue = rs4
														.getDouble("SELLRATE")
														- sellRate;
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- rs4.getDouble("MARGINVALUE");
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								} else if ("P".equalsIgnoreCase(rs4
										.getString("MARGIN_TYPE"))) {

									// @@Modified by kameswari for the WPBN
									// issue-61235
									// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs4.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
									if (flag.equalsIgnoreCase(sellBuyFlag)) {
										if ("modifiedQuote"
												.equalsIgnoreCase(quoteType)) {
											sellRate = rs4
													.getDouble("SELLRATE")
													- (rs4
															.getDouble("SELLRATE")
															* rs4
																	.getDouble("MARGINVALUE") / 100);
											chargeInfo.setDiscount(rs4
													.getDouble("MARGINVALUE"));
										} else {
											if (rs6.next()) {
												sellRate = rs6
														.getDouble("R_SELL_RATE")
														- (rs6
																.getDouble("R_SELL_RATE")
																* rs4
																		.getDouble("MARGINVALUE") / 100);
												marginValue = (rs4
														.getDouble("SELLRATE") - sellRate)
														* 100
														/ rs4
																.getDouble("SELLRATE");
												chargeInfo
														.setDiscount(marginValue);
											}
										}
									} else {
										sellRate = rs4.getDouble("SELLRATE")
												- (rs4.getDouble("SELLRATE")
														* rs4
																.getDouble("MARGINVALUE") / 100);
										chargeInfo.setDiscount(rs4
												.getDouble("MARGINVALUE"));
									}
									// @@WPBN issue-61235
								}
							}

							chargeInfo.setSellRate(sellRate);
							chargeInfo.setLineNumber(rs4.getInt("LINE_NO"));

							/*
							 * if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT"))) {
							 * if(destChargesList==null) destChargesList = new
							 * ArrayList();
							 * destChargesList.add(deliveryChargesDOB); }
							 */
						}
					}
					/*
					 * else { tmpFinalDOB = new QuoteFinalDOB();
					 * tmpFinalDOB.setMasterDOB(masterDOB);
					 * tmpFinalDOB.setDestChargesList(destChargesList);
					 * 
					 * if((masterDOB.getShipperZones()!=null &&
					 * masterDOB.getShipperZones().trim().length()!=0)
					 * ||(masterDOB.getConsigneeZones()!=null &&
					 * masterDOB.getConsigneeZones().trim().length()!=0))
					 * tmpFinalDOB = getCartages(tmpFinalDOB);
					 * 
					 * destChargesList = tmpFinalDOB.getDestChargesList();
					 * 
					 * 
					 * if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
					 * finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
					 * 
					 * finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap()); }
					 */
				}
			}
			String tempShipperCode = null;
			String tempShipperZones = null;
			String tempConsigneeCode = null;
			String tempConsigneeZones = null;
			boolean isShipperFetched = false;
			boolean isConsigneeFetched = false;

			if (masterDOB.getShipperZipCode() != null
					&& masterDOB.getShipperZipCode().trim().length() != 0)
				isShipperZipCode = true;
			if (masterDOB.getShipperZones() != null
					&& masterDOB.getShipperZones().indexOf(",") == -1)
				isSingleShipperZone = true;
			if (masterDOB.getConsigneeZipCode() != null
					&& masterDOB.getConsigneeZipCode().trim().length() != 0)
				isConsigneeZipCode = true;
			if (masterDOB.getConsigneeZones() != null
					&& masterDOB.getConsigneeZones().indexOf(",") == -1)
				isSingleConsigneeZone = true;

			/*
			 * if((isSingleShipperZone && !
			 * isShipperZipCode)||(isSingleConsigneeZone &&
			 * !isConsigneeZipCode)) { finalDOB =
			 * getZipZoneMapping(finalDOB);//@@So fetch the Zip Zone Mapping }
			 * else
			 */
			// {
			/*
			 * if((masterDOB.getShipperZones()!=null &&
			 * masterDOB.getShipperZones().trim().length()!=0)
			 * ||(masterDOB.getConsigneeZones()!=null &&
			 * masterDOB.getConsigneeZones().trim().length()!=0)) {
			 * 
			 * if(isShipperZipCode) { tempShipperCode =
			 * masterDOB.getShipperZipCode(); tempShipperZones =
			 * masterDOB.getShipperZones(); masterDOB.setShipperZipCode(null);
			 * masterDOB.setShipperZones(null); isShipperFetched = true; }
			 * if(isConsigneeZipCode) { tempConsigneeCode =
			 * masterDOB.getConsigneeZipCode(); tempConsigneeZones =
			 * masterDOB.getConsigneeZones();
			 * masterDOB.setConsigneeZipCode(null);
			 * masterDOB.setConsigneeZones(null); isConsigneeFetched = true; }
			 * 
			 * if(!(isShipperFetched && isConsigneeFetched)) { tmpFinalDOB = new
			 * QuoteFinalDOB(); tmpFinalDOB.setMasterDOB(masterDOB);
			 * tmpFinalDOB.setOriginChargesList(originChargesList);
			 * tmpFinalDOB.setDestChargesList(destChargesList); tmpFinalDOB =
			 * getCartages(tmpFinalDOB); originChargesList =
			 * tmpFinalDOB.getOriginChargesList(); destChargesList =
			 * tmpFinalDOB.getDestChargesList();
			 * if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
			 * finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
			 * if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
			 * finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
			 * 
			 * finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
			 * finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap()); }
			 * 
			 * if(tempShipperCode!=null) {
			 * masterDOB.setShipperZipCode(tempShipperCode);
			 * masterDOB.setShipperZones(tempShipperZones); }
			 * if(tempConsigneeCode!=null) {
			 * masterDOB.setConsigneeZipCode(tempConsigneeCode);
			 * masterDOB.setConsigneeZones(tempConsigneeZones); }
			 * 
			 * finalDOB.setMasterDOB(masterDOB);
			 * 
			 * if(!isShipperFetched || !isConsigneeFetched) { finalDOB =
			 * getZipZoneMapping(finalDOB); }
			 * 
			 * //originChargesList = tmpFinalDOB.getOriginChargesList();
			 * //destChargesList = tmpFinalDOB.getDestChargesList();
			 *  /* if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
			 * finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
			 * if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
			 * finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
			 * 
			 * finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
			 * finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
			 */
			/* } */
			// }
			chargesDOB = null;
			rs6 = pStmt.executeQuery();
			// rs3 ResultSet is used for getting Charges
			while (rs3.next()) {
				flag = rs3.getString("SEL_BUY_FLAG");
				if (chargesDOB != null
						&& ((chargesDOB.getBuyChargeId() != null && chargesDOB
								.getBuyChargeId().equals(
										rs3.getString("BUY_CHARGE_ID"))) || (chargesDOB
								.getSellChargeId() != null && chargesDOB
								.getSellChargeId().equals(
										rs3.getString("SELLCHARGEID"))))) {
					chargeInfo = new QuoteChargeInfo();

					chargeInfoList.add(chargeInfo);

					chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
					if (rs3.getString("CURRENCY") != null
							&& rs3.getString("CURRENCY").trim().length() != 0)
						chargeInfo.setCurrency(rs3.getString("CURRENCY"));
					else
						chargeInfo.setCurrency(masterDOB.getTerminalCurrency());

					chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
					chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
					chargeInfo
							.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
					chargeInfo.setSellChargeMarginType(rs3
							.getString("MARGIN_TYPE"));
					chargeInfo
							.setRateIndicator(rs3.getString("RATE_INDICATOR"));
					if ("M"
							.equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())
							|| chargesDOB.getMarginDiscountFlag() == null) {
						chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
						chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));

						/*
						 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100); }
						 * else {
						 * chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
						 * chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
						 * 
						 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100); }
						 */
						if ("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) {
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT"))&&rs6.getString("CHARGE_DESCRIPTION").equalsIgnoreCase(chargesDOB.getChargeDescriptionId()))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("BUYRATE")
											+ rs3.getDouble("MARGINVALUE");
									chargeInfo.setMargin(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												+ rs3.getDouble("MARGINVALUE");
										marginValue = sellRate
												- rs3.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);
									}

								}
							} else {
								sellRate = rs3.getDouble("BUYRATE")
										+ rs3.getDouble("MARGINVALUE");
								chargeInfo.setMargin(rs3
										.getDouble("MARGINVALUE"));
							}
						} else if ("P".equalsIgnoreCase(rs3
								.getString("MARGIN_TYPE"))) {
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {

								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("BUYRATE")
											+ (rs3.getDouble("BUYRATE")
													* rs3
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setMargin(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												- (rs6.getDouble("BUY_RATE")
														* rs3
																.getDouble("MARGINVALUE") / 100);
										marginValue = (rs3.getDouble("BUYRATE") - sellRate)
												* 100
												/ rs3.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);
									}
								}
							} else {
								sellRate = rs3.getDouble("BUYRATE")
										+ (rs3.getDouble("BUYRATE")
												* rs3.getDouble("MARGINVALUE") / 100);
								chargeInfo.setMargin(rs3
										.getDouble("MARGINVALUE"));

							}
						}
					} else {
						chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
						chargeInfo
								.setDiscountType(rs3.getString("MARGIN_TYPE"));

						if ("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("SELLRATE")
											- rs3.getDouble("MARGINVALUE");
									chargeInfo.setDiscount(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- rs3.getDouble("MARGINVALUE");
										marginValue = rs3.getDouble("SELLRATE")
												- sellRate;
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs3.getDouble("SELLRATE")
										- rs3.getDouble("MARGINVALUE");
								chargeInfo.setDiscount(rs3
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						} else if ("P".equalsIgnoreCase(rs3
								.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {

									sellRate = rs3.getDouble("SELLRATE")
											- (rs3.getDouble("SELLRATE")
													* rs3
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setDiscount(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- (rs6.getDouble("R_SELL_RATE")
														* rs3
																.getDouble("MARGINVALUE") / 100);
										marginValue = (rs3
												.getDouble("SELLRATE") - sellRate)
												* 100
												/ rs3.getDouble("SELLRATE");
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs3.getDouble("SELLRATE")
										- (rs3.getDouble("SELLRATE")
												* rs3.getDouble("MARGINVALUE") / 100);
								chargeInfo.setDiscount(rs3
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						}
					}

					// chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
					weightBreak = rs3.getString("WEIGHT_BREAK");
					rateType = rs3.getString("RATE_TYPE");
					if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
							|| "MIN".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| "MAX".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
									.equalsIgnoreCase(rateType))
							|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
									.equalsIgnoreCase(chargeInfo
											.getRateIndicator()))) // MODIFIED
																	// FOR
																	// 183812
					{
						chargeInfo.setBasis("Per Shipment");
					} else {
						/*
						 * if("1".equalsIgnoreCase(rs3.getString("SHMODE")) &&
						 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per ULD"); } else
						 * if(("2".equalsIgnoreCase(rs3.getString("SHMODE"))||"4".equalsIgnoreCase(rs3.getString("SHMODE"))) &&
						 * "LIST".equalsIgnoreCase(rs3.getString("WEIGHT_BREAK"))) {
						 * chargeInfo.setBasis("Per Container"); } else
						 */
						if ("Per Kg".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Pound");
						} else if ("Per CBM".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Meter");
						} else if ("Per CFT".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Feet");
						} else
							chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));

						// weightBreak = rs3.getString("WEIGHT_BREAK");

						if (weightBreak != null
								&& ("Percent".equalsIgnoreCase(weightBreak) || weightBreak
										.endsWith("%")))
							chargeInfo.setPercentValue(true);
					}
					chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));

					chargeInfo.setSellRate(sellRate);
					chargeInfo.setLineNumber(rs3.getInt("LINE_NO"));

				} else {
					chargesDOB = new QuoteCharges();

					if ("Origin".equalsIgnoreCase(rs3
							.getString("COST_INCURREDAT"))) {
						if (originChargesList == null)
							originChargesList = new ArrayList();
						originChargesList.add(chargesDOB);
					} else if ("Destination".equalsIgnoreCase(rs3
							.getString("COST_INCURREDAT"))) {
						if (destChargesList == null)
							destChargesList = new ArrayList();
						destChargesList.add(chargesDOB);
					}

					chargesDOB.setBuyChargeId(rs3.getString("BUY_CHARGE_ID"));
					chargesDOB.setSellChargeId(rs3.getString("SELLCHARGEID"));
					chargesDOB.setSellBuyFlag(rs3.getString("SEL_BUY_FLAG"));

					chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
					chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
					chargesDOB.setMarginDiscountFlag(rs3
							.getString("MARGIN_DISCOUNT_FLAG"));
					chargesDOB.setChargeDescriptionId(rs3
							.getString("CHARGEDESCID"));
					chargesDOB
							.setInternalName(rs3.getString("INT_CHARGE_NAME"));
					chargesDOB
							.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
					chargesDOB.setCostIncurredAt(rs3
							.getString("COST_INCURREDAT"));
					chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));

					if (changeDesc.equalsIgnoreCase(chargesDOB
							.getChargeDescriptionId()))
						finalDOB.setEmailChargeName(chargesDOB
								.getExternalName());

					chargeInfoList = new ArrayList();
					chargeInfo = new QuoteChargeInfo();
					chargeInfoList.add(chargeInfo);

					chargesDOB.setChargeInfoList(chargeInfoList);

					chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
					if (rs3.getString("CURRENCY") != null
							&& rs3.getString("CURRENCY").trim().length() != 0)
						chargeInfo.setCurrency(rs3.getString("CURRENCY"));
					else
						chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
					chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
					chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
					chargeInfo
							.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
					chargeInfo.setSellChargeMarginType(rs3
							.getString("MARGIN_TYPE"));
					chargeInfo
							.setRateIndicator(rs3.getString("RATE_INDICATOR"));
					weightBreak = rs3.getString("WEIGHT_BREAK");
					rateType = rs3.getString("RATE_TYPE");
					// chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
					if ("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint())
							|| "MIN".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| "MAX".equalsIgnoreCase(chargeInfo
									.getBreakPoint())
							|| ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT"
									.equalsIgnoreCase(rateType))
							|| ("BOTH".equalsIgnoreCase(rateType) && "FLAT"
									.equalsIgnoreCase(chargeInfo
											.getRateIndicator()))) // MODIFIED
																	// FOR
																	// 183812
					{
						chargeInfo.setBasis("Per Shipment");
					} else {

						if (weightBreak != null
								&& ("Percent".equalsIgnoreCase(weightBreak) || weightBreak
										.endsWith("%")))
							chargeInfo.setPercentValue(true);

						if ("Per Kg".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Kilogram");
						} else if ("Per Lb".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Pound");
						} else if ("Per CBM".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Meter");
						} else if ("Per CFT".equalsIgnoreCase(rs3
								.getString("CHARGEBASIS"))) {
							chargeInfo.setBasis("Per Cubic Feet");
						} else
							chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
					}
					chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));

					if ("M"
							.equalsIgnoreCase(chargesDOB
									.getMarginDiscountFlag())
							|| chargesDOB.getMarginDiscountFlag() == null) {
						chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
						chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));

						/*
						 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100); }
						 * else {
						 * chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
						 * chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
						 * 
						 * if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
						 * else
						 * if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
						 * sellRate =
						 * rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100); }
						 */
						if ("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) {

							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("BUYRATE")
											+ rs3.getDouble("MARGINVALUE");
									chargeInfo.setMargin(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												+ rs3.getDouble("MARGINVALUE");
										marginValue = sellRate
												- rs3.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);
									}
								}
							} else {

								sellRate = rs3.getDouble("BUYRATE")
										+ rs3.getDouble("MARGINVALUE");
								chargeInfo.setMargin(rs3
										.getDouble("MARGINVALUE"));
							}
						} else if ("P".equalsIgnoreCase(rs3
								.getString("MARGIN_TYPE"))) {

							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {

									sellRate = rs3.getDouble("BUYRATE")
											+ (rs3.getDouble("BUYRATE")
													* rs3
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setMargin(rs3
											.getDouble("MARGINVALUE"));
								} else {

									if (rs6.next()) {
										sellRate = rs6.getDouble("BUY_RATE")
												- (rs6.getDouble("BUY_RATE")
														* rs3
																.getDouble("MARGINVALUE") / 100);
										marginValue = (rs3.getDouble("BUYRATE") - sellRate)
												* 100
												/ rs3.getDouble("BUYRATE");
										chargeInfo.setMargin(marginValue);

									}
								}
							} else {

								sellRate = rs3.getDouble("BUYRATE")
										+ (rs3.getDouble("BUYRATE")
												* rs3.getDouble("MARGINVALUE") / 100);
								chargeInfo.setMargin(rs3
										.getDouble("MARGINVALUE"));
							}
						}
					} else {
						chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
						chargeInfo
								.setDiscountType(rs3.getString("MARGIN_TYPE"));
						if ("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("SELLRATE")
											- rs3.getDouble("MARGINVALUE");

									chargeInfo.setDiscount(rs3
											.getDouble("MARGINVALUE"));
								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- rs3.getDouble("MARGINVALUE");
										marginValue = rs3.getDouble("SELLRATE")
												- sellRate;
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs3.getDouble("SELLRATE")
										- rs3.getDouble("MARGINVALUE");
								chargeInfo.setDiscount(rs3
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						} else if ("P".equalsIgnoreCase(rs3
								.getString("MARGIN_TYPE"))) {

							// @@Modified by kameswari for the WPBN issue-61235
							// if(rs6.next()&&flag.equalsIgnoreCase(rs6.getString("SELL_BUY_FLAG"))&&rs3.getString("CHARGESLAB").equalsIgnoreCase(rs6.getString("BREAK_POINT")))
							if (flag.equalsIgnoreCase(sellBuyFlag)
									&& changeDesc.equalsIgnoreCase(chargesDOB
											.getChargeDescriptionId())) {
								// if("modifiedQuote".equalsIgnoreCase(quoteType))//@@Commented
								// and Modified by Kameswari for the WPBN
								// issue-154398 on 21/02/09
								if ("modifiedQuote".equalsIgnoreCase(quoteType)
										|| "Y".equalsIgnoreCase(rs3
												.getString("CHANGE_FLAG"))) {
									sellRate = rs3.getDouble("SELLRATE")
											- (rs3.getDouble("SELLRATE")
													* rs3
															.getDouble("MARGINVALUE") / 100);
									chargeInfo.setDiscount(rs3
											.getDouble("MARGINVALUE"));

								} else {
									if (rs6.next()) {
										sellRate = rs6.getDouble("R_SELL_RATE")
												- (rs6.getDouble("R_SELL_RATE")
														* rs3
																.getDouble("MARGINVALUE") / 100);
										marginValue = (rs3
												.getDouble("SELLRATE") - sellRate)
												* 100
												/ rs3.getDouble("SELLRATE");
										chargeInfo.setDiscount(marginValue);
									}
								}
							} else {
								sellRate = rs3.getDouble("SELLRATE")
										- (rs3.getDouble("SELLRATE")
												* rs3.getDouble("MARGINVALUE") / 100);
								chargeInfo.setDiscount(rs3
										.getDouble("MARGINVALUE"));
							}
							// @@WPBN issue-61235
						}
					}
					chargeInfo.setSellRate(sellRate);

					chargeInfo.setLineNumber(rs3.getInt("LINE_NO"));
				}
			}

			// @@ For putting the delivery charge at the end of the list, if it
			// exists.
			if (deliveryChargesDOB != null) {
				if (destChargesList == null)
					destChargesList = new ArrayList();
				destChargesList.add(deliveryChargesDOB);
			}
			// @@

			if ((masterDOB.getShipperZones() != null && masterDOB
					.getShipperZones().trim().length() != 0)
					|| (masterDOB.getConsigneeZones() != null && masterDOB
							.getConsigneeZones().trim().length() != 0)) {
				if (isShipperZipCode || isSingleShipperZone) {
					tempShipperCode = masterDOB.getShipperZipCode();
					tempShipperZones = masterDOB.getShipperZones();
					masterDOB.setShipperZipCode(null);
					masterDOB.setShipperZones(null);
					isShipperFetched = true;
				}
				if (isConsigneeZipCode || isSingleConsigneeZone) {
					tempConsigneeCode = masterDOB.getConsigneeZipCode();
					tempConsigneeZones = masterDOB.getConsigneeZones();
					masterDOB.setConsigneeZipCode(null);
					masterDOB.setConsigneeZones(null);
					isConsigneeFetched = true;
				}

				if (!(isShipperFetched && isConsigneeFetched)) {
					if (masterDOB.getShipperZipCode() != null
							|| masterDOB.getShipperZones() != null
							|| masterDOB.getConsigneeZipCode() != null
							|| masterDOB.getConsigneeZones() != null) {
						tmpFinalDOB = new QuoteFinalDOB();
						tmpFinalDOB.setMasterDOB(masterDOB);
						tmpFinalDOB.setOriginChargesList(originChargesList);
						tmpFinalDOB.setDestChargesList(destChargesList);
						tmpFinalDOB = getCartages(tmpFinalDOB);
						originChargesList = tmpFinalDOB.getOriginChargesList();
						destChargesList = tmpFinalDOB.getDestChargesList();

						if (tmpFinalDOB.getPickUpCartageRatesList() != null)
							finalDOB.setPickUpCartageRatesList(tmpFinalDOB
									.getPickUpCartageRatesList());
						if (tmpFinalDOB.getDeliveryCartageRatesList() != null)
							finalDOB.setDeliveryCartageRatesList(tmpFinalDOB
									.getDeliveryCartageRatesList());
						if (tmpFinalDOB.getPickupWeightBreaks() != null)
							finalDOB.setPickupWeightBreaks(tmpFinalDOB
									.getPickupWeightBreaks());
						if (tmpFinalDOB.getDeliveryWeightBreaks() != null)
							finalDOB.setDeliveryWeightBreaks(tmpFinalDOB
									.getDeliveryWeightBreaks());

						finalDOB.setPickZoneZipMap(tmpFinalDOB
								.getPickZoneZipMap());
						finalDOB.setDeliveryZoneZipMap(tmpFinalDOB
								.getDeliveryZoneZipMap());
					}
				}
				if (isShipperFetched) {
					masterDOB.setShipperZipCode(tempShipperCode);
					masterDOB.setShipperZones(tempShipperZones);
				}
				if (isConsigneeFetched) {
					masterDOB.setConsigneeZipCode(tempConsigneeCode);
					masterDOB.setConsigneeZones(tempConsigneeZones);
				}

				finalDOB.setMasterDOB(masterDOB);

				if (!isShipperFetched || !isConsigneeFetched
						|| !isShipperZipCode || !isConsigneeZipCode) {
					finalDOB = getZipZoneMapping(finalDOB);
				}

				// originChargesList = tmpFinalDOB.getOriginChargesList();
				// destChargesList = tmpFinalDOB.getDestChargesList();

				/*
				 * if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
				 * finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
				 * if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
				 * finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
				 * 
				 * finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
				 * finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
				 */
			}

			if (originChargesList != null || destChargesList != null) {
				if (originChargesList != null)
					finalDOB.setOriginChargesList(originChargesList);
				if (destChargesList != null)
					finalDOB.setDestChargesList(destChargesList);
			}

			list_exNotes = new ArrayList();
			String[] notes = null;
			while (rs5.next()) {
				list_exNotes
						.add((rs5.getString("EXTERNAL_NOTES") != null) ? rs5
								.getString("EXTERNAL_NOTES") : "");
			}
			if (list_exNotes != null) {
				notes = new String[list_exNotes.size()];
			}
			String[] arr = null;
			String arr1 = "";
			int j = 0;
			if (list_exNotes != null) {
				int exNotesSize = list_exNotes.size();
				for (int i = 0; i < exNotesSize; i++) {
					if (list_exNotes.get(i) != "") {
						notes[j] = (String) list_exNotes.get(i);
						if ((notes[j].trim()).length() > 0) {
							arr = notes[j].split("");
							for (int k = 0; k < arr.length - 1; k++) {

								if ((arr[k].trim()).length() > 0) {
									arr1 = arr1.concat(arr[k].trim());
								} else {
									if ((arr[k + 1].trim()).length() > 0) {
										arr1 = arr1.concat(" ");
									}
								}
							}
							arr1 = arr1.concat(arr[arr.length - 1]);
							notes[j] = arr1;
							arr1 = "";

							j++;
						}

					}

				}
			}
			finalDOB.setExternalNotes(notes);

		}

		catch (SQLException sql) {
			// Logger.error(FILE_NAME,"SQLException While Fetching Updated
			// Quotes Data"+sql);
			logger.error(FILE_NAME
					+ "SQLException While Fetching Updated Quotes Data" + sql);
			// Logger.error(FILE_NAME,"Error Code: "+sql.getErrorCode());
			logger.error(FILE_NAME + "Error Code: " + sql.getErrorCode());
			// Logger.error(FILE_NAME,"SQL State: "+sql.getSQLState());
			logger.error(FILE_NAME + "SQL State: " + sql.getSQLState());

			sql.printStackTrace();
			throw new SQLException(sql.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Exception While Fetching Updated Quotes
			// Data"+e);
			logger.error(FILE_NAME
					+ "Exception While Fetching Updated Quotes Data" + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs2 != null)
					rs2.close();
				if (rs3 != null)
					rs3.close();
				if (rs4 != null)
					rs4.close();
				if (rs5 != null)
					rs5.close();
				if (rs6 != null)
					rs6.close();// Added By RajKumari on 24-10-2008 for
								// Connection Leakages.
				if (pStmt != null)
					pStmt.close();
				if (csmt != null)
					csmt.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return finalDOB;
	}

	// @@Commented and Modified by kameswari for the WPBN issue-61235
	// public QuoteFinalDOB getUpdatedQuoteInfo(long quoteId,String
	// changeDesc,String sellBuyFlag,QuoteFinalDOB finalDOB,String quoteType)
	// throws SQLException

	public QuoteFinalDOB getZipZoneMapping(QuoteFinalDOB finalDOB)
			throws SQLException {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		QuoteMasterDOB masterDOB = null;

		String shipZoneQry = "";
		String consZoneQry = "";

		String sqlQuery1 = "";
		String sqlQuery2 = "";
		String sqlQuery = "";

		ArrayList delivZipCodes = null;
		ArrayList pickUpZipCodes = null;
		HashMap pickUpZoneZipMap = new HashMap();
		HashMap deliveryZoneZipMap = new HashMap();
		// StringTokenizer str = null;
		// String[] shipperZoneArray = null;
		// String shipperZones = "";
		// String[] consigneeZoneArray = null;
		// String consigneeZones = "";
		boolean executeQry = true;
		String originCountryId = null;
		String destinationCountryId = null;
		StringBuffer shZones = new StringBuffer();
		int shZonesLength = 0;
		String[] shZoneArray = null;

		StringBuffer consigneeZones = new StringBuffer();
		int consigneeZonesLength = 0;
		String[] consigneeZoneArray = null;

		try {
			connection = this.getConnection();

			// stmt = connection.prepareStatement();

			masterDOB = finalDOB.getMasterDOB();
			originCountryId = finalDOB.getHeaderDOB().getOriginCountryId();
			destinationCountryId = finalDOB.getHeaderDOB()
					.getDestinationCountryId();

			if (masterDOB.getShipperZones() != null
					&& masterDOB.getShipperZones().trim().length() != 0) {
				/*
				 * str = new StringTokenizer(masterDOB.getShipperZones(),",");
				 * shipperZoneArray = new String[str.countTokens()];
				 * 
				 * for(int i=0;str.hasMoreTokens();i++) { shipperZoneArray[i] =
				 * str.nextToken(); }
				 */
				if (masterDOB.getShipperZones().split(",").length > 0) {
					shZonesLength = masterDOB.getShipperZones().split(",").length;
					shZoneArray = masterDOB.getShipperZones().split(",");

					for (int k = 0; k < shZonesLength; k++) {
						if (k == (shZonesLength - 1))
							shZones.append("?");
						else
							shZones.append("?,");
					}
				}
			}

			/*
			 * if(shipperZoneArray!=null) { for(int i=0;i<shipperZoneArray.length;i++) {
			 * if((i+1)==shipperZoneArray.length) shipperZones = shipperZones +
			 * "'"+shipperZoneArray[i]+"'"; else shipperZones = shipperZones +
			 * "'"+shipperZoneArray[i]+"',"; } } str = null;
			 */

			if (masterDOB.getConsigneeZones() != null
					&& masterDOB.getConsigneeZones().trim().length() != 0) {
				/*
				 * str = new StringTokenizer(masterDOB.getConsigneeZones(),",");
				 * consigneeZoneArray = new String[str.countTokens()];
				 * 
				 * for(int i=0;str.hasMoreTokens();i++) { consigneeZoneArray[i] =
				 * str.nextToken(); }
				 */
				if (masterDOB.getConsigneeZones().split(",").length > 0) {
					consigneeZonesLength = masterDOB.getConsigneeZones().split(
							",").length;
					consigneeZoneArray = masterDOB.getConsigneeZones().split(
							",");

					for (int k = 0; k < consigneeZonesLength; k++) {
						if (k == (consigneeZonesLength - 1))
							consigneeZones.append("?");
						else
							consigneeZones.append("?,");
					}
				}
			}

			/*
			 * if(consigneeZoneArray!=null) { for(int i=0;i<consigneeZoneArray.length;i++) {
			 * if((i+1)==consigneeZoneArray.length) consigneeZones =
			 * consigneeZones + "'"+consigneeZoneArray[i]+"'"; else
			 * consigneeZones = consigneeZones + "'"+consigneeZoneArray[i]+"',"; } }
			 */

			/*
			 * if(masterDOB.getShipperZones()!=null &&
			 * masterDOB.getShipperZones().trim().length()!=0) shipZoneQry = "
			 * AND D.ZONE IN ("+shipperZones+") ";
			 * if(masterDOB.getConsigneeZones()!=null &&
			 * masterDOB.getConsigneeZones().trim().length()!=0) consZoneQry = "
			 * AND D.ZONE IN ("+consigneeZones+") ";
			 */

			/*
			 * if("CA".equalsIgnoreCase(originCountryId)) { sqlQuery1 = "SELECT
			 * D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup'
			 * CHARGE_TYPE "+ "FROM QMS_ZONE_CODE_MASTER_CA M,
			 * QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND
			 * M.LOCATION_ID = ?"+ " AND M.SHIPMENT_MODE = ? AND
			 * NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")"; } else {
			 * sqlQuery1 = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC,
			 * D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "+ "FROM
			 * QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE =
			 * M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+ "AND M.SHIPMENT_MODE = ?
			 * AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+shZones+")"; }
			 */

			if ("CA".equalsIgnoreCase(originCountryId)) {
				sqlQuery1 = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "
						+ "FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ?"
						+ " AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("
						+ shZones + ")";
			} else {
				sqlQuery1 = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Pickup' CHARGE_TYPE "
						+ "FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "
						+ "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("
						+ shZones + ")";
			}

			/*
			 * if("CA".equalsIgnoreCase(destinationCountryId)) { sqlQuery2 =
			 * "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE,
			 * D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+ "FROM
			 * QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE
			 * D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "+ "AND
			 * M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN
			 * ("+consigneeZones+") "; } else { sqlQuery2 = "SELECT D.ZONE,
			 * D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE,
			 * D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "+ "FROM
			 * QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE =
			 * M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "+ "AND M.SHIPMENT_MODE = ?
			 * AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("+consigneeZones+") "; }
			 */
			if ("CA".equalsIgnoreCase(destinationCountryId)) {
				sqlQuery2 = "SELECT D.ZONE, NULL ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "
						+ "FROM QMS_ZONE_CODE_MASTER_CA M,QMS_ZONE_CODE_DTL_CA D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.LOCATION_ID = ? "
						+ "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("
						+ consigneeZones + ") ";
			} else {
				sqlQuery2 = "SELECT D.ZONE, D.ALPHANUMERIC ALPHANUMERIC, D.TO_ZIPCODE, D.FROM_ZIPCODE,'Delivery' CHARGE_TYPE "
						+ "FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D WHERE D.ZONE_CODE = M.ZONE_CODE AND M.ORIGIN_LOCATION = ? "
						+ "AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.ZONE IN ("
						+ consigneeZones + ") ";
			}

			if (masterDOB.getShipperZones() != null
					&& masterDOB.getConsigneeZones() != null) {
				if (masterDOB.getShipperZipCode() == null
						&& masterDOB.getConsigneeZipCode() == null) {
					sqlQuery = sqlQuery1 + " UNION " + sqlQuery2
							+ " Order By Zone,Alphanumeric, From_Zipcode";
					stmt = connection.prepareStatement(sqlQuery);
					stmt.setString(1, masterDOB.getOriginLocation());
					stmt.setString(2, masterDOB.getShipperMode());
					if ("1".equalsIgnoreCase(masterDOB.getShipperMode()))
						stmt.setString(3, "~");
					else
						stmt.setString(3, masterDOB.getShipperConsoleType());

					int k = 0;
					if (shZoneArray != null && shZoneArray.length > 0) {
						int shZoneArrLen = shZoneArray.length;
						for (k = 0; k < shZoneArrLen; k++)
							stmt.setString(k + 4, shZoneArray[k]);
						k = k + 3;
					} else
						k = k + 2;

					stmt.setString(k + 1, masterDOB.getDestLocation());
					stmt.setString(k + 2, masterDOB.getConsigneeMode());

					if ("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
						stmt.setString(k + 3, "~");
					else
						stmt.setString(k + 3, masterDOB
								.getConsigneeConsoleType());

					if (consigneeZoneArray != null
							&& consigneeZoneArray.length > 0) {
						int conZoneArrLen = consigneeZoneArray.length;
						for (int j = 0; j < conZoneArrLen; j++)
							stmt.setString(j + k + 4, consigneeZoneArray[j]);
					}
					rs = stmt.executeQuery();

				} else if (masterDOB.getShipperZipCode() == null
						&& masterDOB.getConsigneeZipCode() != null) {
					sqlQuery = sqlQuery1
							+ " Order By Zone,Alphanumeric, From_Zipcode";
					stmt = connection.prepareStatement(sqlQuery);
					stmt.setString(1, masterDOB.getOriginLocation());
					stmt.setString(2, masterDOB.getShipperMode());
					if ("1".equalsIgnoreCase(masterDOB.getShipperMode()))
						stmt.setString(3, "~");
					else
						stmt.setString(3, masterDOB.getShipperConsoleType());

					if (shZoneArray != null && shZoneArray.length > 0) {
						int shZoneArrLen = shZoneArray.length;
						for (int k = 0; k < shZoneArrLen; k++)
							stmt.setString(k + 4, shZoneArray[k]);
					}
					rs = stmt.executeQuery();
				} else if (masterDOB.getShipperZipCode() != null
						&& masterDOB.getConsigneeZipCode() == null) {
					sqlQuery = sqlQuery2
							+ "Order By Zone,Alphanumeric, From_Zipcode";
					stmt = connection.prepareStatement(sqlQuery);
					stmt.setString(1, masterDOB.getDestLocation());
					stmt.setString(2, masterDOB.getConsigneeMode());
					if ("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
						stmt.setString(3, "~");
					else
						stmt.setString(3, masterDOB.getConsigneeConsoleType());

					if (consigneeZoneArray != null
							&& consigneeZoneArray.length > 0) {
						int conZoneArrLen = consigneeZoneArray.length;
						for (int k = 0; k < conZoneArrLen; k++)
							stmt.setString(k + 4, consigneeZoneArray[k]);
					}
					rs = stmt.executeQuery();

				}
			} else {
				if (masterDOB.getShipperZones() == null
						&& masterDOB.getConsigneeZones() != null
						&& masterDOB.getConsigneeZipCode() == null) {
					sqlQuery = sqlQuery2
							+ " Order By Zone,Alphanumeric, From_Zipcode";
					stmt = connection.prepareStatement(sqlQuery);
					stmt.setString(1, masterDOB.getDestLocation());
					stmt.setString(2, masterDOB.getConsigneeMode());
					if ("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
						stmt.setString(3, "~");
					else
						stmt.setString(3, masterDOB.getConsigneeConsoleType());

					if (consigneeZoneArray != null
							&& consigneeZoneArray.length > 0) {
						int conZoneArrLen = consigneeZoneArray.length;
						for (int k = 0; k < conZoneArrLen; k++)
							stmt.setString(k + 4, consigneeZoneArray[k]);
					}
					rs = stmt.executeQuery();
				} else if (masterDOB.getShipperZones() != null
						&& masterDOB.getConsigneeZones() == null
						&& masterDOB.getShipperZipCode() == null) {
					sqlQuery = sqlQuery1
							+ " Order By Zone,Alphanumeric, From_Zipcode";
					stmt = connection.prepareStatement(sqlQuery);
					stmt.setString(1, masterDOB.getOriginLocation());
					stmt.setString(2, masterDOB.getShipperMode());
					if ("1".equalsIgnoreCase(masterDOB.getShipperMode()))
						stmt.setString(3, "~");
					else
						stmt.setString(3, masterDOB.getShipperConsoleType());

					if (shZoneArray != null && shZoneArray.length > 0) {
						int shZoneArrLen = shZoneArray.length;
						for (int k = 0; k < shZoneArrLen; k++)
							stmt.setString(k + 4, shZoneArray[k]);
					}
					rs = stmt.executeQuery();
				}
			}

			if (rs != null) {
				while (rs.next()) {
					if ("Pickup".equalsIgnoreCase(rs.getString("CHARGE_TYPE"))) {
						String from_toZip = null;
						if (pickUpZoneZipMap.containsKey(rs.getString("ZONE"))) {
							pickUpZipCodes = (ArrayList) pickUpZoneZipMap
									.get(rs.getString("ZONE"));
							if (rs.getString("ALPHANUMERIC") != null)
								from_toZip = rs.getString("ALPHANUMERIC")
										+ rs.getString("FROM_ZIPCODE") + " - "
										+ rs.getString("ALPHANUMERIC")
										+ rs.getString("TO_ZIPCODE");
							else
								from_toZip = rs.getString("FROM_ZIPCODE")
										+ " - " + rs.getString("TO_ZIPCODE");
							if (from_toZip != null)
								pickUpZipCodes.add(from_toZip);
							pickUpZoneZipMap.put(rs.getString("ZONE"),
									pickUpZipCodes);
						} else {
							pickUpZipCodes = new ArrayList();
							if (rs.getString("FROM_ZIPCODE") != null) {
								if (rs.getString("ALPHANUMERIC") != null)
									from_toZip = rs.getString("ALPHANUMERIC")
											+ rs.getString("FROM_ZIPCODE")
											+ " - "
											+ rs.getString("ALPHANUMERIC")
											+ rs.getString("TO_ZIPCODE");
								else
									from_toZip = rs.getString("FROM_ZIPCODE")
											+ " - "
											+ rs.getString("TO_ZIPCODE");
								if (from_toZip != null)
									pickUpZipCodes.add(from_toZip);
								pickUpZoneZipMap.put(rs.getString("ZONE"),
										pickUpZipCodes);
							}
						}
					} else if ("Delivery".equalsIgnoreCase(rs
							.getString("CHARGE_TYPE"))) {
						String from_toZip = null;
						if (deliveryZoneZipMap
								.containsKey(rs.getString("ZONE"))) {
							delivZipCodes = (ArrayList) deliveryZoneZipMap
									.get(rs.getString("ZONE"));
							if (rs.getString("ALPHANUMERIC") != null)
								from_toZip = rs.getString("ALPHANUMERIC")
										+ rs.getString("FROM_ZIPCODE") + " - "
										+ rs.getString("ALPHANUMERIC")
										+ rs.getString("TO_ZIPCODE");
							else
								from_toZip = rs.getString("FROM_ZIPCODE")
										+ " - " + rs.getString("TO_ZIPCODE");
							if (from_toZip != null)
								delivZipCodes.add(from_toZip);
							deliveryZoneZipMap.put(rs.getString("ZONE"),
									delivZipCodes);
						} else {
							delivZipCodes = new ArrayList();
							if (rs.getString("ALPHANUMERIC") != null)
								from_toZip = rs.getString("ALPHANUMERIC")
										+ rs.getString("FROM_ZIPCODE") + " - "
										+ rs.getString("ALPHANUMERIC")
										+ rs.getString("TO_ZIPCODE");
							else
								from_toZip = rs.getString("FROM_ZIPCODE")
										+ " - " + rs.getString("TO_ZIPCODE");
							if (from_toZip != null)
								delivZipCodes.add(from_toZip);
							deliveryZoneZipMap.put(rs.getString("ZONE"),
									delivZipCodes);
						}
					}
				}
			}

			if (pickUpZoneZipMap.size() > 0)
				finalDOB.setPickZoneZipMap(pickUpZoneZipMap);
			if (deliveryZoneZipMap.size() > 0)
				finalDOB.setDeliveryZoneZipMap(deliveryZoneZipMap);

		} catch (SQLException sql) {
			// Logger.error(FILE_NAME,"SQLException while getting the Zip Zone
			// Code Mapping: "+sql);
			logger.error(FILE_NAME
					+ "SQLException while getting the Zip Zone Code Mapping: "
					+ sql);
			sql.printStackTrace();
			throw new SQLException(sql.toString());
		} catch (Exception e) {
			// Logger.error(FILE_NAME,"Error while getting the Zip Zone Code
			// Mapping: "+e);
			logger.error(FILE_NAME
					+ "Error while getting the Zip Zone Code Mapping: " + e);
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		return finalDOB;
	}

	public String validateCurrency(String baseCurrency) throws Exception {
		String errorMsg = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT currencyid FROM FS_COUNTRYMASTER WHERE currencyid =?";
			connection = getConnection();
			// System.out.println("in dao");
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, baseCurrency);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				errorMsg = "CurrencyID is not valid :" + baseCurrency;
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {

			}
		}
		return errorMsg;
	}

	// @@Added by Kameswari for the WPBN issue-61289
	public void insertAttachmentIdList(QuoteFinalDOB finalDOB) throws Exception {

		Connection connection = null;
		PreparedStatement pst = null;
		QuoteMasterDOB masterDOB = null;
		QuoteAttachmentDOB attachmentDOB = null;
		ArrayList attachmentIdList = null;
		long quoteId;
		int k = 0;
		String insertIdQuery = "INSERT INTO QMS_QUOTE_ATTACHMENTDTL(ID,QUOTE_ID,ATTACHMENT_ID) VALUES(SEQ_QUOTE_ATTACHDTL_ID .NEXTVAL,?,?)";
		try {
			attachmentIdList = finalDOB.getAttachmentDOBList();
			masterDOB = finalDOB.getMasterDOB();
			quoteId = masterDOB.getUniqueId();
			connection = getConnection();
			pst = connection.prepareStatement(insertIdQuery);
			int attachmntIdSize = attachmentIdList.size();
			for (int i = 0; i < attachmntIdSize; i++) {
				attachmentDOB = (QuoteAttachmentDOB) attachmentIdList.get(i);
				pst.setLong(1, quoteId);
				pst.setString(2, attachmentDOB.getAttachmentId());
				k = pst.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			if (pst != null)
				pst.close();
			if (connection != null)
				connection.close();
		}
	}

	public void updateAttachmentIdList(QuoteFinalDOB finalDOB) throws Exception {
		Connection connection = null;
		PreparedStatement pst = null;
		QuoteMasterDOB masterDOB = null;
		long quoteId;
		String deleteIdQuery = "DELETE  FROM QMS_QUOTE_ATTACHMENTDTL WHERE QUOTE_ID=?";
		int i = 0;
		try {
			masterDOB = finalDOB.getMasterDOB();
			quoteId = masterDOB.getUniqueId();
			connection = getConnection();
			pst = connection.prepareStatement(deleteIdQuery);
			pst.setLong(1, quoteId);

			i = pst.executeUpdate();
			insertAttachmentIdList(finalDOB);

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception();
		} finally {
			if (pst != null)
				pst.close();
			if (connection != null)
				connection.close();
		}
	}

	// @@ Added by subrahmanyam for the Enhancement 180161 on 28/08/09
	private String roundDecimal(double sellRate) {

		java.text.DecimalFormat deciFormat = new java.text.DecimalFormat(
				"##0.00000");
		String rateString = "";
		int k = 0;
		int l = 0;
		int m = 0;
		rateString = Double.toString(sellRate);
		k = rateString.length();
		l = rateString.indexOf(".");
		m = (k - l) + 1;
		if (m > 5)
			rateString = deciFormat.format(sellRate);

		return rateString;

	}

	// @@ Ended by subrahmanyam for the Enhancement 180161 on 28/Aug/09

	private boolean isNumber(String s) {
		// TODO Auto-generated method stub
		try {
			Double d = Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	// Added by Mohan for issue no.219979 on 10122010
	public ArrayList getQuoteLaneDetails(CostingHDRDOB costingHDRDOB)
			throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		CostingHDRDOB dob = null;
		ArrayList alist = null;
		try {
			conn = this.getConnection();
			pstmt = conn
					.prepareStatement("SELECT X.QUOTE_ID QUOTE_NO,X.ID QUOTE_ID,X.ORIGIN_PORT,"
							+ " X.DESTIONATION_PORT,X.MULTI_QUOTE_LANE_NO,"
							//+ " X.MULTI_QUOTE_SERVICE_LEVEL," 
							//+ " X.MULTI_QUOTE_CARRIER_ID,"  @@ Commented and modified by govins for the issue 269085
							+ " NVL(X.MULTI_QUOTE_SERVICE_LEVEL,(SELECT WM_CONCAT(DISTINCT QR.SRVLEVEL) FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID = X.ID AND QR.SRVLEVEL != 'SCH'))SERVICE_LEVEL,"
							+ " NVL(X.MULTI_QUOTE_CARRIER_ID,(SELECT WM_CONCAT(DISTINCT DT.CARRIER_ID) FROM QMS_BUYRATES_DTL DT,QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID = X.ID AND DT.BUYRATEID= QR.BUYRATE_ID AND DT.LANE_NO = QR.RATE_LANE_NO AND DT.VERSION_NO=QR.VERSION_NO AND DT.ACTIVEINACTIVE IS NULL AND DT.SERVICE_LEVEL != 'SCH'AND QR.SRVLEVEL != 'SCH'))CARRIER_ID,"		
							+ " X.CUSTOMER_ID,X.SHIPMENT_MODE FROM QMS_QUOTE_MASTER X WHERE X.QUOTE_ID=?"
							+ " AND X.VERSION_NO =(SELECT MAX(Y.VERSION_NO) FROM QMS_QUOTE_MASTER Y WHERE Y.QUOTE_ID=?)");
			if (costingHDRDOB != null) {
				pstmt.setString(1, costingHDRDOB.getQuoteid());
				pstmt.setString(2, costingHDRDOB.getQuoteid());
				rs = pstmt.executeQuery();
				alist = new ArrayList();
				while (rs.next()) {
					dob = new CostingHDRDOB();
					dob.setQuoteid(rs.getString(1));// QUOTE NO(STRING)
					dob.setQuoteId(rs.getLong(2));// QUOTE ID(LONG)
					dob.setOrigin(rs.getString(3));
					dob.setDestination(rs.getString(4));
					dob.setLaneNo(rs.getString(5));
					dob.setServiceLevel(rs.getString(6));
					dob.setCarrier(rs.getString(7));
					dob.setCustomerid(rs.getString(8));
					dob.setShipmentMode(rs.getInt(9));
					costingHDRDOB.setShipmentMode(rs.getInt(9));
					alist.add(dob);
					dob = null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		}
		return alist;
	}

}
// @@the WPBN issue-61289

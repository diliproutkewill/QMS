/**
*
* @ Author : Sailaja K.V.
* @ Version : eTrans 1.8
* @ Date : 15/03/05 
**/
package com.foursoft.esupply.common.dao;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import javax.ejb.EJBException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.foursoft.esupply.common.java.ETPRQAdvVO;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;

public class ETPRQAdvDAO extends ETAdvancedLOVMasterDAO
{
	public static String FILE_NAME = "ETPRQAdvDAO.java";
  private static Logger logger = null;

	public ETPRQAdvDAO()
	{
    logger  = Logger.getLogger(ETPRQAdvDAO.class);
	}
  
public  ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
{  
	ArrayList details =new ArrayList();
	String query=null;
	PreparedStatement pStmt = null;
	Connection connection = null;
	ResultSet rs=null;
	ETPRQAdvVO prqAttributes =null;
	String searchString =null;
		   
	try 
	{
		prqAttributes = (ETPRQAdvVO)searchValues;
		
		String terminalID = prqAttributes.getTerminalID();
		String shipmentMode = prqAttributes.getMode();
		String terminalType=prqAttributes.getTerminalType();
		String shipperId = prqAttributes.getShipperId();
		String consigneeId = prqAttributes.getConsigneeId();
		String originTerminal = prqAttributes.getOriginLocation();
		String destinationTerminal = prqAttributes.getDestinationLocation();
		String UOW = prqAttributes.getUOW();
		String UOM = prqAttributes.getUOM();
		String consoleType = prqAttributes.getConsoleType();
		StringBuffer whereClause =new StringBuffer();
		if(terminalType.equals("NSIB"))
		{
			terminalType="N";
		}

		if(shipmentMode.equals("Air"))
		{
			shipmentMode	=	"1";
		}
		else if(shipmentMode.equals("Sea"))
		{
			shipmentMode	=	"2";
		}
		else if(shipmentMode.equals("Truck"))
		{
			shipmentMode	=	"4";
		}
		if(shipmentMode == "2")
		{ 
			//Logger.info(FILE_NAME,"ConsoleType------->",prqAttributes.getConsoleType());

			if(prqAttributes.getConsoleType().equalsIgnoreCase("LCL_CONSOLE"))
			{
				consoleType	=	" IN ('LCL_TO_LCL','LCL_TO_FCL') ";
			}
			else if(prqAttributes.getConsoleType().equalsIgnoreCase("FCL_CONSOLE"))
			{
				consoleType	=	" IN ('FCL_TO_FCL') ";				
			}
			else if(prqAttributes.getConsoleType().equalsIgnoreCase("BOTH_LCL_FCL"))
			{
			consoleType	=	" IN ('LCL_TO_LCL','LCL_TO_FCL','FCL_TO_FCL') ";
			}
			else
			{
				consoleType=(getSearchString(searchValues.getSearchType(),prqAttributes.getConsoleType().trim()));
			}
		}

		connection = this.getConnection();            
		//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6159 on 20050419.
		StringBuffer selectClause		=new StringBuffer("SELECT DISTINCT PRQ.PRQID PRQID,");
		selectClause.append("PRQ.SHIPPERID SHIPPERID,");
		selectClause.append("SHIPPER.COMPANYNAME SHIPPERNAME,");
		selectClause.append("PRQ.CONSIGNEEID CONSIGNEEID,");
		selectClause.append("CONSIGNEE.COMPANYNAME CONSIGNEENAME,");
		selectClause.append("PRQ.ORIGINTERMINAL ORIGINTERMINAL,");
		selectClause.append("PRQ.DESTTERMINAL DESTTERMINAL,");
		selectClause.append("PRQ.TOTALWEIGHT ACTUALWEIGHT,");
		selectClause.append("PRQ.TOTALPCS NOOFPACKS,");
		// @@ Modified by Sailaja on 2005 05 12 for SPETI-5238
		selectClause.append("PRQ.ACTUALVOLUME VOLUME ");				 
		// @@ 2005 05 12 for SPETI-5238                                                       

		StringBuffer fromClause =new StringBuffer(" FROM FS_FR_PICKUPREQUEST PRQ ,FS_FR_CUSTOMERMASTER SHIPPER "+
                                                      ",FS_FR_CUSTOMERMASTER CONSIGNEE ");		

		whereClause=new StringBuffer("WHERE SHIPPER.CUSTOMERID(+)=PRQ.SHIPPERID AND CONSIGNEE.CUSTOMERID(+)=PRQ.CONSIGNEEID AND PRQ.SHIPMENTMODE='"+shipmentMode+"' ");
                    	
		//Logger.info(FILE_NAME,"...............ETPRQAdvDAO........prqId..........."+prqAttributes.getPrqId());
		if(prqAttributes.getPrqId()!=null && !prqAttributes.getPrqId().equals(""))
		{ 
			whereClause.append(" AND PRQ.PRQID ");
			whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getPrqId().trim()));
		}
		if(prqAttributes.getShipperName()!=null && !prqAttributes.getShipperName().equals(""))
		{ 
			whereClause.append(" AND SHIPPER.COMPANYNAME ");
            whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getShipperName().trim()));
		}
		if(prqAttributes.getConsigneeName()!=null && !prqAttributes.getConsigneeName().equals(""))
		{ 
			whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
            whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getConsigneeName().trim()));
		}
		if(prqAttributes.getOriginLocation()!=null && !prqAttributes.getOriginLocation().equals(""))
		{ 
			whereClause.append(" AND PRQ.ORIGINTERMINAL ");
            whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getOriginLocation().trim()));
		}
		if(prqAttributes.getDestinationLocation()!=null && !prqAttributes.getDestinationLocation().equals(""))
		{ 
			whereClause.append(" AND PRQ.DESTTERMINAL ");
			whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getDestinationLocation().trim()));
		}
		if(prqAttributes.getOriginCountryId()!=null && !prqAttributes.getOriginCountryId().equals(""))
		{ 
			fromClause.append(",FS_FR_LOCATIONMASTER ORCOUNTRY  ");
			whereClause.append(" AND ORCOUNTRY.LOCATIONID = PRQ.ORIGINID "); 
			whereClause.append("AND ORCOUNTRY.COUNTRYID ");
			whereClause.append(searchString = getSearchString(searchValues.getSearchType(),prqAttributes.getOriginCountryId().trim()));
		}
		if(prqAttributes.getDestinationCountryId()!=null && !prqAttributes.getDestinationCountryId().equals(""))
		{ 
			fromClause.append(",FS_FR_LOCATIONMASTER DECOUNTRY  ");
			whereClause.append(" AND DECOUNTRY.LOCATIONID = PRQ.DESTINATIONID "); 
			whereClause.append("AND DECOUNTRY.COUNTRYID ");
			whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getDestinationCountryId().trim()));
		}
		if(shipmentMode == "2")
		{
			if(prqAttributes.getConsoleType()!=null && !prqAttributes.getConsoleType().equals(""))
			{ 
				whereClause.append(" AND PRQ.MASTERTYPE ");
				whereClause.append(consoleType);
			}
		}
		else if(shipmentMode == "4")
		{
			if(prqAttributes.getConsoleType()!=null && !prqAttributes.getConsoleType().equals(""))
			{ 
				whereClause.append(" AND PRQ.TRUCKLOAD ");
				whereClause.append(getSearchString(searchValues.getSearchType(),prqAttributes.getConsoleType().trim()));
			}
		}
    //Logger.info(FILE_NAME,"...............ETPRQAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
		if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		{
			searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays(),"PRQ.PRQDATE");
			whereClause.append(searchString);
		}
			//Logger.info(FILE_NAME,"...ETPRQAdvDAO..invokerOperation...at line170...@@@@@@.."+prqAttributes.getInvokerOperation());
			if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQModify"))
			{
				whereClause.append(" AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) "); 
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') "); 
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQCopy") || prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQView"))
			{
				whereClause.append(" AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append("AND PRQ.OPENBOOKINGSTATUS ='N' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQConvert"))
			{
				whereClause.append(" AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) "); 
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.MASTERDOCID IS NULL ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQDelete"))
			{
				whereClause.append(" AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) "); 
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') "); 
				whereClause.append("AND PRQ.PRQID NOT  IN(SELECT DISTINCT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOpenPRQ"))
			{
				whereClause.append(" AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.OPENBOOKINGSTATUS='Y' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirPRQGroup"))
			{
				fromClause.append(",FS_FR_PRQPACKDTL PACK ");
				whereClause.append(" AND PRQ.PRQSTATUS='P' ");
				whereClause.append("AND PRQ.SHIPPERID='");
				whereClause.append(shipperId);
				whereClause.append("' ");
				whereClause.append("AND PRQ.CONSIGNEEID='");
				whereClause.append(consigneeId);
				whereClause.append("' ");
				whereClause.append("AND PACK.PRQID(+) = PRQ.PRQID "); 					
				whereClause.append("AND PRQ.TRUCKID IS NULL ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQSTATUS='P' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) ");
				whereClause.append("AND PACK.PRQID(+) = PRQ.PRQID ");
				whereClause.append("AND PRQ.UOW = '"); 
				whereClause.append(UOW);
				whereClause.append("' ");	
				whereClause.append("AND (PACK.UOM = '");
				whereClause.append(UOM);
				whereClause.append("' "); 
				whereClause.append("OR PACK.UOM IS NULL) ");
			}	
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckPRQModify"))
			{
				whereClause.append(" AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) "); 
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') "); 
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckPRQCopy") || prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckPRQView"))
			{
				//Logger.info(FILE_NAME,"....................................TruckPRQCopy...........");
				whereClause.append(" AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append("AND PRQ.OPENBOOKINGSTATUS ='N' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckPRQDelete"))
			{
				whereClause.append(" AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND NVL(PRQ.HAULAGEINDICATOR,' ') <>'H' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) "); 
				whereClause.append("AND (PRQ.GROUPEDWITH IS NULL AND PRQ.GROUPED='N') "); 
				whereClause.append("AND PRQ.PRQID NOT  IN(SELECT DISTINCT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOpenPRQ"))
			{
				whereClause.append(" AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.OPENBOOKINGSTATUS='Y' ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQModify"))
			{
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append(" AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQID IN( SELECT PRQID FROM FS_FR_PICKUPREQUEST ");
				whereClause.append("WHERE DIRECTCONSOLE='N' AND CONSOLESTATUS='N' AND (OPENBOOKINGSTATUS='N' ");
				whereClause.append("OR OPENBOOKINGSTATUS IS NULL) MINUS( SELECT B.PRQID ");
				whereClause.append("FROM FS_FR_PICKUPREQUEST B,FS_FR_HOUSEDOCHDR H WHERE H.SHIPMENTMODE=2 ");
				whereClause.append("AND  B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=H.PRQID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL)");
				whereClause.append("UNION  SELECT B.PRQID FROM FS_FR_PICKUPREQUEST B, ");
				whereClause.append("FS_FR_WHMASTER WH WHERE B.PRQID=WH.BOOKINGID ) ) "); 
			}
			 else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQCopy") || prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQView"))
			{
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND (PRQ.OPENBOOKINGSTATUS='N' OR PRQ.OPENBOOKINGSTATUS IS NULL) ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQDelete"))
			{
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append("AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQID IN( SELECT PRQID FROM FS_FR_PICKUPREQUEST ");
				whereClause.append("WHERE DIRECTCONSOLE='N' AND CONSOLESTATUS='N' AND (OPENBOOKINGSTATUS='N' ");
				whereClause.append("OR OPENBOOKINGSTATUS IS NULL) MINUS SELECT B.PRQID ");
				whereClause.append("FROM FS_FR_PICKUPREQUEST B,FS_FR_HOUSEDOCHDR H WHERE H.SHIPMENTMODE=2 ");
				whereClause.append("AND  B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=H.PRQID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL) ");
				whereClause.append("MINUS SELECT B.PRQID FROM FS_FR_PICKUPREQUEST B,FS_FR_WHMASTER W "); 
				whereClause.append("WHERE B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=W.BOOKINGID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL)) ");
				whereClause.append("AND PRQID NOT IN (SELECT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQConvert"))
			{
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append("AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append(" AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQSTATUS!='H' ");
				whereClause.append("AND (PRQ.OPENBOOKINGSTATUS='N' OR PRQ.OPENBOOKINGSTATUS IS NULL) "); 
				whereClause.append("AND PRQ.CONSOLESTATUS='N' ");  
				whereClause.append("AND (PRQ.DIRECTCONSOLE IS NULL OR PRQ.DIRECTCONSOLE='N') "); 
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOpenPRQ"))
			{

				whereClause.append(" AND PRQ.OPENBOOKINGSTATUS='Y' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaPRQGroup"))
			{
				fromClause.append(",FS_FR_PRQPACKDTL PACK ");
				whereClause.append(" AND PRQ.SHIPPERID='");
				whereClause.append(shipperId);
				whereClause.append("' ");
				whereClause.append("AND PRQ.CONSIGNEEID='");
				whereClause.append(consigneeId);
				whereClause.append("' ");
				whereClause.append(" AND PRQ.PRQSTATUS='P' ");
				whereClause.append("AND PACK.PRQID(+) = PRQ.PRQID "); 
				whereClause.append("AND PRQ.UOW = '"); 
				whereClause.append(UOW);
				whereClause.append("' ");					
				whereClause.append("AND (PACK.UOM = '");
				whereClause.append(UOM);
				whereClause.append("' ");
				whereClause.append("OR PACK.UOM IS NULL) "); 
				whereClause.append("AND PRQ.OPENBOOKINGSTATUS='N'  AND PRQ.GROUPED='N' ");  
				whereClause.append("AND (PRQ.WHSTATUS='BNR' OR PRQ.TERMINALWHSTATUS='NBNRT') ");
			}			
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginWHAdd") || prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOriginWHAdd") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                 whereClause.append(" AND PRQ.ORIGINTERMINAL='");
                 whereClause.append(terminalID);
                 whereClause.append("' ");
                 whereClause.append(" AND PRQ.PRQSTATUS='P' "); 
                 whereClause.append(" AND PRQ.TERMINALWHSTATUS LIKE '%BNRT' ");
                 whereClause.append(" AND PRQ.PICKUP = 'N' ");
                 whereClause.append(" AND PRQ.WHSTATUS LIKE '%BNR'");
            }
            
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginWHModify") || prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOriginWHModify") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                 fromClause.append(",FS_FR_WHMASTER WH ");  
                 whereClause.append(" AND PRQ.PRQID = WH.BOOKINGID "); 
                 whereClause.append(" AND PRQ.PRQSTATUS='P' "); 
                 whereClause.append(" AND PRQ.TERMINALWHSTATUS LIKE '%BPRT' ");
                 whereClause.append(" AND PRQ.WHSTATUS LIKE '%BPR' ");  
                 whereClause.append(" AND WH.TERMINALID='");
                 whereClause.append(terminalID);
                 whereClause.append("' ");
                 whereClause.append("AND OPERATIONTYPE = 'Origin' ");
                 whereClause.append("UNION ");
                 whereClause.append(selectClause);
                 whereClause.append(" FROM FS_FR_PICKUPREQUEST PRQ ,FS_FR_CUSTOMERMASTER SHIPPER,");
                 whereClause.append("FS_FR_CUSTOMERMASTER CONSIGNEE,FS_FR_WHMASTER WH,FS_FR_HOUSEDOCHDR HR ");                                   
                 whereClause.append("WHERE PRQ.PRQID = WH.BOOKINGID  ");
                 whereClause.append("AND PRQ.PRQID = HR.PRQID ");
                 whereClause.append("AND HR.MASTERSTATUS != 'CLOSED' ");
                 whereClause.append("AND  PRQ.PRQID NOT IN  (SELECT PRQID FROM FS_FR_HOUSEDOCHDR) ");
                 whereClause.append("AND HR.PRQID IS NOT NULL AND ((PRQ.TERMINALWHSTATUS LIKE '%BPRT' ");
                 whereClause.append("AND PRQ.WHSTATUS LIKE '%BPR') OR (PRQ.TERMINALWHSTATUS LIKE '%BRT' AND PRQ.WHSTATUS LIKE '%BR')) ");
                 whereClause.append("AND  WH.TERMINALID='");
                 whereClause.append(terminalID);
                 whereClause.append("' ");
                 whereClause.append("AND WH.SHIPMENTMODE ='");
                 whereClause.append(shipmentMode);
                 whereClause.append("' ");
                 whereClause.append("AND OPERATIONTYPE = 'Origin' "); 
            }
            else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginWHView") || prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOriginWHView") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                 fromClause.append(",FS_FR_WHMASTER WH "); 
                 whereClause.append(" AND PRQ.PRQID = WH.BOOKINGID ");
                 whereClause.append("AND  WH.TERMINALID='");
                 whereClause.append(terminalID);
                 whereClause.append("' ");
                 whereClause.append("AND OPERATIONTYPE = 'Origin' ");
            }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginWHDelete") || prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOriginWHDelete") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                 fromClause.append(",FS_FR_WHMASTER WH "); 
                 whereClause.append(" AND PRQ.PRQID = WH.BOOKINGID ");
                 whereClause.append("AND  WH.TERMINALID='");
                 whereClause.append(terminalID);
                 whereClause.append("' ");
                 whereClause.append("AND PRQ.PRQSTATUS='P' ");
                 whereClause.append("AND CONSOLESTATUS!='CLOSED' ");
                 whereClause.append("AND PRQ.PICKUP='N' ");
                 whereClause.append("AND OPERATIONTYPE = 'Origin' ");
            }			
			
            else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOriginWHAdd") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                whereClause.append(" AND PRQ.OPENBOOKINGSTATUS='N' ");
                whereClause.append("AND PRQ.PRQSTATUS='P' ");
                whereClause.append("AND PRQ.PICKUP = 'N' ");
                whereClause.append("AND PRQ.GROUPED='N' ");
                whereClause.append("AND PRQ.ORIGINTERMINAL IN ( SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY WHERE GATEWAYID='");
                whereClause.append(terminalID);
                whereClause.append("' ");
                whereClause.append("AND TERMINALID!=GATEWAYID) "); 
                whereClause.append("AND PRQ.TERMINALWHSTATUS LIKE '%BRT' ");
                whereClause.append("AND PRQ.WHSTATUS LIKE '%BNR' ");
                whereClause.append("AND PRQ.PICKUPAT = 'CONSOLE' ");
                whereClause.append("AND PRQ.CONSOLESTATUS='N' UNION ");
                whereClause.append(selectClause);
                whereClause.append(" FROM FS_FR_PICKUPREQUEST PRQ ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
                whereClause.append("WHERE SHIPPER.CUSTOMERID(+)=PRQ.SHIPPERID AND CONSIGNEE.CUSTOMERID(+)=PRQ.CONSIGNEEID ");
                whereClause.append(" AND PRQ.SHIPMENTMODE='");
                whereClause.append(shipmentMode);              
                whereClause.append("' ");					
                whereClause.append("AND PRQ.OPENBOOKINGSTATUS='N' ");
                whereClause.append("AND PRQ.PRQSTATUS='P' ");
                whereClause.append("AND PRQ.PICKUP = 'N' ");
                whereClause.append("AND PRQ.GROUPED='N' ");
                whereClause.append("AND PRQ.ORIGINTERMINAL='");
                whereClause.append(terminalID);
                whereClause.append("' ");
                whereClause.append("AND PRQ.TERMINALWHSTATUS LIKE '%BNRT' "); 
                whereClause.append("AND PRQ.MASTERTYPE ");
                whereClause.append(consoleType);
                whereClause.append(" AND PRQ.PICKUPAT = 'CONSOLE' ");
            }
            else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOriginWHModify") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                fromClause.append(",FS_FR_WHMASTER WH ");
                whereClause.append(" AND WH.BOOKINGID=PRQ.PRQID "); 
                whereClause.append("AND PRQ.GROUPED='N' "); 
                whereClause.append("AND WH.TERMINALID='"); 
                whereClause.append(terminalID);
                whereClause.append("' ");
                whereClause.append("AND PRQ.PRQSTATUS='P' ");
                whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%BPRT' OR PRQ.WHSTATUS LIKE '%BPR') "); 
                whereClause.append("AND PRQ.CONSOLESTATUS='N' ");
                whereClause.append("AND (PRQ.PICKUPAT IS NULL OR PRQ.PICKUPAT = 'CONSOLE') "); 
                whereClause.append("AND WH.OPERATIONTYPE = 'Origin' ");  
            }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOriginWHView"))
            {
                fromClause.append(",FS_FR_WHMASTER WH ");
                whereClause.append(" AND WH.BOOKINGID=PRQ.PRQID "); 
                whereClause.append("AND PRQ.GROUPED='N' ");
                whereClause.append("AND WH.TERMINALID='");
                whereClause.append(terminalID);
                whereClause.append("' ");
                whereClause.append("AND PRQ.MASTERTYPE NOT IN ('FCL_BACK_TO_BACK ','BREAK_BULK') ");
                whereClause.append("AND PRQ.PICKUPAT = 'CONSOLE' "); 
                whereClause.append("AND WH.OPERATIONTYPE = 'Origin' ");
          
            }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOriginWHDelete") && prqAttributes.getOperationType().equalsIgnoreCase("Origin"))
            {
                fromClause.append(",FS_FR_WHMASTER WH ");
                whereClause.append(" AND WH.BOOKINGID=PRQ.PRQID "); 
                whereClause.append("AND PRQ.GROUPED='N' ");
                whereClause.append("AND PRQ.CONSOLESTATUS='N' ");
                whereClause.append("AND WH.TERMINALID='"); 
                whereClause.append(terminalID);
                whereClause.append("' ");
                whereClause.append("AND PRQ.PRQSTATUS = 'P' ");
                whereClause.append("AND PRQ.PICKUPAT = 'CONSOLE' ");
                whereClause.append("AND PRQ.PICKUP='N' ");
                whereClause.append("AND CONSOLESTATUS!='CLOSED' "); 
                whereClause.append("AND WH.OPERATIONTYPE = 'Origin' ");
            }
					

			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckOriginCNAdd"))
			{
				  whereClause.append(" AND PRQ.ORIGINTERMINAL ='"); 
				  whereClause.append(terminalID);
				  whereClause.append("' ");
				  whereClause.append("AND PRQSTATUS='P' "); 
				  whereClause.append("AND ( ( PICKUP = 'Y'  AND PRQID IN ( SELECT BOOKINGID FROM FS_FR_WHMASTER ) ) OR PICKUP = 'N' ) ");
		
        }
        else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaOriginHBLAdd"))
        {
				 
				  whereClause.append(" AND PRQ.ORIGINTERMINAL='"); 
				  whereClause.append(terminalID);
				  whereClause.append("' ");
				  whereClause.append("AND PRQ.PRQSTATUS='P' ");
				  whereClause.append(" AND PRQ.OPENBOOKINGSTATUS='N' ");
				  whereClause.append("AND PRQ.GROUPED='N' ");
				  whereClause.append("AND ( ( PRQ.PICKUP = 'Y' ");
                  whereClause.append("AND PRQ.PRQID IN ( SELECT BOOKINGID FROM FS_FR_WHMASTER ) ) ");
				  whereClause.append("OR ( PRQ.PICKUP = 'N' AND PRQ.PRQID IN ( SELECT BOOKINGID FROM FS_FR_WHMASTER ) )	) ");
          }
          else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaINSHBLAdd"))
          {
              whereClause.append(" AND PRQ.DESTTERMINAL='"); 
              whereClause.append(terminalID);
              whereClause.append("' ");
              whereClause.append("AND PRQ.PRQSTATUS='P' ");
              whereClause.append(" AND PRQ.OPENBOOKINGSTATUS='N' ");
              whereClause.append("AND PRQ.GROUPED='N' ");
              whereClause.append("AND ( ( PRQ.PICKUP = 'Y' AND PRQ.PRQID IN ( SELECT BOOKINGID FROM FS_FR_WHMASTER ) ) ");
              whereClause.append("OR ( PRQ.PICKUP = 'N' AND PRQ.PRQID IN ( SELECT BOOKINGID FROM FS_FR_WHMASTER ) )	) ");
	      }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginHAWBAdd") || prqAttributes.getInvokerOperation().equalsIgnoreCase("AirOriginDirectAWBAdd"))
          {
              whereClause.append(" AND PRQ.ORIGINTERMINAL='"); 
              whereClause.append(terminalID);
              whereClause.append("' ");
              whereClause.append("AND PRQSTATUS='P'  ");
              whereClause.append("AND GROUPED='N'  ");
              whereClause.append("AND( ( PICKUP = 'Y'  ");
              whereClause.append("AND EXISTS ( SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE BOOKINGID = PRQID) )  ");
              whereClause.append("OR PICKUP = 'N' )  ");
          }
          else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirINSHAWBAdd"))
          {		
              whereClause.append(" AND (PRQ.TERMINALID='");
              whereClause.append(terminalID);
              whereClause.append("' ");
              whereClause.append(" OR PRQ.DESTTERMINAL='");
              whereClause.append(terminalID);
              whereClause.append("') ");
              whereClause.append("AND PRQ.ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='");
              whereClause.append(terminalType);
              whereClause.append("')  ");
              whereClause.append("AND PRQSTATUS='P'  ");
              whereClause.append("AND GROUPED='N' ");
          }
          
          else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckHaulagePRQModify"))
          {
            whereClause.append(" AND PRQ.ORIGINTERMINAL='"); 
            whereClause.append(terminalID);
            whereClause.append("' ");
            whereClause.append("AND PRQ.PRQSTATUS != 'H' ");
            whereClause.append("AND PRQ.HAULAGEINDICATOR='H' "); 
            whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' ");
            whereClause.append("OR PRQ.WHSTATUS IS NULL) ");
            whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' ");
            whereClause.append("OR PRQ.TERMINALWHSTATUS IS NULL) ");
            whereClause.append("AND PRQ.GROUPED='N' ");
          }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckHaulagePRQView"))
          {
            whereClause.append(" AND PRQ.ORIGINTERMINAL='"); 
            whereClause.append(terminalID);
            whereClause.append("' ");
            whereClause.append("AND PRQ.HAULAGEINDICATOR='H' "); 
            whereClause.append("AND PRQ.OPENBOOKINGSTATUS ='N' ");
            whereClause.append("AND PRQ.GROUPED='N' ");
          }
           else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckHaulagePRQDelete"))
          {
            whereClause.append(" AND PRQ.ORIGINTERMINAL='"); 
            whereClause.append(terminalID);
            whereClause.append("' ");
            whereClause.append("AND PRQ.PRQSTATUS != 'H' ");
            whereClause.append("AND PRQ.HAULAGEINDICATOR='H' "); 
            whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' ");
            whereClause.append("OR PRQ.WHSTATUS IS NULL) ");
            whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' ");
            whereClause.append("OR PRQ.TERMINALWHSTATUS IS NULL) ");
            whereClause.append("AND PRQ.GROUPED='N' ");
            whereClause.append("AND PRQ.PRQID NOT  IN(SELECT DISTINCT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
          }
		 else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirINSPRQCopy") || prqAttributes.getInvokerOperation().equalsIgnoreCase("AirINSPRQView"))
			{ 
          whereClause.append(" AND PRQ.TERMINALID='");
          whereClause.append(terminalID);
          whereClause.append("' "); 
          whereClause.append("AND PRQ.ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER ");
          whereClause.append("WHERE TERMINALTYPE='");
          whereClause.append(terminalType);
          whereClause.append("') ");
          whereClause.append("AND PRQ.OPENBOOKINGSTATUS ='N' ");
          whereClause.append("AND PRQ.GROUPED='N' ");
			}
			
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirINSPRQModify"))
			{ 
				whereClause.append(" AND PRQ.TERMINALID='");
				whereClause.append(terminalID);
        whereClause.append("' "); 
				whereClause.append("AND PRQ.ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER ");
				whereClause.append("WHERE TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("') ");
				whereClause.append("AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND PRQ.GROUPED ='N' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) ");

			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("AirINSPRQDelete"))
			{ 
				whereClause.append(" AND PRQ.TERMINALID='");
				whereClause.append(terminalID);
                whereClause.append("' "); 
				whereClause.append("AND PRQ.ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER ");
				whereClause.append("WHERE TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("') ");
				whereClause.append("AND PRQ.PRQSTATUS != 'H' ");
				whereClause.append("AND PRQ.GROUPED ='N' ");
				whereClause.append("AND (PRQ.WHSTATUS LIKE '%NR' OR PRQ.WHSTATUS IS NULL) ");
				whereClause.append("AND (PRQ.TERMINALWHSTATUS LIKE '%NRT' OR PRQ.TERMINALWHSTATUS IS NULL) ");
				whereClause.append("AND PRQ.PRQID NOT IN(SELECT DISTINCT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
			}
			 else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaINSPRQCopy") || prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaINSPRQView"))
			{ 
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append(" AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append("AND PRQ.DESTTERMINAL='");
				whereClause.append(terminalID);
                whereClause.append("' "); 
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND (PRQ.OPENBOOKINGSTATUS='N' OR PRQ.OPENBOOKINGSTATUS IS NULL) ");

			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaINSPRQModify"))
			{ 
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append(" AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append(" AND PRQ.DESTTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQID IN( SELECT PRQID FROM FS_FR_PICKUPREQUEST ");
				whereClause.append("WHERE DIRECTCONSOLE='N' AND CONSOLESTATUS='N' AND (OPENBOOKINGSTATUS='N' ");
				whereClause.append("OR OPENBOOKINGSTATUS IS NULL) MINUS( SELECT B.PRQID ");
				whereClause.append("FROM FS_FR_PICKUPREQUEST B,FS_FR_HOUSEDOCHDR H WHERE H.SHIPMENTMODE=2 ");
				whereClause.append("AND  B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=H.PRQID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL)");
				whereClause.append("UNION  SELECT B.PRQID FROM FS_FR_PICKUPREQUEST B, ");
				whereClause.append("FS_FR_WHMASTER WH WHERE B.PRQID=WH.BOOKINGID ) ) "); 
			 
			}
			
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("SeaINSPRQDelete"))
			{ 
				fromClause.append(",FS_FR_TERMINALMASTER TM ");
				whereClause.append("AND TM.TERMINALTYPE='");
				whereClause.append(terminalType);
				whereClause.append("' ");
				whereClause.append("AND PRQ.ORIGINTERMINAL= TM.TERMINALID ");
				whereClause.append(" AND PRQ.DESTTERMINAL='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQ.GROUPED='N' ");
				whereClause.append("AND PRQ.PRQID IN( SELECT PRQID FROM FS_FR_PICKUPREQUEST ");
				whereClause.append("WHERE DIRECTCONSOLE='N' AND CONSOLESTATUS='N' AND (OPENBOOKINGSTATUS='N' ");
				whereClause.append("OR OPENBOOKINGSTATUS IS NULL) MINUS SELECT B.PRQID ");
				whereClause.append("FROM FS_FR_PICKUPREQUEST B,FS_FR_HOUSEDOCHDR H WHERE H.SHIPMENTMODE=2 ");
				whereClause.append("AND  B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=H.PRQID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL) ");
				whereClause.append("MINUS SELECT B.PRQID FROM FS_FR_PICKUPREQUEST B,FS_FR_WHMASTER W "); 
				whereClause.append("WHERE B.DIRECTCONSOLE='N' AND B.CONSOLESTATUS='N' AND B.PRQID=W.BOOKINGID ");
				whereClause.append("AND (B.OPENBOOKINGSTATUS='N' OR B.OPENBOOKINGSTATUS IS NULL)) ");
				whereClause.append("AND PRQID NOT IN (SELECT PRQ_HAWB_DO_NO FROM FS_RS_DETAIL) ");
			}
			else if(prqAttributes.getInvokerOperation().equalsIgnoreCase("TruckHaulageCNAdd"))
			{ 
				whereClause.append(" AND PRQ.ORIGINTERMINAL ='");
				whereClause.append(terminalID);
				whereClause.append("' ");
				whereClause.append("AND PRQSTATUS='P' ");
				whereClause.append("AND HAULAGEINDICATOR='H' "); 
			}
							
			StringBuffer resultQuery= new StringBuffer();
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY PRQID DESC ");


			//Logger.info(FILE_NAME,"...............Final Query..........."+resultQuery.toString());     	
			pStmt = connection.prepareStatement(resultQuery.toString());
			rs = pStmt.executeQuery();

			//Logger.info(FILE_NAME,"...............ETPRQAdvDAO........44...........");
			while(rs.next()){
			//Logger.info(FILE_NAME,"...............ETPRQAdvDAO.....55..............");
			ETPRQAdvVO prqAttributesArr =new ETPRQAdvVO();
			prqAttributesArr.setPrqId(rs.getString(1));
			prqAttributesArr.setShipperId(rs.getString(2));
			prqAttributesArr.setShipperName(rs.getString(3));
			prqAttributesArr.setConsigneeId(rs.getString(4));
			prqAttributesArr.setConsigneeName(rs.getString(5));
			prqAttributesArr.setOriginLocation(rs.getString(6));
			prqAttributesArr.setDestinationLocation(rs.getString(7));
			prqAttributesArr.setActualWeight(rs.getString(8)); 
			prqAttributesArr.setNoOfPacks(rs.getString(9)); 
			prqAttributesArr.setTotalVolume(rs.getString(10)); 
			details.add(prqAttributesArr);
			}
			return details;
			}//end of try

			catch(Exception e){
			e.printStackTrace();
			throw new EJBException(e.getMessage());
			}
			finally
			{
			try
			{
			if(rs!=null){
			rs.close();
			}
			if(pStmt!=null){
			pStmt.close();
			}
			if(connection!=null){
			connection.close();
			}
			}
			catch(SQLException sq){
			throw new EJBException(sq.getMessage());
			}

			}
			}

			private Connection getConnection() throws SQLException
			{
			return dataSource.getConnection();
			}

			}

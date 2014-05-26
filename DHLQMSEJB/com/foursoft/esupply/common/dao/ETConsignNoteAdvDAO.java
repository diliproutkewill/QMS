/* 
*
* Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
* This Software is the proprietary information of FourSoft,Pvt Ltd.
* Use is subject to license terms
*
*
* esupply-V 1.x
*
		File					:	ETConsignNoteAdvDAO.java
		Sub-module name			:	Advanced LOV
		Module name				:	ETrans
		Purpose of the class	:	
		Author					:	Ravi Kumar G.
		Date					:	15-Mar-2005
		Modified By				:	
*
*/
package com.foursoft.esupply.common.dao;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETConsignNoteAdvVO;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

public class ETConsignNoteAdvDAO extends ETAdvancedLOVMasterDAO
{
	public static String FILE_NAME = "ETConsignNoteAdvDAO.java";
  private static Logger logger = null;

	public ETConsignNoteAdvDAO(){
  logger  = Logger.getLogger(ETConsignNoteAdvDAO.class);
  }

	public ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
	{
		ArrayList details = new ArrayList();
		String query=null;
		String searchString =null;
		StringBuffer resultQuery=null;

		StringBuffer selectClause=null;
		StringBuffer fromClause=null;
		StringBuffer whereClause=null;

		PreparedStatement pStmt = null;
		Connection connection = null;
		ResultSet rs=null;
		ETConsignNoteAdvVO consignNoteAttributes =null;
   		consignNoteAttributes = (ETConsignNoteAdvVO)searchValues;

		selectClause = new StringBuffer("SELECT DISTINCT HDHDR.HOUSEDOCID,")
										.append("HDHDR.SHIPPERID,")
										.append("SHIPPER.COMPANYNAME,")
										.append("HDHDR.CONSIGNEEID,")
										.append("CONSIGNEE.COMPANYNAME,")
										.append("HDHDR.ORIGINTERMINAL,")
										.append("HDHDR.DESTTERMINAL,")
										.append("HDHDR.GROSSWEIGHT,")
										.append("HDHDR.TOTPIECES,")
										.append("HDHDR.ACTUALVOLUME ");

		fromClause =new StringBuffer(" FROM FS_FR_HOUSEDOCHDR HDHDR,")
										.append("FS_FR_CUSTOMERMASTER SHIPPER,")
										.append("FS_FR_CUSTOMERMASTER CONSIGNEE");

		whereClause =new StringBuffer(" WHERE HDHDR.SHIPPERID=SHIPPER.CUSTOMERID ") 
										.append(" AND HDHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID ");

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriConNoteModify"))
		{
//			HDHDR.TERMINALID='DHLHKG' AND
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}				
			whereClause.append("AND HDHDR.MASTERSTATUS='N' ");
//			ROUND( TO_DATE('20-03-05 22:39','DD-MM-YY HH24:MI')  - HDHDR.HOUSEDOCDATE ) < 15 AND
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}

			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append("AND HDHDR.HAULAGEINDICATOR IS NULL ");	
			whereClause.append(getCommonWhereClause(searchValues));
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriConNoteView"))
		{
//			HDHDR.TERMINALID='DHLHKG' AND
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		 		
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
//			ROUND( TO_DATE('20-03-05 22:39','DD-MM-YY HH24:MI')  - HDHDR.HOUSEDOCDATE ) < 15 AND
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}
			whereClause.append("AND HDHDR.HAULAGEINDICATOR IS NULL ");		
			whereClause.append(getCommonWhereClause(searchValues));
		}

		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriConNoteViewDoc")) || (consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriConNotePrnDoc")))
		{

//			HDHDR.TERMINALID='DHLHKG' AND
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}				
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
//			ORDER BY HDHDR.HOUSEDOCDATE DESC;
//			whereClause.append("ORDER BY HDHDR.HOUSEDOCDATE DESC  ");
			whereClause.append(getCommonWhereClause(searchValues));			
		}

		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriChrgsAdd")) || (consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriChrgsModify")) || (consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriChrgsDelete")))
		{
//			HDHDR.TERMINALID='DHLHKG' AND
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
//			ROUND(TO_DATE('21/03/2005 01:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND 
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}			
			whereClause.append("AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS IS NULL OR DESTINATIONSTATUS !='F')  ");	
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");			
			whereClause.append(getCommonWhereClause(searchValues));
			whereClause.append("UNION  ");	
			whereClause.append("SELECT DISTINCT MDHDR.MASTERDOCID");	
			whereClause.append(", DMHDR.SHIPPERID");	
			whereClause.append(", SHIPPER.COMPANYNAME");	
			whereClause.append(", DMHDR.CONSIGNEEID");	
			whereClause.append(", CONSIGNEE.COMPANYNAME");	
			whereClause.append(", DMHDR.ORIGINTERMINAL");	
			whereClause.append(", DMHDR.DESTTERMINAL");	
			whereClause.append(", DMHDR.GROSSWEIGHT");	
			whereClause.append(", MDHDR.PIECES");	
			whereClause.append(", NULL ");	
			whereClause.append(" FROM");
			whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
			whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE");				
			whereClause.append(" WHERE");	
			whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
			whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
			whereClause.append(" AND MDHDR.MASTERDOCID = DMHDR.MASTERDOCID");	
//			MDHDR.TERMINALID='DHLHKG' AND 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND MDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}				
			whereClause.append(" AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')");
//			ROUND(TO_DATE('21/03/2005 01:03','DD/MM/YYYY HH24:MI') -MDHDR.MASTERDOCDATE ) < 15 AND 
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDHDR.MASTERDOCDATE ");
				whereClause.append(searchString);
			}	
			if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
			{
				whereClause.append(" AND MDHDR.MASTERDOCID ");
				whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
			}			
			whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
			whereClause.append(" AND MDHDR.SHIPMENTMODE =4");	
			whereClause.append(" AND DMHDR.HOUSEDOCID IS NULL ");	
			whereClause.append(getCommonWhereClause1(searchValues));
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriChrgsView"))
		{
//			HDHDR.TERMINALID='DHLHKG' AND
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
//			ROUND(TO_DATE('21/03/2005 01:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND 
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}			
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");			
			whereClause.append(getCommonWhereClause(searchValues));
			whereClause.append("UNION  ");	
			whereClause.append("SELECT DISTINCT MDHDR.MASTERDOCID");	
			whereClause.append(", DMHDR.SHIPPERID");	
			whereClause.append(", SHIPPER.COMPANYNAME");	
			whereClause.append(", DMHDR.CONSIGNEEID");	
			whereClause.append(", CONSIGNEE.COMPANYNAME");	
			whereClause.append(", DMHDR.ORIGINTERMINAL");	
			whereClause.append(", DMHDR.DESTTERMINAL");	
			whereClause.append(", DMHDR.GROSSWEIGHT");	
			whereClause.append(", MDHDR.PIECES");	
			whereClause.append(", NULL ");	
			whereClause.append(" FROM");	
			whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
			whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");
			whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE");							
			whereClause.append(" WHERE");	
			whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+) ");	
			whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
			whereClause.append(" AND MDHDR.MASTERDOCID = DMHDR.MASTERDOCID");	
//			MDHDR.TERMINALID='DHLHKG' AND 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND MDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}				
			whereClause.append(" AND MDHDR.MASTERTYPE='F'");
//			ROUND(TO_DATE('21/03/2005 01:03','DD/MM/YYYY HH24:MI') -MDHDR.MASTERDOCDATE ) < 15 AND 
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDHDR.MASTERDOCDATE ");
				whereClause.append(searchString);
			}			
			whereClause.append(" AND MDHDR.SHIPMENTMODE =4");	
//			MDHDR.MASTERDOCID LIKE '%' AND 
			if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
			{
				whereClause.append(" AND MDHDR.MASTERDOCID ");
				whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
			}
			whereClause.append(" AND DMHDR.HOUSEDOCID IS NULL ");	
			whereClause.append(getCommonWhereClause1(searchValues));
		}
		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriChrgsSuplChrgs"))
		{
//			ORIGINTERMINAL ='DHLHKG' 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}					
			whereClause.append("AND (HDHDR.ORIGINSTATUS ='F' OR HDHDR.DESTINATIONSTATUS='F') ");	
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(getCommonWhereClause(searchValues));			
			whereClause.append("UNION  ");	
			whereClause.append("SELECT DISTINCT MDHDR.MASTERDOCID");	
			whereClause.append(", DMHDR.SHIPPERID");	
			whereClause.append(", SHIPPER.COMPANYNAME");	
			whereClause.append(", DMHDR.CONSIGNEEID");	
			whereClause.append(", CONSIGNEE.COMPANYNAME");	
			whereClause.append(", DMHDR.ORIGINTERMINAL");	
			whereClause.append(", DMHDR.DESTTERMINAL");	
			whereClause.append(", DMHDR.GROSSWEIGHT");	
			whereClause.append(", MDHDR.PIECES");	
			whereClause.append(", NULL ");	
			whereClause.append(" FROM");
			whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
			whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE");				
			whereClause.append(" WHERE");	
			whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
			whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
			whereClause.append(" AND MDHDR.MASTERDOCID = DMHDR.MASTERDOCID");	
//			MDHDR.TERMINALID='DHLHKG'
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND MDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}				
			whereClause.append(" AND (DMHDR.ORIGINSTATUS = 'F' OR DMHDR.DESTINATIONSTATUS='F')");	
			whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
			whereClause.append(" AND MDHDR.SHIPMENTMODE =4");	
			whereClause.append(" AND DMHDR.HOUSEDOCID IS NULL ");
			if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
			{
				whereClause.append(" AND MDHDR.MASTERDOCID ");
				whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
			}			
			whereClause.append(getCommonWhereClause1(searchValues));
		}
		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriInvGenGenerate")) && (consignNoteAttributes.getInvoiceGenerationType().equalsIgnoreCase("G")))
		{
			if(consignNoteAttributes.getTruckLoad().equalsIgnoreCase("LTL"))
			{
				fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
				whereClause.append("AND HDHDR.HOUSEDOCID=HDCHRGS.HOUSEDOCID ");	
				whereClause.append("AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial') ");	
				whereClause.append("AND HDHDR.ORIGINSTATUS IS NULL ");		
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append("AND HDHDR.ORIGINTERMINAL ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}		
				whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
				whereClause.append("AND HDHDR.TRUCKLOAD ='LTL' ");	
				whereClause.append("AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");	
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause(searchValues));			
			}
			if(consignNoteAttributes.getTruckLoad().equalsIgnoreCase("FTL"))
			{
				fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
				whereClause.append("AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
				whereClause.append("AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial') ");	
				whereClause.append("AND HDHDR.ORIGINSTATUS IS NULL ");	
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}	
				whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
				whereClause.append("AND HDHDR.TRUCKLOAD ='FTL' ");	
				whereClause.append("AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");	
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause(searchValues));			
				whereClause.append(" UNION ");	
				whereClause.append(" SELECT DISTINCT DMHDR.MASTERDOCID");	
				whereClause.append(", DMHDR.SHIPPERID");	
				whereClause.append(", SHIPPER.COMPANYNAME");	
				whereClause.append(", DMHDR.CONSIGNEEID");	
				whereClause.append(", CONSIGNEE.COMPANYNAME");	
				whereClause.append(", DMHDR.ORIGINTERMINAL");	
				whereClause.append(", DMHDR.DESTTERMINAL");	
				whereClause.append(", DMHDR.GROSSWEIGHT");	
				whereClause.append(", MDHDR.PIECES");	
				whereClause.append(", NULL ");	
				whereClause.append(" FROM");	
				whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
				whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");
				whereClause.append(" FS_FR_HOUSEDOCCHARGES HDCHRGS, ");				
				whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
				whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");	
				whereClause.append(" WHERE");	
				whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
				whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
				whereClause.append(" AND DMHDR.MASTERDOCID = MDHDR.MASTERDOCID");
				whereClause.append(" AND MDHDR.MASTERDOCID = HDCHRGS.HOUSEDOCID");
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append(" AND  MDHDR.ORIGINGATEWAYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}				
				whereClause.append(" AND MDHDR.SHIPMENTMODE =4");
				whereClause.append(" AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial')");
				whereClause.append(" AND DMHDR.ORIGINSTATUS IS NULL");	
				whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
				whereClause.append(" AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");			
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDHDR.MASTERDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause1(searchValues));						
			}
		}

		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriInvGenGenerate")) && (consignNoteAttributes.getInvoiceGenerationType().equalsIgnoreCase("C")))
		{
			String shipperId	=	consignNoteAttributes.getShipperId().trim();
			String fromDate		=	consignNoteAttributes.getFromDate().trim();
			String toDate		=	consignNoteAttributes.getToDate().trim();
			String dateFormat	=	consignNoteAttributes.getDateFormat().trim();
			String fromTime		=	" 00:00";
			String toTime		=	" 23:59";
			fromDate			=	fromDate+fromTime;
			toDate				=	toDate+toTime;

			if(consignNoteAttributes.getTruckLoad().equalsIgnoreCase("LTL"))
			{
				fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
				whereClause.append("AND HDHDR.HOUSEDOCID=HDCHRGS.HOUSEDOCID ");	
				whereClause.append("AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial') ");	
				whereClause.append("AND HDHDR.ORIGINSTATUS IS NULL ");		
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append("AND HDHDR.ORIGINTERMINAL ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}		
				whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
				whereClause.append("AND HDHDR.HOUSEDOCDATE BETWEEN  TO_DATE('"+fromDate+"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate+"','"+dateFormat+" HH24:MI') ");
				whereClause.append("AND HDHDR.SHIPPERID='"+shipperId+"'  ");
				whereClause.append("AND HDHDR.TRUCKLOAD ='LTL'  ");				
				whereClause.append("AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");	
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause(searchValues));			
			}
			if(consignNoteAttributes.getTruckLoad().equalsIgnoreCase("FTL"))
			{
				fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
				whereClause.append("AND HDHDR.HOUSEDOCID=HDCHRGS.HOUSEDOCID ");	
				whereClause.append("AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial') ");	
				whereClause.append("AND HDHDR.ORIGINSTATUS IS NULL ");		
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append("AND HDHDR.ORIGINTERMINAL ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}		
				whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
				whereClause.append("AND HDHDR.HOUSEDOCDATE BETWEEN  TO_DATE('"+fromDate+"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate+"','"+dateFormat+" HH24:MI') ");
				whereClause.append("AND HDHDR.SHIPPERID='"+shipperId+"' ");	
				whereClause.append(" AND HDHDR.TRUCKLOAD ='FTL' ");						
				whereClause.append("AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");	
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause(searchValues));			
				whereClause.append(" UNION  ");	
				whereClause.append(" SELECT DISTINCT DMHDR.MASTERDOCID");	
				whereClause.append(", DMHDR.SHIPPERID");	
				whereClause.append(", SHIPPER.COMPANYNAME");	
				whereClause.append(", DMHDR.CONSIGNEEID");	
				whereClause.append(", CONSIGNEE.COMPANYNAME");	
				whereClause.append(", DMHDR.ORIGINTERMINAL");	
				whereClause.append(", DMHDR.DESTTERMINAL");	
				whereClause.append(", DMHDR.GROSSWEIGHT");	
				whereClause.append(", MDHDR.PIECES");	
				whereClause.append(", NULL ");	
				whereClause.append(" FROM");	
				whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
				whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");
				whereClause.append(" FS_FR_HOUSEDOCCHARGES HDCHRGS, ");				
				whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
				whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");	
				whereClause.append(" WHERE");	
				whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
				whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
				whereClause.append(" AND DMHDR.MASTERDOCID = MDHDR.MASTERDOCID");
				whereClause.append(" AND MDHDR.MASTERDOCID = HDCHRGS.HOUSEDOCID");
				if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
				{ 
					whereClause.append(" AND  MDHDR.ORIGINGATEWAYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
				}				
				whereClause.append(" AND MDHDR.SHIPMENTMODE =4");
				whereClause.append(" AND MDHDR.MASTERDOCDATE BETWEEN  TO_DATE('"+fromDate+"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate+"','"+dateFormat+" HH24:MI') ");
				whereClause.append(" AND DMHDR.SHIPPERID='"+shipperId+"' ");	
				whereClause.append(" AND HDCHRGS.PAYAT IN ('Prepaid','Origin','Partial')");
				whereClause.append(" AND DMHDR.ORIGINSTATUS IS NULL");	
				whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
				whereClause.append(" AND (HDCHRGS.INV_FLAG !='B' OR HDCHRGS.INV_FLAG IS NULL) ");			
				if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
				{
					searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDHDR.MASTERDOCDATE ");
					whereClause.append(searchString);
				}			
				whereClause.append(getCommonWhereClause1(searchValues));						
			}
		}

		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriInvGenSuplInv")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("LTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL ='DHLHKG'
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='LTL' ");	
			whereClause.append(" AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append(" AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDCHRGS.PAYAT = 'Origin' ");
			whereClause.append(" AND HDCHRGS.NRML_SUPL_FLAG='S' ");				
			whereClause.append(" AND (HDCHRGS.INV_FLAG IS NULL OR HDCHRGS.INV_FLAG='O' OR HDCHRGS.INV_FLAG='D') ");	
			whereClause.append(" AND HDCHRGS.CHARGEID NOT IN ( '1','2','3') ");				
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriInvGenSuplInv")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("FTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL ='DHLHKG'
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append("AND HDHDR.TRUCKLOAD ='FTL' ");	
			whereClause.append("AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append("AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append("AND (HDCHRGS.INV_FLAG IS NULL OR HDCHRGS.INV_FLAG='O' OR HDCHRGS.INV_FLAG='D') ");	
			whereClause.append("AND HDCHRGS.PAYAT = 'Origin' ");	
			whereClause.append("AND HDCHRGS.NRML_SUPL_FLAG='S' ");	
			whereClause.append("AND HDCHRGS.CHARGEID NOT IN ( '1','2','3') ");	
			whereClause.append(getCommonWhereClause(searchValues));						
			whereClause.append("UNION  ");	
			whereClause.append("SELECT DISTINCT MDHDR.MASTERDOCID");	
			whereClause.append(", DMHDR.SHIPPERID");	
			whereClause.append(", SHIPPER.COMPANYNAME");	
			whereClause.append(", DMHDR.CONSIGNEEID");	
			whereClause.append(", CONSIGNEE.COMPANYNAME");	
			whereClause.append(", DMHDR.ORIGINTERMINAL");	
			whereClause.append(", DMHDR.DESTTERMINAL");	
			whereClause.append(", DMHDR.GROSSWEIGHT");	
			whereClause.append(", MDHDR.PIECES");	
			whereClause.append(", NULL ");	
			whereClause.append(" FROM");	
			whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
			whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE");	
			whereClause.append(" WHERE");	
			whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
			whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
			whereClause.append(" AND MDHDR.MASTERDOCID = DMHDR.MASTERDOCID");	
			whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
			whereClause.append(" AND MDHDR.SHIPMENTMODE =4");	
//			ROUND(TO_DATE('21-03-05 02:47','DD-MM-YY HH24:MI') - MDHDR.MASTERDOCDATE ) < 15 AND
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDHDR.MASTERDOCDATE ");
				whereClause.append(searchString);
			}			
//			MDHDR.MASTERDOCID LIKE '%' AND 
			if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
			{
				whereClause.append(" AND MDHDR.MASTERDOCID ");
				whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
			}
			whereClause.append(" AND DMHDR.ORIGINSTATUS IS NULL");	
			whereClause.append(" AND DMHDR.HOUSEDOCID IS NULL ");				
			whereClause.append(getCommonWhereClause1(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriInvGenJCAdd"))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL ='DHLHKG'  AND 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append("AND HDCHRGS.COSTINCURREDAT='Origin' ");	
			whereClause.append("AND HDCHRGS.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') ");	
			whereClause.append("AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(getCommonWhereClause(searchValues));						
			whereClause.append("UNION  ");	
			whereClause.append("SELECT DISTINCT MDHDR.MASTERDOCID");	
			whereClause.append(", DMHDR.SHIPPERID");	
			whereClause.append(", SHIPPER.COMPANYNAME");	
			whereClause.append(", DMHDR.CONSIGNEEID");	
			whereClause.append(", CONSIGNEE.COMPANYNAME");	
			whereClause.append(", DMHDR.ORIGINTERMINAL");	
			whereClause.append(", DMHDR.DESTTERMINAL");	
			whereClause.append(", DMHDR.GROSSWEIGHT");	
			whereClause.append(", MDHDR.PIECES");	
			whereClause.append(", NULL ");	
			whereClause.append(" FROM");	
			whereClause.append(" FS_FR_MASTERDOCHDR MDHDR,");	
			whereClause.append(" FS_FR_DIRECTMASTERHDR DMHDR,");			
			whereClause.append(" FS_FR_HOUSEDOCCHARGES HDCHRGS,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER SHIPPER,");	
			whereClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE");	
			whereClause.append(" WHERE");	
			whereClause.append(" DMHDR.SHIPPERID=SHIPPER.CUSTOMERID (+)");	
			whereClause.append(" AND DMHDR.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");	
			whereClause.append(" AND MDHDR.MASTERDOCID = DMHDR.MASTERDOCID");	
			whereClause.append(" AND MDHDR.MASTERTYPE='F'");	
			whereClause.append(" AND MDHDR.MASTERDOCID = HDCHRGS.HOUSEDOCID");	
//			MDHDR.MASTERDOCID LIKE '%' AND
			if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
			{
				whereClause.append(" AND MDHDR.MASTERDOCID ");
				whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
			}
//			MDHDR.TERMINALID='DHLHKG' AND 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND MDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append(" AND DMHDR.ORIGINSTATUS IS NULL");	
			whereClause.append(" AND DMHDR.HOUSEDOCID IS NULL ");				
			whereClause.append(getCommonWhereClause1(searchValues));						
		}
		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriITDTInvGen")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("LTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='LTL' ");	
			whereClause.append(" AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append(" AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDCHRGS.PAYAT = 'Destination' ");	
			whereClause.append(" AND HDCHRGS.COSTINCURREDAT ='Origin' ");	
			whereClause.append(" AND (HDCHRGS.NRML_SUPL_FLAG IS NULL OR  HDCHRGS.NRML_SUPL_FLAG='N' ) ");				
			whereClause.append(" AND HDHDR.ORGINDESTTERINV  IS NULL ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}
		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriITDTInvGen")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("FTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL 
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='FTL' ");	
			whereClause.append(" AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append(" AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDCHRGS.PAYAT = 'Destination' ");	
			whereClause.append(" AND HDCHRGS.COSTINCURREDAT ='Origin' ");	
			whereClause.append(" AND (HDCHRGS.NRML_SUPL_FLAG IS NULL OR  HDCHRGS.NRML_SUPL_FLAG='N' ) ");				
			whereClause.append(" AND HDHDR.ORGINDESTTERINV  IS NULL ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}
		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriITDTSuplChrgs")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("LTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
//			HDHDR.ORIGINTERMINAL
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='LTL' ");	
			whereClause.append(" AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append(" AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDCHRGS.PAYAT = 'Destination' ");	
			whereClause.append(" AND HDCHRGS.COSTINCURREDAT ='Origin' ");	
			whereClause.append(" AND HDCHRGS.NRML_SUPL_FLAG='S' ");	
			whereClause.append(" AND (HDCHRGS.INV_FLAG IS NULL OR HDCHRGS.INV_FLAG='O' OR HDCHRGS.INV_FLAG='D') ");				
			whereClause.append(" AND HDCHRGS.CHARGEID NOT IN ( '1','2','3') ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}
		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriITDTSuplChrgs")) && (consignNoteAttributes.getTruckLoad().equalsIgnoreCase("FTL")))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='FTL' ");	
			whereClause.append(" AND HDHDR.SHIPMENTTYPE IN ('Prepaid Cash','Prepaid Credit','Collect Cash','Collect Credit') ");	
			whereClause.append(" AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDCHRGS.PAYAT = 'Destination' ");	
			whereClause.append(" AND HDCHRGS.COSTINCURREDAT ='Origin' ");	
			whereClause.append(" AND HDCHRGS.NRML_SUPL_FLAG='S' ");	
			whereClause.append(" AND (HDCHRGS.INV_FLAG IS NULL OR HDCHRGS.INV_FLAG='O' OR HDCHRGS.INV_FLAG='D') ");				
			whereClause.append(" AND HDCHRGS.CHARGEID NOT IN ( '1','2','3') ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriNonSysATCL"))
		{
			fromClause.append(", FS_FR_TERMINALMASTER TM ");
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.HOUSEDOCID NOT IN( SELECT HOUSEDOCNO FROM FS_FR_NONSYSTEMSHIPMENTS) ");	
			whereClause.append(" AND HDHDR.DESTTERMINAL=TM.TERMINALID ");	
			whereClause.append(" AND TM.TERMINALTYPE='N' ");	
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriNonSysPOD"))
		{
			fromClause.append(", FS_FR_TERMINALMASTER TM ");
			fromClause.append(", FS_FR_NONSYSTEMSHIPMENTS NSS ");

			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND NSS.HOUSEDOCNO =HDHDR.HOUSEDOCID ");	
			whereClause.append(" AND HDHDR.DESTTERMINAL=TM.TERMINALID ");	
			whereClause.append(" AND TM.TERMINALTYPE='N' ");	
			whereClause.append(" AND NSS.DONO IS NULL ");	
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriCNAAdd"))
		{
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.HOUSEDOCID NOT IN(SELECT DOCID FROM FS_FR_HOUSEACTIVITYDTL) ");	
			whereClause.append(" AND HDHDR.RECEIPTSTATUS IS NULL ");
			whereClause.append(getCommonWhereClause(searchValues));						
		}


		if((consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriCNAModify")) || (consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriCNAView")) || (consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkOriCNADelete")))
		{
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.HOUSEDOCID IN(SELECT DOCID FROM FS_FR_HOUSEACTIVITYDTL) ");	
			whereClause.append(" AND HDHDR.RECEIPTSTATUS IS NULL ");
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkHaulageConNoteModify"))
		{
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.MASTERSTATUS='N' ");	
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}			
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.HAULAGEINDICATOR ='H' ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkHaulageConNoteView"))
		{
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}			
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.HAULAGEINDICATOR ='H' ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkDestInvConCAN"))
		{
			fromClause.append(", FS_RT_PLAN RP ");
			fromClause.append(", FS_RT_LEG LEG ");
			whereClause.append(" AND HDHDR.HOUSEDOCID = RP.HAWB_ID ");
			whereClause.append(" AND LEG.RT_PLAN_ID  = RP.RT_PLAN_ID ");

			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND LEG.DEST_TRML_ID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND LEG.SHPMNT_STATUS IN('C','P') ");	
			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkConFTLAdd"))
		{
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append(" AND HDHDR.TERMINALID ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}	
			whereClause.append(" AND HDHDR.MASTERSTATUS='N' ");	
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
			{
				searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"HDHDR.HOUSEDOCDATE ");
				whereClause.append(searchString);
			}			

			whereClause.append(" AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append(" AND HDHDR.TRUCKLOAD ='FTL'  ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		if(consignNoteAttributes.getInvokerOperation().equalsIgnoreCase("TrkDestInvConJCS"))
		{
			fromClause.append(", FS_FR_HOUSEDOCCHARGES HDCHRGS ");
			if(consignNoteAttributes.getTerminalID()!=null && !consignNoteAttributes.getTerminalID().equals(""))
			{ 
				whereClause.append("AND HDHDR.ORIGINTERMINAL ");
				whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getTerminalID().trim()));
			}		
			whereClause.append("AND HDHDR.SHIPMENTMODE =4  ");	
			whereClause.append("AND HDCHRGS.COSTINCURREDAT='Destination' ");	
			whereClause.append("AND HDCHRGS.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') ");	
			whereClause.append("AND HDCHRGS.HOUSEDOCID=HDHDR.HOUSEDOCID ");	
			whereClause.append(getCommonWhereClause(searchValues));						
		}

		resultQuery= new StringBuffer();
		resultQuery.append(selectClause);
		resultQuery.append(fromClause);
		resultQuery.append(whereClause);
		resultQuery.append(" ORDER BY 1 DESC ");
		
		try 
		{     	
			connection = this.getConnection();
			pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

			while(rs.next())
			{
				ETConsignNoteAdvVO consignNoteAttributesArr =new ETConsignNoteAdvVO();
				consignNoteAttributesArr.setConsignNoteId(rs.getString(1));
				consignNoteAttributesArr.setShipperId(rs.getString(2));
				consignNoteAttributesArr.setShipperName(rs.getString(3));
				consignNoteAttributesArr.setConsigneeId(rs.getString(4));
				consignNoteAttributesArr.setConsigneeName(rs.getString(5));
				consignNoteAttributesArr.setOriginTerminal(rs.getString(6)); 
				consignNoteAttributesArr.setDestinationTerminal(rs.getString(7)); 
				consignNoteAttributesArr.setActualWeight(rs.getString(8)); 
				consignNoteAttributesArr.setNoOfPacks(rs.getString(9)); 
				consignNoteAttributesArr.setTotalVolume(rs.getString(10)); 
				details.add(consignNoteAttributesArr);
			}		
			return details;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}
		finally
		{
			try
			{
				if(rs!=null)
				{
					rs.close();
				}
				if(pStmt!=null)
				{
					pStmt.close();
				}
				if(connection!=null)
				{
					connection.close();
				}
			}
			catch(SQLException sq)
			{
				throw new EJBException(sq.getMessage());
			}
		}
	}
	private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
	public String getCommonWhereClause(ETAdvancedLOVMasterVO searchValues)
	{
		ETConsignNoteAdvVO consignNoteAttributes = (ETConsignNoteAdvVO)searchValues;
		StringBuffer whereClause=null;
		String searchString =null;
		whereClause =new StringBuffer("");

		if(consignNoteAttributes.getConsignNoteId()!=null && !consignNoteAttributes.getConsignNoteId().equals(""))
		{
			whereClause.append(" AND HDHDR.HOUSEDOCID ");
			whereClause.append(getSearchString(searchValues.getSearchType(), consignNoteAttributes.getConsignNoteId().trim()));
		}

		if(consignNoteAttributes.getShipperName()!=null && !consignNoteAttributes.getShipperName().equals(""))
		{ 
			whereClause.append("AND SHIPPER.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getShipperName().trim()));
		}

		if(consignNoteAttributes.getConsigneeName()!=null && !consignNoteAttributes.getConsigneeName().equals(""))
		{ 
			whereClause.append("AND CONSIGNEE.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getConsigneeName().trim()));
		}

		if(consignNoteAttributes.getOriginTerminal()!=null && !consignNoteAttributes.getOriginTerminal().equals(""))
		{ 
			whereClause.append("AND HDHDR.ORIGINTERMINAL ");
			whereClause.append(searchString =getSearchString(searchValues.getSearchType(),consignNoteAttributes.getOriginTerminal().trim()));
		}

		if(consignNoteAttributes.getDestinationTerminal()!=null && !consignNoteAttributes.getDestinationTerminal().equals(""))
		{ 
			whereClause.append("AND HDHDR.DESTTERMINAL "); 	
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getDestinationTerminal().trim()));
		}

		return whereClause.toString();
	}

	public String getCommonWhereClause1(ETAdvancedLOVMasterVO searchValues)
	{
		ETConsignNoteAdvVO consignNoteAttributes = (ETConsignNoteAdvVO)searchValues;
		StringBuffer whereClause=null;
		String searchString =null;
		whereClause =new StringBuffer("");

		if(consignNoteAttributes.getShipperName()!=null && !consignNoteAttributes.getShipperName().equals(""))
		{ 
			whereClause.append("AND SHIPPER.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getShipperName().trim()));
		}

		if(consignNoteAttributes.getConsigneeName()!=null && !consignNoteAttributes.getConsigneeName().equals(""))
		{ 
			whereClause.append("AND CONSIGNEE.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getConsigneeName().trim()));
		}

		if(consignNoteAttributes.getOriginTerminal()!=null && !consignNoteAttributes.getOriginTerminal().equals(""))
		{ 
			whereClause.append("AND DMHDR.ORIGINTERMINAL ");
			whereClause.append(searchString =getSearchString(searchValues.getSearchType(),consignNoteAttributes.getOriginTerminal().trim()));
		}

		if(consignNoteAttributes.getDestinationTerminal()!=null && !consignNoteAttributes.getDestinationTerminal().equals(""))
		{ 
			whereClause.append("AND DMHDR.DESTTERMINAL "); 	
			whereClause.append(getSearchString(searchValues.getSearchType(),consignNoteAttributes.getDestinationTerminal().trim()));
		}

		return whereClause.toString();
	}

}
/*<%--
 %
 % Copyright (c) 1999-2005 by FourSoft,Pvt Ltd.Reserved.
 % This software is the proprietary information of FourSoft Ltd.
 % Use is subject to license terms.
 % 
 % esupply - v 1.8
 %
--%>
test 
<%--
% File		      :   ETHAWBAdvDAO.java
% Sub-Module    :   HAWB - Advanced LOV Search. 
% Module        :   ETrans
%
% This is the DAO  for the LOV of the HAWB Ids based on Different Parameters
% 
% Author        :   G.Srinivas 
% Date 			    :   14/03/2005
% Modified date :   14/03/2005
--%>*/


package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETHAWBAdvVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import org.apache.log4j.Logger;

/**
 * 
 */
public class ETHAWBAdvDAO extends ETAdvancedLOVMasterDAO
{
  public static String FILE_NAME = "ETHAWBAdvDAO.java";
  private static Logger logger = null;

  public ETHAWBAdvDAO()
  {
  logger  = Logger.getLogger(ETHAWBAdvDAO.class);
  }
  
  /**
   * 
   * @param searchValues
   * @return 
   */
  public  ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
	{  
	   ArrayList details =new ArrayList();
	   
	   String query=null;
	   PreparedStatement pStmt = null;
       Connection connection = null;
       ResultSet rs=null;
       ETHAWBAdvVO hawbAttributes =null;
	   String searchString =null;
       

	   
     try {
     
			connection = this.getConnection();
			StringBuffer selectClause = new StringBuffer(" ");
			StringBuffer fromClause   = new StringBuffer(" ");
			StringBuffer whereClause  = new StringBuffer(" ");
			StringBuffer resultQuery= new StringBuffer("");
			hawbAttributes = (ETHAWBAdvVO)searchValues;

 // Logger.info(FILE_NAME,"...ETHAWBAdvDAO..invokerOperation.@@@@@@.."+hawbAttributes.getInvokerOperation());
 // Logger.info(FILE_NAME,"...ETHAWBAdvDAO...Operation.... @@@@@@.."+hawbAttributes.getOperation());
 // Logger.info(FILE_NAME,"...ETHAWBAdvDAO...mode.... .....@@@@@@.."+hawbAttributes.getMode());
 // Logger.info(FILE_NAME,"...ETHAWBAdvDAO...TerminalType....@@@@@@.."+hawbAttributes.getTerminalType());
 // Logger.info(FILE_NAME,"...ETHAWBAdvDAO...operationType...@@@@@@.."+hawbAttributes.getOperationType());
  
		//	if((hawbAttributes.getInvokerOperation().equals("")||hawbAttributes.getInvokerOperation().equals("null"))&& hawbAttributes.getOperation().equals("Modify") && !hawbAttributes.getTerminalType().equals("NSIB")&& !hawbAttributes.getOperationType().equals("Destination"))
		 if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBModify"))
		 {	

//Logger.info(FILE_NAME,".....HAWB .SYS..MODIFY...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+".....HAWB .SYS..MODIFY...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 

//OLD QUERY
/*SELECT	HOUSEDOCID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL   FROM  FS_FR_HOUSEDOCHDR,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE  NOT EXISTS ( SELECT INV.HOUSEDOCID FROM  FS_FR_FRTINVOICEMASTER INV WHERE TERMINALID='DHLHKG' AND FS_FR_HOUSEDOCHDR.HOUSEDOCID = INV.HOUSEDOCID) AND ORIGINTERMINAL ='DHLHKG'  AND MASTERSTATUS='N'  AND ROUND(TO_DATE('15/03/2005 15:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND SHIPMENTMODE =1  AND CUSTOMERCONSOLIDATION IS NULL  AND NOT EXISTS ( SELECT DISTINCT HAWB_ID FROM FS_RT_PLAN RP, FS_RT_LEG RL WHERE RP.HAWB_ID = HOUSEDOCID AND RL.RT_PLAN_ID = RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') AND RL.MSTER_DOC_ID IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID*/
//ADVANCED QUERY
/*SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,
  CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME     
  FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE 
  WHERE  NOT EXISTS ( SELECT INV.HOUSEDOCID FROM  FS_FR_FRTINVOICEMASTER INV 
  WHERE TERMINALID='DHLHKG' AND HD.HOUSEDOCID = INV.HOUSEDOCID) AND ORIGINTERMINAL ='DHLHKG'  AND MASTERSTATUS='N'  
  AND ROUND(TO_DATE('15/03/2005 14:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND SHIPMENTMODE =1  
  AND CUSTOMERCONSOLIDATION IS NULL  AND NOT EXISTS ( SELECT DISTINCT HAWB_ID FROM FS_RT_PLAN RP, 
  FS_RT_LEG RL WHERE RP.HAWB_ID = HOUSEDOCID AND RL.RT_PLAN_ID = RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') 
  AND RL.MSTER_DOC_ID IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID*/

	selectClause.append(" SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  NOT EXISTS ( SELECT INV.HOUSEDOCID FROM   FS_FR_FRTINVOICEMASTER INV   WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"' AND  HD.HOUSEDOCID = INV.HOUSEDOCID) AND ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"'  AND  MASTERSTATUS='N' AND HD.SHIPMENTMODE =1 AND CUSTOMERCONSOLIDATION IS NULL AND NOT EXISTS ( SELECT DISTINCT HAWB_ID FROM FS_RT_PLAN RP, FS_RT_LEG RL WHERE RP.HAWB_ID = HOUSEDOCID AND RL.RT_PLAN_ID = RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') AND RL.MSTER_DOC_ID  IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND  CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ");

            
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
           
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
		
				 } //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
           
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........11..........."+resultQuery.toString());
      
	 }
	// else if ((hawbAttributes.getInvokerOperation().equals("")||hawbAttributes.getInvokerOperation().equals("null"))&& hawbAttributes.getOperation().equals("View") && !hawbAttributes.getTerminalType().equals("NSIB")&& !hawbAttributes.getOperationType().equals("Destination"))
	   else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBView"))
		 {
//Logger.info(FILE_NAME,".....HAWB .SYS..VIEW...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+".....HAWB .SYS..VIEW...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME  FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  WHERE		 ORIGINTERMINAL='DHLHKG' AND HD.SHIPMENTMODE =1 AND ROUND(TO_DATE('16/03/2005 20:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ORDER BY HOUSEDOCDATE DESC;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,
CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES,HD.ACTUALVOLUME  
FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  
WHERE		 ORIGINTERMINAL='DHLHKG' AND HD.SHIPMENTMODE =1 AND ROUND(TO_DATE('16/03/2005 20:03','DD/MM/YYYY HH24:MI')
-HOUSEDOCDATE ) < 15 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID 
ORDER BY HOUSEDOCDATE DESC; */

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
									.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
									.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
									.append(" HD.GROSSWEIGHT , HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE		 ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND ")
									.append(" CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ");

            
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
           // StringBuffer resultQuery= new StringBuffer();
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........11..........."+resultQuery.toString());
			//pStmt = connection.prepareStatement(resultQuery.toString());
	 }
//else if ((hawbAttributes.getInvokerOperation().equals("")||hawbAttributes.getInvokerOperation().equals("null"))&& hawbAttributes.getOperation().equals("Delete") && !hawbAttributes.getTerminalType().equals("NSIB")&& !hawbAttributes.getOperationType().equals("Destination"))
	else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDelete"))
	 {
//Logger.info(FILE_NAME,".....HAWB...SYS..DELETE...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+".....HAWB...SYS..DELETE...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT	H.HOUSEDOCID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,H.ORIGINTERMINAL,H.DESTTERMINAL  FROM FS_FR_HOUSEDOCHDR H,  FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE HOUSEDOCID LIKE 'AIRHKGBHX1001150%' AND NOT EXISTS ( SELECT INV.HOUSEDOCID FROM  FS_FR_FRTINVOICEMASTER INV WHERE TERMINALID='DHLHKG' AND H.HOUSEDOCID = INV.HOUSEDOCID)AND H.ORIGINTERMINAL ='DHLHKG' AND H.SHIPMENTMODE =1  AND H.CUSTOMERCONSOLIDATION IS NULL AND ROUND(TO_DATE('16/03/2005 21:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15 AND NOT EXISTS ( SELECT DISTINCT HAWB_ID FROM FS_RT_PLAN RP, FS_RT_LEG RL WHERE RP.HAWB_ID = H.HOUSEDOCID AND RL.RT_PLAN_ID = RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') AND RL.MSTER_DOC_ID IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = H.SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = H.CONSIGNEEID ORDER BY H.HOUSEDOCID ;*/
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,
CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME  
FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE 
WHERE  NOT EXISTS 
( SELECT INV.HOUSEDOCID FROM  FS_FR_FRTINVOICEMASTER INV WHERE TERMINALID='DHLHKG' AND HD.HOUSEDOCID = 
INV.HOUSEDOCID)AND HD.ORIGINTERMINAL ='DHLHKG' AND HD.SHIPMENTMODE =1  AND HD.CUSTOMERCONSOLIDATION IS NULL 
AND ROUND(TO_DATE('16/03/2005 21:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15 AND NOT EXISTS 
( SELECT DISTINCT HAWB_ID FROM FS_RT_PLAN RP, FS_RT_LEG RL WHERE RP.HAWB_ID = HD.HOUSEDOCID AND RL.RT_PLAN_ID = 
RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') AND RL.MSTER_DOC_ID IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = 
HD.SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = HD.CONSIGNEEID ORDER BY HD.HOUSEDOCID  ;
 */

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
										.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
										.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  NOT EXISTS  ( SELECT INV.HOUSEDOCID FROM ")
									.append(" FS_FR_FRTINVOICEMASTER INV WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"' AND ")
									.append(" HD.HOUSEDOCID = INV.HOUSEDOCID)AND HD.ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND HD.CUSTOMERCONSOLIDATION IS NULL ")
									.append(" AND NOT EXISTS ( SELECT DISTINCT HAWB_ID FROM ")
									.append(" FS_RT_PLAN RP, FS_RT_LEG RL WHERE RP.HAWB_ID = HD.HOUSEDOCID AND ")
									.append(" RL.RT_PLAN_ID = RP.RT_PLAN_ID AND RL.LEG_TYPE NOT IN('LT','TL') AND ")
									.append(" RL.MSTER_DOC_ID IS NOT NULL )  AND SHIPPER.CUSTOMERID(+) = ")
									.append(" HD.SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = HD.CONSIGNEEID ");

            
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }

// For  View Document & Print Document .

//else if ((hawbAttributes.getInvokerOperation().equals("")||hawbAttributes.getInvokerOperation().equals("null"))&& hawbAttributes.getOperation().equals("housereport") && !hawbAttributes.getTerminalType().equals("NSIB"))
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBViewDocument")||hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBPrintDocument"))
	 {
//Logger.info(FILE_NAME,"..HAWB .SYS..View & Print  Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 
logger.info(FILE_NAME+"..HAWB .SYS..View & Print  Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '%' AND ORIGINTERMINAL='DHLHKG' AND SHIPMENTMODE=1 ORDER BY HOUSEDOCID DESC;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,
CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME  
FROM  
 FS_FR_HOUSEDOCHDR HD,   
 FS_FR_CUSTOMERMASTER SHIPPER, 
 FS_FR_CUSTOMERMASTER CONSIGNEE 
WHERE 
 HD.SHIPPERID = SHIPPER.CUSTOMERID 
 AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID 
 AND ORIGINTERMINAL='DHLHKG' 
 AND SHIPMENTMODE=1 
ORDER BY 
 HOUSEDOCID DESC;
*/

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
										.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
										.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND HD.SHIPPERID = SHIPPER.CUSTOMERID ")
									.append(" AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ");

            
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
            //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }

//else if (hawbAttributes.getOperation().equals("LabelPrint"))
	else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBLabel"))
	 {
//Logger.info(FILE_NAME,".....HAWB...SYS..LABELPRINT...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 		 
logger.info(FILE_NAME+".....HAWB...SYS..LABELPRINT...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,RP.ORIG_TRML_ID,RP.DEST_TRML_ID  FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,  FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE HD.SHIPMENTMODE=2 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F' AND (ORIGINSTATUS = 'F' OR DESTINATIONORIGINTERINV = 'F') ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B  WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) ) AND ROUND( TO_DATE('17/03/2005 14:03', 'DD/MM/YYYY HH24:MI') - RP.CRTD_TIMESTMP) < 15  AND SHIPPER.CUSTOMERID(+) = RP.SHIPPER_ID AND CONSIGNEE.CUSTOMERID(+) = RP.CONSIGNEE_ID AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,RP.ORIG_TRML_ID,RP.DEST_TRML_ID  FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,  FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F' AND (ORIGINSTATUS = 'F' OR DESTINATIONORIGINTERINV = 'F') ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) ) AND ROUND( TO_DATE('17/03/2005 14:03', 'DD/MM/YYYY HH24:MI') - RP.CRTD_TIMESTMP) < 15  AND SHIPPER.CUSTOMERID(+) = RP.SHIPPER_ID AND CONSIGNEE.CUSTOMERID(+) = RP.CONSIGNEE_ID AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*
 Query not yet Implemented COMPLETELY

*/

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
										.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
										.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND HD.SHIPPERID = SHIPPER.CUSTOMERID ")
									.append(" AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ");

            
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }
//if(hawbAttributes.getOperation().equals("Modify")&& hawbAttributes.getTerminalType().equals("NSIB")&& !hawbAttributes.getOperationType().equals("Destination") )//|| hawbAttributes.getOperation()== "Modify"
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBModifyNSIB"))
		 {
//Logger.info(FILE_NAME,".....HAWB...NSIB..MODIFY...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 			 
logger.info(FILE_NAME+".....HAWB...NSIB..MODIFY...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 			 
//OLD QUERY
//SELECT	HOUSEDOCID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL   FROM FS_FR_HOUSEDOCHDR,  FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N') AND NOT EXISTS (SELECT INV.HOUSEDOCID FROM FS_FR_FRTINVOICEMASTER INV WHERE INV. HOUSEDOCID = FS_FR_HOUSEDOCHDR.HOUSEDOCID AND INV.TERMINALID='DHLHKG' ) AND FS_FR_HOUSEDOCHDR.TERMINALID='DHLHKG' AND MASTERSTATUS='N' AND ROUND(TO_DATE('17/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , 
HD.TOTPIECES ,HD.ACTUALVOLUME 
FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE   
WHERE ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N') AND NOT 
EXISTS (SELECT INV.HOUSEDOCID FROM FS_FR_FRTINVOICEMASTER INV WHERE INV. HOUSEDOCID = HD.HOUSEDOCID AND 
INV.TERMINALID='DHLHKG' ) AND HD.TERMINALID='DHLHKG' AND MASTERSTATUS='N' AND 
ROUND(TO_DATE('17/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15 AND SHIPMENTMODE =1 AND 
SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID */

	selectClause.append(" SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
									.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
									.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,")
									.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER ")
									.append(" WHERE TERMINALTYPE='N') AND NOT EXISTS (SELECT INV.HOUSEDOCID FROM ")
									.append(" FS_FR_FRTINVOICEMASTER INV WHERE INV. HOUSEDOCID = HD.HOUSEDOCID AND ")
									.append(" INV.TERMINALID='"+hawbAttributes.getTerminalID()+"' ) AND ")
									.append(" HD.TERMINALID='"+hawbAttributes.getTerminalID()+"'  AND ")
									.append(" MASTERSTATUS='N' AND HD.SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) = ")
									.append(" SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ");
          
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		  
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
       
				// Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
                    
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
		//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........11..........."+resultQuery.toString());
      
	 }
//else if (hawbAttributes.getOperation().equals("View") && hawbAttributes.getTerminalType().equals("NSIB"))
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBViewNSIB"))
	 {
//Logger.info(FILE_NAME,".....HAWB...NSIB..VIEW...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 			 		 
logger.info(FILE_NAME+".....HAWB...NSIB..VIEW...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 			 		 
//OLD QUERY	 
//ELECT	HOUSEDOCID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL   FROM FS_FR_HOUSEDOCHDR, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE FS_FR_HOUSEDOCHDR.TERMINALID='DHLHKG' AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N')  AND SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ORDER BY HOUSEDOCDATE DESC ;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , 
HD.TOTPIECES ,HD.ACTUALVOLUME  
FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  
WHERE HD.TERMINALID='DHLHKG' AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER 
WHERE TERMINALTYPE='N')  AND SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = 
CONSIGNEEID ORDER BY HOUSEDOCDATE DESC ;

*/

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
									.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
									.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
									.append(" HD.GROSSWEIGHT , HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE HD.TERMINALID='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER ")
									.append(" WHERE TERMINALTYPE='N')  AND HD.SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) ")
									.append(" = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID AND ORIGINTERMINAL ")
									.append(" IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N') ")
									.append(" AND HD.SHIPMENTMODE =1 AND SHIPPER.CUSTOMERID(+) = SHIPPERID AND ")
									.append(" CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID ");
         
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
          
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........11..........."+resultQuery.toString());
			
	 }
// For  View Document & Print Document  NSIB.

//else if (hawbAttributes.getOperation().equals("housereport") && hawbAttributes.getTerminalType().equals("NSIB"))
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBViewDocumentNSIB")||hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBPrintDocumentNSIB"))
	 {
//Logger.info(FILE_NAME,"..HAWB .NSIB..View & Print  Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 		 
logger.info(FILE_NAME+"..HAWB .NSIB..View & Print  Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 		 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '%' AND TERMINALID='DHLHKG' AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N')  AND SHIPMENTMODE=1 ORDER BY HOUSEDOCID DESC;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , 
HD.TOTPIECES ,HD.ACTUALVOLUME  
FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE 
WHERE HD.TERMINALID='DHLHKG' AND HD.SHIPPERID = SHIPPER.CUSTOMERID  
AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER 
WHERE TERMINALTYPE='N')  AND SHIPMENTMODE=1  ORDER BY HOUSEDOCID DESC;
;
*/

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
										.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
										.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE HD.TERMINALID='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND HD.SHIPPERID = SHIPPER.CUSTOMERID ")
									.append(" AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND ORIGINTERMINAL IN ")
									.append(" (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N') ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }
//for Dest Warehouse HAWB ADD

	//else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("Add") && hawbAttributes.getOperationType().equals("Destination"))
	else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestWHAdd"))
	 {
//Logger.info(FILE_NAME,"...WH ....Add...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+"...WH ....Add...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT DISTINCT RP.HAWB_ID FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND RP.PRMY_MODE = 1 AND  HD.HOUSEDOCID NOT IN (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE='Destination' )  AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('17-03-05 22:31', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15  AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR ((SELECT COUNT(HOUSEDOCNO) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0) ) ) AND RP.HAWB_ID LIKE '%' ;
//ADVANCED QUERY

/*SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , HD.TOTPIECES 
,HD.ACTUALVOLUME 
FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE   
WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID
AND HD.SHIPPERID = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID 
 AND RP.PRMY_MODE = 1 AND  HD.HOUSEDOCID NOT IN (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE='Destination' )  
 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND 
 ROUND( TO_DATE('17-03-05 22:31', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15  AND A.DEST_TRML_ID = 'DHLHKG'  
 AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' 
 AND B.AUTO_MNUL_FLAG='M')) AND ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER 
 WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR ((SELECT COUNT(HOUSEDOCNO) 
 FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0) ) ) AND RP.HAWB_ID LIKE '%';

*/

//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-4927 on 20050415.
/*    selectClause.append(" SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID, ")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
										.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
										.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
										.append(" HD.TOTPIECES ,HD.ACTUALVOLUME  "); 
			
	fromClause.append(" FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER ")
										.append(" SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.SHIPPERID = ")
										.append(" SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID ")
										.append(" AND HD.SHIPPERID = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = ")
										.append(" RP.CONSIGNEE_ID  AND RP.PRMY_MODE = 1 AND  HD.HOUSEDOCID NOT IN ")
										.append(" (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE")
										.append(" ='Destination' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM  ")
										.append(" FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ")
										.append(" A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'  ")
										.append(" AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B'))")
										.append(" OR (B.LEG_TYPE ='GT'  AND B.AUTO_MNUL_FLAG='M')) AND ")
										.append(" ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER ")
										.append(" WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR ")
										.append(" ((SELECT COUNT(HOUSEDOCNO)  FROM FS_FR_DELIVERYORDER WHERE ")
										.append(" HOUSEDOCNO=RP.HAWB_ID) = 0) ) ) ");  */

//New  Advanced Query.

//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6620 on 20050503.
/*selectClause.append(" SELECT HD.HAWB_ID , SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME  ");
fromClause.append("FROM( SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT ,  HD.TOTPIECES ,HD.ACTUALVOLUME,HD.ORIGINLOCATION ,HD.DESTINATIONLOCATION,HD.HOUSEDOCDATE  FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE    WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.SHIPPERID =  SHIPPER.CUSTOMERID  AND ( HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ) AND HD.SHIPPERID = RP.SHIPPER_ID  AND ( HD.CONSIGNEEID  =  RP.CONSIGNEE_ID )  AND RP.PRMY_MODE = 1 AND  HD.HOUSEDOCID NOT IN  (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE ='Destination' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM   FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND  A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT'  AND B.AUTO_MNUL_FLAG='M')) AND   ROUND( TO_DATE('15-04-05 13:02', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15   AND   ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER  WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR  ((SELECT COUNT(HOUSEDOCNO)  FROM FS_FR_DELIVERYORDER WHERE  HOUSEDOCNO=RP.HAWB_ID) = 0) ) )UNION SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, FD.ORIGINTERMINAL,FD.DESTTERMINAL,  FD.GROSSWEIGHT,0 TOTPIECES,0 ACTUALVOLUME,FD.ORIGINLOCATION ,FD.DESTLOCATION,HD.MASTERDOCDATE  FROM FS_RT_PLAN RP,FS_FR_MASTERDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ,FS_FR_DIRECTMASTERHDR FD   WHERE RP.HAWB_ID = HD.MASTERDOCID AND FD.SHIPPERID =  SHIPPER.CUSTOMERID  AND ( FD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ) AND FD.SHIPPERID = RP.SHIPPER_ID  AND ( FD.CONSIGNEEID  =  RP.CONSIGNEE_ID ) AND RP.PRMY_MODE = 1 AND  HD.MASTERDOCID NOT IN  (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE ='Destination' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM   FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND  A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT'  AND B.AUTO_MNUL_FLAG='M')) AND   ROUND( TO_DATE('15-04-05 13:02', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15  AND ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR  ((SELECT COUNT(HOUSEDOCNO)  FROM FS_FR_DELIVERYORDER WHERE  HOUSEDOCNO=RP.HAWB_ID) = 0) ) ) ) HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)");  
*/
selectClause.append(" SELECT HD.HAWB_ID HOUSEDOCID, SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME  ");
fromClause.append(" FROM( SELECT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  SUM(HD.GROSSWEIGHT) GROSSWEIGHT,  HD.TOTPIECES ,HD.ACTUALVOLUME,HD.ORIGINLOCATION ,HD.DESTINATIONLOCATION,HD.HOUSEDOCDATE  FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE    WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.SHIPPERID =  SHIPPER.CUSTOMERID  AND ( HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ) AND HD.SHIPPERID = RP.SHIPPER_ID  AND ( HD.CONSIGNEEID  =  RP.CONSIGNEE_ID )  AND RP.PRMY_MODE = 1 AND  HD.HOUSEDOCID NOT IN  (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE ='Destination' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM   FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND  A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT'  AND B.AUTO_MNUL_FLAG='M')) AND   ROUND( TO_DATE('15-04-05 13:02', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15   AND   ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER  WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR  ((SELECT COUNT(HOUSEDOCNO)  FROM FS_FR_DELIVERYORDER WHERE  HOUSEDOCNO=RP.HAWB_ID) = 0) ) ) GROUP BY RP.HAWB_ID, SHIPPER.CUSTOMERID, SHIPPER.COMPANYNAME,CONSIGNEE.CUSTOMERID,  CONSIGNEE.COMPANYNAME, HD.ORIGINTERMINAL,HD.TOTPIECES,HD.ACTUALVOLUME, HD.DESTTERMINAL, HD.ORIGINLOCATION, HD.DESTINATIONLOCATION, HD.HOUSEDOCDATE  UNION  SELECT   RP.HAWB_ID,  SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME, CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, FD.ORIGINTERMINAL,FD.DESTTERMINAL,SUM( FD.GROSSWEIGHT) GROSSWEIGHT, 0 TOTPIECES, 0 ACTUALVOLUME, FD.ORIGINLOCATION , FD.DESTLOCATION, HD.MASTERDOCDATE  FROM  FS_RT_PLAN RP, FS_FR_MASTERDOCHDR HD, FS_FR_CUSTOMERMASTER  SHIPPER,  FS_FR_CUSTOMERMASTER CONSIGNEE , FS_FR_DIRECTMASTERHDR FD  WHERE   RP.HAWB_ID = HD.MASTERDOCID  AND  FD.SHIPPERID =  SHIPPER.CUSTOMERID  AND  ( FD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  )  AND  FD.SHIPPERID = RP.SHIPPER_ID  AND  ( FD.CONSIGNEEID  =  RP.CONSIGNEE_ID )  AND  RP.PRMY_MODE = 1  AND   HD.MASTERDOCID NOT IN  (SELECT BOOKINGID FROM FS_FR_WHMASTER WHERE OPERATIONTYPE ='Destination' )  AND  RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM   FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND  A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND  ((B.LEG_TYPE IN ('TP','GT','TG')  AND  B.SHPMNT_STATUS IN ('P','B'))  OR   (B.LEG_TYPE ='GT'  AND B.AUTO_MNUL_FLAG='M'))  AND    ROUND( TO_DATE('15-04-05 13:02', 'DD-MM-YY HH24:MI') - B.RECEIVED_DATE) < 15   AND  ( ((SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR   ((SELECT COUNT(HOUSEDOCNO)  FROM FS_FR_DELIVERYORDER WHERE  HOUSEDOCNO=RP.HAWB_ID) = 0) ) )  GROUP BY RP.HAWB_ID, SHIPPER.CUSTOMERID, SHIPPER.COMPANYNAME, CONSIGNEE.CUSTOMERID,CONSIGNEE.COMPANYNAME, FD.ORIGINTERMINAL, FD.DESTTERMINAL, FD.ORIGINLOCATION, FD.DESTLOCATION, HD.MASTERDOCDATE  ) HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");  
whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HAWB_ID  ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE  ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HAWB_ID DESC ");
			
	 }
//for HAWBDestWHModify

// else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("Modify") && hawbAttributes.getOperationType().equals("Destination"))
	else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestWHModify"))
	 {
//Logger.info(FILE_NAME,".....WH ...Modify....conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+".....WH ...Modify....conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT 	DISTINCT RP.HAWB_ID, WH.WAREHOUSEID FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD, FS_FR_WHMASTER WH WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( ( (SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR ( (SELECT COUNT(HOUSEDOCNO) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0 ) ) AND ( (SELECT SUM(BOXESRECEIVED) FROM FS_FR_WHMASTER WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) ) AND ROUND( TO_DATE('18-03-05 12:51', 'DD-MM-YY HH24:MI') - HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*SELECT 	DISTINCT RP.HAWB_ID, WH.WAREHOUSEID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,
CONSIGNEE.CUSTOMERID CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL,  
HD.GROSSWEIGHT , HD.TOTPIECES,HD.ACTUALVOLUME
FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD, FS_FR_WHMASTER WH,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE   
WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND 
HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID   
AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B 
WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND 
B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( ( (SELECT SUM(BOXESRELEASEDCURR) 
FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) < B.PIECES_RECEIVED ) OR ( (SELECT COUNT(HOUSEDOCNO) FROM 
FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0 ) ) AND ( (SELECT SUM(BOXESRECEIVED) FROM FS_FR_WHMASTER 
WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) ) AND ROUND( TO_DATE('18-03-05 12:32', 'DD-MM-YY HH24:MI') - 
HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;*/

    selectClause.append(" SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID, ")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
										.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
										.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
										.append(" HD.TOTPIECES ,HD.ACTUALVOLUME , WH.WAREHOUSEID "); 
			
	fromClause.append(" FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER ")
										.append(" SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE, FS_FR_WHMASTER WH ");
			
	whereClause.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID ")
		.append(" AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID ")
		.append(" = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID  AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 ")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'  AND ((B.LEG_TYPE IN ")
		.append(" ('TP','GT','TG') AND  B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) ")
		.append(" AND ( ( (SELECT SUM(BOXESRELEASEDCURR) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) ")
		.append(" < B.PIECES_RECEIVED ) OR ( (SELECT COUNT(HOUSEDOCNO) FROM FS_FR_DELIVERYORDER WHERE ")
		.append(" HOUSEDOCNO=RP.HAWB_ID) = 0 ) ) AND ( (SELECT SUM(BOXESRECEIVED) FROM FS_FR_WHMASTER ")
		.append(" WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) ) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY RP.HAWB_ID DESC ");
			
	 }
//for HAWBDestWHView	
//	else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("View") && hawbAttributes.getOperationType().equals("Destination"))
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestWHView"))
	 {
//Logger.info(FILE_NAME,"....WH ..View...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+"....WH ..View...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT 	DISTINCT RP.HAWB_ID, WH.WAREHOUSEID FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD, FS_FR_WHMASTER WH WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID AND HD.WHSTATUS != 'ND' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) ) AND ROUND( TO_DATE('18-03-05 13:56', 'DD-MM-YY HH24:MI') - HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;
//ADVANCED QUERY

/*SELECT 	DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT ,  HD.TOTPIECES ,
HD.ACTUALVOLUME , WH.WAREHOUSEID 
FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE, FS_FR_WHMASTER WH 
WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID 
AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID  = RP.SHIPPER_ID  
AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID AND HD.WHSTATUS != 'ND' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN 
( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  
AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) ) 
AND ROUND( TO_DATE('18-03-05 13:56', 'DD-MM-YY HH24:MI') - HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;

;
*/

    selectClause.append(" SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID, ")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
										.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
										.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
										.append(" HD.TOTPIECES ,HD.ACTUALVOLUME , WH.WAREHOUSEID "); 
			
	fromClause.append(" FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER ")
										.append(" SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE, FS_FR_WHMASTER WH ");
			
	whereClause.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID ")
		.append(" AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID ")
		.append(" = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID AND HD.WHSTATUS != 'ND' AND RP.PRMY_MODE = 1 ")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'  AND ")
		.append(" ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND ")
		.append(" B.AUTO_MNUL_FLAG='M')) ) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY RP.HAWB_ID DESC ");
			
	 }
// for HAWBDestWHDelete

//	else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("Delete") && hawbAttributes.getOperationType().equals("Destination"))
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestWHDelete"))
	 {
//Logger.info(FILE_NAME,".....WH ...Delete...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
logger.info(FILE_NAME+".....WH ...Delete...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");		 
//OLD QUERY	 
//SELECT 	DISTINCT RP.HAWB_ID, WH.WAREHOUSEID FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD, FS_FR_WHMASTER WH WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (SELECT SUM(BOXESRECEIVED) FROM FS_FR_WHMASTER WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) ) AND (SELECT COUNT(HOUSEDOCNO) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0 AND ROUND( TO_DATE('18-03-05 14:13', 'DD-MM-YY HH24:MI') - HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*SELECT 	DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  
CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT ,  HD.TOTPIECES ,
HD.ACTUALVOLUME , WH.WAREHOUSEID 
FROM FS_RT_PLAN RP,FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE,FS_FR_WHMASTER WH 
WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID  AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND 
HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID  = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID 
AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B 
WHERE A.RT_PLAN_ID = B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND 
B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (SELECT SUM(BOXESRECEIVED) 
FROM FS_FR_WHMASTER WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) ) AND (SELECT COUNT(HOUSEDOCNO) 
FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0 AND ROUND( TO_DATE('18-03-05 14:13', 'DD-MM-YY HH24:MI') 
- HD.HOUSEDOCDATE) < 15 AND RP.HAWB_ID LIKE '%' ;
*/

    selectClause.append(" SELECT DISTINCT RP.HAWB_ID,SHIPPER.CUSTOMERID SHIPPERID, ")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
										.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
										.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
										.append(" HD.TOTPIECES ,HD.ACTUALVOLUME , WH.WAREHOUSEID "); 
			
	fromClause.append(" FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER ")
										.append(" SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE, FS_FR_WHMASTER WH ");
			
	whereClause.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND WH.BOOKINGID = HD.HOUSEDOCID  ")
		.append(" AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND HD.SHIPPERID ")
		.append(" = RP.SHIPPER_ID  AND HD.CONSIGNEEID  = RP.CONSIGNEE_ID AND HD.WHSTATUS = 'PD' AND RP.PRMY_MODE = 1 ")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND ")
		.append(" B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ")
		.append(" ( (SELECT SUM(BOXESRECEIVED) FROM FS_FR_WHMASTER WHERE BOOKINGID=RP.HAWB_ID) < B.PIECES_RECEIVED ) )")
		.append(" AND (SELECT COUNT(HOUSEDOCNO) FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO=RP.HAWB_ID) = 0 ");
		
        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY RP.HAWB_ID DESC ");
			
	 }
	
//else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("OriginAdd") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBOriginChargesAdd"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginAdd...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginAdd...conditions entered..");		 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE  ORIGINTERMINAL='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' UNION SELECT MHDR.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' UNION SELECT MASTERDOCID MID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MASTERDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' AND MASTERDOCID NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER);

//ADVANCED QUERY
/*
SELECT 
 HD.HOUSEDOCID
 , SHIPPER.CUSTOMERID SHIPPERID
 , SHIPPER.COMPANYNAME SHIPPERNAME
 ,CONSIGNEE.CUSTOMERID CONSIGNEEID
 , CONSIGNEE.COMPANYNAME CONSIGNEENAME
 ,HD.ORIGINTERMINAL
 ,HD.DESTTERMINAL
 ,  HD.GROSSWEIGHT 
 , HD.TOTPIECES 
 ,HD.ACTUALVOLUME
from 
 (
   SELECT 
    HOUSEDOCID,ACTUALVOLUME,TOTPIECES,GROSSWEIGHT,DESTTERMINAL,ORIGINTERMINAL,CONSIGNEEID,SHIPPERID ,HOUSEDOCDATE,ORIGINLOCATION ,DESTINATIONLOCATION  
   FROM 
    FS_FR_HOUSEDOCHDR 
   WHERE  
    ORIGINTERMINAL='DHLHKG'  
    AND ROUND(TO_DATE('18/03/2005 20:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  
    AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') 
    AND SHIPMENTMODE =1 
    AND HOUSEDOCID LIKE '%' 
  UNION 
   SELECT 
   MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL ,DMHDR.ORIGINTERMINAL,DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID  ,
    MASTERDOCDATE  , DMHDR.ORIGINLOCATION,    DMHDR.DESTLOCATION  

   FROM 
    FS_FR_MASTERDOCHDR MHDR,
    FS_FR_DIRECTMASTERHDR DMHDR 
   WHERE 
    MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
    AND MHDR.TERMINALID='DHLHKG' 
    AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  
    AND ROUND(TO_DATE('18/03/2005 20:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  
    AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 
    AND MHDR.MASTERDOCID LIKE '%' 
  UNION 
   SELECT 
    MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','' ,MASTERDOCDATE ,'',''   
   FROM 
    FS_FR_MASTERDOCHDR 
   WHERE TERMINALID='DHLHKG'  
    AND ROUND(TO_DATE('18/03/2005 20:03','DD/MM/YYYY HH24:MI') -MASTERDOCDATE ) < 15 
    AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL 
    AND MASTERDOCID LIKE '%' AND MASTERDOCID NOT IN 
     (
       SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER
     )
  ) HD
,
FS_FR_CUSTOMERMASTER SHIPPER,
FS_FR_CUSTOMERMASTER CONSIGNEE
WHERE 
HD.SHIPPERID = SHIPPER.CUSTOMERID(+)
AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+);
*/


    selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM ( SELECT HOUSEDOCID,ACTUALVOLUME,TOTPIECES,GROSSWEIGHT, ")
		.append(" DESTTERMINAL,ORIGINTERMINAL,CONSIGNEEID,SHIPPERID ,HOUSEDOCDATE,ORIGINLOCATION,DESTINATIONLOCATION  ")
		.append("  FROM  FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE  ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"'  AND (ORIGINSTATUS IS NULL ")
		.append(" OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 ")
		.append(" AND HOUSEDOCID LIKE '%' ")
		.append(" UNION ")
		.append(" SELECT MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL ")
		.append(" ,DMHDR.ORIGINTERMINAL , DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE , ")
		.append(" DMHDR.ORIGINLOCATION , DMHDR.DESTLOCATION  FROM  FS_FR_MASTERDOCHDR ")
		.append(" MHDR, FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND ")
		.append(" MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND (DMHDR.ORIGINSTATUS IS NULL OR ")
		.append(" DMHDR.ORIGINSTATUS!='F')  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1  ")
		.append(" UNION  ")
		.append(" SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE,'',''  FROM ")
		.append(" FS_FR_MASTERDOCHDR  WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"'  AND SHIPMENTMODE =1 ")
		.append(" AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID ")
		.append(" NOT IN  ")
      	.append(" ( SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER  ) ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
            //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");


	 }
//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("OriginModify") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBOriginChargesModify"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginModify...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginModify...conditions entered..");		 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE  ORIGINTERMINAL='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' UNION SELECT MHDR.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' UNION SELECT MASTERDOCID MID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MASTERDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' AND MASTERDOCID NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER);

//ADVANCED QUERY
/*SELECT  
 HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID  , SHIPPER.COMPANYNAME SHIPPERNAME ,
 CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME  CONSIGNEENAME ,
 HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME FROM 
 ( 
   SELECT 
    HOUSEDOCID,      ACTUALVOLUME,     TOTPIECES,    GROSSWEIGHT,  
    DESTTERMINAL,    ORIGINTERMINAL,   CONSIGNEEID,  SHIPPERID ,
    HOUSEDOCDATE  , ORIGINLOCATION,    DESTINATIONLOCATION  
  FROM  
    FS_FR_HOUSEDOCHDR  
WHERE  ORIGINTERMINAL='DHLHKG'  
AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  
AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') 
AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' 
UNION 
SELECT  MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL  ,
DMHDR.ORIGINTERMINAL,DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE,ORIGINLOCATION ,DESTLOCATION   
FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  
AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  
AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' 
UNION 
SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE ,'',''   
FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') 
-MASTERDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' AND MASTERDOCID 
NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER) )HD
,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE , FS_FR_LOCATIONMASTER ORCOUNTRY   
WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+)  AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND 
ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION  AND SYSDATE- HD.HOUSEDOCDATE  <= 15 ORDER BY HD.HOUSEDOCID DESC ;*/

selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM ( SELECT HOUSEDOCID,ACTUALVOLUME,TOTPIECES,GROSSWEIGHT, ")
		.append(" DESTTERMINAL,ORIGINTERMINAL,CONSIGNEEID,SHIPPERID ,HOUSEDOCDATE,ORIGINLOCATION,DESTINATIONLOCATION  ")
		.append("  FROM  FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE  ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"'  AND (ORIGINSTATUS IS NULL ")
		.append(" OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 ")
		.append(" AND HOUSEDOCID LIKE '%' ")
		.append(" UNION ")
		.append(" SELECT MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL ")
		.append(" ,DMHDR.ORIGINTERMINAL , DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE , ")
		.append(" DMHDR.ORIGINLOCATION , DMHDR.DESTLOCATION  FROM  FS_FR_MASTERDOCHDR ")
		.append(" MHDR, FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND ")
		.append(" MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND (DMHDR.ORIGINSTATUS IS NULL OR ")
		.append(" DMHDR.ORIGINSTATUS!='F')  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1  ")
		.append(" UNION  ")
		.append(" SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE,'',''  FROM ")
		.append(" FS_FR_MASTERDOCHDR  WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"'  AND SHIPMENTMODE =1 ")
		.append(" AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID ")
		.append(" NOT IN  ")
      	.append(" ( SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER  ) ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");


        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
     }

//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("OriginView") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBOriginChargesView"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginView...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginView...conditions entered..");		 
//OLD QUERY	 
//SELECT DISTINCT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' UNION SELECT MHDR.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.TERMINALID='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15 AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' UNION SELECT MASTERDOCID MID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MASTERDOCDATE ) < 15  AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%';

//ADVANCED QUERY
/*SELECT  
 HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID  , SHIPPER.COMPANYNAME SHIPPERNAME ,
 CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME  CONSIGNEENAME ,
 HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME 
 FROM 
 ( 
   SELECT 
    HOUSEDOCID,      ACTUALVOLUME,     TOTPIECES,    GROSSWEIGHT,  
    DESTTERMINAL,    ORIGINTERMINAL,   CONSIGNEEID,  SHIPPERID ,
    HOUSEDOCDATE  , ORIGINLOCATION,    DESTINATIONLOCATION  
  FROM  
    FS_FR_HOUSEDOCHDR  
WHERE  ORIGINTERMINAL='DHLHKG'  

 AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' 
UNION 
SELECT  MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL  ,
DMHDR.ORIGINTERMINAL,DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE,ORIGINLOCATION ,DESTLOCATION   
FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
AND MHDR.TERMINALID='DHLHKG' AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' 
UNION 
SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE ,'',''   FROM FS_FR_MASTERDOCHDR 
WHERE TERMINALID='DHLHKG'  AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' 
AND MASTERDOCID NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER) )HD
,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE , FS_FR_LOCATIONMASTER ORCOUNTRY   
WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+)  AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND 
ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION  AND SYSDATE- HD.HOUSEDOCDATE  <= 15 ORDER BY HD.HOUSEDOCID DESC ;

*/

selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM ( SELECT HOUSEDOCID,ACTUALVOLUME,TOTPIECES,GROSSWEIGHT, ")
		.append(" DESTTERMINAL,ORIGINTERMINAL,CONSIGNEEID,SHIPPERID ,HOUSEDOCDATE,ORIGINLOCATION,DESTINATIONLOCATION  ")
		.append("  FROM  FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE  ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"' AND SHIPMENTMODE =1 ")
		.append(" AND HOUSEDOCID LIKE '%' ")
		.append(" UNION ")
		.append(" SELECT MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL ")
		.append(" ,DMHDR.ORIGINTERMINAL , DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE , ")
		.append(" DMHDR.ORIGINLOCATION , DMHDR.DESTLOCATION  FROM  FS_FR_MASTERDOCHDR ")
		.append(" MHDR, FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND ")
		.append(" MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND MHDR.MASTERTYPE='D' AND ")
		.append(" MHDR.SHIPMENTMODE =1  ")
		.append(" UNION  ")
		.append(" SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE,'',''  FROM ")
		.append(" FS_FR_MASTERDOCHDR  WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"'  AND SHIPMENTMODE =1 ")
		.append(" AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID ")
		.append(" NOT IN  ")
      	.append(" ( SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER  ) ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

     }
//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("OriginDelete") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBOriginChargesDelete"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginDelete...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--OriginDelete...conditions entered..");		 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE  ORIGINTERMINAL='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' UNION SELECT MHDR.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' UNION SELECT MASTERDOCID MID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('18/03/2005 17:03','DD/MM/YYYY HH24:MI') -MASTERDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' AND MASTERDOCID NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER);

//ADVANCED QUERY
/*SELECT  
 HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID  , SHIPPER.COMPANYNAME SHIPPERNAME ,
 CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME  CONSIGNEENAME ,
 HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME FROM 
 ( 
   SELECT 
    HOUSEDOCID,      ACTUALVOLUME,     TOTPIECES,    GROSSWEIGHT,  
    DESTTERMINAL,    ORIGINTERMINAL,   CONSIGNEEID,  SHIPPERID ,
    HOUSEDOCDATE  , ORIGINLOCATION,    DESTINATIONLOCATION  
  FROM  
    FS_FR_HOUSEDOCHDR  
WHERE  ORIGINTERMINAL='DHLHKG'  
AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') -HOUSEDOCDATE ) < 15  
AND (ORIGINSTATUS IS NULL OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') 
AND SHIPMENTMODE =1 AND HOUSEDOCID LIKE '%' 
UNION 
SELECT  MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL  ,
DMHDR.ORIGINTERMINAL,DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE,ORIGINLOCATION ,DESTLOCATION   
FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS IS NULL OR DMHDR.ORIGINSTATUS!='F')  
AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') -MHDR.MASTERDOCDATE ) < 15  
AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1 AND MHDR.MASTERDOCID LIKE '%' 
UNION 
SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE ,'',''   
FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='DHLHKG'  AND ROUND(TO_DATE('19/03/2005 12:03','DD/MM/YYYY HH24:MI') 
-MASTERDOCDATE ) < 15 AND SHIPMENTMODE =1 AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID LIKE '%' AND MASTERDOCID 
NOT IN (SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER) )HD
,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE , FS_FR_LOCATIONMASTER ORCOUNTRY   
WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+)  AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND 
ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION  AND SYSDATE- HD.HOUSEDOCDATE  <= 15 ORDER BY HD.HOUSEDOCID DESC ;*/

    selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM ( SELECT HOUSEDOCID,ACTUALVOLUME,TOTPIECES,GROSSWEIGHT, ")
		.append(" DESTTERMINAL,ORIGINTERMINAL,CONSIGNEEID,SHIPPERID ,HOUSEDOCDATE,ORIGINLOCATION,DESTINATIONLOCATION  ")
		.append("  FROM  FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE  ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"'  AND (ORIGINSTATUS IS NULL ")
		.append(" OR ORIGINSTATUS!= 'F' OR DESTINATIONSTATUS  IS NULL OR DESTINATIONSTATUS !='F') AND SHIPMENTMODE =1 ")
		.append(" AND HOUSEDOCID LIKE '%' ")
		.append(" UNION ")
		.append(" SELECT MHDR.MASTERDOCID,0,DMHDR.TOTALPCSRECEIVED,DMHDR.GROSSWEIGHT ,DMHDR.DESTTERMINAL ")
		.append(" ,DMHDR.ORIGINTERMINAL , DMHDR.CONSIGNEEID ,DMHDR.SHIPPERID ,MASTERDOCDATE , ")
		.append(" DMHDR.ORIGINLOCATION , DMHDR.DESTLOCATION  FROM  FS_FR_MASTERDOCHDR ")
		.append(" MHDR, FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND ")
		.append(" MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND (DMHDR.ORIGINSTATUS IS NULL OR ")
		.append(" DMHDR.ORIGINSTATUS!='F')  AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE =1  ")
		.append(" UNION  ")
		.append(" SELECT MASTERDOCID,0,PIECES,CHARGEABLEWEIGHT ,'','','','',MASTERDOCDATE,'',''  FROM ")
		.append(" FS_FR_MASTERDOCHDR  WHERE TERMINALID='"+hawbAttributes.getTerminalID()+"'  AND SHIPMENTMODE =1 ")
		.append(" AND SUB_AGENT_ID  IS NOT NULL AND MASTERDOCID ")
		.append(" NOT IN  ")
      	.append(" ( SELECT HOUSEDOCID  FROM FS_FR_FRTINVOICEMASTER  ) ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");

        	
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");


     }
//else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("DestinationAdd") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestChargesAdd"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationAdd...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationAdd...conditions entered..");		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*SELECT 

 A.HAWB_ID,

 HD.ORIGINTERMINAL ,

 HD.DESTTERMINAL ,

 HD.GROSSWEIGHT,

 HD.TOTPIECES,

 HD.ACTUALVOLUME ,

 SHIPPER.CUSTOMERID SHIPPERID  , 

 SHIPPER.COMPANYNAME SHIPPERNAME ,

 CONSIGNEE.CUSTOMERID CONSIGNEEID , 

 CONSIGNEE.COMPANYNAME  CONSIGNEENAME

FROM 

(

SELECT 

 RP.HAWB_ID, 

 RP.PRMY_MODE

FROM 

 FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD 

WHERE 

 HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID 

 AND HD.HOUSEDOCID NOT IN 

 ( 

  SELECT HOUSEDOCID 

  FROM FS_FR_HOUSEDOCHDR 

  WHERE DESTINATIONSTATUS = 'F'  

  AND ORIGINSTATUS = 'F' 

 ) 

 AND RP.HAWB_ID IN 

 ( 

  SELECT A.HAWB_ID 

  FROM FS_RT_PLAN A, FS_RT_LEG B 

  WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  

  AND A.DEST_TRML_ID = 'DHLHKG'  

  AND ((B.LEG_TYPE IN ('TP','GT','TG') 

  AND B.SHPMNT_STATUS IN ('P','B')) 

  OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) 

  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) 

  OR (RP.PRMY_MODE = 1) ) )  

UNION 

   SELECT RP.HAWB_ID, 

    RP.PRMY_MODE 

   FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD 

   WHERE RP.HAWB_ID = HD.HOUSEDOCID 

   AND HD.HOUSEDOCID NOT IN 

   ( 

     SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR 

     WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' 

   ) 

  AND RP.HAWB_ID IN 

  ( 

    SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B 

    WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  

    AND A.DEST_TRML_ID = 'DHLHKG'  

    AND ((B.LEG_TYPE IN ('TP','GT','TG') 

    AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' 

    AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 

    AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) 

   ) 

 ) 

) 

 A, 

 FS_FR_HOUSEDOCHDR HD,

 FS_FR_CUSTOMERMASTER SHIPPER,

 FS_FR_CUSTOMERMASTER CONSIGNEE 

WHERE  

 HD.SHIPPERID = SHIPPER.CUSTOMERID(+) 

 AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)

 AND A.HAWB_ID = HD.HOUSEDOCID;*/

		selectClause.append(" SELECT  A.HAWB_ID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
		fromClause.append(" FROM ")
		.append(" ( SELECT  RP.HAWB_ID,  RP.PRMY_MODE FROM  FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE  HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID  AND HD.HOUSEDOCID ")
		.append(" NOT IN ")
		.append(" ( SELECT HOUSEDOCID  FROM FS_FR_HOUSEDOCHDR  WHERE DESTINATIONSTATUS = 'F' AND ORIGINSTATUS = 'F' )")
		.append(" AND RP.HAWB_ID IN  ( ")
		.append(" SELECT A.HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B   WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  ")
        .append(" AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ")
		.append(" ('P','B'))  OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND ")
		.append(" RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  ")
        .append(" UNION  SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ")
		.append(" ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS ='F' AND ORIGINSTATUS ='F' )")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B  WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID  AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' ")
		.append(" AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR ")
		.append(" (RP.PRMY_MODE = 1) ) ) )  A, ")
		.append("  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
		whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND A.HAWB_ID = HD.HOUSEDOCID(+) ");


		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
           // Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");


	 }
//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("DestinationModify") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestChargesModify"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationModify...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationModify...conditions entered..");		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

selectClause.append(" SELECT  A.HAWB_ID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
		fromClause.append(" FROM ")
		.append(" ( SELECT  RP.HAWB_ID,  RP.PRMY_MODE FROM  FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE  HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID  AND HD.HOUSEDOCID ")
		.append(" NOT IN ")
		.append(" ( SELECT HOUSEDOCID  FROM FS_FR_HOUSEDOCHDR  WHERE DESTINATIONSTATUS = 'F' AND ORIGINSTATUS = 'F' )")
		.append(" AND RP.HAWB_ID IN  ( ")
		.append(" SELECT A.HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B   WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  ")
        .append(" AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ")
		.append(" ('P','B'))  OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND ")
		.append(" RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  ")
        .append(" UNION  SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ")
		.append(" ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS ='F' AND ORIGINSTATUS ='F' )")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B  WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID  AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' ")
		.append(" AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR ")
		.append(" (RP.PRMY_MODE = 1) ) ) )  A, ")
		.append("  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
		whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND A.HAWB_ID = HD.HOUSEDOCID(+) ");


	//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

     }

//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("DestinationView") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestChargesView"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationView...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationView...conditions entered..");		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

selectClause.append(" SELECT  A.HAWB_ID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
		fromClause.append(" FROM ")
		.append(" ( SELECT  RP.HAWB_ID,  RP.PRMY_MODE FROM  FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE  HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID  AND HD.HOUSEDOCID ")
		.append(" NOT IN ")
		.append(" ( SELECT HOUSEDOCID  FROM FS_FR_HOUSEDOCHDR  WHERE DESTINATIONSTATUS = 'F' AND ORIGINSTATUS = 'F' )")
		.append(" AND RP.HAWB_ID IN  ( ")
		.append(" SELECT A.HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B   WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  ")
        .append(" AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ")
		.append(" ('P','B'))  OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND ")
		.append(" RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  ")
        .append(" UNION  SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ")
		.append(" ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS ='F' AND ORIGINSTATUS ='F' )")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B  WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID  AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' ")
		.append(" AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR ")
		.append(" (RP.PRMY_MODE = 1) ) ) )  A, ")
		.append("  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
		whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND A.HAWB_ID = HD.HOUSEDOCID(+) ");


	//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

     }
//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("DestinationDelete") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestChargesDelete"))
	 {
//Logger.info(FILE_NAME,".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationDelete...conditions entered..");		 
logger.info(FILE_NAME+".....Charges--@@@@@@@@@@@@@@@@@@@@@@@--DestinationDelete...conditions entered..");		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

selectClause.append(" SELECT  A.HAWB_ID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
		fromClause.append(" FROM ")
		.append(" ( SELECT  RP.HAWB_ID,  RP.PRMY_MODE FROM  FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE  HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID  AND HD.HOUSEDOCID ")
		.append(" NOT IN ")
		.append(" ( SELECT HOUSEDOCID  FROM FS_FR_HOUSEDOCHDR  WHERE DESTINATIONSTATUS = 'F' AND ORIGINSTATUS = 'F' )")
		.append(" AND RP.HAWB_ID IN  ( ")
		.append(" SELECT A.HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B   WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  ")
        .append(" AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"'   AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ")
		.append(" ('P','B'))  OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND ")
		.append(" RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  ")
        .append(" UNION  SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD ")
		.append(" WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ")
		.append(" ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS ='F' AND ORIGINSTATUS ='F' )")
		.append(" AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B  WHERE A.RT_PLAN_ID = ")
		.append(" B.RT_PLAN_ID  AND A.DEST_TRML_ID = '"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' ")
		.append(" AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR ")
		.append(" (RP.PRMY_MODE = 1) ) ) )  A, ")
		.append("  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
		whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND A.HAWB_ID = HD.HOUSEDOCID(+) ");


		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

			

     }


//else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("Origin") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBOriginChargesSuppli"))
	 {
//Logger.info(FILE_NAME,"..SUPPL.Charges--@@@@@@@@@@@@@@@@@@@@@@@--ORIGIN...conditions entered..");		 
logger.info(FILE_NAME+"..SUPPL.Charges--@@@@@@@@@@@@@@@@@@@@@@@--ORIGIN...conditions entered..");		 

//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID
 , SHIPPER.CUSTOMERID SHIPPERID
 , SHIPPER.COMPANYNAME SHIPPERNAME
 ,CONSIGNEE.CUSTOMERID CONSIGNEEID
 , CONSIGNEE.COMPANYNAME CONSIGNEENAME
 ,HD.ORIGINTERMINAL
 ,HD.DESTTERMINAL
 ,  HD.GROSSWEIGHT 
 , HD.TOTPIECES 
 ,HD.ACTUALVOLUME,
HD.HOUSEDOCDATE   
FROM
(SELECT HOUSEDOCID
 , SHIPPERID
 , CONSIGNEEID
 , ORIGINTERMINAL
 , DESTTERMINAL
 , GROSSWEIGHT
 , TOTPIECES 
 , ACTUALVOLUME
 , HOUSEDOCDATE,
 , ORIGINLOCATION 
 , DESTINATIONLOCATION   
FROM FS_FR_HOUSEDOCHDR 
WHERE 
 (ORIGINSTATUS ='F' OR DESTINATIONSTATUS='F')  AND SHIPMENTMODE = 1 AND ORIGINTERMINAL ='DHLHKG'  
UNION  
SELECT MHDR.MASTERDOCID
,SHIPPERID 
,CONSIGNEEID 
,ORIGINTERMINAL
,DESTTERMINAL
,DMHDR.GROSSWEIGHT
,DMHDR.TOTALPCSRECEIVED
,0
,MASTERDOCDATE
,DMHDR.ORIGINLOCATION 
,DMHDR.DESTLOCATION  
 FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
 AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS = 'F' OR DMHDR.DESTINATIONSTATUS='F')  
 AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE=1
) HD 
,FS_FR_CUSTOMERMASTER SHIPPER,
FS_FR_CUSTOMERMASTER CONSIGNEE
WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+)
AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+);
*/

		selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	    fromClause.append(" FROM (SELECT HOUSEDOCID , SHIPPERID ,CONSIGNEEID ,ORIGINTERMINAL ")
		.append(" , DESTTERMINAL  ,GROSSWEIGHT ,TOTPIECES , ACTUALVOLUME , HOUSEDOCDATE ,ORIGINLOCATION , ")
		.append(" DESTINATIONLOCATION    FROM FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE   (ORIGINSTATUS ='F' OR DESTINATIONSTATUS='F')  AND SHIPMENTMODE = 1 AND ")
		.append(" ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"'  ")
		.append(" UNION   ")
		.append(" SELECT MHDR.MASTERDOCID ,SHIPPERID ,CONSIGNEEID ,ORIGINTERMINAL ,DESTTERMINAL ,DMHDR.GROSSWEIGHT ")
		.append(" ,DMHDR.TOTALPCSRECEIVED ,0,MASTERDOCDATE ,DMHDR.ORIGINLOCATION ,DMHDR.DESTLOCATION   ")
		.append("  FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR  ")
		.append(" WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID  AND MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" (DMHDR.ORIGINSTATUS = 'F' OR DMHDR.DESTINATIONSTATUS='F')  AND MHDR.MASTERTYPE='D' AND ")
		.append(" MHDR.SHIPMENTMODE=1 ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	    whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");

		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
	 }

//	 else if (hawbAttributes.getMode().equals("Air") && hawbAttributes.getOperation().equals("Destination") )
else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("HAWBDestChargesSuppli"))
	 {
//Logger.info(FILE_NAME,"...SUPPL.Charges--@@@@@@@@@@@@@@@@@@@@@@@--Destination...conditions entered..");		 
logger.info(FILE_NAME+"...SUPPL.Charges--@@@@@@@@@@@@@@@@@@@@@@@--Destination...conditions entered..");		 
//OLD QUERY	 
//SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE HD.SHIPMENTMODE=1 AND RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M')) AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%'  UNION SELECT RP.HAWB_ID, RP.PRMY_MODE FROM FS_RT_PLAN RP, FS_FR_HOUSEDOCHDR HD WHERE RP.HAWB_ID = HD.HOUSEDOCID AND HD.HOUSEDOCID NOT IN ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTINATIONSTATUS = 'F'  AND ORIGINSTATUS = 'F' ) AND RP.HAWB_ID IN ( SELECT A.HAWB_ID FROM FS_RT_PLAN A, FS_RT_LEG B WHERE A.RT_PLAN_ID = B.RT_PLAN_ID  AND ROUND( TO_DATE('18/03/2005 17:03', 'DD/MM/YYYY HH24:MI') - B.RECEIVED_DATE) < 15 AND A.DEST_TRML_ID = 'DHLHKG'  AND ((B.LEG_TYPE IN ('TP','GT','TG') AND B.SHPMNT_STATUS IN ('P','B')) OR (B.LEG_TYPE ='GT' AND B.AUTO_MNUL_FLAG='M'))  AND ( (B.SHPMNT_MODE = 1 AND RP.PRMY_MODE != 1) OR (RP.PRMY_MODE = 1) ) )  AND RP.HAWB_ID LIKE '%' ;

//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID
 , SHIPPER.CUSTOMERID SHIPPERID
 , SHIPPER.COMPANYNAME SHIPPERNAME
 ,CONSIGNEE.CUSTOMERID CONSIGNEEID
 , CONSIGNEE.COMPANYNAME CONSIGNEENAME
 ,HD.ORIGINTERMINAL
 ,HD.DESTTERMINAL
 ,  HD.GROSSWEIGHT 
 , HD.TOTPIECES 
 ,HD.ACTUALVOLUME,
HD.HOUSEDOCDATE   
FROM
(SELECT HOUSEDOCID
 , SHIPPERID
 , CONSIGNEEID
 , ORIGINTERMINAL
 , DESTTERMINAL
 , GROSSWEIGHT
 , TOTPIECES 
 , ACTUALVOLUME
 , HOUSEDOCDATE,
 , ORIGINLOCATION 
 , DESTINATIONLOCATION   
FROM FS_FR_HOUSEDOCHDR 
WHERE 
 (ORIGINSTATUS ='F' OR DESTINATIONSTATUS='F')  AND SHIPMENTMODE = 1 AND DESTTERMINAL ='DHLHKG'  
UNION  
SELECT MHDR.MASTERDOCID
,SHIPPERID 
,CONSIGNEEID 
,ORIGINTERMINAL
,DESTTERMINAL
,DMHDR.GROSSWEIGHT
,DMHDR.TOTALPCSRECEIVED
,0
,MASTERDOCDATE
,DMHDR.ORIGINLOCATION 
,DMHDR.DESTLOCATION  
 FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID 
 AND MHDR.TERMINALID='DHLHKG' AND (DMHDR.ORIGINSTATUS = 'F' OR DMHDR.DESTINATIONSTATUS='F')  
 AND MHDR.MASTERTYPE='D' AND MHDR.SHIPMENTMODE=1
) HD 
,FS_FR_CUSTOMERMASTER SHIPPER,
FS_FR_CUSTOMERMASTER CONSIGNEE
WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+)
AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+);
*/

		selectClause.append(" SELECT  HD.HOUSEDOCID , SHIPPER.CUSTOMERID SHIPPERID ")
		.append(" , SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID , CONSIGNEE.COMPANYNAME ")
		.append(" CONSIGNEENAME ,HD.ORIGINTERMINAL ,HD.DESTTERMINAL ,HD.GROSSWEIGHT,HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	    fromClause.append(" FROM (SELECT HOUSEDOCID , SHIPPERID ,CONSIGNEEID ,ORIGINTERMINAL ")
		.append(" , DESTTERMINAL  ,GROSSWEIGHT ,TOTPIECES , ACTUALVOLUME , HOUSEDOCDATE,ORIGINLOCATION , ")
		.append(" DESTINATIONLOCATION    FROM FS_FR_HOUSEDOCHDR  ")
		.append(" WHERE   (ORIGINSTATUS ='F' OR DESTINATIONSTATUS='F')  AND SHIPMENTMODE = 1 AND ")
		.append(" DESTTERMINAL ='"+hawbAttributes.getTerminalID()+"'  ")
		.append(" UNION   ")
		.append(" SELECT MHDR.MASTERDOCID ,SHIPPERID ,CONSIGNEEID ,ORIGINTERMINAL ,DESTTERMINAL ,DMHDR.GROSSWEIGHT ")
		.append(" ,DMHDR.TOTALPCSRECEIVED ,0,MASTERDOCDATE,DMHDR.ORIGINLOCATION ,DMHDR.DESTLOCATION   ")
		.append("  FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR  ")
		.append(" WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID  AND MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND ")
		.append(" (DMHDR.ORIGINSTATUS = 'F' OR DMHDR.DESTINATIONSTATUS='F')  AND MHDR.MASTERTYPE='D' AND ")
		.append(" MHDR.SHIPMENTMODE=1 ) HD ")
		.append(" ,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	    whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) ")
		.append(" AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) ");


		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
	 }

else if (hawbAttributes.getInvokerOperation().equals("HAWBActivityAdd") )
	{
		//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHBLAdvDAO...@@@--HAWBActivityAdd entered...."); 	 \
    logger.info(FILE_NAME+"...@@@@@@@@@@.ETHBLAdvDAO...@@@--HAWBActivityAdd entered...."); 	 
//OLD QUERY
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=2 AND ORIGINTERMINAL='DHLHKG' AND HOUSEDOCID NOT IN(SELECT DOCID FROM FS_FR_HOUSEACTIVITYDTL) AND RECEIPTSTATUS IS NULL  AND HOUSEDOCID LIKE '%' ORDER BY HOUSEDOCID;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID,CONSIGNEE.COMPANYNAME CONSIGNEENAME, ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES , 
HD.ACTUALVOLUME  FROM FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  WHERE  
HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND SHIPMENTMODE=1 AND 
ORIGINTERMINAL='"+hblAttributes.getTerminalID()+"' AND HOUSEDOCID NOT IN(SELECT DOCID FROM FS_FR_HOUSEACTIVITYDTL) 
AND RECEIPTSTATUS IS NULL  
AND HOUSEDOCID LIKE '%' ORDER BY HOUSEDOCID;*/



	selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, ")
				.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
				.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
				.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
				.append(" HD.TOTPIECES ,HD.ACTUALVOLUME  "); 
			
	fromClause.append(" FROM FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER,FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID AND ")
		       .append(" HD.SHIPMENTMODE=1 AND ORIGINTERMINAL='"+hawbAttributes.getTerminalID()+"' AND ")
			   .append(" HOUSEDOCID NOT IN(SELECT DOCID FROM FS_FR_HOUSEACTIVITYDTL) AND RECEIPTSTATUS IS NULL ");


		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
	 }
else if (hawbAttributes.getInvokerOperation().equals("HAWBActivityModify")||hawbAttributes.getInvokerOperation().equals("HAWBActivityView")||hawbAttributes.getInvokerOperation().equals("HAWBActivityDelete") )
	{
		//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered :"+hawbAttributes.getInvokerOperation()); 
    logger.info(FILE_NAME+"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered :"+hawbAttributes.getInvokerOperation()); 
//OLD QUERY
//SELECT DOCID FROM FS_FR_HOUSEACTIVITYHDR WHERE DOCTYPE='H' AND SHIPMENTMODE=2 AND TERMINALID='DHLHKG';
//ADVANCED QUERY
/*SELECT DOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID 
CONSIGNEEID,CONSIGNEE.COMPANYNAME CONSIGNEENAME, ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES , 
HD.ACTUALVOLUME  
FROM FS_FR_HOUSEACTIVITYHDR HA ,FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, 
FS_FR_CUSTOMERMASTER CONSIGNEE  
WHERE  HD.HOUSEDOCID=HA.DOCID AND
HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  AND HA.DOCTYPE='H' AND 
HA.SHIPMENTMODE=1 AND HA.TERMINALID='"+hblAttributes.getTerminalID()+"';*/




	selectClause.append(" SELECT DOCID,SHIPPER.CUSTOMERID SHIPPERID, ")
				.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID ")
				.append(" CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
				.append(" HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT , ")
				.append(" HD.TOTPIECES ,HD.ACTUALVOLUME  "); 
			
	fromClause.append(" FROM FS_FR_HOUSEACTIVITYHDR HA , FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER ")
			  .append(" SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.HOUSEDOCID=HA.DOCID AND HD.SHIPPERID = SHIPPER.CUSTOMERID  AND HD.CONSIGNEEID  = ")
		       .append(" CONSIGNEE.CUSTOMERID  AND HA.DOCTYPE='H' AND HA.SHIPMENTMODE=1 AND ")
		       .append(" HA.TERMINALID='"+hawbAttributes.getTerminalID()+"' ");


		//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
	 }
else if (hawbAttributes.getInvokerOperation().equals("HAWBDestAttach"))
	{
		//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered :"+hawbAttributes.getInvokerOperation()); 
    logger.info(FILE_NAME+"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered :"+hawbAttributes.getInvokerOperation()); 

//OLD QUERY
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '%' AND  DESTTERMINAL ='DHLHKG' AND SHIPMENTMODE=1
//ADVANCED QUERY
/*SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID
,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME
FROM  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE 
WHERE SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID AND HOUSEDOCID LIKE '%' 
AND  DESTTERMINAL ='DHLHKG' AND SHIPMENTMODE=1;*/



selectClause.append(" SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,");
selectClause.append(" CONSIGNEE.CUSTOMERID CONSIGNEEID,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,");
selectClause.append(" DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME ");

fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");

whereClause.append(" WHERE SHIPPER.CUSTOMERID(+) = SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = CONSIGNEEID AND ");
whereClause.append(" HOUSEDOCID LIKE '%' AND  DESTTERMINAL ='");
whereClause.append(hawbAttributes.getTerminalID());
whereClause.append("' ");
whereClause.append(" AND HD.SHIPMENTMODE=1 ");

//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");


	}

else if (hawbAttributes.getInvokerOperation().equals("AESSED"))
	{
		//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered HAWB:"+hawbAttributes.getInvokerOperation()); 
    logger.info(FILE_NAME+"...@@@@@@@@@@.ETHBLAdvDAO...@@@-- entered HAWB:"+hawbAttributes.getInvokerOperation()); 

//OLD QUERY
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=1 AND HOUSEDOCID LIKE '%' AND ORIGINTERMINAL= 'DHLHKG' AND HOUSEDOCID NOT IN(SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NOT NULL )  UNION  SELECT MASTERDOCID FROM FS_FR_DIRECTMASTERHDR WHERE MASTERDOCID LIKE '%' AND ORIGINTERMINAL = 'DHLHKG'  AND HOUSEDOCID  IS NULL  AND MASTERDOCID NOT IN (SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NOT NULL )|SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=1 AND HOUSEDOCID LIKE '%' AND ORIGINTERMINAL= 'DHLHKG' AND HOUSEDOCID NOT IN(SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NOT NULL )  UNION  SELECT MASTERDOCID FROM FS_FR_DIRECTMASTERHDR WHERE MASTERDOCID LIKE '%' AND ORIGINTERMINAL = 'DHLHKG'  AND HOUSEDOCID  IS NULL  AND MASTERDOCID NOT IN (SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NOT NULL );
//ADVANCED QUERY
/*SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID
,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME
FROM (
SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=1 AND HOUSEDOCID LIKE '%' 
AND ORIGINTERMINAL= 'DHLHKG' AND HOUSEDOCID NOT IN(SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL 
WHERE SUBMISSIONDATE IS NOT NULL )  UNION  SELECT MASTERDOCID FROM FS_FR_DIRECTMASTERHDR WHERE MASTERDOCID LIKE '%' 
AND ORIGINTERMINAL = 'DHLHKG'  AND HOUSEDOCID  IS NULL  AND MASTERDOCID 
NOT IN (SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NOT NULL )
) A,
FS_FR_HOUSEDOCHDR HD,

 FS_FR_CUSTOMERMASTER SHIPPER,

 FS_FR_CUSTOMERMASTER CONSIGNEE 

WHERE  

 HD.SHIPPERID = SHIPPER.CUSTOMERID(+) 

 AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)

 AND A.HOUSEDOCID = HD.HOUSEDOCID(+);*/
 
selectClause.append(" SELECT	HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,");
selectClause.append(" CONSIGNEE.CUSTOMERID CONSIGNEEID,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,");
selectClause.append(" DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME ");

fromClause.append(" FROM ( SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=1 AND ");
fromClause.append(" HOUSEDOCID LIKE '%' AND ORIGINTERMINAL= '");
fromClause.append(hawbAttributes.getTerminalID());
fromClause.append("' ");
fromClause.append(" AND HOUSEDOCID NOT IN(SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL ");

fromClause.append(" WHERE SUBMISSIONDATE IS NOT NULL )  UNION  SELECT MASTERDOCID FROM FS_FR_DIRECTMASTERHDR WHERE ");
fromClause.append(" MASTERDOCID LIKE '%' AND ORIGINTERMINAL = '");
fromClause.append(hawbAttributes.getTerminalID());
fromClause.append("' ");
fromClause.append(" AND HOUSEDOCID  IS NULL  AND MASTERDOCID NOT IN (SELECT HOUSENO FROM FS_FR_SEDSHIPMENTDTL WHERE ");
fromClause.append(" SUBMISSIONDATE IS NOT NULL )) A, FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, ");
fromClause.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");

whereClause.append("WHERE   HD.SHIPPERID = SHIPPER.CUSTOMERID(+)  AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+) AND");
whereClause.append(" A.HOUSEDOCID = HD.HOUSEDOCID(+) ");


//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

	}
	else if (hawbAttributes.getInvokerOperation().equalsIgnoreCase("AESSEDConfirm"))
	{
		//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 
    logger.info(FILE_NAME+"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 

//OLD QUERY
//SELECT HOUSENO  FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NULL AND HOUSENO LIKE '%' and TERMINALID='DHLHKG'|SELECT HOUSENO  FROM FS_FR_SEDSHIPMENTDTL WHERE SUBMISSIONDATE IS NULL AND HOUSENO LIKE '%' and TERMINALID='DHLHKG';
//ADVANCED QUERY
/*SELECT SD.HOUSENO,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID
,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME
FROM  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE , FS_FR_SEDSHIPMENTDTL SD 
WHERE  HD.HOUSEDOCID=SD.HOUSENO
AND  SHIPPER.CUSTOMERID(+) = HD.SHIPPERID AND CONSIGNEE.CUSTOMERID(+) = HD.CONSIGNEEID 
AND SUBMISSIONDATE IS NULL  AND SD.TERMINALID='DHLHKG';*/



selectClause.append(" SELECT SD.HOUSENO,SHIPPER.CUSTOMERID SHIPPERID,SHIPPER.COMPANYNAME SHIPPERNAME,");
selectClause.append(" CONSIGNEE.CUSTOMERID CONSIGNEEID ,CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,");
selectClause.append(" DESTTERMINAL,HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME ");


fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD, FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ,");
fromClause.append(" FS_FR_SEDSHIPMENTDTL SD ");

whereClause.append(" WHERE  HD.HOUSEDOCID=SD.HOUSENO AND  SHIPPER.CUSTOMERID(+) = HD.SHIPPERID AND ");
whereClause.append(" CONSIGNEE.CUSTOMERID(+) = HD.CONSIGNEEID AND SUBMISSIONDATE IS NULL  AND SD.TERMINALID='");
whereClause.append(hawbAttributes.getTerminalID());
whereClause.append("' ");


//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId().trim());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId().trim()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().trim().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName().trim()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().trim().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName().trim()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal().trim()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().trim().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal().trim()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId().trim()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().trim().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId().trim()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl().trim());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().trim().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");

	}
//FOR HAWBAttach
else if (hawbAttributes.getInvokerOperation().equals("HAWBAttach"))
	 {
//Logger.info(FILE_NAME,"..HAWB .SYS..ORIGIN Attach Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 
logger.info(FILE_NAME+"..HAWB .SYS..ORIGIN Attach Document ...conditions entered...@@@@@@@@@@@@@@@@@@@@@@@");	 
//OLD QUERY	 
//SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '%' AND ORIGINTERMINAL='DHLHKG' AND SHIPMENTMODE=1 ORDER BY HOUSEDOCID DESC;
//ADVANCED QUERY
/*SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,
CONSIGNEE.COMPANYNAME CONSIGNEENAME,ORIGINTERMINAL,DESTTERMINAL, HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME  
FROM  
 FS_FR_HOUSEDOCHDR HD,   
 FS_FR_CUSTOMERMASTER SHIPPER, 
 FS_FR_CUSTOMERMASTER CONSIGNEE 
WHERE 
 HD.SHIPPERID = SHIPPER.CUSTOMERID 
 AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID 
 AND ORIGINTERMINAL='DHLHKG' 
 AND SHIPMENTMODE=1 
ORDER BY 
 HOUSEDOCID DESC;
*/

    selectClause.append(" SELECT HD.HOUSEDOCID,SHIPPER.CUSTOMERID SHIPPERID,")
										.append(" SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID CONSIGNEEID,")
										.append(" CONSIGNEE.COMPANYNAME CONSIGNEENAME,HD.ORIGINTERMINAL,HD.DESTTERMINAL, ")
										.append(" HD.GROSSWEIGHT , HD.TOTPIECES ,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM  FS_FR_HOUSEDOCHDR HD,   FS_FR_CUSTOMERMASTER SHIPPER,")
									.append(" FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.ORIGINTERMINAL ='"+hawbAttributes.getTerminalID()+"' ")
									.append(" AND HD.SHIPMENTMODE =1  AND HD.SHIPPERID = SHIPPER.CUSTOMERID ")
									.append(" AND HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  ");

            
        	
		//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
        //    Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }
	
	else if (hawbAttributes.getInvokerOperation().equals("InvoiceShipperAirJobCost"))
	 {
//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
logger.info(FILE_NAME+"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
//OLD QUERY	 
//SELECT DISTINCT H.HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H,FS_FR_HOUSEDOCCHARGES C WHERE H.ORIGINTERMINAL = 'DHLMAA'  AND H.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Origin' AND H.HOUSEDOCID LIKE '%' AND   C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND H.HOUSEDOCID=C.HOUSEDOCID UNION  SELECT  DISTINCT MHDR.MASTERDOCID DOCID  FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR,FS_FR_HOUSEDOCCHARGES C WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.MASTERDOCID LIKE '%' AND MHDR.TERMINALID='DHLMAA' AND MHDR.MASTERTYPE='D' AND MHDR.MASTERDOCID=C.HOUSEDOCID;

//ADVANCED QUERY
/*
SELECT HD.HOUSEDOCID  ,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME   FROM(
SELECT DISTINCT HD.HOUSEDOCID ,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT ,  HD.TOTPIECES ,HD.ACTUALVOLUME,HD.ORIGINLOCATION ,HD.DESTINATIONLOCATION,HD.HOUSEDOCDATE FROM FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ,FS_FR_HOUSEDOCCHARGES C WHERE HD.ORIGINTERMINAL = 'DHLMAA'  AND  HD.SHIPPERID =  SHIPPER.CUSTOMERID  AND  HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  AND HD.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Origin' AND HD.HOUSEDOCID LIKE '%' AND   C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND HD.HOUSEDOCID=C.HOUSEDOCID 
UNION  
SELECT  DISTINCT MHDR.MASTERDOCID DOCID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, DMHDR.ORIGINTERMINAL,DMHDR.DESTTERMINAL,  DMHDR.GROSSWEIGHT,0 TOTPIECES,0 ACTUALVOLUME,DMHDR.ORIGINLOCATION ,DMHDR.DESTLOCATION,MHDR.MASTERDOCDATE  
FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR,FS_FR_HOUSEDOCCHARGES C,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.MASTERDOCID LIKE '%' AND MHDR.TERMINALID='DHLMAA' AND MHDR.MASTERTYPE='D' AND MHDR.MASTERDOCID=C.HOUSEDOCID AND  DMHDR.SHIPPERID =  SHIPPER.CUSTOMERID  AND  DMHDR.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  
)HD,
FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE   
WHERE   HD.SHIPPERID = SHIPPER.CUSTOMERID(+) 
AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)  
AND ROUND(SYSDATE- HD.HOUSEDOCDATE ) <= 15 ORDER BY HD.HOUSEDOCID DESC ;
*/

	selectClause.append(" SELECT HD.HOUSEDOCID  ,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM( SELECT DISTINCT HD.HOUSEDOCID ,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, HD.ORIGINTERMINAL,HD.DESTTERMINAL,  HD.GROSSWEIGHT ,  HD.TOTPIECES ,HD.ACTUALVOLUME,HD.ORIGINLOCATION ,HD.DESTINATIONLOCATION,HD.HOUSEDOCDATE FROM FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ,FS_FR_HOUSEDOCCHARGES C WHERE HD.ORIGINTERMINAL = '"+hawbAttributes.getTerminalID()+"'  AND  HD.SHIPPERID =  SHIPPER.CUSTOMERID  AND  HD.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  AND HD.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Origin' AND HD.HOUSEDOCID LIKE '%' AND   C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND HD.HOUSEDOCID=C.HOUSEDOCID UNION  SELECT  DISTINCT MHDR.MASTERDOCID DOCID,SHIPPER.CUSTOMERID SHIPPERID,  SHIPPER.COMPANYNAME SHIPPERNAME,CONSIGNEE.CUSTOMERID  CONSIGNEEID, CONSIGNEE.COMPANYNAME CONSIGNEENAME, DMHDR.ORIGINTERMINAL,DMHDR.DESTTERMINAL,  DMHDR.GROSSWEIGHT,0 TOTPIECES,0 ACTUALVOLUME,DMHDR.ORIGINLOCATION ,DMHDR.DESTLOCATION,MHDR.MASTERDOCDATE  FROM FS_FR_MASTERDOCHDR MHDR,FS_FR_DIRECTMASTERHDR DMHDR,FS_FR_HOUSEDOCCHARGES C,FS_FR_CUSTOMERMASTER  SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE  WHERE MHDR.MASTERDOCID = DMHDR.MASTERDOCID AND MHDR.MASTERDOCID LIKE '%' AND MHDR.TERMINALID='"+hawbAttributes.getTerminalID()+"' AND MHDR.MASTERTYPE='D' AND MHDR.MASTERDOCID=C.HOUSEDOCID AND  DMHDR.SHIPPERID =  SHIPPER.CUSTOMERID  AND  DMHDR.CONSIGNEEID  = CONSIGNEE.CUSTOMERID  )HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)  ");

    
            
        	
		//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY HD.HOUSEDOCID DESC ");
			
	 }
	 else if (hawbAttributes.getInvokerOperation().equals("InvoiceConsigneeAirJobCost"))
	 {
//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
logger.info(FILE_NAME+"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
//OLD QUERY	 
//SELECT DISTINCT H.HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H,FS_FR_HOUSEDOCCHARGES C WHERE H.DESTTERMINAL = 'DHLMAA'  AND H.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Destination' AND H.HOUSEDOCID LIKE '%'  AND  C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND H.HOUSEDOCID=C.HOUSEDOCID ORDER BY HOUSEDOCID;
//ADVANCED QUERY
/*
SELECT A.HOUSEDOCID , HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME , SHIPPER.CUSTOMERID SHIPPERID  , SHIPPER.COMPANYNAME SHIPPERNAME ,  CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME FROM(


SELECT DISTINCT H.HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H,FS_FR_HOUSEDOCCHARGES C WHERE H.DESTTERMINAL = 'DHLMAA'  AND H.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Destination' AND H.HOUSEDOCID LIKE '%'  AND  C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND H.HOUSEDOCID=C.HOUSEDOCID ORDER BY HOUSEDOCID

)A,
 
FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE  A.HOUSEDOCID = HD.HOUSEDOCID(+) AND HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+);

*/

selectClause.append(" SELECT A.HOUSEDOCID ,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
fromClause.append(" FROM( SELECT DISTINCT H.HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H,FS_FR_HOUSEDOCCHARGES C WHERE H.DESTTERMINAL = '"+hawbAttributes.getTerminalID()+"'  AND H.SHIPMENTMODE=1  AND C.COSTINCURREDAT='Destination' AND H.HOUSEDOCID LIKE '%'  AND  C.CHARGEID NOT IN('1','2','3','4','5','6','7','8','9','10') AND H.HOUSEDOCID=C.HOUSEDOCID ORDER BY HOUSEDOCID )A, FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
whereClause.append(" WHERE  A.HOUSEDOCID = HD.HOUSEDOCID(+) AND HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)  ");
    
            
        	
		//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
          //  Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
            
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY A.HOUSEDOCID DESC ");
			
	 }
	 else if (hawbAttributes.getInvokerOperation().equals("InvoiceConsigneeAirCAN"))
	 {
//Logger.info(FILE_NAME,"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
logger.info(FILE_NAME+"...@@@@@@@@@@.ETHAWBAdvDAO...@@@@@@@@@@@@--entered :"+hawbAttributes.getInvokerOperation()); 	 
//OLD QUERY	 
//SELECT HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B WHERE   HAWB_ID LIKE '%' AND A.RT_PLAN_ID = B.RT_PLAN_ID AND A.SHPMNT_STATUS ! = 'D' AND B.MSTER_DOC_ID  IN (SELECT M.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL  D WHERE	M.MASTERDOCID  = D.MASTERDOCID AND M.SHIPMENTMODE =' 1'  AND M.DESTINATIONGATEWAYID = 'DHLMAA' AND  M.MASTERTYPE IS NULL AND  M.RECEPIETSTATUS IN ('P','Full') AND  D.SLNO = (SELECT MAX(SLNO) FROM   FS_FR_MASTERFLIGHTDTL DTL WHERE 	DTL.MASTERDOCID = M.MASTERDOCID)) 
//ADVANCED QUERY
/*SELECT A.HAWB_ID ,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME FROM(
SELECT HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B WHERE   HAWB_ID LIKE '%' AND A.RT_PLAN_ID = B.RT_PLAN_ID AND A.SHPMNT_STATUS ! = 'D' AND B.MSTER_DOC_ID  IN (SELECT M.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL  D WHERE	M.MASTERDOCID  = D.MASTERDOCID AND M.SHIPMENTMODE =' 1'  AND M.DESTINATIONGATEWAYID = 'DHLMAA' AND  M.MASTERTYPE IS NULL AND  M.RECEPIETSTATUS IN ('P','Full') AND  D.SLNO = (SELECT MAX(SLNO) FROM   FS_FR_MASTERFLIGHTDTL DTL WHERE 	DTL.MASTERDOCID = M.MASTERDOCID)) 
)A,
 
FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE WHERE  A.HAWB_ID   = HD.HOUSEDOCID(+) AND HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+);

*/

	selectClause.append(" SELECT A.HAWB_ID ,SHIPPER.CUSTOMERID SHIPPERID, SHIPPER.COMPANYNAME SHIPPERNAME ,CONSIGNEE.CUSTOMERID CONSIGNEEID ,  CONSIGNEE.COMPANYNAME  CONSIGNEENAME, HD.ORIGINTERMINAL , HD.DESTTERMINAL , HD.GROSSWEIGHT, HD.TOTPIECES,HD.ACTUALVOLUME "); 
			
	fromClause.append(" FROM(SELECT HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B WHERE   HAWB_ID LIKE '%' AND A.RT_PLAN_ID = B.RT_PLAN_ID AND A.SHPMNT_STATUS ! = 'D' AND B.MSTER_DOC_ID  IN (SELECT M.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL  D WHERE	M.MASTERDOCID  = D.MASTERDOCID AND M.SHIPMENTMODE =' 1'  AND M.DESTINATIONGATEWAYID = '"+hawbAttributes.getTerminalID()+"' AND  M.MASTERTYPE IS NULL AND  M.RECEPIETSTATUS IN ('P','Full') AND  D.SLNO = (SELECT MAX(SLNO) FROM   FS_FR_MASTERFLIGHTDTL DTL WHERE 	DTL.MASTERDOCID = M.MASTERDOCID)) )A,FS_FR_HOUSEDOCHDR HD,FS_FR_CUSTOMERMASTER SHIPPER, FS_FR_CUSTOMERMASTER CONSIGNEE ");
			
	whereClause.append(" WHERE  A.HAWB_ID   = HD.HOUSEDOCID(+) AND HD.SHIPPERID = SHIPPER.CUSTOMERID(+) AND HD.CONSIGNEEID = CONSIGNEE.CUSTOMERID(+)  ");
    
            
        	
		//	Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........hawbId..........."+hawbAttributes.getHawbId());
			if(hawbAttributes.getHawbId()!=null && !hawbAttributes.getHawbId().equals(""))
		         { 
				    whereClause.append(" AND HD.HOUSEDOCID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getHawbId()));
				 }
          
			if(hawbAttributes.getShipperName()!=null && !hawbAttributes.getShipperName().equals(""))
		         { 
				    whereClause.append(" AND SHIPPER.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getShipperName()));
				 }
		 
            if(hawbAttributes.getConsigneeName()!=null && !hawbAttributes.getConsigneeName().equals(""))
		         { 
				    whereClause.append(" AND CONSIGNEE.COMPANYNAME ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getConsigneeName()));
				 }
            if(hawbAttributes.getOriginTerminal()!=null && !hawbAttributes.getOriginTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.ORIGINTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginTerminal()));
				 }
             if(hawbAttributes.getDestinationTerminal()!=null && !hawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    whereClause.append(" AND HD.DESTTERMINAL ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationTerminal()));
				 }
             if(hawbAttributes.getOriginCountryId()!=null && !hawbAttributes.getOriginCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER ORCOUNTRY  ");
					whereClause.append(" AND ORCOUNTRY.LOCATIONID = HD.ORIGINLOCATION "); 
					whereClause.append(" AND ORCOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getOriginCountryId()));
				 }
             if(hawbAttributes.getDestinationCountryId()!=null && !hawbAttributes.getDestinationCountryId().equals(""))
		         { 
				    fromClause.append(", FS_FR_LOCATIONMASTER DECOUNTRY  ");
					whereClause.append(" AND DECOUNTRY.LOCATIONID = HD.DESTINATIONLOCATION "); 
					whereClause.append(" AND DECOUNTRY.COUNTRYID ");
					whereClause.append(getSearchString(searchValues.getSearchType(),hawbAttributes.getDestinationCountryId()));
				 }
         //   Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........searchValues.getNoOfDaysControl()..........."+searchValues.getNoOfDaysControl());
           
			if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		         {
				     searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays()," HD.HOUSEDOCDATE ");
    				 whereClause.append(searchString);
		         }
              
           	resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			resultQuery.append(" ORDER BY A.HAWB_ID DESC ");
			
	 }
	 
                 	
		//	Logger.info(FILE_NAME,"..End of the conditions....ETHAWBAdvDAO....@@@@@@@@@@....."+resultQuery.toString());
			pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

            //Logger.info(FILE_NAME,"...............ETHAWBAdvDAO........44...........");
            logger.info(FILE_NAME+"...............ETHAWBAdvDAO........44...........");
			while(rs.next()){
			//Logger.info(FILE_NAME,"...............ETHAWBAdvDAO.....55..............");//GS
			  
			  ETHAWBAdvVO hawbAttributesArr =new ETHAWBAdvVO();
			  hawbAttributesArr.setHawbId(rs.getString(1));
			  hawbAttributesArr.setShipperId(rs.getString(2));
			  hawbAttributesArr.setShipperName(rs.getString(3));
			  hawbAttributesArr.setConsigneeId(rs.getString(4));
			  hawbAttributesArr.setConsigneeName(rs.getString(5));
			  hawbAttributesArr.setOriginTerminal(rs.getString(6));
			  hawbAttributesArr.setDestinationTerminal(rs.getString(7));
			  hawbAttributesArr.setActualWeight(rs.getString(8)); 
			  hawbAttributesArr.setNoOfPacks(rs.getString(9)); 
			  hawbAttributesArr.setVolume(rs.getString(10)); 
				
				details.add(hawbAttributesArr);

			}	
			return details;
			}
       catch(Exception e){
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(pStmt!=null){
					pStmt.close();
				}
				if(connection!=null){
					connection.close();
				}
			}catch(SQLException sq){
				throw new EJBException(sq.getMessage());
			}

	}
}
  /**
   * 
   * @return 
   * @throws java.sql.SQLException
   */
private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
  
}
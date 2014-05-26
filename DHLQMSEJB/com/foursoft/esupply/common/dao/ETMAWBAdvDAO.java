/*<%--
 %
 % Copyright (c) 1999-2005 by FourSoft,Pvt Ltd.Reserved.
 % This software is the proprietary information of FourSoft Ltd.
 % Use is subject to license terms.
 % 
 % esupply - v 1.8
 %
--%>

<%--
% File		    :   ETMAWBAdvDAO.java
% Sub-Module    :   MAWB - Advanced LOV Search. 
% Module        :   ETrans
%
% This is the DAO  for the LOV of the MAWB Ids based on Different Parameters
% 
% Author        :   G.Srinivas 
% Date 			:   15/03/2005
% Modified date :   15/03/2005
--%>*/

package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETMAWBAdvVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;

/**
 * 
 */
public class ETMAWBAdvDAO extends ETAdvancedLOVMasterDAO
{
  public static String FILE_NAME = "ETMAWBAdvDAO.java";
  private static Logger logger = null;

  public ETMAWBAdvDAO()
  {
    logger  = Logger.getLogger(ETMAWBAdvDAO.class);
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
       ETMAWBAdvVO mawbAttributes =null;
	   String searchString =null;
       StringBuffer whereClause = null;
       StringBuffer fromClause  = null;
	   StringBuffer selectClause  = null;
       mawbAttributes = (ETMAWBAdvVO)searchValues;
       String terminalId = mawbAttributes.getTerminalId()!=null?mawbAttributes.getTerminalId():"";
	   String operation = mawbAttributes.getOperation()!=null?mawbAttributes.getOperation():"";
	   		
	   
     try {
     
			connection = this.getConnection();
  		
			selectClause	= new StringBuffer(" SELECT MAWB.MASTERDOCID,MAWB.CARRIERID ,MAWB.ORIGINGATEWAYID,MAWB.DESTINATIONGATEWAYID, F.FLIGHTFROM ,F.FLIGHTTO,MAWB.CHARGEABLEWEIGHT,MAWB.MAWB_BLCKD_WT ,F.ETD ,F.ETA  "); 
			
			fromClause		= new StringBuffer(" FROM FS_FR_MASTERDOCHDR MAWB,FS_FR_MASTERFLIGHTDTL F ");
		if(operation!=null && !("").equals(operation))
		 {
			if("CloseMaster".equalsIgnoreCase(operation) || "Delete".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1  AND MAWB.STATUS NOT IN('CLOSED','COB') AND MAWB.MASTERTYPE IS NULL AND MAWB.SUB_AGENT_ID IS NULL ");
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID DESC ");
			 }
			else if("COB".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1  AND MAWB.STATUS ='CLOSED' AND RECEPIETSTATUS IS NULL  AND  MAWB.MASTERTYPE IS NULL AND MAWB.SUB_AGENT_ID IS NULL "); 
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID DESC ");
			 }
			else if("Modify".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1  AND MAWB.STATUS !='COB' AND RECEPIETSTATUS IS NULL  AND  MAWB.MASTERTYPE IS NULL AND MAWB.SUB_AGENT_ID IS NULL ");
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID DESC ");
			 }
			else if("View".equalsIgnoreCase(operation)||"PrintLabels".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1 AND  MAWB.MASTERTYPE IS NULL AND MAWB.SUB_AGENT_ID IS NULL "); 
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID DESC ");
			 }	
			else if("MasterViewReport".equalsIgnoreCase(operation)||"MasterPrintReport".equalsIgnoreCase(operation) || 	"CargoManifest".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'   AND MAWB.ORIGINGATEWAYID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1 AND  MAWB.MASTERTYPE IS NULL  "); 
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID DESC ");

			 }	
			else if("MasterCostAdd".equalsIgnoreCase(operation)) 
			 {
				 selectClause	= new StringBuffer(" SELECT DISTINCT MAWB.MASTERDOCID,MAWB.CARRIERID ,MAWB.ORIGINGATEWAYID,MAWB.DESTINATIONGATEWAYID, F.FLIGHTFROM ,F.FLIGHTTO,MAWB.CHARGEABLEWEIGHT,MAWB.MAWB_BLCKD_WT ,F.ETD ,F.ETA  "); 
				 fromClause		= new StringBuffer(" FROM FS_FR_MASTERDOCHDR MAWB,FS_FR_MASTERDOCCHARGES C, FS_FR_GATEWAYMASTER GATE, FS_FR_CURRENCYMASTER CUR,FS_FR_MASTERFLIGHTDTL F ");

				 whereClause	= new StringBuffer(" WHERE C.MASTERDOCID = MAWB.MASTERDOCID AND  MAWB.SHIPMENTMODE	= 1 AND MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND GATE.GATEWAYID(+) = MAWB.DESTINATIONGATEWAYID AND CUR.CURRENCY1='GBP' AND CUR.CURRENCY2(+) = MAWB.CURRENCYID AND ( CUR.IEFLAG = 'E' OR CUR.IEFLAG IS NULL)  AND (DESTINATIONGATEWAYID IS NULL OR  MAWB.DESTINATIONGATEWAYID !='"+terminalId+"') AND  MAWB.ACCOUNTSTATUS IS NULL AND MAWB.STATUS IN ('CLOSED','COB')  ");
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCID  DESC ");
			 } 
			else if("ModifyMasterCost".equalsIgnoreCase(operation)) 
			 {
				whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'   AND (MAWB.DESTINATIONGATEWAYID IS NULL OR MAWB.DESTINATIONGATEWAYID !='"+terminalId+"') AND  MAWB.ACCOUNTSTATUS='U' AND MAWB.SHIPMENTMODE=1 ");           
				whereClause.append(addFilter(mawbAttributes));
				whereClause.append(" UNION ");
				whereClause.append(selectClause);
				whereClause.append(fromClause);
				whereClause.append(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'  AND (MAWB.DESTINATIONGATEWAYID IS NULL OR MAWB.DESTINATIONGATEWAYID !='"+terminalId+"') AND  MAWB.ACCOUNTSTATUS='U' AND MAWB.SHIPMENTMODE=1  AND MAWB.MASTERTYPE='D' AND  MAWB.STATUS='CLOSED'  ");  
				whereClause.append(addFilter(mawbAttributes));
			 }
			else if("UpdateAccounts".equalsIgnoreCase(operation)) 
			 {
				whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'   AND (MAWB.DESTINATIONGATEWAYID IS NULL OR MAWB.DESTINATIONGATEWAYID !='"+terminalId+"')  AND  ( (MAWB.STATUS = 'COB') OR (MAWB.STATUS='CLOSED' AND MAWB.RECEPIETSTATUS IN('Full','P','Deli')) ) AND  MAWB.ACCOUNTSTATUS='U' AND MAWB.SHIPMENTMODE=1 ");           
				whereClause.append(addFilter(mawbAttributes));
				whereClause.append(" UNION ");
				whereClause.append(selectClause);
				whereClause.append(fromClause);
				whereClause.append(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'  AND (MAWB.DESTINATIONGATEWAYID IS NULL OR MAWB.DESTINATIONGATEWAYID !='"+terminalId+"') AND  MAWB.ACCOUNTSTATUS='U' AND MAWB.SHIPMENTMODE=1  AND MAWB.MASTERTYPE='D' AND  MAWB.STATUS='CLOSED'  ");  
				whereClause.append(addFilter(mawbAttributes));
				whereClause.append(" UNION ");
				whereClause.append(selectClause);
				whereClause.append(fromClause);
				whereClause.append(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID='"+terminalId+"'  AND (MAWB.DESTINATIONGATEWAYID IS NULL OR MAWB.DESTINATIONGATEWAYID !='"+terminalId+"') AND  MAWB.ACCOUNTSTATUS='U' AND MAWB.SHIPMENTMODE=1  AND MAWB.MASTERTYPE IS NULL AND  SUB_AGENT_ID IS NOT NULL  ");  
				whereClause.append(addFilter(mawbAttributes));
			 }
			else if("MasterCost".equalsIgnoreCase(operation)) 
			 {
				 whereClause	= new StringBuffer(" WHERE MAWB.MASTERDOCID=F.MASTERDOCID(+)  AND MAWB.TERMINALID ='"+terminalId+"' AND MAWB.DESTINATIONGATEWAYID !='"+terminalId+"' AND MAWB.SHIPMENTMODE=1  AND ACCOUNTSTATUS IS NOT NULL AND MAWB.MASTERTYPE IS NULL AND MAWB.SUB_AGENT_ID IS NULL  ");
				 whereClause.append(addFilter(mawbAttributes));
				 whereClause.append(" ORDER BY MAWB.MASTERDOCDATE  DESC ");
			 } 
		 }	
			//Logger.info(FILE_NAME,"...............ETMAWBAdvDAO........mawbId..........."+mawbAttributes.getMawbId());
		
	 
               
            StringBuffer resultQuery= new StringBuffer();
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			
			
           // Logger.info(FILE_NAME,"...............ETMAWBAdvDAO........11..........."+resultQuery.toString());     	
			pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

          //  Logger.info(FILE_NAME,"...............ETMAWBAdvDAO........44...........");
			while(rs.next()){
			//Logger.info(FILE_NAME,"...............ETMAWBAdvDAO.....55..............");
			  
			  ETMAWBAdvVO mawbAttributesArr =new ETMAWBAdvVO();
			  mawbAttributesArr.setMawbId(rs.getString(1));
			  mawbAttributesArr.setCarrierId(rs.getString(2));
			  mawbAttributesArr.setOriginGatewayId(rs.getString(3));
			  mawbAttributesArr.setDestinationGatewayId(rs.getString(4));
			  mawbAttributesArr.setOriginTerminal(rs.getString(5));
			  mawbAttributesArr.setDestinationTerminal(rs.getString(6));
			  mawbAttributesArr.setChargeableWeight(rs.getString(7));
			  mawbAttributesArr.setBlockedSpace(rs.getString(8)); 
			  mawbAttributesArr.setETD(rs.getString(9)); 
			  mawbAttributesArr.setETA(rs.getString(10)); 
				
				details.add(mawbAttributesArr);

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
 
private String getWeightControl(String chargableWeightControl, String chargableWeight,String entityDateField)throws javax.ejb.EJBException
	{
		StringBuffer weightCondition=new StringBuffer();
		try
		{
			if(chargableWeightControl.equals("lessOrEqualTo"))
			{  
			     weightCondition.append(entityDateField);
				 weightCondition.append(" <= ");
				 weightCondition.append(chargableWeight);
			}
			else if(chargableWeightControl.equals("equalTo"))
			{
				 weightCondition.append(entityDateField);
				 weightCondition.append(" = ");
				 weightCondition.append(chargableWeight);
                 
			}
            else if(chargableWeightControl.equals("greaterOrEqualTo"))
			{
				 weightCondition.append(entityDateField);
				 weightCondition.append(" >= ");
				 weightCondition.append(chargableWeight);
				 
			}       
			
		}

		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create ",ex.toString(),ex);
      logger.error(FILE_NAME+"create "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return weightCondition.toString();
	} 

private String addFilter(ETMAWBAdvVO mawbAttributes)
{	
		StringBuffer searchString = new StringBuffer();
		try
		{
			//Logger.info(FILE_NAME,"...........addFilter..........."+mawbAttributes.getNoOfDays());
			if(mawbAttributes.getMawbId()!=null && !mawbAttributes.getMawbId().equals(""))
		         { 
				    searchString.append(" AND MAWB.MASTERDOCID ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getMawbId()));
				 }
            if(mawbAttributes.getOriginGatewayId()!=null && !mawbAttributes.getOriginGatewayId().equals(""))
		         { 
				    searchString.append(" AND MAWB.ORIGINGATEWAYID ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getOriginGatewayId()));
				 }
            if(mawbAttributes.getDestinationGatewayId()!=null && !mawbAttributes.getDestinationGatewayId().equals(""))
		         { 
				    searchString.append(" AND MAWB.DESTINATIONGATEWAYID ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getDestinationGatewayId()));
				 }
			if(mawbAttributes.getOriginTerminal()!=null && !mawbAttributes.getOriginTerminal().equals(""))
		         { 
				    searchString.append(" AND F.FLIGHTFROM ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getOriginTerminal()));
				 }
            if(mawbAttributes.getDestinationTerminal()!=null && !mawbAttributes.getDestinationTerminal().equals(""))
		         { 
				    searchString.append(" AND F.FLIGHTTO ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getDestinationTerminal()));
				 }
			if(mawbAttributes.getCarrierId()!=null && !mawbAttributes.getCarrierId().equals(""))
		         { 
				    searchString.append(" AND MAWB.CARRIERID ");
                    searchString.append(getSearchString(mawbAttributes.getSearchType(),mawbAttributes.getCarrierId()));
				 }

//@@ G.Srinivas modified on 20050401 (LOV Advanced Search ) FOR QA ISSUE NO:5173-5184

			//if(mawbAttributes.getChargeableWeightControl()!=null && !mawbAttributes.getChargeableWeightControl().equals(""))
			if(mawbAttributes.getChargeableWeight()!=null && !mawbAttributes.getChargeableWeight().equals(""))
		         {
				     searchString.append(getWeightControl(mawbAttributes.getChargeableWeightControl(),mawbAttributes.getChargeableWeight()," AND MAWB.CHARGEABLEWEIGHT "));
		         }

//@@ G.Srinivas modified on 20050401 (LOV Advanced Search ) FOR QA ISSUE NO:5173-5184
			//if(mawbAttributes.getBlockedSpaceControl()!=null && !mawbAttributes.getBlockedSpaceControl().equals(""))
			if(mawbAttributes.getBlockedSpace()!=null && !mawbAttributes.getBlockedSpace().equals(""))
		         {
				     searchString.append(getWeightControl(mawbAttributes.getBlockedSpaceControl(),mawbAttributes.getBlockedSpace()," AND MAWB.MAWB_BLCKD_WT "));
		         }          
			//Logger.info(FILE_NAME,"...........bAttributes.getNoOfDaysControl()..........."+mawbAttributes.getNoOfDaysControl());
			if(mawbAttributes.getNoOfDaysControl()!=null && !mawbAttributes.getNoOfDaysControl().equals(""))
		         {
				     searchString.append(getDaysControl(mawbAttributes.getNoOfDaysControl(),mawbAttributes.getNoOfDays()," MAWB.MASTERDOCDATE "));    				
		         }
		} // @@ end of try
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"in addFilter() ",ex.toString(),ex);
      logger.error(FILE_NAME+"in addFilter() "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return searchString.toString();

	}// @@ end of addFilter()

}
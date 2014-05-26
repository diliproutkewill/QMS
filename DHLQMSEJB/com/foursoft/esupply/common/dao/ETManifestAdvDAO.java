package com.foursoft.esupply.common.dao;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;

import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETManifestAdvVO;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


public class ETManifestAdvDAO extends ETAdvancedLOVMasterDAO 
{
	public static String FILE_NAME = "ETManifestAdvDAO.java";
  private static Logger logger = null;

	public ETManifestAdvDAO(){
    logger  = Logger.getLogger(ETManifestAdvDAO.class);
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
		ETManifestAdvVO manifestAttributes =null;
		manifestAttributes = (ETManifestAdvVO)searchValues;

		selectClause = new StringBuffer("SELECT DISTINCT MDH.MASTERDOCID MANIFESTID,")
										.append("RP.SHIPPER_ID,")
										.append("SHIPPER.COMPANYNAME SHIPPERNAME,")
										.append("RP.CONSIGNEE_ID,")
										.append("CONSIGNEE.COMPANYNAME CONSIGNEENAME,")
										.append("RP.ORIG_LOC_ID ORIGINLOCATION,")
										.append("RP.DEST_LOC_ID DESTLOCATION,")
										.append("MDH.ORIGINGATEWAYID ORIGINTERMINAL,")
										.append("MDH.DESTINATIONGATEWAYID DESTNATIONTERMINAL,")
										.append("MDH.SERVICELEVEL,")
										.append("MDH.CHARGEABLEWEIGHT GROSSWEIGHT,")
										.append("MDH.PIECES NOOPACKS,")
										.append("MDH.MASTERDOCDATE ");                    

		fromClause =new StringBuffer(" FROM FS_FR_MASTERDOCHDR MDH,")
										.append("FS_RT_LEG LEG,")
										.append("FS_RT_PLAN RP,")
										.append("FS_FR_CUSTOMERMASTER SHIPPER,")
										.append("FS_FR_CUSTOMERMASTER CONSIGNEE");

		whereClause =new StringBuffer(" WHERE RP.CONSIGNEE_ID = CONSIGNEE.CUSTOMERID(+) AND ") 
										.append("RP.SHIPPER_ID = SHIPPER.CUSTOMERID(+) AND ")
										.append("LEG.MSTER_DOC_ID(+) = MDH.MASTERDOCID AND ")
										.append("RP.RT_PLAN_ID(+) = LEG.RT_PLAN_ID ");

		if((manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLModify")) || (manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLDelete")) || (manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLClose")))
		{
			whereClause.append("AND MDH.STATUS!='CLOSED' ");
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'F' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLView"))
		{
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'F' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLConfirm2Acs"))
		{
			whereClause.append("AND MDH.STATUS ='CLOSED' ");
			whereClause.append("AND MDH.ACCOUNTSTATUS = 'U' ");
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'F' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("FTLUpdate"))
		{
			fromClause.append(", FS_FR_DIRECTMASTERHDR DMHDR ");
			whereClause.append("AND MDH.MASTERDOCID = DMHDR.MASTERDOCID ");
			whereClause.append("AND DMHDR.PODID IS NULL ");
			whereClause.append("AND DMHDR.HOUSEDOCID IS NULL ");
			whereClause.append("AND MDH.STATUS ='CLOSED' ");
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'F' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TerModify"))
		{
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'T' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TerView"))
		{
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'T' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TerClose"))
		{
			whereClause.append("AND MDH.STATUS !='CLOSED' ");
			whereClause.append("AND MDH.SHIPMENTMODE IN(4,5,6,7)  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'T' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TranModify"))
		{
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'R' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TranView"))
		{
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'R' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("TranClose"))
		{
			whereClause.append("AND MDH.STATUS !='CLOSED' ");
			whereClause.append("AND MDH.SHIPMENTMODE IN(4,5,6,7)  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'R' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("DirModify"))
		{
			whereClause.append("AND MDH.ASSIGNTOTRIPSHEET='N' ");				
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'D' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("DirView"))
		{
			whereClause.append("AND MDH.SHIPMENTMODE=4  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'D' ");		
		}
		if(manifestAttributes.getInvokerOperation().equalsIgnoreCase("DirClose"))
		{
			whereClause.append("AND MDH.STATUS !='CLOSED' ");
			whereClause.append("AND MDH.SHIPMENTMODE IN(4,5,6,7)  ");	
			whereClause.append("AND MDH.MASTERTYPE = 'D' ");		
		}
		if(manifestAttributes.getTerminalID()!=null && !manifestAttributes.getTerminalID().equals(""))
		{ 
			whereClause.append("AND MDH.TERMINALID ");
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getTerminalID().trim()));
		}				
		if(manifestAttributes.getManifestId()!=null && !manifestAttributes.getManifestId().equals(""))
		{ 
			whereClause.append("AND MDH.MASTERDOCID ");
			whereClause.append(getSearchString(searchValues.getSearchType(), manifestAttributes.getManifestId().trim()));
		}
		if(manifestAttributes.getShipperName()!=null && !manifestAttributes.getShipperName().equals(""))
		{ 
			whereClause.append("AND SHIPPER.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getShipperName().trim()));
		}
		if(manifestAttributes.getConsigneeName()!=null && !manifestAttributes.getConsigneeName().equals(""))
		{ 
			whereClause.append("AND CONSIGNEE.COMPANYNAME ");
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getConsigneeName().trim()));
		}
		if(manifestAttributes.getOriginLocation()!=null && !manifestAttributes.getOriginLocation().equals(""))
		{ 
			whereClause.append("AND RP.ORIG_LOC_ID ");
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getOriginLocation().trim()));
		}
		if(manifestAttributes.getDestinationLocation()!=null && !manifestAttributes.getDestinationLocation().equals(""))
		{ 
			whereClause.append("AND RP.DEST_LOC_ID ");				
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getDestinationLocation().trim()));
		}
		if(manifestAttributes.getOriginTerminal()!=null && !manifestAttributes.getOriginTerminal().equals(""))
		{ 
			whereClause.append("AND MDH.ORIGINGATEWAYID ");
			whereClause.append(searchString =getSearchString(searchValues.getSearchType(),manifestAttributes.getOriginTerminal().trim()));
		}
		if(manifestAttributes.getDestinationTerminal()!=null && !manifestAttributes.getDestinationTerminal().equals(""))
		{ 
			whereClause.append("AND MDH.DESTINATIONGATEWAYID "); 	
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getDestinationTerminal().trim()));
		}
		if(manifestAttributes.getServiceLevel()!=null && !manifestAttributes.getServiceLevel().equals(""))
		{ 
			whereClause.append("AND MDH.SERVICELEVEL "); 	
			whereClause.append(getSearchString(searchValues.getSearchType(),manifestAttributes.getServiceLevel().trim()));
		}			
		if(searchValues.getNoOfDaysControl()!=null && !searchValues.getNoOfDaysControl().equals(""))
		{
			searchString =getDaysControl(searchValues.getNoOfDaysControl(),searchValues.getNoOfDays().trim(),"MDH.MASTERDOCDATE");
			whereClause.append(searchString);
		}

		resultQuery= new StringBuffer();
		resultQuery.append(selectClause);
		resultQuery.append(fromClause);
		resultQuery.append(whereClause);
		resultQuery.append(" ORDER BY MDH.MASTERDOCDATE DESC ");
		
		try 
		{     	
			connection = this.getConnection();
			pStmt = connection.prepareStatement(resultQuery.toString());
            rs = pStmt.executeQuery();

			while(rs.next())
			{
				ETManifestAdvVO manifestAttributesArr =new ETManifestAdvVO();
				manifestAttributesArr.setManifestId(rs.getString(1));
				manifestAttributesArr.setShipperId(rs.getString(2));
				manifestAttributesArr.setShipperName(rs.getString(3));
				manifestAttributesArr.setConsigneeId(rs.getString(4));
				manifestAttributesArr.setConsigneeName(rs.getString(5));
				manifestAttributesArr.setOriginLocation(rs.getString(6));
				manifestAttributesArr.setDestinationLocation(rs.getString(7));
				manifestAttributesArr.setOriginTerminal(rs.getString(8)); 
				manifestAttributesArr.setDestinationTerminal(rs.getString(9)); 
				manifestAttributesArr.setServiceLevel(rs.getString(10)); 
				manifestAttributesArr.setGrossWeight(rs.getString(11)); 
				manifestAttributesArr.setNoOfPacks(rs.getString(12)); 
				details.add(manifestAttributesArr);
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
}
/*
 * Copyright ©.  
 */
package com.foursoft.etrans.sea.common.dao;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import javax.naming.InitialContext;
import com.foursoft.esupply.common.util.ConnectionUtil;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


/**
 * @author  prasadrlv
 * @version 1.6
 */
public class IsExistsDAO 
{
	public static final String FILE_NAME="IsExistsDAO.java";
	private static javax.sql.DataSource dataSource	=	null;
  private static Logger logger = null;
	
  public IsExistsDAO()
  {
    logger  = Logger.getLogger(IsExistsDAO.class);
  }
  
  /**
   * 
   * @param con
   * @param qry
   * @return boolean
   * @throws java.sql.SQLException
   */
	private static boolean isIdExists(Connection con,String qry)throws SQLException
	{
		boolean 		result	= false;
		
		PreparedStatement ps	= null;
		ResultSet		rs	= null;
		try
		{
			ps=con.prepareStatement(qry);
			rs=ps.executeQuery();
			if(rs.next())
			{
				result=true;
			}
		}catch(Exception ex)
		{
			//Logger.error(FILE_NAME," isIdExists() "+ex);
      logger.error(FILE_NAME+" isIdExists() "+ex);
			//Logger.info(FILE_NAME,"Query :"+qry);
			throw new SQLException("Error while checking in Id "+ex);
		}
		finally
		{
			//closeStatement(rs,ps);
      ConnectionUtil.closeConnection(con,ps,rs);
		}
		
		return result;
	}
	
  /**
   * 
   * @param con
   * @param serviceLevelId
   * @param shipmentMode
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isServiceLevelExists(Connection con ,String serviceLevelId,String shipmentMode)throws SQLException
	{
		if(shipmentMode.equals("Air"))
		{
			shipmentMode="1,3,5,7";
		}else if(shipmentMode.equals("Sea"))
		{
			shipmentMode="2,3,6,7";
		}else if(shipmentMode.equals("Truck"))
		{
			shipmentMode="4,5,6,7";
		}
		
		String 		sql	= "SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID ='"+serviceLevelId+"' AND SHIPMENTMODE IN  ("+shipmentMode+") ";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param carrierId
   * @param shipmentMode
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isCarrierIdExists(Connection con,String carrierId,String shipmentMode)throws SQLException
	{
		if(shipmentMode.equals("Air"))
		{
			shipmentMode="1,3,5,7";
		}else if(shipmentMode.equals("Sea"))
		{
			shipmentMode="2,3,6,7";
		}else if(shipmentMode.equals("Truck"))
		{
			shipmentMode="4,5,6,7";
		}
		String 		sql	= "SELECT CARRIERID FROM FS_FR_CAMASTER WHERE CARRIERID='"+carrierId+"' AND SHIPMENTMODE IN  ("+shipmentMode+")" ;
		return isIdExists(con,sql);
	}

  /**
   * 
   * @param con
   * @param currencyId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isCurrencyIdExists(Connection con ,String currencyId)throws SQLException
	{
		String 		sql	= "SELECT DISTINCT CURRENCYID  FROM FS_COUNTRYMASTER WHERE CURRENCYID='"+currencyId+"'" ;
		
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param locationId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isLocationIdExists(Connection con,String locationId)throws SQLException
	{
		String 		sql	= "SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID='"+locationId+"' ";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param locationId
   * @param terminalId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isLocationBelongsToTerminal(Connection con,String locationId,String terminalId)throws SQLException
	{
		String 		sql	= "SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID='"+locationId+"' AND TERMINALID ='"+terminalId+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param terminalId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isTerminalIdExists(Connection con ,String terminalId)throws SQLException
	{
		String 		sql	= "SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+terminalId+"'" ;
		boolean  conCreated=false;
		boolean result=false;
		if(con==null)
		{
			con=getConnection();
			 conCreated=true;
		}
		result=isIdExists(con,sql);
		if(conCreated)
		{
			con.close();
		}
		return result;
	}
  /**
   * 
   * @param con
   * @param terminalId
   * @param gatewayId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isTerminalBelongsToGateway(Connection con ,String terminalId,String gatewayId)throws SQLException
	{
		String 		sql	= "SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+terminalId+"' AND GATEWAYID='"+gatewayId+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param gatewayId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isGatewayIdExists(Connection con,String gatewayId)throws SQLException
	{
		String 		sql	= "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID='"+gatewayId+"' ";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param customerId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isCustomerIdExists(Connection con,String customerId)throws SQLException
	{
		String 		sql	= "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"' AND CUSTOMERTYPE = 'Customer' ";
		return isIdExists(con,sql);
	}

  /**
   * 
   * @param con
   * @param customerId
   * @param terminalId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isCustomerBelongsToTerminal(Connection con,String customerId,String terminalId)throws SQLException
	{
		String 		sql	= "SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID='"+customerId+"' AND TERMINALID='"+terminalId+"' AND CUSTOMERTYPE = 'Customer' ";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param portId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isPortIdExists(Connection con,String portId)throws SQLException
	{
		String 		sql	= "SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID='"+portId+"' ";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param routeId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public boolean isRouteIdExists(Connection con, String routeId) throws SQLException
	{
		String sql="SELECT ROUTEID FROM FS_FRS_ROUTEMASTER  WHERE ROUTEID='"+routeId+"'";
		return isIdExists(con,sql);
	}

  /**
   * 
   * @param con
   * @param routeId
   * @param originTerminal
   * @return boolean
   * @throws java.sql.SQLException
   */
	public boolean isOriginTerminalValidToRoute(Connection con, String routeId,String originTerminal) throws SQLException
	{
		String sql="SELECT ROUTEID FROM FS_FRS_ROUTEMASTER RM,FS_FR_TERMINALGATEWAY TG  WHERE RM.ROUTEID='"+routeId+"' RM.ORIGINGATEWAY=TG.GATEWAYID AND TG.TERMINALID='"+originTerminal+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param routeId
   * @param destTerminal
   * @return booelan
   * @throws java.sql.SQLException
   */
	public boolean isDestinationTerminalValidToRoute(Connection con, String routeId,String destTerminal) throws SQLException
	{
		String sql="SELECT ROUTEID FROM FS_FRS_ROUTEMASTERDTL WHERE ROUTEID='"+routeId+"' AND DESTTERMINAL='"+destTerminal+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param consoleNo
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isConsoleNoExists(Connection con,String consoleNo)throws SQLException
	{
		String 		sql	= "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE CONSOLEID='"+consoleNo+"' AND DIRECTCONSOLE = 'N'";
		return isIdExists(con,sql);
	}

  /**
   * 
   * @param con
   * @param consoleNo
   * @param terminalId
   * @param operation
   * @param terminalType
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isConsoleNoExists(Connection con, String consoleNo, String terminalId, String operation, String terminalType) throws SQLException
	{
		String sql = "";

		String finder = "";
		if(terminalType.equals("S"))
		{
			finder = "WHERE ORIGINGATEWAY='"+terminalId+"' "; //AND DESTGATEWAY IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID!='"+terminalId+"' AND TERMINALTYPE='S')";
		}
		else if(terminalType.equals("N"))
		{
			finder = "WHERE DESTGATEWAY='"+terminalId+"' AND ORIGINGATEWAY IN (SELECT TG.GATEWAYID  FROM FS_FR_TERMINALMASTER TM,FS_FR_TERMINALGATEWAY TG WHERE   TG.TERMINALID=TM.TERMINALID AND TM.TERMINALTYPE='N')";
		}

		if(operation.equals("Modify"))
		{
			sql = "SELECT CONSOLEID  FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER "+finder+" )  AND CONSOLESTATUS='N' AND CONSOLEID='"+consoleNo+"' AND CONSOLTYPE != 'FCL_BACK_TO_BACK' AND DIRECTCONSOLE = 'N' ORDER BY CONSOLEID ";
		}
		else if(operation.equals("Delete"))
		{
		     
		    sql =  " SELECT CONSOLEID  FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER "+finder+" )  AND CONSOLTYPE NOT IN('FCL_BACK_TO_BACK') "
				+ " AND CONSOLEID NOT IN ( SELECT CM.CONSOLEID FROM  FS_FRS_CONSOLEMASTER CM,FS_FRS_OBLMASTER OM  WHERE CM.CONSOLEID = OM.CONSOLEID ) "
				+ " AND DIRECTCONSOLE = 'N' ORDER BY CONSOLEID ";
		}
		else
		{
			sql = "SELECT CONSOLEID  FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER "+finder+" )  AND CONSOLEID='"+consoleNo+"' AND CONSOLTYPE != 'FCL_BACK_TO_BACK' AND DIRECTCONSOLE = 'N' ORDER BY CONSOLEID";
		}
		//Logger.info(FILE_NAME,"Query For Valid SQL is "+sql);
		
		return isIdExists(con,sql);
	}
	// new method added to verify if gateway is a terminal
  /**
   * 
   * @param con
   * @param gatewayId
   * @param bookingId
   * @param shipmentMode
   * @param operationType
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isBookingAtGatewayTerminal(Connection con, String gatewayId, String bookingId, String shipmentMode, String operationType)throws SQLException
	{
		String sql	=  null;
		sql	=	" SELECT PRQID FROM FS_FR_PICKUPREQUEST WHERE PRQID='"+bookingId
            	 +"' AND ORIGINTERMINAL=(SELECT TERMINALID FROM FS_FR_TERMINALGATEWAY"+
                	" WHERE GATEWAYID='"+gatewayId+"' AND GATEWAYID=TERMINALID) "; 
		return isIdExists(con,sql);
	}
	// method ends here

  /**
   * 
   * @param con
   * @param consoleNo
   * @param terminalId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isDirectConsoleExists(Connection con,String consoleNo, String terminalId)throws SQLException
	{
		String sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY='"+terminalId+"')  AND CONSOLESTATUS != 'CLOSED' AND DIRECTCONSOLE='Y' AND CONSOLEID='"+consoleNo+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param con
   * @param consoleNo
   * @param terminalId
   * @param operation
   * @return boolean
   * @throws java.sql.SQLException
   */
	public static boolean isConsoleNoExists(Connection con, String consoleNo, String terminalId, String operation) throws SQLException
	{
		String sql = "";

		if(operation.equals("Modify"))
    {
			sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY='"+terminalId+"')  AND CONSOLESTATUS = 'N' AND CONSOLEID='"+consoleNo+"' AND DIRECTCONSOLE = 'N'";
    }
		else if(operation.equals("View") || operation.equals("Delete"))
    {
			sql = "SELECT CONSOLEID FROM FS_FRS_CONSOLEMASTER WHERE ROUTEID IN (SELECT DISTINCT(ROUTEID) FROM FS_FRS_ROUTEMASTER WHERE ORIGINGATEWAY='"+terminalId+"')  AND CONSOLEID='"+consoleNo+"' AND DIRECTCONSOLE = 'N'";
    }
		return isIdExists(con,sql);
	}
	
	
  /**
   * 
   * @param con
   * @param oblId
   * @return boolean
   * @throws java.sql.SQLException
   */
	public boolean isOblIdExists(Connection con,String oblId) throws SQLException
	{
		String sql="SELECT OBLID FROM  FS_FRS_OBLMASTER WHERE  OBLID ='"+oblId+"'";
		return isIdExists(con,sql);
	}
  /**
   * 
   * @param rs
   * @param st
   * @throws java.sql.SQLException
   */
	private static void closeStatement(ResultSet rs,Statement st)throws SQLException
	{
		if(rs!=null)
    {
      rs.close();
    }
		if(st!=null)
    {
      st.close();
    }
	}
	
  /**
   * 
   * @return Connection
   * @throws java.sql.SQLException
   */
	private static java.sql.Connection getConnection()throws SQLException
	{
		java.sql.Connection connection	=	null;
		try
		{
			if (dataSource == null)
			{
				InitialContext ictx	=  new InitialContext();
				dataSource = (javax.sql.DataSource) ictx.lookup(com.foursoft.esupply.common.java.FoursoftConfig.DATA_SOURCE);
			}
			connection = dataSource.getConnection();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in getConnection() "+e,e);
      logger.error(FILE_NAME+"Exception in getConnection() "+e);
			throw new SQLException("Unable to create connection");
		}
		return connection;
	}
}


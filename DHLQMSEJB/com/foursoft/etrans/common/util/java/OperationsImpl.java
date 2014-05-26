/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/
package	com.foursoft.etrans.common.util.java;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.ejb.EJBException;

import java.rmi.RemoteException;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.ArrayList;

import	java.util.Calendar;
import	java.util.TimeZone;
import	java.sql.Timestamp;
import  java.util.Date;
import  java.text.DateFormat;
import  java.text.SimpleDateFormat;
import com.foursoft.esupply.common.util.ConnectionUtil;

//import com.foursoft.etrans.setup.country.bean.CountryMaster;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.etrans.air.mawb.bean.MasterDocumentCarrierContract;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.exception.InvalidDateFormatException;
/**
 * @author:
 * @version:
 */
public class OperationsImpl	implements javax.ejb.EJBObject
{
	private static final String FILE_NAME = "OperationsImpl.java";
	private	 InitialContext	initialContext = null;
	private	 DataSource		dataSource	   = null;
	private static Logger logger = null;

  public OperationsImpl()
  {
    logger  = Logger.getLogger(OperationsImpl.class);
  }
  /**
   *
   * @return countryIds
   * @throws javax.ejb.EJBException
   */
	public String[]	getCountryIds()	throws javax.ejb.EJBException
	{
		Connection	  connection	= null;
		Statement	  stmt		   = null;
		ResultSet	  rs			= null;
		String[]	  countryIds	= null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_COUNTRYMASTER ORDER BY COUNTRYNAME";
			String sql2	= "SELECT COUNTRYID,COUNTRYNAME	FROM FS_COUNTRYMASTER ORDER	BY COUNTRYNAME";
			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	len	= rs.getInt("NO_ROWS");
			countryIds	 = new String[ len ];
			rs = stmt.executeQuery(	sql2 );
			int	row	= 0;
			while( rs.next() )
			{
				String cId = rs.getString("COUNTRYID");
				String cName = rs.getString("COUNTRYNAME");
				countryIds[	row	]	= cName+" (	"+cId+"	)";
				row++;
			}
			return	countryIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCountryInfo::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					connection.close();
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCountryInfo:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @return vecGatewayIds
   * @throws javax.ejb.EJBException
   */
	public Vector getGatewayIds() throws javax.ejb.EJBException
	{
		Connection	  connection	= null;
		Statement	  stmt			= null;
		ResultSet	  rs			= null;
		Vector		  vecGatewayIds	= new Vector();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT GATEWAYID FROM	FS_FR_GATEWAYMASTER	ORDER BY GATEWAYID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				vecGatewayIds.addElement( rs.getString("GATEWAYID")	);
			}
			return	vecGatewayIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCountryInfo::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCountryInfo:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param searchString
   * @return vecGatewayIds
   * @throws javax.ejb.EJBException
   */
	public ArrayList getGatewayIds(String searchString) throws javax.ejb.EJBException
	{
		Connection	  connection	= null;
		Statement	  stmt			= null;
		ResultSet	  rs			= null;
		ArrayList		  vecGatewayIds	= new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT GATEWAYID FROM	FS_FR_GATEWAYMASTER	WHERE GATEWAYID LIKE '"+searchString+"%' ORDER BY GATEWAYID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				vecGatewayIds.add( rs.getString("GATEWAYID")	);
			}
			return	vecGatewayIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCountryInfo::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCountryInfo:finally::..."+e.toString());
			}
		}
	}
	//@@New method added by Sreelakshmi K.V.A. to retreive HBL Nos for Sea CAN Report 20050211 PR-ET-1196-CAN
	/*
	* @param terminalId
    * @return houseId
	*/
public ArrayList	 getHBLIds( String terminalId, String searchString)
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			/*String sql2	= " SELECT A.HAWB_ID HID FROM  FS_RT_PLAN A,FS_RT_LEG B "
				+ " WHERE A.HAWB_ID IS NOT NULL AND A.RT_PLAN_ID=B.RT_PLAN_ID  AND "
				+ " B.SHPMNT_STATUS IN('C','P','B') ";*/
				String sql2 = " SELECT HAWB_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H"+
						" WHERE"+
						" HAWB_ID LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.SHPMNT_STATUS IN('C','P','B') AND H.SHIPMENTMODE=2";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("HAWB_ID"));

			}
			return	houseId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"getHBLIds:finally::..."+e.toString());
        logger.error(FILE_NAME+"getHBLIds:finally::..."+e.toString());
			}
		}
	}
   //@@ 20050211 PR-ET-1196-CAN

   //@@New method added by Sreelakshmi K.V.A. to retreive Consignment Nos for Truck CAN Report 20050223 PR-ET-1196-CAN
	/*
	* @param terminalId
    * @return houseId
	*/
public ArrayList	 getConsignmentNoteIds( String terminalId, String searchString)
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

				String sql2 = " SELECT HAWB_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H"+
						" WHERE"+
						" HAWB_ID LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
            //@@ rplaced by vinod balla 20050430 issue 6574
					//	" AND LG.SHPMNT_STATUS IN('C','P') AND H.SHIPMENTMODE=4";
              " AND LG.SHPMNT_STATUS IN('C','P','B') AND H.SHIPMENTMODE=4";
            //@@ vinod balla 20050430 issue 6574
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("HAWB_ID"));

			}
			return	houseId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"getConsignmentNoteIds:finally::..."+e.toString());
        logger.error(FILE_NAME+"getConsignmentNoteIds:finally::..."+e.toString());
			}
		}
	}
   //@@ 20050223 PR-ET-1196-CAN
   //@@New method added by Sreelakshmi K.V.A. to retreive HAWB Nos for Air CAN Report 20050223 PR-ET-1196-CAN
	/*
	* @param terminalId
    * @return houseId
	*/
public ArrayList	 getHAWBIds( String terminalId, String searchString)
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

				/*String sql2 = " SELECT H.HOUSEDOCID HID FROM FS_FR_MASTERDOCHDR M, FS_FR_HOUSEDOCHDR H  "
                      +" WHERE "
					  +" HID LIKE '"+searchString+"%'"
					  +"AND H.MASTERDOCID = M.MASTERDOCID AND H.SHIPMENTMODE =' 1'  "
					  +"AND  M.DESTINATIONGATEWAYID = '"+terminalId+"'"
					  +"AND M.STATUS IN('CLOSED','COB') "
					  +"AND M.MASTERTYPE IS NULL "
					  +"AND (M.RECEPIETSTATUS  IS NULL OR  M.RECEPIETSTATUS ='P')";*/


					 String sql2 = " SELECT HAWB_ID  FROM FS_RT_PLAN A, FS_RT_LEG B WHERE "
					               +"  HAWB_ID LIKE '"+searchString+"%'"
					               + " AND A.RT_PLAN_ID = B.RT_PLAN_ID AND A.SHPMNT_STATUS ! = 'D' AND B.MSTER_DOC_ID "
								   + " IN (SELECT M.MASTERDOCID MID FROM FS_FR_MASTERDOCHDR M, FS_FR_MASTERFLIGHTDTL  D "
								   + "WHERE	M.MASTERDOCID  = D.MASTERDOCID AND M.SHIPMENTMODE =' 1'  AND "
								   + "M.DESTINATIONGATEWAYID = '"+terminalId+"'"
								   + " AND  M.MASTERTYPE IS NULL AND  "
								   + "M.RECEPIETSTATUS IN ('P','Full') AND  D.SLNO = (SELECT MAX(SLNO) FROM  "
								   +" FS_FR_MASTERFLIGHTDTL DTL WHERE 	DTL.MASTERDOCID = M.MASTERDOCID)) ";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("HAWB_ID"));

			}
			return	houseId;
		}
		catch( SQLException	e )
		{
			e.printStackTrace();
			return null;
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"getHawbIds:finally::..."+e.toString());
        logger.error(FILE_NAME+"getHawbIds:finally::..."+e.toString());
			}
		}
	}
   //@@ 20050223 PR-ET-1196-CAN
  /**
   *
   * @return vecComodityIds
   * @throws javax.ejb.EJBException
   */
	public Vector getComodityIds() throws javax.ejb.EJBException
	{
		Connection	  connection	= null;
		Statement	  stmt			= null;
		ResultSet	  rs			= null;
		Vector		  vecComodityIds = new Vector();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT COMODITYID	FROM FS_FR_COMODITYMASTER ORDER	BY COMODITYID ";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				vecComodityIds.addElement( rs.getString(1) );
			}
			return	vecComodityIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCountryInfo::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCountryInfo:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @return vecServiceLevels
   * @throws javax.ejb.EJBException
   */
	public Vector getServiceLevels() throws	javax.ejb.EJBException
	{
		Connection	  connection	   = null;
		Statement	  stmt			   = null;
		ResultSet	  rs			   = null;
		Vector		  vecServiceLevels = new Vector();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT SERVICELEVELID, SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER	ORDER BY SERVICELEVELID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				String strId   = rs.getString("SERVICELEVELID");
				String strDesc = rs.getString("SERVICELEVELDESC");
				vecServiceLevels.addElement( strId+","+strDesc );
			}
			return	vecServiceLevels;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getServiceLevels::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getServiceLevels:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param searchString
   * @param shipmentMode
   * @return vecServiceLevels
   * @throws javax.ejb.EJBException
   */
public ArrayList getServiceLevels(String searchString,String shipmentMode) throws	javax.ejb.EJBException
	{
		Connection	  connection	   = null;
		Statement	  stmt			   = null;
		ResultSet	  rs			   = null;
		ArrayList		  vecServiceLevels = new ArrayList();
		try
		{
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipmentMode="(1,3,5,7)";}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipmentMode="(4,5,6,7)";}

			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT SERVICELEVELID, SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER	WHERE SERVICELEVELID LIKE '"+searchString+"%' AND SHIPMENTMODE IN "+shipmentMode+" ORDER BY SERVICELEVELID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				String strId   = rs.getString("SERVICELEVELID");
				String strDesc = rs.getString("SERVICELEVELDESC");
				vecServiceLevels.add( strId+","+strDesc );
			}
			return	vecServiceLevels;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getServiceLevels::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
				{	rs.close();}
				if(	stmt!=null )
				{	stmt.close();}
        if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getServiceLevels:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @return carrierIds
   * @throws javax.ejb.EJBException
   */
	public ArrayList getCarrierIds() throws	javax.ejb.EJBException
	{
		Connection	  connection	   = null;
		Statement	  stmt			   = null;
		ResultSet	  rs			   = null;
		ArrayList	carrierIds	   = new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT CARRIERID,	CARRIERNAME, NUMERICCODE FROM FS_FR_CAMASTER ORDER BY CARRIERID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				String strId   = rs.getString("CARRIERID");
				String strName = rs.getString("CARRIERNAME");
				String strCode = rs.getString("NUMERICCODE");
				carrierIds.add(	strId+","+strName+","+strCode );
			}
			return	carrierIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getServiceLevels::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getServiceLevels:finally::..."+e.toString());
			}
		}
	}
//added	by raghu on	06-april-01
 // added by B S REDDY for Search String
  /**
   *
   * @param searchString
   * @param shipmentMode
   * @return carrierIds
   * @throws javax.ejb.EJBException
   */
public ArrayList getCarrierIds(String searchString,String shipmentMode) throws	javax.ejb.EJBException
	{
		Connection	  connection	   = null;
		Statement	  stmt			   = null;
		ResultSet	  rs			   = null;
		ArrayList	carrierIds	   = new ArrayList();
		try
		{
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipmentMode="(1,3,5,7)";}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipmentMode="(4,5,6,7)";}

			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT CARRIERID,	CARRIERNAME, NUMERICCODE FROM FS_FR_CAMASTER WHERE CARRIERID LIKE '"+searchString+"%'  AND SHIPMENTMODE IN "+shipmentMode+"  ORDER BY CARRIERID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				String strId   = rs.getString("CARRIERID");
				String strName = rs.getString("CARRIERNAME");
				String strCode = rs.getString("NUMERICCODE");
				carrierIds.add(	strId+","+strName+","+strCode );
			}
			return	carrierIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getServiceLevels::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getServiceLevels:finally::..."+e.toString());
			}
		}
	}
//added	by raghu on	06-april-01
  /**
   *
   * @return vecTerminalIds
   * @throws javax.ejb.EJBException
   */
public Vector getTerminalIds() throws javax.ejb.EJBException
	{
		Connection	  connection		=	null;
		Statement	  stmt				=	null;
		ResultSet	  rs				=	null;
		Vector		  vecTerminalIds	=	new	Vector();

		try
		{
			connection	=	getConnection();
			stmt		=	connection.createStatement();
			String sql	=	"SELECT	TERMINALID FROM	FS_FR_TERMINALMASTER  WHERE OPER_ADMIN_FLAG = 'O'  AND SHIPMENTMODE IN (1,3,5,7)  ORDER BY TERMINALID";
			rs			=	stmt.executeQuery( sql );
			while( rs.next() )
			{
				vecTerminalIds.addElement( rs.getString("TERMINALID") );
			}

			return	vecTerminalIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getTerminalIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{ rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getTerminalIds:finally::..."+e.toString());
			}
		}
	}
//end of method
//added	by raghu on	06-april-01
 // added by B S REDDY for Search String
  /**
   *
   * @param searchString
   * @return vecTerminalIds
   * @throws javax.ejb.EJBException
   */
public ArrayList getTerminalIds(String searchString) throws javax.ejb.EJBException
	{
		Connection	  connection		=	null;
		Statement	  stmt				=	null;
		ResultSet	  rs				=	null;
		ArrayList		  vecTerminalIds	=	new	ArrayList();

		try
		{
			connection	=	getConnection();
			stmt		=	connection.createStatement();
			String sql	=	"SELECT	TERMINALID FROM	FS_FR_TERMINALMASTER WHERE TERMINALID LIKE '"+searchString+"%' AND OPER_ADMIN_FLAG = 'O'  AND SHIPMENTMODE IN (1,3,5,7) ORDER BY TERMINALID";
			rs			=	stmt.executeQuery( sql );
			while( rs.next() )
			{
				vecTerminalIds.add( rs.getString("TERMINALID") );
			}

			return	vecTerminalIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getTerminalIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					connection.close();
			}
			catch( SQLException	e )
			{
				throw new EJBException("getTerminalIds:finally::..."+e.toString());
			}
		}
	}
//end of method

  /**
   *
   * @param terminalId
   * @param userTerminalType
   * @return aList
   * @throws javax.ejb.EJBException
   */
public ArrayList getTerminalIdsOfHA(String terminalId,String userTerminalType) throws javax.ejb.EJBException
	{
		Connection	  connection		=	null;
		//Statement	  stmt				=	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		//ResultSet	  rs				=	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		ArrayList 	  aList				=   new ArrayList();

		String sql = "";

		try
		{
			connection	=	getConnection();

			aList = getTerminalsRec(terminalId,connection, new ArrayList());

			return	aList;
		}
		catch( Exception	e )
		{
			throw new EJBException("getTerminalIdsOfHA::..."+e.toString());
		}
		finally
		{
			try
			{
				/*if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getTerminalIdsOfHA:finally::..."+e.toString());
			}
		}
	}
//end of method

  /**
   *
   * @param terminalId
   * @param connection
   * @param aList
   * @return al
   */
	public ArrayList  getTerminalsRec(String terminalId,Connection connection, ArrayList aList)
	{

		Statement	  stmt				=	null;
		ResultSet	  rs				=	null;

		ArrayList 	  al				=   new ArrayList();

		String sql = "";

		try
		{
			stmt		=	connection.createStatement();

			sql	=	"SELECT	CHILD_TERMINAL_ID FROM	FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID = '"+terminalId+"' ORDER BY CHILD_TERMINAL_ID";

			rs			=	stmt.executeQuery( sql );
			while( rs.next() )
			{
				String str = rs.getString(1);

				if(isOperationalTerminal(str,connection))
					{aList.add(str);}
				else
					al = getTerminalsRec(str,connection,aList);
			}

		}
		catch( SQLException	e )
		{
			throw new EJBException("getTerminalsRec::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
				{	rs.close();}
				if(	stmt!=null )
					{stmt.close();}

			}
			catch( Exception	e )
			{
				throw new EJBException("getTerminalIdsOfHA:finally::..."+e.toString());
			}
		}

		return	aList;
	}

	private boolean isOperationalTerminal(String terminalId,Connection connection) throws javax.ejb.EJBException
	{

		Statement	  stmt				=	null;
		ResultSet	  rs				=	null;

		String sql = "";

		try
		{
			stmt		=	connection.createStatement();

			sql	=	"SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+terminalId+"' AND OPER_ADMIN_FLAG = 'O' ";

			rs			=	stmt.executeQuery( sql );
			if( rs.next() )
			   {return true;}
			else
				return false;
		}
		catch( SQLException	e )
		{
			throw new EJBException("isOperationalTerminal(::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{ rs.close();}
				if(	stmt!=null )
					{ stmt.close(); }
			}
			catch( SQLException	e )
			{
				throw new EJBException("isOperationalTerminal(:finally::..."+e.toString());
			}
		}
	}
//end of method

  /**
   *
   * @return customerId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getCustomerIds() throws	javax.ejb.EJBException
	{
		Connection		connection		= null;
		Statement		stmt			= null;
		ResultSet		rs				= null;
		ArrayList		customerId		= new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	=	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_CUSTOMERMASTER ORDER BY	CUSTOMERID";
			String sql2	=	"SELECT	CUSTOMERID FROM	FS_FR_CUSTOMERMASTER ORDER BY CUSTOMERID";







			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				customerId.add(rs.getString("CUSTOMERID"));

			}
			return	customerId;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCustomerIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCustomerIds:finally::..."+e.toString());
			}
		}
	} //getCustomerIds
	 // added by B S REDDY for Search String
  /**
   *
   * @param searchString
   * @return customerId
   * @throws javax.ejb.EJBException
   */
	 public ArrayList	getCustomerIds(String searchString) throws	javax.ejb.EJBException
	{
		Connection		connection		= null;
		Statement		stmt			= null;
		ResultSet		rs				= null;
		ArrayList		customerId		= new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	=	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID LIKE '"+searchString+"%' ORDER BY	CUSTOMERID";
			String sql2	=	"SELECT	CUSTOMERID FROM	FS_FR_CUSTOMERMASTER WHERE CUSTOMERID LIKE '"+searchString+"%' ORDER BY CUSTOMERID";







			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				customerId.add(rs.getString("CUSTOMERID"));

			}
			return	customerId;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCustomerIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCustomerIds:finally::..."+e.toString());
			}
		}
	} //getCustomerIds
  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @param CAcarrierId
   * @param serviceLevel1
   * @return carrierContract
   * @throws javax.ejb.EJBException
   */

	public Vector getCurrencyIds() throws javax.ejb.EJBException
	{
		Connection	  connection	 = null;
		Statement	  stmt			 = null;
		ResultSet	  rs			 = null;
		Vector		  vecCurrencyIds = new Vector();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT CURRENCYID	FROM FS_COUNTRYMASTER ORDER	BY CURRENCYID";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				vecCurrencyIds.addElement( rs.getString("CURRENCYID") );
			}
			return	vecCurrencyIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getCurrencyIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getCurrencyIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @return masterdocIds
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getMasterdocIds( String	terminalId ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		//String[]	  masterdocIds	  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_MASTERDOCHDR WHERE STATUS!='CLOSED' AND TERMINALID='"+terminalId+"' ORDER	BY MASTERDOCID";
			String sql2	= "SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE	STATUS!='CLOSED' AND TERMINALID='"+terminalId+"' ORDER BY MASTERDOCID DESC";
			rs = stmt.executeQuery(	sql1 );
			//rs.next();
			//int	cnt	= rs.getInt("NO_ROWS");
			ArrayList masterdocIds = new ArrayList();
			//int	row	=0;
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterdocIds.add(rs.getString("MASTERDOCID"));

			}
			return	masterdocIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getMasterdocIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param strTerminalId
   * @param strCarrierId
   * @return masterdocIds
   * @throws javax.ejb.EJBException
   */
	public String[]	getNonRegisteredMasterdocIds( String strTerminalId,	String strCarrierId	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  masterdocIds	  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_MASTERDOCREG WHERE TERMINALID='"+	strTerminalId +"' AND CARRIERID='"+	strCarrierId +"' AND STATUS='NA' ORDER BY MASTERDOCID";
			String sql2	= "SELECT MASTERDOCID FROM FS_FR_MASTERDOCREG WHERE	TERMINALID='"+ strTerminalId +"' AND CARRIERID='"+ strCarrierId	+"'	AND	STATUS='NA'	ORDER BY MASTERDOCID";
			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}
			masterdocIds = new String[cnt];
			int	row	=0;
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterdocIds[row] =	rs.getString("MASTERDOCID");
				row++;
			}
			return	masterdocIds;
		}
		catch( SQLException	e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{ stmt.close(); }
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param userId
   * @param docType
   * @param docId
   * @param docDate
   * @param transactionType
   * @throws javax.ejb.EJBException
   */
	public void	setTransactionDetails(	String terminalId,
										String userId,
										String docType,
										String docId,
										java.sql.Timestamp docDate,
										String transactionType	) throws javax.ejb.EJBException
	{
		Connection			connection	   = null;
		//PreparedStatement	pstmt		   = null;
    Statement	stmt		   = null;
		try
		{
			connection = getConnection();
		//	String sql = "INSERT INTO FS_USERLOG( LOCATIONID, USERID, DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE ) "+
						// "VALUES(?,?,?,?,?,?)";
             String sql = "INSERT INTO FS_USERLOG( LOCATIONID, USERID, DOCTYPE, DOCREFNO, DOCDATE, TRANSACTIONTYPE ) "+
						 "VALUES('"+terminalId+"','"+userId+"','"+docType+"','"+docId+"',TO_TIMESTAMP('"+docDate+"','YYYY-MM-DD HH24:MI:ss.ff3'),'"+transactionType+"')";
            // "VALUES('"+terminalId+"',"+userId+","+docType+","+docId+",TO_DATE('"+docDate+"','DD-MM-YYYY HH24:MI:SS'),"+transactionType+")";

		/*	pstmt =	connection.prepareStatement( sql );
			pstmt.setString( 1,	terminalId );
			pstmt.setString( 2,	userId );
			pstmt.setString( 3,	docType	);
			pstmt.setString( 4,	docId );
			pstmt.setTimestamp(	5, docDate );
			pstmt.setString( 6,	transactionType	);
			pstmt.executeUpdate();*/
      stmt=connection.createStatement();
     stmt.executeUpdate(sql);
		}
		catch( SQLException	e )
		{
			throw new EJBException("setTransactionDetails::..."+e.toString());
		}
		finally
		{
			try
			{
				if(stmt!=null	)
					{stmt.close();}
				if(	connection!=null )
					{ connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("setTransactionDetails:finally::..."+e.toString());
			}
		}
	}
	// CRM methods
  /**
   *
   * @param customerId
   * @return prqId
   * @throws javax.ejb.EJBException
   */
public ArrayList	getPRQIds( String customerId ) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  prqId			  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
      String sql2	= "SELECT PRQID	FROM FS_FR_PICKUPREQUEST WHERE SHIPPERID='"+ customerId	+"'	OR CONSIGNEEID='"+customerId+"'	ORDER BY PRQID DESC";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId.add(rs.getString("PRQID"));

			}
			return	prqId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{ rs.close();}
				if(	stmt!=null )
					{ stmt.close();}
				if(	connection!=null )
					{ connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for search String
	// method modified by supraja CKM to implement shipmentmode.
  /**
   *
   * @param customerId
   * @param searchString
   * @param shipmentMode
   * @return prqId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getPRQIds( String customerId ,String searchString,String shipmentMode) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  prqId			  =	new ArrayList();
		String 		  sql1			  = null;
		String 		  sql2			  = null;

		try
		{

			connection = getConnection();
			stmt = connection.createStatement();


				if(shipmentMode.equals("Air"))
					{ shipmentMode="1";}
				else if(shipmentMode.equals("Sea"))
					shipmentMode="2";
				else
					shipmentMode="4";

				 //sql1	= "SELECT COUNT(*) NO_ROWS  FROM FS_FR_PICKUPREQUEST WHERE  PRQID LIKE '"+searchString+"%' AND   SHIPMENTMODE='"+shipmentMode+"' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"')  ORDER BY PRQID ";
				 sql2	= "SELECT PRQID FROM FS_FR_PICKUPREQUEST WHERE  PRQID LIKE '"+searchString+"%' AND PRQID NOT LIKE '%/1' AND  SHIPMENTMODE='"+shipmentMode+"' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"')  ORDER BY PRQID DESC ";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId.add(rs.getString(1));

			}
			return	prqId;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"SQLException in getPrqIds(3):	"+e);
      logger.error(FILE_NAME+"SQLException in getPrqIds(3):	"+e);
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in getPrqIds(3):	"+e);
      logger.error(FILE_NAME+"Exception in getPrqIds(3):	"+e);
			return null;
		}
		finally
		{
				closeConnection(rs,stmt,connection);
		}
	}

  /**
   *
   * @param customerId
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getMasterIds( String customerId	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  masterId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			String sql2	= "SELECT FS_FR_MASTERDOCHDR.MASTERDOCID MASTERDOCID FROM FS_FR_MASTERDOCHDR, FS_FR_HOUSEDOCHDR	WHERE FS_FR_MASTERDOCHDR.MASTERDOCID=FS_FR_HOUSEDOCHDR.MASTERDOCID AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"')	ORDER BY FS_FR_MASTERDOCHDR.MASTERDOCID	DESC";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId.add(rs.getString("MASTERDOCID"));

			}
			return	masterId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	 // added by B S REDDY for Search String ON

  /**
   *
   * @param customerId
   * @param searchString
   * @param shipmentMode
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getMasterIds( String customerId,String searchString,String shipmentMode	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  masterId		  =	new ArrayList();
		String		  sql1			  = null;
		String		  sql2			  = null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			if(shipmentMode.equalsIgnoreCase("Air"))
			{
			//sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_MASTERDOCHDR,	FS_FR_HOUSEDOCHDR WHERE	FS_FR_MASTERDOCHDR.MASTERDOCID=FS_FR_HOUSEDOCHDR.MASTERDOCID AND FS_FR_MASTERDOCHDR.MASTERDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+ customerId +"') ORDER	BY FS_FR_MASTERDOCHDR.MASTERDOCID";
			sql2	= "SELECT FS_FR_MASTERDOCHDR.MASTERDOCID MASTERDOCID FROM FS_FR_MASTERDOCHDR, FS_FR_HOUSEDOCHDR	WHERE FS_FR_MASTERDOCHDR.MASTERDOCID=FS_FR_HOUSEDOCHDR.MASTERDOCID AND FS_FR_HOUSEDOCHDR.SHIPMENTMODE=1 AND FS_FR_MASTERDOCHDR.MASTERDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"')	ORDER BY FS_FR_MASTERDOCHDR.MASTERDOCID	DESC";
			}
			else
			{
				 //sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FRS_CONSOLEMASTER,FS_FR_HOUSEDOCHDR WHERE FS_FR_HOUSEDOCHDR.SHIPMENTMODE=2 AND 	FS_FRS_CONSOLEMASTER.CONSOLEID=FS_FR_HOUSEDOCHDR.MASTERDOCID AND (FS_FR_HOUSEDOCHDR.SHIPPERID='"+	customerId +"' OR FS_FR_HOUSEDOCHDR.CONSIGNEEID='"+ customerId +"') AND FS_FRS_CONSOLEMASTER.CONSOLEID LIKE '"+searchString+"%' ORDER	BY FS_FRS_CONSOLEMASTER.CONSOLEID";
				 sql2	= "SELECT FS_FRS_CONSOLEMASTER.CONSOLEID MASTERDOCID FROM FS_FRS_CONSOLEMASTER, FS_FR_HOUSEDOCHDR	WHERE  FS_FR_HOUSEDOCHDR.SHIPMENTMODE=2 AND  FS_FRS_CONSOLEMASTER.CONSOLEID=FS_FR_HOUSEDOCHDR.MASTERDOCID AND (FS_FR_HOUSEDOCHDR.SHIPPERID='"+customerId+"' OR FS_FR_HOUSEDOCHDR.CONSIGNEEID='"+customerId+"') AND FS_FRS_CONSOLEMASTER.CONSOLEID LIKE '"+searchString+"%'	ORDER BY FS_FRS_CONSOLEMASTER.CONSOLEID	DESC";
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId.add(rs.getString("MASTERDOCID"));

			}
			return	masterId;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getMasterdocIds:finally::..."+e.toString());
		}
		catch( Exception e )
		{
			throw new EJBException("getMasterdocIds:finally::..."+e.toString());
		}
		finally
		{
			try
			{
				closeConnection(rs,stmt,connection);
			}
			catch(Exception	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @return prqId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getPickUpRequestIds( String	terminalId,String shipmentMode  ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  prqId			  =	new ArrayList();

		try
		{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_PICKUPREQUEST	WHERE ORIGINTERMINAL='"	+ terminalId +"' AND SHIPMENTMODE="+shipMode+"  ";
			String sql2	= "SELECT PRQID	FROM FS_FR_PICKUPREQUEST WHERE ORIGINTERMINAL ='" + terminalId +"' 	AND SHIPMENTMODE="+shipMode+"  ORDER	BY PRQID DESC";
    	rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId.add(rs.getString("PRQID"));

			}

			return	prqId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

	// @@ Anup added on 20050103

	public ArrayList	getPackId(String customerId,String shiper,String shipmentMode,String searchString,ESupplyGlobalParameters loginbean )
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  packId		  =	new ArrayList();
		String        sql2            = null;

		try
		{

			String 			userType 			= loginbean.getUserType();
		   // Logger.info(FILE_NAME," Anup ====================================>>>>>>>>>> userType : "+userType);

			connection   = getConnection();
			stmt         = connection.createStatement();

			if("ETC".equals(userType))
			{
			   sql2 = "SELECT DISTINCT P.PACKID FROM FS_FR_PACKIDDTL P,FS_FR_HOUSEDOCHDR H WHERE H.HOUSEDOCID=P.HOUSEDOCID "+
				      " AND H.SHIPPERID='"+customerId+"' OR H.CONSIGNEEID='"+customerId+"'"+
				      " AND P.PACKID LIKE '"+searchString+"%' ORDER BY P.PACKID ";

			}
			else
			{
                sql2 = "SELECT DISTINCT P.PACKID FROM FS_FR_PACKIDDTL P,FS_FR_HOUSEDOCHDR H WHERE H.HOUSEDOCID=P.HOUSEDOCID "+
				       " AND P.PACKID LIKE '"+searchString+"%' AND H.TERMINALID='"+customerId+"' ORDER BY P.PACKID ";
			}
			//Logger.info(FILE_NAME,"test ==================>Query sql2 : "+sql2);

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				packId.add(rs.getString(1));

			}

			return	packId;
		}
		catch( SQLException	e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME," ============>Exception : "+e.toString());
      logger.error(FILE_NAME+" ============>Exception : "+e.toString());

			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getPackId:finally::..."+e.toString());
			}
		}
	}

	// @@

// B S REDDY
  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return prqId
   * @throws javax.ejb.EJBException
   */
  public ArrayList	getPickUpRequestId(	String terminalId,String shiper,
										String shipmentMode,String searchString,
										ESupplyGlobalParameters loginbean) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  prqId			  =	new ArrayList();
		String		  sql1			  =	null;
		String		  sql2			  =	null;
		try
		{
			int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}

			connection = getConnection();
			stmt = connection.createStatement();
			String		dateFormat				=	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);

			if(shiper.equals("Inbound"))
			{
				 //sql1 =	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_PICKUPREQUEST WHERE	 DESTTERMINAL='"+terminalId+"' AND PRQID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15 	 AND SHIPMENTMODE="+shipMode+" ";
				 //@@ Avinash Replaced on 20041016
				 //sql2 =	"SELECT	PRQID FROM FS_FR_PICKUPREQUEST WHERE  DESTTERMINAL='"+terminalId+"'	AND PRQID NOT LIKE '%/1' AND	PRQID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15	 AND SHIPMENTMODE="+shipMode+"  ORDER BY PRQID DESC";
				 sql2 = " SELECT PRQ_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P"+
						" WHERE"+
						" PRQ_ID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
					      // @@ Anup added on 20050503
                          " AND P.GROUPED='N' "+
				        // @@ 20050503
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PRQDATE DESC";
				 //@@
			}
			if(shiper.equals("Outbound"))
			{
			//	sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_PICKUPREQUEST WHERE	ORIGINTERMINAL='"+terminalId+"'	AND	PRQID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
			//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT PRQID FROM FS_FR_PICKUPREQUEST WHERE	ORIGINTERMINAL='"+terminalId+"' AND PRQID NOT LIKE '%/1' AND	PRQID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15	AND SHIPMENTMODE="+shipMode+"  ORDER BY PRQID DESC";
				 sql2 = " SELECT PRQ_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P"+
						" WHERE"+
						" PRQ_ID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
					    // @@ Anup added on 20050503
                          " AND P.GROUPED='N' "+
				        // @@ 20050503
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PRQDATE DESC";
				 //@@
			}
			if(shiper.equals("None"))
			{
				//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_PICKUPREQUEST WHERE	(ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"')	AND	ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15 AND SHIPMENTMODE="+shipMode+" ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT PRQID FROM FS_FR_PICKUPREQUEST WHERE	(ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"') AND PRQID NOT LIKE '%/1'	AND	ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -PRQDATE ) < 15	AND SHIPMENTMODE="+shipMode+"  ORDER BY PRQDATE DESC";
				sql2 = " SELECT PRQ_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P"+
						" WHERE"+
						" PRQ_ID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND (LG.ORIG_TRML_ID = '"+terminalId+"' OR LG.DEST_TRML_ID = '"+terminalId+"')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
					     // @@ Anup added on 20050503
                          " AND P.GROUPED='N' "+
				        // @@ 20050503
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PRQDATE DESC";
			 //@@
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId.add(rs.getString(1));
			}

			return	prqId;
		}
		catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if(	rs!=null ) { rs.close();}
				if(	stmt!=null ) { stmt.close();}
				if(	connection!=null ) { connection.close();}
			}
			catch( SQLException	e ){
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

  /**
   *
   * @param terminalId
   * @param searchString
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getHouseDocIds( String	terminalId,String searchString ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  houseId		  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	HOUSEDOCID LIKE '"+searchString+"%' AND ORIGINTERMINAL='"+terminalId+"'	ORDER BY HOUSEDOCID";
			String sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE HOUSEDOCID LIKE '"+searchString+"%' AND ORIGINTERMINAL='"+ terminalId	+"'	ORDER BY HOUSEDOCID	DESC";
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("HOUSEDOCID"));

			}

			return	houseId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

	// B S REDDY
  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getHouseDocumentId(	String terminalId,String shiper,
											String shipmentMode,String searchString,
											ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  houseId		  =	new ArrayList();
		String		  sql1			  =	null;
		String		  sql2			  =	null;
		try
		{

			int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}

			shiper   = shiper.trim();
			connection = getConnection();
			stmt = connection.createStatement();
			String		dateFormat				=	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			//@@ Avinash Commented on 20041016
			//if(shipMode==1 || shipMode==4)
			//@@
			if(shiper.equals("Inbound"))
			{
				//sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	DESTTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - HOUSEDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+" ";
				//@@ Avinash Replaced on 20041016
				//sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE DESTTERMINAL='"+terminalId+"'	AND	HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15  	AND SHIPMENTMODE="+shipMode+"   ORDER BY HOUSEDOCDATE DESC";
				sql2 = " SELECT HAWB_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H"+
						" WHERE"+
						" HAWB_ID LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND H.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -H.HOUSEDOCDATE ) < 15"+
						" ORDER BY HOUSEDOCDATE DESC";
		 //@@
			}
			else
			if(shiper.equals("Outbound"))
			{
				//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ";
				//@@ Avinash Replaced on 20041016
				//sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE ORIGINTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+" ORDER BY	HOUSEDOCDATE DESC";
				sql2 = " SELECT HAWB_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H"+
						" WHERE"+
						" HAWB_ID LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND H.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -H.HOUSEDOCDATE ) < 15"+
						" ORDER BY HOUSEDOCDATE DESC";
		 //@@
			}
			else
			if(shiper.equals("None"))
			{
				//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_HOUSEDOCHDR WHERE (ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"') AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ";
				//@@ Avinash Replaced on 20041016
				//sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE (ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"') AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ORDER BY	HOUSEDOCDATE DESC";
				sql2 = " SELECT HAWB_ID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H"+
						" WHERE"+
						" HAWB_ID LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND (LG.ORIG_TRML_ID = '"+terminalId+"' OR LG.DEST_TRML_ID = '"+terminalId+"')"+
						" AND H.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -H.HOUSEDOCDATE ) < 15"+
						" ORDER BY HOUSEDOCDATE DESC";
		 //@@

			}
			//@@ Avinash Commented on 20041016
			/*
			else if(shipMode == 2)
			{
				if(shiper.equals("Inbound"))
				{
				  // sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	SHIPMENTMODE=2 AND DESTTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - HOUSEDOCDATE ) < 15 	 ";
				   sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE SHIPMENTMODE=2 AND DESTTERMINAL='"+terminalId+"'	AND	HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15  	 ORDER BY HOUSEDOCDATE DESC";
				}

				if(shiper.equals("Outbound"))
				{
					//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=2 AND ORIGINTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	  ";
					sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE SHIPMENTMODE=2 AND ORIGINTERMINAL='"+terminalId+"' AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	 ORDER BY	HOUSEDOCDATE DESC";
				}
				if(shiper.equals("None"))
				{
				   //sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=2 AND (ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"') AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15 	  ";
				   sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE SHIPMENTMODE=2 AND (ORIGINTERMINAL='"+terminalId+"' OR DESTTERMINAL='"+terminalId+"') AND HOUSEDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  HOUSEDOCDATE ) < 15   ORDER BY	HOUSEDOCDATE DESC";

				}
			}
			*/
			//@@ 20041016
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString(1));

			}

			return	houseId;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"  Error in getHouseDocumentId method-->"+e.toString());
      logger.error(FILE_NAME+"  Error in getHouseDocumentId method-->"+e.toString());
			return null;
		}
		finally{
			try{
				if(	rs!=null ){ rs.close();}
				if(	stmt!=null ){ stmt.close();}
				if(	connection!=null ){ connection.close();}
			}
			catch( SQLException	e ){
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

  /**
   *
   * @param terminalId
   * @return houseId
   * @throws javax.ejb.EJBException
   */
public ArrayList	getDOHouseDocumentIds( String terminalId ) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  houseId		  =	null;
		int			  cnt			  = 0;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"'  )";
			String sql2	= 	"SELECT DONO FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"') ORDER BY DONO ";

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("DONO"));

			}

			return	houseId;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"Exception in getting DOId : "+e);
      logger.error(FILE_NAME+"Exception in getting DOId : "+e);
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param searchString
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getDOHouseDocumentIds( String terminalId,String searchString ) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		int			  cnt			  = 0;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			//String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%' AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"'  )";
			String sql2	= 	"SELECT DONO FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%' AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"') ORDER BY DONO ";
      rs = stmt.executeQuery(	sql2 );

			while( rs.next() )
			{
				houseId.add(rs.getString("DONO"));

			}

			return	houseId;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"Exception in getting DOId : "+e);
      logger.error(FILE_NAME+"Exception in getting DOId : "+e);
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
				{	connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

// added by phani for SHIPMENTMODE
  /**
   *
   * @param terminalId
   * @param searchString
   * @param shipmentMode
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getDOHouseDocumentIds( String terminalId,String searchString,int shipmentMode ) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		int			  cnt			  = 0;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			//String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%' AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"'  )";

			// @@ Replaced by Anup on 20050313 for PR-ET-1207-DMAD
			/*
			String sql2	= 	" SELECT DONO FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'"+
							" AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE DESTTERMINAL='"+terminalId+"')"+
							" AND SHIPMENTMODE="+shipmentMode+" ORDER BY DONO ";
			*/
			String sql2	= 	" SELECT DONO"+
							" FROM FS_FR_DELIVERYORDER, FS_RT_PLAN"+
							" WHERE DONO LIKE '"+searchString+"%'"+
							" AND HOUSEDOCNO = HAWB_ID"+
							" AND DEST_TRML_ID='"+terminalId+"'"+
							" AND SHIPMENTMODE="+shipmentMode+" ORDER BY DONO ";
			// @@ PR-ET-1207-DMAD
      rs = stmt.executeQuery(	sql2 );

			while( rs.next() )
			{
				houseId.add(rs.getString("DONO"));

			}

			return	houseId;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"Exception in getting DOId : "+e);
      logger.error(FILE_NAME+"Exception in getting DOId : "+e);
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

  /**
   *
   * @param terminalId
   * @param searchString
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getMasterdocIds( String	terminalId,String searchString ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  masterId		  =	new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT DISTINCT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+ terminalId	+"'	ORDER BY MASTERDOCID";
			String sql2	= "SELECT DISTINCT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+	terminalId +"' ORDER BY	MASTERDOCID	DESC";
      rs = stmt.executeQuery(	sql2 );









			while( rs.next() )
			{
				masterId.add(rs.getString("MASTERDOCID"));

			}

			return	masterId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

	// Added by Chandu

  /**
   *
   * @param terminalId
   * @param searchString
   * @param destinationId
   * @param fromDate
   * @param toDate
   * @param loginbean
   * @param shipmentMode
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public ArrayList getMasterdocIds( String	terminalId,String searchString ,String destinationId,String fromDate,String toDate,ESupplyGlobalParameters loginbean,String shipmentMode) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  masterId		  =	new ArrayList();

		String 		  sql1			  = null;
		String		  sql2			  = null;

		try
		{
			fromDate = fromDate+" 00:00";
			toDate   = toDate+" 23:59";
			String	dateFormat	=	loginbean.getUserPreferences().getDateFormat();
			connection = getConnection();
			stmt = connection.createStatement();
			if(destinationId != null)
			{
			 	// sql1	= "SELECT DISTINCT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE MASTERDOCID LIKE '"+searchString+"%' AND ORIGINGATEWAYID='"+ terminalId	+"' AND DESTINATIONGATEWAYID = '"+destinationId+"' AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCDATE BETWEEN TO_DATE('"+fromDate+"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate +"','"+dateFormat+" HH24:MI')	ORDER BY MASTERDOCID";

				 sql2	= "SELECT DISTINCT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND ORIGINGATEWAYID='"+	terminalId +"' AND DESTINATIONGATEWAYID = '"+destinationId+"' AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCDATE BETWEEN TO_DATE('"+fromDate +"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate +"','"+dateFormat+" HH24:MI') ORDER BY	MASTERDOCID	DESC";
			}
			else
			{
			 	// sql1	= "SELECT DISTINCT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE MASTERDOCID LIKE '"+searchString+"%' AND ORIGINGATEWAYID='"+ terminalId	+"'  AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCDATE BETWEEN TO_DATE('"+fromDate +"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate +"','"+dateFormat+" HH24:MI')	ORDER BY MASTERDOCID";

				 sql2	= "SELECT DISTINCT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND ORIGINGATEWAYID='"+	terminalId+"' AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCDATE BETWEEN TO_DATE('"+fromDate +"','"+dateFormat+" HH24:MI') AND TO_DATE('"+toDate +"','"+dateFormat+" HH24:MI') ORDER BY	MASTERDOCID	DESC";
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId.add(rs.getString("MASTERDOCID"));

			}

			return	masterId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}



// added by B S REDDY
  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getMasterDocId(	String terminalId,String shiper,String shipmentMode,String searchString,ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  masterId		  =	new ArrayList();
		String		  sql1			   = null;
		String		  sql2			   = null;

		try
		{
			 int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}


			connection = getConnection();
			stmt = connection.createStatement();
			String	dateFormat			=	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			if(shipMode==1 || shipMode==4)
			{
					if(shiper.equals("Inbound"))
					{
						//sql1 = "SELECT COUNT(MASTERDOCID)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE DESTINATIONGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  - MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE DESTINATIONGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  - MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+"  ORDER BY MASTERDOCDATE DESC";
					}

					 if(shiper.equals("Outbound"))
					{
						//sql1 = "SELECT COUNT(MASTERDOCID)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE ORIGINGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID='"+ terminalId	+"'	AND	MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ORDER BY	MASTERDOCDATE DESC";
					}
					if(shiper.equals("None"))
					{
						//sql1 = "SELECT COUNT(MASTERDOCID)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE ( ORIGINGATEWAYID='"+ terminalId +"' OR DESTINATIONGATEWAYID='"+ terminalId +"') AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE ( ORIGINGATEWAYID='"+ terminalId +"' OR DESTINATIONGATEWAYID='"+ terminalId +"') AND	MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ORDER BY	MASTERDOCDATE DESC";
					}
			}
			else if(shipMode == 2)
			{
					if(shiper.equals("Inbound"))
					{
					//	sql1 = " SELECT COUNT(A.CONSOLEID)	NO_ROWS	  FROM "+
							//	"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C "+
							//	" WHERE    B.ROUTEID=A.ROUTEID AND  C.ROUTEID=B.ROUTEID AND C.DESTTERMINAL='"+ terminalId +"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15" ;


						sql2 = " SELECT DISTINCT A.CONSOLEID  FROM "+
								"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C "+
								" WHERE   B.ROUTEID=A.ROUTEID AND  C.ROUTEID=B.ROUTEID AND  C.DESTTERMINAL='"+ terminalId +"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15" ;
					}
					if(shiper.equals("Outbound"))
					{
						//sql1 = "SELECT  COUNT(A.CONSOLEID)	NO_ROWS	FROM "+
							///	"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B "+
							//	" WHERE  B.ROUTEID=A.ROUTEID AND  B.ORIGINGATEWAY "+
							//	"  IN(SELECT GATEWAYID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+terminalId+"') AND	CONSOLEID LIKE '"+searchString+"%'  AND "+
						//		" ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15";

						sql2 = "SELECT DISTINCT A.CONSOLEID FROM "+
								"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B "+
								" WHERE  B.ROUTEID=A.ROUTEID AND  B.ORIGINGATEWAY "+
								"  IN(SELECT GATEWAYID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+terminalId+"') AND	CONSOLEID LIKE '"+searchString+"%'  AND "+
								" ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15";
					}

			}

			//System.out.println("sql 2	=	"+sql2);

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId.add(rs.getString(1));

			}

			return	masterId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @return doId
   * @throws javax.ejb.EJBException
   */
	public String[]	getDeliveryOrderId( String	terminalId ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  doId		  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND H.TERMINALID='"+terminalId+"' ORDER BY H.HOUSEDOCID";
			String sql2	= "SELECT H.HOUSEDOCID HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE	H.HOUSEDOCID=D.HOUSEDOCNO AND H.TERMINALID='"+terminalId+"'	ORDER BY H.HOUSEDOCID DESC";
			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}

			doId = new String[cnt];
			int	row	=0;

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId[row] =	rs.getString("HOUSEDOCID");
				row++;
			}

			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @return doId
   * @throws javax.ejb.EJBException
   */
	public String[]	getDeliveryOrderIds( String	terminalId ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  doId		  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			String sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"'  )";
			String sql2= 	"SELECT DONO FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"' )";

			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}

			doId = new String[cnt];
			int	row	=0;

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId[row] =	rs.getString("DONO");
				row++;
			}

			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	 // added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param searchString
   * @return doId
   * @throws javax.ejb.EJBException
   */
	 public ArrayList	getDeliveryOrderIds( String	terminalId,String searchString ) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  doId		  =	new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			//String sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%' AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"'  )";
			String sql2= 	"SELECT DONO FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%' AND HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"' )";









			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId.add(rs.getString("DONO"));

			}

			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public String[] getCargoManifestIds( String terminalId ,String shipmentMode ) throws javax.ejb.EJBException
	{
        Connection    	connection      	= 	null;
    	Statement     	stmt            	= 	null;
        ResultSet     	rs              	= 	null;
        String[]	  	masterId		  	= 	null;
		String[]	  	receiptStatus	  	= 	null;
		String[]		strReturn			=	null;

        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection 	= 	getConnection();
	        stmt 		= 	connection.createStatement();
			String sql1 = 	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+terminalId+"' AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE IS NULL ORDER BY MASTERDOCID";
			String sql2 = 	"SELECT MASTERDOCID,STATUS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+ terminalId +"' AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE IS NULL ORDER BY MASTERDOCID DESC";

	        rs = stmt.executeQuery( sql1 );
	        rs.next();
	        int cnt = rs.getInt("NO_ROWS");
			if( cnt==0 )
				{return null;}

	        masterId 		= 	new String[cnt];
			receiptStatus	=	new String[cnt];
			strReturn		=	new String[cnt];

	        int row =0;

	        rs = stmt.executeQuery( sql2 );

	        while( rs.next() )
        	{
        		masterId[row] 		= 	rs.getString("MASTERDOCID");
				receiptStatus[row] 	= 	rs.getString("STATUS");
				strReturn[row]		=	masterId[row] + "&" + receiptStatus[row];

        		row++;
        	}

			return  strReturn;
		}
		catch( SQLException e )
		{
			//Logger.error(FILE_NAME,"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
					{rs.close();}
				if( stmt!=null )
					{stmt.close();}
				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @param searchString
   * @param terminalType
   * @return strReturn
   * @throws javax.ejb.EJBException
   */
	public ArrayList getCargoManifestIds( String terminalId ,String shipmentMode,String searchString,String terminalType ) throws javax.ejb.EJBException
	{
        Connection    	connection      	= 	null;
        Statement     	stmt            	= 	null;
        ResultSet     	rs              	= 	null;
       // String[]	  	masterId		  	= 	null;
    		//String[]	  	receiptStatus	  	= 	null;
        ArrayList	strReturn			=	new ArrayList();

		//String sql1 = "";
		String sql2 = "";

        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection 	= 	getConnection();
	        stmt 		= 	connection.createStatement();

			if(terminalType != null && terminalType.equalsIgnoreCase("NSIB"))
			{
				 //sql1 = 	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND DESTINATIONGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE IS NULL ORDER BY MASTERDOCID";
				 sql2 = 	"SELECT MASTERDOCID,STATUS FROM FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+ terminalId +"' AND DESTINATIONGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE IS NULL ORDER BY MASTERDOCID DESC";
			}
			else
			{
				// sql1 = 	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND ORIGINGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE IS NULL ORDER BY MASTERDOCID";
				 sql2 = 	"SELECT MASTERDOCID,STATUS FROM FS_FR_MASTERDOCHDR WHERE MASTERDOCID LIKE '"+searchString+"%' AND TERMINALID='"+ terminalId +"' AND ORIGINGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipMode+" AND MASTERTYPE !='D'   ORDER BY MASTERDOCID DESC";
			}
		      rs = stmt.executeQuery( sql2 );
		    while( rs.next() )
        	{
    				strReturn.add(rs.getString("MASTERDOCID")	 + "&" + rs.getString("STATUS"));
        	}

			return  strReturn;
		}
		catch( SQLException e )
		{
			//Logger.error(FILE_NAME,"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
					{rs.close();}
				if( stmt!=null )
					{stmt.close();}
				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList getHouseDocumentIds( String terminalId ,String shipmentMode) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList	  houseId		  = new ArrayList();

        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection = getConnection();
	        stmt = connection.createStatement();
			//String sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+terminalId+"' AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID";
			String sql2 = "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE ORIGINTERMINAL='"+ terminalId +"' AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID DESC";
		      rs = stmt.executeQuery( sql2 );










	        while( rs.next() )
        	{
        		houseId.add(rs.getString("HOUSEDOCID"));

        	}

			return  houseId;
		}
		catch( SQLException e )
		{
			return null;
//			throw new EJBException("getMasterdocIds::..."+e.toString());
		}
		catch( Exception e )
		{
			return null;
//			throw new EJBException("getMasterdocIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if( rs!=null )
					{rs.close();}
				if( stmt!=null )
					{stmt.close();}
				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @param searchString
   * @param terminalType
   * @return houseId
   * @throws javax.ejb.EJBException
   */
	public ArrayList getHouseDocumentIds( String terminalId ,String shipmentMode,String searchString,String terminalType) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	Statement     stmt            = null;
        ResultSet     rs              = null;
       ArrayList	  houseId		  = new ArrayList();

        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection = getConnection();
	        stmt = connection.createStatement();
			String sql1 ="", sql2 ="";
			if(terminalType.equalsIgnoreCase("NSIB"))
			{
				 //sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND  ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N') AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID";
				 sql2 = "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '"+searchString+"%' AND TERMINALID='"+ terminalId +"' AND ORIGINTERMINAL IN (SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALTYPE='N')  AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID DESC";
			}
			else
			{
				 //sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '"+searchString+"%' AND ORIGINTERMINAL='"+terminalId+"' AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID";
				 sql2 = "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE HOUSEDOCID LIKE '"+searchString+"%' AND ORIGINTERMINAL='"+ terminalId +"' AND SHIPMENTMODE="+shipMode +" ORDER BY HOUSEDOCID DESC";

			}









	        rs = stmt.executeQuery( sql2 );
	        while( rs.next() )
        	{
        		houseId.add(rs.getString("HOUSEDOCID"));
            //System.out.println("houseId : "+houseId);

        	}

			return  houseId;
		}
		catch( SQLException e )
		{
			return null;
//			throw new EJBException("getMasterdocIds::..."+e.toString());
		}
		catch( Exception e )
		{
			return null;
//			throw new EJBException("getMasterdocIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if( rs!=null )
					{rs.close();}
				if( stmt!=null )
					{stmt.close();}
				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[]	getCargoManifestIds( String	terminalId ) throws	javax.ejb.EJBException
	{
		Connection		connection			=	null;
		Statement		stmt				=	null;
		ResultSet		rs					=	null;
		String[]		masterId			=	null;
		String[]		receiptStatus		=	null;
		String[]		strReturn			=	null;

		try
		{
			connection	=	getConnection();
			stmt		=	connection.createStatement();
			String sql1	=	"SELECT	COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+terminalId+"' ORDER BY	MASTERDOCID";
			String sql2	=	"SELECT	MASTERDOCID,STATUS FROM	FS_FR_MASTERDOCHDR WHERE TERMINALID='"+	terminalId +"' ORDER BY	MASTERDOCID	DESC";

			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}
			masterId		=	new	String[cnt];
			receiptStatus	=	new	String[cnt];
			strReturn		=	new	String[cnt];
			int	row	=0;
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId[row]		=	rs.getString("MASTERDOCID");
				receiptStatus[row]	=	rs.getString("STATUS");
				strReturn[row]		=	masterId[row] +	"&"	+ receiptStatus[row];
				row++;
			}
			return	strReturn;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"SQL	Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"SQL	Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param strIdx
   * @param mode
   * @param searchString
   * @return invoiceId
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getInvoiceIds( String terminalId, String strIdx,int mode,String searchString) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;

		String		  typeQry		  = null;
		ArrayList	  invoiceId		  =	new ArrayList();



		if(searchString != null && searchString.length() > 0)
		{
			searchString=" AND M.FRTINVOICEID LIKE '"+searchString+"%' ";
		}else
		{
			searchString=" ";
		}
		if(strIdx.equals("S")){
			typeQry		=	"M.STATUS='Origin' AND DTL.PAYAT='Origin'";
		}
		else if(strIdx.equals("DT")){
			  // @@ Replaced by Anup on 20050324
			  //typeQry		=	"M.STATUS='Origin' AND DTL.PAYAT='Destination'";
				typeQry		=	" DTL.PAYAT='Destination' ";
			  // @@ 20050324
		}
		else if(strIdx.equals("C"))
			{typeQry		=	"M.STATUS='Destination' AND DTL.PAYAT='Origin'";}
		else if(strIdx.equals("OT"))
			{typeQry		=	"M.STATUS='Destination' AND DTL.PAYAT='Destination'";}


		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
//			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_FRTINVOICEMASTER M,FS_FR_FRTINVOICEDTL DTL WHERE TERMINALID='"+terminalId+"' AND  "+typeQry+"  AND M.FRTINVOICEID=DTL.FRTINVOICEID AND SHIPMENTMODE="+mode+searchString;
//			String sql2	= "SELECT FRTINVOICEID FROM	FS_FR_FRTINVOICEMASTER WHERE TERMINALID='"+terminalId+"' AND 						   FRTINVOICEID LIKE '%/"+ strIdx+"%'	AND SHIPMENTMODE="+mode+searchString+" ORDER BY 						   FRTINVOICEID DESC";
			String sql2	= "SELECT DISTINCT M.FRTINVOICEID FROM FS_FR_FRTINVOICEMASTER M,FS_FR_FRTINVOICEDTL DTL WHERE TERMINALID='"+terminalId+"' AND  "+typeQry+"	AND M.FRTINVOICEID=DTL.FRTINVOICEID AND SHIPMENTMODE="+mode+searchString+" ORDER BY FRTINVOICEID DESC";










			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId.add(rs.getString(1));

			}

			return	invoiceId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param strIdx
   * @param searchString
   * @return invoiceId
   * @throws javax.ejb.EJBException
   */
public ArrayList	getInvoiceIds( String terminalId, String strIdx,String searchString	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  invoiceId		  =	new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx	+"%' ORDER BY FRTINVOICEID";
			String sql2	= "SELECT FRTINVOICEID FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx+"%'	ORDER BY FRTINVOICEID DESC";








			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId.add(rs.getString("FRTINVOICEID"));

			}

			return	invoiceId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}


  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @param terminalType
   * @return masterId
   * @throws javax.ejb.EJBException
   */
	public String[] getMasterDocIds( String terminalId ,String shipmentMode,String terminalType) throws javax.ejb.EJBException
	{
        Connection    	connection      	= 	null;
    	Statement     	stmt            	= 	null;
        ResultSet     	rs              	= 	null;
        String[]	  	masterId		  	= 	null;
		String[]	  	receiptStatus	  	= 	null;
		String[]		strReturn			=	null;
		String sql1		= "";
		String sql2		= "";

        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection 	= 	getConnection();
	        stmt 		= 	connection.createStatement();

			if(terminalType != null && terminalType.equalsIgnoreCase("NSIB"))
			{
				 sql1 = 	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+terminalId+"' AND DESTINATIONGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+"  ORDER BY MASTERDOCID";
				 sql2 = 	"SELECT MASTERDOCID,STATUS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+ terminalId +"' AND DESTINATIONGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+" ORDER BY MASTERDOCID DESC";
			}
			else
			{
                                 // Modified by Ravi on 26-April-2005
				 sql1 = 	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+terminalId+"' AND ORIGINGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+" AND (MASTERTYPE !='D' OR MASTERTYPE IS NULL) ORDER BY MASTERDOCID";
				 sql2 = 	"SELECT MASTERDOCID,STATUS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='"+ terminalId +"' AND ORIGINGATEWAYID = '"+terminalId+"'  AND SHIPMENTMODE="+shipMode+"  AND (MASTERTYPE !='D' OR MASTERTYPE IS NULL) ORDER BY MASTERDOCID DESC";

			}



	        rs = stmt.executeQuery( sql1 );
	        rs.next();
	        int cnt = rs.getInt("NO_ROWS");
			if( cnt==0 )
				{return null;}

	        masterId 		= 	new String[cnt];
			receiptStatus	=	new String[cnt];
			strReturn		=	new String[cnt];

	        int row =0;

	        rs = stmt.executeQuery( sql2 );

	        while( rs.next() )
        	{
        		masterId[row] 		= 	rs.getString("MASTERDOCID");
				receiptStatus[row] 	= 	rs.getString("STATUS");
				strReturn[row]		=	masterId[row] + "&" + receiptStatus[row];

        		row++;
        	}

			return  strReturn;
		}
		catch( SQLException e )
		{
			//Logger.error(FILE_NAME,"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"SQL Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
      logger.error(FILE_NAME+"Exception in 'getCargoManifestIds' of OperationsImpl : " + e.toString());
			return null;
		}
		finally
		{
			try
			{
				if( rs!=null )
					{rs.close();}
				if( stmt!=null )
					{stmt.close();}
				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
	// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param searchString
   * @param terminalType
   * @param loginbean
   * @param shipmentMode
   * @return masterIds
   * @throws javax.ejb.EJBException
   */
	public ArrayList getMasterIdsForPrint( String terminalId ,String searchString,String terminalType,ESupplyGlobalParameters loginbean, String shipmentMode) throws javax.ejb.EJBException
	{
		Connection			connection      =	null;
    Statement			stmt            =	null;
    //ResultSet			rs              =	null,rs1=null;;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
    ResultSet			rs1=null;;
		ArrayList			masterIds	    =	new ArrayList();
    String				sql1			=	null;
		String				sql2			=	null;
		int					cnt				=	0;
		int					row				=	0;

		if(shipmentMode.equals("Air"))
			{shipmentMode="1";}
		else if(shipmentMode.equals("Sea"))
			{shipmentMode="2";}
		else
			shipmentMode="4";


        try
       	{
			connection 			=	getConnection();
	        stmt				=	connection.createStatement();
			String dateFormat	=	loginbean.getUserPreferences().getDateFormat();
			String converTime	=	this.getLocalTime(terminalId,dateFormat);

			if(terminalType != null && terminalType.equalsIgnoreCase("NSIB"))
			{
				//sql1			=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='" + terminalId + "'   AND DESTINATIONGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipmentMode+"  AND MASTERDOCID LIKE '"+searchString+"%' AND MASTERTYPE = 'D' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')-MASTERDOCDATE)<=15 ORDER BY MASTERDOCID";
				sql2			=	"SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='" + terminalId + "'  AND DESTINATIONGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipmentMode+"  AND MASTERDOCID LIKE '"+searchString+"%' AND MASTERTYPE = 'D' AND ROUND(SYSDATE-MASTERDOCDATE)<=15 ORDER BY MASTERDOCID";
			}
			else
			{
			//	sql1			=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='" + terminalId + "' AND ORIGINGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCID LIKE '"+searchString+"%' AND MASTERTYPE = 'D' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')-MASTERDOCDATE)<=15 ORDER BY MASTERDOCID";
				sql2			=	"SELECT MASTERDOCID FROM FS_FR_MASTERDOCHDR WHERE TERMINALID='" + terminalId + "' AND ORIGINGATEWAYID = '"+terminalId+"' AND SHIPMENTMODE="+shipmentMode+" AND MASTERDOCID LIKE '"+searchString+"%' AND MASTERTYPE = 'D' AND ROUND(SYSDATE-MASTERDOCDATE)<=15 ORDER BY MASTERDOCID";
			}
	     		rs1				=	stmt.executeQuery( sql2 );




	        while( rs1.next() )
        	{
        		masterIds.add(rs1.getString(1));

        	}
		}
		catch( SQLException e )
		{
			//Logger.error(FILE_NAME, "SQLException in 'getMasterIds' of Operation Impl.java : " + e.toString() );
      logger.error(FILE_NAME+ "SQLException in 'getMasterIds' of Operation Impl.java : " + e.toString() );
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME, "Exception in 'getMasterIds' of Operation Impl.java  : " + e.toString() );
      logger.error(FILE_NAME+ "Exception in 'getMasterIds' of Operation Impl.java  : " + e.toString() );
			return null;
		}
		finally
		{
			try
			{
				/*if( rs!=null )
					{rs.close();}*///Commented By RajKumari on 23-10-2008 for Connection Leakages.
        if( rs1!=null )
					{rs1.close();}
				if( stmt!=null )
					{stmt.close();}

				if( connection!=null )
					{connection.close();}
			}
			catch( SQLException e )
			{
	            //Logger.error(FILE_NAME,"Exception in finally of 'getMasterIds' of Operation Impl.java  : " + e.toString() );
              logger.error(FILE_NAME+"Exception in finally of 'getMasterIds' of Operation Impl.java  : " + e.toString() );
			}
		}
		return  masterIds;
	} //getMasterIds
	//end here

// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return poId
   * @throws javax.ejb.EJBException
   */
	public ArrayList getPurchaseOrderId( String terminalId,String shiper,
										String shipmentMode,String searchString,
										ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList      poId			  = new ArrayList();
        String        sql1            = null;
		String        sql2            = null;

        try
       	{

			 int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}


       		connection = getConnection();
	        stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
	   		String converTime = this.getLocalTime(terminalId,dateFormat);

			if(shiper.equals("Inbound"))
			{
				//sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.POID ) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
				 //  "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.POID PID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
				//"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL ORDER BY FS_FR_PRQINVOICEPARTDTL.POID";
				sql2 =  " SELECT DISTINCT PARTDTL.POID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.POID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.POID";
				//@@
			}
			else if(shiper.equals("CRM"))
			{
				//sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.POID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					  // "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.shipperid='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL ";

				sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.POID PID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					   "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.shipperid='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL ORDER BY FS_FR_PRQINVOICEPARTDTL.POID";
			}
			else
			{
				//sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.POID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					  // "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL ";

				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.POID PID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					   //"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.POID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.POID IS NOT NULL ORDER BY FS_FR_PRQINVOICEPARTDTL.POID";
				sql2 =  " SELECT DISTINCT PARTDTL.POID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.POID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.POID";
				//@@
			}

	        rs = stmt.executeQuery( sql2 );

	        while( rs.next() )
        	{
        	   poId.add(rs.getString(1));

        	}
			//Logger.info(FILE_NAME,"Nof of rows at the end is " + row);
			return  poId;
		}

		catch( SQLException e ){
			//Logger.error(FILE_NAME,"Exception in getPurhaseOrderId() "+e);
      logger.error(FILE_NAME+"Exception in getPurhaseOrderId() "+e);
			e.printStackTrace();
			return null;
		}
		catch( Exception e ){
			//Logger.error(FILE_NAME,"Exception in getPurhaseOrderId() "+e);
      logger.error(FILE_NAME+"Exception in getPurhaseOrderId() "+e);
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if( rs!=null ){ rs.close();}
				if( stmt!=null ) {stmt.close();}
				if( connection!=null ) {connection.close();}
			}
			catch( SQLException e ){
				throw new EJBException("gePuchaseOrderIds:finally::..."+e.toString());
			}
		}
	}



// added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return invId
   * @throws javax.ejb.EJBException
   */
//Commented  By G.Srinivas to resolve the QA-Issue No:SPETI-6206 on 20050420.
/*public ArrayList getShipperInvoiceId(	String terminalId,String shiper ,
										String shipmentMode,String searchString,
										ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList      invId	      = new ArrayList();
       	String        sql2            = null;
		String	      sql1	      = null;
        try
       	{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection = getConnection();
	        stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId);
			if(shiper.equals("Inbound"))
			{
			   //sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.INVOICEID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					  // "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.INVOICEID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND  FS_FR_PRQINVOICEPARTDTL.INVOICEID IS NOT NULL ";
				//@@ Avinash Replaced on 20041016
				// sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.INVOICEID INVID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
						  //"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.INVOICEID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND  FS_FR_PRQINVOICEPARTDTL.INVOICEID IS NOT NULL  ORDER BY FS_FR_PRQINVOICEPARTDTL.INVOICEID";
				sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.INVOICEID";
				//@@
			}
			else
			{
			//	sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.INVOICEID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					 //  "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.INVOICEID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND  FS_FR_PRQINVOICEPARTDTL.INVOICEID IS NOT NULL  ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.INVOICEID INVID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					     //"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.INVOICEID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND  FS_FR_PRQINVOICEPARTDTL.INVOICEID IS NOT NULL  ORDER BY FS_FR_PRQINVOICEPARTDTL.INVOICEID";
				sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID  = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.INVOICEID";
				//@@
			}
		      rs = stmt.executeQuery( sql2 );

			while( rs.next() )
        	{
        	 if(rs.getString("INVOICEID") != null)
        	  {
        		invId.add(rs.getString("INVOICEID"));

        	  }
        	}

			return  invId;
		}
		catch( Exception e ){
			Logger.error(FILE_NAME,"Exception in getInvoiceId() "+e);
			return null;
		}
		finally{
			try{
				if( rs!=null ) {rs.close();}
				if( stmt!=null ){ stmt.close();}
				if( connection!=null ) {connection.close();}
			}
			catch( SQLException e ){
				throw new EJBException("getInvoiceId() : finally : "+e.toString());
			}
		}
	}
// added by B S REDDY for Search String
*/
//Commented above method and added new method By G.Srinivas to resolve the QA-Issue No:SPETI-6206 on 20050420.
public ArrayList getShipperInvoiceId(	String terminalId,String shiper ,
										String shipmentMode,String searchString,
										ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	PreparedStatement	pstmt		   = null;
		ResultSet     rs          = null;
        ArrayList     invId	      = new ArrayList();
       	String        sql2        = null;

		try
       	{
//Logger.info(FILE_NAME,"In Try      @@@@@@@@@@@@@@@@:");
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

       		connection = getConnection();
	      	String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			Timestamp converTime = loginbean.getLocalTime();

			// @@ Added by Anup on 20050520
                 String  onlyDate = converTime.toString().substring(0,converTime.toString().indexOf(" "));
			// @@ 20050520

            // Logger.info(FILE_NAME,"Anup ==========================================>>>> onlyDate :"+sql2);
			if(shiper.equals("Inbound"))
			{//Logger.info(FILE_NAME,"1111111111111111111111111111@@@@@@@@@@@@@@@@ inbound:");
			   	sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE ?"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = ?"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = ?"+
                            // @@ Replaced by Anup on 20050520
						    //" AND ROUND(? -P.PRQDATE ) < 15"+
							" AND ROUND(to_date(?,'YYYY-MM-DD')  -to_date(P.PRQDATE,'DD-MM-YY')) < 15"+
							// @@ 20050520
						" ORDER BY PARTDTL.INVOICEID";

			}
			else
			{//Logger.info(FILE_NAME,"1111111111111111111111111111@@@@@@@@@@@@@@@@ outbound:");
				sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE ?"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID  = ?"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = ?"+
						// @@ Replaced by Anup on 20050520
						    //" AND ROUND(? -P.PRQDATE ) < 15"+
							" AND ROUND(to_date(?,'YYYY-MM-DD')  -to_date(P.PRQDATE,'DD-MM-YY')) < 15"+
							// @@ 20050520
						" ORDER BY PARTDTL.INVOICEID";
			}

			pstmt =	connection.prepareStatement( sql2 );
	//Logger.info(FILE_NAME,"before sql2@@@@@@@@@@@@@@@@:"+sql2);
			pstmt.setString( 1,	searchString+"%");
			pstmt.setString( 2,	terminalId );
			pstmt.setInt( 3,	shipMode	);
			// @@ Replaced by Anup on 20050520
			//pstmt.setTimestamp( 4,	converTime );
			pstmt.setString( 4,	onlyDate );
			// @@ 20050520
			rs = pstmt.executeQuery();
	//Logger.info(FILE_NAME,"after  sql2@@@@@@@@@@@@@@@@:"+sql2);
			while( rs.next() )
        	{
        	 if(rs.getString(1) != null)
        	  {
        		invId.add(rs.getString(1));

        	  }
        	}

			return  invId;
		}
		catch( Exception e ){
			//Logger.error(FILE_NAME,"Exception in getShipperInvoiceId------> "+e);
      logger.error(FILE_NAME+"Exception in getShipperInvoiceId------> "+e);
			return null;
		}
		finally{
			try{
				if( rs!=null ) {rs.close();}
				if( pstmt!=null ){ pstmt.close();}
				if( connection!=null ) {connection.close();}
			}
			catch( SQLException e ){
				throw new EJBException("getShipperInvoiceId : finally -------->: "+e.toString());
			}
		}
	}
// added by B S REDDY for Search String

  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return partId
   * @throws javax.ejb.EJBException
   */
	public ArrayList getPartId( String terminalId,String shiper,
								String shipmentMode,String searchString,
								ESupplyGlobalParameters loginbean ) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
    	Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList      partId	      = new ArrayList();
       	String        sql2            = null;
		String	      sql1	      = null;
        try
       	{
			 int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}

       		connection = getConnection();
	        stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);

			if(shiper.equals("Inbound"))
			{
			   //sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.PARTID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					 //  "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.PARTID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.PARTID IS NOT NULL";

				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.PARTID P_ID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
						//"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.PARTID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.PARTID IS NOT NULL ORDER BY FS_FR_PRQINVOICEPARTDTL.PARTID";
				sql2 =  " SELECT DISTINCT PARTDTL.PARTID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.PARTID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.PARTID";
				//@@
			}
			else
			{
				//sql1 = "SELECT COUNT(DISTINCT FS_FR_PRQINVOICEPARTDTL.PARTID) NO_ROWS FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
					 //  "FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.PARTID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.PARTID IS NOT NULL ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT FS_FR_PRQINVOICEPARTDTL.PARTID P_ID FROM FS_FR_PICKUPREQUEST,FS_FR_PRQINVOICEPARTDTL WHERE "+
						 //"FS_FR_PICKUPREQUEST.PRQID=FS_FR_PRQINVOICEPARTDTL.PRQID AND FS_FR_PRQINVOICEPARTDTL.PARTID LIKE '"+searchString+"%' AND FS_FR_PICKUPREQUEST.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - FS_FR_PICKUPREQUEST.PRQDATE ) < 15 AND SHIPMENTMODE = "+shipMode+" AND FS_FR_PRQINVOICEPARTDTL.PARTID IS NOT NULL ORDER BY FS_FR_PRQINVOICEPARTDTL.PARTID";
				sql2 =  " SELECT DISTINCT PARTDTL.PARTID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.PARTID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID  = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = "+shipMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.PARTID";
				//@@
			}

	        rs = stmt.executeQuery( sql2 );
	        while( rs.next() )
        	{
        		if(rs.getString(1)!=null)
        		{
        			partId.add(rs.getString(1));
         		}

        	}

			return  partId;
		}
		catch( SQLException e ){
			//Logger.error(FILE_NAME,"Exception in getPartId() "+e);
      logger.error(FILE_NAME+"Exception in getPartId() "+e);
			e.printStackTrace();
			return null;
		}
		catch( Exception e ){
			//Logger.error(FILE_NAME,"Exception in getPartId() "+e);
      logger.error(FILE_NAME+"Exception in getPartId() "+e);
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if( rs!=null ){ rs.close();}
				if( stmt!=null ){ stmt.close();}
				if( connection!=null ) {connection.close();}
			}
			catch( SQLException e ){
				throw new EJBException("getPartId() : finally : "+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param shiper
   * @param searchString
   * @param loginbean
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getTrackingDeliveryOrderId(	String terminalId,String shiper	,String searchString,ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  doId			  =	new ArrayList();
		String		  sql1			  =	null;
		String		  sql2			  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			if(shiper.equals("Inbound"))
			{
				//sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15 ";

				sql2 = "SELECT D.DONO FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15  ORDER BY H.HOUSEDOCID DESC ";
			}
			else
			{
				//sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15 ";
				sql2 = "SELECT D.DONO FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15  ORDER BY	H.HOUSEDOCID DESC ";
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId.add(rs.getString("DONO"));

			}
			return	doId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getTrackingDeliveryOrderId  ::   "+e.toString());
			}
		}
	}

//added by shekarlal
  /**
   *
   * @param terminalId
   * @param shipmentMode
   * @param shiper
   * @param searchString
   * @param loginbean
   * @return
   * @throws javax.ejb.EJBException
   */
public ArrayList	getTrackingDeliveryOrderId(	String terminalId,String shipmentMode,
												String shiper	,String searchString,
												ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  doId			  =	new ArrayList();
		String		  sql1			  =	null;
		String		  sql2			  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);

			if(shipmentMode!=null &&  !shipmentMode.equals(""))
			{
				if(shipmentMode.equals("Air")){
					{shipmentMode	=	"1";}
				}
				else if(shipmentMode.equals("Sea")){
					{shipmentMode	=	"2";}
				}else if(shipmentMode.equals("Truck")){
					{shipmentMode	=	"4";}
				}

			}
			if(shiper.equals("Inbound"))
			{
				//sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15 ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT D.DONO FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.DESTTERMINAL='"+terminalId+"' "+str_sql_shipmentMode+" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15  ORDER BY H.HOUSEDOCID DESC ";
				sql2 =  " SELECT D.DONO"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H, FS_FR_DELIVERYORDER D"+
						" WHERE"+
						" D.DONO LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = D.HOUSEDOCNO"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND H.SHIPMENTMODE = "+shipmentMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -H.HOUSEDOCDATE ) < 15"+
						" ORDER BY HOUSEDOCID DESC";
		 //@@
			}
			else
			{
				//sql1 = "SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15 ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT D.DONO FROM FS_FR_HOUSEDOCHDR H,FS_FR_DELIVERYORDER D WHERE H.HOUSEDOCID=D.HOUSEDOCNO AND D.DONO LIKE '"+searchString+"%' AND H.ORIGINTERMINAL='"+terminalId+"' "+str_sql_shipmentMode+" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - D.DELIVERYDATEANDTIME ) < 15  ORDER BY	H.HOUSEDOCID DESC ";
				sql2 = " SELECT D.DONO"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_HOUSEDOCHDR H, FS_FR_DELIVERYORDER D"+
						" WHERE"+
						" D.DONO LIKE '"+searchString+"%'"+
						" AND H.HOUSEDOCID = D.HOUSEDOCNO"+
						" AND H.HOUSEDOCID = PL.HAWB_ID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID  = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND H.SHIPMENTMODE = "+shipmentMode+""+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -H.HOUSEDOCDATE ) < 15"+
						" ORDER BY HOUSEDOCID DESC";
				//@@
			}
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId.add(rs.getString("DONO"));
			}
			return	doId;
		}
		catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if(	rs!=null ) {rs.close();}
				if(	stmt!=null ) {stmt.close();}
				if(	connection!=null ){ connection.close();}
			}
			catch( SQLException	e ){
				throw new EJBException("getTrackingDeliveryOrderId  ::   "+e.toString());
			}
		}
	}
 // added by B S REDDY for Search String
  /**
   *
   * @param terminalId
   * @param searchString
   * @param no_value
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getPickUpRequestIds( String	terminalId,String searchString, String no_value) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  prqId			  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_PICKUPREQUEST	WHERE  PRQID LIKE '"+searchString+"%' AND ORIGINTERMINAL='"	+ terminalId +"'  ";
			String sql2	= "SELECT PRQID	FROM FS_FR_PICKUPREQUEST WHERE  PRQID LIKE '"+searchString+"%' AND ORIGINTERMINAL ='" + terminalId +"' ORDER BY PRQID DESC";










			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId.add(rs.getString("PRQID"));

			}

			return	prqId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}


  /**
   *
   * @param terminalId
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[]	getPickUpRequestIds( String	terminalId) throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  prqId			  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_PICKUPREQUEST	WHERE ORIGINTERMINAL='"	+ terminalId +"'  ";
			String sql2	= "SELECT PRQID	FROM FS_FR_PICKUPREQUEST WHERE ORIGINTERMINAL ='" + terminalId +"' ORDER BY PRQID DESC";

			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}

			prqId =	new	String[cnt];
			int	row	=0;

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				prqId[row] = rs.getString("PRQID");
				row++;
			}

			return	prqId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param customerId
   * @param shipmentMode
   * @return
   */
	public ArrayList	getHouseIds( String	customerId,String shipmentMode )
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList  houseId		  =	new ArrayList();
		try
		{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	(SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE="+shipMode+" ORDER BY	HOUSEDOCID";
			String sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE="+shipMode+" ORDER BY	HOUSEDOCID DESC";













			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString("HOUSEDOCID"));

			}
		//	Logger.info(FILE_NAME,"ROW:"+row);
			return	houseId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"getMasterdocIds:finally::..."+e.toString());
        logger.error(FILE_NAME+"getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

// added by B S REDDY for Search String
  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @param terminalId
   * @return
   */
	public ArrayList	getHouseIds( String	customerId,String shipmentMode,String searchString,String terminalId )
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  houseId		  =	new ArrayList();
		String 		  sql1			  = null;
		String 		  sql2 			  = null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();


			if(shipmentMode.equalsIgnoreCase("Air"))
			{
				 //sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	(SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE=1 AND ORIGINTERMINAL='"+terminalId+"' ORDER BY HOUSEDOCID";
				 sql2	=	" SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR"+
							" WHERE (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"')"+
							" AND SHIPMENTMODE=1"+
							//@@ Avinash commented on 20041018
							//" AND ORIGINTERMINAL='"+terminalId+"'"+
							//@@
							" ORDER BY	HOUSEDOCID DESC";
			}
			if(shipmentMode.equalsIgnoreCase("Sea"))
			{
				//sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	SHIPMENTMODE=2 AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"') AND ORIGINTERMINAL='"+terminalId+"' ORDER BY HOUSEDOCID";
				sql2	=	" SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR"+
							" WHERE SHIPMENTMODE=2"+
							" AND (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"')"+
							//@@ Avinash commented on 20041018
							//" AND ORIGINTERMINAL='"+terminalId+"'"+
							//@@
							" ORDER BY HOUSEDOCID DESC";
			}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
			{
				//sql1="SELECT COUNT(*) NO_ROWS FROM FS_FR_HOUSEDOCHDR H WHERE (H.SHIPPERID='" + customerId + "' OR H.CONSIGNEEID='"+customerId+"' "+
				// " OR H.PRQID=(SELECT P.PRQID FROM FS_FR_PICKUPREQUEST P WHERE P.PRQID=H.PRQID AND P.CUSTOMERID='"+customerId+"')) AND SHIPMENTMODE=4 AND ORIGINTERMINAL='"+terminalId+"' ORDER BY HOUSEDOCID";

				sql2	=	" SELECT H.HOUSEDOCID FROM FS_FR_HOUSEDOCHDR H"+
							" WHERE (H.SHIPPERID='" + customerId + "' OR H.CONSIGNEEID='"+customerId+"'" +
										" OR H.PRQID=(SELECT P.PRQID FROM FS_FR_PICKUPREQUEST P"+
													  " WHERE P.PRQID=H.PRQID AND P.CUSTOMERID='"+customerId+"'))"+
							" AND SHIPMENTMODE=4"+
							//@@ Avinash commented on 20041018
							//" AND ORIGINTERMINAL='"+terminalId+"'"+
							//@@
							" ORDER BY HOUSEDOCID";
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId.add(rs.getString(1));

			}
			return	houseId;
		}
		catch( Exception e ){
			//Logger.error(FILE_NAME,"Exception IN getHouseIds(3):	"+e);
      logger.error(FILE_NAME+"Exception IN getHouseIds(3):	"+e);
			return null;
		}
		finally{
			closeConnection(rs,stmt,connection);
		}
	}



  /**
   *
   * @param customerId
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getDOIds( String customerId	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  doId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			//String sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"')  AND SHIPMENTMODE = 1 )";
			String sql2= 	"SELECT DONO  FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"') AND SHIPMENTMODE=1) ORDER BY DONO DESC ";








			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId.add(rs.getString("DONO"));

			}
			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
// End of CRM methods
	 // added by B S REDDY for Search String

  /**
   *
   * @param customerId
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getDOIds( String customerId,String searchString	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  doId		  =	new ArrayList();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			//String sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"')  AND DONO LIKE '"+searchString+"%' AND SHIPMENTMODE=1 )";
			String sql2= 	"SELECT DONO  FROM FS_FR_DELIVERYORDER WHERE HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"') AND DONO LIKE '"+searchString+"%' AND SHIPMENTMODE=1) ORDER BY DONO DESC ";








			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId.add(rs.getString("DONO"));

			}
			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
// End of CRM methods
	// method added by vsreddy

  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[] getCrmDirectMasterDocIds(String customerId,String shipmentMode	,String searchString	) throws javax.ejb.EJBException
	{
		Connection connection	=	null;
		Statement stmt			=	null;
		ResultSet rs			=	null;
		String[] masterDocIds	=	null;
		int cnt	=	0;
		int row = 0;

		try
		{
			connection	= getConnection();
			stmt		= connection.createStatement();
			String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR M,FS_FR_DIRECTMASTERHDR D  WHERE 	M.MASTERDOCID LIKE '"+searchString+"%'  AND M.MASTERDOCID = D.MASTERDOCID AND M.MASTERTYPE = 'D' AND  D.SHIPPERID='"+customerId+"'  ORDER BY M.MASTERDOCID";
			String sqlQuery	=	"SELECT M.MASTERDOCID FROM FS_FR_MASTERDOCHDR M,FS_FR_DIRECTMASTERHDR D  WHERE 	M.MASTERDOCID LIKE '"+searchString+"%'  AND M.MASTERDOCID = D.MASTERDOCID AND M.MASTERTYPE = 'D' AND  D.SHIPPERID='"+customerId+"'    ORDER BY M.MASTERDOCID";
				rs = stmt.executeQuery(sql1);
				if(rs.next())
				{cnt = rs.getInt("NO_ROWS");}

			if(cnt==0)
			{
				return null;
			}

			masterDocIds = 	new String[cnt];
			rs	= stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				masterDocIds[row] = rs.getString(1);
				row++;
			}
			return masterDocIds;
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"the error in getCrmMasterDocIds"+ex.toString());
      logger.error(FILE_NAME+"the error in getCrmMasterDocIds"+ex.toString());
		}

		finally
		{
			try
			{
				if(connection!=null)
					{connection.close();}
				if(stmt!=null);
					{stmt.close();}
				if(rs!=null)
					{rs.close();	}

			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"the error in getCrmMasterDocIds"+ex.toString());
        logger.error(FILE_NAME+"the error in getCrmMasterDocIds"+ex.toString());
			}
		}
		return masterDocIds;
}
//

  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @return
   */
		public String[]	getHouseIds( String	customerId,String shipmentMode ,String searchString)
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  houseId		  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "";
			String sql2	= "";
			if(shipmentMode.equalsIgnoreCase("Air"))
			{
			 sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	HOUSEDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE=1 ORDER BY	HOUSEDOCID";
			 sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	 WHERE HOUSEDOCID LIKE '"+searchString+"%' AND  (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE=1 ORDER BY	HOUSEDOCID DESC";
			}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
			{
			 sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	SHIPMENTMODE=2 AND HOUSEDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"')  ORDER BY	HOUSEDOCID";
			 sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE SHIPMENTMODE=2 AND HOUSEDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"')  ORDER BY	HOUSEDOCID DESC";
			}
			else
			{
			 sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_HOUSEDOCHDR WHERE	HOUSEDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE=4 ORDER BY	HOUSEDOCID";
			 sql2	= "SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR	WHERE  HOUSEDOCID LIKE '"+searchString+"%' AND (SHIPPERID='"+	customerId +"' OR CONSIGNEEID='"+customerId+"') AND SHIPMENTMODE=4 ORDER BY	HOUSEDOCID DESC";
			}

			//Logger.info(FILE_NAME,"SQL2 FROM OPERATIONIMPL :"+sql2);

			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");

			//Logger.info(FILE_NAME,"CNT:"+cnt);

			if(	cnt==0 )
				{return null;}
			houseId	= new String[cnt];
			int	row	=0;
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				houseId[row] = rs.getString(1);
				row++;
			}
			return	houseId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				//Logger.error(FILE_NAME,"getMasterdocIds:finally::..."+e.toString());
        logger.error(FILE_NAME+"getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
		public String[] getCutomerInvoiceIds(String customerId,String shipmentMode,String searchString ) throws javax.ejb.EJBException
	{
		Connection	connection	=	null;
		Statement	stmt		=	null;
		ResultSet	rs 			=	null;
		String[]	invoiceId	=	null;
		try
		{
			connection 	=	getConnection();
			stmt		=	connection.createStatement();
			String sql1=	"";
			String sql2= 	"";
			//Logger.info(FILE_NAME,"the value of sql2"+sql2);
			if(shipmentMode.equalsIgnoreCase("Air"))
			{
			sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_FRTINVOICEMASTER WHERE  FRTINVOICEID LIKE '"+searchString+"%'  AND   BILLTO='"+customerId+"' AND SHIPMENTMODE=1 ORDER BY FRTINVOICEID";
			 sql2	=	"SELECT FRTINVOICEID FROM FS_FR_FRTINVOICEMASTER WHERE   FRTINVOICEID LIKE '"+searchString+"%' AND BILLTO='"+customerId+"' AND SHIPMENTMODE=1 ORDER BY FRTINVOICEID DESC";
			}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
			{
			sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_FRTINVOICEMASTER WHERE  FRTINVOICEID LIKE '"+searchString+"%' AND BILLTO='"+customerId+"'  AND SHIPMENTMODE=2 ORDER BY FRTINVOICEID";
			 sql2	=	"SELECT FRTINVOICEID FROM FS_FR_FRTINVOICEMASTER WHERE  FRTINVOICEID LIKE '"+searchString+"%' AND BILLTO='"+customerId+"' AND SHIPMENTMODE=2 ORDER BY FRTINVOICEID DESC";
				}
			else
			{
			sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_FRTINVOICEMASTER WHERE  FRTINVOICEID LIKE '"+searchString+"%' AND  BILLTO='"+customerId+"' AND SHIPMENTMODE=4 ORDER BY FRTINVOICEID";
			 sql2	=	"SELECT FRTINVOICEID FROM FS_FR_FRTINVOICEMASTER WHERE  FRTINVOICEID LIKE '"+searchString+"%' AND  BILLTO='"+customerId+"' AND SHIPMENTMODE=4 ORDER BY FRTINVOICEID DESC";

			}

			rs			=	stmt.executeQuery(sql1);
			rs.next();
			int cnt		=	rs.getInt("NO_ROWS");

			if(cnt==0)
				{return null;}
			invoiceId	=	new String[cnt];
			int row 	=	0;
			rs			=	stmt.executeQuery(sql2);
			while(rs.next())
			{
				invoiceId[row]	=	rs.getString("FRTINVOICEID");
				row++;
			}
			return	invoiceId;
		}
		catch(SQLException e)
		{
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null)
					{rs.close();}
				if(stmt!=null)
					{stmt.close();}
				if(connection!=null)
					{connection.close();}
			}
			catch(SQLException e)
			{
				throw new EJBException("getCustomerInvoiceIds:finally::..."+e.toString());
			}
		}
	}

// End of CRM methods


  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
public String[]	getDOIds( String customerId	,String shipmentMode	,String searchString	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  doId		  =	null;
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();

			String sql1=	"";
			String sql2= 	"";
			//Logger.info(FILE_NAME,"the value of sql2"+sql2);
			if(shipmentMode.equalsIgnoreCase("Air"))
			{
			 sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"')  AND SHIPMENTMODE=1 )";
			 sql2= 	"SELECT DONO  FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"') AND SHIPMENTMODE=1) ORDER BY DONO DESC ";
			}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
			{
			 sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=2 AND (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"')   )";
			 sql2= 	"SELECT DONO  FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE SHIPMENTMODE=2 AND (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"') ) ORDER BY DONO DESC ";
			}
			else
			{
			 sql1=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"')  AND SHIPMENTMODE=4 )";
			 sql2= 	"SELECT DONO  FROM FS_FR_DELIVERYORDER WHERE DONO LIKE '"+searchString+"%'  AND  HOUSEDOCNO IN (SELECT HOUSEDOCID FROM FS_FR_HOUSEDOCHDR WHERE (SHIPPERID='"+ customerId +"' OR CONSIGNEEID='"+	customerId +"') AND SHIPMENTMODE=4) ORDER BY DONO DESC ";

			}
			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}
			doId = new String[cnt];
			//Logger.info(FILE_NAME,"the value of doId"+doId);

			int	row	=0;
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				doId[row] =	rs.getString("DONO");
				row++;
			}
			return	doId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
				{	rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

	// method added by vsreddy

  /**
   *
   * @param customerId
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[] getCrmDirectMasterDocIds(String customerId) throws javax.ejb.EJBException
	{
		Connection connection	=	null;
		Statement stmt			=	null;
		ResultSet rs			=	null;
		String[] masterDocIds	=	null;
		int cnt	=	0;
		int row = 0;

		try
		{
			connection	= getConnection();
			stmt		= connection.createStatement();
			String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_MASTERDOCHDR M,FS_FR_DIRECTMASTERHDR D  WHERE M.MASTERDOCID = D.MASTERDOCID AND M.MASTERTYPE = 'D' AND D.SHIPPERID='"+customerId+"' ORDER BY M.MASTERDOCID";
			String sqlQuery	=	"SELECT M.MASTERDOCID FROM FS_FR_MASTERDOCHDR M,FS_FR_DIRECTMASTERHDR D  WHERE M.MASTERDOCID = D.MASTERDOCID AND M.MASTERTYPE = 'D' AND D.SHIPPERID='"+customerId+"' ORDER BY M.MASTERDOCID";
				rs = stmt.executeQuery(sql1);
				if(rs.next())
				{cnt = rs.getInt("NO_ROWS");}

			if(cnt==0)
			{
				return null;
			}

			masterDocIds = 	new String[cnt];
			rs	= stmt.executeQuery(sqlQuery);
			while(rs.next())
			{
				masterDocIds[row] = rs.getString(1);
				row++;
			}
			return masterDocIds;
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"the error in getCrmMasterDocIds"+ex.toString());
      logger.error(FILE_NAME+"the error in getCrmMasterDocIds"+ex.toString());
		}

		finally
		{
			try
			{
				if(connection!=null)
				{	connection.close();}
				if(stmt!=null);
					{stmt.close();}
				if(rs!=null)
					{rs.close();	}

			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"the error in getCrmMasterDocIds"+ex.toString());
        logger.error(FILE_NAME+"the error in getCrmMasterDocIds"+ex.toString());
			}
		}
		return masterDocIds;
}
//

  /**
   *
   * @param customerId
   * @param shipmentMode
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[] getCutomerInvoiceIds(String customerId,String shipmentMode ) throws javax.ejb.EJBException
	{
		Connection	connection	=	null;
		Statement	stmt		=	null;
		ResultSet	rs 			=	null;
		String[]	invoiceId	=	null;
		try
		{
			int shipMode = shipmentMode.equalsIgnoreCase("Air")?1:4;

			connection 	=	getConnection();
			stmt		=	connection.createStatement();
			String sql1	=	"SELECT COUNT(*) NO_ROWS FROM FS_FR_FRTINVOICEMASTER WHERE BILLTO='"+customerId+"' AND SHIPMENTMODE="+shipMode+" ORDER BY FRTINVOICEID";
			String sql2	=	"SELECT FRTINVOICEID FROM FS_FR_FRTINVOICEMASTER WHERE BILLTO='"+customerId+"' AND SHIPMENTMODE="+shipMode+" ORDER BY FRTINVOICEID DESC";
			rs			=	stmt.executeQuery(sql1);
			rs.next();
			int cnt		=	rs.getInt("NO_ROWS");


			if(cnt==0)
				{return null;}
			invoiceId	=	new String[cnt];
			int row 	=	0;
			rs			=	stmt.executeQuery(sql2);
			while(rs.next())
			{
				invoiceId[row]	=	rs.getString("FRTINVOICEID");
				row++;
			}
			return	invoiceId;
		}
		catch(SQLException e)
		{
			return null;
		}
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			try
			{
				if(rs!=null)
					{rs.close();}
				if(stmt!=null)
					{stmt.close();}
				if(connection!=null)
					{connection.close();}
			}
			catch(SQLException e)
			{
				throw new EJBException("getCustomerInvoiceIds:finally::..."+e.toString());
			}
		}
	}

// End of CRM methods

	 // Added by Chandu, for getting EMail status for different levels

  /**
   *
   * @param terminalId
   * @param customerId
   * @param fieldName
   * @return
   * @throws javax.ejb.EJBException
   */
	public boolean getEMailStatus(String terminalId,String customerId, String fieldName)throws javax.ejb.EJBException
	{
		Connection connection   = null;
		PreparedStatement pStmt = null;
		ResultSet rs		    = null;
		String 	  query			= null;
		String    resultValue	= "";
		String    systemLevelFlag = "Y";

		try
		{
			if(systemLevelFlag.equalsIgnoreCase("Y"))
			{
				connection = getConnection();
				query = "SELECT EMAIL_ACTVE_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID = ?";

				pStmt = connection.prepareStatement(query);
				pStmt.setString(1,terminalId);
				rs = pStmt.executeQuery();

				if(rs.next())
					{resultValue = rs.getString(1);}

				if(resultValue.equalsIgnoreCase("Y"))
				{
				   resultValue = "";
				   rs  		   = null;

				   query = "SELECT "+fieldName+" FROM FS_FR_CUSTOMER_NOTIF WHERE TERMINAL_ID = ? AND CUSTOMER_ID = ?";
           //Added By RajKumari on 23-10-2008 for Connection Leakages.
			   	if(pStmt!=null)
          {
            pStmt.close();
            pStmt = null;
          }
					pStmt = connection.prepareStatement(query);
					pStmt.setString(1,terminalId);
					pStmt.setString(2,customerId);

					rs = pStmt.executeQuery();

					if(rs.next())
						{resultValue = rs.getString(1);}
				}
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in OperationImpl...getEMailStatus().."+e.toString());
      logger.error(FILE_NAME+"Exception in OperationImpl...getEMailStatus().."+e.toString());
		}
		finally
		{
			try
			{
				if(rs!=null)
				{	rs.close();}
				if(pStmt!=null)
					{pStmt.close();}
				if(connection!=null)
					{connection.close();}
			}
			catch(SQLException e)
			{
				throw new javax.ejb.EJBException("OperationImpl...getEMailStatus(): finally::..."+e.toString());
			}
		}


		if(resultValue.equalsIgnoreCase("Y"))
				{return true;}
			else
				{return false;}

	}

//Add by Nageswara Rao.D for getting the InvoiceIds for Shipper and Consigenee
  /**
   *
   * @param terminalId
   * @param shiper
   * @param searchString
   * @param loginbean
   * @return
   * @throws javax.ejb.EJBException
   */
public ArrayList	getViewReprotInvoiceIds( String terminalId,String shiper,String searchString,ESupplyGlobalParameters loginbean	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  invoiceId		  =	new ArrayList();
		String		  whereClause	  = "";
		shiper	= shiper.trim();
        if(shiper.equals("Inbound"))
        	{whereClause		= " AND STATUS = 'Origin' " ;}
        if(shiper.equals("Outbound"))
			{whereClause		= " AND STATUS = 'Origin' ";}
		if(shiper.equals("None"))
//			whereClause		= " AND (FRTINVOICEID LIKE '%/S%' OR FRTINVOICEID LIKE '%/C%' )";
			{whereClause		= " AND STATUS IN ('Origin','Destination') ";}

		try
		{
			connection = getConnection();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  INVOICEDATE ) < 15 AND TERMINALID='"+terminalId+"' "+whereClause+"  ";
			String sql2	= "SELECT FRTINVOICEID FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%'  AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  INVOICEDATE ) < 15 AND TERMINALID='"+terminalId+"' "+whereClause+"	ORDER BY INVOICEDATE DESC";








			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId.add(rs.getString("FRTINVOICEID"));

			}

			return	invoiceId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getViewReprotInvoiceIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param shiper
   * @param searchString
   * @param loginbean
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getInvoiceIdsForSea( String terminalId,String shiper,
											String searchString,ESupplyGlobalParameters loginbean	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  invoiceId		  =	new ArrayList();
		String		  whereClause	  = "";
		shiper	= shiper.trim();
        String sql1	=	"";
		String sql2	=	"";

		try
		{
			connection = getConnection();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			stmt = connection.createStatement();
			if(shiper.equals("Inbound"))
			{
			   //sql1 = "SELECT COUNT(DISTINCT I.INVOICEID) NO_ROWS FROM FS_FR_PICKUPREQUEST P,FS_FR_PRQINVOICEPARTDTL I WHERE "+
					  // "P.PRQID=I.PRQID AND P.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - P.PRQDATE ) < 15 AND SHIPMENTMODE =2 AND  I.INVOICEID LIKE '"+searchString+"%' ";
				//@@ Avinash Replaced on 20041016
				// sql2 = "SELECT DISTINCT I.INVOICEID INVID FROM FS_FR_PICKUPREQUEST P,FS_FR_PRQINVOICEPARTDTL I WHERE "+
					   //"P.PRQID=I.PRQID AND P.DESTTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - P.PRQDATE ) < 15 AND SHIPMENTMODE = 2 AND  I.INVOICEID LIKE '"+searchString+"%'  ORDER BY I.INVOICEID";
				sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.DEST_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = 2"+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.INVOICEID";
				//@@

			}
			else
			{
			//	sql1 = "SELECT COUNT(DISTINCT I.INVOICEID) NO_ROWS FROM FS_FR_PICKUPREQUEST P,FS_FR_PRQINVOICEPARTDTL I WHERE "+
					//   "P.PRQID=I.PRQID AND P.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - P.PRQDATE ) < 15 AND SHIPMENTMODE = 2 AND  I.INVOICEID LIKE '"+searchString+"%'  ";
				//@@ Avinash Replaced on 20041016
				//sql2 = "SELECT DISTINCT I.INVOICEID INVID FROM FS_FR_PICKUPREQUEST P,FS_FR_PRQINVOICEPARTDTL I WHERE "+
				//	   "P.PRQID=I.PRQID AND P.ORIGINTERMINAL='"+terminalId+"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') - P.PRQDATE ) < 15 AND SHIPMENTMODE = 2 AND  I.INVOICEID LIKE '"+searchString+"%'  ORDER BY I.INVOICEID";
				sql2 =  " SELECT DISTINCT PARTDTL.INVOICEID"+
						" FROM FS_RT_PLAN PL, FS_RT_LEG LG, FS_FR_PICKUPREQUEST P, FS_FR_PRQINVOICEPARTDTL PARTDTL"+
						" WHERE"+
						" PARTDTL.INVOICEID LIKE '"+searchString+"%'"+
						" AND P.PRQID = PL.PRQ_ID"+
						" AND P.PRQID=PARTDTL.PRQID"+
						" AND LG.RT_PLAN_ID  = PL.RT_PLAN_ID"+
						" AND LG.ORIG_TRML_ID = '"+terminalId+"'"+
						" AND LG.LEG_TYPE NOT IN ('TL','LT')"+
						" AND P.SHIPMENTMODE = 2"+
						" AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -P.PRQDATE ) < 15"+
						" ORDER BY PARTDTL.INVOICEID";
				//@@
			}

			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId.add(rs.getString(1));
			}

			return	invoiceId;
		}
		catch( Exception e ){
			e.printStackTrace();
			return null;
		}
		finally{
			try{
				if(	rs!=null ) {rs.close();}
				if(	stmt!=null ) {stmt.close();}
				if(	connection!=null ){ connection.close();}
			}
			catch( SQLException	e ){
				throw new EJBException("getViewReprotInvoiceIds:finally::..."+e.toString());
			}
		}
	}


  /**
   *
   * @param terminalId
   * @return
   * @throws javax.ejb.EJBException
   */
	public String getLocalTime(String terminalId) throws EJBException
	{
		String stringDate = "";
		int relativeOffset	= 0;


		String terminalTimeZone = this.getTimeZone("TERMINAL",terminalId);
		if(terminalTimeZone != null)
		{
			Calendar gc = Calendar.getInstance();
			TimeZone tz = TimeZone.getDefault(); // defalut TimeZone
			TimeZone t = TimeZone.getTimeZone(terminalTimeZone); // userTimezone
			// def TimeZone offset
			// @@ Added by Santhosam for TogetherJ
			int serverOffset = tz.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
			// userTimeZone offset
			int localOffset = t.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
			relativeOffset	= localOffset - serverOffset;
		}

		Timestamp currentTime = new Timestamp((new java.util.Date()).getTime() + relativeOffset);
		stringDate	=	doFormatDate(currentTime,"dd/MM/yyyy HH:MM");
		return stringDate;
	}
//****************************************** NEW METHOD FOR LOCAL TIME *********************************
/*
*	This method is a substitue method for 'getLocalTime(String terminalId)' method with single argument.
*	'getLocalTime(String terminalId)' method can be removed when it not refered by any programs.
*/
  /**
   *
   * @param terminalId
   * @param dateFormat
   * @return
   * @throws javax.ejb.EJBException
   */
public String getLocalTime(String terminalId,String dateFormat) throws EJBException
{
		String stringDate = "";
		int relativeOffset	= 0;
		 ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility();

		String terminalTimeZone = this.getTimeZone("TERMINAL",terminalId);

	    if(terminalTimeZone != null)
	    {
			Calendar gc = Calendar.getInstance();
			TimeZone tz = TimeZone.getDefault(); // defalut TimeZone
			TimeZone t = TimeZone.getTimeZone(terminalTimeZone); // userTimezone
			// def TimeZone offset
			int serverOffset = tz.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
			// userTimeZone offset
			int localOffset = t.getOffset(Calendar.ERA, Calendar.YEAR, Calendar.MONTH, Calendar.DATE,Calendar.DAY_OF_WEEK,Calendar.MILLISECOND);
			relativeOffset	= localOffset - serverOffset;
		 }

		 Timestamp currentTime = new Timestamp((new java.util.Date()).getTime() + relativeOffset);

 		try
		{
			eSupplyDateUtility.setPatternWithTime(dateFormat);
		}
		catch(InvalidDateFormatException ex)
		{
			//Logger.error(FILE_NAME,"The Date format is not valid :: getLocalTime() :: OperationImpl.java" + ex.toString());
      logger.error(FILE_NAME+"The Date format is not valid :: getLocalTime() :: OperationImpl.java" + ex.toString());
			ex.printStackTrace();
			throw new EJBException("The Date format is not Valid :: getLocalTime() :: OperationImpl.java" );

		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"The Date format is not setting :: getLocalTime():: OperationImpl.java" +ex.toString());
      logger.error(FILE_NAME+"The Date format is not setting :: getLocalTime():: OperationImpl.java" +ex.toString());
			ex.printStackTrace();
			throw new EJBException("The Date format is not setting :: getLocalTime():: OperationalImpl.java");

		}
		// set the date format pattern
		// eSupplyDateUtility.setPatternWithTime(dateFormat);
		 stringDate	=  eSupplyDateUtility.getDisplayString(currentTime);
	  return stringDate;
 }

//****************************************** ENDS HERE *************************************************

	private String doFormatDate(Timestamp timeStamp, String formatType)
    {
        Date date = (Date)timeStamp;
        DateFormat dateFormat = new SimpleDateFormat(formatType );
		return dateFormat.format(date);
    }

	private String getTimeZone(String locationType, String locationId)
	{
		Connection connection		= null;
		Statement	stmt			= null;
		ResultSet	rs				= null;
		String 		timeZone		= null;
		String 		query			= "";
		if(locationType.equals("TERMINAL") || locationType.equals("CORPORATE"))
		{
			query = "SELECT TIME_ZONE FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+locationId+"'";
		}
		else if(locationType.equals("GATEWAY") )
		{
			query = "SELECT TIME_ZONE FROM FS_FR_GATEWAYMASTER WHERE GATEWAYID = '"+locationId+"'";
		}
		else
		{
			query = "SELECT TIME_ZONE FROM FS_AC_COMPANYINFO WHERE COMPANYID = '"+locationId+"'";
		}

		try
		{
			connection = getConnection();
			stmt		= connection.createStatement();
			rs		= stmt.executeQuery(query);
			if(rs.next() )
			{
				timeZone	= rs.getString(1);
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"OperationImpl getTimeZone()"+e.toString());
      logger.error(FILE_NAME+"OperationImpl getTimeZone()"+e.toString());
		}
		finally
		{
			try{
			if(rs != null)
				{rs.close();	}
			if(stmt != null)
				{stmt.close();}
			if(connection != null)
				{connection.close();}
			}
			catch(SQLException sqlEx)
			{}

		}
		return timeZone;

	}


	// Added By Chandu,

  /**
   *
   * @return
   */
	public Vector getLocations()
	{
		ResultSet  rs	= null;
		Statement  stmt	= null;
		Connection connection =	null;

		Vector locationVector = new Vector();
		try
		{
			connection=getConnection();
			stmt=connection.createStatement();
			 String	sqlQuery="SELECT LOCATIONID FROM FS_FR_LOCATIONMASTER " ;
			 rs= stmt.executeQuery(sqlQuery);
			 while( rs.next() )
			 {
			 	locationVector.addElement(rs.getString("LOCATIONID"));
			 }
		}
		catch(SQLException sqle	)
		{
			//Logger.error(FILE_NAME,"SQL	Error OperationImpl::getLocations() "+sqle.toString());
      logger.error(FILE_NAME+"SQL	Error OperationImpl::getLocations() "+sqle.toString());
		}
		catch(Exception	exp	)
		{
			//Logger.error(FILE_NAME,"Error OperationImpl::getLocations() " +exp.toString());
      logger.error(FILE_NAME+"Error OperationImpl::getLocations() " +exp.toString());
		}
		finally
		{
			try
			{
					if(	rs != null )
						{rs.close();}
					if(	stmt !=	null )
						{stmt.close();}
					if(	connection != null )
						{connection.close();}
			}
			catch(SQLException sqle	)
			{
				//Logger.error(FILE_NAME,"SQL	Error while	closing	connections	OperationImpl::getLocations() "+sqle.toString());
        logger.error(FILE_NAME+"SQL	Error while	closing	connections	OperationImpl::getLocations() "+sqle.toString());
			}
		}//End of finally block.
		return locationVector;
	}

	// method modified by supraja CKM to implement shipmentmode.
  /**
   *
   * @param customerId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
		public ArrayList	getContainerNo( String customerId ,String shiper,
											String shipmentMode,String searchString) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		Statement	  stmt1			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  ctnrNo	  	  =	new ArrayList();
		String 		  sql1			  = null;
		String 		  sql2			  = null;
		int			  cnt			  = 0;

		try
		{
			connection = getConnection();
			stmt  = connection.createStatement();
			stmt1 = connection.createStatement();
			 int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}

			if(shipMode==2)
			{
				if(shiper.equals("Inbound"))
				{
					//@@ Avinash replaced on 20041018
					//sql1 =	" SELECT DISTINCT C.CONTAINERNO FROM FS_FRS_CONSOLECONTAINERDTL C,FS_FR_HOUSEDOCHDR H "+
					//		" WHERE H.SHIPMENTMODE=2 AND C.CONSOLEID= H.MASTERDOCID AND H.DESTTERMINAL='"+customerId+"'"+
					//		" AND C.CONTAINERNO IS NOT NULL";
					sql1 =	" SELECT DISTINCT C.CONTAINERNO"+
							" FROM FS_FRS_CONSOLEMASTER M, FS_FRS_CONSOLECONTAINERDTL C,FS_FRS_ROUTEMASTER R"+
							" WHERE"+
							" M.CONSOLEID = C.CONSOLEID"+
							" AND R.ROUTEID=M.ROUTEID"+
							" AND R.DESTGATEWAY	= '"+customerId+"'"+
							" AND C.CONTAINERNO LIKE '"+searchString+"%'"+
							" AND C.CONTAINERNO IS NOT NULL"+
							" ORDER BY C.CONTAINERNO";
					//@@
				}
				if(shiper.equals("Outbound"))
				{
					//@@ Avinash replaced on 20041018
					//sql1 =	" SELECT DISTINCT C.CONTAINERNO FROM FS_FRS_CONSOLECONTAINERDTL C,FS_FR_HOUSEDOCHDR H "+
							//" WHERE H.SHIPMENTMODE=2 AND C.CONSOLEID= H.MASTERDOCID AND H.ORIGINTERMINAL='"+customerId+"'"+
							//" AND C.CONTAINERNO IS NOT NULL";
					sql1 =	" SELECT DISTINCT C.CONTAINERNO"+
							" FROM FS_FRS_CONSOLEMASTER M, FS_FRS_CONSOLECONTAINERDTL C,FS_FRS_ROUTEMASTER R"+
							" WHERE"+
							" M.CONSOLEID = C.CONSOLEID"+
							" AND R.ROUTEID=M.ROUTEID"+
							" AND R.ORIGINGATEWAY	= '"+customerId+"'"+
							" AND C.CONTAINERNO LIKE '"+searchString+"%'"+
							" AND C.CONTAINERNO IS NOT NULL"+
							" ORDER BY C.CONTAINERNO";
					//@@
				}
			}

			rs = stmt1.executeQuery(sql1);
			while( rs.next() )
			{
				ctnrNo.add(rs.getString(1));
			}
			return	ctnrNo;
		}
		catch( Exception e ){
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Exception in getContainerNo() "+e);
      logger.error(FILE_NAME+"Exception in getContainerNo() "+e);
		}
		finally{
				//closeConnection(rs,stmt1,connection);
        //closeConnection(null,stmt,null);
        ConnectionUtil.closeConnection(connection, stmt1, rs);
        ConnectionUtil.closeConnection(null, stmt, null);

		}
		return	ctnrNo;
	}

/**
   * Supraja Methods starts Here***************************************************************
   * @param customerId
   * @param searchString
   * @param shipmentMode
   * @return
   * @throws javax.ejb.EJBException
   */



//method added by Supraja CKM for getting oblIds.
	public ArrayList	getOblIds(String customerId ,String searchString,String shipmentMode) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;

		Statement	  stmt			  =	null;
		Statement	  stmt1			  =	null;

		//ResultSet	  rs			  =	null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
		ResultSet	  rs1			  =	null;

		ArrayList	  oblId			  =	new ArrayList();
		//ArrayList	  hblStatus		  = new ArrayList();

		String		  sql1			  = null;
		String		  sql2			  = null;
		String		  sql3			  = null;
		String		  sql4			  = null;
		String		  sql5			  = null;






		try
		{
			connection = getConnection();
			stmt  = connection.createStatement();
			stmt1 = connection.createStatement();

			sql3	= "SELECT PRQSTATUS FROM FS_FR_PICKUPREQUEST WHERE SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"'";
			sql2	= "SELECT O.OBLID OBLID FROM FS_FRS_OBLMASTER O,FS_FR_HOUSEDOCHDR H WHERE H.SHIPMENTMODE=2 AND (H.SHIPPERID='"+customerId+"' OR H.CONSIGNEEID='"+customerId+"') AND O.OBLID LIKE '"+searchString+"%' AND H.MASTERDOCID=O.CONSOLEID ORDER BY O.OBLID	DESC";
 			sql4	= "SELECT O.OBLID OBLID FROM FS_FRS_OBLMASTER O,FS_FR_PICKUPREQUEST B WHERE (B.SHIPPERID='"+customerId+"' OR B.CONSIGNEEID='"+customerId+"') AND O.OBLID LIKE '"+searchString+"%' AND B.MASTERDOCID=O.CONSOLEID ORDER BY O.OBLID	DESC";
  		rs1 	= stmt.executeQuery(sql3);






	 	    while(rs1.next())
			{
					//hblStatus 	   = new String[cnts];
				String	hblStatus =  rs1.getString(1);
					if(hblStatus!= null && !hblStatus.equals("H"))

					{





						rs1 = stmt1.executeQuery(sql2);


						while(rs1.next())
						{
							oblId.add( rs1.getString(1));


						}
					}
					else
					{





						rs1 = stmt1.executeQuery(sql4);


						while( rs1.next() )
						{
							oblId.add(rs1.getString(1));


						}
					}

			}
			return	oblId;
		}
		catch( SQLException	e )
		{
			e.printStackTrace();
			throw new EJBException("EXception in getOblIds();  "+e.toString());
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
			//Logger.error(FILE_NAME, "EXception in getOblIds();  "+ex.toString());
      logger.error(FILE_NAME+ "EXception in getOblIds();  "+ex.toString());

		}
		finally
		{

			ConnectionUtil.closeConnection(connection, stmt1, rs1);
      //ConnectionUtil.closeConnection(null, null, rs);//Commented By RajKumari on 23-10-2008 for Connection Leakages.
      ConnectionUtil.closeConnection(null, stmt, null);
		}
		return	oblId;
	}

  /**
   *
   * @param terminalId
   * @param shiper
   * @param shipmentMode
   * @param searchString
   * @param loginbean
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getSeaOblIds(String terminalId ,String shiper,String shipmentMode,String searchString,ESupplyGlobalParameters loginbean) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  masterId		  =	new ArrayList();
		String		  sql1			   = null;
		String		  sql2			   = null;


		try
		{
			 int shipMode = 0;
			if(shipmentMode.equalsIgnoreCase("Air"))
				{shipMode	=	1;}
			else if(shipmentMode.equalsIgnoreCase("Truck"))
				{shipMode	=	4;}
			else if(shipmentMode.equalsIgnoreCase("Sea"))
				{shipMode	=	2;}


			connection = getConnection();
			stmt = connection.createStatement();
			String dateFormat =	loginbean.getUserPreferences().getDateFormat();
			String converTime = this.getLocalTime(terminalId,dateFormat);
			if(shipMode==1 || shipMode==4)
			{
					if(shiper.equals("Inbound"))
					{
						//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE DESTINATIONGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  - MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE DESTINATIONGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  - MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+"  ORDER BY MASTERDOCDATE DESC";
					}

					 if(shiper.equals("Outbound"))
					{
						//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE ORIGINGATEWAYID='"+ terminalId +"' AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE ORIGINGATEWAYID='"+ terminalId	+"'	AND	MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ORDER BY	MASTERDOCDATE DESC";
					}
					if(shiper.equals("None"))
					{
						//sql1 = "SELECT COUNT(*)	NO_ROWS	FROM FS_FR_MASTERDOCHDR	WHERE ( ORIGINGATEWAYID='"+ terminalId +"' OR DESTINATIONGATEWAYID='"+ terminalId +"') AND MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15	AND SHIPMENTMODE="+shipMode+" ";
						sql2 = "SELECT MASTERDOCID FROM	FS_FR_MASTERDOCHDR WHERE ( ORIGINGATEWAYID='"+ terminalId +"' OR DESTINATIONGATEWAYID='"+ terminalId +"') AND	MASTERDOCID LIKE '"+searchString+"%' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI')  -  MASTERDOCDATE ) < 15 	AND SHIPMENTMODE="+shipMode+"  ORDER BY	MASTERDOCDATE DESC";
					}
			}
			else if(shipMode == 2)
			{
					if(shiper.equals("Inbound"))
					{
						//sql1 = " SELECT COUNT(*)	NO_ROWS	  FROM FS_FRS_OBLMASTER O,"+
						//		"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C "+
						//		" WHERE   O.CONSOLEID=A.CONSOLEID AND B.ROUTEID=A.ROUTEID AND  C.ROUTEID=B.ROUTEID AND C.DESTTERMINAL='"+ terminalId +"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15" ;


						sql2 = " SELECT DISTINCT O.OBLID  FROM FS_FRS_OBLMASTER O,"+
								"  FS_FRS_CONSOLEMASTER A,FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C "+
								" WHERE   O.CONSOLEID=A.CONSOLEID AND  B.ROUTEID=A.ROUTEID AND  C.ROUTEID=B.ROUTEID AND  C.DESTTERMINAL='"+ terminalId +"' AND ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15" ;
					}
					if(shiper.equals("Outbound"))
					{
						//sql1 = "SELECT  COUNT(*)	NO_ROWS	FROM FS_FRS_OBLMASTER O WHERE O.CONSOLEID IN(SELECT A.CONSOLEID  FROM FS_FRS_CONSOLEMASTER A, "+
							//	" FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C  WHERE   B.ROUTEID=A.ROUTEID AND C.ROUTEID=B.ROUTEID AND B.ORIGINGATEWAY "+
							//	" IN(SELECT GATEWAYID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+ terminalId +"')  AND  ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15) "+
							//	" AND O.OBLID LIKE '"+searchString+"%' ";

						sql2 = "SELECT  DISTINCT O.OBLID  	FROM FS_FRS_OBLMASTER O WHERE O.CONSOLEID IN(SELECT A.CONSOLEID  FROM FS_FRS_CONSOLEMASTER A, "+
								" FS_FRS_ROUTEMASTER B,FS_FRS_ROUTEMASTERDTL C  WHERE   B.ROUTEID=A.ROUTEID AND C.ROUTEID=B.ROUTEID AND B.ORIGINGATEWAY "+
								" IN(SELECT GATEWAYID FROM FS_FR_TERMINALGATEWAY WHERE TERMINALID='"+ terminalId +"')  AND  ROUND(TO_DATE('"+converTime+"','"+dateFormat+" HH24:MI') -  CONSOLEDATE ) < 15) "+
								" AND O.OBLID LIKE '"+searchString+"%' ";
					}

			}









			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				masterId.add(rs.getString(1));

			}

			return	masterId;
		}
		catch( SQLException	e )
		{
			return null;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}



	//Method added by Supraja CKM TO get air/Sea PartIds.
  /**
   *
   * @param shipmentMode
   * @param customerId
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList getAirSeaPartId(String shipmentMode,String customerId ) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
        Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList     partId	      = new ArrayList();
       	String        sql2            = null;
		String	      sql1	      	  = null;
		String			shipMode	  = "1";

        try
       	{
			if(shipmentMode.equalsIgnoreCase("Air"))
			{
				shipMode	  = "1";
			}else if(shipmentMode.equalsIgnoreCase("Sea"))
			{
				shipMode	  = "2";
			}

       		connection  = getConnection();
	        stmt 		= connection.createStatement();

			  //sql1 = "SELECT COUNT(DISTINCT I.PARTID) NO_ROWS FROM FS_FR_PRQINVOICEPARTDTL I,FS_FR_PICKUPREQUEST P "+
				  	 //"WHERE P.SHIPMENTMODE="+shipMode+" AND P.PRQID=I.PRQID AND  (P.SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"' OR CUSTOMERID='"+customerId+"') AND I.PARTID IS NOT NULL";

		   	  sql2 = "SELECT DISTINCT I.PARTID FROM FS_FR_PRQINVOICEPARTDTL I,FS_FR_PICKUPREQUEST P "+
				  	 "WHERE P.SHIPMENTMODE="+shipMode+" AND P.PRQID=I.PRQID AND  (P.SHIPPERID='"+customerId+"' OR CONSIGNEEID='"+customerId+"' OR CUSTOMERID='"+customerId+"') AND I.PARTID IS NOT NULL";

	        rs = stmt.executeQuery( sql2 );
	        while( rs.next() )
        	{
        		if(rs.getString(1)!=null)
        		{
        		partId.add(rs.getString(1));

        		}
        	}

			return  partId;
		}
		catch( SQLException e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Exception in getAiSeaPartId() "+e);
      logger.error(FILE_NAME+"Exception in getAiSeaPartId() "+e);
			return null;

		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in getAirSeaPartId() "+e);
      logger.error(FILE_NAME+"Exception in getAirSeaPartId() "+e);
			return null;

		}
		finally
		{

				closeConnection(rs,stmt,connection);

		}
	}

	//method added by Supraja CKM
  /**
   *
   * @param customerId
   * @param searchString
   * @param shipmentMode
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getAirSeaInvoiceIds(String customerId,String searchString,String shipmentMode) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  invoiceId		  =	new ArrayList();
		String		  whereClause	  = "";
		String 		  sql1			  = "";
		String 		  sql2			  = "";
		String			shipMode	  = "1";
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			if(shipmentMode.equalsIgnoreCase("Air")){
				shipMode	  = "1";
			}else {
				shipMode	  = "2";
			}

			//sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_PRQINVOICEPARTDTL I,FS_FR_PICKUPREQUEST P WHERE I.PRQID LIKE '"+searchString+"%' AND P.SHIPMENTMODE="+shipMode+" AND (P.SHIPPERID='"+customerId+"' OR P.CONSIGNEEID='"+customerId+"') AND I.PRQID=P.PRQID AND I.INVOICEID IS NOT NULL  ";
			sql2	= "SELECT I.INVOICEID FROM	FS_FR_PRQINVOICEPARTDTL I,FS_FR_PICKUPREQUEST P WHERE I.PRQID LIKE '"+searchString+"%' AND P.SHIPMENTMODE="+shipMode+" AND (P.SHIPPERID='"+customerId+"' OR P.CONSIGNEEID='"+customerId+"') AND I.PRQID=P.PRQID AND I.INVOICEID IS NOT NULL";
      rs = stmt.executeQuery(	sql2 );
			while( rs.next())








			{
				invoiceId.add(rs.getString(1));

			}
			return	invoiceId;
		}
		catch( SQLException	e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME, "SqlException in getAirSeaInvoiceIds():   "+e.toString());
      logger.error(FILE_NAME+ "SqlException in getAirSeaInvoiceIds():   "+e.toString());

		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME, "Exception in getAirSeaInvoiceIds():   "+e.toString());
      logger.error(FILE_NAME+ "Exception in getAirSeaInvoiceIds():   "+e.toString());

		}
		finally
		{

				closeConnection(rs,stmt,connection);

		}
		return	invoiceId;
	}
	//Method added by Supraja CKM togetAirSeaPoNo's
  /**
   *
   * @param customerId
   * @param shipmentMode
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList getPurchaseOrder( String customerId,String shipmentMode,String searchString) throws javax.ejb.EJBException
	{
        Connection    connection      = null;
        Statement     stmt            = null;
        ResultSet     rs              = null;
        ArrayList      poId			  =  new ArrayList();
        String        sql1            = null;
        String        sql2            = null;
        Statement     stmt1            = null;
        String			shipMode	  = "1";

        try
       	{

       		connection = getConnection();
	        stmt	   = connection.createStatement();
	        stmt1	   = connection.createStatement();
        if(shipmentMode.equalsIgnoreCase("Air"))
        {
				shipMode	  = "1";
        }
        else
        {
				shipMode	  = "2";
        }
          sql1 = "SELECT DISTINCT PI.POID FROM FS_FR_PICKUPREQUEST P,FS_FR_PRQINVOICEPARTDTL PI WHERE "+
				   "(P.SHIPPERID='"+customerId+"' OR P.CONSIGNEEID='"+customerId+"') AND P.SHIPMENTMODE="+shipMode+" AND P.PRQID=PI.PRQID AND  PI.POID IS NOT NULL ";

	 		    rs = stmt1.executeQuery( sql1 );
			    while( rs.next() )














        	{
        	   poId.add(rs.getString(1));

        	}
		      return  poId;
			}



		catch( SQLException e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Exception in getPurhaseOrder() "+e);
      logger.error(FILE_NAME+"Exception in getPurhaseOrder() "+e);
			return null;
		}
		catch( Exception e )
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Exception in getPurhaseOrder() "+e);
      logger.error(FILE_NAME+"Exception in getPurhaseOrder() "+e);
			return null;
		}
		finally
		{
      ConnectionUtil.closeConnection(connection, stmt1, rs);
      ConnectionUtil.closeConnection(null, stmt, null);

		}
	}

  /**
   *
   * @param customerId
   * @param searchString
   * @return
   * @throws javax.ejb.EJBException
   */
	public ArrayList	getRunSheetNos(String customerId,String searchString)throws	javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  runshtNo  	  =	new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM FS_RS_MASTER RM,  FS_RS_DETAIL RD,FS_FR_HOUSEDOCHDR HD WHERE RM.RUN_SHEET_NO=RD.RUN_SHEET_NO AND RD.RUN_SHEET_NO=HD.HOUSEDOCID AND HD.SHIPPERID='"+customerId+"'";
			String sql2	= "SELECT RM.RUN_SHEET_NO FROM FS_RS_MASTER RM, FS_RS_DETAIL RD,FS_FR_HOUSEDOCHDR HD WHERE RM.RUN_SHEET_NO=RD.RUN_SHEET_NO AND RD.RUN_SHEET_NO=HD.HOUSEDOCID AND HD.SHIPPERID='"+customerId+"'";








			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				runshtNo.add(rs.getString(1));

			}

			return	runshtNo;
		}
		catch( SQLException	e )
		{
			//Logger.error(FILE_NAME,"SQLException in getRunSheetNo's()	"+e);
      logger.error(FILE_NAME+"SQLException in getRunSheetNo's()	"+e);
			return null;
		}
		catch( Exception e )
		{
			//Logger.error(FILE_NAME,"Exception in getRunSheetNo's()	"+e);
      logger.error(FILE_NAME+"Exception in getRunSheetNo's()	"+e);
		}
		finally
		{
				closeConnection(rs,stmt,connection);
		}
		return	runshtNo;
	}
/**
   *Supraja Methods Ends Here*********************************************************************
   *@return
   *@throws javax.ejb.EJBException
   */

public Vector getNonSystemGatewayIds() throws javax.ejb.EJBException
	{
		Connection	  connection	= null;
		Statement	  stmt			= null;
		ResultSet	  rs			= null;
		Vector		  vecGatewayIds	= new Vector();
		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql = "SELECT GATEWAYID FROM FS_FR_GATEWAYMASTER WHERE GATEWAY_TYPE = 'N'";
			rs = stmt.executeQuery(	sql	);
			while( rs.next() )
			{
				vecGatewayIds.addElement( rs.getString("GATEWAYID")	);
			}
			return	vecGatewayIds;
		}
		catch( SQLException	e )
		{
			throw new EJBException("getNonSystemGatewayIds::..."+e.toString());
		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					{connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getNonSystemGatewayIds:finally::..."+e.toString());
			}
		}
	}


  /**
   *
   * @throws javax.ejb.EJBException
   */
	public void	createDataSource() throws EJBException
	{
		try
		{
			initialContext = new InitialContext();
			dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
		}
		catch( Exception e )
		{
			throw new EJBException( "createDataSource:: "+e.toString() );
		}
	}

  /**
   *
   * @return
   * @throws javax.ejb.EJBException
   */
	public Connection  getConnection() throws EJBException
	{
		Connection con = null;
		try
		{
			if(dataSource == null)
				{createDataSource();}

			con	= dataSource.getConnection();
		}
		catch( Exception ex)
		{
		   throw new EJBException( "getConnection: "+ex.toString() );
		}
		return con;
	}
	private void closeConnection( ResultSet rs,Statement st,Connection connection )
    {
        try
        {
            if( rs != null )
              {  rs.close();}
            if( st != null )
                {st.close();}
            if( connection != null )
                {connection.close();}
        }
        catch( SQLException se )
        {
            //Logger.error("closeConnection(R,S,C) from ",se.toString());
            logger.error("closeConnection(R,S,C) from "+se.toString());
        }
    }


	// Methods Required for Extending EJBObject Interface stars here
  /**
   *
   * @param ejbObject
   * @return
   */
	public boolean isIdentical(javax.ejb.EJBObject ejbObject )	{return false;}
  /**
   *
   * @return
   */
	public javax.ejb.EJBHome getEJBHome()	{return null;}
  /**
   *
   * @return
   */
	public java.lang.Object getPrimaryKey()	{return null;}
  /**
   *
   * @return
   */
	public javax.ejb.Handle getHandle()		{return null;}
  /**
   *
   */
	public void remove(){}
	// Methods Required for Extending EJBObject Interface ends here

	// Added by Supraja
  /**
   *
   * @param terminalId
   * @param strIdx
   * @param shipmentMode
   * @return
   * @throws javax.ejb.EJBException
   */
public ArrayList	getInvoiceIds( String terminalId, String strIdx,int shipmentMode) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList	  invoiceId		  =	new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			//String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_FRTINVOICEMASTER WHERE TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx	+"%' AND SHIPMENTMODE = "+shipmentMode+" ORDER BY FRTINVOICEID";
			String sql2	= "SELECT FRTINVOICEID FROM	FS_FR_FRTINVOICEMASTER WHERE TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx+"%'	AND SHIPMENTMODE = "+shipmentMode+" ORDER BY FRTINVOICEID DESC";









			//Logger.info(FILE_NAME,"SQL2:	"+sql2);
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId.add(rs.getString("FRTINVOICEID"));

			}

			return	invoiceId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
					{rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
					connection.close();
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}
  /**
   *
   * @param terminalId
   * @param strIdx
   * @param searchString
   * @param shipmentMode
   * @return
   * @throws javax.ejb.EJBException
   */
	public String[]	getInvoiceIds( String terminalId, String strIdx,String searchString,String shipmentMode	) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		String[]	  invoiceId		  =	null;

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql1	= "SELECT COUNT(*) NO_ROWS FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx	+"%' AND SHIPMENTMODE= '+shipmentMode+' ORDER BY FRTINVOICEID";
			String sql2	= "SELECT FRTINVOICEID FROM	FS_FR_FRTINVOICEMASTER WHERE FRTINVOICEID LIKE '"+searchString+"%' AND TERMINALID='"+terminalId+"' AND FRTINVOICEID LIKE '%/"+ strIdx+"%' AND SHIPMENTMODE= '+shipmentMode+'	ORDER BY FRTINVOICEID DESC";

			//Logger.info(FILE_NAME,"SQL1,SQL1	"+sql1);
			rs = stmt.executeQuery(	sql1 );
			rs.next();
			int	cnt	= rs.getInt("NO_ROWS");
			if(	cnt==0 )
				{return null;}
			invoiceId =	new	String[cnt];
			int	row	=0;
			//Logger.info(FILE_NAME,"SQL2 SQL2:	"+sql2);
			rs = stmt.executeQuery(	sql2 );
			while( rs.next() )
			{
				invoiceId[row] = rs.getString("FRTINVOICEID");
				row++;
			}

			return	invoiceId;
		}
		catch( SQLException	e )
		{
			return null;

		}
		catch( Exception e )
		{
			return null;

		}
		finally
		{
			try
			{
				if(	rs!=null )
				{	rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
				{	connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getMasterdocIds:finally::..."+e.toString());
			}
		}
	}

			//@@ Srivegi Added on 20050415 (Invoice-PR)
	public ArrayList getStockedInvoiceIds( String terminalId,String searchString) throws javax.ejb.EJBException
	{
		Connection	  connection	  =	null;
		Statement	  stmt			  =	null;
		ResultSet	  rs			  =	null;
		ArrayList		  invoiceId	= new ArrayList();

		try
		{
			connection = getConnection();
			stmt = connection.createStatement();
			String sql	= "SELECT INVOICEID FROM FS_FR_INVOICEREG WHERE TERMINALID = '"+terminalId+"' AND INVOICEID LIKE '"+searchString+"%' AND STATUS != 'Y' ORDER BY INVOICEID DESC";
			rs = stmt.executeQuery(	sql );
			while( rs.next() )
			{
				invoiceId.add(rs.getString("INVOICEID"));
			}
			if(invoiceId==null)
			 return	null;

            return invoiceId;
		}
		catch( Exception e )
		{
			return null;
		}
		finally
		{
			try
			{
				if(	rs!=null )
				{	rs.close();}
				if(	stmt!=null )
					{stmt.close();}
				if(	connection!=null )
				{	connection.close();}
			}
			catch( SQLException	e )
			{
				throw new EJBException("getStockedInvoiceIds:finally::..."+e.toString());
			}
		}
	}


 public  double getConvertionFactor(String currencyFrom,String currencyTo)throws Exception
 {
    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;
    double coversionFactor = 1;
      try
      {
        con  = getConnection();
        //stmt= con.createStatement();
        String sql="SELECT DISTINCT SELLRATE FROM FS_FR_CURRENCYMASTER WHERE CURRENCY1=? AND CURRENCY2=? AND CURRENCY_UPDATED_DATE= (SELECT MAX(CURRENCY_UPDATED_DATE) FROM  FS_FR_CURRENCYMASTER WHERE CURRENCY1=? AND CURRENCY2=?)";
        stmt  = con.prepareStatement(sql);
        stmt.setString(1,currencyFrom);
        stmt.setString(2,currencyTo);
        stmt.setString(3,currencyFrom);
        stmt.setString(4,currencyTo);
        rs = stmt.executeQuery();
       if(rs.next())
       {
              coversionFactor = rs.getDouble(1);
       }
      }
      catch( Exception e )
      {
           e.printStackTrace();
           //Logger.info(FILE_NAME," --getConvertedCurrency(String currencyFrom,String currencyTo)"+e);
           logger.info(FILE_NAME+" --getConvertedCurrency(String currencyFrom,String currencyTo)"+e);
           throw new Exception(e);
      }finally
      {
        try
        {

          if(rs!=null)
            { rs.close();}
          if(stmt!=null)
            { stmt.close();}
          if(con!=null)
            { con.close();}
        }catch(SQLException e )
        {
             e.printStackTrace();
             //Logger.info(FILE_NAME," --getConvertedCurrency(String currencyFrom,String currencyTo)"+e);
             logger.info(FILE_NAME+" --getConvertedCurrency(String currencyFrom,String currencyTo)"+e);
             throw new Exception(e);
        }
      }
        return coversionFactor;
  }
  public  double getConvertedAmt(double actualAmt,double convFactor)throws Exception
   {
      try
      {
        double amt = actualAmt*convFactor;
        DecimalFormat dfm  = new DecimalFormat("#.#");
        dfm.setMaximumFractionDigits(2);
        NumberFormat nfm = dfm;
        return Double.parseDouble(nfm.format(amt));
      }catch( Exception e )
      {
           e.printStackTrace();
           //Logger.info(FILE_NAME," --getConvertedCurrency(double actualAmt,double convFactor)"+e);
           logger.info(FILE_NAME+" --getConvertedCurrency(double actualAmt,double convFactor)"+e);
           throw new Exception(e);
      }
   }
  //@@ Added by subrahmanyam for the Enhancement 180161
  public  double getConvertedAmtDecimal(double actualAmt,double convFactor)throws Exception
   {
      try
      {
      String actAmtStr ="";
      int i =0;
      int j=0;
      int k=0;

        double amt = actualAmt*convFactor;
        DecimalFormat dfm  = new DecimalFormat("#.#");
        dfm.setMaximumFractionDigits(5);
        NumberFormat nfm = dfm;
        actAmtStr = Double.toString(amt);
         i = actAmtStr.length();
         j = actAmtStr.indexOf(".");
         k = (i-j)+1;
         if (k>5)
          return Double.parseDouble(nfm.format(amt));
         else
         return Double.parseDouble(actAmtStr);
      }catch( Exception e )
      {
           e.printStackTrace();
           //Logger.info(FILE_NAME," --getConvertedCurrency(double actualAmt,double convFactor)"+e);
           logger.info(FILE_NAME+" --getConvertedAmtDecimal(double actualAmt,double convFactor)"+e);
           throw new Exception(e);
      }
   }
  //@@ ended by subrahmanyam for the Enhancement 180161

}
/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
package com.foursoft.esupply.accesscontrol.dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Iterator;


import javax.ejb.CreateException;
import javax.ejb.DuplicateKeyException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Hashtable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import sun.misc.BASE64Encoder;
import java.io.UnsupportedEncodingException;

import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.accesscontrol.exception.UserDAOAppException;
import com.foursoft.esupply.accesscontrol.java.UserModel;
import com.foursoft.esupply.accesscontrol.java.TxDetailVOB;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.dao.BaseDAOImpl;
import com.foursoft.esupply.common.java.FoursoftConfig;
import org.apache.log4j.Logger;
/**
 * File			: UserDAO.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This is used as DAO for User Creation/ Modify/ View/ Delete
 * 
 * @author	Madhu. P, 
 * @date	28-10-2001
 * 
 * Modified History
 * 13-11-2002		Amit Parekh		Updated to handle optional roles for a COMPANY User for EP and
 * 									a WAREHOUSE User for ELOG. A new table FS_OPTIONALROLES was added
 *                                  in the database for these changes.
 *
 * 28-04-2004		Purushotham		Updated to handle previous password check for acc 2.0.
 */
 
public class UserDAO extends BaseDAOImpl
{

	private static final String fileName="UserDAO.java";
	

	private static String[]		insQuery	= new String[5];
	private static String[]		delQuery	= new String[7];
	private static String		pkQuery		= null;
	//private static String		pkQuery1		= null;
	private static String[]		loadQuery	= new String[5];
	private static String[]		updateQuery = new String[3];
  private static Logger logger = null;
	private static boolean		processOptionalRoles	=	FoursoftConfig.PROCESS_OPTIONAL_ROLE;

	static {
		//insQuery[0]		= "INSERT INTO FS_USERMASTER(USERID, LOCATIONID, USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION,USERSTATUS,LOGINCOUNT,DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME ) "
					//	+"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?)";
//@@ Commented by subrahmabyam for Enhancement 167668 on 28/04/09          
   /*insQuery[0]		= "INSERT INTO FS_USERMASTER(USERID, LOCATIONID, USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION,USERSTATUS,LOGINCOUNT,DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME,PHONE_NO,FAX_NO,MOBILE_NO ) "
						+"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?)";//@@Modified by Kameswari for the WPBN issue-61303
     */
//@@ Added by subrahmanyam for Enhancement 167668 on 28/04/09
//commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
//insQuery[0]		= "INSERT INTO FS_USERMASTER(USERID, LOCATIONID, USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION,USERSTATUS,LOGINCOUNT,DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME,PHONE_NO,FAX_NO,MOBILE_NO,CUST_ADDRLINE1,CUST_ADDRLINE2,CUST_ADDRLINE3 ) "
					//	+"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?,?)";
						//@@WPBN issue-167668 on 28/04/09
	insQuery[0]		= "INSERT INTO FS_USERMASTER(USERID, LOCATIONID, USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION,USERSTATUS,LOGINCOUNT,DESIGNATION_ID ,PHONE_NO,FAX_NO,MOBILE_NO,CUST_ADDRLINE1,CUST_ADDRLINE2,CUST_ADDRLINE3 ) "
						+"VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";
  	insQuery[1]		= "INSERT INTO FS_USERROLES(USERID, ROLEID, LOCATIONID, ROLE_LOCATIONID) "
						+"VALUES(?, ?, ?, ?)";

		insQuery[2]		= "INSERT INTO FS_OPTIONALROLES(USERID, ROLEID, LOCATIONID, ROLE_LOCATIONID) "
						+"VALUES(?, ?, ?, ?)";

		insQuery[3]		= "INSERT INTO FS_USER_PREFERENCES(LOCATIONID, USERID, PARAM_NAME, PARAM_VALUE) "
						+"VALUES(?, ?, ?, ?)";
   insQuery[4]		= "INSERT INTO FS_REP_OFFICERS_MASTER( USERID, LOCATIONID, REP_OFFICERS_ID, ALLOTED_TIME, SHIPMENT_MODE ) "
						+"VALUES(?, ?, ?, ?,?)";


		delQuery[0]		= "DELETE FROM FS_USERMASTER WHERE USERID = ? AND LOCATIONID = ?";

		delQuery[1]		= "DELETE FROM FS_USERROLES WHERE USERID = ? AND LOCATIONID = ?";

		delQuery[2]		= "DELETE FROM FS_OPTIONALROLES WHERE USERID = ? AND LOCATIONID = ?";

		delQuery[3]		= "DELETE FROM FS_USER_PREFERENCES WHERE USERID = ? AND LOCATIONID = ?";

		delQuery[4]		= "DELETE FROM FS_PASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ?";

		delQuery[5]		= "DELETE FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ?";
    //added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
 	  delQuery[6]		= " DELETE FROM FS_REP_OFFICERS_MASTER WHERE  USERID = ? AND LOCATIONID = ? ";

		/*loadQuery[0]	= "SELECT USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION, MODIFIED_BY, MODIFIED_DATE ,U.DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME,LEVEL_NO "
						+"FROM FS_USERMASTER U , QMS_DESIGNATION D  "
						+"WHERE USERID = ? AND LOCATIONID = ? AND D.DESIGNATION_ID=U.DESIGNATION_ID ";*/
//@@Commented by subrahmanyam for Enhancement 167668 on 28/04/09
		/*loadQuery[0]	= "SELECT USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION, MODIFIED_BY, MODIFIED_DATE ,U.DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME,LEVEL_NO,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO "
						+"FROM FS_USERMASTER U , QMS_DESIGNATION D  "
						+"WHERE USERID = ? AND LOCATIONID = ? AND D.DESIGNATION_ID=U.DESIGNATION_ID ";//@@Modified by Kameswari for the WPBN issue-61303
    */
//@@Added by subrahmanyam for Enhancement 167668 on 28/04/09
  //loadQuery[0]	= "SELECT USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION, MODIFIED_BY, MODIFIED_DATE ,U.DESIGNATION_ID ,REP_OFFICERS_ID,ALLOTED_TIME,LEVEL_NO,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO,U.CUST_ADDRLINE1,U.CUST_ADDRLINE2,U.CUST_ADDRLINE3 "
					//	+"FROM FS_USERMASTER U , QMS_DESIGNATION D  "
					//	+"WHERE USERID = ? AND LOCATIONID = ? AND D.DESIGNATION_ID=U.DESIGNATION_ID ";
    //Added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009        
            loadQuery[0]	= "SELECT USERNAME, PASSWORD, EMPID, DEPARTMENT, COMPANYID, USER_LEVEL, EMAILID, VERSION, MODIFIED_BY, MODIFIED_DATE ,U.DESIGNATION_ID ,LEVEL_NO,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO,U.CUST_ADDRLINE1,U.CUST_ADDRLINE2,U.CUST_ADDRLINE3 "
						+"FROM FS_USERMASTER U , QMS_DESIGNATION D  "
						+"WHERE USERID = ? AND LOCATIONID = ? AND D.DESIGNATION_ID=U.DESIGNATION_ID ";

//@@Ended by subrahmanyam for Enhancement 167668 on 28/04/09
    loadQuery[1] = "SELECT ROLEID, ROLE_LOCATIONID FROM FS_USERROLES WHERE USERID = ? AND LOCATIONID = ?";

		loadQuery[2] = "SELECT ROLEID, ROLE_LOCATIONID FROM FS_OPTIONALROLES WHERE USERID = ? AND LOCATIONID = ?";

		loadQuery[3] = "SELECT PARAM_NAME, PARAM_VALUE FROM FS_USER_PREFERENCES WHERE USERID = ? AND LOCATIONID = ?";		
    //Added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
  	loadQuery[4] = "SELECT REP_OFFICERS_ID, ALLOTED_TIME, SHIPMENT_MODE FROM FS_REP_OFFICERS_MASTER WHERE USERID = ? AND LOCATIONID = ?";		

		/*updateQuery[0]	= "UPDATE FS_USERMASTER "
						+"SET USERNAME = ?, PASSWORD = ?, EMPID = ?, DEPARTMENT = ?, COMPANYID = ?, USER_LEVEL = ?, EMAILID = ?, VERSION = ?, MODIFIED_BY = ?, MODIFIED_DATE = ?,DESIGNATION_ID = ? ,REP_OFFICERS_ID = ?, ALLOTED_TIME = ?"
						+"WHERE USERID = ? AND LOCATIONID = ?";*/
            
//@@Commented by subrahmanyam for Enhancement 167668 on 28/04/09
/*
		updateQuery[0]	= "UPDATE FS_USERMASTER "
						+"SET USERNAME = ?, PASSWORD = ?, EMPID = ?, DEPARTMENT = ?, COMPANYID = ?, USER_LEVEL = ?, EMAILID = ?, VERSION = ?, MODIFIED_BY = ?, MODIFIED_DATE = ?,DESIGNATION_ID = ? ,REP_OFFICERS_ID = ?, ALLOTED_TIME = ?, PHONE_NO=?, FAX_NO=?, MOBILE_NO=?"
						+"WHERE USERID = ? AND LOCATIONID = ?";//@@Modified by Kameswari for the WPBN issue-61303
*/
//@@Added by subrahmanyam for Enhancement 167668 on 28/04/09
    updateQuery[0]	= "UPDATE FS_USERMASTER "
						+"SET USERNAME = ?, PASSWORD = ?, EMPID = ?, DEPARTMENT = ?, COMPANYID = ?, USER_LEVEL = ?, EMAILID = ?, VERSION = ?, MODIFIED_BY = ?, MODIFIED_DATE = ?,DESIGNATION_ID = ? ,REP_OFFICERS_ID = ?, ALLOTED_TIME = ?, PHONE_NO=?, FAX_NO=?, MOBILE_NO=?, CUST_ADDRLINE1=?, CUST_ADDRLINE2=?, CUST_ADDRLINE3=? "
						+"WHERE USERID = ? AND LOCATIONID = ?";
//@@Ended by subrahmanyam for Enhancement 167668 on 28/04/09
    updateQuery[1]	= "UPDATE FS_USERROLES SET ROLEID = ?, ROLE_LOCATIONID = ? WHERE USERID = ? AND LOCATIONID= ?";

		pkQuery			= "SELECT USERID, LOCATIONID FROM FS_USERMASTER WHERE USERID = ? AND LOCATIONID = ?";
		//pkQuery1			= "SELECT USERID, LOCATIONID FROM FS_USERMASTER WHERE  LOCATIONID = ?";

	}
	
	/**
	 * default constructor which initializes the initialContext 
	 * and gets the Data Source
	 */		
	public UserDAO()
	{
  logger  = Logger.getLogger(UserDAO.class);
	}
	
	public void create(UserModel userModel) throws UserDAOAppException, DuplicateKeyException
	{
		try
		{
			getConnection();
			insertIntoUserMaster(userModel);
       logUserEntry(userModel.getUserId(), userModel.getLocationId());
	   recordPasswordHistory(userModel.getUserId(), userModel.getLocationId(),userModel.getPassword());
			insertIntoUserRoles(userModel);

			if(processOptionalRoles) {
				insertIntoOptionalRoles(userModel);
			}
			insertUserPreferences(userModel.getUserId(), userModel.getLocationId(), userModel.getUserPreferences() );
		}
        catch(DBSysException dbExc)
        {
            //Logger.error(fileName,"Error While creating user",dbExc);
            logger.error(fileName+" Error While creating user "+dbExc);
            throw new EJBException(dbExc);
        }
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}

	public UserModel load(String userId, String locationId) throws UserDAOAppException, DBSysException 
	{
		// insert dataBase code here
		try
		{
			getConnection();
			UserModel userModel = new UserModel();
			userModel = loadUserMasterAndRole(userId, locationId);
			loadUserRole(userId, locationId, userModel);
			if(processOptionalRoles) {
				loadOptionalRoles(userId, locationId, userModel);
			}
			userModel.setUserId(userId);
			userModel.setLocationId(locationId);
			userModel.setUserPreferences(loadUserPreferences(userId, locationId) );
			return userModel;
		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}// End of method load


	public void store(UserModel userModel) throws UserDAOAppException, DBSysException
	{
        //PreparedStatement pStmt = null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
        //ResultSet rs = null;//Commented By RajKumari on 23-10-2008 for Connection Leakages.
        String password = null;

		try
		{
			getConnection();
            
			updateUserMaster(userModel);
            //System.out.println("password form DB ="+userModel.getDbPassword());
            //System.out.println("Encrypted passowrd ="+encryptSHA(userModel.getPassword()));
            if (userModel.getDbPassword()!=null && !userModel.getDbPassword().equals(userModel.getPassword()))
            {
                upDateUserEntry(userModel.getUserId(),userModel.getLocationId());
				unlockUser(userModel.getUserId(),userModel.getLocationId());
				/* Commented By Yuvraj for Password Configuration Issue. 
         * updatePasswordHistory(userModel.getUserId(),userModel.getLocationId(),userModel.getPassword());
         * Maintain the whole history & fetch as per Prev_password field in Password Configuration
         * */
              recordPasswordHistory(userModel.getUserId(),userModel.getLocationId(),userModel.getPassword());
              //@@Yuvraj
            }
			removeUserRoles( userModel.getUserId(), userModel.getLocationId() );
			insertIntoUserRoles(userModel);

			if(processOptionalRoles) {
				removeOptionalRoles( userModel.getUserId(), userModel.getLocationId() );
				insertIntoOptionalRoles(userModel);
			}
			
			removeUserPreferences(userModel.getUserId(), userModel.getLocationId() );
			insertUserPreferences(userModel.getUserId(), userModel.getLocationId(), userModel.getUserPreferences() );
		}
        catch(Exception exp)
        {
            //Logger.error("UserDAO", "store()","Error in Store - SQLException ",exp);
            logger.error("UserDAO "+ " store()"+"Error in Store - SQLException "+exp);
            //throw new UserDAOAppException("Error in store()",exp);
            throw new UserDAOAppException("Error in store()");
        }
		finally
		{
			//ConnectionUtil.closePreparedStatement(pStmt,rs);     //Commented By RajKumari on 23-10-2008 for Connection Leakages.   
			ConnectionUtil.closeConnection(connection);
		}
		
	}
	
	public void remove(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		try
		{
			getConnection();
			if(processOptionalRoles) {
				removeOptionalRoles(userId, locationId);
			}
			removeUserRoles(userId, locationId);
			removeUserPreferences(userId, locationId);
			removeUserMaster(userId, locationId);
			removePasswordHistory(userId, locationId);
			removePreviousPasswordHistory(userId, locationId);

		}
		finally
		{
			ConnectionUtil.closeConnection(connection);
		}
	}
	
	public boolean findByPrimaryKey(String userId, String locationId) throws javax.ejb.ObjectNotFoundException
	{
		boolean hasRows		= false;

		PreparedStatement pstmt   = null;
		ResultSet rs = null;
		try
		{
			getConnection();

			pstmt = connection.prepareStatement(pkQuery);
					pstmt.setString(1,(String)userId);
					pstmt.setString(2,(String)locationId);
			rs = pstmt.executeQuery();
			if(rs.next()){
				hasRows = true;
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			//Logger.error("UserDAO", "findByPrimaryKey(String userId, String locationId)","Error in Find UserModel - SQLException ",se);
      logger.error("UserDAO"+ " findByPrimaryKey(String userId, String locationId)"+" Error in Find UserModel - SQLException "+se);
		}
		catch(Exception e)
		{
			//Logger.error("UserDAO", "findByPrimaryKey(String userId, String locationId)","Error in Find UserModel - Exception ",e);
      logger.error("UserDAO"+ " findByPrimaryKey(String userId, String locationId)"+" Error in Find UserModel - Exception "+e);
		}
		finally
		{
			//ConnectionUtil.closeConnection(connection, pstmt);
			ConnectionUtil.closeConnection(connection, pstmt,rs);// added by Dilip for PMD Correction on 22/09/2015
		}
		if(hasRows)
			return true;
		else
			throw new javax.ejb.ObjectNotFoundException("Could not find bean");

	}

	/**
	*
	* which is used to insert the User attributes in the Databse
	*
	* @exception	SQLException
	*
	*/
	private void insertIntoUserMaster(UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
    
			pstmt	= connection.prepareStatement(insQuery[0]);
			pstmt.setString(1, userModel.getUserId() );
			pstmt.setString(2, userModel.getLocationId().toUpperCase() );
			pstmt.setString(3, userModel.getUserName());
			//pstmt.setString(4, userModel.getPassword());
			pstmt.setString(4,userModel.getPassword());
			pstmt.setString(5, userModel.getEmpId() );
			pstmt.setString(6, userModel.getDepartment() );
			pstmt.setString(7, userModel.getCompanyId() );
			pstmt.setString(8, userModel.getUserLevel() );
      pstmt.setString(9, userModel.getEMailId() );
			pstmt.setInt(10, userModel.getVersion() );
			pstmt.setInt(11, 1);
			pstmt.setInt(12, 0);
      pstmt.setString(13, userModel.getDesignationId() );//added by rk
    // pstmt.setString(14, userModel.getRepOfficersCode() );//commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
    //  pstmt.setString(15, userModel.getAllotedTime() );//commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
     //@@Added by Kameswari for the WPBN issue-61303
		  pstmt.setString(14, userModel.getPhoneNo());
      pstmt.setString(15, userModel.getFaxNo());
      pstmt.setString(16, userModel.getMobileNo());
      //@@Added by Kameswari for the WPBN issue-61303
//@@ Added by subrahmanyam for Enhancement 167668 on 28/04/09
      pstmt.setString(17, userModel.getCustAddr1());
      pstmt.setString(18, userModel.getCustAddr2());
      pstmt.setString(19, userModel.getCustAddr3());
//@@ Ended by subrahmanyam for Enhancement 167668 on 28/04/09
    	int resultCount = pstmt.executeUpdate();
   		if(resultCount != 1)
				throw new UserDAOAppException("Error in registering the User  !! resultCount = "+resultCount );
	
     //  added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
          for (int i=0;i<userModel.getRepOfficersCode2().size();i++)
						{
            userModel.setRepOfficersCode(userModel.getRepOfficersCode2().get(i).toString());
            userModel.setShipmentModeCode(userModel.getShipmentModeCode2().get(i).toString());
            userModel.setAllotedTime(userModel.getAllotedTime2().get(i).toString());
             pstmt	= connection.prepareStatement(insQuery[4]);
            pstmt.setString(1, userModel.getUserId() );
		      	pstmt.setString(2, userModel.getLocationId().toUpperCase() );
            pstmt.setString(3, userModel.getRepOfficersCode() );
            pstmt.setString(4, userModel.getAllotedTime() );
            pstmt.setInt(5,Integer.parseInt(userModel.getShipmentModeCode()));  
            int resultCount1 = pstmt.executeUpdate();
            if(resultCount1 != 1)
				throw new UserDAOAppException("Error in registering the User  !! resultCount = "+resultCount1 );
            }
  
  
  	}
		catch(SQLException se)
		{
			throw new DBSysException("SQL Exception while registering User "+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}
	} // end of insertIntoUserMaster()

	
   /**
	*
	* which is used to insert the User Roles
	*
	* @exception	SQLException
	*
	*/
	private void insertIntoUserRoles(UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt	= connection.prepareStatement(insQuery[1]);
			
			pstmt.setString(1, userModel.getUserId() );
			pstmt.setString(2, userModel.getRoleId() );
			pstmt.setString(3, userModel.getLocationId() );
			pstmt.setString(4, userModel.getRoleLocationId() );

			int resultCount	= pstmt.executeUpdate();
			
			if(resultCount != 1) {
				throw new UserDAOAppException("Error in registering the User Roles  !! resultCount = "+resultCount );
			}

		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Registering the User Roles : \n"+se);
		}		
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}
	} // end of insertIntoUserRoles


   /**
	*
	* which is used to insert the Optional User Roles for eSupply-EP and eLog modules
	*
	* @exception	SQLException
	*
	*/
	private void insertIntoOptionalRoles(UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;

		try
		{	
			ArrayList	alRoles			=	userModel.getOtherRoleIds();
			ArrayList	alWarehouses	=	userModel.getOtherRoleLocationIds();
			int			otherRolesCount	=	0;
      
  
			if(alWarehouses.size() > 0 && alRoles.size() > 0 && alWarehouses.size() == alRoles.size()) {

        System.out.println("condition crossed i am in if loop");
        
				pstmt	= connection.prepareStatement(insQuery[2]);
				int aRoleSize	=	alRoles.size();
				for(int i=0; i < aRoleSize; i++) {

					pstmt.clearParameters();

					pstmt.setString(1, userModel.getUserId() );
					pstmt.setString(2, (String) alRoles.get(i) );
					pstmt.setString(3, userModel.getLocationId() );
					pstmt.setString(4, (String) alWarehouses.get(i) );

					otherRolesCount	+= pstmt.executeUpdate();
				}
			}
      System.out.println("afetr if loop");
      
			//System.out.println("  otherRolesCount = "+otherRolesCount);

		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Registering the User Roles : \n"+se);
		}		
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}
	} // end of insertIntoUserRoles

	/**
	* this is used to load the attributes from the database
	*
	* @param connection
	* @exception	SQLException
	*
	*/
	private UserModel loadUserMasterAndRole(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		PreparedStatement   	pstmt	                = null;
		ResultSet			        rs	                 	= null;
     // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
    PreparedStatement	    pstmt1                 = null;
    ResultSet		        	rs1		                 = null;
    ArrayList             locationList           = new ArrayList();
    ArrayList             shipmentList           = new ArrayList();
    ArrayList             repOffiecersNameList   = new ArrayList();
    ArrayList             repOffDesignationList  = new ArrayList();
    ArrayList             allotedTime2List       = new ArrayList();
     //WPBN issue 167659 (CR) on 22/04/2009
		UserModel userModel = new UserModel();    
    String    repOffQry     = "SELECT USERNAME,DESIGNATION_ID  FROM FS_USERMASTER WHERE  EMPID =?";
		try
		{
			pstmt	= connection.prepareStatement(loadQuery[0]);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			rs	=	pstmt.executeQuery();
			while(rs.next())
			{
				userModel.setUserName(rs.getString(1));
				userModel.setPassword(rs.getString(2));
				userModel.setEmpId(rs.getString(3));
				userModel.setDepartment(rs.getString(4));
				userModel.setCompanyId(rs.getString(5));
				userModel.setUserLevel(rs.getString(6));
				userModel.setEMailId(rs.getString(7));

				userModel.setVersion( rs.getInt(8));
				userModel.setModifiedBy(rs.getString(9));
				userModel.setModifiedDate(rs.getTimestamp(10));
        userModel.setDesignationId(rs.getString(11));
            //  userModel.setRepOfficersCode(rs.getString(12)); //commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
       // userModel.setAllotedTime(rs.getString(13));//commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
        userModel.setDesignationLevel(rs.getString("LEVEL_NO"));
        //@@Added by Kameswari for the WPBN issue-61303
			  userModel.setPhoneNo(rs.getString("PHONE_NO"));
        userModel.setFaxNo(rs.getString("FAX_NO"));
        userModel.setMobileNo(rs.getString("MOBILE_NO"));
        //@@WPBN issue-61303
//@@Added by subrahmanyam for Enhancement 167668 on 28/04/09
      userModel.setCustAddr1(rs.getString("CUST_ADDRLINE1"));
      userModel.setCustAddr2(rs.getString("CUST_ADDRLINE2"));
      userModel.setCustAddr3(rs.getString("CUST_ADDRLINE3"));
//@@Ended by subrahmanyam for Enhancement 167668 on 28/04/09
      }
       // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
      pstmt	= connection.prepareStatement(loadQuery[4]);
			   
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			rs	=	pstmt.executeQuery();
			pstmt1	= connection.prepareStatement(repOffQry); // Added by Gowtham on 04Feb2011 Loop Leaks
      while(rs.next())
			{
      userModel.setRepOfficersCode(rs.getString("REP_OFFICERS_ID"));
      userModel.setAllotedTime(rs.getString("ALLOTED_TIME"));
      userModel.setShipmentModeCode(rs.getString("SHIPMENT_MODE"));
      locationList.add(userModel.getRepOfficersCode());
      shipmentList.add(userModel.getShipmentModeCode());
      allotedTime2List.add(userModel.getAllotedTime());
   // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009  
      //pstmt1	= connection.prepareStatement(repOffQry);	// Commented by Gowtham on 04Feb2011 Loop Leaks		
			pstmt1.setString(1, userModel.getRepOfficersCode());
			rs1	=	pstmt1.executeQuery();
      if(rs1.next()){
        userModel.setRepOffiecersName(rs1.getString("USERNAME"));
        userModel.setRepOffDesignation(rs1.getString("DESIGNATION_ID"));
       
              }
             
        repOffiecersNameList.add(userModel.getRepOffiecersName());
        repOffDesignationList.add(userModel.getRepOffDesignation());
      }
    userModel.setRepOfficersCode2(locationList); 
    userModel.setShipmentModeCode2(shipmentList);
    userModel.setAllotedTime2(allotedTime2List);
     userModel.setRepOffiecersName2(repOffiecersNameList);
      userModel.setRepOffDesignation2(repOffDesignationList);
  
      if(pstmt!=null)pstmt.close();
      if(rs!=null)rs.close();
       // Commented by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009  
      /*pstmt	= connection.prepareStatement(repOffQry);			
			pstmt.setString(1, userModel.getRepOfficersCode());
			rs	=	pstmt.executeQuery();
      if(rs.next()){
        userModel.setRepOffiecersName(rs.getString("USERNAME"));
        userModel.setRepOffDesignation(rs.getString("DESIGNATION_ID"));
      }
      if(pstmt!=null)pstmt.close();     
			if(rs!=null)
				rs.close();*/
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Loading the User Roles : \n"+se);
		}
		finally
		{
			//ConnectionUtil.closeStatement(pstmt);
			ConnectionUtil.closePreparedStatement(pstmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
      try
      {
      	if(rs1!=null)
        {
            rs1.close();
        }
        if(pstmt1!=null)
        {
             pstmt1.close();  
        }
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
		}
		return userModel;
	} // end of loadUserMasterAndRole(Connection connection)

	/**
	* this is used to load the attributes from the database
	*
	* @param connection
	* @exception	SQLException
	*
	*/
	private UserModel loadUserRole(String userId, String locationId, UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;
		try
		{
			pstmt	= connection.prepareStatement(loadQuery[1]);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			rs	=	pstmt.executeQuery();
			while(rs.next())
			{
				userModel.setRoleId(rs.getString(1));
				userModel.setRoleLocationId(rs.getString(2));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Loading the User Roles : \n"+se);
		}
		finally
		{
			//ConnectionUtil.closeStatement(pstmt);
			ConnectionUtil.closePreparedStatement(pstmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
		}
		return userModel;
	} // end of loadUserRole(Connection connection)


	/**
	* this is used to load the optional roles from the database
	*
	* @param connection
	* @exception	SQLException
	*
	*/
	private UserModel loadOptionalRoles(String userId, String locationId, UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;

		ArrayList	alRoles			=	new ArrayList(10);
		ArrayList	alWarehouses	=	new ArrayList(10);
		
		try
		{
			pstmt	= connection.prepareStatement(loadQuery[2]);
			
			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			rs	=	pstmt.executeQuery();
			
			while(rs.next())
			{
				alRoles.add( rs.getString(1) );
				alWarehouses.add( rs.getString(2) );
			}
			if(rs!=null)
				rs.close();

			userModel.setOtherRoleIds( alRoles );
			userModel.setOtherRoleLocationIds( alWarehouses );
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Loading the User Roles : \n"+se);
		}
		finally
		{
			//ConnectionUtil.closeStatement(pstmt);
			ConnectionUtil.closePreparedStatement(pstmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
		}
		return userModel;
	} // end of loadOptionalRoles(Connection connection)
		
	/**
	* this is used to update the User attributes database from the bean attributes
	*
	* @exception	SQLException
	*
	*/
	private void updateUserMaster(UserModel userModel) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		int resultCount	= 0;

//@@Commented by subrahmanyam for Enhancement 167668 on 28/04/09		
    //String QUERY_NOT_WITH_PASSWORD =  "UPDATE FS_USERMASTER  SET USERNAME = ?, EMPID = ?, DEPARTMENT = ?, COMPANYID = ?, USER_LEVEL = ?, EMAILID = ?, VERSION = ?, MODIFIED_BY = ?, MODIFIED_DATE = ?,DESIGNATION_ID = ? ,REP_OFFICERS_ID = ?, ALLOTED_TIME = ? ,PHONE_NO = ?,FAX_NO = ?,MOBILE_NO = ? WHERE USERID = ? AND LOCATIONID = ?";
//@@Added by subrahmanyam for Enhancement 167668 on 28/04/09     
String QUERY_NOT_WITH_PASSWORD =  "UPDATE FS_USERMASTER  SET USERNAME = ?, EMPID = ?, DEPARTMENT = ?, COMPANYID = ?, USER_LEVEL = ?, EMAILID = ?, VERSION = ?, MODIFIED_BY = ?, MODIFIED_DATE = ?,DESIGNATION_ID = ? ,REP_OFFICERS_ID = ?, ALLOTED_TIME = ? ,PHONE_NO = ?,FAX_NO = ?,MOBILE_NO = ?,CUST_ADDRLINE1=?,CUST_ADDRLINE2=?,CUST_ADDRLINE3=?  WHERE USERID = ? AND LOCATIONID = ?";
 String DELETE_QUERY_FS_REP_OFFICERS_MASTER=" DELETE FROM FS_REP_OFFICERS_MASTER WHERE  USERID = ? AND LOCATIONID = ? ";

		try
		{
			if (userModel.getBackPassword() != null && userModel.getBackPassword().equals("DO NOT UPDATE"))
			{
				pstmt	= connection.prepareStatement(QUERY_NOT_WITH_PASSWORD);
			
				pstmt.setString(1, userModel.getUserName() );				
				pstmt.setString(2, userModel.getEmpId() );
				pstmt.setString(3, userModel.getDepartment() );
				pstmt.setString(4, userModel.getCompanyId() );
				pstmt.setString(5, userModel.getUserLevel() );
				pstmt.setString(6, userModel.getEMailId() );			

				pstmt.setInt(7, userModel.getVersion() );
				pstmt.setString(8, userModel.getModifiedBy() );
				pstmt.setTimestamp(9, userModel.getModifiedDate() );
         pstmt.setString(10, userModel.getDesignationId() );
        pstmt.setString(11, userModel.getRepOfficersCode() );
        pstmt.setString(12, userModel.getAllotedTime() );
         pstmt.setString(13, userModel.getPhoneNo() );
				pstmt.setString(14, userModel.getFaxNo() );
				pstmt.setString(15, userModel.getMobileNo() );  
//@@Commented by subrahmanyam for Enhancement 167668 on 28/04/09        
				/*pstmt.setString(16, userModel.getUserId() );
				pstmt.setString(17, userModel.getLocationId() );*/
//@@Added by subrahmanyam for Enhancement 167668 on 28/04/09        
        pstmt.setString(16, userModel.getCustAddr1() );
        pstmt.setString(17, userModel.getCustAddr2());
        pstmt.setString(18, userModel.getCustAddr3());
        pstmt.setString(19, userModel.getUserId() );
				pstmt.setString(20, userModel.getLocationId() );
//@@Ended by subrahmanyam for Enhancement 167668 on 28/04/09        
       
				resultCount	= pstmt.executeUpdate();
			}
			else
			{
				pstmt	= connection.prepareStatement(updateQuery[0]);
			
				pstmt.setString(1, userModel.getUserName() );
				//pstmt.setString(2, userModel.getPassword() );
				//System.out.println("userModel.getPassword() = "+userModel.getPassword());
				pstmt.setString(2, userModel.getPassword() );
				pstmt.setString(3, userModel.getEmpId() );
				pstmt.setString(4, userModel.getDepartment() );
				pstmt.setString(5, userModel.getCompanyId() );
				pstmt.setString(6, userModel.getUserLevel() );
				pstmt.setString(7, userModel.getEMailId() );			

				pstmt.setInt(8, userModel.getVersion() );
				pstmt.setString(9, userModel.getModifiedBy() );
				pstmt.setTimestamp(10, userModel.getModifiedDate() );
        pstmt.setString(11, userModel.getDesignationId() );
        pstmt.setString(12, userModel.getRepOfficersCode() );
        pstmt.setString(13, userModel.getAllotedTime() );
        //@@Added by Kameswari for the WPBN issue-61303
        pstmt.setString(14, userModel.getPhoneNo());
				pstmt.setString(15, userModel.getFaxNo());
				pstmt.setString(16, userModel.getMobileNo());  
    // commented and modified by phani sekhar for wpbn 175656 on 20090707
    //@@WPBN issue-61303
    /*   pstmt.setString(17, userModel.getUserId());
    pstmt.setString(18, userModel.getLocationId());
    //@@Added by subrahmanyam for Enhancement 167668 on 28/04/09
    pstmt.setString(19, userModel.getCustAddr1());
    pstmt.setString(20, userModel.getCustAddr2());
    pstmt.setString(21, userModel.getCustAddr3());*/
    //@@Ended by subrahmanyam for Enhancement 167668 on 28/04/09
    pstmt.setString(17, userModel.getCustAddr1());
    pstmt.setString(18, userModel.getCustAddr2());
    pstmt.setString(19, userModel.getCustAddr3());   
    pstmt.setString(20, userModel.getUserId());
    pstmt.setString(21, userModel.getLocationId());
    //ends 175656
				resultCount	= pstmt.executeUpdate();
			}
			if(resultCount != 1)
				throw new UserDAOAppException("Error in Updating the User !! resultCount = "+resultCount );
          //added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
        pstmt	= connection.prepareStatement(DELETE_QUERY_FS_REP_OFFICERS_MASTER);  
        	pstmt.setString(1, userModel.getUserId() );
				pstmt.setString(2, userModel.getLocationId() );
        resultCount	= pstmt.executeUpdate();
        for (int i=0;i<userModel.getRepOfficersCode2().size();i++)
						{
            userModel.setRepOfficersCode(userModel.getRepOfficersCode2().get(i).toString());
            userModel.setShipmentModeCode(userModel.getShipmentModeCode2().get(i).toString());
            userModel.setAllotedTime(userModel.getAllotedTime2().get(i).toString());
             pstmt	= connection.prepareStatement(insQuery[4]);
            pstmt.setString(1, userModel.getUserId() );
		      	pstmt.setString(2, userModel.getLocationId().toUpperCase() );
            pstmt.setString(3, userModel.getRepOfficersCode() );
            pstmt.setString(4, userModel.getAllotedTime() );
            pstmt.setInt(5,Integer.parseInt(userModel.getShipmentModeCode()));  
            int resultCount1 = pstmt.executeUpdate();
		  	if(resultCount1 != 1)
				throw new UserDAOAppException("Error in registering the User  !! resultCount = "+resultCount1 );
            }
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Updating the User : \n"+se);
		}
		finally
		{
    //Modified By RajKumari on 23-10-2008 for Connection Leakages.
			//ConnectionUtil.closeStatement(pstmt);
      ConnectionUtil.closePreparedStatement(pstmt);
		}
	} // end of updateUserMaster()


	/**
	* Which is used to remove the record from UserPermissions
	*
	* @exception	SQLException
	*
	*/
	private void removeUserRoles(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[1]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			int resultCount	= pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User Roles : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeUserRoles

	

	/**
	* Which is used to remove the record from Optional User Roles
	*
	* @exception	SQLException
	*
	*/
	private void removeOptionalRoles(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[2]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			
			int resultCount	= pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User Roles : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeOptionalRoles
	
	/**
	* Which is used to remove the record from UserMaster
	*
	* @exception	SQLException
	*
	*/
	private void removeUserMaster(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
      PreparedStatement	pstmt1	= null;
		try
		{
			//@@ Added by subrahmanyam for the wpbn issue: 176576 on 15-jul-09      
       pstmt1 = connection.prepareStatement(delQuery[6]);

			pstmt1.setString(1, (String)userId);
			pstmt1.setString(2, (String)locationId);
			int rCount = pstmt1.executeUpdate();
      if(rCount != 1)
				throw new UserDAOAppException("Error in Removing the User !! resultCount = "+rCount );
//@@ Ended by subrahmanyam for the wpbn issue: 176576 on 15-jul-09
      pstmt = connection.prepareStatement(delQuery[0]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			int resultCount	= pstmt.executeUpdate();
			if(resultCount != 1)
				throw new UserDAOAppException("Error in Removing the User !! resultCount = "+resultCount );
           
        // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009   
//@@ Commented by subrahmanyam for wpbn issue:   176576 on 15-jul-09      
        
/*        pstmt = connection.prepareStatement(delQuery[6]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			resultCount	= pstmt.executeUpdate();
      */
//@@ Ended for 176576  
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
			ConnectionUtil.closeStatement(pstmt1); /* @@ Added by Govind for Closing the Connection(pstmt1))*/

		}

	} // end of removeUserMaster
	
	private void insertUserPreferences(String userId, String locationId, Hashtable userPreferences) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		int prefCount = 0;
		try
		{
			if(userPreferences.size() > 0)
			{
				Iterator itr = userPreferences.keySet().iterator();
				String param 	= "";
				String value		= "";

				pstmt = connection.prepareStatement(insQuery[3]);
				
				while(itr.hasNext() )
				{
					param = (String)itr.next();
                    value = (String)userPreferences.get(param);
					
					pstmt.clearParameters();
					pstmt.setString(1, (String)locationId);
					pstmt.setString(2, (String)userId);
					pstmt.setString(3, (String)param);
                    pstmt.setString(4, (String)value);

					int resultCount	= pstmt.executeUpdate();	
					prefCount++;
						
					if(resultCount != 1 )
						throw new UserDAOAppException("Error in Inserting Userpreferences !! resultCount = "+resultCount );
				}
				if(prefCount != userPreferences.size() )
					throw new UserDAOAppException("Error in Inserting Userpreferences !! prefCount = "+prefCount );
			}
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Inserting Userpreferences : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}
	}

	private void removeUserPreferences(String userId, String locationId) throws DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[3]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			int resultCount	= pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User Preferences : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeUserMaster

	private void removePasswordHistory(String userId, String locationId) throws DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[4]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			int resultCount	= pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User Preferences : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeUser PASSWORD HISTORY

	private void removePreviousPasswordHistory(String userId, String locationId) throws DBSysException
	{
		PreparedStatement	pstmt	= null;
		try
		{
			pstmt = connection.prepareStatement(delQuery[5]);

			pstmt.setString(1, (String)userId);
			pstmt.setString(2, (String)locationId);
			int resultCount	= pstmt.executeUpdate();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Removing the User Preferences : \n"+se);
		}
		finally
		{
			ConnectionUtil.closeStatement(pstmt);
		}

	} // end of removeUser FROM PREVIOUS PASSWORD HISTORY

	private Hashtable loadUserPreferences(String userId, String locationId) throws UserDAOAppException, DBSysException
	{
		PreparedStatement	pstmt	= null;
		ResultSet			rs		= null;
		Hashtable userPreferences = null;
		//Logger.info(fileName,"loadUserPreferences : "+userId + " : "+locationId);
    logger.info(fileName+" loadUserPreferences : "+userId + " : "+locationId);
		try
		{
			pstmt	= connection.prepareStatement(loadQuery[3]);

			pstmt.setString(1, userId);
			pstmt.setString(2, locationId);

			rs	=	pstmt.executeQuery();
			userPreferences = new Hashtable(5);

			while(rs.next())
			{
				userPreferences.put(rs.getString(1), rs.getString(2));
			}
			if(rs!=null)
				rs.close();
		}
		catch(SQLException se)
		{
			throw new DBSysException("SQLException while Loading the User Roles : \n"+se);
		}
		finally
		{
			//System.out.println("User Preferences : "+userPreferences);
			//ConnectionUtil.closeStatement(pstmt);
			ConnectionUtil.closePreparedStatement(pstmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
		}
		return userPreferences;
	} // end of loadUserMasterAndRole(Connection connection)
	
	/**
	 *	This method is added for password encryption using SHA Algorithm with MessageDigest.
	 *  This method is replicated 2-3 places. Later it is shifted to one location.
	 */
    private String encryptSHA(String password)
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA");
        }
        catch(NoSuchAlgorithmException noAlgori)
        {
            //Logger.error("UserDAO", "encryptSHA(String password)","Error while getting MessageDigest for Password Encryption ",noAlgori);
            logger.error("UserDAO"+ " encryptSHA(String password)"+"Error while getting MessageDigest for Password Encryption "+noAlgori);
			throw new EJBException(noAlgori);
        }
        try
        {
            md.update(password.getBytes("UTF-8"));
        }
        catch(UnsupportedEncodingException unSupportErr)
        {
			//Logger.error("UserDAO", "encryptSHA(String password)","Error while generating Encrypted Password ",unSupportErr);
      logger.error("UserDAO"+ " encryptSHA(String password)"+"Error while generating Encrypted Password "+unSupportErr);
			throw new EJBException(unSupportErr);
        }
        byte raw[] = md.digest();
        return (new BASE64Encoder()).encode(raw);
    }

    private void logUserEntry(String userId, String locationId) throws UserDAOAppException
    {
        PreparedStatement pStmt	= null;
		

        try
		{
			pStmt = connection.prepareStatement("INSERT INTO FS_PASSWORDHISTORY (USERID, LOCATIONID, PASSWORDDATE, ALERTCOUNT) VALUES (?,?,?,?)");
            Timestamp entryDate = new Timestamp( new java.util.Date().getTime());
            pStmt.setString(1,userId);
            pStmt.setString(2,locationId);
            pStmt.setTimestamp(3,entryDate);
            pStmt.setInt(4,0);
            int i = pStmt.executeUpdate();
        }
        catch(Exception exp)
        {
            //Logger.error("UserDAO", "logUserEntry(String userId, String locationId)","Error while entry into FS_USERLOG ",exp);
            logger.error("UserDAO"+ " logUserEntry(String userId, String locationId)"+"Error while entry into FS_USERLOG "+exp);
            //throw new UserDAOAppException("Error while entry into FS_USERLOG",exp);
            throw new UserDAOAppException("Error while entry into FS_USERLOG");
        }
        finally
        {
            ConnectionUtil.closeStatement(pStmt);
        }
    }

    /** 
     * This method Adds entry into FS_USERLOG when user changes password.
     */
    private void upDateUserEntry(String userId, String locationId) throws UserDAOAppException
    {
        PreparedStatement pStmt	= null;
		
		System.out.println("called upDateUserEntry");
        try
		{
			pStmt = connection.prepareStatement("UPDATE FS_PASSWORDHISTORY SET PASSWORDDATE = ?, ALERTCOUNT  = ? WHERE USERID = ? AND LOCATIONID = ? ");
        
            
            pStmt.setTimestamp(1,new Timestamp( new java.util.Date().getTime()));
            pStmt.setInt(2,0);
            pStmt.setString(3,userId);
            pStmt.setString(4,locationId);

            int i = pStmt.executeUpdate();
            //System.out.println("count is "+i);
        }
        catch(Exception exp)
        {
            //Logger.error("UserDAO", "upDateUserEntry(String userId, String locationId)","Error while entry into FS_USERLOG ",exp);
            logger.error("UserDAO"+ " upDateUserEntry(String userId, String locationId)"+"Error while entry into FS_USERLOG "+exp);
            //throw new UserDAOAppException("Error while entry into FS_USERLOG",exp);
            throw new UserDAOAppException("Error while entry into FS_USERLOG");
        }
        finally
        {
            ConnectionUtil.closeStatement(pStmt);
        }
    }
	
	private void recordPasswordHistory(String userId, String locationId, String password) throws UserDAOAppException
    {
        PreparedStatement pStmt	= null;
		
        try
		{
			pStmt = connection.prepareStatement("INSERT INTO FS_PREVIOUSPASSWORDHISTORY (USERID, LOCATIONID, PASSWORD, PASSWORDDATE) VALUES (?,?,?,?)");
            Timestamp entryDate = new Timestamp( new java.util.Date().getTime());
            pStmt.setString(1,userId);
            pStmt.setString(2,locationId);
            pStmt.setString(3,password);
            pStmt.setTimestamp(4,entryDate);
            pStmt.executeUpdate();
        }
        catch(Exception exp)
        {
            //Logger.error("UserDAO", "recordPasswordHistory(String userId, String locationId, String password)","Error while entry into FS_PREVIOUSPASSWORDHISTORY ",exp);
            logger.error("UserDAO"+ " recordPasswordHistory(String userId, String locationId, String password)"+"Error while entry into FS_PREVIOUSPASSWORDHISTORY "+exp);
            //throw new UserDAOAppException("Error while entry into FS_USERLOG",exp);
            throw new UserDAOAppException("Error while entry into FS_PREVIOUSPASSWORDHISTORY");
        }
        finally
        {
            ConnectionUtil.closeStatement(pStmt);
        }
    }
/** 
     * This method Adds entry into FS_PREVIOUSPASSWORDHISTORY when user changes password.
     */
    private void updatePasswordHistory(String userId, String locationId, String password) throws UserDAOAppException
    {
        
		PreparedStatement pStmt		= null;
		PreparedStatement pStmt1	= null;
		ResultSet rs				= null;
        try
		{
			pStmt=connection.prepareStatement("Select count(*) from FS_PREVIOUSPASSWORDHISTORY where USERID=? AND LOCATIONID=?");
			pStmt.setString(1,userId);
            pStmt.setString(2,locationId);
            rs=pStmt.executeQuery();
			int five=0;
			if(rs.next())
			{
			five=rs.getInt(1);
			}
			rs.close();
      //Added By RajKumari on 23-10-2008 for Connection Leakages.
      if(pStmt!=null)
      pStmt.close();
			pStmt=null;
			int prv_set = 3;
			pStmt=connection.prepareStatement("Select PREVIOUS_PWDS from FS_PASSWORDCONFIGURATION");
			rs=pStmt.executeQuery();
			
			if(rs.next())
			{
			prv_set=rs.getInt(1);
			}
			rs.close();
			
			if(five < prv_set)
			{
			recordPasswordHistory(userId,locationId,password);
			}
			else{
			pStmt1 = connection.prepareStatement("UPDATE FS_PREVIOUSPASSWORDHISTORY SET PASSWORD = ?, PASSWORDDATE  = ? WHERE USERID = ? AND LOCATIONID = ? AND PASSWORDDATE =(SELECT MIN(PASSWORDDATE) FROM FS_PREVIOUSPASSWORDHISTORY WHERE USERID = ? AND LOCATIONID = ?)");
            pStmt1.setString(1,password);
            pStmt1.setTimestamp(2,new Timestamp( new java.util.Date().getTime()));
            pStmt1.setString(3,userId);
            pStmt1.setString(4,locationId);
			pStmt1.setString(5,userId);
            pStmt1.setString(6,locationId);
			pStmt1.executeUpdate();
			}            
		}
        catch(Exception exp)
        {
            //Logger.error("UserDAO", "FS_PREVIOUSPASSWORDHISTORY(String userId, String locationId)","Error while entry into FS_PREVIOUSPASSWORDHISTORY ",exp);
            logger.error("UserDAO"+ " FS_PREVIOUSPASSWORDHISTORY(String userId, String locationId)"+" Error while entry into FS_PREVIOUSPASSWORDHISTORY "+exp);
            //throw new UserDAOAppException("Error while entry into FS_USERLOG",exp);
            throw new UserDAOAppException("Error while entry into FS_PREVIOUSPASSWORDHISTORY");
        }
        finally
        {
          //ConnectionUtil.closeStatement(pStmt);
        	ConnectionUtil.closePreparedStatement(pStmt,rs);// Added by Dilip for PMD Correction on 22/09/2015
			ConnectionUtil.closeStatement(pStmt1);
          
        }
    }

	private void unlockUser(String userId, String locationId) throws UserDAOAppException
		{

		PreparedStatement pStmt = null;
		try
		{
		// INACTIVE =0;
		pStmt = connection.prepareStatement("UPDATE FS_USERMASTER SET USERSTATUS=1 WHERE USERID=? AND LOCATIONID=?");
		pStmt.setString(1,userId);
		pStmt.setString(2,locationId);
		pStmt.executeUpdate();
						
		}catch(Exception exc)
		{
			//Logger.error("UserDAO","unlockUser(String userId, String locationId)","EXCEPTION IS ",exc);	
      logger.error("UserDAO"+" unlockUser(String userId, String locationId)"+"EXCEPTION IS "+exc);	
			throw new UserDAOAppException("Error while entry into FS_USERMASTER");
		}
		finally
		{
			try
			{
				ConnectionUtil.closeStatement(pStmt);
			}
			catch(Exception exp)
			{
				//Logger.error("UserDAO","unlockUser(String userId, String locationId)","EXCEPTION IS ",exp);	
        logger.error("UserDAO"+" unlockUser(String userId, String locationId)"+" EXCEPTION IS "+exp);	
			}
		}
		
	
	}
}


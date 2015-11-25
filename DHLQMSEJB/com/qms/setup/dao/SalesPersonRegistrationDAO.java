/*
	Program Name	:SalesPersonRegistrationDAO.java
	Module Name		:QMSSetup
	Task			    :SalesPersonRegistration EntityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 28,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.dao;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.setup.java.SalesPersonRegistrationDOB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class SalesPersonRegistrationDAO 
{
      public static final String   FILE_NAME             =  "SalesPersonRegistrationDAO.java";
      OperationsImpl               operationsImpl        =  null;
      Connection                   connection            =  null;
      private static Logger logger = null;
      public SalesPersonRegistrationDAO()
      {
        logger  = Logger.getLogger(SalesPersonRegistrationDAO.class);
      }
      
      public void create(SalesPersonRegistrationDOB dob)
      {    
        try
        {    
            getConnection();    
            insertSalesPersonDetailsDetails(dob);
        }
        catch(Exception e)
        {
          ConnectionUtil.closeConnection(connection);
        }
      }
      
      public boolean findByPrimaryKey(String salesPersonCode) throws javax.ejb.ObjectNotFoundException
      {
        boolean                 hasRows	    =  false;
        String                  pkQuery     =  null;
        PreparedStatement       pstmt       =  null;
        ResultSet               rs          =  null;
        
        try
        {
            pkQuery                  =   "SELECT SALESPERSON_CODE FROM QMS_SALESPERSON_REG WHERE SALESPERSON_CODE =?";
            operationsImpl           =   new OperationsImpl();
            connection               =   operationsImpl.getConnection();
            pstmt                    =   connection.prepareStatement(pkQuery);
            pstmt.setString(1,(String)salesPersonCode);				
            rs = pstmt.executeQuery();
            if(rs.next())
            {
               hasRows = true;
            }			
        }
        catch(SQLException se)
        {
          //Logger.error(FILE_NAME, "findByPrimaryKey(String salesPersonCode)","Error in Find UserModel - SQLException ",se);
          logger.error(FILE_NAME+ "findByPrimaryKey(String salesPersonCode)"+"Error in Find UserModel - SQLException "+se);
        }
        catch(Exception e)
        {
          //Logger.error(FILE_NAME, "findByPrimaryKey(String salesPersonCode)","Error in Find UserModel - Exception ",e);
          logger.error(FILE_NAME+ "findByPrimaryKey(String salesPersonCode)"+"Error in Find UserModel - Exception "+e);
        }
        finally
        {
          ConnectionUtil.closeConnection(connection, pstmt,rs);
        }
        if(hasRows)
          return true;
        else
          throw new javax.ejb.ObjectNotFoundException("Could not find bean");
    
      }
      public SalesPersonRegistrationDOB load(String salesPersonCode)
      {
           SalesPersonRegistrationDOB  dob    =    null;
        try
        {
            operationsImpl           =   new OperationsImpl();
            connection               =   operationsImpl.getConnection();
            dob                      =   loadSalesPersonDetails(salesPersonCode,connection);
        }
        catch(Exception e)
        {
            e.printStackTrace();      
        }
        finally
        {
           ConnectionUtil.closeConnection(connection);
        }
        return dob;
      }
      
      public void store(SalesPersonRegistrationDOB dob)
      {
        try
        {
            operationsImpl           =   new OperationsImpl();
            connection               =   operationsImpl.getConnection();
            updateSalesPersonDetails(dob,connection);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
           ConnectionUtil.closeConnection(connection);
        }
      }
      public void remove(String salesPersonCode)
      {
        try
        {
            
            operationsImpl           =   new OperationsImpl();
            connection               =   operationsImpl.getConnection();
            removeSalesPersonDetails(salesPersonCode,connection);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
           ConnectionUtil.closeConnection(connection);
        }
      }
      private void insertSalesPersonDetailsDetails(SalesPersonRegistrationDOB dob )
      {
          String                    insQuery      =        "INSERT INTO QMS_SALESPERSON_REG (LOCATION_ID ,TERMINAL_ID ,SALESPERSON_CODE ,SALESPERSON_NAME ,DESIGNATION,LEVELID  ,DOJ ,ADDRESSID ,REP_OFFICERS_CODE ,ALLOTED_TIME,REMARKS ,REP_OFFICERS_NAME ,REP_OFFICERS_LEVEL,REP_OFFICERS_DESIGNATION,INVALIDATE  ) VALUES (?,?,?,?,?,?,TO_DATE(?,'DD/MM/YY'),?,?,?,?,?,?,?,?)";            
          String                    addressQuery  =        "INSERT INTO FS_ADDRESS(ADDRESSID  ,ADDRESSLINE1  ,ADDRESSLINE2  ,CITY  ,STATE ,ZIPCODE   ,COUNTRYID  ,PHONENO  ,EMAILID  ,FAX ,MOBILENO ) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
          PreparedStatement         pStmt         =        null;      
          
          try
          {
             pStmt                    =   connection.prepareStatement(insQuery);
             pStmt.setString(1,dob.getLocationId()); 
             pStmt.setString(2,dob.getTerminalId()); 
             pStmt.setString(3,dob.getSalesPersonCode()); 
             pStmt.setString(4,dob.getSalesPersonName()); 
             pStmt.setString(5,dob.getDesignation()); 
             pStmt.setString(6,dob.getLevel()); 
             pStmt.setString(7,dob.getDateOfJoining()); 
             pStmt.setString(8,dob.getAddressId()); 
             pStmt.setString(9,dob.getRepOffCode()); 
             pStmt.setString(10,dob.getTime()); 
             pStmt.setString(11,dob.getRemarks()); 
             pStmt.setString(12,dob.getRepOffName()); 
             pStmt.setString(13,dob.getSuperLevel()); 
             pStmt.setString(14,dob.getSuperDesignationId()); 
             pStmt.setString(15,"F"); 
             pStmt.executeUpdate();
             
             if(pStmt!=null)
               pStmt.close();
               
             pStmt                    =   connection.prepareStatement(addressQuery);
             pStmt.setString(1,dob.getAddressId()); 
             pStmt.setString(2,dob.getAddressLine1()); 
             pStmt.setString(3,dob.getAddressLine2()); 
             pStmt.setString(4,dob.getCity()); 
             pStmt.setString(5,dob.getState()); 
             pStmt.setString(6,dob.getZipCode()); 
             pStmt.setString(7,dob.getCountryId()); 
             pStmt.setString(8,dob.getPhoneNo()); 
             pStmt.setString(9,dob.getEmailid()); 
             pStmt.setString(10,dob.getFax());        
             pStmt.setString(11,dob.getMobilePhoneNo());
             pStmt.executeUpdate();
            
             
          }
          catch(Exception e)
          {
            e.printStackTrace();
            //Logger.error(FILE_NAME,"Error while inserting Record"+e.toString());
            logger.error(FILE_NAME+"Error while inserting Record"+e.toString());
            
          }
          finally
          {
            ConnectionUtil.closeConnection(connection,pStmt);
          }
      }
      private void updateSalesPersonDetails(SalesPersonRegistrationDOB dob,Connection connection)
      {
          String                    updateQuery             =        "UPDATE QMS_SALESPERSON_REG SET LOCATION_ID =?,TERMINAL_ID = ? ,SALESPERSON_NAME =?,DESIGNATION=?,LEVELID =? ,DOJ=TO_DATE(?,'DD/MM/YY') ,REP_OFFICERS_CODE =?,ALLOTED_TIME=?,REMARKS =?,REP_OFFICERS_NAME =?,REP_OFFICERS_LEVEL=?,REP_OFFICERS_DESIGNATION=?  WHERE  SALESPERSON_CODE =?";      
          String                    updateAddressQuery      =        "UPDATE FS_ADDRESS SET ADDRESSLINE1=?  ,ADDRESSLINE2=? ,CITY=?  ,STATE=? ,ZIPCODE =?  ,COUNTRYID=?  ,PHONENO =? ,EMAILID=?  ,FAX=? ,MOBILENO =? WHERE ADDRESSID=(SELECT ADDRESSID FROM QMS_SALESPERSON_REG WHERE SALESPERSON_CODE =?)";           
          PreparedStatement         pStmt                   =        null;
         // ResultSet                 resultSet               =        null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
          
          try
          {         
             pStmt                    =   connection.prepareStatement(updateQuery);         
             pStmt.setString(1,dob.getLocationId());
             pStmt.setString(2,dob.getTerminalId());
             pStmt.setString(3,dob.getSalesPersonName());
             pStmt.setString(4,dob.getDesignation());
             pStmt.setString(5,dob.getLevel());
             pStmt.setString(6,dob.getDateOfJoining());
             pStmt.setString(7,dob.getRepOffCode());
             pStmt.setString(8,dob.getTime());
             pStmt.setString(9,dob.getRemarks());
             pStmt.setString(10,dob.getRepOffName());
             pStmt.setString(11,dob.getSuperLevel());
             pStmt.setString(12,dob.getSuperDesignationId());
             pStmt.setString(13,dob.getSalesPersonCode());
             int count = pStmt.executeUpdate();
             
             if(pStmt!=null)
               pStmt.close();
               
             pStmt                    =   connection.prepareStatement(updateAddressQuery);
              pStmt.setString(1,dob.getAddressLine1());
              pStmt.setString(2,dob.getAddressLine2());
              pStmt.setString(3,dob.getCity());
              pStmt.setString(4,dob.getState());
              pStmt.setString(5,dob.getZipCode());
              pStmt.setString(6,dob.getCountryId());
              pStmt.setString(7,dob.getPhoneNo());
              pStmt.setString(8,dob.getEmailid());
              pStmt.setString(9,dob.getFax());
              pStmt.setString(10,dob.getMobilePhoneNo());
              pStmt.setString(11,dob.getSalesPersonCode());
    
              count = pStmt.executeUpdate();
             
          }
          catch(Exception e)
          {
            e.printStackTrace();
            //Logger.error(FILE_NAME,"Error while inserting Record"+e.toString());
            logger.error(FILE_NAME+"Error while inserting Record"+e.toString());
            
          }
          finally
          {
            ConnectionUtil.closeConnection(connection,pStmt,null);//Modified By RajKumari on 27-10-2008 for Connection Leakages.
          }
      }
      private boolean removeSalesPersonDetails(String salesPersonCode,Connection connection)
      {
          String                    delAddressQuery      =        "DELETE FROM FS_ADDRESS WHERE ADDRESSID= (SELECT ADDRESSID FROM QMS_SALESPERSON_REG WHERE SALESPERSON_CODE=?)";        
          String                    delQuery             =        "DELETE FROM QMS_SALESPERSON_REG WHERE SALESPERSON_CODE=?";        
          PreparedStatement         pStmt         =        null;
          //ResultSet                 resultSet     =        null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
          boolean                   flag          =        false;
          
          try
          {
             
             pStmt                    =   connection.prepareStatement(delAddressQuery);
             pStmt.setString(1,salesPersonCode);  
             flag                     =   pStmt.execute();
             //Added By RajKumari on 27-10-2008 for Connection Leakages.
             if(pStmt!=null)
             {
               pStmt.close();
               pStmt = null;
             }
             pStmt                    =   connection.prepareStatement(delQuery);
             pStmt.setString(1,salesPersonCode);  
             flag                     =   pStmt.execute();
             
          }
          catch(Exception e)
          {
            e.printStackTrace();
            //Logger.error(FILE_NAME,"Error while Deleting Record"+e.toString());
            logger.error(FILE_NAME+"Error while Deleting Record"+e.toString());
            ConnectionUtil.closeConnection(connection,pStmt,null);//Modified By RajKumari on 27-10-2008 for Connection Leakages.
            return false;
          }finally{
        	  ConnectionUtil.closePreparedStatement(pStmt);// Added by Dilip for PMD Correction on 22/09/2015
          }
          return flag;
      }
      public SalesPersonRegistrationDOB loadSalesPersonDetails(String salesPersonCode,Connection connection)
      {
          String                    loadQuery      =        "SELECT LOCATION_ID ,TERMINAL_ID ,SALESPERSON_CODE ,SALESPERSON_NAME ,DESIGNATION,LEVELID  ,TO_CHAR(DOJ,'DD/MM/YY') ,S.ADDRESSID ,REP_OFFICERS_CODE ,ALLOTED_TIME,REMARKS ,REP_OFFICERS_NAME ,REP_OFFICERS_LEVEL,REP_OFFICERS_DESIGNATION,INVALIDATE ,ADDRESSLINE1  ,ADDRESSLINE2  ,CITY  ,STATE ,ZIPCODE   ,COUNTRYID  ,PHONENO  ,EMAILID  ,FAX ,MOBILENO,INVALIDATE  FROM QMS_SALESPERSON_REG S,FS_ADDRESS A WHERE S.ADDRESSID =A.ADDRESSID AND SALESPERSON_CODE =?"; 
          PreparedStatement         pStmt         =        null;
          ResultSet                 resultSet     =        null;
          SalesPersonRegistrationDOB dob          =        null;
          
          try
          {
          
             pStmt                    =   connection.prepareStatement(loadQuery);
             pStmt.setString(1,salesPersonCode);        
             resultSet                =   pStmt.executeQuery();
             
             if(resultSet.next())
             {
             
               dob           =      new SalesPersonRegistrationDOB();
               dob.setLocationId(resultSet.getString(1)!=null?resultSet.getString(1):"");
               dob.setTerminalId(resultSet.getString(2)!=null?resultSet.getString(2):"");
               dob.setSalesPersonCode(resultSet.getString(3)!=null?resultSet.getString(3):"");
               dob.setSalesPersonName(resultSet.getString(4)!=null?resultSet.getString(4):"");
               dob.setDesignation(resultSet.getString(5)!=null?resultSet.getString(5):"");
               dob.setLevel(resultSet.getString(6)!=null?resultSet.getString(6):"");
               dob.setDateOfJoining(resultSet.getString(7)!=null?resultSet.getString(7):"");
               dob.setAddressId(resultSet.getString(8)!=null?resultSet.getString(8):"");
               dob.setRepOffCode(resultSet.getString(9)!=null?resultSet.getString(9):"");
               dob.setTime(resultSet.getString(10)!=null?resultSet.getString(10):"");
               dob.setRemarks(resultSet.getString(11)!=null?resultSet.getString(11):"");
               dob.setRepOffName(resultSet.getString(12)!=null?resultSet.getString(12):"");
               dob.setSuperLevel(resultSet.getString(13)!=null?resultSet.getString(13):"");
               dob.setSuperDesignationId(resultSet.getString(14)!=null?resultSet.getString(14):"");
               dob.setInvalidate(resultSet.getString(15)!=null?resultSet.getString(15):"");
               dob.setAddressLine1(resultSet.getString(16)!=null?resultSet.getString(16):"");
               dob.setAddressLine2(resultSet.getString(17)!=null?resultSet.getString(17):"");
               dob.setCity(resultSet.getString(18)!=null?resultSet.getString(18):"");
               dob.setState(resultSet.getString(19)!=null?resultSet.getString(19):"");
               dob.setZipCode(resultSet.getString(20)!=null?resultSet.getString(20):"");
               dob.setCountryId(resultSet.getString(21)!=null?resultSet.getString(21):"");
               dob.setPhoneNo(resultSet.getString(22)!=null?resultSet.getString(22):"");
               dob.setEmailid(resultSet.getString(23)!=null?resultSet.getString(23):"");
               dob.setFax(resultSet.getString(24)!=null?resultSet.getString(24):"");
               dob.setMobilePhoneNo(resultSet.getString(25)!=null?resultSet.getString(25):"");  
               
             }
             
          }
          catch(Exception e)
          {
            e.printStackTrace();
            //Logger.error(FILE_NAME,"Error while inserting Record"+e.toString());        
            logger.error(FILE_NAME+"Error while inserting Record"+e.toString());        
            return null;
          }
          finally
          {
            ConnectionUtil.closeConnection(connection,pStmt,resultSet);
          }
          return dob;
      }
      public void getConnection()
      {
        try
        {
          operationsImpl           =   new OperationsImpl();
          connection               =   operationsImpl.getConnection();
        }
        catch(Exception e)
        {
          
        }
      }
}
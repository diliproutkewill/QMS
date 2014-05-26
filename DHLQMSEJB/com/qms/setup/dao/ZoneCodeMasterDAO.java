package com.qms.setup.dao;
//import com.foursoft.esupply.common.util.Logger;
import java.sql.Types;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger; 
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.etrans.common.util.ejb.sls.OIDSession;
import com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.setup.java.ZoneCodeChildDOB;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import oracle.jdbc.OracleTypes;

/**
 * Used as a Data Access Object for the Zone Code & Canada Zone Code Modules
 */
public class ZoneCodeMasterDAO 
{
  OperationsImpl    operationsImpl    =   null;
  Connection        connection        =   null;
  private transient DataSource dataSource = null;
  static final String FILE_NAME       = "ZoneCodeMasterDAO.java";
  private static Logger logger = null;
  
  public ZoneCodeMasterDAO()
  {
    logger  = Logger.getLogger(ZoneCodeMasterDAO.class);
    try
    {
      InitialContext ic	= new InitialContext();
      dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
    }
    catch(NamingException nmEx)
    {
      logger.error(FILE_NAME+"ZoneCodeMasterDAO naming exception "+nmEx.toString());
    }
  }
  /**
	*@return Connection
	*gets the connection from the pool
	*/
  private Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
 public ArrayList create(ZoneCodeMasterDOB dob)
 {
  ArrayList         duplicateList   = null;
  ZoneCodeChildDOB  childDOB        = null;
  try
  {
    dob = insertZoneCodeDetails(dob);
    if(dob.getRemarks()!=null && dob.getRemarks().length()>0)
    {
      dob.setRemarks("");
      duplicateList = new ArrayList();
      for(int i=0;i<dob.getZoneCodeList().size();i++)
      {
        childDOB = (ZoneCodeChildDOB)dob.getZoneCodeList().get(i);
        if(childDOB.getRemarks()!=null && childDOB.getRemarks().length()>0)
            duplicateList.add(childDOB.getZone());
      }
    }
  }
  catch(Exception e)
  {
    e.printStackTrace();
    throw new EJBException("Error while inserting Details");
  }
  return duplicateList;
 }
 public boolean findByPrimaryKey(String zoneCode)
 {
        boolean                 hasRows	    =  false;
        String                  pkQuery     =  null;
        PreparedStatement       pstmt       =  null;
        ResultSet               rs          =  null;
        
        try
        {
            pkQuery                  =   "SELECT ZONE_CODE  FROM QMS_ZONE_CODE_MASTER WHERE ZONE_CODE  =?";
            operationsImpl           =   new OperationsImpl();
            connection               =   operationsImpl.getConnection();
            pstmt                    =   connection.prepareStatement(pkQuery);
            pstmt.setString(1,zoneCode);				
            rs = pstmt.executeQuery();
            if(rs.next())
            {
               hasRows = true;
            }			
        }
        catch(SQLException se)
        {
          //Logger.error(FILE_NAME, "findByPrimaryKey(String zoneCode)","Error in Find UserModel - SQLException ",se);
          logger.error(FILE_NAME+ "findByPrimaryKey(String zoneCode)"+"Error in Find UserModel - SQLException "+se);
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
          return false;
 }
 public ZoneCodeMasterDOB load(String location,String zoneCodeType,String shipmentMode,String consoleType ,String operation)
 {
    ZoneCodeMasterDOB dob  =  null;
   try
   {
      dob = loadZoneCodeDetails(location,zoneCodeType,shipmentMode,consoleType,operation);
   }
   catch(Exception e)
   {
     e.printStackTrace();
     return null;
   }
   return dob;
 }
 public ArrayList store(ZoneCodeMasterDOB dob)
 {
   //boolean flag    =  false;
   ArrayList  duplicateList = null;
   try
   {
     duplicateList =  updateZoneCodeDetails(dob);
   }
   catch(Exception e)
   {
     e.printStackTrace();
     throw new EJBException("Error while updating the details"+e.toString());
   }
   return duplicateList;
 }
//@@ COmmented & added by subrahmanyam for 216629 on 31-AUG-10
 //public boolean remove(ZoneCodeMasterDOB dob)
 public String remove(ZoneCodeMasterDOB dob)
 {
    boolean flag  =  false;
    String 			remainingZones			= "";
    
   try
   {
//	    @@ COmmented & added by subrahmanyam for 216629 on 31-AUG-10
    // flag    =  deleteZoneCodeDetails(dob);
	   remainingZones    =  deleteZoneCodeDetails(dob);
   }
   catch(Exception e)
   {
     e.printStackTrace();
     throw new EJBException("Error while deleting the details"+e.toString());
   }
// @@ COmmented & added by subrahmanyam for 216629 on 31-AUG-10
   //return flag;
   return remainingZones;
 }
 
 private ZoneCodeMasterDOB insertZoneCodeDetails(ZoneCodeMasterDOB dob)throws SQLException
 {
   String selectQuery         =  "SELECT ZONE_CODE FROM QMS_ZONE_CODE_MASTER WHERE ORIGIN_LOCATION=? AND ZIPCODE_TYPE=? AND SHIPMENT_MODE = ? AND NVL(CONSOLE_TYPE,'~') = ?";   
   String selectMaxQuery      =  "SELECT MAX(ROWNO) FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE=?"; 
   String insertMasterQuery   =  "INSERT INTO QMS_ZONE_CODE_MASTER(ZONE_CODE,ORIGIN_LOCATION,TERMINALID,CITY,STATE,ZIPCODE_TYPE,INVALIDATE,SHIPMENT_MODE,CONSOLE_TYPE) VALUES(?,?,?,?,?,?,?,?,?)";
   String insertDetailQuery   =  "INSERT INTO QMS_ZONE_CODE_DTL VALUES(?,?,?,?,?,?,?,?,?)";
   PreparedStatement pstmt    =  null;
   PreparedStatement pstmtDtl =  null;
   ResultSet   rs             =  null;   
   String   zoneCode            =    null;
   ZoneCodeChildDOB   childDOB  =    new ZoneCodeChildDOB();
   int      k                   =    0;
   int      maxRow              =    0;
   ArrayList childDOBList         =    null;
   ArrayList updatedChildDOBList=    new ArrayList(); 
   ArrayList duplicateList      =    new ArrayList();
   Integer check                 =   null;
   OIDSession               oidRemote   =     null;
   OIDSessionHome           oidHome     =     null;
   InitialContext           ictxt       =     null;
   CallableStatement        cstmt       =     null;
   try
   {
     //childDOBList             =   new ArrayList();
     childDOBList             =   dob.getZoneCodeList();     
     
     operationsImpl           =   new OperationsImpl();
     connection               =   operationsImpl.getConnection();
     pstmt                    =   connection.prepareStatement(selectQuery);
     pstmtDtl                 =   connection.prepareStatement(selectMaxQuery);
     pstmt.setString(1,dob.getOriginLocation());
     pstmt.setString(2,dob.getZipCode());
     pstmt.setString(3,dob.getShipmentMode());
     if("1".equalsIgnoreCase(dob.getShipmentMode()))
        pstmt.setString(4,"~");
     else
        pstmt.setString(4,dob.getConsoleType());
     //pstmt.setString(4,dob.getPort());
     rs   =  pstmt.executeQuery();
     if(rs.next()){
       zoneCode = rs.getString(1);
       pstmtDtl.setString(1,zoneCode);
       if(rs!=null)
         rs.close();
       rs  = pstmtDtl.executeQuery();
       if(rs.next())
         maxRow  =  Integer.parseInt(rs.getString(1)!=null?rs.getString(1):"0");
       dob.setZoneCode(zoneCode);
     }
     else
     {
          ictxt                                =          new InitialContext();         
          oidHome                              =          (OIDSessionHome)ictxt.lookup("OIDSessionBean");
          oidRemote                            =          (OIDSession)oidHome.create();
          dob.setZoneCode(new Long(oidRemote.getZoneCodeId()).toString());
     }
       
     if(zoneCode==null)
     {
       if(pstmt!=null)
         pstmt.close();
        pstmt                    =   connection.prepareStatement(insertMasterQuery);  
        
       pstmt.setString(1,dob.getZoneCode());
       pstmt.setString(2,dob.getOriginLocation());
       pstmt.setString(3,dob.getTerminalId());
       pstmt.setString(4,dob.getCity()!=null?dob.getCity():"");
       pstmt.setString(5,dob.getState()!=null?dob.getState():"");
       pstmt.setString(6,dob.getZipCode());
       // pstmt.setString(7,dob.getPort());
       pstmt.setString(7,"F");
       pstmt.setString(8,dob.getShipmentMode());
       
       if("1".equalsIgnoreCase(dob.getShipmentMode()))
            pstmt.setNull(9,Types.VARCHAR);
        else
            pstmt.setString(9,dob.getConsoleType());
      
      
       k = pstmt.executeUpdate();
     }
     
     
     if(pstmtDtl!=null)
      pstmtDtl.close();
     if(pstmt!=null)
        pstmt.close();
      
     pstmtDtl                 =   connection.prepareStatement(insertDetailQuery);
     //pstmt                    =   connection.prepareStatement(selectDtlQuery);     
     cstmt                    =   connection.prepareCall("{?= call qms_setups.zone_code_dtl_fun(?,?,?,?,?,?,?,?)}");
     int chldDOBListSize	= childDOBList.size();
     for(int i=0;i<chldDOBListSize;i++)
     {   
         childDOB     =  (ZoneCodeChildDOB)childDOBList.get(i);  
         //System.out.println("childDOB.getFromZipCode()  "+childDOB.getFromZipCode()+" childDOB.getToZipCode() "+childDOB.getToZipCode()+" childDOB.getZone() "+childDOB.getZone()+" dob.getOriginLocation() "+dob.getOriginLocation()+" dob.getTerminalId() "+dob.getTerminalId()+" dob.getTerminalId() "+dob.getTerminalId());
        
        cstmt.clearParameters();
        cstmt.registerOutParameter(1,OracleTypes.INTEGER);
        cstmt.setString(2,childDOB.getFromZipCode());
        cstmt.setString(3,childDOB.getToZipCode());
        cstmt.setString(4,childDOB.getZone());        
        if("numeric".equalsIgnoreCase(dob.getZipCode().toUpperCase()))
           cstmt.setString(5,"");
        else
            cstmt.setString(5,childDOB.getAlphaNumaric().toUpperCase());
        cstmt.setString(6,dob.getOriginLocation());
        cstmt.setString(7,dob.getZipCode());
        cstmt.setString(8,dob.getShipmentMode());
        
        if("1".equalsIgnoreCase(dob.getShipmentMode()))
            cstmt.setNull(9,Types.VARCHAR);
        else
          cstmt.setString(9,dob.getConsoleType());
        
        cstmt.execute();
        check  =  (Integer)cstmt.getObject(1);
       
        //System.out.println("check "+check);
        if(check.intValue()==1)
        {
            pstmtDtl.setString(1,dob.getZoneCode());
            pstmtDtl.setString(2,childDOB.getFromZipCode());
            pstmtDtl.setString(3,childDOB.getToZipCode());
            pstmtDtl.setString(4,childDOB.getZone());
            pstmtDtl.setString(5,childDOB.getEstimationTime());
            pstmtDtl.setString(6,childDOB.getEstimatedDistance());
             if("numeric".equalsIgnoreCase(dob.getZipCode()))
                 pstmtDtl.setString(7,"");
             else
                 pstmtDtl.setString(7,childDOB.getAlphaNumaric());
            pstmtDtl.setString(8,new Integer((maxRow+1)).toString());
            pstmtDtl.setString(9,"F");        
            k=pstmtDtl.executeUpdate();
            maxRow++;
        }
        else if(check.intValue()==0)
        {
          childDOB.setRemarks("The Zip Codes already exist for this location "+dob.getOriginLocation()+" for Zone Code "+childDOB.getZone());
          dob.setRemarks("Failed");
        }
        updatedChildDOBList.add(childDOB);
        check   =  null;
     }
     dob.setZoneCodeList(updatedChildDOBList);
   }
   catch(SQLException e)
   {
     e.printStackTrace();
     //Logger.error(FILE_NAME,"Exception in ZoneCode master DAO "+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode master DAO "+e.toString());
     throw new SQLException("Error while inserting Details");
   }
   catch(Exception e)
   {
     e.printStackTrace();
     //Logger.error(FILE_NAME,"Exception in ZoneCode master DAO "+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode master DAO "+e.toString());
     throw new EJBException("Error while inserting Details");
   }
   finally
   {
     try
     {
      if(cstmt!=null)
         cstmt.close();
      ConnectionUtil.closeConnection(connection,pstmtDtl,rs);
      ConnectionUtil.closeConnection(null,pstmt);
     
       
      childDOB   =  null;
      operationsImpl  =  null;
      childDOBList    =  null;
     }
     catch(Exception e)
     {
       e.printStackTrace();
       throw new EJBException("Error while closing the connections");  
     }
   }
   //return duplicateList;
   return dob;
 }
 private ArrayList updateZoneCodeDetails(ZoneCodeMasterDOB dob)throws SQLException
 {
     //String            updateMasterQuery   =   "UPDATE QMS_ZONE_CODE_MASTER SET TERMINALID = ?  WHERE ZONE_CODE =? ";
     String            updateDtlQuery      =   "UPDATE QMS_ZONE_CODE_DTL SET FROM_ZIPCODE=? , TO_ZIPCODE =? , ZONE =? , ESTIMATED_TIME =? , ESTIMATED_DISTANCE =? , ALPHANUMERIC =? WHERE ZONE_CODE =? AND ROWNO =?";
     PreparedStatement pStmt         =   null;
     String fromZipCode         =    null;
     String toZipCode           =    null;
     String zone                =    null;
     String estimationTime      =    null;
     String estimatedDistance   =    null;
     String alphaNumaric        =    null; 
     String zoneCode            =    null; 
     String rowNo               =    null;
     ZoneCodeChildDOB   childDOB  =  null;
     ArrayList          zoneCodeList = null;
     ArrayList         duplicateList = new ArrayList();
     CallableStatement  cstmt        = null;
     Integer            check        = null;
   
   try
   {     
     zoneCodeList             =   dob.getZoneCodeList();
     operationsImpl           =   new OperationsImpl();
     connection               =  operationsImpl.getConnection();
     //pStmt                    =  connection.prepareStatement(updateMasterQuery);
     //pStmt.setString(1,dob.getTerminalId());
     //pStmt.setString(2,dob.getPort());
     //pStmt.setString(2,zoneCode);
     //int k = pStmt.executeUpdate();
     
     if(pStmt!=null)
       pStmt.close();
     pStmt        =  connection.prepareStatement(updateDtlQuery); 
     //cstmt                    =   connection.prepareCall("{?= call qms_setups.zone_code_dtl_fun(?,?,?,?,?,?)}");
     int zoneCodeListSize	=	zoneCodeList.size();
     for(int i=0;i<zoneCodeListSize;i++)
     {
        childDOB     =   (ZoneCodeChildDOB)zoneCodeList.get(i);
        //cstmt.clearParameters();
        //cstmt.registerOutParameter(1,OracleTypes.INTEGER);
        //Logger.info(FILE_NAME,"childDOB.getFromZipCode():"+childDOB.getFromZipCode());
        //Logger.info(FILE_NAME,"childDOB.getToZipCode():"+childDOB.getToZipCode());
       // Logger.info(FILE_NAME,"childDOB.getZone()::"+childDOB.getZone());
       // Logger.info(FILE_NAME,"dob.getZipCode():"+dob.getZipCode());
        //Logger.info(FILE_NAME,"dob.getOriginLocation():"+dob.getOriginLocation());
        //cstmt.setString(2,childDOB.getFromZipCode());
        //cstmt.setString(3,childDOB.getToZipCode());
        //cstmt.setString(4,childDOB.getZone());        
        //if("numeric".equalsIgnoreCase(dob.getZipCode().toUpperCase()))
           //cstmt.setString(5,"");
        //else
            //cstmt.setString(5,childDOB.getAlphaNumaric().toUpperCase());
        //cstmt.setString(6,dob.getOriginLocation());
        //cstmt.setString(7,dob.getZipCode());
        //cstmt.execute();
        //check  =  (Integer)cstmt.getObject(1);
        //System.out.println("check "+check);
        //if(check.intValue()==1)
        //{
           pStmt.setString(1,childDOB.getFromZipCode());
           pStmt.setString(2,childDOB.getToZipCode());
           pStmt.setString(3,childDOB.getZone());
           pStmt.setString(4,childDOB.getEstimationTime());
           pStmt.setString(5,childDOB.getEstimatedDistance());
           pStmt.setString(6,childDOB.getAlphaNumaric());
           pStmt.setString(7,childDOB.getZoneCode());
           pStmt.setString(8,childDOB.getRowNo());
           pStmt.addBatch();
           //pStmt.executeUpdate();
        //}
        //else if(check.intValue()==0)
        //{
          //duplicateList.add(childDOB.getZone());
        //}
      
     }
     pStmt.executeBatch();
   }
   catch(SQLException e)
   {
     e.printStackTrace();   
     //Logger.error(FILE_NAME,"Exception in ZoneCode Master DAO"+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode Master DAO"+e.toString());
     throw new SQLException("Error while updating the details"+e.toString());
   }
   catch(Exception e)
   {
     e.printStackTrace();   
     //Logger.error(FILE_NAME,"Exception in ZoneCode Master DAO"+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode Master DAO"+e.toString());
     throw new EJBException("Error while updating the details"+e.toString());
   }
   finally
   {
     ConnectionUtil.closeConnection(connection,pStmt);
     childDOB   =  null;
     operationsImpl  =  null;
     zoneCodeList    =  null;
   }
   return duplicateList;
 }
//@@ COmmented & added by subrahmanyam for 216629 on 31-AUG-10 
 //private boolean deleteZoneCodeDetails(ZoneCodeMasterDOB dob)throws SQLException
 private String deleteZoneCodeDetails(ZoneCodeMasterDOB dob)throws SQLException
 {
     String            deleteQuery   =   "DELETE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE =? AND ROWNO =?";
     PreparedStatement pStmt         =   null;
     String fromZipCode         =    null;
     String toZipCode           =    null;
     String zone                =    null;
     String estimationTime      =    null;
     String estimatedDistance   =    null;
     String alphaNumaric        =    null; 
     String zoneCode            =    null; 
     String rowNo               =    null;
     ZoneCodeChildDOB   childDOB  =    new ZoneCodeChildDOB();
     ArrayList         zoneCodeList =   null;
//   @@ Added by subrahmanyam for 216629 on 31-AUG-10
     String 			consoleTypeVal	= "";
     if("1".equalsIgnoreCase(dob.getShipmentMode()))
  	   consoleTypeVal =	" CBM.CONSOLE_TYPE IS NULL ";
     else
  	   consoleTypeVal = " CBM.CONSOLE_TYPE='"+dob.getConsoleType()+"'";
      String deleteChkQry		=	" SELECT COUNT(*) QUOTE_COUNT  FROM QMS_QUOTE_RATES QR WHERE QR.SELL_BUY_FLAG IN ('BC', 'SC')"+
     								" AND QR.BUYRATE_ID IN(SELECT CBD.CARTAGE_ID FROM QMS_CARTAGE_BUYDTL CBD, "+
     								" QMS_CARTAGE_BUYSELLCHARGES CBM WHERE CBD.CARTAGE_ID = CBM.CARTAGE_ID "+
     								" AND CBM.LOCATION_ID = ? AND CBM.SHIPMENT_MODE = ? AND CBD.ZONE_CODE = ?"+
     								" AND "+consoleTypeVal+" AND CBD.LINE_NO = 0)";
     PreparedStatement pStmtDelChk         =   null;
     int			   count			   =   0;
     ResultSet			rsDelChk			= null;
     String remainingZone	=	"";
//   @@ Added by subrahmanyam for 216629 on 31-AUG-10     
   try
   {     
     zoneCodeList             =   dob.getZoneCodeList();
     operationsImpl           =    new OperationsImpl();
     connection               =   operationsImpl.getConnection();
     pStmt                    =   connection.prepareStatement(deleteQuery);
     pStmtDelChk                    =   connection.prepareStatement(deleteChkQry);
     int zoneCodeDtlSzie	=	zoneCodeList.size();
     Set zoneSet	= new HashSet();
     
     for(int i=0;i<zoneCodeDtlSzie;i++)
     {
//       @@ Commented & Added by subrahmanyam for 216629 on 31-AUG-10   
/*	       pStmt.setString(1,childDOB.getZoneCode());
	       pStmt.setString(2,childDOB.getRowNo());
	       pStmt.addBatch();
*/    	 count =0;
       childDOB    =  (ZoneCodeChildDOB)zoneCodeList.get(i);
       pStmtDelChk.setString(1, dob.getOriginLocation());
       pStmtDelChk.setString(2, dob.getShipmentMode());
       pStmtDelChk.setString(3, childDOB.getZone());
       rsDelChk	= pStmtDelChk.executeQuery();
       if(rsDelChk.next())
    	   count	= rsDelChk.getInt("QUOTE_COUNT");
       if (count ==0){
	       pStmt.setString(1,childDOB.getZoneCode());
	       pStmt.setString(2,childDOB.getRowNo());
	       pStmt.addBatch();
       }else{
    	   zoneSet.add(childDOB.getZone());
       }
//     @@ Endded by subrahmanyam for 216629 on 31-AUG-10       
       //pStmt.executeUpdate();
     }
     for(Object o: zoneSet)
    	 remainingZone =remainingZone+(String)o+"\n";
     
     pStmt.executeBatch();
   }
   catch(SQLException e)
   {
     e.printStackTrace();
     //Logger.error(FILE_NAMsE,"Exception in ZoneCode Master DAO"+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode Master DAO"+e.toString());
     throw new SQLException("Error while deleting the details"+e.toString());
   }
   catch(Exception e)
   {
     e.printStackTrace();
     //Logger.error(FILE_NAME,"Exception in ZoneCode Master DAO"+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode Master DAO"+e.toString());
     throw new EJBException("Error while deleting the details"+e.toString());
   }
   finally
   {
	 ConnectionUtil.closePreparedStatement(pStmtDelChk,rsDelChk ); // Added by Gowtham on 03Feb2011 ConnectionLeak checking
     ConnectionUtil.closeConnection(connection,pStmt);
     childDOB   =  null;
     operationsImpl  =  null;
     zoneCodeList    =  null;     
   }
  // return true;
   return remainingZone;
 }
 private ZoneCodeMasterDOB loadZoneCodeDetails(String location,String zoneCodeType,String shipmentMode,String consoleType,String operation)
 {
     String loadMasterQuery    = "SELECT ZONE_CODE,ORIGIN_LOCATION,TERMINALID,CITY ,STATE ,ZIPCODE_TYPE,INVALIDATE FROM QMS_ZONE_CODE_MASTER WHERE ORIGIN_LOCATION=? AND ZIPCODE_TYPE=? AND SHIPMENT_MODE=? AND NVL(CONSOLE_TYPE,'~')=?";
   String whereCondition="";
   if(!"Invalidate".equalsIgnoreCase(operation))
	    whereCondition=" AND INVALIDATE='F' ";
     //@@ modifed by subrahmanyam for 212863 on 26-Jul-10
         String loadDtlQuery       = "SELECT DISTINCT ZONE_CODE,FROM_ZIPCODE ,TO_ZIPCODE ,ZONE  ,ESTIMATED_TIME  ,ESTIMATED_DISTANCE ,ALPHANUMERIC,ROWNO,INVALIDATE FROM QMS_ZONE_CODE_DTL WHERE ZONE_CODE IN (SELECT ZONE_CODE FROM QMS_ZONE_CODE_MASTER WHERE ZONE_CODE=?)"+whereCondition+"  ORDER BY FROM_ZIPCODE";
    
         PreparedStatement  pStmtMaster  = null;
     PreparedStatement  pStmtDtl     = null;
     ResultSet          rsMaster     = null;
     ResultSet          rsDtl        = null;
     ZoneCodeMasterDOB  dob          = null;
     int                count        = 0;     
     ArrayList zoneCodeList          =  null;
     ZoneCodeChildDOB   childDOB     =  null;
     String             zoneCode     =  null;
     try
     {
       operationsImpl         =  new OperationsImpl();
       connection             =  operationsImpl.getConnection();
       pStmtMaster            =  connection.prepareStatement(loadMasterQuery);
       pStmtDtl               =  connection.prepareStatement(loadDtlQuery);
       pStmtMaster.setString(1,location);
       pStmtMaster.setString(2,zoneCodeType);
       pStmtMaster.setString(3,shipmentMode);
       
       if("1".equalsIgnoreCase(shipmentMode))
          pStmtMaster.setString(4,"~");//@@For NVL
       else
          pStmtMaster.setString(4,consoleType);
          
       rsMaster               =  pStmtMaster.executeQuery();
       dob                    =  new ZoneCodeMasterDOB();
       zoneCodeList           =  new ArrayList();
       while(rsMaster.next())
       {
         dob.setOriginLocation(rsMaster.getString(2)!=null?rsMaster.getString(2):"");
         dob.setTerminalId(rsMaster.getString(3)!=null?rsMaster.getString(3):"");
         dob.setCity(rsMaster.getString(4)!=null?rsMaster.getString(4):"");
         dob.setState(rsMaster.getString(5)!=null?rsMaster.getString(5):"");
         dob.setZipCode(rsMaster.getString(6)!=null?rsMaster.getString(6):"");
         zoneCode = rsMaster.getString(1);
         //dob.setPort(rsMaster.getString(7));           
       }               
         pStmtDtl.setString(1,zoneCode);
         rsDtl= pStmtDtl.executeQuery();
         while(rsDtl.next())
         {
             childDOB  =    new ZoneCodeChildDOB();
             childDOB.setZoneCode(rsDtl.getString(1));
             childDOB.setFromZipCode(rsDtl.getString(2)!=null?rsDtl.getString(2):"");
             childDOB.setToZipCode(rsDtl.getString(3)!=null?rsDtl.getString(3):"");
             childDOB.setZone(rsDtl.getString(4)!=null?rsDtl.getString(4):"");
             childDOB.setEstimationTime(rsDtl.getString(5)!=null?rsDtl.getString(5):"");
             childDOB.setEstimatedDistance(rsDtl.getString(6)!=null?rsDtl.getString(6):"");
             childDOB.setAlphaNumaric(rsDtl.getString(7)!=null?rsDtl.getString(7):"");
             childDOB.setRowNo(rsDtl.getString(8)!=null?rsDtl.getString(8):"");
             childDOB.setInvalidate(rsDtl.getString(9)!=null?rsDtl.getString(9):"");//@@Added by kiran.v
             zoneCodeList.add(childDOB);
             
         }
        dob.setZoneCodeList(zoneCodeList);
       
   }
   catch(Exception e)
   {
     //Logger.error(FILE_NAME,"Exception in ZoneCode Master DAO"+e.toString());
     logger.error(FILE_NAME+"Exception in ZoneCode Master DAO"+e.toString());
     e.printStackTrace();
     return null;
   }
   finally
   {
     ConnectionUtil.closeConnection(connection,pStmtMaster,rsMaster);
     ConnectionUtil.closePreparedStatement(pStmtDtl,rsDtl);
   }
   return dob;
 }
/*
 *   This method is for uploading the zonecode master details
 *   param0 ArrayList contains list of masterDOBs
 *   returns map which contains existing and nonexisting records
 */
 public HashMap uploadZoneCodeMasterDetails(ArrayList list)throws SQLException
 {
      HashMap  map  =  null;
      Connection connection = null;
      ZoneCodeMasterDOB zoneCodeMasterDOB = null;
      ZoneCodeChildDOB zoneCodeChildDOB = null;
      ArrayList        existingList  =  null;
      ArrayList        successList   =  new ArrayList();
      ArrayList        nonExistingList  =  null;
      int size  = 0;
      int count = 0;
     try
     {
        operationsImpl   =  new OperationsImpl();
        connection             =  operationsImpl.getConnection();
        map = validateMasterDetails(list,connection);
        existingList    =  (ArrayList)map.get("EXISTS");
        nonExistingList =  (ArrayList)map.get("NONEXISTS");
        count  = existingList.size();
        for(int i=0;i<count;i++)
        {
          zoneCodeMasterDOB = (ZoneCodeMasterDOB)existingList.get(i);
          zoneCodeMasterDOB =  insertZoneCodeDetails(zoneCodeMasterDOB);
          if(zoneCodeMasterDOB.getRemarks()!=null && zoneCodeMasterDOB.getRemarks().length()>0)
          {
            zoneCodeMasterDOB.setRemarks("");
            nonExistingList.add(zoneCodeMasterDOB);
          }
          else
          {
            successList.add(zoneCodeMasterDOB);
          }
        }
        map.clear();
        map.put("EXISTS",successList);//@@Success List
        map.put("NONEXISTS",nonExistingList);//@@Failed List
     }
     catch(SQLException e)
     {
        e.printStackTrace();
        throw new EJBException("Error while uploading the data");
     }
     catch(Exception e)
     {
        e.printStackTrace();
        throw new EJBException("Error while uploading the data");
     }
     finally
     {
       ConnectionUtil.closeConnection(connection,null,null);
       //ConnectionUtil.closeStatement(pStmtDtl,rsDtl);
     }
     return map;
 }
 
  /**
   * Used for Uploading Canada Zip Codes.
   * @throws java.sql.SQLException
   * @return HashMap
   * @param list
   */
  public HashMap uploadCanadaZoneCodes (ArrayList list) throws SQLException
  {
    HashMap  map  =  null;
    Connection connection = null;
    ZoneCodeMasterDOB zoneCodeMasterDOB = null;
    ZoneCodeChildDOB zoneCodeChildDOB = null;
    ArrayList        existingList  =  null;
    ArrayList        successList   =  new ArrayList();
    int size  = 0;
    int count = 0;
    
    try
    {
      connection      =   getConnection();
      map             =   validateMasterDetails(list,connection);
      existingList    =  (ArrayList)map.get("EXISTS");
      insertCanadaZoneCodes(existingList);
      //count  = existingList.size();
    }
    catch (SQLException sql)
    {
      sql.printStackTrace();
      logger.error("SQL Exception in uploadCanadaZoneCodes "+sql);
      throw new SQLException(sql.toString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      logger.error("Error while Uploading Canada Zip Codes in uploadCanadaZoneCodes:" + e);
      throw new EJBException(e);
    }
    //Added By RajKumari on 27-10-2008 for Connection Leakages.
    finally
    {
      ConnectionUtil.closeConnection(connection);
    }
    return map;
  }
 
  private HashMap validateMasterDetails(ArrayList list,Connection connection) throws SQLException
  {
      PreparedStatement pStmtMaster = null;
      //PreparedStatement pStmtDtl = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      ResultSet         rsMaster = null;
     // ResultSet         rsDtl    = null;  //Commented By RajKumari on 27-10-2008 for Connection Leakages.
      ArrayList         existingList = new ArrayList();
      ArrayList         nonExistingList = new ArrayList();
      String            sql      = "SELECT  LOCATIONID FROM  FS_FR_LOCATIONMASTER  WHERE  SHIPMENTMODE IN (?,?,?,?) AND LOCATIONID IN "+
                                   "(SELECT LOCATIONID FROM FS_FR_TERMINALLOCATION WHERE LOCATIONID= ? AND TERMINALID =?)";
      int               count    = list.size();
      ZoneCodeMasterDOB zoneCodeMasterDOB = null;
      //ZoneCodeMasterDOB tempMasterDOB     = null;
      ZoneCodeMasterDOB successMasterDOB  = null;
      ZoneCodeMasterDOB failureMasterDOB  = null;
      HashMap           map      =  new HashMap();
      CallableStatement cstmt    =  null;
      Integer           check    = null;
      ArrayList         childDOBList = null;
      ZoneCodeChildDOB  childDOB = null;
      ArrayList         tempExistingList = new ArrayList();
      ArrayList         tempNonExistingList = new ArrayList();
      String            shipmentModeStr = "";
      
   try
     {
        pStmtMaster  = connection.prepareStatement(sql);
       // cstmt        =   connection.prepareCall("{?= call qms_setups.zone_code_dtl_fun(?,?,?,?,?,?,?,?)}");
        for(int i=0;i<count;i++)
        {
          zoneCodeMasterDOB = (ZoneCodeMasterDOB)list.get(i);
          //tempMasterDOB     = (ZoneCodeMasterDOB)list.get(i);
          pStmtMaster.clearParameters();
          if("1".equalsIgnoreCase(zoneCodeMasterDOB.getShipmentMode()))
          {
            shipmentModeStr = "Air";
            //@@In (1,3,5,7)
            pStmtMaster.setInt(1,1);
            pStmtMaster.setInt(2,3);
            pStmtMaster.setInt(3,5);
            pStmtMaster.setInt(4,7);
          }
          else
          {
            shipmentModeStr = "Sea";
            //@@In (2,3,6,7)
            pStmtMaster.setInt(1,2);
            pStmtMaster.setInt(2,3);
            pStmtMaster.setInt(3,6);
            pStmtMaster.setInt(4,7);
          }
          pStmtMaster.setString(5,zoneCodeMasterDOB.getOriginLocation());
          pStmtMaster.setString(6,zoneCodeMasterDOB.getTerminalId());
          
          rsMaster = pStmtMaster.executeQuery();
          if(rsMaster.next())
          {
            existingList.add(zoneCodeMasterDOB);
            /*childDOBList  = zoneCodeMasterDOB.getZoneCodeList();
            tempExistingList = new ArrayList();
            tempNonExistingList = new ArrayList();
            for(int j=0;j<childDOBList.size();j++)
            {   
              childDOB     =  (ZoneCodeChildDOB)childDOBList.get(j);  
              cstmt.clearParameters();
              cstmt.registerOutParameter(1,OracleTypes.INTEGER);
              cstmt.setString(2,childDOB.getFromZipCode());
              cstmt.setString(3,childDOB.getToZipCode());
              cstmt.setString(4,childDOB.getZone());        
              if("numeric".equalsIgnoreCase(zoneCodeMasterDOB.getZipCode().toUpperCase()))
                 cstmt.setString(5,"");
              else
                  cstmt.setString(5,childDOB.getAlphaNumaric().toUpperCase());
              cstmt.setString(6,zoneCodeMasterDOB.getOriginLocation());
              cstmt.setString(7,zoneCodeMasterDOB.getZipCode());
              cstmt.setString(8,zoneCodeMasterDOB.getShipmentMode());        
              
              if("1".equalsIgnoreCase(zoneCodeMasterDOB.getShipmentMode()))
                  cstmt.setNull(9,Types.VARCHAR);
              else
                cstmt.setString(9,zoneCodeMasterDOB.getConsoleType());
              
              cstmt.execute();
              check  =  (Integer)cstmt.getObject(1);
             
              if(check.intValue()!=1)
              {
                childDOB.setRemarks("The Zip Codes already exist for this location "+zoneCodeMasterDOB.getOriginLocation()+" for Zone Code "+childDOB.getZone());
                tempExistingList.add(childDOB);///@@Failed List
                //for(int k=0;k<tempExistingList.size();k++)
                  //logger.info("tempExistingList(Failed List)::"+((ZoneCodeChildDOB)tempExistingList.get(k)).getFromZipCode());
              }
              else
              {
                tempNonExistingList.add(childDOB);//@@Success List
                //for(int k=0;k<tempNonExistingList.size();k++)
                 // logger.info("tempNonExistingList(Success List)::"+((ZoneCodeChildDOB)tempNonExistingList.get(k)).getFromZipCode());
              }
            }
            //zoneCodeMasterDOB.setZoneCodeList(new ArrayList());
            //tempMasterDOB.setZoneCodeList(new ArrayList());
            if(tempExistingList.size()>0)//@@Failed List
            {
               failureMasterDOB = new ZoneCodeMasterDOB();
               failureMasterDOB.setRowId(zoneCodeMasterDOB.getRowId());
               failureMasterDOB.setShipmentMode(zoneCodeMasterDOB.getShipmentMode());
               failureMasterDOB.setConsoleType(zoneCodeMasterDOB.getConsoleType());
               failureMasterDOB.setOriginLocation(zoneCodeMasterDOB.getOriginLocation());
               failureMasterDOB.setTerminalId(zoneCodeMasterDOB.getTerminalId());
               failureMasterDOB.setCity(zoneCodeMasterDOB.getCity());
               failureMasterDOB.setState(zoneCodeMasterDOB.getState());
               failureMasterDOB.setZipCode(zoneCodeMasterDOB.getZipCode());
               failureMasterDOB.setZoneCodeList(tempExistingList);
               //zoneCodeMasterDOB.setZoneCodeList(tempExistingList);
               //for (int k=0;k<zoneCodeMasterDOB.getZoneCodeList().size();k++)
                  //logger.info("zoneCodeMasterDOB.getZoneCodeList()(Failed List)::"+((ZoneCodeChildDOB)zoneCodeMasterDOB.getZoneCodeList().get(k)).getFromZipCode());
               nonExistingList.add(failureMasterDOB);//@@Failed List
            }
            if(tempNonExistingList.size()>0)//@@Success List
            { 
              //tempMasterDOB.setZoneCodeList(tempNonExistingList);
               successMasterDOB = new ZoneCodeMasterDOB();
               successMasterDOB.setRowId(zoneCodeMasterDOB.getRowId());
               successMasterDOB.setShipmentMode(zoneCodeMasterDOB.getShipmentMode());
               successMasterDOB.setConsoleType(zoneCodeMasterDOB.getConsoleType());
               successMasterDOB.setOriginLocation(zoneCodeMasterDOB.getOriginLocation());
               successMasterDOB.setTerminalId(zoneCodeMasterDOB.getTerminalId());
               successMasterDOB.setCity(zoneCodeMasterDOB.getCity());
               successMasterDOB.setState(zoneCodeMasterDOB.getState());
               successMasterDOB.setZipCode(zoneCodeMasterDOB.getZipCode());
               successMasterDOB.setZoneCodeList(tempNonExistingList);
              //for (int k=0;k<tempMasterDOB.getZoneCodeList().size();k++)
                 // logger.info("tempMasterDOB.setZoneCodeList()(Success List)::"+((ZoneCodeChildDOB)tempMasterDOB.getZoneCodeList().get(k)).getFromZipCode());
               existingList.add(successMasterDOB);//@@Success List
            }*/
          }
          else
          {
            
            zoneCodeMasterDOB.setRemarks("The Location Id "+zoneCodeMasterDOB.getOriginLocation()+" is either not defined for Shipment Mode "
                                          +shipmentModeStr+" or is not mapped to the Terminal Id "+zoneCodeMasterDOB.getTerminalId());
            nonExistingList.add(zoneCodeMasterDOB);//@@Failed List
          } 
          //System.out.println("nonExistingList.size()   "+nonExistingList.size());
            //System.out.println("existingList.size()   "+existingList.size());
        }
        logger.debug("NONEXISTS::"+nonExistingList);
        map.put("EXISTS",existingList);//@@Success List
        map.put("NONEXISTS",nonExistingList);//@@Failed List       
     }
     catch(SQLException e)
     {
        e.printStackTrace();
        throw new SQLException("Error while uploading the data");
     }
     catch(Exception e)
     {
        e.printStackTrace();
        throw new EJBException("Error while uploading the data");
     }
     finally
     {
       ConnectionUtil.closeConnection(connection,pStmtMaster,rsMaster);
       //ConnectionUtil.closeStatement(pStmtDtl,rsDtl);
     }
     return map;
 }
 
  /**
   * <p>To Insert or Update Canada Zone Codes
   * <ul>
   *    <li>Inserts When No Data is Found for the Unique Combination of Shipment Mode, Console Type & Location Id </li>
   *    <li>Updates (Delete & Insert) the record when found for the Unique Combination of Shipment Mode, Console Type & Location Id </li>
   * </ul>
   * </p>
   * @throws java.sql.SQLException
   * @return HashMap
   * @param list
   */
 private void insertCanadaZoneCodes(ArrayList list) throws SQLException
 {
   HashMap            hMap            =   new HashMap();
   Connection         connection      =   null;
   PreparedStatement  pStmt_seq       =   null;
   PreparedStatement  pStmt           =   null;
   PreparedStatement  pStmtDelete     =   null;
   PreparedStatement  pStmtInsert     =   null;
   PreparedStatement  pStmtInsNew     =   null;
   PreparedStatement  pStmtInsExisting=   null;
   ResultSet          rs              =   null;
   int                size            =   0;
   ZoneCodeMasterDOB  masterDOB       =   null;
   ArrayList          childDOBList    =   null;
   ZoneCodeChildDOB   childDOB        =   null;
   
   long               zoneCode        =   0;
   
   String             seq_qry              =   "SELECT ZONECODE_CA_SEQ.NEXTVAL FROM DUAL";   
   String             selectQuery          =   "SELECT ZONE_CODE FROM QMS_ZONE_CODE_MASTER_CA MAS WHERE LOCATION_ID = ? "+
                                               "AND SHIPMENT_MODE = ? AND NVL(CONSOLE_TYPE, '~') = ? ";
   String             insertMasterQry      =   "INSERT INTO QMS_ZONE_CODE_MASTER_CA VALUES(?,?,?,?,'F',?,?,?)";
   String             insertDtlQryNew      =   "INSERT INTO QMS_ZONE_CODE_DTL_CA VALUES(?,?,?,?,?,'F')";
   String             deleteQuery          =   "DELETE FROM QMS_ZONE_CODE_DTL_CA WHERE ZONE_CODE = ? ";
   String             insertDtlQryExisting =   "INSERT INTO QMS_ZONE_CODE_DTL_CA VALUES(?,?,?,?,?,'F')";
//@@ Added by subrahmanyam for the pbn id: 202447 on 12-Apr-10 
   String 			  chkZoneQry		   =   " SELECT COUNT(*) COUNT   FROM QMS_ZONE_CODE_MASTER_CA MAS, QMS_ZONE_CODE_DTL_CA CAD "+
   											   " WHERE MAS.ZONE_CODE = CAD.ZONE_CODE  AND MAS.LOCATION_ID = ? AND MAS.SHIPMENT_MODE = ? "+
   											   " AND NVL(MAS.CONSOLE_TYPE, '~') = ? AND CAD.FROM_ZIPCODE = ?  AND CAD.TO_ZIPCODE = ?"+
   											   " AND CAD.ZONE = ? AND MAS.INVALIDATE = 'F' ";
   PreparedStatement pStmtChkZone		  =   null;
   ResultSet          rsChkZone           =   null;
//@@ Ended by subrahmanyam for the pbn id: 202447 on 12-Apr-10   
   
   try
   {
     connection       =   getConnection();
     pStmt_seq        =   connection.prepareStatement(seq_qry);
     pStmt            =   connection.prepareStatement(selectQuery);
     pStmtDelete      =   connection.prepareStatement(deleteQuery);
     pStmtInsNew      =   connection.prepareStatement(insertDtlQryNew);
     pStmtInsExisting =   connection.prepareStatement(insertDtlQryExisting);
     pStmtInsert      =   connection.prepareStatement(insertMasterQry);
     pStmtChkZone	  =	  connection.prepareStatement(chkZoneQry);//@@ Added by subrahmanyam for the pbn id: 202447 on 12-Apr-10
     
     if(list!=null)
        size  = list.size();
        
     for(int i=0;i<size;i++)
     {
       masterDOB      = (ZoneCodeMasterDOB)list.get(i);
       childDOBList   = masterDOB.getZoneCodeList();
       pStmt.setString(1,masterDOB.getOriginLocation());
       pStmt.setString(2,masterDOB.getShipmentMode());
       
       if("1".equalsIgnoreCase(masterDOB.getShipmentMode()))
          pStmt.setString(3,"~");
       else
          pStmt.setString(3,masterDOB.getConsoleType());
       
       rs   =   pStmt.executeQuery();
       
       if(rs.next())
       {
         zoneCode = rs.getLong("ZONE_CODE");
         logger.info("zoneCode"+zoneCode);
  //@@ Commented by subrahmanyam for the pbn id: 202447 on 12-Apr-10         
        // pStmtDelete.setLong(1,zoneCode);
        // pStmtDelete.addBatch();
         int childDOBListSize	=	childDOBList.size();
         for(int j=0;j<childDOBListSize;j++)
         {
            childDOB = (ZoneCodeChildDOB)childDOBList.get(j);
            logger.info("childDOB.getFromZipCode()"+childDOB.getFromZipCode());
          //@@ Added by subrahmanyam for the pbn id: 202447 on 12-Apr-10            
            pStmtChkZone.setString(1, masterDOB.getOriginLocation());
            pStmtChkZone.setString(2, masterDOB.getShipmentMode());
            if("1".equalsIgnoreCase(masterDOB.getShipmentMode()))
            	pStmtChkZone.setString(3,"~");
             else
            	 pStmtChkZone.setString(3,masterDOB.getConsoleType());
            pStmtChkZone.setString(4, childDOB.getFromZipCode());
            pStmtChkZone.setString(5, childDOB.getToZipCode());
            pStmtChkZone.setString(6, childDOB.getZone());
            rsChkZone	=	pStmtChkZone.executeQuery();
            if(rsChkZone.next() && rsChkZone.getInt("COUNT")==0)
            {//@@ Ended by subrahmanyam for the pbn id: 202447 on 12-Apr-10
            pStmtInsExisting.setLong(1,zoneCode);
            pStmtInsExisting.setString(2,childDOB.getFromZipCode());
            pStmtInsExisting.setString(3,childDOB.getToZipCode());
            pStmtInsExisting.setString(4,childDOB.getZone());
            pStmtInsExisting.setString(5,""+j);
            pStmtInsExisting.addBatch();
            }
         }
       }
       else
       {
         if(rs!=null)
            rs.close();
        
         rs = pStmt_seq.executeQuery();
         if(rs.next())
            zoneCode = rs.getLong(1);
         
         pStmtInsert.setLong(1,zoneCode);
         pStmtInsert.setString(2,masterDOB.getOriginLocation());
         pStmtInsert.setString(3,masterDOB.getCity());
         pStmtInsert.setString(4,masterDOB.getState());
         pStmtInsert.setString(5,masterDOB.getTerminalId());
         pStmtInsert.setString(6,masterDOB.getShipmentMode());
         if("1".equalsIgnoreCase(masterDOB.getShipmentMode()))
            pStmtInsert.setNull(7,Types.VARCHAR);
         else
           pStmtInsert.setString(7,masterDOB.getConsoleType());
        
          pStmtInsert.addBatch();
          int chldDOBListSIze	=	childDOBList.size();
          for(int j=0;j<chldDOBListSIze;j++)
          {
            childDOB = (ZoneCodeChildDOB)childDOBList.get(j);
            pStmtInsNew.setLong(1,zoneCode);
            pStmtInsNew.setString(2,childDOB.getFromZipCode());
            pStmtInsNew.setString(3,childDOB.getToZipCode());
            pStmtInsNew.setString(4,childDOB.getZone());
            pStmtInsNew.setString(5,""+j);
            pStmtInsNew.addBatch();
          }         
       }
     }     
     //pStmtDelete.executeBatch();
     pStmtInsExisting.executeBatch();
     pStmtInsert.executeBatch();
     pStmtInsNew.executeBatch();     
   }
   catch(Exception e)
   {
     e.printStackTrace();
     logger.error("Error in insertCanadaZoneCodes :"+ e);
     throw new SQLException(e.toString());
   }
   finally
   {
     try
     {
       if(pStmt_seq!=null)
          pStmt_seq.close();
       if(pStmtDelete!=null)
          pStmtDelete.close();
       if(pStmtInsExisting!=null)
          pStmtInsExisting.close();
       if(pStmtInsert!=null)
          pStmtInsert.close();
       if(pStmtInsNew!=null)
          pStmtInsNew.close();
     //@@ Added by subrahmanyam for the pbn id: 202447 on 12-Apr-10       
       if(pStmtChkZone !=null)
    	   pStmtChkZone.close();
       if(rsChkZone !=null)
    	   rsChkZone.close();
     //@@ Ended by subrahmanyam for the pbn id: 202447 on 12-Apr-10       
       ConnectionUtil.closeConnection(connection,pStmt,rs);
     }
     catch(SQLException sql)
     {
       sql.printStackTrace();
       logger.error("Error while Closing resources :"+sql);
     }
     
   }
 }
  
}
/**
 * @ (#) IndustryRegDAO.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File : IndustryRegDAO.java
 * Sub-Module : CountryManager
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.dao;

import com.qms.setup.java.QMSAdvSearchLOVDOB;
import com.qms.setup.java.QMSAdvSearchColsDOB;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.CreateException;
import javax.sql.DataSource;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NameNotFoundException;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.sql.Types;

import com.qms.setup.ejb.bmp.MarginLimitMasterBeanPK;
import com.qms.setup.java.MarginLimitMasterDOB;
import oracle.jdbc.OracleTypes;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
public class MarginLimitMasterDAO 
{
    static final String FILE_NAME="MarginLimitMasterDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static Logger logger = null;
  /**
   * The DB field INVALIDATE takes (F or T)
   * vaid Ids   -F
   * Invalid Ids-T
   * BY default insert F as all the ids are valid.
   */
//@@Commented and added the insert query by subrahmnaym for the pbn id: 203354 on 22-APR-10    
    //public static final  String insertQry  = "INSERT INTO QMS_MARGIN_LIMIT_DTL (MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS,INVALIDATE,CHARGETYPE) VALUES (?,?,?,?,?,'F',?)";
    public static final  String insertQry  = "INSERT INTO QMS_MARGIN_LIMIT_DTL (MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS,INVALIDATE,CHARGETYPE,TERMINALID) VALUES (?,?,?,?,?,'F',?,?)";
    public static final  String selectQry  = "SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND SERVICE_LEVEL=? AND INVALIDATE='F'";
    String deletQry   = "DELETE FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND SERVICE_LEVEL=?";
    String updateQry  = "UPDATE QMS_MARGIN_LIMIT_DTL SET MAXDISCOUNT=?,MINMARGINS=? WHERE MARGIN_ID=?  AND LEVELNO=? AND SERVICE_LEVEL=? AND CHARGETYPE=?";
    String invalidate = "UPDATE QMS_MARGIN_LIMIT_DTL SET INVALIDATE=? WHERE MARGIN_ID=?  AND LEVELNO=? AND SERVICE_LEVEL=?";
  /**
   * 
   */
    public MarginLimitMasterDAO()
    {
        logger  = Logger.getLogger(MarginLimitMasterDAO.class);
        try
        {
          InitialContext ic =new InitialContext();
          dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
        }catch(NamingException nex)
        {
          throw new EJBException(nex.toString());
        }catch(Exception e)
        {
          throw new EJBException(e.toString());        
        }  
    }
    
  /**
   * 
   */
    protected void getConnection()
    {         
      try 
      {
           connection = dataSource.getConnection();
      } 
      catch (SQLException se) 
      {
        throw new EJBException(se.toString());
      }
      catch (Exception e) 
      {
              throw new EJBException(e.toString());
      }	   
  
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @param dataList
   */
    public void create(ArrayList  dataList)throws EJBException
    {
      PreparedStatement pstmt = null;
      MarginLimitMasterDOB  marginLimitMasterDOB  =null;
      int dataListSize	=	0;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQry);
        if(dataList!=null && dataList.size()>0)
        {
        	dataListSize	= dataList.size();
          for(int i=0;i<dataListSize;i++)
          {
            marginLimitMasterDOB  = (MarginLimitMasterDOB)dataList.get(i);
            if(marginLimitMasterDOB!=null)
            {
              pstmt.setString(1,marginLimitMasterDOB.getMarginId());
              pstmt.setString(2,marginLimitMasterDOB.getLevelId());
              pstmt.setString(3,marginLimitMasterDOB.getServiceLevel());
              pstmt.setDouble(4,(new Double(marginLimitMasterDOB.getMaxDiscount())).doubleValue());
              pstmt.setDouble(5,(new Double(marginLimitMasterDOB.getMinMargin())).doubleValue());
              pstmt.setString(6,marginLimitMasterDOB.getChargeType());
              pstmt.setString(7,marginLimitMasterDOB.getLoginTerminal());//by subrahmnaym for the pbn id: 203354 on 22-APR-10  
              pstmt.executeUpdate();
            }
            
          }
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();
      }finally
      {
        try
        {
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in IndustryRegDAO.create(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
    }
    
  /**
   * 
   * @throws javax.ejb.ObjectNotFoundException
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
    public MarginLimitMasterBeanPK findByPrimariKey(MarginLimitMasterBeanPK pkObj)throws EJBException,SQLException,ObjectNotFoundException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.marginId);
        pstmt.setString(2,pkObj.levelId);
        pstmt.setString(3,pkObj.sLevelId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
         throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
          throw new SQLException();          
        }
      }
      
      if(marginLimitRow)
      {
        return pkObj;
      }
      else
      {
        throw new ObjectNotFoundException();
      }
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
    public MarginLimitMasterDOB load(MarginLimitMasterBeanPK pkObj)throws EJBException
    {
      MarginLimitMasterDOB  marginLimitMasterDOB  = null;
      try
      {
        marginLimitMasterDOB  = loadMarginLimitDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return marginLimitMasterDOB;        
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param marginLimitMasterDOB
   * @param pkObj
   */
    public void store(MarginLimitMasterBeanPK pkObj,MarginLimitMasterDOB marginLimitMasterDOB)throws SQLException,EJBException
    {
      try
      {
        updateMarginLimitDetails(pkObj,marginLimitMasterDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(MarginLimitMasterDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(MarginLimitMasterDOB param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(MarginLimitMasterDOB param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(MarginLimitMasterDOB param0) method"+e.toString());
        throw new EJBException();        
      }      
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param pkObj
   */
    public void remove(MarginLimitMasterBeanPK pkObj)throws SQLException,EJBException
    {
      try
      {
        deleteMarginLimitDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param pkObj
   */
    public void deleteMarginLimitDetails(MarginLimitMasterBeanPK pkObj)throws SQLException,EJBException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deletQry);
        pstmt.setString(1,pkObj.marginId);
        pstmt.setString(2,pkObj.levelId);
        pstmt.setString(3,pkObj.sLevelId);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
            /*if(rs!=null)
              { rs.close();}*///Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in MarginLimitMasterBeanPK(IndustryRegEntityPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in MarginLimitMasterBeanPK(IndustryRegEntityPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param marginLimitMasterDOB
   * @param pkObj
   */
    public void updateMarginLimitDetails(MarginLimitMasterBeanPK pkObj,MarginLimitMasterDOB marginLimitMasterDOB)throws SQLException,EJBException
    {
      PreparedStatement  pstmt = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,marginLimitMasterDOB.getMaxDiscount());
        pstmt.setString(2,marginLimitMasterDOB.getMinMargin());
        pstmt.setString(3,pkObj.marginId);
        pstmt.setString(4,pkObj.levelId);
        pstmt.setString(5,pkObj.sLevelId);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
  
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @return 
   * @param pkObj
   */
    public MarginLimitMasterDOB loadMarginLimitDetails(MarginLimitMasterBeanPK pkObj)throws SQLException,EJBException
    {
      MarginLimitMasterDOB  marginLimitMasterDOB  = new MarginLimitMasterDOB();
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.marginId);
        pstmt.setString(2,pkObj.levelId);
        pstmt.setString(3,pkObj.sLevelId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitMasterDOB.setMarginId(rs.getString("MARGIN_ID"));
          marginLimitMasterDOB.setLevelId(rs.getString("LEVELNO"));
          marginLimitMasterDOB.setServiceLevel(rs.getString("SERVICE_LEVEL"));
          marginLimitMasterDOB.setMaxDiscount(rs.getString("MAXDISCOUNT"));
          marginLimitMasterDOB.setMinMargin(rs.getString("MINMARGINS"));
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
            if(rs!=null)
              { rs.close();}
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateMarginLimitDetails(MarginLimitMasterBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      } 
      return marginLimitMasterDOB;
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param loginbean
   * @param marginList
   */
    public void invalidateMarginDtl(ArrayList marginList,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)throws SQLException,EJBException
    {
      MarginLimitMasterDOB  marginLimitMasterDOB  = null;
      PreparedStatement pstmt = null;
//    String updateQry  = "UPDATE QMS_INDUSTRYREG SET INVALIDATE =? WHERE INDUSTRY_ID =?";
      int mListSize		=	0;
      try
      {
        getConnection();
       
        if(marginList!=null && marginList.size()>0)
        {
        	mListSize	= marginList.size();
          for(int i=0;i<mListSize;i++)
          {
            marginLimitMasterDOB  = (MarginLimitMasterDOB)marginList.get(i);
            if(marginLimitMasterDOB.getChargeType().equalsIgnoreCase("FREIGHT"))
            invalidate = "UPDATE QMS_MARGIN_LIMIT_DTL SET INVALIDATE=? WHERE MARGIN_ID=?  AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=? ";
            else
            invalidate = "UPDATE QMS_MARGIN_LIMIT_DTL SET INVALIDATE=? WHERE MARGIN_ID=?  AND LEVELNO=? AND CHARGETYPE=?";
            
            pstmt = connection.prepareStatement(invalidate);
            
            pstmt.setString(1,marginLimitMasterDOB.getInvalidate());
            pstmt.setString(2,marginLimitMasterDOB.getMarginId());
            pstmt.setString(3,marginLimitMasterDOB.getLevelId());
            pstmt.setString(4,marginLimitMasterDOB.getChargeType());
            if(marginLimitMasterDOB.getChargeType().equalsIgnoreCase("FREIGHT"))
            pstmt.setString(5,marginLimitMasterDOB.getServiceLevel());
            
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {
        //Logger.error(FILE_NAME,"SQLException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
        throw new SQLException();                  
       }catch(Exception e)
       {
        //Logger.error(FILE_NAME,"EJBException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
        throw new EJBException();            
       }finally
       {
        try{
           if(pstmt!=null)
            { pstmt.close();}
           if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
         //Logger.error(FILE_NAME,"SQLException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateMarginDtl(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }
    }    
  //@@ Added code on 26-10-05
  //@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010    	    
//public MarginLimitMasterDOB getMarginDetails(String shipmentType,String levelId,String sLevelId,String chargeType)throws EJBException,SQLException,ObjectNotFoundException  
   public MarginLimitMasterDOB getMarginDetails(String shipmentType,String levelId,String sLevelId,String chargeType,ESupplyGlobalParameters loginbean,String operation)throws EJBException,SQLException,ObjectNotFoundException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      MarginLimitMasterDOB marginLimitMasterDOB=new MarginLimitMasterDOB();
      String selectQry     = "";
   //@@ Commented & Added by subrahmanyam for the pbn id: 203354 on 22-APR-010    	
     /* if(chargeType.equalsIgnoreCase("FREIGHT"))
      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=?  AND INVALIDATE='F'";
      else
      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND  INVALIDATE='F'";
      */
      if("view".equalsIgnoreCase(operation) || "viewAll".equalsIgnoreCase(operation) || "HO_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
    	  if(chargeType.equalsIgnoreCase("FREIGHT"))
    	      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=?  AND INVALIDATE='F'";
   	      else
    	      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND  INVALIDATE='F'";

      }
      else
      {
      if("ADMN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
    	 if(chargeType.equalsIgnoreCase("FREIGHT"))
    	    selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? "+
    	    			" AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=?  AND INVALIDATE='F' AND TERMINALID IN(SELECT CHILD_TERMINAL_ID "+
    	    			" FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"'"+
    	    			" UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')";
    	 
    	  else
    	      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND  INVALIDATE='F'"+
    	      			" AND TERMINALID IN(SELECT CHILD_TERMINAL_ID "+
    	    			" FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID='"+loginbean.getTerminalId()+"'"+
    	    			" UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')";
    	 
    	 
      }
      else if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
    	   	 if(chargeType.equalsIgnoreCase("FREIGHT"))
    	    	    selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? "+
    	    	    			" AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=?  AND INVALIDATE='F' AND TERMINALID IN("+
    	    	    			" UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')";
    	    	 
    	   	  else
    	  	      selectQry="SELECT MARGIN_ID,LEVELNO,SERVICE_LEVEL,MAXDISCOUNT,MINMARGINS FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND  INVALIDATE='F'"+
    	    	      			" AND TERMINALID IN( UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+loginbean.getTerminalId()+"')";
    	   	 
    }
   }
    //@@ Ended by subrahmanyam for the pbn id: 203354 on 22-APR-010    	      
      //Logger.info("","In DAO  "+selectQry);
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,shipmentType);
        pstmt.setString(2,levelId);
        pstmt.setString(3,chargeType);
        if(chargeType.equalsIgnoreCase("FREIGHT"))
        pstmt.setString(4,sLevelId);
        
        rs    = pstmt.executeQuery();
        
         if(rs.next())
        {
          marginLimitMasterDOB.setMarginId(rs.getString("MARGIN_ID"));
          marginLimitMasterDOB.setLevelId(rs.getString("LEVELNO"));
          marginLimitMasterDOB.setServiceLevel(rs.getString("SERVICE_LEVEL"));
          marginLimitMasterDOB.setMaxDiscount(rs.getString("MAXDISCOUNT"));
          marginLimitMasterDOB.setMinMargin(rs.getString("MINMARGINS"));
        }
       
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
         throw new EJBException();         
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(IndustryRegEntityPK param0) method"+e.toString());
          throw new SQLException();          
        }
      }
      
      
        return marginLimitMasterDOB;
      
    }
    //@@ Added code on 26-10-05
    /////////////////////////////////////////
     public void updateMarginLimitDetail(MarginLimitMasterDOB marginLimitMasterDOB)throws SQLException,EJBException
    {
      PreparedStatement  pstmt = null;
      if("Freight".equalsIgnoreCase(marginLimitMasterDOB.getChargeType()))
      updateQry  = "UPDATE QMS_MARGIN_LIMIT_DTL SET MAXDISCOUNT=?,MINMARGINS=? WHERE MARGIN_ID=?  AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=? ";
      else
      updateQry  = "UPDATE QMS_MARGIN_LIMIT_DTL SET MAXDISCOUNT=?,MINMARGINS=? WHERE MARGIN_ID=?  AND LEVELNO=? AND CHARGETYPE=? ";
      
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,marginLimitMasterDOB.getMaxDiscount());
        pstmt.setString(2,marginLimitMasterDOB.getMinMargin());
        pstmt.setString(3,marginLimitMasterDOB.getMarginId());
        pstmt.setString(4,marginLimitMasterDOB.getLevelId());
        pstmt.setString(5,marginLimitMasterDOB.getChargeType());
        
        if("Freight".equalsIgnoreCase(marginLimitMasterDOB.getChargeType()))
        pstmt.setString(6,marginLimitMasterDOB.getServiceLevel());
        
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
    
    public void deleteMarginLimitDetail(MarginLimitMasterDOB marginLimitMasterDOB)throws SQLException,EJBException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
  	//@@Commented & Added by subrahmanyam for the wpbn id:198576 on 25/Feb/10      
      //deletQry   = "DELETE FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=?";
      deletQry   = "DELETE FROM QMS_MARGIN_LIMIT_DTL WHERE MARGIN_ID=? AND LEVELNO=? AND CHARGETYPE=? AND SERVICE_LEVEL=?";
      
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deletQry);
        pstmt.setString(1,marginLimitMasterDOB.getMarginId());
        pstmt.setString(2,marginLimitMasterDOB.getLevelId());
        pstmt.setString(3,marginLimitMasterDOB.getChargeType());
        pstmt.setString(4, marginLimitMasterDOB.getServiceLevel());//@@Added by subrahmanyam for the wpbn id:198576 on 25/Feb/10
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
            /*if(rs!=null)
              { rs.close();}*///Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteMarginLimitDetail(marginLimitMasterDOB) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
//@@ Added by subrahmanyam for the WPBN ISSUE:146436 on 15/12/2008
  public int getNoOfRecForChargeBasis()
  {
    String sql= "SELECT COUNT(*)  FROM V_CHARGEBASIS_MASTER";
    ResultSet rs=null;
    int no_of_rec_page=0;
   // PreparedStatement pstmt = null;
  Statement stmt = null;
    try
    {
      getConnection();
      //In getNoOfRecForChargeBasis() method, use Statement instead of PreparedStatement.
     // pstmt = connection.prepareStatement(sql);
      //rs=pstmt.executeQuery();
     stmt = connection.createStatement();
     rs=stmt.executeQuery(sql);
      while(rs.next())
       no_of_rec_page=rs.getInt(1);
    }
    catch(SQLException e)
    {
      logger.error(FILE_NAME+"SQLException in getNoOfRecForChargeBasis() method :"+e.toString());
      
    }
    finally
    {
      try
      {
        if(stmt!=null)
          stmt.close();
        if(connection!=null)
          connection.close();
        if(rs!=null)
          rs.close();
      }catch(SQLException e)
      {
        logger.error(FILE_NAME+"SQLException in getNoOfRecForChargeBasis() method :"+e.toString());
      }
    }
  
    return no_of_rec_page;
  }
//@@ Ended by subrahmanyam for the WPBN ISSUE:146436 on 15/12/2008
    public ArrayList getAdvSerchLov(QMSAdvSearchLOVDOB advSearchLovDOB) throws SQLException
  {
    CallableStatement cs     = null;
    Statement         smt    = null;
		ResultSet resultset      = null;
		ResultSet resultsetCols  = null;
		ResultSetMetaData rsmd   = null; 
    String  orgID            = "";
    String  languageID       = "English";
    String  rootPath         = "";

		//int variables
    int       recCount       = 0; // total record count 
		int       colCount       = 0; // column count for the data to be displayed
		int       totColCount    = 0; //column count for the total number of records
		int       i              = 0; // index variable
		int       no_rec_per_page= 0; // number of records per page
		int       count          = 1; // index variable
		int       winWidth       = 0; //window width variable
		int       winHeight      = 0; //window height variable
		
    //String variables
		//StringBuffer        sb                  = new StringBuffer(); // content stringbuffer variable which has to be displayed
		//StringBuffer        sbSearchCols        = new StringBuffer();//string buffer seperately for the columns
    QMSAdvSearchLOVDOB  advSerchLov         = null;
    QMSAdvSearchColsDOB advSerchColLov      = null;
		String    title           = null; //title of the page
		String selectCols[][]     = null; // String array containing the column names and the width that have to be displayed in LOV
		String pagination         = null; //Pagination variable if 'Y' then pagination will be displayed.If 'N' then pagination is not displayed.
		//Error msg variables
		String msg                = null; // These variables are used to display the message 'No Records Found'.
		String msgNo              = null; // Message number variable
		String msgDesc            = null; // Message description that has to be displayed.
    String colId              = null;
		String colDesc            = null;
		String colWidth           = null;
    String sotrtOfString      = null;
		//Error msg variables
        
		//boolean variables
		boolean recFound=false;
    
    ArrayList finalList       = null;
    ArrayList colsNamesList   = null;
    ArrayList list1           = null;
    ArrayList list2           = null;
    ArrayList dataList        = null;
    //Added by RajKumari on 27-11-2008 for 146459 starts
    String    tempQuoteId     = null;
    int       temp          = 0; 
    String    tempConsoleType   = "";
    int      totalCount    = 0;
    //Added by RajKumari on 27-11-2008 for 146459 ends
		try
		{
			//getting database connection
          getConnection();
          long start=System.currentTimeMillis();	
          advSerchLov   = new QMSAdvSearchLOVDOB();
          finalList       = new ArrayList();
          colsNamesList   = new ArrayList();
          dataList        = new ArrayList();
          smt                = connection.createStatement();         
          cs=connection.prepareCall("{ call QMS_ADVLOV_PKG.get_lov(?,?,?,?,?,?,?,?,?,?,?,?,?)}");
         //registering the out parameters
          cs.registerOutParameter(5,OracleTypes.VARCHAR);
          cs.registerOutParameter(6,OracleTypes.VARCHAR);
          cs.registerOutParameter(7,OracleTypes.NUMBER);
          cs.registerOutParameter(8,OracleTypes.NUMBER);
          cs.registerOutParameter(9,OracleTypes.NUMBER);
          cs.registerOutParameter(10,OracleTypes.NUMBER);
          cs.registerOutParameter(11,OracleTypes.NUMBER);
          cs.registerOutParameter(12,OracleTypes.CURSOR);
          cs.registerOutParameter(13,OracleTypes.CURSOR);
          cs.setNull(1,Types.VARCHAR);
          cs.setString(2,advSearchLovDOB.getLovId());
          cs.setString(3,advSearchLovDOB.getWhereCondition());
          /* if(whereClause!=null)
          {
          cs.setString(4,null);
          }
          else
          {
          cs.setString(4,whereClause);
          }*/
          //checking if the current page is null
          cs.setInt(4,Integer.parseInt(advSearchLovDOB.getPageNo()));
          cs.execute();
          //getting the results after executing the procedure
          title = cs.getString(5);
          pagination = cs.getString(6);
//@@ Added by subrahmanyam for the WPBN ISSUE:146436 on 15/12/2008         
          if("CHARGEBASIS_MASTER".equalsIgnoreCase(advSearchLovDOB.getLovId()))
              no_rec_per_page = getNoOfRecForChargeBasis();
            else
//@@Ended by subrahmnayam for the WPBN ISSUE: 146436 on 15/12/2008
                no_rec_per_page = cs.getInt(7);
         
            
          colCount  = cs.getInt(8);
          recCount  = cs.getInt(9);
         
          winWidth  = cs.getInt(10);
          winHeight = cs.getInt(11);
          resultsetCols=(ResultSet)cs.getObject(12);
          resultset=(ResultSet)cs.getObject(13);
       
          long end=System.currentTimeMillis();
				 //Checking if the column count is o.If zero returning null.
          advSerchLov.setTitle(title);
          advSerchLov.setPagination(pagination);
          advSerchLov.setNoOfRecPerPage(no_rec_per_page);
          advSerchLov.setColCount(colCount);
          advSerchLov.setRecCount(recCount);
          advSerchLov.setWinWidth(winWidth);
          advSerchLov.setWinHeight(winHeight);
          
          if(colCount==0)
          {
            return null;
          }
          else
          {
            selectCols = new String[colCount][colCount];
          }
          //System.out.println("4444444444444444444444444444444444444"+advSearchLovDOB.getPageNo());
				  if(resultsetCols.next()) 
				  {
            //System.out.println("55555555555555555555555555");
					 do
           {
						 advSerchColLov   = new QMSAdvSearchColsDOB();
             colId    = resultsetCols.getString("column_id");
						 colDesc  = resultsetCols.getString("description");
						 colWidth = resultsetCols.getString("width");
             advSerchColLov.setColId(colId);
             advSerchColLov.setColDesc(colDesc);
             advSerchColLov.setColWidth(colWidth);
						// System.out.println("Details :::::: :"+colId+"LLLLLLLLL::"+colDesc+" hhhhhhh ::"+colWidth);
             if(colWidth!=null)
						 {
						   colWidth = "10";
						 }
						 if(colId!=null && colDesc!=null)
						 {
							 //System.out.println("555555 :::::: :"+colId+"99999999::"+colDesc+" 0000000000 ::"+colWidth);
                try
                {
                    selectCols[i][0] = new String (colId);
                    
                  //Added By Kishore For StatusReason LOV Array Index Out Of Bounds Exception
                    if(!"STATUS_REASON_LOV".equals(advSearchLovDOB.getLovId())) 
                    selectCols[i][1] = new String (colWidth);
                }
                catch(Exception e)
                { 
                e.printStackTrace();
                }             
                if((advSearchLovDOB.getSortType()!=null && advSearchLovDOB.getSortType().length()>0) && (advSearchLovDOB.getSortFeild()!=null&& advSearchLovDOB.getSortFeild().length()>0))
                {
                    //System.out.println("colIdcolIdcolId : in sls :"+colId);
                   // System.out.println("advSearchLovDOB.getSortFeild()advSearchLovDOB.getSortFeild() :"+advSearchLovDOB.getSortFeild());
                    if(advSearchLovDOB.getSortFeild().equals(colId))
                    {
                        if(advSearchLovDOB.getSortType().equals("ASC"))
                        {
                          sotrtOfString = "DESC";
                        }
                        else if(advSearchLovDOB.getSortType().equals("DESC"))
                        {
                          sotrtOfString = "ASC";
                        }
                    }
                    else
                    {
                      sotrtOfString = null;
                    }
							  }
							  else
								{
                  sotrtOfString = null;
                }
							 //System.out.println("sotrtOfStringsotrtOfStringsotrtOfString : in sls :"+sotrtOfString);  
               advSerchColLov.setSortType(sotrtOfString); 
              }
						  else
						  {
							  return null;
						  }
						  i=i+1;
              colsNamesList.add(advSerchColLov);
					   }
             while(resultsetCols.next());//end of while resultsetCols.next()
					 
					  //System.out.println("4444444444444444::::"+sbSearchCols.toString());
            //sbSearchCols =  null;
				   }//end of  if(resultsetCols.next());
				   else
				   {
					    return null;
				   }// end of else
				   colId   = null;
				   colDesc = null;          
				   rsmd         = resultset.getMetaData();
				   totColCount  = rsmd.getColumnCount();
				  while(resultset.next())
          {
             list1        = new ArrayList();
             for(int j=1;j<=totColCount;j++)
					   {
                colDesc = rsmd.getColumnName(j);
                for(int k=0;k<colCount;k++)
                {
                    if(colDesc.equals(selectCols[k][0]))
                    {
                        colId = resultset.getString(colDesc);
                        if(!recFound)
                        {
                            // System.out.println("formArrayformArrayformArrayformArrayformArray :: "+advSearchLovDOB.getFormArray());
                            //System.out.println("colIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolId IN DAO IF :: "+colId);
                            list1.add(colId);
                            recFound = true;
                            //Added by RajKumari on 27-11-2008 for 146459 starts
                             if("QUOTE_ID".equalsIgnoreCase(colDesc))
                             {
                               if(tempQuoteId!=null&&colId!=null&&tempQuoteId.equalsIgnoreCase(colId))
                               {
                                  temp++;
                               }
                                 tempQuoteId  = colId;
                              }
                              //Added by RajKumari on 27-11-2008 for 146459 ends
                        }//end of if (k==0)
                        else
                        {
                        //Added by RajKumari on 27-11-2008 for 146459 starts
                         /* if(temp>0&&"CONSOLETYPE".equalsIgnoreCase(colDesc))
                            {
                                colId =tempConsoleType+","+colId;
                            }*/ // Commented by Gowtham to show only one time console type in LOV
                            if("CONSOLETYPE".equalsIgnoreCase(colDesc))
                            {
                               tempConsoleType  = colId;
                            }
                           //Added by RajKumari on 27-11-2008 for 146459 ends
                          if("SHIPMENTMODE".equalsIgnoreCase(colDesc))
                          {
                            if("1".equalsIgnoreCase(colId))
                            {
                              colId = "Air";
                            }
                            else if("2".equalsIgnoreCase(colId))
                            {
                              colId = "Sea";
                            }
                            else if("3".equalsIgnoreCase(colId))
                            {
                              colId = "Air,Sea";
                            }
                            else if("4".equalsIgnoreCase(colId))
                            {
                              colId = "Truck";
                            }
                            else if("5".equalsIgnoreCase(colId))
                            {
                              colId = "Air,Truck";
                            }
                            else if("6".equalsIgnoreCase(colId))
                            {
                              colId = "Sea,Truck";
                            }
                            else if("7".equalsIgnoreCase(colId))
                            {
                              colId = "Air,Sea,Truck";
                            }
                          }
                         //System.out.println("colIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolIdcolId IN DAO ELSE :: "+colId);
                          list1.add(colId);
                        }//end of else if(k==0)								
                        totalCount++;//Added by RajKumari on 27-11-2008 for 146459
                    }
							   
                }//end of for(int k=0;k<colCount;k++)
						 }//end of for(int j=1;j<=totColCount;j++)
					   recFound = false;
					   count = count + 1;
             //Added by RajKumari on 27-11-2008 for 146459 starts
             if(temp>0)
             {
                   dataList.remove(dataList.size()-1);
             }
             //Added by RajKumari on 27-11-2008 for 146459 ends
             dataList.add(list1);
             temp = 0;//Added by RajKumari on 27-11-2008 for 146459
				   } //end of while
				   colDesc  = null;
				   colId    = null;
				   //checking if pagination is set to Yes or No and if any records exists to be displayed.If Yes calling the function to get the pagination content
        finalList.add(advSerchLov);
        finalList.add(colsNamesList);
        finalList.add(dataList);
		}
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      //Logger.error(FILE_NAME, "getAdvSerchLovgetAdvSerchLov::"+sqle);
      logger.error(FILE_NAME+ "getAdvSerchLovgetAdvSerchLov::"+sqle);
      throw new EJBException(sqle.toString());
    }
		catch(Exception mainEx)
    {
      mainEx.printStackTrace();
      //Logger.error(FILE_NAME, "getAdvSerchLovgetAdvSerchLov::"+mainEx);
      logger.error(FILE_NAME+ "getAdvSerchLovgetAdvSerchLov::"+mainEx);
      throw new EJBException(mainEx.toString());
    }
    finally
    {					
      try{
        
        title= null;
        selectCols= null;
        pagination= null;
        msgNo=null;
        msgDesc=null;
        if(cs!=null)
        {
          cs.close();
          cs=null;
        }
        if(smt!=null)
        {
          smt.close();	
          smt=null;
        }
        if(rsmd!=null)
        {
          rsmd=null;
        }
        if(resultset!=null)
        {
          resultset.close();	
          resultset=null;
        }
        if(resultsetCols!=null)
        {
          resultsetCols.close();	
          resultsetCols=null;
        }
        if(connection!=null)
        {
          connection.close();
          connection=null;
        }	
      }catch(Exception el){el.printStackTrace();}
    }//finally end here
			
			
			//return value
      if(finalList!=null)
      {
        return finalList;
      }
      else
      {
        return null;
      }	
	}//getSearchData ends here
  

}

     

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
package com.qms.setup.chargemaster.dao;


import java.sql.PreparedStatement;
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
import java.util.ArrayList;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.qms.setup.chargemaster.ejb.bmp.ChargesMasterEntityBeanPK;
import com.qms.setup.java.ChargesMasterDOB;

public class ChargeMasterDAO 
{
    static final String FILE_NAME="ChargeMasterDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static final String insertQry = "INSERT INTO QMS_CHARGESMASTER (CHARGE_ID,CHARGE_DESCRIPTION,SHIPMENT_MODE,COST_INCURREDAT,INVALIDATE) VALUES (?,?,?,?,'F')";
    private static final String selectQry = "SELECT CHARGE_ID,CHARGE_DESCRIPTION,SHIPMENT_MODE,COST_INCURREDAT FROM QMS_CHARGESMASTER WHERE CHARGE_ID =?";
    private static final String updateQry = "UPDATE QMS_CHARGESMASTER SET CHARGE_DESCRIPTION=?,SHIPMENT_MODE=?,COST_INCURREDAT=? WHERE CHARGE_ID =?"; 
    private static final String deleteQry = "DELETE FROM QMS_CHARGESMASTER WHERE CHARGE_ID =?";
    private static Logger logger = null;
        
  /**
   * 
   */
    public ChargeMasterDAO()
    {
          logger  = Logger.getLogger(ChargeMasterDAO.class);
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
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param pkObj
   */
    public ChargesMasterEntityBeanPK findByPrimariKey(ChargesMasterEntityBeanPK pkObj)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.chargeId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(ChargesMasterEntityBeanPK param0) method"+e.toString());
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
   * @param dataList
   */
    public void create(ArrayList dataList)throws EJBException
    {
      try
      {
        insertChargeDetails(dataList);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in create(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in create(ArrayList param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in create(ArrayList param0) method"+e.toString());
        throw new EJBException();         
      }
    }
    
  /**
   * 
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
    public ChargesMasterDOB load(ChargesMasterEntityBeanPK pkObj)throws EJBException
    {
      ChargesMasterDOB  chargesMasterDOB  = null;
      try
      {
        chargesMasterDOB  = loadChargesMasterDOB(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return chargesMasterDOB;        
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param chargesMasterDOB
   * @param pkObj
   */
    public void store(ChargesMasterEntityBeanPK pkObj,ChargesMasterDOB chargesMasterDOB)throws SQLException,EJBException
    {
      try
      {
        updateChargesMasterDetails(pkObj,chargesMasterDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(ChargesMasterDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(ChargesMasterDOB param0) method"+e.toString());
        throw new EJBException();         
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in store(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in store(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(ChargesMasterDOB param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(ChargesMasterDOB param0) method"+e.toString());
        throw new EJBException();        
      }      
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param pkObj
   */
    public void remove(ChargesMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      try
      {
        deleteChargeMasterDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }
    }
    
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param dataList
   */
    public void insertChargeDetails(ArrayList dataList)throws EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ChargesMasterDOB  chargesMasterDOB  =null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQry);
        if(dataList!=null && dataList.size()>0)
        {
        	int dListSize	=	dataList.size();
          for(int i=0;i<dListSize;i++)
          {
            chargesMasterDOB  = (ChargesMasterDOB)dataList.get(i);
            if(chargesMasterDOB!=null)
            {
              pstmt.setString(1,chargesMasterDOB.getChargeId());
              pstmt.setString(2,chargesMasterDOB.getChargeDesc());
              pstmt.setInt(3,chargesMasterDOB.getShipmentMode());
              pstmt.setString(4,chargesMasterDOB.getCostIncurr());
              pstmt.executeUpdate();
            }
            
          }
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in insertChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in insertChargeDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in insertChargeDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in insertChargeDetails(ArrayList param0) method"+e.toString());
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
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @return 
   * @param pkObj
   */
    public ChargesMasterDOB loadChargesMasterDOB(ChargesMasterEntityBeanPK pkObj)throws EJBException,SQLException
    {
      ChargesMasterDOB  chargesMasterDOB  = new ChargesMasterDOB();
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.chargeId);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          chargesMasterDOB.setChargeId(rs.getString("CHARGE_ID"));
          chargesMasterDOB.setChargeDesc(rs.getString("CHARGE_DESCRIPTION"));
          chargesMasterDOB.setShipmentMode(rs.getInt("SHIPMENT_MODE"));
          chargesMasterDOB.setCostIncurr(rs.getString("COST_INCURREDAT"));
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      } 
      return chargesMasterDOB;      
    }
  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param chargesMasterDOB
   * @param pkObj
   */
   public void updateChargesMasterDetails(ChargesMasterEntityBeanPK pkObj,ChargesMasterDOB chargesMasterDOB)throws SQLException,EJBException
    {
      PreparedStatement  pstmt = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,chargesMasterDOB.getChargeDesc());
        pstmt.setInt(2,chargesMasterDOB.getShipmentMode());
        pstmt.setString(3,chargesMasterDOB.getCostIncurr());
        pstmt.setString(4,pkObj.chargeId);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateChargesMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }      
    }
  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param pkObj
   */
    public void deleteChargeMasterDetails(ChargesMasterEntityBeanPK pkObj)throws EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,pkObj.chargeId);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }finally{
          try
          {
           /* if(rs!=null)
              { rs.close();}*///Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteChargeMasterDetails(ChargesMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }      
    }
}
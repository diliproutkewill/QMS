/**
 * @ (#) ChargeBasisMasterDAO.java
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
 * File : ChargeBasisMasterDAO.java
 * Sub-Module : ChargeBasisMaster
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.chargebasis.dao;


import java.sql.PreparedStatement;
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
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;

import com.qms.setup.chargebasis.ejb.bmp.ChargeBasisMasterEntityBeanPK;
import com.qms.setup.java.ChargeBasisMasterDOB;

import java.util.ArrayList;

public class ChargeBasisMasterDAO 
{
    static final String FILE_NAME="ChargeBasisMasterDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static Logger logger = null;
    
    //private static final String insertQry = "INSERT INTO QMS_CHARGE_BASISMASTER (CHARGEBASIS,BASIS_DESCRIPTION,BLOCK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS,CALCULATION,INVALIDATE) VALUES (?,?,?,?,?,?,?,'F')";
    private static final String insertQry = "INSERT INTO QMS_CHARGE_BASISMASTER (CHARGEBASIS,BASIS_DESCRIPTION,BLOCK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS,INVALIDATE) VALUES (?,?,?,?,?,?,'F')";
    //private static final String selectQry = "SELECT CHARGEBASIS,BASIS_DESCRIPTION,BLOCK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS,CALCULATION FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS =? AND INVALIDATE='F'";
    private static final String selectQry = "SELECT CHARGEBASIS,BASIS_DESCRIPTION,BLOCK,PRIMARY_BASIS,SECONDARY_BASIS,TERTIARY_BASIS FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS =? AND INVALIDATE='F'";
    //private static final String updateQry = "UPDATE QMS_CHARGE_BASISMASTER SET BASIS_DESCRIPTION=?,BLOCK=?,PRIMARY_BASIS=?,SECONDARY_BASIS=?,TERTIARY_BASIS=?,CALCULATION=? WHERE CHARGEBASIS =?";
    private static final String updateQry = "UPDATE QMS_CHARGE_BASISMASTER SET BASIS_DESCRIPTION=?,BLOCK=?,PRIMARY_BASIS=?,SECONDARY_BASIS=?,TERTIARY_BASIS=? WHERE CHARGEBASIS =?";
    private static final String deleteQry = "DELETE FROM QMS_CHARGE_BASISMASTER WHERE CHARGEBASIS =?";
  /**
   * 
   */
    public ChargeBasisMasterDAO()
    {
        logger  = Logger.getLogger(ChargeBasisMasterDAO.class);
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
    
    public ChargeBasisMasterEntityBeanPK findByPrimariKey(ChargeBasisMasterEntityBeanPK pkObj)throws EJBException,SQLException,ObjectNotFoundException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.chargeBasis);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
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
    public void create(ArrayList dataList)throws EJBException
    {
      try
      {
        insertChargeBasisDetails(dataList);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
    }
    
    public ChargeBasisMasterDOB load(ChargeBasisMasterEntityBeanPK pkObj)throws EJBException
    {
      ChargeBasisMasterDOB  chargeBasisMasterDOB  = null;
      try
      {
        chargeBasisMasterDOB  = loadChargeBasisMasterDOB(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return chargeBasisMasterDOB;        
    }
    public void store(ChargeBasisMasterEntityBeanPK pkObj,ChargeBasisMasterDOB chargeBasisMasterDOB)throws SQLException,EJBException
    {
      try
      {
        updateChargeBasisMasterDetails(pkObj,chargeBasisMasterDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }      
    }
    public void remove(ChargeBasisMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      try
      {
        deleteChargeBasisMasterDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }
    }
    public void insertChargeBasisDetails(ArrayList dataList)throws EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ChargeBasisMasterDOB  chargeBasisMasterDOB  =null;
      int dataListSize	=	0;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQry);
        if(dataList!=null && dataList.size()>0)
        {
        	dataListSize= dataList.size();
          for(int i=0;i<dataListSize;i++)
          {
            chargeBasisMasterDOB  = (ChargeBasisMasterDOB)dataList.get(i);
            if(chargeBasisMasterDOB!=null)
            {
              pstmt.setString(1,chargeBasisMasterDOB.getChargeBasis());
              pstmt.setString(2,chargeBasisMasterDOB.getChargeDesc());
              pstmt.setString(3,(chargeBasisMasterDOB.getBlock()!=null)?chargeBasisMasterDOB.getBlock():"");
              pstmt.setString(4,chargeBasisMasterDOB.getPrimaryBasis());
              pstmt.setString(5,chargeBasisMasterDOB.getSecondaryBasis());
              pstmt.setString(6,chargeBasisMasterDOB.getTertiaryBasis());             
              //pstmt.setString(7,chargeBasisMasterDOB.getCalculation());             
              pstmt.executeUpdate();
            }
            
          }
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in insertChargeBasisDetails(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }  
    }
    public ChargeBasisMasterDOB loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK pkObj)throws EJBException,SQLException
    {
      ChargeBasisMasterDOB  chargeBasisMasterDOB  = new ChargeBasisMasterDOB();
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.chargeBasis);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          chargeBasisMasterDOB.setChargeBasis(rs.getString("CHARGEBASIS"));
          chargeBasisMasterDOB.setChargeDesc(rs.getString("BASIS_DESCRIPTION"));
          chargeBasisMasterDOB.setBlock(rs.getString("BLOCK"));
          chargeBasisMasterDOB.setPrimaryBasis(rs.getString("PRIMARY_BASIS"));
          chargeBasisMasterDOB.setSecondaryBasis(rs.getString("SECONDARY_BASIS"));
          chargeBasisMasterDOB.setTertiaryBasis(rs.getString("TERTIARY_BASIS"));
          //chargeBasisMasterDOB.setCalculation(rs.getString("CALCULATION"));
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in loadChargeBasisMasterDOB(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      } 
      return chargeBasisMasterDOB;          
    }
    public void updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK pkObj,ChargeBasisMasterDOB chargeBasisMasterDOB)throws SQLException,EJBException
    {
      PreparedStatement  pstmt = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,chargeBasisMasterDOB.getChargeDesc());
        pstmt.setString(2,(chargeBasisMasterDOB.getBlock()!=null)?chargeBasisMasterDOB.getBlock():"");
        pstmt.setString(3,chargeBasisMasterDOB.getPrimaryBasis());
        pstmt.setString(4,chargeBasisMasterDOB.getSecondaryBasis());
        pstmt.setString(5,chargeBasisMasterDOB.getTertiaryBasis());
        //pstmt.setString(6,chargeBasisMasterDOB.getCalculation());
        pstmt.setString(6,pkObj.chargeBasis);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }        
    }
    public void deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,pkObj.chargeBasis);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteChargeBasisMasterDetails(ChargeBasisMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }      
    }      
}

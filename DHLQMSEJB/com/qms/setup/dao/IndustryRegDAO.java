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

import com.qms.setup.ejb.bmp.IndustryRegEntityPK;
import com.qms.setup.java.IndustryRegDOB;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.CreateException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Types;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import java.util.ArrayList;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NameNotFoundException;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;



public class IndustryRegDAO 
{
    static final String FILE_NAME="IndustryRegDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static Logger logger = null;
    public IndustryRegDAO()
    {
      logger  = Logger.getLogger(IndustryRegDAO.class);
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
    
    public IndustryRegEntityPK findByPrimariKey(IndustryRegEntityPK pkObj)throws SQLException,EJBException,ObjectNotFoundException
    {
      PreparedStatement   pstmt      = null;
      ResultSet   rs        = null;
      boolean  industryRows = false;
      String selectQry  = "SELECT INDUSTRY_ID FROM QMS_INDUSTRY_REG WHERE INDUSTRY_ID=?";
      try
      {
          getConnection();
          pstmt  = connection.prepareStatement(selectQry);
          pstmt.setString(1,pkObj.industry);
          rs  = pstmt.executeQuery();
          if(rs.next())
          {
              industryRows = true;
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
      if(industryRows)
      {
        return pkObj;
      }
      else
      {
        throw new ObjectNotFoundException("Could not find bean	with Id "+pkObj.industry);
      }
    
    }
    
    public void create(IndustryRegDOB industryDOB)throws EJBException,CreateException
    {
      String            insertQuery ="INSERT INTO QMS_INDUSTRY_REG (INDUSTRY_ID,DESCRIPTION,INVALIDATE,TERMINALID) VALUES (?,?,?,?)";
      String            industry    =null;
      String            description =null;
      String            terminalId  =null;
      PreparedStatement pstmt       =null;
      boolean           insertStatus=false;
      int               result      =0;
      
      
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQuery);
        
        if(industryDOB!=null)
          {
               industry      = industryDOB.getIndustry();
               description   = industryDOB.getDescription();
               terminalId    = industryDOB.getTerminalId();
               pstmt.setString(1,industry);
               pstmt.setString(2,description);
               pstmt.setString(3,"F");
               pstmt.setString(4,terminalId);
               result=pstmt.executeUpdate();
          }
      }
      catch(SQLException sqex)
      {
        //Logger.error(FILE_NAME,"SQLException in IndustryRegDAO.create(ArrayList param0) method"+sqex.toString());
        logger.error(FILE_NAME+"SQLException in IndustryRegDAO.create(ArrayList param0) method"+sqex.toString());
        throw new EJBException();
      }
      catch(Exception ex)
      {
        //Logger.error(FILE_NAME,"Exception in IndustryRegDAO.create(ArrayList param0) method"+ex.toString());
        logger.error(FILE_NAME+"Exception in IndustryRegDAO.create(ArrayList param0) method"+ex.toString());
        throw new EJBException();
      }
      finally
      {
        ConnectionUtil.closeConnection(connection,pstmt);
      }
    }
    
    public IndustryRegDOB load(IndustryRegEntityPK industrPKObj)
    {
      IndustryRegDOB  industryRegDOB  = null;
      String      industryId  =  industrPKObj.industry;
      try
      {
        industryRegDOB  = loadIndustryDetails(industryId);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in IndustryRegDAO.load(IndustryRegEntityPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in IndustryRegDAO.load(IndustryRegEntityPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in IndustryRegDAO.load(IndustryRegEntityPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in IndustryRegDAO.load(IndustryRegEntityPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return industryRegDOB;
    }
    public void store(IndustryRegDOB industryRegDOB)
    {
      try
      {
        updateIndustryDetails(industryRegDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(IndustryRegDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(IndustryRegDOB param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(IndustryRegDOB param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(IndustryRegDOB param0) method"+e.toString());
        throw new EJBException();        
      }
    }
    public void remove(IndustryRegEntityPK industrPKObj)
    {
      try
      {
        deleteIndustryDetails(industrPKObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(IndustryRegDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(IndustryRegDOB param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(IndustryRegDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(IndustryRegDOB param0) method"+e.toString());
        throw new EJBException();            
      }
    }
    private void deleteIndustryDetails(IndustryRegEntityPK industrPKObj)throws SQLException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      
      String deleteQry  = "DELETE FROM QMS_INDUSTRY_REG WHERE INDUSTRY_ID=?";
      
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,industrPKObj.industry);
        pstmt.executeUpdate();
      }
      catch(SQLException e)
      {
//<<<<<<< IndustryRegDAO.java
        //Logger.error(FILE_NAME,"SQLException in deleteIndustryDetails(IndustryRegDOB param0) method"+e.toString());
        //logger.error(FILE_NAME+"SQLException in deleteIndustryDetails(IndustryRegDOB param0) method"+e.toString());
//=======
//>>>>>>> 1.2.4.1.4.1
        throw new SQLException();        
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
          //Logger.error(FILE_NAME,"SQLException in closing connection deleteIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in closing connection deleteIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          throw new SQLException();           
        }
      }
    }
    
    public void updateIndustryDetails(IndustryRegDOB industryRegDOB)throws SQLException
    {
      PreparedStatement pstmt = null;
      
      String  updateQry = "UPDATE QMS_INDUSTRY_REG SET INDUSTRY_ID =?,DESCRIPTION =? WHERE INDUSTRY_ID =?";
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,industryRegDOB.getIndustry());
        pstmt.setString(2,industryRegDOB.getDescription());
        pstmt.setString(3,industryRegDOB.getIndustry());
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
          //Logger.error(FILE_NAME,"SQLException in updateIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in updateIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          throw new SQLException();         
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
          //Logger.error(FILE_NAME,"SQLException in closing connection updateIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in closing connection updateIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          throw new SQLException();            
        }
      }
      
    }
    
    private IndustryRegDOB loadIndustryDetails(String industryId)throws SQLException
    {
        Statement stmt        = null;
        ResultSet rs          = null;
        
        IndustryRegDOB  industryRegDOB  = null;
        String  selectQry   = "SELECT INDUSTRY_ID,DESCRIPTION FROM QMS_INDUSTRY_REG WHERE INDUSTRY_ID='"+industryId+"' AND INVALIDATE='F'";
                
        try
        {
          getConnection();
          stmt  = connection.createStatement();
          rs    = stmt.executeQuery(selectQry);
          if(rs.next())
          {
            industryRegDOB  = new IndustryRegDOB();
            industryRegDOB.setIndustry(rs.getString("INDUSTRY_ID"));
            industryRegDOB.setDescription(rs.getString("DESCRIPTION"));
          }
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in loadIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in loadIndustryDetails(IndustryRegDOB param0) method"+e.toString());
          throw new SQLException();           
        }finally
        {
          try
          {
            if(rs!=null)
              { rs.close();}
            if(stmt!=null)
              { stmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
             //Logger.error(FILE_NAME,"SQLException in closing connection loadIndustryDetails(IndustryRegDOB param0) method"+e.toString());
             logger.error(FILE_NAME+"SQLException in closing connection loadIndustryDetails(IndustryRegDOB param0) method"+e.toString());
             throw new SQLException();             
          }
        }
        
        return industryRegDOB;
    }
    
    public void invalidateIndustryId(ArrayList industryList,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)throws SQLException,EJBException
    {
      IndustryRegDOB  industryRegDOB  = null;
      PreparedStatement pstmt = null;
      String updateQry  = "UPDATE QMS_INDUSTRY_REG SET INVALIDATE =? WHERE INDUSTRY_ID =?";
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        if(industryList!=null && industryList.size()>0)
        {
        	int industryListSize	= industryList.size();
          for(int i=0;i<industryListSize;i++)
          {
            industryRegDOB  = (IndustryRegDOB)industryList.get(i);
            pstmt.setString(1,industryRegDOB.getInvalidate());
            pstmt.setString(2,industryRegDOB.getIndustry());
            pstmt.executeUpdate();
          }
         }
          
       }catch(SQLException e)
       {
        //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        throw new SQLException();                  
       }catch(Exception e)
       {
        //Logger.error(FILE_NAME,"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in invalidateIndustryId(ArrayList param0) method"+e.toString());
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
         //Logger.error(FILE_NAME,"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in invalidateIndustryId(ArrayList param0) method"+e.toString());
         throw new SQLException();            
        }
       }
    }
    

}

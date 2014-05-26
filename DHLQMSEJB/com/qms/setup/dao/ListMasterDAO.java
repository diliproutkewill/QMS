package com.qms.setup.dao;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.setup.ejb.bmp.ListMasterEntityBeanPK;
import com.qms.setup.java.ListMasterDOB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.ejb.EJBException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ListMasterDAO 
{
static final String FILE_NAME="ListMasterDAO.java";
DataSource dataSource=null;
Connection connection=null;
String insertQry="INSERT INTO QMS_LISTMASTER (SHIPMENT_MODE,UOV,UOM,LIST_TYPE,LIST_DESCRIPTION,VOLUME,PIOVT_UNLADEN_WEIGHT,OVER_PIVOT_TARE_WEIGHT,INVALIDATE) VALUES (?,?,?,?,?,?,?,?,?)";
String selectQry="SELECT SHIPMENT_MODE,LIST_TYPE,UOV,UOM,LIST_DESCRIPTION,VOLUME,PIOVT_UNLADEN_WEIGHT,OVER_PIVOT_TARE_WEIGHT FROM QMS_LISTMASTER WHERE SHIPMENT_MODE=? AND INVALIDATE<>'T' AND LIST_TYPE=?";
 String updateQry="UPDATE QMS_LISTMASTER SET UOV=?,UOM=?,LIST_DESCRIPTION=?,VOLUME=?,PIOVT_UNLADEN_WEIGHT=?,OVER_PIVOT_TARE_WEIGHT=? WHERE SHIPMENT_MODE=? AND LIST_TYPE=?";
 String deleteQry="DELETE FROM QMS_LISTMASTER WHERE SHIPMENT_MODE=? AND LIST_TYPE=?";
 String invalidate="UPDATE QMS_LISTMASTER SET INVALIDATE=? WHERE SHIPMENT_MODE=? AND LIST_TYPE=?";
 private static Logger logger = null;
  public ListMasterDAO()
  {
    logger  = Logger.getLogger(ListMasterDAO.class);
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
    public void create(ArrayList  dataList)throws EJBException
    {
      PreparedStatement pstmt = null;
      ListMasterDOB listmasterDOB=null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQry);
        if(dataList!=null && dataList.size()>0)
        {
        	int dataListSize	=	 dataList.size();
          for(int i=0;i<dataListSize;i++)
          {
            listmasterDOB  = (ListMasterDOB)dataList.get(i);
            if(listmasterDOB!=null)
            {
              pstmt.setString(1,listmasterDOB.getShipmentMode());
              pstmt.setString(2,listmasterDOB.getUov());
              pstmt.setString(3,listmasterDOB.getUom());
              pstmt.setString(4,listmasterDOB.getUldType());
              pstmt.setString(5,listmasterDOB.getDescription());
              pstmt.setDouble(6,Double.parseDouble(listmasterDOB.getVolume()));
              pstmt.setDouble(7,(new Double(listmasterDOB.getPivoteUladenWeight()).doubleValue()));
              pstmt.setDouble(8,new Double(listmasterDOB.getOverPivoteTareWeight()).doubleValue());
              pstmt.setString(9,listmasterDOB.getInvalidate());
              pstmt.executeUpdate();
            }
            
          }
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in ListMasterDAO.create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in ListMasterDAO.create(ArrayList param0) method"+e.toString());
        e.printStackTrace();
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in ListMasteDAO.create(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in ListMasteDAO.create(ArrayList param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in ListMasterDAO.create(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in ListMasterDAO.create(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
    }
     public ListMasterEntityBeanPK findByPrimariKey(ListMasterEntityBeanPK pkObj)throws EJBException,SQLException,ObjectNotFoundException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.shipmentMode);
        pstmt.setString(2,pkObj.listType);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitRow  = true;
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new SQLException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"Exception in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"Exception in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
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
          //Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in findByPrimariKey(ListMasterEntityBeanPK param0) method"+e.toString());
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
    public ListMasterDOB load(ListMasterEntityBeanPK pkObj)throws EJBException
    {
      ListMasterDOB listmasterDOB=null;
      try
      {
        listmasterDOB  = loadListMasterDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();        
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();         
      }
      return listmasterDOB;        
    }
     public void store(ListMasterEntityBeanPK pkObj,ListMasterDOB listmasterDOB)throws SQLException,EJBException
    {
      try
      {
        updateListMasterDeatils(pkObj,listmasterDOB);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in store(ListMasterDOB param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in store(ListMasterDOB param0) method"+e.toString());
        throw new EJBException();         
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(ListMasterDOB param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(ListMasterDOB param0) method"+e.toString());
        throw new EJBException();        
      }      
    }
    public void remove(ListMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      try
      {
        deleteListMasterDetails(pkObj);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();            
      }
    }
     public void deleteListMasterDetails(ListMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      PreparedStatement pstmt = null;
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,pkObj.shipmentMode);
        pstmt.setString(2,pkObj.listType);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
    public boolean invalidateListMaster(ArrayList dobList)
  {
    Statement stmt=null;
    String invalidate=null;
    String shipmentMode=null;
    String listType=null;
    String invalidateQuery=null;
    int dobListSize		=0;
    try
    {
      getConnection();
      stmt=connection.createStatement();
      dobListSize	=	dobList.size();
      for(int i=0;i<dobListSize;i++)
      {
        ListMasterDOB listDOB=(ListMasterDOB)dobList.get(i);
        shipmentMode=listDOB.getShipmentMode();
        listType=listDOB.getUldType();
        invalidate=listDOB.getInvalidate();
        invalidateQuery="UPDATE  QMS_LISTMASTER  SET INVALIDATE='"+invalidate+"' WHERE SHIPMENT_MODE='"+shipmentMode+"' AND LIST_TYPE='"+listType+"'";
        stmt.executeUpdate(invalidateQuery);
      }
      return true;
      
    }catch(SQLException exct)
    {
          System.out.println("exception at  invalidateListMaster(String serviceLevelId,String invalidate):"+exct);
      return false;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt);
    }
  }
    public void updateListMasterDeatils(ListMasterEntityBeanPK pkObj,ListMasterDOB listmasterDOB)throws SQLException,EJBException
    {
      PreparedStatement  pstmt = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(updateQry);
        pstmt.setString(1,listmasterDOB.getUov());
        pstmt.setString(2,listmasterDOB.getUom());
        pstmt.setString(3,listmasterDOB.getDescription());
        pstmt.setString(4,listmasterDOB.getVolume());
        pstmt.setString(5,listmasterDOB.getPivoteUladenWeight());
        pstmt.setString(6,listmasterDOB.getOverPivoteTareWeight());
        pstmt.setString(7,pkObj.shipmentMode);
        pstmt.setString(8,pkObj.listType);
        pstmt.executeUpdate();
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateListMasterDeatils(ListMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      }
    }
     public ListMasterDOB loadListMasterDetails(ListMasterEntityBeanPK pkObj)throws SQLException,EJBException
    {
      ListMasterDOB  listmasterDOB  = new ListMasterDOB();
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.shipmentMode);
        pstmt.setString(2,pkObj.listType);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          listmasterDOB.setUov(rs.getString("UOV"));
          listmasterDOB.setUom(rs.getString("UOM"));
          listmasterDOB.setDescription(rs.getString("LIST_DESCRIPTION"));
          listmasterDOB.setVolume(rs.getString("VOLUME"));
          listmasterDOB.setPivoteUladenWeight(rs.getString("PIOVT_UNLADEN_WEIGHT"));
          listmasterDOB.setOverPivoteTareWeight(rs.getString("OVER_PIVOT_TARE_WEIGHT"));
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();           
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
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
            //Logger.error(FILE_NAME,"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in loadListMasterDetails(ListMasterEntityBeanPK param0) method"+e.toString());
            throw new SQLException();          
          }
      } 
      return listmasterDOB;
    }
    public void invalidateMarginDtl(ArrayList listmaster,com.foursoft.esupply.common.bean.ESupplyGlobalParameters loginbean)throws SQLException,EJBException
    {
      ListMasterDOB  listmasterDOB  = null;
      PreparedStatement pstmt = null;
      int lmSize		=	0;
       try
      {
        getConnection();
        pstmt = connection.prepareStatement(invalidate);
        if(listmaster!=null && listmaster.size()>0)
        {
        	lmSize	=	listmaster.size();
          for(int i=0;i<lmSize;i++)
          {
            listmasterDOB  = (ListMasterDOB)listmaster.get(i);
            pstmt.setString(1,listmasterDOB.getInvalidate());
            pstmt.setString(2,listmasterDOB.getShipmentMode());
            pstmt.setString(3,listmasterDOB.getUldType());
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
}
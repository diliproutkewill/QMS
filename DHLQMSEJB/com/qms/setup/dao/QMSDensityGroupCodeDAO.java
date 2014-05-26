package com.qms.setup.dao;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.setup.java.DensityGroupCodeDOB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;

public class QMSDensityGroupCodeDAO 
{
 
  public static final String insertQry = "INSERT INTO QMS_DENSITY_GROUP_CODE (DGCCODE ,KG_PER_M3 ,LB_PER_F3,INVALIDATE) VALUES(?,?,?,'F')";
  public static final String selectQry = "SELECT LB_PER_F3 FROM QMS_DENSITY_GROUP_CODE WHERE DGCCODE = ? AND KG_PER_M3 = ? AND INVALIDATE='F' ";//AND TERMINALID =?
  public static final String updateQry = "UPDATE QMS_DENSITY_GROUP_CODE  SET LB_PER_F3 = ? WHERE DGCCODE=? AND KG_PER_M3 = ? ";
  public static final String deleteQry = "DELETE QMS_DENSITY_GROUP_CODE WHERE DGCCODE=? AND KG_PER_M3 = ? ";
  public static final String FILE_NAME = "QMSDensityGroupCodeDAO.java";
  public static final String selectAllQry = "SELECT LB_PER_F3,DGCCODE,INVALIDATE,KG_PER_M3 FROM QMS_DENSITY_GROUP_CODE  ";//AND TERMINALID =?
  public static final String InvalidateQry = "UPDATE QMS_DENSITY_GROUP_CODE  SET INVALIDATE = ? WHERE DGCCODE=? AND KG_PER_M3 = ?  ";
  Connection connection = null;
  public QMSDensityGroupCodeDAO()
  {
  }
  
  public void getConnection()
  {
    OperationsImpl operationsImpl  = new OperationsImpl();
    try
    {
      this.connection = operationsImpl.getConnection();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      Logger.error(FILE_NAME,"Error whle getting the connection"+e.toString());
    }
  }
  
  public boolean insertDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB)throws SQLException
  {
    PreparedStatement pStmt = null; 
    try
    {
        this.getConnection();
        pStmt     = connection.prepareStatement(insertQry);  
        System.out.println("dgcDOB.getDGCCode() "+dgcDOB.getDGCCode());
        System.out.println("dgcDOB.getPerKG() "+dgcDOB.getPerKG());
        System.out.println("dgcDOB.getPerLB() "+dgcDOB.getPerLB());
        pStmt.setInt(1,dgcDOB.getDGCCode());
        pStmt.setDouble(2,dgcDOB.getPerKG());
        pStmt.setDouble(3,dgcDOB.getPerLB());    
        pStmt.executeUpdate();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while inserting details");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while inserting details");
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }return true;
  }
  
  public boolean updateDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB)throws SQLException
  {
    PreparedStatement pStmt  = null;
    try
    {
      this.getConnection();
      pStmt   = connection.prepareStatement(updateQry);
      pStmt.setDouble(1,dgcDOB.getPerLB());
      pStmt.setInt(2,dgcDOB.getDGCCode());
      pStmt.setDouble(3,dgcDOB.getPerKG());
      pStmt.executeUpdate();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while updating details");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while updating details");
    }finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }return true;
  }
  
  public double selectDensityGroupCodeDetails(int dgcCode,double kgPerm3)throws SQLException
  {
    PreparedStatement pStmt  = null;
    ResultSet         rs     = null;    
    double           perLB   = 0.0;
    try
    {
      this.getConnection();
      pStmt   = connection.prepareStatement(selectQry);
      pStmt.setInt(1,dgcCode);
      pStmt.setDouble(2,kgPerm3);
      rs      = pStmt.executeQuery();      
      if(rs.next())
      {
        perLB = rs.getDouble(1);
      }
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while getting details");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while getting details");
    }finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }return perLB;
  }
  
  public boolean deleteDensityGroupCodeDetails(int dgcCode,double kgPerm3)throws SQLException
  {
    PreparedStatement pStmt  = null;
    DensityGroupCodeDOB dob  = null;
    try
    {
      this.getConnection();
      pStmt   = connection.prepareStatement(deleteQry);
      pStmt.setInt(1,dgcCode);
      pStmt.setDouble(2,kgPerm3);
      pStmt.executeUpdate();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while deleting details");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while deleting details");
    }finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }
    return true;
  }
  
  public boolean invalidateDensityGroupCodeDetails(ArrayList list)throws SQLException
  {
    PreparedStatement pStmt  = null;
    DensityGroupCodeDOB dgcDOB = null;
    int size    = 0;
    try
    {
      this.getConnection();
      pStmt   = connection.prepareStatement(InvalidateQry);
      size    = list.size();
      for(int i=0;i<size;i++)
      {
        dgcDOB = (DensityGroupCodeDOB)list.get(i);
        pStmt.setString(1,dgcDOB.getInvalidate());
        pStmt.setInt(2,dgcDOB.getDGCCode());
        pStmt.setDouble(3,dgcDOB.getPerKG());
        pStmt.addBatch();
      }
      pStmt.executeBatch();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
      throw new SQLException("Error while inserting details");
    }
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException("Error while inserting details");
    }finally
    {
      ConnectionUtil.closeConnection(connection,pStmt);
    }return true;
  }
  
  
}
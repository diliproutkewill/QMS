package com.qms.setup.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.foursoft.esupply.common.util.ConnectionUtil;
import com.qms.setup.java.SurchargeDOB;

public class SurChargesDAO 
{
static final String FILE_NAME="SurChargesDAO.java";
DataSource dataSource=null;
Connection connection=null;
 private static Logger logger = null;
 private static final String insertSurChargeQry	= 	"INSERT INTO QMS_SURCHARGE_MASTER" +
													"  (SURCHARGE_ID," + 
													"   SURCHARGE_DESC," + 
													"   SHIPMENT_MODE," + 
													"   RATE_BREAK," + 
													"   RATE_TYPE," + 
													"   WEIGHT_BREAKS," +
													"	ID)" + 
													"   VALUES (?,?,?,?,?,?,SEQ_SURCHARGE.NEXTVAL)";
 private static final String chkSurChargeQry	=	" SELECT COUNT(*) FROM QMS_SURCHARGE_MASTER WHERE SURCHARGE_ID=? AND SHIPMENT_MODE=?";
 
 private static final String selectSurchargeQry	=	" SELECT SM.SURCHARGE_ID   SURCHARGE_ID," +
 													"       SM.SURCHARGE_DESC SURCHARGE_DESC," + 
 													"       SM.SHIPMENT_MODE  SHIPMENT_MODE," + 
 													"       SM.RATE_BREAK     RATE_BREAK," + 
 													"       SM.RATE_TYPE      RATE_TYPE" + 
 													"  FROM QMS_SURCHARGE_MASTER SM" + 
 													" WHERE SM.SURCHARGE_ID = ?" + 
 													"   AND SM.SHIPMENT_MODE = ?";
 // Added by Silpa For SurCharge View All on 22-Apr-11
 String selectSurchargeQry1 =	"SELECT SURCHARGE_ID, substr(SURCHARGE_DESC,0,length(SURCHARGE_DESC)-3) SURCHARGE_DESC, SHIPMENT_MODE, RATE_BREAK, RATE_TYPE  FROM QMS_SURCHARGE_MASTER ORDER BY SURCHARGE_DESC,SHIPMENT_MODE ASC";
 
 private static final String modifySurchargeQry	=	" UPDATE QMS_SURCHARGE_MASTER SM" +
	 												"   SET SM.SURCHARGE_DESC = ?" + 
	 												" WHERE SM.SHIPMENT_MODE = ?" + 
	 												"   AND SM.SURCHARGE_ID = ?";
 private static final String delSurchargeQrChkAir	=	 " SELECT COUNT(1)" +
													 "  FROM QMS_BUYRATES_DTL BD" + 
													 " WHERE BD.SURCHARGE_ID =? " + 
													 "   AND BD.ACTIVEINACTIVE IS NULL "+
													 "  AND LENGTH(BD.ORIGIN)=3";
 private static final String delSurchargeChkQrySea	=	 " SELECT COUNT(1)" +
													 "  FROM QMS_BUYRATES_DTL BD" + 
													 " WHERE BD.SURCHARGE_ID =? " + 
													 "   AND BD.ACTIVEINACTIVE IS NULL "+
													 "  AND LENGTH(BD.ORIGIN)=5"; 
													 
private static final String delSurChargeQry			= 	" DELETE FROM QMS_SURCHARGE_MASTER SM" +
														" WHERE SM.SURCHARGE_ID = ?" + 
														"   AND SM.SHIPMENT_MODE = ?";


 	
 PreparedStatement	pStatement	=	null;
 ResultSet			resultSet	=	null;
 PreparedStatement	pStmt1	=	null;
 ResultSet			rs1	=	null;

  public SurChargesDAO()
  {
    logger  = Logger.getLogger(SurChargesDAO.class);
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
  public int insertSurCharges(SurchargeDOB surchargeDOB) throws Exception{
	  
	  int surChargeCount	=	0;
	  PreparedStatement	pStatement	=	null;
	  try {
		  
		  
		  if(chkSurchages(surchargeDOB)==0){
			  this.getConnection();
		pStatement	= connection.prepareStatement(insertSurChargeQry);
		pStatement.setString(1, surchargeDOB.getSurchargeid());
		pStatement.setString(2, surchargeDOB.getSurchargeDesc());
		pStatement.setInt(3, surchargeDOB.getShipmentMode());
		pStatement.setString(4, surchargeDOB.getRateBreak());
		pStatement.setString(5, surchargeDOB.getRateType());
		pStatement.setString(6, surchargeDOB.getWeightBreaks());
		return pStatement.executeUpdate();
		  }
		  else
			  return 5;
	} catch (SQLException e) {
		e.printStackTrace();
		throw new Exception ("Problem while inserting the SurCharges");
	}
	finally{
		ConnectionUtil.closeConnection(connection);
		ConnectionUtil.closePreparedStatement(pStatement);
	}
	
  }
 public void modifySurCharges(SurchargeDOB surchargeDOB) throws Exception{
	  
	  try {
		  PreparedStatement	pStatement	=	null;
		  this.getConnection();
		  
		pStatement	= connection.prepareStatement(modifySurchargeQry);
		pStatement.setString(1, surchargeDOB.getSurchargeDesc());
		pStatement.setInt(2, surchargeDOB.getShipmentMode());
		pStatement.setString(3, surchargeDOB.getSurchargeid());
		 pStatement.executeUpdate();
		  

	} catch (SQLException e) {
		e.printStackTrace();
		throw new Exception ("Problem while Modifying the SurCharges");
	}
	finally{
		ConnectionUtil.closePreparedStatement(pStatement);//call arder changed by Dilip for PMD Correction 
		ConnectionUtil.closeConnection(connection);
	}
	
  }
 
 public int deleteSurCharges(SurchargeDOB surchargeDOB) throws Exception{
	  
	 ResultSet rs	= null;
	 PreparedStatement	pStatement	=	null;
	 PreparedStatement	pStatement1	=	null;
	 int surChargeCount	=	0;
	  try {
		  
		  this.getConnection();
		  if(surchargeDOB.getShipmentMode()==2)
			  pStatement	= connection.prepareStatement(delSurchargeChkQrySea);
		  else
			  pStatement	= connection.prepareStatement(delSurchargeQrChkAir);
		  		
		  	pStatement.setString(1, surchargeDOB.getSurchargeid());
				
		  	rs =	pStatement.executeQuery();
		  	if(rs.next())
		  		surChargeCount = rs.getInt(1);
		  	if(surChargeCount==0){
		  		pStatement1 =	connection.prepareStatement(delSurChargeQry);
		  	pStatement1.setString(1, surchargeDOB.getSurchargeid());
		  	pStatement1.setInt(2, surchargeDOB.getShipmentMode());
		  	return pStatement1.executeUpdate();
		  	}
		  	else 
		  		return 5;

	} catch (SQLException e) {
		e.printStackTrace();
		throw new Exception ("Problem while Deleting  the SurCharges");
	}
	finally{
		ConnectionUtil.closeConnection(connection);
		ConnectionUtil.closePreparedStatement(pStatement, rs);
		ConnectionUtil.closePreparedStatement(pStatement1);
	}
	
 }
  
  public String getSurchargeDetails(SurchargeDOB surchargeDOB){
	  
	  	String returnVal	=	"";
	  try {
		  if(chkSurchages(surchargeDOB)>0){
				this.getConnection();		  
				pStatement	= connection.prepareStatement(selectSurchargeQry);
				pStatement.setString(1, surchargeDOB.getSurchargeid());
				pStatement.setInt(2, surchargeDOB.getShipmentMode());
				rs1	= pStatement.executeQuery();
				while(rs1.next())
				{
					surchargeDOB.setSurchargeDesc(rs1.getString("SURCHARGE_DESC"));
					surchargeDOB.setRateBreak(rs1.getString("RATE_BREAK"));
					surchargeDOB.setRateType(rs1.getString("RATE_TYPE"));
					
				}
				 returnVal	=	"";
		  }
		  else
			  returnVal	 =	 "No SurCharges";
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}finally{
		ConnectionUtil.closeConnection(connection);
		ConnectionUtil.closePreparedStatement(pStatement, rs1);
	}
	return returnVal;
  }
  private int chkSurchages(SurchargeDOB surchargeDOB)
  {
	  int surChargeCount	=	0;
	  this.getConnection();
	  try {
		pStmt1	= connection.prepareStatement(chkSurChargeQry);
	
	  pStmt1.setString(1, surchargeDOB.getSurchargeid());
	  pStmt1.setInt(2, surchargeDOB.getShipmentMode());
	  rs1	= pStmt1.executeQuery();
	  if(rs1.next())
		  surChargeCount =	rs1.getInt(1);
	  } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			ConnectionUtil.closeConnection(connection);
			ConnectionUtil.closePreparedStatement(pStmt1, rs1);
		}
	  
			return surChargeCount;
  }
   
  // Added by Silpa For SurCharge View All on 22-Apr-11

  public ArrayList getSurchargeDetailsforviewall()throws Exception{
	  ArrayList<String>  surChargesList = new ArrayList<String>();
	  
	  PreparedStatement	pStmt1	=	null;
	  ResultSet			rs1	=	null;
	try {
		  this.getConnection();
		 
		  //System.out.println("Connection");  
				pStmt1	= connection.prepareStatement(selectSurchargeQry1);
				
				//System.out.println("selectSurchargeQry1--------"+selectSurchargeQry1);  
				rs1	= pStmt1.executeQuery();
				
				while (rs1.next()) {

				surChargesList.add(rs1.getString("SURCHARGE_ID"));
				surChargesList.add(rs1.getString("SURCHARGE_DESC"));
				surChargesList.add(rs1.getString("SHIPMENT_MODE"));
				surChargesList.add(rs1.getString("RATE_BREAK"));
				surChargesList.add(rs1.getString("RATE_TYPE"));

			  }
				
				 
		 
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new Exception("Problem while fetching the SurchargeDetails");
	}
	catch (Exception e) {
			throw new Exception();
	}finally{
		ConnectionUtil.closePreparedStatement(pStmt1, rs1);
		ConnectionUtil.closeConnection(connection);
		
	}//return sb.toString();
	return surChargesList;
  }
  
	  
// Ended  by Silpa For SurCharge View All on 22-Apr-11  
	  
  //added  by Silpa.p For SurCharge View All on 15-06-11
  public ArrayList getSurchargeDetailsforviewallSort(String serch)throws Exception{
	  ArrayList<String>  surChargesList = new ArrayList<String>();
	  
	  PreparedStatement	pStmt1	=	null;
	  ResultSet			rs1	=	null;
	try {
		  this.getConnection();
		 
		  //System.out.println("Connection");  
				pStmt1	= connection.prepareStatement(serch);
				
				//System.out.println("selectSurchargeQry1--------"+selectSurchargeQry1);  
				rs1	= pStmt1.executeQuery();
				
				while (rs1.next()) {

				surChargesList.add(rs1.getString("SURCHARGE_ID"));
				surChargesList.add(rs1.getString("SURCHARGE_DESC"));
				surChargesList.add(rs1.getString("SHIPMENT_MODE"));
				surChargesList.add(rs1.getString("RATE_BREAK"));
				surChargesList.add(rs1.getString("RATE_TYPE"));

			  }
				
				 
		 
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw new Exception("Problem while fetching the SurchargeDetails");
	}
	catch (Exception e) {
			throw new Exception();
	}finally{
		ConnectionUtil.closePreparedStatement(pStmt1, rs1);
		ConnectionUtil.closeConnection(connection);
	  
	}//return sb.toString();
	return surChargesList;
  }//ended
  
}
  

  
  

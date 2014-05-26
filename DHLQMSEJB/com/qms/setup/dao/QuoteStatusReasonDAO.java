package com.qms.setup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.foursoft.esupply.common.util.ConnectionUtil;
import com.qms.setup.java.QuoteStatusReasonDOB;
import com.qms.setup.java.SurchargeDOB;

public class QuoteStatusReasonDAO {
	
	static final 	String 		FILE_NAME	=	"QuoteStatusReasonDAO.java";
	private static  Logger 		logger 		= 	null;
	DataSource 					dataSource	=	null;
	Connection 					connection	=	null;
	PreparedStatement			pstmt		= null;
	ResultSet 					rs			= null;
	
	String						insertStatusReasonQry	= " insert into QMS_STATUS_REASON(ID,STATUS_REASON,Invalid) " +
														  " values(?,?,?)";
	String						checkStatusReasonQry	= " SELECT count(*) from QMS_STATUS_REASON SR " +
														  " WHERE sr.status_reason=?";
	String						selectStatusReasonQuery	= "SELECT sr.id,sr.status_reason,sr.invalid FROM QMS_STATUS_REASON SR " +
														 "WHERE sr.status_reason =?";
	String						selectStatusReasonQuery1	= " SELECT status_reason from QMS_STATUS_REASON  ";
	
	String						updateStatusReasonQuery	= "UPDATE QMS_STATUS_REASON SR SET SR.STATUS_REASON=?,sr.invalid=? WHERE SR.ID=?";
	String						deleteStatusReasonQuery	= "DELETE FROM QMS_STATUS_REASON SR WHERE sr.id=?";
	String						statusReasonAttachedToQuote = "SELECT Count(*)" +
															  "  FROM QMS_STATUS_REASON SR, QMS_QUOTE_MASTER QM" + 
															  " WHERE qm.status_reason = sr.id" + 
															  "   and sr.status_reason = ? ";

	
	public QuoteStatusReasonDAO()
	  {
	    logger  = Logger.getLogger(QuoteStatusReasonDAO.class);
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
	  
	  public int insertStatusReason(QuoteStatusReasonDOB statusReasonDOB) throws Exception{
		  
		  int statusReasonId	=	0;
		  Statement stmt		= null;
		  
		 
		  try {
			  
			  this.getConnection();
			  
			  if(chkStatusReason(statusReasonDOB)==0){
				  stmt = connection.createStatement();
				  rs = stmt.executeQuery("SELECT QMS_STATUS_REASON_SEQ.NEXTVAL FROM DUAL");
				 
				  if(rs.next())
					  statusReasonId = rs.getInt(1);
				  
				  	  pstmt	= connection.prepareStatement(insertStatusReasonQry);
				  	  pstmt.setInt(1,statusReasonId);
				  	  pstmt.setString(2, statusReasonDOB.getStatusReason());
				  	  pstmt.setString(3, "F");
				
				return pstmt.executeUpdate();
				  }
			  else
				  return 5;
			  
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception ("Problem while inserting the Status Reason");
		}
		catch (Exception e) {
				throw new Exception("Problem while inserting the Status Reason");}
		
		finally{
			ConnectionUtil.closeStatement(stmt, rs);
			ConnectionUtil.closePreparedStatement(pstmt);
			ConnectionUtil.closeConnection(connection);
						
		}
		
	  }
	  
	  
	  private int chkStatusReason(QuoteStatusReasonDOB statusReasonDOB)throws Exception
	  {
		  int statusReasonCount	=	0;
		  
		  try {
			pstmt	= connection.prepareStatement(checkStatusReasonQry);
		
			pstmt.setString(1, statusReasonDOB.getStatusReason());
			rs	= pstmt.executeQuery();
		  
			if(rs.next())
				statusReasonCount =	rs.getInt(1);
		  
		  }catch (SQLException e) {
				e.printStackTrace();
				throw new Exception();
		  
		  }finally{
				ConnectionUtil.closePreparedStatement(pstmt, rs);
			}
		  
				return statusReasonCount;
	  }
	  
	  public String getStatusReason(QuoteStatusReasonDOB statusReasonDOB)throws Exception{
		  
		  	String returnVal	=	"No StatusReasons";
		  	
		  try {
			  this.getConnection();
			  if(chkStatusReason(statusReasonDOB)>0){
						  
					pstmt	= connection.prepareStatement(selectStatusReasonQuery);
					pstmt.setString(1, statusReasonDOB.getStatusReason());
					
					rs	= pstmt.executeQuery();
					while(rs.next())
					{
						statusReasonDOB.setId(rs.getInt("id"));
						statusReasonDOB.setStatusReason(rs.getString("status_Reason"));
						statusReasonDOB.setInvalid(rs.getString("invalid"));
					}
					 returnVal	=	"";
			  }
			  else
				  returnVal	 =	 "No StatusReasons";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Problem while fetching the Status Reason");
		}
		catch (Exception e) {
				throw new Exception();
		}finally{
			ConnectionUtil.closePreparedStatement(pstmt, rs);
			ConnectionUtil.closeConnection(connection);
			
		}
		return returnVal;
	  }
	  //@ added by silpa for status reason view all
	  
	  public ArrayList getStatusReasonViewall()throws Exception{
		  
		  ArrayList <String> StatusReasonlist = new ArrayList <String>();
		  	
		  	//QuoteStatusReasonDOB  statusReasonDOB;
		  try {
			  this.getConnection();
			 
						  
					pstmt	= connection.prepareStatement(selectStatusReasonQuery1);
					//pstmt.setString(1, statusReasonDOB.getStatusReason());
					
					rs	= pstmt.executeQuery();
					
					while(rs.next())
					{
						
					
						
						//statusReasonDOB.setId(rs.getInt("id"));
						//statusReasonDOB.setStatusReason(rs.getString("status_Reason"));
						//statusReasonDOB.setInvalid(rs.getString("invalid"));
						
						//list.add(rs.getInt("id"));
						 //System.out.println("ID----->"+rs.getInt("id"));
						//list.add(rs.getString("status_Reason"));
						//System.out.println("status_Reason-------->"+rs.getString("status_Reason"));
						//list.add(rs.getString("invalid"));
						//System.out.println("invalid-------->"+rs.getString("invalid"));
                                                StatusReasonlist.add(rs.getString("status_Reason"));
					}
					
					 
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Problem while fetching the Status Reason");
		}
		catch (Exception e) {
				throw new Exception();
		}finally{
			ConnectionUtil.closePreparedStatement(pstmt, rs);
			ConnectionUtil.closeConnection(connection);
			
		}//return sb.toString();
		return StatusReasonlist;
	  }
	  
	  
	 //@ ended by silpa for statusreason view all 
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  
	  public String modifyStatusReason(QuoteStatusReasonDOB statusReasonDOB)throws Exception{
		  String status="";
		  String temp	= "";
		  String operation = statusReasonDOB.getOperation();
		  if("Invalidate".equals(operation))
			  temp = "T".equalsIgnoreCase(statusReasonDOB.getInvalid())?"Invalidated":"Validated";
		  else
			  temp = "Modified";
		  
		  try {
			  
			  this.getConnection();
			 
			if("T".equals(statusReasonDOB.getInvalid())){
				if(chkQuotesForStatusReason(statusReasonDOB)==0){
				    pstmt	= connection.prepareStatement(updateStatusReasonQuery);
					pstmt.setString(1, statusReasonDOB.getStatusReason());
					pstmt.setString(2, statusReasonDOB.getInvalid());
					pstmt.setInt(3, statusReasonDOB.getId());
					pstmt.executeUpdate();
					status = "StatusReason '"+statusReasonDOB.getStatusReason()+"' was Successfully "+temp;
				}else{
					status =  "StatusReason '"+statusReasonDOB.getStatusReason()+"' was in use, Not Invalidated ";
				}
			}else{
				pstmt	= connection.prepareStatement(updateStatusReasonQuery);
				pstmt.setString(1, statusReasonDOB.getStatusReason());
				pstmt.setString(2, statusReasonDOB.getInvalid());
				pstmt.setInt(3, statusReasonDOB.getId());
				pstmt.executeUpdate();
				status = "StatusReason '"+statusReasonDOB.getStatusReason()+"'was Successfully "+temp;
				
			}
			  

		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception ("Problem while Updating the StatusReason");
		}
		finally{
			ConnectionUtil.closePreparedStatement(pstmt);
			ConnectionUtil.closeConnection(connection);
		}
		return status;
	  }
		
		 public String deleteStatusReason(QuoteStatusReasonDOB statusReasonDOB)throws Exception{
			  
			 String status = "";
			  try {
				  this.getConnection();
				  
				  if(chkQuotesForStatusReason(statusReasonDOB)==0){
						
					  pstmt	= connection.prepareStatement(deleteStatusReasonQuery);
						pstmt.setInt(1, statusReasonDOB.getId());
						pstmt.executeUpdate();
						status =  "StatusReason '"+statusReasonDOB.getStatusReason()+"'was Successfully Deleted. ";
				  }else{
					  	status =  "StatusReason '"+statusReasonDOB.getStatusReason()+"' was in use";
				  }

			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception ("Problem while Deleting the StatusReason");
			}
			finally{
				ConnectionUtil.closePreparedStatement(pstmt);
				ConnectionUtil.closeConnection(connection);
			}
			
			return status;
	  }
		 
		 
		 private int chkQuotesForStatusReason(QuoteStatusReasonDOB statusReasonDOB)throws Exception
		  {
			  int statusReasonCount	=	0;
			  
			  try {
				pstmt	= connection.prepareStatement(statusReasonAttachedToQuote);
			
				pstmt.setString(1, statusReasonDOB.getStatusReason());
				rs	= pstmt.executeQuery();
			  
				if(rs.next())
					statusReasonCount =	rs.getInt(1);
			  
			  }catch (SQLException e) {
					e.printStackTrace();
					throw new Exception();
			  
			  }finally{
					ConnectionUtil.closePreparedStatement(pstmt, rs);
				}
			  
					return statusReasonCount;
		  }
		 
	 
	
	
	
}

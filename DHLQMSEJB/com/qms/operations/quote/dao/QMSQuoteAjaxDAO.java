

/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: QMSQuoteAjaxDAO
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */





package com.qms.operations.quote.dao;


import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.esupply.common.util.StringUtility;

import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;

import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.rates.dao.RatesDao;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.sql.Types;
import java.util.*;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleTypes;


public class QMSQuoteAjaxDAO {
		private static final 	String 		FILE_NAME		= "QMSQuoteAjaxDAO.java";
		private static Logger logger = null;
		private DataSource dataSource = null;
		
		public QMSQuoteAjaxDAO()  
		 {
	   logger  = Logger.getLogger(QMSQuoteAjaxDAO.class);
	   try
	      {
	        InitialContext ic =new InitialContext();
	        dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
	      }
	      catch(NamingException nex)
	      {
	        throw new EJBException(nex.toString());
	      }catch(Exception e)
	      {
	        throw new EJBException(e.toString());        
	      }
			 
		 }
	public String getCustomerAddress(String custId ,ESupplyGlobalParameters loginBean)throws Exception {
		
			
	PreparedStatement pstmt1 	=	null;
	Connection con				=	null;
	ResultSet	rs				=	null;
	String qryForCustAddress 	=	null;
	String qryForCustValid		=	null;
	String accessType			=	loginBean.getAccessType();
	OperationsImpl      impl    =	null;
	String  add			=	null;
	StringBuffer   terminalQryCustQuote     = new StringBuffer();
	/*String  addLine2			=	null;
	String  addLine3			=	null;*/
	try{ 
		//System.out.println("custId--->"+custId);
		impl = new OperationsImpl();
		qryForCustAddress 		= " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=?) OPERATIONS_EMAILID "
							        + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
							        + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
							        + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? ) ";
		if("HO_TERMINAL".equals(accessType)){
			     terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
		}else if("ADMN_TERMINAL".equals(accessType)){
				terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
						            .append( " UNION ")
						            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
						            .append( " UNION ")
						            .append( " SELECT ? term_id FROM DUAL ")
						            .append( " UNION ")
						            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

		}else if("OPER_TERMINAL".equals(accessType)){
				terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
						            .append( " UNION ")
						            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
						            .append( " UNION ")
						            .append( " SELECT ? term_id FROM DUAL ")
						            .append( " UNION ")
						            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
						            .append(" union ")
						            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
						            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
						            .append(" where fr1.child_terminal_id= ? ))");
		}
		qryForCustValid ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID = ? AND TERMINALID IN "+terminalQryCustQuote.toString();
		con = impl.getConnection();
		pstmt1 = con.prepareStatement(qryForCustValid);
		pstmt1.setString(1, custId);
		System.out.println("for load test--QMSQuoteAjaxDAO---accessType"+accessType);
		System.out.println("for load test--QMSQuoteAjaxDAO---loginBean.getTerminalId()-----"+loginBean.getTerminalId());
		if(!"HO_TERMINAL".equals(accessType)){
			if("ADMN_TERMINAL".equals(accessType))
	          {
				pstmt1.setString(2,loginBean.getTerminalId());
				pstmt1.setString(3,loginBean.getTerminalId());
				pstmt1.setString(4,loginBean.getTerminalId());
	          }else{
	        	  pstmt1.setString(2,loginBean.getTerminalId());
				  pstmt1.setString(3,loginBean.getTerminalId());
				  pstmt1.setString(4,loginBean.getTerminalId());
				  pstmt1.setString(5,loginBean.getTerminalId());
	          }
		}
		rs = pstmt1.executeQuery();
		if(!rs.next()){
		  add = "Customer is Invalid Or Does not exist.";
		}
		else{
		pstmt1.close();
		rs.close();
		pstmt1 = con.prepareStatement(qryForCustAddress);
		pstmt1.setString(1, custId);
		pstmt1.setString(2, custId);
		rs = pstmt1.executeQuery();
		System.out.println(custId);
		if(rs.next())
		{
			add  = 		    (rs.getString("ADDRESSID")!=null?rs.getString("ADDRESSID"):"")+
				        "$"+(rs.getString("ADDRESSLINE1")!=null?rs.getString("ADDRESSLINE1"):"")+
						"$"+(rs.getString("ADDRESSLINE2")!=null?rs.getString("ADDRESSLINE2"):"")+
						"$"+(rs.getString("ADDRESSLINE3")!=null?rs.getString("ADDRESSLINE3"):"")+
						"$"+(rs.getString("CITY")!=null?rs.getString("CITY"):"")+
						"$"+(rs.getString("STATE")!=null?rs.getString("STATE"):"")+
						"$"+(rs.getString("COUNTRYNAME")!=null?rs.getString("COUNTRYNAME"):"")+
						"$"+(rs.getString("ZIPCODE")!=null?rs.getString("ZIPCODE"):"");
			//			add.concat(rs.getString("OPERATIONS_EMAILID")+"\n");
		}
		}
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}finally{
	    if(pstmt1!=null)
	    	pstmt1.close();
	    if(con != null)
	    	con.close();
	    if(rs !=null)
	    	rs.close();
	}
	return add;
	}
	
/*public String getCustomerAttention(String custId ,ESupplyGlobalParameters loginBean,String addressType)throws Exception {
		
		
		PreparedStatement pstmt1 	=	null;
		Connection con				=	null;
		ResultSet	rs				=	null;
		//String qryForCustAddress 	=	null;
		String qryForCustValid		=	null;
		String accessType			=	loginBean.getAccessType();
		OperationsImpl      impl    =	null;
		String  add="$"	;
		StringBuffer   terminalQryCustQuote     = new StringBuffer();
		String			addressCheck=	"";
		String  addLine2			=	null;
		String  addLine3			=	null;
		try{ 
			System.out.println("custId--->"+custId);
			impl = new OperationsImpl();
			qryForCustAddress 		= " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=?) OPERATIONS_EMAILID "
								        + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
								        + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
								        + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? ) ";
			if("HO_TERMINAL".equals(accessType)){
				     terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
			}else if("ADMN_TERMINAL".equals(accessType)){
					terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
							            .append( " UNION ")
							            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
							            .append( " UNION ")
							            .append( " SELECT ? term_id FROM DUAL ")
							            .append( " UNION ")
							            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

			}else if("OPER_TERMINAL".equals(accessType)){
					terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
							            .append( " UNION ")
							            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
							            .append( " UNION ")
							            .append( " SELECT ? term_id FROM DUAL ")
							            .append( " UNION ")
							            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
							            .append(" union ")
							            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
							            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
							            .append(" where fr1.child_terminal_id= ? ))");
			}
			qryForCustValid ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQryCustQuote.toString();
			con = impl.getConnection();
			 System.out.println("connection created");
			pstmt1 = con.prepareStatement(qryForCustValid);
			System.out.println("for load test--QMSQuoteAjaxDAO---qryForCustValid"+qryForCustValid);
			pstmt1.setString(1, custId);
			 System.out.println("custId--->"+custId);
			System.out.println("for load test--QMSQuoteAjaxDAO---accessType"+accessType);
			System.out.println("for load test--QMSQuoteAjaxDAO---loginBean.getTerminalId()-----"+loginBean.getTerminalId());
			if(!"HO_TERMINAL".equals(accessType)){
				if("ADMN_TERMINAL".equals(accessType))
		          {
					pstmt1.setString(2,loginBean.getTerminalId());
					pstmt1.setString(3,loginBean.getTerminalId());
					pstmt1.setString(4,loginBean.getTerminalId());
		          }else{
		        	  pstmt1.setString(2,loginBean.getTerminalId());
					  pstmt1.setString(3,loginBean.getTerminalId());
					  pstmt1.setString(4,loginBean.getTerminalId());
					  pstmt1.setString(5,loginBean.getTerminalId());
		          }
			}
			rs = pstmt1.executeQuery();
			if(!rs.next()){
			  add = "Customer is Invalid Or Does not exist.";
			}
			else{
				if(addressType!=null && addressType.length()>0)
			    {
			      if(addressType.equalsIgnoreCase("P"))
			       addressCheck=" AND ADDRTYPE='P' ";
			      else if(addressType.equalsIgnoreCase("D"))
			       addressCheck=" AND ADDRTYPE='D' ";
			      else if(addressType.equalsIgnoreCase("B"))
			       addressCheck=" AND ADDRTYPE='B' ";
			      else
			       addressCheck=" ";
			    }
			    
			    String sql1=  "SELECT CONTACTPERSON FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? " ;
			    	//+"AND CONTACTPERSON IS NOT NULL "+addressCheck+" ";
			 	 
				 
			    pstmt1.close();
				rs.close();
			    pstmt1           =  con.prepareStatement(sql1);
			    System.out.println("sql1--->"+sql1);
			    pstmt1.setString(1,custId);
			    System.out.println("custId--->"+custId);
			      rs             =  pstmt1.executeQuery();
			  
			      while(rs.next())
			      {
			    	  add += rs.getString("CONTACTPERSON")+"$";
			    		  	//(rs.getString(2)!=null?rs.getString(2):"")+"$"+
			    	  		//(rs.getString(3)!=null?rs.getString(3):"")+"$"+
			    	  		//StringUtility.noNull(rs.getString(6))+"$"+
			    	  		//StringUtility.noNull(rs.getString(4))+"$"+
			    	  		//(rs.getString(5)!=null?rs.getString(5):"")+"$"+
			    	  		
			      //records.add(rs.getString(2)+","+rs.getString(3)+","+StringUtility.noNull(rs.getString(6))+","+StringUtility.noNull(rs.getString(4))+","+rs.getString(5)+","+rs.getString(1));
			    System.out.println(add);
			        
			      }
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
		    if(pstmt1!=null)
		    	pstmt1.close();
		    if(con != null)
		    	con.close();
		    if(rs !=null)
		    	rs.close();
		}
		return add;
		}*/

	
	
	
	public String checkSalesPersonValid(String salePersonId , ESupplyGlobalParameters loginBean,String shipmentMode,String servicelevel) throws SQLException {
		System.out.println(salePersonId);
		PreparedStatement pstmt1 	=	null;
		Connection con				=	null;
		ResultSet	rs				=	null;
		OperationsImpl      impl    =	null;
		StringBuffer  result		=	new StringBuffer();
		String marginId				=	null;
		String accessType			=	loginBean.getAccessType();
		String servicelevel_wherecond=  null;
		System.out.println("accessType---"+accessType);
		StringBuffer      terminalQrySales     = new StringBuffer();
		try{
		String qryForSaleperson		=   "SELECT EMPID FROM FS_USERMASTER WHERE EMPID = ? AND LOCATIONID IN ";
			 if("HO_TERMINAL".equals(accessType)){
				 terminalQrySales.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");  
				  
			 }else if("ADMN_TERMINAL".equals(accessType)){
				 terminalQrySales.append("( select child_terminal_id term_id from fs_fr_terminal_regn  ")
		           				 .append(" where parent_terminal_id=? union select ? from dual )");
				 
			 }
		      else if("OPER_TERMINAL".equals(accessType)){
		    	  terminalQrySales.append("(select  parent_terminal_id term_id from fs_fr_terminal_regn ")
	              .append(" where child_terminal_id = ? ")
	            .append(" union ")
	            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
	            .append(" union ")
	             .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
	            .append("where parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= ?)) ");
		   
		      }
			 impl = new OperationsImpl();
		      con = impl.getConnection();
			  pstmt1 = con.prepareStatement(  qryForSaleperson +  terminalQrySales.toString());
			  pstmt1.setString(1,salePersonId);
			  if("OPER_TERMINAL".equals(accessType) || "ADMN_TERMINAL".equals(accessType) )
			  { 
				  pstmt1.setString(2,loginBean.getTerminalId());
				  pstmt1.setString(3,loginBean.getTerminalId());
			  }
		       rs= pstmt1.executeQuery();
		        if(!rs.next())
		        	result.append("Sales Person Code is Invalid Or Does not exist.");
		        else
		        	result.append("Valid");
		        pstmt1.close();
		        rs.close();
		        if("Valid".equalsIgnoreCase(result.toString())){
		        	if("Air".equalsIgnoreCase(shipmentMode)){
		        		marginId      = "1";
		        	}else if("Sea".equalsIgnoreCase(shipmentMode)){
		        		marginId = "2,4";
		        	}else if("Truck".equalsIgnoreCase(shipmentMode)){
		        		marginId = "7,15";
		        	}
		        	if(servicelevel != null && !"".equals(servicelevel)){
		        		servicelevel_wherecond ="AND SERVICE_LEVEL = ? ";
		        	}
		        	
		        	
		        pstmt1=	con.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
		        			servicelevel_wherecond+" AND MARGIN_ID IN ("+marginId+") AND LEVELNO  = (SELECT LEVEL_NO "+
                            	"FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))");
		        	 if(servicelevel != null && !"".equals(servicelevel))
		             {
		               pstmt1.setString(1,servicelevel);
		               pstmt1.setString(2,servicelevel);
		             }
		             else
		             {
		               pstmt1.setString(1,salePersonId);
		             }
                     rs =pstmt1.executeQuery();
                     if(!rs.next())
		        	result.append("No Margin Limit is Defined for Freight for the Sales Person "+salePersonId+", Quote Type "+shipmentMode);
                     if(servicelevel != null && !"".equals(servicelevel))
                    	 result.append("And Service Level "+servicelevel);
                     pstmt1.close();
                     rs.close();
                     pstmt1 = con.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO  = (SELECT LEVEL_NO "+
                                              "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
                                              "AND CHARGETYPE=? AND INVALIDATE='F'");
                     pstmt1.setString(1,salePersonId);
                     pstmt1.setString(2,"CHARGES");
                     rs=pstmt1.executeQuery();
                     if(!rs.next())
                     result.append("No Margin Limit is Defined for Charges for the Sales Person "+salePersonId+".<br>");
                     
                     pstmt1.close();
                     rs.close();
                     pstmt1=con.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO  = (SELECT LEVEL_NO "+
                             "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
                             "AND CHARGETYPE=? AND INVALIDATE='F'");
                     pstmt1.setString(1,salePersonId);
                     pstmt1.setString(2,"CARTAGES");
                     rs=pstmt1.executeQuery();
                     if(!rs.next())
                         result.append("No Margin Limit is Defined for Cartage Charges for the Sales Person "+salePersonId+".<br>");
                     
		        }
		      
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 if(pstmt1!=null)
			    	pstmt1.close();
			    if(con != null)
			    	con.close();
			    if(rs !=null)
			    	rs.close();
		}
	return result.toString();	
	}
	
	public String  originDestinationValid(String locationId,String shipmentMode) throws SQLException{
		PreparedStatement pstmt1 	=	null;
		Connection con				=	null;
		ResultSet	rs				=	null;
		OperationsImpl      impl    =	null;
		String  result				=	null;
		String shipment				=	null;
		 if("Truck".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (4,5,6,7) ";
		   }else if("AIR".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
		  }else if("SEA".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
		  }
		 try {
			 String qryForOrgDestCheck = "SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
			 
			 impl = new OperationsImpl();
		     con = impl.getConnection();
		     pstmt1 = con.prepareStatement( qryForOrgDestCheck);
		     pstmt1.setString(1,locationId);
		     rs=pstmt1.executeQuery();
		     if(!rs.next())
		    	 result = "Location id is Invalid";
		     else 
		    	 result = "";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			 if(pstmt1!=null)
			    	pstmt1.close();
			    if(con != null)
			    	con.close();
			    if(rs !=null)
			    	rs.close();
		}
			 
		 return result;
	}
	
	public String  originDestinationPortValid(String portId,String shipmentMode) throws SQLException{
		PreparedStatement pstmt1 	=	null;
		Connection con				=	null;
		ResultSet	rs				=	null;
		OperationsImpl      impl    =	null;
		String  result				=	null;
		String shipment				=	null;
		 if("Truck".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (4,5,6,7) ";
		   }else if("AIR".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
		  }else if("SEA".equalsIgnoreCase(shipmentMode)){
		        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
		  }
		 try {
			 String qryForOrgDestPortCheck = null;
			 if(!"SEA".equalsIgnoreCase(shipmentMode))
			  qryForOrgDestPortCheck = " SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
			 else
			 qryForOrgDestPortCheck = " SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID = ? AND (INVALIDATE ='F' OR INVALIDATE IS NULL)";	 
			 
			 impl = new OperationsImpl();
		     con = impl.getConnection();
		     pstmt1 = con.prepareStatement( qryForOrgDestPortCheck);
		     pstmt1.setString(1,portId);
		     rs=pstmt1.executeQuery();
		     if(!rs.next())
		    	 result = "Port id is Invalid";
		     else 
		    	 result = "";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			 if(pstmt1!=null)
			    	pstmt1.close();
			    if(con != null)
			    	con.close();
			    if(rs !=null)
			    	rs.close();
		}
			 
		 return result;
	}
	  public String checkIndustryValid(String industryId,ESupplyGlobalParameters loginBean)throws Exception,SQLException {
		  	PreparedStatement pstmt1 	=	null;
			Connection con				=	null;
			ResultSet	rs				=	null;
			OperationsImpl      impl    =	null;
			String  result				=	null;
			String accessType			=	loginBean.getAccessType();
		    StringBuffer terminalQry	=	new StringBuffer();
		    String qryForIndustryCheck	=	null;
		    try{
		    if("HO_TERMINAL".equalsIgnoreCase(accessType)){
		    	terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
		    }else{
	    	 terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
		                .append( " UNION ")
		                .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
		                .append( " UNION ")
		                .append( " SELECT ? term_id FROM DUAL ")
		                .append( " UNION ")
		                .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
		    }
		    qryForIndustryCheck	= "SELECT INDUSTRY_ID FROM QMS_INDUSTRY_REG WHERE INVALIDATE = 'F' AND INDUSTRY_ID = ? AND TERMINALID IN "+terminalQry.toString();
		    impl = new OperationsImpl();
		     con = impl.getConnection();
		     pstmt1 = con.prepareStatement(qryForIndustryCheck);
		     pstmt1.setString(1,industryId);
		     if(!"HO_TERMINAL".equalsIgnoreCase(accessType)){
		    	 pstmt1.setString(2,loginBean.getTerminalId());
		    	 pstmt1.setString(3,loginBean.getTerminalId());
		    	 pstmt1.setString(4,loginBean.getTerminalId());
		    	 
		     }
		     rs= pstmt1.executeQuery();
		     if(!rs.next())
		    	 result = "Industry Id is Invalid Or Does not exist."; 
		    }catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				 if(pstmt1!=null)
				    	pstmt1.close();
				    if(con != null)
				    	con.close();
				    if(rs !=null)
				    	rs.close();
			}
			return result;
			
	}
	  
public String  checkComodityValid(String comodityId,String shipmentMode,ESupplyGlobalParameters loginBean)throws Exception {
	PreparedStatement pstmt1 	=	null;
	Connection con				=	null;
	ResultSet	rs				=	null;
	OperationsImpl      impl    =	null;
	String  result				=	null;
	String accessType			=	loginBean.getAccessType();
	StringBuffer terminalQry	=	new StringBuffer();
    String qryForComodityCheck	=	null;
    try{
    	if("HO_TERMINAL".equalsIgnoreCase(accessType)){
	    	terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
	    }else{
    	 terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
	                .append( " UNION ")
	                .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
	                .append( " UNION ")
	                .append( " SELECT ? term_id FROM DUAL ")
	                .append( " UNION ")
	                .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
	    }
    	
    	qryForComodityCheck	=	"SELECT COMODITYID FROM FS_FR_COMODITYMASTER WHERE INVALIDATE = 'F' AND COMODITYID=? AND TERMINALID IN "+terminalQry.toString();
    	impl = new OperationsImpl();
	    con = impl.getConnection();
	    pstmt1 = con.prepareStatement(qryForComodityCheck);
	    pstmt1.setString(1,comodityId);
	    if(!"HO_TERMINAL".equalsIgnoreCase(accessType)){
	    	 pstmt1.setString(2,loginBean.getTerminalId());
	    	 pstmt1.setString(3,loginBean.getTerminalId());
	    	 pstmt1.setString(4,loginBean.getTerminalId());
	    	 
	     }
	     rs= pstmt1.executeQuery();
	     if(!rs.next())
	    	 result = "COMMODITY VALUE IS INVALID OR DOES NOT EXIST ";
    	}catch (Exception e) {
		// TODO: handle exception
    	e.printStackTrace();
	}finally{
		if(pstmt1!=null)
	    	pstmt1.close();
	    if(con != null)
	    	con.close();
	    if(rs !=null)
	    	rs.close();
	}
	return result;
	}

public String checkServiceLevelValid(String servicelevel,String shipmentMode,ESupplyGlobalParameters loginBean)throws Exception,SQLException {
	  
	PreparedStatement pstmt1 	=	null;
	Connection con				=	null;
	ResultSet	rs				=	null;
	OperationsImpl      impl    =	null;
	String  result				=	null;
	String accessType			=	loginBean.getAccessType();
	String shipment 			= null;
	String qryForservicelevelCheck = null;
	StringBuffer terminalQry	=	new StringBuffer();
	String whereCondition		=	null;
	try{
		if("HO_TERMINAL".equalsIgnoreCase(accessType)){
	    	terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
	    }else{
    	 terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
	                .append( " UNION ")
	                .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
	                .append( " UNION ")
	                .append( " SELECT ? term_id FROM DUAL ")
	                .append( " UNION ")
	                .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
	    }		
	if("Truck".equalsIgnoreCase(shipmentMode)){
        shipment 		= " AND SHIPMENTMODE IN (4,5,6,7) ";
        whereCondition	= "WHERE SERVICELEVELID =? "+shipment;	
   }else if("AIR".equalsIgnoreCase(shipmentMode)){
        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
        whereCondition	= "WHERE SERVICELEVELID =? "+shipment;
  }else if("SEA".equalsIgnoreCase(shipmentMode)){
        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
        whereCondition	= "WHERE SERVICELEVELID =? "+shipment;
  }

	qryForservicelevelCheck	 = "SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER "+whereCondition+" AND INVALIDATE='F' AND TERMINALID IN "+terminalQry.toString() ;
	impl = new OperationsImpl();
    con = impl.getConnection();
    pstmt1 = con.prepareStatement(qryForservicelevelCheck);
    pstmt1.setString(1, servicelevel);
    if(!"HO_TERMINAL".equalsIgnoreCase(accessType)){
   	 pstmt1.setString(2,loginBean.getTerminalId());
   	 pstmt1.setString(3,loginBean.getTerminalId());
   	 pstmt1.setString(4,loginBean.getTerminalId());
   	}
    rs  = pstmt1.executeQuery();
    if(!rs.next())
		result = "Service Level Id is In Correct Or Does not exist Or does not exist for this Shipment Mode.";
			
		
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
	}finally{
		if(pstmt1!=null)
	    	pstmt1.close();
	    if(con != null)
	    	con.close();
	    if(rs !=null)
	    	rs.close();
	
}
	return result;
}

public String checkRateExistsForTheLane(String org,String dest,String ShipmentMod,String weightBreak,ESupplyGlobalParameters loginBean) throws SQLException
{
	String checkRateQry = 
		"SELECT COUNT(*) FROM QMS_BUYRATES_MASTER BM,QMS_BUYRATES_DTL BD  " +
		"WHERE BD.ORIGIN =? " + 
		"AND BD.DESTINATION =? " + 
		"AND BM.SHIPMENT_MODE = ?" + 
		"AND BM.WEIGHT_BREAK  = ? " + 
		"AND BD.BUYRATEID = BM.BUYRATEID " + 
		"AND (BD.LANE_NO = BM.LANE_NO OR BM.LANE_NO IS NULL) " + 
		"AND BD.VERSION_NO = BM.VERSION_NO " + 
		"AND BD.ACTIVEINACTIVE IS NULL  " + 
		"AND (BD.INVALIDATE='F' OR BD.INVALIDATE IS NULL) " + 
		"AND BM.TERMINALID IN( " + 
		" SELECT CHILD_TERMINAL_ID TERMINALID " + 
		" FROM FS_FR_TERMINAL_REGN " + 
		" CONNECT  By PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID " + 
		" Start With Parent_Terminal_Id = ? " + 
		" Union" + 
		" Select Terminalid " + 
		" From Fs_Fr_Terminalmaster " + 
		" Where Terminalid = ? " + 
		" Union" + 
		" Select Parent_Terminal_Id Terminalid " + 
		" From Fs_Fr_Terminal_Regn" + 
		" Connect By Prior Parent_Terminal_Id = Child_Terminal_Id " + 
		" Start With Child_Terminal_Id = ? " + 
		" Union " + 
		" Select Terminalid " + 
		" From Fs_Fr_Terminalmaster " + 
		" Where Oper_Admin_Flag = 'H' )";
	OperationsImpl      impl    =	null;
	PreparedStatement pstmt1 	=	null;
	Connection con				=	null;
	ResultSet	rs				=	null;
	int count                   = 0;
	String result               = null;
	try{ 
		impl = new OperationsImpl();
	   con = impl.getConnection();
		pstmt1 = con.prepareStatement(checkRateQry);
		pstmt1.setString(1,org);
		pstmt1.setString(2,dest);
		pstmt1.setString(3,ShipmentMod);
		pstmt1.setString(4,weightBreak);
		pstmt1.setString(5,loginBean.getTerminalId());
		pstmt1.setString(6,loginBean.getTerminalId());
		pstmt1.setString(7,loginBean.getTerminalId());
		 rs = pstmt1.executeQuery();
		if(rs.next())
			count =	rs.getInt(1);
		if(count == 0)
		result  = "NO Rate is Defined for the Lane "+org+"-"+dest;
		else
		result  =  null;	
		
}catch (Exception e) {
	e.printStackTrace();
	
}finally{
	if(pstmt1!= null)
		pstmt1.close();
	if(rs!= null)
		rs.close();
	if(con!= null)
		con.close();
} 
return result;

}

	// Added By Kishore Podili For SingleQuote and MultiQuote Validarion

		public String checkQuoteId(String quote,ESupplyGlobalParameters loginBean)throws Exception,SQLException {
		  	PreparedStatement pstmt1 	=	null;
			Connection con				=	null;
			ResultSet	rs				=	null;
			OperationsImpl impl			=	null;
			String  result				=	null;
			String 	quoteIdQuery		=	"SELECT DISTINCT qm.quote_id FROM QMS_QUOTE_MASTER QM " +
													"WHERE qm.quote_id like ? and qm.is_multi_quote=?";
		 
			
			String quoteId				= null;
			String isMultiQuote			= null;
			
		    try{
		   
		    	quoteId 	=  quote.substring(0, quote.indexOf('~'));
		    	isMultiQuote = quote.substring(quote.indexOf('~')+1); 
		    	 	
		    	impl = new OperationsImpl();
		    	con = impl.getConnection();
		    	
		    	pstmt1 = con.prepareStatement(quoteIdQuery);
		        pstmt1.setString(1,quoteId+"%");
		        pstmt1.setString(2,isMultiQuote);
		    	
		        rs= pstmt1.executeQuery();
		     
		        if(!rs.next())
		        	result = " is Invalid Or Does not exist."; 
		    }catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				 if(pstmt1!=null)
				    	pstmt1.close();
				    if(con != null)
				    	con.close();
				    if(rs !=null)
				    	rs.close();
			}
			return result;
			
		}
		
		// End Of Kishore Podili For SingleQuote and MultiQuote Validarion

		//Added on 21Feb2011 for getting Attention To values Ajax Call		
		public String getCustomerAttention(String custId ,ESupplyGlobalParameters loginBean,String addressType)throws Exception {
			
			
			PreparedStatement pstmt1 	=	null;
			Connection con				=	null;
			ResultSet	rs				=	null;
			//String qryForCustAddress 	=	null;
			String qryForCustValid		=	null;
			String accessType			=	loginBean.getAccessType();
			OperationsImpl      impl    =	null;
			String  add			=	"";
			StringBuffer   terminalQryCustQuote     = new StringBuffer();
			String			addressCheck=	"";
			/*String  addLine2			=	null;
			String  addLine3			=	null;*/
			try{ 
				System.out.println("custId--->"+custId);
				impl = new OperationsImpl();
				/*qryForCustAddress 		= " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=?) OPERATIONS_EMAILID "
									        + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
									        + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
									        + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? ) ";*/
				if("HO_TERMINAL".equals(accessType)){
					     terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
				}else if("ADMN_TERMINAL".equals(accessType)){
						terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
								            .append( " UNION ")
								            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
								            .append( " UNION ")
								            .append( " SELECT ? term_id FROM DUAL ")
								            .append( " UNION ")
								            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

				}else if("OPER_TERMINAL".equals(accessType)){
						terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
								            .append( " UNION ")
								            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
								            .append( " UNION ")
								            .append( " SELECT ? term_id FROM DUAL ")
								            .append( " UNION ")
								            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
								            .append(" union ")
								            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
								            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
								            .append(" where fr1.child_terminal_id= ? ))");
				}
				qryForCustValid ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID = ? AND TERMINALID IN "+terminalQryCustQuote.toString();
				con = impl.getConnection();
				pstmt1 = con.prepareStatement(qryForCustValid);
				pstmt1.setString(1, custId);
				System.out.println("for load test--QMSQuoteAjaxDAO---accessType"+accessType);
				System.out.println("for load test--QMSQuoteAjaxDAO---loginBean.getTerminalId()-----"+loginBean.getTerminalId());
				if(!"HO_TERMINAL".equals(accessType)){
					if("ADMN_TERMINAL".equals(accessType))
			          {
						pstmt1.setString(2,loginBean.getTerminalId());
						pstmt1.setString(3,loginBean.getTerminalId());
						pstmt1.setString(4,loginBean.getTerminalId());
			          }else{
			        	  pstmt1.setString(2,loginBean.getTerminalId());
						  pstmt1.setString(3,loginBean.getTerminalId());
						  pstmt1.setString(4,loginBean.getTerminalId());
						  pstmt1.setString(5,loginBean.getTerminalId());
			          }
				}
				rs = pstmt1.executeQuery();
				if(!rs.next()){
				  add = "Customer is Invalid Or Does not exist.";
				}
				else{
					if(addressType!=null && addressType.length()>0)
				    {
				      if(addressType.equalsIgnoreCase("P"))
				       addressCheck=" AND ADDRTYPE='P' ";
				      else if(addressType.equalsIgnoreCase("D"))
				       addressCheck=" AND ADDRTYPE='D' ";
				      else if(addressType.equalsIgnoreCase("B"))
				       addressCheck=" AND ADDRTYPE='B' ";
				      else
				       addressCheck=" ";
				    }
				    
				    String sql1=  "SELECT SL_NO,CONTACTPERSON,NVL(EMAILID,FAX),FAX,CUSTOMERID,CONTACTNO FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? "+
				                  "AND CONTACTPERSON IS NOT NULL AND ACTIVE_STATUS='A' "+addressCheck+" ORDER BY SL_NO";
				 	 
					 
				    pstmt1.close();
					rs.close();
				    pstmt1           =  con.prepareStatement(sql1);
				    pstmt1.setString(1,custId);
				      rs             =  pstmt1.executeQuery();
				  
				      while(rs.next())
				      {
				    	  add += (rs.getString(2)!=null?rs.getString(2):"")+"$"+
				    	  		(rs.getString(3)!=null?rs.getString(3):"")+"$"+
				    	  		StringUtility.noNull(rs.getString(6))+"$"+
				    	  		StringUtility.noNull(rs.getString(4))+"$"+
				    	  		(rs.getString(5)!=null?rs.getString(5):"")+"$"+
				    	  		(rs.getString(1)!=null?rs.getString(1):"")+",";
				      //records.add(rs.getString(2)+","+rs.getString(3)+","+StringUtility.noNull(rs.getString(6))+","+StringUtility.noNull(rs.getString(4))+","+rs.getString(5)+","+rs.getString(1));
				    
				        
				      }
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				if(rs !=null)
			    	rs.close();
			    if(pstmt1!=null)
			    	pstmt1.close();
			    if(con != null)
			    	con.close();
			    
			}
			return add;
			}

		
		// Added By Kishore Podili For SingleQuote and MultiQuote Validarion

		public String validateZoneCode(String zoneCode,String shipmentMode,String  location,String consoleType)throws Exception,SQLException {
		  	PreparedStatement pstmt1 	=	null;
			Connection con				=	null;
			ResultSet	rs				=	null;
			OperationsImpl impl			=	null;
			String  result				=	"";
			String 	zoneCodeQuery		=	"SELECT distinct d.zone" +
											"  FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D" + 
											" WHERE D.INVALIDATE = 'F'" + 
											"   AND D.ZONE_CODE = M.ZONE_CODE" + 
											"   AND M.ORIGIN_LOCATION = ?" + 
											"   AND M.SHIPMENT_MODE = ?" ;
			
			String 	whereCondotion		= "";
			String temp = "";
			String[] zones = null ;
			String[] resultZones = null;
			
			try{
						
					if("SEA".equalsIgnoreCase(shipmentMode))
						whereCondotion = "AND NVL(M.CONSOLE_TYPE,'~')= ?";
					
					if(zoneCode!=null && zoneCode.length()!=0)
				    {
				    	if(zoneCode.indexOf(',')!=-1){
				    		whereCondotion = whereCondotion+" and ( ";
				    		 zones = zoneCode.split(",");
				    		for(String x: zones)
				    			whereCondotion += "D.ZONE LIKE '"+x+"%' or " ;
				    		
				    		whereCondotion = whereCondotion.substring(0, whereCondotion.length()-3);
				    		whereCondotion+=")";
				    	}
				    	else{
				    		zones = new String[1];
				    		zones[0] = zoneCode;
				    		whereCondotion = whereCondotion +" AND D.ZONE LIKE '"+zoneCode+"%'" ; //Added By Kishore Sea Invalid Column Index
				    	}
				    }
						
				
				zoneCodeQuery = zoneCodeQuery+whereCondotion;
						
				
				impl = new OperationsImpl();
		    	con = impl.getConnection();
		    	
		    	pstmt1 = con.prepareStatement(zoneCodeQuery);
		        
		    	if("SEA".equalsIgnoreCase(shipmentMode)){
					pstmt1.setString(1,location);
				   	pstmt1.setString(2,"2");
				   	pstmt1.setString(3,consoleType);
		    	}
		    	else{
		    		pstmt1.setString(1,location);
			   		pstmt1.setString(2,"1");
		    	}
		    	
		        rs= pstmt1.executeQuery();
		        resultZones = new String[zones.length];
		        int i =0;
		       
		        while(rs.next()){
		        	resultZones[i] =  rs.getString("zone");
		        	i++;
		        }
		        boolean isNotExists = true;
		        for(String x: zones){
		        	for(String y: resultZones){
		        		if(y!= null && y.equalsIgnoreCase(x)){
		        			isNotExists = false;
		        			break;
		        		}
		        		else
		        			isNotExists = true;
		        			
		        	}
		          if(isNotExists)
		        	  result += x+",";
		        }
	    		
		        if(result!=null && !"".equals(result)){
		        	result = result.substring(0, result.length()-1);
		        	result += " zone(s) does not exists for Location: "+location;
		        }
		        
		        
		    }catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				 if(pstmt1!=null)
				    	pstmt1.close();
				    if(con != null)
				    	con.close();
				    if(rs !=null)
				    	rs.close();
			}
			return result;
			
		}
		
		// End Of Kishore Podili For SingleQuote and MultiQuote Validarion


//Added by Rakesh		
public String getSalesPerson(String custId ,ESupplyGlobalParameters loginBean,String addressType)throws Exception {
			
			
			PreparedStatement pstmt1 	=	null;
			Connection con				=	null;
			ResultSet	rs				=	null;
			//String qryForCustAddress 	=	null;
			String qryForCustValid		=	null;
			String accessType			=	loginBean.getAccessType();
			OperationsImpl      impl    =	null;
			String  add			=	"";
			StringBuffer   terminalQryCustQuote     = new StringBuffer();
			String			addressCheck=	"";
			/*String  addLine2			=	null;
			String  addLine3			=	null;*/
			try{ 
				System.out.println("custId--->"+custId);
				impl = new OperationsImpl();
				/*qryForCustAddress 		= " SELECT ADDRESSID,ADDRESSLINE1,ADDRESSLINE2,ADDRESSLINE3,CITY,STATE,COUNTRYNAME,ZIPCODE,(SELECT OPERATIONS_EMAILID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=?) OPERATIONS_EMAILID "
									        + " FROM FS_ADDRESS FSA,FS_COUNTRYMASTER FSC "
									        + " WHERE FSA.COUNTRYID = FSC.COUNTRYID AND "
									        + " ADDRESSID IN (SELECT CUSTOMERADDRESSID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? ) ";*/
				if("HO_TERMINAL".equals(accessType)){
					     terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
				}else if("ADMN_TERMINAL".equals(accessType)){
						terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
								            .append( " UNION ")
								            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
								            .append( " UNION ")
								            .append( " SELECT ? term_id FROM DUAL ")
								            .append( " UNION ")
								            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

				}else if("OPER_TERMINAL".equals(accessType)){
						terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
								            .append( " UNION ")
								            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
								            .append( " UNION ")
								            .append( " SELECT ? term_id FROM DUAL ")
								            .append( " UNION ")
								            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
								            .append(" union ")
								            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
								            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
								            .append(" where fr1.child_terminal_id= ? ))");
				}
				qryForCustValid ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID = ? AND TERMINALID IN "+terminalQryCustQuote.toString();
				con = impl.getConnection();
				pstmt1 = con.prepareStatement(qryForCustValid);
				pstmt1.setString(1, custId);
				System.out.println("for load test--QMSQuoteAjaxDAO---accessType"+accessType);
				System.out.println("for load test--QMSQuoteAjaxDAO---loginBean.getTerminalId()-----"+loginBean.getTerminalId());
				if(!"HO_TERMINAL".equals(accessType)){
					if("ADMN_TERMINAL".equals(accessType))
			          {
						pstmt1.setString(2,loginBean.getTerminalId());
						pstmt1.setString(3,loginBean.getTerminalId());
						pstmt1.setString(4,loginBean.getTerminalId());
			          }else{
			        	  pstmt1.setString(2,loginBean.getTerminalId());
						  pstmt1.setString(3,loginBean.getTerminalId());
						  pstmt1.setString(4,loginBean.getTerminalId());
						  pstmt1.setString(5,loginBean.getTerminalId());
			          }
				}
				rs = pstmt1.executeQuery();
				if(!rs.next()){
				  add = "Customer is Invalid Or Does not exist.";
				}
				else{
					if(addressType!=null && addressType.length()>0)
				    {
				      if(addressType.equalsIgnoreCase("P"))
				       addressCheck=" AND ADDRTYPE='P' ";
				      else if(addressType.equalsIgnoreCase("D"))
				       addressCheck=" AND ADDRTYPE='D' ";
				      else if(addressType.equalsIgnoreCase("B"))
				       addressCheck=" AND ADDRTYPE='B' ";
				      else
				       addressCheck=" ";
				    }
				    //Modified by Rakesh
				    String sql1=  "SELECT DISTINCT MASTER.SALESPERSON FROM QMS_CUST_CONTACTDTL CONT,FS_FR_CUSTOMERMASTER MASTER WHERE CONT.CUSTOMERID=? "+
				                  "AND CONT.CUSTOMERID=MASTER.CUSTOMERID AND CONT.TERMINALID=MASTER.TERMINALID AND CONTACTPERSON IS NOT NULL ";
				 	 
					 
				    pstmt1.close();
					rs.close();
				    pstmt1           =  con.prepareStatement(sql1);
				    pstmt1.setString(1,custId);
				      rs             =  pstmt1.executeQuery();
				  
				      if(rs.next())
				      {
				    	  add += (rs.getString(1)!=null?rs.getString(1):"");
				      	}
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
			    if(pstmt1!=null)
			    	pstmt1.close();
			    if(con != null)
			    	con.close();
			    if(rs !=null)
			    	rs.close();
			}
			return add;
}


  public String getCustomerIDs(String party_Id ,ESupplyGlobalParameters loginBean)throws Exception 
  {
	  PreparedStatement pstmt1;
      Connection con;
      ResultSet rs;
      String accessType;
      StringBuffer terminalQryCustQuote;
      StringBuffer result;
      pstmt1 = null;
      con = null;
      rs = null;
		OperationsImpl      impl    =	null;
		accessType			=	loginBean.getAccessType();
		   terminalQryCustQuote     = new StringBuffer();
		   result     = new StringBuffer();
		   impl = new OperationsImpl();
		  
		if("HO_TERMINAL".equals(accessType)){
			     terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
		}else if("ADMN_TERMINAL".equals(accessType)){
				terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
						            .append( " UNION ")
						            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
						            .append( " UNION ")
						            .append( " SELECT ? term_id FROM DUAL ")
						            .append( " UNION ")
						            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");

		}else if("OPER_TERMINAL".equals(accessType)){
				terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
						            .append( " UNION ")
						            .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
						            .append( " UNION ")
						            .append( " SELECT ? term_id FROM DUAL ")
						            .append( " UNION ")
						            .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ? ")
						            .append(" union ")
						            .append("  select child_terminal_id from fs_fr_terminal_regn  ")
						            .append(" where parent_terminal_id in(select parent_terminal_id from fs_fr_terminal_regn fr1")
						            .append(" where fr1.child_terminal_id= ? ))");
		}
		String  qryforCustomerID  ="SELECT COMPANYNAME,CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE COMPANYNAME LIKE ? AND TERMINALID IN "+terminalQryCustQuote.toString();
		try{
		con = impl.getConnection();
		pstmt1 = con.prepareStatement(qryforCustomerID);
		pstmt1.setString(1, party_Id+"%");
			if(!"HO_TERMINAL".equals(accessType)){
			if("ADMN_TERMINAL".equals(accessType))
	          {
				pstmt1.setString(2,loginBean.getTerminalId());
				pstmt1.setString(3,loginBean.getTerminalId());
				pstmt1.setString(4,loginBean.getTerminalId());
	          }else{
	        	  pstmt1.setString(2,loginBean.getTerminalId());
				  pstmt1.setString(3,loginBean.getTerminalId());
				  pstmt1.setString(4,loginBean.getTerminalId());
				  pstmt1.setString(5,loginBean.getTerminalId());
	          }
		}
		rs = pstmt1.executeQuery();
		while(rs.next())
		{
			result.append("<id>");
			result.append(rs.getString("COMPANYNAME")+";"+rs.getString("CUSTOMERid"));
			result.append("</id>");
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			if(rs != null)
	        {
			try{
	            rs.close();
	           }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
	        }
		if(pstmt1 != null)
        {
			try
			{
            pstmt1.close();
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        }
        if(con != null)
        {
        	try
			{
            con.close();
			 }
            catch(Exception e)
            {
            	e.printStackTrace();
            }
        }
		}
return result.toString();
  }

/*//@ ADDED BY SILPA FOR UPDATE THE CONTACT PERSONS TABLE

public void  getCustomerUpdate(String slno,ESupplyGlobalParameters loginbean,String customer)throws SQLException
{
	PreparedStatement pstmt1 	=	null;
	Connection con				=	null;
	ResultSet	rs				=	null;
	ArrayList<Integer> sl_no              = new ArrayList<Integer>();
	String qryForCheck		=	null;
	int rowCount            = 0; 
	String countQry          = null;
	String qryForSlnoupdate  = null;
	OperationsImpl      impl    =	null;
	
		try{
			
		
			System.out.println("slno--->"+slno);
			System.out.println("customer--->"+customer);
			
		impl = new OperationsImpl();
	qryForCheck="UPDATE  QMS_CUST_CONTACTDTL SET ACTIVE_STATUS='I' WHERE sl_no =? AND CUSTOMERID=? ";
	 System.out.println("qryForCheck--->"+qryForCheck);

	con = impl.getConnection();
	pstmt1 = con.prepareStatement(qryForCheck);
	pstmt1.setString(1, slno);
	pstmt1.setString(2, customer);
	pstmt1.executeUpdate();
	//added by govinD.m on 31-05-11
	pstmt1.clearParameters();
	countQry = 
		"SELECT  SL_NO " +
		"  FROM QMS_CUST_CONTACTDTL " + 
		" WHERE CUSTOMERID = ? " + 
		"   AND ACTIVE_STATUS = 'A' " + 
		"   AND CONTACTPERSON IS NOT NULL " + 
		" ORDER BY SL_NO";
	pstmt1 = con.prepareStatement(countQry);
	pstmt1.setString(1,customer);
	rs = pstmt1.executeQuery();
	while(rs.next())
		sl_no.add(rs.getInt("SL_NO"));
	
		rowCount = sl_no!= null?sl_no.size():0;
	pstmt1.clearParameters();
	rs.close();
	qryForSlnoupdate = 
		"UPDATE QMS_CUST_CONTACTDTL QC SET QC.SL_NO =? WHERE QC.CUSTOMERID = ? AND QC.SL_NO = ? AND QC.ACTIVE_STATUS ='A'";
	pstmt1 = con.prepareStatement(qryForSlnoupdate);
	for(int i=0;i<rowCount;i++){
		pstmt1.setInt(1,i);
		pstmt1.setString(2,customer);
		pstmt1.setInt(3,sl_no.get(i));
		pstmt1.addBatch();
		
	}
	pstmt1.executeBatch();
	
	 
		}catch(Exception e){}
		finally{
			pstmt1.close();
			con.close();
						
}	
 } *///ended	

	
}	

	//ENDED
	
	
	
	


package com.foursoft.esupply.common.dao;

//import com.foursoft.esupply.common.util.Logger;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import com.foursoft.esupply.common.java.ETHAWBAdvVO;
import com.foursoft.esupply.common.java.ETCustomerAdvVO;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.ejb.EJBException;
 
public class ETCustomerAdvDAO extends ETAdvancedLOVMasterDAO
{
  public static String FILE_NAME = "ETCustomerAdvDAO.java";
  private static Logger logger = null;

  public ETCustomerAdvDAO()
  {
    logger  = Logger.getLogger(ETCustomerAdvDAO.class);
  }
  
  public  ArrayList getResult(ETAdvancedLOVMasterVO searchValues)
	{  
	  	   //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getResult ...........");
         logger.info(FILE_NAME+"...............ETCustomerAdvDAO........in getResult ...........");

	   ArrayList details =new ArrayList();
	   String query=null;
	   PreparedStatement pStmt = null;
     Connection connection = null;
     ResultSet rs=null;
	   ETCustomerAdvVO customer =null;
	   String searchString =null;
     String terminalId=null;
     StringBuffer resultQuery= new StringBuffer();
    
       
	   
	   
     try {
	   //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getResult : in try...........");
     logger.info(FILE_NAME+"...............ETCustomerAdvDAO........in getResult : in try...........");
     
			connection = this.getConnection();
	  
            
        
         //Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6844 on 20050512.
			  StringBuffer selectClause		=new StringBuffer("SELECT  DISTINCT CUSTOMER.CUSTOMERID,"+				                                               
		 													  "CUSTOMER.COMPANYNAME ,"+
															  "CUSTOMER.TERMINALID ,"+
															  "ADDRESS.CITY ,"+
															  "ADDRESS.COUNTRYID   ,"+
															  "CUSTOMER.REGISTERED ,"+
															  "CUSTOMER.EISSSN ,"+
															  "CUSTOMER.KNOWNSHIPPER   "); 
 
		   StringBuffer fromClause =new StringBuffer(" FROM FS_FR_CUSTOMERMASTER CUSTOMER,FS_ADDRESS ADDRESS  ");
      StringBuffer whereClause =new StringBuffer();
	   // Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getResult : after query..........."+searchValues.getCustomer());
     // Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getCustomer : CUSTOMER..........."+searchValues.getOperation());
		  //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getREGISTER : REGISTER ..........."+searchValues.getRegister());
            
      
            
          
//Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getREGISTER : Start checking conditions ...........1");
logger.info(FILE_NAME+"...............ETCustomerAdvDAO........in getREGISTER : Start checking conditions ...........1");
      
    if(searchValues.getOperation().equals("Modify") || (searchValues.getOperation()).equals("View")&& (searchValues.getCustomer()!=null && searchValues.getCustomer().equals("T")))
          {
            //Logger.info(FILE_NAME,"........... ....ETCustomerAdvDAO........in getREGISTER : Case 1 Modify or View and Customer = T ...........1");
            logger.info(FILE_NAME+"........... ....ETCustomerAdvDAO........in getREGISTER : Case 1 Modify or View and Customer = T ...........1");
            if(searchValues.getRegister()!=null && searchValues.getRegister().equals("Registered"))
              {
                //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getREGISTER : Case 1 (in if) Modify or View and Customer = T ...........1");
                logger.info(FILE_NAME+"...............ETCustomerAdvDAO........in getREGISTER : Case 1 (in if) Modify or View and Customer = T ...........1");
                //Logger.info(FILE_NAME,"...............INSIDE REGISTER...................");
                terminalId=searchValues.getTerminalID();
                //Logger.info(FILE_NAME,"...............AFTER ADD CONDITION TERMINAL..................."+  terminalId);
                whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMER.CUSTOMERTYPE='Customer' AND CUSTOMER.REGISTERED='R'  AND CUSTOMER.TERMINALID='"+terminalId+"' AND CUSTOMER.REGISTERED_TERMINALID IS NULL "); 
               // Logger.info(FILE_NAME,"...............OUTSIDE REGISTER...................");
              }  
              
              
   
             else
             {
              //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........in getREGISTER : AFTER ADD CONDITION = T ...........1");
              logger.info(FILE_NAME+ "...............ETCustomerAdvDAO........in getREGISTER : AFTER ADD CONDITION = T ...........1");
              terminalId=searchValues.getTerminalID();
              whereClause=new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMER.CUSTOMERTYPE='Customer'  AND CUSTOMER.REGISTERED='U'  AND  CUSTOMER.TERMINALID='"+terminalId+"' AND CUSTOMER.REGISTERED_TERMINALID IS NULL "); 
             // Logger.info(FILE_NAME,"...............UN REGISTER CONDITION...................");
             }
      }   
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5299 on 20050408.	  
     else if(searchValues.getOperation().equals("Group2") ||searchValues.getOperation().equals("Add2")  ||  (searchValues.getOperation()).equals("Modify2") || (searchValues.getOperation()).equals("Delete2")|| (searchValues.getOperation()).equals("Copy2") || (searchValues.getOperation()).equals("Convert2")|| (searchValues.getOperation()).equals("OpenBooking2")  || (searchValues.getOperation()).equals("View2") && (searchValues.getAIRPRQ()!=null) && searchValues.getAIRPRQ().equals("airprq"))     
      {
         terminalId=searchValues.getTerminalID();
         //Logger.info(FILE_NAME,"...............AFTER ADD CONDITION TERMINAL..................."+  terminalId);
         // removed by senthil prabhu for SPETI 5298,5322.
		 //Added Operation= Group2 for SPETI-5301 
         whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=address.ADDRESSID AND  CUSTOMERTYPE = 'Customer'    AND CUSTOMER.TERMINALID = '"+terminalId+"' AND  REGISTERED_TERMINALID is NULL ");
         //Logger.info(FILE_NAME,"...............AFTER ADD CONDITION..................."+whereClause);
         //Logger.info(FILE_NAME,"...............AFTER ADD CONDITION...................");
      }
      
  
    
    
      else if(searchValues.getOperation().equals("Add") && (searchValues.getECustomer()!=null) && searchValues.getECustomer().equals("ECustomer"))
      {
        terminalId=searchValues.getTerminalID();
       // Logger.info(FILE_NAME,"...............BEFORE E ACCCOUNTS ADD CONDITUION ..................."+  terminalId);
        whereClause =new StringBuffer("WHERE CUSTOMERID NOT IN (SELECT CUSTVENDID FROM FS_AC_CVLEDGER WHERE BOOKID=16) AND  TERMINALID='"+terminalId+"' AND CUSTOMER.CUSTOMERADDRESSID=address.ADDRESSID  ");
       // Logger.info(FILE_NAME,"...............AFTER ADD CONDITION..................."+whereClause);  
       // Logger.info(FILE_NAME,"...............AFTER E ACCCOUNTS ADD CONDITUION ...................");
       } 
  
  //Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5314 on 20050408.
      else if(searchValues.getOperation().equals("Group3") ||searchValues.getOperation().equals("Add3") && (searchValues.getSEAPRQ()!=null)  || (searchValues.getOperation()).equals("Modify3") || (searchValues.getOperation()).equals("Delete3")|| (searchValues.getOperation()).equals("Copy3") || (searchValues.getOperation()).equals("Convert3")|| (searchValues.getOperation()).equals("OpenBooking3")  || (searchValues.getOperation()).equals("View3") && (searchValues.getAIRPRQ()!=null)&& searchValues.getSEAPRQ().equals("seaprq"))
      {
        terminalId=searchValues.getTerminalID();
       // Logger.info(FILE_NAME,"...............BEFORE SEAPRQ ADD CONDITUION ..................."+  terminalId);
      whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=address.ADDRESSID AND  CUSTOMERTYPE = 'Customer'    AND CUSTOMER.TERMINALID = '"+terminalId+"' AND  REGISTERED_TERMINALID is NULL ");
       // Logger.info(FILE_NAME,"...............AFTER SEQPRQ ADD CONDITION..................."+whereClause);  
      //  Logger.info(FILE_NAME,"...............AFTER SEAPRQ ADD CONDITUION ...................");
      } 
   
   
   
      else if(searchValues.getOperation().equals("Origin") || searchValues.getOperation().equals("Destination") &&(searchValues.getINVOICEADD()!=null) && searchValues.getINVOICEADD().equals("invoiceadd"))
      {
        // Logger.info(FILE_NAME,"...............BEFORE INVOICE ADD CONDITION..................."+whereClause);  
         terminalId=searchValues.getTerminalID();      
         whereClause =new StringBuffer(" WHERE CUSTOMER.CUSTOMERADDRESSID=address.ADDRESSID AND TERMINALID='"+terminalId+"' AND REGISTERED='R' AND CUSTOMERTYPE='Customer' ");
        // Logger.info(FILE_NAME,"...............AFTER INVOICE ADD CONDITION..................."+whereClause);  
        // Logger.info(FILE_NAME,"...............AFTER INVOICE ADD CONDITION...................");  
      }
  	else if(((searchValues.getOperation()).equals("Modify1") || (searchValues.getOperation()).equals("Delete1")) 
        &&  searchValues.getCorporate()!=null && (searchValues.getCorporate()).equals("Corporate") 
        &&   searchValues.getCustomerregi()!=null && (searchValues.getCustomerregi()).equals("Customerregi"))
       {
          //Logger.info(FILE_NAME,"...............ffffffffffffff........11..........."); 
          logger.info(FILE_NAME+"...............ffffffffffffff........11..........."); 
          terminalId=searchValues.getTerminalID();
          
         whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=address.ADDRESSID  AND CUSTOMER.TERMINALID='"+terminalId+"' ");
         whereClause.append ( "AND CUSTOMERTYPE='Corporate' AND REGISTERED_TERMINALID IS NULL ");
         // Logger.info(FILE_NAME,"...............terminalId.......676676667..........."+searchValues.getTerminalID()); 
      }
  
      else if((searchValues.getOperation()).equals("View1")   && (searchValues.getCorporate()!=null && (searchValues.getCorporate()).equals("Corporate") )
        &&   (searchValues.getCustomerregi()!=null && (searchValues.getCustomerregi()).equals("Customerregi")))
      {
        //Logger.info(FILE_NAME,"...............AFTER VIEW OPERATION..........."); 
        logger.info(FILE_NAME+"...............AFTER VIEW OPERATION..........."); 
      
      whereClause =new StringBuffer ("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE='Corporate' AND REGISTERED_TERMINALID IS NULL ");
          
      } 
       
    
    
      else if((searchValues.getOperation()).equals("mapoperation") && searchValues.getMapping()!=null && searchValues.getMapping().equals("mapping"))
      {
       //Logger.info(FILE_NAME,"..............BEFORE MAPPING OPERATION...........");
       logger.info(FILE_NAME+"..............BEFORE MAPPING OPERATION...........");
        terminalId=searchValues.getTerminalID(); 
         whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE = 'Customer'  AND REGISTERED = 'R'   AND TERMINALID= '"+terminalId+"' ");
     // Logger.info(FILE_NAME,"...............AFTER MAPPING OPERATION...........");
      }
      
      
      else if((searchValues.getOperation()).equals("Dircetawb") && searchValues.getDAWB()!=null && searchValues.getDAWB().equals("dawb"))
      {
       
       terminalId=searchValues.getTerminalID();
        whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE = 'Customer'  AND REGISTERED = 'R'   AND TERMINALID= '"+terminalId+"' ");      
       //Logger.info(FILE_NAME,"........AFTER DIRECT AWB...............");
       logger.info(FILE_NAME+"........AFTER DIRECT AWB...............");
      }
        
       else if((searchValues.getOperation()).equals("DirecetCon") && searchValues.getDirect()!=null && searchValues.getDirect().equals("dcon"))
      {
      terminalId=searchValues.getTerminalID();
      whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE = 'Customer'  AND REGISTERED = 'R'   AND TERMINALID= '"+terminalId+"' ");      
      //Logger.info(FILE_NAME,"........AFTER DIRECT CONSOLE...............");
      logger.info(FILE_NAME+"........AFTER DIRECT CONSOLE...............");
        
      } 
      
        
    else if((searchValues.getOperation()).equals("Delete") && searchValues.getTerminaldel()!=null && searchValues.getTerminaldel().equals("terminaldel"))  
      {
        terminalId=searchValues.getTerminalID();
        whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId+"' AND REGISTERED='R'  AND CUSTOMER.REGISTERED_TERMINALID IS NULL "); 
        //Logger.info(FILE_NAME,"........AFTER TERMINAL DELETE ...............");
        logger.info(FILE_NAME+"........AFTER TERMINAL DELETE ...............");
      }
        else if((searchValues.getOperation()).equals("Upgrade"))
       {
         terminalId=searchValues.getTerminalID();
        whereClause =new StringBuffer("WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID   AND CUSTOMERTYPE='Customer' AND TERMINALID='"+terminalId+"' AND REGISTERED='U'  AND CUSTOMER.REGISTERED_TERMINALID IS NULL "); 
        //Logger.info(FILE_NAME,"........AFTER UPGRADE ...............");
        logger.info(FILE_NAME+"........AFTER UPGRADE ...............");
      }
      
      else if((searchValues.getOperation()).equals("Add5") && searchValues.getcontractadd()!=null && searchValues.getcontractadd().equals("contract"))
      {
		  //added  senthil for speti-5295
      terminalId=searchValues.getTerminalID();
      whereClause =new StringBuffer(" WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID AND REGISTERED = 'R' AND  TERMINALID= '"+terminalId+"'  AND CUSTOMERTYPE = 'Customer'   " );
      }
      else
      {
        whereClause = new StringBuffer(" WHERE CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID ");
      }
       
       
        customer = (ETCustomerAdvVO)searchValues;

		whereClause.append(" AND CUSTOMER.CUSTOMERADDRESSID=ADDRESS.ADDRESSID ");
     if(customer.getCustomerId()!=null && !customer.getCustomerId().equals(""))
	      { 
                    whereClause.append("AND CUSTOMER.CUSTOMERID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),customer.getCustomerId()));
        }
          // Logger.info(FILE_NAME,"........ETCustomerAdvDAO...............getCustomername...........<<"+customer.getCustomername()+">>");
     if(customer.getCustomername()!=null && !customer.getCustomername().equals(""))
	     { 
                       whereClause.append("AND CUSTOMER.COMPANYNAME ");
                      whereClause.append(getSearchString(searchValues.getSearchType(),customer.getCustomername()));
       }
            // Logger.info(FILE_NAME,"........ETCustomerAdvDAO...............getTerminal...........<<"+customer.getTerminal()+">>");
    if(customer.getTerminal()!=null && !customer.getTerminal().equals(""))
		    {
                    whereClause.append("AND CUSTOMER.TERMINALID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),customer.getTerminal()));
        }
            // Logger.info(FILE_NAME,"........ETCustomerAdvDAO...............City...........<<"+customer.getCity()+">>");
			 if(customer.getCity()!=null && !customer.getCity().equals(""))
		        { 
                    whereClause.append("AND ADDRESS.CITY ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),customer.getCity()));
             }
             //Logger.info(FILE_NAME,"........ETCustomerAdvDAO...............Country...........<<"+customer.getCountry()+">>");
			  if(customer.getCountry()!=null && !customer.getCountry().equals(""))
		         { 
                 	 	whereClause.append("AND ADDRESS.COUNTRYID ");
                    whereClause.append(getSearchString(searchValues.getSearchType(),customer.getCountry()));
              }
            // Logger.info(FILE_NAME,"........ETCustomerAdvDAO...............REGI...........<<"+customer.getRegi()+">>");
      if(customer.getRegi()!=null && !customer.getRegi().equals(""))
		         { 
                    whereClause.append("AND CUSTOMER.REGISTERED "); 
			if(customer.getRegi().equals("Both"))
                      whereClause.append("IN ('U','R') "); 
					else
                      whereClause.append(getSearchString(searchValues.getSearchType(),customer.getRegi()));
                 }          
       
                //Logger.info(FILE_NAME,"...............///////////////////........11...........");    
                logger.info(FILE_NAME+"...............///////////////////........11...........");    
             if(!"HO_TERMINAL".equalsIgnoreCase(searchValues.getAccessLevel())){//ADDED BY RK
               whereClause.append(" AND TERMINALID IN(SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID = '"+searchValues.getTerminalID()+"'");
               whereClause.append(" UNION SELECT CHILD_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID=PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID='"+searchValues.getTerminalID()+"'");
               whereClause.append(" UNION SELECT PARENT_TERMINAL_ID TERMINALID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR PARENT_TERMINAL_ID=CHILD_TERMINAL_ID START WITH CHILD_TERMINAL_ID='"+searchValues.getTerminalID()+"')");
             }
             else{
             whereClause.append("AND TERMINALID IN(SELECT DISTINCT terminalid FROM FS_FR_TERMINALMASTER  WHERE oper_admin_flag <> 'H' UNION");
             whereClause.append(" SELECT terminalid term_id  FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H')");
            }
			resultQuery.append(selectClause);
			resultQuery.append(fromClause);
			resultQuery.append(whereClause);
			 //Logger.info(FILE_NAME,"..archValues.getOperation()//////////........11..........."+searchValues.getOperation());    
   
			 
		if(searchValues.getOperation().equals("Add") && (searchValues.getECustomer()!=null) && searchValues.getECustomer().equals("ECustomer"))
    {
        resultQuery.append("ORDER BY  COMPANYNAME DESC");
    }
	  
      if(!((searchValues.getOperation().equals("Add") && (searchValues.getECustomer()!=null) && searchValues.getECustomer().equals("ECustomer"))))
			
		 { 
     
     resultQuery.append(" ORDER BY CUSTOMER.CUSTOMERID DESC ");
		 }
     
      
			
           // Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........11..........."+resultQuery.toString());     	
		 	pStmt = connection.prepareStatement(resultQuery.toString());
            //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........after prepared statement...........");     	
            rs = pStmt.executeQuery();
            //Logger.info(FILE_NAME,"...............ETCustomerAdvDAO........after execution...........");     	
            logger.info(FILE_NAME+"...............ETCustomerAdvDAO........after execution...........");     	

        
			while(rs.next()){
	               	
			
			 	ETCustomerAdvVO customerAttributesArr =new ETCustomerAdvVO();
				customerAttributesArr.setCustomerID(rs.getString(1));
				customerAttributesArr.setCustomername(rs.getString(2));
				customerAttributesArr.setTerminal(rs.getString(3));
				customerAttributesArr.setCity(rs.getString(4));
				customerAttributesArr.setCountry(rs.getString(5));
        customerAttributesArr.setRegi(rs.getString(6));
        customerAttributesArr.setEIN(rs.getString(7));
        customerAttributesArr.setknown(rs.getString(8));
        
				
				details.add(customerAttributesArr);
			}		
			return details;
			}
    
       catch(Exception e){
			e.printStackTrace();
			throw new EJBException(e.getMessage());
		}finally{
			try{
				if(rs!=null){
					rs.close();
				}
				if(pStmt!=null){
					pStmt.close();
				}
				if(connection!=null){
					connection.close();
				}
			}catch(SQLException sq){
				 throw new EJBException(sq.getMessage());
			}

	}
}
  
private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
  
}
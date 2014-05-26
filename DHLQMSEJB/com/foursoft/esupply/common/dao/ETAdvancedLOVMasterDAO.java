package com.foursoft.esupply.common.dao;

import java.util.ArrayList;
import com.foursoft.esupply.common.dao.ETConsoleAdvDAO;
import com.foursoft.esupply.common.dao.ETHAWBAdvDAO;
import com.foursoft.esupply.common.dao.ETHBLAdvDAO;
import com.foursoft.esupply.common.dao.ETMAWBAdvDAO;
import com.foursoft.esupply.common.dao.ETPRQAdvDAO;
import com.foursoft.esupply.common.dao.ETCarrierContractAdvDAO;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


public abstract class ETAdvancedLOVMasterDAO   
{
  private static final String FILE_NAME = "ETAdvancedLOVMasterDAO.java";
  public transient DataSource dataSource = null;
  private static Logger logger = null;


  public ETAdvancedLOVMasterDAO()
  {
        logger  = Logger.getLogger(ETAdvancedLOVMasterDAO.class);
        dataSource = null;
        try 
          {
			InitialContext ic = new InitialContext();
			dataSource = ((DataSource) ic.lookup("java:comp/env/jdbc/DB"));
		    }
		catch (NamingException nmEx) 
{
			logger.error(FILE_NAME+ "ETAdvancedLOVMasterDAO(ETAdvancedLOVMasterDAO())" + nmEx.toString());
			throw new EJBException(nmEx.toString());
		}
  }
  
	//The method will execute the query and return the result set to the caller
	public abstract ArrayList getResult(ETAdvancedLOVMasterVO resultData);

public static ArrayList getData(ETAdvancedLOVMasterVO resultData)throws javax.ejb.EJBException
	{
   ArrayList searchDetails =new ArrayList();
  // Logger.info(FILE_NAME,"-------------------> in getData received entity ["+resultData.getEntity()+"]");
		try
		{  
               String selectQuery=null; 
			if((resultData.getEntity()).equalsIgnoreCase("HAWB"))
			{
				ETHAWBAdvDAO hawbDAO = new ETHAWBAdvDAO();
				searchDetails=hawbDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for HAWB ID");
			}	   
			else if((resultData.getEntity()).equals("HBL"))
			{
			//Logger.info(FILE_NAME,"..........@@@@@@@@@@...........ETAdvancedLOVMasterDAO..000......HBL...........");
			ETHBLAdvDAO hblDAO = new ETHBLAdvDAO();
			searchDetails=hblDAO.getResult(resultData);
			}	
			else if((resultData.getEntity()).equals("MAWB"))
			{
			ETMAWBAdvDAO mawbDAO = new ETMAWBAdvDAO();
			searchDetails=mawbDAO.getResult(resultData);
			}	
			else if((resultData.getEntity()).equals("CarrierContract"))
			{
			//Logger.info(FILE_NAME,"...............ETAdvancedLOVMasterDAO..000.................");
			ETCarrierContractAdvDAO carConDAO = new ETCarrierContractAdvDAO();
			//Logger.info(FILE_NAME,"...............ETAdvancedLOVMasterDAO..111.................");
			searchDetails=carConDAO.getResult(resultData);
			    //  Logger.info(FILE_NAME,"...............ETAdvancedLOVMasterDAO...222................");
			}	           
			else if((resultData.getEntity()).equalsIgnoreCase("manifest"))
			{
				ETManifestAdvDAO manifestDAO = new ETManifestAdvDAO();
				searchDetails=manifestDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for Manifest ID");
			}	    
			else if((resultData.getEntity()).equalsIgnoreCase("ConsignmentNote"))
			{
				ETConsignNoteAdvDAO ConsignNoteDAO = new ETConsignNoteAdvDAO();
				searchDetails=ConsignNoteDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for Consignment Note ID");
			}	    
			/*else if((resultData.getEntity()).equalsIgnoreCase("Invoice"))
			{
				ETInvoiceAdvDAO invoiceDAO = new ETInvoiceAdvDAO();
				searchDetails=invoiceDAO.getResult(resultData);
				Logger.info(FILE_NAME,"-------------------> getData() for Invoice ID");
			}*/	    
			else if((resultData.getEntity()).equalsIgnoreCase("Console"))
				{
            ETConsoleAdvDAO consoleDAO = new ETConsoleAdvDAO();
			      searchDetails=consoleDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for Console ID");
			}	    
			else if((resultData.getEntity()).equalsIgnoreCase("Customer"))
			{
				ETCustomerAdvDAO customerDAO = new ETCustomerAdvDAO();
				searchDetails=customerDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for Console ID");
			}
			else if((resultData.getEntity()).equalsIgnoreCase("PRQ"))
			{
				ETPRQAdvDAO prqDAO = new ETPRQAdvDAO();
				searchDetails=prqDAO.getResult(resultData);
				//Logger.info(FILE_NAME,"-------------------> getData() for PRQ ID");
			  }	   
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME," ",ex.toString(),ex);
      logger.error(FILE_NAME+" "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return searchDetails;
	}
	//This function will take the search type and the string which has to be modified.
   public String getSearchString(String searchType, String str)throws javax.ejb.EJBException
	{
     StringBuffer searchString=new StringBuffer();
		try
		{
			if(searchType!=null && searchType.equals("contains"))
			{
           searchString.append("LIKE '%");
				   searchString.append(str);
				   searchString.append("%'");
			}
			else if(searchType!=null && searchType.equals("startsWith"))
			{
				   searchString.append("LIKE '");
				   searchString.append(str);
				   searchString.append("%'");
                   
			}
			else if(searchType!=null && searchType.equals("endsWith"))
			{
				   searchString.append("LIKE '%");
				   searchString.append(str);
				   searchString.append("'");
                  
			}
			else
			{
				   searchString.append("='");
				   searchString.append(str);
				   searchString.append("'");
                   
			}
			return searchString.toString();
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create ",ex.toString(),ex);
      logger.error(FILE_NAME+"create "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		
	}
    
    public String getDaysControl(String noOfDaysControl, String noOfDays, String entityDateField )throws javax.ejb.EJBException
	{
    if (!("".equals(noOfDays)))
    {
        
  	StringBuffer daysCondition=new StringBuffer();
		try
    {
          if(noOfDaysControl.equals("lessOrEqualTo"))
          {
				daysCondition.append("AND ROUND(SYSDATE-");
				daysCondition.append(entityDateField);
				daysCondition.append(") <= ");
                     daysCondition.append(noOfDays);
          }
			else if(noOfDaysControl.equals("equalTo"))
          {
				daysCondition.append("AND ROUND(SYSDATE-");
				daysCondition.append(entityDateField);
				daysCondition.append(") = ");
                    daysCondition.append(noOfDays); 
                     
          }
            else if(noOfDaysControl.equals("greaterOrEqualTo"))
          {
				daysCondition.append("AND ROUND(SYSDATE-");
				daysCondition.append(entityDateField);
				daysCondition.append(") >= ");
                    daysCondition.append(noOfDays);
          }
      } 
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create ",ex.toString(),ex);
      logger.error(FILE_NAME+"create "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return daysCondition.toString();
    }
    else
    {
         return "";
    }
	}
    public String getDaysControl(String noOfDaysControl, String noOfDays)throws javax.ejb.EJBException
	{
		StringBuffer daysCondition=new StringBuffer();
		try
    {
          if(noOfDaysControl.equals("lessOrEqualTo"))
          {
                     daysCondition.append(" AND SYSDATE- A.CONSOLEDATE  <= ");
                     daysCondition.append(noOfDays);
          }
          if(noOfDaysControl.equals("equalTo"))
          {
                    daysCondition.append(" AND SYSDATE- A.CONSOLEDATE = ");
                    daysCondition.append(noOfDays); 
          }
          if(noOfDaysControl.equals("greaterOrEqualTo"))
          {
                    daysCondition.append(" AND SYSDATE- A.CONSOLEDATE >= ");
                    daysCondition.append(noOfDays);
          }
      } 
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"create ",ex.toString(),ex);
      logger.error(FILE_NAME+"create "+ex.toString()+ex);
			throw new javax.ejb.EJBException(ex.toString());
		}
		return daysCondition.toString();
	}


private  Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}
  
}
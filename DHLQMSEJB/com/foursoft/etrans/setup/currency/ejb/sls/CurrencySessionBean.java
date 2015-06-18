package com.foursoft.etrans.setup.currency.ejb.sls;
import java.sql.CallableStatement;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.naming.InitialContext;

import com.foursoft.etrans.setup.currency.dob.CurrencyConversionDOB;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

public class CurrencySessionBean implements SessionBean 
{
  public static final String FILE_NAME="CurrencySessionBean.java";
	private static javax.sql.DataSource dataSource	=	null;
  private static Logger logger = null;
  
  public CurrencySessionBean()
  {
    logger  = Logger.getLogger(CurrencySessionBean.class);
  }
    
  public void ejbCreate()
  {
  }

  public void ejbActivate()
  {
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
  }

  public void setSessionContext(SessionContext ctx)
  {
  }
  public boolean checkCurrencyUpdation()throws EJBException
  {
    Connection        connection = null;
    Statement         stmt       = null;
		ResultSet         rs         =	null;
    Timestamp                timeStamp   = null;    
    SimpleDateFormat         dateFormatter = null; 
    String                   displayString = null;
    boolean                  flag          = false;
    String                   selectQry   = "SELECT VALUE FROM  QMS_CURRENCY_CONFIGURATION where key='LASTUPDATEDON'";
    try
    {
      connection  = this.getConnection();
      timeStamp   = new Timestamp((new java.util.Date()).getTime());            
      dateFormatter = new SimpleDateFormat();
      dateFormatter.applyPattern("dd-MM-yy");      
      displayString = dateFormatter.format(new Date(timeStamp.getTime()));
      //Logger.info(FILE_NAME,"displayString ::"+displayString);
      stmt = connection.createStatement();
      rs = stmt.executeQuery(selectQry);
      if(rs.next())
			{         
			 flag = displayString.equalsIgnoreCase(rs.getString("VALUE"));
      // Logger.info(FILE_NAME,"displayString ::"+rs.getString("VALUE") +" :: flag ::"+flag);       
      }
    }
    catch(Exception e)
		{
			//Logger.error(FILE_NAME,"CurrencySessionBean[checkCurrencyUpdation()] -> "+e.toString());
      logger.error(FILE_NAME+"CurrencySessionBean[checkCurrencyUpdation()] -> "+e.toString());
      throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
        if(rs!=null)
          rs.close();
        if(stmt!=null)
          stmt.close();
        if(connection!=null)//added by madhu on 13-03-2006
          connection.close();
          
			}
      catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : CurrencySessionBean[checkCurrencyUpdation()]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : CurrencySessionBean[checkCurrencyUpdation()]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : CurrencySessionBean[checkCurrencyUpdation()]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : CurrencySessionBean[checkCurrencyUpdation()]-> "+ex.toString());
        throw new EJBException(ex.toString());
			}
    }
    return flag;
  }
  public void insertCurrencyMasterDetails(ArrayList currConvDOBs) throws  EJBException
  {
    Connection        connection = null;
    PreparedStatement pStmt	     = null;
    PreparedStatement upStmt     = null;
    CallableStatement csmt       = null;
    CurrencyConversionDOB    currConvDOB = null;
    Timestamp                timeStamp   = null;
    SimpleDateFormat         dateFormatter = null; 
    String                   displayString = null;
    String                   insQuery    = " INSERT INTO FS_FR_CURRENCYMASTER(CURRENCY1,CURRENCY2,IEFLAG,BUYRATE,SELLRATE,SPECIFIEDRATE,INVALIDATE,CURRENCY_UPDATED_DATE) VALUES (?,?,?,?,?,?,?,?)";    
    String                   updateQry   = " UPDATE QMS_CURRENCY_CONFIGURATION SET VALUE=? WHERE KEY='LASTUPDATEDON' ";
    int 					 currConvDOBsSize	=0;
    try
    {
      connection  = this.getConnection();      
			pStmt = connection.prepareStatement(insQuery);
      
      timeStamp   = new Timestamp((new java.util.Date()).getTime());
      dateFormatter = new SimpleDateFormat();
      dateFormatter.applyPattern("dd-MM-yy");      
      displayString = dateFormatter.format(new Date(timeStamp.getTime()));
      currConvDOBsSize	=	currConvDOBs.size();
      for(int i=0;i<currConvDOBsSize;i++)
      {
        currConvDOB = (CurrencyConversionDOB)currConvDOBs.get(i);
        //Logger.info(FILE_NAME,"currConvDOB.getConvFrom() ::"+currConvDOB.getConvFrom() +" :: currConvDOB.getConvTo() ::"+currConvDOB.getConvTo());
        pStmt.setString(1,currConvDOB.getConvFrom());
        pStmt.setString(2,currConvDOB.getConvTo());
        pStmt.setString(3,"I");
        pStmt.setDouble(4,currConvDOB.getExchangeBuy());        
        pStmt.setDouble(5,currConvDOB.getExchangeSell());
        pStmt.setDouble(6,currConvDOB.getExchangeSell());
        pStmt.setString(7,"F");
        pStmt.setTimestamp(8,timeStamp);
        pStmt.addBatch();
      }
      //@@Commented by Anusha for DHL-4S-CR in Kewill Time & Expense//
      /*pStmt.executeBatch();
      upStmt = connection.prepareStatement(updateQry);
      upStmt.setString(1,displayString);
      upStmt.executeUpdate();
      
      csmt  = connection.prepareCall("{call currency_archival()}");
      csmt.execute();*/
      //@@Commented by Anusha for DHL-4S-CR in Kewill Time & Expense//
      //@@Added by Anusha for DHL-4S-CR in Kewill Time & Expense//
      int rowseffected[]=pStmt.executeBatch();
      if(rowseffected.length>0)
      {
      upStmt = connection.prepareStatement(updateQry);
      upStmt.setString(1,displayString);
      upStmt.executeUpdate();
      
      csmt  = connection.prepareCall("{call currency_archival()}");
      csmt.execute();
      }
    //@@Added by Anusha for DHL-4S-CR in Kewill Time & Expense//
      //@@Added by subrahmanyam for the Enhancement 146444 on 10/02/09//

    }
    catch(EJBException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)] -> "+sqEx.toString());
      throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
			//Logger.error(FILE_NAME,"CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)] -> "+e.toString());
      logger.error(FILE_NAME+"CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)] -> "+e.toString());
      throw new EJBException(e.toString());
		}
		finally
		{
			try
			{        
				if(csmt!=null)csmt.close();
        if(pStmt!=null)pStmt.close();
        if(upStmt!=null)upStmt.close();//added by madhu on 13-03-2006
        if(connection!=null)//added by madhu on 13-03-2006
          connection.close();
			}
      catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Finally : CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : CurrencySessionBean[insertCurrencyMasterDetails(ArrayList currConvDOBs)]-> "+ex.toString());
        throw new EJBException(ex.toString());
			}
    }
  }
  /**
   * 
   * @return Connection
   * @throws java.sql.SQLException
   */
	private static java.sql.Connection getConnection()throws SQLException
	{
		java.sql.Connection connection	=	null;
		try
		{
			if (dataSource == null)
			{
				InitialContext ic	= new InitialContext();
      dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
			}
			connection = dataSource.getConnection();
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in getConnection() "+e,e);
      logger.error(FILE_NAME+"Exception in getConnection() "+e,e);
			throw new SQLException("Unable to create connection");
		}
		return connection;
	}
  public HashMap getUrlDetails() throws EJBException
  {
    Connection          conn        =   null;
    Statement           stmt        =   null;
    ResultSet           rs          =   null;
    String              urlQry =   " SELECT * FROM QMS_CURRENCY_CONFIGURATION ";
    HashMap             urlDetails = new HashMap();
    try
    {
      conn  = this.getConnection();
      stmt  = conn.createStatement();
      rs    = stmt.executeQuery(urlQry);
      while(rs.next())
      {
        if(rs.getString("KEY")!=null && rs.getString("VALUE")!=null)
        urlDetails.put(rs.getString("KEY"),rs.getString("VALUE"));
      }      
    }
    catch(EJBException ejb)
    {
      //Logger.error(FILE_NAME,"EJBException while fetching getUrlProxyDetails "+ejb);
      logger.error(FILE_NAME+"EJBException while fetching getUrlProxyDetails "+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception while fetching getUrlProxyDetails "+e);
      logger.error(FILE_NAME+"Exception while fetching getUrlProxyDetails "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      try
			{
        if(rs!=null)rs.close();
        if(stmt!=null)stmt.close();
         if(conn!=null)conn.close();//added by madhu on 13-03-2006
      }
      catch(EJBException ejb)
      {
        //Logger.error(FILE_NAME,"EJBException while fetching getUrlProxyDetails "+ejb);
        logger.error(FILE_NAME+"EJBException while fetching getUrlProxyDetails "+ejb);
        ejb.printStackTrace();
        throw new EJBException(ejb);
      }
      catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception while fetching getUrlProxyDetails "+e);
        logger.error(FILE_NAME+"Exception while fetching getUrlProxyDetails "+e);
        e.printStackTrace();
        throw new EJBException(e);
      }
    }
    return urlDetails;
  }
}
package com.qms.operations.quote.ejb.sls;


import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
//import com.foursoft.esupply.common.util.Logger;
import com.qms.operations.quote.dob.QuoteAttachmentDOB;
import java.sql.Blob;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.operations.costing.dob.CostingHDRDOB;
import com.qms.operations.costing.dob.CostingMasterDOB;
import com.qms.operations.quote.dao.QMSQuoteDAO;
import com.qms.operations.quote.dob.QuoteChargeInfo;
import com.qms.operations.quote.dob.QuoteCharges;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteFlagsDOB;
import com.qms.operations.quote.dob.QuoteFreightLegSellRates;
import com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB;
import com.qms.operations.quote.dob.QuoteHeader;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import com.qms.operations.quote.dob.QuoteTiedCustomerInfo;
import com.qms.operations.quote.ejb.bmp.QMSQuoteEntityLocal;
import com.qms.operations.quote.ejb.bmp.QMSQuoteEntityLocalHome;
import com.qms.reports.java.ReportDetailsDOB;
import com.qms.reports.java.UpdatedQuotesFinalDOB;
import com.qms.reports.java.UpdatedQuotesReportDOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import oracle.jdbc.driver.OracleTypes;
import oracle.jdbc.driver.OracleCallableStatement;

public class QMSQuoteSessionBean implements SessionBean 
{
  private InitialContext  initialContext	= null; 
	private DataSource		  dataSource		  = null;
  //private LookUpBean      lookUpBean      =   null;
	private static String   FILE_NAME				= "QMSQuoteSessionBean.java";
  private static Logger logger = null;
  public QMSQuoteSessionBean()
  {
    logger  = Logger.getLogger(QMSQuoteSessionBean.class);
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
  
  //Serialization of BeanObject is to be done here.....//
  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
  {
    //write non-serializable attributes here
    out.defaultWriteObject();
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
  {
    //read non-serializable attributes here
    in.defaultReadObject();
  }
  
  private void getDataSource() throws EJBException
  {
    try
    {
      initialContext = new InitialContext();
      dataSource = (DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
    }
    catch( Exception e )
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
      logger.error(FILE_NAME+"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
    }
  }
  
  private Connection  getConnection() throws EJBException
  {
    Connection con = null;
    try
    {
      if(dataSource== null)
        getDataSource();
      con = dataSource.getConnection();
    }
    catch( Exception e )
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Exception in getConnectin() method of QMSQuoteSessionBean.java: "+e.toString());
      logger.error(FILE_NAME+"Exception in getConnectin() method of QMSQuoteSessionBean.java: "+e.toString());
    }
    return con;
  }
  
  
 
  
  /**
	 * This method helps in inserting the quote master details by calling the entity bean
   * 
	 * @param masterDOB 	an QuoteMasterDOB Object that contains all the Quote Master information
	 * 
	 * @exception EJBException 
	 */
  //public long insertQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
  public String insertQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
  {
    Connection connection = null;
    PreparedStatement psmt  = null;
    ResultSet rs= null;
    QuoteMasterDOB masterDOB  = null;
   
    String location = null;
    String quoteId = null;
//@@ Added by subrahmanyam for the WPBN ISSUE: 146971 ON 18/12/2008    
    int i1=0;
    Integer I1=null;
    String[] quoteIds  = null;
   String[]                   quotes=null;
    int                        quoteLen=0;
    String                     quoteSub=null; 
    String                     quoteSub1=null;
    String                     quoteSub2=null; 
//@@ Ended by subrahmanyam for the WPBN ISSUE: 146971 ON 18/12/2008    
    try
    {
//@@ Added by subrahmanyam for the WPBN ISSUE: 146971 ON 18/12/2008       
      masterDOB   = finalDOB.getMasterDOB();
      if(finalDOB!=null&& finalDOB.getUpdatedReportDOB()!=null)
      {
       quoteId = finalDOB.getUpdatedReportDOB().getQuoteId();
      }
      //location  = getLocation(masterDOB.getCreatedBy());
     location   =   masterDOB.getTerminalId();
//@@ Ended by subrahmanyam for the WPBN ISSUE: 146971 ON 18/12/2008  
     connection  = this.getConnection();
      //psmt  = connection.prepareStatement("SELECT QUOTE_SEQ.NEXTVAL FROM DUAL");
      
     /* if("update".equalsIgnoreCase(finalDOB.getUpdate())&&quoteId!=null&&quoteId.trim().length()>0)
      {
      		quotes= quoteId.split("_");
         if(quotes.length==2)
         {
              quoteId=quoteId+ "_001" ;
         }
         else
         {
               quoteLen = Integer.parseInt(quotes[2]);//2nd part of the quoteId
								quoteLen++;
								if(quoteLen<10)
								{
									 quoteId=quotes[2]+"_00"+new Integer(quoteLen).toString();
									
								}
								else if(quoteLen>=10&&quoteLen<100)
								{
					 				quoteId=quotes[quotes.length-2]+"_0"+new Integer(quoteLen).toString();
										
								}
								else
								{
									quoteId=quotes[quotes.length-2]+"_"+new Integer(quoteLen).toString();
									
								}
	      }

           masterDOB.setQuoteId(quoteId);
      }
    else
      {
     // psmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ID IN( SELECT MAX(ID) FROM QMS_QUOTE_MASTER WHERE TERMINAL_ID=? AND VERSION_NO=1 AND instr(QUOTE_ID,'_',1,2)=0)");
        psmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ID IN( SELECT MAX(ID) FROM QMS_QUOTE_MASTER WHERE TERMINAL_ID=? AND VERSION_NO=1 AND instr(QUOTE_ID,'_',1,2)=0)");
         psmt.setString(1,location);
         rs  = psmt.executeQuery();
         if(rs.next())
            quoteId=rs.getString(1);
            location =location.substring(3);
//@@ Added by subrahmanyam for the WPBN issue: 146971 on 18/12/2008
        if(quoteId!=null)
         {
                  quoteIds=quoteId.split("_");
                  quoteId=quoteIds[1];
                 
                    i1=Integer.parseInt(quoteId);
                    i1++;
                    I1= new Integer(i1);
                    quoteId=location+"_"+I1.toString();
          }
         else
         {
            quoteId =location+"_1";
         }

         masterDOB.setQuoteId(quoteId);
      }*/
//@@ Ended by subrahmanyam for the WPBN ISSUE:146971 on 18/12/2008         
         //masterDOB.setQuoteId(rs.getInt(1));//Commented by subrahmanyam for enhancement #146971 on 03/12/2008
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
     
      psmt  = connection.prepareStatement("SELECT QUOTE_MASTER_SEQ.NEXTVAL FROM DUAL");
      rs  = psmt.executeQuery();
      if(rs.next())
        masterDOB.setUniqueId(rs.getInt(1));
      
     // masterDOB = getShipperConsigneeZones(masterDOB);
      
      finalDOB.setMasterDOB(masterDOB);
     
      //lookUpBean  = new LookUpBean();
      QMSQuoteEntityLocalHome localHome   = (QMSQuoteEntityLocalHome)LookUpBean.getEJBLocalHome("java:comp/env/QMSQuoteEntityBean");
      QMSQuoteEntityLocal localRemote = (QMSQuoteEntityLocal)localHome.create(finalDOB);
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,psmt,rs);
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
    return masterDOB.getQuoteId();
  }
  /**
	 * This method helps in modifying the quote master details by calling the entity bean
   * 
	 * @param masterDOB 	an QuoteMasterDOB Object that contains all the Quote Master information
	 * 
	 * @exception EJBException 
	 */
  //public long modifyQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException  //@@ Commented by subrahmanyam for the Enhacnement #146971 on 03/12/2008
  public String modifyQuoteMasterDtls(QuoteFinalDOB finalDOB) throws	EJBException //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
  {
    Connection connection = null;
    //PreparedStatement psmt  = null; //Commented By RajKumari on 27-10-2008 for Connection Leakages.
    //ResultSet rs= null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
    QuoteMasterDOB   masterDOB  = null;
    QMSQuoteDAO       quoteDAO  = null;
    try
    {
      masterDOB   = finalDOB.getMasterDOB();
      connection  = this.getConnection(); 
      quoteDAO  = new QMSQuoteDAO();
      quoteDAO.store(finalDOB); 
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closeConnection(connection,null,null);//Modified By RajKumari on 27-10-2008 for Connection Leakages.
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
    return masterDOB.getQuoteId();
  }
  /**
	 * This method helps in validating the quote master details 
   * 
	 * @param masterDOB 	an QuoteMasterDOB Object that contains all the Quote Master information
	 * 
	 * @exception EJBException 
	 */
  public StringBuffer validateQuoteMaster(QuoteFinalDOB finalDOB) throws	EJBException
  {
    Connection        connection      = null;
    PreparedStatement psmt            = null;
    //Statement         stmt            = null; //Commented By RajKumari on 27-10-2008 for Connection Leakages.
    ResultSet         rs              = null;
    StringBuffer      errors          = null;
    StringBuffer      terminalQry     = new StringBuffer();
    String            shipperAlpha    = null;
    String            consigneeAlpha  = null;
    String            shipperZipCode  = null;
    String            consigneeZipCode= null;
    String            whereCondition  = "";
    QuoteMasterDOB    masterDOB       = null;  
    QuoteFreightLegSellRates  legDOB  = null;
    ArrayList         legDetails      = null;
    String            currency        = null;
    String            error           = null;
    String           originCountryId  = null;
    String           destCountryId    = null;
    StringBuffer      terminalQrySales     = new StringBuffer();
    StringBuffer      terminalQryCustQuote     = new StringBuffer();//@@Added by subrahmanyam for the CR_Enhancement_167669 on 26/may/09
    PreparedStatement psmtSub            = null;
    ResultSet         rsSub              = null;
	String fromZipCode 	=	"";
	String toZipCode	=	"";
    
    try
    {
     
      masterDOB     =   finalDOB.getMasterDOB();
      legDetails    =   finalDOB.getLegDetails();
      int legSize   =   legDetails.size();
      
      if("H".equalsIgnoreCase(masterDOB.getAccessLevel()))
      {
        terminalQry.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
        terminalQrySales.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");
        terminalQryCustQuote.append( " (SELECT terminalid term_id FROM FS_FR_TERMINALMASTER)");//@@ Added by subrahmanyam for the CR_Enhancement_167669 on 26/May/09

      }
      else
      {
        terminalQry.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
        //added by subrahmanyam for CR_Enhancement_167669 on 26/May/09            
             if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
             {
               terminalQryCustQuote.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id = ?)");
             
             }
             else
             {
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
//ended by subrahmanyam for CR_Enhancement_167669 on 26/May/09

     /*  terminalQrySales.append( "(SELECT parent_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY child_terminal_id = PRIOR parent_terminal_id START WITH child_terminal_id = ?")
                   .append( " UNION ")
                   .append( " SELECT terminalid term_id FROM FS_FR_TERMINALMASTER WHERE oper_admin_flag = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT ? term_id FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT child_terminal_id term_id FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR child_terminal_id = parent_terminal_id START WITH parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id = ?))");*/
      if("O".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {// modified by VLAKSHMI for issue 168093 on 20/04/09
           // terminalQrySales.append(" (select child_terminal_id term_id from fs_fr_terminal_regn ")
          //  .append("where parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= ? ))");
         terminalQrySales.append("(select  parent_terminal_id term_id from fs_fr_terminal_regn ")
              .append(" where child_terminal_id = ? ")
            .append(" union ")
            .append(" select terminalid term_id from fs_fr_terminalmaster where oper_admin_flag = 'H' ")
            .append(" union ")
             .append(" select child_terminal_id term_id from fs_fr_terminal_regn ")
            .append("where parent_terminal_id in(select parent_terminal_id term_id  from fs_fr_terminal_regn where child_terminal_id= ?)) ");
        }else if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
       {
          terminalQrySales.append("( select child_terminal_id term_id from fs_fr_terminal_regn  ")
           .append(" where parent_terminal_id=? union select ? from dual )");
            
       }
      }
          
      errors  = new StringBuffer();
      connection  = this.getConnection();
      int  shipmentMode  = masterDOB.getShipmentMode();
      String  shipment      = "";
      String  shipment1     = "";
      String  shipmModeStr  = "";
      String  whereclause = null;
      String  inClause = null;
      
      if(shipmentMode==4){
        shipment = " AND SHIPMENTMODE IN (4,5,6,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (4,5,6,7) ";
      }else if(shipmentMode==1){
        shipment = " AND SHIPMENTMODE IN (1,3,5,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (1,3,5,7) ";
      }else if(shipmentMode==2){
        shipment = " AND SHIPMENTMODE IN (2,3,6,7) ";
        shipment1 = " AND SHIPMENT_MODE IN (2,3,6,7) ";
      }
      
      //if(masterDOB.getPreQuoteId()>0) //Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      if(masterDOB.getPreQuoteId()!=null) //Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      {
          //@@Commented by subrahmanyam for the CR_Enhancement 167669 on 27/may/09      
          // psmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND TERMINAL_ID IN "+terminalQry.toString());
          //@@Added by subrahmanyam for the CR_Enhancement 167669 on 27/may/09      
            psmt  = connection.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND TERMINAL_ID IN "+terminalQryCustQuote.toString());
          //Ended for 167669        
          //psmt.setLong(1,masterDOB.getPreQuoteId());  //@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
        psmt.setString(1,masterDOB.getPreQuoteId());  //@@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          //@@Commented by subrahmanyam for CR_Enhancement_167669 on 26/May/09       
          /*
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
          psmt.setString(4,masterDOB.getTerminalId());
          */
//@@ added by subrahmanyam for CR_Enhancement_167669 on 26/May/09        
          if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
          }
          else
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
            psmt.setString(5,masterDOB.getTerminalId());
          }
//Ended by subrahmanyam for CR_Enhancement_167669 on 26/May/09       

        }
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Previous Quote Id is Invalid Or Does not exist.<br>");
          
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
        
      }
      if(masterDOB.getCustomerId()!=null && (masterDOB.getCustomerId().trim()).length()!=0)
      {
       // String s ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQry.toString();
      //@@Commented by subrahmanyam for the CR_Enhancement 167669 on 26/may/09
    // String s ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQry.toString();       
    //@@Added by subrahmanyam for the CR_Enhancement 167669 on 26/may/09
     String s ="SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQryCustQuote.toString();
    //For 167669     
        psmt  = connection.prepareStatement(s);
        psmt.setString(1,masterDOB.getCustomerId());
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
         //@@ Commented by subrahmanyam for CR_Enhancement_167669 on 26/May/09     
          /*psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
          psmt.setString(4,masterDOB.getTerminalId());*/
//@@ Added by subrhamanyam for CR_Enhancement_167669 on 26/May/09          
          if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
          }
          else
          {
            psmt.setString(2,masterDOB.getTerminalId());
            psmt.setString(3,masterDOB.getTerminalId());
            psmt.setString(4,masterDOB.getTerminalId());
            psmt.setString(5,masterDOB.getTerminalId());
          }
//@@ ended by subrahmanyam for CR_Enhancement_167669 on 26/May/09     
        }
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Customer is Invalid Or Does not exist.<br>");
          
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
          
      }
      if(masterDOB.getSalesPersonCode()!=null && (masterDOB.getSalesPersonCode().trim()).length()!=0)
      {
       // psmt  = connection.prepareStatement("SELECT EMPID FROM FS_USERMASTER WHERE EMPID = ? AND LOCATIONID IN "+terminalQry.toString());
      psmt  = connection.prepareStatement("SELECT EMPID FROM FS_USERMASTER WHERE EMPID = ? AND LOCATIONID IN "+terminalQrySales.toString());
        
        psmt.setString(1,masterDOB.getSalesPersonCode());
        if("O".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
        }
         if("A".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
        }  
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Sales Person Code is Invalid Or Does not exist.<br>");
        else
        {                
          if(rs!=null)
            rs.close();
          if(psmt!=null)
            psmt.close();
          
          whereclause = "";
          
          String marginId = "";
          
          if(masterDOB.getShipmentMode()==1)
          {
              marginId      = "1";
              shipmModeStr  = "Air ";  
          }
          else if(masterDOB.getShipmentMode()==2)
          {
              marginId = "2,4";
              shipmModeStr  = "Sea ";  
          }
          else
          {
              marginId       = "7,15";
              shipmModeStr  = "Truck ";
          }
        
          if(masterDOB.getServiceLevelId()!=null && (masterDOB.getServiceLevelId().trim()).length()!=0)
                whereclause =  "AND SERVICE_LEVEL = ? ";
           
        /*  psmt  = connection.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
                                              whereclause+" AND MARGIN_ID IN ("+marginId+") AND LEVELNO  = (SELECT LEVEL_NO "+
                                              "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))");*/
            psmt  = connection.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
                                              whereclause+" AND MARGIN_ID IN ("+marginId+") AND LEVELNO  = (SELECT LEVEL_NO "+
                                              "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))");

          if(masterDOB.getServiceLevelId()!=null && (masterDOB.getServiceLevelId().trim()).length()!=0)
          {
            psmt.setString(1,masterDOB.getServiceLevelId());
            psmt.setString(2,masterDOB.getSalesPersonCode());
          }
          else
          {
            psmt.setString(1,masterDOB.getSalesPersonCode());
          }
          
          rs  = psmt.executeQuery();
          if(!rs.next())
          {
            String  serviceLevelStr  = "";
            if(whereclause!=null && whereclause.trim().length()!=0)
                serviceLevelStr = " And Service Level "+masterDOB.getServiceLevelId();
            errors.append("No Margin Limit is Defined for Freight for the Sales Person "+masterDOB.getSalesPersonCode()+", Quote Type "+shipmModeStr+serviceLevelStr+".<br>");
          }
          if(rs!=null)
            rs.close();
          if(psmt!=null)
           psmt.close();
          
         /* psmt  = connection.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO  = (SELECT LEVEL_NO "+
                                              "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
                                              "AND CHARGETYPE=? AND INVALIDATE='F'");*/
          
         psmt  = connection.prepareStatement("SELECT MARGIN_ID FROM QMS_MARGIN_LIMIT_DTL WHERE LEVELNO  = (SELECT LEVEL_NO "+
                                              "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
                                              "AND CHARGETYPE=? AND INVALIDATE='F'");

          psmt.setString(1,masterDOB.getSalesPersonCode());
          psmt.setString(2,"CHARGES");
          rs    = psmt.executeQuery();
          
          if(!rs.next())
          {
            errors.append("No Margin Limit is Defined for Charges for the Sales Person "+masterDOB.getSalesPersonCode()+".<br>");
          }
          if(rs!=null)
            rs.close();
          
          psmt.clearParameters();
          psmt.setString(1,masterDOB.getSalesPersonCode());
          psmt.setString(2,"CARTAGES");
          rs    = psmt.executeQuery();
          
          if(!rs.next())
          {
            errors.append("No Margin Limit is Defined for Cartage Charges for the Sales Person "+masterDOB.getSalesPersonCode()+".<br>");
          }
          
        }
        
        if(rs!=null)
            rs.close();
        if(psmt!=null)
           psmt.close();
        
      }
      //if()
      //psmt  = connection.prepareStatement("SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID = ?");
      if(masterDOB.getIndustryId()!=null && (masterDOB.getIndustryId().trim()).length()!=0)
      {
        psmt  = connection.prepareStatement("SELECT INDUSTRY_ID FROM QMS_INDUSTRY_REG WHERE INVALIDATE = 'F' AND INDUSTRY_ID = ? AND TERMINALID IN "+terminalQry.toString());
        psmt.setString(1,masterDOB.getIndustryId());
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
          psmt.setString(4,masterDOB.getTerminalId());
        }
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Industry Id is Invalid Or Does not exist.<br>");
        
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
        
      }
      if(masterDOB.getCommodityId()!=null && (masterDOB.getCommodityId().trim()).length()!=0)
      {
        psmt  = connection.prepareStatement("SELECT COMODITYID FROM FS_FR_COMODITYMASTER WHERE INVALIDATE = 'F' AND COMODITYID=? AND TERMINALID IN "+terminalQry.toString());
        psmt.setString(1,masterDOB.getCommodityId());
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
          psmt.setString(4,masterDOB.getTerminalId());
        }
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Commodity Id is Invalid Or Does not exist.<br>");
        
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
       }
      if(masterDOB.getServiceLevelId()!=null && (masterDOB.getServiceLevelId().trim()).length()!=0)
      {
        if(shipmentMode==4){
					whereclause = "WHERE SERVICELEVELID =? "+shipment;
				}else 	if(shipmentMode==1){
					whereclause = "WHERE SERVICELEVELID =? "+shipment;
				}else if(shipmentMode==2){
					whereclause = "WHERE SERVICELEVELID =? "+shipment;
				}
        
        psmt  = connection.prepareStatement("SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER "+whereclause+" AND INVALIDATE='F' AND TERMINALID IN "+terminalQry.toString());
        psmt.setString(1,masterDOB.getServiceLevelId());
        if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
        {
          psmt.setString(2,masterDOB.getTerminalId());
          psmt.setString(3,masterDOB.getTerminalId());
          psmt.setString(4,masterDOB.getTerminalId());
        }
        
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Service Level Id is In Correct Or Does not exist Or does not exist for this Shipment Mode.<br>");
        
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
        
      }
      
      //psmt  = connection.prepareStatement("SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment);
      //String s = "SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
String s = "SELECT LOCATIONID,COUNTRYID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
psmt  = connection.prepareStatement(s);
      if(masterDOB.getOriginLocation()!=null && (masterDOB.getOriginLocation().trim()).length()!=0)
      {
        psmt.clearParameters();
        psmt.setString(1,masterDOB.getOriginLocation());
        rs  = psmt.executeQuery();
         if(!rs.next())
          errors.append("Origin Location is Invalid Or Does not exist.<br>");
        else
          originCountryId  = rs.getString("COUNTRYID");
      }
      if(masterDOB.getDestLocation()!=null && (masterDOB.getDestLocation().trim()).length()!=0)
      {
        psmt.clearParameters();
        psmt.setString(1,masterDOB.getDestLocation());
        rs  = psmt.executeQuery();
        if(!rs.next())
          errors.append("Destination Location is Invalid Or Does not exist.<br>");  
        else
          destCountryId  = rs.getString("COUNTRYID");
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
      
      /*psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? "+ 
                                          " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE ");*/
          
      if(masterDOB.getShipperZipCode()!=null && (masterDOB.getShipperZipCode().trim()).length()!=0)
      {
        if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",") > -1)
            errors.append("Please Select a Single Shipper Zone Code Matching the Zip Code "+masterDOB.getShipperZipCode()+"<br>");
            
        shipperZipCode  = masterDOB.getShipperZipCode();
          if(shipperZipCode.indexOf("-")!=-1)
        {
          shipperAlpha    =  shipperZipCode.substring(0,shipperZipCode.indexOf("-"));
          shipperZipCode  =  shipperZipCode.substring((shipperZipCode.indexOf("-")+1),shipperZipCode.trim().length());
        }
       if(shipperAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC = ? ";
     //   else
      //      whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
       // }
            
       /* psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);*/
      
      
      
      
     if("CA".equalsIgnoreCase(originCountryId))
       {
     
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE ,   D.TO_ZIPCODE )");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                  " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                  " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);
       }

        psmt.setString(1,masterDOB.getOriginLocation());
        psmt.setString(2,masterDOB.getShipperMode());
        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
            psmt.setString(3,"~");
        else
            psmt.setString(3,masterDOB.getShipperConsoleType());
        
         if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt.setString(4,shipperZipCode);
          
       }
       else
       {
        psmt.setString(4,shipperZipCode);
        
         if(shipperAlpha != null)
            psmt.setString(5,shipperAlpha);
       }
         rs  = psmt.executeQuery();
        if(!rs.next())
        {
        	//@@ Added & commented by subrahmanyam for the wpbn issue: 195650 on 28:Jan:10
        	//errors.append("Shipper Zip Code is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        	if("CA".equalsIgnoreCase(originCountryId) && shipperZipCode.length()>3)
        	{
        		String shipperZipCodeSub	= shipperZipCode.substring(0,3);
        		shipperZipCodeSub	= 	shipperZipCodeSub+'%';
        		boolean zipFlag		=	false;
        		psmtSub		=	connection.prepareStatement(" SELECT D.ZONE_CODE,D.FROM_ZIPCODE,D.TO_ZIPCODE "+
        											" FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
        											" WHERE D.ZONE_CODE = M.ZONE_CODE    AND M.LOCATION_ID = ? "+
        											" AND M.SHIPMENT_MODE = ?  AND NVL(M.CONSOLE_TYPE, '~') = ? "+
        											" AND D.INVALIDATE = 'F'  AND D.ZONE=? AND D.FROM_ZIPCODE LIKE ? ");
        	
        		psmtSub.setString(1,masterDOB.getOriginLocation());
        		psmtSub.setString(2,masterDOB.getShipperMode());
        	        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
        	        	psmtSub.setString(3,"~");
        	        else
        	        	psmtSub.setString(3,masterDOB.getShipperConsoleType());
        	       
        	        psmtSub.setString(4,masterDOB.getShipperZones().trim());
        	        psmtSub.setString(5,shipperZipCodeSub);
        	        rsSub  = psmtSub.executeQuery();
        	        while(rsSub.next())
        	        {
        	        	fromZipCode	=	rsSub.getString("FROM_ZIPCODE");
        	        	toZipCode	=	rsSub.getString("TO_ZIPCODE");
        	        	
        	        	zipFlag	=	getZipCode(fromZipCode,toZipCode,shipperZipCode);
        	        	if(zipFlag)
        	        		break;
        	        	else{
        	        		fromZipCode	=	"";
        	        		toZipCode	=	"";
        	        	}
        	        }
        	        if(!zipFlag)
        	        	errors.append("Shipper Zip Code is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        	}
        	else
        		errors.append("Shipper Zip Code is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        	//@@ ended by subrahmanyam for the wpbn issue: 195650 on 28:Jan:10
        }
        else if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",")==-1)
        {
          if(rs!=null)
            rs.close();
          if(psmt!=null)
            psmt.close();
          if(rsSub!=null)
        	  rsSub.close();
          if(psmtSub!=null)
        	  psmtSub.close();
         /* psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);*/
         
          if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                         " AND D.INVALIDATE='F' AND  ? IN ( D.FROM_ZIPCODE , D.TO_ZIPCODE)  AND D.ZONE= ?");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
         psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);
       }
    
          psmt.setString(1,masterDOB.getOriginLocation());
          psmt.setString(2,masterDOB.getShipperMode());
          if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
              psmt.setString(3,"~");
          else
              psmt.setString(3,masterDOB.getShipperConsoleType());
        
         if("CA".equalsIgnoreCase(originCountryId))
       {
           psmt.setString(4,shipperZipCode);
           psmt.setString(5,masterDOB.getShipperZones().trim());
       }
       else
       {
          psmt.setString(4,shipperZipCode);
          psmt.setString(5,masterDOB.getShipperZones().trim());
          if(shipperAlpha != null)
            psmt.setString(6,shipperAlpha);
       }  
          rs  = psmt.executeQuery();
          if(!rs.next())
          {
        	  if("CA".equalsIgnoreCase(originCountryId))
        	  {
        		  if("".equalsIgnoreCase(fromZipCode))
        			  errors.append("The Shipper Zip Code "+masterDOB.getShipperZipCode()+" Does Not Match the Shipper Zone Code "+masterDOB.getShipperZones()+".<br>");
        	  }
        	  else
        		  errors.append("The Shipper Zip Code "+masterDOB.getShipperZipCode()+" Does Not Match the Shipper Zone Code "+masterDOB.getShipperZones()+".<br>");
          }
        }
       
          //Added on 0406
           if(whereCondition.trim().length()==0)
    {
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
//@@ added for wpbn 185101 on 01-10-09
        if("CA".equalsIgnoreCase(originCountryId))
        {
          psmt  = connection.prepareStatement("  SELECT COUNT(*) COUNT  FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE = M.ZONE_CODE  AND M.LOCATION_ID = ? AND ? "+
                                              " IN (D.FROM_ZIPCODE,D.TO_ZIPCODE)  AND M.SHIPMENT_MODE = ?  AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;//@@ Modified for wpbn 185101 on 01-10-09

        }
        else
        {
          psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;

        }
		//@@ commented for wpbn 185101 on 01-10-09
      /*
       psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;
           */
                            
          psmt.setString(1,masterDOB.getOriginLocation());
          psmt.setString(2,shipperZipCode);
          psmt.setString(3,masterDOB.getShipperMode());          
          psmt.setString(4,masterDOB.getShipperZones());   
          if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
              psmt.setString(5,"~");
          else 
              psmt.setString(5,masterDOB.getShipperConsoleType());
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
          {
          if(rs.getInt(1)>1)
          {
       errors.append("More Than One Alpha Numeric is there for The Shipper Zip Code "+masterDOB.getShipperZipCode()+"Enter Valid Alpha Numeric.<br>");
          }
          }
      }
        //Added end
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      if(masterDOB.getConsigneeZipCode()!=null && (masterDOB.getConsigneeZipCode().trim()).length()!=0)
      {
        if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",") > -1)
            errors.append("Please Select a Single Consignee Zone Code Matching the Zip Code "+masterDOB.getConsigneeZipCode()+"<br>");
        whereCondition    = "";
        consigneeZipCode  = masterDOB.getConsigneeZipCode();
          if(consigneeZipCode.indexOf("-")!=-1)
        {
          consigneeAlpha   =  consigneeZipCode.substring(0,consigneeZipCode.indexOf("-"));
          consigneeZipCode =  consigneeZipCode.substring((consigneeZipCode.indexOf("-")+1),consigneeZipCode.trim().length());
        }
        // Added 2505
        if(consigneeAlpha != null)
            whereCondition  = " AND D.ALPHANUMERIC = ?";
      //  else
       //     whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
       
            
       /* psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ?  AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);*/
        
        if("CA".equalsIgnoreCase(destCountryId))
       {
     
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                          " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                          " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                          " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE ,  D.TO_ZIPCODE )");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
          psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                  " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                  " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ? "+ 
                                  " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE "+whereCondition);
       }
       psmt.setString(1,masterDOB.getDestLocation());
        psmt.setString(2,masterDOB.getConsigneeMode());
        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            psmt.setString(3,"~");
        else
            psmt.setString(3,masterDOB.getConsigneeConsoleType());
          if("CA".equalsIgnoreCase(destCountryId))
       {
            psmt.setString(4,consigneeZipCode);     
       }
       else
       {
        psmt.setString(4,consigneeZipCode);        
        
        if(consigneeAlpha != null)
            psmt.setString(5,consigneeAlpha);
       }     
        rs  = psmt.executeQuery();
       //@@ Commented by subrahmanyam for the wpbn id: 195650 on 29/Jan/10
        /*if(!rs.next())
            errors.append("Consignee Zip Code is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
        */
        //@@ Added by subrahmanyam for the wpbn id: 195650 on 29/Jan/10        
        if(!rs.next())
        {
          	//@@ Added by subrahmanyam for the wpbn issue: 195650 on 28:Jan:10        	
        	if("CA".equalsIgnoreCase(destCountryId) && consigneeZipCode.length()>3)
        	{
             		String consigneeZipCodeSub	= consigneeZipCode.substring(0,3);
             		consigneeZipCodeSub	= 	consigneeZipCodeSub+'%';
            		boolean zipFlag		=	false;
            		psmtSub		=	connection.prepareStatement(" SELECT D.ZONE_CODE,D.FROM_ZIPCODE,D.TO_ZIPCODE "+
            											" FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
            											" WHERE D.ZONE_CODE = M.ZONE_CODE    AND M.LOCATION_ID = ? "+
            											" AND M.SHIPMENT_MODE = ?  AND NVL(M.CONSOLE_TYPE, '~') = ? "+
            											" AND D.INVALIDATE = 'F'  AND D.ZONE=? AND D.FROM_ZIPCODE LIKE ? ");
            	
            		psmtSub.setString(1,masterDOB.getDestLocation());
            		psmtSub.setString(2,masterDOB.getConsigneeMode());
            	        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            	        	psmtSub.setString(3,"~");
            	        else
            	        	psmtSub.setString(3,masterDOB.getConsigneeConsoleType());
            	       
            	        psmtSub.setString(4,masterDOB.getConsigneeZones().trim());
            	        psmtSub.setString(5,consigneeZipCodeSub);
            	        rsSub  = psmtSub.executeQuery();
            	        while(rsSub.next())
            	        {
            	        	fromZipCode	=	rsSub.getString("FROM_ZIPCODE");
            	        	toZipCode	=	rsSub.getString("TO_ZIPCODE");
            	        	
            	        	zipFlag	=	getZipCode(fromZipCode,toZipCode,consigneeZipCode);
            	        	if(zipFlag)
            	        		break;
            	        	else{
            	        		fromZipCode	=	"";
            	        		toZipCode	=	"";
            	        	}
            	        }
            	        if(!zipFlag)
            	        	errors.append("Consignee Zip Code is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
        	}
        	else
                errors.append("Consignee Zip Code is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
            
        }
        //@@ Ended by subrahmanyam for the wpbn id: 195650 on 29/Jan/10        
        else if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",")==-1)
        {
          if(rs!=null) 
            rs.close();
          if(psmt!=null)
            psmt.close();
          if(rsSub!=null) 
        	  rsSub.close();
            if(psmtSub!=null)
            	psmtSub.close();
            
/*psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);*/
            
        if("CA".equalsIgnoreCase(destCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                         " AND D.INVALIDATE='F' AND  ? IN( D.FROM_ZIPCODE , D.TO_ZIPCODE)  AND D.ZONE= ?");//@@ Modified for wpbn 185101 on 01-10-09
       }
       else
       {
         psmt  = connection.prepareStatement(" SELECT D.ZONE_CODE "+ 
                                              " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                              " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ? AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE,'~')= ?"+ 
                                              " AND D.INVALIDATE='F' AND TO_NUMBER (?) BETWEEN D.FROM_ZIPCODE AND D.TO_ZIPCODE AND D.ZONE= ? "+whereCondition);
       }  
          psmt.setString(1,masterDOB.getDestLocation());
          psmt.setString(2,masterDOB.getConsigneeMode());
          if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            psmt.setString(3,"~");
          else
            psmt.setString(3,masterDOB.getConsigneeConsoleType());    
           if("CA".equalsIgnoreCase(destCountryId))
        {
            psmt.setString(4,consigneeZipCode);
             psmt.setString(5,masterDOB.getConsigneeZones().trim()); 
       }
       else
       {
        psmt.setString(4,consigneeZipCode);
          psmt.setString(5,masterDOB.getConsigneeZones().trim());
          
          if(consigneeAlpha != null)
            psmt.setString(6,consigneeAlpha);
          
       }
          rs  = psmt.executeQuery();
//@@ Commented by subrahmanyam for the wpbn id: 195650 on 29/Jan/10          
          /*
          if(!rs.next())
            errors.append("The Consignee Zip Code "+masterDOB.getConsigneeZipCode()+" Does Not Match the Consignee Zone Code "+masterDOB.getConsigneeZones()+".<br>");
			*/
     //@@ Added by subrahmanyam for the wpbn id: 195650 on 29/Jan/10
          if(!rs.next())
          {
        	  if("CA".equalsIgnoreCase(destCountryId))
        	  {
        		  if("".equalsIgnoreCase(fromZipCode))
        			  errors.append("The Consignee Zip Code "+masterDOB.getConsigneeZipCode()+" Does Not Match the Consignee Zone Code "+masterDOB.getConsigneeZones()+".<br>");
        	  }
        	  else
        		  errors.append("The Consignee Zip Code "+masterDOB.getConsigneeZipCode()+" Does Not Match the Consignee Zone Code "+masterDOB.getConsigneeZones()+".<br>");
          }
     //@@ Ended by subrahmanyam for the wpbn id: 195650 on 29/Jan/10
        }
        //Added on 0406
       if(whereCondition.trim().length()==0){
        if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
 //added for 185101
         if("CA".equalsIgnoreCase(destCountryId))
        {
          psmt  = connection.prepareStatement("  SELECT COUNT(*) COUNT  FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                              " WHERE D.ZONE_CODE = M.ZONE_CODE  AND M.LOCATION_ID = ? AND ? "+
                                              " IN (D.FROM_ZIPCODE,D.TO_ZIPCODE)  AND M.SHIPMENT_MODE = ?  AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;//@@ Modified for wpbn 185101 on 01-10-09

        }
        else
        {
          psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;

        }
		//commented for 185101
      /* psmt  = connection.prepareStatement("SELECT COUNT(*) COUNT FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND D.ZONE IN (?) AND NVL(M.CONSOLE_TYPE, '~') = ? ") ;
         */
                           
          psmt.setString(1,masterDOB.getDestLocation());
          psmt.setString(2,consigneeZipCode);
          psmt.setString(3,masterDOB.getConsigneeMode());          
          psmt.setString(4,masterDOB.getConsigneeZones());   
          if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
              psmt.setString(5,"~");
          else 
              psmt.setString(5,masterDOB.getConsigneeConsoleType());
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
          {
          if(rs.getInt(1)>1)
          {
       errors.append("More Than One Alpha Numeric is there for The Consignee Zip Code "+masterDOB.getConsigneeZipCode()+"Enter Valid Alpha Numeric.<br>");
          }
          }
      }
        //Added end
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
      if(masterDOB.getShipperZones()!=null && (masterDOB.getShipperZones().trim()).length()!=0)
      {
        StringBuffer shZones        =  new StringBuffer();
        int          shZonesLength  =  0;
        String[]     shZoneArray    =  null;
        
        if(masterDOB.getShipperZones().split(",").length > 0)
        {
           shZonesLength  =  masterDOB.getShipperZones().split(",").length;
           shZoneArray    =  masterDOB.getShipperZones().split(",");
            
            for(int k=0;k<shZonesLength;k++)
            {
                if(k==(shZonesLength-1))
                    shZones.append("?");
                else
                    shZones.append("?,");
            }
        }
        
       /* psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                           " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                           " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+ 
                                           " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");*/
         if("CA".equalsIgnoreCase(originCountryId))
       {
          psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                           " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                           " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID=? AND M.SHIPMENT_MODE = ? "+ 
                                           " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");
       }
       else
       {
         psmt = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                       " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                       " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND M.SHIPMENT_MODE = ? "+ 
                                       " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+shZones+")");

       }
        psmt.setString(1,masterDOB.getOriginLocation());
        psmt.setString(2,masterDOB.getShipperMode());
        if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
            psmt.setString(3,"~");
        else
            psmt.setString(3,masterDOB.getShipperConsoleType());
        
        if(shZoneArray!=null && shZoneArray.length>0)
        {
        	int shZoneArrLen	=	shZoneArray.length;
          for(int k=0;k<shZoneArrLen;k++)
              psmt.setString(k+4,shZoneArray[k]);
        }
        
        rs  = psmt.executeQuery();                                
        
         
        while(rs.next())
        {
          if(rs.getInt("NO_ROWS")!=shZonesLength)
            errors.append("Shipper Zone is not defined in the Zone code master or an invalid one for the Origin Location & selected Shipper Mode.<br>");
        }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      if(masterDOB.getConsigneeZones()!=null && (masterDOB.getConsigneeZones().trim()).length()!=0)
      {
        
        StringBuffer consigneeZones       =  new StringBuffer();
        int          consigneeZonesLength =  0;
        String[]     consigneeZoneArray   =  null;
        
        if(masterDOB.getConsigneeZones().split(",").length > 0)
        {
            consigneeZonesLength  =  masterDOB.getConsigneeZones().split(",").length;
            consigneeZoneArray    =  masterDOB.getConsigneeZones().split(",");
            
            for(int k=0;k<consigneeZonesLength;k++)
            {
                if(k==(consigneeZonesLength-1))
                    consigneeZones.append("?");
                else
                    consigneeZones.append("?,");
            }
        }
        
        /*psmt  = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                            " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                            " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ?  AND M.SHIPMENT_MODE = ? "+ 
                                            " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+consigneeZones+")");*/
        
         if("CA".equalsIgnoreCase(destCountryId))
       {
           psmt  = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                            " FROM QMS_ZONE_CODE_MASTER_CA M, QMS_ZONE_CODE_DTL_CA D "+
                                            " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.LOCATION_ID= ?  AND M.SHIPMENT_MODE = ? "+ 
                                            " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+consigneeZones+")");
       }
       else
       {
           psmt  = connection.prepareStatement(" SELECT COUNT(DISTINCT D.ZONE) NO_ROWS "+ 
                                            " FROM QMS_ZONE_CODE_MASTER M, QMS_ZONE_CODE_DTL D "+
                                            " WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION= ?  AND M.SHIPMENT_MODE = ? "+ 
                                            " AND NVL(M.CONSOLE_TYPE,'~')= ? AND D.INVALIDATE='F' AND D.ZONE IN ("+consigneeZones+")");
    
       }
        
        
        psmt.setString(1,masterDOB.getDestLocation());
        psmt.setString(2,masterDOB.getConsigneeMode());
        if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
            psmt.setString(3,"~");
        else
            psmt.setString(3,masterDOB.getConsigneeConsoleType());
        
        if(consigneeZoneArray!=null && consigneeZoneArray.length>0)
        {
        	int consZoneArrLen	=	consigneeZoneArray.length;
          for(int k=0;k<consZoneArrLen;k++)
              psmt.setString(k+4,consigneeZoneArray[k]);
        }
        
        rs  = psmt.executeQuery();        
        
        while(rs.next())
        {
          if(rs.getInt("NO_ROWS")!=consigneeZonesLength)
            errors.append("Consignee Zone is not defined in the Zone code master or an invalid one for the Destination Location & selected Consignee Mode.<br>");
        }
        if(rs!=null)
          rs.close();
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
      if(masterDOB.getShipmentMode()!=2)
      {
       // psmt  = connection.prepareStatement("SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment);
       String s1="SELECT LOCATIONID FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) "+shipment;
       psmt  = connection.prepareStatement(s1);
      
         if(masterDOB.getOriginPort()!=null && (masterDOB.getOriginPort().trim()).length()!=0)
        {
          psmt.setString(1,masterDOB.getOriginPort());
          rs  = psmt.executeQuery();
          if(!rs.next())
            errors.append("Origin Port is not valid.<br>");
        }
        if(masterDOB.getDestPort()!=null && (masterDOB.getDestPort().trim()).length()!=0)
        {
          psmt.setString(1,masterDOB.getDestPort());
          rs  = psmt.executeQuery();
          if(!rs.next())
            errors.append("Destination Port is not valid.<br>");
        }
      }
      else
      {
        psmt  = connection.prepareStatement("SELECT PORTID FROM FS_FRS_PORTMASTER WHERE PORTID = ? AND (INVALIDATE ='F' OR INVALIDATE IS NULL)");
      
        if(masterDOB.getOriginPort()!=null && (masterDOB.getOriginPort().trim()).length()!=0)
        {
          psmt.setString(1,masterDOB.getOriginPort());
          rs  = psmt.executeQuery();
          if(!rs.next())
            errors.append("Origin Port is not valid.<br>");
        }
        if(masterDOB.getDestPort()!=null && (masterDOB.getDestPort().trim()).length()!=0)
        {
          psmt.setString(1,masterDOB.getDestPort());
          rs  = psmt.executeQuery();
          if(!rs.next())
            errors.append("Destination Port is not valid.<br>");
        }
      }
      
      //@@Added by kameswari for the WPBN issue-30908   
      String sql           = "SELECT SERVICELEVELID FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID = ? AND INVALIDATE='F'";
      psmt                 = connection.prepareStatement(sql);
      for(int i=0;i<legSize;i++)
      {
          legDOB  = (QuoteFreightLegSellRates)legDetails.get(i);
          if(legDOB.isSpotRatesFlag())
          {
            currency  = legDOB.getCurrency();
            error     = validateCurrency(currency);
            if(error!=null)
            {
              errors.append(error).append("<br>");
            }
            psmt.setString(1,legDOB.getServiceLevel());
            rs=psmt.executeQuery();
            
            if(!rs.next())
            {
              errors.append("Service Level ID "+legDOB.getServiceLevel()+" is invalid.<br>");
            }
            if(rs!=null)
            rs.close();
          }
      }
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
      
      String uom = null;
      
      for(int i=0;i<legSize;i++)
      {
          legDOB  = (QuoteFreightLegSellRates)legDetails.get(i);
          if(legDOB.isSpotRatesFlag())
          {
          if(legDOB.getShipmentMode()==2 && "LIST".equals(legDOB.getSpotRatesType()))//modified by phani sekhar for wpbn 178179  on 20090730
          {
           continue;
          }
          else 
          {
          uom = legDOB.getUom();
            
            if("KG".equalsIgnoreCase(uom) || "CBM".equalsIgnoreCase(uom))
            {
              sql   = "SELECT KG_PER_M3 ||' Kg/M3'  FROM QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F' AND KG_PER_M3=? ";            
            }
            else if("LB".equalsIgnoreCase(uom) || "CFT".equalsIgnoreCase(uom))
            {
              sql   = "SELECT LB_PER_F3 ||' Lb/Ft3'  FROM  QMS_DENSITY_GROUP_CODE WHERE  INVALIDATE='F' AND LB_PER_F3=? ";
            }
            
            psmt                 = connection.prepareStatement(sql); 
            psmt.setString(1,legDOB.getDensityRatio());
            rs                   = psmt.executeQuery();  
            if(!rs.next())
            {
              errors.append("Density Ratio "+legDOB.getDensityRatio()+" is invalid.<br>");
            }
            if(rs!=null)
              rs.close();
            if(psmt!=null)
              psmt.close();
          }//ends 178179
          }
      }
        //@@WPBN-30908
            
      if(masterDOB.getChargeGroupIds()!=null && (masterDOB.getChargeGroupIds()).length!=0)
      {
        psmt  = connection.prepareStatement("SELECT DISTINCT CHARGEGROUP_ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID =? AND INVALIDATE='F' AND INACTIVATE='N' "+shipment1+" AND TERMINALID IN "+terminalQry.toString()+" ORDER BY CHARGEGROUP_ID");//@@ Modified by subrahmanyam for the pbn id: 210495 on 12-Jul-10
        for(int i=0;i<masterDOB.getChargeGroupIds().length;i++)
        {
          if(masterDOB.getChargeGroupIds()[i]!=null && (masterDOB.getChargeGroupIds()[i]).trim().length()!=0)
          {
            psmt.setString(1,masterDOB.getChargeGroupIds()[i]);
            if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
            {
              psmt.setString(2,masterDOB.getTerminalId());
              psmt.setString(3,masterDOB.getTerminalId());
              psmt.setString(4,masterDOB.getTerminalId());
            }
            rs  = psmt.executeQuery();
            if(!rs.next())
              errors.append("Charge Group Id "+masterDOB.getChargeGroupIds()[i]+" is Invalid Or Does not exist.<br>");
            
            if(rs!=null)
              rs.close();
          }
        }
      
        if(psmt!=null)
          psmt.close();
        
      }
      if(masterDOB.getContentOnQuote()!=null && masterDOB.getContentOnQuote().length!=0)
      {
        psmt  = connection.prepareStatement("SELECT CONTENTID FROM  QMS_CONTENTDTLS WHERE CONTENTID=? "+shipment+
                                            " AND INVALIDATE='F' AND FLAG ='F'  AND ACTIVEINACTIVE='N' AND TEMINALID IN "+terminalQry.toString());
                                            
        for(int i=0;i<masterDOB.getContentOnQuote().length;i++)
        {
          if(masterDOB.getContentOnQuote()[i]!=null && masterDOB.getContentOnQuote()[i].trim().length()!=0)
          {
            psmt.setString(1,masterDOB.getContentOnQuote()[i]);
            if(!"H".equalsIgnoreCase(masterDOB.getAccessLevel()))
            {
              psmt.setString(2,masterDOB.getTerminalId());
              psmt.setString(3,masterDOB.getTerminalId());
              psmt.setString(4,masterDOB.getTerminalId());
            }
            rs = psmt.executeQuery();
            if(!rs.next())
              errors.append("Content Id "+masterDOB.getContentOnQuote()[i]+" is Invalid or Does Not Exist for the selected Quote Type <BR>");
            
             if(rs!=null)
              rs.close();
          }
        }
        
        if(psmt!=null)
          psmt.close();
      }
      
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{
				ConnectionUtil.closePreparedStatement(psmtSub,rsSub);// Added by Dilip for PMD Correction on 22/09/2015	
				ConnectionUtil.closeConnection(connection,psmt,rs);
       // ConnectionUtil.closeConnection(null,stmt,null); //Commented By RajKumari on 27-10-2008 for Connection Leakages.
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteSessionBean[insertQuoteMasterDtls(masterDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
    return errors;
  }
  
  public ArrayList  getCostingQuoteIds(QuoteMasterDOB masterDOB,String searchString) throws EJBException
  {
    Connection                 connection                 =         null;
    PreparedStatement          pStmt                      =         null;
    ResultSet                  rs                         =         null;
    String                     sql                        =         null;
    String                     whereCondition             =         "";
    String                     customerId                 =         null;
    String                     operation                  =         null;
    String                     accessLevel                =         null;
    String                     terminalId                 =         null;
    String                     origin                     =         null;
    String                     destination                =         null;
    ArrayList                  quoteList                  =         null;
    
    String                     originFilter               =         "";
    String                     destFilter                 =         "";
    String                     subQuery                   =         "";
    
    try
    {
      customerId                =         masterDOB.getCustomerId();
     // empId                     =         masterDOB.getUserId();
     // buyRatesPermission        =         masterDOB.getBuyRatesPermission();
      operation                 =         masterDOB.getOperation();
      accessLevel               =         masterDOB.getAccessLevel();
      terminalId                =         masterDOB.getTerminalId();
      origin                    =         masterDOB.getOriginLocation();
      destination               =         masterDOB.getDestLocation();
    
      
      
      if("H".equalsIgnoreCase(accessLevel))
        subQuery  = "(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)";
      else
      {
        subQuery  = "(SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = ? UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID=? UNION "+
                    "SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID =? "+
                    "UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')";
      }
       
      if(customerId!=null && customerId.trim().length()!=0)
            whereCondition      =         " CUSTOMER_ID =? AND ";
      if(origin!=null && origin.trim().length()!=0)
            originFilter        =         " ORIGIN_LOCATION='"+origin+"' AND ";
      if(destination!=null && destination.trim().length()!=0)
            destFilter          =         " DEST_LOCATION='"+destination+"' AND ";
      
      sql                       =         "SELECT QUOTE_ID FROM  QMS_QUOTE_MASTER WHERE "+
                                          whereCondition+originFilter+destFilter+
                                          //" QUOTE_ID LIKE '"+searchString+"%'  AND (ACTIVE_FLAG = 'A' OR ACTIVE_FLAG IS NULL) AND COMPLETE_FLAG <>'I' AND QUOTE_STATUS='ACC' "+
                                          " QUOTE_ID LIKE '"+searchString+"%'  AND (ACTIVE_FLAG = 'A' OR ACTIVE_FLAG IS NULL) AND COMPLETE_FLAG <>'I' "+
                                          " AND TERMINAL_ID IN "+subQuery+
                                          " ORDER BY QUOTE_ID";
                                          
      connection                =         this.getConnection();
      
      pStmt                     =         connection.prepareStatement(sql);
      
      if(customerId!=null && customerId.trim().length()!=0)
      {
        pStmt.setString(1,customerId);
        if(!"H".equalsIgnoreCase(accessLevel))
        {
            pStmt.setString(2,terminalId);
            pStmt.setString(3,terminalId);
            pStmt.setString(4,terminalId);
        }
      }
      else
      {
        if(!"H".equalsIgnoreCase(accessLevel))
        {
            pStmt.setString(1,terminalId);
            pStmt.setString(2,terminalId);
            pStmt.setString(3,terminalId);
        }
      }
      
      rs                        =         pStmt.executeQuery();
      quoteList                 =         new ArrayList();
      
      while(rs.next())
      {
          quoteList.add(rs.getString("QUOTE_ID"));      
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"getCostingQuoteIds "+e.toString());
      logger.error(FILE_NAME+"getCostingQuoteIds "+e.toString());
      return null;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return quoteList;
  }
  
  
  
  public ArrayList  getQuoteIds(QuoteMasterDOB masterDOB,String searchString) throws EJBException
  {
    Connection                 connection                 =         null;
    PreparedStatement          pStmt                      =         null;
    ResultSet                  rs                         =         null;
    String                     sql                        =         null;
    String                     whereCondition             =         "";
    String                     customerId                 =         null;
    String                     empId                      =         null;
    String                     buyRatesPermission         =         null;
    String                     operation                  =         null;
    String                     accessLevel                =         null;
    String                     terminalId                 =         null;
    String                     origin                     =         null;
    String                     destination                =         null;
    int                        shipmentMode               =         0;
    ArrayList                  quoteList                  =         null;
    
    String                     originFilter               =         "";
    String                     destFilter                 =         "";
    String                     shipModeQry                =         "";
    String                     subQuery                   =         "";
    String                     updatedCondition           =         "";//@@Updated Quotes are locked and cannot be modified.
    
    try
    {
      customerId                =         masterDOB.getCustomerId();
      empId                     =         masterDOB.getEmpId();
      buyRatesPermission        =         masterDOB.getBuyRatesPermission();
      operation                 =         masterDOB.getOperation();
      accessLevel               =         masterDOB.getAccessLevel();
      terminalId                =         masterDOB.getTerminalId();
      origin                    =         masterDOB.getOriginLocation();
      destination               =         masterDOB.getDestLocation();
      shipmentMode              =         masterDOB.getShipmentMode();
    
      
      if("Modify".equalsIgnoreCase(operation))
          updatedCondition      =         " AND QUOTE_STATUS NOT IN ('ACC','NAC') AND ID NOT IN(SELECT DISTINCT QUOTE_ID FROM QMS_QUOTES_UPDATED WHERE CONFIRM_FLAG IS NULL) ";
      
      if("H".equalsIgnoreCase(accessLevel))
        subQuery  = "(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)";
      else
      {
        if("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
        {
          subQuery  = "(SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = ? UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID=? )";
        }
        else
        {
          subQuery  = "(SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID "+
                    "START WITH PARENT_TERMINAL_ID = ? UNION SELECT TERMINALID "+
                    "FROM FS_FR_TERMINALMASTER WHERE TERMINALID=? UNION "+
                    "SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN "+
                    "CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID =? "+
                    "UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')";
        }
      }
      
      if(customerId!=null && customerId.trim().length()!=0)
            whereCondition      =         "CUSTOMER_ID =? AND ";
      if(origin!=null && origin.trim().length()!=0)
            originFilter        =         " ORIGIN_LOCATION='"+origin+"' AND ";
      if(destination!=null && destination.trim().length()!=0)
            destFilter          =         " DEST_LOCATION='"+destination+"' AND ";
      if(shipmentMode !=0)
            shipModeQry         =          " AND QUOTE_ID IN (SELECT DISTINCT QUOTE_ID FROM FS_RT_PLAN A,FS_RT_LEG B WHERE A.QUOTE_ID= QM.QUOTE_ID AND "+
                                           " A.RT_PLAN_ID=B.RT_PLAN_ID AND B.SHPMNT_MODE= "+shipmentMode+") ";
            
      
      sql                       =         "SELECT QUOTE_ID,BASIS,ESCALATION_FLAG,ESCALATED_TO,COMPLETE_FLAG FROM  QMS_QUOTE_MASTER QM WHERE "+
                                          whereCondition+originFilter+destFilter+
                                          "QM.QUOTE_ID LIKE '"+searchString+"%'  AND (ACTIVE_FLAG = 'A' OR ACTIVE_FLAG IS NULL) "+  
                                          " AND TERMINAL_ID IN "+subQuery+updatedCondition+shipModeQry+
                                          "ORDER BY QUOTE_ID";
      connection                =         this.getConnection();
      pStmt                     =         connection.prepareStatement(sql);
      
      if(customerId!=null && customerId.trim().length()!=0)
      {
        pStmt.setString(1,customerId);
        if(!"H".equalsIgnoreCase(accessLevel))
        {
          if("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
          {
            pStmt.setString(2,terminalId);
            pStmt.setString(3,terminalId);
          }
          else
          {
            pStmt.setString(2,terminalId);
            pStmt.setString(3,terminalId);
            pStmt.setString(4,terminalId);
          }
        }
      }
      else
      {
        if(!"H".equalsIgnoreCase(accessLevel))
        {
          if("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation))
          {
            pStmt.setString(1,terminalId);
            pStmt.setString(2,terminalId);
          }
          else
          {
            pStmt.setString(1,terminalId);
            pStmt.setString(2,terminalId);
            pStmt.setString(3,terminalId);
          }
        }
      }
      
      rs                        =         pStmt.executeQuery();
      quoteList                 =         new ArrayList();
      
      while(rs.next())
      {
        if("Modify".equalsIgnoreCase(operation))
        {
          if(
          ("Y".equalsIgnoreCase(rs.getString("ESCALATION_FLAG")) && empId.equalsIgnoreCase((rs.getString("ESCALATED_TO")))) 
          || ("N".equalsIgnoreCase(rs.getString("ESCALATION_FLAG")) && !("N".equalsIgnoreCase(buyRatesPermission) && "Y".equalsIgnoreCase(rs.getString("BASIS"))))
          )
          {
              quoteList.add(rs.getString("QUOTE_ID"));
          }
        }
        else if("View".equalsIgnoreCase(operation))
        {
          if(!"I".equalsIgnoreCase(rs.getString("COMPLETE_FLAG")))
              quoteList.add(rs.getString("QUOTE_ID"));
        }
        else 
          quoteList.add(rs.getString("QUOTE_ID"));
          
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"getQuoteIds "+e.toString());
      logger.error(FILE_NAME+"getQuoteIds "+e.toString());
      quoteList =  null;
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,rs);
    }
    return quoteList;
  }
  /*
   * This method is for getting QuoteIds into QuoteId EnterId Screen
   * Param0  customerId
   * Param1  searchString of the QuoteId
   * Param2  originLoc
   * Param3  destLocation
   * Param4  serviceLevel
   */
  public ArrayList  getQuoteIds(String customerId,String searchString,String originLoc,String destLocation,String serviceLevel,String terminalid,String searchStr) throws EJBException
  {
    Connection                 connection                 =         null;
    CallableStatement          cStmt                      =         null;
    ResultSet                  rs                         =         null;    
    ArrayList                  quoteList                  =         null;
    try
    {      
      connection                =         this.getConnection();
      cStmt                     =         connection.prepareCall("{ call QMS_QUOTE_PACK.QUOTEID_LOV(?,?,?,?,?,?)}");
      cStmt.setString(1,customerId);      
      cStmt.setString(2,originLoc);
      cStmt.setString(3,destLocation);      
      cStmt.setString(4,terminalid);
      cStmt.setString(5,searchStr);
      cStmt.registerOutParameter(6,OracleTypes.CURSOR);
      
      cStmt.execute();
      rs  = (ResultSet) cStmt.getObject(6);
      
      quoteList                 =         new ArrayList();
      while(rs.next())
      {
        quoteList.add(rs.getString("QUOTE_ID"));
      }
    }    
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"getQuoteIds6) "+e.toString());
      logger.error(FILE_NAME+"getQuoteIds6) "+e.toString());
      //Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());    
    }
    finally
    {
      try
      {
        if(rs!=null)rs.close();
        if(cStmt!=null)cStmt.close();
      }
      catch(SQLException se)
      {
        se.printStackTrace();
      //Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+se.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+se.toString());
			throw new EJBException(se.toString());      
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
        logger.error(FILE_NAME+"QMSQuoteSessionBean[validateQuoteMaster(masterDOB)] -> "+e.toString());
        throw new EJBException(e.toString());        
      }
      ConnectionUtil.closeConnection(connection);      
    }
    return quoteList;
  }

  /*public QuoteFinalDOB getQuoteTiedCustomerInfo(QuoteFinalDOB finalDOB) throws EJBException
  {
    QMSQuoteDAO                 quoteDAO      =   null;
    QuoteMasterDOB              masterDOB     =   null;
    ArrayList                   legDetails    =   null;
    int                         noOfLegs      =   0;
    QuoteFreightLegSellRates    legDOB        =   null;
    QuoteFreightLegSellRates    legFrtDOB     =   null;
    String                      origin        =   null;
    String                      destination   =   null;
    ArrayList                   tiedCustDetails = null;
    
    try
    {
       quoteDAO    = new QMSQuoteDAO();
       tiedCustDetails =  new ArrayList(); 
       masterDOB   = finalDOB.getMasterDOB();
       legDetails  = finalDOB.getLegDetails();
       
       if(masterDOB.getShipmentMode()==2)
       {
         origin       = masterDOB.getOriginPort();
         destination  = masterDOB.getDestPort();
       }
       else
       {
         origin       = masterDOB.getOriginLocation();
         destination  = masterDOB.getDestLocation();
       }
       
       if(legDetails!=null)
          noOfLegs    = legDetails.size();
      
        for(int i=0;i<noOfLegs;i++)
        {
          legDOB    = (QuoteFreightLegSellRates)legDetails.get(i);
          //legFrtDOB = quoteDAO.getTiedCustomerFreightInfo(masterDOB.getCustomerId(),legDOB.getOrigin(),legDOB.getDestination(),masterDOB.getTerminalId(),""+legDOB.getShipmentMode(),masterDOB.getBuyRatesPermission(),origin,destination,masterDOB.getOperation(),masterDOB.getQuoteId());
          
          tiedCustDetails.add(legFrtDOB);
        }
        finalDOB.setTiedCustomerInfoFreightList(tiedCustDetails);
             
    }
    catch(EJBException ejb)
    {
      Logger.error(FILE_NAME,"EJB Exception in getQuoteCustomerTiedInfo "+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      Logger.error(FILE_NAME,"Exception in getQuoteCustomerTiedInfo "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    return finalDOB;
  }*/
  
  /**
	 * This method helps in getting the sell rate details of 
   * all the legs that are involved if a route is specifies
   * or the sell rates between a particular origin and destination
   * 
	 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
	 * 
	 * @exception EJBException 
	 */
  public QuoteFinalDOB  getFreightSellRates(QuoteFinalDOB finalDOB) throws EJBException
  {
    ArrayList                 freightRates            = null;//this is used to maintain info of all the legs
    QuoteFreightLegSellRates  legRateDetails          = null;//to maintain the info of each leg
    QuoteMasterDOB            masterDOB               = null;
    QMSQuoteDAO               quoteDAO                = null;
    QuoteFreightLegSellRates  legDOB                  = null;
    QuoteFreightLegSellRates  tiedCustLegDOB          = null;
    String                    serviceLevel            = null;
    ArrayList                 legDetails              = null;
    ArrayList                 frtTiedCustInfoList     = null;
    int                       legSize                 = 0;
        
    try
    {
     if(finalDOB!=null)//@@Added by Kameswari for the WPBN issue-141961 on 21/10/2008
     {
      legDetails    = finalDOB.getLegDetails();
     }
      if(legDetails!=null)
        legSize       = legDetails.size();
        
     if(finalDOB!=null)//@@Added by Kameswari for the WPBN issue-141961 on 21/10/2008
     {
        masterDOB           = finalDOB.getMasterDOB();
        frtTiedCustInfoList = finalDOB.getTiedCustomerInfoFreightList();
     }
      quoteDAO      = new QMSQuoteDAO();
      
      serviceLevel            = masterDOB.getServiceLevelId()!=null?masterDOB.getServiceLevelId():"";
      
      freightRates  = new ArrayList();
  
      for(int i=0;i<legSize;i++)
      {
        legDOB          = (QuoteFreightLegSellRates)legDetails.get(i);
        if(frtTiedCustInfoList!=null)
          tiedCustLegDOB  = (QuoteFreightLegSellRates)frtTiedCustInfoList.get(i);
        
        //Logger.info(FILE_NAME,"legDOB.getOrigin()::"+legDOB.getOrigin());
        //Logger.info(FILE_NAME,"legDOB.getDestination()::"+legDOB.getDestination());
        //Logger.info(FILE_NAME,"legDOB.getShipmentMode()::"+legDOB.getShipmentMode());
        
        if(!legDOB.isSpotRatesFlag() && tiedCustLegDOB==null)
          legRateDetails  = quoteDAO.getFrtLegSellRates(legDOB.getOrigin(), legDOB.getDestination(), serviceLevel, legDOB.getShipmentMode(), masterDOB.getTerminalId(),masterDOB.getBuyRatesPermission(),masterDOB.getOperation(),masterDOB.getQuoteId(),masterDOB.getWeightBreak()); 
          // Method Signature was Modified by Kishore For Weight Break in Single Quote 
        if(legRateDetails!=null)
        {
          legDOB.setRates(legRateDetails.getRates());
          legDOB.setSlabWeightBreaks(legRateDetails.getSlabWeightBreaks());
          /// legDOB.setFlatWeightBreaks(legRateDetails.getFlatWeightBreaks());
          legDOB.setListWeightBreaks(legRateDetails.getListWeightBreaks());
          legDetails.remove(i);
          legDetails.add(i,legDOB);
        }
      }
   
      //end
      
      finalDOB.setLegDetails(legDetails);
    }
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
      legRateDetails  = null;
		}
    return finalDOB;
  }
  
  /**
	 * This method helps in getting the header and charhes information that are to be displayed on the quote 
   * 
	 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
	 * 
	 * @exception EJBException 
	 */
  public QuoteFinalDOB  getChargesAndHeader(QuoteFinalDOB finalDOB) throws EJBException
  {
    ArrayList                 chargesList   = null;
    QuoteMasterDOB            masterDOB     = finalDOB.getMasterDOB();
    QuoteHeader               headerDOB     = null;
    QuoteCharges              chargesDOB    = null;
        
    try
    {
      finalDOB = getQuoteHeader(finalDOB);
     
      //finalDOB.setHeaderDOB(headerDOB);\
      if("View".equalsIgnoreCase(masterDOB.getOperation()))
      {
         finalDOB  = getRatesChargesInfo(""+masterDOB.getQuoteId(),finalDOB,null);
      }
      else
        finalDOB  = getCharges(finalDOB);
    }
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getChargesAndHeader(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getChargesAndHeader(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
		}
    return finalDOB;
  }
  
  /**
	 * This method helps in getting the header information that is to be displayed on the quote 
   * 
	 * @param finalDOB 	an QuoteFinalDOB Object that contains all the Quote information
	 * 
	 * @exception EJBException 
	 */
   public QuoteFinalDOB getQuoteHeader(QuoteFinalDOB finalDOB) throws EJBException
  {
    QuoteMasterDOB            masterDOB               = finalDOB.getMasterDOB();
    QuoteHeader               headerDOB               = null;
    ArrayList                 legRateDetails          = null;
    QuoteFreightRSRCSRDOB     ratesDOB                = null;
    Connection                connection              = null;
    PreparedStatement         psmt                    = null;
    PreparedStatement         psmt1                   = null;
    ResultSet                 rs                      = null;
    String                    query                   = null;
    String                    locationQry             = null;
    ArrayList                 toEmailIds              = null;
    ArrayList                 toFaxIds                = null;
    String[]                  contactPersonsEmails    = null;
    String[]                  contactPersonsFax       = null;
     //ResultSet                 rsdtls                      = null;    //Commented By RajKumari on 27-10-2008 for Connection Leakages.
     String                    department            = "";
     String                     description          = "";
//@@ Added by subrahmanyam for Enhancement 167668 on 29/04/09     
     String                     custAddress1         =  null;
     String                     custAddress2         =  null;
     String                     custAddress3         =  null;
     String                     custAddrQry          =  null;
     String                     queryNoCustAddr      =  null;
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09     
    try
    {
    
      connection  = this.getConnection();
      headerDOB   = new QuoteHeader();
      StringBuffer sBuffer   = new StringBuffer("");
      
     // locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
      // locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
       locationQry = "SELECT CM.COUNTRYID,COUNTRYNAME,LOCATIONNAME FROM FS_FR_LOCATIONMASTER LM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID = LM.COUNTRYID AND LOCATIONID = ?";
      psmt1  = connection.prepareStatement(locationQry);
      
      if(masterDOB.getOriginLocation()!=null)
      {
        psmt1.setString(1,masterDOB.getOriginLocation());
        rs    = psmt1.executeQuery();
        if(rs.next())
        {
          headerDOB.setOriginCountryId(rs.getString("COUNTRYID"));
          headerDOB.setOriginLocName(rs.getString("LOCATIONNAME"));
          headerDOB.setOriginCountry(rs.getString("COUNTRYNAME"));
        }
      }
      
      psmt1.clearParameters();
      
      if(masterDOB.getDestLocation()!=null)
      {
        psmt1.setString(1,masterDOB.getDestLocation());
        rs    = psmt1.executeQuery();
        if(rs.next())
        {
          headerDOB.setDestinationCountryId(rs.getString("COUNTRYID"));
          headerDOB.setDestLocName(rs.getString("LOCATIONNAME"));
          headerDOB.setDestinationCountry(rs.getString("COUNTRYNAME"));
        }
      }
      
      if(rs!=null)
        rs.close();
      /*if(psmt!=null)
        psmt.close();*/
      //query = null;
      query = "SELECT CM.COMPANYNAME,CM.OPERATIONS_EMAILID,AD.FAX,AD.COUNTRYID,CM.PAYMENTTERMS FROM FS_FR_CUSTOMERMASTER CM,FS_ADDRESS AD"+
              " WHERE CM.CUSTOMERID=? AND CM.CUSTOMERADDRESSID=AD.ADDRESSID";


      psmt  = connection.prepareStatement(query);
      psmt.setString(1,masterDOB.getCustomerId());
      //psmt.setString(2,masterDOB.getTerminalId());
      rs    = psmt.executeQuery();
     
      if(rs.next())
      {
       
        headerDOB.setCustomerName(rs.getString("COMPANYNAME"));
        headerDOB.setCustEmailId(rs.getString("OPERATIONS_EMAILID"));
        headerDOB.setCustFaxNo(rs.getString("FAX"));
        headerDOB.setCustCountyCode(rs.getString("COUNTRYID"));
        headerDOB.setPaymentTerms(rs.getString("PAYMENTTERMS"));
      }
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
      query = null;
      query = "SELECT EMAILID,FAX FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND SL_NO=? ORDER BY SL_NO";
    
      if(masterDOB.getCustomerContacts()!=null)
      {
        toEmailIds  = new ArrayList();
        toFaxIds    = new ArrayList();
        psmt  = connection.prepareStatement(query);
       
           
        for(int i=0;i<masterDOB.getCustomerContacts().length;i++)
        {
        
          if(masterDOB.getCustomerContacts()[i]!=null && masterDOB.getCustomerContacts()[i].trim().length()!=0)
          {
            psmt.clearParameters();
            psmt.setString(1,masterDOB.getCustomerId());
           
            psmt.setInt(2,Integer.parseInt(masterDOB.getCustomerContacts()[i]));
            rs    = psmt.executeQuery();
            
            if(rs.next())
            {
              toEmailIds.add(rs.getString("EMAILID"));
             
              toFaxIds.add(rs.getString("FAX"));
            }
          }
          
          if(rs!=null)
            rs.close();
        }
        if(toEmailIds!=null && toEmailIds.size() > 0)
          contactPersonsEmails  = new String[toEmailIds.size()];
        if(toFaxIds!=null && toFaxIds.size() > 0)
          contactPersonsFax     = new String[toFaxIds.size()];
          
        int mailIdsSize	=	toEmailIds.size();
        for(int i=0;i<mailIdsSize;i++)
        {
          contactPersonsEmails[i]   =   (String)toEmailIds.get(i);
        
        }
        int faxIdsSize	= toFaxIds.size();
        for(int i=0;i<faxIdsSize;i++)
          contactPersonsFax[i]      =    (String)toFaxIds.get(i);
          //adeed by phani sekhar for wpbn 167678 on 20090415
         if(masterDOB.getCustomerContactsEmailIds()==null )
        masterDOB.setCustomerContactsEmailIds(contactPersonsEmails);
        masterDOB.setCustomerContactsFax(contactPersonsFax);
      }
      
      if(psmt!=null)
        psmt.close();
      
      
      if(masterDOB.getCustomerContacts()!=null && masterDOB.getCustomerContacts().length!=0)
        headerDOB.setAttentionTo(masterDOB.getCustomerContacts());
      
      headerDOB.setDateOfQuotation(masterDOB.getModifiedDate()!=null?masterDOB.getModifiedDate():masterDOB.getCreatedDate());
     
      //headerDOB.setPreparedBy(masterDOB.getCreatedBy());
      headerDOB.setAgent("DHL Global Forwarding");//hard coded
      query = null;
      
      query = "SELECT PM.PORTNAME, CM.COUNTRYNAME FROM FS_FRS_PORTMASTER PM, FS_COUNTRYMASTER CM WHERE CM.COUNTRYID=PM.COUNTRYID AND PORTID=?";
      
      psmt  = connection.prepareStatement(query);
      
      //String portName = null;
      
      if(masterDOB.getOriginPort()!=null)
      {
        psmt.setString(1,masterDOB.getOriginPort());
        rs    = psmt.executeQuery();
        if(rs.next())
        {
          //portName  =  rs.getString("PORTNAME");
          headerDOB.setOriginPortName(rs.getString("PORTNAME"));
          headerDOB.setOriginPortCountry(rs.getString("COUNTRYNAME"));
        }
        else
        {
          /*headerDOB.setOriginPortName(headerDOB.getOriginLocName());
          headerDOB.setOriginPortCountry(headerDOB.getOriginCountry());*/
          if(rs!=null)
            rs.close();
          /*if(psmt!=null)
            psmt.close();*/
          psmt1.clearParameters();
          //psmt1    =   connection.prepareStatement(locationQry);
          psmt1.setString(1,masterDOB.getOriginPort());
          rs      =   psmt1.executeQuery();
          
          if(rs.next())
          {
            headerDOB.setOriginPortName(rs.getString("LOCATIONNAME"));
            headerDOB.setOriginPortCountry(rs.getString("COUNTRYNAME"));
          }
        }
      }
      else
      {
        headerDOB.setOriginPortName(headerDOB.getOriginLocName());
        headerDOB.setOriginPortCountry(headerDOB.getOriginCountry());
      }
      psmt.clearParameters();
      
      
      if(masterDOB.getDestPort()!=null)
      {
        psmt.setString(1,masterDOB.getDestPort());
        rs    = psmt.executeQuery();
        if(rs.next())
        {
          headerDOB.setDestPortName(rs.getString("PORTNAME"));
          headerDOB.setDestPortCountry(rs.getString("COUNTRYNAME"));
        }
        else
        {
          /*headerDOB.setDestPortName(headerDOB.getDestLocName());
          headerDOB.setDestPortCountry(headerDOB.getDestinationCountry());*/
          if(rs!=null)
            rs.close();
         /* if(psmt!=null)
            psmt.close();*/
          
          //psmt    =   connection.prepareStatement(locationQry);\
          psmt1.clearParameters();
          psmt1.setString(1,masterDOB.getDestPort());
          rs      =   psmt1.executeQuery();
          
          if(rs.next())
          {
            headerDOB.setDestPortName(rs.getString("LOCATIONNAME"));
            headerDOB.setDestPortCountry(rs.getString("COUNTRYNAME"));
          }
        }
      }
      else
      {
        headerDOB.setDestPortName(headerDOB.getDestLocName());
        headerDOB.setDestPortCountry(headerDOB.getDestinationCountry());
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
      if(psmt1!=null)
        psmt1.close();
    
      String       incoTermsId     = masterDOB.getIncoTermsId();
      StringBuffer cargoAcceptance = new StringBuffer(incoTermsId+" ");
      
      if("other".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
      {
        headerDOB.setCargoAcceptancePlace(masterDOB.getCargoAccPlace());
      }
      else
      {
        if("EXW".equalsIgnoreCase(incoTermsId) || "FCA".equalsIgnoreCase(incoTermsId) || "FAS".equalsIgnoreCase(incoTermsId) || "FOB".equalsIgnoreCase(incoTermsId))
        {
          if("ddao".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
           cargoAcceptance.append("DGF TERMINAL ").append(headerDOB.getOriginLocName());//@@Modified by Yuvraj for WPBN-DHLQMS-22531
          else if("port".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
            cargoAcceptance.append(headerDOB.getOriginPortName()); 
          else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
           cargoAcceptance.append(masterDOB.getShipperZipCode()).append(" ").append(headerDOB.getOriginLocName());
          else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
            cargoAcceptance.append("Zone ").append(masterDOB.getShipperZones()).append(" ").append(headerDOB.getOriginLocName()).append(" (Refer Attachment)");
        }
        else
        {
          if("ddao".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
             cargoAcceptance.append("DGF TERMINAL ").append(headerDOB.getDestLocName());//@@Modified by Yuvraj for WPBN-DHLQMS-22531
          else if("port".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
            cargoAcceptance.append(headerDOB.getDestPortName()); 
          else if("ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
            cargoAcceptance.append(masterDOB.getConsigneeZipCode()).append(" ").append(headerDOB.getDestLocName());
          else if("ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()))
            cargoAcceptance.append(masterDOB.getConsigneeZones()).append(" ").append(headerDOB.getDestLocName()).append("(Refer Attachment)");
        }
        headerDOB.setCargoAcceptancePlace(cargoAcceptance.toString());
      }
        
        ArrayList freightRates            =   finalDOB.getLegDetails();
        int       freightRatesSize        =   freightRates.size();
        QuoteFreightLegSellRates legDOB   =   null;
        StringBuffer             sb       =   new StringBuffer("");
        
        if(freightRatesSize==1)
        {
            headerDOB.setRouting("Direct");            
            query = null;
            query = "SELECT SERVICELEVELDESC  FROM FS_FR_SERVICELEVELMASTER  WHERE SERVICELEVELID=?";
            
            psmt            =   connection.prepareStatement(query);
            legDOB          =  (QuoteFreightLegSellRates)freightRates.get(0);
            
     
            if(legDOB !=null)
            {
              legRateDetails  =   legDOB.getRates();
              if(legRateDetails!=null)
               // ratesDOB        =   (QuoteFreightRSRCSRDOB)legRateDetails.get(0);
            	  if(legDOB.getSelectedFreightSellRateIndex()!= -1)
               ratesDOB        =   (QuoteFreightRSRCSRDOB)legRateDetails.get(legDOB.getSelectedFreightSellRateIndex());
            	  else
            		  ratesDOB        =   (QuoteFreightRSRCSRDOB)legRateDetails.get(0);
            }
            
            if(masterDOB.getServiceLevelId()!=null)
              psmt.setString(1,masterDOB.getServiceLevelId());
            else if(ratesDOB!=null)
            { 
              if(ratesDOB.getServiceLevelId()!=null)
                psmt.setString(1,ratesDOB.getServiceLevelId());
              else
                psmt.setNull(1,Types.VARCHAR);
            }
            else if(legDOB!=null)
              psmt.setString(1,legDOB.getServiceLevel());
            else
                psmt.setNull(1,Types.VARCHAR);
             
            rs    = psmt.executeQuery();
            
            if(rs.next())
            {
            	headerDOB.setTypeOfService(rs.getString("SERVICELEVELDESC"));
            }
            //@@ Added By subrahmanyam for the pbn id: 198934 on 03-mar-010
           	if("".equalsIgnoreCase(headerDOB.getTypeOfService()) || headerDOB.getTypeOfService()==null)
        		headerDOB.setTypeOfService(getServiceLevelDesc(masterDOB.getQuoteId()));
            //@@ Ended By subrahmanyam for the pbn id: 198934 on 03-mar-010           	

        }
        else
        {
          for(int i=0;i<freightRatesSize;i++)
          {
            legDOB          =  (QuoteFreightLegSellRates)freightRates.get(i);
            
            if(i!=0)
              sb.append(", ");
            
            sb.append(legDOB.getOrigin()).append("-").append(legDOB.getDestination());
          }
          headerDOB.setRouting(sb.toString());
          
          headerDOB.setTypeOfService("Multi-Modal");
        }
      /*query = null;
      query = "SELECT SERVICELEVELDESC  FROM FS_FR_SERVICELEVELMASTER  WHERE SERVICELEVELID=?";
      psmt  = connection.prepareStatement(query);
      
      if(masterDOB.getServiceLevelId()!=null)
      {
        psmt.setString(1,masterDOB.getServiceLevelId());
        rs    = psmt.executeQuery();
        if(rs.next())
        {
          headerDOB.setTypeOfService(rs.getString("SERVICELEVELDESC"));
        }
      }
      else
      {
        if(freightRatesSize > 1)
            headerDOB.setTypeOfService("Multi-Modal");
      }*/
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
        
      if(masterDOB.getCommodityId()!=null)
      {
        query = null;
        query = "SELECT COMODITYDESCRIPTION FROM FS_FR_COMODITYMASTER WHERE COMODITYID =?";
        psmt  = connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getCommodityId());
        rs    = psmt.executeQuery();
        if(rs.next())
        {
          headerDOB.setCommodity(rs.getString("COMODITYDESCRIPTION"));
        }
      }
      else
      {
        //headerDOB.setCommodity("General Cargo-Non Hazardous");
    	//Modified By Kishore Podili For Hazardous Checking on 26-Apr-11
	   
		  if(masterDOB.isHazardousInd())
			  headerDOB.setCommodity("Cargo-Hazardous");//modified by silpa 0n 5-04-11
		  else
			  headerDOB.setCommodity("Cargo-Non Hazardous");//modified by silpa 0n 5-04-11
      }
      
      if(rs!=null)
        rs.close();
      if(psmt!=null)
        psmt.close();
      
      headerDOB.setIncoTerms(masterDOB.getIncoTermsId());
      headerDOB.setNotes("Based On Over Length Cargo: "+masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():"");
      headerDOB.setEffDate(masterDOB.getEffDate());
      
      if(masterDOB.getValidTo()!=null)
      {
        headerDOB.setValidUpto(masterDOB.getValidTo());
      }
      
      //psmt  = connection.prepareStatement(query);
      finalDOB.setHeaderDOB(headerDOB);
      
     // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009  
      if(finalDOB.getMasterDOB().getSalesPersonCode()!=null)
      {
           int shipmentMode=masterDOB.getShipmentMode();

       String  shipment      = "";   
        if(shipmentMode==4){
         shipment = " 4,5,6,7 ";
           }else if(shipmentMode==1){
        shipment = " 1,3,5,7 ";
           }else if(shipmentMode==2){
        shipment = " 2,3,6,7 ";
             }
             //"SELECT rw.EMPID, rw.EMAILID, ss.ALLOTED_TIME FROM FS_USERMASTER rw, fs_rep_officers_master ss WHERE rw.EMPID = (SELECT rr.rep_officers_id from fs_rep_officers_master rr where rr.userid = (SELECT USERID FROM FS_USERMASTER WHERE EMPID = 'E537')  and rr.shipment_mode = '4') and rw.userid = ss.userid  and rw.locationid = ss.locationid"
             query = null;
             //commented  by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009

//modified by VLAKSHMI for issue 169957 on 07/05/09
//modified by VLAKSHMI for issue 169805 on 06/05/09
        //query = "SELECT EMPID,EMAILID,ALLOTED_TIME FROM FS_USERMASTER WHERE EMPID=(SELECT REP_OFFICERS_ID FROM FS_USERMASTER WHERE EMPID=?)";
             //query = "SELECT RW.EMPID, RW.EMAILID, SS.ALLOTED_TIME FROM FS_USERMASTER RW, fs_rep_officers_master SS WHERE RW.EMPID = (SELECT RR.rep_officers_id from fs_rep_officers_master RR where RR.userid = (SELECT USERID FROM FS_USERMASTER WHERE EMPID = ?)  and RR.shipment_mode in("+shipment+")) and RW.userid = SS.userid  and RW.locationid = SS.locationid" ;
       //query = "SELECT RW.EMPID, RW.EMAILID, SS.ALLOTED_TIME FROM FS_USERMASTER RW, fs_rep_officers_master SS WHERE RW.EMPID = (SELECT RR.rep_officers_id from fs_rep_officers_master RR where RR.userid = (SELECT USERID FROM FS_USERMASTER WHERE EMPID = ?)  and RR.shipment_mode in("+shipment+")) and RW.userid = SS.userid  and RW.locationid = SS.locationid" ;
   // query = "SELECT ss.rep_officers_id, (select emailid from FS_USERMASTER where userid=ss.rep_officers_id)emailid, SS.ALLOTED_TIME FROM FS_USERMASTER RW, fs_rep_officers_master SS WHERE RW.EMPID =  ? and SS.shipment_mode in("+shipment+") and RW.userid = SS.userid  and RW.locationid = SS.locationid" ;
         query = "SELECT ss.rep_officers_id, (select emailid from FS_USERMASTER where empid=ss.rep_officers_id)emailid, SS.ALLOTED_TIME FROM FS_USERMASTER RW, fs_rep_officers_master SS WHERE RW.EMPID =  ? and SS.shipment_mode in("+shipment+") and RW.userid = SS.userid  and RW.locationid = SS.locationid" ;//@@Commented and Modified by Kameswari for the WPBN issue - 179220 on 12/08/09
          psmt  = connection.prepareStatement(query);

        psmt.setString(1,finalDOB.getMasterDOB().getSalesPersonCode());
        rs    = psmt.executeQuery();
      
        if(rs.next())
        {  
        //  finalDOB.setReportingOfficer(rs.getString("EMPID"));
        //  finalDOB.setReportingOfficerEmail(rs.getString("EMAILID"));
         finalDOB.setReportingOfficer(rs.getString("rep_officers_id"));
         finalDOB.setReportingOfficerEmail(rs.getString("EMAILID"));
          finalDOB.setAllottedTime(rs.getString("ALLOTED_TIME"));
        }
         if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
    if(masterDOB.getEmpId()!=null)
      {
         query = null;
        // query = "SELECT (USERNAME||',\n'||DEPARTMENT)CREATOR,EMAILID FROM FS_USERMASTER WHERE EMPID=?";//@@Commented by Kameswari for the WPBN issue-61303

        /*query  =  "SELECT U.USERNAME,DECODE(U.DEPARTMENT,null,'',U.DEPARTMENT)DEPARTMENT,U.EMAILID,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO,"+  //@@Modified by Kameswari for the WPBN issue-61303
                 " DECODE(D.DESCRIPTION,null,'',D.DESCRIPTION)DESCRIPTION,CURSOR(SELECT COMPANYNAME FROM FS_COMPANYINFO C WHERE C.COMPANYID=U.COMPANYID)COMPANYINFO FROM  QMS_DESIGNATION D,FS_USERMASTER U "+
                 "WHERE U.EMPID=? AND D.DESIGNATION_ID=U.DESIGNATION_ID";*/
         /* query  = "SELECT U.USERNAME, U.DEPARTMENT, U.EMAILID,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, D.DESCRIPTION,FSC.COMPANYNAME FROM QMS_DESIGNATION D,"+
                   "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE U.EMPID = ?  AND D.DESIGNATION_ID = U.DESIGNATION_ID AND FSC.COMPANYID = ?";  */
          /* query  = "SELECT U.USERNAME, U.DEPARTMENT, U.EMAILID,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+
                   "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE U.EMPID = ?   AND FSC.COMPANYID = ?"; */   //@@Modified for the WPBN issue-70355                  
      
         query  = "SELECT U.USERNAME, U.DEPARTMENT, U.EMAILID,U.PHONE_NO,U.FAX_NO,U.MOBILE_NO, FSC.COMPANYNAME FROM "+
                   "  FS_USERMASTER U,FS_COMPANYINFO FSC WHERE U.EMPID = ?   AND FSC.COMPANYID = ?";   //@@Modified for the WPBN issue-70355                  
     

         psmt  = connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getEmpId());
        psmt.setString(2,masterDOB.getCompanyId());
     
         rs    = psmt.executeQuery();
         
        while(rs.next())
        {
              masterDOB.setUserEmailId(rs.getString("EMAILID"));
             /* if(rs.getString("DESCRIPTION")!=null)
                  description = rs.getString("DESCRIPTION");*/
              if(rs.getString("DEPARTMENT")!=null)   
                  department = rs.getString("DEPARTMENT");
          //masterDOB.setCreatorDetails(rs.getString("USERNAME")+"\n"+description+"\n"+department);//@@Modified by Kameswari for the WPBN issue-61303
         masterDOB.setCreatorDetails(rs.getString("USERNAME")+"\n"+department);//@@Modified for the WPBN issue-70355  
         //@@Added by Kameswari for the WPBN issue-61303
          masterDOB.setPhoneNo((rs.getString("PHONE_NO")!=null)?rs.getString("PHONE_NO"):"");
          masterDOB.setFaxNo((rs.getString("FAX_NO")!=null)?rs.getString("FAX_NO"):"");
          masterDOB.setMobileNo((rs.getString("MOBILE_NO")!=null)?rs.getString("MOBILE_NO"):"");
          //rsdtls =(ResultSet)rs.getObject("COMPANYINFO");
          masterDOB.setCompanyName((rs.getString("COMPANYNAME")!=null)?rs.getString("COMPANYNAME"):"");
        
          //@@WPBN issue-61303
         /*  while(rsdtls.next())
        {
           if(rsdtls.getString("COMPANYNAME")!=null)
           masterDOB.setCompanyName(rsdtls.getString("COMPANYNAME"));
           else
            masterDOB.setCompanyName("");
        }*/
         }
       
       /*if(rsdtls!=null)
        rsdtls.close();*/ //Commented By RajKumari on 27-10-2008 for Connection Leakages.
         if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      if(masterDOB.getTerminalId()!=null)
      {
      
        /*query =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n')ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?"; */       
         /* query =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n')ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";   */     
     /* query =   "SELECT (AD.ADDRESSLINE1||'\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n')ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?"; */
     query =   "SELECT (AD.ADDRESSLINE1||'\n\n'||DECODE(AD.ADDRESSLINE2,null,'',AD.ADDRESSLINE2||'\n\n')||DECODE(AD.ADDRESSLINE3,NULL,'',AD.ADDRESSLINE3||'\n\n')||AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)||'\n')ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
//@@ Commented by subrahmanyam for the Enhancement 167668 on 29/04/09      
/*        psmt  = connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getTerminalId());
        rs    = psmt.executeQuery();
        
        if(rs.next())
        {
          masterDOB.setTerminalAddress(rs.getString("ADDRESS"));
        }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
*/
//@@ Added by subrahmanyam for Enhanement 167668  on 29/04/09
      custAddrQry = " SELECT  CUST_ADDRLINE1 ,CUST_ADDRLINE2 ,CUST_ADDRLINE3  FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID=? ";
      queryNoCustAddr = "SELECT AD.CITY||'\n'||DECODE(AD.STATE,NULL,'',AD.STATE||'\n')"+
                "||DECODE(AD.ZIPCODE,NULL,'',AD.ZIPCODE||'\n')||UPPER(CO.COUNTRYNAME)ADDRESS FROM FS_FR_TERMINALMASTER TM,FS_ADDRESS AD "+
                ",FS_COUNTRYMASTER CO WHERE TM.CONTACTADDRESSID=AD.ADDRESSID AND AD.COUNTRYID=CO.COUNTRYID AND TM.TERMINALID=?";
    psmt  = connection.prepareStatement(custAddrQry);  
    psmt.setString(1,masterDOB.getCreatedBy());
    psmt.setString(2,masterDOB.getTerminalId());
    rs    = psmt.executeQuery();
    if(rs.next())
    {
      custAddress1=rs.getString("CUST_ADDRLINE1");
      custAddress2=rs.getString("CUST_ADDRLINE2");
      custAddress3=rs.getString("CUST_ADDRLINE3");
    }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
    if(custAddress1==null && custAddress2==null && custAddress3==null)
    {
         psmt  = connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getTerminalId());
        rs    = psmt.executeQuery();
        
        if(rs.next())
        {
          masterDOB.setTerminalAddress(rs.getString("ADDRESS"));
        }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
    }
    else
      {
            custAddress1=(custAddress1!=null?custAddress1+"\n\n":"")+(custAddress2!=null?custAddress2+"\n\n":"")+(custAddress3!=null?custAddress3+"\n\n":"");
            psmt  = connection.prepareStatement(queryNoCustAddr);
        psmt.setString(1,masterDOB.getTerminalId());
        rs    = psmt.executeQuery();
        
        if(rs.next())
        {
          masterDOB.setTerminalAddress(custAddress1+rs.getString("ADDRESS"));
        }
        if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
//@@ Ended by subrahmanyam for Enhancement 167668 on 29/04/09    
      }
      
        
      if(masterDOB.getSalesPersonCode()!=null)
      {
        query = null;
        query = "SELECT USERNAME,EMAILID FROM FS_USERMASTER WHERE EMPID=?";
        psmt  = connection.prepareStatement(query);
        psmt.setString(1,masterDOB.getSalesPersonCode());
        rs    = psmt.executeQuery();
        if(rs.next())
        {
          masterDOB.setSalesPersonName(rs.getString("USERNAME"));
          masterDOB.setSalesPersonEmail(rs.getString("EMAILID"));
          headerDOB.setPreparedBy(rs.getString("USERNAME"));
        }
         if(rs!=null)
          rs.close();
        if(psmt!=null)
          psmt.close();
      }
      finalDOB.setMasterDOB(masterDOB);
    }
    catch(SQLException sqEx)
		{
      sqEx.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+sqEx.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getQuoteHeader(finalDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
			try
			{	if(rs!=null){
		          rs.close();
		          rs=null;
				}
		      if(psmt!=null){
		        psmt.close();
		        psmt=null;
		      }
		      if(psmt1!=null){
		        psmt1.close();
		        psmt1=null;
		      }
			}catch(Exception e){}
			try
			{
				ConnectionUtil.closeConnection(connection,psmt,rs);
			}
			catch(EJBException ex)
			{
				//Logger.error(FILE_NAME,"Finally : QMSQuoteSessionBean[getQuoteHeader(finalDOB)]-> "+ex.toString());
        logger.error(FILE_NAME+"Finally : QMSQuoteSessionBean[getQuoteHeader(finalDOB)]-> "+ex.toString());
				throw new EJBException(ex.toString());
			}
		}
    return finalDOB;
  }
  
  
 
  /**
	 * This method helps in getting the charges details of 
   * all the legs that are involved if a route specified
   * 
	 * @param finalDOB an QuoteFinalDOB Object that contains all the Quote information
	 * 
	 * @exception EJBException 
	 */
  public QuoteFinalDOB  getCharges(QuoteFinalDOB finalDOB) throws EJBException
  {
    ArrayList                 charges                 = null;//this is used to maintain info of all the legs
    ArrayList                 freightRates            = null;
    ArrayList                 rates                   = null;
    int                       freightRatesSize        = 0;
    
    QuoteFreightLegSellRates  legRateDetails          = null;//to maintain the info of each leg
    QuoteFreightLegSellRates  legChargeDetails        = null;//to maintain the list of charge info of each leg
    QuoteFreightLegSellRates  tiedCustInfoLeg         = null;//to maintain the list of charge info of each leg
    QuoteFreightRSRCSRDOB     ratesDOB                = null;
    QuoteMasterDOB            masterDOB               = null;
    QuoteCharges              chargesDOB              = null;
    QuoteChargeInfo           chargeInfo              = null;
    ArrayList                 chargeInfoList          = null;
    ArrayList                 freightChargesList      = null;
    ArrayList                 tiedCustInfoList        = null;
     
    QMSQuoteDAO               quoteDAO                = null;
    
    ArrayList                 tiedCustInfoFrtList     = null;
    QuoteCharges              tiedCustInfoChargesDOB  = null;
    double                    marginLimit             = 0;
  //@@ Added by subrahmanyam for the wpbn id 199797 on 17-Mar-010    
    QuoteFreightLegSellRates  legChargeDetailsSpotModify        = null;
	ArrayList     freightChargesSpotModify          	= null;
	ArrayList     freightChargesSpotModifyOld           = null;
	QuoteCharges  chargesDOBSpotModify			  		= null;
	QuoteCharges  chargesDOBSpotModifyOld			 	= null;
	ArrayList     freightChargeInfoSpotModify       	= null;
	ArrayList     freightChargeInfoSpotModifyOld       	= null;
	int 		  spotRateOldSize						=	0;
	int 		  spotRateNewSize						=	0;
	QuoteChargeInfo           chargeInfoSpotModify             = new QuoteChargeInfo();
	ArrayList                 chargeInfoListSpotModify          = null;
	
//@@ Ended by subrahmanyam for the wpbn id 199797 on 17-Mar-010	
    try
    {
      charges  = new ArrayList();
        if(finalDOB!=null)
      {
         masterDOB       = finalDOB.getMasterDOB();
         tiedCustInfoList= finalDOB.getTiedCustomerInfoFreightList();
      }
       quoteDAO        = new QMSQuoteDAO();
      //finalDOB        = quoteDAO.getCharges(finalDOB);
      finalDOB.setOriginChargesList(null);
      finalDOB.setDestChargesList(null);
      
      if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
        ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
          finalDOB        = quoteDAO.getCartages(finalDOB);
      
      finalDOB        = quoteDAO.getCharges(finalDOB);
      
      if(finalDOB!=null)
      {
        freightRates  = finalDOB.getLegDetails();
      }
     if(freightRates!=null)
     {
        freightRatesSize  = freightRates.size();//get the no fo legs since the freight rates size gives the no of legs
     }
      Hashtable spotRateDetails = null;
      ArrayList weightBreakSlabs = null;
      Iterator  spotRatesItr  = null;
      
      String weightBreak  = null;
      double[] rateDetail = null;
      
      int     spotRatesSize;
      int     weightBreakSlabSize;
      
      //get the charges for different legs
      for(int i=0;i<freightRatesSize;i++)
      {
        legRateDetails  = (QuoteFreightLegSellRates)freightRates.get(i);
     
    
        //if(tiedCustInfoList!=null)
        //tiedCustInfoLeg = (QuoteFreightLegSellRates)tiedCustInfoList.get(i);
        rates = legRateDetails.getRates();
        
        int selectedFreightRatesIndex = legRateDetails.getSelectedFreightSellRateIndex();
        if(!legRateDetails.isSpotRatesFlag())// && tiedCustInfoLeg==null
        {
        	if(selectedFreightRatesIndex!=-1)
          ratesDOB  = (QuoteFreightRSRCSRDOB)rates.get(selectedFreightRatesIndex);
        	else
        		ratesDOB  = (QuoteFreightRSRCSRDOB)rates.get(0);
        		
        }
        
        String  sellBuyRateId = "";
        String  buyRateId     = "";
        String  laneNo        = "";  
        String  sellOrBuyFlag = "";
        String  origin        = "";
        String  destination   = "";
        String  currency      = null;//@@added by kameswari for the issue WPBN-30908
             
        //get the sell rate or the buy rate index and lane no that are selected
        if(!legRateDetails.isSpotRatesFlag())
        {
          //if(tiedCustInfoLeg==null)
          //{
            if("BR".equalsIgnoreCase(ratesDOB.getRsrOrCsrFlag()))
            {
              sellOrBuyFlag = "N";
              sellBuyRateId = ""+ratesDOB.getBuyRateId();
              marginLimit     =  legRateDetails.getMarginLimit();
           
            }
            else
            {
              sellOrBuyFlag = "Y";
              sellBuyRateId = ""+ratesDOB.getSellRateId();
              buyRateId     = ""+ratesDOB.getBuyRateId();
              marginLimit     =  legRateDetails.getDiscountLimit();
            }
            
            laneNo          = ""+ratesDOB.getLaneNo();
           // marginLimit     =  legRateDetails.getMarginLimit();
            
          //}
        }
        else
        {
        
          sellOrBuyFlag = "SBR";
          laneNo        = ""+i;
         }
        origin      = legRateDetails.getOrigin();
        destination = legRateDetails.getDestination();
        currency    = legRateDetails.getCurrency(); 
// commented  by VLAKSHMI for issue 146968 on 5/12/2008
 //legChargeDetails  = quoteDAO.getLegRates(masterDOB,ratesDOB,origin,destination,sellBuyRateId,buyRateId,laneNo,sellOrBuyFlag,marginLimit,legRateDetails.getShipmentMode()+"",currency,legRateDetails.getDensityRatio());
       // if(!legRateDetails.isSpotRatesFlag())//subrahmanyam for spot rate issue
        //{
        	legChargeDetails  = quoteDAO.getLegRates(masterDOB,ratesDOB,origin,destination,sellBuyRateId,buyRateId,laneNo,sellOrBuyFlag,marginLimit,legRateDetails.getShipmentMode()+"",currency,legRateDetails.getDensityRatio(),legRateDetails.getContainerTypes());
      //  }
         if(legRateDetails.isSpotRatesFlag()&&legChargeDetails==null)//@@Modified by kameswari
        {
          
          chargesDOB  = new QuoteCharges();

          chargesDOB.setSellBuyFlag("SBR");
          chargesDOB.setChargeDescriptionId("Freight Rate");
          chargesDOB.setCostIncurredAt("Carrier");
          chargesDOB.setTerminalId(masterDOB.getTerminalId());
          
          chargeInfoList  = new ArrayList();
                    
          chargesDOB.setChargeInfoList(chargeInfoList);
          
          spotRateDetails = legRateDetails.getSpotRateDetails();
          weightBreakSlabs = new ArrayList();
          weightBreakSlabs  = legRateDetails.getWeightBreaks();
          weightBreakSlabSize=weightBreakSlabs.size();
          spotRatesSize   = spotRateDetails.size();
          spotRatesItr  = spotRateDetails.keySet().iterator();

          for(int j=0;j<weightBreakSlabSize;j++)//while(spotRatesItr.hasNext())
          {
            weightBreak  = (String)weightBreakSlabs.get(j);
          
            rateDetail = (double[])spotRateDetails.get(weightBreak);

            chargeInfo  = new QuoteChargeInfo();
            chargeInfoList.add(chargeInfo);
            chargeInfo.setBreakPoint((weightBreak!=null)?weightBreak:"");

            chargeInfo.setCurrency(currency);//@@added by kameswari for the issue WPBN-30908
            chargeInfo.setBuyRate(rateDetail[2]);
            chargeInfo.setRecOrConSellRrate(0);
            chargeInfo.setSellRate(rateDetail[2]);//@@Initially Sell Rate is same as buy rate as there is no RSR/CSR Defined for Spot Rates.
            chargeInfo.setSellChargeMargin(0);
            chargeInfo.setSellChargeMarginType("");
            chargeInfo.setMargin(0);
            chargeInfo.setMarginType("");
            if(weightBreak!=null&&chargeInfo.getRateDescription()==null)
            {
            if("FSBASIC".equalsIgnoreCase(weightBreak)||"FSMIN".equalsIgnoreCase(weightBreak)||
            "FSKG".equalsIgnoreCase(weightBreak))
              chargeInfo.setRateDescription("FUEL SURCHARGE");
           else  if("SSBASIC".equalsIgnoreCase(weightBreak)||"SSMIN".equalsIgnoreCase(weightBreak)||
            "SSKG".equalsIgnoreCase(weightBreak))
              chargeInfo.setRateDescription("SECURITY SURCHARGE");
               else  if("CAFMIN".equalsIgnoreCase(weightBreak)||"CAF%".equalsIgnoreCase(weightBreak))
                chargeInfo.setRateDescription("C.A.F%");
               else  if("BAFMIN".equalsIgnoreCase(weightBreak)|| "BAFM3".equalsIgnoreCase(weightBreak))
                 chargeInfo.setRateDescription("B.A.F");
              else  if("PSSMIN".equalsIgnoreCase(weightBreak)|| "PSSM3".equalsIgnoreCase(weightBreak))
                 chargeInfo.setRateDescription("P.S.S");
               else  if("CSF".equalsIgnoreCase(weightBreak))
                  chargeInfo.setRateDescription("C.S.F");
               else  if("SURCHARGE".equalsIgnoreCase(weightBreak))
                 chargeInfo.setRateDescription("SURCHARGE");
               else  if(weightBreak.endsWith("CSF")||weightBreak.endsWith("csf"))
                 chargeInfo.setRateDescription("CSF");
              else  if(weightBreak.endsWith("PSS")||weightBreak.endsWith("pss"))
                 chargeInfo.setRateDescription("PSS");
                  else  if(weightBreak.endsWith("CAF")||weightBreak.endsWith("caf"))
                 chargeInfo.setRateDescription("CAF%");
                  else  if(weightBreak.endsWith("BAF")||weightBreak.endsWith("baf"))
                 chargeInfo.setRateDescription("BAF");
               else  
                  chargeInfo.setRateDescription("FREIGHT RATE");
            }
            else 
              chargeInfo.setRateDescription("FREIGHT RATE");
            if(weightBreak!=null)
             {
            
              if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
                if("CSF".equalsIgnoreCase(weightBreak))
                {
                 chargeInfo.setBasis("Per Shipment");
                }
              else  if("FSBASIC".equalsIgnoreCase(weightBreak)||"FSMIN".equalsIgnoreCase(weightBreak)||
               "SSBASIC".equalsIgnoreCase(weightBreak)||"SSMIN".equalsIgnoreCase(weightBreak)||"BAFMIN".equalsIgnoreCase(weightBreak)||
               "PSSMIN".equalsIgnoreCase(weightBreak)||"CAFMIN".equalsIgnoreCase(weightBreak))
               {
              
                  chargeInfo.setBasis("Per Shipment");
               }
               else if("FSKG".equalsIgnoreCase(weightBreak)|| "SSKG".equalsIgnoreCase(weightBreak))
               {
                  chargeInfo.setBasis("Per Kilogram");
               }
               else if("CAF%".equalsIgnoreCase(weightBreak)||"SURCHARGE".equalsIgnoreCase(weightBreak))
               {
                 chargeInfo.setBasis("Percent");
               }
                else  if(weightBreak.endsWith("CAF")||weightBreak.endsWith("caf"))
                {
                      chargeInfo.setBasis("Percent of Freight");
                }
                else  if(weightBreak.endsWith("BAF")||weightBreak.endsWith("baf")||weightBreak.endsWith("CSF")||weightBreak.endsWith("csf")
                ||weightBreak.endsWith("PSS")||weightBreak.endsWith("pss"))
                {
                      chargeInfo.setBasis("Per Container");
                }

                else if("BAFM3".equalsIgnoreCase(weightBreak)||"PSSM3".equalsIgnoreCase(weightBreak))
                {
//                 chargeInfo.setBasis("per Cubic Meter");Commented by govind for displaying the charge basis as percubic meter instead of per WeightMeasurement
                	chargeInfo.setBasis("per Weight Measurement");
                }
                
           }
             else   if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
             {
              if("LIST".equalsIgnoreCase(legRateDetails.getSpotRatesType()))
                

              {
              	//@@ Commented and added by subrahmanyam for the id 218563 on  21/sep/10
                //if(legRateDetails.getShipmentMode()==2 )
            	  if(legRateDetails.getShipmentMode()==2 || legRateDetails.getShipmentMode()==4)
              
                       chargeInfo.setBasis("Per Container");
               else
                    chargeInfo.setBasis("Per ULD");
              
              }
              else
              {
                if("MIN".equalsIgnoreCase(weightBreak))
                {
                  chargeInfo.setBasis("Per Shipment");
                }
                else
                {
                  if(legRateDetails.getShipmentMode()==2 && "FLAT".equalsIgnoreCase(weightBreak))
                	  chargeInfo.setBasis("Per Weight Measurement");
                	else if("Kg".equalsIgnoreCase(legRateDetails.getUom()))
                    chargeInfo.setBasis("Per Kilogram");
                  else if("Lb".equalsIgnoreCase(legRateDetails.getUom()))
                    chargeInfo.setBasis("Per Pound");
                  else if("CBM".equalsIgnoreCase(legRateDetails.getUom()))
                    chargeInfo.setBasis("Per Cubic Meter");
                  else if("CFT".equalsIgnoreCase(legRateDetails.getUom()))
                    chargeInfo.setBasis("Per Cubic Feet");
                }
              }
            }
          }
            chargeInfo.setRatio(legRateDetails.getDensityRatio());
            chargeInfo.setLineNumber(j);
          }
          
          if(legChargeDetails!=null&&!(legRateDetails.isSpotRatesFlag()))
          {
            freightChargesList  = legChargeDetails.getFreightChargesList();
          }
          else
          {
            legChargeDetails  = new QuoteFreightLegSellRates();
            freightChargesList  = new ArrayList();
          }
          
          freightChargesList.add(chargesDOB);
          
          legChargeDetails.setFreightChargesList(freightChargesList);
        }
         
        else if(tiedCustInfoLeg!=null)
        {
            freightChargesList  = new ArrayList();
            legChargeDetails    = new QuoteFreightLegSellRates();
            tiedCustInfoFrtList = tiedCustInfoLeg.getFreightChargesList();
            freightChargesList.add((QuoteCharges)tiedCustInfoFrtList.get(0));
            legChargeDetails.setFreightChargesList(freightChargesList);
            legChargeDetails.setTiedCustInfoFlag(true);
        }
 //@@ Added by subrahmanyam for the wpbn id 199797 on 17-Mar-010         
        else if(legRateDetails.isSpotRatesFlag() && "Modify".equalsIgnoreCase(finalDOB.getMasterDOB().getOperation()))
        {
        	chargesDOB  = new QuoteCharges();
        	chargeInfoListSpotModify = new ArrayList(); //@@Added by subrahmanyam for the pbn id:209849 on 29/Jun/10
            chargesDOB.setSellBuyFlag("SBR");
            chargesDOB.setChargeDescriptionId("Freight Rate");
            chargesDOB.setCostIncurredAt("Carrier");
            chargesDOB.setTerminalId(masterDOB.getTerminalId());
            
            chargeInfoList  = new ArrayList();
                      
            chargesDOB.setChargeInfoList(chargeInfoList);
            
            spotRateDetails = legRateDetails.getSpotRateDetails();
            weightBreakSlabs = new ArrayList();
            weightBreakSlabs  = legRateDetails.getWeightBreaks();
            weightBreakSlabSize=weightBreakSlabs.size();
            spotRatesSize   = spotRateDetails.size();
            spotRatesItr  = spotRateDetails.keySet().iterator();

            for(int j=0;j<weightBreakSlabSize;j++)//while(spotRatesItr.hasNext())
            {
              weightBreak  = (String)weightBreakSlabs.get(j);
            
              rateDetail = (double[])spotRateDetails.get(weightBreak);

              chargeInfo  = new QuoteChargeInfo();
              chargeInfoList.add(chargeInfo);
              chargeInfo.setBreakPoint((weightBreak!=null)?weightBreak:"");

              chargeInfo.setCurrency(currency);//@@added by kameswari for the issue WPBN-30908
              chargeInfo.setBuyRate(rateDetail[2]);
              chargeInfo.setRecOrConSellRrate(0);
              chargeInfo.setSellRate(rateDetail[2]);//@@Initially Sell Rate is same as buy rate as there is no RSR/CSR Defined for Spot Rates.
              chargeInfo.setSellChargeMargin(0);
              chargeInfo.setSellChargeMarginType("");
              chargeInfo.setMargin(0);
              chargeInfo.setMarginType("");
              if(weightBreak!=null&&chargeInfo.getRateDescription()==null)
              {
              if("FSBASIC".equalsIgnoreCase(weightBreak)||"FSMIN".equalsIgnoreCase(weightBreak)||
              "FSKG".equalsIgnoreCase(weightBreak))
                chargeInfo.setRateDescription("FUEL SURCHARGE");
             else  if("SSBASIC".equalsIgnoreCase(weightBreak)||"SSMIN".equalsIgnoreCase(weightBreak)||
              "SSKG".equalsIgnoreCase(weightBreak))
                chargeInfo.setRateDescription("SECURITY SURCHARGE");
                 else  if("CAFMIN".equalsIgnoreCase(weightBreak)||"CAF%".equalsIgnoreCase(weightBreak))
                  chargeInfo.setRateDescription("C.A.F%");
                 else  if("BAFMIN".equalsIgnoreCase(weightBreak)|| "BAFM3".equalsIgnoreCase(weightBreak))
                   chargeInfo.setRateDescription("B.A.F");
                else  if("PSSMIN".equalsIgnoreCase(weightBreak)|| "PSSM3".equalsIgnoreCase(weightBreak))
                   chargeInfo.setRateDescription("P.S.S");
                 else  if("CSF".equalsIgnoreCase(weightBreak))
                    chargeInfo.setRateDescription("C.S.F");
                 else  if("SURCHARGE".equalsIgnoreCase(weightBreak))
                   chargeInfo.setRateDescription("SURCHARGE");
                 else  if(weightBreak.endsWith("CSF")||weightBreak.endsWith("csf"))
                   chargeInfo.setRateDescription("CSF");
                else  if(weightBreak.endsWith("PSS")||weightBreak.endsWith("pss"))
                   chargeInfo.setRateDescription("PSS");
                    else  if(weightBreak.endsWith("CAF")||weightBreak.endsWith("caf"))
                   chargeInfo.setRateDescription("CAF%");
                    else  if(weightBreak.endsWith("BAF")||weightBreak.endsWith("baf"))
                   chargeInfo.setRateDescription("BAF");
                 else  
                    chargeInfo.setRateDescription("FREIGHT RATE");
              }
              else 
                chargeInfo.setRateDescription("FREIGHT RATE");
              if(weightBreak!=null)
               {
              
                if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
                {
                  if("CSF".equalsIgnoreCase(weightBreak))
                  {
                   chargeInfo.setBasis("Per Shipment");
                  }
                else  if("FSBASIC".equalsIgnoreCase(weightBreak)||"FSMIN".equalsIgnoreCase(weightBreak)||
                 "SSBASIC".equalsIgnoreCase(weightBreak)||"SSMIN".equalsIgnoreCase(weightBreak)||"BAFMIN".equalsIgnoreCase(weightBreak)||
                 "PSSMIN".equalsIgnoreCase(weightBreak)||"CAFMIN".equalsIgnoreCase(weightBreak))
                 {
                
                    chargeInfo.setBasis("Per Shipment");
                 }
                 else if("FSKG".equalsIgnoreCase(weightBreak)|| "SSKG".equalsIgnoreCase(weightBreak))
                 {
                    chargeInfo.setBasis("Per Kilogram");
                 }
                 else if("CAF%".equalsIgnoreCase(weightBreak)||"SURCHARGE".equalsIgnoreCase(weightBreak))
                 {
                   chargeInfo.setBasis("Percent");
                 }
                  else  if(weightBreak.endsWith("CAF")||weightBreak.endsWith("caf"))
                  {
                        chargeInfo.setBasis("Percent of Freight");
                  }
                  else  if(weightBreak.endsWith("BAF")||weightBreak.endsWith("baf")||weightBreak.endsWith("CSF")||weightBreak.endsWith("csf")
                  ||weightBreak.endsWith("PSS")||weightBreak.endsWith("pss"))
                  {
                        chargeInfo.setBasis("Per Container");
                  }

                  else if("BAFM3".equalsIgnoreCase(weightBreak)||"PSSM3".equalsIgnoreCase(weightBreak))
                  {
                  // chargeInfo.setBasis("per Cubic Meter");commented by govind for dispalying the charge basis as per Cubic Meter instead of Per WeightMeasurement  
                	  chargeInfo.setBasis("Per Weight Measurement");
                  }
                  
             }
               else   if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
               {
                if("LIST".equalsIgnoreCase(legRateDetails.getSpotRatesType()))
                  

                {
                	//@@ Commented and added by subrahmanyam for the id 218563 on  21/sep/10
                    //if(legRateDetails.getShipmentMode()==2 )
                  if(legRateDetails.getShipmentMode()==2 || legRateDetails.getShipmentMode()==4)
                
                         chargeInfo.setBasis("Per Container");
                 else
                      chargeInfo.setBasis("Per ULD");
                
                }
                else
                {
                  if("MIN".equalsIgnoreCase(weightBreak))
                  {
                    chargeInfo.setBasis("Per Shipment");
                  }
                  else
                  {
                    if("Kg".equalsIgnoreCase(legRateDetails.getUom()))
                      chargeInfo.setBasis("Per Kilogram");
                    else if("Lb".equalsIgnoreCase(legRateDetails.getUom()))
                      chargeInfo.setBasis("Per Pound");
                    else if("CBM".equalsIgnoreCase(legRateDetails.getUom()))
                      chargeInfo.setBasis("Per Cubic Meter");
                    else if("CFT".equalsIgnoreCase(legRateDetails.getUom()))
                      chargeInfo.setBasis("Per Cubic Feet");
                  }
                }
              }
            }
              chargeInfo.setRatio(legRateDetails.getDensityRatio());
              chargeInfo.setLineNumber(j);
            }
            
            if(legChargeDetails!=null&&!(legRateDetails.isSpotRatesFlag()))
            {
              freightChargesList  = legChargeDetails.getFreightChargesList();
            }
            else
            {
            	legChargeDetailsSpotModify  = new QuoteFreightLegSellRates();
              freightChargesList  = new ArrayList();
            }
            
            freightChargesList.add(chargesDOB);
            
            legChargeDetailsSpotModify.setFreightChargesList(freightChargesList);
            freightChargesSpotModify	=	freightChargesList;
            int freightChargesSpotModifySize =0;
            if(freightChargesSpotModify!=null)
            	freightChargesSpotModifySize = freightChargesSpotModify.size();
            else
            	freightChargesSpotModifySize	=	0;
            for(int j=0;j<freightChargesSpotModifySize;j++)
            {
            	chargesDOBSpotModify	=	(QuoteCharges)freightChargesSpotModify.get(j);
            	freightChargeInfoSpotModify	= chargesDOBSpotModify.getChargeInfoList();
            	spotRateNewSize			=	freightChargeInfoSpotModify.size();
            	logger.info("enteredSpotRateDetail Size: "+spotRateNewSize);
            	logger.info("Entered BreakPoints are: "+freightChargeInfoSpotModify);
            }
            
            if(legChargeDetails!=null)
            {
                freightChargesSpotModifyOld	=	legChargeDetails.getFreightChargesList();
            int freightChargesSpotModifyOldSize =0;
            if(freightChargesSpotModifyOld!=null)
            	freightChargesSpotModifyOldSize = freightChargesSpotModifyOld.size();
            else
            	freightChargesSpotModifyOldSize	=	0;
            for(int j=0;j<freightChargesSpotModifyOldSize;j++)
            {
            	chargesDOBSpotModifyOld	=	(QuoteCharges)freightChargesSpotModifyOld.get(j);
            	freightChargeInfoSpotModifyOld	= chargesDOBSpotModifyOld.getChargeInfoList();
            	spotRateOldSize					=	freightChargeInfoSpotModifyOld.size();
            	logger.info("oldSpotRateDetail Size: "+spotRateOldSize);
            	logger.info("Entered BreakPoints are: "+freightChargeInfoSpotModifyOld);
            }
            
            }
          /*  if(spotRateOldSize	!=	spotRateNewSize)
            {*/
            	//legChargeDetails	=	legChargeDetailsSpotModify;
            	String brkPoint	="";
            	double buyRate	= 0.0;
            
            	for(int s =0;s<spotRateNewSize;s++)
            	{
            		brkPoint	=	((QuoteChargeInfo)freightChargeInfoSpotModify.get(s)).getBreakPoint();
            		buyRate		=	((QuoteChargeInfo)freightChargeInfoSpotModify.get(s)).getBuyRate();
            		int count =0;
            		for(int s1 =0;s1<spotRateOldSize;s1++)
            		{
                  		if(( brkPoint.equalsIgnoreCase(((QuoteChargeInfo)freightChargeInfoSpotModifyOld.get(s1)).getBreakPoint()) 
                  				 && buyRate == ((QuoteChargeInfo)freightChargeInfoSpotModifyOld.get(s1)).getBuyRate() ) )
                      		{
                      			chargeInfoListSpotModify.add((QuoteChargeInfo)freightChargeInfoSpotModifyOld.get(s1));
                      			count++;
                      		}
            		}
            		if(count ==0)
            			chargeInfoListSpotModify.add((QuoteChargeInfo)freightChargeInfoSpotModify.get(s));
            		
             
            	}
            	int finalSize 	= 	legChargeDetails.getFreightChargesList().size();
            	for (int j=0;j<finalSize;j++)
            	{
            		((QuoteCharges)legChargeDetails.getFreightChargesList().get(j)).setChargeInfoList(chargeInfoListSpotModify);
           		
            	}
            	
          //  }
          /*  else if(spotRateOldSize	==	spotRateNewSize)
            {
            	for(int x=0;x<spotRateOldSize;x++)
            	{
            		if(!( ((QuoteChargeInfo)freightChargeInfoSpotModifyOld.get(x)).getBreakPoint().equalsIgnoreCase(((QuoteChargeInfo)freightChargeInfoSpotModify.get(x)).getBreakPoint()) 
            				 && ((QuoteChargeInfo)freightChargeInfoSpotModifyOld.get(x)).getBuyRate() == ((QuoteChargeInfo)freightChargeInfoSpotModify.get(x)).getBuyRate() ) )
            			legChargeDetails	=	legChargeDetailsSpotModify;
            	}
            }*/

        	
        }
 //@@ Ended by subrahmanyam for the wpbn id 199797 on 17-Mar-010
        if(legChargeDetails!=null)
        {
          legChargeDetails.setOrigin(origin);
          legChargeDetails.setDestination(destination);
          legChargeDetails.setShipmentMode(legRateDetails.getShipmentMode());
          legChargeDetails.setServiceLevel(legRateDetails.getServiceLevel());
          legChargeDetails.setUom(legRateDetails.getUom());
          legChargeDetails.setDensityRatio(legRateDetails.getDensityRatio());
          legChargeDetails.setRates(legRateDetails.getRates());
          legChargeDetails.setSpotRatesFlag(legRateDetails.isSpotRatesFlag());
          legChargeDetails.setSpotRateDetails(legRateDetails.getSpotRateDetails());

          legChargeDetails.setSpotRatesType(legRateDetails.getSpotRatesType());
          legChargeDetails.setWeightBreaks(legRateDetails.getWeightBreaks());//@@Spot weight Breaks.
          legChargeDetails.setCurrency(legRateDetails.getCurrency());//@@added by kameswari for the issue WPBN-30908
          legChargeDetails.setSlabWeightBreaks(legRateDetails.getSlabWeightBreaks());
          legChargeDetails.setListWeightBreaks(legRateDetails.getListWeightBreaks());
        
          legChargeDetails.setMarginLimit(legRateDetails.getMarginLimit());
          legChargeDetails.setDiscountLimit(legRateDetails.getDiscountLimit());
          freightRates.remove(i);
          freightRates.add(i,legChargeDetails);
        }
      }
      //end
      
      finalDOB.setLegDetails(freightRates);
      //Logger.info(FILE_NAME,"The header dob is:"+finalDOB.getHeaderDOB());
    }
    catch(Exception e)
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
		finally
		{
		}
    return finalDOB;
  }
  
  /**
	 * This method helps in getting the Margin Limits of 
   * freight rates,charges & cartages. This is useful in validating
   * the margin limits for the sales person & to perform 
   * margin test in the 3rd screen.
   * 
	 * @param finalDOB an QuoteFinalDOB Object that contains all the Quote information
	 * 
	 * @exception EJBException 
	 */
  public QuoteFinalDOB getMarginLimit(QuoteFinalDOB finalDOB) throws EJBException
  {
    Connection                connection              = null;
    PreparedStatement         psmt                    = null;
    ResultSet                 rs                      = null;
    String                    frtQuery                = null;
    String                    chargesQuery            = null;
    String                    marginId                = "";
    String                    whereCondition          = "";
    ArrayList                 freightRates            = null;
    int                       noOfLegs                = 0;
    int                       selectedIndex           = 0;
    QuoteFreightLegSellRates  legDOB                  = null;
    QuoteMasterDOB            masterDOB               = null;
    String                    weightBreakType         = "";
    String                    legServiceLevel         = "";
    
    boolean                   marginLimitFlag         = false;
    boolean                   cartageMarginFlag       = false;
    boolean                   chargesMarginFlag       = false;
    
    try
    {          
          connection      =   this.getConnection();
          
          freightRates    =   finalDOB.getLegDetails();
          masterDOB       =   finalDOB.getMasterDOB();
      
          if(freightRates!=null)
            noOfLegs  = freightRates.size();
            
         /*frtQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
                  "AND MARGIN_ID = ? AND SERVICE_LEVEL=? AND LEVELNO  = (SELECT LEVEL_NO "+
                  "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?)) "+
                  "AND CHARGETYPE='FREIGHT'";*/
          frtQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
                  "AND MARGIN_ID = ? AND SERVICE_LEVEL=? AND LEVELNO  = (SELECT LEVEL_NO "+
                  "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?)) "+
                  "AND CHARGETYPE='FREIGHT'";
          /*chargesQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
                  "AND LEVELNO  = (SELECT LEVEL_NO "+
                  "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
                  "AND CHARGETYPE <> 'FREIGHT'";*/
          /*chargesQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
          "AND LEVELNO  = (SELECT LEVEL_NO "+
          "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
          "AND CHARGETYPE <> 'FREIGHT'";*/
            chargesQuery = "SELECT MINMARGINS,MAXDISCOUNT,CHARGETYPE FROM QMS_MARGIN_LIMIT_DTL WHERE INVALIDATE='F' "+
          "AND LEVELNO  = (SELECT LEVEL_NO "+
          "FROM QMS_DESIGNATION WHERE DESIGNATION_ID=(SELECT DESIGNATION_ID FROM FS_USERMASTER WHERE EMPID=?))"+
          "AND CHARGETYPE <> 'FREIGHT'";      
          psmt  = connection.prepareStatement(frtQuery);
          
          for(int i=0;i<noOfLegs;i++)
          {
            marginLimitFlag =     false;
            legDOB          =     (QuoteFreightLegSellRates)freightRates.get(i);
            selectedIndex   =     legDOB.getSelectedFreightSellRateIndex();
            
            if(legDOB.isSpotRatesFlag() || legDOB.isTiedCustInfoFlag())
            {
              //Logger.info(FILE_NAME,"inside spot rates for the leg :"+i);
              weightBreakType = legDOB.getSpotRatesType();
              legServiceLevel = legDOB.getServiceLevel();
            }
            else
            {
             
              //Logger.info(FILE_NAME,"in else with selected index::"+selectedIndex);
              if(selectedIndex!=-1){
              weightBreakType = ((QuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getWeightBreakType()!=null?
              ((QuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getWeightBreakType():"";

              legServiceLevel = (((QuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getServiceLevelId())!=null?
              ((QuoteFreightRSRCSRDOB)legDOB.getRates().get(selectedIndex)).getServiceLevelId():"";
			  }             
            } 
            
            if(legDOB.getShipmentMode()==1)
              marginId = "1";
            else if(legDOB.getShipmentMode()==2 && "LIST".equals(weightBreakType))
              marginId = "4";
            else if(legDOB.getShipmentMode()==2 && !"LIST".equals(weightBreakType))
              marginId = "2";
            else if(legDOB.getShipmentMode()==4 && "LIST".equals(weightBreakType))
              marginId = "15";
            else if(legDOB.getShipmentMode()==4 && !"LIST".equals(weightBreakType))
              marginId = "7";
              
             psmt.setString(1,marginId);
             psmt.setString(2,legServiceLevel);
             psmt.setString(3,masterDOB.getSalesPersonCode());
             
             rs   =   psmt.executeQuery();
             
             if(rs.next())
             {
                //Logger.info(FILE_NAME,"inside rs for margin test:");
                marginLimitFlag = true;//@@This flag is checked only in case of freight rates.
              
                legDOB.setMarginLimit(rs.getDouble("MINMARGINS"));
                
                legDOB.setDiscountLimit(rs.getDouble("MAXDISCOUNT"));
                legDOB.setMarginFlag(true);
               
             }
            else legDOB.setMarginFlag(false);
            
            //Logger.info(FILE_NAME,"legDOB.isMarginFlag():"+legDOB.isMarginFlag());
            
            //Logger.info(FILE_NAME,"legDOBlegDOB::"+legDOB);
                
            if(rs!=null)
              rs.close();
            
            psmt.clearParameters();
                
            freightRates.remove(i);
            freightRates.add(i,legDOB);    
          }
          
          if(rs!=null)
              rs.close();
          if(psmt!=null)
            psmt.close();
          
          psmt  = connection.prepareStatement(chargesQuery);
          
          psmt.setString(1,masterDOB.getSalesPersonCode());
          
          rs    = psmt.executeQuery();
          
          while(rs.next())
          {
            if("CHARGES".equalsIgnoreCase(rs.getString("CHARGETYPE")))
            {
                chargesMarginFlag = true;
                finalDOB.setChargesMargin(rs.getDouble("MINMARGINS"));
               finalDOB.setChargesDiscount(rs.getDouble("MAXDISCOUNT"));
            }
            else if("CARTAGES".equalsIgnoreCase(rs.getString("CHARGETYPE")))
            {
                cartageMarginFlag = true;
                finalDOB.setCartageMargin(rs.getDouble("MINMARGINS"));
               finalDOB.setCartageDiscount(rs.getDouble("MAXDISCOUNT"));
            }
          }
          finalDOB.setChargesMarginDefined(chargesMarginFlag);
          finalDOB.setCartageMarginDefined(cartageMarginFlag);
          finalDOB.setLegDetails(freightRates);        
    }
    catch(Exception e) 
		{
      e.printStackTrace();
			//Logger.error(FILE_NAME,"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getFreightSellRates(masterDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
    finally
    {
      ConnectionUtil.closeConnection(connection,psmt,rs);
    }
    return finalDOB;
  }
  /**
	 * Method Added to Fetch Shipper & Consignee Zip Codes
   * if not Selected in Master Page.
	 * @param masterDOB, QuoteMasterDOB Object that contains all the Quote Master Page information
	 * 
	 * @exception EJBException 
	 */
  public QuoteMasterDOB getShipperConsigneeZones(QuoteMasterDOB masterDOB) throws EJBException
  {
    Connection                connection              = null;
    PreparedStatement         psmt                    = null;
    ResultSet                 rs                      = null;
    String                    query                   = null;
    String                    shipperAlpha            = null;
    String                    shipperZipCode          = null;
    String                    consigneeAlpha          = null;
    String                    consigneeZipCode        = null;
    String                    whereCondition          = "";
    
    try
    {
      
      connection        =     this.getConnection();
      
      /*query             =   "SELECT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE ";
                    
      psmt              =     connection.prepareStatement(query);*/
      
                    
      if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().trim().length()!=0)
      {
        shipperZipCode  = masterDOB.getShipperZipCode();
        if(masterDOB.getShipperZones()==null)
        {
          if(shipperZipCode.indexOf("-")!=-1)
          {
            shipperAlpha    =  shipperZipCode.substring(0,shipperZipCode.indexOf("-"));
            shipperZipCode  =  shipperZipCode.substring((shipperZipCode.indexOf("-")+1),shipperZipCode.trim().length());
          }
        
          if(shipperAlpha != null)
              whereCondition  = " AND D.ALPHANUMERIC= '"+shipperAlpha+"'";
        //  else
         //     whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
          
          query         =   "SELECT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE, '~') = ? "+
                            whereCondition;
          
          psmt          =     connection.prepareStatement(query);
          psmt.setString(1,masterDOB.getOriginLocation());
          psmt.setString(2,shipperZipCode);
          psmt.setString(3,masterDOB.getShipperMode());          
          if("1".equalsIgnoreCase(masterDOB.getShipperMode()))
              psmt.setString(4,"~");
          else 
              psmt.setString(4,masterDOB.getShipperConsoleType());
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
              masterDOB.setShipperZones(rs.getString("ZONE_CODE"));
          
          if(psmt!=null)
            psmt.close();
          if(rs!=null)
            rs.close();
        }
      }
      
      if(masterDOB.getConsigneeZipCode()!= null && masterDOB.getConsigneeZipCode().trim().length()!=0)
      {
        whereCondition    = "";
        consigneeZipCode  = masterDOB.getConsigneeZipCode();
        if(masterDOB.getConsigneeZones()==null)
        {
          if(consigneeZipCode.indexOf("-")!=-1)
          {
            consigneeAlpha    =  consigneeZipCode.substring(0,consigneeZipCode.indexOf("-"));
            consigneeZipCode  =  consigneeZipCode.substring((consigneeZipCode.indexOf("-")+1),consigneeZipCode.trim().length());
          }
          
          if(consigneeAlpha != null)
              whereCondition  = " AND D.ALPHANUMERIC= '"+consigneeAlpha+"'";
        //  else
        //      whereCondition  = " AND D.ALPHANUMERIC IS NULL ";
          
          query         =   "SELECT DISTINCT D.ZONE ZONE_CODE FROM QMS_ZONE_CODE_MASTER M,QMS_ZONE_CODE_DTL D  "+
                            "WHERE D.ZONE_CODE=M.ZONE_CODE AND M.ORIGIN_LOCATION=? AND TO_NUMBER(?) BETWEEN "+
                            "D.FROM_ZIPCODE AND D.TO_ZIPCODE AND M.SHIPMENT_MODE = ? AND NVL(M.CONSOLE_TYPE, '~') = ? "+
                            whereCondition;
          
          psmt          =     connection.prepareStatement(query);
          psmt.setString(1,masterDOB.getDestLocation());
          psmt.setString(2,consigneeZipCode);
          psmt.setString(3,masterDOB.getConsigneeMode());          
          if("1".equalsIgnoreCase(masterDOB.getConsigneeMode()))
              psmt.setString(4,"~");
          else 
              psmt.setString(4,masterDOB.getConsigneeConsoleType());
          
          rs    =   psmt.executeQuery();
          
          while(rs.next())
              masterDOB.setConsigneeZones(rs.getString("ZONE_CODE"));
        }                 
      }
     }
    
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getShipperConsigneeZones"+e);
      logger.error(FILE_NAME+"Error in getShipperConsigneeZones"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,psmt,rs);
    }
    return masterDOB;
  }//@@
  /**
	 * Method Added to Update Flags in QMS_QUOTE_MASTER to indicate that either mail or fax have been
   * sent or a print has been taken of the quote. Status is changed to Pending.
   * 
	 * @param long quoteId,String userId,String operation,boolean compareFlag,int mailStatus
	 * 
	 * @exception EJBException 
	 */
   //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
  //public String  updateSendMailFlag(long quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException
    public String  updateSendMailFlag(String quoteId,String userId,String operation,boolean compareFlag,int mailStatus) throws EJBException
    {
    Connection                connection              = null;
    PreparedStatement         psmt                    = null;
    String                    inactivateQuery         = "";
    String                    query                   = null;    
    try
    {      
      connection        =    this.getConnection();
      inactivateQuery   =   "UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO <> (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
      // query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,QUOTE_STATUS=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";                    
      query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,IE_FLAG=?,QUOTE_STATUS=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";//@@Modified for the WPBN issue-71660                    
      
      if(!compareFlag && "Modify".equalsIgnoreCase(operation))//@@If new Version gets Created, this block should be executed
      {
        psmt              =   connection.prepareStatement(inactivateQuery);
        psmt.setString(1,"I");//@@Making Previous Quotes Inactive      
        psmt.setTimestamp(2,new java.sql.Timestamp((new java.util.Date()).getTime()));
        psmt.setString(3,userId);
        /*psmt.setLong(4,quoteId);
        psmt.setLong(5,quoteId);*/ // @@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
     // @@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
        psmt.setString(4,quoteId);
        psmt.setString(5,quoteId);
     // @@ Ended by subrahmanyam for the Enhancement #146971 on 03/12/2008
        psmt.executeUpdate();
        
        if(psmt!=null)
          psmt.close();
      }
        
      if(mailStatus != 0)//@@If the Quote is Sent to the customer..
      {
        psmt              =   connection.prepareStatement(query);
        psmt.clearParameters();
        psmt.setString(1,"S");
        psmt.setString(2,"E");//@@Added for the WPBN issue-71660    
        psmt.setString(3,"PEN");
        psmt.setTimestamp(4,new java.sql.Timestamp((new java.util.Date()).getTime()));
        psmt.setString(5,userId);
    //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
        /*psmt.setLong(6,quoteId);
        psmt.setLong(7,quoteId);*/
    //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
        psmt.setString(6,quoteId);
        psmt.setString(7,quoteId);
    //@@ Ended by subrahmanyam for the Enhancement #146971 on 03/12/2008
        psmt.executeUpdate();
        //Logger.info(FILE_NAME,"Quote Statuses Updated");
        }
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getShipperConsigneeZones"+e);
      logger.error(FILE_NAME+"Error in getShipperConsigneeZones"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,psmt);
    }
    return "Updated" ;
  }
  
  public CostingMasterDOB getQuoteRateInfo(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean) 
  {
    QMSQuoteDAO  qMSQuoteDAO = null;
    CostingMasterDOB costingMasterDOB = null;
    try
    {
        qMSQuoteDAO = new QMSQuoteDAO();
        
        costingMasterDOB = qMSQuoteDAO.getQuoteRateInfo(costingHDRDOB,loginbean);
        
        
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }

    return costingMasterDOB;
  }

  
  public boolean insertCostingHDR(CostingHDRDOB costingHDRDOB)
  {
    QMSQuoteDAO qMSQuoteDAO =null;
    boolean success = false;
    try
    {
      
      qMSQuoteDAO = new QMSQuoteDAO();
      
      success = qMSQuoteDAO.insertCostingHDR(costingHDRDOB);
      
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }
    return success;
  }
  
  
  
  public String  validateCostingHDR(CostingHDRDOB costingHDRDOB,ESupplyGlobalParameters loginbean)
  {
    QMSQuoteDAO  qMSQuoteDAO = null;
    String errorMsg = "";
    try
    {
      qMSQuoteDAO = new QMSQuoteDAO();
      errorMsg = qMSQuoteDAO.validateCostingHDR(costingHDRDOB);
    }catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }
   return errorMsg; 
  }
  /**
	 * Method Added to fetch the Quote Master Details (1st Screen Information & additional Details) for Quote>>Modify or Quote>>Copy
   * 
	 * @param String quoteId,ESupplyGlobalParameters loginbean
	 * 
	 * @exception EJBException 
	 */
  public QuoteFinalDOB getMasterInfo(String quoteId,ESupplyGlobalParameters loginbean) throws EJBException
	{System.out.println("QUOTE ID IN GET MASTER INFO: "+quoteId);
		Connection                connection   = null;
		PreparedStatement         pStmt        = null;  
		ResultSet                 masterRs     = null;
    PreparedStatement         hStmt        = null;
		ResultSet                 hRs          = null;
    QuoteFinalDOB             finalDOB            = null;
		QuoteMasterDOB            masterDOB           = null;
		QuoteFreightLegSellRates  legDOB              = null;
    QuoteFreightLegSellRates  tempDOB             = null;
    QuoteFlagsDOB             flagsDOB            = null;
		ESupplyDateUtility        eSupplyDateUtility  = new ESupplyDateUtility();
		String                    accessLevel         = "";
		ArrayList                 legDetails          = null;
    ArrayList                 keyList             = new ArrayList();
    int                       keySize             = 0;
    boolean                   isMultiModal        = false;
    
    //String sql          = "SELECT * FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
    //String sql          = "SELECT * FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
   //  String sql          = "SELECT * FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
   // String sql          = "SELECT * FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
     String sql          = "SELECT * FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
    String accessSql    = "SELECT OPER_ADMIN_FLAG FROM FS_FR_TERMINALMASTER WHERE TERMINALID=?";
    String headerQry    = "SELECT * FROM QMS_QUOTE_HF_DTL WHERE QUOTE_ID=?";
    //String chargeQry    = "SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?";
    //String chargeQry    = "SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?";
    //String chargeQry    = "SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?";
 // String chargeQry    = "SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?";
     String chargeQry    = "SELECT CHARGEGROUPID FROM QMS_QUOTE_CHARGEGROUPDTL WHERE QUOTE_ID=?";
    //String contactsQry  = "SELECT CD.SL_NO,CD.CONTACTPERSON FROM QMS_CUST_CONTACTDTL CD,QMS_QUOTE_CONTACTDTL QC WHERE QC.CUSTOMERID=CD.CUSTOMERID AND QC.SL_NO=CD.SL_NO AND QC.QUOTE_ID=? ORDER BY SL_NO";
     String contactsQry  =    "SELECT DISTINCT CD.SL_NO, CD.CONTACTPERSON," + 
		"  CD.CUSTOMERID, CD.CONTACTNO, CD.FAX, CD.EMAILID " +
		"  FROM QMS_CUST_CONTACTDTL CD, QMS_QUOTE_CONTACTDTL QC" + 
		" WHERE QC.CUSTOMERID = CD.CUSTOMERID" + 
		"   AND QC.SL_NO = CD.SL_NO" + 
		" AND CD.ACTIVE_STATUS ='A'" +
		"   AND QC.QUOTE_ID =" + 
		"       (SELECT MAX(QMS.ID) FROM QMS_QUOTE_MASTER QMS WHERE QMS.QUOTE_ID = ?)" + 
		" ORDER BY SL_NO";
    String addressQry   = "SELECT ADDRESSLINE1||' '||ADDRESSLINE2||' '||ADDRESSLINE3||' '||CITY||' '||STATE||' '||ZIPCODE||' '||COUNTRYID ADDRESS FROM FS_ADDRESS WHERE ADDRESSID= ?";
    String legsQry      = "SELECT RL.RT_PLAN_ID,RL.ORIG_LOC,RL.DEST_LOC,RL.SHPMNT_MODE FROM FS_RT_PLAN RP,FS_RT_LEG RL WHERE RP.QUOTE_ID=? AND RP.RT_PLAN_ID=RL.RT_PLAN_ID ORDER BY RL.SERIAL_NO ";
    //String spotRatesQry = "SELECT * FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID = ?"; //@@Commented by subrahmanyam for the pbn id: 207716
    String spotRatesQry = "SELECT * FROM QMS_QUOTE_SPOTRATES WHERE QUOTE_ID = ? ORDER BY LINE_NO";//@@ Added by subrahmanyam for the pbn id: 207716 on 08-Jun-10
    //String notesQry     = "SELECT INTERNAL_NOTES,EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID=?";
    //String notesQry     = "SELECT INTERNAL_NOTES,EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID=?";
   String notesQry     = "SELECT INTERNAL_NOTES,EXTERNAL_NOTES FROM QMS_QUOTE_NOTES WHERE QUOTE_ID=?";
    String commodityQry = "SELECT COMODITYTYPE FROM FS_FR_COMODITYMASTER WHERE COMODITYID=?";
		try
		{
			connection        =   this.getConnection();
			pStmt = connection.prepareStatement(sql);
			pStmt.clearParameters();
			pStmt.setString(1,quoteId);
			pStmt.setString(2,quoteId);
			masterRs = pStmt.executeQuery();
			if(masterRs.next())
			{
        finalDOB  = new QuoteFinalDOB();
				masterDOB = new QuoteMasterDOB();
        
        
        //masterDOB.setQuoteId(masterRs.getLong("QUOTE_ID"));  //@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
        masterDOB.setQuoteId(masterRs.getString("QUOTE_ID"));  //@@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
        masterDOB.setUniqueId(masterRs.getInt("ID"));
        masterDOB.setVersionNo(masterRs.getLong("VERSION_NO"));
        masterDOB.setShipmentMode(masterRs.getInt("SHIPMENT_MODE"));
        //masterDOB.setPreQuoteId(masterRs.getLong("PREQUOTE_ID"));  //@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
        masterDOB.setPreQuoteId(masterRs.getString("PREQUOTE_ID"));  //@@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
        masterDOB.setQuoteStatus(masterRs.getString("QUOTE_STATUS"));//@@Added by VLAKSHMI for the WPWBN issue-167677
        if(masterRs.getString("IU_FLAG")!=null && masterRs.getString("IU_FLAG").equalsIgnoreCase("U"))
         masterDOB.setImpFlag(false);
        else if(masterRs.getString("IU_FLAG")!=null && masterRs.getString("IU_FLAG").equalsIgnoreCase("I"))
         masterDOB.setImpFlag(true);
      
        //eSupplyDateUtility.setPattern(loginbean.getUserPreferences().getDateFormat());
        
        //Timestamp effDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),masterRs.getString("EFFECTIVE_DATE"));
        masterDOB.setEffDate(masterRs.getTimestamp("EFFECTIVE_DATE"));  
        
        if(loginbean!=null)
        {
          masterDOB.setEmpId(loginbean.getEmpId());
          masterDOB.setTerminalCurrency(loginbean.getCurrencyId());
        }
        //Timestamp validTo = null;
        
        //if(masterRs.getString("VALID_TO")!=null)
          //validTo = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),masterRs.getString("VALID_TO"));
        
        masterDOB.setValidTo(masterRs.getTimestamp("VALID_TO"));
        
        //Timestamp createdDate = eSupplyDateUtility.getTimestamp(loginbean.getUserPreferences().getDateFormat(),masterRs.getString("CREATED_DATE"));
        masterDOB.setCreatedDate(masterRs.getTimestamp("CREATED_DATE"));
        masterDOB.setCustDate(masterRs.getTimestamp("CUST_REQUESTED_DATE"));//Added by Rakesh on 23-02-2011 for  Issue:236359
        masterDOB.setCustTime(masterRs.getString("CUST_REQUESTED_TIME"));//Added by Rakesh on 23-02-2011 for  Issue:236359        
        //logger.info("masterRs.getTimestamp "+masterRs.getTimestamp("MODIFIED_DATE"));
        masterDOB.setModifiedDate(masterRs.getTimestamp("MODIFIED_DATE"));
        
        masterDOB.setAccValidityPeriod(masterRs.getInt("ACCEPT_VALIDITYPERIOD"));
      
        if(masterRs.getString("CUSTOMER_ID")!=null && masterRs.getString("CUSTOMER_ID").trim().length()!=0)
          masterDOB.setCustomerId(masterRs.getString("CUSTOMER_ID"));        
        
          masterDOB.setCustomerAddressId(masterRs.getInt("CUSTOMER_ADDRESSID"));        
      
      if(masterRs.getString("CREATED_BY")!=null && masterRs.getString("CREATED_BY").trim().length()!=0)
        masterDOB.setCreatedBy(masterRs.getString("CREATED_BY"));      
      
      if(masterRs.getString("TERMINAL_ID")!=null && masterRs.getString("TERMINAL_ID").trim().length()!=0)
        masterDOB.setTerminalId(masterRs.getString("TERMINAL_ID"));
        
      if(masterRs.getString("SALES_PERSON")!=null && masterRs.getString("SALES_PERSON").trim().length()!=0)
        masterDOB.setSalesPersonCode(masterRs.getString("SALES_PERSON"));
      //@@Added by kameswari for enhancements
      if(masterRs.getString("SALES_PERSON_EMAIL_FLAG")!=null && masterRs.getString("SALES_PERSON_EMAIL_FLAG").trim().length()!=0)
        masterDOB.setSalesPersonFlag(masterRs.getString("SALES_PERSON_EMAIL_FLAG"));
          //@@Added by kameswari for enhancements
      if(masterRs.getString("OVERLENGTH_CARGONOTES")!=null && masterRs.getString("OVERLENGTH_CARGONOTES").trim().length()!=0)
        masterDOB.setOverLengthCargoNotes(masterRs.getString("OVERLENGTH_CARGONOTES"));
        
        //masterDOB.setCargoAcceptance("");
        //masterDOB.setCargoAccPlace("");
      
      if(masterRs.getString("INDUSTRY_ID")!=null && masterRs.getString("INDUSTRY_ID").trim().length()!=0)
        masterDOB.setIndustryId(masterRs.getString("INDUSTRY_ID"));
      
      if(masterRs.getString("COMMODITY_ID")!=null && masterRs.getString("COMMODITY_ID").trim().length()!=0)
        masterDOB.setCommodityId(masterRs.getString("COMMODITY_ID"));
      
      if("Y".equalsIgnoreCase(masterRs.getString("HAZARDOUS_IND")))
        masterDOB.setHazardousInd(true);
      else
        masterDOB.setHazardousInd(false);        
     
        masterDOB.setUnNumber(masterRs.getString("UN_NUMBER"));      
      
        masterDOB.setCommodityClass(masterRs.getString("CLASS"));
      
      if(masterRs.getString("SERVICE_LEVEL_ID")!=null && masterRs.getString("SERVICE_LEVEL_ID").trim().length()!=0)
        masterDOB.setServiceLevelId(masterRs.getString("SERVICE_LEVEL_ID"));
      
      if(masterRs.getString("INCO_TERMS_ID")!=null && masterRs.getString("INCO_TERMS_ID").trim().length()!=0)
        masterDOB.setIncoTermsId(masterRs.getString("INCO_TERMS_ID"));
      
      if(masterRs.getString("QUOTING_STATION")!=null && masterRs.getString("QUOTING_STATION").trim().length()!=0)
        masterDOB.setQuotingStation(masterRs.getString("QUOTING_STATION"));
      
      if(masterRs.getString("ORIGIN_LOCATION")!=null && masterRs.getString("ORIGIN_LOCATION").trim().length()!=0)
        masterDOB.setOriginLocation(masterRs.getString("ORIGIN_LOCATION"));
      
      if(masterRs.getString("SHIPPER_ZIPCODE")!=null && masterRs.getString("SHIPPER_ZIPCODE").trim().length()!=0)
        masterDOB.setShipperZipCode(masterRs.getString("SHIPPER_ZIPCODE"));
        
      if(masterRs.getString("SHIPPERZONES")!=null && masterRs.getString("SHIPPERZONES").trim().length()!=0)
        masterDOB.setShipperZones(masterRs.getString("SHIPPERZONES"));
      
      if(masterRs.getString("SHIPPER_MODE")!=null && masterRs.getString("SHIPPER_MODE").trim().length()!=0)
        masterDOB.setShipperMode(masterRs.getString("SHIPPER_MODE"));
      
      if(masterRs.getString("SHIPPER_CONSOLE_TYPE")!=null && masterRs.getString("SHIPPER_CONSOLE_TYPE").trim().length()!=0)
        masterDOB.setShipperConsoleType(masterRs.getString("SHIPPER_CONSOLE_TYPE"));
            
      if(masterRs.getString("CONSIGNEEZONES")!=null && masterRs.getString("CONSIGNEEZONES").trim().length()!=0)
        masterDOB.setConsigneeZones(masterRs.getString("CONSIGNEEZONES"));
      
      if(masterRs.getString("CONSIGNEE_MODE")!=null && masterRs.getString("CONSIGNEE_MODE").trim().length()!=0)
        masterDOB.setConsigneeMode(masterRs.getString("CONSIGNEE_MODE"));
      
      if(masterRs.getString("CONSIGNEE_CONSOLE_TYPE")!=null && masterRs.getString("CONSIGNEE_CONSOLE_TYPE").trim().length()!=0)
        masterDOB.setConsigneeConsoleType(masterRs.getString("CONSIGNEE_CONSOLE_TYPE"));
      
      if(masterRs.getString("ORIGIN_PORT")!=null &&masterRs.getString("ORIGIN_PORT").trim().length()!=0)
        masterDOB.setOriginPort(masterRs.getString("ORIGIN_PORT"));
      
      if(masterRs.getString("OVERLENGTH_CARGONOTES")!=null  && masterRs.getString("OVERLENGTH_CARGONOTES").trim().length()!=0)
        masterDOB.setOverLengthCargoNotes(masterRs.getString("OVERLENGTH_CARGONOTES"));
             
        masterDOB.setRouteId(Integer.toString(masterRs.getInt("ROUTING_ID")));
      
      if(masterRs.getString("DEST_LOCATION")!=null && masterRs.getString("DEST_LOCATION").trim().length()!=0)
        masterDOB.setDestLocation(masterRs.getString("DEST_LOCATION"));
      
      if(masterRs.getString("CONSIGNEE_ZIPCODE")!=null && masterRs.getString("CONSIGNEE_ZIPCODE").trim().length()!=0)
        masterDOB.setConsigneeZipCode(masterRs.getString("CONSIGNEE_ZIPCODE"));
      
      if(masterRs.getString("DESTIONATION_PORT")!=null && masterRs.getString("DESTIONATION_PORT").trim().length()!=0)
        masterDOB.setDestPort(masterRs.getString("DESTIONATION_PORT"));
      
      if(masterRs.getString("CARGO_ACC_TYPE")!=null && masterRs.getString("CARGO_ACC_TYPE").trim().length()!=0)
        masterDOB.setCargoAcceptance(masterRs.getString("CARGO_ACC_TYPE"));
      
      if(masterRs.getString("CARGO_ACC_PLACE")!=null && masterRs.getString("CARGO_ACC_PLACE").trim().length()!=0)
        masterDOB.setCargoAccPlace(masterRs.getString("CARGO_ACC_PLACE"));
      
      flagsDOB = new QuoteFlagsDOB();
      
      if(masterRs.getString("PN_FLAG")!=null && masterRs.getString("PN_FLAG").trim().length()!=0)
        flagsDOB.setPNFlag(masterRs.getString("PN_FLAG"));
      else
        flagsDOB.setPNFlag("");
        
      if(masterRs.getString("ACTIVE_FLAG")!=null && masterRs.getString("ACTIVE_FLAG").trim().length()!=0)
        flagsDOB.setActiveFlag(masterRs.getString("ACTIVE_FLAG"));
      else
        flagsDOB.setActiveFlag("");
        
      if(masterRs.getString("COMPLETE_FLAG")!=null && masterRs.getString("COMPLETE_FLAG").trim().length()!=0)
        flagsDOB.setCompleteFlag(masterRs.getString("COMPLETE_FLAG"));
      else
        flagsDOB.setCompleteFlag(""); 
        
      if(masterRs.getString("ESCALATION_FLAG")!=null && masterRs.getString("ESCALATION_FLAG").trim().length()!=0)
        flagsDOB.setEscalationFlag(masterRs.getString("ESCALATION_FLAG"));
      else
        flagsDOB.setEscalationFlag("");
        
      if(masterRs.getString("UPDATE_FLAG")!=null && masterRs.getString("UPDATE_FLAG").trim().length()!=0)
        flagsDOB.setUpdateFlag(masterRs.getString("UPDATE_FLAG"));
      else
        flagsDOB.setUpdateFlag("");
        
      if(masterRs.getString("QUOTE_STATUS")!=null && masterRs.getString("QUOTE_STATUS").trim().length()!=0)
        flagsDOB.setQuoteStatusFlag(masterRs.getString("QUOTE_STATUS"));
      else
        flagsDOB.setQuoteStatusFlag("");
        
      if(masterRs.getString("IE_FLAG")!=null && masterRs.getString("IE_FLAG").trim().length()!=0)
        flagsDOB.setInternalExternalFlag(masterRs.getString("IE_FLAG"));
      else
        flagsDOB.setInternalExternalFlag("");
      
      if(masterRs.getString("SENT_FLAG")!=null && masterRs.getString("SENT_FLAG").trim().length()!=0)
        flagsDOB.setSentFlag(masterRs.getString("SENT_FLAG"));
      else
        flagsDOB.setSentFlag("");
        
      if(masterRs.getString("EMAIL_FLAG")!=null && masterRs.getString("EMAIL_FLAG").trim().length()!=0)
        flagsDOB.setEmailFlag(masterRs.getString("EMAIL_FLAG"));
      else
        flagsDOB.setEmailFlag("");
        
      if(masterRs.getString("FAX_FLAG")!=null && masterRs.getString("FAX_FLAG").trim().length()!=0)
        flagsDOB.setFaxFlag(masterRs.getString("FAX_FLAG"));
      else
        flagsDOB.setFaxFlag("");
      
      if(masterRs.getString("PRINT_FLAG")!=null && masterRs.getString("PRINT_FLAG").trim().length()!=0)
        flagsDOB.setPrintFlag(masterRs.getString("PRINT_FLAG"));
      else
        flagsDOB.setPrintFlag("");
      
      finalDOB.setReportingOfficer(masterRs.getString("ESCALATED_TO"));
      
      if(finalDOB.getReportingOfficer()!=null && finalDOB.getReportingOfficer().trim().length()!=0)
        finalDOB.setEscalatedTo(finalDOB.getReportingOfficer());
      
      finalDOB.setFlagsDOB(flagsDOB);
      
      //Added By Kishore Podili on 22-Feb-11 for  Weight Break in Single Quote
      	if(masterRs.getString("MULTI_QUOTE_WEIGHT_BREAK")!=null && masterRs.getString("MULTI_QUOTE_WEIGHT_BREAK").trim().length()!=0)
      	  masterDOB.setWeightBreak(masterRs.getString("MULTI_QUOTE_WEIGHT_BREAK"));
      	else
      		masterDOB.setWeightBreak("");
      		
      
      
      hStmt =  connection.prepareStatement(accessSql);
      hStmt.setString(1,masterDOB.getTerminalId());
      hRs   =  hStmt.executeQuery();
      
      if(hRs.next())
      {
        masterDOB.setAccessLevel(hRs.getString("OPER_ADMIN_FLAG"));
      }
      
      if(hRs != null)
        hRs.close();
      if(hStmt!=null)
        hStmt.close();
      hStmt  = connection.prepareStatement(chargeQry);
      hStmt.clearParameters();      
      hStmt.setInt(1,masterRs.getInt("ID"));
      hRs = hStmt.executeQuery();
      ArrayList chargeGrps = new ArrayList();
      
      while(hRs.next())
      {
       chargeGrps.add(hRs.getString("CHARGEGROUPID"));
      }
      String[] chargeGrp = null;
      
      if(chargeGrps.size()>0)
        chargeGrp = new String[chargeGrps.size()];
     int chargGrpSize	=	chargeGrps.size();
      for(int i=0;i<chargGrpSize;i++)
      {
        chargeGrp[i] = (String)chargeGrps.get(i);
      }
      masterDOB.setChargeGroupIds(chargeGrp);   
      
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close();
          
      hStmt  = connection.prepareStatement(headerQry);
      hStmt.clearParameters();      
      hStmt.setInt(1,masterRs.getInt("ID"));
      hRs = hStmt.executeQuery();
      
      ArrayList headerFooter = new ArrayList();
      ArrayList content      = new ArrayList();
      ArrayList level        = new ArrayList();
      ArrayList align        = new ArrayList();
      
      while(hRs.next())
      {      
        headerFooter.add(hRs.getString("HEADER")!=null?hRs.getString("HEADER"):"");       
        content.add(hRs.getString("CONTENT")!=null?hRs.getString("CONTENT"):"");       
        level.add(hRs.getString("CLEVEL")!=null?hRs.getString("CLEVEL"):"");      
        align.add(hRs.getString("ALIGN")!=null?hRs.getString("ALIGN"):"");
      }
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close();
      
      String[] contentArray            = null;
      String[] headerFooterArray       = null;
      String[] levelArray              = null;
      String[] alignArray              = null;
      
      if(content.size()>0)
      {
        contentArray            = new String[content.size()];
        headerFooterArray       = new String[headerFooter.size()];
        levelArray              = new String[level.size()];
        alignArray              = new String[align.size()];
      }
     int contentSize	=	content.size();
      for(int i=0;i<contentSize;i++)
      {
        contentArray[i]       = (String)content.get(i);
        headerFooterArray[i]  = (String)headerFooter.get(i);
        levelArray[i]         = (String)level.get(i);
        alignArray[i]         = (String)align.get(i);
      }
      
      masterDOB.setHeaderFooter(headerFooterArray); 
      masterDOB.setContentOnQuote(contentArray);
      masterDOB.setLevels(levelArray);
      masterDOB.setAlign(alignArray);
      
      hStmt  = connection.prepareStatement(contactsQry);
      hStmt.clearParameters();     
     // hStmt.setInt(1,masterRs.getInt("ID"));
      hStmt.setString(1,masterDOB.getQuoteId());
      hRs = hStmt.executeQuery();
      ArrayList contactIds   = new ArrayList();
      ArrayList contactNames = new ArrayList();
    //Added to get Attention To values in Modify Case
      ArrayList contactNo = new ArrayList();
      ArrayList fax = new ArrayList();
      ArrayList emailId = new ArrayList();
    //Ended to get Attention To values in Modify Case
      
      while(hRs.next())
      {
       contactIds.add(hRs.getString("SL_NO"));
       contactNames.add(hRs.getString("CONTACTPERSON"));
     //Added to get Attention To values in Modify Case
       contactNo.add(hRs.getString("CONTACTNO"));
       fax.add(hRs.getString("FAX"));
       emailId.add(hRs.getString("EMAILID"));
     //Ended to get Attention To values in Modify Case
      }
      
      String[] contactIdsArray    =   null;
      String[] contactNamesArray    =   null;
    //Added to get Attention To values in Modify Case
      String[] contactNoArray    =   null;
      String[] faxArray    =   null;
      String[] emailIdArray    =   null;
    //Ended to get Attention To values in Modify Case
      
      if(contactIds.size()>0)
      {
          contactIdsArray   = new String[contactIds.size()];
          contactNamesArray = new String[contactIds.size()];
          //Added to get Attention To values in Modify Case
          contactNoArray 	= new String[contactIds.size()];
          faxArray 			= new String[contactIds.size()];
          emailIdArray 		= new String[contactIds.size()];
        //Ended to get Attention To values in Modify Case
      }
      int contactIdsSize	=	contactIds.size();
      for(int i=0;i<contactIdsSize;i++)
      {
        contactIdsArray[i]    = (String)contactIds.get(i);
        contactNamesArray[i]  = (String)contactNames.get(i);
      //Added to get Attention To values in Modify Case
        contactNoArray[i] 	  = (String)contactNo.get(i);
        faxArray[i] 		  = (String)fax.get(i);
        emailIdArray[i]  	  = (String)emailId.get(i);
      //Ended to get Attention To values in Modify Case
      }
      masterDOB.setCustomerContacts(contactIdsArray);     
      masterDOB.setCustContactNames(contactNamesArray);
    //Added to get Attention To values in Modify Case
      masterDOB.setCustomerContactsEmailIds(emailIdArray);
      masterDOB.setCustomerContactsFax(faxArray);
      masterDOB.setCustomerContactNo(contactNoArray);
    //Ended to get Attention To values in Modify Case
      
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close();   

     masterDOB.setCustomerAddressId(masterRs.getInt("CUSTOMER_ADDRESSID"));
        
      hStmt  = connection.prepareStatement(addressQry);
      hStmt.clearParameters();
      hStmt.setInt(1,masterRs.getInt("CUSTOMER_ADDRESSID"));     
      hRs = hStmt.executeQuery();      
      
      while(hRs.next())
      {
        masterDOB.setCustomerAddress(hRs.getString("ADDRESS"));
      }
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close(); 
      
      finalDOB.setMasterDOB(masterDOB);
      
     
      /*    
      if("Add".equals(operation))
          masterDOB.setModifiedDate(new java.sql.Timestamp((new java.util.Date()).getTime()));
        */
     
      int legSize = 0;
      hStmt  = connection.prepareStatement(legsQry);
      hStmt.clearParameters();     
     // hStmt.setInt(1,masterRs.getInt("QUOTE_ID"));   // @@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      hStmt.setString(1,masterRs.getString("QUOTE_ID"));   // @@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      hRs = hStmt.executeQuery();
      legDetails = new ArrayList();
       
      while(hRs.next())
      {
        legDOB  = new QuoteFreightLegSellRates();
        legDOB.setOrigin(hRs.getString("ORIG_LOC"));
        legDOB.setDestination(hRs.getString("DEST_LOC"));
        legDOB.setShipmentMode(hRs.getInt("SHPMNT_MODE"));
        keyList.add(""+legDOB.getShipmentMode());
        masterDOB.setRouteId(hRs.getString("RT_PLAN_ID"));
        //Logger.info("",legDOB.getOrigin()+"--"+legDOB.getDestination()+"--"+legDOB.getShipmentMode()+"--"+masterDOB.getRouteId());
        legDetails.add(legDOB);    
      }
      legSize = legDetails.size();
      //Logger.info("legDetails.size() -- ",""+legDetails.size());           
      for(int i=0;i<legSize;i++)
      {
        tempDOB = (QuoteFreightLegSellRates)legDetails.get(i);
        for(int j=0;j<keySize;j++)
        {
          if(i==j)
            break;
          else
          {
            if(!((String)keyList.get(j)).equalsIgnoreCase(""+tempDOB.getShipmentMode()))
                isMultiModal  = true;
          }
        }
      }
      finalDOB.setLegDetails(legDetails);
      finalDOB.setMasterDOB(masterDOB);
      finalDOB.setMultiModalQuote(isMultiModal);
      finalDOB.setMlutiQuoteLaneNo(masterRs.getString("multi_quote_lane_no"));
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close(); 
      
      hStmt  = connection.prepareStatement(spotRatesQry);
      hStmt.clearParameters();     
      hStmt.setInt(1,masterRs.getInt("ID"));
      hRs = hStmt.executeQuery();
      int j=0;
      Hashtable spotRateDetails  = null;
      ArrayList weightBreakSlabs = null;
      HashMap   legDOBS          = new HashMap();
      
      spotRateDetails  = new Hashtable();//only when there are weight breaks and corresponding rates initialise the spotRateDetails
      weightBreakSlabs = new ArrayList();//only when there are weight breaks initialise the weightBreakSlabs
      int laneNo = 0;
      int lineNo = 0;
      double[] rateDetail = null;
      
      while(hRs.next())
      {
          legDOB  = (QuoteFreightLegSellRates)legDetails.get(hRs.getInt("LANE_NO"));
           legDOB.setServiceLevel(hRs.getString("SERVICELEVEL"));
           legDOB.setUom(hRs.getString("UOM"));
           legDOB.setDensityRatio(hRs.getString("DENSITY_CODE"));
           legDOB.setSpotRatesType(hRs.getString("WEIGHT_BREAK"));
           legDOB.setCurrency(hRs.getString("CURRENCYID"));//@@Added by kameswari for the WPBN issue-30908
           rateDetail = new double[4];//for storing upper bound,lower bound,rate,margin respectively
           if(legDOB.getWeightBreaks()!=null)
           {
             weightBreakSlabs =legDOB.getWeightBreaks();              
             weightBreakSlabs.add(hRs.getString("WEIGHT_BREAK_SLAB"));
            legDOB.setWeightBreaks(weightBreakSlabs);
             
             spotRateDetails = legDOB.getSpotRateDetails();
             
             rateDetail[0] = hRs.getDouble("UPPER_BOUND");//upper bound
             rateDetail[1] = hRs.getDouble("LOWER_BOUND");//lower bound
             rateDetail[2] = hRs.getDouble("CHARGE_RATE");//rate
             //rateDetail[3] = hRs.getDouble("");
             spotRateDetails.put(hRs.getString("WEIGHT_BREAK_SLAB"),rateDetail);
             legDOB.setSpotRateDetails(spotRateDetails);
             legDOB.setSpotRatesFlag(true);
             legDetails.set(hRs.getInt("LANE_NO"),legDOB);
           }
           else
           {
             weightBreakSlabs = new  ArrayList();
             weightBreakSlabs.add(hRs.getString("WEIGHT_BREAK_SLAB"));
             legDOB.setWeightBreaks(weightBreakSlabs);
             spotRateDetails  = new  Hashtable();
             rateDetail[0] = hRs.getDouble("UPPER_BOUND");//upper bound
             rateDetail[1] = hRs.getDouble("LOWER_BOUND");//lower bound
             rateDetail[2] = hRs.getDouble("CHARGE_RATE");//rate
             spotRateDetails.put(hRs.getString("WEIGHT_BREAK_SLAB"),rateDetail);
             legDOB.setSpotRateDetails(spotRateDetails);
             legDOB.setSpotRatesFlag(true);
             legDetails.set(hRs.getInt("LANE_NO"),legDOB);
           }  
      }
       if(legDetails!=null)
         finalDOB.setLegDetails(legDetails);
      
      if(hRs!=null)hRs.close();
      if(hStmt!=null)hStmt.close(); 
      
      hStmt  = connection.prepareStatement(notesQry);
      hStmt.clearParameters();     
      hStmt.setInt(1,masterRs.getInt("ID"));
      hRs = hStmt.executeQuery();
      
      ArrayList internal  = new ArrayList();
      ArrayList external  = new ArrayList();
      
        String[]  internalArr = null;
      String[]  externalArr = null;
      String[]  arr = null;
      String[]  intarr = null;
      String intarr1 = "";
       String  arr1 = "";

      
    
        while(hRs.next())
        {
          internal.add((hRs.getString("INTERNAL_NOTES")!=null)?hRs.getString("INTERNAL_NOTES"):"");
          external.add((hRs.getString("EXTERNAL_NOTES")!=null)?hRs.getString("EXTERNAL_NOTES"):"");
        }
      
        if(internal.size()>0)
        {
          internalArr = new String[internal.size()];
          externalArr = new String[external.size()];

        }
        
        if(externalArr!=null)
        {
        	int extArrLen	=	externalArr.length;
          for(int i=0;i<extArrLen;i++)
          {
            internalArr[i]  = (String)internal.get(i);
            internalArr[i] =  internalArr[i] .trim();
            if(internalArr[i].length()>0)
            {
            intarr = internalArr[i].split("");
            for( j=0;j<intarr.length-1;j++)
            {
             
                 if((intarr[j].trim()).length()>0)
                {
                  intarr1 =intarr1.concat(intarr[j].trim());
                }
             else
             {
               if((intarr[j+1].trim()).length()>0)
               {
                  intarr1 =intarr1.concat(" ");
               }
             }
            }
                  intarr1 =intarr1.concat(intarr[intarr.length-1]);
                  internalArr[i] = intarr1;
                  intarr1 ="";
            }
            arr1 =  "";
            externalArr[i]  = (String)external.get(i);
            externalArr[i]  = externalArr[i].trim();
            if(  externalArr[i].length()>0)
            {
            arr = externalArr[i].split("");
            for( j=0;j<arr.length-1;j++)
            {
             
                 if((arr[j].trim()).length()>0)
                {
                  arr1 =arr1.concat(arr[j].trim());
                }
             else
             {
               if((arr[j+1].trim()).length()>0)
               {
                  arr1 =arr1.concat(" ");
               }
             }
            }
                  arr1 =arr1.concat(arr[arr.length-1]);
                  externalArr[i] = arr1;
            arr1 =  "";
            }
           }
        }

        
        finalDOB.setExternalNotes(externalArr);
        finalDOB.setInternalNotes(internalArr);
        
        if(hRs!=null)hRs.close();
        if(hStmt!=null)hStmt.close(); 
        
        hStmt   =   connection.prepareStatement(commodityQry);
        hStmt.setString(1,masterDOB.getCommodityId());
        
        hRs     =   hStmt.executeQuery();
        
        while(hRs.next())
        {
          masterDOB.setCommodityType(hRs.getString("COMODITYTYPE"));
        }
      }
    }
		catch(SQLException se)
		{
      se.printStackTrace();
      throw new EJBException();
		}
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,pStmt,hRs);
      ConnectionUtil.closeConnection(connection,hStmt,masterRs);
    }
    return finalDOB;
	}

 public StringBuffer validateQuoteId(QuoteMasterDOB masterDOB) throws	EJBException
  {
    Connection        connection   = null;
    PreparedStatement pstmt        = null;
		//Statement         stmt         = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.  
    ResultSet         rs           = null;
		//ResultSet         masterRs     = null;   //Commented By RajKumari on 27-10-2008 for Connection Leakages.  
    boolean           validFlag    = false;
    StringBuffer      errorMessage = new StringBuffer("");
    int               count        = 0;
    StringBuffer      sql          = new StringBuffer("");
    StringBuffer      terminalQuery= new StringBuffer("");
    //long              quoteId      = 0;    //@@ COmmented by subrahmanyam for the enhancement #146971 on 03/12/2008
    String              quoteId      = null;    //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
    String            customerId   = null;
    String            origin       = null;  
    String            destination  = null;
    String            terminalId   = null;
    String            empId        = null;
    String            basisFlag    = null;
    String            operation    = null;
    String            accessLevel  = null;
    String            sqlQuery     = null;
    String            subQuery     = "";
    String            locSubQuery  = "";
    boolean           isOrigin     = false;
    boolean           isCustomer   = false;
    int               shipmentMode = 0;
     String customerName ="";
       String quoteStatus="";
        String quoteActive="";
       String shipModeStr=null;
    
		try
		{
			
      quoteId       =     masterDOB.getQuoteId().trim();
      customerId    =     masterDOB.getCustomerId();
      origin        =     masterDOB.getOriginLocation();
      destination   =     masterDOB.getDestLocation();
      terminalId    =     masterDOB.getTerminalId();
      accessLevel   =     masterDOB.getAccessLevel();
      empId         =     masterDOB.getUserId();
      basisFlag     =     masterDOB.getBuyRatesPermission();
      operation     =     masterDOB.getOperation();
     // shipmentMode  =     masterDOB.getShipmentMode();
      customerName  =     masterDOB.getCompanyName();
      quoteStatus   =     masterDOB.getQuoteStatus();
      quoteActive =  masterDOB.getActiveFlag();
      if(masterDOB.getShipmentMode()==1)
         // shipModeStr = " AND SHIPMENTMODE IN (1,3,5,7)";
         shipModeStr = "1,3,5,7";
        else if(masterDOB.getShipmentMode()==2)
          shipModeStr = "2,3,6,7";
        else if(masterDOB.getShipmentMode()==4)
          shipModeStr = "4,5,6,7";
      //sql.append("SELECT QUOTE_ID,BASIS,ESCALATION_FLAG,ESCALATED_TO FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=").append(quoteId);
      if("H".equalsIgnoreCase(accessLevel))
      {
          sql.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
          terminalQuery.append("(SELECT TERMINALID FROM FS_FR_TERMINALMASTER)");
      }
      else
      {
          if(!"Modify".equalsIgnoreCase(operation) && !"Copy".equalsIgnoreCase(operation))
          {
            /*sql.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '").append(terminalId).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ");*/


            sql.append( "(SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE  PARENT_TERMINAL_ID =(SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID='")
                      .append(terminalId).append("' )UNION SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
                      .append(terminalId).append("'")
                      .append( " UNION ")
                      .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                      .append( " UNION ");
          }

          else
            sql.append("(");
            
//commented by subrahmanyam for CR_Enhancement_167669 on 26/May/09     
    /*            
          sql.append(" SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
          sql.append("START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(terminalId).append("')");
  */
//Added by subrahmanyam for CR_Enhancement_167669 on 26/May/09 
      if("A".equalsIgnoreCase(accessLevel))
      {
          sql.append(" SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
          sql.append("START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(terminalId).append("')");
      }
      else
      {
            sql.append(" SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID ");
            sql.append("START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("' UNION SELECT TERMINALID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='").append(terminalId).append("'");
            sql.append("UNION SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN FRT WHERE PARENT_TERMINAL_ID IN (SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN FRT WHERE FRT.CHILD_TERMINAL_ID='").append(terminalId).append("'))");
          
      }
//ended by subrahmanyam for CR_Enhancement_167669 on 26/May/09             
          terminalQuery.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '").append(terminalId).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT '").append(terminalId).append("' TERM_ID FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '").append(terminalId).append("')");
      }
      
      if("Modify".equalsIgnoreCase(operation))
      {
          sqlQuery      = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
     
      }
      else if("View".equalsIgnoreCase(operation)||"Copy".equalsIgnoreCase(operation))
      {
        sqlQuery      = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                  "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A'))  AND TERMINAL_ID IN "+sql.toString();
      }
      
      connection    =   this.getConnection();
            
      pstmt         = connection.prepareStatement(sqlQuery);
      //pstmt.setLong(1,quoteId);  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
      pstmt.setString(1,quoteId.trim());  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
      
      
      rs            = pstmt.executeQuery();
      
      if(rs.next())
          count = rs.getInt("NO_ROWS");
      
      if(count==0)
          errorMessage.append("Quote Id is Invalid. Please Enter a Valid & Active Quote Id to Continue.<BR>");
      else
      {
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
        
        count = 0;
        
        if("View".equalsIgnoreCase(operation))
        {
           /* sqlQuery      =  "SELECT COUNT(*)ROWS_NO FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                              "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A')) AND COMPLETE_FLAG='C'";*/
      sqlQuery      =  "SELECT COUNT(*)ROWS_NO FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                              "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A')) AND COMPLETE_FLAG='C'";

            pstmt         = connection.prepareStatement(sqlQuery);
            //pstmt.setLong(1,quoteId);   //Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
            pstmt.setString(1,quoteId);   //Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
            rs            = pstmt.executeQuery();
            if(rs.next())
            count = rs.getInt("ROWS_NO");
            if(count==0)
            errorMessage.append("The Selected Quote Is Incomplete. Please Enter/Select a Valid Quote Id.<BR>");
        }
        if(rs!=null)
          rs.close();
        if(pstmt!=null)
          pstmt.close();
        count = 0;
        
        /*@@Commented by Kameswari for the WPBN issue-26514*/
      //  if(shipmentMode !=0)
      if((!"".equals(shipModeStr) && shipModeStr!=null))
        {
            sqlQuery   = " SELECT COUNT(*)NO_ROWS FROM FS_RT_PLAN A,FS_RT_LEG B WHERE A.QUOTE_ID= ? AND "+
                         " A.RT_PLAN_ID=B.RT_PLAN_ID AND B.SHPMNT_MODE IN("+shipModeStr+") ";
            
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,quoteId);
            //pstmt.sets(2,shipModeStr);
            
            rs        = pstmt.executeQuery();
            if(rs.next())
                count = rs.getInt("NO_ROWS");
            
            if(count==0)
              errorMessage.append("Please Enter A Valid Quote Id For the Selected Shipment Mode.<BR>");
            
            if(rs!=null)
              rs.close();
            if(pstmt!=null)
              pstmt.close();
            count = 0;
        }
        
               if(customerName!=null && customerName.trim().length()!=0)
        {
            isCustomer  = true;
            //sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE COMPANYNAME=? AND TERMINALID IN "+terminalQuery;
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE COMPANYNAME=? AND CUSTOMERID=?";
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,customerName);
            pstmt.setString(2,customerId);
            rs        = pstmt.executeQuery();
            if(rs.next())
                count = rs.getInt("NO_ROWS");
            
            if(count==0)
              errorMessage.append("COMPANYNAME is Invalid.<BR>");          
            else
            {
              if(rs!=null)
                rs.close();
              if(pstmt!=null)
                pstmt.close();
              count = 0;
           if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
           {
               sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=?  AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,customerId);
              rs        = pstmt.executeQuery();
           }
           else{
              sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,customerId);
              rs        = pstmt.executeQuery();
           }
              if(rs.next())
                count = rs.getInt("NO_ROWS");
            
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" & COMPANYNAME ").append(customerName).append(" Do Not Match.<BR>");
            }
        }
                if(quoteStatus!=null && quoteStatus.trim().length()!=0)
        {
         if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
           {
             sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND QUOTE_STATUS=?  AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,quoteStatus);
              rs        = pstmt.executeQuery();
           }
           else{
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND QUOTE_STATUS=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,quoteStatus);
              rs        = pstmt.executeQuery();
           }
              if(rs.next())
                count = rs.getInt("NO_ROWS");
            
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" &STATUS ").append(quoteStatus).append(" Do Not Match.<BR>");
          
        }
        
          if(quoteActive!=null && quoteActive.trim().length()!=0)
        {
        
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG=? AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,quoteActive);
              rs        = pstmt.executeQuery();
              if(rs.next())
                count = rs.getInt("NO_ROWS");
            
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" &ACTIVEFLAG ").append(quoteActive).append(" Do Not Match.<BR>");
          
        }
      /*  if(customerId!=null && customerId.trim().length()!=0)
        {
            isCustomer  = true;
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? AND TERMINALID IN "+terminalQuery;
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,customerId);
            rs        = pstmt.executeQuery();
            if(rs.next())
                count = rs.getInt("NO_ROWS");
            
            if(count==0)
              errorMessage.append("Customer Id is Invalid.<BR>");          
            else
            {
              if(rs!=null)
                rs.close();
              if(pstmt!=null)
                pstmt.close();
              count = 0;
              
              sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND CUSTOMER_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+sql.toString();
              pstmt     = connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,customerId);
              rs        = pstmt.executeQuery();
              if(rs.next())
                count = rs.getInt("NO_ROWS");
            
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" & Customer Id ").append(customerId).append(" Do Not Match.<BR>");
            }
        }*/
        
        if(origin!=null && origin.trim().length()!=0)
        {
            if(rs!=null)
                rs.close();
            if(pstmt!=null)
                pstmt.close();
            count = 0;
            isOrigin  = true;
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,origin);
            rs        = pstmt.executeQuery();
            
            if(rs.next())
              count = rs.getInt("NO_ROWS");
            if(count  == 0)
              errorMessage.append("Origin Location is Invalid.<BR>");
            else
            {
              if(rs!=null)
                rs.close();
              if(pstmt!=null)
                pstmt.close();
              count = 0;
              if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
           {
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?  AND ORIGIN_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
              pstmt     =  connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,origin);
              rs  = pstmt.executeQuery();
           }else{
              sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND ORIGIN_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
              pstmt     =  connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,origin);
              rs  = pstmt.executeQuery();
           }  
              if(rs.next())
                count = rs.getInt("NO_ROWS");
              
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" & Origin Location ").append(origin).append(" Do Not Match.<BR>");
            }
        }
        
        if(destination!=null && destination.trim().length()!=0)
        {
            if(rs!=null)
              rs.close();
            if(pstmt!=null)
              pstmt.close();
            count = 0;
              
            sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM  FS_FR_LOCATIONMASTER WHERE LOCATIONID =? AND (INVALIDATE ='F' OR INVALIDATE IS NULL) ";
            pstmt     = connection.prepareStatement(sqlQuery);
            pstmt.setString(1,destination);
            rs        = pstmt.executeQuery();
            
            if(rs.next())
              count = rs.getInt("NO_ROWS");
            if(count  == 0)
              errorMessage.append("Destination Location is Invalid.<BR>");
            else
            {
              if(rs!=null)
                rs.close();
              if(pstmt!=null)
                pstmt.close();
              count = 0;
                 if("NAC".equalsIgnoreCase(quoteStatus) || "ACC".equalsIgnoreCase(quoteStatus))  
           {
             sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?  AND DEST_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
              pstmt     =  connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,destination);              
              rs  = pstmt.executeQuery();
           }else{
              sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND DEST_LOCATION=? AND TERMINAL_ID IN "+sql.toString();
              pstmt     =  connection.prepareStatement(sqlQuery);
              pstmt.setString(1,quoteId);
              pstmt.setString(2,destination);              
              rs  = pstmt.executeQuery();
           }  
              if(rs.next())
                count = rs.getInt("NO_ROWS");
              
              if(count==0)
                errorMessage.append("Quote Id ").append(quoteId).append(" & Destination Location ").append(destination).append(" Do Not Match.<BR>");
            }
        }
        //@@End - WPBN issue-26514
        
        if("Modify".equalsIgnoreCase(operation))
        {
          
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
          count = 0;
          
          //sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED QU WHERE QM.QUOTE_ID=? AND QM.ID=QU.QUOTE_ID AND QU.CONFIRM_FLAG IS NULL";
              sqlQuery  = "SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER QM,QMS_QUOTES_UPDATED QU WHERE QM.QUOTE_ID=? AND QM.ID=QU.QUOTE_ID AND QU.CONFIRM_FLAG IS NULL";
           pstmt     = connection.prepareStatement(sqlQuery);
          //pstmt.setLong(1,quoteId); // Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
          pstmt.setString(1,quoteId); // Added by subrahmanyam for the enhancement #146971 on 03/12/2008
          
          rs        = pstmt.executeQuery();
          
          if(rs.next())
            count = rs.getInt("NO_ROWS");
          if(count > 0)
            errorMessage.append("This Quote Has Been Updated. Please Confirm this Quote Using the Updated Quotes Report.<BR>");
          
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
            

          sqlQuery  = "SELECT UM.USERNAME FROM QMS_QUOTE_MASTER QM,FS_USERMASTER UM WHERE QUOTE_ID=? AND QM.ESCALATED_TO =UM.EMPID AND  ACTIVE_FLAG='A' AND ESCALATION_FLAG='Y' AND ESCALATED_TO <> ?";
          pstmt     = connection.prepareStatement(sqlQuery);
          //pstmt.setLong(1,quoteId); // @@ COmmented by subrahmanyam for the Enhancement #146971 on 03/12/2008
          pstmt.setString(1,quoteId); // @@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
          pstmt.setString(2,empId);
          
          rs        = pstmt.executeQuery();
          
          if(rs.next())
            errorMessage.append("This Quote has been Escalated to "+rs.getString("USERNAME")+".<BR>");
          
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
          
          count = 0;
            
          sqlQuery  = " SELECT COUNT(*)NO_ROWS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ? AND ACTIVE_FLAG='A' AND QUOTE_STATUS IN ('ACC','NAC')";
          pstmt     = connection.prepareStatement(sqlQuery);
         // pstmt.setLong(1,quoteId); //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
         pstmt.setString(1,quoteId); //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
          
          rs        = pstmt.executeQuery();
          
          if(rs.next())
            count   = rs.getInt("NO_ROWS");
          
          if(count > 0)
            errorMessage.append(" Positive/Negative Quotes Cannot Be Modified.<BR> ");
          
          if(rs!=null)
            rs.close();
          if(pstmt!=null)
            pstmt.close();
        }
        
        //sqlQuery  = "SELECT BASIS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+terminalQuery;
        if("Modify".equalsIgnoreCase(operation))
      {
          sqlQuery      = "SELECT BASIS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A' AND TERMINAL_ID IN "+terminalQuery;
        
      }
      else if( "View".equalsIgnoreCase(operation) ||"Copy".equalsIgnoreCase(operation))
      {
        sqlQuery      = "SELECT BASIS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ((QUOTE_STATUS IN ('ACC', 'NAC') AND ACTIVE_FLAG IN ('A', 'I')) OR " +
                        "(QUOTE_STATUS NOT IN ('ACC', 'NAC') AND ACTIVE_FLAG = 'A'))  AND TERMINAL_ID IN "+terminalQuery;
      }
        pstmt     = connection.prepareStatement(sqlQuery);
        //pstmt.setLong(1,quoteId); //@@ Commented by subrahmanyam for the Enhancement #146971 on 03/12/2008
        pstmt.setString(1,quoteId); //@@ Added by subrahmanyam for the Enhancement #146971 on 03/12/2008
        rs        =  pstmt.executeQuery();
        if(rs.next())
        {
          if("N".equalsIgnoreCase(basisFlag) && "Y".equalsIgnoreCase(rs.getString("BASIS")))
            errorMessage.append("You Do Not Have Sufficient Privileges to "+masterDOB.getOperation()+" this Quote.<BR>");
        }
        
        sqlQuery =  "  ";
      }
    }
    catch(SQLException se)
		{
      se.printStackTrace();
      throw new EJBException();
		}
    catch(Exception e)
    {
      e.printStackTrace();
      throw new EJBException();
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,null,null); //Modified By RajKumari on 27-10-2008 for Connection Leakages.
      ConnectionUtil.closePreparedStatement(pstmt,rs);   //Added By RajKumari on 27-10-2008 for Connection Leakages.
    }
    return errorMessage;
  }
   public ArrayList  getQuoteGroups(String[] quoteIds,ESupplyGlobalParameters loginbean) throws EJBException
   {
    Connection                connection              = null;
    PreparedStatement         psmt                    = null;
    String                    query                   = null;    
    ResultSet                 rs                      = null; 
    ArrayList                 chargeGroups            = new ArrayList();
    try
    {      
       connection        =    this.getConnection();
       query             =   "SELECT  M.QUOTE_ID,C.CHARGEGROUPID  FROM QMS_QUOTE_MASTER M,QMS_QUOTE_CHARGEGROUPDTL C WHERE M.QUOTE_ID=? AND M.VERSION_NO=(SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) AND M.ID=C.QUOTE_ID";                    
       psmt              =   connection.prepareStatement(query);
       int quoteIdLen	=	quoteIds.length;
       for(int m=0;m<quoteIdLen;m++)
       {          
          psmt.clearParameters();
         //@@ Commented by subrahmanyam for the enhancement #146971 on 10/12/2008          
          /*psmt.setInt(1,Integer.parseInt(quoteIds[m]));
          psmt.setInt(2,Integer.parseInt(quoteIds[m]));*/
         //@@ Added by subrahmanyam for the enhancement #146971 on 10/12/2008          
          psmt.setString(1,quoteIds[m]);
          psmt.setString(2,quoteIds[m]);

          
          if(rs!=null)
            rs.close();
          
          rs  = psmt.executeQuery();
          
          while(rs.next())
           chargeGroups.add(rs.getString(1)+","+rs.getString(2)); 
       } 
      
      }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getQuoteGroups"+e);
      logger.error(FILE_NAME+"Error in getQuoteGroups"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,psmt,rs);
    }
    return  chargeGroups;
  }

  public ArrayList getQuoteGroupIds(QuoteMasterDOB masterDOB) throws EJBException
   {
    Connection                connection              = null;
    Statement                 stmt                    = null;
    StringBuffer              query                   = new StringBuffer("");    
    ResultSet                 rs                      = null; 
    ArrayList                 quoteIds                = new ArrayList();
    String                    origins                 = "";
    String                    destinations            = "";
    try
    {      
         String[] destLocIds   = masterDOB.getDestLocation().split(","); 
         String[] originLocIds = masterDOB.getOriginLocation().split(",");
         int orgLocIdLen	=	originLocIds.length;
         for(int i=0;i<orgLocIdLen;i++)
        {
          if((i+1)==originLocIds.length)
              origins  = origins + "'"+originLocIds[i]+"'";
          else        
              origins  = origins + "'"+originLocIds[i]+"',";
        }
        int destLocIdsLen	=	destLocIds.length;
        for(int i=0;i<destLocIdsLen;i++)
        {
          if((i+1)==destLocIds.length)
              destinations  = destinations + "'"+destLocIds[i]+"'";
          else        
              destinations  = destinations + "'"+destLocIds[i]+"',";
        }
         
         connection        =    this.getConnection();
         //query             =   "SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID=? AND ORIGIN_LOCATION IN(?) AND DEST_LOCATION IN(?)";
         query.append("SELECT DISTINCT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ACTIVE_FLAG='A' AND SHIPMENT_MODE=").append(masterDOB.getShipmentMode()).append(" ");
         //Commented by Anusha V
         //query.append(" AND QUOTE_STATUS NOT IN ('QUE') AND ESCALATION_FLAG='N' ");
         //Added by Anusha V 
         query.append(" AND QUOTE_STATUS NOT IN ('QUE') AND ESCALATION_FLAG='N' AND IS_MULTI_QUOTE='N' ");
         if(masterDOB.getCustomerId()!=null && masterDOB.getCustomerId().trim().length()!=0)
            query.append("AND ").append("CUSTOMER_ID='").append(masterDOB.getCustomerId()).append("' ");
         if(masterDOB.getOriginLocation()!=null && masterDOB.getOriginLocation().trim().length()!=0)
            query.append("AND ").append("ORIGIN_LOCATION IN(").append(origins).append(") ");
         if(masterDOB.getDestLocation()!=null && masterDOB.getDestLocation().trim().length()!=0)
            query.append("AND ").append("DEST_LOCATION IN(").append(destinations).append(")");
         
          stmt           =   connection.createStatement();
          
          rs  = stmt.executeQuery(query.toString());
          
          while(rs.next())
             quoteIds.add(rs.getString(1));
      
      }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getQuoteGroups"+e);
      logger.error(FILE_NAME+"Error in getQuoteGroups"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closeConnection(connection,stmt,rs);
    }
    return  quoteIds;
   }
   //@@Commented and Modified by kameswari for the WPBN issue-61235
   // public QuoteFinalDOB getUpdatedQuoteInfo(long uniqueId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean,String quoteType) throws EJBException
    public QuoteFinalDOB getUpdatedQuoteInfo(long uniqueId,String changeDesc,String sellBuyFlag,String buyRatesFlag,ESupplyGlobalParameters loginbean,String quoteType) throws EJBException
   {
     QuoteFinalDOB      finalDOB        =     null;
     QuoteMasterDOB     masterDOB       =     null;
     QMSQuoteDAO        quoteDAO        =     null;
     Connection         conn            =     null;
     PreparedStatement  pStmt           =     null;
     ResultSet          rs              =     null;
     //long               QuoteId         =     0;  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
     String               QuoteId       =null;    //@@ Added by subrahmanyam for teh enhancement #146971 on 03/12/2008
     
      
     try
     {
       conn           =   this.getConnection();
       pStmt          =   conn.prepareStatement("SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ID=?");
       pStmt.setLong(1,uniqueId);
       
       rs             =    pStmt.executeQuery();
       
       if(rs.next())
          //QuoteId     =   rs.getLong("QUOTE_ID");  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
          QuoteId     =   rs.getString("QUOTE_ID");  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
       
       quoteDAO       =   new QMSQuoteDAO();
       finalDOB       =   getMasterInfo(""+QuoteId,loginbean);
       masterDOB      =   finalDOB.getMasterDOB();
       masterDOB.setUserId(loginbean.getUserId());
       masterDOB.setBuyRatesPermission(buyRatesFlag);
    
         masterDOB.setCompanyId(loginbean.getCompanyId());
       finalDOB.setMasterDOB(masterDOB);
       finalDOB       =   getQuoteHeader(finalDOB);
       finalDOB       =   quoteDAO.getUpdatedQuoteInfo(uniqueId,changeDesc,sellBuyFlag,finalDOB,quoteType);
      
      // finalDOB       =   getQuoteContentDtl(finalDOB);
    }
     catch(EJBException ejb)
     {
       //Logger.error(FILE_NAME,"EJBException While Fetching Updated Quotes Data"+ejb);
       logger.error(FILE_NAME+"EJBException While Fetching Updated Quotes Data"+ejb);
       ejb.printStackTrace();
       throw new EJBException(ejb);
     }
     catch(SQLException sql)
     {
       //Logger.error(FILE_NAME,"SQLException While Fetching Updated Quotes Data"+sql);
       logger.error(FILE_NAME+"SQLException While Fetching Updated Quotes Data"+sql);
       sql.printStackTrace();
       throw new EJBException(sql);
     }
     catch(Exception e)
     {
       //Logger.error(FILE_NAME,"Exception While Fetching Updated Quotes Data"+e);
       logger.error(FILE_NAME+"Exception While Fetching Updated Quotes Data"+e);
       e.printStackTrace();
       throw new EJBException(e);
     }
     finally
     {
       ConnectionUtil.closeConnection(conn,pStmt,rs);
     }
     return finalDOB;
   }
   public ArrayList getUpdatedQuoteDetails(UpdatedQuotesFinalDOB reportFinalDOB,ESupplyGlobalParameters loginbean)  throws EJBException
   {
     QuoteFinalDOB           finalDOB       = null;
     QuoteMasterDOB          masterDOB      = null;
     QuoteFlagsDOB           flagsDOB       = null;
     UpdatedQuotesReportDOB  reportDOB      = null;
     ArrayList               quoteFinalDOBs = null;
     int[]                   selIndices     = null;
     ArrayList               reportList     = null;
     ArrayList               approvedList   = null;
     ArrayList               attachmentIdList = null;
      ArrayList              attachmentDOBList = new ArrayList();
     ArrayList               filesList       = null;
     String                  quoteId        = null;
     //long                    quoteid        = 0;  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
     String                    quoteid        = null;  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
     QMSQuoteDAO             quoteDAO       = null;
     String                  emailText      = null; 
     String                  quoteType      = null; 
     QuoteAttachmentDOB     attachmentDOB   = null;//@@Added for the WPBN issue-61289
     String                 countryId       = null;//@@Added for the Change Request-71229
     //long quoteId1 =0L;  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
     String                 quoteId1        =null; //@@ Added by subrahmanyam for the enhancement   #146971 on 03/12/2008        
     UpdatedQuotesReportDOB tempObj = null;  // added  by phani sekhar for wpbn 173666 on 20090615
     String updatedQuoteFlag = null;// added  by phani sekhar for wpbn 173666 on 20090615
     HashMap incrctQuoteids = new HashMap();// added  by phani sekhar for wpbn 173666 on 20090615
    try
    {
      quoteDAO       = new QMSQuoteDAO();
      quoteFinalDOBs = new ArrayList();
      selIndices     = reportFinalDOB.getSelectedQuotesIndices();
      reportList     = reportFinalDOB.getUpdatedQuotesList();
     
      approvedList   = reportFinalDOB.getApprovedQuotesList();
      int selIndLen	=	selIndices.length;
      for(int i=0;i<selIndLen;i++)
      {
     updatedQuoteFlag = null;//added  by phani sekhar for wpbn 173666 on 20090615
         if(reportList!=null)
         {
            // added and modified by phani sekhar for wpbn 173666 on 20090615
            tempObj=(UpdatedQuotesReportDOB)reportList.get(i);
            quoteid = updateSelectedQuote(tempObj,loginbean.getUserId(),incrctQuoteids);
            if(incrctQuoteids.containsKey(quoteid))
              {
              updatedQuoteFlag="N";//ends 173666
              }
            quoteId = String.valueOf(quoteid);
                   quoteType = "U";
         }
         else if(approvedList!=null)
         {
           quoteId = ((ReportDetailsDOB)approvedList.get(i)).getQuoteId();
             quoteType = "N";
           }
         finalDOB = getMasterInfo(quoteId,loginbean);
       
         flagsDOB = finalDOB.getFlagsDOB();
          //flagsDOB.setEmailFlag("Y");
          finalDOB.setUpdateQuoteFlag(updatedQuoteFlag); // added  by phani sekhar for wpbn 173666 on 20090615
         finalDOB.setFlagsDOB(flagsDOB);
         masterDOB = finalDOB.getMasterDOB();
         masterDOB.setUserId(loginbean.getUserId());
         masterDOB.setBuyRatesPermission(reportFinalDOB.getBuyRatesFlag());
         masterDOB.setCompanyId(loginbean.getCompanyId());//@@Added by Kameswari for the WPBN issue-61303
         countryId   = getCountryId(masterDOB.getCustomerAddressId());
           masterDOB.setCountryId(countryId);//@@Added for the Change Request-71229
         finalDOB.setMasterDOB(masterDOB);
         finalDOB = getQuoteHeader(finalDOB);
         if(reportList!=null)
         {
             if(!"N".equals(finalDOB.getUpdateQuoteFlag())) // added  by phani sekhar for wpbn 173666 on 20090615
            finalDOB = getRatesChargesInfo(quoteId,finalDOB,(UpdatedQuotesReportDOB)reportList.get(i));
         }
         else
            finalDOB = getRatesChargesInfo(quoteId,finalDOB,null);
         finalDOB = getQuoteContentDtl(finalDOB);
         emailText  = getEmailText(masterDOB.getTerminalId(),quoteType);
         //@@Added for the WPBN issue-61289
         attachmentIdList = (ArrayList)getAttachmentDtls(finalDOB);
     
           if(attachmentIdList!=null)
         {
        	   int attachMentIdSize	=	attachmentIdList.size();
           for(int k=0;k<attachMentIdSize;k++)
           {
         
          
             attachmentDOB =(QuoteAttachmentDOB)attachmentIdList.get(k);
              attachmentDOBList.add(attachmentDOB);
           }
         }
             if(attachmentDOBList!=null)
           {
              finalDOB.setAttachmentDOBList(attachmentDOBList);
           }
           if(!"N".equals(finalDOB.getUpdateQuoteFlag())) // added  by phani sekhar for wpbn 173666 on 20090615
          filesList      =  (ArrayList)getQuoteAttachmentDtls(finalDOB);
       
           if(filesList!=null) 
           {
           finalDOB.setAttachmentDOBList(filesList);
           }
          //@@WPBN issue-61289
          finalDOB.setEmailText(emailText);
          quoteFinalDOBs.add(finalDOB);
      
         if(reportList!=null)
         {
         if(!"N".equals(finalDOB.getUpdateQuoteFlag())) // added  by phani sekhar for wpbn 173666 on 20090615
          {
          reportDOB  = (UpdatedQuotesReportDOB)reportList.get(i);
          reportDOB.setNewQuoteId(masterDOB.getUniqueId());
          finalDOB.setUpdatedReportDOB(reportDOB);
          quoteDAO.setConfirmFlag(reportDOB);
          masterDOB.setOperation("Updated Due to Change in "+reportDOB.getChangeDesc());//@@To Set Transaction Type.
          }
         }
         if(approvedList!=null)
            masterDOB.setOperation("Approved Quote Sent");//@@To Set Transaction Type.
             if(!"N".equals(finalDOB.getUpdateQuoteFlag()))// added  by phani sekhar for wpbn 173666 on 20090615
         quoteDAO.setTransactionDetails(masterDOB);
         
      }
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getUpdatedQuoteDetails"+e);
      logger.error(FILE_NAME+"Error in getUpdatedQuoteDetails"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }  
    return quoteFinalDOBs;
   }
  //private long updateSelectedQuote(UpdatedQuotesReportDOB reportDOB,String userId) throws EJBException  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
 //private long updateSelectedQuote(UpdatedQuotesReportDOB reportDOB,String userId) throws EJBException  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
 //changed method signature by  phani sekhar for wpbn 173666 on 20090615
   private String updateSelectedQuote(UpdatedQuotesReportDOB reportDOB,String userId,HashMap incrctQuoteids) throws EJBException

  {
    String                     quotesListquery  = "SELECT  DISTINCT ACTIVE_FLAG ,CUSTOMER_ID,ORIGIN_LOCATION,DEST_LOCATION ,QUOTE_STATUS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ORIGIN_LOCATION=? AND DEST_LOCATION=? AND ACTIVE_FLAG='A'";//Modified by Anil.k for CR 231104 on 2Feb2011
    String					   quoteRate  		= "SELECT DISTINCT BUYRATE_ID,SELLRATE_ID  FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID=?"; 
    String					   quoteNewRate  	= "SELECT ID FROM QMS_QUOTE_MASTER QM WHERE QM.QUOTE_ID=? AND QM.VERSION_NO=( "+
    											  "SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?) ";
    String					   quoteNRate  		= "SELECT QUOTE_ID  FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID= ? AND BUYRATE_ID=? AND SELLRATE_ID=?";
    Double					   buyRateId		= 0.0;
    Double					   sellRateId		= 0.0;
    ArrayList				   rateId			= null;
    Double				   	   newRateId		= 0.0;
    Connection                 connection       = null;
    CallableStatement          cStmt            = null;
   // long                       newQuoteId       = 0;
   String                       newQuoteId       = null;



    long                       newUniqueId      = 0;
    PreparedStatement          pstmt            = null;
    PreparedStatement          pstmt1            = null;
    PreparedStatement          pstmt2            = null;
    PreparedStatement          pstmt3            = null;
    ResultSet                  rs               = null;
    ResultSet                  rs1               = null;
    ResultSet                  rs2               = null;
    //@@Added by Kameswari for the WPBN issue-116548
    /*String                     quoteIdQuery     = "SELECT MAX(QUOTE_ID) QUOTE_ID,MAX(ID) ID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID=? AND ORIGIN_LOCATION=? AND DEST_LOCATION=?";*/
      //String                     quoteIdQuery     = "SELECT MAX(QUOTE_ID) QUOTE_ID,MAX(ID) ID FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID=? AND ORIGIN_LOCATION=? AND DEST_LOCATION=?";
 String                     quoteIdQuery     = "SELECT QUOTE_ID FROM QMS_QUOTE_MASTER WHERE ID IN (SELECT MAX(ID) FROM QMS_QUOTE_MASTER WHERE CUSTOMER_ID=? AND ORIGIN_LOCATION=? AND DEST_LOCATION=?";
    String                     quoteStatusQuery ="SELECT QUOTE_STATUS FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?";
    String                     updateStatusQuery ="UPDATE QMS_QUOTE_MASTER SET QUOTE_STATUS=? WHERE QUOTE_ID=?";
 //@@WPBN issue-116548
 //@@ Added by subrahmanyam for the enhanement #146970 on 04/12/2008
      String                      quoteId  = null;
      String[]                   quotes=null;
      int                        quoteLen=0;
      String                     quoteSub=null; 
      String                     quoteSub1=null;
      String                     quoteSub2=null; 
//@@ Ended by subrahmanyam for the enhancement #146970 on 04/12/2008
 // added  by phani sekhar for wpbn 173666 on 20090615
   String ratesQuery ="  SELECT COUNT(*) FROM QMS_QUOTE_RATES QR WHERE QR.QUOTE_ID= ? " ; 
    PreparedStatement          ratesStmt           = null;
    ResultSet ratesRslt = null;
    int recordsCnt =0;
    //ends 173666
  try
    {
      connection                =         this.getConnection();
      //Added by Anil.k for CR 231104 on 02Feb2011  
      if(reportDOB!=null && !"Checked".equalsIgnoreCase(reportDOB.getDontModify()))
      { //End  
      cStmt                     =         connection.prepareCall("{ call QMS_QUOTEPACK_NEW.qms_update_quote(?,?,?,?,?,?) }");
    
      pstmt                    =         connection.prepareStatement(quotesListquery);
      ratesStmt = connection.prepareStatement(ratesQuery);// added  by phani sekhar for wpbn 173666 on 20090615
   // pstmt.setLong(1,reportDOB.getQuoteId());
      pstmt.setString(1,reportDOB.getQuoteId());//@@Commented and Modified by subrahmanyam for the WPBN issue-146971
      pstmt.setString(2,reportDOB.getOriginLocation());//Added by Anil.k for CR 231104 on 2Feb2011
      pstmt.setString(3, reportDOB.getDestLocation());//Added by Anil.k for CR 231104 on 2Feb2011

      rs                       =         pstmt.executeQuery();
      
      //@@Added by Kameswari for the WPBN issue-116548
      if(rs.next())
      {
      do
      {
        if("A".equalsIgnoreCase(rs.getString("ACTIVE_FLAG")))
         {
            cStmt.setLong(1,reportDOB.getUniqueId());
            cStmt.setString(2,userId);
            cStmt.setString(3,reportDOB.getSellBuyFlag());
            cStmt.setString(4,reportDOB.getChangeDesc());
  //@@ Added by subrahmanyam for the enhancement #146970 on 04/12/2008
              quoteId = reportDOB.getQuoteId();
           
//@@Added by subrahamnayam for the enhancemente #146970 on 03/01/09
    if(quoteId.indexOf("_")==-1)
		  {
				quoteId=quoteId+"_001";
			
		  }
						
		else
		{
						quotes= quoteId.split("_");
						quoteSub=quotes[quotes.length-2];//first part of the quoteId
						
						if(quotes.length==2 && quoteSub.matches("[a-zA-Z]*"))
						{
                    quoteId=quoteId+"_001";
                 
						}
						else if(quotes.length==2 && !quoteSub.matches("[a-zA-Z]*"))
						{
								quoteLen = Integer.parseInt(quotes[quotes.length-1]);//2nd part of the quoteId
								quoteLen++;
								if(quoteLen<10)
								{
										quoteSub = Integer.toString(quoteLen);
	             			quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									  quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
										
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
						else
						{
							
							quoteLen = Integer.parseInt(quotes[quotes.length-1]);//3rd part of the quoteId
							quoteLen++;
						
								if(quoteLen<10)
								{
									 quoteSub = Integer.toString(quoteLen);
										quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									 quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
		  
		}
              
                cStmt.setString(5,quoteId);
//@@ Ended by subrahmanyam for the enhancement #146970 on 04/12/2008
            
            //cStmt.registerOutParameter(5,OracleTypes.NUMBER);//@@Commented and Modified by subrahmanyam for the WPBN issue-146971
           
            cStmt.registerOutParameter(6,OracleTypes.NUMBER);
            
            cStmt.execute();

    

           // newQuoteId  =   cStmt.getLong(5);//@@Commented and Modified by subrahmanyam for the WPBN issue-146970 on 05/12/08
            //newQuoteId  =   cStmt.getString(5);//@@ Commented by subrahmanyam for the enhancement #146970 on 05/12/08
            newUniqueId =   cStmt.getLong(6); 
            // added  by phani sekhar for wpbn 173666 on 20090615
            ratesStmt.clearParameters();
          ratesStmt.setLong(1,newUniqueId);
           ratesRslt=ratesStmt.executeQuery();
          if(ratesRslt.next())
          { 
          recordsCnt=ratesRslt.getInt(1);
          if(recordsCnt==0)
          {  
          if(!incrctQuoteids.containsKey(reportDOB.getQuoteId()))
          incrctQuoteids.put(reportDOB.getQuoteId(),reportDOB.getQuoteId());
          quoteId =reportDOB.getQuoteId();
          
          }
          }
          if(ratesRslt!=null)
          ratesRslt.close();          
          // ends  by phani sekhar for wpbn 173666 on 20090615*/
         } else
         {
               quoteId = reportDOB.getQuoteId();
        
         //@@Added by subrahamnayam for the enhancemente #146970 on 03/01/09
        if(quoteId.indexOf("_")==-1)
          {
            quoteId=quoteId+"_001";
          
          }
                
        else
        {
						quotes= quoteId.split("_");
						quoteSub=quotes[quotes.length-2];//first part of the quoteId
						
						if(quotes.length==2 && quoteSub.matches("[a-zA-Z]*"))
						{
                    quoteId=quoteId+"_001";
                 
						}
						else if(quotes.length==2 && !quoteSub.matches("[a-zA-Z]*"))
						{
								quoteLen = Integer.parseInt(quotes[quotes.length-1]);//2nd part of the quoteId
								quoteLen++;
								if(quoteLen<10)
								{
										quoteSub = Integer.toString(quoteLen);
	             			quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									  quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
										
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
						else
						{
							
							quoteLen = Integer.parseInt(quotes[quotes.length-1]);//3rd part of the quoteId
							quoteLen++;
						
								if(quoteLen<10)
								{
									 quoteSub = Integer.toString(quoteLen);
										quoteSub1 ="00"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub1;
									
								}
								else if(quoteLen<100)
								{
									
									 quoteSub = Integer.toString(quoteLen);
										quoteSub="0"+quoteSub;
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
								else
								{
										quoteSub = Integer.toString(quoteLen);
										quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
									
								}
						}
		  
		}
              

             /*pstmt1                    =         connection.prepareStatement(quoteIdQuery);
             pstmt1.setString(1,rs.getString("CUSTOMER_ID"));
             pstmt1.setString(2,rs.getString("ORIGIN_LOCATION"));
             pstmt1.setString(3,rs.getString("DEST_LOCATION"));
             rs1                       =         pstmt1.executeQuery();  
             while(rs1.next())
             {
               // newQuoteId             =        rs1.getLong("QUOTE_ID");//@@Commented and Modified by subrahmanyam for the WPBN issue-146971
                newQuoteId             =        rs1.getString("QUOTE_ID");

                newUniqueId            =        rs1.getLong("ID");
             }
              pstmt2                    =         connection.prepareStatement(quoteStatusQuery);
              pstmt3                   =         connection.prepareStatement(updateStatusQuery);

             // pstmt2.setLong(1,newQuoteId);//@@Commented and Modified by subrahmanyam for the WPBN issue-146971
             pstmt2.setString(1,newQuoteId);


              rs2                       =     pstmt2.executeQuery();
              while(rs2.next())
              {
                if(!("QUE".equalsIgnoreCase(rs2.getString("QUOTE_STATUS"))))
                {
                   pstmt3.setString(1,rs.getString("QUOTE_STATUS"));
                   //pstmt3.setLong(2,newQuoteId);//@@Commented and Modified by subrahmanyam for the WPBN issue-146971
                   pstmt3.setString(2,newQuoteId);

                   pstmt3.executeUpdate();
                }
              }*/
             }
          
      }while(rs.next());
      }
      else
      {
    	  quoteId = getQuoteIdValue(reportDOB.getQuoteId());    	  
    	pstmt                    =         connection.prepareStatement(quoteRate);
        pstmt.setDouble(1,reportDOB.getUniqueId());        
        rs    =     pstmt.executeQuery();
        if(rs.next())
        {
        	buyRateId = rs.getDouble("BUYRATE_ID");
        	sellRateId = rs.getDouble("SELLRATE_ID");
        }
        pstmt                    =         connection.prepareStatement(quoteNewRate);
        pstmt.setString(1,quoteId); 
        pstmt.setString(2,quoteId);
        rs    =     pstmt.executeQuery();
        rateId = new ArrayList();
        while(rs.next())
        {
        	rateId.add(rs.getString("ID"));
        }
        for(int i=0;i<rateId.size();i++)
        {
        	 pstmt                    =         connection.prepareStatement(quoteNRate);
             pstmt.setString(1,(String)rateId.get(i)); 
             pstmt.setDouble(2,buyRateId);
             pstmt.setDouble(3,sellRateId);
             rs    =     pstmt.executeQuery();
             if(rs.next())
             {
            	 newRateId = rs.getDouble("QUOTE_ID");
             }
        }
        quoteId = getQuoteIdValue(quoteId);    	
        cStmt.setLong(1,reportDOB.getUniqueId());
        cStmt.setString(2,userId);
        cStmt.setString(3,reportDOB.getSellBuyFlag());
        cStmt.setString(4,reportDOB.getChangeDesc());
        cStmt.setString(5,quoteId);
        cStmt.registerOutParameter(6,OracleTypes.NUMBER);
                  
        cStmt.execute();
        
        newUniqueId =   cStmt.getLong(6); 
         
        ratesStmt.clearParameters();
        ratesStmt.setLong(1,newUniqueId);
        ratesRslt=ratesStmt.executeQuery();
        if(ratesRslt.next())
        { 
        	recordsCnt=ratesRslt.getInt(1);
        	if(recordsCnt==0)
        	{  
        		if(!incrctQuoteids.containsKey(reportDOB.getQuoteId()))
                incrctQuoteids.put(reportDOB.getQuoteId(),reportDOB.getQuoteId());
                quoteId =reportDOB.getQuoteId();
                
                }
        }
        if(ratesRslt!=null)
        	ratesRslt.close();  
      }
    } //Added by Anil.k for CR 231104 on 03Feb2011
    else{
    	quoteId = reportDOB.getQuoteId();
    	long versionNo = 0;
    	String versionQuote = "SELECT VERSION_NO FROM QMS_QUOTE_RATES WHERE QUOTE_ID=?";
    	pstmt                    =         connection.prepareStatement(versionQuote);
    	pstmt.setLong(1, reportDOB.getUniqueId());
    	rs						 =		   pstmt.executeQuery();
    	while(rs.next())
    	{
    		versionNo = rs.getLong("VERSION_NO");
    	}
    	String updateRates = "UPDATE QMS_QUOTE_RATES SET VERSION_NO=? WHERE QUOTE_ID=?";
    	pstmt3                   =         connection.prepareStatement(updateRates);
    	pstmt3.setLong(1, versionNo+1);
    	pstmt3.setLong(2, reportDOB.getUniqueId());  
    	pstmt3.executeUpdate();
    }//Ended by Anil.k for CR 231104 on 03Feb2011
           if(rs!=null)
              rs.close();
           if(rs1!=null)
             rs1.close();
           if(rs2!=null)
             rs2.close(); //Added By RajKumari on 27-10-2008 for Connection Leakages.
             //added by phani sekhar for wpbn 173666
             if(ratesRslt!=null)
          ratesRslt.close();
          if(ratesStmt!=null)
          ratesStmt.close();
          //ends 173666
           if(pstmt!=null)
              pstmt.close();
           if(pstmt2!=null)
              pstmt2.close();
         if(pstmt1!=null)
              pstmt1.close();
            if(pstmt3!=null)
              pstmt3.close();
      //@@WPBN issue-116548
    }
    catch(SQLException sql)
    {
      //Logger.error(FILE_NAME,"SQLException in updateSelectedQuote "+sql);
      logger.error(FILE_NAME+"SQLException in updateSelectedQuote "+sql);
      //Logger.error(FILE_NAME,"Error Code"+sql.getErrorCode());
      logger.error(FILE_NAME+"Error Code"+sql.getErrorCode());
      sql.printStackTrace();
      throw new EJBException(sql);
    }
    catch(EJBException ejb)
    {
      //Logger.error(FILE_NAME,"EJBException in updateSelectedQuote "+ejb);
      logger.error(FILE_NAME+"EJBException in updateSelectedQuote "+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception in updateSelectedQuote "+e);
      logger.error(FILE_NAME+"Exception in updateSelectedQuote "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
    	//Added by Dilip for PMD correction
    	try{
			 if(rs!=null){
		        rs.close();
		        rs=null;
			 }
		     if(rs1!=null){
		       rs1.close();
		       rs1=null;
		     }
		     if(rs2!=null){
		       rs2.close(); 
		       rs2=null;
		     }
		     if(ratesRslt!=null){
		    	ratesRslt.close();
		    	ratesRslt=null;
		     }
		     if(ratesStmt!=null){
		    	ratesStmt.close();
		    	ratesStmt=null;
		     }
		     if(pstmt!=null){
		        pstmt.close();
		        pstmt=null;
		     }
		     if(pstmt1!=null){
		       pstmt1.close();
		       pstmt1=null;   
		     }
		     if(pstmt2!=null){
		      pstmt2.close();
		      pstmt2=null;
		     }
		     if(pstmt3!=null){
		        pstmt3.close();
		        pstmt3=null;    
		     }
		    if(cStmt!=null){
			 cStmt.close();
			 cStmt=null;
		    }
   	}catch(Exception e){}
      //ConnectionUtil.closeConnection(connection,cStmt,null);
    	ConnectionUtil.closeConnection(connection,null,null);
    }
    return quoteId;  //@@ Commented by subrahmanyam for the enhancement #146970 on 05/12/2008
   // return quoteId;
  }
  private QuoteFinalDOB getRatesChargesInfo(String quoteId,QuoteFinalDOB  finalDOB,UpdatedQuotesReportDOB reportDOB) throws EJBException
  {
    Connection                 connection = null;
    CallableStatement          cStmt      = null;
    ResultSet                  rs2        = null,rs3 = null,rs4 = null,rs5 = null;    
    ArrayList                  quoteList  = null;
    ArrayList                  originChargesList       = null;//to maintain the list of  all origin charge dobs
    ArrayList                  destChargesList         = null;//to maintain the list of  all origin charge dobs
    QuoteFreightLegSellRates   legDOB                  = null;
    QuoteFreightLegSellRates   spotLegDOB              = null;
    QuoteCharges               chargesDOB              = null;//to maintain one record that is to be displayed
    QuoteCharges               deliveryChargesDOB      = null;
    QuoteChargeInfo            chargeInfo              = null;
    ArrayList                  chargeInfoList          = null;
    ArrayList                  legDetails              = null;
    ArrayList                  spotRateDetails         = null;
    String                     flag                    = null;
    double                     sellRate                = 0;
    String                     weightBreak             = null;
    String                     chargeSlab              = null;//@@ Added by govind for the issue 260762
    String                     rateType                = null;
    String                     rateIndicator           = null;
    String                     rate_desc               = null;//@@ Added by govind for the issue 260762
    int                        rate_desc_len           =0;
    ArrayList                  list_exNotes            = null;
    ArrayList                  freightChargesList      = null;
    QuoteMasterDOB             masterDOB               = null;
    //UpdatedQuotesReportDOB     reportDOB               = null;
    QuoteFinalDOB              tmpFinalDOB             = null;
    QMSQuoteDAO                quoteDAO                = null;
    int[]                      selectedFrtIndices      = null;
    boolean                    isShipperZipCode        = false;
    boolean                    isConsigneeZipCode      = false;
    boolean                    isSingleShipperZone     = false;
    boolean                    isSingleConsigneeZone   = false;
    int         n;
    String      breakPoint    = null;
    try
    {      
      connection                =         this.getConnection();
      
      masterDOB = finalDOB.getMasterDOB();
        
     // cStmt                     =         connection.prepareCall("{ call QMS_QUOTE_PACK.quote_view_proc(?,?,?,?,?) }");
       cStmt                     =         connection.prepareCall("{ call QMS_QUOTE_PACK.quote_view_proc(?,?,?,?,?,?) }");//Modifed by Anil.K
      cStmt.setString(1,quoteId);
      cStmt.setString(2,finalDOB.getMlutiQuoteLaneNo());
      cStmt.registerOutParameter(3,OracleTypes.CURSOR);
      cStmt.registerOutParameter(4,OracleTypes.CURSOR);
      cStmt.registerOutParameter(5,OracleTypes.CURSOR);
      cStmt.registerOutParameter(6,OracleTypes.CURSOR);
      cStmt.execute();
      rs2  = (ResultSet) cStmt.getObject(3);
      rs3  = (ResultSet) cStmt.getObject(4);
      rs4  = (ResultSet) cStmt.getObject(5);
      rs5  = (ResultSet) cStmt.getObject(6);

      quoteDAO  =   new QMSQuoteDAO();
      
 
       //rs4 ResultSet is used for gettting Rates
      while(rs2.next())
      {
        flag = rs2.getString("SEL_BUY_FLAG");
       
        if(legDOB!=null && legDOB.getLegSerialNo()==rs2.getInt("LEG_SL_NO"))
        {                  
          if((chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs2.getString("SELLCHARGEID"))) || 
             (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs2.getString("BUY_CHARGE_ID"))))
          {
        	        chargeInfo  = new QuoteChargeInfo();
        	     
                  chargeInfoList.add(chargeInfo);
             	 chargeSlab = rs2.getString("CHARGESLAB");
                 //if("LIST".equalsIgnoreCase(rs2.getString("weightbreak")) && "2".equalsIgnoreCase(rs2.getString("SHMODE")) && "A FREIGHT RATE".equalsIgnoreCase(rs.getString("")))
                
            	chargeInfo.setWeight_break(rs2.getString("weight_break")!= null?rs2.getString("weight_break"):"");
            	chargeInfo.setRate_Type(rs2.getString("rate_type")!= null?rs2.getString("rate_type"):"");
                  chargeInfo.setBreakPoint(chargeSlab);
               
                  if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
                    chargeInfo.setCurrency(rs2.getString("CURRENCY"));
                  else
                    chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
                  
                  chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
                  chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
                  chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
                  chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
                  chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
                  rate_desc     =   rs2.getString("RATE_DESCRIPTION")!= null?rs2.getString("RATE_DESCRIPTION"):"";//@@ Added by govind for the issue 260762
                  rate_desc_len = rate_desc.indexOf("-")!= -1?rate_desc.indexOf("-"):rate_desc.length();
                  if("A FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase())|| "FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase()))//@@ Added by govind for the issue 260762
                     chargeInfo.setRateDescription("FREIGHT RATE");
                  else   if("C.P.S.S".equalsIgnoreCase(rate_desc))
                     chargeInfo.setRateDescription("P.S.S");
                  else
                     chargeInfo.setRateDescription(!"".equals(rate_desc)?rate_desc:"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst
                  if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
                  {  
                    chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                    chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
                    
                    if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                    else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
               
                  }
                  else
                  {
                    chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                    chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
                    
                    if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                    else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                  }
                  //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                  weightBreak   =   rs2.getString("WEIGHT_BREAK");
                  rateType      =   rs2.getString("RATE_TYPE");
                  rateIndicator	= 	rs2.getString("RATE_INDICATOR");
                 
               if(!(("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))||("A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))) // Added by Gowtham to show the Basis Description in Quote PDF.
              {
                      if("CSF".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                      {
                           chargeInfo.setBasis("Per Shipment");
                      }
                  
                 else if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("CAFLF")||chargeInfo.getBreakPoint().endsWith("BAF")||chargeInfo.getBreakPoint().endsWith("BAFLF")
                     ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("CSFLF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("PSSLF")||chargeInfo.getBreakPoint().endsWith("caf")||chargeInfo.getBreakPoint().endsWith("caflf")
                     ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("baflf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("csflf")||chargeInfo.getBreakPoint().endsWith("pss")||chargeInfo.getBreakPoint().endsWith("psslf") 
                     || chargeInfo.getBreakPoint().endsWith("CAFPF")//Added by Gowtham to show the Basis Description in Quote PDF.
                     ||("2".equals(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK"))))//Added by GOVIND FOR THE ISSUE 260762
                     {
                          if(chargeInfo.getBreakPoint().toUpperCase().endsWith("CAF")||chargeInfo.getBreakPoint().toUpperCase().endsWith("CAFLF")||chargeInfo.getBreakPoint().toUpperCase().endsWith("CAFPF"))
                         {
                            chargeInfo.setBasis("Percent of Freight");
                         }
                         else
                         {
                           chargeInfo.setBasis("Per Container");
                         }
                      }
               else if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"BAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "PSSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"CAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())|| chargeInfo.getBreakPoint().toUpperCase().endsWith("MIN")||chargeInfo.getBreakPoint().toUpperCase().endsWith("BASIC")||chargeInfo.getBreakPoint().toUpperCase().endsWith("ABSOLUTE")) // Added by Gowtham to show the Basis Description in Quote PDF.
               {
                   
                    chargeInfo.setBasis("Per Shipment");
               }
               else if("2".equalsIgnoreCase(rs2.getString("SHMODE"))&& (chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT")||chargeInfo.getBreakPoint().toUpperCase().endsWith("SLAB")))
               {
            	   chargeInfo.setBasis("Per Weight Measurement ");
               }
               else if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())|| "SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())||chargeInfo.getBreakPoint().toUpperCase().endsWith("FLAT"))// Added by Gowtham to show the Basis Description in Quote PDF.
               {
                    chargeInfo.setBasis("Per Kilogram");
               }
               else if("CAF%".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SURCHARGE".equalsIgnoreCase(chargeInfo.getBreakPoint())||chargeInfo.getBreakPoint().toUpperCase().endsWith("PERCENT")) // Added by Gowtham on 18-Feb2011
               {
                   
                     chargeInfo.setBasis("Percent of Freight");
               }
             else  if(weightBreak.endsWith("CAF%"))
                {
                      chargeInfo.setBasis("Percent of Freight");
                }
                else if("BAFM3".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSM3".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                {
                   /*  chargeInfo.setBasis("per Cubic Meter");*/
                	chargeInfo.setBasis("per Weight Measurement");
                }
              
           }
           else
           {
          	 //@@ Commented & Added by subrahmanyam for the wpbn id: 210657 on 12-Jul-10        	   
        	 //if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))                  
        	   if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(rateIndicator)))
                  {
                    chargeInfo.setBasis("Per Shipment");
                  }
                  else
                  {
                    if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                    {
                      chargeInfo.setBasis("Per ULD");
                    }
                    else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                    {
                     
                       chargeInfo.setBasis("Per Container");
                  
                    }
                    else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Kilogram");
                    }
                    else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Pound");
                    }
                    else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      //chargeInfo.setBasis("Per Cubic Meter"); //Commented by Gowtham for LCL case.
                    	chargeInfo.setBasis("Per Weight Measurement"); // Added by Gowtham to show in LCL case.
                    }
                    else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Cubic Feet");
                    }
                    else
                      chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                  }
           }
                  chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
      
                  chargeInfo.setSellRate(sellRate);
            if(rs2.getString("margin_test_flag")!=null)
            {
                if(rs2.getString("margin_test_flag").equals("Y"))
                  chargeInfo.setMarginTestFailed(true);
                else 
                  chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
          }
          else
          {
                
                  chargesDOB  = new QuoteCharges();
          
                  freightChargesList.add(chargesDOB); 
                    
                  
                  chargesDOB.setBuyChargeId(rs2.getString("BUY_CHARGE_ID"));
                  chargesDOB.setSellChargeId(rs2.getString("SELLCHARGEID"));
                  chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
                  chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
                  chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
                  chargesDOB.setMarginDiscountFlag(rs2.getString("MARGIN_DISCOUNT_FLAG"));
                  //@@Added by Kameswari for the WPBN issue-146448
                   chargesDOB.setFrequency(rs2.getString("FREQUENCY"));       
                   chargesDOB.setCarrier(rs2.getString("CARRIER"));
                   chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
                   chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
                   chargesDOB.setFrequencyChecked(rs2.getString("FREQUENCY_CHECKED"));
                   chargesDOB.setCarrierChecked(rs2.getString("CARRIER_CHECKED"));
                   chargesDOB.setTransitTimeChecked(rs2.getString("TRANSITTIME_CHECKED"));
                   chargesDOB.setRateValidityChecked(rs2.getString("RATEVALIDITY_CHECKED"));
                   //@@WPBN issue-146448
                  chargesDOB.setSellBuyFlag(flag);
                  
                  chargesDOB.setChargeDescriptionId("Freight Rate");
                  chargesDOB.setCostIncurredAt(rs2.getString("COST_INCURREDAT"));
                  chargesDOB.setConsoleType(rs2.getString("CONSOLE_TYPE")); 
                 
                  chargeInfoList  = new ArrayList();
                  chargeInfo  = new QuoteChargeInfo();
                  chargeInfoList.add(chargeInfo);
                  
                  chargesDOB.setChargeInfoList(chargeInfoList);
                  
                  chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
                 
                  if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
                      chargeInfo.setCurrency(rs2.getString("CURRENCY"));
                  else
                      chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
                  chargeInfo.setWeight_break(rs2.getString("weight_break")!= null?rs2.getString("weight_break"):"");
              	chargeInfo.setRate_Type(rs2.getString("rate_type")!= null?rs2.getString("rate_type"):"");
                  chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
                  chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
                  chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
                  chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
                  //chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                  chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
                  if("A FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase())||"FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase()))//@@ Added by govind for the issue 260762
                      chargeInfo.setRateDescription("FREIGHT RATE");
                  else
                      chargeInfo.setRateDescription(!"".equals(rate_desc)?rate_desc.substring(0,rate_desc_len):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst

                 weightBreak = rs2.getString("WEIGHT_BREAK");
                  rateType    = rs2.getString("RATE_TYPE");
                if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
              
                if("CSF".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                {
                 chargeInfo.setBasis("Per Shipment");
                }
              else if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                     ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                     ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
                     {
                          if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
                         {
                            chargeInfo.setBasis("Percent of Freight");
                         }
                         else
                         {
                           chargeInfo.setBasis("Per Container");
                         }
                      }
              else 
               if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"BAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "PSSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"CAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                  chargeInfo.setBasis("Per Shipment");
               }
               else if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())|| "SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                  chargeInfo.setBasis("Per Kilogram");
               }
               else if("CAF%".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SURCHARGE".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                 chargeInfo.setBasis("Percent");
               }
                else if("BAFM3".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSM3".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                {
                 /*chargeInfo.setBasis("per Cubic Meter");*/
                	chargeInfo.setBasis("Per Weight Measurement");
                	
                }
                
           }
           else
           {
        	 //@@ Commented & Added by subrahmanyam for the wpbn id: 210657 on 12-Jul-10        	   
                 // if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
        	   if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(rateIndicator)))
                  {
                    chargeInfo.setBasis("Per Shipment");
                  }
                  else
                  {
                    if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                    {
                      chargeInfo.setBasis("Per ULD");
                    }
                    else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
                    {
                     
                         chargeInfo.setBasis("Per Container");
                    }
                    else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Kilogram");
                    }
                    else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Pound");
                    }
                    else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Cubic Meter");
                    }
                    else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
                    {
                      chargeInfo.setBasis("Per Cubic Feet");
                    }
                    else
                      chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
                  }
           }    
                  chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
                  
                  if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
                  {  
                    chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
                    chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
               
               
                    if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
                    else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
                  }
                  else
                  {
                    chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
                    chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
                    
                    if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
                    else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
                      sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
                  }
                  chargeInfo.setSellRate(sellRate);
            if(rs2.getString("margin_test_flag")!=null)
            {
              if(rs2.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
            }
            else
                chargeInfo.setMarginTestFailed(false);
          }
        }
        else
        {
        
          ArrayList                 ratesList         = new ArrayList();
          QuoteFreightRSRCSRDOB     freightDOB        = new QuoteFreightRSRCSRDOB();
          QuoteFreightLegSellRates  legRateDetails    = null;
          ArrayList                 freightRatesList  = finalDOB.getLegDetails();
          
          if(freightRatesList!=null)
          {
            legRateDetails  = (QuoteFreightLegSellRates)freightRatesList.get(rs2.getInt("LEG_SL_NO")==0?rs2.getInt("LEG_SL_NO"):rs2.getInt("LEG_SL_NO")-1);//Modified by Anil.k
          }
          
          if(legDetails==null)
            legDetails  = new ArrayList();
          
          legDOB = new QuoteFreightLegSellRates();
          selectedFrtIndices  = new int[1];
          selectedFrtIndices[0] = 0;
          
          legDetails.add(legDOB);
          
          freightChargesList           = new ArrayList();
          
          legDOB.setFreightChargesList(freightChargesList);
          
          legDOB.setOrigin(rs2.getString("ORG"));
          legDOB.setDestination(rs2.getString("DEST"));
          legDOB.setLegSerialNo(rs2.getInt("LEG_SL_NO"));
          legDOB.setShipmentMode(rs2.getInt("SHMODE"));
          legDOB.setServiceLevel(rs2.getString("SRV_LEVEL"));
      
         if("SBR".equalsIgnoreCase(flag) && finalDOB.getLegDetails()!=null)
          {
            spotRateDetails = finalDOB.getLegDetails();
            finalDOB.setSpotRatesFlag("Y");
            spotLegDOB      = (QuoteFreightLegSellRates)spotRateDetails.get(rs2.getInt("LANE_NO"));//@@Refer getMasterInfo()
            legDOB.setDensityRatio(spotLegDOB.getDensityRatio());
            legDOB.setUom(spotLegDOB.getUom());
            legDOB.setSpotRatesType(spotLegDOB.getSpotRatesType());
            //@@Added by Kameswari for the WPBN issue-179373 on 13/08/09
            if(legDOB.getShipmentMode()==2)
            {
              if("List".equalsIgnoreCase(legDOB.getSpotRatesType()))
              {
                 legDOB.setConsoleType("FCL");
              }
              else
              {
               legDOB.setConsoleType("LCL");
              }
            }
            //@@WPBN issue-179373 on 13/08/09
            legDOB.setWeightBreaks(spotLegDOB.getWeightBreaks());
            legDOB.setCurrency(spotLegDOB.getCurrency());//@@Added by kameswari for the WPBN issue-30908
            legDOB.setSpotRateDetails(spotLegDOB.getSpotRateDetails());
            legDOB.setSpotRatesFlag(spotLegDOB.isSpotRatesFlag());
          }
          
          if(legRateDetails!=null)
          {
            legDOB.setSlabWeightBreaks(legRateDetails.getSlabWeightBreaks());
            legDOB.setListWeightBreaks(legRateDetails.getListWeightBreaks());
            legDOB.setRates(legRateDetails.getRates());
          }
          else
          {
            freightDOB.setServiceLevelId(legDOB.getServiceLevel());
            freightDOB.setWeightBreakType(rs2.getString("WEIGHT_BREAK"));
            
            ratesList.add(freightDOB);
            legDOB.setRates(ratesList);
          }
          
          legDOB.setSelectedFreightChargesListIndices(selectedFrtIndices);
          
          chargesDOB  = new QuoteCharges();
          
          freightChargesList.add(chargesDOB);
            
          chargesDOB.setChargeDescriptionId("Freight Rate");
          chargesDOB.setBuyChargeId(rs2.getString("BUY_CHARGE_ID"));
          chargesDOB.setSellChargeId(rs2.getString("SELLCHARGEID"));
          chargesDOB.setVersionNo(rs2.getString("VERSION_NO"));
          chargesDOB.setBuyChargeLaneNo(rs2.getString("LANE_NO"));
          chargesDOB.setTerminalId(rs2.getString("TERMINALID"));
          chargesDOB.setMarginDiscountFlag(rs2.getString("MARGIN_DISCOUNT_FLAG"));
                    
          chargesDOB.setSellBuyFlag(flag);
          chargesDOB.setConsoleType(rs2.getString("CONSOLE_TYPE")); 
          chargesDOB.setCostIncurredAt(rs2.getString("COST_INCURREDAT"));
            //@@Added by Kameswari for the WPBN issue-146448
                   chargesDOB.setFrequency(rs2.getString("FREQUENCY"));       
                   chargesDOB.setCarrier(rs2.getString("CARRIER"));
                   chargesDOB.setTransitTime(rs2.getString("TRANSITTIME"));
                   chargesDOB.setValidUpto(rs2.getTimestamp("VALIDUPTO"));
                   chargesDOB.setFrequencyChecked(rs2.getString("FREQUENCY_CHECKED"));
                   chargesDOB.setCarrierChecked(rs2.getString("CARRIER_CHECKED"));
                   chargesDOB.setTransitTimeChecked(rs2.getString("TRANSITTIME_CHECKED"));
                   chargesDOB.setRateValidityChecked(rs2.getString("RATEVALIDITY_CHECKED"));
                   //@@WPBN issue-146448       
          chargeInfoList  = new ArrayList();
          chargeInfo  = new QuoteChargeInfo();
          chargeInfoList.add(chargeInfo);
          
          chargesDOB.setChargeInfoList(chargeInfoList);
          
          chargeInfo.setBreakPoint(rs2.getString("CHARGESLAB"));
         
          if(rs2.getString("CURRENCY")!=null && rs2.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs2.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
          chargeInfo.setWeight_break(rs2.getString("weight_break")!= null?rs2.getString("weight_break"):"");//Govind
          chargeInfo.setRate_Type(rs2.getString("rate_type")!= null?rs2.getString("rate_type"):"");
          chargeInfo.setBuyRate(rs2.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs2.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs2.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs2.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs2.getString("RATE_INDICATOR"));
          weightBreak = rs2.getString("WEIGHT_BREAK");//Added by Anil.k
          rate_desc  = rs2.getString("RATE_DESCRIPTION")!= null?rs2.getString("RATE_DESCRIPTION"):"";
          rate_desc_len = rate_desc.indexOf("-")!= -1?rate_desc.indexOf("-"):rate_desc.length();
           if("A FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase())||"FREIGHT RATE".equalsIgnoreCase(rate_desc.toUpperCase()))
             chargeInfo.setRateDescription("FREIGHT RATE");
        else   if("C.P.S.S".equalsIgnoreCase(rate_desc))
             chargeInfo.setRateDescription("P.S.S");
         else
             chargeInfo.setRateDescription(!"".equals(rate_desc)?rate_desc.substring(0,rate_desc_len):"FREIGHT RATE");//@@Added by Kameswari for Surcharge Enhancemenst

             if(!("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription())))
              {
                if("CSF".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                {
                      chargeInfo.setBasis("Per Shipment");
                }
             else if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                     ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                     ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
                     {
                          if(chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("caf"))
                         {
                            chargeInfo.setBasis("Percent of Freight");
                         }
                         else
                         {
                           chargeInfo.setBasis("Per Container");
                         }
                      }
              else  if("FSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"FSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "SSBASIC".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"BAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||
               "PSSMIN".equalsIgnoreCase(chargeInfo.getBreakPoint())||"CAFMIN".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                 chargeInfo.setBasis("Per Shipment");
               }
               else if("FSKG".equalsIgnoreCase(chargeInfo.getBreakPoint())|| "SSKG".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                       chargeInfo.setBasis("Per Kilogram");
               }
               else if("CAF%".equalsIgnoreCase(chargeInfo.getBreakPoint())||"SURCHARGE".equalsIgnoreCase(chargeInfo.getBreakPoint()))
               {
                     chargeInfo.setBasis("Percent");
               }
                else  if(weightBreak.endsWith("CAF"))
                {
                      chargeInfo.setBasis("Percent of Freight");
                }
                else if("BAFM3".equalsIgnoreCase(chargeInfo.getBreakPoint())||"PSSM3".equalsIgnoreCase(chargeInfo.getBreakPoint()))
                {
                     //chargeInfo.setBasis("per Cubic Meter");
                	chargeInfo.setBasis("per Weight Measurement");
                } 
                
           }
           else
           {
//@@ Commented & Added by subrahmanyam for the wpbn id: 210657 on 12-Jul-10        	   
          //if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "FLAT".equalsIgnoreCase(rateIndicator)))
          {
                     chargeInfo.setBasis("Per Shipment");
          }
          
          else
          {
            if("1".equalsIgnoreCase(rs2.getString("SHMODE")) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
            {
              chargeInfo.setBasis("Per ULD");
            }
            else if(("2".equalsIgnoreCase(rs2.getString("SHMODE"))||"4".equalsIgnoreCase(rs2.getString("SHMODE"))) && "LIST".equalsIgnoreCase(rs2.getString("WEIGHT_BREAK")))
            {
               
                 chargeInfo.setBasis("Per Container");
             
            }
            else if("Per Kg".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Kilogram");
            }
            else if("Per Lb".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Pound");
            }
            else if("Per CBM".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Meter");
            }
            else if("Per CFT".equalsIgnoreCase(rs2.getString("CHARGEBASIS")))
            {
              chargeInfo.setBasis("Per Cubic Feet");
            }
            else
              chargeInfo.setBasis(rs2.getString("CHARGEBASIS"));
            }
           }
         // chargeInfo.setBreakPoint(rs2.getString("WEIGHT_BREAK_SLAB"));
                 //@@Added by kameswari for surcharge enhancements
          ArrayList weightBreakList = legDOB.getWeightBreaks();
          
        
          weightBreak = rs2.getString("WEIGHT_BREAK");
          rateType    = rs2.getString("RATE_TYPE");
     
          chargeInfo.setRatio(rs2.getString("DENSITY_RATIO"));
          
          if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs2.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs2.getString("MARGIN_TYPE"));
             
            if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
            {
              sellRate  = rs2.getDouble("BUYRATE")+rs2.getDouble("MARGINVALUE");
              
            }
            else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
              sellRate  = rs2.getDouble("BUYRATE")+(rs2.getDouble("BUYRATE")*rs2.getDouble("MARGINVALUE")/100);
          }
          else
          {
            chargeInfo.setDiscount(rs2.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs2.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
              sellRate  = rs2.getDouble("SELLRATE")-rs2.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs2.getString("MARGIN_TYPE")))
              sellRate  = rs2.getDouble("SELLRATE")-(rs2.getDouble("SELLRATE")*rs2.getDouble("MARGINVALUE")/100);
          }
          
          chargeInfo.setSellRate(sellRate);
           if(rs2.getString("margin_test_flag")!=null)
            {
              if(rs2.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
        }
      }
      
      if(legDetails!=null)
        finalDOB.setLegDetails(legDetails);
      /*
      chargesDOB  = null;
      //rs3 ResultSet is used for getting Charges  
      while(rs3.next())
      {           
        if(chargesDOB!=null && 
           (
             (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs3.getString("BUY_CHARGE_ID")))
             ||
             (chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs3.getString("SELLCHARGEID")))
           )
          )
        {
          chargeInfo  = new QuoteChargeInfo();
          
          chargeInfoList.add(chargeInfo);
          
          chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
          
          if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs3.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
              
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          
          if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          else
          {
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
              
          chargeInfo.setSellRate(sellRate);
         if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
          }
        else
          chargeInfo.setMarginTestFailed(false);
        }
        else
        {        
          chargesDOB  = new QuoteCharges();
          
          if("Origin".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
          {
            if(originChargesList==null)
              originChargesList = new ArrayList();
            originChargesList.add(chargesDOB);
          }
          else if("Destination".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
          {
            if(destChargesList==null)
              destChargesList = new ArrayList();
            destChargesList.add(chargesDOB);
          }
          
          chargesDOB.setBuyChargeId(rs3.getString("BUY_CHARGE_ID"));
          chargesDOB.setSellChargeId(rs3.getString("SELLCHARGEID"));                
          chargesDOB.setSellBuyFlag(rs3.getString("SEL_BUY_FLAG"));
          
          chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
          chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
          chargesDOB.setMarginDiscountFlag(rs3.getString("MARGIN_DISCOUNT_FLAG"));
          chargesDOB.setChargeDescriptionId(rs3.getString("CHARGEDESCID"));
          chargesDOB.setInternalName(rs3.getString("INT_CHARGE_NAME"));
          chargesDOB.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
          chargesDOB.setCostIncurredAt(rs3.getString("COST_INCURREDAT"));
          chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));
          
          if(reportDOB!=null && reportDOB.getChangeDesc().equals(chargesDOB.getChargeDescriptionId()))
              finalDOB.setEmailChargeName(chargesDOB.getExternalName());
          
          chargeInfoList  = new ArrayList();
          chargeInfo      = new QuoteChargeInfo();
          chargeInfoList.add(chargeInfo);
          
          chargesDOB.setChargeInfoList(chargeInfoList);
          
          chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
          
          if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs3.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
          
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
            
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
          
         if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          else
          {
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
              
          chargeInfo.setSellRate(sellRate);
          if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
        }            
      }*/
      chargesDOB  = null;
      //rs4 ResultSet is used for Cartage Charges
      
      //commented by govind FOR NOT TAKING THE PICK UP AND DELUVERY CHARGES FROM VIEW PROC AND TO TAKE FROM CARTAGES PROC.
      //Un commented by Kiran for the issue 266732 for not getting the margintest flag in escalated view pages
    /*  while(rs4.next())
      {
         if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
        {
        if( (masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().trim().length()!=0)
            ||(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",")==-1) 
          )
        {
         // isSingleShipperZone = true;
          if(chargesDOB!=null && 
           (
             (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
             ||
             (chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID")))
           )
          )
         {
            chargeInfo  = new QuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
            
            if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs4.getString("CURRENCY"));
            else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            
            chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
            //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            weightBreak   =   rs4.getString("WEIGHT_BREAK");
            rateType      =   rs4.getString("RATE_TYPE");
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else
                chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            }
            chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
              if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
            {  
              chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
              {
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
             
              
              }
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
            else
            {
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
          
            chargeInfo.setSellRate(sellRate);
          if(rs4.getString("margin_test_flag")!=null)
          {
            if(rs4.getString("margin_test_flag").equals("Y"))
            chargeInfo.setMarginTestFailed(true);
            else 
            chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
      
         }
         else
         {         
            chargesDOB  = new QuoteCharges();
            
            chargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
            chargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                     
            chargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
            
            //Added By Kishore Podili for Multiple Zone Codes
            chargesDOB.setChargeDescriptionId(rs4.getString("RATE_DESCRIPTION"));
            chargesDOB.setCostIncurredAt(rs4.getString("COST_INCURREDAT"));
            chargesDOB.setTerminalId(rs4.getString("TERMINALID"));
            chargesDOB.setSelectedFlag(rs4.getString("SELECTED_FLAG"));
            chargesDOB.setMarginDiscountFlag(rs4.getString("MARGIN_DISCOUNT_FLAG"));
            
            chargeInfoList  = new ArrayList();
            chargeInfo      = new QuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
            if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs4.getString("CURRENCY"));
            else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
            //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            weightBreak   =   rs4.getString("WEIGHT_BREAK");
            rateType      =   rs4.getString("RATE_TYPE");
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else
                chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            }
            
            chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
           if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
            {  
              chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
              {
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
                   }
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
            else
            {
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
              {
                sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
                }
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
              
            chargeInfo.setSellRate(sellRate);
            if(rs4.getString("margin_test_flag")!=null)
            {
              if(rs4.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
            
            if("Pickup".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
            {
              if(originChargesList==null)
                originChargesList = new ArrayList();
              originChargesList.add(chargesDOB);
            }
         }
        }
        else
        {
          tmpFinalDOB = new QuoteFinalDOB();
          tmpFinalDOB.setMasterDOB(masterDOB);
          tmpFinalDOB.setOriginChargesList(originChargesList);
       //   Logger.info(FILE_NAME,"masterDOB.getShipperZones()::"+masterDOB.getShipperZones());
          //Logger.info(FILE_NAME,"masterDOB.getConsigneeZones()::"+masterDOB.getConsigneeZones());
          if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0))
              tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);
       
          //finalDOB.setOriginChargesList(tmpFinalDOB.getOriginChargesList());
          originChargesList = tmpFinalDOB.getOriginChargesList();
          
        //  Logger.info(FILE_NAME,"tmpFinalDOB.getPickUpCartageRatesList()"+tmpFinalDOB.getPickUpCartageRatesList());
          
          if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());          
        }
      }
      else if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
       { 
        if((masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",")==-1))
        {
         // isSingleConsigneeZone = true; 
          if(deliveryChargesDOB!=null && 
           (
             (deliveryChargesDOB.getBuyChargeId()!=null && deliveryChargesDOB.getBuyChargeId().equals(rs4.getString("BUY_CHARGE_ID")))
             ||
             (deliveryChargesDOB.getSellChargeId()!=null && deliveryChargesDOB.getSellChargeId().equals(rs4.getString("SELLCHARGEID")))
           )
          )
         {
            chargeInfo  = new QuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
            if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs4.getString("CURRENCY"));
            else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
            //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            weightBreak   =   rs4.getString("WEIGHT_BREAK");
            rateType      =   rs4.getString("RATE_TYPE");
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else
                chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            }
            
            chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
            
            if("M".equalsIgnoreCase(deliveryChargesDOB.getMarginDiscountFlag()) || deliveryChargesDOB.getMarginDiscountFlag()==null)
            {  
              chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
            else
            {
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
          
            chargeInfo.setSellRate(sellRate);
            if(rs4.getString("margin_test_flag")!=null)
            {
              if(rs4.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
               chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
         }
        else
         {         
            deliveryChargesDOB  = new QuoteCharges();
            
            deliveryChargesDOB.setSellChargeId(rs4.getString("SELLCHARGEID"));
            deliveryChargesDOB.setBuyChargeId(rs4.getString("BUY_CHARGE_ID"));
                     
            deliveryChargesDOB.setSellBuyFlag(rs4.getString("SEL_BUY_FLAG"));
            
            //Added By Kishore Podili for Multiple Zone Codes
            deliveryChargesDOB.setChargeDescriptionId(rs4.getString("RATE_DESCRIPTION"));
            
            deliveryChargesDOB.setCostIncurredAt(rs4.getString("COST_INCURREDAT"));
            deliveryChargesDOB.setTerminalId(rs4.getString("TERMINALID"));
            deliveryChargesDOB.setSelectedFlag(rs4.getString("SELECTED_FLAG"));
            deliveryChargesDOB.setMarginDiscountFlag(rs4.getString("MARGIN_DISCOUNT_FLAG"));
            
            chargeInfoList  = new ArrayList();
            chargeInfo      = new QuoteChargeInfo();
            
            chargeInfoList.add(chargeInfo);
            
            deliveryChargesDOB.setChargeInfoList(chargeInfoList);
            
            chargeInfo.setBreakPoint(rs4.getString("CHARGESLAB"));
            if(rs4.getString("CURRENCY")!=null && rs4.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs4.getString("CURRENCY"));
            else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
            chargeInfo.setBuyRate(rs4.getDouble("BUYRATE"));
            chargeInfo.setRecOrConSellRrate(rs4.getDouble("SELLRATE"));
            chargeInfo.setSellChargeMargin(rs4.getDouble("MARGINVALUE"));
            chargeInfo.setSellChargeMarginType(rs4.getString("MARGIN_TYPE"));
            chargeInfo.setRateIndicator(rs4.getString("RATE_INDICATOR"));
            //chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            weightBreak = rs4.getString("WEIGHT_BREAK");
            rateType    = rs4.getString("RATE_TYPE");
            
            if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
            {
              chargeInfo.setBasis("Per Shipment");
            }
            else
            {
              if("LIST".equalsIgnoreCase(rs4.getString("WEIGHT_BREAK")))
              {
                chargeInfo.setBasis("Per Container");
              }
              else if("Per Kg".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Kilogram");
              }
              else if("Per Lb".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Pound");
              }
              else if("Per CFT".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Feet");
              }
              else if("Per CBM".equalsIgnoreCase(rs4.getString("CHARGEBASIS")))
              {
                chargeInfo.setBasis("Per Cubic Meter");
              }
              else
                chargeInfo.setBasis(rs4.getString("CHARGEBASIS"));
            }
            
            chargeInfo.setRatio(rs4.getString("DENSITY_RATIO"));
            
            if("M".equalsIgnoreCase(deliveryChargesDOB.getMarginDiscountFlag()) || deliveryChargesDOB.getMarginDiscountFlag()==null)
            {  
              chargeInfo.setMargin(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setMarginType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+rs4.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("BUYRATE")+(rs4.getDouble("BUYRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
            else
            {
              chargeInfo.setDiscount(rs4.getDouble("MARGINVALUE"));
              chargeInfo.setDiscountType(rs4.getString("MARGIN_TYPE"));
              
              if("A".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-rs4.getDouble("MARGINVALUE");
              else if("P".equalsIgnoreCase(rs4.getString("MARGIN_TYPE")))
                sellRate  = rs4.getDouble("SELLRATE")-(rs4.getDouble("SELLRATE")*rs4.getDouble("MARGINVALUE")/100);
            }
              
            chargeInfo.setSellRate(sellRate);
            if(rs4.getString("margin_test_flag")!=null)
            {
              if(rs4.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
            }
            else
              chargeInfo.setMarginTestFailed(false);
              
            if("Delivery".equalsIgnoreCase(rs4.getString("COST_INCURREDAT")))
            {
              if(destChargesList==null)
                destChargesList = new ArrayList();
              destChargesList.add(deliveryChargesDOB);
            }
         }
        }
        else
        {
          tmpFinalDOB = new QuoteFinalDOB();
          tmpFinalDOB.setMasterDOB(masterDOB);
          tmpFinalDOB.setDestChargesList(destChargesList);
     //     Logger.info(FILE_NAME,"masterDOB.getConsigneeZones()(quote rate info::"+masterDOB.getConsigneeZones());
          if((masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
                tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);        
          
          destChargesList = tmpFinalDOB.getDestChargesList();
          
          
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
            finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());           
          
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());
        }
       }
      }*/
      
      /*String tempShipperCode    =   null;
      String tempShipperZones   =   null;
      String tempConsigneeCode  =   null;
      String tempConsigneeZones =   null;
      boolean isShipperFetched  =   false;
      boolean isConsigneeFetched=   false;
       
      if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().trim().length()!=0)
          isShipperZipCode        =  true;
      if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",")==-1)
          isSingleShipperZone = true;
      if(masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().trim().length()!=0)
          isConsigneeZipCode      =  true;
      if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",")==-1)
          isSingleConsigneeZone   =  true;
      
      /*if((isSingleShipperZone && ! isShipperZipCode)||(isSingleConsigneeZone && !isConsigneeZipCode))
      {
        finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);//@@So fetch the Zip Zone Mapping
      }
      else*/
      //{
     /* if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
        {
          if(isShipperZipCode || isSingleShipperZone)
          {
             tempShipperCode  = masterDOB.getShipperZipCode();
             tempShipperZones = masterDOB.getShipperZones();
             masterDOB.setShipperZipCode(null);
             masterDOB.setShipperZones(null);
             isShipperFetched = true;
          }
          if(isConsigneeZipCode || isSingleConsigneeZone)
          {
             tempConsigneeCode  = masterDOB.getConsigneeZipCode();
             tempConsigneeZones = masterDOB.getConsigneeZones();
             masterDOB.setConsigneeZipCode(null);
             masterDOB.setConsigneeZones(null);
             isConsigneeFetched = true;
          }

          if(!(isShipperFetched && isConsigneeFetched))
          {
              tmpFinalDOB = new QuoteFinalDOB();
              tmpFinalDOB.setMasterDOB(masterDOB);
              tmpFinalDOB.setOriginChargesList(originChargesList);
              tmpFinalDOB.setDestChargesList(destChargesList);
              logger.info("tmpFinalDOB ::"+tmpFinalDOB.getMasterDOB().getBuyRatesPermission());
              tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);
              originChargesList = tmpFinalDOB.getOriginChargesList();
              destChargesList = tmpFinalDOB.getDestChargesList();
              
              if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
                finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
              if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
                finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
              
              finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
              finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());              
          }
          if(tempShipperCode!=null)
          {
              masterDOB.setShipperZipCode(tempShipperCode);
              masterDOB.setShipperZones(tempShipperZones);
          }
          if(tempConsigneeCode!=null)
          {
            masterDOB.setConsigneeZipCode(tempConsigneeCode);
            masterDOB.setConsigneeZones(tempConsigneeZones);
          }
          
          finalDOB.setMasterDOB(masterDOB);
          
          if(!isShipperFetched || !isConsigneeFetched)
          {
            finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);
          }         
          
          //originChargesList = tmpFinalDOB.getOriginChargesList();
          //destChargesList = tmpFinalDOB.getDestChargesList();
          
          /*if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
            finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
          
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());*/
        /*}*/
     // }
      //@@Moved after the cartage charges rs by Yuvraj.
      chargesDOB  = null;
      //rs3 ResultSet is used for getting Charges  
      while(rs3.next())
      {
        if(chargesDOB!=null && 
           (
             (chargesDOB.getBuyChargeId()!=null && chargesDOB.getBuyChargeId().equals(rs3.getString("BUY_CHARGE_ID")))
             ||
             (chargesDOB.getSellChargeId()!=null && chargesDOB.getSellChargeId().equals(rs3.getString("SELLCHARGEID")))
           )
          )
        {
          chargeInfo  = new QuoteChargeInfo();
          
          chargeInfoList.add(chargeInfo);
          
          chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
          
          if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs3.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
              
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
          if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          else
          {
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          weightBreak   = rs3.getString("WEIGHT_BREAK");
          rateType      = rs3.getString("RATE_TYPE");
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            //weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
              
          chargeInfo.setSellRate(sellRate);
         if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
                chargeInfo.setMarginTestFailed(true);
              else 
                chargeInfo.setMarginTestFailed(false);
          }
        else
          chargeInfo.setMarginTestFailed(false);
        }
        else
        {        
          chargesDOB  = new QuoteCharges();
          
          if("Origin".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
          {
            if(originChargesList==null)
              originChargesList = new ArrayList();
            originChargesList.add(chargesDOB);
          }
          else if("Destination".equalsIgnoreCase(rs3.getString("COST_INCURREDAT")))
          {
            if(destChargesList==null)
              destChargesList = new ArrayList();
            destChargesList.add(chargesDOB);
          }
          
          chargesDOB.setBuyChargeId(rs3.getString("BUY_CHARGE_ID"));
          chargesDOB.setSellChargeId(rs3.getString("SELLCHARGEID"));                
          chargesDOB.setSellBuyFlag(rs3.getString("SEL_BUY_FLAG"));
          
          chargesDOB.setChargeId(rs3.getString("CHARGE_ID"));
          chargesDOB.setTerminalId(rs3.getString("TERMINALID"));
          chargesDOB.setMarginDiscountFlag(rs3.getString("MARGIN_DISCOUNT_FLAG"));
          chargesDOB.setChargeDescriptionId(rs3.getString("CHARGEDESCID"));
          chargesDOB.setInternalName(rs3.getString("INT_CHARGE_NAME"));
          chargesDOB.setExternalName(rs3.getString("EXT_CHARGE_NAME"));
          chargesDOB.setCostIncurredAt(rs3.getString("COST_INCURREDAT"));
          chargesDOB.setSelectedFlag(rs3.getString("SELECTED_FLAG"));
          
          if(reportDOB!=null && reportDOB.getChangeDesc().equals(chargesDOB.getChargeDescriptionId()))
              finalDOB.setEmailChargeName(chargesDOB.getExternalName());
          
          chargeInfoList  = new ArrayList();
          chargeInfo      = new QuoteChargeInfo();
          chargeInfoList.add(chargeInfo);
          
          chargesDOB.setChargeInfoList(chargeInfoList);
          
          chargeInfo.setBreakPoint(rs3.getString("CHARGESLAB"));
          
          if(rs3.getString("CURRENCY")!=null && rs3.getString("CURRENCY").trim().length()!=0)
              chargeInfo.setCurrency(rs3.getString("CURRENCY"));
          else
              chargeInfo.setCurrency(masterDOB.getTerminalCurrency());
          
          chargeInfo.setBuyRate(rs3.getDouble("BUYRATE"));
          chargeInfo.setRecOrConSellRrate(rs3.getDouble("SELLRATE"));
          chargeInfo.setSellChargeMargin(rs3.getDouble("MARGINVALUE"));
          chargeInfo.setSellChargeMarginType(rs3.getString("MARGIN_TYPE"));
          chargeInfo.setRateIndicator(rs3.getString("RATE_INDICATOR"));
          weightBreak     =   rs3.getString("WEIGHT_BREAK");
          rateType        =   rs3.getString("RATE_TYPE");
          //chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
          if("BASE".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MIN".equalsIgnoreCase(chargeInfo.getBreakPoint()) || "MAX".equalsIgnoreCase(chargeInfo.getBreakPoint()) || ("SLAB".equalsIgnoreCase(weightBreak) && "FLAT".equalsIgnoreCase(rateType)) || ("BOTH".equalsIgnoreCase(rateType) && "F".equalsIgnoreCase(chargeInfo.getRateIndicator())))
          {
            chargeInfo.setBasis("Per Shipment");
          }
          else
          {
            chargeInfo.setBasis(rs3.getString("CHARGEBASIS"));
            
            //weightBreak   = rs3.getString("WEIGHT_BREAK");
          
            if(weightBreak!=null && ("Percent".equalsIgnoreCase(weightBreak) || weightBreak.endsWith("%")))
              chargeInfo.setPercentValue(true);
            
          }
          chargeInfo.setRatio(rs3.getString("DENSITY_RATIO"));
          
         if("M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()) || chargesDOB.getMarginDiscountFlag()==null)
          {  
            chargeInfo.setMargin(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setMarginType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("BUYRATE")+(rs3.getDouble("BUYRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
          else
          {
            chargeInfo.setDiscount(rs3.getDouble("MARGINVALUE"));
            chargeInfo.setDiscountType(rs3.getString("MARGIN_TYPE"));
            
            if("A".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-rs3.getDouble("MARGINVALUE");
            else if("P".equalsIgnoreCase(rs3.getString("MARGIN_TYPE")))
              sellRate  = rs3.getDouble("SELLRATE")-(rs3.getDouble("SELLRATE")*rs3.getDouble("MARGINVALUE")/100);
          }
              
          chargeInfo.setSellRate(sellRate);
          if(rs3.getString("margin_test_flag")!=null)
          {
              if(rs3.getString("margin_test_flag").equals("Y"))
              chargeInfo.setMarginTestFailed(true);
              else 
              chargeInfo.setMarginTestFailed(false);
          }
          else
            chargeInfo.setMarginTestFailed(false);
        }            
      }
      
      //@@ For putting the delivery charge at the end of the list, if it exists.
      if(deliveryChargesDOB!=null)
      {
          if(destChargesList==null)
                destChargesList = new ArrayList();
          destChargesList.add(deliveryChargesDOB);
      }
      //@@
      
      /*
       *  --------- Commented By Kishore for Multiple Zone Codes ---------
       * String tempShipperCode    =   null;
      String tempShipperZones   =   null;
      String tempConsigneeCode  =   null;
      String tempConsigneeZones =   null;
      boolean isShipperFetched  =   false;
      boolean isConsigneeFetched=   false;
       
      if(masterDOB.getShipperZipCode()!=null && masterDOB.getShipperZipCode().trim().length()!=0)
          isShipperZipCode        =  true;
      if(masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().indexOf(",")==-1)
          isSingleShipperZone = true;
      if(masterDOB.getConsigneeZipCode()!=null && masterDOB.getConsigneeZipCode().trim().length()!=0)
          isConsigneeZipCode      =  true;
      if(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().indexOf(",")==-1)
          isSingleConsigneeZone   =  true;
       --------- Commented By Kishore for Multiple Zone Codes ---------
      */
      
      /*if((isSingleShipperZone && ! isShipperZipCode)||(isSingleConsigneeZone && !isConsigneeZipCode))
      {
        finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);//@@So fetch the Zip Zone Mapping
      }
      else*/
      //{
      if((masterDOB.getShipperZones()!=null && masterDOB.getShipperZones().trim().length()!=0)
            ||(masterDOB.getConsigneeZones()!=null && masterDOB.getConsigneeZones().trim().length()!=0))
        {
          /*
           *  --------- Commented By Kishore for Multiple Zone Codes ---------
           *  if(isShipperZipCode || isSingleShipperZone)
          {
             tempShipperCode  = masterDOB.getShipperZipCode();
             tempShipperZones = masterDOB.getShipperZones();
             masterDOB.setShipperZipCode(null);
             masterDOB.setShipperZones(null);
             isShipperFetched = true;
          }
          if(isConsigneeZipCode || isSingleConsigneeZone)
          {
             tempConsigneeCode  = masterDOB.getConsigneeZipCode();
             tempConsigneeZones = masterDOB.getConsigneeZones();
             masterDOB.setConsigneeZipCode(null);
             masterDOB.setConsigneeZones(null);
             isConsigneeFetched = true;
          }

          if(!(isShipperFetched && isConsigneeFetched))
          {
           --------- Commented By Kishore for Multiple Zone Codes ---------*/
    	 
          masterDOB.setOperation(masterDOB.getOperation()== null?"View":masterDOB.getOperation()); //Added by govind for not getting origin charges in quote view bcoz masterdob.operation is null  
    	  if((masterDOB.getShipperZipCode()!=null || masterDOB.getShipperZones()!=null || masterDOB.getConsigneeZipCode()!=null || masterDOB.getConsigneeZones()!=null)
             )
              { 
               
            	 tmpFinalDOB = new QuoteFinalDOB();
                tmpFinalDOB.setMasterDOB(masterDOB);
                
                //Commented By Kishore for Multiple Zone Codes 
                //tmpFinalDOB.setOriginChargesList(originChargesList);
                //tmpFinalDOB.setDestChargesList(destChargesList);
                //Added By govind for not getting origin and destination charges in detailed view
             if("View".equalsIgnoreCase(masterDOB.getOperation()) ||masterDOB.getOperation()== null ){
                masterDOB.setOperation("View");
                tmpFinalDOB.setOriginChargesList(originChargesList);
              tmpFinalDOB.setDestChargesList(destChargesList);
              }
     
            tmpFinalDOB = quoteDAO.getCartages(tmpFinalDOB);
                originChargesList = tmpFinalDOB.getOriginChargesList();
                destChargesList = tmpFinalDOB.getDestChargesList();
                
                //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
                if(tmpFinalDOB.getPickupChargeBasisList()!=null)
                    finalDOB.setPickupChargeBasisList(tmpFinalDOB.getPickupChargeBasisList());
                if(tmpFinalDOB.getDelChargeBasisList()!=null)
                    finalDOB.setDelChargeBasisList(tmpFinalDOB.getDelChargeBasisList());
                //Added By Kishore For the ChargeBasis in the Annexure PDF on 06-Jun-11
                  
                if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
                  finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
                if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
                  finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
                if(tmpFinalDOB.getPickupWeightBreaks()!=null)
                  finalDOB.setPickupWeightBreaks(tmpFinalDOB.getPickupWeightBreaks());
                if(tmpFinalDOB.getDeliveryWeightBreaks()!=null)
                  finalDOB.setDeliveryWeightBreaks(tmpFinalDOB.getDeliveryWeightBreaks());
                
                finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
                finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap()); 
              }
         //  --------- Commented By Kishore for Multiple Zone Codes ---------
             /* }
          if(isShipperFetched)
          {
              masterDOB.setShipperZipCode(tempShipperCode);
              masterDOB.setShipperZones(tempShipperZones);
          }
          if(isConsigneeFetched)
          {
            masterDOB.setConsigneeZipCode(tempConsigneeCode);
            masterDOB.setConsigneeZones(tempConsigneeZones);
          }
          
          finalDOB.setMasterDOB(masterDOB);
          
          if(!isShipperFetched || !isConsigneeFetched || !isShipperZipCode || !isConsigneeZipCode)
          {
            finalDOB    =  quoteDAO.getZipZoneMapping(finalDOB);
          }         
           --------- Commented By Kishore for Multiple Zone Codes ---------
          */        
          
          //originChargesList = tmpFinalDOB.getOriginChargesList();
          //destChargesList = tmpFinalDOB.getDestChargesList();
          
          /*if(tmpFinalDOB.getPickUpCartageRatesList()!=null)
            finalDOB.setPickUpCartageRatesList(tmpFinalDOB.getPickUpCartageRatesList());
          if(tmpFinalDOB.getDeliveryCartageRatesList()!=null)
            finalDOB.setDeliveryCartageRatesList(tmpFinalDOB.getDeliveryCartageRatesList());
          
          finalDOB.setPickZoneZipMap(tmpFinalDOB.getPickZoneZipMap());
          finalDOB.setDeliveryZoneZipMap(tmpFinalDOB.getDeliveryZoneZipMap());*/
        }
      
      if(originChargesList!=null || destChargesList!=null)
      {
        if(originChargesList!=null)
        {
          int originChargesSize = 0;
          originChargesSize     = originChargesList.size();

          int[] originSelectedIndices = new int[originChargesSize];
          
          for(int i=0;i<originChargesSize;i++)
          {
            originSelectedIndices[i]  = i;
          }
          finalDOB.setOriginChargesList(originChargesList);
          finalDOB.setSelectedOriginChargesListIndices(originSelectedIndices);
        }
        if(destChargesList!=null)
        {
          int destChargesSize = 0;
          destChargesSize     = destChargesList.size();
          int[] destSelectedIndices = new int[destChargesSize];
              
          for(int i=0;i<destChargesSize;i++)
          {
            destSelectedIndices[i]  = i;
          }
          finalDOB.setSelctedDestChargesListIndices(destSelectedIndices);
          finalDOB.setDestChargesList(destChargesList);
        }
      }
	//Added by Mohan for Issue No.219976 on 04-11-2010
      String[]  inotes = null;
      String[]  enotes = null;
      ArrayList iNotes = new ArrayList();
      ArrayList eNotes =  new ArrayList();
      int notesCount  = 0 ;  
      while(rs5.next())
      {
    	  iNotes.add((rs5.getString("INTERNAL_NOTES")!=null)?rs5.getString("INTERNAL_NOTES"):"");
    	  eNotes.add((rs5.getString("EXTERNAL_NOTES")!=null)?rs5.getString("EXTERNAL_NOTES"):"");
      }
      notesCount = iNotes.size();      
      inotes = new String[notesCount];
      enotes = new String[notesCount];
      for( int i=0; i<notesCount;i++)
      {
    	  inotes[i] = (String)iNotes.get(i);
    	  enotes[i] = (String)eNotes.get(i);
      }
      //Commented by Mohan for Issue No.219976 on 04-11-2010
      /*list_exNotes = new ArrayList();
      String[]  notes = null;

      String[] arr = null;
      String arr1="";
      int j = 0;
      while(rs5.next())
      {
        list_exNotes.add((rs5.getString("EXTERNAL_NOTES")!=null)?rs5.getString("EXTERNAL_NOTES"):"");
      }
       if(list_exNotes!=null)
      {
        notes   = new String[list_exNotes.size()];
      }
       if(list_exNotes != null)
      {
    	   int exNoteSize	=	list_exNotes.size();
        for(int i=0;i<exNoteSize;i++)
        {
         if(list_exNotes.get(i)!="")
         {
           notes[j]  = (String)list_exNotes.get(i);
           if((notes[j].trim()).length()>0)
           {
          arr = notes[j] .split("");
            for( int k=0;k<arr.length-1;k++)
            {
             
                 if((arr[k].trim()).length()>0)
                {
                  arr1 =arr1.concat(arr[k].trim());
                }
             else
             {
               if((arr[k+1].trim()).length()>0)
               {
                  arr1 =arr1.concat(" ");
               }
             }
            }
               arr1 =arr1.concat(arr[arr.length-1]);
               notes[j] = arr1;
            arr1 =  "";
          
          j++;
           }
         }

        }
      }*/

      finalDOB.setInternalNotes(inotes);//Modified by Mohan for Issue No.219976 on 30102010
      finalDOB.setExternalNotes(enotes);//Modified by Mohan for Issue No.219976 on 30102010
    }    
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"getQuoteIds6) "+e.toString());
      logger.error(FILE_NAME+"getQuoteIds6) "+e.toString());
      //Logger.error(FILE_NAME,"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
      logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
			throw new EJBException(e.toString());    
    }
    finally
    {
      try
      {
        if(rs5!=null)rs5.close();
        if(rs4!=null)rs4.close();
        if(rs3!=null)rs3.close();
        if(rs2!=null)rs2.close();
        if(cStmt!=null)cStmt.close();
      }
      catch(SQLException se)
      {
        se.printStackTrace();
        //Logger.error(FILE_NAME,"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+se.toString());
        logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+se.toString());
			  throw new EJBException(se.toString());      
      }
      catch(Exception e)
      {
        e.printStackTrace();
        //Logger.error(FILE_NAME,"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
        logger.error(FILE_NAME+"QMSQuoteSessionBean[getRatesChargesInfo(quoteId)] -> "+e.toString());
        throw new EJBException(e.toString());        
      }
      ConnectionUtil.closeConnection(connection);      
    }
    return finalDOB;
  }   

  public String validateCurrency(String baseCurrency)
  {
    String errormsg = null;
    QMSQuoteDAO qMSQuoteDAO = null;
    try
    {
      qMSQuoteDAO = new QMSQuoteDAO();
      errormsg    =  qMSQuoteDAO.validateCurrency(baseCurrency); 
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,"in validateCurrency()"+e);
      logger.info(FILE_NAME+"in validateCurrency()"+e);
      throw new EJBException();
    }
    return errormsg; 
  }
  public QuoteFinalDOB getQuoteContentDtl(QuoteFinalDOB finalDOB) throws EJBException
  {
    Connection          conn        =   null;
    PreparedStatement   pstmt       =   null;
    ResultSet           rs          =   null;
    PreparedStatement   pstmt1       =   null;
    ResultSet           rs1          =   null;
    ArrayList           descList          =   null;
    ArrayList           hdrFtrList        =   null;
    ArrayList           alignList         =   null;
    ArrayList           defaultDescList   =   null;
    ArrayList           defaultHdrFtrList =   null;
    StringBuffer        terminalQry       =   null;
    String              quoteQry          =   "";
    String       salesPersonEmailQuery    =   "";//@@Added by kameswari for enhancements
    StringBuffer        contentQry        =   null;
    QuoteMasterDOB      masterDOB         =   null;
    String              shipModeStr       =   "";
    String              operation         =   "";
     
    String[]            contentArray        = null;
    String[]            headerFooterArray   = null;
    String[]            alignArray          = null;
    
    String[]            defaultContentArray  = null;
    String[]            defaultHdrFooterArray= null;
    String                terminalIdQuery     ="SELECT TERMINAL_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?";
    try
    {
      conn              =     this.getConnection();
      terminalQry       =     new StringBuffer("");
      contentQry        =     new StringBuffer("");
      descList          =     new ArrayList();
      hdrFtrList        =     new ArrayList();
      alignList         =     new ArrayList();
      defaultDescList   =     new ArrayList();
      defaultHdrFtrList =     new ArrayList(); 
      masterDOB         =     finalDOB.getMasterDOB();
      operation         =     masterDOB.getOperation();
     
      if("View".equalsIgnoreCase(operation))
      {
            pstmt1      =     conn.prepareStatement(terminalIdQuery);
            pstmt1.setString(1,masterDOB.getQuoteId());
            rs1         =     pstmt1.executeQuery();
            while(rs1.next())
            {
                  masterDOB.setTerminalId(rs1.getString("TERMINAL_ID"));   
            }
      }  
      quoteQry          =     "SELECT CM.DESCRIPTION,QCD.HEADER,QCD.ALIGN FROM QMS_CONTENTDTLS CM,QMS_QUOTE_HF_DTL QCD "+
                          "WHERE CM.CONTENTID=QCD.CONTENT AND QCD.QUOTE_ID = ? AND CM.FLAG ='F' ORDER BY HEADER DESC,CLEVEL";
      


      salesPersonEmailQuery ="SELECT EMAILID FROM FS_USERMASTER WHERE EMPID=?";
      
    
     // if("H".equalsIgnoreCase(masterDOB.getAccessLevel()))
      //@@ Commented & Added by subrahmanyam for the pbn id: 210886 on 12-Jul-10
      //if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()))
      if("DHLCORP".equalsIgnoreCase(masterDOB.getTerminalId()) || "DHLASPA".equalsIgnoreCase(masterDOB.getTerminalId()))
      {
      //  terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER)");
      terminalQry.append( " (SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE TERMINALID='"+masterDOB.getTerminalId()+"')");
      }
      else
      {
        terminalQry.append( "(SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '")
                   .append(masterDOB.getTerminalId()).append("'")
                   .append( " UNION ")
                   .append( " SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' ")
                   .append( " UNION ")
                   .append( " SELECT '").append(masterDOB.getTerminalId()).append("' TERM_ID FROM DUAL ")
                   .append( " UNION ")
                   .append( " SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '")
                   .append(masterDOB.getTerminalId()).append("')");
      }
      if(!finalDOB.isMultiModalQuote())
      {
        if(masterDOB.getShipmentMode()==1)
          shipModeStr = " AND SHIPMENTMODE IN (1,3,5,7)";
        else if(masterDOB.getShipmentMode()==2)
          shipModeStr = " AND SHIPMENTMODE IN (2,3,6,7)";
        else if(masterDOB.getShipmentMode()==4)
          shipModeStr = " AND SHIPMENTMODE IN (4,5,6,7)";
      }
      
      contentQry.append("SELECT DESCRIPTION,HEADERFOOTER,'L' ALIGN,(DECODE(ACCESSLEVEL,'H','A','A','H','O'))ACCESSLEVEL,FLAG FROM QMS_CONTENTDTLS WHERE ACTIVEINACTIVE='N' AND INVALIDATE='F' ")
                .append(shipModeStr).append("AND FLAG ='T' AND TEMINALID IN ").append(terminalQry)
               // .append(" ORDER  BY ACCESSLEVEL,CONTENTID"); commented by VLAKSHMI for issue 172986 on 10/6/2009
               .append(" ORDER  BY CONTENTID");
      
      pstmt   =   conn.prepareStatement(contentQry.toString());
      
      rs      =   pstmt.executeQuery();
      
      if("QuoteGrouping".equalsIgnoreCase(operation))
      {
        while(rs.next())
        {
          defaultDescList.add(rs.getString("DESCRIPTION"));
          defaultHdrFtrList.add(rs.getString("HEADERFOOTER"));
        }
      }
      else
      {
        while(rs.next())
        {
          descList.add(rs.getString("DESCRIPTION"));
          hdrFtrList.add(rs.getString("HEADERFOOTER"));
          alignList.add(rs.getString("ALIGN"));//@@Default Left Aligned
        }
      }
      
      if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      
      pstmt         =     conn.prepareStatement(quoteQry);
      
      pstmt.setLong(1,masterDOB.getUniqueId());
      
      rs            =     pstmt.executeQuery();
      
      while(rs.next())
      {
        descList.add(rs.getString("DESCRIPTION"));
        hdrFtrList.add(rs.getString("HEADER"));
        alignList.add(rs.getString("ALIGN"));
      }
      
      if(descList.size()>0)
      {
        contentArray            = new String[descList.size()];
        headerFooterArray       = new String[hdrFtrList.size()];
        alignArray              = new String[alignList.size()];
      }
     int desListSize	=	descList.size();
      for(int i=0;i<desListSize;i++)
      {
        contentArray[i]       = (String)descList.get(i);
        headerFooterArray[i]  = (String)hdrFtrList.get(i);
        alignArray[i]         = (String)alignList.get(i);
      }
      
      int defaultContentSize  = defaultDescList.size();
      
      if(defaultContentSize >0)
      {
        defaultContentArray   =   new String[defaultContentSize];
        defaultHdrFooterArray =   new String[defaultContentSize];
      }
      
      for(int i=0;i<defaultContentSize;i++)
      {
        defaultContentArray[i]   =   (String)defaultDescList.get(i);
        defaultHdrFooterArray[i] =   (String)defaultHdrFtrList.get(i);
      }
       if(rs!=null)
        rs.close();
      if(pstmt!=null)
        pstmt.close();
      if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
       {
          pstmt = conn.prepareStatement(salesPersonEmailQuery);
          pstmt.setString(1,masterDOB.getSalesPersonCode());
          rs  = pstmt.executeQuery();
          if(rs.next())
          {
              masterDOB.setSalesPersonEmail(rs.getString("EMAILID"));
          }
       }   
      masterDOB.setHeaderFooter(headerFooterArray); 
      masterDOB.setContentOnQuote(contentArray);
      masterDOB.setAlign(alignArray);
      
      masterDOB.setDefaultContent(defaultContentArray);
      masterDOB.setDefaultHeaderFooter(defaultHdrFooterArray);
      
      finalDOB.setMasterDOB(masterDOB);
    
      
   
 
    }
    catch(EJBException ejb)
    {
      //Logger.error(FILE_NAME,"EJBException while fetching Quote Content Header Footer Details "+ejb);
      logger.error(FILE_NAME+"EJBException while fetching Quote Content Header Footer Details "+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Exception while fetching Quote Content Header Footer Details "+e);
      logger.error(FILE_NAME+"Exception while fetching Quote Content Header Footer Details "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(conn,pstmt,rs);
      ConnectionUtil.closeConnection(conn,pstmt1,rs1);
    }
    return finalDOB;
  }
  public QuoteFinalDOB getQuoteDetails(String quoteId,String buyRatesFlag,ESupplyGlobalParameters loginbean) throws EJBException
   {
     QuoteFinalDOB           finalDOB       = null;
     QuoteMasterDOB          masterDOB      = null;
     QuoteAttachmentDOB      attachmentDOB  = null;
     ArrayList               attachmentIdList = new  ArrayList();       
    
    try
    {
         finalDOB = getMasterInfo(quoteId,loginbean);
         masterDOB = finalDOB.getMasterDOB();
         masterDOB.setUserId(loginbean.getUserId());
         masterDOB.setBuyRatesPermission(buyRatesFlag);
         masterDOB.setCompanyId(loginbean.getCompanyId());
         finalDOB.setMasterDOB(masterDOB);
         finalDOB = getQuoteHeader(finalDOB);
         finalDOB = getRatesChargesInfo(quoteId,finalDOB,null);
         finalDOB = getQuoteContentDtl(finalDOB);
      
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in getUpdatedQuoteDetails"+e);
      logger.error(FILE_NAME+"Error in getUpdatedQuoteDetails"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }  
    return finalDOB;
   }
   
   public CostingMasterDOB getContactPersonDetails(CostingMasterDOB costingMasterDOB) throws EJBException
   {
     Connection         conn     =   null;
     PreparedStatement  pstmt    =   null;
     ResultSet          rs       =   null;
     String query                =   "SELECT EMAILID,FAX FROM QMS_CUST_CONTACTDTL WHERE CUSTOMERID=? AND SL_NO=? ORDER BY SL_NO";
     
     String[]           contactPersonIds        = null;
     int                length                  = 0;
     ArrayList          toEmailIds              = new ArrayList();
     ArrayList          toFaxIds                = new ArrayList();
     String[]           contactPersonsEmails    = null;
     String[]           contactPersonsFax       = null;
     
     try
     {
       conn     =     getConnection();
       pstmt    =     conn.prepareStatement(query);
       
       if(costingMasterDOB !=null)
       {
            contactPersonIds =  costingMasterDOB.getContactPersonIds();
            length           =  contactPersonIds.length;
            
            for(int i=0;i<length;i++)
            {
              pstmt.clearParameters();
              pstmt.setString(1,costingMasterDOB.getCustomerid());
              pstmt.setString(2,contactPersonIds[i]);
              rs             =  pstmt.executeQuery();
              
              if(rs.next())
              {
                toEmailIds.add(rs.getString("EMAILID"));
                toFaxIds.add(rs.getString("FAX"));
              }
              
                if(rs!=null)
                rs.close();
            }
            
            if(toEmailIds!=null && toEmailIds.size() > 0)
              contactPersonsEmails  = new String[toEmailIds.size()];
            if(toFaxIds!=null && toFaxIds.size() > 0)
              contactPersonsFax     = new String[toFaxIds.size()];
              
           if(toEmailIds!=null && toEmailIds.size() > 0)
           {
        	   int emailIdsSize	=	toEmailIds.size();
            for(int i=0;i<emailIdsSize;i++)
              contactPersonsEmails[i]   =   (String)toEmailIds.get(i);
           }
           if(toFaxIds!=null && toFaxIds.size() > 0)
           {
        	   int faxIdsSize	=	toFaxIds.size();
            for(int i=0;i<faxIdsSize;i++)
              contactPersonsFax[i]      =    (String)toFaxIds.get(i);
             }
           if(costingMasterDOB.getContactEmailIds()==null)//adeed by phani sekhar for wpbn 167678 on 20090415    
          costingMasterDOB.setContactEmailIds(contactPersonsEmails);
          costingMasterDOB.setContactsFax(contactPersonsFax);
       }
       
       
     }
     catch(Exception e)
     {
       e.printStackTrace();
       logger.error("Exception in getContactPersonDetails "+e);
       throw new EJBException(e);
     }
     finally
     {
        ConnectionUtil.closeConnection(conn,pstmt,rs);   
       
     }
     return costingMasterDOB;
   }
   //@@Added by Kameswari for the  WPBN issue-61295
    public String getEmailText(String terminalId,String quoteType) throws EJBException
    {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet   rs        = null;
        String      emailText = null;
        
        /*String      emailTextQuery  ="SELECT TEXT FROM QMS_EMAIL_TEXT_MASTER"+ 
          " WHERE TERMINAL_ID=((SELECT DISTINCT TERMINAL_ID FROM QMS_EMAIL_TEXT_MASTER WHERE"+
          " TERMINAL_ID=(SELECT TERMINAL_ID FROM(SELECT T1.TERMINAL_ID ,ROWNUM FROM(SELECT "+
          " T.TERMINAL_ID,DECODE(TM.OPER_ADMIN_FLAG,'O',1,'A',2,'H',3)OPER_ADMIN_FLAG FROM("+
          " SELECT TERMINAL_ID,COUNT(*)NO_ROWS FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID"+ 
          " IN(SELECT ? TERM_ID FROM DUAL UNION ALL SELECT PARENT_TERMINAL_ID TERM_ID"+ 
          " FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID"+ 
          " START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID TERM_ID FROM "+
          " FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')GROUP BY TERMINAL_ID)T,"+
          " FS_FR_TERMINALMASTER TM WHERE T.NO_ROWS>0 AND T.TERMINAL_ID=TM.TERMINALID ORDER BY"+
          " OPER_ADMIN_FLAG)T1 WHERE ROWNUM=1)))) AND QUOTE_TYPE=?"; */
             
        String      emailTextQuery  ="SELECT TEXT FROM QMS_EMAIL_TEXT_MASTER"+ 
          " WHERE TERMINAL_ID=((SELECT DISTINCT TERMINAL_ID FROM QMS_EMAIL_TEXT_MASTER WHERE"+
          " TERMINAL_ID=(SELECT TERMINAL_ID FROM(SELECT T1.TERMINAL_ID ,ROWNUM FROM(SELECT "+
          " T.TERMINAL_ID,DECODE(TM.OPER_ADMIN_FLAG,'O',1,'A',2,'H',3)OPER_ADMIN_FLAG FROM("+
          " SELECT TERMINAL_ID,COUNT(*)NO_ROWS FROM QMS_EMAIL_TEXT_MASTER WHERE TERMINAL_ID"+ 
          " IN(SELECT ? TERM_ID FROM DUAL UNION ALL SELECT PARENT_TERMINAL_ID TERM_ID"+ 
          " FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID=PRIOR PARENT_TERMINAL_ID"+ 
          " START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID TERM_ID FROM "+
          " FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H')GROUP BY TERMINAL_ID)T,"+
          " FS_FR_TERMINALMASTER TM WHERE T.NO_ROWS>0 AND T.TERMINAL_ID=TM.TERMINALID ORDER BY"+
          " OPER_ADMIN_FLAG)T1 WHERE ROWNUM=1)))) AND QUOTE_TYPE=?";
        try
        {
          connection  =   getConnection();
          pst         =   connection.prepareStatement(emailTextQuery);
          pst.setString(1,terminalId);
          pst.setString(2,terminalId);
          pst.setString(3,quoteType);
          rs        =   pst.executeQuery();
          if(rs.next())
             emailText  = rs.getString("TEXT");
        }
        catch(Exception e)
        {
           e.printStackTrace();
           logger.error("Exception in getting EmailText "+e);
           throw new EJBException(e);
       }
        finally
        {
           ConnectionUtil.closeConnection(connection,pst,rs);   
        }
             return emailText;
    }
    //@@WPBN issue-61295
    //@@Added by Kameswari for the WPBN issue-61289
//@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
public String getLocation(String createdBy) throws EJBException //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
{
    Connection            connection                = null;
    PreparedStatement     pstmt                      = null;
    String                location                  = null;
    ResultSet             rset                         =null;
 String                sql                             ="SELECT LOCATIONID FROM FS_USERMASTER WHERE USERID=?";
                                                      
                                       
      try{
              
             connection  = getConnection();
             pstmt        = connection.prepareStatement(sql);
             pstmt.setString(1,createdBy);
             rset = pstmt.executeQuery();
             while(rset.next())
             {
                location =rset.getString(1);
             }
        }
        catch(Exception e)
        {
            logger.info("Problem Occured During getLocation() : "+e.getMessage());
        }
        finally
        {
              ConnectionUtil.closeConnection(connection,pstmt,rset);   
        }
  return location;
}
//@@ Ended by subrahmanyam for the enhancement #146971 on 03/12/2008
  public ArrayList getAttachmentIdList(QuoteFinalDOB finalDOB, String attachmentId)throws EJBException
  {
     Connection                 connection             = null;
     PreparedStatement          pst                    = null;
     ResultSet                  rs                     = null;
     QuoteMasterDOB             masterDOB              = null;
     QuoteHeader                headerDOB              = null;
     QuoteFreightLegSellRates   legRates               = null;
     QuoteFreightRSRCSRDOB      freightDOB             = null;
     ArrayList                  legDetails             = new ArrayList();
     ArrayList                  freightRates           = null;
     ArrayList                  attachmentIdList       = new ArrayList();
     String                     quoteType              = null;
     String                     carrierId              = null;
     String                     attachmentIdListQuery  = "SELECT DISTINCT QAD.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,QMS_ATTACHMENT_DTL QAD"+
                                                          " WHERE  QAM.ATTACHMENT_ID=QAD.ATTACHMENT_ID AND QAM.INVALIDATE='F' AND QAM.TERMINAL_ID IN"+
                                                          " ( SELECT ? TERM_ID FROM DUAL   UNION ALL"+
                                                         " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+ 
                                                          " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
                                                          " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID IN"+
                                                          " ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL  WHERE FROM_LOCATION"+
                                                          " =? OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL  WHERE TO_LOCATION=? OR TO_LOCATION IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY=?"+
                                                          " OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE"+
                                                          " TO_COUNTRY=? OR QAD.TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE CARRIER_ID=? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID=? OR SERVICE_LEVEL_ID IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=?"+
                                                          " OR QUOTE_TYPE IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE SHIPMENT_MODE =? OR SHIPMENT_MODE IS NULL INTERSECT SELECT ATTACHMENT_ID "+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL) AND QAD.ATTACHMENT_ID LIKE ?";


      /*String                     attachmentIdListQuery  = "SELECT DISTINCT QAD.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,QMS_ATTACHMENT_DTL QAD"+
                                                          " WHERE  QAM.ATTACHMENT_ID=QAD.ATTACHMENT_ID AND QAM.TERMINAL_ID IN"+
                                                          " ( SELECT TERMINAL_ID TERM_ID FROM DUAL  WHERE TERMINAL_ID =? UNION ALL"+
                                                         " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+ 
                                                          " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
                                                          " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID IN"+
                                                          " ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL  WHERE FROM_LOCATION"+
                                                          " =? OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL  WHERE TO_LOCATION=? OR TO_LOCATION IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY=?"+
                                                          " OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE"+
                                                          " TO_COUNTRY=? OR QAD.TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE CARRIER_ID=? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID=? OR SERVICE_LEVEL_ID IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=?"+
                                                          " OR QUOTE_TYPE IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE SHIPMENT_MODE =? OR SHIPMENT_MODE IS NULL INTERSECT SELECT ATTACHMENT_ID "+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL) AND QAD.ATTACHMENT_ID LIKE ?";*/
      try
     {
         masterDOB       = finalDOB.getMasterDOB();
         legDetails      = finalDOB.getLegDetails();
         headerDOB       = finalDOB.getHeaderDOB();
         if(finalDOB.getUpdatedReportDOB()!= null)
              quoteType = "U";
         else
              quoteType = "N";    
             int legDtlSize	=	legDetails.size();
                for(int i=0;i<legDtlSize;i++)
                {
                    legRates  = (QuoteFreightLegSellRates)legDetails.get(i);
                    if(legRates!=null)
                       freightRates = (ArrayList)legRates.getRates();
                }
          if(freightRates!=null)
          {
            freightDOB = (QuoteFreightRSRCSRDOB)freightRates.get(0);
            carrierId  =(String)freightDOB.getCarrierId();
              
          
          }
           connection  = getConnection();
           pst         = connection.prepareStatement(attachmentIdListQuery);
          
        
           pst.setString(1,masterDOB.getTerminalId());
           pst.setString(2,masterDOB.getTerminalId());
           pst.setString(3,finalDOB.getDefaultFlag());
           pst.setString(4,masterDOB.getOriginLocation());
           pst.setString(5,masterDOB.getDestLocation());
           pst.setString(6,headerDOB.getOriginCountryId());
           pst.setString(7,headerDOB.getDestinationCountryId());
           pst.setString(8,carrierId);
           pst.setString(9,masterDOB.getServiceLevelId());
           pst.setString(10,quoteType);
           pst.setInt(11,masterDOB.getShipmentMode());
           pst.setString(12,masterDOB.getIndustryId());
           pst.setString(13,attachmentId+"%");
           rs = pst.executeQuery();
           while(rs.next())
           {
              attachmentIdList.add((String)rs.getString("ATTACHMENT_ID"));
           }
     }  
      catch(Exception e)
      {
           
           e.printStackTrace();
           logger.error("Exception in retreiving attachmentIdList "+e);
           throw new EJBException(e);
     }
      finally
     {
           ConnectionUtil.closeConnection(connection,pst,rs);   
      }
      return attachmentIdList;
  }
  public ArrayList getQuoteAttachmentDtls(QuoteFinalDOB finalDOB)throws EJBException
  {
       Connection                 conn                 = null;
       PreparedStatement          pst                  = null;
       CallableStatement       cstmt                  = null;
       ResultSet                  rs                   = null;
       QuoteAttachmentDOB         attachmentDOB        = null;
       QuoteMasterDOB             masterDOB            = null;
       QuoteHeader                headerDOB            = null;
       QuoteFreightLegSellRates   legRates             = null;
       QuoteFreightRSRCSRDOB      freightDOB           = null;
       QuoteCharges                chargesDOB          = null;
       ArrayList                  defaultIdList        = new ArrayList();
       ArrayList                  attachmentIdList     = null;
       ArrayList                  legDetails           = new ArrayList();
       ArrayList                  freightRates         = null;
        ArrayList                  chargesList         = null;
       ArrayList                  attachmentDOBList    = new ArrayList();
        byte                      fileBytes[]          = null; 
       String                     filebyte             = null;
       Blob                       fileBlob             = null;
       String                     quoteType            = null;
       String                     carrierId            = null;
       long                       quoteId;
       String                     nondefaultFileQry    = "SELECT PDF_FILENAME,PDF_FILE FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID=? ORDER BY PDF_FILENAME";
       /*String                    defaultFileQry       = "SELECT PDF_FILE ,PDF_FILENAME FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID IN"+ 
                                                          "(SELECT QAM.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,"+
                                                          " QMS_ATTACHMENT_DTL QAD,QMS_ATTACHMENT_FILEDTL QAF WHERE  QAM.ATTACHMENT_ID="+
                                                          " QAD.ATTACHMENT_ID AND QAM.ATTACHMENT_ID=QAF.ATTACHMENT_ID AND QAM.TERMINAL_ID IN ("+
                                                          " SELECT TERMINAL_ID TERM_ID FROM DUAL  WHERE TERMINAL_ID =? UNION ALL"+
                                                          " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+
                                                          " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
                                                          " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID"+
                                                          " IN ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE FROM_LOCATION =?"+
                                                          " OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE TO_LOCATION=? OR QAD.TO_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY=? OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE TO_COUNTRY=? OR TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE CARRIER_ID=? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID=? OR SERVICE_LEVEL_ID IS NULL INTERSECT"+
                                                          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=? OR QUOTE_TYPE IS NULL INTERSECT"+
                                                          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE SHIPMENT_MODE =? OR SHIPMENT_MODE IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL))";*/
                                                          
//@@ modified the following query by addding the invalidate='F' FOR THE PBN ID: 188000 ON 29/OCT/09
		  String                    defaultFileQry       = "SELECT PDF_FILE ,PDF_FILENAME FROM QMS_ATTACHMENT_FILEDTL WHERE ATTACHMENT_ID IN"+ 
                                                          "(SELECT QAM.ATTACHMENT_ID FROM QMS_ATTACHMENT_MASTER  QAM,"+
                                                          " QMS_ATTACHMENT_DTL QAD,QMS_ATTACHMENT_FILEDTL QAF WHERE  QAM.ATTACHMENT_ID="+
                                                          " QAD.ATTACHMENT_ID AND QAM.ATTACHMENT_ID=QAF.ATTACHMENT_ID "+
                                                          " AND QAM.INVALIDATE='F' AND QAM.TERMINAL_ID IN ("+
                                                          " SELECT ? TERM_ID FROM DUAL  UNION ALL"+
                                                          " SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY  CHILD_TERMINAL_ID="+
                                                          " PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID=? UNION ALL SELECT TERMINALID"+
                                                          " FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG='H') AND QAD.DEFAULT_FLAG=? AND QAD.ATTACHMENT_ID"+
                                                          " IN ( SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE FROM_LOCATION =?"+
                                                          " OR FROM_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL"+
                                                          " WHERE TO_LOCATION=? OR QAD.TO_LOCATION IS NULL INTERSECT SELECT ATTACHMENT_ID FROM"+
                                                          " QMS_ATTACHMENT_DTL WHERE FROM_COUNTRY=? OR FROM_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE TO_COUNTRY=? OR TO_COUNTRY IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE CARRIER_ID=? OR CARRIER_ID IS NULL INTERSECT SELECT ATTACHMENT_ID"+
                                                          " FROM QMS_ATTACHMENT_DTL WHERE SERVICE_LEVEL_ID=? OR SERVICE_LEVEL_ID IS NULL INTERSECT"+
                                                          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE QUOTE_TYPE=? OR QUOTE_TYPE IS NULL INTERSECT"+
                                                          " SELECT ATTACHMENT_ID FROM QMS_ATTACHMENT_DTL WHERE SHIPMENT_MODE =? OR SHIPMENT_MODE IS NULL"+
                                                          " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE INDUSTRY_ID =? OR INDUSTRY_ID IS NULL"+
                                                         " INTERSECT SELECT ATTACHMENT_ID  FROM QMS_ATTACHMENT_DTL WHERE CONSOLE_TYPE =? OR CONSOLE_TYPE IS NULL)) ORDER BY PDF_FILENAME";  //@@Modified  by kameswari on 19/02/09
                                                          
    
      String                  uncompressedFilequery  = "{?=call UTL_COMPRESS.LZ_UNCOMPRESS(?)}";
       try
       {
       
         masterDOB  = finalDOB.getMasterDOB();
         headerDOB  = finalDOB.getHeaderDOB();
         legDetails  = finalDOB.getLegDetails();
      
         attachmentIdList = (ArrayList)finalDOB.getAttachmentDOBList();
          finalDOB.setDefaultFlag("Y");
         if(finalDOB.getUpdatedReportDOB()!= null)
              quoteType = "U";
         else
              quoteType = "N";    
             
         if(legDetails!=null)
         {
        	 int legDtlSize	=	legDetails.size();
          for(int j=0;j<legDtlSize;j++)
          {
              legRates  = (QuoteFreightLegSellRates)legDetails.get(j);
                 if(legRates.getShipmentMode()==2)
             {
                    if("List".equalsIgnoreCase(legRates.getSpotRatesType()))
                    {
                      legRates.setConsoleType("FCL");
                    }
                    else
                    {
                      legRates.setConsoleType("LCL");
                    }
            }

              if(legRates!=null)
              {
                 freightRates = legRates.getRates();
                 chargesList  =  legRates.getFreightChargesList();

                   if(chargesList!=null)
                   {
                        chargesDOB  =  (QuoteCharges)chargesList.get(0);
                   }
              }
              if(freightRates!=null)
              {
                freightDOB = (QuoteFreightRSRCSRDOB)freightRates.get(0);


                carrierId  = (String)freightDOB.getCarrierId();
              }


          conn     = getConnection();
          pst      = conn.prepareStatement(nondefaultFileQry);
         if(attachmentIdList!=null)
        {
        	 int attachMentListSize	=	attachmentIdList.size();
          for(int i=0;i<attachMentListSize;i++)
         {
            attachmentDOB   = (QuoteAttachmentDOB)attachmentIdList.get(i);
            pst.setString(1,attachmentDOB.getAttachmentId());
            rs = pst.executeQuery();
            while(rs.next())
            {

              attachmentDOB   = new QuoteAttachmentDOB();
              fileBlob        =  rs.getBlob("PDF_FILE");
              //fileBytes       =  (byte[])fileBlob.getBytes(1,(int)fileBlob.length());
               cstmt         = conn.prepareCall(uncompressedFilequery);
                cstmt.registerOutParameter(1,OracleTypes.BLOB);
                cstmt.setBlob(2,fileBlob);
                cstmt.execute();
               fileBlob  =  cstmt.getBlob(1);
               fileBytes  =  fileBlob.getBytes(1,(int)fileBlob.length());
              attachmentDOB.setPdfFile(fileBytes);
              attachmentDOB.setFileName(rs.getString("PDF_FILENAME"));
              attachmentDOB.setTerminalId(masterDOB.getTerminalId());
              attachmentDOB.setUserId(masterDOB.getUserId());
              attachmentDOBList.add(attachmentDOB);
            }
         }
        }
           if(rs!=null)
            rs.close();
           if(pst!=null)
            pst.close();
           if(cstmt!=null)
            cstmt.close();
           pst         = conn.prepareStatement(defaultFileQry);

           pst.setString(1,masterDOB.getTerminalId());
           pst.setString(2,masterDOB.getTerminalId());
           pst.setString(3,finalDOB.getDefaultFlag());
         //  pst.setString(4,masterDOB.getOriginLocation());
           pst.setString(4,legRates.getOrigin());
          // pst.setString(5,masterDOB.getDestLocation());
           pst.setString(5,legRates.getDestination());
           pst.setString(6,headerDOB.getOriginCountryId());

           pst.setString(7,headerDOB.getDestinationCountryId());
           pst.setString(8,carrierId);
           //pst.setString(9,masterDOB.getServiceLevelId());
            pst.setString(9,legRates.getServiceLevel());
           pst.setString(10,quoteType);
          // pst.setInt(11,masterDOB.getShipmentMode());
           pst.setInt(11,legRates.getShipmentMode());
           pst.setString(12,masterDOB.getIndustryId());
             if("Y".equalsIgnoreCase(finalDOB.getSpotRatesFlag()))
           {
                 pst.setString(13,legRates.getConsoleType());
           }
           else
           {
                pst.setString(13,chargesDOB!=null?chargesDOB.getConsoleType():"");
           }


             rs = pst.executeQuery();
           while(rs.next())
           {
        	   if(rs.getString("PDF_FILENAME")!= null && !"".equalsIgnoreCase(rs.getString("PDF_FILENAME"))){
        	  attachmentDOB   = new QuoteAttachmentDOB();
              fileBlob        =  rs.getBlob("PDF_FILE");
               cstmt         = conn.prepareCall(uncompressedFilequery);
               cstmt.registerOutParameter(1,OracleTypes.BLOB);
               cstmt.setBlob(2,fileBlob);
               cstmt.execute();
               fileBlob  =  cstmt.getBlob(1);
              fileBytes       =  fileBlob.getBytes(1,(int)fileBlob.length());
              attachmentDOB.setPdfFile(fileBytes);
              attachmentDOB.setFileName(rs.getString("PDF_FILENAME"));
              attachmentDOB.setTerminalId(masterDOB.getTerminalId());
              attachmentDOB.setUserId(masterDOB.getUserId());
              attachmentDOBList.add(attachmentDOB);
           }
           }
          }
         }
       }

       catch(Exception e)
      {
           e.printStackTrace();
           logger.error("Exception in retreiving attachmentDOBList "+e);
           throw new EJBException(e);
      }
      finally
      {
           ConnectionUtil.closeConnection(conn,pst,rs);   
      }
      return attachmentDOBList;
  }
  public ArrayList getAttachmentDtls(QuoteFinalDOB finalDOB)throws EJBException
  {
      QuoteAttachmentDOB attachmentDOB          = null;
      QuoteMasterDOB     masterDOB              = null;   
      Connection         connection             = null;
      PreparedStatement  pst                    = null;
      ResultSet          rs                     = null;
      ArrayList          attachmentIdList       = new ArrayList();    
      long               quoteId;
      String             viewAttachmentListQry  = "SELECT ATTACHMENT_ID FROM QMS_QUOTE_ATTACHMENTDTL WHERE QUOTE_ID=?";
      try
      {
          masterDOB    = (QuoteMasterDOB)finalDOB.getMasterDOB();
          quoteId      = masterDOB.getUniqueId();
          
          connection   = getConnection();
          pst          = connection.prepareStatement(viewAttachmentListQry);
          pst.setLong(1,quoteId);
          rs          = pst.executeQuery();
          while(rs.next())
          {
              attachmentDOB  = new QuoteAttachmentDOB();
              attachmentDOB.setAttachmentId(rs.getString("ATTACHMENT_ID"));
              attachmentIdList.add(attachmentDOB);
          }
      }
       catch(Exception e)
      {
           e.printStackTrace();
           logger.error("Exception in retreiving attachmentDOBList "+e);
           throw new EJBException(e);
      }
      finally
      {
           ConnectionUtil.closeConnection(connection,pst,rs);   
      }
      return attachmentIdList;
  }

  //@@WPBN issue-61289
  //@@Added for the WPBN Change Request-71229
  
   public String getCountryId(int addressId)throws EJBException
   {
    
       Connection         connection             = null;
       PreparedStatement  pst                    = null;
       ResultSet          rs                     = null;
       String             countryId              = null;
       String             countryIdQuery         = "SELECT COUNTRYID FROM FS_ADDRESS WHERE ADDRESSID=?";
      try
      {
        
        connection = getConnection();
         pst  = connection.prepareStatement(countryIdQuery);
         pst.setInt(1,addressId);
         rs = pst.executeQuery();
        while(rs.next())
        {
           countryId = (String)rs.getString("COUNTRYID");
        }
       
      }
       catch(Exception e)
      {
           e.printStackTrace();
           logger.error("Exception in retreiving countryid "+e);
           throw new EJBException(e);
      }
      finally
      {
           ConnectionUtil.closeConnection(connection,pst,rs);   
      }
      return countryId;
   }
   //@@WPBN Change Request-71229  
   public ArrayList getUpdatedQuotesList(ArrayList updatedQuotesList)throws EJBException 
   {
    String                 quotesListquery  = "SELECT ACTIVE_FLAG ,QUOTE_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?";
     Connection             connection       = null;
     PreparedStatement      pstmt            = null;    
     ResultSet              rs               = null;
     ArrayList            quotesList        = new ArrayList();
     UpdatedQuotesReportDOB  reportDOB  = null;
   try
   {
     connection     = getConnection();
     int updatedQutListSize	=	updatedQuotesList.size();
      for(int j=0;j<updatedQutListSize;j++)
      {
        reportDOB   = (UpdatedQuotesReportDOB)updatedQuotesList.get(j);
       
         pstmt      = connection.prepareStatement(quotesListquery);
        // pstmt.setLong(1,reportDOB.getQuoteId());  //@@ Commented by subrahmanyam for the enhancement #146971 on 03/12/2008
         pstmt.setString(1,reportDOB.getQuoteId());  //@@ Added by subrahmanyam for the enhancement #146971 on 03/12/2008
         rs = pstmt.executeQuery();
         while(rs.next())
         {
           if("I".equalsIgnoreCase(rs.getString("ACTIVE_FLAG")))
           {
             updatedQuotesList.remove(reportDOB);
           }
        
         }
      }  
     
   }
   catch(Exception e)
   {
     e.printStackTrace();
   }
   finally
   {
      ConnectionUtil.closeConnection(connection,pstmt,rs);   
      
   }
    return updatedQuotesList;
  }
    //added by phani sekhar for wpbn 167678
    public void  updateAttentionToContacts(HashMap attentionDetails) throws EJBException
  {
	Connection                connection              = null;
	PreparedStatement         pstmt                    = null;
	String query="UPDATE QMS_CUST_CONTACTDTL DTL SET DTL.EMAILID=?,DTL.FAX=?,CONTACTNO=? WHERE DTL.CUSTOMERID=? AND DTL.SL_NO=?";
	//HashMap attentionDetails = null;
  String customerIds[] = null;
  String slNos[] = null;
  String emailIds[] = null;
  String faxNos[] = null;
  String contactNos[] = null;
	try
	{
	 connection  = this.getConnection();
	 pstmt  = connection.prepareStatement(query);
	// attentionDetails = masterDOB.getAttentionToDetails();
	 if(attentionDetails.get("customerId")!=null)
    customerIds =(String[]) attentionDetails.get("customerId");
    if(attentionDetails.get("slNo")!=null)
    slNos =(String[]) attentionDetails.get("slNo");
    if(attentionDetails.get("emailId")!=null)
    emailIds =(String[]) attentionDetails.get("emailId");
    if(attentionDetails.get("faxNo")!=null)
    faxNos =(String[]) attentionDetails.get("faxNo");
      if(attentionDetails.get("contactNo")!=null)
    contactNos =(String[]) attentionDetails.get("contactNo");
    if(customerIds!=null && customerIds.length>0)
    {
    	int custIdsLen	=	customerIds.length;
      for(int i=0;i<custIdsLen;i++)
      {
        pstmt.clearParameters();        
        if(emailIds!=null && emailIds[i]!=null)
              pstmt.setString(1,emailIds[i]);
            else
                pstmt.setNull(1,Types.VARCHAR);
       if(faxNos!=null && i<faxNos.length)
       {
          if(faxNos[i]!=null)
              pstmt.setString(2,faxNos[i]);
            else
                pstmt.setNull(2,Types.VARCHAR);
       }
       else pstmt.setNull(2,Types.VARCHAR);
        if(contactNos!=null && i<contactNos.length)
       {
          if(contactNos[i]!=null)
              pstmt.setString(3,contactNos[i]);
            else
                pstmt.setNull(3,Types.VARCHAR);
       }
       else pstmt.setNull(3,Types.VARCHAR);
        pstmt.setString(4,customerIds[i]);
        pstmt.setInt(5,Integer.parseInt(slNos[i]));
       
        pstmt.executeUpdate();
      }
    }
    
	} catch(SQLException sqEx)
		{
      sqEx.printStackTrace();			
      logger.error(FILE_NAME+"QMSQuoteSessionBean[updateAttentionToContacts(headerDOB)] -> "+sqEx.toString());
			throw new EJBException(sqEx.toString());
		}
   catch(Exception e)
		{
      e.printStackTrace();	
      logger.error(FILE_NAME+"QMSQuoteSessionBean[updateAttentionToContacts(headerDOB)] -> "+e.toString());
			throw new EJBException(e.toString());
		}
	finally {	ConnectionUtil.closeConnection(connection,pstmt);}

  }
  //ends 167768
   //@@ Added by subrahmanyam for the wpbn #185127 on 05/10/2009
 public String getServiceLevelDesc(String QuoteId) throws EJBException
 {
          Connection                connection              = null;
          ResultSet                 rs                      = null;
          ResultSet                 rs1                     = null;
          ResultSet                 rs2                     = null;
          ResultSet                 rs3                     = null;
          PreparedStatement         pstmt                   = null;
          PreparedStatement         pstmt1                  = null;
          PreparedStatement         pstmt2                  = null;
          PreparedStatement         pstmt3                  = null;
          String                    idSpotFlag              = " SELECT ID,SPOT_RATES_FLAG  FROM QMS_QUOTE_MASTER "+
                                                              " WHERE QUOTE_ID = ? AND (QUOTE_STATUS IN ('ACC', 'NAC') AND " +
                                                              " ACTIVE_FLAG IN ('A', 'I')  OR (QUOTE_STATUS NOT IN('ACC','NAC') AND ACTIVE_FLAG IN('A') )) "+
                                                              " AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID = ? ) ";
         String                    spotFlagYes              = " SELECT DISTINCT SR.SERVICELEVEL FROM QMS_QUOTE_SPOTRATES SR WHERE SR.QUOTE_ID= ? ";
         String                    bRLnVnId                 = " SELECT DISTINCT BUYRATE_ID, RATE_LANE_NO, VERSION_NO  FROM QMS_QUOTE_RATES "+
                                                              " WHERE QUOTE_ID = ? AND SELL_BUY_FLAG IN ('BR','RSR')";
         String                   spotFlagNo                = " SELECT DISTINCT BD.SERVICE_LEVEL    FROM QMS_BUYRATES_DTL BD "+
                                                              " WHERE BUYRATEID = ? AND LANE_NO = ? AND VERSION_NO = ? "+
                                                              " AND BD.SERVICE_LEVEL NOT IN ('SCH')";
         String                    serviceLevelDesc         = " SELECT SERVICELEVELDESC FROM FS_FR_SERVICELEVELMASTER WHERE SERVICELEVELID = ? ";
         long                      id                       = 0;
         String                    spotRateFlag             = "";
         long                      buyrateid                = 0;
         int                       laneno                   = 0;
         int                       versionno                = 0;
         String                    serviceLevelId           = "";
         String                    serviceLevelDes          = "";
        try
        {
          connection  = this.getConnection();
          pstmt  = connection.prepareStatement(idSpotFlag);
          pstmt.setString(1,QuoteId);
          pstmt.setString(2,QuoteId);
          rs  = pstmt.executeQuery();
          if(rs.next())
          {
            id           = rs.getLong("ID");
            spotRateFlag = rs.getString("SPOT_RATES_FLAG");
          }
          if("Y".equalsIgnoreCase(spotRateFlag))
          {
            pstmt1  = connection.prepareStatement(spotFlagYes);
            pstmt1.setLong(1,id);
            rs1 = pstmt1.executeQuery();
            if(rs1.next())
            {
              serviceLevelId =  rs1.getString("SERVICELEVEL");
            }
         }
         else
         {
           pstmt1  = connection.prepareStatement(bRLnVnId);
           pstmt1.setLong(1,id);
           rs1 = pstmt1.executeQuery();
           if(rs1.next())
           {
             buyrateid = rs1.getLong("BUYRATE_ID");
             laneno    = rs1.getInt("RATE_LANE_NO");
             versionno = rs1.getInt("VERSION_NO");
             pstmt2  = connection.prepareStatement(spotFlagNo);
             pstmt2.setLong(1,buyrateid);
             pstmt2.setInt(2,laneno);
             pstmt2.setInt(3,versionno);
             rs2 = pstmt2.executeQuery();
             if(rs2.next())
             {
               serviceLevelId =  rs2.getString("SERVICE_LEVEL");
             }
           }
         }
         pstmt3  = connection.prepareStatement(serviceLevelDesc);
         pstmt3.setString(1,serviceLevelId);
         rs3 = pstmt3.executeQuery();
         if(rs3.next())
         {
           serviceLevelDes = rs3.getString("SERVICELEVELDESC");
         }
        }
        catch(SQLException sqEx)
        {
              sqEx.printStackTrace();
              logger.error(FILE_NAME+"QMSQuoteSessionBean[getServiceLevelDesc(QuoteId)] ->while viewing the Quote "+sqEx.toString());
              throw new EJBException(sqEx.toString());

        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error(FILE_NAME+"QMSQuoteSessionBean[getServiceLevelDesc(QuoteId)] ->while viewing the Quote  "+e.toString());
            throw new EJBException(e.toString());

        }
        finally
        {
          ConnectionUtil.closeConnection(connection,pstmt,rs);
          ConnectionUtil.closePreparedStatement(pstmt1,rs1);
          ConnectionUtil.closePreparedStatement(pstmt2,rs2);
          ConnectionUtil.closePreparedStatement(pstmt3,rs3);
        }
        return serviceLevelDes;
 }
   //@@ Ended by subrahmanyam for the wpbn #185127 on 05/oct/2009
 //@@ Added by subrahmanyam for the wpbn issue: 195650 on 28:Jan:10
 public boolean getZipCode(String fromZipCode,String toZipCode, String givenZipCode) throws EJBException
 {
	 /**
	  * For Example:
	  * 
	  * fromZipCode 	=	"M6L 1A1";
	  * toZipCode		=	"M6L 3G5";
	  * givenZipCode	=	"M6L 3F9";
	  * zone1Sub		= 	"1A1";
	  * zone2Sub		=	"3G5";
	  * zoneReqSub		=	"3F9";
	  * zone1Sub1		=	1;
	  * zone1Sub2		=	65;(HERE ALL ARE CAPITAL LETTERS ONLY)(ASCII VALUE)
	  * zone1Sub3		=	1;
	  * zone2Sub1		=	3;
	  * zone2Sub2		=	71;
	  * zone2Sub3		=	5;
	  * zoneReqSub1		=	3;
	  * zoneReqSub2		=	70;
	  * zoneReqSub3		=	9;
	  */
		if(givenZipCode.length()>6)
		{
				 String zone1 		= 	fromZipCode;
				 String zone2 		= 	toZipCode;
				 String zoneReq 	= 	givenZipCode;
			     String zone1Sub 	= 	zone1.substring(4,7);
				 String zone2Sub 	= 	zone2.substring(4,7);
			     String zoneReqSub 	= 	zoneReq.substring(4,7);
			     int 	zone1Sub1 	= 	Integer.parseInt(zone1Sub.substring(0,1));
			     int 	zone1Sub2 	= 	zone1Sub.substring(1,2).codePointAt(0);
			     int 	zone1Sub3 	= 	Integer.parseInt(zone1Sub.substring(2,3));
			     int 	zone2Sub1   = 	Integer.parseInt(zone2Sub.substring(0,1));
			     int 	zone2Sub2 	= 	zone2Sub.substring(1,2).codePointAt(0);
			     int 	zone2Sub3 	= 	Integer.parseInt(zone2Sub.substring(2,3));
			     int 	zoneReqSub1 = 	Integer.parseInt(zoneReqSub.substring(0,1));
			     int 	zoneReqSub2 = zoneReqSub.substring(1,2).codePointAt(0);
			     int 	zoneReqSub3 = 	Integer.parseInt(zoneReqSub.substring(2,3));
			     boolean zipCodeFlag = false;
			     
					if(zoneReqSub1>=zone1Sub1 && zoneReqSub1 <=zone2Sub1 )
					{
						if(zoneReqSub2>=zone1Sub2 && zoneReqSub2 <=zone2Sub2 )
						{
							if(zoneReqSub2>=zone1Sub2 && zoneReqSub2 <zone2Sub2 )
							{
								if(zoneReqSub3>=zone1Sub3 && zoneReqSub3 <10 )
								{
									zipCodeFlag	=	true;
								}
								else
								{
									zipCodeFlag	= false;
								}					
							}
							else
							{
								if(zoneReqSub3>=zone1Sub3 && zoneReqSub3 <=zone2Sub3 )
								{
									zipCodeFlag	=	true;
								}
								else
								{
									zipCodeFlag	=	false;
								}
							}
			
						}
						else
						{
							zipCodeFlag	=	false;
						}
					}
					else
					{
						zipCodeFlag	=	false;
					}
				 return zipCodeFlag ;
		}
		else
			return false;
		
 }
 private String getQuoteIdValue(String quoteId)
 {	
     String[]                   quotes=null;
     int                        quoteLen=0;
     String                     quoteSub=null; 
     String                     quoteSub1=null;
     String                     quoteSub2=null; 
	 if(quoteId.indexOf("_")==-1)
	  {
			quoteId=quoteId+"_001";
	  }
					
	 else
	 {
		quotes= quoteId.split("_");
		quoteSub=quotes[quotes.length-2];//first part of the quoteId
					
		if(quotes.length==2 && quoteSub.matches("[a-zA-Z]*"))
		{
               quoteId=quoteId+"_001";
       }
		else if(quotes.length==2 && !quoteSub.matches("[a-zA-Z]*"))
		{
			quoteLen = Integer.parseInt(quotes[quotes.length-1]);//2nd part of the quoteId
			quoteLen++;
			if(quoteLen<10)
			{
				quoteSub = Integer.toString(quoteLen);
				quoteSub1 ="00"+quoteSub;
				quoteId=quotes[quotes.length-2]+"_"+quoteSub1;
								
			}
			else if(quoteLen<100)
			{
								
				quoteSub = Integer.toString(quoteLen);
				quoteSub="0"+quoteSub;
				quoteId=quotes[quotes.length-2]+"_"+quoteSub;
									
			}
			else
			{
				quoteSub = Integer.toString(quoteLen);
				quoteId=quotes[quotes.length-2]+"_"+quoteSub;
									
			}
		}
		else
		{
			
			quoteLen = Integer.parseInt(quotes[quotes.length-1]);//3rd part of the quoteId
			quoteLen++;
			
			if(quoteLen<10)
			{
				quoteSub = Integer.toString(quoteLen);
				quoteSub1 ="00"+quoteSub;
				quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub1;
								
			}
			else if(quoteLen<100)
			{
								
				quoteSub = Integer.toString(quoteLen);
				quoteSub="0"+quoteSub;
				quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
								
			}
			else
			{
				quoteSub = Integer.toString(quoteLen);
				quoteId=quotes[quotes.length-3]+"_"+quotes[quotes.length-2]+"_"+quoteSub;
				
			}
		}
	  
	}
	 return quoteId;
 }
 //@@ Ended by subrahmanyam for the wpbn issue: 195650 on 28:Jan:10
 
 
 
}
   
  
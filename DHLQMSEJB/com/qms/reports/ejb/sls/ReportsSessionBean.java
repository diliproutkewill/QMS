package com.qms.reports.ejb.sls;
import com.foursoft.esupply.common.java.LookUpBean;
import com.foursoft.esupply.common.util.ConnectionUtil;
import com.foursoft.esupply.common.util.ESupplyDateUtility;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import org.apache.log4j.Logger;

import com.foursoft.etrans.common.util.java.OperationsImpl;
import com.qms.reports.java.ReportDetailsDOB;
import com.qms.reports.java.ReportsEnterIdDOB;
import com.qms.reports.dao.QmsReportsDAO;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.ejb.EJBException;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.naming.Context;
import javax.naming.InitialContext;

public class ReportsSessionBean implements SessionBean 
{
 private String FILE_NAME = "RepotsSessionBean.java";
  ArrayList        mainDataList = null;     
  QmsReportsDAO     qmsReports  = null;

  private static Logger logger = null;  
  	
  public ReportsSessionBean()
  {
    logger  = Logger.getLogger(ReportsSessionBean.class);  
  }

  private InitialContext  initialContext	= null; 
	private javax.sql.DataSource	dataSource		  = null;

  
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
  private void getDataSource() throws EJBException
  {
    try
    {
      initialContext = new InitialContext();
      dataSource = (javax.sql.DataSource)initialContext.lookup("java:comp/env/jdbc/DB");
    }
    catch( Exception e )
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
      //logger.error(FILE_NAME+"Exception in getDataSource() method of QMSQuoteSessionBean.java: "+e.toString());
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
      logger.error(FILE_NAME+"Exception in getConnectin() method of QMSQuoteSessionBean.java: "+e.toString());
    }
    return con;
  }
public ArrayList getBuyRatesExpiryReport(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {
    
     try
     {
        qmsReports  =  new QmsReportsDAO();
        logger.info(FILE_NAME+"------->getFormateSerchgetFormateSerch() in Bean :: "+reportsEnterIdDOB.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportsEnterIdDOB.getFormateSerch()))
            mainDataList = qmsReports.getBuyRatesExpiryReportDataForExcl(reportsEnterIdDOB);
        else
            mainDataList = qmsReports.getBuyRatesExpiryReportData(reportsEnterIdDOB);
            
    }catch(Exception e)
    {
    e.printStackTrace();
        logger.error(FILE_NAME+"------->getBuyRatesExpiryReport()"+e.toString());
        throw new EJBException();         
    }
    return mainDataList;
  }
  
  public ArrayList getPendingQuoteReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {
    try
     {
        qmsReports  =  new QmsReportsDAO();   
        
        if("Excel".equalsIgnoreCase(reportsEnterIdDOB.getFormateSerch()))
          mainDataList = qmsReports.getPendingQuoteReportDataForExcl(reportsEnterIdDOB);
        else
          mainDataList = qmsReports.getPendingQuoteReportData(reportsEnterIdDOB);
   
      
     }catch(Exception e)
      {
        logger.error(FILE_NAME+"------->getPendingQuoteReportDetails()"+e.toString());
        throw new EJBException();         
      }
    return mainDataList; 
   
  }
  
  public String updatePendingQuoteReport(ArrayList updateDataList)throws EJBException
  {
     String updated            = null;
     try
     {
        qmsReports  =  new QmsReportsDAO();
        updated = qmsReports.updatePendingQuoteReportData(updateDataList);
        
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->updatePendingQuoteReport()"+e.toString());
        throw new EJBException();         
    }
    return updated;
  }
  
  public ArrayList getUpdatedQuotesReportDetails(String changeDesc,int pageNo,String sortBy,String sortOrder,String terminalId,String repFormat,String userId,String empId)throws EJBException
  {
    ArrayList    list   =   null;
    
    try
    {
      qmsReports  =  new QmsReportsDAO();
      logger.info(FILE_NAME+"getUpdatedQuotesReportDetails()----repFormatrepFormatrepFormatrepFormat in Bean:: "+repFormat);
      if("excel".equalsIgnoreCase(repFormat))
        list        =  qmsReports.getUpdatedQuotesReportDetailsExcl(changeDesc,pageNo,sortBy,sortOrder,terminalId,userId,empId);
      else
        list        =  qmsReports.getUpdatedQuotesReportDetails(changeDesc,pageNo,sortBy,sortOrder,terminalId,userId,empId);
    }
    catch(EJBException ejb)
    {
      logger.error(FILE_NAME+"EJBException in getUpdatedQuotesReportDetails"+ejb);
      ejb.printStackTrace();
      throw new EJBException(ejb);
    }
    catch(Exception e)
    {
      
      logger.error(FILE_NAME+"Error in getUpdatedQuotesReportDetails"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    return list; 
  }
  
  
  public ArrayList  getAproveRRejectQuoteDetail( ReportsEnterIdDOB	 reportenterDob)throws EJBException
  {
     try
     {
        qmsReports  =  new QmsReportsDAO();
        logger.info(FILE_NAME+"------->getFormateSerch in Bean():: "+reportenterDob.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportenterDob.getFormateSerch()))
          mainDataList = qmsReports.getAproveRRejectQuoteDetailDataForExcl(reportenterDob);
        else
          mainDataList = qmsReports.getAproveRRejectQuoteDetailData(reportenterDob);
        
     }catch(Exception e)
    {
        
        logger.error(FILE_NAME+"------->getAproveRRejectQuoteDetail()"+e.toString());
        throw new EJBException();         
    }
    return mainDataList;
  }
  
  
  
  public String updateAprovedQuoteDetail(ArrayList updateDataList,ESupplyGlobalParameters loginbean)throws EJBException
  {   
  
     String updated = null; 
     int updateDataListSize=0;
     ResultSet rs=null;
     PreparedStatement pstmt=null;
     Connection con=null;
     OperationsImpl operationsImpl      = null;
     ReportDetailsDOB  reportDetailsDOB=null;    
     String message   ="Quote is approved";
     String userid="";
     String userqry="";
     String fromuser="";
     String toAddress           =   "";
     String locid="";
     String quoteId             =   null;
     try
     {
     operationsImpl = new OperationsImpl();
     con  = operationsImpl.getConnection();
     userid= loginbean.getUserId();
     locid = loginbean.getTerminalId();
     
     userqry="SELECT EMAILID FROM FS_USERMASTER WHERE USERID=? AND LOCATIONID = ?" ;
     
     pstmt  =  con.prepareStatement(userqry);
     
    pstmt.clearParameters();
    pstmt.setString(1,userid);
    pstmt.setString(2,locid);
    
    rs   = pstmt.executeQuery();
    
    if(rs.next())
    {
     fromuser=rs.getString("EMAILID");
    }
    
     if(updateDataList!=null && updateDataList.size()>0)
     {
       updateDataListSize=updateDataList.size();
       for(int i=0;i<updateDataListSize;i++)
       {
         reportDetailsDOB = (ReportDetailsDOB)updateDataList.get(i);
         if(reportDetailsDOB!=null)
         {      
          toAddress        = reportDetailsDOB.getOperEmailId();
          quoteId          = reportDetailsDOB.getQuoteId();
//@@Commented by subrahmanyam for the WPBN ISSUE:146970 AND 146971 ON 17/12/2008          
          //updateSendMailFlag(Long.parseLong(quoteId),loginbean.getUserId(),con);
//@@Added by subrahmanyam for the WPBN ISSUE: 146970 & 146971 ON 17/12/2008          
          updateSendMailFlag(quoteId,loginbean.getUserId(),con);
         }
         
        // sendMail(fromuser,toAddress,message,"");
       }
     }
     
     /*msReports  =  new QmsReportsDAO();
       updated = qmsReports.updateAproveRRejectQuoteDetailData(updateDataList);*/
      
     }
     catch(Exception e)
      {
        
        logger.error(FILE_NAME+"------->updateAproveRRejectQuoteDetail()"+e.toString());
        throw new EJBException();         
      }
      finally
      {
        ConnectionUtil.closeConnection(con,pstmt,rs);
      }
    return fromuser;
    
  }
//@@ Commented by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008  
 //private String  updateSendMailFlag(long quoteId,String userId,Connection connection) throws EJBException
//@@ Added by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008  
 private String  updateSendMailFlag(String quoteId,String userId,Connection connection) throws EJBException
  {
    PreparedStatement         psmt                    = null;
    String                    inactivateQuery         = "";
    String                    query                   = null;
    
    try
    {      
      //connection        =   operationsImpl.getConnection();
      inactivateQuery   =   "UPDATE QMS_QUOTE_MASTER SET ACTIVE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=?";
      //query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,QUOTE_STATUS=?,ACTIVE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
      query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,IE_FLAG=?,QUOTE_STATUS=?,ACTIVE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";  //@@Modified for the WPBN issue-71660                  
      
      psmt              =   connection.prepareStatement(inactivateQuery);
      
      psmt.setString(1,"I");   
     psmt.setTimestamp(2,new java.sql.Timestamp((new java.util.Date()).getTime()));
      psmt.setString(3,userId);
//@@ Commented by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008        
      //psmt.setLong(4,quoteId);
//@@ Added by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008              
      psmt.setString(4,quoteId);
      psmt.executeUpdate();
      
      if(psmt!=null)
        psmt.close();
        
      psmt              =   connection.prepareStatement(query);
//@@ Commented by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008              
      //if(quoteId > 0)
//@@ Added by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008              
      if(quoteId!=null)
      {
        psmt.clearParameters();
        psmt.setString(1,"S");
         psmt.setString(2,"E");//@@Added for the WPBN issue-71660
        psmt.setString(3,"PEN");
        psmt.setString(4,"A");//@@Active, Inactive Flag
        psmt.setTimestamp(5,new java.sql.Timestamp((new java.util.Date()).getTime()));
        psmt.setString(6,userId);
//@@ Commented by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008                
        //psmt.setLong(7,quoteId);
        //psmt.setLong(8,quoteId);
//@@ Added by subrahmanyam for the WPBN ISSUE: 146970 & 146971 on 17/12/2008                        
        psmt.setString(7,quoteId);
        psmt.setString(8,quoteId);
        psmt.executeUpdate();
      }
    }
    catch(Exception e)
    {
      logger.error(FILE_NAME+"Error in getShipperConsigneeZones"+e);
      e.printStackTrace();
      throw new EJBException(e.toString());
    }
    finally
    {
      ConnectionUtil.closePreparedStatement(psmt);
      //ConnectionUtil.closeConnection(connection);
    }
    return "Updated" ;
  }
  public String updateSendMailFlag (ArrayList sentList, String userId) throws EJBException
  {
    Connection          conn      =   null;
    PreparedStatement   psmt      =   null;
    String              query     =   "";
    String              quoteId   =   "";
    QuoteFinalDOB       finalDOB   = null;
    QuoteMasterDOB      masterDOB = null;
    try
    {
      conn    =     getConnection();
       // query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,QUOTE_STATUS=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";
     //@@Commented and Modified by Kameswari for the WPBN issue-116548
//   query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,QUOTE_STATUS=?,IE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";//@@Modified for the WPBN issue-71660
     //@@Commented and Modified by Kameswari for the WPBN issue-116548
       query             =   "UPDATE QMS_QUOTE_MASTER SET SENT_FLAG=?,IE_FLAG=?,MODIFIED_DATE=?,MODIFIED_BY=? WHERE QUOTE_ID=? AND VERSION_NO = (SELECT MAX(VERSION_NO) FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=?)";//@@Modified for the WPBN issue-116548
       psmt              =   conn.prepareStatement(query);
      int sentListSize	=	sentList.size();
      for(int i=0;i<sentListSize;i++)
      {
        quoteId   =   (String)sentList.get(i);
        //finalDOB   =   (QuoteFinalDOB)sentList.get(i);
      //  quoteId     =   finalDOB.getMasterDOB().getQuoteId();
          psmt.clearParameters();
        psmt.setString(1,"S");
       // psmt.setString(2,"PEN");//@@Commented by Kameswari for the WPBN issue-116548
        // psmt.setString(2,(String)finalDOB.getFlagsDOB().getQuoteStatusFlag());
        psmt.setString(2,"E");//@@Added for the WPBN issue-71660
        psmt.setTimestamp(3,new java.sql.Timestamp((new java.util.Date()).getTime()));
        psmt.setString(4,userId);
      // psmt.setLong(6, finalDOB.getMasterDOB().getQuoteId());
//psmt.setLong(7, finalDOB.getMasterDOB().getQuoteId());
     //   psmt.setLong(5,Long.parseLong(quoteId));
      //  psmt.setLong(6,Long.parseLong(quoteId));
       psmt.setString(5,quoteId);
       psmt.setString(6,quoteId);
        psmt.addBatch();
      }
      psmt.executeBatch();
      logger.info(FILE_NAME+"Quote Statuses Updated");
      
    }
    catch (Exception e)
    {
      logger.error(FILE_NAME+"Error in updateSendMailFlag "+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    finally
    {
      ConnectionUtil.closeConnection(conn,psmt);
    }
    return "Updated";
  }
  public ArrayList getEscalatedQuoteReportDetails( ReportsEnterIdDOB	 reportenterDob)throws EJBException
  {
     try
     {
        qmsReports  =  new QmsReportsDAO();
        logger.info(FILE_NAME+"------->getFormateSerchgetFormateSerch() in Bean:: "+reportenterDob.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportenterDob.getFormateSerch()))
            mainDataList = qmsReports.getAproveRRejectQuoteDetailDataForExcl(reportenterDob);
        else
            mainDataList = qmsReports.getAproveRRejectQuoteDetailData(reportenterDob);
        
     }catch(Exception e)
      {
          logger.error(FILE_NAME+"------->getEscalatedQuoteReportDetails()"+e.toString());
          throw new EJBException();         
      }    
    return mainDataList;    
  }
  
  public String updateEscalatedQuoteReportDetails(ArrayList updateDataList)throws EJBException
  {   
     String updated = null; 
     try
     {        
        qmsReports  =  new QmsReportsDAO();
        updated = qmsReports.updateEscalatedQuoteReportDetailsData(updateDataList);
      
     }catch(Exception e)
      {
        logger.error(FILE_NAME+"------->updateEscalatedQuoteReportDetailsData()"+e.toString());
        throw new EJBException();         
      }
    return updated;
  }
  
  public ArrayList getQuoteExpiryReport(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {   
     try
     {
        qmsReports  =  new QmsReportsDAO();
        System.out.println("getFormateSerchgetFormateSerchgetFormateSerch in Bean:: "+reportsEnterIdDOB.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportsEnterIdDOB.getFormateSerch()))
          mainDataList = qmsReports.getQuoteExpiryReportDataExcl(reportsEnterIdDOB);
        else
          mainDataList = qmsReports.getQuoteExpiryReportData(reportsEnterIdDOB);

      
     }catch(Exception e)
      {
        logger.error(FILE_NAME+"------->getQuoteExpiryReport()"+e.toString());
        throw new EJBException();         
      }
    return mainDataList;
  }
  
  public String updateQuoteExpiryReport(ArrayList updateDataList)throws EJBException
  {   
     String updated = null; 
     try
     {        
        qmsReports  =  new QmsReportsDAO();
        updated = qmsReports.updateQuoteExpiryReportData(updateDataList);
      
     }catch(Exception e)
      {
        logger.error(FILE_NAME+"------->getQuoteExpiryReport()"+e.toString());
        throw new EJBException();         
      }
    return updated;
  }
  
  public ArrayList getActivityReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {
     try
     {
        
        qmsReports  =  new QmsReportsDAO();  
        logger.info(FILE_NAME+"------->getFormateSerchgetFormateSerchgetFormateSerch() in Bean :: "+reportsEnterIdDOB.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportsEnterIdDOB.getFormateSerch()))
            mainDataList = qmsReports.getActivityReportDetailsDataForExcl(reportsEnterIdDOB);
        else
            mainDataList = qmsReports.getActivityReportDetailsData(reportsEnterIdDOB);
       
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getActivityReportDetails()"+e.toString());
        throw new EJBException();         
    }
   
    return mainDataList;
  }
  
  //Included by Shyam for DHL for Issue No: 14048 
   public HashMap updateActivityFlag(HashMap checkMap)throws EJBException //modified by phani sekhar for wpbn 181670 on 20090909
  {
    HashMap temp=null; //added by phani sekhar for wpbn 181670 on 20090909
     try
     {
        qmsReports  =  new QmsReportsDAO();  
       temp= qmsReports.updateActivityFlag(checkMap); //modified by phani sekhar for wpbn 181670 on 20090909
        
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->updateActivityFlag()"+e.toString());
        throw new EJBException();         
    }
   return temp;
  }
  //Included by Shyam for DHL
  
  public ArrayList getYieldReportDetails(ReportsEnterIdDOB reportsEnterIdDOB)throws EJBException
  {
 
     try
     {
        qmsReports  =  new QmsReportsDAO();
        logger.info(FILE_NAME+"------->getFormateSerchgetFormateSerchgetFormateSerch() in Bean :: "+reportsEnterIdDOB.getFormateSerch());
        if("Excel".equalsIgnoreCase(reportsEnterIdDOB.getFormateSerch()))
            mainDataList = qmsReports.getYieldReportDetailsDataForExcl(reportsEnterIdDOB);  
        else
            mainDataList = qmsReports.getYieldReportDetailsData(reportsEnterIdDOB);  
      
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getYieldReportDetails()"+e.toString());
        throw new EJBException();         
    }
    return mainDataList;
  }
  
    public ArrayList getYieldReportDetailsLegs(String quoteId)throws EJBException
  {
     ArrayList mainDetailsList = null;
     QmsReportsDAO qmsReports  = null;
     try
     {
       // System.out.println("in session");
        qmsReports  =  new QmsReportsDAO();
        mainDetailsList = qmsReports.getYieldReportDetailsLegs(quoteId);  
        
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getYieldReportDetailsLegs()"+e.toString());
        throw new EJBException();         
    }finally
    {qmsReports = null;
    }
    return mainDetailsList;
  }
  
  public HashMap getActivitySummaryReportDetails(ESupplyGlobalParameters loginbean)throws EJBException
  {
     HashMap  summaryDetails = new HashMap();
     try
     {
        
        qmsReports     =  new QmsReportsDAO();  
        summaryDetails =  qmsReports.getActivitySummaryReportDetailsData(loginbean);
       
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getActivityReportDetails()"+e.toString());
        throw new EJBException();         
    }
   
    return summaryDetails;
  }
  
  public ArrayList getUpdatedQuotes(String terminalId,String userId,String empId) throws EJBException
  {
    ArrayList    list   =   null;
    try
    {
      qmsReports  =  new QmsReportsDAO();
      list        =  qmsReports.getUpdatedQuotes(terminalId,userId,empId);
    }
    catch(Exception e)
    {
      logger.error(FILE_NAME+"Error in getYieldReportDetails"+e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    return list;
  }
 
  public void sendMail(String frmAddress, String toAddress, String message, String attachments) 
		{
//			System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
		   	try 
			{   //attachment = "c:/"+attachment;
          
          Context initial = new InitialContext();
          Session session = (Session) initial.lookup("java:comp/env/mail/MS");
          InternetAddress fromAddress = new InternetAddress(frmAddress);
          
          Message msg = new MimeMessage(session); 
          msg.setFrom(fromAddress); 
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress) ); 
          msg.setSubject("Approved Information"); 
          msg.setSentDate(new java.util.Date()); 
          
          // create and fill the first message part 
          MimeBodyPart mbp1 = new MimeBodyPart(); 
          mbp1.setText(message); 
          Multipart mp = new MimeMultipart();
          mp.addBodyPart(mbp1);
          
          
          
          String attachs[] = attachments.split(",");          
          
          if(attachs!=null && attachs.length>0)
          {
           ///---------
           Multipart multipart = new MimeMultipart();
//<in  a loop for multiple files>
      /* BodyPart messageBodyPart = new MimeBodyPart();
       String fileName = <the file to be attached (full path)>
       DataSource source =  new FileDataSource(fileName);
       messageBodyPart.setDataHandler(new DataHandler(source));
       
messageBodyPart.setFileName(fileName.substring(fileName.lastIndexOf(separator)+1));
       multipart.addBodyPart(messageBodyPart);
<file attachement loop ends>*/

          // ----------
           int attachsLen	=	attachs.length;
           for(int i=0;i<attachsLen;i++)
           {
             BodyPart mbp2 = new MimeBodyPart();
             DataSource fds = new FileDataSource(attachs[i]);          
             mbp2.setDataHandler(new DataHandler(fds));
             mbp2.setFileName(fds.getName()); 
             mp.addBodyPart(mbp2);
           }
          }
	      msg.setContent(mp);
	   		Transport.send(msg); 
	   }
	   catch(MessagingException me)
	   {
//		System.out.println("Message Exception in send Mails ... "+me.toString());
		throw new EJBException(me.toString());
	   } 
	   catch(Exception e)
		 {
//		   System.out.println("Exception in sendMails CustomizedReportBean..."+e.toString());	
	       throw new EJBException(e.getMessage());
	   }
	}
  public void sendMail(String frmAddress, String toAddress, String message, String attachments,File[] fArr) 
		{
//			System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
		   	try 
			{   //attachment = "c:/"+attachment;
          
          Context initial = new InitialContext();
          Session session = (Session) initial.lookup("java:comp/env/mail/MS");
          InternetAddress fromAddress = new InternetAddress(frmAddress);
          
          Message msg = new MimeMessage(session); 
          msg.setFrom(fromAddress); 
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress) ); 
          msg.setSubject("Approved Information"); 
          msg.setSentDate(new java.util.Date()); 
          
          // create and fill the first message part 
          MimeBodyPart mbp1 = new MimeBodyPart(); 
          mbp1.setText(message); 
          Multipart mp = new MimeMultipart();
          mp.addBodyPart(mbp1);
          
          
          String attachs[] = attachments.split(",");          
          
          if(attachs!=null && attachs.length>0)
          {
           ///---------
           Multipart multipart = new MimeMultipart();
//<in  a loop for multiple files>
      /* BodyPart messageBodyPart = new MimeBodyPart();
       String fileName = <the file to be attached (full path)>
       DataSource source =  new FileDataSource(fileName);
       messageBodyPart.setDataHandler(new DataHandler(source));
       
messageBodyPart.setFileName(fileName.substring(fileName.lastIndexOf(separator)+1));
       multipart.addBodyPart(messageBodyPart);
<file attachement loop ends>*/

          // ----------
           int attachsLen	=	attachs.length;
           for(int i=0;i<attachsLen;i++)
           {
             BodyPart mbp2 = new MimeBodyPart();
             DataSource fds = new FileDataSource(fArr[i]);          
             mbp2.setDataHandler(new DataHandler(fds));
             mbp2.setFileName(fds.getName()); 
             mp.addBodyPart(mbp2);
           }
          }
	      msg.setContent(mp);
	   		Transport.send(msg); 
	   }
	   catch(MessagingException me)
	   {
//		System.out.println("Message Exception in send Mails ... "+me.toString());
		throw new EJBException(me.toString());
	   } 
	   catch(Exception e)
		 {
//		   System.out.println("Exception in sendMails CustomizedReportBean..."+e.toString());	
	       throw new EJBException(e.getMessage());
	   }
	}
  
  public String[] getAppRejMailDetails(String quoteId, String userId) throws EJBException, RemoteException
  {
    Connection              con                       = null;
    PreparedStatement       psmt                      = null;
    ResultSet               rs                        = null;
    
    OperationsImpl          operationsImpl            = null;
    
    String                  fromAddr                  = null;
    String                  toAddr                    = null;
    
    String                  fromAddrQry               = null;
    String                  toAddrSalesQry            = null;
    String                  toAddrCreatedByQry        = null;
    String                  customerNameQry           = null;
    
    String[]                fromAndToAddr             = null;
    
    try
    {
      fromAddrQry         = "SELECT EMAILID FROM FS_USERMASTER WHERE USERID=?";
      toAddrSalesQry      = "SELECT EMAILID FROM FS_USERMASTER WHERE EMPID=(SELECT SALES_PERSON FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A')";
      toAddrCreatedByQry  = "SELECT UM.EMAILID FROM FS_USERMASTER UM, QMS_QUOTE_MASTER QM WHERE UM.USERID=QM.CREATED_BY AND QM.ACTIVE_FLAG='A' AND UM.LOCATIONID=QM.TERMINAL_ID AND  QM.QUOTE_ID=?";
      customerNameQry     = "SELECT COMPANYNAME FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=(SELECT CUSTOMER_ID FROM QMS_QUOTE_MASTER WHERE QUOTE_ID=? AND ACTIVE_FLAG='A')";
      
      operationsImpl = new OperationsImpl();
      con  = operationsImpl.getConnection();
      
      fromAndToAddr = new String[4];
      
      psmt  = con.prepareStatement(fromAddrQry);
      psmt.setString(1,userId);
      
      rs  = psmt.executeQuery();
      while(rs.next())
      {
        fromAndToAddr[0] = rs.getString("EMAILID");
      }
      
      if(rs!=null)  
        rs.close();
      if(psmt!=null)  
        psmt.close();
        
      psmt  = con.prepareStatement(toAddrSalesQry);
      psmt.setString(1,quoteId);
      
      rs  = psmt.executeQuery();
      while(rs.next())
      {
        fromAndToAddr[1]  = rs.getString("EMAILID");
      }
      
      if(rs!=null)  
        rs.close();
      if(psmt!=null)  
        psmt.close();
        
      psmt  = con.prepareStatement(toAddrCreatedByQry);
      psmt.setString(1,quoteId);
      
      rs  = psmt.executeQuery();
      while(rs.next())
      {
        fromAndToAddr[2]  = rs.getString("EMAILID");
      }
      
      if(rs!=null)  
        rs.close();
      if(psmt!=null)  
        psmt.close();
        
      psmt  = con.prepareStatement(customerNameQry);
      psmt.setString(1,quoteId);
      
      rs  = psmt.executeQuery();
      while(rs.next())
      {
        fromAndToAddr[3]  = rs.getString("COMPANYNAME");
      }
    }
    catch(SQLException sqle)
    {
      sqle.printStackTrace();
      logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+sqle.toString());
    }
    catch(EJBException ee)
    {
      ee.printStackTrace();
      logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+ee.toString());
    }
    catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
    }
    finally
    {
      try
      {
        ConnectionUtil.closeConnection(con, psmt, rs);
      }
      catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
      }
    }
    return fromAndToAddr;
  }
  //@@Added by Kameswari for the WPBN issue-167655 on 23/04/09
  public HashMap getGroupingExcelDetails(HashMap<String,String> paramsMap, ESupplyGlobalParameters loginbean)  throws EJBException

  {
     HashMap ratesList       = null;
      try
    {
             qmsReports  =  new QmsReportsDAO();   
             ratesList   = qmsReports.getGroupingExcelDetails(paramsMap,loginbean);
     }
     catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
    }
    
     return ratesList;
  }
//@@WPBN issue-167655 on 23/04/09
public String validateRateDetails(String customerId,String originCountry,String originCity,String destCountry,String destCity,ESupplyGlobalParameters loginbean)  throws EJBException
{
   ArrayList          ratesList       =   null;
   String             terminalQry     = "";
   String             locationQuery   =  "SELECT COUNT(LOCATIONID)  FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID= ?";
   String             countryQuery    =  "SELECT COUNT(COUNTRYID)  FROM FS_COUNTRYMASTER WHERE COUNTRYID= ?";
   String            customerQuery    ="";
    Connection         conn            = null;
   PreparedStatement   pstmt          = null;
   ResultSet           rs             = null;
   PreparedStatement   pstmt1         = null;
   ResultSet           rs1            = null;
   PreparedStatement   pstmt2         = null;
   ResultSet           rs2            = null;
   String              errorMsg       = "";
     try
    {
       
        conn              = getConnection();
        if(originCity!=null&&originCity.trim().length()>0)
        {
         pstmt            =  conn.prepareStatement(locationQuery);
         pstmt.setString(1,originCity);
         rs               = pstmt.executeQuery();
         while(rs.next())
          {
            if(rs.getInt(1) == 0)
            {
                errorMsg ="Origin Location is Invalid"; 
            }
         }
        }
         if(rs!=null)
            rs.close();
        if(pstmt!=null)
          pstmt.close();
        if(destCity!=null&&destCity.trim().length()>0)
        {  
           pstmt            =  conn.prepareStatement(locationQuery);
            pstmt.setString(1,destCity);
         rs  = pstmt.executeQuery();
         while(rs.next())
          {
             if(rs.getInt(1) == 0)
            {
               if(errorMsg!=null&&errorMsg.trim().length()>0)
               {
                 errorMsg = errorMsg+","+" Destination Location is Invalid"; 
               }
               else
               {
                 errorMsg = "Destination Location is Invalid"; 
               }
            }
         }
        }
          if(originCountry!=null&&originCountry.trim().length()>0)
        {  
        pstmt1            =  conn.prepareStatement(countryQuery);
        pstmt1.setString(1,originCountry);
         rs1  = pstmt1.executeQuery();
         while(rs1.next())
          {
            if(rs1.getInt(1) == 0)
            {
                 if(errorMsg!=null&&errorMsg.trim().length()>0)
               {
                 errorMsg = errorMsg+","+"  Origin  Country is Invalid"; 
               }
               else
               {
                 errorMsg = " Origin  Country is Invalid";  
               }
            }
         }
        }
           if(rs1!=null)
            rs1.close();
          if(pstmt1!=null)
           pstmt1.close();
         if(destCountry!=null&&destCountry.trim().length()>0)
        { 
         pstmt1            =  conn.prepareStatement(countryQuery);
         pstmt1.setString(1,destCountry);
         rs1  = pstmt1.executeQuery();
         while(rs1.next())
          {
             if(rs1.getInt(1) == 0)
            {
               if(errorMsg!=null&&errorMsg.trim().length()>0)
               {
                 errorMsg = errorMsg+","+"  Destination  Country is Invalid"; 
               }
               else
               {
                 errorMsg = " Destination  Country is Invalid";  
               }
            }
         }
        }
        if("OPER_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
     {
         terminalQry  = "AND TERMINALID IN (SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID = '"+loginbean.getTerminalId()+"' UNION ALL SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID IN (SELECT PARENT_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE CHILD_TERMINAL_ID= '"+loginbean.getTerminalId()+"))";
      }
      else if("ADMIN_TERMINAL".equalsIgnoreCase(loginbean.getAccessType()))
      {
        terminalQry  = "AND TERMINALID IN ( SELECT CHILD_TERMINAL_ID FROM FS_FR_TERMINAL_REGN WHERE PARENT_TERMINAL_ID = '"+loginbean.getTerminalId()+"' UNION ALL SELECT  '"+loginbean.getTerminalId()+"' FROM DUAL)";
      }
     customerQuery   =  "SELECT COUNT(CUSTOMERID)  FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERID=? "+terminalQry;
  
         if(customerId!=null&&customerId.trim().length()>0)
        { 
         pstmt2            =  conn.prepareStatement(customerQuery);
         pstmt2.setString(1,customerId);
         rs2  = pstmt2.executeQuery();
         while(rs2.next())
          {
             if(rs2.getInt(1) == 0)
            {
               if(errorMsg!=null&&errorMsg.trim().length()>0)
               {
                 errorMsg = errorMsg+","+"  CustomerId is Invalid"; 
               }
               else
               {
                 errorMsg = " CustomerId is Invalid";  
               }
            }
         }
        }
   }
     catch(Exception e)
    {
      e.printStackTrace();
      logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
    }
    finally
    {
      try
      {
        if(rs!=null)
        {
           rs.close();
        }
         if(rs1!=null)
        {
           rs1.close();
        }
         if(rs2!=null)
        {
           rs2.close();
        }
         if(pstmt1!=null)
        {
           pstmt1.close();
        }
         if(pstmt2!=null)
        {
           pstmt2.close();
        }
         if(pstmt!=null)
        {
           pstmt.close();
        }
         if(conn!=null)
        {
           conn.close();
        }
      }
        catch(Exception e)
      {
        e.printStackTrace();
        logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
      }
    }
     return errorMsg; 
}
  //added by phani sekhar for 167656
  public StringBuffer validateFreightReportData(com.qms.reports.java.QMSRatesReportDOB ratesReportDOB)
  {
    QmsReportsDAO qmsReports  = null;
    StringBuffer errors = null;
    try
     {
     qmsReports  =  new QmsReportsDAO();
     errors=qmsReports.validateFreightReportData(ratesReportDOB) ;
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getYieldReportDetailsLegs()"+e.toString());
        throw new EJBException();         
    }finally
    {
    qmsReports = null;
    }
    return errors;
  }
  public ArrayList getFreightRatesExcelDetails(com.qms.reports.java.QMSRatesReportDOB ratesReportDOB)
  {
    ArrayList dataList = null;
    QmsReportsDAO qmsReports  = null;
     try
     {
     
        qmsReports  =  new QmsReportsDAO();
        dataList = qmsReports.getFreightRatesExcelDetails(ratesReportDOB);
        
     }catch(Exception e)
    {
        logger.error(FILE_NAME+"------->getYieldReportDetailsLegs()"+e.toString());
        throw new EJBException();         
    }finally
    {
    qmsReports = null;
    }
    return dataList;
  }//ends 167656

  // Added by Gowtham to get Place Description
  public String getPlaceName(String placeId)
  {
	  	Connection         	conn            = null;
	  	PreparedStatement   pstmt         	= null;
	  	ResultSet           rs             = null;
	  	String				locationName	="";
	  	String             locationQuery   =  "SELECT LOCATIONNAME  FROM FS_FR_LOCATIONMASTER WHERE LOCATIONID= ?";
	  	try{
	  		conn = this.getConnection();
	  		pstmt = conn.prepareStatement(locationQuery);
	  		pstmt.setString(1, placeId);
	  		rs = pstmt.executeQuery();
	  		if(rs.next())
	  		{
	  			locationName = rs.getString("LOCATIONNAME");
	  			
	  		}
	  		else
	  			locationName = "";
	  			
	  		
	  	}catch(Exception e)
	    {
	        e.printStackTrace();
	        logger.error(FILE_NAME+"Error in ReportsSessionBean ::"+e.toString());
	    }
	  	finally
	  	{
	  		ConnectionUtil.closeConnection(conn,pstmt,rs);
	  	}
	  	return locationName;
  }
  
  // End of Gowtham to get Place Description


}
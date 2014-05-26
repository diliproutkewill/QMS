package com.qms.reports.ejb.sls;
import com.foursoft.esupply.common.util.Logger;
import com.qms.reports.java.ReportDetailsDOB;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
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

public class RepotsSessionBean implements SessionBean 
{ private String FILE_NAME = "RepotsSessionBean.java";
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
   public ArrayList  getAproveRRejectQuoteDetail()throws EJBException
  {
//     Connection connection     = null;commented by govind on 15-02-2010 for connection leakages
     CallableStatement cStmt   = null;
 //    ResultSet         rs      = null;commented by govind on 15-02-2010 for connection leakages
     
     ReportDetailsDOB reportDetailsDOB = null;
     ArrayList        dataList         = new ArrayList();  
     try
     {
         /*connection  = operationsImpl.getConnection();
        cStmt       = connection.prepareCall("");
        * calling procedure
        */
        
        /*while(rs.next())
        {*/
          for(int i=0;i<4;i++)
          {
            reportDetailsDOB = new ReportDetailsDOB();
            //reportDetailsDOB.setImportant("");
            reportDetailsDOB.setQuoteId("QUOTE11");
            reportDetailsDOB.setCustomerId("IBM GLOBAL");
            reportDetailsDOB.setShipmentMode("Air");
            reportDetailsDOB.setServiceLevel("EC");
            reportDetailsDOB.setFromCountry("NZ");
            reportDetailsDOB.setFromLocation("CHC");
            reportDetailsDOB.setToCountry("SIN");
            reportDetailsDOB.setToLocation("NYC");
            reportDetailsDOB.setDueDateNTime("01-09-05");
            reportDetailsDOB.setApprovedRrejectedBy("Manager 1");
            reportDetailsDOB.setApprovedRrejectedDtNtime("01-09-05");
            dataList.add(reportDetailsDOB);            
          }
        //}
     }catch(Exception e)
    {
        Logger.error(FILE_NAME,"------->getAproveRRejectQuoteDetail()",e.toString());
        throw new EJBException();         
    }finally
      {
        try
        {
         /* if(rs!=null)
            { rs.close();}*/
          if(cStmt!=null)
            { cStmt.close();}
          /*if(connection!=null)
            { connection.close();}*/
        }catch(SQLException e)
        {
          Logger.error(FILE_NAME,"------->getAproveRRejectQuoteDetail()",e.toString());
          throw new EJBException();
        }
      }
    
    return dataList;
  }
  
  public ArrayList getEscalatedQuoteReportDetails()throws EJBException
  {
    // Connection connection     = null;commented by govind for connection leakage
     CallableStatement cStmt   = null;
//     ResultSet         rs      = null;commented by govind for connection leakage
     
     ReportDetailsDOB reportDetailsDOB = null;
     ArrayList        dataList         = new ArrayList();  
     try
     {
         /*connection  = operationsImpl.getConnection();
        cStmt       = connection.prepareCall("");
        * calling procedure
        */
        
        /*while(rs.next())
        {*/
          for(int i=0;i<4;i++)
          {
            reportDetailsDOB = new ReportDetailsDOB();
            //reportDetailsDOB.setImportant("");
            reportDetailsDOB.setQuoteId("QUOTE11");
            reportDetailsDOB.setCustomerId("IBM GLOBAL");
            reportDetailsDOB.setShipmentMode("Air");
            reportDetailsDOB.setServiceLevel("EC");
            reportDetailsDOB.setFromCountry("NZ");
            reportDetailsDOB.setFromLocation("CHC");
            reportDetailsDOB.setToCountry("SIN");
            reportDetailsDOB.setToLocation("NYC");
            dataList.add(reportDetailsDOB);            
          }
        //}
     }catch(Exception e)
      {
          Logger.error(FILE_NAME,"------->getEscalatedQuoteReportDetails()",e.toString());
          throw new EJBException();         
      }finally
      {
        try
        {
          /*if(rs!=null)
            { rs.close();}*///commented by govind for connection leakage
          if(cStmt!=null)
            { cStmt.close();}
         /* if(connection!=null)
            { connection.close();}*///commented by govind for connection leakage
        }catch(SQLException e)
        {
          Logger.error(FILE_NAME,"------->getEscalatedQuoteReportDetails()",e.toString());
          throw new EJBException();
        }
      }
    
    return dataList;    
  }
  public void sendMail(String frmAddress, String toAddress, String message, String attachment) 
		{
//			System.out.println("sendMail in CustomizedReportBean called.. attchment :: "+attachment);
		   	try 
			{   //attachment = "c:/"+attachment;
           Logger.info(FILE_NAME,"message  "+message);
           Logger.info(FILE_NAME,"frmAddress  "+frmAddress);
           Logger.info(FILE_NAME,"toAddress  "+toAddress);
           Logger.info(FILE_NAME,"attachment  "+attachment);
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
	      	Logger.info(FILE_NAME,"mbp1  "+mbp1);
          Logger.info(FILE_NAME,"mp  "+mp);
	   		MimeBodyPart mbp2 = new MimeBodyPart(); 
	   		FileDataSource fds = new FileDataSource(attachment); 
        Logger.info(FILE_NAME,"fds  "+fds);
	   		mbp2.setDataHandler(new DataHandler(fds)); 
	   		mbp2.setFileName(fds.getName()); 
	 		mp.addBodyPart(mbp2);
	      	msg.setContent(mp);
			System.out.println("message content set, now ready to send the mail"); 
	   		Transport.send(msg); 
			System.out.println("message content has been sent in mail ");
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
}
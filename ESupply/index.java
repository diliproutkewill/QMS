import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.foursoft.esupply.common.java.FoursoftWebConfig;

/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
/**
 * File			: index.java
 * module 		: esupply
 * 
 * This is a Bean utility class which loads images from Database
 * 
 * @author	B.amakumar, 
 * @date	12-06-2002
 * 
 * modified history
 * Modifed By		Modified Date		Reason
 * Amit Parekh		21/10/2002			The global datasource name 'mail/MS' or otherwise is now
 *										maintained globally in FoursoftWebConfig.java
 *                                      Application mail sending message format has been eloborated
 * Amit Parekh		25/04/2003			The Licensee name was changed to "Four Soft Limited". The copy right period was changed to 1999-2003
 * Amit Parekh		06/05/2003			This file was modified to show different User Types according to configuration in Four Soft Config File
 */

public final class index  extends HttpServlet 
{
	private static final String CONTENT_TYPE = "text/html";
	public static int winNo = 0;
	public static final String FILE_NAME = "index.java";
	public static int INSTCNT = 0;

	// ETRANS User Types
	public static final String	ETS = "ETS";
	public static final String	ETC = "ETC";
	
	// SP User Types
	public static final String	ESP = "ESP";
	public static final String	ESC = "ESC";
	public static final String	ESV = "ESV";

	// EP User Types
	public static final String	EEP = "EEP";
	public static final String	EPC = "EPC";
	public static final String	EPV = "EPV";
	public static final String	EPT = "EPT";

	// ELOG User Types
	public static final String	ELG = "ELG";
	public static final String	ELC = "ELC";
	public static final String	ELV = "ELV";

  private static Logger logger = null;
  
	InitialContext ic = null;
	 
    /**
     * Called by the servlet container to indicate to a servlet that the
     * servlet is being placed into service.
     *
     * @param config the ServletConfig object that contains configutation
     *					information for this servlet
     * @exception ServletException if an exception occurs that interrupts
     *					the servlet's normal operation.
     */
    public void init(ServletConfig config) throws ServletException
    {
        logger  = Logger.getLogger(index.class);
        super.init(config);
		sendMail();
		INSTCNT++;
		//System.out.println("IN INIT OF INDEX SERVLET..."+INSTCNT);
    }


    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a GET request.
     *
     * @param request 	an HttpServletRequest object that contains the request
     *              	the client has made of the servlet
     * @param response 	an HttpServletResponse object that contains the
     *              	response the servlet sends to the client
     * @exception IOException if an input or output error is detected when
     *               	the servlet handles the GET request
	 * @exception ServletException if the request for the GET could not be
	 * 					handled
     */
    public void doGet(HttpServletRequest request,
    			HttpServletResponse response)
    			throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Called by the server (via the service method) to allow a servlet to
     * handle a POST request.
     *
     * @param request 	an HttpServletRequest object that contains the
     *              	request the client has made of the servlet
     * @param response 	an HttpServletResponse object that contains the
     *              	responsethe servlet sends to the client
     * @exception IOException if an input or output error is detected
     *               	when the servlet handles the POST request
	 * @exception ServletException if the request for the POST could not
	 *					 be handled.
     */
    public void doPost(HttpServletRequest request,
    				HttpServletResponse response)
    				throws ServletException, IOException
	{
		
		try
		{
			response.setContentType(CONTENT_TYPE);

			response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   			response.setHeader("Pragma","no-cache"); // HTTP 1.0
   			response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
			
			index.winNo++;

			//Logger.warning("index.java", "No of Hits to the Index the page:"+index.winNo);
      logger.warn("index.java"+ "No of Hits to the Index the page:"+index.winNo);
			
			PrintWriter out = response.getWriter();
			RawData imgObj = new RawData();

			StringBuffer sbResponse	=	new StringBuffer("");
			
			String img  = "images/DHLlogo.gif";
			String img1 = " ";
			String	NL = "\n";
			String	BLANK_ROW = "<tr><td colspan='2'>&nbsp;</td></tr>";
			String	portedModule	=	FoursoftWebConfig.MODULE;
			
			sbResponse.append(	 "<html>"+NL
								+"<head>"+NL
								+"<title>welcome</title> "+NL
								+"<script language=\"JavaScript\" src=\"eventhandler.js\"></script> "+NL
								+"<script LANGUAGE='JavaScript'> "+NL
								+"var windowFeatures	=	'toolbar=0,status=1,location=no,menubar=no,directories=no,scrollbars=yes,resizable=yes'; "+NL+NL
								+"function newWindow(userType){ "+NL+NL
								+"	// If winName is not passed just reuse current windows.name; "+NL
								+"	var millisecs	=	(new Date()).getTime(); "+NL
								+"	var	winName		=	'LoginAt'+millisecs; "+NL
								+"	var newWin = window.open('ESupplyLogin?userType='+userType+'&windowName='+winName ,winName, windowFeatures); "+NL					  
								+"	newWin.resizeTo(screen.availWidth,screen.availHeight) "+NL
								+"	newWin.focus(); "+NL
								+"	newWin.moveTo(0,0); "+NL
								+"}"+NL+NL
								+"</script>"+NL
								+"<head>"+NL+NL
								+"<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">"+NL
								+"<div align=\"center\">"+NL
								+"<center>"+NL
								
								+"<table bgcolor=\"#efefef\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" height=\"100%\">"+NL

								+"	<tr>"+NL
								+"		<td bgcolor=\"#efefef\" height=\"40\" colspan=\"2\"> "+NL
								+"			<p align=\"left\"><img alt=\"DHL \" width='201' height='62' src=\""+img+"\">"+NL
								+"		</td> "+NL 
								+"		<td bgcolor=\"#efefef\" valign=\"middle\" align=\"right\"></td>"+NL
								+"	</tr>"+NL
								+"	<tr>"+NL
								+"		<td bgcolor=\"#CC0000\" height=\"5\" colspan=\"3\">&nbsp </td>"+NL
								+"	</tr>"+NL
								+"	<tr bgcolor=\"#efefef\" align=\"center\">"+NL

								+"	  <td width=\"22%\" height=\"300\" valign=\"top\">"+NL
								+"		<p>&nbsp;</p><p></p>"+NL
								+"	  </td>"+NL
								
								+"	  <td colspan=\"2\" height=\"300\" valign=\"top\"><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>"+NL
								+"		<table bgcolor=\"#efefef\" border=0 cellpadding=0 cellspacing=0 width=\"100%\">"+NL);

			

				sbResponse.append(
								 "			<tr >"+NL
								+"				<td width=\"15%\" align=\"right\">"+NL
								+"				</td>"+NL	
								+"				<td width=\"\" align=\"right\"><font face=\"Arial\" size=\"2\" color=\"#000099\"><b><a href=javascript:newWindow('ETS')><b><font face=\"Verdana\" size=\"2\">QuoteShop</font></b></a></b></font></td>"+NL
								+"			</tr>"+NL
								+BLANK_ROW
								);
				  
							  
			sbResponse.append(					  
								 "			<tr>"
								+"				<td width=\"27%\">&nbsp;</td><td width=\"9%\" align=\"center\" colspan=\"4\">&nbsp;</td>"+NL
								+"				<td width=\"13%\">&nbsp;</td><td width=\"2%\">&nbsp;</td><td width=\"12%\" align=\"center\">&nbsp;</td>"+NL
								+"				<td width=\"2%\" align=\"center\">&nbsp;</td>"+NL
								+"			</tr>"+NL
					  
								+"			<tr>"+NL
								+"				<td width=\"27%\">&nbsp;</td>"+NL
								+"				<td width=\"9%\" colspan=\"4\">&nbsp;</td>"+NL
								+"				<td width=\"13%\" align=\"right\">&nbsp;</td>"+NL
								+"				<td width=\"2%\" align=\"right\">&nbsp;</td>"+NL
								+"				<td width=\"12%\">&nbsp;</td> "+NL
								+"				<td width=\"2%\" align=\"center\">&nbsp;</td>"+NL
								+"			</tr>"+NL

								+"			<tr>"+NL
								+"				<td width=\"27%\"></td> "+NL
								+"				<td width=\"9%\" colspan=\"4\">&nbsp;</td>"+NL
								+"				<td width=\"13%\" align=\"right\">&nbsp;</td>"+NL
								+"				<td width=\"2%\" align=\"right\">&nbsp;</td>"+NL
								+"				<td width=\"12%\">&nbsp;</td>"+NL
								+"				<td width=\"2%\" align=\"center\">&nbsp;</td>"
								+"			</tr>"+NL
								
								+"		</table> "+NL
								+"	  </td>"+NL
								+"	</tr>"+NL

								+"	<tr bgcolor=\"#CC0000\">"+NL
								+"		<td height=\"16\" colspan=\"3\">"+NL
								+"		  <p align=\"center\"><font color=#ffffff face=Arial size=2>This site is best viewed with Internet Explorer 6.0 with 1024 by 768 pixel</font>"+NL
								+"		</td>"+NL
								+"	</tr>"+NL
								
								+"	<tr>"
								+"		<td bgcolor=\"#efefef\" height=\"42\" colspan=\"3\"><font color=#000000 face=\"Arial, Helvetica, sans-serif\" size=1>"+NL
								+//@@Modified by Kameswari for teh WPBN issue-38172
                "		<div align=center>Copyright © 2005-2006 DHL Global Forwarding, All rights reserved.<br> </div>"+NL
								+"			<div align=center>DHL, DHL's Logos, are registered trademarks of DHL Global Forwarding.<br></div>"+NL
								+//@@WPBN issue-38172
                "			<div align=center>Other products and brand names are trademarks of their respective owners. </div></font>"+NL
								+"		</td>"+NL
								+"	</tr>"+NL
								
								+"</table>"+NL
								
								+"</center>"+NL
								+"</div>"+NL
								+"</body>"+NL
								+"</html>"
								);

			out.println( sbResponse.toString() );
					  
		}
		catch(Exception e)
		{
			//Logger.error("index.java","Error in doPost",e);
      logger.error("index.java"+"Error in doPost"+e);
			
		}
		
		
    }

	private static final String frAddress = FoursoftWebConfig.FROM_EMAIL_ADDR;
	private static final String toAddress = FoursoftWebConfig.TO_EMAIL_ADDR;
	private static final String subject = "4S Application Status";

	/**
	* This method is used to send the mail to the FourSoft WebMaster when ever EAR is started or deployed
	* at the Partners place or at the Customers place.
	*
	*/
	
	private void sendMail()
	{
		java.util.Enumeration attributes	=	this.getServletContext().getAttributeNames();
		
		while(attributes.hasMoreElements()) {
			String	name = (String) attributes.nextElement();
			Object	value = this.getServletContext().getAttribute( name );
		}

		java.net.URL url = null;
		try {
			url = this.getServletContext().getResource("");
		} catch(Exception mfue) {}
		
		Session ms = null;

		StringBuffer	message	=	new StringBuffer("The 4S Application has been started, containing following details.\n\n\n");
		message.append("EAR Name 			: '"+ FoursoftWebConfig.EAR_NAME +"'\n");
		message.append("Appl. Resource		: '"+ url +"'\n");
		message.append("Start Date 			: '"+ new java.util.Date().toString()+"'\n");
 		message.append("Module Name 		: '"+ FoursoftWebConfig.MODULE +"'\n");
		message.append("Appl. given for		: '"+ FoursoftWebConfig.MODULE_PURPOSE +"'\n");
		message.append("Expires On 	    	: '"+ FoursoftWebConfig.EXP_DATE +"'\n");
		message.append("No of Users 		: '"+ FoursoftWebConfig.MAX_USERS_LIMIT +"'\n");
		message.append("Client Name 		: '"+ FoursoftWebConfig.CLIENT_NAME +"'\n");
		message.append("Integrated By		: '"+ FoursoftWebConfig.INT_NAME +"'\n");
		message.append("Integrated Date 	: '"+ FoursoftWebConfig.INT_DATE +"'\n");
		message.append("\n"+"Intimation from the application ends.");

		
		try
		{
			/*
			Properties properties = new Properties();
			properties.put(Context.PROVIDER_URL, "t3://localhost:7001");
			properties.put(Context.INITIAL_CONTEXT_FACTORY,  "weblogic.jndi.WLInitialContextFactory");
			properties.put(Context.SECURITY_PRINCIPAL,"user name"); 
			properties.put(Context.SECURITY_CREDENTIALS,"password");
			ic = new InitialContext(properties);
 
			*/
			if(ic==null) {
				ic = new InitialContext();
			}

		    InternetAddress fromAddress = new InternetAddress(frAddress);
		    ms = (Session) ic.lookup( FoursoftWebConfig.MAIL_SOURCE );
			ms.setDebug(false);

		  	Message msg = new MimeMessage(ms); 
			msg.setFrom(fromAddress); 
		   	msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress) ); 	  

			if (subject!=null)
		      msg.setSubject(subject); 
			else
			  msg.setSubject("nosubject");  

			msg.setSentDate(new java.util.Date());
			msg.setText(message.toString());
			Transport.send(msg);
			
			
		}
		catch(Exception e)
		{
			//Logger.error("index.java","Exception in sendMail()",e);
      logger.error("index.java"+"Exception in sendMail()"+e);
		}
		
	}
    
   	/**
   	 * Called by the servlet container when servlet is being taken out of
   	 * service. The servlet can clean up any resources that are being held.
	 */
    public void destroy()
    {
    }

}

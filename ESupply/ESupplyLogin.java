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
 * File			: ESupplyLogin.jsp
 * sub-module	: AccessControl
 * module		: esupply
 *
 * This is used to display login form 
 * 
 * author		: Madhu. P
 * date			: 5-09-2001
 * 
 * modified history
 * Modifed By		Modified Date		Reason
 * Amit Parekh		16/10/2002			If the image/ DB are corrupted or if the license has expired
 * 										this same servlet will send and HTMl response back to the
 *                                      client and will not forward it to two separate html pages.
 *                                      Additionally, this servlet will also check for maximum
 *                                      concurrent Users connected to the application as per the
 *                                      license.
 * Amit Parekh		25/04/2003			The Licensee name was changed to "Four Soft Limited". The copy right period was changed to 1999-2003
 * Amit Parekh		06/05/2003			This file's method is now synchronized as it handles (creates or manipulates) an application level object
 */

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.PrintWriter;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import java.util.Hashtable;


public final class ESupplyLogin  extends HttpServlet 
{
	private static final String CONTENT_TYPE = "text/html";

	public static final String FILE_NAME = "ESupplyLogin.java";
	public boolean isExpired = false;
	public boolean isCorrupted = false;
	private static Logger logger = null;
  
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
        logger  = Logger.getLogger(ESupplyLogin.class);
        super.init(config);
    }

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
	 
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		synchronized(this) {
			try
			{
				HttpSession session 	=	null;
			
				String message			=	"";
				String from				=	null;
				String windowName		=	null;
				String userType			=	null;
				String actionPage		=	"";
				String img				=	"";
				String img1				=	"";
				String userIdLable		=	"User Id    ";
				String locationIdLable	=	"LocationId";
				String	invalidate		=	"";

				boolean maxUsersExceeded = false;
				Hashtable	appSettings	=	(Hashtable) this.getServletContext().getAttribute("appSettings");


				if(appSettings!=null && appSettings.size() > 0) {
					try  {
						ESupplyGlobalParameters.setTerminalUserId( appSettings );
					} catch (FoursoftException ex)  {
						maxUsersExceeded = true;
						message = ex.getMessage();
					}
				}
			
				String password=null;
				String userId=null;
				String locationId=null;
				
				if(request.getAttribute("invalidate")!=null)
				{
					invalidate = (String)request.getAttribute("invalidate");
					password = (String)request.getAttribute("password");
					userId = (String)request.getAttribute("userId");
					locationId = (String)request.getAttribute("locationId");
			
				}
				//System.out.println("invalidate"+invalidate);
				RawData  imgObj = new RawData();
			
				boolean correptedFlag = imgObj.isImageCorrecpted();
				boolean licenseFlag   = imgObj.isLicenseValid();
			
				if(licenseFlag == false) {
					isExpired = true;
				}
			
				if(correptedFlag == true) {
					isCorrupted = true;
				}

				img  = "";
				//img1 = "LoadServlet?name="+imgObj.getIndexLogoName();

				response.setContentType( CONTENT_TYPE );
				response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
				response.setHeader("Pragma","no-cache"); // HTTP 1.0
				response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server

				//System.out.println("ENCODING of browser = "+request.getHeader("Accept-Encoding"));

				if(isCorrupted==false && isExpired==false)
				{

					if(!maxUsersExceeded)
					{

						session = request.getSession(true);
			
						//Logger.info(FILE_NAME ,"After crossing the Security check." ); // sec check means security check.
            logger.info(FILE_NAME +"After crossing the Security check." ); // sec check means security check.
			
						if(request.getAttribute("Login_Message") != null) {
							message = (String)request.getAttribute("Login_Message");
						}

						windowName	= request.getParameter("windowName");
						userType	= request.getParameter("userType");

						//actionPage	= "ESACLoginController.jsp?windowName="+windowName;
						actionPage	= "ESLoginController?windowName="+windowName;

						//Logger.info( FILE_NAME, "User Type : "+userType+ " Session Span is : "+session.getMaxInactiveInterval());

						if(userType.equalsIgnoreCase("ESP") || userType.equalsIgnoreCase("ELG") || userType.equalsIgnoreCase("EEP")) 
						{
							locationIdLable	= "Location Id";
						}
						else if( userType.equalsIgnoreCase("ETS") || userType.equalsIgnoreCase("ELT"))
						{
							locationIdLable	= "Terminal Id";
						}
						else if(userType.equalsIgnoreCase("ETC") || userType.equalsIgnoreCase("ELC") || userType.equalsIgnoreCase("ESC") || userType.equalsIgnoreCase("EPC")) 
						{
							locationIdLable	= "Customer Id";
						}
						else if(userType.equalsIgnoreCase("ELV") || userType.equalsIgnoreCase("EPV") || userType.equalsIgnoreCase("ESV")) 
						{
							locationIdLable	= "Vendor Id ";
						}
						else if(userType.equalsIgnoreCase("EPT")) 
						{
							locationIdLable	= "Transporter Id ";
						}
			
						from	= request.getParameter("from");
						if(from==null) {
							from = "";
						}
					}
				
					PrintWriter out = response.getWriter();
				
					out.write("<html><head><title>Welcome to DHL QuoteShop</title> \n"
								+"<script language=\"JavaScript\" src=\"eventhandler.js\"></script>\n"
								+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"> \n"
								+"<SCRIPT language=\"JavaScript\" src=\"sha1.js\"></SCRIPT>\n"
								+"<SCRIPT lanugage = \"javascript\"> \n "
								+"var from =  '"+from+"'; \n"
								+"function getPassword() \n { \n var width = 770; \n var height = 155; \n var top = (screen.availHeight - height)/2;"
								+"var left = (screen.availWidth  - width)/2; \n var Url	= 'ResetPassword.jsp';\n"
								+"var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';"
								+"var Options = 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no'; \n"
								+"var Features = Bars+' '+Options; \n var Win=open(Url,'Doc',Features); \n if (!Win.opener) Win.opener = self; \n"
								+"if (Win.focus != null)  \n Win.focus(); \n return false;}\n"
								+"function placeFocus() \n { \n "
								+"var browserName =  navigator.appName \n"
								+"if( document.forms.length > 0 ) \n { \n var field = document.forms[0] \n for(i = 0; i < field.length; i++) \n"
								+"{ \n if( (field.elements[i].type == \"text\") || (field.elements[i].type == \"textarea\") || (field.elements[i].type.toString().charAt(0) == \"s\")) \n"
								+"{ \n document.forms[0].elements[i].focus() \n break \n } \n } \n } \n document.forms[0].brName.value=browserName \n } \n"
								+" function stringFilter(input) \n { \n s = input.value \n filteredValues = \"'\"   // Characters stripped out. \n"
								+" var i \n var returnString = \"\" \n for (i = 0; i < s.length; i++) // Search through string and append to unfiltered values to returnString. \n"
								+"{ \n var c = s.charAt(i) \n if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase() \n } \n input.value = returnString \n } \n"
								+"function validateForm() \n { \n var flag = false \n if( document.forms[0].userId.value.length == 0 ) \n { \n"
								+"var userId = \"User Id\" 	\n alert(\"Please enter \"+userId) \n document.forms[0].userId.focus()  \n return false \n } \n"
								+"else if( document.forms[0].password.value.length == 0 ) \n { \n alert(\"Please enter Password\") \n document.forms[0].password.focus() \n return false \n } \n"
								+"else if( document.forms[0].locationId.value.length == 0 ) \n { \n var locationId = \"");
					out.print(locationIdLable);
					out.write("\" \n alert(\"Please enter \"+locationId) \n"
								+"document.forms[0].locationId.focus() \n return false \n } \n "
                //+" else{document.forms[0].password.value=document.forms[0].password.value;}} \n" 
								+" else{document.forms[0].password.value=hex_sha1(document.forms[0].password.value);}} \n" 
                + "function warnCapsLock(display){document.getElementById('warning').style.display = display ? 'block' : 'none';} \n"
                + "function ucasePressed(Event){if (window.event) Event = window.event; if (Event.keyCode) {if (Event.keyCode <= 90 && Event.keyCode >= 65) return !Event.shiftKey;if (Event.keyCode <= 122 && Event.keyCode >= 97) return Event.shiftKey;}return false;}"
                +"function clearMsg(){document.getElementById('warning').style.display = 'none';}"
								+"function resetForm() \n { document.loginForm.reset() \n return false \n } \n </SCRIPT> \n <style type=\"text/css\"> \n"
								+" <!-- \n"
								+" a:link {  text-decoration: none} \n"
								+" a:hover {  text-decoration: none; } \n"
								+"--> \n "
                + "#warning {display: none;color: red; font-style: bold; padding: 0.5em; }  </style> \n </head> \n" );

						if(invalidate.equals("y")){
						out.write("<body text=\"#000000\" leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" > \n"
									+"<table width=\"100%\" border=\"2\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#660000\" align=\"center\"> \n"
									+"<tr><td width=\"100%\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr><td bgcolor='#efefef' width=\"100%\"><img width='201' height='62'  src=\"images/DHLlogo.jpg");
						out.print("");
						out.write("\" width=\"264\" height=\"63\"></td> \n"
									+"</tr><tr><td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n </tr></table><p>&nbsp;</p><p>&nbsp;</p> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" align=\"center\"> \n"
									+"<tr><td width=\"50%\">");

						out.print("");
						out.write("</td> \n"
									+"<td  valign=\"top\" width=\"50%\" align=\"center\"> \n <form name = \"loginForm\" action = \"");
						out.print(actionPage);
						out.write("\" method=\"POST\"> <input type = \"hidden\" name = \"userType\"  value=\"");
						out.print(userType);
						out.write("\"> <input type = \"hidden\" name = \"password\"  value=\"");
						out.print(password);
						out.write("\"> <input type = \"hidden\" name = \"userId\"  value=\"");
						out.print(userId);
						out.write("\"> <input type = \"hidden\" name = \"locationId\"  value=\"");
						out.print(locationId);
						out.write("\"> <input type = \"hidden\" name = \"invalidate\"  value=\"yes");
						out.write("\" > <input type=hidden name=brName value=\"\"> <input type = \"hidden\" name = \"caller\"  value=\"login\" > ");
						out.write("&nbsp;<p>&nbsp;</p><table width=\"275\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"#efefef\"> \n"
									+"<tr><td valign=\"top\" colspan=\"2\"><img src=\"images/login_title.gif\" width=\"256\" height=\"51\"></td> \n"						
									+"</tr><tr><td valign=\"top\" width=\"275\" colspan=2><b><font face=\"Arial\" size=\"2\" color=\"#FF0000\">"); 
						out.print(message); 
					
						out.write("</font></b></td> </tr><tr><td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\"> ");
						out.write("</font></b></font></td> \n <td width=\"192\">&nbsp;</td>");

						//out.write("</tr><tr> \n <td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\"></font></b></font></td> \n"
						//			+"<td width=\"192\">&nbsp;</td>" );

						out.write("</tr>");
				
						out.write("<tr> \n <td valign=\"top\" width=\"100\" align=\"right\"> "
									+"<input type=submit value=\"Invalidate\" name=\"image1\"></td>\n"
									+"<td  valign=\"top\" align=\"center\"> "
									+"<input type=button value=\"Cancel\" onClick='javascript:window.close();' name=\"image1\"></td>\n"
									+"</tr></table></form></td></tr></table><br> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr> <td bgcolor='#efefef' width=\"100%\">&nbsp;</td></tr><tr> \n"
									+"<td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n"
									+"</tr></table></td></tr></table></body></html> ");
						}
						else if(!maxUsersExceeded) {
			
						out.write("<body text=\"#000000\" leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" onLoad=\"placeFocus()\"> \n"
									+"<table width=\"100%\" border=\"2\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#660000\" align=\"center\"> \n"
									+"<tr><td width=\"100%\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr><td bgcolor='#efefef' width=\"100%\"><img width='201' height='62' src=\"images/DHLlogo.jpg" );
						out.print("");
						out.write("\" width=\"264\" height=\"63\"></td> \n"
									+"</tr><tr><td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n </tr></table><p>&nbsp;</p><p>&nbsp;</p> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" align=\"center\"> \n"
									+"<tr><td width=\"50%\">");

						out.print("");
						out.write("</td> \n"
									+"<td  valign=\"top\" width=\"50%\" align=\"center\"> \n <form name = \"loginForm\" action = \"");
						out.print(actionPage);
						out.write("\" method=\"POST\"> <input type = \"hidden\" name = \"userType\"  value=\"");
						out.print(userType);
						out.write("\" > <input type = \"hidden\" name = \"caller\"  value=\"login\" > ");
						out.write("&nbsp;<p>&nbsp;</p><table width=\"275\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"#efefef\"> \n"
									+"<tr><td valign=\"top\" colspan=\"2\"><img src=\"images/login_title.gif\" width=\"256\" height=\"51\"></td> \n"						
									+"</tr><tr><td valign=\"top\" width=\"275\" colspan=2><b><font face=\"Arial\" size=\"2\" color=\"#FF0000\">"); 
						out.print(message);

						out.write("</font></b></td> </tr><tr><td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\"> ");
						out.print(userIdLable);

						out.write("</font></b></font></td> \n <td width=\"192\"> <font face=\"Verdana\" size=\"2\"><b><font color=\"\">:</font></b></font> \n");
						out.write("<input style=\"background-color:; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366\" type=\"text\" name=\"userId\" size=14 maxlength=25 VALUE='' onBlur=\"stringFilter(this)\"> \n");
						out.write("</td></tr><tr> \n <td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\">Password</font></b></font></td> \n"
									+"<td width=\"192\"> <font face=\"Verdana\" size=\"2\"><b><font color=\"\"> \n"
									+":</font></b></font>&nbsp;" );
						out.write("<input style=\"background-color:; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366\" type='password' 	value='' name='password' size=14 maxlength=25 onblur='clearMsg();' onkeypress='if (window.event) Event = window.event; warnCapsLock(ucasePressed(Event))'> \n");
						out.write("</td></tr><tr> \n <td width=\"82\"><font face=\"Verdana\" size=\"1\" color=\"\"><b>");
						out.print(locationIdLable);
						out.write("</b></font></td> \n <td width=\"192\"><font face=\"Verdana\" size=\"2\" color=\"\"><b>:</b></font> \n");
						out.write("<input style=\"background-color:; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366\" type='text' name='locationId' value='' size=14 maxlength=25  onBlur=\"stringFilter(this)\"> \n");
				
						out.write("</td></tr><tr><td id='warning' colspan='3'><font face=\"Verdana\" size=\"2\"><B>Warning: CAPS LOCK IS ON.</b><br> Having Caps Lock on may cause you to enter your password incorrectly.<br> You should press Caps Lock to turn it off before entering your password.</font></td></tr>"
                  +"<tr> \n <td valign=\"top\" align=\"center\" width=\"82\">&nbsp;</td> <td valign=\"top\" align=\"center\" width=\"192\"> "
									+"<input type=image src=\"images/Login.gif\" width=\"60\" height=\"21\" onClick='return validateForm()' name=\"image1\">&nbsp;&nbsp;&nbsp;<input type=image src=\"images/Reset.gif\" width=\"60\" height=\"21\" onClick='return resetForm()' name=\"image2\"></td> \n"
									+"<input type=hidden name=brName value=\"\"> "
									+"</tr></table><A HREF='javascript:void(0);' onClick='getPassword();' STYLE='TEXT-DECORATION: NONE'>Forgot Password</A></form></td></tr></table><br> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr> <td bgcolor='#efefef' width=\"100%\">&nbsp;</td></tr><tr> \n"
									+"<td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n"
									+"</tr></table></td></tr></table></body></html> ");
					} else {

						out.write("<body text=\"#000000\" leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\" > \n"
									+"<table width=\"100%\" border=\"2\" cellspacing=\"0\" cellpadding=\"0\" bordercolor=\"#660000\" align=\"center\"> \n"
									+"<tr><td width=\"100%\"><table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr><td bgcolor='#efefef' width=\"100%\"><img width='201' height='62' src=\"images/DHLlogo.jpg" );
						out.print("");
						out.write("\" width=\"264\" height=\"63\"></td> \n"
									+"</tr><tr><td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n </tr></table><p>&nbsp;</p><p>&nbsp;</p> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" align=\"center\"> \n"
									+"<tr><td width=\"50%\">");

						out.print("");
						out.write("</td> \n"
									+"<td  valign=\"top\" width=\"50%\" align=\"center\"> \n <form name = \"loginForm\" action = \"");
						out.print(actionPage);
						out.write("\" method=\"POST\"> <input type = \"hidden\" name = \"userType\"  value=\"");
						out.print(userType);
						out.write("\" > <input type = \"hidden\" name = \"caller\"  value=\"login\" > ");
						out.write("&nbsp;<p>&nbsp;</p><table width=\"275\" border=\"0\" cellspacing=\"1\" cellpadding=\"4\" bgcolor=\"#efefef\"> \n"
									+"<tr><td valign=\"top\" colspan=\"2\"><img src=\"images/login_title.gif\" width=\"256\" height=\"51\"></td> \n"						
									+"</tr><tr><td valign=\"top\" width=\"275\" colspan=2><b><font face=\"Arial\" size=\"2\" color=\"#FF0000\">"); 
						out.print(message); 
						out.write("</font></b></td> </tr><tr><td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\"> ");
						out.write("</font></b></font></td> \n <td width=\"192\">&nbsp;</td>");

						out.write("</tr><tr> \n <td width=\"82\"><font face=\"Verdana\" size=\"1\"><b><font color=\"\"></font></b></font></td> \n"
									+"<td width=\"192\">&nbsp;</td>" );

						out.write("</tr>");
				
						out.write("<tr> \n <td colspan='2' valign=\"top\" align=\"center\"> "
									+"<input type=button value=\"OK\" onClick='javascript:window.close();' name=\"image1\"></td>\n"
									+"</tr></table></form></td></tr></table><br> \n"
									+"<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
									+"<tr> <td bgcolor='#efefef' width=\"100%\">&nbsp;</td></tr><tr> \n"
									+"<td bgcolor='#efefef' width=\"100%\">&nbsp;</td> \n"
									+"</tr></table></td></tr></table></body></html> ");
			
					}
			
					out.flush();
					out.close();

				} else {
					try  {
						if(isCorrupted) {
							sendResponse( response, "COR" );
						} else if(isExpired) {
							sendResponse( response, "LIC" );
						}
					} catch (Exception ex)  {}
				}
			
			} catch(Exception e) {
				//Logger.error(FILE_NAME, " [doPost(request, response)] -> ", e);
        logger.error(FILE_NAME+ " [doPost(request, response)] -> "+ e);
			}
			
		}//sync block
		
    }

   	/**
   	 * Called by the servlet container when servlet is being taken out of
   	 * service. The servlet can clean up any resources that are being held.
	 */
    public void destroy()
    {
    }
	
	private void doDispatcher(HttpServletRequest request, HttpServletResponse response, String forwardFile) 
					throws IOException, ServletException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.error(FILE_NAME+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}

	private void sendResponse(HttpServletResponse response, String flag) throws IOException, ServletException{ 
		
		PrintWriter out	=	response.getWriter();
		
		if(flag.equals("LIC")) {
			out.write( VIEW_START + licExpiry + VIEW_END );
		}
		if(flag.equals("COR")) {
			out.write( VIEW_START +  appCorr  + VIEW_END );
		}
		out.flush();
		out.close();
	}

	public static final String	VIEW_START	=
			"<html><head><title>Notification</title>"+
			"<script language='JavaScript' src='eventhandler.js'>"+
			"</script>"+
			"</script>"+
			"</head>"+
			"<body bgcolor='' leftmargin='5' topmargin='0' marginwidth='0' marginheight='0'>"+
			"<br><br><br><br>"+
			"<table width='760' border='1' bordercolor='#660000' cellspacing='0' cellpadding='0' align='center'>"+
			" <tr>"+
			"	<td>"+
			"	  <table width='100%' cellspacing='1' cellpadding='4' height='500'>"+
			"		<tr valign='top' bgcolor='#666699'> "+
			"		  <td bgcolor='#efefef' width='100%'><font color='' face='Arial, Helvetica, sans-serif' size='2'><b><img width='201' height='62' src='images/DHLlogo.jpg'></b></font></td>"+
			"		</tr>";

	public static final String	licExpiry	=
			"		<tr valign='middle' bgcolor='#666699' align='center'>"+ 
			"		  <td bgcolor='#efefef' width='100%'><font color='' face='Arial, Helvetica, sans-serif' size='2'><b>Thank you for using eSupply!</b></font></td>"+
			"		</tr>"+
			"		<tr valign='top' align='center'> <td>&nbsp;</td></tr>"+
			"		<tr valign='top'> "+
			"		  <td align='center'>"+ 
			"			<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>"+
			"			<p>	<font face='Arial, Helvetica, sans-serif' size='2' color='#3333FF'><b>The license for the product has expired.</b><br><b>For more information, please contact </b> "+
			"					<a href='mailto:webmaster@four-soft.com '>webmaster@four-soft.com</a>"+
			"				</font>"+
			"			</p>"+
			"			<p>&nbsp;</p>"+
			"			<p><font face='Arial, Helvetica, sans-serif' size='2' color='#3333FF'><br></font>"+
			"			</p>"+
			"		  </td>"+
			"		</tr>";
			

	public static final String	appCorr	=
			"		<tr valign='middle' bgcolor='#666699' align='center'>"+ 
			"		  <td bgcolor='#efefef' width='100%'><font color='' face='Arial, Helvetica, sans-serif' size='2'><b>Warning!</b></font></td>"+
			"		</tr>"+
			"		<tr valign='top' align='center'><td>&nbsp;</td></tr>"+
			"		<tr valign='top'> "+
			"		  <td align='center'> "+
			"			<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>"+
			"			<p><font face='Arial, Helvetica, sans-serif' size='2' color='#3333FF'><b>The application has been mishandled!</b><br><b>Un-authorised access to the application.</b></font></p>"+
			"			<p>&nbsp;</p>"+
			"			<p><font face='Arial, Helvetica, sans-serif' size='2' color='#3333FF'></font><br>"+
			"			</p>"+
			"		  </td>"+
			"		</tr>";
		
	public static final String	VIEW_END	=	
			"		<tr valign='top'><td><div align='center'></div></td></tr>"+
			"		<tr valign='top' bgcolor='#666699' align='center'>"+ 
			"		  <td bgcolor='#efefef' width='100%'>"+
			"			<font color='#5c5c5c' face='Arial, Helvetica, sans-serif, Abadi MT Condensed Light' size=1>"+
			"				&nbsp;<font color=''>Copyright © 1999-2003 Four Soft Limited, All rights reserved.<br>"+
			"				Foursoft, Foursoft's Logos, are registered trademarks of Four Soft Limited in India.<br>"+
			"				Other products and brand names are trademarks of their respective owners.</font></font><font color='' face='Arial, Helvetica, sans-serif, Abadi MT Condensed Light' size='1'>"+ 
			"			</font>"+
			"		  </td>"+
			"		</tr>"+			
			"		<tr valign='top'><td bgcolor='#efefef' width='100%'>&nbsp;</td></tr>"+
			"	  </table>"+
			"	</td>"+
			"  </tr>"+
			"</table>"+
			"</body>"+
			"</html>";

}

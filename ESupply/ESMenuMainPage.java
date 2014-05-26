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
 * @author ramakumar
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.BundleFile;


public final class ESMenuMainPage  extends HttpServlet 
{
    private static final String CONTENT_TYPE = "text/html";
	private static final String fileName = "ESMenuMainPage.java";
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
        logger  = Logger.getLogger(ESMenuMainPage.class);
        super.init(config);
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
	  public void doPost(HttpServletRequest request,
    			HttpServletResponse response)
    			throws ServletException, IOException
    {
	  doGet(request,response);
	  }
    public void doGet(HttpServletRequest request,
    			HttpServletResponse response)
    			throws ServletException, IOException
    {
		response.setContentType(CONTENT_TYPE);
        String head = "\n</script></head> \n <body ";
        HttpSession session = request.getSession();
        ServletContext appli = getServletConfig().getServletContext();
		Boolean passwordBool = (Boolean)session.getAttribute("userPasswordValidation");
		Boolean warningBool = (Boolean)session.getAttribute("warningLimitCrossed");
		Boolean firstLogin	=	(Boolean)session.getAttribute("firstLogin");
    
		boolean passwordStatus = passwordBool != null? passwordBool.booleanValue():false;
		boolean warningStatus = warningBool != null? warningBool.booleanValue():false;
		boolean loginStatus	=	firstLogin != null? firstLogin.booleanValue():false;
		
    if (passwordStatus && !warningStatus)
			head = "function call()\n{ \n var goahead = false; \n goahead = confirm(\""+((BundleFile) appli.getAttribute("bundle")).getBundle().getString("100526")+"\");\n if(goahead==true) \n{\n document.esmain.submit(); \n}\n\n}"+head+"onLoad=\"call();\"";
		else if (passwordStatus && warningStatus)
			head = "function call()\n{\n alert(\""+((BundleFile) appli.getAttribute("bundle")).getBundle().getString("100527")+" \");\n \n parent.frames[0].OpenClose(); document.esmain.submit(); \n\n}"+head+"onLoad=\"call();\"";
		else if(loginStatus)
			head ="function call()\n{\n alert(\""+((BundleFile) appli.getAttribute("bundle")).getBundle().getString("100090")+" \");\n \n document.esmain.submit(); \n\n}"+head+"onLoad=\"call();\"";

		session.removeAttribute("userPasswordValidation");
		session.removeAttribute("warningLimitCrossed");
		session.removeAttribute("firstLogin");
    if((passwordStatus && !warningStatus) || (passwordStatus && warningStatus) || loginStatus)
    {
      PrintWriter out = response.getWriter();    
      out.write("<html><head><script language=\"JavaScript\" src=\"eventhandler.js\"></script> \n\n <script language=\"JavaScript\" >\n "
                    +head				
            +">\n <form method=\"post\" action=\"ESACPasswordChangeView.jsp\" name=\"esmain\">\n<p><font color=red> </font></p> \n"
            +"<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p> \n"
            +"<table width=\"800\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"> \n"
            +"<tr align=\"center\" valign=\"top\"> \n"
            +"<td> ");
      //out.print("LoadServlet?name=index_caption.jpg");
      out.write("</td> \n"
            +"</tr></table><font color=red><H1 align=center>&nbsp; </h1></font></form></body></html> ");
  
      response.setHeader("Expires", "0");
      response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
      response.setHeader("Pragma", "public");
    }
    else
    {
      doDispatcher(request,response,"QMSReportController?Operation=ActivitySummary");
    }
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
			inValidateSession( request );
			//Logger.error(fileName, " [doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);\
      logger.error(fileName+ " [doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}

	private void inValidateSession( HttpServletRequest request ) {

		try {
			if(request!=null) {
				HttpSession		session	=	request.getSession();
				if(session!=null) {
					session.invalidate();
					//Logger.error(fileName, "Log-in process was terminated as an error occurred while logging in.");				
          logger.error(fileName+ "Log-in process was terminated as an error occurred while logging in.");				
				}
			}
		} catch(Exception ex) {}
		
	}

}

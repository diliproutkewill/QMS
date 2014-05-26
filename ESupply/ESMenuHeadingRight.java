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


public final class ESMenuHeadingRight  extends HttpServlet 
{
    private static final String CONTENT_TYPE = "text/html";

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
    public void doGet(HttpServletRequest request,
    			HttpServletResponse response)
    			throws ServletException, IOException
    {
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.write("<html>");
		out.write("<head><script language=\"JavaScript\" src=\"eventhandler.js\"></script></head>");
        out.write("<body bgcolor='#C0C0C0'><p align='right'>");
		//out.write("<img src=\"");
		//out.print("LoadServlet?name=eSupply_product_logo.gif\"");
        out.write("</p></body></html>");
    }
   	/**
   	 * Called by the servlet container when servlet is being taken out of
   	 * service. The servlet can clean up any resources that are being held.
	 */
    public void destroy()
    {
    }

}

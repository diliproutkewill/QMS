/*Which is wrriten for task Management
wrriten  by prasad*/
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
//import com.foursoft.etrans.setup.common.ejb.sls.ETransHOSuperUserSetupSession;
//import com.foursoft.etrans.setup.common.ejb.sls.ETransHOSuperUserSetupSessionHome;

public class ETTaskEvntController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private static Logger logger = null;
  
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(ETTaskEvntController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
     doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    response.setContentType(CONTENT_TYPE);
    String terminal = request.getParameter("terminal");
   try{
    HttpSession session =request.getSession();
    ESupplyGlobalParameters loginbean=(ESupplyGlobalParameters)session.getAttribute("loginbean");
   // ETransHOSuperUserSetupSessionHome home = (ETransHOSuperUserSetupSessionHome)loginbean.getEjbHome("ETransHOSuperUserSetupSessionBean");
  //  ETransHOSuperUserSetupSession remote  = (ETransHOSuperUserSetupSession)home.create();
 //   boolean flag = remote.checkPendEvnts(terminal); 
    PrintWriter out = response.getWriter();
    String color="";
    String image = null;
    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html>");
    out.println("<head><title>ETTaskEvntController</title></head><script language=javascript>");
    out.println("function checkImage(image){var data ='';if(image=='images/envelopeRed.gif'){ data	+= \"<INPUT TYPE='image' SRC='images/envelopeRed.gif'  height='28' width='31' BORDER='0' ALT='Shipment Tracking' onClick='return newWindow()'> \";");
    out.println("}else{data	+= \"<IMG SRC='images/envelopewhite.gif' name=envelope ALT='Shipment Tracking'  height='28' width='31' BORDER=0>\";");
    out.println("}if( document.layers){	document.layers.cust10.document.write(data);// here 'cust' is the nameof span(see below for name span)");
    out.println("document.layers.cust10.document.close();}else{if(document.all){cust.innerHTML = data;}}}");
    out.println("function newWindow(){checkImage('images/envelopewhite.gif');var myUrl        = 'etrans/ETTaskPendShow.jsp?terminal="+terminal+"';");
    out.println("var myBars       = 'directories=no,location=right,menubar=no,status=no,titlebar=no,toolbar=no';");
    out.println("var myOptions    = 'scrollbars=no,width=1000,height=500,resizable=no';var myFeatures   =  myBars+','+myOptions;var newWin       = open(myUrl,'myDoc',myFeatures);return false;}</script>");
    /* if(flag)
        image = "images/envelopeRed.gif";
     else
//        image = "images/envelopeWhite.gif"; */
    out.println("<body bgcolor='#C0C0C0' onLoad=\"checkImage('"+image+"')\">");
    out.println("<table border='0' cellspacing='0' cellpadding='0' height='24' bgcolor='#C0C0C0'><tr><td valign='middle' WIDTH='12' HEIGHT='24' BGCOLOR='#C0C0C0'>");
    out.println("<span id='cust'></span></td></tr></table><META HTTP-EQUIV='Refresh' CONTENT='90'></body></html>");
    out.close();
    }
    catch(Exception ex)
     {
       //Logger.error("ETTaskEvntController.java","Error occured in checking Task Evnents pending or not");
       logger.error("ETTaskEvntController.java"+"Error occured in checking Task Evnents pending or not");
     }
    
  }
}

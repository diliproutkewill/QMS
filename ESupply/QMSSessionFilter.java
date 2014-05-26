/*
 * Source File Name:   CredentialFilter.java
 */ 
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

public class QMSSessionFilter  implements Filter
{
  private static final String FILE_NAME = "QMSSessionFilter.java";
  private static ArrayList staticList = null;
  private static Logger logger = null;
    public QMSSessionFilter()
    {
        logger  = Logger.getLogger(QMSSessionFilter.class);
    }

    public void init(FilterConfig config)
        throws ServletException
    {
        staticList = new ArrayList();
        //staticList.add("/");
        staticList.add("/index");
        staticList.add("/index.jsp");
        staticList.add("/ESupplyLogin");
        staticList.add("/ESLoginController");
        staticList.add("/ESupplyLogin.jsp");
        staticList.add("/ESACLoginController.jsp");
        staticList.add("/ESACLogout.jsp");
        staticList.add("/ESupplyCalendar.jsp");
        staticList.add("/ESupplyForgotPassword.jsp");
        staticList.add("/ResetPassword.jsp");
        staticList.add("/PasswordProcess.jsp");
        staticList.add("/ESLogout");
        staticList.add("/ESFoursoft_css.jsp");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletRequest newRequest = null;
        if(request instanceof HttpServletRequest)
        {
            newRequest = (HttpServletRequest)request;
            HttpSession session = newRequest.getSession(false);
            String requestURI = newRequest.getRequestURI();
           // Logger.info(FILE_NAME,"requestURI == "+requestURI);
            int position = requestURI.lastIndexOf("/");            
            String page = requestURI.substring(position);
            //Logger.info(FILE_NAME,"requestURI :: "+page);
            printRequestAndHeader(newRequest);
            if(!staticList.contains(page))
            {
                RequestDispatcher rd = null;
                if(session == null)
                {
                   // Logger.info(FILE_NAME,"session == null");
                    //printRequestAndHeader(newRequest);
                    //Logger.info(FILE_NAME,"session == null END");
                    rd = newRequest.getRequestDispatcher("/ESACSessionExpired.html");
                    request.setAttribute("ErrorMessage", "Users Session Expired! Please ReLogin ");
                   // Logger.info(FILE_NAME,"rdrdrd::"+rd);
                    rd.forward(request, response);
                } else
                {
                //Logger.info(FILE_NAME,"session.getAttribute(loginbean)"+session.getAttribute("loginbean"));
                    if(session.getAttribute("loginbean") == null || (session.getAttribute("loginbean")!=null && ((ESupplyGlobalParameters)session.getAttribute("loginbean")).getUserId()==null))
                    {
                        //Logger.info(FILE_NAME,"CredentialFilter - user credentials does not exist");
                       // Logger.info(FILE_NAME,"loginbean == null");
                        //printRequestAndHeader(newRequest);
                        //Logger.info(FILE_NAME,"loginbean == null End");
                        //Logger.info(FILE_NAME,session.getId() + "- session.tracker : " + (String)session.getAttribute("session.tracker") + "- Time: " + (new Date()).toString());
                        rd = request.getRequestDispatcher("/ESACSessionExpired.html");
                        request.setAttribute("ErrorMessage", "Users Session Expired! Please ReLogin ");
                       // Logger.info(FILE_NAME,"requestrequest::"+request);
                        //Logger.info(FILE_NAME,"rdrdrd::"+rd);
                        rd.forward(request, response);
                        //Logger.info(FILE_NAME,"newRequestnewRequest0000::"+newRequest);
                    }
                    else
                    {
                      //Logger.info(FILE_NAME,"newRequestnewRequest111::"+newRequest);
                      generateRequestID(newRequest);
                      //Logger.info(FILE_NAME,"newRequestnewRequest222::"+newRequest);
                      chain.doFilter(request, response);
                    }
                    //Logger.info(FILE_NAME,"newRequestnewRequest3333::"+newRequest);
                }
            } else
            {
                chain.doFilter(request, response);
                //Logger.info(FILE_NAME,"in else chain.doFilter");
                logger.info(FILE_NAME+"in else chain.doFilter");
            }
        }
    }

    private void printRequestAndHeader(HttpServletRequest req)
    {
        String dateStr = "Time: " + (new Date()).toString();
        String hdr = null;
        String jvmId = (String)req.getSession().getServletContext().getAttribute("JVM_ID");
        dateStr = dateStr + " JVM ID: " + jvmId;
       /* for(Enumeration e = req.getHeaderNames(); e.hasMoreElements(); Logger.info(FILE_NAME,dateStr + " " + req.getRemoteAddr() + " " + "Hdr: " + hdr + "    value: " + req.getHeader(hdr)))
            hdr = (String)e.nextElement();*/

        //Logger.info(FILE_NAME,dateStr + " " + req.getRemoteAddr() + " " + "URI " + req.getRequestURI());
        //Logger.info(FILE_NAME,dateStr + " " + req.getRemoteAddr() + " " + "Operation " + req.getParameter("Operation"));
        //Logger.info(FILE_NAME,dateStr + " " + req.getRemoteAddr() + " " + "SubOperation " + req.getParameter("SubOperation"));
        //Logger.info(FILE_NAME,dateStr + " " + req.getRemoteAddr() + " " + "NextOperation " + req.getParameter("NextOperation"));
    }

    private void generateRequestID(HttpServletRequest request)
    {
        String signOnId = "";
        try
        {
            HttpSession session = request.getSession();
            ESupplyGlobalParameters loginbean = (ESupplyGlobalParameters)session.getAttribute("loginbean");
            String jvmId = (String)session.getServletContext().getAttribute("JVM_ID");
            String uniqueNumber = " " + (new Long(System.currentTimeMillis())).toString();
            String serverIp = InetAddress.getLocalHost().getHostAddress();
            if(loginbean!=null && loginbean.getUserId()!=null && loginbean.getLocationId()!=null)
                signOnId = loginbean.getUserId() + ", " + loginbean.getLocationId();
            String requestID = serverIp + "," + jvmId + "," + session.getId() + "," + uniqueNumber + "," + signOnId + ", " + request.getParameter("ModuleName") + "," + request.getParameter("Operation") + "," + request.getParameter("subOperation");
            session.setAttribute("requestID", requestID);
        }
        catch(Exception e) 
        {
            //Logger.info(FILE_NAME,"Error in Filter :"+e);
            logger.info(FILE_NAME+"Error in Filter :"+e);
            e.printStackTrace();
        }
    }

    public void destroy()
    {
    }
}

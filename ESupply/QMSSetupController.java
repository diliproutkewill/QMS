import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.setup.ejb.sls.SetUpSession;
import com.qms.setup.ejb.sls.SetUpSessionHome;
import com.qms.setup.java.QMSContentDOB;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.PrintWriter;
import java.io.IOException;

public class QMSSetupController extends HttpServlet 
{
  private static final String CONTENT_TYPE = "text/html; charset=windows-1252";
  private String   FILE_NAME = "QMSSetupController.java";
  private ErrorMessage errorMessageObject = null;   
  ArrayList  keyValueList       = new ArrayList();
  private  String nextNavigation       = null;  
  SetUpSession              remote     =  null;
  SetUpSessionHome          home       =  null;
   QMSContentDOB       dob        =  null;
   HttpSession              session      =   null;
   private static Logger logger = null;
   
  public void init(ServletConfig config) throws ServletException
  {
    logger  = Logger.getLogger(QMSSetupController.class);
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    try
    {
       doPost(request,response);  
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.info(FILE_NAME,""+e.toString());
      logger.info(FILE_NAME+""+e.toString());
    }
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String  operation         =   null;
    String  subOperation      =   null;    
    int     noOfcols          =   0;
    String	str	              =   "";
    
    try
    {
      operation      =   request.getParameter("Operation");
      subOperation   =   request.getParameter("subOperation");
      //Logger.info(FILE_NAME,"subOperation asdfsdf"+subOperation);
      //Logger.info(FILE_NAME,"operation sdf"+operation);
      session  = request.getSession();
      ESupplyGlobalParameters   loginbean  =  (ESupplyGlobalParameters)session.getAttribute("loginbean");
      if("ViewAll".equalsIgnoreCase(operation) || "Invalidate".equalsIgnoreCase(operation))
      {        
               
        if(subOperation==null)
        {
            str+="SHIPMENTMODE,";
            noOfcols++;
            if(request.getParameter("HEADERFOOTER")!=null && request.getParameter("HEADERFOOTER").equals("0"))
            {
              str+="HEADERFOOTER,";
              noOfcols++;
            }
            if(request.getParameter("CONTENTID")!=null && request.getParameter("CONTENTID").equals("0"))
            {
              str+="CONTENTID,";
              noOfcols++;
            }
            if (request.getParameter("DESCRIPTION")!=null && request.getParameter("DESCRIPTION").equals("0"))
            {
              str+="DESCRIPTION,";
              noOfcols++;
            }
            if (request.getParameter("FLAG")!=null && request.getParameter("FLAG").equals("0"))
            {
              str+="FLAG,";
              noOfcols++;
            }
            
            home    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
            remote  =  home.create();              
            //@@ Commented & Added by subrahmanyam for the pbn id: 203873 on 26-APR-10            
            //ArrayList list     =  remote.getAllContentDetails(operation,loginbean.getTerminalId());
            ArrayList list     =  remote.getAllContentDetails(operation,loginbean.getTerminalId(),loginbean.getAccessType());
              //@@ ended 203873
            request.setAttribute("content",list);  
            //nextNavigation    =   "QMSContentDescriptionView.jsp";  
            nextNavigation    =   "QMSContentDescriptionViewAll.jsp?str="+str+"&noOfcols="+noOfcols;
        }
        else
        {
          ArrayList contentList  = null;
          session =  request.getSession();
          contentList   =  (ArrayList)session.getAttribute("content");
          session.removeAttribute("content");
          int contentIdListSize	=	contentList.size();
          for(int i=0;i<contentIdListSize;i++)
          {  
            dob	=	(QMSContentDOB)contentList.get(i);
            if(request.getParameter("invalidate"+i)!=null)
                dob.setInvalidate("T");            
            else
                dob.setInvalidate("F");
          }   
                home	  =	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		            remote	=	(SetUpSession)home.create();
                if(contentList!=null)
                {
                  remote.invalidateContentDtls(contentList);
                }
             errorMessageObject = new ErrorMessage("Record successfully Invalidated",request.getContextPath()+"/etrans/ETAViewAllAdmin.jsp?View=ContentMaster");             
             keyValueList.add(new KeyValue("ErrorCode","RSI")); 
             keyValueList.add(new KeyValue("Operation","Invalidate")); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
             nextNavigation  =  "QMSESupplyErrorPage.jsp";
        }
        
      }else
      if("Add".equalsIgnoreCase(operation))
      {
        if(subOperation == null)
        {
           nextNavigation    =   "QMSContentAdd.jsp";
        }
        else
        {
          addContentDetails(request,response);          
        }
        
      }
      else if("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation))
      {
       
          if(subOperation==null)
          {
             nextNavigation    =   "QMSContentEnterId.jsp"; 
          }
          else if("View".equalsIgnoreCase(subOperation))
          {
              String contentId      =  request.getParameter("descId");			        
                //String shipmentMode   =  request.getParameter("shipmentMode");
              home    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
              remote  =  home.create();
              dob     =  remote.getContentDetails(contentId,loginbean.getTerminalId());
              session =  request.getSession();
              session.setAttribute("content",dob);      
              if(dob!=null)
                nextNavigation    =   "QMSContentDescriptionView.jsp"; 
              else
              {
                 errorMessageObject = new ErrorMessage("No Records Found ","QMSContentEnterId.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                 keyValueList.add(new KeyValue("Operation","Modify")); 	
                 errorMessageObject.setKeyValueList(keyValueList);
                 request.setAttribute("ErrorMessage",errorMessageObject); 
                 nextNavigation    =   "QMSESupplyErrorPage.jsp";
                 nextNavigation    =   "QMSESupplyErrorPage.jsp"; 
              }
          } 
          else if("Submit".equalsIgnoreCase(subOperation))
          {
            boolean  flag=false;
            if("Modify".equalsIgnoreCase(operation)){
             // ESupplyGlobalParameters   loginbean  =  (ESupplyGlobalParameters)request.getAttribute("loginbean");
              home    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
              remote  =  home.create();
              dob.setHeaderFooter( request.getParameter("headerFooter"));
              dob.setContentDescription( request.getParameter("contentDesc"));              
              dob.setShipmentMode( new Integer(request.getParameter("shipmentMode")).intValue());
              dob.setDefaultFlag( request.getParameter("default"));
              dob.setTerminalId(loginbean.getTerminalId());
               flag=remote.modifyContentDescription(dob);
               keyValueList = new ArrayList();
      
               if(flag==true)
                 {
                errorMessageObject = new ErrorMessage("Record successfully Modified ","QMSContentEnterId.jsp");
                keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
               }
               else
               {  
                 errorMessageObject = new ErrorMessage("Error while Modifing the Record ","QMSContentEnterId.jsp");
                 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
               }
      
               keyValueList.add(new KeyValue("Operation",operation)); 	
               errorMessageObject.setKeyValueList(keyValueList);
               request.setAttribute("ErrorMessage",errorMessageObject); 
               nextNavigation = "QMSESupplyErrorPage.jsp";
            }
            else if("Delete".equalsIgnoreCase(operation))
             {
                String contentId      =  request.getParameter("descId");			        
                //String shipmentMode   =  request.getParameter("shipmentMode");
          
               flag=remote.deleteContentDetails(contentId);
               keyValueList = new ArrayList();
  
               if(flag==true)
               {
                errorMessageObject = new ErrorMessage("Record successfully deleted","QMSContentEnterId.jsp");
                keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
              }
              else
              {
                errorMessageObject = new ErrorMessage("Error while Deleting the Record","QMSContentEnterId.jsp");
                keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
              }
  
             keyValueList.add(new KeyValue("Operation","Delete")); 	
             errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject); 
            // Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
             nextNavigation = "QMSESupplyErrorPage.jsp";
            }
        }
          
      }
      
      if(nextNavigation!=null)
         doFileDispatch(request,response,nextNavigation);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      errorMessageObject = new ErrorMessage("Error While Inserting the Records","QMSContentAdd.jsp");
      keyValueList       =  new ArrayList();
			keyValueList.add(new KeyValue("ErrorCode","ERR"));
      keyValueList.add(new KeyValue("Operation","Add")); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 	
			nextNavigation = "QMSESupplyErrorPage.jsp";
      try
      {      
         doFileDispatch(request,response,nextNavigation);
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    
    }
  }
  private void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		//Logger.info(FILE_NAME," In Dispatcher " + forwardFile );
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [doFileDispatch() ", " Exception in forwarding ---> "+ ex.toString());
      logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
  private void addContentDetails(HttpServletRequest request, HttpServletResponse response) 
  {
    try
    {
      session =  request.getSession();
      ESupplyGlobalParameters   loginbean  =  (ESupplyGlobalParameters)session.getAttribute("loginbean");        
			String[] headerFooter         =  request.getParameterValues("headerFooter");
      String[] contentId            =  request.getParameterValues("contentId");			
      String[] contentDescription   =  request.getParameterValues("contentDescription");
      String[] defaultFlag          =  request.getParameterValues("default");
			String[] airCheckbox		      =	request.getParameterValues("aircheckbox");
			String[] seaCheckbox		      =	request.getParameterValues("seacheckbox");
			String[] truckCheckbox	      =	request.getParameterValues("truckcheckbox");
      ArrayList  dobList            = new ArrayList();
      
			
			int[] shipMode        =   new int[headerFooter.length];
			if(headerFooter!=null && headerFooter.length>0)
			{
				int hFootLen	= headerFooter.length;
				for(int i=0;i<hFootLen;i++)
				{    
          if(request.getParameter("aircheckbox"+(i+1))!=null)
					{
						if(request.getParameter("seacheckbox"+(i+1))!=null)
						{
							if(request.getParameter("truckcheckbox"+(i+1))!=null)
							{
								shipMode[i]	=	7;
							}else
							{
								shipMode[i]	=	3;
							}
						}else if(request.getParameter("truckcheckbox"+(i+1))!=null)
						{
							shipMode[i]	=	5;
						}else
						{
							shipMode[i]	=	1;
						}
					}else if(request.getParameter("seacheckbox"+(i+1))!=null)
					{
						if(request.getParameter("truckcheckbox"+(i+1))!=null)
						{
							shipMode[i]	=	6;
						}else
						{
							shipMode[i]	=	2;
						}
					}else if(request.getParameter("truckcheckbox"+(i+1))!=null)
					{
						shipMode[i]	=	4;
					}
				}
			//}//Logger.info(FILE_NAME,"shipMode.length  "+shipMode);
      //Logger.info(FILE_NAME,"loginbean  "+loginbean);
//      Logger.info(FILE_NAME,"loginbean.getTerminalId()  "+loginbean.getTerminalId());
			
			for(int i=0;i<hFootLen;i++)
			{
				
				dob  = new QMSContentDOB();
				dob.setHeaderFooter(headerFooter[i]);
				//Logger.info(FILE_NAME,"shipMode[i ] "+shipMode[i]);
        dob.setShipmentMode(shipMode[i]);
				dob.setContentId(contentId[i]);
				dob.setContentDescription(contentDescription[i]);				
        //Logger.info(FILE_NAME,"dob.setShipmentMode( "+dob.getShipmentMode());
        dob.setDefaultFlag(defaultFlag[i]);
				dob.setTerminalId(loginbean.getTerminalId());
        dob.setAccessLevel(loginbean.getUserLevel());
				dobList.add(dob);
			}
			}
			   home    =  (SetUpSessionHome)LookUpBean.getEJBHome("SetUpSessionBean");
         remote  =  home.create();
				 HashMap map = remote.addContentDiscriptionDtls(dobList);
				 keyValueList = new ArrayList();
				 
				 if(map!=null)
				 {		
              String duplicate ="";			 
				      
					 if(((ArrayList)map.get("NONEXISTS")).size()==dobList.size())
					    errorMessageObject = new ErrorMessage("Record successfully added ","QMSContentAdd.jsp");
					 else if(((ArrayList)map.get("EXISTS")).size()>0){
						 ArrayList existingList  = (ArrayList)map.get("EXISTS");
						 int exisListSize	=	existingList.size();
						 for(int i=0;i<exisListSize;i++){
						      dob    =  (QMSContentDOB)existingList.get(i);
						      duplicate  = duplicate +"Content Id :"+ dob.getContentId()+"\n";
						 }
						 errorMessageObject = new ErrorMessage("The following Record(s) Already Exist \n "+duplicate,"QMSContentAdd.jsp");
					 }
					 else
						 errorMessageObject = new ErrorMessage("Error While Inserting the Records\n","QMSContentAdd.jsp");
      					
						 
					 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {					
					errorMessageObject = new ErrorMessage("Error While Inserting the Records","QMSContentAdd.jsp");
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 

			nextNavigation = "QMSESupplyErrorPage.jsp";
      //if(nextNavigation!=null)
        // doFileDispatch(request,response,nextNavigation);
		
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Error in addContentDetails"+e.toString());
      logger.error(FILE_NAME="Error in addContentDetails"+e.toString());
      errorMessageObject = new ErrorMessage("Error While Inserting the Records","QMSContentAdd.jsp");
      keyValueList       =  new ArrayList();
			keyValueList.add(new KeyValue("ErrorCode","ERR"));
      keyValueList.add(new KeyValue("Operation","Add")); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 	
			nextNavigation = "QMSESupplyErrorPage.jsp";
      try
      {      
         doFileDispatch(request,response,nextNavigation);
      }
      catch(Exception ex)
      {
        ex.printStackTrace();
      }
    }
  }
}
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.qms.setup.dao.QuoteStatusReasonDAO;
import com.qms.setup.dao.SurChargesDAO;
import com.qms.setup.java.QuoteStatusReasonDOB;
import com.qms.setup.java.SurchargeDOB;


public class QMSQuoteStatusReasonController extends HttpServlet {
	
	private static final String 	   FILE_NAME 		= "QMSQuoteStatusReasonController.java";
	private static Logger 			   logger 			= null;
	
	HttpSession					session					=	 null;
	String 						operation				=	null;
	String 						subOperation			=	 null;
	StringBuffer				errors					=	null;
	ArrayList					keyValueList			=	null;	 
	ErrorMessage 				errorMessageObject 		=	null;
	String						statusReason			= null;
	ArrayList                   statusreasonVOList               = null;
	public QMSQuoteStatusReasonController() {
		super();
		logger  = Logger.getLogger(QMSQuoteStatusReasonController.class);
	}

	
	public void destroy() {
		super.destroy(); 
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
					
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 operation     	=  request.getParameter("Operation");
		  subOperation      =  request.getParameter("subOperation");	  
		  try{
			  handleRequest(request,response);
		  	}catch(Exception e)
		  {
         doFileDispatch(request,response,"ESupplyErrorPage.jsp"); 
		  }
		
	}
	

	private void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws FoursoftException {
		try {
			QuoteStatusReasonDOB	statusReasonDOB	= new QuoteStatusReasonDOB();
			QuoteStatusReasonDAO	statusReasonDAO	= new QuoteStatusReasonDAO();
			
			 errors = new StringBuffer();
			 PrintWriter out=response.getWriter();
			 keyValueList	=	new ArrayList();
			 session		=	request.getSession();
			  System.out.println(FILE_NAME+":	Operation..."+operation);
			  System.out.println(FILE_NAME+":	subOperation..."+subOperation);
		   
		 if("Add".equalsIgnoreCase(operation) && ("".equalsIgnoreCase(subOperation) || subOperation ==null) )
			doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonMaster.jsp");
		
		 else if("Add".equalsIgnoreCase(operation) && "Enter".equalsIgnoreCase(subOperation) )
			{
					logger.info("In Handle Request for Suboperation Enter..");
					statusReason			=	request.getParameter("statusReason");
			   
			 if(statusReason!=null && !"".equals(statusReason)){
			    
				    statusReasonDOB.setStatusReason(statusReason);
											
		             if(statusReasonDAO.insertStatusReason(statusReasonDOB)==5){ 
	 					errors.append(" StatusReason:' "+statusReason +"' Is Already Exist ");
							errorMessageObject = new ErrorMessage(errors.toString(),"QMSQuoteStatusReasonController?Operation=Add"); 
							keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
							keyValueList.add(new KeyValue("Operation",operation)); 	
	
						}
					else{		    
							errors.append("StatusReason Is SuccessFully Inserted The StatusReason: "+statusReasonDOB.getStatusReason());
							errorMessageObject = new ErrorMessage(errors.toString(),"QMSQuoteStatusReasonController?Operation=Add"); 
							keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
							keyValueList.add(new KeyValue("Operation",operation)); 	
					}
					errorMessageObject.setKeyValueList(keyValueList);
	
					request.setAttribute("ErrorMessage",errorMessageObject);
					session.setAttribute("ErrorMessage","StatusReason added Sucessfully");
					session.setAttribute("ErrorCode","RSI");
					session.setAttribute("NextNavigation","QMSQuoteStatusReasonController?Operation=Add");
				    doFileDispatch(request,response,"ESupplyErrorPage.jsp")	;
			}else{
				doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonMaster.jsp?errMsg=Status Reason is Empty");
		  }
				
			}
		 else if(("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation) || "Invalidate".equalsIgnoreCase(operation) ) && ("".equalsIgnoreCase(subOperation) || subOperation==null))
		{
			doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation);
		}
		 else if("View All".equalsIgnoreCase(operation) && ("".equalsIgnoreCase(subOperation) || subOperation==null))
			{
			 
			 statusreasonVOList=(ArrayList)statusReasonDAO.getStatusReasonViewall();
			 
			 
				request.setAttribute("statusreasonVOList" ,statusreasonVOList);
				doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonViewAll.jsp");
				//getServletContext().getRequestDispatcher("/etrans/QMSQuoteStatusReasonViewAll.jsp").forward(request,response);	
			}
			 
			
			
		
			 
			
	
		
	     else if(("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)||"Delete".equalsIgnoreCase(operation) || "Invalidate".equalsIgnoreCase(operation)) && "Enter".equalsIgnoreCase(subOperation) )
		{
					String returnVal	=	"";
					
					int modeInt	=	0;
					statusReason = request.getParameter("statusReason");
					
					if(statusReason!=null && !"".equals(statusReason)){
								
							
							statusReasonDOB.setStatusReason(statusReason);
							
							returnVal	=	statusReasonDAO.getStatusReason(statusReasonDOB);
							
							if(!"No StatusReasons".equalsIgnoreCase(returnVal)&&!"Invalidate".equalsIgnoreCase(operation)&&!"View".equalsIgnoreCase(operation)){
								
								returnVal = "T".equalsIgnoreCase(statusReasonDOB.getInvalid())?"No StatusReasons":"";
								
							}
							
							if("No StatusReasons".equalsIgnoreCase(returnVal)){
							       errorMessageObject = new ErrorMessage("There Is No Specified Status Reason: "+statusReasonDOB.getStatusReason(),"etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation); 
							        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
							        keyValueList.add(new KeyValue("Operation",operation)); 	
							        errorMessageObject.setKeyValueList(keyValueList);
							        request.setAttribute("ErrorMessage",errorMessageObject);
							        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
							}
							else{
								logger.info("id..."+statusReasonDOB.getId());
								logger.info("dstatusReason..."+statusReasonDOB.getStatusReason());
								logger.info("type..."+statusReasonDOB.getInvalid());
								
							request.getSession().setAttribute("statusReasonDOB", statusReasonDOB);
							doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonDetails.jsp?Operation="+operation);
							}
							
					}else{
						doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation+"&errMsg=Status Reason is Empty");
					}
						
		}
		 
	     else if(("Modify".equalsIgnoreCase(operation)  && "Details".equalsIgnoreCase(subOperation))) 
			{
	    	 String	status	="";
	    	 statusReasonDOB.setStatusReason(request.getParameter("statusReason"));
	    	 statusReasonDOB.setId(Integer.parseInt(request.getParameter("id")));
	    	 statusReasonDOB.setInvalid("F");
	    	 statusReasonDOB.setOperation(operation);
	    	
	    	 
	    	 status = statusReasonDAO.modifyStatusReason(statusReasonDOB);
			       errorMessageObject = new ErrorMessage(status ,"etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
	
			}
	     else if("View".equalsIgnoreCase(operation) && ("Details".equalsIgnoreCase(subOperation) ))
			{
				doFileDispatch(request, response, "etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation=View");
			}
		 
	     else if(("Delete".equalsIgnoreCase(operation)  && "Details".equalsIgnoreCase(subOperation))) 
			{
	    	 statusReasonDOB.setStatusReason(request.getParameter("statusReason"));
	    	 statusReasonDOB.setId(Integer.parseInt(request.getParameter("id")));
	    	 String status = statusReasonDAO.deleteStatusReason(statusReasonDOB);
			       errorMessageObject = new ErrorMessage(status,"etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");

			}
		 
	     else if(("Invalidate".equalsIgnoreCase(operation)  && "Details".equalsIgnoreCase(subOperation))) 
			{
	    	 String invalid = request.getParameter("invalid");
	    	 String status   = "";
	    	 
	    	 statusReasonDOB.setStatusReason(request.getParameter("statusReason"));
	    	 statusReasonDOB.setId(Integer.parseInt(request.getParameter("id")));
	    	 statusReasonDOB.setInvalid(invalid);
	    	 statusReasonDOB.setOperation(operation);
	    	 
	    	 status = statusReasonDAO.modifyStatusReason(statusReasonDOB);
			       errorMessageObject = new ErrorMessage(status,"etrans/QMSQuoteStatusReasonMasterEnterId.jsp?Operation="+operation); 
			        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			        keyValueList.add(new KeyValue("Operation",operation)); 	
			        errorMessageObject.setKeyValueList(keyValueList);
			        request.setAttribute("ErrorMessage",errorMessageObject);
			        doFileDispatch(request,response,"ESupplyErrorPage.jsp");
	
			}
		
		   
		} catch (Exception e) {
			e.printStackTrace();
		      errorMessageObject = new ErrorMessage("Error While Processing","QMSQuoteStatusReasonController?Operation="+operation); 
		        keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
		        keyValueList.add(new KeyValue("Operation",operation)); 	
		  
		        errorMessageObject.setKeyValueList(keyValueList);
		        request.setAttribute("ErrorMessage",errorMessageObject);
		        try {
					doFileDispatch(request,response,"ESupplyErrorPage.jsp");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ServletException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 

		} 
		
	}
	
	public void doFileDispatch(HttpServletRequest request, HttpServletResponse response, String forwardFile)throws IOException, ServletException
	{
		try
		{
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			logger.error(FILE_NAME+ " [doFileDispatch() "+ " Exception in forwarding ---> "+ ex.toString());
			ex.printStackTrace();
		}
	}
	

}

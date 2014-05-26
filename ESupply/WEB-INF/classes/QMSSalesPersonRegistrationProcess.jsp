<!--
%
%	Programme Name : QMSSalesPersonRegistrationProcess.jsp.
%	Module Name    : QMSSetup.
%	Task Name      : SalesPersonRegistration.
%	Sub Task Name  : for all the operations processing page.
%	Author		   : Rama Krishna.Y
%	Date Started   : 16-06-2005
%	Date Ended     : 
%	Modified Date  : 
%	Description    :
%	Methods		   :
%
-->


<%@ page import = "org.apache.log4j.Logger,
                   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList,
				   com.qms.setup.ejb.sls.QMSSetUpSession,
				   com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				   com.qms.setup.java.SalesPersonRegistrationDOB,
				   javax.naming.InitialContext"%>
                   
<jsp:useBean id="salesPersonRegistrationDOB" class="com.qms.setup.java.SalesPersonRegistrationDOB" scope="request" />
<jsp:setProperty name="salesPersonRegistrationDOB" property="*"/>

<jsp:useBean id="loginBean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%! 
  private static Logger logger = null;
  public static final String FILE_NAME= "QMSSalesPersonRegistrationProcess.jsp"; %>

<%   
      logger  = Logger.getLogger(FILE_NAME);
       QMSSetUpSessionHome             home			=           null;
	   QMSSetUpSession                 remote		=           null;
	   InitialContext                  iCtxt		=           null;
       String                          operation	=           request.getParameter("Operation");                       
	   ArrayList                       keyValueList =           null;
	   ErrorMessage                    errorMessage =           null;
	   SalesPersonRegistrationDOB      dob          =           null;

	   
      // Logger.info(FILE_NAME,"Operation  "+operation);
	   if(operation.equals("Add"))
	   {
		   try
		   {
				   iCtxt                 =         new InitialContext();
				   home                  =         (QMSSetUpSessionHome) iCtxt.lookup("QMSSetUpSessionBean");
				   remote                =         (QMSSetUpSession)     home.create(); 
				   String message        =         remote.insertSalesPersonDetails(salesPersonRegistrationDOB);
				   keyValueList          =         new ArrayList();
				   errorMessage          =         new ErrorMessage(message,"QMSSalesPersonRegistrationAdd.jsp?Operation=Add");
				   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				   keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				   errorMessage.setKeyValueList(keyValueList);
				   request.setAttribute("ErrorMessage",errorMessage);
				   session.setAttribute("NextNavigation","QMSSalesPersonRegistrationAdd.jsp?Operation=Add"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
		   catch(Exception e)
		   {
			    //Logger.error(FILE_NAME,"Error while Adding the record"+e.toString());
          logger.error(FILE_NAME+"Error while Adding the record"+e.toString());
				keyValueList   =  new ArrayList();
				errorMessage   =  new ErrorMessage("Error while Adding the record","QMSSalesPersonRegistrationAdd.jsp?Operation=Add");
				keyValueList.add(new KeyValue("ErrorCode","ERR"));
				errorMessage.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","QMSSalesPersonRegistrationAdd.jsp?Operation=Add"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
	   }
	   if("Delete".equals(operation))
	   {
		   try
		   {
			   iCtxt                 =         new InitialContext();
			   home                  =         (QMSSetUpSessionHome) iCtxt.lookup("QMSSetUpSessionBean");
			   remote                =         (QMSSetUpSession)     home.create(); 
			   boolean flag                  =         remote.removeSalesPersonDetails(salesPersonRegistrationDOB.getSalesPersonCode());
			   //Logger.info(FILE_NAME,"flag   "+flag);
			   keyValueList          =         new ArrayList();
			   errorMessage          =         new ErrorMessage("Record with Salesperson Code "+salesPersonRegistrationDOB.getSalesPersonCode()+" is deleted successfully","QMSSalesPersonRegistrationEnterId.jsp?Operation=Delete");
			   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			  errorMessage.setKeyValueList(keyValueList);
			  request.setAttribute("ErrorMessage",errorMessage);
			  session.setAttribute("NextNavigation","QMSSalesPersonRegistrationEnterId.jsp?Operation=Delete"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   //Logger.error(FILE_NAME,"Error while removing the record"+e.toString());
         logger.error(FILE_NAME+"Error while removing the record"+e.toString());
			   keyValueList   =  new ArrayList();
			   errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationEnterId.jsp?Operation=Delete");
			   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			   keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			   errorMessage.setKeyValueList(keyValueList);
			
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
	   }
	   if("Modify".equals(operation))
	   {
		   try
		   {
			    //System.out.println("salesPersonRegistrationDOB  "+request.getParameter("dateOfJoining"));
			   iCtxt                 =         new InitialContext();
			   home                  =         (QMSSetUpSessionHome) iCtxt.lookup("QMSSetUpSessionBean");
			   remote                =         (QMSSetUpSession)     home.create(); 
			   boolean flag                  =         remote.updateSalesPersonDetails(salesPersonRegistrationDOB);
			   keyValueList          =         new ArrayList();
			   errorMessage          =         new ErrorMessage("Record with Salesperson Code "+salesPersonRegistrationDOB.getSalesPersonCode()+" is modified successfully","QMSSalesPersonRegistrationEnterId.jsp?Operation=Modify");
			   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			  errorMessage.setKeyValueList(keyValueList);
			  request.setAttribute("ErrorMessage",errorMessage);
			  session.setAttribute("NextNavigation","QMSSalesPersonRegistrationEnterId.jsp?Operation=Modify"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
		   catch(Exception e)
		   {e.printStackTrace();
			   //Logger.error(FILE_NAME,"Error while removing the record"+e.toString());
         logger.error(FILE_NAME+"Error while removing the record"+e.toString());
			          keyValueList   =  new ArrayList();
				    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationEnterId.jsp?Operation=Modify");
				 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				 errorMessage.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessage); 
				 session.setAttribute("NextNavigation","QMSSalesPersonRegistrationEnterId.jsp?Operation=Modify"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
	   }
	   if("Invalidate".equals(operation))
	   {
		   try
		   {
			    SalesPersonRegistrationDOB  tempDOB  =  null;				
				String[] check         =         request.getParameterValues("check");				
				ArrayList sprList      =         (ArrayList)session.getAttribute("sprList");	
				String checkBoxValue[]=   request.getParameterValues("checkBoxValue");
				ArrayList newList      =          new ArrayList();				
				session.removeAttribute("sprList");
				
				for(int i=0;i<checkBoxValue.length;i++)
			    {   dob      =  (SalesPersonRegistrationDOB)sprList.get(i);
				    tempDOB  =  new SalesPersonRegistrationDOB();
					
					if("true".equals(checkBoxValue[i])){
						tempDOB.setInvalidate("T");
						tempDOB.setSalesPersonCode(dob.getSalesPersonCode());						
					}
					else
					{
						if("T".equals(dob.getInvalidate()))
						{
							tempDOB.setInvalidate("F");
						    tempDOB.setSalesPersonCode(dob.getSalesPersonCode());								
						}
					}

					newList.add(tempDOB);
			    }
			   iCtxt                 =         new InitialContext();
			   home                  =         (QMSSetUpSessionHome) iCtxt.lookup("QMSSetUpSessionBean");
			   remote                =         (QMSSetUpSession)     home.create(); 
			   boolean flag          =         remote.invalidateSalesPersonDetails(newList);
			   keyValueList          =         new ArrayList();
			   errorMessage          =         new ErrorMessage("Record(s) successfully invalidated ","QMSSalesPersonRegistrationViewAll.jsp?Operation=Invalidate");
			   		  
			   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			  errorMessage.setKeyValueList(keyValueList);
			  request.setAttribute("ErrorMessage",errorMessage); 
			   session.setAttribute("NextNavigation","QMSSalesPersonRegistrationViewAll.jsp?Operation=Invalidate"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   //Logger.error(FILE_NAME,"Error while invalidating the record"+e.toString());
         logger.error(FILE_NAME+"Error while invalidating the record"+e.toString());
			          keyValueList   =  new ArrayList();
				    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationViewAll.jsp?Operation=Invalidate");
				keyValueList.add(new KeyValue("ErrorCode","ERR"));
				keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			    errorMessage.setKeyValueList(keyValueList);
			    request.setAttribute("ErrorMessage",errorMessage); 
			    session.setAttribute("NextNavigation","QMSSalesPersonRegistrationViewAll.jsp?Operation=Invalidate"); 
%>
           <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
		   }
	   }
       
%>
<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*
*	Programme Name	:ETCHOCompanyRegistrationProcess.jsp.
*	Module    Name  :ETrans.
*	Task			:HO CompanyRegistration.
*	Sub Task		:To update or delete the details of company.
*	Author Name		:Raghavender.G.
*   Date Started    :
*   Date Completed  :
*   Date Modified   : Sept 12,2001.By Ratan K.M.
*	Description     :
*   Method's Summary:
*/
%>
		<%@ page import ="	javax.naming.InitialContext,
							java.util.Vector,
							org.apache.log4j.Logger,					
							com.qms.setup.ejb.sls.SetUpSession,
                            com.qms.setup.ejb.sls.SetUpSessionHome,
							com.foursoft.etrans.common.util.ejb.sls.OIDSession,
							com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome"%>
							
<jsp:useBean id ="CompanyObj" class= "com.foursoft.etrans.setup.company.bean.HORegistrationJspBean" scope ="request"/>
<jsp:setProperty name="CompanyObj" property="*"/>
<jsp:useBean id ="AddressObj" class= "com.foursoft.etrans.common.bean.Address" scope ="request"/>
<jsp:setProperty name = "AddressObj" property = "*" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCHOCompanyRegistrationProcess.jsp ";
%>

<%
/*
* This file is invoked from ETCHOCompanyRegistrationView.jsp. It is used to update or delete the details of company .
* This file interacts with ETransHOSuperSessionBean.
*
* @version 	1.00 19 01 2001
* @author 	Raghavender.G
*/
  logger  = Logger.getLogger(FILE_NAME);	
     String strParam = request.getParameter("Operation");
	 String login= null;
try
 {
     InitialContext initial = new InitialContext();
     SetUpSessionHome slh = ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
     SetUpSession sl	   = (SetUpSession) slh.create();
     int addId = 0;
	 if(strParam.equals("Add"))
	 {
		 String companyId = request.getParameter("companyId");
		 login = loginbean.getTerminalId();
	  	 
	  	 if( sl.isHORegistrationCompanyExists(companyId))
		 {
	
			 String errorMessage = "Record already exists with CompanyId : "+companyId; 
			 session.setAttribute("ErrorCode","RAE");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCHOCompanyRegistrationAdd.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		}
		else
		{
				
           OIDSessionHome oidHome = (OIDSessionHome)initial.lookup("OIDSessionBean");
		   OIDSession oidRemote = (OIDSession) oidHome.create();	
		   int i = oidRemote.getAddressOID();
		   CompanyObj.setAddressId(i);  
	       String dayLight1="";
      	   dayLight1 = "N";
	 	   CompanyObj.setDayLightSavings(dayLight1);
			CompanyObj.setHCurrency(request.getParameter("currencyId"));
    	   sl.addHORegistrationDetails(CompanyObj,AddressObj,loginbean);
   
	     	 String errorMessage = "Record successfully added with CompanyId : "+CompanyObj.getCompanyId(); 
			 session.setAttribute("ErrorCode","RSI");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCHOCompanyRegistrationAdd.jsp");
		}
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	 }
	 if(strParam.equals("Modify"))
	   {
       	    addId = Integer.parseInt( request.getParameter("addressId"));
	        String dayLight = request.getParameter("dayLightSavings");
       		String dayLight1="Y";
       		CompanyObj.setDayLightSavings(dayLight1);
			CompanyObj.setHCurrency(request.getParameter("currencyId"));
		    int companyAddressId = CompanyObj.getAddressId();
	        boolean flag = sl.updateHORegistrationDetails(CompanyObj,loginbean,addId,AddressObj);
	      
	      if( flag )
	        {
				 String errorMessage = "Record successfully modified with CompanyId : " + CompanyObj.getCompanyId();
				 session.setAttribute("ErrorCode","RSM");
				 session.setAttribute("ErrorMessage",errorMessage);
			     session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp?Operation="+strParam);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%    			   
	        }
		      else
		      {
				 String errorMessage = "Error while modifying Company Registration : " +CompanyObj.getCompanyId();
				 session.setAttribute("ErrorCode","ERR");
				 session.setAttribute("ErrorMessage",errorMessage);
			     session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp?Operation="+strParam);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		      }
	    }
	    else if(strParam.equals("Delete"))
	    {
	      	
		    String first	=(String)session.getAttribute("first");
			if(!((first!=null)&&(first.equalsIgnoreCase("Second")) ))
			{
			
				String errorMessage = "You are using browser back button.Don't use that button .";
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			}	
			session.removeAttribute("first");
			
	      	int companyAddressId = CompanyObj.getAddressId();
	      	boolean flag 		 = sl.deleteHORegistrationDetails(CompanyObj.getCompanyId(),loginbean,companyAddressId);
		    if( flag )
	        {
			 String errorMessage = "Record successfully deleted with CompanyId : "+CompanyObj.getCompanyId();
			 session.setAttribute("ErrorCode","RSD");
			 session.setAttribute("ErrorMessage",errorMessage);
		     session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp?Operation="+strParam);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	        }
		      else
		      {
				 String errorMessage ="You are not allowed to delete this CompanyId : "+CompanyObj.getCompanyId()+ ", this Id is under usage"; 
				 session.setAttribute("ErrorCode","ERR");
				 session.setAttribute("ErrorMessage",errorMessage);
			     session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp?Operation="+strParam);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		      }
		  }
 }
 catch(Exception exp)
 {
	 String errorMessage = "Unable to "+strParam+" the record";
	 session.setAttribute("ErrorCode","ERR");
	 session.setAttribute("ErrorMessage",errorMessage);
	 if(strParam.equals("Add"))
		session.setAttribute("NextNavigation","ETCHOCompanyRegistrationAdd.jsp");
	 else
	    session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp?Operation="+strParam);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
 }
%>

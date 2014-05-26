<%--
% 
% Copyright (c) 1999-2006 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*	Programme Name : CurrencyServletJSP.
*	Module Name    : Enterprise Setup.
*	Task Name      : Company Master
*	Sub Task Name  : 
*	Author		   : Yuvraj Waghray
*	Date Started   : Sept 20, 2006
*	Date Ended     : Sept 20, 2006.
*	Modified Date  : 
*	Description    : This JSP is used as a servlet to fetch Currency Id when a Country Id is entered in Company Add Screen. (For AJAX 						 Implementation).
*	Methods		   :
*/
%>

<%@ page import = "org.apache.log4j.Logger,
				   javax.naming.InitialContext,
				   com.qms.setup.ejb.sls.SetUpSession,
                   com.qms.setup.ejb.sls.SetUpSessionHome "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
%>
<%
		String FILE_NAME	=	"ETCCurrency.jsp";
		logger  = Logger.getLogger(FILE_NAME);
		String countryId = request.getParameter("countryId");
		String currencyId = null;
		response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");

try
 {
     InitialContext initial = new InitialContext();
     SetUpSessionHome home	  = ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
     SetUpSession	  remote  = (SetUpSession) home.create();
	 
	 if(countryId!=null && countryId.trim().length()!=0)
	  {
			currencyId		=	remote.getCurrencyId(countryId);
			out.print(currencyId);
	  }
	  else
	 {
		  out.print("QMS0");//Country Id Not Entered.
	 }
 }
 catch(Exception exp)
 {
	 currencyId = null;
	 logger.error("Error while AJAXing Cuurency Id "+exp);
	 exp.printStackTrace();
	 out.print("QMS-1");//DB Error	 
 }
%>

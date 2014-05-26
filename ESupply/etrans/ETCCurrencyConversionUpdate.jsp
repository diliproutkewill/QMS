<%
/*
	Program Name	:ETACurrencyConversionUpdate.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:Mod
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:July 1,2003 by Ramesh Kumar.P
	Description		:This file main purpose is to  using Lov from the database.
*/
%>

<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector, 
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"
					
%>

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyConversionUpdate.jsp ";
%>
<%
/**
* This file is invoked on  submission of CurrencyConversionAdd.jsp file.This file is used to submit the values entered in
* respective fields .If conversion factor doesn't exist for the particular currency Ids entered than ,user is acknowledged with successful
*  message else error message is displayed.
* This file will interacts with SetUpSessionBean and then calls the method setCurrencyConversionDetails( currencyConversion )
* which inturn retrive the details.These details are then set to the respective varaibles through Object CurrencyConversionObj.
**/
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String baseCurrency			=	request.getParameter("baseCurrency");
	String[] sltdCurrency		=	request.getParameterValues("sltdCurrency");
	String[] BuyRate			=	request.getParameterValues("BuyRate") ;
	String[] SellRate			=	request.getParameterValues("SellRate") ;
	String[] SpecifiedRate		=	request.getParameterValues("SpecifiedRate") ;
	String radioChkd			=	request.getParameter("R1");
	String	operation			=	null;
	String	msg					=	null;	
	
	try
	{
		operation								=	request.getParameter("Operation");
		InitialContext ictx						=	new InitialContext();
		SetUpSessionHome home		=	null;
		SetUpSession     remote	=	null;
		StringBuffer errorMessage				=	new StringBuffer();
		home		=	(SetUpSessionHome)ictx.lookup("SetUpSessionBean");
		remote		=	(SetUpSession)home.create();
		if(operation.equals("Modify"))
		{		
			msg = "modifying";
			errorMessage.append("Currency Conversion successfully modified for following Currencies : \n\n"); 
			boolean flag	=	remote.updateCurrencyConversion(baseCurrency,sltdCurrency,BuyRate,SellRate,SpecifiedRate,radioChkd);	
			if(flag) 
			{
				for(int k = 0;k < sltdCurrency.length;k++)
				{
					errorMessage.append("Base Currency : " + baseCurrency+ " and Selected Currency : "+sltdCurrency[k]+"\n");
				}
				session.setAttribute("Operation", "Modify");
				session.setAttribute("ErrorCode","RSM");
				session.setAttribute("ErrorMessage",errorMessage.toString());
				session.setAttribute("NextNavigation","ETCCurrencyConversionAdd.jsp");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />	 
<%	
				return ;
			}
			else
			{
				String errorMessage1 ="Unable to modify the record with Base Currency: "+baseCurrency;
				session.setAttribute("Operation", "Modify");
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage1);
				session.setAttribute("NextNavigation","ETCCurrencyConversionAdd.jsp");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%	
				return ;
			}
		}//end of if
		else if(operation.equals("Add"))
		{
			errorMessage.append("Currency Conversion successfully added for following Currencies : \n\n"); 
			msg = "adding";
			boolean flag  = remote.addConversionFactor(baseCurrency,sltdCurrency,BuyRate,SellRate,SpecifiedRate,radioChkd);	
			for(int k = 0;k < sltdCurrency.length; k++)
			{
				if(flag) 
				{
					errorMessage.append("Base Currency : " + baseCurrency+ " and Selected Currency : "+sltdCurrency[k]+"\n");
				}
			}
				session.setAttribute("ErrorCode","RSI");
				session.setAttribute("ErrorMessage",errorMessage.toString());
				session.setAttribute("Operation","Add");
				session.setAttribute("NextNavigation","ETCCurrencyConversionAdd.jsp");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
				return;
		}
	}//end of try block
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"CurrencyConversionUpdate.jsp : Exception : " , e.toString());	
    logger.error(FILE_NAME+"CurrencyConversionUpdate.jsp : Exception : " + e.toString());	
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage","Error while "+msg+" the record/records .");
		session.setAttribute("Operation",operation);
		session.setAttribute("NextNavigation","ETCCurrencyConversionAdd.jsp");
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>
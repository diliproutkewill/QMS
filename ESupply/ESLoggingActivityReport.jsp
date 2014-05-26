<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 %
 --%>
<%--
 % File			: ESLoggingActivityReport.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to display the Usage Activities View filtered on User
 % 
 % author		: Madhu. P
 % date			: 10-01-2002
--%>
<%@ page import= "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,com.foursoft.esupply.common.java.FoursoftWebConfig, com.foursoft.esupply.common.exception.FoursoftException, com.foursoft.esupply.common.java.ReportFormatter, com.foursoft.esupply.common.java.UserLogVOB, org.apache.log4j.Logger, com.foursoft.esupply.common.java.KeyValue, com.foursoft.esupply.delegate.UserRoleDelegate, com.foursoft.esupply.common.java.ErrorMessage, java.sql.Timestamp, java.text.DateFormat, java.util.Locale, java.util.ArrayList,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="dateUtility"  class="com.foursoft.esupply.common.util.ESupplyDateUtility" scope="session"/>
<%!
  private static Logger logger = null;
	String fileName		= "ESLoggingActivityReport.jsp";
				
%>
<%
    logger  = Logger.getLogger(fileName);
	  String  language = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
	
	String actionPage = "";
	UserRoleDelegate userDelegate = null;
	String locationId		= "";
	String userId			= "";
	String fromDate			= "";
	String toDate			= "";
	String errorMessage		= "";
	String reportType		= "";
	String accessType		= "";
	String openFormat		= "";
	String userDateFormat	=	loginbean.getUserPreferences().getDateFormat();
	String UIName			= "";

	locationId	= request.getParameter("locationId");
	userId		= request.getParameter("userId");
	fromDate	= request.getParameter("fromDate");
	toDate		= request.getParameter("toDate");
	reportType	= request.getParameter("reportType");
	accessType	= request.getParameter("accessType");
	openFormat	= request.getParameter("openFormat");
		
	String formatStr="";
	
	//System.out.println("openFormat : "+openFormat);
	if(openFormat.equals("xls")){
	formatStr="XL Sheet Generated.";
	}

	
	DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK);
	
    
    try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        ErrorMessage errMsg = null;
        errorMessage = "Error in Lookup "+exp.getErrorCode();
        if (reportType.equalsIgnoreCase("SINGLE"))
            errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp?UIName=View",exp.getErrorCode(),exp.getComponentDetails());
        else
            errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp?UIName=ViewAll",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }

	//Timestamp	fromDateTimestamp	= new Timestamp(df.parse(fromDate).getTime() );
	if (userDateFormat == null || userDateFormat.equals(""))
		userDateFormat = "DD/MM/YY";
	


	Timestamp	fromDateTimestamp	=	dateUtility.getTimestampWithTime(userDateFormat,fromDate,null);
	
	long l = 0L;

	l = 23*60*60*1000 + 59*60*1000 + 59*1000;
	//Timestamp	toDateTimestamp	= new Timestamp(df.parse(toDate).getTime() + l );
	Timestamp	toDateTimestamp	=	dateUtility.getTimestampWithTime(userDateFormat,toDate,"23:59:59");
	//System.out.println("Later Time : "+toDateTimestamp);	
	//Logger.info(fileName, "Location Id : "+locationId+" User Id : "+userId+" From Date : "+fromDateTimestamp+" TO Date : "+toDateTimestamp );
	
	UserLogVOB							userLogVOB			= null;
	ArrayList						userLogList			= new ArrayList();
	boolean status										= false;
	//String errorMessage									= null;
	ErrorMessage	errMsg	= null;
			
		if(reportType.equals("SINGLE"))
		{
			
			try{
			//status = userDelegate.isLocationIdExists(locationId,accessType);
			 
				
					status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
				
			
			//status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId()); 
			userDelegate.getUserInformation(locationId, userId);
			userLogList	= userDelegate.getUserLogDetails(locationId, userId, fromDateTimestamp, toDateTimestamp);

			
			//userLogList	= remote.getUserLogDetails(locationId, userId, fromDateTimestamp, toDateTimestamp);
			actionPage = "ESLoggingReportView.jsp";		
			session.setAttribute("locationId",locationId);
			session.setAttribute("userId",userId);	
			UIName="View";
			}
			catch(FoursoftException fe)
			{
				
				actionPage = "ESupplyMessagePage.jsp";
				//Logger.warning(fileName, fe.getMessage());
        logger.warn(fileName+ fe.getMessage());
				if (fe.getMessage()!= null)
				{
						if(!status)
						{
						errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
						}
						else
						{
						errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100064")+"'"+userId+"' "+((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100065")+" '"+locationId+"'";
						}
				errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp",fe.getErrorCode(),fe.getComponentDetails());
				ArrayList keyVlaueList = new ArrayList();
				keyVlaueList.add(new KeyValue("UIName", "View") );
				keyVlaueList.add(new KeyValue("action", reportType) );
				errMsg.setKeyValueList(keyVlaueList);
				request.setAttribute("errorMessage", errMsg); 	
				}
			}	
		}
		else if(reportType.equals("MULTIPLE"))
		{
			
			try
			{
			//status = userDelegate.isLocationIdExists(locationId,accessType);
			

					status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId());
							
			//status = userDelegate.IsLocIdExists(locationId,loginbean.getAccessType(),accessType,loginbean.getLocationId()); 
			userLogList	= userDelegate.getUserLogViewAllDetails(locationId, fromDateTimestamp, toDateTimestamp);

			actionPage = "ESLoggingActivityViewAllReport.jsp";	
			session.setAttribute("locationId",locationId);
			UIName="ViewAll";
			}
			catch(FoursoftException fe)
			{
				//errorMessage = bundle.getBundle().getString("100072");
				//Logger.error(fileName,"Error Occured in Retrieving User's Logging activity Report : ",fe );
        logger.error(fileName+"Error Occured in Retrieving User's Logging activity Report : "+fe );
				actionPage = "ESupplyMessagePage.jsp";
						//Logger.warning(fileName, fe.getMessage());
            logger.warn(fileName+ fe.getMessage());
						if (fe.getMessage()!= null)
						{
								if(!status)
								{
								errorMessage =((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100623")+"\n"+" '"+locationId+"'";
								}
						errMsg = new ErrorMessage(errorMessage, "ESLoggingEnterId.jsp",fe.getErrorCode(),fe.getComponentDetails());
						ArrayList keyVlaueList = new ArrayList();
						keyVlaueList.add(new KeyValue("UIName", "ViewAll") );
						keyVlaueList.add(new KeyValue("action", reportType) );
						errMsg.setKeyValueList(keyVlaueList);
						request.setAttribute("errorMessage", errMsg); 	
						}
			}
							
		}

	
%>
<%
	session.removeAttribute("report");
	session.removeAttribute("openFormat");
	ReportFormatter formatter = new ReportFormatter(userLogList, 50, 10);
	session.setAttribute("report", formatter);
	session.setAttribute("openFormat", openFormat);
%>
<%if((!actionPage.equals("ESupplyMessagePage.jsp"))&&(openFormat.equals("xls"))){%>
<html>
<script>
function sub(){
window.location.href="<%= actionPage %>";
}
function fun(){
window.location.href="ESLoggingEnterId.jsp?UIName=<%=UIName%>";

}
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body onload=sub(); >
<form method="post" >
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" > 
      <td>
        <table width="100%" class='formlabel' border="0">
          <tr valign="top" class='formlabel'> 
            <td class="formlabel" colspan=2 ><fmt:message key="100312" bundle="${lang}"/></td>
			<td class="formlabel" colspan=2 align="right"></td>
          </tr>	
		</table>
	
	    <br>
	
	    <table width="100%" cellpadding="1" cellspacing="1" border="0">  
          <tr valign="top" class='formdata'> 
			<td>
				<%=formatStr%>
            </td>
          </tr>		  
          <tr valign="top"> 
            <td width="50%" align="right">
				<input type=button name="continue" value='<fmt:message key="7777" bundle="${lang}"/>' onClick='fun()' class='input'> 
            </td>
          </tr>
        </table>
	  <td>	 
    </tr>
  </table>
</form>
</body>
</html>
<%}
else if(!openFormat.equals("xls")){
%>
<jsp:forward page="<%= actionPage %>" />
<%
}
else{
%>
<jsp:forward page="ESupplyMessagePage.jsp" />
<%
}
%>
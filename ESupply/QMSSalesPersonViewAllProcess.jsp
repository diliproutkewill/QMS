<%
/*
	Program Name	:QMSSalesPersonViewAllProcess.jsp
	Module Name		:QMSSetup
	Task			:QMSSalesPersonViewAllProcess Master
	Sub Task		:
	Author Name		:RamaKrishna Y
	Date Started	:June 28,2005
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>                                                                     
<%@ page import = "java.util.ArrayList,
                   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.*,
				   com.qms.setup.ejb.sls.QMSSetUpSession,
				   com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				   com.qms.setup.java.SalesPersonRegistrationDOB,
				   javax.naming.InitialContext,				   
				   java.util.StringTokenizer"%>

<jsp:useBean id="loginBean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSSalesPersonViewAllProcess.jsp ";
%>

<% 


      logger  = Logger.getLogger(FILE_NAME);
	   		String locationId						=	"";
			String terminalId						=	"";
			String salesPersonCode					=	"";
			String salesPersonName					=	"";
			String designation						=	"";						 
			String designationLevel					=	"";
			String dateOfJoining					=	"";				
			String reportingCode					=	"";				
			String reportingOffName					=	"";  					
			String escalationLevel					=	"";					
			String reportingsDesignation			=	"";		
			String allotedTime						=	"";						
			String remarks							=	"";	
			QMSSetUpSessionHome		home			=	null;	  		 			
			QMSSetUpSession			remote			=	null;			 			
			ArrayList dobList  			            =	null;			 			
			String values							=	""; 						
			String operation						=	request.getParameter("Operation");
			int rows								=	0;								
			int cols								=	0;								
			int firstIndex							=	0,lastIndex=0,midIndex=0;		 
			String heading							=	null,str1=null,str2=null,str3=null; 
			int headingSize							=	0,len=0;						 					
			String	message							=	" No records not found for this TerminalId  " ;					
			ErrorMessage	errorMessageObject		=	null;
			ArrayList		keyValueList			=	new ArrayList();
			SalesPersonRegistrationDOB   dob        =   null;
 try
   { 


     if(request.getParameter("locationId")!=null && request.getParameter("locationId").equals("0") )
     {
	
        locationId  = "LocationId ";
		
     }
	 if(request.getParameter("terminalId")!=null && request.getParameter("terminalId").equals("0"))
	 {	
		terminalId  = "TerminalId ";      
	 
	 }
	 if(request.getParameter("salesPersonCode")!=null && request.getParameter("salesPersonCode").equals("0"))
	 {	
	   salesPersonCode = "SalesPersonCode ";
	
	 } 
	  if(request.getParameter("salesPersonName")!=null &&  request.getParameter("salesPersonName").equals("0"))
	 {
		salesPersonName = "SalesPersonName ";
	
	 }   
	 if(request.getParameter("designation")!=null && request.getParameter("designation").equals("0"))
	 {	
	   designation   = "Designation ";
	 } 
	 if(request.getParameter("designationLevel")!=null && request.getParameter("designationLevel").equals("0"))
	 {	 
       designationLevel  = "DesignationLevel ";
	 }
	 if(request.getParameter("dateOfJoining")!=null && request.getParameter("dateOfJoining").equals("0"))
	 {	 
       dateOfJoining  = "DateOfJoining ";
	 }
	 if(request.getParameter("reportingCode")!=null && request.getParameter("reportingCode").equals("0"))
	 { 
	   reportingCode    = "ReportingCode ";
	 } 
	
	  if(request.getParameter("reportingOffName")!=null && request.getParameter("reportingOffName").equals("0"))
	 {
		 reportingOffName    = "ReportingOffName ";
	 } 
	 if(request.getParameter("escalationLevel")!=null && request.getParameter("escalationLevel").equals("0"))
	 {	
	   escalationLevel    = "EscalationLevel ";
	 }
	 if(request.getParameter("reportingsDesignation")!=null && request.getParameter("reportingsDesignation").equals("0"))
	 {	 
	   reportingsDesignation  = "ReportingsDesignation ";
	 } 
	 if(request.getParameter("time")!=null && request.getParameter("time").equals("0"))
	 {	
	  allotedTime= "AllotedTime ";
	 }
     if(request.getParameter("remarks")!=null && request.getParameter("remarks").equals("0"))
     {	
      remarks      = "Remarks ";
	 }	 
		values = values + locationId + terminalId + salesPersonName + salesPersonCode + designation + designationLevel + dateOfJoining + reportingCode + reportingOffName + escalationLevel+ reportingsDesignation + allotedTime + remarks  ;
	
    }
	catch(Exception exp)
     {
	    //Logger.error(FILE_NAME,"Exception in DetailedTrackingProcess.jsp : ", exp.toString());
      logger.error(FILE_NAME+"Exception in DetailedTrackingProcess.jsp : "+ exp.toString());
     }

	if((operation.equals("ViewAll") || operation.equals("Invalidate"))&& (!values.equals("")) )
	 {
		try
    	{
	 		 InitialContext initial	=	new InitialContext();			 
			 home				= (QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");			 
			 remote				= (QMSSetUpSession)home.create();			 
			 dobList = remote.getSalesPersonDetails();
			if(dobList != null){
				 rows	=	dobList.size();
				 session.setAttribute("sprList",dobList);
			}			
	if(rows>0)
	{			
%>

			<html>
			<head>
			<title>View All</title>
			<link rel="stylesheet" href="ESFoursoft_css.jsp">
			<style type="text/css">
			.time
			{	
						font-size : 9px;
			}
			</style>
			</head>
<script language='javascript'>
function checkValues()
{
   var checkBox=document.getElementsByName("check");
   var checkBoxValue=document.getElementsByName("checkBoxValue");
   for(m=0;m<checkBox.length;m++)
   {
     if(checkBox[m].checked)
		checkBoxValue[m].value="true";    	 
  }
   
}
</script>
			<body >
			<form method="post" action='QMSSalesPersonRegistrationProcess.jsp'  onSubmit='checkValues()'>
<%
  			  StringTokenizer st1	= new StringTokenizer(values);
			  int tableWidth		= 0;  
		      while(st1.hasMoreTokens())
		      {
			         tableWidth = tableWidth +70;
					 st1.nextToken();	 
		      }    
%>
			<table width='100%' border="0" cellspacing="0" cellpadding="0" >
			<tr bgcolor="#ffffff">
			<td>
			<table width='100%' border="0" cellspacing="1" cellpadding="4">
			<tr valign="top" class='formlabel'> 
			<td  ><font  size="4">Sales Person Registration -<%=operation%></td>
			<td align="right">QS1010432</td>
			</tr>
			</table>
         
         <table width='100%'  border="0" cellspacing="1" cellpadding="4" >
          <tr valign="top" class='formheader'> 
<%
  			  StringTokenizer st	= new StringTokenizer(values);
			  headingSize			= 100;				  
		      while(st.hasMoreTokens())
		      {
				   heading=(String)st.nextElement();
				   cols++;	 
           		   if(heading.equalsIgnoreCase("locationId"))
					   heading = "LocationId";
					if(heading.equalsIgnoreCase("terminalId"))
					   heading = "TerminalId";
				   if(heading.equalsIgnoreCase("salesPersonName"))
					   heading = "SalesPersonName";	
				   if(heading.equalsIgnoreCase("salesPersonCode"))
					   heading = "SalesPersonCode";
				   if(heading.equalsIgnoreCase("designation"))
					   heading = "Designation";	
				   if(heading.equalsIgnoreCase("designationLevel"))				     
					   heading = "DesignationLevel";
					if(heading.equalsIgnoreCase("dateOfJoining"))
						heading = "DateOfJoining";
				   if(heading.equalsIgnoreCase("reportingCode"))
					   heading = "Reporting Off Code";	
				   if(heading.equalsIgnoreCase("reportingOffName"))
					   heading = "Reporting Off Name";
				   if(heading.equalsIgnoreCase("escalationLevel"))
					   heading = "Escalation Level";	
				   if(heading.equalsIgnoreCase("reportingsDesignation"))				       
					   heading = "Reporting Off Designation";
				   if(heading.equalsIgnoreCase("time"))
					   heading = "Time";	
				   if(heading.equalsIgnoreCase("remarks"))
					   heading = "Remarks";
%>
  				  <td  align="center"><font  size="1" ><%=heading%></font>
			      </td>
<%   
		         }
%>
                 <td  align="center"><font  size="1" ></font></td>
			</tr>
			
<%
	String checkInvalidate="";
		  for(int i=0;i<dobList.size();i++)
		  {	
				dob   =  (SalesPersonRegistrationDOB)dobList.get(i);
				if(!("ViewAll".equals(operation) && "T".equals(dob.getInvalidate())) ){
				if("T".equals(dob.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
        
%> 
             <tr valign="top" class='formdata'  align="center">  
			 <%if(request.getParameter("locationId")!=null && request.getParameter("locationId").equals("0") ){%>
					<td ><font  size="1"  ><%=dob.getLocationId()%></td>
			<%}if(request.getParameter("terminalId")!=null && request.getParameter("terminalId").equals("0") ){%>
					<td  ><font  size="1" ><%=dob.getTerminalId()%></font></td>
					<%}if(request.getParameter("salesPersonName")!=null && request.getParameter("salesPersonName").equals("0") ){%>
					<td ><font  size="1" ><%=dob.getSalesPersonName()%></font></td>
					<%}if(request.getParameter("salesPersonCode")!=null && request.getParameter("salesPersonCode").equals("0") ){%>
					<td > <font  size="1" ><%=dob.getSalesPersonCode()%></font></td>
					<%}if(request.getParameter("designation")!=null && request.getParameter("designation").equals("0") ){%>
					<td> <font  size="1"  ><%=dob.getDesignation()%></font></td>
					<%}if(request.getParameter("designationLevel")!=null && request.getParameter("designationLevel").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getLevel()%></font></td>
					<%}if(request.getParameter("dateOfJoining")!=null && request.getParameter("dateOfJoining").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getDateOfJoining()%></font></td>
					<%}if(request.getParameter("reportingCode")!=null && request.getParameter("reportingCode").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getRepOffCode()%></font></td>
					<%}if(request.getParameter("reportingOffName")!=null && request.getParameter("reportingOffName").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getRepOffName()%></font></td>
					<%}if(request.getParameter("escalationLevel")!=null && request.getParameter("escalationLevel").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getSuperLevel()%></font></td>
					<%}if(request.getParameter("reportingsDesignation")!=null && request.getParameter("reportingsDesignation").equals("0") ){%>
					<td  > <font  size="1"  ><%=dob.getSuperDesignationId()%></font></td>
					<%}if(request.getParameter("time")!=null && request.getParameter("time").equals("0") ){%>
					<td  > <font  size="1" ><%=dob.getTime()%></font></td>
					<%}if(request.getParameter("remarks")!=null && request.getParameter("remarks").equals("0") ){%>
					<td  > <font  size="1" ><%=dob.getRemarks()%></font></td>
					<%}%>
					 <%if("Invalidate".equals(operation)){%>
					<td  align="center"><font  size="1" ><input type="checkBox" name="check" <%=checkInvalidate%>></font><input type="hidden" name="checkBoxValue"></td>
					<%}%>
             </tr>
<%			}
          } // for loop
%>		  
        <%if("Invalidate".equals(operation)){%>
		<table width='100%'  border="0" cellspacing="1" cellpadding="4"><tr class="formdata" align="right"><td align='right'><input type="submit" name='submit' value='Submit'>
		<input type='hidden' name='Operation' value='<%=operation%>'>
		</td></tr></table>
		<%}%>
        </table>
		</td>
		</tr>
		</table>
		
      </form>
    </body>
  </html> 
<%				

  }
  else
  {
	   	
	    String errorMessage=message;	    
		errorMessageObject = new ErrorMessage(errorMessage,"javascript:window.close()");		 
		keyValueList.add(new KeyValue("ErrorCode","RNF"));
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		session.setAttribute("NextNavigation","QMSSalesPersonRegistrationAdd.jsp?Operation=Add"); 

		
%>
		  <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%	

  }	      
   }catch(Exception exp)
       {exp.printStackTrace();
		   //Logger.error(FILE_NAME,"Exception in last ViewAll Process.jsp", exp.toString());
       logger.error(FILE_NAME+"Exception in last ViewAll Process.jsp"+ exp.toString());
       }
  }
  
%>
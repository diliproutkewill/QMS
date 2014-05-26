
<%
/*	Programme Name : TerminalInvalidate.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : TerminalInvalidate
*	Sub Task Name  : Modify/View/ViewAll/Delete Modules
*	Author		   : K.NARESH KUMAR REDDY
*	Date Started   : 28 June 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.ejb.sls.SetUpSession,
				   com.qms.setup.ejb.sls.SetUpSessionHome,
				   java.util.ArrayList,
				   java.util.StringTokenizer,
				   com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="ServiceLevelInvalidate.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	ServiceLevelJspBean     serviceDOB=null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String					  heading="";
	String str="";
    int count=0;
	String operation="Invalidate";
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote.getInvalidateServiceLevelDetails();
		serviceDOB     = new ServiceLevelJspBean();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCServiceLevelProcess.jsp?Operation=Invalidate";
		}

			if (request.getParameter("P_SERVICEID")==null)
				{
				str+="P_SERVICEID,";
				count++;
				}
			if	(request.getParameter("P_SERVICEDESC")!=null && request.getParameter("P_SERVICEDESC").equals("NO"))
				{
				str+="P_SERVICEDESC,";
				count++;
				}
			if	(request.getParameter("P_REMARKS")!=null && request.getParameter("P_REMARKS").equals("NO"))
				{
				str+="P_REMARKS,";
				count++;
				}
					  
      
%>

<html>
<head>
<title>ServiceLevelInvalidate</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<script language="JavaScript">

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
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>'  >
<!--<table border="0" cellpadding="4" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="6" width="100%" height="21"><font size='3'><b>ServiceLevel- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
           <td width="100" height="1">Service&nbsp;Level&nbsp;Id</td>
            <td width="250" height="1">ServiceLevelDescription&nbsp;</td>
			<td width="100" height="1">Remarks&nbsp;</td>-->
			<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }   
%>
<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>ServiceLevel - <%=operation%></td><td align=right>QS1222676</td></tr></table></td>
		  </tr></table>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {heading = st.nextToken();
			 if(heading.equals("P_SERVICEID"))
			   {
				heading="ServicelevelId";
				
			   }
 			 else if(heading.equals("P_SERVICEDESC"))
			   {
				 heading="SeviceDescription";	
 				
			   }

			  else if(heading.equals("P_REMARKS"))
			   {
				heading="Remarks";	
				
			   }
			   			


%>
      			  <td width ="25%" ><%=heading%>
			      </td>
<%   
		   }
%>
			<%if("Invalidate".equals(operation)){%>
			<td width="100" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						serviceDOB  = (ServiceLevelJspBean)dobList.get(i);

						
		               
					   if("T".equals(serviceDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
		  
            <td ><%=serviceDOB.getServiceLevelId()%></td>
			<% 
				if(str.indexOf("P_SERVICEDESC")>0)
				{
			 %>
            <td ><%=serviceDOB.getServiceLevelDescription()%></td>
			<%
				}
				if(str.indexOf("P_REMARKS")>0)
				{
			%>
            <td><%=serviceDOB.getRemarks()==null?"":serviceDOB.getRemarks()%></td>

				<%}
				  if("Invalidate".equals(operation))
				  {
				%>
			<td ><input type='checkbox' name='check'  <%=checkInvalidate%>><input type="hidden" name="checkBoxValue" value="false"></td>
				<%
				  }
				%>
          </tr> 
		  <%
		
		
				
		 
					}
		  %>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
        <tbody>
		<% 		 session.setAttribute("dobList",dobList);

			 %>
          <tr >
            <td align="right" colspan="6" width="100%">
			<input type="button" value="<< Back" name="Back" onClick="javascript:history.back()" class='input'>
			<input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' "border-style: solid">
			<input type="hidden" name="Operation" value='<%=operation%>'>
            </td>
          </tr>
        </tbody>
      </table>

</body>

</html>
<%}
  catch(Exception e)
  {
	  System.out.println("e:"+e);
%>
			
<%

  }
%>
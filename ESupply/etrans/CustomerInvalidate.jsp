
<%
/*	Programme Name : CustomerInvalidate.jsp.
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
				   com.foursoft.etrans.setup.customer.java.CustomerModel,
				   java.util.ArrayList,
				   java.util.StringTokenizer,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="CountryInvalidate.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	CustomerModel   custDOB=null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation="Invalidate";
	String heading="";
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote. getInvalidateCustomerMasterDetails();
		custDOB     = new CustomerModel();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCustomerRegistrationProcess.jsp?Operation=Invalidate";
		}
		String str="";
	       int count=0;
			if (request.getParameter("CUSTOMERID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="CUSTOMERID,";
				count++;
				}
			if	(request.getParameter("COMPANYNAME")!=null && request.getParameter("COMPANYNAME").equals("0")){
				str+="COMPANYNAME,";
				count++;
				}
			if	(request.getParameter("CONTACTNAME")!=null && request.getParameter("CONTACTNAME").equals("0")){
				str+="CONTACTNAME,";
				count++;
				}
			if	(request.getParameter("REGISTERED")!=null && request.getParameter("REGISTERED").equals("0")){
				str+="REGISTERED,";
				count++;
				}
			if	(request.getParameter("TERMINALID")!=null && request.getParameter("TERMINALID").equals("0")){
				str+="TERMINALID,";
				count++;
				}
			if	(request.getParameter("CITY")!=null && request.getParameter("CITY").equals("0")){
				str+="CITY,";
				count++;
				}
	
      
%>

<html>
<head>
<title>Density Group Code</title>
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
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="DensityGroupCode"  >
<!--<table border="0" cellpadding="4" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="7" width="100%" height="21"><font size='3'><b>Customer- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="100" height="1">CustomerId&nbsp;</td>
            <td width="150" height="1">CompanyName&nbsp;</td>
            <td width="100" height="1">Contact &nbsp;Name&nbsp;</td>
			<td width="175" height="1">Registered&nbsp;</td>
			<td width="150" height="1">TerminalId&nbsp;</td>
			<td width="150" height="1">City&nbsp;</td>-->
			<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }    
%>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("CUSTOMERID"))
				heading="Customer Id";
 			 if(heading.equals("COMPANYNAME"))
				heading="Company Name";	
			 if(heading.equals("CONTACTNAME"))
				heading="ContactName";	
 			 if(heading.equals("REGISTERED"))
				heading="Registered";	
			 if(heading.equals("TERMINALID"))
				heading="TeminalId";	
			 if(heading.equals("CITY"))
				heading="City";	
%>
      			  <td width ="25%" ><%=heading%>
			      </td>
<%   
		   }
			if("Invalidate".equals(operation)){%>
			<td width="100" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						custDOB  = (CustomerModel)dobList.get(i);

						
		               
					   if("T".equals(custDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="100" height="27"><%=custDOB.getCustomerId()%></td>
			<%if(str.indexOf("COMPANYNAME")>0)
			{
			  %>
			<td width="150" height="27"><%=custDOB.getCompanyName()%></td>
			<%
			}
			  if(str.indexOf("CONTACTNAME")>0)
			{
			  %>
			
			<td width="100" height="27"><%=custDOB.getContactName()%></td>
			<%
			}
			if(str.indexOf("REGISTERED")>0)
			{
			  %>
			
            <td width="175" height="27"><%=custDOB.getRegistered()%></td>
			<%
			}
			if(str.indexOf("TERMINALID")>0)
			{
			  %>
			
			<td width="150" height="27"><%=custDOB.getTerminalId()%></td>
			<%
			}
			if(str.indexOf("CITY")>0)
			{
			  %>
			
			<td width="150" height="27"><%=custDOB.getCity()==null?"":custDOB.getCity()%></td>
				<%}
				  if("Invalidate".equals(operation))
				  {
				%>
			<td width="100" height="27"><input type='checkbox' name='check'  <%=checkInvalidate%>><input type="hidden" name="checkBoxValue" value="false"></td>
				<%
				  }
				%>
          </tr> 
		  <%
				
		
		
				
		 
					}
		  %>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="803" bgcolor='#ffe88a'>
        <tbody>
		<% 		 session.setAttribute("dobList",dobList);

			 %>
          <tr >
            <td align="right" colspan="6" width="100%"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' "border-style: solid">
			<input type="hidden" name="Operation" value='<%=operation%>'
            </td>
          </tr>
        </tbody>
      </table>

</body>

</html>
<%}
  catch(Exception e)
  {
	  //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeView.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSDensityGroupCodeView.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSDensityGroupCodeAdd.jsp");
	  keyValueList   =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%

  }
%>
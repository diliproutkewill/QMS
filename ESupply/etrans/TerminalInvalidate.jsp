
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
				   java.util.StringTokenizer,
				   com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean,
				   java.util.ArrayList,
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
	TerminalRegJspBean     terminalDOB=null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation="Invalidate";
	String heading               = null;
   int headingSize              = 0,len=0;
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote.getInvalidateTerminalDetails();
		terminalDOB     = new TerminalRegJspBean();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCTerminalRegistrationProcess.jsp?Operation=Invalidate";
		}
		String str="";
	       int count=0;
			if (request.getParameter("TERMINALID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="TERMINALID,";
				count++;
				}
			if	(request.getParameter("CITY")!=null && request.getParameter("CITY").equals("0")){
				str+="CITY,";
				count++;
				}
			if	(request.getParameter("CONTACTNAME")!=null && request.getParameter("CONTACTNAME").equals("0")){
				str+="CONTACTNAME,";
				count++;
				}
			if	(request.getParameter("DESIGNATION")!=null && request.getParameter("DESIGNATION").equals("0")){
				str+="DESIGNATION,";
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
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="InvalidateTerminal"  >
<!--<table border="0" cellpadding="4" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="6" width="100%" height="21"><font size='3'><b>Terminal- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="100" height="1">TerminalId</td>
            <td width="150" height="1">City &nbsp;</td>
            <td width="100" height="1">Contact &nbsp;Name&nbsp;</td>
			<td width="175" height="1">Designation&nbsp;</td>-->
			      
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
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Terminal Master - <%=operation%></td><td align="right">QS1222665</td></tr></table></td>
		  </tr></table>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF"  >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("TERMINALID"))
				heading="TerminalId";
 			 if(heading.equals("CITY"))
				heading="City";	
			 if(heading.equals("CONTACTNAME"))
				heading="ContactName";	
 			 if(heading.equals("DESIGNATION"))
				heading="Designation";	
%>
      			  <td width ="25%" ><%=heading%>
			      </td>
<%   
		   }
			if("Invalidate".equals(operation)){%>
			<td>Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						terminalDOB  = (TerminalRegJspBean)dobList.get(i);

						
		               
					   if("T".equals(terminalDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td ><%=terminalDOB.getTerminalId()%></td>
			<% if (str.indexOf("CITY")>0)
			{%>
            <td><%=terminalDOB.getCity()%></td>
			<%
			  }
				if(str.indexOf("CONTACTNAME")>0)
			  {
			%>
            <td><%=terminalDOB.getContactName()==null?"":terminalDOB.getContactName()%></td>
			<% 
			} 
				if(str.indexOf("DESIGNATION")>0)
			{
			%>
			<td><%=terminalDOB.getDesignation()==null?"":terminalDOB.getDesignation()%></td>
			<%
			}
				  if("Invalidate".equals(operation))
				  {
				%>
			<td >
			<input type='checkbox' name='check'  <%=checkInvalidate%>>
			<input type="hidden" name="checkBoxValue" value="false"></td>
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
			<input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' "border-style: solid">
			<input type="button" value="<< Back" name="Back" onClick="javascript:history.back()" class='input'>
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
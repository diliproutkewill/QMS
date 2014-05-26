
<%
/*	Programme Name : PortViewAll.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : Modify/View/ViewAll/Delete Modules
*	Author		   : K.NareshKumarReddy.
*	Date Started   : 28 June 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.ejb.sls.SetUpSession,
				   com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean,
				   java.util.ArrayList,
				   java.util.StringTokenizer,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="PortViewAll.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	PortMasterJSPBean		portDOB	=  null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation=request.getParameter("Operation");
	String heading="";
	String str="";
    int count=0;
	try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote.getInvalidatePortMasterDetails();
		portDOB     = new PortMasterJSPBean();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETSPortMasterProcess.jsp?Operation=Invalidate";
		}
		if(operation.equalsIgnoreCase("viewAll"))
		{
			submitLabel="Continue";
			nextNavigation="../ETAShowViewAllReport.jsp?viewParameter=portmaster";
		}
	
		if (request.getParameter("P_PORTID")==null)
		{
			str+="P_PORTID,";
			count++;
			}
		if	(request.getParameter("P_PORTNAME")!=null && request.getParameter("P_PORTNAME").equals("NO")){
			str+="P_PORTNAME,";
			count++;
			}
		if	(request.getParameter("P_COUNTRYID")!=null && request.getParameter("P_COUNTRYID").equals("NO")){
			str+="P_COUNTRYID,";
			count++;
			}
		if	(request.getParameter("P_DESCRIPTION")!=null && request.getParameter("P_DESCRIPTION").equals("NO")){
			str+="P_DESCRIPTION,";
			count++;
		 	}	
	
      
%>

<html>
<head>
<title>Density Group Code</title>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
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
<table border="0" cellpadding="4" cellspacing="0" width="100%" height=""  bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td colspan="7" width="789" height="21"><font size='3'><b>Port- <%=operation.equalsIgnoreCase("viewAll")?"View All":operation%></b></font></td>
			<td>QS1222654</td>
          </tr>
          <!-- <tr class="formheader">
            <td width="267" height="1">Port&nbsp;Id&nbsp;</td>
            <td width="267" height="1">PortName &nbsp;</td>
            <td width="267" height="1">CountryId&nbsp;</td>
			<td width="267" height="1">Description&nbsp;</td> -->
			<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }    
%>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("P_PORTID"))
				heading="Port Id";
 			 if(heading.equals("P_PORTNAME"))
				heading="Port Name";	
			 if(heading.equals("P_COUNTRYID"))
				heading="Country Id";	
 			 if(heading.equals("P_DESCRIPTION"))
				heading="Description";	
%>
      			  <td width ="25%" ><%=heading%>
			      </td>
<%   
		   }if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						portDOB=(PortMasterJSPBean)dobList.get(i);

						
		               
					   if("T".equals(portDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="267" height="27"><%=portDOB.getPortId()%></td>
			<%if(str.indexOf("P_PORTNAME")>0)
			  {
			 %>
            <td width="223" height="27"><%=portDOB.getPortName()%></td>
			<%}
			 if(str.indexOf("P_COUNTRYID")>0)
			{
			%>
            <td width="223" height="27"><%=portDOB.getCountryId()%></td>
			<%}
			if(str.indexOf("P_DESCRIPTION")>0)
			{
			%>
			<td width="267" height="27"><%=portDOB.getDescription()==null?"":portDOB.getDescription()%></td>
				<%
			}
				  if("Invalidate".equals(operation))
				  {
				%>
			<td width="223" height="27"><input type='checkbox' name='check'  <%=checkInvalidate%>><input type="hidden" name="checkBoxValue" value="false"></td>
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
            <td align="right" colspan="7"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' "border-style: solid">
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
	  //Logger.error(FILE_NAME,"Error in PortViewAll.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in PortViewAll.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","PortViewAll.jsp");
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
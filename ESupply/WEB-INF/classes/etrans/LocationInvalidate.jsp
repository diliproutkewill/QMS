
<%
/*	Programme Name : LocationInvalidate.jsp.
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
				   com.foursoft.etrans.setup.location.bean.LocationMasterJspBean,
				   java.util.StringTokenizer,
				   java.util.ArrayList,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;  
  public static final String FILE_NAME="LocationViewAll.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	LocationMasterJspBean		locationDOB	=  null;
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

	
	    dobList     =  remote.getLocationDetails();
		locationDOB     = new LocationMasterJspBean();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETLocationProcess.jsp?operation=Invalidate";
		}
		String str="";
	       int count=0;
			if (request.getParameter("P_LOCATIONID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="P_LOCATIONID,";
				count++;
				}
			if	(request.getParameter("P_LOCATIONNAME")!=null && request.getParameter("P_LOCATIONNAME").equals("NO")){
				str+="P_LOCATIONNAME,";
				count++;
				}
			if	(request.getParameter("P_COUNTRYID")!=null && request.getParameter("P_COUNTRYID").equals("NO")){
				str+="P_COUNTRYID,";
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
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="LocationIvalidate"  >
<!--<table border="0" cellpadding="2" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="7" width="789" height="21"><font size='3'><b>Designation- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="267" height="1">Locationid&nbsp;</td>
            <td width="267" height="1">LocatinName &nbsp;</td>
            <td width="267" height="1">CountryId&nbsp;</td>
			<td width="267" height="1">City&nbsp;</td>
			<td width="267" height="1">ZipCode&nbsp;</td>
			<td width="267" height="1">Shipment&nbsp;Mode&nbsp;</td>-->
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
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Location Master - <%=operation%></td><td align=right>QS1222643</td></tr></table></td>
		  </tr></table>
		  <table width="100%" cellpadding="0" cellspacing="0" >
			<tr valign="top"class='formdata'> 
            <td > Terminal : <%=loginbean.getTerminalId()%></td>
            <td > User : <%=loginbean.getUserId()%></td>
            <td > Date : <%=loginbean.getCurrentDateString()%></td>
          </tr></table>
		 
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("P_LOCATIONID"))
				heading="Location Id";
 			 if(heading.equals("P_LOCATIONNAME"))
				heading="Location Name";	
			 if(heading.equals("P_COUNTRYID"))
				heading="Country Id";	
 			 %>
      			  <td width ="25%" ><%=heading%>
			      </td>
			<%   
		   }
			%>
			<%if("Invalidate".equals(operation)){%>
			<td >Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						locationDOB=(LocationMasterJspBean)dobList.get(i);

						
		               
					   if("T".equals(locationDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td><%=locationDOB.getLocationId()%></td>
			<%if(str.indexOf("P_LOCATIONNAME")>0)
			{
			%>
            <td ><%=locationDOB.getLocationName()%></td>
			<%} if(str.indexOf("P_COUNTRYID")>0)
			{%>
            <td ><%=locationDOB.getCountryId()%></td>
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
            <td align="right" colspan="7">
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
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","");
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
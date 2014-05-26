
<%
/*	Programme Name : CountryInvalidate.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : CountryInvalidate
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
				   com.foursoft.esupply.common.java.CountryMasterDOB,
   				   java.util.StringTokenizer,
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
	CountryMasterDOB			 countryDOB				=  null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";  
	String					  currencyCheck			="";
	String                    code					=  "";
	String operation="Invalidate";
	String str="";
    int count=0;
	String heading               = null;


try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote.getInvalidateCountryDetails();
		countryDOB     = new CountryMasterDOB();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCCountryProcess.jsp?Operation=Invalidate";
		}
			if (request.getParameter("P_COUNTRYID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="P_COUNTRYID,";
				count++;
				}
			if	(request.getParameter("P_COUNTRYNAME")!=null && request.getParameter("P_COUNTRYNAME").equals("NO")){
				str+="P_COUNTRYNAME,";
				count++;
				}
			if	(request.getParameter("P_CURRENCYID")!=null && request.getParameter("P_CURRENCYID").equals("NO")){
				str+="P_CURRENCYID,";
				count++;
				}
			if	(request.getParameter("P_REGION")!=null && request.getParameter("P_REGION").equals("NO")){
				str+="P_REGION,";
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
  var currencyCheck=document.getElementsByName("currencycheck");
  var currecnycheckBoxValue=document.getElementsByName("currecnycheckBoxValue");
  for(n=0;n<currencyCheck.length;n++)
  {
	  if(currencyCheck[n].checked)
		currecnycheckBoxValue[n].value="true";
	  }

   
}
function checkcurrency(obj)
{
	if(obj.checked)
		document.getElementsByName("currencycheck")[obj.id].checked=true;
	else
		document.getElementsByName("currencycheck")[obj.id].checked=false;
}
function validate(obj)
{
	if(obj.checked==false && document.getElementsByName("check")[obj.id].checked==true)
	{
		alert("Country should also be validated for validating the Currency");
		document.getElementsByName("check")[obj.id].checked=false;
	}



}
</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="CountryInvalidate"  >
<!--<table border="0" cellpadding="4" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="6" width="100%" height="21"><font size='3'><b>Designation- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="100" height="1">CountryId</td>
            <td width="350" height="1">CountryName &nbsp;</td>
            <td width="100" height="1">CurrencyId&nbsp;</td>
			<td width="150" height="1">Region&nbsp;</td>
			<td width="100" height="1">Area&nbsp;</td>-->
		<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }    
%>
    
	<table width="100%" cellpadding="0" cellspacing="0" >
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
			<tr class='formlabel'><td  width="50%">Country Master - <%=operation%></td><td width="50%" align=right>QS1000142</td></tr>
	</table>
	<table width="100%" cellpadding="0" cellspacing="0" >
			<tr valign="top" class='formdata'> 
            <td > Terminal : <%=loginbean.getTerminalId()%></td>
            <td > User : <%=loginbean.getUserId()%></td>
            <td > Date : <%=loginbean.getCurrentDateString()%></td>
          </tr></table>
		 <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF'>
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("P_COUNTRYID"))
				heading="Country Id";
 			 if(heading.equals("P_COUNTRYNAME"))
				heading="Country Name";	
			 if(heading.equals("P_CURRENCYID"))
				heading="Currency Id";	
 			 if(heading.equals("P_REGION"))
				heading="Region";	
%>
      			  <td><%=heading%>
			      </td>
<%   
		   }
			if("Invalidate".equals(operation)){%>
			<td >Country Invalidate</td>
			<td >Currency Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						countryDOB  = (CountryMasterDOB)dobList.get(i);

						
		               
					   if("T".equals(countryDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
					   if("Y".equals(countryDOB.getCurrencyInvalidate()))
						   currencyCheck="checked";
					   else
						   currencyCheck="";

				
		  %>
          <tr class="formdata">

            <td><%=countryDOB.getCountryId()%></td>
			<%
			if(str.indexOf("P_COUNTRYNAME")>0)
			{
			%>
				<td ><%=countryDOB.getCountryName()%></td>
			<% 
			}
			if(str.indexOf("P_CURRENCYID")>0)
			{
			%>
				<td ><%=countryDOB.getCurrencyId()%></td>
			<%
			}
			if(str.indexOf("P_REGION")>0)
			{
			%>
			<td ><%=countryDOB.getRegion()%></td>
			

			<%
			}
			  if("Invalidate".equals(operation))
				  {
				%>
				
			<td ><input type='checkbox' name='check' id='<%=i%>' onClick="checkcurrency(this)" <%=checkInvalidate%>><input type="hidden" name="checkBoxValue" value="false"></td>
			<td ><input type='checkbox' name='currencycheck' id='<%=i%>' onClick="validate(this)" <%=currencyCheck%> ><input type="hidden" name="currecnycheckBoxValue" value="false"></td>
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
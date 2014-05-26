
<%
/*	Programme Name : CarrierInvalidate.jsp.
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
				   com.foursoft.etrans.setup.carrier.bean.CarrierDetail,
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
	CarrierDetail   carrierDOB=null;
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
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     =  remote.getInvalidateCarrierDetails();
		carrierDOB     = new CarrierDetail();
		
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCCarrierRegistrationProcess.jsp?Operation=Invalidate";
		}
		String str="";
	       int count=0;
			if (request.getParameter("CARRIERID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="CARRIERID,";
				count++;
				}
			if	(request.getParameter("CARRIERNAME")!=null && request.getParameter("CARRIERNAME").equals("0")){
				str+="CARRIERNAME,";
				count++;
				}
			if	(request.getParameter("SHIPMENTNAME")!=null && request.getParameter("SHIPMENTNAME").equals("0")){
				str+="SHIPMENTNAME,";
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
            <td colspan="7" width="100%" height="21"><font size='3'><b>Carrier- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="100" height="1">CarrierId&nbsp;</td>
            <td width="150" height="1">CarrierName&nbsp;</td>
			<td width="150" height="1">ShipmentMode&nbsp;</td>
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
			 if(heading.equals("CARRIERID"))
				heading="Carrier Id";
 			 if(heading.equals("CARRIERNAME"))
				heading="CarrierName";	
			 if(heading.equals("SHIPMENTNAME"))
				heading="ShipmentName";	
 			 if(heading.equals("CITY"))
				heading="City";	
%>
      			  <td width ="25%" ><%=heading%>
			      </td>
<%   
		   }if("Invalidate".equals(operation)){%>
			<td width="100" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						carrierDOB  = (CarrierDetail)dobList.get(i);

						
		               
					   if("T".equals(carrierDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="100" height="27"><%=carrierDOB.getCarrierId()%></td>
			<%if(str.indexOf("CARRIERNAME")>0)
			{
			 %>
			<td width="150" height="27"><%=carrierDOB.getCarrierName()%></td>
			<%}
			 if(str.indexOf("SHIPMENTNAME")>0)
			{
			%>
			<td width="150" height="27"><%=carrierDOB.getShipmentMode()%></td>
			<%}if(str.indexOf("CITY")>0)
			{

			%>
			<td width="150" height="27"><%=carrierDOB.getCity()==null?"":carrierDOB.getCity()%></td>
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
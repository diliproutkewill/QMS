
<%
/*	Programme Name : QMSChargeDescriptionView.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : Modify/View/ViewAll/Delete Modules
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.operations.charges.ejb.sls.ChargeMasterSession,
				   com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
				   com.qms.operations.charges.java.BuychargesHDRDOB,
				   com.qms.operations.charges.java.BuySellChargesEnterIdDOB,
				   com.foursoft.esupply.common.exception.FoursoftException,
				   java.util.ArrayList,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="QMSChargeDescriptionView.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ChargeMasterSession       remote				=  null;
	ChargeMasterSessionHome   home					=  null;
	ArrayList                 descList				=  null;
	ArrayList                 keyValueList	=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    chargeId			    =  request.getParameter("chargeId");
	String                    operation			    =  request.getParameter("Operation");
	String                    descId  			    =  request.getParameter("descId");
	String					  terminalId			=  request.getParameter("terminalId");
	String                    submitLabel		    =  null;
	String                    nextNavigation        =  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                  checkInvalidate		    =  "";   
	String                    code					=  "";
	BuychargesHDRDOB          dob                   =  null;
	ArrayList                 chargeList            =  null;

	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = new BuySellChargesEnterIdDOB();
try{
	home				=		(ChargeMasterSessionHome)ctxt.lookup("ChargeMasterSession");
	remote				=		(ChargeMasterSession)home.create();

	if(terminalId==null || terminalId.trim().equals(""))
	{	terminalId=loginbean.getTerminalId();}
	if(!("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)))
	{
		buySellChargesEnterIdDOB.setChargeId(chargeId);
		buySellChargesEnterIdDOB.setChargeDescId(descId);
		buySellChargesEnterIdDOB.setTerminalId(terminalId);
		buySellChargesEnterIdDOB.setOperation(operation);

	   //dob                 =  remote.selectChargeDiscriptionDtls(chargeId,descId,terminalId,loginbean);
	   try{
	   dob                 =  remote.selectChargeDiscriptionDtls(buySellChargesEnterIdDOB,loginbean);
	   }catch(FoursoftException e)
		{
			errorMessageObject = new ErrorMessage("Record is Already in use ,Not available for operation:"+operation,"QMSChargeDescriptionEnterId.jsp");
			keyValueList   =   new ArrayList();
			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>		    
<%
		}

	  
	}
	else{
	  chargeList           =  remote.getChargeDescDetails(operation,loginbean);
	  session.setAttribute("chargeList",chargeList);
	}
	

	if("Modify".equals(operation) || "Delete".equals(operation) || "Invalidate".equals(operation))
	{
		submitLabel="Submit";
		nextNavigation      = "QMSChargeDescriptionProcess.jsp";		
	}
	else if("View".equals(operation))
	{
		submitLabel = "Continue";
		nextNavigation      = "QMSChargeDescriptionEnterId.jsp";
	}else if("ViewAll".equals(operation))
	{
		submitLabel = "Continue";
		nextNavigation      = "../ESMenuRightPanel.jsp?link=es-et-Administration-Charges";
	}
      
%>

<html>
<head>
<title>Charge Description</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
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
function trimAll(obj) 
{
	var sString = obj.value;
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	obj.value = sString;
}
</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="chargeDescription"  >
<%if((!("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)) && dob==null)  || (("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)) &&chargeList==null)){
	errorMessageObject = new ErrorMessage("Record doesn't exist:","QMSChargeDescriptionEnterId.jsp");
	  keyValueList   =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>

<%}else{%>
<table border="0" cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td><font size='3'><b>Charge Description -<%=operation%></td><td align='right'>QS1050222</b></font></td>
          </tr>
		  </table>
         
		  <table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formheader">
            <td>Shipment Mode</td>
<%
			if("Invalidate".equalsIgnoreCase(operation)|| "ViewAll".equalsIgnoreCase(operation))
			{
%>
				<td align=center>Terminal Id</td>
<%
			}
%>

            <td>Charge Id </td>
            <td>Description Id </td>
			<!--td>Remarks:</td-->	
			<td>Internal Charge Name </td>
			<td>External Charge Name </td>
			<%if("Invalidate".equals(operation)){%>
			<td>Invalidate</td>		
			<%}%>
		 </tr>
		  <%    
				String airChecked  ="";
				String seaChecked  ="";
				String truckChecked  ="";
				String airDisabled  ="" ;
				String seaDisabled  ="" ;
				String truckDisabled  ="" ;
				if(!("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation))){
			    int    shipMode	=	dob.getShipmentMode();
					if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
					{
						airChecked	=	"Checked";
						airDisabled =    "disabled";
					}
					if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
					{
						seaChecked	=	"Checked";seaDisabled =    "disabled";
					}
					if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
					{
						truckChecked=	"Checked";truckDisabled =    "disabled";
					}

					if(!"Modify".equals(operation))
					{
						airDisabled =    "disabled";seaDisabled =    "disabled";truckDisabled =    "disabled";
					}
			%>

		  <tr class="formdata">
		    <td nowrap><input type='checkbox' name='airChecked' <%=airChecked%> <%=airDisabled%> >Air
						<input type='hidden' name='aircheckbox' value='<%=airChecked%>' >
			<input type='checkbox' name='seaChecked' <%=seaChecked%> <%=seaDisabled%>>Sea
						<input type='hidden' name='seacheckbox' value='<%=seaChecked%>'>&nbsp;<input type='checkbox' name='truckChecked' <%=truckChecked%> <%=truckDisabled%>>Truck</td>
						<input type='hidden' name='truckcheckbox' value='<%=truckChecked%>' >
<%
			if("Invalidate".equalsIgnoreCase(operation)|| "ViewAll".equalsIgnoreCase(operation))
			{
%>
	          <td><%=dob.getTerminalId()%></td>
<%
			}
%>
            <td><input type='hidden' name='chargeId'value='<%=chargeId%>'><%=chargeId%></td>
            <td><input type='hidden' name='descId'value='<%=descId%>'><%=descId%></td>
			<%if("Modify".equals(operation)){%>
            <td><input type='text' class='text' name='remarks'value='<%=dob.getRemarks()!=null?dob.getRemarks():""%>' onBlur="trimAll(this)" maxlength='50'></td>
			<td><input type='text' class='text' name='externalChargeName'value='<%=dob.getExternalChargeName()!=null?dob.getExternalChargeName():""%>' onBlur="trimAll(this)" maxlength='50'></td>
			<%}else{%>
			<td><!--input type='hidden' name='remarks' value=''--><%=dob.getRemarks()!=null?dob.getRemarks():""%></td>
			<td><%=dob.getExternalChargeName()!=null?dob.getExternalChargeName():""%></td>
			<%}%>
			<input type='hidden' name='shipmentMode'value='<%=dob.getShipmentMode()%>'>
          </tr>
		<%}else{
			  for(int i=0;i<chargeList.size();i++){
					dob  = (BuychargesHDRDOB)chargeList.get(i);


					int    shipMode	=	dob.getShipmentMode();
          airChecked    = "";
					seaChecked    = "";
					truckChecked  = ""; 
					if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
					{
						airChecked	=	"Checked";
					}
					if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
					{
						seaChecked	=	"Checked";
					}
					if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
					{
						truckChecked=	"Checked";
					}
			  %>  
		  <tr class="formdata">
		    <td nowrap><input type='checkbox' name='airChecked' <%=airChecked%> disabled>Air
			<input type='checkbox' name='seaChecked' <%=seaChecked%> disabled>Sea&nbsp;<input type='checkbox' name='truckChecked' <%=truckChecked%> disabled>Truck</td>
            <td><%=dob.getTerminalId()%></td>
            <td><%=dob.getChargeId()%></td>
            <td><%=dob.getChargeDescId()%></td>			
            <td><%="null".equalsIgnoreCase(dob.getRemarks())?"":dob.getRemarks()%></td>
			<!-- null check by naresh -->
			<td><%=dob.getExternalChargeName()!=null?dob.getExternalChargeName():""%></td>
			<%if("Invalidate".equalsIgnoreCase(operation)){%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(dob.getInvalidate())?"checked":""%>></td>			
			<%}%>
			<input type='hidden' name='shipmentMode'value='<%=dob.getShipmentMode()%>'>
          </tr>
		<%}
		}%>
        </tbody>
      </table>

      <table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
        <tbody>
          <tr >
				<td colspan="2" width="452" class='denotes'><font color="#FF0000">*</font>Denotes Mandatory</td>
				<td align="right" colspan="3" width="325"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>'>
				<input type="hidden" name="Operation" value='<%=operation%>'>
				<input type="hidden" name="terminalId" value='<%=terminalId%>'>
            </td>
          </tr>
        </tbody>
      </table>

<%}%>
</form>
</body>

</html>
<%}
  catch(Exception e)
  {e.printStackTrace();
	  //Logger.error(FILE_NAME,"Error in QMSChargeDescriptionView.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSChargeDescriptionView.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Error while accessing this page","QMSChargeDescriptionEnterId.jsp");
	  keyValueList   =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%

  }
%>
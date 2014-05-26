
<%
/*	Programme Name : QMSDensityGroupCodeView.jsp.
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

<%@ page import = "com.qms.setup.ejb.sls.QMSSetUpSession,
				   com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				   com.qms.setup.java.DensityGroupCodeDOB,
				   java.util.ArrayList,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!  
  private static Logger logger = null;
  public static final String FILE_NAME="QMSDensityGroupCodeView.jsp";%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	QMSSetUpSession       remote				=  null;
	QMSSetUpSessionHome       home					=  null;
	DensityGroupCodeDOB       dgcDOB				=  null;
	DensityGroupCodeDOB       dgcDOB1				=  null;
	ArrayList                 daoList				=  null;
	ArrayList                 keyValueList	=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    dgcCode			  =  request.getParameter("dgcCode");
	String                    operation			  =  request.getParameter("Operation");
	//String                    uom					  =  request.getParameter("uom");
	String                    perKG					=  request.getParameter("perKG");
	String                    submitLabel		=  null;
	String                    nextNavigation=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                  checkInvalidate		=  "";   
	String                    code					=  "";
	double                    perLB             = 0.0;

try{
	home				=		(QMSSetUpSessionHome)ctxt.lookup("QMSSetUpSessionBean");
	remote				=		(QMSSetUpSession)home.create();

	if(!"ViewAll".equals(operation) && !"Invalidate".equals(operation) )
	{

		perLB     =  remote.getDensityGroupCodeDetails(Integer.parseInt(dgcCode),Double.parseDouble(perKG));	//uom,
	}
	else
	{
	    daoList     =  remote.getDensityGroupCodeDetails();
		dgcDOB     = new DensityGroupCodeDOB();
	}

	if("Modify".equals(operation) || "Delete".equals(operation) ||  "Invalidate".equals(operation))
	{
		submitLabel="Submit";
		nextNavigation      = "QMSDensityGroupCodeProcess.jsp";		
	}
	else if("View".equals(operation))
	{
		submitLabel = "Continue";
		nextNavigation      = "QMSDensityGroupCodeEnterId.jsp";
	}
      
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<html>
<head>
<title>Density Group Code</title>
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
function submitButton()//Added by rk
{
	var operation ='<%=operation%>';
	window.document.forms[0].action='QMSDensityGroupCodeViewAllEnterId.jsp?Operation='+operation;
	window.document.forms[0].submit();
}
</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="DensityGroupCode">
<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Density Group Code - <%=operation%></td><td align=right>QS1010321</td></tr></table></td>
		  </tr></table>
		  
		  <table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
          <tr class="formheader">
		  <%if("Invalidate".equals(operation)||"ViewAll".equals(operation)){%>
		  <%if(request.getParameter("dgcCode")!=null && request.getParameter("dgcCode").equals("0")){%>
            <td width="267" height="1">Shipment&nbsp;Mode&nbsp;</td>
			<%}if(request.getParameter("kgPerm3")!=null && request.getParameter("kgPerm3").equals("0")){%>
            <td width="267" height="1">KG per m3 &nbsp;</td>
			<%}if(request.getParameter("kgPerf3")!=null && request.getParameter("kgPerf3").equals("0")){%>
            <td width="267" height="1">LB per f3 &nbsp;</td>
			<%}if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}}else{%>
			<td width="267" height="1">Shipment&nbsp;Mode&nbsp;</td>
            <td width="267" height="1">KG per m3 &nbsp;</td>
            <td width="267" height="1">LB per f3&nbsp;<FONT size='1' COLOR="red">*</FONT> &nbsp;</td>
			<!-- <td width="267" height="1">UOM</td>		 -->	
			<%}%>
          </tr>
		  <%    code = "";
				if("ViewAll".equals(operation) || "Invalidate".equals(operation)){
					for(int i=0;i<daoList.size();i++)
					{
						dgcDOB  = (DensityGroupCodeDOB)daoList.get(i);

						if(dgcDOB.getDGCCode()==1)
							code="Air";
						else if(dgcDOB.getDGCCode()==2)
							code="Sea";
						else if(dgcDOB.getDGCCode()==4)
							code="Truck";
		               
					   if("T".equals(dgcDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
					if("F".equals(dgcDOB.getInvalidate()) || "Invalidate".equals(operation))
					{
		  %>
          <tr class="formdata">
		  <%if(request.getParameter("dgcCode")!=null && request.getParameter("dgcCode").equals("0")){%>
            <td width="30%" height="27"><input type='hidden' name='dgcCode'value='<%=dgcDOB.getDGCCode()%>'><%=code%></td>
			<%}if(request.getParameter("kgPerm3")!=null && request.getParameter("kgPerm3").equals("0")){%>
            <td width="30%" height="27"><input type='hidden' name='perKG'value='<%=dgcDOB.getPerKG()%>'><%=dgcDOB.getPerKG()%></td>
			<%}if(request.getParameter("kgPerf3")!=null && request.getParameter("kgPerf3").equals("0")){%>
            <td width="30%" height="27"><input type='hidden' name='perLB'value='<%=dgcDOB.getPerLB()%>'><%=dgcDOB.getPerLB()%></td>
			<%}%>
				<%
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
			}
		 }else if(perLB>0){
		   
			    if(new Integer(dgcCode).intValue()==1)
							code="Air";
				else if(new Integer(dgcCode).intValue()==2)
							code="Sea";
				else if(new Integer(dgcCode).intValue()==4)
							code="Truck";
			%>

		  <tr class="formdata">
            <td width="100" height="27"><input type='hidden' name='dgcCode'value='<%=dgcCode%>'><%=code%></td>
            <td width="223" height="27"><input type='hidden' name='perKG'value='<%=perKG%>'><%=perKG%></td>
			<%if("Modify".equals(operation)){%>
            <td width="223" height="27"><input type='text' name='perLB' size='12' maxLength='12' value='<%=perLB%>' class='text'></td>
			<%}else{%>
			<td width="223" height="27"><input type='hidden' name='perLB'value='<%=perLB%>'><%=perLB%></td>
			<%}%>
			<!-- <td width="223" height="27"><input type='hidden' name='uom'value='<%//=dgcDOB1.getUOM()%>'><%//=dgcDOB1.getUOM()%></td> -->
          </tr>
		  <% 
			}else{		  	  
			      errorMessageObject = new ErrorMessage("The Kg per m3 is not valid :"+perKG,"QMSDensityGroupCodeEnterId.jsp");
				  keyValueList   =   new ArrayList();
				  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				  errorMessageObject.setKeyValueList(keyValueList);
				  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>

		  <%}%>
        </tbody>
      </table>
	  <%
			  if("ViewAll".equals(operation)){%>
       <table border="0" cellpadding="" cellspacing="" width="100%" bgcolor='#FFFFFF'>
        <tbody>
          <tr >
            <td colspan="1" width="" align='right'>
	  <input type="button" value="Continue" name="continue" class="input" onclick='submitButton()'><!-- added by rk for issue 8385 -->
	  </td>
          </tr>
        </tbody>
      </table>
<%
		}
		else
		{
%>
	  <table border="0" cellpadding="4" cellspacing="0" width="100%" >
        <tbody>
          <tr class='denotes' >
            <td colspan="2" width="452"><font color="#FF0000">*</font>Denotes Mandatory</td>
            <td align="right" colspan="2" width="325"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' 'border-style: solid'>
			<input type="hidden" name="Operation" value='<%=operation%>'
            </td>
          </tr>
        </tbody>
      </table>
	  <%}%>

</body>

</html>
<%}
  catch(Exception e)
  {e.printStackTrace();
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
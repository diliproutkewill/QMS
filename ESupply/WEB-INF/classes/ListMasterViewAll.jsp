
<%
/*	Programme Name : ListMasterViewAll.jsp.
*	Module Name    : ListMaster.
*	Task Name      : ListMaster
*	Author		   : K.NareshKumarReddy
*	Date Started   : 23 July 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.ejb.sls.SetUpSession,
				   com.qms.setup.ejb.sls.SetUpSessionHome,
				   java.util.ArrayList,
				   javax.naming.InitialContext,
				   com.qms.setup.java.ListMasterDOB,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="ListMasterViewAll.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	ListMasterDOB			 listDOB				=  null;
	ArrayList                 dobList				=  null;
	ArrayList				  airList				=null;
	ArrayList				  seaList				= null;
	ArrayList				  truckList				=null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation=request.getParameter("Operation");
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

	
	    dobList     = remote.getListDetails();
		airList=(ArrayList) dobList.get(0);
		seaList=(ArrayList) dobList.get(1);
		truckList=(ArrayList) dobList.get(2);

		if(operation.equalsIgnoreCase("ViewAll"))
		{
			submitLabel="Continue";
			nextNavigation="javascript:history.go(-1)";
			
		}
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ListMasterProcess.jsp?Operation=Invalidate";
		}
	
      
%>

<html>
<head>
<title>Density Group Code</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
</head>

<script language="JavaScript">

function checkValues()
{ <%
if(airList.size()>0)
{%>
   var aircheckBox=document.getElementsByName("checkair");
   var aircheckBoxValue=document.getElementsByName("aircheckBoxValue");
   for(m=0;m<aircheckBox.length;m++)
   {
     if(aircheckBox[m].checked)
		aircheckBoxValue[m].value="true";    	 
  }
	 <%}
if(seaList.size()>0)
{%>
	 var seacheckBox=document.getElementsByName("checksea");
   var seacheckBoxValue=document.getElementsByName("seacheckBoxValue");
   for(m=0;m<seacheckBox.length;m++)
   {
     if(seacheckBox[m].checked)
		seacheckBoxValue[m].value="true";    	 
  }
  <%}
if(truckList.size()>0)
{%>
 var truckcheckBox=document.getElementsByName("checktruck");
   var truckcheckBoxValue=document.getElementsByName("truckcheckBoxValue");
   for(m=0;m<truckcheckBox.length;m++)
   {
     if(truckcheckBox[m].checked)
		truckcheckBoxValue[m].value="true";    	 
  }
<%}%>   
}
</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="ListMaster"  >
<table border="0" cellpadding="4" cellspacing="1" width="803" height="83" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
<%		  if(operation.equalsIgnoreCase("ViewAll"))
		{
%>
            <td colspan="4" width="789" height="21"><font size='3'><b>ListMaster- <%=operation%></b></font></td>
			<td align="right">QS1020231</td>
<%
		}
		else
		{
%>
		       <td colspan="5" width="789" height="21"><font size='3'><b>ListMaster- <%=operation%></b></font></td>
			<td align="right">QS1020241</td>
<%
	}
%>

          </tr>
		  <tr class="formheader">
            <td colspan="6" width="789" height="21"><font size='3'><b>ShipmentMode- Air</b></font></td>
          </tr>
		<% if(airList.size()>0)
			{%>
          <tr class="formheader">
            <td width="267" height="1">ULD Type</td>
            <td width="267" height="1">Description &nbsp;</td>
            <td width="267" height="1">Volume&nbsp;</td>
			<td width="267" height="1">PivotWeight</td>
			<td width="267" height="1">OverPivotWeight</td>
			<%if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<airList.size();i++)
					{
						listDOB  = (ListMasterDOB)airList.get(i);

						
		               
					   if("T".equals(listDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="267" height="27"><%=listDOB.getUldType()%></td>
            <td width="223" height="27"><%=listDOB.getDescription()!=null?listDOB.getDescription():""%></td>
            <td width="223" height="27"><%=listDOB.getVolume()%></td>
            <td width="223" height="27"><%=listDOB.getPivoteUladenWeight()%></td>
            <td width="223" height="27"><%=listDOB.getOverPivoteTareWeight()%></td>
			<%
				  if("Invalidate".equals(operation))
				  {
				%>
			<td width="223" height="27"><input type='checkbox' name='checkair'  <%=checkInvalidate%>><input type="hidden" name="aircheckBoxValue" value="false"></td>
				<%
				  }
				%>
          </tr> 
			<%}
			}
			if(seaList.size()>0)
			{%>
			<tr class="formheader">
            <td colspan="6" width="789" height="21"><font size='3'><b>ShipmentMode- Sea</b></font></td>
          </tr>

          <tr class="formheader">
            <td width="267" height="1">Container&nbsp;Type</td>
            <td width="267" height="1">Description &nbsp;</td>
            <td width="267" height="1">Volume&nbsp;</td>
			<td width="267" height="1">Laden&nbsp;Weight</td>
			<td width="267" height="1">Tare Weight</td>
			<%if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<seaList.size();i++)
					{
						listDOB  = (ListMasterDOB)seaList.get(i);

						
		               
					   if("T".equals(listDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="267" height="27"><%=listDOB.getUldType()%></td>
            <td width="223" height="27"><%=listDOB.getDescription()!=null?listDOB.getDescription():""%></td>
            <td width="223" height="27"><%=listDOB.getVolume()%></td>
            <td width="223" height="27"><%=listDOB.getPivoteUladenWeight()%></td>
            <td width="223" height="27"><%=listDOB.getOverPivoteTareWeight()%></td>
			<%
				  if("Invalidate".equals(operation))
				  {
				%>
			<td width="223" height="27"><input type='checkbox' name='checksea'  <%=checkInvalidate%>><input type="hidden" name="seacheckBoxValue" value="false"></td>
				<%
				  }
				%>
          </tr> 
			<%}
			}if(truckList.size()>0)
				{%>
			<tr class="formheader">
            <td colspan="6" width="789" height="21"><font size='3'><b>ShipmentMode-Truck</b></font></td>
          </tr>

          <tr class="formheader">
            <td width="267" height="1">Container&nbsp;Type</td>
            <td width="267" height="1">Description &nbsp;</td>
            <td width="267" height="1">Volume&nbsp;</td>
			<td width="267" height="1">Laden&nbsp;Weight</td>
			<td width="267" height="1">Tare Weight</td>
			<%if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<truckList.size();i++)
					{
						listDOB  = (ListMasterDOB)truckList.get(i);

						
		               
					   if("T".equals(listDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="267" height="27"><%=listDOB.getUldType()%></td>
            <td width="223" height="27"><%=listDOB.getDescription()!=null?listDOB.getDescription():""%></td>
            <td width="223" height="27"><%=listDOB.getVolume()%></td>
            <td width="223" height="27"><%=listDOB.getPivoteUladenWeight()%></td>
            <td width="223" height="27"><%=listDOB.getOverPivoteTareWeight()%></td>


				<%
				  if("Invalidate".equals(operation))
				  {
				%>
			<td width="223" height="27"><input type='checkbox' name='checktruck'  <%=checkInvalidate%>><input type="hidden" name="truckcheckBoxValue" value="false"></td>
				<%
				  }
					}
				%>
          </tr> 
		  <%
				
		
		
				
		 
					}
		  %>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="803" bgcolor='#FFFFFF'>
        <tbody>
		<% 	if(session.getAttribute("dobList")!=null)
			{
			  session.removeAttribute("dobList");
			}

			  session.setAttribute("dobList",dobList);

			 %>
          <tr >
            <td colspan="2" width="452" class='denotes'><font color="#FF0000">*</font>Denotes Mandatory</td>
            <td align="right" colspan="2" width="325"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>'>
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
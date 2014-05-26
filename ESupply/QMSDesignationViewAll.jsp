
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
				   com.qms.setup.java.DesignationDOB,
				   java.util.ArrayList,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!  
  private static Logger logger = null;
  public static final String FILE_NAME="QMSDesignationViewAll.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	QMSSetUpSession       remote				=  null;
	QMSSetUpSessionHome   home					=  null;
	DesignationDOB			 desiDOB				=  null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation=request.getParameter("Operation");
try{
	home				=		(QMSSetUpSessionHome)ctxt.lookup("QMSSetUpSessionBean");
	remote				=		(QMSSetUpSession)home.create();

	
	    dobList     =  remote.getDesignationDetails();
		desiDOB     = new DesignationDOB();
		if(operation.equalsIgnoreCase("ViewAll"))
		{
			submitLabel="Continue";
        
			nextNavigation="javascript:history.go(-1)";
			
		}
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="QMSDesignationProcess.jsp?operation=Invalidate";
		}
	
      
%>

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
</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="DensityGroupCode"  >
<table border="0" cellpadding="4" cellspacing="1" width="803" height="83" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td colspan="2" width="789" height="21"><font size='3'><b>Designation- <%=operation%></b></font></td>
			<td align="right">QS1020131</td>
          </tr>
          <tr class="formheader">
            <td width="267" height="1">LevelNo&nbsp;</td>
            <td width="267" height="1">Designation&nbsp;Code&nbsp;</td>
            <td width="267" height="1">Description &nbsp;</td>
            
			<%if("Invalidate".equals(operation)){%>
			<td width="267" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
				
					for(int i=0;i<dobList.size();i++)
					{
						desiDOB  = (DesignationDOB)dobList.get(i);

						
		               
					   if("T".equals(desiDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
				
		  %>
          <tr class="formdata">
            <td width="223" height="27"><%=desiDOB.getLevelNo()%></td>
            <td width="267" height="27"><%=desiDOB.getDesignationId()%></td>
            <td width="223" height="27"><%=desiDOB.getDescription()%></td>
            
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
		  %>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="803" bgcolor='#FFFFFF'>
        <tbody>
		<% 		 session.setAttribute("dobList",dobList);

			 %>
          <tr >
            <td colspan="2" width="452" class='denotes'><font color="#FF0000">*</font>Denotes Mandatory</td>
            <td align="right" colspan="2" width="325"><input class='input' name='<%=submitLabel%>' type='Submit' value='<%=submitLabel%>' "border-style: solid">
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
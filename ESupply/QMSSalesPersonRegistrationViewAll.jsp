
<%
/*	Programme Name : QMSSalesPersonRegistrationViewAll.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : SalesPersonRegistration
*	Sub Task Name  : 
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import ="java.util.ArrayList,
                 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.*,
				 "
%>
<jsp:useBean id="loginBean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
%>

<%
    logger  = Logger.getLogger("QMSSalesPersonRegistrationViewAll.jsp");
    String  operation     =  request.getParameter("Operation");
%>
<html>
<head>
<title>View All</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript" ></script>

<script>

function selectedAll()
{
  if(document.forms[0].selectAll.checked==true)
  {		
     for (var i=0 ; i < document.forms[0].elements.length;i++)
	  {
	   if(document.forms[0].elements[i].type=="checkbox")	
		 {
			document.forms[0].elements[i].checked=true
	       }  
 	   }
   }
    else
    {		
     for (var i=0 ; i < document.forms[0].elements.length;i++)
	  {
	   if(document.forms[0].elements[i].type=="checkbox")	
		 {
	       document.forms[0].elements[i].checked=false
	       }  
 	   }
   }
}

function goBack()
{
 history.back();
}
function checkSelected()
{
     var flag = false;	
     for (var i=0 ; i < document.forms[0].elements.length;i++)
	  {
	   if(document.forms[0].elements[i].type=="checkbox" && document.forms[0].elements[i].name!="selectAll")	
		 {
	       if(document.forms[0].elements[i].checked==true)
	       {
			 flag = true;
			 return flag;
	       }
		 } 
      }   
	 return flag; 
}

function Mandatory()
{
    if( document.forms.length > 0 ) 
   {
		var field = document.forms[0];
		for(i = 0; i < field.length; i++) 
		{
		   if( (field.elements[i].type == "text"))
			   document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
           
        }
    }
   var flag = checkSelected();
   if(flag==false)
	   alert("Please Select atlease one check box");
return flag;
}
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

</head>

<body  onLoad="">
<form method="post" action="QMSSalesPersonViewAllProcess.jsp"  onSubmit ="return Mandatory();placeFocus()">
  <table width="760" border="0" cellspacing="0" cellpadding="0" height="0">
    <tr bgcolor="#ffffff"> 
      <td height="0">
        <table width="760" border="0" cellspacing="1" cellpadding="4" >
          <tr valign="top" class="formlabel" > 
            <td ><b>
			   Sales Person Registration -<%=operation%></td><td align="right">QS1010431	 </td>
          </tr>
        </table>
        
        <table width="760" border="0" cellspacing="1" cellpadding="4" >
          
          <tr class='formdata' valign="top"> 
            <td colspan="3"> 
              <input type="checkbox" name="locationId" value="0">
              Location Id&nbsp;</td>
            <td > 
              <input type="checkbox" name="terminalId" value="0">
              Terminal Id</td>
            <td > 
              <input type="checkbox" name="salesPersonCode" value="0">
              Sales Person Code</td>
          </tr>
          <tr class='formdata' valign="top"> 
		  <td colspan="3"> 
              <input type="checkbox" name="salesPersonName" value="0">
              Sales Person Name</td>
            <td > 
              <input type="checkbox" name="designation" value="0">
              Designation</td>
            <td > 
              <input type="checkbox" name="designationLevel" value="0">
              Level</td>
            
          </tr>
          <tr class='formdata' valign="top"> 
		  <td colspan="3"> 
              <input type="checkbox" name="dateOfJoining" value="0">
              Date Of Joining</td>
            <td > 
              <input type="checkbox" name="reportingCode" value="0">
              Reporting Officer's Code</td>
            <td > 
              <input type="checkbox" name="reportingOffName" value="0">
              Reporting Officer's Name</td>
            
          </tr>
          <tr class='formdata' valign="top"> 
		  <td colspan="3"> 
              <input type="checkbox" name="escalationLevel" value="0">
              Escalation Level</td>
            <td > 
              <input type="checkbox" name="reportingsDesignation" value="0">
              Designation</td>
            <td > 
              <input type="checkbox" name="time" value="0">
              Alloted Time</td>
           
			  
          </tr>
		  <tr class='formdata' valign="top">
		  <td colspan="3">
              <input type="checkbox" name="remarks" value="0"> 
              Remarks</td>
			  <td></td>
			  <td></td>
		  </tr>
          
        </table> 
         <table width="760" border="0" cellspacing="0" cellpadding="0" height="0"> 
         <tr bgcolor="white">   
		 <td>		 
        <font size="2" face="Verdana">
        <input type="checkbox" name="selectAll" value="0" onClick="selectedAll()">		
		<b>Select All</b>
         </font>
		 </td>
		 <td align="right">
			<input type="reset" name="Reset" value="Reset" onClick=placeFocus() class='input'>		  	
		    <input type="submit" name="submit" value="Submit" class='input'>             
			<input type="hidden" name="Operation" value = "<%=operation%>">
		</td>
        </tr>
        </table>
      </td>
    </tr>
  </table>
  <br>
</form>
</body>
</html>
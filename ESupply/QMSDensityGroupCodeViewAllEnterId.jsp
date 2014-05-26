
<%
/*	Programme Name : QMSDensityGroupCodeViewAllEnterId.jsp.
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
    logger  = Logger.getLogger("QMSDensityGroupCodeViewAllEnterId.jsp");
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
  document.forms[0].dgcCode.disabled=false;
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

<body  onLoad="" >
<form method="post" action="QMSDensityGroupCodeView.jsp?Operation=<%=operation%>"  onSubmit ="return Mandatory();placeFocus()">
  <table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Density Group Code - <%=operation%></td><td align=right>QMS1010411</td></tr></table></td>
		  </tr></table>
		  
		  <table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
          <tr class='formdata' valign="top" > 
            <td  align="right" width="50%"> 
              <input type="checkbox" name="dgcCode" value="0" checked disabled></td>
			  <td width="50%">
              Shipment Mode</td>
			  </tr>
			  <tr class='formdata' valign="top"> 
            <td align="right" width="50%"> 
              <input type="checkbox" name="kgPerm3" value="0"></td><td width="50%">
              Kg Per m3</td>
			  </tr>
			  <tr class='formdata' valign="top"> 
            <td align="right" width="50%"> 
              <input type="checkbox" name="kgPerf3" value="0"></td><td width="50%">
              Kg per f3</td>
			  </tr>
			  <tr class='formdata' valign="top">
				<td align='right'><input type="checkbox" name="selectAll" value="0" onClick="selectedAll()"></td>
				<td align='left'>Select All</td>
			</tr>
        </table> 
		 <table width="100%" border="0" cellspacing="0" cellpadding="0" height="0"> 
         <tr bgcolor="white"> 
		 <td align="right">
			<input type="reset" name="Reset" value="Reset" class='input'>		  	
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
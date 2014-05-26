<%@ page import = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters,java.util.ArrayList,
					org.apache.log4j.Logger, java.util.*"%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session" />
<%!
  private static Logger logger = null;
  private static final String FILE_NAME = "ETCustomerInvoiceInfo.jsp";
%>
<html>
  <head>
	<jsp:include page="ETDateValidation.jsp" >
		<jsp:param name="format" value="<%=loginbean.getUserPreferences().getDateFormat()%>"/>
	</jsp:include>
    <title>Invoice Info</title>
    <link rel="stylesheet" href="ESFoursoft_css.jsp">
<SCRIPT>
function validate()
{	
	if(document.forms[0].invoiceFrequencyValidDate.value=="")
	{
		alert("Valid From Date shouldn't be blank");
		document.forms[0].invoiceFrequencyValidDate.focus();
		return false;	
	}  
	else
		sendData();
	return true;
}
function sendData() 
{
		var form		= document.forms[0];
		var openerform	= opener.document.forms[0];
		openerform.invoiceInfo.value = "";
		
		openerform.invoiceFrequencyValidDate.value = form.invoiceFrequencyValidDate.value;
		if(form.invoiceFrequencyFlag[0].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "D";
		else if(form.invoiceFrequencyFlag[1].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "O";
		else if(form.invoiceFrequencyFlag[2].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "W";
		else if(form.invoiceFrequencyFlag[3].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "F";
		else if(form.invoiceFrequencyFlag[4].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "M";
		else if(form.invoiceFrequencyFlag[5].checked==true)
			openerform.invoiceFrequencyFlag.value		 = "S";
		if(openerform.invoiceFrequencyFlag.value == "O")
			openerform.invoiceInfo.value		 = form.onceInDays.value;	
		if(openerform.invoiceFrequencyFlag.value == "F")
			openerform.invoiceInfo.value		 = "15";	
		else if(openerform.invoiceFrequencyFlag.value	== "W") 
		{
		  for (var i=0;i<form.selectDays2.options.length;i++) {
			if (form.selectDays2.options[i].selected) 
			openerform.invoiceInfo.value	 = openerform.invoiceInfo.value+form.selectDays2.options[i].value+",";	       
	  	  }
		}
		else if(openerform.invoiceFrequencyFlag.value	== "S") 
		{
		  for (var i=0;i<form.selectDates2.options.length;i++) {
			if (form.selectDates2.options[i].selected) 
			openerform.invoiceInfo.value	= openerform.invoiceInfo.value+form.selectDates2.options[i].value+",";	       
	  	  }
		}
		if(form.invoiceFrequencyFlag[2].checked==true || form.invoiceFrequencyFlag[5].checked==true ){
			openerform.invoiceInfo.value=openerform.invoiceInfo.value.substring(0,(openerform.invoiceInfo.value).lastIndexOf(","));	
		}

	window.close();
	openerform.focus();
}

function deleteOption(object,index) {
    object.options[index] = null;
}
function addOption(object,text,value) {
    var defaultSelected = true;
    var selected = true;
    var optionName = new Option(text, value, defaultSelected, selected)
    object.options[object.length] = optionName;
}
function copyAllSelected(fromObject,toObject) {
	var count=0;
    for (var i=0;i<fromObject.options.length;i++) {
		if (fromObject.options[i].selected) {
            addOption(toObject,fromObject.options[i].text,fromObject.options[i].value);
			count++;
		}
    }
	if(count==0) {
		alert("Select atleast one option");
		fromObject.focus();
	}
    for (var i=fromObject.options.length-1;i>-1;i--) {
        if (fromObject.options[i].selected)
            deleteOption(fromObject,i);
    }
}

function checkTableVisibility(checkedValue){

	var specificPeriod		= document.getElementById("specificPeriod");
	var weekly				= document.getElementById("weekly");

	if(checkedValue == 'S'){
		specificPeriod.style.display= "block";
		weekly.style.display		= "none";
	}else if(checkedValue == 'W'){
		weekly.style.display		= "block";
		specificPeriod.style.display= "none";
	}else{
		specificPeriod.style.display= "none";
		weekly.style.display		="none";
	}
}

<%
  logger  = Logger.getLogger(FILE_NAME);	
String	invoiceFrequencyValidDate	= request.getParameter("invoiceFrequencyValidDate");
String	invoiceFrequencyFlag		= request.getParameter("invoiceFrequencyFlag");
String	invoiceInfo					= request.getParameter("invoiceInfo");
Set		invoiceInfoSet				= new HashSet();

if("W".equals(invoiceFrequencyFlag) || "S".equals(invoiceFrequencyFlag)){
	invoiceInfoSet.addAll(java.util.Arrays.asList(invoiceInfo.split(",")));
}
%>
</script>
  </head>
<body onLoad="">
<form>
	<table width="500" border="0" cellspacing="0" cellpadding="0">
     <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="500" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formlabel'> 
            <td colspan="3">
			<table width="500" border="0" ><tr class='formlabel'>
			<td>Invoicing Frequency<%=invoiceInfoSet%></td>
			</tr></table>
			</td>
          </tr></table>
	  <table width="500" cellpadding="4" cellspacing="1">
		<tr class='formdata'> 
            <td width="500">Valid From (DD-MM-YY) :<font color="#ff0000">*</font><br>
              <input type="text" class="text" name="invoiceFrequencyValidDate" size="16" onBlur="dtCheck(this);"
			   value="<%=invoiceFrequencyValidDate==null ? "" : invoiceFrequencyValidDate%>">
			 <input type='button' value='...' onClick="newWindow('invoiceFrequencyValidDate','0','0','../')"  name='validFromDateLOV'  class='input'> 
			</td>
		</tr>
		<tr class='formheader'>
		 <td width="500"> Period <br> </td>
        </tr>
		<tr valign="top" class='formdata'> 
	       <td  width="500">	
		   	 <input type="radio" name="invoiceFrequencyFlag" value="D" checked onClick="checkTableVisibility()"
			 <%="D".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Daily
		   </td>
		</tr>
		<tr valign="top" class='formdata'>
			 <td  class='formdata' width="500"> 
				<input type="radio" name="invoiceFrequencyFlag" value="O" onClick="checkTableVisibility()"
				<%="O".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Once in	
			    <input type="text" class="text" name="onceInDays" size="3"
				value="<%="O".equals(invoiceFrequencyFlag) ? invoiceInfo : ""%>"> Days 
			 </td>
		</tr>
		<tr valign="top" class='formdata'> 
	       <td  width="500">	
		   	 <input type="radio" name="invoiceFrequencyFlag" value="W" onClick="checkTableVisibility('W')" 
			 <%="W".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Weekly
		   </td>
		</tr>
		 </table>
		 <table width="500" cellspacing="1" cellpadding='4' id="weekly" 
		style="<%="W".equals(invoiceFrequencyFlag) ? "" : "display:none"%>">
		<tr class='formheader'>
		  <td colspan='4'>Select Week Days For Invoicing</td>
		 </tr>
 		 <tr class='formdata'> 
            <td width='150' align='right'> 
              <select name="selectDays1" size="7" multiple class='select' style="width: 12mm" >
				<%
					for(int day=1;day<=7;day++){
						if(invoiceInfoSet.contains(day+"") && "W".equals(invoiceFrequencyFlag)){
							continue;
						}
				%>
						  <option value=<%=day%>> <%=getDay(day)%></option>
				<%
					}
				%>
              </select>           
            </td>
    		<td width='150' align='middle'>  <br>
				<input type='button' class='input' value=" >> " name="B1" onClick="copyAllSelected(this.form.selectDays1,this.form.selectDays2)"  > <br><br><br>
				<input type='button' class='input' value=" << " name="B2" onClick="copyAllSelected(this.form.selectDays2,this.form.selectDays1)" > 
			</td>
			<td  width="200"> 
              <select name="selectDays2" size="7" multiple class='select' style="width: 12mm" >		
			<%
				if("W".equals(invoiceFrequencyFlag)){
					for(int day=1;day<=7;day++){
						if(invoiceInfoSet.contains(day+"")){
				%>
						  <option value=<%=day%> selected> <%=getDay(day)%></option>
				<%
						}
					}
				}
			%>	
			  </select>
            </td>
         </tr>
		</table>
	
		<table width="500" cellpadding="4" cellspacing="1">
		<tr valign="top" class='formdata'> 
	       <td  width="500">	
		   	 <input type="radio" name="invoiceFrequencyFlag" value="F"  onClick="checkTableVisibility()"
			 <%="F".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Fortnightly
		   </td>
		</tr>
		<tr valign="top" class='formdata'> 
	       <td  width="500">	
		   	 <input type="radio" name="invoiceFrequencyFlag" value="M"  onClick="checkTableVisibility()"
			 <%="M".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Monthly
		   </td>
		</tr>
		<tr valign="top" class='formdata'> 
	       <td  width="500">	
		   	 <input type="radio" name="invoiceFrequencyFlag" value="S"  onClick="checkTableVisibility('S')"
			 <%="S".equals(invoiceFrequencyFlag) ? "checked" : ""%>>Specific Period
		   </td>
		</tr>
      </table>  
	  <table width="500" cellspacing="1" cellpadding='4' id="specificPeriod" 
	  style="<%="S".equals(invoiceFrequencyFlag) ? "" : "display:none"%>">
		<tr class='formheader'>
		  <td colspan='4'>Select Week Days For Invoicing</td>
		 </tr>
 		 <tr class='formdata'> 
            <td width='150' align='right'> 
              <select name="selectDates1" size="8"  multiple class='select' style="width: 10mm" >
		<%
			for(int date=1;date<=31;date++){
				if(invoiceInfoSet.contains(date+"") && "S".equals(invoiceFrequencyFlag)){
					continue;
				}
		%>
				  <option value=<%=date%>> <%=date%></option>
		<%
			}
		%>
              </select>           
            </td>
    		<td width='150' align='middle'>    <br>
				<input class='input' type='button' value=" >> " name="B3" onClick="copyAllSelected(this.form.selectDates1,this.form.selectDates2)"  > <br><br><br>
				<input type='button' class='input' value=" << " name="B4" onClick="copyAllSelected(this.form.selectDates2,this.form.selectDates1)" > 
			</td>
			<td  width="200"> 
              <select name="selectDates2" size="7" multiple class='select' style="width: 10mm"  >	
		<%
			if("S".equals(invoiceFrequencyFlag)){
				for(int date=1;date<=31;date++){
					if(invoiceInfoSet.contains(date+"")){
			%>
					  <option value=<%=date%> selected> <%=date%></option>
			<%
					}
				}
			}
		%>			  
			  </select>
            </td>
         </tr>
		 </table>
		<table width="500" cellspacing="1" cellpadding='4'>
		 <tr class='denotes'> 
			<td colspan='4' align='middle' width='500'>
				<input type='button' value='Cancel' onClick="window.close()" name='close' class='input'>
				<input type='button' value='Submit' name='submit' class='input' onClick="validate()">
			</td>
		  </tr>
		</table>
	  </td>
    </tr>
  </table>
  </form>
</body>
</html>
<%!

private String getDay(int day){
	switch(day){
		case 1:
			return "Sun";
		case 2:
			return "Mon";
		case 3:
			return "Tue";
		case 4:
			return "Wed";
		case 5:
			return "Thu";
		case 6:
			return "Fri";
		case 7:
			return "Sat";
		default:
			return "";
	}
}
%>
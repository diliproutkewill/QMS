<!-- 
File				: BuyRatesUpLoad.jsp
Sub-module		    : Quote Reports
Module			    : QMS
Author              : RamaKrishna Y.
date				: 16-09-2005

Modified Date	Modified By			Reason
-->

<%@ page language = "java"
		 import   = "java.util.ArrayList,
					com.foursoft.esupply.common.java.ErrorMessage,
					com.foursoft.esupply.common.java.KeyValue,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					org.apache.log4j.Logger"
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME  =  "QMSQuoteEscalatedReport.jsp"; %>

<%
    logger  = Logger.getLogger(FILE_NAME);	
         ArrayList       keyValueList      =   null;
		 ErrorMessage    message           =   null;
		 String          operation         =   request.getParameter("Operation");
try
{

%>

<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>
function numberFilter(input)
  {
        
		var s = input.value.toUpperCase();
        filteredValues = "1234567890";	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) != -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Enter only numbers ");
                input.focus();
				input.select();
                return false;
            }
        }
        return true;
}
</script>
<script language='javaScript' src='../html/eSupply.js'>
</script>

  <body>
	<form method='POST' action='QMSReportController' >
	 
	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="99%" border="0" ><tr class='formlabel'>
			      <td> Quotes Awaiting Approval </td>
			      <td align=right>QS1013511</td></tr></table></td>
			  </tr>
		    </table>
			  
        <table width="100%"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>
			    <td colspan=3>&nbsp;
				</td>
				
		  </tr>
			  <tr valign="top" class='formdata'>
			    
            <td width="186" >Approval Required By: </td>
				
            <td width="77"><input name="radiobutton" type="radio" value="self" checked>
              Self</td>
				
            <td width="507"> <input name="radiobutton" type="radio" value="others"> 
              Others </td>
		  </tr>
			  <tr valign="top" class='formdata'>
			    <td  >Output Format:<br>
			      <select name='format' class='select' size=1>
                    <option value="Html">HTML</option>
                    <option value="Excel">EXCEL</option>
				   <td colspan="2">Report Per Page:<br>
  		          <input type='text' class='text' name='noofrecords' size='4' maxlength=4 onBlur="numberFilter(document.forms[0].noofrecords)"></td>
                  </select> </td>
	      </tr>
			
			  <tr valign="top" class='denotes'>
			    <td colspan="2"><font color="#ff0000">*</font> Denotes Mandatory </td>
			    <td align="right"><input type='submit' class='input'  name='b1' value='Next>>'></td>
				<input type='hidden' class='input'  name='subOperation' value='html/excel'></td>
				<input type='hidden' class='input'  name='Operation' value='<%=operation%>'></td>
	      </tr>
			</table>
	</td>
		</tr>
	  </table>
	  
	</form>
  </body>
</html>
<%}catch(Exception exp){
		exp.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSQuoteEscalatedReport.jsp file ",exp.toString());
    logger.error(FILE_NAME+"Error in QMSQuoteEscalatedReport.jsp file "+exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","QMSQuoteEscalatedReport.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
	}
%>
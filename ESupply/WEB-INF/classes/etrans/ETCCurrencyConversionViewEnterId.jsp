<%
/*
	Program Name	:ETACurrencyConversionViewEnterId.jsp
	Module Name		:ETrans
	Task			:CurrencyConversion Master
	Sub Task		:ViewEnterId
	Author Name		:Subba Reddy V
	Date Started	:September 12,2001
	Date Completed	:September 13,2001
	Date Modified	:July 1,2003 by Ramesh Kumar.P
	Description		:This file main purpose is to get View record from the database.
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"
					 
%>

<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCurrencyConversionViewEnterId.jsp ";
%>

<%-- Starts Here  --%>
<%
/**
* This file is invoked when user selects View of CurrencyConversion from Menu bar of  main Tree structure .This file is 
* used to view the  Conversion Factors between based  Currency1 i.e HCurrency of companyinfo of login TerminalId.
* User  can be select currency ids from the list  provided .On submission of this file  CurrencyConversionViewProcess.jsp is called.
* This file will interacts with SetUpSessionBean and then calls the method getCurrencyList(companyid),
*  which inturn retrive the details.These details are then set to the respective varaibles through Object CurrencyConversionObj. 
**/

	if(loginbean.getTerminalId()==null)
	{
%>
		<jsp:forward page="../ESupplyLogin.jsp?userType=ETS" />
<%
	}
  logger  = Logger.getLogger(FILE_NAME);	
	Vector 	currencyId	 =	null;
	int		len			 =	0;
	int		firstIndex	 =	0;
	int		lastIndex	 =	0;
	String 	str			 =	null;
	String	str1		 =	null;	
	String baseCurrency		=	request.getParameter("baseCurrency");
	String radioChkd		=	request.getParameter("R1");
	String currStr			=	"";
	String	operation		=	null;
	int		len1			=	0;
	String value1			=	"No Currencys are Available";
	try
	{
		operation	= (String)session.getAttribute("Operation");
		InitialContext initial					= new InitialContext();
		SetUpSessionHome 	home 	= ( SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
		SetUpSession 		remote	= (SetUpSession)home.create();
		
		if(operation.equals("Add"))
		{	
			currencyId = remote.getSelectedCurrency(baseCurrency,radioChkd,loginbean.getTerminalId(),operation);//ADDED BY RK
		}
		else if(operation.equals("Modify"))
		{	
			currencyId = remote.getModifiedCurrencyList(baseCurrency,radioChkd ,loginbean.getTerminalId(),operation);//ADDED BY RK
		}
		else
		{
			currencyId	= remote.getCurrencyList1("ALL");
		}
		len1=currencyId.size();
		
%>

<html>
<head>
<title>Currency Conversion Module</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
// This function is used to select list of values (currencies) from the list provided .

	function addSelectedItemsToParent() 
	{
		self.opener.addToParentList(window.document.forms[0].destList);
		window.close();
	}

	function fillInitialDestList() 
	{
		var destList = window.document.forms[0].destList;
		var srcList = document.forms[0].parentList;
		for (var count = destList.options.length - 1; count >= 0; count--) 
		{
			destList.options[count] = null;
		}
		for(var i = 0; i < srcList.options.length; i++) 
		{
		if (srcList.options[i] != null)
			destList.options[i] = new Option(srcList.options[i].text);
		}
	}

// This function is used to add this list to right side list box.
	function moveDestSelectedRecords(objSourceElement,objTargetElement) 
	{
		//objTargetElement = window.document.forms[0].destList;
		//objSourceElement  = window.document.forms[0].srcList;

		var aryTempSourceOptions = new Array();        var x = 0;                //looping through source element to find selected options        
			for (var i = 0; i < objSourceElement.length; i++) {   
					if (objSourceElement.options[i].selected) {               
							//need to move this option to target element                
							var intTargetLen = objTargetElement.length++; 
							objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;                
							objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;           
					}
					else
					{               
						//storing options that stay to recreate select element          
						var objTempValues = new Object(); 
						objTempValues.text = objSourceElement.options[i].text;
						objTempValues.value = objSourceElement.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
					}
			}
			//resetting length of source 

			objSourceElement.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				objSourceElement.options[i].text = aryTempSourceOptions[i].text;  
				objSourceElement.options[i].value = aryTempSourceOptions[i].value; 
				objSourceElement.options[i].selected = false;  
			}
	}

// This function is used to the delete the select value from right side.
	function deleteFromDestList() 
	{
		var destList  = window.document.forms[0].destList;
		var len = destList.length;
		for(var i = (len-1); i >= 0; i--) 
		{
			if((destList.options[i] != null) && (destList.options[i].selected == true)) 
			{
				destList.options[i] = null;
			}
		}
	}

	function checkForEmpty()
	{
		var len   =   window.document.forms[0].destList.length;
		if(len == 0)
		{
			alert("Please select atleast one currency value");
			window.document.forms[0].srcList.focus();
			return false;
		}
		return true;
	}
</script>

<script language="JavaScript">
	function small_window(myurl)
		{
			var newWindow;
			var props = 'scrollBars=yes,resizable=yes,toolbar=no,menubar=no,location=no,directories=no,width=360,height=360';
			newWindow = window.open(myurl, "Add_from_Src_to_Dest", props);
		}

	function addToParentList(sourceList) 
		{
			destinationList = window.document.forms[0].parentList;
			for(var count = destinationList.options.length - 1; count >= 0; count--) 
			{
				destinationList.options[count] = null;
			}
			for(var i = 0; i < sourceList.options.length; i++) 
			{
				if (sourceList.options[i] != null)
				destinationList.options[i] = new Option(sourceList.options[i].value, sourceList.options[i].value );
			}
		}

	function selectList(sourceList) 
		{
			sourceList = window.document.forms[0].parentList;
			for(var i = 0; i < sourceList.options.length; i++) 
			{
				if (sourceList.options[i] != null)
				sourceList.options[i].selected = true;
			}
			return true;
		}

	function deleteSelectedItemsFromList(sourceList) 
		{
			var maxCnt = sourceList.options.length;
			for(var i = 0;i<=maxCnt-1;i++) 
			{
			if((sourceList.options[i] != null) && (sourceList.options[i].selected == true)) 
			{
				sourceList.options[i] = null;
			}
			}
		}

// This function is used to show the values of right side list box.
	function setCountryId()
		{
			if( document.form1.ids.selectedIndex == -1 )
			{
				alert("Please select a CurrencyId");
			}
			else
			{
				temp1 = document.form1.ids.options[ document.form1.ids.selectedIndex ].value;
				idx = temp1.indexOf('(');
				window.opener.document.forms[0].countryId.value = temp1.substring(idx+2,idx+4);
				window.close();
			}
		}

	function show()
		{
			str=document.forms[0].destList.options.length
			document.forms[0].hide.value=str
			stlist = new Array();
			for(i=0;i<str;i++)
			{
				stlist[i]= document.forms[0].destList.options[i].value;
			}
			if(stlist=='')
				document.forms[0].hide1.value=' ';
			else
				document.forms[0].hide1.value=stlist.toString();
		}

	function populate()
		{
<%
		  for(int i=0;i<len;i++)
		   {
			 str  = currencyId.elementAt(i).toString(); 
			 firstIndex = str.indexOf("[");
			 str1 = str.substring(0,firstIndex-1);
%>
			document.forms[0].srcList.options[ <%= i %> ] = new Option('<%=str1%>','<%=str1%>');
<%
			}
%>
		}

	function setCurrencyValues()
		{
			if(document.forms[0].destList.selectedIndex == 0)
			{
				alert("Please select a CurrencyId")
			}
			else
			{
				var index	=	document.forms[0].destList.selectedIndex;
				firstTemp	=	document.forms[0].destList.options[index].value
				temp	=	firstTemp.toString();
				window.opener.document.forms[0].sltdCurrency.value=temp;
				window.close();
			}
		}

	function setCurrencyId()
	{
			if(document.forms[0].destList.options.length == 0)
			{
				alert("Please select atleast one CurrencyId");
			}
			else
			{
				var len = document.forms[0].destList.options.length;
				str = "";
				for(i=0;i<len;i++)
				{
					if(document.forms[0].destList.options[i])
					{
						str = str + "-" + document.forms[0].destList.options[i].value ;
					}
				}
					opener.hf = str;
					opener.assignLocations();	
					self.close();
			}		
	}
	
	function assignLocations()
{
	var len1= window.document.forms[0].sltdCurrency.options.length;
	var index=0;
	for(var i=0;i<len1;i++)
	{
		window.document.forms[0].sltdCurrency.options.remove(index);
	}
	str = hf;	
	entries = str.split("-");
	for(i=0;i<entries.length;i++)
	{
		if(entries[i] != "-" && entries[i]!="")
		{
			window.document.forms[0].sltdCurrency.options[index] = new Option(entries[i] ,entries[i] )	
			index++;
		}
	}
	len = document.forms[0].sltdCurrency.options.length;
	currencyList = new Array();
	for(i=0;i<len;i++)
	{
		currencyList[i] = document.forms[0].sltdCurrency.options[i].length;
	}
	if(document.forms[0].sltdCurrency.options.length > 0)
	{
		var terId = document.forms[0].sltdCurrency.options[0].value
		document.forms[0].currencyIdHide.value = currencyList.toString();
	}
	else
	{
		document.forms[0].currencyIdHide.value = '';
	}
}
</script>
</head>
<body  onLoad="populate()">
<form method="POST" >
<table width="325" border="0" cellspacing="0" cellpadding="0">
<tr valign="top" bgcolor="#FFFFFF"> 
<td>
<table width="325">
<%
	if(len1 >0)
	{
%>
		<tr valign="top" class="formlabel"> 
		<td colspan="3"><center>Currency Select LOV </center></td>
		</tr>
		<tr class='formdata'>
		<td colspan="3">&nbsp;</td>
		
		</tr>
		<tr class="formheader" align="center" valign="top"> 
		<td width="100" >Available 
		</td>
		<td width="125">
		</td>
		<td width="100">Selected
		</td>
		</tr>
		<tr valign="top" class="formdata" align="center"> 
		<td width="100" > 
		<select size="10" name="srcList" multiple class="select">
		<%
			if(currencyId != null)
			{	
			 for(int i=0;i<currencyId.size();i++)
			 {
			  currStr=(String)currencyId.elementAt(i);	
		%>
			  <option  value="<%=currStr%>"><%=currStr%></option>
		<%
			 }
		   }
		%>
		</select>
		</td>
		<td  width="125"> 
		<br>
		<br>
		<br>
		<input type="button" value=" >> " onClick="javascript:moveDestSelectedRecords(document.forms[0].srcList,document.forms[0].destList)" name="button" class="input">
		<br>
		<br>
		<input type="button" value=" << " onClick="javascript:moveDestSelectedRecords(document.forms[0].destList,document.forms[0].srcList);" name="button" class="input">
		</td>
		<td  width="150"> 
		<select size="10" name="destList" multiple  class="select">
		</select>
		</td>
		</tr>
		<tr align="center"> 
		<td width="100" ></td>
		<td width="125" align="center"> 
		<input type="button" value=" Ok " name="showbutton" onClick="setCurrencyId()" class="input">&nbsp;
		<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input">
		<input type ="hidden" name="hide" value="">
		<input type ="hidden" name="hide1" value="">
		</td>
		<td width="100"></td>
		</tr>
<%
	}
	else
	{
%>
		<table width="325" border="0" cellspacing="0" cellpadding="0">
		<tr valign="top" class="formdata"> 
		<td colspan="3"><center><b>Currency Select LOV</b> </center>
		<center>
			<textarea rows=6 name="ta" cols="30" class="select"><%= value1 %></textarea>
		</center>
		<br>
		<center>
			<input type="button" value="OK" name="B2" onClick="window.close()" class="input">
		</center>
		</td>
		</tr>
		</table>
<%
	}
%>
</table>
</td>
</tr>
</table>
</form>
</body>
</html>
<%
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME,"ETCCurrencyConversionViewEnterId.jsp : Exception : ", exp.toString());   
    logger.error(FILE_NAME+"ETCCurrencyConversionViewEnterId.jsp : Exception : "+ exp.toString());   
	}
%>
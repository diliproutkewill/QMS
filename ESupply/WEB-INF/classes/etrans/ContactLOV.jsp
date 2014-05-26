<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--

	Program Name	:ContactLOV.jsp
	Module Name		:DHLQMS
	Task			:ContactNames
	Sub Task		:ContactNamesLOV	
	Author Name		:K.NareshKumarReddy
	Date Started	:3 September 2005 
	Date Completed	:3 September 2005 
	Date Modified	:
		

--%>

<%@ page import="javax.naming.InitialContext,
					javax.naming.Context,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome" %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ContactLOV.jsp";
%>
<jsp:useBean id="loginbean" class ="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope ="session"/>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ArrayList ids			=	null; //Vector to store ids
	int len					=	0;	 // variable to store ids size
	String whereClause		= 	null;	
	String customerId		=	null;    
    String address          =   null;
	String flag             =   null;
	String quoteId			=	null;
	
// Changes are done by Nageswara Rao.D
	try
	{
		
		flag  =  request.getParameter("flag");
		
		
		if(request.getParameter("custId")!=null)
			customerId	=	request.getParameter("custId");
		else
			customerId	=	"";
       
		if(request.getParameter("quoteId")!=null && request.getParameter("quoteId").trim().length()>0)
			//quoteId		=	Long.parseLong(request.getParameter("quoteId"));
            quoteId		=	request.getParameter("quoteId");

		if(ids==null)
		{
			Context initial						= 	new InitialContext(); // variable to get initial context for JNDI
			SetUpSessionHome home		=	(SetUpSessionHome) loginbean.getEjbHome("SetUpSessionBean");	
			SetUpSession remote			=	(SetUpSession)home.create();
			//System.out.println("flagflagflagflagflagflag :::: "+flag);
			if(flag!=null)
            {
				  if("Costing".equalsIgnoreCase(flag))
				  {
						ids		=	remote.getCostingContactNames(customerId,quoteId);
				  }
				  else
				  {

					  if(request.getParameter("address")!=null)
						address =request.getParameter("address");
					  else
						address ="";

					  ids							=	remote.getContactNames(customerId,address);		
				  }
			}
			else
			{
			  ids							=	remote.getContactNames(customerId);			
			}
			

			if(ids != null);
				len	=	ids.size();
			
		}
	}
	catch(Exception ee)
	{
	//Logger.error(FILE_NAME,"Error in ContactLOV.jsp : ", ee.toString());
  logger.error(FILE_NAME+"Error in ContactLOV.jsp : "+ ee.toString());
	}
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
var tempArray=new Array();
function  populateids()
{	
 
 
<%		
		for( int i=0;i<len;i++ )
		{// for loop begin , this for loop is to get the ids and there description
			String str	=ids.get(i).toString(); 

			
%>

       //@@Modified by Kameswari for the WPBN issue-133569   
		
			val2='<%=str.substring(0,str.indexOf(","))%>'+'['+'<%=str.substring(str.indexOf(",")+1,str.lastIndexOf(","))%>'+']';	
		     
		   val	= '<%=str.substring(0,str.indexOf(","))%>';
		
			val1	= '<%=str.substring(str.lastIndexOf(",")+1,str.length())%>';
      //@@WPBN issue-133569
	
			tempArray['<%=i%>']=val1;
<%
		if("Quote".equals(flag)){
%>
			document.forms[0].contactNames.options[ <%= i %> ] = new Option(val2,val);
<%
}
else
{
%>
			document.forms[0].contactNames.options[ <%= i %> ] = new Option(val2,val);
<%
}
%>


<%
		}//for loop end
		if(len > 0)
		{
%>
			document.forms[0].contactNames.options[0].selected = true;	
			document.forms[0].contactNames.focus();

<%
		}else{

%>
		document.forms[0].B2.focus();
<%
		}
%>
}

function setids()
{
	if(document.forms[0].contactNames.selectedIndex == -1)
	{
		alert("Please select At Least one Contact Person");
	}
	else
	{
			var count		=	0;
			for(var i=0;i<document.forms[0].contactNames.options.length;i++)
			{
				if(document.forms[0].contactNames.options[i].selected)
				{
					count++;
				}
			}
			var levelTemp = new Array(count);
			var sIndex=0;
            var contactValues="";
			var mIndex=0;
			var mailArray = new Array(count);
			var mTemp;
			for(var i=0;i<document.forms[0].contactNames.options.length;i++)
			{
				if(document.forms[0].contactNames.options[i].selected)
				{
					if(i==0)
						contactValues	=	tempArray[i];
					else
					{	
						if(contactValues.length!=0)
							contactValues=contactValues+","+tempArray[i];
						else
							contactValues	=	tempArray[i];
					}

					strTemp		=	document.forms[0].contactNames.options[i].value;
					levelTemp[sIndex]	=	strTemp;
					sIndex++;
					//added by phani sekhar
					mTemp= document.forms[0].contactNames.options[i].text;
					mailArray[mIndex]=mTemp.substring(mTemp.indexOf("[")+1,mTemp.indexOf("]"));
					mIndex++;
				}
			}
          
			window.opener.document.forms[0].contactIds.value=contactValues;//added by rk
		
		
		//firstTemp	=document.forms[0].contactNames;
         window.opener.setValues(levelTemp);
		 window.opener.setMailValues(mailArray);//added by phani sekhar
		//alert('levelTemp'+levelTemp);
		window.close();
	}
}



function onEnterKey()
{
	
	if(event.keyCode == 13)
	{
		setids();
	}
	if(event.keyCode == 27){
		window.close();
	}
}
function onEscKey(){
	if(event.keyCode == 27){
		window.close();
	}
}
function modifyOptions()
{
 if(checkemail())
	{
  var testval=document.forms[0].selectedIndex.value;
  var optionval=document.forms[0].editMail.value;
  var optiontext=document.forms[0].selectedText.value;
  optionval=document.forms[0].selectedText.value+"["+optionval+"]";
  document.forms[0].contactNames.options[ testval ] = new Option(optionval,document.forms[0].selectedText.value);
  document.forms[0].editMail.value="";
}
}
function modifyMailId()
{
	var twp;
	var result;
	for(var i=0;i<document.forms[0].contactNames.options.length;i++)
			{
				if(document.forms[0].contactNames.options[i].selected)
				{
				
					document.forms[0].selectedIndex.value=i;
					document.forms[0].selectedText.value=document.forms[0].contactNames.options[i].value;
						twp= document.forms[0].contactNames.options[i].text;
					document.forms[0].editMail.value=twp.substring(twp.indexOf("[")+1,twp.indexOf("]"));
					//alert('document.forms[0].selectedIndex.value=='+document.forms[0].selectedIndex.value+'--document.forms[0].selectedText.value--'+document.forms[0].selectedText.value+'---options value--'+document.forms[0].editMail.value);
					break;
				}
			}
}
function checkemail(){
		var testresults;
		var str=document.forms[0].editMail.value
		var filter=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i
		if (filter.test(str))
		{
		testresults=true;
		}
		else{
		alert("Please input a valid email address!");
		testresults=false;
		}
		return (testresults)
}

</script>
</head>
 <body class='formdata' onLoad="populateids()" onKeyPress='onEscKey()'>
  <form> 
   <center><b><font face="verdana" size="2"></font></b></center>
<%
	if(len > 0)
	{
%>
		<br>
		<center>
		<select size="10" name="contactNames" multiple onDblClick='setids()' onKeyPress='onEnterKey()' class="select">
		</select>
		<br><br>
		<input type="text" value="" name="editMail" onblur="modifyOptions()" class="text">
		<input type="hidden" value="" name="selectedIndex">
		<input type="hidden" value="" name="selectedText">
		<input type="button" value=" Ok " name="OK" onClick="setids()" class="input">
		<input type="button" value=" Edit " name="Edit" onClick="modifyMailId()" class="input">
		<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input">
		</center>
<%
	}
	else
	{
%>
		<br>
		<center>
		<textarea cols=30 class='select' rows = 6 readOnly >No ContactNames  available
		</textarea>
		<br><br>
		<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
		</center>
<%
	}
%>
</form>
</body>
</html>

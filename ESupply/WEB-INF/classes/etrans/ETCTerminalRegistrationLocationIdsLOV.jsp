
<%--
	Program Name	: ETCTerminalRegistrationLocationIdsLOV.jsp
	Module name		: HO Setup
	Task			: Adding Terminal
	Sub task		: to display all location Ids
	Author Name		: A.Hemanth Kumar
	Date Started	: September 11, 2001
	Date completed	: September 12, 2001
	Description      :
		This file displays all the location Ids. It allows multiple selection of location ids to add to the
		terminal registration form.

--%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector,
				 	org.apache.log4j.Logger,					
					com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTerminalRegistrationLocationIdsLOV.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	Vector							vecLocationInfo		= new Vector();    // a Vector  to store location information
	Vector							opTerminals			= new Vector();
	String[]						locationId			= null;    // a String array to store location Ids
	SetUpSessionHome	terminalHome		= null;    // a variable to store Home Object
	SetUpSession		terminalRemote		= null;    // a variable to store Remote Object
	TerminalRegJspBean				terminalReg         = null;    // a TerminalRegJspBean variable
	InitialContext					initialContext		= null;    // a variable to store InitialContext
	String							flag                = null;    // a String to store the type of operation requested
	String							terminalId          = null;    // a String to store terminal Id
	String							temp_location       = null;    // a String to store location
	int								checklen            = 0;       // an integer to store the size of a Vector
	String terminalType	= request.getParameter("terminalType");
	String adminType	= request.getParameter("adminType");
	String idLike		= request.getParameter("idLike");
	String terminal		= request.getParameter("terminalId");
	String shipmentMode = request.getParameter("shipmentMode");
	String fetchTerminalType = request.getParameter("fetchTerminalType");
	String countryId		=	request.getParameter("countryId");
	String companyId		=	request.getParameter("companyId");


	String label		= "Location Id ";
	
	//Logger.info(FILE_NAME,"ShipmentMode is " + shipmentMode);
	//Logger.info(FILE_NAME,"countryId is " + countryId);
	
		
	try
	{


			if(terminalType!=null && !terminalType.equals("O"))
				label	= "Terminal Id";
	
			if(adminType==null || adminType.equals("null"))
				adminType="A";
		
			initialContext  = new InitialContext();
			terminalHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote  = (SetUpSession)terminalHome.create();
			
			//Logger.info(FILE_NAME,"terminalType..."+terminalType);	
			//Logger.info(FILE_NAME,"loginbean.getAccessType()..."+loginbean.getAccessType());	
						
			if(terminalType!=null && !terminalType.equals("O"))
			{
				if( loginbean.getAccessType().equals("LICENSEE")||
					loginbean.getAccessType().equals("HO_TERMINAL")|| 
					loginbean.getAccessType().equals("ADMN_TERMINAL"))		
				{

					vecLocationInfo 	= terminalRemote.getOperationTerminalInfo(terminal,loginbean,adminType,idLike,fetchTerminalType);
					//vecLocationInfo 	= terminalRemote.getOperationTerminalInfo(terminal,loginbean,adminType,idLike);
				}
			//	else if	(loginbean.getAccessType()!=null && loginbean.getAccessType().equals("HO_TERMINAL"))
			//		vecLocationInfo 	= terminalRemote.getOperationTerminalInfo(terminal,loginbean,adminType,idLike);	
			//	else if	(loginbean.getAccessType()!=null && loginbean.getAccessType().equals("ADMN_TERMINAL"))
				//	vecLocationInfo 	= terminalRemote.getOperationTerminalInfo(terminal,loginbean,adminType,idLike);	
			}
			else
			{
			  	vecLocationInfo		= terminalRemote.getLocationInfo(shipmentMode,countryId,companyId);
			}
			
			
			// if not the Vector is null
			if(vecLocationInfo != null)
				checklen = vecLocationInfo.size();
			flag = request.getParameter("flag");
			
			// checking the type of operation
			
			if(flag != null && flag.equals("Modify"))
			{
				if(request.getParameter("terminalid") != null)
				{
					terminalId		= request.getParameter("terminalid");
					terminalReg		=  terminalRemote.getTerminalRegDetails(request.getParameter("terminalid"),terminalType,loginbean);
					locationId		= terminalReg.getLocationId();
					temp_location	= request.getParameter("loc");
				}
			}
			if(flag != null && flag.equals("Add"))
			{
				temp_location = request.getParameter("loc");
			}

	}
	catch(Exception nexp)
	{
		//Logger.error(FILE_NAME,"Error in TerminalRegistrationView.jsp file ", nexp.toString());
    logger.error(FILE_NAME+"Error in TerminalRegistrationView.jsp file "+ nexp.toString());
	}
if(checklen > 0 || (locationId!=null && locationId.length >0) )
//if(checklen > 0)
{
%>
<html>
<head>
<title>Select</title>
 <link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
var operation = '<%=flag%>';

	function populateLocationInfo()
	{
		var temp_loc = ""
<%
		if(flag != null && flag.equals("Add"))
		{
%>
			temp_loc = '<%= temp_location%>'
			var len1= window.document.forms[0].selectedLocationIds.length;
			var index=0;
			for(var i=0;i<len1;i++)
			{
				window.document.forms[0].selectedLocationIds.options.remove(index);
			}
			entries = temp_loc.split("-");
			for(i=0;i<entries.length;i++)
			{
				if(entries[i] != "-" && entries[i]!="")
				{
					window.document.forms[0].selectedLocationIds.options[index] = new Option(entries[i] ,entries[i] )
					index++;
				}
			}
<%
		}
%>
<%
		try
		{
			if( vecLocationInfo!=null )
			{
				int length = vecLocationInfo.size();
				for(int i=0;i<length;i++)
				{
					String str = vecLocationInfo.elementAt(i).toString();
					int index =  str.indexOf("%");
					String strLocationId = str.substring(0,index);
%>
					document.forms[0].locationIds.options[<%=i%>] = new Option('<%=strLocationId%>','<%=strLocationId%>');
<%
				}
			}
			if(flag != null && flag.equals("Modify"))
			{
				if( locationId!=null )
				{
					for(int i=0;i<locationId.length;i++)
					{
%>
						document.forms[0].selectedLocationIds.options[<%=i%>] = new Option('<%=locationId[i]%>','<%=locationId[i]%>');
<%
					}
				}
%>
				temp_loc = '<%= temp_location%>'
				var len1= window.document.forms[0].selectedLocationIds.length;
				var index=0;
				for(var i=0;i<len1;i++)
				{
					window.document.forms[0].selectedLocationIds.options.remove(index);
				}
				entries = temp_loc.split("-");
				for(i=0;i<entries.length;i++)
				{
					if(entries[i] != "-" && entries[i]!="")
					{
						window.document.forms[0].selectedLocationIds.options[index] = new Option(entries[i] ,entries[i] )
						index++;
					}
				}
<%
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in TerminalInformationLocationLov.jsp " , e.toString());
      logger.error(FILE_NAME+"Exception in TerminalInformationLocationLov.jsp " + e.toString());
		}
%>
}
	function addSrcToDestList()
	{
		var aryTempSourceOptions = new Array(); 
		selectedLocationIds = window.document.forms[0].selectedLocationIds;
		locationIds = window.document.forms[0].locationIds;
		var len = selectedLocationIds.length;
		var x=0;
		for(var i = 0; i < locationIds.length; i++)
		{
			if ((locationIds.options[i] != null) && (locationIds.options[i].selected))
			{
				//@@Commented by kameswari for WPBN - 14937
				/*var found = false;
				for(var count = 0; count < len; count++)
				{
					if (selectedLocationIds.options[count] != null)
					{
						if (locationIds.options[i].text == selectedLocationIds.options[count].text)
						{
							found = true;
							break;
						}
					}
				}
				if (found != true)
				{

					selectedLocationIds.options[len] = new Option(locationIds.options[i].text,locationIds.options[i].value);
					locationIds.options[i].selected = false;
					len++;
					locationIds.options[i] = null;
				}*/
				//@@WPBN - 14937
               selectedLocationIds.options[len] = new Option(locationIds.options[i].text,locationIds.options[i].value);
			   len++;
			}//@@added by kameswari for WPBN-14937
			else
			{
			var objTempValues = new Object(); 
						objTempValues.text = locationIds.options[i].text;
						objTempValues.value = locationIds.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
			}
		}
			locationIds.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				locationIds.options[i].text = aryTempSourceOptions[i].text;  
				locationIds.options[i].value = aryTempSourceOptions[i].value; 
				locationIds.options[i].selected = false;  
			}//@@WPBN-14937
	}

	function deleteFromDestList()
	{
		 	var aryTempSourceOptions = new Array(); 
	      var x=0;
			var locationIds = window.document.forms[0].locationIds;
			var locLen  = locationIds.length;
			var selectedLocationIds  = window.document.forms[0].selectedLocationIds;
			var len = selectedLocationIds.length;
<%
		if(flag != null && flag.equals("Modify"))
		{
%>
	      
			
			var scrTerminalId1 = '<%=terminalId%>' ;
			for(var i = 0; i <len; i++)
			{
				if ((selectedLocationIds.options[i] != null) && (selectedLocationIds.options[i].selected == true))
				{
					if( selectedLocationIds.options[i].value == scrTerminalId1.substring(3,6) )
					{
						locstore = selectedLocationIds.options[i].value
						alert("You cannot delete  ( " +locstore+" ), this Location is associated with the Terminal ");
					}
					else
					{
						//@@Commented by kameswari for WPBN-14937
					/*	var found = false;
						for(var count = 0; count < locLen; count++)
						{
							if (locationIds.options[count] != null)
							{
								if (locationIds.options[count].text == destList.options[i].text)
								{
									found = true;
									break;
								}
							}
						}
						if(!found)
						{
							locationIds.options[locLen] = new Option(destList.options[i].text,destList.options[i].value);	
						destList.options[i] = null;
					}*/
					//@@WPBN-14937
					 locationIds.options[locLen] = new Option(selectedLocationIds.options[i].text,selectedLocationIds.options[i].value);
			   locLen++;
					}//@@WPBN-14937

				}//@@added by kameswari for WPBN-14937
				else
				{
                	var objTempValues = new Object(); 
						objTempValues.text = selectedLocationIds.options[i].text;
						objTempValues.value = selectedLocationIds.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;   
				}
			
			}
		selectedLocationIds.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				selectedLocationIds.options[i].text = aryTempSourceOptions[i].text;  
				selectedLocationIds.options[i].value = aryTempSourceOptions[i].value; 
				selectedLocationIds.options[i].selected = false;  
			}//@@WPBN-14937
<%
		}
		else
		{
	%>		
	
		for(var i = 0; i < selectedLocationIds.length; i++)
		{
			if ((selectedLocationIds.options[i] != null) && (selectedLocationIds.options[i].selected))
			{
             locationIds.options[len] = new Option(selectedLocationIds.options[i].text,selectedLocationIds.options[i].value);
			   len++;
			}//@@added by kameswari for WPBN-14937
			else
			{
			var objTempValues = new Object(); 
						objTempValues.text = selectedLocationIds.options[i].text;
						objTempValues.value = selectedLocationIds.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
			}
		}
			selectedLocationIds.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				selectedLocationIds.options[i].text = aryTempSourceOptions[i].text;  
				selectedLocationIds.options[i].value = aryTempSourceOptions[i].value; 
				selectedLocationIds.options[i].selected = false;  
			}//@@WPBN-14937


//@@Commented by kameswari for WPBN-14937

		/*var destList  = window.document.forms[0].selectedLocationIds;
			var len = destList.length;
			for(var i = (len-1); i >= 0; i--)
			{
				if ((destList.options[i] != null) && (destList.options[i].selected == true))
				{
					destList.options[i] = null;
				}
			}*/
			//@@WPBN-14937
<%
		}
%>
	}
var label 		= '<%=label%>';	
var terminalType= '<%=terminalType%>';
var companyId	= '<%=idLike%>';

function setLocationId()
{
	var len=document.forms[0].selectedLocationIds.length;
	str = "";
	
	if(len==0 && operation=='Add' && terminalType!='A')
	{
		alert('Please select a '+label)
		return false;
	}
	
	for(i=0;i<len;i++)
	{
		if(document.forms[0].selectedLocationIds.options[i])
		{
			str = str + "-" + document.forms[0].selectedLocationIds.options[i].value ;
		}
	}
	opener.hf 	  = str;
	opener.compId = companyId;
	opener.assignLocations();
	self.close();
}

</script>
</head>
<body class='formdata' onLoad=populateLocationInfo() >
<form>
  <b><font size="5">
  <center>
    <font size="2" face="Verdana"><%=label%>s</font>
  </center>
  </font></b>
	<center>
       <table border="0" cellpadding="2" align="center" width="303" height="252">
          <tr class='formdata'>
            <td width="114" height="246">
              <div align="right">
				<select size="12" name="locationIds" multiple class='select'>
				</select>
              </div>
            </td>
            <td width="123" height="246">
              <div align="center">
					<input type="button" value=" >> " onClick="addSrcToDestList()" class='input'>
              </div>
              <p align="center">
					<input type="button" value=" << " onclick="deleteFromDestList()" class='input'>
              </p>
            </td>
            <td width="108" height="246">
              <div align="left">
					<select size="12" name="selectedLocationIds" selected=0 multiple class='select'></select>
              </div>
            </td>
          </tr>
          <input type="hidden" name="terminalhide">
        </table>
	</center>
  <div align="center"><br>
    <input type="button" value=" Ok " name="B1" onClick=setLocationId() class='input'>
	<input type="button" value="Cancel" name="B2" onClick="window.close()" class='input'>
  </div>
</form>
</body>
</html>
<%
 }
 else
 {
%>
<html>
 <head>
  <title>Select</title>
 <link rel="stylesheet" href="../ESFoursoft_css.jsp">
   </head>
    <body class='formdata'  >
     <b><font size="5">
      <center>
       <font size="2" face="Verdana"><%=label%>s</font>
        </center>
         </font>
		  </b>
	       <center>
		    <textarea rows=6 cols=30 readonly class='select'>No <%=label%>s are  available.
		 </textarea>
		</center>
	   <br>
	  <br>
	 <center>
	<input type=button value=" Ok " onClick="window.close()" class='input'>
   </center>
  </body>
 </html>
<%
 }
%>

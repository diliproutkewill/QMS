
<%--
	Program Name	: ETCCBTTerminalRegistrationLocationIdsLOV.jsp
	Module name		: HO Setup
	Task			: Adding CBT Terminal
	Sub task		: to display all location Ids
	Author Name		: K.N.V.Prasada Reddy
	Date Started	: July 6, 2003
	Date completed	: July 7, 2003
	Description      :
		This file displays all the Terminal Ids Under the HO or ADMIN Terminal . It allows multiple selection of Terminal ids to add It as a 
		CBT Terminals.

--%>

<%@ page import	=	"java.util.StringTokenizer "%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
	private static final String FILE_NAME	=	"ETCCBTTerminalRegistrationLocationIdsLOV.jsp ";
%>

<%
	String							vecLocationInfo		= null;    // a Vector  to store location information
	String							temp_location       = null;    // a String to store location

	
	 vecLocationInfo  = request.getParameter("loc");
     temp_location	= request.getParameter("cbtloc");

		
%>
<html>
<head>
<title>Select</title>
 <link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>


	function populateLocationInfo()
	{
		var temp_loc = ""
			temp_loc = '<%= temp_location%>'
			var len1= window.document.forms[0].selectedLocationIds.length;
			var index=0;
			for(var i=0;i<len1;i++)
			{
				window.document.forms[0].selectedLocationIds.options.remove(index);
			}
			entries = temp_loc.split(",");
			for(i=0;i<entries.length;i++)
			{
				if(entries[i] != "," && entries[i]!="")
				{
					window.document.forms[0].selectedLocationIds.options[index] = new Option(entries[i] ,entries[i] )
					index++;
				}
			}

<%
		try
		{
	       
			if( vecLocationInfo!=null )
			{

    			StringTokenizer stToken = new StringTokenizer(vecLocationInfo,",");
				int i = 0;
				while (stToken.hasMoreTokens())
				{
					String str = stToken.nextToken();
					
%>
					document.forms[0].locationIds.options[<%=i%>] = new Option('<%=str%>','<%=str%>');
<%
	            i++;
				}
			}
			
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in ETCCBTTerminalRegistrationLocationIdsLOV.jsp " , e.toString());
			System.out.println(FILE_NAME +"Exception in ETCCBTTerminalRegistrationLocationIdsLOV.jsp " + e.toString());
		}
%>
}
	function addSrcToDestList()
	{
		selectedLocationIds = window.document.forms[0].selectedLocationIds;
		locationIds = window.document.forms[0].locationIds;
		var len = selectedLocationIds.length;
		for(var i = 0; i < locationIds.length; i++)
		{
			if ((locationIds.options[i] != null) && (locationIds.options[i].selected))
			{
				var found = false;
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
					len++;
				}
			}
		}
	}

	function deleteFromDestList()
	{

			var destList  = window.document.forms[0].selectedLocationIds;
			var len = destList.length;
			for(var i = (len-1); i >= 0; i--)
			{
				if ((destList.options[i] != null) && (destList.options[i].selected == true))
				{
					destList.options[i] = null;
				}
			}

	}

function setLocationId()
{
	var len=document.forms[0].selectedLocationIds.length;
	str = "";
	
	if(len==0 )
	{
		alert('Please select a Terminas')
		return false;
	}
	
	for(i=0;i<len;i++)
	{
		if(document.forms[0].selectedLocationIds.options[i])
		{
			str = str + "-" + document.forms[0].selectedLocationIds.options[i].value ;
		}
	}
	opener.cbthf 	  = str;
	opener.cbtassignLocations();
	self.close();
}

</script>
</head>
<body class='formdata' onLoad=populateLocationInfo() >
<form>
  <b><font size="5">
  <center>
    <font size="2" face="Verdana">Terminals</font>
  </center>
  </font></b>
	<center>
       <table border="0" cellpadding="2" align="center" width="303" height="252">
          <tr class='formdata'>
            <td width="114" height="246">
              <div align="right">
				<select size="12" name="locationIds" selected=0  multiple class='select'>
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

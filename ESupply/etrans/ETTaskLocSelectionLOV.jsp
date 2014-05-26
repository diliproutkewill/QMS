
<%--
	Program Name	  : ETTaskLocSelectionLOV.jsp
	Module name		  : ServiceLevel 
	Task		      : Adding Task Event
	Sub task		  : to display all Location Ids
	Author Name		  : K.N.V.Prasada Reddy
	Date Started	  : Feb 19th, 2003
	Date completed	  : Feb 24, 2003
	Description       :
		This file displays all the Location Ids for Task Plan. It allows multiple selection of location ids to add to the
		Service Level registration form.
   
--%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.Vector,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    java.util.ArrayList,
                com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETTaskLocSelectionLOV.jsp ";
%>

<%  
	logger  = Logger.getLogger(FILE_NAME);	
	ArrayList						locationId			= null;    // a String array to store location Ids
	SetUpSessionHome	taskHome		= null;    // a variable to store Home Object
	SetUpSession		taskRemote		= null;    // a variable to store Remote Object
	InitialContext					initialContext		= null;    // a variable to store InitialContext
	String searchString= request.getParameter("searchString");
	String searchString1 =request.getParameter("searchString1");
	String operation	= request.getParameter("operation");
	
		
	try
	{
    	initialContext  = new InitialContext();
			taskHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			taskRemote  = (SetUpSession)taskHome.create();
		  locationId 	= taskRemote.getLocationIds(searchString,loginbean.getTerminalId(),operation,"");
			}
	catch(Exception nexp)
	{
		//Logger.error(FILE_NAME,"Error in ETaskLocSelectionLOV.jsp file ", nexp.toString());
    logger.error(FILE_NAME+"Error in ETaskLocSelectionLOV.jsp file "+ nexp.toString());
	}

if(locationId!=null)
{
 %>
    <html>
    <head>
    <title>Select</title>
     <link rel="stylesheet" href="../ESFoursoft_css.jsp">
    <script language="JavaScript">
      function populateLocationInfo()
      {
  
      var index=0;  	
      var searchString='<%=searchString%>';
    <%
        try
        {
        if( locationId!=null )
          {  int j=0;
            int length = locationId.size();
            for(int i=0;i<length;i++)
            {
              String str = (String)locationId.get(i);
		     if(searchString1.indexOf(str)<0)
				{
    %>
              document.forms[0].locationIds.options[<%=j%>] = new Option('<%=str%>','<%=str%>');
    <%    j++;    
		  }
	        
            }
          }
    %>     if (searchString!='') 
            {
            entries = searchString.split("-");
            for(i=0;i<entries.length;i++)
              {
               if(entries[i] != "-" && entries[i]!="")
                {
                 window.document.forms[0].selectedLocationIds.options[index] = new Option(entries[i] ,entries[i] );
                  index++;
                 }
               }
            }
      <%	
        }
        catch(Exception e)
        {
          //Logger.error(FILE_NAME,"Exception in TerminalInformationLocationLov.jsp " , e.toString());
          logger.error(FILE_NAME+"Exception in TerminalInformationLocationLov.jsp " + e.toString());
        }
    %>
    }
      /*function addSrcToDestList()
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
      }*/

	  function moveDestSelectedRecords(objSourceElement, objTargetElement)    {    
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
      var str1="";
	

      for(i=0;i<len;i++)
      {
        if(document.forms[0].selectedLocationIds.options[i])
        {
		  str1=document.forms[0].selectedLocationIds.options[i].value;
		  str1=str1.substr(0,3);
          str = str + "-" + str1;
        }
      }
      opener.hf=str;
      opener.operation='<%=operation%>';
      opener.assignLocations();
      self.close();
    }
    </script>
    </head>
    <body class='formdata' onLoad=populateLocationInfo() >
    <form>
      <b><font size="5">
      <center>
        <font size="2" face="Verdana">Select Locations</font>
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
              <input type="button" value=" >> " onClick="moveDestSelectedRecords(document.forms[0].locationIds,document.forms[0].selectedLocationIds)" class='input'>
                  </div>
                  <p align="center">
              <input type="button" value=" << " onclick="moveDestSelectedRecords(document.forms[0].selectedLocationIds,document.forms[0].locationIds)" class='input'>
                  </p>
                </td>
                <td width="108" height="246">
                  <div align="left">
              <select size="12" name="selectedLocationIds" selected=0 multiple class='select'></select>
                  </div>
                </td>
              </tr>
         
            </table>
      </center>
      <div align="center"><br>
        <input type="button" value=" Ok " name="B1" onClick=setLocationId() class='input'>
      <input type="button" value="Cancel" name="B2" onClick="window.close()" class='input'>
      </div>
    </form>
    </body>
    </html>
 <% }
else {%>
  <html>
   <head>
    <title>Select</title>
   <link rel="stylesheet" href="../ESFoursoft_css.jsp">
     </head>
      <body class='formdata'  >
       <b><font size="5">
        <center>
         <font size="2" face="Verdana">Location's</font>
          </center>
           </font>
        </b>
           <center>
          <textarea rows=6 cols=30 readonly class='select'>No Locations are  available.
       </textarea>
      </center>
       <br>
      <br>
     <center>
    <input type=button value="Close" onClick="window.close()" class='input'>
     </center>
    </body>
   </html>
<%}%>

<%@ page import="javax.naming.InitialContext,
 				 javax.naming.NamingException,
				 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				 java.util.ArrayList,
				org.apache.log4j.Logger"%>					

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
%>

<%
	  String FILE_NAME ="ETShipConsDtlsLOV.jsp";
    logger  = Logger.getLogger(FILE_NAME);	
      InitialContext 				jndiContext 		= null;
	  SetUpSession		customerRemote	= null;
	  SetUpSessionHome	customerHome	= null;
	  ArrayList						listOfValues		= null;
      int							len					= 0;	
	  String						shipOrCons			= "";
	  String					    customerId			= "";
	  String					    terminalId			= "";
	  String					    module				= "";
	  String						noOfRows			= "";
	  String						rowNumber			= "";
	  String						shipOrConsTerminalId= "";
	  	
	  
	  
	  shipOrCons		   =   request.getParameter("ShipOrCons");
	  customerId		   =   request.getParameter("CustomerId");
	  terminalId		   =   request.getParameter("TerminalId");	
	  noOfRows			   =   request.getParameter("NoOfRows");
	  rowNumber			   =   request.getParameter("RowNumber");
	  shipOrConsTerminalId =   request.getParameter("ShipOrConsTerminal");
      
      
      try
      {
            //jndiContext    = new InitialContext();
			customerHome   =(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
			customerRemote =(SetUpSession)customerHome.create();
			
			listOfValues   = customerRemote.getShipConsDtls(customerId,shipOrCons,terminalId,shipOrConsTerminalId);  
            if(listOfValues!=null)
				len =  listOfValues.size();
      
      }catch(Exception ex)
      {
       //Logger.error(FILE_NAME,"Exception :: ETShipConsDtlsLOV.jsp " + ex.toString() ); //RS#NAMECHANGE#745# 
       logger.error(FILE_NAME+"Exception :: ETShipConsDtlsLOV.jsp " + ex.toString() ); //RS#NAMECHANGE#745# 
      }    
%>	  
<html>
<head>
<title>Select </title>
<%@ include file="/ESEventHandler.jsp" %>
<script>

    var shipOrCons = '<%=shipOrCons%>';
	function showCustomerIds()
	{
<%	 
       for(int i = 0;i<len; i++)
		{
%>
			val	=	'<%=listOfValues.get(i).toString()%>';			
			window.document.form1.ids.options['<%=i%>']	=	new Option(val,val);
<%
		}
		if(len > 0)
		{
%>
			window.document.form1.ids.options[0].selected = true;	
		 	window.document.form1.ids.focus();
<%
		}else{
%>
			window.document.form1.B2.focus();
<%			
		}
%>
	}	

	// This function is used to set the Customer Id to the Text Filed after selecting it from the LOV.
	function setCustomerId()
	{
		var noOfRows = '<%=noOfRows%>';
		var rowNumber = '<%=rowNumber%>';
		
		if(document.form1.ids.selectedIndex == -1)
		{
			alert("Plese select atleast one Customer Id");
		}
		else
		{
			
			var	len	=  document.form1.ids.length;
			count=0;
			
			for(var i=0;i<len;i++)
			{
				
				if((document.form1.ids.options[i]!=null)  && (document.form1.ids.options[i].selected) )
				{
			      count ++;	       
				}
			}			
			
			
			if(shipOrCons=='ShipLOV')
			{
				 if(noOfRows==1 && rowNumber==0)	
				 {
					window.opener.document.form1.shipper.options.length = null; 
					window.opener.document.form1.shipper.options.length = count;
				 }
				 else
				 {
				    window.opener.document.form1.shipper[rowNumber].options.length = null; 
					window.opener.document.form1.shipper[rowNumber].options.length = count;
				 
				 }
			
			}
			else
			{
			    if(noOfRows==1 && rowNumber==0)	
			 	{
				    window.opener.document.forms[0].consignee.options.length = null;
				    window.opener.document.forms[0].consignee.options.length = count;
			 	}
			    else
			    {
			        window.opener.document.forms[0].consignee[rowNumber].options.length = null;
				    window.opener.document.forms[0].consignee[rowNumber].options.length = count;
			    
			    }
			}
			count = 0 ; 
			for(var i=0;i<len;i++)
			{
				
				if((document.form1.ids.options[i]!=null)  && (document.form1.ids.options[i].selected) )
				{
						temp1	=	document.form1.ids.options[i].value;
						index1	=	temp1.indexOf('[');
						temp2	=	temp1.substring(0,(index1)).toString();
						                        
                       
						if(shipOrCons=='ShipLOV')			
						{
							 if(noOfRows==1 && rowNumber==0)	
			 				 {
				               window.opener.document.form1.shipper.options[count].value=temp2;							
							   window.opener.document.form1.shipper.options[count].text=temp2;
						       window.opener.document.form1.shipper.options[count].selected=true;
			 				 }  
							 else
							 {
							   window.opener.document.form1.shipper[rowNumber].options[count].value=temp2;
							   window.opener.document.form1.shipper[rowNumber].options[count].text=temp2;
						       window.opener.document.form1.shipper[rowNumber].options[count].selected=true;
							 
							 }	
							
							
						}
						else
						{
						     if(noOfRows==1 && rowNumber==0)	
			 				 {
						        window.opener.document.form1.consignee.options[count].value=temp2;
								window.opener.document.form1.consignee.options[count].text=temp2;
							    window.opener.document.form1.consignee.options[count].selected=true;
			 				 }
							 else
							 {
							    window.opener.document.form1.consignee[rowNumber].options[count].value=temp2;
								window.opener.document.form1.consignee[rowNumber].options[count].text=temp2;
							    window.opener.document.form1.consignee[rowNumber].options[count].selected=true;
							 
							 } 
						}
						count = count + 1;						
			               
			   }
		  } 
		  
		  if(shipOrCons=='ShipLOV')			
		  {
		    if(noOfRows==1 && rowNumber==0)	
			 {
			   window.opener.document.form1.shipper.options[count-1].value=temp2+"&";							
			 }  
			 else
			 {
			   window.opener.document.form1.shipper[rowNumber].options[count-1].value=temp2+"&";	
			 }	
							
							
		 }
		 else
		 {
			 if(noOfRows==1 && rowNumber==0)	
			  {
				window.opener.document.form1.consignee.options[count-1].value=temp2+"&";	
			  }
			 else
			 {
				window.opener.document.form1.consignee[rowNumber].options[count-1].value=temp2+"&";	
			 } 
		}
		  
	  window.close();	 
	  }
	}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCustomerId()
	}
	if(event.keyCode == 27 ){
		window.close();
	}
}
function onEscKey(){
	if(event.keyCode == 27 ){
		window.close();
	}
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">

</head>

<body onLoad = "showCustomerIds()" onKeyPress='onEscKey()' class='formdata'>
<form name= form1>
<br>	<b><center>Customer Ids </center></b>
<br>
<%
	if(len > 0)
	{
%>
		<center>
				<select size=8 name="ids" class="select" onDblClick='setCustomerId()' onKeyPress='onEnterKey()' multiple>
				</select>
		</center>
		<br>
		<center>
				<input type=button name="addButton" value=" Ok " onClick="setCustomerId()" class="input">
				<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input">
		</center>
<%
	}
	else
	{
%>
		<center><!-- Modified By G.Srinivas to resolve the QA-Issue -->
		<textarea rows=6 name="ta" class='select'  readOnly cols="30" >No CustomerIds available for this terminal <%=terminalId%> </textarea>
		</center><br>
		<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>

</body>
</html>
	  
	  
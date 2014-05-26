<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/**
	Program Name	  : ETCServiceLevelView.jsp
	Module Name		  : ETrans
	Task			  : ServiceLevel	
	Sub Task		  : View 	
	Author Name		  : Sivarama Krishna .V
	Date Started	  : September 12,2001
	Date Completed	  : September 12,2001
	Date Modified	  : Feb 18,2003
    Modified By       : K.N.V.Prasada Reddy.
    Modification      : Added new Functionality for TaskPlan
	Description		  :
		This file is used to Modify or View the details of the ServiceLevel entered and Task Events. This jsp takes all the data entered in the fields and passes to 
		ETCServiceLevelProcess.jsp if the module selected from the eSupply tree is either 'Modify' else if the
		module selected is 'View', this jsp shows the details of the ServiceLevel corresponding to the selected ServiceLevel in the
		ETCServiceLevelEnterId.jsp.
		This file interacts with ServiceLevelSessionBean and then calls the method 'getServiceLevelDetails(String serviceLevelId)' which inturn 
		retrieves all the details of the ServiceLevel passed.
	Method Summary	:
*/
%>
<%@ page import="javax.naming.InitialContext,
				 javax.naming.NamingException, 	
				 org.apache.log4j.Logger,
				 com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean,
				 com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
%>
<%
	String operation           =    null;
	String FILE_NAME	       =   "ETCServiceLevelView.jsp";	
  logger  = Logger.getLogger(FILE_NAME);	
	String shipmentMode	       =	 request.getParameter("shipmentMode");		//Variable to store shipmentMode
	String  disabled 	       = "";
    String guaranteeDelivery   = null;
    String eventIds[]          = null;
    String eventDesc[]         = null;
    int[] allocatedTime        = null;
    int[] alertTime            = null;
    String[] originLocationIds = null;
    String[] destLocationIds   = null;
	ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = new ArrayList();
	String[]  eventDescription      = null;
    int[]  eventIdVal               = null;  
	
	//try1 begin
    try
    {
    //if 1 begin		
	if(loginbean.getTerminalId() == null)
	{
%>
	<jsp:forward page="../ESupplyLogin.jsp" />
<%
	}//if 1 end
	//else1 begin
    else
	{
%>
<%  	
	    
        String serviceLevelId = null;  //variable that represents serviceLevelId
    	String serviceLevelDescription = null;  //variable that represents ServiceLevelDescription
    	String remarks = null;  //variable that represents remarks if any
    	String readOnly = null;
    	String submitValue=null; 
        //tyr2 begin
		try
		{
 			operation = request.getParameter("Operation");
   			if ( (operation.equals("Delete")) || (operation.equals("View")) )
         		readOnly="readonly";
    		else
       			readOnly="";
        session.setAttribute("OPERATION",operation);
        serviceLevelId = request.getParameter("serviceLevelId").toUpperCase();
        InitialContext context = new InitialContext();	//Object of Initialcontext for JNDI lookup Purpose
        SetUpSessionHome slh = ( SetUpSessionHome )context.lookup("SetUpSessionBean");  //Object of ETransHOSuperSessionBean Home Interface
        SetUpSession sl = (SetUpSession)slh.create();  //Object of ETransHOSuperBean Remote Interface
        ServiceLevelJspBean ServiceLevelObj = sl.getServiceLevelDetails(serviceLevelId); //Object of ServiceLevelJspBean
            //if 2 begin
			if( (operation.equals("View")) || (operation.equals("Delete")) )
			{ 
			      readOnly="readonly"; 
			      disabled = "disabled";
			}
			else
			{ 
				readOnly=""; 
				   
			}
    		if( ServiceLevelObj != null)
    		{
	    		serviceLevelId = ServiceLevelObj.getServiceLevelId();
                eventIds = ServiceLevelObj.getEventIds();
                eventDesc= ServiceLevelObj.getEventDesc();
                allocatedTime = ServiceLevelObj.getAllocatedTime();
                alertTime = ServiceLevelObj.getAlertTime();
                originLocationIds=ServiceLevelObj.getOriginLocations();
                destLocationIds=ServiceLevelObj.getDestiLocations();
		    	serviceLevelDescription = ServiceLevelObj.getServiceLevelDescription();
			   	remarks 		= ServiceLevelObj.getRemarks();
                guaranteeDelivery = ServiceLevelObj.getGuaranteeDelivery();
			  	int shipMode	= ServiceLevelObj.getShipmentMode();
	            session.setAttribute("Service",ServiceLevelObj);
	
				if(shipMode == 1)
					shipmentMode = "(1,3,5,7)";
				if(shipMode == 2)
					shipmentMode = "(2,3,6,7)";
				if(shipMode == 4)
					shipmentMode = "(4,5,6,7)";  
					
	     if(serviceLevelId == null)
				{
					serviceLevelId = "";
				}
				if(serviceLevelDescription == null)
				{
					serviceLevelDescription = "";
				}
				if(remarks == null)
				{
					remarks = "";
				}
       ArrayList eventInfor =null;
	   eventInfor  = sl.getEventData();
       if(eventInfor != null)
          if(eventInfor.size()>1)
            {
            eventIdVal       = (int[])eventInfor.get(0);
            eventDescription = (String[])eventInfor.get(1);
              }
%>
<html>
<head>
<title>ServiceLevel View</title>
<script language="JavaScript">

    var allEventDescription = new Array();
	var allEventId = new Array();
	var AllocatedTime = new Array()
	var lertTime = new Array()
    totalEvent   = 0;
	EventIds					= new Array(<%=eventIdVal.length%>);
	EventDescs				= new Array(<%=eventIdVal.length%>);
	AllocatedTime			= new Array(<%=eventIdVal.length%>);
	AlertTime			= new Array(<%=eventIdVal.length%>);
    ControlList=new Array(<%=eventIdVal.length%>);
	op="";
	op1 = "";
	flag1 = 'No';
	var number				='1';
    
	function loadEventIds()
	{   
   
           
			
        <% for(int i=0;i<eventIdVal.length;i++)
           {%>
			  EventIds[<%=i%>]              = '';
              EventDescs[<%=i%>]	        = '';
              allEventDescription[<%=i%>]	= '<%=eventDescription[i]%>';
	    	  allEventId[<%=i%>]	        = '<%=eventIdVal[i]%>';
              AllocatedTime[<%=i%>]         = 0;
              AlertTime[<%=i%>]             = 0;
              ControlList[<%=i%>]          =''
           <%}%>     
<% if(eventIds!=null)
    {
      if(eventIds.length>1)
        { 
        for(int i=0;i<eventIds.length;i++)
            {
        %>
         
			for(kk=0;kk<allEventId.length;kk++)
				if(allEventId[kk]==document.forms[0].EventDesc1['<%=i%>'].value)
				     break;
         ControlList[kk]=document.forms[0].EventDesc1['<%=i%>'].value;
   <%   
            }
        }
        else
           {%>
			for(kk=0;kk<allEventId.length;kk++)
				if(allEventId[kk]==document.forms[0].EventDesc1.value)
				     break;
            ControlList[kk]=document.forms[0].EventDesc1.value;   
     <%}
}%>     
              for(c=0;c<allEventId.length;c++)
	            	{
                if(parseInt(ControlList[c])!=parseInt(allEventId[c]))
						{
  		         	     op = op+"<option value="+allEventId[c]+">"+allEventDescription[c]+"</option>";	
						 totalEvent++;
						}
              	}
   <% if ((!operation.equals("View"))&&(!operation.equals("Delete")))
      {  %>
       if (op!='')
		  {
         if(number<=0) number=1;
				   createForm(number,-1);
		  }
<%}%>
  }
	 function createForm(numb,row)
     {
		var data = "" ;
		var number = parseFloat(numb)
		tablestart = "<table width=800  cellpadding=4 cellspacing=1  bgcolor='#ffffff'>";    	
		tableend  = "</table>" ;
		tabledata = "" ;
		var rowdata = "" ; 
		var k1=0;
		var k2=0;
		var k3=0;
		var k4=0;
		browser=navigator.appName
		var data1="";		  
		
		 for(j=0; j < document.forms[0].elements.length; j++)
			{
			if(document.forms[0].elements[j].name == "EventDesc")
			{
				EventIds[k1] = document.forms[0].elements[j].value;
				for(kk=0;kk<allEventId.length;kk++)
				   {
					if(allEventId[kk]==EventIds[k1])
					   break;
                   } 
                EventDescs[k1]=allEventDescription[kk];
				k1++
			}
			
			else if(document.forms[0].elements[j].name == "AllocatedTime")
			{
				AllocatedTime[k3] = document.forms[0].elements[j].value;
				k3++
			}
			
			else if(document.forms[0].elements[j].name == "AlertTime")
			{
				AlertTime[k4] = document.forms[0].elements[j].value;
				k4++
			}
		}
       if((row !=-1)&&(row!=-2))
		   {
			for(var j=0; j < EventIds.length -1; j++)
			{
				if( j  >= row)
					{
						EventIds[j] = EventIds[j+1];
						EventDescs[j] = EventDescs[j+1];
						AllocatedTime[j] = AllocatedTime[j+1];
						AlertTime[j]=AlertTime[j+1];
						
					}
			 }
		    }

		inter = "'";
		rowdata =""
		for(i=0;i<number;i++)
			{
				rowdata = rowdata +"<tr class='formdata' align='center'>"
      
              if(i==0)
              {
                rowdata = rowdata +"<td width=25>&nbsp;</td>"
              }else
              {
               if(i==(number-1))
                  rowdata = rowdata +"<td width=25><input type=button value='<<' onClick='removeFormRow("+i+")' class='input'> </td>";
               else
                 rowdata = rowdata +"<td width=25>&nbsp;</td>";
              }
      rowdata = rowdata +"<td width=450 ><select size=1 name=EventDesc  onChange='isOneMoreEvent();' class='select'><option selected value="+EventIds[i]+ ">"+ EventDescs[i] +"</option>"+op
      if(flag1=='Yes')
             rowdata+=op1;	  
	  rowdata+="<select></td>"
                       	+"<td width=150><input type='text' class='text' name=AllocatedTime  maxlength =3 class='select'  size=8 align='right'   value= "+AllocatedTime[i]+"  onkeyPress='return getDotNumberCode(this)'></td>"
		                    +"<td width=150 align='center' ><input type='text' class='text' class='select' align='right' name=AlertTime  size=8 maxlength=3 value= "+inter+AlertTime[i]+inter+"  onkeyPress='return getDotNumberCode(this)' ></td>"
        if((i == (number-1))&&(totalEvent>number))
				{
            rowdata +="<td width=25><input type='Button' size=10 name = nextButton  value='>>' disabled  onClick='createForm(++number,-1)' class='input'></td>";
            
				}else
					{
						rowdata+="<td width=25>&nbsp;</td>";
					}
					rowdata = rowdata+ "</tr>"  
				   flag1='No';
			     }
			data=tablestart+""+data1+""+rowdata+""+tableend; 
      
      
		if(browser == "Microsoft Internet Explorer")
		  {
		   if(document.layers) 
			  {
			   document.layers.cust.document.write(data);
			   document.layers.cust.document.close();
			   }
		     else if(document.all) 
				{
					cust.innerHTML = data;
				}
			}
		 if(browser == "Netscape" && parseFloat(navigator.appVersion) < 5 )
			{
				document.layers["cust"].document.write(data);
				document.layers["cust"].document.close();       
			}
 
 
}

function removeFormRow(row)
 {
		number-- ;
	 if(number==1)
       {
        flag1='Yes';
        op1="<option value=''></option>";
       }
		createForm(number,row);
		isOneMoreEvent();
 }
 function isOneMoreEvent()
    {
if (totalEvent>number)
{
	document.forms[0].nextButton.disabled=true;
 	flag = false;
	var field = document.forms[0];
	idx = new Array()
	x = 0;
	for(i = 0; i < field.length; i++) 
	{
		if( field.elements[i].name == "EventDesc")
		{
			idx[x] = field.elements[i].value	
			x++
		}
	}
	for(i = 0; i <  idx.length; i++) 
	{
		if(idx[i].length > 0)
		{
			flag = true
		
		}
		else
		{
			flag= false
		}
			if(flag == false)
			return 
	}
    if(flag == true)
	{
	    document.forms[0].nextButton.disabled=false;
    }
    else
	{
	    document.forms[0].nextButton.disabled=true;
     
	}
} 
	}
	function placeFocus() 
	{
		var operation='<%= operation %>';
  		if(operation==('View')|| operation==('Delete') )
		{
			document.forms[0].Submit.focus();
		}
		else
		{
		  	document.forms[0].serviceLevelDescription.focus();
	    }
	    guaranteeDelivery = '<%=guaranteeDelivery%>'
        if(guaranteeDelivery=='Y')
         {
          document.forms[0].guaranteeDelivery.checked = true;
         }
		shipmentMode = <%= shipMode%>
      
	  document.forms[0].shipmentMode.value = shipmentMode;

	  if(shipmentMode == '1' || shipmentMode == '3' ||shipmentMode == '5' ||shipmentMode == '7')
	  {	
		document.forms[0].Air.checked = true;
		document.forms[0].Air.disabled = true;
	  }	
	  if(shipmentMode == '2' || shipmentMode == '3' ||shipmentMode == '6' ||shipmentMode == '7')
	  {	
		document.forms[0].Sea.checked = true;
		document.forms[0].Sea.disabled = true;
	  }	
	  if(shipmentMode == '4' || shipmentMode == '5' ||shipmentMode == '6' ||shipmentMode == '7')
	  {	
		document.forms[0].Truck.checked = true;
		document.forms[0].Truck.disabled = true;
	  }	
		
	}
	function showLOV()
	{
		var Url=' LOV.jsp';
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=360,height=360,resizable=no';
		var Features=Bars+' '+Options;
    	var Win=open(Url,'Doc',Features);
	}
	function stringFilter(input) 
	{
		s = input.value;
		input.value=s.toUpperCase();
	
	}
	function getSpecialCode()
 	{
  		if(event.keyCode!==13)
    	{
     		if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 			event.returnValue =false;
   		}
  		return true;
 	} 
 	
	function setShipmentMode(obj)
	{
	  var objName = obj.name;
	  var objValue = obj.value;
	  var shipmentValue = document.forms[0].shipmentMode.value;
	  if(obj.checked)
		document.forms[0].shipmentMode.value = parseInt(shipmentValue) + parseInt(obj.value);
	  else
	  	document.forms[0].shipmentMode.value = parseInt(shipmentValue) - parseInt(obj.value);

	}
	function Mandatory()
	{
		ShipmentMode  =  document.forms[0].shipmentMode.value ;
		if(document.forms.length > 0)
	 	{	
	   		for( i=0;i<document.forms[0].elements.length;i++)
	   		{
		 		if((document.forms[0].elements[i].type=="text") || (document.forms[0].elements[i].type=="textarea"))
		 		document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
	   		}
	 	}
   		Remarks  = document.forms[0].remarks.value;	
   		ServiceLevelDescription = document.forms[0].serviceLevelDescription.value;
    	if(ServiceLevelDescription.length==0)
		{
	   		alert(" Please enter ServiceLevel Description");
	   		document.forms[0].serviceLevelDescription.focus();
	   		return false;
		}	
   		if(Remarks.length > 4000)
		{
			alert("Remarks should be less than 4000 characters");
			document.forms[0].remarks.focus();
			return false;
		}
		if(ShipmentMode==0)
		{
			 alert("Please select atleast one Shipment Mode");
			 return false;
		}
		//document.forms[0].Submit.disabled='true';
   		return true;	
	}

function findDuplicates()
   {
   
   var r=0; 
   var k=0;
  if(number!=1)
  {
     for(r=0;r<number;r++)
      {
      for(k=r+1;k<number;k++)
        {
        if(document.forms[0].EventDesc[r].value==document.forms[0].EventDesc[k].value)
          {
          alert("Duplicate Events Not Allowed");
          return false;
          }
        }
      }  
      }
      return true;
   }
   

 function setevntDeleteValue(index)
    {
	if(document.forms[0].evntDelete.length>1)
        {

        if(document.forms[0].evntDelete[index].checked)
			if(confirm("Do You Want To Delete This Event"))
			  {
			     document.forms[0].evntDeleteValue[index].value='Y'; 
			  }
             else
			    {
                 document.forms[0].evntDelete[index].checked = false;
				} 
         else
                 document.forms[0].evntDeleteValue[index].value='N';
           }
    else
          {
          if(document.forms[0].evntDelete.checked)
			  if(confirm("Do You Want To Delete This Event"))
			     {
                 document.forms[0].evntDeleteValue.value='Y';
				 }
             else
		    	  {
                    document.forms[0].evntDelete.checked  = false;
				  }
        
         else
                 document.forms[0].evntDeleteValue.value='N';
           }
	}
	function getDotNumberCode(input)
	{
		if(event.keyCode!=13)
		{	
			
			 if((event.keyCode <= 46 || event.keyCode==47 || event.keyCode > 57) )
 			   return false;	
			  else
			  {
					var index = input.value.indexOf(".");
					if( index != -1 )
					{
						if(input.value.length == index+3)
						return false;
					}
			  }
		}
		return true;	
	}
function checkSeviceLevelData()
 {
   if(!Mandatory())
        return false;
<% if (eventIds!=null){%>
     if(document.forms[0].AllocatedTime1.length==1)
	     {
		  if((parseFloat(document.forms[0].AllocatedTime1.value)==0)||(document.forms[0].AllocatedTime1.value=="")) 
		           {
				   alert("Please Enter Allocated Time");
				   document.forms[0].AllocatedTime1.focus();
				   return false;
          	      }
           else if((document.forms[0].AlertTime1.value==""))
		      {
                 alert("Please Enter Alert Time");
                 document.forms[0].AlertTime1.focus();
				 return false;
			  }
           else if((parseFloat(document.forms[0].AllocatedTime1.value))<(parseFloat(document.forms[0].AlertTime1.value)))
		         {
				 alert("Allocated Time must GreaterThan Alert Time");
                 document.forms[0].AllocatedTime1.focus();
				 return false;
				 }
		 }
     else
	     {
		   for(kk=0;kk<document.forms[0].AllocatedTime1.length;kk++)
		   {
              if((parseFloat(document.forms[0].AllocatedTime1[kk].value)==0)||(document.forms[0].AllocatedTime1[kk].value=="")) 
		           {
				   alert("Please Enter Allocated Time");
				   document.forms[0].AllocatedTime1[kk].focus();
				   return false;
          	      }
           else if((document.forms[0].AlertTime1[kk].value==""))
		      {
                 alert("Please Enter Alert Time");
                 document.forms[0].AlertTime1[kk].focus();
				 return false;
			  }
           else if((parseFloat(document.forms[0].AllocatedTime1[kk].value))<(parseFloat(document.forms[0].AlertTime1[kk].value)))
		         {
				 alert("Allocated Time must GreaterThan Alert Time");
                 document.forms[0].AllocatedTime1[kk].focus();
				 return false;
				 }  
		   }
		 }
<%}%>
		 /*for new Event Data checking*/
if (op!='')
{
  if(number==1)
    {
   if(document.forms[0].EventDesc.value.length>0)
     {
		  if((parseFloat(document.forms[0].AllocatedTime.value)==0)||(document.forms[0].AllocatedTime.value=="")) 
		           {
				   alert("Please Enter Allocated Time");
				   document.forms[0].AllocatedTime.focus();
				   return false;
          	    }
        else if((document.forms[0].AlertTime.value==""))
		         {
                 alert("Please Enter Alert Time");
                 document.forms[0].AlertTime.focus();
				         return false;
			        }
         else if((parseFloat(document.forms[0].AllocatedTime.value))<(parseFloat(document.forms[0].AlertTime.value)))
		        {
				     alert("Allocated Time must GreaterThan Alert Time");
             document.forms[0].AllocatedTime.focus();
				     return false;
				     }
	
	   }
     else{
	      if((parseFloat(document.forms[0].AllocatedTime.value)>0)||(parseFloat(document.forms[0].AlertTime.value)>0))
		        {
				alert("Please Select One Event ");
				document.forms[0].EventDesc.focus();
				return false;
				}
	     }
	}	 
     else
	     {
		   for(kk=0;kk<document.forms[0].AllocatedTime.length;kk++)
		   {
		   if(document.forms[0].EventDesc[kk].value.length>0)
		     {
         if((parseFloat(document.forms[0].AllocatedTime[kk].value)==0)||(document.forms[0].AllocatedTime[kk].value=="")) 
		           {
                alert("Please Enter Allocated Time");
                document.forms[0].AllocatedTime[kk].focus();
                return false;
                }
           else if((document.forms[0].AlertTime[kk].value==""))
		         {
                 alert("Please Enter Alert Time");
                 document.forms[0].AlertTime[kk].focus();
				         return false;
			      }
           else if((parseFloat(document.forms[0].AllocatedTime[kk].value))<(parseFloat(document.forms[0].AlertTime[kk].value)))
		         {
        				 alert("Allocated Time must GreaterThan Alert Time");
                 document.forms[0].AllocatedTime[kk].focus();
			        	 return false;
				      }  
          }
			else
			   {
			    if((parseFloat(document.forms[0].AllocatedTime[kk].value))>0||(parseFloat(document.forms[0].AlertTime[kk].value)>0))
              {
              alert("Please Select One Event ");
              document.forms[0].EventDesc[kk].focus();
              return false;
              }
          }
		 }  
       
		}//else close
        if(!findDuplicates())
		  return false;
}
document.forms[0].Submit.disabled='true';
return true;
 }

 function LocationBased()
   {
 <%if((operation.equals("Add"))||(operation.equals("Modify")))
   {%>
   if(checkSeviceLevelData())
     {
       
      document.forms[0].action='ETCLocTaskPlan.jsp';
      document.forms[0].submit();
      }
    else 
      return false;
  
  <%}else {
  %>     
        document.forms[0].action='ETCLocTaskPlan.jsp';
         document.forms[0].submit();
      <%}%>
   }   

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<!-- loadEventIds(); -->
<body  onLoad="placeFocus();">
<%
			String actionValue=null;
    
    		if (operation.equals("View") )
      			actionValue="ETCServiceLevelEnterId.jsp?Operation="+operation;
    		else 
	      		actionValue="ETCServiceLevelProcess.jsp";
%>
	  
 <form method="POST"  <% if (operation.equals("Modify")||operation.equals("Add")) {%> onSubmit="return checkSeviceLevelData()" <%}%> action="<%=actionValue%>" name="servlevel">
 		   <input type="hidden" value="<%=operation%>"  name="Operation">
<%
			if (operation.equals("View") )
				submitValue="Continue";
			else if(operation.equals("Delete"))
				submitValue="Delete";
			else
				submitValue="Submit";
%>       

<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" > 
    <td bgcolor="ffffff">
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td> 
    Service Level - <%=operation%> </td><td align=right><%=loginbean.generateUniqueId("ETCServiceLevelView.jsp",operation)%></td></tr>
	</table>
</td>
        </tr>
        </table>
        

            <table border="0" width="800" cellpadding="4" cellspacing="1">
             <tr class='formdata'><td colspan="2">&nbsp;</td></tr>
            
            <tr valign="top" class='formdata'> 
            <td width="360" >Service 
                    Level Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="serviceLevelId" value=" <%=serviceLevelId%>" maxlength= " 5 "   readonly>
                   
											
                    </td>
                  <td >Service 
                    Description:<font color="#FF0000">*</font><br>
                    <input type='text' class='text'   name="serviceLevelDescription" value="<%=serviceLevelDescription%>"   size="50" maxlength ="50" onKeypress="return getSpecialCode(serviceLevelDescription)" onBlur="stringFilter(this)" <%=readOnly%>>
                    </td>
					
                </tr>
                <tr  width="360" class='formdata'> 
                  <td >Remarks:<br>
                    <textarea  <%=readOnly%> rows="5" class='select' name="remarks"  cols="32" maxlength ="4000"  onKeypress="return getSpecialCode(remarks)" onBlur="stringFilter(remarks)"><%=remarks %></textarea>
                    </td>

					<td >Shipment 
              Mode:<font color="#FF0000">*</font><br>
			  
               <input type="hidden" name="shipmentMode" value=0 class="select">
			  
			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)" <%=disabled%>>Air
			   
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)" <%=disabled%>>Sea
			   
			   <input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)" <%=disabled%>>Truck     
               <br>Guaranteed Delivery <input type=checkbox name="guaranteeDelivery" value='Y' <%=disabled%>>   
   

       </td>
					
					   
                </tr>
              </table>
          
              
<% if((eventIds!=null)||(operation.equals("Modify")))
    {%>
   <!--  <table width=800  cellpadding=4 cellspacing=1 >    
             <tr align='center' valign=top class='formheader'>
                  <td width=25>&nbsp;</td>
                  <td width=460  >Event</td>
                  <td width=145  >Allocated Time (mm) </td>
                  <td width=145>Alert Time<br>(mm)</td>
                  <td width=25>&nbsp;</td>
              </tr> -->
    
<%  }
   if(eventIds!=null)  
    {
      for(int i=0;i<eventIds.length;i++)
        {
%>
            <tr class='formdata' align='center'>
<%        if (operation.equals("Modify"))
               {
%>                 
                <td width=25><input type=checkbox name=evntDelete value='Y' onclick=setevntDeleteValue(<%=i%>)>
                <input type=hidden name=evntDeleteValue value='N'></td>
<%            }
          else
             {
%>            <td width=25>&nbsp;</td>
<%           }%>
                
                 <td width=460  >
                     <label class='select'><%=eventDesc[i]%></label>
                     <input type=hidden name=EventDesc1 value='<%=eventIds[i]%>'>
                     
                </td>
                  <td width=145>
                      <input type='text' class='text' name=AllocatedTime1  <%=readOnly%>  class='select'  size=8 align='right' maxlength=3   value= '<%=allocatedTime[i]%>'  onkeyPress='return getDotNumberCode(this)'>
                </td>
                <td width=145 align='center' >
                        <input type='text' class='text'  align='right' class='select'   <%=readOnly%>  maxlength=3   name=AlertTime1  size=8 value= '<%=alertTime[i]%>' onkeyPress='return getDotNumberCode(this)'>
                </td>
                <td width=25>&nbsp;</td>
              <tr>
 <%       }//for close
     }//if close checking eventids for null 
 %>
            </table>  
             <!-- <span id=cust style="POSITION: relative" ;> </span> -->
             <!--  <table width='800' border='0' cellspacing='1' cellpadding='4'>
                   <tr class='formdata'>
               <%if((originLocationIds!=null)||(operation.equals("Add"))||(operation.equals("Modify"))){%>    
                        <td><input type='button' value='Location' class='input' name='But'  onClick='return LocationBased()'> </td></tr>
						</table> -->
                <%}%>  
             <table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor="#ffffff">
			 <tr class='denotes'><td ><font color='#FF0000'>*</font>Denotes
                Mandatory</td>
				<td  align='right'>
               <input type='submit' class='input' value='<%=submitValue%>' name='Submit'></td></tr>
               </table>
         
    </td>
  </tr>
</table>
<% 
   		}//if 2 end
		else
		{
			 
			 errorMessageObject = new ErrorMessage("Record does not exist with ServiceLevel Id  : "+serviceLevelId,"ETCServiceLevelEnterId.jsp"); 
			 keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			 keyValueList.add(new KeyValue("Operation",operation)); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 			 
			 /**
			 session.setAttribute("Operation",operation); 
			 String errorMessage = "Record does not exist with ServiceLevel Id  : "+serviceLevelId;
			 session.setAttribute("ErrorCode","RNF");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCServiceLevelEnterId.jsp?Operation="+operation);  */
%>
      <jsp:forward page="../ESupplyErrorPage.jsp" />
  
<%		
		}
	}
  //try2 end
 	catch( NamingException nme )
  	{
       //Logger.error(FILE_NAME,"Error while bounding ServiceLevelBean in ServiceLevel View "+nme.toString());
       logger.error(FILE_NAME+"Error while bounding ServiceLevelBean in ServiceLevel View "+nme.toString());
  	}
%> 
</form> 
</body>
</html>
<%
	}//try1 end
	}catch(Exception e)
	{
		//Logger.error(FILE_NAMsE,"Exception while accessing Loginbean in ServiceLevelView JSP"+e.toString());
    logger.error(FILE_NAME+"Exception while accessing Loginbean in ServiceLevelView JSP"+e.toString());
		
		errorMessageObject = new ErrorMessage("Error occured while accessing the page ","ETCServiceLevelEnterId.jsp"); 
	    keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
		
		/**
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage","Error occured while accessing the page ");
		session.setAttribute("NextNavigation","ETCServiceLevelEnterId.jsp");
		session.setAttribute("Operation",operation);  */
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>
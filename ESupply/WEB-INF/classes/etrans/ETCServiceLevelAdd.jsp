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
	Program Name	  : ETCServiceLevelAdd.jsp
	Module Name		  : ETrans
	Task			  : ServiceLevel & TaskPlan	
	Sub Task		  : Add	
	Author Name		  : Sivarama Krishna .V
	Date Started	  : September 12,2001
	Date Completed	  : September 12,2001
	Date Modified	  : Feb 18th,2003
    Modified By       : K.N.V.Prasada Reddy
    Modification      : Added New Functionality for Task Plan.
	Description		:
		This file is used to add a new Service Levels and also used to add Task Plan to that Service Level. This jsp takes all the data entered in the fields and passes to 
	    ETCServiceLevelAddProcess.jsp 
        This file interacts with CodeCustomisationSessionBean and then calls the method getCodeType()' which inturn 
        retrieves all the CodeId Names.
	Method Summary	:
		placeFocus()  			//This method will focus the cursor in the serviceLevelId 
		stringFilter(input)		//This method will change lower case letter to upper case 
		Mandatory()				//This method will prompt the user about the Mandatory fields
		getKeyCode()			//This method will return the keycode.
		getSpecialCode()		//This method will return the special codes
    createForm()    //This method will Produce dynamic Rows.
*/
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ page import = "org.apache.log4j.Logger,
                   javax.naming.InitialContext,
                   com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList " %>	
           
<%!
  private static Logger logger = null;
%>
<%
    session.removeAttribute("Service");
	String FILE_NAME = "ETCServiceLevelAdd.jsp";	
  logger  = Logger.getLogger(FILE_NAME);	
	String operation =request.getParameter("Operation");			
    String[]  eventDescription  = null;
    int[]  eventIdVal  =null;  
	ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = null;
	
	try
    {
	if(loginbean.getTerminalId() == null)
	{
%>
	<jsp:forward page="../ESupplyLogin.jsp" />
<%
	}
    else
	{
       ArrayList eventInfor =null;
	   InitialContext   itcx =new InitialContext();
       SetUpSessionHome home = (SetUpSessionHome)itcx.lookup("SetUpSessionBean");
       SetUpSession remote   =(SetUpSession)home.create();
       eventInfor  = remote.getEventData();
       if(eventInfor != null)
          if(eventInfor.size()>1)
            {
            eventIdVal       = (int[])eventInfor.get(0);
            eventDescription = (String[])eventInfor.get(1);
              }
%>
<html>
<head>
<title>Service Level Add</title>
<script language="JavaScript">
<!--
	op1 = ''; 
	var allEventDescription = new Array();
	var allEventId = new Array();
	var AllocatedTime = new Array()
	var lertTime = new Array()
	EventIds					= new Array(<%=eventIdVal.length%>);
	EventDescs				= new Array(<%=eventIdVal.length%>);
	AllocatedTime			= new Array(<%=eventIdVal.length%>);
	AlertTime			= new Array(<%=eventIdVal.length%>);
	op="";
	totalEvent = 0;
	var number				='1';
    Locatiion  ='No';  
	flag1 = 'No';
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
       <%}%>      
           
					
         for(c=0;c<allEventId.length;c++)
	      	{
  	       	op = op+"<option value="+allEventId[c]+">"+allEventDescription[c]+"</option>";	
			totalEvent++;
	       	}
   
         if(number<=0) number=1;
				 createForm(number,-1);

  }
	function placeFocus() 
	{

    document.forms[0].serviceLevelId.focus();
    
	}
	function stringFilter(input) 
	{
		s = input.value;
		input.value = s.toUpperCase();
	}
	function Mandatory()
	{
		ShipmentMode  =  document.forms[0].shipmentMode.value;
		if(document.forms.length > 0)
	 	{	
	   		for( i=0;i<document.forms[0].elements.length;i++)
	 	  	{
			 if((document.forms[0].elements[i].type=="text") || (document.forms[0].elements[i].type=="textarea"))
		 		 document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
	   		}
	 	}
   		serviceLevelId          = document.forms[0].serviceLevelId.value;
   		ServiceLevelDescription = document.forms[0].serviceLevelDescription.value;
   		Remarks                 = document.forms[0].remarks.value;
    	if(serviceLevelId.length==0)
		{
	   		alert(" Please enter ServiceLevel  Id");
	   		document.forms[0].serviceLevelId.focus();
	   		return false;
		}	
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
  		return true;	
	 
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
 
	function getKeyCode()
 	{
  		if(event.keyCode!==13)
    	{
     		if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode > 90 && event.keyCode < 97)||
	 		(event.keyCode > 122 && event.keyCode <127||(event.keyCode >57 && event.keyCode <65)))
	 		event.returnValue =false;
    	}
  		return true;
 	}
	function selectAll(input)
	{
		var fields	= document.forms[0];
		var flag=true;
		if(!input.checked)
			flag=false;

		for(var i=0;i<fields.length;i++)
		{
			if(fields.elements[i].type=='checkbox' && fields.elements[i].name!='all')
			{
				fields.elements[i].checked=flag;
				fields.elements[i].disabled=flag;
			}
		}
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

  function createForm(numb,row)
    {
		var data = "" ;
		var number = parseInt(numb)
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
		data1 = data1 + "<tr align='center' valign=top class='formheader'>";
		data1 = data1 + "<td width=30>&nbsp;</td>";
		data1 = data1 + "<td width=320  align=center>";
		data1 = data1 + "Event</td>";
		data1 = data1 + "<td width=190  align=center >Allocated Time <br>(mm)</td>"; 
		data1 = data1 + "<td width=190  align=center>Alert Time<br>(mm)</td>"; 
		data1 = data1 + "<td width=30>&nbsp;</td>";
		data1 = data1 + "</tr>";
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
					rowdata = rowdata +"<td width=30>&nbsp;</td>"
				}else
				{
         if((row!=-2) && (i==(number-1)))
		   			rowdata = rowdata +"<td width=30><input type=button value='<<' onClick='removeFormRow("+i+")' class='input'> </td>";
         else
           rowdata = rowdata +"<td width=30>&nbsp;&nbsp;&nbsp;&nbsp;</td>";
			  }
			rowdata = rowdata +"<td width=320 ><select size=1 name=EventDesc  onChange='isOneMoreEvent();' class='select'><option selected value="+EventIds[i]+ ">"+ EventDescs[i] +"</option>"+op;
			 if(flag1=='Yes')
                rowdata+=op1;  
			  rowdata+="<select></td>"
              	        +"<td width=190><input type='text' class='text' name=AllocatedTime   maxlength=3 onkeyPress='return getDotNumberCode(this)' size=8 align='right'   value= "+inter+AllocatedTime[i]+inter+" ></td>"
				              	+"<td width=190 align='center' ><input type='text' class='text' maxlength=3 align='right' name=AlertTime  onkeyPress='return getDotNumberCode(this)' size=8 value= "+inter+AlertTime[i]+inter+"  ></td>"
       if((i == (number-1))&&(totalEvent>number))
				{
         if(row!=-2)
					  rowdata +="<td><input type='Button' size=10 name = nextButton  value='>>' disabled  onClick='createForm(++number,-1)' class='input'></td>";
          else
            rowdata+="<td width=30>&nbsp;</td>";
				}
        else
					{
						rowdata+="<td>&nbsp;</td>";
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
		 if(browser == "Netscape" && parseInt(navigator.appVersion) < 5 )
			{
				document.layers["cust"].document.write(data);
				document.layers["cust"].document.close();       
			}
		
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
        else if(document.forms[0].AlertTime.value=="")
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
           else if(document.forms[0].AlertTime[kk].value=="")
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
 function LocationBased()
   {
   if(checkSeviceLevelData())
     {
      document.forms[0].action='ETCLocTaskPlan.jsp';
      document.forms[0].submit();
      }
    else 
      return false;
   }
//-->

	
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<!-- loadEventIds(); -->
<body  onLoad="placeFocus();">
<form method="POST" onSubmit ="return checkSeviceLevelData()" action="ETCServiceLevelProcess.jsp" name="servlevel">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff"> 
    <td  colspan="2">
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td><table width="790" border="0" ><tr class='formlabel'><td>Service Level - Add</td><td align=right><%=loginbean.generateUniqueId("ETCServiceLevelAdd.jsp","Add")%></td></tr></table></td></tr>
        </table>
            
            
            <table border="0" width="800" cellpadding="4" cellspacing="1" >
			<tr class='formdata'><td colspan="3">&nbsp;</td></tr>
            <tr valign="top" class='formdata'> 
            <td width=360>Service 
                    Level Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="serviceLevelId" maxlength="5" size="7" onkeyPress="return getKeyCode(serviceLevelId)" onBlur="stringFilter(serviceLevelId)">
                    </td> 
                  <td  >Service 
                    Description:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="serviceLevelDescription" size="50" maxlength = 50 onKeypress="return getSpecialCode(serviceLevelDescription)" onBlur="stringFilter(this)">
                    </td>
					
                </tr>
                <tr valign="top" class='formdata'> 
                  <td width=360>Remarks:<br>
                    <textarea rows="5" class="select" name="remarks" cols="32" maxlength ="4000"  onKeypress="return getSpecialCode(remarks)" onBlur="stringFilter(this)"></textarea>
                  </td>
				   <td >Shipment
              Mode:<font color="#FF0000">*</font><br>
			  &nbsp;&nbsp;
               <input type="hidden" name="shipmentMode" value=0 class="select">
			   Air
			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)">
			   &nbsp;&nbsp;Sea
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)">
			   &nbsp;&nbsp;Truck
			   <input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)">
                <br><br>Guaranteed Delivery <input type=checkbox name="guaranteeDelivery" value='Y'>   
            </td>
				  
                </tr>
              </table>
              <!--  <span id=cust style="POSITION: relative" ;> </span> -->
                
                  <table border='0' width='800' cellpadding='4' cellspacing='1'>
				 <!--  <tr class='formdata'>
                        <td  valign='top'  colspan=2><input type='button' value='Location' class='input' name='But' onClick='return LocationBased()'></td></tr> -->
                        <tr class='denotes'>
                       <td valign='top' >
                           <font color='#FF0000'>*</font>
                              Denotes Mandatory</td>
                       
                        <td valign='top'  align='right'>
                         <input type='submit' class='input' value='Submit' name='Submit'>
						   <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'></td></tr> </table>
						 <input type="hidden" name=Operation value="<%= operation %>">
              
        </td>
        </tr>
      </table>
      
</form>
</body>
</html>
<%
	}
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while accessing Loginbean in ServiceLevelAdd JSP");
    logger.error(FILE_NAME+"Exception while accessing Loginbean in ServiceLevelAdd JSP");
		
		errorMessageObject = new ErrorMessage("Error occured while accessing the page","ETCServiceLevelAdd.jsp"); 
	    keyValueList = new ArrayList();
		
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	    keyValueList.add(new KeyValue("Operation","Add")); 	
	    errorMessageObject.setKeyValueList(keyValueList);
	    request.setAttribute("ErrorMessage",errorMessageObject);
		
        /**		
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage","Error occured while accessing the page ");
		session.setAttribute("NextNavigation","ETCServiceLevelAdd.jsp");
		session.setAttribute("Operation","Add");   */
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
   
%>
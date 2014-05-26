
<%
/**
	Program Name	  : ETCLocTaskPlan.jsp
	Module Name		  : ETrans
	Task			  : ServiceLevel & TaskPlan	
	Sub Task		  : Add	
	Author Name		  : K.N.V.Prasada Reddy
	Date Started	  : Feb 20,2003
	Date Completed	  : march 2nd,2003
	Modification      : Added New Functionality for Task Plan.
	Description		:
		This file is used to add a new Service Levels and also used to add Task Plan to that Service Level. This jsp takes all the data entered in the fields and passes to 
	    ETCServiceLevelAddProcess.jsp 
        
	Method Summary	:
		stringFilter(input)		//This method will change lower case letter to upper case 
    createForm()    //This method will Produce dynamic Rows.
*/
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ page import = "org.apache.log4j.Logger,
                   javax.naming.InitialContext,
                   com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.foursoft.esupply.common.java.ErrorMessage,
                   com.foursoft.etrans.setup.servicelevel.bean.ServiceLevelJspBean,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList " %>	
           
<%!
  private static Logger logger = null;
%>
<%  String FILE_NAME = "ETCLocTaskPlan.jsp";
    logger  = Logger.getLogger(FILE_NAME);	
  	ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = null;
    String servicelevelId=request.getParameter("serviceLevelId");
    String serviceLevelDesc=request.getParameter("serviceLevelDescription");
    int shipmentMode=Integer.parseInt(request.getParameter("shipmentMode"));
    String guaranteeDelivery=request.getParameter("guaranteeDelivery");
    String remarks=request.getParameter("remarks");
	String operation =request.getParameter("Operation");		
    String[] EventIds   =request.getParameterValues("EventDesc");
    String[] strAllocatedTime=request.getParameterValues("AllocatedTime");  
    String[] strAlertTime=request.getParameterValues("AlertTime");   
    String[] strPreEventIds = null;
    String[] strPreAllocatedTime = null;
    String[] strPreAlertTime = null;
    String[] evntDelete = null;
    String[] eventDesc = null;
    String[] originLocationIds=null;
    String[] destLocationIds=null;
    String[] eventIds = null;
	String[] eventDescription = null; 
	int[] eventIdVal  = null;
    String readOnly="";
    String disabled="";
    int flagCount = 0;
    ServiceLevelJspBean ServiceLevelObj=null;
	ServiceLevelJspBean  ServiceLevelObject = null;

     
    int[] AllocatedTime=null;
    int[] AlertTime=null;
 
    if((operation.equals("Add"))||(operation.equals("Modify")))
	 {
       if(EventIds!=null)
		 {
         AllocatedTime=new int[strAllocatedTime.length];
         AlertTime=new int[strAllocatedTime.length];
 
      for(int i=0;i<strAllocatedTime.length;i++)
           {
            AllocatedTime[i]=Integer.parseInt(strAllocatedTime[i]);
            AlertTime[i]=Integer.parseInt(strAlertTime[i]);
           }
		 }   
      }
     else{  
            readOnly="readonly"; 
			      disabled = "disabled";
			    }
		
   if(operation.equals("Add"))            
   {
      ServiceLevelObject=new  ServiceLevelJspBean();
      ServiceLevelObject.setServiceLevelId(servicelevelId);
      ServiceLevelObject.setServiceLevelDescription(serviceLevelDesc);
      ServiceLevelObject.setShipmentMode(shipmentMode);
      ServiceLevelObject.setRemarks(remarks);
      ServiceLevelObject.setGuaranteeDelivery(guaranteeDelivery);
      ServiceLevelObject.setEventIds(EventIds);
      ServiceLevelObject.setAllocatedTime(AllocatedTime);
      ServiceLevelObject.setAlertTime(AlertTime);
      session.setAttribute("Service",ServiceLevelObject);   
	}    
  if(!operation.equals("Add"))
    { 
    ServiceLevelObj=(ServiceLevelJspBean)session.getAttribute("Service");  
    eventDesc=ServiceLevelObj.getLocEventDesc();
    originLocationIds=ServiceLevelObj.getOriginLocations();
    destLocationIds=ServiceLevelObj.getDestiLocations();
    eventIds=ServiceLevelObj.getLocEventIds();
    }
  if(operation.equals("Modify"))
      {
    
      ServiceLevelObj.setShipmentMode(shipmentMode);
      ServiceLevelObj.setServiceLevelDescription(serviceLevelDesc);
      ServiceLevelObj.setRemarks(remarks);
      ServiceLevelObj.setGuaranteeDelivery(guaranteeDelivery);
      strPreEventIds=request.getParameterValues("EventDesc1");
      strPreAllocatedTime=request.getParameterValues("AllocatedTime1");
      strPreAlertTime=request.getParameterValues("AlertTime1");
      evntDelete =request.getParameterValues("evntDeleteValue");
      
      if (strPreEventIds!=null)
          {
             flagCount           = 0;
            int preEventCount       = 0;
            for(int i=0;i<evntDelete.length;i++)
                 if (!evntDelete[i].equals("Y"))
                       flagCount++;
             if(flagCount!=0)
              {
               if(EventIds!=null)
                     preEventCount=EventIds.length;
                String[] EventIdList=new String[flagCount+preEventCount];
                int[]    AllocatedTimeList=new int[flagCount+preEventCount];
                int[]    AlertTimeList    =new int[flagCount+preEventCount];
                preEventCount=0;
                for(int i=0;i<strPreEventIds.length;i++)
                   {
                   if(!evntDelete[i].equals("Y"))
                       {
                       EventIdList[preEventCount]=strPreEventIds[i];
                       AllocatedTimeList[preEventCount]=Integer.parseInt(strPreAllocatedTime[i]);
                       AlertTimeList[preEventCount]=Integer.parseInt(strPreAlertTime[i]);
                       preEventCount++;
                       }
                   } 
                 if(EventIds!=null)
                     for(int i=0;i<EventIds.length;i++)
                         {
                          EventIdList[i+flagCount]=EventIds[i];
                          AllocatedTimeList[i+flagCount]=AllocatedTime[i];
                          AlertTimeList[i+flagCount]=AlertTime[i];
                         }
                   ServiceLevelObj.setEventIds(EventIdList);
                   ServiceLevelObj.setAllocatedTime(AllocatedTimeList);    
                   ServiceLevelObj.setAlertTime(AlertTimeList);
                     EventIdList		= null;	
                     AllocatedTimeList  = null;
                     AlertTimeList  = null;
                     strPreEventIds   = null;
                     strPreAllocatedTime  = null;
                     strPreAlertTime  = null;
                }
                
            }
            if(flagCount==0)
               {
                   ServiceLevelObj.setEventIds(EventIds);
                   ServiceLevelObj.setAllocatedTime(AllocatedTime);    
                   ServiceLevelObj.setAlertTime(AlertTime);
               }
              
 }
 // for View  and Delete 

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

	var allEventDescription = new Array();
	var allEventId = new Array();
	var AllocatedTime = new Array()
    var lertTime = new Array()
	EventIds					= new Array(<%=eventIdVal.length%>);
	EventDescs				= new Array(<%=eventIdVal.length%>);
	AllocatedTime			= new Array(<%=eventIdVal.length%>);
	AlertTime			= new Array(<%=eventIdVal.length%>);
	op="";
    op1="";
    flag1='No';
	var number				='1';
	totalEvent   = 0;
    Locatiion  ='No';  
    ControlList=new Array(<%=eventIdVal.length%>);
	function loadEventIds()
	{   
   
           
				document.forms[0].originLocationIds.value="";
				document.forms[0].destiLocationIds.value="";
     <% for(int i=0;i<eventIdVal.length;i++)
         {
	  %>
	    EventIds[<%=i%>]            = '';
        EventDescs[<%=i%>]	        = '';
        allEventDescription[<%=i%>]	= '<%=eventDescription[i]%>';
	   	allEventId[<%=i%>]	        = '<%=eventIdVal[i]%>';
        AllocatedTime[<%=i%>]       = 0;
        AlertTime[<%=i%>]           = 0;
		ControlList[<%=i%>]        ='';
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
}     
   %>
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
	   if(op!='')
		 {
         if(number<=0) number=1;
				   createForm(number,-1);
		 }
<%}%>
  }

	function stringFilter(input) 
	{
		s = input.value;
		input.value = s.toUpperCase();
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
					rowdata = rowdata +"<td width=25>&nbsp;&nbsp;&nbsp;&nbsp;</td>"
				}else
				{
         if((row!=-2) && (i==(number-1)))
		   			rowdata = rowdata +"<td width=25><input type=button value='<<' onClick='removeFormRow("+i+")' class='input'> </td>";
         else
           rowdata = rowdata +"<td width=25>&nbsp;&nbsp;&nbsp;&nbsp;</td>";
			  }
			rowdata = rowdata +"<td width=450 align='left'><select size=1 name=EventDesc  onChange='isOneMoreEvent();' class='select'><option selected value="+EventIds[i]+ ">"+ EventDescs[i] +"</option>"+op;
          if(flag1=='Yes')
                rowdata+=op1;
                rowdata+="</select></td>"
              	        +"<td width=150 align='center'><input type='text' class='text' name=AllocatedTime  class='select' maxlength=3 onkeyPress='return getDotNumberCode(this)' size=8 align='right'   value= "+inter+AllocatedTime[i]+inter+" ></td>"
				              	+"<td width=150 align='center' ><input type='text' class='text' maxlength=3 class='select' align='right' name=AlertTime  onkeyPress='return getDotNumberCode(this)' size=8 value= "+inter+AlertTime[i]+inter+"  ></td>"
       if((i == (number-1))&&(totalEvent>number))
				{
         if(row!=-2)
					  rowdata +="<td><input type='Button' size=10 name = nextButton  value='>>' disabled  onClick='createForm(++number,-1)' class='input'></td>";
          else
            rowdata+="<td width=25>&nbsp;</td>";
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

function removeRows()
  {
  operation='1';
  hf="";
  assignLocations();
  operation='2';
  hf="";
  assignLocations();
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

  function isOneMoreEvent()
    {
if (totalEvent>number)
{
  if(number==1)
     if(document.forms[0].EventDesc.value.length<1)
        <%if(eventIds==null) {
           %>
              removeRows();
        <%  }%>      
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

function getLocations(operation)
{
   var searchString='';
   var searchString1= '';
   if (operation=='1')
	{
      var len=document.forms[0].originLoc.length;
      var len1=document.forms[0].destiLoc.length;
	}
	else{
		 var len1=document.forms[0].originLoc.length;
          var len=document.forms[0].destiLoc.length;
	    }
	

	for(i=0;i<len;i++)
	{
	if (operation=='1')
			searchString= searchString + "-" + document.forms[0].originLoc.options[i].value ;
    else 
			searchString= searchString + "-" + document.forms[0].destiLoc.options[i].value ;
   	}
	for(i=0;i<len1;i++)
	 {
		if (operation=='2')
			searchString1= searchString1 + "-" + document.forms[0].originLoc.options[i].value ;
    else 
			searchString1= searchString1 + "-" + document.forms[0].destiLoc.options[i].value ;
	 }

		var myUrl = 'ETTaskLocSelectionLOV.jsp?searchString='+searchString+'&operation='+operation+'&searchString1='+searchString1;
        var myBars = 'directories=no,location=right,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=no,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		var newWin = open(myUrl,'myDoc',myFeatures);
		
} 
function assignLocations()
{
  operation=operation;
  if (operation=='1')
    var len1= window.document.forms[0].originLoc.length;
   else
    var len1= window.document.forms[0].destiLoc.length;
   var index=0;
   for(var i=0;i<len1;i++)
    {
     if(operation=='1')
        window.document.forms[0].originLoc.options.remove(index);
     else
       window.document.forms[0].destiLoc.options.remove(index);
    }
    str = hf;
    if(str.length>0)
    {
     if(operation=='1')
       document.forms[0].originLocationIds.value=str;
     else
        document.forms[0].destiLocationIds.value=str;
      entries = str.split("-");
      for(i=0;i<entries.length;i++)
      {
        if(entries[i] != "-" && entries[i]!="")
        {
         if(operation=='1')
             window.document.forms[0].originLoc.options[index] = new Option(entries[i] ,entries[i] )
         else
              window.document.forms[0].destiLoc.options[index] = new Option(entries[i] ,entries[i] )
        
          index++;
			    }
		     }
		   }
       else
        {
          if(operation=='1')
                  document.forms[0].originLocationIds.value="";
           else
                 document.forms[0].destiLocationIds.value=="";
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
 
<%if((operation.equals("View"))||(operation.equals("Delete")))
      {%>
   return true;
   <%}%>
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
            alert("Please Select Event ");
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
                  else if (document.forms[0].AlertTime[kk].value=="")
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
                   alert("Please Select Event ");
                   document.forms[0].EventDesc[kk].focus();
                   return false;
                 }
               }
		    }  
       
		 }//else close
        if(!findDuplicates())
		  return false;
 
      if(number==1)
	     {
        if((document.forms[0].originLocationIds.value=="")&&(document.forms[0].destiLocationIds.value==""))
	         {
		     if(document.forms[0].EventDesc.value.length >= 1)
			     {
				   alert("Please Select Origin Location's");
				   return false;
        		  } 
              else{
         		  return true;
			      }
			 }
         }
      else if (number >=1)
     	 {
		  if(document.forms[0].originLocationIds.value=="")
			 {
			  alert("Please Select Origin Location's");
			  return false;
			 }
		 }
    if(document.forms[0].originLocationIds.value=="")
         {
           alert("Please Select Origin Locations Also");
           document.forms[0].origin.focus();
           return false;
          }
     if(document.forms[0].destiLocationIds.value=="")
        {
           alert("Please Select Destination Locations Also");
           document.forms[0].desti.focus();
           return false;
         }
     if(document.forms[0].originLocationIds.value!="")
            if(number==1)
                if(document.forms[0].EventDesc.value.length<1)
                     {
                     <%if(eventIds==null) {%> 
                     alert("Please Select Event");
                     document.forms[0].EventDesc.focus();
                     return false;
                    <%}%> 
                     }
 }
<% if ((operation.equals("Modify"))&&(ServiceLevelObj!=null)&&(eventIds!=null && eventIds.length > 0 && ServiceLevelObj.getLocAllocatedTime().length>0)){%>
 if (document.forms[0].AllocatedTime1.length>0)
	 {
	 if(document.forms[0].AllocatedTime1.length == 1)
		 {
		 if ((parseFloat(document.forms[0].AllocatedTime1.value)==0)||(document.forms[0].AllocatedTime1.value=="")) 
			 {
			 alert("Please Enter Allocated Time");
             document.forms[0].AllocatedTime1.focus();
			 return false;
			 }
		if((parseFloat(document.forms[0].AllocatedTime1.value))<(parseFloat(document.forms[0].AlertTime1.value)))
			{
			 alert("Allocated Time must GreaterThan Alert Time");
			 document.forms[0].AllocatedTime1.focus();
			 return false;
			 } 
		 }
	 for(uu=0;uu<document.forms[0].AllocatedTime1.length;uu++)
		 {
		 if ((parseFloat(document.forms[0].AllocatedTime1[uu].value)==0)||(document.forms[0].AllocatedTime1[uu].value=="")) 
			 {
			 alert("Please Enter Allocated Time");
             document.forms[0].AllocatedTime1[uu].focus();
			 return false;
			 }
		 if((parseFloat(document.forms[0].AllocatedTime1[uu].value))<(parseFloat(document.forms[0].AlertTime1[uu].value)))
			{
			 alert("Allocated Time must GreaterThan Alert Time");
			 document.forms[0].AllocatedTime1[uu].focus();
			 return false;
			 }  
		 }
    if(document.forms[0].originLocationIds.value=="")
         {
           alert("Please Select Origin Locations Also");
           document.forms[0].origin.focus();
           return false;
          }
     if(document.forms[0].destiLocationIds.value=="")
        {
           alert("Please Select Destination Locations Also");
           document.forms[0].desti.focus();
           return false;
         }    
	 }
<%}%>
return true;
 }

function setOriginDestLoc()
  {
  var index1=0
  var originLocationIds='';
  var destiLocationIds='';

 <%if (originLocationIds!=null)
   {
  for(int i=0;i<originLocationIds.length;i++)
    {
%>  
      window.document.forms[0].originLoc.options[index1] = new Option('<%=originLocationIds[i]%>' ,'<%=originLocationIds[i]%>');  
      originLocationIds=originLocationIds+"-"+'<%=originLocationIds[i]%>';
     index1++;
 <% }}%>
 document.forms[0].originLocationIds.value=originLocationIds;
 index1=0;
  <%if (destLocationIds!=null)
   {
  for(int i=0;i<destLocationIds.length;i++)
    {
%>
      window.document.forms[0].destiLoc.options[index1] = new Option('<%=destLocationIds[i]%>' ,'<%=destLocationIds[i]%>');  
      destiLocationIds=destiLocationIds+"-"+'<%=destLocationIds[i]%>';
     index1++;
 <% }}%>
 document.forms[0].destiLocationIds.value=destiLocationIds;
 
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
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<%
			String actionValue=null;
    
    		if (operation.equals("View") )
      			actionValue="ETCServiceLevelEnterId.jsp?Operation="+operation;
    		else 
	      		actionValue="ETCServiceLevelProcess.jsp";
%>
<%   String submitValue="";
			if (operation.equals("View") )
				submitValue="Continue";
			else
				submitValue="Submit";
%>    
<body   onLoad="loadEventIds(); <% if (originLocationIds!=null) {%>setOriginDestLoc();<%}%>">
<form method="POST" onSubmit ="return checkSeviceLevelData()" action='<%=actionValue%>' name="servlevel">
<input type='hidden' name=originLocationIds>
<input type='hidden' name=destiLocationIds>
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff"> 
    <td  colspan="2">
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td>Service Level - <%=operation%></td>
          <% // @@ Added by Sailaja 0n 2005 05 26 for SPETI-1768 %>
          <td align = right>QS1000523</td>
          <% // @@ 2005 05 26 for SPETI-1768 %>
        </tr>
        </table>
        <table width=800  cellpadding=4 cellspacing=1  bgcolor='#ffffff'>  	
          <tr align='center' valign=top class='formheader'>
            <td width=25>&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td width=460  align=center>Event</td>
            <td width=145  align=center >Allocated Time (mm)</td>
            <td width=145  align=center>Alert Time<br>(mm)</td>
            <td width=25>&nbsp;</td>
          </tr>
 <% if((operation.equals("Modify"))||(operation.equals("Delete"))||(operation.equals("View")))
{ 
     if (eventIds!=null)
          {
          AllocatedTime=ServiceLevelObj.getLocAllocatedTime();
          AlertTime=ServiceLevelObj.getLocAlertTime();
             if(operation.equals("View"))
                session.removeAttribute("Service");
          for(int i=0;i<eventIds.length;i++)
              {
 %>       
         <tr class='formdata'>
                <%if(operation.equals("Modify")){%>
                <td width=25><input type=checkbox name=evntDelete class='formdata' value='Y' onclick=setevntDeleteValue(<%=i%>)>
                <input type=hidden name=evntDeleteValue value='N'></td>
                 <%}else {%>
                <td width=25>&nbsp;</td>
                <%}%>
                 <td width=460 align='left' >
                     <label class='select'><%=eventDesc[i]%></label>
                     <input type=hidden name=EventDesc1 value='<%=eventIds[i]%>'>
                     
                </td>
                  <td width=145 align='center'>
                      <input type='text' class='text' name=AllocatedTime1  <%=readOnly%> class='select'  size=8 align='right' maxlength=3   value= '<%=AllocatedTime[i]%>'  onkeyPress='return getDotNumberCode(this)'>
                </td>
                <td width=145 align='center' >
                        <input type='text' class='text'  align='right' class='select'  <%=readOnly%> maxlength=3   name=AlertTime1  size=8 value= '<%=AlertTime[i]%>' onkeyPress='return getDotNumberCode(this)'>
                </td>
                <td width=25>&nbsp;</td>
 </tr>
               
  <%           }
      }
    }%>          
         </table>
           <span id=cust style="POSITION: relative" ;> </span>   
       </table>                 
        </td></tr></table>   
         <table width=800  cellpadding=4 cellspacing=1  bgcolor='#ffffff'>
        <tr class='formdata' align='center'>
	          <td width =25>&nbsp;</td>
			  <% // @@ Modified by Sailaja 0n 2005 05 26 for SPETI-1768 %>
			  <td width=100 align="center">
			   Origin :    
			  </td>
               <td width=500 >&nbsp;</td><td align="center"> Destination: </td>
			  <td width=10>&nbsp;</td>
	 <tr>
     <tr class ='formdata' align='center'>
	     	  <td width=25>&nbsp;</td>
			  <td width=100>
			        <select name=originLoc size=10 class='select' ></select>
					<input type="button" name="origin" value="..." align="bottom" class='input' <% if ((operation.equals("Add"))||(operation.equals("Modify"))){%> onClick='getLocations(1)' <%}%>></td>
			  <td width=580>&nbsp;</td>
			  <td width=100>
			        <select name =destiLoc size=10 class='select'></select>
					<input type="button" name="desti" value="..." align="bottom" class='input' <% if ((operation.equals("Add"))||(operation.equals("Modify"))){%> onClick='getLocations(2)' <%}%>></td>
			  <td width=10>&nbsp;</td>
			  <% // @@ 2005 05 26 for SPETI-1768 %>
   </table>
     
     <table border='0' width='800' cellpadding='4' cellspacing='1'>
                        <tr bgcolor='ffffff'>
                            <td valign='top' colspan=5 align='right'>
                                  <input type='submit' class='input' value='<%=submitValue%>' name='Submit'></td></tr> </table>
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
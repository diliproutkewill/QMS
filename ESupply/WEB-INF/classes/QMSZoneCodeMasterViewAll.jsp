
<%
/*	Programme Name : QMSZoneCodeMasterViewAll.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : ZoneCodeMaster
*	Sub Task Name  : For viewAll and Invalidate modules
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import ="java.util.ArrayList,
                 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.java.*,
				 com.qms.setup.java.ZoneCodeMasterDOB,
				 com.qms.setup.java.ZoneCodeChildDOB,
				 com.qms.setup.ejb.sls.QMSSetUpSession,
				 com.qms.setup.ejb.sls.QMSSetUpSessionHome,		
				 java.util.HashMap,
				 javax.naming.InitialContext;
				 "
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
  public final static String   FILE_NAME = "QMSZoneCodeMasterViewAll.jsp";%>
<%
    logger  = Logger.getLogger(FILE_NAME);
    String						operation			=	request.getParameter("Operation");
	QMSSetUpSessionHome			home				=	null;
	QMSSetUpSession				remote				=	null;
	InitialContext				ictxt				=	null;
	ArrayList					zoneList			=	null;
	ZoneCodeMasterDOB			dob					=	null;
	ArrayList					requiredAttributes	=	new ArrayList();
	LOVListHandler				listHandler			=	null;
	ZoneCodeChildDOB            childDOB            =   null;
	ArrayList                   zoneChildList       =   null;
	String[]                    checkbox            =   null;
	String[]                    zoneCode            =   null;
	String[]                    rowNo               =   null;
	int							noOfSegments        =	Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
	try
	{
		checkbox   =  request.getParameterValues("checkBoxValue");
		zoneCode   =  request.getParameterValues("zonecode");
		rowNo      =  request.getParameterValues("rowNo");

		if(request.getParameter("pageNo")!=null)
		{
			try
			{
				String zoneCodeTemp   = "";
				listHandler           = (LOVListHandler)session.getAttribute("zoneList");
				requiredAttributes    = listHandler.requiredAttributes; 
				zoneList              = (ArrayList)session.getAttribute("zoneListTemp");
				if(zoneCode!=null && zoneCode.length>0)
  				   zoneCodeTemp       = zoneCode[0];
				for(int i=0;i<zoneList.size();i++)
				{
					dob  = (ZoneCodeMasterDOB)zoneList.get(i);
					//System.out.println("dob.getZoneCode()  "+dob.getZoneCode());
					if(dob.getZoneCode().equalsIgnoreCase(zoneCodeTemp))
					{   zoneChildList       = dob.getZoneCodeList();
						for(int j=0;j<zoneChildList.size();j++)
						{
							childDOB    =    (ZoneCodeChildDOB)zoneChildList.get(j);
							//System.out.println("checkbox.length "+checkbox.length);
								//System.out.println("zoneChildList.size() "+zoneChildList.size());
							if(checkbox.length==zoneChildList.size()){
								//System.out.println("checkbox[j] "+checkbox[j]);
								//System.out.println("rowNo[j] "+rowNo[j]);
								if("true".equals(checkbox[j]))
									childDOB.setInvalidate("T");							
								else
									childDOB.setInvalidate("F");
							}
							else
								break;
						}break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				listHandler = null;
			}
		}
		
		
		if(listHandler == null)
		{
			ictxt		=  new InitialContext();
			zoneList	=  new ArrayList();
			home		=  (QMSSetUpSessionHome)ictxt.lookup("QMSSetUpSessionBean");
			remote		=  (QMSSetUpSession)home.create();
			zoneList	=  remote.viewAllZoneCodeDetails();
			requiredAttributes.add(operation);
			session.setAttribute("zoneListTemp", zoneList);
		}
		if(request.getParameter("pageNo")!=null)
		{
			try
			{
				listHandler           = (LOVListHandler)session.getAttribute("zoneList");
				requiredAttributes    = listHandler.requiredAttributes; 
			}
			catch (Exception e)
			{
				listHandler = null;
			}
		}

		if(zoneList!=null)
		{
			listHandler   = new LOVListHandler(zoneList,1,requiredAttributes);
			session.setAttribute("zoneList", listHandler);			
			listHandler = (LOVListHandler)session.getAttribute("zoneList");
		}

		String pageNo			= request.getParameter("pageNo");
		if(pageNo == null)
		{
			pageNo = "1";
			listHandler.currentPageIndex = 1;
		}
		else
		{
			listHandler.currentPageIndex = Integer.parseInt(pageNo);
		}
		ArrayList	currentPageList	= listHandler.getCurrentPageData();
        //System.out.println("requiredAttributes.size"+requiredAttributes.size());
		if(requiredAttributes!=null)
        {
                 operation= (String)requiredAttributes.get(0);
				 System.out.println("operation"+operation);
        }
	


%>
<html>
<head>
<title>View All</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript" ></script>

<script>
function checkValues()
{
   var checkBox=document.getElementsByName("check");
   var checkBoxValue=document.getElementsByName("checkBoxValue");
   for(m=0;m<checkBox.length;m++)
   {
     if(checkBox[m].checked)
	 {
		checkBoxValue[m].value="true";
	 }
  }
   
}

function setVar()
{
  closeWindow = 'false';
}
function functionCall(pageNo)
{checkValues();
	document.forms[0].action='QMSZoneCodeMasterViewAll.jsp?pageNo='+pageNo;
	document.forms[0].submit();
}
function continue1()
{
	document.forms[0].action='QMSZoneCodeMasterViewAll.jsp?pageNo=1';
	document.forms[0].submit();
}
</script>
</head>
<body  onLoad="">
<form method="post" action="QMSZoneCodeMasterProcess.jsp"  onSubmit ="return checkValues();">
  <table width="100%" border="0" cellspacing="0" cellpadding="0" height="0">
    <tr bgcolor="#ffffff"> 
      <td height="0">
        <table width="100%" border="0" cellspacing="0" cellpadding="4" >
          <tr valign="top" class="formlabel" > 
            <td align="left"><b>Zone Code Master-
			   <%=operation%></b>
			 </td>
			 <td align="right"><b>QS1000141</b></td>
          </tr>
        </table>
<%
if(currentPageList.size() > 0)
{
	for( int j=0;j<currentPageList.size();j++ )
	{
				  dob  = (ZoneCodeMasterDOB)currentPageList.get(j);

%>
        <table width="100%" border="0" cellspacing="1" cellpadding="4" >
          <tr class='formdata' valign="top"> 
              <td><b>Shipment&nbsp;Mode:</b><br><%=dob.getShipmentMode()!=null?dob.getShipmentMode():""%></td>
              <td><b>Console&nbsp;Type:</b><br><%=dob.getConsoleType()!=null?dob.getConsoleType():""%></td>
			  <td><b>Zip Code Type :</b><br><%=dob.getZipCode()%></td>
          </tr>
		  <tr class='formdata'>
			  <td><b>Location:</b><br><%=dob.getOriginLocation()%></td>
			  <td><b>City :</b><br><%=dob.getCity()!=null?dob.getCity():""%></td>
			  <td><b>Terminal Id:</b><br><%=dob.getTerminalId()%></td>              
		  </tr>
		  </table>
		  <table width="100%" border="0" cellspacing="1" cellpadding="4" >
          <tr class='formheader' valign="top"> 
            <td > 
				 Zone
			</td>
            <td >
			     From Zip Code
            </td>
			<td colspan="3"> 
			     To Zip Code
            </td>
			<td> Estimated Time
			</td>
			<td>Estimated Distance
			</td>
			<td>
			<%if("Invalidate".equals(operation)){%>
			  Invalidate
			<%}%>
			</td>
          </tr>
		  <% 
			
			zoneChildList       = dob.getZoneCodeList();
			 for(int k=0;k<zoneChildList.size();k++){	
				 childDOB    =    (ZoneCodeChildDOB)zoneChildList.get(k);
				 if(!("ViewAll".equals(operation) && "T".equals(childDOB.getInvalidate()))){
				
		  %>
		
          <tr class='formdata' valign="top"> 
            <td > 
				   <input type="hidden" name="zone" value='<%=childDOB.getZone()%>'><%=childDOB.getZone()%>
			</td>
            <td >
			<%if("ALPHANUMERIC".equals(dob.getZipCode())){%>
			        <input type="hidden" name="alphaNumeric" value='<%=childDOB.getAlphaNumaric()%>'>
					<%=childDOB.getAlphaNumaric()!=null?childDOB.getAlphaNumaric():""%> -
			<%}%>
					<input type="hidden" name="fromZipCode" value='<%=childDOB.getFromZipCode()%>'>
					 <%=childDOB.getFromZipCode()%>
            </td>
			<td colspan="3"> 
			     <input type="hidden" name="toZipCode" value='<%=childDOB.getToZipCode()%>'><%=childDOB.getToZipCode()%>
            </td>
			<td > 
			     <input type="hidden" name="estimationTime" value='<%=childDOB.getEstimationTime()%>'><%=childDOB.getEstimationTime()%>
            </td>
			<td > 
			     <input type="hidden" name="estimatedDistance" value='<%=childDOB.getEstimatedDistance()%>'><%=childDOB.getEstimatedDistance()%>
				 <input type="hidden" name="estimatedDistance" value='<%=childDOB.getEstimatedDistance()%>'>				 
            </td>
			<%if("Invalidate".equals(operation)){%>
			<td colspan="4"> 
			     <input type='checkbox' name='check' <%="T".equals(childDOB.getInvalidate())?"checked":""%>>
				 <input type="hidden" name="checkBoxValue" value="">
				 <input type="hidden" name="zonecode" value='<%=childDOB.getZoneCode()%>'>
				 <input type="hidden" name="rowNo" value='<%=childDOB.getRowNo()%>'>
				 <input type="hidden" name="checked" >
            </td>
			<%}else{%>
			<td> 
			     
			</td>
			<%}%>
          </tr>        
		<%    }
			}%>
			<tr></tr>
		</table><%
		  
	}
}
		%>
<!-- <pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= FILE_NAME %>"/>
 -->
 <%
					int	 noofPages		=	0;	
					if(zoneList!=null)
						noofPages	= zoneList.size();

					int currentPageNo = Integer.parseInt(request.getParameter("pageNo")==null?"1":request.getParameter("pageNo"));

					if(currentPageNo != 1)
					{
%>
						<a href="#" onClick="functionCall('<%=currentPageNo-1%>')"><img src="images/Toolbar_backward.gif"></a>

 <%					}        
					
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;

					if(currentPageNo > noOfSegments)
					{
						multiplier = currentPageNo / noOfSegments;
						if(currentPageNo % noOfSegments != 0)
							startNo = noOfSegments * multiplier;
						else
							startNo = noOfSegments * (multiplier - 1);
					}
					if(noofPages > startNo)
					{
						if(noofPages > startNo + noOfSegments)
							endNo = startNo + noOfSegments;
						else
							endNo = startNo + (noofPages - startNo);
					} else
					{
						endNo = noofPages;
					}
					for(int i = startNo; i < endNo; i++)
						if(currentPageNo == i + 1){%>
							<font size="3"><B><%=(i + 1)%></B></font>&nbsp;
						<%}else{%>
							<a href="#" onClick="functionCall('<%=(i + 1)%>')"><%=(i + 1)%></a>&nbsp;  
<%	                       }
				if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick="functionCall('<%=currentPageNo+1%>')"><img src="images/Toolbar_forward.gif"></a>
<%					
				  }
%>
 <%if(!"ViewAll".equalsIgnoreCase(operation)){%>
         <table width="100%" border="0" cellspacing="0" cellpadding="0" height="0"> 
         <tr bgcolor="white">   		 
		 <td align="right">
			<input type="reset" name="Reset" value="Reset" onClick="placeFocus()" class='input'>		  	
		    <input type="submit" name="submit1" value="Submit" class='input'>             
			<input type="hidden" name="Operation" value = "<%=operation%>">
		</td>
        </tr>
        </table>
		<%}else{%>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" height="0"> 
         <tr bgcolor="white">   		 
		 <td align="right"><input type="button" name="continue" value="Continue" class='input' onClick='continue1()'> 
		 </td>
        </tr>
        </table>
		<%}%>
      </td>
    </tr>
  </table>
  <br>
</form>
</body>
</html>

<%}
	catch(Exception e)
	{
		e.printStackTrace();
	}
%>
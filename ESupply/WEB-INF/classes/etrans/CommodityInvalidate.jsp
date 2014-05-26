
<%
/*	Programme Name : CommodityInvalidate.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : TerminalInvalidate
*	Sub Task Name  : Modify/View/ViewAll/Delete Modules
*	Author		   : K.NARESH KUMAR REDDY
*	Date Started   : 28 June 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.ejb.sls.SetUpSession,
				   com.qms.setup.ejb.sls.SetUpSessionHome,
				   java.util.ArrayList,
				   java.util.HashMap,
				   java.util.StringTokenizer,
				   com.foursoft.etrans.setup.commodity.bean.CommodityJspBean,
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="CommodityInvalidate.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
  logger  = Logger.getLogger(FILE_NAME);
	SetUpSession       remote				=  null;
	SetUpSessionHome   home					=  null;
	CommodityJspBean     commodityDOB=null;
	ArrayList                 dobList				=  null;
	ArrayList                 keyValueList			=  null;
	InitialContext            ctxt					=  new InitialContext();
	String                    submitLabel			=  null;
	String                    nextNavigation		=  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                    checkInvalidate		=  "";   
	String                    code					=  "";
	String operation="Invalidate";
	String heading               = "";
try{
	home				=		(SetUpSessionHome)ctxt.lookup("SetUpSessionBean");
	remote				=		(SetUpSession)home.create();

		//shyam starts here
		int noofRecords	=	0;
		int noofPages	=	0;
		ArrayList			mainList	= null;
		String				sortBy                  =   request.getParameter("SortBy");
		String              sortOrder               =   request.getParameter("SortOrder");
		int		            pageNo                  =   Integer.parseInt(request.getParameter("PageNo"));
		String				noOfrecs1				= 	loginbean.getUserPreferences().getLovSize();
		int			noOfrecs	=	Integer.parseInt(noOfrecs1);
		//int			noOfrecs	=	10;

		String       imagePath     = "";
		 if("ASC".equalsIgnoreCase(sortOrder))
		   imagePath = request.getContextPath()+"/images/asc.gif";
		 else
		   imagePath = request.getContextPath()+"/images/desc.gif";

		//shyam ends here

	
	   // dobList     =  remote.getInvalidateCommodityDetails();
	    //shyam starts here
		mainList     =  remote.getInvalidateCommodityDetails(noOfrecs,pageNo,sortBy,sortOrder);
		Integer  no_ofRecords   =   (Integer)mainList.get(0);
		Integer  no_ofPages		=	(Integer)mainList.get(1);
		dobList				=	(ArrayList)mainList.get(2);
		noofRecords				=	no_ofRecords.intValue();
		noofPages				=	no_ofPages.intValue();
		commodityDOB     = new CommodityJspBean();
		HashMap                   mapDob                            =   null;
		ArrayList				  unCheckList						=	null;		
		mapDob			=	(HashMap)session.getAttribute("HashList");
		unCheckList			=	(ArrayList)session.getAttribute("unCheckList");

		//shyam ends here
		//if(mapDob!=null)
			//System.out.println("mapDob---"+mapDob.size());
		if(operation.equalsIgnoreCase("Invalidate"))
		{
			submitLabel="Submit";
			nextNavigation="ETCCommodityProcess.jsp?Operation=Invalidate";
		}
		String str="";
	       int count=0;
	     if(request.getParameter("queryStr")!=null)//Shyam condition
		 {
			 str=request.getParameter("queryStr");
			 count=Integer.parseInt(request.getParameter("noOfcols"));
		 }
		 else
		{
			if (request.getParameter("P_COMMODITYID")==null)//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="P_COMMODITYID,";
				count++;
				}
			if	(request.getParameter("P_COMMODITYTYPE")!=null && request.getParameter("P_COMMODITYTYPE").equals("NO")){
				str+="P_COMMODITYTYPE,";
				count++;
				}
			if	(request.getParameter("P_COMMODITYDESC")!=null && request.getParameter("P_COMMODITYDESC").equals("NO")){
				str+="P_COMMODITYDESC,";
				count++;
				}
			if	(request.getParameter("P_HANDLINGINFO")!=null && request.getParameter("P_HANDLINGINFO").equals("NO")){
				str+="P_HANDLINGINFO,";
				count++;
				}
		}
      
%>

<html>
<head>
<title>Density Group Code</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function checkValues()
{
   var checkBox=document.getElementsByName("check");
   var checkBoxValue=document.getElementsByName("checkBoxValue");
   for(m=0;m<checkBox.length;m++)
   {
     if(checkBox[m].checked)
		checkBoxValue[m].value="true";    	 
  }
   
   //shyam starts here
}

 function fetchSortedPageData(lable,pageNo,clickFrom)
	{
	   var sortBy     = lable;
	   var sortOrder  = "";   
	   if(lable==document.forms[0].sortedBy.value)
		{	 
		  if(clickFrom=="lable")
			{
			  if(document.forms[0].sortedOrder.value=="ASC")
				sortOrder = "DESC";
			  else
				sortOrder = "ASC";
			}
		  else
			sortOrder = document.forms[0].sortedOrder.value;
		}
		else
		{
			sortOrder = "ASC";
		}
		document.forms[0].action = "ETCCommodityProcess.jsp?SortBy="+sortBy+"&SortOrder="+sortOrder+"&PageNo="+pageNo;
		//alert(document.forms[0].action);
		document.forms[0].submit();
	}
	function CheckIt(obj,unCommId)
	{
		var index	=	obj.id;
		if(document.getElementsByName("check").length == 1)
		{
			if(document.forms[0].check.checked==false)
			{
				document.forms[0].unChecked.value	=	unCommId;
			}
			else
			{
				document.forms[0].unChecked.value	=	"";
			}
		}
		else
		{
			if(document.forms[0].check[index].checked==false)
			{
				document.forms[0].unChecked[index].value	=	unCommId;
			}
			else
			{
				document.forms[0].unChecked[index].value	=	"";
			}
		}
		//alert(document.forms[0].unChecked[index].value);
	}
	   //shyam ends here

</script>
<body >
<form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>' name="DensityGroupCode">
<!--<table border="0" cellpadding="4" cellspacing="1" width="803" height="83">
        <tbody>
          <tr class="formlabel">
            <td colspan="5" width="100%" height="21"><font size='3'><b>Commodity- <%=operation%></b></font></td>
          </tr>
          <tr class="formheader">
            <td width="100" height="1">CommodityId</td>
            <td width="550" height="1">CommodityType</td>
			<td width="350" height="1">CommodityDescription</td>
			<td width="100" height="1">HandlingInformation</td>-->
			<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }    
%>
<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Commodity Master - <%=operation%></td><td align="right">QS1222632</td></tr></table></td>
		  </tr></table>
		  <table width="100%" cellpadding="0" cellspacing="0" >
			<tr valign="top" class='formdata'> 
            <td > Terminal : <%=loginbean.getTerminalId()%></td>
            <td > User : <%=loginbean.getUserId()%></td>
            <td > Date : <%=loginbean.getCurrentDateString()%></td>
          </tr></table>
	<table width="100%"  border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF" >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("P_COMMODITYID"))
				heading="Commodity Id";
 			 if(heading.equals("P_COMMODITYTYPE"))
				heading="Commodity Type";	
			 if(heading.equals("P_COMMODITYDESC"))
				heading="Commodity Description";	
 			 if(heading.equals("P_HANDLINGINFO"))
				heading="Handling Info";	
%>
      			  <td width ="25%" >
				  
				  <A href='###' onClick='fetchSortedPageData("<%=heading%>","<%=pageNo%>","lable")' onmouseover='status="Sort by <%=heading%> as";return true;' onmouseout="status = '';return true;" title="Sort by <%=heading%> as">

				  <%=heading%>
			      </td>
<%   
		   }
if("Invalidate".equals(operation)){%>
			<td width="100" height="1">Invalidate</td>
			<%}%>
          </tr>
		  <%    code = "";
					for(int i=0;i<dobList.size();i++)
					{
						commodityDOB  = (CommodityJspBean)dobList.get(i);

		               
					   if("T".equals(commodityDOB.getInvalidate()))
						   checkInvalidate="checked";
					   else
						   checkInvalidate="";
		  %>
          <tr class="formdata">
            <td ><%=commodityDOB.getCommodityId()%></td>
			<%if(str.indexOf("P_COMMODITYTYPE")>0)
			{
			%>
            <td><%=commodityDOB.getCommodityType()%></td>
			<%}
			if(str.indexOf("P_COMMODITYDESC")>0)
			{
			%>
            <td><%=commodityDOB.getCommodityDescription()%></td>
			<%}
			  if(str.indexOf("P_HANDLINGINFO")>0)
			{
			%>
			<td><%=commodityDOB.getCommodityHandlingInfo()==null?"":commodityDOB.getCommodityHandlingInfo()%></td>
				<%
			}
				  if("Invalidate".equals(operation))
				  {
					String commId	=	"";
					//shyam starts here
						if(mapDob!=null)
						{
							commId	=	(String)mapDob.get(commodityDOB.getCommodityId());
						}
						
						if(commId!=null  && commId.equals(commodityDOB.getCommodityId()))
								 checkInvalidate="checked";
						
						if(unCheckList!=null)
					    {
							if(unCheckList.contains(commodityDOB.getCommodityId()))
							{
								checkInvalidate="";
							}
						}
			
					//shyam ends here
				
				%>
			<td><input type='checkbox' name='check' value="<%=commodityDOB.getCommodityId()%>"  id='<%=i%>' onClick="CheckIt(this,'<%=commodityDOB.getCommodityId()%>');" <%=checkInvalidate%>>
			<!-- <input type="hidden" name="checkBoxValue" value="false"></td> -->
				<input type="hidden" name="checkBoxValue" value="<%=commodityDOB.getCommodityId()%>">
				<input type="hidden" name="mfValues" value="<%=commodityDOB.getCommodityId()%>">
				<input type="hidden" name="unChecked" value="" id='<%=i%>'></td>
				<%
				  }
				%>
          </tr> 
		  <%
					}
		  %>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
        <tbody>
		<% 		 session.setAttribute("dobList",dobList);

			 %>
          <tr >
            <td align="right" colspan="6" width="100%">
						<!-- shyam starts here -->
      <%         int  currentPageNo =pageNo;
				 if(currentPageNo != 1)
			      {%>
			     <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo-1%>","pageNo")'><img src="../images/Toolbar_backward.gif"  border="0"></a>
     <%			  } 
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;
					int noOfSegments	=	Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());

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
							<%=(i + 1)%>&nbsp;
						<%}else{%>
							<a href="#" onClick="fetchSortedPageData('<%=sortBy%>','<%=(i + 1)%>','pageNo')" ><%=(i + 1)%></a>&nbsp;
<%	                   }
				  
				  if(currentPageNo != noofPages)
			      { %>
                   <a href="#" onClick='fetchSortedPageData("<%=sortBy%>","<%=currentPageNo+1%>","pageNo")'><img src="../images/Toolbar_forward.gif" border="0"></a>
   <%		      }

    %>        <!-- shyam ends here -->


			<input type="button" value="<< Back" name="Back" onClick="javascript:history.back()" class='input'>
			<input class='input' name='Submit' type='Submit' value='<%=submitLabel%>' "border-style: solid">
			<input type="hidden" name="Operation" value='<%=operation%>'>
			
			<!-- Shyam starts here -->
			<INPUT TYPE="hidden" name='sortedOrder' value="<%=sortOrder%>">
			<INPUT TYPE="hidden" name='sortedBy' value="<%=sortBy%>">
			<INPUT TYPE="hidden" name='pageNo' value="<%=pageNo%>">
			<INPUT TYPE="hidden" name='queryStr' value="<%=str%>">
			<INPUT TYPE="hidden" name='noOfcols' value="<%=count%>">

			<!-- Shyam ends here -->
            </td>
          </tr>
        </tbody>
      </table>

</body>

</html>
<%}
  catch(Exception e)
  {
	  //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeView.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSDensityGroupCodeView.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSDensityGroupCodeAdd.jsp");
	  keyValueList   =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%

  }
%>
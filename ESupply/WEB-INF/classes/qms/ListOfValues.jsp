
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteMaster.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 8th Aug 2005 	
Date modified		:  
Author    			: Madhu.Y
Related Document	: CR_DHLQMS_1007

--%>
<%@page import = "java.util.ArrayList,
				  java.sql.Timestamp,
				  javax.ejb.ObjectNotFoundException,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.setup.java.QMSAdvSearchColsDOB,
				  com.qms.setup.java.QMSAdvSearchLOVDOB,
				  com.qms.setup.java.QMSAdvSearchHelperObj"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!	
  private static Logger logger = null;
  private static final String FILE_NAME	=	"ListOfValues.jsp"; %>

<%

    logger  = Logger.getLogger(FILE_NAME);	
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		long start=System.currentTimeMillis();
        String localTerminal   =    null;
		String localAcceslevel =    null;
		String where           =	null;
		String lovid           =	null;
		String tabCols         =	null;
		String formCols        =	null;
		String sortType        =	null;
		String sortField       =	null;
		String currentPage1    =	null;
		String designationID   =	null;
		String operation	   =	null;
		String shipmentMode	   =	null;
		String temp			   =	"";
		String	search		   =	null;
		String title			=	null;
		String pagination		=	null;
		String addressId		=   null;//@@Added by Kameswari for the issue-61314
		int noOfRecPerPage		=	0;
		int colCount			=	0;
		int recCount			=	0;
		int winWidth			=	0;
		int winHeight			=	0;

		StringBuffer sb         =   null;
		ArrayList	valueList	=	null;
		ArrayList	colsList	=	null;
		ArrayList	dataList	=	null;
		ArrayList  addressList  =	null;
		QMSAdvSearchLOVDOB	advSerchLov			=	null;
		QMSAdvSearchColsDOB advSerchColsLov		=	null;
		ESupplyDateUtility  eSupplyDateUtility	=	new ESupplyDateUtility();
		String              dateFormat			=	loginbean.getUserPreferences().getDateFormat();
		int relativeOffset	   =	loginbean.getRelativeOffset();
		Timestamp localTime	   =	null;
		String terminalId=null;//150461
		String Operation = request.getParameter("Operation");
		String loginTerminal = loginbean.getTerminalId();//@@ Added by subrahmanyam for the wpbn issue: 150461 on 30/12/08
		String	multiQuote  =  null;	 //Added by Rakesh
		
		

		try
		{
			eSupplyDateUtility.setPatternWithTime(dateFormat);
		}
		catch(Exception exp)
		{
			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
			logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
		}

		try
		{
			where				=	request.getParameter("lovWhere");
			addressList         =   (ArrayList)session.getAttribute("addressList");
			if(where!=null)
				where  = where.replaceAll("~","%");
			search				=	request.getParameter("search");
					

			
			//if(search!=null)
				//search	=	search..replaceAll("~","%");
			lovid				=	request.getParameter("lovid");
			tabCols				=	request.getParameter("tabArray");
			formCols			=	request.getParameter("formArray");
			sortType            =	request.getParameter("sortType");
			sortField           =	request.getParameter("sortField");
			currentPage1		=	request.getParameter("pg");
			designationID		=	request.getParameter("designationID");
			operation			=	request.getParameter("operation");
			shipmentMode		=   request.getParameter("shipmentMode");
			terminalId           =  request.getParameter("terminalId");
			localTerminal        =  request.getParameter("localTerminal");//added by VLAKSHMI on 22/05/2009
			localAcceslevel      =  request.getParameter("localAcceslevel");//added by VLAKSHMI on 22/05/2009
			multiQuote      =  request.getParameter("multiQuote");//added by Rakesh on 16-03-2011
			valueList	=	(ArrayList)session.getAttribute("lovData");
			session.removeAttribute("lovData");
			
			if(valueList==null)	
			{	
				response.sendRedirect("../QMSAdvSerchLovController?lovWhere="+search+"&pg=1&lovid="+lovid+"&tabArray="+tabCols+"&formArray="+formCols+"&sortType="+sortType+"&sortField="+sortField+"&operation="+operation+"&designationID="+designationID+"&shipmentMode="+shipmentMode+"&search="+search+"&terminalId="+terminalId+"&localTerminal="+localTerminal+"&localAcceslevel="+localAcceslevel+"&multiQuote="+multiQuote);	  //Modified by Rakesh
			}		
			else
			{
				advSerchLov	=	(QMSAdvSearchLOVDOB)valueList.get(0);
				colsList	=	(ArrayList)valueList.get(1);
				dataList	=	(ArrayList)valueList.get(2);
			}
			
			

%>

 <html>
  <head><title>List of Values</title>
  <STYLE type="text/css">
  A:link { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: none }
  A:hover { COLOR: blue; FONT-WEIGHT: none; TEXT-DECORATION: underline }
  A:visited { color: blue; font-weight: none; text-decoration: none }
  </STYLE>
  <META HTTP-EQUIV= "PRAGMA"CONTENT="NO-CACHE">
  <META HTTP-EQUIV="Expires" CONTENT="Tue, 04 Dec 1996 21:29:02 GMT"> 
  <META HTTP-EQUIV="Cache-control" CONTENT="no-cache">
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache"> 
  <META HTTP-EQUIV="Last-Modified" CONTENT="Tue, 04 Dec 2099 21:29:02 GMT">
  <LINK REL=stylesheet TYPE="text/css" HREF="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
  <script language="javascript">
	
   function pgSelect(pg)
   {
		document.theform.pg.value=pg;

		document.theform.action ="QMSAdvSerchLovController";
		document.theform.submit();
   }
     
   //Check at the time of search user entered any value or not
   function isAllEmpty()
   {   
		for(var i=0;i<document.theform.elements.length;i++)
		{
			if(document.theform.elements[i].type=='text')
			{
				
				if(!isEmpty(document.theform.elements[i])) 
				{	
					
				   return false;
			    }
			}
		}
		return true;
   }
   //Check at the time of search user entered any value or not end
   
   function clckFldSearch()
   {
	 
    
	   // var searchParam="";//@@ Commented by subrahmanyam for the wpbn issue: 150461 on 30/12/08
//@@ Added by subrahmanyam for the wpbn issue: 150461 on 30/12/08
			
	   var searchParam1 = document.theform.search.value;
	   if(searchParam1.indexOf('terminalid')!=-1)
		   var searchParam="terminalid='<%=loginTerminal%>' and ";
	   else if(searchParam1.indexOf('Corporate')!=-1)
	   {
		   var searchParam="CUSTOMERID IN(SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERTYPE='Corporate') AND ";
	   }
	   else
	   var searchParam="";
//@@ Ended by subrahmanyam for the wpbn issue: 150461 on 30/12/08
	   if (navigator.appName == "Microsoft Internet Explorer")
	   {
	     tmp = window.event.keyCode;
	   }
	   else
	   {
	     tmp = e.which;
	   }	   
		
		if(isAllEmpty() && tmp == 13)
		{
			document.theform.where.value=document.theform.lovWhere.value; //document.theform.lovWhere.value;
			document.theform.search.value=""; //document.theform.lovWhere.value;
			document.theform.action ="QMSAdvSerchLovController";
			document.theform.submit();
		}
		else if(tmp==13)
		{		
	   //For loop Start	
	   		   var val;
			   var value = new Array();
			   var andflag=false;			   
			   for(var i=0;i<document.theform.elements.length;i++)
			   {
				     if(document.theform.elements[i].type=='text')
					 {
						   var fldName		=	document.theform.elements[i].name;

						   sanitise(document.theform.elements[i]);
						   //@@Added by kameswari for the issue-54847
						   if(fldName.toUpperCase()=='BASIS_DESCRIPTION')
						  {
						       val			=	document.theform.elements[i].value;
							  value = val.split(" ");
							    for(var k=0;k<value.length;k++)
							  {
							   
								  var m=value[k].toString().charAt(0).toUpperCase();
							
								  var p =value[k].toString().substring(1,  (value[k].toString().length)).toLowerCase();
								  value[k]= m+p;
								 // alert(value[k]);
								 if(k==0)
								  {
								  val = value[k];
								  }
								  else
								  {
								   val = val+" "+value[k];
								  }
								 
							  }
						  }
						  else
						 {
						    val			=	document.theform.elements[i].value.toUpperCase();
						  }
						   var operation	=	document.theform.operation.value;
						   
						   	if(andflag==false)
							{ 	
								if(!isEmptyValue(val))
								{
									
								  
									if(fldName.toUpperCase()=='COMPANYNAME' && (operation.toUpperCase()=='QUOTECUSTOMER' || operation.toUpperCase()=='COSTINGQUOTEID') &&(operation.toUpperCase()=='QUOTEMODIFY'))
									{
										alert("B");
										searchParam =  searchParam + fldName + " like '%"+val+"%' " ;
										
									}
									else if(fldName.toUpperCase()=='SHIPMENTMODE')
									{
										if(val=='AIR' || val=='1')
										{
											val	=	"1,3,5,7";
										}
										else if(val=='SEA' || val=='2')
										{
											val	=	"2,3,6,7";
										}
										else if(val=='TRUCK' || val=='4')
										{
											val	=	"4,5,6,7";
										}
										else
										{
											//if (tmp == 13) 
											//{
												alert("The Valid Search Parameters For This Field are Air/1, Sea/2 or Truck/4");
												document.theform.elements[i].value = '';
												document.theform.elements[i].focus();
												return false;
											//}
										}
										searchParam =  searchParam + fldName + " in ("+val+")" ;
									}
									//@@Added by Kameswari for the WPBN issue-26514
									else if(fldName.toUpperCase()=='QUOTE_STATUS')
									{
									   //  if (tmp == 13) 
										//{
											val=val.substring(0,3);
											 if(val=="QUE")
											{
											   val="QUE";
											}
										   else  if(val=="GEN")
											{
											   val="GEN";
											}
											else  if(val=="POS")
											{
											   val="ACC";
											}
										   else  if(val=="NEG")
											{
											   val="NAC";
											}
											else  if(val=="PEN")
											{
											   val="PEN";
											}
										   else	 if(val=="APP")
											{
											   val="APP";
											}
										   else	 if(val=="REJ")
											{
											   val="REJ";
											}
											else if(val.length<3)
											{
												alert("Please enter atleast three characters");
												document.theform.elements[i].value = '';
												document.theform.elements[i].focus();
												return false;
											}
										//}
									    
									    
                                        searchParam =  searchParam + fldName + " like '"+val+"%' " ;
									}
									else if(fldName.toUpperCase()=='ACTIVE_FLAG')
									{
										//if(tmp==13)
										//{
											
											if(val.charAt(0)=="A")
											{
												val="A";
											}
											else if(val.charAt(0)=="I")
											{
												val="I";
											}
											
									//	}

										searchParam =  searchParam + fldName + " like '"+val+"%' " ;
									}
									//@@WPBN issue-26514
									//added by silpa.p on 8-06-11 
									else if(fldName == 'SURCHARGE_DESC')
									{
										searchParam =  searchParam + "UPPER("+fldName +")"+ " like '"+val+"%' " ;
									}
									
										else if(fldName == 'RATE_BREAK')
									{
										searchParam =  searchParam + "UPPER("+fldName +")"+ " like '"+val+"%' " ;
									}
									
										else if(fldName == 'RATE_TYPE')
									{
										searchParam =  searchParam + "UPPER("+fldName +")"+ " like '"+val+"%' " ;
									}
									
										else if(fldName == 'WEIGHT_BREAKS')
									{
										searchParam =  searchParam + "UPPER("+fldName +")"+ " like '"+val+"%' " ;
									}//ended
									else
									{
				              			searchParam =  searchParam + fldName + " like '"+val+"%' " ;
									}
									andflag=true;

									//if(tmp==13)
										document.theform.pg.value='1';
									
								}								
							}
				           	else
							{
						   		if(!isEmptyValue(val))
								{
									if(fldName.toUpperCase()=='COMPANYNAME' && operation.toUpperCase()=='QUOTECUSTOMER')
									{
										searchParam =  searchParam + " and " + fldName + " like '%"+val+"%' " ;
									}
									else if(fldName.toUpperCase()=='SHIPMENTMODE')
									{
										if(val=='AIR' || val=='1')
										{
											val	=	"1,3,5,7";
										}
										else if(val=='SEA' || val=='2')
										{
											val	=	"2,3,6,7";
										}
										else if(val=='TRUCK' || val=='4')
										{
											val	=	"4,5,6,7";
										}
										else
										{
											//if (tmp == 13) 
											//{
												alert("The Valid Search Parameters For This Field are Air/1, Sea/2 or Truck/4");
												document.theform.elements[i].value = '';
												document.theform.elements[i].focus();
												return false;
											//}
										}
										searchParam =  searchParam + " and " + fldName + " in ("+val+")" ;
									}
									//@@Added by Kameswari for the WPBN issue-26514
									else if(fldName.toUpperCase()=='QUOTE_STATUS')
									{
									   //   if (tmp == 13) 
									//	{
											 val=val.substring(0,3);
											 if(val=="QUE")
											{
											   val="QUE";
											}
											 else  if(val=="GEN")
											{
											   val="GEN";
											}
											else  if(val=="POS")
											{
											   val="ACC";
											}
										   else  if(val=="NEG")
											{
											   val="NAC";
											}
											else  if(val=="PEN")
											{
											   val="PEN";
											}
										   else	 if(val=="APP")
											{
											   val="APP";
											}
										   else	 if(val=="REJ")
											{
											   val="REJ";
											}
											else if(val.length<3)
											{
												alert("Please enter atleast three characters");
												document.theform.elements[i].value = '';
												document.theform.elements[i].focus();
												return false;
											}
									//	}
									    
                                        searchParam =  searchParam + " and " + fldName + "  like '"+val+"%' " ;
									}
										else if(fldName.toUpperCase()=='ACTIVE_FLAG')
								    	{
											//if(tmp==13)
										//	{
										
											
											if(val.charAt(0)=="A")
											{
												val="A";
											}
											else if(val.charAt(0)=="I")
											{
												val="I";
										//	}
											
										}
									
										searchParam =  searchParam + " and " + fldName + " like '"+val+"%' " ;
									}
									//@@WPBN issue-26514
									else
									{
				              			searchParam =  searchParam + " and " + fldName + " like '"+val+"%' " ;
										
									}
									//if(tmp==13)
										document.theform.pg.value='1';
								}
							}
					 }
			   }			   
			   if(!isEmpty(document.theform.lovWhere))
			   {
				 searchParam = " where  " + searchParam;
					
			   }
			   else
			   {
			     	if(andflag && !isEmptyValue(searchParam))
					{				
						searchParam = " where  " + searchParam;
					}
			   }			   
		// For loop end			  		
			  // if (tmp == 13) 
			  // {
				 //document.theform.pg.value= 1;
				 //alert(document.theform.sortField.value.length);
				 //alert(document.theform.sortType.value);
				 //alert(searchParam);

				 if(document.theform.sortField.value!=null && document.theform.sortField.value!='null' && document.theform.sortField.value.length!=0 )
					document.theform.search.value	= searchParam+ " order by "+document.theform.sortField.value+" "+document.theform.sortType.value;
				else
					document.theform.search.value	= searchParam;

				

				 document.theform.where.value	= searchParam;
				 document.theform.lovWhere.value	= searchParam;
				document.theform.action ="QMSAdvSerchLovController";
				document.theform.submit();
			 //  }
		}
	  

   }
   function sanitise(input)
   {
		s = input.value;
		filteredValues = "';<>-";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++)
		{
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 )
					returnString += c;
			else
				flag = 1;
		}
		input.value = returnString;
   }
   function isEmpty(element)
	{
      var s,i,c;
	   s = element.value;
	  for (i = 0; i < s.length; i++) {
		c = s.charAt(i);
		if(c==" ");
		else
			return false;
		}
		element.value="";
      return true
	}		
    function isEmptyValue(val)
	{
      var s,i,c;
	  
		for (i = 0; i < val.length; i++) {
		c = val.charAt(i);
		if(c==" ");
		else
			return false;
		}
      return true
	}   
  function sortLov(fld,sortType) 
  {
  		var  sortClause	=	'';
		var mode	= '';
	
	if(!isAllEmpty())
	{
		var whereClause =  document.theform.where.value;
			if(document.forms[0].lovid.value == 'SURCHARGE_LOV'){
				if(document.forms[0].shipmentMode.value == 'Air')
					mode ='1';
				else if(document.forms[0].shipmentMode.value == 'Sea')
					mode='2';
				else if(document.forms[0].shipmentMode.value == 'Sea')
					mode='4';
			   sortClause =  " where shipment_mode="+mode+" order by "+fld+" "+ sortType;
			}
			else
			   sortClause =  " order by "+fld+" "+ sortType;
		 //whereClause +  sortClause
		 document.theform.search.value = whereClause +  sortClause; 
		// document.theform.search.value =  sortClause; 
		 document.theform.sortType.value = sortType; 
		 document.theform.sortField.value = fld;
		 document.theform.action ="QMSAdvSerchLovController";
		 //document.theform.action="ListOfValues.jsp";
		 document.theform.submit();
	}
	else
	{
		
		var whereClause =  "";
			if(document.forms[0].lovid.value == 'SURCHARGE_LOV'){
				if(document.forms[0].shipmentMode.value == 'Air')
					mode ='1';
				else if(document.forms[0].shipmentMode.value == 'Sea')
					mode='2';
				else if(document.forms[0].shipmentMode.value == 'Sea')
					mode='4';
			   sortClause =  " where shipment_mode="+mode+" order by "+fld+" "+ sortType;
			}
			else
			   sortClause =  " order by "+fld+" "+ sortType;
			 //whereClause +  sortClause
		 document.theform.search.value = whereClause +  sortClause; 
		 //alert(document.theform.search.value);
		// document.theform.search.value =  sortClause; 
		 document.theform.sortType.value = sortType; 
		 document.theform.sortField.value = fld;
		 //alert(sortClause)
		 document.theform.action ="QMSAdvSerchLovController";
		 //document.theform.action="ListOfValues.jsp";
		 document.theform.submit();
	}
	 
  }
 function clckLink(recno)
 {
	 name	=	document.theform.formArray.value;
	 name1	=	document.theform.tabArray.value;
	if(name.indexOf(",")!=-1)
	 { 
		 var names = name.split(",");
		 var names1 = name1.split(",");
		 for(i=0;i<names.length;i++)
		 {
			 tabField  = eval("document.theform."+names1[i]+""+recno+"");
			 formField = eval("window.opener.document.forms[0]."+names[i]+"");
			 formField.value=tabField.value;
			 //Added by Rakesh on 29-03-2011
			 <% if("SURCHARGE_LOV".equalsIgnoreCase(lovid)){ %>
			 if(i==names.length-1)//Added by Anil.k for Spot Rates
			 formField.focus();
			 <%}%>
		 }
	 }
	 else
	 {
		 tabField  = eval("document.theform."+name1+""+recno+"");
		 formField = eval("window.opener.document.forms[0]."+name+"");	
		 formField.value=tabField.value;
		 if(name1 == 'CUSTOMERID')
          formField.focus();
	 }
	
	 if(document.forms[0].operation.value.toUpperCase()=='QUOTECUSTOMER')
	 {
		if(window.parent.opener.document.forms[0].address!=null)
		 {
			//@@Added by kameswari for the WPBN issue-61314
			<%if(addressList!=null)
			 {
				
				for(int i=0;i<addressList.size();i=i+8)
			 {
						%>
				
				 
					if(formField.value=='<%=addressList.get(i)%>')
				 {
					window.parent.opener.document.forms[0].addressId.value ='<%=addressList.get(i+1)%>';	window.parent.opener.document.forms[0].address.value="<%=(addressList.get(i+2)!=null)?addressList.get(i+2):""%>"+'\n'+"<%=(addressList.get(i+3)!=null)?addressList.get(i+3):""%>"+'\n'+"<%=(addressList.get(i+4)!=null)?addressList.get(i+4):""%>"+'\n'+"<%=(addressList.get(i+5)!=null)?addressList.get(i+5):""%>"+'\n'+"<%=(addressList.get(i+6)!=null)?addressList.get(i+6):""%>"+'\n'+"<%=(addressList.get(i+7)!=null)?addressList.get(i+7):""%>";
					   
				 }
				
			 <%}
			 }%>
				 //@@WPBN issue-61314
		 }
		if(window.parent.opener.document.forms[0].contactPersons!=null)
			window.parent.opener.document.forms[0].contactPersons.length=0;
	 }
	 if(document.forms[0].operation.value.toUpperCase()=='SELLCHARGEBASIS')
	 {
		window.opener.document.forms[0].chargeBasisId.onchange();
	 }
	 else if(document.forms[0].operation.value.toUpperCase()=='LOCATIONS' && window.opener.document.forms[0].city!=null)
	 {
		 window.opener.document.forms[0].city.value = eval("document.theform.CITY"+recno+".value");
	 }
     window.close();    
 }
	//onload='javascript:windowLoad();
 </script>
 </head>
 <body MARGINWIDTH="0" MARGINHEIGHT="0" onload='' LEFTMARGIN="0" TOPMARGIN="0" BOTTOMMARGIN="0" RIGHTMARGIN="0"  bgcolor='#FFFFFF'>
   <form name="theform" method="post">
   <table width="100%"  bgcolor='#FFFFFF'>
	<tr><td class="COLHEAD" style="text-align:left" colspan="20">
	</td></tr>
    <tr class="COLHEAD">
	<td>
	<%
		if(valueList!=null)
		{
			title			=	advSerchLov.getTitle();
			pagination		=	advSerchLov.getPagination();
			noOfRecPerPage	=	advSerchLov.getNoOfRecPerPage();
 		colCount		=	advSerchLov.getColCount();
			recCount		=	advSerchLov.getRecCount();
			winWidth		=	advSerchLov.getWinWidth();
			winHeight		=	advSerchLov.getWinHeight();
			
%>
		<table width="100%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
		<tr class="formlabel"><td><%=title!=null?title.toUpperCase():"ADVANCED SEARCH"%> LOV</td></table>
		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
			<tr class='formdata'>
				<td  class='formdata'>&nbsp;&nbsp;</td>
				<td class='formdata' colspan="<%=colCount%>" align='right'></td>
			</tr>
			<tr class='formheader'>
<%
			int colsListSize	=	colsList.size();
			String[] colId			=	new	String[colsListSize];
			String[] colDesc		=	new	String[colsListSize];
			String[] colWidth		=	new	String[colsListSize];
			String[] sotrtOfString	=	new	String[colsListSize];
			ArrayList	list1		=	null;
			for(int i=0;i<colsListSize;i++)
			{
				 advSerchColsLov		=	(QMSAdvSearchColsDOB)colsList.get(i);
				 colId[i]			=	advSerchColsLov.getColId();
			//	 System.out.println(colId[i]);
				 colDesc[i]			=	advSerchColsLov.getColDesc();	
				 colWidth[i]		=	advSerchColsLov.getColWidth();
				 sotrtOfString[i]	=	advSerchColsLov.getSortType();

%>
<input type='hidden' value=''>
				<td  nowrap CLASS='formdata'>
				<!--@@Added by Kameswari for the WPBN issue-61234-->
				<%if("View".equalsIgnoreCase(request.getParameter("Operation")))%>
					<input type='text' name='searchParam' value="where  CUSTOMERID IN(SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERTYPE='Corporate' AND CUSTOMERID LIKE"/>
					<%if("Modify".equalsIgnoreCase(request.getParameter("Operation")) || "Delete".equalsIgnoreCase(request.getParameter("Operation")))%>
					<input type='text' name='searchParam1' value="where  CUSTOMERID IN(SELECT CUSTOMERID FROM FS_FR_CUSTOMERMASTER WHERE CUSTOMERTYPE='Corporate' AND CUSTOMERID LIKE"/>
				<%if("ACTIVE_FLAG".equalsIgnoreCase(colId[i]))
				{%>
					<input type='text' name="<%=colId[i]%>" value="<%=request.getParameter(colId[i])!=null?request.getParameter(colId[i]):""%>"  onkeypress='Javascript:clckFldSearch();' class='text' size='<%=colWidth[i]!=null?colWidth[i]:""%>'>
				<%}
				else
				{%>
				<input type='text' name="<%=colId[i]%>" value="<%=request.getParameter(colId[i])!=null?request.getParameter(colId[i]):""%>"  onkeypress='Javascript:clckFldSearch();' class='text' size='<%=colWidth[i]!=null?colWidth[i]:""%>'>
				<%}%>
				</td>
<%
			}
%>
			</tr>
			<tr class='formdata'>
<%
			for(int k=0;k<colsListSize;k++)
			{
%>
				<td  class='formheader' nowrap>
<%			if(!"SURCHARGE".equals(operation)){   //Modified  By Kishore Podili For SurCharges LOV 
				if(sotrtOfString[k]!=null && "ASC".equals(sotrtOfString[k]))
				{
%>
					<img src="images/asc.gif"><a href="Javascript:sortLov('<%=colId[k]%>','ASC')">
<%
				}
				else if(sotrtOfString[k]!=null && "DESC".equals(sotrtOfString[k]))
				{
%>
					<img src="images/desc.gif"><a href="Javascript:sortLov('<%=colId[k]%>','DESC')">
<%
				}
				else
				{
%>
					<a href="Javascript:sortLov('<%=colId[k]%>','ASC')">
<%
				} %>
		<!-- Modified  By Kishore Podili For SurCharges LOV -->

				<b><%=colDesc[k]%></b></a></td>
		<%		}else{  %>
				
				<font color='BLUE'>	<b><%=colDesc[k]%>	</b></a> </font></td>
				
<%		} 
	 //Modified  By Kishore Podili For SurCharges LOV 
	       
			}
%>
			</tr>
<%
				//list1.
			if(dataList!=null && dataList.size()>0)
			{
				int dataListSize	=	dataList.size();
			
				int count	=	1;
				for(int j=0;j<dataListSize;j++)
				{
					list1	=	(ArrayList)dataList.get(j);
					int list1Size	=	list1.size();
					
%>
		<tr class='formdata'>
<%
						String [] str	=	null;
                    String		text	=	null;
					for(int l=0;l<list1Size;l++)
					{
						
						if("CREATED_DATE".equalsIgnoreCase(colId[l]))
						{
							localTime	=	Timestamp.valueOf((String)list1.get(l));
							localTime	=	new Timestamp(localTime.getTime() + relativeOffset);
							str			=   eSupplyDateUtility.getDisplayStringArray(localTime);
						}
						//@@Added by Kameswari for the WPBN issue-26514
						else if("QUOTE_STATUS".equalsIgnoreCase(colId[l]))
						{
							
						  text=(String)list1.get(l);
							
							 if("QUE".equalsIgnoreCase(text))
							 {
							 text="Queued";
							 }
                     		 else if("GEN".equalsIgnoreCase(text))
							{
							  text="Generated";
							}
							 else if("ACC".equalsIgnoreCase(text))
							{
							  text="Positive";
							}
							 else if("NAC".equalsIgnoreCase(text))
							{
							  text="Negative";
							}
							else if("PEN".equalsIgnoreCase(text))
							{
							  text="Pending";
							}
							else if("APP".equalsIgnoreCase(text))
							{
							  text="Approved";
							}
							 else if("REJ".equalsIgnoreCase(text))
							{
							  text="Rejected";
							}
						}
						else if("ACTIVE_FLAG".equalsIgnoreCase(colId[l]))
						{
						   text=(String)list1.get(l);
						   if("A".equalsIgnoreCase(text))
							{
							   text="Active";
							}
						   else if("I".equalsIgnoreCase(text))
							{
							   text="Inactive";
							}
						}
						//@@WPBN issue-26514
						else
						{
						   text=(String)list1.get(l);
						}
						if(l==0)
						{
%>
							<td nowrap> <a href='javascript:clckLink("<%=count%>");'><%=text!=null?text:""%></a></td>
<%
						}
            
						
						else
						{
%>
							<td nowrap ><%="CREATED_DATE".equalsIgnoreCase(colId[l])?str[0]+" "+str[1]:text!=null?text:""%></td>
<%
						}
%>
						<input type=hidden name="<%=colId[l]+count%>" value="<%=text!=null?text:""%>">
<%
					}
%>
		</tr>
<%
				count++;
				}
%>
			
<%
		}
		else
		{
%>
			<tr class='formdata'>
				<td colspan="<%=colCount%>" align="center">No Records are found for this combination.</td>
			</tr>
<%
		}
	}
%>

    </tr>
   </table>
   <table width="100%"  bgcolor='#FFFFFF'>
   <tr>
	<td>
<%
			long modRecords = 0;
			long divRecords = 0;
			long noPages=0;
			long startPage=0;
			long lastPage=0;
			long firstPage=0;
			long totRecords=0;
			int  recPerPage=0;
			long currentPage=0;
			//StringBuffer sb=new StringBuffer();
			//StringFunctions sf=new StringFunctions();
				
			
			
				if(currentPage1!=null && currentPage1.length()>0)
					currentPage	=	Long.parseLong(currentPage1);
				totRecords = recCount;
				recPerPage = noOfRecPerPage;
				if(totRecords>0 && currentPage>0 && recPerPage>0)
				{						
					divRecords = (totRecords / recPerPage);
					modRecords = (totRecords % recPerPage);				
					if(modRecords == 0)
					{
						noPages = divRecords;
					}
					else
					{
						noPages = divRecords + 1;
					}
					if(currentPage>=1)
					{
						startPage=1;					
					}
					//THIS IS FOR PREVIOUS
					if(currentPage==1)
					{
%>
						<font size='2' color='black'><b> &lt;&lt; </b></font>
<%
					}
					else
					{
%>
						<a href="Javascript:pgSelect('<%=currentPage-1%>')"><font size='2' color='blue'><b> &lt;&lt; </b></font></a>
<%
					}
								
					if(noPages<=10)
					{
						lastPage=noPages;
					}
					else if(noPages>10 && currentPage>=9)
					{
						if((currentPage+9)<noPages)
						{
							lastPage=currentPage+9;
						}
						else
						{
							lastPage=noPages;
						}
						startPage=currentPage-8;
						}
					else if(noPages>10 && currentPage<9)
					{
						if((currentPage+9)<noPages)
						{
							lastPage=currentPage+9;
						}
						else
						{
							lastPage=noPages;
						}
						startPage=1;
					}
					for(long i=startPage;i<=lastPage;i++)
					{
									
						if(currentPage==i)
						{
%>
							<font size='3' color='blue'><b><%=i%></b></font>
<%
						}
						else
						{
%>
							<a href="Javascript:pgSelect('<%=i%>')"><font size='2' color='blue'><b><%=i%></b></font></a>
<%
						}								
					}
					//THIS IS FOR NEXT
					if(currentPage==noPages)
					{
%>
						<font size='2' color='blue'><b> &gt;&gt;</b></font>
<%
					}
					else
					{
%>
						<a href="Javascript:pgSelect('<%=currentPage+1%>')";><font size='2' color='blue'><b> &gt;&gt;</b></font></a>
<%
					}		
					//THIS Previous 10 Pages and Next 10 Pages Logic
					if(noPages>10)
					{
%>
						<font size='2' color='black'><b> &gt;&gt;</b></font>
<%
					}
					//else
					//{
%>
						<!-- <a href="Javascript:pgSelect('<%//=currentPage+1%>')"><font size='2' color='blue'><b> &gt;&gt;</b></font></a> -->
<%
					//}
					//TOTAL RECORDS
%>
<!-- @@ Added by subrahmanyam for the WPBN ISSUE:146436 ON 26/12/2008 -->
				<% if(!lovid.equalsIgnoreCase("CHARGEBASIS_MASTER"))
					{%><!--@@ Ended by subrahmanyam for the WPBN ISSUE: 146436 ON 26/12/2008  -->
					<br><font size='2' color='blue'><b>Pages&nbsp;:<%=currentPage%>&nbsp;of&nbsp;<%=noPages%></b></font>	
					&nbsp;<font size='2' color='blue'><b>Total&nbsp;Records&nbsp;:<%=totRecords%></b></font>
					<br><font size='2' color='blue'><b>Note: 1. Please Enter the search criteria in the text boxes and Press Enter for searching the data.</b><br><b>&nbsp;&nbsp;&nbsp;&nbsp;2. Please Use the date format DD-MON-YY for searching Date Fields.</b></font>
<%
					}//@@ Added by subrahmanyam for the WPBN ISSUE: 146436 ON 26/12/2008
				}

               
		
%>
	
	</td>
   </table>
    <input type="hidden" name="search" value="<%=search!=null?search:""%>">
	<input type="hidden" name="where" value="<%=where!=null?where:""%>">
	<input type="hidden" name="lovWhere" value="<%=where!=null?where:""%>">
    <input type="hidden" name="pg" value="<%=currentPage1!=null?currentPage1:""%>">
    <input type="hidden" name="lovid" value="<%=lovid!=null?lovid:""%>">
    <input type="hidden" name="tabArray" value="<%=tabCols!=null?tabCols:""%>">
	<input type="hidden" name="formArray" value="<%=formCols!=null?formCols:""%>">
	<input type="hidden" name="sortType" value="<%=sortType!=null?sortType:""%>">
	<input type="hidden" name="sortField" value="<%=sortField!=null?sortField:""%>">
	<input type="hidden" name="operation" value="<%=operation!=null?operation:""%>">
	<input type="hidden" name="designationID" value="<%=designationID!=null?designationID:""%>">
	<input type="hidden" name="shipmentMode" value="<%=shipmentMode!=null?shipmentMode:""%>">
	<input type="hidden" name="Operation" value="<%=Operation!=null?Operation:""%>">
	<input type="hidden" name="localTerminal" value="<%=localTerminal!=null?localTerminal:""%>">
	<input type="hidden" name="localAcceslevel" value="<%=localAcceslevel!=null?localAcceslevel:""%>">
	<input type="hidden" name="multiQuote" value="<%=multiQuote!=null?multiQuote:""%>">	  <!-- Added by Rakesh -->
	<input type="hidden" name="temp" value="">
	
 </form>
 <%
 		
		sb=null;
		lovid=null;
		currentPage1=null;
		tabCols=null;
		formCols=null;
		long end=System.currentTimeMillis();
  %>
 </body>
 </html>
<%
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in the jsp  :"+e.toString());
    logger.error(FILE_NAME+"Error in the jsp  :"+e.toString());
	}
	
%>
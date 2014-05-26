
<%
/*	Programme Name : QMSContentDescriptionView.jsp.
    Module Name		:QMSSetup
	Task			:Content Description Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:Sep 21,2005
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>

<%@ page import = "
				   com.qms.setup.java.QMSContentDOB,
				   java.util.ArrayList,
				   java.util.StringTokenizer,
				   javax.naming.InitialContext,
				   com.foursoft.esupply.common.util.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!  public static final String FILE_NAME="QMSContentDescriptionViewAll.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
	
	ArrayList                 descList				=  null;
	ArrayList                 keyValueList	=  null;
	String                    chargeId			    =  request.getParameter("chargeId");
	String                    operation			    =  request.getParameter("Operation");
	String                    descId  			    =  request.getParameter("descId");
	String                    submitLabel		    =  null;
	String                    nextNavigation        =  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                  checkInvalidate		    =  "";   
	String                  code					=  "";
	QMSContentDOB			dob						=  null;
	ArrayList				list					=  null;
	String					airChecked				=	"";
	String					seaChecked				=	"";
	String					truckChecked			=	"";
	String					airDisabled				=	"";
	String					seaDisabled				=	"";
	String					truckDisabled			=	"";
	String					str						=	"";
	int						noOfcols				=	0;
try{
	//System.out.println("operation  "+operation);
	str				=	request.getParameter("str");
	noOfcols		=	Integer.parseInt(request.getParameter("noOfcols"));
	if(!("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)))	{
			dob                 =  (QMSContentDOB)session.getAttribute("content");
			session.removeAttribute("content");
    }
	else
	{
		list  =  (ArrayList)request.getAttribute("content"); 
		session.setAttribute("content",list);
	}
    //System.out.println("dob.getDefaultFlag()  "+dob.getDefaultFlag());
	if("Invalidate".equals(operation))
	{
		submitLabel="Submit";
		nextNavigation      = "QMSSetupController";		
	}
	else if("ViewAll".equalsIgnoreCase(operation))
	{
		submitLabel = "Continue";		
		if("ViewAll".equalsIgnoreCase(operation))
   		   nextNavigation      = "QMSSetupController?Operation=ViewAll";	
		else 
			nextNavigation      = "QMSContentEnterId.jsp";
	}
	
%>

<html>
<head>
<title>Charge Description</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function checkValues()
{
            var shipMode ;
			var shipModeStr ;
			if(document.forms[0].airChecked.checked )
			{
				if(document.forms[0].seaChecked.checked)
				{
					if(document.forms[0].truckChecked.checked)
					{
						shipMode	=	7;
						shipModeStr	=	"1,2,3,4,5,6,7";
					}else
					{
						shipMode	=	3;
						shipModeStr	=	"1,2,3,5,6,7";
					}
				}else if(document.forms[0].truckChecked.checked)
				{
					shipMode	=	5;
					shipModeStr	=	"1,3,4,5,6,7";
				}else
				{
					shipMode	=	1;
					shipModeStr	=	"1,3,5,7";
				}
			}else if(document.forms[0].seaChecked.checked)
			{
				if(document.forms[0].truckChecked.checked)
				{
					shipMode	=	6;
					shipModeStr	=	"2,3,4,5,6,7";
				}else
				{
					shipMode	=	2;
					shipModeStr	=	"2,3,6,7";
				}
			}else if(document.forms[0].truckChecked.checked)
			{
				shipMode	=	4;
				shipModeStr	=	"4,5,6,7";
			}
			
			  document.forms[0].shipmentMode.value =shipMode;
			
   
}
</script>
<body >
<%
	if("ViewAll".equalsIgnoreCase(operation))
	{
	
%>
     <form method="post"  >
 <% }else{%>
	 <form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>'   >
 <% } %>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
       
          <tr class="formlabel">
            <td colspan="4" width="789" height="21"><font size='3'><b>Content Master -<%=operation%></b></font></td>
			<td align="right">QS1020423</td>
          </tr>
		  </table>
		  <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
          <tr class="formheader">

<%
		StringTokenizer	str1 = new StringTokenizer(str,",");
	 	String arrCountry[] = new String[5];
	 	int l=0;
		while(str1.hasMoreTokens())
		{
			arrCountry[l++] =(String)str1.nextToken();
		}  
		if("SHIPMENTMODE".equalsIgnoreCase(arrCountry[0]))
		{
%>
            <td>Shipment Mode</td>
<%		
		}
		if("HEADERFOOTER".equalsIgnoreCase(arrCountry[1]))
		{
%>
            <td >Header/ Footer</td>
<%
		}
		if("CONTENTID".equalsIgnoreCase(arrCountry[1]) 
			|| "CONTENTID".equalsIgnoreCase(arrCountry[2]))
		{
%>
            <td >Content Description Id &nbsp;</td>
<%
		}
		if("DESCRIPTION".equalsIgnoreCase(arrCountry[1]) 
			|| "DESCRIPTION".equalsIgnoreCase(arrCountry[2]) 
			|| "DESCRIPTION".equalsIgnoreCase(arrCountry[3]))
		{
%>
			<td >Content Description</td>	
<%
		}
		if("FLAG".equalsIgnoreCase(arrCountry[1]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[2]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[3]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[4]))
		{
%>
			<td >Default</td>	
<%
		}
%>
			<%if("Invalidate".equals(operation)){%>
			<td>Invalidate</td>
			<%}%>
		
<%
		
		int listValue	=	list.size();
		if(listValue > 0)
		{
			 for(int i=0;i<listValue;i++){
				dob  =  (QMSContentDOB) list.get(i);
				int    shipMode	=	dob.getShipmentMode();
				airChecked				=	"";
				seaChecked				=	"";
				truckChecked			=	"";
				airDisabled				=    "disabled";
				seaDisabled				=    "disabled";
				truckDisabled			=    "disabled";
					if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
					{
						airChecked	=	"Checked";
												
					}
					if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
					{//System.out.println("shipMode 567            ");
						seaChecked	=	"Checked";

					}
					if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
					{
						truckChecked=	"Checked";
						
					}
			  %>
			<tr class="formdata">
	<%
		if("SHIPMENTMODE".equalsIgnoreCase(arrCountry[0]))
		{
	%>
		    <td nowrap><input type='checkbox' name='airChecked' <%=airChecked%> <%=airDisabled%>>Air
			<input type='checkbox' name='seaChecked' <%=seaChecked%> <%=seaDisabled%>>Sea
			<input type='checkbox' name='truckChecked' <%=truckChecked%> <%=truckDisabled%>>Truck</td>
	<%
		}
		if("HEADERFOOTER".equalsIgnoreCase(arrCountry[1]))
		{
	%>
			<td ><%=(dob.getHeaderFooter()!=null&&"H".equalsIgnoreCase(dob.getHeaderFooter())?"Header":"Footer")%></td>
	<%
		}
		if("CONTENTID".equalsIgnoreCase(arrCountry[1]) 
			|| "CONTENTID".equalsIgnoreCase(arrCountry[2]))
		{
	%>
			<td ><%=dob.getContentId()%></td>
	<%
		}
		if("DESCRIPTION".equalsIgnoreCase(arrCountry[1]) 
			|| "DESCRIPTION".equalsIgnoreCase(arrCountry[2]) 
			|| "DESCRIPTION".equalsIgnoreCase(arrCountry[3]))
		{
	%>
			<td ><%=dob.getContentDescription()%></td>
	<%
		}
		if("FLAG".equalsIgnoreCase(arrCountry[1]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[2]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[3]) 
			|| "FLAG".equalsIgnoreCase(arrCountry[4]))
		{
	%>
			<td ><%=(dob.getDefaultFlag()!=null && "T".equalsIgnoreCase(dob.getDefaultFlag()))?"Yes":"No"%></td>
	<%
		}
	%>
<%			if("Invalidate".equalsIgnoreCase(operation))
			{
%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(dob.getInvalidate())?"checked":""%>></td>			
<%			}
%>
<%		}
	}
	else
	{
%>
		<tr bgcolor="#FFFFFF">
		<td colspan='6' align='center'>
			<font face="Verdana" size="2" color='red'>
			<b>No Records Found.</b>
			</font>
		</td>
		</tr>
<%
	}
%>
       
      </table>
	  
      <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
        
          <tr class='text'>
            <td colspan="2" ><font color="#FF0000">*</font>Denotes Mandatory</td>
<%
		if("ViewAll".equalsIgnoreCase(operation))
		{
%>
            <td align="right" colspan="2" ><input type='button' class='input' name='<%=submitLabel%>'  value='<%=submitLabel%>' onclick="history.back()" >
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="shipmentMode" value='<%//=dob.getShipmentMode()%>'></td>
<%
		}
		else
		{
		
%>
			 <td align="right" colspan="2" >
<%
			if(listValue>0)
			{
%>
				<input type='Submit' class='input' name='<%=submitLabel%>'  value='<%=submitLabel%>' "border-style: solid">
<%
			}
			else
			{
%>
				<input type='button' class='input' name='<%=submitLabel%>'  value='Continue' onclick="history.back()">
<%
			}
%>
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="shipmentMode" value='<%//="Invalidate".equalsIgnoreCase(operation)?"":""+dob.getShipmentMode()%>'>
			<input name="subOperation" type="hidden" value="submit" class='input'>
            </td>
<%
		}
%>
          </tr>
        
      </table>
	  

</body>

</html>
<%}
  catch(Exception e)
  {e.printStackTrace();
	  Logger.error(FILE_NAME,"Error in QMSContentDescriptionViewAll.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","etrans/ETAViewAllAdmin.jsp?View=ContentMaster");
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
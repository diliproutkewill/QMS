
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
				   javax.naming.InitialContext,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue"%>

<%!
  private static Logger logger = null;
public static final String FILE_NAME="QMSContentDescriptionView.jsp";%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ArrayList                 descList				=  null;
	ArrayList                 keyValueList	=  null;
	String                    chargeId			    =  request.getParameter("chargeId");
	String                    operation			    =  request.getParameter("Operation");
	String                    descId  			    =  request.getParameter("descId");
	String                    submitLabel		    =  null;
	String                    nextNavigation        =  null;	
	ErrorMessage			  errorMessageObject	=  null; 
    String                  checkInvalidate		    =  "";   
	String                    code					=  "";
	QMSContentDOB          dob                   =  null;
	ArrayList              list                  =  null;
try{
	//System.out.println("operation  "+operation);
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
	if("Modify".equals(operation) || "Delete".equals(operation) || "Invalidate".equals(operation))
	{
		submitLabel="Submit";
		nextNavigation      = "QMSSetupController";		
	}
	else if("View".equals(operation)||"ViewAll".equalsIgnoreCase(operation))
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
function check_Length(maxLength,input)
{
	val = input.value;
	if(val.length > maxLength)
	{
		alert("Character Limit reached("+maxLength+")");
		check_textArea(maxLength,input);
	}
}
function check_textArea(maxLength,input) 
{ 
val = input.value;
val = val.substring(0,maxLength);

input.value = val;

}
</script>
<body >
<%
	if("ViewAll".equalsIgnoreCase(operation))
	{%>
     <form method="post"  >
 <% }else{%>
	 <form method="post"  onSubmit="return checkValues()" action='<%=nextNavigation%>'   >
 <% } %>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
       
          <tr class="formlabel">
            <td colspan="4" width="789" height="21"><font size='3'><b>Content Master -<%=operation%></b></font></td>
			<td align="right">QS1020422</td>
          </tr>
		  </table>
		  <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
          <tr class="formheader">
            <td nowrap>Shipment Mode:</td>
            <td>Header/ Footer:</td>
            <td>Content Description Id:</td>
			<td>Content Description:</td>			
			<td>Default:</td>		
			<%if("Invalidate".equals(operation)){%>
			<td>Invalidate</td>
			<%}%>
		 </tr>
		  <%    String airChecked  ="";
				String seaChecked  ="";
				String truckChecked  ="";
				String airDisabled  ="" ;
				String seaDisabled  ="" ;
				String truckDisabled  ="" ;
				if(!("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)))	{
				
			    int    shipMode	=	dob.getShipmentMode();
					if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
					{
						airChecked	=	"Checked";
						airDisabled =    "disabled";
					}
					if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
					{
						seaChecked	=	"Checked";
						seaDisabled =    "disabled";
					}
					if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
					{
						truckChecked=	"Checked";
						truckDisabled =    "disabled";
					}
			%>

		  <tr class="formdata">
		    <td nowrap><input type='checkbox' name='airChecked' <%=airChecked%> <%=airDisabled%>>Air&nbsp;<input type='checkbox' name='seaChecked' <%=seaChecked%> <%=seaDisabled%>>Sea&nbsp;<input type='checkbox' name='truckChecked' <%=truckChecked%> <%=truckDisabled%>>Truck</td>
            <td><%if("Modify".equalsIgnoreCase(operation)){%><select name='headerFooter'value=''>
			<%if("H".equalsIgnoreCase(dob.getHeaderFooter())){%>
				<option value='H'>Header</option>
				<option value='F'>Footer</option>
			<%}else{%>
				<option value='F'>Footer</option>
			    <option value='H'>Header</option>
			<%}%>
			</select>
			<%}else{%>
			<%=("H".equalsIgnoreCase(dob.getHeaderFooter()))?"Header":"Footer"%>
			<%}%>
			</td>
            <td><input type='hidden' name='descId'value='<%=descId%>'><%=descId%></td>
			<%if("Modify".equals(operation)){
				%>
            <td>
			         <!--<textArea name='contentDesc' rows='4' cols='20' maxLength='200' onblur="check_Length(500,this)" value='<%=dob.getContentDescription()%>' ><%=dob.getContentDescription()%></textArea>-->
					  <textArea name='contentDesc' rows='4' cols='20' maxLength='200' onblur="check_Length(500,this)" ><%=dob.getContentDescription()%></textArea>
			</td>
			<td><select name='default' value=''>
			<%if("T".equalsIgnoreCase(dob.getDefaultFlag())){%>
				<option value='T'>Yes</option>
				<option value='F'>No</option>
			<%}else{%>
				<option value='F'>No</option>
			    <option value='T'>Yes</option>
			<%}%>
			</td>
			<%}else{%>
				<td>
				<!--<textArea name='contentDesc' rows='4' cols='20' maxLength='200' onblur="check_Length(500,this)" value='<%=dob.getContentDescription()%>' readOnly ><%=dob.getContentDescription()%></textArea>-->
				<textArea name='contentDesc' rows='4' cols='20' maxLength='200' onblur="check_Length(500,this)" readOnly ><%=dob.getContentDescription()%></textArea></td>
				<%if("T".equalsIgnoreCase(dob.getDefaultFlag())){%>
				<td><input type='hidden' name='default' readonly>Yes</td>
				<%}else{%>
				<td><input type='hidden' name='default' readonly>No</td>
				<%}%>
			<%}%>
			
          </tr>
		  <%}else { for(int i=0;i<list.size();i++){
				dob  =  (QMSContentDOB) list.get(i);
				int    shipMode	=	dob.getShipmentMode();
				airChecked = "";
				seaChecked = "";
				truckChecked = "";
				airDisabled =    "disabled";
				seaDisabled =    "disabled";
				truckDisabled =    "disabled";
				//System.out.println("shipMode             "+shipMode);
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
		    <td nowrap><input type='checkbox' name='airChecked' <%=airChecked%> <%=airDisabled%>>Air
			<input type='checkbox' name='seaChecked' <%=seaChecked%> <%=seaDisabled%>>Sea
			<input type='checkbox' name='truckChecked' <%=truckChecked%> <%=truckDisabled%>>Truck</td>
			<td><%=(dob.getHeaderFooter()!=null&&"H".equalsIgnoreCase(dob.getHeaderFooter())?"Header":"Footer")%></td>
			<td><%=dob.getContentId()%></td>
			<td><%=dob.getContentDescription()%></td>
			<td><%=(dob.getDefaultFlag()!=null && "T".equalsIgnoreCase(dob.getDefaultFlag()))?"Yes":"No"%></td>
			<%if("Invalidate".equalsIgnoreCase(operation)){%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(dob.getInvalidate())?"checked":""%>></td>			
			<%}%>
		  <%}
		  }%>
        
      </table>
	  
      <table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
        
          <tr class='text'>
            <td colspan="2" width="452"><font color="#FF0000">*</font>Denotes Mandatory</td>
<%
		if("ViewAll".equalsIgnoreCase(operation))
		{
%>
            <td align="right" colspan="2"><input type='button' class='input' name='<%=submitLabel%>'  value='<%=submitLabel%>' onclick="history.back()" >
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="shipmentMode" value='<%=dob.getShipmentMode()%>'></td>
<%
		}
		else
		{
			
		
%>
			 <td align="right" colspan="2"><input type='Submit' class='input' name='<%=submitLabel%>'  value='<%=submitLabel%>' "border-style: solid">
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="shipmentMode" value='<%=dob.getShipmentMode()%>'>
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
	  //Logger.error(FILE_NAME,"Error in QMSContentDescriptionView.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSContentDescriptionView.jsp file"+e.toString());
	  errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSContentDescriptionView.jsp");
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
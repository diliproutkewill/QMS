<%--
/**
	Program Name	: RevenueSharingReports.jsp
	Module Name	: ETrans
	Task		: RevenueSharing
	Sub Task	: Reports
	Author Name	: Rizwan.
	Date Started	: December 4,2001
	Date Completed	: December 5,2001
	Date Modified	: 
*/
--%>
<% // @@ Murali Modified On 20050429 Regarding SPETI - 5700 %>

<%@ page import = "java.sql.Timestamp,
				java.util.Date,
				java.util.StringTokenizer,
				java.text.SimpleDateFormat,
				org.apache.log4j.Logger,
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.util.ESupplyDateUtility,
				com.foursoft.etrans.common.util.java.FormatDate
				" %>
<% // @@ Murali Modified On 20050429 Regarding SPETI - 5700 %>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETARevenueSharingReports.jsp ";
%>

<%        
      logger  = Logger.getLogger(FILE_NAME);
		  String headingSize	= null;
		  String terminalId	= null;
		  String userId		= null;
		  String Revenue[][]	= null;
		  String str		= "";
	          String heading	= null;
		  Date	 d		= new Date();
		  String operation	= request.getParameter("Operation");  
		  // @@ Murali Modified On 20050429 Regarding SPETI - 5700
		  // SimpleDateFormat dt   = new SimpleDateFormat("dd-MM-yy");
		  // String date		= dt.format(d);
		  ESupplyDateUtility eSupplyDateUtility  =  new ESupplyDateUtility();
		  String dateFormat =	loginbean.getUserPreferences().getDateFormat();
		  String date	=  eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		  // @@ Murali Modified On 20050429 Regarding SPETI - 5700
		  int	    noOfcols	= 2;
		  int	    tdwidth	= 0;
		  int       len; 
	          
		  terminalId        = loginbean.getTerminalId();
		  userId            = loginbean.getUserId();    
%>
<%      
       if(operation.equals("RevenuSharing"))
       {
                  str+= "CTYPE1,CTYPE2,";

	    if     (request.getParameter("code")!=null&& request.getParameter("code").equals("0"))
				{
				str+="CODE,";
				noOfcols++;
				}
			
			if	(request.getParameter("chargeId")!=null)
				{
				str+="CHARGEID,";
				noOfcols++;
				}
			if (request.getParameter("sharingValue")!=null)
				{
				str+="SHARINGVALUE,";
				noOfcols++;
				}	
			if(request.getParameter("indicator")!=null && request.getParameter("indicator").equals("0"))
				{
				str+="INDICATOR,";
				noOfcols++;
				}




	   	try
		{
		// InitialContext			initial		= new InitialContext();
	      //   SetUpSessionHome	reportsHome	= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		     SetUpSessionHome	reportsHome	= (SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		 SetUpSession		reportRemote	= (SetUpSession)reportsHome.create();
		 Revenue=reportRemote.getViewRevenueSharingReport(str,noOfcols);
		//Revenue=reportRemote.getViewRevenueSharingReport();
		 len = Revenue.length;
		 if(len>0)
			{

%>		
<html>
<head>
<title>Profit Sharing</title>       <%@ include file="../../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
</head>
<body>
<form method="post" action="">
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
		<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr valign="top" class='formlabel'> 
		<td  width="100%" align="center">
		<table width="100%" border="0" ><tr class='formlabel'><td>Profit  Sharing - View All
		</td><td align=right><%=loginbean.generateUniqueId("ETARevenueSharingReports.jsp","RevenueSharing")%></td></tr></table></td>
		</tr>
		</table>

		<table width="100%" border="0" cellspacing="1" cellpadding="4">
		<tr valign="top" class='formdata'> 
            <td colspan="3" >&nbsp;</td>
          </tr>
		<tr class='formdata' valign="top"> 
		<td ><b>Terminal : </b> <%=terminalId%></td>
		<td align="center" ><b>User :</b><%=userId%></td>
		<td align="right" ><b>Date :</b> 
		<%=date%>
		</td>
		</tr>
		<tr valign="top"  class='formdata'> 
		<td width="100%" colspan=3>
		<b>A:</b>Absolute&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<b>P:</b>Percentage
		</td>
		</tr>
		</table>

<%
		StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +150;	 
					 st1.nextToken();	
		        }    
%>
	<table width=100%  border="0" cellspacing="1" cellpadding="4" >
          <tr valign="top" class='formheader'> 
  <%
	StringTokenizer st = new StringTokenizer(str,",");
	  while(st.hasMoreTokens())
	  {
		  heading = st.nextToken();
         
           if(heading.equals("CTYPE1"))
		 	 heading = "Company 1";
		   if(heading.equals("CTYPE2"))
			 heading = "Company 2";	
		   if(heading.equals("CODE"))
		  	 heading = "Code";
		  
		   if(heading.equals("INDICATOR"))
			 heading = "Indicator";
		   if(heading.equals("CHARGEID"))
			  heading = "Charge Id";	
		  if(heading.equals("SHARINGVALUE"))
			 heading = "Sharing Value";	  
		
%>
	
           <td width =20% colspan="1"><%=heading%></td>
<%			
     }
 %>	
	        </tr>
	
    
        <%     
		  for(int i=0;i<len;i++)
		    {
%> 
              <tr valign="top" class='formdata'>             
<%					
			    for( int j=0;j<noOfcols;j++)
			 	 {
					  if(Revenue[i][j]==null)
					   {		
%>
					  <td width=20% >Not Available</td>
<%   
           		        }  
						else
						{   
%>
					<td width=20% ><%=Revenue[i][j]%></td>
<%				
						 }		   
 			 		  }
%>					   
 </tr>
<%
  }
%>
</table>
						<!-- Modified By G.Srinivas to resolve the QA-Issue -->
						<table><tr align="right" border="0">
						<td align="">&nbsp;</b>
						<td width="100%"   align="right" bgcolor="white">
						<input type="button" value="Continue" name="close" onClick="javascript:history.go(-1);" class='input'>
						</td>
		</tr>
</table>
</table>
 </form>
<br>
<%
}
  else
   {
					
%>		
<form method="POST">
	<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="760" cellpadding="4" cellspacing="1" >
          <tr valign="top" class='formlabel'><h4>&nbsp;&nbsp;&nbsp;Record  Not  Found <h4> 
            <td colspan="2" >
              </td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td colspan="2"> 
              <textarea class='select' rows="8" name="Message" cols="80" readonly class='select'>No  Relevant data is found.</textarea>
            </td>
          </tr>
          <tr valign="top"> 
            <td width="50%"  align="right"> 
			   <input type="button" value="OK" name="Back" onClick="javascript:history.back()" class='input'>
             
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  </form>
    </body>
</html>

<%  
   } 

  } // try
   catch(Exception e)
	{
     	//Logger.error(FILE_NAME,"problem in execution : ",e.toString());
      logger.error(FILE_NAME+"problem in execution : "+e.toString());
	}
 } // if main
 
%>
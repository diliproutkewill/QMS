<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<% // @@ Murali Modified on 20050427 Regarding Date Format %>

<%@ page import =  " java.sql.Timestamp,
					 java.util.StringTokenizer,
					 org.apache.log4j.Logger,
					 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					 com.foursoft.etrans.common.util.java.FormatDate,
					 com.foursoft.esupply.common.util.ESupplyDateUtility						  
				"%>
<% // @@ Murali Modified on 20050427 Regarding Date Format %>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETATerminalMasterRepors.jsp ";
%>

<%         
      logger  = Logger.getLogger(FILE_NAME);
		   //Logger.info(FILE_NAME,"Hello");
       logger.info(FILE_NAME+"Hello");
		   String heading               = null;
		   int headingSize              = 0,len=0;
           String    terminalId         = null;
		   String    userId             = null;
           terminalId        = loginbean.getTerminalId();
	       userId            = loginbean.getUserId();
		   String operation=request.getParameter("Operation");
		   String terminals[][] = null;
	       int row =0,col=0;   
	       // @@ Murali Modified on 20050427 Regarding Date Format
		   /*
	       SimpleDateFormat df= new SimpleDateFormat("dd-MM-yy");
		   Date d = new Date();
		   String date=df.format(d);
		   */
		   ESupplyDateUtility eSupplyDateUtility = new ESupplyDateUtility();	
		   String date = eSupplyDateUtility.getCurrentDateString(loginbean.getUserPreferences().getDateFormat());
		   // @@ Murali Modified on 20050427 Regarding Date Format
		   String str="";
	       int count=0;
			if (request.getParameter("TERMINALID")==null )//&& request.getParameter("TERMINALID").equals("0"))
				{
				str+="TERMINALID,";
				count++;
				}
			if	(request.getParameter("CITY")!=null && request.getParameter("CITY").equals("0")){
				str+="CITY,";
				count++;
				}
			if	(request.getParameter("CONTACTNAME")!=null && request.getParameter("CONTACTNAME").equals("0")){
				str+="CONTACTNAME,";
				count++;
				}
			if	(request.getParameter("DESIGNATION")!=null && request.getParameter("DESIGNATION").equals("0")){
				str+="DESIGNATION,";
				count++;
				}		  
        
         if(operation.equals("Terminal") && (!str.equals("")))
         { 
  	      try
	       {
            
		 //  InitialContext initial = new InitialContext();
		  // SetUpSessionHome reportsHome=(SetUpSessionHome)initial.lookup("SetUpSessionBean");
		   SetUpSessionHome reportsHome=(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		   SetUpSession reportRemote= (SetUpSession)reportsHome.create();
		   terminals=reportRemote.getViewTerminalReports(str,count, terminalId);
		   len = terminals.length;
//Logger.info(FILE_NAME,len);
			if(len>0)
				{	 				
%>		
<html>

<head>
<title>TerminalMasterReports</title>
<script>
 
	</script>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
</head>
<body>
<form method="post" action="../ETAViewAllAdmin.jsp?View=ViewTerminal">
         <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
	 <table width="100%" border="0" cellspacing="1" cellpadding="4">
		     <tr valign="top" class='formlabel'> 
            <td  align="center" ><table width="100%" border="0" ><tr class='formlabel'><td>
             <% // @@ Murali Modified On 20050427 Regarding SPETI- 5548;  %>
             <% // @@ Murali Modified On 20050422 Regarding SPETI- 5548;  %>
             <!--  Terminal View Reports  -->
			  Terminal - View All
			<% // @@ Murali Modified On 20050422 Regarding SPETI-  5548;  %>
			  
            </td><td align=right><%=loginbean.generateUniqueId("ETATerminalMasterReports.jsp","ViewTerminal")%></td></tr></table></td>
          </tr>
		 </table>  
	  
	  
    <table width="100%" border="0" cellspacing="1" cellpadding="4">
          <tr  valign="top" class='formdata'> 
            <td ><b>Terminal : </b><%=terminalId%></td>
            <td align="center"><b>User : </b><%=userId%></td>
            <td align="right"><b>Date :</b>
              <%=date%> </td>
          </tr>
        </table>
      
<%
  			  StringTokenizer st1= new StringTokenizer(str,",");
			  int tableWidth = 0;  
		      while(st1.hasMoreTokens())
		        {
			         tableWidth = tableWidth +190;	 
					 st1.nextToken();	
		        }    
%>
     <table width="100%"  border="0" cellspacing="1" cellpadding="4"  >
   <tr valign="top" class='formheader'> 
<%
 		   StringTokenizer st= new StringTokenizer(str,",");
		   while(st.hasMoreTokens())
		   {
			 heading = st.nextToken();
			 if(heading.equals("TERMINALID"))
				heading="Terminal Id";
 			 if(heading.equals("CITY"))
				heading="City";	
			 if(heading.equals("CONTACTNAME"))
				heading="Contact Name";	
 			 if(heading.equals("DESIGNATION"))
				heading="Designation";	
%>
      			  <td width ="25%" ><%=heading%>
			      </td>
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
			    for( int j=0;j<count;j++)
			 	 {
					  if(terminals[i][j]==null) 
					  {
%>
                          <td width="25%" >Not Available</td>
<%   
           		        }  
						else
						{   
%>
                          <td width="25%"><%=terminals[i][j]%></td>
<%				
						 }		   
 			 		  }
%>					   
                     </tr>
<%
			        }
%>		</table>
        </table>
		<table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
        <tbody>		
          <tr >
            <td align="right" colspan="7"><input class='input' name='Continue' type='Submit' value='Continue' >
            </td>
          </tr>
        </tbody>
      </table>
      </form>
    </body>
</html>
<%
	}
	  else
	  {
%>
      <html>
<head>
<title>ESupplyErrorPage</title>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
<script>
</script>
</head>
<body >
<form method="POST" action="" >
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="760" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formlabel'> 
            <td colspan="2"><img border="0" src="" width="19" height="23" align="left">
              </td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td colspan="2"> 
              <textarea class=text rows="8" class='select' name="Message" cols="80" readonly >No relevant data was found.</textarea>
            </td>
          </tr>
          <tr valign="top"> 
            <td width="50%" align="right"> 
			  <input type="button" class='input' value="   Ok    " name="submit" onclick="javascript:window.close()">
             
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
     }
   catch(Exception e)
	{
	//Logger.error(FILE_NAME,"TerminalMasterReports.jsp",e.toString());
  logger.error(FILE_NAME+"TerminalMasterReports.jsp"+e.toString());
	}
         }
%>
	

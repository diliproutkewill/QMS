<%@page import="javax.naming.Context,
				javax.naming.InitialContext,
				java.util.Vector,
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
   String   originTerm			=	"";
   String   destTerm			=	"";
   Vector   termnlId			= 	new Vector();
   String documentId				= request.getParameter("documentNo");
   if(documentId==null)
	 documentId ="";
   System.out.println("documentId is " + documentId);
   String checkType   				= request.getParameter("checkType");
   if(checkType==null)
	 checkType = "";

	   
		Context				   initial	=	new InitialContext();	 
	  SetUpSessionHome home 	= 	(SetUpSessionHome )loginbean.getEjbHome( "SetUpSessionBean" );
 		SetUpSession	   remote 	= 	(SetUpSession)home.create();
		termnlId						=	remote.getTerminals(documentId);
		
		if(termnlId != null)
		{
			if(termnlId.size() > 0)
			{
				originTerm	= (String)termnlId.elementAt(0);
				destTerm	= (String)termnlId.elementAt(1);
			}		
		}
   if(checkType.equals("multipleViews"))
   {
    		
   		String	houseDocId		= "";
   		String	boundIndex		= request.getParameter("boundIndex");
   		String	shipmentMode	= request.getParameter("shipmentMode");
   		String	terminalId		= request.getParameter("terminalId");
	//	Context					initial		=	new InitialContext();	 
	    SetUpSessionHome	hHome		=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		SetUpSession		hRemote		= 	(SetUpSession)hHome.create(); 
		houseDocId								=	hRemote.getHouseDocId(documentId,shipmentMode,terminalId,boundIndex);
		if(houseDocId!=null)
			documentId = houseDocId;
		else
			documentId = "";	  
		   
   }		   			
   
  
%>
<HTML>
<head>
<script>

function submitParent()
{
	
	opener.document.forms[0].target="reportFrame";	
	opener.document.forms[0].submit();
}
</script>
</head>
<title>Report
</title>
<% // @@ Modified by G.Srinivas on 20050525 for SPETI 1792-1800 %>
<% // @@ Anup replaced on 20050520 for SPETI-6829  %>
<% // @@ Modified by Sailaja 0n 2005 05 09 for SPETI-6505 %>
 <frameset  rows="30px, * " border=0 onLoad="submitParent();"> 
<!-- <frameset  rows="30px, * " border=0> -->
<% // @@ 2005 05 09 for SPETI-6505 %>
<% // @@ on 20050520 for SPETI-6829  %>
	<frame border=0 src='EAReportBar.jsp?originTerm=<%=originTerm%>&destTerm=<%=destTerm%>&documentId=<%=request.getParameter("documentNo")%>' name="barFrame" scrolling=no></frame>
	<frame border=0 src="" 					name="reportFrame" ></frame>
</frameset>

</HTML>

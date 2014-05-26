<%
/*	Program Name		: ETCTerminalRegistrationAddProcess.jsp
	Module name			: O Setup
	Task				: Adding Terminal
	Sub task			: to process the addition of a terminal
	Author Name			: C.L.N Saravana
	Date Started		: Feb 25, 2002
	Date completed		: Feb 27, 2002
	Description	:
		This file is used to store the information of a terminal in a persistent storage and there by the
		page is forwarded to ../ESupplyErrorPage.jsp.
*/
%>
<%@ page import = "	java.util.StringTokenizer,
					javax.naming.InitialContext,
				 	org.apache.log4j.Logger,		
                    com.foursoft.esupply.common.util.ESupplyDateUtility,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome,
					com.foursoft.etrans.common.util.ejb.sls.OIDSession"%>

<jsp:useBean id="TerminalRegObj" class="com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean" scope ="request"/>
<jsp:setProperty name="TerminalRegObj" property="*"/>

<jsp:useBean id="AddressObj" class="com.foursoft.etrans.common.bean.Address" scope="request"/>
<jsp:setProperty name="AddressObj" property="*"/>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;  
	private static final String FILE_NAME	=	"ETCTerminalRegistrationAddProcess.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	try
	{
		if(loginbean.getTerminalId() == null )
		{
%>
			<jsp:forward page="../ESupplyLogin.jsp"/>
<%
		}
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"ETCTerminalRegistrationAddProcess.jsp file 1 : "+e.toString());
    logger.error(FILE_NAME+"ETCTerminalRegistrationAddProcess.jsp file 1 : "+e.toString());
	}
	try
	{
		InitialContext context				= null;
		SetUpSessionHome trsh 	= null;
		SetUpSession trs 		= null;
		
		context = new InitialContext();
		trsh 	= (SetUpSessionHome)context.lookup ("SetUpSessionBean");
		trs  	= (SetUpSession)trsh.create();
		
		String notes    = request.getParameter("notes");
		String contact  = request.getParameter("contactName");
		//@@ Srivegi Added on 20050419 (Invoice-PR)
      	String stockedInvoiceIds  = request.getParameter("stockedInvoiceIds");
		//@@ 20050419 (Invoice-PR)		
		String collectStatus    = request.getParameter("collectShipment");
		String emailStatus  	= request.getParameter("emailStatus");
		String timeZone    		= request.getParameter("timeZone");
		String opTerminalType  	= request.getParameter("opTerminalType");
		String shipmentMode  	= request.getParameter("shipmentMode");
		//Logger.info(FILE_NAME,"shipmentMode  in process : "+shipmentMode);
		String adminType	= request.getParameter("adminType");
		String adminROTerminal	= (request.getParameter("adminROTerminal")!=null)?request.getParameter("adminROTerminal"):"";
		//Logger.info(FILE_NAME,"adminROTerminal:"+adminROTerminal);
		String terminalId	= request.getParameter("terminalId"); 
		String companyId	= request.getParameter("companyId");
		//Logger.info(FILE_NAME,"terminalId in Process "+terminalId);
        //<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
		String frequency  	    = request.getParameter("frequency");
		String carrier  	    = request.getParameter("carrier");
		String transittime  	= request.getParameter("transittime");
		String rateValidity  	= request.getParameter("rateValidity");
        //<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
		TerminalRegObj.setTerminalId(terminalId);
		TerminalRegObj.setCompanyId(companyId);
		TerminalRegObj.setTerminalType(request.getParameter("terminalType"));
		//added by phani sekhar for wpbn 170758 on 20090626
		String marginType = request.getParameter("marginType");
    String discountType= request.getParameter("discountType");
		TerminalRegObj.setMarginType(marginType);
    TerminalRegObj.setDiscountType(discountType);
		//ends 170758
		if(opTerminalType!=null && opTerminalType.equals("A"))
		{
			if(trs.isExists(terminalId))
			{	
				String errorMessage = "Record already exists with TerminalId : "+terminalId;
				session.setAttribute("ErrorCode","RAE");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Add");
				session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation="+request.getParameter("Operation"));
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}							
		}
		//Logger.info(FILE_NAME,"opTerminalType in jsp :"+opTerminalType);

		if(notes == null || notes.trim().length()==0)
			TerminalRegObj.setNotes(" ");
		else 
            TerminalRegObj.setNotes(notes);
		if(contact ==null || contact.trim().length()==0)
			TerminalRegObj.setContactName(" ");
		
		if(collectStatus == null || collectStatus.trim().length()==0)
			collectStatus=" ";
		if(emailStatus ==null || emailStatus.trim().length()==0)
			emailStatus=" ";
		if(timeZone == null || timeZone.trim().length()==0)
				timeZone = " ";
			
		TerminalRegObj.setCollectShipment(collectStatus);
		TerminalRegObj.setEmailStatus(emailStatus);
		TerminalRegObj.setTimeZone(timeZone);
		
		if( opTerminalType.equals("A") &&  (adminType!=null && adminType.equals("H")) )
		{	
			TerminalRegObj.setOperationTerminalType(adminType);	
		}
		else
			TerminalRegObj.setOperationTerminalType(opTerminalType);
		//@@ Srivegi Added on 20050419 (Invoice-PR)
		TerminalRegObj.setStockedInvoiceIdsCheck(stockedInvoiceIds);
		//@@ 20050419 (Invoice-PR)		
		TerminalRegObj.setChildTerminalFlag(adminROTerminal);//Added By I.V.Sekhar
		TerminalRegObj.setShipmentMode(shipmentMode);

        //<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
        TerminalRegObj.setFrequency(frequency);
		TerminalRegObj.setCarrier(carrier);
	    TerminalRegObj.setTransitTime(transittime);
		TerminalRegObj.setRateValidity(rateValidity);
       //<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
    
    TerminalRegObj.setIataCode(request.getParameter("iatacode"));
		TerminalRegObj.setAccountCode(request.getParameter("accountcode"));
		TerminalRegObj.setTaxRegNo(request.getParameter("taxregno"));
		
		com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome oidHome = (com.foursoft.etrans.common.util.ejb.sls.OIDSessionHome )context.lookup("OIDSessionBean");
		com.foursoft.etrans.common.util.ejb.sls.OIDSession oidRemote = (com.foursoft.etrans.common.util.ejb.sls.OIDSession) oidHome.create();
		int addressId = oidRemote.getAddressOID();
		String locationIdHide = request.getParameter("locationIdHide");
		//Logger.info(FILE_NAME,"locationIdHide : "+locationIdHide);
		StringTokenizer st = new StringTokenizer(locationIdHide,",");
		int len =0;
		while(st.hasMoreTokens())
		{
			len++;
			st.nextToken();
		}
		String locationIds[] = new String[len];
		st = new StringTokenizer(locationIdHide,",");
		for(int i=0;st.hasMoreTokens();i++)
		{
			locationIds[i] = st.nextToken();
 		}
		AddressObj.setAddressId(addressId);
		TerminalRegObj.setLocationId(locationIds);
		/* For CBT Terminals*/
		String cbtFlag = request.getParameter("cbtflag");
		if (cbtFlag!=null && cbtFlag.equals("Y"))
		 {
				String cbtLocationIdHide = request.getParameter("cbtLocationIdHide");
				cbtLocationIdHide = cbtLocationIdHide.substring(1,cbtLocationIdHide.length());
				//Logger.info(FILE_NAME,"cbtLocationIdHide : "+cbtLocationIdHide);
				java.util.Vector cbtLocationIds = new java.util.Vector();
    			StringTokenizer cbtst = new StringTokenizer(cbtLocationIdHide,",");
				while(cbtst.hasMoreTokens())
				{
					cbtLocationIds.add(cbtst.nextToken());

				}

				TerminalRegObj.setCBTLocationId(cbtLocationIds);
		 }

		/*End of CBT Terminals*/
		//Logger.info(FILE_NAME,"TerminalRegObj.getLocationId :"+TerminalRegObj.getTerminalId());
		
		TerminalRegObj.setContactAddressId(addressId);
		//setting Server Time Difference 
		ESupplyDateUtility eSupplyDateUtility   = new ESupplyDateUtility();
		String serverTimeDiff =    eSupplyDateUtility.getTimeDifference(TerminalRegObj.getTimeZone());
		TerminalRegObj.setServerTimeDiff(serverTimeDiff);
       
		terminalId = trs.setTerminalRegDetails(TerminalRegObj,AddressObj, loginbean );
		
		if( terminalId!=null )
		{
			String errorMessage = "Record successfully added with TerminalId : "+terminalId;
			session.setAttribute("ErrorCode","RSI");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation="+request.getParameter("Operation"));
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
		else
		{
			String errorMessage = "Record already exists with TerminalId : "+TerminalRegObj.getTerminalId();
			session.setAttribute("Operation", "Add");
			session.setAttribute("ErrorCode","RAE");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation="+request.getParameter("Operation"));
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"TerminalRegistrationAddProcess.jsp file 4 : ", e.toString());
    logger.error(FILE_NAME+"TerminalRegistrationAddProcess.jsp file 4 : "+ e.toString());
		String errorMessage = "Error while adding the record";
		session.setAttribute("Operation", "Add");
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage",errorMessage);
		session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation="+request.getParameter("Operation"));
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>

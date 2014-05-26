<%
/*	Program Name		: ETCTerminalRegistrationProcess.jsp
	Module name			: HO Setup
	Task				: Adding Terminal
	Sub task			: to register the terminal details during modification or deletion
	Author Name			: C.L.N Saravana
	Date Started		: Feb 25, 2002
	Date completed		: Feb 27, 2002
	Description			:
				This file modifies or deletes the terminal according to the 
				request by invoking methods on SetUpSession bean.
*/
%>

<%@ page import	=	"javax.naming.InitialContext,
					java.util.StringTokenizer,
				 	org.apache.log4j.Logger,	
                    com.foursoft.esupply.common.util.ESupplyDateUtility,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id ="terminalReg" class= "com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean" scope ="request"/>
<jsp:setProperty name="terminalReg" property="*"/>

<jsp:useBean id="address" class="com.foursoft.etrans.common.bean.Address" scope="request"/>
<jsp:setProperty name="address" property="*"/>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTerminalRegistrationProcess.jsp ";
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
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in ETCTerminalRegistrationProcess.jsp file 1: ", e.toString());\
    logger.error(FILE_NAME+"Error in ETCTerminalRegistrationProcess.jsp file 1: "+ e.toString());
	    e.printStackTrace();
	}
%>	
<%
	String strParam = null;    // String to store the type of operation requested
    strParam = request.getParameter("Operation");
	session.removeAttribute("terminalId");
	session.removeAttribute("opTerminalType");
	try 
	{
  if(strParam.equalsIgnoreCase("Invalidate"))
		{
			java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
			  String invalidater[]=request.getParameterValues("checkBoxValue");
		   boolean flag=false;

		   InitialContext initial1 = new InitialContext();
		  
		 SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)initial1.lookup("SetUpSessionBean");
		
		SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
      

		   for(int i=0;i<dobList.size();++i)
		 {
			   if(invalidater[i].equalsIgnoreCase("false"))
					invalidater[i]="F";
			   else if(invalidater[i].equalsIgnoreCase("true"))
					invalidater[i]="T";
			com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean terminalDOB=(com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean)dobList.get(i);
			terminalDOB.setInvalidate(invalidater[i]);
		 }
		 			flag=ETransHOSuperUserRemote.invalidateTerminalMaster(dobList);

			if( flag )
			{
				String errorMessage = "Record successfully updated  ";
				session.setAttribute("ErrorCode","RSV");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Invalidate");
				session.setAttribute("NextNavigation","../Invalidate.jsp?View=terminalInvalidate");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
				String errorMessage = "Recored could updated: ";
				session.setAttribute("ErrorCode","RSU");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Invalidate");
				session.setAttribute("NextNavigation","../Invalidate.jsp?View=terminalInvalidate");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
		}
		else
		{
		String notes    = request.getParameter("notes");    // String to store notes
		String contact  = request.getParameter("contactName");     // String to store contact name
		
		String collectStatus    = request.getParameter("collectShipment");
		String emailStatus  	= request.getParameter("emailStatus");
		String timeZone    		= request.getParameter("timeZone");
		String opTerminalType  	= request.getParameter("opTerminalType");	
		String shipmentMode  	= request.getParameter("shipmentMode");	
		//@@ Srivegi Added on 20050419 (Invoice-PR)
      	String stockedInvoiceIds  = request.getParameter("stockedInvoiceIds");
		//@@ 20050419 (Invoice-PR)		
		//Added BY I.V.Sekhar
		String adminROTerminal	= (request.getParameter("adminROTerminal")!=null)?request.getParameter("adminROTerminal"):"";
		//end
		//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
		String frequency  	    = (request.getParameter("frequency")!=null)?request.getParameter("frequency"):"";
		String carrier  	    = (request.getParameter("carrier")!=null)?request.getParameter("carrier"):"";
		String transittime  	= (request.getParameter("transittime")!=null)?request.getParameter("transittime"):"";
		String rateValidity  	= (request.getParameter("rateValidity")!=null)?request.getParameter("rateValidity"):"";
        //<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
		// checking whether  String notes is null or not
		if(notes == null || notes.trim().length()==0)     
			terminalReg.setNotes(" ");
		// checking whether  String contact is null or not
		if(contact ==null || contact.trim().length()==0)
			terminalReg.setContactName(" ");
			
		if(collectStatus == null || collectStatus.trim().length()==0)
			collectStatus=" ";
		if(emailStatus ==null || emailStatus.trim().length()==0)
			emailStatus=" ";
		if(timeZone == null || timeZone.trim().length()==0)
				timeZone = " ";
			
		terminalReg.setCollectShipment(collectStatus);
		terminalReg.setEmailStatus(emailStatus);
		terminalReg.setTimeZone(timeZone);
		terminalReg.setOperationTerminalType(opTerminalType);
		terminalReg.setShipmentMode(shipmentMode);

		terminalReg.setChildTerminalFlag(adminROTerminal);//Added By I.V.Sekhar

		//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
        terminalReg.setFrequency(frequency);
		terminalReg.setCarrier(carrier);
	    terminalReg.setTransitTime(transittime);
		terminalReg.setRateValidity(rateValidity);
		//<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->

		terminalReg.setIataCode(request.getParameter("iatacode"));
		terminalReg.setAccountCode(request.getParameter("accountcode"));
		terminalReg.setTaxRegNo(request.getParameter("taxregno"));

		//Logger.info(FILE_NAME,"terminalReg.getOperationTerminalType() : "+terminalReg.getOperationTerminalType());
		//@@ Srivegi Added on 20050419 (Invoice-PR)
		terminalReg.setStockedInvoiceIdsCheck(stockedInvoiceIds);
		//@@ 20050419 (Invoice-PR)			
		//added by phani sekhar for wpbn 170758 on 20090626
		terminalReg.setMarginType(request.getParameter("marginType"));
		terminalReg.setDiscountType(request.getParameter("discountType"));
		//ends 170758
			
		InitialContext initial = new InitialContext();    // creating an instance of InitialContext
		SetUpSession trr = (SetUpSession)session.getAttribute("remote");    // obtaining SetUpSession bean Object from Session
		//Logger.info(FILE_NAME,"trr : "+trr);		
		String strAddressId = request.getParameter("addressId");    // storing address Id in a String
		String locationIdHide = request.getParameter("locationIdHide");     // storing locationIdHide value in a String
		
		strParam = request.getParameter("Operation");
		//Logger.info(FILE_NAME,"strParam : "+strParam);
		if(strParam!=null && !strParam.equals("Delete") && !strParam.equals("View"))
		{
			StringTokenizer st = new StringTokenizer(locationIdHide,",");       // creating a StringTokenizer Object with locationIdHide as one of the parameters
			int len =0;    // an integer to maintain the count of tokens in the 'st' StringTokenizer
			// while there are tokens in StringTokenizer
			while(st.hasMoreTokens())
			{
				len++;
				st.nextToken();
			}
			String locationIds[] = new String[len];    // a String array to store location Ids
			st = new StringTokenizer(locationIdHide,",");
	
			// populating String array 'locationIds' with tokens from StringTokenizer
			for(int i=0;st.hasMoreTokens();i++)
			{
				locationIds[i] = st.nextToken();
			}
			terminalReg.setLocationId(locationIds);
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
					terminalReg.setCBTLocationId(cbtLocationIds);
			 }

			/*End of CBT Terminals*/
		}
			
		int addressId = Integer.parseInt(strAddressId);    
		String oldlocationid= request.getParameter("oldlocationId");
		
		terminalReg.setContactAddressId(addressId);
		address.setAddressId(addressId);
		
		
		//checking for the type of operation
		if(strParam.equals("Modify"))
		{    
			//setting Server Time Difference 
			ESupplyDateUtility eSupplyDateUtility   = new ESupplyDateUtility();
			String serverTimeDiff =    eSupplyDateUtility.getTimeDifference(terminalReg.getTimeZone());
			terminalReg.setServerTimeDiff(serverTimeDiff);
			terminalReg.setChildTerminalFlag(!"null".equals(adminROTerminal)?adminROTerminal:"");//Added By I.V.Sekhar
			boolean flag = trr.updateTerminalReg(terminalReg,address,oldlocationid,loginbean);
			if( flag)
			{
				String errorMessage = "Record successfully modified with Terminal Id : "+terminalReg.getTerminalId();
				session.setAttribute("ErrorCode","RSM");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Modify");
				session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
				String errorMessage = "Error while modifying Terminal Id : "+terminalReg.getTerminalId();
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Modify");
				session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
		}
		else if(strParam.equals("Delete"))
		{
			//Logger.info(FILE_NAME,"terminalReg.getTerminalId() : "+terminalReg.getTerminalId());
			//Logger.info(FILE_NAME,"terminalReg.getContactAddressId() : "+terminalReg.getContactAddressId());
			//Logger.info(FILE_NAME,"oldlocationid : "+oldlocationid);
			//Logger.info(FILE_NAME,"opTerminalType : "+opTerminalType);
			
			boolean flag = trr.deleteTerminalReg(terminalReg.getTerminalId(),terminalReg.getContactAddressId(),oldlocationid,opTerminalType,loginbean);
			if( flag )
			{
				String errorMessage = "Record successfully deleted with Terminal Id : "+terminalReg.getTerminalId();
				session.setAttribute("ErrorCode","RSD");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Delete");
				session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
				String errorMessage = "Recored could not be deleted with Terminal Id : "+terminalReg.getTerminalId();
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Delete");
				session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
		
	}
  }
  }
	catch(Exception exp)
	{
			//Logger.error(FILE_NAME,"Error in TerminalRegistrationProcess.jsp file 2: ", exp.toString());
      logger.error(FILE_NAME+"Error in TerminalRegistrationProcess.jsp file 2: "+ exp.toString());
			exp.printStackTrace();
			String errorMessage = "Unable to "+strParam+" the record with Terminal Id : "+terminalReg.getTerminalId();
			session.setAttribute("ErrorCode","ERR");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("Operation",strParam);
			session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
  
%>

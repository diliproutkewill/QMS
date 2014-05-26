<%--
   Program Name		: ETCustomerRegistrationProcess.jsp
   Module name		: HO Setup
   Task		        : Adding Customer
   Sub task	    	: adding a new Customer by looking up the CustomerReg Session Bean
   Author Name		: A.Hemanth Kumar
   Date Started		: September 08, 2001
   Date completed	: September 11, 2001
   Date Modified	: December 10,2001 by Rizwan.
			Corrected the problem which was not showing CorporateCustomerId when modified.
   Description      :
    This file is used to add a new Customer by looking up the CustomerReg Session Bean. Then it calls
    the method, setCustomerDetail( CustomerDetail CustomerRegObj, Address AddressObj, ESupplyGlobalParameters loginbean) of the session bean.
    which inturn inserts the values into the tables FS_FR_CUSTOMERMASTER and FS_ADDRESS.

--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<jsp:useBean id = "CustomerRegObj" class = "com.foursoft.etrans.setup.customer.java.CustomerModel" scope = "request"/>
<jsp:setProperty name = "CustomerRegObj" property = "*"/>
<jsp:useBean id = "AddressObj" class = "com.foursoft.etrans.common.bean.Address" scope = "request"/>
<jsp:setProperty name = "AddressObj" property = "*"/>

<%@ page import = " com.qms.setup.ejb.sls.SetUpSessionHome,
					com.qms.setup.ejb.sls.SetUpSession,
					com.foursoft.etrans.setup.customer.java.CustomerModel,
					com.foursoft.etrans.common.bean.Address,
					javax.naming.InitialContext,
					javax.naming.Context,
				    org.apache.log4j.Logger,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList,
					com.foursoft.etrans.setup.customer.java.CustContactDtl,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					java.sql.Timestamp,
					com.foursoft.etrans.setup.codecust.exception.CodeCustNotDoneException,
					com.foursoft.esupply.common.exception.FoursoftException" %>

<%!
  private static Logger logger = null;
%>

<%
	String  operation		= request.getParameter("Operation");  // String to store type of operation obtained from session
	SetUpSessionHome 	home   		= 	null;    // variable to store Home Object
	SetUpSession     	 	remote	= 	null;    // variable to store Remote Object
	Context initial			= null;
	CustContactDtl		custDtl=null;
	String FILE_NAME ="ETCustomerRegistrationProcess.jsp";
  logger  = Logger.getLogger(FILE_NAME);	
	initial 				= 	new InitialContext();
if( operation.equalsIgnoreCase("addContactDetails"))
  {
	String addrType1[]			=   request.getParameterValues("addrtype");
	String contactPerson1[]		=	request.getParameterValues("contactPerson");
	String designation1[]		=	request.getParameterValues("Designation");
	String dept1[]				=	request.getParameterValues("Department");
	String zipCode1[]			=	request.getParameterValues("ZipCode");
	String contact1[]			=	request.getParameterValues("Contact");
	String fax1[]				=	request.getParameterValues("conFax");
	String email1[]				=	request.getParameterValues("Email");
	String customerId1			=	request.getParameter("custId");


	ArrayList custContactDtls	=	new ArrayList();
	if(contactPerson1.length>0)
	{
		for(int i=0;i<contactPerson1.length;i++)
		{
			custDtl=new CustContactDtl();
			if(contactPerson1[i]!=null && !contactPerson1[i].equalsIgnoreCase(""))
			{
				custDtl.setAddrType(addrType1[i]);
				custDtl.setContactPerson(contactPerson1[i]);
				custDtl.setDesignation(designation1[i]==null?"":designation1[i]);
				custDtl.setDept(dept1[i]==null?"":dept1[i]);
				custDtl.setZipCode(zipCode1[i]==null?"":zipCode1[i]);
				custDtl.setContact(contact1[i]==null?"":contact1[i]);
				custDtl.setFax(fax1[i]==null?"":fax1[i]);
				custDtl.setEmail(email1[i]==null?"":email1[i]);
				custContactDtls.add(custDtl);
			}
		}
		
		try
		{
			  home			= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		      remote 		= (SetUpSession)home.create();
			 // System.out.println("alsdkfj:::");
			  String straddr=remote.insertCustContactDetial(custContactDtls,customerId1,loginbean.getTerminalId());
			  //System.out.println("StrAddr:"+straddr);
%>
<script>
				window.close();
</script>
<%	
			//System.out.println("StrAddr3333:"+straddr);
		}catch(Exception e)
		{
			System.out.println("exception e:"+e);
		}
  }
}
else if(operation.equalsIgnoreCase("addAddress"))	 	
{
	
	String contactPersond		=	request.getParameter("contactPersondm")==null?"":request.getParameter("contactPersondm");
	String designationd		    =	request.getParameter("designationdm")==null?"":request.getParameter("designationdm");
	String add_Flagd			=	request.getParameter("add_Flagdm");
	String del_Flagd			=	request.getParameter("del_Flagdm");
	String addressLine1d		=	request.getParameter("addressLine1dm");
	String addressLine2d		=	request.getParameter("addressLine2dm")==null?"":request.getParameter("addressLine2dm");
	String cityd				=	request.getParameter("citydm");
	String zipOrPostalCoded 	=	request.getParameter("zipOrPostalCodedm")==null?"":request.getParameter("zipOrPostalCodedm");
	String stated	    		=	request.getParameter("statedm")==null?"":request.getParameter("statedm");
	String countryIdd			=	request.getParameter("countryId");
	String contactNod			=	request.getParameter("contactNodm")==null?"":request.getParameter("contactNodm");
	String emailIdd  			=	request.getParameter("emailIddm")==null?"":request.getParameter("emailIddm");
	String custId				=	request.getParameter("custId");
	Address addrObj				=	new Address();
	CustContactDtl  custDtl1		=   new CustContactDtl();

	custDtl1.setContactPerson(contactPersond);
	custDtl1.setDesignation(designationd);
	custDtl1.setdeleteOption(del_Flagd);
	custDtl1.setAddrType(add_Flagd);
	addrObj.setAddressLine1(addressLine1d);
	addrObj.setAddressLine2(addressLine2d);
	addrObj.setCity(cityd);
	addrObj.setState(stated);
	addrObj.setZipCode(zipOrPostalCoded);
	addrObj.setCountryId(countryIdd);
	addrObj.setPhoneNo(contactNod);
	addrObj.setEmailId(emailIdd);


		try
		{
			  home					= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		      remote 				= (SetUpSession)home.create();
			  StringBuffer straddr	= remote.insertCustAddrDetails(custId,loginbean.getTerminalId(),custDtl1,addrObj);
			  String strAddress		= straddr.toString();
			  String[]	   address	= strAddress.split(",");
			  //System.out.println("adress***********************:"+strAddress);
%>
				 <script language="javascript">
					window.opener.document.forms[0].addressId.value = '<%=address[1]%>';
					window.opener.document.forms[0].address.value='<%=address[0]%>';
					window.close();
				</script>
<%				   
		}catch(Exception e)
		{
				//Logger.error(FILE_NAME,"Exception in process page"+e);
        logger.error(FILE_NAME+"Exception in process page"+e);
				e.printStackTrace();
				String errorMessage = "Error inserting the details  ";
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Add");
				session.setAttribute("NextNavigation","");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}

}
//Logger.info(FILE_NAME,"After the Operation.....");
logger.info(FILE_NAME+"After the Operation.....");
if(operation.equalsIgnoreCase("Invalidate"))
	{
		try
		{
			  initial 				= 	new InitialContext();   // obtaining Initial Context
			  home   		= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		      remote 		= (SetUpSession)home.create();
			  java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
			   String invalidater[]=request.getParameterValues("checkBoxValue");
			   boolean flag=false;
			   for(int i=0;i<dobList.size();++i)
			 {
				   if(invalidater[i].equalsIgnoreCase("false"))
						invalidater[i]="F";
				   else if(invalidater[i].equalsIgnoreCase("true"))
						invalidater[i]="T";
				com.foursoft.etrans.setup.customer.java.CustomerModel custDOB=(com.foursoft.etrans.setup.customer.java.CustomerModel )dobList.get(i);
				custDOB.setInvalidate(invalidater[i]);
			}
				flag=remote.invalidateCustomerMaster(dobList);

			if(flag)
			{String errorMessage = "Record successfully updated  ";
				session.setAttribute("ErrorCode","RSD");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Update");
				session.setAttribute("NextNavigation","");
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else
			{
				String errorMessage = "Recored could updated: ";
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("Operation","Delete");
				session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
			}
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
				<%

		}
		catch(Exception e)
		{
			System.out.println("exception at invalidate:"+e);
		}
	}
	else if(!"Invalidate".equalsIgnoreCase(operation) && !"addAddress".equalsIgnoreCase(operation) && !"addContactDetails".equalsIgnoreCase(operation))
	{
		//Logger.info(FILE_NAME,"In Else");
    logger.info(FILE_NAME+"In Else");
	String	customerId 		= null;    	// String to store customer Id obtained from session
	String	errorMessage 	= null;		// String to store errorMessage
	String	custType 		= null;
	String	corpCustomerId	= null;
		String  customerType	= null;
    String contactPerFistName		= null;
	String contactPerLastName		= null;
	String einssn		    = null;
	String knownShipper		= null;
	// @@ Suneetha added on 20050305 for Bulk Invoicing
	String  invoiceFrequencyValidDate = request.getParameter("invoiceFrequencyValidDate");
	String  invoiceFrequencyFlag	  = request.getParameter("invoiceFrequencyFlag");	
	String  invoiceInfo			  = request.getParameter("invoiceInfo");

	String  bulkInvoiceRequired	  = request.getParameter("bulkInvoiceRequired")!=null?request.getParameter("bulkInvoiceRequired"):"N";	

	String		dateFormat		=	loginbean.getUserPreferences().getDateFormat();	
	ESupplyDateUtility	eSupplyDateUtility	=	new ESupplyDateUtility();
	Timestamp invoiceFrequencyValidFrom		=	null;
	
	String paymentTerms			=	request.getParameter("paymentTerms")!=null?request.getParameter("paymentTerms"):"";
	String division				=	request.getParameter("division")!=null?request.getParameter("division"):"";
	String fromWhat				=	request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):"";
	String contactPersond[]		=	request.getParameterValues("contactPersond");
	String designationd[]		=	request.getParameterValues("designationd");
	String add_Flagd[]			=	request.getParameterValues("add_Flagd");
	String del_Flagd[]			=	request.getParameterValues("del_Flagd");
	String addressLine1d[]		=	request.getParameterValues("addressLine1d");
	String addressLine2d[]		=	request.getParameterValues("addressLine2d");
	String addressLine3d[]		=	request.getParameterValues("addressLine3d");
	String cityd[]				=	request.getParameterValues("cityd");
	String zipOrPostalCoded[]	=	request.getParameterValues("zipOrPostalCoded");
	String stated[]				=	request.getParameterValues("stated");
	String countryIdd[]			=	request.getParameterValues("countryIdd");
	String contactNod[]			=	request.getParameterValues("contactNod");
	String emailIdd[]			=	request.getParameterValues("emailIdd");
	String customerAddressId	=	request.getParameter("customerAddressId");
	
	
	String contactPersondm[]	=	request.getParameterValues("contactPersondm");
	String designationdm[]		=	request.getParameterValues("designationdm");
	String add_Flagdm[]			=	request.getParameterValues("add_Flagdm");
	String del_Flagdm[]			=	request.getParameterValues("del_Flagdm");
	String addressLine1dm[]		=	request.getParameterValues("addressLine1dm");
	String addressLine2dm[]		=	request.getParameterValues("addressLine2dm");
	String addressLine3dm[]		=	request.getParameterValues("addressLine3dm"); 
	String citydm[]				=	request.getParameterValues("citydm");
	String zipOrPostalCodedm[]	=	request.getParameterValues("zipOrPostalCodedm");
	String statedm[]			=	request.getParameterValues("statedm");
	String countryIddm[]		=	request.getParameterValues("countryIddm");
	String contactNodm[]		=	request.getParameterValues("contactNodm");
	String emailIddm[]			=	request.getParameterValues("emailIddm");
	String addressIddm[]		=	request.getParameterValues("addressIddm");
    String serviceLevelId		=   request.getParameter("serviceLevelId");
	String addrType[]			=   request.getParameterValues("addrtype");
	String contactId[]			=   request.getParameterValues("ContactId");
	String contactPerson[]		=	request.getParameterValues("contactPerson");
	String designation[]		=	request.getParameterValues("Designation");
	String dept[]				=	request.getParameterValues("Department");
	String zipCode[]			=	request.getParameterValues("ZipCode");
	String contact[]			=	request.getParameterValues("Contact");
	String fax[]				=	request.getParameterValues("conFax");
	String email[]				=	request.getParameterValues("Email");
	
	ArrayList costAddressList	=	new ArrayList();
	ArrayList costAddressListME	=	new ArrayList();	//Here 	costAddressListME means Exixting Customer Etails
	ArrayList costAddressListM	=	new ArrayList();	//This is new Modify costomer Address details
	ArrayList custContactDtl	=	new ArrayList();
  
  		// @@ Added By Kiran Kumar S 
		// 11/9/2005
		// To Be Modified After, As this is not correct function
	if(addressLine1dm!=null)
		{
			if(addressLine3dm==null)
			{
				addressLine3dm = new String[addressLine1dm.length]; 
			}
		}
    
    
//  K.N.V.Prasada Reddy4
  if((serviceLevelId!=null)&&(serviceLevelId.length()>0))
    {
     CustomerRegObj.setServiceLevelId(request.getParameter("serviceLevelId"));
     } 
if(operation.equals("Add")){	
		
	if((add_Flagd!=null) && (add_Flagd.length> 0) && (addressLine1d!=null && addressLine1d.length > 0) && (cityd!=null && cityd.length > 0) && (countryIdd!=null && countryIdd.length > 0) ){
		for(int i=0;i<addressLine1d.length;i++){
		
			String tempDelFlag	=	"N";
				if(operation.equals("Add")){
					tempDelFlag = "N";
				}	
				costAddressList.add(new Address(addressLine1d[i],addressLine2d[i],addressLine3d[i],cityd[i],stated[i],zipOrPostalCoded[i],countryIdd[i],contactNod[i],emailIdd[i],contactPersond[i],designationd[i],tempDelFlag,add_Flagd[i]) );
		}
	}
	if(contactPerson.length>0)
	{
		for(int i=0;i<contactPerson.length;i++)
		{
			custDtl=new CustContactDtl();
			if(contactPerson[i]!=null && !contactPerson[i].equalsIgnoreCase(""))
			{
				custDtl.setAddrType(addrType[i]);
				custDtl.setContactPerson(contactPerson[i]);
				custDtl.setDesignation(designation[i]==null?"":designation[i]);
				custDtl.setDept(dept[i]==null?"":dept[i]);
				custDtl.setZipCode(zipCode[i]==null?"":zipCode[i]);
				custDtl.setContact(contact[i]==null?"":contact[i]);
				custDtl.setFax(fax[i]==null?"":fax[i]);
				custDtl.setEmail(email[i]==null?"":email[i]);
				custContactDtl.add(custDtl);
			}
		}
	}

}

if(operation.equals("Modify")||operation.equals("Delete")){



		if((add_Flagdm!=null) && (add_Flagdm.length> 0) && (addressLine1dm!=null && addressLine1dm.length > 0) && (citydm!=null && citydm.length > 0) && (countryIddm!=null && countryIddm.length > 0) ){
			for(int i=0;i<add_Flagdm.length && i<addressIddm.length;i++){
					costAddressListME.add(new Address(addressLine1dm[i],addressLine2dm[i],addressLine3dm[i], citydm[i],statedm[i],zipOrPostalCodedm[i],countryIddm[i],contactNodm[i],emailIddm[i],contactPersondm[i],designationdm[i],del_Flagdm[i],add_Flagdm[i],Integer.parseInt(addressIddm[i])) );
			}//for
		}//if
			if((add_Flagd!=null) && (add_Flagd.length> 0) && (addressLine1d!=null && addressLine1d.length > 0) && (cityd!=null && cityd.length > 0) && (countryIdd!=null && countryIdd.length > 0) ){
				for(int i=0;i<add_Flagd.length;i++){				
					if(del_Flagd[i].equals("N")){
						costAddressListM.add(new Address(addressLine1d[i],addressLine2d[i],addressLine3d[i], cityd[i],stated[i],zipOrPostalCoded[i],countryIdd[i],contactNod[i],emailIdd[i],contactPersond[i],designationd[i],del_Flagd[i],add_Flagd[i]) );
					}//IF
				}//FOR
			}//IF
	if(contactPerson!=null && contactPerson.length>0)
	{
		for(int i=0;i<contactPerson.length;i++)
		{
			if(contactPerson[i]!=null && contactPerson[i].trim().length()!=0)
			{
				custDtl=new CustContactDtl();
				custDtl.setAddrType(addrType[i]);
				custDtl.setContactPerson(contactPerson[i]);
				custDtl.setDesignation(designation[i]==null?"":designation[i]);
				custDtl.setDept(dept[i]==null?"":dept[i]);
				custDtl.setZipCode(zipCode[i]==null?"":zipCode[i]);
				custDtl.setContact(contact[i]==null?"":contact[i]);
				custDtl.setFax(fax[i]==null?"":fax[i]);
				custDtl.setEmail(email[i]==null?"":email[i]);
				custDtl.setContactId((contactId[i]!=null && contactId[i].trim().length()!=0)?(Integer.parseInt(contactId[i])):-1);
				custContactDtl.add(custDtl);
			}
		}
	}
}//Modify



//End of the Add More Customer Address detailes

	
	
	
	
	ErrorMessage errorMessageObject = null;
	ArrayList	 keyValueList	    = new ArrayList();
	
	try
	{
		
		//@@ Srivegi added on 20050224 (AES-SED)
         contactPerFistName		=	request.getParameter("contactPerFistName");
		 contactPerLastName		=	request.getParameter("contactPerLastName");
		 einssn				    =	request.getParameter("einssn");
		 knownShipper			=	request.getParameter("knownShipper");
        //@@ 20050224
        
		initial 				= 	new InitialContext();   // obtaining Initial Context
		custType 				= 	request.getParameter("typeOfCustomer");
		customerId  			= 	request.getParameter("customerId").trim();
		corpCustomerId  		= 	request.getParameter("corpCustomerId");
		operation 				= 	request.getParameter("Operation");
		String creditFlag		=	request.getParameter("creditFlag");
		String masterCloseFlag	=	request.getParameter("masterCloseFlag");
		String bbFlag			=	request.getParameter("bbFlag");
		String prqCreateFlag	=	request.getParameter("prqCreateFlag");
		String prqDeleteFlag	=	request.getParameter("prqDeleteFlag");
		CustomerRegObj.setCustomerId(customerId);
         CustomerRegObj.setContactName(contactPerFistName);
					
					CustomerRegObj.setContactLastName(contactPerLastName);
					CustomerRegObj.setEINSSNNo(einssn);
					CustomerRegObj.setcustType(knownShipper);
					// @@ Suneetha added on 20050305 for Bulk Invoicing
					CustomerRegObj.setBulkInvoiceRequired(bulkInvoiceRequired);
					if("Y".equals(CustomerRegObj.getBulkInvoiceRequired()))
					{
						invoiceFrequencyValidFrom	= eSupplyDateUtility.getTimestamp(dateFormat,invoiceFrequencyValidDate);
						CustomerRegObj.setInvoiceFrequencyValidDate(invoiceFrequencyValidFrom);
						CustomerRegObj.setInvoiceFrequencyFlag(invoiceFrequencyFlag);
						CustomerRegObj.setInvoiceInfo(invoiceInfo);
					}
					
					// @@ 20050305 for Bulk Invoicing
		if(creditFlag == null )
			CustomerRegObj.setCreditFlag("N");
		if(masterCloseFlag == null )
			CustomerRegObj.setMasterCloseFlag("N");
		if(bbFlag == null )
			CustomerRegObj.setBBFlag("N");
		if(prqCreateFlag==null)
			CustomerRegObj.setPrqCreateFlag("N");
		if(prqDeleteFlag==null)
			CustomerRegObj.setPrqDeleteFlag("N");
		
		java.util.ArrayList	vecCustomerId	=	null;    // Vector to store Customer Ids
		int len	=	0;    								// integer to store sizze of Vector

		//SetUpSessionHome 	home   		= 	null;    // variable to store Home Object
		//SetUpSession     	 	remote	= 	null;    // variable to store Remote Object
	   	String						whereClause	=	" REGISTERED='R' AND CUSTOMERTYPE='Corporate' AND CUSTOMERID='"+corpCustomerId+"' ";
	   	boolean flag=false;
	   	if(operation.equalsIgnoreCase("Add"))
		{
				home   			= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
				remote 			= (SetUpSession)home.create();


				if(customerId != null && customerId.length()>0)
				{
					CustomerRegObj.setAbbrName(customerId);
                    //@@ Srivegi added on 20050224 (AES-SED)
                    CustomerRegObj.setContactName(contactPerFistName);
					
					CustomerRegObj.setContactLastName(contactPerLastName);
					CustomerRegObj.setEINSSNNo(einssn);
					CustomerRegObj.setcustType(knownShipper);
                    //@@ for credit Details
					CustomerRegObj.setCreditDays(Integer.parseInt(request.getParameter("creditdays")));
                    CustomerRegObj.setCreditLimit(Double.parseDouble(request.getParameter("creditlimit")));
					//
					//@@ 20050224
					//vecCustomerId	= remote.getCustomerIds(loginbean,customerId);
					//System.out.println("customer dtls:::::");
					vecCustomerId	= remote.getCustomerIds(loginbean,whereClause);
					for(int i=0;i<vecCustomerId.size();i++)
					{
						String 	temp		=	(String)vecCustomerId.get(i);
						int 	custId		=	temp.indexOf('[');
						String 	tempCustId	=	temp.substring(0,custId).trim();
						if(tempCustId.equalsIgnoreCase(customerId))
						{
							flag	=	true;
							break;
						}
					}//end of for loop
					if(flag == true)
					{
%>
							<script>
							alert("Customer already exists with this id ,Please enter correct Corporate CustomerId:");
							history.back();
							</script>
<%
	     					return;

	    		 	} // if flag

	    		} // if customerid
				if(custType.equalsIgnoreCase("Customer"))
				{
					if(corpCustomerId != null && corpCustomerId.length()>0)
					{
						vecCustomerId	= remote.getCustomerIds(loginbean,whereClause);
						for(int i=0;i<vecCustomerId.size();i++)
						{
							String 	temp		=	(String)vecCustomerId.get(i);
							int 	custId		=	temp.indexOf('[');
							String 	tempCustId	=	temp.substring(0,custId).trim();
							if(tempCustId.equalsIgnoreCase(corpCustomerId))
							{
								flag	=	true;
								break;
							}
						}//end of for loop
								if(flag == false)
								{
			%>
										<script>
										alert("Please enter correct Corporate CustomerId:");
										history.back();
										</script>
			<%
										return;

								} // if flag

					}// corpCustomerId block
				}// if customer block
	 	}  // if add block

	}//end of try block
	catch(Exception e)
	{
			String customer			=	request.getParameter("Customer");
			String registrationLevel=	request.getParameter("registrationLevel");
			String url				=	"ETCustomerRegistrationAdd.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;
			errorMessage 			= 	"Error occured while "+operation+"ing the record";
			
			errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
						
%>
			<jsp:forward page=" ../ESupplyErrorPage.jsp" />
<%
	}

	if(operation.equalsIgnoreCase("Add"))
	{
		String customer				=	request.getParameter("Customer");
		String registrationLevel	=	request.getParameter("registrationLevel");
		String url					=	"ETCustomerRegistrationAdd.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;
		try
		{
			home 	= 	(SetUpSessionHome)initial.lookup("SetUpSessionBean");  // varaible to store Home object
			remote	= 	(SetUpSession)home.create();    // variable to store Remote Object
		//	customerId 									= 	remote.createCustomerDetails(CustomerRegObj,AddressObj,loginbean);
		    CustomerRegObj.setContactDtl(custContactDtl);
			CustomerRegObj.setPaymentTerms(paymentTerms);
			CustomerRegObj.setDivision(division);
			CustomerRegObj.setCreditDays(Integer.parseInt(request.getParameter("creditdays")));
            CustomerRegObj.setCreditLimit(Double.parseDouble(request.getParameter("creditlimit")));
CustomerRegObj.setTerminalId(request.getParameter("terminalId"));//@@ Added by subrahmanyam for 167669 on 27/04/09
			AddressObj.setAddressLine3(request.getParameter("addressLine3"));
			boolean flag		= false;
			String	errorCode	= "RSI";
			try{
				customerId=remote.createCustomerDetails(CustomerRegObj,AddressObj,loginbean,costAddressList);
 				
			}catch(FoursoftException e)
			{
              flag		= true;
			  errorCode	= "RAE";
			 errorMessage = "Customer Already Exists with CustomerId:"+CustomerRegObj.getCustomerId();

			}
            catch(CodeCustNotDoneException ccnd)
			{
				ccnd.printStackTrace();
				flag = true;
				errorCode	= "ERR";
				url	 = "ETCCodeCustomisationAdd.jsp";
				//Logger.error(FILE_NAME,"Code Customisation Not Done for Customer");
				logger.error(FILE_NAME+"Code Customisation Not Done for Customer");
				errorMessage 			= 	"Code Customization Has Not Been Done for Customer. Please Click on the Continue Button to do Code Customization.";
			}   
			// if above customer Id exists
			if(customerId!=null)
			{
				if("Quote".equalsIgnoreCase(request.getParameter("fromWhat")))
				{
					
					url						=	"javascript:window.close();";
					 if(!flag)
					{
%>
					<script>
							
							window.opener.document.forms[0].customerId.value="<%=customerId%>";
							window.close();
					</script>
<%
					}else
					{
							errorMessageObject      =  new ErrorMessage(errorMessage,url); 
							keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
					%>
						<jsp:forward page="../ESupplyErrorPage.jsp" />
					<%

					}
				}
				else if(customerId.equals("CND") && !flag)
					errorMessage 				= 	"Code Customisation Not Done,Please do CodeCustomization  ";
				else if(!flag)
					errorMessage 				= 	"Record successfully added with CustomerId : "+customerId;
				
				/*String customer				=	request.getParameter("Customer");
				String registrationLevel	=	request.getParameter("registrationLevel");
				String url					=	"ETCustomerRegistrationAdd.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;*/
				
				errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
				keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				if(!"Quote".equalsIgnoreCase(request.getParameter("fromWhat")))
				{				
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			}}
			else
			{
				//Logger.info(FILE_NAME,"in else state");
				errorMessage 				= 	"Unable to add Record : ";
				/*String	customer			=	request.getParameter("Customer");
				String 	registrationLevel	=	request.getParameter("registrationLevel");
				String 	url					=	"ETCustomerRegistrationAdd.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;*/
				
				errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
			
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%			}
		}
		catch(Exception ee)
		{
							//Logger.info(FILE_NAME,"in exception"+ee);
              logger.info(FILE_NAME+"in exception"+ee);
			errorMessage 			= 	"Error while adding the record ";
			/*String customer			=	request.getParameter("Customer");
			String registrationLevel=	request.getParameter("registrationLevel");
			String url				=	"ETCustomerRegistrationAdd.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;*/
			
			errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			
						
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%		}
	}
	else
	{
	//	Logger.info(FILE_NAME,"Hai this is at line no 175");
		String terminalId	=	"";
		String Customer 	= 	"";
		String registered 	= 	"";
		try
		{
			customerId   =  request.getParameter("customerId");
			terminalId   =  request.getParameter("terminalId");
			Customer     =  request.getParameter("Customer");
	        registered	 =  request.getParameter("registered"); 
			home 	= (SetUpSessionHome)initial.lookup("SetUpSessionBean");  // varaible to store Home object
			remote = (SetUpSession)home.create();    // variable to store Remote Object
			
	        // checking for the type of operation
			
			if(registered!=null)
				CustomerRegObj.setRegistered(registered);
			if(customerAddressId!=null){
				CustomerRegObj.setCustomerAddressId(Integer.parseInt(customerAddressId));
				AddressObj.setAddressId(CustomerRegObj.getCustomerAddressId());
			}
			
			if(operation.equals("Modify"))
			{
				//Logger.info(FILE_NAME,"Hai this is at line no 320");

//js
/*
				if((add_Flagdm!=null) && (add_Flagdm.length> 0) && (addressLine1dm!=null && addressLine1dm.length > 0) && (citydm!=null && citydm.length > 0) && (countryIddm!=null && countryIddm.length > 0) ){
				for(int i=0;i<add_Flagdm.length && i<addressIddm.length;i++){
						Logger.info(FILE_NAME," 5345346234 addressIddm["+i+"]--->"+addressIddm[i]); 
						costAddressListME.add(new Address(addressLine1dm[i],addressLine2dm[i], citydm[i],statedm[i],zipOrPostalCodedm[i],countryIddm[i],contactNodm[i],emailIddm[i],contactPersondm[i],designationdm[i],del_Flagdm[i],add_Flagdm[i],Integer.parseInt(addressIddm[i])) );
				}
				//Logger.info(FILE_NAME," designationd --->"+designationd[i]+" add_Flagd--> " +add_Flagd[i]);
			//	Logger.info(FILE_NAME,"Size of the Array List costAddressList---> "+costAddressListME.size());

		
				//Logger.info(FILE_NAME," Values Of Multipule Address Detailes Are --->");
					for(int i=0;i<add_Flagd.length;i++){				
						if(del_Flagd[i].equals("N")){
						//	Logger.info(FILE_NAME," del_Flagdm[i] --->"+del_Flagd[i]+" operation--> "+operation);		
						
							costAddressListM.add(new Address(addressLine1d[i],addressLine2d[i], cityd[i],stated[i],zipOrPostalCoded[i],countryIdd[i],contactNod[i],emailIdd[i],contactPersond[i],designationd[i],del_Flagd[i],add_Flagd[i]) );
						}
					}
					//Logger.info(FILE_NAME," designationd --->"+designationd[i]+" add_Flagd--> " +add_Flagd[i]);
					Logger.info(FILE_NAME,"Size of the Array List costAddressList---> "+costAddressListM.size());
				
			}//if
*/
//jS

				
			//	remote.updateCustomerDetails(CustomerRegObj,AddressObj,loginbean);
		// JS
				CustomerRegObj.setContactDtl(custContactDtl);
				CustomerRegObj.setPaymentTerms(paymentTerms);
				CustomerRegObj.setDivision(division);
				CustomerRegObj.setCreditDays(Integer.parseInt(request.getParameter("creditdays")));
                CustomerRegObj.setCreditLimit(Double.parseDouble(request.getParameter("creditlimit")));

				remote.updateCustomerDetails(CustomerRegObj,AddressObj,loginbean,costAddressListME,costAddressListM);

				errorMessage 			= 	"Record successfully modified with CustomerId : "+ CustomerRegObj.getCustomerId();
				String customer			=	request.getParameter("Customer");
				String registrationLevel=	request.getParameter("registrationLevel");
				String url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation=Modify&Customer=CCS&registrationLevel=T";
				
				
				if(registrationLevel!=null && registrationLevel.equals("C"))
					url				=	"ETCustomerRegistrationEnterIdC.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

				errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
				keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
	%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
	<%
			}
		else if(operation.equals("Delete"))
		{
			home 	= (SetUpSessionHome)initial.lookup("SetUpSessionBean");  // varaible to store Home object
			remote = (SetUpSession)home.create();    // variable to store Remote Object
			//Logger.info(FILE_NAME," befor calling deleteCustomerDetail method ");		
			CustomerRegObj.setContactDtl(custContactDtl);
			if(remote.deleteCustomerDetail(CustomerRegObj,AddressObj,loginbean))
			{
					errorMessage 			= 	"Record successfully deleted  with CustomerId : "+ CustomerRegObj.getCustomerId();
					String customer			=	request.getParameter("Customer");
					String registrationLevel=	request.getParameter("registrationLevel");
					String url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+customer+"&registrationLevel="+registrationLevel;
					
					if(registrationLevel!=null && registrationLevel.equals("C"))
						url				=	"ETCustomerRegistrationEnterIdC.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation",operation)); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
					
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	       } 
           else
           {  
			   // Logger.info(FILE_NAME,"Hai this is at line no 232");
	            //session.setAttribute("Operation",operation);
				String customer			=	request.getParameter("Customer");
				String registrationLevel=	request.getParameter("registrationLevel");
				String url				=	"ETCustomerRegistrationEnterId.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

				if(registrationLevel!=null && registrationLevel.equals("C"))
					url				=	"ETCustomerRegistrationEnterIdC.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

				errorMessage 			=	"You are not allowed to delete this CustomerId :"+CustomerRegObj.getCustomerId()+ ", this Id is under usage";
				
				errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
           }
		}
	  else if(operation.equalsIgnoreCase("Upgrade"))
	  {
	      // on successful CustomerStatus upgradation
	     
	     customerType  =  request.getParameter("registered");
	     CustomerRegObj.setRegistered(customerType);
	     if(remote.upgradeCustomerStatus(CustomerRegObj))
	      {

//			remote.setCustomerValidation(customerId,terminalId);
			errorMessage 			= 	"Customer "+  customerId  + " successfully Upgraded  to Registered Customer ";
			String customer			=	request.getParameter("Customer");
			String registrationLevel=	request.getParameter("registrationLevel");
			String url				=	"ETCustomerRegistrationEnterId.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;
			
			if(registrationLevel!=null && registrationLevel.equals("C"))
					url				=	"ETCustomerRegistrationEnterIdC.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

			errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
			keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			
%>
      	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%		}
    
	  }
		//Logger.info(FILE_NAME,"Hai this is sivaram at line 265 of ERCustomerRegistrationProcess.jsp:::::::::::::::::");
	}
	catch(Exception e)
	{
			e.printStackTrace();
			
			errorMessage 			=	"You are not allowed to "+operation+" this CustomerId :"+CustomerRegObj.getCustomerId()+ ", this Id is under usage";
			String customer			=	request.getParameter("Customer");
			String registrationLevel=	request.getParameter("registrationLevel");
			String url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+customer+"&registrationLevel="+registrationLevel;
			
			if(registrationLevel!=null && registrationLevel.equals("C"))
					url				=	"ETCustomerRegistrationEnterIdC.jsp?Customer="+customer+"&registrationLevel="+registrationLevel;

			errorMessageObject      =  new ErrorMessage(errorMessage,url); 
		 		
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	} 

}
}
//Logger.info(FILE_NAME,"Exiting the Process Page");
logger.info(FILE_NAME+"Exiting the Process Page");
%>
					


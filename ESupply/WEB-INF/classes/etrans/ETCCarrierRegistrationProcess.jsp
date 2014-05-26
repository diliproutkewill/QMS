<%--
	Program Name    : ETCCarrierRegistrationProcess.jsp
    Module Name     : ETrans
	Task            : CarrierRegistration
	Sub Task        : Modify / Delete
	Author Name     : Giridhar Manda
	Date Started    : September 12,2001
    Date Completed  : September 12,2001
	Date Modified   :
	Description     :
					This file gets the values form ETCCarrierRegistrationView.jsp which are used in Modifying or Deleting a particular record.
					This method interacts with SetUpSessionBean to call the method 
					updateCarrierDetail( CarrierDetail CarrierRegObj, Address AddressObj, ESupplyGlobalParameters loginbean) to update the 
					the values in the tables FS_FR_CAMASTER and FS_ADDRESS or calls 
					deleteCarrierDetail( CarrierDetail CarrierRegObj, Address AddressObj, ESupplyGlobalParameters loginbean) to Deleta a record
					from the tables FS_FR_CAMASTER and FS_ADDRESS.
  
 
--%>
<%@ page import = "java.sql.Connection,
				   java.sql.Statement,
				   java.sql.ResultSet,
				   javax.sql.DataSource,
				   javax.naming.InitialContext,
				   java.util.Vector,
				   com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList" %>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<jsp:useBean id = "CarrierRegObj" class = "com.foursoft.etrans.setup.carrier.bean.CarrierDetail" scope = "request"/>
<jsp:setProperty name = "CarrierRegObj" property = "*"/>

<jsp:useBean id = "AddressObj" class = "com.foursoft.etrans.common.bean.Address" scope = "request"/>
<jsp:setProperty name = "AddressObj" property = "*"/>

<%
	String							carrierId		= null; // to store carrierId
	String							strParam		= null; // to store strpram
	String							errorMessage	= null; // to store errormessage
	String							shipmentMode	= "Air";
	String							chkCarrierNumber= null;
	InitialContext					initial			= null;
	SetUpSessionHome	home 			= null;
	SetUpSession 		remote			= null;
	
	ErrorMessage errorMessageObject = null;
	ArrayList	 keyValueList	    = new ArrayList();
	
	try{
		strParam		= request.getParameter("Operation");	
		shipmentMode	= request.getParameter("shipmentMode");	
		carrierId								= CarrierRegObj.getCarrierId();
		chkCarrierNumber = request.getParameter("carrierNumber")!=null?request.getParameter("carrierNumber"):"N";
		CarrierRegObj.setCarrierNumber(chkCarrierNumber);
		initial			= new InitialContext();
		home 			= (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		remote 			= (SetUpSession)home.create();

	if(strParam.equals("Add"))
	 {
	  if(remote.isCarrierExists(carrierId,shipmentMode))		
		{    
			 
			 errorMessageObject = new ErrorMessage("Record already exists with CarrierId : "+ carrierId,"ETCCarrierRegistrationAdd.jsp"); 
		 	 
			 keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
			 keyValueList.add(new KeyValue("Operation","Add")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 		 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		 }
		 else 
		 {  
			
            if(remote.setCarrierDetail(CarrierRegObj,AddressObj,loginbean))
            {	 			
			     
				 errorMessageObject = new ErrorMessage("Record successfully added with CarrierId : "+ carrierId,"ETCCarrierRegistrationAdd.jsp"); 
		 	 
				 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject);
				 
				
%>
		         <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		 
 	        }
			
		}	
	 }
	if(strParam.equals("Modify"))
	{
			chkCarrierNumber =request.getParameter("carrierNumber");
			if(chkCarrierNumber !=null && chkCarrierNumber.equals("on"))
			{
			
				chkCarrierNumber ="Y" ;
			}
			else
			{
			    chkCarrierNumber ="N";
			}
			CarrierRegObj.setCarrierNumber(chkCarrierNumber);
			CarrierRegObj.setNumericCode(request.getParameter("numericCode")!=null?request.getParameter("numericCode"):"");
			int result =remote.updateCarrierDetail(CarrierRegObj,AddressObj,loginbean,shipmentMode);
		    errorMessageObject = new ErrorMessage("Record successfully modified with Carrier Id : " +  CarrierRegObj.getCarrierId(),"ETCCarrierRegistrationEnterId.jsp"); 
	 	 
		    keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			keyValueList.add(new KeyValue("Operation","Modify")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
	else if(strParam.equals("Delete"))
	{       
			carrierId	=	CarrierRegObj.getCarrierId();
	    	if(remote.deleteCarrierDetail(CarrierRegObj,AddressObj,loginbean,shipmentMode))
	    	{	
			    
				errorMessageObject = new ErrorMessage("Record successfully deleted with Carrier Id : "+ CarrierRegObj.getCarrierId(),"ETCCarrierRegistrationEnterId.jsp"); 
	 	 
				keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
				keyValueList.add(new KeyValue("Operation","Delete")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	        }
			else
			{	
			    
				errorMessageObject = new ErrorMessage("CarrierId "+CarrierRegObj.getCarrierId()+" cannot be deleted as it is in use.","ETCCarrierRegistrationEnterId.jsp"); 
	 	 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Delete")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	        }
	}
	else if(strParam.equals("View"))
   	{
%>
		<h2> FourSoft 4S-ESupply </H2>
<%	
	}
	else if(strParam.equals("Invalidate"))
	{       
		 java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
		   String invalidater[]=request.getParameterValues("checkBoxValue");
		   boolean flag=false;
		   for(int i=0;i<dobList.size();++i)
		 {
			   if(invalidater[i].equalsIgnoreCase("false"))
					invalidater[i]="F";
			   else if(invalidater[i].equalsIgnoreCase("true"))
					invalidater[i]="T";
			com.foursoft.etrans.setup.carrier.bean.CarrierDetail carrierDOB=(			com.foursoft.etrans.setup.carrier.bean.CarrierDetail)dobList.get(i);
			carrierDOB.setInvalidate(invalidater[i]);
		 }
		 			flag=remote.invalidateCarrierMaster(dobList);

			
	    	if(flag)
	    	{	
			    
				errorMessageObject = new ErrorMessage("Record successfully Validated: ","../Invalidate.jsp?View=CarrierRegistration"); 
	 	 
				keyValueList.add(new KeyValue("ErrorCode","RSV")); 	
				keyValueList.add(new KeyValue("Operation","Invalidate")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	       }
         }
 	}
 	catch(Exception exp)
 	{
 		 errorMessageObject = new ErrorMessage("Error while "+strParam+"ing the record with Carrier Id : "+ CarrierRegObj.getCarrierId(),"ETCCarrierRegistrationEnterId.jsp"); 
	 	 
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 keyValueList.add(new KeyValue("Operation",strParam)); 	
		 errorMessageObject.setKeyValueList(keyValueList);
		 request.setAttribute("ErrorMessage",errorMessageObject);
		 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
	finally
	{
		initial	=	null;
		home	=	null;
		remote	=	null;	
	}
%>	
	


<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/**
	Program Name	: ETCCodeCustomisationProcess.jsp
	Module Name		: ETrans
	Task			    : Code_Customization	
	Sub Task		  : Process	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 11,2001
	Date Completed	: September 11,2001
	Date Modified	: 
	Description		:
		This file gets the values form ETCCodeCustomisationView.jsp which are used in Modifying a particular record.
		This method interacts with CodeCustomisationSessionBean to call the method 
		updateCodeCustomisationDetails( CodeCustomiseJSPBean CodeCustomisationJSPBean, ESupplyGlobalParameters loginbean) to update the 
		the values in the tables FS_FR_CONFIGPARAM.
	Method Summary  :
			
*/
%>
<%@ page import="javax.naming.InitialContext,
         org.apache.log4j.Logger,
				 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				 com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean,
				 com.foursoft.esupply.common.java.ErrorMessage,
			     com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="CodeCustomisationJSPBean" class="com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean" scope="request"/>
<jsp:setProperty name="CodeCustomisationJSPBean" property="*"/>

<%!
  private static Logger logger = null;
%>

<%    
    String      FILE_NAME   = "ETCCodeCustomisationProcess.jsp";  
    logger  = Logger.getLogger(FILE_NAME);
    //Logger.info(FILE_NAME,"00000: ");      
    logger.info(FILE_NAME+"00000: ");      
		String 	    Operation 	=	null;
		boolean			flag		    =	true;   //Variable that represents wether data is inserted or not
		boolean			recordExist	= false;  //Variable to store the boolean value of recordExisted or not
		ArrayList   codeName	  =	new ArrayList();   //Array Variable get all the codetypes 

		ErrorMessage errorMessageObject = null;
		ArrayList	   keyValueList	      = new ArrayList();
		String temp	=""; //new
    String[]     valGrp             = new String[3];
    String[]     valIndctrs         = new String[3]; 
    String[]     valLengths         = new String[3]; 

     int          noGrps            = 0;
     int[]       valuesLen          = new int[3];
    
try 
{
	Operation = request.getParameter("Operation");	
//Logger.info(FILE_NAME,"111111: "+Operation);     
	InitialContext					            initial			            =	new InitialContext();   //Variable represents the initial context for JNDI loop up process
  SetUpSessionHome 	ETransHOSuperUserHome	  =	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
	SetUpSession 		  ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
  noGrps      =   CodeCustomisationJSPBean.getNoOfGrps();        
//Logger.info(FILE_NAME,"222222: "+noGrps);   
  valGrp      =   request.getParameterValues("valGrp");
  valIndctrs  =   request.getParameterValues("valInd");  
  valLengths  =   request.getParameterValues("valLen");  
  
//Logger.info(FILE_NAME,"valLengths: "+valLengths);  
  if(valLengths != null)
  {
      for(int i=0;i<valLengths.length;i++)
      {
        valuesLen[i] = Integer.parseInt(valLengths[i]);
        //Logger.info(FILE_NAME,"valLengths: "+valLengths[i]);
      }

   }

      
      CodeCustomisationJSPBean.setValGrps(valGrp);  
      CodeCustomisationJSPBean.setValInds(valIndctrs);  
      CodeCustomisationJSPBean.setValLens(valuesLen);
      
//Logger.info(FILE_NAME,"333333: "); 

  if(Operation.equalsIgnoreCase("Modify"))
	{

//Logger.info(FILE_NAME,"CodeIdName: "+CodeCustomisationJSPBean.getValDesc1());      
//Logger.info(FILE_NAME,"ShipmentMode: "+CodeCustomisationJSPBean.getValDesc2());      

		int rowsUpdated = ETransHOSuperUserRemote.updateCodeCustomisationDetails(CodeCustomisationJSPBean,loginbean);	// To update the database with Current Data



//Logger.info(FILE_NAME,"ModifiedRecords: "+rowsUpdated);
    
		errorMessageObject = new ErrorMessage("Record With CodeId Name '" + CodeCustomisationJSPBean.getCodeIdName() + "' is Successfully Modified","ETCCodeCustomisationEnterId.jsp"); 
	  keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
	  keyValueList.add(new KeyValue("Operation","Modify")); 	
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject);
		
		
%>
		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
	else if(Operation.equalsIgnoreCase("Add"))
	{
		//Logger.info(FILE_NAME,"Desc1: "+CodeCustomisationJSPBean.getValDesc1());    
		//Logger.info(FILE_NAME,"Desc2: "+CodeCustomisationJSPBean.getValDesc2());      

		// @@ Suneetha replaced on 20050525
		//String codeType 	= CodeCustomisationJSPBean.getCodeIdName();
		//codeName	=	ETransHOSuperUserRemote.getCodeCustomizationCodeType(Operation,CodeCustomisationJSPBean.getShipmentMode(),CodeCustomisationJSPBean.getTerminalId(),temp); //new added temp	// To get the all the CodeTypes Existing in the database.

		CodeCustomisationJSPBean.setCodeIdName(request.getParameter("codeId"));
		String codeType 	= CodeCustomisationJSPBean.getCodeIdName();
		codeName	=	ETransHOSuperUserRemote.getCodeCustomizationCodeType(Operation,CodeCustomisationJSPBean.getShipmentMode(), CodeCustomisationJSPBean.getTerminalId(),CodeCustomisationJSPBean.getCodeIdName()); //new added temp	// To get the all the CodeTypes Existing in the database.
		recordExist	=	true;

		// @@ 20050525

		for( int i =0;i<codeName.size(); i++)	//loop for checking the Present Id with the Ids from DataBase.
		{
			if(codeName.get(i).equals(codeType))
			{
				// @@ Suneetha replaced on 20050525
				// recordExist	=	true;
				recordExist	=	false;
				// @@ 20050525
				break;
			}
		}
        // if1 begin
		if(recordExist)	// To forward page saying that "Record already existing " , If record found in data base with same Id.
		{	
		  // @@ Suneetha replaced on 20050525	
		  // errorMessageObject = new ErrorMessage( "Record Already Exists with CodeId Name '"+ codeType + "'","ETCCodeCustomisationAdd.jsp");
  		  errorMessageObject = new ErrorMessage( "Record Already Exists (or) An Invalid CodeId Name '"+ codeType + "'","ETCCodeCustomisationAdd.jsp");
		  // @@ 20050525
	      keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
	      keyValueList.add(new KeyValue("Operation","Add")); 	
	      errorMessageObject.setKeyValueList(keyValueList);
	      request.setAttribute("ErrorMessage",errorMessageObject);
			
		
%>
		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
		else
		{
			flag	=	ETransHOSuperUserRemote.addCodeCustomisationDetails(CodeCustomisationJSPBean,loginbean); //To Insert the data into database;
//Logger.info(FILE_NAME,"Flag:  "+flag);      
			if(flag) 
			{
				if(CodeCustomisationJSPBean.getCodeIdName().equals("HOUSEDOCUMENT") || CodeCustomisationJSPBean.getCodeIdName().equals("HBL") || CodeCustomisationJSPBean.getCodeIdName().equals("CONSIGNMENTNOTE") && CodeCustomisationJSPBean.getCustFlag().equals("D"))
          errorMessageObject = new ErrorMessage( "Code Customisation has been Completed  with prq details for CodeId'" +CodeCustomisationJSPBean.getCodeIdName() +"'" ,"ETCCodeCustomisationAdd.jsp"); 
        else        
          errorMessageObject = new ErrorMessage( "Code Customisation has been Completed for CodeId '" +CodeCustomisationJSPBean.getCodeIdName() +"'","ETCCodeCustomisationAdd.jsp"); 
			   keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
			   keyValueList.add(new KeyValue("Operation","Add")); 	
	           errorMessageObject.setKeyValueList(keyValueList);
	           request.setAttribute("ErrorMessage",errorMessageObject);
				
				/**
				String errorMessage = "Code Customisation has been Completed for CodeId '" +CodeCustomisationJSPBean.getCodeIdName() +"'";
				session.setAttribute("ErrorCode","RSI");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCCodeCustomisationAdd.jsp");
				session.setAttribute("Operation","Add");  */
%>
				<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}
			else	// To forward page saying that "Error while adding data".
			{
				
			   errorMessageObject = new ErrorMessage( "Invalid CodeType","ETCCodeCustomisationAdd.jsp"); 
			   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			   keyValueList.add(new KeyValue("Operation","Add")); 	
	           errorMessageObject.setKeyValueList(keyValueList);
	           request.setAttribute("ErrorMessage",errorMessageObject);
				
				
			   /**	
				String errorMessage = "Error  while adding the record '" +CodeCustomisationJSPBean.getCodeIdName() +"'";
				session.setAttribute("ErrorCode","ERR");
				session.setAttribute("ErrorMessage",errorMessage);
				session.setAttribute("NextNavigation","ETCCodeCustomisationAdd.jsp");
				session.setAttribute("Operation","Add");   */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%		
			}
	 	 }
 	  }
    }
	catch(Exception e)
	{
    	
		 if(Operation.equals("Add"))
		    errorMessageObject = new ErrorMessage( "Error while "+ Operation +"ing the Data ","ETCCodeCustomisationAdd.jsp"); 
	   else
			errorMessageObject = new ErrorMessage(  "Error while "+ Operation +"ing the Data ","ETCCodeCustomisationEnterId.jsp");              
		   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		   keyValueList.add(new KeyValue("Operation",Operation)); 	
	     errorMessageObject.setKeyValueList(keyValueList);
	     request.setAttribute("ErrorMessage",errorMessageObject);
		
      String errorMessage = "Error while "+ Operation +"ing the Data ";
      session.setAttribute("Operation", "Modify" );
      session.setAttribute("ErrorCode","RSM");
      session.setAttribute("ErrorMessage",errorMessage);
      if(Operation.equals("Add"))
        session.setAttribute("NextNavigation","ETCCodeCustomisationAdd.jsp");	
      else
        session.setAttribute("NextNavigation","ETCCodeCustomisationEnterId.jsp");  
%>
		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>
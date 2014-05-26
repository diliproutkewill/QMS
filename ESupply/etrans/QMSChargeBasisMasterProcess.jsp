<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargeBasisMaster.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : Adding/View/Modify/Delete chargebasis
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" chargebasis information
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import = "com.qms.setup.java.ChargeBasisMasterDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger ,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  java.util.Hashtable"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSChargeBasisMasterProcess.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String[]    chargebasisid	=	request.getParameterValues("chargebasisid");
	String[]    chargebasisdesc	=	request.getParameterValues("chargebasisdesc");
	String[]    block			=	request.getParameterValues("block");
	String[]    primary			=	request.getParameterValues("primary");
	String[]    secondary		=	request.getParameterValues("secondary"); 
	String[]    tertiary		=	request.getParameterValues("tertiary");
	//String[]    calculation		=	request.getParameterValues("calculation");
	SetUpSessionHome 	home  		= 	null;    // variable to store Home Object
	SetUpSession      	remote		= 	null;    // variable to store Remote Object
	InitialContext		inital		=	null;
	String		msg					=	"";
	boolean     success				=	true;
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	StringBuffer	errorMessage	=	new StringBuffer();
	String		url					=	null;
	ChargeBasisMasterDOB	chargeBasisMasterDOB	=	null;
	ArrayList	chargeBasisList		=	new ArrayList();
	ArrayList   returnList		=	null;
	ArrayList   insertedList	=	null;
	ArrayList   invalidList		=	null;
	ArrayList   chargeList      =   null;


	try{

		if(operation!=null && operation.equals("Add"))
		{
				if(chargebasisid!=null && chargebasisid.length>0)
					{
						for(int i=0;i<chargebasisid.length;i++)
						{
							chargeBasisMasterDOB	=	new ChargeBasisMasterDOB();
							chargeBasisMasterDOB.setChargeBasis((chargebasisid[i]!=null)?chargebasisid[i]:"");
							chargeBasisMasterDOB.setChargeDesc((chargebasisdesc[i]!=null)?chargebasisdesc[i]:"");
							chargeBasisMasterDOB.setBlock((block[i]!=null)?block[i]:"");
							chargeBasisMasterDOB.setPrimaryBasis((primary[i]!=null)?primary[i]:"");
							chargeBasisMasterDOB.setSecondaryBasis((secondary[i]!=null)?secondary[i]:"");
							chargeBasisMasterDOB.setTertiaryBasis((tertiary[i]!=null)?tertiary[i]:"");
							//chargeBasisMasterDOB.setCalculation((calculation[i]!=null)?calculation[i]:"");
							chargeBasisList.add(chargeBasisMasterDOB);
						}
					}
		}else if(operation!=null && (operation.equals("Modify") || operation.equals("Delete")))
		{
				if(chargebasisid!=null && chargebasisid.length>0)
					{
							chargeBasisMasterDOB	=	new ChargeBasisMasterDOB();													chargeBasisMasterDOB.setChargeBasis((chargebasisid[0]!=null)?chargebasisid[0]:"");
							chargeBasisMasterDOB.setChargeDesc((chargebasisdesc[0]!=null)?chargebasisdesc[0]:"");
							chargeBasisMasterDOB.setBlock((block[0]!=null)?block[0]:"");
							chargeBasisMasterDOB.setPrimaryBasis((primary[0]!=null)?primary[0]:"");
							chargeBasisMasterDOB.setSecondaryBasis((secondary[0]!=null)?secondary[0]:"");
							chargeBasisMasterDOB.setTertiaryBasis((tertiary[0]!=null)?tertiary[0]:"");
							//chargeBasisMasterDOB.setCalculation((calculation[0]!=null)?calculation[0]:"");
					}
		}else if(operation!=null && operation.equals("Invalidate")){
			chargeList   =  (ArrayList)session.getAttribute("chargeList");
			session.removeAttribute("chargeList");
			for(int i=0;i<chargeList.size();i++)
			{  
				chargeBasisMasterDOB	=	(ChargeBasisMasterDOB)chargeList.get(i);
				if(request.getParameter("invalidate"+i)!=null){
				    chargeBasisMasterDOB.setInvalidate("T");
				}
				else
					chargeBasisMasterDOB.setInvalidate("F");

			}
		}
	}catch(Exception e)
	{
		success	=	false;
		msg		=	"Exception while reading the data";
		//Logger.error(FILE_NAME,"Exception while reading the data"+e.toString());
    logger.error(FILE_NAME+"Exception while reading the data"+e.toString());
	}

	try{

		if(success)
		{
			inital		=	new InitialContext();
			home		=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");		
			remote		=	(SetUpSession)home.create();
			if(operation!=null && operation.equals("Add"))
			{
				if(chargeBasisList!=null && chargeBasisList.size()>0)
				{
					returnList	=	remote.insertChargesBasisDtls(chargeBasisList,loginbean);
				}
			}else if(operation!=null && operation.equals("Modify"))
			{
					if(chargeBasisMasterDOB!=null)
					{
						success	=	remote.updateChargesBasisDetails(chargeBasisMasterDOB,loginbean);
					}
			}else if(operation!=null && operation.equals("Delete"))
			{
					if(chargeBasisMasterDOB!=null)
					{
						success	=	remote.deleteChargesBasisDtls(chargeBasisMasterDOB,loginbean);
					}				
			}
			else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
			{
					if(chargeList!=null)
					{
						remote.invalidateChargeBasisId(chargeList);
					}				
			}
		}
	}catch(Exception e)
	{
		success	=	false;
		//Logger.error(FILE_NAME,"Exception while remote call to bean"+e.toString());
    logger.error(FILE_NAME+"Exception while remote call to bean"+e.toString());
	}
	try{
		if(success)
		{
			if(operation!=null && operation.equals("Add"))
				{
							insertedList	=	(ArrayList)returnList.get(0);
							invalidList     =   (ArrayList)returnList.get(1);
							if(insertedList.size()>0)
							{
								errorMessage.append("ChargeBasis added successfully :\n");
								for(int i=0;i<insertedList.size();i++)
								{
									chargeBasisMasterDOB	= (ChargeBasisMasterDOB)insertedList.get(i);
									errorMessage.append("ChargeBasisId:");
									errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
									errorMessage.append("\n");
								}
							}
							if(invalidList.size()>0)
							{
								errorMessage.append("Charge Group(s) are already defined/Invalid data :\n");
								for(int i=0;i<invalidList.size();i++)
								{
									chargeBasisMasterDOB	= (ChargeBasisMasterDOB)invalidList.get(i);
									errorMessage.append("ChargeBasisId:");
									errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
									errorMessage.append("\n");
								}
							}
					url						=	"QMSChargeBasisMaster.jsp?Operation=Add";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
					keyValueList.add(new KeyValue("Operation","Add")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage.append("Record Successfully Modified with Charge Basis Id:");
					errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage.append("Record Successfully Deleted with Charge Basis Id :");
					errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
				{
					errorMessage.append("The Selected Charge Ids Have Been Successfully Invalidated");
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-Charges";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				
		}else
		{
			if(operation!=null && operation.equals("Add"))
				{
					errorMessage.append("Exeption while Inserting the records :");
					url						=	"QMSChargeBasisMaster.jsp?Operation=Add";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Add")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage.append("Exeption while Modifing chargeBasisId :");
					errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage.append("Exeption while Deleting chargeBasisId :");
					errorMessage.append(chargeBasisMasterDOB.getChargeBasis());
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
				{
					errorMessage.append("Exception while invalidate ChargeBasisId's:");
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-Charges";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
		}
%>
						<jsp:forward page="../QMSESupplyErrorPage.jsp" />
<%
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while forwarding to error page"+e);
    logger.error(FILE_NAME+"Exception while forwarding to error page"+e);
	}
%>
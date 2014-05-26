<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargesMasterProcess.jsp
Product Name	: QMS
Module Name		: Charges master
Task		    : Adding/View/Modify/Delete ChargesMaster
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" ChargesMaster information
Actor           :
Related Document: CR_DHLQMS_1005
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%@page import ="com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  com.qms.setup.java.ChargesMasterDOB,
				  com.foursoft.esupply.common.java.FoursoftConfig"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSChargesMasterProcess.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String[]	chargeId		=	request.getParameterValues("chargeid");
	String[]	chargeDesc		=	request.getParameterValues("chargedesc");
	String[]	airCheckbox		=	request.getParameterValues("aircheckbox");
	String[]	seaCheckbox		=	request.getParameterValues("seacheckbox");
	String[]	truckCheckbox	=	request.getParameterValues("truckcheckbox");
	String[]	costIncurred	=	request.getParameterValues("costincurr");

	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	StringBuffer errorMessage		=	new StringBuffer("");
	String		url					=	null;

	ArrayList	dataList		=	new ArrayList();
	ArrayList   returnList		=	null;
	ArrayList   insertedList	=	null;
	ArrayList   invalidList		=	null;
	ChargesMasterDOB chargesMasterDOB	=null;
	SetUpSessionHome	home	=	null;
	SetUpSession		remote	=	null;
	boolean			success		=	true;
	int[]		shipMode	=	new int[chargeId.length];
	String			invalidate	=	"";
	ArrayList chargeList   =  null;


	try{
			if(chargeId!=null && chargeId.length>0)
			{
				for(int i=0;i<chargeId.length;i++)
				{
					if(request.getParameter("aircheckbox"+(i+1))!=null)
					{
						if(request.getParameter("seacheckbox"+(i+1))!=null)
						{
							if(request.getParameter("truckcheckbox"+(i+1))!=null)
							{
								shipMode[i]	=	7;
							}else
							{
								shipMode[i]	=	3;
							}
						}else if(request.getParameter("truckcheckbox"+(i+1))!=null)
						{
							shipMode[i]	=	5;
						}else
						{
							shipMode[i]	=	1;
						}
					}else if(request.getParameter("seacheckbox"+(i+1))!=null)
					{
						if(request.getParameter("truckcheckbox"+(i+1))!=null)
						{
							shipMode[i]	=	6;
						}else
						{
							shipMode[i]	=	2;
						}
					}else if(request.getParameter("truckcheckbox"+(i+1))!=null)
					{
						shipMode[i]	=	4;
					}
				}
			}

		if(operation!=null && operation.equals("Add"))
		{
			if(chargeId!=null && chargeId.length>0)
			{
				for(int i=0;i<chargeId.length;i++)
				{
					chargesMasterDOB	=	new ChargesMasterDOB();
					chargesMasterDOB.setChargeId(chargeId[i].trim()!=null?chargeId[i].trim():"");
					chargesMasterDOB.setChargeDesc((chargeDesc[i]!=null)?chargeDesc[i]:"");
					chargesMasterDOB.setShipmentMode(shipMode[i]);
					chargesMasterDOB.setCostIncurr(costIncurred[i]);
					dataList.add(chargesMasterDOB);
				}
			}
		}else if(operation!=null && (operation.equals("Modify") || operation.equals("Delete")))
		{
				if(chargeId!=null && chargeId.length>0)
					{
						chargesMasterDOB	=	new ChargesMasterDOB();
						chargesMasterDOB.setChargeId(chargeId[0].trim()!=null?chargeId[0].trim():"");
						chargesMasterDOB.setChargeDesc((chargeDesc[0]!=null)?chargeDesc[0]:"");
						chargesMasterDOB.setShipmentMode(shipMode[0]);
						chargesMasterDOB.setCostIncurr(costIncurred[0]);
					}
		}else if(operation!=null && operation.equals("Invalidate")){
			chargeList   =  (ArrayList)session.getAttribute("chargeList");
			session.removeAttribute("chargeList");
			//System.out.println("chargeList"+chargeList);
			String[] checkBoxes	=	request.getParameterValues("invalidate");
			/*for(int i=0;i<checkBoxes.length;i++)
			{
				System.out.println("chargeList   "+checkBoxes[i]);
			}*/
			for(int i=0;i<chargeList.size();i++)
			{  
				chargesMasterDOB	=	(ChargesMasterDOB)chargeList.get(i);
				if(request.getParameter("invalidate"+i)!=null && "on".equalsIgnoreCase(request.getParameter("invalidate"+i)))
				    chargesMasterDOB.setInvalidate("T");
				else
					chargesMasterDOB.setInvalidate("F");
			}
		}
		

		home	=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		remote	=	(SetUpSession)home.create();
		if(operation!=null && operation.equals("Add"))
		{
			if(dataList!=null && dataList.size()>0)
			{
				returnList	=	remote.insertChargesMasterDtls(dataList,loginbean);
				if(returnList!=null && returnList.size()==2)
				{
					insertedList	=	(ArrayList)returnList.get(0);
					invalidList     =   (ArrayList)returnList.get(1);
				}
				success		=	true;
			}
		}else if(operation!=null && operation.equals("Modify"))
		{
			if(chargesMasterDOB!=null)
			{
				success	=	remote.updateChargesMasterDetails(chargesMasterDOB,loginbean);
			}
		}else if(operation!=null && operation.equals("Delete"))
		{
			if(chargesMasterDOB!=null)
			{
				success	=	remote.deleteChargesMasterDtls(chargesMasterDOB,loginbean);
			}
		}else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
			{
					if(chargeList!=null)
					{
						remote.invalidateChargeMasterId(chargeList);
					}				
			}
	}catch(Exception e)
	{
		success	= false;
		e.printStackTrace();
        //Logger.error(FILE_NAME,"Exception while processing the data --->",e.toString());
        logger.error(FILE_NAME+"Exception while processing the data --->"+e.toString());
	}
	try{
		if(success)
		{
				if(operation!=null && operation.equals("Add"))
				{
							if(insertedList.size()>0)
							{
								errorMessage.append("Charge(s) added successfully :\n");
								for(int i=0;i<insertedList.size();i++)
								{
									chargesMasterDOB	= (ChargesMasterDOB)insertedList.get(i);
									errorMessage.append("ChargeId:");
									errorMessage.append(chargesMasterDOB.getChargeId());
									errorMessage.append("\t | \tCostIncurred:");
									errorMessage.append(chargesMasterDOB.getCostIncurr());
									errorMessage.append("\n");
								}
							}
							if(invalidList.size()>0)
							{
								errorMessage.append("ChargeIds are already defined/Invalid data :\n");
								for(int i=0;i<invalidList.size();i++)
								{
									chargesMasterDOB	= (ChargesMasterDOB)invalidList.get(i);
									errorMessage.append("ChargeId:");
									errorMessage.append(chargesMasterDOB.getChargeId());
									errorMessage.append("| \tCostIncurred:");
									errorMessage.append(chargesMasterDOB.getCostIncurr());
									errorMessage.append("\n");
								}
							}
							url						=	"QMSChargesMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
							keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage.append("The Charge was modified successfully:");
					errorMessage.append(((chargeId[0]!=null)?chargeId[0]:""));
					errorMessage.append("--");
					errorMessage.append(((costIncurred[0]!=null)?costIncurred[0]:""));
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage.append("The Charge was deleted successfully:");
					errorMessage.append(((chargeId[0]!=null)?chargeId[0]:""));
					errorMessage.append("--");
					errorMessage.append(((costIncurred[0]!=null)?costIncurred[0]:""));
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
				{
					errorMessage.append("The Charge Id's are invalidated successfully");
					url						=	"QMSChargesMaster.jsp?Operation=Invalidate";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
			}else{
				if(operation!=null && operation.equals("Add"))
				{
							errorMessage.append("Exception while Adding the records :");
							url						=	"QMSChargesMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
							keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage.append("Exeption while Modifying ChargeId :");
					errorMessage.append(((chargeId[0]!=null)?chargeId[0]:""));
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage.append("Exeption while Deleting ChargeId :");
					errorMessage.append(((chargeId[0]!=null)?chargeId[0]:""));
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
				{
					errorMessage.append("Exception while invalidate ChargeId's:");
					url						=	"QMSChargesMaster.jsp?Operation=Invalidate";
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

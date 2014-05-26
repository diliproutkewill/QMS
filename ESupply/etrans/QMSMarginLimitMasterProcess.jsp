
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistrationProcess.jsp
Product Name	: QMS
Module Name		: Industry Registration
Task		    : Adding/View/Modify/Delete/Invalidate Industry
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete/Invalidate" Industry information
Actor           :
Related Document: CR_DHLQMS_1002
--%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%@page import ="com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  com.qms.setup.java.MarginLimitMasterDOB,
				  com.foursoft.esupply.common.java.FoursoftConfig"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSMarginLimitMasterProcess.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String		shipmentMode	=	request.getParameter("shipmentmode");
	String		consoletype		=	(request.getParameter("consoletype")!=null)?request.getParameter("consoletype"):"";
	String[]    marginId		=	request.getParameterValues("marginId");
	String[]	levelId			=	request.getParameterValues("levelId");
	String[]	serviceLevelId	=	request.getParameterValues("serviceLevelId");
	String[]	maxDiscount		=	request.getParameterValues("maxDiscount");
	String[]	minMargin		=	request.getParameterValues("minMargin");
	String      chargeType      =   request.getParameter("ChargeType");
	//Logger.info("Line No is 45::",chargeType);
	int			shipmentType	=	0;
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	"";
	String		url					=	null;

	ArrayList	dataList		=	new ArrayList();
	ArrayList   returnList		=	null;
	ArrayList   insertedList	=	null;
	ArrayList   invalidList		=	null;
	ArrayList	marginList		=	null;
	MarginLimitMasterDOB marginLimitMasterDOB	=null;
	MarginLimitMasterDOB	marginMasterDOB		= null;
	SetUpSessionHome	home	=	null;
	SetUpSession		remote	=	null;
	boolean				success	=	true;
	String shipMode				=	"";
	String			invalidate	=	"";

	try{
		if(shipmentMode!=null)
		{
			if(shipmentMode.equals("1"))
			{
				shipMode		=	"Air";
				shipmentType	=	FoursoftConfig.AIR;
			}else if(shipmentMode.equals("2"))
			{
				shipMode	=	"Sea";
				if(consoletype!=null && consoletype.equals("LCL"))
				{
					shipmentType	=	FoursoftConfig.SEA_LCL;
				}else if(consoletype!=null && consoletype.equals("FCL"))
				{
					shipmentType	=	FoursoftConfig.SEA_FCL;
				}
			}else if(shipmentMode.equals("4"))
			{
				shipMode	=	"Truck";
				if(consoletype!=null && consoletype.equals("LTL"))
				{
					shipmentType	=	FoursoftConfig.TRUCK_LTL;
				}else if(consoletype!=null && consoletype.equals("FTL"))
				{
					shipmentType	=	FoursoftConfig.TRUCK_FTL;
				}
			}
		}
		if(operation!=null && operation.equals("Add"))
		{
			if(levelId!=null && levelId.length>0)
			{
				for(int i=0;i<levelId.length;i++)
				{
					marginLimitMasterDOB	=	new MarginLimitMasterDOB();
					marginLimitMasterDOB.setMarginId(Integer.toString(shipmentType));
					marginLimitMasterDOB.setLevelId((levelId[i]!=null)?levelId[i]:"");
					if(serviceLevelId!=null)
					marginLimitMasterDOB.setServiceLevel((serviceLevelId[i]!=null)?serviceLevelId[i]:"");
					
					marginLimitMasterDOB.setMaxDiscount((maxDiscount[i]!=null)?maxDiscount[i]:"0.00");
					marginLimitMasterDOB.setMinMargin((minMargin[i]!=null)?minMargin[i]:"0.00");
					marginLimitMasterDOB.setChargeType(chargeType!=null?chargeType.toUpperCase():"");
					//System.out.println("shipmentModeshipmentModeshipmentModeshipmentMode :: "+shipmentMode);
					marginLimitMasterDOB.setShipmentMode(shipmentMode!=null?shipmentMode:"");
					marginLimitMasterDOB.setLoginTerminal(loginbean.getTerminalId());//@@Added by  subrahmanyam for id:203354
					dataList.add(marginLimitMasterDOB);
				}
			}
		}else if(operation!=null && (operation.equals("Modify") || operation.equals("Delete")))
		{
				if(levelId!=null && levelId.length>0)
					{
						marginLimitMasterDOB	=	new MarginLimitMasterDOB();
						marginLimitMasterDOB.setMarginId(Integer.toString(shipmentType));
						marginLimitMasterDOB.setLevelId((levelId[0]!=null)?levelId[0]:"");
						marginLimitMasterDOB.setServiceLevel((serviceLevelId[0]!=null)?serviceLevelId[0]:"");
						marginLimitMasterDOB.setMaxDiscount((maxDiscount[0]!=null)?maxDiscount[0]:"0.00");
						marginLimitMasterDOB.setMinMargin((minMargin[0]!=null)?minMargin[0]:"0.00");
					    marginLimitMasterDOB.setChargeType(chargeType!=null?chargeType.toUpperCase():"");
						marginLimitMasterDOB.setShipmentMode(shipmentMode!=null?shipmentMode:"");
					}
		}else if(operation!=null && operation.equals("Invalidate"))
		{
				marginList = (ArrayList)session.getAttribute("marginList");
				session.removeAttribute("marginList");
				if(marginId!=null && marginId.length>0)
					{
						for(int i=0;i<marginId.length;i++)
						{
							invalidate	=	"invalidate"+i;
							marginLimitMasterDOB	=	new MarginLimitMasterDOB();
							marginLimitMasterDOB.setMarginId((marginId[i]!=null)?marginId[i]:"");
							marginLimitMasterDOB.setLevelId((levelId[i]!=null)?levelId[i]:"");
							marginLimitMasterDOB.setServiceLevel((serviceLevelId[i]!=null)?serviceLevelId[i]:"");
							//marginLimitMasterDOB.setMaxDiscount((maxDiscount[i]!=null)?maxDiscount[i]:"0.00");
							//marginLimitMasterDOB.setMinMargin((minMargin[i]!=null)?minMargin[i]:"0.00");
							marginLimitMasterDOB.setChargeType(request.getParameterValues("chargeType")[i]);
							marginLimitMasterDOB.setShipmentMode(shipmentMode!=null?shipmentMode:"");
							if(request.getParameter(invalidate)!=null)
							{
								marginLimitMasterDOB.setInvalidate("T");
							}else
							{
								marginLimitMasterDOB.setInvalidate("F");
							}
							marginMasterDOB = (MarginLimitMasterDOB)marginList.get(i);
							if(!marginMasterDOB.getInvalidate().equals(marginLimitMasterDOB.getInvalidate()))
								{	dataList.add(marginLimitMasterDOB);}
						}
					}
		}

		home	=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		remote	=	(SetUpSession)home.create();
		if(operation!=null && operation.equals("Add"))
		{
			if(dataList!=null && dataList.size()>0)
			{
				returnList	=	remote.insertMarginListDtls(dataList,loginbean);
				if(returnList!=null && returnList.size()==2)
				{
					insertedList	=	(ArrayList)returnList.get(0);
					invalidList     =   (ArrayList)returnList.get(1);
				}
				success		=	true;
			}
		}else if(operation!=null && operation.equals("Modify"))
		{
			if(marginLimitMasterDOB!=null)
			{
				success	=	remote.updateMarginLimitDetails(marginLimitMasterDOB,loginbean);
			}
		}else if(operation!=null && operation.equals("Delete"))
		{
			if(marginLimitMasterDOB!=null)
			{
				success	=	remote.deleteMariginLimitMasterDtls(marginLimitMasterDOB);
			}
		}else if(operation!=null && operation.equals("Invalidate"))
		{
			if(dataList!=null && dataList.size()>0)
			{
				success	=	remote.invalidateMarginLimitDetails(dataList,loginbean);
			}
		}
	}catch(Exception e)
	{
		success	= false;
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
								errorMessage			=  "Margin Limit(s) added successfully :\n";
								errorMessage			+= "mode:"+shipMode+"	|	ConsoleType:"+consoletype+"\n";
								for(int i=0;i<insertedList.size();i++)
								{
									marginLimitMasterDOB	= (MarginLimitMasterDOB)insertedList.get(i);
									errorMessage			+= "Level:"+marginLimitMasterDOB.getLevelId();
									if(marginLimitMasterDOB.getServiceLevel()!=null)
									{
									errorMessage += "       |  ServiceLevel:"+marginLimitMasterDOB.getServiceLevel();								
								    }
                                    errorMessage +="\n";       
								}
							}
							if(invalidList.size()>0)
							{
								errorMessage			+= "Margin Limits for the given selection are already defined/Invalid data :\n";
								errorMessage			+= "mode:"+shipMode+"	|	ConsoleType:"+consoletype+"\n";
								for(int i=0;i<invalidList.size();i++)
								{
									marginLimitMasterDOB	= (MarginLimitMasterDOB)invalidList.get(i);
									errorMessage			+= "Level:"+marginLimitMasterDOB.getLevelId();	if(marginLimitMasterDOB.getServiceLevel()!=null)
									{
									errorMessage += "	 |   ServiceLevel:"+marginLimitMasterDOB.getServiceLevel();
								    }
                                    errorMessage +="\n";  
								}
							}
							url						=	"QMSMarginLimitMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage,url); 
							keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  "The Margin Limit was modified successfully :\n";
					errorMessage			+= "mode:"+shipMode+"	|	ConsoleType:"+consoletype+"\n";
					errorMessage			+= "Level:"+marginLimitMasterDOB.getLevelId();		
					
					if(marginLimitMasterDOB.getServiceLevel()!=null)
									{
									errorMessage += "	 |   ServiceLevel:"+marginLimitMasterDOB.getServiceLevel();								
								    }
                                    errorMessage +="\n";  

					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage			=	"The Margin Limit was deleted successfully :\n";
					errorMessage			+= "mode:"+shipMode+"	|	ConsoleType:"+consoletype+"\n";
					errorMessage			+= "Level:"+marginLimitMasterDOB.getLevelId();
					
					
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage			=	"Record successfully Updated :";
					url						=	"QMSMarginLimitMasterViewAllEnterId.jsp?Operaion=Invalidate";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate"));
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}

			}else{
				if(operation!=null && operation.equals("Add"))
				{
							errorMessage			=  "Exception while Margin Limit the records :";
							url						=	"QMSMarginLimitMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage,url); 
							keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  "Exeption while Modifying Margin Limit :";
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					//errorMessage			=	"Exeption while Deleting Margin Limit :";
					errorMessage			=	"You are not able to delete this  Margin Limit cause Might be used in the Quotes :";
					url						=	"QMSMarginLimitMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					//keyValueList.add(new KeyValue("ErrorCode","ERR"));
					keyValueList.add(new KeyValue("ErrorCode","MSG"));  	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage			=	"Exception While Updating the details :";
					url						=	"QMSMarginLimitMasterViewAllEnterId.jsp?Operaion=Invalidate";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate"));
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

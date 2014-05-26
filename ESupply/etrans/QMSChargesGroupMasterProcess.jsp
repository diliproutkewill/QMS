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
				  com.qms.setup.java.ChargeGroupingDOB,
				  com.foursoft.esupply.common.java.FoursoftConfig"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSChargesGroupMasterProcess.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String		chargeGroupId	=	request.getParameter("chargegroupid");
	String		airCheckbox		=	request.getParameter("aircheckbox");
	String		seaCheckbox		=	request.getParameter("seacheckbox");
	String		truckCheckbox	=	request.getParameter("truckcheckbox");
	String[]    chargeIds		=	request.getParameterValues("chargeid");
	String[]	chargeDescIds	=	request.getParameterValues("chargedescid");
	String		terminalId		=   request.getParameter("terminalId");
	String		originCountry	=	request.getParameter("originValue");//Added by Anil.k for Enhancement 231214 on 24Jan2011
	String		destination		=	request.getParameter("destValue");//Added by Anil.k for Enhancement 231214 on 24Jan2011
  
 
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	StringBuffer errorMessage		=	new StringBuffer();
	String		url					=	null;

	ArrayList	dataList		=	new ArrayList();
	ArrayList   returnList		=	null;
	ArrayList   insertedList	=	null;
	ArrayList   invalidList		=	null;
	ChargeGroupingDOB chargeGroupingDOB	=null;
	SetUpSessionHome	home	=	null;
	SetUpSession		remote	=	null;
	boolean			success		=	true;
	int		shipMode	=	0;
	String			invalidate	=	"";
	ArrayList       chargeList  =   null;
//@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
	ArrayList		successStatus		= new ArrayList(2);
	int				successCount		=0;
//@@Ended by subrahmanyam for the pbn id: 201931 on 05-04-2010

	try{
			if(terminalId==null || terminalId.equals(""))
			{	terminalId = loginbean.getTerminalId();}

			if(chargeGroupId!=null)
			{
					if(request.getParameter("aircheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("airChecked")) )
					{
						if(request.getParameter("seacheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("seaChecked")))
						{
							if(request.getParameter("truckcheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("truckChecked")))
							{
								shipMode	=	7;
							}else
							{
								shipMode	=	3;
							}
						}else if(request.getParameter("truckcheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("truckChecked")))
						{
							shipMode	=	5;
						}else
						{
							shipMode	=	1;
						}
					}else if(request.getParameter("seacheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("seaChecked")))
					{
						if(request.getParameter("truckcheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("truckChecked")))
						{
							shipMode	=	6;
						}else
						{
							shipMode	=	2;
						}
					}else if(request.getParameter("truckcheckbox")!=null || "checked".equalsIgnoreCase(request.getParameter("truckChecked")))
					{
						shipMode	=	4;
					}
			}


		if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
		{
			if(chargeGroupId!=null)
			{
				 chargeGroupId = chargeGroupId.trim();
				for(int i=0;i<chargeIds.length;i++)
				{
					chargeGroupingDOB	=	new ChargeGroupingDOB();
					//chargeIdsB			=	new StringBuffer();
					chargeGroupingDOB.setChargeGroup(chargeGroupId);
					chargeGroupingDOB.setShipmentMode(shipMode);

					chargeGroupingDOB.setChargeIds(chargeIds[i]);
					chargeGroupingDOB.setChargeDescId(chargeDescIds[i]);
					chargeGroupingDOB.setTerminalId(terminalId);
					chargeGroupingDOB.setOriginCountry(originCountry);//Added by Anil.k for Enhancement 231214 on 24Jan2011
					chargeGroupingDOB.setDestinationCountry(destination);//Added by Anil.k for Enhancement 231214 on 24Jan2011
					dataList.add(chargeGroupingDOB);
				}
			}
		}else if(operation!=null && operation.equals("Invalidate")){
			chargeList   =  (ArrayList)session.getAttribute("chargeList");
			session.removeAttribute("chargeList");
			//System.out.println("chargeList"+chargeList);
			String[] checkBoxes	=	request.getParameterValues("invalidate");
			
			for(int i=0;i<chargeList.size();i++)
			{  
				chargeGroupingDOB	=	(ChargeGroupingDOB)chargeList.get(i);
				if(request.getParameter("invalidate"+i)!=null && "on".equalsIgnoreCase(request.getParameter("invalidate"+i)))
				    chargeGroupingDOB.setInvalidate("T");
				else
					chargeGroupingDOB.setInvalidate("F");
			}
		}
		

		home	=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		remote	=	(SetUpSession)home.create();
		if(operation!=null && operation.equals("Add"))
		{
			if(dataList!=null && dataList.size()>0)
			{
				chargeGroupingDOB=(ChargeGroupingDOB)dataList.get(0);
				success	=	remote.insertChargesGroupDtls(dataList,loginbean);
				if(returnList!=null && returnList.size()==2)
				{
					insertedList	=	(ArrayList)returnList.get(0);
					invalidList     =   (ArrayList)returnList.get(1);
				}
				//success		=	true;
			}
		}else if(operation!=null && operation.equals("Modify"))
		{
			if(chargeGroupingDOB!=null)
			{
				success	=	remote.updateChargesGroupDetails(dataList,loginbean);
			}
		}else if(operation!=null && operation.equals("Delete"))
		{
			if(chargeGroupId!=null)
			{
//@@Commented & Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
				//success	=	remote.deleteChargesGroupDtls(chargeGroupId,loginbean);
				successStatus	=	remote.deleteChargesGroupDtls(chargeGroupId,loginbean);
				success	=((Boolean)successStatus.get(0)).booleanValue();
				successCount =	((Integer)successStatus.get(1)).intValue();
//@@Ended by subrahmanyam for the pbn id: 201931 on 05-04-2010


			}
		}else if(operation!=null && operation.equalsIgnoreCase("Invalidate"))
		{//System.out.println("chargeList3434 "+chargeList);
				if(chargeList!=null)
				{
					remote.invalidateChargeGroupId(chargeList);
					success  = true;
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
							/*if(insertedList.size()>0)
							{
								errorMessage.append("ChargeGroup(s) added successfully :\n");
								for(int i=0;i<insertedList.size();i++)
								{
									chargeGroupingDOB	= (ChargeGroupingDOB)insertedList.get(i);
									errorMessage.append("ChargegroupId:");
									errorMessage.append(chargeGroupingDOB.getChargeGroup());
									errorMessage.append("\n");
								}
							}
							if(invalidList.size()>0)
							{
								errorMessage.append("ChargeGroup(s) are already defined/Invalid data :\n");
								for(int i=0;i<invalidList.size();i++)
								{
									chargeGroupingDOB	= (ChargeGroupingDOB)invalidList.get(i);
									errorMessage.append("ChargegroupId:");
									errorMessage.append(chargeGroupingDOB.getChargeGroup());
									errorMessage.append("\n");
								}
							}*/
							errorMessage.append("ChargeGroup(s) added successfully :");
							errorMessage.append(chargeGroupId);
							url						=	"QMSChargesGroupingMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
							keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage.append("The ChargeGroup was modified successfully :"+chargeGroupId);
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
//@@Commented by subrahmanyam for the pbn id: 201931 on 05-04-2010

					/*
						errorMessage.append("The ChargeGroup was deleted successfully :"+chargeGroupId);
						url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Delete";
						errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
						keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						keyValueList.add(new KeyValue("Operation","Delete")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);

					*/
//@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
					if(successCount==1111)
					{
							errorMessage.append("Quotes were created using  ChargeGroup Id: "+chargeGroupId+" so you cannot delete this one.");
												url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Delete";
						errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
						keyValueList.add(new KeyValue("ErrorCode","INF")); 	
						keyValueList.add(new KeyValue("Operation","Delete")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);

					}
					else
					{
						errorMessage.append("The ChargeGroup was deleted successfully :"+chargeGroupId);
						url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Delete";
						errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
						keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
						keyValueList.add(new KeyValue("Operation","Delete")); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject);
					}
//@@Ended by subrahmanyam for the pbn id: 201931 on 05-04-2010
				}
				else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage.append("The ChargeGroup was Invalidated successfully ");
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-Charges";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
			}else{
				if(operation!=null && operation.equals("Add"))
				{
							errorMessage.append("ChargeGroup are already defined/Invalid data :");
							errorMessage.append(chargeGroupId);
							url						=	"QMSChargesGroupingMaster.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
							keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					//errorMessage.append("Exeption while Modifying ChargeGroupId :");
					errorMessage.append("ChargeGroup are already defined/Invalid data :");
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage.append("Exeption while Deleting ChargeGroupId :");
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage.append("Exeption while Invalidating ChargeGroupId :");
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-Charges";
					errorMessageObject      =  new ErrorMessage(errorMessage.toString(),url); 
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

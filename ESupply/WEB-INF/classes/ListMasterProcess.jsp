
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: ListMasterProcess.jsp
Product Name	: QMS
Module Name		: ListMaster
Task		    : Adding/View/Modify/Delete/Invalidate List
Date started	:19 July 2005 	
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy
Description		: The application "Adding/View/Modify/Delete/Invalidate" ListMaster Information
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
				  com.qms.setup.java.ListMasterDOB,
				  com.foursoft.esupply.common.java.FoursoftConfig"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="ListMasterProcess.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
	String		operation		=	request.getParameter("Operation");
	String		shipmentMode	=	request.getParameter("shipmentMode");
	String		uov				=	request.getParameter("uov");
	String		uom				=	request.getParameter("uow");
		int			shipmentType	=	0;
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	"";
	String		url					=	null;
	ArrayList	airList1=new ArrayList();
	ArrayList	seaList1=new ArrayList();
	ArrayList	truckList1=new ArrayList();
	ArrayList	dataList		=	new ArrayList();
	ArrayList   returnList		=	null;
	ArrayList   insertedList	=	null;
	ArrayList   invalidList		=	null;
	ArrayList	marginList		=	null;
	ListMasterDOB	listMasterDOB= null;
	SetUpSessionHome	home	=	null;
	SetUpSession		remote	=	null;
	boolean				success	=	true;
	boolean				success1=false;
	String shipMode				=	"";
	String			invalidate	=	"";

	try{
		home	=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
		remote	=	(SetUpSession)home.create();

		if(operation.equals("Invalidate"))
		{
			ArrayList dobList=(ArrayList)session.getAttribute("dobList");
			ArrayList airList=(ArrayList)dobList.get(0);
			ArrayList seaList=(ArrayList)dobList.get(1);
			ArrayList truckList=(ArrayList)dobList.get(2);
			 String aircheckBoxValue[]=   request.getParameterValues("aircheckBoxValue");
			 String seacheckBoxValue[]=   request.getParameterValues("seacheckBoxValue");
			 String truckcheckBoxValue[]=   request.getParameterValues("truckcheckBoxValue");
			 //System.out.println("aircheckBoxValue"+aircheckBoxValue);
			// System.out.println("seacheckBoxValue"+seacheckBoxValue);
			// System.out.println("truckcheckBoxValue"+truckcheckBoxValue);

			 if(aircheckBoxValue!=null)
			{ for(int i=0;i<aircheckBoxValue.length;++i)
			{
				 listMasterDOB=(ListMasterDOB)airList.get(i);
				 //System.out.println("description:"+listMasterDOB.getDescription());
				 if("true".equals(aircheckBoxValue[i]))
				 {
					listMasterDOB.setInvalidate("T");
				 }
				 else
				{
					 listMasterDOB.setInvalidate("F");
				}
				airList1.add(listMasterDOB);
			}
			}
			if(seacheckBoxValue!=null)
			{ for(int i=0;i<seacheckBoxValue.length;++i)
			{
				
				 
				 listMasterDOB=(ListMasterDOB)seaList.get(i);
				// System.out.println("listMasterDOB.getDes:"+listMasterDOB.getDescription());
				 if("true".equals(seacheckBoxValue[i]))
				 {
					listMasterDOB.setInvalidate("T");
				 }
				 else
				{
					 listMasterDOB.setInvalidate("F");
				}
				seaList1.add(listMasterDOB);
			}
			}
			if(truckcheckBoxValue!=null)
			{
			 for(int i=0;i<truckcheckBoxValue.length;++i)
			{
				 listMasterDOB =new ListMasterDOB();
				 listMasterDOB=(ListMasterDOB)truckList.get(i);
				 if("true".equals(truckcheckBoxValue[i]))
				 {
					listMasterDOB.setInvalidate("T");
				 }
				 else
				{
					 listMasterDOB.setInvalidate("F");
				}
				truckList1.add(listMasterDOB);
			}
			}

			remote.invalidateListMaster(airList1);
			remote.invalidateListMaster(seaList1);
			if(truckList1.size()>0)
			{
			remote.invalidateListMaster(truckList1);
			}
					errorMessage			=  "ListMaster Validated successfully :\n";
					url						=	"ListMasterViewAll.jsp?Operation=Invalidate";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
					keyValueList.add(new KeyValue("Operation","Invalidated")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
			}
			else
			{
			if(shipmentMode!=null)
			{
				if(shipmentMode.equals("Air"))
				{
					shipMode		=	"1";
					shipmentType	=	FoursoftConfig.AIR;
				}else if(shipmentMode.equals("Sea"))
				{
					shipMode	=	"2";
					
				}else if(shipmentMode.equals("Truck"))
				{
					shipMode	=	"4";
				}
			}
			if(operation!=null && operation.equals("Add"))
			{
				String[]    Type			=	request.getParameterValues("Type");
				String[]	description		=	request.getParameterValues("description");
				String[]	volume			=	request.getParameterValues("volume");
				String[]	upweight		=	request.getParameterValues("upweight");
				String[]	otweight		=	request.getParameterValues("otweight");

				if(Type!=null && Type.length>0)
				{
					for(int i=0;i<Type.length;i++)
					{
						listMasterDOB	=	new ListMasterDOB();
						listMasterDOB.setShimpmentMode(shipMode);
						listMasterDOB.setUov(uov);
						listMasterDOB.setUom(uom);
						listMasterDOB.setUldType(Type[i]);
						listMasterDOB.setDescription((description[i]!=null)?description[i]:"");
						listMasterDOB.setVolume(("".equalsIgnoreCase(volume[i]))?"0":volume[i]);
						//System.out.println("listMasterDOB.setVolume:"+listMasterDOB.getVolume());
						listMasterDOB.setPivoteUladenWeight((upweight[i]!=null && upweight[i].trim().length()!=0)?upweight[i]:"0");
						listMasterDOB.setOverPivoteTareWeight((otweight[i]!=null && otweight[i].trim().length()!=0)?otweight[i]:"0");
						listMasterDOB.setInvalidate("F");
						dataList.add(listMasterDOB);
					}
				}
			}
			else if(operation.equals("modify")||operation.equals("delete"))
			{
				String listType=request.getParameter("listType");
				String description=request.getParameter("description");
				String volume=request.getParameter("volume");
				String pivoteWeight=request.getParameter("pivoteWeight");
				String overpivoteWeight=request.getParameter("overpivoteWeight");
				listMasterDOB	=	new ListMasterDOB();
				listMasterDOB.setShimpmentMode(shipMode);
				listMasterDOB.setUov(uov);
				listMasterDOB.setUom(uom);
				listMasterDOB.setUldType(listType);
				listMasterDOB.setDescription(description);
				listMasterDOB.setVolume(volume);
				listMasterDOB.setPivoteUladenWeight(pivoteWeight);
				listMasterDOB.setOverPivoteTareWeight(overpivoteWeight);
				listMasterDOB.setInvalidate("F");
				
			}

			
			if(operation!=null && operation.equals("Add"))
			{
				if(dataList!=null && dataList.size()>0)
				{
					returnList	=	remote.insertListMasterDetails(dataList);
					if(returnList!=null && returnList.size()==2)
					{
						insertedList	=	(ArrayList)returnList.get(0);
						invalidList     =   (ArrayList)returnList.get(1);
					}
					success		=	true;
				}
			}
			else if(operation.equals("modify"))
			{

				success1=remote.updateListMasterDetails(listMasterDOB);
			}
			else if(operation.equals("delete"))
			{
				success1=remote.deleteListMasterDtls(listMasterDOB);
			}
			}
		}catch(Exception e)
		{
			success	= false;
			//Logger.error(FILE_NAME,"Exception while processing the data --->",e.toString());
      logger.error(FILE_NAME+"Exception while processing the data --->"+e.toString());
		}
	try{
		if(success||success1)
		{
				if(operation!=null && operation.equals("Add"))
				{
							if(insertedList.size()>0)
							{
								errorMessage			=  "ListMaster Details added successfully :\n";
								
							}
							if(invalidList.size()>0)
							{
								errorMessage			+= "ListMaster details already definde in the data :\n";
								
							}
							url						=	"ListMasterAdd.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage,url); 
							keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("modify"))
				{
					errorMessage			=  "ListMaster was modified successfully :\n";
					url						=	"ListMasterEnterId.jsp?Operation=modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation.equals("delete"))
				{
					errorMessage			=	"The List has been deleted successfully :\n";
					url						=	"ListMasterEnterId.jsp?Operation=delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
		}
		else
		{
				if(operation!=null && operation.equals("Add"))
				{
							errorMessage			=  "Exception while ListMaster the records :";
							url						=	"ListMasterAdd.jsp?Operation=Add";
							errorMessageObject      =  new ErrorMessage(errorMessage,url); 
							keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
							keyValueList.add(new KeyValue("Operation","Add")); 	
							errorMessageObject.setKeyValueList(keyValueList);
							request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  "Exeption while Modifying ListMaster:";
					url						=	"ListMasterEnterId.jsp?Operation=modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
		}
	%>
					<jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while forwarding to error page"+e);
    logger.error(FILE_NAME+"Exception while forwarding to error page"+e);
	}
%>

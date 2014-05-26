
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
<%@page import = "com.qms.setup.java.IndustryRegDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  java.util.Hashtable"%>
<%! 
  private static Logger logger = null;
	private static final String FILE_NAME="QMSIndustryRegistrationProcess.jsp";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	String		operation		=	request.getParameter("Operation");
	String[]	industry		=	request.getParameterValues("industry");
	String[]	description		=	request.getParameterValues("description");

	
	IndustryRegDOB industryRegDOB	=	null;
	IndustryRegDOB industryDOB		=	null;
	ArrayList	   industryList		=	new ArrayList();
	ArrayList	industryOriginalList=	new ArrayList();

	SetUpSessionHome 	home   		= 	null;    // variable to store Home Object
	SetUpSession     	 	remote	= 	null;    // variable to store Remote Object
	InitialContext		inital		=	null;
	String				message		=	null;
	boolean				success		=	true;
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	Hashtable	ht					=	null;
	ArrayList	insertedList		=	null;
	ArrayList	invalidList			=	null;
	String		successStr			=	"";
	String		invalidStr			=	"";
	String		invalidate			=	"";

	try{
		if(operation!=null && operation.equals("Add"))
		{
				if(industry!=null && industry.length>0)
					{
						for(int i=0;i<industry.length;i++)
						{
							industryRegDOB	=	new IndustryRegDOB();
							industryRegDOB.setIndustry((industry[i]!=null)?industry[i]:"");
							industryRegDOB.setDescription((description[i]!=null)?description[i]:"");
              industryRegDOB.setTerminalId(loginbean.getTerminalId());
              industryList.add(industryRegDOB);
						}
					}

		}else if(operation!=null && (operation.equals("Modify") || operation.equals("Delete")))
		{
				if(industry!=null && industry.length>0)
					{
						industryRegDOB	=	new IndustryRegDOB();
						industryRegDOB.setIndustry((industry[0]!=null)?industry[0]:"");
						industryRegDOB.setDescription((description[0]!=null)?description[0]:"");
					}
		}else if(operation!=null && operation.equals("Invalidate"))
		{
				industryOriginalList=(ArrayList)session.getAttribute("industryList");
				session.removeAttribute("industryList");
				if(industry!=null && industry.length>0)
					{
						for(int i=0;i<industry.length;i++)
						{
							invalidate	=	"invalidate"+i;
							industryRegDOB	=	new IndustryRegDOB();
							industryRegDOB.setIndustry((industry[i]!=null)?industry[i]:"");
							industryRegDOB.setDescription((description[i]!=null)?description[i]:"");
							if(request.getParameter(invalidate)!=null)
							{
								industryRegDOB.setInvalidate("T");
							}else
							{
								industryRegDOB.setInvalidate("F");
							}
							industryDOB	=	(IndustryRegDOB)industryOriginalList.get(i);
							if(!industryRegDOB.getInvalidate().equals(industryDOB.getInvalidate()))
								{	industryList.add(industryRegDOB);}
						}
					}
		}
	}catch(Exception e)
	{
		success	=	false;
		//Logger.error(FILE_NAME,"Exception while reading the data"+e.toString());
    logger.error(FILE_NAME+"Exception while reading the data"+e.toString());
	}

	try{
			inital		=	new InitialContext();
			home		=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");		
			remote		=	(SetUpSession)home.create();
			
			if(success && operation!=null && operation.equals("Add"))
			{
				if(industryList!=null && industryList.size()>0)
				{
					ht	=	remote.insertIndustryDetails(industryList,loginbean);
					
				}
			}else if(success && operation!=null && operation.equals("Modify"))
			{
					if(industryRegDOB!=null)
					{
						success	=	remote.updateIndustryDetails(industryRegDOB,loginbean);
					}
			}else if(success && operation!=null && operation.equals("Delete"))
			{
					if(industryRegDOB!=null)
					{
						success	=	remote.deleteIndustryDetails(industryRegDOB,loginbean);
					}				
			}else if(success && operation!=null && operation.equals("Invalidate"))
			{
				if(industryList!=null && industryList.size()>0)
				{
						success	=	remote.invalidateIndustryDetails(industryList,loginbean);
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
					if(ht!=null && !ht.isEmpty())
					{
						insertedList	=	(ArrayList)ht.get("insetedList");
						invalidList		=	(ArrayList)ht.get("invalidList");
					}
					if(insertedList!=null && insertedList.size()>0)
					{
						successStr	=	"Following Industry Ids are successfully Registered :\n";
						for(int i=0;i<insertedList.size();i++)
						{
							successStr	+=	(String)insertedList.get(i)+"\n";
						}
					}
					if(invalidList!=null && invalidList.size()>0)
					{
						invalidStr	=	"Following Industry Id(s) Already Exist and Have Not Been Registred :\n";
						for(int i=0;i<invalidList.size();i++)
						{
							invalidStr	+=	(String)invalidList.get(i)+"\n";
						}
					}
					errorMessage			=  successStr+invalidStr;
					url						=	"QMSIndustryRegistration.jsp?Operation=Add";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
					keyValueList.add(new KeyValue("Operation","Add")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  "Record successfully Modified IndustryId :"+industryRegDOB.getIndustry();
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					errorMessage			=	"Record successfully Deleted IndustryId :"+industryRegDOB.getIndustry();
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage			=	"Record successfully Updated :";
					url						=	"QMSIndustryRegistration.jsp?Operation=Invalidate";
					//url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-ModuleII";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate"));
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}

	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
		}else
		{
			if(operation!=null && operation.equals("Add"))
				{
					errorMessage			=  "Exeption while Inserting the records :";
					url						=	"QMSIndustryRegistration.jsp?Operation=Add";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Add")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  "Exeption while Modifying IndustryId :"+industryRegDOB.getIndustry();
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Delete"))
				{
					//errorMessage			=	"Exeption while Deleting IndustryId :"+industryRegDOB.getIndustry();
					errorMessage	=	"You can not Delete this IndustryId : "+industryRegDOB.getIndustry()+",as this is under use";
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && operation.equals("Invalidate"))
				{
					errorMessage			=	"Exception While Updating the details :";
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-ModuleI";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate"));
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
		}
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while forwarding to error page"+e);
    logger.error(FILE_NAME+"Exception while forwarding to error page"+e);
	}
	%>


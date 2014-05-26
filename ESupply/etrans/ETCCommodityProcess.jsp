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
	Program Name	: CommodityProcess.jsp
	Module Name		: ETrans
	Task			: Commodity	
	Sub Task		: CommodityProcess	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 9,2001
	Date Completed	: September 9,2001
	Date Modified	: 
	Description		:
		* This file is invoked on submission of CommodityView.jsp.This file is used to set the  Commodity details into the FS_FR_COMODITYMASTER after  
		* Modifying the Commodity details from the screen CommodityView.jsp . This file will interacts with CommodityMasterSessionBean and then calls the  
		* method updatetCommodityMasterDetails(CommodityMasterJspBean, loginDetails) or deleteCommodityMasterDetails(CommodityMasterJspBean, logindetails). 
		* details are then set to the respective variables through Object CommodityMasterJspBean.
		
*/
%>
<%@ page import = " javax.naming.InitialContext,
					org.apache.log4j.Logger,
 					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.commodity.bean.CommodityJspBean,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
					javax.servlet.RequestDispatcher,
				    java.util.ArrayList,
				    java.util.Iterator,
					java.util.HashMap" %>

<jsp:useBean id ="commodityObj" class= "com.foursoft.etrans.setup.commodity.bean.CommodityJspBean" scope ="session"/>
<jsp:setProperty name="commodityObj" property="*"/>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCommodityProcess.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
   try
   {

    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<%
   }
  }
  catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in CommodityProcess.jsp file : ", e.toString());
  logger.error(FILE_NAME+"Error in CommodityProcess.jsp file : "+ e.toString());
  } 
%>    	
<%
	String commodityId = null;
	
	
	ErrorMessage errorMessageObject = null;
    ArrayList	 keyValueList	    = new ArrayList(); 	
	

	if(request.getParameter("hazardIndicator")!=null&&request.getParameter("hazardIndicator").equals("Y"))
			commodityObj.setHazardIndicator("Y");
		else
			commodityObj.setHazardIndicator("N");
	String strParam = request.getParameter("Operation"); // variable to store the operation i.e View/Modify/Delete
	try
	{
    	 InitialContext initial = new InitialContext(); // variable to store Initial context for JNDI
 		 SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
		 SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
	  // System.out.println("submit---"+request.getParameter("Submit"));
	   if(strParam.equals("Add"))
	   {
	         commodityId = commodityObj.getCommodityId();
			 commodityObj.setSubClass(request.getParameter("subClass")==null?"":request.getParameter("subClass"));
			 commodityObj.setUnNumber(request.getParameter("unNumber")==null?"":request.getParameter("unNumber"));
			 commodityObj.setClassType(request.getParameter("classType")==null?"":request.getParameter("classType"));
		     boolean flag =  ETransHOSuperUserRemote.addCommodityDetails(commodityObj,loginbean);   
			 if(flag)
			 {
            	 errorMessageObject = new ErrorMessage("Record successfully added with CommodityId : "+commodityId,"ETCCommodityAdd.jsp"); 
				 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject);
				 
				 /**
				 String errorMessage = "Record successfully added with CommodityId : "+commodityId;
				 session.setAttribute("ErrorCode","RSI");
				 session.setAttribute("ErrorMessage",errorMessage);
				 session.setAttribute("NextNavigation","ETCCommodityAdd.jsp");  */
%>
			    <jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			 }
			 else
			 {
		    	 
				 errorMessageObject = new ErrorMessage("Record already exists with CommodityId : "+commodityId,"ETCCommodityAdd.jsp"); 
				 keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject);
				 
				 /**
				 String errorMessage = "Record already exists with CommodityId : "+commodityId;
				 session.setAttribute("Operation", "Add");
				 session.setAttribute("ErrorCode","RAE");
				 session.setAttribute("ErrorMessage",errorMessage);
				 session.setAttribute("NextNavigation","ETCCommodityAdd.jsp");  */
%>
				<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
			 }
	   }
	  else if(strParam.equals("Modify"))
	   {
			commodityObj.setSubClass(request.getParameter("subClass")==null?"":request.getParameter("subClass"));
			 commodityObj.setUnNumber(request.getParameter("unNumber")==null?"":request.getParameter("unNumber"));
			 commodityObj.setClassType(request.getParameter("classType")==null?"":request.getParameter("classType"));
		     
	       boolean flag = ETransHOSuperUserRemote.updateCommodityMasterDetails(commodityObj,loginbean);
		   // if2 begin
  		   if( flag)
	       {
	         
			 errorMessageObject = new ErrorMessage("Record successfully modified with CommodityId : "+commodityObj.getCommodityId(),"ETCCommodityEnterId.jsp?Operation="+strParam); 
			 keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
			 keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 /**		 		 
			 String errorMessage = "Record successfully modified with CommodityId : "+commodityObj.getCommodityId(); 
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","RSM");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	       }//if2 end
		   else
		   {
    	     errorMessageObject = new ErrorMessage("Error while modifying the record with CommodityId : "+commodityObj.getCommodityId(),"ETCCommodityEnterId.jsp?Operation="+strParam); 
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation","Modify")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 /**			 		 			 
			 String errorMessage = "Error while modifying the record with CommodityId : "+commodityObj.getCommodityId();
			 session.setAttribute("Operation", "Modify");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		   }
	   } // if1 end
	   //else1 start
	   else if(strParam.equals("Delete"))
	   {
			String comodityId = commodityObj.getCommodityId(); // variable to store the commodityid
	    	boolean flag = ETransHOSuperUserRemote.deleteCommodityMasterDetails( comodityId ,loginbean );
			//if in start
		  if( flag)
	      {
			 
			 errorMessageObject = new ErrorMessage("Record successfully deleted with CommodityId : "+comodityId,"ETCCommodityEnterId.jsp?Operation="+strParam); 
			 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
			 keyValueList.add(new KeyValue("Operation","Delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 			 
			 /**
			 String errorMessage = "Record successfully deleted with CommodityId : "+comodityId ;
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","RSD");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	      }// if in end
		    else
		    {
		     errorMessageObject = new ErrorMessage("You are not allowed to delete this CommodityId :"+commodityObj.getCommodityId(),"ETCCommodityEnterId.jsp?Operation="+strParam); 
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation","Delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 	 
			 /**
			 String errorMessage = "You are not allowed to delete this CommodityId :"+commodityObj.getCommodityId()+", this Id under usage";
			 session.setAttribute("Operation", "Delete");
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+strParam);  */
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		    }
		} // else1 end
		//else2 start 
    else if(strParam.equals("View"))
	   {
%>
		<h2> FourSoft 4S-ESupply </H2>
<%	
	   } //else2 end
     else if(strParam.equals("Invalidate"))
	   {
		 //shyam starts here
	      if(request.getParameter("Submit")==null)
		   {
				   
			   try
			   {
					String[]                  mfValues                          =   null;
					String[]                  checkValue                        =   null;
					String[]                  unCheckValue                      =   null;
					HashMap                   mapDob                            =   null;
					ArrayList				  unCheckList						=	null;
					mfValues        =   request.getParameterValues("mfValues");
					checkValue      =   request.getParameterValues("check");
					unCheckValue      =   request.getParameterValues("unChecked");
					mapDob			=	(HashMap)session.getAttribute("HashList");
					unCheckList			=	(ArrayList)session.getAttribute("unCheckList");

					if(mapDob==null)
						mapDob    =   new HashMap();

					if(checkValue!=null)
					{
					   int   checkValuelength  = checkValue.length;
					   String   hiddenChecked  =  null;

						for(int j=0;j<checkValuelength;j++)
						{
						  hiddenChecked = checkValue[j];
							  if(mfValues!=null)
							  {
									int mfValuesLength  = mfValues.length;
									boolean checkflag     = false;
									for(int i=0;i<mfValuesLength;i++)
									{
									    
									  if(hiddenChecked.equals(mfValues[i]))
									  {
										checkflag = true;
										break;
									  }
								
									}

									for(int k=0;k<mfValuesLength;k++)
									{
										if(unCheckValue[k]!=null || unCheckValue[k].length()>0)
										{
											if(mapDob.containsKey(unCheckValue[k]))
												mapDob.remove(unCheckValue[k]);
										}
									}

									if(checkflag)
									{
										
										mapDob.remove(hiddenChecked);
										mapDob.put(hiddenChecked,hiddenChecked);
											
										
									}
									else
									{
										mapDob.remove(hiddenChecked);
									}
							 }//end of If mfValues
							else
							{
							mapDob.remove(hiddenChecked);
							}
						 }//end of outer For loop
				}//end of If Checked values

				if(unCheckList==null)
					unCheckList	=	new ArrayList();

				int unChkLength	=	unCheckValue.length;
				if(unChkLength > 0)
				{
					for(int i=0;i<unChkLength;i++)
					{
						unCheckList.add(unCheckValue[i]);
					}
				}

				session.setAttribute("HashList",mapDob);
				session.setAttribute("unCheckList",unCheckList);
				RequestDispatcher rd = request.getRequestDispatcher("CommodityInvalidate.jsp");
				rd.forward(request, response);
				
			 }//end of try Block
			 catch(Exception e)
			 {
				e.printStackTrace();
				/*errorMessageObject = new ErrorMessage("Error while doPageVlues()",nextNavigation); 
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
				doFileDispatch(request,response,"ESupplyErrorPage.jsp");*/
			 }

		   }//shyam ends here
		   else if (request.getParameter("Submit").equals("Submit"))
		   {
			  
			   //shyam starts heere
			  HashMap                   mapDob                            =   null;
			  mapDob			=	(HashMap)session.getAttribute("HashList"); 
			  ArrayList	unCheckList			=	(ArrayList)session.getAttribute("unCheckList");
			  String[] unCheckValue      =   request.getParameterValues("unChecked");
			  String[] checkValue      =   request.getParameterValues("check");

			//unCheckded ones for current page
			  				
				if(unCheckValue !=null)
			    {
					int unChkLength1	=	unCheckValue.length;
					
					if(unChkLength1 > 0)
					{
						if(unCheckList==null)
							unCheckList	=	new ArrayList();
						for(int j=0;j<unChkLength1;j++)
						{
							unCheckList.add(unCheckValue[j]);
						}
					}
				}

				
				//Checked ones for current page
				if(checkValue != null)
			   {
					int chkLength	=	checkValue.length;
					
					if(chkLength > 0)
					{
						if(mapDob==null)
							mapDob	=	new HashMap();
						for(int j=0;j<chkLength;j++)
						{
							mapDob.put(checkValue[j],checkValue[j]);
						}
					}
			   }

			  ArrayList		dobList =	new ArrayList();	
			  if(mapDob!=null)
			  {
				  Iterator iterator	=	mapDob.keySet().iterator();
				  while(iterator.hasNext())
				  {
						CommodityJspBean commodityDOB	=	new CommodityJspBean();
						commodityDOB.setCommodityId((String)iterator.next());
						commodityDOB.setInvalidate("T");
						dobList.add(commodityDOB);
				  }
			  }	
			  
			   if(unCheckList!=null)
			  {
				   int unChkLength	=	unCheckList.size();
				   for(int i=0;i<unChkLength;i++)
				  {
						CommodityJspBean commodityDOB	=	new CommodityJspBean();
						commodityDOB.setCommodityId((String)unCheckList.get(i));
						commodityDOB.setInvalidate("F");
						dobList.add(commodityDOB);
				  }
			  }	
			   boolean flag=false;
			  //shyam ends here

			  /*java.util.ArrayList dobList=(java.util.ArrayList)session.getAttribute("dobList");
			  System.out.println("dobList.size()--"+dobList.size());
			  String invalidater[]=request.getParameterValues("checkBoxValue");
			   boolean flag=false;
			   for(int i=0;i<dobList.size();++i)
				{
				   if(invalidater[i].equalsIgnoreCase("false"))
						invalidater[i]="F";
				   else if(invalidater[i].equalsIgnoreCase("true"))
						invalidater[i]="T";
				com.foursoft.etrans.setup.commodity.bean.CommodityJspBean commodityDOB=(			com.foursoft.etrans.setup.commodity.bean.CommodityJspBean )dobList.get(i);
				commodityDOB.setInvalidate(invalidater[i]);

				}*/
				flag=ETransHOSuperUserRemote.invalidateCommodityMaster(dobList);
		  
		  if( flag)
	      {
			 
			 errorMessageObject = new ErrorMessage("Record successfully updated with CommodityId : ","../Invalidate.jsp?View=commoditymaster"); 
			 keyValueList.add(new KeyValue("ErrorCode","RSD")); 	
			 keyValueList.add(new KeyValue("Operation","Update")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 			 
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	      }// if in end
		    else
		    {
		     errorMessageObject = new ErrorMessage("You are not allowed to delete this CommodityId :","../Invalidate.jsp?View=commoditymaster"); 
			 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			 keyValueList.add(new KeyValue("Operation","Delete")); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 	 
			 %>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		    } 
			 //session.removeAttribute("HashList");
		}
		} 
 } 
 catch(Exception exp)
 {
	
	//Logger.error(FILE_NAME,"Error in ETCCommodityProcess.jsp file : ", exp.toString());
  logger.error(FILE_NAME+"Error in ETCCommodityProcess.jsp file : "+ exp.toString());
   if(strParam.equals("Add"))
      errorMessageObject = new ErrorMessage("Unable to "+strParam+" the record","ETCCommodityAdd.jsp"); 
   else
	  errorMessageObject = new ErrorMessage("Unable to "+strParam+" the record","ETCCommodityEnterId.jsp");     
   keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
   keyValueList.add(new KeyValue("Operation",strParam)); 	
   errorMessageObject.setKeyValueList(keyValueList);
   request.setAttribute("ErrorMessage",errorMessageObject);
  
   /**   
   session.setAttribute("ErrorCode","ERR");
   session.setAttribute("ErrorMessage","Unable to "+strParam+" the record" );
    if(strParam.equals("Add"))
	  session.setAttribute("NextNavigation","ETCCommodityAdd.jsp");
   else	
	   session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+strParam);  */
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />

<% 
 }
%>




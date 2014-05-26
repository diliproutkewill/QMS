
<%
/*	Programme Name : QMSChargeDescriptionProcess.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : The density group is used for the calculation of the volumetric weight.The DGC codes are 1 for                           Air,2 for Sea and 4 for Truck shipments.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.operations.charges.java.BuychargesHDRDOB,
				  com.qms.operations.charges.ejb.sls.ChargeMasterSession,
				  com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
				  java.util.ArrayList,
				  java.util.HashMap,
				  javax.naming.InitialContext,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue"%>


<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSChargeDescriptionProcess.jsp ";
%>
<%

    logger  = Logger.getLogger(FILE_NAME);	
    ChargeMasterSession			remote				=		null;
	ChargeMasterSessionHome     home				=		null;
    String						operation			=		request.getParameter("Operation");
	String						terminalId			=		request.getParameter("terminalId");
	ArrayList                   dgcList				=		new ArrayList();
	InitialContext				ctxt				=		new InitialContext();
	ArrayList                   keyValueList		=		null;
	ErrorMessage                errorMessageObject	=		null; 
	boolean                     flag				=		false;
	int[]                      shipMode             =       null;
	String[]                      chargeId            =       null;
	String[]                      descId              =       null;
	String[]                      remarks             =       null;
  String[]					          externalChargeName	  =     null;
	BuychargesHDRDOB              dob                 =       new BuychargesHDRDOB();
	ArrayList                     dobList             =        new ArrayList();
	String                        cId                 =        null;
	String                        descId1              =        null;
	String                        desc                =        null;
	String[]	airCheckbox		= null;
	String[]	seaCheckbox		= null;
    String[]	truckCheckbox	= null;
	ArrayList   chargeList      = null;
	StringBuffer errorMsg		= null;

    //ChargeMasterSession			remote				  =		null;
	//ChargeMasterSessionHome     home				  =		null;
    //String						operation			  =		request.getParameter("Operation");
	//String						terminalId			  =		request.getParameter("terminalId");
	/*ArrayList                   dgcList				  =		new ArrayList();
	InitialContext				ctxt				  =		new InitialContext();
	ArrayList                   keyValueList		  =		null;
	ErrorMessage                errorMessageObject	  =		null; 
	boolean                     flag				  =		false;
	int[]                       shipMode			  =     null;
	String[]					chargeId			  =     null;
	String[]					descId				  =     null;
	String[]					remarks				  =     null;
	String[]					externalChargeName	  =     null;
	BuychargesHDRDOB			dob				 	  =     new BuychargesHDRDOB();
	ArrayList					dobList				  =     new ArrayList();
	String						cId					  =     null;
	String						descId1				  =     null;
	String						desc				  =     null;
	String[]					airCheckbox			  =		null;
	String[]					seaCheckbox			  =		null;
    String[]					truckCheckbox		  =		null;
	ArrayList					chargeList			  =		null;
	StringBuffer				errorMsg			  =		null;*/

	try
	{
		errorMsg = new StringBuffer();

		home   =   (ChargeMasterSessionHome)ctxt.lookup("ChargeMasterSession");
		remote =   (ChargeMasterSession)home.create();

		//Logger.info("askdjfhasklf","askdfhasd"+operation);



		if("Add".equals(operation))
		{   
			chargeId			=   request.getParameterValues("chargeId");
            descId				=   request.getParameterValues("chargeDescId");			
            remarks				=   request.getParameterValues("remarks");
			airCheckbox			=	request.getParameterValues("aircheckbox");
			seaCheckbox			=	request.getParameterValues("seacheckbox");
			truckCheckbox		=	request.getParameterValues("truckcheckbox");
			externalChargeName	=	request.getParameterValues("externalChargeName");
			//System.out.println(chargeId+"      chargeId");


			shipMode        =   new int[chargeId.length];
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
			}//Logger.info(FILE_NAME,"chargeId.length  "+chargeId.length);
			for(int i=0;i<chargeId.length;i++)
			{
				dob  = new BuychargesHDRDOB();
				dob.setChargeId(chargeId[i]);
				//Logger.info(FILE_NAME,"chargeId[i ] "+chargeId[i]);
				dob.setChargeDescId(descId[i].trim());
				dob.setRemarks(remarks[i]);
				dob.setExternalChargeName(externalChargeName[i]);
				dob.setShipmentMode(shipMode[i]);
				dob.setTerminalId(loginbean.getTerminalId());
				dobList.add(dob);
			}
			
				 HashMap map = remote.addChargeDiscriptionDtls(dobList,loginbean);
				// Logger.info("laskdf","askdfj");
				 keyValueList = new ArrayList();
				 
				 if(map!=null)
				 {		
					 String duplicate ="";			 
					 ArrayList returnList = new ArrayList();

					 if(((ArrayList)map.get("NONEXISTS")).size()>0)
					 {
						 returnList  = (ArrayList)map.get("NONEXISTS");
						 errorMsg.append("Following Records are successfully added :\n");

						 for(int i=0;i<returnList.size();i++)
						 {

							  dob    =  (BuychargesHDRDOB)returnList.get(i);
                              errorMsg.append("ChargeDescriptioinId \t"+dob.getChargeDescId()+"\n");
						 }
						 

					    //errorMessageObject = new ErrorMessage("Record successfully added ","QMSChargeDescriptionAdd.jsp");
					 }
					 if(((ArrayList)map.get("EXISTS")).size()>0)
					 {
						 returnList  = (ArrayList)map.get("EXISTS");
						
						 errorMsg.append("The Following Records Were Not Inserted As the Data is Invalid or Already exists :\n");

						 for(int i=0;i<returnList.size();i++)
						 {
						      dob    =  (BuychargesHDRDOB)returnList.get(i);
							  errorMsg.append("Charge Description Id \t"+dob.getChargeDescId()+"\n");
						 }
					 }
      					
					 errorMessageObject = new ErrorMessage(errorMsg.toString(),"QMSChargeDescriptionAdd.jsp");	 
					 keyValueList.add(new KeyValue("ErrorCode","INF")); 
				 }
				 else
			     {					
					errorMessageObject = new ErrorMessage("Error While Inserting the Records","QMSChargeDescriptionAdd.jsp");
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }



				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}
		else if("Modify".equals(operation))
		{
			airCheckbox		=	request.getParameterValues("aircheckbox");
			seaCheckbox		=	request.getParameterValues("seacheckbox");
			truckCheckbox	=	request.getParameterValues("truckcheckbox");
            
			int shipmentMode        =   0;
			if("Checked".equals(airCheckbox[0]) || request.getParameter("airChecked")!=null)
			{
				if("Checked".equals(seaCheckbox[0]) || request.getParameter("seaChecked")!=null)
				{
					if("Checked".equals(truckCheckbox[0]) ||  request.getParameter("truckChecked")!=null)
					{
						shipmentMode	=	7;
					}else
					{
						shipmentMode	=	3;
					}
				}else if("Checked".equals(truckCheckbox[0]) || request.getParameter("truckChecked")!=null)
				{
					shipmentMode	=	5;
				}else
				{
					shipmentMode	=	1;
				}
			}else if("Checked".equals(seaCheckbox[0]) || request.getParameter("seaChecked")!=null)
			{
				if("Checked".equals(truckCheckbox[0]) || request.getParameter("truckChecked")!=null)
				{
					shipmentMode	=	6;
				}else
				{
					shipmentMode	=	2;
				}
			}else if("Checked".equals(truckCheckbox[0]) || request.getParameter("truckChecked")!=null)
			{
				shipmentMode	=	4;
			}
			
			//System.out.println("shipmentMode"+shipmentMode);

			  dob.setChargeId( request.getParameter("chargeId"));
			  dob.setChargeDescId( request.getParameter("descId"));
			  dob.setRemarks( request.getParameter("remarks")); 
			  dob.setExternalChargeName(request.getParameter("externalChargeName"));
              dob.setShipmentMode(shipmentMode);
			  //dob.setTerminalId( loginbean.getTerminalId()); 
			  dob.setTerminalId( terminalId); 
			     flag=remote.modifyChargeDiscriptionDtls(dob);
				 keyValueList = new ArrayList();

				 if(flag==true)
			     {
					errorMessageObject = new ErrorMessage("Record successfully Modified ","QMSChargeDescriptionEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {  
					 errorMessageObject = new ErrorMessage("Error while Modifing the Record ","QMSChargeDescriptionEnterId.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Modify")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}
		else if("Delete".equals(operation))
		{
			        cId      =  request.getParameter("chargeId");
			        descId1   =  request.getParameter("descId");
			
			  
				 //flag=remote.deleteChargeDiscriptionDtls(cId,descId1,loginbean.getTerminalId());
				 flag=remote.deleteChargeDiscriptionDtls(cId,descId1,terminalId);
				 keyValueList = new ArrayList();

				 if(flag==true)
			     {
					errorMessageObject = new ErrorMessage("Record successfully deleted","QMSChargeDescriptionEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {
					errorMessageObject = new ErrorMessage("Error while Deleting the Record","QMSChargeDescriptionEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Delete")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				// Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}else if(operation!=null && operation.equals("Invalidate")){
			chargeList   =  (ArrayList)session.getAttribute("chargeList");
			//session.removeAttribute("chargeList");
			//System.out.println("chargeList"+chargeList);
			for(int i=0;i<chargeList.size();i++)
			{  
				dob	=	(BuychargesHDRDOB)chargeList.get(i);
			//	System.out.println("chargeList   "+request.getParameter("invalidate"+i));
				if(request.getParameter("invalidate"+i)!=null)
				    dob.setInvalidate("T");
				else
					dob.setInvalidate("F");
			//	System.out.println("chargeList  11 "+dob.getInvalidate());
			}
			if(chargeList!=null)
			   remote.invalidateChargeDescId(chargeList);
			        errorMessageObject = new ErrorMessage("Description Id's invalidated  successfully.","../ESMenuRightPanel.jsp?link=es-et-Administration-Charges");
					keyValueList = new ArrayList();
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
				 

				 keyValueList.add(new KeyValue("Operation","Delete")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 //Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}
		
	 }	 
	 catch(Exception exp)
	 {exp.printStackTrace();
				 //Logger.error(FILE_NAME,"Error in QMSChargeDescriptionProcess.jsp file ",exp.toString());
         logger.error(FILE_NAME+"Error in QMSChargeDescriptionProcess.jsp file "+exp.toString());
				 
				 keyValueList       = new ArrayList();
				 if(operation.equals("Add"))
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSChargeDescriptionAdd.jsp");
				 else
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSChargeDescriptionEnterI.djsp?Operation="+operation);
				 
				 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
	}
%>
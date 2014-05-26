
<%
/*	Programme Name : QMSZoneCodeMasterProcess.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : ZoneCodeMaster
*	Sub Task Name  : ZoneCodeProcess page for all operations
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "com.qms.setup.java.ZoneCodeMasterDOB,
				  com.foursoft.esupply.common.exception.FoursoftException,
                  com.qms.setup.java.ZoneCodeChildDOB,
				  com.qms.setup.ejb.sls.QMSSetUpSession,
				  com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				  java.util.ArrayList,
				  javax.naming.InitialContext,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
	private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSZoneCodeMasterProcess.jsp ";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);
    QMSSetUpSession			remote				=		null;
	QMSSetUpSessionHome     home				=		null;
    String						operation			=		request.getParameter("Operation");
	ArrayList                   zoneCodeList		=		new ArrayList();
	InitialContext				ctxt				=		new InitialContext();
	ArrayList                   keyValueList		=		null;
	ErrorMessage                errorMessageObject	=		null; 
	boolean                     flag				=		false;
	ZoneCodeMasterDOB           dob                 =       null;
	String[]					zoneCode			=		request.getParameterValues("zoneCode");
	String[]					fromZipCode			=		request.getParameterValues("fromZipCode");
	String[]					toZipCode		    =		request.getParameterValues("toZipCode");
	String[]					zone				=		request.getParameterValues("zone");
	String[]					estimationTime		=		request.getParameterValues("estimatedTime");
	String[]					estimatedDistance	=		request.getParameterValues("estimatedDistance");
	String[]					alphaNumaric		=		request.getParameterValues("alphaNumeric"); 
	String[]					rowNo				=		request.getParameterValues("rowNo"); 	
	String						originLocation		=       request.getParameter("locationId");
	String						terminalId  		=       request.getParameter("terminalId");
	String						city				=       request.getParameter("city");
	String						state				=       request.getParameter("state");
	String						zipCode				=       request.getParameter("zipCode");
	
	String						shipmentMode		=       request.getParameter("shipmentMode");
	String						consoleType			=       request.getParameter("consoleType");
	String						shipmentModeStr		=       null;

	ZoneCodeChildDOB            childDOB            =       null;
	String[]		            checkBoxValue       =       null; 
	int							count				=       0;
	String remainingZones					=	"";
    try
	{
		home			=   (QMSSetUpSessionHome)ctxt.lookup("QMSSetUpSessionBean");
		remote			=   (QMSSetUpSession)home.create();
		
		if("1".equalsIgnoreCase(shipmentMode))
			shipmentModeStr	= "Air";
		else 
			shipmentModeStr	= "Sea";

		if("Add".equals(operation))
		{     
			try
			{
				 dob         =  new ZoneCodeMasterDOB();
                 childDOB    =  new ZoneCodeChildDOB();
				 dob.setOriginLocation((originLocation!=null && !"null".equalsIgnoreCase(originLocation))?originLocation:"");
				 dob.setTerminalId((terminalId!=null && !"null".equalsIgnoreCase(terminalId))?terminalId:"");
				 dob.setCity((city!=null && !"null".equalsIgnoreCase(city))?city:"");
				 dob.setState((state!=null && !"null".equalsIgnoreCase(state))?state:"");
				 //System.out.println("dob.getState()  "+dob.getState());
				 dob.setZipCode((zipCode!=null && !"null".equalsIgnoreCase(zipCode))?zipCode:"");
				 
				 dob.setShipmentMode(shipmentMode!=null?shipmentMode:"");
				 dob.setConsoleType(consoleType!=null?consoleType:"");
				 
				 zoneCodeList         =    new ArrayList();
				 
				for(int i=0;i<fromZipCode.length;i++)
				{
					 childDOB    =  new ZoneCodeChildDOB();
					 if("ALPHANUMERIC".equals(zipCode))
						 childDOB.setAlphaNumaric((alphaNumaric[i]!=null && !"null".equalsIgnoreCase(alphaNumaric[i]))?alphaNumaric[i]:"");
					 childDOB.setEstimatedDistance((estimatedDistance[i]!=null && !"null".equalsIgnoreCase(estimatedDistance[i]))?estimatedDistance[i]:"");
					 childDOB.setEstimationTime((estimationTime[i]!=null && !"null".equalsIgnoreCase(estimationTime[i]))?estimationTime[i]:"");
					 childDOB.setZone((zone[i]!=null && !"null".equalsIgnoreCase(zone[i]))?zone[i]:"");
					 childDOB.setToZipCode((toZipCode[i]!=null && !"null".equalsIgnoreCase(toZipCode[i]))?toZipCode[i]:"");
					 childDOB.setFromZipCode((fromZipCode[i]!=null && !"null".equalsIgnoreCase(fromZipCode[i]))?fromZipCode[i]:"");
					 zoneCodeList.add(childDOB);
				}
				 //dob.validateDetails(locationId,terminalId);
				 dob.setZoneCodeList(zoneCodeList);
				 keyValueList = new ArrayList();
				 flag=remote.insertZoneCodeDetails(dob);
				 //System.out.println("flag "+flag);
				 if(flag==true)
				 {					 
					 errorMessageObject = new ErrorMessage("Record(s) successfully added","QMSZoneCodeMasterAdd.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","RSI")); 	
				 }
				 else
			     {					
					errorMessageObject = new ErrorMessage("The Location Id "+originLocation+" is either not defined for Shipment Mode "+shipmentModeStr+" or is not mapped to the Terminal Id "+terminalId,"QMSZoneCodeMasterAdd.jsp");
					keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Add")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
			catch(FoursoftException fs)
			{
				fs.printStackTrace();
				//Logger.warning(FILE_NAME,"Record Already Exists :"+fs.toString());
				logger.warn(FILE_NAME+"Record Already Exists :"+fs.toString());
				errorMessageObject = new ErrorMessage(fs.getMessage(),"QMSZoneCodeMasterAdd.jsp");
				keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"Error while inserting Record"+e.toString());
				logger.error(FILE_NAME+"Error while inserting Record"+e.toString());
				errorMessageObject = new ErrorMessage("Error while inserting Record ","QMSZoneCodeMasterAdd.jsp");
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
		}
		else if("Modify".equals(operation))
		{
			try
			{
                 //System.out.println("After if");
			     dob								=		new ZoneCodeMasterDOB();			 
				 count								=       Integer.parseInt(request.getParameter("count"));
				 checkBoxValue		                =		request.getParameterValues("checkBoxValue"); 
				 rowNo								=       request.getParameterValues("rowNo"); 
				 zoneCode							=       request.getParameterValues("zoneCode"); 
				 dob.setTerminalId(terminalId);
				 dob.setOriginLocation((originLocation!=null && !"null".equalsIgnoreCase(originLocation))?originLocation:"");
				 dob.setCity((city!=null && !"null".equalsIgnoreCase(city))?city:"");
				 dob.setState((state!=null && !"null".equalsIgnoreCase(state))?state:"");
				 dob.setZipCode(request.getParameter("zipCode"));
				 dob.setShipmentMode(request.getParameter("shipmentMode"));
				 dob.setConsoleType(request.getParameter("consoleType"));
				 //dob.setPort(port);
				 zoneCodeList      =   new ArrayList();
				 for(int i=0;i<fromZipCode.length;i++)
			     {
				     if("true".equals(checkBoxValue[i]))
					 {   //System.out.println("After if       "+rowNo[i]);
						 childDOB    =  new ZoneCodeChildDOB();
					     if(!request.getParameter("zipCode").equals("NUMERIC"))
							childDOB.setAlphaNumaric((alphaNumaric[i]!=null && !"null".equalsIgnoreCase(alphaNumaric[i]))?alphaNumaric[i]:"");
						 childDOB.setEstimatedDistance((estimatedDistance[i]!=null && !"null".equalsIgnoreCase(estimatedDistance[i]))?estimatedDistance[i]:"");
						 childDOB.setEstimationTime((estimationTime[i]!=null && !"null".equalsIgnoreCase(estimationTime[i]))?estimationTime[i]:"");
						 childDOB.setZone((zone[i]!=null && !"null".equalsIgnoreCase(zone[i]))?zone[i]:"");
						 childDOB.setToZipCode((toZipCode[i]!=null && !"null".equalsIgnoreCase(toZipCode[i]))?toZipCode[i]:"");
						 childDOB.setFromZipCode((fromZipCode[i]!=null && !"null".equalsIgnoreCase(fromZipCode[i]))?fromZipCode[i]:"");
						 childDOB.setRowNo((rowNo[i]!=null && !"null".equalsIgnoreCase(rowNo[i]))?rowNo[i]:"");
						 childDOB.setZoneCode((zoneCode[i]!=null && !"null".equalsIgnoreCase(zoneCode[i]))?zoneCode[i]:"");
						 zoneCodeList.add(childDOB);
				    }
				 }
				
				 dob.setZoneCodeList(zoneCodeList);
				 keyValueList = new ArrayList();
			     flag=remote.updateZoneCodeDetails(dob);

				 

				 if(flag==true)
			     {
					errorMessageObject = new ErrorMessage("Record successfully Modified ","QMSZoneCodeMasterEnterId.jsp");
					keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
				 }
				 else
			     {  
					 errorMessageObject = new ErrorMessage("Error while Modifing the Record ","QMSZoneCodeMasterEnterId.jsp");
					 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 }

				 keyValueList.add(new KeyValue("Operation","Modify")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 
%>
			 <jsp:forward page="QMSESupplyErrorPage.jsp"/>  
<%
			}
			catch(FoursoftException fs)
			{
				fs.printStackTrace();
				//Logger.warning(FILE_NAME,"Record Already Exists :"+fs.toString());
        logger.warn(FILE_NAME+"Record Already Exists :"+fs.toString());
				errorMessageObject = new ErrorMessage(fs.getMessage(),"QMSZoneCodeMasterAdd.jsp");
				keyValueList.add(new KeyValue("ErrorCode","RAE")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"Error while Modifing Record"+e.toString());
				logger.error(FILE_NAME+"Error while Modifing Record"+e.toString());
				errorMessageObject = new ErrorMessage("Error while Modifing Record ","QMSZoneCodeMasterEnterId.jsp");
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
		}
		else if("Delete".equals(operation))
		{
			try
			{
			     dob								=		new ZoneCodeMasterDOB();
				 dob.setShipmentMode(shipmentMode);
				 dob.setConsoleType(consoleType);
                 dob.setOriginLocation(originLocation);
				 ArrayList     zoneList             =       new ArrayList();				 
				 count								=       Integer.parseInt(request.getParameter("count"));
				 checkBoxValue						=		request.getParameterValues("checkBoxValue"); 
                 zoneCodeList						=		new ArrayList();
				 for(int i=0;i<fromZipCode.length;i++)
			     {
				     if("true".equals(checkBoxValue[i]))
					 {
						 childDOB    =  new ZoneCodeChildDOB();					     
						 childDOB.setRowNo(rowNo[i]);
						 childDOB.setZoneCode(zoneCode[i]);
						 childDOB.setZone((zone[i]!=null && !"null".equalsIgnoreCase(zone[i]))?zone[i]:"");//subbu
						 zoneCodeList.add(childDOB);						 
				    }
				 }
				 dob.setZoneCodeList(zoneCodeList);

				// flag=remote.removeZoneCodeDetails(dob);
				remainingZones	= remote.removeZoneCodeDetails(dob);

				 keyValueList = new ArrayList();
				 //   @@  Added by subrahmanyam for 216629 on 31-AUG-10
				 if(remainingZones==null || remainingZones=="")
	 						 errorMessageObject = new ErrorMessage("Record successfully deleted","QMSZoneCodeMasterEnterId.jsp");				
				 else
						 errorMessageObject = new ErrorMessage("Following Records were Not Deleted as these zones were linked to Quotes."+"\n"+remainingZones,"QMSZoneCodeMasterEnterId.jsp");
				 keyValueList.add(new KeyValue("ErrorCode","RSD")); 					 
				
				 keyValueList.add(new KeyValue("Operation","Delete")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 //Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			  <jsp:forward page="QMSESupplyErrorPage.jsp"/> 
<%
			}
			catch(Exception e)
			{
				e.printStackTrace();
				//Logger.error(FILE_NAME,"Error while Deleting Record"+e.toString());
        logger.error(FILE_NAME+"Error while Deleting Record"+e.toString());
				errorMessageObject = new ErrorMessage("Error while Deleting Record ","QMSZoneCodeMasterEnterId.jsp");
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
		}
		else if("Invalidate".equals(operation))
		{			
			try
			{
				 StringBuffer  messageInvalidate = new StringBuffer();
				 StringBuffer  messageValidate   = new StringBuffer();
				 //@@Added by kiran.v on 06/02/2012 for Wpbn Issue -289556
				  dob								=		new ZoneCodeMasterDOB();
				 dob.setShipmentMode(shipmentMode);
				 dob.setConsoleType(consoleType);
                 dob.setOriginLocation(originLocation);
				 ArrayList     zoneList             =       new ArrayList();				 
				 count								=       Integer.parseInt(request.getParameter("count"));
				 checkBoxValue						=		request.getParameterValues("checkBoxValue"); 
                 zoneCodeList						=		new ArrayList();
				 for(int i=0;i<fromZipCode.length;i++)
			     {
					  childDOB    =  new ZoneCodeChildDOB();	
					  childDOB.setRowNo(rowNo[i]);
						 childDOB.setZoneCode(zoneCode[i]);
						 childDOB.setZone((zone[i]!=null && !"null".equalsIgnoreCase(zone[i]))?zone[i]:"");
				     if("true".equals(checkBoxValue[i]))
					 {				     
						childDOB.setInvalidate("T");											 
				    }
					   else
					 {				     
								childDOB.setInvalidate("F");											 
				    }
						 zoneCodeList.add(childDOB);		
				 }
				 //@@Ended by kiran.v
			/*	 String zoneCodeTemp   = "";
				 String[] checkbox   =  request.getParameterValues("checkBoxValue");
				 zoneCode   =  request.getParameterValues("zonecode");
				 rowNo      =  request.getParameterValues("rowNo");
				 if(zoneCode!=null && zoneCode.length>0)
  				   zoneCodeTemp       = zoneCode[0];
				  /*for(int i=0;i<checkBoxValue.length;i++)
			      {
						 childDOB    =  new ZoneCodeChildDOB();
						 childDOB.setRowNo(rowNo[i]);
						 childDOB.setZoneCode(zoneCode1[i]);					  	
					 if("true".equals(checkBoxValue[i]))
						 childDOB.setInvalidate("T");
					 else
						 childDOB.setInvalidate("F");
					 zoneCodeList.add(childDOB);
				  }

				ArrayList zoneList              = (ArrayList)session.getAttribute("zoneListTemp");
				ArrayList zoneChildList          = null;
				session.removeAttribute("zoneListTemp");
				for(int i=0;i<zoneList.size();i++)
				{
					dob  = (ZoneCodeMasterDOB)zoneList.get(i);
					if(dob.getZoneCode().equalsIgnoreCase(zoneCodeTemp))
					{   zoneChildList       = dob.getZoneCodeList();
						for(int j=0;j<zoneChildList.size();j++)
						{
							childDOB    =    (ZoneCodeChildDOB)zoneChildList.get(j);
							if("true".equals(checkbox[j]))
								childDOB.setInvalidate("T");							
							else
								childDOB.setInvalidate("F");
						}break;
					}
				}		*/		 
				// dob.setZoneCodeList(zoneList);
                 
			//	 flag = remote.invalidateZoneCodeDetails(zoneList);
	 flag = remote.invalidateZoneCodeDetails(zoneCodeList);
				 if(flag==false)
					messageInvalidate=new StringBuffer("Error while invalidating the Records");
				 else 
					 messageInvalidate=new StringBuffer("Successfully Invalidated");

				 keyValueList = new ArrayList();
				 errorMessageObject = new ErrorMessage(messageInvalidate.toString(),"QMSZoneCodeMasterEnterId.jsp?Operation=Invalidate");

				 if(flag==true)
					 keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
				 else
					 keyValueList.add(new KeyValue("ErrorCode","MSG")); 	

				 keyValueList.add(new KeyValue("Operation","Invalidate")); 	
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
				 //Logger.info(FILE_NAME,"Before errorMessageObject"+keyValueList);
%>
			  <jsp:forward page="QMSESupplyErrorPage.jsp"/>  
<%
			}
			catch(Exception e)
			{
				e.printStackTrace();
				keyValueList = new ArrayList();
				//Logger.error(FILE_NAMsE,"Error while Invalidating Record"+e.toString());
        logger.error(FILE_NAME+"Error while Invalidating Record"+e.toString());
				errorMessageObject = new ErrorMessage("Error while Invalidating Record ","QMSZoneCodeMasterViewAll.jsp?Operation=Invalidate");
				keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				keyValueList.add(new KeyValue("Operation","Add")); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
			}
		}
	 }	 
	 catch(Exception exp)
	 {
				 //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeProcess.jsp file ",exp.toString());
         logger.error(FILE_NAME+"Error in QMSDensityGroupCodeProcess.jsp file "+exp.toString());
				 exp.printStackTrace();
				 keyValueList       = new ArrayList();
				 if(operation.equals("Add"))
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSZoneCodeMasterAdd.jsp?Operation="+operation);
				 else
					 errorMessageObject = new ErrorMessage("Unable to "+operation+" the record","QMSZoneCodeMasterEnterId.jsp?Operation="+operation);
				 
				 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
				 keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
				 errorMessageObject.setKeyValueList(keyValueList);
				 request.setAttribute("ErrorMessage",errorMessageObject); 
			 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>
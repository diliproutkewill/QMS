
<%
/*	Programme Name : QMSZoneCodeMasterEnterId.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : Modify/View/Delete.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>


<%@ page import = "java.util.ArrayList,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				   com.qms.setup.ejb.sls.QMSSetUpSession,				   
				   javax.naming.InitialContext,
				   java.util.Hashtable,
				   java.util.Enumeration,
				   com.qms.setup.java.ZoneCodeMasterDOB,
				   com.qms.setup.java.ZoneCodeChildDOB,
				   com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!  
     private static Logger logger = null;
     public static final String FILE_NAME="QMSZoneCodeMasterEnterId.jsp";%>
<%
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList          keyValueList           =    null;
	ErrorMessage       errorMessageObject     =    null;
	String             operation              =    null;
    QMSSetUpSessionHome  home             =    null;
	QMSSetUpSession      remote           =    null;
	InitialContext           ictxt            =    null;
	String                   locationIds      =    null;
	String                   shipmentMode     =    null;
	String                   consoleType      =    null;
	//String                   controlStation   =    null;
	ZoneCodeMasterDOB       zoneCodeMasterDOB =    null;
	ArrayList               zoneChildList     =    null;
	ZoneCodeChildDOB        zoneCodeChildDOB  =    null;
	InitialContext          initial           =    null;

	ArrayList				zoneHashTable	  =	null;
  try 			
  {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
	 {											 		
%>

		<jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
	 else
	 { 
		             operation    =   request.getParameter("Operation");
		             shipmentMode =   request.getParameter("shipmentMode");
					 consoleType  =   request.getParameter("consoleType");
					 locationIds  =   request.getParameter("locationId");					
					 initial = new InitialContext();
					 home	=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
					 remote	=	(QMSSetUpSession)home.create();
					 zoneHashTable = remote.downloadZoneCodeMasterDetails(locationIds,shipmentMode,consoleType);
					
					//Logger.info(FILE_NAME,"zoneHashTablezoneHashTablezoneHashTablezoneHashTable::   "+zoneHashTable.size());
					out.clearBuffer();
					response.setContentType("application/vnd.ms-excel");	
					String contentDisposition = " :attachment;";	
					response.setHeader("Content-Disposition","attachment;filename=ZoneCodeMaster.xls");
	 
	   out.print("ROW ID\t" );
	   out.print("Shipment Mode\t" );
	   out.print("Console Type\t" );
	   out.print("Location\t" );
	   out.print("Terminal Id\t" );
	   out.print("City\t" );
	   out.print("State\t" );
	   //out.print("Port:\t" );
	   out.println("Zipcode Type\t" );  
	 
	if(zoneHashTable!=null && zoneHashTable.size()>0)
    {
	  int zoneHashTableValue	=	zoneHashTable.size();
	  for(int k=0;k<zoneHashTableValue;k++)
	  {   
		 
	      zoneCodeMasterDOB  =  (ZoneCodeMasterDOB)zoneHashTable.get(k);
		  out.print(new Integer(k).intValue()+"\t");
		  out.print(zoneCodeMasterDOB.getShipmentMode()+"\t");
		  out.print((zoneCodeMasterDOB.getConsoleType()!=null?zoneCodeMasterDOB.getConsoleType():"")+"\t");
		   out.print(zoneCodeMasterDOB.getOriginLocation()+"\t");
		   out.print(zoneCodeMasterDOB.getTerminalId()+"\t");
		   out.print(zoneCodeMasterDOB.getCity()+"\t"); 
		   out.print(zoneCodeMasterDOB.getState()+"\t"); 
//		   out.print(zoneCodeMasterDOB.getPort()+"\t"); 
		   out.println(zoneCodeMasterDOB.getZipCode().equals("NUMERIC")?"N":"AN"+"\t");		
	
	  }
		   out.println();
	       out.print("ROW ID\t" );
		   out.print("Alpha-Numeric  Code \t" );
		   out.print("From Zipcode\t" );
		   out.print("To Zipcode\t" );
		   out.print("Zone\t" );
		   out.print("Estimated Time \t" );
		   out.print("Estimated Distance\t" );
		   out.println("Row No\t" );

	  for(int j=0;j<zoneHashTableValue;j++)
	  {   
	      zoneCodeMasterDOB  =  (ZoneCodeMasterDOB)zoneHashTable.get(j);
		  zoneChildList      =  zoneCodeMasterDOB.getZoneCodeList();
	      for(int i=0;i<zoneChildList.size();i++)
		  {
			     zoneCodeChildDOB   =  (ZoneCodeChildDOB)zoneChildList.get(i);
		   out.print(new Integer(j).intValue()+"\t"); 
		   String alpha ="";
		   if(zoneCodeChildDOB.getAlphaNumaric()!=null)
			   alpha = zoneCodeChildDOB.getAlphaNumaric();
			else
			   alpha = "-";
		   out.print(alpha+"\t");
		   out.print(zoneCodeChildDOB.getFromZipCode()+"\t");
		   out.print(zoneCodeChildDOB.getToZipCode()+"\t"); 
		   out.print(zoneCodeChildDOB.getZone()+"\t"); 
		   out.print(zoneCodeChildDOB.getEstimationTime()+"\t"); 
		   out.print(zoneCodeChildDOB.getEstimatedDistance()+"\t");
		   out.println(zoneCodeChildDOB.getRowNo()+"\t");
		 }
	  }
	}
	else
	{
		out.println("No data available\t" );

	}
	}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
  
%>
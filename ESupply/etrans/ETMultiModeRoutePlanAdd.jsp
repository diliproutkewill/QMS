<%--
	% 
	% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	% This software is the proprietary information of FourSoft, Pvt Ltd.
	% Use is subject to license terms.
	%
	% esupply - v 1.x 
	%

	Program Name	:	ETMultiModeRoutePlanAdd.jsp
	Module Name		:	ETrans
	Task			:	Route Plan
	SubTask			:	
	Date started	:	21/05/2002
	Date Completed	:	
	Date modified	:	
	Author Name		:	Srinivasa Rao Koppurauri
	Description		: 	The following methods helps to to display the details of the given id
						with client-side validations
--%>

<%@ page import =  "com.foursoft.esupply.common.bean.DateFormatter,
					org.apache.log4j.Logger,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					java.util.ArrayList,
					com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanHdrDOB,
					com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanDtlDOB"
%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME = "ETMultiModeRoutePlanAdd.jsp";
%>

<%

  logger  = Logger.getLogger(FILE_NAME);	
	long routePlanId		= 0L;

	ETMultiModeRoutePlanHdrDOB		routePlanHdrDOB = null;
	ETMultiModeRoutePlanDtlDOB[]	routePlanDtlDOB = null;

	String operation	= "";
	String documentType = "";
	String docLabel		= "";
	String docNo		= "";
	String prqNo		= "";
	String hawbNo		= "";
	String shipmentMode = "";
	String errors		= "";
	String checked		= "";
	String createDate	= "";
	String onLoad		= "";
	String shipMode		= "";
	String terminalId	= loginbean.getTerminalId();
    String mastdisable = "";
	String serviceLevelId	= "";
	String serviceLevelDesc	= "";
	String deliveryTerms	= "";
	String prqStatus		= "";
	String whStatus			= "";
	int totalPieces = 0;
	double weight = 0.0;

	int number = 0;
	int	primaryMode = 0;	
	String shipmentStatus			= "";
	String originLocationId			= "";
	String originLocationName		= "";
	String destinationLocationId	= "";
	String destinationLocationName	= "";
	String shipperId				= "";
	String consigneeId				= "";
	String originTerminalId			= "";
	String originTerminalLocation	= "";
	String destinationTerminalId	= "";
	String destinationTerminalLocation = "";
	String transhipStatus			= "";

	String[][] originGatewayIds		= null;
	String[][] destinationGatewayIds= null;

	String oloriginId		= "";
	String oldestinationId	= "";
	String olLegType		= "";
	String olLegValidFlag	= "";
	double olcostAmount		= 0.0;
	int olShipmentMode	= 0;
	String olAutoManual		= "";
	String olMasterDocNo	= "";
	String olMasterStatus	= "";
	int olPiecesReceived	= 0;
	String olReceivedDate	= "";
	String olRemarks		= "";
	String olCarrier		= "";
	String olETD			= "";
	String olETA			= "";
	String olCarrierData	= "";

	String otoriginId		= "";
	String otdestinationId	= "";
	String otLegType		= "";
	String otLegValidFlag	= "";
	double otcostAmount		= 0.0;

	int otShipmentMode	= 0;
	String otAutoManual		= "";
	String otMasterDocNo	= "";
	String otMasterStatus	= "";
	int otPiecesReceived	= 0;
	String otReceivedDate	= "";
	String otRemarks		= "";
	String otCarrier		= "";
	String otETD			= "";
	String otETA			= "";
	String otCarrierData	= "";

	String ogoriginId		= "";
	String ogdestinationId	= "";
	String ogLegType		= "";
	String ogLegValidFlag	= "";
	double ogcostAmount		= 0.0;

	int ogShipmentMode	= 0;
	String ogAutoManual		= "";
	String ogMasterDocNo	= "";
	String ogMasterStatus	= "";
	int ogPiecesReceived	= 0;
	String ogReceivedDate	= "";
	String ogRemarks		= "";
	String ogLocationName	= "";
	String ogCarrier		= "";
	String ogETD			= "";
	String ogETA			= "";
	String ogCarrierData	= "";

	String dgoriginId		= "";
	String dgdestinationId	= "";
	String dgLegType		= "";
	String dgLegValidFlag	= "";
	double dgcostAmount		= 0.0;

	int dgShipmentMode	= 0;
	String dgAutoManual		= "";
	String dgMasterDocNo	= "";
	String dgMasterStatus	= "";
	int dgPiecesReceived	= 0;
	String dgReceivedDate	= "";
	String dgRemarks		= "";
	String dgLocationName	= "";
	String dgCarrier		= "";
	String dgETD			= "";
	String dgETA			= "";
	String dgCarrierData	= "";

	String dtoriginId		= "";
	String dtdestinationId	= "";
	String dtLegType		= "";
	String dtLegValidFlag	= "";
	double dtcostAmount		= 0.0;

	int dtShipmentMode	= 0;
	String dtAutoManual		= "";
	String dtMasterDocNo	= "";
	String dtMasterStatus	= "";
	int dtPiecesReceived	= 0;
	String dtReceivedDate	= "";
	String dtRemarks		= "";
	String dtCarrier		= "";
	String dtETD			= "";
	String dtETA			= "";
	String dtCarrierData	= "";

	ArrayList allocated = new ArrayList();
	boolean originSelf		= false;
	boolean destinationSelf	= false;
	String disabled = "";

	boolean staticRow = true;
	boolean temp = true;
	boolean isNonSystem       = false;

	String[] idInfo = null;

	try
	{

		idInfo = (String[])session.getAttribute("GateTermGetIds");
		operation		= request.getParameter("Operation");
		documentType	= request.getParameter("documentType");
		shipmentMode	= request.getParameter("ShipmentMode");

		ArrayList originList		= new ArrayList();
		ArrayList destinationList	= new ArrayList();

		String		dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		ESupplyDateUtility		utility  = new ESupplyDateUtility();
		try
		{
			utility.setPatternWithTime(dateFormat);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"The Date format is not setting in ETMulitModelRoutePlanAdd.jsp" +ex.toString());
      logger.error(FILE_NAME+"The Date format is not setting in ETMulitModelRoutePlanAdd.jsp" +ex.toString());
			ex.printStackTrace();
		}

		//Logger.info(FILE_NAME, "**************** Break Point 1 *******************");
    logger.info(FILE_NAME+ "**************** Break Point 1 *******************");

		if(session.getAttribute("OriginGatewayIds") != null)
		{
			originGatewayIds = (String[][])session.getAttribute("OriginGatewayIds");
			for(int i=0; i<originGatewayIds.length; i++)
				originList.add(originGatewayIds[i][0]);
		}
		if(session.getAttribute("DestinationGatewayIds") != null)
		{
			destinationGatewayIds = (String[][])session.getAttribute("DestinationGatewayIds");
			for(int i=0; i<destinationGatewayIds.length; i++)
				destinationList.add(destinationGatewayIds[i][0]);
		}

		//Logger.info(FILE_NAME, "**************** Break Point 2 *******************");
    logger.info(FILE_NAME+ "**************** Break Point 2 *******************");

		if(session.getAttribute("ERROR") != null)
		{
			errors = (String)session.getAttribute("ERROR");
			session.removeAttribute("ERROR");
		}

		//Logger.info(FILE_NAME, "**************** Break Point 3 *******************");
    logger.info(FILE_NAME+ "**************** Break Point 3 *******************");

		if(session.getAttribute("RoutePlanHdr") != null)
		{
			routePlanHdrDOB = (ETMultiModeRoutePlanHdrDOB)session.getAttribute("RoutePlanHdr");
			routePlanDtlDOB = routePlanHdrDOB.getRoutePlanDtlDOB();



			if(routePlanDtlDOB != null)
			{
				for(int i=0; i<routePlanDtlDOB.length; i++)
				{
					System.out.println("Origin Id ...... i -> "+routePlanDtlDOB[i].getOriginTerminalId());
				}
			}




			if(routePlanDtlDOB != null)
				number = routePlanDtlDOB.length;

			routePlanId				= routePlanHdrDOB.getRoutePlanId();

			if(routePlanHdrDOB.getHouseDocumentId() != null)
				hawbNo = routePlanHdrDOB.getHouseDocumentId();

			if(routePlanHdrDOB.getPRQId() != null)
				prqNo = routePlanHdrDOB.getPRQId();
			originLocationId		= routePlanHdrDOB.getOriginLocationId();

			if(routePlanHdrDOB.getOriginLocationName() != null)
				originLocationName		= routePlanHdrDOB.getOriginLocationName();
			destinationLocationId	= routePlanHdrDOB.getDestinationLocationId();

			if(routePlanHdrDOB.getDestinationLocationName() != null)
				destinationLocationName	= routePlanHdrDOB.getDestinationLocationName();
			shipperId				= routePlanHdrDOB.getShipperId();
			consigneeId				= routePlanHdrDOB.getConsigneeId();
			primaryMode				= routePlanHdrDOB.getPrimaryMode();
			
			originTerminalId		= routePlanHdrDOB.getOriginTerminalId();

			if(routePlanHdrDOB.getOriginTerminalLocation() != null)
				originTerminalLocation	= routePlanHdrDOB.getOriginTerminalLocation();
	
			destinationTerminalId	= routePlanHdrDOB.getDestinationTerminalId();

			if(routePlanHdrDOB.getDestinationTerminalLocation() != null)  
				destinationTerminalLocation = routePlanHdrDOB.getDestinationTerminalLocation();
			shipmentStatus			= routePlanHdrDOB.getShipmentStatus();
			String overWrite		= routePlanHdrDOB.getOverWriteFlag();
			serviceLevelId			= routePlanHdrDOB.getServiceLevelId();
			serviceLevelDesc		= routePlanHdrDOB.getServiceLevelDesc();
			deliveryTerms			= routePlanHdrDOB.getDeliveryTerms();
			totalPieces				= routePlanHdrDOB.getTotalPieces();
			weight					= routePlanHdrDOB.getWeight();
			isNonSystem				= routePlanHdrDOB.getIsNonSystemInBound();
			//Logger.info(FILE_NAME, "**************** Break Point 4 *******************");
      logger.info(FILE_NAME+ "**************** Break Point 4 *******************");

			if(shipmentStatus != null && shipmentStatus.equals("N"))
				shipmentStatus = "O";

			if(overWrite != null && overWrite.equals("Y"))
				checked = "checked";

			if(routePlanHdrDOB.getCreateTimestamp() != null)
				createDate = utility.getDisplayString(routePlanHdrDOB.getCreateTimestamp());

			if(documentType.equals("PRQ"))
			{
				docLabel = "PRQ No";
				docNo = prqNo;
			}
			else if(documentType.equals("BOOKING"))
			{
				docLabel = "Booking No";
				docNo = prqNo;
			}
			else if(documentType.equals("HAWB"))
			{
				docLabel = "HAWB No";
				docNo = hawbNo;
			}
			else if(documentType.equals("HBL"))
			{
				docLabel = "HBL No";
				docNo = hawbNo;
			}
			else if(documentType.equals("CONSIGNMENTNOTE"))
			{
				docLabel = "Consignment Note No";
				docNo = hawbNo;
			}
			else if(documentType.equals("Quote"))
			{
				docLabel = "Quote Id";
				docNo = prqNo;
			}

			if(primaryMode == 1)
				shipMode = "Air";
			else if(primaryMode == 2)
				shipMode = "Sea";
			else if(primaryMode == 4)
				shipMode = "Truck";

			//Logger.info(FILE_NAME, "**************** Break Point 5 *******************");
      logger.info(FILE_NAME+ "**************** Break Point 5 *******************");

			if(routePlanDtlDOB != null)
			{
				for(int i=0; i<routePlanDtlDOB.length; i++)
				{
					String legType = routePlanDtlDOB[i].getLegType();
					System.out.println("legTypelegTypelegTypelegTypelegType in jsp :: "+legType);
					if(legType.equals("GT") && isNonSystem)
						legType = "TP";
					if(legType.equals("LL"))
					{
						//Logger.info(FILE_NAME, "LT is working");
            logger.info(FILE_NAME+ "LT is working");
						oloriginId		= routePlanDtlDOB[i].getOriginTerminalId();
						oldestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();
						olLegType		= routePlanDtlDOB[i].getLegType();
						olLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
						olcostAmount	= routePlanDtlDOB[i].getCostAmount();

						olShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
						olAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
						if(routePlanDtlDOB[i].getCarrier() != null)
							olCarrier		= routePlanDtlDOB[i].getCarrier();
						if(routePlanDtlDOB[i].getETD() != null)
							olETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
						if(routePlanDtlDOB[i].getETA() != null)
							olETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

						if(!olCarrier.equals(""))
							olCarrierData += "Carrier: "+olCarrier+"<br>";
						if(!olETA.equals(""))
							olCarrierData += "ETA: "+olETA+"<br>";
						if(!olETD.equals(""))
							olCarrierData += "ETD: "+olETD;

						if(routePlanDtlDOB[i].getMasterDocId() != null)
							olMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
						olMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
						olPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();

						if(routePlanDtlDOB[i].getReceivedDate() != null)
							olReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
						if(routePlanDtlDOB[i].getRemarks() != null)
							olRemarks		= routePlanDtlDOB[i].getRemarks();
						allocated.add(new Integer(i));
					}
					else if(legType.equals("TG") || (originTerminalId.equals(routePlanDtlDOB[i].getOriginTerminalId()) && legType.equals("TP")))
					{
						//Logger.info(FILE_NAME, "TG or origin Selfgateway is working");
            logger.info(FILE_NAME+ "TG or origin Selfgateway is working");
						if(originTerminalId.equals(routePlanDtlDOB[i].getOriginTerminalId()) && legType.equals("TP"))
							originSelf = true;
						otoriginId		= routePlanDtlDOB[i].getOriginTerminalId();
						otdestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();
						otLegType		= routePlanDtlDOB[i].getLegType();
						otLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
						otcostAmount	= routePlanDtlDOB[i].getCostAmount();

						otShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
						otAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
						if(routePlanDtlDOB[i].getMasterDocId() != null)
							otMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
						otMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
						otPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();
						if(routePlanDtlDOB[i].getCarrier() != null)
							otCarrier		= routePlanDtlDOB[i].getCarrier();
						if(routePlanDtlDOB[i].getETD() != null)
							otETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
						if(routePlanDtlDOB[i].getETA() != null)
							otETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

						if(!otCarrier.equals(""))
							otCarrierData += "Carrier: "+otCarrier+"<br>";
						if(!otETA.equals(""))
							otCarrierData += "ETA: "+otETA+"<br>";
						if(!otETD.equals(""))
							otCarrierData += "ETD: "+otETD;

						if(routePlanDtlDOB[i].getReceivedDate() != null)
							otReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
						if(routePlanDtlDOB[i].getRemarks() != null)
							otRemarks		= routePlanDtlDOB[i].getRemarks();
						allocated.add(new Integer(i));
						if(originSelf)
						{
							ogoriginId		= otoriginId;
							ogLocationName	= originTerminalLocation;
						}
					}
					else if(legType.equals("GT"))
					{
						//Logger.info(FILE_NAME, "GT is working");
            logger.info(FILE_NAME+ "GT is working");

						dgoriginId		= routePlanDtlDOB[i].getOriginTerminalId();
						if(routePlanDtlDOB[i].getOriginTerminalLocation() != null)
							dgLocationName = routePlanDtlDOB[i].getOriginTerminalLocation() ;
						dgdestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();

						//Logger.info(FILE_NAME, "Destination Gateway -> "+dgoriginId);
            logger.info(FILE_NAME+ "Destination Gateway -> "+dgoriginId);
						//Logger.info(FILE_NAME, "Destination Terminal -> "+dgdestinationId);
            logger.info(FILE_NAME+ "Destination Terminal -> "+dgdestinationId);

						dgLegType		= routePlanDtlDOB[i].getLegType();
						dgLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
						dgcostAmount	= routePlanDtlDOB[i].getCostAmount();

						dgShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
						dgAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
						if(routePlanDtlDOB[i].getMasterDocId() != null)
							dgMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
						
						
						dgMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
						dgPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();
						if(routePlanDtlDOB[i].getCarrier() != null)
							dgCarrier		= routePlanDtlDOB[i].getCarrier();
						if(routePlanDtlDOB[i].getETD() != null)
							dgETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
						if(routePlanDtlDOB[i].getETA() != null)
							dgETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

						if(!dgCarrier.equals(""))
							dgCarrierData += "Carrier: "+dgCarrier+"<br>";
						if(!dgETA.equals(""))
							dgCarrierData += "ETA: "+dgETA+"<br>";
						if(!dgETD.equals(""))
							dgCarrierData += "ETD: "+dgETD;

						if(routePlanDtlDOB[i].getReceivedDate() != null)
							dgReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
						if(routePlanDtlDOB[i].getRemarks() != null)
							dgRemarks		= routePlanDtlDOB[i].getRemarks();
						allocated.add(new Integer(i));
					}
					else if(legType.equals("TL"))
					{
						//Logger.info(FILE_NAME, "TL is working");
            logger.info(FILE_NAME+ "TL is working");

						dtoriginId		= routePlanDtlDOB[i].getOriginTerminalId();
						dtdestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();
						dtLegType		= routePlanDtlDOB[i].getLegType();
						dtLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
						dtcostAmount	= routePlanDtlDOB[i].getCostAmount();

						dtShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
						dtAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
						if(routePlanDtlDOB[i].getMasterDocId() != null)
							dtMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
						dtMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
						dtPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();
						if(routePlanDtlDOB[i].getCarrier() != null)
							dtCarrier		= routePlanDtlDOB[i].getCarrier();
						if(routePlanDtlDOB[i].getETD() != null)
							dtETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
						if(routePlanDtlDOB[i].getETA() != null)
							dtETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

						if(!dtCarrier.equals(""))
							dtCarrierData += "Carrier: "+dtCarrier+"<br>";
						if(!dtETA.equals(""))
							dtCarrierData += "ETA: "+dtETA+"<br>";
						if(!dtETD.equals(""))
							dtCarrierData += "ETD: "+dtETD;

						if(routePlanDtlDOB[i].getReceivedDate() != null)
							dtReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
						if(routePlanDtlDOB[i].getRemarks() != null)
							dtRemarks		= routePlanDtlDOB[i].getRemarks();
						allocated.add(new Integer(i));
					}
				}

				for(int i=0; i<routePlanDtlDOB.length; i++)
				{
					if(dgoriginId.equals("") && routePlanDtlDOB[i].getOriginTerminalId().equals(destinationTerminalId))
					{
						//Logger.info(FILE_NAME, "Self Gateway for destination is working");
            logger.info(FILE_NAME+ "Self Gateway for destination is working");
						dgoriginId		= routePlanDtlDOB[i].getOriginTerminalId();

						if(routePlanDtlDOB[i].getOriginTerminalLocation() != null)
							dgLocationName = routePlanDtlDOB[i].getOriginTerminalLocation() ;
						
						dgdestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();
						dgLegType		= routePlanDtlDOB[i].getLegType();
						dgLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
						dgcostAmount	= routePlanDtlDOB[i].getCostAmount();

						dgShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
						dgAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
						if(routePlanDtlDOB[i].getCarrier() != null)
							dgCarrier		= routePlanDtlDOB[i].getCarrier();
						if(routePlanDtlDOB[i].getETD() != null)
							dgETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
						if(routePlanDtlDOB[i].getETA() != null)
							dgETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());
						if(!dgCarrier.equals(""))
							dgCarrierData += "Carrier: "+dgCarrier+"<br>";
						if(!dgETA.equals(""))
							dgCarrierData += "ETA: "+dgETA+"<br>";
						if(!dgETD.equals(""))
							dgCarrierData += "ETD: "+dgETD;
						if(routePlanDtlDOB[i].getMasterDocId() != null){
							dgMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
							mastdisable    = "disabled";
                         }
						
						dgMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
						dgPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();

						if(routePlanDtlDOB[i].getReceivedDate() != null)
							dgReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
						if(routePlanDtlDOB[i].getRemarks() != null)
							dgRemarks		= routePlanDtlDOB[i].getRemarks();
						allocated.add(new Integer(i));
					}
					else if(routePlanDtlDOB[i].getOriginTerminalId().equals(otdestinationId))
					{
						//Logger.info(FILE_NAME, "Orign Gateway Data");
            logger.info(FILE_NAME+ "Orign Gateway Data");
						if(!originSelf)
						{
							ogoriginId		= routePlanDtlDOB[i].getOriginTerminalId();
							if(routePlanDtlDOB[i].getOriginTerminalLocation() != null)
								ogLocationName  = routePlanDtlDOB[i].getOriginTerminalLocation();
							ogdestinationId	= routePlanDtlDOB[i].getDestinationTerminalId();
							ogLegType		= routePlanDtlDOB[i].getLegType();
							ogLegValidFlag	= routePlanDtlDOB[i].getLegValidFlag();
							ogcostAmount	= routePlanDtlDOB[i].getCostAmount();

							ogShipmentMode	= routePlanDtlDOB[i].getShipmentMode();
							ogAutoManual	= routePlanDtlDOB[i].getAutoManualFlag();
							if(routePlanDtlDOB[i].getCarrier() != null)
								ogCarrier		= routePlanDtlDOB[i].getCarrier();
							if(routePlanDtlDOB[i].getETD() != null)
								ogETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
							if(routePlanDtlDOB[i].getETA() != null)
								ogETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

							if(!ogCarrier.equals(""))
								ogCarrierData += "Carrier: "+ogCarrier+"<br>";
							if(!ogETA.equals(""))
								ogCarrierData += "ETA: "+ogETA+"<br>";
							if(!ogETD.equals(""))
								ogCarrierData += "ETD: "+ogETD;

							if(routePlanDtlDOB[i].getMasterDocId() != null){
								ogMasterDocNo	= routePlanDtlDOB[i].getMasterDocId();
								mastdisable    = "disabled";
                           }

							ogMasterStatus	= routePlanDtlDOB[i].getShipmentStatus();
							ogPiecesReceived= routePlanDtlDOB[i].getPiecesReceived();

							if(routePlanDtlDOB[i].getReceivedDate() != null)
								ogReceivedDate	= utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
							if(routePlanDtlDOB[i].getRemarks() != null)
								ogRemarks		= routePlanDtlDOB[i].getRemarks();
							allocated.add(new Integer(i));
						}
					}
				}
			}
		}

		if(ogoriginId.equals(""))
			ogoriginId = otoriginId;


		if(ogoriginId.equals(otoriginId))
		{
			

			ogdestinationId		= otdestinationId;
			ogLegType		= otLegType;
			ogLegValidFlag		= otLegValidFlag;
			ogcostAmount		= otcostAmount;

			ogShipmentMode		= otShipmentMode;
			ogAutoManual		= otAutoManual;
			ogMasterDocNo		= otMasterDocNo;
			ogMasterStatus		= otMasterStatus;
			ogPiecesReceived	= otPiecesReceived;
			ogReceivedDate		= otReceivedDate;
			ogRemarks		= otRemarks;

			ogCarrier		= otCarrier;
			ogETD			= otETD;
			ogETA			= otETA;
			ogCarrierData		= otCarrierData;



			otdestinationId		= "";
			otLegType		= "";
			otLegValidFlag		= "";
			otcostAmount		= 0.0;

			otShipmentMode		= 0;
			otAutoManual		= "AssignedToGateway";
			otMasterDocNo		= "";
			otMasterStatus		= "";
			otPiecesReceived	= 0;
			otReceivedDate		= "";
			otRemarks		= "";

			otCarrier		= "";
			otETD			= "";
			otETA			= "";
			otCarrierData		= "";
		}

		if(dtoriginId.equals(dgoriginId))
		{

			dtdestinationId	= dgdestinationId;

			dtLegValidFlag	= dgLegValidFlag;
			dtcostAmount		= dgcostAmount;

			dtShipmentMode	= dgShipmentMode;
			dtAutoManual	= dgAutoManual;
			dtMasterDocNo	= dgMasterDocNo;
			dtMasterStatus	= dgMasterStatus;
			dtPiecesReceived= dgPiecesReceived;
			dtReceivedDate	= dgReceivedDate;
			dtRemarks	= dgRemarks;

			dtCarrier	= dgCarrier;
			dtETD		= dgETD;
			dtETA		= dgETA;
			dtCarrierData	= dgCarrierData;


			dgdestinationId	= "";
			dgLegType	= "";
			dgLegValidFlag	= "";
			dgcostAmount	= 0.0;

			dgShipmentMode	= 0;
			dgAutoManual	= "";
			dgMasterDocNo	= "";
			dgMasterStatus	= "";
			dgPiecesReceived= 0;
			dgReceivedDate	= "";
			dgRemarks	= "";
			dgLocationName	= "";
			dgCarrier	= "";
			dgETD		= "";
			dgETA		= "";
			dgCarrierData	= "";



		
		}

%>
<html>
<head>
	<title>Routing Plan </title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp" type="text/css">
	<script>

		idsGate = new Array();
<%
		if(idInfo!=null && idInfo.length>0)
		{
			for(int loop=0;loop<idInfo.length; loop++)
			{
%>
				idsGate[<%=loop%>] = '<%=idInfo[loop]%>';
<%
			}
		}
%>



	originId		= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	destinationId	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	shipmentMode	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	autoManual		= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	masterDocNo		= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	masterStatus	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	legType			= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	legValidFlag	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	costAmount		= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	piecesReceived	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	receivedDate	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	remarks			= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	originLocation	= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	carrier			= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	ETD				= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	ETA				= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	staticFlag		= new Array("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

	var k1 = 0;
	var k2 = 0;
	var k3 = 0;
	var k4 = 0;
	var k5 = 0;
	var k6 = 0;
	var k7 = 0;
	var k8 = 0;
	var k9 = 0;
	var k10= 0;
	var k11= 0;
	var k12= 0;
	var k13= 0;
	var k14=0;
	var k15=0;
	var k16 = 0;
	var k17 = 0;

<%
		int count = 0;

		if(routePlanDtlDOB != null)
		{
			String flagValue = "C";

			for(int i=0; i<routePlanDtlDOB.length; i++)
			{
				if(!allocated.contains(new Integer(i)))
				{
					String masterDocNo = routePlanDtlDOB[i].getMasterDocId();
					String receivedDate = "";
					String remarks = "";
					String originLocation = "";
					String carrier = "";
					String ETD = "";
					String ETA = "";

					if(masterDocNo == null || masterDocNo.equals(""))
						masterDocNo = "";

					String sMode = "";

					if(routePlanDtlDOB[i].getShipmentMode() == 1)
						sMode = "Air";
					else if(routePlanDtlDOB[i].getShipmentMode() == 2)
						sMode = "Sea";
					else if(routePlanDtlDOB[i].getShipmentMode() == 4)
						sMode = "Truck";

					if(routePlanDtlDOB[i].getReceivedDate() != null)
						receivedDate = utility.getDisplayString(routePlanDtlDOB[i].getReceivedDate());
					if(routePlanDtlDOB[i].getRemarks() != null)
						remarks = routePlanDtlDOB[i].getRemarks();
					if(routePlanDtlDOB[i].getOriginTerminalLocation() != null)
						originLocation = routePlanDtlDOB[i].getOriginTerminalLocation();

					if(routePlanDtlDOB[i].getCarrier() != null)
						carrier	= routePlanDtlDOB[i].getCarrier();
					if(routePlanDtlDOB[i].getETD() != null)
						ETD = utility.getDisplayString(routePlanDtlDOB[i].getETD());
					if(routePlanDtlDOB[i].getETA() != null)
						ETA = utility.getDisplayString(routePlanDtlDOB[i].getETA());

					if(routePlanDtlDOB[i].getShipmentStatus().equals("C") || routePlanDtlDOB[i].getShipmentStatus().equals("B") || routePlanDtlDOB[i].getShipmentStatus().equals("P"))
						transhipStatus = "C";

					if(terminalId.equals(routePlanDtlDOB[i].getOriginTerminalId()))
						temp = false;

					if(temp)
						flagValue = "C";
					else flagValue = "O";

					if(terminalId.equals(originLocation))
						flagValue = "O";
					if(terminalId.equals(otoriginId))
						flagValue = "O";
					if(terminalId.equals(ogoriginId))
						flagValue = "O";

%>
	originId[<%=count%>]		= '<%=routePlanDtlDOB[i].getOriginTerminalId()%>';
	destinationId[<%=count%>]	= '<%=routePlanDtlDOB[i].getDestinationTerminalId()%>';
	shipmentMode[<%=count%>]	= '<%=sMode%>';
	autoManual[<%=count%>]		= '<%=routePlanDtlDOB[i].getAutoManualFlag()%>';
	masterDocNo[<%=count%>]		= '<%=masterDocNo%>';
	masterStatus[<%=count%>]	= '<%=routePlanDtlDOB[i].getShipmentStatus()%>';
	legType[<%=count%>]			= '<%=routePlanDtlDOB[i].getLegType()%>';		
	legValidFlag[<%=count%>]	= '<%=routePlanDtlDOB[i].getLegValidFlag()%>';
	costAmount[<%=count%>]	= '<%=routePlanDtlDOB[i].getCostAmount()%>';

	piecesReceived[<%=count%>]	= '<%=routePlanDtlDOB[i].getPiecesReceived()%>';
	receivedDate[<%=count%>]	= '<%=receivedDate%>';
	remarks[<%=count%>]			= '<%=remarks%>';
	originLocation[<%=count%>]	= '<%=originLocation%>';
	carrier[<%=count%>]			= '<%=carrier%>';
	ETD[<%=count%>]				= '<%=ETD%>';
	ETA[<%=count%>]				= '<%=ETA%>';
	staticFlag[<%=count%>]		= '<%=flagValue%>';
<%
					count++;
				}
			}
		}
		//Logger.info(FILE_NAME, "Count -> "+count);
    logger.info(FILE_NAME+ "Count -> "+count);

		if(count > 0)
		{
			onLoad = "createForm(number, -1);";
			disabled = "disabled";
		}
		if(count == 0)
			count = 1;
%>
	var check = <%=count%>
	var number = <%=count%>

	var modes = new Array('Air', 'Sea', 'Truck');
	var am = new Array('Auto', 'Manual');
	var amCodes = new Array('A', 'M');

	function createForm(numb, row)
	{
		dgmStatus = document.forms[0].dgmasterStatus.value;
		ogmStatus = document.forms[0].ogmasterStatus.value;
		number = parseInt(numb);

		for(var j=0; j<document.forms[0].elements.length; j++)
		{
			if(document.forms[0].elements[j].name == "originId")
			{
					originId[k1] = document.forms[0].elements[j].value;
					k1++;
			}
			if(document.forms[0].elements[j].name == "destinationId")
			{
					destinationId[k2] = document.forms[0].elements[j].value;
					k2++;
			}
			if(document.forms[0].elements[j].name == "shipmentMode")
			{
					shipmentMode[k3] = document.forms[0].elements[j].value;
					k3++;
			}
			if(document.forms[0].elements[j].name == "autoManual")
			{
					autoManual[k4] = document.forms[0].elements[j].value;
					k4++;
			}
			if(document.forms[0].elements[j].name == "masterDocNo")
			{
					masterDocNo[k5] = document.forms[0].elements[j].value;
					k5++;
			}
			if(document.forms[0].elements[j].name == "masterStatus")
			{
					masterStatus[k6] = document.forms[0].elements[j].value;
					k6++;
			}
			if(document.forms[0].elements[j].name == "legType")
			{
					legType[k7] = document.forms[0].elements[j].value;
					k7++;
			}
			if(document.forms[0].elements[j].name == "legValidFlag")
			{
					legValidFlag[k8] = document.forms[0].elements[j].value;
					k8++;
			}
			if(document.forms[0].elements[j].name == "piecesReceived")
			{
					piecesReceived[k9] = document.forms[0].elements[j].value;
					k9++;
			}
			if(document.forms[0].elements[j].name == "receivedDate")
			{
					receivedDate[k10] = document.forms[0].elements[j].value;
					k10++;
			}
			if(document.forms[0].elements[j].name == "remarks")
			{
					remarks[k11] = document.forms[0].elements[j].value;
					k11++;
			}
			if(document.forms[0].elements[j].name == "originLocation")
			{
					originLocation[k12] = document.forms[0].elements[j].value;
					k12++;
			}
			if(document.forms[0].elements[j].name == "carrier")
			{
					carrier[k13] = document.forms[0].elements[j].value;
					k13++;
			}
			if(document.forms[0].elements[j].name == "staticFlag")
			{
					staticFlag[k16] = document.forms[0].elements[j].value;
					k16++;
			}
			if(document.forms[0].elements[j].name == "costAmount")
			{
					costAmount[k17] = document.forms[0].elements[j].value;
					k17++;
			}

		}

		if(row !=-1)
		{
			for(var j=0; j < destinationId.length -1; j++)
			{
				if( j  >= row)
				{
					originId[j]			= originId[j+1];
					destinationId[j]	= destinationId[j+1];
					shipmentMode[j]		= shipmentMode[j+1];
					autoManual[j]		= autoManual[j+1];
					masterDocNo[j]		= masterDocNo[j+1];
					masterStatus[j]		= masterStatus[j+1];
					legType[j]			= legType[j+1];
					legValidFlag[j]		= legValidFlag[j+1];
					costAmount[j]		= costAmount[j+1];
					
					piecesReceived[j]	= piecesReceived[j+1];
					receivedDate[j]		= receivedDate[j+1];
					remarks[j]			= remarks[j+1];
					originLocation[j]	= originLocation[j+1];
					carrier[j]			= carrier[j+1];
					ETD[j]				= ETD[j+1];
					ETA[j]				= ETA[j+1];
					staticFlag[j]		= staticFlag[j+1];
				}
			}
		}
		data = "";
		inter = "'";

		if(number < 20 && number > -1)
		{
			data += "<table width='800'  bgcolor=FFFFFF  cellpadding=4 cellspacing=1 border=0>";
			for(var i=0; i< number; i++)
			{
				data = ""+data +"<tr class='formdata' valign='top'>";

				data = data + "<td width='10%' class='formheader'><font size=1>Gateway</font></td><td width='5%'>";

				if(!(masterStatus[i] == 'C' || masterStatus[i] == 'B' || masterStatus[i] == 'P' || staticFlag[i] == 'C'))
				{

					if( (i>0 && (masterStatus[i-1] == 'C' || masterStatus[i-1] == 'B' || masterStatus[i-1] == 'P' || staticFlag[i-1] == 'C')) || (ogmStatus == 'C' || ogmStatus == 'B' || ogmStatus == 'P') )
						data = data + "<input type=button value='<<' disabled onClick='removeFormRow("+i+")' class='input'>";
					else
						data = data + "<input type=button value='<<' onClick='removeFormRow("+i+")' class='input'>";
				}

				if(!(masterStatus[i] == 'C' || masterStatus[i] == 'B' || masterStatus[i] == 'P' || staticFlag[i] == 'C'))
				{
					data = data + "</td><input type=hidden name='masterStatus' value='"+masterStatus[i]+"'>"+
<%					if(isNonSystem == true)
					{
%>
						"<input type=hidden name='legType' value='GT'>"+
<%					}
					else
				    {
%>					
						"<input type=hidden name='legType' value='TP'>"+
<%					}
%>								 
									"<input type=hidden name='legValidFlag' value='"+legValidFlag[i]+"'>"+
									"<input type=hidden name='costAmount' value='"+costAmount[i]+"'>"+

									"<input type=hidden name='destinationId' value='"+destinationId[i]+"'>"+
									"<input type=hidden name='piecesReceived' value='"+piecesReceived[i]+"'>"+
									"<input type=hidden name='receivedDate' value='"+receivedDate[i]+"'>"+
									"<input type=hidden name='ETD' value='"+ETD[i]+"'>"+
									"<input type=hidden name='ETA' value='"+ETA[i]+"'>"+
									"<input type=hidden name='carrier' value='"+carrier[i]+"'>"+
									"<input type=hidden name='staticFlag' value='"+staticFlag[i]+"'>"

					data = data + "<td width='10%' align=center><font size=1>"+originLocation[i]+"</font><input type=hidden name='originLocation' value='"+originLocation[i]+"'></td>"
					data = data + "<td width=10% align=center>"

					if( (i>0 && (masterStatus[i-1] == 'C' || masterStatus[i-1] == 'B' || masterStatus[i-1] == 'P' || staticFlag[i-1] == 'C')) || (ogmStatus == 'C' || ogmStatus == 'B' || ogmStatus == 'P') )
					{
						ogmStatus = '';
						data = data + "<input type=text class='text' name ='originId' value='"+originId[i]+"' size=7 disabled onBlur='toUpperCase(this)'><input type=button name='gateIds' row = '"+i+"' value='...' disabled class='input' onClick='showLOVForGatewayIds(this)'></td>"
						data = data + "<input type=hidden name ='originIdTemp' value='"+originId[i]+"'></td>"
					}
					else
					{
						data = data + "<input type=text class='text' name ='originId' value='"+originId[i]+"' size=7 onBlur='toUpperCase(this);changeValue(this,"+i+")'><input type=button name='gateIds' row = '"+i+"' value='...' class='input' onClick='showLOVForGatewayIds(this)'></td>"
						data = data + "<input type=hidden name ='originIdTemp' value='"+originId[i]+"'></td>"
					}
					data = data + "<td width=10% align=center><select name='shipmentMode' row='"+i+"' size=1 class='select'  onFocus='confirmSM(this)' onChange='removeValue(this);confirmSMode(this)'>"

					for(var j=0; j<modes.length; j++)
					{
						if(shipmentMode[i] == modes[j])
						{
							data = data + "<option value='"+modes[j]+"' selected>"+modes[j]+"</option>";
						}
						else
						{
							data = data + "<option value='"+modes[j]+"'>"+modes[j]+"</option>";
						}
					}

					data = data + "</select></td><td width=10% align=center>Auto<input type=hidden name='autoManual' value='A'>"
					data = data +"</td><td width=20% align=center><input type=text class='text' name='masterDocNo' value='"+masterDocNo[i]+"' size=12 onBlur='toUpperCase(this)'>&nbsp;<input type=button name='masDoc' row='"+i+"' value='...' class='input' onClick='showLocationsLOV(this)'></td>"+
							"<td width='5%' align=left>";

					if(i == (number-1))
					{
						if(!(dgmStatus == 'C' || dgmStatus == 'B' || dgmStatus == 'P'))
						{
							data = "" + data + "<input type='Button' size=10  value='>>'  onClick='createForm(++number,-1);' class='input'>"
						}
					}
					data = "" + data + "</td>"+
							"<td width='10%' align=center colspan='2'><input type=text class='text' name='remarks' value='"+remarks[i]+"' size=10></td>"+
							"<td width='10%' align=center><font size=1>"
					if(carrier[i] != '')
						data = data + "Carrier : "+carrier[i]+"<br>";
					if(ETA[i] != '')
						data = data + "ETA : "+ETA[i]+"<br>";
					if(ETD[i] != '')
						data = data + "ETD : "+ETD[i]+"<br>";
					data  = data + "</font></td>"
				}
				else
				{
					data = data + "</td><input type=hidden name='masterStatus' value='"+masterStatus[i]+"'>"+
									"<input type=hidden name='legType' value='TP'>"+
									"<input type=hidden name='legValidFlag' value='"+legValidFlag[i]+"'>"+
									"<input type=hidden name='costAmount' value='"+costAmount[i]+"'>"+

									"<input type=hidden name='destinationId' value='"+destinationId[i]+"'>"+
									"<input type=hidden name='originId' value='"+originId[i]+"'>"+
									"<input type=hidden name='originIdTemp' value='"+originId[i]+"'>"+
									"<input type=hidden name='piecesReceived' value='"+piecesReceived[i]+"'>"+
									"<input type=hidden name='receivedDate' value='"+receivedDate[i]+"'>"+
									"<input type=hidden name='shipmentMode' value='"+shipmentMode[i]+"'>"+
									"<input type=hidden name='autoManual' value='"+autoManual[i]+"'>"+
									"<input type=hidden name='masterDocNo' value='"+masterDocNo[i]+"'>"+
									"<input type=hidden name='remarks' value='"+remarks[i]+"'>"+
									"<input type=hidden name='carrier' value='"+carrier[i]+"'>"+
									"<input type=hidden name='ETD' value='"+ETD[i]+"'>"+
									"<input type=hidden name='ETA' value='"+ETA[i]+"'>"+
									"<input type=hidden name='originLocation' value='"+originLocation[i]+"'>"+
									"<td width='10%' align=center><font size=1>"+originLocation[i]+"</font></td>"+
									"<td width=10% align=center>"+originId[i]+"</td>"+
									"<td width=10% align=center>"+shipmentMode[i]+"</td><td width=10% align=center>Auto</td>"+
									"<td width=20% align=center>"+masterDocNo[i]+"</td>"+
									"<td width='5%'  align=center>";

					if(i == (number-1))
					{
						if((dgmStatus == 'C' || dgmStatus == 'B' || dgmStatus == 'P'))
						{
							data = "" + data + "<input type='Button' size=10  value='>>'  onClick='createForm(++number,-1);' class='input'>"
						}
						
						document.forms[0].dgoriginId.disabled = true;
					}
					data = "" + data + "</td>"+
							"<td width='10%' align=center colspan='2'>"+remarks[i]+"</td>"+
							"<td width='10%' align=center><font size=1>"
					if(carrier[i] != '')
						data = data + "Carrier : "+carrier[i]+"<br>";
					if(ETA[i] != '')
						data = data + "ETA : "+ETA[i]+"<br>";
					if(ETD[i] != '')
						data = data + "ETD : "+ETD[i]+"<br>";
					data  = data + "</font></td>"
				}
				
				data = data + "</tr>";
			}
			data += "</table>";
			k1 = 0;
			k2 = 0;
			k3 = 0;
			k4 = 0;
			k5 = 0;
			k6 = 0;
			k7 = 0;
			k8 = 0;
			k9 = 0;
			k10 = 0;
			k11 = 0;
			k12 = 0;
			k13 = 0;
			k16 = 0;

			if (document.layers)
			{
				document.layers.cust.document.write(data);
				document.layers.cust.document.close();
			}
			else
			{
					if (document.all)
					{
						cust.innerHTML = data;
					}
			}
		}
		else
		{
			window.alert("Only up to 20 entries.");
		}
	}

	function removeFormRow(row)
	{
		number -- ;
		createForm(number, row);
		if(row == 0 && number == 0)
			document.forms[0].tranship.disabled = false;
	}
	function showLocationsLOV(obj)
	{
		name1 = obj.name;
		row = obj.row;
		alert("5555 "+name1+" rowrowrow: "+row);
		var originTerminal = '';
		var destinationTerminal = '';
		var lent;

		if(name1 == 'olmasDoc')
		{
			originTerminal		= document.forms[0].oloriginId.value;	 
			destinationTerminal	= document.forms[0].oldestinationId.value;
			shipmentMode = document.forms[0].olshipmentMode.value;
			searchStr = document.forms[0].olmasterDocNo.value;
		}
		if(name1 == 'otmasDoc')
		{
			originTerminal		= document.forms[0].otoriginId.value;	 
			destinationTerminal	= document.forms[0].ogoriginId.options[document.forms[0].ogoriginId.options.selectedIndex].value;

			if(originTerminal == destinationTerminal)
			{
				if(document.forms[0].originId != null)
				{
					lent = document.forms[0].originId.length;
					if(lent > 1)
						destinationTerminal	= document.forms[0].originId[row].value;
					else destinationTerminal	= document.forms[0].originId.value;
				}
				else
				{
					destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				}
			}

			shipmentMode = document.forms[0].otshipmentMode.value;
			searchStr = document.forms[0].otmasterDocNo.value;
		}
		else if(name1 == 'ogmasDoc')
		{
			originTerminal		= document.forms[0].ogoriginId.value;	 
			if(document.forms[0].originId != null)
			{
				lent = document.forms[0].originId.length;
				if(lent > 1)
					destinationTerminal	= document.forms[0].originId[row].value;
				else destinationTerminal	= document.forms[0].originId.value;
			}
			else
			{
				destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
			}

			shipmentMode = document.forms[0].ogshipmentMode.value;
			searchStr = document.forms[0].ogmasterDocNo.value;
		}
		else if(name1 == 'dgmasDoc')
		{
			originTerminal = document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;

			if(originTerminal == document.forms[0].dtoriginId.value)
			{
				destinationTerminal	= document.forms[0].dtdestinationId.value;
			}
			else
			{
				destinationTerminal	= document.forms[0].dgdestinationId.value;
			}
			shipmentMode = document.forms[0].dgshipmentMode.value;
			searchStr = document.forms[0].dgmasterDocNo.value;
		}
		else if(name1 == 'dtmasDoc')
		{
			originTerminal		= document.forms[0].dtoriginId.value;	 
			destinationTerminal	= document.forms[0].dtdestinationId.value;
			shipmentMode = document.forms[0].dtshipmentMode.value;
			searchStr = document.forms[0].dtmasterDocNo.value;
		}
		else if(name1 == 'masDoc')
		{
			lent = document.forms[0].masterDocNo.length;
			row = obj.row

			if(lent > 1)
			{
				originTerminal = document.forms[0].originId[row].value;

				if(row == (lent -1))
				{
					destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				}
				else
				{
					destinationTerminal	= document.forms[0].originId[parseInt(row)+1].value;
				}
				shipmentMode = document.forms[0].shipmentMode[row].value;
				searchStr = document.forms[0].masterDocNo[row].value;
			}
			else
			{
				originTerminal = document.forms[0].originId.value;
				destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				shipmentMode = document.forms[0].shipmentMode.value;
				searchStr = document.forms[0].masterDocNo.value;
			}
		}
		alert(originTerminal);
		alert(searchStr);

		var	URL		 = 'etrans/ETLOVTerminalLocations.jsp?&originTerminalId='+originTerminal+'&searchStr='+searchStr+'&forWhat='+name1+'&row='+row+'&shipmentMode='+shipmentMode;
		var	Bars	 = 'directories=no,	location=no, menubar=no, status=no,	titlebar=no';
		var	Options	 = 'scrollbars=yes,width=450,height=400,resizable=no';
		var	Features = Bars+' '+Options;
		var	Win		 = open(URL,'Doc',Features);
		if(Win.opener != null)
			Win.opener = self
		if(Win != null)
			Win.focus();
	}
	function showMasterDocsLOV(obj)
	{
		name1 = obj.name;
		row = obj.row;
		var searchStr = '';
		var shipmentMode = '';
		var originTerminal = '';
		var destinationTerminal = '';
		var lent;

		if(name1 == 'olmasDoc')
		{
			originTerminal		= document.forms[0].oloriginId.value;	 
			destinationTerminal	= document.forms[0].oldestinationId.value;
			shipmentMode = document.forms[0].olshipmentMode.value;
			searchStr = document.forms[0].olmasterDocNo.value;
		}
		if(name1 == 'otmasDoc')
		{
			originTerminal		= document.forms[0].otoriginId.value;	 
			destinationTerminal	= document.forms[0].ogoriginId.options[document.forms[0].ogoriginId.options.selectedIndex].value;

			if(originTerminal == destinationTerminal)
			{
				if(document.forms[0].originId != null)
				{
					lent = document.forms[0].originId.length;
					if(lent > 1)
						destinationTerminal	= document.forms[0].originId[row].value;
					else destinationTerminal	= document.forms[0].originId.value;
				}
				else
				{
					destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				}
			}

			shipmentMode = document.forms[0].otshipmentMode.value;
			searchStr = document.forms[0].otmasterDocNo.value;
		}
		else if(name1 == 'ogmasDoc')
		{
			originTerminal		= document.forms[0].ogoriginId.value;	 
			if(document.forms[0].originId != null)
			{
				lent = document.forms[0].originId.length;
				if(lent > 1)
					destinationTerminal	= document.forms[0].originId[row].value;
				else destinationTerminal	= document.forms[0].originId.value;
			}
			else
			{
				destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
			}

			shipmentMode = document.forms[0].ogshipmentMode.value;
			searchStr = document.forms[0].ogmasterDocNo.value;
		}
		else if(name1 == 'dgmasDoc')
		{
			originTerminal = document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;

			if(originTerminal == document.forms[0].dtoriginId.value)
			{
				destinationTerminal	= document.forms[0].dtdestinationId.value;
			}
			else
			{
				destinationTerminal	= document.forms[0].dgdestinationId.value;
			}
			shipmentMode = document.forms[0].dgshipmentMode.value;
			searchStr = document.forms[0].dgmasterDocNo.value;
		}
		else if(name1 == 'dtmasDoc')
		{
			originTerminal		= document.forms[0].dtoriginId.value;	 
			destinationTerminal	= document.forms[0].dtdestinationId.value;
			shipmentMode = document.forms[0].dtshipmentMode.value;
			searchStr = document.forms[0].dtmasterDocNo.value;
		}
		else if(name1 == 'masDoc')
		{
			lent = document.forms[0].masterDocNo.length;
			row = obj.row

			if(lent > 1)
			{
				originTerminal = document.forms[0].originId[row].value;

				if(row == (lent -1))
				{
					destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				}
				else
				{
					destinationTerminal	= document.forms[0].originId[parseInt(row)+1].value;
				}
				shipmentMode = document.forms[0].shipmentMode[row].value;
				searchStr = document.forms[0].masterDocNo[row].value;
			}
			else
			{
				originTerminal = document.forms[0].originId.value;
				destinationTerminal	= document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
				shipmentMode = document.forms[0].shipmentMode.value;
				searchStr = document.forms[0].masterDocNo.value;
			}
		}

		var	URL		 = 'etrans/ETLOVMultiModeRoutePlan.jsp?forWhat='+name1+'&searchStr='+searchStr+'&row='+row+'&shipmentMode='+shipmentMode+'&originTerminalId='+originTerminal+'&destinationTerminalId='+destinationTerminal;
		var	Bars	 = 'directories=no,	location=no, menubar=no, status=no,	titlebar=no';
		var	Options	 = 'scrollbars=yes,width=450,height=400,resizable=no';
		var	Features = Bars+' '+Options;
		var	Win		 = open(URL,'Doc',Features);
		if(Win.opener != null)
			Win.opener = self
		if(Win != null)
			Win.focus();
	}

	function changeValue(val,i)
	{
		if(i<=0)
			document.forms[0].originIdTemp.value = val.value;
		else
			document.forms[0].originIdTemp[i].value = val.value;
	}

	function showLOVForGatewayIds(obj)
	{
		name1 = obj.name;
		row = obj.row;

		var documentType = '<%=documentType%>';
		var searchStr	 = '';
		lent = document.forms[0].masterDocNo.length;
		if(lent > 1)
			searchStr = document.forms[0].originId[row].value;
		else searchStr = document.forms[0].originId.value;
		var	URL		 = 'etrans/ETLOVMultiModeRoutePlan.jsp?forWhat='+name1+'&searchStr='+searchStr+'&documentType='+documentType+'&row='+row;
		var	Bars	 = 'directories=no,	location=no, menubar=no, status=no,	titlebar=no';
		var	Options	 = 'scrollbars=yes,width=450,height=400,resizable=no';
		var	Features = Bars+' '+Options;
		var	Win		 = open(URL,'Doc',Features);
		if(Win.opener != null)
			Win.opener = self
		if(Win != null)
			Win.focus();
	}

	function removeValue(obj)
	{
		var name1 = obj.name;

		if(name1 == 'ogshipmentMode')
		{
			document.forms[0].ogmasterDocNo.value = '';
		}
		else if(name1 == 'dgshipmentMode')
		{
			document.forms[0].dgmasterDocNo.value = '';
		}
		else if(name1 == 'dtshipmentMode')
		{
			document.forms[0].dtmasterDocNo.value = '';
		}
		else if(name1 == 'shipmentMode')
		{
			row = obj.row;
			var lent = document.forms[0].masterDocNo.length;
			if(lent > 1)
			{
				document.forms[0].masterDocNo[row].value = '';
			}
			else
			{
				document.forms[0].masterDocNo.value = '';
			}
		}
	}
	function flush(val)
	{
		  document.forms[0].ogmasterDocNo.value ="";
	}
	

	function toUpperCase(field)
	{
		field.value	= field.value.toUpperCase();
	}

	function validateForm()
	{
		

		if(document.forms[0].destinationId != null)
		{

			if(document.forms[0].originId.value !=null)
			{
				flag=false;
				for(loop = 0; loop<idsGate.length; loop++)
				{
					if(idsGate[loop] == document.forms[0].originId.value )
					{
						flag=true;
						break;
					}
				}
				if(! flag)
				{
					alert("Please select a valid Gateway Id  " );
					document.forms[0].originId.focus();
					return false;			
				}

			}
			var lent = document.forms[0].masterDocNo.length;

			if(lent > 1)
			{
				for(var i=0; i<lent; i++)
				{
					if(document.forms[0].originId[i].value == 0)
					{
						alert('Please Enter Gateway Or Terminal Id in row '+(i+1));
						document.forms[0].originId[i].focus();
						return false;
					}
					else
					{
						flag=false;
						for(loop = 0; loop<idsGate.length; loop++)
						{
							if(idsGate[loop] == document.forms[0].originId[i].value )
							{
								flag=true;
								break;
							}
						}
							if(! flag)
							{
								alert("Please select a valid Gateway Id  " );
								document.forms[0].originId[i].focus();
								return false;			
							}
						
					}
					for(var j=i+1; j<lent; j++)
					{
						if(document.forms[0].originId[i].value == document.forms[0].originId[j].value)
						{
							alert('Gateway Or Terminal Id can not be duplicated.');
							document.forms[0].originId[j].focus();
							return false;
						}
					}
					if(document.forms[0].originId[i].value == document.forms[0].oloriginId.value)
					{
						alert('Origin Location and Transhipment terminal/gateway id can not be same.');
						document.forms[0].originId[i].focus();
						return false;
					}
					if(document.forms[0].originId[i].value == document.forms[0].otoriginId.value)
					{
						alert('Origin Terminal and Transhipment terminal/gateway id can not be same.');
						document.forms[0].originId[i].focus();
						return false;
					}
					if(document.forms[0].originId[i].value == document.forms[0].ogoriginId.value)
					{
						alert('Origin Gateway and Transhipment terminal/gateway id can not be same.');
						document.forms[0].originId[i].focus();
						return false;
					}
					if(document.forms[0].originId[i].value == document.forms[0].dgoriginId.value)
					{
						alert('Destination Gateway and Transhipment terminal/gateway id can not be same.');
						document.forms[0].originId[i].focus();
						return false;
					}
					if(document.forms[0].originId[i].value == document.forms[0].dtoriginId.value)
					{
						alert('Destination Terminal and Transhipment terminal/gateway id can not be same.');
						document.forms[0].originId[i].focus();
						return false;
					}
				}
			}
			else
			{
				if(document.forms[0].originId.value == 0)
				{
					alert('Please Enter Gateway Or Terminal Id.');
					document.forms[0].originId.focus();
					return false;
				}
				if(document.forms[0].originId.value == document.forms[0].oloriginId.value)
				{
					alert('Origin Location and Transhipment terminal/gateway id can not be same.');
					document.forms[0].originId.focus();
					return false;
				}
				if(document.forms[0].originId.value == document.forms[0].otoriginId.value)
				{
					alert('Origin Terminal and Transhipment terminal/gateway id can not be same.');
					document.forms[0].originId.focus();
					return false;
				}
				if(document.forms[0].originId.value == document.forms[0].ogoriginId.value)
				{
					alert('Origin Gateway and Transhipment terminal/gateway id can not be same.');
					document.forms[0].originId.focus();
					return false;
				}
				if(document.forms[0].originId.value == document.forms[0].dgoriginId.value)
				{
					alert('Destination Gateway and Transhipment terminal/gateway id can not be same.');
					document.forms[0].originId.focus();
					return false;
				}
				if(document.forms[0].originId.value == document.forms[0].dtoriginId.value)
				{
					alert('Destination Terminal and Transhipment terminal/gateway id can not be same.');
					document.forms[0].originId.focus();
					return false;
				}
			}
		}

		var otOriginId = document.forms[0].otoriginId.value;
		var ogOriginId = document.forms[0].ogoriginId.value;
		
		if(otOriginId == ogOriginId)
		{
			

			document.forms[0].oglegType.value = 'TP';
			if(document.forms[0].destinationId != null)
			{
				var lent = document.forms[0].masterDocNo.length;

				if(lent > 1)
					document.forms[0].ogdestinationId.value = document.forms[0].originId[0].value
				else
					document.forms[0].ogdestinationId.value = document.forms[0].originId.value
			}
			else
				document.forms[0].ogdestinationId.value = document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;

		}
		else
		{
			document.forms[0].otlegType.value = 'TG';
			document.forms[0].otdestinationId.value = document.forms[0].ogoriginId.value;

			if(document.forms[0].destinationId != null)
			{
				var lent = document.forms[0].masterDocNo.length;

				if(lent > 1)
					document.forms[0].ogdestinationId.value = document.forms[0].originId[0].value
				else
					document.forms[0].ogdestinationId.value = document.forms[0].originId.value
			}
			else
				document.forms[0].ogdestinationId.value = document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;
		}

		var dtOriginId = document.forms[0].dtoriginId.value;
		var dgOriginId = document.forms[0].dgoriginId.value;
		if(dtOriginId == dgOriginId)
		{
			document.forms[0].dglegType.value = 'TL';
			document.forms[0].dgdestinationId.value = document.forms[0].destinationLocationId.value;
		}
		else
		{
			document.forms[0].dglegType.value = 'GT';
			document.forms[0].dtlegType.value = 'TL';
			document.forms[0].dgdestinationId.value = document.forms[0].dtoriginId.value;
			document.forms[0].dtdestinationId.value = document.forms[0].destinationLocationId.value;
		}

		if(document.forms[0].destinationId != null)
		{
			var lent = document.forms[0].masterDocNo.length;

			if(lent > 1)
			{
				for(var i=0; i<lent; i++)
				{
					if( i == lent-1)
						document.forms[0].destinationId[i].value = document.forms[0].dgoriginId.value;
					else
						document.forms[0].destinationId[i].value = document.forms[0].originId[parseInt(i)+1].value
				}
			}
			else
				document.forms[0].destinationId.value = document.forms[0].dgoriginId.options[document.forms[0].dgoriginId.options.selectedIndex].value;			
		}

		if( (document.forms[0].ogoriginId != null && document.forms[0].ogoriginIdTemp != null))
		{
			if(document.forms[0].ogoriginId.type != "hidden")
			{
				if(document.forms[0].ogoriginId.options[document.forms[0].ogoriginId.options.selectedIndex].value != document.forms[0].ogoriginIdTemp.value)
					document.forms[0].consoleAttach.value = "Changed";
			}
			else
			{
				if(document.forms[0].ogoriginId.value != document.forms[0].ogoriginIdTemp.value)
					document.forms[0].consoleAttach.value = "Changed";
			}
		}
		else if(document.forms[0].originId != null)
		{
			document.forms[0].consoleAttach.value = "Changed";
		}
		document.forms[0].submit.disabled='true';
	}


	function gateWayIdsCheck(val)
	{
		var flag = true;
		if(idsGate!=null && idsGate.length>0)
		{
			for(loop=0;loop<idsGate.length; loop++)
			{
				if(idsGate[loop] == val.value || val.value == '')
				{
					flag = false;
				}
			}
		}

		if(flag)
		{
			alert('Please select a valid Gateway Id');
			val.focus();
			return false;
		}
		else
			return true;
	}


	function showTranshipments(obj)
	{
		obj.disabled = true;
		if(number == 0)
			number = 1;
		createForm(number);
	}

	function  changeStatus()
	{
		if(document.forms[0].otdestinationId.value == document.forms[0].ogdestinationId.value)
		{
			document.forms[0].ogmasterDocNo.readOnly = true;
			if(document.forms[0].ogmasDoc != null)
				document.forms[0].ogmasDoc.disabled = true;
		}
		else
		{
			document.forms[0].ogmasterDocNo.readOnly = false;
			if(document.forms[0].ogmasDoc != null)
				document.forms[0].ogmasDoc.disabled = false;
		}

		if(document.forms[0].dtdestinationId.value == document.forms[0].dgdestinationId.value)
		{
			document.forms[0].dtmasterDocNo.readOnly = true;
			if(document.forms[0].dtmasDoc != null)
				document.forms[0].dtmasDoc.disabled = true;
		}
		else
		{
			document.forms[0].dtmasterDocNo.readOnly = false;
			if(document.forms[0].dtmasDoc != null)
				document.forms[0].dtmasDoc.disabled = false;
		}
	}
	var prevInd =	0;

	function confirmSM(obj){
	prevInd = obj.selectedIndex;

	}
	function confirmSMode(obj)
	{
		
				var tmpval = obj.value
				var field	=	obj.name;
				var ind = obj.selectedIndex;
				var SMorNot=confirm('Select shipment mode is different from the mode of the shipment'+'\n'+' Do you wish to continue?');
				if(!SMorNot)
				{
					obj.selectedIndex	=	prevInd;

				}

	}
	function showLocation(obj)
	{
		if(obj.name == 'ogoriginId')
		{
			if(obj.type == 'select-one')
				ogIdSpan.innerHTML = obj.options[obj.options.selectedIndex].id;
			else ogIdSpan.innerHTML = obj.id;
		}
		else if(obj.name == 'dgoriginId')
		{
			if(obj.type == 'select-one')
				dgIdSpan.innerHTML = obj.options[obj.options.selectedIndex].id;
			else dgIdSpan.innerHTML = obj.id;
		}
	}

	var otAutoManual='<%=otAutoManual%>';
	var dgAutoManual='<%=dgAutoManual%>';
	
	function setAutoManual(obj)
	{
		if(obj.name == 'otshipmentMode')
		{
			if(document.forms[0].otautoManual.type == 'select-one')
			{
				document.forms[0].otautoManual.options.length=0;
				if(obj.value == "Air")
				{
					document.forms[0].otautoManual.options[0]=new Option("Auto","A");
					 document.forms[0].otmasterDocNo.value = "";
				}
				else if(obj.value == "Truck")
				{
					document.forms[0].otmasterDocNo.value = "";
					document.forms[0].otautoManual.options[0]=new Option("Auto","A");
					document.forms[0].otautoManual.options[1]=new Option("Manual","M");
				    
                    if(otAutoManual=='M')
					{
				        document.forms[0].otautoManual.options[1].selected = true;
						disableMaster(document.forms[0].otautoManual);
                    }
					else
					    document.forms[0].otautoManual.options[0].selected = true; 

				  
				}
			}
		}
		else if(obj.name == 'dgshipmentMode')
		{
			if(document.forms[0].dgautoManual.type == 'select-one')
			{
				document.forms[0].dgautoManual.options.length=0;
				if(obj.value == "Air")
				{
					document.forms[0].dgautoManual.options[0]=new Option("Auto","A");
				}
				else if(obj.value == "Truck")
				{
					document.forms[0].dgautoManual.options[0]=new Option("Auto","A");
					document.forms[0].dgautoManual.options[1]=new Option("Manual","M");
				
				    if(dgAutoManual=='M')
				        document.forms[0].dgautoManual.options[1].selected = true;
                    else
					    document.forms[0].dgautoManual.options[0].selected = true; 
				
				}
			}
		}
	}

	var otAutoManual='<%=otAutoManual%>';
	var dgAutoManual='<%=dgAutoManual%>';
	

	function setAutoManualOnLoad(obj)
	{
		if(obj.name == 'otshipmentMode')
		{
			if(document.forms[0].otautoManual.type == 'select-one')
			{
				document.forms[0].otautoManual.options.length=0;
				if(obj.value == "Air")
				{
					document.forms[0].otautoManual.options[0]=new Option("Auto","A");

				}
				else if(obj.value == "Truck")
				{
					document.forms[0].otautoManual.options[0]=new Option("Auto","A");
					document.forms[0].otautoManual.options[1]=new Option("Manual","M");
				    
                    if(otAutoManual=='M')
					{
				        document.forms[0].otautoManual.options[1].selected = true;
						disableMaster(document.forms[0].otautoManual);
                    }
					else
					    document.forms[0].otautoManual.options[0].selected = true; 

				  
				}
			}
		}
		else if(obj.name == 'dgshipmentMode')
		{
			if(document.forms[0].dgautoManual.type == 'select-one')
			{
				document.forms[0].dgautoManual.options.length=0;
				if(obj.value == "Air")
				{
					document.forms[0].dgautoManual.options[0]=new Option("Auto","A");
				}
				else if(obj.value == "Truck")
				{
					document.forms[0].dgautoManual.options[0]=new Option("Auto","A");
					document.forms[0].dgautoManual.options[1]=new Option("Manual","M");
				
				    if(dgAutoManual=='M')
				        document.forms[0].dgautoManual.options[1].selected = true;
                    else
					    document.forms[0].dgautoManual.options[0].selected = true; 
				
				}
			}
		}
	}


	function loadValue()
	{

		
		var ogmStatus1 = document.forms[0].ogmasterStatus.value;

	}

	function checkOriginIds(obj)
	{
		if(obj.name == 'ogoriginId')
		{
			if(document.forms[0].ogoriginId.value == document.forms[0].otoriginId.value)
			{
				document.forms[0].otshipmentMode.disabled = true;
				document.forms[0].otautoManual.disabled = true;
				document.forms[0].otmasterDocNo.disabled = true;
				if(document.forms[0].otmasDoc!=null)
					document.forms[0].otmasDoc.disabled = true;
				document.forms[0].otremarks.disabled = true;
				
			}
			else
			{
				var selectedOption = document.forms[0].otautoManual.selectedIndex;
				if(selectedOption !=1)
				 {
					 
					 

					 document.forms[0].otmasterDocNo.disabled=true;
					 if(document.forms[0].otmasDoc!=null)
					 document.forms[0].otmasDoc.disabled = true;
			 
						document.forms[0].otshipmentMode.disabled = false;
						document.forms[0].otautoManual.disabled = false;
						document.forms[0].otmasterDocNo.disabled = false;
						if(document.forms[0].otmasDoc!=null)
							document.forms[0].otmasDoc.disabled = false;
						document.forms[0].otremarks.disabled = false; 

				}
				else
				{
					 
					

					 document.forms[0].otmasterDocNo.disabled=true;
					 if(document.forms[0].otmasDoc!=null)
					 document.forms[0].otmasDoc.disabled = true;
			 
						document.forms[0].otshipmentMode.disabled = false;
						document.forms[0].otautoManual.disabled = false;
						document.forms[0].otmasterDocNo.disabled = true;
						if(document.forms[0].otmasDoc!=null)
							document.forms[0].otmasDoc.disabled = true;
						document.forms[0].otremarks.disabled = true; 

				}

			}
		}
		if(obj.name == 'dgoriginId')
		{
			if(document.forms[0].dgoriginId.value == document.forms[0].dtoriginId.value)
			{
				document.forms[0].dgshipmentMode.disabled = true;
				document.forms[0].dgautoManual.disabled = true;
				document.forms[0].dgmasterDocNo.disabled = true;
				if(document.forms[0].dgmasDoc!=null)
					document.forms[0].dgmasDoc.disabled = true;
				document.forms[0].dgremarks.disabled = true;
			}
			else
			{
				document.forms[0].dgshipmentMode.disabled = false;
					document.forms[0].dgautoManual.disabled = false;
                <%if(!dgAutoManual.equals("M")){%> 
				if(document.forms[0].dgmasDoc!=null)
					document.forms[0].dgmasDoc.disabled = false;
    			    document.forms[0].dgmasterDocNo.disabled = false;
				<%}else{%>
					document.forms[0].dgmasDoc.disabled = true;
				    document.forms[0].dgmasterDocNo.disabled = true;
                <%}%>

			}
		}
	}
	
	
	function disableMaster(obj)
	{
	   if(obj.name=='otautoManual')
	   {
	      var selectedOption = document.forms[0].otautoManual.selectedIndex;
	      if(selectedOption==1)
		  {
		     document.forms[0].otmasterDocNo.value = "";
			 document.forms[0].otmasterDocNo.disabled=true;
             	if(document.forms[0].otmasDoc!=null)
					document.forms[0].otmasDoc.disabled = true;
		 }
	     else
		 {
		     document.forms[0].otmasterDocNo.disabled=false;
             	if(document.forms[0].otmasDoc!=null)
					document.forms[0].otmasDoc.disabled = false;
		 
		 }
	 }
     else if(obj.name=='dgautoManual')
	 {
	    var selectedOption = document.forms[0].dgautoManual.selectedIndex;
	      if(selectedOption==1)
		  {
		     document.forms[0].dgmasterDocNo.value = "";
			 document.forms[0].dgmasterDocNo.disabled=true;
             	if(document.forms[0].dgmasDoc!=null)
					document.forms[0].dgmasDoc.disabled = true;
		 }
	     else
		 {
		     document.forms[0].dgmasterDocNo.disabled=false;
             	if(document.forms[0].otmasDoc!=null)
					document.forms[0].dgmasDoc.disabled = false;
		 
		 } 
	 
	 
	 }
   
 
 }
	
	
	</script>
</head>
<body  onLoad='<%=onLoad%>loadValue();setAutoManualOnLoad(document.forms[0].otshipmentMode);setAutoManualOnLoad(document.forms[0].dgshipmentMode);showLocation(document.forms[0].ogoriginId);showLocation(document.forms[0].dgoriginId);checkOriginIds(document.forms[0].ogoriginId);checkOriginIds(document.forms[0].dgoriginId);'>
	<form method='POST' action='ETMultiModeRoutePlanController' onSubmit='return validateForm()'>
	<input type="hidden" name="consoleAttach" value=''>
	<input type=hidden name='terminalId' value ='<%=terminalId%>'>
	<input type=hidden name='Operation' value='<%=operation%>'>
	<input type=hidden name='documentType' value='<%=documentType%>'>
	<input type=hidden name='ShipmentMode' value='<%=shipmentMode%>'>
	<input type=hidden name='SubOperation' value='Detail'>

	<input type=hidden name='routePlanId'  value='<%=routePlanId%>'>
	<input type=hidden name='prqNo' value='<%=prqNo%>'>
	<input type=hidden name='hawbNo' value='<%=hawbNo%>'>
	<input type=hidden name='originLocationId' value='<%=originLocationId%>'>
	<input type=hidden name='destinationLocationId' value='<%=destinationLocationId%>'>
	<input type=hidden name='originLocationName' value='<%=originLocationName%>'>
	<input type=hidden name='destinationLocationName' value='<%=destinationLocationName%>'>
	<input type=hidden name='shipperId' value='<%=shipperId%>'>
	<input type=hidden name='consigneeId' value='<%=consigneeId%>'>
	<input type=hidden name='primaryMode' value='<%=primaryMode%>'>
	<input type=hidden name='originTerminalId' value = '<%=originTerminalId%>'>
	<input type=hidden name='destinationTerminalId' value='<%=destinationTerminalId%>'>
	<input type=hidden name='originTerminalLocation' value = '<%=originTerminalLocation%>'>
	<input type=hidden name='destinationTerminalLocation' value='<%=destinationTerminalLocation%>'>
	<input type=hidden name='createDate' value='<%=createDate%>'>
	<input type=hidden name='shipmentStatus' value='<%=shipmentStatus%>'>
	<input type=hidden name='ShipmentMode' value='<%=shipmentMode%>'>

	<input type=hidden name='serviceLevelId' value = '<%=serviceLevelId%>'>
	<input type=hidden name='serviceLevelDesc' value='<%=serviceLevelDesc%>'>
	<input type=hidden name='deliveryTerms' value='<%=deliveryTerms%>'>
	<input type=hidden name='totalPieces' value='<%=totalPieces%>'>
	<input type=hidden name='weight' value='<%=weight%>'>

	<table width="800" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top" bgcolor="#FFFFFF">
				<table width="800" border="0" cellspacing="1" cellpadding="4">
					<tr class='formlabel'> 
						<td width='800' colspan="4"><table width="790" border="0" ><tr class='formlabel'>
						<% // @@ Modified by Sailaja on 2005 04 27 for SPETI-6440 %>
						<% // <td> Routing Plan - Add</td>%>
						<td>Route Plan - Add</td>
						<% // @@ 2005 04 27 for SPETI-6440 %>
						<td align=right></td></tr></table></td>
						</tr>
<%
		if(errors != null)
		{
%>
					<tr class='formdata'> 
						<td colspan=4><font color=red><b><%=errors%></b></font></td>
					</tr>
<%
		}
%>
					<tr class='formdata'> 
						<td width="33%" align='center'><%=docLabel%>:<br><%=docNo%></td>
						<td width="33%" align='center'>Primary Shipment Mode:<br><%=shipMode%></td>
						<td width="34%" align='center'>Delivery Terms:<br><%=deliveryTerms!=null?deliveryTerms:""%></td>
					</tr>
					<tr class='formdata'> 
						<td width="33%">Overwrite RoutePlan? <input type=checkbox name='overWrite' value='Y' <%=checked%>></td>
						<td width="33%" align='center'>Service Level :<br><%=serviceLevelDesc!=null?serviceLevelDesc:""%>(<%=serviceLevelId!=null?serviceLevelId:""%>)</td>
						<td width="34%" align='center'>Pieces/Weight(Kg):<br><%=totalPieces%>/<%=weight%></td>
					</tr>
				</table>
				<table width="800" border="0" cellspacing="1" cellpadding="4">
					<tr class='formheader'> 
						<td width='10%' align='center'>Entity</td>
						<td width='5%' align='center'>-</td>
						<td width='10%' align='center'>Location</td>
						<td width='10%' align='center'>Terminal ID</td>
						<td width='10%' align='center'>Mode</td>
						<td width='10%' align='center'>Flag</td>
						<td width='20%' align='center'>Location ID</td>
						<td width='5%' align='center'>-</td>
						<td width='10%' align='center' >Remarks</td>
						<td width='10%' align='center'><font size=1></font></td>
					</tr>
					<tr class='formdata'> 
						<input type=hidden name='olmasterStatus' value='<%=olMasterStatus%>'>
						<input type=hidden name='ollegType' value='LT'>
						<input type=hidden name='oloriginId' value='<%=originLocationId%>'>
						<input type=hidden name='oloriginIdTemp' value='<%=originLocationId%>'>
						<input type=hidden name='oldestinationId' value='<%=originTerminalId%>'>
						<input type=hidden name='ollegValidFlag' value='<%=olLegValidFlag%>'>
						<input type=hidden name='olcostAmount' value='<%=olcostAmount%>'>
	
						<input type=hidden name='olpiecesReceived' value='<%=olPiecesReceived%>'>
						<input type=hidden name='olreceivedDate' value='<%=olReceivedDate%>'>
						<input type=hidden name='olcarrier' value='<%=olCarrier%>'>
						<input type=hidden name='olETD' value='<%=olETD%>'>
						<input type=hidden name='olETA' value='<%=olETA%>'>

<%
	//Logger.info(FILE_NAME, " Flag ***************** -> "+staticRow);
  logger.info(FILE_NAME+ " Flag ***************** -> "+staticRow);
	if(terminalId.equals(originLocationId))
		staticRow = false;
	//Logger.info(FILE_NAME, " Flag ***************** -> "+staticRow);
  logger.info(FILE_NAME+ " Flag ***************** -> "+staticRow);

	if(!(staticRow || terminalId.equals(originLocationId))) 
	{

%>
						<td width='10%' align='center'  class='formheader'><font size=1>Shipper</font></td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><%=originLocationName%> (<%=originLocationId%>)</font></td>
						<td width='10%' align='center'><%=shipperId%></td>
						<td width='10%' align='center'>
							<select name ='olshipmentMode' size=1 class='select'>
								<option value='Truck'>Truck</option>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='olautoManual' size=1 class='select' onChange='disableMaster(this)'>
<%
		
	    if("A".equals(olAutoManual))
		{
%>
								<option value='A' selected>Auto</option>
<%
		}
		else 
		{
%>
								<option value='A'>Auto</option>
<%
		}
		if("M".equals(olAutoManual))
		{
%>
								<option value='M' selected>Manual</option>
<%
		}
		else 
		{
%>
								<option value='M'>Manual</option>
<%
		}
%>
							</select>
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='olmasterDocNo' value='<%=olMasterDocNo%>' maxlength="25" size=12><%=olMasterDocNo%>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=text class='text' name='olremarks' size=10 value='<%=olRemarks%>'></td>
<%
	}
	else
	{
		String tmp = "";
		if("A".equals(olAutoManual))
			tmp = "Auto";
		else tmp = "Manual";
%>
						<td width='10%' align='center'  class='formheader'><font size=1>Shipper</font></td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><%=originLocationName%> (<%=originLocationId%>)</font></td>
						<td width='10%' align='center'><%=shipperId%></td>
						<td width='10%' align='center'><input type=hidden name='olshipmentMode' value='Truck'>Truck</td>
						<td width='10%' align='center'><input type=hidden name='olautoManual' value='<%=olAutoManual%>'><%=tmp%></td>
						<td width='20%' align='center'><%=olMasterDocNo%>
							<input type=hidden name='olmasterDocNo' value='<%=olMasterDocNo%>'>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=hidden name='olremarks' size=10 value='<%=olRemarks%>'><%=olRemarks%></td>

<%
	}
%>
						<td width='10%' align='center'></td
						<td width='10%' align='center'><font size=1><%=olCarrierData%></font></td>
					</tr>	
					<tr class='formdata'> 
						<input type=hidden name='otmasterStatus' value='<%=otMasterStatus%>'>
						<input type=hidden name='otlegType' value='TG'>
						<input type=hidden name='otoriginId' value='<%=originTerminalId%>'>
						<input type=hidden name='otoriginIdTemp' value='<%=originTerminalId%>'>
						<input type=hidden name='otdestinationId' value='<%=otdestinationId%>'>
						<input type=hidden name='otlegValidFlag' value='<%=otLegValidFlag%>'>
						<input type=hidden name='otcostAmount' value='<%=otcostAmount%>'>
	
						<input type=hidden name='otpiecesReceived' value='<%=otPiecesReceived%>'>
						<input type=hidden name='otreceivedDate' value='<%=otReceivedDate%>'>
						<input type=hidden name='otcarrier' value='<%=otCarrier%>'>
						<input type=hidden name='otETD' value='<%=otETD%>'>
						<input type=hidden name='otETA' value='<%=otETA%>'>

						<td width='10%' align='center' class='formheader'><font size=1>Origin Terminal</font></td>
						<td width='5%' align='center'></td>
<%
	if(terminalId.equals(originTerminalId))
		staticRow = false;
	if((staticRow)) 
	{

%>
							<td width='10%' align='center'><font size=1><%=originTerminalLocation%></font></td>
							<td width='10%' align='center'><%=originTerminalId%></td>
						<td width='10%' align='center'>
							<select name ='otshipmentMode' size=1 class='select'  onFocus='confirmSM(this)' onChange='setAutoManual(this);confirmSMode(this)'>
<%
		if(otShipmentMode == 1)
		{
%>
								<option value='Air' selected>Air</option>
<%
		}
		else
		{
%>
								<option value='Air' >Air</option>
<%
		}
		if(otShipmentMode == 4)
		{
%>
								<option value='Truck' selected>Truck</option>
<%

		}
		else
		{
%>
								<option value='Truck' >Truck</option>
<%

		}
%>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='otautoManual' size=1 class='select' onChange='disableMaster(this)'>
<%
		if("A".equals(otAutoManual))
		{
%>
								<option value='A' selected>Auto</option>
<%
		}
		else
		{
%>
								<option value='A'>Auto</option>
<%
		}
		if("Manual".equals(otAutoManual))
		{
%>
								<option value='M' selected>Manual</option>
<%
		}
		else
		{
%>
								<option value='M'>Manual</option>
<%
		}
%>
							</select>
						</td>
						<td width='20%' align='center'>
							<input type=text class='text' name='otmasterDocNo' value='<%=otMasterDocNo%>' size=12>
							<input type=button name='otmasDoc' value='...' class='input' row=0 onClick='showLocationsLOV(this)'>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=text class='text' name='otremarks' value='<%=otRemarks%>' size=10></td>
<%
	}
	else
	{
		String shMode = "";

		if(otShipmentMode == 1)
			shMode = "Air";
		if(otShipmentMode == 2)
			shMode = "Sea";
		if(otShipmentMode == 4)
			shMode = "Truck";

		String tmp = "";
		if("A".equals(otAutoManual))
			tmp = "Auto";
		else if("AssignedToGateway".equals(otAutoManual))
			tmp = "";
		else 
			tmp = "Manual";
%>
							<td width='10%' align='center'><font size=1><%=originTerminalLocation%></font></td>
							<td width='10%' align='center'><%=originTerminalId%></td>

						<td width='10%' align='center'><input type=hidden name='otshipmentMode' value='<%=shMode%>'><%=shMode%>
						</td>
						<td width='10%' align='center'><input type=hidden name='otautoManual' value='<%=otAutoManual%>'><%=tmp%>
						</td>
						<td width='20%' align='center'><%=otMasterDocNo%>
							<input type=hidden name='otmasterDocNo' value='<%=otMasterDocNo%>' size=12>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' colspan='2'><input type=hidden name='otremarks' value='<%=otRemarks%>' size=10><%=otRemarks%></td>
<%
	}
%>
						<td width='10%' align='center'><font size=1><%=otCarrierData%></font></td>
					</tr>	
					<tr class='formdata'> 
						<td width='10%' align='center' class='formheader'><font size=1>Origin Gateway</font></td>
						<input type=hidden name='ogmasterStatus' value='<%=ogMasterStatus%>'>
						<input type=hidden name='oglegType' value='TP'>
						<input type=hidden name='ogdestinationId' value='<%=ogdestinationId%>'>
						<input type=hidden name='oglegValidFlag' value='<%=ogLegValidFlag%>'>
						<input type=hidden name='ogcostAmount' value='<%=ogcostAmount%>'>

						<input type=hidden name='ogpiecesReceived' value='<%=ogPiecesReceived%>'>
						<input type=hidden name='ogreceivedDate' value='<%=ogReceivedDate%>'>
						<input type=hidden name='ogcarrier' value='<%=ogCarrier%>'>
						<input type=hidden name='ogETD' value='<%=ogETD%>'>
						<input type=hidden name='ogETA' value='<%=ogETA%>'>


						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><span id='ogIdSpan'><%=ogLocationName%></span></font></td>
<%
	if(terminalId.equals(ogoriginId))
		staticRow = false;
	
	if((staticRow)) 
	{
    if (ogMasterDocNo != null &&  ! ogMasterDocNo.equals("") && staticRow )
    mastdisable="disabled";

%>
						<td width='10%' align='center'>
							<input type=hidden name='ogoriginIdTemp' value='<%=ogoriginId%>'>
							<select name='ogoriginId' size=1 class='select'  onChange='showLocation(this);checkOriginIds(this)' <%=mastdisable%>  >
<%
			for(int i=0; i< originGatewayIds.length; i++)
			{
				if(ogoriginId.equals(originGatewayIds[i][0]))
				{
%>
								<option id='<%=originGatewayIds[i][1]%>' value='<%=originGatewayIds[i][0]%>' selected><%=originGatewayIds[i][0]%></option>
<%
				}
				else
				{
%>
								<option id='<%=originGatewayIds[i][1]%>' value='<%=originGatewayIds[i][0]%>'><%=originGatewayIds[i][0]%></option>
<%
				}
			}
%>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='ogshipmentMode' size=1 class='select'  onFocus='confirmSM(this)' onChange='flush(this);confirmSMode(this)'>
<%
		if(ogShipmentMode == 1)
		{
%>
								<option value='Air' selected>Air</option>
<%
		}
		else
		{
%>
								<option value='Air' >Air</option>
<%
		}
		if(ogShipmentMode == 2)
		{
%>
								<option value='Sea' selected>Sea</option>
<%
		}
		else
		{
%>
								<option value='Sea' >Sea</option>
<%
		}
		if(ogShipmentMode == 4)
		{
%>
								<option value='Truck' selected>Truck</option>
<%

		}
		else
		{
%>
								<option value='Truck' >Truck</option>
<%

		}
%>
							</select>
						</td>
						<td width='10%' align='center'>
							<input type=hidden name ='ogautoManual' value='A'>Auto
						</td>
						<td width='20%' align='center'>
							<input type=text class='text' name='ogmasterDocNo' value='<%=ogMasterDocNo%>' size=12>
							<input type=button name='ogmasDoc' value='...' class='input' row=0 onClick='showLocationsLOV(this)'>
						</td>
						<td width='5%' align='center'><input type=button name='tranship' value='>>' onClick='showTranshipments(this)' class='input'  <%=disabled%>></td>
						<td width='10%' align='center' ><input type=text class='text' name='ogremarks' value='<%=ogRemarks%>' size=10></td>
<%
	}
    else
	{
		String shMode = "";

		if(ogShipmentMode == 1)
			shMode = "Air";
		if(ogShipmentMode == 2)
			shMode = "Sea";
		if(ogShipmentMode == 4)
			shMode = "Truck";
%>
						<td width='10%' align='center'>
<%
			String ogName = "";
			for(int i=0; i< originGatewayIds.length; i++)
			{
				if(ogoriginId.equals(originGatewayIds[i][0]))
				{
					ogName = originGatewayIds[i][1];
				}
			}
%>
							<input type=hidden name='ogoriginId' value='<%=ogoriginId%>' id='<%=ogName%>'><%=ogoriginId%>
							<input type=hidden name='ogoriginIdTemp' value='<%=ogoriginId%>'>
						</td>
						<td width='10%' align='center'><input type=hidden name='ogshipmentMode' value='<%=shMode%>' ><%=shMode%>
						</td>
						<td width='10%' align='center'>
							<input type=hidden name ='ogautoManual' value='A'>Auto
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='ogmasterDocNo' value='<%=ogMasterDocNo%>' size=12><%=ogMasterDocNo%>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=hidden name='ogremarks' value='<%=ogRemarks%>' size=10><%=ogRemarks%></td>
<%
	}
%>
						<td width='10%' align='center'><font size=1><%=ogCarrierData%></font></td>
					</tr>	
				</table>
				<span id='cust'></span>
				<table width="800" border="0" cellspacing="1" cellpadding="4">
					<tr class='formdata'> 
						<td width='10%' align='center' class='formheader'><font size=1>Destination Gateway</font></td>
						<input type=hidden name='dgmasterStatus' value='<%=dgMasterStatus%>'>
						<input type=hidden name='dglegType' value='GT'>
						<input type=hidden name='dgdestinationId' value='<%=dgdestinationId%>'>
						<input type=hidden name='dglegValidFlag' value='<%=dgLegValidFlag%>'>
						<input type=hidden name='dgcostAmount' value='<%=dgcostAmount%>'>

						<input type=hidden name='dgpiecesReceived' value='<%=dgPiecesReceived%>'>
						<input type=hidden name='dgreceivedDate' value='<%=dgReceivedDate%>'>
						<input type=hidden name='dgcarrier' value='<%=dgCarrier%>'>
						<input type=hidden name='dgETD' value='<%=dgETD%>'>
						<input type=hidden name='dgETA' value='<%=dgETA%>'>


						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><span id='dgIdSpan'><%=(!dtoriginId.equals(dgoriginId))?dgLocationName:""%></span></font></td>

						<td width='10%' align='center'>
<%
	if(staticRow)
		staticRow = temp;
	if(terminalId.equals(dgoriginId))
		staticRow = false;
		System.out.println("Skip at main bef");
	if(( staticRow)) 
	{
	  if (dgMasterDocNo != null &&  ! dgMasterDocNo.equals(""))
    mastdisable="disabled";
%>
							<input type=hidden  name='dgoriginIdTemp' value='<%=dgoriginId%>'>
							<select name='dgoriginId' size=1 class='select' onChange='showLocation(this);checkOriginIds(this)' <%=mastdisable%>  >
<%
			for(int i=0; i< destinationGatewayIds.length; i++)
			{
				if(dgoriginId.equals(destinationGatewayIds[i][0]))
				{
%>
								<option id='<%=destinationGatewayIds[i][1]%>' value='<%=destinationGatewayIds[i][0]%>' selected><%=destinationGatewayIds[i][0]%></option>
<%
				}
				else
				{
%>
								<option id='<%=destinationGatewayIds[i][1]%>' value='<%=destinationGatewayIds[i][0]%>'><%=destinationGatewayIds[i][0]%></option>
<%
				}
			}
%>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='dgshipmentMode' size=1 class='select'  onFocus='confirmSM(this)' onChange='setAutoManual(this);confirmSMode(this)'>
<%
		if(dgShipmentMode == 1)
		{
%>
								<option value='Air' selected>Air</option>
<%
		}
		else
		{
%>
								<option value='Air' >Air</option>
<%
		}
		if(dgShipmentMode == 4)
		{
%>
								<option value='Truck' selected>Truck</option>
<%

		}
		else
		{
%>
								<option value='Truck' >Truck</option>
<%

		}
%>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='dgautoManual' size=1 class='select' onChange='disableMaster(this)'>
<%
		
		if("A".equals(dgAutoManual))   
		{
%>
								<option value='A' selected>Auto</option>
<%
		}
		else
		{
%>
								<option value='A'>Auto</option>
<%
		}
		if("M".equals(dgAutoManual))
		{
%>
								<option value='M' selected>Manual</option>
<%
		}
		else
		{
%>
								<option value='M'>Manual</option>
<%
		}
%>							</select>
						</td>
						<td width='20%' align='center'>
							<input type=text class='text' name='dgmasterDocNo' value='<%=dgMasterDocNo%>' maxlength="25" size=12>
							<input type=button name='dgmasDoc' value='...' class='input' row=0 onClick='showLocationsLOV(this)'>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=text class='text' name='dgremarks' size=10  value='<%=dgRemarks%>'></td>


<%
	}
	else
	{

		System.out.println("Skip at main else");

		String shMode = "";

		if(dgShipmentMode == 1)
			shMode = "Air";
		if(dgShipmentMode == 2)
			shMode = "Sea";
		if(dgShipmentMode == 4)
			shMode = "Truck";

		String tmp = "";
		if("A".equals(dgAutoManual))
			tmp = "Auto";
		else tmp = "Manual";

			String dgName = "";
			for(int i=0; i< destinationGatewayIds.length; i++)
			{
				if(dgoriginId.equals(destinationGatewayIds[i][0]))
				{
					dgName = destinationGatewayIds[i][1];
				}
			}
		
%>

<%
			System.out.println("Skip at checking .......... dtoriginId : dgoriginId "+	dtoriginId + " : "+dgoriginId);
			if(!dtoriginId.equals(dgoriginId))
			{
				System.out.println("Skip at checking .......... in if ");
%>
						<input type=hidden  name='dgoriginId' size=1 value='<%=dgoriginId%>' id='<%=dgName%>'><%=dgoriginId%>
						</td>
						<td width='10%' align='center'><input type=hidden name='dgshipmentMode' value='<%=shMode%>'><%=shMode%>
						</td>
						<td width='10%' align='center'><input type=hidden name='dgautoManual' value='<%=dgAutoManual%>'><%=tmp%>
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='dgmasterDocNo' value='<%=dgMasterDocNo%>' size=12><%=dgMasterDocNo%>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><input type=hidden name='dgremarks' size=10  value='<%=dgRemarks%>'><%=dgRemarks%></td>
						<input type=hidden  name='dgoriginIdTemp' size=1 value='<%=dgoriginId%>' id='<%=dgName%>'>
<%
			}
			else
			{
				System.out.println("Skip at checking .......... in else ");
%>
						<input type=hidden  name='dgoriginId' size=1 value='<%=dgoriginId%>' id='<%=dgName%>'>
						<input type=hidden  name='dgoriginIdTemp' size=1 value='<%=dgoriginId%>' id='<%=dgName%>'>
						</td>
						<td width='10%' align='center'><input type=hidden name='dgshipmentMode' value='<%=shMode%>'>
						</td>
						<td width='10%' align='center'><input type=hidden name='dgautoManual' value='<%=dgAutoManual%>'>
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='dgmasterDocNo' value='<%=dgMasterDocNo%>' size=12>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=hidden name='dgremarks' size=10  value='<%=dgRemarks%>'></td>
<%
			}
%>


<%
	}
%>
						<td width='10%' align='center'><font size=1><%=(!dtoriginId.equals(dgoriginId))?dgCarrierData:""%></font></td>
					</tr>	
					<tr class='formdata'> 
						<input type=hidden name='dtmasterStatus' value='<%=dtMasterStatus%>'>
						<input type=hidden name='dtlegType' value='TL'>
						<input type=hidden name='dtoriginId' value='<%=dtoriginId%>'>
						<input type=hidden name='dtoriginIdTemp' value='<%=dtoriginId%>'>
						<input type=hidden name='dtdestinationId' value='<%=dtdestinationId%>'>
						<input type=hidden name='dtdestinationIdTemp' value='<%=dtdestinationId%>'>
						<input type=hidden name='dtlegValidFlag' value='<%=dtLegValidFlag%>'>
						<input type=hidden name='dtcostAmount' value='<%=dtcostAmount%>'>

						<input type=hidden name='dtpiecesReceived' value='<%=dtPiecesReceived%>'>
						<input type=hidden name='dtreceivedDate' value='<%=dtReceivedDate%>'>
						<input type=hidden name='dtcarrier' value='<%=dtCarrier%>'>
						<input type=hidden name='dtETD' value='<%=olETD%>'>
						<input type=hidden name='dtETA' value='<%=olETA%>'>


						<td width='10%' align='center' class='formheader'><font size=1>Destination Terminal</font></td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><%=destinationTerminalLocation%></font></td>
						<td width='10%' align='center'><%=destinationTerminalId%></td>
						<td width='10%' align='center'>
<%
	if(terminalId.equals(dtoriginId))
		staticRow = false;

	if("X".equals(dtMasterStatus))
		staticRow = true; 
	
	if(( staticRow))
	{
%>
							<select name ='dtshipmentMode' size=1 class='select'>
								<option value='Truck'>Truck</option>
							</select>
						</td>
						<td width='10%' align='center'>
							<select name ='dtautoManual' size=1 class='select'>
<%
		if("A".equals(dtAutoManual))
		{
%>
								<option value='A' selected>Auto</option>
<%
		}
		else
		{
%>
								<option value='A'>Auto</option>
<%
		}
		if("M".equals(dtAutoManual))
		{
%>
								<option value='M' selected>Manual</option>
<%
		}
		else
		{
%>
								<option value='M'>Manual</option>
<%
		}
%>	
							</select>
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='dtmasterDocNo' value='<%=dtMasterDocNo%>' size=12><%=dtMasterDocNo%>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><input type=text class='text' name='dtremarks' value='<%=dtRemarks%>' size=10></td>
<%
	}
	else
	{
		String tmp = "";
		if("A".equals(dtAutoManual))
			tmp = "Auto";
		else tmp = "Manual";
%>
							<input type=hidden name ='dtshipmentMode' value='Truck'>Truck
						</td>
						<td width='10%' align='center'>
							<input type=hidden name ='dtautoManual' value='<%=dtAutoManual%>'><%=tmp%>
						</td>
						<td width='20%' align='center'>
							<input type=hidden name='dtmasterDocNo' value='<%=dtMasterDocNo%>' size=12><%=dtMasterDocNo%>
						</td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center' ><input type=hidden name='dtremarks' value='<%=dtRemarks%>' size=10><%=dtRemarks%></td>
<%
	}
%>
						<td width='10%' align='center'><font size=1><%=%></font></td>
					</tr>
					<tr class='formdata'> 
						<td width='10%' align='center' class='formheader'><font size=1>Consignee</font></td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'><font size=1><%=destinationLocationName%> (<%=destinationLocationId%>)</font></td>
						<td width='10%' align='center'><%=consigneeId!=null?consigneeId:""%></td>
						<td width='10%' align='center'></td>
						<td width='10%' align='center'></td>
						<td width='20%' align='center'></td>
						<td width='5%' align='center'></td>
						<td width='10%' align='center'></td>
						<td width='10%' align='center'></td>
					</tr>	
				</table>
				<table width="800" border="0" cellspacing="1" cellpadding="4">
					<tr> 
						<td align="right"> 

							<input type="submit" value="Submit" name="submit" class='input'>
							<input type="reset" value="Reset" class='input'>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</form>
</body>
</html>
<%
	}
	catch(Exception ex)
	{
		//Logger.error(FILE_NAME, " -> "+ex.toString());
    logger.error(FILE_NAME+ " -> "+ex.toString());
		ex.printStackTrace();
	}
%>


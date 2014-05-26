import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
 
import javax.naming.InitialContext;
import javax.servlet.ServletConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foursoft.esupply.common.bean.DateFormatter;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSession;
import com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSessionHome;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanHdrDOB;
import com.foursoft.etrans.common.routeplan.java.ETMultiModeRoutePlanDtlDOB;
import com.foursoft.esupply.common.java.ErrorMessage;
import com.foursoft.esupply.common.java.KeyValue;
import com.foursoft.esupply.common.util.ESupplyDateUtility;

public class ETMultiModeRoutePlanController extends HttpServlet
{
	String FILE_NAME = "ETMultiModelRoutePlanController.java";
	InitialContext ictx = null;
	ETMultiModeRoutePlanSessionHome	home	= null;
	ETMultiModeRoutePlanSession		remote	= null;
	ESupplyDateUtility		utility	= new ESupplyDateUtility();
	private static Logger logger = null;

	public void init(ServletConfig config) throws ServletException
    {
        logger  = Logger.getLogger(ETMultiModeRoutePlanController.class);
        super.init(config);
    }

	public void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException
	{
		String operation	= null;
		String subOperation	= null;
		HttpSession session = request.getSession();
    ETMultiModeRoutePlanDtlDOB routeplanDob = null;  

		ErrorMessage errorMessageObject = null;
		ArrayList	 keyValueList	    = new ArrayList();
    ArrayList  routePlanDtl     = null;
		
		try
		{
			operation		= request.getParameter("Operation");
			subOperation	= request.getParameter("SubOperation");

			//Logger.info(FILE_NAME, "Operation	-> "+operation);
			//Logger.info(FILE_NAME, "SubOperation-> "+subOperation);

			ictx = new InitialContext();
			home	= (ETMultiModeRoutePlanSessionHome)ictx.lookup("ETMultiModeRoutePlanSessionBean");
			remote	= home.create();

			if(operation.equals("Add") && subOperation == null)
			{
				String shipmentMode = request.getParameter("ShipmentMode");
				session.removeAttribute("GateTermGetIds");
				doDispatcher(request, response, "/etrans/ETMultiModeRoutePlanEnterId.jsp?Operation="+operation+"&ShipmentMode="+shipmentMode);
			}
			else if(operation.equals("Add") && subOperation.equals("EnterId"))
			{
				String shipmentMode = request.getParameter("ShipmentMode");
				String[] idInfo = null;
				String houseDocNo = request.getParameter("qouteId");
        //System.out.println("qouteIdqouteIdqouteIdqouteIdqouteId :: "+houseDocNo);
				String documentType = request.getParameter("documentType");

				ESupplyGlobalParameters eSupplyGlobalParameters = (ESupplyGlobalParameters)session.getAttribute("loginbean");
        //modefied by VLAKSHMI for issue 174469
				boolean isValidDocumentNo = remote.isValidDocumentNo(documentType, houseDocNo, eSupplyGlobalParameters.getTerminalId(),eSupplyGlobalParameters.getUserTerminalType(), shipmentMode);
				//////////////////////////////////////////////////////////////////////////////////////////////////
				// For getting GatwauIds and Terminals
				//////////////////////////////////////////////////////////////////////////////////////////////////
        //idInfo = remote.getGatewayIds(null, null, "");
				//session.setAttribute("GateTermGetIds", idInfo);

				if(isValidDocumentNo)
				{
					//ETMultiModeRoutePlanHdrDOB routePlanHdr = remote.getRoutePlan(documentType, houseDocNo);
					//session.setAttribute("RoutePlanHdr", routePlanHdr);

					//String[][] originGatewayIds = remote.getGatewayIds(routePlanHdr.getOriginTerminalId());
					//String[][] destinationGatewayIds = remote.getGatewayIds(routePlanHdr.getDestinationTerminalId());
          routePlanDtl = remote.getRoutePlanDetails(houseDocNo,eSupplyGlobalParameters);
					//session.setAttribute("OriginGatewayIds", originGatewayIds);
          
					session.setAttribute("RoutePlanDtls",routePlanDtl);
					doDispatcher(request, response, "/etrans/QMSRoutePlanAdd.jsp");
				}
				else
				{
					request.setAttribute("ERROR", "Please Enter Valid Document No.");
					doDispatcher(request, response, "/etrans/ETMultiModeRoutePlanEnterId.jsp?Operation=Add&ShipmentMode="+shipmentMode);
				}
			}
			else if(operation.equals("Add") && subOperation.equals("Detail"))
			{
          String[]  shipmentMode    =  request.getParameterValues("shipmentMode");
          String[]  orignLoc        =  request.getParameterValues("orginLocId");
          String[]  destLoc         =  request.getParameterValues("destinationLocId");
          String[]  remarks         =  request.getParameterValues("remarks");
          String    quoteId         =  request.getParameter("qouteId");
          String[]  orignLocValues  =  null;
          String[]  destLocValues   =  null;
          String[]  shipMode        =  null;
          String	  errorMessage    =  "";
          StringBuffer errorMassege =  null;
          if(shipmentMode!=null)
          {
              routePlanDtl  = new ArrayList();
              int   shipmentModeLength  = shipmentMode.length;
              orignLocValues  =  new String[shipmentModeLength];
              destLocValues   =  new String[shipmentModeLength];
              shipMode        =  new String[shipmentModeLength];
              for(int i=0;i<shipmentModeLength;i++)
              {
                  routeplanDob    =   new ETMultiModeRoutePlanDtlDOB();
                  routeplanDob.setShipmentMode(Integer.parseInt(shipmentMode[i]));
                  routeplanDob.setOrgLoc(orignLoc[i]);
                  orignLocValues[i] = orignLoc[i];
                  routeplanDob.setDestLoc(destLoc[i]);
                  destLocValues[i]  = destLoc[i];
                  shipMode[i]       = shipmentMode[i];
                  routeplanDob.setRemarks(remarks[i]);
                  if("1".equals(shipmentMode[i]))
                    routeplanDob.setLegType("LL");
                  else if("2".equals(shipmentMode[i]))
                    routeplanDob.setLegType("PP");
                  else if("4".equals(shipmentMode[i]))
                     routeplanDob.setLegType("LL");
                   //System.out.println("flagflagflagflagflag in controller :: "+remarks[i]);
                routePlanDtl.add(routeplanDob);
              }
              ESupplyGlobalParameters eSupplyGlobalParameters = (ESupplyGlobalParameters)session.getAttribute("loginbean");
             errorMassege			=   remote.validateSellRatesHdrData(orignLocValues,destLocValues,shipMode);
             if(errorMassege.length() > 0)
             {
                session.setAttribute("RoutePlanDtls",routePlanDtl);
                request.setAttribute("ERROR",errorMassege.toString());
                doDispatcher(request, response, "/etrans/QMSRoutePlanAdd.jsp");
             }
             else
             {
              boolean flag = remote.updateRoutePlan(routePlanDtl,quoteId,eSupplyGlobalParameters);
             // System.out.println("flagflagflagflagflag in controller :: "+flag);
              if(flag)
              {
                  errorMessage = "Route Plan successfully Modified for Document No: "+quoteId;
                  
                  errorMessageObject = new ErrorMessage(errorMessage,"ETMultiModeRoutePlanController?Operation=Add&ShipmentMode="+shipmentMode);
                  keyValueList.add(new KeyValue("ErrorCode","RSM")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
					
              }
              else
              {
                  errorMessage = "Sorry Unable to Modify the Route Plan for Document No: "+quoteId;
                  errorMessageObject = new ErrorMessage(errorMessage,"etrans/ETMultiModeRoutePlanEnterId.jsp?Operation=Add&ShipmentMode=1");
                  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
                  keyValueList.add(new KeyValue("Operation",operation)); 	
                  errorMessageObject.setKeyValueList(keyValueList);
                  request.setAttribute("ErrorMessage",errorMessageObject); 
              }
              doDispatcher(request, response, "/ESupplyErrorPage.jsp");
             }
          }
			}
			else if(operation.equals("Swap") && subOperation.equals("convertToSea"))
			{
			 //	doConvertToSeaPRQ(request, response);
			}
			else if(operation.equals("Swap") && subOperation.equals("convertToAir"))
			{
				//doConvertToAirPRQ(request, response);
			}
		}
		catch(Exception ex)
		{
      ex.printStackTrace();
			//Logger.error(FILE_NAME, " [doPost(request, response)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [doPost(request, response)] -> "+ex.toString());
      errorMessageObject = new ErrorMessage("Error while doPost()","etrans/ETMultiModeRoutePlanEnterId.jsp?Operation=Add&ShipmentMode=1");
      keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
      keyValueList.add(new KeyValue("Operation",operation)); 	
      errorMessageObject.setKeyValueList(keyValueList);
      request.setAttribute("ErrorMessage",errorMessageObject); 
		}
	}

	private ETMultiModeRoutePlanHdrDOB doRoutePlan(HttpServletRequest request)
	{
		ETMultiModeRoutePlanHdrDOB routePlanHdr = new ETMultiModeRoutePlanHdrDOB();
		ETMultiModeRoutePlanDtlDOB[] routePlanDtl = null;
		HttpSession 			session 		= 	request.getSession();
		ESupplyGlobalParameters loginBean		= 	null;
		loginBean								= 	(ESupplyGlobalParameters)session.getAttribute("loginbean");
		String	dateFormat			=	loginBean.getUserPreferences().getDateFormat();


		try
		{
			DateFormatter dateFormatter = DateFormatter.getInstance();
			int modeCode = 0;
			long   routePlanId			= 0L;
			String routeId				= request.getParameter("routePlanId");
			String prqId				= request.getParameter("prqNo");
			String hawbId				= request.getParameter("hawbNo");
			String originTerminalId		= request.getParameter("originTerminalId");
			String destinationTerminalId= request.getParameter("destinationTerminalId");
			String originLocationId		= request.getParameter("originLocationId");
			String destinationLocationId= request.getParameter("destinationLocationId");
			String shipperId			= request.getParameter("shipperId");
			String consigneeId			= request.getParameter("consigneeId");
			String primaryMode			= request.getParameter("primaryMode");
			String overWriteFlag		= request.getParameter("overWrite");
			String shipmentStatus		= request.getParameter("shipmentStatus");

			String originTerminalLocation		= request.getParameter("originTerminalLocation");
			String destinationTerminalLocation	= request.getParameter("destinationTerminalLocation");
			String originLocationName			= request.getParameter("originLocationName");
			String destinationLocationName		= request.getParameter("destinationLocationName");

			String serviceLevelId	= request.getParameter("serviceLevelId");
			String serviceLevelDesc = request.getParameter("serviceLevelDesc");
			String deliveryTerms	= request.getParameter("deliveryTerms");
			String totalPieces		= request.getParameter("totalPieces");
			String weight			= request.getParameter("weight");

			int pieces = 0;
			double wt = 0.0;

			if(totalPieces != null && !totalPieces.equals(""))
				pieces = Integer.parseInt(totalPieces);

			if(weight != null && !weight.equals(""))
				wt = Double.parseDouble(weight);

			if(overWriteFlag == null)
				overWriteFlag = "N";

			Timestamp lastUpdateTimestamp = new Timestamp(new Date().getTime());

			if(routeId != null && !routeId.equals(""))
				routePlanId = Long.parseLong(routeId);

			if(primaryMode != null && !primaryMode.equals(""))
				modeCode = Integer.parseInt(primaryMode);

			routePlanHdr.setRoutePlanId(routePlanId);
			routePlanHdr.setPRQId(prqId);
			routePlanHdr.setHouseDocumentId(hawbId);
			routePlanHdr.setOriginTerminalId(originTerminalId);
			routePlanHdr.setDestinationTerminalId(destinationTerminalId);
			routePlanHdr.setOriginLocationId(originLocationId);
			routePlanHdr.setDestinationLocationId(destinationLocationId);
			routePlanHdr.setShipperId(shipperId);
			routePlanHdr.setConsigneeId(consigneeId);
			routePlanHdr.setPrimaryMode(modeCode);
			routePlanHdr.setOverWriteFlag(overWriteFlag);
			routePlanHdr.setCreateTimestamp(lastUpdateTimestamp);
			routePlanHdr.setLastUpdateTimestamp(lastUpdateTimestamp);
			routePlanHdr.setShipmentStatus(shipmentStatus);
			routePlanHdr.setOriginTerminalLocation(originTerminalLocation);
			routePlanHdr.setDestinationTerminalLocation(destinationTerminalLocation);
			routePlanHdr.setOriginLocationName(originLocationName);
			routePlanHdr.setDestinationLocationName(destinationLocationName);
			routePlanHdr.setServiceLevelId(serviceLevelId);
			routePlanHdr.setServiceLevelDesc(serviceLevelDesc);
			routePlanHdr.setDeliveryTerms(deliveryTerms);
			routePlanHdr.setTotalPieces(pieces);
			routePlanHdr.setWeight(wt);

			String oloriginId		= request.getParameter("oloriginId");
			if(oloriginId==null)
				oloriginId		= request.getParameter("oloriginIdTemp");

			String oldestinationId	= request.getParameter("oldestinationId");
			String olmasterStatus	= request.getParameter("olmasterStatus");
			if(olmasterStatus == null || olmasterStatus.equals(""))
				olmasterStatus = "X";
			String ollegType		= request.getParameter("ollegType");
			String olpiecesReceived	= request.getParameter("olpiecesReceived");
			String olreceivedDate	= request.getParameter("olreceivedDate");
			String olshipmentMode	= request.getParameter("olshipmentMode");
			String olautoManual		= request.getParameter("olautoManual");
			String olmasterDocNo	= request.getParameter("olmasterDocNo");
			String olremarks		= request.getParameter("olremarks");
			String ollegValidFlag	= request.getParameter("ollegValidFlag");
			String olcostAmount		= request.getParameter("olcostAmount");


			if(ollegValidFlag == null || ollegValidFlag.equals(""))
				ollegValidFlag = "Y";

			if(olmasterDocNo != null)
			{
				if(olmasterDocNo.equals(""))
					olmasterStatus = "X";
				else if(!olmasterStatus.equals("") && olmasterStatus.equals("X"))
					olmasterStatus = "O";
			}

			String otoriginId		= request.getParameter("otoriginId");
			if(otoriginId==null)
				otoriginId = request.getParameter("otoriginIdTemp");

			String otdestinationId	= request.getParameter("otdestinationId");
			String otmasterStatus	= request.getParameter("otmasterStatus");
			if(otmasterStatus == null || otmasterStatus.equals(""))
				otmasterStatus = "X";
			String otlegType		= request.getParameter("otlegType");
			String otpiecesReceived	= request.getParameter("otpiecesReceived");
			String otreceivedDate	= request.getParameter("otreceivedDate");
			String otshipmentMode	= request.getParameter("otshipmentMode");
			String otautoManual		= request.getParameter("otautoManual");
			String otmasterDocNo	= request.getParameter("otmasterDocNo");
			String otremarks		= request.getParameter("otremarks");
			String otlegValidFlag	= request.getParameter("otlegValidFlag");
			String otcostAmount		= request.getParameter("otcostAmount");

			if(otlegValidFlag == null || otlegValidFlag.equals(""))
				otlegValidFlag = "Y";

			if(otmasterDocNo != null)
			{
				if(otmasterDocNo.equals(""))
					otmasterStatus = "X";
				else if(!otmasterStatus.equals("") && otmasterStatus.equals("X"))
					otmasterStatus = "O";
			}

			String ogoriginId		= request.getParameter("ogoriginId");
			if(ogoriginId==null)
				ogoriginId = request.getParameter("ogoriginIdTemp");

			
			String ogdestinationId	= request.getParameter("ogdestinationId");
			String ogmasterStatus	= request.getParameter("ogmasterStatus");
			if(ogmasterStatus == null || ogmasterStatus.equals(""))
				ogmasterStatus = "X";
			String oglegType		= request.getParameter("oglegType");
			String ogpiecesReceived	= request.getParameter("ogpiecesReceived");
			String ogreceivedDate	= request.getParameter("ogreceivedDate");
			String ogshipmentMode	= request.getParameter("ogshipmentMode");
			String ogautoManual		= request.getParameter("ogautoManual");
			String ogmasterDocNo	= request.getParameter("ogmasterDocNo");
			String ogremarks		= request.getParameter("ogremarks");
			String oglegValidFlag	= request.getParameter("oglegValidFlag");
			String ogcostAmount		= request.getParameter("ogcostAmount");

			if(oglegValidFlag == null || oglegValidFlag.equals(""))
				oglegValidFlag = "Y";

			if(ogmasterDocNo != null)
			{
				if(ogmasterDocNo.equals(""))
					ogmasterStatus = "X";
				else if(!ogmasterStatus.equals("") && ogmasterStatus.equals("X"))
					ogmasterStatus = "O";
			}

			String dgoriginId		= request.getParameter("dgoriginId");
			if(dgoriginId==null)
				dgoriginId = request.getParameter("dgoriginIdTemp");

			String dgdestinationId	= request.getParameter("dgdestinationId");
			String dgmasterStatus	= request.getParameter("dgmasterStatus");
			if(dgmasterStatus == null || dgmasterStatus.equals(""))
				dgmasterStatus = "X";
			String dglegType		= request.getParameter("dglegType");
			String dgpiecesReceived	= request.getParameter("dgpiecesReceived");
			String dgreceivedDate	= request.getParameter("dgreceivedDate");
			String dgshipmentMode	= request.getParameter("dgshipmentMode");
			String dgautoManual		= request.getParameter("dgautoManual");
			String dgmasterDocNo	= request.getParameter("dgmasterDocNo");
			String dgremarks		= request.getParameter("dgremarks");
			String dglegValidFlag	= request.getParameter("dglegValidFlag");
			String dgcostAmount		= request.getParameter("dgcostAmount");

			if(dglegValidFlag == null || dglegValidFlag.equals(""))
				dglegValidFlag = "Y";

			if(dgmasterDocNo != null)
			{
				if(dgmasterDocNo.equals(""))
					dgmasterStatus = "X";
				else if(!dgmasterStatus.equals("") && dgmasterStatus.equals("X"))
					dgmasterStatus = "O";
			}

			String dtoriginId		= request.getParameter("dtoriginId");
			if(dtoriginId==null)
				dtoriginId = request.getParameter("dtoriginIdTemp");

			
			String dtdestinationId	= request.getParameter("dtdestinationId");

			if(dtdestinationId==null)
				dtdestinationId = request.getParameter("dtdestinationIdTemp");
			String dtmasterStatus	= request.getParameter("dtmasterStatus");
			if(dtmasterStatus == null || dtmasterStatus.equals(""))
				dtmasterStatus = "X";
			String dtlegType		= request.getParameter("dtlegType");
			String dtpiecesReceived	= request.getParameter("dtpiecesReceived");
			String dtreceivedDate	= request.getParameter("dtreceivedDate");
			String dtshipmentMode	= request.getParameter("dtshipmentMode");
			String dtautoManual		= request.getParameter("dtautoManual");
			String dtmasterDocNo	= request.getParameter("dtmasterDocNo");
			String dtremarks		= request.getParameter("dtremarks");
			String dtlegValidFlag	= request.getParameter("dtlegValidFlag");
			String dtcostAmount		= request.getParameter("dtcostAmount");

			if(dtlegValidFlag == null || dtlegValidFlag.equals(""))
				dtlegValidFlag = "Y";

			if(dtmasterDocNo != null)
			{
				if(dtmasterDocNo.equals(""))
					dtmasterStatus = "X";
				else if(!dtmasterStatus.equals("") && dtmasterStatus.equals("X"))
					dtmasterStatus = "O";
			}

			String[] originIdTemp	= request.getParameterValues("originIdTemp");
			String[] originId		= null;
			
			if(originIdTemp!=null)
			{
				originId		= new String[originIdTemp.length];
				int orgIdTempLen	=	originIdTemp.length;
				for(int loop=0;loop<orgIdTempLen;loop++)
					originId[loop] = originIdTemp[loop];
			}
			
			String[] originLocation = request.getParameterValues("originLocation");
			String[] destinationId	= request.getParameterValues("destinationId");
			String[] masterStatus	= request.getParameterValues("masterStatus");
			String[] legType		= request.getParameterValues("legType");
			String[] piecesReceived	= request.getParameterValues("piecesReceived");
			String[] receivedDate	= request.getParameterValues("receivedDate");
			String[] shipmentMode	= request.getParameterValues("shipmentMode");
			String[] autoManual		= request.getParameterValues("autoManual");
			String[] masterDocNo	= request.getParameterValues("masterDocNo");
			String[] remarks		= request.getParameterValues("remarks");
			String[] legValidFlag	= request.getParameterValues("legValidFlag");
			String[] costAmount		= request.getParameterValues("costAmount");

			int lent = 5;
			if(destinationId != null)
				lent += destinationId.length;

			if(ogoriginId.equals(otoriginId))
				lent--;
			if(dgoriginId.equals(dtoriginId))
				lent--;

			routePlanDtl = new ETMultiModeRoutePlanDtlDOB[lent];

			int sNo = 0;
			//String originLocationId		= request.getParameter("originLocationId");
			//String destinationLocationId= request.getParameter("destinationLocationId");

			routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, oloriginId, oldestinationId, getModeCode(olshipmentMode), olmasterStatus, olautoManual, olmasterDocNo, ollegType, ollegValidFlag,originLocationId,olmasterDocNo);
			routePlanDtl[sNo].setRemarks(olremarks);
			if(olcostAmount!=null)
				routePlanDtl[sNo].setCostAmount(Double.parseDouble(olcostAmount));
			else
				routePlanDtl[sNo].setCostAmount(0.0);


			int received = 0;
			Timestamp rcvDate = null;
			if(olpiecesReceived != null && !olpiecesReceived.equals(""))
				received = Integer.parseInt(olpiecesReceived);

			try
			{
				if(olreceivedDate != null && !olreceivedDate.equals(""))
					rcvDate = utility.getTimestamp(dateFormat,olreceivedDate);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Invalid Date or Time at line No:481" +ex.toString());
        logger.error(FILE_NAME+"Invalid Date or Time at line No:481" +ex.toString());
				ex.printStackTrace();
			}
			routePlanDtl[sNo].setPiecesReceived(received);
			routePlanDtl[sNo].setReceivedDate(rcvDate);
			
			sNo++;

			/*
			routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, otoriginId, otdestinationId, getModeCode(otshipmentMode), otmasterStatus, otautoManual, otmasterDocNo, otlegType, otlegValidFlag);
			routePlanDtl[sNo].setRemarks(otremarks);
			received = 0;
			rcvDate = null;
			if(otpiecesReceived != null && !otpiecesReceived.equals(""))
				received = Integer.parseInt(otpiecesReceived);

			if(otreceivedDate != null && !otreceivedDate.equals(""))
				rcvDate = dateFormatter.convertToTimestamp(otreceivedDate);
			sNo++;

			if(!ogoriginId.equals(otoriginId))
			{
				routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, ogoriginId, ogdestinationId, getModeCode(ogshipmentMode), ogmasterStatus, ogautoManual, ogmasterDocNo, oglegType, oglegValidFlag);
				routePlanDtl[sNo].setRemarks(ogremarks);
				received = 0;
				rcvDate = null;
				if(ogpiecesReceived != null && !ogpiecesReceived.equals(""))
					received = Integer.parseInt(ogpiecesReceived);

				if(ogreceivedDate != null && !ogreceivedDate.equals(""))
					rcvDate = dateFormatter.convertToTimestamp(ogreceivedDate);
				sNo++;
			}

			if(originId != null)
			{
				for(int i=0; i<originId.length; i++)
				{
					if(legValidFlag[i] == null || legValidFlag[i].equals(""))
						legValidFlag[i] = "Y";

					if(masterDocNo[i] != null)
					{
						if(masterDocNo[i].equals(""))
							masterStatus[i] = "X";
						else if(!masterStatus[i].equals("") && masterStatus[i].equals("X"))
							masterStatus[i] = "O";
					}

					routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, originId[i], destinationId[i], getModeCode(shipmentMode[i]), masterStatus[i], autoManual[i], masterDocNo[i], legType[i], legValidFlag[i]);
					routePlanDtl[sNo].setRemarks(remarks[i]);
					routePlanDtl[sNo].setOriginTerminalLocation(originLocation[i]);
					received = 0;
					rcvDate = null;
					if(piecesReceived[i] != null && !piecesReceived[i].equals(""))
						received = Integer.parseInt(piecesReceived[i]);

					if(receivedDate[i] != null && !receivedDate[i].equals(""))
						rcvDate = dateFormatter.convertToTimestamp(receivedDate[i]);
					sNo++;
				}
			}

			routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, dgoriginId, dgdestinationId, getModeCode(dgshipmentMode), dgmasterStatus, dgautoManual, dgmasterDocNo, dglegType, dglegValidFlag);
			routePlanDtl[sNo].setRemarks(dgremarks);
			received = 0;
			rcvDate = null;
			if(dgpiecesReceived != null && !dgpiecesReceived.equals(""))
				received = Integer.parseInt(dgpiecesReceived);

			if(dgreceivedDate != null && !dgreceivedDate.equals(""))
				rcvDate = dateFormatter.convertToTimestamp(dgreceivedDate);
			sNo++;

			if(!dtoriginId.equals(dgoriginId))
			{
				routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, dtoriginId, dtdestinationId, getModeCode(dtshipmentMode), dtmasterStatus, dtautoManual, dtmasterDocNo, dtlegType, dtlegValidFlag);
				routePlanDtl[sNo].setRemarks(dtremarks);
				received = 0;
				rcvDate = null;
				if(dtpiecesReceived != null && !dtpiecesReceived.equals(""))
					received = Integer.parseInt(dtpiecesReceived);

				if(dtreceivedDate != null && !dtreceivedDate.equals(""))
					rcvDate = dateFormatter.convertToTimestamp(dtreceivedDate);
				sNo++;
			}
			*/
//////////////////////////////////////////////////////////////////////////////////////////////////
			//Added by AvatarKrishna for Interchange of Terminal and Gateway dtl at front end
			
			if(!ogoriginId.equals(otoriginId))
			{
				routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, otoriginId, otdestinationId, getModeCode(otshipmentMode), otmasterStatus, otautoManual, otmasterDocNo, otlegType, otlegValidFlag,olmasterDocNo,otmasterDocNo);
				routePlanDtl[sNo].setRemarks(otremarks);
				if(otcostAmount!=null)
					routePlanDtl[sNo].setCostAmount(Double.parseDouble(otcostAmount));
				else
					routePlanDtl[sNo].setCostAmount(0.0);

				received = 0;
				rcvDate = null;
				if(otpiecesReceived != null && !otpiecesReceived.equals(""))
					received = Integer.parseInt(otpiecesReceived);
				try
				{
					if(otreceivedDate != null && !otreceivedDate.equals(""))
						rcvDate = utility.getTimestamp(dateFormat,otreceivedDate);
				}
				catch(Exception ex)
				{
					//Logger.error(FILE_NAME,"Invalid Date or Time at line No:584" +ex.toString());
          logger.error(FILE_NAME+"Invalid Date or Time at line No:584" +ex.toString());
					ex.printStackTrace();
				}
				routePlanDtl[sNo].setPiecesReceived(received);
				routePlanDtl[sNo].setReceivedDate(rcvDate);

				sNo++;
			}

			routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, ogoriginId, ogdestinationId, getModeCode(ogshipmentMode), ogmasterStatus, ogautoManual, ogmasterDocNo, oglegType, oglegValidFlag,otmasterDocNo,ogmasterDocNo);
			routePlanDtl[sNo].setRemarks(ogremarks);
			if(ogcostAmount!=null)
				routePlanDtl[sNo].setCostAmount(Double.parseDouble(ogcostAmount));
			else
				routePlanDtl[sNo].setCostAmount(0.0);

			received = 0;
			rcvDate = null;
			if(ogpiecesReceived != null && !ogpiecesReceived.equals(""))
				received = Integer.parseInt(ogpiecesReceived);

			try
			{
				if(ogreceivedDate != null && !ogreceivedDate.equals(""))
					rcvDate = utility.getTimestamp(dateFormat,ogreceivedDate);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Invalid Date or Time at line No:604" +ex.toString());
        logger.error(FILE_NAME+"Invalid Date or Time at line No:604" +ex.toString());
				ex.printStackTrace();
			}
			routePlanDtl[sNo].setPiecesReceived(received);
			routePlanDtl[sNo].setReceivedDate(rcvDate);

			sNo++;

			if(originId != null)
			{
				int orgIdLen	=	originId.length;
				for(int i=0; i<orgIdLen; i++)
				{
					if(legValidFlag[i] == null || legValidFlag[i].equals(""))
						legValidFlag[i] = "Y";
					
					if(masterDocNo[i] != null)
					{
						
						if(masterDocNo[i].equals(""))
							masterStatus[i] = "X";
						else if(!masterStatus[i].equals("") && masterStatus[i].equals("X"))
							masterStatus[i] = "O";
					    else if(masterStatus[i].equals("") && masterDocNo[i].length()>0)
							masterStatus[i] = "O";
					
					}
				    
					//System.out.println("In MasterDocStatus is " + masterStatus[i].length());
					//routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, originId[i], destinationId[i], getModeCode(shipmentMode[i]), masterStatus[i], autoManual[i], masterDocNo[i], legType[i], legValidFlag[i],ogmasterDocNo);
					routePlanDtl[sNo].setRemarks(remarks[i]);
					if(costAmount!=null && costAmount[i] !=null && !costAmount[i].equals(""))
						routePlanDtl[sNo].setCostAmount(Double.parseDouble(costAmount[i]));
					else
						routePlanDtl[sNo].setCostAmount(0.0);

					routePlanDtl[sNo].setOriginTerminalLocation(originLocation[i]);
					received = 0;
					rcvDate = null;
					if(piecesReceived[i] != null && !piecesReceived[i].equals(""))
						received = Integer.parseInt(piecesReceived[i]);

					try
					{
						if(receivedDate[i] != null && !receivedDate[i].equals(""))
							rcvDate = utility.getTimestamp(dateFormat,receivedDate[i]);
					}
					catch(Exception ex)
					{
						//Logger.error(FILE_NAME,"Invalid Date or Time at line No:644" +ex.toString());
            logger.error(FILE_NAME+"Invalid Date or Time at line No:644" +ex.toString());
						ex.printStackTrace();
					}
					routePlanDtl[sNo].setPiecesReceived(received);
					routePlanDtl[sNo].setReceivedDate(rcvDate);

					sNo++;
				}
			}


			if(!dtoriginId.equals(dgoriginId))
			{
				routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, dgoriginId, dgdestinationId, getModeCode(dgshipmentMode), dgmasterStatus, dgautoManual, dgmasterDocNo, dglegType, dglegValidFlag);
				routePlanDtl[sNo].setRemarks(dgremarks);
				if(dgcostAmount!=null)
					routePlanDtl[sNo].setCostAmount(Double.parseDouble(dgcostAmount));
				else
					routePlanDtl[sNo].setCostAmount(0.0);

				received = 0;
				rcvDate = null;
				if(dgpiecesReceived != null && !dgpiecesReceived.equals(""))
					received = Integer.parseInt(dgpiecesReceived);
				
				try
				{
					if(dgreceivedDate != null && !dgreceivedDate.equals(""))
						rcvDate = utility.getTimestamp(dateFormat,dgreceivedDate);
				}
				catch(Exception ex)
				{
					//Logger.error(FILE_NAME,"Invalid Date or Time at line No:644" +ex.toString());
          logger.error(FILE_NAME+"Invalid Date or Time at line No:644" +ex.toString());
					ex.printStackTrace();
				}
					routePlanDtl[sNo].setPiecesReceived(received);
					routePlanDtl[sNo].setReceivedDate(rcvDate);

					sNo++;
			}

			routePlanDtl[sNo] = new ETMultiModeRoutePlanDtlDOB(sNo+1, routePlanId, dtoriginId, dtdestinationId, getModeCode(dtshipmentMode), dtmasterStatus, dtautoManual, dtmasterDocNo, dtlegType, dtlegValidFlag);
			routePlanDtl[sNo].setRemarks(dtremarks);
			if(dtcostAmount!=null)
				routePlanDtl[sNo].setCostAmount(Double.parseDouble(dtcostAmount));
			else
				routePlanDtl[sNo].setCostAmount(0.0);

			received = 0;
			rcvDate = null;
			if(dtpiecesReceived != null && !dtpiecesReceived.equals(""))
				received = Integer.parseInt(dtpiecesReceived);

			try
			{
				if(dtreceivedDate != null && !dtreceivedDate.equals(""))
					rcvDate = utility.getTimestamp(dateFormat,dtreceivedDate);
			}
			catch(Exception ex)
			{
				//Logger.error(FILE_NAME,"Invalid Date or Time at line No:688" +ex.toString());
        logger.error(FILE_NAME+"Invalid Date or Time at line No:688" +ex.toString());
				ex.printStackTrace();
			}
			routePlanDtl[sNo].setPiecesReceived(received);
			routePlanDtl[sNo].setReceivedDate(rcvDate);

			sNo++;

//////////////////////////////////////////////////////////////////////////////////////////////////

			routePlanHdr.setRoutePlanDtlDOB(routePlanDtl);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " -> "+ex.toString());
      logger.error(FILE_NAME+ " -> "+ex.toString());
		}
		return routePlanHdr;
	}

	private void setSessionValues(HttpServletRequest request, String errorCode, String errorMessage, String operation, String nextNavigation) throws ServletException
	{
		try
		{
			HttpSession session = request.getSession();
			session.setAttribute("ErrorCode",errorCode);
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("Operation",operation);
			session.setAttribute("NextNavigation",nextNavigation);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [setSessionValues(request, errorCode, errorMessage, operation, nextNavigation)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [setSessionValues(request, errorCode, errorMessage, operation, nextNavigation)] -> "+ex.toString());
		}
	}

	private int getModeCode(String shipmentMode)
	{
		try
		{
			if(shipmentMode.equals("Air"))
				return 1;
			else if(shipmentMode.equals("Sea"))
				return 2;
			else if(shipmentMode.equals("Truck"))
				return 4;
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, " [getModeCode(shipmentMode)] -> "+ex.toString());
      logger.error(FILE_NAME+ " [getModeCode(shipmentMode)] -> "+ex.toString());
		}
		return 0;
	}

	private void displayRouteValues(ETMultiModeRoutePlanHdrDOB routePlanHdr)
	{
		try
		{
			ETMultiModeRoutePlanDtlDOB[] routePlanDtlDOB = routePlanHdr.getRoutePlanDtlDOB();
			int rtPlanDtlDOB	=	routePlanDtlDOB.length;
			for(int i=0; i< rtPlanDtlDOB; i++)
			{
				//Logger.info(FILE_NAME, "Leg "+(i+1));
				//Logger.info(FILE_NAME, "Origin		->"+routePlanDtlDOB[i].getOriginTerminalId());
				//Logger.info(FILE_NAME, "Destination	-> "+routePlanDtlDOB[i].getDestinationTerminalId());
				//Logger.info(FILE_NAME, "ShipmentMode-> "+routePlanDtlDOB[i].getShipmentMode());
				//Logger.info(FILE_NAME, "Leg Type	-> "+routePlanDtlDOB[i].getLegType());
				//Logger.info(FILE_NAME, "Status		-> "+routePlanDtlDOB[i].getShipmentStatus()); 
				//Logger.info(FILE_NAME, "AutoFlag	-> "+routePlanDtlDOB[i].getAutoManualFlag()); 
				//Logger.info(FILE_NAME, "MasterDocNo	-> "+routePlanDtlDOB[i].getMasterDocId()); 
				//Logger.info(FILE_NAME, "ValidFlag	-> "+routePlanDtlDOB[i].getLegValidFlag()); 
				//Logger.info(FILE_NAME, "Remarks		-> "+routePlanDtlDOB[i].getRemarks()); 
				//Logger.info(FILE_NAME, "ReceivedDate-> "+routePlanDtlDOB[i].getReceivedDate()); 
				//Logger.info(FILE_NAME, "ReceivedBox	-> "+routePlanDtlDOB[i].getPiecesReceived()); 
				//Logger.info(FILE_NAME, "Location	-> "+routePlanDtlDOB[i].getOriginTerminalLocation()); 
			}
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME, "[displayRouteValues()] -> "+ex.toString());
      logger.error(FILE_NAME+ "[displayRouteValues()] -> "+ex.toString());
		}
	}


	public void doDispatcher(HttpServletRequest request, HttpServletResponse response, String forwardFile) 
					throws IOException, ServletException
	{
		try
		{
			HttpSession		session	= request.getSession();
			RequestDispatcher rd = request.getRequestDispatcher(forwardFile);
			rd.forward(request, response);
		}
		catch(Exception ex)
		{
			//Logger.info(FILE_NAME, " doDispatcher() ", " Exception in forwarding to "+forwardFile, ex);
      logger.info(FILE_NAME+ " doDispatcher() "+ " Exception in forwarding to "+forwardFile+ ex);
		}
	}
}
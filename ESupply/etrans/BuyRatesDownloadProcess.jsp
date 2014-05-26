
<%
/*	Programme Name : BuyRatesDownloadProcess.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : Buy Rates.
*	Sub Task Name  : Modify/View/Delete.
*	Author		   : Rama Krishna.Y
*	Date Started   : 25-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>


<%@ page import = "java.util.ArrayList,
				   com.foursoft.esupply.common.util.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.qms.operations.rates.ejb.sls.BuyRatesSession,
                   com.qms.operations.rates.ejb.sls.BuyRatesSessionHome,				   
				   javax.naming.InitialContext,
				   java.util.Hashtable,
				   java.util.Enumeration,
				   java.util.HashMap,
				   java.util.Iterator,
				   com.qms.operations.rates.dob.RateDOB,
				   com.qms.operations.rates.dob.FlatRatesDOB,
				   com.qms.operations.sellrates.java.QMSBoundryDOB,
				   com.qms.operations.sellrates.java.QMSSellRatesDOB,
				   com.foursoft.esupply.common.util.ESupplyDateUtility,
				   com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!  
     public static final String FILE_NAME="BuyRatesDownloadProcess.jsp";%>
<%

	ArrayList          keyValueList           =    null;
	ErrorMessage       errorMessageObject     =    null;
	String             operation              =    null;
    BuyRatesSessionHome      home             =    null;
	BuyRatesSession          remote           =    null;
	InitialContext           ictxt            =    null;
	String                   weightClass      =    null;		
	String                   originCountry    =    null;		
	String                   shipmentMode     =    null;		
	String                   carrierId        =    null;			
	String                   origin           =    null;				
	String                   rateType         =    null;			
    String                   weightBreak      =    null;		
	String                   destination      =    null;
	String                   destinationCountry=    null;
	String                   serviceLevelId   =    null;
	String                   currencyId       =    null;
	String                   consoleType        =    null;
	String                   keyHash          =    null;
	Hashtable                rateHashTable    =    null;		
	Enumeration              enum1             =    null;	
	int                      k                =       0;		
	RateDOB                  rateDOB          =    null;		
	ArrayList                rateList         =    null;	
	ArrayList                headerlist       =    null;
	ArrayList                weightBreakList  =    null;
	FlatRatesDOB            flatRatesDOB      =    null;
	InitialContext          initial           =    null;
	ESupplyDateUtility      utility           =  new ESupplyDateUtility();
	QMSSellRatesDOB         sellRatesDob      =  new QMSSellRatesDOB();
	HashMap                 mapValues         =  new HashMap();
	Iterator                itr               =  null;
	QMSBoundryDOB		    boundryDob		  =	 null;
	ArrayList               chargeDtlList     =  null;
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
		             operation					=   request.getParameter("Operation");					 
					 weightClass				=   request.getParameter("weightClass");						 
					 originCountry				=   request.getParameter("originCountry");	
					 shipmentMode				=   request.getParameter("shipmentMode");
					 carrierId					=   request.getParameter("carriers");
					 origin						=   request.getParameter("origin");
					 rateType					=   request.getParameter("rateType");
					 weightBreak				=   request.getParameter("weightBreak");
					 destination			    =   request.getParameter("destination");
					 destinationCountry			=   request.getParameter("destinationCountry");
					 serviceLevelId				=   request.getParameter("serviceLevelId");
					 sellRatesDob.setOrigin(origin);
					 sellRatesDob.setWeightClass(weightClass);
					 sellRatesDob.setOriginCountry(originCountry);
					 sellRatesDob.setShipmentMode(shipmentMode);
					 sellRatesDob.setCarrier_id(carrierId);
					 sellRatesDob.setRateType(rateType);
					 sellRatesDob.setWeightBreak(weightBreak);
					 sellRatesDob.setDestination(destination);
					 sellRatesDob.setDestinationCountry(destinationCountry);
					 sellRatesDob.setServiceLevel(serviceLevelId);
					 Logger.info(FILE_NAME,"operation  "+operation);
					 initial = new InitialContext();
					 home	  =	(BuyRatesSessionHome)initial.lookup("BuyRatesSessionBean");
					 remote	  =	(BuyRatesSession)home.create();
					 rateList = remote.getSellRatesValues(sellRatesDob,loginbean,operation);
					 //enum     =  rateHashTable.keys();
					 Logger.info(FILE_NAME,"rateList          "+rateList.size());
					 if(rateList!=null)
					{
							sellRatesDob	=	(QMSSellRatesDOB)rateList.get(0);

							currencyId		=	sellRatesDob.getCurrencyId();
							origin			=	sellRatesDob.getOrigin();
							originCountry		=	sellRatesDob.getOriginCountry();
							destination		=	sellRatesDob.getDestination();
							destinationCountry	=	sellRatesDob.getDestinationCountry();
							serviceLevelId	=	sellRatesDob.getServiceLevel();
							carrierId		=	sellRatesDob.getCarrier_id();

							shipmentMode			=	sellRatesDob.getShipmentMode();
							weightBreak				=	sellRatesDob.getWeightBreak();
							rateType				=	sellRatesDob.getRateType();
							weightClass				=	sellRatesDob.getWeightClass();
							consoleType 				=	sellRatesDob.getConsoleType();
							if("1".equals(shipmentMode) || ("2".equals(shipmentMode) && "LCL".equals(consoleType )) || ("4".equals(shipmentMode) && "LTL".equals(consoleType )))
							{
								if("Flat".equals(sellRatesDob.getWeightBreak()))
								{
										mapValues		=	(HashMap)rateList.get(1);
										System.out.println("mapValuesmapValuesmapValuesmapValuesmapValues ::: "+mapValues.size());
								}
								else if("Slab".equals(sellRatesDob.getWeightBreak()))
								{
										headerlist			=	(ArrayList)rateList.get(1);
										weightBreakList		=	(ArrayList)headerlist.get(0);
										session.setAttribute("weightBreak",weightBreakList);
										mapValues			=	(HashMap)headerlist.get(1);
								}
								else if("List".equals(sellRatesDob.getWeightBreak()))
								{
										headerlist			=	(ArrayList)rateList.get(1);
										weightBreakList		=	(ArrayList)headerlist.get(0);
										session.setAttribute("weightBreak",weightBreakList);
										mapValues			=	(HashMap)headerlist.get(1);	
								}
							}
							else
							{
									headerlist			=	(ArrayList)rateList.get(1);
									weightBreakList		=	(ArrayList)headerlist.get(0);
									session.setAttribute("weightBreak",weightBreakList);
									mapValues			=	(HashMap)headerlist.get(1);
									itr					=	(mapValues.keySet()).iterator();
							}
							System.out.println("consoleType consoleType  in Jsp :: "+consoleType );
							System.out.println("getWeightBreakgetWeightBreakgetWeightBreak : "+sellRatesDob.getWeightBreak());
							System.out.println("getRateTypegetRateTypegetRateTypegetRateType : "+sellRatesDob.getRateType());

					}
					out.clearBuffer();
					response.setContentType("application/vnd.ms-excel");	
					String contentDisposition = " :attachment;";	
					response.setHeader("Content-Disposition","attachment;filename=RateMaster.xls");
					Logger.info(FILE_NAME,"enum          "+enum1);
	 if(mapValues!=null && mapValues.size()>0)
	 {
		if(sellRatesDob!=null && ("1".equals(sellRatesDob.getShipmentMode()) 
		|| ("2".equals(sellRatesDob.getShipmentMode()) && "LCL".equals(sellRatesDob.getConsoleType())) 
		|| ("4".equals(sellRatesDob.getShipmentMode()) && "LTL".equals(sellRatesDob.getConsoleType()))))
		{
			if("Flat".equals(sellRatesDob.getWeightBreak()))
			{  
			   
			   out.print("ORIGIN:\t" );
			   out.print("ORIGIN COUNTRY:\t" );
			   out.print("DESTINATION:\t" );
			   out.print("DESTINATION COUNTRY:\t" );
			   out.print("EFFECTIVE FROM:\t" );
			   out.print("VALID UPTO:\t" );
			   out.print("CURRENCY:\t" );
			   out.print("CARRIER ID:\t" );
			   out.print("SERVICELEVEL:\t" );
			   out.print("FREQUENCY:\t" );
			   out.print("TRANSIT TIME:\t" );  
			   out.print("MIN:\t" );  
			   out.print("FLAT:\t" );  
			   out.print("NOTES:\t" );  
			   out.println();
			   itr				=	(mapValues.keySet()).iterator();
				int i=0;
				while(itr.hasNext())
				{
					keyHash	=	(String)itr.next();
					QMSSellRatesDOB sellRatesDob1	= (QMSSellRatesDOB)mapValues.get(keyHash);
					System.out.println("sellRatesDob1.getLanNumber()"+sellRatesDob1.getLanNumber()+"  "+sellRatesDob1.getBuyRateId());
			    	   		   
		   	   
				   out.print(sellRatesDob1.getOrigin()+"\t");
				   out.print(sellRatesDob1.getOriginCountry()+"\t");
				   out.print(sellRatesDob1.getDestination()+"\t");
				   out.print(sellRatesDob1.getDestinationCountry()+"\t");
				   utility.setPattern("DD-MM-YY");
				   out.print(utility.getDisplayString(sellRatesDob1.getEffectiveFrom())+"\t");
				   out.print(utility.getDisplayString(sellRatesDob1.getValidUpto())+"\t");
				   out.print(sellRatesDob1.getCurrencyId()+"\t");
				   out.print(sellRatesDob1.getCarrier_id()+"\t");		   
				   out.print(sellRatesDob1.getServiceLevel()+"\t"); 
				   out.print(sellRatesDob1.getFrequency()+"\t");
				   out.print(sellRatesDob1.getTransitTime()+"\t");			   
				   out.print(sellRatesDob1.getMinimumRate()+"\t"); 
				   out.print(sellRatesDob1.getFlatRate()+"\t"); 	
				   out.print(sellRatesDob1.getNotes()+"\t"); 
				   out.println();

		      }		 
	       }
		   else if(sellRatesDob!=null && ("Slab".equals(sellRatesDob.getWeightBreak()) || "List".equals(sellRatesDob.getWeightBreak())))
		   {
			   out.print("ORIGIN:\t" );
			   out.print("ORIGIN COUNTRY:\t" );
			   out.print("DESTINATION:\t" );
			   out.print("DESTINATION COUNTRY:\t" );
			   out.print("EFFECTIVE FROM:\t" );
			   out.print("VALID UPTO:\t" );
			   out.print("CURRENCY:\t" );
			   out.print("CARRIER ID:\t" );
			   out.print("SERVICELEVEL:\t" );
			   out.print("FREQUENCY:\t" );
			   out.print("TRANSIT TIME:\t" );
			   if(weightBreakList!=null)
			   {
				String	weightBreakValue	=	null;
				int weightBreakSize		=	weightBreakList.size();
				for(int i=0;i<weightBreakSize;i++)
				{
					weightBreakValue	=	(String)weightBreakList.get(i);
					out.print(weightBreakValue+"\t" );					
				}
			 }
			 out.println("NOTES:\t" ); 
			 int i=0;
			 itr					=	(mapValues.keySet()).iterator();
			 while(itr.hasNext())
			 {
				keyHash	=	(String)itr.next();
				QMSSellRatesDOB sellRatesDob1	= (QMSSellRatesDOB)mapValues.get(keyHash);
				chargeDtlList					=  sellRatesDob1.getBoundryList();
				int weightBreSize		=	weightBreakList.size();
				String	weightBreakValues	=	null;
				String  flagValue			=	"";
				String  minFlat				=	"";
				boolean	flag				=	false;
				out.print(sellRatesDob1.getOrigin()+"\t");
			   out.print(sellRatesDob1.getOriginCountry()+"\t");
			   out.print(sellRatesDob1.getDestination()+"\t");
			   out.print(sellRatesDob1.getDestinationCountry()+"\t");
			   utility.setPattern("DD-MM-YY");
			   out.print(utility.getDisplayString(sellRatesDob1.getEffectiveFrom())+"\t");
			   out.print(utility.getDisplayString(sellRatesDob1.getValidUpto())+"\t");
			   out.print(sellRatesDob1.getCurrencyId()+"\t");
			   out.print(sellRatesDob1.getCarrier_id()+"\t");		   
			   out.print(sellRatesDob1.getServiceLevel()+"\t"); 
			   out.print(sellRatesDob1.getFrequency()+"\t");
			   out.print(sellRatesDob1.getTransitTime()+"\t");			   
			   
				for( k=0;k<weightBreSize;k++)
				{
					flag				=	false;
					weightBreakValues	=	(String)weightBreakList.get(k);
					
					int chargeDtlSize	=	chargeDtlList.size();
					for(int j=0;j<chargeDtlSize;j++)
					{
						boundryDob	=	(QMSBoundryDOB)chargeDtlList.get(j);
						if(boundryDob.getWeightBreak().equals(weightBreakValues))
						{
							flag				=	true;
							break;
						}
					}
					if(flag)
					{
						if(k>0 && "Both".equalsIgnoreCase(rateType) && "slab".equalsIgnoreCase(weightBreak))
						{
							String chargeIndicaror = "";
							if("Slab".equalsIgnoreCase(boundryDob.getChargerateIndicator()))
								chargeIndicaror  = "S";
							else if("Flat".equalsIgnoreCase(boundryDob.getChargerateIndicator()))
								chargeIndicaror  = "F";							   
						  out.print(boundryDob.getChargeRate()+""+chargeIndicaror+"\t"); 
					    }
						else
							out.print(boundryDob.getChargeRate()+"\t"); 

					}
					else
					{
						out.print("NA \t"); 
					}		   
			  }
			   out.print(sellRatesDob1.getNotes()+"\t"); 
			   out.println();
			  i++;
		   }
		  }
		}
		else
        {
			   out.print("ORIGIN:\t" );
			   out.print("ORIGIN COUNTRY:\t" );
			   out.print("DESTINATION:\t" );
			   out.print("DESTINATION COUNTRY:\t" );
			   out.print("EFFECTIVE FROM:\t" );
			   out.print("VALID UPTO:\t" );
			   out.print("CURRENCY:\t" );
			   out.print("CARRIER ID:\t" );
			   out.print("SERVICELEVEL:\t" );
			   out.print("FREQUENCY:\t" );
			   out.print("TRANSIT TIME:\t" );
			if(weightBreakList!=null)
			{
				String	weightBreakValue	=	null;
				int weightBreakSize		=	weightBreakList.size();
				for(int i=0;i<weightBreakSize;i++)
				{
					weightBreakValue	=	(String)weightBreakList.get(i);
					out.print(weightBreakValue+"\t" );
				}
				out.println("NOTES:\t" );
			}
			int i=0;
			itr					=	(mapValues.keySet()).iterator();
			while(itr.hasNext())
			{
				keyHash	=	(String)itr.next();
				QMSSellRatesDOB sellRatesDob1	= (QMSSellRatesDOB)mapValues.get(keyHash);
				chargeDtlList					=  sellRatesDob1.getBoundryList();
				out.print(sellRatesDob1.getOrigin()+"\t");
			   out.print(sellRatesDob1.getOriginCountry()+"\t");
			   out.print(sellRatesDob1.getDestination()+"\t");
			   out.print(sellRatesDob1.getDestinationCountry()+"\t");
			   utility.setPattern("DD-MM-YY");
			   out.print(utility.getDisplayString(sellRatesDob1.getEffectiveFrom())+"\t");
			   out.print(utility.getDisplayString(sellRatesDob1.getValidUpto())+"\t");
			   out.print(sellRatesDob1.getCurrencyId()+"\t");
			   out.print(sellRatesDob1.getCarrier_id()+"\t");		   
			   out.print(sellRatesDob1.getServiceLevel()+"\t"); 
			   out.print(sellRatesDob1.getFrequency()+"\t");
			   out.print(sellRatesDob1.getTransitTime()+"\t");
				int weightBreSize		=	weightBreakList.size();
				String	weightBreakValues	=	null;
				String  flagValue			=	"";
				String  minFlat				=	"";
				boolean	flag				=	false;
				for( k=0;k<weightBreSize;k++)
				{
					flag				=	false;
					weightBreakValues	=	(String)weightBreakList.get(k);
					
					int chargeDtlSize	=	chargeDtlList.size();
					for(int j=0;j<chargeDtlSize;j++)
					{
						boundryDob	=	(QMSBoundryDOB)chargeDtlList.get(j);
						if(boundryDob.getWeightBreak().equals(weightBreakValues))
						{
							flag				=	true;
							break;
						}
					}
					if(flag)
					{
						out.print(boundryDob.getChargeRate()+"\t"); 
					}
					else
					{
						out.print("NA \t"); 
					}
			  }
			  i++;
		    }
	      }
	 }
	 else if(rateList!=null && (mapValues!=null && mapValues.size()==0))
	{
		 out.print("No Rates Are Defined for the Specified Details"); 
	}
 }
}
catch(Exception e)
{
	e.printStackTrace();
}
  
%>
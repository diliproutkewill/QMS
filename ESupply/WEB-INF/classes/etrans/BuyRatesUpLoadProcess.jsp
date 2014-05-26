<!-- 
File				: BuyRatesUpLoadProcess.jsp
Sub-module		    : Upload Index Process Page For Buy Rates
Module			    : QMS
Author              : RamaKrishna Y.
date				: 14-07-2005

Modified Date	Modified By			Reason
-->
<%@ page import="java.sql.Timestamp,
				 com.oreilly.servlet.multipart.*,
				 java.io.BufferedReader,
				 java.io.InputStreamReader,
				 java.io.FileInputStream,
				 java.util.ArrayList,
                 java.util.HashMap,
				 java.util.Hashtable,
				 java.util.Enumeration,
                 java.io.File,
				 java.text.*,
				 java.util.Calendar,
                 java.util.Iterator,
				 java.util.ListIterator,
				 java.util.StringTokenizer,
				 javax.naming.InitialContext,
				 com.qms.operations.rates.ejb.sls.BuyRatesSession,
         com.qms.operations.rates.ejb.sls.BuyRatesSessionHome,
				 com.foursoft.esupply.common.util.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.qms.operations.rates.dob.RateDOB,
				 java.util.Date,
				 com.qms.operations.rates.dob.FlatRatesDOB,
				 com.foursoft.esupply.common.util.ESupplyDateUtility,
				 com.foursoft.esupply.common.java.KeyValue" %>

<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />

<%! public static final String FILE_NAME  =  "BuyRatesUpLoadProcess.jsp"; 
    int iSeparator                        =   0;
	boolean   bNumber                     =   false;
	private ArrayList checkDuplicateRecords(ArrayList rateList)
    {
     ArrayList     tempList   =  new ArrayList();
     FlatRatesDOB  flatRateDOB    =  null;
     FlatRatesDOB  tempDOB    =  null;
      try
      {
        for(int i=0;i<rateList.size();i++)
        {
          tempDOB  = (FlatRatesDOB)rateList.get(i);
          for(int j=i+1;j<rateList.size();j++)
          {
            flatRateDOB  =  (FlatRatesDOB)rateList.get(j);
            if(tempDOB.getOrigin().equalsIgnoreCase(flatRateDOB.getOrigin()) && tempDOB.getDestination().equalsIgnoreCase(flatRateDOB.getDestination()) && tempDOB.getServiceLevel().equalsIgnoreCase(flatRateDOB.getServiceLevel())  && tempDOB.getEffDate().toString().equalsIgnoreCase(flatRateDOB.getEffDate().toString()) && tempDOB.getValidUpto().toString().equalsIgnoreCase(flatRateDOB.getValidUpto().toString()) && tempDOB.getCarrierId().equalsIgnoreCase(flatRateDOB.getCarrierId()))
            {
				flatRateDOB.setSlNo(j);
                tempList.add(flatRateDOB);
            }
          }
        }
      }
      catch(Exception e)
      {
        e.printStackTrace();
        Logger.error(FILE_NAME,"Error in checkDuplicateRecords"+e.toString());
      }
      return tempList;     
    }
    public boolean checkNumber(char[] cArr)
	{
		for(int cnt=0; cnt<cArr.length;++cnt){
    		// cArr[cnt] is a valid figure - true
    		if(Character.isDigit(cArr[cnt])){
    			bNumber = true;    			
    		// cArr[cnt] is no figure, but '.' or ','	
    		}else{
    			return false;
    		}
    	}return bNumber;
	}
	public String dtCheck(String dtobj)
	{
		String alert = null;
		String edt = dtobj;
		//alert = "dtobj name = "+dtobj.name+"\ndtobj value = "+dtobj.value);		
					
		if(edt.length()>10) {
			alert = "The Date format is invalid. Please enter the date in 'DD/MM/YY[YY]' format";			
			return alert;
		}
		String seps = "/";
		String seph = "-";
		if( (edt.indexOf(seps)== -1) && (edt.indexOf(seph)== -1)) {
			alert = "The Date format is invalid. Please enter the date in 'DD/MM/YY[YY]' format";			
			return alert;	
		}
		
		int fpos = edt.indexOf(seps);
		
		if(fpos==-1)
			fpos = edt.indexOf(seph);

		int spos = edt.indexOf(seps,fpos+1);
		if(spos==-1)
			spos = edt.indexOf(seph,fpos+1);

		String day1 = edt.substring(0,fpos);
		String mon1 = edt.substring(fpos+1,spos);
		String year1 = edt.substring(spos+1);

		if(year1.length()==0) {
			alert = "Please enter the 'year' of the date.";
			return alert;
		}
		
		if(year1.length()==3) {
			alert = "The 'year' in the date is not valid.";
			return alert;
		}
		
		if(day1.length()>2 || mon1.length()>2 || year1.length()>4) {
			alert = "This is not a valid date.";
			return alert;
		}

		/*if(isNaN(day) || isNaN(mon) || isNaN(year)) {
			alert = "This is not a valid date.";
			
			return false;
		}
		
		if(isNaN(day)) {
			alert = "Please enter only numeric value(s) for the 'day'.";
			
			return false;
		}
		
		if(isNaN(mon)) {
			alert = "Please enter only numeric value(s) for the 'month'.";
			
			return false;
		}
		
		if(isNaN(year)) {
			alert = "Please enter only numeric value(s) for the 'year'.";
			
			return false;
		}*/
        int mon = new Integer(mon1).intValue();
		int day = new Integer(day1).intValue();
		int year = new Integer(year1).intValue();
		//year = y2k1(year);
		if (mon>12 || mon <1) {
			alert = "The 'month' in the date is not valid.";			
			return alert;
		}
		
		if ((mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) && (day > 31 || day < 1)) {
			alert = "The 'day' in the date is not valid.";
			return alert;
		}
		
		if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && (day > 30 || day < 1)) {
			alert = "The 'day' in the date is not valid.";
			
			return alert;
		}

		if (mon == 2)  {
			if (day < 1) {
				alert = "The 'day' in the date is not valid.";
				return alert;
			}
			if (LeapYear(year))  {
				if (day > 29)  {
					alert = "There cannot be more than 29 days in a leap year.";
					return alert;
				}
			} else  {
				if (day > 28)  {
					alert = "There cannot be more than 28 days in a year other than a leap year.";
					return alert;
				}
			}
		 }

		//day = padout1(day);
		//mon = padout1(mon);

		dtobj = day+"/"+mon+"/"+year  ;
		
		return dtobj;

		//	alert = dtobj.value  +   "is a valid date") ;
		
	} // end dtCheck
	public boolean LeapYear(int intYear) 
	{
		if (intYear % 100 == 0) 
			if (intYear % 400 == 0) 
					return true; 
		else if ((intYear % 4) == 0)
				return true; 

		return false;
	}
	/*public String y2k1(int num) 
	{

		//String number = ""+num;
		//String number = parseInt(strNumber, 10);
		
		//alert("year = '"+number+"'");

		if(number == 0) {
			number =  2000;
		}

		if (number >= 1  && number <= 99 )
		{
			if(number >=1 & number <= 9) {
				number =  '200' + number;
			}
			if(number >= 10 & number <= 99) {
				number =  '20' + number;
			}
		}
		//alert("year = "+number);
		return number ;
	}*/
	
	
%>

<%
          ArrayList       keyValueList      =   null;
		  ErrorMessage    message           =   null;
          String          operation         =   request.getParameter("Operation");
  try
  {	  
		  
		  MultipartParser mp                =   new MultipartParser(request, 10*1024*1024); // 10MB
		  Part            part              =   null;
		  String          name              =   null;
		  String          value             =   null;
		  FilePart        filePart          =   null;
		  ParamPart       paramPart         =   null;
		  FileInputStream fileRead          =   null;
          String          fileName          =   null;
		  long            size              =   0;
		  BufferedReader  br                =   null;
		  String          data              =   null;
		  int             delimIndex        =   0;
		  int             i                 =   1;
          String          tempString        =   null;
		  String          subString         =   null;
		  int             beginIndex        =   0;
		  RateDOB         buyRateDOB        =   null;
		  FlatRatesDOB    flatRatesDOB      =   null;
          String          shipmentMode      =   null;
		  String          currencyId        =   null;
		  String          carrierId         =   null;	
		  String          weightBreak       =   null;
		  String          rateType          =   null;
		  String          origin            =   null;
		  String          tempOrigin        =   null;
		  String          destination       =   null;
		  String          serviceLevel      =   null;
		  String          frequency         =   null;
		  String          tempFrequency     =   null;
		  String          transitTime       =   null;
		  String          weightBreakSlab   =   null;
		  String          chargeRate        =   null;
		  String          weightClass       =   null;
		  String          effFromDate       =   null;
		  String          validUpto         =   null;
		  String          overPiot          =   null;
		  String          uom               =   null;
		  String          consoleType       =   null;
		  String          chargeSlab        =   null;
		  int             lowerBound        =   0;
		  int             upperBound        =   0;
		  int             laneNo            =   -1;
		  int             lineNo            =   -1;
		  String          process           =   null;
		  String          type              =   null;
		  String          errorMode         =   null;
		  String          remarks           =   "";
		  int             rowId             =   -1;
		  int             count             =    0;
		  boolean         flag              =   false;
		  StringTokenizer strToken          =   null;
		  StringTokenizer freStrToken       =   null;
		  ArrayList       flatRatesList     =   null;
		  ArrayList       successList       =   new ArrayList();
		  ArrayList       failureList       =   new ArrayList();
		  ArrayList       exList            =   new ArrayList();
		  ArrayList       duplicateList     =   new ArrayList();
		  ArrayList       newList           =   new ArrayList();		  
		  Hashtable       buyRateDOBHashtable =  new Hashtable();
		  ArrayList       chargeRateList      =  new ArrayList();
		  ArrayList       wtBreakList         =  new ArrayList();
		  ArrayList       slabFlatList        =  new ArrayList();
		  ESupplyDateUtility		fomater   =   new ESupplyDateUtility();
		  Logger.info(FILE_NAME,"DAte format         "+loginbean.getUserPreferences().getDateFormat());
		  
		  while ((part = mp.readNextPart()) != null)
		  {
			  name = part.getName();
			  if (name != null)
                name = name.trim();
			  if (part.isParam())
			  {   				  
				paramPart         =        (ParamPart) part;
				value             =        paramPart.getStringValue();
				
				if (name.equals("Process")){
					process = value;               
                }
				if(name.equals("type")){
					type = value;                    
				}	
				if(name.equals("errorMode")){
					errorMode = value;
				}
				if(name.equals("Operation")){
					operation = value;
				}

             }
			 else if (part.isFile())
			 {
				filePart         =         (FilePart) part;
				fileName         =         filePart.getFileName();
				if (fileName != null){ 
				  fileName = fileName.trim();
				  size = filePart.writeTo(new File("./"));
				}else{ 
					// the field did not contain a file
					
				}
			}//else if
		}//while
		fileRead            =        new FileInputStream(fileName);	 
		br                  =        new BufferedReader(new InputStreamReader(fileRead));
		data                =        br.readLine();
		data                =        br.readLine();
			
		if(!data.equals("") && data!=null)
	    {
			data           +=        ",";
			tempString     =         "";
			beginIndex     =         0;
			for(int noOfCols=0;noOfCols<7; noOfCols++) 
		    {
				delimIndex  =  data.indexOf(",");
				Logger.info(FILE_NAME,"delimIndex   "+delimIndex);
				if(delimIndex==0){
					if(noOfCols==0 ){
						tempString=tempString+" ,";
					}
					if(noOfCols==1){
							tempString=tempString+" ,";
					}
					if(noOfCols==2){
						tempString=tempString+" ,";
					}
					if(noOfCols==3 ){
						tempString=tempString + " ,";
					}
					if(noOfCols==4 ){
						tempString=tempString + " ,";
					}
					if(noOfCols==5 ){
						tempString=tempString + " ,";
					}
					if(noOfCols==6 ){
						tempString=tempString + " ,";
					}	
					if(noOfCols==7 ){
						tempString=tempString + " ,";
					}
					
					
					Logger.info(FILE_NAME,"noOfCols   "+noOfCols);
					Logger.info(FILE_NAME,"tempString   "+tempString);
               }
			   else {				
				subString=data.substring(beginIndex,delimIndex);
				tempString=tempString + subString+",";	
				Logger.info(FILE_NAME,"tempString else  "+tempString);
			  } 
			  data             =     data.substring(delimIndex+1);
			  Logger.info(FILE_NAME,"data   "+data);
			  beginIndex       =     0;
			}
			  
			  data             =     tempString.substring(0,tempString.length());
			  Logger.info(FILE_NAME,"data   "+data);
			  strToken         =     new StringTokenizer(data,",");
			  if(strToken.hasMoreElements())
			  {
					  
					  shipmentMode 	= strToken.nextToken();
					  if(shipmentMode!=null && !shipmentMode.trim().equals("")){
							shipmentMode = shipmentMode.toUpperCase();									
							if(shipmentMode.length() > 5)
								remarks = remarks + " Shipment Mode Must Be  Air,Sea,or Truck . ";
							else{
								if("AIR".equals(shipmentMode))
									 shipmentMode = "1";
								else if("SEA".equals(shipmentMode))
									shipmentMode = "2";
								else if("TRUCK".equals(shipmentMode))
									shipmentMode = "4";
								else
                                    remarks = remarks + "Shipment Mode  Must Be  Air,Sea,or Truck . ";

							}
					  }else
							remarks = remarks + "Mandatory fields are not provided (Shipment  Mode). ";
					 Logger.info(FILE_NAME,"shipmentMode  "+shipmentMode );

					  currencyId 	= strToken.nextToken();
					  if(currencyId!=null && !currencyId.trim().equals("")){
							currencyId = currencyId.toUpperCase();	
							if(currencyId.length() > 3)
								remarks = remarks + "Max Length Must Be  3 (Currency Id). ";
					  }else
							remarks = remarks + "Mandatory fields are not provided (Location Id). ";
					   Logger.info(FILE_NAME,"currencyId  "+currencyId );

					  

					  weightBreak 	= strToken.nextToken();
					  if(weightBreak!=null && !weightBreak.trim().equals("")){
							weightBreak = weightBreak.toUpperCase();
							if(!("FLAT".equalsIgnoreCase(weightBreak)||"SLAB".equalsIgnoreCase(weightBreak)||"LIST".equalsIgnoreCase(weightBreak)))
								remarks = remarks + "Weight Break should be Either FLAT,SLAB or List";
					        if(weightBreak.length() > 8)
								remarks = remarks + "Max Length Must Be  8 (Weight Break ). ";
					  }else
							remarks = remarks + "Mandatory fields are not provided (Weight Break ). ";
					   Logger.info(FILE_NAME,"weightBreak  "+weightBreak );

					  weightClass 	= strToken.nextToken();
					  if(weightClass!=null && !weightClass.trim().equals("")){
							weightClass = weightClass.toUpperCase();	
							if(weightClass.length() > 10)
								remarks = remarks + "Max Length Must Be  10 (Weight Class ). ";
							if("General".equalsIgnoreCase(weightClass))
								weightClass  = "G";
							else
								weightClass  = "";
					  }else
							remarks = remarks + "Mandatory fields are not provided (Weight Class ). ";
					   Logger.info(FILE_NAME,"weightClass  "+weightClass );

					  rateType 	= strToken.nextToken();
					  if(rateType!=null && !rateType.trim().equals("")){
							rateType = rateType.toUpperCase();	
							if("FLAT".equalsIgnoreCase(weightBreak)){
								if(!"FLAT".equalsIgnoreCase(rateType))
								   remarks = remarks + "RateType Should be FLAT. ";  
							}
							else if("SLAB".equalsIgnoreCase(weightBreak)){								       
								if(!("FLAT".equalsIgnoreCase(rateType)||"SLAB".equalsIgnoreCase(rateType)||"BOTH".equalsIgnoreCase(rateType)))
								   remarks = remarks + "RateType Should be Either FLAT,SLAB or BOTH. ";  
							}
							else if("LIST".equalsIgnoreCase(weightBreak) && "1".equalsIgnoreCase(shipmentMode)){
								if(!("PIVOT".equalsIgnoreCase(rateType)))
								   remarks = remarks + "RateType Should be PIVOT. ";  
							}
							else{
								if(!("FLAT".equalsIgnoreCase(rateType)))
									remarks = remarks + "RateType Should be FLAT. ";  
							}
							if(rateType.length() > 8)
								remarks = remarks + "Max Length Must Be  8 (Rate Type ). ";
					  }else
							remarks = remarks + "Mandatory fields are not provided (Rate Type ). ";

					  uom 	= strToken.nextToken();
					  if(uom!=null && !uom.trim().equals("")){
							uom = uom.toUpperCase();	
							if(uom.length() > 8)
								remarks = remarks + "Max Length Must Be  8 (UOM ). ";
                            else if(!"KG".equalsIgnoreCase(uom) && !"LB".equalsIgnoreCase(uom)&& !"CBM".equalsIgnoreCase(uom) && !"CFT".equalsIgnoreCase(uom))
							  {
								remarks = remarks + "UOM Should be either KG,LB,CBM or CFT. ";
							  }
					  }else
							remarks = remarks + "Mandatory fields are not provided (UOM ). ";
					  Logger.info(FILE_NAME,"uom  "+uom );
					  

					  consoleType 	= strToken.nextToken();
					  if(consoleType!=null && !consoleType.trim().equals("")){						    
							consoleType = consoleType.toUpperCase();	
							if("1".equalsIgnoreCase(shipmentMode) ){
								if(consoleType.length()>0)
								  remarks = remarks + "The ConsoleType Should be Empty. ";
							}
							if("2".equalsIgnoreCase(shipmentMode) ){
								if(!("LCL".equalsIgnoreCase(consoleType)||"FCL".equalsIgnoreCase(consoleType)))
								    remarks = remarks + "The ConsoleType Should be Either LCL or FCL. ";
								else if("List".equalsIgnoreCase(weightBreak) && !"FCL".equalsIgnoreCase(consoleType))								  
									remarks = remarks + "The ConsoleType Should be FCL. ";
							    else if(!"LCL".equalsIgnoreCase(consoleType) &&  !"List".equalsIgnoreCase(weightBreak))
									remarks = remarks + "The ConsoleType Should be LCL. ";
							}
								
							if( "4".equalsIgnoreCase(shipmentMode)){
							  if(!("LTL".equalsIgnoreCase(consoleType)||"FTL".equalsIgnoreCase(consoleType)))
								    remarks = remarks + "The ConsoleType Should be Either LTL or FTL. ";
							  else if("List".equalsIgnoreCase(weightBreak) && !"FTL".equalsIgnoreCase(consoleType))								  
									remarks = remarks + "The ConsoleType Should be FTL. ";
							    else if(!"LTL".equalsIgnoreCase(consoleType) &&  !"List".equalsIgnoreCase(weightBreak))
									remarks = remarks + "The ConsoleType Should be LTL. ";
							}
							if(consoleType.length() > 8)
								remarks = remarks + "Max Length Must Be  8 (ConsoleType ). ";
					  }else{
						  if("2".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode))
							remarks = remarks + "Mandatory fields are not provided (ConsoleType ). ";
					  }
					   Logger.info(FILE_NAME,"consoleType  "+consoleType );

					  Logger.info(FILE_NAME,"remarks  "+remarks );
					  buyRateDOB      =   new RateDOB();
					  buyRateDOB.setShipmentMode(shipmentMode);
					  buyRateDOB.setCurrency(currencyId);					  
					  buyRateDOB.setWeightBreak(weightBreak);
					  buyRateDOB.setRateType(rateType);
					  buyRateDOB.setWeightClass(weightClass);
					  buyRateDOB.setTerminalId(loginbean.getTerminalId());					  
					  buyRateDOB.setUom(uom);
					  buyRateDOB.setConsoleType(consoleType);
					  buyRateDOB.setUser(loginbean.getUserId());
					  buyRateDOB.setRateDtls(new ArrayList());
					  buyRateDOB.setCreatedTime(loginbean.getLocalTime());
					  buyRateDOB.setAccessLevel(loginbean.getAccessType());
					  buyRateDOB.setRemarks(remarks);
			  }
              data=br.readLine();
			   Logger.info(FILE_NAME,"data  "+data );
				//if (data==null)
				//	break;				
				//remarks = "";
				i++;
				Logger.info(FILE_NAME,"rowId 1234  "+rowId);
				buyRateDOBHashtable.put(new Integer(rowId),buyRateDOB);
				rowId = -1;
			}
			
            //data          =        br.readLine();
		    data          =        br.readLine();
			strToken      =        new StringTokenizer(data,",");
			int totalColumnsCount         =        strToken.countTokens();
		    Logger.info(FILE_NAME,"countTokens()   "+strToken.countTokens() );
			Logger.info(FILE_NAME,"data  3"+data );
			rowId         =        -1;
		while(!data.equals("") && data!=null)
	    {   remarks        =       "";
			data           +=        ",";
			tempString     =         "";
			beginIndex     =         0;
            Logger.info(FILE_NAME,"totalColumnsCount  "+totalColumnsCount );
			for(int noOfCols=0;noOfCols<totalColumnsCount; noOfCols++) 
		    {
				delimIndex  =  data.indexOf(",");
				if(delimIndex==0){					
					tempString=tempString + " ,";
                }
			   else {				
				subString=data.substring(beginIndex,delimIndex);
				tempString=tempString + subString+",";				
			  } 
			  data             =     data.substring(delimIndex+1);
			  beginIndex       =     0;
			  Logger.info(FILE_NAME,noOfCols+"  data  "+data );
			} 
			  data             =     tempString.substring(0,tempString.length());
			  strToken         =     new StringTokenizer(data,",");
			  if(strToken.hasMoreElements())
			  {   
				  if(count==0)
				  {
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  strToken.nextToken();
					  for(int k=0;k<totalColumnsCount-8;k++)
					  {
						wtBreakList.add(strToken.nextToken());
					  }
					  buyRateDOB.setWtBreakList(wtBreakList);
				  }			
				  else
				  {
						lineNo++;
						
						 origin	= strToken.nextToken();
						  if(origin!=null && !origin.trim().equals("")){
								origin = origin.toUpperCase();	
								if(origin.length() > 3)
									remarks = remarks + "Max Length Must Be  3 (Origin). ";
						  }else
								remarks = remarks + "Mandatory fields are not provided (Origin). ";
						   Logger.info(FILE_NAME,"origin  "+origin );
				  
						  destination 	= strToken.nextToken();
						  if(destination!=null && !destination.trim().equals("")){
								destination = destination.toUpperCase();	
								if(destination.length() > 8)
									remarks = remarks + "Max Length Must Be  8 (Destination ). ";
						  }else
								remarks = remarks + "Mandatory fields are not provided (Destination ). ";
                           Logger.info(FILE_NAME,"destination  "+destination );
							
							  carrierId 	= strToken.nextToken();
							  if(carrierId!=null && !carrierId.trim().equals("")){
									carrierId = carrierId.toUpperCase();	
									if(carrierId.length() > 5)
										remarks = remarks + "Max Length Must Be  5 (Carrier Id ). ";
							  }else
									remarks = remarks + "Mandatory fields are not provided (Carrier Id ). ";
							   Logger.info(FILE_NAME,"carrierId  "+carrierId );
						  serviceLevel 	= strToken.nextToken();
						  if(serviceLevel!=null && !serviceLevel.trim().equals("")){
								serviceLevel = serviceLevel.toUpperCase();	
								if(serviceLevel.length() > 8)
									remarks = remarks + "Max Length Must Be  8 (Service Level ). ";
						  }else
								remarks = remarks + "Mandatory fields are not provided (Service Level ). ";
                          Logger.info(FILE_NAME,"serviceLevel  "+serviceLevel );

						  tempFrequency 	= strToken.nextToken();
						  if("1".equalsIgnoreCase(shipmentMode) || "4".equalsIgnoreCase(shipmentMode)){
						  if(tempFrequency!=null && !tempFrequency.trim().equals(""))
						     frequency     = tempFrequency.replace('&',',');
						  }
						  else
                             frequency     = tempFrequency;

						  Logger.info(FILE_NAME,"tempFrequency  "+tempFrequency );						  
						  if(frequency!=null && !frequency.trim().equals("")){
								frequency = frequency.toUpperCase();	
								if(frequency.length() > 8)
									remarks = remarks + "Max Length Must Be  8 (Frequency ). ";
								else
									if("Sea".equalsIgnoreCase(shipmentMode)) 
									   if(!"Weekly".equalsIgnoreCase(frequency) && !"Fortnightly".equalsIgnoreCase(frequency) && !"Monthly".equalsIgnoreCase(frequency))
										   remarks = remarks + "Frequency Should be either Weekly, Fortnightly or Monthly. ";
								     if("Air".equalsIgnoreCase(shipmentMode) || "truck".equalsIgnoreCase(shipmentMode))
									  {
										 StringTokenizer strTok  =  new StringTokenizer(frequency,",");
										 while(strTok.hasMoreTokens())
										  {
											 int num = new Integer(strTok.nextToken()).intValue();
											 if(num != 1 && num != 2 && num != 3 && num != 4 && num!= 5 && num != 6 && num != 7){
												 remarks = remarks + "Frequency Should be either 1,2,3,4,5,6 or 7. ";			
												 break;
											 }
										  }
									  }
								    
									 
						  }else
								remarks = remarks + "Mandatory fields are not provided (Frequency ). ";
						  
						  Logger.info(FILE_NAME,"frequency  "+frequency );

						  transitTime 	= strToken.nextToken();
						  if(transitTime!=null && !transitTime.trim().equals("")){
								transitTime = transitTime.toUpperCase();	
								if(transitTime.length() > 6)
									remarks = remarks + "Max Length Must Be  6 (TransitTime ). ";
								else
								  {
									if("Air".equalsIgnoreCase(shipmentMode) || "truck".equalsIgnoreCase(shipmentMode))
									    if(transitTime.length()==3)
										   transitTime  =  transitTime.substring(0,0)+":"+transitTime.substring(1,transitTime.length());
										else if(transitTime.length()==4)
										   transitTime  =  transitTime.substring(0,1)+":"+transitTime.substring(2,transitTime.length());
										else if(transitTime.indexOf(":")<0 && transitTime.length()==5)
										      remarks = remarks + "The Transit Time format should be HH:MM. ";
								  }
						  }else
								remarks = remarks + "Mandatory fields are not provided (TransitTime ). ";
						  Logger.info(FILE_NAME,"transitTime  "+transitTime );

							effFromDate 	= strToken.nextToken();
						  if(effFromDate!=null && !effFromDate.trim().equals("")){
								effFromDate = effFromDate.toUpperCase();
								Logger.info(FILE_NAME,"4444444444444444   "+dtCheck(effFromDate));
								Logger.info(FILE_NAME,"fomater.getCurrentDateString(DD/MM/YY);"+fomater.getCurrentDateString("DD/MM/YY"));
								if(effFromDate.length() > 10)
									remarks = remarks + "Max Length Must Be  8 (EffFromDate ). ";
								
						  }else
								remarks = remarks + "Mandatory fields are not provided (EffFromDate ). ";
						  Logger.info(FILE_NAME,"effFromDate  "+effFromDate );

						  validUpto 	= strToken.nextToken();
						  if(validUpto!=null && !validUpto.trim().equals("")){
								validUpto = validUpto.toUpperCase();	
								Logger.info(FILE_NAME,"4444444444444444  "+dtCheck(validUpto));
								if(validUpto.length() > 10)
									remarks = remarks + "Max Length Must Be  8 (ValidUpto ). ";
						  }else
								remarks = remarks + "Mandatory fields are not provided (ValidUpto ). ";
						  Logger.info(FILE_NAME,"validUpto  "+validUpto );
                          chargeRateList    =  new ArrayList();
						  for(int k=0;k<totalColumnsCount-8;k++)
					      {
								  chargeRate 	= strToken.nextToken();
								  if(chargeRate!=null && !chargeRate.trim().equals("") && !chargeRate.trim().equalsIgnoreCase("NA")){
										chargeRate = chargeRate.toUpperCase();	
										if(chargeRate.length() > 15)
											remarks = remarks + "Max Length Must Be  15 (Charge Rate ). ";
									if("Both".equalsIgnoreCase(rateType)){
										chargeRateList.add(chargeRate.substring(0,chargeRate.length()-1));
										if(k==0)
											     slabFlatList.add("");
										else if("F".equalsIgnoreCase(chargeRate.substring(chargeRate.length()-1,chargeRate.length())))
										     slabFlatList.add("Flat");
										else
											if("S".equalsIgnoreCase(chargeRate.substring(chargeRate.length()-1,chargeRate.length())))
											   slabFlatList.add("Slab");										
										else
											remarks = remarks + "The PostFix Should be Either F or S. ";
									}
									else{
										flag   =  checkNumber(chargeRate.toCharArray());
										if(!flag)
											remarks = remarks + "Charge Rate is not a Number. ";										
										
										chargeRateList.add(chargeRate);
									}
								  }else if(k<3)
								   {
									  if(k==0)
									       remarks = remarks + "The Charge Rate Should Not be Empty For MIN";
									    else if(k==1 && "FLAT".equalsIgnoreCase(rateType))
										    remarks = remarks + "The Charge Rate Should Not be Empty For FLAT";
										  else if(k==1 && "SLAB".equalsIgnoreCase(rateType))
											  remarks = remarks + "The Charge Rate Should Not be Empty for the slab rate"+wtBreakList.get(1);
										  else if(k==2 && "SLAB".equalsIgnoreCase(rateType))
											  remarks = remarks + "The Charge Rate Should Not be Empty for the slab rate"+wtBreakList.get(2);								  
									}else
										chargeRate  =  "";

								  Logger.info(FILE_NAME,"chargeRate  "+chargeRate );
								  Logger.info(FILE_NAME,"remarks  "+remarks );
						  }						  
				  }
				  count++;
			  } 
			  if(count>1){
			    flatRatesDOB  =  new FlatRatesDOB();
                flatRatesDOB.setOrigin(origin);
				flatRatesDOB.setDestination(destination);
				flatRatesDOB.setServiceLevel(serviceLevel);
				flatRatesDOB.setFrequency(frequency);
				flatRatesDOB.setTransittime(transitTime);				
				flatRatesDOB.setslabFlatList(slabFlatList);
				//flatRatesDOB.setChargeRate(Double.parseDouble(chargeRate));
				flatRatesDOB.setWtBreakSlab(weightBreakSlab);
				flatRatesDOB.setLaneNo(lineNo);
				flatRatesDOB.setLowerBound(0);
				flatRatesDOB.setUpperBound(0);
				flatRatesDOB.setNotes("");
				flatRatesDOB.setCarrierId(carrierId);				
				buyRateDOB.setCarrierId(carrierId);		
				flatRatesDOB.setChargeRateIndicator("");
				flatRatesDOB.setOverPivot("");						
				flatRatesDOB.setChargeRateList(chargeRateList);	

                Logger.info(FILE_NAME,"45555555555555555555555  "+loginbean.getUserPreferences().getDateFormat());
				
				SimpleDateFormat formatter = new SimpleDateFormat(loginbean.getUserPreferences().getDateFormat().toLowerCase());
				ParsePosition position = new ParsePosition(0);
				Calendar cal1 = Calendar.getInstance();
				Logger.info(FILE_NAME,"45555555555555555555555  "+formatter.parse(effFromDate, position));//formatter.parse(effFromDate, position)
				cal1.setTime(new Date(effFromDate));
				Logger.info(FILE_NAME,"1111111111111122  "+cal1.getTimeInMillis());

				formatter = new SimpleDateFormat(loginbean.getUserPreferences().getDateFormat().toLowerCase());
				Calendar cal2 = Calendar.getInstance();
				position = new ParsePosition(0);
				cal2.setTime(new Date(validUpto));
				Logger.info(FILE_NAME,"1111111111111122  "+cal2.getTimeInMillis());
				Logger.info(FILE_NAME,"11111111111111  "+(cal2.getTimeInMillis()>cal1.getTimeInMillis()));

				if(cal2.getTimeInMillis()<cal1.getTimeInMillis())
                    remarks += "Valid Upto date should be greater than or equal to Effetive From Date \t"; 

				cal2 = Calendar.getInstance();
				position = new ParsePosition(0);
				cal2.setTime(new Date(fomater.getCurrentDateString(loginbean.getUserPreferences().getDateFormat())));
				
				if(cal2.getTimeInMillis()<cal1.getTimeInMillis())
                    remarks += "Effetive From Date should be greater than or equal to Today Date \t"; 
				
				flatRatesDOB.setRemarks(remarks);
				flatRatesDOB.setEffDate(fomater.getTimestamp(loginbean.getUserPreferences().getDateFormat(),effFromDate));
			    flatRatesDOB.setValidUpto(fomater.getTimestamp(loginbean.getUserPreferences().getDateFormat(),validUpto));
				//buyRateDOB           =   (RateDOB)buyRateDOBHashtable.get(new Integer(rowId));						
				flatRatesList        =   (ArrayList)buyRateDOB.getRateDtls();				
				flatRatesList.add(flatRatesDOB);				
				buyRateDOB.setRateDtls(flatRatesList);	
			  }
                data=br.readLine();				
				if (data==null)
					break;				
				//remarks = "";
				i++;				
				rowId = -1;				
			}
            
                
			flatRatesList   =  buyRateDOB.getRateDtls();
			ArrayList tempFlatRateList = new ArrayList();
			for(i=0;i<flatRatesList .size();i++)
			{   
				flatRatesDOB  = (FlatRatesDOB)flatRatesList .get(i);				
				Logger.info(FILE_NAME,"flatRatesDOB.getRemarks()  "+flatRatesDOB.getRemarks() );
				if(flatRatesDOB.getRemarks().equals("")){			
					tempFlatRateList .add(flatRatesDOB);
				}else{			
					failureList.add(flatRatesDOB);
				}				
			}
			if(failureList.size()<=0 && "".equals(buyRateDOB.getRemarks()))
				successList.add(buyRateDOB);
            Logger.info(FILE_NAME,"successList  "+successList.size() );
			HashMap finalMap = new HashMap(2,2);

		if(successList.size() > 0 && (buyRateDOB.getRemarks().equals("")) && failureList.size() <=0){
			
			duplicateList  =  checkDuplicateRecords(buyRateDOB.getRateDtls());
				if(duplicateList.size()<=0){
					InitialContext initial = new InitialContext();
					BuyRatesSessionHome 	home	=	(BuyRatesSessionHome)initial.lookup("BuyRatesSessionBean");			
					BuyRatesSession 		remote	=	(BuyRatesSession)home.create();	
					 flag = remote.validateCurrency(buyRateDOB.getCurrency());
					if(flag)
			          finalMap = remote.upLoadBuyRates(successList,false,loginbean);
					else {
						keyValueList       = new ArrayList(3);
						message = new ErrorMessage("Currency Id is Not Valid,Please enter Valid Currency ","BuyRatesUpLoad.jsp");
						keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
						keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
						keyValueList.add(new KeyValue("Type","BuyRates"));
						message.setKeyValueList(keyValueList);				 
						request.setAttribute("ErrorMessage",message); 
%>
						<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
					}
				}
			Logger.info(FILE_NAME,"finalMap    "+finalMap);
		}else if(successList.size() == 0 && failureList.size() == 0 && "".equals(buyRateDOB.getRemarks())){
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage("No Data Found In The Uploaded File","BuyRatesUpLoad.jsp");
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","BuyRates"));
			message.setKeyValueList(keyValueList);				 
			request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		}
		if(successList.size() > 0 && duplicateList.size()<=0){
			Logger.info(FILE_NAME,"process  "+process );
			/*if(process.equalsIgnoreCase("ADD")){
				exList = (ArrayList)finalMap.get("EXISTS");
				newList = (ArrayList)finalMap.get("NONEXISTS");
			}else*/{
				exList = (ArrayList)finalMap.get("NONEXISTS");
				newList = (ArrayList)finalMap.get("EXISTS");
			}
		}
		Logger.info(FILE_NAME,"duplicateList.size()  "+duplicateList.size() );
		//Logger.info(FILE_NAME,"exList  "+exList.size() );
		//Logger.info(FILE_NAME,"newList  "+newList.size() );
		Logger.info(FILE_NAME,"errorMode  "+errorMode );
		String successErrMsg ="";
		String failureErrMsg = "";
		if(errorMode.equalsIgnoreCase("N")){
			if(failureList.size() <=0 && "".equals(buyRateDOB.getRemarks())){
			if(newList.size() > 0)
				successErrMsg = " The Details Are Uploaded Successfully : \n";			
			
			if(exList.size() > 0 && process.equalsIgnoreCase("ADD"))
				failureErrMsg = " The Following Details  Couldn't Be Uploaded,  : \n";
			for(int k = 0;k<exList.size();k++){
					flatRatesDOB = (FlatRatesDOB)exList.get(k);									    
					failureErrMsg = failureErrMsg + flatRatesDOB.getOrigin()+"\t";      
					failureErrMsg = failureErrMsg + flatRatesDOB.getDestination()+"\t"; 
					failureErrMsg = failureErrMsg + flatRatesDOB.getCarrierId()+"\t";   
					failureErrMsg = failureErrMsg + flatRatesDOB.getServiceLevel()+"\t";
					fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t";
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t";    
				    failureErrMsg = failureErrMsg +  " " + flatRatesDOB.getRemarks() +".\n";
			}
			}
			if(failureList.size() >0 || !buyRateDOB.getRemarks().equals(""))
				failureErrMsg = " The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : \n";
			   {
				if(!buyRateDOB.getRemarks().equals("")){
					failureErrMsg = failureErrMsg + buyRateDOB.getShipmentMode()+"\t";
					failureErrMsg = failureErrMsg + buyRateDOB.getCurrency()+"\t";  
					failureErrMsg = failureErrMsg + buyRateDOB.getWeightBreak()+"\t";
					failureErrMsg = failureErrMsg + buyRateDOB.getWeightClass()+"\t";
					failureErrMsg = failureErrMsg + buyRateDOB.getRateType()+"\t";  
					failureErrMsg = failureErrMsg + buyRateDOB.getUom()+"\t";       
					failureErrMsg = failureErrMsg + buyRateDOB.getConsoleType();    
					failureErrMsg = failureErrMsg + buyRateDOB.getRemarks() +".\n";				
				}
				for(int l = 0;l<failureList.size();l++){
					flatRatesDOB = (FlatRatesDOB)failureList.get(l);									    
					failureErrMsg = failureErrMsg + flatRatesDOB.getOrigin()+"\t";      
					failureErrMsg = failureErrMsg + flatRatesDOB.getDestination()+"\t"; 
					failureErrMsg = failureErrMsg + flatRatesDOB.getCarrierId()+"\t";   
					failureErrMsg = failureErrMsg + flatRatesDOB.getServiceLevel()+"\t";
					fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t";
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t";    
				    failureErrMsg = failureErrMsg +  " " + flatRatesDOB.getRemarks() +".\n";
				}
			}
            if( duplicateList.size()>0)
				failureErrMsg  = "The following Details are Duplicate rows in Excel File : \n";
			for(int l = 0;l<duplicateList.size();l++){
					flatRatesDOB = (FlatRatesDOB)duplicateList.get(l);				    
					failureErrMsg = failureErrMsg + flatRatesDOB.getOrigin()+"\t";      
					failureErrMsg = failureErrMsg + flatRatesDOB.getDestination()+"\t"; 
					failureErrMsg = failureErrMsg + flatRatesDOB.getCarrierId()+"\t";   
					failureErrMsg = failureErrMsg + flatRatesDOB.getServiceLevel()+"\t";
					fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t";
					failureErrMsg = failureErrMsg + fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t";    
					failureErrMsg = failureErrMsg + "At row No "+(flatRatesDOB.getSlNo()+1)+".\n";
			}
				 
			keyValueList       = new ArrayList(3);
			message = new ErrorMessage(successErrMsg + failureErrMsg ,"BuyRatesUpLoad.jsp");
			keyValueList.add(new KeyValue("ErrorCode","MSG")); 	
			keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 	
			keyValueList.add(new KeyValue("Type","BuyRate"));
			message.setKeyValueList(keyValueList);
				 
			request.setAttribute("ErrorMessage",message); 
%>
				<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
		
      }else{
		    out.clearBuffer();
			response.setContentType("application/vnd.ms-excel");	
			String contentDisposition = " :attachment;";	
			response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
	        if(failureList.size() <=0  && "".equals(buyRateDOB.getRemarks())){
			//out.clearBuffer();
			//response.setContentType("application/vnd.ms-excel");	
			//String contentDisposition = " :attachment;";	
			//response.setHeader("Content-Disposition","attachment;filename=Exceptions.xls");
			Logger.info(FILE_NAME,"newList.size() "+newList.size());
			if(newList.size() > 0){
				successErrMsg = " The Following Id(s) Are Uploaded Successfully : ";
				out.println(successErrMsg);
				out.println();				   
				   out.print("SHIPMENT MODE:\t" );
				   out.print("CURRENCY:\t" );
				   out.print("WEIGHT BREAK:\t" );
				   out.print("WEIGHT CLASS:\t" );
				   out.print("RATE TYPE:\t" );
				   out.print("UOM:\t" );  
				   out.println("CONSOLETYPE:\t" );
						   
			   out.print(buyRateDOB.getShipmentMode()+"\t");
			   out.print(buyRateDOB.getCurrency()+"\t"); 
			   out.print(buyRateDOB.getWeightBreak()+"\t"); 
			   out.print(buyRateDOB.getWeightClass()+"\t"); 
			   out.print(buyRateDOB.getRateType()+"\t");
			   out.print(buyRateDOB.getUom()+"\t");
			   out.print(buyRateDOB.getConsoleType());
			}
			if(newList.size() > 0){
				out.println();
				   out.print("ORIGIN:\t" );
				   out.print("DESTINATION:\t" );
				   out.print("CARRIER ID:\t" );
				   out.print("SERVICELEVEL:\t" );
				   out.print("FREQUENCY:\t" );
				   out.print("TRANSIT TIME:\t" ); 
				   out.print("EFFECTIVE FROM:\t" );  
				   out.print("VALID UPTO:\t" );
				   for( i=0;i<buyRateDOB.getWtBreakList().size();i++)
					   out.print(buyRateDOB.getWtBreakList().get(i)+"\t");
				   out.println();
			Logger.info(FILE_NAME,"newList.size() "+newList.size());
			       for(int j=0;j<newList.size();j++){
					flatRatesDOB = (FlatRatesDOB)newList.get(j);					   
						   out.print(flatRatesDOB.getOrigin()+"\t");
						   out.print(flatRatesDOB.getDestination()+"\t");
			               out.print(flatRatesDOB.getCarrierId()+"\t");
						   out.print(flatRatesDOB.getServiceLevel()+"\t");
						   out.print(flatRatesDOB.getFrequency()+"\t");
						   out.print(flatRatesDOB.getTransittime()+"\t");
						   fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
						   out.print(fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t");
						   out.print(fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t");
						   Logger.info(FILE_NAME,"flatRatesDOB.getSlabFlatList().size() "+flatRatesDOB.getSlabFlatList().size());
						   for( i=0;i<flatRatesDOB.getSlabFlatList().size();i++)
						        out.print(flatRatesDOB.getSlabFlatList().get(i)+"\t");
						   out.println();						
				   }
					
			}
			Logger.info(FILE_NAME,"exList.size() "+exList.size());
			if(exList.size() > 0 ){
				   out.println(" The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ");				   
				   out.print("SHIPMENT MODE:\t" );
				   out.print("CURRENCY:\t" );
				   out.print("WEIGHT BREAK:\t" );
				   out.print("WEIGHT CLASS:\t" );
				   out.print("RATE TYPE:\t" );
				   out.print("UOM:\t" );  
				   out.print("CONSOLETYPE:\t" );
				   out.println("REMARKS:\t" );
					   out.print(buyRateDOB.getShipmentMode()+"\t");
					   out.print(buyRateDOB.getCurrency()+"\t"); 
					   out.print(buyRateDOB.getWeightBreak()+"\t"); 
					   out.print(buyRateDOB.getWeightClass()+"\t"); 
					   out.print(buyRateDOB.getRateType()+"\t");
					   out.print(buyRateDOB.getUom()+"\t");
					   out.print(buyRateDOB.getConsoleType());
					   out.println(buyRateDOB.getRemarks());
				  
			}
			
			
			if(exList.size() > 0){				
				   out.println();
				   out.print("ORIGIN:\t" );
				   out.print("DESTINATION:\t" );
				   out.print("CARRIER ID:\t" );
				   out.print("SERVICELEVEL:\t" );
				   out.print("FREQUENCY:\t" );
				   out.print("TRANSIT TIME:\t" ); 
				   out.print("EFFECTIVE FROM:\t" );  
				   out.print("VALID UPTO:\t" );
				   out.println("REMARKS:\t" );
				   for(int j = 0;j<exList.size();j++){
						flatRatesDOB = (FlatRatesDOB)exList.get(j);					
							   out.print(flatRatesDOB.getOrigin()+"\t");
							   out.print(flatRatesDOB.getDestination()+"\t");
							   out.print(flatRatesDOB.getCarrierId()+"\t");
							   out.print(flatRatesDOB.getServiceLevel()+"\t"); 
							   out.print(flatRatesDOB.getFrequency()+"\t");
							   out.print(flatRatesDOB.getTransittime()+"\t");
							   fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
							   out.print(fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t");
							   out.print(fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t");
							   out.println(flatRatesDOB.getRemarks());
				  }
			}
			}
			if(failureList.size() >0 || !buyRateDOB.getRemarks().equals("")){
				
				  out.print(" The Following Id(s) Couldn't Be Uploaded, Due To Below Reasons : ");
				  out.println();
				   
					   if(!buyRateDOB.getRemarks().equals("")){
						   out.println();
						   out.print("SHIPMENT MODE:\t" );
						   out.print("CURRENCY:\t" );
						   out.print("WEIGHT BREAK:\t" );
						   out.print("WEIGHT CLASS:\t" );
						   out.print("RATE TYPE:\t" );
						   out.print("UOM:\t" );  
						   out.print("CONSOLETYPE:\t" );
						   out.println("REMARKS:\t" );						   														   
							   out.print(buyRateDOB.getShipmentMode()+"\t");
							   out.print(buyRateDOB.getCurrency()+"\t"); 
							   out.print(buyRateDOB.getWeightBreak()+"\t"); 
							   out.print(buyRateDOB.getWeightClass()+"\t"); 
							   out.print(buyRateDOB.getRateType()+"\t");
							   out.print(buyRateDOB.getUom()+"\t");
							   out.print(buyRateDOB.getConsoleType()+"\t");
							   out.println(buyRateDOB.getRemarks());
						  
					}
			}
			Logger.info(FILE_NAME,"failureList.size() "+failureList.size());
			if(failureList.size() > 0){										
				   out.print("ORIGIN:\t" );
				   out.print("DESTINATION:\t" );
				   out.print("CARRIER ID:\t" );
				   out.print("SERVICELEVEL:\t" );
				   out.print("FREQUENCY:\t" );
				   out.print("TRANSIT TIME:\t" ); 
				   out.print("EFFECTIVE FROM:\t" );  
				   out.print("VALID UPTO:\t" );
				   for( i=0;i<buyRateDOB.getWtBreakList().size();i++)
					   out.print(buyRateDOB.getWtBreakList().get(i)+"\t");
				   out.println("REMARKS:\t" );
			}
			for(int j = 0;j<failureList.size();j++){
					flatRatesDOB = (FlatRatesDOB)failureList.get(j);
						   out.print(flatRatesDOB.getOrigin()+"\t");
						   out.print(flatRatesDOB.getDestination()+"\t");
				           out.print(flatRatesDOB.getCarrierId()+"\t");
						   out.print(flatRatesDOB.getServiceLevel()+"\t");
						   out.print(flatRatesDOB.getFrequency()+"\t");
						   out.print(flatRatesDOB.getTransittime()+"\t");
						   fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
						   out.print(fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t");
						   out.print(fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t");
						   for( i=0;i<flatRatesDOB.getSlabFlatList().size();i++)
						       out.print(flatRatesDOB.getSlabFlatList().get(i)+"\t");
						   out.println(flatRatesDOB.getRemarks());
				
			}
			
			if( duplicateList.size()>0)
				failureErrMsg  = "The following Details are Duplicate rows in Excel File : \n";
			if(duplicateList.size() > 0){						
				   out.println("The following Details are Duplicate Rows");
				   out.println();
				   out.print("ORIGIN:\t" );
				   out.print("DESTINATION:\t" );
				   out.print("CARRIER ID:\t" );
				   out.print("SERVICELEVEL:\t" );
				   out.print("EFFECTIVE FROM:\t" );  
				   out.print("VALID UPTO:\t" );
				   out.println("ROW NO:\t" );
			}
			for(int j = 0;j<duplicateList.size();j++){
					flatRatesDOB = (FlatRatesDOB)duplicateList.get(j);
						   out.print(flatRatesDOB.getOrigin()+"\t");
						   out.print(flatRatesDOB.getDestination()+"\t");
				           out.print(flatRatesDOB.getCarrierId()+"\t");
						   out.print(flatRatesDOB.getServiceLevel()+"\t");
						   fomater.setPattern(loginbean.getUserPreferences().getDateFormat());
						   out.print(fomater.getDisplayString(flatRatesDOB.getEffDate())+"\t");
						   out.print(fomater.getDisplayString(flatRatesDOB.getValidUpto())+"\t");
						   out.println((flatRatesDOB.getSlNo()+1));
			}
		
	  }
	}catch(Exception exp){
		exp.printStackTrace();
		Logger.error(FILE_NAME,"Error in BuyRatesUpLoadProcess.jsp file ",exp.toString());
			 
		keyValueList       = new ArrayList(3);
		message = new ErrorMessage("Process could not be completed.Please try again","ETCUploadIndex.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		keyValueList.add(new KeyValue("Type","Country"));
		message.setKeyValueList(keyValueList);
             
		request.setAttribute("ErrorMessage",message); 
%>
			<jsp:forward page="../QMSESupplyErrorPage.jsp"/>
<%
	}
%>
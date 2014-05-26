/*
 * @(#)MasterDocumentHeader.java         07/09/2001
 *
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd, 
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.    
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Four-Soft. 
*/

package com.foursoft.etrans.common.routeplan.java;

import com.foursoft.esupply.accesscontrol.java.VersionedModel;
// @@ Suneetha Added on 20050711 (RATE_CLASS)
//import com.foursoft.etrans.setup.carriercontract.java.ETCarrierContractDtlDOB;
// @@ 20050711 (RATE_CLASS)
import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Locale;


   /**
	 * @file : MasterDocumentHeader.java
	 * @author : 
	 * @date : 2003-08-07
	 * @version : 1.6 
	 */

public class MasterDocumentHeader extends VersionedModel 
{        
	   /*
		* @param :
		* @return : 
		* Contructor 
		*
		*/
		public MasterDocumentHeader()
		{
			this.masterDocId		=  null;
			this.masterDocDate		=  null;
			this.terminalId			=  null;
			this.originGatewayId	=  null;
			this.destinationGatewayId=  null;
			this.caContractId		=  null;
			this.carrierCode		=  null;
			this.serviceLevelId		=  null;
			this.shipmentMode		=  null;
			this.chargeableWeight	=  0;
			this.UOW				=  null;
			this.unitRate			=  0;
			this.pieces				=  0;
			this.currencyId			=  null;
			this.closingDate		=  null;
			this.closedBy			=  null;
			this.discount			=  0;
			this.carrierId			=  null;
			this.indicator			=  null; 		
			this.conversionFactor	=  0;
			this.totalAmount		=  0;
			this.otherCharges		=  0;
			this.handlingInfo		=  null;
			this.status				=  null;
			this.actualTime			=  null;
			this.recepietStatus		=  null;			
			this.houseDocId			=  null;
			this.houseSelected		=  null;
			this.paidStatus			=  null;
			this.rateClass   		=  null;	 
		    this.carriagevalue		=  null;	
		    this.customsvalue		=  null;	 
		    this.insurancevalue		=  null;	 
		    this.commodityId   		=  null;	 
		    this.notifyparty		=  null;	 
		    this.description		=  null;	 
		    this.otherremarks		=  null;	 
		    this.coloaderId         =  null;
		    this.mawbOwner          =  null;
			this.registered         =  null; 
			this.blockedSpace		=  0;
			this.registered         =  null; 
			this.consignmentType    =  null;
			this.transhipment       = false;
			this.openingKm			= 0;
			this.closingKm			= 0;
			this.vendorId			= null;
			this.driverName			= null;
			this.etaDt				= null;
			this.etdDt				= null;
			manifestType			= null;
			this.isTruckExists      = false;
			this.prqIds			    = null;
			this.prqSelected		= null;
			// added by Nageswara Rao.D for Descriptive Lov at route plan			
			this.documentId			=  null;
			this.shipperId			=  null;
			this.consigneeId		=  null;
			this.originTerminal		=  null;
			this.destTerminal		=  null;
			this.primaryMode        =  0;
			this.subAgentId			=  null	;
			this.masterCost			=  0.0;
			this.amtTransAccs		=  0.0;	
			this.rateIndicator      =  null;

			// @@  Added By G.Srinivas  for DHL Issue FSET-2543 on 20050615 -->
			this.reasonCode			=  null;
		
		}
		
// @@  Added By G.Srinivas  for DHL Issue FSET-2543 on 20050615 -->
/**
	 * sets reasonCode
	 *
	 * @param reasonCode
	 */
	public void setReasonCode(String[] reasonCode)
	{
		this.reasonCode=reasonCode;
	}

	/**
	* Returns reasonCode
	*
	* @return the String contains reasonCode
	*/
	public String[] getReasonCode()
	{
		return reasonCode;
	}
// @@  Added By G.Srinivas  for DHL Issue FSET-2543 on 20050615 -->
		         
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 	 public void setRegistered( String registered )
     {
          this.registered     = registered;
     }	
	 
	          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
	 public String getRegistered( )
     {
          return registered ;
     }	
 	 			         
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */	
 		public  void setMasterDocId( String masterDocId )
		{
			this.masterDocId = masterDocId;
		}
			          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getMasterDocId()
		{
			return masterDocId;
		}
		
					          
	/*
     * 
     * @param : Timestamp
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setMasterDocDateTimestamp( java.sql.Timestamp masterDocDate )
		{
			this.masterDocDate = masterDocDate;
		}
// commented by Nageswara Rao. 28-11-02
 	/*	public  void setMasterDocDate( String masterDocDate )
		{
			try
			{
				DateFormat mmdd = DateFormat.getDateTimeInstance( DateFormat.SHORT,DateFormat.SHORT, Locale.UK );
				java.util.Date dMasterDocDate = mmdd.parse( masterDocDate );
				this.masterDocDate = new java.sql.Timestamp( dMasterDocDate.getTime() );
			}
			catch( ParseException e )
			{
				this.masterDocDate = null;
			}
		}*/
// added by Nageswarar Rao.D 28-11-02
       
	   					          
	/*
     * 
     * @param : Timestamp
	 * @return : 
	 * @author :
	 *
	 */
		public  void setMasterDocDate( Timestamp masterDocDate )
		{
				this.masterDocDate = masterDocDate;
		}

							          
	/*
     * 
     * @param : 
	 * @return : Timestamp
	 * @author :
	 *
	 */
 		public  java.sql.Timestamp getMasterDocDate()
		{
			return masterDocDate;
		}
		
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setTerminalId( String terminalId )
		{
			this.terminalId = terminalId;
		}

									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getTerminalId()
		{
			return terminalId;
		}
        

									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setOriginGatewayId( String originGatewayId )
		{
			this.originGatewayId = originGatewayId;
		}

									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getOriginGatewayId()
		{
			return originGatewayId;
		}
        

									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setDestinationGatewayId( String destinationGatewayId )
		{
			this.destinationGatewayId = destinationGatewayId;
		}
 	     
		 							          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getDestinationGatewayId()
		{
			return destinationGatewayId;
		}
		

									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setCaContractId( String caContractId )
		{
			this.caContractId = caContractId;
		}

         
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getCaContractId()
		{
			return caContractId;
		}
        
		
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setCarrierId( String carrierId )
		{
			this.carrierId = carrierId;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getCarrierId()
		{
			return carrierId;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setCarrierCode( String carrierCode )
		{
			this.carrierCode = carrierCode;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getCarrierCode()
		{
			return carrierCode.trim();
		}
         
		 
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setServiceLevelId( String serviceLevelId )
		{
			this.serviceLevelId = serviceLevelId;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getServiceLevelId()
		{
			return serviceLevelId;
		}
        
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setShipmentMode( String shipmentMode )
		{
			this.shipmentMode = shipmentMode;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getShipmentMode()
		{
			return shipmentMode;
		}
         
									          
	/*
     * 
     * @param : double
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setChargeableWeight( double chargeableWeight )
		{
			this.chargeableWeight = chargeableWeight;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getChargeableWeight()
		{
			return chargeableWeight;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setUOW( String UOW)
		{
			this.UOW = UOW;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getUOW()
		{
			return UOW;
		}
            
									          
	/*
     * 
     * @param : int
	 * @return : 
	 * @author :
	 *
	 */  
 		public  void setPieces( int pieces )
		{
			this.pieces = pieces;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : int
	 * @author :
	 *
	 */
 		public  int getPieces()
		{
			return pieces;
		}
        
									          
	/*
     * 
     * @param : double
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setUnitRate( double unitRate )
		{
			this.unitRate = unitRate;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getUnitRate()
		{
			return unitRate;
		}
		
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setCurrencyId( String currencyId )
		{
			this.currencyId = currencyId;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getCurrencyId()
		{
			return currencyId;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setClosingDate( String closingDate )
		{
			try
			{
				DateFormat mmdd = DateFormat.getDateInstance( DateFormat.SHORT, Locale.UK );
				java.util.Date dClosingDate = mmdd.parse( closingDate );
			    this.closingDate = new java.sql.Timestamp( dClosingDate.getTime() );
		    }
		    catch( Exception e )
	    	{
			    this.closingDate = null;
    		}
		}
										          
	/**
     * 
     * @param : Timestamp
	 * @return : 
	 * @author :
	 *
	 */
 
 		public  java.sql.Timestamp getClosingDate()
		{
			return closingDate;
		}
        
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setClosedBy( String closedBy )
		{
			this.closedBy = closedBy;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getClosedBy()
		{
			return closedBy;
		}
        
									          
	/*
     * 
     * @param : double
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setDiscount( double discount )
		{
			this.discount = discount;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getDiscount()
		{
			return discount;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */ 
 		public  void setIndicator( String indicator )
		{
			this.indicator = indicator;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getIndicator()
		{
			return indicator;
		}
		
         
									          
	/*
     * 
     * @param : double
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setConversionFactor( double conversionFactor )
		{
			this.conversionFactor = conversionFactor;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double 
	 * @author :
	 *
	 */
 		public  double getConversionFactor()
		{
			return conversionFactor;
		}
        
									          
	/*
     * 
     * @param : 
	 * @return : double 
	 * @author :
	 *
	 */
 		public  void setTotalAmount( double totalAmount )
		{
			this.totalAmount = totalAmount;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getTotalAmount()
		{
			return totalAmount;
		}
        
									          
	/*
     * 
     * @param : double
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setOtherCharges( double otherCharges )
		{
			this.otherCharges = otherCharges;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getOtherCharges()
		{
			return otherCharges;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setHandlingInfo( String handlingInfo )
		{
			this.handlingInfo = handlingInfo;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getHandlingInfo()
		{
			return handlingInfo;
		}
         
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setStatus( String status )
		{
			this.status = status;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String 
	 * @author :
	 *
	 */
 		public  String getStatus()
		{
			return status;
		}
		
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */		
 		public  void setActualTime( String actualTime )
		{
			try
			{
				DateFormat mmdd = DateFormat.getDateInstance( DateFormat.SHORT, Locale.UK );
				java.util.Date dActualTime = mmdd.parse( actualTime );
				this.actualTime = new java.sql.Timestamp( dActualTime.getTime() );
			}
			catch( Exception e )
			{
				this.actualTime = null;
			}
		}
		
  						          
	/**
     * 
     * @param : 
	 * @return : Timestamp 
	 * @author :
	 *
	 */
 		public java.sql.Timestamp getActualTime()
		{
			return actualTime;
		}
        
									          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setRecepietStatus( String recepietStatus )
		{
			this.recepietStatus = recepietStatus;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getRecepietStatus()
		{
			return recepietStatus;
		}
		
		//for house master
		
									          
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setHouseDocId( String[] houseDocId )
		{
			this.houseDocId = houseDocId;
		}
		
									          
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setPRQIds( String[] prqIds )
		{
			this.prqIds = prqIds;
		}
		
									          
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public  String[] getHouseDocId()
		{
			return houseDocId;
		}

											          
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
		public String[] getPRQIds()
		{
			return prqIds;
		}	
											          
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setHouseSelected( String[] houseSelected)
		{
			this.houseSelected = houseSelected;
		}
											          
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setPRQSelected( String[] prqSelected)
		{
			this.prqSelected = prqSelected;
		}	
													          
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public  String[] getHouseSelected()
		{
			return houseSelected;
		}
													          
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public  String[] getPRQSelected()
		{
			return prqSelected;
		}
													          
	/*
     * 
     * @param : String
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setPaidStatus( String paidStatus )
		{
			this.paidStatus = paidStatus;
		}
													          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getPaidStatus()
		{
			return paidStatus;
		}
		
//latest
											          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
    public  void setRateClass ( String rateClass )
	{
		this.rateClass  = rateClass ;
	}
												          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getRateClass ()
 	{
	   return rateClass ;
 	}	
	 											          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
   public  void setCarriageValue ( String carriagevalue )
	{
		this.carriagevalue  = carriagevalue ;
	}
												          
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getCarriageValue ()
 	{
	   return carriagevalue ;
 	}	
	 											          
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	public  void setCustomsValue ( String customsvalue )
	{
		this.customsvalue  = customsvalue ;
	}
		 	 											          
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getCustomsValue ()
 	{
	   return customsvalue ;
 	}	

	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public  void setInsuranceValue ( String insurancevalue )
	{
		this.insurancevalue  = insurancevalue ;
	}

	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getInsuranceValue ()
 	{
	   return insurancevalue ;
 	}
	 
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
	 public  void setCommodityId ( String commodityId )
	{
		this.commodityId  = commodityId ;
	}
		 
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getCommodityId ()
 	{
	   return commodityId ;
 	}
	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public  void setNotifyParty ( String notifyparty )
	{
		this.notifyparty  = notifyparty ;
	}

		 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getNotifyParty ()
 	{
	   return notifyparty ;
 	}	
	 
	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public  void setDescription ( String description )
	{
		this.description  = description ;
	}
			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getDescription ()
 	{
	   return description ;
 	}	
	 
	 	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public  void setOtherRemarks ( String otherremarks )
	{
		this.otherremarks  = otherremarks ;
	}
				 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */
 	public  String getOtherRemarks ()
 	{
	   return otherremarks ;
 	}		 		 									
		
	  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	 public String getCoLoaderId()
     {
        return coloaderId;
     }	
	 
	 	 	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public void setCoLoaderId(String coloaderId)
     {
        this.coloaderId = coloaderId;
     }		
	 
			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	 public String getMawbOwner()
     {
        return mawbOwner;
     }		
	 
	 	 	 	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public void setMawbOwner(String mawbOwner)
     {
        this.mawbOwner = mawbOwner;
     }
	 	 	 	 	 		 
	/*
     * 
     * @param : double 
	 * @return : 
	 * @author :
	 *
	 */
	 public void setBlockedSpace(double blockedSpace)
	 {
		this.blockedSpace = blockedSpace;
	 }

	 	 	 	 	 		 
	/*
     * 
     * @param :  
	 * @return : double 
	 * @author :
	 *
	 */
	 public double getBlockedSpace()
	 {
		return blockedSpace;
	 }
      
	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	 	 public String getNonSystem()
     {
        return nonSystem;
     }		
	 
      	 	 	 	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	 public void setNonSystem(String nonSystem)
     {
        this.nonSystem = nonSystem;
     }
	 
	  	 	 	 	 	 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */
	  public void setConsignmentType( String consignmentType )
     {
          this.consignmentType     = consignmentType;
     }	
	 
	 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	 public String getConsignmentType( )
     {
          return consignmentType ;
     }	
     
	 	  			  			 		 
	/*
     * 
     * @param : boolean 
	 * @return : 
	 * @author :
	 *
	 */	
	public void setTranshipment(boolean transhipment)
	{
		this.transhipment = transhipment;
	}

		 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : boolean
	 * @author :
	 *
	 */	
	public boolean isTranshipment()
	{
		return transhipment;
	}

		 	  			  			 		 
	/*
     * 
     * @param : boolean 
	 * @return : 
	 * @author :
	 *
	 */	
	public void setIsTruckExists(boolean isTruckExists)
	{
		this.isTruckExists = isTruckExists;
	}

			 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : boolean
	 * @author :
	 *
	 */	
	public boolean IsTruckExists()
	{
		return isTruckExists;
	}
	
		 	  			  			 		 
	/*
     * 
     * @param : String 
	 * @return : 
	 * @author :
	 *
	 */	
	public  void setManifestType( String manifestType )
	{
		this.manifestType = manifestType;
	}

		 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	public  String getManifestType()
	{
		return manifestType;
	}

		 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : long
	 * @author :
	 *
	 */	
			public long getClosingKm()
	{
		return closingKm;
	}
	
		 	  			  			 		 
	/*
     * 
     * @param : long 
	 * @return : 
	 * @author :
	 *
	 */	
	public void setClosingKm(long closingKm)
	{
		this.closingKm = closingKm;
	}
	
		 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : long
	 * @author :
	 *
	 */	
	public long getOpeningKm()
	{
		return openingKm;
	}

			 	  			  			 		 
	/*
     * 
     * @param : long 
	 * @return : 
	 * @author :
	 *
	 */	
	public void setOpeningKm(long openingKm)
	{
		this.openingKm = openingKm;
	}
	
			 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	public String getVendorId()
	{
		return vendorId;
	}

		 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
	public void setVendorId(String vendorId)
	{
		this.vendorId = vendorId;
	}								
	
				 	  			  			 		 
	/*
     * 
     * @param :  
	 * @return : String
	 * @author :
	 *
	 */	
	public String getDriverName()
	{
		return driverName;
	}
	
			 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
	public void setDriverName(String driverName)
	{
		this.driverName = driverName;
	}	
	
			 											          
	/*
     * 
     * @param :   
	 * @return : Date
	 * @author :
	 *
	 */
	public java.util.Date getETA()
	{
		return etaDt;
	}	
	
			 											          
	/*
     * 
     * @param : Date  
	 * @return : 
	 * @author :
	 *
	 */
	public void setETA(java.util.Date etaDt)
	{
		this.etaDt = etaDt;
	}	
			 											          
	/*
     * 
     * @param :   
	 * @return : Date
	 * @author :
	 *
	 */
	public java.util.Date getETD()
	{
		return etdDt;
	}
			 											          
	/*
     * 
     * @param : Date  
	 * @return : 
	 * @author :
	 *
	 */
	public void setETD(java.util.Date etdDt)
	{
		this.etdDt = etdDt;
	}
			 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
	public  void setVendorContractId( String vendorContractId )
	{
		this.vendorContractId = vendorContractId;
	}
			 											          
	/*
     * 
     * @param :   
	 * @return : String 
	 * @author :
	 *
	 */
	public  String getVendorContractId()
	{
		return vendorContractId;
	}
     		 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
	public  void setTruckId( String truckId )
	{
		this.truckId = truckId;
	}
			 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
	public  String getTruckId()
	{
		return truckId;
	}
			 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
   public  void setDocId( String documentId)
		{
			this.documentId = documentId;
		}
				 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getDocId()
		{
			return documentId;
		}	
 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setShipperId( String shipperId	)
		{
			this.shipperId = shipperId;
		}
				 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getShipperId()
		{
			return shipperId;
		}
		 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
		public  void setConsigneeId( String consigneeId	)
		{
			this.consigneeId = consigneeId;
		}

						 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getConsigneeId()
		{
			return consigneeId;
		}
				 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setOriginTerminal( String originTerminal )
		{
			this.originTerminal = originTerminal;
		}
								 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getOriginTerminal()
		{
			return originTerminal;
		}
        				 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setDestTerminal( String destTerminal )
		{
			this.destTerminal = destTerminal;
		}
										 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getDestTerminal()
		{
			return destTerminal;
		}
                				 				 											          
	/*
     * 
     * @param : int  
	 * @return : 
	 * @author :
	 *
	 */ 
 		public  void setPrimaryMode( int primaryMode )
		{
			this.primaryMode = primaryMode;
		}
		        				 				 											          
	/*
     * 
     * @param :   
	 * @return : int
	 * @author :
	 *
	 */
 		public  int getPrimaryMode()
		{
			return primaryMode;
		}
                 				 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
		public  void setSubAgentId( String subAgentId )
		{
			this.subAgentId = subAgentId;
		}
												 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getSubAgentId()
		{
			return subAgentId;
		}
		                 				 				 											          
	/*
     * 
     * @param : String  
	 * @return : 
	 * @author :
	 *
	 */
		public  void setRateIndicator( String rateIndicator )
		{
			this.rateIndicator = rateIndicator;
		}
														 											          
	/*
     * 
     * @param :   
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getRateIndicator()
		{
			return rateIndicator;
		}
														 											          
	/*
     * 
     * @param : double   
	 * @return : 
	 * @author :
	 *
	 */
		public  void setMasterCost( double masterCost )
		{
			this.masterCost = masterCost;
		}
														 											          
	/*
     * 
     * @param :   
	 * @return : double
	 * @author :
	 *
	 */
 		public  double getMasterCost()
		{
			return masterCost;
		}
        														 											          
	/*
     * 
     * @param : double   
	 * @return : 
	 * @author :
	 *
	 */
		public  void setAmtTransAccs( double amtTransAccs )
		{
			this.amtTransAccs = amtTransAccs;
		}
			/*
			 * 
			 * @param :   
			 * @return : double
			 * @author :
			 *
			 */
 		public  double getAmtTransAccs()
		{
			return amtTransAccs;
		}
		        														 											          
	/*
     * 
     * @param : int   
	 * @return : 
	 * @author :
	 *
	 */
		public  void setDistance( int distance )
		{
			this.distance = distance;
		}
		        														 											          
	/*
     * 
     * @param :    
	 * @return : int
	 * @author :
	 *
	 */
 		public  int getDistance()
		{
			return distance;
		}

//@@ Added By Ravi Kumar for Job File No PR 07072005

		public  void setJobFileId(String jobFileId )
		{
			this.jobFileId = jobFileId;
		}	
		
 		public  String getJobFileId()
		{
			return jobFileId;
		}
//@@ 07072005
		private String  	masterDocId  			=  null;
		private Timestamp	masterDocDate			=  null;
		private String  	terminalId				=  null;
		private String  	originGatewayId			=  null;
		private String  	destinationGatewayId 	=  null;
 		private String  vendorContractId			=  null;
 		private String  truckId						=  null;
		private String  	caContractId			=  null;
		private String  	carrierId				=  null;
		private String  	serviceLevelId			=  null;
		private String  	shipmentMode			=  null;
		private double		chargeableWeight 		=  0;
		private String  	UOW						=  null;
		private double  	unitRate				=  0;
		private int			pieces					=  0;
		private String  	currencyId				=  null;
		public Timestamp	closingDate				=  null;    
		private String  	closedBy				=  null;
		private double		discount				=  0;
		private String  	indicator				=  null; 		
		private double		conversionFactor		=  0;
		private double		totalAmount				=  0;
		private double		otherCharges			=  0;
		private String  	handlingInfo			=  null;
		private String  	status					=  null;
		public Timestamp	actualTime   			=  null;
		private String  	recepietStatus			=  null;
		private String[] 	houseDocId				=  null;
		private String[] 	houseSelected			=  null;
		// @@  Added By G.Srinivas  for DHL Issue FSET-2543 on 20050615 -->
		private String[]	reasonCode				=  null;

		private String   	paidStatus				=  null;
		private String   	carrierCode				=  null;	
		private String   	rateClass   			=  null;	 
		private String   	carriagevalue			=  null;	
		private String   	customsvalue			=  null;	 
		private String   	insurancevalue			=  null;	 
		private String   	commodityId    			=  null;	 
		private String   	notifyparty				=  null;	 
		private String   	description				=  null;	 
		private String   	otherremarks			=  null;	 
		private String   	coloaderId         		=  null;
		private String   	mawbOwner          		=  null;
		private String  	registered         		=  null;
		private double		blockedSpace			=  0;
// Trucking Variables starts
		private String  nonSystem           =  null;
		private String  consignmentType		=  null;
		private String  manifestType		=  null; 
		private long openingKm			=  0;
		private long closingKm			=  0;
		private String vendorId				=  null;			
		private String driverName			=  null;	
		private java.util.Date etaDt					=  null;
		private java.util.Date etdDt					=  null;	
		private boolean transhipment        =  false;
		private boolean isTruckExists       =  false; 
		private String[] prqIds = null;
		private String[] prqSelected = null;
		// Lov Description variable starts
		private String    	documentId		=  null;
 		private String  	shipperId		=  null;
 		private String  	consigneeId		=  null;
 		private String  	originTerminal	=  null;
 		private String  	destTerminal	=  null;
	    private int			primaryMode		=  0;
		private String      subAgentId		=  null;
		private double		masterCost		=  0.0;
		private double		amtTransAccs	=  0.0;
		private String 		rateIndicator	=  null;
		private int			distance		= 0;
		public  String		assignToTripsheet;	
		public int			intShipmentMode	= 0;
		public String 		accountStatus;
		public double		accountsTotalAmount;
		//@@ Avinash added on 20041201 (MULTI-UOM) 
		public String 		UOV;
		//@@ 20041201 (MULTI-UOM) 		
		//@@ Avinash added on 20050106 (DHL) 
		public boolean 		useNextContractSlabRate;
		public double		nextContractSlabRate;
		public double		nextContractSlabValue;
		//@@ 20041201 (DHL) 
		
		// @@ Suneetha Added on 20050630 (RATE_CLASS_VALIDATION)
		public double					volume;
		public java.util.ArrayList		rateClassDtlList;
		public int						laneNo;
		//public ETCarrierContractDtlDOB	etCarrierContractDtlDOB;
		// @@ 20050630 (RATE_CLASS_VALIDATION)

		public boolean 		cancelVehicleBookings;
		public String[] 		bookedVehicleIDs;
		public String[] 		bookingCancellationIDs;

//@@ Added by Ravi Kumar for Job File PR on  07-07-2005

		private String	jobFileId;
//@@ 07072005
		
		public static class ManifestTranshipmentPoint implements java.io.Serializable
		{
			public String vehicleID;
			public String origin;
			public String destination;
		}; 	 
}
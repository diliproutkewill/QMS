/*
 * @(#)MasterDocumentFlightDetails.java         07/09/2001
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.sql.Timestamp;
import com.foursoft.esupply.common.util.Logger;

   /**
	 * @file : MasterDocumentFlightDetails.java
	 * @author : 
	 * @date : 2003-08-07
	 * @version : 1.6 
	 */
public class MasterDocumentFlightDetails implements java.io.Serializable
{
	private static final String FILE_NAME = "Air >> MasterDocumentFlightDetails.java";


	   /*
		* @param :
		* @return : 
		* Contructor 
		*
		*/
	public MasterDocumentFlightDetails()
		{
			this.masterDocId	=  null;
			this.carrierId 		=  null;
			this.flightNo		=  null;
			this.flightFrom		=  null;
			this.etd			=  null;
			this.etdTime		=  null;
			this.flightTo		=  null;
			this.eta			=  null;
			this.etaTime		=  null;
			this.shipmentMode	=  null;

			this.truckId 		=  null;
			this.registered		=  null;
			
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
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setCarrierId( String[] carrierId )
		{
			this.carrierId = carrierId;
		}

				      
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public String[] getCarrierId()
		{
			return carrierId;
		}
     
	 		      
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setFlightNo( String[] flightNo )
		{
			this.flightNo = flightNo;
		}

						      
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public String[] getFlightNo()
		{
			return flightNo;
		}
        
		 		      
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setFlightFrom( String[] flightFrom )
		{
			this.flightFrom = flightFrom;
		}

							      
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public String[] getFlightFrom()
		{
			return flightFrom;
		}
		
			 		      
	/*
     * 
     * @param : Timestamp[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setEtd( Timestamp[] etd )
		{
			this.etd = etd;
		}

         			 		      
	/*
     * 
     * @param : 
	 * @return : Timestamp[]
	 * @author :
	 *
	 */
 		public Timestamp[] getEtd()
		{
			return etd;
		}
		

					 		      
	/*
     * 
     * @param : Timestamp[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setEtdTime( Timestamp[] etdTime )
		{
			this.etdTime = etdTime;
		}

		         			 		      
	/*
     * 
     * @param : 
	 * @return : Timestamp[]
	 * @author :
	 *
	 */
 		public Timestamp[] getEtdTime()
		{
			return etdTime;
		}
        

			         			 		      
	/*
     * 
     * @param : String[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setFlightTo( String[] flightTo )
		{
			this.flightTo = flightTo;
		}

				         			 		      
	/*
     * 
     * @param : 
	 * @return : String[]
	 * @author :
	 *
	 */
 		public  String[] getFlightTo()
		{
			return flightTo;
		}
		
						         			 		      
	/*
     * 
     * @param : Timestamp[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setEta( Timestamp[] eta )
		{
			this.eta = eta;
		}
		
        						         			 		      
	/*
     * 
     * @param : 
	 * @return : Timestamp[]
	 * @author :
	 *
	 */
 		public Timestamp[] getEta()
		{
			return eta;
		}
        
								         			 		      
	/*
     * 
     * @param : Timestamp[]
	 * @return : 
	 * @author :
	 *
	 */
 		public  void setEtaTime( Timestamp[] etaTime )
		{
			this.etaTime = etaTime;
		}
		

		     						         			 		      
	/*
     * 
     * @param : 
	 * @return : Timestamp[]
	 * @author :
	 *
	 */
 		public Timestamp[] getEtaTime()
		{
			return etaTime;
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
		
		//methods
				     						         			 		      
	/*
     * 
     * @param : 
	 * @return : int
	 * @author :
	 *
	 */
		public int size()
		{
			return carrierId.length;
		}
		
 		/*public java.sql.Timestamp[] getFlightEtd()
		{
			String[] str = this.etd;
			String[] str1 = this.etdTime;
			
			if( str!=null )
			{
				java.sql.Timestamp[] tsEtd = new java.sql.Timestamp[ str.length ];	
				for( int i=0; i<str.length; i++ )
				{
				  String strEtd = str[i].trim();	
				  String strEtdTime = null;
				  java.util.Date d = null;
				  int len = 0;
				  if( strEtd!=null )
				  	len = strEtd.length();
				  
				  int len1 = 0;
				  if( str1!=null )
  				    len1 = str1[i].trim().length();
					
				  if( len1>0 )
					strEtdTime = str1[i].trim();							  
				  else
				  	strEtdTime = "0:0";
				  	
				  if( len>0 )
			  	  {
			  	  	DateFormat mmdd = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, Locale.UK );
			  	  	
			  	  	try
		  	  		{
					    d = mmdd.parse( strEtd+" "+strEtdTime );
		  			    tsEtd[i] = new java.sql.Timestamp( d.getTime() );
	  			    }
	  			    catch( ParseException e )
  			    	{
  			    		tsEtd[i] = null;
  			    	}
			      }
				  else
			  	  {
	  			    tsEtd[i] = null;
		  	  	  }
  				}
  				return tsEtd;
			}
			return null;
		}
		
 		public java.sql.Timestamp[] getFlightEta()
		{
			String str[] = this.eta;
			String str1[] = this.etaTime;
			
			if( str!=null )
			{
				java.sql.Timestamp[] tsEta = new java.sql.Timestamp[ str.length ];
				for( int i=0; i<str.length; i++ )
				{
					String strEta = str[i].trim();
					String strEtaTime = null;
					java.util.Date d = null;
				    int len = 0;
				    if( strEta!=null )
				  	   len = strEta.length();					
				    
				    int len1 = 0;
				    if( str1!=null )
				    	len1 = str1[i].trim().length();
						
					if( len1>0 )
						strEtaTime	= str1[i].trim();
				    else
				    	strEtaTime = "0:0";
				    
					if( len>0 )
					{
					   DateFormat mmdd = DateFormat.getDateTimeInstance( DateFormat.SHORT, DateFormat.SHORT, Locale.UK );
					   try
				   	   {
					   		d = mmdd.parse( strEta+" "+strEtaTime );
  				       		tsEta[i] = new java.sql.Timestamp( d.getTime() );
  				       	}
  				       	catch( ParseException e )
  				       	{
  				       		tsEta[i] = null;
  				       	}
				    }
					else
					{
  				       tsEta[i] = null;
				   }
	  			}
	  			return tsEta;
  			}	
			return null;	
		}*/
        
				     						         			 		      
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
 		public String getTruckId()
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
		public  void setRegistered( String registered )
		{	
			Logger.info(FILE_NAME,"registered is setting in java bean as "+registered);
			this.registered = registered;
		}

						     						         			 		      
	/*
     * 
     * @param : 
	 * @return : String
	 * @author :
	 *
	 */
 		public  String getRegistered()
		{
			Logger.info(FILE_NAME,"registered is getting in java bean as "+registered);
			return registered;
		}



		//datamembers
 		private String    masterDocId  	=  null;
 		private String[]  carrierId		=  null;
 		private String[]  flightNo		=  null;
 		private String[]  flightFrom	=  null;
 		private Timestamp[]  etd			=  null;
 		private Timestamp[]  etdTime		=  null;
 		private String[]  flightTo 		=  null;
 		private Timestamp[]  eta			=  null;
 		private Timestamp[]  etaTime		=  null;
 		private String	  shipmentMode	=  null;

 		private String  truckId		=  null;
		private String	registered	=  null;

}
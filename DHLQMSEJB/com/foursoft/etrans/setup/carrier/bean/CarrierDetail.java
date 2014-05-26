/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/

/*    Program Name    : CarrierDetail.java
      Module Name     : ETrans
	  Task            : CarrierRegistration
	  Author Name     : Giridhar Manda
	  Date Started    : September 13,2001
	  Date Completed  : September 13,2001
	  Date Modified   : 
	  Description     :
	     This class is useful in manipulate all the details of Carrier.This class consist of accessors and mutators for these objects.
         This object is used in add, modify and view modules of the Carrier. In modify, view modules the Carrier 
		 details  data will be fetched( with respect to FS_FR_CAMASTER table in the database ) and the fetched data
		 will be stored in  this object,and this object will be returned back to the client. In add module, entered Carreir
		 details by the client will send to the server through this object.
	 Method Summery :
	    	String getCarrierId()                   // return carrierID
			String getCarrierName()			  	    // return carrierName	
			String getNumericCode()			  	    // return numericCode
			String getShipmentMode()	           	// return shipmentMode 
			int getAddressId()                     	// return addressId
			void setCarrierId(String carrierId)    	//setting the crrierId
			void setCarrierName(String carrierName) // setting the carrierName
			void setNumericCode(String numericCode) // setting the NumericCode
			void setShipmentMode(String shipmentMode) // setting the shipmentMode
			void setAddressId(int addressId)        // setting the addressId
	   	 
	           
*/

package com.foursoft.etrans.setup.carrier.bean;
/**
 * @author Giridhar Manda
 * @version etrans1.6
 */
public class CarrierDetail implements java.io.Serializable
{
	
	public String carrierId  	= null; // to store the carrierId
	public String carrierName 	= null; // to store the carrierName 
	public String numericCode 	= null; // to store the numericCode
  	public String shipmentMode 	= null; // to store the mode of Transportation
  	public int addressId; // to store the addressId
   private String invalidate=null;
  private String city=null;
	public String carrierNumber	= null; // to store the Auto Generation Of Carrier
  
  		
  /**
   * @param
   */
  	public CarrierDetail()
  	{
   		 carrierId  	= null;
  		 carrierName 	= null;
  	   	 numericCode 	= null;
  	   	 shipmentMode 	= null;
  	   	 addressId		= 0;  
		 carrierNumber	= null;
 	}					 					

	/**
    * Returns the Carrier Id.
    *
    * @returns the carrierId as String.
    */
	public String getCarrierId()
	{
   		return carrierId;
 	}	  	
	/**
     * Returns the Carrier Name.
     *
     * @returns the carrierName as String.
     */
	public String getCarrierName()
	{
   		return carrierName;
 	}	  	

	/**
     * Returns the Numeric Code. It is the code given to each CarrierId. 
     *
     * @returns the numericCode as String.
     */
	public String getNumericCode()
	{
   		return numericCode;
 	}

	/**
     * Returns the Mode of Transportation. It can be either Air, Sea or Truck
     *
     * @returns the shipmentMode as String.
     */
	public String getShipmentMode()
	{
   		return shipmentMode;
 	}

	/**
     * Returns the Address Id. 
     *
     * @returns the addressId as Integer.
     */
	public int getAddressId()
	{
   		return addressId;
 	}
	/**
     * Returns the carrierNumber. 
     *
     * @returns the carrierNumber as String.
     */
	public String getCarrierNumber()
	{
   		return carrierNumber;
 	}
 

    /**
     * Takes the Carrier Id as String argument and sets the same to the member variable, carrierId
     *
     * @param carrierId this String is used to set Carrier Id.
     */
  	public void setCarrierId(String carrierId)
	{
   		this.carrierId = carrierId;
 	}	  

    /**
     * Takes the Carrier Name as String argument and sets the same to the member variable, carrierName
     *
     * @param carrierName this String is used to set Carrier Name.
     */
	public void setCarrierName(String carrierName)
	{
   		this.carrierName = carrierName;
 	}

    /**
     * Takes the Numeric Code as String argument and sets the same to the member variable, numericCode
     *
     * @param numericCode this String is used to set Numeric Code.
     */
	public void setNumericCode(String numericCode)
	{
   		this.numericCode = numericCode;
 	}

    /**
     * Takes the Shipment Mode as String argument and sets the same to the member variable, shipmentMode
     *
     * @param shipmentMode this String is used to set Shipment Mode.
     */
	public void setShipmentMode(String shipmentMode)
	{
   		this.shipmentMode = shipmentMode;
 	}

    /**
     * Takes the Address Id as int argument and sets the same to the member variable, addressId
     *
     * @param addressId this int is used to set Address Id.
     */
	public void setAddressId(int addressId)
	{
   		this.addressId = addressId;
 	}
	/**
     * Takes the carrierNumber as String argument and sets the same to the member variable carrierNumber
     *
     * @param carrierNumber this String is used to set carrierNumber.
     */
	public void setCarrierNumber(String carrierNumber)
	{
   		this.carrierNumber = carrierNumber;
 	}
  public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate;
  }
  public String getInvalidate()
  {
    return invalidate;
  }
  public void setCity(String city)
  {
    this.city=city;
  }
  public String getCity()
  {
    return city;
  }
  
  }	

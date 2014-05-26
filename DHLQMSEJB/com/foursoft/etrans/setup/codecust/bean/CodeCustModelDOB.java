/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* esupply - v 1.x
*
*/


package com.foursoft.etrans.setup.codecust.bean;

/**
 * @author
 * @version etrans1.6
 */
	public class CodeCustModelDOB implements java.io.Serializable
	{

    public String companyId       = null;     
    public String locationId      = null;     
    public String carrierId       = null;   
    public String originId        = null;
    public String destinationId   = null;
    public String shipperName     = null;
    public String consigneeName   = null;
    public String consolType      = null;
    public String portOfLoading   = null;
    public String portOfDischarge = null;
    public String manifestType    = null;
    public String vehicleId       = null;
    public String vendorId        = null;
    public String truckId         = null;
    public String terminalId      = null;
    public int    shipmentMode    = 0;
    public String shipperId		    = null;
    public String consoleType	    = null;
    public String consigneeId     = null;

  /**
   * @param
   */
    public CodeCustModelDOB()
    {}
    
  /**
   * 
   * @param companyId
   * @param locationId
   * @param carrierId
   * @param originId
   * @param destinationId
   * @param shipperName
   * @param consigneeName
   * @param consolType
   * @param portOfLoading
   * @param portOfDischarge
   * @param manifestType
   * @param vehicleId
   * @param vendorId
   * @param truckId
   * @param terminalId
   * @param shipmentMode
   * @param shipperId
   * @param consoleType
   * @param consigneeId
   */
		public CodeCustModelDOB(String companyId,String locationId,String carrierId,String originId,String destinationId,
                            String shipperName,String consigneeName,String consolType,String portOfLoading,String portOfDischarge,
                            String manifestType,String vehicleId,String vendorId,String truckId,String terminalId,int shipmentMode,
                            String shipperId,String consoleType,String consigneeId)
		{
        		
           
            this.companyId       = companyId;
            this.consigneeName   = consigneeName;
            this.carrierId       = carrierId;
            this.consolType      = consolType;
            this.destinationId   = destinationId;
            this.locationId      = locationId;
            this.manifestType    = manifestType;
            this.originId        = originId;
            this.portOfDischarge = portOfDischarge;
            this.portOfLoading   =  portOfLoading;
            this.shipperName     = shipperName;
            this.consolType      = consolType;
            this.vendorId        = vendorId;
            this.vehicleId       = vehicleId;
            this.truckId         = truckId;
            this.terminalId      = terminalId;            
            this.shipmentMode	 = shipmentMode;		
            this.shipperId		 = shipperId;
            this.consoleType	 = consoleType;
            this.consigneeId   = consigneeId;
    }

}		

package com.foursoft.etrans.setup.codecust.bean;

import java.util.ArrayList;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;

/**
 * @author
 * @version etrans1.6
 */
public class CodeCustomisationDOB implements java.io.Serializable
{


  /**
   * @param
   */
  public CodeCustomisationDOB()
  {
     companyId      =   null;
     terminalId     =   null;
     locationId     =   null;
     
  }
  /**
   * 
   * @param companyId
   * @param terminalId
   * @param locationId
   */
  public CodeCustomisationDOB(String companyId,String terminalId,String locationId)
  {
    this.companyId  = companyId;
    this.terminalId = terminalId;
    this.locationId = locationId;
  }
  
  /**
   * 
   * @return String
   */
  public String getCompanyId()
  {
      return companyId;
  }
  /**
   * 
   * @param companyId
   */
  public void setCompanyId(String companyId)
	{
		this.companyId	= companyId;
	}    

  /**
   * 
   * @return String
   */
  public String getTerminalId()
  {
      return terminalId;
  }
  /**
   * 
   * @param terminalId
   */
  public void setTerminalId(String terminalId)
	{
		this.terminalId	= terminalId;
	}    

  /**
   * 
   * @return String
   */
  public String getLocationId()
  {
      return locationId;
  }
  /**
   * 
   * @param locationId
   */
  public void setLocationId(String locationId)
	{
		this.locationId	= locationId;
	}    
  /**
   * 
   * @return ArrayList
   */
  public ArrayList getVendorValues()
  {
      return vendorValues;
  }
  /**
   * 
   * @param vendorValues
   */
  public void setVendorValues(ArrayList vendorValues)
	{
		this.vendorValues	= vendorValues;
	}

  /**
   * 
   * @return ArrayList
   */
  public ArrayList getIdsList()
  {
      return idsLIst;
  }
  /**
   * 
   * @param idsLIst
   */
  public void setIdsList(ArrayList idsLIst)
	{
		this.idsLIst	= idsLIst;
	}
  
  public String  companyId  =  null;
  public String  terminalId =  null; 
  public String  locationId =  null;
  public ArrayList vendorValues = null;
  public ArrayList idsLIst   = null;
  
}
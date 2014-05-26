package com.foursoft.esupply.common.java;

public class ETCarrierContractAdvVO extends ETAdvancedLOVMasterVO 
{
  public ETCarrierContractAdvVO()
  {
  }
    protected String	contractId;
    protected String	carrierId;	
    protected String	originTerminalId;
    protected String	destTerminalId;	
    protected String	serviceLevelId;
    protected String	activeStatus;
    protected String validUpto;
    protected String	contractName;
    
    //set methods
    public void setValidUpto(String validUpto)
	{
		this.validUpto=validUpto;
	}
  public void setContractId(String contractId)
	{
		this.contractId=contractId;
	}
   public void setCarrierId(String carrierId)
	{
		this.carrierId=carrierId;
	}
   public void setOriginTerminalId(String originTerminalId)
	{
		this.originTerminalId=originTerminalId;
	}
   public void setDestTerminalId(String destTerminalId)
	{
		this.destTerminalId=destTerminalId;
	}
   public void setServiceLevelId(String serviceLevelId)
	{
		this.serviceLevelId=serviceLevelId;
	}
   public void setActiveStatus(String activeStatus)
	{
		this.activeStatus=activeStatus;
	}
   public void setContractName(String contractName)
	{
		this.contractName=contractName;
	}
  //get methods
   public String getValidUpto()
	{
		return validUpto;
	}
  public String getContractId()
	{
		return contractId;
	}
  public String getCarrierId()
	{
		return carrierId;
	}
  public String getOriginTerminalId()
	{
		return originTerminalId;
	}
  public String getDestTerminalId()
	{
		return destTerminalId;
	}
  public String getServiceLevelId()
	{
		return serviceLevelId;
	}
  public String getActiveStatus()
	{
		return activeStatus;
	}
  public String getContractName()
	{
		return contractName;
	}
}
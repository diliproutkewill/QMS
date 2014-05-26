package com.foursoft.esupply.common.java;


import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import java.util.HashMap;

public class ETConsoleAdvVO extends ETAdvancedLOVMasterVO
{
    protected String	consoleId;
    protected String	originGateway;
    protected String	destinationGateway;
    protected String	portOfLoading;
    protected String	portOfDestination;
    protected String	carrierId;
    protected String	serviceLevel;
    protected String	cutOffDays;
    protected String	consoleType;	
    protected String	fromWhat;
    protected String	operation;
    protected String	formField;
    protected String	dateFormat;
    
    HashMap	params=null;
 
    public ETConsoleAdvVO()
    {
    }
    public void setConsoleId(String consoleId)
    {
      this.consoleId=consoleId;
    }
    public void setOriginGateway(String originGateway)
    {
      this.originGateway=originGateway;
    }
    public void setDestinationGateway(String destinationGateway)
    {
      this.destinationGateway=destinationGateway;
    }
    public void setPortOfLoading(String portOfLoading)
    {
      this.portOfLoading=portOfLoading;
    }
    public void setPortOfDestination(String portOfDestination)
    {
      
      this.portOfDestination=portOfDestination;
    }
    public void setCarrierId(String carrierId)
    {
      this.carrierId=carrierId;
    }
    public void setServiceLevel(String serviceLevel)
    {
      this.serviceLevel=serviceLevel;
    }
	
    public void setCutOffDays(String cutOffDays)
    {
      
      this.cutOffDays=cutOffDays;
    }
    
    public void setConsoleType(String consoleType)
    {
      this.consoleType=consoleType;
    }
    
    public void setFromWhat(String fromWhat)
    {
      this.fromWhat=fromWhat;
    }
    public void setOperation(String operation)
    {
      this.operation=operation;
    }
    public void setFormField(String formField)
    {
      this.formField=formField;
    }
    public void setDateFormat(String dateFormat)
    {
      this.dateFormat=dateFormat;
    }
     public String getConsoleId()
      {
        return consoleId;
      }
      public String getOriginGateway()
      {
        return originGateway;
      }
      public String getDestinationGateway()
      {
        return destinationGateway;
      }
  

     public String getPortOfLoading()
    {
      return portOfLoading;
    }
    public String getPortOfDestination()
    {
  
      return portOfDestination;
    }
    public String getCarrierId()
    {
      return carrierId;
    }
    public String getServiceLevel()
    {
      return serviceLevel;
    }
    public String getCutOffDays()
    {
  
      return cutOffDays;
    }
    public String getConsoleType()
    {
      return consoleType;
    }
    public String getFromWhat()
    {
      return fromWhat;
    }
    
    public String getOperation()
    {
      return operation;
    }
    public String getFormField()
    {
      return formField;
    }
    
    public String getDateFormat()
    {
      return dateFormat;
    }
    
    
}
package com.qms.setup.java;

public class IndustryRegDOB implements java.io.Serializable
{
    String industry   = null;
    String description= null;
    String invalidate = null;
    String terminalId = null;
    public IndustryRegDOB()
    {
    }
    
    public void setIndustry(String industry)
    {
      this.industry=industry;
    }
    public String getIndustry()
    {
      return this.industry;
    }
    
    public void setTerminalId(String terminalId)
    {
      this.terminalId=terminalId;
    }
    public String getTerminalId()
    {
      return this.terminalId;
    }
    
    public void setDescription(String description)
    {
      this.description=description;
    }
    public String getDescription()
    {
      return this.description;
    }
    
    public void setInvalidate(String invalidate)
    {
      this.invalidate = invalidate;
    }
    public String getInvalidate()
    {
      return this.invalidate;
    }
}
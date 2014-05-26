package com.foursoft.esupply.common.java;

 public class ETAdvancedLOVMasterVO 
{
  protected  String	 	noOfDays ;
  protected  String		mode ;
  protected  String		operation ;
  protected  String		terminalType ;
  protected  String		terminalID ;
  protected  String		entity ;
  protected  String		noOfDaysControl ;
  protected  String	    searchType ;
  protected  String	    type ;
  protected  String	  operationType ;
  protected  String	  invokerOperation ;
  protected  String Customer;
  protected   String Register;
  protected   String Corporate;
  protected String Customerregi;
  protected  String AIRPRQ;
  protected   String ECustomer;  
  protected String SEAPRQ;
  protected String INVOICEADD;
    protected String Mapping;
    protected String DAWB; 
    protected String Direct;
     protected  String Terminaldel;
     protected String  contractadd;
	 protected String accessLevel;//added by rk 
     
     
       
	public void setAccessLevel(String a)//added by rk 
	{
		this.accessLevel=a;
	}
  	public String getAccessLevel()//added by rk 
	{
		return this.accessLevel;
	}
 public void setType(String type)
	{
		this.type=type;
	}
  public void setOperationType(String operationType)
	{
		this.operationType=operationType;
	}
  public void setInvokerOperation(String invokerOperation)
	{
		this.invokerOperation=invokerOperation;
	}
 public void setSearchType(String searchType)
	{
		this.searchType=searchType;
	}
 public void setNoOfDaysControl(String noOfDaysControl)
	{
		this.noOfDaysControl=noOfDaysControl;
	}
   public void setNoOfDays(String noOfDays)
	{
		this.noOfDays=noOfDays;
	}
   public void setMode(String mode)
	{
		this.mode=mode;
	}
  public void setOperation(String operation)
	{
		 this.operation=operation;
	}
  public void setTerminalType(String terminalType)
	{ 
		 this.terminalType=terminalType;
	}
 public void setTerminalID(String terminalID)
	{
		this.terminalID=terminalID;
	}
  public void setEntity(String entity)
	{
		 this.entity=entity;
 	}
  
  public void setCustomer(String Customer)
	{
		this.Customer=Customer;
  }
    public void setRegister(String Register)
	{
		this.Register=Register;
	}
  public void setCorporate(String Register)
	{
		this.Corporate=Corporate;
	}
   public void setCustomerregi(String Customerregi)
	{
		this.Customerregi=Customerregi;
	}
   public void setAIRPRQ(String AIRPRQ)
	{
		this.AIRPRQ=AIRPRQ;
	}
  public void setECustomer(String ECustomer)
	{
		this.ECustomer=ECustomer;
	}
  public void setSEAPRQ(String SEAPRQ)
	{
		this.SEAPRQ=SEAPRQ;
	}
  public void setINVOICEADD(String INVOICEADD)
	{
		this.INVOICEADD=INVOICEADD;
	}
  public void setMapping(String Mapping)
	{
		this.Mapping=Mapping;
	}
   public void setDAWB(String DAWB)
	{
		this.DAWB=DAWB;
	} 
  
  public void setDirect(String Direct)
	{
		this.Direct=Direct;
	} 
  public void setTerminaldel(String b)
	{ 
		this.Terminaldel=b;
	}
  
  public void setcontractadd(String h)
  {
    this.contractadd=h;
  }
  
  
  
  
  
  
  
	

	//get methods
  public String getType()
	{
		 return type;
	}
  public String getOperationType()
	{
		return operationType;
	}
  public String getSearchType()
	{
		return searchType;
	}
  public String getNoOfDaysControl()
	{
		return noOfDaysControl;
	}
  public String getEntity()
	{
		return entity;
	}
  public String getNoOfDays()
	 {
		return noOfDays;
	}
  public String getMode()
	{
	 	return mode;
	}
    public String getOperation()
	{
		return operation;
	}
   public String getInvokerOperation()
	{
		return invokerOperation;
	}
  public String getTerminalType()
	{
		return terminalType;
	}
public String getTerminalId()
	{
		return terminalID;
  }    
  public String getTerminalID()
	{
		return terminalID;
  }
     public String getCustomer()
	{
		 return Customer; 
 	}
public String getRegister()
	{
	 	return Register;  

	}
public String getCorporate()
	{
	      return Corporate;
	}
  public String getCustomerregi()
	{
	    return Customerregi;
	 }
    public String getAIRPRQ()
	{
	      return AIRPRQ;
	 }
   public String getECustomer()
	{
	      return ECustomer;
	 }
   public String getSEAPRQ() 
	{
	        return  SEAPRQ;
	 } 
   public String getINVOICEADD()
	{ 
	      return INVOICEADD;
	 }
    public String getMapping()
	{
	     return Mapping;
	 }
   public String getDAWB()
	{ 
	     return DAWB;
	 }
    public String getDirect()
	{
	     return Direct;
	 } 
   public String getTerminaldel()
	{
		return Terminaldel;
	}
   
   public String getcontractadd()
   {
     return contractadd;
   } 
      
   
  
  
  
} 
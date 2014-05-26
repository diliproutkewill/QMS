package com.foursoft.esupply.common.java;


import com.foursoft.esupply.common.java.ETAdvancedLOVMasterVO;
import java.util.HashMap;

public class ETCustomerAdvVO extends ETAdvancedLOVMasterVO
{
    

	protected  String Terminal;
	protected   String Customername;
	protected   String CustomerId;
	protected   String City;
	protected   String Country;
	protected   String Regi;
	protected   String EIN;
  protected   String known;
  protected   String Corporate;
  protected String Customerregi;
  protected String formField;
 
  
     


     HashMap	params=null;
 
     public ETCustomerAdvVO()
    {
    }
  
 	public void setFormField(String a)
	{
		this.formField=a;
	}
	public String getFormField()
	{
		return this.formField;
	}
  
  
  
  
	
	
	public void setTerminal(String Terminal)
	{
		this.Terminal=Terminal;
	}
	public void setCustomerID(String CustomerId)
	{
	  	this.CustomerId=CustomerId;
	}
	public void setCustomername(String Customername)
	{
		this.Customername=Customername;
	}
	public void setCity(String City)
	{
		this.City=City;
	}
	public void setCountry(String Country)
	{
	 	this.Country=Country;
	}
	public void setRegi(String Regi)
	{
		 this.Regi=Regi;
	}
	public void setknown(String known)
	{
		this.known=known;
	}
public void setEIN(String EIN)
	{
		this.EIN=EIN;
	}
	 public void setCorporate(String Corporate)
	{
		this.Corporate=Corporate;
	}
  public void setCustomerregi(String Customerregi)
	{
		this.Customerregi=Customerregi;
	}
  
  

	
	 
	
  	public String getCustomerId()
	{
	 	return CustomerId;
	}
	public String getCustomername()
	{ 
		return Customername; 
	}
	public String getCity()
	{
		return City;
	}
		  public String getCountry()
	{
		 return Country;
	}
		public String getTerminal()
	{
		return Terminal;
	}
	public String getRegi()
	{
		return Regi;
	}
	public String getknown()
	{
	  	 return known;
	}
  public String getEIN()
	{ 
		 return EIN;
	}
	  public String getCorporate()
	{ 
	    return Corporate;
	} 
   
   public String getCustomerregi()
	{ 
	     return Customerregi;
	 }
   
   
  
 

  

 
}
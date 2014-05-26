/*
 * Copyright ©.
 */
package com.foursoft.etrans.common.bean;

/**
 * @author
 * @version etrans1.6
 */
public class Address implements java.io.Serializable
{
	//private static final String FILE_NAME = "Address.java";
  /**
   * @param
   */
     public Address()
     {
     	  this.addressId   		= 0;
          this.addressLine1     = null;
          this.addressLine2     = null; 
          this.addressLine3=null;
          this.city   	        = null;
          this.state            = null;
          this.zipCode          = null;
          this.countryId        = null;
          this.phoneNo          = null;
          this.emailId          = null;
          this.fax              = null;
          this.helpLine         = null;
     }
	 
  /**
   * 
   * @param addressId
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param state
   * @param zipCode
   * @param countryId
   * @param phoneNo
   * @param emailId
   * @param fax
   * @param customerId
   * @param companyName
   */
	 public Address( int addressId, 
     				 String addressLine1,
     				 String addressLine2,
     				 String city,
     				 String state,
     				 String zipCode,
   				 	 String countryId,
   				 	 String phoneNo,
   				 	 String emailId,
   				 	 String fax,
					 String customerId,
					 String companyName)
     {
          this.addressId   		= addressId;
          this.addressLine1     = addressLine1;
          this.addressLine2     = addressLine2;          
          this.city   	        = city;
          this.state            = state;
          this.zipCode          = zipCode;
          this.countryId        = countryId;
          this.phoneNo          = phoneNo;
          this.emailId          = emailId;
          this.fax              = fax;
	      this.customerId		= customerId;
		  this.companyName		= companyName;
     }

  /**
   * 
   * @param addressId
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param state
   * @param zipCode
   * @param countryId
   * @param phoneNo
   * @param emailId
   * @param fax
   * @param hlepLine
   */
	 public Address( int addressId, 
     				 String addressLine1,
     				 String addressLine2,
     				 String city,
     				 String state,
     				 String zipCode,
   				 	 String countryId,
   				 	 String phoneNo,
   				 	 String emailId,
   				 	 String fax,
					 String hlepLine)
     {
          this.addressId   		= addressId;
          this.addressLine1     = addressLine1;
          this.addressLine2     = addressLine2;          
          this.city   	        = city;
          this.state            = state;
          this.zipCode          = zipCode;
          this.countryId        = countryId;
          this.phoneNo          = phoneNo;
          this.emailId          = emailId;
          this.fax              = fax;
	      this.helpLine			= helpLine;
//		  Logger.error(FILE_NAME,"Address HelpLine :"+helpLine);
     }

  /**
   * 
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param state
   * @param zipCode
   * @param countryId
   * @param phoneNo
   * @param emailId
   * @param fax
   * @param addressId
   */
public Address(String addressLine1,
     				 String addressLine2,
             String addressLine3,
     				 String city,
     				 String state,
     				 String zipCode,
   				 	 String countryId,
   				 	 String phoneNo,
   				 	 String emailId,
   				 	 String fax,
					 int    addressId
					 )
     {
          
          this.addressLine1     = addressLine1;
          this.addressLine2     = addressLine2;   
          this.addressLine3     = addressLine3; 
          this.city   	        = city;
          this.state            = state;
          this.zipCode          = zipCode;
          this.countryId        = countryId;
          this.phoneNo          = phoneNo;
          this.emailId          = emailId;
          this.fax              = fax;
	      this.addressId   		= addressId;
//		  this.helpLine			= helpLine;
//		  Logger.info(FILE_NAME,"Address HelpLine :"+helpLine);
     } 
  /**
   * 
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param countryId
   */
public Address(String addressLine1, String addressLine2,String city ,String countryId)
      {
		this.addressLine1 = addressLine1 ;
		this.addressLine2 = addressLine2 ;
		this.city         = city ;
		this.countryId    = countryId ;
      } 

//This Constructer is Added by JS And it is used for storing the multiple Customer Addresses

  /**
   * 
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param state
   * @param zipCode
   * @param countryId
   * @param phoneNo
   * @param emailId
   * @param contactName
   * @param designation
   * @param delFlag
   * @param addressType
   */
	public Address(String addressLine1,String addressLine2,String addressLine3, String city,String state,String zipCode,String countryId,String phoneNo, String emailId,String contactName,String designation,String delFlag,String addressType )
     {
          
          this.addressLine1     = addressLine1;
          this.addressLine2     = addressLine2;          
          this.addressLine3     = addressLine3;
          this.city   	        = city;
          this.state            = state;
          this.zipCode          = zipCode;
          this.countryId        = countryId;
          this.phoneNo          = phoneNo;
          this.emailId          = emailId;		  
		  this.contactName		= contactName;
		  this.designation		= designation;
		  this.delFlag			= delFlag;
		  this.addressType		= addressType;
       
     } 
  /**
   * 
   * @param addressLine1
   * @param addressLine2
   * @param city
   * @param state
   * @param zipCode
   * @param countryId
   * @param phoneNo
   * @param emailId
   * @param contactName
   * @param designation
   * @param delFlag
   * @param addressType
   * @param addressId
   */
	 	public Address(String addressLine1,String addressLine2,String addressLine3, String city,String state,String zipCode,String countryId,String phoneNo, String emailId,String contactName,String designation,String delFlag,String addressType, int addressId)
     {
          
          this.addressLine1     = addressLine1;
          this.addressLine2     = addressLine2;
		  this.addressLine3     = addressLine3;
          this.city   	        = city;
          this.state            = state;
          this.zipCode          = zipCode;
          this.countryId        = countryId;
          this.phoneNo          = phoneNo;
          this.emailId          = emailId;		  
		  this.contactName		= contactName;
		  this.designation		= designation;
		  this.delFlag			= delFlag;
		  this.addressType		= addressType;
		  this.addressId		= addressId;
       
     } 


  /**
   * 
   * @param helpLine
   */
     public void setHelpLine ( String  helpLine ) 
     {
     	  this.helpLine   		= helpLine;
     }

  /**
   * 
   * @param customerId
   */
     public void setCustomerId ( String  customerId ) 
     {
     	  this.customerId   		= customerId;
     }
  /**
   * 
   * @param companyName
   */
	 public void setCompanyName ( String companyName ) 
     {
     	  this.companyName   	= companyName;
     }

  /**
   * 
   * @param addressId
   */
     public void setAddressId ( int addressId ) 
     {
     	  this.addressId   		= addressId;
     }
  /**
   * 
   * @param addressLine1
   */
     public void setAddressLine1( String addressLine1 )
     {
          this.addressLine1     = addressLine1;
      }
  /**
   * 
   * @param addressLine2
   */
     public void setAddressLine2( String  addressLine2 )
     {
          this.addressLine2     = addressLine2;          
     }
     /**
   * 
   * @param addressLine2
   */
     public void setAddressLine3( String  addressLine3 )
     {
          this.addressLine3     = addressLine3;          
     }
  /**
   * 
   * @param city
   */
     public void setCity( String city)
     {
          this.city   	        = city;
     }
  /**
   * 
   * @param state
   */
     public void setState( String state)
     { 
          this.state            = state;
      }
  /**
   * 
   * @param zipCode
   */
      public void setZipCode( String zipCode)
      { 
          this.zipCode          = zipCode;
      }
  /**
   * 
   * @param countryId
   */
      public void setCountryId( String countryId)
      {
          this.countryId        = countryId;
      }
  /**
   * 
   * @param phoneNo
   */
      public void setPhoneNo( String phoneNo )
      {
          this.phoneNo          = phoneNo;
      }
  /**
   * 
   * @param emailId
   */
      public void setEmailId( String emailId )
      {
          this.emailId          = emailId;
      }
  /**
   * 
   * @param fax
   */
      public void setFax(String fax)
      {
          this.fax              = fax;
      }     
  /**
   * 
   * @return int
   */
     public int getAddressId()
     {
        return addressId;
     }
  /**
   * 
   * @return  String
   */
     public String getAddressLine1()
     {
        return addressLine1;
     }
  /**
   * 
   * @return  String
   */
     public String getAddressLine2()
     {
        return addressLine2;
     }
      public String getAddressLine3()
     {
        return addressLine3;
     }
  /**
   * 
   * @return  String
   */
     public String getCity()
     {
        return city;
     }
  /**
   * 
   * @return  String
   */
     public String getState ()
     {
        return state ;
     }
  /**
   * 
   * @return  String
   */
     public String getZipCode()
     {
        return zipCode;
     }
  /**
   * 
   * @return  String
   */
     public String getCountryId()
     {
        return countryId;
     }
  /**
   * 
   * @return  String
   */
     public String getPhoneNo ()
     {
        return phoneNo ;
     }
  /**
   * 
   * @return  String
   */
     public String getEmailId ()
     {
        return  emailId ;
     }
  /**
   * 
   * @return  String
   */
	 public String getFax()
     {
        return fax;
     }
  /**
   * 
   * @return  String
   */
	 public String getCustomerId()
     {
        return customerId;
     }
  /**
   * 
   * @return  String
   */
	 public String getCompanyName()
     {
        return companyName;
     }
  /**
   * 
   * @return  String
   */
     public String getHelpLine ( ) 
     {
     	  return this.helpLine ;
     }


  /**
   * 
   * @return  String
   */
	 public String getContactName(){
		return contactName;
	 }
  /**
   * 
   * @param contactName
   */
	 public void setContactName(String contactName){
		 this.contactName	=	contactName;
		
	 }
  /**
   * 
   * @return  String
   */
	 public String getDesignation(){
		return designation;
	 }
  /**
   * 
   * @param designation
   */
	 public void setDesignation(String designation){
		this.designation	=	designation;
	 }
  /**
   * 
   * @return  String
   */
	 public String getDelFlag(){
		return delFlag;
	 }
  /**
   * 
   * @param delFlag
   */
	 public void setDelFlag(String delFlag){
		this.delFlag	=	delFlag;
	 }
  /**
   * 
   * @return  String
   */
	 public String getAdddressType(){
		return addressType;
	 }
  /**
   * 
   * @param addressType
   */
	 public void setAddressType(String addressType){
		this.addressType	=	addressType;
	 }


     //datamembers
     private String  customerId		 = null;
	 private String  companyName    = null;
     private int     addressId       = 0;
     private String  addressLine1    = null;
     private String  addressLine2    = null;
	 private String	 addressLine3	=null;
     private String  city            = null;
     private String  state           = null;
     private String  zipCode         = null;
     private String  countryId       = null;
     private String  phoneNo         = null;
     private String  emailId         = null;
     private String  fax             = null;
	 private String  helpLine		 = null;
	
	 private String	contactName		= null; //Added by JS
	 private String	designation		= null; //Added by JS
	 private String	delFlag			= null; //Added by JS
	 private String	addressType		= null; //Added by JS

}
/**
*	@(#) AddressDOB.java
*	Copyright (c) 2000-2001 Four Soft Pvt Ltd,
*	5Q1A3, Hi-Tech Cit, Madhapur, Hyderabad-33, India.
*	This software is the confidential and proprietary information of Four Soft Pvt Ltd,
*	("Confidential Information"), you shall not disclose such confidential Information and shall
*	use it only in accordance with the terms of the License agreement your entered in to  with Four Soft.
*	For more detals contact Four Soft Pvt Ltd.,
*/

package com.foursoft.esupply.common.java;
import java.io.Serializable;
/**
*	This class will be usefull to manipulate all the details of AddressDOB detals
*	This class useful to prepare the Object at Ejb's and use full to manipulate
*	at client side and this class implements java.io.Serializable.
*	@see also java.util.Serializable
*/

public class AddressDOB implements Serializable
{

/**
*	These variables are global variables these variables are initialized at constructor level
*	@param String addressLine1 AddressLine1.
*	@param String addressLine2 AddressLine2.
*	@param String city.City name.
*	@param String state.State name.
*	@param String countryId,Country Id it is String.
*	@prama String zipCode,City zip code.
*	@prama String phoneNo,phone number.
*	@prama String fax,fax number .
*	@prama String emailId,Emial Id .
*/	
  public String addressLine1;
  public String addressLine2;
  public String city;
  public String state;
  public String countryId;
  public String zipCode;
  public String phoneNo;
  public String fax;
  public String emailId;
/** *This is a default constructor.
   *It does't take any arguments.
 */
   public AddressDOB()
  {
  }//End of the constructor
/**
*	This constructor with 9 parameters as arguments.
*	Through this constructor we will initialize all the parameters and 
*	construct a Object.
*@param String newAddressLine1.
*@param String newAddressLine2.
*@param String newCity.
*@param String newState.
*@param String newCountryId.
*@param String newnewZipCode.
*@param String newPhoneNo.
*@param String newFax.
*@param String newEmailId.
*These are all formal parameters assigned to actual parameters.
*/
  public AddressDOB(String newAddressLine1, String newAddressLine2, String newCity, String newState, String newZipCode, String newCountryId, String newPhoneNo, String newEmailId, String newFax)
  {
	addressLine1= newAddressLine1;
	addressLine2= newAddressLine2;
	city= newCity;
	state= newState;
	countryId= newCountryId;
	zipCode= newZipCode;
	phoneNo= newPhoneNo;
	fax= newFax;
	emailId= newEmailId;
  }//End of the constructor

/** *This method is retrive the Addres Line1.
    *@return String addressLine1.It will return a String value.
	*It doesnot take any arguments.
*/
  
  
  public String getAddressLine1()
  {
    return addressLine1;
  }//End of the Method
/** *This method is setting the Addres Line1.
    *@param String newAddressLine1.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setAddressLine1(String newAddressLine1)
  {
    addressLine1 = newAddressLine1;
  }//End of the Method

/** *This method is setting the Addres Line2.
    *@param String newAddressLine2.It will take the one argument as a String.
	*It doesnot return  any value.
*/
  public void setAddressLine2(String newAddressLine2)
  {
    addressLine2 = newAddressLine2;
  }//End of the Method
/** *This method is setting the Addres Line2.
    *@param String newAddressLine2.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public String getAddressLine2()
  {
    return addressLine2;
  }//End of the Method
/** *This method is setting the city name.
    *@param String newCity.It will take the one argument as a String.
	*It doesnot return  any value.
*/


  public void setCity(String newCity)
  {
    city = newCity;
  }//End of the Method

/** *This method is retrive the City name.
    *@return String city.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getCity()
  {
    return city;
  }//End of the Method
/** *This method is setting the State name.
    *@param String newState.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setState(String newState)
  {
    state = newState;
  }//End of the Method
/** *This method is retrive the State name.
    *@return String state.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getState()
  {
    return state;
  }//End of the Method
/** *This method is setting the Country Id.
    *@param String newCountryId.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setCountryId(String newCountryId)
  {
    countryId = newCountryId;
  }//End of the Method
/** *This method is retrive the Country Id.
    *@return String countryId.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getCountryId()
  {
    return countryId;
  }//End of the Method
/** *This method is setting the Zip Code.
    *@param String newZipCode.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setZipCode(String newZipCode)
  {
    zipCode = newZipCode;
  }//End of the Method
 /** *This method is retrive the Zip code.
    *@return String zipCode.It will return a String value.
	*It doesnot take any arguments.
*/
  public String getZipCode()
  {
    return zipCode;
  }//End of the Method
/** *This method is setting the Phone Number.
    *@param String newPhoneNo.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setPhoneNo(String newPhoneNo)
  {
    phoneNo = newPhoneNo;
  }//End of the Method
/** *This method is retrive the Phone number.
    *@return String phoneNo.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getPhoneNo()
  {
    return phoneNo;
  }//End of the Method
/**  *This method is setting the Fax number.
    *@param String newFax.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setFax(String newFax)
  {
    fax = newFax;
  }//End of the Method
/**  *This method is retrive the Fax number.
    *@return String fax.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getFax()
  {
    return fax;
  }//End of the Method
/** 
      *This method is setting the Email Id.
    *@param String newEmailId.It will take the one argument as a String.
	*It doesnot return  any value.
*/

  public void setEmailId(String newEmailId)
  {
    emailId = newEmailId;
  }//End of the Method
/**
    *This method is retrive the Email Id.
    *@return String emailId.It will return a String value.
	*It doesnot take any arguments.
*/


  public String getEmailId()
  {
    return emailId;
  }//End of the Method
  
  public String toString()
  {
	return addressLine1+" "+addressLine2+" "+city+" "+state+" "+countryId+" "+zipCode+" "+phoneNo+" "+fax+" "+emailId;
  }
}//End of the AddressDOB class
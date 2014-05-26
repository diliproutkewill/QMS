/*
	Program Name	:SalesPersonRegistrationPK.java
	Module Name		:QMSSetup
	Task			    :SalesPersonRegistration EntityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.bmp;
import java.io.Serializable;

public class SalesPersonRegistrationPK implements Serializable 
{
  public String salesPersonCode      =    null;
  
  public SalesPersonRegistrationPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof SalesPersonRegistrationPK)
    {
      final SalesPersonRegistrationPK otherSalesPersonRegistrationPK = (SalesPersonRegistrationPK)other;

      // The following assignment statement is auto-maintained and may be overwritten.
      boolean areEqual = true;

      return areEqual;
    }

    return false;
  }

  public int hashCode()
  {
    // Add custom hashCode() impl here
    return super.hashCode();
  }
}
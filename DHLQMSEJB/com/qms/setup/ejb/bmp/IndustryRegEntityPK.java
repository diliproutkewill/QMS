package com.qms.setup.ejb.bmp;
import java.io.Serializable;

/**
 * 
 * This class will be useful to .
 * 
 * File		  : ConsoleRegistrationEntityBean.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */

public class IndustryRegEntityPK implements Serializable 
{
  public java.lang.String industry;
  
  public IndustryRegEntityPK()
  {
  }
  /**
   * 
   * @param obj
   * @return boolean
   */
  public boolean equals(Object obj)
  {
    if (obj instanceof IndustryRegEntityPK)
    {
      final IndustryRegEntityPK otherIndustryRegEntityPK = (IndustryRegEntityPK)obj;

      // The following assignment statement is auto-maintained and may be overwritten.
      boolean areEqual = true;

      return areEqual;
    }

    return false;
  }
  /**
   * 
   * @return int
   */
  public int hashCode()
  {
   try {
			long	crcKey = -1;
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
			java.util.zip.Adler32 adl32 = new java.util.zip.Adler32();
			adl32.update(bos.toByteArray());
			crcKey = adl32.getValue();
			return (int)(crcKey ^ (crcKey >> 32));
      }
      catch (java.io.IOException ioEx) 
      {
        return -1;
     // return super.hashCode();
      }
  }
}
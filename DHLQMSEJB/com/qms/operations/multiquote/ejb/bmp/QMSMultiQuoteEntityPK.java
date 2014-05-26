
/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: QMSMultiQuoteEntityPK.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */



package com.qms.operations.multiquote.ejb.bmp;
import java.io.Serializable;

public class QMSMultiQuoteEntityPK implements Serializable 
{
  public QMSMultiQuoteEntityPK()
  {
  }
  
  public long quoteId;

  public boolean equals(Object other)
  {
    if (other instanceof QMSMultiQuoteEntityPK)
    {
      final QMSMultiQuoteEntityPK otherQMSQuoteEntityPK = (QMSMultiQuoteEntityPK)other;

      // The following assignment statement is auto-maintained and may be overwritten.
      //boolean areEqual = true;

      return (quoteId==otherQMSQuoteEntityPK.quoteId);
    }

    return false;
  }

  public int hashCode()
  {
		try 
    {
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
		}
	}
}
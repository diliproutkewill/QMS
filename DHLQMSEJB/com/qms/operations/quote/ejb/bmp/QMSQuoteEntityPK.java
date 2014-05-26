package com.qms.operations.quote.ejb.bmp;
import java.io.Serializable;

public class QMSQuoteEntityPK implements Serializable 
{
  public QMSQuoteEntityPK()
  {
  }
  
  public long quoteId;

  public boolean equals(Object other)
  {
    if (other instanceof QMSQuoteEntityPK)
    {
      final QMSQuoteEntityPK otherQMSQuoteEntityPK = (QMSQuoteEntityPK)other;

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
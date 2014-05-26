package com.qms.operations.sellrates.ejb.bmp;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.io.Serializable;

public class QMSSellRatesEntityPK implements Serializable 
{
  public QMSSellRatesEntityPK()
  {
  }
  private QMSSellRatesDOB rateDetails;
  public boolean equals(Object other)
  {
    boolean areEqual = false;
    if (other instanceof QMSSellRatesEntityPK)
    {
      final QMSSellRatesEntityPK otherQMSSellRatesEntityPK = (QMSSellRatesEntityPK)other;

      // The following assignment statement is auto-maintained and may be overwritten.
      areEqual = true;      
    }

    return areEqual;
  }

  public int hashCode()
  {
    // Add custom hashCode() impl here
    return super.hashCode();
  }

  public QMSSellRatesDOB getRateDetails()
  {
    return rateDetails;
  }

  public void setRateDetails(QMSSellRatesDOB rateDetails)
  {
    this.rateDetails = rateDetails;
  }
}
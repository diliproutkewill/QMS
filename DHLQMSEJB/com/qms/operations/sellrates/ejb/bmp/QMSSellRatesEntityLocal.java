package com.qms.operations.sellrates.ejb.bmp;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import javax.ejb.EJBLocalObject;

public interface QMSSellRatesEntityLocal extends EJBLocalObject 
{
  /**
   * 
   *@param  QMSSellRatesDOB
   * 
   */
  public void setQMSSellRates(QMSSellRatesDOB rateDetails);
   /**
   * 
   * 
   * 
   */
  public java.util.ArrayList getQMSSellRates();
   /**
   * 
   * @return QMSSellRatesDOB
   * 
   */  
}
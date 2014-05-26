package com.qms.operations.quote.ejb.sls;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface QMSQuoteSessionLocalHome extends EJBLocalHome 
{
  QMSQuoteSessionLocal create() throws CreateException;
}
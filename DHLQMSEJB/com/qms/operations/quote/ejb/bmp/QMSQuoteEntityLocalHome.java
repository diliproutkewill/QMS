package com.qms.operations.quote.ejb.bmp;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface QMSQuoteEntityLocalHome extends EJBLocalHome 
{
  QMSQuoteEntityLocal create(QuoteFinalDOB finalDOB) throws CreateException;

  QMSQuoteEntityLocal findByPrimaryKey(QMSQuoteEntityPK primaryKey) throws FinderException;
}
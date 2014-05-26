package com.qms.operations.sellrates.ejb.bmp;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.util.ArrayList;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface QMSSellRatesEntityLocalHome extends EJBLocalHome 
{
  QMSSellRatesEntityLocal create() throws CreateException;
  
  QMSSellRatesEntityLocal create(ArrayList sellRateDetails) throws CreateException;
  
  QMSSellRatesEntityLocal create(QMSSellRatesDOB sellDob,ArrayList listValues,ESupplyGlobalParameters loginbean,String operation) throws CreateException;

  QMSSellRatesEntityLocal findByPrimaryKey(QMSSellRatesEntityPK primaryKey) throws FinderException;
  
  QMSSellRatesEntityLocal create(ArrayList sellDob,ArrayList listValues,ESupplyGlobalParameters loginbean,String operation) throws CreateException;
}
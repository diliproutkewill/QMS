package com.qms.setup.ejb.bmp;
import com.qms.setup.java.ZoneCodeMasterDOB;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface ZoneCodeMasterLocalHome extends EJBLocalHome 
{
  ZoneCodeMasterLocal create() throws CreateException;
  
  ZoneCodeMasterLocal create(ZoneCodeMasterDOB dob) throws CreateException;
  
  ZoneCodeMasterLocal findByPrimaryKey(ZoneCodeMasterPK primaryKey) throws FinderException;
}
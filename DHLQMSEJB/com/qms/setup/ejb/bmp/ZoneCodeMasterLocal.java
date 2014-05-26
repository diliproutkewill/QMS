package com.qms.setup.ejb.bmp;
import com.qms.setup.java.ZoneCodeMasterDOB;
import javax.ejb.EJBLocalObject;

public interface ZoneCodeMasterLocal extends EJBLocalObject 
{
   public void setZoneCodeMasterDOB(ZoneCodeMasterDOB dob);
   
   public ZoneCodeMasterDOB getZoneCodeMasterDOB();
}
/*
	Program Name	:QMSSetUpSessionBeanLocal.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode SessionBean LocalRemote
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.sls;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.setup.java.SalesPersonRegistrationDOB;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.ejb.EJBLocalObject;
import com.qms.setup.java.DensityGroupCodeDOB;
import com.qms.setup.java.DesignationDOB;

public interface QMSSetUpSessionLocal extends EJBLocalObject 
{
    public ArrayList getDensityGroupCodeDetails();  
    
    public double    getDensityGroupCodeDetails(int dgcCode,double perkg);
    
    public boolean insertDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) ;
    
    public boolean updateDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) ;
    
    public boolean deleteDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) ;    
    
    public ArrayList getPerKGIds(int dgcCode,String operation,String perKG,String terminalId);
    
    public boolean invalidateDensityGroupCodeDetails(ArrayList dgcList);
    
    public String designationRegistration(DesignationDOB desiDob,String invalidate);
    public ArrayList getDesignationIs(String searchString);
    public DesignationDOB getDesignationDetails(String	designationId) ;
    public boolean updateDesignationDetails(DesignationDOB desiDob);
     public boolean deleteDesignation(String designationId);
    public ArrayList getDesignationDetails();
     public boolean invalidateDesignation(ArrayList dobModList);
     
      public String insertSalesPersonDetails(SalesPersonRegistrationDOB sprDOB) ;
      public SalesPersonRegistrationDOB getSalePersonDetails(String salesPersonCode);
      public boolean removeSalesPersonDetails(String salesPersonCode);
      public boolean updateSalesPersonDetails(SalesPersonRegistrationDOB sprDOB);
      public ArrayList getSalesPersonDetails() ;
      public ArrayList getSalesPersonIds(String salesPersonCode,String terminalId,String operation);
     //  public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel) ;  
//      public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId) ;  //@@MOdified by kameswari for the issue
      public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId,String fromWhere) ;//@@ Modified by subrahmanyam  for the pbn id:220125 on 07-oct-10	
      public ArrayList getDesignationIds(String searchString,String terminalId,String operation);
      public boolean  invalidateSalesPersonDetails(ArrayList sprList);
      public ArrayList getRepOffIds(String salesPersonCode,String level);
      
       public boolean insertZoneCodeDetails(ZoneCodeMasterDOB dob) throws FoursoftException;
    
    //public ZoneCodeMasterDOB selectZoneCodeDetails(String location,String zipCode);
//    @@ COmmented & added by subrahmanyam for 216629 on 31-AUG-10
     //public boolean removeZoneCodeDetails(ZoneCodeMasterDOB dob);
     public String removeZoneCodeDetails(ZoneCodeMasterDOB dob);
    
    public boolean updateZoneCodeDetails(ZoneCodeMasterDOB dob) throws FoursoftException;
    
    public ArrayList viewAllZoneCodeDetails();
    
    public boolean invalidateZoneCodeDetails(ArrayList list)throws SQLException;
    
    public ArrayList getLocationIds(String searchString); 
    
    
    public HashMap uploadZoneCodeMasterDetails(ArrayList zoneCodeList,boolean addModFlag);
    
     //public ArrayList downloadZoneCodeMasterDetails(String locationIds)throws SQLException;
     public ArrayList getMultipleLocationIds(String controlStation);
}
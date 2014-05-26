/*
	ProgramName	:QMSSetUpSessionBean.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode SessionBean Remote
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.sls;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.qms.setup.java.DensityGroupCodeDOB;
import com.qms.setup.java.DesignationDOB;
import com.qms.setup.java.SalesPersonRegistrationDOB;
import com.qms.setup.java.ZoneCodeMasterDOB;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;


public interface QMSSetUpSession extends EJBObject 
{
    public ArrayList getDensityGroupCodeDetails()throws RemoteException;
    
    public double getDensityGroupCodeDetails(int dgcCode,double perKg)throws RemoteException;
    
    public boolean insertDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) throws RemoteException;
    
    public boolean updateDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) throws RemoteException;
    
    public boolean deleteDensityGroupCodeDetails(DensityGroupCodeDOB dgcDOB) throws RemoteException;
    
    public ArrayList getPerKGIds(int dgcCode,String operation,String perKG,String terminalId) throws RemoteException;
    
    public boolean invalidateDensityGroupCodeDetails(ArrayList dgcList) throws RemoteException;
    
    public String designationRegistration(DesignationDOB desiDob,String invalidate) throws java.rmi.RemoteException;
    public ArrayList getDesignationIs(String searchString) throws RemoteException;
    public DesignationDOB getDesignationDetails(String	designationId) throws RemoteException;
    public boolean updateDesignationDetails(DesignationDOB desiDob) throws RemoteException;
    public boolean deleteDesignation(String designationId) throws RemoteException;
    public ArrayList getDesignationDetails() throws RemoteException;
    public boolean invalidateDesignation(ArrayList dobModList) throws RemoteException;
    
        public String insertSalesPersonDetails(SalesPersonRegistrationDOB sprDOB) throws RemoteException;
      public SalesPersonRegistrationDOB getSalePersonDetails(String salesPersonCode) throws RemoteException;
      public boolean removeSalesPersonDetails(String salesPersonCode) throws RemoteException;
      public boolean updateSalesPersonDetails(SalesPersonRegistrationDOB sprDOB) throws RemoteException;
      public ArrayList getSalesPersonDetails() throws RemoteException;
      public ArrayList getSalesPersonIds(String salesPersonCode,String terminalId,String operation) throws RemoteException,EJBException;
     // public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel) throws RemoteException,EJBException;
//      public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId) throws RemoteException,EJBException;//@@Modified by kameswari for the issue
      public ArrayList getEmpIds(String salesPersonCode,String terminalId,String accessLevel,String empId,String fromWhere)throws RemoteException,EJBException; ;//@@ Modified by subrahmanyam  for the pbn id:220125 on 07-oct-10
      public ArrayList getDesignationIds(String searchString,String terminalId,String operation) throws RemoteException;
       public boolean  invalidateSalesPersonDetails(ArrayList sprList) throws RemoteException;
       public ArrayList getRepOffIds(String salesPersonCode,String level) throws RemoteException;
       
       
       
        public boolean insertZoneCodeDetails(ZoneCodeMasterDOB dob) throws RemoteException,EJBException,FoursoftException;
     
     public ZoneCodeMasterDOB selectZoneCodeDetails(String location,String zipCode,String shipmentMode,String consoleType,String operation)throws RemoteException,EJBException;
//   @@ Commentd & Added by subrahmanyam for 216629 on 31-AUG-10       
     //public boolean removeZoneCodeDetails(ZoneCodeMasterDOB dob)throws RemoteException,EJBException;
     public String removeZoneCodeDetails(ZoneCodeMasterDOB dob)throws RemoteException,EJBException;
     
     public boolean updateZoneCodeDetails(ZoneCodeMasterDOB dob)throws RemoteException,EJBException,FoursoftException;
     
     public ArrayList viewAllZoneCodeDetails() throws RemoteException,EJBException;
     
     public boolean invalidateZoneCodeDetails(ArrayList list) throws RemoteException,EJBException, SQLException;
     
     public ArrayList getLocationIds(String searchString) throws RemoteException;
     
     public HashMap uploadZoneCodeMasterDetails(ArrayList zoneCodeList,boolean addModFlag) throws RemoteException,EJBException;     
     public HashMap uploadCanadaZoneCodes(ArrayList zoneCodesList) throws RemoteException,EJBException;     
      public ArrayList downloadZoneCodeMasterDetails(String locationIds,String shipmentMode,String consoleType) throws RemoteException,EJBException,SQLException;
      public ArrayList downloadCanadaZones(ZoneCodeMasterDOB masterDOB) throws RemoteException,EJBException; 
      public ArrayList getMultipleLocationIds(String controlStation) throws RemoteException;
    public ArrayList getCustAddresses(
		String customerId,
		String addrType,
		String operation) throws RemoteException;
}
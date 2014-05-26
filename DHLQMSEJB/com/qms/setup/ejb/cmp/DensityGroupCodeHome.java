/*
	Program Name	:DensityGroupCodeHome.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode Entity Home
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.cmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import java.util.Collection;
import com.qms.setup.java.DensityGroupCodeDOB;

public interface DensityGroupCodeHome extends EJBHome 
{
    DensityGroupCode create() throws RemoteException, CreateException;
    
    DensityGroupCode create(int dgcCode,double perKG,double perLB,String uom,String invaliDate) throws RemoteException, CreateException;    
    
    DensityGroupCode findByPrimaryKey(DensityGroupCodePK primaryKey) throws RemoteException, FinderException;
    
    Collection findAll() throws RemoteException, FinderException;
}
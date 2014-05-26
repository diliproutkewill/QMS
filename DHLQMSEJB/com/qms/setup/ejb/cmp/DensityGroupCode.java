/*
	Program Name    : DensityGroupCode.java
	Module Name     : QMSSetup
	Task			      : DensityGroupCode Entity Remote
	Sub Task		    :
	Author Name		  : RamaKrishna Y
	Date Started	  : June 17,2001
	Date Completed  :
	Date Modified	  :
	Description		  :
*/

package com.qms.setup.ejb.cmp;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import javax.ejb.EJBObject;
import com.qms.setup.java.DensityGroupCodeDOB;


public interface DensityGroupCode extends EJBObject 
{

    public abstract void setDgcCode(int dgcCode)throws RemoteException,EJBException;
    
    public abstract void setPerKG(double perKG)throws RemoteException,EJBException;
    
    public  abstract void setPerLB(double perLB)throws RemoteException,EJBException;
    
    public abstract void setUom(String uom)throws RemoteException,EJBException;
    
    public abstract int getDgcCode()throws RemoteException,EJBException;
    
    public abstract double getPerKG()throws RemoteException,EJBException;
    
    public  abstract double getPerLB()throws RemoteException,EJBException;
    
    public abstract String getUom()throws RemoteException,EJBException;
    
    public  abstract void setInvaliDate(String invaliDate) throws RemoteException;
    
    public  abstract String getInvaliDate() throws RemoteException;
    
}



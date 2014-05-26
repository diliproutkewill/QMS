/*
	Program Name	:QMSSetUpSessionBeanHome.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode SessionBean Home
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface QMSSetUpSessionHome extends EJBHome 
{
    QMSSetUpSession create() throws RemoteException, CreateException;
}
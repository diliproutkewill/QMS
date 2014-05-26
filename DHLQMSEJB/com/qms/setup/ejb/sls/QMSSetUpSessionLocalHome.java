/*
	Program Name	:QMSSetUpSessionBeanLocalHome.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode SessionBean LocalHome
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.sls;

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface QMSSetUpSessionLocalHome extends EJBLocalHome 
{
    QMSSetUpSessionLocal create() throws CreateException;
}
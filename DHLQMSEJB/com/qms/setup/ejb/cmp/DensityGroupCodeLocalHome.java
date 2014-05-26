/*
	Program Name	:DensityGroupCodeLocalHome.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode Entity LocalHome
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.cmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import java.util.Collection;

public interface DensityGroupCodeLocalHome extends EJBLocalHome 
{
    DensityGroupCodeLocal create() throws  CreateException;
    
    DensityGroupCodeLocal create(int dgcCode,double perKG,double perLB,String uom,String invaliDate) throws  CreateException;    
    
    DensityGroupCodeLocal findByPrimaryKey(DensityGroupCodePK primaryKey) throws  FinderException;
    
    Collection findAll() throws  FinderException;
    
}
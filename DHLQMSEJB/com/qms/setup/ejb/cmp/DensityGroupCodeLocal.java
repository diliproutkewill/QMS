/*
	Program Name	:DensityGroupCodeLocal.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode EntityBean Local Remote
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.cmp;

import java.util.ArrayList;
import javax.ejb.EJBLocalObject;
import com.qms.setup.java.DensityGroupCodeDOB;

public interface DensityGroupCodeLocal extends EJBLocalObject 
{
    public abstract void setDgcCode(int dgcCode);
    
    public abstract void setPerKG(double perKG);
    
    public  abstract void setPerLB(double perLB);
    
    public abstract void setUom(String uom);
    
    public abstract int getDgcCode();
    
    public abstract double getPerKG();
    
    public  abstract double getPerLB();
    
    public abstract String getUom();
    
    public  abstract void setInvaliDate(String invaliDate) ;
    
    public  abstract String getInvaliDate() ;
    
}
/*
	Program Name	:DensityGroupCodeDOB.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode DOB
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.java;
import java.io.Serializable;

public class DensityGroupCodeDOB implements java.io.Serializable
{
    private int    dgcCode      = 0;
    private double perKG        = 0.0;
    private double perLB        = 0.0;
    private String uom          = null;
    private String invalidate   = null;   
    
    
    public void setDGCCode(int dgcCode)
    {
      this.dgcCode  =  dgcCode;
    }
    
    public int getDGCCode()
    {
      return this.dgcCode;
    }
    
    public void setPerKG(double perKG)
    {
      this.perKG  =  perKG;
    }
    
    public double getPerKG()
    {
      return this.perKG;
    }
    
    public void setPerLB(double perLB)
    {
      this.perLB  =  perLB;
    }
    
    public double getPerLB()
    {
      return this.perLB;
    }
    
    public void setUOM(String uom)
    {
      this.uom  =  uom;
    }
    
    public String getUOM()
    {
      return this.uom;
    }
    
    public void setInvalidate(String invalidate)
    {
      this.invalidate  =  invalidate;
    }
    
    public String getInvalidate()
    {
      return this.invalidate;
    }
    
    public DensityGroupCodeDOB()
    {
    }
    
}
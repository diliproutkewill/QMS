/*
	Program Name	:DensityGroupCodePK.java
	Module Name		:QMSSetup
	Task			    :DensityGroupCode Entity PrimaryKey
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/

package com.qms.setup.ejb.cmp;
import java.io.Serializable;

public class DensityGroupCodePK implements Serializable 
{
   //composite Key on these 3 fields
    public  int       dgcCode       =      0;
    public  double    perKG         =    0.0;
    public  String    uom           =    null;
    
    public DensityGroupCodePK()
    {
    }

    public boolean equals(Object other)
    {
        if (other instanceof DensityGroupCodePK)
        {
            final DensityGroupCodePK otherDensityGroupCodePK = (DensityGroupCodePK)other;

            // The following assignment statement is auto-maintained and may be overwritten.
            boolean areEqual = true;

            return areEqual;
        }

        return false;
    }

    public int hashCode()
    {
        // Add custom hashCode() impl here
        return super.hashCode();
    }
}
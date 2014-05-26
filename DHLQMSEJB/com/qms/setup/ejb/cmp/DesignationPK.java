package com.qms.setup.ejb.cmp;
import java.io.Serializable;

public class DesignationPK implements Serializable 
{
    public		java.lang.String	designationId;
    
    public DesignationPK()
    {
    }
    public java.lang.String getDesignationId()
    {
      return designationId;
    }
    
    public void setDesignationId(java.lang.String id){
      designationId = id;
    }
    public boolean equals(Object other)
    {
        if (other instanceof DesignationPK)
        {
            final DesignationPK otherDesignationPK = (DesignationPK)other;

            // The following assignment statement is auto-maintained and may be overwritten.
            boolean areEqual = true;

            return areEqual;
        }

        return false;
    }

    public int hashCode()
    {
        try 
		{
			long	crcKey = -1;
			java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
			java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
			java.util.zip.Adler32 adl32 = new java.util.zip.Adler32();
			adl32.update(bos.toByteArray());
			crcKey = adl32.getValue();
			return (int)(crcKey ^ (crcKey >> 32));
		} 
		catch (java.io.IOException ioEx) {
			return -1;
		}
    }
}
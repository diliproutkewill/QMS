package com.qms.setup.chargebasis.ejb.bmp;
import java.io.Serializable;

public class ChargeBasisMasterEntityBeanPK implements Serializable 
{
  public String chargeBasis;
  public ChargeBasisMasterEntityBeanPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof ChargeBasisMasterEntityBeanPK)
    {
      final ChargeBasisMasterEntityBeanPK otherChargeBasisMasterEntityBeanPK = (ChargeBasisMasterEntityBeanPK)other;

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
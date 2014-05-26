package com.qms.setup.ejb.bmp;
import java.io.Serializable;

public class ListMasterEntityBeanPK implements Serializable 
{
  public java.lang.String shipmentMode;
  public java.lang.String listType;
  public ListMasterEntityBeanPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof ListMasterEntityBeanPK)
    {
      final ListMasterEntityBeanPK otherListMasterEntityBeanPK = (ListMasterEntityBeanPK)other;

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
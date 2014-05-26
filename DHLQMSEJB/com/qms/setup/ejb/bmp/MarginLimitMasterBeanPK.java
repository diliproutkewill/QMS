package com.qms.setup.ejb.bmp;
import java.io.Serializable;

public class MarginLimitMasterBeanPK implements Serializable 
{
  public java.lang.String levelId;
  public java.lang.String marginId;
  public java.lang.String sLevelId;
  public MarginLimitMasterBeanPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof MarginLimitMasterBeanPK)
    {
      final MarginLimitMasterBeanPK otherMarginLimitMasterBeanPK = (MarginLimitMasterBeanPK)other;

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
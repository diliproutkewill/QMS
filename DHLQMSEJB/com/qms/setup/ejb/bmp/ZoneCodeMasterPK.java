package com.qms.setup.ejb.bmp;
import java.io.Serializable;

public class ZoneCodeMasterPK implements Serializable 
{
  public String zoneCode     =  null;
  public ZoneCodeMasterPK()
  {
  }

  public boolean equals(Object other)
  {
    if (other instanceof ZoneCodeMasterPK)
    {
      final ZoneCodeMasterPK otherZoneCodeMasterPK = (ZoneCodeMasterPK)other;

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
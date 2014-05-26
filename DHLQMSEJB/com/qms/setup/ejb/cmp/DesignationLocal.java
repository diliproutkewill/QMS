package com.qms.setup.ejb.cmp;
import javax.ejb.EJBLocalObject;

public interface DesignationLocal extends EJBLocalObject 
{
	public void setDesignationId(String designationId);
	public void setDescription(String description);
	public void setLevelNo(String levelNo);
	public void setInvalidate(String invalidate);
	public String getDesignationId();
	public String getDescription();
	public String getLevelNo();
	public String getInvalidate();
}
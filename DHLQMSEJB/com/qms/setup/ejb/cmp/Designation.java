package com.qms.setup.ejb.cmp;
import javax.ejb.EJBObject;

public interface Designation extends EJBObject 
{
	public void setDesignationId(String designationId) throws java.rmi.RemoteException;
	public void setDescription(String description) throws java.rmi.RemoteException;
	public void setLevelNo(String levelNo) throws java.rmi.RemoteException;
	public void setInvalidate(String invalidate) throws java.rmi.RemoteException;
	public String getDesignationId() throws java.rmi.RemoteException;
	public String getDescription() throws java.rmi.RemoteException;
	public String getLevelNo() throws java.rmi.RemoteException;
	public String getInvalidate() throws java.rmi.RemoteException;
}
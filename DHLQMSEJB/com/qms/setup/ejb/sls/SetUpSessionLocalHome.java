package com.qms.setup.ejb.sls;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface SetUpSessionLocalHome extends EJBLocalHome 
{
  com.qms.setup.ejb.sls.SetUpSessionLocal create() throws CreateException;
}
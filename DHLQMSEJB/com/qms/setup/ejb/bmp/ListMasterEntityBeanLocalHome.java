package com.qms.setup.ejb.bmp;
import java.util.ArrayList;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface ListMasterEntityBeanLocalHome extends EJBLocalHome 
{
  ListMasterEntityBeanLocal create(ArrayList param) throws CreateException;

  ListMasterEntityBeanLocal findByPrimaryKey(ListMasterEntityBeanPK primaryKey) throws FinderException;
}
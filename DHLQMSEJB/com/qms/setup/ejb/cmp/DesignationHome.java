package com.qms.setup.ejb.cmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import java.util.Collection;

public interface DesignationHome extends EJBHome 
{
    Designation create(String destignationId,String description,String levelNo,String invalidate) throws RemoteException, CreateException;
   Designation create() throws RemoteException, CreateException;
    Designation findByPrimaryKey(DesignationPK primaryKey) throws RemoteException, FinderException;

    //Collection findAll() throws RemoteException, FinderException;
}
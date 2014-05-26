package com.qms.operations.rates.ejb.bmp;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface BuyRatesEntityHome extends EJBHome 
{
    BuyRatesEntity create() throws RemoteException, CreateException;

    BuyRatesEntity findByPrimaryKey(Long primaryKey) throws RemoteException, FinderException;
}
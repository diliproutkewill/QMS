package com.qms.operations.rates.ejb.sls;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface BuyRatesSessionHome extends EJBHome 
{
    BuyRatesSession create() throws RemoteException, CreateException;
}
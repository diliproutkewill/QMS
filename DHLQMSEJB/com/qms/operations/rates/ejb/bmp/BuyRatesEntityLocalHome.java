package com.qms.operations.rates.ejb.bmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;

public interface BuyRatesEntityLocalHome extends EJBLocalHome 
{
    BuyRatesEntityLocal create() throws CreateException;

    BuyRatesEntityLocal findByPrimaryKey(Long primaryKey) throws FinderException;
}
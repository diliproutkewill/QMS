package com.qms.operations.rates.ejb.bmp;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public class BuyRatesEntityBean implements EntityBean 
{
    private EntityContext context;

    public Long ejbCreate()
    {
        return null;
    }

    public void ejbPostCreate()
    {
    }

    public Long ejbFindByPrimaryKey(Long primaryKey)
    {
        return primaryKey;
    }

    public void ejbActivate()
    {
    }

    public void ejbLoad()
    {
    }

    public void ejbPassivate()
    {
    }

    public void ejbRemove()
    {
    }

    public void ejbStore()
    {
    }

    public void setEntityContext(EntityContext ctx)
    {
        this.context = ctx;
    }

    public void unsetEntityContext()
    {
        this.context = null;
    }
}
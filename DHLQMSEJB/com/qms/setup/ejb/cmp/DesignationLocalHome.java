package com.qms.setup.ejb.cmp;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import java.util.Collection;

public interface DesignationLocalHome extends EJBLocalHome 
{
    DesignationLocal create() throws CreateException;

    DesignationLocal findByPrimaryKey(DesignationPK primaryKey) throws FinderException;
     DesignationLocal create(String destignationId,String description,String levelNo,String invalidate) throws  CreateException;
    //Collection findAll() throws FinderException;
}
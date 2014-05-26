/*
	Program Name	:SalesPersonRegistrationBean.java
	Module Name		:QMSSetup
	Task			    :SalesPersonRegistration EntityBean
	Sub Task		  :
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed:
	Date Modified	:
	Description		:
*/


package com.qms.setup.ejb.bmp;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import com.qms.setup.dao.SalesPersonRegistrationDAO;
import com.qms.setup.java.SalesPersonRegistrationDOB;

public class SalesPersonRegistrationBean implements EntityBean 
{
  private EntityContext        context  ;
  SalesPersonRegistrationDOB   dob       =  null;
  SalesPersonRegistrationDAO   dao       =  null;
  private static final String FILE_NAME  =  "SalesPersonRegistrationBean.java";
  public boolean              flag       =  false;
  private static Logger logger = null;
  
  public SalesPersonRegistrationBean()
  {
    logger  = Logger.getLogger(SalesPersonRegistrationBean.class);
  }
  
  public SalesPersonRegistrationPK ejbCreate(SalesPersonRegistrationDOB   dob)
  { 
     //Logger.info(FILE_NAME,"Inside ejbCreate()");
     logger.info(FILE_NAME+"Inside ejbCreate()");
     SalesPersonRegistrationDAO   sprDAO    =  null;
     SalesPersonRegistrationPK    pk        =  null;
    try
    {
            
            sprDAO        =    new SalesPersonRegistrationDAO();    
            sprDAO.create(dob);
            pk            =    new SalesPersonRegistrationPK();
            pk.salesPersonCode    =   dob.getSalesPersonCode();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return pk;
  }

  public void ejbPostCreate(SalesPersonRegistrationDOB   dob)
  {
      //Logger.info(FILE_NAME,"Inside ejbPostCreate()");
      logger.info(FILE_NAME+"Inside ejbPostCreate()");
  }
   public SalesPersonRegistrationPK ejbCreate()throws EJBException
  { 
      //Logger.info(FILE_NAME,"Inside ejbCreate()");
      logger.info(FILE_NAME+"Inside ejbCreate()");
    return null;
  }

  public void ejbPostCreate()throws EJBException
  {
      //Logger.info(FILE_NAME,"Inside ejbPostCreate()");
      logger.info(FILE_NAME+"Inside ejbPostCreate()");
  }

  public SalesPersonRegistrationPK ejbFindByPrimaryKey(SalesPersonRegistrationPK primaryKey)
  {   
      //Logger.info(FILE_NAME,"Inside ejbFindByPrimaryKey()");
      logger.info(FILE_NAME+"Inside ejbFindByPrimaryKey()");
      SalesPersonRegistrationDAO   sprDAO    =  null;
      
    try
    {
      sprDAO        =    new SalesPersonRegistrationDAO();            
      sprDAO.findByPrimaryKey(primaryKey.salesPersonCode);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return primaryKey;
  }

  public void ejbActivate()
  { 
     //Logger.info(FILE_NAME,"Inside ejbActivate()");
     logger.info(FILE_NAME+"Inside ejbActivate()");
  }

  public void ejbLoad()
  {  
  
       //Logger.info(FILE_NAME,"Inside ejbLoad()");
       logger.info(FILE_NAME+"Inside ejbLoad()");
       SalesPersonRegistrationDAO   sprDAO    =  null;
       SalesPersonRegistrationPK    pk        =  null;
       SalesPersonRegistrationDOB   dob       =  null;
      try
      {
        flag          =    false;
        sprDAO        =    new SalesPersonRegistrationDAO();   
        pk            =    (SalesPersonRegistrationPK)context.getPrimaryKey();
        this.dob      =    sprDAO.load(pk.salesPersonCode);
        
      }
      catch(Exception e)
      {
        e.printStackTrace();
      }
      
  }

  public void ejbPassivate()
  { 
     //Logger.info(FILE_NAME,"Inside ejbPassivate()");
     logger.info(FILE_NAME+"Inside ejbPassivate()");
  }

  public void ejbRemove()
  { 
  
     //Logger.info(FILE_NAME,"Inside ejbRemove()");
     logger.info(FILE_NAME+"Inside ejbRemove()");
     SalesPersonRegistrationDAO   sprDAO    =  null;
     SalesPersonRegistrationPK    pk        =  null;    
    try
    {
      pk            =    (SalesPersonRegistrationPK)context.getPrimaryKey();      
      sprDAO        =  new SalesPersonRegistrationDAO();      
      sprDAO.remove(pk.salesPersonCode);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    finally
    {
      pk     =   null;
      sprDAO =   null;
    }
  }

  public void ejbStore()
  {   
      //Logger.info(FILE_NAME,"Inside ejbStore()");
      logger.info(FILE_NAME+"Inside ejbStore()");
      SalesPersonRegistrationDAO   sprDAO    =  null;
      
      try
			{
       if(flag)
       {
				  sprDAO	= new SalesPersonRegistrationDAO();				
					sprDAO.store(dob);
          flag = false;
       }
			}
      catch(Exception e)
      {
        e.printStackTrace();
      }
  }
//Serialization of BeanObject is to be done here.....//
  public void setEntityContext(EntityContext ctx)
  { 
     //Logger.info(FILE_NAME,"Inside setEntityContext()");
     logger.info(FILE_NAME+"Inside setEntityContext()");
     this.context = ctx;
  }

  public void unsetEntityContext()
  { 
    //Logger.info(FILE_NAME,"Inside unsetEntityContext()");
    logger.info(FILE_NAME+"Inside unsetEntityContext()");
    this.context = null;
  }
  
  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
	{ 
    //Logger.info(FILE_NAME,"Inside writeObject()");
    logger.info(FILE_NAME+"Inside writeObject()");
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
	{ 
    //Logger.info(FILE_NAME,"Inside readObject()");
    logger.info(FILE_NAME+"Inside readObject()");
		in.defaultReadObject();
	}
  
  public void setSalesPersonRegistrationDOB(SalesPersonRegistrationDOB dob)
  { 
     //Logger.info(FILE_NAME,"Inside setSalesPersonRegistrationDOB()");
     logger.info(FILE_NAME+"Inside setSalesPersonRegistrationDOB()");
    flag   =  true;  
     this.dob  = dob ;
  }
  public SalesPersonRegistrationDOB getSalesPersonRegistrationDOB()
  { 
    //Logger.info(FILE_NAME,"Inside getSalesPersonRegistrationDOB()");
    logger.info(FILE_NAME+"Inside getSalesPersonRegistrationDOB()");
    return this.dob;
  }
}
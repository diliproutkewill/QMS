package com.qms.setup.ejb.bmp;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import com.qms.setup.java.IndustryRegDOB;
import com.qms.setup.dao.IndustryRegDAO;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public class IndustryRegEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="IndustryRegEntityBean.java";
  private IndustryRegEntityPK   pkObj     =null;
  private IndustryRegDAO    industryRegDAO=null;
  private IndustryRegDOB    industryRegDOB=null;
  boolean isDirty                         =false;
  private static Logger logger = null;
  
  public IndustryRegEntityBean()
  {
    logger  = Logger.getLogger(IndustryRegEntityBean.class);
  }
  
   /**
   * 
   * @param industryRegDOB
   */ 
  public void setIndustryRegDOB(IndustryRegDOB industryRegDOB)
  {
    this.industryRegDOB = industryRegDOB;
    this.isDirty        = true;
  }
   /**
   * 
   * @return IndustryRegDOB
   */ 
  public IndustryRegDOB getIndustryRegDOB()
  {
    return this.industryRegDOB;
  }

   /**
   * 
   * @param industryRegDOB
   * @return IndustryRegEntityPK
   */ 
  public IndustryRegEntityPK ejbCreate(IndustryRegDOB industryRegDOB)
  {
    try
    {
      industryRegDAO      = new IndustryRegDAO();
      industryRegDAO.create(industryRegDOB);
      this.pkObj				  = new IndustryRegEntityPK();
      this.pkObj.industry = industryRegDOB.getIndustry();
      this.industryRegDOB = industryRegDOB;
    }catch(Exception e)
    {
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ", e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+ e);
			throw new javax.ejb.EJBException(e.toString());
    }finally
    {
      industryRegDAO=null;
      industryRegDOB=null;
    }
    return this.pkObj;
  }
  /**
   * 
   * @param industryRegDOB
   */
  public void ejbPostCreate(IndustryRegDOB industryRegDOB)
  {
  }

   /**
   * @throws javax.ejb.FinderException
   * @param pkObj
   * @return IndustryRegEntityPK
   */ 
  public IndustryRegEntityPK ejbFindByPrimaryKey(IndustryRegEntityPK pkObj)throws FinderException,ObjectNotFoundException
  {
    boolean industryRow   = false;
    industryRegDAO =  new IndustryRegDAO();
    try
    {
      this.pkObj  = industryRegDAO.findByPrimariKey(pkObj);
      industryRow = true;
    }catch(ObjectNotFoundException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
    }catch(Exception e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
    }finally
    {
      industryRegDAO  = null;
    }
    if(industryRow)
    {
      return pkObj;
    }else
    {
      throw new ObjectNotFoundException("Could not found the bean");
    }
    
  }
  
  /**
   * 
   */
  public void ejbActivate()
  {
  }
  
  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbLoad()throws EJBException
  {
    industryRegDAO =  new IndustryRegDAO();
    try
    {
      this.pkObj  = (IndustryRegEntityPK)context.getPrimaryKey();
      this.industryRegDOB = industryRegDAO.load(pkObj);
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"Exception in ejbLoad() method"+e.toString());
        logger.error(FILE_NAME+"Exception in ejbLoad() method"+e.toString());
        throw new EJBException();      
    }finally
    {
     industryRegDAO =  null;
    }
  }

  /**
   * 
   */
  public void ejbPassivate()
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbRemove()throws EJBException
  {
     industryRegDAO =  new IndustryRegDAO();
     try
     {
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the industryRegDOB with Id  "+pkObj);
			industryRegDAO.remove(this.pkObj);       
     }catch(Exception e)
     {
        //Logger.error(FILE_NAME,"Exception in ejbRemove() method"+e.toString());
        logger.error(FILE_NAME+"Exception in ejbRemove() method"+e.toString());
        throw new EJBException();        
     }finally
      {
         industryRegDAO =  null;
      }
    
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbStore()throws EJBException
  {
    industryRegDAO =  new IndustryRegDAO();
    try
    {
      if(isDirty)
      {
        industryRegDAO.store(this.industryRegDOB);
      }
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"Exception in ejbStore() method"+e.toString());
        logger.error(FILE_NAME+"Exception in ejbStore() method"+e.toString());
        throw new EJBException();       
    }finally
    {
      industryRegDAO  = null;
    }
  }
  
 /* public void invalidateIndustryDtls(java.util.ArrayList industryList)throws EJBException
  {
     industryRegDAO =  new IndustryRegDAO();
     try
     {
       industryRegDAO.invalidateIndustryId(industryList);
     }catch(java.sql.SQLException e)
     {
        Logger.error(FILE_NAME,"SQLException in invalidateIndustryDtls(ArrayList param0) method"+e.toString());
        throw new EJBException();           
     }catch(Exception e)
     {
        Logger.error(FILE_NAME,"Exception in invalidateIndustryDtls(ArrayList param0) method"+e.toString());
        throw new EJBException();           
     }
  }*/

  /**
   * 
   * @param ctx
   * @throws javax.ejb.EJBException
   * @throws java.rmi.RemoteException
   */
  public void setEntityContext(EntityContext ctx)throws javax.ejb.EJBException, java.rmi.RemoteException
  {
    this.context = ctx;
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.rmi.RemoteException
   */
  public void unsetEntityContext()throws javax.ejb.EJBException, java.rmi.RemoteException
  {
    this.context = null;
  }
}
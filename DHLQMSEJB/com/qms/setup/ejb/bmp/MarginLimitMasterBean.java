package com.qms.setup.ejb.bmp;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import com.qms.setup.ejb.bmp.MarginLimitMasterBeanPK;
import com.qms.setup.dao.MarginLimitMasterDAO;
import com.qms.setup.java.MarginLimitMasterDOB;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;

public class MarginLimitMasterBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="MarginLimitMasterBean.java";
  private MarginLimitMasterDAO  marginLimitMasterDAO=null;
  private MarginLimitMasterBeanPK pkObj =null;
  private MarginLimitMasterDOB  marginLimitMasterDOB=null;
  private boolean isValid   = false;
  private static Logger logger = null;
  
  public MarginLimitMasterBean()
  {
    logger  = Logger.getLogger(MarginLimitMasterBean.class);
  }
  
  /**
   * 
   * @param marginLimitMasterDOB
   */
  public void setMarginLimitDOB(MarginLimitMasterDOB marginLimitMasterDOB)
  {
    this.marginLimitMasterDOB = marginLimitMasterDOB;
    isValid = true;
  }
  
  /**
   * 
   * @return 
   */
  public MarginLimitMasterDOB getMarginLimitDOB()
  {
    return this.marginLimitMasterDOB;
  }
  
  /**
   * 
   * @return 
   * @param dataList
   */
  public MarginLimitMasterBeanPK ejbCreate(java.util.ArrayList dataList)
  {
    try
    {
      marginLimitMasterDAO.create(dataList);
      marginLimitMasterDOB  = (MarginLimitMasterDOB)dataList.get(0);
      this.pkObj				  = new MarginLimitMasterBeanPK();
      this.pkObj.levelId  = marginLimitMasterDOB.getLevelId();
      this.pkObj.marginId = marginLimitMasterDOB.getMarginId();
      this.pkObj.sLevelId  = marginLimitMasterDOB.getServiceLevel();
    }catch(Exception e)
    {
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ",e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+e);
			throw new javax.ejb.EJBException(e);      
    }finally
    {
      marginLimitMasterDOB  = null;
    }
    return this.pkObj;
  }

  /**
   * 
   * @param dataList
   */
  public void ejbPostCreate(java.util.ArrayList dataList)
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param primaryKey
   */
  public MarginLimitMasterBeanPK ejbFindByPrimaryKey(MarginLimitMasterBeanPK primaryKey)throws ObjectNotFoundException,EJBException
  {
    boolean marginLimitRows = false;
    try
    {
      marginLimitMasterDAO.findByPrimariKey(primaryKey);
      marginLimitRows = true;
    }catch(ObjectNotFoundException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new ObjectNotFoundException();
    }catch(SQLException e)
    {
       //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);  
       logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);  
    }catch(Exception e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);   
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);   
    }
    if(marginLimitRows)
    {
      return primaryKey;  
    }else
    {
      throw new ObjectNotFoundException("Could Not Found Bean");
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
   */
  public void ejbLoad()
  {
    try
    {
      this.pkObj  = (MarginLimitMasterBeanPK)context.getPrimaryKey();    
      this.marginLimitMasterDOB=marginLimitMasterDAO.load(this.pkObj);
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"Exception in ejbLoad() method"+e.toString());
        logger.error(FILE_NAME+"Exception in ejbLoad() method"+e.toString());
        throw new EJBException();      
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
    	try
		{
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the marginLimitMasterDOB with Id  "+pkObj);
			marginLimitMasterDAO.remove(pkObj);
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ejbRemove()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbRemove()"+"Could not Find Bean "+e);
			throw new javax.ejb.EJBException(e.toString());
		}
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   */
  public void ejbStore()throws EJBException
  {
		try 
		{
	
			if(isValid)
			{
				isValid=false;
				marginLimitMasterDAO.store(this.pkObj,this.marginLimitMasterDOB);
				
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME," ejbStore()	","Exception while Storing  the Model ", e);
      logger.error(FILE_NAME+" ejbStore()	"+"Exception while Storing  the Model "+e);
			throw new javax.ejb.EJBException(e.toString());
		}  
  }

  /**
   * 
   * @param ctx
   */
  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
    marginLimitMasterDAO =  new   MarginLimitMasterDAO();
  }

  /**
   * 
   */
  public void unsetEntityContext()
  {
    this.context = null;
  }
}
package com.qms.operations.sellrates.ejb.bmp;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.sellrates.dao.QMSSellRatesDAO;
import com.qms.operations.sellrates.java.QMSSellRatesDOB;
import java.util.ArrayList;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

public class QMSSellRatesEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME         =  "QMSSellRatesEntityBean.java";
  private QMSSellRatesEntityPK   pkObj          =null;
  private QMSSellRatesDAO        sellRatesDAO   =null;
  private QMSSellRatesDOB        sellRatesDOB   =null;
  private java.util.ArrayList    sellRateDetails=null;
  boolean isValid                               =false;
  private static Logger logger = null;
  
  public QMSSellRatesEntityBean()
  {
    logger  = Logger.getLogger(QMSSellRatesEntityBean.class);
  }
  /**
   *@param  QMSSellRatesDOB
   */
  public void setQMSSellRates(QMSSellRatesDOB rateDetails)
  {
    this.sellRatesDOB   = rateDetails;
    this.isValid        = true;
  }
  public java.util.ArrayList getQMSSellRates()
  {
    return this.sellRateDetails;
  }
   /**
   * 
   * @return QMSSellRatesDOB
   * 
   */
  
  public QMSSellRatesEntityPK ejbCreate(QMSSellRatesDOB sellRatesDob,ArrayList list,ESupplyGlobalParameters logIn,String operation)
  {
  
      QMSSellRatesDAO         sellRatesDao   =   null;
      try
      {
            
            sellRatesDao          =   new QMSSellRatesDAO();  
            logger.info("in dao");
            sellRatesDao.create(sellRatesDob,list,logIn,operation);
           
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in ejbCreate()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in ejbCreate()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException("SQLEXception in ejbCreate() :"+sqle.toString());
      }
      catch(Exception e)
      {
        e.printStackTrace();
         throw new EJBException(e);
      }
    return new QMSSellRatesEntityPK();
  }
  public QMSSellRatesEntityPK ejbCreate(ArrayList sellRatesList,ArrayList list,ESupplyGlobalParameters logIn,String operation)
  {
  
      QMSSellRatesDAO         sellRatesDao   =   null;
      try
      {
            
            sellRatesDao          =   new QMSSellRatesDAO();    
            sellRatesDao.create(sellRatesList,list,logIn,operation);
            //Logger.info(FILE_NAME,"ejbStore............... in entity bean");
            logger.info(FILE_NAME+"ejbStore............... in entity bean");
      }
      catch(SQLException sqle)
      {
          //Logger.error(FILE_NAME,"SQLEXception in ejbCreate()-->"+sqle.toString());
          logger.error(FILE_NAME+"SQLEXception in ejbCreate()-->"+sqle.toString());
          sqle.printStackTrace();
          throw new EJBException("SQLEXception in ejbCreate() :"+sqle.toString());
      }
      catch(Exception e)
      {
        e.printStackTrace();
         throw new EJBException(e);
      }
    return new QMSSellRatesEntityPK();
  }
  
  public void ejbPostCreate(QMSSellRatesDOB sellRatesDob,ArrayList list,ESupplyGlobalParameters logIn,String operation)
  {
  }
  public void ejbPostCreate(ArrayList sellRatesDob,ArrayList list,ESupplyGlobalParameters logIn,String operation)
  {
  }  
  public QMSSellRatesEntityPK ejbCreate(ArrayList sellRateDetails)
  {
    try
    {
        //Logger.info(FILE_NAME,"inside ejbCreate()");
        logger.info(FILE_NAME+"inside ejbCreate()");
        sellRatesDAO = new QMSSellRatesDAO();
        sellRatesDAO.create(sellRateDetails);
        pkObj = new QMSSellRatesEntityPK();
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error while inserting records..",e);
      logger.error(FILE_NAME+"Error while inserting records..",e);
      e.printStackTrace();
      throw new EJBException(e);
    }
    return pkObj;
  }
  public void ejbPostCreate(java.util.ArrayList sellRateDetails)
  {
    //Logger.info(FILE_NAME,"Inside ejbPostCreate()");
    logger.info(FILE_NAME+"Inside ejbPostCreate()");
  }
  public QMSSellRatesEntityPK ejbCreate()
  {
    return null;
  }

  public void ejbPostCreate()
  {
  }

  public QMSSellRatesEntityPK ejbFindByPrimaryKey(QMSSellRatesEntityPK primaryKey)
  {
    //Logger.info(FILE_NAME,"ejbFindByPrimaryKey::");
    logger.info(FILE_NAME+"ejbFindByPrimaryKey::");
    return primaryKey;
  }

  public void ejbActivate()
  {
    //Logger.info(FILE_NAME,"ejbActivate....");
    logger.info(FILE_NAME+"ejbActivate....");
  }

  public void ejbLoad()
  {
    try
    {
      //Logger.info(FILE_NAME,"ejbLoad....");
      logger.info(FILE_NAME+"ejbLoad....");
      this.pkObj = (QMSSellRatesEntityPK)context.getPrimaryKey();
      sellRatesDAO = new QMSSellRatesDAO();
      this.sellRateDetails = sellRatesDAO.load(pkObj.getRateDetails());
    }
    catch(Exception e)
    {
      //Logger.error(FILE_NAME,"Error in the search process"+e.toString());
      logger.error(FILE_NAME+"Error in the search process"+e.toString());
      e.printStackTrace();
      throw new EJBException(e);
    }
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
    QMSSellRatesDAO           sellRatesDao    = null;
  }

  public void ejbStore()
  {
    if(isValid)
    {
      //Logger.info(FILE_NAME,"ejbStore....");
      logger.info(FILE_NAME+"ejbStore....");
    }
  }

  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
    //Logger.info(FILE_NAME,"setEntityContext....");
    logger.info(FILE_NAME+"setEntityContext....");
  }

  public void unsetEntityContext()
  {
    this.context = null;
  }
}
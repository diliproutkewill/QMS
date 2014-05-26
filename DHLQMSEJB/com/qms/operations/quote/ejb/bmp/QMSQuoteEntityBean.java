package com.qms.operations.quote.ejb.bmp;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.operations.quote.dao.QMSQuoteDAO;
import com.qms.operations.quote.dob.QuoteFinalDOB;
import com.qms.operations.quote.dob.QuoteMasterDOB;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.ObjectNotFoundException;

public class QMSQuoteEntityBean implements EntityBean 
{
  private EntityContext     context;
  private QMSQuoteDAO       quoteDAO  = null;
  private QMSQuoteEntityPK  pkObj     = null;
  private static final String FILE_NAME		= "QMSQuoteEntityBean.java";
  private static Logger logger = null;

  public QMSQuoteEntityBean()
  {
    logger  = Logger.getLogger(QMSQuoteEntityBean.class);
  }
  public QMSQuoteEntityPK ejbCreate(QuoteFinalDOB finalDOB)
  {
    QuoteMasterDOB  masterDOB = null;
    try
    {
      quoteDAO  = new QMSQuoteDAO();
      quoteDAO.create(finalDOB);
      pkObj = new QMSQuoteEntityPK();
      masterDOB = (QuoteMasterDOB)finalDOB.getMasterDOB();
      pkObj.quoteId = masterDOB.getUniqueId();
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Finally : QMSQuoteEntityBean[ejbCreate(masterDOB)]-> "+e.toString());
      logger.error(FILE_NAME+"Finally : QMSQuoteEntityBean[ejbCreate(masterDOB)]-> "+e.toString());
      throw new EJBException(e.toString());
    }
    return pkObj;
  }

    
  public void ejbPostCreate(QuoteFinalDOB finalDOB)
  {
  }

  public QMSQuoteEntityPK ejbFindByPrimaryKey(QMSQuoteEntityPK primaryKey)
  {
    try
    {
      quoteDAO  = new QMSQuoteDAO();
      quoteDAO.findByPrimaryKey(primaryKey.quoteId);
    }
    catch(Exception e)
    {
      e.printStackTrace();
      //Logger.error(FILE_NAME,"Finally : QMSQuoteEntityBean[ejbFindByPrimaryKey(primaryKey)]-> "+e.toString());
      logger.error(FILE_NAME+"Finally : QMSQuoteEntityBean[ejbFindByPrimaryKey(primaryKey)]-> "+e.toString());
      throw new EJBException(e.toString());
    }
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
    quoteDAO=null;
    pkObj=null;
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
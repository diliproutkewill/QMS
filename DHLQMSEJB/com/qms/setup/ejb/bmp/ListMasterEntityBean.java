package com.qms.setup.ejb.bmp;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.qms.setup.dao.ListMasterDAO;
import com.qms.setup.java.ListMasterDOB;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.ObjectNotFoundException;

public class ListMasterEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME="ListMasterEntityBean.java";
  private ListMasterEntityBeanPK pkObj=null;
  private ListMasterDOB listmasterDOB=null;
  private ListMasterDAO listmasterDAO=null;
  private boolean isValid=false;
  private static Logger logger = null;
  
  public ListMasterEntityBean()
  {
    logger  = Logger.getLogger(ListMasterEntityBean.class);
  }
      
  public void setListMasterDOB(ListMasterDOB listmasterDOB)
  {
    this.listmasterDOB=listmasterDOB;
	isValid = true;
  }
  public ListMasterDOB getListeMasterDOB()
  {
    return listmasterDOB;
  }

  public ListMasterEntityBeanPK ejbCreate(ArrayList dataList)
  {
  try
  {
  listmasterDAO.create(dataList);
    listmasterDOB=(ListMasterDOB)dataList.get(0);
    this.pkObj=new ListMasterEntityBeanPK();
    this.pkObj.shipmentMode=listmasterDOB.getShipmentMode();
    this.pkObj.listType=listmasterDOB.getUldType();
  }catch(Exception e)
  {
    //Logger.error(FILE_NAME,"ejbCreate()","Exception in Creating Bean"+e);
    logger.error(FILE_NAME+"ejbCreate()"+"Exception in Creating Bean"+e);
	throw new EJBException(e.toString());
  }finally
  {
      listmasterDOB=null;
  }
  
    return pkObj;
  }

  public void ejbPostCreate(ArrayList dataList)
  {
  }

  public ListMasterEntityBeanPK ejbFindByPrimaryKey(ListMasterEntityBeanPK primaryKey) throws ObjectNotFoundException,EJBException
  {
  boolean limitRows=false;
  try
  {
    listmasterDAO.findByPrimariKey(primaryKey);
    limitRows=true;
  }
  catch(ObjectNotFoundException e)
  {
    //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Object Not Found"+e);
    logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Object Not Found"+e);
  }
  catch(SQLException sqe)
  {
    //Logger.error(FILE_NAME,"ejbFindByPrimarykey()","SQLExcpetion"+sqe);
    logger.error(FILE_NAME+"ejbFindByPrimarykey()"+"SQLExcpetion"+sqe);
  }
  catch(Exception ec)
  {
    //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Object not found"+ec);
    logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Object not found"+ec);
  }
  if(limitRows)
  { 
   return primaryKey;
  }
  else
  {
     throw new ObjectNotFoundException("Could not create the bean");
  }
   
  }

  public void ejbActivate()
  {
  }

  public void ejbLoad()
  {
  try
  {
    this.pkObj=(ListMasterEntityBeanPK)context.getPrimaryKey();
    this.listmasterDOB=listmasterDAO.load(this.pkObj);
  }catch(Exception e)
  {
    //Logger.error(FILE_NAME,"ejbLoad()","error in loading ejbLoad"+e);
    logger.error(FILE_NAME+"ejbLoad()"+"error in loading ejbLoad"+e);
  }
  
  }

  public void ejbPassivate()
  {
  }

  public void ejbRemove()
  {
  try
  {
   //Logger.error(FILE_NAME,"in ejbRemove()","removing list masterDOB"+pkObj); 
   logger.error(FILE_NAME+"in ejbRemove()"+"removing list masterDOB"+pkObj); 
   listmasterDAO.remove(pkObj);
  }catch(Exception e)
  {
    //Logger.error(FILE_NAME,"ejbRemove()","Could not find in Bean"+e);
    logger.error(FILE_NAME+"ejbRemove()"+"Could not find in Bean"+e);
  }
  
  }

  public void ejbStore()
  {
  try
  {
    if(isValid)
    {
      isValid=false;
      listmasterDAO.store(this.pkObj,this.listmasterDOB);
    }
  }catch(Exception e)
  {
    //Logger.error(FILE_NAME,"ejbStore()","Exception in storemethod"+e);
    logger.error(FILE_NAME+"ejbStore()"+"Exception in storemethod"+e);
  }
  
  }

  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
    listmasterDAO =new ListMasterDAO();
  }

  public void unsetEntityContext()
  {
    this.context = null;
  }
}
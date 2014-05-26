package com.qms.setup.ejb.bmp;
import java.util.ArrayList;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import com.qms.setup.java.ZoneCodeMasterDOB;
import com.qms.setup.dao.ZoneCodeMasterDAO;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.FinderException;

public class ZoneCodeMasterBean implements EntityBean 
{
  private EntityContext context;
  private ZoneCodeMasterDAO  zoneCodeMasterDAO    =  null;
  private ZoneCodeMasterDOB  zoneCodeMasterDOB    =  null;
  private final static  String FILE_NAME          =  "ZoneCodeMasterBean"; 
  private boolean dirtyFlag                       =  false;
  private static Logger logger = null;
  
  public ZoneCodeMasterBean()
  {
    logger  = Logger.getLogger(ZoneCodeMasterBean.class);
  }
  
  public ZoneCodeMasterPK ejbCreate()
  {
    //Logger.info(FILE_NAME,"in side ejbCreate() entitybean");
    logger.info(FILE_NAME+"in side ejbCreate() entitybean");
    return null;
  }

  public void ejbPostCreate()
  {
  //Logger.info(FILE_NAME,"in side ejbPostCreate() entitybean");
  logger.info(FILE_NAME+"in side ejbPostCreate() entitybean");
  }
  
  public ZoneCodeMasterPK ejbCreate(ZoneCodeMasterDOB dob) 
  { 
   
    ZoneCodeMasterPK  pk        =   new ZoneCodeMasterPK();
    try
    {
      zoneCodeMasterDAO         =   new ZoneCodeMasterDAO();
      zoneCodeMasterDAO.create(dob);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
    pk.zoneCode   =   dob.getZoneCode();
    return pk;
  }
  
  public void ejbPostCreate(ZoneCodeMasterDOB dob)
  {
  }
  
  public ZoneCodeMasterPK ejbFindByPrimaryKey(ZoneCodeMasterPK  primaryKey )
  {
    //Logger.info(FILE_NAME,"in side ejbFindByPrimaryKey() entitybean");
    logger.info(FILE_NAME+"in side ejbFindByPrimaryKey() entitybean");
    try
    {
      
      this.zoneCodeMasterDAO  =  new ZoneCodeMasterDAO();
      boolean falg            =  zoneCodeMasterDAO.findByPrimaryKey(primaryKey.zoneCode);      
    }    
    catch(Exception e)
    {
      e.printStackTrace();
    }
    return primaryKey;
  }

  public void ejbActivate()
  {
    //Logger.info(FILE_NAME,"in side ejbActivate() entitybean");
    logger.info(FILE_NAME+"in side ejbActivate() entitybean");
  }

  public void ejbLoad()
  {
     ZoneCodeMasterPK pk   =  null;
     
     try
     {
       //Logger.info(FILE_NAME,"in side ejbLoad() entitybean");
       logger.info(FILE_NAME+"in side ejbLoad() entitybean");
       pk            =    (ZoneCodeMasterPK)context.getPrimaryKey();
       this.zoneCodeMasterDAO   =  new ZoneCodeMasterDAO();
      // this.zoneCodeMasterDOB   =  zoneCodeMasterDAO.load(pk.zoneCode);
     }
     catch(Exception e)
     {
       e.printStackTrace();
     }
  }

  public void ejbPassivate()
  {
   //Logger.info(FILE_NAME,"in side ejbPassivate() entitybean");
   logger.info(FILE_NAME+"in side ejbPassivate() entitybean");
  }

  public void ejbRemove()
  {
    //Logger.info(FILE_NAME,"in side ejbRemove() entitybean");
    logger.info(FILE_NAME+"in side ejbRemove() entitybean");
    ZoneCodeMasterPK pk   =  null;
    try
    {
      pk                      =  (ZoneCodeMasterPK)context.getPrimaryKey();
      this.zoneCodeMasterDAO  =  new ZoneCodeMasterDAO();
      zoneCodeMasterDAO.remove(this.zoneCodeMasterDOB);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void ejbStore()
  {
    //Logger.info(FILE_NAME,"in side ejbStore() entitybean");
    logger.info(FILE_NAME+"in side ejbStore() entitybean");
    try
    {
      if(dirtyFlag)
      {
        this.zoneCodeMasterDAO   =  new ZoneCodeMasterDAO();
        zoneCodeMasterDAO.store(this.zoneCodeMasterDOB);
        dirtyFlag                = false;
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  public void setEntityContext(EntityContext ctx)
  {
    //Logger.info(FILE_NAME,"in side setEntityContext() entitybean");
    logger.info(FILE_NAME+"in side setEntityContext() entitybean");
    this.context = ctx;
  }

  public void unsetEntityContext()
  {
    //Logger.info(FILE_NAME,"in side unsetEntityContext() entitybean");
    logger.info(FILE_NAME+"in side unsetEntityContext() entitybean");
    this.context = null;
  }
  
  public void setZoneCodeMasterDOB(ZoneCodeMasterDOB dob)
  {
    dirtyFlag                       =   true;
    this.zoneCodeMasterDOB     =   dob;
  }
  
  public ZoneCodeMasterDOB getZoneCodeMasterDOB()
  {
    return this.zoneCodeMasterDOB;
  }
  
  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
	{
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}
   
    
}
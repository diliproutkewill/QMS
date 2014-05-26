package com.foursoft.etrans.setup.vendorregistration.ejb.bmp;
import javax.ejb.EntityBean;

import java.rmi.RemoteException;

import javax.ejb.EntityContext;
import javax.ejb.CreateException;
import com.foursoft.etrans.setup.vendorregistration.dao.VendorRegistrationDAO;
import com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
/**
 * 
 * This class will be useful to .
 * 
 * File		  : VendorRegistrationEnitityBean.java
 * @version	: etrans 1.6
 * @author	: Srinivasa Rao Koppurauri 
 * 
 * @date	  : 2002-03-24
 *
 */


public class VendorRegistrationEnitityBean implements EntityBean 
{
  	private					EntityContext	entityContext							=	null;
	static 					String			FILE_NAME							=	"VendorRegistration.java";
	VendorRegistrationEnitityPK			pkObj									=	null;
	boolean										isValid									=	false;
	VendorRegistrationDAO				vendorRegistrationDAO			=	null;
  VendorRegistrationJava vendorRegistrationJava;
  private static Logger logger = null;
  
  public VendorRegistrationEnitityBean()
  {
    logger  = Logger.getLogger(VendorRegistrationEnitityBean.class);
  }
  /**
   * 
   * @return VendorRegistrationJava
   */
	public VendorRegistrationJava getVendorRegistrationJava()
	{
		return vendorRegistrationJava;
	}
  /**
   * 
   * @param vendorRegistrationJava
   */
	public void	setVendorRegistrationJava(VendorRegistrationJava vendorRegistrationJava)
	{
		this.vendorRegistrationJava	= vendorRegistrationJava;
		isValid					= true;
	}
  /**
   * 
   * @param vendorRegistrationJava
   * @return VendorRegistrationEnitityPK
   */
  public VendorRegistrationEnitityPK ejbCreate(VendorRegistrationJava vendorRegistrationJava)
  {
    try
		{
			vendorRegistrationDAO.create(vendorRegistrationJava);
			this.pkObj				= new VendorRegistrationEnitityPK();
			pkObj.vendorId		= vendorRegistrationJava.getVendorId();
			this.vendorRegistrationJava	= vendorRegistrationJava;
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ", e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+ e);
			throw new javax.ejb.EJBException(e.toString());
		}
		return pkObj;
	}
  /**
   * 
   * @param vendorRegistrationJava
   */
  public void ejbPostCreate(VendorRegistrationJava vendorRegistrationJava)
  {
  }

  /**
   * 
   * @param pkObj
   * @return VendorRegistrationEnitityPK
   * @throws javax.ejb.FinderException
   */
  public VendorRegistrationEnitityPK ejbFindByPrimaryKey(VendorRegistrationEnitityPK pkObj)throws javax.ejb.FinderException
	{
		boolean	vendorRows = false;
		try
		{
			this.pkObj	= vendorRegistrationDAO.findByPrimaryKey(pkObj);
			vendorRows = true;
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"vendorFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"vendorFindByPrimaryKey()"+"Could not Find Bean "+e);
		}
		if (vendorRows)
    {
			return pkObj;
    }
		else
    {
			throw new javax.ejb.ObjectNotFoundException("Could not find bean");
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
  public void ejbLoad()throws javax.ejb.EJBException
	{
		try
		{
			pkObj = (VendorRegistrationEnitityPK)entityContext.getPrimaryKey();
			this.vendorRegistrationJava	= vendorRegistrationDAO.load(pkObj);
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME," ejbLoad()	","Exception while Loading the Model ", e);
      logger.error(FILE_NAME+" ejbLoad()	"+"Exception while Loading the Model "+ e);
			throw new javax.ejb.EJBException(e.toString());
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
  public void ejbRemove()throws javax.ejb.EJBException
 {
  	try
		{
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the VendorRegistrationDOB with Id  "+pkObj);
			vendorRegistrationDAO.remove(pkObj);			
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
  public void ejbStore()throws javax.ejb.EJBException
  {
		try 
		{
	
			if(isValid)
			{
				isValid=false;
				vendorRegistrationDAO.store(vendorRegistrationJava);
				
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME," ejbStore()	","Exception while Storing  the Model ", e);
      logger.error(FILE_NAME+" ejbStore()	"+"Exception while Storing  the Model "+ e);
			throw new javax.ejb.EJBException(e.toString());
		}
	}

  /**
   * 
   * @param entityContext
   * @throws javax.ejb.EJBException
   * @throws java.rmi.RemoteException
   */
 public void setEntityContext(EntityContext entityContext)throws javax.ejb.EJBException, java.rmi.RemoteException
	{
		this.entityContext 	= entityContext;
		vendorRegistrationDAO				= new VendorRegistrationDAO();
	}

  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws java.rmi.RemoteException
   */
 public void unsetEntityContext()throws javax.ejb.EJBException, java.rmi.RemoteException
	{
		this.entityContext = null;
	}
	
}
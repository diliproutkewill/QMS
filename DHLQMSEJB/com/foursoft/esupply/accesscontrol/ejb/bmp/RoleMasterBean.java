/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.accesscontrol.ejb.bmp;

import javax.ejb.*;

import com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.accesscontrol.dao.RoleDAO;
import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.accesscontrol.exception.RoleDAOAppException;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;

/**
 * File			: RoleMasterBean.java
 * module 		: esupply
 * sub-module	: AccessControl
 *
 *
 * This is used for Registering the Role.
 * It instantiates the RoleDAO and takes the Role model and pass it to RoleDAO
 * It represents the tables FS_ROLMASTER, FS_ROLEPERMISSIONS
 *
 * @author	Madhusudhan Rao. P,
 * @date	28-08-2001
 *
 * Modified History
 * Amit Parekh		03/03/2003			Updated to handle versioning of this entitiy. This changes also require
 * 										changes in the database
 */

public class RoleMasterBean implements javax.ejb.EntityBean
{
	private 	javax.ejb.EntityContext  entityContext = null;
	public		RoleModel 	roleModel;
    private transient RoleDAO	roleDAO;
	private transient boolean isModified	= false;	

	public com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK  ejbCreate(RoleModel roleModel)	throws javax.ejb.CreateException
	{
		this.roleModel = roleModel;
		try
		{
			roleDAO	= getDAO();
			// insert dataBase code here
			roleDAO.create(roleModel);
		}
		catch(RoleDAOAppException rap)
		{
			entityContext.setRollbackOnly();
			throw new CreateException(rap.getMessage());
		}
		catch(DBSysException dbs)
		{
			throw new EJBException(dbs.getMessage());
		}
		
		com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK pkObj = new com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK();
		pkObj.roleId 		= roleModel.getRoleId();
		pkObj.locationId 	= roleModel.getLocationId();
		return pkObj;
	}
	
	public void  ejbPostCreate(RoleModel roleModel)	throws javax.ejb.CreateException
	{
	}
	//Place your business methods here.
	
	public void setRoleModel(RoleModel roleModel) throws DataIntegrityViolationException
	{
		int dbVersion	= this.roleModel.getVersion();
		int currVersion	= roleModel.getVersion();

		if(currVersion==dbVersion) 
		{
			// Increment Version Number
			roleModel.setVersion( dbVersion + 1);

			// Modify Role
			this.roleModel	= roleModel;
			isModified	= true;

		} else {
			// Old Version (Stale Data)
			throw new DataIntegrityViolationException("The Version of your '"+roleModel.ENTITY_LABEL+"' data is old. "+this.roleModel.getModifiedByMessage() );
		}
	}
	
	public RoleModel getRoleModel()
	{
		return roleModel;
	}

	//FindByPK method
	public com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK ejbFindByPrimaryKey(com.foursoft.esupply.accesscontrol.ejb.bmp.RoleMasterBeanPK pkObj)
		throws javax.ejb.FinderException
	{
		try
		{
			roleDAO	= getDAO();
			// insert dataBase code here
			roleDAO.findByPrimaryKey(pkObj.roleId, pkObj.locationId);
		}
   catch(FinderException fe)
   {
      
	  throw new FinderException("Unable to find the Role Model");
   }
   catch(Exception dbs)
	{
			
			throw new EJBException(dbs.getMessage() );
	}
		this.roleModel = new RoleModel();
		roleModel.setRoleId(pkObj.roleId);
		roleModel.setLocationId(pkObj.locationId);
		return pkObj;
	}

	public void ejbLoad() throws javax.ejb.EJBException
	{
		try
		{
			roleDAO	= getDAO();
			RoleMasterBeanPK pkObj = (RoleMasterBeanPK)entityContext.getPrimaryKey();
			roleModel	= (RoleModel)roleDAO.load(pkObj.roleId, pkObj.locationId);
		}
		catch(RoleDAOAppException rap)
		{
			throw new EJBException(rap.getMessage());
		}
		catch(DBSysException dbs)
		{
			throw new EJBException(dbs.getMessage());
		}
	}

	public void ejbStore() throws javax.ejb.EJBException
	{
		if(isModified)
		{
			try
			{
				roleDAO	= getDAO();
				if(isModified)
					roleDAO.store(roleModel);
			}
			catch(RoleDAOAppException rap)
			{
				throw new EJBException(rap.getMessage());
			}
			catch(DBSysException dbs)
			{
				throw new EJBException(dbs.getMessage());
			}
		}
		isModified = false;
	}

	public void ejbRemove()	throws javax.ejb.EJBException, javax.ejb.RemoveException
	{
		try
		{
			roleDAO	= getDAO();
			roleDAO.remove(roleModel.getRoleId(), roleModel.getLocationId() );
		}
		catch(RoleDAOAppException rap)
		{
			entityContext.setRollbackOnly();
			throw new RemoveException(rap.getMessage());
		}
		catch(DBSysException dbs)
		{
			throw new EJBException(dbs.getMessage());
		}
	
	}

	public RoleMasterBean() {
		//System.out.println("FROM Constructor of '"+this.getClass().getName()+"'");
	}

	//Implementing EntityBeanInterface methods.....
	public void setEntityContext(EntityContext entityContext) throws javax.ejb.EJBException
	{
		this.entityContext = entityContext;
		//System.out.println("FROM setEntityContext of '"+this.getClass().getName()+"'");
	}
	
	public void unsetEntityContext() throws javax.ejb.EJBException
	{
		this.entityContext = null;
	}
	
		//Remove Methods....
	
	public void ejbActivate() throws javax.ejb.EJBException
	{
	}
	
	public void ejbPassivate() throws javax.ejb.EJBException
	{
		roleModel	= null;
		roleDAO		= null;
	}
	
	//-----end of implementation-----------
	//Serialization of BeanObject is to be done here.....//
	private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException
	{
		out.defaultWriteObject();
	}

	private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}
	// serialization done       ///

    private RoleDAO getDAO() 
	{
        if(roleDAO == null) 
		{
            roleDAO = new RoleDAO();
	    }
        return roleDAO;
    }

}

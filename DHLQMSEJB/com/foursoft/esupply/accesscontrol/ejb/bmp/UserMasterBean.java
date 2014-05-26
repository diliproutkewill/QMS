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

import javax.naming.NamingException;

import com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK;
import com.foursoft.esupply.accesscontrol.java.UserModel;
import com.foursoft.esupply.accesscontrol.dao.UserDAO;
import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.accesscontrol.exception.UserDAOAppException;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;

/**
 * File			: UserMasterBean.java
 * module 		: esupply
 * sub-module	: AccessControl
 *
 *
 * This is used for Registering the User.
 * It instantiates the UserDAO and takes the UserModel and pass it to UserDAO
 * It represents the FS_USERMASTER, FS_USERROLES, FS_USERPREFERENCES
 *
 * @author	Madhusudhan Rao. P,
 * @date	28-08-2001
 *
 * Modified History
 * Amit Parekh		03/03/2003			Updated to handle versioning of this entitiy. This changes also require
 * 										changes in the database
 */

public class UserMasterBean implements javax.ejb.EntityBean
{
	private 	javax.ejb.EntityContext entityContext 	= null;
	public		UserModel 				userModel;
	private		transient  UserDAO		userDAO;
	private 	transient boolean		isModified		= false;	
	//Place your business methods here.

	public com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK  ejbCreate(UserModel userModel)	throws javax.ejb.CreateException
	{
		this.userModel = userModel;
		// insert dataBase code here
		try
		{
			userDAO = getDAO();
			userDAO.create(userModel);
		}
		catch(UserDAOAppException uap)
		{
			entityContext.setRollbackOnly();
			throw new CreateException(uap.getMessage());
		}
/*        
		catch(DBSysException dbs)
		{
			throw new EJBException(dbs.getMessage());
		}
*/
		com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK pkObj = new com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK();
		pkObj.userId 		= userModel.getUserId();
		pkObj.locationId 	= userModel.getLocationId();
		return pkObj;
	}
	
	public void  ejbPostCreate(UserModel userModel)	throws javax.ejb.CreateException
	{
	}
	
	public void setUserModel(UserModel userModel) throws DataIntegrityViolationException
	{
		int dbVersion	= this.userModel.getVersion();
		int currVersion	= userModel.getVersion();

		if(currVersion==dbVersion) 
		{

			// Increment Version Number
			userModel.setVersion( dbVersion + 1 );
				
			// Modify User
			this.userModel	= userModel;
			isModified		= true;

		} 
		else 
		{
			// Old Version (Stale Data)
			throw new DataIntegrityViolationException(	"The Version of your '"+userModel.ENTITY_LABEL+"' data is old. "+this.userModel.getModifiedByMessage() );
		}

	}
	
	public UserModel getUserModel()
	{
		return userModel;
	}

	//FindByPK method
	public com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK ejbFindByPrimaryKey(com.foursoft.esupply.accesscontrol.ejb.bmp.UserMasterBeanPK pkObj)
		throws javax.ejb.FinderException
	{
		// insert dataBase code here
		try
		{
			userDAO = getDAO();
			
			userDAO.findByPrimaryKey(pkObj.userId, pkObj.locationId);
		}
		catch(Exception dbs)
		{
			throw new EJBException(dbs.getMessage() );
		}
		this.userModel = new UserModel();
		userModel.setUserId(pkObj.userId);
		userModel.setLocationId(pkObj.locationId);
		return pkObj;
	}

	public void ejbLoad() throws javax.ejb.EJBException
	{
		try
		{
			userDAO 	= getDAO();
			UserMasterBeanPK 	pkObj 		= (UserMasterBeanPK)entityContext.getPrimaryKey();
			userModel						= (UserModel)userDAO.load(pkObj.userId, pkObj.locationId);
		}
		catch(UserDAOAppException uap)
		{
			throw new EJBException(uap.getMessage());
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
				userDAO = getDAO();
				userDAO.store(userModel);
			}
			catch(UserDAOAppException uap)
			{
				throw new EJBException(uap.getMessage());
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
			userDAO = getDAO();
			userDAO.remove(userModel.getUserId(), userModel.getLocationId());
		}
		catch(UserDAOAppException uap)
		{
			entityContext.setRollbackOnly();
			throw new RemoveException(uap.getMessage());
		}
		catch(DBSysException dbs)
		{
			throw new EJBException(dbs.getMessage());
		}
	}

	public UserMasterBean() {
		//System.out.println("FROM Constructor of '"+this.getClass().getName()+"'");
	}

	//Implementing EntityBeanInterface methods.....
	public void setEntityContext(EntityContext entityContext) throws javax.ejb.EJBException
	{
		//System.out.println("FROM setEntityContext of '"+this.getClass().getName()+"'");
		this.entityContext = entityContext;
		try
		{
			userDAO	= new UserDAO();            
		}
		catch(Exception e)
		{
		}
	}
	
	public void unsetEntityContext() throws javax.ejb.EJBException
	{
		this.entityContext = null;
		userDAO	= null;
	}
	
		//Remove Methods....
	
	public void ejbActivate() throws javax.ejb.EJBException
	{
	}
	
	public void ejbPassivate() throws javax.ejb.EJBException
	{
		userModel	= null;
		userDAO		= null;
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
	
	private UserDAO getDAO() 
	{
        if(userDAO == null) 
		{
			userDAO = new UserDAO();		// DONOT process optional roles table
	    }
        return userDAO;
    }

}

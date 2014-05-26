/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x      
 * 
 */ 

package com.foursoft.esupply.delegate;

import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSession;
import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSessionHome;
import com.foursoft.esupply.accesscontrol.exception.AlreadyExistsException;
import com.foursoft.esupply.accesscontrol.exception.DataIntegrityViolationException;
import com.foursoft.esupply.accesscontrol.exception.NotExistsException;
import com.foursoft.esupply.accesscontrol.java.RoleModel;
import com.foursoft.esupply.accesscontrol.java.UserModel;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.rmi.*;
import java.rmi.RemoteException;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import javax.ejb.CreateException;

import javax.naming.*;
import javax.naming.NamingException;

import javax.rmi.PortableRemoteObject;

/**
 * File			: UserRoleDelegate.java
 * sub-module 	: delegate
 * module 		: esupply
 * 
 * This file for BusinessDelegation of User Interface
 * 
 * @author	Madhusudhan Rao. V, 
 * @date	09-08-2003
 */
 
public class UserRoleDelegate 
{
    private static Logger logger = null;
    public UserRoleDelegate() throws FoursoftException
    {
        logger  = Logger.getLogger(UserRoleDelegate.class);
        try
        {
            UserRoleRegistrationSessionHome userHome = (UserRoleRegistrationSessionHome) LookUpBean.getEJBHome("UserRoleRegistrationSessionBean");
            userInterface = userHome.create();
            //UserRoleRegistrationSessionHome userHome = (UserRoleRegistrationSessionHome) LookUpBean.getEJBHome("java:comp/env/ejb/UserRoleRegistrationSession");
            //userInterface = userHome.create();
	   }
        catch(NamingException nExc)
        {
            //Logger.error(FILENAME,"Error in UserRoleDelegate Constructor ",nExc);
            logger.error(FILENAME+"Error in UserRoleDelegate Constructor "+nExc);
            throw FoursoftException.getException(nExc,FILENAME,"UserRoleDelegate");
        }
        catch(CreateException creExc)
        {
            //Logger.error(FILENAME,"Error in UserRoleDelegate Constructor ",creExc);\
            logger.error(FILENAME+"Error in UserRoleDelegate Constructor "+creExc);
            throw FoursoftException.getException(creExc,FILENAME,"UserRoleDelegate");
        }
        catch(Exception rExc)
        {
            //Logger.error(FILENAME,"Error in UserRoleDelegate Constructor",rExc);
            logger.error(FILENAME+"Error in UserRoleDelegate Constructor"+rExc);
            throw FoursoftException.getException(rExc,FILENAME,"UserRoleDelegate");
        }
    }

	
    public UserModel getUserInformation(String locationId,String userId) throws FoursoftException
    {
        
		try
        {
            return (UserModel)userInterface.getUserInformation(locationId, userId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the UserInformation",exp);
            logger.error(FILENAME+"Error in Retrieving the UserInformation"+exp);
          	throw FoursoftException.getException(exp,FILENAME,"getUserInformation","Uable to find the userId and LocationId combination details for user Id:"+userId,NotExistsException.ERRORCODE);
        }
    }

    public ArrayList getLocationIds(String locationId) throws FoursoftException
    {
        try
        {
            return userInterface.getLocationIds(locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getLocationIds",exp);
            logger.error(FILENAME+"Error in getLocationIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getLocationIds");
        }
    }
//added by purushothamanan to validate location id in user add screen

	public boolean isLocationIdExists(String locationId,String accessType) throws FoursoftException
    {
        try
        {
            return userInterface.isLocationIdExists(locationId,accessType);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getLocationIds",exp);
            logger.error(FILENAME+"Error in getLocationIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getLocationIds");
        }
    }

	public boolean IsLocIdExists( String locationId, String userType, String accessType, String custlocId) throws FoursoftException
    {
        try
		{
			return userInterface.IsLocIdExists(locationId,userType,accessType,custlocId);

		}
		catch(Exception exp)
		{
				//Logger.error(FILENAME,"Error in validating LocationIds",exp);
        logger.error(FILENAME+"Error in validating LocationIds"+exp);
				throw FoursoftException.getException(exp,FILENAME,"IsLocIdExists");
		}
		
    }

	/* new methods ends here */
	public ArrayList getLocationIds(String locationId, String loginAccessType, String accessType, String locationIdFilter) throws FoursoftException
    {
        try
        {
            return userInterface.getLocationIds(locationId, loginAccessType, accessType, locationIdFilter);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getLocationIds",exp);
            logger.error(FILENAME+"Error in getLocationIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getLocationIds");
        }
    }

    public int changePassword(String userId, String locationId, String oldPassword, String newPassword,String mailWord) throws FoursoftException
    {
        try
        {
            return userInterface.changePassword(userId, locationId, oldPassword, newPassword, mailWord);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in changePassword",exp);
            logger.error(FILENAME+"Error in changePassword"+exp);
            throw FoursoftException.getException(exp,FILENAME,"changePassword","100074");
        }
    }
    
    public ArrayList getRoleIds(String locationId, String accessType, String roleFilter) throws FoursoftException
    {
        try
        {
            return userInterface.getRoleIds(locationId,accessType,roleFilter);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getRoleIds",exp);
            logger.error(FILENAME+"Error in getRoleIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRoleIds");
        }
    }

    public ArrayList getRoleIdLocation(String roleId, String roleLocationId, String locationId, String accessType, String loginBeanLocationId, String loginBeanAccessType, String roleModule) throws FoursoftException
    {
        try
        {
            return userInterface.getRoleIdLocation(roleId, roleLocationId, locationId, accessType, loginBeanLocationId, loginBeanAccessType, roleModule);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getRoleIdLocation",exp);
            logger.error(FILENAME+"Error in getRoleIdLocation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRoleIdLocation");
        }
    }

    public ArrayList getTransactionIds(String currentModule, String roleLevel) throws FoursoftException
    {
        try
        {
            return userInterface.getTransactionIds(currentModule, roleLevel);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getTransactionIds",exp);
            logger.error(FILENAME+"Error in getTransactionIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getTransactionIds");
        }
    }
    
    public ArrayList getTransactionIds(String currentModule, int shipmentModeId, String roleLevel) throws FoursoftException
    {
        try
        {
            return userInterface.getTransactionIds(currentModule, shipmentModeId, roleLevel);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getTransactionIds",exp);
            logger.error(FILENAME+"Error in getTransactionIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getTransactionIds");
        }
    }

    public RoleModel getRoleModel(String roleId, String locationId) throws FoursoftException
    {




    try
        {
            return (RoleModel)userInterface.getRoleModel(roleId, locationId);
        }
        catch(Exception exp)
        {
            
			//Logger.error(FILENAME,"Error in Retrieving the UserInformation",exp);
      logger.error(FILENAME+"Error in Retrieving the UserInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRoleModel");
        }



       /* try
        {
            return (RoleModel) userInterface.getRoleModel(roleId, locationId);
        }
		catch(NotExistsException exp)
		{
			Logger.error(FILENAME,"Error in getRoleModel",exp);
            //errorMessage = "Error: The Role '"+roleId+"' does not exist." ;
            //throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation",errorMessage);
			throw FoursoftException.getException(exp,FILENAME,"getRoleModel","100079",NotExistsException.ERRORCODE);
		}
		//For Under modification access control.
        catch(Exception exp)
        { System.out.println(" EXP "+exp);

	     String 	errorMessage = "Error: The Role '"+roleId+"' does not exist." ;
            Logger.error(FILENAME,"Error in getRoleModel",errorMessage);
            throw FoursoftException.getException(errorMessage,FILENAME,"getRoleModel");
        }
    */
	  }

    public void registerRole(RoleModel roleModel, String userId, String locationId) throws FoursoftException
    {
        String errorMessage = "";
        try
        {
            userInterface.registerRole(roleModel, userId, locationId);
        }//throw FoursoftException.getException(exp,FILENAME,"registerUser","100075",AlreadyExistsException.ERRORCODE);
		catch(AlreadyExistsException exp)
		{
			errorMessage = exp.getMessage();
			//Logger.error(FILENAME,"Already Exists Exception ",exp);
      logger.error(FILENAME+"Already Exists Exception "+exp);
            //throw FoursoftException.getException(fe,FILENAME,"registerRole",errorMessage);
			throw FoursoftException.getException(exp,FILENAME,"registerRole","100076",AlreadyExistsException.ERRORCODE);
		}
		catch(FoursoftException fe)
		{
			errorMessage = fe.getMessage();
			//Logger.error(FILENAME,"Already Exists Exception ",fe);
      logger.error(FILENAME+"Already Exists Exception "+fe);
            throw FoursoftException.getException(fe,FILENAME,"registerRole",errorMessage);
		}
		catch(Exception e)
		{
			//Logger.error(FILENAME,"General Exception ",e.getMessage());
      logger.error(FILENAME+"General Exception "+e.getMessage());
			errorMessage = "Error: The Role was not registered.";
            throw FoursoftException.getException(e,FILENAME,"registerRole",errorMessage);
		}
    }

    public void updateRoleModel(RoleModel roleModel, String userId, String locationId) throws FoursoftException
    {
        try
        {
            userInterface.updateRoleModel(roleModel,userId,locationId);
        }
		catch(DataIntegrityViolationException exp)
        {
            //Logger.error(FILENAME,"Error in updateRoleModel",exp);
            logger.error(FILENAME+"Error in updateRoleModel"+exp);
            throw FoursoftException.getException(exp,FILENAME,"updateUserInformation","100073",DataIntegrityViolationException.ERRORCODE);
        } 
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in updateRoleModel",exp);
            logger.error(FILENAME+"Error in updateRoleModel"+exp);
            throw FoursoftException.getException(exp,FILENAME,"updateRoleModel");
        }
    }

    public boolean removeRole(String roleId, String locationId, String userId, String loginbeanLocationId) throws FoursoftException
    {
        String errorMessage="";
        try
        {
            userInterface.removeRole(roleId, locationId, userId, loginbeanLocationId);
            return true;
        }
        catch(DataIntegrityViolationException de)
		{
			//Logger.warning(FILENAME," error in removeRole",de);
      logger.warn(FILENAME+" error in removeRole"+de);
			errorMessage =	"One or more Users have been assigned the Role '"+ roleId+"'."+"\n"+
							"Hence, deletion of this Role is not possible.";
            //throw FoursoftException.getException(de,FILENAME,"removeRole",errorMessage);
			throw FoursoftException.getException(de,FILENAME,"removeRole",errorMessage,DataIntegrityViolationException.ERRORCODE);
		}
        catch(Exception exp)
        {
            errorMessage = "Error: The Role was not deleted." ;
            //Logger.error(FILENAME,"Error in removeRoles",exp);
            logger.error(FILENAME+"Error in removeRoles"+exp);
            throw FoursoftException.getException(exp,FILENAME,"removeRole",errorMessage);
        }
    }

    public ArrayList getRoleViewAllInformation(String locationId) throws FoursoftException
    {
        try
        {
            return userInterface.getRoleViewAllInformation(locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getRoleViewAllInformation",exp);
            logger.error(FILENAME+"Error in getRoleViewAllInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRoleViewAllInformation");
        }
    }

    public ArrayList getRoleViewAllInformation() throws FoursoftException
    {
        try
        {
            return userInterface.getRoleViewAllInformation();
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getRoleViewAllInformation",exp);
            logger.error(FILENAME+"Error in getRoleViewAllInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRoleViewAllInformation");
        }
    }

    public ArrayList getTransactionDetailVOBs(String moduleName, String roleLevel) throws FoursoftException
    {
        try
        {
            return userInterface.getTransactionDetailVOBs(moduleName, roleLevel);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getTransactionDetailVOBs",exp);
            logger.error(FILENAME+"Error in getTransactionDetailVOBs"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getTransactionDetailVOBs");
        }
    }

    public ArrayList getTransactionDetailVOBs(String moduleName, int shipmentMode, String roleLevel) throws FoursoftException
    {
        try
        {
            return userInterface.getTransactionDetailVOBs(moduleName, shipmentMode, roleLevel);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getTransactionDetailVOBs",exp);
            logger.error(FILENAME+"Error in getTransactionDetailVOBs"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getTransactionDetailVOBs");
        }
    }
    public ArrayList getUserIds(String locationId, String filterString) throws FoursoftException 
    {
        try
        {
            return userInterface.getUserIds(locationId, filterString);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserIds",exp);
            logger.error(FILENAME+"Error in getUserIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserIds");
        }        
    }
    //added by ramakrishna
    public ArrayList getRepOfficersIds( String filterString) throws FoursoftException 
    {
        try
        {
            return userInterface.getRepOfficersIds( filterString);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserIds",exp);
            logger.error(FILENAME+"Error in getUserIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserIds");
        }        
    }
 
// Commented by subrahmanyam for Enhancement 154384 on 10/04/09
   // public ArrayList getUserIds(String locationId, String accessType, String filterString) throws FoursoftException 
//Added by subrahmanyam for Enhancement 154384 on 10/04/09   
    public ArrayList getUserIds(String locationId, String accessType, String filterString,String terminalId,String terminalType) throws FoursoftException 
    {
        try
        {
    // Commented by subrahmanyam for Enhancement 154384 on 10/04/09
        // return userInterface.getUserIds(locationId, accessType, filterString);
        //Added by subrahmanyam for Enhancement 154384 on 10/04/09   
            return userInterface.getUserIds(locationId, accessType, filterString,terminalId,terminalType);
            
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserIds",exp);
            logger.error(FILENAME+"Error in getUserIds"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserIds");
        }        
    }

    public HashMap getWarehousesAndRoles(String parentId, String accessType) throws FoursoftException 
    {
        try
        {
            return userInterface.getWarehousesAndRoles(parentId, accessType);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getWarehousesAndRoles",exp);
            logger.error(FILENAME+"Error in getWarehousesAndRoles"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getWarehousesAndRoles");
        }        
    }

    public boolean isMaxLimitNotExceeded() throws FoursoftException 
    {
        try
        {
            return userInterface.isMaxLimitNotExceeded();
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in isMaxLimitNotExceededs",exp);
            logger.error(FILENAME+"Error in isMaxLimitNotExceeded"+exp);
            throw FoursoftException.getException(exp,FILENAME,"isMaxLimitNotExceeded");
        }        
    }

    public void registerUser(UserModel userModel, String userId, String locationId, String notification) throws FoursoftException 
    {
        String errorMessage = "";
        try
        {
            userInterface.registerUser(userModel, userId, locationId, notification);
        }
        catch(AlreadyExistsException aeExp)
        {
          //Logger.warning(FILENAME,"Error in registerUser",aeExp.getMessage());
          logger.warn(FILENAME+"Error in registerUser"+aeExp.getMessage());
			//errorMessage = "The User '"+userModel.getUserId()+"' already exists for '"+userModel.getLocationId()+"'.";
            //throw FoursoftException.getException(fe,FILENAME,"registerUser",errorMessage);
      if("Employee Id is Already Exist".equals(aeExp.getMessage()))
        throw FoursoftException.getException(aeExp,FILENAME,"registerUser","100628",AlreadyExistsException.ERRORCODE);
      else if("The Reporting Officer's Code is not exit".equals(aeExp.getMessage()))  
        throw FoursoftException.getException(aeExp,FILENAME,"registerUser","100630",AlreadyExistsException.ERRORCODE);
      else if("The Designation Id is not Exist".equals(aeExp.getMessage()))  
        throw FoursoftException.getException(aeExp,FILENAME,"registerUser","100629",AlreadyExistsException.ERRORCODE);
      else
        throw FoursoftException.getException(aeExp,FILENAME,"registerUser","100075",AlreadyExistsException.ERRORCODE);
    
		}
		catch(FoursoftException fe )
		{
			//Logger.warning(FILENAME,"Error in registerUser",fe.getHiddenException());
      logger.warn(FILENAME+"Error in registerUser"+fe.getHiddenException());
			errorMessage = "The User '"+userModel.getUserId()+"' already exists for '"+userModel.getLocationId()+"'.";
            throw FoursoftException.getException(fe,FILENAME,"registerUser",errorMessage);
		}
		catch(Exception exp)
		{
			//Logger.error(FILENAME,"Error in registerUser",exp);
      logger.error(FILENAME+"Error in registerUser"+exp);
        logger.info("errorMessage"+errorMessage);  
			errorMessage = "Error: The User was not registered.";
            throw FoursoftException.getException(exp,FILENAME,"registerUser",errorMessage); 
          
		}
    }

    public void updateUserInformation(UserModel userModel, String userId, String locationId) throws FoursoftException
    {
        String errorMessage = "";
        try
        {
            userInterface.updateUserInformation(userModel, userId, locationId);
        }
        
        catch(NotExistsException nExp)
        {
            //Logger.error(FILENAME,"Error in updateUserInformation",nExp.getMessage());
            logger.error(FILENAME+"Error in updateUserInformation"+nExp.getMessage());
            if("The Designation Id is not Exist".equals(nExp.getMessage()))
            {
                errorMessage = "Error: The Designation Id does not exist." ;
            //throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation",errorMessage);
              throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation","100629",NotExistsException.ERRORCODE);
            }
            if("The Reporting Officer's Code is not exit".equals(nExp.getMessage()))
            {
                errorMessage = "Error: The Reporting Officer's Code is not exit" ;
            //throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation",errorMessage);
              throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation","100630",NotExistsException.ERRORCODE);
            }
           /* if("Employee Id is not Exist".equals(nExp.getMessage()))
            {
                errorMessage = "Error: Employee Id is not Exist" ;
            //throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation",errorMessage);
              throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation","100628",NotExistsException.ERRORCODE);
            }*/
            else
            {
              errorMessage = "Error: The User '"+userModel.getUserId()+"' does not exist." ;
            //throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation",errorMessage);
              throw FoursoftException.getException(nExp,FILENAME,"updateUserInformation","100078",NotExistsException.ERRORCODE);
            }
        } 
        catch(FoursoftException fe )
        {
         //Logger.warning(FILENAME,"Error in registerUser",fe.getHiddenException());
         logger.warn(FILENAME+"Error in registerUser"+fe.getHiddenException());
         errorMessage = "The User '"+userModel.getUserId()+"' already exists for '"+userModel.getLocationId()+"'.";
            throw FoursoftException.getException(fe,FILENAME,"registerUser",errorMessage);
        }
        catch(DataIntegrityViolationException exp)
        {
            //Logger.error(FILENAME,"Error in updateUserInformation",exp);
            logger.error(FILENAME+"Error in updateUserInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"updateUserInformation","100073",DataIntegrityViolationException.ERRORCODE);
        } 
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in updateUserInformation",exp);
            logger.error(FILENAME+"Error in updateUserInformation"+exp);
            errorMessage = "Error: The User information was not updated." ;
            throw FoursoftException.getException(exp,FILENAME,"updateUserInformation",errorMessage);
        } 
    }

    public boolean removeUser(String userId, String locationId, String loginbeanUserId, String loginbeanLocationId) throws FoursoftException 
    {
        String errorMessage = "";
        try
        {
            return userInterface.removeUser(userId, locationId, loginbeanUserId, loginbeanLocationId);
        }
        catch(NotExistsException nExp)
        {
            //Logger.error(FILENAME,"Error in removeUser",nExp);
            logger.error(FILENAME+"Error in removeUser"+nExp);
            errorMessage = "Error: The User '"+userId+"' does not exist for '"+locationId+"'.";
            throw FoursoftException.getException(nExp,FILENAME,"removeUser",errorMessage);
        } 
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in removeUser",exp);
            logger.error(FILENAME+"Error in removeUser"+exp);
            errorMessage = "Error: The User information was not deleted." ;
            throw FoursoftException.getException(exp,FILENAME,"removeUser",errorMessage);
        } 
    }

    public ArrayList getUserViewAllInformation(String locationId) throws FoursoftException 
    {
        try
        {
            return userInterface.getUserViewAllInformation(locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserViewAllInformation",exp);
            logger.error(FILENAME+"Error in getUserViewAllInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserViewAllInformation");
        }        
    }   

    public ArrayList getUserViewAllInformation() throws FoursoftException 
    {
        try
        {
            return userInterface.getUserViewAllInformation();
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserViewAllInformation",exp);
            logger.error(FILENAME+"Error in getUserViewAllInformation"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserViewAllInformation");
        }        
    } 
/*
    public UserModel getUserInformation(String locationId, String userId) throws FoursoftException 
    {
        try
        {
            return userInterface.getUserInformation(locationId, userId);
        }
        catch(NotExistsException ne)
		{
			Logger.error(FILENAME,"Record does not exists",ne.getMessage());
            throw FoursoftException.getException(ne,FILENAME,"getUserInformation");
		}
        catch(Exception exp)
        {
            Logger.error(FILENAME,"Error in getUserInformation",exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserInformation");
        }        
    }
*/    

    public ArrayList getUserLogDetails(String locationId, String userId, Timestamp fromDateTimestamp, Timestamp toDateTimestamp) throws FoursoftException 
    {
        try
        {
            return userInterface.getUserLogDetails(locationId, userId, fromDateTimestamp, toDateTimestamp);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserLogDetails",exp);
            logger.error(FILENAME+"Error in getUserLogDetails"+exp);
            //throw FoursoftException.getException(exp,FILENAME,"getUserLogDetails");
			throw FoursoftException.getException(exp,FILENAME,"getUserLogDetails","Uable to find LogDetails"+userId,NotExistsException.ERRORCODE);

        }        
    } 

    public ArrayList getUserLogViewAllDetails(String locationId, Timestamp fromDateTimestamp, Timestamp toDateTimestamp) throws FoursoftException 
    {
        try
        {
            return userInterface.getUserLogViewAllDetails(locationId, fromDateTimestamp, toDateTimestamp);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in getUserLogViewAllDetails",exp);
            logger.error(FILENAME+"Error in getUserLogViewAllDetails"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getUserLogViewAllDetails");
        }        
    } 

    public String upDateUserWithPassword(String userId, String locationId,String ipAddress) throws FoursoftException
    {
       try
        {
            return userInterface.upDateUserWithPassword(userId, locationId,ipAddress);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in upDateUserWithPassword",exp);
            logger.error(FILENAME+"Error in upDateUserWithPassword"+exp);
            throw FoursoftException.getException(exp,FILENAME,"upDateUserWithPassword");
        }  
    }
    
    //private UserRoleRegistrationSession userInterface;
    private UserRoleRegistrationSession userInterface;
    private final static String FILENAME = "UserRoleDelegate.java";

    public String resetUserPassword(String userId, String locationId,String pwd, String email)  throws FoursoftException
    {
       try
        {
            return userInterface.resetUserPassword(userId, locationId, pwd, email);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in resetUserPassword",exp);
            logger.error(FILENAME+"Error in resetUserPassword"+exp);
            throw FoursoftException.getException(exp,FILENAME,"resetUserPassword");
			
        }  
    }

    public void updateSecuritySettings(ArrayList arrayList)  throws FoursoftException
    {
       try
        {
            userInterface.updateSecuritySettings(arrayList);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in updatePasswordDuration",exp);
            logger.error(FILENAME+"Error in updatePasswordDuration"+exp);
            throw FoursoftException.getException(exp,FILENAME,"updatePasswordDuration");
        }  
    }

	public ArrayList getSecuritySettings() throws FoursoftException
    {
       try
        {
            return userInterface.getSecuritySettings();
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in updatePasswordDuration",exp);\
            logger.error(FILENAME+"Error in updatePasswordDuration"+exp);
            throw FoursoftException.getException(exp,FILENAME,"updatePasswordDuration");
        }  
    }
    //@@ Added by subrahmanyam for the Enhancement 154384 on 28/01/09
  public ArrayList getSalesPersons(String locationId, String delSalesPerson,String accessTerminal)throws FoursoftException
  {
    ArrayList salesPersons =null;
    try
    {
      salesPersons = userInterface.getSalesPersons(locationId,delSalesPerson,accessTerminal);
    }
    catch(Exception e)
    {
      logger.error(FILENAME+" Error in getting the SalesPersons "+e); 
      throw FoursoftException.getException(e,FILENAME," Getting the SalesPersons");
    }
    return salesPersons;
  }
//@@Commented & Added by subrahmanyam for the pbn id:208582 on 17-Jun-10
  //public void updateSalesPerson(String newSalesPerson, String delSalesPerson)throws FoursoftException
  public void updateSalesPerson(String newSalesPerson, String delSalesPerson,String accessType)throws FoursoftException
  {
    try
    {
    	//@@Commented & Added by subrahmanyam for the pbn id:208582 on 17-Jun-10
      //userInterface.updateSalesPerson(newSalesPerson, delSalesPerson);
    	userInterface.updateSalesPerson(newSalesPerson, delSalesPerson,accessType);
    }catch(Exception e)
    {
      logger.error(FILENAME+" While updating  the SalesPersons "+e); 
      throw FoursoftException.getException(e,FILENAME," while updating the SalesPersons");
    }
  }
  public String getDelSalesPerson(String delUserId)throws FoursoftException
  {
      String newCreatedBy=null;
    try
    {
      newCreatedBy=userInterface.getDelSalesPerson(delUserId);
    }catch(Exception e)
    {
      logger.error(FILENAME+" While Getting the created_by "+e); 
      throw FoursoftException.getException(e,FILENAME," While Getting the created_by ");
    }
    return newCreatedBy;
  }
//@@ Ended by subrahmanyam for the Enhancement 154384 on 28/01/09.
  //Added by Rakesh
  public ArrayList getUserViewAllInformation2(String locationId) throws FoursoftException 
  {
      try
      {                        
          return userInterface.getUserViewAllInformation2(locationId);
      }
      catch(Exception exp)
      {
          //Logger.error(FILENAME,"Error in getUserViewAllInformation",exp);
          logger.error(FILENAME+"Error in getUserViewAllInformation"+exp);
          throw FoursoftException.getException(exp,FILENAME,"getUserViewAllInformation");
      }        
  }
}
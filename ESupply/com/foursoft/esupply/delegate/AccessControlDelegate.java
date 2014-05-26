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

import com.foursoft.eaccounts.common.java.AccountsCredentials;
import com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSession;
import com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSessionHome;
import com.foursoft.esupply.accesscontrol.exception.InvalidUserException;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;
import com.foursoft.esupply.common.exception.FoursoftException;
import com.foursoft.esupply.common.java.LookUpBean;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.Hashtable;

import javax.ejb.CreateException;

import javax.naming.NamingException;

/**
 * File			: AccessControlDelegate.java
 * sub-module 	: delegate
 * module 		: esupply
 * 
 * This file for BusinessDelegation of Logging Process
 * 
 * @author	Madhusudhan Rao. V, 
 * @date	11-08-2003
 */

public class AccessControlDelegate 
{
    private static Logger logger = null;
    
    public AccessControlDelegate() throws FoursoftException
    {
        logger  = Logger.getLogger(AccessControlDelegate.class);
        try
        {
            AccessControlSessionHome accessHome = (AccessControlSessionHome) LookUpBean.getEJBHome("AccessControlSessionBean");
            accessInterface = accessHome.create();
            //AccessControlSessionHome accessHome = (AccessControlSessionHome) LookUpBean.getEJBHome("java:comp/env/ejb/AccessControlSession");
            //accessInterface = accessHome.create();
        }
        catch(NamingException nExc)
        {
            //Logger.error(FILENAME,"Error in AccessControlDelegate Constructor ",nExc);
            logger.error(FILENAME+"Error in AccessControlDelegate Constructor "+nExc);
            //throw new FoursoftException(nExc,FILENAME,"AccessControlDelegate");
            throw FoursoftException.getException(nExc,FILENAME,"AccessControlDelegate");
        }
        catch(CreateException creExc)
        {
            //Logger.error(FILENAME,"Error in AccessControlDelegate Constructor ",creExc);
            logger.error(FILENAME+"Error in AccessControlDelegate Constructor "+creExc);
            throw new FoursoftException(creExc,FILENAME,"AccessControlDelegate");
        }
        catch(Exception rExc)
        {
            //Logger.error(FILENAME,"Error in AccessControlDelegate Constructor",rExc);
            logger.error(FILENAME+"Error in AccessControlDelegate Constructor"+rExc);
            throw new FoursoftException(rExc,FILENAME,"AccessControlDelegate");
        }    
    }

    public String getELogCurrencyId(String locationId, String userId) throws FoursoftException
    {
        try
        {
            //return accessInterface.getELogCurrencyId(locationId, userId);
            return accessInterface.getELogCurrencyId(locationId, userId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getELogCurrencyId",exp);
            logger.error(FILENAME+"Error in Retrieving the getELogCurrencyId"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getELogCurrencyId");
        }        
    }
    
	public ESupplyGlobalParameters getESupplyGlobalParameters(String userId, String password, String locationId, String userType) throws FoursoftException
    {
        try
        {
            return accessInterface.getESupplyGlobalParameters(userId, password, locationId, userType);
        }
        catch(InvalidUserException invalidExp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getESupplyGlobalParameters",invalidExp);
            logger.error(FILENAME+"Error in Retrieving the getESupplyGlobalParameters"+invalidExp);
            throw FoursoftException.getException(invalidExp,FILENAME,"getESupplyGlobalParameters",invalidExp.getMessage());
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getESupplyGlobalParameters",exp);
            logger.error(FILENAME+"Error in Retrieving the getESupplyGlobalParameters"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getESupplyGlobalParameters");
        }        
    }   //InvalidUserException;

	public String getETransCurrencyId(String wareHouse) throws FoursoftException
    {
        try
        {
            return accessInterface.getETransCurrencyId(wareHouse);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getETransCurrencyId",exp);
            logger.error(FILENAME+"Error in Retrieving the getETransCurrencyId"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getETransCurrencyId");
        }        
    }

    public Hashtable getOptionalRolePermissions(String userId, String userLocationId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.getOptionalRolePermissions(userId,userLocationId,locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getOptionalRolePermissions",exp);
            logger.error(FILENAME+"Error in Retrieving the getOptionalRolePermissions"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getOptionalRolePermissions");
        }        
    }
    
	public Hashtable getRolePermissions(String roleId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.getRolePermissions(roleId,locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getRolePermissions",exp);
            logger.error(FILENAME+"Error in Retrieving the getRolePermissions"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getRolePermissions");
        }        
    }

	public Integer getTerminalType(String locationId, String accessType) throws FoursoftException
    {
        try
        {
            return accessInterface.getTerminalType(locationId, accessType);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getTerminalType",exp);
            logger.error(FILENAME+"Error in Retrieving the getTerminalType"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getTerminalType");
        }        
    }
    
	public AccountsCredentials prepareAccountsCredentials(String locationId, String accessType) throws FoursoftException
    {
        try
        {
            return accessInterface.prepareAccountsCredentials(locationId, accessType);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the prepareAccountsCredentials",exp);
            logger.error(FILENAME+"Error in Retrieving the prepareAccountsCredentials"+exp);
            throw FoursoftException.getException(exp,FILENAME,"prepareAccountsCredentials");
        }        
    }
    
	public ESupplyGlobalParameters verifyELogCRMVRMUser(String userId, String password, String locationId, String userType) throws FoursoftException
    {
        try
        {
            return accessInterface.verifyELogCRMVRMUser(userId, password, locationId, userType);
        }
        catch(InvalidUserException iExp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifyELogCRMVRMUser",iExp);
            logger.error(FILENAME+"Error in Retrieving the verifyELogCRMVRMUser"+iExp);
            throw FoursoftException.getException(iExp,FILENAME,"verifyELogCRMVRMUser");
        } 
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifyELogCRMVRMUser",exp);
            logger.error(FILENAME+"Error in Retrieving the verifyELogCRMVRMUser"+exp);
            throw FoursoftException.getException(exp,FILENAME,"verifyELogCRMVRMUser");
        }
    }

	public ESupplyGlobalParameters verifyETransCRMUser(String userId, String password, String locationId, String userType) throws FoursoftException
    {
        try
        {
            return accessInterface.verifyETransCRMUser(userId, password, locationId, userType);
        }
        catch(InvalidUserException iExp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifyETransCRMUser",iExp);
            logger.error(FILENAME+"Error in Retrieving the verifyETransCRMUser"+iExp);
            throw FoursoftException.getException(iExp,FILENAME,"verifyETransCRMUser");
        } 
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifyETransCRMUser",exp);
            logger.error(FILENAME+"Error in Retrieving the verifyETransCRMUser"+exp);
            throw FoursoftException.getException(exp,FILENAME,"verifyETransCRMUser");
        }
    }
    
	public ESupplyGlobalParameters verifySPCRMVRMUser(String userId, String password, String locationId, String userType) throws FoursoftException
    {
        try
        {
            return accessInterface.verifySPCRMVRMUser(userId, password, locationId, userType);
        }
        catch(InvalidUserException iExp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifySPCRMVRMUser",iExp);
            logger.error(FILENAME+"Error in Retrieving the verifySPCRMVRMUser"+iExp);
            throw FoursoftException.getException(iExp,FILENAME,"verifySPCRMVRMUser");
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the verifySPCRMVRMUser",exp);
            logger.error(FILENAME+"Error in Retrieving the verifySPCRMVRMUser"+exp);
            throw FoursoftException.getException(exp,FILENAME,"verifySPCRMVRMUser");
        }        
    } 

	public boolean getPasswordModifiedDate(String userId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.getPasswordModifiedDate(userId, locationId);
        }
/*        
        catch(InvalidUserException iExp)
        {
            Logger.error(FILENAME,"Error in Retrieving the getPasswordModifiedDate",iExp);
            throw FoursoftException.getException(iExp,FILENAME,"getPasswordModifiedDate()");
        }
*/
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getPasswordModifiedDate",exp);
            logger.error(FILENAME+"Error in Retrieving the getPasswordModifiedDate"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getPasswordModifiedDate()");
        }        
    } 

	public boolean lockUser(String userId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.lockUser(userId,locationId);
        }
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in locking User",exp);
            logger.error(FILENAME+"Error in locking User"+exp);
            throw FoursoftException.getException(exp,FILENAME,"lockUser()");
        }        
    } 

	public boolean getWarningCountExceeded(String userId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.getWarningCountExceeded(userId, locationId);
        }
/*        
        catch(InvalidUserException iExp)
        {
            Logger.error(FILENAME,"Error in Retrieving the getWarningCount",iExp);
            throw FoursoftException.getException(iExp,FILENAME,"getWarningCount()");
        }
*/
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getWarningCount",exp);
            logger.error(FILENAME+"Error in Retrieving the getWarningCount"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getWarningCount()");
        }        
    }
	public boolean getLoginCount(String userId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.getLoginCount(userId, locationId);
        }
/*        
        catch(InvalidUserException iExp)
        {
            Logger.error(FILENAME,"Error in Retrieving the getWarningCount",iExp);
            throw FoursoftException.getException(iExp,FILENAME,"getWarningCount()");
        }
*/
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getWarningCount",exp);
            logger.error(FILENAME+"Error in Retrieving the getWarningCount"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getWarningCount()");
        }        
    }
	public int getNoFailAttempts() throws FoursoftException
    {
        try
        {
            return accessInterface.getNoFailAttempts();
        }
/*        
        catch(InvalidUserException iExp)
        {
            Logger.error(FILENAME,"Error in Retrieving the getWarningCount",iExp);
            throw FoursoftException.getException(iExp,FILENAME,"getWarningCount()");
        }
*/
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getWarningCounts",exp);
            logger.error(FILENAME+"Error in Retrieving the getWarningCount"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getWarningCount()");
        }        
    }
	//@@Added for no'of users logged	
	public boolean logoutUpdate(String userId, String locationId) throws FoursoftException
    {
        try
        {
            return accessInterface.logOutUpdate(userId, locationId);
        }
/*        
        catch(InvalidUserException iExp)
        {
            Logger.error(FILENAME,"Error in Retrieving the getWarningCount",iExp);
            throw FoursoftException.getException(iExp,FILENAME,"getWarningCount()");
        }
*/
        catch(Exception exp)
        {
            //Logger.error(FILENAME,"Error in Retrieving the getWarningCount",exp);
            logger.error(FILENAME+"Error in updating logout time"+exp);
            throw FoursoftException.getException(exp,FILENAME,"getWarningCount()");
        }        
    }
	//@@Added for no'of users logged
    //private AccessControlSession accessInterface;
    private AccessControlSession accessInterface;
    private final static String FILENAME = "AccessControlDelegate.java";   
}
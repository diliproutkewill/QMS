/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.java;

import java.util.*;
import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSession;
import com.foursoft.esupply.accesscontrol.ejb.sls.UserRoleRegistrationSessionHome;
import com.foursoft.esupply.common.util.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import com.foursoft.esupply.common.java.LookUpBean;

/**
 * File			: CacheManager.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is used to manage the Cache Objects
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	2002-04-25
 */
public class CacheManager 
{
	private static java.util.HashMap cacheHashMap = new java.util.HashMap();

    static
    {
        try
        {
            Thread threadCleanerUpper = new Thread(
            new Runnable()
            {
              int milliSecondSleepTime = 3600000;

              public void run()
              {
                try
                {
                  while (true)
                  {
                    //System.out.println("ThreadCleanerUpper Scanning For Expired Objects...");

                    java.util.Set keySet = cacheHashMap.keySet();

                    java.util.Iterator keys = keySet.iterator();
                    while(keys.hasNext())
                    {
                      Object key = keys.next();
                      Cacheable value = (Cacheable)cacheHashMap.get(key);
                      if (value.isExpired())
                      {
                        cacheHashMap.remove(key);
                        //System.out.println("ThreadCleanerUpper Running. Found an Expired Object in the Cache.");
                      }
                    }

                    Thread.sleep(this.milliSecondSleepTime);
                  }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                return;
              } 
            }
		); /* End class definition and close new thread definition */

            threadCleanerUpper.setPriority(Thread.MIN_PRIORITY);
            threadCleanerUpper.start();
        }
        catch(Exception e)
        {
              //System.out.println("CacheManager.Static Block: " + e);
        }
    } /* End static block */



	public CacheManager()
	{
	}

	public static void putCache(Cacheable object)
	{
		cacheHashMap.put(object.getIdentifier(), object);
	}

	public static Cacheable getCache(Object identifier)
	{
		Cacheable cacheObject = (Cacheable)cacheHashMap.get(identifier);

		if(cacheObject == null && identifier.toString().equals("transactions-list") )
		{
			Hashtable	txIdDescription = new Hashtable();
			UserRoleRegistrationSessionHome home = null;
			UserRoleRegistrationSession remote = null;
			//InitialContext ic = null;
			try
			{
				//ic = new InitialContext();
				//home = (UserRoleRegistrationSessionHome) ic.lookup( "UserRoleRegistrationSessionBean" );
				home	=	(UserRoleRegistrationSessionHome)	LookUpBean.getEJBHome("UserRoleRegistrationSessionBean");
				remote	=	(UserRoleRegistrationSession)		home.create();
				
				txIdDescription = (Hashtable)remote.getTransactionIdAndDescription();
				cacheObject	= new CachedTransactions(txIdDescription, "transactions-list", 0);  
				putCache(cacheObject);
			}
			catch( Exception ex )
			{
				//System.out.println( "RolePermissionControllerJBean:RolePermissionControllerJBean()" + "error in getting txIdDescription : " + ex.toString() );
			}
		}
		if (cacheObject == null)
		  return null;
		

		if (cacheObject.isExpired())
		{
		  cacheHashMap.remove(identifier);
		  return null;
		}
		else
		{
		  return cacheObject;
		}
	}
	
}
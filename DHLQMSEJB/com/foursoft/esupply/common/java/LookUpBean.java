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
/**
 * File			: LookUpBean.java
 * sub-module 	: Common
 * module 		: esupply
 * 
 * This is a facilitator bean and access to it is encapsulated in the ESupplyGlobalParameters.
 * It is used to cache the Home Interfaces looked up by the clients so that they can be reused.
 * 
 * @author	Amit Parekh 
 * @date	29-07-2002
 *
 * Modified History 
 * Amit Parekh	21/10/2002		This file was modified to handle instance level EJB Home caching
 * 								for session beans. An object of this class will be maintained by
 *                              each instance of an session bean.
 * 								The global datasource name 'jdbc/DB' or otherwise is now
 *								maintained globally in FourSoftConfig.java
 *                        		Also the Home lookup-once-and-cache pattern is implemented
 */
 
import java.util.HashMap;
import java.util.Collections;
import javax.naming.InitialContext;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.NamingException;
import java.sql.Timestamp;
import javax.sql.DataSource;
import com.foursoft.esupply.common.java.FoursoftConfig;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


public class LookUpBean extends Object
{

	private static String fileName = "LookUpBean.java";
  private static Logger logger = null;

	
	private InitialContext	initCntx	=	null;
	private	HashMap			hmEjbHomes	=	new HashMap(5);

	public LookUpBean() {
  logger  = Logger.getLogger(LookUpBean.class);
		try {
			initCntx	=	new InitialContext();
		} catch(Exception e){
			//Logger.error( fileName, "Error instantiating Look Up Bean.");
      logger.error( fileName+ "Error instantiating Look Up Bean.");
		}
	}

	/**
	 * This method is used as a look up pattern for Session Beans. An object of this class is maintained
	 * as an instance variable in the session bean and this method is used to lookup and cache
	 * EJB homes for the particular instance of the session bean.
	 */
	 
    public EJBHome lookupEjbHome(String jndiNameOfBean) throws NamingException
	{
		// First search in the instance level cache
		EJBHome ejbHome = (EJBHome) hmEjbHomes.get( jndiNameOfBean );
		
		// If its not in the instance cache,
		if(ejbHome == null) 
		{
			// look it up
			ejbHome = (EJBHome) initCntx.lookup( jndiNameOfBean );
			
			// After looking up, put it in the Hasttable for future reference calls
			// from the same ejb instance to whcih this object is attached
			if(ejbHome != null) {
				hmEjbHomes.put( jndiNameOfBean, ejbHome );
				//System.out.println("  Put In CACHE");
			}
			
		}
		
		return ejbHome;
    }

	/**
	 * This method is used as a look up pattern for Session Beans. An object of this class is maintained
	 * as an instance variable in the session bean and this method is used to lookup and cache
	 * EJB homes for the particular instance of the session bean.
	 */
	 
    public DataSource lookupDataSource() throws NamingException
	{
		DataSource  dataSource = (DataSource) hmEjbHomes.get( "dataSource" );
		// If its not in the instance Hashtable,
		if(dataSource == null) 
		{
			dataSource = (DataSource) initCntx.lookup( FoursoftConfig.DATA_SOURCE );
			
			// After looking up, put it in the Hasttable for future reference calls
			if(dataSource != null) {
				hmEjbHomes.put( "dataSource", dataSource );
				//System.out.println("  Put In CACHE");
			}
			
		}
		
		return dataSource;
    }


	private static HashMap hashMap = new HashMap();

    public static synchronized EJBLocalHome getEJBLocalHome(String jndiNameOfEjb) throws NamingException
	{
		long msStart =	(new Timestamp( new java.util.Date().getTime() )).getTime(); 
		//System.out.println();
		//System.out.println("LOOKUP BEAN STARTS : For '"+jndiNameOfEjb+"' : msStart = "+msStart);
		// First look for the required Home reference in the Hashtable
		EJBLocalHome ejbHome = (EJBLocalHome) hashMap.get( jndiNameOfEjb );
		
		// If its not in the global Hashtable,
		if(ejbHome == null) 
		{
			//System.out.println("  First Lookup");
			// Look up the Home in the JNDI
			InitialContext ic = null;
			
			ic =	(InitialContext) hashMap.get("ic");
			
			if(ic==null) {
				ic = new InitialContext();
				hashMap.put("ic", ic);
			}
			
			ejbHome = (EJBLocalHome) ic.lookup( jndiNameOfEjb );
			
			// After looking up, put it in the Hasttable for future reference calls
			if(ejbHome!=null) {
				hashMap.put( jndiNameOfEjb, ejbHome);
				//System.out.println("  Put In CACHE");
			}
			
		} else {
			//EJBHome ejbHomeClone
			//System.out.println("  CACHED HOME Found");
		}

		//System.out.println("  HOME CACHE size = "+hashMap.size() );
		long msEnd =	(new Timestamp( new java.util.Date().getTime() )).getTime(); 
		//System.out.println("LOOKUP BEAN ENDS : msEnd = "+msEnd+"..... Time taken = '"+( msEnd - msStart )+"' milliseconds\n");
		//System.out.println();
		
		return ejbHome;
    }
    

    public static synchronized EJBHome getEJBHome(String jndiNameOfEjb) throws NamingException
	{
		long msStart =	(new Timestamp( new java.util.Date().getTime() )).getTime(); 
		//System.out.println();
		//System.out.println("LOOKUP BEAN STARTS : For '"+jndiNameOfEjb+"' : msStart = "+msStart);
		// First look for the required Home reference in the Hashtable
		EJBHome ejbHome = (EJBHome) hashMap.get( jndiNameOfEjb );
		
		// If its not in the global Hashtable,
		if(ejbHome == null) 
		{
			//System.out.println("  First Lookup");
			// Look up the Home in the JNDI
			InitialContext ic = null;
			
			ic =	(InitialContext) hashMap.get("ic");
			
			if(ic==null) {
				ic = new InitialContext();
				hashMap.put("ic", ic);
			}
			
			ejbHome = (EJBHome) ic.lookup( jndiNameOfEjb );
			
			// After looking up, put it in the Hasttable for future reference calls
			if(ejbHome!=null) {
				hashMap.put( jndiNameOfEjb, ejbHome);
				//System.out.println("  Put In CACHE");
			}
			
		} else {
			//EJBHome ejbHomeClone
			//System.out.println("  CACHED HOME Found");
		}

		//System.out.println("  HOME CACHE size = "+hashMap.size() );
		long msEnd =	(new Timestamp( new java.util.Date().getTime() )).getTime(); 
		//System.out.println("LOOKUP BEAN ENDS : msEnd = "+msEnd+"..... Time taken = '"+( msEnd - msStart )+"' milliseconds\n");
		//System.out.println();
		
		return ejbHome;
    }
	
}

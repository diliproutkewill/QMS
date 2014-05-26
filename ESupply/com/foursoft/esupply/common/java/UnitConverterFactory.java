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
 * File			: UnitConverterFactory.java
 * sub-module 	: Common
 * module 		: eSupply
 * 
 * This is a Factory Class which is responsible for returning an instance of an object
 * that implements the UnitConverter interface. The User of the class does not have to
 * worry about the instantiation details of the nit Converter object. They are handled
 * in this class
 * 
 * @author	Amit Parekh 
 * @date	27-12-2002
 */

import java.math.BigDecimal;

import com.foursoft.esupply.common.java.UnitConverter;
import com.foursoft.esupply.common.java.UnitConversion;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.lang.SecurityException;

public final class UnitConverterFactory  
{

	/**
	 * @variable fileName	The name of this File
	 */
	private static final String	fileName	=	"UnitConverterFactory.java";
  private static Logger logger = null;
  
	private UnitConverterFactory() 
  {
    logger  = Logger.getLogger(UnitConverterFactory.class);
	}

	/**
	  *	Returns an instance of this class
	  *
	  * @return		UnitConverterFactory	An instance of this class
	  *
	  */
	public static UnitConverterFactory getUnitConverterFactory() 
    {
		return new UnitConverterFactory();
	}

    /**
     * Return an object of a class that implements the interface UnitConverter 
	 * 
	 * @return		UnitConverter	An object of a class that implements the interface UnitConverter
     *
     */
	public UnitConverter getUnitConverter() 
    {

		UnitConversion	uc	=	null;
		String	UnitConversionClass	=	"com.foursoft.esupply.common.java.UnitConversion";
		
		try
		{
			Class conversionClass	=	null;
			
			//System.out.println("	Looking for Class '"+UnitConversionClass+"'");
			ClassLoader	classLoader		=	getContextClassLoader();

			if(classLoader != null) 
            {
				//Logger.info(fileName, "	Class Loader Found");
				try 
                {
					//Logger.info(fileName, "	loadClass was called");
					conversionClass	=	classLoader.loadClass( UnitConversionClass) ;
				} 
                catch( ClassNotFoundException ex ) 
                {
					//Logger.info(fileName, "	forName was called");
					conversionClass	=	Class.forName( UnitConversionClass );
				}
			}

			//Logger.info(fileName, "	The Class '"+UnitConversionClass+"' Found and Loaded.");
			
			if(conversionClass!=null && !conversionClass.isInterface()) 
            {
				uc	=	(UnitConversion) conversionClass.newInstance();
				//Logger.info(fileName, "	The Class object was instantiated.");
				uc.setInternalValueRMode( BigDecimal.ROUND_HALF_EVEN );
				uc.setConvertedValueRMode( BigDecimal.ROUND_HALF_EVEN );
				uc.setVolumetricWeightRMode( BigDecimal.ROUND_HALF_EVEN );
			}
			
		} 
        catch (ClassNotFoundException cnfEx) 
        {
			//Logger.error(fileName, "	The Class '"+UnitConversionClass+"' Was Not Found", cnfEx);
      logger.error(fileName+ "	The Class '"+UnitConversionClass+"' Was Not Found"+ cnfEx);
		}
        catch (IllegalAccessException illEx) 
        {
			//Logger.error(fileName, "	Class is Inaccessible to You", illEx);
      logger.error(fileName+ "	Class is Inaccessible to You"+ illEx);
		} 
        catch (InstantiationException insEx) 
        {
			//Logger.error(fileName, "	Instance could not be created", insEx);
      logger.error(fileName+ "	Instance could not be created"+ insEx);
		}

		return uc;
	}



    /**
     * Return the thread context class loader if available; else returns null.
     * 
     * The thread context class loader is available for JDK 1.2
     * or later, if certain security conditions are met.
	 * 
	 * @return		ClassLoader	the class loader that loaded the class for which the current thread is executing
     *
     */

    protected static ClassLoader getContextClassLoader()
    {
        ClassLoader classLoader = null;

        try 
        {
            // Are we running on a JDK 1.2 or later system?
            Method method = UnitConverterFactory.class.getMethod("getContextClassLoader", null);

            // Get the thread context class loader (if there is one)
            try 
            {
                classLoader = (ClassLoader) method.invoke(Thread.currentThread(), null);
            } 
            catch (InvocationTargetException e) 
            {
                /**
                 * InvocationTargetException is thrown by 'invoke' when
                 * the method being invoked (getContextClassLoader) throws
                 * an exception.
                 * 
                 * getContextClassLoader() throws SecurityException when
                 * the context class loader isn't an ancestor of the
                 * calling class's class loader, or if security
                 * permissions are restricted.
                 * 
                 * In the first case (not related), we want to ignore and
                 * keep going.  We cannot help but also ignore the second
                 * with the logic below, but other calls elsewhere (to
                 * obtain a class loader) will trigger this exception where
                 * we can make a distinction.
                 */

                if (e.getTargetException() instanceof SecurityException) 
                {
                    ;  // ignore
                } 
                else 
                {
                    // Capture 'e.getTargetException()' exception for details
                    // alternate: log 'e.getTargetException()', and pass back 'e'.
                    //throw new InvocationTargetException
                      //  ("Unexpected InvocationTargetException", e.getTargetException());
                }
            }
        } 
        catch (NoSuchMethodException e) 
        {
            // Assume we are running on JDK 1.1
            classLoader = UnitConverterFactory.class.getClassLoader();
        }
        catch(Exception exp)
        {
            // Class Loading Exception
        }

        // Return the selected class loader
        return classLoader;
    }


}
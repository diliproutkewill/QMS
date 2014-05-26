/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
package com.foursoft.esupply.common.dao;

import java.sql.Connection; 
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ejb.EJBException;

import com.foursoft.esupply.common.exception.DBSysException;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.java.FoursoftConfig;

/**
 * File			: BaseDAOImpl.java
 * sub-module	: common
 * module		: esupply
 *
 * This is a Base Implementation of DAO 
 * This class encapsulates connection Creation and Datasource initilization logic
 * which holds the Connection object also  (protected)
 *
 * @author	Madhusudhan Rao. P, 
 * @date	1-04-2001
 *
 * Modified History 
 * Amit Parekh	21/10/2002		The global datasource name 'jdbc/DB' or otherwise is now
 *								maintained globally in FourSoftConfig.java
 *                        		Also the Home lookup-once-and-cache pattern is implemented
 */


public class BaseDAOImpl
{
	protected	transient		Connection connection	= null;
    private		transient		DataSource datasource   = null;
	public BaseDAOImpl()
	{
        try 
		{
            InitialContext ic = new InitialContext();
            datasource  = (DataSource) ic.lookup( FoursoftConfig.DATA_SOURCE ); 
        } 
		catch (NamingException ne) 
		{
            throw new EJBException(	"NamingException while looking " +
										"up DataSource Connection " + 
                                        FoursoftConfig.DATA_SOURCE+" \n" + ne.getMessage());
        }
	}

    protected void getConnection() throws DBSysException 
	{         
        try 
		{
           	connection = datasource.getConnection();
        } 
		catch (SQLException se) 
		{
            throw new DBSysException("SQLException while getting " +
                                      "DB connection : \n" + se.getMessage());
        }
    }
	
}

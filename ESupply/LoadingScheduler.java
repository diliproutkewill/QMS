

/**
 * 
 * Copyright (c) 2000-2009 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 * 
 * File       : LoadingScheduler.java
 *
 * Sub-Module : Startup Servlet
 * Module     : eTrans
 * 
 * Purpose of file: Startup Servlet for initailizing, loading Scheduler
 * @author subrahmanyamk
 * 
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.util.ConnectionUtil;


public class LoadingScheduler extends HttpServlet {

	public static final String QUARTZ_FACTORY_KEY = "org.quartz.impl.StdSchedulerFactory.KEY";
	private boolean performShutdown = true;
	private Scheduler sched = null;
	private transient DataSource dataSource = null;
	private static final String FILE_NAME		= "LoadingScheduler.java";
	  private static Logger logger = null;
	  public static String jobName	="";
	public void init(ServletConfig cfg) throws javax.servlet.ServletException 
	{
		super.init(cfg);
		logger  = Logger.getLogger(LoadingScheduler.class);
			try
		    {
		      InitialContext ic	= new InitialContext();
		      dataSource			=(DataSource) ic.lookup("java:comp/env/jdbc/DB");
		    }
		    catch(NamingException nmEx)
		    {
		      logger.error(FILE_NAME+" naming exception "+nmEx.toString());
		    }

		
		StdSchedulerFactory factory;

		File file               = null;
		InputStream is          = null;
		String shutdownPref     = null;
		Properties quartz_props = null;
		Properties econ_props   = null;
		String realPath         = null;
		String	schedId			= null;
		//String	jobName			= "QuoteShop";
		String	cronExp			= null;
		String	seconds			= "00";
		String	minuts			= "00";
		String	hours			= "10";    
		String	dayOfMonth		= "?";
		String	month			= "*";
		String	dayOfWeek		= "1-7";
		JobDetail job           = null;
		String	jobClass		= "JobSchedulerProcess";
		CronTrigger cronTrigger = null;
		logger.info("Scheduler Started............");
		Statement 	stmt 	= null;
		Connection connection		=	null;
		ResultSet rs				=	null;
		String currencySchedularDtls		=	"SELECT * FROM QMS_QUARTZ_SCHEDULAR_CONFIG WHERE JOBID=1 AND JOBNAME='QuoteShop' AND SCHEDULAR_NAME='currency'";
		try
		{     
			try
			{
				connection = this.getConnection();
				stmt	= connection.createStatement();
				rs = stmt.executeQuery(currencySchedularDtls);
				if(rs.next())
				{
					jobName		= 	rs.getString("JOBNAME");
					seconds		= 	rs.getString("TIME_SEC");
					minuts		=	rs.getString("TIME_MIN");;
					hours		=	rs.getString("TIME_HRS");
					dayOfMonth	=	rs.getString("TIME_DAYOFMONTH");
					month		=	rs.getString("TIME_MONTH");
					dayOfWeek	=	rs.getString("TIME_DAYOFWEEK");
			
					logger.info("jobName , sec, min, hrs, dayOfMon, month,dayOfwk :"+jobName+","+seconds+","+minuts+","+hours+","+dayOfMonth+","+month+","+dayOfWeek);
				}
			}
			catch(Exception e)
			{
				logger.error(FILE_NAME+" Error while getting the configuration details for the currencySchedular:"+e.getMessage());
				e.printStackTrace();
				throw new ServletException(e);

			}
			finally
			{
				try
				{
					ConnectionUtil.closeConnection(connection, stmt, rs);
				}
				catch(Exception e)
				{
					logger.error(FILE_NAME+" Error while closing the connection & statement & ResultSet.");
				}
			}
			shutdownPref = cfg.getInitParameter("shutdown-on-unload");
			if (shutdownPref != null)
				performShutdown = Boolean.valueOf(shutdownPref).booleanValue();
			
			// IMPORTANT
			// For SunServer/Glassfish & OC4J [will not work in WebLogic]
			//realPath = cfg.getServletContext().getRealPath("/WEB-INF/quartz.properties");//by subarhmanyam
			// For WebLogic & OC4J [Will not work in SunServer/Glassfish]
			//this configuration required if quartz.properties file is out side the quartz.jar
	/*		if(realPath == null)
				realPath = cfg.getServletContext().getResource("/WEB-INF/classes/quartz.properties").getPath();
				//realPath = cfg.getServletContext().getResource("/WEB-INF/quartz.properties").getPath();
			file = new File(realPath);
			is = new FileInputStream(file);
			quartz_props = new Properties();
			quartz_props.load (is);*/
			//factory = new StdSchedulerFactory(quartz_props); 
	//ended for quartz.properties outside the quartz.jar file
			factory = new StdSchedulerFactory();// if quartz.properties is inside the quartz.jar
			cfg.getServletContext().setAttribute(QUARTZ_FACTORY_KEY, factory);      

			// Initially SCHEDULER -- will be in PAUSE state so we have to start the scheduler -- 
			sched = factory.getScheduler();
			

			logger.info("Scheduler Started     "+ sched.toString());
			logger.info("Scheduler Name        "+ sched.getSchedulerName());
			logger.info("Scheduler InstanceId  "+ sched.getSchedulerInstanceId());

			file = null;
			quartz_props = null;
			econ_props = null;
			//is.close();

			//TriggerScheduler Classes
			factory = (StdSchedulerFactory)getServletConfig().getServletContext().getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
			sched = factory.getScheduler();
			schedId = sched.getSchedulerInstanceId();
			cronExp=seconds+" "+minuts+" "+hours+" "+dayOfMonth+" "+month+" "+dayOfWeek;
			job = new JobDetail(jobName,schedId,Class.forName(jobClass), false, true, false);
			
			job.setGroup("JG");
			job.setDurability(true);
			job.setJobDataMap(new JobDataMap());
			//sched.deleteJob(jobName,"JG");	
			sched.addJob(job,true);

			//job=sched.getJobDetail(jobName,"JG");
			
			cronTrigger = new  CronTrigger("001", "TG",jobName , "JG", cronExp); //Frequency
			cronTrigger.setVolatility(false);

			sched.scheduleJob(cronTrigger);
			logger.info("Before calling  execute().......");  
			sched.start();
		} 
		catch (Exception e) 
		{			
			logger.info("exception in loading scheduler "+e);
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	  private Connection getConnection() throws SQLException
		{
			return dataSource.getConnection();
		}
	  
	public void destroy() 
	{
		logger.info("before destroy---");
		try
		{
		sched.deleteJob(jobName, "JG");
		}
		catch(Exception e)
		{
			logger.error(FILE_NAME+" Error while Delete the jobs and corresponding Triggers.");
			e.printStackTrace();
		}
		if ( !performShutdown )
		return;      
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException 
	{
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)		throws ServletException, IOException 
	{
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}

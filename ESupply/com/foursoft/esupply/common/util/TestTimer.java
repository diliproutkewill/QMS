package com.foursoft.esupply.common.util;

import java.io.*;
import java.util.*;
import com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSession;
import com.foursoft.esupply.accesscontrol.ejb.sls.AccessControlSessionHome;
import javax.naming.InitialContext;

/**
 * @author  ramakumar
 */

 
 
public class TestTimer extends TimerTask	
{
	public TestTimer()
	{
		call();
	}
	
	public int i=0;
	
	Runtime rt = Runtime.getRuntime();

	AccessControlSessionHome accHome = null;
	AccessControlSession accRemote	 = null;
	
	private void call()
	{
		try
		{
			InitialContext ic = new InitialContext();
			accHome = (AccessControlSessionHome)ic.lookup("AccessControlSessionBean");	
			accRemote = accHome.create();
			
			
		}
		catch(Exception e)
		{
			System.out.println(e);
		}	
	}
		
	
	public void run()
	{
		try
		{
			
			
			//here i have to call remote method
			boolean status = accRemote.getPasswordModifiedDate("SESHU","FOURSOFT");

			if(status)
				System.out.println("Send Mail");//invoke methos in the bean to send mail
			else
				System.out.println("No Mail");
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}	
		
		
	}	
	
}

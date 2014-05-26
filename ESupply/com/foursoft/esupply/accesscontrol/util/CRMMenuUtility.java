/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
package com.foursoft.esupply.accesscontrol.util;

import java.io.*;
import java.sql.*;
import java.util.Hashtable;
import java.util.*;

import com.foursoft.esupply.accesscontrol.java.UserInterface;
import com.foursoft.esupply.accesscontrol.java.MenuModule;

/**
 * File			: CRMMenuUtility.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This java class  is used to generate the menu according to the borwser.
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 */
public class CRMMenuUtility
{
	public CRMMenuUtility()
	{
	}
	/*
	 * This method will generate script for InternetExplorer.
	 */
	public String[] getScript(ArrayList tabList,Hashtable alevel)
	{
		String[] scriptArray = new String[3];
		String NL = "\n";
		StringBuffer tableStr = new StringBuffer();
		StringBuffer menuStr = new StringBuffer("<table width=100% bgcolor=#006699 >"+NL+"<tr>");
		StringBuffer tabStr   = new StringBuffer();
		
		int index = 0;
		int tabListSize	=	 tabList.size();
		for(int i=0;i<tabListSize;i++)
		{
			MenuModule module = (MenuModule)tabList.get(i);
			String moduleName		= module.moduleName;
			ArrayList uiList		= module.uiList;
			boolean uiExistsInTab	= false;
			int uiListSize		= uiList.size();
			for(int j=0;j<uiListSize;j++)
			{
				UserInterface ui = (UserInterface)uiList.get(j);
				int actLevel = ui.accessLevel;
				String process = ui.process;
				String url 	   = ui.url;
				String txid	   = ui.txid;
				boolean permissionFlag = false;
				if(alevel.containsKey(txid))
				{
					int accessLevel = ((Integer)alevel.get(txid)).intValue();
					switch(accessLevel)
					{
						case 1:
							if(actLevel==1)
								permissionFlag = true;
							break;
						case 2:
							if(actLevel==2)
								permissionFlag = true;
							break;
						case 3:
							if(actLevel==1||actLevel==2)
								permissionFlag = true;
							break;
						case 4:
							if(actLevel==4)
								permissionFlag = true;
							break;
						case 5:
							if(actLevel==1||actLevel==4)
								permissionFlag = true;
							break;
						case 6:
							if(actLevel==2||actLevel==4)
								permissionFlag = true;
							break;
						case 7:
							if(actLevel==1||actLevel==2||actLevel==4)
								permissionFlag = true;
							break;
					}
				}
					
					if(permissionFlag)
					{							
						tabStr.append("<tr><td bgcolor=#006699 onMouseOver=\"change(this,0);\" onMouseOut=\"change(this,1);\"  class=\"menuItem\"><a href=\""+url+"\">"+process+"</a></td></tr>");
						if(!uiExistsInTab)
						{
							uiExistsInTab = true;
						}
					}
			}
			if(uiExistsInTab)
			{
				//System.out.println(" index :"+index+" tabList.size()"+tabList.size()  );
				if(index!=0)
				{	
					menuStr.append("<td>&nbsp;</td>");				
					menuStr.append("<td class=tdClass noWrap>|</td>");
				}	
				menuStr.append("<td>&nbsp;</td>");	
				menuStr.append("<td class=tdClass  id=tdItem"+index+" noWrap><a href=\"javascript:void(0)\" onMouseOver=\"showMenu("+index+")\">"+moduleName+"</a></td>");
				tableStr.append("<div style=\"position:absolute; top:25px; visibility:hidden;\"  id=divMenu"+index+" onMouseOver=\"showMenu("+index+")\" onMouseOut=\"hideAll(1);\">"+NL+"<table cellpadding=\"0\" cellspacing=\"0\" >");
				tableStr.append(tabStr.toString());
				tableStr.append("</table></div>");
				tabStr = new StringBuffer();
				
					index++;
			}
		}
		menuStr.append("<td class=tdClass width=100% onMouseOver=\"hideAll(1);\">&nbsp;</td>");
		menuStr.append("</tr></table>");
		scriptArray[0] = menuStr.toString();
		scriptArray[1] = tableStr.toString();
		scriptArray[2] = ""+index+"";
		return scriptArray;
	}
}

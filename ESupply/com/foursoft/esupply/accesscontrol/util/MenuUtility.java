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
 * File			: MenuUtility.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This java class  is used to generate the menu according to the borwser.
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 * 
 * Modified History
 * Modified By		Modified On		Reason
 * Amit Parekh		23.07.2002		Hotkey incorporated as an attribute
 */
public class MenuUtility
{
	
	public static String MENU_IE_STR = "<div id=\"menuBar\" style=\"position:absolute; top:0px; left:0px; width:100%;\">"
											+"<table cellpadding=\"0\" cellspacing=\"0\">"
											+"<tr>";
	
	public static String MNEU_NS_STR = "<div id=\"menuBar\" style=\"position:absolute; top:0px; left:0px; width:100%;\">"
											+"<table cellpadding=\"0\" cellspacing=\"0\">"
											+"<tr>";
	
	public static String SCRIPT_NS_STR ="document.addEventListener(\"mouseout\", offPage, true);"
											+"function init() {"
											+"var menuBarTop = document.getElementById(\"menuBar\").offsetHeight;";
	
	public static String SCRIPT_IE_STR = "document.onmouseout = offPage;"
											+"function offPage(event){ var offScreen;"
											+"offScreen = (window.event.clientY < 5) ? 1 : 0;"
											+"if (offScreen) hideOthers(20,20);} function init() {"
											+"var menuBarTop = document.getElementById(\"menuBar\").offsetHeight;";
	public static String br = "\n";
	
	public MenuUtility()
	{
	}
	/*
	 * This method will generate script for InternetExplorer.
	 */
	public String[] getIEScript(ArrayList tabList,Hashtable alevel)
	{
		String[] scriptArray = new String[4];
		
		StringBuffer tableStr = new StringBuffer();
		StringBuffer tabStr   = new StringBuffer();
		StringBuffer scriptStr = new StringBuffer(SCRIPT_IE_STR);
		StringBuffer menuStr   = new StringBuffer(MENU_IE_STR);
		ArrayList	keyList		= new ArrayList();	// list of hot keys
		
		int index = 0;
		int tabListSize			=	tabList.size();

		for(int i=0;i<tabListSize;i++)
		{
			MenuModule	module			= (MenuModule)tabList.get(i);
			String		moduleName		= module.moduleName;
			String 		hotKey			= module.hotKey;	
			ArrayList	uiList			= module.uiList;
			boolean		uiExistsInTab	= false;
			int 			tdCount 		= 1;	// To Count the menubar items
			//int 			tdCount 		= 0;	// To Count the menubar items
			
			int uiListSize 	= uiList.size();
			for(int j=0;j<uiListSize;j++)
			{
				UserInterface ui = (UserInterface)uiList.get(j);
				
				int		actLevel		= ui.accessLevel;
				String	process			= ui.process;
				String	url				= ui.url;
				String	txid			= ui.txid;
				boolean permissionFlag	= false;
				
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
						case 8:
							if(actLevel==8)
								permissionFlag = true;
							break;
						case 9:
							if(actLevel==1||actLevel==8)
								permissionFlag = true;
							break;
						case 10:
							if(actLevel==2||actLevel==8)
								permissionFlag = true;
							break;
						case 11:
							if(actLevel==1 || actLevel==2||actLevel==8)
								permissionFlag = true;
							break;
						case 12:
							if(actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 13:
							if(actLevel==1  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 14:
							if(actLevel==2  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 15:
							if(actLevel==1 || actLevel==2  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
					}
				}
					
				if(permissionFlag)
				{
					tableStr.append("<tr>"+br);
					tableStr.append("<td onMouseOver = highLight(this) onMouseOut = lowLightAll() id=\"tdsubMenu"+index+"0"+tdCount+"\" class=\"menuItem\">"+br);
					tableStr.append("<a id=\"subMenu"+index+"0"+tdCount+"\" class=\"menuLink\" href=\""+url+"\">"+process+"</a>"+br);
					tableStr.append("</td>"+br);
					tableStr.append("</tr>"+br);
					//tableStr.append("<tr>");
					//tableStr.append("<td class=\"menuItem\">");
					//tableStr.append("<a class=\"menuLink\" href=\""+url+"\" target=\"text\">"+process+"</a>");
					//tableStr.append("</td>");
					//tableStr.append("</tr>");

					if(!uiExistsInTab)
					{
						uiExistsInTab = true;
					}
					tdCount++;
				}

			}
			if(uiExistsInTab)
			{
				keyList.add(hotKey);
				//System.out.println("IN WRITING TABS");
				menuStr.append("<td id=\"menuBar"+index+"\" class=\"menuButton\" style=\"background:  #D4D0C8; border-left-width:2px;\" onmouseover=\"showSubMenu("+index+",0);\">"+br);
				//menuStr.append("<td id=\"menuBar"+index+"\" class=\"menuButton\" style=\"background:  #D4D0C8; border-left-width:2px;\" onmouseover=\"showSubMenu("+index+",0);\">");
				
				if(hotKey!=null && hotKey.length() > 0) {
					
					String underLinedModuleName = "";
					
					//String	tempModuleName	=	new String( moduleName );
					
					//tempModuleName	=	tempModuleName.toUpperCase();
					String	uCaseHotKey = hotKey.toUpperCase();
					String	lCaseHotKey = hotKey.toLowerCase();
					
					String underLineduCaseHotKey = "<u>"+uCaseHotKey+"</u>";
					String underLinedlCaseHotKey = "<u>"+lCaseHotKey+"</u>";					
					
					int	indexOfuCaseHotKey	=	moduleName.indexOf( uCaseHotKey );
					int	indexOflCaseHotKey	=	moduleName.indexOf( lCaseHotKey );
					
					if(indexOfuCaseHotKey != -1) {
						String	before	=	moduleName.substring(0,indexOfuCaseHotKey);
						String	after	=	moduleName.substring(indexOfuCaseHotKey+1);
						
						underLinedModuleName = before + underLineduCaseHotKey + after;
						
					} else {
						if(indexOflCaseHotKey != -1) {
							String	before	=	moduleName.substring(0,indexOflCaseHotKey);
							String	after	=	moduleName.substring(indexOflCaseHotKey+1);
							
							underLinedModuleName = before + underLinedlCaseHotKey + after;
						}
					}
					
					if(underLinedModuleName!=null && underLinedModuleName.length() > 0) {
						menuStr.append(underLinedModuleName);
					} else {
						menuStr.append(moduleName);
					}

				} else {
					menuStr.append(moduleName);
				}
				
				menuStr.append("</td>"+br);
				
				tabStr.append("<div id=\"subMenu"+index+"0\" class=\"menu\" style=\"position:absolute; top:16px; left:0px; visibility:hidden;\">"+br);
	 			tabStr.append("<table cellpadding=\"0\" cellspacing=\"0\">"+tableStr.toString()+"</table>"+br+"</div>"+br);
				tableStr = new StringBuffer();
				
				if(index!=0) {
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.top = menuBarTop + \"px\";");
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.left = (document.getElementById(\"menuBar"+index+"\").offsetLeft-2) + \"px\";");
				} else {
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.top = menuBarTop + \"px\";");
				}
				
				index++;
			}
		}
		menuStr.append("</table></div>");
		
		scriptArray[0] = scriptStr.toString();
		scriptArray[1] = menuStr.toString();
		scriptArray[2] = tabStr.toString();
		
		StringBuffer	keyScript = new StringBuffer();
		int keyListSize		=	0;
		if(keyList != null && !keyList.isEmpty())
		{	
			keyListSize	= keyList.size();
			for(int i = 0; i < keyListSize; i++)
			{
				if(i == 0)
					keyScript.append("			if");
				else
					keyScript.append("			else if");
					
				//keyScript.append("(key=='"+ keyList.get(i)+"' || whichCode==19)"+br);
				keyScript.append("(key=='"+ keyList.get(i)+"' || key.toUpperCase()=='"+ keyList.get(i)+"')"+br);
				keyScript.append("			{ currentMenu = "+i+"; }"+br);
			}
			if(keyScript!=null && keyScript.toString().length() > 0) {
				keyScript.append("			else { return false; }"+br);
			}
		}
		// Add the hot key script to the script array to be sent to JSP
		scriptArray[3] = keyScript.toString();
		
		return scriptArray;
	}
	/*
	 * This method will generate script for NetScape.
	 */
	public String[] getNSScript(ArrayList tabList,Hashtable alevel)
	{
		String []scriptArray = new String[4];
		
		StringBuffer tableStr 	= new StringBuffer();
		StringBuffer tabStr   	= new StringBuffer();
		StringBuffer menuStr  	= new StringBuffer(MNEU_NS_STR);
		StringBuffer scriptStr 	= new StringBuffer(SCRIPT_NS_STR);
		ArrayList	keyList		= new ArrayList();
		
		int index = 0;
		int tabListSize		= tabList.size();
		for(int i=0;i<tabListSize;i++)
		{
			MenuModule	module			= (MenuModule)tabList.get(i);
			String		moduleName		= module.moduleName;
			String		hotKey			= module.hotKey;
			ArrayList	uiList			= module.uiList;
			boolean		uiExistsInTab	= false;
			int 		tdCount 		= 1;
			int uiListSize 			= uiList.size();
			for(int j=0; j < uiListSize; j++)
			{
				UserInterface ui = (UserInterface)uiList.get(j);
				
				int		actLevel		= ui.accessLevel;
				String	process			= ui.process;
				String	url				= ui.url;
				String	txid			= ui.txid;
				boolean	permissionFlag	= false;
				
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
						case 8:
							if(actLevel==8)
								permissionFlag = true;
							break;
						case 9:
							if(actLevel==1||actLevel==8)
								permissionFlag = true;
							break;
						case 10:
							if(actLevel==2||actLevel==8)
								permissionFlag = true;
							break;
						case 11:
							if(actLevel==1 || actLevel==2||actLevel==8)
								permissionFlag = true;
							break;
						case 12:
							if(actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 13:
							if(actLevel==1  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 14:
							if(actLevel==2  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
						case 15:
							if(actLevel==1 || actLevel==2  || actLevel==4 || actLevel==8)
								permissionFlag = true;
							break;
					}
				}
					
				if(permissionFlag)
				{
					tableStr.append("<tr>"+br);
					tableStr.append("<td onMouseOver = highLight(this) onMouseOut = lowLightAll() id=\"tdsubMenu"+index+"0"+tdCount+"\" class=\"menuItem\">"+br);
					tableStr.append("<a id=\"subMenu"+index+"0"+tdCount+"\" class=\"menuLink\" href=\""+url+"\">"+process+"</a>"+br);
					tableStr.append("</td>"+br);
					tableStr.append("</tr>"+br);
					/*
					tableStr.append("<tr>");
					tableStr.append("<td class=\"menuItem\">");
					tableStr.append("<a class=\"menuLink\" href=\""+url+"\">"+process+"</a>");
					tableStr.append("</td>");
					tableStr.append("</tr>");
					*/
					if(!uiExistsInTab)
					{
						uiExistsInTab = true;
					}
					tdCount++;
				}
			}

			if(uiExistsInTab)
			{
				keyList.add(hotKey);
				
				menuStr.append("<td id=\"menuBar"+index+"\" class=\"menuButton\" style=\"background: #D4D0C8; border-left-width:2px;\" onmouseover=\"showSubMenu("+index+",0);\">"+br);
				
				if(hotKey!=null && hotKey.length() > 0) {
					
					String underLinedModuleName = "";
					
					//String	tempModuleName	=	new String( moduleName );
					
					//tempModuleName	=	tempModuleName.toUpperCase();
					String	uCaseHotKey = hotKey.toUpperCase();
					String	lCaseHotKey = hotKey.toLowerCase();
					
					String underLineduCaseHotKey = "<u>"+uCaseHotKey+"</u>";
					String underLinedlCaseHotKey = "<u>"+lCaseHotKey+"</u>";					
					
					int	indexOfuCaseHotKey	=	moduleName.indexOf( uCaseHotKey );
					int	indexOflCaseHotKey	=	moduleName.indexOf( lCaseHotKey );
					
					if(indexOfuCaseHotKey != -1) {
						String	before	=	moduleName.substring(0,indexOfuCaseHotKey);
						String	after	=	moduleName.substring(indexOfuCaseHotKey+1);
						
						underLinedModuleName = before + underLineduCaseHotKey + after;
						
					} else {
						if(indexOflCaseHotKey != -1) {
							String	before	=	moduleName.substring(0,indexOflCaseHotKey);
							String	after	=	moduleName.substring(indexOflCaseHotKey+1);
							
							underLinedModuleName = before + underLinedlCaseHotKey + after;
						}
					}
					
					if(underLinedModuleName!=null && underLinedModuleName.length() > 0) {
						menuStr.append(underLinedModuleName);
					} else {
						menuStr.append(moduleName);
					}

				} else {
					menuStr.append(moduleName);
				}
				
				menuStr.append("</td>"+br);

				tabStr.append("<div id=\"subMenu"+index+"0\" class=\"menu\" style=\"position:absolute; top:16px; left:0px; visibility:hidden;\">"+br);
	 			tabStr.append("<table cellpadding=\"0\" cellspacing=\"0\">"+tableStr.toString()+"</table>"+br+"</div>"+br);

				tableStr = new StringBuffer();

				if(index!=0) {
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.top = menuBarTop + \"px\";");
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.left = (document.getElementById(\"menuBar"+index+"\").offsetLeft-2) + \"px\";");
				} else {
					scriptStr.append("document.getElementById(\"subMenu"+index+"0\").style.top = menuBarTop + \"px\";");
				}
				
				index++;
			}
		}
		
		menuStr.append("</table></div>");
		
		scriptArray[0] = scriptStr.toString();
		scriptArray[1] = menuStr.toString();
		scriptArray[2] = tabStr.toString();
		
		StringBuffer	keyScript = new StringBuffer();
	
		//keyScript.append("var whichCode	= (window.Event) ? e.which : e.keyCode;"+br); 
		//keyScript.append("var key		= String.fromCharCode(whichCode);"+br); 
		
				
		if(keyList != null && !keyList.isEmpty())
		{	
			int keyListSize		= keyList.size();
			for(int i = 0; i < keyListSize; i++)
			{
				if(i == 0)
					keyScript.append("			if");
				else
					keyScript.append("			else if");
					
				//keyScript.append("(key=='"+ keyList.get(i)+"' || whichCode==19)"+br);
				keyScript.append("(key=='"+ keyList.get(i)+"' || key.toUpperCase()=='"+ keyList.get(i)+"')"+br);
				keyScript.append("			{ currentMenu = "+i+"; }"+br);
			}
			if(keyScript!=null && keyScript.toString().length() > 0) {
				keyScript.append("			else { return false; }"+br);
			}
		}
		
		scriptArray[3] = keyScript.toString();
		
		return scriptArray;
	}
}

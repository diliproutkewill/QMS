/**
 * 
 * Copyright (c) 2000-2001 by FourSoft, Inc. All Rights Reserved.
 * This software is the proprietary information of FourSoft, Pvt Ltd.
 * Use is subject to license terms.
 *
 * esupply - v 1.x 
 *
 */
 
package com.foursoft.esupply.accesscontrol.java;

import java.util.ArrayList;

/**
 * File			: MenuModule.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This java object used to store properties of one MenuModule
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 * 
 * Modified History
 * Modified By		Modified On		Reason
 * Amit Parekh		23.07.2002		Hotkey incorporated as an attribute
 * Amit Parekh		09.05.2003		The link to the Administration Right Panel Menu is defined as an constant
 */

public class MenuModule 
{
	public String		moduleName;
	public String		hotKey	=	"";
	public ArrayList	uiList;

	public	static String	RIGHT_FRAME_MENU_ACCESS_CONTROL	=	"ESMenuRightPanel.jsp?link=es-Administration-Setup";
	
	public MenuModule()
	{
	}
	
	public MenuModule(String moduleName,ArrayList uiList)
	{
		this.moduleName = moduleName;
		this.uiList		= uiList;
	}
	
	// New const added for hotkey attribute for the Menu
	public MenuModule(String moduleName,String hotKey,ArrayList uiList)
	{
		this.moduleName = moduleName;
		this.hotKey		= hotKey;
		this.uiList		= uiList;
	}
	
	public String toString()
	{
		return moduleName+"\n\t"+uiList;
	}
}
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

import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

import com.foursoft.esupply.accesscontrol.java.UserInterface;
import com.foursoft.esupply.accesscontrol.java.MenuModule;

/**
 * File			: UIPermissions.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This java class  parses the xml file where we have define 
 * the information and stores information about all the UserInterfaces  
 * and respective AccessLevel required that  UserInterface.
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 * 
 * Modified History
 * Modified By		Modified On		Reason
 * Amit Parekh		23.07.2002		Hotkey incorporated as an attribute
 */
public class UIPermissions implements java.io.Serializable
{
	public Hashtable transactionsTable;
	Hashtable subTable = null;
	public long lastModified = 0;
	public String configFile;
	//added by Sanjay on 09-11-2005 for singleton purpose
	private static UIPermissions _UIPermissions = null;
  
	private UIPermissions(){} 
  
	public static UIPermissions getInstance(){
		if (_UIPermissions == null) 
		{
			createInstance();
		}
		return _UIPermissions;
	}

 
	private synchronized static void createInstance() {
		if (_UIPermissions == null)
		{ 
			_UIPermissions = new UIPermissions();
		}
	}
	//end

	public Hashtable getTransactionsTable()
	{
		return transactionsTable;
	}
	
	public long getLastModified()
	{
		return lastModified;
	}
	public void setLastModified(long time)
	{
		lastModified = time;
		//System.out.println("setLastModified is called");
	}
	
	public Hashtable getLeafTable(Hashtable accessTable)
	{
		java.util.Iterator itr = transactionsTable.keySet().iterator();
		Hashtable leafTable = new Hashtable();
		while(itr.hasNext())
		{
			String leafName = (String)itr.next();
			boolean hasLeaf	= false;
			ArrayList moduleList = (ArrayList)transactionsTable.get(leafName);
			int modListSize	=	moduleList.size();
			for(int i=0;i<modListSize;i++)
			{
				MenuModule module = (MenuModule)moduleList.get(i);
				String moduleName		= module.moduleName;
				ArrayList uiList		= module.uiList;
				int uiListSize	=	uiList.size();
				for(int j=0;j<uiListSize;j++)
				{
					UserInterface ui = (UserInterface)uiList.get(j);
					String txid	   = ui.txid;
					boolean permissionFlag = false;
					if(accessTable.containsKey(txid))
					{
						if(!hasLeaf)
							hasLeaf = true;
					}
				}
			}
			if(hasLeaf)
				leafTable.put(leafName,"true");
			
		}
		return leafTable;
	}
	public void process(String configFile)
	{
		this.configFile   = configFile;
	//	System.out.println("Process Called : ");
		transactionsTable = new Hashtable();
		
		ArrayList uiList;
		ArrayList moduleList;
		int alevel;
		String url;
		String process;
		
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
              DocumentBuilder builder = factory.newDocumentBuilder();
             //System.out.println("configFile:"+configFile);
              document = builder.parse(configFile);
			   NodeList nodeList = document.getDocumentElement().getChildNodes();
			   //System.out.println("Node length :"+nodeList.getLength());
			   for(int i=0;i<nodeList.getLength();i++)
			   {
					if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE)
					{
						moduleList = new ArrayList();
						//System.out.println("node name:"+nodeList.item(i).getNodeName());
						NamedNodeMap 	nnm1			= nodeList.item(i).getAttributes();
						String leafName	 	= nnm1.getNamedItem("leafName").getNodeValue();
						//System.out.println("Leaf Name : "+leafName);
						
						NodeList nl = nodeList.item(i).getChildNodes();
						//System.out.println("Node length :"+nl.getLength());
						for(int j=0;j<nl.getLength();j++)
						{
							//System.out.println("in for of nl and node name"+nl.item(j).getNodeName());
							if(nl.item(j).getNodeName().equals("module"))
							{
								uiList = new ArrayList();
									//System.out.println("in if of module nodes");
								NodeList subList = nl.item(j).getChildNodes();
								NamedNodeMap nnp = nl.item(j).getAttributes();
								Node txidNode = nnp.getNamedItem("txid");
								Node nameNode = nnp.getNamedItem("name");
								Node hotkeyNode = nnp.getNamedItem("hotkey");
								
								String	hotkey = "";
								
								if(hotkeyNode!=null && hotkeyNode.getNodeValue()!=null && hotkeyNode.getNodeValue().length() > 0) {
									hotkey = hotkeyNode.getNodeValue();
								}
								//System.out.println("Module Tapin : "+nameNode.getNodeValue());
								
								for(int k=0;k<subList.getLength();k++)
								{
									Node n = subList.item(k);
									if(n.getNodeName().equals("uiName"))
									{
										NamedNodeMap nnm = n.getAttributes();
										
										alevel  = Integer.parseInt(nnm.getNamedItem("alevel").getNodeValue());
										process = nnm.getNamedItem("process").getNodeValue();
										url		= nnm.getNamedItem("url").getNodeValue();
										UserInterface dob = null;
										dob = new UserInterface(alevel,txidNode.getNodeValue(),process,url);
										uiList.add(dob);
											
									}
									
								}
								//System.out.println("UIPermissions  : Menu Tab wise "+uiList);
								moduleList.add(new MenuModule( nameNode.getNodeValue(), hotkey, uiList ) );
							}
						}
						//System.out.println("UIPermissions  : LeafWise"+moduleList);
						transactionsTable.put(leafName, moduleList);
					}
				//System.out.println("UIPermissions  : Total Tree "+transactionsTable);
			   }

          }
          catch (SAXParseException spe) {
			  System.out.println("SAXParseException is"+spe.toString());
          }
          catch (SAXException se) {
			   System.out.println("SAXException is"+se.toString());
          }
          catch (ParserConfigurationException pce) {
			   System.out.println("ParserConfigurationException is"+pce.toString());
          }
          catch (IOException ioe) {
			   System.out.println("IOException is"+ioe.toString());
          }
		  catch (Exception e) {
			   System.out.println("Exception is"+e.toString());
          }
				
		 
	}

}
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

import javax.xml.parsers.*;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger; 

import com.foursoft.esupply.common.exception.FoursoftException;


/**
 * File			: CRMTreeUtility.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This utility java class  is used to generate the tree according to user permissions.
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 */
public class CRMTreeUtility implements Serializable
{
	public static final String FILE_NAME  = "CRMTreeUtility";
	public static final String RIGHT_MENU = "ESMenuCRMRightPanel.jsp";
	public Hashtable menuListTable;
	public long lastModified = 0;
	public String treeFile;
	public Document xmlDocument;
	public String htmlStr;
	boolean containsFlag = false;
	boolean isFolder     = true;
	boolean parentContains = true;
	String parentFolderName = "e-Supply";
	String folderName = "e-Supply";
	String NL = "\n";
	/*public CRMTreeUtility()
	{
	}*/
  //added by Sanjay on 09-11-2005 for singleton purpose
	private static CRMTreeUtility _CRMTreeUtility = null;
  private static Logger logger = null;
  
	private CRMTreeUtility(){
    logger  = Logger.getLogger(CRMTreeUtility.class);
  } 
  
	public static CRMTreeUtility getInstance(){
		if (_CRMTreeUtility == null) 
		{
			createInstance();
		}
		return _CRMTreeUtility;
	}

 
	private synchronized static void createInstance() {
		if (_CRMTreeUtility == null)
		{ 
			_CRMTreeUtility = new CRMTreeUtility();
		}
	}
	//end
	public void setMenuListTable(Hashtable menuListTable)
	{
		this.menuListTable = menuListTable;
	}
	public void setTreeFile(String treeFile)
	{
		this.treeFile = treeFile;
	}
	public void setLastModified(long lastModified)
	{
		this.lastModified = lastModified;
	}
	public long getLastModified()
	{
		return lastModified;
	}
	public void parse()throws FoursoftException
	{
		try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setValidating(true);
        		DocumentBuilder builder = factory.newDocumentBuilder();
                xmlDocument = builder.parse(treeFile);
          }
          catch (SAXParseException spe) {
			  //Logger.error(FILE_NAME,"parse","SAXParseException is"+spe.toString());
        logger.error(FILE_NAME+"parse"+"SAXParseException is"+spe.toString());
			   throw new FoursoftException("There is some problem in tree-config.xml File verify it");
          }
          catch (SAXException se) {
			   //Logger.error(FILE_NAME,"parse","SAXException is"+se.toString());
         logger.error(FILE_NAME+"parse"+"SAXException is"+se.toString());
			   throw new FoursoftException("There is some problem in tree-config.xml File verify it");
          }
          catch (ParserConfigurationException pce) {
			   //Logger.error(FILE_NAME,"parse","ParserConfigurationException is"+pce.toString());
         logger.error(FILE_NAME+"parse"+"ParserConfigurationException is"+pce.toString());
			   throw new FoursoftException("There is some problem in tree-config.xml File verify it");
          }
          catch (IOException ioe) {
			   //Logger.error(FILE_NAME,"parse","IOException is"+ioe.toString());
         logger.error(FILE_NAME+"parse"+"IOException is"+ioe.toString());
			   throw new FoursoftException("There is some problem in tree-config.xml File verify it");
          }
		  catch (Exception e) {
			   //Logger.error(FILE_NAME,"parse","Exception is"+e.toString());
         logger.error(FILE_NAME+"parse"+"Exception is"+e.toString());
			   throw new FoursoftException("There is some problem in tree-config.xml File verify it");
          }
		//Logger.info("TreeUtility","parse","TIME AFTER PARSING:"+new java.sql.Timestamp(new java.util.Date().getTime()));
	}
	public String process()
	{	
		htmlStr = "";
		String mainString = "";
			  NodeList nodeList = xmlDocument.getDocumentElement().getChildNodes();
			  for(int i=0;i<nodeList.getLength();i++)
			  {
				  if(nodeList.item(i).getNodeName().equals("leaf-node"))
					  mainString += traverseTree(nodeList.item(i));
			  }
			 //System.out.println("MAIN STRING IN TREE UTILITY IS:"+mainString[0]);
			 //System.out.println("MAIN STRING IN TREE UTILITY IS:"+mainString[1]);
			return mainString;
	}
	private String traverseTree(Node node)
	{
		NodeList nodeList = node.getChildNodes();
		//Logger.info("TreeUtility","traverseLeaf","Parent leaf is:"+parent);
		String leafStr 			= "";
		String folderName 		= "";
		String linkName			= "";
		boolean leafExists 		= false;
		for(int i=0;i<nodeList.getLength();i++)
		{
			if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				if(nodeList.item(i).getNodeName().equals("name"))
				{
					if(nodeList.item(i).hasChildNodes())
						folderName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("permission-link"))
				{
					if(nodeList.item(i).hasChildNodes())
					{
						linkName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
						try{
								if(menuListTable.get(nodeList.item(i).getChildNodes().item(0).getNodeValue())!=null)
								{
									leafExists = true;
								}
							}
							catch(Exception e)
							{
								//Logger.error(FILE_NAME,"traverseLeaf()","IN permission-link NODE AND EXCEPTION IS:"+e.toString());
                logger.error(FILE_NAME+"traverseLeaf()"+"IN permission-link NODE AND EXCEPTION IS:"+e.toString());
							}
					}
				}
			}
		}
		if(leafExists)
		{
			leafStr = "<tr> "+NL+
    						"<td class=\"menulines\"><img src=\"images/arrow.gif\" width=\"12\" height=\"11\"><a href=\"ESMenuCRMRightPanel.jsp?link="+linkName+"\"  target=\"main\"><font face=\"Arial\" size=\"2\" color=\"#ffffff\">"+folderName+"</font></a></td>"+NL+
  					  "</tr>";
		}
		return leafStr;
	}
}
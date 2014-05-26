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
import java.util.HashMap;
import java.util.Vector;
import java.io.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;

//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.foursoft.esupply.common.exception.FoursoftException;


/**
 * File			: TreeUtility.java
 * sub-module	: AccessControl
 * module		: esupply
 * 
 * This utility java class  is used to generate the tree according to user permissions.
 * 
 * @author	Sasi Bhushan. P, 
 * @date	22-12-2001
 */
public class TreeUtility implements Serializable
{
	public static final String FILE_NAME  = "TreeUtility";
	public static final String RIGHT_MENU = "ESMenuRightPanel.jsp";
	public Hashtable menuListTable;
	public long lastModified = 0;
	public String treeFile;
	public Document xmlDocument;

	private	HashMap	hmAddedHotKeyCombos;	

	boolean containsFlag = false;
	boolean isFolder     = true;
	boolean parentContains = true;
	String parentFolderName = "e-Supply";
	String folderName = "e-Supply";
	String NL = "\n";
	public int shipmentMode ;
	/*public TreeUtility()
	{ 
	}*/
  //added by Sanjay on 09-11-2005 for singleton purpose
	private static TreeUtility _TreeUtility = null;
  private static Logger logger = null;
	private TreeUtility(){
    logger  = Logger.getLogger(TreeUtility.class);
  }
  
	public static TreeUtility getInstance(){
		if (_TreeUtility == null) 
		{
			createInstance();
		}
		return _TreeUtility;
	}

 
	private synchronized static void createInstance() {
		if (_TreeUtility == null)
		{ 
			_TreeUtility = new TreeUtility();
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
			  //Logger.error(FILE_NAME,"parses","SAXParseException is"+spe.toString());
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
		hmAddedHotKeyCombos	= new HashMap();

		String[] mainString = new String[3];

		NodeList nodeList = xmlDocument.getDocumentElement().getChildNodes();

		for(int i=0;i<nodeList.getLength();i++)
		{
			if(nodeList.item(i).getNodeName().equals("tree-node"))
				mainString = traverseTree(nodeList.item(i),"e-Supply");
		}

		//String processedScript = "";
		//processedScript += mainString[2] +"\n\n\n";
		//processedScript += "function initializeMenus() {";
		//processedScript += mainString[1] + "\n\n\n";
		//processedScript += "}";
		
		return mainString[1];
		//return processedScript;
	}
	
	private String traverseLeaf(Node node,String parent)
	{
		NodeList nodeList = node.getChildNodes();
		//Logger.info("TreeUtility","traverseLeaf","Parent leaf is:"+parent);
		String leafStr = "";
		String parentFolderName = parent;
		String folderName 		= "";
		String displayName 		= "";
		String stBarDisplay 	= "";
		String hyperLink 		= "";
		String openGif 			= "";
		String closedGif 		= "";
		
		String hotKey			= "";
		String ctrlKey			= "";
		String shiftKey			= "";
		String altKey			= "";
		
		boolean leafExists 		= false;

		//System.out.println();
		//System.out.println(" TRAVERSE LEAF STARTS");
		
		for(int i=nodeList.getLength()-1;i>=0;i--)
		{
			if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
			
				if(nodeList.item(i).getNodeName().equals("hyperLink"))
				{
					if(nodeList.item(i).hasChildNodes())
						hyperLink = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("name"))
				{
					if(nodeList.item(i).hasChildNodes())
						folderName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("display-name"))
				{
					if(nodeList.item(i).hasChildNodes())
						displayName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("open-icon"))
				{
					if(nodeList.item(i).hasChildNodes())
						openGif = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("hot-key"))
				{
					//System.out.println();
					if(nodeList.item(i).hasChildNodes()) {
						hotKey = nodeList.item(i).getChildNodes().item(0).getNodeValue();
					}

					boolean	isValidHotKey = isValidHotKey( hotKey );
					
					// Get the SHIFT and/or CTRL combinations for this hot key
					if(isValidHotKey) {
						
						hotKey	= hotKey.toUpperCase();
						NamedNodeMap	hotKeyAttributes	=	nodeList.item(i).getAttributes();
						
						Node ctrlKeyNode	= hotKeyAttributes.getNamedItem("ctrl");
						Node shiftKeyNode	= hotKeyAttributes.getNamedItem("shift");
						Node altKeyNode		= hotKeyAttributes.getNamedItem("alt");
						
						if(ctrlKeyNode!=null) {
							ctrlKey	= ctrlKeyNode.getNodeValue();
							if(!(ctrlKey.equalsIgnoreCase("Y") || ctrlKey.equalsIgnoreCase("N"))) {
								ctrlKey = "N";
							}
						} else {
							ctrlKey = "N";
						}	
						if(shiftKeyNode!=null) {
							shiftKey = shiftKeyNode.getNodeValue();
							if(!(shiftKey.equalsIgnoreCase("Y") || shiftKey.equalsIgnoreCase("N"))) {
								shiftKey = "N";
							}
						} else {
							shiftKey = "N";
						}
						if(altKeyNode!=null) {
							altKey = altKeyNode.getNodeValue();
							if(!(altKey.equalsIgnoreCase("Y") || altKey.equalsIgnoreCase("N"))) {
								altKey = "N";
							}
						} else {
							altKey = "N";
						}

						// Make a combination to check for duplicate
						String hotKeyCombo	=	hotKey + ctrlKey + shiftKey + altKey;

						// First check for existence of duplicate in HashMap

						String	duplicate = (String) hmAddedHotKeyCombos.get( hotKeyCombo );

						if(duplicate==null) {
							// If no duplicate combo is found, add it to the list
							hmAddedHotKeyCombos.put( hotKeyCombo, hotKeyCombo );
						} else {
							// If no duplicate combo is found, i.e. some other item already has the current
							// combo defined for it, ignore this one
							//Logger.warning(FILE_NAME,"Duplicate Hot-Key Combo : HINT --> PARENT'S NAME='"+parent+"' KEY='"+hotKey+"' CTRL='"+ctrlKey+"' SHIFT='"+shiftKey+"' ALT='"+altKey+"' Action Taken: This combo is ignored.");
              logger.warn(FILE_NAME+"Duplicate Hot-Key Combo : HINT --> PARENT'S NAME='"+parent+"' KEY='"+hotKey+"' CTRL='"+ctrlKey+"' SHIFT='"+shiftKey+"' ALT='"+altKey+"' Action Taken: This combo is ignored.");
							
							hotKey		= "";
							ctrlKey		= "";
							shiftKey	= "";
							altKey		= "";
						}
	
					} else {
						hotKey		= "";
						ctrlKey		= "";
						shiftKey	= "";
						altKey		= "";
					} // if hotkey
					
					//System.out.println(" hotKey   = '"+hotKey+"'.....VALID = "+isValidHotKey);
					//System.out.println(" ctrlKey  = '"+ctrlKey+"'");
					//System.out.println(" shiftKey = '"+shiftKey+"'");
					//System.out.println(" altKey   = '"+altKey+"'");
					//System.out.println();
				}
				else if(nodeList.item(i).getNodeName().equals("close-icon"))
				{
					if(nodeList.item(i).hasChildNodes())
						closedGif = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("status-bar-display"))
				{
					if(nodeList.item(i).hasChildNodes())
						stBarDisplay = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("permission-link"))
				{
					if(nodeList.item(i).hasChildNodes())
					{
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
				
			} // if ELEMENT_NODE
			
		} // for
		
		//System.out.println(" displayName = "+displayName);
		//System.out.println(" TRAVERSE LEAF ENDS");
		//System.out.println();
		
		if(leafExists)
		{
			leafStr += parentFolderName+".MTMAddItem(new MTMenuItem('"+displayName+"', '"+RIGHT_MENU+"?"+hyperLink+"', 'text','','','"+folderName+"','"+openGif+"','"+closedGif+"','"+hotKey+"','"+ctrlKey+"','"+shiftKey+"','"+altKey+"'));"+NL;
		}
		return leafStr;
	}
	
	private String[] traverseTree(Node node,String parent)
	{

		String [] htmlStrings = new String[3];
		String treeStr = "";
		String subName = "";
		String treeStrVariableDeclarations = "";
		ArrayList treeList = new ArrayList();
		NodeList nodeList = node.getChildNodes();
		String parentFolderName = parent;
		String folderName       = "";
		String displayName 		= "";
		String stBarDisplay 	= "";
		String hyperLink 		= "";
		String openGif 			= "";
		String closedGif 		= "";
		
		String hotKey			= "";
		String ctrlKey			= "";
		String shiftKey			= "";
		String altKey			= "";
		
		boolean hasLeaf			= false;
		boolean leafFlag		= false;
		int treeIndex			= 0;

		//System.out.println();
		//System.out.println(" TRAVERSE TREE STARTS");

		for(int i=0;i<nodeList.getLength();i++)
		{
			if(nodeList.item(i).getNodeType()==Node.ELEMENT_NODE)
			{
				
				if(nodeList.item(i).getNodeName().equals("hyperLink"))
				{
					if(nodeList.item(i).hasChildNodes())
						hyperLink = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("name"))
				{
					if(nodeList.item(i).hasChildNodes())
						folderName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("display-name"))
				{
					if(nodeList.item(i).hasChildNodes())
						displayName = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("status-bar-display"))
				{
					if(nodeList.item(i).hasChildNodes())
						stBarDisplay = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("open-icon"))
				{
					if(nodeList.item(i).hasChildNodes())
						openGif = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("hot-key"))
				{
					//System.out.println();
					if(nodeList.item(i).hasChildNodes()) {
						hotKey = nodeList.item(i).getChildNodes().item(0).getNodeValue();
					}
					
					boolean	isValidHotKey = isValidHotKey( hotKey );
					
					// Get the SHIFT and/or CTRL combinations for this hot key
					if(isValidHotKey) {
						
						hotKey	= hotKey.toUpperCase();
						NamedNodeMap	hotKeyAttributes	=	nodeList.item(i).getAttributes();
						
						Node ctrlKeyNode	= hotKeyAttributes.getNamedItem("ctrl");
						Node shiftKeyNode	= hotKeyAttributes.getNamedItem("shift");
						Node altKeyNode		= hotKeyAttributes.getNamedItem("alt");
						
						if(ctrlKeyNode!=null) {
							ctrlKey	= ctrlKeyNode.getNodeValue();
							if(!(ctrlKey.equalsIgnoreCase("Y") || ctrlKey.equalsIgnoreCase("N"))) {
								ctrlKey = "N";
							}
						} else {
							ctrlKey = "N";
						}	
						if(shiftKeyNode!=null) {
							shiftKey = shiftKeyNode.getNodeValue();
							if(!(shiftKey.equalsIgnoreCase("Y") || shiftKey.equalsIgnoreCase("N"))) {
								shiftKey = "N";
							}
						} else {
							shiftKey = "N";
						}
						if(altKeyNode!=null) {
							altKey = altKeyNode.getNodeValue();
							if(!(altKey.equalsIgnoreCase("Y") || altKey.equalsIgnoreCase("N"))) {
								altKey = "N";
							}
						} else {
							altKey = "N";
						}

						// Make a combination to check for duplicate
						String hotKeyCombo	=	hotKey + ctrlKey + shiftKey + altKey;

						// First check for existence of duplicate in HashMap

						String	duplicate = (String) hmAddedHotKeyCombos.get( hotKeyCombo );

						if(duplicate==null) {
							// If no duplicate combo is found, add it to the list
							hmAddedHotKeyCombos.put( hotKeyCombo, hotKeyCombo );
						} else {
							// If no duplicate combo is found, i.e. some other item already has the current
							// combo defined for it, ignore this one
							//Logger.warning(FILE_NAME,"Duplicate Hot-Key Combo : HINT --> PARENT'S NAME='"+parent+"' KEY='"+hotKey+"' CTRL='"+ctrlKey+"' SHIFT='"+shiftKey+"' ALT='"+altKey+"' Action Taken: This combo is ignored.");
              logger.warn(FILE_NAME+"Duplicate Hot-Key Combo : HINT --> PARENT'S NAME='"+parent+"' KEY='"+hotKey+"' CTRL='"+ctrlKey+"' SHIFT='"+shiftKey+"' ALT='"+altKey+"' Action Taken: This combo is ignored.");
							
							hotKey		= "";
							ctrlKey		= "";
							shiftKey	= "";
							altKey		= "";
						}
	
					} else {
						hotKey		= "";
						ctrlKey		= "";
						shiftKey	= "";
						altKey		= "";
					} // if hotkey
					
					//System.out.println(" hotKey   = '"+hotKey+"'.....VALID = "+isValidHotKey);
					//System.out.println(" ctrlKey  = '"+ctrlKey+"'");
					//System.out.println(" shiftKey = '"+shiftKey+"'");
					//System.out.println(" altKey   = '"+altKey+"'");
					//System.out.println();
				}
				else if(nodeList.item(i).getNodeName().equals("close-icon"))
				{
					if(nodeList.item(i).hasChildNodes())
						closedGif = nodeList.item(i).getChildNodes().item(0).getNodeValue();
				}
				else if(nodeList.item(i).getNodeName().equals("leaf-node"))
				{
					String leafTemp = traverseLeaf(nodeList.item(i),folderName);
					if(!leafTemp.equals(""))
					{
						leafFlag = true;
						treeStr+=leafTemp+NL;
						treeIndex++;
					}
					if(!hasLeaf)
						hasLeaf = leafFlag;
				}
				else if(nodeList.item(i).getNodeName().equals("tree-node"))
				{
        //System.out.println("folderNamefolderNamefolderNamefolderName "+folderName);
					if(folderName.equals("Air"))
					{
						if(shipmentMode==1||shipmentMode==3||shipmentMode==5||shipmentMode==7)
						{
							String [] temp = traverseTree(nodeList.item(i),folderName);
							if(!temp[0].equals(""))
							{
								treeList.add(temp[0]+treeIndex);
								treeIndex++;
								treeStr+=temp[1]+NL;
								treeStrVariableDeclarations += temp[2]+NL;
								hasLeaf = true;
							} 
												
							if(!hasLeaf)
								if(treeList.size()>0)
									hasLeaf = true;
						}
					}
					else if(folderName.equals("Sea"))
					{
						if(shipmentMode==2||shipmentMode==3||shipmentMode==6||shipmentMode==7)
						{
							String [] temp = traverseTree(nodeList.item(i),folderName);
							if(!temp[0].equals(""))
							{
								treeList.add(temp[0]+treeIndex);
								treeIndex++;
								treeStr+=temp[1]+NL;
								treeStrVariableDeclarations += temp[2]+NL;
								hasLeaf = true;
							} 
												
							if(!hasLeaf)
								if(treeList.size()>0)
									hasLeaf = true;
						}

					}
					else if(folderName.equals("Truck"))
					{
						if(shipmentMode==4||shipmentMode==5||shipmentMode==6||shipmentMode==7)
						{
							String [] temp = traverseTree(nodeList.item(i),folderName);
							if(!temp[0].equals(""))
							{
								treeList.add(temp[0]+treeIndex);
								treeIndex++;
								treeStr+=temp[1]+NL;
								treeStrVariableDeclarations += temp[2]+NL;
								hasLeaf = true;
							} 
												
							if(!hasLeaf)
								if(treeList.size()>0)
									hasLeaf = true;
						}

					}
					else
					{
							String [] temp = traverseTree(nodeList.item(i),folderName);
							if(!temp[0].equals(""))
							{
								treeList.add(temp[0]+treeIndex);
								treeIndex++;
								treeStr+=temp[1]+NL;
								treeStrVariableDeclarations += temp[2]+NL;
								//System.out.println("temp[1] :"+temp[1]);
								hasLeaf = true;
							} 
												
							if(!hasLeaf)
								if(treeList.size()>0)
									hasLeaf = true;
					}
											/*switch(shipmentMode)
						{
							String [] temp = traverseTree(nodeList.item(i),folderName);
							if(!temp[0].equals(""))
							{
								treeList.add(temp[0]+treeIndex);
								treeIndex++;
								treeStr+=temp[1];
								hasLeaf = true;
							} 
												
							if(!hasLeaf)
								if(treeList.size()>0)
									hasLeaf = true;
						}*/
				}
				
				
			}
		}
		
		//System.out.println(" displayName = "+displayName);
		//System.out.println(" TRAVERSE TREE ENDS");
		//System.out.println();
		
		if(hasLeaf)
		{
			subName = folderName;
			treeStr = NL+NL+"var "+folderName+" = new MTMenu();"+NL + treeStr + NL;
			//treeStr = NL+NL+ folderName+" = new MTMenu();"+NL + treeStr + NL;
			//treeStrVariableDeclarations = "var "+folderName+";"+NL + treeStrVariableDeclarations + NL;
			
			//System.out.println("IN TREE UTILITY JAVA folderName :"+folderName+" displayName :"+displayName);
			
			if(!parentFolderName.equals("e-Supply"))
			{
				if(hyperLink.equals("") )
					treeStr = parentFolderName+".MTMAddItem(new MTMenuItem('"+displayName+"', '', 'text',''    ,''             ,'"+folderName+"','"+openGif+"','"+closedGif+"','"+hotKey+"','"+ctrlKey+"','"+shiftKey+"','"+altKey+"'));"+treeStr+NL;
				else
					treeStr = parentFolderName+".MTMAddItem(new MTMenuItem('"+displayName+"', '', 'text','true','"+hyperLink+"','"+folderName+"','"+openGif+"','"+closedGif+"','"+hotKey+"','"+ctrlKey+"','"+shiftKey+"','"+altKey+"'));"+treeStr+NL;
			}
				int treeListSize	=	treeList.size();
			for(int i=(treeListSize-1);i>=0;i--)
			{
					String subFNameWithIndex = (String)treeList.get(i);
					String index	= subFNameWithIndex.substring(subFNameWithIndex.length()-1);
					String subFName = subFNameWithIndex.substring(0,subFNameWithIndex.length()-1);
					treeStr += folderName+".items["+index+"].MTMakeSubmenu("+subFName+",false,'"+openGif+"','"+closedGif+"');"+NL;
			}
		}
		htmlStrings[0] = subName;
		htmlStrings[1] = treeStr;
		htmlStrings[2] = treeStrVariableDeclarations;
		return htmlStrings;
	}
	
	private static String[] validHotKeys = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
											"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
											"1","2","3","4","5","6","7","8","9","0"
											};
	
	private boolean isValidHotKey( String hotKey ) {
		boolean isValid = false;
		
		if(hotKey!=null && hotKey.length() > 0) {
			int validHotKeyLen	=	validHotKeys.length;
			for(int i=0; i < validHotKeyLen; i++) {
				if(hotKey.equals( validHotKeys[i])) {
					isValid = true;
					break;
				}
			}
		}		
		return isValid;
	}
	
}
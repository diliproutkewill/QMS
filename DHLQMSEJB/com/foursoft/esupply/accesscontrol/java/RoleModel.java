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
import java.io.Serializable;
import com.foursoft.esupply.accesscontrol.java.VersionedModel;

/**
 * File			: RoleModel.java
 * sub-module	: accesscontrol
 * module		: esupply
 * 
 * This is the Domain Object of the Role
 *
 * @author	Madhu. P, 
 * @date	28-08-2001
 * 
 * Modification History
 * 01.03.2003	Amit Parekh		Added three more attributes (version, modifiedBy, modifedDate)
 *								to handle versioning of the Entity represented by this Data Object.
 *                              This was handled by subclassing additonal functioanlity of a
 *								Versioned Entity Model class
 */

public class RoleModel extends VersionedModel implements Serializable
{
	
	private String 		roleType 		= null; // role Type (SPECIFI or GENERIC)
	private String 		roleLevel 		= null; // role level represents accessLevl (CORPORATE, PROJECT etc.,)
	private String 		roleModule 		= null; // role's module  (ETRANS, ELOG, EACCOUNTS, ESUPPLY )
	private String 		roleId 			= null; // roleId
	private String 		description 	= null; // description of the role
	private String 		locationId 		= null; // locationId to which the role belongs
	private ArrayList	rolePermissions	= null; // which holds the list of permission assosiated with the role
	
	public	String		ENTITY_LABEL	= "Role";

	public RoleModel()
	{
		rolePermissions	= new ArrayList();
	}
	
	/*
	* Constructor which takes roleId, locationId, roleModule, roleType, roleLevel, description and rolePermissions as argumentsas arguments
	* @param roleId
	* @param locationId
	* @param roleModule
	* @param roleLevel
	* @param roleType
	* @param description
	*/
	public RoleModel(String roleId, String locationId, String roleModule, String roleType, String roleLevel, String description, ArrayList rolePermissions)
	{
		this.roleId				= roleId;
		this.locationId			= locationId;
		this.roleModule			= roleModule;
		this.roleType			= roleType;
		this.roleLevel			= roleLevel;
		this.description		= description;
		this.rolePermissions	= rolePermissions;
	}	

	public String toString()
	{
		StringBuffer sb = new StringBuffer(100);
		//return 
		sb.append("RoleModel : "+"\r\n");
		sb.append("\t Role Id : "+roleId+"\r\n");
		sb.append("\t location Id : "+locationId+"\r\n");
		sb.append("\t Role Module : ");
		sb.append(roleModule);
		sb.append("\r\n");
		sb.append("\t Role Type : ");
		sb.append(roleType);
		sb.append("\r\n");
		sb.append("\t Role Level: ");
		sb.append(roleLevel);		
		sb.append("\r\n");
		sb.append("\t description : ");
		sb.append(description);
		sb.append("\r\n");
		sb.append("\t Role Permissions : ");
		sb.append("\t"+rolePermissions);

		return sb.toString();
	}
	
	/*
	* Constructor which takes roleId, locationId,  description  as argumentsas arguments
	* @param roleId
	* @param locationId
	* @param description
	*/
	public RoleModel(String roleId, String locationId,String description)
	{
		this.roleId				= roleId;
		this.locationId			= locationId;
		this.description		= description;
	}	
	
	/*
	* Constructor which takes roleId, locationId, roleModule, roleType, roleLevel, description as argumentsas arguments
	* @param roleId
	* @param locationId
	* @param roleModule
	* @param roleLevel
	* @param roleType
	* @param description
	*/
	public RoleModel(String roleId, String locationId, String roleModule, String roleType, String roleLevel, String description)
	{
		this.roleId				= roleId;
		this.locationId			= locationId;
		this.roleModule			= roleModule;
		this.roleType			= roleType;
		this.roleLevel			= roleLevel;
		this.description		= description;
	}	
	
	
	/*
	* Constructor which takes roleId, locationId, roleModule, roleType, roleLevel, description and rolePermissions as argumentsas arguments
	* @param roleId
	* @param locationId
	*/
	public RoleModel(String roleId, String locationId)
	{
		this.roleId				= roleId;
		this.locationId			= locationId;
	}	
	

	/** 
	 * Accessor method for  roleType.
	 * @returns java.lang.String which indicates .roleType
	 */
	public java.lang.String	getRoleType()
	{
		return	roleType;
	}
	/** 
	 * Mutator method for roleType.
	 * @param roleType as java.lang.String
	 */
	public void	setRoleType(java.lang.String  roleType)
	{
		this.roleType= roleType;
	}

	/** 
	 * Accessor method for  roleLevel.
	 * @returns java.lang.String which indicates .roleLevel
	 */
	public java.lang.String	getRoleLevel()
	{
		return	roleLevel;
	}
	/** 
	 * Mutator method for roleLevel.
	 * @param roleLevel as java.lang.String
	 */
	public void	setRoleLevel(java.lang.String  roleLevel)
	{
		this.roleLevel= roleLevel;
	}

	/** 
	 * Accessor method for  roleModule.
	 * @returns java.lang.String which indicates .roleModule
	 */
	public java.lang.String	getRoleModule()
	{
		return	roleModule;
	}
	/** 
	 * Mutator method for roleModule.
	 * @param roleModule as java.lang.String
	 */
	public void	setRoleModule(java.lang.String  roleModule)
	{
		this.roleModule= roleModule;
	}

	/** 
	 * Accessor method for  roleId.
	 * @returns java.lang.String which indicates .roleId
	 */
	public java.lang.String	getRoleId()
	{
		return	roleId;
	}
	/** 
	 * Mutator method for roleId.
	 * @param roleId as java.lang.String
	 */
	public void	setRoleId(java.lang.String  roleId)
	{
		this.roleId= roleId;
	}

	/** 
	 * Accessor method for  description.
	 * @returns java.lang.String which indicates .description
	 */
	public java.lang.String	getDescription()
	{
		if(this.description==null) {
			this.description = "";
		}
		return	this.description;
	}
	/** 
	 * Mutator method for description.
	 * @param description as java.lang.String
	 */
	public void	setDescription(java.lang.String  description)
	{
		if(description==null) {
			description = "";
		}
		this.description= description;
	}

	/** 
	 * Accessor method for  locationId.
	 * @returns java.lang.String which indicates .locationId
	 */
	public java.lang.String	getLocationId()
	{
		return	locationId;
	}
	/** 
	 * Mutator method for locationId.
	 * @param locationId as java.lang.String
	 */
	public void	setLocationId(java.lang.String  locationId)
	{
		this.locationId= locationId;
	}

	/** 
	 * Accessor method for  rolePermissions.
	 * @returns java.util.ArrayList which indicates .rolePermissions
	 */
	public java.util.ArrayList	getRolePermissions()
	{
		return	rolePermissions;
	}
	/** 
	 * Mutator method for rolePermissions.
	 * @param rolePermissions as java.util.ArrayList
	 */
	public void	setRolePermissions(java.util.ArrayList  rolePermissions)
	{
		this.rolePermissions= rolePermissions;
	}

}

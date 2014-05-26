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

/**
 * File			: UserRoleDOB.java
 * sub-module 	: Access Control
 * module 		: esupply
 * 
 * This reprersents the user, and his role information
 * This is used as ValueObjects also 
 * 
 * @author	Madhusudhan Rao. P, 
 * @date	28-08-2001
 */
public class UserRoleDOB implements java.io.Serializable
{
	public String accessType; // access type of the user
	public String userId; // user's Id
	public String locationId; // user's location Id
	public String userName; // name of the user
	public String roleLocationId; // role's location
	public String roleDescription; // description of the role
	public String roleId; // user's role
	public String emailId;
	
  public String empId;
  public String designationId; // user's role
	public String levelNo;
	//@@Added by Kameswari for the WPBN issue-61303 
  public String phoneNo;
  public String faxNo; // user's role
	public String mobileNo; 
	//@@WPBN issue-61303
	
  
	/*
	*	Constructor of the UserInfo class
	*	@param userId
	*	@param userName
	*	@param locationId
	*	@param accessType
	*	@param roleId			- user's role
	*	@param roleLocationId	- locationOf the to which it assosiates
	*	@param roleDescription 	- description of the role
	*/
	public UserRoleDOB( String userId, String locationId, String userName, String accessType, String roleId, String roleLocationId, String roleDescription, String emailId) 
	{
		this.userId = userId;
		this.locationId = locationId;
		this.userName = userName;
		this.accessType = accessType;
		this.roleId = roleId;
		this.roleLocationId = roleLocationId;
		this.roleDescription = roleDescription;
		this.emailId = emailId;
	}

	public UserRoleDOB( String userId, String locationId, String userName, String accessType, String roleId, String roleLocationId, String roleDescription) 
	{
		this.userId = userId;
		this.locationId = locationId;
		this.userName = userName;
		this.accessType = accessType;
		this.roleId = roleId;
		this.roleLocationId = roleLocationId;
		this.roleDescription = roleDescription;
	}
	
  public UserRoleDOB( String userId, String locationId, String userName, String accessType, String roleId, String roleLocationId, String roleDescription, String emailId,String empId,String designationId,String levelNo,String phoneNo,String faxNo,String mobileNo) 
	{
		this.userId = userId;
		this.locationId = locationId;
		this.userName = userName;
		this.accessType = accessType;
		this.roleId = roleId;
		this.roleLocationId = roleLocationId;
		this.roleDescription = roleDescription;
		this.emailId = emailId;
	  this.empId   = empId;
    this.designationId = designationId;
    this.levelNo = levelNo;
    //@@Added by Kameswari for the WPBN issue-61303
    this.phoneNo   = phoneNo;
    this.faxNo = faxNo;
    this.mobileNo = mobileNo;
    //@@WPBN issue-61303
  }
  
	/*
	* Empty constructor Initializes variables
	*/
	public UserRoleDOB() 
	{
		userId = new String();
		locationId = new String();
		userName = new String();
		accessType = new String();
		roleId = new String();
		roleLocationId = new String();
		roleDescription = new String();	
		emailId =  new String();
    
	}
}

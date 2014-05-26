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


import java.io.Serializable;
import java.util.Hashtable;
import java.util.ArrayList;
import com.foursoft.esupply.accesscontrol.java.VersionedModel;

/**
 * @author madhu
 *
 * Modification History
 * 12.11.2002	Amit Parekh		Added functionality for a User to take on multiple Roles provided the
 * 								Module is ELOG or EP and the User's Access Type is WAREHOUSE for ELOG and
 *								COMPANY for EP. New constructor added
 * 01.03.2003	Amit Parekh		Added three more attributes (version, modifiedBy, modifedDate)
 *								to handle versioning of the Entity represented by this Data Object.
 *                              This was handled by subclassing additonal functioanlity of a
 *								Versioned Entity Model class
 */
public class UserModel extends VersionedModel implements Serializable
{
	private String 		userId 			= null; // id of the user
	private String 		locationId 		= null; // location he is belongs to
	private String 		userName 		= null; // name of the user
	private String 		password 		= null; // password
	private String 		department 		= null; // user's department
	private String 		empId 			= null; // user's employee Id
	private String 		userLevel 		= null; // level of the user (corporate, terminal)
	private String 		companyId 		= null; // company which the user belongs

	private String 		roleId 			= null; // user's roleId
	private String 		roleLocationId 	= null; // location to which the user belongs to
	private String 		eMailId 		= null;

	public	String		ENTITY_LABEL	= "User";

	private ArrayList	otherRoleIds			= new ArrayList(10);
	private ArrayList	otherRoleLocationIds	= new ArrayList(10);

	private Hashtable	userPreferences	= null;
    private String      dbPassword      = null; // This field is added for keeping user user's passowrd from DataBase.
    public String backPassword;
	private String  designationId;
  
  private String  repOfficersCode;
  private String  allotedTime;
  private String  designationLevel;
  private String  repOffiecersName;
  private String  repOffDesignation;
  private String accessLevel;
  private String phoneNo;
  private String faxNo;
  private String mobileNo;
//@@ Added by subrahmanyam for Enhancement 167668 on 27/04/09
  private String custAddr1;
  private String custAddr2;
  private String custAddr3;
//@@ Ended by subrahmanyam for Enhancement 167668 on 27/04/09
  private ArrayList  repOfficersCode2= new ArrayList(10); //added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
  private ArrayList  shipmentModeCode2= new ArrayList(10); //added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
  private ArrayList  repOffiecersName2= new ArrayList(10);//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
  private ArrayList  repOffDesignation2= new ArrayList(10);//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
   private ArrayList  allotedTime2= new ArrayList(10);//added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
   private String shipmentModeCode; //added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009

	public UserModel()
	{
		userPreferences = new Hashtable();
	}
	
	public UserModel(String userId, String locationId, String userName, String password, String department, String empId, String userLevel, String companyId, String roleId, String eMailId, String roleLocationId )
	{
		this.userId 		= userId; 
		this.locationId 	= locationId; 
		this.userName 		= userName;
		this.password 		= password;
		this.department 	= department; 
		this.empId 			= empId;
		this.userLevel 		= userLevel; 
		this.companyId 		= companyId; 
		this.roleId 		= roleId; 
		this.eMailId		= eMailId;
		this.roleLocationId = roleLocationId; 
	}

	public UserModel(String userId, String locationId, String userName, String password, String department, String empId, String userLevel, String companyId, String roleId, String eMailId, String roleLocationId, Hashtable userPreferences)
	{
		this.userId 			= userId; 
		this.locationId 		= locationId; 
		this.userName 			= userName;
		this.password 			= password;
		this.department 		= department; 
		this.empId 				= empId;
		this.userLevel 			= userLevel; 
		this.companyId 			= companyId; 
		this.roleId 			= roleId; 
		this.eMailId			= eMailId;
		this.roleLocationId		= roleLocationId; 
		this.userPreferences	= userPreferences;
	}

	// Const added on 12-11-2002 for assigning surrogate roles for EP-COMPANY and ELOG-WAREHOUSE Use
	public UserModel(String userId, String locationId, String userName, String password, String department, String empId, String userLevel, String companyId, String roleId, String eMailId, String roleLocationId, ArrayList otherRoleIds, ArrayList otherRoleLocationIds, Hashtable userPreferences)
	{
		this.userId 			= userId; 
		this.locationId 		= locationId; 
		this.userName 			= userName;
		this.password 			= password;
		this.department 		= department; 
		this.empId 				= empId;
		this.userLevel 			= userLevel; 
		this.companyId 			= companyId; 
		this.roleId 			= roleId; 
		this.eMailId			= eMailId;
		this.roleLocationId		= roleLocationId;

		this.otherRoleIds			= otherRoleIds;
		this.otherRoleLocationIds	= otherRoleLocationIds;
		
		this.userPreferences	= userPreferences;
	}



// setter getters methods go here

	/** 
	 * Accessor method for  userId.
	 * @returns java.lang.String which indicates .userId
	 */
	public java.lang.String	getUserId()
	{
		return	userId;
	}
	/** 
	 * Mutator method for userId.
	 * @param userId as java.lang.String
	 */
	public void	setUserId(java.lang.String  userId)
	{
		this.userId= userId;
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
	 * Accessor method for  userName.
	 * @returns java.lang.String which indicates .userName
	 */
	public java.lang.String	getUserName()
	{
		return	userName;
	}
	/** 
	 * Mutator method for userName.
	 * @param userName as java.lang.String
	 */
	public void	setUserName(java.lang.String  userName)
	{
		this.userName= userName;
	}

	/** 
	 * Accessor method for  password.
	 * @returns java.lang.String which indicates .password
	 */
	public java.lang.String	getPassword()
	{
		return	password;
	}
	/** 
	 * Mutator method for password.
	 * @param password as java.lang.String
	 */
	public void	setPassword(java.lang.String  password)
	{
		this.password= password;
	}

	/** 
	 * Accessor method for  department.
	 * @returns java.lang.String which indicates .department
	 */
	public java.lang.String	getDepartment()
	{
		return	department;
	}
	/** 
	 * Mutator method for department.
	 * @param department as java.lang.String
	 */
	public void	setDepartment(java.lang.String  department)
	{
		this.department= department;
	}

	/** 
	 * Accessor method for  empId.
	 * @returns java.lang.String which indicates .empId
	 */
	public java.lang.String	getEmpId()
	{
		return	empId;
	}
	/** 
	 * Mutator method for empId.
	 * @param empId as java.lang.String
	 */
	public void	setEmpId(java.lang.String  empId)
	{
		this.empId= empId;
	}

	/** 
	 * Accessor method for  userLevel.
	 * @returns java.lang.String which indicates .userLevel
	 */
	public java.lang.String	getUserLevel()
	{
		return	userLevel;
	}
	/** 
	 * Mutator method for userLevel.
	 * @param userLevel as java.lang.String
	 */
	public void	setUserLevel(java.lang.String  userLevel)
	{
		this.userLevel= userLevel;
	}

	/** 
	 * Accessor method for  companyId.
	 * @returns java.lang.String which indicates .companyId
	 */
	public java.lang.String	getCompanyId()
	{
		return	companyId;
	}
	
	/** 
	 * Mutator method for companyId.
	 * @param companyId as java.lang.String
	 */
	public void	setCompanyId(java.lang.String  companyId)
	{
		this.companyId = companyId;
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
	 * Accessor method for  otherRoleIds.
	 * @returns java.util.ArrayList which indicates otherRoleIds
	 */
	public java.util.ArrayList	getOtherRoleIds()
	{
		return	otherRoleIds;
	}
	/** 
	 * Mutator method for otherRoleIds.
	 * @param otherRoleIds as java.util.ArrayList
	 */
	public void	setOtherRoleIds(java.util.ArrayList  otherRoleIds)
	{
		this.otherRoleIds = otherRoleIds;
	}

	/** 
	 * Accessor method for  roleLocationId.
	 * @returns java.lang.String which indicates .roleLocationId
	 */
	public java.lang.String	getRoleLocationId()
	{
		return	roleLocationId;
	}
	/** 
	 * Mutator method for roleLocationId.
	 * @param roleLocationId as java.lang.String
	 */
	public void	setRoleLocationId(java.lang.String  roleLocationId)
	{
		this.roleLocationId= roleLocationId;
	}


	/** 
	 * Accessor method for  otherRoleLocationIds.
	 * @returns java.util.ArrayList which indicates otherRoleLocationIds
	 */
	public java.util.ArrayList	getOtherRoleLocationIds()
	{
		return	otherRoleLocationIds;
	}
	/** 
	 * Mutator method for otherRoleLocationIds.
	 * @param otherRoleLocationIds as java.util.ArrayList
	 */
	public void	setOtherRoleLocationIds(java.util.ArrayList  otherRoleLocationIds)
	{
		this.otherRoleLocationIds = otherRoleLocationIds;
	}

	/** 
	 * Accessor method for  EMailId.
	 * @returns java.lang.String which indicates eMailId
	 */
	public String getEMailId()
	{
		return eMailId;
	}

	/** 
	 * Mutator method for EMailId.
	 * @param newEMailId as java.lang.String
	 */
	public void setEMailId(String newEMailId)
	{
		this.eMailId	= newEMailId;
	}	

	/** 
	 * Accessor method for  userPreferences.
	 * @returns UserPreferences which indicates userPreferences
	 */
	public Hashtable getUserPreferences()
	{
		return userPreferences;
	}

	/** 
	 * Mutator method for userPreferences.
	 * @param userPreferences as UserPreferences
	 */
	public void setUserPreferences(Hashtable userPreferences)
	{
		this.userPreferences	= userPreferences;
	}

	public String toString()
	{	    return userId + " - "+ locationId + " - "+ userName + " - "+ password + " - "+ department + " - "+ empId + " - "+ userLevel + " - "+ companyId + " - "+ roleId + " - "+ roleLocationId+" - "+ eMailId+"\n"+" User Preferences : "+userPreferences; 
	}
	
	public Hashtable validateUserModel()
	{
		Hashtable errorFields = new Hashtable(5);
		/*if(userId.length() > 16 )
			errorFields.put("User Id","User Id length should not exceed 16 characters");*/
      //@@Commented and Modified by Kameswari for the WPBN issue-
    if(userId.length() > 12 )
			errorFields.put("User Id","User Id length should not exceed 12 characters");  //@@Modified by Kameswari for enhancements.
		if(locationId.length() > 16 )
			errorFields.put("Location Id","Location Id length should not exceed 16 characters");
		if(userName.length() > 30 )
			errorFields.put("User Name","User Name length should not exceed 30 characters");
		if(password.length() < 4 )
			errorFields.put("Password","Error, Encryption logic is not working");
		if(empId.length() > 16 )
			errorFields.put("Employee Id","Employee Id length should not exceed 16 characters");
		//@@Commented and Modified by Anusha for the WPBN Issue-389773
		/*if(department.length() > 25 )
			errorFields.put("Department","Department length should not exceed 25 characters");*/
		if(department.length() > 45 )
			errorFields.put("Department","Department length should not exceed 45 characters");//@@Modified by Anusha for the WPBN Issue-389773.
		if(userLevel.length() > 16 )
			errorFields.put("User Level","User Level length should not exceed 16 characters");
		if(companyId.length() > 16 )
			errorFields.put("Company Id","Company Id length should not exceed 16 characters");

		return errorFields;
		
	}	

    public String getDbPassword()
    {
        return dbPassword;
    }

    public void setDbPassword(String newDbPassword)
    {
        dbPassword = newDbPassword;
    }

    public String getBackPassword()
    {
        return backPassword;
    }

    public void setBackPassword(String newBackPassword)
    {
        backPassword = newBackPassword;
    }


  public void setDesignationId(String designationId)
  {
    this.designationId = designationId;
  }


  public String getDesignationId()
  {
    return designationId;
  }


  public void setRepOfficersCode(String repOfficersCode)
  {
    this.repOfficersCode = repOfficersCode;
  }


  public String getRepOfficersCode()
  {
    return repOfficersCode;
  }


  public void setAllotedTime(String allotedTime)
  {
    this.allotedTime = allotedTime;
  }


  public String getAllotedTime()
  {
    return allotedTime;
  }


  public void setDesignationLevel(String designationLevel)
  {
    this.designationLevel = designationLevel;
  }


  public String getDesignationLevel()
  {
    return designationLevel;
  }


  public void setRepOffiecersName(String repOffiecersName)
  {
    this.repOffiecersName = repOffiecersName;
  }


  public String getRepOffiecersName()
  {
    return repOffiecersName;
  }


  public void setRepOffDesignation(String repOffDesignation)
  {
    this.repOffDesignation = repOffDesignation;
  }


  public String getRepOffDesignation()
  {
    return repOffDesignation;
  }

  public String getAccessLevel()
  {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }

 //@@Added by Kameswari for the WPBN issue-61303
  public String getPhoneNo()
  {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo)
  {
    this.phoneNo = phoneNo;
  }

  public String getFaxNo()
  {
    return faxNo;
  }

  public void setFaxNo(String faxNo)
  {
    this.faxNo = faxNo;
  }

  public String getMobileNo()
  {
    return mobileNo;
  }

  public void setMobileNo(String mobileNo)
  {
    this.mobileNo = mobileNo;
  }
  //@@WPBN issue-61303
//@@ Added by subrahmanyam for Enhancement 167668  on 27/04/09
public void setCustAddr1(String custAddr1)
{
  this.custAddr1=custAddr1;
}
public String getCustAddr1()
{
  return custAddr1;
}
public void setCustAddr2(String custAddr2)
{
  this.custAddr2=custAddr2;
}
public String getCustAddr2()
{
  return custAddr2;
}
public void setCustAddr3(String custAddr3)
{
  this.custAddr3=custAddr3;
}
public String getCustAddr3()
{
  return custAddr3;
}
//@@ Ended  by subrahmanyam for Enhancement 167668  on 27/04/09
  public void setShipmentModeCode(String shipmentModeCode)
  {
    this.shipmentModeCode = shipmentModeCode;
  }


  public String getShipmentModeCode()
  {
    return shipmentModeCode;
  }


  public void setRepOfficersCode2(ArrayList repOfficersCode2)
  {
    this.repOfficersCode2 = repOfficersCode2;
  }


  public ArrayList getRepOfficersCode2()
  {
    return repOfficersCode2;
  }


  public void setShipmentModeCode2(ArrayList shipmentModeCode2)
  {
    this.shipmentModeCode2 = shipmentModeCode2;
  }


  public ArrayList getShipmentModeCode2()
  {
    return shipmentModeCode2;
  }


  public void setRepOffiecersName2(ArrayList repOffiecersName2)
  {
    this.repOffiecersName2 = repOffiecersName2;
  }


  public ArrayList getRepOffiecersName2()
  {
    return repOffiecersName2;
  }


  public void setRepOffDesignation2(ArrayList repOffDesignation2)
  {
    this.repOffDesignation2 = repOffDesignation2;
  }


  public ArrayList getRepOffDesignation2()
  {
    return repOffDesignation2;
  }


  public void setAllotedTime2(ArrayList allotedTime2)
  {
    this.allotedTime2 = allotedTime2;
  }


  public ArrayList getAllotedTime2()
  {
    return allotedTime2;
  }


}

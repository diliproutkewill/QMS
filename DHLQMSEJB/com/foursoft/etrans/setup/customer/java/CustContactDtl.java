package com.foursoft.etrans.setup.customer.java;
import java.io.Serializable;

public class CustContactDtl implements Serializable
{
  public CustContactDtl()
  {
  }
  public void setAddrType(String addrType)
  {
    this.addrType=addrType;
  }
  public void setContactPerson(String contactPerson)
  {
    this.contactPerson=contactPerson;
  }
  public void setDesignation(String designation)
  {
    this.designation=designation;
  }
  public void setDept(String dept)
  {
    this.dept=dept;
  }
  public void setZipCode(String zipCode)
  {
    this.zipCode=zipCode;
  }
  public void setContact(String contact)
  {
    this.contact=contact;
  }
  public void setFax(String fax)
  {
    this.fax=fax;
  }
  public void setEmail(String eMail)
  {
    this.eMail=eMail;
  }
  public String getAddrType()
  {
    return addrType;
  }
  public void setdeleteOption(String deleteOption)
	{
	 this.deleteOption=deleteOption;
	}
	public String getdeleteOption()
	{
		return deleteOption;
	}
  public String getContactPerson()
  {
    return contactPerson;
  }
  public String getDesignation()
  {
    return designation;
  }
  public String getDept()
  {
    return dept;
  }
  public String getZipCode()
  {
    return zipCode;
  }
  public String getContact()
  {
    return contact;
  }
  public String getFax()
  {
    return fax;
  }
  public String getEmail()
  {
    return eMail;
  }
  private String addrType;
  private String contactPerson;
  private String designation;
  private String dept;
  private String zipCode;
  private String contact;
  private String fax;
  private String eMail;
  private String deleteOption;
  private int contactId;

  public int getContactId()
  {
    return contactId;
  }

  public void setContactId(int contactId)
  {
    this.contactId = contactId;
  }
}
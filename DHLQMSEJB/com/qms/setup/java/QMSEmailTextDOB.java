/**
*
* Copyright (c) 2007-2008 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* @version 
* @author--P.Kameswari
* @Created Date--28/12/2006
* 
*/
package com.qms.setup.java;
import java.io.Serializable;
import java.sql.Timestamp;
/**
 * This class will be useful to store the details of email text in email text master. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class QMSEmailTextDOB implements Serializable
{
  public QMSEmailTextDOB()
  {
  }
  private String quoteType;
  private String emailText;
  private String terminalId;
  private long id;
  private String createdBy;
  private Timestamp createdDate;
  private String accessType;
  private boolean flag;
  private String modifiedBy;
  private Timestamp modifiedDate;
  private String loginTerminal;
  private String loginAccessType;
 


  public void setLoginTerminal(String loginTerminal)
  {
    this.loginTerminal = loginTerminal;
  }


  public String getLoginTerminal()
  {
    return loginTerminal;
  }
  public void setLoginAccessType(String loginAccessType)
  {
    this.loginAccessType = loginAccessType;
  }


  public String getLoginAccessType()
  {
    return loginAccessType;
  }
  public void setEmailText(String emailText)
  {
    this.emailText = emailText;
  }


  public String getEmailText()
  {
    return emailText;
  }


  public void setQuoteType(String quoteType)
  {
    this.quoteType = quoteType;
  }


  public String getQuoteType()
  {
    return quoteType;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getCreatedBy()
  {
    return createdBy;
  }

  public void setCreatedBy(String createdBy)
  {
    this.createdBy = createdBy;
  }

  public Timestamp getCreatedDate()
  {
    return createdDate;
  }

  public void setCreatedDate(Timestamp createdDate)
  {
    this.createdDate = createdDate;
  }

  public String getAccessType()
  {
    return accessType;
  }

  public void setAccessType(String accessType)
  {
    this.accessType = accessType;
  }

  public boolean isFlag()
  {
    return flag;
  }

  public void setFlag(boolean flag)
  {
    this.flag = flag;
  }

  public String getModifiedBy()
  {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy)
  {
    this.modifiedBy = modifiedBy;
  }

  public Timestamp getModifiedDate()
  {
    return modifiedDate;
  }

  public void setModifiedDate(Timestamp modifiedDate)
  {
    this.modifiedDate = modifiedDate;
  }



   
}
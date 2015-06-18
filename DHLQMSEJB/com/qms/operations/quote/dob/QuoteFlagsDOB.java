/**
*
* Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* 
* This class will be useful to store the Various Flags of a Quote.
* @version 	
*
*/

/*
Programme Name		  :QuoteFlagsDOB.java
Module  Name  		  :Quote.
Task           		  :         
SubTask       		  :   
Author Name         :Yuvraj
Date Started        :
Date Finished       :7th Oct 2005 
Date Modified       :
Description         :
Method's Summary	  :
*/
package com.qms.operations.quote.dob;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;


public class QuoteFlagsDOB implements java.io.Serializable
{
  private long   quoteId;
  
  private String pNFlag;
  private String updateFlag;
  private String activeFlag;  
  private String completeFlag;
  private String quoteStatusFlag;  
  private String escalationFlag;
  
  private String sentFlag;
  private String emailFlag;
  private String faxFlag;
  private String printFlag;
  private String internalExternalFlag;
  public long getQuoteId()
  {
    return quoteId;
  }

  public void setQuoteId(long quoteId)
  {
    this.quoteId = quoteId;
  }

  public String getPNFlag()
  {
    return pNFlag;
  }

  public void setPNFlag(String pNFlag)
  {
    this.pNFlag = pNFlag;
  }

  public String getUpdateFlag()
  {
    return updateFlag;
  }

  public void setUpdateFlag(String updateFlag)
  {
    this.updateFlag = updateFlag;
  }

  public String getActiveFlag()
  {
    return activeFlag;
  }

  public void setActiveFlag(String activeFlag)
  {
    this.activeFlag = activeFlag;
  }

  public String getSentFlag()
  {
    return sentFlag;
  }

  public void setSentFlag(String sentFlag)
  {
    this.sentFlag = sentFlag;
  }



  public String getCompleteFlag()
  {
    return completeFlag;
  }

  public void setCompleteFlag(String completeFlag)
  {
    this.completeFlag = completeFlag;
  }

  public String getQuoteStatusFlag()
  {
    return quoteStatusFlag;
  }

  public void setQuoteStatusFlag(String quoteStatusFlag)
  {
    this.quoteStatusFlag = quoteStatusFlag;
  }

  public String getInternalExternalFlag()
  {
    return internalExternalFlag;
  }

  public void setInternalExternalFlag(String internalExternalFlag)
  {
    this.internalExternalFlag = internalExternalFlag;
  }

  public String getEscalationFlag()
  {
    return escalationFlag;
  }

  public void setEscalationFlag(String escalationFlag)
  {
    this.escalationFlag = escalationFlag;
  }

  public String getEmailFlag()
  {
    return emailFlag;
  }

  public void setEmailFlag(String emailFlag)
  {
    this.emailFlag = emailFlag;
  }

  public String getFaxFlag()
  {
    return faxFlag;
  }

  public void setFaxFlag(String faxFlag)
  {
    this.faxFlag = faxFlag;
  }

  public String getPrintFlag()
  {
    return printFlag;
  }

  public void setPrintFlag(String printFlag)
  {
    this.printFlag = printFlag;
  }
  
  public boolean equals(QuoteFlagsDOB flagsDOB)
  {
    if(this.getPNFlag()==null)
     this.setPNFlag("");
    if(this.getActiveFlag()==null)
     this.setActiveFlag("");
    if(this.getCompleteFlag()==null)
     this.setCompleteFlag("");
    if(this.getEscalationFlag()==null)
     this.setEscalationFlag("");
    if(this.getUpdateFlag()==null)
     this.setUpdateFlag("");
    if(this.getQuoteStatusFlag()==null)
     this.setQuoteStatusFlag("");
    if(this.getInternalExternalFlag()==null)
     this.setInternalExternalFlag("");
     
    //Logger.info("Flags DOB","flagsDOB.getPNFlag()::"+flagsDOB.getPNFlag()+",this.getPNFlag():"+this.getPNFlag());
    //Logger.info("Flags DOB","flagsDOB.getActiveFlag()::"+flagsDOB.getActiveFlag()+",this.getActiveFlag():"+this.getActiveFlag());
    //Logger.info("Flags DOB","flagsDOB.getCompleteFlag()::"+flagsDOB.getCompleteFlag()+",this.getCompleteFlag():"+this.getCompleteFlag());
    //Logger.info("Flags DOB","flagsDOB.getEscalationFlag()::"+flagsDOB.getEscalationFlag()+",this.getEscalationFlag():"+this.getEscalationFlag());
    //Logger.info("Flags DOB","flagsDOB.getUpdateFlag()::"+flagsDOB.getUpdateFlag()+",this.getUpdateFlag():"+this.getUpdateFlag());
    //Logger.info("Flags DOB","flagsDOB.getQuoteStatusFlag()::"+flagsDOB.getQuoteStatusFlag()+",this.getQuoteStatusFlag():"+this.getQuoteStatusFlag());
    //Logger.info("Flags DOB","flagsDOB.getQuoteStatusFlag()::"+flagsDOB.getInternalExternalFlag()+",this.getQuoteStatusFlag():"+this.getInternalExternalFlag());
     
    if((flagsDOB.getPNFlag()).equalsIgnoreCase(this.getPNFlag()) && 
       (flagsDOB.getActiveFlag()).equalsIgnoreCase(this.getActiveFlag()) &&
       (flagsDOB.getCompleteFlag()).equalsIgnoreCase(this.getCompleteFlag()) && 
       (flagsDOB.getEscalationFlag()).equalsIgnoreCase(this.getEscalationFlag()) && 
       (flagsDOB.getUpdateFlag()).equalsIgnoreCase(this.getUpdateFlag()) && 
       (flagsDOB.getQuoteStatusFlag()).equalsIgnoreCase(this.getQuoteStatusFlag()) && 
       (flagsDOB.getInternalExternalFlag()).equalsIgnoreCase(this.getInternalExternalFlag())
      )
      {
        return true;
      }
      else
       return false;
   }
}
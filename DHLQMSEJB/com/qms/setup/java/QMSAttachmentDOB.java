/**
*
* Copyright (c) 2007-2008 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* @version 
* @author--P.Kameswari
* @Created Date--20/01/2007
* 
*/
package com.qms.setup.java;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * This class will be useful to store the details of attachmentId in attachment master. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class QMSAttachmentDOB implements Serializable
{
  
  private String accessType;
  private String terminalId;
  private String attachmentId;
  private ArrayList qmsAttachmentDetailDOBList;
  private ArrayList qmsAttachmentFileDOBList;
  private String invalidate;
  private String loginTerminal;
  private String loginAccessType;
 
  public QMSAttachmentDOB()
  {
  }

  public String getAccessType()
  {
    return accessType;
  }

  public void setAccessType(String accessType)
  {
    this.accessType = accessType;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
  }

  public ArrayList getQmsAttachmentDetailDOBList()
  {
    return qmsAttachmentDetailDOBList;
  }

  public void setQmsAttachmentDetailDOBList(ArrayList qmsAttachmentDetailDOBList)
  {
    this.qmsAttachmentDetailDOBList = qmsAttachmentDetailDOBList;
  }

  public ArrayList getQmsAttachmentFileDOBList()
  {
    return qmsAttachmentFileDOBList;
  }

  public void setQmsAttachmentFileDOBList(ArrayList qmsAttachmentFileDOBList)
  {
    this.qmsAttachmentFileDOBList = qmsAttachmentFileDOBList;
  }

  public String getInvalidate()
  {
    return invalidate;
  }

  public void setInvalidate(String invalidate)
  {
    this.invalidate = invalidate;
  }
  public String getLoginTerminal()
  {
    return loginTerminal;
  }

  public void setLoginTerminal(String loginTerminal)
  {
    this.loginTerminal = loginTerminal;
  }
  public String getLoginAccessType()
  {
    return loginAccessType;
  }

  public void setLoginAccessType(String loginAccessType)
  {
    this.loginAccessType = loginAccessType;
  }

 }
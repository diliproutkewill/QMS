/**
 * Copyright (c) 2000-2001 Four-Soft Pvt Ltd,
 * 5Q1A3, Hi-Tech City, Madhapur, Hyderabad-33, India.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of  Four-Soft Pvt Ltd,
 * ("Confidential Information").  You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement you entered
 * into with Four-Soft. For more information on the Four Soft Pvt Ltd
 *
 

 * File					: MultiQuoteAttachmentDOB.java
 * @author				: Govind
 * @date				: 
 *CR-                   :CR-DHLQMS-CR-219979&80


 *	This Controller is used to control the flow in the quote module
 */
package com.qms.operations.multiquote.dob;
import java.io.Serializable;

/**
 * This class will be useful to store the list of attachmentIds in quote summary. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class MultiQuoteAttachmentDOB implements Serializable
{
  private long id;
  private String attachmentId;
  private String fileName;
  private byte[] pdfFile;
  private String userId;
  private String terminalId;

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getAttachmentId()
  {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId)
  {
    this.attachmentId = attachmentId;
  }

  public String getFileName()
  {
    return fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public byte[] getPdfFile()
  {
    return pdfFile;
  }

  public void setPdfFile(byte[] pdfFile)
  {
    this.pdfFile = pdfFile;
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }
}
/**
*
* Copyright (c) 2007-2008 by FourSoft, Pvt Ltd. All Rights Reserved.
* This software is the proprietary information of FourSoft, Pvt Ltd.
* Use is subject to license terms.
*
* QMS - v 1.x
* @version 
* @author--P.Kameswari
* @Created Date--19/02/2007
* 
*/
package com.qms.operations.quote.dob;
import java.io.Serializable;

/**
 * This class will be useful to store the list of attachmentIds in quote summary. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class QuoteAttachmentDOB implements Serializable
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
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
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ArrayList;
/**
 * This class will be useful to store the file details of attachmentId in attachment master. When data is retrived
* from Data Base the set methods are used and when the data has to be inserted or used any where then get methods are used.
 */
public class QMSAttachmentFileDOB implements Serializable
{
  private String fileName;
  private long id;
  private String attachmentId;
  private File pdfFile;

 

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


  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }


  public String getFileName()
  {
    return fileName;
  }


  public void setPdfFile(File pdfFile)
  {
    this.pdfFile = pdfFile;
  }


  public File getPdfFile()
  {
    return pdfFile;
  }
 
}
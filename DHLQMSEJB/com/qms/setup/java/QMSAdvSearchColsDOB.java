package com.qms.setup.java;
import java.io.Serializable;

public class QMSAdvSearchColsDOB implements Serializable
{
  
  private String colId;
  private String colDesc;
  private String colWidth;
  private String sortType;

  public String getColId()
  {
    return colId;
  }

  public void setColId(String colId)
  {
    this.colId = colId;
  }

  public String getColDesc()
  {
    return colDesc;
  }

  public void setColDesc(String colDesc)
  {
    this.colDesc = colDesc;
  }

  public String getColWidth()
  {
    return colWidth;
  }

  public void setColWidth(String colWidth)
  {
    this.colWidth = colWidth;
  }

  public String getSortType()
  {
    return sortType;
  }

  public void setSortType(String sortType)
  {
    this.sortType = sortType;
  }
}
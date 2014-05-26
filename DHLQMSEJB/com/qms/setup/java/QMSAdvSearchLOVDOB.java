package com.qms.setup.java;
import java.io.Serializable;

public class QMSAdvSearchLOVDOB implements Serializable
{
  private String lovWhere;
  private String pageNo;
  private String lovId;
  private String tabArray;
  private String formArray;
  private String sortType;
  private String sortFeild;
  private String operation;
  private String designationId;
  private String terminalId;
  private String whereCondition;
  private String shipmentMode;
  private String accessLevel;
  private String title;
  private String pagination;
  private int noOfRecPerPage;
  private int colCount;
  private int recCount;
  private int winWidth;
  private int winHeight;
  private String buyRatesPermission;//@@Added by Kameswari for the WPBN issue-26514
  private String empId;//@@Added by Kameswari for the WPBN issue-26514
private String localTerminal ;  ///added by VLAKSHMI on 22/05/2009
private String localAcceslevel;///added by VLAKSHMI on 22/05/2009
  private String multiQuote;///added by Rakesh on 16-03-2011
  public String getLovWhere()
  {
    return lovWhere;
  }

  public void setLovWhere(String lovWhere)
  {
    this.lovWhere = lovWhere;
  }

  public String getPageNo()
  {
    return pageNo;
  }

  public void setPageNo(String pageNo)
  {
    this.pageNo = pageNo;
  }

  public String getLovId()
  {
    return lovId;
  }

  public void setLovId(String lovId)
  {
    this.lovId = lovId;
  }

  public String getTabArray()
  {
    return tabArray;
  }

  public void setTabArray(String tabArray)
  {
    this.tabArray = tabArray;
  }

  public String getFormArray()
  {
    return formArray;
  }

  public void setFormArray(String formArray)
  {
    this.formArray = formArray;
  }

  public String getSortType()
  {
    return sortType;
  }

  public void setSortType(String sortType)
  {
    this.sortType = sortType;
  }

  public String getSortFeild()
  {
    return sortFeild;
  }

  public void setSortFeild(String sortFeild)
  {
    this.sortFeild = sortFeild;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }

  public String getDesignationId()
  {
    return designationId;
  }

  public void setDesignationId(String designationId)
  {
    this.designationId = designationId;
  }

  public String getTerminalId()
  {
    return terminalId;
  }

  public void setTerminalId(String terminalId)
  {
    this.terminalId = terminalId;
  }

  public String getWhereCondition()
  {
    return whereCondition;
  }

  public void setWhereCondition(String whereCondition)
  {
    this.whereCondition = whereCondition;
  }

  public String getShipmentMode()
  {
    return shipmentMode;
  }

  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }

  public String getAccessLevel()
  {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel)
  {
    this.accessLevel = accessLevel;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getPagination()
  {
    return pagination;
  }

  public void setPagination(String pagination)
  {
    this.pagination = pagination;
  }

  public int getNoOfRecPerPage()
  {
    return noOfRecPerPage;
  }

  public void setNoOfRecPerPage(int noOfRecPerPage)
  {
    this.noOfRecPerPage = noOfRecPerPage;
  }

  public int getColCount()
  {
    return colCount;
  }

  public void setColCount(int colCount)
  {
    this.colCount = colCount;
  }

  public int getRecCount()
  {
    return recCount;
  }

  public void setRecCount(int recCount)
  {
    this.recCount = recCount;
  }

  public int getWinWidth()
  {
    return winWidth;
  }

  public void setWinWidth(int winWidth)
  {
    this.winWidth = winWidth;
  }

  public int getWinHeight()
  {
    return winHeight;
  }

  public void setWinHeight(int winHeight)
  {
    this.winHeight = winHeight;
  }
  //@@Added by Kameswari for the WPBN issue-26514
  public String getBuyRatesPermission()
  {
    return buyRatesPermission;
  }

  public void setBuyRatesPermission(String buyRatesPermission)
  {
    this.buyRatesPermission = buyRatesPermission;
  }

  public String getEmpId()
  {
    return empId;
  }

  public void setEmpId(String empId)
  {
    this.empId = empId;
  }


  public void setLocalTerminal(String localTerminal)
  {
    this.localTerminal = localTerminal;
  }


  public String getLocalTerminal()
  {
    return localTerminal;
  }


  public void setLocalAcceslevel(String localAcceslevel)
  {
    this.localAcceslevel = localAcceslevel;
  }


  public String getLocalAcceslevel()
  {
    return localAcceslevel;
  }
//@@ WPBN issue-26514

public String getMultiQuote() {
	return multiQuote;
}

public void setMultiQuote(String multiQuote) {
	this.multiQuote = multiQuote;
}

}
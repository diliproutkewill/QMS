package com.qms.reports.java;
import java.io.Serializable;
import java.util.ArrayList;

public class UpdatedQuotesFinalDOB implements Serializable
{
  int changeDescSelectedIndex;
  private int[] selectedQuotesIndices;
  private ArrayList updatedQuotesList;
  private ArrayList changeDescList;
  private ArrayList approvedQuotesList;
  private String buyRatesFlag;
  String[] changeDescSelectedIndexArr;//Added by Anil.k for Excel Page
  
  public UpdatedQuotesFinalDOB()
  {
    this.setChangeDescSelectedIndex(-1);
  }
  
  public int getChangeDescSelectedIndex()
  {
    return changeDescSelectedIndex;
  }

  public void setChangeDescSelectedIndex(int changeDescSelectedIndex)
  {
    this.changeDescSelectedIndex = changeDescSelectedIndex;
  }

  public int[] getSelectedQuotesIndices()
  {
    return selectedQuotesIndices;
  }

  public void setSelectedQuotesIndices(int[] selectedQuotesIndices)
  {
    this.selectedQuotesIndices = selectedQuotesIndices;
  }

  public ArrayList getUpdatedQuotesList()
  {
    return updatedQuotesList;
  }

  public void setUpdatedQuotesList(ArrayList updatedQuotesList)
  {
    this.updatedQuotesList = updatedQuotesList;
  }

  public ArrayList getChangeDescList()
  {
    return changeDescList;
  }

  public void setChangeDescList(ArrayList changeDescList)
  {
    this.changeDescList = changeDescList;
  }

  public ArrayList getApprovedQuotesList()
  {
    return approvedQuotesList;
  }

  public void setApprovedQuotesList(ArrayList approvedQuotesList)
  {
    this.approvedQuotesList = approvedQuotesList;
  }

  public String getBuyRatesFlag()
  {
    return buyRatesFlag;
  }

  public void setBuyRatesFlag(String buyRatesFlag)
  {
    this.buyRatesFlag = buyRatesFlag;
  }
//Added by Anil.k for Excel Page
public String[] getChangeDescSelectedIndexArr() {
	return changeDescSelectedIndexArr;
}

public void setChangeDescSelectedIndexArr(String[] changeDescSelectedIndexArr) {
	this.changeDescSelectedIndexArr = changeDescSelectedIndexArr;
}//Ended by Anil.k for Excel Page
  
}
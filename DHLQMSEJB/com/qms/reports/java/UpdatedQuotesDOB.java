package com.qms.reports.java;
import java.io.Serializable;

public class UpdatedQuotesDOB implements Serializable
{
  private String changeDesc;
  private int updatedQuotes;
  private int confirmedQuotes;
  private int unconfirmedQuotes;

  public String getChangeDesc()
  {
    return changeDesc;
  }

  public void setChangeDesc(String changeDesc)
  {
    this.changeDesc = changeDesc;
  }

  public int getUpdatedQuotes()
  {
    return updatedQuotes;
  }

  public void setUpdatedQuotes(int updatedQuotes)
  {
    this.updatedQuotes = updatedQuotes;
  }

  public int getConfirmedQuotes()
  {
    return confirmedQuotes;
  }

  public void setConfirmedQuotes(int confirmedQuotes)
  {
    this.confirmedQuotes = confirmedQuotes;
  }

  public int getUnconfirmedQuotes()
  {
    return unconfirmedQuotes;
  }

  public void setUnconfirmedQuotes(int unconfirmedQuotes)
  {
    this.unconfirmedQuotes = unconfirmedQuotes;
  }
}
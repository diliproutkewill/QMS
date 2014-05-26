package com.qms.operations.costing.dob;
import java.io.Serializable;

public class QuoteRateDetails implements Serializable 
{
  private String weightBreak;
  private double rate;
  private double lowerBound;
  private double upperBound;
  private String rateIndicator;

  public QuoteRateDetails()
  {
  }

  public String getWeightBreak()
  {
    return weightBreak;
  }

  public void setWeightBreak(String weightBreak)
  {
    this.weightBreak = weightBreak;
  }

  public double getRate()
  {
    return rate;
  }

  public void setRate(double rate)
  {
    this.rate = rate;
  }

  public double getLowerBound()
  {
    return lowerBound;
  }

  public void setLowerBound(double lowerBound)
  {
    this.lowerBound = lowerBound;
  }

  public double getUpperBound()
  {
    return upperBound;
  }

  public void setUpperBound(double upperBound)
  {
    this.upperBound = upperBound;
  }

  public String getRateIndicator()
  {
    return rateIndicator;
  }

  public void setRateIndicator(String rateIndicator)
  {
    this.rateIndicator = rateIndicator;
  }
}
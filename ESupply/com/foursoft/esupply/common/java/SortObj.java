package com.foursoft.esupply.common.java;  
import java.sql.Timestamp;
public class SortObj 
{
  public SortObj()
  {
    this.location = -1;
    this.value = "";
  }

  public SortObj(int param0, String param1)
  {
    this.location = param0;
    this.value = param1;
  }
  public SortObj(int param0, Timestamp param1)
  {
    this.location = param0;
    this.tvalue = param1;
  }

  public SortObj(int param0, Integer param1)
  {
    this.location = param0;
    this.intValue = param1;
  }
	 
  public SortObj(int param0, Double param1)
  {
    this.location = param0;
    this.doubleValue = param1;
  }


  public int getLocation()
  {
    return location;
  }

  public String getValue()
  {
    return value;
  }

  public Integer getIntValue()
  {
    return intValue;
  }

  public Double getDoubleValue()
  {
    return doubleValue;
  }

  public Timestamp getTValue()
  {
    return tvalue;
  }

  public void setLocation(int param0)
  {
    location = param0;
  }

  public void setValue(String param1)
  {
    value = param1;
  }

  public void setTValue(Timestamp param1)
  {
    tvalue = param1;
  }
  
  public void setIntValue(Integer param1)
  {
    intValue = param1;
  }
  public void setDoubleValue(Double param1)
  {
    doubleValue = param1;
  }

  private int location;
  private String value = "";
  private Timestamp tvalue;
  private Integer intValue;
  private Double doubleValue; 

}
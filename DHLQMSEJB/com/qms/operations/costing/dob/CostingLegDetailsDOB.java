package com.qms.operations.costing.dob;
import java.io.Serializable;
import java.util.ArrayList;
import java.sql.Timestamp;
public class CostingLegDetailsDOB implements Serializable
{
  
  private String origin;
  private String destination;
  private String frequency;
  private String transitTime;
  private String notes;
  private ArrayList costingChargeDetailList;
  private int legSerialNo;
 private Timestamp rateValidity;
  private String carrierName;
  private String frequencyChecked;
  private String transitChecked;
//  private String rateDescription;
//Added by Rakesh on 24-01-2011 for CR:231219
  String carrier		= 	null;
  String serviceLevel	=	null;
  
  
  
  public CostingLegDetailsDOB()
  {
  }


  public void setOrigin(String origin)
  {
    this.origin = origin;
  }


  public String getOrigin()
  {
    return origin;
  }


  public void setDestination(String destination)
  {
    this.destination = destination;
  }


  public String getDestination()
  {
    return destination;
  }


  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }


  public String getFrequency()
  {
    return frequency;
  }


  public void setTransitTime(String transitTime)
  {
    this.transitTime = transitTime;
  }


  public String getTransitTime()
  {
    return transitTime;
  }


  public void setNotes(String notes)
  {
    this.notes = notes;
  }


  public String getNotes()
  {
    return notes;
  }




  public void setLegSerialNo(int legSerialNo)
  {
    this.legSerialNo = legSerialNo;
  }


  public int getLegSerialNo()
  {
    return legSerialNo;
  }


  public void setCostingChargeDetailList(ArrayList costingChargeDetailList)
  {
    this.costingChargeDetailList = costingChargeDetailList;
  }


  public ArrayList getCostingChargeDetailList()
  {
    return costingChargeDetailList;
  }

  public Timestamp getRateValidity()
  {
    return rateValidity;
  }

  public void setRateValidity(Timestamp rateValidity)
  {
    this.rateValidity = rateValidity;
  }

public String getDummy()
{
  return "this is accepting....";
}

  public String getCarrierName()
  {
    return carrierName;
  }

  public void setCarrierName(String carrierName)
  {
    this.carrierName = carrierName;
  }

  public String getFrequencyChecked()
  {
    return frequencyChecked;
  }

  public void setFrequencyChecked(String frequencyChecked)
  {
    this.frequencyChecked = frequencyChecked;
  }

  public String getTransitChecked()
  {
    return transitChecked;
  }

  public void setTransitChecked(String transitChecked)
  {
    this.transitChecked = transitChecked;
  }

 /* public String getRateDescription()
  {
    return rateDescription;
  }

  public void setRateDescription(String rateDescription)
  {
    this.rateDescription = rateDescription;
  }*/

  public String getCarrier() {
	return carrier;
  }
  public void setCarrier(String carrier) {
	this.carrier = carrier;
  }

  public String getServiceLevel() {
	return serviceLevel;
  }
  public void setServiceLevel(String serviceLevel) {
	this.serviceLevel = serviceLevel;
  }
  //Ended by Rakesh on 24-01-2011 for CR:231219
}
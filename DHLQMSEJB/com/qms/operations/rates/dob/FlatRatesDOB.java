package com.qms.operations.rates.dob;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

public class FlatRatesDOB  implements Serializable
{
  
  private String origin;
  private String buyrateId;
  private String destination;
  private String serviceLevel;
  private Timestamp effDate;
  private String  effectiveDate;
  private String frequency;
  private String transittime;
  private String basic;//Added by govind for the CR-219973
  private String min;
  private String flat;
  private String notes;
  private String shipmentMode;
  private double upperBound;//Added by govind for the CR-219973
  private double lowerBound;//Added by govind for the CR-219973
  private String surchargeId;//Added by govind for the CR-219973
  private String chargeRateIndicator ;
  private String extNotes;//Added by Mohan for the cr-219976.
  private String overPivot;
  private String weightClass;
  private String wtBreakSlab;
  private double chargeRate; 
  private String chargeSlab;
  private int lineNo;
  private int laneNo;
  private String[]  rates;
  private String[]  slabValues;
  private String[]  flatValues;
  private String[]  listValues;
  private String invalidate;
  private Timestamp validUpto;
  private String carrierId;
  private int slNo       ;
  private ArrayList slabFlatList;
  private String    remarks;
  private ArrayList chargeRateList;
  private String    densityRatio;//added by rk
  //@@Added by Kameswari for Surcharge Enhancements
  private String fsbasic;
  private String fsmin;
  private String fskg;
  //private String typeofcharge;
  private Map breaksList;
  private String[] wtBreaks;
  private String typeofcharge;//@@Added by Kameswari for Surcharge Enhancements
  private String surchargeCurrency;//@@Added by govind for cr-219973
  private String[]  schCurr; // Added by gowtham for currency update
  public void setDensityRatio(String densityRatio)
  {
    this.densityRatio = densityRatio;
  }
  public String getDensityRatio()
  {
    return this.densityRatio;
  }
  public void setSlNo(int slNo)
  {
    this.slNo = slNo;
  }
  public int getSlNo()
  {
    return this.slNo;
  }
  public void setChargeRateList(ArrayList chargeRateList)
  {
    this.chargeRateList = chargeRateList;
  }
  public ArrayList getChargeRateList()
  {
    return chargeRateList;
  } 
  
  public void setslabFlatList(ArrayList slabFlatList)
  {
    this.slabFlatList = slabFlatList;
  }
  public ArrayList getSlabFlatList()
  {
    return slabFlatList;
  } 

  public void setValidUpto(Timestamp validUpto)
  {
    this.validUpto = validUpto;
  }
  public void setRemarks(String remarks)
  {
    this.remarks = remarks;
  }
  public String getRemarks()
  {
    return remarks;
  } 
  public void setCarrierId(String carrierId)
  {
    this.carrierId = carrierId;
  }
  public String getCarrierId()
  {
    return carrierId;
  }

  public Timestamp getValidUpto()
  {
    return validUpto;
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


  public void setServiceLevel(String serviceLevel)
  {
    this.serviceLevel = serviceLevel;
  }


  public String getServiceLevel()
  {
    return serviceLevel;
  }


  public void setEffDate(Timestamp effDate)
  {
    this.effDate = effDate;
  }


  public Timestamp getEffDate()
  {
    return effDate;
  }


  public void setFrequency(String frequency)
  {
    this.frequency = frequency;
  }


  public String getFrequency()
  {
    return frequency;
  }


  public void setTransittime(String transittime)
  {
    this.transittime = transittime;
  }


  public String getTransittime()
  {
    return transittime;
  }
  

  public void setMin(String min)
  {
    this.min = min;
  }


  public String getMin()
  {
    return min;
  }


  public void setFlat(String flat)
  {
    this.flat = flat;
  }


  public String getFlat()
  {
    return flat;
  }


  public void setNotes(String notes)
  {
    this.notes = notes;
  }


  public String getNotes()
  {
    return notes;
  }


  public void setSlabValues(String[] slabValues)
  {
    this.slabValues = slabValues;
  }


  public String[] getSlabValues()
  {
    return slabValues;
  }


  public void setFlatValues(String[] flatValues)
  {
    this.flatValues = flatValues;
  }


  public String[] getFlatValues()
  {
    return flatValues;
  }


  public void setWeightClass(String weightClass)
  {
    this.weightClass = weightClass;
  }


  public String getWeightClass()
  {
    return weightClass;
  }


  public void setShipmentMode(String shipmentMode)
  {
    this.shipmentMode = shipmentMode;
  }


  public String getShipmentMode()
  {
    return shipmentMode;
  }


  public void setEffectiveDate(String effectiveDate)
  {
    this.effectiveDate = effectiveDate;
  }


  public String getEffectiveDate()
  {
    return effectiveDate;
  }


  public void setChargeRate(double chargeRate)
  {
    this.chargeRate = chargeRate;
  }


  public double getChargeRate()
  {
    return chargeRate;
  }


  public void setUpperBound(double upperBound)
  {
    this.upperBound = upperBound;
  }


  public double getUpperBound()
  {
    return upperBound;
  }


  public void setLowerBound(double lowerBound)
  {
    this.lowerBound = lowerBound;
  }


  public double getLowerBound()
  {
    return lowerBound;
  }


  public void setChargeSlab(String chargeSlab)
  {
    this.chargeSlab = chargeSlab;
  }


  public String getChargeSlab()
  {
    return chargeSlab;
  }


  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }


  public int getLineNo()
  {
    return lineNo;
  }


  public void setLaneNo(int laneNo)
  {
    this.laneNo = laneNo;
  }


  public int getLaneNo()
  {
    return laneNo;
  }


  public void setWtBreakSlab(String wtBreakSlab)
  {
    this.wtBreakSlab = wtBreakSlab;
  }


  public String getWtBreakSlab()
  {
    return wtBreakSlab;
  }


  public void setChargeRateIndicator(String chargeRateIndicator)
  {
    this.chargeRateIndicator = chargeRateIndicator;
  }


  public String getChargeRateIndicator()
  {
    return chargeRateIndicator;
  }


  public void setListValues(String[] listValues)
  {
    this.listValues = listValues;
  }


  public String[] getListValues()
  {
    return listValues;
  }


  public void setOverPivot(String overPivot)
  {
    this.overPivot = overPivot;
  }


  public String getOverPivot()
  {
    return overPivot;
  }


  public void setRates(String[] rates)
  {
    this.rates = rates;
  }


  public String[] getRates()
  {
    return rates;
  }


  public void setBuyrateId(String buyrateId)
  {
    this.buyrateId = buyrateId;
  }


  public String getBuyrateId()
  {
    return buyrateId;
  }


  public void setInvalidate(String invalidate)
  {
    this.invalidate = invalidate;
  }


  public String getInvalidate()
  {
    return invalidate;
  }

  public String getFsbasic()
  {
    return fsbasic;
  }

  public void setFsbasic(String fsbasic)
  {
    this.fsbasic = fsbasic;
  }

  public String getFsmin()
  {
    return fsmin;
  }

  public void setFsmin(String fsmin)
  {
    this.fsmin = fsmin;
  }

  public String getFskg()
  {
    return fskg;
  }

  public void setFskg(String fskg)
  {
    this.fskg = fskg;
  }

  /*public String getTypeofcharge()
  {
    return typeofcharge;
  }

  public void setTypeofcharge(String typeofcharge)
  {
    this.typeofcharge = typeofcharge;
  }*/

  public Map getBreaksList()
  {
    return breaksList;
  }

  public void setBreaksList(Map breaksList)
  {
    this.breaksList = breaksList;
  }

  public String[] getWtBreaks()
  {
    return wtBreaks;
  }

  public void setWtBreaks(String[] wtBreaks)
  {
    this.wtBreaks = wtBreaks;
  }
//@@Added by Kameswari for Surcharge Enhancements
  public String getTypeofcharge()
  {
    return typeofcharge;
  }

  public void setTypeofcharge(String typeofcharge)
  {
    this.typeofcharge = typeofcharge;
  }
public String getSurchargeCurrency() {
	return surchargeCurrency;
}
public void setSurchargeCurrency(String surchargeCurrency) {
	this.surchargeCurrency = surchargeCurrency;
}
public String getBasic() {
	return basic;
}
public void setBasic(String basic) {
	this.basic = basic;
}
public String getSurchargeId() {
	return surchargeId;
}
public void setSurchargeId(String surchargeId) {
	this.surchargeId = surchargeId;
}
public String getExtNotes() {
	return extNotes;
}
public void setExtNotes(String extNotes) {
	this.extNotes = extNotes;
}
public String[] getSchCurr() {
	return schCurr;
}
public void setSchCurr(String[] schCurr) {
	this.schCurr = schCurr;
}

 
}
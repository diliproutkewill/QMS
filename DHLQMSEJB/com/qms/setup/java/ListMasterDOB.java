package com.qms.setup.java;
import java.io.Serializable;

public class ListMasterDOB implements Serializable
{
  private String shipmentMode;    
  private String uom;
  private String uov;
  private String uldType;
  private String description;
  private String volume;
  private String pivoteUladenWeight;
  private String overPivoteTareWeight;
  private String invalidate;
  

	public ListMasterDOB()
	 {
	 }
	 public void setShimpmentMode(String shipmentMode)
	 {
		 this.shipmentMode=shipmentMode;
	 }
	 public void setUom(String uom)
	{
		  this.uom=uom;
	}
	public void setUov(String uov)
	{
		this.uov=uov;
	}
	public void setUldType(String uldType)
	{
		this.uldType=uldType;
	}
	public void setDescription(String description)
	{
		this.description= description;
	}
	public void setVolume(String volume)
	{
		this.volume=volume;
	}
	public void setPivoteUladenWeight(String pivoteUladenWeight)
	{
		this.pivoteUladenWeight=pivoteUladenWeight;
	}
	public void setOverPivoteTareWeight(String overPivoteTareWeight)
	{
		this.overPivoteTareWeight=overPivoteTareWeight;
	}
  public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate;
  }
	public String getShipmentMode()
	{
		return shipmentMode;
	}
	public String getUom()
	{
		return uom;
	}
	public String getUov()
	{
		return uov;
	}
	public String getUldType()
	{
		return uldType;
	}
	public String getDescription()
	{
		return description;
	}
	public String getVolume()
	{
		return volume;
	}
	public String getPivoteUladenWeight()
	{
		return pivoteUladenWeight;
	}
	public String getOverPivoteTareWeight()
	{
		return overPivoteTareWeight;
	}
  public String getInvalidate()
  {
    return invalidate;
  }

}
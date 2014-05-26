package com.qms.setup.java;

public class ChargeBasisMasterDOB implements java.io.Serializable
{
  String chargeBasis    = null;
  String chargeDesc     = null;
  String block          = null;
  String primaryBasis   = null;
  String secondaryBasis = null;
  String tertiaryBasis  = null;
  String calculation    = null;
  String invalidate     = null;
  /**
   * 
   */
  public ChargeBasisMasterDOB()
  {
  }
   public void setInvalidate(String invalidate)
  {
    this.invalidate=invalidate  ;
  }
  public String getInvalidate()
  {
    return this.invalidate;
  }
  /**
   * 
   * @param chargeBasis
   */
  public void setChargeBasis(String chargeBasis)
  {
    this.chargeBasis= chargeBasis ;
  }
  /**
   * 
   * @return 
   */
  public String getChargeBasis()
  {
    return this.chargeBasis;
  }
  /**
   * 
   * @param chargeDesc
   */
    public void setChargeDesc(String chargeDesc)
  {
    this.chargeDesc=  chargeDesc;
  }
  /**
   * 
   * @return 
   */
  public String getChargeDesc()
  {
    return this.chargeDesc;
  }

  /**
   * 
   * @param block
   */
  public void setBlock(String block)
  {
    this.block  = block;
  }
  /**
   * 
   * @return 
   */
  public String getBlock()
  {
    return this.block;
  }
  /**
   * 
   * @return 
   */
  public String getPrimaryBasis()
  {
    return this.primaryBasis;
  }
    /**
   * 
   * @param primaryBasis
   */
    public void setPrimaryBasis(String primaryBasis)
  {
    this.primaryBasis=  primaryBasis;
  }
  /**
   * 
   * @param secondaryBasis
   */
    public void setSecondaryBasis(String secondaryBasis)
  {
    this.secondaryBasis=  secondaryBasis;
  }
  /**
   * 
   * @return 
   */
  public String getSecondaryBasis()
  {
    return this.secondaryBasis;
  }
  /**
   * 
   * @param tertiaryBasis
   */
    public void setTertiaryBasis(String tertiaryBasis)
  {
    this.tertiaryBasis=  tertiaryBasis;
  }
  /**
   * 
   * @return 
   */
  public String getTertiaryBasis()
  {
    return this.tertiaryBasis;
  }
  /**
   * 
   * @param calculation
   */
    public void setCalculation(String calculation)
  {
    this.calculation=  calculation;
  }
  /**
   * 
   * @return 
   */
  public String getCalculation()
  {
    return this.calculation;
  }
  
}
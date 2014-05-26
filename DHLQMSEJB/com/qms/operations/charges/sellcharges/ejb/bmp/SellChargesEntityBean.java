/**
 * @ (#) SellChargesEntityBean.java
 * Copyright (c) 2001 The Four Soft Pvt Ltd., 
 * 5Q1A3, Cyber Towers, 5th floor, HiTec City, Madhapur, Hyderabad - 33.
 * All rights reserved.
 *
 * This Software is the Confidential and proprietary information of Four Soft Pvt Ltd.
 * ("Confidential Information"). You shall not disclose such Confidential Information
 * and shall use it only in accordance with the terms of the license agreement
 * you entered into with Four Soft.
 */

/**
 * File : SellChargesEntityBean.java
 * Sub-Module : Sell charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.sellcharges.ejb.bmp;
import com.foursoft.esupply.common.exception.FoursoftException;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger; 

import com.qms.operations.charges.sellcharges.ejb.bmp.SellChargesEntityBeanPK;
import com.qms.operations.charges.sellcharges.dao.SellChargesDAO;
import com.qms.operations.charges.java.BuychargesHDRDOB;

public class SellChargesEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="BuyChargesEntityBean.java";
  private SellChargesDAO  sellChargesDAO=null;
  private SellChargesEntityBeanPK pkObj =null;
  private BuychargesHDRDOB  sellchargesHDRDOB=null;
  private boolean isValid   = false;  
  private static Logger logger = null;
  
  public SellChargesEntityBean()
  {
    logger  = Logger.getLogger(SellChargesEntityBean.class);
  }
  /**
   * 
   * @param buychargesHDRDOB
   */
  public void setSellchargesHDRDOB(BuychargesHDRDOB sellchargesHDRDOB)
  {
    this.sellchargesHDRDOB = sellchargesHDRDOB;
    isValid = true;
  }

  /**
   * 
   * @return 
   */
  public BuychargesHDRDOB getSellchargesHDRDOB()
  {
    return this.sellchargesHDRDOB;
  }
  /**
   * 
   * @return 
   * @param buychargesHDRDOB
   */
  public SellChargesEntityBeanPK ejbCreate(BuychargesHDRDOB sellchargesHDRDOB)throws FoursoftException
  {
      try
      {
        logger.info(FILE_NAME+" In EJB Create of Sell Charges");
        sellChargesDAO.create(sellchargesHDRDOB);
        //buychargesHDRDOB         = (BuychargesHDRDOB)dataList.get(0);
        this.pkObj				       = new SellChargesEntityBeanPK();
        this.pkObj.chargeId      = sellchargesHDRDOB.getChargeId();
        /*this.pkObj.chargeBasisId = sellchargesHDRDOB.getChargeBasisId();
        this.pkObj.rateBreak     = sellchargesHDRDOB.getRateBreak();
        this.pkObj.rateType      = sellchargesHDRDOB.getRateType();*/
        this.pkObj.chargeDescId  = sellchargesHDRDOB.getChargeBasisId();
        this.pkObj.terminalId    = sellchargesHDRDOB.getTerminalId();
      }catch(FoursoftException e)
      {
         e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in load(ArrayList param0) method"+e.getMessage());
        logger.error(FILE_NAME+"SQLException in load(ArrayList param0) method"+e.getMessage());
        throw new FoursoftException(e.getMessage());    
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ",e);
        logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+e);
        throw new javax.ejb.EJBException(e);      
      }finally
      {
        sellchargesHDRDOB  = null;
      }
      return this.pkObj;
  }

  /**
   * 
   * @param buychargesHDRDOB
   */
  public void ejbPostCreate(BuychargesHDRDOB sellchargesHDRDOB)
  {
  }

  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param pkObj
   */
  public SellChargesEntityBeanPK ejbFindByPrimaryKey(SellChargesEntityBeanPK pkObj)throws ObjectNotFoundException,SQLException
  {
      boolean sellChargesRow = false;
    try
    {
      this.sellChargesDAO.findByPrimariKey(pkObj);
      sellChargesRow     = true;
    }catch(ObjectNotFoundException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new ObjectNotFoundException();      
    }catch(SQLException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new SQLException();      
    }catch(Exception e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new EJBException();      
    }
    if(sellChargesRow)
     {  return pkObj;}
    else
     { throw new ObjectNotFoundException("bean could not Found-------->");}
  }

  /**
   * 
   */
  public void ejbActivate()
  {
  }

  /**
   * 
   */
  public void ejbLoad()
  {
    try
    {
      this.pkObj  = (SellChargesEntityBeanPK)context.getPrimaryKey();
      this.sellchargesHDRDOB = this.sellChargesDAO.load(this.pkObj);
    }catch(Exception e)
    {
        //Logger.error(FILE_NAME,"Exception in ejbLoad() method"+e.toString());
        logger.error(FILE_NAME+"Exception in ejbLoad() method"+e.toString());
        throw new EJBException();      
    }   
  }

  /**
   * 
   */
  public void ejbPassivate()
  {
  }

  /**
   * 
   */
  public void ejbRemove()
  {
   	try
		{
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the buychargesHDRDOB with Id  "+pkObj);
      logger.info(FILE_NAME+"ejbRemove()"+"Removing the buychargesHDRDOB with Id  "+pkObj);
			//this.sellChargesDAO.remove(this.pkObj);
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ejbRemove()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbRemove()"+"Could not Find Bean "+e);
			throw new javax.ejb.EJBException(e.toString());
		} 
    sellChargesDAO = null;
  }

  /**
   * 
   */
  public void ejbStore()
  {
		try 
		{
	
			if(isValid)
			{
				isValid=false;
				this.sellChargesDAO.store(this.pkObj,this.sellchargesHDRDOB);
				
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME," ejbStore()	","Exception while Storing  the Model ", e);
      logger.error(FILE_NAME+" ejbStore()	"+"Exception while Storing  the Model "+ e);
			throw new javax.ejb.EJBException(e.toString());
		}   
  }

  /**
   * 
   * @param ctx
   */
  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
    sellChargesDAO = new SellChargesDAO();
  }

  /**
   * 
   */
  public void unsetEntityContext()
  {
    this.context = null;
    sellChargesDAO = null;
  }
}
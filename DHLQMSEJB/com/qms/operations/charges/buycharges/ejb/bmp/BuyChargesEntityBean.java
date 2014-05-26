/**
 * @ (#) BuyChargesEntityBean.java
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
 * File : BuyChargesEntityBean.java
 * Sub-Module : Buy charges master
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.operations.charges.buycharges.ejb.bmp;
import com.foursoft.esupply.common.exception.FoursoftException;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.qms.operations.charges.buycharges.ejb.bmp.BuyChargesEntityBeanPK;
import com.qms.operations.charges.buycharges.dao.BuyChargesDAO;
import com.qms.operations.charges.java.BuychargesHDRDOB;

public class BuyChargesEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="BuyChargesEntityBean.java";
  private BuyChargesDAO  buyChargesDAO=null;
  private BuyChargesEntityBeanPK pkObj =null;
  private BuychargesHDRDOB  buychargesHDRDOB=null;
  private boolean isValid   = false;  
  private static Logger logger = null;

  public BuyChargesEntityBean()
  {
    logger  = Logger.getLogger(BuyChargesEntityBean.class);
  }
  /**
   * 
   * @param buychargesHDRDOB
   */
  public void setBuychargesHDRDOB(BuychargesHDRDOB buychargesHDRDOB)
  {
    this.buychargesHDRDOB = buychargesHDRDOB;
    isValid = true;
  }

  /**
   * 
   * @return 
   */
  public BuychargesHDRDOB getBuychargesHDRDOB()
  {
    return this.buychargesHDRDOB;
  }
  
  /**
   * 
   * @return 
   * @param dataList
   */
  public BuyChargesEntityBeanPK ejbCreate(BuychargesHDRDOB buychargesHDRDOB)throws FoursoftException
  {
    try
    {
      buyChargesDAO.create(buychargesHDRDOB);
      //buychargesHDRDOB         = (BuychargesHDRDOB)dataList.get(0);
      this.pkObj				       = new BuyChargesEntityBeanPK();
      this.pkObj.chargeId      = buychargesHDRDOB.getChargeId();
      //this.pkObj.chargeBasisId = buychargesHDRDOB.getChargeBasisId();
      this.pkObj.chargeDescId  = buychargesHDRDOB.getChargeDescId(); 
      this.pkObj.terminalId    = buychargesHDRDOB.getTerminalId();
    }catch(FoursoftException e)
      {
         e.printStackTrace();
        //Logger.error(FILE_NAME,"SQLException in load(ArrayList param0) method"+e.getMessage());
        logger.error(FILE_NAME+"SQLException in load(ArrayList param0) method"+e.getMessage());
        throw new FoursoftException(e.getMessage());    
      }
    catch(Exception e)
    {
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ",e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+e);
			throw new javax.ejb.EJBException(e);      
    }finally
    {
      buychargesHDRDOB  = null;
    }
    return this.pkObj;
  }

  /**
   * 
   * @param dataList
   */
  public void ejbPostCreate(BuychargesHDRDOB buychargesHDRDOB)
  {
  }

  /**
   * 
   * @throws java.sql.SQLException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param pkObj
   */
  public BuyChargesEntityBeanPK ejbFindByPrimaryKey(BuyChargesEntityBeanPK pkObj)throws ObjectNotFoundException,SQLException
  {
    boolean buyChargesRow = false;
    try
    {
      this.buyChargesDAO.findByPrimariKey(pkObj);
      buyChargesRow     = true;
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
    if(buyChargesRow)
     {  return pkObj;}
    else
     { throw new ObjectNotFoundException("bean could not Found-------->");}
  }

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
      this.pkObj  = (BuyChargesEntityBeanPK)context.getPrimaryKey();
      this.buychargesHDRDOB = this.buyChargesDAO.load(this.pkObj);
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
			//this.buyChargesDAO.remove(this.pkObj);
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ejbRemove()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbRemove()"+"Could not Find Bean "+e);
			throw new javax.ejb.EJBException(e.toString());
		}  
    buyChargesDAO = null;
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
				this.buyChargesDAO.store(this.pkObj,this.buychargesHDRDOB);
				
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
    buyChargesDAO = new BuyChargesDAO();
  }

  /**
   * 
   */
  public void unsetEntityContext()
  {
    this.context = null;
    buyChargesDAO = null;
  }
}
/**
 * @ (#) ChargesMasterEntityBean.java
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
 * File : ChargesMasterEntityBean.java
 * Sub-Module : ChargeMaster
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.chargemaster.ejb.bmp;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;

import com.qms.setup.chargemaster.ejb.bmp.ChargesMasterEntityBeanPK;
import com.qms.setup.chargemaster.dao.ChargeMasterDAO;
import com.qms.setup.java.ChargesMasterDOB;

public class ChargesMasterEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="ChargesMasterEntityBean.java";
  private ChargeMasterDAO  chargeMasterDAO=null;
  private ChargesMasterEntityBeanPK pkObj =null;
  private ChargesMasterDOB  chargesMasterDOB=null;
  private boolean isValid   = false;  
  private static Logger logger = null;
  
  public ChargesMasterEntityBean()
  {
    logger  = Logger.getLogger(ChargesMasterEntityBean.class);
  }
  
  /**
   * 
   * @param chargesMasterDOB
   */
  public void setChargesMasterDOB(ChargesMasterDOB chargesMasterDOB)
  {
    this.chargesMasterDOB = chargesMasterDOB;
    isValid = true;
  }
  /**
   * 
   * @return 
   */
  public ChargesMasterDOB getChagesMasterDOB()
  {
    return this.chargesMasterDOB;
  }
  /**
   * 
   * @return 
   * @param dataList
   */
  public ChargesMasterEntityBeanPK ejbCreate(java.util.ArrayList dataList)
  {
    try
    {
      logger.info("In EJBCreate of ChargesMasterEntityBean");
      chargeMasterDAO.create(dataList);
      chargesMasterDOB  = (ChargesMasterDOB)dataList.get(0);
      this.pkObj				  = new ChargesMasterEntityBeanPK();
      this.pkObj.chargeId  = chargesMasterDOB.getChargeId();
    }catch(Exception e)
    {
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ",e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+e);
			throw new javax.ejb.EJBException(e);      
    }finally
    {
      chargesMasterDOB  = null;
    }
    return this.pkObj;
  }

  /**
   * 
   * @param dataList
   */
  public void ejbPostCreate(java.util.ArrayList dataList)
  {
  }

  /**
   * 
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param primaryKey
   */
  public ChargesMasterEntityBeanPK ejbFindByPrimaryKey(ChargesMasterEntityBeanPK primaryKey)throws ObjectNotFoundException,EJBException
  {
    boolean chargeMasterRow = false;
    try
    {
      chargeMasterDAO.findByPrimariKey(primaryKey);
      chargeMasterRow = true;
    }catch(ObjectNotFoundException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new ObjectNotFoundException();
    }catch(SQLException e)
    {
       //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);  
       logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);  
    }catch(Exception e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);   
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);   
    }
    if(chargeMasterRow)
    {
      return primaryKey;  
    }else
    {
      throw new ObjectNotFoundException("Could Not Found Bean");
    }
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
      this.pkObj  = (ChargesMasterEntityBeanPK)context.getPrimaryKey();    
      this.chargesMasterDOB= chargeMasterDAO.load(this.pkObj);
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
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the chargesMasterDOB with Id  "+pkObj);
			chargeMasterDAO.remove(this.pkObj);
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ejbRemove()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbRemove()"+"Could not Find Bean "+e);
			throw new javax.ejb.EJBException(e.toString());
		} 
    chargeMasterDAO = null;
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
				chargeMasterDAO.store(this.pkObj,this.chargesMasterDOB);
				
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
    chargeMasterDAO = new  ChargeMasterDAO();
  }

  /**
   * 
   */
  public void unsetEntityContext()
  {
    this.context = null;
    chargeMasterDAO = null;
  }
}
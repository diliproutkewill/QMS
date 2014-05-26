/**
 * @ (#) ChargeBasisMasterEntityBean.java
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
 * File : ChargeBasisMasterEntityBean.java
 * Sub-Module : ChargeBasisMaster
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.chargebasis.ejb.bmp;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.EJBException;
import java.sql.SQLException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import com.foursoft.esupply.common.bean.ESupplyGlobalParameters;

import com.qms.setup.chargebasis.ejb.bmp.ChargeBasisMasterEntityBeanPK;
import com.qms.setup.chargebasis.dao.ChargeBasisMasterDAO;
import com.qms.setup.java.ChargeBasisMasterDOB;

public class ChargeBasisMasterEntityBean implements EntityBean 
{
  private EntityContext context;
  private static final String FILE_NAME   ="ChargeBasisMasterEntityBean.java";
  private ChargeBasisMasterDAO  chargeBasisMasterDAO=null;
  private ChargeBasisMasterEntityBeanPK pkObj =null;
  private ChargeBasisMasterDOB  chargeBasisMasterDOB=null;
  private boolean isValid   = false;  
  private static Logger logger = null;
  
  public ChargeBasisMasterEntityBean()
  {
    logger  = Logger.getLogger(ChargeBasisMasterEntityBean.class);
  }
  
  /**
   * 
   * @param chargeBasisMasterDOB
   */
  public void setChargeBasisMasterDOB(ChargeBasisMasterDOB chargeBasisMasterDOB)
  {
    this.chargeBasisMasterDOB = chargeBasisMasterDOB;
    isValid = true;
  }
  /**
   * 
   * @return 
   */
  public ChargeBasisMasterDOB getChagesBasisMasterDOB()
  {
    return this.chargeBasisMasterDOB;
  }
  
  public ChargeBasisMasterEntityBeanPK ejbCreate(java.util.ArrayList dataList)
  {
    try
    {
      chargeBasisMasterDAO.create(dataList);
      chargeBasisMasterDOB  = (ChargeBasisMasterDOB)dataList.get(0);
      this.pkObj				  = new ChargeBasisMasterEntityBeanPK();
      this.pkObj.chargeBasis  = chargeBasisMasterDOB.getChargeBasis();
    }catch(Exception e)
    {
			//Logger.error(FILE_NAME,"ejbCreate()","Exception  in Creating the Bean ",e);
      logger.error(FILE_NAME+"ejbCreate()"+"Exception  in Creating the Bean "+e);
			throw new javax.ejb.EJBException(e);      
    }finally
    {
      chargeBasisMasterDOB  = null;
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
   * @throws EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return 
   * @param primaryKey
   */
  public ChargeBasisMasterEntityBeanPK ejbFindByPrimaryKey(ChargeBasisMasterEntityBeanPK primaryKey)throws ObjectNotFoundException,EJBException
  {
    boolean chargeBasisMasterRow = false;
    try
    {
      chargeBasisMasterDAO.findByPrimariKey(primaryKey);
      chargeBasisMasterRow = true;
    }catch(ObjectNotFoundException e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);
      throw new ObjectNotFoundException();
    }catch(SQLException e)
    {
       //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);  
       logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean ",e);  
    }catch(Exception e)
    {
      //Logger.error(FILE_NAME,"ejbFindByPrimaryKey()","Could not Find Bean ",e);   
      logger.error(FILE_NAME+"ejbFindByPrimaryKey()"+"Could not Find Bean "+e);   
    }
    if(chargeBasisMasterRow)
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
      this.pkObj  = (ChargeBasisMasterEntityBeanPK)context.getPrimaryKey();    
      this.chargeBasisMasterDOB= chargeBasisMasterDAO.load(this.pkObj);
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
			//Logger.info(FILE_NAME,"ejbRemove()","Removing the chargeBasisMasterDOB with Id  "+pkObj);
			chargeBasisMasterDAO.remove(this.pkObj);
		}
		catch(Exception e)
		{
			
			//Logger.error(FILE_NAME,"ejbRemove()","Could not Find Bean ",e);
      logger.error(FILE_NAME+"ejbRemove()"+"Could not Find Bean "+e);
			throw new javax.ejb.EJBException(e.toString());
		}    
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
				chargeBasisMasterDAO.store(this.pkObj,this.chargeBasisMasterDOB);
				
			}
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME," ejbStore()	","Exception while Storing  the Model ", e);
      logger.error(FILE_NAME+" ejbStore()	"+"Exception while Storing  the Model "+ e);
			throw new javax.ejb.EJBException(e.toString());
		}    
  }

  public void setEntityContext(EntityContext ctx)
  {
    this.context = ctx;
    chargeBasisMasterDAO = new  ChargeBasisMasterDAO();    
  }

  public void unsetEntityContext()
  {
    this.context = null;
  }
}
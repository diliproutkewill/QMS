/**
 * @ (#) ChargeGroupingDAO.java
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
 * File : ChargeGroupingDAO.java
 * Sub-Module : CountryManager
 * Module : QMS
 * @author : I.V.Sekhar Merrinti
 * * @date 25-06-2005
 * Modified by      Date     Reason
 */
package com.qms.setup.chargegroup.dao;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.CreateException;
import javax.sql.DataSource;
import java.sql.Connection;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import javax.naming.InitialContext;
import javax.ejb.ObjectNotFoundException;
import javax.naming.NameNotFoundException;
import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import java.util.ArrayList;

import com.qms.setup.java.ChargeGroupingDOB;

public class ChargeGroupingDAO
{
    static final String FILE_NAME="ChargeGroupingDAO.java";
    DataSource  dataSource=null;
    Connection  connection=null;
    private static final String insertQry  = "INSERT INTO QMS_CHARGE_GROUPSMASTER (CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID,TERMINALID,INVALIDATE,INACTIVATE,ID,ORIGINCOUNTRY,DESTINATIONCOUNTRY) VALUES (?,?,?,?,?,'F','N',CHARGE_GROUPSMASTER_SEQ.NEXTVAL,?,?)";//Modified by Anil.k for Enhancement 231214 on 24Jan2011
    //@@Commented and Modified by Kameswari
    //private static final String selectQry  = "SELECT CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND TERMINALID=? AND INACTIVATE='N' AND INVALIDATE='F'";
//@@ Commented and added by subrahmanyam for internal issue invalidate chargeDescription is flowing into the ChargeGrop view
    // private static final String selectQry  = "SELECT CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID,ID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND TERMINALID=? AND INACTIVATE='N' AND INVALIDATE='F' ORDER BY ID";//@@Commented and Modified by Kameswari for the WPBN issue-130387
    private static final String selectQry  = "SELECT CGM.CHARGEGROUP_ID," +
									    	"       CGM.CHARGE_ID," + 
									    	"       CGM.SHIPMENT_MODE," + 
									    	"       CGM.CHARGEDESCID," + 
									    	"       CGM.ID," + 
									    	"       CGM.ORIGINCOUNTRY," + //Added by Anil.k for Enhancement 231214 on 24Jan2011
									    	"       CGM.DESTINATIONCOUNTRY" + //Added by Anil.k for Enhancement 231214 on 24Jan2011
									    	"  FROM QMS_CHARGE_GROUPSMASTER CGM, QMS_CHARGEDESCMASTER CDM" + 
									    	" WHERE CGM.CHARGEGROUP_ID = ?" + 
									    	"   AND CGM.CHARGE_ID = CDM.CHARGEID" + 
									    	"   AND CGM.CHARGEDESCID = CDM.CHARGEDESCID" + 
									    	"   AND CDM.INVALIDATE = 'F'" + 
									    	"   AND CDM.INACTIVATE = 'N'" + 
									    	"   AND CGM.TERMINALID = ?" + 
									    	"   AND CGM.INACTIVATE = 'N'" + 
									    	"   AND CGM.INVALIDATE = 'F'" + 
									    	" ORDER BY CGM.ID";
//ended by subrahmanyam
    private static final String updateQry  = "UPDATE QMS_CHARGE_GROUPSMASTER SET CHARGE_ID=?,SHIPMENT_MODE=?,CHARGEDESCID=? WHERE CHARGEGROUP_ID=? AND TERMINALID=?";
    //private static final String deleteQry  = "DELETE FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND TERMINALID=?";

   private static final String deleteQry  = "UPDATE QMS_CHARGE_GROUPSMASTER SET INACTIVATE='Y' WHERE CHARGEGROUP_ID=? AND TERMINALID=? AND INACTIVATE = 'N' ";
 //@@ Commented & Added by subrahmanyam for the wpbn id:210918    
   private static final String deleteChkQry	 = " SELECT  COUNT(DISTINCT QM.ID)  FROM QMS_QUOTE_CHARGEGROUPDTL CD, "+
		    " QMS_QUOTE_MASTER   QM,   QMS_CHARGE_GROUPSMASTER  CGM, QMS_QUOTE_RATES  QR "+
		    " WHERE QM.ID = CD.QUOTE_ID   AND QM.ID = QR.QUOTE_ID  AND CD.CHARGEGROUPID = CGM.CHARGEGROUP_ID "+
		    "  AND (CGM.CHARGE_ID, CGM.CHARGEDESCID) IN  (SELECT QR1.CHARGE_ID, QR1.CHARGE_DESCRIPTION "+
		    "  FROM QMS_QUOTE_RATES QR1   WHERE QR1.QUOTE_ID = QM.ID   AND QR1.SELL_BUY_FLAG IN ('B', 'S')) "+
		    "  AND CD.CHARGEGROUPID = ?   AND CGM.TERMINALID =?    AND " +
		    "  ((QM.QUOTING_STATION NOT IN ('ACC', 'NAC') AND QM.ACTIVE_FLAG = 'A') OR    (QM.QUOTING_STATION IN ('ACC', 'NAC') "+
		    "  AND  QM.ACTIVE_FLAG IN ('A', 'I')))";

   // private static final String deleteChkQry  = "SELECT COUNT(*) FROM QMS_QUOTE_CHARGEGROUPDTL CD WHERE CD.CHARGEGROUPID=?";
 //@@ Ended by subrahmanyam for the wpbn id:210918    
   //Added by subrahmanyam for wpbn id: 181538 on 07-sep-09
    private static final String QryForChangeDesc  = " SELECT DISTINCT QU.CHANGEDESC  FROM QMS_QUOTES_UPDATED QU,  QMS_CHARGE_GROUPSMASTER  CGM, "+
                         " QMS_QUOTE_CHARGEGROUPDTL QCG WHERE QU.QUOTE_ID = QCG.QUOTE_ID  AND QCG.CHARGEGROUPID = CGM.CHARGEGROUP_ID "+
                         " AND QCG.CHARGEGROUPID = ? AND QU.SELL_BUY_FLAG IN ('B','S')  AND  QU.CHANGEDESC NOT IN "+
                         " (SELECT CG.CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER CG  WHERE QCG.CHARGEGROUPID = CG.CHARGEGROUP_ID "+
                         "  AND CG.CHARGEGROUP_ID = ? AND CG.INACTIVATE='N') "  ;
    private static final String QryForDeleteQuotes = " DELETE FROM QMS_QUOTES_UPDATED QU WHERE QU.CHANGEDESC= ? AND CONFIRM_FLAG IS NULL" ;
//Ended by subrahmanyam for wpbn id: 181538 on 07-sep-09

    private static Logger logger = null;
  /**
   *
   */
    public ChargeGroupingDAO()
    {
          logger  = Logger.getLogger(ChargeGroupingDAO.class);
          try
          {
            InitialContext ic =new InitialContext();
            dataSource        =(DataSource)ic.lookup("java:comp/env/jdbc/DB");
          }catch(NamingException nex)
          {
            throw new EJBException(nex.toString());
          }catch(Exception e)
          {
            throw new EJBException(e.toString());
          }
    }
  /**
   *
   */
    protected void getConnection()
    {
      try
      {
           connection = dataSource.getConnection();
      }
      catch (SQLException se)
      {
        throw new EJBException(se.toString());
      }
      catch (Exception e)
      {
              throw new EJBException(e.toString());
      }
    }
  /**
   *
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @throws javax.ejb.ObjectNotFoundException
   * @return
   * @param pkObj
   */
    /*public ChargeGroupEntityBeanPK findByPrimariKey(ChargeGroupEntityBeanPK pkObj)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      boolean     marginLimitRow  = false;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(selectQry);
        pstmt.setString(1,pkObj.chargeGroup);
        rs    = pstmt.executeQuery();
        if(rs.next())
        {
          marginLimitRow  = true;
        }
      }catch(SQLException e)
      {
        Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new SQLException();
      }catch(Exception e)
      {
         Logger.error(FILE_NAME,"Exception in findByPrimariKey(ChargeGroupEntityBeanPK param0) method"+e.toString());
         throw new EJBException();
      }finally
      {
        try
        {
          if(rs!=null)
            { rs.close();}
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          Logger.error(FILE_NAME,"SQLException in findByPrimariKey(ChargeGroupEntityBeanPK param0) method"+e.toString());
          throw new SQLException();
        }
      }

      if(marginLimitRow)
      {
        return pkObj;
      }
      else
      {
        throw new ObjectNotFoundException();
      }
    }*/
  /**
   *
   * @throws javax.ejb.EJBException
   * @param dataList
   */
    public void create(ArrayList dataList)throws EJBException
    {
      try
      {
        insertChargeGroupDetails(dataList);

      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in load(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in load(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in load(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }
    }

  /**
   *
   * @throws javax.ejb.EJBException
   * @return
   * @param pkObj
   */
    public ArrayList load(String chargeGroupId,String terminalId,String fromWhere,String accessType)throws EJBException
    {
      ChargeGroupingDOB  chargeGroupingDOB  = null;
      ArrayList dataList  = null;
      try
      {
        dataList = loadChargesGroupDOB(chargeGroupId,terminalId,fromWhere,accessType);
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }
      return dataList;
    }
  /**
   *
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param chargeGroupingDOB
   * @param pkObj
   */
    public void store(ArrayList dataList)throws ObjectNotFoundException,EJBException
    {
      try
      {
        updateChargesGroupDetails(dataList);
      }catch(ObjectNotFoundException e)
      {
            //Logger.error(FILE_NAME,"remove()",e.toString());
            logger.error(FILE_NAME+"remove()"+e.toString());
            throw new ObjectNotFoundException("Bean could not find");
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in store(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in store(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
         //Logger.error(FILE_NAME,"SQLException in store(ChargeGroupEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in store(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }
    }
  /**
   *
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param pkObj
   */
    public int remove(String chargeGroupId,String terminalid)throws SQLException,EJBException
    {
      int deletedRow  = 0;
      try
      {
        deletedRow = deleteChargeGroupDetails(chargeGroupId,terminalid);
      }
      catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(EJBException e)
      {
        //Logger.error(FILE_NAME,"EJBException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"EJBException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in remove(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }
      return deletedRow;
    }

  /**
   *
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param dataList
   */
    public void insertChargeGroupDetails(ArrayList dataList)throws EJBException
    {
      PreparedStatement pstmt = null;
      int dataListSize		=	0	;
      ChargeGroupingDOB  chargeGroupingDOB  =null;
      try
      {
        getConnection();
        pstmt = connection.prepareStatement(insertQry);
        if(dataList!=null && dataList.size()>0)
        {
        	dataListSize	=dataList.size();
          for(int i=0;i<dataListSize;i++)
          {
            chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(i);
            if(chargeGroupingDOB!=null)
            {
              pstmt.setString(1,chargeGroupingDOB.getChargeGroup());
              pstmt.setString(2,(chargeGroupingDOB.getChargeIds()).toString());
              pstmt.setInt(3,chargeGroupingDOB.getShipmentMode());
              pstmt.setString(4,chargeGroupingDOB.getChargeDescId());
              pstmt.setString(5,chargeGroupingDOB.getTerminalId());
              pstmt.setString(6, chargeGroupingDOB.getOriginCountry());//Added by Anil.k for Enhancement 231214 on 24Jan2011
              pstmt.setString(7, chargeGroupingDOB.getDestinationCountry());//Added by Anil.k for Enhancement 231214 on 24Jan2011
              pstmt.addBatch();
            }

          }
          pstmt.executeBatch();
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"Exception in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
        logger.error(FILE_NAME+"Exception in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
        throw new EJBException();
      }finally
      {
        try
        {
          if(pstmt!=null)
            { pstmt.close();}
          if(connection!=null)
            { connection.close();}
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in insertChargeGroupDetails(ArrayList param0) method"+e.toString());
          throw new EJBException();
        }
      }
    }
  /**
   *
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @return
   * @param pkObj
   */
    public ArrayList loadChargesGroupDOB(String chargeGroup,String terminalId,String fromWhere,String accessType)throws EJBException,SQLException
    {
      ChargeGroupingDOB  chargeGroupingDOB  = new ChargeGroupingDOB();
      PreparedStatement pstmt = null;
      ResultSet         rs    = null;
      ArrayList         dataList = new ArrayList();
      String            dataAccessQry = "";
      String            query    =  "";
      try
      {
        getConnection();

        if("Quote".equalsIgnoreCase(fromWhere))
        {
          if("HO_TERMINAL".equalsIgnoreCase(accessType))
            dataAccessQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER";
          else
            dataAccessQry = "SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '"+terminalId+"' UNION "+
                            "SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' UNION "+
                            "SELECT '"+terminalId+"'TERM_ID FROM DUAL UNION "+
                            "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '"+terminalId+"'";
//@@ commented and added by subrahmanya for invalidate chargedesc is flowing into charegroup view
         // query = "SELECT CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND TERMINALID IN ("+dataAccessQry+")  AND INACTIVATE='N' AND INVALIDATE='F'";
          query =	"SELECT CGM.CHARGEGROUP_ID," +
	       	  	  "       CGM.CHARGE_ID," + 
	        	  "       CGM.SHIPMENT_MODE," + 
	        	  "       CGM.CHARGEDESCID " +
	        	  "       CGM.ORIGINCOUNTRY, "+ //Added By Anil 
	        	  "       CGM.DESTINATIONCOUNTRY, "+
	        	  "  FROM QMS_CHARGE_GROUPSMASTER CGM, QMS_CHARGEDESCMASTER CDM" + 
	        	  " WHERE CGM.CHARGEGROUP_ID = ?" + 
	        	  "   AND CGM.CHARGE_ID = CDM.CHARGEID" + 
	        	  "   AND CGM.CHARGEDESCID = CDM.CHARGEDESCID" + 
	        	  "   AND CDM.INVALIDATE = 'F'" + 
	        	  "   AND CDM.INACTIVATE = 'N'" + 
	        	  "   AND CGM.TERMINALID IN ("+dataAccessQry+")" + 
	        	  "   AND CGM.INACTIVATE = 'N'" + 
	        	  "   AND CGM.INVALIDATE = 'F'";

          pstmt = connection.prepareStatement(query);
        }
        else
          pstmt = connection.prepareStatement(selectQry);

        pstmt.setString(1,chargeGroup);

        if(!"Quote".equalsIgnoreCase(fromWhere))
          pstmt.setString(2,terminalId);
        rs    = pstmt.executeQuery();

        while(rs.next())
        {
          chargeGroupingDOB = new ChargeGroupingDOB();
          chargeGroupingDOB.setChargeGroup(rs.getString("CHARGEGROUP_ID"));
          chargeGroupingDOB.setChargeIds(rs.getString("CHARGE_ID"));
          chargeGroupingDOB.setShipmentMode(rs.getInt("SHIPMENT_MODE"));
          chargeGroupingDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
          chargeGroupingDOB.setOriginCountry(rs.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 24Jan2011
          chargeGroupingDOB.setDestinationCountry(rs.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 24Jan2011
          chargeGroupingDOB.setTerminalId(terminalId);
          dataList.add(chargeGroupingDOB);
        }
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally{
          try
          {
            if(rs!=null)
              { rs.close();}
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
            throw new SQLException();
          }
      }
      return dataList;
    }
  /**
   *
   * @throws javax.ejb.EJBException
   * @throws java.sql.SQLException
   * @param chargeGroupingDOB
   * @param pkObj
   */
   public void updateChargesGroupDetails(ArrayList dataList)throws ObjectNotFoundException,EJBException
    {
        String chargeGroupId = "";
        //Added by subrahmanyam for wpbn id: 181538 on 07-sep-09
        PreparedStatement pstmt  = null;
        PreparedStatement pstmt1 = null;
        ResultSet         rs    = null;
     //Ended by subrahmanyam for wpbn id: 181538 on 07-sep-09

      //PreparedStatement  pstmt = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.
      ChargeGroupingDOB chargeGroupingDOB = null;
      try
      {
        //getConnection();
        int dataListSize = dataList.size();
        chargeGroupingDOB = (ChargeGroupingDOB)dataList.get(0);
        chargeGroupId =  chargeGroupingDOB.getChargeGroup(); //Added by subrahmanyam for wpbn id: 181538 on 07-sep-09
        //int deleted = deleteChargeGroupDetails(chargeGroupingDOB.getChargeGroup(),chargeGroupingDOB.getTerminalId());
   //@@Commented & Added by subrahmanyam for the pbn id: 201931 on 05-04-2010 
        //int deleted = deleteChargeGroupDetails(chargeGroupId,chargeGroupingDOB.getTerminalId()); //modified by subrahmanyam for wpbn id: 181538 on 07-sep-09
        int deleted = updateChargesGroupProcess(chargeGroupId,chargeGroupingDOB.getTerminalId()); //modified by subrahmanyam for wpbn id: 181538 on 07-sep-09
        if(deleted>0)
        {
            insertChargeGroupDetails(dataList);

            //Added by subrahmanyam for wpbn id: 181538 on 07-sep-09
            try
             {
                getConnection();
                pstmt = connection.prepareStatement(QryForChangeDesc);
                 pstmt1 = connection.prepareStatement(QryForDeleteQuotes);
                pstmt.setString(1,chargeGroupId);
                pstmt.setString(2,chargeGroupId);
                rs = pstmt.executeQuery();
                while (rs.next())
                {
                  pstmt1.clearParameters();
                  pstmt1.setString(1,rs.getString("CHANGEDESC"));
                  pstmt1.executeUpdate();
                }
              }catch(SQLException e)
              {
                logger.error(FILE_NAME+"SQLException While Deleting the Quotes From the Updated Report which are came to the UpdatedReport using the Deleted Charge From Modified Charge Group:"+e.toString());
                throw new EJBException();
              }catch(Exception e)
              {
               logger.error(FILE_NAME+"SQLException While Deleting the Quotes From the Updated Report which are came to the UpdatedReport using the Deleted Charge From Modified Charge Group:"+e.toString());
               throw new EJBException();
            }finally{
                      
                      ConnectionUtil.closePreparedStatement(pstmt1);
                      ConnectionUtil.closeConnection(connection,pstmt,rs);
                     
                  }
    //Ended by subrahmanyam for wpbn id: 181538 on 07-sep-09
        }else
        {
          throw new ObjectNotFoundException();
        }

      }catch(ObjectNotFoundException e)
      {
          //Logger.error(FILE_NAME,"updateChargesGroupDetails()",e.toString());
          logger.error(FILE_NAME+"updateChargesGroupDetails()"+e.toString());
          throw new ObjectNotFoundException("Bean could not find");
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally{
          try
          {
           /* if(pstmt!=null)
              { pstmt.close();}*///Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in updateChargesGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            throw new EJBException();
          }
      }
    }
  /**
   *
   * @throws java.sql.SQLException
   * @throws javax.ejb.EJBException
   * @param pkObj
   */
    public int deleteChargeGroupDetails(String chargeGroupId,String terminalId)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
    //@@Added by subrahmanyam for the pbn id: 201931 on 05-04-2010      
      PreparedStatement pstmtChk = null;
      int count =0;
      ResultSet         rs    = null;
      int deletedRow  = 0;      
    //@@Ended by subrahmanyam for the pbn id: 201931 on 05-04-2010      
      //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.

      try
      {
        getConnection();
//@@ Commented & Added by subrahmanyam for the pbn id: 201931 on 05-04-2010
        /*
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,chargeGroupId);
        pstmt.setString(2,terminalId);
        deletedRow = pstmt.executeUpdate();

         */
        pstmtChk = connection.prepareStatement(deleteChkQry);
        pstmtChk.setString(1,chargeGroupId);
        pstmtChk.setString(2,terminalId);
        rs =  pstmtChk.executeQuery();
        if(rs.next())
        	count	= rs.getInt(1);
        if(count==0)
        {
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,chargeGroupId);
        pstmt.setString(2,terminalId);
        deletedRow = pstmt.executeUpdate();
        }
        else 
        	deletedRow =	1111;
 //@@Ended by subrahmanyam for the pbn id: 201931 on 05-04-2010        
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally{
          try
          {
            if(rs!=null)
              { rs.close();}//Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(pstmtChk!=null)
              { pstmtChk.close();}
            if(pstmt!=null)
            { pstmt.close();}

            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            throw new SQLException();
          }
      }
      return deletedRow;
    }
    //@@Added by subrahmanyam for the pbn id: 202679 on 05-04-2010     
    public int updateChargesGroupProcess(String chargeGroupId,String terminalId)throws ObjectNotFoundException,EJBException,SQLException
    {
      PreparedStatement pstmt = null;
       int deletedRow  = 0;      
     //ResultSet         rs    = null;//Commented By RajKumari on 27-10-2008 for Connection Leakages.

      try
      {
        getConnection();
        pstmt = connection.prepareStatement(deleteQry);
        pstmt.setString(1,chargeGroupId);
        pstmt.setString(2,terminalId);
        deletedRow = pstmt.executeUpdate();
         
      }catch(SQLException e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }catch(Exception e)
      {
        //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
        throw new EJBException();
      }finally{
          try
          {
            /*if(rs!=null)
              { rs.close();}*///Commented By RajKumari on 27-10-2008 for Connection Leakages.
            if(pstmt!=null)
              { pstmt.close();}
            if(connection!=null)
              { connection.close();}
          }catch(SQLException e)
          {
            //Logger.error(FILE_NAME,"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            logger.error(FILE_NAME+"SQLException in deleteChargeGroupDetails(ChargeGroupEntityBeanPK param0) method"+e.toString());
            throw new SQLException();
          }
      }
      return deletedRow;
    }
  //@@Added by subrahmanyam for the pbn id: 202679 on 05-04-2010 
    //Added by Anil.k for Issue 236357 on 22Feb2011
    /**
    *
    * @throws javax.ejb.EJBException
    * @return
    * @param pkObj
    */
     public ArrayList load(String operation,String chargeGroupId,String terminalId,String fromWhere,String accessType)throws EJBException
     {
       ChargeGroupingDOB  chargeGroupingDOB  = null;
       ArrayList dataList  = null;
       try
       {
         dataList = loadChargesGroupDOB(operation,chargeGroupId,terminalId,fromWhere,accessType);
       }catch(SQLException e)
       {
         //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
         throw new EJBException();
       }catch(EJBException e)
       {
         //Logger.error(FILE_NAME,"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"EJBException in load(MarginLimitMasterBeanPK param0) method"+e.toString());
         throw new EJBException();
       }catch(Exception e)
       {
         //Logger.error(FILE_NAME,"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
         logger.error(FILE_NAME+"SQLException in load(ChargesMasterEntityBeanPK param0) method"+e.toString());
         throw new EJBException();
       }
       return dataList;
     }
     /**
     *
     * @throws java.sql.SQLException
     * @throws javax.ejb.EJBException
     * @return
     * @param pkObj
     */
      public ArrayList loadChargesGroupDOB(String operation,String chargeGroup,String terminalId,String fromWhere,String accessType)throws EJBException,SQLException
      {
        ChargeGroupingDOB  chargeGroupingDOB  = new ChargeGroupingDOB();
        PreparedStatement pstmt = null;
        ResultSet         rs    = null;
        PreparedStatement pstmt2 = null;//Added by Anil.k for issue 236357 on 22Feb2011 
        ResultSet         rs2    = null;
        PreparedStatement pstmt3 = null;
        ResultSet         rs3    = null;
        ArrayList         buyChargeList = new ArrayList();
        ArrayList         sellChargeList = new ArrayList();
        ArrayList         chargesList = new ArrayList();//Ended by Anil.k for issue 236357 on 22Feb2011 
        ArrayList         dataList = new ArrayList();
        ArrayList			chargeId = new ArrayList();//Added by Anil.k for issue 236357 on 22Feb2011 
        ArrayList			chargeDescId = new ArrayList();
        String            dataAccessQry = "";
        String            query    =  "";
        try
        {
          getConnection();

          if("Quote".equalsIgnoreCase(fromWhere))
          {
            if("HO_TERMINAL".equalsIgnoreCase(accessType))
              dataAccessQry = "SELECT TERMINALID FROM FS_FR_TERMINALMASTER";
            else
              dataAccessQry = "SELECT PARENT_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY CHILD_TERMINAL_ID = PRIOR PARENT_TERMINAL_ID START WITH CHILD_TERMINAL_ID = '"+terminalId+"' UNION "+
                              "SELECT TERMINALID TERM_ID FROM FS_FR_TERMINALMASTER WHERE OPER_ADMIN_FLAG = 'H' UNION "+
                              "SELECT '"+terminalId+"'TERM_ID FROM DUAL UNION "+
                              "SELECT CHILD_TERMINAL_ID TERM_ID FROM FS_FR_TERMINAL_REGN CONNECT BY PRIOR CHILD_TERMINAL_ID = PARENT_TERMINAL_ID START WITH PARENT_TERMINAL_ID = '"+terminalId+"'";
  //@@ commented and added by subrahmanya for invalidate chargedesc is flowing into charegroup view
           // query = "SELECT CHARGEGROUP_ID,CHARGE_ID,SHIPMENT_MODE,CHARGEDESCID FROM QMS_CHARGE_GROUPSMASTER WHERE CHARGEGROUP_ID=? AND TERMINALID IN ("+dataAccessQry+")  AND INACTIVATE='N' AND INVALIDATE='F'";
            query =	"SELECT CGM.CHARGEGROUP_ID," +
  	       	  	  "       CGM.CHARGE_ID," + 
  	        	  "       CGM.SHIPMENT_MODE," + 
  	        	  "       CGM.CHARGEDESCID," + 
  	        	  "		  CGM.ORIGINCOUNTRY,"+//Added by Anil.k
  	        	  "		  CGM.DESTINATIONCOUNTRY"+
  	        	  "  FROM QMS_CHARGE_GROUPSMASTER CGM, QMS_CHARGEDESCMASTER CDM" + 
  	        	  " WHERE CGM.CHARGEGROUP_ID = ?" + 
  	        	  "   AND CGM.CHARGE_ID = CDM.CHARGEID" + 
  	        	  "   AND CGM.CHARGEDESCID = CDM.CHARGEDESCID" + 
  	        	  "   AND CDM.INVALIDATE = 'F'" + 
  	        	  "   AND CDM.INACTIVATE = 'N'" + 
  	        	  "   AND CGM.TERMINALID IN ("+dataAccessQry+")" + 
  	        	  "   AND CGM.INACTIVATE = 'N'" + 
  	        	  "   AND CGM.INVALIDATE = 'F'";

            pstmt = connection.prepareStatement(query);
          }
          else
            pstmt = connection.prepareStatement(selectQry);

          pstmt.setString(1,chargeGroup);

          if(!"Quote".equalsIgnoreCase(fromWhere))
            pstmt.setString(2,terminalId);
          rs    = pstmt.executeQuery();

          while(rs.next())
          {
            chargeGroupingDOB = new ChargeGroupingDOB();
            chargeGroupingDOB.setChargeGroup(rs.getString("CHARGEGROUP_ID"));
            chargeGroupingDOB.setChargeIds(rs.getString("CHARGE_ID"));
            chargeId.add(rs.getString("CHARGE_ID"));//Added by Anil.k for issue 236357 on 22Feb2011 
            chargeGroupingDOB.setShipmentMode(rs.getInt("SHIPMENT_MODE"));
            chargeGroupingDOB.setChargeDescId(rs.getString("CHARGEDESCID"));
            chargeDescId.add(rs.getString("CHARGEDESCID"));
            chargeGroupingDOB.setOriginCountry(rs.getString("ORIGINCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 24Jan2011
            chargeGroupingDOB.setDestinationCountry(rs.getString("DESTINATIONCOUNTRY"));//Added by Anil.k for Enhancement 231214 on 24Jan2011
            chargeGroupingDOB.setTerminalId(terminalId);
            dataList.add(chargeGroupingDOB);
          }
          //Added by Anil.k for issue 236357 on 22Feb2011 
          String buyChargeValues = " SELECT B.BUYSELLCHARGEID,B.CHARGE_ID,B.CHARGEBASIS,B.CHARGEDESCID,"+
          						 " B.CURRENCY,D.CHARGERATE,B.DENSITY_CODE,D.CHARGESLAB,C.COST_INCURREDAT , D.CHARGERATE_INDICATOR  FROM "+
          						 " QMS_BUYSELLCHARGESMASTER B,QMS_BUYCHARGESDTL D,QMS_CHARGESMASTER C WHERE B.CHARGE_ID = ? "+
          						 " AND B.BUYSELLCHARGEID = D.BUYSELLCHAEGEID AND B.DEL_FLAG = 'N' AND C.CHARGE_ID = B.CHARGE_ID AND B.CHARGEDESCID = ?";
                        //AND B.DEL_FLAG = 'N' CONDITION ADDED BY ADI FOR GETTING THE CHARGES WHCICH ARE MODIFIED 
          String buysellChargeValues = " SELECT QBS.BUYSELLCHARGEID,QBS.CHARGE_ID,QBS.CHARGEBASIS, " +
          						   " QBS.CHARGEDESCID,QBS.CURRENCY,D.CHARGERATE,QBS.DENSITY_CODE, " +
          						   " D.CHARGESLAB,C.COST_INCURREDAT,SD.CHARGERATE+SD.MARGINVALUE SELLRATE "+
          						   " FROM QMS_SELLCHARGESMASTER QS,QMS_BUYSELLCHARGESMASTER QBS, "+
          						   " QMS_BUYCHARGESDTL D,QMS_SELLCHARGESDTL SD,QMS_CHARGESMASTER  C "+
          						   " WHERE QBS.CHARGE_ID = ? AND QS.BUYCHARGEID=QBS.BUYSELLCHARGEID "+
          						   " AND QBS.BUYSELLCHARGEID = D.BUYSELLCHAEGEID "+
          						   " AND QS.CHARGE_ID = QBS.CHARGE_ID AND QBS.CHARGEDESCID = ? "+
          						   " AND SD.SELLCHARGEID = QS.SELLCHARGEID AND C.CHARGE_ID = QS.CHARGE_ID "+
          						   " AND QS.CHARGE_ID = ? AND QS.CHARGEDESCID = ? "+
          						   " AND D.CHARGESLAB = SD.CHARGESLAB ";
          String sellChargeValues= " SELECT S.SELLCHARGEID,S.CHARGE_ID,S.CHARGEBASIS,S.CHARGEDESCID, "+
          						 " S.CURRENCY, SD.CHARGESLAB,SD.CHARGERATE+SD.MARGINVALUE SELLRATE,C.COST_INCURREDAT "+
          						 " FROM QMS_SELLCHARGESMASTER S,QMS_SELLCHARGESDTL SD,QMS_CHARGESMASTER C "+          
          						 " WHERE S.CHARGE_ID = ?  AND SD.SELLCHARGEID = S.SELLCHARGEID AND C.CHARGE_ID = S.CHARGE_ID "+
          						 " AND S.CHARGEDESCID = ?";
             
          for(int i=0;i<chargeId.size();i++)
          {
        	  pstmt3 = connection.prepareStatement(sellChargeValues);
            	pstmt3.setString(1,(String)chargeId.get(i));
            	pstmt3.setString(2,(String)chargeDescId.get(i));
            	rs3    = pstmt3.executeQuery();
            	/*while(rs3.next())
            	{
            		chargeGroupingDOB = new ChargeGroupingDOB();
            		chargeGroupingDOB.setBuySellChargeId(rs3.getString("SELLCHARGEID"));
            		chargeGroupingDOB.setChargeIds(rs3.getString("CHARGE_ID"));
            		chargeGroupingDOB.setChargeBasis(rs3.getString("CHARGEBASIS"));
            		chargeGroupingDOB.setChargeDescId(rs3.getString("CHARGEDESCID"));
            		chargeGroupingDOB.setCurrency(rs3.getString("CURRENCY"));
            		chargeGroupingDOB.setBuyChargeRate(rs3.getDouble("SELLRATE"));        		
            		chargeGroupingDOB.setChargeSlab(rs3.getString("CHARGESLAB"));
            		chargeGroupingDOB.setCostIncurredIn(rs3.getString("COST_INCURREDAT"));
            		sellChargeList.add(chargeGroupingDOB);
            	}*/
            if(rs3.next()){
          	pstmt2 = connection.prepareStatement(buysellChargeValues);
          	pstmt2.setString(1,(String)chargeId.get(i));
          	pstmt2.setString(2,(String)chargeDescId.get(i));
          	pstmt2.setString(3,(String)chargeId.get(i));
          	pstmt2.setString(4,(String)chargeDescId.get(i));
          	rs2    = pstmt2.executeQuery();
          	while(rs2.next())
          	{
          		chargeGroupingDOB = new ChargeGroupingDOB();
          		chargeGroupingDOB.setBuySellChargeId(rs2.getString("BUYSELLCHARGEID"));
          		chargeGroupingDOB.setChargeIds(rs2.getString("CHARGE_ID"));
          		chargeGroupingDOB.setChargeBasis(rs2.getString("CHARGEBASIS"));
          		chargeGroupingDOB.setChargeDescId(rs2.getString("CHARGEDESCID"));
          		chargeGroupingDOB.setCurrency(rs2.getString("CURRENCY"));
          		chargeGroupingDOB.setBuyChargeRate(rs2.getDouble("CHARGERATE"));
          		chargeGroupingDOB.setDensityCode(rs2.getString("DENSITY_CODE"));
          		chargeGroupingDOB.setChargeSlab(rs2.getString("CHARGESLAB"));
          		chargeGroupingDOB.setCostIncurredIn(rs2.getString("COST_INCURREDAT"));
          		chargeGroupingDOB.setSellChargeRate(rs2.getDouble("SELLRATE"));
          		buyChargeList.add(chargeGroupingDOB);
          	}
            }
            else{
            	pstmt2 = connection.prepareStatement(buyChargeValues);
              	pstmt2.setString(1,(String)chargeId.get(i));
              	pstmt2.setString(2,(String)chargeDescId.get(i));              	
              	rs2    = pstmt2.executeQuery();
              	while(rs2.next())
              	{
              		chargeGroupingDOB = new ChargeGroupingDOB();
              		chargeGroupingDOB.setBuySellChargeId(rs2.getString("BUYSELLCHARGEID"));
              		chargeGroupingDOB.setChargeIds(rs2.getString("CHARGE_ID"));
              		if((rs2.getString("CHARGERATE_INDICATOR")!= null )&& ("F".equalsIgnoreCase(rs2.getString("CHARGERATE_INDICATOR"))))//If condition added by govind fort he issue buycharge view
              		chargeGroupingDOB.setChargeBasis("SHIPMENT");
              		else if("MIN".equalsIgnoreCase(rs2.getString("CHARGESLAB")))
              		chargeGroupingDOB.setChargeBasis("SHIPMENT");
              		else
              		chargeGroupingDOB.setChargeBasis(rs2.getString("CHARGEBASIS"));	
              		chargeGroupingDOB.setChargeDescId(rs2.getString("CHARGEDESCID"));
              		chargeGroupingDOB.setCurrency(rs2.getString("CURRENCY"));
              		chargeGroupingDOB.setBuyChargeRate(rs2.getDouble("CHARGERATE"));
              		chargeGroupingDOB.setDensityCode(rs2.getString("DENSITY_CODE"));
              		
              		chargeGroupingDOB.setChargeSlab(rs2.getString("CHARGESLAB"));
              		chargeGroupingDOB.setCostIncurredIn(rs2.getString("COST_INCURREDAT"));
              		buyChargeList.add(chargeGroupingDOB);
              	}            	
            }
          	
          }
          chargesList.add(dataList);
          chargesList.add(buyChargeList);
          chargesList.add(sellChargeList);
          //Ended by Anil.k for issue 236357 on 22Feb2011 
        }catch(SQLException e)
        {
          //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
          throw new EJBException();
        }catch(Exception e)
        {
          //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
          logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
          throw new EJBException();
        }finally{
            try
            {
              if(rs!=null)
                { rs.close();}
              if(pstmt!=null)
                { pstmt.close();}
              if(connection!=null)
                { connection.close();}
              ConnectionUtil.closePreparedStatement(pstmt2,rs2);//Added by Anil.k for issue 236357 on 22Feb2011 
              ConnectionUtil.closePreparedStatement(pstmt3,rs3);//Added by Anil.k for issue 236357 on 22Feb2011 
            }catch(SQLException e)
            {
              //Logger.error(FILE_NAME,"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
              logger.error(FILE_NAME+"SQLException in loadChargesMasterDOB(MarginLimitMasterBeanPK param0) method"+e.toString());
              throw new SQLException();
            }
        }
        //return dataList;
        return chargesList;
      }
     //Ended by Anil.k for Issue 236357 on 22Feb2011
}
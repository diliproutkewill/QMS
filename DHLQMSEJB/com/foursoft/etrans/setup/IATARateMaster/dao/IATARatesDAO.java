/**
	 * @file : IATARatesDAO.java
	 * @author : Srivegi
	 * @date : 23-03-1005
	 * @version : 1.8 
	 */

package com.foursoft.etrans.setup.IATARateMaster.dao;

import com.foursoft.etrans.setup.IATARateMaster.java.IATAChargeDtlsModel;
import com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import javax.ejb.EJBException;

import java.sql.Connection; 
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.foursoft.esupply.common.util.ConnectionUtil;
//import com.foursoft.esupply.common.util.Logger;
import org.apache.log4j.Logger;
import javax.ejb.ObjectNotFoundException;

import java.util.ArrayList;


public class IATARatesDAO
{
 
    static final String FILE_NAME  =   "IATARatesDAO.java";
    private static Logger logger = null;
    
    private Connection connection	= null;
    private	DataSource datasource   = null;
       
    public IATARatesDAO()
    {
    logger  = Logger.getLogger(IATARatesDAO.class);
		try 
		{
            InitialContext ic 	= new InitialContext();
            datasource  		= (DataSource) ic.lookup("java:comp/env/jdbc/DB"); 
        } 
		catch (NamingException ne) 
    		{
            throw new EJBException(ne.toString());
        }
		catch (Exception e) 
		{
            throw new EJBException(e.toString());
        }
    }
	
  
		
 	public boolean create(IATADtlModel  rateDtlModel)
	{
		PreparedStatement pStmt  =  null;
		Statement st  =  null;
        ResultSet rs  = null;
		int id        = 0;
	    try
	    {
          // Logger.info(FILE_NAME,"...............in dao create()........");
	       getConnection();
           String idQuery    =  "SELECT MAX(IATAMASTERID) FROM FS_FR_IATARATEMASTERHDR";
		   st = connection.createStatement();
		   rs = st.executeQuery(idQuery);

		   if(rs.next())
			{ id = rs.getInt(1); }
           //Logger.info(FILE_NAME,"...............id=========== "+id);
		   pStmt 	=  connection.prepareStatement("INSERT INTO FS_FR_IATARATEMASTERHDR (IATAMASTERID,ORIGINLOCATION,DESTLOCATION, SERVICELEVEL,RATE_TYPE,CURRENCYID,VALIDFROM,VALIDUPTO,WEIGHT_CLASS,UOM) VALUES (? ,?, ?, ?, ?, ?, ?, ?, ?, ?)");

		   pStmt.setInt(1,id+1);
		   pStmt.setString(2,rateDtlModel.getOriginLocation());
		   pStmt.setString(3,rateDtlModel.getDestLocation());
		   pStmt.setString(4,rateDtlModel.getServiceLevel());
		   pStmt.setString(5,rateDtlModel.getRateType());
		   pStmt.setString(6,rateDtlModel.getCurrencyId());
		   pStmt.setTimestamp(7,rateDtlModel.getValidFromDate());
		   pStmt.setTimestamp(8,rateDtlModel.getValidUptoDate());
		   pStmt.setString(9,rateDtlModel.getWeightClass());
		   pStmt.setString(10,rateDtlModel.getUOM());
           
           int n=pStmt.executeUpdate();
          // Logger.info(FILE_NAME,"...............n============.."+n);
		   if(n<=0)
			   return false;
          
		   IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
       //Added By RajKumari on 23-10-2008 for Connection Leakages.
       if(pStmt!=null)
       {
         pStmt.close();
	       pStmt = null;			   
       }
		   pStmt 	=  connection.prepareStatement("INSERT INTO FS_FR_IATARATEMASTERDTL (IATAMASTERID,SLNO,CHARGE_SLAB,CHARGE_RATE) VALUES (? ,?, ?, ?)");
           
		   rateDtls = rateDtlModel.getRateDtls();
		   pStmt.setInt(1,id+1);
		   pStmt.setInt(2,1);
		   pStmt.setString(3,rateDtls.getSlabValue());
		   pStmt.setString(4,rateDtls.getRateValue());
       
        n=pStmt.executeUpdate();
         //  Logger.info(FILE_NAME,"...............n============.."+n);

		   if(n>0)
			   return true;
		   else 
			   return false;


		   }
		catch (Exception e) 
		{
            throw new EJBException(e.toString());
        }	   
	    finally
	    {
	      ConnectionUtil.closeConnection(connection,st,rs); 
        ConnectionUtil.closeConnection(null,pStmt,null); 
	    }
	  
	}
	public boolean create(IATADtlModel  rateDtlModel,ArrayList chargeValues)
	{
		PreparedStatement pStmt  =  null;
		Statement st  =  null;
        ResultSet rs  = null;
		int id        = 0;
	    try
	    {
           //Logger.info(FILE_NAME,"...............in dao create()........");
	       getConnection();
           String idQuery    =  "SELECT MAX(IATAMASTERID) FROM FS_FR_IATARATEMASTERHDR";
		   st = connection.createStatement();
		   rs = st.executeQuery(idQuery);

		   if(rs.next())
			{ id = rs.getInt(1); }
          // Logger.info(FILE_NAME,"...............id=========== "+id);
		   pStmt 	=  connection.prepareStatement("INSERT INTO FS_FR_IATARATEMASTERHDR (IATAMASTERID,ORIGINLOCATION,DESTLOCATION, SERVICELEVEL,RATE_TYPE,CURRENCYID,VALIDFROM,VALIDUPTO,WEIGHT_CLASS,UOM) VALUES (? ,?, ?, ?, ?, ?, ?, ?, ?, ?)");

		   pStmt.setInt(1,id+1);
		   pStmt.setString(2,rateDtlModel.getOriginLocation());
		   pStmt.setString(3,rateDtlModel.getDestLocation());
		   pStmt.setString(4,rateDtlModel.getServiceLevel());
		   pStmt.setString(5,rateDtlModel.getRateType());
		   pStmt.setString(6,rateDtlModel.getCurrencyId());
		   pStmt.setTimestamp(7,rateDtlModel.getValidFromDate());
		   pStmt.setTimestamp(8,rateDtlModel.getValidUptoDate());
		   pStmt.setString(9,rateDtlModel.getWeightClass());
		   pStmt.setString(10,rateDtlModel.getUOM());
           
           int n=pStmt.executeUpdate();
          // Logger.info(FILE_NAME,"...............n============.."+n);
          
           if(n<=0)
			   return false;

		   IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
       //Added By RajKumari on 24-10-2008 for Connection Leakages.
       if(pStmt!=null)
       {
         pStmt.close();
         pStmt = null;
       }
        pStmt 	=  connection.prepareStatement("INSERT INTO FS_FR_IATARATEMASTERDTL (IATAMASTERID,SLNO,CHARGE_SLAB,CHARGE_RATE) VALUES (? ,?, ?, ?)");
		int chargeValSize	= chargeValues.size();   
        for(int i=0;i<chargeValSize;i++)
			{
//Commented By RajKumari on 24-10-2008 for Connection Leakages.
	      // pStmt = null;			   
		  // pStmt 	=  connection.prepareStatement("INSERT INTO FS_FR_IATARATEMASTERDTL (IATAMASTERID,SLNO,CHARGE_SLAB,CHARGE_RATE) VALUES (? ,?, ?, ?)");

           rateDtlModel =(IATADtlModel)chargeValues.get(i);
		   rateDtls = rateDtlModel.getRateDtls();
		   pStmt.setInt(1,id+1);
		   pStmt.setInt(2,i+1);
                     // Logger.info(FILE_NAME,"...........charge insert============.."+rateDtls.getSlabValue());
		   pStmt.setString(3,rateDtls.getSlabValue());
		   pStmt.setString(4,rateDtls.getRateValue());
           n=pStmt.executeUpdate();

           //Logger.info(FILE_NAME,"...............n============.."+n);
			}
		   if(n>0)
			   return true;
		   else 
			   return false;

		   }
		catch (Exception e) 
		{
            throw new EJBException(e.toString());
        }	   
	    finally
	    {
	      ConnectionUtil.closeConnection(connection,st,rs); 
        ConnectionUtil.closeConnection(null,pStmt,null);
      }
	  
	}
	public boolean store(IATADtlModel  rateDtlModel,ArrayList chargeValues)
	{
		PreparedStatement pStmt  =  null;
		//Statement st  =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
      //  ResultSet rs  = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
		int id        = 0;
	    try
	    {
          // Logger.info(FILE_NAME,"...............in dao create()........");
	       getConnection();
           
		   pStmt 	=  connection.prepareStatement("UPDATE FS_FR_IATARATEMASTERHDR SET CURRENCYID=? , VALIDFROM=? , VALIDUPTO=? , WEIGHT_CLASS=? , UOM = ? WHERE IATAMASTERID=?");

		   pStmt.setString(1,rateDtlModel.getCurrencyId());
           pStmt.setTimestamp(2,rateDtlModel.getValidFromDate());
		   pStmt.setTimestamp(3,rateDtlModel.getValidUptoDate());
		   pStmt.setString(4,rateDtlModel.getWeightClass());
		   pStmt.setString(5,rateDtlModel.getUOM());
		   pStmt.setInt(6,rateDtlModel.getIATAMasterId()); 

           int n=pStmt.executeUpdate();
          // Logger.info(FILE_NAME,"...............n============.."+n);
          
            if(n>0)
			{
			IATAChargeDtlsModel rateDtls = new IATAChargeDtlsModel();
			IATADtlModel chargeDtlModel = new IATADtlModel();
      //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!=null)
      {
        pStmt.close();
        pStmt = null;
      }
      pStmt 	=  connection.prepareStatement("UPDATE FS_FR_IATARATEMASTERDTL SET SLNO=? , CHARGE_SLAB=? , CHARGE_RATE =? WHERE IATAMASTERID=? AND SLNO =? ");
		  int chargeValSize		= chargeValues.size();
      for(int i=0;i<chargeValSize;i++)
			{
      //Commented By RajKumari on 24-10-2008 for Connection Leakages.
			  //  pStmt = null;
              //    pStmt 	=  connection.prepareStatement("UPDATE FS_FR_IATARATEMASTERDTL SET SLNO=? , CHARGE_SLAB=? , CHARGE_RATE =? WHERE IATAMASTERID=? AND SLNO =? ");

		   chargeDtlModel =(IATADtlModel)chargeValues.get(i);
		   rateDtls = chargeDtlModel.getRateDtls();
		   pStmt.setInt(1,i+1);
                     // Logger.info(FILE_NAME,"...........charge insert============.."+rateDtls.getSlabValue());
		   pStmt.setString(2,rateDtls.getSlabValue());
		   pStmt.setString(3,rateDtls.getRateValue());
		   pStmt.setInt(4,rateDtlModel.getIATAMasterId());
		   		   pStmt.setInt(5,i+1);
           n=pStmt.executeUpdate();

          // Logger.info(FILE_NAME,"...............n============.."+n);
			}
			}
		   if(n>0)
			   return true;
		   else 
			   return false;


		   }
		catch (Exception e) 
		{
            throw new EJBException(e.toString());
        }	   
	    finally
	    {
	      ConnectionUtil.closeConnection(connection,null,null); //Modified By RajKumari on 24-10-2008 for Connection Leakages.
         ConnectionUtil.closeConnection(null,pStmt,null); 
	    }
	  
	}
	public ArrayList load(int masterId)
  {
		PreparedStatement pStmt  =  null;
		//Statement st  =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
        ResultSet rs  = null;
		int id        = 0;
		ArrayList result = new ArrayList();
		IATADtlModel iataDtls = new IATADtlModel();
		IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
	    try
	    {
          // Logger.info(FILE_NAME,"...............in dao load()........");
	       getConnection();
		  
			 
              pStmt 	=  connection.prepareStatement("SELECT SLNO ,CHARGE_RATE,CHARGE_SLAB  FROM FS_FR_IATARATEMASTERDTL WHERE IATAMASTERID =? ORDER BY SLNO");
              pStmt.setInt(1,masterId);
		      rs=pStmt.executeQuery();
			  while(rs.next())
				{
				   iataDtls = new IATADtlModel();
				   rateDtls      = new IATAChargeDtlsModel();

                   rateDtls.setSlNo(rs.getInt(1));
				   rateDtls.setSlabValue(rs.getString(3));
				   rateDtls.setRateValue(rs.getString(2));
				   iataDtls.setRateDtls(rateDtls);
				  // Logger.info(FILE_NAME,"..............in dao....getOriginLocation....===="+rateDtls.getSlNo());
				  // Logger.info(FILE_NAME,"..............in dao....getOriginLocation....===="+rateDtls.getSlabValue());
				   result.add(iataDtls);

				}
			  if(result==null)
				  return null;

		   }
		
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in  load method	iatadao :	" +	e.toString());
      logger.error(FILE_NAME+"Exception in  load method	iatadao :	" +	e.toString());
			e.printStackTrace();
		}
		finally
	    {
	      
        ConnectionUtil.closeConnection(connection,null,rs); //Modified By RajKumari on 24-10-2008 for Connection Leakages.
        ConnectionUtil.closeConnection(null,pStmt,null); 
	    }
    return result;
	}
   public IATADtlModel load(String originTerminalId,String destinationTerminalId,String serviceLevelId,String operation,int masterId)
	{
		PreparedStatement pStmt  =  null;
		//Statement st  =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
        ResultSet rs  = null;
		int id        = 0;
		IATADtlModel iataDtls = new IATADtlModel();
		IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();
	    try
	    {
          // Logger.info(FILE_NAME,"...............in dao load()........");
	       getConnection();
		   if(!operation.equalsIgnoreCase("Modify") && !operation.equalsIgnoreCase("View") && !operation.equalsIgnoreCase("Delete"))
			{
           pStmt 	=  connection.prepareStatement("SELECT IATAMASTERID,ORIGINLOCATION,DESTLOCATION, SERVICELEVEL,RATE_TYPE,CURRENCYID,VALIDFROM,VALIDUPTO,WEIGHT_CLASS,UOM FROM FS_FR_IATARATEMASTERHDR WHERE ORIGINLOCATION=? AND DESTLOCATION=? AND SERVICELEVEL=? ");
		   
		   pStmt.setString(1,originTerminalId);
		   pStmt.setString(2,destinationTerminalId);
		   pStmt.setString(3,serviceLevelId);

		   rs=pStmt.executeQuery();
		   if(rs.next())
			{
			   iataDtls.setOriginLocation(rs.getString(2));
			   iataDtls.setDestLocation(rs.getString(3));
			   iataDtls.setServiceLevel(rs.getString(4));
			   iataDtls.setRateType(rs.getString(5));
			   iataDtls.setCurrencyId(rs.getString(6));
			   iataDtls.setValidFromDate(rs.getTimestamp(7));
			   iataDtls.setValidUptoDate(rs.getTimestamp(8));
			   iataDtls.setWeightClass(rs.getString(9));
			   iataDtls.setUOM(rs.getString(10));
			   iataDtls.setIATAMasterId(rs.getInt(1));
            }
			//Logger.info(FILE_NAME,"...............in dao load()..operation......"+operation);
			//Logger.info(FILE_NAME,"...............in dao load()..masterId......"+masterId);
      //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!=null)
      {
        pStmt.close();
        pStmt= null;
      }
			}
			else
		   {
			 
              pStmt 	=  connection.prepareStatement("SELECT SLNO ,CHARGE_RATE,CHARGE_SLAB  FROM FS_FR_IATARATEMASTERDTL WHERE IATAMASTERID =? ORDER BY SLNO ");
              pStmt.setInt(1,masterId);
		      rs=pStmt.executeQuery();
			  if(rs.next())
				{
                   rateDtls.setSlNo(rs.getInt(1));
				   rateDtls.setSlabValue(rs.getString(3));
				   rateDtls.setRateValue(rs.getString(2));
				   iataDtls.setRateDtls(rateDtls);
				  // Logger.info(FILE_NAME,"..............in dao....getOriginLocation....===="+rateDtls.getSlNo());
				  // Logger.info(FILE_NAME,"..............in dao....getOriginLocation....===="+rateDtls.getSlabValue());

				}
			  

		   }
		   if(iataDtls==null)
			   return null;
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in  load method	iatadao :	" +	e.toString());
      logger.error(FILE_NAME+"Exception in  load method	iatadao :	" +	e.toString());
			e.printStackTrace();
		}
		finally
	    {
        ConnectionUtil.closeConnection(connection,null,rs); //Modified By RajKumari on 24-10-2008 for Connection Leakages.
        ConnectionUtil.closeConnection(null,pStmt,null);
	    }
    return iataDtls;
	}
   public String store(IATADtlModel  rateDtlModel)		
    {
        PreparedStatement pStmt  =  null;
		//Statement st  =  null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
       // ResultSet rs  = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
		int id        = 0;
		IATAChargeDtlsModel   rateDtls      = new IATAChargeDtlsModel();

        try
        {
           getConnection();
           pStmt 	=  connection.prepareStatement("UPDATE FS_FR_IATARATEMASTERHDR SET CURRENCYID=? , VALIDFROM=? , VALIDUPTO=? , WEIGHT_CLASS=? , UOM = ? WHERE IATAMASTERID=?");
		   
		   pStmt.setString(1,rateDtlModel.getCurrencyId());
           pStmt.setTimestamp(2,rateDtlModel.getValidFromDate());
		   pStmt.setTimestamp(3,rateDtlModel.getValidUptoDate());
		   pStmt.setString(4,rateDtlModel.getWeightClass());
		   pStmt.setString(5,rateDtlModel.getUOM());
		   pStmt.setInt(6,rateDtlModel.getIATAMasterId()); 

           int n=pStmt.executeUpdate();
          // Logger.info(FILE_NAME,"...............n============.."+n);
		   
           if(n>0)
			{
      //Added By RajKumari on 24-10-2008 for Connection Leakages.
      if(pStmt!= null)
      {
        pStmt.close();
        pStmt = null;
      }
                  pStmt 	=  connection.prepareStatement("UPDATE FS_FR_IATARATEMASTERDTL SET SLNO=? , CHARGE_SLAB=? , CHARGE_RATE =? WHERE IATAMASTERID=?");

                  rateDtls=rateDtlModel.getRateDtls();

				  pStmt.setInt(1,rateDtls.getSlNo());
				  pStmt.setString(2,rateDtls.getSlabValue());
				  pStmt.setString(3,rateDtls.getRateValue());
				  pStmt.setInt(4,rateDtlModel.getIATAMasterId());

				  n=pStmt.executeUpdate();
                //  Logger.info(FILE_NAME,"...............n============.."+n);
                  if (n>0)
                  {
					  return "Modified";
                  }
			}
			   
		   else
			   return null;
        }
        catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in  load method	iatadao :	" +	e.toString());
      logger.error(FILE_NAME+"Exception in  load method	iatadao :	" +	e.toString());
			e.printStackTrace();
		}
		finally
	    {
        ConnectionUtil.closeConnection(connection,null,null); //Modified By RajKumari on 24-10-2008 for Connection Leakages.
        ConnectionUtil.closeConnection(null,pStmt,null);
	    }
     return "Modified";
	}
   public String remove(int IATAMasterId)
	{
        PreparedStatement pStmt  =  null;
		//ResultSet rs  = null;//Commented By RajKumari on 24-10-2008 for Connection Leakages.
		int n        = 0;
		String result = "";
		
        try
        {
           getConnection();
           pStmt 	=  connection.prepareStatement("DELETE  FROM FS_FR_IATARATEMASTERDTL WHERE IATAMASTERID =?");
		   pStmt.setInt(1,IATAMasterId);
		   n=pStmt.executeUpdate();
		   if (n>0)
		   {
       //Added By RajKumari on 24-10-2008 for Connection Leakages.
       if(pStmt!=null)
       {
         pStmt.close();
         pStmt = null;
       }
                pStmt 	=  connection.prepareStatement("DELETE  FROM FS_FR_IATARATEMASTERHDR WHERE IATAMASTERID =?");
		        pStmt.setInt(1,IATAMasterId);
		        n=pStmt.executeUpdate();
				if(n>0)
					 result= "Deleted";
				else
                     result = "Error";
		   }
		}
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Exception in  load method	iatadao :	" +	e.toString());
      logger.error(FILE_NAME+"Exception in  load method	iatadao :	" +	e.toString());
			e.printStackTrace();
		}
		finally
	    {
	      ConnectionUtil.closeConnection(connection,pStmt); 
	    }
		return result;
	}
   protected void getConnection()
	{         
		try 
		{
			   connection = datasource.getConnection();
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
  
 
 
 }	